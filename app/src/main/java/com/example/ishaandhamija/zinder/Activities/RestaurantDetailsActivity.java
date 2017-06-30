package com.example.ishaandhamija.zinder.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ishaandhamija.zinder.Models.Restaurant;
import com.example.ishaandhamija.zinder.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RestaurantDetailsActivity extends AppCompatActivity {

    private static final int REQ_CODE = 111;
    TextView restName, restRating, restLoc, restCostForTwo, restContactNo, restAddress;
    ImageView headerImage, star;
    Button btnMenu, btnPhotos;

    String name, rating, loc, address, picUrl, menu, photos;
    Integer costForTwo, contactNo;
    public boolean selected;

    public static final String TAG = "YY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        restRating = (TextView) findViewById(R.id.restRating);
        restName = (TextView) findViewById(R.id.restName);
        restLoc = (TextView) findViewById(R.id.restLoc);
        restCostForTwo = (TextView) findViewById(R.id.restCostForTwo);
        restContactNo = (TextView) findViewById(R.id.restContactNo);
        restAddress = (TextView) findViewById(R.id.restAddress);
        headerImage = (ImageView) findViewById(R.id.header);
        star = (ImageView) findViewById(R.id.star);
        btnMenu = (Button) findViewById(R.id.btnMenu);
        btnPhotos = (Button) findViewById(R.id.btnPhotos);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getIntent().getStringExtra("restName"));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });

        rating = getIntent().getStringExtra("restRating");
        name = getIntent().getStringExtra("restName");
        loc = getIntent().getStringExtra("restLoc");
        costForTwo = getIntent().getIntExtra("restCostForTwo", 0);
        contactNo = getIntent().getIntExtra("restContactNo", 0);
        address = getIntent().getStringExtra("restAddress");
        picUrl = getIntent().getStringExtra("picUrl");
        menu = getIntent().getStringExtra("restMenu");
        photos = getIntent().getStringExtra("restPhotos");

        if (rating.equals("0")) {
            restRating.setText("NA");
        } else {
            restRating.setText(rating);
        }
        restName.setText(name);
        restLoc.setText(loc);
        restCostForTwo.setText(costForTwo.toString());
        restContactNo.setText(contactNo.toString());
        restAddress.setText(address);
        if (picUrl.equals("") || picUrl == null){
            headerImage.setImageResource(R.mipmap.loader);
        }
        else{
            Picasso.with(this)
                    .load(picUrl)
                    .placeholder(R.mipmap.loader)
                    .error(R.mipmap.error)
                    .into(headerImage);
        }

        restContactNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int callPerm = ContextCompat.checkSelfPermission(RestaurantDetailsActivity.this, Manifest.permission.CALL_PHONE);
                if (callPerm != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RestaurantDetailsActivity.this, new String[]{
                            Manifest.permission.CALL_PHONE
                    }, REQ_CODE);
                } else {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + contactNo.toString()));
                    startActivity(callIntent);
                }
            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(menu);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });

        btnPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(photos);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });

        selected = getIntent().getBooleanExtra("restStar", false);
        if (!selected){
            star.setImageResource(R.drawable.star);
        }
        else{
            star.setImageResource(R.drawable.starfill);
        }

        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selected){
                    star.setImageResource(R.drawable.starfill);
                    selected = true;
                }
                else{
                    star.setImageResource(R.drawable.star);
                    selected = false;
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent output = new Intent();
        output.putExtra("Naam", name);
        output.putExtra("Selected", selected);
        setResult(RESULT_OK, output);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQ_CODE) {
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Permission Not Given", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + contactNo.toString()));
            startActivity(callIntent);
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
