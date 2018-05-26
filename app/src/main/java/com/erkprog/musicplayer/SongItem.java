package com.erkprog.musicplayer;

public class SongItem {
    private Song mSong;
    private boolean locallyAvailable;
    private int mProgress;

    public SongItem(Song song) {
        mSong = song;
        locallyAvailable = false;
        mProgress = 0;
    }

    public Song getSong() {
        return mSong;
    }

    public boolean isLocallyAvailable() {
        return locallyAvailable;
    }

    public int getProgress() {
        return mProgress;
    }

    public void setSong(Song song) {
        mSong = song;
    }

    public void setLocallyAvailable(boolean locallyAvailable) {
        this.locallyAvailable = locallyAvailable;
    }

    public void setProgress(int progress) {
        mProgress = progress;
    }
}
