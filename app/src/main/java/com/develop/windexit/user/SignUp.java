package com.develop.windexit.user;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignUp extends AppCompatActivity {

    public String temporary;
    public String userid;


    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;

    private static DatabaseReference myRef;


    String userID;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Font
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto_Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        startauthentication();
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().setTitle("Sign Up");
        // toolbar.setTitleTextColor(Color.WHITE);

        //setTitleColor();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences mPreferences;
        mPreferences = SignUp.this.getSharedPreferences("users", MODE_PRIVATE);
        temporary = mPreferences.getString("saveuserid", "");

        if (temporary != null && !temporary.isEmpty()) {

            mAuth = FirebaseAuth.getInstance();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            myRef = mFirebaseDatabase.getReference();

            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            userid = currentFirebaseUser.getUid();

        } else {

        }

    }

    public void startauthentication() {
        SharedPreferences mPreferences;
        mPreferences = getSharedPreferences("users", MODE_PRIVATE);
        temporary = mPreferences.getString("saveuserid", "");

        if (temporary != null && !temporary.isEmpty()) {
        } else {
            Intent y = new Intent(SignUp.this, PhoneAuthActivity.class);
            y.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            y.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(y);

        }
    }
}