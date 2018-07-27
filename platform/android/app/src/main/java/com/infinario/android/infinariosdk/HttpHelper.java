package com.infinario.android.infinariosdk;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * This file has been created by igi on 1/13/15.
 */
public class HttpHelper {

    private String target;
    private Map<String, Object> header;
    private String userAgent;
    private int timeout;

    public HttpHelper(String target, String userAgent){
        if (target == null) {
            this.target = Contract.DEFAULT_TARGET;
        }
        else {
            this.target = target;
        }

        this.userAgent = userAgent;
        timeout = 10000;
        header = new HashMap<String, Object>();
    }

    public HttpHelper setTimeout(int timeout){
        this.timeout = timeout;
        return this;
    }

    public HttpHelper addRequestProperty(String field, String value){
        header.put(field, value);
        return this;
    }

    public JSONObject post(String endPoint, JSONObject data){
        HttpURLConnection connection = null;

        try {
            URL url = new URL(target + endPoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            if (userAgent != null){
                connection.setRequestProperty("User-Agent", userAgent);
            }

            if (header.size() > 0) {
                for (Map.Entry<String, Object> entry : header.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue().toString());
                }
            }

            connection.setRequestMethod("POST");

            connection.connect();

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(os, "UTF-8"));
            writer.write(data.toString());
            writer.close();
            os.close();

            InputStream is = null;
            try {
                is = connection.getInputStream();
            }
            catch (IOException e) {
                is = connection.getErrorStream(); // may return null, hence the check further on
            }

            if (is != null) {
                BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringWriter response = new StringWriter();
                char[] buffer = new char[1024 * 4];
                int n = 0;
                while (-1 != (n = responseBuffer.read(buffer))) {
                    response.write(buffer, 0, n);
                }

                return new JSONObject(response.toString());
            } else {
                Log.e(Contract.TAG, "Response is null");
            }

        } catch (MalformedURLException e) {
            Log.e(Contract.TAG, e.toString());
        } catch (IOException e) {
            Log.e(Contract.TAG, e.toString());
        } catch (JSONException e) {
            Log.e(Contract.TAG, e.toString());
        } finally {
            if (connection != null){
                connection.disconnect();
            }
        }

        return null;
    }
}
