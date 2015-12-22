package com.vietdms.mobile.dmslauncher;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by DMSv4 on 12/19/2015.
 */
public class MyMethod {
    public static final String LOG_EDMS = "eDMS LifeCycle";
    public static final String SHAREDPREFERENCE_KEY = "CheckLogin_Value";
    public static final String SHAREDPREFERENCE_User = "UserName_Value";
    public static final String SHAREDPREFERENCE_Pass = "PassWord_Value";
    public static Location location = null;

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
        editor.commit();
    }

    public static void savePreferences(Context context, String key, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
    //CALL LOG
    public static int getContactIDFromNumber(String contactNumber, Context context) {
        contactNumber = Uri.encode(contactNumber);
        int phoneContactID = new Random().nextInt();
        Cursor contactLookupCursor = context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, contactNumber), new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
        while (contactLookupCursor.moveToNext()) {
            phoneContactID = contactLookupCursor.getInt(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
        }
        contactLookupCursor.close();

        return phoneContactID;
    }

    public static String getDateHmFromDate(Date date) {
        String dateF = "";
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
            if (cur.moveToFirst()) {
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;
    }

}
