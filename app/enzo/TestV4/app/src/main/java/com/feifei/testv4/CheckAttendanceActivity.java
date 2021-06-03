package com.feifei.testv4;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

public class CheckAttendanceActivity extends AppCompatActivity {
    private TextView textViewResult;

    private JsonPlaceHolderApi jsonPlaceHolderApi;

    ArrayList<User_Subject> subjectArrayList;
    ArrayList<Scan_Data> scanDataList;
    ArrayList<DummyAttendanceList> dummyattendance;

    EditText inputUsername;
    EditText inputEmail;
    EditText inputID;

    DBHelperAttendanceList DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_attendance);

        textViewResult = findViewById(R.id.text_view_result);
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
                .baseUrl("http://192.168.1.3:8000/viewset/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        DB = new DBHelperAttendanceList(this);

        // load database info on the list on create

        //Ito so far yng 3 functions na kayang gawin ng App. Ginagawa lang kasi sa main is hinahandle yung pagcall niya from JsonPlaceHolderApi
        //If you want to modify kung aling parts gusto mong kunin from which endpoint, you got o REST_Post
        //getPosts();
        //createPost();
        //updatePost();



    }


    //Function for getting stuff in database sa server
    private void getPosts(String studentnum){
        Call<List<REST_Post>> call = jsonPlaceHolderApi.getPosts();
        call.enqueue(new Callback<List<REST_Post>>() {
            @Override
            public void onResponse(Call<List<REST_Post>> call, Response<List<REST_Post>> response) {


                if(!response.isSuccessful()){
                    textViewResult.setText("Get Posts: Code: " + response.code() + "\n");
                    try{
                        textViewResult.setText("Get Posts: Error: "+  response.errorBody().string());
                    } catch (IOException e) {
                        textViewResult.setText("Unkown error: " + response.errorBody());
                    }
                    return;
                }
                List<REST_Post> RESTPosts = response.body();
                textViewResult.setText("");
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

                        String content = "";
                        content += "Course: " + RESTPost.getCourse() + "\n";
                        content += "Student Number: " + RESTPost.getStudent() + "\n";
                        content += "Attendance Class: " + RESTPost.getAttendanceclass() + "\n";
                        content += "Date: " + RESTPost.getDate() + "\n";
                        content += "Status: " + RESTPost.getStatus() + "\n";
                        textViewResult.append(content);


                    }
                }
            }

            @Override
            public void onFailure(Call<List<REST_Post>> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
    }



    //Function for creating stuff in database sa server
    private void createPost(String course, String student, String attendanceclass, String date, boolean status){

        REST_Post RESTPost = new REST_Post(course, student, attendanceclass, date, status);

        Call<REST_Post> call = jsonPlaceHolderApi.createPost(RESTPost);

        call.enqueue(new Callback<REST_Post>() {
            @Override
            public void onResponse(Call<REST_Post> call, Response<REST_Post> response) {
                if(!response.isSuccessful()){
                    textViewResult.setText("Get Posts: Code: " + response.code());
                    return;
                }

                REST_Post RESTPostResponse = response.body();
                String content = "";
                content += "Course: " + RESTPost.getCourse() + "\n";
                content += "Student Number: " + RESTPost.getStudent() + "\n";
                content += "Attendance Class: " + RESTPost.getAttendanceclass() + "\n";
                content += "Date: " + RESTPost.getDate() + "\n";
                content += "Status: " + RESTPost.getStatus() + "\n";

                textViewResult.setText(content);

            }

            @Override
            public void onFailure(Call<REST_Post> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
    }

    //function for updating stuff sa database ng server
    private void updatePost(String course, String student, String attendanceclass, String date, boolean status, int ID) {
        REST_Post RESTPost = new REST_Post(course, student, attendanceclass, date, status);

        Call<REST_Post> call = jsonPlaceHolderApi.putPost(ID, RESTPost);

        call.enqueue(new Callback<REST_Post>() {
            @Override
            public void onResponse(Call<REST_Post> call, Response<REST_Post> response) {
                if(!response.isSuccessful()){
                    textViewResult.setText("Get Posts: Code: " + response.code());
                    return;
                }

                REST_Post RESTPostResponse = response.body();
                String content = "";
                content += "Course: " + RESTPost.getCourse() + "\n";
                content += "Student Number: " + RESTPost.getStudent() + "\n";
                content += "Attendance Class: " + RESTPost.getAttendanceclass() + "\n";
                content += "Date: " + RESTPost.getDate() + "\n";
                content += "Status: " + RESTPost.getStatus() + "\n";

                textViewResult.setText(content);

            }

            @Override
            public void onFailure(Call<REST_Post> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });

    }

    public boolean CheckinOnline (String course, String student, String attendanceclass, String date, Boolean status) {
        final boolean[] checkstatus = new boolean[1];

        Call<List<REST_Post>> call = jsonPlaceHolderApi.getPosts();
        call.enqueue(new Callback<List<REST_Post>>() {
            @Override
            public void onResponse(Call<List<REST_Post>> call, Response<List<REST_Post>> response) {
                List<REST_Post> RESTPosts = response.body();
                textViewResult.setText("");

                for(REST_Post RESTPost : RESTPosts){


                    if (Objects.equals(student, RESTPost.getStudent()) && Objects.equals(course, RESTPost.getCourse()) &&
                            Objects.equals(attendanceclass, RESTPost.getAttendanceclass()) && Objects.equals(date, RESTPost.getDate())
                            && Objects.equals(status, RESTPost.getStatus())) {
                        checkstatus[0] = true;
                        break;
                    }

                }
                checkstatus[0] = false;
            }

            @Override
            public void onFailure(Call<List<REST_Post>> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
        return checkstatus[0];
    }

    public void GETButton(View view) {
        DB.deleteAll();
        String studentnum = "201501827";

        getPosts(studentnum);
    }
    /*
        public void POSTButton(View view) {
            String usernamefinal, emailfinal;

            inputUsername = (EditText) findViewById(R.id.inputUsername);
            inputEmail = (EditText) findViewById(R.id.inputEmail);

            usernamefinal = inputUsername.getText().toString();
            emailfinal = inputEmail.getText().toString();

            //createPost(emailfinal, usernamefinal);
        }


        public void PUTBUTTON(View view) {
            String usernamefinal, emailfinal;
            int idnumberfinal;

            inputUsername = (EditText) findViewById(R.id.inputUsername);
            inputEmail = (EditText) findViewById(R.id.inputEmail);
            inputID = (EditText) findViewById(R.id.inputID);

            usernamefinal = inputUsername.getText().toString();
            emailfinal = inputEmail.getText().toString();
            idnumberfinal = Integer.valueOf(inputID.getText().toString());
            //updatePost(emailfinal, usernamefinal, idnumberfinal);

        }
    */
    public void GETfromDB(View view) {
        Cursor res = DB.getdata();
        StringBuffer buffer = new StringBuffer();
        while(res.moveToNext()) {
            buffer.append("Course: " + res.getString(0) + "\n" );
            buffer.append("Date: " + res.getString(1) + "\n" );
            buffer.append("Class Room: " + res.getString(2) + "\n" );
            buffer.append("Date: " + res.getString(3) + "\n" );
            buffer.append("Present: " + res.getString(4) + "\n" );

        }

        textViewResult.setText(buffer.toString());
    }


    public void UpdateEntriesDB(View view) throws ParseException {
        Cursor res = DB.getdata();

        Boolean status;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        StringBuffer buffer = new StringBuffer();
        while(res.moveToNext()) {
            String checkcourse =  res.getString(0);
            String checkstudent =  res.getString(1);
            String checkclass =  res.getString(2);
            String checkdate =  res.getString(3);
            String checkstatus =  res.getString(4);

            buffer.append("Date: " + res.getString(1) + "\n" );
            buffer.append("Present: " + res.getString(2) + "\n" );

            Log.e("How bout", "here");
            Boolean checker = CheckinOnline(checkcourse, checkstudent, checkclass, checkdate, Boolean.parseBoolean(checkstatus));
            Log.e("YO", "Did it go here");
            if (checker == true){
                createPost(checkcourse, checkstudent, checkclass, checkdate, Boolean.parseBoolean(checkstatus));
            }
            else{
                Toast.makeText(this, "Nothing to Add", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void CrossCheckUpdateAttendance(View view) {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        subjectArrayList = new ArrayList<>();
        scanDataList = new ArrayList<>();
        databaseAccess.open();
        subjectArrayList = databaseAccess.getData();
        scanDataList = databaseAccess.getScanData();
        databaseAccess.close();

        dummyattendance = new ArrayList<>();
        int count = 0;

        //Get the dates and check for duplicates
        //String [][] Test;
        //Test = new String[][] {{"working", "with"}, {"2D", "arrays"}, {"is", "fun"}};
       // Log.d("Check", RemovePeripherals(Arrays.toString(Test[0])) + "  ===  " + Test[0][1]+ "  |||  " + Test[1]);


        dummyattendance.add(new DummyAttendanceList("pwer", "pwet"));
        Log.d("tag", dummyattendance.get(0).getClass_() + dummyattendance.get(0).getDate());
        Log.d("Tag", String.valueOf(scanDataList.size()));


        for (int i = 0; i< scanDataList.size(); i++){
                if(scanDataList.get(i++) == null){
                    break;
                }

                String dummyclass;
                String dateformat;
                String nextclass;
                Object[] sepdateformat = getDetails(scanDataList.get(i).getTime()); //get timeformat 1
                Object[] dummydateformat = getDetails(scanDataList.get(i++).getTime()); // get timeformat 2
                String nextdate; //
                nextdate = (String) dummydateformat[1]; //dummydate 2
                nextclass = scanDataList.get(i++).getUuid(); //class2
                dummyclass = scanDataList.get(i).getUuid(); //class 1
                dateformat = (String) sepdateformat[1];//dummydate 1

                if(!dummyclass.equals(nextclass) && !dateformat.equals(nextdate)) {
                    dummyattendance.add(new DummyAttendanceList(dummyclass,dateformat));
                }
        }

        for (int i = 0; i < subjectArrayList.size(); i++) {
            for (int j = 0; j < scanDataList.size(); j++ ) {
                if(subjectArrayList.get(i).getUuid().equals(scanDataList.get(j).getUuid())){
                    Object[] data = getDetails(scanDataList.get(j).getTime());
                    if((int)data[2] >= subjectArrayList.get(i).getTimestart() && (int)data[2] <= subjectArrayList.get(i).getTimeend()){
                        count = count + 1;
                    }
                    if (count >= 6){
                        Log.d("DB", "Identified 6");
                    }
                 }
            }
        }
    }

    public static String RemovePeripherals(String removal) {
        String nocomma = removal.replace(",", "");
        String noleftbracket = nocomma.replace("[", "");
        String norightbracket = noleftbracket.replace("]", "");
        String last = norightbracket.trim();

        return last;
    }

    public static Object[] getDetails (String ScanDataString) {
        String[] Splitted = ScanDataString.split(" ");
        String day = Splitted[0];
        String date = Splitted[1];
        int time = Integer.parseInt(Splitted[2].replace(":", ""));
        if (Splitted[3] == "pm") {
            time = time + 1200;
        }
        String ampm = Splitted[3];

        return new Object[] {day, date, time, ampm};

    }

    public Boolean checkentries(String course, String date){
        Cursor res = DB.getdata();
        while(res.moveToNext()) {
            if (course.equals(res.getString(0)) && date.equals(res.getString(1))) {
                return true;
            } else
                continue;
            /*
            buffer.append("Course: " + res.getString(0) + "\n" );
            buffer.append("Date: " + res.getString(1) + "\n" );
            buffer.append("Class Room: " + res.getString(2) + "\n" );
            buffer.append("Date: " + res.getString(3) + "\n" );
            buffer.append("Present: " + res.getString(4) + "\n" );

        }*/
        }
        return false;
    }

}
