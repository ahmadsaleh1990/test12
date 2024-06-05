/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Widgets.AvailabilityCalendar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.ClientAvailabilities;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.PermissionsUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * Custom View used to display the client schedule on a calendar.<br>
 * The calendar can be used as display or as an editable calendar to define the client schedule.
 * 
 * @author Elias
 *
 */
@SuppressLint("UseSparseArrays")
public class AvailabilityCalendarView extends View {

	/**
	 * Boolean used to determine if the availability calendar is used as view only, or is editable otherwise.
	 */
	private boolean isView;
	
	/**
	 * Constant integer holding the slot duration in minutes.
	 */
	public static final int SLOT_DURATION = 30;
	
	/**
	 * Constant integer holding the text size in DP used by the paint object.
	 */
	private static final int TEXT_SIZE_DP = 19;
	
	/**
	 * Constant integer holding the slot border width in DP.
	 */
	private static final int SLOT_BORDER_WIDTH_DP = 2;
	
	/**
	 * Constant integer holding the slot height in DP.
	 */
	private static final int SLOT_HEIGHT_DP = 40;
	
	/**
	 * Constant integer holding the slot padding in DP.
	 */
	private static final int SLOT_PADDING_DP = 5;
	
	/**
	 * Constant integer holding the starting working hour.
	 */
	private final int startingWorkingHour;
	
	/**
	 * Constant integer holding the ending working hour.
	 */
	private final int endingWorkingHour;
	
	/**
	 * Constant float holding the value of 1 DP in pixels
	 */
	private final float oneDP;
	
	/**
	 * Constant integer holding the slot border width in pixels
	 */
	private final int slotBorderWidth;
	
	/**
	 * Constant integer holding the slot height in pixels
	 */
	private final int slotHeight;
	
	/**
	 * Constant integer holding the slot padding in pixels
	 */
	private final int slotPadding;
	
	/**
	 * Constant integer holding the text size in pixels used by the paint object.
	 */
	private final int textSize;
	
	/**
	 * String holding monday's abbreviation.
	 */
	private final String mondayAbbreviation;
	
	/**
	 * String holding tuesday's abbreviation.
	 */
	private final String tuesdayAbbreviation;
	
	/**
	 * String holding wednesday's abbreviation.
	 */
	private final String wednesdayAbbreviation;
	
	/**
	 * String holding thursday's abbreviation.
	 */
	private final String thursdayAbbreviation;

	/**
	 * String holding friday's abbreviation.
	 */
	private final String fridayAbbreviation;
	
	/**
	 * String holding saturday's abbreviation.
	 */
	private final String saturdayAbbreviation;
	
	/**
	 * String holding sunday's abbreviation.
	 */
	private final String sundayAbbreviation;
	
	/**
	 * Integer holding the number of modified calendar slots.
	 */
	private int modifications;
	
	/**
	 * Integer holding the previous number of modified calendar slots (one modification behind).
	 */
	private int modificationsOld;
	
	/**
	 * A two digits formatter used to display hours and minutes in the proper format.
	 */
	private final DecimalFormat twoDigits;
	
	/**
	 * List of {@link me.SyncWise.Android.Widgets.AvailabilityCalendar.AvailabilityCalendar AvailabilityCalendar} objects hosting the availability duration headers of the current availability calendar view.
	 * <em>(e.g. 8:00, 8:30, ..)</em>
	 */
	private List < AvailabilityCalendar > durationSlots;
	
	/**
	 * List of {@link me.SyncWise.Android.Widgets.AvailabilityCalendar.AvailabilityCalendar AvailabilityCalendar} objects hosting the availability day headers of the current availability calendar view.<br>
	 * <em>(e.g. MO, TU, ..)</em>
	 */
	private List < AvailabilityCalendar > dayHeaders;
	
	/**
	 * Map of {@link me.SyncWise.Android.Widgets.AvailabilityCalendar.AvailabilityCalendar AvailabilityCalendar} objects hosting the availability slots of the current availability calendar view.<br>
	 * The lists are mapped to the appropriate day number based on the {@link java.util.Calendar Calendar} class.
	 */
	private HashMap < Integer , ArrayList < AvailabilityCalendar > > calendarSlots;
	
	/**
	 * {@link android.graphics.Paint Paint} object holding the style and color information about how to draw geometries, text and bitmaps.<br>
	 * This object is used to draw text.
	 */
	private Paint paintText;
	
