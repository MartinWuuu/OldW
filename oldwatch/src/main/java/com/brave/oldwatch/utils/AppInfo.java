package com.brave.oldwatch.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Brave on 2016/11/1.
 */

public class AppInfo {

    public static final String HttpUrl = "http://59.110.12.158/index.php/Interface/";

    public static final String HttpImgUrl = "http://59.110.12.158/Public/Uploads/";

    public static final String HttpAmrUrl = "http://59.110.12.158/Public/Audio/";

    private static final String SPName = "oldwatch";


    public static void save(Context context,String key, String value){
        SharedPreferences sp = context.getSharedPreferences(SPName,0);
        SharedPreferences.Editor e = sp.edit();
        e.putString(key,value);
        e.commit();
    }

    public static void save(Context context,String key, boolean value){
        SharedPreferences sp = context.getSharedPreferences(SPName,0);
        SharedPreferences.Editor e = sp.edit();
        e.putBoolean(key,value);
        e.commit();
    }

    public static String getString(Context context,String key){
        SharedPreferences sp = context.getSharedPreferences(SPName,0);
        return sp.getString(key,"");
    }

    public static boolean getBoolean(Context context,String key){
        SharedPreferences sp = context.getSharedPreferences(SPName,0);
        return sp.getBoolean(key,false);
    }

}
