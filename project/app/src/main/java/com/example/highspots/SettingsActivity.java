package com.example.highspots;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.highspots.repositories.UserDataRepository;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    /* Views */
    private Button logOutBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.getSupportActionBar().hide();

        initViews();
    }

    private void initViews() {
        this.logOutBTN = findViewById(R.id.SettingsPageLogOutBTN);

        logOutBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginPage();
                Toast.makeText(SettingsActivity.this, "LogOut was successful!", Toast.LENGTH_SHORT).show();
            }
        });
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
}