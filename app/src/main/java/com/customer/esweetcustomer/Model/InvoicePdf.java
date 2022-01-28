package com.customer.esweetcustomer.Model;

public class InvoicePdf {
    String product;
    String qty;
    String amount;

    public InvoicePdf() {
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public InvoicePdf(String product, String qty, String amount) {
        this.product = product;
        this.qty = qty;
        this.amount = amount;
    }
}
