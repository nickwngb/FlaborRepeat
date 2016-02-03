package com.example.user.repeat.Other;

/**
 * Created by v on 2015/12/12.
 */
public class User {
    private static User user = new User();
    private String CustomerNo;
    private String FLaborNo;
    private String CellPhone;

    private User() {

    }

    public static User getUser() {
        return user;
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
