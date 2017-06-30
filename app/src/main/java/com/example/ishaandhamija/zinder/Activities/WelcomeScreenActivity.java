package com.example.ishaandhamija.zinder.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.ishaandhamija.zinder.R;
import com.google.firebase.auth.FirebaseAuth;

public class WelcomeScreenActivity extends AppCompatActivity {

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        if (!isNetworkAvailable()){
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
            finish();
        }
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i;
                    auth = FirebaseAuth.getInstance();
                    Log.d("WSS", "run: " + auth);
                    if (auth.getCurrentUser() != null) {
                        i = new Intent(WelcomeScreenActivity.this, DashboardActivity.class);
                    } else {
                        i = new Intent(WelcomeScreenActivity.this, MainActivity.class);
                    }
                    startActivity(i);
                    finish();
                }
            }, 1000);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
