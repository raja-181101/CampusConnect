package com.example.try1.models;

public class SubjectModel {
    private String subjectName;
    private String notesid;
    private String displayName;
    private String sec;
    private String year;
    private String branch;
    private String uploadedBy;
    private long uploadedAt;
    private long fileSize;
    private String subjectkey;

    public SubjectModel() {}

    public SubjectModel(String subjectName, String notesid, String displayName, String sec) {
        this.subjectName = subjectName;
        this.notesid = notesid;
        this.displayName = displayName;
        this.sec = sec;
    }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public String getNotesid() { return notesid; }
    public void setNotesid(String notesid) { this.notesid = notesid; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getSec() { return sec; }
    public void setSec(String sec) { this.sec = sec; }
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }
    public String getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; }
    public long getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(long uploadedAt) { this.uploadedAt = uploadedAt; }
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public String getSubjectkey() {
        return subjectkey;
    }

    public void setSubjectkey(String subjectkey) {
        this.subjectkey = subjectkey;
    }
}
