package com.infinario.android.infinariosdk;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This file has been created by igi on 1/13/15.
 */
public class Request {

    private int id;
    private JSONObject command;
    private int retries;


    public Request(int id, String command, int retries) throws JSONException {
        this.command = new JSONObject(command);
        this.id = id;
        this.retries = retries;
    }

    public JSONObject getCommand() {
        return command;
    }

    public int getId() {
        return id;
    }

    @SuppressWarnings("unused")
    public int getRetries() {
        return retries;
    }
}
