# from social.apps.flask_app import routes

from flask import render_template, redirect, flash, url_for, request, g, session, jsonify, abort
from flask.ext.login import login_required, logout_user, login_user, current_user
from flask.ext.babel import gettext
from flask.ext.sqlalchemy import get_debug_queries
from flask.ext.restful import Resource, reqparse, fields, marshal
from sqlalchemy.sql import func
from werkzeug import check_password_hash, generate_password_hash
from flask.ext.httpauth import HTTPBasicAuth

auth = HTTPBasicAuth()

from guess_language import guessLanguage

from app import app, db, lm, oid, Base, babel, api
from forms import LoginForm, OidLoginForm, EditForm, PostForm, SearchForm, RegisterForm
from models import User, Post, Bucket, Plan, ROLE_USER, ROLE_ADMIN
from emails import follower_notification
from translate import microsoft_translate

from config import POSTS_PER_PAGE, MAX_SEARCH_RESULTS, LANGUAGES, DATABASE_QUERY_TIMEOUT

from datetime import datetime, timedelta, date

now = datetime.utcnow()


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


@app.route('/oid_login', methods=['GET', 'POST'])
@oid.loginhandler
def oid_login():
    if g.user is not None and g.user.is_authenticated():
        return redirect(url_for('index'))
    form = OidLoginForm()
    if form.validate_on_submit():
        session['remember_me'] = form.remember_me.data
        return oid.try_login(form.openid.data, ask_for=['nickname', 'email'])
    return render_template('login2.html',
                           title='Sign in',
                           form=form,
                           providers=app.config['OPENID_PROVIDERS'])


@app.route('/logout')
def logout():
    logout_user()
    return redirect(url_for('index'))


@oid.after_login
def after_login(resp):
    if resp.email is None or resp.email == "":
        flash(gettext('Invalid login. Please try again.'))
        return redirect(url_for('login'))
    user = User.query.filter_by(email=resp.email).first()
    if user is None:
        username = resp.nickname
        if username is None or username == "":
            username = resp.email.split('@')[0]
        username = User.make_valid_username(username)
        username = User.make_unique_username(username)
        user = User(username=username, email=resp.email, role=ROLE_USER)
        db.session.add(user)
        db.session.commit()
        # Make the user follow her/himself
        #         db.session.add(user.follow(user))
        db.session.commit()
    remember_me = False
    if 'remember_me' in session:
        remember_me = session['remember_me']
        session.pop('remember_me', None)
    login_user(user, remember=remember_me)
    return redirect(request.args.get('next') or url_for('index'))


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
    if bkt.parentID:
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
    user = User.verify_auth_token(username_or_token)
    if not user:
        # try to authenticate with username/password
        user = User.query.filter_by(email = username_or_token).first()
        if not user or not user.verify_password(password):
            return False
    g.user = user
    return True





# @app.route('/api/getUserDday', methods=['GET'])
# def getUserDday():
#     u = User.query.filter_by(username=g.user.username).first()
#     birth = datetime.strptime(u.birthday, '%Y%m%d')
#
#     decade = []
#     for i in range(1, 10):
#         data = {'range': str((i - 1) * 10) + '\'s',
#                 'userDueDate': datetime.strftime(birth + timedelta(3652.5 * i), '%Y-%m-%d')}
#         if int(data['userDueDate'][0:4]) - 10 < datetime.now().year <= int(data['userDueDate'][0:4]):
#         # if int(data['userDueDate'][0:4]) - 10 < datetime.now().year and int(data['userDueDate'][0:4]) >= datetime.now().year:
#             data['current'] = 'OK'
#         else:
#             data['current'] = 'NO'
#         decade.append(data)
#     decade.append({'range': 'lifetime', 'userDueDate': 'None', 'current': 'NO'})
#
#     year = []
#     for i in range(100):
#         data = {'range': 'Year ' + str(birth.year + i),
#                 'dueDate': datetime.strftime(date(birth.year + i, 12, 31), '%Y/%m/%d')}
#         if data['dueDate'][0:4] == str(datetime.now().year):
#             data['current'] = 'OK'
#         else:
#             data['current'] = 'NO'
#         year.append(data)
#
#     month = []
#     for i in range(1, 13):
#         if i < 10:
#             ii = '0' + str(i)
#         else:
#             ii = str(i)
#         data = {'range': 'Month ' + ii, 'dueDate': '/' + ii + '/31'}
#         if i == datetime.now().month:
#             data['current'] = 'OK'
#             data['dueDate'] = str(datetime.now().year) + data['dueDate']
#         else:
#             data['current'] = 'NO'
#             if i < datetime.now().month:
#                 data['dueDate'] = str(datetime.now().year + 1) + data['dueDate']
#             else:
#                 data['dueDate'] = str(datetime.now().year) + data['dueDate']
#         month.append(data)
#
#     return jsonify({'decade': decade, 'yearly': year, 'monthly': month})


