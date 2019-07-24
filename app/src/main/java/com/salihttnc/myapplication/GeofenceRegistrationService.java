package com.salihttnc.myapplication;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Random;

public class GeofenceRegistrationService extends IntentService {

    private static final String TAG = "KonumDegisti3";

    public GeofenceRegistrationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        DatabaseReference rRef,rRef1;
        rRef = FirebaseDatabase.getInstance().getReference().child("Geofencing");

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.d(TAG, "GeofencingEvent error " + geofencingEvent.getErrorCode());
        } else {
            int transaction = geofencingEvent.getGeofenceTransition();
            List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();

            for(int i=0;i<geofences.size();i++) {
                Geofence geofence = geofences.get(i);
                if (transaction == Geofence.GEOFENCE_TRANSITION_ENTER) {
                    Log.d("geofenceidgeldi", geofence.getRequestId());
                    rRef.setValue("geofence geldi");
                    notification(geofence.getRequestId());
                    Log.d(TAG, "Evdesin");
                } else {
                    notification("Evde degilsin");
                    Log.d(TAG, "Evde degilsin");
                }
            }
        }
    }

    public void notification(String bildirimText) {
        NotificationCompat.Builder builder;

        NotificationManager bildirimYoneticisi =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;

        Intent ıntent = new Intent(this,MainActivity.class);

        PendingIntent gidilecekIntent = PendingIntent.getActivity(this,1,ıntent,PendingIntent.FLAG_UPDATE_CURRENT);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Oreo sürümü için bu kod çalışacak.

            String kanalId = "kanalId";
            String kanalAd = "kanalAd";
            String kanalTanım = "kanalTanım";
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
                    .setAutoCancel(true)  // Bildirim tıklandıktan sonra kaybolur."
                    .setPriority(Notification.PRIORITY_DEFAULT);

        }

        bildirimYoneticisi.notify(m,builder.build());
    }
}