	/**
	 * {@link android.graphics.Paint Paint} object holding the style and color information about how to draw geometries, text and bitmaps.<br>
	 * This object is used to draw borders.
	 */
	private Paint paintBorder;
	
	/**
	 * {@link android.graphics.Paint Paint} object holding the style and color information about how to draw geometries, text and bitmaps.<br>
	 * This object is used to draw availability slots.
	 */
	private Paint paintSlot;
	
	/**
	 * Constant integer holding the color of a selected availability slot.
	 */
	private final int selectedSlotColor;
	
	/**
	 * Constant integer holding the color of an unselected availability slot.
	 */
	private final int unselectedSlotColor;
	
	/**
	 * Constructor.<br>
	 * Use {@code NULL} for calendarSlots if the view should initialize this attribute.
	 * 
	 * @param context	The application context.
	 * @param calendarSlots	Map of {@link me.SyncWise.Android.Widgets.AvailabilityCalendar.AvailabilityCalendar AvailabilityCalendar} objects hosting the availability slots of the current availability calendar view.
	 * @param clientAvailabilities	List of {@link me.SyncWise.Android.Database.ClientAvailabilities ClientAvailabilities} objects hosting the client schedule.
	 */
	public AvailabilityCalendarView ( Context context , final HashMap < Integer , ArrayList < AvailabilityCalendar > > calendarSlots , final List < ClientAvailabilities > clientAvailabilities ) {
		// Superclass constructor
		super ( context );
		
		// Initialize the calendar slots map
		this.calendarSlots = calendarSlots;
		
		// Initialize a two digits formatter
		twoDigits = new DecimalFormat ( "00" );
		
		// Compute the slot border width in pixels
		slotBorderWidth = (int) TypedValue.applyDimension ( TypedValue.COMPLEX_UNIT_DIP , SLOT_BORDER_WIDTH_DP , context.getResources ().getDisplayMetrics () );
		// Compute the slot height in pixels
		slotHeight = (int) TypedValue.applyDimension ( TypedValue.COMPLEX_UNIT_DIP , SLOT_HEIGHT_DP , context.getResources ().getDisplayMetrics () );
		// Compute the slot padding in pixels
		slotPadding = (int) TypedValue.applyDimension ( TypedValue.COMPLEX_UNIT_DIP , SLOT_PADDING_DP , context.getResources ().getDisplayMetrics () );
		// Compute the text size in pixels
		textSize = (int) TypedValue.applyDimension ( TypedValue.COMPLEX_UNIT_DIP , TEXT_SIZE_DP , context.getResources ().getDisplayMetrics () );
		// Compute the value of 1 DP in pixels
		oneDP = TypedValue.applyDimension ( TypedValue.COMPLEX_UNIT_DIP , 1 , context.getResources ().getDisplayMetrics () );
		// Set the selected slot color
		selectedSlotColor = getResources ().getColor ( R.color.PowderBlue );
		// Set the unselected slot color
		unselectedSlotColor = getResources ().getColor ( R.color.Linen );
		// Retrieve the week days abbreviations
		mondayAbbreviation = getContext ().getString ( R.string.monday_abbreviation );
		tuesdayAbbreviation = getContext ().getString ( R.string.tuesday_abbreviation );
		wednesdayAbbreviation = getContext ().getString ( R.string.wednesday_abbreviation );
		thursdayAbbreviation = getContext ().getString ( R.string.thursday_abbreviation );
		fridayAbbreviation = getContext ().getString ( R.string.friday_abbreviation );
		saturdayAbbreviation = getContext ().getString ( R.string.saturday_abbreviation );
		sundayAbbreviation = getContext ().getString ( R.string.sunday_abbreviation );
		
		// Retrieve the user code
		String userCode = DatabaseUtils.getCurrentUserCode ( getContext () );
		// Retrieve the company code
		String companyCode = DatabaseUtils.getCurrentCompanyCode ( getContext () );
		// Retrieve the starting working hour
		startingWorkingHour = PermissionsUtils.getStartingWorkingHour ( getContext () , userCode , companyCode );
		// Retrieve the ending working hour
		endingWorkingHour = PermissionsUtils.getEndingWorkingHour ( getContext () , userCode , companyCode );
		// Determine if the working hours are valid
		if ( endingWorkingHour <= startingWorkingHour )
			// Invalid working hours
			// Do nothing
			return;
		
		// Check if both the calendar slots map and the client availabilities list are invalid (at least one should be valid)
		if ( calendarSlots == null && clientAvailabilities == null )
			// Invalid data
			// Do nothing
			return;
		
		// Initialize paint objects
		initializePaints ();
		
		// Initialize the day headers, hosting the 7 abbreviations of the week days
		initializeDayHeaders ();
		// Check if the availability slots map is initialized
		if ( this.calendarSlots == null )
			// Initialize the availability slots map lists
			initializeAvailabilitySlots ( clientAvailabilities );
		else
			// Compute the number of modified slots
			computeModifiedCalendarSlots ();
		// Initialize the duration headers
		initializeDurationHeaders ();
	}
	
