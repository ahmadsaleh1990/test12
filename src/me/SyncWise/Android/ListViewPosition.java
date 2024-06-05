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

import java.io.Serializable;

import android.view.View;
import android.widget.ListView;

/**
 * Helper Class used to save/restore a list view's position.
 * 
 * @author Elias
 * @sw.todo <b>Note :</b><br>This class is used by simply saving the object as serializable in the {@code outState} bundle via the {@link android.app.Activity#onSaveInstanceState Activity.onSaveInstanceState} method.<br>
 * The list view position can be retrieved and applied to the list view via the {@link #restore(ListView)} method.
 *
 */
public class ListViewPosition implements Serializable {

	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Flag used to determine if the current object is valid.<br>
	 * The current is only valid if valid list view is provided to the constructor.
	 */
	private final boolean isValid;
	
	/**
	 * Integer holding the index of the first visible position in the list view.
	 */
	private final int index;
	
	/**
	 * Integer holding the number of pixels from the top edge of the first item in the list view.
	 */
	private final int top;
	
	/**
	 * Constructor.
	 * 
	 * @param listView	Reference to the list view whose position needs saving.
	 */
	public ListViewPosition ( final ListView listView ) {
		// Determine if the list view is valid
		if ( listView == null ) {
			// Invalid list view
			isValid = false;
			index = 0;
			top = 0;
			return;
		}
		// Otherwise the list view is valid, indicate so by setting the flag
		isValid = true;
		// Store the list view's first visible child position
		index = listView.getFirstVisiblePosition ();
		// Retrieve a reference to the first list view child
		View view = listView.getChildAt ( 0 );
		// Store the child's top edge pixels (if valid)
		top = ( view == null ? 0 : view.getTop () );
	}
	
	/**
	 * Restores the list view's position based on the child index and top edge pixels.
	 * 
	 * @param listView	Reference to the list view whose position must be restored.
	 */
	public void restore ( final ListView listView ) {
		// Determine if the provided list view and current object are valid
		if ( listView != null && isValid )
			// Restore the list view's position
			listView.setSelectionFromTop ( index , top );
	}
	
}