/**
 *        __       __               __ 
 *   ____/ /_ ____/ /______ _ ___  / /_
 *  / __  / / ___/ __/ ___/ / __ `/ __/
 * / /_/ / (__  ) / / /  / / /_/ / / 
 * \__,_/_/____/_/ /_/  /_/\__, /_/ 
 *                           / / 
 *                           \/ 
 * http://distriqt.com
 *
 * @brief
 * @author marchbold
 * @created 20/07/2018
 * @copyright http://distriqt.com/copyright/license.txt
 */
package com.distriqt.extension.exponea.controller;

import android.content.Intent;

import org.json.JSONObject;

public class Purchase
{
	public String productId;
	public long purchaseTime = 0;
	public String purchaseToken;



	public String toJSONString()
	{
		JSONObject o = new JSONObject();
		try
		{
			if (productId != null) o.put( "productId", productId );
			o.put( "purchaseTime", purchaseTime );
			if (purchaseToken != null) o.put( "purchaseToken", purchaseToken );
		}
		catch (Exception e)
		{
		}
		return o.toString();
	}


	public Intent toIntent()
	{
		Intent i = new Intent();

		i.putExtra( "RESPONSE_CODE", 0 );
		i.putExtra( "INAPP_PURCHASE_DATA", toJSONString() );

		return i;
	}

}
