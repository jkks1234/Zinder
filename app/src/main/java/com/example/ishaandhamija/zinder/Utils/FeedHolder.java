package com.example.ishaandhamija.zinder.Utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ishaandhamija.zinder.R;

/**
 * Created by ishaandhamija on 09/06/17.
 */

public class FeedHolder extends RecyclerView.ViewHolder {

    TextView name, locality, costForTwo, rating;
    ImageView img;
    View vv;

    public FeedHolder(View itemView) {
        super(itemView);

        this.name = (TextView) itemView.findViewById(R.id.name);
        this.locality = (TextView) itemView.findViewById(R.id.locality);
        this.costForTwo = (TextView) itemView.findViewById(R.id.costForTwo);
        this.img = (ImageView) itemView.findViewById(R.id.img);
        this.rating = (TextView) itemView.findViewById(R.id.rating);
        this.vv = itemView;

    }
}
