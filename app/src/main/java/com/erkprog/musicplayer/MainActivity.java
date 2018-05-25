package com.erkprog.musicplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
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
        Toast.makeText(this, "" +isWriteStoragePermissionGranted(), Toast.LENGTH_SHORT).show();
        testWrite();

        btnPlay = findViewById(R.id.btnPlay);
        btnStop = findViewById(R.id.btnStop);
        seekBar = findViewById(R.id.seekBar);
        songCurrentTime = findViewById(R.id.songCurrentTime);
        songTotalTime = findViewById(R.id.songTotalTime);
        songCurrentTime.setText("0:00");
        songTotalTime.setText("0:00");


//            new DownloadFileFromURL().execute("http://hck.re/ZeSJFd");


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
                playSong(position);
            }

            @Override
            public void onDownloadClick(int position) {
//                mRecyclerViewAdapter.onDownloadClick(position);
                mPresenter.downloadSong(mRecyclerViewAdapter.getSong(position));

            }
        });
        recyclerView.setAdapter(mRecyclerViewAdapter);
    }

    private void playSong(int position) {
        Song song = mRecyclerViewAdapter.getSong(position);
        playerService = new Intent(MainActivity.this, PlayerInService.class);
        playerService.putExtra("songUrl", song.getUrl());
        playerService.putExtra("songName", song.getName());
        playerService.putExtra("songArtists", song.getArtists());
        Log.d(TAG, "onCreate: Starting service");
        startService(playerService);
    }

    @Override
    public void updateProgress(int progress) {
        seekBar.setProgress(progress);
    }

    @Override
    public void onFileDownloaded() {
        Toast.makeText(this, "File downloaded", Toast.LENGTH_SHORT).show();
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

    public  boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted2");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted2");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                Log.d(TAG, "External storage2");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                    Log.d(TAG, "onRequestPermissionsResult: Granted");
                    //resume tasks needing this permission
                    //downloadPdfFile();
                }else{
//                    progress.dismiss();
                    Log.d(TAG, "onRequestPermissionsResult: No permission");
                }
                break;


        }
    }

    private void testWrite(){
        try {
            String root = Environment.getExternalStorageDirectory().toString();
//            File myDir = new File(root + "/saved_images");
//            myDir.mkdirs();
//
//            String fname = "Image-"+ n +".jpg";
//            File file = new File (myDir, fname);

            File myFile = new File(root + "/tess.txt");
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append("Some text");
            myOutWriter.close();
            fOut.close();

        } catch (Exception e) {
            Log.e("ERRR", "Could not create file",e);
        }
    }


    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            Log.d(TAG, "doInBackground: starts");
            int count;
            try {
                URL url = new URL(expandUrl(f_url[0]));
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file
                OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().toString() + "/mm.mp3");

                byte data[] = new byte[1024];

                long total = 0;

                Log.d(TAG, "doInBackground: downloading");

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                Log.d(TAG, "doInBackground: finished");

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
//            pDialog.setProgress(Integer.parseInt(progress[0]));
            seekBar.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded

            // Displaying downloaded image into image view
            // Reading image path from sdcard
            String imagePath = Environment.getExternalStorageDirectory().toString() + "/downloadedfile.jpg";
            // setting downloaded into image view
        }

        public String expandUrl(String shortenedUrl) throws IOException {
            URL url = new URL(shortenedUrl);
            // open connection
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);

            // stop following browser redirect
            httpURLConnection.setInstanceFollowRedirects(false);

            // extract location header containing the actual destination URL
            String expandedURL = httpURLConnection.getHeaderField("Location");
            httpURLConnection.disconnect();

            return expandedURL;
        }
    }


}
