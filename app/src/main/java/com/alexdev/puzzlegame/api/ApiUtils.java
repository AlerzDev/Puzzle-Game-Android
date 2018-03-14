package com.alexdev.puzzlegame.api;

import com.alexdev.puzzlegame.utils.Constants;

/**
 * Created by alexdev on 10/03/18.
 */

public class ApiUtils {

    public static UserApi getUserAPI() {
        return RetrofitClient.getClient(Constants.URL_BASE_API).create(UserApi.class);
    }

    public static GameApi getGameAPI()
    {
        return RetrofitClient.getClient(Constants.URL_BASE_API).create(GameApi.class);
    }

}
