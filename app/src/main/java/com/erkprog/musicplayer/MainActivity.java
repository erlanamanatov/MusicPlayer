package com.erkprog.musicplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainActivityView {
    private static final String TAG = "MainActivity";

    private MainActivityPresenter mPresenter;
    private RecyclerViewAdapter mRecyclerViewAdapter;

    public static Button btnPlay, btnStop;
    public static SeekBar seekBar;
    public static TextView songCurrentTime, songTotalTime;
    private Intent playerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initRecyclerView();

        mPresenter = new MainActivityPresenter(this);
        mPresenter.loadSongs();

        btnPlay = findViewById(R.id.btnPlay);
        btnStop = findViewById(R.id.btnStop);
        seekBar = findViewById(R.id.seekBar);
        songCurrentTime = findViewById(R.id.songCurrentTime);
        songTotalTime = findViewById(R.id.songTotalTime);
        songCurrentTime.setText("0:00");
        songTotalTime.setText("0:00");

    }

    private void initRecyclerView() {
        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getBaseContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        mRecyclerViewAdapter = new RecyclerViewAdapter(this, new ArrayList<Song>());
        mRecyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Song song = mRecyclerViewAdapter.onItemClick(position);
                playerService = new Intent(MainActivity.this, PlayerInService.class);
                playerService.putExtra("songUrl", song.getUrl());
                playerService.putExtra("songName", song.getName());
                playerService.putExtra("songArtists", song.getArtists());
                Log.d(TAG, "onCreate: Starting service");
                startService(playerService);
            }

            @Override
            public void onDownloadClick(int position) {
                mRecyclerViewAdapter.onDownloadClick(position);
            }
        });
        recyclerView.setAdapter(mRecyclerViewAdapter);
    }

    @Override
    public void displaySongs(List<Song> songList) {
        Log.d(TAG, "displaySongs: songList " + songList.size());
        mRecyclerViewAdapter.loadNewData(songList);
    }

    @Override
    protected void onDestroy() {
        if (PlayerInService.mp != null) {
            if (!PlayerInService.mp.isPlaying()) {
                PlayerInService.mp.stop();
                stopService(playerService);
            } else {
//            btnPlay.setBackgroundResource(R.drawable.pause);
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
//                btnPlay.setBackgroundResource(R.drawable.player);

                } else {
//                btnPlay.setBackgroundResource(R.drawable.pause);
                }
            }
        } catch (Exception e) {
            Log.e("Exception", "" + e.getMessage() + e.getStackTrace() + e.getCause());
        }

        super.onResume();

    }
}
