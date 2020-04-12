package com.example.medicalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private Button btnSubmit;
    private EditText editResetEmail;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_forgot_password);
        editResetEmail = findViewById(R.id.editResetEmail);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userEmail = editResetEmail.getText().toString();
                if(TextUtils.isEmpty(userEmail))
                {
                    Toast.makeText(ForgotPasswordActivity.this, "please write your valid email address....", Toast.LENGTH_SHORT).show();
                }
                else {
                    mAuth.sendPasswordResetEmail(userEmail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful()){
                                        Toast.makeText(ForgotPasswordActivity.this, "please check your email account...", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ForgotPasswordActivity.this,LoginActivity.class));

                                    }
                                    else{
                                        String message = task.getException().getMessage();
                                        Toast.makeText(ForgotPasswordActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                    }


                                }
                            });
                }
            }
        });

    }
}
