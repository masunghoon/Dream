import os
import sys

from sqlalchemy import create_engine

from flask import Flask, g
from flask.ext.sqlalchemy import SQLAlchemy
from flask.ext import login
from flask.ext.openid import OpenID
from flask.ext.mail import Mail
from flask.ext.babel import Babel

from sqlalchemy.orm import scoped_session, sessionmaker
from sqlalchemy.ext.declarative import declarative_base

sys.path.append('../..')

from social.apps.flask_app.routes import social_auth
from social.apps.flask_app.models import init_social
from social.apps.flask_app.template_filters import backends

from datetime import datetime

# App
app = Flask(__name__)
app.config.from_object('config')

from config import basedir, ADMINS, MAIL_SERVER, MAIL_PORT, MAIL_USERNAME, MAIL_PASSWORD, LANGUAGES
from app.momentjs import momentjs

# DB
db = SQLAlchemy(app)
db.metadata.bind = create_engine(app.config['SQLALCHEMY_DATABASE_URI'])

engine = create_engine(app.config['SQLALCHEMY_DATABASE_URI'],
                       convert_unicode=True)
# session = scoped_session(sessionmaker(bind=engine))
Base = declarative_base()
# Base.query = db_session.query_property()

app.register_blueprint(social_auth)
social_storage = init_social(app, Base, db.session)

# Login Manager
lm = login.LoginManager()
#lm.login_view = 'main'
lm.login_message = ''
#lm.setup_app(app)
lm.init_app(app)
lm.login_view = 'login'

oid = OpenID(app, os.path.join(basedir, 'tmp'))

# Debugging
if not app.debug:
    import logging
    from logging.handlers import SMTPHandler
    credentials = None
    if MAIL_USERNAME or MAIL_PASSWORD:
        credentials = (MAIL_USERNAME, MAIL_PASSWORD)
    mail_handler = SMTPHandler((MAIL_SERVER, MAIL_PORT), 'no-reply@' + MAIL_SERVER, ADMINS, 'microblog failure', credentials)
    mail_handler.setLevel(logging.ERROR)
    app.logger.addHandler(mail_handler)
    
    from logging.handlers import RotatingFileHandler
    file_handler = RotatingFileHandler('tmp/microblog.log', 'a', 1 * 1024 * 1024, 10)
    file_handler.setFormatter(logging.Formatter('%(asctime)s %(levelname)s: %(message)s [in %(pathname)s:%(lineno)d]'))
    app.logger.setLevel(logging.INFO)
    file_handler.setLevel(logging.INFO)
    app.logger.addHandler(file_handler)
    app.logger.info('microblog startup')

# momentjs
app.jinja_env.globals['momentjs'] = momentjs

# Mailing
mail = Mail(app)

# I18n and L10n
babel = Babel(app)


from app import models
from app import views
from app.forms import SearchForm

@lm.user_loader
def load_user(userid):
    try:
        return models.User.query.get(int(userid))
    except (TypeError, ValueError):
        pass


@app.teardown_appcontext
def commit_on_success(error=None):
    if error is None:
        db.session.commit()


@app.teardown_request
def shutdown_session(exception=None):
    db.session.remove()


@app.context_processor
def inject_user():
    try:
        return {'user': g.user}
    except AttributeError:
        return {'user': None}


app.context_processor(backends)
