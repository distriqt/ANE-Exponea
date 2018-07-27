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
 * @brief  		Main Context for an ActionScript Native Extension
 * @author 		Michael Archbold
 * @created		Jan 19, 2012
 * @copyright	http://distriqt.com/copyright/license.txt
 *
 */
package com.distriqt.extension.exponea;

import android.content.Intent;
import android.content.res.Configuration;

import com.adobe.air.ActivityResultCallback;
import com.adobe.air.AndroidActivityWrapper;
import com.adobe.air.StateChangeCallback;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.distriqt.core.utils.IExtensionContext;
import com.distriqt.extension.exponea.controller.ExponeaController;
import com.distriqt.extension.exponea.functions.DisableAutomaticFlushingFunction;
import com.distriqt.extension.exponea.functions.FlushFunction;
import com.distriqt.extension.exponea.functions.GetCPUFrequencyFunction;
import com.distriqt.extension.exponea.functions.GetCurrentSegmentFunction;
import com.distriqt.extension.exponea.functions.GetNumberOfCoresFunction;
import com.distriqt.extension.exponea.functions.GetTotalRAMFunction;
import com.distriqt.extension.exponea.functions.IdentifyFunction;
import com.distriqt.extension.exponea.functions.ImplementationFunction;
import com.distriqt.extension.exponea.functions.InitialiseFunction;
import com.distriqt.extension.exponea.functions.IsSupportedFunction;
import com.distriqt.extension.exponea.functions.TrackFunction;
import com.distriqt.extension.exponea.functions.TrackGooglePurchaseFunction;
import com.distriqt.extension.exponea.functions.TrackSessionEndFunction;
import com.distriqt.extension.exponea.functions.TrackSessionStartFunction;
import com.distriqt.extension.exponea.functions.TrackVirtualPaymentFunction;
import com.distriqt.extension.exponea.functions.UpdateFunction;
import com.distriqt.extension.exponea.functions.VersionFunction;

import java.util.HashMap;
import java.util.Map;

public class ExponeaContext extends FREContext implements IExtensionContext, ActivityResultCallback, StateChangeCallback
{
	////////////////////////////////////////////////////////////
	//	CONSTANTS
	//

	public static final String TAG = ExponeaContext.class.getSimpleName();

	public static final String VERSION = "1.0";
	public static final String IMPLEMENTATION = "Android";



	////////////////////////////////////////////////////////////
	//	VARIABLES
	//

	private AndroidActivityWrapper _aaw;

	private ExponeaController _controller = null;



	////////////////////////////////////////////////////////////
	//	FUNCTIONALITY
	//

	public ExponeaContext()
	{
		_aaw = AndroidActivityWrapper.GetAndroidActivityWrapper();
		_aaw.addActivityResultListener( this );
		_aaw.addActivityStateChangeListner( this );
	}


	@Override
	public void dispose() 
	{
		if (_controller != null)
		{
			_controller.dispose();
			_controller = null;
		}
		if (_aaw != null)
		{
			_aaw.removeActivityResultListener( this );
			_aaw.removeActivityStateChangeListner( this );
			_aaw = null;
		}
	}

	
	@Override
	public Map<String, FREFunction> getFunctions() 
	{
		Map<String, FREFunction> functionMap = new HashMap<String, FREFunction>();
		
		functionMap.put( "isSupported", 		new IsSupportedFunction() );
		functionMap.put( "version",   			new VersionFunction() );
		functionMap.put( "implementation", 		new ImplementationFunction() );

		functionMap.put( "initialise", 			new InitialiseFunction() );
		functionMap.put( "identify", 			new IdentifyFunction() );
		functionMap.put( "update", 				new UpdateFunction() );
		functionMap.put( "track", 				new TrackFunction() );

		functionMap.put( "trackSessionStart", 	new TrackSessionStartFunction() );
		functionMap.put( "trackSessionEnd", 	new TrackSessionEndFunction() );

		functionMap.put( "trackGooglePurchase",			new TrackGooglePurchaseFunction() );
		functionMap.put( "trackVirtualPayment",			new TrackVirtualPaymentFunction() );
		functionMap.put( "getCurrentSegment",			new GetCurrentSegmentFunction() );
		functionMap.put( "disableAutomaticFlushing",	new DisableAutomaticFlushingFunction() );
		functionMap.put( "flush",						new FlushFunction() );


		functionMap.put( "getCPUFrequency", 	new GetCPUFrequencyFunction() );
		functionMap.put( "getNumberOfCores", 	new GetNumberOfCoresFunction() );
		functionMap.put( "getTotalRAM",		 	new GetTotalRAMFunction() );

		return functionMap;
	}


	//
	//	CONTROLLER
	//

	public ExponeaController controller()
	{
		if (_controller == null)
		{
			_controller = new ExponeaController( this );
		}
		return _controller;
	}


	//
	//	ActivityResultCallback
	//

	@Override
	public void onActivityResult( int requestCode, int resultCode, Intent intent )
	{
		if (_controller != null)
		{
			_controller.onActivityResult( requestCode, resultCode, intent );
		}
	}


	//
	//	StateChangeCallback
	//

	@Override
	public void onActivityStateChanged( AndroidActivityWrapper.ActivityState state )
	{
		if (_controller != null)
		{
			switch (state)
			{
				case STARTED:
					_controller.onStart();
					break;

				case STOPPED:
					_controller.onStop();
					break;

				case PAUSED:
					_controller.onPause();
					break;

				case RESTARTED:
					_controller.onRestart();
					break;

				case DESTROYED:
					_controller.onDestroy();
					break;

				case RESUMED:
					_controller.onResume();
					break;
			}
		}
	}


	@Override
	public void onConfigurationChanged( Configuration paramConfiguration )
	{
	}


	//
	//	IExtensionContext
	//

	@Override
	public void dispatchEvent( final String code, final String data )
	{
		try
		{
			dispatchStatusEventAsync( code, data );
		}
		catch (Exception e)
		{
		}
	}
	
}
