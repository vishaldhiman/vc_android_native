package com.vchoose.Vchoose.util;

/**
 * Created by Sam on 7/7/2015.
 */
public class User {
    static public String auth_token;
    static public String user_name;

    public static String getUser_name() {
        return user_name;
    }

    public static void setUser_name(String user_name) {
        User.user_name = user_name;
    }

    public static String getAuth_token() {
        return auth_token;
    }

    public static void setAuth_token(String auth_token) {
        User.auth_token = auth_token;
    }
}
