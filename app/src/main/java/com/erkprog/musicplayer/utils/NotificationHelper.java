package com.erkprog.musicplayer.utils;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.erkprog.musicplayer.MainActivity;
import com.erkprog.musicplayer.R;

public class NotificationHelper extends ContextWrapper {
    private final static String CHANNEL_ID = "com.test.MusicPlayer";
    private final static String CHANNEL_NAME = "Test MusicPlayer";
    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        createChannel();
    }

    private void createChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(false);
            channel.enableVibration(false);
            channel.setSound(null, null);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            getManager().createNotificationChannel(channel);
        }
    }

    public NotificationManager getManager() {
        if (manager == null)
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    public NotificationCompat.Builder getChannelNotification(String title, String body) {
        Context context = getApplicationContext();
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), notificationIntent, 0);

            return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setContentText(body)
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.place_holder)
                    .setContentIntent(contentIntent)
                    .setOngoing(true)
                    .setAutoCancel(false);

    }
}
