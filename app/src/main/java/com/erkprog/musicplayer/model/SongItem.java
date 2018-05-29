package com.erkprog.musicplayer.model;

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

    public void setLocallyAvailable(boolean locallyAvailable) {
        this.locallyAvailable = locallyAvailable;
    }

    public void setProgress(int progress) {
        mProgress = progress;
    }
}
