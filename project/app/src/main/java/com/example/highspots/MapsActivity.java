package com.example.highspots;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.highspots.adapters.FeatureRVAdapter;
import com.example.highspots.enums.Feature;
import com.example.highspots.models.Spot;
import com.example.highspots.repositories.UserDataRepository;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.highspots.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.slider.Slider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static int LOCATION_PERMISSION_CODE = 101;
    private final double ALLOWED_USER_SPOT_DISTANCE = 100;

    /* Google Maps */
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;

    /* Variables */
    private List<Spot> allSpots = new ArrayList<>();
    private Map<Marker, Spot> markerSpotMap = new HashMap<>();

    /* Maps Views */
    private DrawerLayout drawerLayout;
    private ImageButton menuBTN;
    private BottomNavigationView bottomNavigationView;
    private ImageButton addSpotIBTN;

    /* Menu Views */
    private TextView distanceTV;
    private Slider menuSlider;
    private GridLayout menuGridLayout;
    private List<CheckBox> menuFeatureCheckBoxes = new ArrayList<>();
    private Button filterBTN;

    /* Dialog */
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    /* Add Spot Dialog Views */
    private GridLayout addSpotDialogGridLayout;
    private List<CheckBox> addSpotDialogFeatureCheckBoxes = new ArrayList<>();
    private Button addSpotDialogSaveBTN;
    private Spinner addSpotDialogLocOptionsSpinner;
    private TextView addSpotRatingTV;
    private Slider addSpotRatingSlider;

    /* Open Spot Dialog Views */
    private TextView spotRatingTV;
    private TextView numberOfVisitorsTV;
    private RecyclerView spotFeaturesRV;
    private Button visitSpotBTN;
    private Button rateSpotBTN;

    /* Database */
    private DatabaseReference spotDataReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Ask for location permission
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (!isLocationPermissionGranted()) {
            requestLocationPermission();
        }
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
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {

                openSpotDialog(markerSpotMap.get(marker));

                return false;
            }
        });

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        initViews(); // should be called before initVars()
        initVars();
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

    private void initVars() {
        this.spotDataReference = FirebaseDatabase.getInstance("https://highspots-project-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Spots");
        spotDataReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    return;
                }

                allSpots.clear();
                for (DataSnapshot ds : task.getResult().getChildren()) {
                    Spot spot = ds.getValue(Spot.class);

//                    if (isUserCloseToSpot(spot, menuSlider.getValue() * 1000)) {
                        allSpots.add(spot); // uncomment the if-statement later
//                    }
                }
                addSpotsOnMap(allSpots);
            }
        });
    }

    private void openSpotDialog(Spot spot) {
        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.open_spot_dialog, null);

        initOpenSpotDialogViews(popupView, spot);

        // show dialog
        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    private void initOpenSpotDialogViews(View popupView, Spot spot) {
        this.spotRatingTV = popupView.findViewById(R.id.openSpotDialogRatingTV);
        spotRatingTV.setText("Rating: " + spot.getRating());

        this.numberOfVisitorsTV = popupView.findViewById(R.id.openSpotDialogVisitorsTV);
        numberOfVisitorsTV.setText("Visitors: " + spot.getVisitors().size());

        this.visitSpotBTN = popupView.findViewById(R.id.openSpotDialogVisitBTN);
        visitSpotBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDataRepository.getInstance().visitSpot(spot);
                Toast.makeText(MapsActivity.this, "Spot is now visited!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        this.rateSpotBTN = popupView.findViewById(R.id.openSpotDialogRateBTN);
        rateSpotBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Disable the visit and rate btns if the user is far from the spot
        if (!isUserCloseToSpot(spot, ALLOWED_USER_SPOT_DISTANCE)) {
            rateSpotBTN.setEnabled(false);
            visitSpotBTN.setEnabled(false);
        }

        // Disable the visit btn if the user has found or already visited the spot
        if (spot.getCreatorID().equals(UserDataRepository.getInstance().getUser().getDbID())) {
            visitSpotBTN.setEnabled(false);
            visitSpotBTN.setText("Found");
        } else if (UserDataRepository.getInstance().getUser().getVisitedSpots().contains(spot.getDbID())) {
            visitSpotBTN.setEnabled(false);
            visitSpotBTN.setText("Visited");
        }

        // Configure features recycler view adapter
        this.spotFeaturesRV = popupView.findViewById(R.id.openSpotDialogRV);
        FeatureRVAdapter rvAdapter = new FeatureRVAdapter(spot.getFeatures());
        spotFeaturesRV.setAdapter(rvAdapter);
        spotFeaturesRV.setLayoutManager(new LinearLayoutManager(this));
    }

    private boolean isUserCloseToSpot(Spot spot, double maxDistance) {
        double[] spotLocation = spotLocStringToDouble(spot);
        float[] distanceResult = new float[3];
        Location.distanceBetween(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), spotLocation[0], spotLocation[1], distanceResult);
        System.out.println("DISTANCE = " + distanceResult[0]);
        return distanceResult[0] <= maxDistance;
    }

    private void initMenuViews() {
        this.menuSlider = findViewById(R.id.mapsMenuSlider);
        menuSlider.setValue(30);

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
        menuGridLayout.setRowCount(Feature.values().length / 2 + 1);
        menuGridLayout.setColumnCount(2);
        for (Feature features : Feature.values()) {
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
                filterSpots();
            }
        });
    }

    private void filterSpots() {
        List<String> selectedFeatures = getSelectedFeatures();

        List<Spot> filteredSpots;
        filteredSpots = allSpots.stream()
                .filter(spot -> isUserCloseToSpot(spot, menuSlider.getValue() * 1000) && spot.getFeatures().stream()
                        .anyMatch(feature -> selectedFeatures.contains(feature))).collect(Collectors.toList());

        addSpotsOnMap(filteredSpots);
    }

    private List<String> getSelectedFeatures() {
        return menuFeatureCheckBoxes.stream()
                .filter(checkBox -> checkBox.isChecked())
                .map(checkBox -> checkBox.getText().toString())
                .collect(Collectors.toList());
    }

    private void openAddSpotDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.add_spot_dialog, null);

        initAddSpotDialogViews(popupView);

        // show dialog
        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    private void initAddSpotDialogViews(View popupView) {
        this.addSpotDialogGridLayout = popupView.findViewById(R.id.addSpotDialogGridLayout);
        addSpotDialogGridLayout.setRowCount(Feature.values().length / 2 + 1);
        addSpotDialogGridLayout.setColumnCount(2);
        for (Feature features : Feature.values()) {
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
                saveSpot();
            }
        });

        this.addSpotDialogLocOptionsSpinner = popupView.findViewById(R.id.addSpotDialogLocationSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.location_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addSpotDialogLocOptionsSpinner.setAdapter(adapter);

        // Configure slider and its label
        this.addSpotRatingTV = popupView.findViewById(R.id.addSpotDialogRatingTV);
        this.addSpotRatingSlider = popupView.findViewById(R.id.addSpotRatingSlider);
        addSpotRatingTV.setText("Rating: " + addSpotRatingSlider.getValue());
        addSpotRatingSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                addSpotRatingTV.setText("Rating: " + addSpotRatingSlider.getValue());
            }
        });
    }

    private void saveSpot() {
        String location;
        if (addSpotDialogLocOptionsSpinner.getSelectedItemPosition() == 0) { // Get current location
            location = lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude();
        } else if (addSpotDialogLocOptionsSpinner.getSelectedItemPosition() == 1) { // Get location from map
            Toast.makeText(this, "This location option is not yet implemented!", Toast.LENGTH_LONG).show();
            return;
        } else {
            return;
        }

        List<String> newSpotFeatures = new ArrayList<>();
        for (CheckBox checkBox : addSpotDialogFeatureCheckBoxes) {
            if (checkBox.isChecked()) {
                newSpotFeatures.add(checkBox.getText().toString());
            }
        }

        if (newSpotFeatures.size() < 2) {
            Toast.makeText(this, "Choose at least 2 features!", Toast.LENGTH_LONG).show();
            return;
        }

        if (this.addSpotRatingSlider.getValue() < 4.5) {
            Toast.makeText(this, "What is the point in sharing a spot rated under 4.5 xD?", Toast.LENGTH_LONG).show();
            return;
        }

        // Create and save in db the new spot
        String newSpotID = spotDataReference.push().getKey();
        double rating = (double) this.addSpotRatingSlider.getValue();
        int numberOfRatings = 1;
        String creatorID = UserDataRepository.getInstance().getUser().getDbID();
        Spot newSpot = new Spot(newSpotFeatures, location, newSpotID, rating, numberOfRatings, Arrays.asList(creatorID), creatorID);
        spotDataReference.child(newSpotID).setValue(newSpot).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(MapsActivity.this, "The spot has been saved!", Toast.LENGTH_SHORT).show();
                Toast.makeText(MapsActivity.this, "Thank you for your contribution!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                allSpots.add(newSpot);
                addSpotsOnMap(allSpots);
            }
        });

        // Update the current user with a new found spot
        UserDataRepository.getInstance().getUser().addFoundSpot(newSpotID);
        UserDataRepository.getInstance().getUser().incrementNumberOfDoneRatings();
        UserDataRepository.getInstance().updateUserInDB();
    }

    private void addSpotsOnMap(List<Spot> spots) {
        mMap.clear();
        for (Spot spot : spots) {
            double[] latLng = spotLocStringToDouble(spot);
            double lat = latLng[0];
            double lng = latLng[1];

            Marker pin = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)));
            markerSpotMap.put(pin, spot);
        }
    }

    /**
     * Given Spot object converts the string location to array of doubles.
     * @param spot
     * @return - array of doubles where arr[0] = lat and arr[1] = lng
     */
    private double[] spotLocStringToDouble(Spot spot) {
        String[] latLngSTR = spot.getLocation().split(",");
        double[] latLng = new double[2];
        latLng[0] = Double.parseDouble(latLngSTR[0]);
        latLng[1] = Double.parseDouble(latLngSTR[1]);
        return latLng;
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (isLocationPermissionGranted()) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                requestLocationPermission();
            }
        } catch (SecurityException e)  {
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (isLocationPermissionGranted()) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), 15f));
                            }
                        } else {
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
        }
    }

    private boolean isLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_CODE);
    }
}