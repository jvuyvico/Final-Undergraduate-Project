package com.feifei.testv4.ActivityPages;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.feifei.testv4.AttendanceData_ListAdapter;
import com.feifei.testv4.Classes.Attendance_Data;
import com.feifei.testv4.SQLite.DBHelperAttendanceList;
import com.feifei.testv4.SQLite.DBHelperCrossCheckAttendanceList;
import com.feifei.testv4.SQLite.DatabaseAccess;
import com.feifei.testv4.JSON.JsonPlaceHolderApi;
import com.feifei.testv4.JSON.REST_Post;
import com.feifei.testv4.R;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

public class CheckAttendanceActivity extends AppCompatActivity {


    private JsonPlaceHolderApi jsonPlaceHolderApi;

    ListView lv_attendanceData;
    ArrayList<Attendance_Data> attendanceData_AL;
    AttendanceData_ListAdapter attendanceDataListAdapter;
    Button AD_refresh_button;

    DBHelperAttendanceList DB;
    DBHelperCrossCheckAttendanceList DBCross;

    ArrayList<REST_Post> restpostcompare;
    ArrayList<REST_Post> restpost;
    ArrayList<Attendance_Data> toimport;

    String studentnumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_attendance);
        AD_refresh_button = findViewById(R.id.but_CA_refresh);

        lv_attendanceData = (ListView) findViewById(R.id.lv_attendance_data);
        attendanceData_AL = new ArrayList<>();

        // load database info on the list on create
        DatabaseAccess ca_DA = DatabaseAccess.getInstance(this);
        ca_DA.open();
        attendanceData_AL = ca_DA.getAttendanceData();
        ca_DA.close();
