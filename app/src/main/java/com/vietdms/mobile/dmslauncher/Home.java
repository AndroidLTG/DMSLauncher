package com.vietdms.mobile.dmslauncher;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.orhanobut.dialogplus.DialogPlus;
import com.victor.loading.rotate.RotateLoading;
import com.vietdms.mobile.dmslauncher.CustomAdapter.CustomAdapterGripView;
import com.vietdms.mobile.dmslauncher.CustomAdapter.CustomAdapterListView;
import com.vietdms.mobile.dmslauncher.Fragment.CenterFragment;
import com.vietdms.mobile.dmslauncher.Fragment.LeftFragment;
import com.vietdms.mobile.dmslauncher.Fragment.RightFragment;
import com.vietdms.mobile.dmslauncher.GCM.RegistrationIntentService;
import com.vietdms.mobile.dmslauncher.GetSet.AppsDetail;
import com.vietdms.mobile.dmslauncher.GetSet.CallHistory;
import com.vietdms.mobile.dmslauncher.Receiver.DMSDeviceAdminReceiver;
import com.vietdms.mobile.dmslauncher.RecycleView.RecyclerItemClickListener;
import com.vietdms.mobile.dmslauncher.Service.BackgroundService;

import java.util.ArrayList;
import java.util.List;

import CommonLib.EventPool;
import CommonLib.EventType;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import jp.wasabeef.blurry.Blurry;

public class Home extends AppCompatActivity implements ViewPager.OnPageChangeListener, RecyclerItemClickListener.OnItemClickListener {
    //GCM
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG_GCM = "GCM_LOG";
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    //---
    static final int ACTIVATION_REQUEST = 47; // identifies our request id
    public static GridView gridListApp;
    public static LinearLayout layout_listapp;
    public static int LIMITROW = 10; //max line call log
    public static ListView lstHistory;//row in call history
    public static CustomAdapterListView adapterListView = null;
    public static ArrayList<CallHistory> arrCall = new ArrayList<>();
    public static List<AppsDetail> allItems;
    public static RelativeLayout rela_layout_center, rela_main_center;
    public static RotateLoading rotateLoading;
    public static LinearLayout linearMenu;
    public static TextView txtTitle;
    public static DialogPlus dialogOrder, dialogCustomer;
    public static EditText editSearch;
    public static Toolbar toolbar;
    public static RelativeLayout rela_checkin, rela_main, rela_checkout;
    public static LinearLayout linearLogin, linearChangePass, linearListOrder, linearCustomer;
    public static CoordinatorLayout mapView;
    public static CustomAdapterGripView adapterGripView;
    public static TextView txtAddressIn;
    public static TextView txtAddressOut;
    public static DevicePolicyManager devicePolicyManager;
    private ComponentName dmsDeviceAdmin;
    public static ViewPager viewPager;
    public static PackageManager manager;
    private CoordinatorLayout coordinatorLayout;
    public static TextView mInformationTextView;
    public static RelativeLayout relaMain;
    public static RelativeLayout relativeRight, relativeLeft, relativeCheckIn, relativeCheckOut;
    private Context context;


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
//        CustomActivityOnCrash.install(this);// report crash
//        CustomActivityOnCrash.setErrorActivityClass(CustomErrorActivity.class);
        EventPool.control().enQueue(new EventType.EventListApp(MyMethod.getListApp(getApplicationContext())));


