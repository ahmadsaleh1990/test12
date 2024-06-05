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
import java.util.ArrayList;

import me.SyncWise.Android.Database.ClientDivisions;
import me.SyncWise.Android.Database.Clients;
import me.SyncWise.Android.Database.CycleCalls;

/**
 * Class used to represent scheduled cycle call.<br>
 * Additional functionalities over the GreenDao generated class include but are not limited to : related journey and visit flags.
 * 
 * @author Elias
 *
 */
public class Call implements Serializable {

	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * String used to hold the call code, in order to differentiate the current call with others.<br>
	 * The call code is constructed in the following manner :<br>
	 * {@code WEEK & DAY & CLIENT CODE & DAY & DIVISION CODE }
	 */
	private final String code;
	
	/**
	 * Reference to the client object.
	 */
	private Clients client;
	
	/**
	 * Reference to the client division object.
	 */
	private ClientDivisions clientDivision;
	
	/**
	 * List of strings hosting the client barcodes.
	 */
	private ArrayList < String > clientBarcodes;
	
	/**
	 * String hosting the linked journey code (if any).
	 */
	private String journeyCode;
	
	/**
	 * String hosting the call's day abbreviation.
	 */
	private String day;
	
	/**
	 * Flag used to indicate if the call is scheduled (or out of route).
	 */
	private boolean isScheduled;
	
	/**
	 * Flag used to indicate if the call is performed.
	 */
	private boolean isVisited;
	
	/**
	 * Flag used to indicate if the call is cancelled.
	 */
	private boolean isCancelled;
	
	/**
	 * Flag used to indicate if the call (visit performed) or any previous calls for the current journey contains a sales action.
	 */
	private boolean isSales;
	
	/**
	 * Flag used to indicate if the call (visit performed) or any previous calls for the current journey contains a survey.
	 */
	private boolean isSurvey;
	
	/**
	 * Flag used to indicate if a time pass code is required to perform the call.
	 */
	private boolean useTimePasscode;
	
	/**
	 * Getter - {@link #code}
	 * 
	 * @return	String used to hold the call code.
	 */
	public String getCode () {
		// Return the call code
		return code;
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
		// Check if the object is valid and instance of Call
		if ( object != null && object instanceof Call )
			// Compare objects
			isEqual = code.equals ( ( (Call) object ).getCode () );
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
		result = prime * result + ( ( code == null ) ? 0 : code.hashCode () );
		return result;
	}
	
	/**
	 * Updates the current with values from the provided call.<br>
	 * The updated values are
	 * <ol>
	 * <li>The {@link #isVisited} flag.</li>
	 * <li>The {@link #isCancelled} flag.</li>
	 * <li>The {@link #isSales} flag.</li>
	 * <li>The {@link #isSurvey} flag.</li>
	 * </ol>
	 * 
	 * @param call	The updated call object.
	 */
	public void update ( Call call ) {
		// Determine if the provided call is valid
		if ( call == null )
			// Invalid call
			return;
		// Check if both calls have the same code
		if ( ! code.equals ( call.getCode () ) )
			// Invalid call
			return;
		/*
		 * Otherwise, update the call by setting the values below :
		 * - isVisited
		 * - isCancelled
		 * - isSales
		 * - isSurvey
		 */
		isVisited = call.isVisited ();
		isCancelled = call.isCancelled ();
		isSales = call.isSales ();
		isSurvey = call.isSurvey ();
	}
	
	/**
	 * Getter - {@link #client}
	 * 
	 * @return	Reference to the client object.
	 */
	public Clients getClient () {
		// Return the client object
		return client;
	}
	
	/**
	 * Getter - {@link #clientDivision}
	 * 
	 * @return	Reference to the client division object.
	 */
	public ClientDivisions getClientDivision () {
		// Return the client division object
		return clientDivision;
	}
	
	/**
	 * Setter - {@link #clientBarcodes}
	 * 
	 * @param	List of strings hosting the client barcodes.
	 */
	public void setClientBarcodes ( final ArrayList < String > clientBarcodes ) {
		// Set the client barcodes list
		this.clientBarcodes = clientBarcodes;
	}
	
	/**
	 * Getter - {@link #clientBarcodes}
	 * 
	 * @return	List of strings hosting the client barcodes.
	 */
	public ArrayList < String > getClientBarcodes () {
		// Return the client barcodes list
		return clientBarcodes;
	}
	
	/**
	 * Setter - {@link #journeyCode}
	 * 
	 * @param journeyCode	String hosting the linked journey code (if any).
	 */
	public void setJourneyCode ( final String journeyCode ) {
		// Set the journey code
		this.journeyCode = journeyCode;
	}
	
