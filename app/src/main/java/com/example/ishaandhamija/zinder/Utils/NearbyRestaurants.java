package com.example.ishaandhamija.zinder.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ishaandhamija.zinder.Activities.DashboardActivity;
import com.example.ishaandhamija.zinder.Interfaces.GetResponse;
import com.example.ishaandhamija.zinder.Models.Restaurant;

import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ishaandhamija on 07/06/17.
 */

public class NearbyRestaurants {

    Context ctx;
    Activity activity;
    String Entity_type;
    Integer Entity_id;
    String Area, Lat, Lon;
    ArrayList<Restaurant> restList = new ArrayList<>();
    RecyclerView rvList;
    public static final String TAG = "PPOP";
    FeedAdapter feedAdapter;
    GetResponse gr;
    String url;

    public NearbyRestaurants(Context ctx, String area, String lat, String lon, RecyclerView rvList, Activity activity, GetResponse gr) {
        this.ctx = ctx;
        this.Area = area;
        this.Lat = lat;
        this.Lon = lon;
        url = "https://developers.zomato.com/api/v2.1/geocode?user-key=9b71538cb6d3419677ab58e98d85cfd6&lat=" + lat + "&lon=" + lon;
        Log.d("urlll", "NearbyRestaurants: " + url);
        this.rvList = rvList;
        this.activity = activity;
        this.gr = gr;
        Log.d(TAG, "NearbyRestaurants: Latitude = " + Lat);
        Log.d(TAG, "NearbyRestaurants: Longitude = " + Lon);
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(jsonObjectRequest2);
    }

    JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest("https://developers.zomato.com/api/v2.1/geocode?user-key=9b71538cb6d3419677ab58e98d85cfd6&lat=" + "28.6391" + "&lon=" + "77.0868",
            null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try
                    {
                        Log.d(TAG, "onResponse: " + Lat + "\t" + Lon);
                        for (int i=0;i<response.getJSONArray("nearby_restaurants").length();i++){
                            String name = response.getJSONArray("nearby_restaurants").getJSONObject(i).getJSONObject("restaurant").getString("name").toString();
                            String locality = response.getJSONArray("nearby_restaurants").getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getString("locality").toString();
                            String picUrl = response.getJSONArray("nearby_restaurants").getJSONObject(i).getJSONObject("restaurant").getString("featured_image").toString();
                            String thumb = response.getJSONArray("nearby_restaurants").getJSONObject(i).getJSONObject("restaurant").getString("thumb").toString();
                            Integer costForTwo = response.getJSONArray("nearby_restaurants").getJSONObject(i).getJSONObject("restaurant").getInt("average_cost_for_two");
                            String rating = response.getJSONArray("nearby_restaurants").getJSONObject(i).getJSONObject("restaurant").getJSONObject("user_rating").getString("aggregate_rating").toString();
//                            Integer contactNo = response.getJSONArray("nearby_restaurants").getJSONObject(i).getJSONObject("restaurant").getInt("phone_numbers");
                            Integer contactNo = 1234;
                            String address = response.getJSONArray("nearby_restaurants").getJSONObject(i).getJSONObject("restaurant").getJSONObject("location").getString("address").toString();
                            String menu = response.getJSONArray("nearby_restaurants").getJSONObject(i).getJSONObject("restaurant").getString("menu_url").toString();
                            String photos = response.getJSONArray("nearby_restaurants").getJSONObject(i).getJSONObject("restaurant").getString("photos_url").toString();

                            Restaurant restaurant = new Restaurant(name, locality, picUrl, costForTwo, rating, contactNo, address, menu, photos, thumb, false);
                            restList.add(restaurant);
                            Log.d(TAG, "onResponse: Naam : " + name);
                        }

                        gr.onSuccess(restList);

                        feedAdapter = new FeedAdapter(ctx, restList, activity);
                        rvList.setLayoutManager(new GridLayoutManager(ctx, 2));
                        rvList.setAdapter(feedAdapter);
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(ctx,e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ctx, error.toString(), Toast.LENGTH_SHORT).show();
                    Log.d("Yolo", "onErrorResponse: "+ error.toString());
                    Toast.makeText(ctx, "Error Found", Toast.LENGTH_SHORT).show();
                }
            }){
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String,String> map = new HashMap<>();
            map.put("user-key","9b71538cb6d3419677ab58e98d85cfd6");
            return map;
        }

        @Override
        public Map<String, String> getParams() throws AuthFailureError {
            Map<String,String> map = new HashMap<>();
            map.put("lat",Lat);
            map.put("lon",Lon);
            return map;
        }
    };

    public FeedAdapter getFeedAdapter(){
        return feedAdapter;
    }

}
