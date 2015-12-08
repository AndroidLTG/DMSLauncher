package com.example.dmsv4.dmslauncher;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.dmsv4.dmslauncher.Fragment.CenterFragment;
import com.example.dmsv4.dmslauncher.Fragment.LeftFragment;
import com.example.dmsv4.dmslauncher.Fragment.RightFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    DevicePolicyManager devicePolicyManager;
    ComponentName dmsDeviceAdmin;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    static final int ACTIVATION_REQUEST = 47; // identifies our request id
    private Animation animFadein;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        Log.w("onCreate", "onCreate");
        init();
    }

    @Override
    public void onClick(View v) {

    }

    private void init() {

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        //Create viewpager with three fragment
        setupViewPager(viewPager);
        //Get permision ADMIN DEVICE
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        dmsDeviceAdmin = new ComponentName(this, DMSDeviceAdminReceiver.class);
        Intent intent = new Intent(
                DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                dmsDeviceAdmin);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Your boss told you to do this");
        startActivityForResult(intent, ACTIVATION_REQUEST);
        //Set current page is center fragment
        viewPager.setCurrentItem(1);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LeftFragment(), "LEFT");
        adapter.addFragment(new CenterFragment(), "CENTER");
        adapter.addFragment(new RightFragment(), "RIGHT");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {//Adapter for fragment
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onPause() {
        Log.w("onPause", "onPause");
        //Set current page to center fragment
        viewPager.setCurrentItem(1);
        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_out);
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.w("onResume", "onResume");
        super.onResume();
    }

    @Override
    protected void onRestart() {
        Log.w("onRestart", "onRestart");
        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        super.onRestart();
    }

    @Override
    protected void onStart() {
        Log.w("onStart", "onStart");
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        Log.w("onDestroy", "onDestroy");
        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_out);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        Log.w("onStop", "onStop");
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTIVATION_REQUEST:
                if (resultCode != Activity.RESULT_OK) {//If not has permision ADMIN DEVICE then open activity get permision again
                    Intent intent = new Intent(
                            DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                            dmsDeviceAdmin);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                            "Your boss told you to do this");
                    startActivityForResult(intent, ACTIVATION_REQUEST);
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void showCal(View v) {//Show calculator app
        Intent i = getPackageManager().getLaunchIntentForPackage("com.android.calculator2");
        startActivity(i);
    }

    public void showApps(View v) {//Show list app in device
        Intent i = new Intent(MainActivity.this, AppList.class);
        startActivity(i);
    }

    public void callPhone(View v) {// Show call app
        Intent i = getPackageManager().getLaunchIntentForPackage("com.android.contacts");
        startActivity(i);
    }

    public void lockDevice(View v) {// Lock screen
        devicePolicyManager.lockNow();
    }

    public void showSms(View v) {// Show sms app
        Intent i = getPackageManager().getLaunchIntentForPackage("com.android.mms");
        startActivity(i);
    }

    public void showGmail(View v) {//show mail app
        Intent i = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 2) {
            //if in check in
            if (adapter.getItem(2).getView().findViewById(R.id.rela_layout_checkin).getVisibility() == View.VISIBLE) {
                adapter.getItem(2).getView().findViewById(R.id.rela_layout_checkin).setVisibility(View.GONE);
                adapter.getItem(2).getView().findViewById(R.id.rela_layout_main).setVisibility(View.VISIBLE);
            }
            //if in check out
            else if (adapter.getItem(2).getView().findViewById(R.id.rela_layout_checkout).getVisibility() == View.VISIBLE) {
                adapter.getItem(2).getView().findViewById(R.id.rela_layout_checkout).setVisibility(View.GONE);
                adapter.getItem(2).getView().findViewById(R.id.rela_layout_main).setVisibility(View.VISIBLE);
            } else // if in main
                viewPager.setCurrentItem(1);
        }
        if (viewPager.getCurrentItem() == 0) {
            viewPager.setCurrentItem(1);
        }
    }

}