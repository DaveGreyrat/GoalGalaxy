package com.example.goalgalaxy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.goalgalaxy.Adapter.ToDoAdapter;
import com.example.goalgalaxy.Authentication.LoginActivity;
import com.example.goalgalaxy.Fragments.CompletedFragment;
import com.example.goalgalaxy.Fragments.GoalsFragment;
import com.example.goalgalaxy.Fragments.HomeFragment;
import com.example.goalgalaxy.Fragments.SettingsFragment;
import com.example.goalgalaxy.Fragments.TasksFragment;
import com.example.goalgalaxy.Fragments.TodayFragment;
import com.example.goalgalaxy.Tasks.AddNewTask;
import com.example.goalgalaxy.Utils.DatabaseHandler;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private FirebaseAuth auth;
    private ToDoAdapter adapter;
    private DatabaseHandler db;
    private Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        db = new DatabaseHandler(this);
        adapter = new ToDoAdapter(db, this);

        context = this;

        DatabaseHandler databaseHandler = new DatabaseHandler(context);

        db.setupFirebaseListener(this);

        updateNavigationHeader();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(updateReceiver, new IntentFilter("UPDATE_DATA"));
        LocalBroadcastManager.getInstance(this).registerReceiver(updateUsernameReceiver, new IntentFilter("UPDATE_USERNAME"));
        Log.d("MainActivity", "onCreate: Registered updateReceiver and updateUsernameReceiver");
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        } else if (itemId == R.id.nav_tasks) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TasksFragment()).commit();
        } else if (itemId == R.id.nav_completed) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CompletedFragment()).commit();
        } else if (itemId == R.id.nav_settings) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
        } else if (itemId == R.id.nav_logout) {
            clearRememberMe();
            auth.signOut();
            Toast.makeText(this, "Logout successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void clearRememberMe() {
        SharedPreferences sharedPref = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("username");
        editor.remove("password");
        editor.remove("rememberMe");
        editor.apply();
    }

    private void updateNavigationHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView usernameTextView = headerView.findViewById(R.id.nav_header_username);
        TextView emailTextView = headerView.findViewById(R.id.nav_header_email);

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String username = currentUser.getDisplayName();
            String email = currentUser.getEmail();

            if (username != null && !username.isEmpty()) {
                usernameTextView.setText(username);
            } else {
                usernameTextView.setText("Username");
            }
            if (email != null && !email.isEmpty()) {
                emailTextView.setText(email);
            } else {
                emailTextView.setText("email@example.com");
            }
        }

        Log.d("MainActivity", "Update navigation drawer");
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateUsernameReceiver);
        Log.d("MainActivity", "onDestroy: Unregistered updateReceiver and updateUsernameReceiver");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainActivity", "onResume: Activity resumed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MainActivity", "onPause: Activity paused");
    }

    private final BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("UPDATE_DATA".equals(intent.getAction())) {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (currentFragment != null && currentFragment.isVisible()) {
                    if (currentFragment instanceof TasksFragment) {
                        ((TasksFragment) currentFragment).loadTasks();
                    } else if (currentFragment instanceof HomeFragment) {
                        ((HomeFragment) currentFragment).loadTodayTasks();
                    } else if (currentFragment instanceof CompletedFragment) {
                        ((CompletedFragment) currentFragment).loadCompletedTasks();
                    } else {
                        Log.d("MainActivity", "Current fragment is not a target fragment for update");
                    }
                } else {
                    Log.d("MainActivity", "No visible fragment found to update");
                }
            }
        }
    };

    private final BroadcastReceiver updateUsernameReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("UPDATE_USERNAME".equals(intent.getAction())) {
                Log.d("MainActivity", "Received UPDATE_USERNAME broadcast");
                updateNavigationHeader();
            } else {
                Log.d("MainActivity", "Received unknown broadcast: " + intent.getAction());
            }
        }
    };

    public ToDoAdapter getAdapter() {
        return adapter;
    }
}
