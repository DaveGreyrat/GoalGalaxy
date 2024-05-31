package com.example.goalgalaxy.Authentication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.goalgalaxy.MainActivity;
import com.example.goalgalaxy.R;
import com.example.goalgalaxy.Utils.DatabaseHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import android.content.SharedPreferences;


public class ChooseUser extends DialogFragment {

    Button btnuser1, btnuser2, btnuser3, btnuser4;
    private FirebaseAuth auth;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_choose, container, false);

        btnuser1 = view.findViewById(R.id.user1);
        btnuser2 = view.findViewById(R.id.user2);
        btnuser3 = view.findViewById(R.id.user3);
        btnuser4 = view.findViewById(R.id.user4);

        btnuser1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithUser("sictst1@gmail.com", "Samsung2023", v);
            }
        });

        btnuser2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithUser("sictst2@gmail.com", "Samsung2023", v);            }
        });

        btnuser3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithUser("sictst3@gmail.com", "Samsung2023", v);            }
        });

        btnuser4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithUser("sictst4@gmail.com", "Samsung2023", v);            }
        });

        return view;
    }

    private void signInWithUser(String email, String password, final View view) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        if (auth.getCurrentUser().isEmailVerified()) {
                            Toast.makeText(view.getContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                            saveCredentials(email, password);
                            new DatabaseHandler(view.getContext()).clearLocalDatabase();
                            new DatabaseHandler(view.getContext()).syncFromFirebase();
                            startActivity(new Intent(view.getContext(), MainActivity.class));
                            dismiss();
                        } else {
                            Toast.makeText(view.getContext(), "Email not verified", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(view.getContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        SharedPreferences sharedPref = getContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);

    }

    private void saveCredentials(final String email, final String password) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                SharedPreferences sharedPref = getContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
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