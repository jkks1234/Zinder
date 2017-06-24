package com.example.ishaandhamija.zinder.Interfaces;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

/**
 * Created by ishaandhamija on 15/06/17.
 */

public interface GetLL {

    public void onSuccess(Context ctx, String area, String city, String latitude, String longitude, RecyclerView rvList);
    public void onError(String errorMsg);

}
