package com.salihttnc.myapplication.Common;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {
    public static final String APP_ID="a017567a008d3137d542b74dfc7084aa";
    public static LatLng current_location=null;
    public static String convertUnixToDate(long  dt){

        Date date=new Date(dt*1000L);
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm  EEE MM yy");
        String formatted=sdf.format(date);
        return formatted;
    }
    public static String convertUnixToHour(long dt){

        Date date=new Date(dt*1000L);
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
        String formatted=sdf.format(date);
        return formatted;

    }

}
