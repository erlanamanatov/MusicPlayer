package com.erkprog.musicplayer.model.repositories.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.media.midi.MidiManager;
import android.os.Environment;
import android.util.Log;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;
import com.erkprog.musicplayer.model.Song;
import com.erkprog.musicplayer.model.SongItem;
import com.erkprog.musicplayer.model.repositories.SongsRepository;
import com.erkprog.musicplayer.utils.DownloadManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

public class DatabaseSongRepository implements SongsRepository {
    private static final String TAG = "DatabaseSongRepository";

    private DatabaseHelper dbHelper;

    public interface OnTrackDownloadListener {
        void onMp3TrackDownloadComplete();
        void onMp3TrackDownloadError();
        void onMp3TrackDownloadProgress(int songPosition);
    }

    public interface OnCoverImageDownloadListener {
        void onCoverImageDownloadComplete();
        void onCoverImageDownloadError();
    }

    public DatabaseSongRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
        PRDownloader.initialize(context);
        Log.d(TAG, "DatabaseSongRepository: Created");

    }

    @Override
    public void getSongList(OnFinishedListener onFinishedListener) {

    }

    public void downloadMp3Track(final SongItem songItem, final int position, final OnTrackDownloadListener listener){
        Log.d(TAG, "downloadSong: started downloading");

        int downloadId = PRDownloader.download("http://hck.re/wxlUcX",
                Environment.getExternalStorageDirectory().toString(),
                "tt.mp3")
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progressBytes) {
                        int progress =(int) (((double) progressBytes.currentBytes/progressBytes.totalBytes) * 100);
                        if (progress % 10 == 0 || progress == 1 ||progress ==6) {
                            songItem.setProgress(progress);
                            listener.onMp3TrackDownloadProgress(position);
                        }
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        listener.onMp3TrackDownloadComplete();
                    }

                    @Override
                    public void onError(Error error) {
                        listener.onMp3TrackDownloadError();
                    }
                });
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
