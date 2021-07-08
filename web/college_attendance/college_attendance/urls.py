"""college_attendance URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/3.1/topics/http/urls/
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
from django.urls import path, include
from django.contrib.auth import views as auth_views
from fromESP.views import *

urlpatterns = [
    path('admin/', admin.site.urls),
    path('', include('info.urls')),
    path('info/', include('info.urls')),
    path('accounts/login/', auth_views.LoginView.as_view(template_name='info/login.html'), name='login'),
    path('accounts/logout/', auth_views.LogoutView.as_view(template_name='info/logout.html'), name='logout'),
    path('esp/', fromESPview, name='esp'), # esp URL was mostly used for testing
    path('espTest/', espTestView, name='espTest'), # This URL has actual logic attached to it from ESP32
    path('testResults/', testResultView, name='testResults'),
]