        //GCM
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(MyMethod.SENT_TOKEN_TO_SERVER, false);
//                if (sentToken) {
//                    mInformationTextView.setText(getString(R.string.gcm_send_message));
//                } else {
//                    mInformationTextView.setText(getString(R.string.token_error_message));
//                }
            }
        };

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
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
        context = getApplicationContext();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        linearMenu = (LinearLayout) findViewById(R.id.linearMenu);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txtTitle = (TextView) findViewById(R.id.txtTile);
    }


    private void init() {// go to in this heart
        //Create viewpager with three fragment
        setupViewPager(viewPager);
        //Get permission ADMIN DEVICE
        getPermission();
        //set event main menu

    }

    private void setupViewPager(final ViewPager viewPager) {// setup view page fragment
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LeftFragment(), getString(R.string.calllog));
        adapter.addFragment(new CenterFragment(), getString(R.string.home));
        adapter.addFragment(new RightFragment(), getString(R.string.app_dms));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
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
    public void onItemClick(View view, int position) {

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        MyMethod.closeFocus(getCurrentFocus());
    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                txtTitle.setText(getString(R.string.calllog));

                if (!MyMethod.blurredLeft) {
                    MyMethod.blur(context, Home.relativeLeft);
                    MyMethod.blurredLeft = !MyMethod.blurredLeft;
                }

                break;
            case 1:

                if (layout_listapp != null)
                    if (MyMethod.isVisible(layout_listapp)) {
                        txtTitle.setText(getString(R.string.list_app));
                    } else txtTitle.setText(getString(R.string.home));
                break;
            case 2:
                txtTitle.setText(getString(R.string.app_dms));
                if (!MyMethod.blurredRight) {
                    MyMethod.blur(context, Home.relativeRight);

                    MyMethod.blurredRight = !MyMethod.blurredRight;
                }
                break;
            default:
                linearMenu.setVisibility(View.VISIBLE);
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG_GCM, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Home.rela_checkin.setBackground(MyMethod.getWallpaper(context));
                Home.rela_checkout.setBackground(MyMethod.getWallpaper(context));
                Home.relativeRight.setBackground(MyMethod.getWallpaper(context));
                Home.relaMain.setBackground(MyMethod.getWallpaper(context));
                Home.relativeLeft.setBackground(MyMethod.getWallpaper(context));
            }
        } catch (Exception e) {
            Log.d(context.getString(R.string.tagEx), e.toString());
        }
        if (!MyMethod.checkAdminActive(devicePolicyManager, dmsDeviceAdmin)) getPermission();
        Log.w("onResume", "onResume");
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(MyMethod.REGISTRATION_COMPLETE));
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
                viewPager.setCurrentItem(1);
                break;
            case 1:
                if (MyMethod.isVisible(layout_listapp)) {
                    if (MyMethod.blurredMenu) {
                        Blurry.delete((ViewGroup) Home.relaMain);
                        MyMethod.blurredMenu = !MyMethod.blurredMenu;
                    }
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
                    if (dialogOrder != null && dialogOrder.isShowing()) dialogOrder.dismiss();
                    else {
                        MyMethod.setGone(linearListOrder);
                        MyMethod.setVisible(rela_main);
                    }
                } else if (MyMethod.isVisible(linearCustomer)) {
                    if (dialogCustomer != null && dialogCustomer.isShowing())
                        dialogCustomer.dismiss();
                    else {
                        MyMethod.setGone(linearCustomer);
                        MyMethod.setVisible(rela_main);
                    }
                } else if (MyMethod.isVisible(mapView)) {
                    MyMethod.setGone(mapView);
                    if (MyMethod.CHECKIN) {
                        MyMethod.setVisible(rela_checkin);
                        txtAddressIn.setText(getApplicationContext().getString(R.string.location_none));
                    } else {
                        MyMethod.setVisible(rela_checkout);
                        txtAddressOut.setText(getApplicationContext().getString(R.string.location_none));
                    }
                } else {
                    viewPager.setCurrentItem(1);

                }

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
                if (MyMethod.blurredMenu) {
                    Blurry.delete(Home.relaMain);
                    MyMethod.blurredMenu = !MyMethod.blurredMenu;
                }
                MyMethod.setGone(layout_listapp);
                MyMethod.setVisible(rela_layout_center);
                MyMethod.closeFocus(rela_layout_center);
                if (dialogOrder != null) dialogOrder.dismiss();
                if (dialogCustomer != null) dialogCustomer.dismiss();
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