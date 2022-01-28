package com.customer.esweetcustomer.Model;

public class MyCartModel {

    String productID;
    String productName;
    double productPrice;
    String productImageUrl;
    int totalQty;
    String customerGoogleUid;
    double totalPrice;
    String currentDate;
    String currentTime;



    public MyCartModel() {
    }

    public MyCartModel(String productID, String productName, double productPrice, String productImageUrl, int totalQty, String customerGoogleUid, double totalPrice, String currentDate, String currentTime) {
        this.productID = productID;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productImageUrl = productImageUrl;
        this.totalQty = totalQty;
        this.customerGoogleUid = customerGoogleUid;
        this.totalPrice = totalPrice;
        this.currentDate = currentDate;
        this.currentTime = currentTime;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public int getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(int totalQty) {
        this.totalQty = totalQty;
    }

    public String getCustomerGoogleUid() {
        return customerGoogleUid;
    }

    public void setCustomerGoogleUid(String customerGoogleUid) {
        this.customerGoogleUid = customerGoogleUid;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
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
