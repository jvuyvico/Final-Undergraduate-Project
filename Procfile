web: cd web/college_attendance && gunicorn college_attendance.wsgi --log-file - 
worker: python web/college_attendance/manage.py qcluster --settings=web.college_attendance.college_attendance.settings