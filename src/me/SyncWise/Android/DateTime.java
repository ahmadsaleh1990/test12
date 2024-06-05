/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;

/**
 * Helper Class used to provide date and time utilities.
 * 
 * @author Elias
 *
 */
public class DateTime {

	/**
	 * Constant integer holding the starting working hour.
	 */
	public static final int WORKING_HOUR_START = 8;
	
	/**
	 * Constant integer holding the ending working hour.
	 */
	public static final int WORKING_HOUR_END = 18;
	
	/**
	 * Returns the string representation of the day, retrieved from the provided calendar.
	 * 
	 * @param context	The application context.
	 * @param calendar	The calendar object holding the required day.
	 * @return	String holding the day label.
	 * @see #getDayAbbreviation(Context, Calendar)
	 * @see #getMonth(Context, Calendar)
	 */
	public static String getDay ( final Context context , final Calendar calendar ) {
		// Determine if the content and/or the calendar objects are valid
		if ( context == null || calendar == null )
			// Invalid arguments
			return null;
		// Compute and return the corresponding day label
		switch ( calendar.get ( Calendar.DAY_OF_WEEK ) ) {
		case Calendar.MONDAY:
			return AppResources.getInstance ( context ).getString ( context , R.string.monday_label );
		case Calendar.TUESDAY:
			return AppResources.getInstance ( context ).getString ( context , R.string.tuesday_label );
		case Calendar.WEDNESDAY:
			return AppResources.getInstance ( context ).getString ( context , R.string.wednesday_label );
		case Calendar.THURSDAY:
			return AppResources.getInstance ( context ).getString ( context , R.string.thursday_label );
		case Calendar.FRIDAY:
			return AppResources.getInstance ( context ).getString ( context , R.string.friday_label );
		case Calendar.SATURDAY:
			return AppResources.getInstance ( context ).getString ( context , R.string.saturday_label );
		case Calendar.SUNDAY:
			return AppResources.getInstance ( context ).getString ( context , R.string.sunday_label );
		} // End switch
		// Never reached
		return null;
	}
	
	/**
	 * Returns the abbreviation string representation of the day, retrieved from the provided calendar.
	 * 
	 * @param context	The application context.
	 * @param calendar	The calendar object holding the required day.
	 * @return	String holding the abbreviation day label.
	 * @see #getDay(Context, Calendar)
	 * @see #getMonth(Context, Calendar)
	 */
	public static String getDayAbbreviation ( final Context context , final Calendar calendar ) {
		// Determine if the content and/or the calendar objects are valid
		if ( context == null || calendar == null )
			// Invalid arguments
			return null;
		// Compute and return the corresponding abbreviation day label
		switch ( calendar.get ( Calendar.DAY_OF_WEEK ) ) {
		case Calendar.MONDAY:
			return AppResources.getInstance ( context ).getString ( context , R.string.monday_abbreviation );
		case Calendar.TUESDAY:
			return AppResources.getInstance ( context ).getString ( context , R.string.tuesday_abbreviation );
		case Calendar.WEDNESDAY:
			return AppResources.getInstance ( context ).getString ( context , R.string.wednesday_abbreviation );
		case Calendar.THURSDAY:
			return AppResources.getInstance ( context ).getString ( context , R.string.thursday_abbreviation );
		case Calendar.FRIDAY:
			return AppResources.getInstance ( context ).getString ( context , R.string.friday_abbreviation );
		case Calendar.SATURDAY:
			return AppResources.getInstance ( context ).getString ( context , R.string.saturday_abbreviation );
		case Calendar.SUNDAY:
			return AppResources.getInstance ( context ).getString ( context , R.string.sunday_abbreviation );
		} // End switch
		// Never reached
		return null;
	}
	
	/**
	 * Returns the string representation of the month, retrieved from the provided calendar.
	 * 
	 * @param context	The application context.
	 * @param calendar	The calendar object holding the required month.
	 * @return	String holding the month label.
	 * @see #getDay(Context, Calendar)
	 */
	public static String getMonth ( final Context context , final Calendar calendar ) {
		// Determine if the content and/or the calendar objects are valid
		if ( context == null || calendar == null )
			// Invalid arguments
			return null;
		// Compute and return the corresponding month label
		switch ( calendar.get ( Calendar.MONTH ) ) {
    	case Calendar.JANUARY:
    		return AppResources.getInstance ( context ).getString ( context , R.string.january_label );
    	case Calendar.FEBRUARY:
    		return AppResources.getInstance ( context ).getString ( context , R.string.february_label );
    	case Calendar.MARCH:
    		return AppResources.getInstance ( context ).getString ( context , R.string.march_label );
    	case Calendar.APRIL:
    		return AppResources.getInstance ( context ).getString ( context , R.string.april_label );
    	case Calendar.MAY:
    		return AppResources.getInstance ( context ).getString ( context , R.string.may_label );
    	case Calendar.JUNE:
    		return AppResources.getInstance ( context ).getString ( context , R.string.june_label );
    	case Calendar.JULY:
    		return AppResources.getInstance ( context ).getString ( context , R.string.july_label );
    	case Calendar.AUGUST:
    		return AppResources.getInstance ( context ).getString ( context , R.string.august_label );
    	case Calendar.SEPTEMBER:
    		return AppResources.getInstance ( context ).getString ( context , R.string.september_label );
    	case Calendar.OCTOBER:
    		return AppResources.getInstance ( context ).getString ( context , R.string.october_label );
    	case Calendar.NOVEMBER:
    		return AppResources.getInstance ( context ).getString ( context , R.string.november_label );
    	case Calendar.DECEMBER:
    		return AppResources.getInstance ( context ).getString ( context , R.string.december_label );
		} // End switch
		// Never reached
		return null;
	}
	
