package com.erkprog.musicplayer;

import java.util.List;

public interface MainActivityView {

    void displaySongs(List<Song> songList);

    void updateProgress(int progress);

    void onFileDownloaded();

    void updateSong(Song song, int position);
}
