package com.example.medicalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.medicalapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

    private EditText editName, editEmail, editPassword, editRePassword, editPhoneNo_, editLicense;
    private Spinner spn;
    private Button btnSubmit;
    private String[] option;
    private ProgressDialog dialog;
    private FirebaseAuth auth;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnSubmit = findViewById(R.id.btnSubmit);
        editLicense = findViewById(R.id.editLicense);
        editRePassword = findViewById(R.id.editRePassword);
        editEmail = findViewById(R.id.editEmail);
        editName = findViewById(R.id.editName);
        editPhoneNo_ = findViewById(R.id.editPhoneNo_);
        spn = findViewById(R.id.Spinner);
        editPassword = findViewById(R.id.editPassword);
        option = new String[]{"SELECT", "DOCTOR", "ORGANIZER"};
        ArrayAdapter<String> typeadapter = new ArrayAdapter<>(SignUpActivity.this, android.R.layout.simple_list_item_1, option);
        typeadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn.setAdapter(typeadapter);
        spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==2) {
                    editLicense.animate().alpha(0).setDuration(200).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            editLicense.setVisibility(View.GONE);
                        }
                    }).start();
                }else if(position == 1){
                    editLicense.animate().alpha(1).setDuration(200).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            editLicense.setVisibility(View.VISIBLE);
                        }
                    }).start();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = editEmail.getText().toString().trim().toLowerCase();
                final String password = editPassword.getText().toString().trim().toLowerCase();
                String Repassword = editRePassword.getText().toString().trim().toLowerCase();
                final String phoneno = editPhoneNo_.getText().toString().trim().toLowerCase();
                final String license = editLicense.getText().toString().trim().toLowerCase();
                if (password.equals(Repassword)) {
                    Toast.makeText(SignUpActivity.this, email.length() + "," + password.length(), Toast.LENGTH_SHORT).show();
                    if (email.length() > 11 && password.length() > 8) {
                        showDialog("processing");
                        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                hideDialog();
                                if (task.isSuccessful()) {
                                    FirebaseUser user = task.getResult().getUser();
                                    if (user != null) {
                                        user.sendEmailVerification();
                                        String name = editName.getText().toString().trim().toLowerCase();
                                        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest
                                                .Builder()
                                                .setDisplayName(name)
                                                .build();
                                        user.updateProfile(changeRequest);
                                        updateUserStore(email, name, phoneno, license, option[spn.getSelectedItemPosition()]);
                                        updateUI(user);
                                    }
                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(SignUpActivity.this, error, Toast.LENGTH_LONG).show();

                                }
                            }
                        });

                    } else {

                        Toast.makeText(SignUpActivity.this, "invalid data for account creation", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, "password do not match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUserStore(String email, String name, String phoneno, String license, String usertype) {
        showDialog("updating profile, please wait");
        db.collection("users").document(email).set(new User(email, name, phoneno, license, usertype)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                hideDialog();
                Snackbar.make(btnSubmit, "Successfully created profile", Snackbar.LENGTH_LONG);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideDialog();
                Snackbar.make(btnSubmit, "failed to save profile details", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(SignUpActivity.this, WelcomeActivity.class));
            finish();
        }
    }

    private void hideDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void showDialog(String msg) {
        dialog = new ProgressDialog(SignUpActivity.this);
        dialog.setMessage(msg);
        dialog.setTitle("please wait");
        dialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dialog = null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        updateUI(currentUser);
    }
}
