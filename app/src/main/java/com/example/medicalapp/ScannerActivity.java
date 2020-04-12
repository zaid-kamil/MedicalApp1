package com.example.medicalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScannerActivity extends AppCompatActivity {
    ImageView imageView;
    Button btnGenerate;
    Button btnScan;
    Thread thread;
    TextView tv_qr_readTxt;
    EditText editText;
    String EditTextValue;
    public final static int QRcodeWidth = 350;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        imageView = findViewById(R.id.imageView);
        btnGenerate = findViewById(R.id.btnGenerate);
        btnScan = findViewById(R.id.btnScan);
        tv_qr_readTxt = findViewById(R.id.tv_qr_readTxt);
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editText.getText().toString().isEmpty()){
                    EditTextValue = editText.getText().toString();
                    try {
                        bitmap = TextToImageEncode(EditTextValue);

                        imageView.setImageBitmap(bitmap);

                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    editText.requestFocus();
                    Toast.makeText(ScannerActivity.this, "Please Enter Your Scanned Test", Toast.LENGTH_LONG).show();


                }


            }
        });
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(ScannerActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }


        });
    }

     Bitmap TextToImageEncode(String editTextValue) throws WriterException {
         BitMatrix bitMatrix;
         try{
             bitMatrix = new MultiFormatWriter().encode(
                     EditTextValue, BarcodeFormat.DATA_MATRIX.QR_CODE,
                     QRcodeWidth,QRcodeWidth,null);
         }
         catch (IllegalArgumentException Illegalargumentexception){
             return null;
         }
         int bitMatrixWidth = bitMatrix.getWidth();

         int bitMatrixHeight = bitMatrix.getHeight();

         int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

         for (int y = 0; y < bitMatrixHeight; y++) {
             int offset = y * bitMatrixWidth;

             for (int x = 0; x < bitMatrixWidth; x++) {
                 pixels[offset + x] = bitMatrix.get(x, y) ?
                         getResources().getColor(R.color.QRCodeBlackColor):getResources().getColor(R.color.QRCodeWhiteColor);
             }
         }
         Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

         bitmap.setPixels(pixels, 0, 350, 0, 0, bitMatrixWidth, bitMatrixHeight);
         return bitmap;
     }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.e("Scan*******", "Cancelled scan");

            } else {
                Log.e("Scan", "Scanned");

                tv_qr_readTxt.setText(result.getContents());
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}




