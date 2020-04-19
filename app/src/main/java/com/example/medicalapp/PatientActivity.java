package com.example.medicalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class PatientActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private TextView textInfo;
    private EditText pEmail, editName, editAge, editHeight, editAddress, editReason;
    private String[] option;
    int counter = 1;
    private ImageView imgQr;
    private String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
    };
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paitent);

        textInfo = findViewById(R.id.textInfo);
        pEmail = findViewById(R.id.pEmail);
        editName = findViewById(R.id.editName);
        editAddress = findViewById(R.id.editAddress);
        editAge = findViewById(R.id.editAge);
        editHeight = findViewById(R.id.editHeight);
        imgQr = findViewById(R.id.imgQR);
        editReason = findViewById(R.id.editReason);
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    private void sendEmail() {
        Intent intent = new Intent(new Intent(PatientActivity.this, EmailActivity.class));
        String email = pEmail.getText().toString().trim().toLowerCase();
        String name = editName.getText().toString().trim().toLowerCase();
        intent.putExtra("email", email);
        intent.putExtra("name", name);
        startActivity(intent);
    }

    private void generateQR(String qrData) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(qrData, BarcodeFormat.QR_CODE, 400, 400);

            imgQr.setImageBitmap(bitmap);
        } catch (Exception e) {
            Snackbar.make(editAddress, e.getMessage(), BaseTransientBottomBar.LENGTH_INDEFINITE).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        handlePermissions();
    }

    private void handlePermissions() {
        if (!EasyPermissions.hasPermissions(this, permissions)) {
            EasyPermissions.requestPermissions(this, "app permission", 91, permissions);
        } else {
            updateUI();
        }
    }

    private void updateUI() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (counter == 1) {
                    String email = pEmail.getText().toString().trim().toLowerCase();
                    String name = editName.getText().toString().trim().toLowerCase();
                    String age = editAge.toString().trim().toLowerCase();
                    String Height = editHeight.toString().trim().toLowerCase();
                    String address = editAddress.getText().toString().trim().toLowerCase();
                    String reason = editReason.toString().trim().toLowerCase();
                    String qrData = "MEDICAL DETAIL\nfrom app\nfrom doctor" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + "\nPATIENT DETAILS\nEMAIL - " + email + "\nNAME - " + name + "\nAGE - " + age + "\nHEIGHT - " + Height + "\nADDRESS - " + address + "\nREASON - " + reason;
                    generateQR(qrData);
                    counter = 2;
                    btnSubmit.setText("click on Image to Save");
                } else if (counter == 3) {
                    counter = 1;
                    sendEmail();
                } else {
                    Toast.makeText(PatientActivity.this, "save image", Toast.LENGTH_SHORT).show();
                }
            }
        });
        imgQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PatientActivity.this, "saving", Toast.LENGTH_SHORT).show();
                saveImage();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void saveImage() {
        imgQr.buildDrawingCache();
        Bitmap bmp = imgQr.getDrawingCache();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            String displayName = "Patient_" + editName.getText().toString().toLowerCase() + "_QR";
            try {
                saveBitmap(this, bmp, Bitmap.CompressFormat.JPEG, "image/jpg", displayName);
                counter = 3;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            saveBitmapOld(bmp);
        }
    }

    private void saveBitmapOld(Bitmap bmp) {
        File storageLoc = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        String filename = "Patient_" + editName.getText().toString().toLowerCase() + "_QR";
        File file = new File(storageLoc, filename + ".jpg");

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            Toast.makeText(this, "saved image", Toast.LENGTH_SHORT).show();
            scanFile(this, Uri.fromFile(file));
            counter = 3;
            btnSubmit.setText("send Email to patient");


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void scanFile(Context context, Uri imageUri) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(imageUri);
        context.sendBroadcast(scanIntent);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        updateUI();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Snackbar.make(btnSubmit, "the app wont work without permission", BaseTransientBottomBar.LENGTH_INDEFINITE).setAction("try again", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePermissions();
            }
        }).show();
    }

    private void saveBitmap(@NonNull final Context context, @NonNull final Bitmap bitmap, @NonNull final Bitmap.CompressFormat format, @NonNull final String mimeType, @NonNull final String displayName) throws IOException {
        final String relativeLocation = Environment.DIRECTORY_PICTURES + File.separator + "medicalapp";
        final ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation);
        final ContentResolver resolver = context.getContentResolver();
        OutputStream stream = null;
        Uri uri = null;
        try {
            final Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            uri = resolver.insert(contentUri, contentValues);
            if (uri == null) {
                throw new IOException("Failed to create new MediaStore record.");
            }
            stream = resolver.openOutputStream(uri);
            if (stream == null) {
                throw new IOException("Failed to get output stream.");
            }
            if (bitmap.compress(format, 95, stream) == false) {
                throw new IOException("Failed to save bitmap.");
            }
        } catch (IOException e) {
            if (uri != null) {
                // Don't leave an orphan entry in the MediaStore
                resolver.delete(uri, null, null);
            }
            throw e;
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }
}
