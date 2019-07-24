package com.salihttnc.myapplication;

import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double editLng, editLat, editRadius;
    private String key_global;
    String guncelBaslik, guncelNot;
    private DatabaseReference mDatabaseRef;
    private Not not;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps3);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mDatabaseRef=FirebaseDatabase.getInstance().getReference().child("Notes").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Notlar");
    }

    private void setInfo() {

        key_global = getIntent().getStringExtra("key");
        Log.d("tag", "setInfo: PersonEdit");
        Toast.makeText(this,"deger key :"+key_global,Toast.LENGTH_LONG).show();
        mDatabaseRef.child(key_global).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                not = new Not();
                not = dataSnapshot.getValue(Not.class);
                editRadius = (Double.parseDouble(not.getRadius()));
                editLat = (Double.parseDouble(not.getLat()));
                editLng = Double.parseDouble(not.getLng());
                final LatLng latLng1 = new LatLng(editLat, editLng);
                final MarkerOptions userMarkerOption1 = new MarkerOptions();
                userMarkerOption1.position(latLng1);
                userMarkerOption1.title(not.getTittle());
                userMarkerOption1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                CircleOptions circle1 = new CircleOptions();
                circle1.center(new LatLng(editLat, editLng));
                circle1.radius(editRadius);
                circle1.fillColor(Color.TRANSPARENT);
                circle1.strokeWidth(6);
                mMap.addCircle(circle1);
                Log.d("tag", "onDataChange: PersonEditSetInfo ");
                mMap.addMarker(userMarkerOption1);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng1,15),1000,null);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
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
        public void onMapReady (GoogleMap googleMap){
            mMap = googleMap;

            // Add a marker in Sydney and move the camera
            setInfo();
        }

}
