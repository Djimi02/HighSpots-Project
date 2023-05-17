package com.example.highspots;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.highspots.enums.Features;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.highspots.databinding.ActivityMapsBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    /* Google Maps */
    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    /* Variables */

    /* Views */
    DrawerLayout drawerLayout;
    ImageButton menuBTN;
    BottomNavigationView bottomNavigationView;
    ImageButton addSpotIBTN;

    /* Menu Views */
    TextView distanceTV;
    Slider menuSlider;
    GridLayout menuGridLayout;
    List<CheckBox> menuFeatureCheckBoxes = new ArrayList<>();
    Button filterBTN;

    /* Dialog */
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    /* Dialog Views */
    private GridLayout addSpotDialogGridLayout;
    private List<CheckBox> addSpotDialogFeatureCheckBoxes = new ArrayList<>();
    private Button addSpotDialogSaveBTN;
    private Spinner addSpotDialogLocOptionsSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        initViews();

    }

    private void initViews() {
        this.drawerLayout = findViewById(R.id.mapsLayout);

        this.menuBTN = findViewById(R.id.mapsMenuBTN);
        menuBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.open();
            }
        });

        this.bottomNavigationView = findViewById(R.id.nav_view_maps);
        // Configure Bottom Nav View
        bottomNavigationView.setSelectedItemId(R.id.navigation_map);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.navigation_home:
                        Intent intent1 = new Intent(getApplicationContext(), HomePageActivity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent1);
                        finish();
                        return true;
                    case R.id.navigation_settings:
                        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                        finish();
                        return true;
                    case R.id.navigation_map:
                        return true;
                }

                return false;
            }
        });

        this.addSpotIBTN = findViewById(R.id.mapsAddSpotBTN);
        addSpotIBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddSpotDialog();
            }
        });

        initMenuViews();
    }

    private void initMenuViews() {
        this.menuSlider = findViewById(R.id.mapsMenuSlider);

        this.distanceTV = findViewById(R.id.mapsMenuChooseDistanceTV);
        distanceTV.setText("Choose distance: " + menuSlider.getValue() + " km");

        // should be added after this.menuTitleTV is init
        menuSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                distanceTV.setText("Choose distance: " + menuSlider.getValue() + " km");
            }
        });

        // fill in grid layout with categories as check boxes
        this.menuGridLayout = findViewById(R.id.mapMenuGridLayout);
        menuGridLayout.setRowCount(Features.values().length / 2 + 1);
        menuGridLayout.setColumnCount(2);
        for (Features features : Features.values()) {
            CheckBox checkBox = new CheckBox(getApplicationContext());
            checkBox.setText(features.toString());
            checkBox.setChecked(false);
            checkBox.setTextSize(18);
            menuFeatureCheckBoxes.add(checkBox);
            menuGridLayout.addView(checkBox);
        }

        this.filterBTN = findViewById(R.id.mapsMenuFilterBTN);
        filterBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
    }

    private void openAddSpotDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.add_spot_dialog, null);

        initDialogViews(popupView);

        // show dialog
        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    private void initDialogViews(View popupView) {
        this.addSpotDialogGridLayout = popupView.findViewById(R.id.addSpotDialogGridLayout);
        addSpotDialogGridLayout.setRowCount(Features.values().length / 2 + 1);
        addSpotDialogGridLayout.setColumnCount(2);
        for (Features features : Features.values()) {
            CheckBox checkBox = new CheckBox(getApplicationContext());
            checkBox.setText(features.toString());
            checkBox.setChecked(false);
            checkBox.setTextSize(23);
            addSpotDialogFeatureCheckBoxes.add(checkBox);
            addSpotDialogGridLayout.addView(checkBox);
        }

        this.addSpotDialogSaveBTN = popupView.findViewById(R.id.addSpotSaveBTN);
        addSpotDialogSaveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        this.addSpotDialogLocOptionsSpinner = popupView.findViewById(R.id.addSpotDialogLocationSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.location_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addSpotDialogLocOptionsSpinner.setAdapter(adapter);
        addSpotDialogLocOptionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("position : " + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}