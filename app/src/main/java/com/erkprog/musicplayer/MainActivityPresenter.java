package com.erkprog.musicplayer;

import android.util.Log;

import com.erkprog.musicplayer.model.Song;
import com.erkprog.musicplayer.model.SongItem;
import com.erkprog.musicplayer.model.repositories.SongsRepository;
import com.erkprog.musicplayer.model.repositories.local.DatabaseSongRepository;
import com.erkprog.musicplayer.model.repositories.remote.ServerSongsRepository;
import com.erkprog.musicplayer.utils.DownloadManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivityPresenter implements SongsRepository.OnFinishedListener,
        DownloadManager.OnDownloadStatusListener, DatabaseSongRepository.OnTrackDownloadListener, DatabaseSongRepository.OnCoverImageDownloadListener{
    private static final String TAG = "MainActivityPresenter";

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
    }

    @Override
    public void onFinished(List<Song> songList) {
        List<SongItem> songItems = new ArrayList<>();
        for (Song song : songList) {
            Log.d(TAG, "onFinished: " + song.toString());
            songItems.add(new SongItem(song));
        }

        view.displaySongs(songItems);
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
//        songItem.getSong().setUrl(filePath);
//        songItem.setLocallyAvailable(true);
//        view.updateSongItem(position);
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
        songItem.getSong().setUrl(mp3FilePath);
        songItem.getSong().setImageUrl(coverImgFilePath);
        songItem.setLocallyAvailable(true);
        view.updateSongItem(position);
    }

    @Override
    public void onCoverImageDownloadError(int songItemPosition) {
        view.showToast("Downloading Error");
        view.updateSongItem(songItemPosition);
    }
}
