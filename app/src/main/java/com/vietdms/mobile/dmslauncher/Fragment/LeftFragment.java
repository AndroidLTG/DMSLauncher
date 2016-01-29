package com.vietdms.mobile.dmslauncher.Fragment;


import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vietdms.mobile.dmslauncher.CustomAdapter.CustomAdapterListView;
import com.vietdms.mobile.dmslauncher.GetSet.CallHistory;
import com.vietdms.mobile.dmslauncher.Home;
import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.R;

import java.util.ArrayList;
import java.util.Date;
//import jp.wasabeef.blurry.Blurry;

/**
 * Created by DMSv4 on 12/3/2015.
 */
public class LeftFragment extends Fragment {


    private Context context;

    public LeftFragment() {
    }

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_left, container, false);
        getId(v);
        loadLeft_();
        return v;
    }


    private void getId(View v) {
        context = getContext();
        Home.lstHistory = (ListView) v.findViewById(R.id.listHistory);
        Home.relativeLeft = (RelativeLayout) v.findViewById(R.id.rela_bg_left);

    }


    private void getCallDetails() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                null, null, CallLog.Calls.DATE + " DESC");
        int number = managedCursor != null ? managedCursor.getColumnIndex(CallLog.Calls.NUMBER) : 0;
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        while (managedCursor.moveToNext()) {
            if (Home.arrCall.size() < Home.LIMITROW) {// limit 10 row
                String phNumber = managedCursor.getString(number);
                String callType = managedCursor.getString(type);
                String callDate = managedCursor.getString(date);
                String phName = managedCursor.getString(name);
                Date callDayTime = new Date(Long.valueOf(callDate));
                String callDuration = managedCursor.getString(duration);
                int dir = 0;
                int dircode = Integer.parseInt(callType);
                switch (dircode) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        dir = 0;
                        break;
                    case CallLog.Calls.INCOMING_TYPE:
                        dir = 1;
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        dir = 2;
                        break;
                }
                CallHistory callHistory = new CallHistory();
                if (phName == null)
                    callHistory.setPhoneName(" ");
                else
                    callHistory.setPhoneName(phName);
                callHistory.setCallTime(MyMethod.getDateHmFromDate(callDayTime));
                callHistory.setCallDuration(callDuration);
                callHistory.setCallType(dir);
                callHistory.setPhoneNumber(phNumber);
                Home.arrCall.add(callHistory);
            }
        }
        managedCursor.close();
        Home.adapterListView.notifyDataSetChanged();
    }

    private void loadLeft_() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Home.relativeLeft.setBackground(MyMethod.getWallpaper(context));
        }
        Home.arrCall = new ArrayList<>();
        Home.adapterListView = new CustomAdapterListView(
                this.getActivity(),
                R.layout.list_call_history,// lấy custom layout
                Home.arrCall/*thiết lập data source*/);
        Home.lstHistory.setAdapter(Home.adapterListView);
        Home.lstHistory.setScrollingCacheEnabled(false);
        getCallDetails();
        Home.lstHistory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final CallHistory callHistory = (CallHistory) parent.getItemAtPosition(position);
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.view_listviewlonglick);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                final TextView txtViewContact, txtEditNumberBeforeCall, txtCopyCallNumber, txtRejectCall, txtDeleteCallLog, txtDeleteAllLogThisNumber, txtNameContact;
                txtViewContact = (TextView) dialog.findViewById(R.id.txtViewContact);
                txtNameContact = (TextView) dialog.findViewById(R.id.txtNameContact);
                txtEditNumberBeforeCall = (TextView) dialog.findViewById(R.id.txtEditNumberBeforeCall);
                txtCopyCallNumber = (TextView) dialog.findViewById(R.id.txtCopyCallNumber);
                txtRejectCall = (TextView) dialog.findViewById(R.id.txtRejectCall);
                txtDeleteCallLog = (TextView) dialog.findViewById(R.id.txtDeleteCallLog);
                txtDeleteAllLogThisNumber = (TextView) dialog.findViewById(R.id.txtDeleteAllCallLogThisNumber);
                // set the custom dialog components - text, image and button
                txtNameContact.setText(callHistory.getPhoneName() + "\n" + callHistory.getPhoneNumber());
                if (!MyMethod.contactExists(context, callHistory.getPhoneNumber()))
                    txtViewContact.setText(getString(R.string.add_to_contact));
                txtViewContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (txtViewContact.getText().toString().equals(getString(R.string.add_to_contact))) {
                            Intent intent = new Intent(Intent.ACTION_INSERT,
                                    ContactsContract.Contacts.CONTENT_URI);
                            intent.putExtra(ContactsContract.Intents.Insert.PHONE, callHistory.getPhoneNumber());
                            startActivity(intent);
                            dialog.dismiss();
                        } else {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(MyMethod.getContactIDFromNumber(callHistory.getPhoneNumber(), getContext())));
                            intent.setData(uri);
                            startActivity(intent);
                            dialog.dismiss();
                        }

                    }
                });
                txtEditNumberBeforeCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + callHistory.getPhoneNumber()));
                        startActivity(callIntent);
                        dialog.dismiss();
                    }
                });
                txtCopyCallNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            clip = ClipData.newPlainText(callHistory.getPhoneName(), callHistory.getPhoneNumber());
                            clipboard.setPrimaryClip(clip);

                        }
                        dialog.dismiss();

                    }
                });
                txtRejectCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                txtDeleteCallLog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyMethod.DeleteCallLogByNumber(callHistory.getPhoneNumber(), context);
                        Home.arrCall.remove(position);
                        getCallDetails();
                        Home.adapterListView.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                txtDeleteAllLogThisNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                dialog.show();
                return false;
            }
        });
        Home.lstHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                final CallHistory callHistory = (CallHistory) parent.getItemAtPosition(position);
                if (MyMethod.contactExists(context, callHistory.getPhoneNumber())) {
                    Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(MyMethod.getContactIDFromNumber(callHistory.getPhoneNumber(), getContext())));
                    intent.setData(uri);
                    startActivity(intent);
                }

            }
        });

        Home.lstHistory.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
//                    if (Home.flag_loading == false) {
//                        Home.flag_loading = true;
//                        Home.arrCall.clear();
//                        Home.adapterListView.clear();
//                       // Home.LIMITROW = Home.LIMITROW + 10;
//                        getCallDetails();
//
//                    }
                }
            }
        });
    }
}
