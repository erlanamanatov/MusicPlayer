package com.erkprog.musicplayer.model.repositories.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ListView;

import com.erkprog.musicplayer.model.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "myLogs:DatabaseHelper";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "musicDB";
    public static final String TABLE_SONGS = "songs";

    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_ARTISTS = "artists";
    public static final String KEY_TRACK_PATH = "songPath";
    public static final String KEY_COVER_IMG_PATH = "coverImgPath";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
        contentValues.put(DatabaseHelper.KEY_TRACK_PATH, song.getSongSource());
        contentValues.put(DatabaseHelper.KEY_COVER_IMG_PATH, song.getImageSource());

        long result = db.insert(DatabaseHelper.TABLE_SONGS, null, contentValues);
        db.close();

        if (result == -1) {
            Log.d(TAG, "addSongToDB: error");
            return false;
        } else {
            Log.d(TAG, "addSongToDB: new song added");
            return true;
        }
    }

    public ArrayList<Song> getAllSongs() {
        Log.d(TAG, "getAllSongs: starts");
        ArrayList<Song> songList = new ArrayList<>();

        //List of songs that we cant use, for example we have row in DB, but file does not exist in storage
        ArrayList<Integer> unUsableSongs = new ArrayList<>();

        //select all query
        String query = "SELECT * FROM " + TABLE_SONGS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int song_id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                Song song = new Song(cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                        cursor.getString(cursor.getColumnIndex(KEY_TRACK_PATH)),
                        cursor.getString(cursor.getColumnIndex(KEY_ARTISTS)),
                        cursor.getString(cursor.getColumnIndex(KEY_COVER_IMG_PATH)));
                if (!isSongUsable(song)) {
                    unUsableSongs.add(song_id);
                } else {
                    songList.add(song);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        Log.d(TAG, "getAllSongs: " + songList.size() + " usable songs found in DB");
        Log.d(TAG, "getAllSongs: unUsable Songs count: " + unUsableSongs.size());


        //delete unUsable songs from db
        if (unUsableSongs.size() > 0) {
            for (int id : unUsableSongs) {
                db.delete(TABLE_SONGS, KEY_ID + "= ?", new String[]{String.valueOf(id)});
                Log.d(TAG, "getAllSongs: song deleted from db, id = " + id);
            }
        }

        db.close();
        return songList;
    }

    private boolean isSongUsable(Song song) {
        File trackPath = new File(song.getSongSource());
        if (!trackPath.exists()) {
            return false;
        }
        File coverImgPath = new File(song.getImageSource());
        if (!coverImgPath.exists()) {
            return false;
        }
        return true;
    }

    public boolean isSongInDB(Song song) {
        //TODO delete unUsable Songs first
        Log.d(TAG, "isSongInDB: starts, songName =" + song.getName());
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SONGS, null,
                "( " + KEY_NAME + "= ?)  and " + "(" + KEY_ARTISTS + " = ?)",
                new String[]{song.getName(), song.getArtists()},
                null, null, null);

        if (cursor.moveToFirst()){
            Log.d(TAG, "isSongInDB: true");
            db.close();
            return true;
        } else {
            Log.d(TAG, "isSongInDB: false");
            db.close();
            return false;
        }
    }
}
