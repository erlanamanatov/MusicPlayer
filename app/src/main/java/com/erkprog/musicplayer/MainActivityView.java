package com.erkprog.musicplayer;

import java.util.List;

public interface MainActivityView {

    void displaySongs(List<SongItem> songItems);

    void updateSongProgress(int songItemPosition);

    void updateSong(int songItemPosition);
}
