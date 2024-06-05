/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Database;

/**
 * Utilities class for generated GreenDao Entities, holding helper methods for additional functionality related to the {@link me.SyncWise.Android.Database.ClientStockCountHeadersUtils ClientStockCountHeadersUtils} objects.
 * 
 * @author Elias
 *
 */
public class ClientStockCountHeadersUtils {
	
	/**
	 * String hosting the flag's default value which is used to determine if the expiry date is required in the client stock count.
	 */
	public static final String DEFAULT_ENFORCE_STOCK_EXPIRY_DATE = "N";

	/**
	 * Helper class used to define the various values of a count type.
	 * 
	 * @author Elias
	 *
	 */
	public static class CountType {
		
		/**
		 * Integer holding the shelf count type.
		 */
		public static final int SHELF = 1;
		
		/**
		 * Integer holding the stock count type
		 */
		public static final int STOCK = 2;
		
	}
	
}