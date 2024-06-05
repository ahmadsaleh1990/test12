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

public class ClientPicturesUtils {
	
	public static class Type {
		
		/**
		 * Integer holding the profile picture.
		 */
		public static final int IS_PROFILED = 1;
		/**
		 * Integer holding the profile picture.
		 */
		public static final int IS_NOT_PROFILED = 0;
		
	}

	/**
	 * String holding the folder of pictures.
	 */
	public static final String FOLDER = "CLIENT_PICTURES";
	
	/**
	 * Gets the picture id using the provided calendar object.<br>
	 * The picture id is in the following format : YYYYMMDDHHMMSSSSS
	 * 
	 * @param calendar	Calendar object used to compute the picture id.
	 * @return	Long holding the picture id.
	 */
	public static long getPictureID ( final Calendar calendar ) {
		// Declare and initialize a two digits formatter
    	DecimalFormat twoDigits = new DecimalFormat ( "00" );
		// Declare and initialize a 3 digits formatter
    	DecimalFormat threeDigits = new DecimalFormat ( "000" );
    	// Compute the picture id in the following format : YYYYMMDDHHMMSSSSS
    	StringBuilder pictureId = new StringBuilder ();
    	pictureId.append ( calendar.get ( Calendar.YEAR ) ); // YYYY
    	pictureId.append ( twoDigits.format ( 1 + calendar.get ( Calendar.MONTH ) ) ); // MM
    	pictureId.append ( twoDigits.format ( calendar.get ( Calendar.DATE ) ) ); // DD
    	pictureId.append ( twoDigits.format ( calendar.get ( Calendar.HOUR_OF_DAY ) ) ); // HH
    	pictureId.append ( twoDigits.format ( calendar.get ( Calendar.MINUTE ) ) ); // MM
    	pictureId.append ( twoDigits.format ( calendar.get ( Calendar.SECOND ) ) ); // SS
    	pictureId.append ( threeDigits.format ( calendar.get ( Calendar.MILLISECOND ) ) ); // SSS
    	// Return the picture id
    	return Long.parseLong ( pictureId.toString () );
	}
	
}