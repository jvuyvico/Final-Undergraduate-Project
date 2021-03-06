from django.db import models
import math
from django.core.validators import MinValueValidator, MaxValueValidator
from django.contrib.auth.models import AbstractUser
from django.db.models.signals import post_save, post_delete
from datetime import timedelta
from django.core.validators import MinLengthValidator, MaxLengthValidator

from django.utils.timezone import now
import datetime

# Left as default for now, easy enough to change. Note that string formatting of time is relevant
# to how data is processed in views.py when checking schedule stored in database
time_slots = (
    ('7:30 - 8:30', '7:30 - 8:30'),
    ('8:30 - 9:30', '8:30 - 9:30'),
    ('9:30 - 10:30', '9:30 - 10:30'),
    ('11:00 - 11:50', '11:00 - 11:50'),
    ('11:50 - 12:40', '11:50 - 12:40'),
    ('12:40 - 1:30', '12:40 - 1:30'),
    ('2:30 - 3:30', '2:30 - 3:30'),
    ('3:30 - 4:30', '3:30 - 4:30'),
    ('4:30 - 5:30', '4:30 - 5:30'),
)

DAYS_OF_WEEK = (
    ('Monday', 'Monday'),
    ('Tuesday', 'Tuesday'),
    ('Wednesday', 'Wednesday'),
    ('Thursday', 'Thursday'),
    ('Friday', 'Friday'),
    ('Saturday', 'Saturday'),
)
class User(AbstractUser):
    @property
    def is_student(self):
        if hasattr(self, 'student'):
            return True
        return False

    @property
    def is_teacher(self):
        if hasattr(self, 'teacher'):
            return True
        return False

        

# EEEI
class Dept(models.Model):
    id = models.CharField(primary_key='True', max_length=100)
    name = models.CharField(max_length=200)

    def __str__(self):
        return self.name

# ex. CoE 198 MAB1
class Course(models.Model):
    dept = models.ForeignKey(Dept, on_delete=models.CASCADE)
    id = models.CharField(primary_key='True', max_length=50)
    name = models.CharField(max_length=50)
    shortname = models.CharField(max_length=50, default='X')

    def __str__(self):
        return self.name
# Map a Course to a building id and room id for the ESP32 beacon purposes
class Course_Mapping(models.Model):
    course = models.OneToOneField(Course, on_delete=models.CASCADE)
    room_id = models.PositiveSmallIntegerField()
    building_id = models.PositiveSmallIntegerField()

class Class(models.Model):
    # courses = models.ManyToManyField(Course, default=1)
    id = models.CharField(primary_key='True', max_length=100)
    dept = models.ForeignKey(Dept, on_delete=models.CASCADE)
    section = models.CharField(max_length=100)
    sem = models.IntegerField()

    class Meta:
        verbose_name_plural = 'classes'

    def __str__(self):
        d = Dept.objects.get(name=self.dept)
        return '%s : %d %s' % (d.name, self.sem, self.section)


class Student(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE, null=True)
    class_id = models.ForeignKey(Class, on_delete=models.CASCADE, default=1)
    # 9 number student number = USN
    USN = models.CharField(primary_key='True', max_length=9, validators=[MinLengthValidator(9), MaxLengthValidator(9)])
    name = models.CharField(max_length=200)
    # DOB not really necessary
    DOB = models.DateField(default='1998-01-01')
    #student_id = models.CharField(max_length = 9, unique=True,validators=[MinLengthValidator(9), MaxLengthValidator(9)] )

    def __str__(self):
        return self.name


class Teacher(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE, null=True)
    id = models.CharField(primary_key=True, max_length=100)
    dept = models.ForeignKey(Dept, on_delete=models.CASCADE, default=1)
    name = models.CharField(max_length=100)
    DOB = models.DateField(default='1980-01-01')

    def __str__(self):
        return self.name

# Maps a course and teacher to a Class
# Assign is treated like a "Block"
class Assign(models.Model):
    class_id = models.ForeignKey(Class, on_delete=models.CASCADE)
    course = models.ForeignKey(Course, on_delete=models.CASCADE)
    teacher = models.ForeignKey(Teacher, on_delete=models.CASCADE)

    class Meta:
        unique_together = (('course', 'class_id', 'teacher'),)

    def __str__(self):
        cl = Class.objects.get(id=self.class_id_id)
        cr = Course.objects.get(id=self.course_id)
        te = Teacher.objects.get(id=self.teacher_id)
        return '%s : %s : %s' % (te.name, cr.shortname, cl)


class AssignTime(models.Model):
    assign = models.ForeignKey(Assign, on_delete=models.CASCADE)
    period = models.CharField(max_length=50, choices=time_slots, default='11:00 - 11:50')
    day = models.CharField(max_length=15, choices=DAYS_OF_WEEK)

