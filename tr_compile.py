#!venv/mini/bin/python
import os
import sys
if sys.platform == 'wn32':
    pybabel = 'flask\\Scripts\\pybabel'
else:
    pybabel = 'venv/mini/bin/pybabel'
os.system(pybabel + ' compile -f -d app/translations')
