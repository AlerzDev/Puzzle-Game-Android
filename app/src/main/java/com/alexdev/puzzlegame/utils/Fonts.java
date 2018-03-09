package com.alexdev.puzzlegame.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;

import java.util.Locale;

/**
 * Created by alexdev on 07/03/18.
 */

public class Fonts {

    public static Fonts instance;

    public static Fonts getInstance()
    {
        if(instance == null) instance = new Fonts();
        return instance;
    }

    public Typeface getPoiretOneRegular(Context context)
    {
        AssetManager assetManager = context.getApplicationContext().getAssets();
        return Typeface.createFromAsset(assetManager, String.format(Locale.US,"fonts/%s", "poiretoneregular.ttf"));
    }

}
