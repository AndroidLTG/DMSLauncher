package Controller;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;

import CommonLib.AlarmTimer;
import CommonLib.Const;
import CommonLib.EventPool;
import CommonLib.EventType;
import CommonLib.LocationDetector;
import CommonLib.Model;
import CommonLib.PhoneState;
import CommonLib.WakeLock;

/**
 * Created by My PC on 27/11/2015.
 */
public class ControlThread extends Thread {
    private static ControlThread instance = null;
    private boolean isRunning = false;

    private ControlThread() {
        super();
    }

    public synchronized static ControlThread inst() {
        if (instance == null) {
            instance = new ControlThread();
            Log.d("ControlThread", "Create new instance");
        }
        return instance;
    }

    public void init(Context context) {
        if (Model.inst().getDeviceId(context).isEmpty()) {
            EventPool.view().enQueue(new EventType.EventError("Không thể lấy thông tin thiết bị"));
            return;
        }
        if (!LocalDB.inst().init(context)) {
            EventPool.view().enQueue(new EventType.EventError("Lỗi dữ liệu cục bộ"));
            return;
        }
        PhoneState.inst().init(context);
        WakeLock.inst().init(context);
        LocationDetector.inst().start(context);
        AlarmTimer.inst().start(context);
        super.start();
    }

    private void initInWorkingThread() {
        NetworkTransaction.inst().getConfigs();
    }

    public void requestStop() {
        isRunning = false;
        LocationDetector.inst().stop();
    }

    @Override
    public void run() {
        isRunning = true;
        initInWorkingThread();
        try {
            while (isRunning) {
                Log.v("QueueTimerControl", "timedout");
                EventType.EventBase event = EventPool.control().deQueue();
                if (event == null) {
                    sleep(Const.QueueTimerControl);
                } else {
                    processEvent(event);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processEvent(EventType.EventBase event) {
        switch (event.type) {
            case Login: {
                EventType.EventLoginRequest loginRequest = (EventType.EventLoginRequest)event;
                NetworkTransaction.inst().doLogin(loginRequest.userName, loginRequest.passWord);
            }
                break;
            case Logout: {
                Model.inst().setLogin("", "", "");
                LocalDB.inst().setConfigValue(Const.ConfigKeys.LoginToken, "");
                LocalDB.inst().setConfigValue(Const.ConfigKeys.Username, "");
                LocalDB.inst().setConfigValue(Const.ConfigKeys.Fullname, "");
                EventPool.view().enQueue(new EventType.EventLogoutResult(true, "OK"));
            }
                break;
            case ChangePass:
                EventPool.view().enQueue(new EventType.EventChangeResult(true, "OK"));
                break;
            case LoadOrders:
                EventPool.view().enQueue(new EventType.EventLoadOrderResult(true, "OK", null));
                break;
            case LoadCustomers:
                EventPool.view().enQueue(new EventType.EventLoadCustomerResult(true, "OK", null));
                break;
            case AlarmTrigger: {
                NetworkTransaction.inst().sendTracking();
                AlarmTimer.inst().continueTimer();
                WakeLock.inst().release();
            }
                break;
            case SendListApp: {
                HashMap<String, Integer> listAppRole = new HashMap<>();
                //Load role from webservice and send view
                //To do this

                EventPool.view().enQueue(new EventType.EventListAppResult(listAppRole));
            }
                break;
            case GCMToken: {
                EventType.EventGCMToken gcmToken = (EventType.EventGCMToken)event;
                NetworkTransaction.inst().sendGcmToken(gcmToken.token);
            }
                break;
            case GCMMessage: {
                EventType.EventGCMMessage gcmMessage = (EventType.EventGCMMessage) event;
                EventPool.view().enQueue(new EventType.EventGCMMessage(gcmMessage.message));
            }
                break;
            default:
                Log.w("Control_processEvent", "unhandled " + event.type);
                break;
        }
    }
}
