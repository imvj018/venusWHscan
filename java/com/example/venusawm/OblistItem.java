package com.example.venusawm;

public class OblistItem {
    public final String id, delnum, code, desc, qty, uom, date, time, customer;

    public OblistItem(String id, String delnum, String code, String desc, String qty, String uom, String date, String time, String customer) {
        this.id = id;
        this.delnum = delnum;
        this.code = code;
        this.desc = desc;
        this.qty = qty;
        this.uom = uom;
        this.date = date;
        this.time = time;
        this.customer = customer;
    }
    public String getId() {
        return id;
    }
    public String getDelnum() {
        return delnum;
    }
    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public String getQty() {
        return qty;
    }

    public String getUom() {
        return uom;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getCustomer() {
        return customer;
    }
}
