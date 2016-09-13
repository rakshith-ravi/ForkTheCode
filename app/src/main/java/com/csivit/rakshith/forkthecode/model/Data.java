package com.csivit.rakshith.forkthecode.model;

import android.content.Context;
import android.content.SharedPreferences;

public class Data {

    public static String AccessToken = "";

    private static SharedPreferences sharedPreferences;
    private static boolean isLoggedIn;
    private static String username;

    public static void initialize(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        isLoggedIn = sharedPreferences.getBoolean(Constants.LOGGED_IN_KEY, false);
        username = sharedPreferences.getString(Constants.USERNAME_KEY, "");
    }

    public static void save() {
        sharedPreferences.edit()
                .putBoolean(Constants.LOGGED_IN_KEY, isLoggedIn)
                .putString(Constants.USERNAME_KEY, username)
                .apply();
    }

    public static boolean isLoggedIn() {
        return isLoggedIn;
    }

    public static void setLoggedIn(boolean value) {
        isLoggedIn = value;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String value) {
        username = value;
    }
}
