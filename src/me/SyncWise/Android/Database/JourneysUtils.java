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

import android.text.TextUtils;

/**
 * Utilities class for generated GreenDao Entities, holding helper methods for additional functionality related to the {@link me.SyncWise.Android.Database.Journeys Journeys} objects.
 * 
 * @author Elias
 *
 */
public class JourneysUtils {
	
	/**
	 * Gets the journey code using the user prefix ID and today's date.
	 * 
	 * @param prefixID	Integer holding the user prefix ID.
	 * @return	String holding the journey code.
	 * @see #getJourneyCode(int, Calendar)
	 */
	public static String getJourneyCode ( final int prefixID ) {
		// Return the journey code using today's date
		return getJourneyCode ( prefixID , Calendar.getInstance () );
	}
	
	/**
	 * Gets the journey code using the user prefix ID and the provided calendar object.<br>
	 * The journey code is in the following format : User Prefix ID followed by 'YYYYMMDD'
	 * 
	 * @param prefixID	Integer holding the user prefix ID.
	 * @param calendar	Calendar object used to compute the journey code.
	 * @return	String holding the journey code.
	 * @see #getJourneyCode(int)
	 */
	public static String getJourneyCode ( final int prefixID , final Calendar calendar ) {
		// Declare and initialize a two digits formatter
    	DecimalFormat twoDigits = new DecimalFormat ( "00" );
    	// Compute and return the journey code in the following format : Prefix ID & 'YYYYMMDD'
    	return String.valueOf ( prefixID ) + // Prefix ID
    			String.valueOf ( calendar.get ( Calendar.YEAR ) ) // YYYY
    			+ twoDigits.format ( calendar.get ( Calendar.MONTH ) + 1 ) // MM
    			+ twoDigits.format ( calendar.get ( Calendar.DATE ) ); // DD
	}
	
	/**
	 * Gets a calendar object associated with the provided journey code :<br>
	 * The year, month and day are extracted from the journey code itself, while the hour, minute, second and milliseconds values are set to 0.
	 * 
	 * @param journeyCode	String holding the journey code.
	 * @return	A calendar object representing the start of the journey represented by the journey code, or {@code NULL} if the journey code is invalid.
	 */
	public static Calendar getCalendar ( final String journeyCode ) {
		// Check if the journey code is valid
		if ( TextUtils.isEmpty ( journeyCode ) || journeyCode.length () <= 8 )
			// Invalid journey code
			return null;
		// Extract the year, month and day
		int year = 0;
		int month = 0;
		int day = 0;
		try {
			int length = journeyCode.length ();
			year = Integer.parseInt ( journeyCode.substring ( length - 8 , length - 4 ) );
			month = Integer.parseInt ( journeyCode.substring ( length - 4 , length - 2 ) );
			day = Integer.parseInt ( journeyCode.substring ( length - 2 , length ) );
		} catch ( Exception exception ) {
			// Invalid journey code
			return null;
		} // End of try-catch block
		// Declare and initialize a calendar using the year, month and day retrieved above
		Calendar calendar = Calendar.getInstance ();
		calendar.set ( year , month - 1 , day );
		calendar.set ( Calendar.HOUR_OF_DAY , 0 );
		calendar.set ( Calendar.MINUTE , 0 );
		calendar.set ( Calendar.SECOND , 0 );
		calendar.set ( Calendar.MILLISECOND , 0 );
		// Return the computed calendar object
		return calendar;
	}
	
}