	/**
	 * Getter - {@link #isView}
	 * 
	 * @return Boolean used to determine if the availability calendar is used as view only, or is editable otherwise.
	 */
	public boolean isView () {
		return isView;
	}
	
	/**
	 * Setter - {@link #isView}
	 * 
	 * @param isView Boolean used to determine if the availability calendar is used as view only, or is editable otherwise.
	 */
	public void setView ( boolean isView ) {
		this.isView = isView;
	}
	
	/**
	 * Getter - {@link #calendarSlots}
	 * 
	 * @return Map of {@link me.SyncWise.Android.Widgets.AvailabilityCalendar.AvailabilityCalendar AvailabilityCalendar} objects hosting the availability slots of the current availability calendar view.
	 */
	public HashMap < Integer , ArrayList < AvailabilityCalendar >> getCalendarSlots () {
		return calendarSlots;
	}

	/**
	 * Properly initializes all paint objects used to draw the view's components.
	 */
	private void initializePaints () {
		// Initialize the paint object used to draw text
		paintText = new Paint ( Paint.ANTI_ALIAS_FLAG );
		paintText.setTextAlign ( Align.CENTER );
		paintText.setTypeface ( Typeface.defaultFromStyle ( Typeface.BOLD ) );
		paintText.setStyle ( Style.FILL_AND_STROKE );
		paintText.setTextSize ( textSize );
		// Initialize the paint object used to draw slots borders
		paintBorder = new Paint ();
		paintBorder.setStyle ( Style.STROKE );
		paintBorder.setStrokeWidth ( slotBorderWidth );
		// Initialize the paint object used to draw slots backgrounds
		paintSlot = new Paint ();
		paintSlot.setStyle ( Style.FILL );
	}
	
	/**
	 * Initializes the day headers of the calendar, hosting the 7 abbreviations of the week days.
	 */
	private void initializeDayHeaders () {
		// Initialize the day headers list
		dayHeaders = new ArrayList < AvailabilityCalendar > ();
		// Declare and initialize the 7 objects used to represent the 7 abbreviations of the week days
		AvailabilityCalendar monday = new AvailabilityCalendar ( true );
		AvailabilityCalendar tuesday = new AvailabilityCalendar ( true );
		AvailabilityCalendar wednesday = new AvailabilityCalendar ( true );
		AvailabilityCalendar thursday = new AvailabilityCalendar ( true );
		AvailabilityCalendar friday = new AvailabilityCalendar ( true );
		AvailabilityCalendar saturday = new AvailabilityCalendar ( true );
		AvailabilityCalendar sunday = new AvailabilityCalendar ( true );
		// Set the text abbreviations of the week days
		monday.setText ( AppResources.getInstance ( getContext () ).getString ( getContext() , R.string.monday_abbreviation ) );
		tuesday.setText ( AppResources.getInstance ( getContext () ).getString ( getContext() , R.string.tuesday_abbreviation ) );
		wednesday.setText ( AppResources.getInstance ( getContext () ).getString ( getContext() , R.string.wednesday_abbreviation ) );
		thursday.setText ( AppResources.getInstance ( getContext () ).getString ( getContext() , R.string.thursday_abbreviation ) );
		friday.setText ( AppResources.getInstance ( getContext () ).getString ( getContext() , R.string.friday_abbreviation ) );
		saturday.setText ( AppResources.getInstance ( getContext () ).getString ( getContext() , R.string.saturday_abbreviation ) );
		sunday.setText ( AppResources.getInstance ( getContext () ).getString ( getContext() , R.string.sunday_abbreviation ) );
		// Add the objects to the list
		dayHeaders.add ( monday );
		dayHeaders.add ( tuesday );
		dayHeaders.add ( wednesday );
		dayHeaders.add ( thursday );
		dayHeaders.add ( friday );
		dayHeaders.add ( saturday );
		dayHeaders.add ( sunday );
	}
	
