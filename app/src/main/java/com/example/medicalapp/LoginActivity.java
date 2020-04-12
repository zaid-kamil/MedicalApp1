package com.example.medicalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private Button btnSubmit;
    private EditText mEmail,mPassword;
    private Spinner Spinner;
    private String[] option;
    private ProgressDialog Dialog;
    private TextView resetPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnSubmit = findViewById(R.id.btnSubmit);
        mEmail = findViewById(R.id.mEmail);
        mPassword = findViewById(R.id.mPassword);
        resetPassword = findViewById(R.id.resetPassword);
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
            }
        });
        Spinner = findViewById(R.id.Spinner);
        option = new String[]{"SELECT", "DOCTOR", "ORGANIZER"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_list_item_1, option);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner.setAdapter(typeAdapter);
        Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim().toLowerCase();
                String password = mPassword.getText().toString().trim().toLowerCase();
                if (email.length() > 11 && password.length() > 6) {
                    showDialog("registering...");
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    hideDialog();
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Logged In successFully", Toast.LENGTH_SHORT).show();
                                        updateUI(task.getResult().getUser());
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();



                                    }
                                }
                            });
                }

            }
        });


        return ;
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            SharedPreferences userType = LoginActivity.this.getSharedPreferences("user_type",
                    Context.MODE_PRIVATE);
            userType.edit().putString("person", option[Spinner.getSelectedItemPosition()])
                    .apply();
            startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
            finish();
        }


    }

    private void hideDialog() {
        if (Dialog != null && Dialog.isShowing()) {
            Dialog.dismiss();
        }
    }

    private void showDialog(String msg) {
        Dialog = new ProgressDialog(LoginActivity.this);
        Dialog.setMessage(msg);
        Dialog.setTitle("please wait");
        Dialog.show();
    }

}




