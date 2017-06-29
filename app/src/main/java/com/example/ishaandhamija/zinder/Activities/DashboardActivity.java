package com.example.ishaandhamija.zinder.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.ishaandhamija.zinder.Fragments.FindMatchFragment;
import com.example.ishaandhamija.zinder.Interfaces.GetLL;
import com.example.ishaandhamija.zinder.Interfaces.GetResponse;
import com.example.ishaandhamija.zinder.Interfaces.OnAuthentication;
import com.example.ishaandhamija.zinder.Models.Restaurant;
import com.example.ishaandhamija.zinder.Models.User2;
import com.example.ishaandhamija.zinder.R;
import com.example.ishaandhamija.zinder.Utils.GPSTracker;
import com.example.ishaandhamija.zinder.Utils.NearbyRestaurants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQ_CODE = 121;
    public static final String TAG = "LOCATION";
    Context ctx;
    Activity activity;
    String latitude, longitude, city = null, area;
    NearbyRestaurants nr;
    ProgressDialog pDialog;
    RecyclerView rvList;
    GetLL getLL;
    GetResponse getResponse;
    FirebaseAuth auth;
    ArrayList<Restaurant> restaurantArrayList;
    public static ArrayList<Restaurant> selectedRestaurants;
    DatabaseReference firebaseDatabase;
    FirebaseDatabase firebaseInstance;
    String userId;
    FragmentManager fragManager;
    FragmentTransaction fragTxn;
    ProgressDialog pD;
    OnAuthentication oA;
    Bitmap photoToBeDisplayed;
    public static GPSTracker gpsTracker;

    ImageView userKaPhoto;
    TextView userKaNaam, userKaEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ctx = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rvList = (RecyclerView) findViewById(R.id.rvList);
        pDialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
        firebaseInstance = FirebaseDatabase.getInstance();
        firebaseDatabase = firebaseInstance.getReference("userss");
        userId = auth.getCurrentUser().getUid();

        userKaPhoto = (ImageView) findViewById(R.id.userKaPhoto);
        userKaNaam = (TextView) findViewById(R.id.userKaNaam);
        userKaEmail = (TextView) findViewById(R.id.userKaEmail);

        pD = new ProgressDialog(this);
        pD.setMessage("Fetching Nearby Restaurants...");
        pD.show();

        oA = new OnAuthentication() {
            @Override
            public void onSuccess(Bitmap photoUrl) {
                userKaPhoto.setImageBitmap(photoUrl);
//                pD.dismiss();
            }
        };

        getLL = new GetLL() {
            @Override
            public void onSuccess(Context ctx, String area, String city, String latitude, String longitude, RecyclerView rvList) {
                Toast.makeText(ctx, city, Toast.LENGTH_LONG).show();
                Log.d("Success", "onSuccess: " + area);
                nr = new NearbyRestaurants(ctx, area, latitude, longitude, rvList, activity, getResponse);
            }

            @Override
            public void onError(String errorMsg) {
                Toast.makeText(ctx, errorMsg, Toast.LENGTH_SHORT).show();
                nr = new NearbyRestaurants(ctx, area, latitude, longitude, rvList, activity, getResponse);
//                finish();
            }
        };

        getResponse = new GetResponse() {
            @Override
            public void onSuccess(ArrayList<Restaurant> restList) {
                restaurantArrayList = restList;
                for (int i=0;i<restaurantArrayList.size();i++) {
                    Log.d("Aagya", "onSuccess: " + restaurantArrayList.get(i).getName());
                }
                firebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User2 user = dataSnapshot.getValue(User2.class);

                        if (user == null) {
                            Log.e(TAG, "User data is null!");
                            return;
                        }

                        ArrayList<Restaurant> selectedList = user.getRestaurants();

                        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                        View hView =  navigationView.getHeaderView(0);
                        userKaNaam = (TextView)hView.findViewById(R.id.userKaNaam);
                        userKaEmail = (TextView) hView.findViewById(R.id.userKaEmail);
                        userKaPhoto = (ImageView) hView.findViewById(R.id.userKaPhoto);
                        userKaNaam.setText(user.getName());
                        userKaEmail.setText(user.getEmail());

//                        Bitmap photoToBeDisplayed = null;
//
//                        try {
//                            URL urll = new URL(user.getProfilePicUrl());
//                            photoToBeDisplayed = BitmapFactory.decodeStream(urll.openConnection().getInputStream());
//                        } catch (MalformedURLException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }

//                        photoToBeDisplayed = null;
//                        MyAsync myAsync = new MyAsync();
//                        myAsync.execute(user.getProfilePicUrl());

//                        oA.onSuccess(photoToBeDisplayed);
                        pD.dismiss();

                        if (selectedList != null){
                            for (int i=0;i<restaurantArrayList.size();i++){
                                for (int j=0;j<selectedList.size();j++){
                                    if (restaurantArrayList.get(i).getName().equals(selectedList.get(j).getName())){
                                        restaurantArrayList.get(i).setSelected(true);
                                    }
                                }
                            }
                        }

                        Log.e(TAG, "User data is changed!" + user.getName());

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e(TAG, "Failed to read user", error.toException());
                    }
                });
            }

            @Override
            public void onError() {

            }
        };

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        int coarsePerm = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int finePerm = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if ((coarsePerm != PackageManager.PERMISSION_GRANTED) || (finePerm != PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, REQ_CODE);
        }
        else {
            getYourLocation();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.favPlaces) {

        } else if (id == R.id.findMatch) {

            fragManager = getSupportFragmentManager();
            FindMatchFragment findMatchFragment = new FindMatchFragment(this);
            fragTxn = fragManager.beginTransaction();
            fragTxn.replace(R.id.fragContainer,findMatchFragment);
            fragTxn.commit();

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getYourLocation(){

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        gpsTracker = new GPSTracker(this, locationManager, getLL, ctx, rvList);

        if (gpsTracker.getIsGPSTrackingEnabled())
        {

            latitude = String.valueOf(gpsTracker.getLatitude());

            longitude = String.valueOf(gpsTracker.getLongitude());

            city = gpsTracker.getLocality(this);

            area = gpsTracker.getAddressLine(this);

            Log.d("ab", "getYourLocation: " + latitude + "\n" + longitude + "\n" + city + "\n" + area);

        }
        else
        {
            gpsTracker.showSettingsAlert();
        }

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
            getYourLocation();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    class LoadingAysncTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

//            RequestQueue requestQueue = Volley.newRequestQueue(ctx);
//            requestQueue.add(nr.getJsonObjectRequest2());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pDialog.isShowing())
                pDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        nr.getFeedAdapter().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

            if (restaurantArrayList != null) {
                selectedRestaurants = new ArrayList<>();
                for (int i = 0; i < restaurantArrayList.size(); i++) {
                    if (restaurantArrayList.get(i).isSelected()) {
                        selectedRestaurants.add(restaurantArrayList.get(i));
                    }
                }
                firebaseDatabase.child(userId).child("restaurants").setValue(selectedRestaurants);
                addUserChangeListener();
            }
    }

    private void addUserChangeListener() {
        firebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User2 user = dataSnapshot.getValue(User2.class);

                if (user == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }

                Log.e(TAG, "User data is changed!" + user.getName());

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }

    public class MyAsync extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                photoToBeDisplayed = BitmapFactory.decodeStream(input);
                return photoToBeDisplayed;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            oA.onSuccess(photoToBeDisplayed);

        }
    }


    public static ArrayList<Restaurant> getSelectedRestaurants(){
        return selectedRestaurants;
    }
}
