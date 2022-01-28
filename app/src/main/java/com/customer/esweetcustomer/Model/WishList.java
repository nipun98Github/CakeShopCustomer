package com.customer.esweetcustomer.Model;

public class WishList {

    String customerGoogleUid;
    String productID;
    String currentDate;
    String currentTime;


    public WishList() {
    }

    public WishList(String customerGoogleUid, String productID, String currentDate, String currentTime) {
        this.customerGoogleUid = customerGoogleUid;
        this.productID = productID;
        this.currentDate = currentDate;
        this.currentTime = currentTime;
    }

    public String getCustomerGoogleUid() {
        return customerGoogleUid;
    }

    public void setCustomerGoogleUid(String customerGoogleUid) {
        this.customerGoogleUid = customerGoogleUid;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }
}
