/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Journey;

import java.io.Serializable;
import java.util.Calendar;

import android.content.Context;
import android.text.TextUtils;

import me.SyncWise.Android.DateTime;
import me.SyncWise.Android.Database.JourneysUtils;
import me.SyncWise.Android.Database.Reasons;

/**
 * Class used to represent a journey reason.
 * 
 * @author Elias
 *
 */
public class JourneyReason implements Serializable {

	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * String holding the calendar label of the current journey.
	 */
	private String calendarLabel;
	
	/**
	 * String holding the current journey code.
	 */
	private String journeyCode;
	
	/**
	 * String holding the selected reason for the current journey reason.
	 */
	private String reasonCode;
	
	/**
	 * Flag used to determine if the current journey item is selected.
	 */
	private boolean selected;
	
	/**
	 * Getter - {@link #calendarLabel}
	 * 
	 * @return	String holding the calendar label of the current journey.
	 */
	public String getCalendarLabel () {
		// Return the calendar label
		return calendarLabel;
	}
	
	/**
	 * Getter - {@link #journeyCode}
	 * 
	 * @return	String holding the current journey code.
	 */
	public String getJourneyCode () {
		// Return the journey code
		return journeyCode;
	}
	
	/**
	 * Setter - {@link #reasonCode}
	 * 
	 * @param reason	String holding the selected reason for the current journey reason.
	 */
	public void setReasonCode ( final Reasons reason ) {
		// Check if the provided reason is valid
		if ( reason == null )
			// Invalid reason
			return;
		// Otherwise the reason is valid, set the reason code
		reasonCode = reason.getReasonCode ();
	}
	
	/**
	 * Clears the stored reason code.
	 */
	public void clearReasonCode () {
		// Clear the reason code
		reasonCode = null;
	}
	
	/**
	 * Getter - {@link #reasonCode}
	 * 
	 * @return	String holding the selected reason for the current journey reason.
	 */
	public String getReasonCode () {
		// Return the reason code
		return reasonCode;
	}
	
	/**
	 * Determines if the current journey reason has a valid reason.
	 * 
	 * @return	Boolean indicating if the current journey has a valid reason.
	 */
	public boolean hasReason () {
		// Check if the reason code is valid
		if ( TextUtils.isEmpty ( reasonCode ) )
			// No reasons stored
			return false;
		// Otherwise a reason is stored
		return true;
	}
	
	/**
	 * Setter - {@link #selected}
	 * 
	 * @param selected	Boolean indicating whether the current journey item is selected or not.
	 */
	public void setSelected ( final boolean selected ) {
		// Set the flag
		this.selected = selected;
	}
	
	/**
	 * Getter - {@link #selected}
	 * 
	 * @return	Boolean indicating whether the current journey item is selected or not.
	 */
	public boolean getSelected () {
		// Return the flag
		return selected;
	}
	
	/**
	 * Constructor
	 * 
	 * @param context	The application context.
	 * @param prefixID	Integer holding the user prefix ID.
	 * @param calendar	{@link java.util.Calendar Calendar} object representing the current journey.
	 */
	public JourneyReason ( final Context context , final int prefixID , final Calendar calendar ) {
		// Initialize attributes
		journeyCode = JourneysUtils.getJourneyCode ( prefixID , calendar );
		calendarLabel = DateTime.getFullDate ( context , calendar );
	}
	
}