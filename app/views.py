from social.apps.flask_app import routes

from flask import render_template, redirect, flash, url_for, request, g, session, jsonify, abort
from flask.ext.login import login_required, logout_user, login_user, current_user
from flask.ext.babel import gettext
from flask.ext.sqlalchemy import get_debug_queries
from flask.ext.restful import Resource, reqparse, fields, marshal
from werkzeug import check_password_hash, generate_password_hash

from sqlalchemy import and_, or_

from guess_language import guessLanguage

from app import app, db, lm, oid, Base, babel, api
from forms import LoginForm, OidLoginForm, EditForm, PostForm, SearchForm, RegisterForm
from models import User, Post, Bucket, ROLE_USER, ROLE_ADMIN
from emails import follower_notification
from translate import microsoft_translate

from config import POSTS_PER_PAGE, MAX_SEARCH_RESULTS, LANGUAGES, DATABASE_QUERY_TIMEOUT

from datetime import datetime
import time

now = datetime.utcnow()

@babel.localeselector
def get_locale():
    return request.accept_languages.best_match(LANGUAGES.keys())


@app.route('/', methods=['GET','POST'])
@app.route('/index', methods=['GET','POST'])
@app.route('/index/<int:page>', methods=['GET','POST'])
@login_required
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
        title = 'Home',
        form = form,
        posts = posts)


@app.route('/delete/<int:id>')
@login_required
def delete(id):
    post = Post.query.get(id)
    if post == None:
        flash('Post not found.')
        return redirect(url_for('index'))
    if post.author.id != g.user.id:
        flash('You cannot delete this post.')
        return redirect(url_for('index'))
    db.session.delete(post)
    db.session.commit()
    flash('Your post has been deleted.')
    return redirect(url_for('index'))


@app.route('/register', methods = ['GET', 'POST'])
def register():
    if g.user is not None and g.user.is_authenticated():
        return redirect(url_for('index'))
    form = RegisterForm()
    if form.validate_on_submit():
        u = User(email = form.email.data,
                 username = form.username.data,
                 password = generate_password_hash(form.password.data))
        db.session.add(u)
        db.session.commit()
        return redirect(url_for('index'))
    return render_template('register.html',
                           title='Register',
                           form=form)


@app.route('/login', methods=['GET','POST'])
def login():        
    if g.user is not None and g.user.is_authenticated():
        return redirect(url_for('index'))
    form = LoginForm()
    if form.validate_on_submit():
        user = User.query.filter_by(email = form.email.data).first()
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
            
        
   
@app.route('/oid_login', methods = ['GET', 'POST'])
@oid.loginhandler
def oid_login():
    if g.user is not None and g.user.is_authenticated():
        return redirect(url_for('index'))
    form = OidLoginForm()
    if form.validate_on_submit():
        session['remember_me'] = form.remember_me.data
        return oid.try_login(form.openid.data, ask_for = ['nickname','email'])
    return render_template('login.html',
                           title = 'Sign in',
                           form = form,
                           providers = app.config['OPENID_PROVIDERS'])


@app.route('/logout')
def logout():
    logout_user()
    return redirect(url_for('index'))


@oid.after_login
def after_login(resp):
    if resp.email is None or resp.email == "":
        flash(gettext('Invalid login. Please try again.'))
        return redirect(url_for('login'))
    user = User.query.filter_by(email = resp.email).first()
    if user is None:
        username = resp.nickname
        if username is None or username == "":
            username = resp.email.split('@')[0]
        username = User.make_valid_username(username)
        username = User.make_unique_username(username)
        user = User(username = username, email = resp.email, role = ROLE_USER)
        db.session.add(user)
        db.session.commit()
        # Make the user follow her/himself
#         db.session.add(user.follow(user))
        db.session.commit()
    remember_me = False
    if 'remember_me' in session:
        remember_me = session['remember_me']
        session.pop('remember_me', None)
    login_user(user, remember = remember_me)
    return redirect(request.args.get('next') or url_for('index'))


@app.route('/user/<username>')
@app.route('/user/<username>/<int:page>')
@login_required
def user(username, page=1):
    user = User.query.filter_by(username = username).first()
    if user == None:
        flash(gettext('User %(username)s not found.', username = username))
        return redirect(url_for('index'))
    posts = user.posts.paginate(page, POSTS_PER_PAGE, False)
    return render_template('user.html',
                           user = user,
                           posts = posts)


