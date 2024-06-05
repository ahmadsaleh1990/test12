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
 * Utilities class for generated GreenDao Entities, holding helper methods for additional functionality related to the {@link me.SyncWise.Android.Database.TransactionDetails TransactionDetails} objects.
 * 
 * @author Elias
 *
 */
public class TransactionDetailsUtils {

	/**
	 * Helper class used to define the various values of a transaction detail type.
	 * 
	 * @author Elias
	 *
	 */
	public static class Type {
		
		/**
		 * String holding the in sales order transaction detail type.
		 */
		public static final String ORDER = "O";
		
		/**
		 * String holding the in free transaction detail type.
		 */
		public static final String FREE = "F";
		
	}
	
}