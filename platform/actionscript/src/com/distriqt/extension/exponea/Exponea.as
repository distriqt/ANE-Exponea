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
 * @brief  		Exponea Native Extension
 * @author 		Michael Archbold
 * @created		Jan 19, 2012
 * @copyright	http://distriqt.com/copyright/license.txt
 */
package com.distriqt.extension.exponea
{
	import com.distriqt.extension.exponea.events.ExponeaEvent;
	
	import flash.events.Event;
	import flash.events.ErrorEvent;
	import flash.events.EventDispatcher;
	import flash.events.StatusEvent;
	import flash.external.ExtensionContext;
	
	
	/**	
	 * <p>
	 * This class represents the Exponea extension.
	 * </p>
	 */
	public final class Exponea extends EventDispatcher
	{
		////////////////////////////////////////////////////////
		//	CONSTANTS
		//

		//
		//	ID and Version numbers
		public static const EXT_CONTEXT_ID			: String = "com.distriqt.Exponea";
		private static const EXT_ID_NUMBER			: int = -1;
		
		public static const VERSION					: String = Version.VERSION;
		private static const VERSION_DEFAULT		: String = "0";
		private static const IMPLEMENTATION_DEFAULT	: String = "unknown";
		
		//
		//	Error Messages
		private static const ERROR_CREATION			: String = "The native extension context could not be created";
		private static const ERROR_SINGLETON		: String = "The singleton has already been created. Use Exponea.service to access the functionality";
		
		
		////////////////////////////////////////////////////////
		//	VARIABLES
		//
		
		//
		// Singleton variables
		private static var _instance				: Exponea;
		private static var _shouldCreateInstance	: Boolean = false;
		
		private static var _extContext		: ExtensionContext = null;
		
		private var _extensionId			: String = "";
		private var _extensionIdNumber		: int = -1;
		
		
		
		////////////////////////////////////////////////////////
		//	SINGLETON INSTANCE
		//
		
		/**
		 * The singleton instance of the Exponea class.
		 * @throws Error If there was a problem creating or accessing the extension, or if the key is invalid
		 */
		public static function get instance():Exponea
		{
			createInstance();
			return _instance;
		}
		
		
		/**
		 * @private
		 * Creates the actual singleton instance 
		 */
		private static function createInstance():void
		{
			if(_instance == null)
			{
				_shouldCreateInstance = true; 
				_instance = new Exponea();
				_shouldCreateInstance = false;
			}
		}
		
		
		
		////////////////////////////////////////////////////////
		//	FUNCTIONALITY
		//
		
		/**
		 * Constructor
		 * 
		 * You should not call this directly, but instead use the singleton access
		 */
		public function Exponea()
		{
			if (_shouldCreateInstance)
			{
				try
				{
					_extensionId = EXT_CONTEXT_ID;
					_extensionIdNumber = EXT_ID_NUMBER;
					_extContext = ExtensionContext.createExtensionContext( EXT_CONTEXT_ID, null );
					_extContext.addEventListener( StatusEvent.STATUS, extension_statusHandler, false, 0, true );
				}
				catch (e:Error)
				{
					throw new Error( ERROR_CREATION );
				}
			}
			else
			{
				throw new Error( ERROR_SINGLETON );
			}
		}
		
		
		/**
		 * <p>
		 * Disposes the extension and releases any allocated resources. Once this function 
		 * has been called, a call to <code>init</code> is neccesary again before any of the 
		 * extensions functionality will work.
		 * </p>
		 */		
		public function dispose():void
		{
			if (_extContext)
			{
				_extContext.removeEventListener( StatusEvent.STATUS, extension_statusHandler );
				_extContext.dispose();
				_extContext = null;
			}
			_instance = null;
		}
		
		
		/**
		 * Whether the current device supports the extensions functionality
		 */
		public static function get isSupported():Boolean
		{
			createInstance();
			return _extContext.call( "isSupported" );
		}
		
		
		/**
		 * <p>
		 * The version of this extension.
		 * </p>
		 * <p>
		 * This should be of the format, MAJOR.MINOR.BUILD
		 * </p>
		 */
		public function get version():String
		{
			return VERSION;
		}
		
		
		/**
		 * <p>
		 * The native version string of the native extension.
		 * </p>
		 */
		public function get nativeVersion():String
		{
			try
			{
				return _extContext.call( "version" ) as String;
			}
			catch (e:Error)
			{
			}
			return VERSION_DEFAULT;
		}
		
		
		/**
		 * <p>
		 * The implementation currently in use. 
		 * This should be one of the following depending on the platform in use and the
		 * functionality supported by this extension:
		 * <ul>
		 * <li><code>Android</code></li>
		 * <li><code>iOS</code></li>
		 * <li><code>default</code></li>
		 * <li><code>unknown</code></li>
		 * </ul>
		 * </p>
		 */		
		public function get implementation():String
		{
			try
			{
				return _extContext.call( "implementation" ) as String;
			}
			catch (e:Error)
			{
			}
			return IMPLEMENTATION_DEFAULT;
		}
		
		
		//
		//
		//	EXTENSION FUNCTIONALITY
		//
		//
		
		
		/**
		 * Initialise the Exponea SDK
		 *
		 * @param token
		 * @param url
		 */
		public function initialise( token:String, url:String="https://api.exponea.com" ):void
		{
			try
			{
				_extContext.call( "initialise", token, url );
			}
			catch (e:Error)
			{
			}
		}
		
		
		/**
		 * Identify the user
		 *
		 * @param customerId
		 */
		public function identify( customerId:String ):void
		{
			try
			{
				_extContext.call( "identify", customerId );
			}
			catch (e:Error)
			{
			}
		}
		
		
		/**
		 * Store arbitrary data that is not event-specific e.g. customer age, gender, initial referrer
		 *
		 * @param properties
		 */
		public function update( properties:Object ):void
		{
			try
			{
				_extContext.call( "update", JSON.stringify(properties) );
			}
			catch (e:Error)
			{
			}
		}
		
		
		/**
		 * Track event
		 *
		 * @param type
		 * @param properties
		 * @param timestamp  	Unix timestamp, the number of milliseconds since Jan 01 1970 (default uses the current time)
		 */
		public function track( type:String, properties:Object=null, timestamp:Number=-1 ):void
		{
			try
			{
				if (properties == null) properties = {};
	
				_extContext.call( "track", type, JSON.stringify(properties), timestamp );
			}
			catch (e:Error)
			{
			}
		}
		
		
		/**
		 * Start a session
		 */
		public function trackSessionStart():void
		{
			try
			{
				_extContext.call( "trackSessionStart" );
			}
			catch (e:Error)
			{
			}
		}
		
		
		/**
		 * End a session
		 */
		public function trackSessionEnd():void
		{
			try
			{
				_extContext.call( "trackSessionEnd" );
			}
			catch (e:Error)
			{
			}
		}
		
		
		/**
		 * Track the completion of a payment on Android through Google Play.
		 * This process fakes the completion Intent by constructing a successful payment
		 * Intent from the data in the <code>Purchase</code>. It must contain a valid Google Play,
		 * <code>productId</code> and a <code>purchaseTime</code> and <code>purchaseToken</code>
		 * that you wish to track.
		 *
		 * @param purchase	The details of the purchase
		 */
		public function trackGooglePurchase( purchase:Purchase ):void
		{
			if (purchase == null || purchase.productId == null)
				throw new Error( "Invalid Purchase object" );
			
			try
			{
				_extContext.call( "trackGooglePurchase", purchase );
			}
			catch (e:Error)
			{
			}
		}
		
		
		/**
		 * If you use virtual payments (e.g. purchase with in-game gold, coins, ...) in your project,
		 * you can track them with a call to trackVirtualPayment.
		 *
		 * @param currency
		 * @param amount
		 * @param itemName
		 * @param itemType
		 */
		public function trackVirtualPayment( currency:String, amount:int, itemName:String, itemType:String ):void
		{
			try
			{
				_extContext.call( "trackVirtualPayment", currency, amount, itemName, itemType );
			}
			catch (e:Error)
			{
			}
		}
		
		
		/**
		 * <p>
		 * If you want to get current segment of your player, just call getCurrentSegment and listen for the
		 * appropriate events.
		 * </p>
		 * <ul>
		 *     <li><code>ExponeaEvent.GET_CURRENT_SEGMENT_SUCCESS</code>: dispatched when the call succeeds and will contain the segment name</li>
		 *     <li><code>ExponeaEvent.GET_CURRENT_SEGMENT_ERROR</code>: dispatched when the call fails, the details of the error will be in the event</li>
		 * </ul>
		 * <p>
		 * You will need id of your segmentation and project secret token.
		 * </p>
		 *
		 * @param segmentationId
		 * @param projectSecretToken
		 */
		public function getCurrentSegment( segmentationId:String, projectSecretToken:String ):void
		{
			try
			{
				_extContext.call( "getCurrentSegment", segmentationId, projectSecretToken );
			}
			catch (e:Error)
			{
			}
		}
		
		
		/**
		 * Tracked events are stored locally and periodically "flushed" to the Exponea API automatically.
		 * If you wish to manage this yourself you can disable the automatic flushing by calling this function.
		 * You will then need to manually flush the events by calling <code>flush()</code>.
		 *
		 * @see #flush()
		 */
		public function disableAutomaticFlushing():void
		{
			try
			{
				_extContext.call( "disableAutomaticFlushing" );
			}
			catch (e:Error)
			{
			}
		}
		
		
		/**
		 * If you have disabled automatic flushing you call this function to flush
		 * the tracked events manually every time there is something to flush.
		 *
		 * @see #disableAutomaticFlushing()
		 */
		public function flush():void
		{
			try
			{
				_extContext.call( "flush" );
			}
			catch (e:Error)
			{
			}
		}
		
		
		
		
		//
		//
		//	ADDITIONAL FUNCTIONALITY
		//
		//
		
		/**
		 * <p>
		 * Get the CPU Frequency.
		 * </p>
		 * <p>
		 * The frequency may be 0 if it cannot be retrieved on the current operating system.
		 * This is true of many iOS devices. In these cases it can be useful to use a lookup
		 * table to determine the cpu frequency from the device model.
		 * </p>
		 *
		 * @return the CPU Frequency
		 */
		public function getCPUFrequency():Number
		{
			try
			{
				return _extContext.call( "getCPUFrequency" ) as Number;
			}
			catch (e:Error)
			{
			}
			return 0;
		}
		
		
		/**
		 * The number of processor cores.
		 *
		 * @return	number of processor cores
		 */
		public function getNumberOfCores():Number
		{
			try
			{
				return _extContext.call( "getNumberOfCores" ) as Number;
			}
			catch (e:Error)
			{
			}
			return 0;
		}
		
		
		/**
		 * The total amount of memory on the device
		 *
		 * @return total amount of memory
		 */
		public function getTotalRAM():Number
		{
			try
			{
				return _extContext.call( "getTotalRAM" ) as Number;
			}
			catch (e:Error)
			{
			}
			return 0;
		}
		
		
		
		
		////////////////////////////////////////////////////////
		//	INTERNALS
		//
		
		
		
		////////////////////////////////////////////////////////
		//	EVENT HANDLERS
		//
		
		private function extension_statusHandler( event:StatusEvent ):void
		{
			var data:Object;
			try
			{
				switch (event.code)
				{
					case "extension:error":
						dispatchEvent( new ErrorEvent( ErrorEvent.ERROR, false, false, event.level ));
						break;
					
					case ExponeaEvent.GET_CURRENT_SEGMENT_SUCCESS:
					case ExponeaEvent.GET_CURRENT_SEGMENT_ERROR:
					{
						data = JSON.parse( event.level );
						
						var e:ExponeaEvent = new ExponeaEvent( event.code );
						if (data.hasOwnProperty( "segment" )) e.segment = data.segment;
						if (data.hasOwnProperty( "errorCode" )) e.errorCode = data.errorCode;
						if (data.hasOwnProperty( "message" )) e.message = data.message;
						
						dispatchEvent( e );
						break;
					}
				}
			}
			catch (e:Error)
			{
			}
		}
		
		
	}
	
}
