package com.vietdms.mobile.dmslauncher.Fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.vietdms.mobile.dmslauncher.CustomAdapter.DialogAdapter;
import com.vietdms.mobile.dmslauncher.Home;
import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.R;
import com.vietdms.mobile.dmslauncher.RecycleView.Customer;
import com.vietdms.mobile.dmslauncher.RecycleView.Products;
import com.vietdms.mobile.dmslauncher.RecycleView.RecyclerItemClickListener;
import com.vietdms.mobile.dmslauncher.RecycleView.RecyclerViewAdapterCustomer;
import com.vietdms.mobile.dmslauncher.RecycleView.RecyclerViewAdapterOrder;

import org.buraktamturk.loadingview.LoadingView;

import java.io.File;
import java.util.ArrayList;

import CommonLib.Const;
import CommonLib.EventPool;
import CommonLib.EventType;
import CommonLib.LocationDetector;
import CommonLib.Model;
import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * Created by DMSv4 on 12/3/2015.
 */
public class RightFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, RecyclerItemClickListener.OnItemClickListener, AdapterView.OnItemSelectedListener {
    private static final int ACTION_TAKE_CARMERA = 999;
    private GoogleMap googleMap;
    private EditText editName, editPass, editPassOld, editPassNew, editPassNewAgain;
    private ImageView imagePhotoIn, imagePhotoOut;
    private TextInputLayout inputLayoutName, inputLayoutPassword, inputLayoutPasswordOld, inputLayoutPasswordNew, inputLayoutPasswordNewAgain;
    private RecyclerView recyclerOrder, recyclerCustomer;
    private ArrayList<Products> productsArrayList = new ArrayList<>();
    private ArrayList<Customer> customersArrayList = new ArrayList<>();
    private RecyclerViewAdapterOrder adapterOrder;
    private RecyclerViewAdapterCustomer adapterCustomer;
    private LoadingView loadingLogin;
    private String imagePath;
    private boolean isRunning = false;
    private Handler handler = new Handler();
    private Context context;
    private SupportMapFragment mapFragment, mapAdminFragment;
    private Location location = null;
    private TextView txtUserName, txtFullName, txtDeviceName, txtAccuracy;
    private FloatingActionButton fab, fabcancel;
    private MaterialSpinner spStaff;
    private ArrayAdapter<String> adapterStaff;

