from celery import shared_task
from celery import Celery
from .models import Dept, Class, Student, Attendance, Course, Teacher, Assign, AttendanceTotal, time_slots, \
    DAYS_OF_WEEK, AssignTime, AttendanceClass, StudentCourse, espData, espDataDaily
from django.db.models import Count

from datetime import datetime
import pytz
from celery.task import periodic_task




from celery.schedules import crontab




# Part 1 of aggregating the ESP32Data objects into an Attendance object.
# This aggregates ESPData objects into ESPDataDaily Objects (check models.py)
def update_espData_daily():
	espAttendance = espData.objects.all().filter(dayStamp=datetime.now(pytz.timezone('Asia/Manila')).strftime("%Y-%m-%d")).exclude(course="None").order_by("numID", "course")
	ping_count = 1
	num = 0 
	while num < len(espAttendance):
		# Print statements were just for debugging
		# print(num)
		# print(len(espAttendance))
		# print(espAttendance[num])
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

# Used to aggreagate the pings coming from the ESP32 beacon into an actual 
# Attendance object. Turns ESPDataDaily objects into actual Attendance.
def update_attendance_daily():
	espAttendance = espDataDaily.objects.all().filter(dayStamp=datetime.now(pytz.timezone('Asia/Manila')).strftime("%Y-%m-%d"))
	for item in espAttendance:
		if item.pings >= 6: # Change number of pings depending on pings needed per subject

			attendance = Attendance(student = Student.objects.get(USN=item.numID), 
				course=Course.objects.get(id=item.course), date= item.dayStamp)
			attendance.save()
	# Comment out next line for now
	#attendance.save()

	# Test out espTest router, create list of objects as below
    # {
    #     "dayStamp": "2021-06-01",
    #     "timeStamp": "08:35:24",
    #     "bid": "1",
    #     "rid": "1",
    #     "numID": "201506921",
    #     "rssi": "50"
    # }

    # https://docs.djangoproject.com/en/dev/topics/db/aggregation/


# Adds False attendances daily according to which students don't yet have
# any Attendance Object for scheduled classes that day
def daily_attendance_false():
	students = Student.objects.all()
	day = datetime.now(pytz.timezone("Asia/Manila")).strftime("%A")
	for student in students:
		class_id_object = getattr(student, "class_id")
		assign_object = Assign.objects.filter(class_id= class_id_object)
		for item in assign_object:
			for time_period in AssignTime.objects.filter(assign=item):
				if ( (time_period.day == day) ):
					# Next two lines are for debugging / testing
					# if Attendance.objects.filter(student=student, course=item.course, date=datetime.now(pytz.timezone('Asia/Manila')).strftime("%Y-%m-%d")).exists():
					# 	print("attendance already there")
					else:
						temp = Attendance(student=student, course=item.course, date=datetime.now(pytz.timezone('Asia/Manila')).strftime("%Y-%m-%d"), status='False')
						temp.save()