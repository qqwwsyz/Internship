package com.example.mondayoclock;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Button send_btn,receive_btn;
    EditText type_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        send_btn = findViewById(R.id.send_btn);
        receive_btn = findViewById(R.id.receive_btn);
        type_message = findViewById(R.id.type_message);

        final ArrayList<MessageModel> messagesList = new ArrayList<>();

//        for (int i=0;i<10;i++) {
//            messagesList.add(new MessageModel("Hi", true));
//        }

        final CustomAdapter adapter = new CustomAdapter(this, messagesList);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageModel message = new MessageModel(type_message.getText().toString(),true);
                messagesList.add(message);
                adapter.notifyDataSetChanged();
                type_message.setText("");

                Toast.makeText(ChatActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();

            }
        });

        receive_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageModel message = new MessageModel(type_message.getText().toString(),false);
                messagesList.add(message);
                adapter.notifyDataSetChanged();
                type_message.setText("");

                Toast.makeText(ChatActivity.this, "Message received", Toast.LENGTH_SHORT).show();

            }
        });
    }
}