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
 * @brief  		Main Extension implementation for this ANE
 * @author 		Michael Archbold
 * @created		Jan 19, 2012
 * @copyright	http://distriqt.com/copyright/license.txt
 *
 */
package com.distriqt.extension.exponea;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREExtension;

public class ExponeaExtension implements FREExtension
{
	public static ExponeaContext context;
	
	public static String ID	= "com.distriqt.Exponea";
	
	
	@Override
	public FREContext createContext(String arg0) 
	{
		context = new ExponeaContext();
		return context;
	}

	
	@Override
	public void dispose()
	{
		if (context != null)
		{
			context.dispose();
			context = null;
		}
	}

	
	@Override
	public void initialize() 
	{
	}

}
