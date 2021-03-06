package com.libertacao.libertacao.persistence;

import android.content.Context;
import android.content.SharedPreferences;

import com.libertacao.libertacao.MyApp;
import com.libertacao.libertacao.data.Event;

public class UserPreferences {
    // Common methods

    private static SharedPreferences getSharedPreferences() {
        return MyApp.getAppContext().getSharedPreferences("com.libertacao.libertacao.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE);
    }

    public static void clearSharedPreferences() {
        UserPreferences.getSharedPreferences().edit().clear().commit();
    }

    // Common methods - Double

    private static double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    private static SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    // Latitude
    public static double getLatitude() {
        return getDouble(UserPreferences.getSharedPreferences(), "LATITUDE", Event.INVALID_LOCATION);
    }

    public static void setLatitude(double newValue) {
        SharedPreferences sharedPref = UserPreferences.getSharedPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        putDouble(editor, "LATITUDE", newValue);
        editor.apply();
    }

    // Longitude
    public static double getLongitude() {
        return getDouble(UserPreferences.getSharedPreferences(), "LONGITUDE", Event.INVALID_LOCATION);
    }

    public static void setLongitude(double newValue) {
        SharedPreferences sharedPref = UserPreferences.getSharedPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        putDouble(editor, "LONGITUDE", newValue);
        editor.apply();
    }

    // Already associated user with installation
    public static boolean isUserAssociatedWithInstallation() {
        return getSharedPreferences().getBoolean("IS_USER_ASSOCIATED_WITH_INSTALLATION", false);
    }

    public static void setUserAssociatedWithInstallation() {
        SharedPreferences sharedPref = UserPreferences.getSharedPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("IS_USER_ASSOCIATED_WITH_INSTALLATION", true);
        editor.apply();
    }

    // Already associated user with installation
    public static boolean isNotificationEnabled() {
        return getSharedPreferences().getBoolean("IS_NOTIFICATION_ENABLED", true);
    }

    public static void setIsNotificationEnabled(boolean newValue) {
        SharedPreferences sharedPref = UserPreferences.getSharedPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("IS_NOTIFICATION_ENABLED", newValue);
        editor.apply();
    }

    // Selected tab
    public static int getSelectedTab() {
        return getSharedPreferences().getInt("SELECTED_TAB", 0);
    }

    public static void setSelectedTab(int newValue) {
        SharedPreferences sharedPref = UserPreferences.getSharedPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("SELECTED_TAB", newValue);
        editor.apply();
    }

    // Selected order by
    public static int getSelectedOrderBy() {
        return getSharedPreferences().getInt("SELECTED_ORDER_BY", 0);
    }

    public static void setSelectedOrderBy(int newValue) {
        SharedPreferences sharedPref = UserPreferences.getSharedPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("SELECTED_ORDER_BY", newValue);
        editor.apply();
    }
}
