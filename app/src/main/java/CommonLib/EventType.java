package CommonLib;

import java.util.ArrayList;

/**
 * Created by My PC on 26/11/2015.
 */
public abstract class EventType {
    public static enum Type {
        Login,
        ChangePass,
        LoadOrders
    }

    ;

    public static class EventBase {
        public Type type;

        public EventBase(Type type) {
            this.type = type;
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

    public static class EventChangeRequest extends EventBase {
        public String oldPass, newPass;

        public EventChangeRequest(String old_pwd, String new_pwd) {
            super(Type.ChangePass);
            this.oldPass = old_pwd;
            this.newPass = new_pwd;

        }
    }

    public static class EventLoadRequest extends EventBase {
        public EventLoadRequest() {
            super(Type.LoadOrders);
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

    public static class EventChangeResult extends EventBase {
        public boolean success;
        public String message;

        public EventChangeResult(boolean success, String message) {
            super(Type.ChangePass);
            this.success = success;
            this.message = message;
        }
    }

    public static class EventLoadResult extends EventBase {
        public boolean success;
        public String message;
        public ArrayList<Order> arrOrder;

        public EventLoadResult(boolean success, String message, ArrayList<Order> arrOrder) {
            super(Type.LoadOrders);
            this.success = success;
            this.message = message;
            this.arrOrder = arrOrder;
        }
    }
}

