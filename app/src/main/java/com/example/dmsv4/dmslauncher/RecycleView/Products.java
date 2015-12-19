package com.example.dmsv4.dmslauncher.RecycleView;

/**
 * Created by DMSv4 on 12/14/2015.
 */
public class Products {
    private String productNo_;
    private String productName_;
    private Float productAmount_;
    private Float productQuantity_;


    public Float getProductAmount_() {
        return productAmount_;
    }

    public void setProductAmount_(Float productAmount_) {
        this.productAmount_ = productAmount_;
    }

    public Float getProductQuantity_() {
        return productQuantity_;
    }

    public void setProductQuantity_(Float productQuantity_) {
        this.productQuantity_ = productQuantity_;
    }

    public String getProductType_() {
        return productType_;
    }

    public void setProductType_(String productType_) {
        this.productType_ = productType_;
    }

    private String productType_;

    public String getProductNo_() {
        return productNo_;
    }

    public void setProductNo_(String productNo_) {
        this.productNo_ = productNo_;
    }

    public String getProductName_() {
        return productName_;
    }

    public void setProductName_(String productName_) {
        this.productName_ = productName_;
    }
}
