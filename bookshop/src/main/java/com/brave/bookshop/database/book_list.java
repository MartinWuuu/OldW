package com.brave.bookshop.database;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Brave on 2016/5/30.
 */
public class book_list extends BmobObject{

    private String b_name;
    private Integer b_price;
    private String b_details;
    private Integer b_classification;
    private Integer b_quality;
    private String b_uid;
    private String b_browse;
    private BmobFile b_photo;
    private Boolean b_state;



    public Boolean getB_state() {
        return b_state;
    }

    public void setB_state(Boolean b_state) {
        this.b_state = b_state;
    }

    public Integer getB_quality() {
        return b_quality;
    }

    public Integer getB_classification() {
        return b_classification;
    }

    public void setB_classification(Integer b_classification) {
        this.b_classification = b_classification;
    }

    public void setB_quality(Integer b_quality) {
        this.b_quality = b_quality;
    }

    public BmobFile getB_photo() {
        return b_photo;
    }

    public void setB_photo(BmobFile b_photo) {
        this.b_photo = b_photo;
    }

    public String getB_name() {
        return b_name;
    }

    public void setB_name(String b_name) {
        this.b_name = b_name;
    }

    public Integer getB_price() {
        return b_price;
    }

    public void setB_price(Integer b_price) {
        this.b_price = b_price;
    }

    public String getB_details() {
        return b_details;
    }

    public void setB_details(String b_details) {
        this.b_details = b_details;
    }


    public String getB_uid() {
        return b_uid;
    }

    public void setB_uid(String b_uid) {
        this.b_uid = b_uid;
    }

    public String getB_browse() {
        return b_browse;
    }

    public void setB_browse(String b_browse) {
        this.b_browse = b_browse;
    }




}
