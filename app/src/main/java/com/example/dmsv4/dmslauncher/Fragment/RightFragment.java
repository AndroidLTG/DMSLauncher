package com.example.dmsv4.dmslauncher.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dmsv4.dmslauncher.Home;
import com.example.dmsv4.dmslauncher.R;
import com.example.dmsv4.dmslauncher.RecycleView.Products;
import com.example.dmsv4.dmslauncher.RecycleView.RecyclerItemClickListener;
import com.example.dmsv4.dmslauncher.RecycleView.RecyclerViewAdapter;
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
    private static final String LOG_EDMS = "eDMS LifeCycle";
    private static final String SHAREDPREFERENCE_KEY = "CheckLogin_Value";
    private static final String SHAREDPREFERENCE_User = "UserName_Value";
    private static final String SHAREDPREFERENCE_Pass = "PassWord_Value";
    private RelativeLayout rela_checkin, rela_main, rela_checkout, rela_setting, rela_notify, rela_location;
    private EditText editName, editPass, editPassOld, editPassNew, editPassNewAgain;
    private TextInputLayout inputLayoutName, inputLayoutPassword, inputLayoutPasswordOld, inputLayoutPasswordNew, inputLayoutPasswordNewAgain;
    private RecyclerView recyclerProducts;
    private ArrayList<Products> productsArrayList = new ArrayList<>();
    private RecyclerViewAdapter adapter;
    private LoadingView loadingLogin;
    private int positionClick;
    private LinearLayout linearLogin, linearChangePass, linearListOrder;
    private CheckBox checkLogin;
    private String passWordStore = "", userNameStore = "";


    public RightFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.w(LOG_EDMS, "onCreateViewFragment begin");

        View v = inflater.inflate(R.layout.fragment_right, container, false);
        getId(v);
        event(v);
        checkLogin();
        Log.d(LOG_EDMS, "onCreateViewFragment end");
        return v;
    }

    private void checkLogin() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean checkLoginValue = preferences.getBoolean(SHAREDPREFERENCE_KEY, false);
        userNameStore = preferences.getString(SHAREDPREFERENCE_User, "");
        passWordStore = preferences.getString(SHAREDPREFERENCE_Pass, "");
        if (!checkLoginValue)
            showLayout(Layouts.LogIn);
        else {
            showLayout(Layouts.Main);
        }
    }

    private void savePreferences(String key, boolean value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    private void savePreferences(String key, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void event(View v) {
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
        linearLogin = (LinearLayout) v.findViewById(R.id.linear_login);
        linearChangePass = (LinearLayout) v.findViewById(R.id.linear_change_pass);
        linearListOrder = (LinearLayout) v.findViewById(R.id.linear_list_order);
        recyclerProducts = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerProducts.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(v.getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerProducts.setLayoutManager(manager);
        adapter = new RecyclerViewAdapter(productsArrayList, v.getContext());
        recyclerProducts.setAdapter(adapter);
        rela_checkout = (RelativeLayout) v.findViewById(R.id.rela_layout_checkout);
        rela_checkin = (RelativeLayout) v.findViewById(R.id.rela_layout_checkin);
        rela_main = (RelativeLayout) v.findViewById(R.id.rela_layout_main);
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        v.setLayoutParams(p);
        v.requestLayout();
    }

    @Override
    public void onStop() {
        Log.w(LOG_EDMS, "onStopFragment begin");
        super.onStop();
        isRunning = false;
        Log.d(LOG_EDMS, "onStopFragment end");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.w(LOG_EDMS, "onViewStateRestoredFragment begin");
        isRunning = true;
        handler.postDelayed(runnable, Const.QueueTimerView);
        Log.w(LOG_EDMS, "onViewStateRestoredFragment end");
    }

    @Override
    public void onResume() {
        Log.w(LOG_EDMS, "onResumeFragment begin");
        super.onResume();
        isRunning = true;
        handler.postDelayed(runnable, Const.QueueTimerView);
        Log.w(LOG_EDMS, "onResumeFragment end");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        closeFocus(v);
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
        Toast.makeText(getContext(), "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
        closeFocus(v);
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
        CheckIn, CheckOut, Notify, Location, Setting, LogIn, ChangePass, ListOrder, Main, MenuOrderClick
    }

    private void showLayout(Layouts layout) {
        switch (layout) {
            case CheckIn:

                rela_checkin.setVisibility(View.VISIBLE);
                rela_main.setVisibility(View.GONE);
                linearLogin.setVisibility(View.GONE);
                linearChangePass.setVisibility(View.GONE);
                linearListOrder.setVisibility(View.GONE);
                break;
            case CheckOut:
                rela_checkout.setVisibility(View.VISIBLE);
                rela_main.setVisibility(View.GONE);
                linearLogin.setVisibility(View.GONE);
                linearChangePass.setVisibility(View.GONE);
                linearListOrder.setVisibility(View.GONE);
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
                rela_checkin.setVisibility(View.GONE);
                rela_checkout.setVisibility(View.GONE);
                rela_main.setVisibility(View.GONE);
                linearLogin.setVisibility(View.VISIBLE);
                linearChangePass.setVisibility(View.GONE);
                linearListOrder.setVisibility(View.GONE);
                break;
            case ChangePass:
                rela_checkin.setVisibility(View.GONE);
                rela_checkout.setVisibility(View.GONE);
                rela_main.setVisibility(View.GONE);
                linearLogin.setVisibility(View.GONE);
                linearChangePass.setVisibility(View.VISIBLE);
                linearListOrder.setVisibility(View.GONE);
                break;
            case ListOrder:
                //
                rela_checkin.setVisibility(View.GONE);
                rela_checkout.setVisibility(View.GONE);
                rela_main.setVisibility(View.GONE);
                linearLogin.setVisibility(View.GONE);
                linearChangePass.setVisibility(View.GONE);
                linearListOrder.setVisibility(View.VISIBLE);
                break;
            case Main:
                loadingLogin.setLoading(false);
                rela_checkin.setVisibility(View.GONE);
                rela_checkout.setVisibility(View.GONE);
                rela_main.setVisibility(View.VISIBLE);
                linearLogin.setVisibility(View.GONE);
                linearChangePass.setVisibility(View.GONE);
                linearListOrder.setVisibility(View.GONE);
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

    private boolean validateName() {
        if (editName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(editName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }
        return true;
    }


    private boolean validatePassword() {
        if (editPass.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(editPass);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePasswordOld() {
        if (editPassOld.getText().toString().trim().isEmpty()) {
            inputLayoutPasswordOld.setError(getString(R.string.err_msg_password_old));
            requestFocus(editPassOld);
            return false;
        } else {
            inputLayoutPasswordOld.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePasswordNew() {
        if (editPassNew.getText().toString().trim().isEmpty()) {
            inputLayoutPasswordNew.setError(getString(R.string.err_msg_password_new));
            requestFocus(editPassNew);
            return false;
        } else {
            inputLayoutPasswordNew.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePasswordNewAgain() {
        if (editPassNewAgain.getText().toString().trim().isEmpty()) {
            inputLayoutPasswordNewAgain.setError(getString(R.string.err_msg_password_new_again));
            requestFocus(editPassNewAgain);
            return false;
        } else if (!editPassNewAgain.getText().toString().equals(editPassNew.getText().toString())) {
            inputLayoutPasswordNewAgain.setError(getString(R.string.err_msg_password_not_match));
            requestFocus(editPassNewAgain);
            return false;
        } else {
            inputLayoutPasswordNewAgain.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void closeFocus(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void processEvent(EventType.EventBase event) {
        switch (event.type) {
            case Login:
                EventType.EventLoginResult loginResult = (EventType.EventLoginResult) event;
                if (loginResult.success) {
                    Toast.makeText(getContext(), getString(R.string.sign_in_success), Toast.LENGTH_SHORT).show();
                    loadingLogin.setLoading(false);
                    //SAVE IF CHECKED CHECKBOX
                    savePreferences(SHAREDPREFERENCE_KEY, checkLogin.isChecked());
                    if (checkLogin.isChecked()) {
                        savePreferences(SHAREDPREFERENCE_User, editName.getText().toString());
                        savePreferences(SHAREDPREFERENCE_Pass, editPass.getText().toString());
                    }
                    //
                    showLayout(Layouts.Main);
                } else {
                    Toast.makeText(getContext(), getString(R.string.sign_in_fail), Toast.LENGTH_SHORT).show();
                    loadingLogin.setLoading(false);
                    showLayout(Layouts.LogIn);
                }
                break;
            case ChangePass:
                EventType.EventChangeResult changeResult = (EventType.EventChangeResult) event;
                if (changeResult.success) {
                    Toast.makeText(getContext(), getString(R.string.change_pass_success), Toast.LENGTH_SHORT).show();
                    loadingLogin.setLoading(false);
                    showLayout(Layouts.LogIn);
                } else {
                    Toast.makeText(getContext(), getString(R.string.change_pass_fail), Toast.LENGTH_SHORT).show();
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
