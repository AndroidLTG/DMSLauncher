package com.vietdms.mobile.dmslauncher.Receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.R;
import com.vietdms.mobile.dmslauncher.Service.BackgroundService;

import CommonLib.Const;
import CommonLib.EventPool;
import CommonLib.EventType;
import CommonLib.WakeLock;

/**
 * Created by DMSv4 on 12/22/2015.
 */
public class LocationAlarmManager extends BroadcastReceiver {
    private static final String TAG = "DMS Tracking";
    private static final String GETBY = "Get by";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    private static final String NOW = "Location Now";
    private Location mLastLocation;
    private static final String INITIALIZE = "Tracking creating..";

    @Override
    public void onReceive(Context context, Intent intent) {
        WakeLock.inst().acquire();
        EventPool.control().enQueue(new EventType.EventBase(EventType.Type.SendTracking));
        //Send location
//        if (mLocationManager == null) {
//            mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        }
//        try {
//            mLocationManager.requestLocationUpdates(
//                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
//                    mLocationListeners[1]);
//        } catch (java.lang.SecurityException ex) {
//        } catch (IllegalArgumentException ex) {
//        }
//        try {
//            mLocationManager.requestLocationUpdates(
//                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
//                    mLocationListeners[0]);
//
//        } catch (java.lang.SecurityException ex) {
//        } catch (IllegalArgumentException ex) {
//        }
    }

//    private class LocationListener implements android.location.LocationListener {
//
//
//        public LocationListener(String provider) {
//            Log.w(TAG, GETBY + provider);
//            mLastLocation = new Location(provider);
//        }
//
//        @Override
//        public void onLocationChanged(Location location) {
//            Log.w(TAG, NOW + location);
//
//            mLastLocation.set(location);
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//        }
//
//        @Override
//        public void onProviderEnabled(String provider) {
//        }
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//        }
//    }
//
//    LocationListener[] mLocationListeners = new LocationListener[]{
//            new LocationListener(LocationManager.GPS_PROVIDER),
//            new LocationListener(LocationManager.NETWORK_PROVIDER)
//    };

    public void SetAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, LocationAlarmManager.class);
        intent.putExtra(context.getString(R.string.setAlarm), Boolean.FALSE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), Const.LocationUpdateIntervalInSeconds * 1000, pi);
    }

    public void CancelAlarm(Context context) {
        Intent intent = new Intent(context, LocationAlarmManager.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);

        //location
//        if (mLocationManager != null) {
//            for (int i = 0; i < mLocationListeners.length; i++) {
//                try {
//                    mLocationManager.removeUpdates(mLocationListeners[i]);
//                } catch (Exception ex) {
//                }
//            }
//        }
    }

}