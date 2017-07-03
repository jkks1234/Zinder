package com.example.ishaandhamija.zinder.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ishaandhamija.zinder.Fragments.DashBoardFragment;
import com.example.ishaandhamija.zinder.Fragments.FavouritePlacesFragment;
import com.example.ishaandhamija.zinder.Fragments.FindMatchFragment;
import com.example.ishaandhamija.zinder.Interfaces.GetLL;
import com.example.ishaandhamija.zinder.Interfaces.GetResponse;
import com.example.ishaandhamija.zinder.Models.Restaurant;
import com.example.ishaandhamija.zinder.Models.User2;
import com.example.ishaandhamija.zinder.R;
import com.example.ishaandhamija.zinder.Utils.GPSTracker;
import com.example.ishaandhamija.zinder.Utils.NearbyRestaurants;
import com.example.ishaandhamija.zinder.Utils.SearchRestaurants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQ_CODE = 121;
    public static final String TAG = "LOCATION";
    Context ctx;
    Activity activity;
    Activity mActivity = DashboardActivity.this;
    String latitude, longitude, city, area;
    NearbyRestaurants nr;
    SearchRestaurants sr;
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
    GPSTracker gpsTracker;
    String q;
    Toolbar toolbar;
    Bitmap photoToBeDisplayed;

    ImageView userKaPhoto;
    TextView userKaNaam, userKaEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ctx = this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Dashboard");
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
        pD.setCancelable(false);
        pD.show();

//        photoToBeDisplayed = getBitmapFromURL(auth.getCurrentUser().getPhotoUrl().toString());

//        oA = new OnAuthentication() {
//            @Override
//            public void onSuccess(Bitmap photoUrl) {
//                userKaPhoto.setImageBitmap(photoUrl);
////                pD.dismiss();
//            }
//        };

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
//                nr = new NearbyRestaurants(ctx, area, latitude, longitude, rvList, activity, getResponse);
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
                        userKaPhoto.setImageResource(R.mipmap.ic_launcher);
                        Glide.with(ctx).load(user.getProfilePicUrl()).into(userKaPhoto);

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
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashboard, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                q = query;
                if (q == ""){
                    nr = new NearbyRestaurants(ctx, area, latitude, longitude, rvList, activity, getResponse);
                }
                else {
                    sr = new SearchRestaurants(ctx, query, rvList, activity, getResponse);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.dashboard) {
            fragManager = getSupportFragmentManager();
            DashBoardFragment dashboardFragment = new DashBoardFragment();
            fragTxn = fragManager.beginTransaction();
            fragTxn.replace(R.id.fragContainer, dashboardFragment);
            fragTxn.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragTxn.commit();

        } else if (id == R.id.favPlaces) {

            fragManager = getSupportFragmentManager();
            FavouritePlacesFragment favouritePlacesFragment = new FavouritePlacesFragment(ctx, activity);
            fragTxn = fragManager.beginTransaction();
            fragTxn.replace(R.id.fragContainer,favouritePlacesFragment);
            fragTxn.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragTxn.commit();

        } else if (id == R.id.findMatch) {

            fragManager = getSupportFragmentManager();
            FindMatchFragment findMatchFragment = new FindMatchFragment(this);
            fragTxn = fragManager.beginTransaction();
            fragTxn.replace(R.id.fragContainer,findMatchFragment);
            fragTxn.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragTxn.commit();

        } else if (id == R.id.aboutUs) {

            Toast.makeText(ctx, "About Us mein Jayenge", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getYourLocation(){

        Log.d("Dhund", "getYourLocation: ");
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Log.d("Dhundaa", "getYourLocation: ");
//                gpsTracker = new GPSTracker(ctx, locationManager, getLL, ctx, rvList);
//
//                if (gpsTracker.getIsGPSTrackingEnabled())
//                {
//
//                    Log.d("Dhundo", "getYourLocation: " + "Finding");
//
//                    latitude = String.valueOf(gpsTracker.getLatitude());
//
//                    longitude = String.valueOf(gpsTracker.getLongitude());
//
//                    city = gpsTracker.getLocality(ctx);
//
//                    area = gpsTracker.getAddressLine(ctx);
//
//                }
//                else
//                {
//                    gpsTracker.showSettingsAlert();
//                }
//            }
//        }, 5000);

        Log.d("Dhundaa", "getYourLocation: ");
        gpsTracker = new GPSTracker(this, locationManager, getLL, ctx, rvList);
        Log.d("Dhun", "getYourLocation: ");

//        final LocationAsyncTask locationAsyncTask = new LocationAsyncTask(ctx, getLL, rvList, locationManager);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//                gpsTracker = new GPSTracker(ctx, locationManager, getLL, ctx, rvList);
//                Log.d("Result", "run: ");
//                onGettingLocation.onSuccess(gpsTracker, latitude, longitude, city, area);
//            }
//        }).start();

        if (gpsTracker.getIsGPSTrackingEnabled())
        {

            Log.d("Dhundo", "getYourLocation: " + "Finding");

            latitude = String.valueOf(gpsTracker.getLatitude());

            longitude = String.valueOf(gpsTracker.getLongitude());

            city = gpsTracker.getLocality(this);

            area = gpsTracker.getAddressLine(this);

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

//    class LoadingAysncTask extends AsyncTask<Void, Void, Void>{
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pDialog.setMessage("Loading...");
//            pDialog.setCancelable(false);
//            pDialog.show();
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            if (pDialog.isShowing())
//                pDialog.dismiss();
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 9876){
            Log.d("OOPP", "onActivityResult: " + gpsTracker.getIsGPSTrackingEnabled());
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            if ((lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)))){
                startActivity(new Intent(DashboardActivity.this, DashboardActivity.class));
            }
            else{
                finish();
                Toast.makeText(ctx, "Please Turn On Location", Toast.LENGTH_LONG).show();
            }
            return;
        }
        if (q == null || q == "") {
            nr.getFeedAdapter().onActivityResult(requestCode, resultCode, data);
        }
        else{
            sr.getFeedAdapter().onActivityResult(requestCode, resultCode, data);
        }
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

    public void setActionBarTitle(String title){
        toolbar.setTitle(title);
    }
}
