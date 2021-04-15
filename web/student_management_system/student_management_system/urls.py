"""student_management_system URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/2.2/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path
from django.conf.urls.static import static

from student_management_app import views
from student_management_system import settings
from student_management_app import HodViews, StaffViews, StudentViews


urlpatterns = [
	path('demo/', views.showDemoPage),
	path('doLogin', views.doLogin, name='do_login'),
	path('', views.showLoginPage, name='show_login'),
    path('admin/', admin.site.urls),
    path('get_user_details', views.GetUserDetails),
    path('logout_user', views.logout_user, name='logout'),
    path('admin_home',HodViews.admin_home, name="admin_home"),
    path('add_staff', HodViews.add_staff, name="add_staff"),
    path('add_staff_save', HodViews.add_staff_save, name='add_staff_save'),
    path('add_course', HodViews.add_course, name="add_course"),
    path('add_course_save', HodViews.add_course_save, name='add_course_save'),
    path('add_student', HodViews.add_student, name="add_student"),
    path('add_student_save', HodViews.add_student_save, name='add_student_save'),
    path('add_subject', HodViews.add_subject,name="add_subject"),
    path('add_subject_save', HodViews.add_subject_save,name="add_subject_save"),
    path('manage_staff', HodViews.manage_staff,name="manage_staff"),
    path('manage_student', HodViews.manage_student,name="manage_student"),
    path('manage_course', HodViews.manage_course,name="manage_course"),
    path('manage_subject', HodViews.manage_subject,name="manage_subject"),

    path("staff_home", StaffViews.staff_home, name='staff_home'),
    path("student_home", StudentViews.student_home, name='student_home'),
]+static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)+static(settings.STATIC_URL, document_root=settings.STATIC_ROOT)