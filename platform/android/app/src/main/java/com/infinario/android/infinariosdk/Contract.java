package com.infinario.android.infinariosdk;

import android.app.AlarmManager;
import android.os.Build;

/**
 * This file has been created by igi on 1/14/15.
 */
public class Contract {
    /**
     * Logging
     */
    public static final String TAG = "Infinario";

    /**
     * SDK
     */
    public static final String SDK = "AndroidSDK";
    public static final String VERSION = "1.1.4";
    public static final String OS = "Android";

    /**
     * Preferences details
     */
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String COOKIE = "cookie";
    public static final String CAMPAIGN_COOKIE = "campaign_cookie";
    public static final String REGISTERED = "registered";
    public static final String PROPERTY_APP_VERSION = "app_version";
    public static final String PROPERTY_SENDER_ID = "sender_id";
    public static final String PROPERTY_TARGET = "target";
    public static final String PROPERTY_AUTO_FLUSH = "auto_flush";
    public static final String PROPERTY_GOOGLE_PUSH_NOTIFICATIONS = "google_push_notifications";
    public static final String PROPERTY_ICON = "icon";
    public static final String PROPERTY_REFERRER = "referrer";
    public static final String PROPERTY_TOKEN = "token";
    public static final String PROPERTY = "infinario";
    public static final String PROPERTY_SESSION_START = "session_start";
    public static final String PROPERTY_SESSION_END = "session_end";
    public static final String PROPERTY_SESSION_END_PROPERTIES = "session_end_properties";
    public static final String EXTRA_REQUEST_CODE = "request_code";
    public static final String PROPERTY_GOOGLE_ADV_ID = "google_advertising_id";
    public static final String PROPERTY_DEVICE_TYPE = "device_type";

    /**
     * Cookie ID negotiation
     */
    public static final String NEGOTIATION_ENDPOINT = "/crm/customers/track";

    /**
     * Infinario admin details
     */
    public static final String DB_GOOGLE_REGISTRATION_ID = "google_push_notification_id";

    /**
     * Command details
     */
    public static final String CUSTOMER_ENDPOINT = "crm/customers";
    public static final String EVENT_ENDPOINT = "crm/events";
    public static final String DEFAULT_TARGET = "https://api.infinario.com";
    public static final String PING_TARGET = "/system/time";
    public static final String BULK_URL = "/bulk";
    public static final String SEGMENT_URL = "/analytics/segmentation-for";

    /**
     * GCM details
     */
    public static final int NOTIFICATION_ID = 444;
    public static final boolean DEFAULT_PUSH_NOTIFICATIONS = false;

    /**
     * Automatic flushing
     */
    public static final long FLUSH_DELAY = 10 * 1000;
    public static final int FLUSH_COUNT = 50;
    public static final boolean DEFAULT_AUTO_FLUSH = true;
    public static int FLUSH_MIN_INTERVAL = 1000;
    public static int FLUSH_MAX_INTERVAL = 3600000;

    /**
     * Session
     */
    public static final int SESSION_PING_INTERVAL = 1; // in seconds
    public static final int SESSION_TIMEOUT = 60 * 1000;

    /**
     * DbQueue controls
     */
    public static final int DEFAULT_LIMIT = 50;
    public static final int MAX_RETRIES = 20;

    /**
     * Database
     */
    public static final String TABLE_COMMANDS = "commands";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_COMMAND = "command";
    public static final String COLUMN_RETRIES = "retries";

    public static final String DATABASE_NAME = "commands.db";
    public static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    public static final String DATABASE_CREATE = "create table "
            + TABLE_COMMANDS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_COMMAND
            + " text not null, " + COLUMN_RETRIES
            + " integer not null default 0);";
}
