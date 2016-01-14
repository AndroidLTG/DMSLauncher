package Controller;

import android.location.Location;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import CommonLib.Const;
import CommonLib.Model;
import CommonLib.PhoneState;
import CommonLib.TrackingItem;

/**
 * Created by My PC on 04/12/2015.
 */
class NetworkTransaction {
    private static NetworkTransaction instance = null;
    private URL defaultUrl = null;

    private NetworkTransaction() {
        try {
            defaultUrl = new URL(Const.HttpEndpoint);
        } catch (Exception ex) {
        }
    }

    public synchronized static NetworkTransaction inst() {
        if (instance == null) {
            instance = new NetworkTransaction();
            Log.d("NetworkTransaction", "Create new instance");
        }
        return instance;
    }

    public static byte[] sendPostRequest(URL url, byte[] send, boolean recvNeeded) {
        if (recvNeeded) {
            ByteArrayOutputStream recv = new ByteArrayOutputStream();
            if (sendPostRequest(url, send, recv)) return recv.toByteArray();
        } else {
            if (sendPostRequest(url, send, null)) return new byte[0];
        }
        return null;
    }

    public static boolean sendPostRequest(URL url, byte[] send, ByteArrayOutputStream recv) {
        InputStream reader = null;
        try {
            URLConnection conn = url.openConnection();
            if (!url.getHost().equals(conn.getURL().getHost())) {
                throw new Exception("Host redirected!");
            }
            conn.setDoOutput(true);
            conn.setDoInput(true);
            OutputStream wr = conn.getOutputStream();
            wr.write(send);
            wr.flush();
            reader = conn.getInputStream();
            byte[] buff = new byte[4096];
            int read = reader.read(buff);
            while (read > 0) {
                if (recv != null) recv.write(buff, 0, read);
                read = reader.read(buff);
            }
            return true;
        } catch (Exception ex) {
            Log.e("sendPostRequest", ex.toString());
            return false;
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (Exception ex) {
            }
        }
    }

    public synchronized void sendTracking() {
        int lastAckLocationID = Model.inst().getLastAckLocationID();
        if (lastAckLocationID < 0) {
            Log.w("sendTracking", "lastAckLocationID not set");
            getConfigs();
            return;
        }
        Location location = Model.inst().getLastLocation();
        TrackingItem item = new TrackingItem();
        if (location != null) {
            item.latitude = location.getLatitude();
            item.longitude = location.getLongitude();
            item.accuracy = location.getAccuracy();
            item.speed = location.getSpeed();
            item.locationDate = location.getTime();
        }
        item.cellInfo = PhoneState.inst().getCellInfo();
        item.batteryLevel = PhoneState.inst().getBatteryLevel();
        if (LocalDB.inst().addTracking(item) < 0) {
            Log.e("sendTracking", "cannot add TrackingItem to LocalDB");
            return;
        }
        int nUnAcked = item.rowID - lastAckLocationID;
        if (nUnAcked <= 0) {
            Log.e("sendTracking", "lastAckLocationID mismatch");
            return;
        }
        TrackingItem[] items;
        if (nUnAcked == 1) {
            items = new TrackingItem[1];
            items[0] = item;
        } else {
            items = LocalDB.inst().getLastTrackingRecords(nUnAcked < Const.MaxRecordsLastSend ? nUnAcked : Const.MaxRecordsLastSend);
        }
        byte[] buffer = new Packets.ToServer.PacketSendTracking(items).getData();
        byte[] result = sendPostRequest(defaultUrl, buffer, true);
        if (result != null) {
            Log.i("sendTracking", "success n=" + items.length);
            Packets.FromServer.PacketSendTracking packetSendTracking = new Packets.FromServer.PacketSendTracking(result);
            if (packetSendTracking.lastAckLocationID == -2) {
                Log.w("sendTracking", "server cannot add TrackingItem");
            } else if (packetSendTracking.lastAckLocationID < 0) {
                Log.w("sendTracking", "server cannot set lastAckLocationID");
            } else {
                if (LocalDB.inst().setAckLocationID(packetSendTracking.lastAckLocationID)) {
                    Log.i("sendTracking", "lastAckLocationID=" + packetSendTracking.lastAckLocationID);
                } else {
                    Log.w("sendTracking", "cannot set lastAckLocationID=" + packetSendTracking.lastAckLocationID);
                }
            }
        } else {
            Log.w("sendTracking", "fail n=" + items.length);
        }
    }

    public synchronized boolean getConfigs() {
        byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.Packet(Packets.ToServer.PacketType.GetConfig).getData(), true);
        if (result != null) {
            Log.i("getConfigs", "success");
            Packets.FromServer.PacketGetConfig packetGetConfig = new Packets.FromServer.PacketGetConfig(result);
            String lastTrackingID = packetGetConfig.map.get(Const.ConfigKeys.InitTrackingRowID);
            if (lastTrackingID != null) {
                int id = Integer.parseInt(lastTrackingID);
                if (id >= 0) {
                    Model.inst().setLastAckLocationID(id);
                    LocalDB.inst().setTrackingRowIDSeed(id);
                }
            }
            return true;
        } else {
            Log.w("getConfigs", "fail");
            return false;
        }
    }
}
