package com.example.hashu_baba.bazaar;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import me.dm7.barcodescanner.zxing.ZXingScannerView;




public class MainActivity extends AppCompatActivity {


    ImageButton scanner2, scanner3;
    private ZXingScannerView scannerView;
    String Barcode = null;
    String macAddress;
    String quantity,name,price,name1,price1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanner2 = (ImageButton) findViewById(R.id.scanner2);
        scanner3 = (ImageButton) findViewById(R.id.scanner3);

        scanner3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "Connected to Database Successfully", Toast.LENGTH_SHORT);
                toast.setMargin(0, -5);
                toast.show();
            }
        });

        scanner2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, Cart_Details.class);
                startActivity(intent);

            }
        });



    }

    public void scanBar(View view) {

        scannerView = new ZXingScannerView(this);
        scannerView.setResultHandler(new ZXingScannerResultHandler());

        setContentView(scannerView);
        scannerView.startCamera();


    }


    class ZXingScannerResultHandler implements ZXingScannerView.ResultHandler {
        @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
        @Override
        public void handleResult(Result result) {
            Barcode = result.getText();
            macAddress = getMacAddr();

            FirebaseDatabase.getInstance().getReference("ProductDetails").child(Barcode).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                   name = dataSnapshot.child("ProductName").getValue().toString();
                   price = dataSnapshot.child("ProductPrice").getValue().toString();


                   AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Product Details");
                    final EditText input = new EditText(MainActivity.this);
                    input.setHint("Enter the Quantity");
                    input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                    alertDialog.setView(input);

                    alertDialog.setButton(Dialog.BUTTON_POSITIVE,"Add To Cart",new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            quantity = input.getText().toString();
                            Product product = new Product(name, price,quantity);
                            FirebaseDatabase.getInstance().getReference("Users").child(macAddress).child(Barcode).setValue(product);
                            Toast toast = Toast.makeText(getApplicationContext(), "Added to cart", Toast.LENGTH_SHORT);
                            toast.setMargin(0, 0);
                            toast.show();

                            }
                    });
                    alertDialog.setButton(Dialog.BUTTON_NEGATIVE,"Cancel",new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.setMessage("Product Name: " + name + "\nProduct Price: " + price);
                    alertDialog.show();

                    }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }


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
}




