@app.route('/edit', methods = ['GET','POST'])
@login_required
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
                           form = form)
    
    
@app.route('/follow/<username>')
def follow(username):
    user = User.query.filter_by(username = username).first()
    if user == None:
        flash('User ' + username + ' not found.')
        return redirect(url_for('index'))
    if user == g.user:
        flash('You can\'t follow yourself!')
        return redirect(url_for('user', username = username))
    u = g.user.follow(user)
    if u is None:
        flash('Cannot follow ' + username + '.')
        return redirect(url_for('user', username = username))
    db.session.add(u)
    db.session.commit()
    flash('You are now following ' + username + '!')
    follower_notification(user, g.user)
    return redirect(url_for('user', username = username))


@app.route('/unfollow/<username>')
def unfollow(username):
    user = User.query.filter_by(username = username).first()
    if user == None:
        flash('User ' + username + ' not found.')
        return redirect(url_for('index'))
    if user == g.user:
        flash('You can\'t unfollow yourself!')
        return redirect(url_for('user', username = username))
    u = g.user.unfollow(user)
    if u is None:
        flash('Cannot unfollow ' + username + '.')
        return redirect(url_for('user', username = username))
    db.session.add(u)
    db.session.commit()
    flash('You have stopped following ' + username + '.')
    return redirect(url_for('user', username = username))        


##### SEARCHING #############################################

@app.route('/search', methods = ['POST'])
@login_required
def search():
    if not g.search_form.validate_on_submit():
        return redirect(url_for('index'))
    return redirect(url_for('search_results', query = g.search_form.search.data))


@app.route('/search_results/<query>')
@login_required
def search_results(query):
    results = Post.query.whoosh_search(query, MAX_SEARCH_RESULTS).all()
    return render_template('search_results.html',
                           query = query,
                           results = results)


##### TRANSLATION #############################################

@app.route('/translate', methods=['POST'])
@login_required
def translate():
    return jsonify({
        'text': microsoft_translate(
            request.form['text'],
            request.form['sourceLang'],
            request.form['destLang']) })


@app.before_request
#def before_reuqest():
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
            app.logger.warning("SLOW QUERY: %s\nParameters: %s\nDuration: %fs\nContext: %s\n" % (query.statement, query.parameters, query.duration, query.context))
    return response


##### Routes for RESTful API #################################

@app.route('/buckets')
def show_buckets():
    return render_template('bucketlist.html')


##### for RESTful API #######################################

@app.route('/api/v0.1/buckets', methods=['GET'])
def get_buckets():
    data = []
    bkt = Bucket.query.filter_by(user_id = '6')\
                      .filter_by(is_live = '0')\
                      .all()
    for i in bkt:
        data.append({
            'id':i.id,
            'user_id':i.user_id,
            'title':i.title.encode('utf-8'),
        })
    return jsonify({'buckets':data}), 200


@app.route('/api/v0.1/buckets/<int:bkt_id>', methods=['GET'])
def get_bucket(bkt_id):
    bkt = Bucket.query.filter_by(id = bkt_id).first()
    data = {
        'id':bkt.id,
        'user_id':bkt.user_id,
        'title':bkt.title.encode('utf-8'),
    }
    return jsonify({'buckets':data}), 200


@app.route('/api/v0.1/buckets', methods=['POST'])
def create_bucket():
    if not request.json or not 'title' in request.json:
        abort(400)
#     print request.json
    bkt = Bucket(title=request.json['title'], user_id='6')
    db.session.add(bkt)
    db.session.commit()
    return jsonify({'status':'success'}), 201


@app.route('/api/v0.1/buckets/<int:bkt_id>', methods=['PUT'])
def modify_bucket(bkt_id):
    bkt = Bucket.query.filter_by(id = bkt_id).first()
    bkt.title = request.json.get('title', bkt.title)
    bkt.level = request.json.get('level', bkt.level)
    bkt.is_live = request.json.get('is_live', bkt.is_live)
    bkt.is_private = request.json.get('is_private', bkt.is_private)
    bkt.deadline = request.json.get('deadline', bkt.deadline)
    db.session.commit()
    return jsonify({'status':'sucess'}), 201
    
    
@app.route('/api/v0.1/buckets/<int:bkt_id>', methods=['DELETE'])
def del_bucket(bkt_id):
    bkt = Bucket.query.filter_by(id = bkt_id).first()
    db.session.delete(bkt)
    db.session.commit()
    return jsonify({'status':'success'}), 201


