package com.example.mondayoclock;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ClipDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class SettingDetail extends AppCompatActivity {
    static int songID = 1;
    static int settingID = 0;
    String[] listString1;
    int [] image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_setting);

        Intent mIntent = getIntent();
        int intValue = mIntent.getIntExtra("intVariableName", 0);


        switch (intValue) {
            case 0:
                settingID = 1;
                listString1= new String[]{"Ringtone 01", "Ringtone 02","Ringtone 03","Ringtone 04", "Ringtone 05",
                "Ringtone 06", "Ringtone 07"};
                image = new int[]{R.drawable.aap, R.drawable.a2p, R.drawable.a3p, R.drawable.a4p,R.drawable.a5p,
                R.drawable.a6p,R.drawable.a7p};
                break;
            case 1:
                settingID = 2;
                listString1 = new String[]{"", "","","","", "","",""};
                image = new int[]{R.drawable.bg01, R.drawable.bg02,R.drawable.bg03,R.drawable.bg04,R.drawable.bg05,
                        R.drawable.bg06,R.drawable.bg07,R.drawable.bg08};

                break;


        }

        ListView list=(ListView) findViewById(R.id.listView1);
        CustomAdaptor customAdaptor=new CustomAdaptor();
        list.setAdapter(customAdaptor);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent();
                myIntent.setClass(SettingDetail.this, SettingPreview.class);
                myIntent.putExtra("intVariableName", position);
                startActivity(myIntent);
            }
        });

    }

    class CustomAdaptor extends BaseAdapter {

        @Override
        public int getCount() {
            return listString1.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
/*
        @Override
        public View getView(int position, View view, ViewGroup parent) {
            view = getLayoutInflater().inflate(R.layout.setting_change_layout,null);
            TextView text1=(TextView) view.findViewById(R.id.textView4);
            TextView text2=(TextView) view.findViewById(R.id.textView5);
            ImageView img=  view.findViewById(R.id.img_view);
            img.setImageResource(image[position]);
            text1.setText(listString1[position]);
            return view;
        }*/
@Override
public View getView(int position, View view, ViewGroup parent) {
    view = getLayoutInflater().inflate(R.layout.listlayout,null);
    TextView text1=(TextView) view.findViewById(R.id.textView2);
    TextView text2=(TextView) view.findViewById(R.id.textView3);
    ImageView img=  view.findViewById(R.id.imageView);
    img.setImageResource(image[position]);
    text1.setText(listString1[position]);
    return view;
}
    }

}