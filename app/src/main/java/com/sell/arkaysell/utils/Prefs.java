package com.sell.arkaysell.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
 
    //mode
    int PRIVATE_MODE = 0;
 
    // file name
    public static final String PREFS_NAME = "play_quiz_preferences";
    public static final String USER_ID = "user_id";
    public static final String IS_SIGN_IN_GOOGLE = "is_sign_in_google";

    public final static String TOKEN = "token";
    public final static String IDADSREMOVED = "isremoved";

    //Game Preferences
    public static final String ISFROMGAMEOVER = "isFromGameOver";
    public static final String GAMESOUND = "gamesound";
    public static final String LAST_LEVEL_SCORE = "lastlevelscore";




    public Prefs(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFS_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void setLastLevelScore(long lastlevelscore) {
        editor.putLong(LAST_LEVEL_SCORE, lastlevelscore);
        editor.commit();
    }

    public long getLastLevelScore() {
        return pref.getLong(LAST_LEVEL_SCORE, 0l);
    }

    public void setGamesound(boolean gameSound) {
        editor.putBoolean(GAMESOUND, gameSound);
        editor.commit();
    }

    public boolean isGamesound() {
        return pref.getBoolean(GAMESOUND, true);
    }

    public void setAdsRemove(boolean adRemoveSatus) {
        editor.putBoolean(IDADSREMOVED, adRemoveSatus);
        editor.commit();
    }

    public boolean isAdsRemove() {
        return pref.getBoolean(IDADSREMOVED, true);
    }


    public void setFromGameOver(boolean isFromGameOver) {
        editor.putBoolean(ISFROMGAMEOVER, isFromGameOver);
        editor.commit();
    }

    public boolean isFromGameOver() {
        return pref.getBoolean(ISFROMGAMEOVER, false);
    }





    //User Preferences
    public void setToken(String token) {
        editor.putString(TOKEN, token);
        editor.commit();
    }

    public String getToken() {
        return pref.getString(TOKEN, "");
    }


    public void setUID(String uid) {
        editor.putString(USER_ID, uid);
        editor.commit();
    }

    public String getUID() {
        return pref.getString(USER_ID, "");
    }

    public void setSignINGoogle(boolean isSigninGoogle) {
        editor.putBoolean(IS_SIGN_IN_GOOGLE, isSigninGoogle);
        editor.commit();
    }

    public boolean getSignINGoogle() {
        return pref.getBoolean(IS_SIGN_IN_GOOGLE, false);
    }


}