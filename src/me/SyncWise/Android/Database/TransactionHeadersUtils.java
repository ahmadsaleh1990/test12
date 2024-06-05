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
 * Utilities class for generated GreenDao Entities, holding helper methods for additional functionality related to the {@link me.SyncWise.Android.Database.TransactionHeaders TransactionHeaders} objects.
 * 
 * @author Elias
 *
 */
public class TransactionHeadersUtils {

	/**
	 * Helper class used to define the various values of a transaction header type.
	 * 
	 * @author Elias
	 *
	 */
	public static class Type {
		
		/**
		 * Integer holding the sales order transaction header type.
		 */
		public static final int SALES_ORDER = 1;
		
		/**
		 * Integer holding the sales invoice transaction header type.
		 */
		public static final int SALES_INVOICE = 2;
		
		/**
		 * Integer holding the sales return transaction header type.
		 */
		public static final int SALES_RETURN = 3;
		
		/**
		 * Integer holding the sales invoice transaction header type.
		 */
		public static final int SALES_RFR = 4;
		public static final int COLLECTION = 5;
	 

		
		/**
		 * Integer holding the return transaction header type.
		 */
		public static final int RETURN = 8;
		public static final int Movement = 14;
		
	}
	
	/**
	 * Gets the maximum number of characters allowed concerning the sales order note.
	 * 
	 * @return	Integer holding the maximum number of allowed characters.
	 */
	public static int getFreeRemarksMaxLength () {
		return 100;
	}
	
	/**
	 * Gets the maximum number of characters allowed concerning the client reference number.
	 * 
	 * @return	Integer holding the maximum number of allowed characters.
	 */
	public static int getClientReferenceNumberMaxLength () {
		return 30;
	}
	
}