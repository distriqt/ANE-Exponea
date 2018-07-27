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
 * @created 05/07/2018
 * @copyright http://distriqt.com/copyright/license.txt
 */
package com.distriqt.extension.exponea.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class JSONUtils
{

	public static HashMap<String,Object> jsonObjectToMap( JSONObject json )
	{
		HashMap<String,Object> map = new HashMap<>();

		Iterator<String> iter = json.keys();
		while (iter.hasNext())
		{
			String key = iter.next();
			try
			{
				Object value = json.get( key );
				map.put( key, value );
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}

		return map;
	}




}
