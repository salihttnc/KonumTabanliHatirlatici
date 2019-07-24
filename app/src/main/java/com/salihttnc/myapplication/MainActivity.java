package com.salihttnc.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.firebase.geofire.GeoFire;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,  SharedPreferences.OnSharedPreferenceChangeListener {
    FirebaseAuth mAuth;
    private GeoFire geoHelper;
    Button button, button3;
    private static final int REQUEST_LOCATION_PERMISSION_CODE = 101;
    Toolbar toolbar;
    TextView txtViewEkle, txtViewListe, txtViewGeo;
    private String deger1;
    private int sayac;
    ImageView imgAdd, imgList, imgGeo, imgHava;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleApiClient mGoogleApiClient;
    private LocationCallback locationCallback;
    private static final String TAG = "resPMain";
    PendingIntent pendingIntent1, pendingIntent2, pendingIntent;
    private AlarmManager alarmManager, alarmManager1;
    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    Date currentTime;
    Calendar calender;
    ArrayList<Not> mNotes;
    Runnable r;
    private String saatcik,dakikacik;
    private String userId;
    Not not, not1;
    private Long hourr;
    private int counter=0;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private int mHour2, mMinute2;
    private  String  saat,dakika;
    // The Broadcast
    //Receiver used to listen from broadcasts from the service.
    private AlarmManager alarmMgr;
    private MyReceiver myReceiver;
    SharedPreferences prefs;
    // A reference to the service used to get location updates.
    private LocationUpdatesService mService = null;

    // Tracks the bound state of the service.
    private boolean mBound = false;
    private DatabaseReference rRef;
    Handler h2;
    // UI elements.
    private Button mRequestLocationUpdatesButton;
    private Button mRemoveLocationUpdatesButton;

    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myReceiver = new MyReceiver();
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        button3 = findViewById(R.id.button3);
        mNotes = new ArrayList<>();
        txtViewEkle = findViewById(R.id.txtViewEkle);
        txtViewListe = findViewById(R.id.txtViewListe);
        txtViewGeo = findViewById(R.id.txtViewGeo);
        imgAdd = findViewById(R.id.imgAdd);
        imgGeo = findViewById(R.id.imgGeo);

        imgList = findViewById(R.id.imgList);
        imgHava = findViewById(R.id.imgHava);
        if (Utils.requestingLocationUpdates(this)) {
            if (!checkPermissions()) {
                requestPermissions();
            }
        }









        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION_CODE);
        }




        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.getInstance().signOut();
                Intent login = new Intent(MainActivity.this, GirisActivity.class);
                startActivity(login);
                finish();
                mService.removeLocationUpdates();


            }
        });
        txtViewListe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent list = new Intent(MainActivity.this, NotListeleme.class);
                startActivity(list);
            }
        });
        txtViewEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(add);
            }
        });
        txtViewGeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent KonumActivity = new Intent(MainActivity.this, KonumActivity.class);
                startActivity(KonumActivity);
            }
        });

        imgHava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add1 = new Intent(MainActivity.this, HavaDurumu1.class);
                startActivity(add1);
            }
        });
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add2 = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(add2);

            }
        });
        imgList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent list1 = new Intent(MainActivity.this, NotListeleme.class);
                startActivity(list1);
            }
        });
        imgGeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent geo = new Intent(MainActivity.this, KonumActivity.class);
                startActivity(geo);
            }
        });

        final Calendar c1 = Calendar.getInstance();
        mHour = c1.get(Calendar.HOUR_OF_DAY);
        mMinute = c1.get(Calendar.MINUTE);
        Log.d("saat","degerler : "+mHour+mMinute);



    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }
public void alarmKur(String clck,String mnt){
    Log.d("firebase saat","deger:"+clck+mnt);

    final Calendar calendar = Calendar.getInstance();
    mHour2= calendar.get(Calendar.HOUR_OF_DAY);
    mMinute2= calendar.get(Calendar.MINUTE);
    int i=Integer.parseInt(clck);
    int j=Integer.parseInt(mnt);
    Log.d("parse","deger:"+i+j);




              notification("Bugün ki notlarınızı görmek için tıklayın.");



    Log.d("device  saat","deger:"+mHour2+mMinute2);

}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = (MenuItem) menu.findItem(R.id.switchId);
        item.setActionView(R.layout.menu_switch);
        final Switch switchAB = item
                .getActionView().findViewById(R.id.switchAB);
        switchAB.setChecked(false);

        switchAB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    if (!checkPermissions()) {
                        requestPermissions();
                    } else {

                        mService.requestLocationUpdates();
                    }
                } else {
                    mService.removeLocationUpdates();
                }
            }
        });
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        rRef = FirebaseDatabase.getInstance().getReference().child("Notes").child(userId).child("Clock");
        FirebaseUser currentUser = mAuth.getInstance().getCurrentUser();
        switch (item.getItemId()) {
            case R.id.logout:
                mAuth.getInstance().signOut();
                Intent login = new Intent(MainActivity.this, GirisActivity.class);
                startActivity(login);
                finish();
                break;
            case R.id.clock:
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                rRef.child("saat").setValue(hourOfDay);
                                rRef.child("dakika").setValue(minute);


                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();




        }
        return super.onOptionsItemSelected(item);
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        toast("Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        toast("Failed");
    }

    private void toast(String message) {
        try {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {

        }
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10.0f);
    }

    private void buildLocationCallBack() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
