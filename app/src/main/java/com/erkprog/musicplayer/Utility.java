package com.erkprog.musicplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Created by masum on 09/12/2015.
 */
public class Utility {
    //Notification
    // Set up the notification ID
    public static final int NOTIFICATION_ID = 1;
    @SuppressWarnings("unused")
    public static NotificationManager mNotificationManager;

    // Create Notification
//    @SuppressWarnings("deprecation")
    public static void initNotification(String songTitle, Context mContext) {
        try {
            String ns = Context.NOTIFICATION_SERVICE;
            mNotificationManager = (NotificationManager) mContext.getSystemService(ns);
            int icon = R.drawable.place_holder;
            CharSequence tickerText = "Playing your song";
            long when = System.currentTimeMillis();

//            Notification notification = new Notification(icon, tickerText, when);
//            notification.flags = Notification.FLAG_ONGOING_EVENT;
            Context context = mContext.getApplicationContext();
//            CharSequence songName = "" + songTitle;
//
            Intent notificationIntent = new Intent(mContext, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
//            notification.setLatestEventInfo(context, songName, null, contentIntent);
//            mNotificationManager.notify(NOTIFICATION_ID, notification);

            Notification.Builder builder = new Notification.Builder(mContext);
            builder.setTicker("ticker text")
                    .setContentTitle("My notification")
                    .setContentText("content text")
                    .setSmallIcon(R.drawable.place_holder)
                    .setContentIntent(contentIntent)
                    .setOngoing(true)
                    .setSubText("this is subtext")
                    .setNumber(100)
                    .build();

            Notification notification = builder.getNotification();
            mNotificationManager.notify(NOTIFICATION_ID, notification);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = (double) 0;
        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        // return percentage
        return percentage.intValue();
    }

    public static int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }

    public static String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        //Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Pre appending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

}
