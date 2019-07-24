package com.salihttnc.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.core.app.ActivityCompat;

/**
 * Created by Opriday on 11/15/2018.
 */

public class Permissions {

  public static Permissions permissions;
  static Context context;
  public static int LOCATION_PERMISSION_CODE=101;

  public static Permissions getInstance(Context ctx){
    context = ctx;
    if (permissions == null){
      permissions = new Permissions();
      return permissions;
    }
    return permissions;
  }
  public boolean checkInternetConnection(){
    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo info = connectivityManager.getActiveNetworkInfo();
    if (info != null && info.isConnected()){
      return true;
    }else {
      return false;
    }
  }

  public boolean checkLocationPermission(){
    if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
      return true;
    }else {
      requestLocationPermission();
      return false;
    }
  }

  public void requestLocationPermission(){
    ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_CODE);
  }



}