package com.salihttnc.myapplication;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class KayitActivity extends AppCompatActivity {
    EditText userEmail, userPassword,txtAd,txtSoyAd;
    Button btnRegister;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    ProgressBar mprogressBar;
    Not notes;
    private String userId;
    private DatabaseReference rRef;
    String ad,soyad,email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit);
        init();
        eventHandler();
        notes=new Not();

    }

    private void eventHandler() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        String mUserEmail = userEmail.getText().toString();
        String mUserPassword = userPassword.getText().toString();


        if (!TextUtils.isEmpty(mUserEmail) && !TextUtils.isEmpty(mUserPassword)) {
            mprogressBar.setVisibility(View.VISIBLE);
            mAuth.getInstance().createUserWithEmailAndPassword(mUserEmail, mUserPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        rRef = FirebaseDatabase.getInstance().getReference().child("Notes").child(userId);
                        email=userEmail.getText().toString();
                        ad=txtAd.getText().toString();
                        soyad=txtSoyAd.getText().toString();
                        rRef.child("email").setValue(email);
                        rRef.child("ad").setValue(ad);
                        rRef.child("soyad").setValue(soyad);




                        Toast.makeText(getApplicationContext(), "Hesap başarıyla oluşturuldu", Toast.LENGTH_LONG).show();
                        Intent login=new Intent(getApplicationContext(),GirisActivity.class);
                        startActivity(login);
                        finish();
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(getApplicationContext(), "Hata:" + error, Toast.LENGTH_LONG).show();
                    }
                    mprogressBar.setVisibility(View.GONE);
                }
            });
        } else if (mUserEmail.isEmpty()) {
            userEmail.setError("Bu alan boş bırakılamaz");
        } else {
            userPassword.setError("Bu alan boş bırakılamaz");
        }


    }

    private void init() {
        userEmail = findViewById(R.id.username);
        userPassword = findViewById(R.id.password);
        txtAd = findViewById(R.id.txtAd);
        txtSoyAd=findViewById(R.id.txtSoyAd);
        userPassword = findViewById(R.id.password);
        btnRegister = findViewById(R.id.btn_login);
        mprogressBar = findViewById(R.id.register_progressbar);


    }
}