##### RESTful API with Flask-restful  ##################################

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
    'is_live': fields.Integer,
    'is_private': fields.Integer,
    'reg_date': fields.String,
    'deadline': fields.String,
    'scope': fields.String,
    'range': fields.String,
    'parent_id': fields.Integer,
    'uri': fields.Url('bucket')
}

plan_fields = {
    'id': fields.Integer,
    'date': fields.String,
    'bucket_id': fields.Integer,
    'user_id': fields.Integer,
    'isDone': fields.Integer,
    'title': fields.String,
    'is_live': fields.Integer,
    'is_private': fields.Integer,
    'deadline': fields.String,
    'scope': fields.String,
    'range': fields.String,
    'rptType': fields.String,
    'rptCndt': fields.String,
    'parent_id': fields.Integer,
}


class BucketAPI(Resource):
    decorators = [auth.login_required]

    def __init__(self):
        # self.reqparse = reqparse.RequestParser()
        # self.reqparse.add_argument('title', type=str, required=True, help='No task title privided', location='json')
        # self.reqparse.add_argument('description', type=str, default="", location='json')
        # self.reqparse.add_argument('level', type=str, default="1", location='json')
        # self.reqparse.add_argument('is_live', type=int, default=0, location='json')
        # self.reqparse.add_argument('is_private', type=int, default=0, location='json')
        # self.reqparse.add_argument('reg_date', type=datetime, default=now, location='json')
        # self.reqparse.add_argument('deadline', type=str, location='json')
        super(BucketAPI, self).__init__()

    def get(self, id):
        u = User.query.filter_by(username=g.user.username).first()
        if not g.user.is_following(u):
            if g.user == u:
                pass
            else:
                return {'status': 'Unauthorized'}, 401
        b = Bucket.query.filter_by(id=id).order_by(Bucket.deadline).first()

        if u != g.user:
            if b.is_private:
                return {'status': 'Unauthorized'}, 401
        todo = []
        t = Bucket.query.filter_by(parentID=id).order_by(Bucket.deadline).all()
        for j in t:
            if u != g.user:
                if j.is_private:
                    continue
            todo.append({
                'todoID': j.id,
                'todoTitle': j.title,
                'todoIsLive': j.is_live,
                'todoIsPrivate': j.is_private,
                'todoParent_id': j.parentID,
                'todoRegDate': j.reg_date.strftime("%Y-%m-%d %H:%M:%S"),
                'todoDeadline': j.deadline.strftime("%Y-%m-%d"),
                'todoScope': j.scope,
                'todoRange': j.range,
                'todoRptType': j.rptType,
                'todoRptCndt': j.rptCndt
            })

        data = {
            'id': b.id,
            'user_id': b.user_id,
            'title': b.title,
            'description': b.description,
            'level': b.level,
            'is_live': b.is_live,
            'is_private': b.is_private,
            'parent_id': b.parentID,
            'reg_date': b.reg_date.strftime("%Y-%m-%d %H:%M:%S"),
            'deadline': b.deadline.strftime("%Y-%m-%d"),
            'scope': b.scope,
            'range': b.range,
            'rptType': b.rptType,
            'rptCndt': b.rptCndt,
            'todos': todo
        }

        return data, 200

    def post(self, id):
        if request.json:
            params = request.json
        elif request.form:
            params = request.form
        else:
            return {'status':'Request Failed!'}

        if not params or not 'title' in params:
            abort(400)
        if not 'deadline' in params or params.get('deadline') == "":
            dueDate = Bucket.query.filter_by(id=id).first().deadline
        else:
            dueDate = datetime.strptime(params.get('deadline'), '%Y/%m/%d').date()
        bkt = Bucket(title=params.get('title'),
                     description=params.get('description'),
                     user_id=g.user.id,
                     level=params.get('level'),
                     is_live=bool(params.get('is_live')),
                     is_private=bool(params.get('is_private')),
                     reg_date=datetime.now(),
                     deadline=dueDate,
                     # parentID=id,
                     parentID=params.get('parent_id'),
                     scope=params.get('scope'),
                     range=params.get('range')
        )
        db.session.add(bkt)
        db.session.commit()
        return {'bucket': marshal(bkt, bucket_fields)}, 201

    def put(self, id):
        if request.json:
            params = request.json
        elif request.form:
            params = request.form
        else:
            return {'status':'Request Failed!'}

        bkt = Bucket.query.filter_by(id=id).first()
        for item in params:
            if item:
                setattr(bkt, item, params.get(item))
        db.session.commit()
        return {'bucket': marshal(bkt, bucket_fields)}, 201

    def delete(self, id):
        print id
        bkt = Bucket.query.filter_by(id=id).first()
        print bkt.title
        try:
            print "1"
            db.session.delete(bkt)
            print "2"
            db.session.commit()
            print "3"
            return {'status': 'success'}, 200
        except:
            return {'status': 'delete failed'}, 400 # HTTP Status Code Review


