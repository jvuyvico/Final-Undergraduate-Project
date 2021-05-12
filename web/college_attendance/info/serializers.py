from rest_framework import serializers
from .models import Attendance, Course, Student
from django.db import models
from datetime import datetime
import pytz
# class AttendanceSerializer(serializers.Serializer):
#     course = serializers.ReadOnlyField()
#     student = serializers.ReadOnlyField()
#     attendanceclass = serializers.ReadOnlyField()
#     date = serializers.DateField()
#     status = serializers.BooleanField(default='True')

#     def create(self, validated_data):
#     	return Attendance.objects.create(validated_data)

#     def update(self, instance, validated_data):
#     	instance.course = validated_data.get('course', instance.course)
#     	instance.student = validated_data.get('student', instance.student)
#     	instance.attendanceclass = validated_data.get('attendanceclass', instance.attendanceclass)
#     	instance.date = validated_data.get('date', instance.date)
#     	instance.status = validated_data.get('status', instance.status)
#     	instance.save()
#     	return instance


class AttendanceSerializer(serializers.ModelSerializer):

	#course = models.CharField(required=False)

	#course = serializers.CharField(default= Course.objects.get(id='CoE198MAB1'))
	class Meta:
		model = Attendance
		fields = ['course', 'student', 'attendanceclass', 'date', 'status']

class ESP32Serializer(serializers.ModelSerializer):

	#course = models.CharField(required=False)

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
# Test these: https://stackoverflow.com/questions/33641134/django-rest-how-to-modify-value-before-returning-the-rest-response
# https://stackoverflow.com/questions/38388233/drf-allow-all-fields-in-get-request-but-restrict-post-to-just-one-field/38448743#38448743
# https://stackoverflow.com/questions/38316321/change-a-field-in-a-django-rest-framework-modelserializer-based-on-the-request-t
# https://stackoverflow.com/questions/33945148/return-nested-serializer-in-serializer-method-field