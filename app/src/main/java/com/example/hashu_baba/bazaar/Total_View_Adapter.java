package com.example.hashu_baba.bazaar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Total_View_Adapter extends ArrayAdapter<NewTotal> {

    private Context mContext;
    private ArrayList<NewTotal> orderlist1 ;


    public Total_View_Adapter(@NonNull Context context, ArrayList<NewTotal> list)
    {
        super(context,0,list);
        mContext=context;
        orderlist1=list;

    }

    public Total_View_Adapter(Context context, int resource, List<NewTotal> items) {
        super(context, resource, items);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View v = convertView;
        NewTotal currentOrder = orderlist1.get(position);
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.total, null);


        }

        ((TextView) v.findViewById(R.id.GrandTotal)).setText( "GrandTotal : Rs." + currentOrder.getGrandTotal() + "/-");
        return v;
    }

}
