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

import android.location.Location;

/**
 * Utilities class for generated GreenDao Entities, holding helper methods for additional functionality related to the {@link me.SyncWise.Android.Database.Visits Visits} objects.
 * 
 * @author Elias
 *
 */
public class VisitsUtils {
	
	/**
	 * Constant holding the default number of weeks that the visit history covers.
	 */
	public static final int DEFAULT_VISIT_HISTORY_WEEKS = 4;
	
	/**
	 * Constant string holding the default value used to indicate if the achievements include the out of route visits.
	 */
	public static final String DEFAULT_ACHIEVEMENTS_INCLUDE_OUTOFROUTE = "N";
	
	/**
	 * Helper class used to define the various values of the barcode field.
	 * 
	 * @author Elias
	 *
	 */
	public static class Barcode {
		
		/**
		 * String holding the 'Is Barcode' visit type.
		 */
		public static final String YES = "Y";
		
		/**
		 * String holding the 'Is NOT Barcode' visit type.
		 */
		public static final String NO = "N";
		
	}
	
	/**
	 * Helper class used to define the various values of a visit type.
	 * 
	 * @author Elias
	 *
	 */
	public static class Type {
		
		/**
		 * String holding the in route visit type.
		 */
		public static final String IN_ROUTE = "I";
		
		/**
		 * String holding the out of route visit type.
		 */
		public static final String OUT_OF_ROUTE = "O";
		
	}
	
	/**
	 * Gets the maximum number of characters allowed concerning the visit note.
	 * 
	 * @return	Integer holding the maximum number of allowed characters.
	 */
	public static int getNoteMaxLength () {
		return 500;
	}
	
	/**
	 * Gets the visit id using today's date.
	 * 
	 * @return	Long holding the visit id.
	 * @see #getVisitID(Calendar)
	 */
	public static long getVisitID () {
		// Return the visit id using today's date
		return getVisitID ( Calendar.getInstance () );
	}
	
	/**
	 * Gets the visit id using the provided calendar object.<br>
	 * The visit id is in the following format : YYYYMMDDHHMMSSSSS
	 * 
	 * @param calendar	Calendar object used to compute the visit id.
	 * @return	Long holding the visit id.
	 * @see #getVisitID()
	 */
	public static long getVisitID ( final Calendar calendar ) {
		// Declare and initialize a two digits formatter
    	DecimalFormat twoDigits = new DecimalFormat ( "00" );
		// Declare and initialize a 3 digits formatter
    	DecimalFormat threeDigits = new DecimalFormat ( "000" );
    	// Compute the visit id in the following format : YYYYMMDDHHMMSSSSS
    	StringBuilder visitId = new StringBuilder ();
    	visitId.append ( calendar.get ( Calendar.YEAR ) ); // YYYY
    	visitId.append ( twoDigits.format ( 1 + calendar.get ( Calendar.MONTH ) ) ); // MM
    	visitId.append ( twoDigits.format ( calendar.get ( Calendar.DATE ) ) ); // DD
    	visitId.append ( twoDigits.format ( calendar.get ( Calendar.HOUR_OF_DAY ) ) ); // HH
    	visitId.append ( twoDigits.format ( calendar.get ( Calendar.MINUTE ) ) ); // MM
    	visitId.append ( twoDigits.format ( calendar.get ( Calendar.SECOND ) ) ); // SS
    	visitId.append ( threeDigits.format ( calendar.get ( Calendar.MILLISECOND ) ) ); // SSS
    	// Return the visit id
    	return Long.parseLong ( visitId.toString () );
	}
	public static Location SwitchLocation ( Location LocationGPS, Location LocationNetwork){
		Location Result = null;
		if (LocationGPS!= null
				&&  LocationNetwork != null) {

			// smaller the number more accurate result will
			if (LocationGPS.getAccuracy() >  LocationNetwork
					.getAccuracy())
				Result =  LocationNetwork;
			else
				Result =  LocationGPS;

		} else {

			if (LocationGPS != null) {
				Result =   LocationGPS;
			} else if ( LocationNetwork != null) {
				Result = LocationNetwork;
			}
			  if(Result!=null){
        	        Calendar today = Calendar.getInstance ();
        	        long gps_time=Result.getTime()/1000;
        	        long current_time=today.getTimeInMillis()/1000;
        	        long diff=current_time-gps_time;
        	        if(diff>60){
        	        	Result=null;
        	        }
                }
		}
		return Result;
		
	}
}