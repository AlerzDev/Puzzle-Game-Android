package com.alexdev.puzzlegame.models;

/**
 * Created by alexdev on 12/03/18.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GameResponse {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("game")
    @Expose
    private GamePost game;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public GamePost getGame() {
        return game;
    }

    public void setGame(GamePost game) {
        this.game = game;
    }
}
