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
 * Utilities class for generated GreenDao Entities, holding helper methods for additional functionality related to the {@link me.SyncWise.Android.Database.Objectives Objectives} objects.
 * 
 * @author Elias
 *
 */
public class ObjectivesUtils {

	/**
	 * Helper class used to define the various values of an objective type.
	 * 
	 * @author Elias
	 *
	 */
	public static class Type {
		
		/**
		 * Integer holding the user objective type.
		 */
		public static final int USER = 1;
		
		/**
		 * Integer holding the client objective type.
		 */
		public static final int CLIENT_OBJECTIVE = 2;
		
		/**
		 * Integer holding the client focus type.
		 */
		public static final int CLIENT_FOCUS = 3;
		
	}
	
	/**
	 * Helper class used to define the various values of an objective source.
	 * 
	 * @author Elias
	 *
	 */
	public static class Source {
		
		/**
		 * Integer holding the web objective source.
		 */
		public static final int WEB = 1;
		
		/**
		 * Integer holding the device objective source.
		 */
		public static final int DEVICE = 2;
		
	}
	
	/**
	 * Gets the maximum number of characters allowed concerning the objective description.
	 * 
	 * @return	Integer holding the maximum number of allowed characters.
	 */
	public static int getDescriptionMaxLength () {
		return 500;
	}
	
	/**
	 * Gets the objective id using today's date.
	 * 
	 * @return	Long holding the objective id.
	 * @see #getObjectiveID(Calendar)
	 */
	public static long getObjectiveID () {
		// Return the objective id using today's date
		return getObjectiveID ( Calendar.getInstance () );
	}
	
	/**
	 * Gets the objective id using the provided calendar object.<br>
	 * The objective id is in the following format : YYYYMMDDHHMMSSSSS
	 * 
	 * @param calendar	Calendar object used to compute the objective id.
	 * @return	Long holding the objective id.
	 * @see #getObjectiveID()
	 */
	public static long getObjectiveID ( final Calendar calendar ) {
		// Declare and initialize a two digits formatter
    	DecimalFormat twoDigits = new DecimalFormat ( "00" );
		// Declare and initialize a 3 digits formatter
    	DecimalFormat threeDigits = new DecimalFormat ( "000" );
    	// Compute the objective id in the following format : YYYYMMDDHHMMSSSSS
    	StringBuilder objectiveId = new StringBuilder ();
    	objectiveId.append ( calendar.get ( Calendar.YEAR ) ); // YYYY
    	objectiveId.append ( twoDigits.format ( 1 + calendar.get ( Calendar.MONTH ) ) ); // MM
    	objectiveId.append ( twoDigits.format ( calendar.get ( Calendar.DATE ) ) ); // DD
    	objectiveId.append ( twoDigits.format ( calendar.get ( Calendar.HOUR_OF_DAY ) ) ); // HH
    	objectiveId.append ( twoDigits.format ( calendar.get ( Calendar.MINUTE ) ) ); // MM
    	objectiveId.append ( twoDigits.format ( calendar.get ( Calendar.SECOND ) ) ); // SS
    	objectiveId.append ( threeDigits.format ( calendar.get ( Calendar.MILLISECOND ) ) ); // SSS
    	// Return the objective id
    	return Long.parseLong ( objectiveId.toString () );
	}
	
}