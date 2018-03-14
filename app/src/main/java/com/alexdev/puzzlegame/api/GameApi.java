package com.alexdev.puzzlegame.api;

import com.alexdev.puzzlegame.models.GamePost;
import com.alexdev.puzzlegame.models.GameResponse;
import com.alexdev.puzzlegame.models.SaveGameResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by alexdev on 12/03/18.
 */

public interface GameApi {

    @POST("new-save-game")
    Call<GameResponse> saveGame(@Body GamePost game);

    @GET("get-all-save-game/{id}")
    Call<SaveGameResponse> getSaveGames(@Path("id") String id);

    @GET("get-all-games")
    Call<SaveGameResponse> getAllGames();

}
