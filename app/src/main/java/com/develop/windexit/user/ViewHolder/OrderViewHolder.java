package com.develop.windexit.user.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.develop.windexit.user.Interface.ItemCliclListener;
import com.develop.windexit.user.R;


/**
 * Created by WINDEX IT on 26-Feb-18.
 */

public class OrderViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtOrderId,txtOrderAddress,txtOrderStatus,txtOrderPhone,txtOrderComment,order_date;

    private ItemCliclListener itemCliclListener;

    public OrderViewHolder(View itemView)
    {
        super(itemView);
         txtOrderId =  itemView.findViewById(R.id.order_id);
         txtOrderAddress=  itemView.findViewById(R.id.order_address);
         txtOrderStatus=  itemView.findViewById(R.id.order_status);
         txtOrderPhone=  itemView.findViewById(R.id.order_phone);
         txtOrderComment = itemView.findViewById(R.id.order_comment);
         order_date = itemView.findViewById(R.id.order_date);
        //itemView.setOnClickListener(this);
    }


   public void setItemCliclListener(ItemCliclListener itemCliclListener) {
        this.itemCliclListener = itemCliclListener;
    }

    @Override
    public void onClick(View v) {
       itemCliclListener.onClick(v, getAdapterPosition(), false);

    }

}
