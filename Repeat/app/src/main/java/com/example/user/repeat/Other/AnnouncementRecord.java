package com.example.user.repeat.Other;

/**
 * Created by user on 2015/12/29.
 */
public class AnnouncementRecord {
    private int MPSNo;
    private String PushContent;
    private String CreateID;
    private String CreateDate;

    public int getMPSNo() {
        return MPSNo;
    }

    public void setMPSNo(int MPSNo) {
        this.MPSNo = MPSNo;
    }

    public String getPushContent() {
        return PushContent;
    }

    public void setPushContent(String pushContent) {
        PushContent = pushContent;
    }

    public String getCreateID() {
        return CreateID;
    }

    public void setCreateID(String createID) {
        CreateID = createID;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }
}
