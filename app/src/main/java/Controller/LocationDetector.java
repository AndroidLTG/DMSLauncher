package Controller;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Date;

import CommonLib.Const;
import CommonLib.Model;

/**
 * Created by My PC on 05/12/2015.
 */
class LocationDetector implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static LocationDetector instance = null;
    private LocationDetector() {super(); }
    public synchronized static LocationDetector inst(){
        if (instance == null) {
            instance = new LocationDetector();
            Log.d("LocationDetector", "Create new instance");
        }
        return instance;
    }

    private GoogleApiClient mGoogleApiClient = null;
    public void start(Context context) {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }
    public void stop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    public boolean setHighAccuracy() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        catch (SecurityException ex) {
            Log.e("GoogleApiClient", "setHighAccuracy access denied");
            return false;
        }
        catch (Exception ex) {
            Log.e("GoogleApiClient", "setHighAccuracy error " + ex.toString());
            return false;
        }
        return true;
    }
    public boolean setNormalAccuracy() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Const.LocationUpdateIntervalInSeconds * 1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        catch (SecurityException ex) {
            Log.e("GoogleApiClient", "setNormalAccuracy access denied");
            return false;
        }
        catch (Exception ex) {
            Log.e("GoogleApiClient", "setNormalAccuracy error " + ex.toString());
            return false;
        }
        return true;
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i("GoogleApiClient", "Connected to Google Play services!");
        try {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                Model.inst().setLastLocation(mLastLocation);
                showUpdateLocation();
            }
            else {
                Log.w("GoogleApiClient", "cannot get current location");
            }
            setNormalAccuracy();
        }
        catch (SecurityException ex) {
            Log.e("GoogleApiClient", "access denied");
        }
        catch (Exception ex) {
            Log.e("GoogleApiClient", "error " + ex.toString());
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.w("GoogleApiClient", "The connection has been interrupted. Cause:" + cause);
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.w("GoogleApiClient", "onConnectionFailed:" + result.getErrorMessage());
        // This callback is important for handling errors that
        // may occur while attempting to connect with Google.
        //
        // More about this in the 'Handle Connection Failures' section.
    }

    @Override
    public void onLocationChanged(Location location) {
        Model.inst().setLastLocation(location);
        showUpdateLocation();
    }

    private void showUpdateLocation() {
        Location mLastLocation = Model.inst().getLastLocation();
        String lat = String.valueOf(mLastLocation.getLatitude());
        String lon = String.valueOf(mLastLocation.getLongitude());
        String accuracy = String.valueOf(mLastLocation.getAccuracy());
        String speed = String.valueOf(mLastLocation.getSpeed());
        String time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(new Date(mLastLocation.getTime()));
        Log.i("GoogleApiClient", "location:" + lat + "," + lon + " accuracy:" + accuracy + " speed:" + speed + " time:" + time);
    }
}
