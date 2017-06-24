package com.example.ishaandhamija.zinder.Models;

/**
 * Created by ishaandhamija on 07/06/17.
 */

public class Restaurant {

    String name;
    String locality;
    String picUrl;
    Integer costForTwo;
    String rating;
    Integer contactNo;
    String address;
    String menu;
    String photos;
    String thumb;
    boolean selected;

    public Restaurant(){}

    public Restaurant(String name, String locality, String picUrl, Integer costForTwo, String rating, Integer contactNo, String address, String menu, String photos, String thumb, boolean selected) {
        this.name = name;
        this.locality = locality;
        this.picUrl = picUrl;
        this.costForTwo = costForTwo;
        this.rating = rating;
        this.contactNo = contactNo;
        this.address = address;
        this.menu = menu;
        this.photos = photos;
        this.thumb = thumb;
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public String getLocality() {
        return locality;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public Integer getCostForTwo() {
        return costForTwo;
    }

    public String getRating() {
        return rating;
    }

    public Integer getContactNo() {
        return contactNo;
    }

    public String getAddress() {
        return address;
    }

    public String getMenu() {
        return menu;
    }

    public String getPhotos() {
        return photos;
    }

    public String getThumb() {
        return thumb;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
