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
 * @author 		marchbold
 * @created		20/7/18
 * @copyright	http://distriqt.com/copyright/license.txt
 */
package com.distriqt.extension.exponea.events 
{
	import com.distriqt.extension.exponea.events.ExponeaEvent;
	
	import flash.events.Event;
	
	
	public class ExponeaEvent extends Event
    {
 		////////////////////////////////////////////////////////
        //  CONSTANTS
        //
        
        private static const TAG : String = "ExponeaEvent";
	
		/**
		 * Dispatched when the <code>getCurrentSegment</code> call succeeds and will contain the segment name
		 *
		 * @eventType get:current:segment:success
		 */
		public static const GET_CURRENT_SEGMENT_SUCCESS : String = "get:current:segment:success";
	
		/**
		 * Dispatched when the <code>getCurrentSegment</code> call fails,
		 * the details of the error will be in the event
		 *
		 * @eventType get:current:segment:error
		 */
		public static const GET_CURRENT_SEGMENT_ERROR : String = "get:current:segment:error";

		
        
 		////////////////////////////////////////////////////////
        //  VARIABLES
        //
	
		/**
		 * The segment name
		 */
		public var segment : String;
	
		/**
		 * An error message if an error occurred
		 */
		public var message : String;
	
		/**
		 *
		 */
		public var errorCode : int = -1;
		
		
 		////////////////////////////////////////////////////////
        //  FUNCTIONALITY
        //
	
	
		public function ExponeaEvent( type:String, bubbles:Boolean =false, cancelable:Boolean =false )
		{
			super( type, bubbles, cancelable );
		}
	
	
		override public function clone():Event
		{
			var e:ExponeaEvent = new ExponeaEvent( type, bubbles, cancelable );
			e.segment = segment;
			e.errorCode = errorCode;
			e.message = message;
			return e;
		}
	
		
	}
}
