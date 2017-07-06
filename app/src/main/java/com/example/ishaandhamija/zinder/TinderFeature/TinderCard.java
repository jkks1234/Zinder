package com.example.ishaandhamija.zinder.TinderFeature;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ishaandhamija.zinder.Activities.DashboardActivity;
import com.example.ishaandhamija.zinder.Interfaces.GetUsers;
import com.example.ishaandhamija.zinder.Interfaces.OnRightSwipe;
import com.example.ishaandhamija.zinder.Models.User2;
import com.example.ishaandhamija.zinder.R;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;

import java.util.ArrayList;

/**
 * Created by ishaandhamija on 22/06/17.
 */

@Layout(R.layout.tinder_card_view)
public class TinderCard {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseInstance = FirebaseDatabase.getInstance();
    DatabaseReference firebaseDatabase = firebaseInstance.getReference("userss");
    String userId = auth.getCurrentUser().getUid();
    ArrayList<Profile> swippedList;
    OnRightSwipe onRightSwipe;

    Firebase userRef;
    ArrayList<User2> allUsers = new ArrayList<>();

    @View(R.id.profileImageView)
    private ImageView profileImageView;

    @View(R.id.nameAgeTxt)
    private TextView nameAgeTxt;

    @View(R.id.locationNameTxt)
    private TextView locationNameTxt;

    private Profile mProfile;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;

    public TinderCard(Context context, Profile profile, SwipePlaceHolderView swipeView) {
        mContext = context;
        mProfile = profile;
        mSwipeView = swipeView;
    }

    public void getAllUsers() {
        userRef = new Firebase("https://zinder-dc0b2.firebaseio.com/").child("userss");
        userRef.addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                for (com.firebase.client.DataSnapshot snap : dataSnapshot.getChildren()) {
                    User2 user = snap.getValue(User2.class);
                    allUsers.add(user);
                }
            }


            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    @Resolve
    private void onResolved(){
        Glide.with(mContext).load(mProfile.getUrl()).into(profileImageView);
        nameAgeTxt.setText(mProfile.getName() + ", " + mProfile.getAge());
        locationNameTxt.setText(mProfile.getLocation());
    }

    @SwipeOut
    private void onSwipedOut(){
        Log.d("EVENT", "onSwipedOut");
        mSwipeView.addView(this);
    }

    @SwipeCancelState
    private void onSwipeCancelState(){
        Log.d("EVENT", "onSwipeCancelState");
    }

    @SwipeIn
    private void onSwipeIn(){
        Log.d("EVENT", "onSwipedIn");

        getAllUsers();

        firebaseDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("swippedPeopleArrayList").getValue() != null){
                    swippedList = (ArrayList<Profile>) dataSnapshot.child("swippedPeopleArrayList").getValue();
                }
                else{
                    swippedList = new ArrayList<>();
                }
                    swippedList.add(new Profile(mProfile.getUrl(), mProfile.getName(), mProfile.getAge(), mProfile.getLocation()));
                    onRightSwipe.onSwipe(swippedList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        onRightSwipe = new OnRightSwipe() {
            @Override
            public void onSwipe(ArrayList<Profile> swippedList) {
                firebaseDatabase.child(userId).child("swippedPeopleArrayList").setValue(swippedList);
                for (int i=0;i<allUsers.size();i++){
                    boolean matchFound = false;
                    if (allUsers.get(i).getName().equals(swippedList.get(swippedList.size()-1).getName())){
                        if (allUsers.get(i).getSwippedPeopleArrayList() != null) {
                            for (int j = 0; j < allUsers.get(i).getSwippedPeopleArrayList().size(); j++) {
                                if (allUsers.get(i).getSwippedPeopleArrayList().get(j).getUrl().equals(mProfile.getUrl())) {
                                    Log.d("Hogyaa", "Matched!!!");
                                    matchFound = true;
                                    User2 userr = null;
                                    for (int k=0;k<allUsers.size();k++){
                                        if (allUsers.get(k).getProfilePicUrl().equals(mProfile.getUrl())){
                                            userr = allUsers.get(k);
                                            break;
                                        }
                                    }
                                    generateNotification(allUsers.get(i));
                                    matchIsFound(userr, allUsers.get(i));
                                    break;
                                }
                            }
                        }
                    }
                    if (matchFound){
                        break;
                    }
                }
            }
        };
    }

