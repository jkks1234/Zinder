package com.example.ishaandhamija.zinder.TinderFeature;

/**
 * Created by ishaandhamija on 22/06/17.
 */

public class Profile {

    String url;
    String name;
    Integer age;
    String location;

    public Profile(String url, String name, Integer age, String location) {
        this.url = url;
        this.name = name;
        this.age = age;
        this.location = location;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public String getLocation() {
        return location;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
