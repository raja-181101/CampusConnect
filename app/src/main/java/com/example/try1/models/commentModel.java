package com.example.try1.models;

public class commentModel {
    private String commentedBy;
    private String commentBody;
    private long  commentedAt;
    private String answerimg;

    public String getAnswerimg() {
        return answerimg;
    }

    public void setAnswerimg(String answerimg) {
        this.answerimg = answerimg;
    }

    public String getCommentedBy() {
        return commentedBy;
    }

    public void setCommentedBy(String commentedBy) {
        this.commentedBy = commentedBy;
    }

    public String getCommentBody() {
        return commentBody;
    }

    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
    }

    public long getCommentedAt() {
        return commentedAt;
    }

    public void setCommentedAt(long commentedAt) {
        this.commentedAt = commentedAt;
    }
}
