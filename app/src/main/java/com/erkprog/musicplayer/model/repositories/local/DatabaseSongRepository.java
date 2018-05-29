package com.erkprog.musicplayer.model.repositories.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.erkprog.musicplayer.model.Song;
import com.erkprog.musicplayer.model.repositories.SongsRepository;

public class DatabaseSongRepository implements SongsRepository {
    private static final String TAG = "DatabaseSongRepository";

    private DatabaseHelper dbHelper;

    public DatabaseSongRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
        Log.d(TAG, "DatabaseSongRepository: Created");
    }

    @Override
    public void getSongList(OnFinishedListener onFinishedListener) {

    }

    public boolean addSong(Song song) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.KEY_NAME, song.getName());
        contentValues.put(DatabaseHelper.KEY_ARTISTS, song.getArtists());
        contentValues.put(DatabaseHelper.KEY_PATH, song.getUrl());

        Log.d(TAG, "addSong: adding new song");

        long result = db.insert(DatabaseHelper.TABLE_SONGS, null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
}
