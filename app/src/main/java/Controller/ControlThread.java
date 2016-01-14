package Controller;

import android.app.usage.UsageEvents;
import android.content.Context;
import android.util.Log;

import CommonLib.Const;
import CommonLib.EventType;
import CommonLib.EventPool;
import CommonLib.Model;
import CommonLib.PhoneState;
import CommonLib.WakeLock;

/**
 * Created by My PC on 27/11/2015.
 */
public class ControlThread extends Thread{
    private static ControlThread instance = null;
    private ControlThread() { super(); }
    public synchronized static ControlThread inst(){
        if (instance == null) {
            instance = new ControlThread();
            Log.d("ControlThread", "Create new instance");
        }
        return instance;
    }

    public void init(Context context) {
        Model.inst().getDeviceId(context);
        PhoneState.inst().init(context);
        WakeLock.inst().init(context);
        LocalDB.inst().init(context);
        LocationDetector.inst().start(context);
        super.start();
    }

    private void initInWorkingThread() {
        NetworkTransaction.inst().getConfigs();
    }

    private boolean isRunning = false;
    public void requestStop() {
        isRunning = false;
        LocationDetector.inst().stop();
    }

    @Override
    public void run() {
        isRunning = true;
        initInWorkingThread();
        try {
            while(isRunning) {
                Log.v("QueueTimerControl", "timedout");
                EventType.EventBase event = EventPool.control().deQueue();
                if (event == null) {
                    sleep(Const.QueueTimerControl);
                }
                else {
                    processEvent(event);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void processEvent(EventType.EventBase event) {
        switch (event.type) {
            case Login:
                EventPool.view().enQueue(new EventType.EventLoginResult(true, "OK"));
                break;
            case ChangePass:
                EventPool.view().enQueue(new EventType.EventChangeResult(true, "OK"));
                break;
            case LoadOrders:
                EventPool.view().enQueue(new EventType.EventLoadResult(true, "OK", null));
                break;
            case SendTracking:
                NetworkTransaction.inst().sendTracking();
                WakeLock.inst().release();
                break;
            default:
                Log.w("Control_processEvent", "unhandled " + event.type);
                break;
        }
    }
}
