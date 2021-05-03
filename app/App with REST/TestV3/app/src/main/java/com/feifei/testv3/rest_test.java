package com.feifei.testv3;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;



public class rest_test extends AppCompatActivity {

    private TextView textViewResult;

    private JsonPlaceHolderApi jsonPlaceHolderApi;

    EditText inputUsername;
    EditText inputEmail;
    EditText inputID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rest_test);

        textViewResult = findViewById(R.id.text_view_result);
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        //This helps in debugging kapag nakikipag interact with the server
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
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

        //Ito so far yng 3 functions na kayang gawin ng App. Ginagawa lang kasi sa main is hinahandle yung pagcall niya from JsonPlaceHolderApi
        //If you want to modify kung aling parts gusto mong kunin from which endpoint, you got o REST_Post
        //getPosts();
        //createPost();
        //updatePost();
    }


    //Function for getting stuff in database sa server
    private void getPosts(){
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
                for(REST_Post RESTPost : RESTPosts){
                    String content = "";
                    content += "Username: " + RESTPost.getUsername() + "\n";
                    content += "Email: " + RESTPost.getEmail() + "\n";

                    textViewResult.append(content);
                }
            }

            @Override
            public void onFailure(Call<List<REST_Post>> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
    }



    //Function for creating stuff in database sa server
    private void createPost(String email, String username){

       REST_Post RESTPost = new REST_Post(email, username);

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
                content += "Code: " + response.code() + "\n";
                content += "Username: " + RESTPostResponse.getUsername() + "\n";
                content += "Email: " + RESTPostResponse.getEmail() + "\n";

                textViewResult.setText(content);

            }

            @Override
            public void onFailure(Call<REST_Post> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
    }

    //function for updating stuff sa database ng server
    private void updatePost(String email, String username, int ID) {
        REST_Post RESTPost = new REST_Post(email, username);

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
                content += "Code: " + response.code() + "\n";
                content += "Username: " + RESTPostResponse.getUsername() + "\n";
                content += "Email: " + RESTPostResponse.getEmail() + "\n";

                textViewResult.setText(content);

            }

            @Override
            public void onFailure(Call<REST_Post> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });

    }

    public void GETButton(View view) {
        getPosts();
    }

    public void POSTButton(View view) {
        String usernamefinal, emailfinal;

        inputUsername = (EditText) findViewById(R.id.inputUsername);
        inputEmail = (EditText) findViewById(R.id.inputEmail);

        usernamefinal = inputUsername.getText().toString();
        emailfinal = inputEmail.getText().toString();

        createPost(emailfinal, usernamefinal);
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
        updatePost(emailfinal, usernamefinal, idnumberfinal);

    }
}
