package com.infinario.android.infinariosdk;

import android.os.Build;

import java.util.HashMap;
import java.util.Map;

/**
 * This file has been created by igi on 3/10/15.
 */
public class Device {
    public static Map<String, Object> deviceProperties(Preferences preferences) {
        Map<String, Object> properties = new HashMap<>();

        properties.put("sdk", Contract.SDK);
        properties.put("sdk_version", Contract.VERSION);
        properties.put("device_model", Build.MODEL);
        properties.put("device_type", preferences.getDeviceType());
        properties.put("os_version", Build.VERSION.RELEASE);
        properties.put("os_name", Contract.OS);

        return properties;
    }
}
