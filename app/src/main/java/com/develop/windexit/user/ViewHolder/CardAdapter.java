package com.develop.windexit.user.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.develop.windexit.user.Cart;
import com.develop.windexit.user.Common.Common;
import com.develop.windexit.user.Database.Database;
import com.develop.windexit.user.Interface.ItemCliclListener;
import com.develop.windexit.user.Model.Order;
import com.develop.windexit.user.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by WINDEX IT on 24-Feb-18.
 */

class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

    TextView txt_cart_name, txt_price;
    ElegantNumberButton btn_quantity;
    ItemCliclListener itemCliclListener;
    ImageView cart_Image;

    public CardViewHolder(View itemView, TextView txt_cart_name, TextView txt_price, ElegantNumberButton btn_quantity, ItemCliclListener itemCliclListener, ImageView cart_Image) {
        super(itemView);

        this.txt_cart_name = txt_cart_name;
        this.txt_price = txt_price;
        this.btn_quantity = btn_quantity;
        this.itemCliclListener = itemCliclListener;
        this.cart_Image = cart_Image;
    }

 /*   public void setTxt_cart_name(TextView txt_cart_name) {
        this.txt_cart_name = txt_cart_name;
    }*/

    public CardViewHolder(View itemView) {
        super(itemView);

        txt_cart_name = (TextView) itemView.findViewById(R.id.card_item_name);
        txt_price = (TextView) itemView.findViewById(R.id.card_item_price);
        btn_quantity = (ElegantNumberButton) itemView.findViewById(R.id.btn_quantity);
        cart_Image = itemView.findViewById(R.id.cart_image);

        itemView.setOnCreateContextMenuListener(this);
        //itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select action");
        menu.add(0, 0, getAdapterPosition(), Common.DELETE);
    }
}

public class CardAdapter extends RecyclerView.Adapter<CardViewHolder> {

    private List<Order> listData = new ArrayList<>();
    private Cart cart;

    public CardAdapter(List<Order> listData, Cart cart) {
        this.listData = listData;
        this.cart = cart;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(cart);
        View itemView = inflater.inflate(R.layout.cart_layout, parent, false);
        return new CardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, final int position) {
        /*TextDrawable drawable = TextDrawable.builder().buildRound("" + listData.get(position).getQuantity(), Color.RED);
        holder.btn_quantity.setImageDrawable(drawable);
*/
        Picasso.with(cart.getBaseContext())
                .load(listData.get(position).getImage())
                .resize(70, 70)
                .centerCrop()
                .into(holder.cart_Image);

        holder.btn_quantity.setNumber(listData.get(position).getQuantity());
        holder.btn_quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Order order = listData.get(position);

                order.setQuantity(String.valueOf(newValue));
                new Database(cart).updateCart(order);


                //Update text total
                int total = 0;
                List<Order> orders = new Database(cart).getCarts(Common.currentUser.getPhone());
                for (Order item : orders)
                   //total = total+((Integer.parseInt(item.getPrice())) - (((Integer.parseInt(item.getDiscount()))/(Integer.parseInt(item.getPrice())))* (Integer.parseInt(item.getPrice())))) * (Integer.parseInt(item.getQuantity()));
                total += (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));

                Locale locale = new Locale("bd", "BD");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                cart.txtTotalPrice.setText(fmt.format(total));
            }
        });
        Locale locale = new Locale("bd", "BD");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(listData.get(position).getPrice())) * (Integer.parseInt(listData.get(position).getQuantity()));
        holder.txt_price.setText(fmt.format(price));
        holder.txt_cart_name.setText(listData.get(position).getProductName());

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


}
