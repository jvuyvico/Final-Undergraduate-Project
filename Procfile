web: cd web/college_attendance && gunicorn college_attendance.wsgi --log-file - 
worker: cd web/college_attendance && python manage.py qcluster --settings=college_attendance.settings