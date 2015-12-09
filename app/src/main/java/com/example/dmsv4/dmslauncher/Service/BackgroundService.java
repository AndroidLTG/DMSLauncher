package com.example.dmsv4.dmslauncher.Service;

import android.app.Service;
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
