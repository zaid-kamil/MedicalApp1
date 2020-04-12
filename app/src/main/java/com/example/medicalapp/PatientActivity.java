package com.example.medicalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class PatientActivity extends AppCompatActivity {

    private TextView textInfo;
    private EditText pEmail,editName,editAge,editHeight,editAddress,editReason;
    private String[] option;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paitent);
        Button btnSubmit = findViewById(R.id.btnSubmit);
        textInfo = findViewById(R.id.textInfo);
        pEmail = findViewById(R.id.pEmail);
        editName = findViewById(R.id.editName);
        editAddress = findViewById(R.id.editAddress);
        editAge = findViewById(R.id.editAge);
        editHeight = findViewById(R.id.editHeight);
        editReason = findViewById(R.id.editReason);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = pEmail.getText().toString().trim().toLowerCase();
                String name =  editName.getText().toString().trim().toLowerCase();
                String age = editAge.toString().trim().toLowerCase();
                String Height = editHeight.toString().trim().toLowerCase();
                String address = editAddress.getText().toString().trim().toLowerCase();
                String reason   = editReason.toString().trim().toLowerCase();
                Intent intent = new Intent(new Intent(PatientActivity.this,ScanningActivity.class));
                startActivity(intent);
                finish();
            }
        });


    }
}
