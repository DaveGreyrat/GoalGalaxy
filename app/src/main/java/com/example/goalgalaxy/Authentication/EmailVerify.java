package com.example.goalgalaxy.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.Toast;

import com.example.goalgalaxy.MainActivity;
import com.example.goalgalaxy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailVerify extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verify);

        auth = FirebaseAuth.getInstance();
        FirebaseUser User = auth.getCurrentUser();
        toastEmailOpen();
    }

    private void toastEmailOpen() {
        Toast.makeText(EmailVerify.this, R.string.if_open_email, Toast.LENGTH_SHORT).show();
        Handler handler = new Handler();
        handler.postDelayed(() -> {
        }, 2000);
        new CountDownTimer(Integer.MAX_VALUE, 3000) {
            public void onTick(long millisUntilFinished) {
                Intent intent = getIntent();
                String password = "";
                if(intent!=null){
                    password = intent.getStringExtra("Password");
                }
                auth.signInWithEmailAndPassword(auth.getCurrentUser().getEmail(), password).addOnCompleteListener(task -> {
                    if (auth.getCurrentUser().isEmailVerified()) {
                        Toast.makeText(EmailVerify.this, "Is verified", Toast.LENGTH_SHORT).show();
                        cancel();
                        startActivity(new Intent(EmailVerify.this, MainActivity.class));
                        finish();
                    }

                });

            }
            public void onFinish() {
            }
        }.start();

    }
}

