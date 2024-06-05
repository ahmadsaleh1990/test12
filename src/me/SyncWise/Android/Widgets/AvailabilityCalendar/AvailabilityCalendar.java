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

import java.io.Serializable;

import android.graphics.Rect;

/**
 * Custom class used to represent an availability slot inside the availability calendar, from the calendar and graphical points of view.
 * 
 * @author Elias
 *
 */
public class AvailabilityCalendar implements Serializable {

	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Boolean used to indicate if the current object is final (used for display only, and hence cannot be modified) or not.
	 */
	private final boolean isFinal;
	
	/**
	 * Integer holding the {@link java.util.Calendar#DAY_OF_WEEK Calendar.DAY_OF_WEEK} index.
	 */
	private int day;
	
	/**
	 * Integer holding the starting hour of the current availability slot. (between 0 and 23)
	 */
	private int fromHour;
	
	/**
	 * Integer holding the starting minute of the current availability slot. (between 0 and 59)
	 */
	private int fromMinute;
	
	/**
	 * {@link android.graphics.Rect Rect} object holding the four integer coordinates for the availability slot inside the calendar.
	 */
	private Rect rect;
	
	/**
	 * Boolean used to indicate if the current availability slot was previously selected or not.
	 */
	private boolean isPreviouslySelected;
	
	/**
	 * Boolean used to indicate if the current availability slot is selected or not.
	 */
	private boolean isSelected;
	
	/**
	 * String holding text to be displayed on the calendar slot.
	 */
	private String text;
	
	/**
	 * Constructor.
	 *
	 * @param isFinal	Boolean used to indicate if the current object is final (used for display only, and hence cannot be modified) or not.
	 */
	public AvailabilityCalendar ( final boolean isFinal ) {
		// Initialize attribute
		this.isFinal = isFinal;
		this.rect = new Rect ();
	}
	
	/**
	 * Constructor.
	 * 
	 * @param day	Integer holding the {@link java.util.Calendar#DAY_OF_WEEK Calendar.DAY_OF_WEEK} index.
	 * @param hour	Integer holding the starting hour of the current availability slot.
	 * @param minute	Integer holding the starting minute of the current availability slot.
	 */
	public AvailabilityCalendar ( final boolean isFinal , final int day , final int hour , final int minute ) {
		// Overloaded method call
		this ( isFinal );
		// Initialize attributes
		setDay ( day );
		setFromHour ( hour );
		setFromMinute ( minute );
	}
	
	/*
	 * Compares this instance with the specified object and indicates if they are equal.
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals ( Object object ) {
		// Flag used to indicate if the objects are equal
		boolean isEqual = false;
		// Check if the object is valid and instance of AvailabilityCalendar
		if ( object != null && object instanceof AvailabilityCalendar )
			// Compare objects based on the day, starting hour and starting minute
			isEqual = day == ( (AvailabilityCalendar) object ).getDay ()
				&& fromHour == ( (AvailabilityCalendar) object ).getFromHour ()
				&& fromMinute == ( (AvailabilityCalendar) object ).getFromMinute ();
		// Return flag
		return isEqual;
	}
	/*
	 * Returns an integer hash code for this object.
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode () {
		final int prime = 31;
		int result = 1;
		result = prime * result + day;
		result = prime * result + fromHour;
		result = prime * result + fromMinute;
		return result;
	}
	
	/**
	 * Getter - {@link #isFinal}
	 * 
	 * @return	Boolean used to indicate if the current object is final (used for display only, and hence cannot be modified) or not.
	 */
	public boolean isFinal () {
		return isFinal;
	}
	
	/**
	 * Getter - {@link #day}
	 * 
	 * @return Integer holding the {@link java.util.Calendar#DAY_OF_WEEK Calendar.DAY_OF_WEEK} index.
	 */
	public int getDay () {
		return day;
	}
	
	/**
	 * Setter - {@link #day}
	 * 
	 * @param day	Integer holding the {@link java.util.Calendar#DAY_OF_WEEK Calendar.DAY_OF_WEEK} index.
	 */
	public void setDay ( final int day ) {
		this.day = day;
	}
	
	/**
	 * Getter - {@link #fromHour}
	 * 
	 * @return Integer holding the starting hour of the current availability slot.
	 */
	public int getFromHour () {
		return fromHour;
	}
	
	/**
	 * Setter - {@link #fromHour}
	 * 
	 * @param fromHour	Integer holding the starting hour of the current availability slot.
	 */
	public void setFromHour ( final int fromHour ) {
		// Check if the hour is valid
		if ( fromHour >= 0 && fromHour <= 23 )
			this.fromHour = fromHour;
	}
	
