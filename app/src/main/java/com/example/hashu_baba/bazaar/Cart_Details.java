package com.example.hashu_baba.bazaar;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Cart_Details extends AppCompatActivity{

    ListView orderslist,orderslist1;
    Cart_Details_Adapter mAdapter;
    Total_View_Adapter nAdapter;
    ArrayList<NewProduct> orderlist;
    ArrayList<NewTotal> orderlist1;
    HashMap<Integer, NewProduct> OrderMap;
    HashMap<Integer, NewTotal> OrderMap1;
    String macaddress;
    Integer GrandTotal=0;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart);

        orderslist = (ListView)findViewById(R.id.cart_view);
        orderslist1 = (ListView)findViewById(R.id.cart_view2);
        orderlist = new ArrayList<NewProduct>();
        orderlist1 = new ArrayList<NewTotal>();
        mAdapter = new Cart_Details_Adapter(Cart_Details.this,orderlist);
        nAdapter = new Total_View_Adapter(Cart_Details.this,orderlist1);
        OrderMap = new HashMap<Integer, NewProduct>();
        OrderMap1 = new HashMap<Integer, NewTotal>();

        orderslist.setAdapter(mAdapter);
        orderslist1.setAdapter(nAdapter);

        macaddress = getMacAddr();
        //Load data from sentOrder table
        FirebaseDatabase.getInstance().getReference("Users").child(macaddress).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                String name = dataSnapshot.child("productName").getValue().toString();
                String price = dataSnapshot.child("productPrice").getValue().toString();
                String quantity = dataSnapshot.child("quantity").getValue().toString();
                Integer q = Integer.parseInt(quantity);
                Integer p = Integer.parseInt(price);
                Integer Total = q*p;
                GrandTotal =  GrandTotal+Total;


                NewProduct order = new NewProduct(name,p,q,Total);
                NewTotal order1 = new NewTotal(Total,GrandTotal);
                OrderMap.put(orderlist.size(),order);
                OrderMap1.put(orderlist1.size(),order1);
                orderlist.add(order);
                orderlist1.add(order1);
                mAdapter.notifyDataSetChanged();
                nAdapter.notifyDataSetChanged();

            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }


    public String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }
}
