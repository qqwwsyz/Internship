package com.example.mondayoclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.mondayoclock.Model.NotificationHelper;


public class AlarmReceiver extends BroadcastReceiver {
    //static Ringtone ringtone;
    static MediaPlayer mMediaPlayer;
    public int ringtoneID = 1;
    public void setRingtoneID(int num){
        ringtoneID = num;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        notificationHelper.getManager().notify(1, nb.build());
            Intent i=new Intent();
            i.setClassName("com.example.mondayoclock","com.example.mondayoclock.MathQuizActivity");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Toast.makeText(context, "Alarm On", Toast.LENGTH_LONG).show();
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            //ringtone = RingtoneManager.getRingtone(context, alarmUri);
            //mMediaPlayer = MediaPlayer.create(context, R.raw.aa);
            //ringtone.play();

            switch(SettingDetail.songID){
                case 1: mMediaPlayer = MediaPlayer.create(context, R.raw.aa);
                    break;
                case 2: mMediaPlayer = MediaPlayer.create(context, R.raw.a2);
                    break;
                case 3: mMediaPlayer = MediaPlayer.create(context, R.raw.a3);
                    break;
                case 4: mMediaPlayer = MediaPlayer.create(context, R.raw.a4);
                    break;
                case 5: mMediaPlayer = MediaPlayer.create(context, R.raw.a5);
                    break;
                case 6: mMediaPlayer = MediaPlayer.create(context, R.raw.a6);
                    break;
                case 7: mMediaPlayer = MediaPlayer.create(context, R.raw.a7);
                    break;
                default: mMediaPlayer = MediaPlayer.create(context, R.raw.aa);
                    break;

            }
        mMediaPlayer.start();
            String state = intent.getExtras().getString("extra");

            context.startActivity(i);



    }
}

/*
public class AlarmReceiver extends BroadcastReceiver {
static int num;
    @Override
    public void onReceive(Context context, Intent intent) {

        String state = intent.getExtras().getString("extra");
        Log.e("Alarm activity", "In receiver " + state);
        Intent serviceIntent = new Intent(context, RingtoneService.class);
        serviceIntent.putExtra("extra", state);

        context.startService(serviceIntent);

        try {
            Intent i=new Intent();
            i.setClassName("com.example.mondayoclock","com.example.mondayoclock.MathQuizActivity");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Toast.makeText(context, "Alarm On", Toast.LENGTH_LONG).show();
            context.startActivity(i);
        }catch (Exception e){
            System.out.println("Caught exception: "+e);
        }
        System.out.println(SettingDetail.songID);
    }
}
*/