	/**
	 * Returns the string representation of the full date, retrieved from the provided calendar.<br>
	 * The format used is as follows : <br>
	 * <b>DAY, MONTH DD YYYY</b>
	 * 
	 * @param context	The application context.
	 * @param calendar	The calendar object holding the required date.
	 * @return	String holding the full date label.
	 * @see #getBriefDate(Context, Calendar)
	 * @see #getBriefDate(Context, Date)
	 * @see #getFullTime(Context, Calendar, boolean, boolean)
	 * @see #getFullTime(Context, Calendar)
	 * @see #getFullDateTime(Context, Calendar, boolean, boolean)
	 * @see #getFullDateTime(Context, Calendar)
	 */
	public static String getFullDate ( final Context context , final Calendar calendar ) {
    	// Declare and initialize a two digits string formatter
    	DecimalFormat twoDigits = new DecimalFormat ( "00" );
    	// Compute the full date
    	StringBuilder date = new StringBuilder ();
    	date.append ( getDay ( context , calendar ) );
    	date.append ( ", " );
    	date.append ( getMonth ( context , calendar ) );
    	date.append ( " " );
    	date.append ( twoDigits.format ( calendar.get ( Calendar.DAY_OF_MONTH ) ) );
    	date.append ( ", " );
    	date.append ( String.valueOf ( calendar.get ( Calendar.YEAR ) ) );
    	// Return the full date
    	return date.toString ();
	}
	
	/**
	 * Returns the string representation of the brief date, retrieved from the provided calendar.<br>
	 * The format used is as follows : <br>
	 * <b>DD-MM-YYYY</b>
	 * 
	 * @param context	The application context.
	 * @param calendar	The calendar object holding the required date.
	 * @return	String holding the brief date label.
	 * @see #getBriefDate(Context, Calendar)
	 * @see #getFullTime(Context, Calendar, boolean, boolean)
	 * @see #getFullTime(Context, Calendar)
	 * @see #getFullDateTime(Context, Calendar, boolean, boolean)
	 * @see #getFullDateTime(Context, Calendar)
	 */
	public static String getBriefDate ( final Context context , final Date date ) {
		// Declare and initialize a calendar object
		Calendar calendar = Calendar.getInstance ();
		// Set the time
		calendar.setTime ( date );
		// Compute and return the brief date
		return getBriefDate ( context , calendar );
	}
	
	/**
	 * Returns the string representation of the brief date, retrieved from the provided calendar.<br>
	 * The format used is as follows : <br>
	 * <b>DD-MM-YYYY</b>
	 * 
	 * @param context	The application context.
	 * @param calendar	The calendar object holding the required date.
	 * @return	String holding the brief date label.
	 * @see #getBriefDate(Context, Date)
	 * @see #getFullTime(Context, Calendar, boolean, boolean)
	 * @see #getFullTime(Context, Calendar)
	 * @see #getFullDateTime(Context, Calendar, boolean, boolean)
	 * @see #getFullDateTime(Context, Calendar)
	 */
	public static String getBriefDate ( final Context context , final Calendar calendar ) {
    	// Declare and initialize a two digits string formatter
    	DecimalFormat twoDigits = new DecimalFormat ( "00" );
    	// Compute the brief date
    	StringBuilder date = new StringBuilder ();
    	date.append ( twoDigits.format ( calendar.get ( Calendar.DAY_OF_MONTH ) ) );
    	date.append ( "-" );
    	date.append ( twoDigits.format ( calendar.get ( Calendar.MONTH ) + 1 ) );
    	date.append ( "-" );
    	date.append ( String.valueOf ( calendar.get ( Calendar.YEAR ) ) );
    	// Return the brief date
    	return date.toString ();
	}
	
