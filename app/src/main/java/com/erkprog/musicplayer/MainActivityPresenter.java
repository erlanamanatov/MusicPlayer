package com.erkprog.musicplayer;

import android.util.Log;

import com.erkprog.musicplayer.model.Song;
import com.erkprog.musicplayer.model.SongItem;
import com.erkprog.musicplayer.model.repositories.SongsRepository;
import com.erkprog.musicplayer.model.repositories.local.DatabaseSongRepository;
import com.erkprog.musicplayer.model.repositories.remote.ServerSongsRepository;
import com.erkprog.musicplayer.utils.DownloadManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivityPresenter implements SongsRepository.OnFinishedListener,
        DownloadManager.OnDownloadStatusListener, DatabaseSongRepository.OnTrackDownloadListener, DatabaseSongRepository.OnCoverImageDownloadListener{
    private static final String TAG = "myLogs:Presenter";

    private MainActivityView view;
    private SongsRepository mSongsRepository;
    private DatabaseSongRepository mDatabaseSongRepository;

    public MainActivityPresenter(MainActivityView view, DatabaseSongRepository dbSongRepository) {
        this.view = view;
        mSongsRepository = new ServerSongsRepository();
        mDatabaseSongRepository = dbSongRepository;
    }

    /*
    getting data from server
     */
    void loadSongs() {
        mSongsRepository.getSongList(this);
        mDatabaseSongRepository.getSongList(this);
    }

    @Override
    public void onFinished(List<Song> songList) {
        Log.d(TAG, "onFinished: starts");
//        List<SongItem> songItems = new ArrayList<>();

        if (!songList.isEmpty()){
            for (Song song : songList) {
                Log.d(TAG, "onFinished: " + song.toString());
                if (song.getImageSource().contains("http")){
                    if (!mDatabaseSongRepository.isSongLocallyAvailable(song)){
                        Log.d(TAG, "onFinished: view should add item");
                        view.addItem(new SongItem(song, false));
                    }
                } else {
                    view.addItem(new SongItem(song, true));
                }
//                songItems.add(new SongItem(song));
            }
        }

//        for (Song song : songList) {
//            Log.d(TAG, "onFinished: " + song.toString());
//            songItems.add(new SongItem(song));
//        }

//        view.displaySongs(songItems);
    }


    @Override
    public void onFailure(Throwable t) {
        Log.d(TAG, "onFailure: " + t.getMessage());
    }


    /*
    Downloading song
     */

    void downloadSong(SongItem songItem, int position){
        mDatabaseSongRepository.downloadMp3Track(songItem, position, this);
//        new DownloadManager(songItem, position, this).download();
    }

    @Override
    public void updateSongProgress(int songItemPosition) {
        view.updateSongProgress(songItemPosition);
    }

    @Override
    public void onSongDownloaded(int position) {
        view.updateSongItem(position);
    }


    @Override
    public void onMp3TrackDownloadComplete(SongItem songItem, int position, String mp3FilePath) {
        mDatabaseSongRepository.downloadCoverImg(songItem, position, mp3FilePath, this);

    }

    @Override
    public void onMp3TrackDownloadError(int songItemPosition) {
        view.updateSongProgress(songItemPosition);

    }

    @Override
    public void onMp3TrackDownloadProgress(int songItemPosition) {
        view.updateSongProgress(songItemPosition);
    }

    @Override
    public void onCoverImageDownloadComplete(SongItem songItem, int position, String mp3FilePath, String coverImgFilePath) {
        if (mDatabaseSongRepository.addSongToDB(songItem.getSong(), mp3FilePath, coverImgFilePath)) {
            songItem.getSong().setSongSource(mp3FilePath);
            songItem.getSong().setImageSource(coverImgFilePath);
            songItem.setLocallyAvailable(true);

            view.updateSongItem(position);
        } else {
            view.showToast("Error on saving song to DB.");
        }
    }

    @Override
    public void onCoverImageDownloadError(int songItemPosition) {
        view.showToast("Downloading Error");
        view.updateSongItem(songItemPosition);
    }
}
