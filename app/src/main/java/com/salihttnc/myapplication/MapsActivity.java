package com.salihttnc.myapplication;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MapsActivity extends FragmentActivity implements
        DatePickerDialog.OnDateSetListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private EditText edtText, tittle;
    private Button btnKaydet;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker anlıkNokta;
    private DatabaseReference rRef;
    private Not yeniNot;
    private static final int Request_Kullanıcı_Konum_Code = 99;
    long maxid = 0;
    private String userId;
    private TextView textView;
    private SeekBar seekBar;
    double deger;
    double deger2;
    String currentDateString;
    private ImageView button;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        edtText = findViewById(R.id.edtText);
        btnKaydet = findViewById(R.id.btnKaydet);
        tittle = findViewById(R.id.tittle);
        seekBar=findViewById(R.id.seekBar);
        textView=findViewById(R.id.txtView);
        seekBar.setMax(10000);
        seekBar.setMin(100);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkKisiKonunİzin();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        yeniNot = new Not();
        rRef = FirebaseDatabase.getInstance().getReference().child("Notes").child(userId).child("Notlar");
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              showDatePickerDialog();
            }
        });

    }
    public void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());


        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        SimpleDateFormat bugun = new SimpleDateFormat("dd/MM/yyyy");
        String currentDateString = bugun.format(c.getTime());
        Date tarih = new Date();
        yeniNot.setDate(bugun.format(c.getTime()));
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(currentDateString);
    }
    public void onClick(View v) {
        mMap.clear();
        switch (v.getId()) {
            case R.id.adres_arama:
                EditText adresalan = (EditText) findViewById(R.id.konum_arama);
                final String address = adresalan.getText().toString();
                List<Address> addressList = null;
                final MarkerOptions userMarkerOption = new MarkerOptions();
                if (!TextUtils.isEmpty(address)) {
                    Geocoder geocoder = new Geocoder(this);
                    try {
                        addressList = geocoder.getFromLocationName(address, 6);
                        if (addressList != null) {
                            for (int i = 0; i < addressList.size(); i++) {
                                final Address kullanıcıAdres = addressList.get(i);
                                final LatLng latLng = new LatLng(kullanıcıAdres.getLatitude(), kullanıcıAdres.getLongitude());
                                double lat11=kullanıcıAdres.getLatitude();
                                double lng11=kullanıcıAdres.getLongitude();
                                Toast.makeText(this, "pozisyon: " + kullanıcıAdres.getLatitude() + kullanıcıAdres.getLongitude(), Toast.LENGTH_SHORT).show();
                                userMarkerOption.position(latLng);
                                userMarkerOption.title(address);
                                userMarkerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                                mMap.addMarker(userMarkerOption);
                                CircleOptions circle2 = new CircleOptions();
                                circle2.center(new LatLng(latLng.latitude,latLng.longitude));
                                circle2.radius(750);
                                circle2.fillColor(Color.TRANSPARENT);
                                circle2.strokeWidth(6);
                                mMap.addCircle(circle2);
                                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                                        textView.setText("Alan(Metre):" +progress);
                                        deger=progress;


                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        mMap.clear();
                                        MarkerOptions newMarker=new MarkerOptions();
                                        newMarker.position(latLng);
                                        newMarker.title(address);
                                        newMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                                        mMap.addMarker(newMarker);
                                        CircleOptions circle1 = new CircleOptions();
                                        circle1.center(new LatLng(latLng.latitude, latLng.longitude));
                                        if(deger==0) {
                                            circle1.radius(750);
                                        }
                                        else {
                                            circle1.radius(deger);
                                        }
                                        circle1.fillColor(Color.TRANSPARENT);
                                        circle1.strokeWidth(6);
                                        mMap.addCircle(circle1);
                                    }
                                });




                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15),1000,null);
                                btnKaydet.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        double latitute1 = latLng.latitude;
                                        double longtitue1 = latLng.longitude;

                                        yeniNot.setTittle(tittle.getText().toString());
                                        yeniNot.setNote(edtText.getText().toString());
                                        yeniNot.setLat(String.valueOf(latitute1));
                                        yeniNot.setLng(String.valueOf(longtitue1));
                                        Date currentTime = Calendar.getInstance().getTime();
                                        yeniNot.setClock(currentTime.toString());

                                        if(deger==0)
                                            yeniNot.setRadius(String.valueOf(750));
                                        else
                                            yeniNot.setRadius(String.valueOf(deger));
                                        if (!edtText.getText().toString().isEmpty() && !tittle.getText().toString().isEmpty() ) {
                                            String key = rRef.push().getKey();
                                            rRef.child(key).setValue(yeniNot).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(MapsActivity.this, "Notunuz başarıyla kaydedildi", Toast.LENGTH_SHORT).show();

                                                        Intent it1 = new Intent(MapsActivity.this,KonumActivity.class);

                                                        startActivity(it1);
                                                    }
                                                }
                                            });


                                        }
                                        else
                                            Toast.makeText(MapsActivity.this,"Not ekleyebilmek için  not başlığını ve notunuzun olması gerekmektedir.",Toast.LENGTH_LONG).show();


                                    }
                                });
                            }
                        } else {
                            Toast.makeText(this, "Aradıgınız adres bulunumadı", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "lüften konum girerek arama yapınız", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    Double defaultLatMap = 39.7112228, defaultLongMap = 36.339064;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng defaultLocation = new LatLng(defaultLatMap, defaultLongMap);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 4.5f));
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);

        }
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(final LatLng point) {

                mMap.clear();
                MarkerOptions marker = new MarkerOptions().position(
                        new LatLng(point.latitude, point.longitude)).title("Tıkladıgınız Konum");

                marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                final LatLng latLng1 = new LatLng(point.latitude, point.longitude);
                mMap.addMarker(marker);
                CircleOptions circle6 = new CircleOptions();
                circle6.center(new LatLng(latLng1.latitude, latLng1.longitude));
                circle6.radius(750);
                circle6.fillColor(Color.TRANSPARENT);
                circle6.strokeWidth(6);
                mMap.addCircle(circle6);

                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        textView.setText("Alan(Metre):" + progress);
                        deger = progress;
                        Toast.makeText(MapsActivity.this,"deger : "+deger,Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mMap.clear();

                        MarkerOptions newMarker = new MarkerOptions();
                        newMarker.position(point);
                        newMarker.title("Tıklanınan konum");
                        newMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

                        mMap.addMarker(newMarker);
                        CircleOptions circle1 = new CircleOptions();
                        circle1.center(new LatLng(point.latitude, point.longitude));


                        if(deger==0) {
                            circle1.radius(750);
                        }
                        else {
                            circle1.radius(deger);
                        }

                        circle1.fillColor(Color.TRANSPARENT);
                        circle1.strokeWidth(6);
                        mMap.addCircle(circle1);
                    }
                });
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 15), 1000, null);

                btnKaydet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        double latitute11 = point.latitude;
                        double longtitue11 = point.longitude;

                        yeniNot.setTittle(tittle.getText().toString());
                        yeniNot.setNote(edtText.getText().toString());
                        yeniNot.setLat(String.valueOf(latitute11));
                        yeniNot.setLng(String.valueOf(longtitue11));

                        Date currentTime = Calendar.getInstance().getTime();
                        yeniNot.setClock(currentTime.toString());
                        Date tarih = new Date();
                        SimpleDateFormat bugun = new SimpleDateFormat("dd/MM/yyyy");

                        if(deger==0)
                            yeniNot.setRadius(String.valueOf(750));
                        else
                            yeniNot.setRadius(String.valueOf(deger));


                        if (!edtText.getText().toString().isEmpty() && !tittle.getText().toString().isEmpty() ) {
                            String key = rRef.push().getKey();
                            rRef.child(key).setValue(yeniNot).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MapsActivity.this, "Notunuz başarıyla kaydedildi", Toast.LENGTH_SHORT).show();

                                        Intent it = new Intent(MapsActivity.this,KonumActivity.class);

                                        startActivity(it);
                                    }
                                }
                            });


                        }
                        else
                            Toast.makeText(MapsActivity.this,"Not ekleyebilmek için  not başlığını ve notunuzun olması gerekmektedir.",Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
        mMap.setMyLocationEnabled(true);
    }

    public boolean checkKisiKonunİzin() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_Kullanıcı_Konum_Code);

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_Kullanıcı_Konum_Code);
            }
            return false;
        } else {
            return true;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Request_Kullanıcı_Konum_Code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "İzin alınamadı..", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastlocation = location;
        if (anlıkNokta != null) {
            anlıkNokta.remove();

        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(" kullanıcının anlık lokasyonu");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15),1000,null);
        anlıkNokta = mMap.addMarker(markerOptions);
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    private void stopLocationUpdates() {
        // Pil tüketimi için konum almayı kesmemiz gerek

    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
