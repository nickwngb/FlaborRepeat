package com.example.user.repeat.Other;

import java.io.Serializable;

/**
 * Created by user on 2015/12/10.
 */
public class ProblemRecord implements Serializable {
    private int PRSNo;
    private String CustomerNo;
    private String FLaborNo;
    private String SatisfactionDegree;
    private String ProblemStatus;
    // Last Response
    private String ResponseContent;
    private String ResponseDate;
    private String ResponseID;
    private String ResponseRole;
    public ProblemRecord() {

    }

    public String getResponseContent() {
        return ResponseContent;
    }

    public void setResponseContent(String responseContent) {
        ResponseContent = responseContent;
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

    public String getResponseRole() {
        return ResponseRole;
    }

    public void setResponseRole(String responseRole) {
        ResponseRole = responseRole;
    }

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

    public String getFLaborNo() {
        return FLaborNo;
    }

    public void setFLaborNo(String FLaborNo) {
        this.FLaborNo = FLaborNo;
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
}
