package Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import CommonLib.Const;
import CommonLib.EventPool;
import CommonLib.EventType;
import CommonLib.Model;
import CommonLib.TrackingItem;

/**
 * Created by My PC on 04/12/2015.
 */
class LocalDB {
    private static LocalDB instance = null;
    private LocalDB() { }
    public synchronized static LocalDB inst(){
        if (instance == null) {
            instance = new LocalDB();
            Log.d("LocalDB", "Create new instance");
        }
        return instance;
    }

    public synchronized boolean init(Context context) {
        dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
        Log.i("LocalDB", "database initialized");
        String deviceId = getConfigValue(Const.ConfigKeys.DeviceID);
        if (deviceId == null) {
            if (!setConfigValue(Const.ConfigKeys.DeviceID, Model.inst().getDeviceId())) {
                Log.e("LocalDB", "cannot save DeviceID");
                return false;
            }
        }
        else {
            if (!deviceId.equals(Model.inst().getDeviceId())) {
                Log.e("LocalDB", "DeviceID mismatched");
                return false;
            }
            String loginToken = getConfigValue(Const.ConfigKeys.LoginToken);
            if (loginToken != null && !loginToken.isEmpty()) {
                String username = LocalDB.inst().getConfigValue(Const.ConfigKeys.Username);
                String fullname = LocalDB.inst().getConfigValue(Const.ConfigKeys.Fullname);
                Model.inst().setLogin(loginToken, username, fullname);
                EventPool.view().enQueue(new EventType.EventLoginResult(true, ""));
            }
        }
        return true;
    }

    public synchronized String getConfigValue(Const.ConfigKeys key) {
        Cursor cursor = db.rawQuery("select Value from " + DbHelper.CONFIG_NAME + " where Key=" + String.valueOf(key.ordinal()), null);
        if (cursor.getCount() == 0) return null;
        cursor.moveToFirst();
        return cursor.getString(0);
    }

    public synchronized boolean setConfigValue(Const.ConfigKeys key, Object value) {
        ContentValues cv = new ContentValues();
        cv.put("Value", value.toString());
        if (getConfigValue(key) == null) {
            cv.put("Key", key.ordinal());
            return db.insert(DbHelper.CONFIG_NAME, null, cv) >= 0;
        }
        else {
            return db.update(DbHelper.CONFIG_NAME, cv, "Key=" + String.valueOf(key.ordinal()), null) > 0;
        }
    }

    public synchronized long addTracking(TrackingItem trackingItem) {
        ContentValues cv = new ContentValues();
        cv.put("DeviceID", trackingItem.deviceId);
        cv.put("VisitedID", trackingItem.visitedId);
        cv.put("VisitedType", trackingItem.visitedType);
        cv.put("Latitude", trackingItem.latitude);
        cv.put("Longitude", trackingItem.longitude);
        cv.put("Accuracy", trackingItem.accuracy);
        cv.put("Speed", trackingItem.speed);
        cv.put("DistanceMeter", trackingItem.distanceMeter);
        cv.put("MilisecElapsed", trackingItem.milisecElapsed);
        cv.put("LocationDate", trackingItem.locationDate);
        cv.put("TrackingDate", trackingItem.trackingDate);
        cv.put("IsWifi", trackingItem.isWifi);
        cv.put("Is3G", trackingItem.is3G);
        cv.put("IsAirplaneMode", trackingItem.isAirplaneMode);
        cv.put("IsGPS", trackingItem.isGPS);
        cv.put("Note", trackingItem.note);
        cv.put("GetType", trackingItem.getType);
        cv.put("GetMethod", trackingItem.getMethod);
        if (trackingItem.cellInfo != null) {
            cv.put("CellID", trackingItem.cellInfo.cellID);
            cv.put("LAC", trackingItem.cellInfo.LAC);
            cv.put("MCC", trackingItem.cellInfo.MCC);
            cv.put("MNC", trackingItem.cellInfo.MNC);
        }
        cv.put("BatteryLevel", trackingItem.batteryLevel);
        long rowId = db.insert(DbHelper.TABLE_NAME, null, cv);
        if (rowId >= 0) trackingItem.rowID = (int)rowId;
        return rowId;
    }

    public synchronized boolean setTrackingRowIDSeed(int seed) {
        TrackingItem[] items = getLastTrackingRecords(1);
        if (items.length > 0) {
            if (items[0].rowID >= seed) {
                Log.i("LocalDB", "no need to setTrackingRowIDSeed");
                return false;
            }
            Log.w("LocalDB", "setTrackingRowIDSeed reseed from " + items[0].rowID + " to " + seed);
        }
        else {
            Log.i("LocalDB", "setTrackingRowIDSeed to " + seed);
        }
        db.execSQL("insert into " + DbHelper.TABLE_NAME + "(RowID) values(" + seed + "); delete from " + DbHelper.TABLE_NAME + " where RowID=" + seed);
        return true;
    }

