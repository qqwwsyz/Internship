package com.example.mondayoclock;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class SettingPreview extends MainActivity {
int num;
static boolean cusBac = false;
String ringtoneSetting = "Ringtone set to #";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ringtone_preview);

        Intent mIntent = getIntent();
        int intValue = mIntent.getIntExtra("intVariableName", 0);
        if(SettingDetail.settingID == 1) {
            num = intValue;
            SettingDetail.songID = num + 1;
            ringtoneSetting += SettingDetail.songID;
            Toast.makeText(this, ringtoneSetting, Toast.LENGTH_LONG).show();
        }
        else if(SettingDetail.settingID == 2){
            cusBac = true;
            switch (intValue){
                case 0:
                    MainActivity.bSource = R.drawable.bg01;
                    break;
                case 1:MainActivity.bSource = R.drawable.bg02;
                    break;
                case 2:MainActivity.bSource = R.drawable.bg03;
                    break;
                case 3:MainActivity.bSource = R.drawable.bg04;
                    break;
                case 4:MainActivity.bSource = R.drawable.bg05;
                    break;
                case 5:MainActivity.bSource = R.drawable.bg06;
                    break;
                case 6:MainActivity.bSource = R.drawable.bg07;
                    break;
                case 7:MainActivity.bSource = R.drawable.bg08;
                    break;
                default:
                    MainActivity.bSource = R.drawable.abd;
                    break;
            }
            //layout.setBackgroundResource(R.drawable.a3p);

        }
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);

    }

    public void confirm(View v){
        SettingDetail.songID=num+1;
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

}