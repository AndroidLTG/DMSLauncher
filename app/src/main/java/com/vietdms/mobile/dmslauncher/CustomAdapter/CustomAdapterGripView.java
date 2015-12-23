package com.vietdms.mobile.dmslauncher.CustomAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vietdms.mobile.dmslauncher.GetSet.AppsDetail;
import com.vietdms.mobile.dmslauncher.R;

import java.util.List;

public class CustomAdapterGripView extends BaseAdapter {
    private LayoutInflater layoutinflater;
    private List<AppsDetail> listApp;
    private Context context;

    public CustomAdapterGripView(Context context, List<AppsDetail> customizedListView) {
        this.context = context;
        layoutinflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listApp = customizedListView;
    }

    @Override
    public int getCount() {
        return listApp.size();
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
        ViewHolder listViewHolder;
        if (convertView == null) {
            listViewHolder = new ViewHolder();
            convertView = layoutinflater.inflate(R.layout.custom_gripview, parent, false);
            listViewHolder.textInListView = (TextView) convertView.findViewById(R.id.textView);
            listViewHolder.imageInListView = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(listViewHolder);
        } else {
            listViewHolder = (ViewHolder) convertView.getTag();
        }
        if (listApp.get(position).role == 1 || listApp.get(position).role == 2) {
            listViewHolder.textInListView.setText(listApp.get(position).label);
            listViewHolder.imageInListView.setImageDrawable(listApp.get(position).icon);
        }
        return convertView;
    }

    static class ViewHolder {
        TextView textInListView;
        ImageView imageInListView;
    }
}