package com.brave.bookshop.database;

import cn.bmob.v3.BmobObject;

/**
 * Created by Brave on 2016/5/30.
 */
public class user extends BmobObject {

    public user(){
        this.setTableName("user");
    }

    public String getU_contact() {
        return u_contact;
    }

    public void setU_contact(String u_contact) {

        this.u_contact = u_contact;
    }

    public String getU_name() {

        return u_name;
    }

    public void setU_name(String u_name) {

        this.u_name = u_name;
    }

    public String getU_email() {

        return u_email;
    }

    public void setU_email(String u_email)
    {
        this.u_email = u_email;
    }

    public String getU_password() {
        return u_password;
    }

    public void setU_password(String u_password) {

        this.u_password = u_password;
    }



    private String u_contact;
    private String u_name;
    private String u_email;
    private String u_password;
}
