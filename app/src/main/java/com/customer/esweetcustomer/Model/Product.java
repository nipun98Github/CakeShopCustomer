package com.customer.esweetcustomer.Model;

public class Product {



    String ProductName;
    double ProductPrice;
    String ProductNote;
    String ProductDescription;
    String ProductCategoryName;
    String ProductImageUrl;

    public Product() {
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public double getProductPrice() {
        return ProductPrice;
    }

    public void setProductPrice(double productPrice) {
        ProductPrice = productPrice;
    }

    public String getProductNote() {
        return ProductNote;
    }

    public void setProductNote(String productNote) {
        ProductNote = productNote;
    }

    public String getProductDescription() {
        return ProductDescription;
    }

    public void setProductDescription(String productDescription) {
        ProductDescription = productDescription;
    }

    public String getProductCategoryName() {
        return ProductCategoryName;
    }

    public void setProductCategoryName(String productCategoryName) {
        ProductCategoryName = productCategoryName;
    }

    public String getProductImageUrl() {
        return ProductImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        ProductImageUrl = productImageUrl;
    }

    public Product(String productName, double productPrice, String productNote, String productDescription, String productCategoryName, String productImageUrl) {
        ProductName = productName;
        ProductPrice = productPrice;
        ProductNote = productNote;
        ProductDescription = productDescription;
        ProductCategoryName = productCategoryName;
        ProductImageUrl = productImageUrl;
    }
}