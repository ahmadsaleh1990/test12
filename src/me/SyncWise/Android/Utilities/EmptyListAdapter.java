/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Utilities;

import me.SyncWise.Android.R;
import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * A concrete BaseAdapter backed by an array of strings which is used to indicate that a list view is empty.
 * 
 * @author Elias
 *
 */
public class EmptyListAdapter extends ArrayAdapter < String > {

	/**
	 * Constructor
	 * @param context	The application context.
	 * @param emptyLabel	String hosting the empty label.
	 */
	public EmptyListAdapter ( Context context , String emptyLabel ) {
		// Superclass method call
		super ( context , R.layout.emtpy_list_item , R.id.empty_list_view , new String [] { emptyLabel } );
	}
	
	/*
	 * Returns true if the item at the specified position is not a separator. (A separator is a non-selectable, non-clickable item).
	 *
	 * @see android.widget.BaseAdapter#isEnabled(int)
	 */
	@Override
	public boolean isEnabled ( int position ) {
		return false;
	}
	
}