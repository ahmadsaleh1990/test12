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
 * Utilities class for generated GreenDao Entities, holding helper methods for additional functionality related to the {@link me.SyncWise.Android.Database.Reasons Reasons} objects.
 * 
 * @author Elias
 *
 */
public class ReasonsUtils {

	/**
	 * Helper class used to define the various values of a reason type.
	 * 
	 * @author Elias
	 *
	 */
	public static class Type {
		
		/**
		 * String holding the missed journey reasons type
		 */
		public static final String MISSED_JOURNEY = "J";
		
		/**
		 * String holding the cancel visit reasons type
		 */
		public static final String CANCEL_VISIT = "C";
		
		/**
		 * String holding the no sales reasons type
		 */
		public static final String NO_SALES = "NS";
		
		/**
		 * String holding the no client objective reasons type
		 */
		public static final String NO_CLIENT_OBJECTIVE = "NCO";
		
		/**
		 * String holding the no client survey reasons type
		 */
		public static final String NO_CLIENT_SURVEY = "NCS";
		
		/**
		 * String holding the cancel client objective reasons type
		 */
		public static final String CANCEL_CLIENT_OBJECTIVE = "OC";

		/**
		 * String holding the item status reasons type
		 */
		public static final String ITEM_STATUS = "IS";
		
		/**
		 * String holding the no client barcode reasons type
		 */
		public static final String NO_CLIENT_BARCODE = "B";
		
		public static final String MSL = "MSL";
		public static final String NEWSKU = "NSKU";
	}
	
	/**
	 * Gets the maximum number of characters allowed concerning the reason note.
	 * 
	 * @return	Integer holding the maximum number of allowed characters.
	 */
	public static int getNoteMaxLength () {
		return 500;
	}
	
	/**
	 * Indicates if the provided reason affected stock code represents a good or damaged affected stock.
	 * 
	 * @param reasonAffectedStock	A string hosting the affect stock code.
	 * @return	Boolean indicating if the stock is affected negatively.
	 */
	public static boolean isDamage ( final String reasonAffectedStock ) {
		// Check if the affected stock is valid and if the affected value is D
		if ( reasonAffectedStock != null && reasonAffectedStock.equalsIgnoreCase ( "d" ) )
			// Damaged reason
			return true;
		// Otherwise good reason
		return false;
	}

	/**
	 * Indicates if the provided reason affected stock code represents a good or damaged affected stock.
	 * 
	 * @param reasonAffectedStock	A string hosting the affect stock code.
	 * @return	Boolean indicating if the stock is affected negatively.
	 */
	public static boolean isDamages ( final String reasonAffectedStock ) {
		// Check if the affected stock is valid and if the affected value is D
		if ( reasonAffectedStock != null && reasonAffectedStock.equalsIgnoreCase ( "g" ) )
			// Damaged reason
			return false;
		else	if ( reasonAffectedStock == null || reasonAffectedStock.equalsIgnoreCase ( "d" ) )
		// Otherwise good reason
		return true;
		 return false;
	}
	
	/**
	 * Indicates if the provided reason represents a good or damaged affected stock.
	 * 
	 * @param reason	A {@link me.SyncWise.Android.Database.Reasons Reasons} object used to affect stock.
	 * @return	Boolean indicating if the stock is affected negatively.
	 */
	public static boolean isDamage ( final Reasons reason ) {
		// Check if the reason is valid and if the affected stock is valid and if the affected value is D
		if ( reason != null && isDamage ( reason.getReasonAffectStock () ) )
			// Damaged reason
			return true;
		// Otherwise good reason
		return false;
	}
	
}