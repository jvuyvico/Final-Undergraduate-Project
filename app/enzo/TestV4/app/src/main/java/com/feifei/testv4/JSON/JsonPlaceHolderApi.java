package com.feifei.testv4.JSON;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


//Dito ilalagay main functions of REST like GET, POST, PUT, PATCH, DELETE etc. Dito magccall yung main page mo if
// you want to do stuff like that

public interface JsonPlaceHolderApi {


    //Yung mga may quotations inside GET,POST, etc ay endpoints kung san mo sila gusto kunin
    @GET("student/attendance/")
    Call<List<REST_Post>> getPostsAttendance();


    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json;charset=utf-8",
            "Cache-Control: no-cache"
    })
    @POST("student/attendance/")
    Call<REST_Post> createPostAttendance(@Body REST_Post RESTPost);

    @PUT("student/attendance/{id}/")
    Call<REST_Post> putPostAttendance(@Path("id") int id, @Body REST_Post RESTPost);

    @GET("student/student/")
    Call<List<REST_Student>> getStudentProfile();

    @GET("student/assign/")
    Call<List<REST_Assign>> getStudentAssign();

    @GET("student/assigntime/")
    Call<List<REST_AssignTime>> getStudentAssignTime();

    @GET("student/course/")
    Call<List<REST_Course>> getStudentCourse();

    @GET("student/coursemapping/")
    Call<List<REST_CourseMapping>> getStudentMapping();


/*
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
 */

}

