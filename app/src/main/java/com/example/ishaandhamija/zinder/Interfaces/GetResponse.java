package com.example.ishaandhamija.zinder.Interfaces;

import com.example.ishaandhamija.zinder.Models.Restaurant;

import java.util.ArrayList;

/**
 * Created by ishaandhamija on 19/06/17.
 */

public interface GetResponse {

    public void onSuccess(ArrayList<Restaurant> restList);
    public void onError();

}