	/**
	 * Getter - {@link #journeyCode}
	 * 
	 * @return	String hosting the linked journey code (if any).
	 */
	public String getJourneyCode () {
		// Return the linked journey code
		return journeyCode;
	}
	
	/**
	 * Setter - {@link #day}
	 * 
	 * @param day	String hosting the call's day abbreviation.
	 */
	public void setDay ( final String day ) {
		// Set the call's day
		this.day = day;
	}
	
	/**
	 * Getter - {@link #day}
	 * 
	 * @return	String hosting the call's day abbreviation.
	 */
	public String getDay () {
		// Return the call's day
		return day;
	}
	
	/**
	 * Setter - {@link #isScheduled}
	 * 
	 * @param isScheduled	Flag used to indicate if the call is scheduled (or out of route).
	 */
	public void setScheduled ( final boolean isScheduled ) {
		// Set the flag
		this.isScheduled = isScheduled;
	}
	
	/**
	 * Getter - {@link #isScheduled}
	 * 
	 * @return	Flag used to indicate if the call is scheduled (or out of route).
	 */
	public boolean isScheduled () {
		// Return flag
		return isScheduled;
	}
	
	/**
	 * Setter - {@link #isVisited}
	 * 
	 * @param isVisited	Flag used to indicate if the call is performed.
	 */
	public void setVisited ( final boolean isVisited ) {
		// Set the flag
		this.isVisited = isVisited;
	}
	
	/**
	 * Getter - {@link #isVisited}
	 * 
	 * @return	Flag used to indicate if the call is performed.
	 */
	public boolean isVisited () {
		// Return flag
		return isVisited;
	}
	
	/**
	 * Setter - {@link #isCancelled}
	 * 
	 * @param isCancelled	Flag used to indicate if the call is cancelled.
	 */
	public void setCancelled ( final boolean isCancelled ) {
		// Set the flag
		this.isCancelled = isCancelled;
	}
	
	/**
	 * Getter - {@link #isVisited}
	 * 
	 * @return	Flag used to indicate if the call is cancelled.
	 */
	public boolean isCancelled () {
		// Return flag
		return isCancelled;
	}
	
	/**
	 * Setter - {@link #isSales}
	 * 
	 * @param isSales	Flag used to indicate if the call (visit performed) or any previous calls for the current journey contains a sales action.
	 */
	public void setSales ( final boolean isSales ) {
		// Set the flag
		this.isSales = isSales;
	}
	
	/**
	 * Getter - {@link #isSales}
	 * 
	 * @return	Flag used to indicate if the call (visit performed) or any previous calls for the current journey contains a sales action.
	 */
	public boolean isSales () {
		// Return flag
		return isSales;
	}
	
	/**
	 * Setter - {@link #isSurvey}
	 * 
	 * @param isSurvey	Flag used to indicate if the call (visit performed) or any previous calls for the current journey contains a survey.
	 */
	public void setSurvey ( final boolean isSurvey ) {
		// Set the flag
		this.isSurvey = isSurvey;
	}
	
	/**
	 * Getter - {@link #isSurvey}
	 * 
	 * @return	Flag used to indicate if the call (visit performed) or any previous calls for the current journey contains a survey.
	 */
	public boolean isSurvey () {
		// Return flag
		return isSurvey;
	}
	
	/**
	 * Setter - {@link #useTimePasscode}
	 * 
	 * @param useTimePasscode	Flag used to indicate if a time pass code is required to perform the call.
	 */
	public void setUseTimePasscode ( final boolean useTimePasscode ) {
		// Set the flag
		this.useTimePasscode = useTimePasscode;
	}
	
	/**
	 * Getter - {@link #useTimePasscode}
	 * 
	 * @return	Flag used to indicate if a time pass code is required to perform the call.
	 */
	public boolean getUseTimePasscode () {
		// Return flag
		return useTimePasscode;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param cycleCall	Reference to the cycle call object, used to compute the code.
	 * @param client	Reference to the client object.
	 * @param clientDivision	Reference to the client division object.
	 */
	public Call ( final CycleCalls cycleCall , final Clients client , final ClientDivisions clientDivision ) {
		// Compute the call code
		code = cycleCall.getWeek () + cycleCall.getDay () + cycleCall.getClientCode () + cycleCall.getDay () + clientDivision.getDivisionCode ();
		// Store the current client & division
		this.client = client;
		this.clientDivision = clientDivision;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param code	String hosting the object code.
	 * @param client	Reference to the client object.
	 * @param clientDivision	Reference to the client division object.
	 */
	public Call ( final String code , final Clients client , final ClientDivisions clientDivision ) {
		// Set the call code
		this.code = code;
		// Store the current client & division
		this.client = client;
		this.clientDivision = clientDivision;
	}
	
}