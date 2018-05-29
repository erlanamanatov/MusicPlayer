package com.erkprog.musicplayer.model.repositories.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "musicDB";
    public static final String TABLE_SONGS = "songs";

    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_ARTISTS = "artists";
    public static final String KEY_PATH = "path";

    public DatabaseHelper(Context context){
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlQuery = "CREATE TABLE " + TABLE_SONGS + " ("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_NAME + " TEXT, "
                + KEY_ARTISTS + " TEXT, "
                + KEY_PATH + " TEXT"
                + ")";
        Log.d(TAG, "onCreate: " + sqlQuery);
        db.execSQL(sqlQuery);
        Log.d(TAG, "onCreate: ends");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String sqlQuery = "drop table if exists " + TABLE_SONGS;
        db.execSQL(sqlQuery);
        onCreate(db);
    }
}
