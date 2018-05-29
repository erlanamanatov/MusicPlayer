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

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

public class DatabaseSongRepository implements SongsRepository {
    private static final String TAG = "DatabaseSongRepository";
    private static String mDataDir = "Test Music Player";
    private static String mSongsDir = "mp3files";
    private static String mCoverDir = "cover_img";


    private DatabaseHelper dbHelper;

    public interface OnTrackDownloadListener {
        void onMp3TrackDownloadComplete(SongItem songItem, int position, String mp3FilePath);
        void onMp3TrackDownloadError(int songItemPosition);
        void onMp3TrackDownloadProgress(int songItemPosition);
    }

    public interface OnCoverImageDownloadListener {
        void onCoverImageDownloadComplete(SongItem songItem, int position, String mp3FilePath, String coverImgFilePath);
        void onCoverImageDownloadError(int songItemPosition);
    }

    public DatabaseSongRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
        PRDownloader.initialize(context);
        Log.d(TAG, "DatabaseSongRepository: Created");

    }

    private void createDirIfNotExist() {
        Log.d(TAG, "createDirIfNotExist: starts");
        File dataDir = new File(Environment.getExternalStorageDirectory(), mDataDir);
        if (!dataDir.exists()){
            if (dataDir.mkdirs()){ 
                Log.d(TAG, "createDirIfNotExist: folder created");
            } else {
                Log.d(TAG, "createDirIfNotExist: folder not created");
            }
        }
        File songsDir = new File(Environment.getExternalStorageDirectory().toString()
                + "/" + mDataDir, mSongsDir);
        if (!songsDir.exists()){
            if (songsDir.mkdirs()){
                Log.d(TAG, "createDirIfNotExist: songsDir created");
            } else {
                Log.d(TAG, "createDirIfNotExist: songsDir not created");
            }
        }
        File coverDir = new File(Environment.getExternalStorageDirectory().toString()
                + "/" + mDataDir, mCoverDir);
        if (!coverDir.exists()){
            coverDir.mkdirs();
        }
    }

    @Override
    public void getSongList(OnFinishedListener onFinishedListener) {

    }

    public void downloadMp3Track(final SongItem songItem, final int position, final OnTrackDownloadListener listener){
        Log.d(TAG, "downloadSong: started downloading");
        createDirIfNotExist();
        String destinationFolder = Environment.getExternalStorageDirectory().toString()
                + "/" + mDataDir
                + "/" + mSongsDir;

        String fileName = songItem.getSong().getName() + " - " + songItem.getSong().getArtists() + ".mp3";
        final String filePath = destinationFolder + "/" + fileName;

        int downloadId = PRDownloader.download(songItem.getSong().getUrl(),
                destinationFolder,
                fileName)
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
                        listener.onMp3TrackDownloadComplete(songItem, position, filePath);
                    }

                    @Override
                    public void onError(Error error) {
                        songItem.setProgress(0);
                        listener.onMp3TrackDownloadError(position);
                    }
                });
    }

    public void downloadCoverImg(final SongItem songItem, final int position, final String mp3FilePath, final OnCoverImageDownloadListener onCoverImageDownloadListener){
        Log.d(TAG, "downloadCoverImg: starts");
        String destinationFolder = Environment.getExternalStorageDirectory().toString()
                + "/" + mDataDir
                + "/" + mCoverDir;

        String fileName = songItem.getSong().getName() + " - " + songItem.getSong().getArtists() + ".jpg";
        final String coverImgFilePath = destinationFolder + "/" + fileName;

        int downloadId = PRDownloader.download(songItem.getSong().getImageUrl(),
                destinationFolder,
                fileName)
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

                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        onCoverImageDownloadListener.onCoverImageDownloadComplete(songItem, position, mp3FilePath, coverImgFilePath);
                    }

                    @Override
                    public void onError(Error error) {
                        songItem.setProgress(0);
                        onCoverImageDownloadListener.onCoverImageDownloadError(position);
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