	/**
	 * Getter - {@link #fromMinute}
	 * 
	 * @return Integer holding the starting minute of the current availability slot.
	 */
	public int getFromMinute () {
		return fromMinute;
	}
	
	/**
	 * Setter - {@link #fromMinute}
	 * 
	 * @param fromMinute Integer holding the starting minute of the current availability slot.
	 */
	public void setFromMinute ( final int fromMinute ) {
		// Check if the minute is valid
		if ( fromMinute >= 0 && fromMinute <= 59 )
			this.fromMinute = fromMinute;
	}
	
	/**
	 * Getter - {@link #rect}
	 * 
	 * @return {@link android.graphics.Rect Rect} object holding the four integer coordinates for the availability slot inside the calendar.
	 */
	public Rect getRect () {
		return rect;
	}
	
	/**
	 * Gets the ending hour of the current availability slot, based on the slot duration set by {@link me.SyncWise.Android.Widgets.AvailabilityCalendar.AvailabilityCalendarView#SLOT_DURATION AvailabilityCalendarView.SLOT_DURATION}
	 * 
	 * @return	Integer holding the ending hour of the current availability slot.
	 */
	public int getToHour () {
		// Calculate the ending hour of the availability slot based on the starting hour and slot duration
		int endingHour = fromHour + ( AvailabilityCalendarView.SLOT_DURATION / 60 );
		// Calculate the ending hour of the availability slot based on the starting minute and slot duration
		endingHour += ( fromMinute + ( AvailabilityCalendarView.SLOT_DURATION % 60 ) ) / 60;
		// Return the result
		return endingHour;
	}
	
	/**
	 * Gets the ending minute of the current availability slot, based on the slot duration set by {@link me.SyncWise.Android.Widgets.AvailabilityCalendar.AvailabilityCalendarView#SLOT_DURATION AvailabilityCalendarView.SLOT_DURATION}
	 * 
	 * @return	Integer holding the ending minute of the current availability slot.
	 */
	public int getToMinute () {
		// Calculate and return the ending minute of the availability slot based on the starting minute and slot duration
		return ( fromMinute + ( AvailabilityCalendarView.SLOT_DURATION % 60 ) ) % 60;
	}
	
	/**
	 * Getter - {@link #isPreviouslySelected}
	 * 
	 * @return Boolean used to indicate if the current availability slot was previously selected or not.
	 */
	public boolean isPreviouslySelected () {
		return isPreviouslySelected;
	}
	
	/**
	 * Setter - {@link #isPreviouslySelected}
	 * 
	 * @param isPreviouslySelected Boolean used to indicate if the current availability slot was previously selected or not.
	 */
	public void setPreviouslySelected ( final boolean isPreviouslySelected ) {
		this.isPreviouslySelected = isPreviouslySelected;
	}
	
	/**
	 * Getter - {@link #isSelected}
	 * 
	 * @return Boolean used to indicate if the current availability slot is selected or not.
	 */
	public boolean isSelected () {
		return isSelected;
	}
	
	/**
	 * Setter - {@link #isSelected}
	 * 
	 * @param isSelected Boolean used to indicate if the current availability slot is selected or not.
	 */
	public void setSelected ( final boolean isSelected ) {
		this.isSelected = isSelected;
	}
	
	/**
	 * Toggles the availability slot selection.
	 */
	public void toggle () {
		this.isSelected = ! this.isSelected;
	}
	
	/**
	 * Determines if the current availability slot is modified or not.
	 * 
	 * @return	Boolean indicating if the current availability slot is modified or not.
	 */
	public boolean isModified () {
		// Check if the current availability slot is final
		if ( isFinal )
			// A final availability slot can never be modified
			return false;
		// Otherwise the current availability slot is not final
		// Determine if the current availability slot has a previous selection (and hence compare with the current selection)
		// OR simply if the current availability slot is newly selected
		return isPreviouslySelected ? isPreviouslySelected != isSelected : isSelected;
	}
	
	/**
	 * Getter - {@link #text}
	 * 
	 * @return String holding text to be displayed on the calendar slot.
	 */
	public String getText () {
		return text;
	}
	
	/**
	 * Setter - {@link #text}
	 * 
	 * @param text String holding text to be displayed on the calendar slot.
	 */
	public void setText ( final String text ) {
		this.text = text;
	}
	
}