package com.example.dmsv4.dmslauncher.Fragment;


import android.Manifest;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dmsv4.dmslauncher.CallHistory;
import com.example.dmsv4.dmslauncher.CustomAdapterListView;
import com.example.dmsv4.dmslauncher.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * Created by DMSv4 on 12/3/2015.
 */
public class LeftFragment extends Fragment {
    private ListView lstHistory;
    private ArrayList<CallHistory> arrCall = new ArrayList<CallHistory>();
    CustomAdapterListView adapterListView = null;

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
        control_();


        return v;
    }

    @Override
    public void onPause() {
        Log.w("OnPause", "OnPause");

        super.onPause();
    }

    private void control_() {
        arrCall = new ArrayList<>();
        adapterListView = new CustomAdapterListView(
                getActivity(),
                R.layout.list_call_history,// lấy custom layout
                arrCall/*thiết lập data source*/);
        lstHistory.setAdapter(adapterListView);
        getCallDetails();
        lstHistory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final CallHistory callHistory = (CallHistory) parent.getItemAtPosition(position);
                final Dialog dialog = new Dialog(getContext());
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

                if (!contactExists(getContext(), callHistory.getPhoneNumber()))
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
                            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(getContactIDFromNumber(callHistory.getPhoneNumber(), getContext())));
                            intent.setData(uri);
                            getContext().startActivity(intent);
                            dialog.dismiss();
                        }

                    }
                });
                txtEditNumberBeforeCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + callHistory.getPhoneNumber()));
                        getContext().startActivity(callIntent);
                        dialog.dismiss();
                    }
                });
                txtCopyCallNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
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
                        DeleteCallLogByNumber(callHistory.getPhoneNumber());
                        arrCall.remove(position);
                        getCallDetails();
                        adapterListView.notifyDataSetChanged();
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
        lstHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                final CallHistory callHistory = (CallHistory) parent.getItemAtPosition(position);
                Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(getContactIDFromNumber(callHistory.getPhoneNumber(), getContext())));
                intent.setData(uri);
                getContext().startActivity(intent);
            }
        });
    }

    private void getId(View v) {
        lstHistory = (ListView) v.findViewById(R.id.listHistory);
    }

    private void getCallDetails() {

        Cursor managedCursor = getContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                null, null, CallLog.Calls.DATE + " DESC");
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            String phName = managedCursor.getString(name);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = managedCursor.getString(duration);
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "Cuộc gọi đi";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    dir = "Cuộc gọi đến";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "Cuộc gọi nhỡ";
                    break;
            }
            CallHistory callHistory = new CallHistory();
            if (phName == null)
                callHistory.setPhoneName(" ");
            else
                callHistory.setPhoneName(phName);
            callHistory.setCallDuration(callDuration);
            callHistory.setCallTime(callDayTime + "");
            callHistory.setCallType(dir);
            callHistory.setPhoneNumber(phNumber);
            arrCall.add(callHistory);
        }
        managedCursor.close();
        adapterListView.notifyDataSetChanged();

    }

    public boolean contactExists(Context context, String number) {
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

    public void DeleteCallLogByNumber(String number) {
        String queryString = "NUMBER=" + number;
        getContext().getContentResolver().delete(CallLog.Calls.CONTENT_URI, queryString, null);
    }

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
}
