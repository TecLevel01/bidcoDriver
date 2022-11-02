package com.bidco.bidcodriverapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

public class Verify extends AppCompatActivity {
    EditText dEmail, dPW;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        dEmail = findViewById(R.id.dEmail);
        dPW = findViewById(R.id.dPW);
        mAuth = FirebaseAuth.getInstance();

    }


    public void dVerify(View view) {
        String email = dEmail.getText().toString().trim(),
                pw = dPW.getText().toString().trim();
        if (email.isEmpty()) {
            dEmail.setError("email required!");
            dEmail.requestFocus();
            return;
        }
        if (pw.isEmpty()){
            dPW.setError("password required!");
            dPW.requestFocus();
            return;
        }
        myProgress dialog = new myProgress(this, "Verifying");
        mAuth.signInWithEmailAndPassword(email, pw).addOnCompleteListener(this, task -> {
            String msg;
            if (task.isSuccessful()){
                msg =  "Driver verified";
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }else {
                msg = task.getException().getMessage();
            }
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

    }
    public static class myProgress extends ProgressDialog{
        public myProgress(Context context, String msg){
            super(context);
            getWindow().setBackgroundDrawableResource(R.drawable.rounded_all);
            setMessage(msg + "...");
            show();
        }
    }
}