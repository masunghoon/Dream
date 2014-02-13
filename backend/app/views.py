# from social.apps.flask_app import routes

from flask import render_template, redirect, flash, url_for, request, g, session, jsonify, abort
from flask.ext.login import login_required, logout_user, login_user, current_user
from flask.ext.babel import gettext
from flask.ext.sqlalchemy import get_debug_queries
from flask.ext.restful import Resource, reqparse, fields, marshal
from werkzeug import check_password_hash, generate_password_hash
from flask.ext.httpauth import HTTPBasicAuth
from rauth.service import OAuth2Service

auth = HTTPBasicAuth()

from guess_language import guessLanguage

from app import app, db,  babel, api
from forms import LoginForm, OidLoginForm, EditForm, PostForm, SearchForm, RegisterForm
from models import User, Post, Bucket, Plan, ROLE_USER, ROLE_ADMIN
from emails import follower_notification, send_awaiting_confirm_mail
from translate import microsoft_translate

from config import POSTS_PER_PAGE, MAX_SEARCH_RESULTS, LANGUAGES, DATABASE_QUERY_TIMEOUT, FB_CLIENT_ID, FB_CLIENT_SECRET

from datetime import datetime

now = datetime.utcnow()


graph_url = 'https://graph.facebook.com/'
facebook = OAuth2Service(name='facebook',
                         authorize_url='https://www.facebook.com/dialog/oauth',
                         access_token_url=graph_url+'oauth/access_token',
                         client_id=FB_CLIENT_ID,
                         client_secret=FB_CLIENT_SECRET,
                         base_url=graph_url);

@babel.localeselector
def get_locale():
    return request.accept_languages.best_match(LANGUAGES.keys())


@app.route('/', methods=['GET', 'POST'])
@app.route('/index', methods=['GET', 'POST'])
@app.route('/index/<int:page>', methods=['GET', 'POST'])
@auth.login_required
def index(page=1):
    form = PostForm()
    if form.validate_on_submit():
        language = guessLanguage(form.post.data)
        if language == 'UNKNOWN' or len(language) > 5:
            language = ''
        post = Post(body=form.post.data, timestamp=datetime.utcnow(), author=g.user, language=language)
        db.session.add(post)
        db.session.commit()
        flash('Your post is now live!')
        return redirect(url_for('index'))
    posts = g.user.followed_posts().paginate(page, POSTS_PER_PAGE, False)
    return render_template("index.html",
                           title='Home',
                           form=form,
                           posts=posts)


@app.route('/delete/<int:id>')
@auth.login_required
def delete(id):
    post = Post.query.get(id)
    if post is None:
        flash('Post not found.')
        return redirect(url_for('index'))
    if post.author.id != g.user.id:
        flash('You cannot delete this post.')
        return redirect(url_for('index'))
    db.session.delete(post)
    db.session.commit()
    flash('Your post has been deleted.')
    return redirect(url_for('index'))


@app.route('/register', methods=['GET', 'POST'])
def register():
    if g.user is not None and g.user.is_authenticated():
        return redirect(url_for('index'))
    form = RegisterForm()
    if form.validate_on_submit():
        u = User(email=form.email.data,
                 username=form.username.data,
                 password=generate_password_hash(form.password.data))
        db.session.add(u)
        db.session.commit()
        return redirect(url_for('index'))
    return render_template('register.html',
                           title='Register',
                           form=form)


@app.route('/login', methods=['GET', 'POST'])
def login():
    if g.user is not None and g.user.is_authenticated():
        return redirect(url_for('index'))
    form = LoginForm()
    if form.validate_on_submit():
        user = User.query.filter_by(email=form.email.data).first()
        if not check_password_hash(user.password, form.password.data):
            if user.login_fault < 5:
                user.login_fault += 1
                db.session.commit()
                print user.login_fault
                return redirect(url_for('login'))
            else:
                print "ID " + user.email + " Blocked!!"
                return redirect(url_for('login'))
                #         if login_error_cnt > 5:
                #             print login_error_cnt
                #             user.is_blocked = 1
                #             return redirect(url_for('index'))
        else:
            session_user = db.session.query(User).filter_by(email=form.email.data).first()
            login_user(session_user)
            user.login_fault = 0
            db.session.commit()
            flash('You were logged in')
            return redirect(url_for('index'))
    return render_template('login.html',
                           title='Login',
                           form=form)


# @app.route('/oid_login', methods=['GET', 'POST'])
# @oid.loginhandler
# def oid_login():
#     if g.user is not None and g.user.is_authenticated():
#         return redirect(url_for('index'))
#     form = OidLoginForm()
#     if form.validate_on_submit():
#         session['remember_me'] = form.remember_me.data
#         return oid.try_login(form.openid.data, ask_for=['nickname', 'email'])
#     return render_template('login2.html',
#                            title='Sign in',
#                            form=form,
#                            providers=app.config['OPENID_PROVIDERS'])


