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
 * Utilities class for generated GreenDao Entities, holding helper methods for additional functionality related to the {@link me.SyncWise.Android.Database.MovementHeaders MovementHeaders} objects.
 * 
 * @author Elias
 *
 */
public class MovementHeadersUtils {

	/**
	 * Helper class used to define the various values of a transaction header type.
	 * 
	 * @author Elias
	 *
	 */
	public static class Type {
		
		/**
		 * Integer holding the load request movement header type.
		 */
		public static final int LOAD_REQUEST = 1;
		
		/**
		 * Integer holding the load movement header type.
		 */
		public static final int LOAD = 2;
		
		/**
		 * Integer holding the unload movement header type.
		 */
		public static final int UNLOAD = 3;
		
		/**
		 * Integer holding the unload request movement header type.
		 */
		public static final int UNLOAD_REQUEST = 4;
		
		/**
		 * Integer holding the van stock count movement header type.
		 */
		public static final int VAN_STOCK_COUNT = 5;
		
		/**
		 * Integer holding the van stock count movement header type.
		 */
		public static final int MISSED_UNLOAD = 6;
		
		/**
		 * Integer holding the stock management movement header type.
		 */
		public static final int STOCK_MANAGEMENT = 7;
		
		/**
		 * Integer holding the stock reconciliation movement header type.
		 */
		public static final int STOCK_RECONCILIATION = 8;
		
		/**
		 * Integer holding the van transfer movement header type.
		 */
		public static final int VAN_TRANSFER = 9;
		
		/**
		 * Integer holding the Physical Direct Load movement header type.
		 */ 
		public static final int Physical_Direct_Load = 10;
		/**
		 * Integer holding the PHYSICAL DIRECT UNLOAD type.
		 */ 
		public static final int PHYSICAL_DIRECT_UNLOAD= 11; 
		
	}
	
}