package com.example.hashu_baba.bazaar;


import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.dm7.barcodescanner.zxing.ZXingScannerView;




public class MainActivity extends AppCompatActivity {


    ImageButton scanner2, scanner3;
    private ZXingScannerView scannerView;
    String Barcode = null;
    String macAddress;
    String quantity,name,price,name1,price1;
    boolean scanning = false;
    ProgressDialog pd;
    TextView lblconn;
    boolean connected = false;
    Handler handler;
    Runnable runnable;
    final String PREFS_NAME = "MyPrefsFile";
    List<String> cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        cart = new ArrayList<>();

        SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        if (!settings.contains("first_time")) {
            //the app is being launched for first time, do something
            // first time task
            Intent intent = new Intent(MainActivity.this, tutorialActivity.class);
            startActivity(intent);
            // record the fact that the app has been started at least once
            settings.edit().putString("first_time", "").commit();
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 1);
        }

        lblconn = findViewById(R.id.connection);

        handler=new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                refresh(null);
                handler.postDelayed(this, 3000);
            }
        };
        startHandler();

        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setTitle("Bazaar product search");
        pd.setMessage("Fetch details. Please wait...");
        pd.setIndeterminate(true);
        pd.setCanceledOnTouchOutside(false);

        scanning = false;
        setContentView(R.layout.activity_main);

        scanner2 = (ImageButton) findViewById(R.id.scanner2);
        scanner3 = (ImageButton) findViewById(R.id.scanner3);
        refresh(new View(MainActivity.this));
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public void cart(View view)
    {
        if (!connected)
        {
            Toast.makeText(this, "Server connection failed! Please check your internet connection and Press REFRESH button!", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(MainActivity.this, Cart_Details.class);
        startActivity(intent);
    }

    public void refresh(final View vview)
    {
        if (isNetworkConnected())
            FirebaseDatabase.getInstance().getReference("ProductDetails").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (vview != null)
                {
                    Toast toast = Toast.makeText(getApplicationContext(), "Connected to Database Successfully", Toast.LENGTH_SHORT);
                    toast.setMargin(0, -5);
                    toast.show();

                    TextView tv = findViewById(R.id.connection);
                    tv.setText("Connected!");
                    tv.setTextColor(Color.parseColor("#008000"));
                }
                connected = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (vview != null)
                {
                    Toast toast = Toast.makeText(getApplicationContext(), "Connection failed!", Toast.LENGTH_SHORT);
                    toast.setMargin(0, -5);
                    toast.show();

                    TextView tv = findViewById(R.id.connection);
                    tv.setText("Connection failed!");
                    tv.setTextColor(Color.parseColor("#8B0000"));
                }
                connected = false;
            }
        });
        else
        {
            if (vview != null)
            {
                Toast toast = Toast.makeText(getApplicationContext(), "Connection failed!", Toast.LENGTH_SHORT);
                toast.setMargin(0, -5);
                toast.show();

                TextView tv = findViewById(R.id.connection);
                tv.setText("Connection failed!");
                tv.setTextColor(Color.parseColor("#8B0000"));
            }
            connected = false;
        }
    }

    public void scanBar(View view) {

        if (!connected)
        {
            Toast.makeText(this, "Server connection failed! Please check your internet connection!", Toast.LENGTH_SHORT).show();
            return;
        }

        scanning = true;
        scannerView = new ZXingScannerView(this);
        scannerView.setResultHandler(new ZXingScannerResultHandler());

        setContentView(scannerView);
        scannerView.startCamera();
    }

    private void stopHandler() {
        handler.removeCallbacks(runnable);
    }


    private void startHandler() {
        handler.postDelayed(runnable,3000);
    }

    @Override
    protected void onResume() {
        refresh(new View(MainActivity.this));
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (scanning)
        {
            setContentView(R.layout.activity_main);
            scannerView.stopCamera();
        }
        else
            super.onBackPressed();
    }

    class ZXingScannerResultHandler implements ZXingScannerView.ResultHandler {
        @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
        @Override
        public void handleResult(Result result) {


            Barcode = result.getText();
            macAddress = getMacAddr();
            pd.show();

            FirebaseDatabase.getInstance().getReference("ProductDetails").child(Barcode).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                   name = dataSnapshot.child("ProductName").getValue().toString();
                   price = dataSnapshot.child("ProductPrice").getValue().toString();


                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Product Details");
                    final EditText input = new EditText(MainActivity.this);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    input.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
                    input.setHint("Enter the Quantity");
                    input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                    alertDialog.setView(input);

                    alertDialog.setButton(Dialog.BUTTON_POSITIVE,"Add To Cart",new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            quantity = input.getText().toString();

                            if (quantity.equals(""))
                            {
                                Toast.makeText(MainActivity.this, "Quantity not entered!\nPlease insert quantity!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (Integer.parseInt(quantity) <= 0)
                            {
                                Toast.makeText(MainActivity.this, "Quantity must be greater than zero", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Product product = new Product(name, price,quantity);

                            if (!cart.contains(product.getProductName())) {
                                cart.add(product.getProductName());
                                FirebaseDatabase.getInstance().getReference("Users").child(macAddress).child(Barcode).setValue(product);
                            } else {
                                final AlertDialog warning = new AlertDialog.Builder(MainActivity.this).create();
                                warning.setTitle("Warning");
                                TextView tv = new TextView(MainActivity.this);
                                tv.setText("\n    Product is already present in cart\n\n        Quantity = " + product.getQuantity() + "\n    Select Any : ");
                                warning.setView(tv);
                                final String adup = String.valueOf(Integer.parseInt(product.getQuantity()) + Integer.parseInt(quantity));
                                warning.setButton(Dialog.BUTTON_POSITIVE, "ADD Quantities (" + adup+")", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        FirebaseDatabase.getInstance().getReference("Users").child(macAddress).child(Barcode).setValue(new Product(name, price, adup));
                                        warning.dismiss();
                                    }
                                });
                                warning.setButton(Dialog.BUTTON_NEGATIVE, "Set New Quantity (" + quantity+")", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        FirebaseDatabase.getInstance().getReference("Users").child(macAddress).child(Barcode).setValue(new Product(name, price, quantity));
                                        warning.dismiss();
                                    }
                                });
                                warning.show();
                                Toast toast = Toast.makeText(getApplicationContext(), "Added to cart\n Happy shopping!", Toast.LENGTH_SHORT);
                                toast.setMargin(0, 0);
                                toast.show();
                            }

                            Toast toast = Toast.makeText(getApplicationContext(), "Added to cart\n Happy shopping!", Toast.LENGTH_SHORT);
                            toast.setMargin(0, 0);
                            toast.show();

                            }
                    });
                    alertDialog.setButton(Dialog.BUTTON_NEGATIVE,"Cancel",new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, "Cancelled!\n Happy shopping!", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                    });
                        alertDialog.setMessage("Product Name: " + name + "\nProduct Price: " + price);
                        pd.hide();
                        alertDialog.show();
                    }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            scanning = false;
            setContentView(R.layout.activity_main);
            scannerView.stopCamera();
            refresh(new View(MainActivity.this));
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
}




















