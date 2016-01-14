package CommonLib;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

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

    public void init(Context context) {
        telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = context.registerReceiver(null, ifilter);
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
}
