package com.customer.esweetcustomer.Holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.customer.esweetcustomer.R;


public class OrderHolder extends RecyclerView.ViewHolder{

    public TextView orderId;
    public TextView orderdate;
    public TextView orderS;
    public TextView orderamount;
    public ImageView status;
    public ImageView recieve;


    public OrderHolder(@NonNull View itemView) {
        super(itemView);

        orderId = itemView.findViewById(R.id.order_id);
        orderdate = itemView.findViewById(R.id.order_date);
        orderamount = itemView.findViewById(R.id.order_amount);
        status = itemView.findViewById(R.id.status_lbl);
        recieve = itemView.findViewById(R.id.recieved_lbl);
        orderS = itemView.findViewById(R.id.order_status);

    }
}