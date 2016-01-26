package Controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import CommonLib.Const;
import CommonLib.Model;
import CommonLib.TrackingItem;

/**
 * Created by My PC on 19/12/2015.
 */
abstract class Packets {
    public static abstract class FromServer {
        public static class Packet {
            protected final byte[] data;
            private int dataOffset;
            public Packet(byte[] data) {
                this.data = data;
                dataOffset = 0;
            }
            protected boolean readBool() {
                return data[dataOffset++] != 0;
            }
            protected byte readByte()
            {
                return data[dataOffset++];
            }
            protected short readShort() {
                short result = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getShort(dataOffset);
                dataOffset += 2;
                return result;
            }
            protected int readInt() {
                int result = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getInt(dataOffset);
                dataOffset += 4;
                return result;
            }
            protected long readLong() {
                long result = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getLong(dataOffset);
                dataOffset += 8;
                return result;
            }
            protected float readFloat() {
                float result = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getFloat(dataOffset);
                dataOffset += 4;
                return result;
            }
            protected double readDouble() {
                double result = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getDouble(dataOffset);
                dataOffset += 8;
                return result;
            }
            protected String readString() {
                short len = readShort();
                String result = "";
                try {
                    if (len < 0) {
                        len = (short) -(len * 2);
                        result = new String(data, dataOffset, len, "UTF-16LE");
                    } else if (len > 0){
                        result = new String(data, dataOffset, len, "US-ASCII");
                    }
                }
                catch (UnsupportedEncodingException ex) {}
                dataOffset += len;
                return result;
            }
        }
        public static class PacketSendTracking extends Packet {
            public PacketSendTracking(byte[] data) {
                super(data);
                lastAckLocationID = readInt();
            }
            public final int lastAckLocationID;
        }
        public static class PacketGetConfig extends Packet {
            public PacketGetConfig(byte[] data) {
                super(data);
                int len = readInt();
                for (int i = 0; i < len; i++) {
                    int key = readInt();
                    String value = readString();
                    for (int j = 0; j < Const.ConfigKeys.values().length; j++) {
                        if (Const.ConfigKeys.values()[j].ordinal() == key) {
                            map.put(Const.ConfigKeys.values()[j], value);
                            break;
                        }
                        else {
                            //error?
                        }
                    }
                }
            }
            public final HashMap<Const.ConfigKeys, String> map = new HashMap<Const.ConfigKeys, String>();
        }
    }

    public static abstract class ToServer {
        public enum PacketType{
            None,
            Test,
            GetConfig,
            Login,
            SendTracking
        }

        public static class Packet {
            public Packet(PacketType type) {
                this(type, 0);
            }
            public Packet(PacketType type, int extrasize) {
                this.type = type;
                data = new ByteArrayOutputStream(4 + Model.inst().getDeviceId().length() + extrasize);
                write((short) type.ordinal());
                write(Model.inst().getDeviceId(), true);
            }
            public byte[] getData() {
                return data.toByteArray();
            }
            public final PacketType type;
            private final ByteArrayOutputStream data;
            protected void write(boolean value) {
                data.write((byte)(value ? 1 : 0));
            }
            protected void write(byte value) {
                data.write(value);
            }
            protected void write(short value) {
                try {
                    byte[] buff = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(value).array();
                    data.write(buff);
                }
                catch (IOException ex) {
                }
            }
            protected void write(int value) {
                try {
                    byte[] buff = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array();
                    data.write(buff);
                }
                catch (IOException ex) {
                }
            }
            protected void write(long value) {
                try {
                    byte[] buff = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(value).array();
                    data.write(buff);
                }
                catch (IOException ex) {
                }
            }
            protected void write(float value) {
                try {
                    byte[] buff = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(value).array();
                    data.write(buff);
                }
                catch (IOException ex) {
                }
            }
            protected void write(double value) {
                try {
                    byte[] buff = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putDouble(value).array();
                    data.write(buff);
                }
                catch (IOException ex) {
                }
            }
            protected void write(String value, boolean isASCII) {
                if (value == null) {
                    write((short)0);
                }
                else {
                    write((short) (isASCII ? value.length() : -value.length()));
                    for (int i = 0; i < value.length(); i++) {
                        write((byte) value.charAt(i));
                        if (!isASCII) write((byte) (((short) value.charAt(i)) >> 8));
                    }
                }
            }
        }

        public static class PacketTest extends Packet {
            public PacketTest(String text, double real) {
                super(PacketType.Test);
                write(text, false);
                write(real);
            }
        }
        public static class PacketLogin extends Packet {
            public PacketLogin(String username, String password) {
                super(PacketType.Login);
                write(username, false);
                write(password, false);
            }
        }
        public static class PacketSendTracking extends Packet {
            public PacketSendTracking(TrackingItem[] items) {
                super(PacketType.SendTracking);
                write(items.length);
                for (int i = 0; i < items.length; i++) {
                    TrackingItem item = items[i];
                    write(item.rowID);
                    write(item.deviceId, false);
                    write(item.visitedId, false);
                    write(item.visitedType);
                    write(item.latitude);
                    write(item.longitude);
                    write(item.accuracy);
                    write(item.speed);
                    write(item.distanceMeter);
                    write(item.milisecElapsed);
                    write(item.locationDate);
                    write(item.trackingDate);
                    write(item.note, false);
                    write(item.getType);
                    write(item.getMethod);
                    write(item.isWifi);
                    write(item.is3G);
                    write(item.isAirplaneMode);
                    write(item.isGPS);
                    if (item.cellInfo != null) {
                        write(true);
                        write(item.cellInfo.cellID);
                        write(item.cellInfo.LAC);
                        write(item.cellInfo.MCC);
                        write(item.cellInfo.MNC);
                    } else {
                        write(false);
                    }
                    write(item.batteryLevel);
                }
            }
        }
    }
}
