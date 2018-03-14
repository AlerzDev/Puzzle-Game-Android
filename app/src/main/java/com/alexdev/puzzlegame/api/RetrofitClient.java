package com.alexdev.puzzlegame.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by alexdev on 10/03/18.
 */

public class RetrofitClient {

    private static Retrofit instance = null;

    public static Retrofit getClient(String baseUrl) {
        if (instance==null) {
            instance = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return instance;
    }

}
