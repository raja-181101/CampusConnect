package com.example.try1.models;

public class FollowModel {

    private String followedBy;
    private Long followedAt;
    private String followingTo;

    public FollowModel() {
    }

    public String getFollowedBy() {
        return followedBy;
    }

    public void setFollowedBy(String followedBy) {
        this.followedBy = followedBy;
    }

    public Long getFollowedAt() {
        return followedAt;
    }

    public void setFollowedAt(Long followedAt) {
        this.followedAt = followedAt;
    }

    public String getFollowingTo() {
        return followingTo;
    }

    public void setFollowingTo(String followingTo) {
        this.followingTo = followingTo;
    }
}