    private int positionClick = 0;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.v("QueueTimerView", "timedout");
            EventType.EventBase event = EventPool.view().deQueue();
            while (event != null) {
                processEvent(event);
                if (!isRunning) break;
                event = EventPool.view().deQueue();
            }
            if (isRunning) handler.postDelayed(this, Const.QueueTimerView);
        }
    };

    //MAP
    public RightFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.w(MyMethod.LOG_EDMS, "onCreateViewFragment begin");
        View v = inflater.inflate(R.layout.fragment_right, container, false);
        getId(v);
        event(v);
        showLayout(Layouts.LogIn);
        Log.d(MyMethod.LOG_EDMS, "onCreateViewFragment end");
        return v;
    }


    private void event(final View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Home.relativeCheckIn.setBackground(MyMethod.getWallpaper(context));
            Home.relativeCheckOut.setBackground(MyMethod.getWallpaper(context));
            Home.relativeRight.setBackground(MyMethod.getWallpaper(context));
        }

        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapAdminFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapAdmin);
        String[] Staff = {"Staff 1", "Staff 2", "Staff 3", "Staff 4", "Staff 5", "Staff 6"};
        adapterStaff = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, Staff);
        adapterStaff.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStaff.setAdapter(adapterStaff);
        spStaff.setOnItemSelectedListener(this);
        v.findViewById(R.id.fab).setOnClickListener(this);
        v.findViewById(R.id.fabGetAgain).setOnClickListener(this);
        v.findViewById(R.id.fabCancel).setOnClickListener(this);
        v.findViewById(R.id.btnCheckIn).setOnClickListener(this);
        v.findViewById(R.id.btnCheckOut).setOnClickListener(this);
        v.findViewById(R.id.btnNotify).setOnClickListener(this);
        v.findViewById(R.id.btnCustomer).setOnClickListener(this);
        v.findViewById(R.id.btnSetting).setOnClickListener(this);
        v.findViewById(R.id.btn_signin).setOnClickListener(this);
        v.findViewById(R.id.btn_changepass).setOnClickListener(this);
        v.findViewById(R.id.btn_logout).setOnClickListener(this);
        v.findViewById(R.id.btnOrder).setOnClickListener(this);
        v.findViewById(R.id.btnReport).setOnClickListener(this);
        v.findViewById(R.id.btnSetting).setOnClickListener(this);
        v.findViewById(R.id.btnUpdate).setOnClickListener(this);
        v.findViewById(R.id.btn_get_checkin).setOnClickListener(this);
        v.findViewById(R.id.btn_get_photo_checkin).setOnClickListener(this);
        v.findViewById(R.id.btn_get_checkout).setOnClickListener(this);
        v.findViewById(R.id.btn_get_photo_checkout).setOnClickListener(this);
        v.findViewById(R.id.btn_save_send_checkin).setOnClickListener(this);
        v.findViewById(R.id.btn_save_send_checkout).setOnClickListener(this);
        editName.addTextChangedListener(new MyTextWatcher(editName));
        editPass.addTextChangedListener(new MyTextWatcher(editPass));
        editPassOld.addTextChangedListener(new MyTextWatcher(editPassOld));
        editPassNew.addTextChangedListener(new MyTextWatcher(editPassNew));
        editPassNewAgain.addTextChangedListener(new MyTextWatcher(editPassNewAgain));
        recyclerOrder.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), this));
        recyclerCustomer.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), this));

    }

    private void getId(View v) {
        context = getContext();
        spStaff = (MaterialSpinner) v.findViewById(R.id.spStaff);
        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fabcancel = (FloatingActionButton) v.findViewById(R.id.fabCancel);
        txtUserName = (TextView) v.findViewById(R.id.txtUserName);
        txtFullName = (TextView) v.findViewById(R.id.txtFullName);
        txtDeviceName = (TextView) v.findViewById(R.id.txtDeviceName);
        txtAccuracy = (TextView) v.findViewById(R.id.txtAccuracy);
        loadingLogin = (LoadingView) v.findViewById(R.id.loginLoadingView);
        editPassNew = (EditText) v.findViewById(R.id.input_password_new);
        editPassOld = (EditText) v.findViewById(R.id.input_password_old);
        editPassNewAgain = (EditText) v.findViewById(R.id.input_password_new_again);
        inputLayoutPasswordOld = (TextInputLayout) v.findViewById(R.id.input_layout_password_old);
        inputLayoutPasswordNew = (TextInputLayout) v.findViewById(R.id.input_layout_password_new);
        inputLayoutPasswordNewAgain = (TextInputLayout) v.findViewById(R.id.input_layout_password_new_again);
        editName = (EditText) v.findViewById(R.id.input_name);
        editPass = (EditText) v.findViewById(R.id.input_password);
        imagePhotoIn = (ImageView) v.findViewById(R.id.imagePhotoIn);
        imagePhotoOut = (ImageView) v.findViewById(R.id.imagePhotoOut);
        Home.txtAddressIn = (TextView) v.findViewById(R.id.txtAddressIn);
        Home.txtAddressOut = (TextView) v.findViewById(R.id.txtAddressOut);
        inputLayoutName = (TextInputLayout) v.findViewById(R.id.input_layout_name);
        inputLayoutPassword = (TextInputLayout) v.findViewById(R.id.input_layout_password);
        Home.linearLogin = (LinearLayout) v.findViewById(R.id.linear_login);
        Home.linearChangePass = (LinearLayout) v.findViewById(R.id.linear_change_pass);
        Home.linearListOrder = (LinearLayout) v.findViewById(R.id.linear_list_order);
        Home.linearCustomer = (LinearLayout) v.findViewById(R.id.linear_customer);
        Home.mapView = (CoordinatorLayout) v.findViewById(R.id.mapView);
        Home.mapViewAdmin = (CoordinatorLayout) v.findViewById(R.id.mapViewAdmin);
        recyclerOrder = (RecyclerView) v.findViewById(R.id.recyclerOrder);
        recyclerOrder.setHasFixedSize(true);
        recyclerCustomer = (RecyclerView) v.findViewById(R.id.recyclerCustomer);
        recyclerCustomer.setHasFixedSize(true);
        LinearLayoutManager managerOrder = new LinearLayoutManager(v.getContext());
        managerOrder.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager managerCustomer = new LinearLayoutManager(v.getContext());
        managerCustomer.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerOrder.setLayoutManager(managerOrder);
        recyclerCustomer.setLayoutManager(managerCustomer);
        adapterOrder = new RecyclerViewAdapterOrder(productsArrayList, v.getContext());
        adapterCustomer = new RecyclerViewAdapterCustomer(customersArrayList, v.getContext());
        recyclerOrder.setAdapter(adapterOrder);
        recyclerCustomer.setAdapter(adapterCustomer);
        Home.rela_checkout = (RelativeLayout) v.findViewById(R.id.rela_layout_checkout);
        Home.rela_checkin = (RelativeLayout) v.findViewById(R.id.rela_layout_checkin);
        Home.relativeCheckIn = (RelativeLayout) v.findViewById(R.id.rela_bg_checkin);
        Home.relativeCheckOut = (RelativeLayout) v.findViewById(R.id.rela_bg_checkout);
        Home.relativeRight = (RelativeLayout) v.findViewById(R.id.rela_bg_right);
        Home.rela_main = (RelativeLayout) v.findViewById(R.id.rela_layout_main);
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        v.setLayoutParams(p);
        v.requestLayout();
    }

    @Override
    public void onStop() {
        Log.w(MyMethod.LOG_EDMS, "onStopFragment begin");
        super.onStop();
        isRunning = false;
        Log.d(MyMethod.LOG_EDMS, "onStopFragment end");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.w(MyMethod.LOG_EDMS, "onViewStateRestoredFragment begin");
        isRunning = true;
        handler.postDelayed(runnable, Const.QueueTimerView);
        Log.w(MyMethod.LOG_EDMS, "onViewStateRestoredFragment end");
    }

    @Override
    public void onResume() {

        Log.w(MyMethod.LOG_EDMS, "onResumeFragment begin");
        super.onResume();
        isRunning = true;
        handler.postDelayed(runnable, Const.QueueTimerView);
        Log.w(MyMethod.LOG_EDMS, "onResumeFragment end");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == ACTION_TAKE_CARMERA) {
            BitmapFactory.Options options;
            Bitmap bitmap = null;
            try {
                options = new BitmapFactory.Options();
                options.inSampleSize = 4;// 1/4 of origin image size from width and height
                bitmap = BitmapFactory.decodeFile(imagePath, options);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (MyMethod.CHECKIN) {
                imagePhotoIn.setVisibility(View.VISIBLE);
                imagePhotoIn.setImageBitmap(bitmap);
            } else {
                imagePhotoOut.setVisibility(View.VISIBLE);
                imagePhotoOut.setImageBitmap(bitmap);
            }
            EventPool.control().enQueue(new EventType.EventTakePhoto(imagePath, Model.inst().getServerTime()));
            MyMethod.showToast(getContext(), imagePath);
        }
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File fileDir = new File(Environment.getExternalStorageDirectory()
                + "/DMSData");
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        if (MyMethod.CHECKIN)
            imagePath = Environment.getExternalStorageDirectory() + "/DMSData/" + "IN_"
                    + System.currentTimeMillis() + ".jpg";
        else
            imagePath = Environment.getExternalStorageDirectory() + "/DMSData/" + "OUT_"
                    + System.currentTimeMillis() + ".jpg";
        File carmeraFile = new File(imagePath);
        Uri imageCarmeraUri = Uri.fromFile(carmeraFile);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                imageCarmeraUri);
        try {
            intent.putExtra("return-data", true);
            this.startActivityForResult(intent, ACTION_TAKE_CARMERA);
        } catch (ActivityNotFoundException e) {
            // Do nothing for now
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (location != null) {
                    if (MyMethod.CHECKIN) {
                        showLayout(Layouts.CheckIn);
                        Home.txtAddressIn.setText(MyMethod.getAddress(location, context));

                    } else {
                        showLayout(Layouts.CheckOut);
                        Home.txtAddressOut.setText(MyMethod.getAddress(location, context));
                    }
                } else {
                    MyMethod.refreshMap(context, googleMap);
                    MyMethod.showToast(context, context.getString(R.string.location_wait));
                    //EventPool.control().enQueue(new EventType.EventLoadHighPrecisionLocationRequest());
                    LocationDetector.inst().setRequest(true, Const.DefaultHighPrecisionIntervalInSeconds);
                }

                break;
            case R.id.fabGetAgain:
                MyMethod.refreshMap(context, googleMap);
                MyMethod.showToast(context, context.getString(R.string.location_wait));
                //EventPool.control().enQueue(new EventType.EventLoadHighPrecisionLocationRequest());
                LocationDetector.inst().setRequest(true, Const.DefaultHighPrecisionIntervalInSeconds);
                break;
            case R.id.fabCancel: {
                if (MyMethod.CHECKIN)
                    showLayout(Layouts.CheckIn);

                else
                    showLayout(Layouts.CheckOut);
            }
            break;
            case R.id.btn_get_checkout:
                showLayout(Layouts.Map);
                break;
            case R.id.btn_get_photo_checkout:
                //GET CAMERA PHOTO
                takePhoto();
                break;
            case R.id.btn_get_checkin:
                showLayout(Layouts.Map);
                break;
            case R.id.btn_get_photo_checkin:
                //GET CAMERA PHOTO
                takePhoto();
                break;
            case R.id.btn_save_send_checkin:

                if (MyMethod.checkInputSaveSend(Home.txtAddressIn, imagePhotoIn, context)) {
                    //SAVE AND SEND CHECK IN DATA
                    Home.txtAddressIn.setText(context.getString(R.string.location_none));
                    showLayout(Layouts.Main);
                }
                break;
            case R.id.btnCheckIn:
                showLayout(Layouts.CheckIn);
                break;
            case R.id.btnCheckOut:
                showLayout(Layouts.CheckOut);
                break;
            case R.id.btn_save_send_checkout:
                if (MyMethod.checkInputSaveSend(Home.txtAddressOut, imagePhotoOut, context)) {
                    //SAVE AND SEND CHECK IN DATA
                    Home.txtAddressOut.setText(context.getString(R.string.location_none));
                    showLayout(Layouts.Main);
                }
                break;
            case R.id.btnCustomer:
                Customer customer = new Customer();
                customer.setCustomerNo_("KH01");
                customer.setCustomerName_("khách hàng của Lãng Tử Gió");
                customer.setCustomerAddress_("Gò cẩm đệm");
                customer.setCustomerPhoto_(null);
                customersArrayList.add(customer);
                adapterCustomer.notifyDataSetChanged();
                EventPool.control().enQueue(new EventType.EventLoadCustomerRequest());
                break;
            case R.id.btnReport:
                EventPool.control().enQueue(new EventType.EventGetLocationsRequest(30));
                showLayout(Layouts.MapAdmin);
                break;
            case R.id.btn_signin:
                submitFormLogin(v);
                break;
            case R.id.btn_logout:
                EventPool.control().enQueue(new EventType.EventLogoutRequest());
                break;
            case R.id.btn_changepass:
                //XU LY DOI PASS
                submitFormChange(v);
                break;
            case R.id.btnNotify:
                //XU LY BAO CAO
                break;
            case R.id.btnSetting:
                //XU LY SETTING
                showLayout(Layouts.ChangePass);
                break;
            case R.id.btnUpdate:
                //XU LY UPDATE
                break;
            case R.id.btnOrder:
                //XU LY Order
                //test
                Products products = new Products();
                products.setProductNo_("DH01");
                products.setProductName_("Đơn hàng của Lãng Tử Gió");
                products.setProductAmount_((float) 150000);
                products.setProductQuantity_((float) 1);
                products.setProductType_("Cái");
                productsArrayList.add(products);
                adapterOrder.notifyDataSetChanged();
                EventPool.control().enQueue(new EventType.EventLoadOrderRequest());
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        if (MyMethod.isVisible(Home.linearListOrder))
            showLayout(Layouts.MenuOrderClick);
        else showLayout(Layouts.MenuCustomerClick);
    }

    private void submitFormLogin(View v) {
        if (!validateName()) {
            return;
        }
        if (!validatePassword()) {
            return;
        }
        EventPool.control().enQueue(new EventType.EventLoginRequest(editName.getText().toString(), editPass.getText().toString()));
        MyMethod.closeFocus(v);
        loadingLogin.setLoading(true);

        //showLayout(Layouts.Main);
    }

    private void submitFormChange(View v) {
        if (!validatePasswordOld()) {
            return;
        }
        if (!validatePasswordNew()) {
            return;
        }
        if (!validatePasswordNewAgain()) {
            return;
        }
        MyMethod.closeFocus(v);
        EventPool.control().enQueue(new EventType.EventChangeRequest(editPassOld.getText().toString(), editPassNew.getText().toString()));
        //showLayout(Layouts.LogIn);
    }

    private void showLayout(Layouts layout) {
        switch (layout) {
            case CheckIn:
//                if (!MyMethod.blurredCheckIn) {
//                    MyMethod.blur(context, Home.relativeCheckIn);
//                    MyMethod.blurredCheckIn = !MyMethod.blurredCheckIn;
//                }
                MyMethod.CHECKIN = true;
                imagePhotoIn.setVisibility(View.GONE);
                Home.rela_checkin.setVisibility(View.VISIBLE);
                Home.rela_main.setVisibility(View.GONE);
                Home.linearLogin.setVisibility(View.GONE);
                Home.linearChangePass.setVisibility(View.GONE);
                Home.linearListOrder.setVisibility(View.GONE);
                Home.linearCustomer.setVisibility(View.GONE);
                Home.mapView.setVisibility(View.GONE);
                Home.mapViewAdmin.setVisibility(View.GONE);
                break;
            case CheckOut:
//                if (!MyMethod.blurredCheckOut) {
//                    MyMethod.blur(context, Home.relativeCheckOut);
//                    MyMethod.blurredCheckOut = !MyMethod.blurredCheckOut;
//                }
                MyMethod.CHECKIN = false;
                imagePhotoOut.setVisibility(View.GONE);
                Home.rela_checkout.setVisibility(View.VISIBLE);
                Home.rela_main.setVisibility(View.GONE);
                Home.linearLogin.setVisibility(View.GONE);
                Home.linearChangePass.setVisibility(View.GONE);
                Home.linearListOrder.setVisibility(View.GONE);
                Home.linearCustomer.setVisibility(View.GONE);
                Home.mapView.setVisibility(View.GONE);
                Home.mapViewAdmin.setVisibility(View.GONE);
                break;
            case Notify:
                break;
            case Customer:
                Home.rela_checkin.setVisibility(View.GONE);
                Home.rela_checkout.setVisibility(View.GONE);
                Home.rela_main.setVisibility(View.GONE);
                Home.linearLogin.setVisibility(View.GONE);
                Home.linearChangePass.setVisibility(View.GONE);
                Home.linearListOrder.setVisibility(View.GONE);
                Home.linearCustomer.setVisibility(View.VISIBLE);
                Home.mapView.setVisibility(View.GONE);
                Home.mapViewAdmin.setVisibility(View.GONE);
                break;
            case Setting:
                break;
            case LogIn:
                //show hide layouts....
                loadingLogin.setLoading(false);
                Home.linearCustomer.setVisibility(View.GONE);
                Home.rela_checkin.setVisibility(View.GONE);
                Home.rela_checkout.setVisibility(View.GONE);
                Home.rela_main.setVisibility(View.GONE);
                Home.linearLogin.setVisibility(View.VISIBLE);
                Home.mapView.setVisibility(View.GONE);
                Home.mapViewAdmin.setVisibility(View.GONE);
                Home.linearChangePass.setVisibility(View.GONE);
                Home.linearListOrder.setVisibility(View.GONE);
                break;
            case ChangePass:
                Home.linearCustomer.setVisibility(View.GONE);
                Home.rela_checkin.setVisibility(View.GONE);
                Home.rela_checkout.setVisibility(View.GONE);
                Home.rela_main.setVisibility(View.GONE);
                Home.linearLogin.setVisibility(View.GONE);
                Home.linearChangePass.setVisibility(View.VISIBLE);
                Home.mapView.setVisibility(View.GONE);
                Home.mapViewAdmin.setVisibility(View.GONE);
                Home.linearListOrder.setVisibility(View.GONE);
                break;
            case Map:
                if(googleMap!=null) googleMap.clear();
                Home.linearCustomer.setVisibility(View.GONE);
                Home.rela_checkin.setVisibility(View.GONE);
                Home.rela_checkout.setVisibility(View.GONE);
                Home.rela_main.setVisibility(View.GONE);
                Home.linearLogin.setVisibility(View.GONE);
                Home.linearChangePass.setVisibility(View.GONE);
                Home.linearListOrder.setVisibility(View.GONE);
                Home.mapView.setVisibility(View.VISIBLE);
                Home.mapViewAdmin.setVisibility(View.GONE);
                mapFragment.getMapAsync(this);
                break;
            case MapAdmin:
                Home.linearCustomer.setVisibility(View.GONE);
                Home.rela_checkin.setVisibility(View.GONE);
                Home.rela_checkout.setVisibility(View.GONE);
                Home.rela_main.setVisibility(View.GONE);
                Home.linearLogin.setVisibility(View.GONE);
                Home.linearChangePass.setVisibility(View.GONE);
                Home.linearListOrder.setVisibility(View.GONE);
                Home.mapView.setVisibility(View.GONE);
                Home.mapViewAdmin.setVisibility(View.VISIBLE);
                mapAdminFragment.getMapAsync(this);
                break;
            case ListOrder:
                //
                Home.rela_checkin.setVisibility(View.GONE);
                Home.rela_checkout.setVisibility(View.GONE);
                Home.rela_main.setVisibility(View.GONE);
                Home.linearLogin.setVisibility(View.GONE);
                Home.linearChangePass.setVisibility(View.GONE);
                Home.mapView.setVisibility(View.GONE);
                Home.mapViewAdmin.setVisibility(View.GONE);
                Home.linearListOrder.setVisibility(View.VISIBLE);
                Home.linearCustomer.setVisibility(View.GONE);
                break;
            case Main:
                loadingLogin.setLoading(false);
                Home.rela_checkin.setVisibility(View.GONE);
                Home.rela_checkout.setVisibility(View.GONE);
                Home.rela_main.setVisibility(View.VISIBLE);
                Home.linearLogin.setVisibility(View.GONE);
                Home.linearChangePass.setVisibility(View.GONE);
                Home.linearListOrder.setVisibility(View.GONE);
                Home.mapView.setVisibility(View.GONE);
                Home.mapViewAdmin.setVisibility(View.GONE);
                Home.linearCustomer.setVisibility(View.GONE);
                break;
            case MenuOrderClick:
                Holder holderOrder = new ListHolder();
                DialogAdapter dialogOrder = new DialogAdapter(getContext());
                Home.dialogOrder = DialogPlus.newDialog(getContext())
                        .setContentHolder(holderOrder)
                        .setHeader(R.layout.header_dialog)
                        .setFooter(R.layout.footer_dialog)
                        .setCancelable(true)
                        .setGravity(Gravity.BOTTOM)
                        .setAdapter(dialogOrder)
                        .setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                TextView textView = (TextView) view.findViewById(R.id.text_view);
                                switch (textView.getText().toString()) {
                                    case "Ghi nhận vào":
                                        MyMethod.showToast(context, "Ghi nhận vào");
                                        break;
                                    case "Ghi nhận ra":
                                        MyMethod.showToast(context, "Ghi nhận ra");
                                        break;
                                    case "Hủy bỏ":
                                        dialog.dismiss();
                                        break;
                                    default:
                                        break;
                                }
                                //        dialog.dismiss();
                            }
                        })
                        .setExpanded(false)
                        .create();
                Home.dialogOrder.findViewById(R.id.footer_dialog).requestFocus();
                Home.dialogOrder.show();
                ((TextView) Home.dialogOrder.findViewById(R.id.header_dialog)).setText(productsArrayList.get(positionClick).getProductName_());

                break;
            case MenuCustomerClick:
                Holder holderCustomer = new ListHolder();
                DialogAdapter dialogCustomer = new DialogAdapter(getContext());
                Home.dialogCustomer = DialogPlus.newDialog(getContext())
                        .setContentHolder(holderCustomer)
                        .setHeader(R.layout.header_dialog)
                        .setFooter(R.layout.footer_dialog)
                        .setCancelable(true)
                        .setGravity(Gravity.BOTTOM)
                        .setAdapter(dialogCustomer)
                        .setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                TextView textView = (TextView) view.findViewById(R.id.text_view);
                                switch (textView.getText().toString()) {
                                    case "Ghi nhận vào":
                                        MyMethod.showToast(context, "Ghi nhận vào");
                                        break;
                                    case "Ghi nhận ra":
                                        MyMethod.showToast(context, "Ghi nhận ra");
                                        break;
                                    case "Hủy bỏ":
                                        dialog.dismiss();
                                        break;
                                    default:
                                        break;
                                }
                                //        dialog.dismiss();
                            }
                        })
                        .setExpanded(false)
                        .create();
                Home.dialogCustomer.findViewById(R.id.footer_dialog).requestFocus();
                Home.dialogCustomer.show();
                ((TextView) Home.dialogCustomer.findViewById(R.id.header_dialog)).setText(customersArrayList.get(positionClick).getCustomerName_());
                break;
        }
    }

    private boolean isVisible(Layouts layouts) {
        boolean result = false;
        switch (layouts) {
            case ChangePass:
                result = (MyMethod.isVisible(Home.linearChangePass));
                break;
            case LogIn:
                result = (MyMethod.isVisible(Home.linearLogin));
                break;
        }
        return result;
    }

    private boolean validateName() {
        if (editName.getText().toString().trim().isEmpty() && isVisible(Layouts.LogIn)) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            MyMethod.requestFocus(editName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword() {
        if (editPass.getText().toString().trim().isEmpty() && isVisible(Layouts.LogIn)) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            MyMethod.requestFocus(editPass);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePasswordOld() {
        if (editPassOld.getText().toString().trim().isEmpty() && isVisible(Layouts.ChangePass)) {
            inputLayoutPasswordOld.setError(getString(R.string.err_msg_password_old));
            MyMethod.requestFocus(editPassOld);
            return false;
        } else {
            inputLayoutPasswordOld.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePasswordNew() {
        if (editPassNew.getText().toString().trim().isEmpty() && isVisible(Layouts.ChangePass)) {
            inputLayoutPasswordNew.setError(getString(R.string.err_msg_password_new));
            MyMethod.requestFocus(editPassNew);
            return false;
        } else {
            inputLayoutPasswordNew.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePasswordNewAgain() {
        if (editPassNewAgain.getText().toString().trim().isEmpty() && isVisible(Layouts.ChangePass)) {
            inputLayoutPasswordNewAgain.setError(getString(R.string.err_msg_password_new_again));
            MyMethod.requestFocus(editPassNewAgain);
            return false;
        } else if (!editPassNewAgain.getText().toString().equals(editPassNew.getText().toString()) && isVisible(Layouts.ChangePass)) {
            inputLayoutPasswordNewAgain.setError(getString(R.string.err_msg_password_not_match));
            MyMethod.requestFocus(editPassNewAgain);
            return false;
        } else {
            inputLayoutPasswordNewAgain.setErrorEnabled(false);
        }
        return true;
    }

    private void processEvent(EventType.EventBase event) {
        switch (event.type) {
            case Logout:
                EventType.EventLogoutResult logoutResult = (EventType.EventLogoutResult) event;
                if (logoutResult.success) {
                    showLayout(Layouts.LogIn);
                } else {
                    MyMethod.showToast(context, logoutResult.message);
                }
                break;
            case Login:
                EventType.EventLoginResult loginResult = (EventType.EventLoginResult) event;
                if (loginResult.success) {
                    MyMethod.showToast(getContext(), getString(R.string.sign_in_success));
                    loadingLogin.setLoading(false);
                    showLayout(Layouts.Main);
                    txtUserName.setText(Model.inst().getUsername());
                    txtFullName.setText(Model.inst().getFullname());
                    txtDeviceName.setText(Build.MODEL);
                } else {
                    MyMethod.showToast(getContext(), getString(R.string.sign_in_fail) + " : " + loginResult.message);
                    loadingLogin.setLoading(false);
                    showLayout(Layouts.LogIn);
                }
                break;
            case ChangePass:
                EventType.EventChangeResult changeResult = (EventType.EventChangeResult) event;
                if (changeResult.success) {
                    MyMethod.showToast(getContext(), getString(R.string.change_pass_success));
                    loadingLogin.setLoading(false);
                    showLayout(Layouts.LogIn);
                } else {
                    MyMethod.showToast(getContext(), getString(R.string.change_pass_fail));
                    loadingLogin.setLoading(false);
                    showLayout(Layouts.ChangePass);
                }
                break;

            case LoadOrders:
                showLayout(Layouts.ListOrder);
                break;
            case LoadCustomers:
                showLayout(Layouts.Customer);
                break;
            case HighPrecisionLocation:
                EventType.EventLoadHighPrecisionLocationResult locationResult = (EventType.EventLoadHighPrecisionLocationResult) event;
                if (locationResult.location != null) {
                    location = locationResult.location;
                    MyMethod.loadMap(googleMap, location, context);
                    txtAccuracy.setText("Độ chính xác " + location.getAccuracy() + "m");
                    fab.setEnabled(true);
                } else {
                    MyMethod.showToast(context, context.getString(R.string.location_none));
                }

                break;
            case GCMMessage:
                EventType.EventGCMMessage gcmMessage = (EventType.EventGCMMessage) event;
                MyMethod.sendNotification(context, gcmMessage.message);
                break;
            case GetLocations:
                EventType.EventGetLocationsResult arrLocations = (EventType.EventGetLocationsResult) event;
                MyMethod.drawMap(context,googleMap,arrLocations.arrayLocations);
            default:
                Log.w("View_processEvent", "unhandled " + event.type);
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (googleMap != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(17, 107), 5.5f));
            fab.setEnabled(false);
            this.googleMap = googleMap;
            //EventPool.control().enQueue(new EventType.EventLoadHighPrecisionLocationRequest());
            LocationDetector.inst().setRequest(true, Const.DefaultHighPrecisionIntervalInSeconds);
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position >= 0) MyMethod.showToast(context, adapterStaff.getItem(position).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private enum Layouts {
        CheckIn, CheckOut, Notify, Customer, Setting, LogIn, ChangePass, ListOrder, Main, MenuOrderClick, MenuCustomerClick, Map, MapAdmin
    }

    private class MyTextWatcher implements TextWatcher {
        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_name:
                    validateName();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
                case R.id.input_password_old:
                    validatePasswordOld();
                    break;
                case R.id.input_password_new:
                    validatePasswordNew();
                    break;
                case R.id.input_password_new_again:
                    validatePasswordNewAgain();
                    break;
            }
        }
    }
}
