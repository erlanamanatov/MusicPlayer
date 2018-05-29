package com.erkprog.musicplayer.model.repositories;

import com.erkprog.musicplayer.model.Song;

import java.util.List;

public interface SongsRepository {
    void getSongList(OnFinishedListener onFinishedListener);

    interface OnFinishedListener {
        void onFinished(List<Song> songList);
        void onFailure(Throwable t);
    }
}
