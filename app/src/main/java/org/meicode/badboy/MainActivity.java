package org.meicode.badboy;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.anastr.speedviewlib.TubeSpeedometer;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;

import android.location.LocationListener;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {


    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private GoogleMap map;
    private TextView dateTimeTextView;
    private BarChart barChart;
    private ImageView musicIcon;


    static ArrayList<MusicFiles> musicFiles;
    static boolean shuffleBoolean=false,repeatBoolean=false;
    static ArrayList<MusicFiles> albums=new ArrayList<>();

    private TubeSpeedometer speedView;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private DrawerLayout drawerLayout;
    private CircularProgressIndicator circularProgressIndicator;
    private int i = 0; // Current progress
    private boolean isCharging = true;

    private View batteryView;

    private ImageView batteryicon,batteryicon1;

    private static final int REQUEST_CODE_ENABLE_ADMIN = 1;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName adminComponent;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private TextView distanceDurationTextView,endAddressTextView,speedTextView,progressText,progressText2,kmText,kmTxt;

    private List<Polyline> polylines = new ArrayList<>();
    private Handler handler = new Handler();
    private Runnable runnable;
    private LinearLayout linearLayout;
    private Button startButton,directionsButton; // Start button reference
    private LatLng destinationLatLng;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        drawerLayout = findViewById(R.id.main_drawer);
        ImageView menuIcon = findViewById(R.id.menu);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    drawerLayout.openDrawer(GravityCompat.END);
                }
            }
        });
        NavigationView navigationView = findViewById(R.id.navigation_view);


        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            View view = findViewById(android.R.id.content);

            // Show different Snackbar messages for each item
            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(MainActivity.this, ServerActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.nav_profile) {
                Intent intent = new Intent(MainActivity.this, RgbLightActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.nav_settings) {
                Snackbar.make(view, "Settings selected", Snackbar.LENGTH_SHORT).show();
            }

            // Close the drawer after selection
            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });

        // Initialize the Places SDK
        Places.initialize(getApplicationContext(), "AIzaSyDiStP6t6PY1R9P7brttwu2_V65ZJeJDUI");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        distanceDurationTextView = findViewById(R.id.tv_distance_duration);
        distanceDurationTextView.setVisibility(View.GONE);
        endAddressTextView=findViewById(R.id.endAddress);
        endAddressTextView.setVisibility(View.GONE);
        linearLayout=findViewById(R.id.linear);
        linearLayout.setVisibility(View.GONE);
        // Set up the Search Places button
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        PlacesClient placesClient = Places.createClient(this);
        dateTimeTextView = findViewById(R.id.date_time_text);
        startLiveDateTimeUpdate();
        speedTextView = findViewById(R.id.speedTxt); // Speed TextView
        speedView = findViewById(R.id.speedView); //

        // Set an OnClickListener for the search button
        findViewById(R.id.btn_search_places).setOnClickListener(v -> openAutocompleteActivity());
        updateDateTime();
        barChart = findViewById(R.id.barChart);

        setupBarChart();
        musicIcon = findViewById(R.id.music_icon);
        musicIcon.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MusicActivity.class);
            startActivity(intent);
        });
        Places.initialize(getApplicationContext(), "AIzaSyDiStP6t6PY1R9P7brttwu2_V65ZJeJDUI");


        // Set up the Google Map
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Set up Google Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.id_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize Location Callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) return;

                Location location = locationResult.getLastLocation();
                if (location != null) {
                    updateSpeed(location);
                }
            }
        };

        // Check permissions and start location updates
        startLocationUpdates();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_drawer), (v, insets) -> {
            int leftPadding = insets.getInsets(WindowInsetsCompat.Type.systemBars()).left;
            int topPadding = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            int rightPadding = insets.getInsets(WindowInsetsCompat.Type.systemBars()).right;
            int bottomPadding = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;

            v.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
            return insets;
        });

        circularProgressIndicator = findViewById(R.id.circularProgressIndicator);
        progressText = findViewById(R.id.batteryTxt);
        progressText2 = findViewById(R.id.progressText);
        kmText=findViewById(R.id.km_text);
        kmTxt=findViewById(R.id.kmTxt);
        batteryView=findViewById(R.id.batteryView);
        batteryicon=findViewById(R.id.battery_icon);
        batteryicon1=findViewById(R.id.battery_icon1);

        // Rotate the CircularProgressIndicator to make it appear to start from the bottom left
        circularProgressIndicator.setRotation(184f);

