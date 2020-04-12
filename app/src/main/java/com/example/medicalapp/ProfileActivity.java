package com.example.medicalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class ProfileActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    private EditText editName,editAddress,editSpeciality,editQualification;
    private Button btnSubmit;
    private TextView textGender,textLogout;
    private RadioGroup radioGroup;
    private RadioButton radioButton,radioButton2;
    private FirebaseAuth mAuth;
    String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        editName = findViewById(R.id.editName);
        editAddress = findViewById(R.id.editAddress);
        editSpeciality = findViewById(R.id.editSpeciality);
        editQualification = findViewById(R.id.editQualification);
        btnSubmit = findViewById(R.id.btnSubmit);
        radioGroup = findViewById(R.id.radioGroup);
        radioButton = findViewById(R.id.radioButton);
        radioButton2 = findViewById(R.id.radioButton2);
        textGender = findViewById(R.id.textGender);
        textLogout = findViewById(R.id.textLogout);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mauth = FirebaseAuth.getInstance();
                String name = editName.getText().toString().trim().toLowerCase();
                String address = editAddress.getText().toString().trim().toLowerCase();
                String speciality = editSpeciality.getText().toString().trim().toLowerCase();
                String gender = textGender.getText().toString().trim().toLowerCase();
                String qualification = editQualification.getText().toString().trim().toLowerCase();
                String logout = textLogout.getText().toString().trim().toLowerCase();
                Intent intent = new Intent(ProfileActivity.this, PatientActivity.class);
                startActivity(intent);
                finish();
            }
        });
        textLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.putExtra("position",0);
                startActivity(intent);
                finish();
            }
        });
        radioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (1){
            case R.id.radioButton2:
                gender = "Female";
                break;
            case R.id.radioButton:
                gender = "Male";
                break;

        }

    }

}
