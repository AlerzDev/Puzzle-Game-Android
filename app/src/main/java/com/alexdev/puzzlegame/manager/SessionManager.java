package com.alexdev.puzzlegame.manager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.alexdev.puzzlegame.models.UserPost;

/**
 * Created by alexdev on 12/03/18.
 */

public class SessionManager {

    public static final String REFERENCE = "MyPrefs" ;
    public static final String USERNAME = "nameKey";
    public static final String EMAIL = "emailKey";
    public static final String GENDER = "genderKey";
    public static final String ID = "idKey";

    UserPost user;
    SharedPreferences sharedpreferences;

    public SessionManager(UserPost u, Activity activity)
    {
        user = u;
        sharedpreferences = activity.getSharedPreferences(REFERENCE, Context.MODE_PRIVATE);
        saveSession();
    }
    private void saveSession() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(USERNAME, user.getUsername());
        editor.putString(EMAIL, user.getEmail());
        editor.putString(ID,user.getId());
        editor.putString(GENDER, user.getGender());
        editor.commit();
    }
}
