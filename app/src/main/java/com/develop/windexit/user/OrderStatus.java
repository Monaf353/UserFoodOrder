package com.develop.windexit.user;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.develop.windexit.user.Common.Common;
import com.develop.windexit.user.Model.Request;
import com.develop.windexit.user.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrderStatus extends AppCompatActivity {


    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference requests;
    SwipeRefreshLayout swipeRefreshLayout;

    TextView totalCount, todayOrder;
    long size, size2;

    Spinner spinner;
    int mYear, mMonth, mDay;
    String startDate, endDate;
    TextView edtStartDate, edtEndDate;
    Button selectDateStart, selectDateEnd;

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

        setContentView(R.layout.activity_order_status);

        /* toolbar =  findViewById(R.id.sylBack);
        setSupportActionBar(toolbar);*/
        getSupportActionBar().setTitle("Orders");
        setTitleColor(Color.WHITE);
        // toolbar.setTitleTextColor(Color.WHITE);

        //setTitleColor();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //view
        swipeRefreshLayout = findViewById(R.id.swipe_layout_order_status);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getIntent() == null) {
                    loadOrders(Common.currentUser.getPhone());
                } else {
                    if (getIntent().getStringExtra("userPhone") == null) {
                        loadOrders(Common.currentUser.getPhone());
                    } else {
                        loadOrders(getIntent().getStringExtra("userPhone"));
                    }
                }
            }
        });

        //Default, load for first time
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (getIntent() == null) {
                    loadOrders(Common.currentUser.getPhone());
                } else {
                    if (getIntent().getStringExtra("userPhone") == null) {
                        loadOrders(Common.currentUser.getPhone());
                    } else {
                        loadOrders(getIntent().getStringExtra("userPhone"));
                    }
                }
            }
        });


        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Request");

        recyclerView = findViewById(R.id.listOrders);
        // recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(), R.anim.layout_bottom_up);
        recyclerView.setLayoutAnimation(controller);

        totalCount = findViewById(R.id.totalCount);
        //todayOrder = findViewById(R.id.todayOrderCount);

        requests.orderByChild("phone")
                .equalTo(Common.currentUser.getPhone())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        size = dataSnapshot.getChildrenCount();
                        totalCount.setText("Your total orders: " + size);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

       /* requests.orderByChild("date")
                .equalTo(Common.date())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        size2 = dataSnapshot.getChildrenCount();
                        todayOrder.setText("Today order: " + size2);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

*/



    }


    private void loadOrders(String phone) {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests.orderByChild("phone").equalTo(phone)
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {

                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.order_date.setText(model.getDate());

                if (model.getStatus().equals("0")) { //placed
                    //viewHolder.txtOrderComment.setText("Your Comment will be reply very soon!");
                    viewHolder.txtOrderComment.setText(model.getComment());
                    //viewHolder.txtOrderComment.setTextColor(getResources().getColor(R.color.colorAccent));
                }
               else if (model.getStatus().equals("1")) { //Being processed
                    viewHolder.txtOrderComment.setText(model.getComment());
                    viewHolder.txtOrderComment.setTextColor(getResources().getColor(R.color.colorAccent));
                } else{  // Complete
                    viewHolder.txtOrderComment.setText("Your status complete, Thank you for being with us!");
                    viewHolder.txtOrderComment.setTextColor(getResources().getColor(R.color.colorAccent));
                }


            }

        };
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

}





