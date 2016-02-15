package com.example.user.repeat.Other;

import java.io.Serializable;

/**
 * Created by user on 2015/12/29.
 */
public class PARecord implements Serializable { // For Adapter
    // ARecord = AnnouncementRecord
    // PRecord = ProblemRecord
    public String tag;
    public static final String TAG_ARecord = "AnnouncementRecord";
    public static final String TAG_PRecord = "ProblemRecord";
    // ProblemRecord
    private int PRSNo;
    private String CustomerNo;
    private String FLaborNo;
    private String SatisfactionDegree;
    private String ProblemStatus;

    private String ResponseContent;
    private String ResponseDate;
    private String ResponseID;
    private String ResponseRole;

    // AnnouncementRecord
    private int MPSNo;
    private String PushContent;
    private String CreateID;
    private String CreateDate;

    public int getPRSNo() {
        return PRSNo;
    }

    public void setPRSNo(int PRSNo) {
        this.PRSNo = PRSNo;
    }

    public String getCustomerNo() {
        return CustomerNo;
    }

    public void setCustomerNo(String customerNo) {
        CustomerNo = customerNo;
    }

    public String getResponseContent() {
        return ResponseContent;
    }

    public void setResponseContent(String responseContent) {
        ResponseContent = responseContent;
    }

    public String getResponseRole() {
        return ResponseRole;
    }

    public void setResponseRole(String responseRole) {
        ResponseRole = responseRole;
    }

    public String getFLaborNo() {
        return FLaborNo;
    }

    public void setFLaborNo(String FLaborNo) {
        this.FLaborNo = FLaborNo;
    }

    public String getResponseDate() {
        return ResponseDate;
    }

    public void setResponseDate(String responseDate) {
        ResponseDate = responseDate;
    }

    public String getResponseID() {
        return ResponseID;
    }

    public void setResponseID(String responseID) {
        ResponseID = responseID;
    }

    public String getSatisfactionDegree() {
        return SatisfactionDegree;
    }

    public void setSatisfactionDegree(String satisfactionDegree) {
        SatisfactionDegree = satisfactionDegree;
    }

    public String getProblemStatus() {
        return ProblemStatus;
    }

    public void setProblemStatus(String problemStatus) {
        ProblemStatus = problemStatus;
    }

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
