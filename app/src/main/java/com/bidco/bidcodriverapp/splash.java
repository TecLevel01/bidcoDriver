package com.bidco.bidcodriverapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class splash extends AppCompatActivity {
    Animation topAnim, BottomAnim;
    ImageView img;
    TextView mTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        BottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        //Hooks
        img = findViewById(R.id.logo);
        mTxt = findViewById(R.id.title);

        img.setAnimation(topAnim);
        mTxt.setAnimation(BottomAnim);

        int callDActivity = 3000;
        new Handler().postDelayed(( () -> {
            Intent intent = new Intent(this, Verify.class);
            startActivity(intent);
            finish();
        }), callDActivity);
    }
}