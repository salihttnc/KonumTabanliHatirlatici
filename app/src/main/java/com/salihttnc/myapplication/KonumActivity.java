package com.salihttnc.myapplication;

import android.Manifest;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KonumActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "KonumActivity";
    private static final int REQUEST_LOCATION_PERMISSION_CODE = 101;

    private GoogleMap googleMap;

    private GeofencingRequest geofencingRequest;
    private GoogleApiClient googleApiClient;

    private boolean isMonitoring = false;
    ArrayList<Not> mNotes;
    private MarkerOptions markerOptions;


    private Marker currentLocationMarker;
    private PendingIntent pendingIntent;
    public ArrayList geofenceList = new ArrayList();

    ProgressDialog progressDialog;

    public DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Notes/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Notlar");

    public HashMap<String, LatLng> AREA_LANDMARKS = new HashMap<String, LatLng>();
    public static ArrayList<Not> liste = new ArrayList();
    Not not, not1;
//    public void firebaseDoldur() {
//
//
//        NoteClass noteClass = new NoteClass();
//        noteClass.setLat(41.2246699);
//        noteClass.setLng(32.6658782);
//        noteClass.setRadius(10000f);
//        noteClass.setNoteKey("dsads");
//        AREA_LANDMARKS.put(noteClass.getNoteKey(),new LatLng(noteClass.getLat(),noteClass.getLng()));
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konum);
//        mSensorService = new DenemeService();
        liste = new ArrayList<>();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

//        firebaseDoldur();
        mNotes = new ArrayList<>();
        mapFragment.getMapAsync(this);

        progressDialog = new ProgressDialog(KonumActivity.this);
        progressDialog.setMessage("Lütfen bekleyin notunuz kaydediliyor.");
        progressDialog.show();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Notes").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Notlar");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                not = new Not();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        not = ds.getValue(Not.class);
                        mNotes.add(not);

                    }
                }
                getGeofence(degiskenFonksiyon(mNotes));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION_CODE);
        }

    }
    private HashMap<String, String> guncelHash = new HashMap<>();
    private HashMap<String, String> guncelHash1 = new HashMap<>();
    public HashMap<String, LatLng> degiskenFonksiyon(ArrayList<Not> abcd) {
        for (int i = 0; i < abcd.size(); i++) {
            guncelHash.put(abcd.get(i).getTittle(), abcd.get(i).getRadius());
            AREA_LANDMARKS.put(abcd.get(i).getTittle(), new LatLng(Double.parseDouble(abcd.get(i).getLat()), Double.parseDouble(abcd.get(i).getLng())));
            guncelHash1.put(abcd.get(i).getTittle(), abcd.get(i).getDate());
        }
        Log.d("samet", "size: " + abcd.size());
        return AREA_LANDMARKS;
    }

    public void startLocationMonitor() {
        Log.d(TAG, "start location monitor-->");
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(2000)
                .setFastestInterval(1000)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    if (currentLocationMarker != null) {
                        currentLocationMarker.remove();
                    }
                    markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
                    markerOptions.title("Current Location");
                    currentLocationMarker = googleMap.addMarker(markerOptions);
                    Log.d("KonumDegisti2", "Location Change Lat Lng " + location.getLatitude() + " " + location.getLongitude());
                }
            });
        } catch (SecurityException e) {
            Log.d(TAG, e.getMessage());
        }

    }

    private void startGeofencing() {
        Log.d(TAG, "Start geofencing monitoring call");
        pendingIntent = getGeofencePendingIntent();
        geofencingRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER)
                .addGeofences(geofenceList)
                .build();

        if (!googleApiClient.isConnected()) {
            Log.d(TAG, "Google API client not connected");
        } else {
            try {
                LocationServices.GeofencingApi.addGeofences(googleApiClient, geofencingRequest, pendingIntent).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.d(TAG, "Successfully Geofencing Connected");


                        } else {
                            Log.d(TAG, "Failed to add Geofencing " + status.getStatus());
                        }
                    }
                });
            } catch (SecurityException e) {
                Log.d(TAG, e.getMessage());
            }
        }
        isMonitoring = true;
        invalidateOptionsMenu();
    }

//    private GeofencingRequest getGeofencingRequest() {
//        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
//        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
//        builder.addGeofences(geofenceList);
//        return builder.build();
//    }

    @NonNull
    private void getGeofence(HashMap<String,LatLng> AREA_LANDMARKS) {
        Log.d("sizeHash: ", "" + AREA_LANDMARKS.size());
        for (Map.Entry<String, LatLng> entry : AREA_LANDMARKS.entrySet()) {
            geofenceList.add(new Geofence.Builder()
                    .setRequestId(entry.getKey())
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Float.parseFloat(guncelHash.get(entry.getKey()))
                    )
                    .setExpirationDuration(999999999)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
    }

    private PendingIntent getGeofencePendingIntent() {
        if (pendingIntent != null) {
            return pendingIntent;
        }
        Intent intent = new Intent(this, GeofenceRegistrationService.class);


        return PendingIntent.getService(this, 0, intent,PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    private void stopGeoFencing() {
        pendingIntent = getGeofencePendingIntent();
        LocationServices.GeofencingApi.removeGeofences(googleApiClient, pendingIntent)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess())
                            Log.d(TAG, "Stop geofencing");
                        else
                            Log.d(TAG, "Not stop geofencing");
                    }
                });
        isMonitoring = false;
        invalidateOptionsMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int response = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(KonumActivity.this);
        if (response != ConnectionResult.SUCCESS) {
            Log.d(TAG, "Google Play Service Not Available");
            GoogleApiAvailability.getInstance().getErrorDialog(KonumActivity.this, response, 1).show();
        } else {
            Log.d(TAG, "Google play service available");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.reconnect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manu_map_activity, menu);
        if (isMonitoring) {
            menu.findItem(R.id.action_start_monitor).setVisible(false);
            menu.findItem(R.id.action_stop_monitor).setVisible(true);
        } else {
            menu.findItem(R.id.action_start_monitor).setVisible(true);
            menu.findItem(R.id.action_stop_monitor).setVisible(false);
        }
        return true;
    }

    private GoogleMap mMap;
    private UiSettings mUiSettings;
    Double defaultLatMap = 39.7112228, defaultLongMap = 36.339064;

    @Override
    public void onMapReady(GoogleMap googleMap) {
//        AREA_LANDMARKS2 = firebaseDoldur();
        mMap = googleMap;

        // Toast.makeText(KonumActivity.this,"size: " + liste.size(),Toast.LENGTH_LONG).show();

        LatLng defaultLocation = new LatLng(defaultLatMap, defaultLongMap);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 4.5f));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        this.googleMap = googleMap;
        mMap.setMyLocationEnabled(true);

        mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Google Api Client Connected");
        isMonitoring = true;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startGeofencing(); dbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dS : dataSnapshot.getChildren()) {
                            LatLng latLng = new LatLng(Double.parseDouble(dS.child("lat").getValue().toString()), Double.parseDouble(dS.child("lng").getValue().toString()));
                            mMap.addMarker(new MarkerOptions().position(latLng).title(dS.child("tittle").getValue().toString()));
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f));

                            Circle circle = mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(latLng.latitude, latLng.longitude))
                                    .radius(Float.parseFloat(dS.child("radius").getValue().toString()))
                                    .strokeColor(Color.RED)
                                    .strokeWidth(4f));
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                startLocationMonitor();
            }
        }, 3000);

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Google Connection Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        isMonitoring = false;
        Log.e(TAG, "Connection Failed:" + connectionResult.getErrorMessage());
    }


}