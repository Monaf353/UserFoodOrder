package com.develop.windexit.user;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.develop.windexit.user.Common.Common;
import com.develop.windexit.user.Database.Database;
import com.develop.windexit.user.Model.MyResponse;
import com.develop.windexit.user.Model.Notification;
import com.develop.windexit.user.Model.Order;
import com.develop.windexit.user.Model.Request;
import com.develop.windexit.user.Model.Sender;
import com.develop.windexit.user.Model.Token;
import com.develop.windexit.user.Remote.APIService;
import com.develop.windexit.user.ViewHolder.CardAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import in.shadowfax.proswipebutton.ProSwipeButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Cart extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;


    DatabaseReference requests;

    ProSwipeButton btnPlace;


    public TextView txtTotalPrice;

    List<Order> cart = new ArrayList<>();
    CardAdapter adapter;



    String address, comment;



    APIService mService;
    // private IGeoCoordinates mService;




    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Font
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto_Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_cart);


        mService = Common.getFCMService();

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Request");


        //init
        recyclerView = findViewById(R.id.listCart);
        // recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(), R.anim.layout_from_right);
        recyclerView.setLayoutAnimation(controller);

        txtTotalPrice = (TextView) findViewById(R.id.total);

        btnPlace = (ProSwipeButton) findViewById(R.id.btnPlaceOrder);
        btnPlace.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
            @Override
            public void onSwipeConfirm() {
                // user has swiped the btn. Perform your async operation now
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // task success! show TICK icon in ProSwipeButton
                        if (cart.size() > 0) {
                            showAlertDialog();
                            btnPlace.showResultIcon(true);
                        } else {
                            Toast.makeText(Cart.this, "Your card is empty !!", Toast.LENGTH_SHORT).show();
                           btnPlace.showResultIcon(false);
                        }

                        // false if task failed
                    }
                }, 1000);
            }
        });

        btnPlace.setSwipeDistance(0.6f);

        loadListFood();


    }



    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One more step!");
        alertDialog.setMessage("Enter address and write a note!");


        final LayoutInflater inflater = this.getLayoutInflater();
        View order_address_comment = inflater.inflate(R.layout.order_address_comment, null);

        final MaterialEditText edtAddress = order_address_comment.findViewById(R.id.edtAddress);


        final MaterialEditText edtComment = order_address_comment.findViewById(R.id.edtComment);

        //radio
        final RadioButton rdiHomeAddress = (RadioButton) order_address_comment.findViewById(R.id.rdihomeToAddress);


       /* //Payment
        final RadioButton rdiCOD = (RadioButton) order_address_comment.findViewById(R.id.rdiCOD);
        final RadioButton rdiPaypal = (RadioButton) order_address_comment.findViewById(R.id.rdiPaypal);
        final RadioButton rdiBuyItBlance = (RadioButton) order_address_comment.findViewById(R.id.rdiBuyItBalance);
*/

        //eventradio
        rdiHomeAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (Common.currentUser.getHomeAddress() != null || !TextUtils.isEmpty(Common.currentUser.getHomeAddress())) {
                        address = Common.currentUser.getHomeAddress();
                        edtAddress.setText(address);
                    } else {
                        Toast.makeText(Cart.this, "Please update your home address", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });



        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_add_shopping_cart_black_24dp);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                comment = edtComment.getText().toString();


                if (!rdiHomeAddress.isChecked())
                {
                    address=edtAddress.getText().toString();

                }
                if (TextUtils.isEmpty(address)) {
                    Toast.makeText(Cart.this, "Select option Or Enter address", Toast.LENGTH_SHORT).show();
                    return;
                }


                /*//check payment
                if (!rdiCOD.isChecked() && !rdiPaypal.isChecked() && !rdiBuyItBlance.isChecked()) //if both COD and Paypal and Buy Balance not checked
                {
                    Toast.makeText(Cart.this, "Please select option", Toast.LENGTH_SHORT).show();
                    //Remove fragment
                    getFragmentManager().beginTransaction()
                            .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                            .commit();
                    return;
                } else if (rdiPaypal.isChecked()) {

                    //show paypal to payment
                    String formatAmount = txtTotalPrice.getText().toString()
                            .replace("BDT", "$")
                            .replace("$", "")
                            .replace(",", "");
                    // float amount = Float.parseFloat(formatAmount);
                    PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(formatAmount),
                            "USD",
                            "Final Project Order",
                            PayPalPayment.PAYMENT_INTENT_SALE);

                    Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                    intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                    intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
                    startActivityForResult(intent, PAYPAL_REQUEST_CODE);
                } else if (rdiCOD.isChecked()) {*/

                    Request request = new Request(
                            Common.currentUser.getPhone(),
                            Common.currentUser.getName(),
                            address,
                            txtTotalPrice.getText().toString(),
                            "0",
                            comment,
                            "COD!",
                            "Unpaid",
                            //jsonObject.getJSONObject("response").getString("state"),
                            //latLng string format... mLastLocation.getLongitude hole own location hoito but now
                            //String.format("%s,%s",shippingAddress.getLatLng().latitude,shippingAddress.getLatLng().longitude),
                            Common.date(),
                            cart);


                    //Common.currentRequest = request;

                    String order_number = String.valueOf(System.currentTimeMillis());
                    //submit firebase
                    requests.child(order_number)
                            .setValue(request);

                    //delete cart
                    new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());
                    // adapter.notifyDataSetChanged();

                    sendNotificationOrder(order_number);

                    Toast.makeText(Cart.this, "Thank you, Order Place!!", Toast.LENGTH_LONG).show();
                    finish();


               /* } else if (rdiBuyItBlance.isChecked()) {
                    double amount = 0;
                    //first we will get total price from txttotalprice

                    //amount = Double.parseDouble(txtTotalPrice.getText().toString());
                    Locale locale = new Locale("bd", "BD");
                    try {
                        amount = Common.formatCurrency(txtTotalPrice.getText().toString(), locale).doubleValue();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (Double.parseDouble(Common.currentUser.getBalance().toString()) >= amount) {

                        Request request = new Request(
                                Common.currentUser.getPhone(),
                                Common.currentUser.getName(),
                                address,
                                txtTotalPrice.getText().toString(),
                                "0",
                                comment,
                                "Desh Balance",
                                "Paid",
                                //jsonObject.getJSONObject("response").getString("state"),
                                //latLng string format... mLastLocation.getLongitude hole own location hoito but now
                                //String.format("%s,%s",shippingAddress.getLatLng().latitude,shippingAddress.getLatLng().longitude),
                                String.format("%s,%s", mLastLocation.getLatitude(), mLastLocation.getLongitude()),
                                Common.date(),
                                cart);

                        //Common.currentRequest = request;


                        final String order_number = String.valueOf(System.currentTimeMillis());
                        //submit firebase
                        requests.child(order_number)
                                .setValue(request);

                        //delete cart
                        new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());
                        // adapter.notifyDataSetChanged();

                        //Update balance
                        double balance = Double.parseDouble(Common.currentUser.getBalance().toString()) - amount;
                        Map<String, Object> update_balance = new HashMap<>();
                        update_balance.put("balance", balance);

                        FirebaseDatabase.getInstance().getReference("User")
                                .child(Common.currentUser.getPhone())
                                .updateChildren(update_balance)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseDatabase.getInstance().getReference("User")
                                                    .child(Common.currentUser.getPhone())
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Common.currentUser = dataSnapshot.getValue(User.class);
                                                            sendNotificationOrder(order_number);
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                        }
                                    }
                                });
                        Toast.makeText(Cart.this, "Thank you, order Place", Toast.LENGTH_LONG).show();
                        finish();

                    } else {
                        Toast.makeText(Cart.this, "Your balance not enough , Please choose other payment", Toast.LENGTH_LONG).show();

                    }
                }
               */


            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();


            }
        });
        alertDialog.show();
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        String paymentDetail = confirmation.toJSONObject().toString(4);
                        JSONObject jsonObject = new JSONObject(paymentDetail);

                        Request request = new Request(
                                Common.currentUser.getPhone(),
                                Common.currentUser.getName(),
                                address,
                                txtTotalPrice.getText().toString(),
                                "0",
                                comment,
                                "Paypal",
                                jsonObject.getJSONObject("response").getString("state"),
                                //latLng string format... mLastLocation.getLongitude hole own location hoito but now
                                String.format("%s,%s", shippingAddress.getLatLng().latitude, shippingAddress.getLatLng().longitude),
                                Common.date(),
                                cart);

                        //Common.currentRequest = request;

                        String order_number = String.valueOf(System.currentTimeMillis());
                        //submit firebase
                        requests.child(order_number)
                                .setValue(request);

                        //delete cart
                        new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());
                        // adapter.notifyDataSetChanged();

                        sendNotificationOrder(order_number);

                        Toast.makeText(Cart.this, "Thank you, order Place", Toast.LENGTH_LONG).show();
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(Cart.this, "payment cancle", Toast.LENGTH_LONG).show();
            else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
                Toast.makeText(Cart.this, "invalid payment", Toast.LENGTH_LONG).show();
        }
    }*/

    private void sendNotificationOrder(final String order_number) {

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");

        final Query data = tokens.orderByChild("serverToken").equalTo(true); //get all node with isServerToken is true
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {

                    Token serverToken = postSnapShot.getValue(Token.class);

                    Notification notification = new Notification("CLIENT", "You have new order" + order_number);

                    // assert serverToken != null;
                    Sender content = new Sender(serverToken.getToken(), notification);

                    mService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success == 1) {
                                            Toast.makeText(Cart.this, "Thank you , order Place", Toast.LENGTH_LONG).show();
                                            finish();
                                        } else {
                                            Toast.makeText(Cart.this, "Failed", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("Error", t.getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadListFood() {

        cart = new Database(this).getCarts(Common.currentUser.getPhone());
        adapter = new CardAdapter(cart, this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        //calculate total price
        int total = 0;
        for (Order order : cart) {

            //total = total + ( (Integer.parseInt(order.getPrice())) - (((Integer.parseInt(order.getDiscount()))/(Integer.parseInt(order.getPrice())))*(Integer.parseInt(order.getPrice())))) * (Integer.parseInt(order.getQuantity()));
            total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));
        }

        // Locale locale = new Locale("en", "US");
        Locale locale = new Locale("bd", "BD");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        txtTotalPrice.setText(fmt.format(total));
        //txtTotalPrice.setText(total);

        /*adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);*/

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return super.onContextItemSelected(item);
    }

    private void deleteCart(int position) {
        cart.remove(position);
        new Database(this).cleanCart(Common.currentUser.getPhone());

        for (Order item : cart)
            new Database(this).addToCart(item);
        loadListFood();
    }


}