class UserListAPI(Resource):
    def __init__(self):
        # self.reqparse = reqparse.RequestParser()
        # self.reqparse.add_argument('title', type=str, required=True, help='No task title privided', location='json')
        # self.reqparse.add_argument('description', type=str, default="", location='json')
        # self.reqparse.add_argument('level', type=str, default="1", location='json')
        # self.reqparse.add_argument('is_live', type=int, default=0, location='json')
        # self.reqparse.add_argument('is_private', type=int, default=0, location='json')
        # self.reqparse.add_argument('reg_date', type=datetime, default=now, location='json')
        # self.reqparse.add_argument('deadline', type=str, location='json')
        # self.reqparse.add_argument('parentID', type=str, default=0, location='json')
        # self.reqparse.add_argument('scope', type=str, location='json')
        # self.reqparse.add_argument('range', type=str, location='json')
        super(UserListAPI, self).__init__()

    @auth.login_required
    def get(self):
        data = []
        u = User.query.all()
        for i in u:
            if i == g.user:
                continue
            else:
                data.append({
                    'id': i.id,
                    'username': i.username,
                    'email': i.email,
                    'about_me': i.about_me,
                    'last_seen': i.last_seen.strftime("%Y-%m-%d %H:%M:%S"),
                    'birthday': i.birthday,
                    'is_following': g.user.is_following(i),
                    'pic': '<img src="' + i.avatar(64) + '">',
                })
        return {'users': map(lambda t: marshal(t, user_fields), data)}, 200


    def post(self):
        if request.json:
            params = request.json
        elif request.form:
            params = request.form
        else:
            return {'status':'Request Failed!'}

        if not 'email' in params:
            return {'status':'error','reason':'Email Address input error!'}
        elif not 'password' in params:
            return {'status':'error','reason':'Password Missing'}

        if params.get('command') == 'register':
            if params.get('username') is None or params.get('username') == "":
                username = params.get('email').split('@')[0]
            else:
                username = params.get('username')
            username = User.make_valid_username(username)
            username = User.make_unique_username(username)
            u = User(email=params.get('email'),
                     username=username,
                     # birthday=params.get('birthday'),
                     last_seen=datetime.now())
            u.hash_password(params.get('password'))
            db.session.add(u)
            db.session.commit()
        elif params.get('command') == 'login':
            u = User.query.filter_by(email=params.get('email')).first()
            if u is None:
                return {'status':'error','reason':'User Not Exists!'}
            elif not u.verify_password(params.get('password')):
                return {'status':'error','reason':'Wrong Password!'}
            try:
                login_user(u)
                u.login_fault = 0
                db.session.commit()
                flash('You were logged in')
            except:
                return{'status':'error','reason':'Something wrong after Authentication.'}
        else:
            return{'status':'error','reason':'Command is worng(login or register)'}

        return {'user': marshal(u, user_fields)}, 201


