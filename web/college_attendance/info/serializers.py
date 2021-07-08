from rest_framework import serializers
from .models import Attendance, Course, Student, espData, Assign, AssignTime, Course, Course_Mapping
from django.db import models
from datetime import datetime
import pytz



class AttendanceSerializer(serializers.ModelSerializer):

	#course = models.CharField(required=False)

	#course = serializers.CharField(default= Course.objects.get(id='CoE198MAB1'))
	class Meta:
		model = Attendance
		fields = ['course', 'student', 'attendanceclass', 'date', 'status']

class StudentSerializer(serializers.ModelSerializer):
	class Meta:
		model = Student
		fields = '__all__'

class StudentAssignSerializer(serializers.ModelSerializer):
	class Meta:
		model = Assign
		fields = '__all__'

class StudentAssignTimeSerializer(serializers.ModelSerializer):
	class Meta:
		model = AssignTime
		fields = '__all__'

class StudentCourseSerializer(serializers.ModelSerializer):
	class Meta:
		model = Course
		fields = '__all__'

class StudentCourseMappingSerializer(serializers.ModelSerializer):
	class Meta:
		model = Course_Mapping
		fields = '__all__'

class ESP32Serializer(serializers.ModelSerializer):

	#course = models.CharField(required=False)
	# Set as default before changing in views.py
	course = serializers.CharField(default= Course.objects.get(id='CoE198MAB1'))
	class Meta:
		model = Attendance
		fields = ['course', 'student', 'attendanceclass', 'date', 'status']

	def to_representation(self, data):
		data = super(ESP32Serializer, self).to_representation(data)
		#temp_stud = Student.objects.get('student')
		now = datetime.now(pytz.timezone('Asia/Manila'))
		current_time = now.strftime("%H:%M:%S")

		test_time1 = datetime(2018, 1, 1, 15, 15, 15)
		test_time2 = datetime(2018, 1, 1, 16, 15, 15)

		start_time = test_time1.strftime("%H:%M:%S")
		end_time = test_time2.strftime("%H:%M:%S")

		
		return data


class EspDataSerializer(serializers.ModelSerializer):
	#dayStamp = models.DateField(required = False)
	#dayStamp = serializers.DateTimeField(default=datetime.now())
	#dayStamp = serializers.DateField(initial=datetime.today)
	class Meta:
		model = espData
		fields = ('dayStamp', 'timeStamp', 'bid', 'rid', 'numID', 'rssi', 'course')