##### RESTful API with Flask-restful  ##################################

bucket_fields = {
    'id': fields.Integer,
    'user_id': fields.Integer,
    'title': fields.String,
    'level': fields.String,
    'is_live': fields.Integer,
    'is_private': fields.Integer,
    'reg_date': fields.String,
#     'uri': fields.Url('bucket')
}

class BucketListAPI(Resource):
    decorators = [login_required]

    def __init__(self):
        self.reqparse = reqparse.RequestParser()
        self.reqparse.add_argument('title', type=str, required=True, help='No task title privided', location='json')
        self.reqparse.add_argument('level', type=str, default="1", location='json')
        self.reqparse.add_argument('is_live', type=int, default=0, location='json')
        self.reqparse.add_argument('is_private', type=int, default=0, location='json')
        self.reqparse.add_argument('reg_date', type=datetime, default=now, location='json')
#         self.reqparse.add_argument('deadline', type=datetime, default=strptime("31 Dec 2099" "%d %b %y"), location='json')
        super(BucketListAPI, self).__init__()

    def get(self):
        data = []
        bkt = Bucket.query.filter_by(user_id = g.user.id)\
                          .filter_by(is_live = '0')\
                          .all()
        for i in bkt:
            data.append({
                'id':i.id,
                'user_id':i.user_id,
                'title':i.title,
                'level':i.level,
                'is_live':i.is_live,
                'is_private':i.is_private,
                'reg_date':i.reg_date.strftime("%Y-%m-%d %H:%M:%S"),
            })
#         return {'buckets':data}, 200 
        return {'buckets': map(lambda t: marshal(t, bucket_fields), data)}, 200
#         return {'buckets': marshal(data, bucket_fields)}, 200
    
    def post(self):
        args = self.reqparse.parse_args()
        if not request.json or not 'title' in request.json:
            abort(400)
        bkt = Bucket(title=args['title'], 
                     user_id=g.user.id, 
                     level=args['level'], 
                     is_live=args['is_live'],
                     is_private=args['is_private'],
                     reg_date=args['reg_date'])
        db.session.add(bkt)
        db.session.commit()
        return {'status':'success'}, 201
    

class BucketAPI(Resource):
    decorators = [login_required]

    def __init__(self):
        self.reqparse = reqparse.RequestParser()
        self.reqparse.add_argument('title', type=str, required=True, help='No task title privided', location='json')
        self.reqparse.add_argument('level', type=str, default="1", location='json')
        self.reqparse.add_argument('is_live', type=int, default=0, location='json')
        self.reqparse.add_argument('is_private', type=int, default=0, location='json')
        self.reqparse.add_argument('reg_date', type=datetime, default=now, location='json')
#         self.reqparse.add_argument('deadline', type=datetime, default=strptime("31 Dec 2099" "%d %b %y"), location='json')
        super(BucketAPI, self).__init__()

    def get(self, bkt_id):
        bkt = Bucket.query.filter_by(id = bkt_id).first()
        data = {
            'id':bkt.id,
            'user_id':bkt.user_id,
            'title':bkt.title.encode('utf-8'),
        }
        return {'buckets':data}, 200
    
    def put(self, bkt_id):
        bkt = Bucket.query.filter_by(id = bkt_id).first()
        bkt.title = request.json.get('title', bkt.title)
        bkt.level = request.json.get('level', bkt.level)
        bkt.is_live = request.json.get('is_live', bkt.is_live)
        bkt.is_private = request.json.get('is_private', bkt.is_private)
        bkt.deadline = request.json.get('deadline', bkt.deadline)
        db.session.commit()
        return {'status':'sucess'}, 201
    
    def delete(self, id):
        bkt = Bucket.query.filter_by(id = bkt_id).first()
        db.session.delete(bkt)
        db.session.commit()
        return {'status':'success'}, 201

    
api.add_resource(BucketListAPI, '/api/buckets', endpoint='buckets')
api.add_resource(BucketAPI, '/api/buckets/<int:bkt_id>', endpoint='bucket')

##### ERROR HANDLING ########################################

@app.errorhandler(404)
def internal_error(error):
    return render_template('404.html'), 404


@app.errorhandler(500)
def internal_error(error):
    db.session.rollback()
    return render_template('500.html'), 500
