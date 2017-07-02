package com.example.ishaandhamija.zinder.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ishaandhamija.zinder.Activities.DashboardActivity;
import com.example.ishaandhamija.zinder.Models.Restaurant;
import com.example.ishaandhamija.zinder.Models.User2;
import com.example.ishaandhamija.zinder.R;
import com.example.ishaandhamija.zinder.Utils.FeedAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.ishaandhamija.zinder.R.id.rvList;

/**
 * Created by ishaandhamija on 01/07/17.
 */

public class FavouritePlacesFragment extends Fragment {

    Context ctx;
    ArrayList<Restaurant> favPlacesArrayList;
    FirebaseAuth auth;
    DatabaseReference firebaseDatabase;
    FirebaseDatabase firebaseInstance;
    String userId;
    FeedAdapter feedAdapter;
    RecyclerView favPlacesRV;
    Activity activity;

    public FavouritePlacesFragment(Context ctx, Activity activity) {
        this.ctx = ctx;
        this.activity = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_favourite_places, container, false);

        ((DashboardActivity) getActivity()).setActionBarTitle("Your Favourite Places");

        favPlacesRV = (RecyclerView) rootView.findViewById(R.id.favPlacesRV);

        auth = FirebaseAuth.getInstance();
        firebaseInstance = FirebaseDatabase.getInstance();
        firebaseDatabase = firebaseInstance.getReference("userss");
        userId = auth.getCurrentUser().getUid();

        firebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User2 user = dataSnapshot.getValue(User2.class);

                if (user == null) {
                    Log.e("NoData", "User data is null!");
                    return;
                }
                favPlacesArrayList = user.getRestaurants();

                if (favPlacesArrayList == null){
                    Toast.makeText(ctx, "No Places Added", Toast.LENGTH_SHORT).show();
                }

                else {
                    feedAdapter = new FeedAdapter(ctx, favPlacesArrayList, activity, "favPlaces");
                    favPlacesRV.setLayoutManager(new GridLayoutManager(ctx, 2));
                    favPlacesRV.setAdapter(feedAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("UserFailed", "Failed to read user", error.toException());
            }
        });

        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);
    }
}
