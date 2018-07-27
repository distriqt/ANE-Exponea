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
package com.distriqt.extension.exponea.events;

import org.json.JSONObject;

public class ExponeaEvent
{
	public static final String GET_CURRENT_SEGMENT_SUCCESS = "get:current:segment:success";
	public static final String GET_CURRENT_SEGMENT_ERROR = "get:current:segment:error";



	public static String formatSegmentForEvent( String segment )
	{
		try
		{
			JSONObject event = new JSONObject();

			event.put( "segment", segment );

			return event.toString();
		}
		catch (Exception e)
		{
		}
		return "{}";
	}


	public static String formatErrorForEvent( int code, String message )
	{
		try
		{
			JSONObject event = new JSONObject();

			event.put( "errorCode", code );
			event.put( "message", message );

			return event.toString();
		}
		catch (Exception e)
		{
		}
		return "{}";
	}


}
