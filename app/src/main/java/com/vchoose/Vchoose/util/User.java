package com.vchoose.Vchoose.util;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by Sam on 7/7/2015.
 */
public class User {
    static public boolean login_status = false;
    static public boolean facebookLogin;
    static public String auth_token;
    static public String user_name;
    static public String user_image;
    static public Drawable user_photo;

    public static String getUser_name() {
        return user_name;
    }

    public static void setUser_name(String user_name) {
        User.user_name = user_name;
    }

    public static String getUser_image() {
        return user_image;
    }

    public static void setUser_image(String user_image) {
        User.user_image = user_image;
    }

    public static String getAuth_token() {
        return auth_token;
    }

    public static void setAuth_token(String auth_token) {
        User.auth_token = auth_token;
    }

    public static Drawable getUser_photo() {
        return user_photo;
    }

    public static void setUser_photo(Drawable user_photo) {
        User.user_photo = user_photo;
    }

    public static boolean isFacebookLogin() {
        return facebookLogin;
    }

    public static void setFacebookLogin(boolean facebookLogin) {
        User.facebookLogin = facebookLogin;
    }
}
