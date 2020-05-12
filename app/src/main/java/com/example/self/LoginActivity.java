package com.example.self;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button loginButton,createAccountButtonLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.email_sign_in_button);
        createAccountButtonLogin = findViewById(R.id.create_account_button_login);
        loginButton.setOnClickListener(this);
        createAccountButtonLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.email_sign_in_button:
                //
                break;
            case R.id.create_account_button_login:
                startActivity(new Intent(LoginActivity.this,CreateAccountActivity.class));
                break;
        }

    }
}
