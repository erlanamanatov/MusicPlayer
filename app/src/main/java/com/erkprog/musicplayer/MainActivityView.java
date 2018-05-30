package com.erkprog.musicplayer;

import com.erkprog.musicplayer.model.SongItem;

import java.util.List;

public interface MainActivityView {

    void displaySongs(List<SongItem> songItems);

    void updateSongProgress(int songItemPosition);

    void updateSongItem(int songItemPosition);

    void showToast(String message);

    void addItem(SongItem songItem);
}
