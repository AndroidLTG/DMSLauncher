package com.vietdms.mobile.dmslauncher.CustomAdapter;

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

import com.vietdms.mobile.dmslauncher.GetSet.CallHistory;
import com.vietdms.mobile.dmslauncher.R;

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
        final CallHistory emp = myArray.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater inflater =
                    context.getLayoutInflater();
            convertView = inflater.inflate(layoutId, null);
            holder = new ViewHolder();
            holder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            holder.txtInfo = (TextView) convertView.findViewById(R.id.txtInfo);
            holder.imgPhoto = (ImageView) convertView.findViewById(R.id.imgProfilePhoto);
            holder.imgCall = (ImageView) convertView.findViewById(R.id.imgOptionCallMail);
            holder.imgSms = (ImageView) convertView.findViewById(R.id.imgOptionSmsMail);
            holder.imgCallType = (ImageView) convertView.findViewById(R.id.imgCallType);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        switch (emp.getCallType()) {
            case 0:
                holder.imgCallType.setImageResource(R.drawable.from_btn);
                break;
            case 1:
                holder.imgCallType.setImageResource(R.drawable.to_btn);
                break;
            case 2:
                holder.imgCallType.setImageResource(R.drawable.exclam_btn);
                break;
            default:
                holder.imgCallType.setImageResource(R.drawable.from_btn);
                break;

        }
        holder.txtName.setText(emp.getPhoneName() + context.getString(R.string.downline) + emp.getPhoneNumber());
        holder.txtInfo.setText(emp.getCallTime() + context.getString(R.string.spaceline) + retrieveDurationCall(emp.getCallDuration()));
        holder.imgPhoto.setImageBitmap(retrieveContactPhoto(context, emp.getPhoneNumber()));
        holder.imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + emp.getPhoneNumber()));
                getContext().startActivity(callIntent);
            }
        });
        holder.imgSms.setOnClickListener(new View.OnClickListener() {
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

    static class ViewHolder {
        TextView txtName;
        TextView txtInfo;
        ImageView imgPhoto;
        ImageView imgCall;
        ImageView imgSms;
        ImageView imgCallType;
    }

    private String retrieveDurationCall(String duration) {
        String retrieve = "";
        int durationF = Integer.parseInt(duration);
        int hours = durationF / 3600;
        int minutes = (durationF % 3600) / 60;
        int seconds = durationF % 60;
        retrieve = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        return retrieve;
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
                if (inputStream != null) {
                    inputStream.close();
                }

            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        return photo;
    }

}
