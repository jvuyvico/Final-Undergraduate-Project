from celery import shared_task
from celery import Celery
from .models import Dept, Class, Student, Attendance, Course, Teacher, Assign, AttendanceTotal, time_slots, \
    DAYS_OF_WEEK, AssignTime, AttendanceClass, StudentCourse, espData, espDataDaily
from django.db.models import Count

from datetime import datetime
import pytz
#from celery.task.schedules import crontab
from celery.task import periodic_task


time = "19:11"
date_time_obj = datetime.strptime(time, '%H:%M').replace(tzinfo=pytz.timezone('Asia/Manila'))

#https://stackoverflow.com/questions/32449845/how-to-run-a-django-celery-task-every-6am-and-6pm-daily
# https://stackoverflow.com/questions/49116586/celery-tasks-on-django-models
from celery.schedules import crontab


# Check this when deploying to Heroku - https://www.reddit.com/r/django/comments/kjx6ja/deploying_django_q_to_heroku/
# https://mattsegal.dev/simple-scheduled-tasks.html
#@shared_task
#@periodic_task(run_every=crontab(minute=date_time_obj.minute, hour=date_time_obj.hour))

def update_espData_daily():
	#espAttendance = espData.objects.annotate(count=Count('numID') )
	espAttendance = espData.objects.all().filter(dayStamp=datetime.now(pytz.timezone('Asia/Manila')).strftime("%Y-%m-%d")).exclude(course="None").order_by("numID", "course")
	ping_count = 1
	num = 0 
	while num < len(espAttendance):
		print(num)
		print(len(espAttendance))
		print(espAttendance[num])
		if ( (espAttendance[num].numID == espAttendance[num + 1].numID) and (espAttendance[num].course == espAttendance[num + 1].course) ):
			ping_count += 1
			num += 1
			if num == len(espAttendance) - 1:
				temp = espDataDaily(numID = espAttendance[num].numID, course=espAttendance[num].course, pings=ping_count, dayStamp=espAttendance[num].dayStamp)
				temp.save()
				break
		else:
			if ping_count >= 1:
				temp = espDataDaily(numID = espAttendance[num].numID, course=espAttendance[num].course, pings=ping_count, dayStamp=espAttendance[num].dayStamp)
				temp.save()
			ping_count = 1
			num += 1
			if num == len(espAttendance) - 1:
				break
def update_attendance_daily():
	#attendance = Attendance(student=Student.objects.get(USN='201506921'), course=Course.objects.get(id='CoE198MAB1'))
	espAttendance = espDataDaily.objects.all().filter(dayStamp=datetime.now(pytz.timezone('Asia/Manila')).strftime("%Y-%m-%d"))
	for item in espAttendance:
		if item.pings >= 2:

			attendance = Attendance(student = Student.objects.get(USN=item.numID), course=Course.objects.get(id=item.course), date= item.dayStamp)
			attendance.save()
	# Comment out next line for now
	#attendance.save()

	# Test out espTest router
    # {
    #     "dayStamp": "2021-06-01",
    #     "timeStamp": "08:35:24",
    #     "bid": "1",
    #     "rid": "1",
    #     "numID": "201506921",
    #     "rssi": "50"
    # }

    # https://docs.djangoproject.com/en/dev/topics/db/aggregation/