@app.route('/logout')
def logout():
    logout_user()
    return redirect(url_for('index'))


# @oid.after_login
# def after_login(resp):
#     if resp.email is None or resp.email == "":
#         flash(gettext('Invalid login. Please try again.'))
#         return redirect(url_for('login'))
#     user = User.query.filter_by(email=resp.email).first()
#     if user is None:
#         username = resp.nickname
#         if username is None or username == "":
#             username = resp.email.split('@')[0]
#         username = User.make_valid_username(username)
#         username = User.make_unique_username(username)
#         user = User(username=username, email=resp.email, role=ROLE_USER)
#         db.session.add(user)
#         db.session.commit()
#         Make the user follow her/himself
#                 db.session.add(user.follow(user))
        # db.session.commit()
    # remember_me = False
    # if 'remember_me' in session:
    #     remember_me = session['remember_me']
    #     session.pop('remember_me', None)
    # login_user(user, remember=remember_me)
    # return redirect(request.args.get('next') or url_for('index'))


@app.route('/userprofile/<username>')
@app.route('/userprofile/<username>/<int:page>')
@auth.login_required
def userprofile(username, page=1):
    user = User.query.filter_by(username=username).first()
    if user is None:
        flash(gettext('User %(username)s not found.', username=username))
        return redirect(url_for('index'))
    posts = user.posts.paginate(page, POSTS_PER_PAGE, False)
    return render_template('user.html',
                           user=user,
                           posts=posts)


@app.route('/edit', methods=['GET', 'POST'])
@auth.login_required
def edit():
    form = EditForm(g.user.username)
    if form.validate_on_submit():
        g.user.username = form.username.data
        g.user.about_me = form.about_me.data
        db.session.add(g.user)
        db.session.commit()
        flash('Your changes have been saved.')
        return redirect(url_for('edit'))
    else:
        form.username.data = g.user.username
        form.about_me.data = g.user.about_me
    return render_template('edit.html',
                           form=form)


@app.route('/follow/<username>')
def follow(username):
    user = User.query.filter_by(username=username).first()
    if user is None:
        flash('User ' + username + ' not found.')
        return redirect(url_for('index'))
    if user == g.user:
        flash('You can\'t follow yourself!')
        return redirect(url_for('user', username=username))
    u = g.user.follow(user)
    if u is None:
        flash('Cannot follow ' + username + '.')
        return redirect(url_for('user', username=username))
    db.session.add(u)
    db.session.commit()
    flash('You are now following ' + username + '!')
    follower_notification(user, g.user)
    return redirect(url_for('userprofile', username=g.user.username))


@app.route('/unfollow/<username>')
def unfollow(username):
    user = User.query.filter_by(username=username).first()
    if user is None:
        flash('User ' + username + ' not found.')
        return redirect(url_for('index'))
    if g.user == user:
        flash('You can\'t unfollow yourself!')
        return redirect(url_for('user', username=username))
    u = g.user.unfollow(user)
    if u is None:
        flash('Cannot unfollow ' + username + '.')
        return redirect(url_for('user', username=username))
    db.session.add(u)
    db.session.commit()
    flash('You have stopped following ' + username + '.')
    return redirect(url_for('userprofile', username=g.user.username))


##### SEARCHING #############################################

@app.route('/search', methods=['POST'])
@auth.login_required
def search():
    if not g.search_form.validate_on_submit():
        return redirect(url_for('index'))
    return redirect(url_for('search_results', query=g.search_form.search.data))


@app.route('/search_results/<query>')
@auth.login_required
def search_results(query):
    results = Post.query.whoosh_search(query, MAX_SEARCH_RESULTS).all()
    return render_template('search_results.html',
                           query=query,
                           results=results)


##### TRANSLATION #############################################

@app.route('/translate', methods=['POST'])
@auth.login_required
def translate():
    return jsonify({
        'text': microsoft_translate(
            request.form['text'],
            request.form['sourceLang'],
            request.form['destLang'])})


@app.before_request
def global_user():
    g.user = current_user
    if g.user.is_authenticated():
        g.user.last_seen = datetime.utcnow()
        db.session.add(g.user)
        db.session.commit()
        g.search_form = SearchForm()
    g.locale = get_locale()


@app.after_request
def after_request(response):
    for query in get_debug_queries():
        if query.duration >= DATABASE_QUERY_TIMEOUT:
            app.logger.warning("SLOW QUERY: %s\nParameters: %s\nDuration: %fs\nContext: %s\n" % (
                query.statement, query.parameters, query.duration, query.context))
    return response


##### ERROR HANDLING ########################################

@app.errorhandler(404)
def internal_error(error):
    return render_template('404.html'), 404


