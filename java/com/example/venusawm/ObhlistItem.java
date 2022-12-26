package com.example.venusawm;

public class ObhlistItem {
    public final String id, delnum, customer, date, qty;

    public ObhlistItem(String id, String delnum, String customer, String date, String qty) {
        this.id = id;
        this.delnum = delnum;
        this.customer = customer;
        this.date = date;
        this.qty = qty;
    }

    public String getId() {
        return id;
    }

    public String getdelnum() {
        return delnum;
    }

    public String getcustomer() {
        return customer;
    }

    public String getDate() {
        return date;
    }

    public String getQty() {
        return qty;
    }

}