	/**
	 * Initializes the availability slots map lists of the calendar.
	 * 
	 * @param clientAvailabilities	List of {@link me.SyncWise.Android.Database.ClientAvailabilities ClientAvailabilities} objects hosting the client schedule.
	 */
	private void initializeAvailabilitySlots ( final List < ClientAvailabilities > clientAvailabilities ) {
		// Initialize the availability slots map lists
		calendarSlots = new HashMap < Integer , ArrayList<AvailabilityCalendar> > ();
		calendarSlots.put ( Calendar.MONDAY , new ArrayList < AvailabilityCalendar > () );
		calendarSlots.put ( Calendar.TUESDAY , new ArrayList < AvailabilityCalendar > () );
		calendarSlots.put ( Calendar.WEDNESDAY , new ArrayList < AvailabilityCalendar > () );
		calendarSlots.put ( Calendar.THURSDAY , new ArrayList < AvailabilityCalendar > () );
		calendarSlots.put ( Calendar.FRIDAY , new ArrayList < AvailabilityCalendar > () );
		calendarSlots.put ( Calendar.SATURDAY , new ArrayList < AvailabilityCalendar > () );
		calendarSlots.put ( Calendar.SUNDAY , new ArrayList < AvailabilityCalendar > () );
		// Declare and initialize integers to hold the current time
		int hour = startingWorkingHour;
		int minute = 0;
		// Keep iterating until the ending working hour is reached
		while ( hour < endingWorkingHour ) {
			// Declare and initialize an availability slot for every day of the week, for the current time
			calendarSlots.get ( Calendar.MONDAY ).add ( new AvailabilityCalendar ( false , Calendar.MONDAY , hour , minute ) );
			calendarSlots.get ( Calendar.TUESDAY ).add ( new AvailabilityCalendar ( false , Calendar.TUESDAY , hour , minute ) );
			calendarSlots.get ( Calendar.WEDNESDAY ).add ( new AvailabilityCalendar ( false , Calendar.WEDNESDAY , hour , minute ) );
			calendarSlots.get ( Calendar.THURSDAY ).add ( new AvailabilityCalendar ( false , Calendar.THURSDAY , hour , minute ) );
			calendarSlots.get ( Calendar.FRIDAY ).add ( new AvailabilityCalendar ( false , Calendar.FRIDAY , hour , minute ) );
			calendarSlots.get ( Calendar.SATURDAY ).add ( new AvailabilityCalendar ( false , Calendar.SATURDAY , hour , minute ) );
			calendarSlots.get ( Calendar.SUNDAY ).add ( new AvailabilityCalendar ( false , Calendar.SUNDAY , hour , minute ) );
			// Update the current time by adding one slot duration
			ArrayList < AvailabilityCalendar > list = calendarSlots.get ( Calendar.SUNDAY );
			AvailabilityCalendar lastSlot = list.get ( list.size () - 1 );
			hour = lastSlot.getToHour ();
			minute = lastSlot.getToMinute ();
		} // End while loop
		
		// Now that the calendar slots are defined
		// Define the calendar schecule using the provided list
		// Check first if the list is valid
		if ( clientAvailabilities == null )
			// Invalid list
			// Do nothing
			return;
		
		// Iterate over all client availabilities
		for ( ClientAvailabilities clientAvailability : clientAvailabilities )
			// Define the schedule using the current client availability
			defineSchedule ( clientAvailability );
	}
	
	/**
	 * Computes the number of modified calendar slots.<br>
	 * This method is mainly useful if the view is being restored, and hence the {@link #calendarSlots} list is provided (instead of built).
	 */
	private void computeModifiedCalendarSlots () {
		// Reset the number of modified calendar slots
		modifications = 0;
		// Iterate over the availability lists
		for ( Integer day : calendarSlots.keySet () )
			// Iterate over the availability slots
			for ( AvailabilityCalendar slot : calendarSlots.get ( day ) )
				// Check if the current slot is modified
				if ( slot.isModified () )
					// Update the number of modified calendar slots
					modifications ++;
		modificationsOld = modifications;
	}
	
