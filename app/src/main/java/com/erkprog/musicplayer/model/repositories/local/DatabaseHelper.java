package com.erkprog.musicplayer.model.repositories.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.erkprog.musicplayer.model.Song;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "musicDB";
    public static final String TABLE_SONGS = "songs";

    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_ARTISTS = "artists";
    public static final String KEY_TRACK_PATH = "songPath";
    public static final String KEY_COVER_IMG_PATH = "coverImgPath";

    public DatabaseHelper(Context context){
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlQuery = "CREATE TABLE " + TABLE_SONGS + " ("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_NAME + " TEXT, "
                + KEY_ARTISTS + " TEXT, "
                + KEY_TRACK_PATH + " TEXT, "
                + KEY_COVER_IMG_PATH + " TEXT"
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

    public boolean addSongToDB(Song song) {
        Log.d(TAG, "addSongToDB: starts");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.KEY_NAME, song.getName());
        contentValues.put(DatabaseHelper.KEY_ARTISTS, song.getArtists());
        contentValues.put(DatabaseHelper.KEY_TRACK_PATH, song.getUrl());
        contentValues.put(DatabaseHelper.KEY_COVER_IMG_PATH, song.getImageUrl());

        long result = db.insert(DatabaseHelper.TABLE_SONGS, null, contentValues);

        if (result == -1) {
            Log.d(TAG, "addSongToDB: error");
            return false;
        } else {
            Log.d(TAG, "addSongToDB: new song added");
            return true;
        }
    }
}
