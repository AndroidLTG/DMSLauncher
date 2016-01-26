package com.vietdms.mobile.dmslauncher;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
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
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vietdms.mobile.dmslauncher.CustomAdapter.CustomAdapterGripView;
import com.vietdms.mobile.dmslauncher.GetSet.AppsDetail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by ${LTG} was born ${10/12/1994}.
 */
public class MyMethod {
    public static final String PASSWORD = "LTG";
    public static final String LOG_EDMS = "eDMS LifeCycle";
    public static final String SHAREDPREFERENCE_KEY = "CheckLogin_Value";
    public static final String SHAREDPREFERENCE_User = "UserName_Value";
    public static final String SHAREDPREFERENCE_Pass = "PassWord_Value";
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";

    public static boolean CHECKIN = false;//true is open rela_checkin false is open rela_checkout
    public static boolean ORDER = false;// true is open order false is open customer

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
            if (imm.isAcceptingText())
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
        try{
        Intent i = context.getPackageManager().getLaunchIntentForPackage(context.getString(R.string.phone_location));
        context.startActivity(i);
        } catch (Exception e) {
            Log.d(context.getString(R.string.tagEx), e.toString());
        }
    }

    public static void showSms(Context context) {// Show sms app
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setType(context.getString(R.string.sms_location));
            context.startActivity(intent);
        } catch (Exception e) {
            Log.d(context.getString(R.string.tagEx), e.toString());
        }
    }

    public static void lockDevice(Context context, DevicePolicyManager devicePolicyManager) {// Lock screen
        try {
            devicePolicyManager.lockNow();
        } catch (Exception e) {
            Log.d(context.getString(R.string.tagEx), e.toString());
        }
    }


    public static void showGmail(Context context) {//show mail app
        try {
            Intent i = context.getPackageManager().getLaunchIntentForPackage(context.getString(R.string.gmail_location));
            context.startActivity(i);
        } catch (Exception e) {
            Log.d(context.getString(R.string.tagEx), e.toString());
        }
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

    public static HashMap<String, String> getListApp(Context context) {
            PackageManager manager = context.getPackageManager();
            HashMap<String, String> listApp = new HashMap<>();
            Intent i = new Intent(Intent.ACTION_MAIN, null);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
            for (ResolveInfo ri : availableActivities)
                listApp.put(ri.activityInfo.name, ri.activityInfo.packageName);
            return listApp;
    }

    public static void showApps(Context context, ViewPager viewPager, String s) {//show list app by a part of name
        try {
            Home.allItems = MyMethod.loadApps(context, viewPager, s);
            Home.adapterGripView = new CustomAdapterGripView(context, Home.allItems);
            Home.gridListApp.setAdapter(Home.adapterGripView);
            Home.adapterGripView.notifyDataSetChanged();
        } catch (Exception e) {
            Log.d(context.getString(R.string.tagEx), e.toString());
        }
    }


    public static void showApps(Context context, ViewPager viewPager) {//Show list app in device
        try {
            Home.txtTitle.setText(context.getString(R.string.list_app));
            Home.rotateLoading.start();
            Home.allItems = MyMethod.loadApps(context, viewPager);
            Home.adapterGripView = new CustomAdapterGripView(context, Home.allItems);
            Home.gridListApp.setAdapter(Home.adapterGripView);
        } catch (Exception e) {
            Log.d(context.getString(R.string.tagEx), e.toString());
        }
    }


    public static String getAddress(Location lastLocation, Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lastLocation.getLatitude(), lastLocation.getLongitude(), 1);
            String add = "";
            Address obj = addresses.get(0);
            for (int i = 0; i < obj.getMaxAddressLineIndex(); i++)
                add += obj.getAddressLine(i) + " ";
            return add;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }

    }

    public static void addMarker(GoogleMap googleMap, LatLng latLng, String title, String snippet, final Context context) {
        try {
            googleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(title)
                            .snippet(snippet)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.markerto_btn))
            ).showInfoWindow();
            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    LinearLayout info = new LinearLayout(context);
                    info.setOrientation(LinearLayout.VERTICAL);

                    TextView title = new TextView(context);
                    title.setTextColor(Color.BLUE);
                    title.setGravity(Gravity.CENTER);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setText(marker.getTitle());

                    TextView snippet = new TextView(context);
                    snippet.setTextColor(Color.GRAY);
                    snippet.setText(marker.getSnippet());
                    info.addView(title);
                    info.addView(snippet);

                    return info;
                }
            });
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
            googleMap.animateCamera(cameraUpdate);
        } catch (Exception e) {
            Log.d(context.getString(R.string.tagEx), e.toString());
        }
    }

    public static void loadMap(GoogleMap googleMap, Location location, final Context context) {
        try {
            googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(location.getLatitude(), location.getLongitude())
                            )
                            .title(context.getString(R.string.location_here))
                            .snippet(MyMethod.getAddress(location, context))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.markerto_btn))
            ).showInfoWindow();
            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    LinearLayout info = new LinearLayout(context);
                    info.setOrientation(LinearLayout.VERTICAL);

                    TextView title = new TextView(context);
                    title.setTextColor(Color.BLUE);
                    title.setGravity(Gravity.CENTER);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setText(marker.getTitle());

                    TextView snippet = new TextView(context);
                    snippet.setTextColor(Color.GRAY);
                    snippet.setText(marker.getSnippet());
                    info.addView(title);
                    info.addView(snippet);

                    return info;
                }
            });
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16);
            googleMap.animateCamera(cameraUpdate);
        } catch (Exception e) {
            Log.d(context.getString(R.string.tagEx), e.toString());
        }
    }


    public static void refreshMap(Context context, GoogleMap googleMap) {
        try {
            googleMap.clear();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(15.8669512, 101.299562), 10);
            googleMap.animateCamera(cameraUpdate);
        } catch (Exception e) {
            Log.d(context.getString(R.string.tagEx), e.toString());
        }
    }

    public static boolean checkInputSaveSend(TextView textView, ImageView imageView, Context context) {
        if (textView.getText().toString().equals(context.getString(R.string.location_none))) {
            showToast(context, context.getString(R.string.notify_location));
            return false;
        } else if (imageView.getDrawable().getBounds().equals(ContextCompat.getDrawable(context, R.mipmap.ic_launcher).getBounds())) {
            showToast(context, context.getString(R.string.notify_take_photo));
            return false;
        } else
            return true;
    }

    public static void sendNotification(Context context, String message) {
        try {
            Intent intent = new Intent(context, Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.notice_btn)
                    .setContentTitle("Thông báo công ty")
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        } catch (Exception e) {
            Log.d(context.getString(R.string.tagEx), e.toString());
        }
    }

    public static Drawable getWallpaper(Context context) {
        try {
            final WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
            return wallpaperManager.getDrawable();
        } catch (Exception e) {
            Log.d(context.getString(R.string.tagEx), e.toString());
            return ContextCompat.getDrawable(context, R.drawable.background);
        }
    }

    public static void showChangeWallpaper(Context context) {
        try {
            context.startActivity(new Intent("android.intent.action.SET_WALLPAPER"));
        } catch (Exception e) {
            Log.d(context.getString(R.string.tagEx), e.toString());
        }
    }
}
