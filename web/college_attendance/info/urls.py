from django.urls import path, include
from . import views
#from .views import AttendanceAPI

from django.contrib.auth import views as auth_views
from rest_framework.routers import DefaultRouter

router = DefaultRouter()
router.register('attendance', views.AttendanceViewSet, basename='attendance')

router1 = DefaultRouter()
router1.register('attendance1', views.ESP32ViewSet, basename='attendance1')

router2 = DefaultRouter()
router2.register('student', views.StudentViewSet, basename='student')

router3 = DefaultRouter()
router3.register('assign', views.StudentAssignViewSet, basename='assign')

router4 = DefaultRouter()
router4.register('assigntime', views.StudentAssignTimeViewSet, basename='assigntime')

router5 = DefaultRouter()
router5.register('course', views.StudentCourseViewSet, basename='course')

router6 = DefaultRouter()
router6.register('coursemapping', views.StudentCourseMappingViewSet, basename='coursemaping')

urlpatterns = [
    path('', views.index, name='index'),
    path('student/<slug:stud_id>/attendance/', views.attendance, name='attendance'),
    path('student/<slug:stud_id>/<slug:course_id>/attendance/', views.attendance_detail, name='attendance_detail'),
    path('student/<slug:class_id>/timetable/', views.timetable, name='timetable'),
    path('teacher/<slug:teacher_id>/<int:choice>/Classes/', views.t_clas, name='t_clas'),
    path('teacher/<int:assign_id>/Students/attendance/', views.t_student, name='t_student'),
    path('teacher/<int:assign_id>/ClassDates/', views.t_class_date, name='t_class_date'),
    path('teacher/<int:ass_c_id>/Cancel/', views.cancel_class, name='cancel_class'),
    path('teacher/<int:ass_c_id>/attendance/', views.t_attendance, name='t_attendance'),
    path('teacher/<int:ass_c_id>/Edit_att/', views.edit_att, name='edit_att'),
    path('teacher/<int:ass_c_id>/attendance/confirm/', views.confirm, name='confirm'),
    path('teacher/<slug:stud_id>/<slug:course_id>/attendance/', views.t_attendance_detail, name='t_attendance_detail'),
    path('teacher/<int:att_id>/change_attendance/', views.change_att, name='change_att'),
    path('teacher/<int:assign_id>/Extra_class/', views.t_extra_class, name='t_extra_class'),
    path('teacher/<slug:assign_id>/Extra_class/confirm/', views.e_confirm, name='e_confirm'),
    path('teacher/<int:assign_id>/Report/', views.t_report, name='t_report'),

    path('teacher/<slug:teacher_id>/t_timetable/', views.t_timetable, name='t_timetable'),
    path('teacher/<int:asst_id>/Free_teachers/', views.free_teachers, name='free_teachers'),

    path('superuser/superuser_attendance', views.superuser_attendance, name='superuser_attendance'),

    #path('test', views.attendance_list),
    #path('attendance', views.AttendanceAPIView.as_view()),
    path('student/', include(router.urls)), # So link becomes test/attendance
    path('student/<int:pk>', include(router.urls)),
    path('student/', include(router2.urls)),
    path('student/', include(router3.urls)),
    path('student/', include(router4.urls)),
    path('student/', include(router5.urls)),
    path('student/', include(router6.urls)),

    path('esp32', include(router1.urls)),
    path('espTest', views.fromESPview) # Receives info from ESP32 pings
    ]