package com.alexdev.puzzlegame.manager;

import android.app.Activity;
import android.content.Intent;

import com.alexdev.puzzlegame.activities.game.GameActivity;
import com.alexdev.puzzlegame.activities.home.HomeActivity;
import com.alexdev.puzzlegame.activities.session.SplashScreenActivity;

/**
 * Created by alexdev on 08/03/18.
 */

public class NavigatorManager {

    public static NavigatorManager instance;

    public static NavigatorManager getInstance() {

        if(instance == null) instance = new NavigatorManager();
        return instance;
    }

    public void goHomeActivity(Activity activity){
        Intent intent = new Intent(activity, HomeActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public void goSplashScreenActivity(Activity activity){
        Intent intent = new Intent(activity, SplashScreenActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public void goGameActivity(Activity activity){
        Intent intent = new Intent(activity, GameActivity.class);
        activity.startActivity(intent);
    }

}