class UserAPI(Resource):
    decorators = [auth.login_required]

    def __init__(self):
        self.reqparse = reqparse.RequestParser()
        # self.reqparse.add_argument('email', type=str, location='json')
        # self.reqparse.add_argument('username', type=str, location='json')
        # self.reqparse.add_argument('password', type=str, location='json')
        # self.reqparse.add_argument('about_me', type=str, location='json')
        # self.reqparse.add_argument('last_seen', type=datetime, default=datetime.now(), location='json')
        # self.reqparse.add_argument('birthday', type=str, location='json')
        super(UserAPI, self).__init__()

    #get specific User's Profile
    def get(self, username):
        u = User.query.filter_by(username=username).first()
        data = {
            'id': u.id,
            'username': u.username,
            'email': u.email,
            'about_me': u.about_me,
            'last_seen': u.last_seen.strftime("%Y-%m-%d %H:%M:%S"),
            'birthday': u.birthday,
            'is_following': g.user.is_following(u),
            'pic': '<img src="' + u.avatar(64) + '">',
        }
        return {'user': marshal(data, user_fields)}, 200

    #modify My User Profile
    def put(self, username):
        u = User.query.filter_by(username=username).first()
        if u != g.user:
            return {'status': 'Unauthorized'}, 401
        u.username = request.json.get('username', u.username)
        u.birthday = request.json.get('birthday', u.birthday)
        if request.json.get('password'):
            u.hash_password(request.json.get('password'))
        u.about_me = request.json.get('about_me', u.about_me)
        db.session.commit()
        data = {
            'id': u.id,
            'username': u.username,
            'email': u.email,
            'about_me': u.about_me,
            'last_seen': u.last_seen.strftime("%Y-%m-%d %H:%M:%S"),
            'birthday': u.birthday,
            'is_following': g.user.is_following(u),
            'pic': '<img src="' + u.avatar(64) + '">',
        }
        return {'user': marshal(data, user_fields)}, 201

    #delete a User
    def delete(self, username):
        u = User.query.filter_by(username=username).first()
        if u != g.user:
            return {'status': 'Unauthorized'}, 401
        db.session.delete(u)
        db.session.commit()
        return {'status': 'success'}, 201


