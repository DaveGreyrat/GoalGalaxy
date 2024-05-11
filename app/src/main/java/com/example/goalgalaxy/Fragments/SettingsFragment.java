package com.example.goalgalaxy.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.goalgalaxy.Authentication.ChangeUsernameActivity;
import com.example.goalgalaxy.Authentication.DeleteAccount;
import com.example.goalgalaxy.Authentication.LoginActivity;
import com.example.goalgalaxy.Authentication.PasswordResetActivity;
import com.example.goalgalaxy.Fragments.Settings.FAQActivity;
import com.example.goalgalaxy.Fragments.Settings.PolicyActivity;
import com.example.goalgalaxy.R;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        TextView changeUsernameTextView = view.findViewById(R.id.change_username);
        TextView changePasswordTextView = view.findViewById(R.id.change_password);
        TextView policyTextView = view.findViewById(R.id.policy);
        TextView faqTextView = view.findViewById(R.id.FAQ);
        TextView logOutTextView = view.findViewById(R.id.log_out);
        TextView deleteAccountTextView = view.findViewById(R.id.Delete_account);
        ImageView backImageView = view.findViewById(R.id.back);


        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        changeUsernameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), ChangeUsernameActivity.class));
            }
        });

        changePasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), PasswordResetActivity.class));
            }
        });

        policyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), PolicyActivity.class));
            }
        });

        faqTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), FAQActivity.class));
            }
        });

        logOutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("rememberMe", false).apply();
                startActivity(new Intent(requireActivity(), LoginActivity.class));
                requireActivity().finish();
            }
        });

        deleteAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), DeleteAccount.class));
            }
        });

        return view;
    }
}
