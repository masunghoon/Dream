#!flask/bin/python
import os
import unittest

# from config import basedir
from app import app, db, db_session
from app.models.user import *
# from app.models.post import Post

from datetime import datetime, timedelta

import pdb

basedir = os.path.abspath(os.path.dirname(__file__))

class TestCase(unittest.TestCase):
    def setUp(self):
        app.config['TESTING'] = True
        app.config['CSRF_ENABLED'] = False
        app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///' + os.path.join(basedir, 'test.db')
        self.app = app.test_client()
        db.create_all()

    def tearDown(self):
        db_session.remove()
        db.drop_all()

#     def test_avatar(self):
#         u = User(username = 'john', email = 'john@example.com')
#         avatar = u.avatar(128)
#         expected = 'http://www.gravatar.com/avatar/d4c74594d841139328695756648b6bd6'
#         assert avatar[0:len(expected)] == expected

#     def test_make_unique_username(self):
#         u = User(username = 'john', email = 'john@example.com')
#         db_session.add(u)
#         db_session.commit()
#         username = User.make_unique_username('john')
#         assert username != 'john'
#         u = User(username = username, email = 'susan@example.com')
#         db_session.add(u)
#         db_session.commit()
#         username2 = User.make_unique_username('john')
#         assert username2 != 'john'
#         assert username2 != username
    
    def test_follow_posts(self):
#         pdb.set_trace()
        # make four users
        u1 = User(username = 'john', email = 'john@example.com')
        u2 = User(username = 'susan', email = 'susan@example.com')
        u3 = User(username = 'mary', email = 'mary@example.com')
        u4 = User(username = 'david', email = 'david@example.com')
        db_session.add(u1)
        db_session.add(u2)
        db_session.add(u3)
        db_session.add(u4)
        db_session.commit()
        # make four posts
        utcnow = datetime.utcnow()
        p1 = Post(body = "post from john", author = u1, timestamp = utcnow + timedelta(seconds = 1))
        p2 = Post(body = "post from susan", author = u2, timestamp = utcnow + timedelta(seconds = 2))
        p3 = Post(body = "post from mary", author = u3, timestamp = utcnow + timedelta(seconds = 3))
        p4 = Post(body = "post from david", author = u4, timestamp = utcnow + timedelta(seconds = 4))
        db_session.add(p1)
        db_session.add(p2)
        db_session.add(p3)
        db_session.add(p4)
        db_session.commit()
        # setup the followers
        u1.follow(u1) # john follows himself
        u1.follow(u2) # john follows susan
        u1.follow(u4) # john follows david
        u2.follow(u2) # susan follows herself
        u2.follow(u3) # susan follows mary
        u3.follow(u3) # mary follows herself
        u3.follow(u4) # mary follows david
        u4.follow(u4) # david follows himself
        db_session.add(u1)
        db_session.add(u2)
        db_session.add(u3)
        db_session.add(u4)
        db_session.commit()
        # check the followed posts of each user
        f1 = u1.followed_posts().all()
        f2 = u2.followed_posts().all()
        f3 = u3.followed_posts().all()
        f4 = u4.followed_posts().all()
        assert len(f1) == 3
        assert len(f2) == 2
        assert len(f3) == 2
        assert len(f4) == 1
        assert f1 == [p4, p2, p1]
        assert f2 == [p3, p2]
        assert f3 == [p4, p3]
        assert f4 == [p4]

if __name__ == '__main__':
    unittest.main()
