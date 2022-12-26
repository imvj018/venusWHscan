package com.example.venusawm;

public class IBHlistItem {
    public final String id, grnum, vendor, date, qty;

    public IBHlistItem(String id, String grnum, String vendor, String date, String qty) {
        this.id = id;
        this.grnum = grnum;
        this.vendor = vendor;
        this.date = date;
        this.qty = qty;
    }

    public String getId() {
        return id;
    }

    public String getGrnum() {
        return grnum;
    }

    public String getVendor() {
        return vendor;
    }

    public String getDate() {
        return date;
    }

    public String getQty() {
        return qty;
    }



}
