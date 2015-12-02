package com.example.dmsv4.dmslauncher;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void showCal(View v){
        Intent i = getPackageManager().getLaunchIntentForPackage("com.android.calculator2");
        startActivity(i);
    }
    public void showApps(View v){
        Intent i = new Intent(this, AppList.class);
        startActivity(i);
    }
    public void callPhone(View v){
        Intent i = getPackageManager().getLaunchIntentForPackage("com.android.contacts");
        startActivity(i);
    }

    public void showSms(View v){
        Intent i = getPackageManager().getLaunchIntentForPackage("com.android.mms");
        startActivity(i);
    }

    public void showGmail(View v){
        Intent i = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
        startActivity(i);
    }

    @Override
    public void onBackPressed() {

    }
}