    @SwipeInState
    private void onSwipeInState(){
        Log.d("EVENT", "onSwipeInState");
    }

    @SwipeOutState
    private void onSwipeOutState(){
        Log.d("EVENT", "onSwipeOutState");
    }

    void generateNotification(User2 user){
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        char otherUserSex = 'f';
        if (user.getSex() == 1){
            otherUserSex = 'm';
        }
        if (otherUserSex == 'f') {
            CharSequence message = "Your blind dated is fixed with Ms. " + user.getName() + ".\nHer Details " +
                    "and Contact Number has been sent to you via SMS, kindly choose a nice place for her :)";
            String title="Match Found";
            int icon = R.drawable.dinner;
            long when = System.currentTimeMillis();

            Notification notification;
            notification = new NotificationCompat.Builder(mContext)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(icon)
                    .setWhen(when)
                    .build();

            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            notificationManager.notify(0, notification);
        }
        else{
            String message = "Your blind date is fixed with Mr. " + user.getName() + "." + "\n" + "His Details " +
                    "and Contact Number has been sent to you via SMS, kindly choose a nice place for him :)";
            String title="Match Found";
            int icon = R.drawable.dinner;
            long when = System.currentTimeMillis();

            Notification notification;
            notification = new NotificationCompat.Builder(mContext)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(icon)
                    .setWhen(when)
                    .build();

            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            notificationManager.notify(0, notification);
        }

    }

    void matchIsFound(User2 user1, User2 user2){
        Toast.makeText(mContext, "Match Found!!", Toast.LENGTH_SHORT).show();

        String user1ContactNo = user1.getContactNo().toString();
        if (user1.getSex() == 1) {
            String msgForUser1 = "Match Found!!\nYou have a blind date fixed with Ms. " + user2.getName() +
                    "...\nHer Details :-\nName : " + user2.getName() + "\nAge : " + user2.getAge()
                    + "\nCity : " + user2.getCity() + "\nContact Number : " + user2.getContactNo()
                    + "\nEmail Id : " + user2.getEmail();
            sendMessage(user1ContactNo, msgForUser1);
        }
        else{
            String msgForUser1 = "Match Found!!\nYou have a blind date fixed with Mr. " + user2.getName() +
                    "...\nHis Details :-\nName : " + user2.getName() + "\nAge : " + user2.getAge()
                    + "\nCity : " + user2.getCity() + "\nContact Number : " + user2.getContactNo()
                    + "\nEmail Id : " + user2.getEmail();
            sendMessage(user1ContactNo, msgForUser1);
        }

        String user2ContactNo = user2.getContactNo().toString();
        if (user2.getSex() == 1) {
            String msgForUser2 = "Match Found!!\nYou have a blind date fixed with Ms. " + user1.getName() +
                    "...\nHer Details :-\nName : " + user1.getName() + "\nAge : " + user1.getAge()
                    + "\nCity : " + user1.getCity() + "\nContact Number : " + user1.getContactNo()
                    + "\nEmail Id : " + user1.getEmail();
            sendMessage(user2ContactNo, msgForUser2);
        }
        else{
            String msgForUser2 = "Match Found!!\nYou have a blind date fixed with Mr. " + user1.getName() +
                    "...\nHis Details :-\nName : " + user1.getName() + "\nAge : " + user1.getAge()
                    + "\nCity : " + user1.getCity() + "\nContact Number : " + user1.getContactNo()
                    + "\nEmail Id : " + user1.getEmail();
            sendMessage(user2ContactNo, msgForUser2);
        }
    }

    public boolean sendMessage(String phno, String msg) {
        try {
            if (phno == null) {
                return false;
            } else {
                SmsManager smsmanager = SmsManager.getDefault();
                smsmanager.sendTextMessage(phno, null, msg, null, null);
                Log.d("YahaHu", "sendMessage: " + phno);
                return true;
            }
        }
        catch (Exception e)
        {
            return false;
        }
    }

}
