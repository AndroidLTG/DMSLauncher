package com.vietdms.mobile.dmslauncher;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.vietdms.mobile.dmslauncher.GetSet.AppsDetail;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by ${LTG} on ${10/12/1994}.
 */
public class MyMethod {
    public static final String LOG_EDMS = "eDMS LifeCycle";
    public static final String SHAREDPREFERENCE_KEY = "CheckLogin_Value";
    public static final String SHAREDPREFERENCE_User = "UserName_Value";
    public static final String SHAREDPREFERENCE_Pass = "PassWord_Value";

    public static void showToast(Context context, String toast) {
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
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                builder.setView(R.layout.dialog_enter_pass);
                builder.setTitle(context.getString(R.string.enter_password));
                builder.setPositiveButton(context.getString(R.string.accept), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //check password if ok
                        Intent i = manager.getLaunchIntentForPackage(app.name.toString());
                        context.startActivity(i);
                    }
                });//second parameter used for onclicklistener
                builder.setNegativeButton(context.getString(R.string.cancel), null);
                builder.show();
                break;
            default:
                break;
        }

    }

    public static void showDialog(Context context) {

    }

    public static boolean checkAdminActive(DevicePolicyManager devicePolicyManager, ComponentName componentName) {
        return devicePolicyManager.isAdminActive(componentName);
    }

    //MAIN MENU
    public static void callPhone(Context context) {// Show call app
        Intent i = context.getPackageManager().getLaunchIntentForPackage("com.android.contacts");
        context.startActivity(i);
    }

    public static void showSms(Context context) {// Show sms app
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("vnd.android-dir/mms-sms");
        context.startActivity(intent);
    }

    public static void lockDevice(DevicePolicyManager devicePolicyManager) {// Lock screen
        devicePolicyManager.lockNow();
    }


    public static void showGmail(Context context) {//show mail app
        Intent i = context.getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
        context.startActivity(i);
    }

}
