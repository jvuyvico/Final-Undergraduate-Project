from django.shortcuts import render, get_object_or_404
from django.http import HttpResponseRedirect
from .models import Dept, Class, Student, Attendance, Course, Teacher, Assign, AttendanceTotal, time_slots, \
    DAYS_OF_WEEK, AssignTime, AttendanceClass, StudentCourse
from django.urls import reverse
from django.utils import timezone
from django.contrib.auth.decorators import login_required
from django.contrib.auth.models import User

from django.http import HttpResponse, JsonResponse
from rest_framework.parsers import JSONParser
from .serializers import AttendanceSerializer, ESP32Serializer
from django.views.decorators.csrf import csrf_exempt
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status
from rest_framework.views import APIView

from rest_framework import viewsets
from django.shortcuts import get_object_or_404

class AttendanceViewSet(viewsets.ViewSet):
    def list(self, request):
        attendance = Attendance.objects.all()
        serializer = AttendanceSerializer(attendance, many=True)
        lookup_field = "USN"
        return Response(serializer.data)
    def create(self, request):
        serializer = AttendanceSerializer(data = request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
            
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
@csrf_exempt
class AttendanceAPIView(APIView):

    def get(self, request):
        attendance = Attendance.objects.all()
        serializer = AttendanceSerializer(attendance, many=True)
        return Response(serializer.data)

    def put(self, request):
        #attendance = self.get_object(id)
        serializer = AttendanceSerializer(data = request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
            
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
# ESP-32 version of serializer views
class ESP32ViewSet(viewsets.ViewSet):
    # Add dates and times like in serializers.py
    serializer_class = ESP32Serializer
    def list(self, request):
        attendance = Attendance.objects.all()
        serializer = ESP32Serializer(attendance, many=True)
        return Response(serializer.data)
    def create(self, request):
        #data = request.data
        temp = Course.objects.get(id='EEE100HYZ')
        #if data['course'] == Course.objects.get(id='CoE198MAB1'):
        #    data['course'] = Course.objects.get(id='EEE100HYZ')
        serializer = ESP32Serializer(data = request.data)
        if serializer.is_valid():
            serializer.validated_data['course'] = temp
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
            
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
# @csrf_exempt
# class ESP32APIView(APIView):
#     #Try this: https://stackoverflow.com/questions/60635118/drf-how-to-change-the-value-of-the-model-fields-before-saving-to-the-database
#     def get(self, request):
#         attendance = Attendance.objects.all()
#         serializer = ESP32Serializer(attendance, many=True)
#         return Response(serializer.data)

#     def put(self, request):
#         #attendance = self.get_object(id)
#         data = request.data
#         if data['course'] == Course.objects.get(id='CoE198MAB1'):
#             data['course'] = Course.objects.get(id='EEE100HYZ')
#         serializer = ESP32Serializer(data = request.data)
#         if serializer.is_valid():
#             serializer.save()
#             return Response(serializer.data, status=status.HTTP_201_CREATED)
            
#         return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


        # space

@csrf_exempt
def attendance_list(request):

    if request.method == 'GET':
        attendances = Attendance.objects.all()
        serializer = AttendanceSerializer(attendances, many=True)
        return JsonResponse(serializer.data, safe=False)

    elif request.method == 'POST':
        data = JSONParser().parse(request)
        serializer = AttendanceSerializer(data = data)

        if serializer.is_valid():
            serializer.save()
            return JsonResponse(serializer.data, status=201) 

        return JsonResponse(serializer.errors, status=400)

# @api_view(['GET', 'POST'])
# @csrf_exempt
# def attendance_list(request):

#     if request.method == 'GET':
#         attendances = Attendance.objects.all()
#         serializer = AttendanceSerializer(attendances, many=True)
#         return Response(serializer.data)

#     elif request.method == 'POST':
#         #data = JSONParser().parse(request)
#         serializer = AttendanceSerializer(data = request.data)

#         if serializer.is_valid():
#             serializer.save()
#             return Response(serializer.data, status=status.HTTP_201_CREATED) 

#         return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


@login_required
def index(request):
    #superusers = User.objects.filter(is_superuser=True)
    if request.user.is_teacher:
        return render(request, 'info/t_homepage.html')
    if request.user.is_student:
        return render(request, 'info/homepage.html')
    if request.user.is_superuser:
        return render(request, 'info/superuser_homepage.html')
    return render(request, 'info/logout.html')


@login_required()
def attendance(request, stud_id):
    stud = Student.objects.get(USN=stud_id)
    ass_list = Assign.objects.filter(class_id_id=stud.class_id)
    att_list = []
    for ass in ass_list:
        try:
            a = AttendanceTotal.objects.get(student=stud, course=ass.course)
        except AttendanceTotal.DoesNotExist:
            a = AttendanceTotal(student=stud, course=ass.course)
            a.save()
        att_list.append(a)
    return render(request, 'info/attendance.html', {'att_list': att_list})


@login_required()
def attendance_detail(request, stud_id, course_id):
    stud = get_object_or_404(Student, USN=stud_id)
    cr = get_object_or_404(Course, id=course_id)
    att_list = Attendance.objects.filter(course=cr, student=stud).order_by('date')
    return render(request, 'info/att_detail.html', {'att_list': att_list, 'cr': cr})

# Teacher Views

@login_required
def t_clas(request, teacher_id, choice):
    teacher1 = get_object_or_404(Teacher, id=teacher_id)
    return render(request, 'info/t_clas.html', {'teacher1': teacher1, 'choice': choice})


@login_required()
def t_student(request, assign_id):
    ass = Assign.objects.get(id=assign_id)
    att_list = []
    for stud in ass.class_id.student_set.all():
        try:
            a = AttendanceTotal.objects.get(student=stud, course=ass.course)
        except AttendanceTotal.DoesNotExist:
            a = AttendanceTotal(student=stud, course=ass.course)
            a.save()
        att_list.append(a)
    return render(request, 'info/t_students.html', {'att_list': att_list})


@login_required()
def t_class_date(request, assign_id):
    now = timezone.now()
    ass = get_object_or_404(Assign, id=assign_id)
    att_list = ass.attendanceclass_set.filter(date__lte=now).order_by('-date')
    return render(request, 'info/t_class_date.html', {'att_list': att_list})


@login_required()
def cancel_class(request, ass_c_id):
    assc = get_object_or_404(AttendanceClass, id=ass_c_id)
    assc.status = 2
    assc.save()
    return HttpResponseRedirect(reverse('t_class_date', args=(assc.assign_id,)))


@login_required()
def t_attendance(request, ass_c_id):
    assc = get_object_or_404(AttendanceClass, id=ass_c_id)
    ass = assc.assign
    c = ass.class_id
    context = {
        'ass': ass,
        'c': c,
        'assc': assc,
    }
    return render(request, 'info/t_attendance.html', context)


@login_required()
def edit_att(request, ass_c_id):
    assc = get_object_or_404(AttendanceClass, id=ass_c_id)
    cr = assc.assign.course
    att_list = Attendance.objects.filter(attendanceclass=assc, course=cr)
    context = {
        'assc': assc,
        'att_list': att_list,
    }
    return render(request, 'info/t_edit_att.html', context)


@login_required()
def confirm(request, ass_c_id):
    assc = get_object_or_404(AttendanceClass, id=ass_c_id)
    ass = assc.assign
    cr = ass.course
    cl = ass.class_id
    for i, s in enumerate(cl.student_set.all()):
        status = request.POST[s.USN]
        if status == 'present':
            status = 'True'
        else:
            status = 'False'
        if assc.status == 1:
            try:
                a = Attendance.objects.get(course=cr, student=s, date=assc.date, attendanceclass=assc)
                a.status = status
                a.save()
            except Attendance.DoesNotExist:
                a = Attendance(course=cr, student=s, status=status, date=assc.date, attendanceclass=assc)
                a.save()
        else:
            a = Attendance(course=cr, student=s, status=status, date=assc.date, attendanceclass=assc)
            a.save()
            assc.status = 1
            assc.save()

    return HttpResponseRedirect(reverse('t_class_date', args=(ass.id,)))


@login_required()
def t_attendance_detail(request, stud_id, course_id):
    stud = get_object_or_404(Student, USN=stud_id)
    cr = get_object_or_404(Course, id=course_id)
    att_list = Attendance.objects.filter(course=cr, student=stud).order_by('date')
    return render(request, 'info/t_att_detail.html', {'att_list': att_list, 'cr': cr})


@login_required()
def change_att(request, att_id):
    a = get_object_or_404(Attendance, id=att_id)
    a.status = not a.status
    a.save()
    return HttpResponseRedirect(reverse('t_attendance_detail', args=(a.student.USN, a.course_id)))


@login_required()
def t_extra_class(request, assign_id):
    ass = get_object_or_404(Assign, id=assign_id)
    c = ass.class_id
    context = {
        'ass': ass,
        'c': c,
    }
    return render(request, 'info/t_extra_class.html', context)


@login_required()
def e_confirm(request, assign_id):
    ass = get_object_or_404(Assign, id=assign_id)
    cr = ass.course
    cl = ass.class_id
    assc = ass.attendanceclass_set.create(status=1, date=request.POST['date'])
    assc.save()

    for i, s in enumerate(cl.student_set.all()):
        status = request.POST[s.USN]
        if status == 'present':
            status = 'True'
        else:
            status = 'False'
        date = request.POST['date']
        a = Attendance(course=cr, student=s, status=status, date=date, attendanceclass=assc)
        a.save()

    return HttpResponseRedirect(reverse('t_clas', args=(ass.teacher_id, 1)))


@login_required()
def t_report(request, assign_id):
    ass = get_object_or_404(Assign, id=assign_id)
    sc_list = []
    for stud in ass.class_id.student_set.all():
        a = StudentCourse.objects.get(student=stud, course=ass.course)
        sc_list.append(a)
    return render(request, 'info/t_report.html', {'sc_list': sc_list})


@login_required()
def timetable(request, class_id):
    asst = AssignTime.objects.filter(assign__class_id=class_id)
    matrix = [['' for i in range(12)] for j in range(6)]

    for i, d in enumerate(DAYS_OF_WEEK):
        t = 0
        for j in range(12):
            if j == 0:
                matrix[i][0] = d[0]
                continue
            if j == 4 or j == 8:
                continue
            try:
                a = asst.get(period=time_slots[t][0], day=d[0])
                matrix[i][j] = a.assign.course_id#a.assign.course_id
            except AssignTime.DoesNotExist:
                pass
            t += 1

    context = {'matrix': matrix}
    return render(request, 'info/timetable.html', context)


@login_required()
def t_timetable(request, teacher_id):
    asst = AssignTime.objects.filter(assign__teacher_id=teacher_id)
    class_matrix = [[True for i in range(12)] for j in range(6)]
    for i, d in enumerate(DAYS_OF_WEEK):
        t = 0
        for j in range(12):
            if j == 0:
                class_matrix[i][0] = d[0]
                continue
            if j == 4 or j == 8:
                continue
            try:
                a = asst.get(period=time_slots[t][0], day=d[0])
                class_matrix[i][j] = a
            except AssignTime.DoesNotExist:
                pass
            t += 1

    context = {
        'class_matrix': class_matrix,
    }
    return render(request, 'info/t_timetable.html', context)


@login_required()
def free_teachers(request, asst_id):
    asst = get_object_or_404(AssignTime, id=asst_id)
    ft_list = []
    t_list = Teacher.objects.filter(assign__class_id__id=asst.assign.class_id_id)
    for t in t_list:
        at_list = AssignTime.objects.filter(assign__teacher=t)
        if not any([True if at.period == asst.period and at.day == asst.day else False for at in at_list]):
            ft_list.append(t)

    return render(request, 'info/free_teachers.html', {'ft_list': ft_list})

@login_required()
def superuser_attendance(request):
    #stud = Student.objects.get(USN=stud_id)
    #all_classes = Assign.objects.filter(class_id_id=stud.class_id)

    all_classes = AssignTime.objects.filter(assign__class_id__id='ClassTiglao01')

    #all_classes = get_object_or_404(AttendanceClass, id='01')
    assc = all_classes#.assign
    temp = []
    now = timezone.now()

    att_list = []
    for a in all_classes:
        temp.append(a)
        att_list.append(a.assign.attendanceclass_set)
    c = assc
    context = {
        'all_classes': att_list,
        'c': c,
        'temp': temp,
    }   
    # now = timezone.now()
    # ass = get_object_or_404(Assign, id=assign_id)
    # att_list = ass.attendanceclass_set.filter(date__lte=now).order_by('-date')
    # return render(request, 'info/t_class_date.html', {'att_list': att_list})
    return render(request, "info/superuser_attendance.html", context)


# def
#     stud = Student.objects.get(USN=stud_id)
#     ass_list = Assign.objects.filter(class_id_id=stud.class_id)
#     att_list = []
#     for ass in ass_list:
#         try:
#             a = AttendanceTotal.objects.get(student=stud, course=ass.course)
#         except AttendanceTotal.DoesNotExist:
#             a = AttendanceTotal(student=stud, course=ass.course)
#             a.save()
#         att_list.append(a)
#     return render(request, 'info/attendance.html', {'att_list': att_list})


# def t_attendance(request, ass_c_id):
#     assc = get_object_or_404(AttendanceClass, id=ass_c_id)
#     ass = assc.assign
#     c = ass.class_id
#     context = {
#         'ass': ass,
#         'c': c,
#         'assc': assc,
#     }
#     return render(request, 'info/t_attendance.html', context)