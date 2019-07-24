package com.salihttnc.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Looper;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;

import com.salihttnc.myapplication.Adapter.ViewPagerAdapter;
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
import com.salihttnc.myapplication.Common.Common1;

import java.util.List;

public class HavaDurumu1 extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CoordinatorLayout coordinatorLayout;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.havadurumu);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.root_view);

        //request permission
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            buildLocationRequest();
                            buildLocationCallBack();

                            if (ActivityCompat.checkSelfPermission(HavaDurumu1.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HavaDurumu1.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                return;
                            }
                            fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient((HavaDurumu1.this));
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
        locationCallback=new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Common1.current_location=locationResult.getLastLocation();
                viewPager=(ViewPager)findViewById(R.id.view_pager);
                setupViewPager(viewPager);
                tabLayout=(TabLayout)findViewById(R.id.tabs);
                tabLayout.setupWithViewPager(viewPager );

                Log.d("Location",locationResult.getLastLocation().getLatitude()+"/"+locationResult.getLastLocation().getLongitude());

            }
        };


    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(TodayWeatherFragment1.getInstance(),"Bugün");
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
