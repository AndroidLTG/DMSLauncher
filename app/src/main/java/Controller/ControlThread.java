package Controller;

import android.util.Log;

import CommonLib.Const;
import CommonLib.EventPool;
import CommonLib.EventType;

/**
 * Created by My PC on 27/11/2015.
 */
public class ControlThread extends Thread{
    private static ControlThread instance = null;
    private ControlThread() {super(); }
    public synchronized static ControlThread inst(){
        if (instance == null) {
            instance = new ControlThread();
            Log.d("ControlThread", "Create new instance");
        }
        return instance;
    }

    private boolean isRunning = false;
    public void requestStop() {
        isRunning = false;
    }

    @Override
    public void run() {
        isRunning = true;
        LocalDB.inst();
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
            case BEGIN:
                break;
            case END:
                break;
            default:
                Log.w("Control_processEvent", "unhandled " + event.type);
                break;
        }
    }
}
