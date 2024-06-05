/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.SectionsList;

import me.SyncWise.Android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * A concrete BaseAdapter that is backed by an array of {@link me.SyncWise.Android.SectionsList.Section Section} objects.
 * 
 * @author Elias
 *
 */
public class SectionAdapter extends ArrayAdapter < Section > {

	/**
	 * {@link android.view.LayoutInflater} used to instantiate a layout XML file into its corresponding View objects.
	 */
	private final LayoutInflater layoutInflater;
	
	/**
	 * XML Layout (layout) resource id.
	 */
	private final int layout;
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 */
	public SectionAdapter ( Context context ) {
		// Superclass method call
		super ( context , 0 );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = R.layout.section_item;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param layout	XML Layout (layout) resource id.
	 */
	public SectionAdapter ( Context context , final int layout ) {
		// Superclass method call
		super ( context , 0 );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
	}
	
	/*
	 * Get a View that displays the data at the specified position in the data set.
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView ( int position , View convertView , ViewGroup parent ) {
		// Check if an inflated view is provided
		if ( convertView == null )
			// A new view must be inflated
			convertView = layoutInflater.inflate ( layout , null );

		// Retrieve a reference to the text view
		TextView section = (TextView) convertView;
		// Set the section name
		section.setText ( getItem ( position ).getName () );
		// Set the section color (if any)
		if ( getItem ( position ).getTextColor () != null )
			section.setTextColor ( getItem ( position ).getTextColor () );
		// Set the section icon (if any)
		if ( getItem ( position ).getIconResourceID () != null )
			section.setCompoundDrawablesWithIntrinsicBounds ( getItem ( position ).getIconResourceID () , 0 , 0 , 0 );
		// Set the section background color (if any)
		if ( getItem ( position ).getBackgroundColor () != null )
			section.setBackgroundColor ( getItem ( position ).getBackgroundColor () );
		// Set the section background drawable (if any)
		if ( getItem ( position ).getBackgroundResourceID () != null )
			section.setBackgroundResource ( getItem ( position ).getBackgroundResourceID () );
		// Return the view
		return convertView;
	}
	
}