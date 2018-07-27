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
 * @author 		"Michael Archbold (ma&#64;distriqt.com)"
 * @created 16/11/2017
 * @copyright http://distriqt.com/copyright/license.txt
 */
package com.distriqt.extension.exponea.controller;

import android.app.Activity;

import com.distriqt.core.ActivityStateListener;
import com.distriqt.core.utils.IExtensionContext;
import com.distriqt.extension.exponea.events.ExponeaEvent;
import com.distriqt.extension.exponea.utils.Errors;
import com.distriqt.extension.exponea.utils.Logger;
import com.infinario.android.infinariosdk.Infinario;
import com.infinario.android.infinariosdk.InfinarioSegment;

import java.util.Map;

public class ExponeaController extends ActivityStateListener
{
	////////////////////////////////////////////////////////////
	//	CONSTANTS
	//

	public static final String TAG = ExponeaController.class.getSimpleName();



	////////////////////////////////////////////////////////////
	//	VARIABLES
	//


	private IExtensionContext _extContext;

	private Infinario _infinario;



	////////////////////////////////////////////////////////////
	//	FUNCTIONALITY
	//

	public ExponeaController( IExtensionContext extensionContext )
	{
		_extContext = extensionContext;
	}


	public void dispose()
	{
		if (_infinario != null)
		{
			_infinario = null;
		}
		_extContext = null;
	}


	public void initialise( String token, String url )
	{
		Logger.d( TAG, "initialise( %s, %s )", token, url );
		try
		{
			_infinario = Infinario.getInstance( _extContext.getActivity().getApplicationContext(), token, url );
		}
		catch (Exception e)
		{
			Errors.handleException( e );
		}
	}

	public void identify( String customerId )
	{
		Logger.d( TAG, "identify( %s )", customerId );

		if (_infinario != null)
		{
			_infinario.identify( customerId );
		}
	}


	public void update( Map<String, Object> properties )
	{
		Logger.d( TAG, "update( %s )", properties.toString() );
		if (_infinario != null)
		{
			_infinario.update( properties );
		}
	}


	public void track( String type, Map<String, Object> properties, double timestamp )
	{
		Logger.d( TAG, "track( %s, %s, %f )", type, properties.toString(), timestamp );
		if (_infinario != null)
		{
			if (timestamp > 0)
			{
				_infinario.track( type, properties, (long) timestamp );
			}
			else
			{
				_infinario.track( type, properties, null );
			}
		}
	}


	public void trackSessionStart()
	{
		Logger.d( TAG, "trackSessionStart()" );
		if (_infinario != null)
		{
			_infinario.trackSessionStart();
		}
	}


	public void trackSessionEnd()
	{
		Logger.d( TAG, "trackSessionEnd()" );
		if (_infinario != null)
		{
			_infinario.trackSessionEnd();
		}
	}


	public void trackGooglePurchase( Purchase purchase )
	{
		Logger.d( TAG, "trackPurchase()" );
		if (_infinario != null)
		{
			_infinario.trackGooglePurchases( Activity.RESULT_OK, purchase.toIntent() );
		}
	}


	public void trackVirtualPayment( String currency, int amount, String itemName, String itemType )
	{
		Logger.d( TAG, "trackVirtualPayment( %s, %d, %s, %s )", currency, amount, itemName, itemType );
		if (_infinario != null)
		{
			_infinario.trackVirtualPayment( currency, amount, itemName, itemType );
		}
	}


	public void getCurrentSegment( String segmentationId, String projectSecretToken )
	{
		Logger.d( TAG, "getCurrentSegment( %s, %s )", segmentationId, projectSecretToken );
		if (_infinario != null)
		{
			_infinario.getCurrentSegment( segmentationId, projectSecretToken, new Infinario.SegmentListener()
			{
				@Override
				public void onSegmentReceive( boolean wasSuccessful, InfinarioSegment segment, String error )
				{
					try
					{
						Logger.d( TAG, "getCurrentSegment::onSegmentReceive( %b, %s, %s )",
								wasSuccessful,
								segment == null ? "null" : segment.getName(),
								error == null ? "null" : error
						);

						if (wasSuccessful)
						{
							_extContext.dispatchEvent(
									ExponeaEvent.GET_CURRENT_SEGMENT_SUCCESS,
									ExponeaEvent.formatSegmentForEvent( segment.getName() )
							);
						}
						else
						{
							_extContext.dispatchEvent(
									ExponeaEvent.GET_CURRENT_SEGMENT_ERROR,
									ExponeaEvent.formatErrorForEvent( -1, error )
							);
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			} );
		}
	}


	public void disableAutomaticFlushing()
	{
		Logger.d( TAG, "disableAutomaticFlushing()" );
		if (_infinario != null)
		{
			_infinario.disableAutomaticFlushing();
		}
	}


	public void flush()
	{
		Logger.d( TAG, "flush()" );
		if (_infinario != null)
		{
			_infinario.flush();
		}
	}


	//
	//	INFO
	//

	public double getCPUFrequency()
	{
		Logger.d( TAG, "getCPUFrequency()" );
		try
		{
			return (double)SystemUtils.getMaxCPUFreqMHz();
		}
		catch (Throwable e)
		{
			Errors.handleException( e );
		}
		return 0;
	}


	public int getNumberOfCores()
	{
		Logger.d( TAG, "getNumberOfCores()" );
		try
		{
			return SystemUtils.getNumberOfCores();
		}
		catch (Throwable e)
		{
			Errors.handleException( e );
		}
		return 0;
	}


	public double getTotalRAM()
	{
		Logger.d( TAG, "getTotalRAM()" );
		try
		{
			return (double)SystemUtils.getTotalMemory( _extContext.getActivity().getApplicationContext() );
		}
		catch (Throwable e)
		{
			Errors.handleException( e );
		}
		return 0;
	}


}
