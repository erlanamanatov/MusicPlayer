package com.erkprog.musicplayer;


import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.erkprog.musicplayer.utils.NotificationHelper;
import com.erkprog.musicplayer.utils.Utility;

import java.lang.ref.WeakReference;

public class PlayerInService extends Service implements OnClickListener, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "myLogs:PlayerInService";
    private static final int NOTIFICATION_ID = 82;
    private WeakReference<ImageButton> btnPlay;
    private WeakReference<ImageButton> btnStop;
    public static WeakReference<TextView> textSongCurrentTime;
    public static WeakReference<TextView> textSongTotalTime;
    public static WeakReference<TextView> playerSongName;
    public static WeakReference<TextView> playerSongArtists;
    public static WeakReference<SeekBar> songSeekBar;
    public static WeakReference<ProgressBar> connectionProgressBar;
    private NotificationHelper helper;
    private String mDataSource;
    private static String songName, songArtists;
    static Handler progressBarHandler = new Handler();

    public static MediaPlayer mp;
    private boolean isPause = false;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: starts");
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
        if (!startOnResume) {
            mDataSource = intent.getStringExtra("songUrl");
            songName = intent.getStringExtra("songName");
            songArtists = intent.getStringExtra("songArtists");
            Log.d(TAG, "onStartCommand: songName: " + songName + ", songArtists" + songArtists);
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
        playerSongName = new WeakReference<>(MainActivity.playerSongName);
        playerSongArtists = new WeakReference<>(MainActivity.playerSongArtists);
        songSeekBar = new WeakReference<>(MainActivity.seekBar);
        songSeekBar.get().setOnSeekBarChangeListener(this);
        connectionProgressBar = new WeakReference<>(MainActivity.progressBar);
        btnPlay.get().setOnClickListener(this);
        btnStop.get().setOnClickListener(this);
        mp.setOnCompletionListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPlay:
                Log.d(TAG, "onClick: btnPlayClicked");

                if (mp.isPlaying()) {
                    mp.pause();
                    Log.d(TAG, "onClick: mp pause");
                    isPause = true;
                    progressBarHandler.removeCallbacks(mUpdateTimeTask);
                    btnPlay.get().setBackgroundResource(R.drawable.play_img);
                    return;
                }

                if (isPause) {
                    mp.start();
                    Log.d(TAG, "onClick: mp start");
                    isPause = false;
                    updateProgressBar();
                    btnPlay.get().setBackgroundResource(R.drawable.pause_img);
                    return;
                }

                if (!mp.isPlaying()) {
                    playSong();
                }

                break;
            case R.id.btnStop:
                mp.stop();
                onCompletion(mp);
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
                textSongCurrentTime.get().setText(Utility.milliSecondsToTimer(currentDuration));
                textSongTotalTime.get().setText(Utility.milliSecondsToTimer(totalDuration));
                playerSongName.get().setText(songName != null ? songName : "");
                playerSongArtists.get().setText(songArtists != null ? songArtists : "");
                int progress = (int) (Utility.getProgressPercentage(currentDuration, totalDuration));
                songSeekBar.get().setProgress(progress);    /* Running this thread after 100 milliseconds */
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
        connectionProgressBar.get().setVisibility(View.VISIBLE);
        progressBarHandler.removeCallbacks(mUpdateTimeTask);
        songSeekBar.get().setProgress(0);
        textSongCurrentTime.get().setText("0:00");
        textSongTotalTime.get().setText("0:00");
        showOnPlayNotification();

        try {
            mp.reset();
            mp.setDataSource(mDataSource);
            mp.prepareAsync();
            Log.d(TAG, "playSong: prepareAsync");
            mp.setOnPreparedListener(new OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    connectionProgressBar.get().setVisibility(View.INVISIBLE);
                    Log.d(TAG, "onPrepared: ready");
                    try {
                        mp.start();
                        updateProgressBar();
                        btnPlay.get().setBackgroundResource(R.drawable.pause_img);
                    } catch (Exception e) {
                        Log.i("EXCEPTION", "" + e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            connectionProgressBar.get().setVisibility(View.INVISIBLE);
        }
    }

    private void showOnPlayNotification() {
        NotificationCompat.Builder builder = helper.getChannelNotification(songName, songArtists);
        helper.getManager().notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "onCompletion: triggered");
        textSongCurrentTime.get().setText("0.00");
        textSongTotalTime.get().setText("0.00");
        songSeekBar.get().setProgress(0);
        progressBarHandler.removeCallbacks(mUpdateTimeTask); /* Progress Update stop */
        helper.getManager().cancel(NOTIFICATION_ID);
        btnPlay.get().setBackgroundResource(R.drawable.play_img);
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