@app.errorhandler(500)
def internal_error(error):
    db.session.rollback()
    return render_template('500.html'), 500


##### Routes for RESTful API #################################
@app.route('/user/<string:username>/bucketlist')
@auth.login_required
def show_buckets(username=None):
    if not username:
        user = g.user
    else:
        user = User.query.filter_by(username=username).first()
    return render_template('bucketlist.html',
                           user=user)


@app.route('/user/<string:username>/bucket/<int:id>')
def bucketdetail(username, id):
    bkt = Bucket.query.filter_by(id=id).first()
    if bkt.parent_id:
        return jsonify({'status': 'error'}), 400
    return render_template('bucketdetail.html', bucket=bkt)


@app.route('/test')
def test_template():
    return render_template('test.html')


@app.route('/html5study')
def html5_study():
    return render_template('html5study/studyMain.html')


@app.route('/html5study/<string:id>')
def uri_redirect(id):
    return render_template('html5study/' + id + '.html')


##### for RESTful API #######################################

### Authentication ###
@app.route('/api/token')
@auth.login_required
def get_auth_token():
    token = g.user.generate_auth_token()
    return jsonify({'user':{'id': g.user.id,
                            'username': g.user.username,
                            'email': g.user.email,
                            'birthday': g.user.birthday,},
                    'token': token.decode('ascii')})


@app.route('/api/resource')
@auth.login_required
def get_resource():
    return jsonify({'id': g.user.id,
            'username': g.user.username,
            'email': g.user.email,
            'birthday': g.user.birthday,})


@auth.verify_password
def verify_password(username_or_token, password):
    # first try to authenticate by token
    if password == "facebook":
        auth = facebook.get_session(token=username_or_token)
        resp = auth.get('/me')
        if resp.status_code == 200:
            fb_user = resp.json()
            # user = User.query.filter_by(email=fb_user.get('email')).first()
            birthday = fb_user['birthday'][6:10] + fb_user['birthday'][0:2] + fb_user['birthday'][3:5]
            user = User.get_or_create(fb_user['email'], fb_user['username'], fb_user['id'], birthday)
        else:
            return False
    else:
        user = User.verify_auth_token(username_or_token)
    if not user:
        # try to authenticate with username/password
        user = User.query.filter_by(email = username_or_token).first()
        if not user or not user.verify_password(password):
            return False
    g.user = user
    return True


@app.route('/login/facebook')
def login():
    redirect_uri = url_for('authorized', _external=True)
    params = {'redirect_uri': redirect_uri, 'scope': 'email, user_birthday'}
    return redirect(facebook.get_authorize_url(**params))


@app.route('/facebook/authorized')
def authorized():
    # check to make sure the user authorized the request
    if not 'code' in request.args:
        return jsonify({'status':'error',
                        'reason':'Authentication failed'})

    # make a request for the access token credentials using code
    redirect_uri = url_for('authorized', _external=True)
    data = dict(code=request.args['code'], redirect_uri=redirect_uri)

    auth = facebook.get_auth_session(data=data)

    # the "me" response
    me = auth.get('me').json()
    birthday = me['birthday'][6:10] + me['birthday'][0:2] + me['birthday'][3:5]
    u = User.get_or_create(me['email'], me['username'], me['id'], birthday)

    return jsonify({'user':{'id':u.id,
                            'username':u.username,
                            'email':u.email,
                            'birthday':u.birthday,},
                    'access_token':auth.access_token})


@app.route('/activate_user/<user_id>')
def activate_user(user_id):
    u = User.query.filter_by(id=user_id).first()
    if not u:
        return jsonify({'error':'User not found'}),401
    else:
        if u.active == 0:
            u.active = 1
            return jsonify({'status':'User Activated'}),200
        else:
            return jsonify({'status':'User aleady activated'}),200




##### RESTful API with Flask-restful  ##################################

plan_fields = {
    'id': fields.Integer,
    'date': fields.String,
    'bucket_id': fields.Integer,
    'user_id': fields.Integer,
    'status': fields.Integer,
    'title': fields.String,
    'status': fields.Integer,
    'private': fields.Integer,
    'deadline': fields.String,
    'scope': fields.String,
    'range': fields.String,
    'rep_type': fields.String,
    'rpt_cndt': fields.String,
    'parent_id': fields.Integer,
}


