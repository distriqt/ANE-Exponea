package com.infinario.android.infinariosdk;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This file has been created by Igor Liska on 1/8/15.
 */
public class Infinario {
    private static Infinario instance = null;

    private GoogleCloudMessaging gcm = null;
    private String token;
    private String registrationId;
    private String userAgent;
    private Map<String, String> customer;
    private CommandManager commandManager;
    private final Context context;
    private int commandCounter = Contract.FLUSH_COUNT;
    private Preferences preferences;
    private IabHelper iabHelper = null;
    private Map<String, Object> sessionProperties;
    private JSONObject amazonProduct;
    private long sessionStart = -1;
    private long sessionEnd = -1;
    private long sessionTimeOut;
    private Object lockPublic;
    private Object lockFlushTimer;
    private static Object lockInstance = new Object();
    private Handler sessionHandler;
    private Runnable sessionEndRunnable;
    private Runnable sessionFlushRunnable;
    private int sessionCounter;

    private Infinario(Context context, String token, String target, Map<String, String> customer) {
        this.token = token;
        this.context = context.getApplicationContext();

        preferences = Preferences.get(context);
        preferences.setToken(token);

        sessionProperties = new HashMap<>();
        sessionTimeOut = Contract.SESSION_TIMEOUT;
        sessionCounter = 0;

        if (null != target) {
            preferences.setTarget(target.replaceFirst("/*$", ""));
        }

        if (preferences.getGoogleAdvertisingId().isEmpty()){
            initializeGoogleAdvertisingId();
        }

        if (preferences.getDeviceType().isEmpty()){
            initializeDeviceType();
        }

        if (preferences.getCookieId().isEmpty()){
            preferences.setCookieId(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        }

        userAgent = UserAgent.create(preferences);

        commandManager = new CommandManager(context, target, userAgent);

        iabHelper = new IabHelper(context);
        iabHelper.startSetup(null);

        if (customer == null) {
            customer = new HashMap<>();
        }

        customer.put(Contract.COOKIE, preferences.getCookieId());

        lockPublic = new Object();
        lockFlushTimer = new Object();

        sessionHandler = new Handler();
        sessionEndRunnable = new Runnable() {
            public void run() {
                synchronized (lockPublic) {
                    if (preferences.getSessionEnd() != -1) {
                        sessionEnd(preferences.getSessionEnd(), (preferences.getSessionEnd() - preferences.getSessionStart()) / 1000L, preferences.getSessionEndProperties());
                    }
                }
            }
        };
        sessionFlushRunnable = new Runnable() {
            public void run() {
                flush();
            }
        };

        this.customer = customer;

        if (preferences.getAutomaticFlushing()) {
            startFlushTimer();
        }
    }

    /**
     * Obtains instance of Infinario instance to work with and identifies the customer.
     *
     * @param context  application's context
     * @param token    project token obtained from Infinario admin
     * @param target   Infinario API location
     * @param customer key-value ids (cookie ID or registered ID)
     * @return Infinario instance
     */
    @SuppressWarnings("unused")
    public static Infinario getInstance(Context context, String token, String target, Map<String, String> customer) {
        if (instance == null) {
            synchronized (lockInstance){
                if (instance == null){
                    instance = new Infinario(context, token, target, customer);
                }
            }
        }

        return instance;
    }

    /**
     * Obtains instance of Infinario instance to work with and identifies the customer.
     *
     * @param context  application's context
     * @param token    project token obtained from Infinario admin
     * @param target   Infinario API location
     * @param customer customer's registered ID
     * @return Infinario instance
     */
    @SuppressWarnings("unused")
    public static Infinario getInstance(Context context, String token, String target, String customer) {
        return getInstance(context, token, target, translateId(customer));
    }

    /**
     * Obtains instance of Infinario instance to work with.
     *
     * @param context  application's context
     * @param token    project token obtained from Infinario admin
     * @param target   Infinario API location
     * @return Infinario instance
     */
    @SuppressWarnings("unused")
    public static Infinario getInstance(Context context, String token, String target) {
        return getInstance(context, token, target, (Map<String, String>) null);
    }

    /**
     * Obtains instance of Infinario instance to work with and identifies the customer.
     *
     * @param context  application's context
     * @param token    project token obtained from Infinario admin
     * @param customer key-value ids (cookie ID or registered ID)
     * @return Infinario instance
     */
    @SuppressWarnings("unused")
    public static Infinario getInstance(Context context, String token, Map<String, String> customer) {
        return getInstance(context, token, null, customer);
    }

    /**
     * Obtains instance of Infinario instance to work with.
     *
     * @param context  application's context
     * @param token    project token obtained from Infinario admin
     * @return Infinario instance
     */
    @SuppressWarnings("unused")
    public static Infinario getInstance(Context context, String token) {
        return getInstance(context, token, null, (Map<String, String>) null);
    }

    /**
     * Identifies a customer with their registered ID.
     *
     * @param customer key-value ids (registered ID)
     * @param properties key-value customer's properties
     */
    @SuppressWarnings("unused")
    public void identify(Map<String, String> customer, Map<String, Object> properties) {
        synchronized (lockPublic) {
            if (customer.containsKey(Contract.REGISTERED)) {

                this.customer.put(Contract.REGISTERED, customer.get(Contract.REGISTERED));

                if (!customer.get(Contract.REGISTERED).equals(preferences.getRegistredId())){
                    preferences.setRegistredId(customer.get(Contract.REGISTERED));
                    Map<String, Object> identificationProperties = Device.deviceProperties(preferences);
                    identificationProperties.put(Contract.REGISTERED, customer.get(Contract.REGISTERED));
                    _track("identification", identificationProperties);
                    _update(properties);
                }
            }
        }
    }

    /**
     * Identifies a customer with their registered ID.
     *
     * @param customer customer's registered ID
     * @param properties key-value customer's properties
     */
    @SuppressWarnings("unused")
    public void identify(String customer, Map<String, Object> properties) {
        identify(translateId(customer), properties);
    }

    /**
     * Identifies a customer with their registered ID or cookie ID. If no cookie ID is
     * supplied, it is generated automatically.
     *
     * @param customer key-value ids (cookie ID or registered ID)
     */
    @SuppressWarnings("unused")
    public void identify(Map<String, String> customer) {
        identify(customer, new HashMap<String, Object>());
    }

    /**
     * Identifies a customer with their registered ID.
     *
     * @param customer customer's registered ID
     */
    @SuppressWarnings("unused")
    public void identify(String customer) {
        identify(customer, new HashMap<String, Object>());
    }

    /**
     * Return customers cookie
     *
     * @return string containing customer's cookie
     */
    public String getCookie() {
        return preferences.getCookieId();
    }

    /**
     * Updates customer's properties. Cannot be called prior to the identification.
     *
     * @param properties key-value customer's properties.
     * @return success of the operation
     */
    public boolean update(Map<String, Object> properties) {
        synchronized (lockPublic) {
            return _update(properties);
        }
    }

    private boolean _update(Map<String, Object> properties) {
        if (commandManager.schedule(new Customer(customer, token, properties))) {
            if (preferences.getAutomaticFlushing()) {
                startFlushTimer();
            }

            return true;
        }

        return false;
    }

    private void sessionStart(long timeStamp, Map<String, Object> properties){
        preferences.setSessionStart(timeStamp);
        _track("session_start", mergeProperties(properties, -1), timeStamp);
    }

    private void sessionEnd(long timeStamp, long duration, Map<String, Object> properties){
        _track("session_end", mergeProperties(properties, duration), timeStamp);
        preferences.setSessionStart(-1);
        preferences.setSessionEnd(-1, null);
    }

    public void trackSessionStartImpl(){
        trackSessionStartImpl(null);
    }

    public void trackSessionStartImpl(Map<String, Object> properties){
        synchronized (lockPublic){
            long now = (new Date()).getTime();
            long sessionEnd = preferences.getSessionEnd();
            long sessionStart = preferences.getSessionStart();

            if (sessionHandler != null){
                sessionHandler.removeCallbacks(sessionEndRunnable);
            }

            if (sessionEnd != -1){
                if (now - sessionEnd > sessionTimeOut){
                    //Create session end
                    sessionEnd(sessionEnd, (sessionEnd - sessionStart) / 1000L, preferences.getSessionEndProperties());
                    //Create session start
                    sessionStart(now, properties);
                } else {
                    //Continue in current session
                }
            } else  if (sessionStart == -1){
                //Create session start
                sessionStart(now, properties);
            } else if (now - sessionStart > sessionTimeOut) {
                //Create session start
                sessionStart(now, properties);
            } else {
                //Continue in current session
            }
        }
    }

    public void trackSessionEndImpl(){
        trackSessionEndImpl(null);
    }

    public void trackSessionEndImpl(Map<String, Object> properties){
        synchronized (lockPublic){
            //Save session end with current timestamp and start count TIMEOUT
            preferences.setSessionEnd((new Date()).getTime(), properties);
            sessionHandler.postDelayed(sessionEndRunnable, sessionTimeOut);
        }
    }

    public void setSessionTimeOut(long value) {
        synchronized (lockPublic){
            sessionTimeOut = value;
        }
    }

    public void trackSessionStart(){
        trackSessionStart(null);
    }

    public void trackSessionStart(Map<String, Object> properties){
        int _sessionCounter = 0;
        synchronized (lockPublic) {
            sessionCounter += 1;
            _sessionCounter = sessionCounter;
        }

        if (_sessionCounter == 1){
            trackSessionStartImpl(properties);
        }
    }

    public void trackSessionEnd(){
        trackSessionEnd(null);
    }

    public void trackSessionEnd(Map<String, Object> properties){
        int _sessionCounter = 0;
        synchronized (lockPublic){
            if (sessionCounter > 0){
                sessionCounter -= 1;
            }
            _sessionCounter = sessionCounter;
        }

        if (_sessionCounter == 0) {
            trackSessionEndImpl(properties);
        }
    }

    private Map<String, Object> mergeProperties(Map<String, Object> properties, long duration) {
        Map<String, Object> deviceProperties = Device.deviceProperties(preferences);
        String appVersionName = preferences.getAppVersionName();
        if (appVersionName != null){
            deviceProperties.put("app_version", appVersionName);
        }
        if (duration != -1){
            deviceProperties.put("duration", duration);
        }
        if (properties != null) {
            deviceProperties.putAll(properties);
        }

        return deviceProperties;
    }

    /**
     * Tracks an event for a customer. Cannot be called prior to the identification.
     *
     * @param type       event's type
     * @param properties event's properties
     * @param timestamp  event's timestamp in milliseconds since 1970-01-01 00:00:00 GMT
     * @return success of the operation
     */
    public boolean track(String type, Map<String, Object> properties, Long timestamp) {
        synchronized (lockPublic) {
            return _track(type, properties, timestamp);
        }
    }

    private boolean _track(String type, Map<String, Object> properties, Long timestamp) {
        if (commandManager.schedule(new Event(customer, token, type, properties, timestamp))) {
            if (preferences.getAutomaticFlushing()) {
                startFlushTimer();
            }

            return true;
        }

        return false;
    }

    private boolean _track(String type, Map<String, Object> properties) {
        return _track(type, properties, null);
    }

    /**
     * Tracks an event for a customer. Cannot be called prior to the identification.
     *
     * @param type event's type
     * @return success of the operation
     */
    public boolean track(String type) {
        return track(type, null, null);
    }

    /**
     * Tracks an event for a customer. Cannot be called prior to the identification.
     *
     * @param type       event's type
     * @param properties event's properties
     * @return success of the operation
     */
    public boolean track(String type, Map<String, Object> properties) {
        return track(type, properties, null);
    }

    /**
     * Tracks an event for a customer. Cannot be called prior to the identification.
     *
     * @param type event's type
     * @param timestamp event's timestamp in milliseconds since 1970-01-01 00:00:00 GMT
     * @return success of the operation
     */
    public boolean track(String type, Long timestamp) {
        return track(type, null, timestamp);
    }

    @SuppressWarnings("unused")
    public void setSessionProperties(Map<String, Object> properties) {
        synchronized (lockPublic) {
            if (properties != null) {
                sessionProperties = properties;
            }
        }
    }

    /**
     * @deprecated  As of release 1.1.0, replaced by {@link #trackGooglePurchases(int resultCode, Intent data)}
     */
    @Deprecated
    public void trackPurchases(int resultCode, Intent data){
        this.trackGooglePurchases(resultCode, data);
    }

    public void trackGooglePurchases(int resultCode, Intent data) {
        synchronized (lockPublic) {
            if (!iabHelper.setupDone() || data == null) {
                return;
            }

            int responseCode = IabHelper.getResponseCodeFromIntent(data);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");

            if (resultCode == Activity.RESULT_OK && responseCode == 0) {
                if (purchaseData == null) {
                    Log.d(Contract.TAG, "purchaseData is null");
                    return;
                }

                try {
                    JSONObject o = new JSONObject(purchaseData);
                    final String productId = o.optString("productId");
                    final Long purchaseTime = o.optLong("purchaseTime");
                    final String purchaseToken = o.optString("purchaseToken");

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            synchronized (lockPublic) {
                                Log.d(Contract.TAG, "Purchased item " + productId + " at " + purchaseTime);

                                SkuDetails details;

                                try {
                                    details = iabHelper.querySkuDetails("inapp", productId);

                                    if (details == null && iabHelper.subscriptionsSupported()) {
                                        details = iabHelper.querySkuDetails("subs", productId);
                                    }
                                }
                                catch (RemoteException | JSONException e) {
                                    return null;
                                }

                                if (details != null) {
                                    Map<String, Object> properties = Device.deviceProperties(preferences);

                                    properties.put("gross_amount", details.getPrice());
                                    properties.put("currency", details.getCurrency());
                                    properties.put("product_id", productId);
                                    properties.put("product_title", details.getTitle());
                                    properties.put("product_token", purchaseToken);
                                    properties.put("payment_system", "Google Play Store");

                                    _track("payment", properties, purchaseTime);
                                }

                                return null;
                            }
                        }
                    }.execute(null, null, null);
                }
                catch (JSONException e) {
                    Log.e(Contract.TAG, "Cannot parse purchaseData");
                }
            }
        }
    }

    public void loadAmazonProduct(JSONObject amazonJsonProductDataResponse){
        synchronized (lockPublic) {
            amazonProduct = amazonJsonProductDataResponse;
        }
    }

    public void trackAmazonPurchases(JSONObject amazonJsonPurchaseResponse){
        synchronized (lockPublic) {
            Map<String, Object> properties = Device.deviceProperties(preferences);
            properties.put("payment_system", "Amazon Store");
            try {
                String sku = amazonJsonPurchaseResponse.getJSONObject("receipt").getString("sku");
                if (amazonProduct != null){
                    try {
                        String [] priceCurrency = splitPriceAndCurrency(amazonProduct.getJSONObject("productData").getJSONObject(sku).getString("price"));
                        properties.put("gross_amount", priceCurrency[1]);
                        properties.put("currency", priceCurrency[0]);
                        properties.put("product_title", amazonProduct.getJSONObject("productData").getJSONObject(sku).getString("title"));
                    } catch (JSONException e) {
                        Log.e(Contract.TAG, "Cannot parse productData from Amazon Store");
                    }
                }
                properties.put("product_id", sku);
                properties.put("user_id", amazonJsonPurchaseResponse.getJSONObject("userData").getString("userId"));
                properties.put("receipt", amazonJsonPurchaseResponse.getJSONObject("receipt").getString("receiptId"));
                _track("payment", properties);
            } catch (JSONException e) {
                Log.e(Contract.TAG, "Cannot parse purchaseData from Amazon Store");
            }
        }
    }

    public void trackVirtualPayment(String currency, int amount, String itemName, String itemType){
        synchronized (lockPublic) {
            Map<String, Object> virtualPayment = Device.deviceProperties(preferences);
            virtualPayment.put("currency", currency);
            virtualPayment.put("amount", amount);
            virtualPayment.put("item_name", itemName);
            virtualPayment.put("item_type", itemType);
            _track("virtual_payment", virtualPayment);
        }
    }

    private void trackLogError(String tag, String message){
        trackLog("log_error", tag, message, null);
    }

    private void trackLogError(String tag, String message, HashMap<String, Object> properties){
        trackLog("log_error", tag, message, properties);
    }

    private void trackLogWarning(String tag, String message){
        trackLog("log_warning", tag, message, null);
    }

    private void trackLogWarning(String tag, String message, HashMap<String, Object> properties){
        trackLog("log_warning", tag, message, properties);
    }

    private void trackLogDebug(String tag, String message){
        trackLog("log_debug", tag, message, null);
    }

    private void trackLogDebug(String tag, String message, HashMap<String, Object> properties){
        trackLog("log_debug", tag, message, properties);
    }

    private void trackLog(String type, String tag, String message, HashMap<String, Object> properties){
        HashMap<String, Object> logMessage = new HashMap<>();
        logMessage.put("tag", tag);
        logMessage.put("message", message);
        if (properties != null){
            logMessage.putAll(properties);
        }
        track(type, logMessage);
    }

    /**
     * Return name of segment
     */
    public void getCurrentSegment(final String segmentationId, final String projectSecretToken, final SegmentListener listener){
        synchronized (lockPublic) {
            if (Preferences.get(context).getTarget().startsWith("https")){
                new AsyncTask<Void, Void, JSONObject>(){

                    @Override
                    protected JSONObject doInBackground(Void... params) {
                        HttpHelper http;

                        synchronized (lockPublic){
                            http = new HttpHelper(Preferences.get(context).getTarget(), userAgent)
                                    .addRequestProperty("X-Infinario-Secret", projectSecretToken)
                                    .setTimeout(2000);
                        }

                        try {
                            JSONObject main = new JSONObject();
                            JSONObject ids = new JSONObject();

                            synchronized (lockPublic) {
                                ids.put(Contract.COOKIE, customer.get(Contract.COOKIE));
                                ids.put(Contract.REGISTERED, customer.get(Contract.REGISTERED));
                            }

                            main.put("customer_ids", ids);
                            main.put("analysis_id", segmentationId);

                            return http.post(Contract.SEGMENT_URL, main);
                        } catch (JSONException e) {
                            Log.e(Contract.TAG, e.toString());
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(JSONObject result){
                        if (result != null){
                            if (result.optBoolean("success")){
                                listener.onSegmentReceive(true, new InfinarioSegment().setName(result.optString("segment")), null);
                            } else {
                                listener.onSegmentReceive(false, null, "Unsuccesfull response");
                            }
                        } else {
                            listener.onSegmentReceive(false, null, "Null response");
                        }
                    }
                }.execute();
            } else {
                listener.onSegmentReceive(false, null, "Target must be https");
            }
        }
    }

    /**
     * Flushes the updates / events to Infinario API asynchronously.
     */
    @SuppressWarnings("unused")
    public void flush() {
        commandManager.flush(Contract.MAX_RETRIES);
    }

    /**
     * Enables automatic flushing of the events / updates to Infinario.
     */
    @SuppressWarnings("UnusedDeclaration")
    public void enableAutomaticFlushing() {
        synchronized (lockPublic) {
            preferences.setAutomaticFlushing(true);
            startFlushTimer();
        }
    }

    /**
     * Disables automatic flushing of the events / updates to Infinario.
     */
    @SuppressWarnings("unused")
    public void disableAutomaticFlushing() {
        synchronized (lockPublic) {
            preferences.setAutomaticFlushing(false);
            stopFlushTimer();
        }
    }

    /**
     * @deprecated  As of release 1.1.0, replaced by {@link #enableGooglePushNotifications(String senderId, int iconDrawable)}
     */
    @Deprecated
    public void enablePushNotifications(String senderId, int iconDrawable){
        this.enableGooglePushNotifications(senderId, iconDrawable);
    }

    /**
     * Enables receiving of google push notifications to the app from an Infinario scenario. Push
     * notifications cannot be enabled prior to the identification.
     *
     * @param senderId sender ID or project number obtained from Google Developers Console
     * @param iconDrawable icon for the notifications, e.g. R.drawable.icon
     */
    @SuppressWarnings("unused")
    public void enableGooglePushNotifications(String senderId, int iconDrawable) {
        synchronized (lockPublic) {
            preferences.setGooglePushNotifications(true);

            // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
            if (checkPlayServices(context)) {
                gcm = GoogleCloudMessaging.getInstance(context);
                registrationId = preferences.getRegistrationId();

                preferences.setSenderId(senderId);
                preferences.setIcon(iconDrawable);

                if (registrationId.isEmpty()) {
                    registerInBackground();
                } else {
                    Log.i(Contract.TAG, "Already registered");
                }
            }
            else {
                Log.i(Contract.TAG, "No valid Google Play Services APK found.");
            }
        }
    }

    /**
     * @deprecated  As of release 1.1.0, replaced by {@link #enableGooglePushNotifications(String senderId)}
     */
    @Deprecated
    public void enablePushNotifications(String senderId){
        this.enableGooglePushNotifications(senderId);
    }

    /**
     * Enables receiving of google push notifications to the app from an Infinario scenario.
     *
     * @param senderId sender ID or project number obtained from Google Developers Console
     */
    @SuppressWarnings("unused")
    public void enableGooglePushNotifications(String senderId) {
        enableGooglePushNotifications(senderId, getDrawableId("infinario_notification_icon"));
    }

    /**
     * @deprecated  As of release 1.1.0, replaced by {@link #enableGooglePushNotifications(String senderId, String nameDrawable)}
     */
    @Deprecated
    public void enablePushNotifications(String senderId, String nameDrawable){
        this.enableGooglePushNotifications(senderId, nameDrawable);
    }

    @SuppressWarnings("unused")
    public void enableGooglePushNotifications(String senderId, String nameDrawable) {
        enableGooglePushNotifications(senderId, getDrawableId(nameDrawable));
    }

    /**
     * @deprecated  As of release 1.1.0, replaced by {@link #disableGooglePushNotifications()}
     */
    @Deprecated
    public void disablePushNotifications(){
        this.disableGooglePushNotifications();
    }

    /**
     * Disables receiving of google push notifications to the app from an Infinario scenario.
     */
    @SuppressWarnings("unused")
    public void disableGooglePushNotifications() {
        synchronized (lockPublic) {
            preferences.setGooglePushNotifications(false);
        }
    }

    /**
     * Handles intent (GCM message) from the GcmBroadcastReceiver for all Infinario instances.
     *
     * @param context application's context
     * @param intent received intent from a broadcast receiver.
     */
    public static void handleGooglePushNotification(Context context, Intent intent) {
        Preferences preferences = Preferences.get(context);

        if (preferences.getGooglePushNotifications() && checkPlayServices(context)) {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
            Bundle extras = intent.getExtras();
            String messageType = gcm.getMessageType(intent);
            String senderId = preferences.getSenderId();

            if (!extras.isEmpty() &&
                senderId != null &&
                !senderId.equals("") &&
                GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType) &&
                extras.getString("from").equals(senderId)) {

                Log.d(Contract.TAG, "Received data: " + intent.getExtras().toString());
                sendNotification(context, intent.getExtras(), preferences.getIcon());
            }
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void clearStoredData() {
        synchronized (lockPublic) {
            preferences.clearStoredData();
        }
    }

    /**
     * Translates String registered ID to Map<String, String> compound ID
     * @param customer customer's registered ID
     * @return key-value customer IDs
     */
    private static Map<String, String> translateId(String customer) {
        Map<String, String> customer_ids = new HashMap<>();
        customer_ids.put(Contract.REGISTERED, customer);
       return customer_ids;
    }

    /**
     * Gets Alarm Manager.
     *
     * @param context application's context
     * @return alarm manager instance
     */
    private static AlarmManager getAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK.
     *
     * @param context application's context
     */
    private static boolean checkPlayServices(Context context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);

        if (resultCode != ConnectionResult.SUCCESS) {
            Log.i(Contract.TAG, "This device is not supported.");
            return false;
        }

        return true;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    registrationId = gcm.register(preferences.getSenderId());

                    sendRegistrationIdToBackend();
                    preferences.setRegistrationId(registrationId);
                } catch (IOException ex) {
                    Log.e(Contract.TAG, "Error :" + ex.getMessage());
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }

                return null;
            }
        }.execute(null, null, null);
    }

    /**
     * Sends the registration ID to the server over HTTP, so it can send
     * messages to the app.
     */
    private void sendRegistrationIdToBackend() {
        Log.i(Contract.TAG, "Sending registration ID to backend");
        Map<String, Object> properties = new HashMap<>();
        properties.put(Contract.DB_GOOGLE_REGISTRATION_ID, registrationId);
        _update(properties);
    }

    /**
     * Displays a notification (on locked screen or in notification area).
     *
     * @param context application's context
     * @param data data from intent
     * @param iconDrawable icon for the notifications, e.g. R.drawable.icon
     */
    private static void sendNotification(Context context, Bundle data, int iconDrawable) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());

        if (intent != null) {
            intent.putExtras(data);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            String message = data.getString("message");

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(iconDrawable)
                            .setContentTitle(data.getString("title"))
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                            .setContentText(message)
                            .setAutoCancel(true);

            builder.setContentIntent(contentIntent);
            notificationManager.notify(Contract.NOTIFICATION_ID, builder.build());
        }
    }

    /**
     * @return return drawable id by string
     */
    private int getDrawableId(String nameDrawable){
        try {
            return context.getResources().getIdentifier(nameDrawable, "drawable", context.getPackageName());
        } catch (Exception e) {
            Log.e(Contract.TAG, "Cannot find drawable with name " + nameDrawable);
            if (!nameDrawable.equals("infinario_notification_icon")){
                return getDrawableId("infinario_notification_icon");
            } else {
                return -1;
            }
        }
    }

    /**
     * initializes google advertising ID
     */
    private void initializeGoogleAdvertisingId(){
        new Thread(new Runnable() {
            public void run() {
                try {
                    AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                    preferences.setGoogleAdvertisingId(adInfo.getId());

                    synchronized (lockPublic) {
                        HashMap<String,Object> advId = new HashMap<String, Object>();
                        advId.put(Contract.PROPERTY_GOOGLE_ADV_ID,adInfo.getId());
                        _update(advId);
                    }
                } catch (Exception e) {
                    Log.e(Contract.TAG, "Cannot initialize google advertising ID");
                }
            }
        }).start();
    }

    /**
     * Split string where is Price and Currency together.
     */
    private String[] splitPriceAndCurrency(String price){
        StringBuilder currency = new StringBuilder();
        for (int i=0;i<price.length();i++){
            if (!Character.isDigit(price.charAt(i))){
                currency.append(price.charAt(i));
            } else {
                break;
            }
        }
        return new String[]{currency.toString(),price.substring(currency.length())};
    }

    /**
     * Check if device is mobile or tablet
     */
    private void initializeDeviceType() {
        try{
            boolean device_large = ((context.getResources().getConfiguration().screenLayout &
                    Configuration.SCREENLAYOUT_SIZE_MASK) >=
                    Configuration.SCREENLAYOUT_SIZE_LARGE);

            if (device_large) {
                Log.d(Contract.TAG, "Detect tablet");
                preferences.setDeviceType("tablet");
            } else {
                Log.d(Contract.TAG, "Detect mobile");
                preferences.setDeviceType("mobile");
            }
        } catch (Exception e){
            Log.e(Contract.TAG, "Cannot initialize device type");
        }
    }

    /**
     * Listnener for segmentation
     */
    public interface SegmentListener{
        void onSegmentReceive(boolean wasSuccessful, InfinarioSegment segment, String error);
    }

    private void startFlushTimer(){
        synchronized (lockFlushTimer){
            if (sessionHandler != null){
                stopFlushTimer();
                sessionHandler.postDelayed(sessionFlushRunnable, Contract.FLUSH_DELAY);
            }
        }
    }

    private void stopFlushTimer(){
        synchronized (lockFlushTimer){
            if(sessionHandler != null){
                sessionHandler.removeCallbacks(sessionFlushRunnable);
            }
        }
    }
}