//                Common.current_location = locationResult.getLastLocation();


                Log.d("Location", locationResult.getLastLocation().getLatitude() + "/" + locationResult.getLastLocation().getLongitude());

            }
        };

    }
    @Override
    protected void onStart() {
        FirebaseUser currentUser = mAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Intent login = new Intent(getApplicationContext(), GirisActivity.class);
            startActivity(login);
            finish();
        }
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);



        // Restore the state of the buttons when the activity (re)launches.
        setButtonsState(Utils.requestingLocationUpdates(this));

        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        bindService(new Intent(this, LocationUpdatesService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        rRef = FirebaseDatabase.getInstance().getReference().child("Notes").child(userId).child("Clock");


        h2=new Handler();
        rRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                saatcik = dataSnapshot.child("saat").getValue().toString();
                dakikacik= dataSnapshot.child("dakika").getValue().toString();
                Log.d("data change","askdhfa");
                 sayac=0;
                Log.d("sayac degisimi","askdhfa");

                Log.d("saatcik"," "+saatcik+dakikacik);
                 r = new Runnable() {
                    public void run() {
                        final Calendar calendar = Calendar.getInstance();
                        mHour2= calendar.get(Calendar.HOUR_OF_DAY);
                        mMinute2= calendar.get(Calendar.MINUTE);
                        final int i=Integer.parseInt(saatcik);
                        final int j=Integer.parseInt(dakikacik);
                        if(mHour2==i && mMinute2==j && sayac==0) {
                            sayac++;

                            alarmKur(saatcik,dakikacik);
                        }

                       h2.postDelayed(this, 10000);
                    }
                };

               h2.postDelayed(r, 10000);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
        h2.postDelayed(r,10000);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        h2.removeCallbacks(r);
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection);
            mBound = false;
        }
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    /**
     * Returns the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        return  PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.activity_main),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                mService.requestLocationUpdates();
            } else {
                // Permission denied.
                setButtonsState(false);
                Snackbar.make(
                        findViewById(R.id.activity_main),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }

    /**
     * Receiver for broadcasts sent by {@link LocationUpdatesService}.
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            if (location != null) {
                Toast.makeText(MainActivity.this, Utils.getLocationText(location),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        // Update the buttons state depending on whether location updates are being requested.
        if (s.equals(Utils.KEY_REQUESTING_LOCATION_UPDATES)) {
            setButtonsState(sharedPreferences.getBoolean(Utils.KEY_REQUESTING_LOCATION_UPDATES,
                    false));
        }
    }

    private void setButtonsState(boolean requestingLocationUpdates) {
        if (requestingLocationUpdates) {

        } else {

        }
    }
    public void notification(String bildirimText) {
        NotificationCompat.Builder builder;

        NotificationManager bildirimYoneticisi =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;

        Intent ıntent = new Intent(this,NotListeleme1.class);

        PendingIntent gidilecekIntent = PendingIntent.getActivity(this,1,ıntent,PendingIntent.FLAG_UPDATE_CURRENT);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Oreo sürümü için bu kod çalışacak.

            String kanalId = "kanalId2";
            String kanalAd = "kanalAd2";
            String kanalTanım = "kanalTanım2";
            int kanalOnceligi = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel kanal = bildirimYoneticisi.getNotificationChannel(kanalId);

            if (kanal == null) {
                kanal = new NotificationChannel(kanalId, kanalAd, kanalOnceligi);
                kanal.setDescription(kanalTanım);
                bildirimYoneticisi.createNotificationChannel(kanal);
            }



            builder = new NotificationCompat.Builder(this, kanalId);

            builder.setContentTitle("KONUM HATIRLATICI")  // gerekli
                    .setContentText(bildirimText)  // gerekli
                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark) // gerekli
                    .setAutoCancel(true)  // Bildirim tıklandıktan sonra kaybolur."
                    .setContentIntent(gidilecekIntent);


        } else { // OREO Sürümü haricinde bu kod çalışacak.

            builder = new NotificationCompat.Builder(this);

            builder.setContentTitle("KONUM HATIRLATICI")  // gerekli
                    .setContentText(bildirimText)  // gerekli
                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark) // gerekli
                    .setContentIntent(gidilecekIntent)
                    .setOngoing(true)
                    .setAutoCancel(true)  // Bildirim tıklandıktan sonra kaybolur."
                    .setPriority(Notification.PRIORITY_DEFAULT);

        }

        bildirimYoneticisi.notify(m,builder.build());
    }
}
