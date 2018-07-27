package com.infinario.android.infinariosdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class ReferrerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            Log.d(Contract.TAG, "Referrer received: " + intent.toString());

            String rawReferrer = intent.getStringExtra("referrer");

            if (rawReferrer != null) {
                saveReferrer(rawReferrer, context);
            }
        }
    }

    private void saveReferrer(String rawReferrer, Context context) {
        String referrer;

        try {
            referrer = URLDecoder.decode(rawReferrer, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return;
        }

        if (referrer == null || referrer.isEmpty()) {
            return;
        }

        Uri uri = Uri.parse('?' + referrer); // appends ? for Uri to pickup query string

        String campaignId;

        try {
            campaignId = uri.getQueryParameter("campaign_id");
        } catch (UnsupportedOperationException e) {
            return;
        }

        Preferences.get(context).setReferrer(campaignId);
    }
}