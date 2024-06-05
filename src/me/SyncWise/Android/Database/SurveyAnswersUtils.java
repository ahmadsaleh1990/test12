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
 * Utilities class for generated GreenDao Entities, holding helper methods for additional functionality related to the {@link me.SyncWise.Android.Database.SurveyAnswers SurveyAnswers} objects.
 * 
 * @author Elias
 *
 */
public class SurveyAnswersUtils {

	/**
	 * Gets the maximum number of characters allowed concerning the survey question answer.
	 * 
	 * @return	Integer holding the maximum number of allowed characters.
	 */
	public static int getAnswerMaxLength () {
		return 200;
	}
	
	/**
	 * Gets the survey answer id using today's date.
	 * 
	 * @return	Long holding the survey answer id.
	 * @see #getSurveyAnswerID(Calendar)
	 */
	public static long getSurveyAnswerID () {
		// Return the survey answer id using today's date
		return getSurveyAnswerID ( Calendar.getInstance () );
	}
	
	/**
	 * Gets the survey answer id using the provided calendar object.<br>
	 * The survey answer id is in the following format : YYYYMMDDHHMMSSSSS
	 * 
	 * @param calendar	Calendar object used to compute the survey answer id.
	 * @return	Long holding the survey answer id.
	 * @see #getSurveyAnswerID()
	 */
	public static long getSurveyAnswerID ( final Calendar calendar ) {
		// Declare and initialize a two digits formatter
    	DecimalFormat twoDigits = new DecimalFormat ( "00" );
		// Declare and initialize a 3 digits formatter
    	DecimalFormat threeDigits = new DecimalFormat ( "000" );
    	// Compute the survey answer id in the following format : YYYYMMDDHHMMSSSSS
    	StringBuilder surveyanswerId = new StringBuilder ();
    	surveyanswerId.append ( calendar.get ( Calendar.YEAR ) ); // YYYY
    	surveyanswerId.append ( twoDigits.format ( 1 + calendar.get ( Calendar.MONTH ) ) ); // MM
    	surveyanswerId.append ( twoDigits.format ( calendar.get ( Calendar.DATE ) ) ); // DD
    	surveyanswerId.append ( twoDigits.format ( calendar.get ( Calendar.HOUR_OF_DAY ) ) ); // HH
    	surveyanswerId.append ( twoDigits.format ( calendar.get ( Calendar.MINUTE ) ) ); // MM
    	surveyanswerId.append ( twoDigits.format ( calendar.get ( Calendar.SECOND ) ) ); // SS
    	surveyanswerId.append ( threeDigits.format ( calendar.get ( Calendar.MILLISECOND ) ) ); // SSS
    	// Return the survey answer id
    	return Long.parseLong ( surveyanswerId.toString () );
	}
	
}