package com.erkprog.musicplayer.repositories;

import android.view.View;

import com.erkprog.musicplayer.Song;

import java.util.List;

public interface SongsRepository {
    void getDataFromServer(OnFinishedListener onFinishedListener);

    interface OnFinishedListener {
        void onFinished(List<Song> songList);
        void onFailure(Throwable t);
    }
}
