package com.erkprog.musicplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainActivityView{
    private static final String TAG = "MainActivity";

    private MainActivityPresenter mPresenter;
    private RecyclerViewAdapter mRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initRecyclerView();

        mPresenter = new MainActivityPresenter(this);
        mPresenter.loadSongs();
    }

    private void initRecyclerView(){
        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerViewAdapter = new RecyclerViewAdapter(this, new ArrayList<Song>());
        mRecyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mRecyclerViewAdapter.onItemClick(position);
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
        Log.d(TAG, "displaySongs: songList " +songList.size());
        mRecyclerViewAdapter.loadNewData(songList);
    }
}
