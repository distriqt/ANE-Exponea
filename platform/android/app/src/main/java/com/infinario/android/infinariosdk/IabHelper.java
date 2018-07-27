/* Copyright (c) 2012 Google Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.infinario.android.infinariosdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import org.json.JSONException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class IabHelper {
    // Is setup done?
    boolean mSetupDone = false;

    // Has this object been disposed of? (If so, we should ignore callbacks, etc)
    boolean mDisposed = false;

    // Are subscriptions supported?
    boolean mSubscriptionsSupported = false;

    // Context we were passed during initialization
    Context mContext;

    // Connection to the service
    IabProxy mService;
    ServiceConnection mServiceConn;

    // Billing response codes
    public static final int BILLING_RESPONSE_RESULT_OK = 0;
    public static final int BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE = 3;

    // IAB Helper error codes
    public static final int IABHELPER_REMOTE_EXCEPTION = -1001;

    // Keys for the responses from InAppBillingService
    public static final String RESPONSE_CODE = "RESPONSE_CODE";
    public static final String INAPP_CONTINUATION_TOKEN = "INAPP_CONTINUATION_TOKEN";

    // Item types
    public static final String ITEM_TYPE_INAPP = "inapp";
    public static final String ITEM_TYPE_SUBS = "subs";

    /**
     * Creates an instance. After creation, it will not yet be ready to use. You must perform
     * setup by calling {@link #startSetup} and wait for setup to complete. This constructor does not
     * block and is safe to call from a UI thread.
     *
     * @param ctx Your application or Activity context. Needed to bind to the in-app billing service.
     */
    public IabHelper(Context ctx) {
        mContext = ctx.getApplicationContext();
    }

    /**
     * Callback for setup process. This listener's {@link #onIabSetupFinished} method is called
     * when the setup process is complete.
     */
    public interface OnIabSetupFinishedListener {
        /**
         * Called to notify that setup is complete.
         *
         * @param result The result of the setup process.
         */
        public void onIabSetupFinished(IabResult result);
    }

    /**
     * Starts the setup process. This will start up the setup process asynchronously.
     * You will be notified through the listener when the setup process is complete.
     * This method is safe to call from a UI thread.
     *
     * @param listener The listener to notify when the setup process is complete.
     */
    public void startSetup(final OnIabSetupFinishedListener listener) {
        if (mSetupDone) return;

        // Connection to IAB service
        Log.d(Contract.TAG, "Starting in-app billing setup.");
        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(Contract.TAG, "Billing service disconnected.");
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                if (mDisposed) return;
                Log.d(Contract.TAG, "Billing service connected.");

                mService = new IabProxy(service);

                if (!mService.isLoaded()) {
                    Log.i(Contract.TAG, "InAppBillingService.aidl not included, automatic payment tracking is unavailable");
                    return;
                }

                String packageName = mContext.getPackageName();
                try {
                    Log.d(Contract.TAG, "Checking for in-app billing 3 support.");

                    // check for in-app billing v3 support
                    int response = mService.isBillingSupported(3, packageName, ITEM_TYPE_INAPP);
                    if (response != BILLING_RESPONSE_RESULT_OK) {
                        if (listener != null) listener.onIabSetupFinished(new IabResult(response,
                                "Error checking for billing v3 support."));

                        // if in-app purchases aren't supported, neither are subscriptions.
                        mSubscriptionsSupported = false;
                        return;
                    }
                    Log.d(Contract.TAG, "In-app billing version 3 supported for " + packageName);

                    // check for v3 subscriptions support
                    response = mService.isBillingSupported(3, packageName, ITEM_TYPE_SUBS);
                    if (response == BILLING_RESPONSE_RESULT_OK) {
                        Log.d(Contract.TAG, "Subscriptions AVAILABLE.");
                        mSubscriptionsSupported = true;
                    }
                    else {
                        Log.d(Contract.TAG, "Subscriptions NOT AVAILABLE. Response: " + response);
                    }

                    mSetupDone = true;
                }
                catch (RemoteException e) {
                    if (listener != null) {
                        listener.onIabSetupFinished(new IabResult(IABHELPER_REMOTE_EXCEPTION,
                                                    "RemoteException while setting up in-app billing."));
                    }
                    e.printStackTrace();
                    return;
                }

                if (listener != null) {
                    listener.onIabSetupFinished(new IabResult(BILLING_RESPONSE_RESULT_OK, "Setup successful."));
                }
            }
        };

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");

        List<ResolveInfo> services = mContext.getPackageManager().queryIntentServices(serviceIntent, 0);

        if (services != null && !services.isEmpty()) {
            // service available to handle that Intent
            mContext.bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
        }
        else {
            // no service available to handle that Intent
            if (listener != null) {
                listener.onIabSetupFinished(
                        new IabResult(BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE,
                        "Billing service unavailable on device."));
            }
        }
    }

    /**
     * Dispose of object, releasing resources. It's very important to call this
     * method when you are done with this object. It will release any resources
     * used by it such as service connections. Naturally, once the object is
     * disposed of, it can't be used again.
     */
    public void dispose() {
        mSetupDone = false;
        if (mServiceConn != null) {
            if (mContext != null) mContext.unbindService(mServiceConn);
        }
        mDisposed = true;
        mContext = null;
        mServiceConn = null;
        mService = null;
    }

    /** Returns whether subscriptions are supported. */
    public boolean subscriptionsSupported() {
        return mSubscriptionsSupported;
    }

    public boolean setupDone() {
        return mSetupDone;
    }

    SkuDetails querySkuDetails(String itemType, String sku) throws RemoteException, JSONException {
        if (!setupDone()) {
            return null;
        }

        ArrayList<String> skuList = new ArrayList<>();
        skuList.add(sku);
        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
        Bundle skuDetails = mService.getSkuDetails(3, mContext.getPackageName(), itemType, querySkus);

        if (skuDetails == null || !skuDetails.containsKey("DETAILS_LIST")) {
            return null;
        }

        ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");

        if (responseList.isEmpty()) {
            return null;
        }

        return new SkuDetails(responseList.get(0));
    }

    // Workaround to bug where sometimes response codes come as Long instead of Integer
    public static int getResponseCodeFromIntent(Intent i) {
        Object o = i.getExtras().get("RESPONSE_CODE");
        if (o == null) {
            Log.i(Contract.TAG, "Intent with no response code, assuming OK (known issue)");
            return 0;
        }
        else if (o instanceof Integer) return ((Integer)o).intValue();
        else if (o instanceof Long) return (int)((Long)o).longValue();
        else {
            Log.e(Contract.TAG, "Unexpected type for intent response code.");
            return 0;
        }
    }
}
