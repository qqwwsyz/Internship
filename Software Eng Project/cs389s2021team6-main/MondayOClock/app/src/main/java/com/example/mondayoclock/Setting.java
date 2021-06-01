package com.example.mondayoclock;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Setting extends AppCompatActivity {
    int [] image;
    String [] listString1= {"Ringtone","Theme"};
    String [] listString2={"Set ringtone","Set Theme"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        image = new int[]{R.drawable.ic_rington,R.drawable.ic_image};

        Intent mIntent = getIntent();
        int intValue = mIntent.getIntExtra("intVariableName", 0);

        ListView list=(ListView) findViewById(R.id.listView);
        CustomAdaptor customAdaptor=new CustomAdaptor();
        list.setAdapter(customAdaptor);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent();
                myIntent.setClass(Setting.this, SettingDetail.class);
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

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            view = getLayoutInflater().inflate(R.layout.ringtone_selection,null);
            TextView text1=(TextView) view.findViewById(R.id.textView2);
            TextView text2=(TextView) view.findViewById(R.id.textView3);
            ImageView img=  view.findViewById(R.id.imageView3);
            img.setImageResource(image[position]);
            text1.setText(listString1[position]);
            text2.setText(listString2[position]);
            return view;
        }
    }
}


