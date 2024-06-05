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

/**
 * Interface used to implement the client filter definition.
 * 
 * @author Elias
 *
 */
public interface ClientFilter {

	/**
	 * Gets the client filter description.
	 * 
	 * @return	String holding the client filter description.
	 */
	public String getDescription ();
	
	/**
	 * Gets the current total filter count.
	 * 
	 * @return	Integer holding the total number of filters.
	 */
	public int getCount ();
	
	/**
	 * Gets the current selected filter count.
	 * 
	 * @return	Integer holding the selected number of filters.
	 */
	public int getSelectionCount ();
	
	/**
	 * Clears the client filter selections.
	 */
	public void clear ();
	
	/**
	 * Gets the client filter child at the indicated position.
	 * 
	 * @param position	Integer holding the client filter child position.
	 * @return	Object holding the client filter child at the indicated position.
	 */
	public Object getChild ( int position );
	
	/**
	 * Gets the filter description of the indicated client filter child position.
	 * 
	 * @param position	Integer holding the client filter child position.
	 * @return	String holding the client filter description.
	 */
	public String getChildDescription ( int position );
	
	/**
	 * Toggles the client filter at the indicated position.
	 * 
	 * @param position	Integer holding the client filter child position.
	 * @return	Boolean indicating if the filter is selected or not after the toggle.
	 */
	public boolean toggleFilter ( int position );
	
	/**
	 * Indicates if the client filter at the indicated position is selected or not.
	 * 
	 * @param position	Integer holding the client filter child position.
	 * @return	Boolean indicating if the client filter at the indicated position is selected or not.
	 */
	public boolean isFilterSelected ( int position );
	
	/**
	 * Creates a backup of the saved selections.
	 */
	public void backup ();
	
	/**
	 * Restores the backup of the saved selections.
	 */
	public void restore ();
	
	/**
	 * Clears the backup of the saved selections.
	 */
	public void clearBackup ();
}