package com.erkprog.musicplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.erkprog.musicplayer.model.Song;
import com.erkprog.musicplayer.model.SongItem;
import com.erkprog.musicplayer.model.repositories.local.DatabaseSongRepository;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainActivityView {
    private static final String TAG = "myLogs:MainActivity";

    private MainActivityPresenter mPresenter;
    private RecyclerViewAdapter mRecyclerViewAdapter;

    public static ImageButton btnPlay, btnStop;
    public static SeekBar seekBar;
    public static TextView songCurrentTime, songTotalTime, playerSongName, playerSongArtists;
    private Intent playerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initRecyclerView();

        btnPlay = findViewById(R.id.btnPlay);
        btnStop = findViewById(R.id.btnStop);
        seekBar = findViewById(R.id.seekBar);
        songCurrentTime = findViewById(R.id.songCurrentTime);
        songTotalTime = findViewById(R.id.songTotalTime);
        playerSongName = findViewById(R.id.player_song_name);
        playerSongArtists = findViewById(R.id.player_song_artists);
        songCurrentTime.setText("0:00");
        songTotalTime.setText("0:00");
        playerSongName.setText("");
        playerSongArtists.setText("");

        isWriteStoragePermissionGranted();
        mPresenter = new MainActivityPresenter(this, new DatabaseSongRepository(getApplication()));
        mPresenter.loadSongs();
    }

    private void initRecyclerView() {

        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getBaseContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        mRecyclerViewAdapter = new RecyclerViewAdapter(this, new ArrayList<SongItem>());
        mRecyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                playSong(position);
            }

            @Override
            public void onDownloadClick(int position) {
                if (isWriteStoragePermissionGranted()) {
                    mPresenter.downloadSong(mRecyclerViewAdapter.getSongItem(position), position);
                }
            }
        });
        recyclerView.setAdapter(mRecyclerViewAdapter);
    }

    private void playSong(int position) {
        Song song = mRecyclerViewAdapter.getSongItem(position).getSong();
        playerSongName.setText(song.getName());
        playerSongArtists.setText(song.getArtists());
        playerService = new Intent(MainActivity.this, PlayerInService.class);
        playerService.putExtra("songUrl", song.getSongSource());
        playerService.putExtra("songName", song.getName());
        playerService.putExtra("songArtists", song.getArtists());
        Log.d(TAG, "onCreate: Starting service");
        startService(playerService);
    }

    @Override
    public void updateSongProgress(int position) {
        mRecyclerViewAdapter.notifyItemChanged(position);
    }

    @Override
    public void updateSongItem(int songItemPosition) {
        mRecyclerViewAdapter.notifyItemChanged(songItemPosition);
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void displaySongs(List<SongItem> songItems) {
        Log.d(TAG, "displaySongs: songList " + songItems.size());
        mRecyclerViewAdapter.loadNewData(songItems);
    }

    @Override
    protected void onDestroy() {
        if (PlayerInService.mp != null) {
            if (!PlayerInService.mp.isPlaying()) {
                PlayerInService.mp.stop();
                stopService(playerService);
            } else {
            btnPlay.setBackgroundResource(R.drawable.pause_img);
            }
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        try {
            playerService = new Intent(MainActivity.this, PlayerInService.class);
            playerService.putExtra("onResume", true);
            startService(playerService);
            if (PlayerInService.mp != null) {
                if (!PlayerInService.mp.isPlaying()) {
                btnPlay.setBackgroundResource(R.drawable.play_img);

                } else {
                btnPlay.setBackgroundResource(R.drawable.pause_img);
                }
            }
        } catch (Exception e) {
            Log.e("Exception", "" + e.getMessage() + e.getCause());
        }

        super.onResume();

    }

    public  boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Write Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Write Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Write Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Write Permission: "+permissions[0]+ " was "+grantResults[0]);
                    Log.d(TAG, "onRequestPermissionsResult: Granted");
                }else{
                    Log.d(TAG, "onRequest Write PermissionsResult: No permission");
                }
                break;
        }
    }

}
