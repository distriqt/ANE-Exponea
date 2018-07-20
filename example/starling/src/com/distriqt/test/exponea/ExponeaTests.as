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
 * @author 		"Michael Archbold (ma&#64;distriqt.com)"
 * @created		08/01/2016
 * @copyright	http://distriqt.com/copyright/license.txt
 */
package com.distriqt.test.exponea
{
	import com.distriqt.extension.exponea.Exponea;
	import com.distriqt.extension.exponea.Purchase;
	import com.distriqt.extension.exponea.events.ExponeaEvent;
	
	import flash.display.Bitmap;
	import flash.filesystem.File;
	import flash.geom.Rectangle;
	import flash.net.URLRequest;
	import flash.net.navigateToURL;
	import flash.utils.setTimeout;
	
	import starling.core.Starling;
	
	import starling.display.Image;
	import starling.display.Sprite;
	
	/**	
	 */
	public class ExponeaTests extends Sprite
	{
		public static const TAG : String = "";
		
		private var _l : ILogger;
		
		private function log( log:String ):void
		{
			_l.log( TAG, log );
		}
		
		
		
		////////////////////////////////////////////////////////
		//	FUNCTIONALITY
		//
		
		public function ExponeaTests( logger:ILogger )
		{
			_l = logger;
			try
			{
				log( "Exponea Supported: " + Exponea.isSupported );
				if (Exponea.isSupported)
				{
					log( "Exponea Version:   " + Exponea.instance.version );
				
					log( "getCPUFrequency:  " + Exponea.instance.getCPUFrequency() );
					log( "getNumberOfCores: " + Exponea.instance.getNumberOfCores() );
					log( "getTotalRAM:      " + Exponea.instance.getTotalRAM() );
				}
			}
			catch (e:Error)
			{
				trace( e );
			}
		}
		
		
		////////////////////////////////////////////////////////
		//  
		//
		
		public function initialise():void
		{
			log( "initialise()" );
			
			var token:String = "AABBCCDDEEFFF";
			var url:String = "https://api.exponea.com";
			
			Exponea.instance.initialise( token, url );
		}
		
		
		public function identify():void
		{
			log( "identify()" );
			
			var customerId:String = "test@test.com";
			
			Exponea.instance.identify( customerId );
		}
		
		public function update():void
		{
			log( "update()" );
			
			var properties:Object = {
				age: 34
			};
			
			Exponea.instance.update( properties );
		}
		
		public function track():void
		{
			log( "track()" );
			
			var type:String = "item_bought";
			var properties:Object = {
				item_id: 45
			};
			var timestamp:Number = (new Date()).time;
			
			Exponea.instance.track(
					type,
					properties,
					timestamp
			);
		}
		
		public function sessionStart():void
		{
			log( "sessionStart()" );
			Exponea.instance.trackSessionStart();
		}
		
		public function sessionEnd():void
		{
			log( "sessionEnd()" );
			Exponea.instance.trackSessionEnd();
		}
		
		
		//
		//	PURCHASES
		//
		
		
		public function trackGooglePurchase():void
		{
			log( "trackGooglePurchase()" );
			var purchase:Purchase = new Purchase()
					.setProductId( "com.distriqt.test.awesomeproduct1" )
					.setPurchaseTime( new Date().time )
					.setPurchaseToken( "XXXXXXAAAAAA" );
			
			Exponea.instance.trackGooglePurchase( purchase );
		}
		
		public function trackVirtualPayment():void
		{
			log( "trackVirtualPayment()" );
			
			var currency:String = "USD";
			var amount:int = 100;
			var itemName:String = "awesome product";
			var itemType:String = "something";
			
			Exponea.instance.trackVirtualPayment( currency, amount, itemName, itemType );
		}
		
		
		//
		//	SEGMENT
		//
		
		public function getCurrentSegment():void
		{
			log( "getCurrentSegment" );
		
			Exponea.instance.addEventListener( ExponeaEvent.GET_CURRENT_SEGMENT_SUCCESS, getCurrentSegmentHandler );
			Exponea.instance.addEventListener( ExponeaEvent.GET_CURRENT_SEGMENT_ERROR, getCurrentSegmentHandler );
			
			var segmentationId:String = "some_id";
			var projectSecretToken:String = "secret_token";
			
			Exponea.instance.getCurrentSegment( segmentationId, projectSecretToken );
		}
		
		private function getCurrentSegmentHandler( event:ExponeaEvent ):void
		{
			log( "getCurrentSegmentHandler: " + event.type );
			log( "   segment: " + event.segment );
			log( "   error: " + event.message);
		
			Exponea.instance.removeEventListener( ExponeaEvent.GET_CURRENT_SEGMENT_SUCCESS, getCurrentSegmentHandler );
			Exponea.instance.removeEventListener( ExponeaEvent.GET_CURRENT_SEGMENT_ERROR, getCurrentSegmentHandler );
		}
		
		
		
		//
		//	FLUSHING
		//
		
		public function disableAutomaticFlushing():void
		{
			log( "disableAutomaticFlushing" );
			Exponea.instance.disableAutomaticFlushing();
		}
		
		public function flush():void
		{
			log( "flush" );
			Exponea.instance.flush();
		}
		
		
		
		
	}
}
