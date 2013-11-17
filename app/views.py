from social.apps.flask_app import routes

from flask import render_template, redirect, flash, url_for, request, g, session
from flask.ext.login import login_required, logout_user, login_user, current_user

from app import app, db, lm, oid, Base
from forms import LoginForm, EditForm, PostForm
from models import User, Post, ROLE_USER, ROLE_ADMIN
from emails import follower_notification

from datetime import datetime

from config import POSTS_PER_PAGE, MAX_SEARCH_RESULTS


@app.route('/', methods=['GET','POST'])
@app.route('/index', methods=['GET','POST'])
@app.route('/index/<int:page>', methods=['GET','POST'])
@login_required
def index(page=1):
    form = PostForm()
    if form.validate_on_submit():
        post = Post(body = form.post.data, timestamp = datetime.utcnow(), author = g.user)
        db.session.add(post)
        db.session.commit()
        flash('Your post is now live!')
        return redirect(url_for('index'))
    posts = g.user.followed_posts().paginate(page, POSTS_PER_PAGE, False)
    return render_template("index.html",
        title = 'Home',
        form = form,
        posts = posts)

    
@app.route('/login', methods = ['GET', 'POST'])
@oid.loginhandler
def login():
    if g.user is not None and g.user.is_authenticated():
        return redirect(url_for('index'))
    form = LoginForm()
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
        flash('Invalid login. Please try again.')
        return redirect(url_for('login'))
    user = User.query.filter_by(email = resp.email).first()
    if user is None:
        username = resp.nickname
        if username is None or username == "":
            username = resp.email.split('@')[0]
        username = User.make_unique_username(username)
        user = User(username = username, email = resp.email, role = ROLE_USER)
        db.session.add(user)
        db.session.commit()
        # Make the user follow her/himself
        db.session.add(user.follow(user))
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
        flash('User ' + username + ' not found.')
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
    print results
    return render_template('search_results.html',
                           query = query,
                           results = results)


##### SOCIAL LOGIN #############################################
 
# @app.route('/social_login')
# def main():
#     return render_template('home.html')
 

# @login_required
# @app.route('/done/')
# def done():
#     return render_template('done.html')


#@app.route('/logout')
#def logout():
#    """Logout view"""
#    logout_user()
#    return redirect('/')



##### ERROR HANDLER #############################################

@app.errorhandler(404)
def internal_error(error):
    return render_template('404.html'), 404

@app.errorhandler(500)
def internal_error(error):
    db.session.rollback()
    return render_template('500.html'), 500
