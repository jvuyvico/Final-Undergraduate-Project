from django.shortcuts import render
from django.http import HttpResponse
from django.contrib import messages
from student_management_app.models import CustomUser, Staffs, Courses, Subjects
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
			return HttpResponseRedirect("/add_staff")
		except:
			messages.error(request, "Failed to add staff")
			return HttpResponseRedirect("/add_staff")


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
			return HttpResponseRedirect("/add_course")
		except: 
			messages.error(request, "Failed to add course!")
			return HttpResponseRedirect("/add_course")

def add_student(request):
	courses = Courses.objects.all()
	return render(request, "hod_template/add_student_template.html", {"courses":courses})	


def add_student_save(request):
	if request.method!= "POST":
		return HttpResponse("Method not allowed!")
	else:
		first_name = request.POST.get("first_name")
		last_name = request.POST.get("last_name")
		username = request.POST.get("username")
		email = request.POST.get("email")
		password = request.POST.get("password")
		address = request.POST.get("address")
		session_start = request.POST.get("session_start")
		session_end = request.POST.get("session_end")
		course_id = request.POST.get("course")
		try:
			user = CustomUser.objects.create_user(username=username, password=password, email=email, first_name=first_name, last_name=last_name, user_type=2)
			user.students.address = address
			course_obj = Courses.objects.get(id=course_id)
			user.students.course_id = course_obj
			user.students.sessions_start_year = session_start
			user.students.sessions_end_year = session_end
			user.students.profile_pic = ""
			user.save()
			messages.success(request, "Successfully added student")
			return HttpResponseRedirect("/add_student")
		except:
			messages.error(request, "Failed to add student")
			return HttpResponseRedirect("/add_student")
