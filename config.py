# -*- coding: utf-8 -*-
from app import app

app.debug = True

CSRF_ENABLED = True
SECRET_KEY = 'you-will-never-guess'
SESSION_COOKIE_NAME = 'dream_project'

##### DATABASE ####################################
import os 
basedir = os.path.abspath(os.path.dirname(__file__))

SQLALCHEMY_RECORD_QUERIES = True
SQLALCHEMY_DATABASE_URI = 'mysql://apps:apps@localhost/apps'
# slow database query threshold (in seconds)
DATABASE_QUERY_TIMEOUT = 0.5
DEBUG_TB_INTERCEPT_REDIRECTS = False
SESSION_PROTECTION = 'strong'

OPENID_PROVIDERS = [
    { 'name': 'Google', 'url': 'https://www.google.com/accounts/o8/id' },
    { 'name': 'Yahoo', 'url': 'https://me.yahoo.com' },
    { 'name': 'AOL', 'url': 'http://openid.aol.com/<username>' },
    { 'name': 'Flickr', 'url': 'http://www.flickr.com/<username>' },
    { 'name': 'MyOpenID', 'url': 'https://www.myopenid.com' }]

##### MAILING ####################################
# mail server settings
MAIL_SERVER = 'smtp.cafe24.com'
MAIL_PORT = 587
MAIL_USE_TLS = True
MAIL_USE_SSL = False
MAIL_USERNAME = 'admin@massi.kr'
MAIL_PASSWORD = 'admin9988'

# administrator list
ADMINS = ['admin@massi.kr']

##### PAGINATION #################################
POSTS_PER_PAGE = 3

##### SEARCHING ##################################
WHOOSH_BASE = os.path.join(basedir, 'search.db')
MAX_SEARCH_RESULTS = 50

##### I18n and L10n ##############################
# available languages
LANGUAGES = {
    'en': 'English',
    'es': 'Español',
    'ko': 'Korean'
}

# microsoft translation service
MS_TRANSLATOR_CLIENT_ID = 'Dream_project'
MS_TRANSLATOR_CLIENT_SECRET = 'PyFBJRYDzH8DKqp+9bdU/hBFALTQcahMmcqjL83fe2I='

##### SOCIAL AUTH ################################
SOCIAL_AUTH_LOGIN_URL = '/login'
SOCIAL_AUTH_LOGIN_REDIRECT_URL = '/index'
SOCIAL_AUTH_USER_MODEL = 'app.models.User'
SOCIAL_AUTH_AUTHENTICATION_BACKENDS = (
    'social.backends.twitter.TwitterOAuth',
    'social.backends.facebook.FacebookOAuth2',
#     'social.backends.open_id.OpenIdAuth',
#     'social.backends.google.GoogleOpenId',
#     'social.backends.google.GoogleOAuth2',
#     'social.backends.google.GoogleOAuth',
#     'social.backends.yahoo.YahooOpenId',
#     'social.backends.stripe.StripeOAuth2',
#     'social.backends.persona.PersonaAuth',
#     'social.backends.facebook.FacebookAppOAuth2',
#     'social.backends.yahoo.YahooOAuth',
#     'social.backends.angel.AngelOAuth2',
#     'social.backends.behance.BehanceOAuth2',
#     'social.backends.bitbucket.BitbucketOAuth',
#     'social.backends.box.BoxOAuth2',
#     'social.backends.linkedin.LinkedinOAuth',
#     'social.backends.github.GithubOAuth2',
#     'social.backends.foursquare.FoursquareOAuth2',
#     'social.backends.instagram.InstagramOAuth2',
#     'social.backends.live.LiveOAuth2',
#     'social.backends.vk.VKOAuth2',
#     'social.backends.dailymotion.DailymotionOAuth2',
#     'social.backends.disqus.DisqusOAuth2',
#     'social.backends.dropbox.DropboxOAuth',
#     'social.backends.evernote.EvernoteSandboxOAuth',
#     'social.backends.fitbit.FitbitOAuth',
#     'social.backends.flickr.FlickrOAuth',
#     'social.backends.livejournal.LiveJournalOpenId',
#     'social.backends.soundcloud.SoundcloudOAuth2',
#     'social.backends.thisismyjam.ThisIsMyJamOAuth1',
#     'social.backends.stocktwits.StocktwitsOAuth2',
#     'social.backends.tripit.TripItOAuth',
#     'social.backends.twilio.TwilioAuth',
#     'social.backends.xing.XingOAuth',
#     'social.backends.yandex.YandexOAuth2',
#     'social.backends.podio.PodioOAuth2',
#     'social.backends.reddit.RedditOAuth2',
)

SOCIAL_AUTH_TWITTER_KEY = 'FL686aqs4NVkWyrrxs7zQ'
SOCIAL_AUTH_TWITTER_SECRET = 'bxcIpVfAf8OMhKmj966C1EynevAz5OvoLYNlMEYxFE'

SOCIAL_AUTH_FACEBOOK_KEY = '268430496627035'
SOCIAL_AUTH_FACEBOOK_SECRET = '6693091870169acf20aacc35233ed65c'
