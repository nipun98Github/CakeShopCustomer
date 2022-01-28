package com.customer.esweetcustomer.Holder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.customer.esweetcustomer.Interface.ItemClickListener;
import com.customer.esweetcustomer.Model.MyCartModel;
import com.customer.esweetcustomer.R;

public class CartHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    public TextView cartItemName;
    public TextView cartItemTotalPrice;
    public TextView cartItemPrice;
    public Button cartItemPlusBtn;
    public TextView cartItemQty;
    public Button cartItemMinusBtn;
    public Button cartItemRemoveBtn;
    public ImageView cartImage;

    public MyCartModel myCartModel;

    private ItemClickListener itemClickListener;


    public CartHolder(@NonNull View itemView) {

        super(itemView);

        cartItemName = (TextView) itemView.findViewById(R.id.cartItemName);
        cartItemPrice = (TextView) itemView.findViewById(R.id.cartItemPrice);
        cartItemTotalPrice = (TextView) itemView.findViewById(R.id.cartItemTotalPrice);
        cartItemPlusBtn = (Button) itemView.findViewById(R.id.promtPlusBtn);
        cartItemQty = (TextView) itemView.findViewById(R.id.cartItemQty);
        cartItemMinusBtn = (Button) itemView.findViewById(R.id.promtMinusBtn);
        cartItemRemoveBtn = (Button) itemView.findViewById(R.id.cartItemRemoveBtn);
        cartImage = (ImageView) itemView.findViewById(R.id.cartImage);

        itemView.setOnClickListener(this);


    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);

    }

}
