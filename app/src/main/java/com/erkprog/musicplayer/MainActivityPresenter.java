package com.erkprog.musicplayer;

import android.util.Log;

import com.erkprog.musicplayer.repositories.SongsRepository;
import com.erkprog.musicplayer.repositories.impl.ServerSongsRepository;

import java.util.List;

public class MainActivityPresenter implements SongsRepository.OnFinishedListener {
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

    @Override
    public void onFinished(List<Song> songList) {
        for (Song song : songList) {
            Log.d(TAG, "onFinished: " + song.toString());
        }

        view.displaySongs(songList);
    }

    @Override
    public void onFailure(Throwable t) {
        Log.d(TAG, "onFailure: " + t.getMessage());
    }
}
