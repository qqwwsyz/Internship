package com.example.mondayoclock;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.Random;

public class RingtoneService extends Service {

    private boolean isRunning;
    private Context context;
    static MediaPlayer mMediaPlayer;
    private int startId;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("MyActivity", "Monday O'clock");
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        final NotificationManager mNM = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);

        Intent intent1 = new Intent(this.getApplicationContext(), MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent1, 0);

        Notification mNotify  = new Notification.Builder(this)
                .setContentTitle("wait up" + "!")
                .setContentText("Click me!")
                .setSmallIcon(R.drawable.smile)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();

        String state = intent.getExtras().getString("extra");

        Log.e("what is going on here  ", state);

        assert state != null;
        switch (state) {
            case "no":
                startId = 0;
                break;
            case "yes":
                startId = 1;
                break;
            default:
                startId = 0;
                break;
        }


        if(!this.isRunning && startId == 1) {
            Log.e("if there was not sound ", " and you want start");

            int min = 1;
            int max = 9;

            Random r = new Random();
/*
            switch(SettingDetail.songID){
                case 1: mMediaPlayer = MediaPlayer.create(this, R.raw.aa);
                break;
                case 2: mMediaPlayer = MediaPlayer.create(this, R.raw.a2);
                break;
                case 3: mMediaPlayer = MediaPlayer.create(this, R.raw.a3);
                break;
                case 4: mMediaPlayer = MediaPlayer.create(this, R.raw.a4);
                    break;
                case 5: mMediaPlayer = MediaPlayer.create(this, R.raw.a5);
                    break;
                case 6: mMediaPlayer = MediaPlayer.create(this, R.raw.a6);
                    break;
                case 7: mMediaPlayer = MediaPlayer.create(this, R.raw.a7);
                    break;
                default: mMediaPlayer = MediaPlayer.create(this, R.raw.aa);
                break;
            }


            mMediaPlayer.start();
*/

            mNM.notify(0, mNotify);

            this.isRunning = true;
            this.startId = 0;

        }
        else if (!this.isRunning && startId == 0){
            Log.e("if there was not sound ", " and you want end");

            this.isRunning = false;
            this.startId = 0;

        }

        else if (this.isRunning && startId == 1){
            Log.e("if there is sound ", " and you want start");

            this.isRunning = true;
            this.startId = 0;

        }
        else {
            Log.e("if there is sound ", " and you want end");

            mMediaPlayer.stop();
            mMediaPlayer.reset();

            this.isRunning = false;
            this.startId = 0;
        }


        Log.e("MyActivity", "In the service");

        return START_NOT_STICKY;

    }


    @Override
    public void onDestroy() {
        Log.e("JSLog", "on destroy called");
        super.onDestroy();

        this.isRunning = false;
    }







}
