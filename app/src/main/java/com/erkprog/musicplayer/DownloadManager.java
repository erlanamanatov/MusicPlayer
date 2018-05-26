package com.erkprog.musicplayer;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

public class DownloadManager extends AsyncTask<Song, String, Song> {
    private static final String TAG = "DownloadManager";
    private OnDownloadStatusListener mOnDownloadStatusListener;
    private int mSongPosition;

    public interface OnDownloadStatusListener {
        void onFileDownloadFinished();
        void updateProgress(String progress);
        void onSongDownloaded(Song song, int songPosition);
    }


    public DownloadManager(OnDownloadStatusListener listener, int songPosition){
        mOnDownloadStatusListener = listener;
        mSongPosition = songPosition;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Song doInBackground(Song... songs) {
        Log.d(TAG, "doInBackground: starts");
        int count;
        try {
            URL url = new URL(expandUrl(songs[0].getUrl()));
            URLConnection conection = url.openConnection();
            conection.connect();
            int lenghtOfFile = conection.getContentLength();
            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().toString() + "/mm.mp3");

            byte data[] = new byte[1024];
            long total = 0;
            Log.d(TAG, "doInBackground: downloading");

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            Log.d(TAG, "doInBackground: finished");

            output.flush();
            output.close();
            input.close();

            return songs[0];

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

        return null;
    }

    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
        mOnDownloadStatusListener.updateProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(Song result) {
        result.setName("Downloaded");
//        mOnDownloadStatusListener.onFileDownloadFinished();
        mOnDownloadStatusListener.onSongDownloaded(result, mSongPosition);
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
}