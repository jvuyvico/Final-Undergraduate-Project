package com.feifei.testv4.ActivityPages;

/*
    Activity to set the credentials of a user in a single instance offline database using shared preferences
    Activity only accessible after completing AdminLoginActivity
 */

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.feifei.testv4.Classes.CourseSection;
import com.feifei.testv4.Classes.CourseTime;
import com.feifei.testv4.SQLite.DatabaseAccess;
import com.feifei.testv4.JSON.JsonPlaceHolderApi;
import com.feifei.testv4.JSON.REST_Assign;
import com.feifei.testv4.JSON.REST_AssignTime;
import com.feifei.testv4.JSON.REST_Course;
import com.feifei.testv4.JSON.REST_CourseMapping;
import com.feifei.testv4.JSON.REST_Student;
import com.feifei.testv4.R;
import com.feifei.testv4.Classes.User_SubjectExtended;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class SetCredentialsActivity extends AppCompatActivity {

    EditText inputUsername;
    EditText inputStudentnumber;
    Button backButton;
    Button submitButton;

    ArrayList<REST_Assign> assignArrayList;
    ArrayList<REST_AssignTime> assignTimeArrayList;
    ArrayList<REST_Course> courseArrayList;
    ArrayList<REST_CourseMapping> courseMappingArrayList;
    ArrayList<REST_Student> studentArrayList;

    ArrayList<REST_Assign> assignArrayListfinal;
    ArrayList<REST_AssignTime> assignTimeArrayListfinal;
    ArrayList<REST_Course> courseArrayListfinal;
    ArrayList<REST_CourseMapping> courseMappingArrayListfinal;
    ArrayList<REST_Student> studentArrayListfinal;
    ArrayList<CourseTime> coursetimeArrayListfinal;
    ArrayList<CourseTime> coursetimeArrayListdummy;
    ArrayList<CourseSection> courseSectionArrayListfinal;

    ArrayList<User_SubjectExtended> subjectArrayList;

    private JsonPlaceHolderApi jsonPlaceHolderApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_credentials);
        backButton = (Button) findViewById(R.id.backButton);
        submitButton = (Button) findViewById(R.id.button_submit);
        inputUsername = findViewById(R.id.set_username);
        inputStudentnumber = findViewById(R.id.set_studentnumber);

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        String AUTH = "Basic" + Base64.encodeToString(("enAlcantara:Alcantara01827").getBytes(), Base64.NO_WRAP);

        //This helps in debugging kapag nakikipag interact with the server
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(@NotNull Chain chain) throws IOException {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder()
                                .addHeader("Authorization", AUTH)
                                .method(original.method(), original.body());

                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                })
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        //base of the REST API. Dito mo idedefine kung saang base site ka kukuha
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.3:8000/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        assignArrayList = new ArrayList<>();
        assignTimeArrayList= new ArrayList<>();
        courseArrayList= new ArrayList<>();
        courseMappingArrayList= new ArrayList<>();
        studentArrayList= new ArrayList<>();

        studentArrayListfinal = new ArrayList<>();
        assignArrayListfinal = new ArrayList<>();
        courseArrayListfinal = new ArrayList<>();
        courseMappingArrayListfinal = new ArrayList<>();
        assignTimeArrayListfinal = new ArrayList<>();
        coursetimeArrayListfinal = new ArrayList<>();
        coursetimeArrayListdummy = new ArrayList<>();
        courseSectionArrayListfinal = new ArrayList<>();
        subjectArrayList = new ArrayList<>();

        getAssignStudent();
        getAssignTimeStudent();
        getCourseMappingStudent();
        getCourseStudent();
        getStudentStudent();

    }

    // perform error checking to see if input from text is valid format of desired credentials information
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void submitButtonClicked(View view){

        int studentinlog = 0;
        if(studentArrayList.isEmpty()){
            Toast.makeText(this, "Server is Offline", Toast.LENGTH_SHORT).show();
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);

        }
        else {
            for (REST_Student studentuserlog : studentArrayList) {
                if (studentuserlog.getUSN().equals(inputStudentnumber.getText().toString())) {
                    studentinlog = 1;
                }
            }

            if (inputUsername.getText().toString().isEmpty() || inputStudentnumber.getText().length() != 9) {    //check if username is empty or invalid studentnumber
                Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                inputUsername.getText().clear();
                inputStudentnumber.getText().clear();
            }
            if (studentinlog != 1) {
                Toast.makeText(this, "Student Number Not Registered", Toast.LENGTH_SHORT).show();
                inputUsername.getText().clear();
                inputStudentnumber.getText().clear();
            } else {
                DatabaseAccess dbAccessSCA = DatabaseAccess.getInstance(this);
                dbAccessSCA.open();
                String username = dbAccessSCA.getStudentUsername();
                if (username.contains("none")) {
                    dbAccessSCA.insertStudentData(inputUsername.getText().toString(), inputStudentnumber.getText().toString());
                } else {
                    Log.d("TAG", "submitButtonClicked: updating");
                    dbAccessSCA.updateStudentData(inputUsername.getText().toString(), inputStudentnumber.getText().toString());
                }
                String studentnumber = dbAccessSCA.getStudentNumber();
                dbAccessSCA.close();


                //=========== Algorithm to get classes and parse within local database. Medyo complicated siya
                //=========== pero ang gist is kumukuha sa database based on(1) get student profile in student table
                // (2) get assign id from student profile then get time from assigntime table and courses table from there
                // (3) get list of courses then get course mapping
                //(4) parse in local database
                for (REST_Student studentusr : studentArrayList) {
                    if (studentusr.getUSN().equals(studentnumber)) {
                        Log.d("CHECK", studentusr.getName() + studentusr.getUSN() + studentusr.getDOB());
                        studentArrayListfinal.add(studentusr);
                    }
                }

                String classidcompare = studentArrayListfinal.get(0).getClass_id();
                for (REST_Assign assignee : assignArrayList) {
                    if (assignee.getClass_id().equals(classidcompare)) {
                        assignArrayListfinal.add(assignee);
                        Log.d("CHECK", assignee.getCourse() + " " + assignee.getTeacher());

                    }
                }
                for (REST_Assign assign : assignArrayListfinal) {
                    for (REST_AssignTime assignTime : assignTimeArrayList) {
                        if (assign.getId().equals(assignTime.getAssign_id())) {
                            assignTimeArrayListfinal.add(assignTime);
                        }
                    }
                }

                for (REST_AssignTime assigntime1 : assignTimeArrayListfinal) {
                    String finalday = "";
                    Object[] startendtime = new Object[0];
                    for (REST_AssignTime assigntime2 : assignTimeArrayListfinal) {
                        if (assigntime1.getAssign_id().equals(assigntime2.getAssign_id())) {
                            String dummy = "";
                            startendtime = SplitPeriods(assigntime1.getPeriod());
                            dummy = abbrevDay(assigntime2.getDay());
                            finalday = finalday + dummy;
                        }
                    }
                    if (!finalday.isEmpty()) {
                        coursetimeArrayListdummy.add(new CourseTime(finalday, startendtime[0].toString(), startendtime[1].toString(), assigntime1.getAssign_id()));
                    }
                }

                for (CourseTime coursetimedummy : coursetimeArrayListdummy) {
                    int count = 0;
                    for (CourseTime coursetimefinal : coursetimeArrayListfinal) {
                        if (coursetimedummy.getAssign_id().equals(coursetimefinal.getAssign_id())) {
                            count++;
                        }
                    }
                    if (count == 0) {
                        coursetimeArrayListfinal.add(coursetimedummy);
                    }
                }

                for (REST_Assign assign : assignArrayListfinal) {
                    Object[] coursesectionfinal = null;
                    coursesectionfinal = SplitSection(assign.getCourse());
                    Log.d("CHECKING", coursesectionfinal[0].toString() + " " + coursesectionfinal[1].toString());
                    courseSectionArrayListfinal.add(new CourseSection(coursesectionfinal[0].toString(), coursesectionfinal[1].toString(), assign.getId()));

                }

                for (REST_Assign assign : assignArrayListfinal) {
                    for (CourseTime assignTime : coursetimeArrayListfinal) {
                        for (CourseSection coursesection : courseSectionArrayListfinal) {
                            for (REST_CourseMapping courseMapping : courseMappingArrayList) {
                                if (assign.getId().equals(assignTime.getAssign_id()) && assign.getId().equals(coursesection.getId()) && assign.getCourse().equals(courseMapping.getCourse())) {
                                    String content = "";
                                    subjectArrayList.add(new User_SubjectExtended(coursesection.getCourse(), coursesection.getSection(), assignTime.getDay(), Integer.parseInt(assignTime.getStarttime()),
                                            Integer.parseInt(assignTime.getEndtime()), "18CE4027A5B440208704EC20EBB82E60", courseMapping.getBuilding_id(), courseMapping.getRoom_id()));
                                    content = "Subject: " + coursesection.getCourse() + " Section " + coursesection.getSection() + " Days " + assignTime.getDay() +
                                            " Start time: " + assignTime.getStarttime() + " End time: " + assignTime.getEndtime() + "\n";

                                }
                            }
                        }
                    }
                }

                dbAccessSCA.open();
                dbAccessSCA.deleteAllData();
                for (User_SubjectExtended finalusersub : subjectArrayList) {
                    dbAccessSCA.insertExtendedData(finalusersub);
                }
                dbAccessSCA.close();

                Toast.makeText(this, "Credentials successfully set", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    // modify 'back' or 'return' functions to softlock until user sets their credentials

    public void backButtonClicked(View view){
        DatabaseAccess dbAccessSCA = DatabaseAccess.getInstance(this);
        dbAccessSCA.open();
        String username = dbAccessSCA.getStudentUsername();
        dbAccessSCA.close();
        if(username.contains("none")) {
            Toast.makeText(this, "Please set-up credentials", Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        DatabaseAccess dbAccessSCA = DatabaseAccess.getInstance(this);
        dbAccessSCA.open();
        String username = dbAccessSCA.getStudentUsername();
        dbAccessSCA.close();
        if(username.contains("none")) {
            Toast.makeText(this, "Please set-up credentials", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    //=============================================================functions for peripherals
    public static Object[] SplitSection (String section){ //Splits ECE198MAB1 to ECE198 MAB1

        String[] Splitted = section.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        String coursefinal = Splitted[0] + Splitted[1];
        String sectionfinal = Splitted[2];

        return new Object[] {coursefinal, sectionfinal};
    }

    //function to separate time period
    public static Object[] SplitPeriods (String timeperiod) { //Splits 7:30 - 8:30 to 730 and 830
        String[] Splitted = timeperiod.split(" ");
        String[] startTimesplit = Splitted[0].split(":");
        String[] endTimesplit = Splitted[2].split(":");

        String starttime = startTimesplit[0] + startTimesplit[1];
        String endtime = endTimesplit[0] + endTimesplit[1];

        return new Object[] {starttime, endtime};
    }

    //function to shortenday
    private String abbrevDay (String day) {
        if (day.equals("Monday")){
            return "M";
        }
        if (day.equals("Tuesday")){
            return "T";
        }
        if (day.equals("Wednesday")){
            return "W";
        }
        if (day.equals("Thursday")){
            return "H";
        }
        if (day.equals("Friday")){
            return "F";
        }
        if (day.equals("Saturday")){
            return "S";
        }

        return "Day Not Found";
    }

    //============================functions for getting data in server
    private void getAssignStudent(){
        assignArrayList = new ArrayList<>();

        Call<List<REST_Assign>> call = jsonPlaceHolderApi.getStudentAssign();
        call.enqueue(new Callback<List<REST_Assign>>() {
            @Override
            public void onResponse(Call<List<REST_Assign>> call, Response<List<REST_Assign>> response) {
                if(!response.isSuccessful()){
                    Log.e("Error", "Get Posts: Code: " + response.code() + "\n");
                    try{

                        Log.e("Error", "Get Posts: Error: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("Error", "Unkown error: " + response.errorBody());
                    }
                    return;
                }
                List<REST_Assign> RESTAssigns = response.body();

                for (REST_Assign restassign : RESTAssigns){
                    Log.d("Check", restassign.getCourse() + restassign.getTeacher());
                    assignArrayList.add(restassign);
                }
            }

            @Override
            public void onFailure(Call<List<REST_Assign>> call, Throwable t) {
                Log.e("Error onFailure", t.getMessage());
            }
        });
    }

    private void getAssignTimeStudent(){
        Call<List<REST_AssignTime>> call = jsonPlaceHolderApi.getStudentAssignTime();
        call.enqueue(new Callback<List<REST_AssignTime>>() {
            @Override
            public void onResponse(Call<List<REST_AssignTime>> call, Response<List<REST_AssignTime>> response) {
                if(!response.isSuccessful()){
                    Log.e("Error", "Get Posts: Code: " + response.code() + "\n");
                    try{
                        Log.e("Error", "Get Posts: Error: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("Error", "Unkown error: " + response.errorBody());
                    }
                    return;
                }
                List<REST_AssignTime> RESTAssignTimes = response.body();
                for (REST_AssignTime restassigntime : RESTAssignTimes){
                    assignTimeArrayList.add(restassigntime);
                    Log.d("CHECK 2", restassigntime.getAssign_id() + restassigntime.getPeriod());
                }
            }

            @Override
            public void onFailure(Call<List<REST_AssignTime>> call, Throwable t) {
                Log.e("Error onFailure", t.getMessage());
            }
        });
    }

    private void getCourseStudent(){
        Call<List<REST_Course>> call = jsonPlaceHolderApi.getStudentCourse();
        call.enqueue(new Callback<List<REST_Course>>() {
            @Override
            public void onResponse(Call<List<REST_Course>> call, Response<List<REST_Course>> response) {
                if(!response.isSuccessful()){
                    Log.e("Error", "Get Posts: Code: " + response.code() + "\n");
                    try{
                        Log.e("Error", "Get Posts: Error: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("Error", "Unkown error: " + response.errorBody());
                    }
                    return;
                }
                List<REST_Course> RESTCourses = response.body();
                for (REST_Course restcourse : RESTCourses){
                    courseArrayList.add(restcourse);

                    Log.d("CHECK 3", restcourse.getDept() + restcourse.getShortname());
                }
            }

            @Override
            public void onFailure(Call<List<REST_Course>> call, Throwable t) {
                Log.e("Error onFailure", t.getMessage());
            }
        });
    }

    private void getCourseMappingStudent(){
        Call<List<REST_CourseMapping>> call = jsonPlaceHolderApi.getStudentMapping();
        call.enqueue(new Callback<List<REST_CourseMapping>>() {
            @Override
            public void onResponse(Call<List<REST_CourseMapping>> call, Response<List<REST_CourseMapping>> response) {
                if(!response.isSuccessful()){
                    Log.e("Error", "Get Posts: Code: " + response.code() + "\n");
                    try{
                        Log.e("Error", "Get Posts: Error: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("Error", "Unkown error: " + response.errorBody());
                    }
                    return;
                }
                List<REST_CourseMapping> RESTCourseMappings = response.body();
                for (REST_CourseMapping restcoursemapping : RESTCourseMappings){
                    courseMappingArrayList.add(restcoursemapping);

                    Log.d("CHECK 4", restcoursemapping.getCourse() + restcoursemapping.getBuilding_id());
                }
            }

            @Override
            public void onFailure(Call<List<REST_CourseMapping>> call, Throwable t) {
                Log.e("Error onFailure", t.getMessage());
            }
        });
    }

    private void getStudentStudent(){
        Call<List<REST_Student>> call = jsonPlaceHolderApi.getStudentProfile();
        call.enqueue(new Callback<List<REST_Student>>() {
            @Override
            public void onResponse(Call<List<REST_Student>> call, Response<List<REST_Student>> response) {
                if(!response.isSuccessful()){
                    Log.e("Error", "Get Posts: Code: " + response.code() + "\n");
                    try{
                        Log.e("Error", "Get Posts: Error: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("Error", "Unkown error: " + response.errorBody());
                    }
                    return;
                }
                List<REST_Student> RESTStudents = response.body();
                for (REST_Student reststudent : RESTStudents){
                    studentArrayList.add(reststudent);

                    Log.d("CHECK 5", reststudent.getName() + reststudent.getUser());
                }
            }

            @Override
            public void onFailure(Call<List<REST_Student>> call, Throwable t) {
                Log.e("Error onFailure", t.getMessage());
            }
        });
    }

}