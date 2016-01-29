package com.vietdms.mobile.dmslauncher.CustomAdapter;

/**
 * Created by ${LTG} on ${10/12/1994}.
 */


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import com.vietdms.mobile.dmslauncher.R;

/**
 * @author notme
 */
public class DialogLockAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;

    public DialogLockAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = convertView;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.dialog_block, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.editText = (EditText) view.findViewById(R.id.edit_pass);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.image_pass);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Context context = parent.getContext();
        viewHolder.imageView.setImageResource(R.drawable.key_white_btn);
        viewHolder.editText.requestFocus();


        return view;
    }

    static class ViewHolder {
        EditText editText;
        ImageView imageView;
    }
}
