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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class GirisActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    EditText mUserEmail,mUserPassword;
    Button   btn_login;
    ProgressBar progressBar;
    TextView btn_register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);
        init();
        eventHandler();

    }

    private void eventHandler() {
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register=new Intent(GirisActivity.this,KayitActivity.class);
                register.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(register);

            }
        });
    }

    private void LoginUser() {
        String email = mUserEmail.getText().toString();
        String password = mUserPassword.getText().toString();
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(getApplicationContext(),"Giriş Başarılı",Toast.LENGTH_LONG).show();
                        Intent main=new Intent(GirisActivity.this,MainActivity.class);
                        startActivity(main);
                        finish();
                    }
                    else
                    {
                        String error=task.getException().getMessage();
                        Toast.makeText(getApplicationContext(),"Hata:"+error,Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);


                }
            });
        }
        else if(email.isEmpty())
        {
            mUserEmail.setError("Bu alan boş bırakılamaz");
        }
        else
        {
            mUserPassword.setError("Bu alan boş birakılamaz");
        }
    }


    private void init() {
        mUserEmail = findViewById(R.id.login_username);
        mUserPassword = findViewById(R.id.login_password);
        btn_login = findViewById(R.id.login_btn_login);
        progressBar=findViewById(R.id.progressBar);
        btn_register=findViewById(R.id.btn_register);

    }


}
