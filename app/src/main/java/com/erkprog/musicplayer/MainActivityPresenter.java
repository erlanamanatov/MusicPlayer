package com.erkprog.musicplayer;

import android.util.Log;

import com.erkprog.musicplayer.repositories.SongsRepository;
import com.erkprog.musicplayer.repositories.impl.ServerSongsRepository;

import java.util.List;

public class MainActivityPresenter implements SongsRepository.OnFinishedListener, DownloadManager.OnDownloadStatusListener{
    private static final String TAG = "MainActivityPresenter";

    private MainActivityView view;
    private SongsRepository mSongsRepository;

    public MainActivityPresenter(MainActivityView view) {
        this.view = view;
        mSongsRepository = new ServerSongsRepository();
    }

    void loadSongs() {
        mSongsRepository.getDataFromServer(this);
    }

    void downloadSong(Song song, int position){
//        new DownloadManager(this).execute(song.getUrl());
        new DownloadManager(this, position).execute(song);
    }

    @Override
    public void onFinished(List<Song> songList) {
        for (Song song : songList) {
            Log.d(TAG, "onFinished: " + song.toString());
        }

        view.displaySongs(songList);
    }

    @Override
    public void onSongDownloaded(Song song, int position) {
        view.updateSong(song, position);
    }

    @Override
    public void onFailure(Throwable t) {
        Log.d(TAG, "onFailure: " + t.getMessage());
    }

    @Override
    public void updateProgress(String progress) {
        view.updateProgress(Integer.parseInt(progress));
    }

    @Override
    public void onFileDownloadFinished() {
        view.onFileDownloaded();
    }
}
