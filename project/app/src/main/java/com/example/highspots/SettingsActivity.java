package com.example.highspots;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.highspots.repositories.UserDataRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    /* Views */
    private Button logOutBTN;
    private EditText nickNameET;
    private Button saveNicknameET;
    private BottomNavigationView bottomNavigationView;

    /* Database */
    private UserDataRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.getSupportActionBar().hide();

        initVars(); // should be called before initViews()
        initViews();
    }

    private void initViews() {
        this.logOutBTN = findViewById(R.id.SettingsPageLogOutBTN);
        this.nickNameET = findViewById(R.id.SettingsPageNicknameET);
        this.saveNicknameET = findViewById(R.id.SettingsPageSaveNicknameBTN);
        this.bottomNavigationView = findViewById(R.id.nav_view_home_page);

        // Configure Bottom Nav View
        bottomNavigationView.setSelectedItemId(R.id.navigation_settings);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.navigation_home:
                        Intent intent1 = new Intent(getApplicationContext(), HomePageActivity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent1);
                        return true;
                    case R.id.navigation_settings:
                        return true;
                    case R.id.navigation_map:
                        startActivity(new Intent(SettingsActivity.this, MapsActivity.class));
                        return true;
                }

                return false;
            }
        });

        if (repository.getUser() != null) {
            nickNameET.setText(repository.getUser().getNickName());
        }

        logOutBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginPage();
                Toast.makeText(SettingsActivity.this, "LogOut was successful!", Toast.LENGTH_SHORT).show();
            }
        });

        saveNicknameET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNickName();
                hideSoftKeyboard(SettingsActivity.this);
                Toast.makeText(SettingsActivity.this, "Nickname updated successfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initVars() {
        this.repository = UserDataRepository.getInstance();
    }

    private void updateNickName() {
        String newNickName = nickNameET.getText().toString().trim();

        if (newNickName.length() < 4) {
            nickNameET.setError("Nickname should be at least 4 characters long!");
            return;
        }

        repository.updateNickName(newNickName);
    }

    /** Redirects to login page and clears the back stack. */
    private void goToLoginPage() {
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(SettingsActivity.this, LogInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        UserDataRepository.deleteCurrentUserData();
    }

    private void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.bottomNavigationView.setSelectedItemId(R.id.navigation_settings);
    }
}