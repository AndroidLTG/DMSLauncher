package Controller;

import android.graphics.AvoidXfermode;
import android.location.Location;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import CommonLib.Const;
import CommonLib.Model;
import CommonLib.PhoneState;
import CommonLib.TrackingItem;

import com.loopj.android.http.*;

/**
 * Created by My PC on 04/12/2015.
 */
class NetworkTransaction {
    private static NetworkTransaction instance = null;
    private NetworkTransaction() {
        try {
            defaultUrl = new URL(Const.HttpEndpoint);
        }
        catch (Exception ex) {
        }
    }
    public synchronized static NetworkTransaction inst(){
        if (instance == null) {
            instance = new NetworkTransaction();
            Log.d("NetworkTransaction", "Create new instance");
        }
        return instance;
    }
    private URL defaultUrl = null;
    private AsyncHttpClient client = new AsyncHttpClient();

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
        }
        else {
            items = LocalDB.inst().getLastTrackingRecords(nUnAcked < Const.MaxRecordsLastSend ? nUnAcked : Const.MaxRecordsLastSend);
        }
        int remain = items.length;
        while (remain > 0) {
            int nsend = remain > 30 ? 30 : remain;
            TrackingItem[] itemsToSend = new TrackingItem[nsend];
            for (int i = 0; i < nsend; i++) {
                itemsToSend[i] = items[items.length - remain + i];
            }
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketSendTracking(itemsToSend).getData(), true);
            if (result != null) {
                Log.i("sendTracking", "success " + nsend + "/" + remain);
                Packets.FromServer.PacketSendTracking packetSendTracking = new Packets.FromServer.PacketSendTracking(result);
                if (packetSendTracking.lastAckLocationID == -2) {
                    Log.w("sendTracking", "server cannot add TrackingItem");
                    return;
                }
                else if (packetSendTracking.lastAckLocationID < 0) {
                    Log.w("sendTracking", "server cannot set lastAckLocationID");
                    return;
                }
                else {
                    if (LocalDB.inst().setAckLocationID(packetSendTracking.lastAckLocationID)) {
                        Log.i("sendTracking", "lastAckLocationID=" + packetSendTracking.lastAckLocationID);
                    } else {
                        Log.w("sendTracking", "cannot set lastAckLocationID=" + packetSendTracking.lastAckLocationID);
                    }
                }
            }
            else {
                Log.w("sendTracking", "fail " + nsend + "/" + remain);
                return;
            }
            remain -= nsend;
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
            String serverTime = packetGetConfig.map.get(Const.ConfigKeys.ServerTime);
            if (serverTime != null) {
                long timestamp = Long.parseLong(serverTime);
                if (timestamp > 0) {
                    Model.inst().setServerTime(timestamp);
                }
            }
            return true;
        }
        else {
            Log.w("getConfigs", "fail");
            return false;
        }
    }

    public byte[] sendPostRequest(URL url, byte[] send, boolean recvNeeded) {
        if (recvNeeded) {
            ByteArrayOutputStream recv = new ByteArrayOutputStream();
            if (sendPostRequest(url, send, recv)) return recv.toByteArray();
        }
        else {
            if (sendPostRequest(url, send, null)) return new byte[0];
        }
        return null;
    }
    public boolean sendPostRequest(URL url, byte[] send, ByteArrayOutputStream recv) {
        RequestParams params = new RequestParams();
        String[] types = {"application/octet-stream"};
        client.post(Const.HttpEndpoint, params, new BinaryHttpResponseHandler(types) {
            @Override
            public void onSuccess(int i, byte[] bytes) {
                super.onSuccess(i, bytes);
                Log.i("sendPostRequest", "success");
            }

            @Override
            public void onFailure(Throwable throwable, byte[] bytes) {
                super.onFailure(throwable, bytes);
                Log.i("sendPostRequest", "failure");
            }

        });
        return false;
    }
//    public static boolean sendPostRequest(URL url, byte[] send, ByteArrayOutputStream recv) {
//        HttpURLConnection conn = null;
//        InputStream reader = null;
//        try
//        {
//            conn = (HttpURLConnection)url.openConnection();
//            if (!url.getHost().equals(conn.getURL().getHost())) {
//                throw new Exception("Host redirected!");
//            }
//            conn.setDoOutput(true);
//            conn.setDoInput(true);
//            conn.setFixedLengthStreamingMode(send.length);
//            OutputStream wr = conn.getOutputStream();
//            wr.write(send);
//            wr.flush();
//            int responseCode = conn.getResponseCode();
//            Log.i("sendPostRequest", "Response Code=" + responseCode);
//            reader = conn.getInputStream();
//            byte[] buff = new byte[4096];
//            int read = reader.read(buff);
//            while (read > 0) {
//                if (recv != null) recv.write(buff, 0, read);
//                read = reader.read(buff);
//            }
//            return true;
//        }
//        catch(Exception ex)
//        {
//            Log.e("sendPostRequest", ex.toString());
//            return false;
//        }
//        finally
//        {
//            try
//            {
//                if (reader != null)
//                    reader.close();
//                if (conn != null)
//                    conn.disconnect();
//            }
//            catch(Exception ex) {}
//        }
//    }
}
