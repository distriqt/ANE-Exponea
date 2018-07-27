package com.infinario.android.infinariosdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * This file has been created by igi on 2/18/15.
 */
public class Preferences {

    private Context context;
    private static Preferences instance = null;
    private static Object lockInstance = new Object();
    private Object lockAccess;

    private Preferences(Context context) {
        this.context = context;
        lockAccess = new Object();
    }

    public static Preferences get(Context context) {
        if (instance == null) {
            synchronized (lockInstance) {
                if (instance == null) {
                    instance = new Preferences(context);
                }
            }
        }

        return instance;
    }

    /**
     * @param context application's context.
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        synchronized (lockAccess) {
            return context.getSharedPreferences(Contract.PROPERTY, Context.MODE_PRIVATE);
        }
    }

    /**
     * Gets sender ID from preferences.
     *
     * @return sender ID or project number obtained from Google Developers Console
     */
    public String getSenderId() {
        return getPreferences(context).getString(Contract.PROPERTY_SENDER_ID, null);
    }

    /**
     * Stores sender ID in preferences.
     *
     * @param senderId sender ID or project number obtained from Google Developers Console
     */
    public void setSenderId(String senderId) {
        getPreferences(context).edit().putString(Contract.PROPERTY_SENDER_ID, senderId).commit();
    }

    /**
     * Gets referrer from preferences.
     *
     * @return referrer
     */
    public String getReferrer() {
        return getPreferences(context).getString(Contract.PROPERTY_REFERRER, null);
    }

    /**
     * Stores referrer in preferences.
     *
     * @param referrer referrer from INSTALL_REFERRER intent
     */
    public void setReferrer(String referrer) {
        getPreferences(context).edit().putString(Contract.PROPERTY_REFERRER, referrer).commit();
    }

    /**
     * Gets icon from preferences.
     *
     * @return icon resource
     */
    public int getIcon() {
        return getPreferences(context).getInt(Contract.PROPERTY_ICON, -1);
    }

    /**
     * Stores notification icon preferences.
     *
     * @param iconDrawable icon for the notifications, e.g. R.drawable.icon
     */
    public void setIcon(int iconDrawable) {
        getPreferences(context).edit().putInt(Contract.PROPERTY_ICON, iconDrawable).commit();
    }

    /**
     * Gets token from preferences.
     *
     * @return token
     */
    public String getToken() {
        return getPreferences(context).getString(Contract.PROPERTY_TOKEN, null);
    }

    /**
     * Stores token in preferences.
     *
     * @param token token
     */
    public void setToken(String token) {
        getPreferences(context).edit().putString(Contract.PROPERTY_TOKEN, token).commit();
    }

    /**
     * Gets target (Infinario API location) from preferences.
     *
     * @return Infinario API location
     */
    public String getTarget() {
        return getPreferences(context).getString(Contract.PROPERTY_TARGET, Contract.DEFAULT_TARGET);
    }

    /**
     * Stores target (Infinario API location) in preferences.
     *
     * @param target Infinario API location
     */
    public void setTarget(String target) {
        getPreferences(context).edit().putString(Contract.PROPERTY_TARGET, target).commit();
    }

    /**
     * Checks the state of automatic flushing.
     *
     * @return true if flushing is enabled, false otherwise
     */
    public boolean getAutomaticFlushing() {
        return getPreferences(context).getBoolean(Contract.PROPERTY_AUTO_FLUSH, Contract.DEFAULT_AUTO_FLUSH);
    }

    /**
     * Stores status of automatic flushing in preferences.
     *
     * @param value enabled / disabled
     */
    public void setAutomaticFlushing(boolean value) {
        getPreferences(context).edit().putBoolean(Contract.PROPERTY_AUTO_FLUSH, value).commit();
    }

    /**
     * Gets session start.
     *
     * @return timestamp of session start in milliseconds
     */
    public long getSessionStart() {
        return getPreferences(context).getLong(Contract.PROPERTY_SESSION_START, -1);
    }

    /**
     * Stores session start.
     *
     * @param value timestamp in milliseconds
     */
    public void setSessionStart(long value) {
        getPreferences(context).edit().putLong(Contract.PROPERTY_SESSION_START, value).commit();
    }

    /**
     * Gets session end.
     *
     * @return timestamp of session start in milliseconds
     */
    public long getSessionEnd() {
        return getPreferences(context).getLong(Contract.PROPERTY_SESSION_END, -1);
    }

    public Map<String, Object> getSessionEndProperties() {
        return jsonToMap(getPreferences(context).getString(Contract.PROPERTY_SESSION_END_PROPERTIES, ""));
    }

    /**
     * Stores session end.
     *
     * @param value timestamp in milliseconds
     * @param properties Map of custom properties
     */
    public void setSessionEnd(long value, Map<String, Object> properties) {
        getPreferences(context).edit().putLong(Contract.PROPERTY_SESSION_END, value).commit();
        if (properties != null) {
            getPreferences(context).edit().putString(Contract.PROPERTY_SESSION_END_PROPERTIES, new JSONObject(properties).toString()).commit();
        } else {
            getPreferences(context).edit().putString(Contract.PROPERTY_SESSION_END_PROPERTIES, "").commit();
        }
    }

