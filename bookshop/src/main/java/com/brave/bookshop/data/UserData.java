package com.brave.bookshop.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Brave on 2016/5/31.
 */
public class UserData {

    private static SharedPreferences sp;
    private static SharedPreferences.Editor e;

    private static void init(Context c){
        sp = c.getSharedPreferences("user",0);
        e = sp.edit();
    }

    public static void setUserData(Context c, String email, String name, String phone){
        init(c);
        e.putString("email",email);
        e.putString("name",name);
        e.putString("phone",phone);
        e.putBoolean("isLogin",true);
        e.commit();
    }

    public static String getEmail(Context c){
        init(c);
        return sp.getString("email","");
    }

    public static String getName(Context c){
        init(c);
        return sp.getString("name","");
    }

    public static String getPhone(Context c){
        init(c);
        return sp.getString("phone","");
    }

    public static boolean isLogin(Context c){
        init(c);
        return sp.getBoolean("isLogin",false);
    }

    public static void clearUserData(Context c){
        init(c);
        e.putBoolean("isLogin",false);
        e.commit();
    }

}
