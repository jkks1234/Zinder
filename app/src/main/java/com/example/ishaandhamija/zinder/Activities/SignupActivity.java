package com.example.ishaandhamija.zinder.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ishaandhamija.zinder.Models.Restaurant;
import com.example.ishaandhamija.zinder.Models.User2;
import com.example.ishaandhamija.zinder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity {

    EditText inputEmail, inputPassword, inputName, inputAge, inputSex;
    Button btnSignIn, btnSignUp, btnResetPassword;
    ProgressBar progressBar;
    FirebaseAuth auth;
    DatabaseReference firebaseDatabase;
    FirebaseDatabase firebaseInstance;
    FirebaseUser userr;

    String userId;

    public static final String TAG = "DD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
//        userr = auth.getCurrentUser();
//        userId = userr.getUid();
        getSupportActionBar().setTitle("SignUp");

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputAge = (EditText) findViewById(R.id.age);
        inputName = (EditText) findViewById(R.id.name);
        inputSex = (EditText) findViewById(R.id.sex);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        firebaseInstance = FirebaseDatabase.getInstance();
        firebaseDatabase = firebaseInstance.getReference("users");

        firebaseInstance.getReference("zinder-dc0b2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String appTitle = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name = inputName.getText().toString().trim();
                final String stringAge = inputAge.getText().toString().trim();
                final Integer age = Integer.parseInt(stringAge);
                final Integer sex;
                String stringSex = inputSex.getText().toString().trim();
                if (stringSex.equals("MALE") || stringSex.equals("Male") || stringSex.equals("M") || stringSex.equals("m") || stringSex.equals("male")){
                    sex = 1;
                }
                else{
                    sex = 2;
                }
                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Enter Name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(stringAge)) {
                    Toast.makeText(getApplicationContext(), "Enter Age!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(stringSex)) {
                    Toast.makeText(getApplicationContext(), "Enter Sex!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter Email Address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter Password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    inputPassword.setError("Password too short, enter minimum 6 characters!");
                    return;
                }

                if (age < 0 || age > 100) {
                    inputAge.setError("Enter Correct Age!");
                    return;
                }

                if (age < 18 || age > 50) {
                    inputAge.setError("You are not Eligible!");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SignupActivity.this, "Sign Up Hogya", Toast.LENGTH_SHORT).show();
//                                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
//                                    finish();
                                    if (TextUtils.isEmpty(userId)){
                                        createUser(name, age, sex, email, password);
                                    }
                                    else{
                                        updateUser(name, age, sex, email, password);
                                    }
                                }
                            }
                        });

            }
        });

    }
    public void createUser(String name, Integer age, Integer sex, String email, String password) {

        if (TextUtils.isEmpty(userId)) {
            userr = auth.getCurrentUser();
            userId = userr.getUid();
        }

        ArrayList<Restaurant> al = new ArrayList<>();

        User2 user = new User2(name, age, sex, email, password, al);

        firebaseDatabase.child(userId).setValue(user);

        addUserChangeListener();
    }

    private void addUserChangeListener() {
        firebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User2 user = dataSnapshot.getValue(User2.class);

                if (user == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }

                Log.e(TAG, "User data is changed!" + user.getName());

                Toast.makeText(SignupActivity.this, "User Created", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to read user", error.toException());
                Toast.makeText(SignupActivity.this, "Error aagyi bc!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateUser(String name, Integer age, Integer sex, String email, String password) {

        if (!TextUtils.isEmpty(name))
            firebaseDatabase.child(userId).child("name").setValue(name);

        if (age != null)
            firebaseDatabase.child(userId).child("age").setValue(age);

        if (sex != null)
            firebaseDatabase.child(userId).child("sex").setValue(sex);

        if (!TextUtils.isEmpty(email))
            firebaseDatabase.child(userId).child("email").setValue(email);

        if (!TextUtils.isEmpty(password))
            firebaseDatabase.child(userId).child("password").setValue(password);

    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
