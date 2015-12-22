package com.vietdms.mobile.dmslauncher;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.orhanobut.dialogplus.DialogPlus;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;
import java.util.List;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

public class Home extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, RecyclerItemClickListener.OnItemClickListener {
    private DevicePolicyManager devicePolicyManager;
    private ComponentName dmsDeviceAdmin;
    private ViewPager viewPager;
    public static GridView gridListApp;
    private ViewPagerAdapter adapter;
    static final int ACTIVATION_REQUEST = 47; // identifies our request id
    public static int LIMITROW = 10;
    public static ListView lstHistory;//row in call history
    public static CustomAdapterListView adapterListView = null;
    public static ArrayList<CallHistory> arrCall = new ArrayList<>();
    public static boolean flag_loading = false;
    private PackageManager manager;
    private List<AppsDetail> allItems;
    public static RelativeLayout rela_layout_center, rela_main_center;
    public static RotateLoading rotateLoading;
    private CoordinatorLayout coordinatorLayout;
    public static LinearLayout linearMenu;
    private ImageView imgCenter;
    private ViewGroup.LayoutParams defaultparams;
    public static TextView txtTitle;
    public static DialogPlus dialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.w("onCreate", "onCreate");
        Awake();
        changeColorStatusBar();
        getId();
        init();
    }

    private void Awake() {
        CustomActivityOnCrash.install(this);
        LocationAlarmManager alarmManager = new LocationAlarmManager();
        startService(new Intent(this, BackgroundService.class));
        if(alarmManager != null){
            alarmManager.SetAlarm(getApplicationContext());
        }else{
            MyMethod.showToast(this,"Alarm is null");
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
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

    private void changeColorStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }
    }

    private void getId() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        imgCenter = (ImageView) findViewById(R.id.imgCenterSwipe);
        linearMenu = (LinearLayout) findViewById(R.id.linearMenu);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txtTitle = (TextView) findViewById(R.id.txtTile);
    }

    @Override
    public void onClick(View v) {
    }

    private void init() {
        //Create viewpager with three fragment
        setupViewPager(viewPager);
        //Get permission ADMIN DEVICE
        getPermission();
        //Load left call history
    }


    private void setupViewPager(final ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LeftFragment(), getString(R.string.calllog));
        adapter.addFragment(new CenterFragment(), getString(R.string.home));
        adapter.addFragment(new RightFragment(), getString(R.string.app_dms));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        defaultparams = viewPager.getLayoutParams();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
                        txtTitle.setText(getString(R.string.home));
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
        });
        //Set current page is center fragment
        viewPager.setCurrentItem(1);

    }


    private void getPermission() {
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
        Uri packageUri = Uri.parse("package:" + allItems.get(position).getName());
        Intent uninstallIntent =
                new Intent(Intent.ACTION_DELETE, packageUri);
        startActivity(uninstallIntent);
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = manager.getLaunchIntentForPackage(allItems.get(position).name.toString());
        startActivity(i);
    }

    @Override
    public void onItemClick(View view, int position) {

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
        rotateLoading.start();
        gridListApp.setOnItemLongClickListener(this);
        allItems = loadApps();
        CustomAdapterGripView adapterGripView = new CustomAdapterGripView(Home.this, allItems);
        gridListApp.setAdapter(adapterGripView);
        gridListApp.setOnItemClickListener(this);
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
        rotateLoading.stop();
        rela_layout_center.setVisibility(View.GONE);
        gridListApp.setVisibility(View.VISIBLE);
        if (viewPager.getCurrentItem() != 1) viewPager.setCurrentItem(1);
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
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("vnd.android-dir/mms-sms");
        startActivity(intent);
    }

    public void showGmail(View v) {//show mail app
        Intent i = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        switch (viewPager.getCurrentItem()) {
            case 0:
                break;
            case 1:
                if (adapter.getItem(1).getView().findViewById(R.id.gridListApp).getVisibility() == View.VISIBLE) {
                    adapter.getItem(1).getView().findViewById(R.id.gridListApp).setVisibility(View.GONE);
                    adapter.getItem(1).getView().findViewById(R.id.rela_layout_center).setVisibility(View.VISIBLE);

                }
                break;
            case 2:
                //if in check in
                if (adapter.getItem(2).getView().findViewById(R.id.rela_layout_checkin).getVisibility() == View.VISIBLE) {
                    adapter.getItem(2).getView().findViewById(R.id.rela_layout_checkin).setVisibility(View.GONE);
                    adapter.getItem(2).getView().findViewById(R.id.rela_layout_main).setVisibility(View.VISIBLE);
                }
                //if in check out
                else if (adapter.getItem(2).getView().findViewById(R.id.rela_layout_checkout).getVisibility() == View.VISIBLE) {
                    adapter.getItem(2).getView().findViewById(R.id.rela_layout_checkout).setVisibility(View.GONE);
                    adapter.getItem(2).getView().findViewById(R.id.rela_layout_main).setVisibility(View.VISIBLE);
                } else if (adapter.getItem(2).getView().findViewById(R.id.linear_change_pass).getVisibility() == View.VISIBLE) {
                    adapter.getItem(2).getView().findViewById(R.id.linear_change_pass).setVisibility(View.GONE);
                    adapter.getItem(2).getView().findViewById(R.id.rela_layout_main).setVisibility(View.VISIBLE);

                } else if (adapter.getItem(2).getView().findViewById(R.id.linear_list_order).getVisibility() == View.VISIBLE) {
                    adapter.getItem(2).getView().findViewById(R.id.linear_list_order).setVisibility(View.GONE);
                    adapter.getItem(2).getView().findViewById(R.id.rela_layout_main).setVisibility(View.VISIBLE);
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
                adapter.getItem(1).getView().findViewById(R.id.gridListApp).setVisibility(View.GONE);
                adapter.getItem(1).getView().findViewById(R.id.rela_layout_center).setVisibility(View.VISIBLE);
                if (dialog != null) dialog.dismiss();
                Log.d("whatever", "Home pressed");
            }
        }
    }


}