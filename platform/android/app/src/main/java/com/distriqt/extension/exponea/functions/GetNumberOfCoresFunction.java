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
 * @created 11/07/2018
 * @copyright http://distriqt.com/copyright/license.txt
 */
package com.distriqt.extension.exponea.functions;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.distriqt.extension.exponea.ExponeaContext;
import com.distriqt.extension.exponea.utils.Errors;

public class GetNumberOfCoresFunction implements FREFunction
{

	@Override
	public FREObject call( FREContext context, FREObject[] args )
	{
		FREObject result = null;
		try
		{
			ExponeaContext ctx = (ExponeaContext)context;

			int cores = ctx.controller().getNumberOfCores();

			result = FREObject.newObject( cores );
		}
		catch (Exception e)
		{
			Errors.handleException(context,e);
		}
		return result;
	}

}
