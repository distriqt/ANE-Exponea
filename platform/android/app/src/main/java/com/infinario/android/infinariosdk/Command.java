package com.infinario.android.infinariosdk;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * This file has been created by igi on 1/13/15.
 */

public class Command {

    protected String endpoint;
    protected Date createdAt;

    public Command(String endpoint, Long timestamp) {
        this.endpoint = endpoint;
        this.createdAt = ((null == timestamp) ? new Date() : new Date(timestamp));
    }

    public Command(String endpoint) {
        this(endpoint, null);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> command = new HashMap<>();

        command.put("name", endpoint);
        command.put("data", new JSONObject(getData()));

        return command;
    }

    public String toString() {
        return (new JSONObject(toMap())).toString();
    }

    protected Map<String, Object> getData() {
        return new HashMap<>();
    }
}
