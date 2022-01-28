package com.customer.esweetcustomer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;


public class MapAddressActivity extends AppCompatActivity {


    private TextView textview_address;
    private MapsFragment MapsFragment;
    FragmentManager fm = getSupportFragmentManager();
    private Address setAddress;
    private int LOCATION_PERMISSION = 100;
    private double estimateValue;
    private String duration;
    private LatLng customerLocation;
    private LatLng destinationLocation;
    private TextView estimateTime;
    private TextView deliveryFee;
    private Button confirm_address_btn;
    private String maptotal;
    private String subtotal;
    private String totalbuynow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_address);
        MapsFragment = (MapsFragment) fm.findFragmentById(R.id.currentLocationfragment);
        textview_address = findViewById(R.id.textview_address);
        deliveryFee = findViewById(R.id.deliveryFee);
        estimateTime = findViewById(R.id.estimateTime);
        confirm_address_btn = findViewById(R.id.confirm_address_btn);
        Intent intent = getIntent();
        subtotal = intent.getStringExtra("total");
        totalbuynow = intent.getStringExtra("totalbuynow");
        Log.d("subtotalsubtotalsubtotalsubtotal>>>>>>>", "" + subtotal);

        confirm_address_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapAddressActivity.this, PaymentOptionActivity.class);
                intent.putExtra("textview_address", textview_address.getText().toString());
                intent.putExtra("deliveryFee", "" + estimateValue);
                intent.putExtra("maptotal", "" + subtotal);
                intent.putExtra("maptotalbuynow", "" + totalbuynow);
                startActivity(intent);

            }
        });


    }

    public void setAddress(Address address) {
        this.setAddress = address;
        textview_address.setText(String.valueOf(address.getAddressLine(0)));
    }


    public void setEstimateValue(double estimateValue) {

        this.estimateValue = estimateValue;
        deliveryFee.setText(String.valueOf(estimateValue));
    }

    public double getEstimateValue() {
        return estimateValue;
    }

    public void setDuration(String duration) {
        this.duration = duration;
        estimateTime.setText(String.valueOf(duration));
    }

    public String getDuration() {
        return duration;
    }

    public void setJobLatLang(LatLng customerlocation, LatLng destinationlocation) {
        this.customerLocation = customerlocation;
        this.destinationLocation = destinationlocation;
    }
}