//====================== REST stuff
        String AUTH = "Basic" + Base64.encodeToString(("enAlcantara:Alcantara01827").getBytes(), Base64.NO_WRAP);

        attendanceDataListAdapter = new AttendanceData_ListAdapter(this, R.layout.attendance_data_list_item, attendanceData_AL);
        lv_attendanceData.setAdapter(attendanceDataListAdapter);

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

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

        DB = new DBHelperAttendanceList(this);
        DBCross = new DBHelperCrossCheckAttendanceList(this);
        toimport = new ArrayList<>();

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        studentnumber = databaseAccess.getStudentNumber();
        databaseAccess.close();
        DB.deleteAll();
        importToLocal();
        CheckinOnline(studentnumber);

    }

    public void CA_refreshClicked( View view ) {
        attendanceData_AL.clear();
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        attendanceData_AL.addAll(databaseAccess.getAttendanceData());
        databaseAccess.close();
        attendanceDataListAdapter.notifyDataSetChanged();
        importToLocal();
    }

    public void SendToServer(View view) {//function that sends to server while cross checking kung ano yung hindi pa nasesend
//logic here is kinukuha muna yung data na nasa server then store in a separate arraylist
        //tas get yung local attendance data to compare with sa online
        //it will then send yung mga stuff sa local na di pa nakikita sa online

        Cursor res = DB.getdata();
        Boolean status;
        restpostcompare = new ArrayList<>();
        restpost = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();
        while(res.moveToNext()) {
            restpost.add(new REST_Post(res.getString(0), res.getString(1), res.getString(2), res.getString(3), Boolean.parseBoolean(res.getString(4))));
        }

        Cursor res2 = DBCross.getdata();
        while(res2.moveToNext()) {
            restpostcompare.add(new REST_Post(res2.getString(0), res2.getString(1), res2.getString(2), res2.getString(3), Boolean.parseBoolean(res2.getString(4))));
            Log.d("RESPOST2ADD", res2.getString(0) + res2.getString(2));
        }
        int count;
        for (REST_Post restpost1 : restpost){
            Log.d("RESPOST1" , restpost1.getCourse() + restpost1.getDate());
            count =0;
            for(REST_Post restpost2: restpostcompare){
                Log.d("RESPOST 1 and RESPOST 2", "==" + restpost2.getDate()+"==" + restpost1.getDate()+"==");
                if(Objects.equals(restpost1.getCourse(), restpost2.getCourse())){
                    Log.d("EQUAL DATE", "CONFIRMED");
                    if(Objects.equals(restpost1.getDate(), restpost2.getDate())){
                        Log.d("EQUAL COURSE", "CONFIRMED");
                        if(restpost1.getStatus().equals(restpost2.getStatus())){
                            Log.d("EQUAL STATUS", "Duplicate");
                            count++;
                        }
                    }
                }
            }
            if (count == 0){
                createPost(restpost1.getCourse(), restpost1.getStudent(), restpost1.getAttendanceclass(), restpost1.getDate(),restpost1.getStatus());
            }


        }
        Toast.makeText(this, "Update Complete", Toast.LENGTH_SHORT).show();
        finish(); //it is important that you restart the activity so it can update the information automatically on onCreate
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);

    }


    private void importToLocal(){ //this function imports attendance data from local database to another database na ginawa
        //para magsend ng data sa server
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        toimport = databaseAccess.getAttendanceData();
        databaseAccess.close();
        DB.deleteAll();
        String attendancestatus = "";
        Log.e("Check", " AMGEARE");
        for (Attendance_Data datatoimport : toimport){
            Log.e("Checkerd" , datatoimport.getSubject() + datatoimport.getDate());
            if(datatoimport.getStatus().equals("Present")){
                attendancestatus = "true";
            }
            else {
                attendancestatus = "false";
            }

            String[] date;
            date = datatoimport.getDate().split(" ");
            String datefinal = "";
            datefinal = date[0];
            String[] datesep = datefinal.split("-");
            String newday = datesep[1];
            if(Integer.valueOf(newday) <10 ){
                newday = "0" + newday;
            }
            datefinal = datesep[0] + "-" + newday + "-" + datesep[2];
            Boolean checkinsertdata  = DB.insertuserdata(datatoimport.getSubject(), studentnumber, datatoimport.getMajor(), datefinal, attendancestatus);

            if(checkinsertdata == true) {
                Log.d("ADDED", "Success");
            }
            else{
                Log.e("Not", "Successful");
            }
        }
    }


    //=====================REST Functions

    private void getPosts(String studentnum){
        Call<List<REST_Post>> call = jsonPlaceHolderApi.getPostsAttendance();
        call.enqueue(new Callback<List<REST_Post>>() {
            @Override
            public void onResponse(Call<List<REST_Post>> call, Response<List<REST_Post>> response) {


                if(!response.isSuccessful()){
                    Log.e("Error", "Get Posts: Code: " + response.code() + "\n");
                    try{
                        Log.e("Error", "Get Posts: Error: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("Error", "Unkown error: " + response.errorBody());
                    }
                    return;
                }
                List<REST_Post> RESTPosts = response.body();
                DB.deleteAll();

                int checker = 0;
                for(REST_Post RESTPost : RESTPosts){
                    if (Objects.equals(studentnum, RESTPost.getStudent())) {

                        Boolean checkinsertdata = DB.insertuserdata(RESTPost.getCourse(), RESTPost.getStudent(), RESTPost.getAttendanceclass(), RESTPost.getDate(), String.valueOf(RESTPost.getStatus()) );

                        if(checkinsertdata == true) {
                            Log.d("ADDED", "Success");
                        }
                        else{
                            Log.d("Not", "Successful");
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<List<REST_Post>> call, Throwable t) {
                Toast.makeText(CheckAttendanceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Function for creating stuff in database sa server
    private void createPost(String course, String student, String attendanceclass, String date, boolean status){

        REST_Post RESTPost = new REST_Post(course, student, attendanceclass, date, status);

        Call<REST_Post> call = jsonPlaceHolderApi.createPostAttendance(RESTPost);

        call.enqueue(new Callback<REST_Post>() {
            @Override
            public void onResponse(Call<REST_Post> call, Response<REST_Post> response) {
                if(!response.isSuccessful()){
                    Log.e("ERROR ON CREATE: ", "Get Posts: Code: " + response.code());
                    return;
                }

                REST_Post RESTPostResponse = response.body();
                String content = "";
                content += "Course: " + RESTPost.getCourse() + "\n";
                content += "Student Number: " + RESTPost.getStudent() + "\n";
                content += "Attendance Class: " + RESTPost.getAttendanceclass() + "\n";
                content += "Date: " + RESTPost.getDate() + "\n";
                content += "Status: " + RESTPost.getStatus() + "\n";

                Log.d("ADDED", content);

            }

            @Override
            public void onFailure(Call<REST_Post> call, Throwable t) {
                Log.e("FAILURE ON CREATE: ", t.getMessage());
            }
        });
    }

    public void CheckinOnline (String studentnum) {


        Call<List<REST_Post>> call = jsonPlaceHolderApi.getPostsAttendance();
        call.enqueue(new Callback<List<REST_Post>>() {
            @Override
            public void onResponse(Call<List<REST_Post>> call, Response<List<REST_Post>> response) {
                List<REST_Post> RESTPosts = response.body();
                DBCross.deleteAll();
                for(REST_Post RESTPost : RESTPosts) {
                    if (Objects.equals(studentnum, RESTPost.getStudent())) {

                        Boolean checkinsertdata = DBCross.insertuserdata(RESTPost.getCourse(), RESTPost.getStudent(), RESTPost.getAttendanceclass(), RESTPost.getDate(), String.valueOf(RESTPost.getStatus()) );

                        if(checkinsertdata == true) {
                            Log.d("ADDED", "Success");
                        }
                        else{
                            Log.d("Not", "Successful");
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<List<REST_Post>> call, Throwable t) {
                Log.e("FAILURE ON CREATE: ", t.getMessage());
            }
        });

    }


}