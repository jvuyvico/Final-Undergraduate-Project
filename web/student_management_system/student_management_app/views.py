from django.shortcuts import render
from django.http import HttpResponseRedirect, HttpResponse
from student_management_app.EmailBackEnd import EmailBackEnd
from django.contrib.auth import authenticate, login, logout
from django.contrib import messages

# Create your views here.
def showDemoPage(request):
	return render(request, "demo.html")

def showLoginPage(request):
	return render(request, "login_page.html")

def doLogin(request):
	if request.method != "POST":
		return HttpResponse("<h2>Method not allowed!</h2>")
	else:
		user = EmailBackEnd.authenticate(request, username=request.POST.get("email"), password=request.POST.get("password"))
		if user != None:
			login(request, user)
			if user.user_type == '1':

				return HttpResponseRedirect('/admin_home')
			elif user.user_type == '2':
				return HttpResponse("Staff login" + str(user.user_type))
			else:
				return HttpResponse("Student login " + str(user.user_type))

		else:
			messages.error(request, "Invalid login Details")
			return HttpResponseRedirect("/")

def GetUserDetails(request):
	if request.user != None:
		return HttpResponse("User: " + request.user.email+" usertype: " + request.user.user_type)
	else:
		return HttpResponse("Please login first")

def logout_user(request):
	logout(request)
	return HttpResponseRedirect("/")
