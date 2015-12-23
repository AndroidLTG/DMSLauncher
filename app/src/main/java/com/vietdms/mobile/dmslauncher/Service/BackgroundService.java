package com.vietdms.mobile.dmslauncher.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import Controller.ControlThread;

public class BackgroundService extends Service {
    public BackgroundService() {
    }





    @Override
    public void onCreate() {
        Log.d("BackgroundService", "onCreate");
        ControlThread.inst().start();

    }

    @Override
    public void onDestroy() {
        Log.d("BackgroundService", "onDestroy");
        Intent mStartService = new Intent(getApplicationContext(), BackgroundService.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getService(getApplicationContext(), mPendingIntentId, mStartService, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 10000, mPendingIntent);
        System.exit(0);
        ControlThread.inst().requestStop();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("BackgroundService", "onStartCommand");
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
