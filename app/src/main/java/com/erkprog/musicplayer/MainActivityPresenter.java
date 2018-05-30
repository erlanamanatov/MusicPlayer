package com.erkprog.musicplayer;

import android.util.Log;

import com.erkprog.musicplayer.model.Song;
import com.erkprog.musicplayer.model.SongItem;
import com.erkprog.musicplayer.model.repositories.SongsRepository;
import com.erkprog.musicplayer.model.repositories.local.DatabaseSongRepository;
import com.erkprog.musicplayer.model.repositories.remote.ServerSongsRepository;

import java.util.List;

public class MainActivityPresenter implements SongsRepository.OnFinishedListener,
        DatabaseSongRepository.OnTrackDownloadListener, DatabaseSongRepository.OnCoverImageDownloadListener{
    private static final String TAG = "myLogs:Presenter";

    private MainActivityView view;
    private ServerSongsRepository mServerSongRepository;
    private DatabaseSongRepository mDatabaseSongRepository;

    public MainActivityPresenter(MainActivityView view, DatabaseSongRepository dbSongRepository) {
        this.view = view;
        mServerSongRepository = new ServerSongsRepository();
        mDatabaseSongRepository = dbSongRepository;
    }

    /*
    getting data from repositories
     */
    void loadSongs() {
        mServerSongRepository.getSongList(this);
        mDatabaseSongRepository.getSongList(this);
    }

    //List<Song> returned from repositories
    @Override
    public void onFinished(List<Song> songList) {
        if (!songList.isEmpty()){
            for (Song song : songList) {
                Log.d(TAG, "onFinished: " + song.toString());
                if (song.getImageSource().contains("http")){
                    if (!mDatabaseSongRepository.isSongLocallyAvailable(song)){
                        view.addItem(new SongItem(song, false));
                    }
                } else {
                    view.addItem(new SongItem(song, true));
                }
            }
        }
    }


    @Override
    public void onFailure(Throwable t) {
        if (t != null) {
            Log.d(TAG, "onFailure: " + t.getMessage());
            view.showToast("Failure receiving network data");
        }
    }

    /*
    Downloading song
    Download mp3file ----> download cover image -----> save data to local Database -----> change songItem in recyclerView and notify adapter
     */

    void downloadSong(SongItem songItem, int position){
        mDatabaseSongRepository.downloadMp3Track(songItem, position, this);
    }

    @Override
    public void onMp3TrackDownloadComplete(SongItem songItem, int position, String mp3FilePath) {
        mDatabaseSongRepository.downloadCoverImg(songItem, position, mp3FilePath, this);

    }

    @Override
    public void onMp3TrackDownloadError(SongItem songItem, int songItemPosition) {
        songItem.setProgress(0);
        view.updateSongItem(songItemPosition);
    }

    @Override
    public void onMp3TrackDownloadProgress(SongItem songItem, int songItemPosition, int progress) {
        songItem.setProgress(progress);
        view.updateSongItem(songItemPosition);
    }

    @Override
    public void onCoverImageDownloadComplete(SongItem songItem, int position, String mp3FilePath, String coverImgFilePath) {
        if (mDatabaseSongRepository.addSongToDB(songItem.getSong(), mp3FilePath, coverImgFilePath)) {
            songItem.getSong().setSongSource(mp3FilePath);
            songItem.getSong().setImageSource(coverImgFilePath);
            songItem.setLocallyAvailable(true);
            view.updateSongItem(position);
        } else {
            view.showToast("Error on saving song to DB.");
        }
    }

    @Override
    public void onCoverImageDownloadError(SongItem songItem, int songItemPosition) {
        songItem.setProgress(0);
        view.showToast("Downloading Error");
        view.updateSongItem(songItemPosition);
    }
}
