from django.shortcuts import render
from django.http import HttpResponse
from django.core.files.storage import FileSystemStorage
from django.contrib import messages
from django.urls import reverse
from student_management_app.forms import AddStudentForm
from student_management_app.models import CustomUser, Staffs, Courses, Subjects, Students
from django.http import HttpResponseRedirect, HttpResponse




def admin_home(request):
	return render(request, "hod_template/home_content.html")

def add_staff(request):
	return render(request, "hod_template/add_staff_template.html")

def add_staff_save(request):
	if request.method != "POST":
		return HttpResponse("Method not allowed!")
	else:
		first_name = request.POST.get("first_name")
		last_name = request.POST.get("last_name")
		username = request.POST.get("username")
		email = request.POST.get("email")
		password = request.POST.get("password")
		address = request.POST.get("address")
		try:
			user = CustomUser.objects.create_user(username=username, password=password, email=email, first_name=first_name, last_name=last_name, user_type=2)
			user.staffs.address = address
			user.save()
			messages.success(request, "Successfully added staff")
			return HttpResponseRedirect(reverse("add_staff") )
		except:
			messages.error(request, "Failed to add staff")
			return HttpResponseRedirect(reverse("add_staff") )


def add_course(request):
	return render(request, "hod_template/add_course_template.html")

def add_course_save(request):
	if request.method != "POST":
		return HttpResponse("Method not allowed!")
	else:
		course = request.POST.get("course")
		try:
			course_model = Courses(course_name=course)
			course_model.save()
			messages.success(request, "Successfully added course!")
			return HttpResponseRedirect(reverse("add_course") )
		except: 
			messages.error(request, "Failed to add course!")
			return HttpResponseRedirect(reverse("add_course") )

def add_student(request):
	courses = Courses.objects.all()
	form = AddStudentForm()
	return render(request, "hod_template/add_student_template.html", {"courses":courses})	


def add_student_save(request):
    if request.method!="POST":
        return HttpResponse("Method Not Allowed")
    else:
        first_name=request.POST.get("first_name")
        last_name=request.POST.get("last_name")
        username=request.POST.get("username")
        email=request.POST.get("email")
        password=request.POST.get("password")
        session_start=request.POST.get("session_start")
        session_end=request.POST.get("session_end")
        course_id=request.POST.get("course")

        try:
            user=CustomUser.objects.create_user(username=username,password=password,email=email,last_name=last_name,first_name=first_name,user_type=3)
            course_obj=Courses.objects.get(id=course_id)
            user.students.course_id=course_obj
            user.students.session_start_year=session_start
            user.students.session_end_year=session_end
            user.save()
            messages.success(request,"Successfully Added Student")
            return HttpResponseRedirect("/add_student")
        except:
            messages.error(request,"Failed to Add Student")
            return HttpResponseRedirect(reverse("add_student"))



def add_subject(request):
    courses=Courses.objects.all()
    staffs=CustomUser.objects.filter(user_type=2)
    return render(request,"hod_template/add_subject_template.html",{"staffs":staffs,"courses":courses})

def add_subject_save(request):
    if request.method!="POST":
        return HttpResponse("<h2>Method Not Allowed</h2>")
    else:
        subject_name=request.POST.get("subject_name")
        course_id=request.POST.get("course")
        course=Courses.objects.get(id=course_id)
        staff_id=request.POST.get("staff")
        staff=CustomUser.objects.get(id=staff_id)

        try:
            subject=Subjects(subject_name=subject_name,course_id=course,staff_id=staff)
            subject.save()
            messages.success(request,"Successfully Added Subject")
            return HttpResponseRedirect(reverse("add_subject"))
        except:
            messages.error(request,"Failed to Add Subject")
            return HttpResponseRedirect(reverse("add_subject"))

def manage_staff(request):
    staffs=Staffs.objects.all()
    return render(request,"hod_template/manage_staff_template.html",{"staffs":staffs})

def manage_student(request):
    students=Students.objects.all()
    return render(request,"hod_template/manage_student_template.html",{"students":students})

def manage_course(request):
    courses=Courses.objects.all()
    return render(request,"hod_template/manage_course_template.html",{"courses":courses})

def manage_subject(request):
    subjects=Subjects.objects.all()
    return render(request,"hod_template/manage_subject_template.html",{"subjects":subjects})

def edit_staff(request,staff_id):
    staff=Staffs.objects.get(admin=staff_id)
    return render(request,"hod_template/edit_staff_template.html",{"staff":staff,"id":staff_id})