class UserBucketAPI(Resource):
    decorators = [auth.login_required]

    def __init__(self):
        self.reqparse = reqparse.RequestParser()
        # self.reqparse.add_argument('title', type=str, required=True, help='No task title privided', location='json')
        # self.reqparse.add_argument('description', type=str, default='', location='json')
        # self.reqparse.add_argument('level', type=str, default='10000000', location='json')
        # self.reqparse.add_argument('is_live', type=bool, default=False, location='json')
        # self.reqparse.add_argument('is_private', type=bool, default=False, location='json')
        # self.reqparse.add_argument('reg_date', type=datetime, default=now, location='json')
        # self.reqparse.add_argument('deadline', type=str, default='2999/12/31', location='json')
        # self.reqparse.add_argument('parentID', type=int, default=0, location='json')
        # self.reqparse.add_argument('scope', type=str, default='DECADE', location='json')
        # self.reqparse.add_argument('range', type=str, default='20', location='json')
        super(UserBucketAPI, self).__init__()

    def get(self, username):
        u = User.query.filter_by(username=username).first()
        if not g.user.is_following(u):
            if g.user == u:
                pass
            else:
                return {'status': 'Unauthorized'}, 401
        b = Bucket.query.filter_by(user_id=u.id).order_by(Bucket.deadline).all()

        data = []
        for i in b:
            if u != g.user:
                if i.is_private:
                    continue
            if i.scope == 'TODO':
                continue
            todo = []
            t = Bucket.query.filter_by(parentID=i.id).order_by(Bucket.deadline).all()
            for j in t:
                if u != g.user:
                    if j.is_private:
                        continue
                todo.append({
                    'todoID': j.id,
                    'todoTitle': j.title,
                    'todoIsLive': j.is_live,
                    'todoIsPrivate': j.is_private,
                    'todoParent_id': j.parentID,
                    'todoRegDate': j.reg_date.strftime("%Y-%m-%d %H:%M:%S"),
                    'todoDeadline': j.deadline.strftime("%Y-%m-%d"),
                    'todoScope': j.scope,
                    'todoRange': j.range,
                    'todoRptType': j.rptType,
                    'todoRptCndt': j.rptCndt
                })

            data.append({
                'id': i.id,
                'user_id': i.user_id,
                'title': i.title,
                'description': i.description,
                'level': i.level,
                'is_live': i.is_live,
                'is_private': i.is_private,
                'parent_id': i.parentID,
                'reg_date': i.reg_date.strftime("%Y-%m-%d %H:%M:%S"),
                'deadline': i.deadline.strftime("%Y-%m-%d"),
                'scope': i.scope,
                'range': i.range,
                'rptType': i.rptType,
                'rptCndt': i.rptCndt,
                'todos': todo
            })

        return data, 200
        # return {'buckets': map(lambda t: marshal(t, bucket_fields), data)}, 200

    def post(self, username):
        if request.json:
            params = request.json
        elif request.form:
            params = request.form
        else:
            return {'status':'Request Failed!'}

        if not 'title' in params:
            return {'status': 'error'}, 401
        if not 'deadline' in params or params.get('deadline') is None:
            dueDate = datetime.strptime('2999/12/31', '%Y/%m/%d').date()
        else:
            dueDate = datetime.strptime(params.get('deadline'), '%Y-%m-%d').date()
        bkt = Bucket(title=params.get('title'),
                     description=params.get('description'),
                     user_id=g.user.id,
                     level=params.get('level'),
                     is_live=bool(params.get('is_live')),
                     is_private=bool(params.get('is_private')),
                     parentID=params.get('parentID'),
                     reg_date=datetime.now(),
                     deadline=dueDate,
                     scope=params.get('scope'),
                     range=params.get('range')
        )
        db.session.add(bkt)
        db.session.commit()

        return {'bucket': marshal(bkt, bucket_fields)}, 201


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
                'isDone': p.isDone,
                'title': b.title,
                'is_live': b.is_live,
                'is_private': b.is_private,
                'deadline': b.deadline,
                'scope': b.scope,
                'range': b.range,
                'rptType': b.rptType,
                'rptCndt': b.rptCndt,
                'parent_id': b.parentID
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


