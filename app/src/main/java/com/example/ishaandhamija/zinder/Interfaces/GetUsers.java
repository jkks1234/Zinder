package com.example.ishaandhamija.zinder.Interfaces;

import com.example.ishaandhamija.zinder.Models.User2;

import java.util.ArrayList;

/**
 * Created by ishaandhamija on 26/06/17.
 */

public interface GetUsers {

    public void onSuccess(ArrayList<User2> userList);
    public void onError(String errorMsg);

}
