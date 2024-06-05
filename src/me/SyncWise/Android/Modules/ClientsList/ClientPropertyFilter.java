/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.ClientsList;

import java.io.Serializable;
import java.util.ArrayList;

import me.SyncWise.Android.Database.ClientPropertyLevels;
import me.SyncWise.Android.Database.ClientPropertyValues;

/**
 * Class used to represent the client filters related to the client properties.
 * 
 * @author Elias
 *
 */
public class ClientPropertyFilter implements ClientFilter , Serializable {

	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * {@link me.SyncWise.Android.Database.ClientPropertyLevels ClientPropertyLevels} object used to determine the current client filter.
	 */
	private final ClientPropertyLevels level;
	
	/**
	 * List of {@link me.SyncWise.Android.Database.ClientPropertyValues ClientPropertyValues} objects used to determine the current client filter values.
	 */
	private final ArrayList < ClientPropertyValues > values;
	
	/**
	 * List of strings holding the client property value codes of the selected client property values.<br>
	 * What differs this list from {@link #selectedValueCodes} is that this list is used as a backup list in order to store / retrieve the saved selection.<br>
	 * In this manner the user may change the client filter and then choose not to save it.
	 */
	private ArrayList < String > backup;
	
	/**
	 * List of strings holding the client property value codes of the selected client property values.
	 */
	private ArrayList < String > selectedValueCodes;
	
	/**
	 * Constructor.
	 * 
	 * @param level	{@link me.SyncWise.Android.Database.ClientPropertyLevels ClientPropertyLevels} object used to determine the current client filter.
	 * @param values	List of {@link me.SyncWise.Android.Database.ClientPropertyValues ClientPropertyValues} objects used to determine the current client filter values.
	 */
	public ClientPropertyFilter ( final ClientPropertyLevels level , final ArrayList < ClientPropertyValues > values ) {
		// Initialize attributes
		this.level = level;
		this.values = values;
		this.selectedValueCodes = new ArrayList < String > ();
	}

	/*
	 * Returns the current total filter count.
	 *
	 * @see me.SyncWise.Android.Modules.ClientsList.ClientFilter#getCount()
	 */
	@Override
	public int getCount () {
		// Return the total number of client property values
		return values.size ();
	}
	
	/*
	 * Gets the current selected filter count.
	 *
	 * @see me.SyncWise.Android.Modules.ClientsList.ClientFilter#getSelectionCount()
	 */
	@Override
	public int getSelectionCount () {
		// Return the number of selected value codes
		return selectedValueCodes.size ();
	}
	
	/*
	 * Clears the client filter selections.
	 *
	 * @see me.SyncWise.Android.Modules.ClientsList.ClientFilter#clear()
	 */
	@Override
	public void clear () {
		// Clear the list of selected value codes
		selectedValueCodes.clear ();
	}

	/*
	 * Gets the client filter description.
	 *
	 * @see me.SyncWise.Android.Modules.ClientsList.ClientFilter#getDescription()
	 */
	@Override
	public String getDescription () {
		// Return the client property level description
		return level.getClientPropertyLevelDescription ();
	}

	/*
	 * Gets the filter description of the indicated client filter child position.
	 *
	 * @see me.SyncWise.Android.Modules.ClientsList.ClientFilter#getChildDescription(int)
	 */
	@Override
	public Object getChild ( int position ) {
		try {
			// Return the appropriate client property value
			return values.get ( position );
		} catch ( Exception exception ) {
			// An exception occurred : either the list or the position is invalid
			return null;
		} // End of try-catch block
	}
	
	/*
	 * Gets the filter description of the indicated client filter child position.
	 *
	 * @see me.SyncWise.Android.Modules.ClientsList.ClientFilter#getChildDescription(int)
	 */
	@Override
	public String getChildDescription ( int position ) {
		try {
			// Return the appropriate client property value description
			return values.get ( position ).getClientPropertyValueDescription ();
		} catch ( Exception exception ) {
			// An exception occurred : either the list or the position is invalid
			return null;
		} // End of try-catch block
	}

	/*
	 * Toggles the client filter at the indicated position.
	 *
	 * @see me.SyncWise.Android.Modules.ClientsList.ClientFilter#toggleFilter(int)
	 */
	@Override
	public boolean toggleFilter ( int position ) {
		try {
			// Retrieve the client property value code
			String code = values.get ( position ).getClientPropertyValueCode ();
			// Compute the code index in the selected codes list (if any)
			int index = selectedValueCodes.indexOf ( code );
			// Determine if the code is selected
			if ( index == -1 )
				// Add the code to the selected codes list
				selectedValueCodes.add ( code );
			else
				// Otherwise remove the code from the selected codes list
				selectedValueCodes.remove ( index );
			// Returns whether the filter was added or not
			return index == -1;
		} catch ( Exception exception ) {
			// An exception occurred : either the list or the position is invalid
			return false;
		} // End of try-catch block
	}

	/*
	 * Indicates if the client filter at the indicated position is selected or not.
	 *
	 * @see me.SyncWise.Android.Modules.ClientsList.ClientFilter#isFilterSelected(int)
	 */
	@Override
	public boolean isFilterSelected ( int position ) {
		try {
			// Retrieve the client property value code
			String code = values.get ( position ).getClientPropertyValueCode ();
			// Compute the code index in the selected codes list (if any)
			int index = selectedValueCodes.indexOf ( code );
			// Determine if the code is added to the selected codes list or not, and return  the result
			return index != -1;
		} catch ( Exception exception ) {
			// An exception occurred : either the list or the position is invalid
			return false;
		} // End of try-catch block
	}

	/*
	 * Creates a backup of the selected value codes list.
	 *
	 * @see me.SyncWise.Android.Modules.ClientsList.ClientFilter#backup()
	 */
	@Override
	public void backup () {
		// Make a backup from the main list
		backup = new ArrayList < String > ( selectedValueCodes );
	}
	
	/*
	 * Restores the backup of the selected value codes list.
	 *
	 * @see me.SyncWise.Android.Modules.ClientsList.ClientFilter#restore()
	 */
	@Override
	public void restore () {
		// Check if the backup list is valid
		if ( backup != null ) {
			// Restore the backup list and then clear it
			selectedValueCodes = backup;
			backup = null;
		} // End if
	}
	
	/*
	 * Clears the backup of the selected value codes list.
	 *
	 * @see me.SyncWise.Android.Modules.ClientsList.ClientFilter#clearBackup()
	 */
	@Override
	public void clearBackup () {
		// Clear the backup of the main list
		backup = null;
	}
	
}