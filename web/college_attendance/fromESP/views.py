from django.shortcuts import render
from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt
from .serializers import *
from rest_framework.response import Response
from rest_framework.decorators import api_view
from django.http import JsonResponse
from rest_framework import status
from datetime import *
from .models import *

# Create your views here.
@api_view(['POST'])
@csrf_exempt
def fromESPview(request):
    #serializer = EspDataSerializer(data=request.data) #request.data is seen as list
    dataList = request.data
    #for loop on request.data then pass each instance to serializer
    for obj in dataList:
        serializer = EspDataSerializer(data=obj)
        if serializer.is_valid():   #if you keep requet.data as is, an error will pop up saying: expected dictionary, got list instead
            serializer.save()
            #return Response(serializer.data, status = status.HTTP_201_CREATED)
        else:
            print(serializer.errors)

    espAttendanceView()
    return Response(serializer.data)

def espAttendanceView():
    timeSlots = timeSlot.objects.all()
    timeDic = {}
    deltaDic = {}
    for obj in timeSlots:
        strID = str(obj.bid) + "-" + str(obj.rid)
        tempList = [obj.slotA, obj.slotB, obj.slotC, obj.slotD, obj.slotE, obj.slotF, obj.slotG, obj.slotH, obj.slotI]
        timeList = []
        #get valid timeslots
        for item in tempList:
            if item not in timeList:
                if item != time(0, 0, 0):
                    timeList.append(item)
        timeDic[strID] = timeList

        tempDeltaList = [obj.deltaA, obj.deltaB, obj.deltaC, obj.deltaD, obj.deltaE, obj.deltaF, obj.deltaG, obj.deltaH, obj.deltaI]
        timeDeltaList = []
        #get valid timedeltas
        for delta in tempDeltaList:
            if delta != 0:
                timeDeltaList.append(delta)
        deltaDic[strID] = timeDeltaList
    # print(timeDic)
    # print(deltaDic)

    #get all objects scanned today
    today = date.today()
    espToday = espData.objects.filter(dayStamp=today)
    # print(today.weekday())

    #generate SN list for today
    snList = []
    for obj in espToday:
        if obj.numID > 190000000:
            if obj.numID not in snList:
                snList.append(obj.numID)
    #print(snList)

    #organize espData into espAttendance
    for sn in snList:
        snQuery = espToday.filter(numID=sn)
        brList = [] #list of rooms that detected this sn
        for snObj in snQuery:
            bld = snObj.bid
            rm = snObj.rid
            brKey = str(bld) + "-" + str(rm)
            if brKey not in brList:
                brList.append(brKey)
        # print(sn)
        # print(brList)
        for key in brList:
            for idx, tSlot in enumerate(timeDic[key]):
                #generate timeSlot + timedelta
                delta = timedelta(minutes=deltaDic[key][idx])
                dt = datetime.combine(date.today(), tSlot) + delta
                delTime = dt.time()

                #filter snQuery wrt tSlot and delTime
                slotQuery = snQuery.filter(timeStamp__gte=tSlot).filter(timeStamp__lte=delTime)

                #get bid and rid with greatest rssi
                maxRssi = -999
                b = 0
                r = 0
                for slotObj in slotQuery:
                    if slotObj.rssi > maxRssi:
                        maxRssi = slotObj.rssi
                        b = slotObj.bid
                        r = slotObj.rid

                #get num of times this sn is scanned in this bid rid
                scanCount = slotQuery.filter(bid=b).filter(rid=r).count()

                if scanCount > 0:
                    #check if sn, tslot, bid, rid, today combination exists in espAttendance db
                    wantedQ = espAttendance.objects.filter(bid=b).filter(rid=r).filter(numID=sn).filter(timeSlot=tSlot).filter(dayStamp=today)
                    if wantedQ.exists():
                        wObj = wantedQ.first()
                        if wantedQ.count() > 1:
                            print("This Shouldn't happen")
                            wObj.ping = scanCount
                            wObj.save()
                        else:
                            #update
                            wObj.ping = scanCount
                            wObj.save()
                    else:
                        #create the object
                        espAttendance.objects.create(numID=sn, bid=b, rid=r, weekDay=today.weekday(), timeSlot = tSlot, ping=scanCount)

    #change this return to something informative (not an http response)
    return HttpResponse()