    public synchronized TrackingItem[] getLastTrackingRecords(int n) {
        Cursor cursor = db.rawQuery("select * from " + DbHelper.TABLE_NAME + " order by RowID desc limit " + n, null);
        TrackingItem[] result = new TrackingItem[cursor.getCount()];
        cursor.moveToFirst();
        for (int i = result.length - 1; i >= 0; i--) {
            TrackingItem trackingItem = new TrackingItem();
            trackingItem.rowID = cursor.getInt(cursor.getColumnIndex("RowID"));
            trackingItem.deviceId = cursor.getString(cursor.getColumnIndex("DeviceID"));
            trackingItem.visitedId = cursor.getString(cursor.getColumnIndex("VisitedID"));
            trackingItem.visitedType = (byte)cursor.getInt(cursor.getColumnIndex("VisitedType"));
            trackingItem.latitude = cursor.getDouble(cursor.getColumnIndex("Latitude"));
            trackingItem.longitude = cursor.getDouble(cursor.getColumnIndex("Longitude"));
            trackingItem.accuracy = cursor.getFloat(cursor.getColumnIndex("Accuracy"));
            trackingItem.speed = cursor.getFloat(cursor.getColumnIndex("Speed"));
            trackingItem.distanceMeter = cursor.getInt(cursor.getColumnIndex("DistanceMeter"));
            trackingItem.milisecElapsed = cursor.getInt(cursor.getColumnIndex("MilisecElapsed"));
            trackingItem.locationDate = cursor.getLong(cursor.getColumnIndex("LocationDate"));
            trackingItem.trackingDate = cursor.getLong(cursor.getColumnIndex("TrackingDate"));
            trackingItem.isWifi = (byte)cursor.getInt(cursor.getColumnIndex("IsWifi"));
            trackingItem.is3G = (byte)cursor.getInt(cursor.getColumnIndex("Is3G"));
            trackingItem.isAirplaneMode = (byte)cursor.getInt(cursor.getColumnIndex("IsAirplaneMode"));
            trackingItem.isGPS = (byte)cursor.getInt(cursor.getColumnIndex("IsGPS"));
            trackingItem.note = cursor.getString(cursor.getColumnIndex("Note"));
            trackingItem.getType = (byte)cursor.getInt(cursor.getColumnIndex("GetType"));
            trackingItem.getMethod = (byte)cursor.getInt(cursor.getColumnIndex("GetMethod"));
            if (!cursor.isNull(cursor.getColumnIndex("CellID"))) {
                trackingItem.cellInfo = new TrackingItem.CellInfo();
                trackingItem.cellInfo.cellID = cursor.getInt(cursor.getColumnIndex("CellID"));
                trackingItem.cellInfo.LAC = cursor.getInt(cursor.getColumnIndex("LAC"));
                trackingItem.cellInfo.MCC = cursor.getInt(cursor.getColumnIndex("MCC"));
                trackingItem.cellInfo.MNC = cursor.getInt(cursor.getColumnIndex("MNC"));
            }
            trackingItem.batteryLevel = cursor.getInt(cursor.getColumnIndex("BatteryLevel"));
            result[i] = trackingItem;
            cursor.moveToNext();
        }
        return result;
    }

    public synchronized boolean setAckLocationID(int id) {
        ContentValues cv = new ContentValues();
        cv.put("Acked", 1);
        if (db.update(DbHelper.TABLE_NAME, cv, "RowID=" + id, null) > 0) {
            Model.inst().setLastAckLocationID(id);
            return true;
        }
        return false;
    }

    private SQLiteDatabase db = null;
    private DbHelper dbHelper = null;
    private class DbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 6;
        public static final String DATABASE_NAME = "edms_local.db";
        public static final String TABLE_NAME = "tblTracking";
        public static final String CONFIG_NAME = "tblConfig";
        public static final String SQL_CREATE_ENTRIES = "create table " + TABLE_NAME + " ("
                + "RowID integer primary key autoincrement"
                + ",DeviceID nvarchar(30)"
                + ",VisitedID nvarchar(30)"
                + ",VisitedType tinyint"
                + ",Latitude float"
                + ",Longitude float"
                + ",Accuracy float"
                + ",Speed float"
                + ",DistanceMeter int"
                + ",MilisecElapsed int"
                + ",LocationDate bigint"
                + ",TrackingDate bigint"
                + ",IsWifi bit"
                + ",Is3G bit"
                + ",IsAirplaneMode bit"
                + ",IsGPS bit"
                + ",Note nvarchar(250)"
                + ",GetType tinyint"
                + ",GetMethod tinyint"
                + ",CellID int"
                + ",LAC int"
                + ",MCC int"
                + ",MNC int"
                + ",BatteryLevel int"
                + ",Acked tinyint"
                + ");";
        public static final String SQL_CREATE_CONFIG = "create table " + CONFIG_NAME + " ("
                + "RowID integer primary key autoincrement"
                + ",Key integer unique not null"
                + ",Value nvarchar(250) not null"
                + ");";
        public static final String SQL_DELETE_ENTRIES = "drop table if exists " + TABLE_NAME + ";";
        public static final String SQL_DELETE_CONFIG = "drop table if exists " + CONFIG_NAME + ";";

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
            db.execSQL(SQL_CREATE_CONFIG);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            Log.i("DbHelper", "Change version from " + oldVersion + " to " + newVersion);
            db.execSQL(SQL_DELETE_ENTRIES);
            db.execSQL(SQL_DELETE_CONFIG);
            onCreate(db);
        }
        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

}
