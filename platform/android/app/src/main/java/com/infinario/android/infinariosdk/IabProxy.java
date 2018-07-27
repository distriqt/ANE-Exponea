package com.infinario.android.infinariosdk;

import android.os.IBinder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This file has been created by igi on 3/11/15.
 */
public class IabProxy {

    private Class<?> stub;
    private Object proxy;

    public IabProxy(IBinder service) {
        try {
            stub = Class.forName("com.android.vending.billing.IInAppBillingService$Stub");
            Method method = stub.getMethod("asInterface", android.os.IBinder.class);
            proxy = method.invoke(null, service);
            return;
        }
        catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
        }

        stub = null;
        proxy = null;
    }

    public boolean isLoaded() {
        return (stub != null && proxy != null);
    }

    public int isBillingSupported(int apiVersion, String packageName, String type) throws android.os.RemoteException {
        try {
            Method method = stub.getMethod("isBillingSupported", Integer.TYPE, String.class, String.class);
            return (Integer) method.invoke(proxy, apiVersion, packageName, type);
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
        }

        return 0;
    }

    public android.os.Bundle getSkuDetails(int apiVersion, java.lang.String packageName, java.lang.String type, android.os.Bundle skusBundle) throws android.os.RemoteException {
        try {
            Method method = stub.getMethod("getSkuDetails", Integer.TYPE, String.class, String.class,android.os.Bundle.class);
            return (android.os.Bundle) method.invoke(proxy, apiVersion, packageName, type, skusBundle);
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
        }

        return null;
    }
}
