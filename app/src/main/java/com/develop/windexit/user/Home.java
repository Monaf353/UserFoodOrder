package com.develop.windexit.user;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.develop.windexit.user.Common.Common;
import com.develop.windexit.user.Database.Database;
import com.develop.windexit.user.Interface.ItemCliclListener;
import com.develop.windexit.user.Model.Category;
import com.develop.windexit.user.Model.Token;
import com.develop.windexit.user.Model.User;
import com.develop.windexit.user.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    FirebaseStorage storage;
    StorageReference storageReference;

    Uri saveUri;


    MaterialEditText edtName,edtPassword, edtEmail, edtHomeAddress;
    Button btnSelect, btnUload;
    CircleImageView imageView;


    FirebaseDatabase database;
    DatabaseReference category;
    TextView navtext, txtEmail;
    RecyclerView recycler_menu;


    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    CounterFab fab;

    FirebaseAuth mAuth;
    String userID;
    String userPhone;

    public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 99;


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
        setContentView(R.layout.activity_home);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();




        database = FirebaseDatabase.getInstance();
        category = database.getReference("category");

        //Make sure you move this function after database is getInstance
        // move from loadMenu
        //Animation
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(
                Category.class,
                R.layout.menu_item,
                MenuViewHolder.class,
                category.orderByChild("activeInactive").equalTo("1")) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Category model, int position) {

                viewHolder.txtMenuName.setText(model.getName());

                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageView);
                final Category clickItem = model;

                viewHolder.setItemCliclListener(new ItemCliclListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // Toast.makeText(Home.this, "" + clickItem.getName(), Toast.LENGTH_LONG).show();
                        //Get categoryId and send to new activity
                        Intent intent = new Intent(Home.this, FoodList.class);
                        //Because categoryId is key, so we just get key of this item
                        intent.putExtra("CategoryId", adapter.getRef(position).getKey());
                        startActivity(intent);

                    }
                });
            }
        };

        //remenber password
        Paper.init(this);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent cartaintent = new Intent(Home.this, Cart.class);
                startActivity(cartaintent);
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        fab.setCount(new Database(this).getCountCart(Common.currentUser.getPhone()));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer,toolbar , R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        navtext = headerView.findViewById(R.id.navtxt);
        navtext.setText(Common.currentUser.getName());
        txtEmail = headerView.findViewById(R.id.nav_email);
        txtEmail.setText(Common.currentUser.getEmail());
        imageView = headerView.findViewById(R.id.nav_header_image);
        Picasso.with(getBaseContext())
                .load(Common.currentUser.getImage())
                .into(imageView);

        //load menu
        recycler_menu = findViewById(R.id.recycle_menu);
        recycler_menu.setHasFixedSize(true);
        //layoutManager = new LinearLayoutManager(this);
        // recycler_menu.setLayoutManager(layoutManager);

        //Animation
        recycler_menu.setLayoutManager(new GridLayoutManager(this, 2));
       // LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recycler_menu.getContext(), R.anim.layout_bottom_up);
        //recycler_menu.setLayoutAnimation(controller);

        //Register service
        // startService(new Intent(Home.this, ListenOrder.class));
        //view
        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Common.isConnectedToINternet(getBaseContext())) {
                    loadMenu();
                } else {
                    Toast.makeText(Home.this, "please check your internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        //Default, load for first time
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Common.isConnectedToINternet(getBaseContext())) {
                    loadMenu();
                } else {
                    Toast.makeText(Home.this, "please check your internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        updateToken(FirebaseInstanceId.getInstance().getToken());
        //setupSlider();

    }




    @Override
    protected void onResume() {
        super.onResume();
        fab.setCount(new Database(this).getCountCart(Common.currentUser.getPhone()));
        //Fix click back button from Food and don't see category

    }



    private void updateToken(String token) {

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token data = new Token(token, false);
        tokens.child(Common.currentUser.getPhone())
                .setValue(data);
    }

    private void loadMenu() {
        recycler_menu.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

        //Animation
        recycler_menu.getAdapter().notifyDataSetChanged();
        recycler_menu.scheduleLayoutAnimation();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_exit) {
          // startActivity(new Intent(Home.this,SearchActivity.class));
            showExitDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit this beautiful application!?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Home.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_menu) {
            startActivity(new Intent(Home.this, Home.class));
        } else if (id == R.id.nav_cart) {
            startActivity(new Intent(Home.this, Cart.class));
        } else if (id == R.id.nav_order) {
            startActivity(new Intent(Home.this, OrderStatus.class));
        } else if (id == R.id.nav_logout) {
            //Delete remember user & password
            Paper.book().destroy();
            Intent signIn = new Intent(Home.this, MainActivity.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(signIn);
        } else if (id == R.id.nav_chnge_profile) {
            updateProfile(Common.currentUser);
        }else if(id == R.id.nav_contact_admin){
            showAdminNameNumber();
        }else if(id == R.id.nav_favorites){
            startActivity(new Intent(Home.this,FavoritesActivity.class));
        }
        else if(id==R.id.about){
            startActivity(new Intent(Home.this,About.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showAdminNameNumber() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Admin profile");

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.admin_layout,null);

        LinearLayout lyphone = view.findViewById(R.id.layout_phone);
        LinearLayout lyemail = view.findViewById(R.id.layout_email);


        lyphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+8801719450855"));

                if (ContextCompat.checkSelfPermission(Home.this,android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Home.this, new String[]{android.Manifest.permission.CALL_PHONE},MY_PERMISSIONS_REQUEST_CALL_PHONE);
                }
                else
                {
                    startActivity(intent);
                }

            }

        });

        lyemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"minionbd03@gmail.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, "subject");
                email.putExtra(Intent.EXTRA_TEXT, "message");
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
        });
        alertDialog.setView(view);
        alertDialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }


   private void updateProfile(final User user) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Update profile");
        alertDialog.setMessage("Please fill all information");
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_home = inflater.inflate(R.layout.update_profile, null);

        edtHomeAddress = layout_home.findViewById(R.id.edtHomeAddress);
        edtEmail = layout_home.findViewById(R.id.edtEmail);
        edtName = layout_home.findViewById(R.id.edtNameProfile);
        edtPassword = layout_home.findViewById(R.id.edtPassword);

        edtHomeAddress.setText(user.getHomeAddress());
        edtEmail.setText(user.getEmail());
        edtName.setText(user.getName());
        edtPassword.setText(user.getPassword());

        btnSelect = layout_home.findViewById(R.id.btnSelect);
        btnUload = layout_home.findViewById(R.id.btnUpload);

        //Event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), Common.PICK_IMAGE_REQUEST); //Let user select image from and save Uri of this image

            }
        });

        btnUload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saveUri != null) {
                    final ProgressDialog mDialog = new ProgressDialog(Home.this);
                    mDialog.setMessage("Uploading....");
                    mDialog.show();
                    String imageName = UUID.randomUUID().toString();
                    final StorageReference imageFolder = storageReference.child("images/" + imageName);
                    imageFolder.putFile(saveUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    mDialog.dismiss();
                                    /// Toast.makeText(Home.this, "Uploaded!!!", Toast.LENGTH_SHORT).show();
                                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            //set value for newCategory if image upload and we can get download link
                                            user.setImage(uri.toString());

                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    mDialog.dismiss();
                                    Toast.makeText(Home.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                    mDialog.setMessage("Uploaded " + progress + "%");
                                }
                            });
                }
            }
        });


        alertDialog.setView(layout_home);
        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                user.setHomeAddress(edtHomeAddress.getText().toString());
                user.setEmail(edtEmail.getText().toString());
                user.setName(edtName.getText().toString());
                user.setPassword(edtPassword.getText().toString());

                FirebaseDatabase.getInstance().getReference("User")
                        .child(user.getPhone())
                        .setValue(user)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(Home.this, "Update Address Successful", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        alertDialog.show();
    }


    //Press Ctrl+O
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST &&
                resultCode == RESULT_OK &&
                data != null &&
                data.getData() != null) {
            saveUri = data.getData();
            btnSelect.setText("Image Selected !");
        }
    }


}
