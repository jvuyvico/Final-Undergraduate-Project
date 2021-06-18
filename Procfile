web: cd web/college_attendance && gunicorn college_attendance.wsgi --log-file - 
worker: python manage.py qcluster --settings=web.college_attendance.college_attendance.settings