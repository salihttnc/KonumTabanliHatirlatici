package com.salihttnc.myapplication;

import android.app.DatePickerDialog;

import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EditMapsActivity extends FragmentActivity implements
        DatePickerDialog.OnDateSetListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private EditText edtText, tittle;
    private TextView textView,textView1;
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
    private SeekBar seekBar;
    private String tarihhhh;

    double deger=0;
    double deger2;
    private DatabaseReference mDatabaseRef;
    private Not not;
    private double editLng,editLat,editRadius;
    private String key_global;
    String guncelBaslik,guncelNot;
    private String datee;
    private ImageView button;
    int radius;
    private String currentDateString;
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
        textView1 = (TextView) findViewById(R.id.textView);
        seekBar.setMax(10000);
        seekBar.setMin(100);

        mDatabaseRef=FirebaseDatabase.getInstance().getReference().child("Notes").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Notlar");


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
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        SimpleDateFormat bugun = new SimpleDateFormat("dd/MM/yyyy");
      currentDateString = bugun.format(c.getTime());
      if(!currentDateString.equals(datee))
          textView1.setText(currentDateString);

        Date tarih = new Date();


    }
    private void setInfo()
    {

        key_global=getIntent().getStringExtra("key");
        Toast.makeText(this, ""+key_global, Toast.LENGTH_SHORT).show();
        Log.d("tag", "setInfo: PersonEdit" );
        mDatabaseRef.child(key_global).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                not =new Not();
                not=dataSnapshot.getValue(Not.class);
                tittle.setText(not.getTittle());
                edtText.setText(not.getNote());
                if(not.getDate()!=null) {
                    textView1.setText(not.getDate());
                }
                else {
                    textView1.setTextSize(15);
                    textView1.setText("Tarih Belirtilmedi");
                }
                datee=not.getDate();
                Log.d("deger ","date "+datee);
                editRadius=(Double.parseDouble(not.getRadius()));
                editLat=(Double.parseDouble(not.getLat()));
                editLng=Double.parseDouble(not.getLng());
                guncelBaslik=not.getTittle();
                guncelNot=not.getNote();




                Log.d("ABCD","DEGERLER "+guncelBaslik+guncelNot);


                final LatLng latLng1 = new LatLng(editLat,editLng);
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
                textView.setText("Alan(Metre):" +editRadius);

                Log.d("123", "DEGERLER " + guncelBaslik+tittle.getText().toString());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng1,15),500,null);
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
                        final LatLng latLng1 = new LatLng(editLat,editLng);
                        final MarkerOptions userMarkerOption1 = new MarkerOptions();
                        userMarkerOption1.position(latLng1);
                        userMarkerOption1.title(not.getTittle());
                        userMarkerOption1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                        mMap.addMarker(userMarkerOption1);
                        CircleOptions circle1 = new CircleOptions();
                        circle1.center(new LatLng(editLat, editLng));
                        circle1.radius(deger);
                        circle1.fillColor(Color.TRANSPARENT);
                        circle1.strokeWidth(6);
                        mMap.addCircle(circle1);

                    }
                });
                btnKaydet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        yeniNot.setTittle(tittle.getText().toString());
                        yeniNot.setNote(edtText.getText().toString());
                        yeniNot.setLat(String.valueOf(editLat));
                        yeniNot.setLng(String.valueOf(editLng));
                        yeniNot.setClock("sasa");

                        if(editRadius!=deger && deger!=0) {
                            yeniNot.setRadius(String.valueOf(deger));
                        }
                        else
                        {
                            yeniNot.setRadius(String.valueOf(editRadius));
                        }
                        if(currentDateString!=null) {
                            if (!currentDateString.equals(datee)) {
                                textView1.setText(currentDateString);
                                yeniNot.setDate(currentDateString);

                            }
                        }
                        else {
                            yeniNot.setDate(datee);
                        }
                        if(!edtText.getText().toString().equals(guncelNot) || !tittle.getText().toString().equals(guncelBaslik) ) {
                            rRef.child(key_global).setValue(yeniNot).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(EditMapsActivity.this, "Notunuz başarıyla kaydedildi", Toast.LENGTH_SHORT).show();
                                        Intent it = new Intent(EditMapsActivity.this, MainActivity.class);
                                        startActivity(it);
                                    }

                                }
                            });
                        }
                        else
                            Toast.makeText(EditMapsActivity.this,"Güncelleme yapmak için not başlığını veya notunuzu değiştirmeniz gerekmektedir.",Toast.LENGTH_LONG).show();

                    }


                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });




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
                                            circle1.radius(100);
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
                                        Date tarih = new Date();
                                        SimpleDateFormat bugun = new SimpleDateFormat("yyyy/MM/dd");

                                        if(editRadius!=deger && deger!=0) {
                                            yeniNot.setRadius(String.valueOf(deger));
                                        }
                                        else
                                        {
                                            yeniNot.setRadius(String.valueOf(editRadius));
                                        }
                                        if(!currentDateString.equals(datee)) {
                                            textView1.setText(currentDateString);
                                            yeniNot.setDate(currentDateString);

                                        }
                                        else {
                                            yeniNot.setDate(datee);
                                        }



                                        rRef.child(key_global).setValue(yeniNot).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(EditMapsActivity.this, "Notunuz başarıyla kaydedildi", Toast.LENGTH_SHORT).show();
                                                    Intent it = new Intent(EditMapsActivity.this, KonumActivity.class);
                                                    startActivity(it);
                                                }

                                            }
                                        });


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
        setInfo();
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

                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        textView.setText("Alan:" +progress);
                        deger=progress;

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mMap.clear();
                        MarkerOptions newMarker=new MarkerOptions();
                        newMarker.position(point);
                        newMarker.title("Tıklanınan konum");
                        newMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                        mMap.addMarker(newMarker);
                        CircleOptions circle1 = new CircleOptions();
                        circle1.center(new LatLng(point.latitude, point.longitude));
                        if(deger==0) {
                            circle1.radius(100);
                        }
                        else {
                            circle1.radius(deger);
                        }
                        circle1.fillColor(Color.TRANSPARENT);
                        circle1.strokeWidth(6);
                        mMap.addCircle(circle1);
                    }
                });
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point,15),1000,null);
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
                        SimpleDateFormat bugun = new SimpleDateFormat("yyyy/MM/dd");

                        yeniNot.setRadius(String.valueOf(deger));
                        if(currentDateString!=null) {
                            if (!currentDateString.equals(datee)) {
                                textView1.setText(currentDateString);
                                yeniNot.setDate(currentDateString);

                            }
                        }
                        else {
                            yeniNot.setDate(datee);
                        }

                        mDatabaseRef.child(key_global).setValue(yeniNot).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(EditMapsActivity.this, "Notunuz başarıyla kaydedildi", Toast.LENGTH_SHORT).show();
                                    Intent it = new Intent(EditMapsActivity.this, KonumActivity.class);
                                    startActivity(it);
                                }


                            }
                        });


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
        anlıkNokta = mMap.addMarker(markerOptions);
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    private void stopLocationUpdates() {
        // Pil tüketimi için konum almayı kesmemiz gerek
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        googleApiClient.disconnect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }
}

