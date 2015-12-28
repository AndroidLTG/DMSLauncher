package com.vietdms.mobile.dmslauncher.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vietdms.mobile.dmslauncher.Home;
import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.R;
import com.vietdms.mobile.dmslauncher.RecycleView.Products;
import com.vietdms.mobile.dmslauncher.RecycleView.RecyclerItemClickListener;
import com.vietdms.mobile.dmslauncher.RecycleView.RecyclerViewAdapter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;

import org.buraktamturk.loadingview.LoadingView;

import java.util.ArrayList;

import CommonLib.Const;
import CommonLib.EventPool;
import CommonLib.EventType;

/**
 * Created by DMSv4 on 12/3/2015.
 */
public class RightFragment extends Fragment implements View.OnClickListener, RecyclerItemClickListener.OnItemClickListener {


    private EditText editName, editPass, editPassOld, editPassNew, editPassNewAgain;
    private TextInputLayout inputLayoutName, inputLayoutPassword, inputLayoutPasswordOld, inputLayoutPasswordNew, inputLayoutPasswordNewAgain;
    private RecyclerView recyclerProducts;
    private ArrayList<Products> productsArrayList = new ArrayList<>();
    private RecyclerViewAdapter adapter;
    private LoadingView loadingLogin;
    private int positionClick;
    private CheckBox checkLogin;
    private String passWordStore = "", userNameStore = "";

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
        checkLogin();
        Log.d(MyMethod.LOG_EDMS, "onCreateViewFragment end");
        return v;
    }


    private void checkLogin() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean checkLoginValue = preferences.getBoolean(MyMethod.SHAREDPREFERENCE_KEY, false);
        userNameStore = preferences.getString(MyMethod.SHAREDPREFERENCE_User, "");
        passWordStore = preferences.getString(MyMethod.SHAREDPREFERENCE_Pass, "");
        if (!checkLoginValue)
            showLayout(Layouts.LogIn);
        else {
            showLayout(Layouts.Main);
        }
    }


    private void event(final View v) {
        v.findViewById(R.id.btnCheckIn).setOnClickListener(this);
        v.findViewById(R.id.btnCheckOut).setOnClickListener(this);
        v.findViewById(R.id.btnNotify).setOnClickListener(this);
        v.findViewById(R.id.btnLocation).setOnClickListener(this);
        v.findViewById(R.id.btnSetting).setOnClickListener(this);
        v.findViewById(R.id.btn_signin).setOnClickListener(this);
        v.findViewById(R.id.btn_changepass).setOnClickListener(this);
        v.findViewById(R.id.btnOrder).setOnClickListener(this);
        v.findViewById(R.id.btnReport).setOnClickListener(this);
        v.findViewById(R.id.btnSetting).setOnClickListener(this);
        v.findViewById(R.id.btnUpdate).setOnClickListener(this);
        v.findViewById(R.id.btn_get_checkin).setOnClickListener(this);
        v.findViewById(R.id.btn_get_photo).setOnClickListener(this);
        v.findViewById(R.id.btn_save_send_checkin).setOnClickListener(this);
        editName.addTextChangedListener(new MyTextWatcher(editName));
        editPass.addTextChangedListener(new MyTextWatcher(editPass));
        editPassOld.addTextChangedListener(new MyTextWatcher(editPassOld));
        editPassNew.addTextChangedListener(new MyTextWatcher(editPassNew));
        editPassNewAgain.addTextChangedListener(new MyTextWatcher(editPassNewAgain));
        recyclerProducts.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), this));

    }

    private void getId(View v) {

        checkLogin = (CheckBox) v.findViewById(R.id.checkLogin);
        loadingLogin = (LoadingView) v.findViewById(R.id.loginLoadingView);
        editPassNew = (EditText) v.findViewById(R.id.input_password_new);
        editPassOld = (EditText) v.findViewById(R.id.input_password_old);
        editPassNewAgain = (EditText) v.findViewById(R.id.input_password_new_again);
        inputLayoutPasswordOld = (TextInputLayout) v.findViewById(R.id.input_layout_password_old);
        inputLayoutPasswordNew = (TextInputLayout) v.findViewById(R.id.input_layout_password_new);
        inputLayoutPasswordNewAgain = (TextInputLayout) v.findViewById(R.id.input_layout_password_new_again);
        editName = (EditText) v.findViewById(R.id.input_name);
        editPass = (EditText) v.findViewById(R.id.input_password);
        inputLayoutName = (TextInputLayout) v.findViewById(R.id.input_layout_name);
        inputLayoutPassword = (TextInputLayout) v.findViewById(R.id.input_layout_password);
        Home.linearLogin = (LinearLayout) v.findViewById(R.id.linear_login);
        Home.linearChangePass = (LinearLayout) v.findViewById(R.id.linear_change_pass);
        Home.linearListOrder = (LinearLayout) v.findViewById(R.id.linear_list_order);
        recyclerProducts = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerProducts.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(v.getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerProducts.setLayoutManager(manager);
        adapter = new RecyclerViewAdapter(productsArrayList, v.getContext());
        recyclerProducts.setAdapter(adapter);
        Home.rela_checkout = (RelativeLayout) v.findViewById(R.id.rela_layout_checkout);
        Home.rela_checkin = (RelativeLayout) v.findViewById(R.id.rela_layout_checkin);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_checkin:
                showLayout(Layouts.Map);
                break;
            case R.id.btn_get_photo:
                //GET CAMERA PHOTO
                break;
            case R.id.btn_save_send_checkin:
                //SAVE AND SEND CHECK IN DATA
                break;
            case R.id.btnCheckIn:
                showLayout(Layouts.CheckIn);
                break;
            case R.id.btnCheckOut:
                showLayout(Layouts.CheckOut);
                break;
            case R.id.btnLocation:
                showLayout(Layouts.Location);
                break;
            case R.id.btnReport:
                showLayout(Layouts.Notify);
                break;
            case R.id.btn_signin:
                //XU LY DANG NHAP
                submitFormLogin(v);

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
                adapter.notifyDataSetChanged();
                EventPool.control().enQueue(new EventType.EventLoadRequest());
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        positionClick = position;
        showLayout(Layouts.MenuOrderClick);
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

    private boolean isRunning = false;
    private Handler handler = new Handler();
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


    private enum Layouts {
        CheckIn, CheckOut, Notify, Location, Setting, LogIn, ChangePass, ListOrder, Main, MenuOrderClick, Map
    }

    private void showLayout(Layouts layout) {
        switch (layout) {
            case CheckIn:
                Home.rela_checkin.setVisibility(View.VISIBLE);
                Home.rela_main.setVisibility(View.GONE);
                Home.linearLogin.setVisibility(View.GONE);
                Home.linearChangePass.setVisibility(View.GONE);
                Home.linearListOrder.setVisibility(View.GONE);
                break;
            case CheckOut:
                Home.rela_checkout.setVisibility(View.VISIBLE);
                Home.rela_main.setVisibility(View.GONE);
                Home.linearLogin.setVisibility(View.GONE);
                Home.linearChangePass.setVisibility(View.GONE);
                Home.linearListOrder.setVisibility(View.GONE);
                break;
            case Notify:
                break;
            case Location:
                break;
            case Setting:
                break;
            case LogIn:
                //show hide layouts....
                loadingLogin.setLoading(false);
                Home.rela_checkin.setVisibility(View.GONE);
                Home.rela_checkout.setVisibility(View.GONE);
                Home.rela_main.setVisibility(View.GONE);
                Home.linearLogin.setVisibility(View.VISIBLE);
                Home.linearChangePass.setVisibility(View.GONE);
                Home.linearListOrder.setVisibility(View.GONE);
                break;
            case ChangePass:
                Home.rela_checkin.setVisibility(View.GONE);
                Home.rela_checkout.setVisibility(View.GONE);
                Home.rela_main.setVisibility(View.GONE);
                Home.linearLogin.setVisibility(View.GONE);
                Home.linearChangePass.setVisibility(View.VISIBLE);
                Home.linearListOrder.setVisibility(View.GONE);
                break;
            case Map:
                Home.rela_checkin.setVisibility(View.GONE);
                Home.rela_checkout.setVisibility(View.GONE);
                Home.rela_main.setVisibility(View.GONE);
                Home.linearLogin.setVisibility(View.GONE);
                Home.linearChangePass.setVisibility(View.GONE);
                Home.linearListOrder.setVisibility(View.GONE);
                break;
            case ListOrder:
                //
                Home.rela_checkin.setVisibility(View.GONE);
                Home.rela_checkout.setVisibility(View.GONE);
                Home.rela_main.setVisibility(View.GONE);
                Home.linearLogin.setVisibility(View.GONE);
                Home.linearChangePass.setVisibility(View.GONE);
                Home.linearListOrder.setVisibility(View.VISIBLE);
                break;
            case Main:
                loadingLogin.setLoading(false);
                Home.rela_checkin.setVisibility(View.GONE);
                Home.rela_checkout.setVisibility(View.GONE);
                Home.rela_main.setVisibility(View.VISIBLE);
                Home.linearLogin.setVisibility(View.GONE);
                Home.linearChangePass.setVisibility(View.GONE);
                Home.linearListOrder.setVisibility(View.GONE);
                break;
            case MenuOrderClick:
                ArrayAdapter<String> dialogadapter = new ArrayAdapter<>(getContext(), R.layout.dialog_menu_custom);

                dialogadapter.add(getString(R.string.check_in));
                dialogadapter.add(getString(R.string.check_out));
                dialogadapter.add(getString(R.string.cancel));
                Home.dialog = DialogPlus.newDialog(getContext())
                        .setAdapter(dialogadapter)
                        .setHeader(R.layout.header_dialog)
                        .setFooter(R.layout.footer_dialog)
                        .setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                            }
                        })
                        .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                        .create();
                Home.dialog.findViewById(R.id.footer_dialog).requestFocus();
                Home.dialog.show();
                ((TextView) Home.dialog.findViewById(R.id.header_dialog)).setText(productsArrayList.get(positionClick).getProductName_());

                break;
        }
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

    private boolean isVisible(Layouts layouts) {
        boolean result = false;
        switch (layouts) {
            case ChangePass:
                result = (MyMethod.isVisible(Home.linearChangePass)) ? true : false;
                break;
            case LogIn:
                result = (MyMethod.isVisible(Home.linearLogin)) ? true : false;
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
            case Login:
                EventType.EventLoginResult loginResult = (EventType.EventLoginResult) event;
                if (loginResult.success) {
                    MyMethod.showToast(getContext(), getString(R.string.sign_in_success));
                    loadingLogin.setLoading(false);
                    //SAVE IF CHECKED CHECKBOX
                    MyMethod.savePreferences(getContext(), MyMethod.SHAREDPREFERENCE_KEY, checkLogin.isChecked());
                    if (checkLogin.isChecked()) {
                        MyMethod.savePreferences(getContext(), MyMethod.SHAREDPREFERENCE_User, editName.getText().toString());
                        MyMethod.savePreferences(getContext(), MyMethod.SHAREDPREFERENCE_Pass, editPass.getText().toString());
                    }
                    //
                    showLayout(Layouts.Main);
                } else {
                    MyMethod.showToast(getContext(), getString(R.string.sign_in_fail));
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
            default:
                Log.w("View_processEvent", "unhandled " + event.type);
                break;
        }
    }
}
