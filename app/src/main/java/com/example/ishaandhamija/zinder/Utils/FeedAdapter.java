package com.example.ishaandhamija.zinder.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ishaandhamija.zinder.Activities.DashboardActivity;
import com.example.ishaandhamija.zinder.Activities.RestaurantDetailsActivity;
import com.example.ishaandhamija.zinder.Models.Restaurant;
import com.example.ishaandhamija.zinder.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ishaandhamija on 09/06/17.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedHolder> {

    Context ctx;
    Activity activity;
    ArrayList<Restaurant> restaurantArrayList;

    public static final Integer REQ_CODE = 1001;

    public FeedAdapter(Context ctx, ArrayList<Restaurant> restaurantArrayList, Activity activity) {
        this.ctx = ctx;
        this.restaurantArrayList = restaurantArrayList;
        this.activity = activity;
    }

    @Override
    public FeedHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = li.inflate(R.layout.restaurant_list_view, parent, false);

        return new FeedHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FeedHolder holder, int position) {

        final Restaurant restaurant = restaurantArrayList.get(position);
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
