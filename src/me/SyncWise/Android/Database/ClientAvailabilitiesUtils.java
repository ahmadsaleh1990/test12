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
 * Utilities class for generated GreenDao Entities, holding helper methods for additional functionality related to the {@link me.SyncWise.Android.Database.ClientAvailabilities ClientAvailabilities} objects.
 * 
 * @author Elias
 *
 */
public class ClientAvailabilitiesUtils {

	/**
	 * Gets the availability id using today's date.
	 * 
	 * @return	Long holding the availability id.
	 * @see #getAvailabilityID(Calendar)
	 */
	public static long getAvailabilityID () {
		// Return the availability id using today's date
		return getAvailabilityID ( Calendar.getInstance () );
	}
	
	/**
	 * Gets the availability id using the provided calendar object.<br>
	 * The availability id is in the following format : YYYYMMDDHHMMSSSSS
	 * 
	 * @param calendar	Calendar object used to compute the availability id.
	 * @return	Long holding the availability id.
	 * @see #getAvailabilityID()
	 */
	public static long getAvailabilityID ( final Calendar calendar ) {
		// Declare and initialize a two digits formatter
    	DecimalFormat twoDigits = new DecimalFormat ( "00" );
		// Declare and initialize a 3 digits formatter
    	DecimalFormat threeDigits = new DecimalFormat ( "000" );
    	// Compute the availability id in the following format : YYYYMMDDHHMMSSSSS
    	StringBuilder availabilityId = new StringBuilder ();
    	availabilityId.append ( calendar.get ( Calendar.YEAR ) ); // YYYY
    	availabilityId.append ( twoDigits.format ( 1 + calendar.get ( Calendar.MONTH ) ) ); // MM
    	availabilityId.append ( twoDigits.format ( calendar.get ( Calendar.DATE ) ) ); // DD
    	availabilityId.append ( twoDigits.format ( calendar.get ( Calendar.HOUR_OF_DAY ) ) ); // HH
    	availabilityId.append ( twoDigits.format ( calendar.get ( Calendar.MINUTE ) ) ); // MM
    	availabilityId.append ( twoDigits.format ( calendar.get ( Calendar.SECOND ) ) ); // SS
    	availabilityId.append ( threeDigits.format ( calendar.get ( Calendar.MILLISECOND ) ) ); // SSS
    	// Return the availability id
    	return Long.parseLong ( availabilityId.toString () );
	}
	
}