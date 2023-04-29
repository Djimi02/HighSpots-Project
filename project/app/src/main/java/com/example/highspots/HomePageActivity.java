package com.example.highspots;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.highspots.interfaces.UserDataListener;
import com.example.highspots.repositories.UserDataRepository;

public class HomePageActivity extends AppCompatActivity implements UserDataListener {

    /* Views */
    private ImageButton goToSettingsBTN;
    private TextView nickNameTV;
    private TextView emailTV;

    /* Database */
    UserDataRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        this.getSupportActionBar().hide();

        initViews();
        initVars();
    }

    private void initViews() {
        this.goToSettingsBTN = findViewById(R.id.HomePageSettingsBTN);
        this.nickNameTV = findViewById(R.id.HomePageUserNickname);
        this.emailTV = findViewById(R.id.HomePageUserEmail);

        goToSettingsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
    }

    private void initVars() {
        this.repository = UserDataRepository.getInstance();
        repository.addListener(this);
    }

    @Override
    public void retrieveUserData() {
        this.nickNameTV.setText(repository.getUser().getNickName());
        this.emailTV.setText(repository.getUser().getEmail());
    }
}