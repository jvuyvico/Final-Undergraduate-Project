from django.db import models
from datetime import *

# Create your models here.
# Mostly used for testing
class espData(models.Model):
    dayStamp = models.DateField(auto_now=False, auto_now_add=False)
    timeStamp = models.TimeField(auto_now=False, auto_now_add=False)
    bid = models.PositiveSmallIntegerField()
    rid = models.PositiveSmallIntegerField()
    numID = models.PositiveIntegerField()
    rssi = models.SmallIntegerField()

class espAttendance(models.Model):
    dayStamp = models.DateField(auto_now=False, auto_now_add=True)
    numID = models.PositiveIntegerField()
    bid = models.PositiveSmallIntegerField()
    rid = models.PositiveSmallIntegerField()
    weekDay = models.PositiveSmallIntegerField()
    timeSlot = models.TimeField(auto_now=False, auto_now_add=False, null=True)
    ping = models.PositiveSmallIntegerField()

class timeSlot(models.Model):
    bid = models.PositiveSmallIntegerField()
    rid = models.PositiveSmallIntegerField()
    slotA= models.TimeField(auto_now=False, auto_now_add=False, null=True)# 7-8:30
    slotB= models.TimeField(auto_now=False, auto_now_add=False, null=True)# 8:30-10
    slotC= models.TimeField(auto_now=False, auto_now_add=False, null=True)# 10-11:30
    slotD= models.TimeField(auto_now=False, auto_now_add=False, null=True)# 11:30-1
    slotE= models.TimeField(auto_now=False, auto_now_add=False, null=True)# 1-2:30
    slotF= models.TimeField(auto_now=False, auto_now_add=False, null=True)# 2:30-4
    slotG= models.TimeField(auto_now=False, auto_now_add=False, null=True)# 4-5:30
    slotH= models.TimeField(auto_now=False, auto_now_add=False, null=True)# 5:30-7
    slotI= models.TimeField(auto_now=False, auto_now_add=False, null=True)# 7-9
    deltaA = models.PositiveSmallIntegerField() #class duration in minutes
    deltaB = models.PositiveSmallIntegerField()
    deltaC = models.PositiveSmallIntegerField()
    deltaD = models.PositiveSmallIntegerField()
    deltaE = models.PositiveSmallIntegerField()
    deltaF = models.PositiveSmallIntegerField()
    deltaG = models.PositiveSmallIntegerField()
    deltaH = models.PositiveSmallIntegerField()
    deltaI = models.PositiveSmallIntegerField()

class testData(models.Model):
    dateTime = models.DateTimeField(auto_now=False, auto_now_add=True)
    timeScan = models.FloatField()
    bid = models.PositiveSmallIntegerField()
    rid = models.PositiveSmallIntegerField()
    numID = models.PositiveIntegerField()
    rssi = models.SmallIntegerField()