/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */


package me.SyncWise.Android.Utilities;

/**
 * Utility class that provides helper methods for Collections.
 * 
 * @author Elias
 *
 */
public class Collection {

	public static final String DEFAULT_ALLOW_COLLECTION_REPRINT = "Y";
	
	public static final String DEFAULT_APPLY_DEFAULT_CURRENCY = "USD";
	
	public static final String DEFAULT_ALLOW_CASH_CHECK = "N";
	
	public static final String DEFAULT_ALLOW_UNDUE_CHECK = "N";

	public static final String DEFAULT_PRINT_COLLECTION_STAMP = "N";
	
	public static final String DEFAULT_USE_PREDEFINED_COLLECTION_STAMP = "Y";
	
	/**
	 * Gets the maximum number of characters allowed concerning the collection note.
	 * 
	 * @return	Integer holding the maximum number of allowed characters.
	 */
	public static int getFreeRemarksMaxLength () {
		return 200;
	}
	
	/**
	 * Gets the maximum number of characters allowed concerning the check code.
	 * 
	 * @return	Integer holding the maximum number of allowed characters.
	 */
	public static int getCheckCodeMaxLength () {
		return 20;
	}
	
}