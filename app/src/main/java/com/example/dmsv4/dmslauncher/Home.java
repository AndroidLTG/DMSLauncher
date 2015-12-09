package com.example.dmsv4.dmslauncher;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.dmsv4.dmslauncher.CustomAdapter.CustomAdapterGripView;
import com.example.dmsv4.dmslauncher.CustomAdapter.CustomAdapterListView;
import com.example.dmsv4.dmslauncher.Fragment.CenterFragment;
import com.example.dmsv4.dmslauncher.Fragment.LeftFragment;
import com.example.dmsv4.dmslauncher.Fragment.RightFragment;
import com.example.dmsv4.dmslauncher.GetSet.AppsDetail;
import com.example.dmsv4.dmslauncher.GetSet.CallHistory;
import com.example.dmsv4.dmslauncher.Receiver.DMSDeviceAdminReceiver;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener {
    DevicePolicyManager devicePolicyManager;
    ComponentName dmsDeviceAdmin;
    private ViewPager viewPager;
    public static GridView gridListApp;
    private ViewPagerAdapter adapter;
    static final int ACTIVATION_REQUEST = 47; // identifies our request id
    private Animation animFadein;
    public static int LIMITROW = 10;
    public static ListView lstHistory;
    public static CustomAdapterListView adapterListView = null;
    public static ArrayList<CallHistory> arrCall = new ArrayList<CallHistory>();
    public static boolean flag_loading = false;
    private PackageManager manager;
    public static RelativeLayout rela_layout_center;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        Log.w("onCreate", "onCreate");
        getId();
        init();
    }

    private void getId() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);

    }

    @Override
    public void onClick(View v) {

    }

    private void init() {

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

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        AppsDetail appsDetail = (AppsDetail) parent.getItemAtPosition(position);
        Toast.makeText(getApplicationContext(),"Long click :"+appsDetail.getName(),Toast.LENGTH_SHORT).show();
        return false;
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
        if (arrCall != null && adapterListView != null && arrCall.size() > 10) {
            for (int i = 10; i < arrCall.size(); i++) arrCall.remove(i);
            lstHistory.setAdapter(adapterListView);
            adapterListView.notifyDataSetChanged();
        }
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
                            "Trình chạy DMS");
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
        rela_layout_center.setVisibility(View.GONE);
        gridListApp.setVisibility(View.VISIBLE);
        gridListApp.setOnItemLongClickListener(this);
        loadApps();
        final List<AppsDetail> allItems = loadApps();
        CustomAdapterGripView customAdapter = new CustomAdapterGripView(Home.this, allItems);
        gridListApp.setAdapter(customAdapter);

        gridListApp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = manager.getLaunchIntentForPackage(allItems.get(position).name.toString());
                startActivity(i);
            }
        });

    }
    private List<AppsDetail> loadApps() {
        manager = getPackageManager();
        List<AppsDetail> apps = new ArrayList<>();
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
        for (ResolveInfo ri : availableActivities) {
            AppsDetail app = new AppsDetail();
            app.label = ri.loadLabel(manager);
            app.name = ri.activityInfo.packageName;
            app.icon = ri.activityInfo.loadIcon(manager);
            apps.add(app);
        }
        return apps;
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
        if(viewPager.getCurrentItem()==1)
        {
            if(adapter.getItem(1).getView().findViewById(R.id.gridListApp).getVisibility()==View.VISIBLE){
                adapter.getItem(1).getView().findViewById(R.id.gridListApp).setVisibility(View.GONE);
                adapter.getItem(1).getView().findViewById(R.id.rela_layout_center).setVisibility(View.VISIBLE);

            }
        }
    }

    //DETECT PRESS HOME BUTTON
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_MAIN.equals(intent.getAction())) {
            final boolean alreadyOnHome =
                    ((intent.getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
                            != Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            if (alreadyOnHome) {
                adapter.getItem(1).getView().findViewById(R.id.gridListApp).setVisibility(View.GONE);
                adapter.getItem(1).getView().findViewById(R.id.rela_layout_center).setVisibility(View.VISIBLE);
                Log.d("whatever", "Home pressed");
            }
        }
    }
}