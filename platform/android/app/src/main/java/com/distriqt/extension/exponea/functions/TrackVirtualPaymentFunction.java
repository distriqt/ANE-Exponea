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
package com.distriqt.extension.exponea.functions;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.distriqt.extension.exponea.ExponeaContext;
import com.distriqt.extension.exponea.utils.Errors;

public class TrackVirtualPaymentFunction implements FREFunction
{

	@Override
	public FREObject call( FREContext context, FREObject[] args )
	{
		try
		{
			ExponeaContext ctx = (ExponeaContext)context;

			String currency = args[0].getAsString();
			int amount = args[1].getAsInt();
			String itemName = args[2].getAsString();
			String itemType = args[3].getAsString();

			ctx.controller().trackVirtualPayment( currency, amount, itemName, itemType );
		}
		catch (Exception e)
		{
			Errors.handleException( context, e );
		}
		return null;
	}

}