class TestAPI(Resource):
    decorators = [auth.login_required]

    def __init__(self):
        self.reqparse = reqparse.RequestParser()
        super(TestAPI, self).__init__()

    def get(self, username):
        data = []
        u = User.query.filter_by(username=username).first()
        if u is None:
            return jsonify({'status':'User does not Exists'})

        b = Bucket.query.filter_by(user_id=u.id, level='0').all()
        for i in b:
            data.append({
                'id': i.id,
                'user_id': i.user_id,
                'title': i.title,
                'description': i.description,
                'level': i.level,
                'is_live': i.is_live,
                'is_private': i.is_private,
                'parent_id': i.parentID,
                'reg_date': i.reg_date.strftime("%Y-%m-%d %H:%M:%S"),
                'deadline': i.deadline.strftime("%Y-%m-%d"),
                'scope': i.scope,
                'range': i.range,
                'rptType': i.rptType,
                'rptCndt': i.rptCndt,
                'subBuckets': []
            })

        b1 = Bucket.query.filter_by(user_id=u.id, level='1').all()
        for i in b1:
            for j in range(0,len(data)):
                if data[j]['id'] == i.parentID:
                    data[j]['subBuckets'].append({
                        'id': i.id,
                        'user_id': i.user_id,
                        'title': i.title,
                        'description': i.description,
                        'level': i.level,
                        'is_live': i.is_live,
                        'is_private': i.is_private,
                        'parent_id': i.parentID,
                        'reg_date': i.reg_date.strftime("%Y-%m-%d %H:%M:%S"),
                        'deadline': i.deadline.strftime("%Y-%m-%d"),
                        'scope': i.scope,
                        'range': i.range,
                        'rptType': i.rptType,
                        'rptCndt': i.rptCndt,
                        'subBuckets': []
                    })

        b2 = Bucket.query.filter_by(user_id=u.id, level='2').all()
        for i in b2:
            for j in range(0,len(data)):
                for k in range(0,len(data[j]['subBuckets'])):
                    if data[j]['subBuckets'][k]['id'] == i.parentID:
                        data[j]['subBuckets'][k]['subBuckets'].append({
                            'id': i.id,
                            'user_id': i.user_id,
                            'title': i.title,
                            'description': i.description,
                            'level': i.level,
                            'is_live': i.is_live,
                            'is_private': i.is_private,
                            'parent_id': i.parentID,
                            'reg_date': i.reg_date.strftime("%Y-%m-%d %H:%M:%S"),
                            'deadline': i.deadline.strftime("%Y-%m-%d"),
                            'scope': i.scope,
                            'range': i.range,
                            'rptType': i.rptType,
                            'rptCndt': i.rptCndt,
                            'subBuckets':[]
                        })

        b3 = Bucket.query.filter_by(user_id=u.id, level='3').all()
        for i in b3:
            for j in range(0,len(data)):
                for k in range(0,len(data[j]['subBuckets'])):
                    for l in range(0,len(data[j]['subBuckets'][k]['subBuckets'])):
                        if data[j]['subBuckets'][k]['subBuckets'][l]['id'] == i.parentID:
                            data[j]['subBuckets'][k]['subBuckets'][l]['subBuckets'].append({
                                'id': i.id,
                                'user_id': i.user_id,
                                'title': i.title,
                                'description': i.description,
                                'level': i.level,
                                'is_live': i.is_live,
                                'is_private': i.is_private,
                                'parent_id': i.parentID,
                                'reg_date': i.reg_date.strftime("%Y-%m-%d %H:%M:%S"),
                                'deadline': i.deadline.strftime("%Y-%m-%d"),
                                'scope': i.scope,
                                'range': i.range,
                                'rptType': i.rptType,
                                'rptCndt': i.rptCndt,
                                'subBuckets':[]
                            })

        b4 = Bucket.query.filter_by(user_id=u.id, level='4').all()
        for i in b4:
            for j in range(0,len(data)):
                for k in range(0,len(data[j]['subBuckets'])):
                    for l in range(0,len(data[j]['subBuckets'][k]['subBuckets'])):
                        for m in range(0,len(data[j]['subBuckets'][k]['subBuckets'][l]['subBuckets'])):
                            if data[j]['subBuckets'][k]['subBuckets'][l]['subBuckets'][m]['id'] == i.parentID:
                                data[j]['subBuckets'][k]['subBuckets'][l]['subBuckets'][m]['subBuckets'].append({
                                    'id': i.id,
                                    'user_id': i.user_id,
                                    'title': i.title,
                                    'description': i.description,
                                    'level': i.level,
                                    'is_live': i.is_live,
                                    'is_private': i.is_private,
                                    'parent_id': i.parentID,
                                    'reg_date': i.reg_date.strftime("%Y-%m-%d %H:%M:%S"),
                                    'deadline': i.deadline.strftime("%Y-%m-%d"),
                                    'scope': i.scope,
                                    'range': i.range,
                                    'rptType': i.rptType,
                                    'rptCndt': i.rptCndt,
                                    'subBuckets':[]
                                })

        return data, 200

