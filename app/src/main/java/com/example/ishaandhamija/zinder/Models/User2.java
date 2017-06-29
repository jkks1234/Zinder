package com.example.ishaandhamija.zinder.Models;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ishaandhamija on 03/06/17.
 */

public class User2 {

    String name;
    Integer age;
    Integer sex;
    String city;
    String profilePicUrl;
    String email;
    String password;
    ArrayList<Restaurant> restaurants;

    public User2() {

    }

    public User2(String name, Integer age, Integer sex, String city, String profilePicUrl, String email, String password, ArrayList<Restaurant> restaurants) {
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.city = city;
        this.profilePicUrl = profilePicUrl;
        this.email = email;
        this.password = password;
        this.restaurants = restaurants;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public Integer getSex() {
        return sex;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public ArrayList<Restaurant> getRestaurants() {
        return restaurants;
    }

    public String getCity() {
        return city;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }
}
