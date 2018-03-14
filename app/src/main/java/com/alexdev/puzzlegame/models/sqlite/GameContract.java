package com.alexdev.puzzlegame.models.sqlite;

import android.provider.BaseColumns;

/**
 * Created by alexdev on 14/03/18.
 */

public class GameContract {

    public static abstract class GameEntry implements BaseColumns {
        public static final String TABLE_NAME ="game";
        public static final String ID = "id";
        public static final String USERNAME = "username";
        public static final String GENDER = "gender";
        public static final String TIME = "time";
        public static final String PLAYER = "player";
    }

}
