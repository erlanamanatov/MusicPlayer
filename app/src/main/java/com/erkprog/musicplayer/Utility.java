package com.erkprog.musicplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import static android.content.Context.NOTIFICATION_SERVICE;

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
            Context context = mContext.getApplicationContext();
            Intent notificationIntent = new Intent(mContext, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), notificationIntent, 0);

//            notification.setLatestEventInfo(context, songName, null, contentIntent);
//            mNotificationManager.notify(NOTIFICATION_ID, notification);

//            Notification.Builder builder = new Notification.Builder(context);
//            builder.setTicker("ticker text")
//                    .setContentTitle("My notification")
//                    .setContentText("content text")
//                    .setSmallIcon(R.drawable.place_holder)
//                    .setContentIntent(contentIntent)
//                    .setOngoing(true)
//                    .setSubText("this is subtext")
//                    .setNumber(100);
//
//            Notification notification = builder.build();
//            mNotificationManager.notify(NOTIFICATION_ID, notification);

            Notification n = new Notification.Builder(mContext.getApplicationContext())
                    .setContentTitle("Title text")
                    .setContentText("content text")
                    .setSmallIcon(R.drawable.place_holder)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .build();

            NotificationManager notificationManager =
                    (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);

            notificationManager.notify(0, n);




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