class TestAPI2(Resource):
    decorators = [auth.login_required]

    def __init__(self):
        self.reqparse = reqparse.RequestParser()
        super(TestAPI2, self).__init__()

    def get(self, id):
        # data = []
        u = User.query.filter_by(username=g.user.username).first()
        if u is None:
            return jsonify({'status':'User does not Exists'})

        b = Bucket.query.filter_by(user_id=u.id, id=id).first()
        data={
            'id': b.id,
            'user_id': b.user_id,
            'title': b.title,
            'description': b.description,
            'level': b.level,
            'is_live': b.is_live,
            'is_private': b.is_private,
            'parent_id': b.parentID,
            'reg_date': b.reg_date.strftime("%Y-%m-%d %H:%M:%S"),
            'deadline': b.deadline.strftime("%Y-%m-%d"),
            'scope': b.scope,
            'range': b.range,
            'rptType': b.rptType,
            'rptCndt': b.rptCndt,
            'subBuckets': []
        }

        b1 = Bucket.query.filter_by(user_id=u.id, level=int(data['level'])+1).all()
        for i in b1:
            if data['id'] == i.parentID:
                data['subBuckets'].append({
                    'id': i.id,
                    'user_id': i.user_id,
                    'title': i.title,
                    'description': i.description,
                    'level': i.level,
                    'is_live': i.is_live,
                    'is_private': i.is_private,
                    'parent_id': i.parentID,
                    'reg_date': i.reg_date.strftime("%Y-%m-%d %H:%M:%S"),
                    'deadline': i.deadline.strftime("%Y-%m-%d"),
                    'scope': i.scope,
                    'range': i.range,
                    'rptType': i.rptType,
                    'rptCndt': i.rptCndt,
                    'subBuckets': []
                })

        b2 = Bucket.query.filter_by(user_id=u.id, level=int(data['level'])+2).all()
        for i in b2:
            for j in range(0,len(data['subBuckets'])):
                if data['subBuckets'][j]['id'] == i.parentID:
                    data['subBuckets'][j]['subBuckets'].append({
                        'id': i.id,
                        'user_id': i.user_id,
                        'title': i.title,
                        'description': i.description,
                        'level': i.level,
                        'is_live': i.is_live,
                        'is_private': i.is_private,
                        'parent_id': i.parentID,
                        'reg_date': i.reg_date.strftime("%Y-%m-%d %H:%M:%S"),
                        'deadline': i.deadline.strftime("%Y-%m-%d"),
                        'scope': i.scope,
                        'range': i.range,
                        'rptType': i.rptType,
                        'rptCndt': i.rptCndt,
                        'subBuckets':[]
                    })

        b3 = Bucket.query.filter_by(user_id=u.id, level=int(data['level'])+3).all()
        for i in b3:
            for j in range(0,len(data['subBuckets'])):
                for k in range(0,len(data['subBuckets'][j]['subBuckets'])):
                    if data['subBuckets'][j]['subBuckets'][k]['id'] == i.parentID:
                        data['subBuckets'][j]['subBuckets'][k]['subBuckets'].append({
                            'id': i.id,
                            'user_id': i.user_id,
                            'title': i.title,
                            'description': i.description,
                            'level': i.level,
                            'is_live': i.is_live,
                            'is_private': i.is_private,
                            'parent_id': i.parentID,
                            'reg_date': i.reg_date.strftime("%Y-%m-%d %H:%M:%S"),
                            'deadline': i.deadline.strftime("%Y-%m-%d"),
                            'scope': i.scope,
                            'range': i.range,
                            'rptType': i.rptType,
                            'rptCndt': i.rptCndt,
                            'subBuckets':[]
                        })

        b4 = Bucket.query.filter_by(user_id=u.id, level=int(data['level'])+4).all()
        for i in b4:
            for j in range(0,len(data['subBuckets'])):
                for k in range(0,len(data['subBuckets'][j]['subBuckets'])):
                    for l in range(0,len(data['subBuckets'][j]['subBuckets'][k]['subBuckets'])):
                        if data['subBuckets'][j]['subBuckets'][k]['subBuckets'][l]['id'] == i.parentID:
                            data['subBuckets'][j]['subBuckets'][k]['subBuckets'][l]['subBuckets'].append({
                                'id': i.id,
                                'user_id': i.user_id,
                                'title': i.title,
                                'description': i.description,
                                'level': i.level,
                                'is_live': i.is_live,
                                'is_private': i.is_private,
                                'parent_id': i.parentID,
                                'reg_date': i.reg_date.strftime("%Y-%m-%d %H:%M:%S"),
                                'deadline': i.deadline.strftime("%Y-%m-%d"),
                                'scope': i.scope,
                                'range': i.range,
                                'rptType': i.rptType,
                                'rptCndt': i.rptCndt,
                                'subBuckets':[]
                            })

        return data, 200

api.add_resource(UserAPI, '/api/user/<username>', endpoint='user')
api.add_resource(UserListAPI, '/api/users', endpoint='users')
api.add_resource(BucketAPI, '/api/bucket/<id>', endpoint='bucket')
api.add_resource(UserBucketAPI, '/api/buckets/<username>', endpoint='buckets')
api.add_resource(PlanListAPI, '/api/plans/<username>', endpoint='plans')
api.add_resource(PlanAPI, '/api/plan/<id>', endpoint='plan')
api.add_resource(TestAPI, '/api/test/<username>', endpoint='test')
api.add_resource(TestAPI2, '/api/test2/<id>', endpoint='test2')
