package CommonLib;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import Controller.ControlThread;

/**
 * Created by My PC on 05/01/2016.
 */
public class PhoneState {
    private static PhoneState instance = null;
    private PhoneState() {
    }
    public synchronized static PhoneState inst(){
        if (instance == null) {
            instance = new PhoneState();
            Log.d("PhoneState", "Create new instance");
        }
        return instance;
    }

    private TelephonyManager telephonyManager = null;
    private Intent batteryStatus = null;
    private WifiManager wifiManager = null;
    private ConnectivityManager connectivityManager = null;
    private LocationManager locationManager = null;
    private ContentResolver contentResolver = null;

    public void init(Context context) {
        telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = context.registerReceiver(null, ifilter);
        wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        contentResolver = context.getContentResolver();
    }

    public TrackingItem.CellInfo getCellInfo() {
        try {
            TrackingItem.CellInfo cellInfo = new TrackingItem.CellInfo();
            GsmCellLocation cellLocation = (GsmCellLocation) telephonyManager.getCellLocation();
            if (cellLocation != null) {
                cellInfo.cellID = cellLocation.getCid() & 0xffff;
                cellInfo.LAC = cellLocation.getLac() & 0xffff;
            }
            String networkOperator = telephonyManager.getNetworkOperator();
            if (networkOperator != null) {
                cellInfo.MCC = Integer.parseInt(networkOperator.substring(0, 3));
                cellInfo.MNC = Integer.parseInt(networkOperator.substring(3));
            }
            return cellInfo;
        }
        catch (Exception ex) {
            return null;
        }
    }

    public int getBatteryLevel() {
        try {
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            if (scale <= 0) return -1;
            return level * 100 / scale;
        }
        catch (Exception ex) {
            return -1;
        }
    }

    public byte isWifi() {
        try {
            NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            boolean wifiConnected = wifiInfo.getState() == NetworkInfo.State.CONNECTED;
            if (wifiConnected) return 1;
            return 0;
        }
        catch (Exception ex) {
            return -1;
        }
    }

    public byte is3G() {
        try {
            NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            boolean dataConnected = mobileInfo.getState() == NetworkInfo.State.CONNECTED;
            if (dataConnected) return 1;
            return 0;
        }
        catch (Exception ex) {
            return -1;
        }
    }
    public boolean turn3GOnOff(boolean turnOn) {
        Log.i("turning 3G",turnOn ? "on..." : "off...");
        try {
            final Class conmanClass = Class.forName(connectivityManager.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(connectivityManager);
            final Class iConnectivityManagerClass = Class.forName(
                    iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass
                    .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);

            setMobileDataEnabledMethod.invoke(iConnectivityManager, turnOn);
        }
        catch (Exception ex) {
            return false;
        }
        return true;
    }

    public byte isGPS() {
        try {
            boolean isON = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isON) return 1;
            return 0;
        }
        catch (Exception ex) {
            return -1;
        }
    }
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public byte isAirplaneMode() {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                boolean isON = Settings.System.getInt(contentResolver, Settings.System.AIRPLANE_MODE_ON, 0) != 0;
                if (isON) return 1;
            } else {
                boolean isON = Settings.Global.getInt(contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
                if (isON) return 1;
            }
            return 0;
        }
        catch (Exception ex) {
            return -1;
        }
    }
}
