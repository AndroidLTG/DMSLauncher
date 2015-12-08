package com.example.dmsv4.dmslauncher.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.dmsv4.dmslauncher.R;

/**
 * Created by DMSv4 on 12/3/2015.
 */
public class RightFragment extends Fragment implements View.OnClickListener {
    private RelativeLayout rela_checkin,rela_main,rela_checkout,rela_setting,rela_notify,rela_location;
    public RightFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_right, container, false);
        rela_checkout = (RelativeLayout)v.findViewById(R.id.rela_layout_checkout);
        rela_checkin = (RelativeLayout)v.findViewById(R.id.rela_layout_checkin);
        rela_main = (RelativeLayout) v.findViewById(R.id.rela_layout_main);
        v.findViewById(R.id.btnCheckIn).setOnClickListener(this);
        v.findViewById(R.id.btnCheckOut).setOnClickListener(this);
        v.findViewById(R.id.btnNotify).setOnClickListener(this);
        v.findViewById(R.id.btnLocation).setOnClickListener(this);
        v.findViewById(R.id.btnSetting).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCheckIn:
                showLayout(Layouts.CheckIn);
                break;
            case R.id.btnCheckOut:
                showLayout(Layouts.CheckOut);
                break;
            case R.id.btnLocation:
                showLayout(Layouts.Location);
                break;
            case R.id.btnNotify:
                showLayout(Layouts.Notify);
                break;
            case R.id.btnSetting:
                showLayout(Layouts.Setting);
                break;
        }
    }
    private enum Layouts {
        CheckIn, CheckOut, Notify, Location, Setting
    }

    private void showLayout(Layouts layout) {
        switch (layout) {
            case CheckIn:
                rela_checkin.setVisibility(View.VISIBLE);
                rela_main.setVisibility(View.GONE);
                break;
            case CheckOut:
                rela_checkout.setVisibility(View.VISIBLE);
                rela_main.setVisibility(View.GONE);
                break;
            case Notify:
                break;
            case Location:
                break;
            case Setting:
                break;
        }
    }
}
