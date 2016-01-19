package com.vietdms.mobile.dmslauncher.RecycleView;

import android.graphics.Bitmap;

/**
 * Created by ${LTG} was born ${10/12/1994}.
 */
public class Customer {
    private String customerNo_;
    private String customerName_;
    private String customerAddress_;
    private Bitmap customerPhoto_;

    public String getCustomerNo_() {
        return customerNo_;
    }

    public void setCustomerNo_(String customerNo_) {
        this.customerNo_ = customerNo_;
    }

    public String getCustomerName_() {
        return customerName_;
    }

    public void setCustomerName_(String customerName_) {
        this.customerName_ = customerName_;
    }

    public String getCustomerAddress_() {
        return customerAddress_;
    }

    public void setCustomerAddress_(String customerAddress_) {
        this.customerAddress_ = customerAddress_;
    }

    public Bitmap getCustomerPhoto_() {
        return customerPhoto_;
    }

    public void setCustomerPhoto_(Bitmap customerPhoto_) {
        this.customerPhoto_ = customerPhoto_;
    }
}
