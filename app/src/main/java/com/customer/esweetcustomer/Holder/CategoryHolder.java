package com.customer.esweetcustomer.Holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.customer.esweetcustomer.Interface.ItemClickListener;
import com.customer.esweetcustomer.R;


public class CategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView categoryName;
    public ImageView categoryImage;

    private ItemClickListener itemClickListener;

    public CategoryHolder(@NonNull View itemView) {
        super(itemView);


        categoryName = (TextView) itemView.findViewById(R.id.cateoryName);
        categoryImage = (ImageView) itemView.findViewById(R.id.category_image);
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
