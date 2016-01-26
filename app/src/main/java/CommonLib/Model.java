package CommonLib;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
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
    private Location lastValidLocation = null;
    public synchronized Location getLastLocation() {
        return lastLocation;
    }
    public synchronized Location getLastValidLocation() {
        return lastValidLocation;
    }
    public synchronized void setLastLocation(Location location) {
        lastLocation = location;
        if (lastLocation != null) {
            if (lastValidLocation != null) {
                float distanceMeter = lastLocation.distanceTo(lastValidLocation);
                int milisecElapsed = (int)(lastLocation.getTime() - lastValidLocation.getTime());
                if (milisecElapsed > 0) {
                    float accuracy = (lastLocation.getAccuracy() + lastValidLocation.getAccuracy()) / 2;
                    float speed = (distanceMeter - accuracy) * 1000 / milisecElapsed;
                    if (speed > Const.DroppedSpeedMPS) {
                        lastLocation = null;
                        return;
                    }
                    if (speed > Const.BoostedSpeedMPS) {
                        int interval = getAlarmIntervalBoosted();
                        LocationDetector.inst().setInterval(interval);
                        AlarmTimer.inst().setAlarmInterval(interval);
                    } else {
                        int interval = getAlarmIntervalNormal();
                        LocationDetector.inst().setInterval(interval);
                        AlarmTimer.inst().setAlarmInterval(interval);
                    }
                    Bundle extras = new Bundle();
                    extras.putFloat("distanceMeter", distanceMeter);
                    extras.putInt("milisecElapsed", milisecElapsed);
                    lastLocation.setExtras(extras);
                }
            }
            lastValidLocation = lastLocation;
        }
    }

    private int lastAckLocationID = -1;
    public synchronized int getLastAckLocationID() { return lastAckLocationID; }
    public synchronized void setLastAckLocationID(int lastId) { lastAckLocationID = lastId; }

    private long serverTime = -1;
    private long serverTimeClientTick = -1;
    public synchronized void setServerTime(long serverTime) {
        this.serverTime = serverTime;
        serverTimeClientTick = SystemClock.elapsedRealtime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // the format of your date
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+7")); // give a timezone reference for formating
        Log.i("setServerTime", sdf.format(new Date(serverTime)));
    }
    public synchronized long getServerTime() {
        if (serverTime >= 0 && serverTimeClientTick >= 0) {
            return serverTime + SystemClock.elapsedRealtime() - serverTimeClientTick;
        }
        return System.currentTimeMillis();
    }

    private int alarmIntervalNormal = Const.DefaultAlarmIntervalInSeconds;
    private int alarmIntervalBoosted = Const.DefaultAlarmIntervalInSeconds;
    public synchronized void setAlarmIntervalNormal(int sec) {
        alarmIntervalNormal = sec;
        Log.i("setAlarmIntervalNormal", String.valueOf(sec));
    }
    public synchronized void setAlarmIntervalBoosted(int sec) {
        alarmIntervalBoosted = sec;
        Log.i("setAlarmIntervalBoosted", String.valueOf(sec));
    }
    public synchronized int getAlarmIntervalNormal() {
        return alarmIntervalNormal;
    }
    public synchronized int getAlarmIntervalBoosted() {
        return alarmIntervalBoosted;
    }
}