class PlanListAPI(Resource):
    decorators = [auth.login_required]

    def __init__(self):
        self.reqparse = reqparse.RequestParser()
        super(PlanListAPI, self).__init__()

    def get(self,username):

        data = []
        u = User.query.filter_by(username = username).first()
        if u is None:
            return jsonify({'status':'User does not Exists'})

        for p, b in db.session.query(Plan, Bucket).filter(Plan.bucket_id == Bucket.id,Plan.user_id == g.user.id).order_by(Plan.date.desc(), Bucket.deadline.desc()).all():
            data.append({
                'id': p.id,
                'date': p.date,
                'bucket_id': p.bucket_id,
                'user_id': p.user_id,
                'status': p.status,
                'title': b.title,
                'status': b.status,
                'private': b.private,
                'deadline': b.deadline,
                'scope': b.scope,
                'range': b.range,
                'rep_type': b.rep_type,
                'rpt_cndt': b.rpt_cndt,
                'parent_id': b.parent_id
            })

        return map(lambda t: marshal(t, plan_fields), data), 200


class PlanAPI(Resource):
    decorators = [auth.login_required]

    def __init__(self):
        self.reqparse = reqparse.RequestParser()
        super(PlanAPI, self).__init__()

    def put(self, id):
        if request.json:
            params = request.json
        elif request.form:
            params = request.form
        else:
            return {'status':'Request Failed!'}

        p = Plan.query.filter_by(id=id).first()
        if p.user_id != g.user.id:
            return {'status':'Unauthorized'}, 401

        try:
            for item in params:
                if item:
                    setattr(p, item, params.get(item))
            db.session.commit()
        except:
            return {'status':'failed'}, 401

        return {'status':'succeed'}, 200

api.add_resource(PlanListAPI, '/api/plans/<username>', endpoint='plans')
api.add_resource(PlanAPI, '/api/plan/<id>', endpoint='plan')


##### Revision RESTful API with Flask-restful  ##################################

user_fields = {
    'id': fields.Integer,
    'username': fields.String,
    'email': fields.String,
    'about_me': fields.String,
    'last_seen': fields.String,
    'birthday': fields.String,
    'is_following': fields.Boolean,
    'pic': fields.String,
    'uri': fields.Url('user'),
}

bucket_fields = {
    'id': fields.Integer,
    'user_id': fields.Integer,
    'title': fields.String,
    'description': fields.String,
    'level': fields.String,
    'status': fields.Integer,
    'private': fields.Integer,
    'reg_dt': fields.String,
    'deadline': fields.String,
    'scope': fields.String,
    'range': fields.String,
    'parent_id': fields.Integer,
    'uri': fields.Url('bucket')
}


class UserListAPI(Resource):
    def __init__(self):
        super(UserListAPI, self).__init__()

    @auth.login_required
    def get(self):
        # data = []
        u = User.query.all()
        return map(lambda t:marshal(t, user_fields), u)


    def post(self):
        if request.json:
            params = request.json
        elif request.form:
            params = request.form
        else:
            return {'error':'Request Failed!'}, 400

        # Check Requirements <Email, Password>
        if not 'email' in params:
            return {'error':'Email Address input error!'}, 400
        elif not 'password' in params:
            return {'error':'Password Missing'}, 400

        # Check email address is unique
        if User.email_exists(params['email']):
            return {'error':'Already registered Email address'}, 400

        # Make username based on email address when it was not submitted.
        if not 'username' in params or params['username'] == "":
            username = params['email'].split('@')[0]
            username = User.make_valid_username(username)
            username = User.make_unique_username(username)
        else:
            username = params['username']
            if User.username_exists(username):
                return {'error':'Username already exists.'}, 400

        # Check User Birthday
        if not 'birthday' in params or params['birthday']=="":
            birthday = None
        else:
            birthday = params['birthday']

        u = User(email=params['email'],
                 username=username,
                 fb_id=None,
                 birthday=birthday)

        # Password Hashing
        u.hash_password(params['password'])

        # Database Insert/Commit
        try:
            db.session.add(u)
            db.session.commit()
        except:
            return {'error':'Something went wrong.'}, 500

        send_awaiting_confirm_mail(u)
        # return marshal(u, user_fields), 201
        g.user = u
        token = g.user.generate_auth_token()

        return jsonify({'user':{'id': g.user.id,
                                'username': g.user.username,
                                'email': g.user.email,
                                'birthday': g.user.birthday,},
                        'token': token.decode('ascii')})


