#!venv/mini/bin/python
from migrate.versioning import api
from app import app, db, models, Base, engine
from config import SQLALCHEMY_DATABASE_URI, SQLALCHEMY_MIGRATE_REPO

from social.apps.flask_app import models

import os.path
# db.create_all()
db.create_all()
Base.metadata.create_all(bind=engine)
