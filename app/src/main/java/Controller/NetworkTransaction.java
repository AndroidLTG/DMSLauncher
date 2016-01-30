package Controller;

import android.graphics.AvoidXfermode;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import CommonLib.Const;
import CommonLib.EventPool;
import CommonLib.EventType;
import CommonLib.Model;
import CommonLib.PhoneState;
import CommonLib.TrackingItem;

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

    public synchronized void sendTracking() {
        int lastAckLocationID = Model.inst().getLastAckLocationID();
        if (lastAckLocationID < 0) {
            Log.w("sendTracking", "lastAckLocationID not set");
            if (!getConfigs()) {
                if (PhoneState.inst().isWifi() != 1 && PhoneState.inst().is3G() != 1) {
                    PhoneState.inst().turn3GOnOff(true);
                }
            }
            return;
        }
        Location location = Model.inst().getLastLocation();
        TrackingItem item = new TrackingItem();
        if (location != null) {
            item.latitude = location.getLatitude();
            item.longitude = location.getLongitude();
            item.accuracy = location.getAccuracy();
            item.speed = location.getSpeed();
            Bundle extras = location.getExtras();
            if (extras != null) {
                item.distanceMeter = extras.getInt("distanceMeter", 0);
                item.milisecElapsed = extras.getInt("milisecElapsed", 0);
            }
            item.locationDate = location.getTime();
        }
        item.trackingDate = Model.inst().getServerTime();
        item.cellInfo = PhoneState.inst().getCellInfo();
        item.batteryLevel = PhoneState.inst().getBatteryLevel();
        item.isWifi = PhoneState.inst().isWifi();
        item.is3G = PhoneState.inst().is3G();
        item.isGPS = PhoneState.inst().isGPS();
        item.isAirplaneMode = PhoneState.inst().isAirplaneMode();
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
                Packets.FromServer.PacketIntAck packetFromServer = new Packets.FromServer.PacketIntAck(result);
                if (packetFromServer.intValue == -2) {
                    Log.w("sendTracking", "server cannot add TrackingItem");
                    return;
                }
                else if (packetFromServer.intValue < 0) {
                    Log.w("sendTracking", "server cannot set lastAckLocationID");
                    return;
                }
                else {
                    if (LocalDB.inst().setAckLocationID(packetFromServer.intValue)) {
                        Log.i("sendTracking", "lastAckLocationID=" + packetFromServer.intValue);
                    } else {
                        Log.w("sendTracking", "cannot set lastAckLocationID=" + packetFromServer.intValue);
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

    public synchronized boolean sendGcmToken(String gcmToken) {
        byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketSendGcmToken(gcmToken).getData(), true);
        if (result != null) {
            Packets.FromServer.PacketIntAck packetFromServer = new Packets.FromServer.PacketIntAck(result);
            if (packetFromServer.intValue != 0) {
                Log.i("sendGcmToken", "success");
            }
            else {
                Log.w("sendGcmToken", "server cannot save token");
            }
            return true;
        }
        else {
            Log.w("sendGcmToken", "fail");
            return false;
        }
    }

    public synchronized boolean doLogin(String username, String password) {
        byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketLogin(username, password).getData(), true);
        if (result != null) {
            Packets.FromServer.PacketLogin packetLogin = new Packets.FromServer.PacketLogin(result);
            if (packetLogin.success) {
                Log.i("doLogin", "success");
                Model.inst().setLogin(packetLogin.token, packetLogin.username, packetLogin.fullname);
                LocalDB.inst().setConfigValue(Const.ConfigKeys.LoginToken, packetLogin.token);
                LocalDB.inst().setConfigValue(Const.ConfigKeys.Username, packetLogin.username);
                LocalDB.inst().setConfigValue(Const.ConfigKeys.Fullname, packetLogin.fullname);
                EventPool.view().enQueue(new EventType.EventLoginResult(true, packetLogin.message));
            }
            else {
                Log.i("doLogin", "access denied");
                EventPool.view().enQueue(new EventType.EventLoginResult(false, packetLogin.message));
            }
            return true;
        }
        else {
            Log.w("doLogin", "fail");
            EventPool.view().enQueue(new EventType.EventLoginResult(false, "Không thể kết nối đến máy chủ"));
            return false;
        }
    }

    public synchronized boolean getConfigs() {
        byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.Packet(Packets.ToServer.PacketType.GetConfig).getData(), true);
        if (result != null) {
            Log.i("getConfigs", "success");
            Packets.FromServer.PacketGetConfig packetGetConfig = new Packets.FromServer.PacketGetConfig(result);
            if ("true".equals(packetGetConfig.map.get(Const.ConfigKeys.Kickout))) {
                Log.i("getConfigs", "kickout");
                Model.inst().setLogin("", "", "");
                LocalDB.inst().setConfigValue(Const.ConfigKeys.LoginToken, "");
                LocalDB.inst().setConfigValue(Const.ConfigKeys.Username, "");
                LocalDB.inst().setConfigValue(Const.ConfigKeys.Fullname, "");
                EventPool.view().enQueue(new EventType.EventLogoutResult(true, ""));
                return true;
            }
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
            String intervalNormal = packetGetConfig.map.get(Const.ConfigKeys.AlarmIntervalNormal);
            if (intervalNormal != null) {
                int sec = Integer.parseInt(intervalNormal);
                if (sec > 0) {
                    Model.inst().setAlarmIntervalNormal(sec);
                }
            }
            String intervalBoosted = packetGetConfig.map.get(Const.ConfigKeys.AlarmIntervalBoosted);
            if (intervalBoosted != null) {
                int sec = Integer.parseInt(intervalBoosted);
                if (sec > 0) {
                    Model.inst().setAlarmIntervalBoosted(sec);
                }
            }
            String username = packetGetConfig.map.get(Const.ConfigKeys.Username);
            if (username != null && !username.isEmpty()) {
                String fullname = packetGetConfig.map.get(Const.ConfigKeys.Fullname);
                Model.inst().setLogin(Model.inst().getLoginToken(), username, fullname);
                LocalDB.inst().setConfigValue(Const.ConfigKeys.Username, username);
                LocalDB.inst().setConfigValue(Const.ConfigKeys.Fullname, fullname);
                EventPool.view().enQueue(new EventType.EventLoginResult(true, ""));
            }
            return true;
        }
        else {
            Log.w("getConfigs", "fail");
            return false;
        }
    }
    public synchronized boolean getLastLocations(int max) {
        byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketGetLocations(max).getData(), true);
        if (result != null) {
            Log.i("getLastLocations", "success");
            Packets.FromServer.PacketGetLocations packetGetLocations = new Packets.FromServer.PacketGetLocations(result);
            EventPool.view().enQueue(new EventType.EventGetLocationsResult(packetGetLocations.arrayLocations, packetGetLocations.message));
            return true;
        }
        else {
            Log.w("getLastLocations", "fail");
            EventPool.view().enQueue(new EventType.EventGetLocationsResult(null, "Không thể kết nối đến máy chủ"));
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
    public static boolean sendPostRequest(URL url, byte[] send, ByteArrayOutputStream recv) {
        HttpURLConnection conn = null;
        InputStream reader = null;
        try
        {
            conn = (HttpURLConnection)url.openConnection();
            if (!url.getHost().equals(conn.getURL().getHost())) {
                throw new Exception("Host redirected!");
            }
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/octet-stream");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setFixedLengthStreamingMode(send.length);
            conn.connect();
            OutputStream wr = conn.getOutputStream();
            wr.write(send);
            wr.flush();
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = conn.getInputStream();
                byte[] buff = new byte[4096];
                int read = reader.read(buff);
                while (read > 0) {
                    if (recv != null) recv.write(buff, 0, read);
                    read = reader.read(buff);
                }
                return true;
            }
            Log.w("sendPostRequest", "Response Code=" + responseCode);
            return false;
        }
        catch(Exception ex)
        {
            Log.e("sendPostRequest", ex.toString());
            return false;
        }
        finally
        {
            try
            {
                if (reader != null)
                    reader.close();
                if (conn != null)
                    conn.disconnect();
            }
            catch(Exception ex) {}
        }
    }
}
