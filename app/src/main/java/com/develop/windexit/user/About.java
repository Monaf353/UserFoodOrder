package com.develop.windexit.user;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.develop.windexit.user.Home.MY_PERMISSIONS_REQUEST_CALL_PHONE;

public class About extends AppCompatActivity implements View.OnClickListener{

    LinearLayout dev_phone,dev_email,more_apps;


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
        setContentView(R.layout.activity_about);

        findViewById(R.id.about_github_url).setOnClickListener(this);
        findViewById(R.id.about_fb).setOnClickListener(this);

        findViewById(R.id.about_insta).setOnClickListener(this);
        findViewById(R.id.about_twitter).setOnClickListener(this);
        findViewById(R.id.about_linkdin).setOnClickListener(this);
        dev_phone = findViewById(R.id.dev_number);
        dev_email = findViewById(R.id.dev_email);
        more_apps = findViewById(R.id.more_apps);
        more_apps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Abdul+Monaf")));
            }
        });
        dev_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+8801724353528"));

                if (ContextCompat.checkSelfPermission(About.this,android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(About.this, new String[]{android.Manifest.permission.CALL_PHONE},MY_PERMISSIONS_REQUEST_CALL_PHONE);
                }
                else
                {
                    startActivity(intent);
                }

            }

        });

        dev_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"monaf.cse@gmail.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, "subject");
                email.putExtra(Intent.EXTRA_TEXT, "message");
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.about_github_url:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Monaf353/"));
                startActivity(intent);
                break;

            case R.id.about_fb:
                Intent in2 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/monaf.cse/"));
                startActivity(in2);
                break;

            case R.id.about_insta:
                Intent in3= new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/ab_monaf/"));
                startActivity(in3);
                break;
            case R.id.about_twitter:
                Intent in4= new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/abdulmonaf10/"));
                startActivity(in4);
                break;
            case R.id.about_linkdin:
                Intent in5=new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/abdul-monaf-a457a4146/"));
                startActivity(in5);
                break;
        }
    }
}
