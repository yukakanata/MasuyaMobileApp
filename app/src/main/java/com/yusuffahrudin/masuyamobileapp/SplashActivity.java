package com.yusuffahrudin.masuyamobileapp;

import android.content.pm.PackageInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.yusuffahrudin.masuyamobileapp.util.SessionManager;

import java.util.HashMap;

public class SplashActivity extends AppCompatActivity {
    private ImageView img_logo;
    private TextView tv_version;
    SessionManager sessionManager;
    private String level, kdkota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        img_logo = findViewById(R.id.img_logo);
        tv_version = findViewById(R.id.tv_version);
        tv_version.setText("Masuya Mobile App v"+getApplicationVersionName());
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.splash_transition);
        img_logo.startAnimation(anim);
        tv_version.startAnimation(anim);

        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        level = user.get(SessionManager.level);
        kdkota = user.get(SessionManager.kdkota);

        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(5000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                finally {
                    sessionManager.checkLogin();
                    finish();
                }
            }
        };
        timer.start();
    }

    //Programmatically get the current version Name
    private String getApplicationVersionName() {

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch(Exception ignored){}
        return "";
    }
}
