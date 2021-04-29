from rest_framework import serializers
from .models import Attendance

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
	class Meta:
		model = Attendance
		fields = ['course', 'student', 'attendanceclass', 'date', 'status']
