package com.alexdev.puzzlegame.api;

import com.alexdev.puzzlegame.models.UserPost;
import com.alexdev.puzzlegame.models.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by alexdev on 10/03/18.
 */

public interface UserApi {

    @POST("new-user")
    Call<UserResponse> registerUser(@Body UserPost user);

    @POST("new-session")
    @FormUrlEncoded
    Call<UserResponse> newSession(@Field("username") String username, @Field("password") String password);

}
