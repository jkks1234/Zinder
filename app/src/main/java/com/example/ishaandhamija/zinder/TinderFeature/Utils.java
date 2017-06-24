package com.example.ishaandhamija.zinder.TinderFeature;

import android.content.Context;
import android.util.Log;

import com.example.ishaandhamija.zinder.Models.User2;
import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ishaandhamija on 22/06/17.
 */

public class Utils {

    private static final String TAG = "Utils";

    public static List<Profile> loadProfiles(Context context){

        List<Profile> profileList = new ArrayList<>();

//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                User2 user = dataSnapshot.getValue(User2.class);
//
//                if (user == null) {
//                    Log.e(TAG, "User data is null!");
//                    return;
//                }
//
//                Log.e(TAG, "User data is changed!" + user.getName());
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                Log.e(TAG, "Failed to read user", error.toException());
//            }
//        });

        profileList.add(new Profile("https://pbs.twimg.com/profile_images/572905100960485376/GK09QnNG.jpeg", "Sofia",  20, "New York"));
        profileList.add(new Profile("https://pbs.twimg.com/profile_images/572905100960485376/GK09QnNG.jpeg", "Sofia",  20, "New York"));
        profileList.add(new Profile("https://pbs.twimg.com/profile_images/572905100960485376/GK09QnNG.jpeg", "Sofia",  20, "New York"));
        profileList.add(new Profile("https://pbs.twimg.com/profile_images/572905100960485376/GK09QnNG.jpeg", "Sofia",  20, "New York"));
        profileList.add(new Profile("https://pbs.twimg.com/profile_images/572905100960485376/GK09QnNG.jpeg", "Sofia",  20, "New York"));

        return profileList;
    }

}
