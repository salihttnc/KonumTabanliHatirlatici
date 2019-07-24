package com.salihttnc.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Random;

public class AlarmReceiver1 extends BroadcastReceiver {

    private NotificationManager notifManager;

    private String userId;
    private DatabaseReference rRef;
    private String deger1;
     private int NOTIFY_ID = 0;
    @Override
    public void onReceive(Context context, Intent intent) {
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        rRef = FirebaseDatabase.getInstance().getReference().child("Notes").child(userId);
        deger1=rRef.child("saat").getKey();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE, 33);
        calendar.set(Calendar.SECOND, 15);
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR_OF_DAY, 14);
        calendar1.set(Calendar.MINUTE, 33);
        calendar1.set(Calendar.SECOND, 15);
        Calendar rightNow = Calendar.getInstance();


            createNotification("Bugün ki notlarınızı görmek için tıklayın.",context);


        }


    public void createNotification(String aMessage, Context context) {
        // ID of notification
        Random random = new Random();
       NOTIFY_ID = random.nextInt(9999 - 1000) + 1000;
        String id = "one"; // default_channel_id
        String title = "Match Update"; // Default Channel
        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;
        if (notifManager == null) {
            notifManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, title, importance);
                mChannel.enableVibration(true);
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, NotListeleme1.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle(aMessage)                            // required message
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                    .setContentText(context.getString(R.string.app_name)) // required this is mainly title
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage);

        }
        else {
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, NotListeleme1.class); //which clas it should go
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle(aMessage)                            // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                    .setContentText(context.getString(R.string.app_name)) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setPriority(Notification.PRIORITY_DEFAULT);
        }
        Notification notification = builder.build();
        notifManager.notify(NOTIFY_ID, notification);
    }


}





