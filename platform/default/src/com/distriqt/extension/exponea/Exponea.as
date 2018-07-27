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
	import flash.events.ErrorEvent;
	import flash.events.Event;
	import flash.events.EventDispatcher;
	
	
	
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
		private static const ERROR_SINGLETON		: String = "The extension has already been created. Use ExtensionClass.service to access the functionality of the class";
		
		
		
		////////////////////////////////////////////////////////
		//	VARIABLES
		//
		
		//
		// Singleton variables
		private static var _instance				: Exponea;
		private static var _shouldCreateInstance	: Boolean = false;
		
		protected var _extensionId			: String = "";
		protected var _extensionIdNumber	: int = -1;
		
		
		////////////////////////////////////////////////////////
		//	SINGLETON INSTANCE
		//
		
		public static function get instance():Exponea
		{
			createInstance();
			return _instance;
		}
		
		
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
		
		public function Exponea()
		{
			if (_shouldCreateInstance)
			{
				_extensionId = EXT_CONTEXT_ID;
				_extensionIdNumber = EXT_ID_NUMBER;
			}
			else
			{
				throw new Error( ERROR_SINGLETON );
			}
		}
		
		
		public function dispose():void
		{
			_instance = null;
		}
		
		
		public static function get isSupported():Boolean
		{
			return false;
		}
		
		
		public function get version():String
		{
			return VERSION;
		}
		
		
		public function get nativeVersion():String
		{
			return VERSION_DEFAULT;
		}
		
		public function get implementation():String
		{
			return "default";
		}
		
		
		
		
		
		//
		//
		//	EXTENSION FUNCTIONALITY
		//
		//
		
		
		public function initialise( token:String, url:String="https://api.exponea.com" ):void
		{
		}
		
		
		public function identify( customerId:String ):void
		{
		}
		
		
		public function update( properties:Object ):void
		{
		}
		
		
		public function track( type:String, properties:Object=null, timestamp:Number=-1 ):void
		{
		}
		
		
		public function trackSessionStart():void
		{
		}
		
		
		public function trackSessionEnd():void
		{
		}
		
		
		public function trackGooglePurchase( purchase:Purchase ):void
		{
		}
		
		
		public function trackVirtualPayment( currency:String, amount:int, itemName:String, itemType:String ):void
		{
		}
		
		
		public function getCurrentSegment( segmentationId:String, projectSecretToken:String ):void
		{
		}
		
		
		public function disableAutomaticFlushing():void
		{
		}
		
		
		public function flush():void
		{
		}
		
		
		
		
		public function getCPUFrequency():Number
		{
			return 0;
		}
		
		public function getNumberOfCores():Number
		{
			return 0;
		}
		
		public function getTotalRAM():Number
		{
			return 0;
		}
		
		
		
		////////////////////////////////////////////////////////
		//	INTERNALS
		//
		
		
		
		////////////////////////////////////////////////////////
		//	EVENT HANDLERS
		//
		
	}
}
