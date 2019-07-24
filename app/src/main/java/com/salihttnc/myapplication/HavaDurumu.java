package com.salihttnc.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.salihttnc.myapplication.Adapter.ViewPagerAdapter;
import com.salihttnc.myapplication.Common.Common;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class HavaDurumu extends AppCompatActivity {
    private double lat,lng;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CoordinatorLayout coordinatorLayout;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private String key_global;
    String guncelBaslik, guncelNot;
    private DatabaseReference mDatabaseRef;
    private Not not;
    LatLng latLng;
    private  Handler handler;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.havadurumu);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.root_view);
        mDatabaseRef=FirebaseDatabase.getInstance().getReference().child("Notes").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Notlar");
        //request permission

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            buildLocationRequest();
                            buildLocationCallBack();


                            if (ActivityCompat.checkSelfPermission(HavaDurumu.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HavaDurumu.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                return;
                            }
                            fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient((HavaDurumu.this));
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Snackbar.make(coordinatorLayout,"permission denied",Snackbar.LENGTH_LONG)
                                .show();
                    }
                }).check();





    }

    private void buildLocationCallBack() {
        key_global = getIntent().getStringExtra("key");
        locationCallback=new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

//                Common.current_location=locationResult.getLastLocation();



                Log.d("Location",locationResult.getLastLocation().getLatitude()+"/"+locationResult.getLastLocation().getLongitude());
                Log.d("L1111","deger"+locationResult);
            }
        };
        mDatabaseRef.child(key_global).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                not = new Not();
                not = dataSnapshot.getValue(Not.class);
                lat=Double.parseDouble(not.getLat());
                lng=Double.parseDouble(not.getLng());
                Log.d("degerler ","deger"+lat+lng);
                latLng=new LatLng(lat,lng);
                Common.current_location=latLng;

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        viewPager=(ViewPager)findViewById(R.id.view_pager);
        setupViewPager(viewPager);
        tabLayout=(TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager );

    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(TodayWeatherFragment.getInstance(),"Bugün");
        viewPager.setAdapter(adapter);


    }

    private void buildLocationRequest() {
        locationRequest=new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10.0f);
    }


}
