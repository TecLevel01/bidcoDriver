package com.bidco.bidcodriverapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class start_trip extends AppCompatActivity {
    Button rBtn;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_trip);
        rBtn = findViewById(R.id.reachedBtn);

        rBtn.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
        });

    }

}
