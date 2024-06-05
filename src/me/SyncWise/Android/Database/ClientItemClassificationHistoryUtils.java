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

import java.text.DecimalFormat;
import java.util.Calendar;

/**
 * Utilities class for generated GreenDao Entities, holding helper methods for additional functionality related to the {@link me.SyncWise.Android.Database.ClientItemClassificationHistory ClientItemClassificationHistory} objects.
 * 
 * @author Elias
 *
 */
public class ClientItemClassificationHistoryUtils {

	/**
	 * Gets the maximum number of characters allowed concerning the client item classification remarks.
	 * 
	 * @return	Integer holding the maximum number of allowed characters.
	 */
	public static int getRemarksMaxLength () {
		return 200;
	}
	
	/**
	 * Gets the client item classification id using today's date.
	 * 
	 * @return	Long holding the client item classification id.
	 * @see #getClientItemClassificationID(Calendar)
	 */
	public static long getClientItemClassificationID () {
		// Return the client item classification id using today's date
		return getClientItemClassificationID ( Calendar.getInstance () );
	}
	
	/**
	 * Gets the client item classification id using the provided calendar object.<br>
	 * The client item classification id is in the following format : YYYYMMDDHHMMSSSSS
	 * 
	 * @param calendar	Calendar object used to compute the client item classification id.
	 * @return	Long holding the client item classification id.
	 * @see #getClientItemClassificationID()
	 */
	public static long getClientItemClassificationID ( final Calendar calendar ) {
		// Declare and initialize a two digits formatter
    	DecimalFormat twoDigits = new DecimalFormat ( "00" );
		// Declare and initialize a 3 digits formatter
    	DecimalFormat threeDigits = new DecimalFormat ( "000" );
    	// Compute the client item classification id in the following format : YYYYMMDDHHMMSSSSS
    	StringBuilder classificationId = new StringBuilder ();
    	classificationId.append ( calendar.get ( Calendar.YEAR ) ); // YYYY
    	classificationId.append ( twoDigits.format ( 1 + calendar.get ( Calendar.MONTH ) ) ); // MM
    	classificationId.append ( twoDigits.format ( calendar.get ( Calendar.DATE ) ) ); // DD
    	classificationId.append ( twoDigits.format ( calendar.get ( Calendar.HOUR_OF_DAY ) ) ); // HH
    	classificationId.append ( twoDigits.format ( calendar.get ( Calendar.MINUTE ) ) ); // MM
    	classificationId.append ( twoDigits.format ( calendar.get ( Calendar.SECOND ) ) ); // SS
    	classificationId.append ( threeDigits.format ( calendar.get ( Calendar.MILLISECOND ) ) ); // SSS
    	// Return the client item classification id
    	return Long.parseLong ( classificationId.toString () );
	}
	
}