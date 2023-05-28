package com.example.highspots;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.highspots.interfaces.UserDataListener;
import com.example.highspots.models.Spot;
import com.example.highspots.repositories.UserDataRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomePageActivity extends AppCompatActivity implements UserDataListener {

    /* Views */
    private TextView nickNameTV;
    private TextView emailTV;
    private BottomNavigationView bottomNavigationView;
    private TextView numberOfVisitedSpotsTV;
    private TextView numberOfDoneRatingsTV;
    private TextView numberOfFoundSpotsTV;
    private TextView averageRatingFoundSpotsTV;

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
        this.nickNameTV = findViewById(R.id.HomePageUserNickname);
        this.emailTV = findViewById(R.id.HomePageUserEmail);
        this.bottomNavigationView = findViewById(R.id.nav_view_home_page);
        this.numberOfVisitedSpotsTV = findViewById(R.id.homePageVisitedSpotsTV);
        this.numberOfDoneRatingsTV = findViewById(R.id.homePageRatingsTV);
        this.numberOfFoundSpotsTV = findViewById(R.id.homePageFoundSpotsTV);
        this.numberOfFoundSpotsTV.setText("My Spots: 0");
        this.averageRatingFoundSpotsTV = findViewById(R.id.homePageFoundSpotsAvgRatingTV);
        averageRatingFoundSpotsTV.setText("Average Rating: No Spots");

        // Configure Bottom Nav View
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.navigation_home:
                        return true;
                    case R.id.navigation_settings:
                        Intent intent1 = new Intent(getApplicationContext(), SettingsActivity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent1);
                        return true;
                    case R.id.navigation_map:
                        startActivity(new Intent(HomePageActivity.this, MapsActivity.class));
                        return true;
                }

                return false;
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
        this.numberOfVisitedSpotsTV.setText("Visited spots: " + (repository.getUser().getVisitedSpots().size()
                + repository.getUser().getFoundSpots().size()));
        this.numberOfDoneRatingsTV.setText("Ratings: " + repository.getUser().getRatedSpots().size());
    }

    @Override
    public void retrieveFoundSpotsData() {
        if (repository.getFoundSpots() != null) {
            System.out.println("My Spots: " + repository.getFoundSpots().size());
            this.numberOfFoundSpotsTV.setText("My Spots: " + repository.getFoundSpots().size());
        }

        if (repository.getFoundSpots().size() > 0) {
            double ratingSum = 0;
            for (Spot spot : repository.getFoundSpots()) {
                ratingSum += spot.getRating();
            }
            double avgRating = ratingSum / repository.getFoundSpots().size();
            this.averageRatingFoundSpotsTV.setText("Average Rating: " + String.format("%.2f", avgRating));
        } else {
            this.averageRatingFoundSpotsTV.setText("Average Rating: No Spots");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }
}