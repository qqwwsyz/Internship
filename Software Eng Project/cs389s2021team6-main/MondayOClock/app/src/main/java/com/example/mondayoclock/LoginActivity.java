package com.example.mondayoclock;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText enterName;
    private EditText enterPassword;
    private Button enterLogin;
    String userName = "";
    String userPassword = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        enterName = findViewById(R.id.etName);
        enterPassword = findViewById(R.id.etPassword);
        enterLogin = findViewById(R.id.btnLogin);

        enterLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userName = enterName.getText().toString();
                userPassword = enterPassword.getText().toString();

                if(userName.isEmpty() || userPassword.isEmpty())
                {
                    Toast.makeText(LoginActivity.this, "Please enter name and password!", Toast.LENGTH_LONG).show();

                }else {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });


    }


}
