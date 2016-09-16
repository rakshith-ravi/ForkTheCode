package com.csivit.rakshith.forkthecode.model;

import android.content.Context;
import android.content.SharedPreferences;

public class Data {

    public static String AccessToken;
    public static String AuthToken;

    private static SharedPreferences sharedPreferences;
    private static boolean isLoggedIn;
    private static String username;
    private static String questionID;
    private static String question;
    private static String clue;

    public static void initialize(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        isLoggedIn = sharedPreferences.getBoolean(Constants.LOGGED_IN_KEY, false);
        AccessToken = sharedPreferences.getString(Constants.ACCESS_TOKEN_KEY, "null");
        AuthToken = sharedPreferences.getString(Constants.AUTH_TOKEN_KEY, "null");
        username = sharedPreferences.getString(Constants.USERNAME_KEY, "");
        questionID = sharedPreferences.getString(Constants.QUESTION_ID_KEY, "null");
        question = sharedPreferences.getString(Constants.QUESTION_KEY, "null");
        clue = sharedPreferences.getString(Constants.CLUE_KEY, null);
    }

    public static void save() {
        sharedPreferences.edit()
                .putBoolean(Constants.LOGGED_IN_KEY, isLoggedIn)
                .putString(Constants.USERNAME_KEY, username)
                .putString(Constants.QUESTION_ID_KEY, questionID)
                .putString(Constants.ACCESS_TOKEN_KEY, AccessToken)
                .putString(Constants.AUTH_TOKEN_KEY, AuthToken)
                .putString(Constants.QUESTION_KEY, question)
                .putString(Constants.CLUE_KEY, clue)
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
}
