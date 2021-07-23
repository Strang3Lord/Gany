package com.idkwts.bruh.Model;

public class User {

    private String email;
    private String Groups;
    private String username;
    private String bio;
    private String imageurl,status;
    private String id;
    private String bg;
    private boolean isSelected;


    public User() {
    }

    public User(String email, String username, String bio, String imageurl,String status, String id,String bg) {
        this.email = email;
        this.username = username;
        this.bio = bio;
        this.imageurl = imageurl;
        this.status = status;
        this.id = id;
        this.bg = bg;
    }

    public String getGroups() {
        return Groups;
    }

    public void setGroups(String groups) {
        Groups = groups;
    }

    public String getBg() {
        return bg;
    }

    public void setBg(String bg) {
        this.bg = bg;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
