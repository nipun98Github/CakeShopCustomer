package com.customer.esweetcustomer.Holder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.customer.esweetcustomer.Interface.ItemClickListener;
import com.customer.esweetcustomer.Model.MyCartModel;
import com.customer.esweetcustomer.Model.WishList;
import com.customer.esweetcustomer.R;

public class WishListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public final TextView wishListItemName;
    public final TextView wishListItemNote;
    public final TextView wishListItemPrice;
    public final Button wishlistAddToCartBtn;
    public final Button wishlistRemoveBtn;
    public final ImageView wishlistImage;
    public TextView editTextResult;
    public WishList wishList;
    private ItemClickListener itemClickListener;


    public WishListHolder(@NonNull View itemView) {
        super(itemView);

        wishListItemName = (TextView) itemView.findViewById(R.id.wishListItemName);
        wishListItemNote = (TextView) itemView.findViewById(R.id.wishListItemNote);
        wishListItemPrice = (TextView) itemView.findViewById(R.id.wishListItemPrice);
        wishlistAddToCartBtn = (Button) itemView.findViewById(R.id.wishlistAddToCartBtn);
        wishlistRemoveBtn = (Button) itemView.findViewById(R.id.wishlistRemoveBtn);
        wishlistImage = (ImageView) itemView.findViewById(R.id.wishlistImage);
        editTextResult=(TextView)itemView.findViewById(R.id.editTextResult);
        itemView.setOnClickListener(this);


    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {

    }
}
