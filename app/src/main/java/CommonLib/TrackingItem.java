package CommonLib;

/**
 * Created by My PC on 29/12/2015.
 */
public class TrackingItem {
    public static class CellInfo {
        public int cellID;
        public int LAC;
        public int MCC;
        public int MNC;
    }
    public int rowID = -1;
    public String deviceId = Model.inst().getDeviceId();
    public String visitedId;
    public byte visitedType;
    public double latitude;
    public double longitude;
    public float accuracy;
    public float speed;
    public float distanceMeter;
    public int milisecElapsed;
    public String note;
    public byte getType;
    public byte getMethod;
    public byte isWifi = -1;
    public byte is3G = -1;
    public byte isAirplaneMode = -1;
    public byte isGPS = -1;
    public CellInfo cellInfo;
    public int batteryLevel = -1;
    public long locationDate;
}
