package com.vietdms.mobile.dmslauncher;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.vietdms.mobile.dmslauncher.CustomAdapter.CustomAdapterGripView;
import com.vietdms.mobile.dmslauncher.GetSet.AppsDetail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by ${LTG} on ${10/12/1994}.
 */
public class MyMethod {
    public static final String PASSWORD = "LTG";
    public static final String LOG_EDMS = "eDMS LifeCycle";
    public static final String SHAREDPREFERENCE_KEY = "CheckLogin_Value";
    public static final String SHAREDPREFERENCE_User = "UserName_Value";
    public static final String SHAREDPREFERENCE_Pass = "PassWord_Value";

    public static void showToast(Context context, String toast) {// show toast so cool
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }

    public static void requestFocus(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static void closeFocus(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void savePreferences(Context context, String key, boolean value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void savePreferences(Context context, String key, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    //CALL LOG
    public static int getContactIDFromNumber(String contactNumber, Context context) {
        contactNumber = Uri.encode(contactNumber);
        int phoneContactID = new Random().nextInt();
        Cursor contactLookupCursor = context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, contactNumber), new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
        while (contactLookupCursor != null && contactLookupCursor.moveToNext()) {
            phoneContactID = contactLookupCursor.getInt(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
        }
        if (contactLookupCursor != null) {
            contactLookupCursor.close();
        }

        return phoneContactID;
    }

    public static String getDateHmFromDate(Date date) {
        String dateF;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (calendar.get(Calendar.HOUR_OF_DAY) < 10 && calendar.get(Calendar.MINUTE) < 10)
            dateF = "0" + calendar.get(Calendar.HOUR_OF_DAY) + ":0" + calendar.get(Calendar.MINUTE);
        else if (calendar.get(Calendar.HOUR_OF_DAY) < 10)
            dateF = "0" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
        else if (calendar.get(Calendar.MINUTE) < 10)
            dateF = calendar.get(Calendar.HOUR_OF_DAY) + ":0" + calendar.get(Calendar.MINUTE);
        else dateF = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
        return dateF;
    }

    public static void DeleteCallLogByNumber(String number, Context context) {
        String queryString = "NUMBER=" + number;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, queryString, null);
    }

    public static boolean contactExists(Context context, String number) {
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
        try {
            if (cur != null && cur.moveToFirst()) {
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;
    }

    public static boolean isVisible(View v) {
        return (v.getVisibility() == View.VISIBLE);
    }

    public static void setVisible(View v) {
        v.setVisibility(View.VISIBLE);
    }

    public static void setGone(View v) {
        v.setVisibility(View.GONE);
    }

    public static void changeColorStatusBar(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }
    }


    public static void runApp(final AppsDetail app, final PackageManager manager, final Context context) {
        switch (app.role) {
            case 1:
                Intent i = manager.getLaunchIntentForPackage(app.name.toString());
                context.startActivity(i);
                break;
            case 2:
                final AlertDialog.Builder builder =
                        new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View view = inflater.inflate(R.layout.dialog_enter_pass, null);
                builder.setView(view);
                builder.setTitle(context.getString(R.string.enter_password));
                builder.setCancelable(false);
                builder.setPositiveButton(context.getString(R.string.accept), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //check password if ok
                        if (((EditText) view.findViewById(R.id.edit_dialog_pass)).getText().toString().toLowerCase().equals(PASSWORD.toLowerCase())) {
                            Intent i = manager.getLaunchIntentForPackage(app.name.toString());
                            context.startActivity(i);
                        } else
                            ((TextInputLayout) view.findViewById(R.id.input_dialog_layout_password)).setError(context.getString(R.string.err_msg_password));
                    }
                });//second parameter used for onclicklistener
                builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                break;
            default:
                break;
        }

    }


    public static boolean checkAdminActive(DevicePolicyManager devicePolicyManager, ComponentName componentName) {
        return devicePolicyManager.isAdminActive(componentName);
    }

    //MAIN MENU
    public static void callPhone(Context context) {// Show call app
        Intent i = context.getPackageManager().getLaunchIntentForPackage(context.getString(R.string.phone_location));
        context.startActivity(i);
    }

    public static void showSms(Context context) {// Show sms app
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType(context.getString(R.string.sms_location));
        context.startActivity(intent);
    }

    public static void lockDevice(DevicePolicyManager devicePolicyManager) {// Lock screen
        devicePolicyManager.lockNow();
    }


    public static void showGmail(Context context) {//show mail app
        Intent i = context.getPackageManager().getLaunchIntentForPackage(context.getString(R.string.gmail_location));
        context.startActivity(i);
    }

    public static List<AppsDetail> loadApps(Context context, ViewPager viewPager) {// Load all app in this phone

        PackageManager manager = context.getPackageManager();
        List<AppsDetail> apps = new ArrayList<>();
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
        for (ResolveInfo ri : availableActivities) {
            AppsDetail app = new AppsDetail();
            app.label = ri.loadLabel(manager);
            app.name = ri.activityInfo.packageName;
            app.icon = ri.activityInfo.loadIcon(manager);
            app.role = 2;
            apps.add(app);
        }
        Home.rotateLoading.stop();
        Home.rela_layout_center.setVisibility(View.GONE);
        Home.layout_listapp.setVisibility(View.VISIBLE);
        if (viewPager.getCurrentItem() != 1) viewPager.setCurrentItem(1);
        return apps;
    }

    public static List<AppsDetail> loadApps(Context context, ViewPager viewPager, String s) {//load app from this phone by a part of app name

        PackageManager manager = context.getPackageManager();
        List<AppsDetail> apps = new ArrayList<>();
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
        for (ResolveInfo ri : availableActivities) {
            AppsDetail app = new AppsDetail();
            app.label = ri.loadLabel(manager);
            app.name = ri.activityInfo.packageName;
            app.icon = ri.activityInfo.loadIcon(manager);
            app.role = 2;
            if (app.label.toString().toLowerCase().contains(s.toLowerCase()))
                apps.add(app);
        }
        Home.rotateLoading.stop();
        Home.rela_layout_center.setVisibility(View.GONE);
        Home.layout_listapp.setVisibility(View.VISIBLE);
        if (viewPager.getCurrentItem() != 1) viewPager.setCurrentItem(1);
        return apps;
    }

    public static void showApps(Context context, ViewPager viewPager, String s) {//show list app by a part of name
        Home.allItems = MyMethod.loadApps(context, viewPager, s);
        Home.adapterGripView = new CustomAdapterGripView(context, Home.allItems);
        Home.gridListApp.setAdapter(Home.adapterGripView);
        Home.adapterGripView.notifyDataSetChanged();
    }


    public static void showApps(Context context, ViewPager viewPager) {//Show list app in device
        Home.txtTitle.setText(context.getString(R.string.list_app));
        Home.rotateLoading.start();
        Home.allItems = MyMethod.loadApps(context, viewPager);
        Home.adapterGripView = new CustomAdapterGripView(context, Home.allItems);
        Home.gridListApp.setAdapter(Home.adapterGripView);
    }

}
