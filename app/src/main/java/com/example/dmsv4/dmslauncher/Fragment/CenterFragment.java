package com.example.dmsv4.dmslauncher.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.example.dmsv4.dmslauncher.Home;
import com.example.dmsv4.dmslauncher.R;
import com.victor.loading.rotate.RotateLoading;

/**
 * Created by DMSv4 on 12/3/2015.
 */
public class CenterFragment extends Fragment {
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
        return v;
    }

    private void getId(View v) {
        Home.rela_layout_center = (RelativeLayout) v.findViewById(R.id.rela_layout_center);
        Home.gridListApp = (GridView) v.findViewById(R.id.gridListApp);
        Home.rotateLoading = (RotateLoading) v.findViewById(R.id.rotateloading);
        Home.rotateLoading.stop();
    }
}
