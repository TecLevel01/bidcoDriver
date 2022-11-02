package com.bidco.bidcodriverapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    //Location Variable
    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    FirebaseUser myUser = FirebaseAuth.getInstance().getCurrentUser();
    String uid = myUser != null ? myUser.getUid() : null;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    DocumentReference myRef;

    //Interface variables
    EditText nPlate, itemName;
    Button start;
    FirebaseFirestore mDb;
    private FirebaseUser driver;
    private HashMap<String, Object> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nPlate = findViewById(R.id.nPlat);
        itemName = findViewById(R.id.item);
        mDb = FirebaseFirestore.getInstance();
        getField();


        start = findViewById(R.id.startBtn);

        start.setOnClickListener(view -> {
            //geting user id
            driver = FirebaseAuth.getInstance().getCurrentUser();
            if (driver != null) {

                MyProgressDialog dialog = new MyProgressDialog(this, "Adding Trip Details");
                getField();

                mDb.collection("history").document(driver.getUid()).collection("history")
                        .add(map).addOnCompleteListener(task -> {
                            String msg;
                            if (task.isSuccessful()) {
                                msg = "Trip Details Added";
                            } else {
                                msg = task.getException().getMessage();
                            }
                            startActivity(new Intent(this, start_trip.class));
                            finish();
//                            onGoing = myDialog(this, R.layout.activity_start_trip);
                            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        });

            }
        });

        if (myUser != null) {
            myRef = FirebaseFirestore.getInstance().collection("driverLocation").document(uid);
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                onGPS();
            } else {
                //GPS is on then
                getLocation();
                getLocationUpdates();
                startLocationUpdates();
            }

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);


            //----------------------------------
        }

    }


    //Capture fields
    private void getField() {

        String NPlate = nPlate.getText().toString().trim(),
                ItemName = itemName.getText().toString().trim();

        if (NPlate.isEmpty()) {
            nPlate.setError("Number Plate Required!");
            nPlate.requestFocus();
            return;
        }
        if (ItemName.isEmpty()) {
            itemName.setError("Item Name Required!");
            itemName.requestFocus();
            return;
        }
        map = new HashMap<>();
        map.put("nPlate", NPlate);
        map.put("item", ItemName);
        map.put("timestamp", new Date());
    }

    public static class MyProgressDialog extends ProgressDialog {
        public MyProgressDialog(Context context, String msg) {
            super(context);
            getWindow().setBackgroundDrawableResource(R.drawable.rounded_all);
            setMessage(msg + "...");
            show();
        }
    }

    private void onGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", (dialogInterface, i) ->
                startActivity(new Intent(Settings.ACTION_LOCALE_SETTINGS))).setNegativeButton("No",
                (dialogInterface, i) -> dialogInterface.cancel());
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void getLocationUpdates() {
        createLocationRequest();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    Double lat = location.getLatitude();
                    Double log = location.getLongitude();
                    dLocate dLoc = new dLocate(lat, log);
                    myRef.set(dLoc);
                }
            }
        };
    }

    private void getLocation() {
        //check permission
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            Location LocationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location LocationNetProv = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location LocationPsv = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (LocationGps != null) {
                Double lat = LocationGps.getLatitude();
                Double log = LocationGps.getLongitude();
                dLocate dLoc = new dLocate(lat, log);
                myRef.set(dLoc);
            } else if (LocationNetProv != null) {
                Double lat = LocationNetProv.getLatitude();
                Double log = LocationNetProv.getLongitude();
                dLocate dLoc = new dLocate(lat, log);
                myRef.set(dLoc);
            } else if (LocationPsv != null) {
                Double lat = LocationPsv.getLatitude();
                Double log = LocationPsv.getLongitude();
                dLocate dLoc = new dLocate(lat, log);

                myRef.set(dLoc);
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            FusedLocationProviderClient
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            //check if the user is logged in
            user.reload();
        }else{
            Intent intent = new Intent(this, splash.class);
            startActivity(intent);
            finish();
        }
    }
}
