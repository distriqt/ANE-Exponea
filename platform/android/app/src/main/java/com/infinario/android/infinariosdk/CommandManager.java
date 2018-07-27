package com.infinario.android.infinariosdk;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This file has been created by igi on 1/13/15.
 */
public class CommandManager {

    private static final int MAX_RETRIES = 20;

    DbQueue queue;
    HttpHelper http;
    Preferences preferences;
    Object lockFlush;
    boolean flushInProgress;
    boolean flushMayNeedRestart;
    AsyncTask<Void, Void, Void> flushTask;

    public CommandManager(Context context, String target, String userAgent) {
        queue = new DbQueue(context);
        preferences = Preferences.get(context);
        http = new HttpHelper(target, userAgent);
        lockFlush = new Object();

        synchronized (lockFlush){
            flushInProgress = false;
            flushMayNeedRestart = false;
        }
    }

    public boolean schedule(Command command) {
        return queue.schedule(command);
    }

    public boolean executeBatch() {
        Set<Integer> deleteRequests = new HashSet<>();
        Set<Integer> successfulRequests = new HashSet<>();
        JSONArray results;
        JSONArray commands = new JSONArray();
        JSONObject payload = new JSONObject();
        List<Request> requests = queue.pop();
        JSONObject data;
        Request request;
        JSONObject result;
        String status;

        if (requests.isEmpty()) {
            return true;
        }

        Log.i(Contract.TAG, "sending ids " + requests.get(0).getId() + " - " + requests.get(requests.size() - 1).getId());

        for (Request r : requests) {
            commands.put(setCookieId(setAge(r.getCommand())));
        }

        try {
            payload.put("commands", commands);
        } catch (JSONException e) {
            Log.e(Contract.TAG, e.getMessage().toString());
        }

        data = http.post(Contract.BULK_URL, payload);

        StringBuilder logResult = new StringBuilder();
        logResult.append("Batch executed, ")
                .append(requests.size())
                .append(" prepared, ");


        if (data != null) {
            try {
                results = data.getJSONArray("results");
            } catch (JSONException e) {
                results = null;
                Log.e(Contract.TAG, e.getMessage().toString());
            }

            if (results != null) {
                for (int i = 0; i < requests.size() && i < results.length(); ++i) {
                    try {
                        request = requests.get(i);
                        result = results.getJSONObject(i);
                        status = result.getString("status").toLowerCase();

                        if (status.equals("ok")) {
                            deleteRequests.add(request.getId());
                            successfulRequests.add(request.getId());
                        } else if (status.equals("error")) {
                            deleteRequests.add(request.getId());
                        }
                    } catch (JSONException e) {
                        Log.e(Contract.TAG, e.getMessage().toString());
                    }
                }
                logResult.append(successfulRequests.size())
                        .append(" succeeded, ")
                        .append(deleteRequests.size() - successfulRequests.size());
            } else {
                Log.e(Contract.TAG, "Results are null");
                logResult.append("0 succeeded, ")
                        .append(requests.size());
            }
        } else {
            Log.e(Contract.TAG, "Data is null");
            logResult.append("0 succeeded, ")
                    .append(requests.size());
        }

        logResult.append(" failed, rest was told to retry");
        Log.i(Contract.TAG, logResult.toString());

        queue.clear(deleteRequests);

        return requests.size() == deleteRequests.size();
    }

    public boolean flush(final int count) {
        synchronized (lockFlush) {
            flushMayNeedRestart = true;
            if (!flushInProgress) {
                flushInProgress = true;
                flushTask = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        flushCommands(count);
                        return null;
                    }
                }.execute();

                return true;
            }
            else {
                return false;
            }
        }
    }

    private void flushCommands(int maxRetries) {
        while (true) {
            int delayFlush = 1000;
            int retryCounter = 0;

            synchronized (lockFlush) {
                if (!flushMayNeedRestart) {
                     flushInProgress = false;
                     break;
                }
                flushMayNeedRestart = false;
            }

            try{
                while (!queue.isEmpty() && (retryCounter <= maxRetries)) {
                    if (!executeBatch()) {
                        if (maxRetries > 1) {
                            Thread.sleep(delayFlush);
                            delayFlush = exponentialIncrease(delayFlush);
                        }

                        retryCounter += 1;
                    }
                }
            } catch (Exception e) {
                Log.e(Contract.TAG, e.getMessage().toString());
            }
        }
    }


    private int exponentialIncrease(int timeInMiliseconds)
    {
        int calculateDelay = timeInMiliseconds * 2;

        return Contract.FLUSH_MAX_INTERVAL < calculateDelay
                ? Contract.FLUSH_MAX_INTERVAL
                : calculateDelay;
    }

    private JSONObject setCookieId(JSONObject command) {
        try {
            JSONObject data = command.getJSONObject("data");

            if (data.has("ids") && data.getJSONObject("ids").getString("cookie").isEmpty()) {
                data.getJSONObject("ids").put("cookie", preferences.getCookieId());
            }

            if (data.has("customer_ids") && data.getJSONObject("customer_ids").getString("cookie").isEmpty()) {
                data.getJSONObject("customer_ids").put("cookie", preferences.getCookieId());
            }
        }
        catch (JSONException ignored) {
        }

        return command;
    }

    private JSONObject setAge(JSONObject command) {
        try {
            long timestamp = command.getJSONObject("data").getLong("age");
            command.getJSONObject("data").put("age", ((new Date()).getTime() - timestamp) / 1000L);
        }
        catch (JSONException ignored) {
        }

        return command;
    }
}