    /**
     * Checks the state of push notifications.
     *
     * @return true if google push notifications are enabled, false otherwise
     */
    public boolean getGooglePushNotifications() {
        return getPreferences(context).getBoolean(Contract.PROPERTY_GOOGLE_PUSH_NOTIFICATIONS, Contract.DEFAULT_PUSH_NOTIFICATIONS);
    }

    /**
     * Stores status of google push notifications in preferences.
     *
     * @param value enabled / disabled
     */
    public void setGooglePushNotifications(boolean value) {
        getPreferences(context).edit().putBoolean(Contract.PROPERTY_GOOGLE_PUSH_NOTIFICATIONS, value).commit();
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    public String getRegistrationId() {
        final SharedPreferences prefs = getPreferences(context);
        String registrationId = prefs.getString(Contract.PROPERTY_REG_ID, "");

        if (registrationId.isEmpty()) {
            Log.i(Contract.TAG, "Registration not found.");
            return "";
        }

        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(Contract.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersionCode();

        if (registeredVersion != currentVersion) {
            Log.i(Contract.TAG, "App version changed.");
            return "";
        }

        return registrationId;
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param registrationId registration ID
     */
    public void setRegistrationId(String registrationId) {
        int appVersion = getAppVersionCode();
        Log.i(Contract.TAG, "Saving regId on app version " + appVersion);

        getPreferences(context)
                .edit()
                .putString(Contract.PROPERTY_REG_ID, registrationId)
                .putInt(Contract.PROPERTY_APP_VERSION, appVersion)
                .commit();
    }

    /**
     * Gets cookie ID from preferences.
     *
     * @return cookie ID
     */
    public String getCookieId() {
        return getPreferences(context).getString(Contract.COOKIE, "");
    }

    /**
     * Sets cookie ID in preferences.
     */
    public void setCookieId(String value) {
        getPreferences(context).edit().putString(Contract.COOKIE, value).commit();
    }

    /**
     * Gets registred ID from preferences.
     *
     * @return cookie ID
     */
    public String getRegistredId() {
        return getPreferences(context).getString(Contract.REGISTERED, "");
    }

    /**
     * Sets registred ID in preferences.
     */
    public void setRegistredId(String value) {
        getPreferences(context).edit().putString(Contract.REGISTERED, value).commit();
    }

    /**
     * Gets google advertising ID from preferences.
     *
     * @return google advertising ID
     */
    public String getGoogleAdvertisingId() {
        return getPreferences(context).getString(Contract.PROPERTY_GOOGLE_ADV_ID, "");
    }

    /**
     * Sets google advertising ID in preferences.
     */
    public void setGoogleAdvertisingId(String value){
        getPreferences(context).edit().putString(Contract.PROPERTY_GOOGLE_ADV_ID, value).commit();
    }

    /**
     * Gets device type from preferences.
     *
     * @return mobile / tablet
     */
    public String getDeviceType(){
        return getPreferences(context).getString(Contract.PROPERTY_DEVICE_TYPE,"");
    }

    /**
     * Sets device type (mobile / tablet) in preferences.
     */
    public void setDeviceType(String value){
        getPreferences(context).edit().putString(Contract.PROPERTY_DEVICE_TYPE, value).commit();
    }

    /**
     * Gets app's version.
     *
     * @return Application's version code from the {@code PackageManager}.
     */
    private int getAppVersionCode() {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Gets app's version name.
     *
     * @return Application's version name from the {@code PackageManager}.
     */
    public String getAppVersionName() {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    /**
     * Clears cached information from device's memory (registration id, app version,
     * cookie ID)
     */
    @SuppressWarnings("unused")
    public void clearStoredData() {
        getPreferences(context).edit()
                .remove(Contract.PROPERTY_APP_VERSION)
                .remove(Contract.PROPERTY_REG_ID)
                .remove(Contract.PROPERTY_ICON)
                .remove(Contract.PROPERTY_GOOGLE_PUSH_NOTIFICATIONS)
                .remove(Contract.PROPERTY_SENDER_ID)
                .remove(Contract.PROPERTY_AUTO_FLUSH)
                .remove(Contract.PROPERTY_SESSION_START)
                .remove(Contract.PROPERTY_SESSION_END)
                .remove(Contract.COOKIE)
                .remove(Contract.CAMPAIGN_COOKIE)
                .remove(Contract.PROPERTY_GOOGLE_ADV_ID)
                .remove(Contract.PROPERTY_DEVICE_TYPE)
                .commit();
    }

    private Map<String, Object> jsonToMap(String json){
        try {
            if (json.isEmpty()) {
                return null;
            }

            JSONObject jsonObj = new JSONObject(json);
            Iterator it = jsonObj.keys();
            Map<String, Object> map = new HashMap<>();

            while(it.hasNext())
            {
                String key = it.next().toString();
                map.put(key, jsonObj.get(key));
            }

            return map;
        } catch (JSONException e){
            Log.e(Contract.TAG, e.getMessage().toString());
        } catch (NullPointerException e){
            Log.e(Contract.TAG, e.getMessage().toString());
        }

        return null;
    }
}
