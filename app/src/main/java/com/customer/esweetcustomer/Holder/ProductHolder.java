package com.customer.esweetcustomer.Holder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.customer.esweetcustomer.Interface.ItemClickListener;
import com.customer.esweetcustomer.Model.Product;
import com.customer.esweetcustomer.R;

public class ProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView ratingNumber;
    public TextView productNameField;
    public TextView productNoteField;
    public TextView productPriceField;
    public Button increaseButton;
    public TextView qtyField;
    public Button decreaseButton;
    public Button cartButton;
    public Button buyItNowButton;
    public ImageView productImage;
    public Button cartItemWishListBtn;

    public Product product;

    private ItemClickListener itemClickListener;


    public ProductHolder(@NonNull View itemView) {

        super(itemView);
         productNameField = (TextView) itemView.findViewById(R.id.productNameField);
        productNoteField = (TextView) itemView.findViewById(R.id.productNoteField);
        productPriceField = (TextView) itemView.findViewById(R.id.productPriceField);
        increaseButton = (Button) itemView.findViewById(R.id.productItemPlusBtn);
        qtyField =(TextView)  itemView.findViewById(R.id.qtyField);
        decreaseButton =(Button) itemView.findViewById(R.id.productItemMinusBtn);
        cartButton =(Button) itemView.findViewById(R.id.cartButton);
        buyItNowButton =(Button) itemView.findViewById(R.id.buyItNowButton);
        productImage =(ImageView) itemView.findViewById(R.id.productImage);
        cartItemWishListBtn =(Button) itemView.findViewById(R.id.cartItemWishListBtn);
        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);

    }

}
