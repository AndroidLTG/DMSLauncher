package com.vietdms.mobile.dmslauncher.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vietdms.mobile.dmslauncher.Home;
import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.R;
import com.victor.loading.rotate.RotateLoading;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by DMSv4 on 12/3/2015.
 */
public class CenterFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, TextWatcher {
    private static final String PACKAGE = "package:";
    ;
    private Timer timer = new Timer();
    private Context context;

    public CenterFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_center, container, false);
        getId(v);
        event(v);
        return v;
    }

    private void event(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Home.linearMain.setBackground(MyMethod.getWallpaper(context));
        }
        v.findViewById(R.id.btn_Call).setOnClickListener(this);
        v.findViewById(R.id.btn_SmS).setOnClickListener(this);
        v.findViewById(R.id.btn_Email).setOnClickListener(this);
        v.findViewById(R.id.btn_Menu).setOnClickListener(this);
        v.findViewById(R.id.btn_Lock).setOnClickListener(this);
    }

    private void getId(View v) {
        context = getContext();
        Home.linearMain = (LinearLayout) v.findViewById(R.id.linear_chinh);
        Home.mInformationTextView = (TextView) v.findViewById(R.id.txtNotify);
        Home.rela_layout_center = (RelativeLayout) v.findViewById(R.id.rela_layout_center);
        Home.rela_main_center = (RelativeLayout) v.findViewById(R.id.rela_main_center);
        Home.layout_listapp = (LinearLayout) v.findViewById(R.id.linear_list_app);
        Home.editSearch = (EditText) v.findViewById(R.id.edit_Search);
        Home.gridListApp = (GridView) v.findViewById(R.id.gridListApp);
        Home.rotateLoading = (RotateLoading) v.findViewById(R.id.rotateloading);
        Home.rotateLoading.stop();
        Home.rela_layout_center.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Home.txtTitle.setText("Đổi hình nền");
                MyMethod.showChangeWallpaper(context);
                return false;
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Call:
                MyMethod.callPhone(v.getContext());
                break;
            case R.id.btn_Menu:
                Home.editSearch.setText("");
                Home.editSearch.clearFocus();
                MyMethod.showApps(v.getContext(), Home.viewPager);
                Home.gridListApp.setOnItemLongClickListener(this);
                Home.gridListApp.setOnItemClickListener(this);
                Home.editSearch.addTextChangedListener(this);
                break;
            case R.id.btn_SmS:
                MyMethod.showSms(v.getContext());
                break;
            case R.id.btn_Email:
                MyMethod.showGmail(v.getContext());
                break;
            case R.id.btn_Lock:
                MyMethod.lockDevice(Home.devicePolicyManager);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Uri packageUri = Uri.parse(PACKAGE + Home.allItems.get(position).name);
        Intent uninstallIntent =
                new Intent(Intent.ACTION_DELETE, packageUri);
        startActivity(uninstallIntent);
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MyMethod.runApp(Home.allItems.get(position), Home.manager, view.getRootView().getContext());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        timer.cancel();
        timer = new Timer();
        long DELAY = 500;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // you will probably need to use runOnUiThread(Runnable action) for some specific actions

            }

        }, DELAY);
        //Do that
        MyMethod.showApps(getContext(), Home.viewPager, Home.editSearch.getText().toString());

    }

}
