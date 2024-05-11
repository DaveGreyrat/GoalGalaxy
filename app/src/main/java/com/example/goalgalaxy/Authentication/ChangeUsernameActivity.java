package com.example.goalgalaxy.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goalgalaxy.Fragments.SettingsFragment;
import com.example.goalgalaxy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ChangeUsernameActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText newUsernameEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_username);

        mAuth = FirebaseAuth.getInstance();

        newUsernameEditText = findViewById(R.id.new_username);
        Button changeUsernameButton = findViewById(R.id.change_username_btn);
        TextView goBackToSettingsTextView = findViewById(R.id.gobackSettings);

        changeUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUsername();
            }
        });

        goBackToSettingsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToSettings();
            }
        });
    }

    private void changeUsername() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String newUsername = newUsernameEditText.getText().toString().trim();
            if (TextUtils.isEmpty(newUsername)) {
                Toast.makeText(this, "Please enter a new username", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newUsername.length() < 4) {
                Toast.makeText(this, "Username must be at least 4 characters long", Toast.LENGTH_SHORT).show();
                return;
            }

            user.updateProfile(new UserProfileChangeRequest.Builder()
                            .setDisplayName(newUsername)
                            .build())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ChangeUsernameActivity.this, "Username updated successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ChangeUsernameActivity.this, "Failed to update username", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void goBackToSettings() {
        Intent intent = new Intent(this, SettingsFragment.class);
        startActivity(intent);
        finish();
    }
}