/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Database;

/**
 * Utilities class for generated GreenDao Entities, holding helper methods for additional functionality related to the {@link me.SyncWise.Android.Database.PromotionHeaders PromotionHeaders} and {@link me.SyncWise.Android.Database.PromotionDetails PromotionDetails} objects.
 * 
 * @author Elias
 *
 */
public class PromotionUtils {
	
	/**
	 * Helper class used to define the various values of a promotion type.
	 * 
	 * @author Elias
	 *
	 */
	public static class Type {
		
		/**
		 * Integer holding the instant promotion type
		 */
		public static final int INSTANT_PROMOTION = 1;
		
		/**
		 * Integer holding the basket promotion type
		 */
		public static final int BASKET_PROMOTION = 2;
		
		/**
		 * Integer holding the structural promotion type
		 */
		public static final int STRUCTURAL_PROMOTION = 3;
		
		public static final int LIMITED_PROMOTION = 4;
		public static final int PRICELIST_PROMOTION = 5;
	}
	
	/**
	 * Helper class used to define the various values of a promotion detail type.
	 * 
	 * @author Elias
	 *
	 */
	public static class DetailType {

		/**
		 * Integer holding the offered quantity detail type
		 */
		public static final int OFFERED_QUANTITY = 1;
		
		/**
		 * Integer holding the discount percentage detail type
		 */
		public static final int DISCOUNT_PERCENTAGE = 3;
		
	}
	
	public static class Instant {
		//Ordered Quantity VS Offered Quantity
		public static final int OQ_OQ = 1;
		//Ordered Quantity VS Discount Amount
		public static final int OQ_DA = 2;
		//Ordered Quantity VS Discount Percentage
		public static final int OQ_DP = 3;
		//Total Amount VS Offered Quantity
		public static final int TA_OQ = 4;
		//Total Amount VS Discount Amount
		public static final int TA_DA = 5;
		//Total Amount VS Discount Percentage
		public static final int TA_DP = 6;
	}
	
	public static class Basket {
		//Ordered Quantity VS Offered Quantity
		public static final int OQ_OQ = 1;
		//Ordered Quantity VS Discount Amount
		public static final int OQ_DA = 2;
		//Ordered Quantity VS Discount Percentage
		public static final int OQ_DP = 3;
	}
	
	public static class Structural {
		//Discount Percentage
		public static final int DP = 1;
		//Total AMount VS Discount Amount
		public static final int TA_DA = 2;
		//Total Amount VS Discount Percentage
		public static final int TA_DP = 3;
		//Ordered Quantity VS Discount Amount
		public static final int OQ_DA = 4;
		//Ordered Quantity Vs Discount Percentage
		public static final int OQ_DP = 5;
	}

}