package com.example.dmsv4.dmslauncher.CustomAdapter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dmsv4.dmslauncher.GetSet.CallHistory;
import com.example.dmsv4.dmslauncher.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by DMSv4 on 12/7/2015.
 */
public class CustomAdapterListView extends ArrayAdapter<CallHistory> {
    Activity context = null;
    ArrayList<CallHistory> myArray = null;
    int layoutId;

    public CustomAdapterListView(Activity context, int layoutId, ArrayList<CallHistory> arr) {
        super(context, layoutId, arr);
        this.context = context;
        this.layoutId = layoutId;
        this.myArray = arr;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =
                context.getLayoutInflater();
        convertView = inflater.inflate(layoutId, null);
        final TextView txtName = (TextView)
                convertView.findViewById(R.id.txtName);
        final TextView txtInfo = (TextView)
                convertView.findViewById(R.id.txtInfo);
        final CallHistory emp = myArray.get(position);
        txtName.setText(emp.getPhoneName() + " \n" + emp.getPhoneNumber());
        txtInfo.setText(emp.getCallType() + " \nThời gian " + emp.getCallTime() + " \nĐộ dài " + emp.getCallDuration());
        final ImageView imgPhoto = (ImageView) convertView.findViewById(R.id.imgProfilePhoto);
        imgPhoto.setImageBitmap(retrieveContactPhoto(context, emp.getPhoneNumber()));
        final ImageView imgCall = (ImageView) convertView.findViewById(R.id.imgOptionCallMail);
        final ImageView imgSms = (ImageView) convertView.findViewById(R.id.imgOptionSmsMail);
        imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + emp.getPhoneNumber()));
                getContext().startActivity(callIntent);
            }
        });
        imgSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", emp.getPhoneNumber());
                smsIntent.putExtra("sms_body", "");
                getContext().startActivity(smsIntent);

            }
        });

        return convertView;
    }

    public static Bitmap retrieveContactPhoto(Context context, String number) {
        ContentResolver contentResolver = context.getContentResolver();
        String contactId = null;
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};
        Cursor cursor =
                contentResolver.query(
                        uri,
                        projection,
                        null,
                        null,
                        null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            }
            cursor.close();
        }
        Bitmap photo = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.noavatar_btn);
        if (contactId != null)
            try {
                InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(contactId)));
                if (inputStream != null) {
                    photo = BitmapFactory.decodeStream(inputStream);
                }
                assert inputStream != null;
                inputStream.close();

            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        return photo;
    }

}
