package com.example.user.repeat.Other;

/**
 * Created by v on 2015/12/12.
 */
public class User {
    private static User user = new User();
    private String CustomerNo;
    private String FLaborNo;
    private String CellPhone;
    private String ChineseName;
    private String LaborPhoto;

    private User() {
        this.ChineseName = "強森";
    }

    public static User getUser() {
        return user;
    }

    public String getChineseName() {
        return ChineseName;
    }

    public void setChineseName(String chineseName) {
        ChineseName = chineseName;
    }

    public String getLaborPhoto() {
        return LaborPhoto;
    }

    public void setLaborPhoto(String laborPhoto) {
        LaborPhoto = laborPhoto;
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

    public String getCellPhone() {
        return CellPhone;
    }

    public void setCellPhone(String cellPhone) {
        CellPhone = cellPhone;
    }
}