class UserAPI(Resource):
    decorators = [auth.login_required]

    def __init__(self):
        super(UserAPI, self).__init__()

    #get specific User's Profile
    def get(self, id):
        u = User.query.filter_by(id=id).first()
        return marshal(u, user_fields), 200

    #modify My User Profile
    def put(self, id):
        if request.json:
            params = request.json
        elif request.form:
            params = request.form
        else:
            return {'error':'Request Failed!'}, 400

        u = User.query.filter_by(id=id).first()
        if u != g.user:
            return {'error': 'Unauthorized'}, 401

        for key in params:
            value = None if params[key]=="" else params[key]    # Or Use (params[key],None)[params[key]==""] Sam Hang Yeonsanja kk
            print key
            print value

            # Nobody can change id, email, fb_id, last_seen
            if key in ['id', 'email', 'fb_id', 'last_seen']:
                return {'error':'Cannot change ' + key}, 400

            # Just ROLE_ADMIN user can change 'role', 'login_fault'
            if key in ['login_fault', 'role'] and g.user.role == ROLE_USER:
                return {'error':'Only Admin can change ' + key}, 401

            # Validate & hash Password
            if key == 'password':
                if len(value) < 4:
                    return {'error':'Password is too short'}, 400
                u.hash_password(value)
                continue                                        # if not continue hash will be reset.

            # Birthday can only be None or 8-digit integer(between 1900/01/01 ~ thisyear 12/31)
            elif key == 'birthday' and value is not None:
                if len(value) != 8 or \
                    int(value[0:4]) < 1900 or int(value[0:4]) > int(datetime.now().strftime("%Y")) or \
                    int(value[4:6]) < 0 or int(value[4:6]) > 12 or \
                    int(value[6:8]) < 0 or int(value[6:8]) > 31:
                        return {"error":"Invalid value for Birthday: " + value[0:4] + '/' + value[4:6] + '/' + value [6:8]}, 400

            # Username cannot be null
            elif key == 'username':
                if value == None:
                    return {'error':'Username cannot be blank'}, 400


            elif key not in ['about_me']:
                return {'error':'Invalid user key'}, 400

            setattr(u, key, value)
        db.session.commit()

        return marshal(u, user_fields), 201

    #delete a User
    def delete(self, id):
        u = User.query.filter_by(id=id).first()
        if u != g.user:
            return {'error':'Unauthorized'}, 401
        else:
            try:
                db.session.delete(u)
                db.session.commit()
            except:
                {'error':'Something went wrong'}, 500

        return {'status':'success'}, 201


