package com.feifei.testv3;

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
    @GET("users/")
    Call<List<REST_Post>> getPosts();

    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json;charset=utf-8",
            "Cache-Control: no-cache"
    })
    @POST("users/")
    Call<REST_Post> createPost(@Body REST_Post RESTPost);
    /*
        @FormUrlEncoded
        @POST("article/")
        Call<Post> createPost(
                @Field("id") int id,
                @Field("title") String title,
                @Field("author") String author,
                @Field("email") String email
        );

        @FormUrlEncoded
        @POST("article/")
        Call<Post> createPost(@FieldMap Map<String, String> fields);
    */
    @PUT("users/{id}/")
    Call<REST_Post> putPost(@Path("id") int id, @Body REST_Post RESTPost);
}
