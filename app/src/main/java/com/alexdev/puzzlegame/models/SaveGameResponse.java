package com.alexdev.puzzlegame.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by alexdev on 12/03/18.
 */

public class SaveGameResponse {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("games")
    @Expose
    private List<GamePost> games;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<GamePost> getGame() {
        return games;
    }

    public void setGame(List<GamePost> game) {
        this.games = game;
    }
}
