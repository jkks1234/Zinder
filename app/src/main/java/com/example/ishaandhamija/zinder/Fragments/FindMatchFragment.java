package com.example.ishaandhamija.zinder.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ishaandhamija.zinder.Activities.DashboardActivity;
import com.example.ishaandhamija.zinder.Interfaces.GetUsers;
import com.example.ishaandhamija.zinder.Models.Restaurant;
import com.example.ishaandhamija.zinder.Models.User2;
import com.example.ishaandhamija.zinder.R;
import com.example.ishaandhamija.zinder.TinderFeature.Profile;
import com.example.ishaandhamija.zinder.TinderFeature.TinderCard;
import com.example.ishaandhamija.zinder.TinderFeature.Utils;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by ishaandhamija on 21/06/17.
 */

public class FindMatchFragment extends Fragment {

    Context mContext;
    private SwipePlaceHolderView mSwipeView;
    Firebase userRef;
    ArrayList<User2> allUsers;
    ArrayList<User2> selectedUsers;
    ArrayList<Profile> selectedUserProfiles;
    GetUsers getUsers;
    ProgressDialog loadingDialog;

    public static final String TAG = "UUU";

    public FindMatchFragment() {
    }

    public FindMatchFragment(Context ctx) {
        this.mContext = ctx;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_find_match, container, false);

        allUsers = new ArrayList<>();
        selectedUsers = new ArrayList<>();
        selectedUserProfiles = new ArrayList<>();
        loadingDialog = ProgressDialog.show(mContext, "", "Loading. Please wait...", true);

        Firebase.setAndroidContext(mContext);
        userRef = new Firebase("https://zinder-dc0b2.firebaseio.com/").child("userss");

        userRef.addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                for (com.firebase.client.DataSnapshot snap : dataSnapshot.getChildren()){
                    User2 user = snap.getValue(User2.class);
                    allUsers.add(user);
                }
                getUsers.onSuccess(allUsers);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                getUsers.onError("No Other Users");
            }
        });

        getUsers = new GetUsers() {
            @Override
            public void onSuccess(ArrayList<User2> userList) {
                ArrayList<Restaurant> mySelectedRestaurants = DashboardActivity.getSelectedRestaurants();

                if (mySelectedRestaurants != null) {
                    for (int i = 0; i < userList.size(); i++) {
                        User2 ekUser = userList.get(i);
                        if (ekUser.getRestaurants() != null) {
                            ArrayList<Restaurant> ekUserKeRestaurants = ekUser.getRestaurants();
                            for (int j = 0; j < ekUserKeRestaurants.size(); j++) {
                                for (int k = 0; k < mySelectedRestaurants.size(); k++) {
                                    if (ekUserKeRestaurants.get(j).getName().equals(mySelectedRestaurants.get(k).getName())) {
                                        selectedUsers.add(ekUser);
                                    }
                                }
                            }
                        }
                    }

                    if (selectedUsers != null) {
                        for (int i = 0; i < selectedUsers.size(); i++) {
                            selectedUserProfiles.add(new Profile(selectedUsers.get(i).getProfilePicUrl(), selectedUsers.get(i).getName(), selectedUsers.get(i).getAge(), selectedUsers.get(i).getCity()));
                        }

                        mSwipeView = (SwipePlaceHolderView)rootView.findViewById(R.id.swipeView);

                        mSwipeView.getBuilder()
                                .setDisplayViewCount(3)
                                .setSwipeDecor(new SwipeDecor()
                                        .setPaddingTop(20)
                                        .setRelativeScale(0.01f)
                                        .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                                        .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));


                        rootView.findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mSwipeView.doSwipe(false);
                            }
                        });

                        rootView.findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mSwipeView.doSwipe(true);
                            }
                        });

                        for(Profile profile : selectedUserProfiles){
                            mSwipeView.addView(new TinderCard(mContext, profile, mSwipeView));
                        }
                    }
                }
                else{
                    Toast.makeText(mContext, "No Restaurants Added to Favourites", Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onError(String errorMsg) {
                loadingDialog.dismiss();
                Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
            }
        };

        return rootView;
    }
}