//        // Set default colors for the CircularProgressIndicator
//        circularProgressIndicator.setIndicatorColor(Color.parseColor("#FF5722")); // Default orange color for indicator
//        circularProgressIndicator.setTrackColor(Color.parseColor("#BDBDBD")); // Default light gray color for track

        // Handler to update progress every 2 seconds
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (i <= 100 && i >= 0) {
                    // Update progress text and color based on progress percentage
                    progressText.setText(i + " %");
                    progressText2.setText(i + " %");
//                    kmText.setText(i + " %");
//                    kmTxt.setText(i + " %");


                    // Change text color based on the progress level
                    if (i <= 20) {
                        progressText.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.redColor));
                        progressText2.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.redColor));
                        kmText.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.redColor));
                        kmTxt.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.redColor));
                        batteryView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.redColor));
                        circularProgressIndicator.setIndicatorColor(ContextCompat.getColor(MainActivity.this, R.color.redColor));
                        batteryicon.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.redColor));
                        batteryicon1.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.redColor));// Red indicator
                    } else if (i <= 40) {
                        progressText.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.orangeColor));
                        progressText2.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.orangeColor));
                        kmText.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.orangeColor));
                        kmTxt.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.orangeColor));
                        batteryView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.orangeColor));// Orange
                        circularProgressIndicator.setIndicatorColor(ContextCompat.getColor(MainActivity.this, R.color.orangeColor));
                        batteryicon.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.orangeColor));
                        batteryicon1.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.orangeColor));// Orange indicator
                    } else {
                        progressText2.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.blueColor));
                        progressText.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.blueColor));
                        kmText.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.blueColor));
                        kmTxt.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.blueColor));
                        batteryView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.blueColor));
                        circularProgressIndicator.setIndicatorColor(ContextCompat.getColor(MainActivity.this, R.color.blueColor));
                        batteryicon.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.blueColor));
                        batteryicon1.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.blueColor));
                    }

                    // Update progress on the CircularProgressIndicator
                    circularProgressIndicator.setProgress(i);

                    // Simulate charging or discharging
                    if (isCharging) {
                        i++; // Increase progress if charging
                    } else {
                        i--; // Decrease progress if discharging
                    }

                    // Toggle between charging and discharging after reaching max or min progress
                    if (i >= 100) {
                        isCharging = false; // Start discharging when full
                    } else if (i <= 0) {
                        isCharging = true; // Start charging when empty
                    }

                    handler.postDelayed(this, 2000); // Delay for 2 seconds
                } else {
                    handler.removeCallbacks(this);
                }
            }
        }, 2000);
        enableDeviceAdmin();
        startButton = findViewById(R.id.start);
        startButton.setOnClickListener(v -> focusOnCurrentLocation());
        // Set up the Directions button
        directionsButton = findViewById(R.id.directions);
        directionsButton.setOnClickListener(v -> focusOnDestination());

    }
    private void focusOnDestination() {
        if (destinationLatLng != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(destinationLatLng, 15));
        } else {
            Toast.makeText(this, "Destination not set. Please search for a place first.", Toast.LENGTH_SHORT).show();
        }
    }

    private void focusOnCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                // Update the camera position to focus on the current location
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));

                // Add a marker at the current location
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(currentLatLng) // Set the position of the marker
                        .title("Your Location")  // Add a title to the marker
                        .snippet("You are here!") // Add a snippet (optional subtitle)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)); // Optional: Custom icon or color

                // Add the marker to the map
                map.addMarker(markerOptions);
            } else {
                Toast.makeText(this, "Unable to fetch current location", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void enableDeviceAdmin() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Enable device admin to secure your device.");
        startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
    }
    private void updateSpeedFromLocation(Location location) {
        if (location.hasSpeed()) {
            float speed = location.getSpeed() * 3.6f; // Convert m/s to km/h
            speedTextView.setText(String.format(Locale.getDefault(), "Speed: %.2f km/h", speed));
        } else {
            speedTextView.setText("Speed: Not Available");
        }
    }
    private void startLiveDateTimeUpdate() {
        runnable = new Runnable() {
            @Override
            public void run() {
                // Get current date and time
                String currentDateTime = new SimpleDateFormat("hh:mm a dd MMM yyyy", Locale.getDefault()).format(new Date());
                // Update the TextView
                dateTimeTextView.setText(currentDateTime);
                // Schedule the runnable again after 1 second
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);
    }
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            updateSpeedFromLocation(location);
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // Starting location updates
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationRequest locationRequest = LocationRequest.create()
                    .setInterval(1000) // Update every second
                    .setFastestInterval(500) // Fastest update rate
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } else {
            // Request permissions if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    private void updateSpeed(Location location) {
        int speedInKmH = (int) (location.getSpeed() * 3.6); // Convert m/s to km/h
        speedTextView.setText( speedInKmH + "");
        speedView.speedTo(speedInKmH); // Update speed on TubeSpeedometer
    }

    private void setMapStyle() {
        try {
            // Load the style from the JSON file in the res/raw folder
            boolean success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));

            if (!success) {
                Log.e("MapStyle", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapStyle", "Can't find style. Error: ", e);
        }
    }

    private void updateDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a dd-MMM-yyyy", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());
        dateTimeTextView.setText(currentDateAndTime);
    }

    // Open the Autocomplete activity
    private void openAutocompleteActivity() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng defaultLocation = new LatLng(-34, 151); // Default location: Sydney
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));

        // Enable location if permission granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(false);
        }
        setMapStyle();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            if (place.getLatLng() != null) {
                destinationLatLng = place.getLatLng();

                clearExistingPolylines();
                map.addMarker(new MarkerOptions().position(destinationLatLng).title(place.getName()));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLatLng, 15));
                fetchRouteToDestination(destinationLatLng);
                distanceDurationTextView.setVisibility(View.VISIBLE);
                endAddressTextView.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void fetchRouteToDestination(LatLng destinationLatLng) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                        "origin=" + currentLatLng.latitude + "," + currentLatLng.longitude +
                        "&destination=" + destinationLatLng.latitude + "," + destinationLatLng.longitude +
                        "&key=AIzaSyDiStP6t6PY1R9P7brttwu2_V65ZJeJDUI";

                new FetchDirectionsTask(url).execute();
            } else {
                Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class FetchDirectionsTask extends AsyncTask<Void, Void, String> {
        private final String url;

        FetchDirectionsTask(String url) {
            this.url = url;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.connect();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                return result.toString();
            } catch (Exception e) {
                Log.e("DirectionsAPI", "Error: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                JSONArray legs = jsonResponse.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
                JSONObject leg = legs.getJSONObject(0);
                String distance = leg.getJSONObject("distance").getString("text");
                String duration = leg.getJSONObject("duration").getString("text");
                String endAddress = leg.getString("end_address");

                JSONArray steps = leg.getJSONArray("steps");
                StringBuilder instructions = new StringBuilder();
                for (int i = 0; i < steps.length(); i++) {
                    JSONObject step = steps.getJSONObject(i);
                    String instruction = step.getString("html_instructions");
                    instructions.append(instruction).append("\n\n");
                }

                JSONObject overviewPolyline = jsonResponse.getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline");
                String encodedPolyline = overviewPolyline.getString("points");
                List<LatLng> polylinePoints = decodePolyline(encodedPolyline);

                    distanceDurationTextView.setText(duration + " - " + distance);
                endAddressTextView.setText(endAddress);


                Polyline polyline = map.addPolyline(new PolylineOptions()
                        .addAll(polylinePoints)
                        .color(Color.BLUE)
                        .width(8));
                polylines.add(polyline);

            } catch (Exception e) {
                Log.e("DirectionsAPI", "Error parsing response: " + e.getMessage());
            }
        }
    }

    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> polyline = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            polyline.add(new LatLng((lat / 1E5), (lng / 1E5)));
        }
        return polyline;
    }

    private void clearExistingPolylines() {
        for (Polyline polyline : polylines) {
            polyline.remove();
        }
        polylines.clear();
        map.clear();
//        instructionsTextView.setText("");
        distanceDurationTextView.setText("");
        endAddressTextView.setText("");
    }
    private void setupBarChart() {
        ArrayList<BarEntry> positiveEntries = new ArrayList<>();
        ArrayList<BarEntry> negativeEntries = new ArrayList<>();

        // Add sample data
        positiveEntries.add(new BarEntry(0, 20f));  // Positive side
        negativeEntries.add(new BarEntry(1, -12f)); // Negative side
        positiveEntries.add(new BarEntry(2, 13f));
        negativeEntries.add(new BarEntry(3, -14f));
        positiveEntries.add(new BarEntry(4, 16f));
        negativeEntries.add(new BarEntry(5, -80f));
        positiveEntries.add(new BarEntry(6, 10f));
        positiveEntries.add(new BarEntry(7, 15f));
        positiveEntries.add(new BarEntry(8, 16f));
        negativeEntries.add(new BarEntry(9, -22f));
        positiveEntries.add(new BarEntry(10, 18f));
        positiveEntries.add(new BarEntry(11, 10f));
        negativeEntries.add(new BarEntry(12, -5f));
        negativeEntries.add(new BarEntry(13, -60f));
        negativeEntries.add(new BarEntry(14, -80f));
        negativeEntries.add(new BarEntry(15, -10f));
        negativeEntries.add(new BarEntry(16, -20f));
        negativeEntries.add(new BarEntry(17, -50f));
        positiveEntries.add(new BarEntry(18, 10f));
        positiveEntries.add(new BarEntry(19, 8f));
        positiveEntries.add(new BarEntry(20, 10f));
        positiveEntries.add(new BarEntry(21, 15f));
        positiveEntries.add(new BarEntry(22, 16f));
        negativeEntries.add(new BarEntry(23, -22f));
        positiveEntries.add(new BarEntry(24, 18f));
        positiveEntries.add(new BarEntry(25, 10f));
        negativeEntries.add(new BarEntry(26, -5f));
        negativeEntries.add(new BarEntry(27, -60f));
        negativeEntries.add(new BarEntry(28, -80f));
        negativeEntries.add(new BarEntry(29, -10f));
        negativeEntries.add(new BarEntry(30, -20f));
        negativeEntries.add(new BarEntry(31, -50f));
        positiveEntries.add(new BarEntry(32, 10f));
        positiveEntries.add(new BarEntry(33, 8f));
        negativeEntries.add(new BarEntry(35, -10f));
        negativeEntries.add(new BarEntry(36, -20f));
        negativeEntries.add(new BarEntry(37, -50f));
        positiveEntries.add(new BarEntry(38, 10f));
        positiveEntries.add(new BarEntry(39, 8f));
        positiveEntries.add(new BarEntry(40, 10f));
        positiveEntries.add(new BarEntry(41, 15f));
        positiveEntries.add(new BarEntry(42, 16f));
        negativeEntries.add(new BarEntry(43, -22f));
        positiveEntries.add(new BarEntry(44, 18f));
        positiveEntries.add(new BarEntry(45, 10f));
        negativeEntries.add(new BarEntry(46, -5f));
        negativeEntries.add(new BarEntry(47, -60f));
        negativeEntries.add(new BarEntry(48, -80f));
        negativeEntries.add(new BarEntry(49, -10f));
        negativeEntries.add(new BarEntry(50, -20f));
        negativeEntries.add(new BarEntry(51, -50f));
        positiveEntries.add(new BarEntry(52, 10f));
        positiveEntries.add(new BarEntry(53, 8f));
        negativeEntries.add(new BarEntry(54, -10f));
        negativeEntries.add(new BarEntry(55, -20f));
        negativeEntries.add(new BarEntry(56, -50f));
        positiveEntries.add(new BarEntry(57, 10f));
        positiveEntries.add(new BarEntry(58, 8f));
        positiveEntries.add(new BarEntry(59, 10f));
        positiveEntries.add(new BarEntry(60, 15f));
        positiveEntries.add(new BarEntry(61, 16f));
        negativeEntries.add(new BarEntry(62, -22f));
        positiveEntries.add(new BarEntry(63, 18f));
        positiveEntries.add(new BarEntry(64, 10f));
        negativeEntries.add(new BarEntry(65, -5f));
        negativeEntries.add(new BarEntry(66, -5f));
        negativeEntries.add(new BarEntry(67, -80f));
        negativeEntries.add(new BarEntry(68, -10f));
        negativeEntries.add(new BarEntry(69, -20f));
        negativeEntries.add(new BarEntry(70, -50f));
        positiveEntries.add(new BarEntry(71, 10f));
        positiveEntries.add(new BarEntry(72, 8f));

        // Create dataset for positive entries
        BarDataSet positiveDataSet = new BarDataSet(positiveEntries, "Positive");
        positiveDataSet.setColor(ContextCompat.getColor(MainActivity.this, R.color.blueColor)); // Set color for positive values

        // Create dataset for negative entries
        BarDataSet negativeDataSet = new BarDataSet(negativeEntries, "Negative");
        negativeDataSet.setColor(ContextCompat.getColor(MainActivity.this, R.color.redColor)); // Set color for negative values

        // Combine datasets into BarData object
        BarData data = new BarData(positiveDataSet, negativeDataSet);
        barChart.setData(data);
        data.setBarWidth(0.1f); // Set the width of the bars

        // Hide values on top of bars
        data.setDrawValues(false);

        // Configure X-axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false); // Hide X-axis line
        xAxis.setDrawLabels(false); // Hide X-axis labels

        // Configure Y-axis
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false); // Hide grid lines
        leftAxis.setDrawAxisLine(false); // Hide left Y-axis line
        leftAxis.setDrawLabels(false); // Hide left Y-axis labels

        // Set the right Y-axis to false as we are using the left side
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false); // Disable right Y-axis

        // Hide the chart description and legend
        barChart.getDescription().setEnabled(false); // Hide description
        barChart.getLegend().setEnabled(false); // Hide legend

        // Refresh the chart
        barChart.invalidate(); // Refresh the chart to apply changes
    }
}