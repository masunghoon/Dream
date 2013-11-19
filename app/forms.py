from flask.ext.wtf import Form, TextField, BooleanField, TextAreaField
from flask.ext.wtf import Required, Length
from models import User

class LoginForm(Form):
    openid = TextField('openid', validators = [Required()])
    remember_me = BooleanField('remember_me', default = False)
    
class EditForm(Form):
    username = TextField('username', validators = [Required()])
    about_me = TextAreaField('about_me', validators = [Length(min = 0, max = 140)])

    def __init__(self, original_username, *args, **kwargs):
        Form.__init__(self, *args, **kwargs)
        self.original_username = original_username
        
    def validate(self):
        if not Form.validate(self):
            return False
        if self.username.data == self.original_username:
            return True
        if self.username.data != User.make_vaild_username(self.username.data):
            self.username.errorsappend(gettext('This username has invalid characters. Please use letters, numbers, dots and undersocres only.'))
            return False
        user = User.query.filter_by(username = self.username.data).first()
        if user != None:
            self.username.errors.append(gettext('This username is aleady in use. Please choose another one.'))
            return False
        return True
        
class PostForm(Form):
    post = TextField('post', validators=[Required()])

class SearchForm(Form):
    search = TextField('search', validators = [Required()])
