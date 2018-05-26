package com.erkprog.musicplayer;


import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Random;

public class PlayerInService extends Service implements OnClickListener, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "PlayerInService";
    private static final int NOTIFICATION_ID = 82;
    private WeakReference<Button> btnPlay;
    private WeakReference<Button> btnStop;
    public static WeakReference<TextView> textSongCurrentTime;
    public static WeakReference<TextView> textSongTotalTime;
    public static WeakReference<SeekBar> songProgressBar;
    private NotificationHelper helper;
    private String mDataSource, songName, songArtists;
    static Handler progressBarHandler = new Handler();

    public static MediaPlayer mp;
    private boolean isPause = false;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: test");
        mp = new MediaPlayer();
        mp.reset();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

        helper = new NotificationHelper(this);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initUI();
        Log.d(TAG, "onStartCommand: Starts");
        boolean startOnResume = intent.getBooleanExtra("onResume", false);
        Log.d(TAG, "onStartCommand: " + startOnResume);
        if (!startOnResume) {
            mDataSource = intent.getStringExtra("songUrl");
            songName = intent.getStringExtra("songName");
            songArtists = intent.getStringExtra("songArtists");
            playSong();
        }

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initUI() {
        btnPlay = new WeakReference<>(MainActivity.btnPlay);
        btnStop = new WeakReference<>(MainActivity.btnStop);
        textSongCurrentTime = new WeakReference<>(MainActivity.songCurrentTime);
        textSongTotalTime = new WeakReference<>(MainActivity.songTotalTime);
        songProgressBar = new WeakReference<>(MainActivity.seekBar);
        songProgressBar.get().setOnSeekBarChangeListener(this);
        btnPlay.get().setOnClickListener(this);
        btnStop.get().setOnClickListener(this);
        mp.setOnCompletionListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPlay:
                Log.d(TAG, "onClick: Clicked button");

                if (mp.isPlaying()) {
                    Log.d(TAG, "onClick: ");
                    mp.pause();
                    isPause = true;
                    progressBarHandler.removeCallbacks(mUpdateTimeTask);
//                    btnPlay.get().setBackgroundResource(R.drawable.player);
                    return;
                }

                if (isPause) {
                    Log.d(TAG, "onClick: in isPause");
                    mp.start();
                    isPause = false;
                    updateProgressBar();
//                    btnPlay.get().setBackgroundResource(R.drawable.pause);
                    return;
                }

                if (!mp.isPlaying()) {
                    Log.d(TAG, "onClick: should PLay song");
                    playSong();
                }

                break;
            case R.id.btnStop:
                mp.stop();
                onCompletion(mp);
//                textViewSongTime.get().setText("0.00/0.00"); // Displaying time completed playing
                break;

        }
    }

    public void updateProgressBar() {
        try {
            progressBarHandler.postDelayed(mUpdateTimeTask, 100);
        } catch (Exception e) {
            Log.d(TAG, "updateProgressBar: error");
        }
    }

    static Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = 0;
            long currentDuration = 0;

            try {
                totalDuration = mp.getDuration();
                currentDuration = mp.getCurrentPosition();
//                textViewSongTime.get().setText(Utility.milliSecondsToTimer(currentDuration) + "/" + Utility.milliSecondsToTimer(totalDuration)); // Displaying time completed playing
                textSongCurrentTime.get().setText(Utility.milliSecondsToTimer(currentDuration));
                textSongTotalTime.get().setText(Utility.milliSecondsToTimer(totalDuration));
                int progress = (int) (Utility.getProgressPercentage(currentDuration, totalDuration));
                songProgressBar.get().setProgress(progress);    /* Running this thread after 100 milliseconds */
                progressBarHandler.postDelayed(this, 100);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    @Override
    public void onDestroy() {
        if (mp != null) mp.release();
        helper.getManager().cancel(NOTIFICATION_ID);
        Log.d(TAG, "onDestroy: Service on destroy");

    }

    // Play song
    public void playSong() {
        Log.d(TAG, "playSong: starts");
        progressBarHandler.removeCallbacks(mUpdateTimeTask);
        songProgressBar.get().setProgress(0);
        textSongCurrentTime.get().setText("0:00");
        textSongTotalTime.get().setText("0:00");
        showOnPlayNotification();

        try {
            mp.reset();
//            Uri myUri = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.bangla);
//            mp.setDataSource("http://hck.re/ZeSJFd");
            mp.setDataSource(mDataSource);
            mp.prepareAsync();
            mp.setOnPreparedListener(new OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    Log.d(TAG, "onPrepared: ready");
                    try {
                        mp.start();
                        updateProgressBar();
//                        btnPlay.get().setBackgroundResource(R.drawable.pause);
                    } catch (Exception e) {
                        Log.i("EXCEPTION", "" + e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showOnPlayNotification() {
        Notification.Builder builder = helper.getChannelNotification(songName, songArtists);
        helper.getManager().notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "onCompletion: triggered");
        textSongCurrentTime.get().setText("0.00");
        textSongTotalTime.get().setText("0.00");
        songProgressBar.get().setProgress(0);
        progressBarHandler.removeCallbacks(mUpdateTimeTask); /* Progress Update stop */
        helper.getManager().cancel(NOTIFICATION_ID);
//        btnPlay.get().setBackgroundResource(R.drawable.player);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        progressBarHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        progressBarHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = Utility.progressToTimer(seekBar.getProgress(), totalDuration);
        mp.seekTo(currentPosition);
        updateProgressBar();
    }
}