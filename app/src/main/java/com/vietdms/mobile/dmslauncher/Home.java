package com.vietdms.mobile.dmslauncher;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.victor.loading.rotate.RotateLoading;
import com.vietdms.mobile.dmslauncher.CustomAdapter.CustomAdapterGripView;
import com.vietdms.mobile.dmslauncher.CustomAdapter.CustomAdapterListView;
import com.vietdms.mobile.dmslauncher.Fragment.CenterFragment;
import com.vietdms.mobile.dmslauncher.Fragment.LeftFragment;
import com.vietdms.mobile.dmslauncher.Fragment.RightFragment;
import com.vietdms.mobile.dmslauncher.GetSet.AppsDetail;
import com.vietdms.mobile.dmslauncher.GetSet.CallHistory;
import com.vietdms.mobile.dmslauncher.Receiver.DMSDeviceAdminReceiver;
import com.vietdms.mobile.dmslauncher.Receiver.LocationAlarmManager;
import com.vietdms.mobile.dmslauncher.RecycleView.RecyclerItemClickListener;
import com.vietdms.mobile.dmslauncher.Service.BackgroundService;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Home extends AppCompatActivity implements ViewPager.OnPageChangeListener, TextWatcher, View.OnClickListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, RecyclerItemClickListener.OnItemClickListener {
    static final int ACTIVATION_REQUEST = 47; // identifies our request id
    private static final String PACKAGE = "package:";
    public static GridView gridListApp;
    public static LinearLayout layout_listapp;
    public static int LIMITROW = 10;
    public static ListView lstHistory;//row in call history
    public static CustomAdapterListView adapterListView = null;
    public static ArrayList<CallHistory> arrCall = new ArrayList<>();
    public static boolean flag_loading = false;
    public static List<AppsDetail> allItems;
    public static RelativeLayout rela_layout_center, rela_main_center;
    public static RotateLoading rotateLoading;
    public static LinearLayout linearMenu;
    public static TextView txtTitle;
    public static DialogPlus dialog = null;
    public static EditText editSearch;
    public static Toolbar toolbar;
    public static RelativeLayout rela_checkin, rela_main, rela_checkout;
    public static LinearLayout linearLogin, linearChangePass, linearListOrder;
    public static CustomAdapterGripView adapterGripView;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName dmsDeviceAdmin;
    private ViewPager viewPager;
    private PackageManager manager;
    private CoordinatorLayout coordinatorLayout;
    private ImageView imgCenter;
    private ViewGroup.LayoutParams defaultparams;
    private Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.w("onCreate", "onCreate");
        startService(new Intent(this, BackgroundService.class));
        Awake();
        MyMethod.changeColorStatusBar(getWindow());
        getId();
        init();
    }

    private void Awake() {//startup
        // CustomActivityOnCrash.install(this);// report crash
        LocationAlarmManager alarmManager = new LocationAlarmManager();
        alarmManager.SetAlarm(getApplicationContext());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {// event when lost focus and start animation so cool
        Animation animA;
        if (hasFocus) {
            animA = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.fade_in);
            coordinatorLayout.startAnimation(animA);
        } else {
            animA = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.fade_out);
            coordinatorLayout.startAnimation(animA);
        }
    }

    private void getId() {//get every id i had
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        imgCenter = (ImageView) findViewById(R.id.imgCenterSwipe);
        linearMenu = (LinearLayout) findViewById(R.id.linearMenu);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txtTitle = (TextView) findViewById(R.id.txtTile);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Call:
                MyMethod.callPhone(v.getContext());
                break;
            case R.id.btn_Menu:
                editSearch.setText("");
                editSearch.clearFocus();
                MyMethod.showApps(v.getContext(), viewPager);
                gridListApp.setOnItemLongClickListener(Home.this);
                gridListApp.setOnItemClickListener(this);
                editSearch.addTextChangedListener(this);
                break;
            case R.id.btn_SmS:
                MyMethod.showSms(v.getContext());
                break;
            case R.id.btn_Email:
                MyMethod.showGmail(v.getContext());
                break;
            case R.id.btn_Lock:
                MyMethod.lockDevice(devicePolicyManager);
                break;
            default:
                break;
        }
    }

    private void init() {// go to in this heart
        //Create viewpager with three fragment
        setupViewPager(viewPager);
        //Get permission ADMIN DEVICE
        getPermission();
        //set event main menu
        findViewById(R.id.btn_Call).setOnClickListener(this);
        findViewById(R.id.btn_SmS).setOnClickListener(this);
        findViewById(R.id.btn_Email).setOnClickListener(this);
        findViewById(R.id.btn_Menu).setOnClickListener(this);
        findViewById(R.id.btn_Lock).setOnClickListener(this);
    }

    private void setupViewPager(final ViewPager viewPager) {// setup view page fragment
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LeftFragment(), getString(R.string.calllog));
        adapter.addFragment(new CenterFragment(), getString(R.string.home));
        adapter.addFragment(new RightFragment(), getString(R.string.app_dms));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(0);
        defaultparams = viewPager.getLayoutParams();
        viewPager.addOnPageChangeListener(this);
        //Set current page is center fragment
        viewPager.setCurrentItem(1);
    }

    private void getPermission() {// open activity get permission ADMIN DEVICE
        manager = getPackageManager();
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        dmsDeviceAdmin = new ComponentName(this, DMSDeviceAdminReceiver.class);
        Intent intent = new Intent(
                DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                dmsDeviceAdmin);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                getString(R.string.permissionlabel));
        startActivityForResult(intent, ACTIVATION_REQUEST);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Uri packageUri = Uri.parse(PACKAGE + allItems.get(position).name);
        Intent uninstallIntent =
                new Intent(Intent.ACTION_DELETE, packageUri);
        startActivity(uninstallIntent);
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MyMethod.runApp(allItems.get(position), manager, view.getRootView().getContext());
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        timer.cancel();
        timer = new Timer();
        long DELAY = 500;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // you will probably need to use runOnUiThread(Runnable action) for some specific actions

            }

        }, DELAY);
        //Do that
        MyMethod.showApps(getApplicationContext(), viewPager, editSearch.getText().toString());

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                txtTitle.setText(getString(R.string.calllog));
                imgCenter.setImageResource(R.drawable.swipeleft);
                viewPager.setLayoutParams(defaultparams);
                linearMenu.setVisibility(View.VISIBLE);
                break;
            case 1:
                if (layout_listapp != null)
                    if (MyMethod.isVisible(layout_listapp))
                        txtTitle.setText(getString(R.string.list_app));
                    else txtTitle.setText(getString(R.string.home));
                imgCenter.setImageResource(R.drawable.swipecenter);
                viewPager.setLayoutParams(defaultparams);
                linearMenu.setVisibility(View.VISIBLE);
                break;
            case 2:
                txtTitle.setText(getString(R.string.app_dms));
                linearMenu.setVisibility(View.GONE);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                viewPager.setLayoutParams(params);
                imgCenter.setImageResource(R.drawable.swiperight);
                break;
            default:
                linearMenu.setVisibility(View.VISIBLE);
                imgCenter.setImageResource(R.drawable.swipecenter);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    //LIFE CYCLE
    @Override
    protected void onPause() {
        Log.w("onPause", "onPause");
//        //Set current page to center fragment
//        if (arrCall != null && adapterListView != null && arrCall.size() > 10) {
//            for (int i = 10; i < arrCall.size(); i++) arrCall.remove(i);
//            lstHistory.setAdapter(adapterListView);
//            adapterListView.notifyDataSetChanged();
//        }
//        viewPager.setCurrentItem(1);
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (!MyMethod.checkAdminActive(devicePolicyManager, dmsDeviceAdmin)) getPermission();
        Log.w("onResume", "onResume");
        super.onResume();
    }

    @Override
    protected void onRestart() {
        Log.w("onRestart", "onRestart");
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
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        Log.w("onStop", "onStop");
        Intent mStartService = new Intent(getApplicationContext(), BackgroundService.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getService(getApplicationContext(), mPendingIntentId, mStartService, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 10000, mPendingIntent);
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
                            getString(R.string.permissionlabel));
                    startActivityForResult(intent, ACTIVATION_REQUEST);
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        switch (viewPager.getCurrentItem()) {
            case 0:
                break;
            case 1:
                if (MyMethod.isVisible(layout_listapp)) {
                    MyMethod.setGone(layout_listapp);
                    MyMethod.setVisible(rela_layout_center);
                    txtTitle.setText(getString(R.string.home));
                }
                break;
            case 2:
                //if in check in
                if (MyMethod.isVisible(rela_checkin)) {
                    MyMethod.setGone(rela_checkin);
                    MyMethod.setVisible(rela_main);
                }
                //if in check out
                else if (MyMethod.isVisible(rela_checkout)) {
                    MyMethod.setGone(rela_checkout);
                    MyMethod.setVisible(rela_main);
                } else if (MyMethod.isVisible(linearChangePass)) {
                    MyMethod.setGone(linearChangePass);
                    MyMethod.setVisible(rela_main);

                } else if (MyMethod.isVisible(linearListOrder)) {
                    MyMethod.setGone(linearListOrder);
                    MyMethod.setVisible(rela_main);

                    if (dialog != null) dialog.dismiss();

                } else
                    viewPager.setCurrentItem(1);
                break;
            default:
                break;
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
                MyMethod.setGone(layout_listapp);
                MyMethod.setVisible(rela_layout_center);
                MyMethod.closeFocus(rela_layout_center);
                if (dialog != null) dialog.dismiss();
            }
        }
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


}