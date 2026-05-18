package com.example.try1.models;

public class postlayoutmodel {
    int profilephoto,postimage,save;
    String postuername,aboutpost,like,comment,send;

    public postlayoutmodel(int profilephoto, int postimage, int save, String postuername, String aboutpost, String like, String comment, String send) {
        this.profilephoto = profilephoto;
        this.postimage = postimage;
        this.save = save;
        this.postuername = postuername;
        this.aboutpost = aboutpost;
        this.like = like;
        this.comment = comment;
        this.send = send;
    }

    public postlayoutmodel() {
    }

    public int getProfilephoto() {
        return profilephoto;
    }

    public void setProfilephoto(int profilephoto) {
        this.profilephoto = profilephoto;
    }

    public int getPostimage() {
        return postimage;
    }

    public void setPostimage(int postimage) {
        this.postimage = postimage;
    }

    public int getSave() {
        return save;
    }

    public void setSave(int save) {
        this.save = save;
    }

    public String getPostuername() {
        return postuername;
    }

    public void setPostuername(String postuername) {
        this.postuername = postuername;
    }

    public String getAboutpost() {
        return aboutpost;
    }

    public void setAboutpost(String aboutpost) {
        this.aboutpost = aboutpost;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSend() {
        return send;
    }

    public void setSend(String send) {
        this.send = send;
    }
}
