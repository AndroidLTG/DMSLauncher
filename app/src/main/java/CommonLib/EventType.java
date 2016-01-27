package CommonLib;

import android.location.Location;

import com.vietdms.mobile.dmslauncher.RecycleView.Customer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by My PC on 26/11/2015.
 */
public abstract class EventType {
    public static enum Type {
        Error,
        Login,
        Logout,
        ChangePass,
        LoadOrders,
        LoadCustomers,
        AlarmTrigger,
        SendListApp,
        SendLogCrash,
        TakePhoto,
        HighPrecisionLocation,
        GCMToken,
        GCMMessage
    }

    public static class EventBase {
        public Type type;

        public EventBase(Type type) {
            this.type = type;
        }
    }

    //SEND FROM VIEW
    public static class EventGCMToken extends EventBase {
        public String token;

        public EventGCMToken(String token) {
            super(Type.GCMToken);
            this.token = token;
        }
    }

    public static class EventError extends EventBase {
        String message;

        public EventError(String message) {
            super(Type.Error);
            this.message = message;
        }
    }

    public static class EventTakePhoto extends EventBase {
        public String imagePath;
        public long imageTime;

        public EventTakePhoto(String imagePath, long imageTime) {
            super(Type.TakePhoto);
            this.imagePath = imagePath;
            this.imageTime = imageTime;
        }
    }

    public static class EventLogCrash extends EventBase {
        public String logCrash;

        public EventLogCrash(String logCrash) {
            super(Type.SendLogCrash);
            this.logCrash = logCrash;
        }
    }

    public static class EventListApp extends EventBase {
        public HashMap<String, String> listApp;

        public EventListApp(HashMap<String, String> listApp) {
            super(Type.SendListApp);
            this.listApp = listApp;
        }
    }

    public static class EventLoginRequest extends EventBase {
        public String userName, passWord;

        public EventLoginRequest(String username, String password) {
            super(Type.Login);
            this.userName = username;
            this.passWord = password;

        }
    }
    public static class EventLogoutRequest extends EventBase {
        public EventLogoutRequest() {
            super(Type.Logout);
        }
    }

    public static class EventChangeRequest extends EventBase {
        public String oldPass, newPass;

        public EventChangeRequest(String old_pwd, String new_pwd) {
            super(Type.ChangePass);
            this.oldPass = old_pwd;
            this.newPass = new_pwd;

        }
    }

    public static class EventLoadOrderRequest extends EventBase {
        public EventLoadOrderRequest() {
            super(Type.LoadOrders);
        }
    }

    public static class EventLoadHighPrecisionLocationRequest extends EventBase {
        public EventLoadHighPrecisionLocationRequest() {
            super(Type.HighPrecisionLocation);
        }
    }

    public static class EventLoadCustomerRequest extends EventBase {
        public EventLoadCustomerRequest() {
            super(Type.LoadCustomers);
        }
    }
//RETURN FROM CONTROL

    public static class EventGCMMessage extends EventBase {
        public String message;

        public EventGCMMessage(String message) {
            super(Type.GCMMessage);
            this.message = message;
        }
    }

    public static class EventListAppResult extends EventBase {
        public HashMap<String, Integer> listAppRole;

        public EventListAppResult(HashMap<String, Integer> listAppRole) {
            super(Type.SendListApp);
            this.listAppRole = listAppRole;
        }
    }

    public static class EventLoginResult extends EventBase {
        public boolean success;
        public String message;

        public EventLoginResult(boolean success, String message) {
            super(Type.Login);
            this.success = success;
            this.message = message;
        }
    }
    public static class EventLogoutResult extends EventBase {
        public boolean success;
        public String message;

        public EventLogoutResult(boolean success, String message) {
            super(Type.Logout);
            this.success = success;
            this.message = message;
        }
    }

    public static class EventChangeResult extends EventBase {
        public boolean success;
        public String message;

        public EventChangeResult(boolean success, String message) {
            super(Type.ChangePass);
            this.success = success;
            this.message = message;
        }
    }

    public static class EventLoadOrderResult extends EventBase {
        public boolean success;
        public String message;
        public ArrayList<Order> arrOrder;

        public EventLoadOrderResult(boolean success, String message, ArrayList<Order> arrOrder) {
            super(Type.LoadOrders);
            this.success = success;
            this.message = message;
            this.arrOrder = arrOrder;
        }
    }

    public static class EventLoadCustomerResult extends EventBase {
        public boolean success;
        public String message;
        public ArrayList<Customer> arrCustomer;

        public EventLoadCustomerResult(boolean success, String message, ArrayList<Customer> arrCustomer) {
            super(Type.LoadCustomers);
            this.success = success;
            this.message = message;
            this.arrCustomer = arrCustomer;
        }
    }

    public static class EventLoadHighPrecisionLocationResult extends EventBase {
        public Location location;
        public String message;

        public EventLoadHighPrecisionLocationResult(Location location, String message) {
            super(Type.HighPrecisionLocation);
            this.location = location;
            this.message = message;
        }
    }

}

