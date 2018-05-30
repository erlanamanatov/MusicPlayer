package com.erkprog.musicplayer.model.repositories.local;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.erkprog.musicplayer.model.Song;
import com.erkprog.musicplayer.model.SongItem;
import com.erkprog.musicplayer.model.repositories.SongsRepository;

import java.io.File;
import java.util.ArrayList;

public class DatabaseSongRepository implements SongsRepository {
    private static final String TAG = "myLogs:DatabaseRepo";
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
        Log.d(TAG, "DatabaseSongRepository: Constructor");
        dbHelper = new DatabaseHelper(context);
        PRDownloader.initialize(context);

    }

    @Override
    public void getSongList(OnFinishedListener onFinishedListener) {
        try {
            ArrayList<Song> dbSongs = dbHelper.getAllSongs();
            Log.d(TAG, "getSongList: got list with " + dbSongs.size() + " items from DbHelper");
            onFinishedListener.onFinished(dbSongs);
        } catch (Exception e){
            onFinishedListener.onFailure(null);
        }
    }

    public boolean addSongToDB(Song song, String mp3FilePath, String coverImgFilePath) {
        return dbHelper.addSongToDB(new Song(song.getName(), mp3FilePath, song.getArtists(), coverImgFilePath));
    }

    public boolean isSongLocallyAvailable(Song song) {
        return dbHelper.isSongInDB(song);
    }

    public void downloadMp3Track(final SongItem songItem, final int position, final OnTrackDownloadListener listener) {
        Log.d(TAG, "downloadSong: starts");
        createDirIfNotExist();
        String destinationFolder = Environment.getExternalStorageDirectory().toString()
                + "/" + mDataDir
                + "/" + mSongsDir;

        String fileName = songItem.getSong().getName() + " - " + songItem.getSong().getArtists() + ".mp3";
        final String filePath = destinationFolder + "/" + fileName;

        int downloadId = PRDownloader.download(songItem.getSong().getSongSource(),
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
                        int progress = (int) (((double) progressBytes.currentBytes / progressBytes.totalBytes) * 100);
                        if (progress % 10 == 0 || progress == 1 || progress == 6) {
                            songItem.setProgress(progress);
                            listener.onMp3TrackDownloadProgress(position);
                        }
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        Log.d(TAG, "onDownloadComplete: mp3 file downloaded successfully");
                        listener.onMp3TrackDownloadComplete(songItem, position, filePath);
                    }

                    @Override
                    public void onError(Error error) {
                        Log.d(TAG, "onError: " + error.toString());
                        songItem.setProgress(0);
                        listener.onMp3TrackDownloadError(position);
                    }
                });
    }

    public void downloadCoverImg(final SongItem songItem, final int position, final String mp3FilePath, final OnCoverImageDownloadListener onCoverImageDownloadListener) {
        Log.d(TAG, "downloadCoverImg: starts");
        String destinationFolder = Environment.getExternalStorageDirectory().toString()
                + "/" + mDataDir
                + "/" + mCoverDir;

        String fileName = songItem.getSong().getName() + " - " + songItem.getSong().getArtists() + ".jpg";
        final String coverImgFilePath = destinationFolder + "/" + fileName;

        int downloadId = PRDownloader.download(songItem.getSong().getImageSource(),
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
                        Log.d(TAG, "onDownloadComplete: coverImg downloaded successfully");
                        onCoverImageDownloadListener.onCoverImageDownloadComplete(songItem, position, mp3FilePath, coverImgFilePath);
                    }

                    @Override
                    public void onError(Error error) {
                        Log.d(TAG, "onError: coverImg download error");
                        songItem.setProgress(0);
                        onCoverImageDownloadListener.onCoverImageDownloadError(position);
                    }
                });
    }

    private void createDirIfNotExist() {
        Log.d(TAG, "createDirIfNotExist: starts");
        File dataDir = new File(Environment.getExternalStorageDirectory(), mDataDir);
        if (!dataDir.exists()) {
            if (dataDir.mkdirs()) {
                Log.d(TAG, "createDirIfNotExist: folder created");
            } else {
                Log.d(TAG, "createDirIfNotExist: folder not created");
            }
        }
        File songsDir = new File(Environment.getExternalStorageDirectory().toString()
                + "/" + mDataDir, mSongsDir);
        if (!songsDir.exists()) {
            if (songsDir.mkdirs()) {
                Log.d(TAG, "createDirIfNotExist: songsDir created");
            } else {
                Log.d(TAG, "createDirIfNotExist: songsDir not created");
            }
        }
        File coverDir = new File(Environment.getExternalStorageDirectory().toString()
                + "/" + mDataDir, mCoverDir);
        if (!coverDir.exists()) {
            coverDir.mkdirs();
        }
    }

}
