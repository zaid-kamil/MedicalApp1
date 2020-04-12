package com.example.medicalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.zxing.integration.android.IntentIntegrator;

public class ScanningActivity extends AppCompatActivity {
    private Button btnScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning);
         btnScan = findViewById(R.id.btnScan);
         btnScan.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 IntentIntegrator integrator = new IntentIntegrator(ScanningActivity.this);
                 integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                 integrator.setPrompt("Scan");
                 integrator.setCameraId(0);
                 integrator.setBeepEnabled(false);
                 integrator.setBarcodeImageEnabled(false);
                 integrator.initiateScan();

             }
         });
    }
}
