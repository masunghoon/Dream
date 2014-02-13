from flask import render_template, url_for
from flask.ext.mail import Message
from app import mail, security
from decorators import async
from config import ADMINS

@async    
def send_async_email(msg):
    mail.send(msg)
    
def send_email(subject, sender, recipients, text_body, html_body):
    msg = Message(subject, sender = sender, recipients = recipients)
    msg.body = text_body
    msg.html = html_body
    send_async_email(msg)
    #thr = threading.Thread(target = send_async_email, args = [msg])
    #thr.start()

    
def follower_notification(followed, follower):
    send_email("[Dream Proj.] %s is now following you!" % follower.username,
        ADMINS[0],
        [followed.email],
        render_template("follower_email.txt",  user = followed, follower = follower),
        render_template("follower_email.html",  user = followed, follower = follower))

def send_awaiting_confirm_mail(user):
    # Send the awaiting for confirmation mail to the user.
    print user.email
    print user.id
    subject = "We're waiting for your confirmation!!"
    msg = Message(subject=subject, sender=ADMINS[0], recipients=[user.email])
    confirmation_url = url_for('activate_user', user_id=user.id, _external=True)
    msg.body = "Dear %s, click here to confirm: %s" % (user.username, confirmation_url)
    send_async_email(msg)
