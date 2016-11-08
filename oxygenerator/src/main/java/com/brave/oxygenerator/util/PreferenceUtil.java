package com.brave.oxygenerator.util;

import android.content.Context;
import android.content.SharedPreferences;


public class PreferenceUtil {

    private static final String NAME = "oxygenerator";

    public static String getGesturePassword(Context context) {
        SharedPreferences sp = context.getSharedPreferences("gesturePassword", Context.MODE_PRIVATE);
        String password = sp.getString("password", "");
        return password;
    }

    public static void setGesturePassword(Context context, String gesturePassword) {
        SharedPreferences sp = context.getSharedPreferences("gesturePassword", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("password", gesturePassword);
        editor.commit();
    }


    public static boolean isLogin(Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        boolean is = sp.getBoolean("isLogin", false);
        return is;
    }

    public static void setIsLogin(Context context, boolean isLogin) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isLogin", isLogin);
        editor.commit();
    }


    public static void setUserPassword(Context context, String userPassword) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("password", userPassword);
        editor.commit();
    }

    public static void setUserName(Context context, String userName) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username", userName);
        editor.commit();
    }

    public static String getUserPassword(Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        String password = sp.getString("password", "");
        return password;
    }

    public static String getUserName(Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        String username = sp.getString("username", "");
        return username;
    }

}
