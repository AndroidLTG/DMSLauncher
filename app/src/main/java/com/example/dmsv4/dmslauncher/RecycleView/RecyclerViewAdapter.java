package com.example.dmsv4.dmslauncher.RecycleView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.dmsv4.dmslauncher.R;

import java.util.List;


/**
 * Created by DMSv4 on 8/8/2015.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ContactViewHolder> {
    private List<Products> ProductList;
    private Context context;

    public RecyclerViewAdapter(List<Products> ProductList, Context context) {
        this.ProductList = ProductList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return ProductList.size();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
        Products ci = ProductList.get(i);
        contactViewHolder.productName.setText(ci.getProductName_());
        contactViewHolder.productNo.setText(ci.getProductNo_());
        contactViewHolder.productAmount.setText(ci.getProductAmount_() + context.getString(R.string.money));
        contactViewHolder.productType.setText(ci.getProductType_());
        contactViewHolder.productQuantity.setText(ci.getProductQuantity_() + "");
    }

    @Override
    public ContactViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
        final View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_order, viewGroup, false);
        return new ContactViewHolder(itemView);
    }


    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        protected TextView productName, productNo, productAmount, productQuantity, productType;

        public ContactViewHolder(View v) {
            super(v);
            productName = (TextView) v.findViewById(R.id.order_name);
            productNo = (TextView) v.findViewById(R.id.order_no);
            productAmount = (TextView) v.findViewById(R.id.order_price);
            productQuantity = (TextView) v.findViewById(R.id.order_quantity);
            productType = (TextView) v.findViewById(R.id.order_type);
//Set clicked in cardview


        }
    }

}

