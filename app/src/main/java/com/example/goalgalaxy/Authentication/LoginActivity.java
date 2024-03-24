package com.example.goalgalaxy.Authentication;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.content.Intent;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.goalgalaxy.MainActivity;
import com.example.goalgalaxy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText loginEmail, loginPassword;
    private Button loginButton;
    private TextView signupRedirectText, forgotPasswordText;
    private CheckBox remember;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirect);
        remember = findViewById(R.id.remember);
        forgotPasswordText = findViewById(R.id.forgetpassword);



        // Check if user previously opted for "Remember Me"
        SharedPreferences sharedPref = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        boolean rememberMe = sharedPref.getBoolean("rememberMe", false);


        if (rememberMe) {
            String savedEmail = sharedPref.getString("username", "");
            String savedPassword = sharedPref.getString("password", "");

            if (!savedEmail.isEmpty() && !savedPassword.isEmpty()) {
                auth.signInWithEmailAndPassword(savedEmail, savedPassword)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                if (auth.getCurrentUser().isEmailVerified()) {
                                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Email not verified", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "Auto Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = loginEmail.getText().toString().trim();
                String pass = loginPassword.getText().toString();

                if (email.isEmpty()) {
                    loginEmail.setError("Email cannot be empty");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email.toLowerCase()).matches()) {
                    loginEmail.setError("Please enter a valid email");
                } else {
                    if (!pass.isEmpty()) {
                        auth.signInWithEmailAndPassword(email, pass)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        if (auth.getCurrentUser().isEmailVerified()) {
                                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                            if (remember.isChecked()) {
                                                saveCredentials(email, pass);
                                            }
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Email not verified", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        loginPassword.setError("Password cannot be empty");
                    }
                }
            }
        });

        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPasswordReset();
            }
        });

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }

    public void openPasswordReset() {
        Intent intent = new Intent(this, PasswordResetActivity.class);
        startActivity(intent);
    }



    private void saveCredentials(final String email, final String password) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                SharedPreferences sharedPref = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("username", email)
                        .putString("password", password)
                        .putBoolean("rememberMe", true)
                        .apply();
                return null;
            }
        }.execute();
    }


}