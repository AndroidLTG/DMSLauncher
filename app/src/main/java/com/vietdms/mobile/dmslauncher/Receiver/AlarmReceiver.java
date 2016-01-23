package com.vietdms.mobile.dmslauncher.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import CommonLib.EventPool;
import CommonLib.EventType;
import CommonLib.WakeLock;

/**
 * Created by DMSv4 on 12/22/2015.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        WakeLock.inst().acquire();
        EventPool.control().enQueue(new EventType.EventBase(EventType.Type.AlarmTrigger));
    }
}