	/**
	 * Initializes the duration slots lists of the calendar.
	 */
	private void initializeDurationHeaders () {
		// Initialize the duration slots list
		durationSlots = new ArrayList < AvailabilityCalendar > ();
		// Declare and initialize integers to hold the current time
		int hour = startingWorkingHour;
		int minute = 0;
		// Keep iterating until the ending working hour is reached
		while ( hour < endingWorkingHour ) {
			// Declare and initialize a duration slot for the current time
			AvailabilityCalendar duration = new AvailabilityCalendar ( true );
			duration.setFromHour ( hour );
			duration.setFromMinute ( minute );
			duration.setText ( twoDigits.format ( hour ) + ":" + twoDigits.format ( minute ) );
			durationSlots.add ( duration );
			// Update the current time by adding one slot duration
			hour = duration.getToHour ();
			minute = duration.getToMinute ();
		} // End while loop
		// Declare and initialize a duration slot for the very last time slot
		AvailabilityCalendar duration = new AvailabilityCalendar ( true );
		duration.setText ( twoDigits.format ( hour ) + ":" + twoDigits.format ( minute ) );
		durationSlots.add ( duration );
	}
	
	/**
	 * Defines the availability calendar schedule based on the provided client availability.<br>
	 * The appropriate availability slots are selected.
	 * 
	 * @param clientAvailability	List of {@link me.SyncWise.Android.Database.ClientAvailabilities ClientAvailabilities} objects hosting the client schedule.
	 */
	private void defineSchedule ( final ClientAvailabilities clientAvailability ) {
		try {
			// Declare a list used to hold the calendar slots of the current day
			List < AvailabilityCalendar > slots = null;
			// Determine the availability day and retrieve the appropriate list
			if ( clientAvailability.getDay ().equals ( mondayAbbreviation ) )
				slots = calendarSlots.get ( Calendar.MONDAY );
			else if ( clientAvailability.getDay ().equals ( tuesdayAbbreviation ) )
				slots = calendarSlots.get ( Calendar.TUESDAY );
			else if ( clientAvailability.getDay ().equals ( wednesdayAbbreviation ) )
				slots = calendarSlots.get ( Calendar.WEDNESDAY );
			else if ( clientAvailability.getDay ().equals ( thursdayAbbreviation ) )
				slots = calendarSlots.get ( Calendar.THURSDAY );
			else if ( clientAvailability.getDay ().equals ( fridayAbbreviation ) )
				slots = calendarSlots.get ( Calendar.FRIDAY );
			else if ( clientAvailability.getDay ().equals ( saturdayAbbreviation ) )
				slots = calendarSlots.get ( Calendar.SATURDAY );
			else if ( clientAvailability.getDay ().equals ( sundayAbbreviation ) )
				slots = calendarSlots.get ( Calendar.SUNDAY );
			// Check if the calendar slots list is valid
			if ( calendarSlots == null )
				// Invalid list
				// Do nothing
				return;
			// Determine the starting time of the current availability
			Calendar start = Calendar.getInstance ();
			start.setTimeInMillis ( clientAvailability.getStartTime ().getTime () );
			// Determine the ending time of the current availability
			Calendar end = Calendar.getInstance ();
			end.setTimeInMillis ( clientAvailability.getEndTime ().getTime () );
			// Before checking if both calendars are valid, set the same date for both and reset the seconds and milliseconds values
			Calendar today = Calendar.getInstance ();
			start.set ( today.get ( Calendar.YEAR ) , today.get ( Calendar.MONTH ) , today.get ( Calendar.DAY_OF_MONTH ) );
			start.set ( Calendar.SECOND , 0 );
			start.set ( Calendar.MILLISECOND , 0 );
			end.set ( today.get ( Calendar.YEAR ) ,today.get ( Calendar.MONTH ) , today.get ( Calendar.DAY_OF_MONTH ) );
			end.set ( Calendar.SECOND , 0 );
			end.set ( Calendar.MILLISECOND , 0 );
			// Check if both calendars are valid (ending time should be after starting time)
			if ( end.compareTo ( start ) != 1 )
				// Invalid dates
				// Do nothing
				return;
		
			// Declare and initialize starting and ending times for the availability slot
			Calendar slotStart = Calendar.getInstance ();
			Calendar slotEnd = Calendar.getInstance ();
			// Set the same date for both
			slotStart.set ( today.get ( Calendar.YEAR ) , today.get ( Calendar.MONTH ) , today.get ( Calendar.DAY_OF_MONTH ) );
			slotStart.set ( Calendar.SECOND , 0 );
			slotStart.set ( Calendar.MILLISECOND , 0 );
			slotEnd.set ( today.get ( Calendar.YEAR ) ,today.get ( Calendar.MONTH ) , today.get ( Calendar.DAY_OF_MONTH ) );
			slotEnd.set ( Calendar.SECOND , 0 );
			slotEnd.set ( Calendar.MILLISECOND , 0 );
			// Iterate over the calendar slots of the current day
			for ( AvailabilityCalendar slot : slots ) {
				// Set the starting and ending dates
				slotStart.set ( Calendar.HOUR_OF_DAY , slot.getFromHour () );
				slotStart.set ( Calendar.MINUTE , slot.getFromMinute () );
				slotEnd.set ( Calendar.HOUR_OF_DAY , slot.getToHour () );
				slotEnd.set ( Calendar.MINUTE , slot.getToMinute () );
				// Determine if the slot duration is included in the client availability
				// The slot start should be equal or after the availability start (NOT BEFORE)
				// The slot end should be equal or before the availability start (NOT AFTER)
				if ( slotStart.compareTo ( start ) != -1 && slotEnd.compareTo ( end ) != 1 ) {
					// Select the current slot
					slot.setPreviouslySelected ( true );
					slot.setSelected ( true );
				} // End if
			} // End for each
		} catch ( Exception exception ) {
			// Do nothing
		} // End of try-catch block
	}
	
