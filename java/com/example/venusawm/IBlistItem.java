package com.example.venusawm;

public class IBlistItem {
    public final String id, grnum, code, desc, qty, uom, date, time, vendor;

    public IBlistItem(String id, String grnum, String code, String desc, String qty, String uom, String date, String time, String vendor) {
        this.id = id;
        this.grnum = grnum;
        this.code = code;
        this.desc = desc;
        this.qty = qty;
        this.uom = uom;
        this.date = date;
        this.time = time;
        this.vendor = vendor;
    }

    public String getId() {
        return id;
    }

    public String getGrnum() {
        return grnum;
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

    public String getVendor() {
        return vendor;
    }
}
