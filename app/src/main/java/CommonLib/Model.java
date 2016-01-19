package CommonLib;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.location.Location;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by My PC on 26/11/2015.
 */
public class Model {
    private static Model instance = null;
    private Model() { }
    public synchronized static Model inst(){
        if (instance == null) {
            instance = new Model();
            Log.d("Model", "Create new instance");
        }
        return instance;
    }

    private String deviceId = null;
    public synchronized String getDeviceId() {
        return deviceId;
    }
    public synchronized String getDeviceId(Context context) {
        if (deviceId == null) {
            deviceId = "";
//            try {
//                Class<?> c = Class.forName("android.os.SystemProperties");
//                Method get = c.getMethod("get", String.class, String.class );
//                deviceId += (String)(get.invoke(c, "ro.serialno", "unknown" ) );
//            }
//            catch (Exception ignored) {
//            }
            try {
                deviceId += Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);
            }
            catch (Exception ignored) {
            }
            Log.i("getDeviceId", deviceId);
        }
        return deviceId;
    }

    private Location lastLocation = null;
    public synchronized Location getLastLocation() {
        return lastLocation;
    }
    public synchronized void setLastLocation(Location location) {
        lastLocation = location;
    }

    private int lastAckLocationID = -1;
    public synchronized int getLastAckLocationID() { return lastAckLocationID; }
    public synchronized void setLastAckLocationID(int lastId) { lastAckLocationID = lastId; }

    private long serverTime = -1;
    private long serverTimeClientTick = -1;
    public void setServerTime(long serverTime) {
        this.serverTime = serverTime;
        serverTimeClientTick = SystemClock.elapsedRealtime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // the format of your date
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+7")); // give a timezone reference for formating
        Log.i("setServerTime", sdf.format(new Date(serverTime)));
    }
    public long getServerTime() {
        if (serverTime >= 0 && serverTimeClientTick >= 0) {
            return serverTime + SystemClock.elapsedRealtime() - serverTimeClientTick;
        }
        return System.currentTimeMillis();
    }
}
