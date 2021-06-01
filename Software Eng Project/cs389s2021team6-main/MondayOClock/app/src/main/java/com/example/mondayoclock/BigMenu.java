package com.example.mondayoclock;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BigMenu extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.big_menu);
    }

    public void gotoTodoList(View view) {
        Intent i = new Intent(this, TodoList.class);
        startActivity(i);

    }

    public void gotoWeather(View view) {
        Intent i = new Intent(this, Weather.class);
        startActivity(i);
    }
    public void gotoSetting(View view) {
        Intent i = new Intent(this, Setting.class);
        startActivity(i);
    }
    public void gotoMain(View view){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
    public void gotoChat(View view) {
        Intent i = new Intent(this, ChatActivity.class);
        startActivity(i);
    }
    public void gotoNews(View view) {
        Intent i = new Intent(this,News.class);
        startActivity(i);
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
                Intent intent = new Intent(BigMenu.this,ChatActivity.class);
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
}
