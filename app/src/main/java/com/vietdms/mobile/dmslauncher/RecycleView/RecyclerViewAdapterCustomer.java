package com.vietdms.mobile.dmslauncher.RecycleView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vietdms.mobile.dmslauncher.R;

import java.util.List;


/**
 * Created by ${LTG} was born ${10/12/1994}.
 */
public class RecyclerViewAdapterCustomer extends RecyclerView.Adapter<RecyclerViewAdapterCustomer.ContactViewHolder> {
    private List<Customer> CustomerList;
    private Context context;

    public RecyclerViewAdapterCustomer(List<Customer> CustomerList, Context context) {
        this.CustomerList = CustomerList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return CustomerList.size();
    }

    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
        Customer c = CustomerList.get(i);
        contactViewHolder.customerName.setText(c.getCustomerName_());
        contactViewHolder.customerNo.setText(c.getCustomerNo_());
        contactViewHolder.customerAddress.setText(c.getCustomerAddress_());
        contactViewHolder.customerPhoto.setImageBitmap(c.getCustomerPhoto_());
    }

    @Override
    public ContactViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
        final View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_customer, viewGroup, false);
        return new ContactViewHolder(itemView);
    }


    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        protected TextView customerName, customerNo, customerAddress;
        protected ImageView customerPhoto;

        public ContactViewHolder(View v) {
            super(v);
            customerName = (TextView) v.findViewById(R.id.customer_name);
            customerNo = (TextView) v.findViewById(R.id.customer_no);
            customerAddress = (TextView) v.findViewById(R.id.customer_address);
            customerPhoto = (ImageView) v.findViewById(R.id.customer_photo);
            //Set clicked in cardview
        }
    }

}

