package com.example.medicalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private EditText editName,editEmail,editPassword,editRePassword,editPhoneNo_,editLicense;
    private Spinner Spinner;
    private Button btnSubmit;
    private String[] option;
    private ProgressDialog dialog;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        btnSubmit = findViewById(R.id.btnSubmit);
        editLicense = findViewById(R.id.editLicense);
        editRePassword = findViewById(R.id.editRePassword);
        editEmail = findViewById(R.id.editEmail);
        editName = findViewById(R.id.editName);
        editPhoneNo_ = findViewById(R.id.editPhoneNo_);
        Spinner = findViewById(R.id.Spinner);
        editPassword = findViewById(R.id.editPassword);
        option = new String[]{"SELECT", "DOCTOR", "ORGANIZER"};
        ArrayAdapter<String> typeadapter = new ArrayAdapter<>(SignUpActivity.this, android.R.layout.simple_list_item_1, option);
        typeadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner.setAdapter(typeadapter);
        Spinner.setOnItemSelectedListener(this);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String email = editEmail.getText().toString().trim().toLowerCase();
                String password = editPassword.getText().toString().trim().toLowerCase();
                String Repassword = editRePassword.getText().toString().trim().toLowerCase();
                String phoneno = editPhoneNo_.getText().toString().trim().toLowerCase();
                String License = editLicense.getText().toString().trim().toLowerCase();
                if (password.equals(Repassword)) {
                    Toast.makeText(SignUpActivity.this, email.length()+","+password.length(), Toast.LENGTH_SHORT).show();
                    if (email.length() > 11 && password.length() > 8) {
                        showDialog("processing");
                        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                hideDialog();
                                if (task.isSuccessful()) {
                                    FirebaseUser user = task.getResult().getUser();
                                    user.sendEmailVerification();
                                    String name = editName.getText().toString().trim().toLowerCase();
                                    UserProfileChangeRequest changeRequest = new UserProfileChangeRequest
                                            .Builder()
                                            .setDisplayName(name)
                                            .build();
                                    user.updateProfile(changeRequest);
                                    updateUI(user);
                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(SignUpActivity.this, error, Toast.LENGTH_LONG).show();
                                    
                                }
                            }
                        });

                    }else{

                        Toast.makeText(SignUpActivity.this, "invalid data for account creation", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(SignUpActivity.this, "password do not match", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return ;
    }

    private void updateUI(FirebaseUser user) {
        startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
        finish();
    }


    private void hideDialog() {
        if (dialog != null && dialog.isShowing()) ;
        {
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
