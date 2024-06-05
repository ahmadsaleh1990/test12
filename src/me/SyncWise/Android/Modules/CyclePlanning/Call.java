/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.CyclePlanning;

import java.io.Serializable;
import java.util.ArrayList;

import android.text.TextUtils;

import me.SyncWise.Android.Database.ClientDivisions;
import me.SyncWise.Android.Database.Clients;
import me.SyncWise.Android.Database.CycleCalls;
import me.SyncWise.Android.Database.EntityUtils;

/**
 * Class used to represent scheduled cycle call.<br>
 * Additional functionalities over the GreenDao generated class include but are not limited to : addition and modification flags.
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
	 * {@code CYCLE ID & # & WEEK & DAY & CLIENT CODE & DAY & DIVISION CODE }
	 */
	private final String code;
	
	/**
	 * Reference to the cycle call object.
	 */
	private CycleCalls cycleCall;
	
	/**
	 * A backup to the cycle call object.<br>
	 * This backup is mainly used to perform a comparison later on to deduce if any modifications occurred.
	 */
	private CycleCalls cycleCallBackup;
	
	/**
	 * A {@link me.SyncWise.Android.Database.Clients Clients} object holding a reference to the current client cycle call.
	 */
	private final Clients client;
	
	/**
	 * A {@link me.SyncWise.Android.Database.ClientDivisions ClientDivisions} object holding a reference to the current client division cycle call.
	 */
	private final ClientDivisions clientDivision;
	
	/**
	 * String hosting the cycle name.
	 */
	private String cycleName;
	
	/**
	 * Flag used to indicate if the call has been added by the user.
	 */
	private boolean isAdded;
	
	/**
	 * Flag used to indicate if the call has been modified by the user.
	 */
	private boolean isModified;
	
	/**
	 * Flag used to indicate if the call can be modified by the user.
	 */
	private boolean editable;
	
	/**
	 * List of strings used to host the area levels that this call is related to.
	 */
	private ArrayList < String > areaLevels;
	
	/**
	 * List of strings used to host the areas that this call is related to.
	 */
	private ArrayList < String > areas;
	
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
	 * Getter - {@link #cycleCallBackup}
	 * 
	 * @return	Reference to the call cycle clone object.
	 */
	public CycleCalls getBackup () {
		// Return the cycle call clone object
		return cycleCallBackup;
	}
	
	/**
	 * Getter - {@link #cycleCall}
	 * 
	 * @return	Reference to the call cycle object.
	 */
	public CycleCalls getCycleCall () {
		// Return the cycle call object
		return cycleCall;
	}
	
	/**
	 * Getter - {@link #client}
	 * 
	 * @return	A {@link #client} object holding a reference to the current client cycle call.
	 */
	public Clients getClient () {
		// Return the client object
		return client;
	}
	
	/**
	 * Getter - {@link #clientDivision}
	 * 
	 * @return	A {@link #clientDivision} object holding a reference to the current client division cycle call.
	 */
	public ClientDivisions getClientDivision () {
		// Return the client division object
		return clientDivision;
	}
	
	/**
	 * Gets the client code of the current client call.<br>
	 * If the client object is invalid, an empty string is returned.
	 * 
	 * @return	String holding the client code, or an empty string if invalid.
	 */
	public String getClientCode () {
		// Check if the client object is valid
		if ( client != null )
			// Return the client code
			return client.getClientCode ();
		// Otherwise the client code is invalid
		return "";
	}
	
	/**
	 * Setter - {@link #cycleName}
	 * 
	 * @param cycleName	String hosting the cycle name.
	 */
	public void setCycleName ( final String cycleName ) {
		// Set the cycle name
		this.cycleName = cycleName;
	}
	
	/**
	 * Getter - {@link #cycleName}
	 * 
	 * @return	String hosting the cycle name.
	 */
	public String getCycleName () {
		// Return the cycle name
		return cycleName;
	}
	
	/**
	 * Setter - {@link #isAdded}
	 * 
	 * @param added	Flag used to indicate if the call has been added by the user.
	 */
	public void setAdded ( final boolean added ) {
		// Store flag
		isAdded = added;
	}
	
	/**
	 * Getter - {@link #isAdded}
	 * 
	 * @return	Flag used to indicate if the call has been added by the user.
	 */
	public boolean isAdded () {
		// Return flag
		return isAdded;
	}
	
	/**
	 * Setter - {@link #isModified}
	 * 
	 * @param modified	Flag used to indicate if the call has been modified by the user.
	 */
	public void setModified ( final boolean modified ) {
		// Store flag
		isModified = modified;
	}
	
	/**
	 * Getter - {@link #isModified}
	 * 
	 * @return	Flag used to indicate if the call has been modified by the user.
	 */
	public boolean isModified () {
		// Return flag
		return isModified;
	}
	
	/**
	 * Setter - {@link #editable}
	 * 
	 * @param editable	Flag used to indicate if the call can be modified by the user.
	 */
	public void setEditable ( final boolean editable ) {
		// Store flag
		this.editable = editable;
	}
	
	/**
	 * Getter - {@link #editable}
	 * 
	 * @return	Flag used to indicate if the call can be modified by the user.
	 */
	public boolean getEditable () {
		// Return flag
		return editable;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param cycleCall	Reference to the cycle call object.
	 * @param client	Reference to the client object.
	 * @param clientDivision	Reference to the client division object.
	 */
	public Call ( final CycleCalls cycleCall , final Clients client , final ClientDivisions clientDivision ) {
		// Check if the cycle call and client are valid
		if ( cycleCall == null || client == null ) {
			// Invalid cycle call
			code = "";
			this.client = null;
			this.clientDivision = null;
			this.cycleCall = null;
		}
		else {
			// Compute the call code
			code = cycleCall.getCycleID () + "#" + cycleCall.getWeek () + cycleCall.getDay () + cycleCall.getClientCode () + cycleCall.getDay () + clientDivision.getDivisionCode ();
			// Store the current client and cycle call
			this.cycleCall = cycleCall;
			this.client = client;
			this.clientDivision = clientDivision;
		} // End else
	}
	
	/**
	 * Constructor.<br>
	 * Mainly used if the call is added by the user.
	 * 
	 * @param cycleCall	Reference to the cycle call object.
	 * @param client	Reference to the client object.
	 * @param clientDivision	Reference to the client division object.
	 * @param isAdded	Flag used to indicate if the call has been added by the user.
	 */
	public Call ( final CycleCalls cycleCall , final Clients client , final ClientDivisions clientDivision , final boolean isAdded ) {
		// Superclass method call
		this ( cycleCall , client , clientDivision );
		// Initialize attribute
		this.isAdded = isAdded;
	}
	
	/**
	 * Generates a backup (clone) of the current call state.<br>
	 * No backup is generated if a previous one is built.
	 * @see	Call#clearAndBackup() 
	 */
	public void backup () {
		// Check if the object has no previous backup
		if ( cycleCallBackup != null )
			// Do not backup
			return;
		// Determine if the current cycle call is valid
		if ( cycleCall == null )
			// Invalid cycle call
			return;
		// Otherwise, perform a clone of the current cycle call
		cycleCallBackup = EntityUtils.clone ( CycleCalls.class , cycleCall );
	}
	
	/**
	 * Clears any previously generated backup.
	 */
	public void clearBackup () {
		// Remove clone
		cycleCallBackup = null;
	}
	
	/**
	 * Clears any previously generated backup of the call state and builds a new one.
	 * @see	Call#backup()
	 */
	public void clearAndBackup () {
		// Remove any previous backup
		clearBackup ();
		// Perform a new backup
		backup ();
	}
	
	/**
	 * Setter - {@link #areaLevels} {@link #areas} 
	 * 
	 * @param areaLevels	List of strings used to host the area levels that this call is related to.
	 * @param areas	List of strings used to host the areas that this call is related to.
	 */
	public void setAreas ( final ArrayList < String > areaLevels , final ArrayList < String > areas ) {
		this.areaLevels = areaLevels;
		this.areas = areas;
	}
	
	/**
	 * Determines if the call is related to at least one area.
	 * 
	 * @return	Boolean indicating if the call is related to at least one area.
	 */
	public boolean hasAreas () {
		// Determine if the current call is related to at least one area
		return areaLevels != null;
	}
	
	/**
	 * Getter - {@link #areaLevels}
	 * 
	 * @return	List of strings used to host the area levels that this call is related to.
	 */
	public ArrayList < String > getAreaLevels () {
		return areaLevels;
	}
	
	/**
	 * Getter - {@link #areas}
	 * 
	 * @return	List of strings used to host the areas that this call is related to.
	 */
	public ArrayList < String > getAreas () {
		return areas;
	}
	
	/**
	 * Relates the current call to an area, belonging to an area level.
	 * 
	 * @param areaLevel	String hosting the area level.
	 * @param area	String hosting the area(s).
	 */
	public void addArea ( final String areaLevel , final String area ) {
		// Check if the arguments are valid
		if ( TextUtils.isEmpty ( areaLevel ) || TextUtils.isEmpty ( area ) )
			// Invalid data
			return;
		// Check if the area levels list is valid
		if ( areaLevels == null ) 
			// Initialize the area levels list
			areaLevels = new ArrayList < String > ();
		// Check if the areas list is valid
		if ( areas == null ) 
			// Initialize the areas list
			areas = new ArrayList < String > ();
		// Add the area and its level to the lists
		areaLevels.add ( areaLevel );
		areas.add ( area );
	}

}