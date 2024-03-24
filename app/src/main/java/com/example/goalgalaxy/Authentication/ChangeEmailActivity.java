package com.example.goalgalaxy.Authentication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goalgalaxy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangeEmailActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText currentEmailEditText, newEmailEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        mAuth = FirebaseAuth.getInstance();
        currentEmailEditText = findViewById(R.id.current_email);
        newEmailEditText = findViewById(R.id.new_email);

        Button resetEmailButton = findViewById(R.id.resetemail);
        resetEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendChangeEmailLink();
            }
        });

        TextView goBackTextView = findViewById(R.id.gobackSettings);
        goBackTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to SettingsFragment
                onBackPressed();
            }
        });
    }

    private void sendChangeEmailLink() {
        String currentEmail = currentEmailEditText.getText().toString().trim();
        String newEmail = newEmailEditText.getText().toString().trim();

        if (currentEmail.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(this, "Please enter both your current email and the new email", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.updateEmail(newEmail)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Email updated successfully
                            Toast.makeText(ChangeEmailActivity.this, "Email change link sent to your current email address. Please check your email.", Toast.LENGTH_SHORT).show();
                            // Redirect user to change their email after verification
                            // You can handle this based on your app's flow, possibly by opening another activity
                        } else {
                            // Error updating email
                            Toast.makeText(ChangeEmailActivity.this, "Failed to update email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
