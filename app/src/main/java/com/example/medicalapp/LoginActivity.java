package com.example.medicalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medicalapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private Button btnSubmit;
    private EditText mEmail, mPassword;
    private Spinner Spinner;
    private String[] option;
    private ProgressDialog dialog;
    private TextView resetPassword;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private TextView textUserType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnSubmit = findViewById(R.id.btnSubmit);
        mEmail = findViewById(R.id.mEmail);
        mPassword = findViewById(R.id.mPassword);
        resetPassword = findViewById(R.id.resetPassword);
        textUserType = findViewById(R.id.textUserType);
        if (getIntent() != null) {
            String usertype = getIntent().getStringExtra("usertype");
            if (usertype != null) {
                textUserType.setText(usertype.toUpperCase());
            } else {
                textUserType.setText("DOCTOR");
            }
        } else {
            textUserType.setText("DOCTOR");
        }
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim().toLowerCase();
                String password = mPassword.getText().toString().trim().toLowerCase();
                if (email.length() > 11 && password.length() > 6) {
                    showDialog("verfying...");
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    verifyUserType(email, password);
                }
            }
        });
    }

    private void verifyUserType(final String email, final String password) {
        db.collection("users").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                hideDialog();
                if (task.isSuccessful()) {
                    User user = task.getResult().toObject(User.class);
                    if (user != null) {
                        if (!user.userType.equalsIgnoreCase(textUserType.getText().toString())) {
                            Snackbar.make(textUserType, "wrong user type selected", BaseTransientBottomBar.LENGTH_INDEFINITE).setAction("go back", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            }).show();
                        } else {
                            showDialog("authenticating");
                            doAuth(email, password);
                        }
                    } else {
                        userNotFound();
                    }
                } else {
                    userNotFound();
                }
            }
        });
    }

    private void userNotFound() {
        Snackbar.make(textUserType, "no user registered with this email id", BaseTransientBottomBar.LENGTH_INDEFINITE).setAction("register", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        }).show();
    }

    private void doAuth(String email, String password) {

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                hideDialog();
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Logged In successFully", Toast.LENGTH_SHORT).show();
                    final FirebaseUser user = task.getResult().getUser();
                    if (user.isEmailVerified()) {
                        updateUI(user);
                    } else {
                        Snackbar.make(textUserType, "Please check your email for the verfication code", BaseTransientBottomBar.LENGTH_INDEFINITE).setAction("resend", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                user.sendEmailVerification();
                            }
                        }).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            SharedPreferences userType = LoginActivity.this.getSharedPreferences("usertype", Context.MODE_PRIVATE);
            userType.edit().putString("type", textUserType.getText().toString().toLowerCase()).apply();
            startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
            finish();
        }


    }

    private void hideDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void showDialog(String msg) {
        dialog = new ProgressDialog(LoginActivity.this);
        dialog.setMessage(msg);
        dialog.setTitle("please wait");
        dialog.show();
    }
    @Override
    protected void onStop() {
        super.onStop();
        dialog = null;
    }
}




