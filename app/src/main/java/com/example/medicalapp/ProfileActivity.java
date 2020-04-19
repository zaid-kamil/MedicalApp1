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
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.medicalapp.models.Profile;
import com.example.medicalapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {
    private EditText editName, editAddress, editSpeciality, editQualification;
    private Button btnSubmit;
    private TextView textGender, textLogout;
    private RadioGroup genderOptions;
    String gender = "male";
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;
    private FirebaseFirestore db;
    private boolean isSkippedable = false;
    private String regEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        regEmail = mAuth.getCurrentUser().getEmail();
        verifyUserType(regEmail);
        editName = findViewById(R.id.editName);
        editAddress = findViewById(R.id.editAddress);
        editSpeciality = findViewById(R.id.editSpeciality);
        editQualification = findViewById(R.id.editQualification);
        btnSubmit = findViewById(R.id.btnSubmit);
        genderOptions = findViewById(R.id.genderOption);
        textGender = findViewById(R.id.textGender);
        textLogout = findViewById(R.id.textLogout);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = editName.getText().toString().trim().toLowerCase();
                String address = editAddress.getText().toString().trim().toLowerCase();
                String speciality = editSpeciality.getText().toString().trim().toLowerCase();
                switch (genderOptions.getCheckedRadioButtonId()) {
                    case R.id.male:
                        gender = "male";
                        break;
                    case R.id.female:
                        gender = "female";
                        break;
                }
                String qualification = editQualification.getText().toString().trim().toLowerCase();
                updateProfileStore(name, address, speciality, gender, qualification);

            }
        });
        textLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
                getSharedPreferences("usertype", Context.MODE_PRIVATE).edit().clear().apply();
                Intent intent = new Intent(ProfileActivity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.btnSkip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSkippedable) {
                    SharedPreferences userType = getSharedPreferences("usertype", Context.MODE_PRIVATE);
                    if (userType.getString("type", "doctor").equalsIgnoreCase("doctor")) {
                        startActivity(new Intent(ProfileActivity.this, PatientActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(ProfileActivity.this, ScannerActivity.class));
                        finish();
                    }
                } else {
                    Snackbar.make(editAddress, "You have never updated you profile", BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateProfileStore(String name, String address, String speciality, String gender, String qualification) {
        showDialog("updating profile");
        db.collection("profiles").document(regEmail).set(new Profile(name, address, speciality, gender, qualification)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                hideDialog();
                SharedPreferences userType = getSharedPreferences("usertype", Context.MODE_PRIVATE);
                if (userType.getString("type", "doctor").equalsIgnoreCase("doctor")) {
                    startActivity(new Intent(ProfileActivity.this, PatientActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(ProfileActivity.this, ScannerActivity.class));
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideDialog();
                Snackbar.make(btnSubmit, "failed to update your profile.", BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });
    }

    private void verifyUserType(final String email) {
        showDialog("verifying");
        db.collection("profiles").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                hideDialog();
                if (task.isSuccessful()) {
                    DocumentSnapshot result = task.getResult();
                    if (result != null) {
                        Profile profile = task.getResult().toObject(Profile.class);
                        try {
                            editName.setText(profile.name);
                            editAddress.setText(profile.address);
                            editQualification.setText(profile.qualification);
                            editSpeciality.setText(profile.speciality);
                            if (profile.gender.equalsIgnoreCase("male")) {
                                genderOptions.check(R.id.male);
                            } else {
                                genderOptions.check(R.id.female);
                            }
                            isSkippedable = true;
                        } catch (Exception e) {
                            isSkippedable = false;

                        }
                    } else {
                        isSkippedable = false;
                    }
                } else {
                    isSkippedable = false;
                }
            }
        });
    }

    private void gotoPatientActivity() {
        Intent intent = new Intent(ProfileActivity.this, PatientActivity.class);
        startActivity(intent);
        finish();
    }

    private void hideDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void showDialog(String msg) {
        dialog = new ProgressDialog(this);
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
