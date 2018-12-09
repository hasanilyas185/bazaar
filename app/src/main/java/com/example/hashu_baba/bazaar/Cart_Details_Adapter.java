package com.example.hashu_baba.bazaar;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Cart_Details_Adapter extends ArrayAdapter<NewProduct> {

    private Context mContext;
    private ArrayList<NewProduct> orderlist ;
    Button update;


    public Cart_Details_Adapter(@NonNull Context context, ArrayList<NewProduct> list)
    {
        super(context,0,list);
        mContext=context;
        orderlist=list;

    }

    public Cart_Details_Adapter(Context context, int resource, List<NewProduct> items) {
        super(context, resource, items);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View v = convertView;
        View x = convertView;
       NewProduct currentOrder = orderlist.get(position);
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.cart_details, null);


        }

        ((TextView) v.findViewById(R.id.productName)).setText( "Name : " + currentOrder.getProductName());
        ((TextView) v.findViewById(R.id.productPrice)).setText( "Price : " + currentOrder.getProductPrice());
        ((TextView) v.findViewById(R.id.Quantity)).setText( "Quantity : " + currentOrder.getQuantity());
        ((TextView) v.findViewById(R.id.Total)).setText( "Total : " + currentOrder.getTotal());




      return v;


    }

}
