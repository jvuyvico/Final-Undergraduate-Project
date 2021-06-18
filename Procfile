web: cd web/college_attendance && gunicorn college_attendance.wsgi --log-file - 
worker: python web/college_attendance/manage.py qcluster