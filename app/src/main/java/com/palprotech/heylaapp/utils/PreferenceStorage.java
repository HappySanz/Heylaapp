package com.palprotech.heylaapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Admin on 11-10-2017.
 */

public class PreferenceStorage {

    /*To check welcome screen to launch*/
    public static void setFirstTimeLaunch(Context context, boolean isFirstTime) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(HeylaAppConstants.IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.apply();
    }

    public static boolean isFirstTimeLaunch(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(HeylaAppConstants.IS_FIRST_TIME_LAUNCH, true);
    }

    /*To save FCM key locally*/
    public static void saveGCM(Context context, String gcmId) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(HeylaAppConstants.KEY_FCM_ID, gcmId);
        editor.apply();
    }

    public static String getGCM(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPreferences.getString(HeylaAppConstants.KEY_FCM_ID, "");
    }

    /*To save mobile IMEI number */
    public static void saveIMEI(Context context, String imei) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(HeylaAppConstants.KEY_IMEI, imei);
        editor.apply();
    }

    public static String getIMEI(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPreferences.getString(HeylaAppConstants.KEY_IMEI, "");
    }

    /*To check remember me enabled or not*/
    public static void setRememberMe(Context context, boolean isRemembered) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(HeylaAppConstants.IS_REMEMBER_ENABLED, isRemembered);
        editor.apply();
    }

    public static boolean isRemembered(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(HeylaAppConstants.IS_REMEMBER_ENABLED, false);
    }

    /*To store userId*/
    public static void saveUserId(Context context, String userName) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(HeylaAppConstants.KEY_USER_ID, userName);
        editor.apply();
    }

    public static String getUserId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPreferences.getString(HeylaAppConstants.KEY_USER_ID, "");
    }

    /*To store username*/
    public static void saveUsername(Context context, String userName) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(HeylaAppConstants.KEY_USERNAME, userName);
        editor.apply();
    }

    public static String getUsername(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPreferences.getString(HeylaAppConstants.KEY_USERNAME, "");
    }

    /*To store username*/
    public static void savePassword(Context context, String password) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(HeylaAppConstants.KEY_PASSWORD, password);
        editor.apply();
    }

    public static String getPassword(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPreferences.getString(HeylaAppConstants.KEY_PASSWORD, "");
    }

    public static void saveUserOccupation(Context context, String data) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(HeylaAppConstants.KEY_USER_OCCUPATION, data);
        editor.commit();

    }

    public static String getUserOccupation(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String url = sharedPreferences.getString(HeylaAppConstants.KEY_USER_OCCUPATION, "");
        return url;

    }

    public static void saveUserGender(Context context, String data) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(HeylaAppConstants.KEY_USER_GENDER, data);
        editor.commit();

    }

    public static String getUserGender(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String url = sharedPreferences.getString(HeylaAppConstants.KEY_USER_GENDER, "");
        return url;

    }

    public static void saveUserBirthday(Context context, String data) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(HeylaAppConstants.KEY_USER_BIRTHDAY, data);
        editor.commit();

    }

    public static String getUserBirthday(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String url = sharedPreferences.getString(HeylaAppConstants.KEY_USER_BIRTHDAY, "");
        return url;

    }

    public static void saveLoginMode(Context context, int type) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(HeylaAppConstants.KEY_LOGIN_MODE, type);
        editor.commit();
    }

    public static int getLoginMode(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Integer mode = sharedPreferences.getInt(HeylaAppConstants.KEY_LOGIN_MODE, 0);
        return mode;
    }

    public static void saveMobileNo(Context context, String type) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(HeylaAppConstants.KEY_MOBILE_NUMBER, type);
        editor.apply();
    }

    public static String getMobileNo(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String mobileNo = sharedPreferences.getString(HeylaAppConstants.KEY_MOBILE_NUMBER, "");
        return mobileNo;
    }

}
