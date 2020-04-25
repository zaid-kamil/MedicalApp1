package com.example.medicalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button btnDocLogin = findViewById(R.id.btnDocLogin);
        Button btnOrgLogin = findViewById(R.id.btnOrgLogin);
        Button btnCreateAccount = findViewById(R.id.btnCreateAccount);
        TextView textForgot = findViewById(R.id.textForgot);

        btnCreateAccount.setOnClickListener(this);
        btnDocLogin.setOnClickListener(this);
        btnOrgLogin.setOnClickListener(this);
        textForgot.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btnDocLogin:
                intent.setClass(this, LoginActivity.class);
                intent.putExtra("usertype", "doctor");
                startActivity(intent);

                break;
            case R.id.btnOrgLogin:
                intent.setClass(this, LoginActivity.class);
                intent.putExtra("usertype", "organizer");
                startActivity(intent);

                break;
            case R.id.btnCreateAccount:
                intent.setClass(this, SignUpActivity.class);
                startActivity(intent);
                break;
            case R.id.textForgot:
                intent.setClass(this, ForgotPasswordActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
