package com.infinario.android.infinariosdk;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * This file has been created by igi on 1/13/15.
 */
public class Customer extends Command {

    protected Map<String, String> ids;
    protected String companyId;
    protected Map<String, Object> properties = null;

    public Customer(Map<String, String> ids, String companyId, Map<String, Object> properties) {
        super(Contract.CUSTOMER_ENDPOINT);

        this.ids = ids;
        this.companyId = companyId;
        this.properties = properties;
    }

    @Override
    protected Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<>();

        data.put("ids", new JSONObject(ids));
        data.put("company_id", companyId);

        if (null != properties) {
            data.put("properties", new JSONObject(properties));
        }

        return data;
    }


}
