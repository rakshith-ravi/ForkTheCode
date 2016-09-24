package com.csivit.rakshith.forkthecode.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

public class Data {

    public static String AuthToken;

    private static SharedPreferences sharedPreferences;
    private static boolean loggedIn;
    private static boolean joinedTeam;
    private static boolean mapActivity;
    private static String username;
    private static String questionID;
    private static String question;
    private static String clue;
    private static Location location;

    public static void initialize(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        loggedIn = sharedPreferences.getBoolean(Constants.LOGGED_IN_KEY, false);
        joinedTeam = sharedPreferences.getBoolean(Constants.JOINED_TEAM_KEY, false);
        mapActivity = sharedPreferences.getBoolean(Constants.MAP_ACTIVITY_KEY, false);
        AuthToken = sharedPreferences.getString(Constants.AUTH_TOKEN_KEY, "");
        username = sharedPreferences.getString(Constants.USERNAME_KEY, "");
        questionID = sharedPreferences.getString(Constants.QUESTION_ID_KEY, "");
        question = sharedPreferences.getString(Constants.QUESTION_KEY, "");
        clue = sharedPreferences.getString(Constants.CLUE_KEY, "");
        location = new Location("SharedPreferences");
        location.setLatitude(Double.longBitsToDouble(sharedPreferences.getLong(Constants.LATITUDE_KEY, -1L)));
        location.setLongitude(Double.longBitsToDouble(sharedPreferences.getLong(Constants.LONGITUDE_KEY, -1L)));
    }

    public static void save() {
        sharedPreferences.edit()
                .putBoolean(Constants.LOGGED_IN_KEY, loggedIn)
                .putBoolean(Constants.JOINED_TEAM_KEY, joinedTeam)
                .putBoolean(Constants.MAP_ACTIVITY_KEY, mapActivity)
                .putString(Constants.USERNAME_KEY, username)
                .putString(Constants.QUESTION_ID_KEY, questionID)
                .putString(Constants.AUTH_TOKEN_KEY, AuthToken)
                .putString(Constants.QUESTION_KEY, question)
                .putString(Constants.CLUE_KEY, clue)
                .putLong(Constants.LATITUDE_KEY, Double.doubleToLongBits(location.getLatitude()))
                .putLong(Constants.LONGITUDE_KEY, Double.doubleToLongBits(location.getLongitude()))
                .apply();
    }

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public static void setLoggedIn(boolean value) {
        loggedIn = value;
    }

    public static boolean isJoinedTeam() {
        return joinedTeam;
    }

    public static void setJoinedTeam(boolean joinedTeam) {
        Data.joinedTeam = joinedTeam;
    }

    public static boolean isMapActivity() {
        return mapActivity;
    }

    public static void setMapActivity(boolean mapActivity) {
        Data.mapActivity = mapActivity;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String value) {
        username = value;
    }

    public static String getQuestion() {
        return question;
    }

    public static String getQuestionID() {
        return questionID;
    }

    public static void setQuestion(String questionID, String question) {
        Data.questionID = questionID;
        Data.question = question;
    }

    public static String getClue() {
        return clue;
    }

    public static void setClue(String clue) {
        Data.clue = clue;
    }

    public static Location getLocation() {
        return location;
    }
    public static void setLocation(Location location) {
        Data.location = location;
    }
}
