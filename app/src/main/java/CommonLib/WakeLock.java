package CommonLib;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import com.vietdms.mobile.dmslauncher.R;

/**
 * Created by My PC on 05/01/2016.
 */
public class WakeLock {
    private static WakeLock instance = null;
    private WakeLock() {
    }
    public synchronized static WakeLock inst(){
        if (instance == null) {
            instance = new WakeLock();
            Log.d("WakeLock", "Create new instance");
        }
        return instance;
    }

    private PowerManager.WakeLock wakeLock = null;
    private int lockCount = 0;
    public synchronized boolean init(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (pm == null) return false;
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, context.getString(R.string.tagWL));
        if (wakeLock == null) return false;
        return true;
    }
    public synchronized boolean acquire() {
        if (wakeLock != null) {
            if (lockCount == 0) {
                wakeLock.acquire();
            }
            lockCount++;
            return true;
        }
        return false;
    }
    public synchronized boolean release() {
        if (wakeLock != null) {
            lockCount--;
            if (lockCount == 0) {
                wakeLock.release();
            }
            return true;
        }
        return false;
    }
}
