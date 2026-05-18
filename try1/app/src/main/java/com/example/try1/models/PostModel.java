package com.example.try1.models;

public class PostModel {
    private  String postid;
    private  String postimage;
    private  String postedby;
    private  String postdiscription;
    private  long postedAt;
    private  int postlikes;

    public int getPostlikes() {
        return postlikes;
    }

    public void setPostlikes(int postlikes) {
        this.postlikes = postlikes;
    }

    public PostModel(String postid, String postimage, String postedby, String postdiscription, long postedAt) {
        this.postid = postid;
        this.postimage = postimage;
        this.postedby = postedby;
        this.postdiscription = postdiscription;
        this.postedAt = postedAt;
    }

    public PostModel() {
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getPostedby() {
        return postedby;
    }

    public void setPostedby(String postedby) {
        this.postedby = postedby;
    }

    public String getPostdiscription() {
        return postdiscription;
    }

    public void setPostdiscription(String postdiscription) {
        this.postdiscription = postdiscription;
    }

    public long getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(long postedAt) {
        this.postedAt = postedAt;
    }
}
