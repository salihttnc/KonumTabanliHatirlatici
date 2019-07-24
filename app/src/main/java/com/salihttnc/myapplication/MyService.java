package com.salihttnc.myapplication;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MyService extends Service {

    private DatabaseReference databaseReference;
    private double latitude;
    private double longitude;

    private LocationRequest mLocationRequest;
    private static final long UPDATE_INTERVAL = 1;
    private static final long FASTEST_INTERVAL = 5;

    //   private Geocoder geocoder;
//    private List<Address> addresses;

    private FusedLocationProviderClient mFusedLocationClient;

    public MyService() {

    }

    @Override
    public void onCreate() {
        mFusedLocationClient = getFusedLocationProviderClient(this);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Location");

        startLocationUpdates();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Toast.makeText(this,"Service running",Toast.LENGTH_SHORT).show();

        getLastLocation();

        databaseReference.child("latitude").setValue(latitude).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
        databaseReference.child("longitude").setValue(longitude).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });


        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @SuppressWarnings("MissingPermission")
    public void getLastLocation() {

//        mFusedLocationClient = getFusedLocationProviderClient(this);

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            Log.d("location ","deger " +location);
                            onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("log","error getting GPS location");
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this,"Service stopped",Toast.LENGTH_SHORT).show();
    }

    public void onLocationChanged(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
    }

    protected void startLocationUpdates() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);


    }

}