class BucketAPI(Resource):
    decorators = [auth.login_required]

    def __init__(self):
        super(BucketAPI, self).__init__()

    def get(self, id):
        b = Bucket.query.filter(Bucket.id==id, Bucket.status!='9').first()
        if b == None:
            return {'error':'No data found'}, 204
        data={
            'id': b.id,
            'user_id': b.user_id,
            'title': b.title,
            'description': b.description,
            'level': b.level,
            'status': b.status,
            'private': b.private,
            'parent_id': b.parent_id,
            'reg_dt': b.reg_dt.strftime("%Y-%m-%d %H:%M:%S"),
            'deadline': b.deadline.strftime("%Y-%m-%d"),
            'scope': b.scope,
            'range': b.range,
            'rep_type': b.rep_type,
            'rpt_cndt': b.rpt_cndt,
            'sub_buckets': []
        }

        b1 = Bucket.query.filter_by(level=int(data['level'])+1).all()
        for i in b1:
            if data['id'] == i.parent_id:
                data['sub_buckets'].append({
                    'id': i.id,
                    'user_id': i.user_id,
                    'title': i.title,
                    'description': i.description,
                    'level': i.level,
                    'status': i.status,
                    'private': i.private,
                    'parent_id': i.parent_id,
                    'reg_dt': i.reg_dt.strftime("%Y-%m-%d %H:%M:%S"),
                    'deadline': i.deadline.strftime("%Y-%m-%d"),
                    'scope': i.scope,
                    'range': i.range,
                    'rep_type': i.rep_type,
                    'rpt_cndt': i.rpt_cndt,
                    'sub_buckets': []
                })

        b2 = Bucket.query.filter_by(level=int(data['level'])+2).all()
        for i in b2:
            for j in range(0,len(data['sub_buckets'])):
                if data['sub_buckets'][j]['id'] == i.parent_id:
                    data['sub_buckets'][j]['sub_buckets'].append({
                        'id': i.id,
                        'user_id': i.user_id,
                        'title': i.title,
                        'description': i.description,
                        'level': i.level,
                        'status': i.status,
                        'private': i.private,
                        'parent_id': i.parent_id,
                        'reg_dt': i.reg_dt.strftime("%Y-%m-%d %H:%M:%S"),
                        'deadline': i.deadline.strftime("%Y-%m-%d"),
                        'scope': i.scope,
                        'range': i.range,
                        'rep_type': i.rep_type,
                        'rpt_cndt': i.rpt_cndt,
                        'sub_buckets':[]
                    })

        b3 = Bucket.query.filter_by(level=int(data['level'])+3).all()
        for i in b3:
            for j in range(0,len(data['sub_buckets'])):
                for k in range(0,len(data['sub_buckets'][j]['sub_buckets'])):
                    if data['sub_buckets'][j]['sub_buckets'][k]['id'] == i.parent_id:
                        data['sub_buckets'][j]['sub_buckets'][k]['sub_buckets'].append({
                            'id': i.id,
                            'user_id': i.user_id,
                            'title': i.title,
                            'description': i.description,
                            'level': i.level,
                            'status': i.status,
                            'private': i.private,
                            'parent_id': i.parent_id,
                            'reg_dt': i.reg_dt.strftime("%Y-%m-%d %H:%M:%S"),
                            'deadline': i.deadline.strftime("%Y-%m-%d"),
                            'scope': i.scope,
                            'range': i.range,
                            'rep_type': i.rep_type,
                            'rpt_cndt': i.rpt_cndt,
                            'sub_buckets':[]
                        })

        b4 = Bucket.query.filter_by(level=int(data['level'])+4).all()
        for i in b4:
            for j in range(0,len(data['sub_buckets'])):
                for k in range(0,len(data['sub_buckets'][j]['sub_buckets'])):
                    for l in range(0,len(data['sub_buckets'][j]['sub_buckets'][k]['sub_buckets'])):
                        if data['sub_buckets'][j]['sub_buckets'][k]['sub_buckets'][l]['id'] == i.parent_id:
                            data['sub_buckets'][j]['sub_buckets'][k]['sub_buckets'][l]['sub_buckets'].append({
                                'id': i.id,
                                'user_id': i.user_id,
                                'title': i.title,
                                'description': i.description,
                                'level': i.level,
                                'status': i.status,
                                'private': i.private,
                                'parent_id': i.parent_id,
                                'reg_dt': i.reg_dt.strftime("%Y-%m-%d %H:%M:%S"),
                                'deadline': i.deadline.strftime("%Y-%m-%d"),
                                'scope': i.scope,
                                'range': i.range,
                                'rep_type': i.rep_type,
                                'rpt_cndt': i.rpt_cndt,
                                'sub_buckets':[]
                            })

        return data, 200

    def put(self, id):
        if request.json:
            params = request.json
        elif request.form:
            params = request.form
        else:
            return {'error':'Request Failed!'}, 500

        b = Bucket.query.filter_by(id=id).first()
        if b.user_id != g.user.id:
            return {'error':'Unauthorized'}, 400

        for key in params:
            value = None if params[key]=="" else params[key]

            # Editable Fields
            if key not in ['title','status','private','deadline','description','parent_id','scope','range','rpt_type','rpt_cndt']:
                return {'error':'Invalid key: '+key}, 400

            # Nobody can modify id, user_id, reg_dt
            if key in ['id','user_id','reg_dt']:
                return {'error':'Cannot change ' + key}, 400

            # Just ROLE_ADMIN user can change 'language', 'level'
            if key in ['language','level'] and g.user.role == ROLE_USER:
                return {'error':'Only Admin can chagne' + key}, 401

            # When modify user's parent_id adjusts its level
            if key == 'parent_id':
                if value == None:
                    params['level'] = '0'
                else:
                    pb = Bucket.query.filter_by(id=int(value)).first() # pb = parent bucket
                    if pb == None:
                        return {'error':'Parent does not exists'}, 400
                    else:
                        params['level'] = str(int(pb.level)+1)

            # Set other key's validation
            if key == 'title' and len(value) > 128:
                return {'error':'Title length must be under 128'}, 400

            if key == 'description' and len(value) > 512:
                return {'error':'Description too long (512)'}, 400

            if key == 'deadline':
                value = datetime.strptime(value,'%Y-%m-%d')

            if key == 'scope' and value not in ['DECADE','YEARLY','MONTHLY']:
                return {'error':'Invalid scope value'}, 400

            if key == 'rpt_type' and value not in ['WKRP','WEEK','MNTH']:
                return {'error':'Invalid repeat-type value'}, 400

            # TODO:Change plan if condition effects today.
            # if key == 'rpt_cndt':

            setattr(b, key, value)

        try:
            db.session.commit()
        except:
            return {'error':'Something went wrong'}, 500

        return {'bucket': marshal(b, bucket_fields)}, 201

    def delete(self, id):
        b = Bucket.query.filter_by(id=id).first()

        # Only bucket's owner can delete action.
        if b.user_id != g.user.id:
            return {'error':'Unauthorized'}, 401

        try:
            b.status = '9'
            db.session.commit()
            return {'status': 'success'}, 200
        except:
            return {'status': 'delete failed'}, 500


