package CommonLib;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.vietdms.mobile.dmslauncher.Receiver.AlarmReceiver;

/**
 * Created by My PC on 22/01/2016.
 */
public class AlarmTimer {
    private static AlarmTimer instance = null;
    private AlarmTimer() {
    }
    public synchronized static AlarmTimer inst(){
        if (instance == null) {
            instance = new AlarmTimer();
            Log.d("AlarmTimer", "Create new instance");
        }
        return instance;
    }

    private AlarmManager alarmManager = null;
    private PendingIntent pendingIntent = null;
    private int alarmInterval = Const.DefaultAlarmIntervalInSeconds;

    public synchronized boolean start(Context context) {
        try {
            if (alarmManager == null) {
                alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            }
            if (pendingIntent == null) {
                Intent intent = new Intent(context, AlarmReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            }
            if (alarmManager == null || pendingIntent == null) return false;
        }
        catch (Exception ex) {
            return false;
        }
        return setTimer(3);
    }

    public synchronized void setAlarmInterval(int sec) {
        if (alarmInterval == sec) return;
        Log.i("AlarmTimer", "setAlarmInterval from " + alarmInterval + " to " + sec);
        alarmInterval = sec;
    }

    public synchronized boolean continueTimer() {
        if (alarmInterval > 0) return setTimer(alarmInterval);
        stopTimer();
        return false;
    }

    public synchronized boolean setTimer(int sec) {
        try {
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + sec * 1000, pendingIntent);
        }
        catch (Exception ex) {
            return false;
        }
        return true;
    }

    public synchronized void stopTimer() {
        try {
            alarmManager.cancel(pendingIntent);
        }
        catch (Exception ex) { }
    }
}

