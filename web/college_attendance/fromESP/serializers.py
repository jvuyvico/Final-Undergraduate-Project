from rest_framework import serializers
from .models import *

class EspDataSerializer(serializers.ModelSerializer):
    class Meta:
        model = espData
        fields = ('dayStamp', 'timeStamp', 'bid', 'rid', 'numID', 'rssi')

class EspAttendanceSerializer(serializers.ModelSerializer):
	class Meta:
		model = espAttendance
		fields = ('numID', 'bid', 'rid', 'weekDay', 'timeSlot', 'ping')
        
class TestDataSerializer(serializers.ModelSerializer):
    class Meta:
        model = testData
        fields = ('timeScan', 'bid', 'rid', 'numID', 'rssi')