	/**
	 * Determines whether or not the calendar has been modified.
	 * 
	 * @return	Boolean indicating if the calendar has been modified or not.
	 */
	public boolean isModified () {
		// Determine the number of modifications
		return modifications == 0 ? false : true;
	}
	
	/*
	 * Measure the view and its content to determine the measured width and the measured height.
	 *
	 * @see android.view.View#onMeasure(int, int)
	 */
	protected void onMeasure ( int widthMeasureSpec , int heightMeasureSpec ) {
		try {
			// Retrieve the number of slots per day
			int dailySlots = calendarSlots.get ( calendarSlots.keySet ().toArray () [ 0 ] ).size ();
			// Compute the height
			int height = slotHeight * ( dailySlots + 2 ) + slotPadding * dailySlots;
			// This method must be called by onMeasure(int, int) to store the measured width and measured height
			setMeasuredDimension ( widthMeasureSpec , height );
		} catch ( Exception exception ) {
			// An error occurred, set a height of 0
		} // End of try-catch block
	}
	
	/*
	 * Implement this to do your drawing.
	 *
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw ( Canvas canvas ) {
		// Check if all the objects are valid
		if ( calendarSlots == null || dayHeaders == null || durationSlots == null )
			// Invalid data
			// Do nothing
			return;
		
		// Otherwise the data is valid
		int left = 0;
		int right = 0;
		int top = 0;
		int bottom = 0;
		int size = 0;
		
		// Start by drawing the day headers
		// Retrieve a reference to paint font metrics
		FontMetrics fontMetrics = paintText.getFontMetrics ();
		// Iterate over all day headers
		for ( int i = 0 ; i < dayHeaders.size () ; i ++ )
			// Draw the day header
			canvas.drawText ( dayHeaders.get ( i ).getText () , Math.round ( ( i + 1.5 ) * getWidth () / 8 ) , Math.round ( ( slotHeight / 2.0 ) - ( fontMetrics.ascent + fontMetrics.descent ) / 2.0 ) , paintText );
		
		// Draw all the availability slots, one day at a time
		size = calendarSlots.get ( Calendar.MONDAY ).size ();
		// Iterate over the availability slots
		for ( Integer day : calendarSlots.keySet () ) {
			int column = 0;
			// Determine the column index
			switch ( day ) {
			case Calendar.MONDAY:
				column = 1;
				break;
			case Calendar.TUESDAY:
				column = 2;
				break;
			case Calendar.WEDNESDAY:
				column = 3;
				break;
			case Calendar.THURSDAY:
				column = 4;
				break;
			case Calendar.FRIDAY:
				column = 5;
				break;
			case Calendar.SATURDAY:
				column = 6;
				break;
			case Calendar.SUNDAY:
				column = 7;
				break;
			} // End switch
			// Iterate over all availability slots of the current day
			ArrayList < AvailabilityCalendar > slots = calendarSlots.get ( day );
			for ( int i = 0 ; i < size ; i ++ ) {
				// Retrieve a reference to the current slot
				AvailabilityCalendar slot = slots.get ( i );
				// Compute the availability slot size
				left = (int) Math.round ( column * getWidth () / 8.0 );
				right = (int) Math.round ( ( column + 1 ) * getWidth () / 8.0 );
				top = ( i + 1 ) * slotHeight + ( i + 1 ) * slotPadding;
				bottom = ( i + 2 ) * slotHeight + ( i + 1 ) * slotPadding;
				// Set the availability slot size
				slot.getRect ().set ( left , top , right , bottom );
				// Modify the paint object accordingly (based on if the slot is selected or not)
				paintSlot.setColor ( slot.isSelected () ? selectedSlotColor : unselectedSlotColor );
				// Draw the availability slot
				canvas.drawRect ( slots.get ( i ).getRect () , paintSlot );
				// Draw the availability slot border
				canvas.drawRect ( slots.get ( i ).getRect () , paintBorder );
			} // End for loop
		} // End for each

		// Compute the allowed width : 90 % of a slot width
		int allowedWidth = (int) Math.round ( getWidth () / 8.0 * 0.9 );
		// Reduce the text size if the current size exceeds the allowed width
		while ( paintText.measureText ( "00:00" ) > allowedWidth )
			// Reduce the text size by 1 DP
			paintText.setTextSize ( paintText.getTextSize () - oneDP );
		// Draw all the duration slots
		size = durationSlots.size ();
		float x = Math.round ( getWidth () / 8.0 / 2.0 );
		// Iterate over all duration slots
		for ( int i = 0 ; i < size ; i ++ ) {
			// Draw the duration header
			canvas.drawText ( durationSlots.get ( i ).getText () , x , ( i + 1 ) * slotHeight + ( i + 0.5f ) * slotPadding - ( fontMetrics.ascent + fontMetrics.descent ) / 2f , paintText );
		} // End for loop
		// Reset the text size
		paintText.setTextSize ( textSize );
	}
	
	/*
	 * Implement this method to handle touch screen motion events.
	 *
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@SuppressLint("ClickableViewAccessibility") 
	@Override
	public boolean onTouchEvent ( MotionEvent event ) {
		// Determine if the pressed gesture has finished
		if ( event.getAction () != MotionEvent.ACTION_UP )
			// Indicate that the event was successfully handled
			// The pressed gesture has not finished
			return true;
		// Otherwise the pressed gesture has finished
		// Determine if the current availability calendar view is not editable
		if ( isView )
			// The view is not editable
			// Superclass method call
			return super.onTouchEvent ( event );
		// Otherwise the view is editable
		// Determine if an availability slot was clicked
		// Iterate over the availability slots
		for ( Integer day : calendarSlots.keySet () )
			for ( AvailabilityCalendar slot : calendarSlots.get ( day ) )
				// Check if current slot was clicked
				if ( slot.getRect ().contains ( (int) event.getX () , (int) event.getY () ) ) {
					// Toggle the slot selection
					slot.toggle ();
					// Update the number of modified calendar slots
					modifications = slot.isSelected () == slot.isPreviouslySelected () ? modifications - 1 : modifications + 1;
					// Mark the area defined by dirty as needing to be drawn
					invalidate ( slot.getRect () );
					
					// At this point, obviously, the value of modifications differs from modificationsOld
					// Determine if the options menu should be invalidated
					// To determine this, one of the variables should be equal to zero
					if ( modifications == 0 || modificationsOld == 0 )
			    		// Refresh the menu items in the action bar
						( (Activity) getContext () ).invalidateOptionsMenu ();
					
					// Update the previous number of modifed calendar slots
					modificationsOld = modifications;
					// Exit loop
					break;
				} // End if
		// Indicate that the event was successfully handled
		return true;
	}

}