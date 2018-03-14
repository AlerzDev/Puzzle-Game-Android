package com.alexdev.puzzlegame.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.alexdev.puzzlegame.models.GamePost;
import com.alexdev.puzzlegame.models.sqlite.GameContract;

import java.util.List;

/**
 * Created by alexdev on 14/03/18.
 */

public class GameDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "games.db";

    public GameDbHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + GameContract.GameEntry.TABLE_NAME + " ("
                + GameContract.GameEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + GameContract.GameEntry.ID + " TEXT NOT NULL,"
                + GameContract.GameEntry.USERNAME + " TEXT NOT NULL,"
                + GameContract.GameEntry.GENDER + " TEXT NOT NULL,"
                + GameContract.GameEntry.PLAYER + " TEXT NOT NULL,"
                + GameContract.GameEntry.TIME + " TEXT NOT NULL)");
    }

    public boolean saveGames(List<GamePost> games) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues values;
        for (GamePost game:games) {
            values = new ContentValues();
            values.put(GameContract.GameEntry.ID,game.getId());
            values.put(GameContract.GameEntry.GENDER,game.getGender());
            values.put(GameContract.GameEntry.PLAYER,game.getPlayer());
            values.put(GameContract.GameEntry.TIME,game.getTime());
            values.put(GameContract.GameEntry.USERNAME,game.getUsername());
            sqLiteDatabase.insert(GameContract.GameEntry.TABLE_NAME, null, values);
        }
        return true;
    }

    public Cursor getAllGames()
    {
        return getReadableDatabase().query(GameContract.GameEntry.TABLE_NAME,null,
                null,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