# Not really necessary for our purposes due to how the phone sends information
# Each can hold multiple Attendance objects.
class AttendanceClass(models.Model):
    assign = models.ForeignKey(Assign, on_delete=models.CASCADE)
    date = models.DateField()
    status = models.IntegerField(default=0)

    class Meta:
        verbose_name = 'Attendance'
        verbose_name_plural = 'Attendance'


class Attendance(models.Model):
    course = models.ForeignKey(Course, on_delete=models.CASCADE)
    student = models.ForeignKey(Student, on_delete=models.CASCADE)
    attendanceclass = models.ForeignKey(AttendanceClass, on_delete=models.CASCADE, default=1)
    date = models.DateField(default=datetime.date.today)
    status = models.BooleanField(default='True')

    def __str__(self):
        sname = Student.objects.get(name=self.student)
        cname = Course.objects.get(name=self.course)
        return '%s : %s' % (sname.name, cname.shortname)


class AttendanceTotal(models.Model):
    course = models.ForeignKey(Course, on_delete=models.CASCADE)
    student = models.ForeignKey(Student, on_delete=models.CASCADE)

    class Meta:
        unique_together = (('student', 'course'),)

    @property
    def att_class(self):
        stud = Student.objects.get(name=self.student)
        cr = Course.objects.get(name=self.course)
        att_class = Attendance.objects.filter(course=cr, student=stud, status='True').count()
        return att_class

    @property
    def total_class(self):
        stud = Student.objects.get(name=self.student)
        cr = Course.objects.get(name=self.course)
        total_class = Attendance.objects.filter(course=cr, student=stud).count()
        return total_class

    @property
    def attendance(self):
        stud = Student.objects.get(name=self.student)
        cr = Course.objects.get(name=self.course)
        total_class = Attendance.objects.filter(course=cr, student=stud).count()
        att_class = Attendance.objects.filter(course=cr, student=stud, status='True').count()
        if total_class == 0:
            attendance = 0
        else:
            attendance = round(att_class / total_class * 100, 2)
        return attendance

    @property
    def classes_to_attend(self):
        stud = Student.objects.get(name=self.student)
        cr = Course.objects.get(name=self.course)
        total_class = Attendance.objects.filter(course=cr, student=stud).count()
        att_class = Attendance.objects.filter(course=cr, student=stud, status='True').count()
        cta = math.ceil((0.75 * total_class - att_class) / 0.25)
        if cta < 0:
            return 0
        return cta

# Also needed for proper viewing on Teacher profiles
class StudentCourse(models.Model):
    student = models.ForeignKey(Student, on_delete=models.CASCADE)
    course = models.ForeignKey(Course, on_delete=models.CASCADE)

    class Meta:
        unique_together = (('student', 'course'),)
        #verbose_name_plural = 'Marks'

    def __str__(self):
        sname = Student.objects.get(name=self.student)
        cname = Course.objects.get(name=self.course)
        return '%s : %s' % (sname.name, cname.shortname)



    def get_attendance(self):
        a = AttendanceTotal.objects.get(student=self.student, course=self.course)
        return a.attendance


class AttendanceRange(models.Model):
    start_date = models.DateField()
    end_date = models.DateField()


# Triggers


def daterange(start_date, end_date):
    for n in range(int((end_date - start_date).days)):
        yield start_date + timedelta(n)


days = {
    'Monday': 1,
    'Tuesday': 2,
    'Wednesday': 3,
    'Thursday': 4,
    'Friday': 5,
    'Saturday': 6,
}


def create_attendance(sender, instance, **kwargs):
    if kwargs['created']:
        start_date = AttendanceRange.objects.all()[:1].get().start_date
        end_date = AttendanceRange.objects.all()[:1].get().end_date
        for single_date in daterange(start_date, end_date):
            if single_date.isoweekday() == days[instance.day]:
                try:
                    AttendanceClass.objects.get(date=single_date.strftime("%Y-%m-%d"), assign=instance.assign)
                except AttendanceClass.DoesNotExist:
                    a = AttendanceClass(date=single_date.strftime("%Y-%m-%d"), assign=instance.assign)
                    a.save()



post_save.connect(create_attendance, sender=AssignTime)

# ESP32 stuff

class espData(models.Model):
    dayStamp = models.DateField(auto_now=False, auto_now_add=False)
    timeStamp = models.TimeField(auto_now=False, auto_now_add=False)
    bid = models.PositiveSmallIntegerField()    
    rid = models.PositiveSmallIntegerField()
    numID = models.PositiveIntegerField()
    rssi = models.SmallIntegerField()
    course = models.CharField(default="None", max_length=100)

class espDataDaily(models.Model):
    numID = models.PositiveIntegerField()
    course = models.CharField(default="None", max_length=100)
    pings = models.PositiveIntegerField()
    dayStamp = models.DateField(auto_now=False, auto_now_add=False, default="2020-05-10")

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