class UserBucketAPI(Resource):
    decorators = [auth.login_required]

    def __init__(self):
        super(UserBucketAPI, self).__init__()

    def get(self, id):
        u = User.query.filter_by(id=id).first()
        if not g.user.is_following(u):
            if g.user == u:
                pass
            else:
                return {'error':'User unauthorized'}, 401

        data = []

        if g.user == u:
            b = Bucket.query.filter(Bucket.user_id==u.id,Bucket.status!='9',Bucket.level=='0').all()
        else:
            b = Bucket.query.filter(Bucket.user_id==u.id,Bucket.status!='9',Bucket.level=='0',Bucket.private=='0').all()

        if len(b) == 0:
            return {'error':'No data Found'}, 204

        for i in b:
            data.append({
                'id': i.id,
                'user_id': i.user_id,
                'title': i.title,
                'description': i.description,
                'level': i.level,
                'status': i.status,
                'private': i.private,
                'parent_id': i.parent_id,
                'reg_dt': i.reg_dt.strftime("%Y-%m-%d %H:%M:%S"),
                'deadline': i.deadline.strftime("%Y-%m-%d"),
                'scope': i.scope,
                'range': i.range,
                'rep_type': i.rep_type,
                'rpt_cndt': i.rpt_cndt,
                'sub_buckets': []
            })


        # for ii in data:
        #     bb = Bucket.query.filter_by(parent_id=ii.id).all()
        #     ii['sub_buckets'].append({
        #         'id': bb.id,
        #         'user_id': bb.user_id,
        #         'title': bb.title,
        #         'description': bb.description,
        #         'level': bb.level,
        #         'status': bb.status,
        #         'private': bb.private,
        #         'parent_id': bb.parent_id,
        #         'reg_dt': bb.reg_dt.strftime("%Y-%m-%d %H:%M:%S"),
        #         'deadline': bb.deadline.strftime("%Y-%m-%d"),
        #         'scope': bb.scope,
        #         'range': bb.range,
        #         'rep_type': bb.rep_type,
        #         'rpt_cndt': bb.rpt_cndt,
        #         'sub_buckets': []
        #     })

        if g.user == u:
            b1 = Bucket.query.filter(Bucket.user_id==u.id, Bucket.status!='9',Bucket.level=='1').all()
        else:
            b1 = Bucket.query.filter(Bucket.user_id==u.id, Bucket.status!='9',Bucket.level=='1',Bucket.private=='0').all()
        for i in b1:
            for j in range(0,len(data)):
                if data[j]['id'] == i.parent_id:
                    data[j]['sub_buckets'].append({
                        'id': i.id,
                        'user_id': i.user_id,
                        'title': i.title,
                        'description': i.description,
                        'level': i.level,
                        'status': i.status,
                        'private': i.private,
                        'parent_id': i.parent_id,
                        'reg_dt': i.reg_dt.strftime("%Y-%m-%d %H:%M:%S"),
                        'deadline': i.deadline.strftime("%Y-%m-%d"),
                        'scope': i.scope,
                        'range': i.range,
                        'rep_type': i.rep_type,
                        'rpt_cndt': i.rpt_cndt,
                        'sub_buckets': []
                    })

        if g.user == u:
            b2 = Bucket.query.filter(Bucket.user_id==u.id,Bucket.status!='9',Bucket.level=='2').all()
        else:
            b2 = Bucket.query.filter(Bucket.user_id==u.id,Bucket.status!='9',Bucket.level=='2',Bucket.private=='0').all()
        for i in b2:
            for j in range(0,len(data)):
                for k in range(0,len(data[j]['sub_buckets'])):
                    if data[j]['sub_buckets'][k]['id'] == i.parent_id:
                        data[j]['sub_buckets'][k]['sub_buckets'].append({
                            'id': i.id,
                            'user_id': i.user_id,
                            'title': i.title,
                            'description': i.description,
                            'level': i.level,
                            'status': i.status,
                            'private': i.private,
                            'parent_id': i.parent_id,
                            'reg_dt': i.reg_dt.strftime("%Y-%m-%d %H:%M:%S"),
                            'deadline': i.deadline.strftime("%Y-%m-%d"),
                            'scope': i.scope,
                            'range': i.range,
                            'rep_type': i.rep_type,
                            'rpt_cndt': i.rpt_cndt,
                            'sub_buckets':[]
                        })

        if g.user == u:
            b3 = Bucket.query.filter(Bucket.user_id==u.id,Bucket.status!='9',Bucket.level=='3').all()
        else:
            b3 = Bucket.query.filter(Bucket.user_id==u.id,Bucket.status!='9',Bucket.level=='3',Bucket.private=='0').all()
        for i in b3:
            for j in range(0,len(data)):
                for k in range(0,len(data[j]['sub_buckets'])):
                    for l in range(0,len(data[j]['sub_buckets'][k]['sub_buckets'])):
                        if data[j]['sub_buckets'][k]['sub_buckets'][l]['id'] == i.parent_id:
                            data[j]['sub_buckets'][k]['sub_buckets'][l]['sub_buckets'].append({
                                'id': i.id,
                                'user_id': i.user_id,
                                'title': i.title,
                                'description': i.description,
                                'level': i.level,
                                'status': i.status,
                                'private': i.private,
                                'parent_id': i.parent_id,
                                'reg_dt': i.reg_dt.strftime("%Y-%m-%d %H:%M:%S"),
                                'deadline': i.deadline.strftime("%Y-%m-%d"),
                                'scope': i.scope,
                                'range': i.range,
                                'rep_type': i.rep_type,
                                'rpt_cndt': i.rpt_cndt,
                                'sub_buckets':[]
                            })

        if g.user == u:
            b4 = Bucket.query.filter(Bucket.user_id==u.id,Bucket.status!='9',Bucket.level=='4').all()
        else:
            b4 = Bucket.query.filter(Bucket.user_id==u.id,Bucket.status!='9',Bucket.level=='4',Bucket.private=='0').all()
        for i in b4:
            for j in range(0,len(data)):
                for k in range(0,len(data[j]['sub_buckets'])):
                    for l in range(0,len(data[j]['sub_buckets'][k]['sub_buckets'])):
                        for m in range(0,len(data[j]['sub_buckets'][k]['sub_buckets'][l]['sub_buckets'])):
                            if data[j]['sub_buckets'][k]['sub_buckets'][l]['sub_buckets'][m]['id'] == i.parent_id:
                                data[j]['sub_buckets'][k]['sub_buckets'][l]['sub_buckets'][m]['sub_buckets'].append({
                                    'id': i.id,
                                    'user_id': i.user_id,
                                    'title': i.title,
                                    'description': i.description,
                                    'level': i.level,
                                    'status': i.status,
                                    'private': i.private,
                                    'parent_id': i.parent_id,
                                    'reg_dt': i.reg_dt.strftime("%Y-%m-%d %H:%M:%S"),
                                    'deadline': i.deadline.strftime("%Y-%m-%d"),
                                    'scope': i.scope,
                                    'range': i.range,
                                    'rep_type': i.rep_type,
                                    'rpt_cndt': i.rpt_cndt,
                                    'sub_buckets':[]
                                })

        return data, 200
        # return {'buckets': map(lambda t: marshal(t, bucket_fields), data)}, 200

    def post(self, id):
        u = User.query.filter_by(id=id).first()
        if u.id != g.user.id:
            return {'error':'Unauthorized'}, 401

        if request.json:
            params = request.json
        elif request.form:
            params = request.form
        else:
            return {'error':'Request Failed!'}

        # Replace blank value to None(null) in params
        for key in params:
            params[key] = None if params[key] == "" else params[key]

            if key in ['id', 'user_id', 'reg_dt', 'language']:
                return {'error': key + ' cannot be entered manually.'}, 401

        # Bucket Title required
        if not 'title' in params:
            return {'error':'Bucket title required'}, 401

        # Check ParentID is Valid & set level based on ParentID
        if not 'parent_id' in params or params['parent_id'] == None:
            level = 0
        else:
            b = Bucket.query.filter_by(id=params['parent_id']).first()
            if b is None:
                return {'error':'Invalid ParentID'}, 401
            elif b.user_id != g.user.id:
                return {'error':'Cannot make sub_bucket with other user\'s Bucket'}, 401
            else:
                level = int(b.level) + 1

        bkt = Bucket(title=params['title'],
                     user_id=g.user.id,
                     level=str(level),
                     status= params['status'] if 'status' in params else True,
                     private=params['private'] if 'private' in params else False,
                     reg_dt=datetime.now(),
                     deadline=datetime.strptime(params['deadline'],'%Y/%m/%d').date() if 'deadline' in params else datetime.now(),
                     description=params['description'] if 'description' in params else None,
                     parent_id=params['parent_id'] if 'parent_id' in params else None,
                     scope=params['scope'] if 'scope' in params else None,
                     range=params['range'] if 'range' in params else None,
                     rep_type=params['rep_type'] if 'rep_type' in params else None,
                     rpt_cndt=params['rpt_cndt'] if 'rpt_cndt' in params else None)
        
        db.session.add(bkt)
        db.session.commit()

        return {'bucket': marshal(bkt, bucket_fields)}, 201


api.add_resource(UserAPI, '/api/user/<int:id>', endpoint='user')
api.add_resource(UserListAPI, '/api/users', endpoint='users')
api.add_resource(BucketAPI, '/api/bucket/<int:id>', endpoint='bucket')
api.add_resource(UserBucketAPI, '/api/buckets/user/<int:id>', endpoint='buckets')



class VerificationAPI(Resource):
    def __init__(self):
        super(VerificationAPI, self).__init__()

    def post(self):
        if request.json:
            print "1"
            params = request.json
        elif request.form:
            print "2"
            params = request.form
        else:
            print "3"
            return {'error':'Request Failed!'}, 400

        try:
            if User.email_exists(params['email']):
                return {'error':'Email aleady exists'}, 400
            else:
                return {'success':'Available Email Address'}, 200
        except:
            return {'error':'Something went wrong'}, 500

api.add_resource(VerificationAPI, '/api/valid_email', endpoint='verifyEmail')