package com.customer.esweetcustomer.Model;

public class CustomizeOrder {


    String categoryName;
    String size;
    String description;
    String color;
    String orderImageUrlPath;


    public CustomizeOrder() {
    }



    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getOrderImageUrlPath() {
        return orderImageUrlPath;
    }

    public void setOrderImageUrlPath(String orderImageUrlPath) {
        this.orderImageUrlPath = orderImageUrlPath;
    }

    public CustomizeOrder(String categoryName, String size, String description, String color, String orderImageUrlPath) {

        this.categoryName = categoryName;
        this.size = size;
        this.description = description;
        this.color = color;
        this.orderImageUrlPath = orderImageUrlPath;
    }
}
