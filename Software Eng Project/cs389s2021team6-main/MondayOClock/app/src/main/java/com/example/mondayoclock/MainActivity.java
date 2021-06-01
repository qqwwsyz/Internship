package com.example.mondayoclock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    AlarmManager alarmManager;
    private PendingIntent pending_intent;
    private PendingIntent pendingintent2;
    private TimePicker alarmTimePicker;
    private TextView alarmTextView;
    MainActivity clock;
    Context context;
    static int bSource = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = this;
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.main_layout);
        //layout.setBackgroundResource(bSource);
        if(SettingPreview.cusBac == true){
            layout.setBackgroundResource(bSource);
        }
        alarmTextView = (TextView) findViewById(R.id.alarmText);
        final Intent myIntent = new Intent(this.context, AlarmReceiver.class);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        final Calendar calendar = Calendar.getInstance();
        alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);


        Button start_alarm= (Button) findViewById(R.id.start_alarm);
        start_alarm.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)

            @Override
            public void onClick(View v) {

                calendar.add(Calendar.SECOND,3);

                final int hour = alarmTimePicker.getCurrentHour();
                final int minute = alarmTimePicker.getCurrentMinute();;
                calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
                calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
                calendar.set(Calendar.SECOND, 0);

                myIntent.putExtra("extra", "yes");
                pending_intent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, 0);

                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending_intent);


                if(minute<10)
                    setInstruction("Alarm set to " + hour + ":0" + minute);
                else
                setInstruction("Alarm set to " + hour + ":" + minute);

            }

        });

    }


    public void setInstruction(String alarmText) {
        alarmTextView.setText(alarmText);
    }
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chat_item:
                Intent intent = new Intent(MainActivity.this,ChatActivity.class);
                startActivity(intent);
                break;
            case R.id.help_item:
                Toast.makeText(this, "email: admin@mondayoclock.com", Toast.LENGTH_SHORT).show();
                break;
            case R.id.share_item:
                Intent intent5 = new Intent(Intent.ACTION_SEND);
                intent5.setType("text/plain");
                intent5.putExtra(Intent.EXTRA_TEXT, "Tell your friend about us!");
                startActivity(Intent.createChooser(intent5,"Tell your friend about us!"));
                startActivity(intent5);
                break;
            case R.id.about_us:
                Toast.makeText(this, "Pace University cs389 spring 2021 team 6", Toast.LENGTH_SHORT).show();
                break;
            default:

        }
        return true;
    }
    @Override
    public void onStart() {
        super.onStart();
        clock = this;
    }
    public void gotoMenu(View v){
        Intent i = new Intent(this, BigMenu.class);
        startActivity(i);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.e("Clock Activity", "on Destroy");
    }

}


