package com.example.ishaandhamija.zinder.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ishaandhamija.zinder.Activities.DashboardActivity;
import com.example.ishaandhamija.zinder.Activities.RestaurantDetailsActivity;
import com.example.ishaandhamija.zinder.Models.Restaurant;
import com.example.ishaandhamija.zinder.Models.User2;
import com.example.ishaandhamija.zinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ishaandhamija on 09/06/17.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedHolder> {

    Context ctx;
    Activity activity;
    ArrayList<Restaurant> restaurantArrayList;
    String fragName;

    FirebaseAuth auth;
    DatabaseReference firebaseDatabase;
    FirebaseDatabase firebaseInstance;
    String userId;

    public static final Integer REQ_CODE = 1001;

    public FeedAdapter(Context ctx, ArrayList<Restaurant> restaurantArrayList, Activity activity, String fragName) {
        this.ctx = ctx;
        this.restaurantArrayList = restaurantArrayList;
        this.activity = activity;
        this.fragName = fragName;

        auth = FirebaseAuth.getInstance();
        firebaseInstance = FirebaseDatabase.getInstance();
        firebaseDatabase = firebaseInstance.getReference("userss");
        userId = auth.getCurrentUser().getUid();
    }

    @Override
    public FeedHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = li.inflate(R.layout.restaurant_list_view, parent, false);

        return new FeedHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FeedHolder holder, final int position) {

        final Restaurant restaurant = restaurantArrayList.get(position);
        final Integer pos = position;
        holder.name.setText(restaurant.getName());
        holder.locality.setText(restaurant.getLocality());
        holder.costForTwo.setText("Cost for Two : Rs. " + restaurant.getCostForTwo().toString());
        if (restaurant.getRating().equals("0")) {
            holder.rating.setText("NA");
        }
        else{
            holder.rating.setText(restaurant.getRating());
        }
        if (restaurant.getThumb().isEmpty()){
            holder.img.setImageResource(R.mipmap.loader);
        }
        else {
            Picasso.with(ctx)
                    .load(restaurant.getThumb())
                    .placeholder(R.mipmap.loader)
                    .error(R.mipmap.error)
                    .into(holder.img);
        }
        holder.vv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, RestaurantDetailsActivity.class);
                intent.putExtra("restRating", restaurant.getRating());
                intent.putExtra("restName", restaurant.getName());
                intent.putExtra("restLoc", restaurant.getLocality());
                intent.putExtra("restCostForTwo", restaurant.getCostForTwo());
                intent.putExtra("restContactNo", restaurant.getContactNo());
                intent.putExtra("restAddress", restaurant.getAddress());
                intent.putExtra("picUrl", restaurant.getPicUrl());
                intent.putExtra("restMenu", restaurant.getMenu());
                intent.putExtra("restPhotos", restaurant.getPhotos());
                intent.putExtra("restStar", restaurant.isSelected());
                ((Activity) ctx).startActivityForResult(intent, REQ_CODE);
            }

        });

        if (fragName.equals("favPlaces")){
            holder.vv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);

                    alertDialog.setTitle("Remove");
                    alertDialog.setMessage("Remove from Favourite Places?");
                    alertDialog.setIcon(R.mipmap.ic_bin);

                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
//                            final Integer[] noOfRests = new Integer[1];
//                            firebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    User2 user = dataSnapshot.getValue(User2.class);
//                                    noOfRests[0] = user.getRestaurants().size();
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//                            firebaseDatabase.child(userId).child("restaurants").child(pos.toString())
//                                    .addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    dataSnapshot.getRef().removeValue();
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//
//                            for (Integer i = pos+1; i< noOfRests[0]; i++) {
//                                final Integer finalI = i;
//                                firebaseDatabase.child(userId).child("restaurants")
//                                        .addValueEventListener(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                                dataSnapshot.getRef().child(finalI.toString()).setValue(finalI + 1);
//                                            }
//
//                                            @Override
//                                            public void onCancelled(DatabaseError databaseError) {
//
//                                            }
//                                        });
//                            }
                        }
                    });

                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    alertDialog.show();
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return restaurantArrayList.size();
    }

    public  void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE){
            if (resultCode == RestaurantDetailsActivity.RESULT_OK){

                String naam = data.getStringExtra("Naam");
                boolean seleted = data.getBooleanExtra("Selected", false);

                for (int i=0;i<restaurantArrayList.size();i++){
                    if (restaurantArrayList.get(i).getName().equals(naam)){
                        restaurantArrayList.get(i).setSelected(seleted);
                        break;
                    }
                }
            }
        }
    }
}
