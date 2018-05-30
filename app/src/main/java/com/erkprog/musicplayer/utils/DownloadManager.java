package com.erkprog.musicplayer.utils;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.erkprog.musicplayer.model.SongItem;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

public class DownloadManager extends AsyncTask<String, Integer, String> {
    private static final String TAG = "myLogs:DownloadManager";
    private OnDownloadStatusListener mOnDownloadStatusListener;
    private int mSongPosition;
    private SongItem mSongItem;

    public interface OnDownloadStatusListener {
        void updateSongProgress(int songItemPosition);
        void onSongDownloaded(int songItemPosition);
    }


    public DownloadManager(SongItem songItem, int songPosition, OnDownloadStatusListener listener){
        mSongItem = songItem;
        mOnDownloadStatusListener = listener;
        mSongPosition = songPosition;
    }

    public void download(){
        this.execute(mSongItem.getSong().getSongSource());
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... songUrl) {
        Log.d(TAG, "doInBackground: starts");
        int count;
        try {
            URL url = new URL(expandUrl(songUrl[0]));
            URLConnection conection = url.openConnection();
            conection.connect();
            int lenghtOfFile = conection.getContentLength();
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            File dir = new File(Environment.getExternalStorageDirectory(), "Test Music Player");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            OutputStream output = new FileOutputStream(dir.toString()
                    + "/" + mSongItem.getSong().getName() + " - " + mSongItem.getSong().getArtists()
                    + ".mp3");

            byte data[] = new byte[1024];
            long total = 0;
            Log.d(TAG, "doInBackground: downloading");

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
//                publishProgress((int) ((total * 100) / lenghtOfFile));

                int progress = (int) ((total * 100) / lenghtOfFile);
                if (progress % 10 == 0 || progress == 1 ||progress ==6) {
                    publishProgress(progress);
                }

                // writing data to file
                output.write(data, 0, count);
            }

            Log.d(TAG, "doInBackground: finished");

            output.flush();
            output.close();
            input.close();

            return "some result";

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }
        return null;
    }

    protected void onProgressUpdate(Integer... progress) {
        // setting progress percentage
        mSongItem.setProgress(progress[0]);
        mOnDownloadStatusListener.updateSongProgress(mSongPosition);
    }

    @Override
    protected void onPostExecute(String result) {
        mSongItem.setLocallyAvailable(true);
        mOnDownloadStatusListener.onSongDownloaded(mSongPosition);
    }

    private String expandUrl(String shortenedUrl) throws IOException {
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

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
