package com.example.venusawm;

public class stocklistItem {
    public final String id,  code, desc, qty, uom;

    public stocklistItem(String id, String code, String desc, String qty, String uom) {
        this.id = id;
        this.code = code;
        this.desc = desc;
        this.qty = qty;
        this.uom = uom;
    }
    public String getId() {
        return id;
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


}
