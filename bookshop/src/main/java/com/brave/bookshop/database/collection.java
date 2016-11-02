package com.brave.bookshop.database;

import cn.bmob.v3.BmobObject;

/**
 * Created by Brave on 2016/5/31.
 */
public class collection extends BmobObject {
    private String c_bid;

    private String c_uid;

    public String getC_uid() {
        return c_uid;
    }

    public void setC_uid(String c_uid) {
        this.c_uid = c_uid;
    }

    public String getC_bid() {
        return c_bid;
    }

    public void setC_bid(String c_bid) {
        this.c_bid = c_bid;
    }

}
