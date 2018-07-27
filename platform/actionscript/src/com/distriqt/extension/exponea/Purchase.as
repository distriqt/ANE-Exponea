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
package com.distriqt.extension.exponea 
{

    public class Purchase
    {
 		////////////////////////////////////////////////////////
        //  CONSTANTS
        //
        
        private static const TAG : String = "Purchase";
        
        
 		////////////////////////////////////////////////////////
        //  VARIABLES
        //
	
		/**
		 * The product identifier
		 */
		public var productId:String;
	
		/**
		 * The time of purchase
		 */
		public var purchaseTime:Number;
	
		/**
		 * The purchase token
		 */
		public var purchaseToken:String;
	
	
	
		////////////////////////////////////////////////////////
        //  FUNCTIONALITY
        //
        
        public function Purchase() 
        {
			purchaseTime = new Date().time;
        }
        
		
		public function setProductId( productId:String ):Purchase
		{
			this.productId = productId;
			return this;
		}
	
	
		public function setPurchaseTime( purchaseTime:Number ):Purchase
		{
			this.purchaseTime = purchaseTime;
			return this;
		}
	
	
		public function setPurchaseToken( purchaseToken:String ):Purchase
		{
			this.purchaseToken = purchaseToken;
			return this;
		}
		
		
    }
}
