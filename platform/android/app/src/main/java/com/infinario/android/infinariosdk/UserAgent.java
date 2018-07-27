package com.infinario.android.infinariosdk;

import android.os.Build;
import android.util.Log;

/**
 * Created by rolandrogansky on 06/10/15.
 */
public class UserAgent {
    public static String create(Preferences preferences) {
        StringBuilder userAgent = new StringBuilder();
        String deviceType = preferences.getDeviceType();

        userAgent = new StringBuilder();
        userAgent.append("InfinarioAndroidSDK/")
                .append(Contract.VERSION)
                .append(" (Android ")
                .append(Build.VERSION.RELEASE);

        if (!deviceType.isEmpty()){
            try {
                userAgent.append("; ")
                        .append(deviceType.substring(0,1).toUpperCase() + deviceType.substring(1));
            } catch (StringIndexOutOfBoundsException e){
                Log.e(Contract.TAG, "Unknow device type");
            }
        }

        userAgent.append(")");

        return userAgent.toString();
    }
}