	/**
	 * Returns the string representation of the full time, retrieved from the provided calendar.<br>
	 * The format used is as follows : <br>
	 * <b>HH:MM:SS.SSS</b><br>
	 * The seconds (<b>SS</b>) and / or milliseconds (<b>SSS</b>) values may be omitted.
	 * 
	 * @param context	The application context.
	 * @param calendar	The calendar object holding the required date.
	 * @param seconds	Boolean indicating whether to add the seconds value or not.
	 * @param milliseconds	Boolean indicating whether to add the milliseconds value or not.
	 * @return	String holding the full time label.
	 * @see #getBriefDate(Context, Calendar)
	 * @see #getBriefDate(Context, Date)
	 * @see #getFullDate(Context, Calendar)
	 * @see #getFullTime(Context, Calendar)
	 * @see #getFullDateTime(Context, Calendar, boolean, boolean)
	 * @see #getFullDateTime(Context, Calendar)
	 */
	public static String getFullTime ( final Context context , final Calendar calendar , boolean seconds , boolean milliseconds ) {
    	// Declare and initialize a two digits string formatter
    	DecimalFormat twoDigits = new DecimalFormat ( "00" );
    	// Compute the full time
    	StringBuilder time = new StringBuilder ();
    	time.append ( twoDigits.format ( calendar.get ( Calendar.HOUR_OF_DAY ) ) );
    	time.append ( ":" );
    	time.append ( twoDigits.format ( calendar.get ( Calendar.MINUTE ) ) );
    	if ( seconds ) {
    		time.append ( ":" );
    		time.append ( twoDigits.format ( calendar.get ( Calendar.SECOND ) ) );
    	} // End if
    	if ( milliseconds ) {
    		time.append ( "." );
    		time.append ( twoDigits.format ( calendar.get ( Calendar.MILLISECOND ) ) );
    	} // End if
    	// Return the full time
    	return time.toString ();
	}
	
	/**
	 * Same as {@link #getFullTime(Context, Calendar, boolean, boolean)} with no seconds nor milliseconds values (flags are {@code false}).
	 * 
	 * @param context	The application context.
	 * @param calendar	The calendar object holding the required date.
	 * @return	String holding the full time label.
	 * @see #getBriefDate(Context, Calendar)
	 * @see #getBriefDate(Context, Date)
	 * @see #getFullDate(Context, Calendar)
	 * @see #getFullTime(Context, Calendar, boolean, boolean)
	 * @see #getFullDateTime(Context, Calendar, boolean, boolean)
	 * @see #getFullDateTime(Context, Calendar)
	 */
	public static String getFullTime ( final Context context , final Calendar calendar ) {
		// Compute and return the full time without the seconds and milliseconds values
		return getFullTime ( context , calendar , false , false );
	}
	
	/**
	 * Returns the string representation of the full date and time, retrieved from the provided calendar.<br>
	 * Same as {@link #getFullDate(Context, Calendar)} and {@link #getFullTime(Context, Calendar, boolean, boolean)} combined.
	 * 
	 * @param context	The application context.
	 * @param calendar	The calendar object holding the required date.
	 * @param seconds	Boolean indicating whether to add the seconds value or not.
	 * @param milliseconds	Boolean indicating whether to add the milliseconds value or not.
	 * @return	String holding the full date time label.
	 * @see #getBriefDate(Context, Calendar)
	 * @see #getBriefDate(Context, Date)
	 * @see #getFullDate(Context, Calendar)
	 * @see #getFullTime(Context, Calendar, boolean, boolean)
	 * @see #getFullTime(Context, Calendar)
	 * @see #getFullDateTime(Context, Calendar)
	 */
	public static String getFullDateTime ( final Context context , final Calendar calendar , boolean seconds , boolean milliseconds ) {
		// Compute the full date and time
    	StringBuilder dateTime = new StringBuilder ();
    	dateTime.append ( getFullDate ( context , calendar ) );
    	dateTime.append ( " " );
    	dateTime.append ( AppResources.getInstance ( context ).getString ( context , R.string.at_label ) );
    	dateTime.append ( " " );
    	dateTime.append ( getFullTime ( context , calendar , seconds , milliseconds ) );
    	// Return the full date and time
		return dateTime.toString ();
	}
	
	/**
	 * Same as {@link #getFullDateTime(Context, Calendar, boolean, boolean)} with no seconds nor milliseconds values (flags are {@code false}).
	 * 
	 * @param context	The application context.
	 * @param calendar	The calendar object holding the required date.
	 * @return	String holding the full date time label.
	 * @see #getBriefDate(Context, Calendar)
	 * @see #getBriefDate(Context, Date)
	 * @see #getFullDate(Context, Calendar)
	 * @see #getFullTime(Context, Calendar, boolean, boolean)
	 * @see #getFullTime(Context, Calendar)
	 * @see #getFullDateTime(Context, Calendar, boolean, boolean)
	 */
	public static String getFullDateTime ( final Context context , final Calendar calendar ) {
		// Compute and return the full date and time without the seconds and milliseconds values
    	return getFullDateTime ( context , calendar , false , false );
	}
	
}
