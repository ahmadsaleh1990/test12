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

import java.util.ArrayList;
import java.util.List;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

/**
 * A concrete BaseAdapter that is backed by an array of {@link me.SyncWise.Android.Modules.CyclePlanning.Call Call} objects.
 * 
 * @author Elias
 *
 */
public class CyclePlanningAdapter extends ArrayAdapter < Call > {

	/**
	 * {@link android.view.LayoutInflater} used to instantiate a layout XML file into its corresponding View objects.
	 */
	private final LayoutInflater layoutInflater;
	
	/**
	 * XML Layout (layout) resource id.
	 */
	private final int layout;
	
	/**
	 * Flag used to indicate if the adapter is in the edit mode.<br>
	 * Mainly this flag is used to display / hide the drag handle accordingly in order to use the drag sort list view functionalities. 
	 */
	private boolean isEdit;
	
	/**
	 * Sets the edit mode.<br>
	 * Setting a new mode will take effect instantly.
	 * 
	 * @param edit	Boolean indicating the edit mode status.
	 */
	public void setEdit ( final boolean edit ) {
		// Check if the edit status has changed
		if ( isEdit == edit )
			// No changes, do nothing
			return;
		// Apply the new edit status
		isEdit = edit;
		// Apply new effects
		notifyDataSetChanged ();
	}
	
	/**
	 * Nested class implementation of the view holder pattern used to keep references to children views in order to avoid unnecessary calls to {@link android.view.View#findViewById(int) findViewById(id)} on each row.
	 * 
	 * @author Elias
	 *
	 */
	static class ViewHolder {
		public String callCode;
		public String clientCode;
		public ImageView icon;
		public TextView _code;
		public TextView name;
		public TextView sequence;
		public ImageView dragHandle;
	}
	
	/**
	 * String used to hold the code label.
	 */
	private final String codeLabel;
	
	/**
	 * String used to hold the new line string.
	 */
	private final String newLine = "\n";
	
	/**
	 * String used to hold the cycle label.
	 */
	private final String cycleLabel;
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param layout	XML Layout (layout) resource id.
	 * @param callItems	List of {@link me.SyncWise.Android.Modules.CyclePlanning.Call Call} objects.
	 * @param edit	Boolean indicating the edit mode status.
	 */
	public CyclePlanningAdapter ( Context context , int layout , List < Call > callItems , boolean edit ) {
		// Superclass method call
		super ( context , layout , callItems );
		// Initialize attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
		isEdit = edit;
		codeLabel = AppResources.getInstance ( context ).getString ( context , R.string.code_label );
		cycleLabel = AppResources.getInstance ( context ).getString ( context , R.string.cycle_label );
	}

	/*
	 * Get a View that displays the data at the specified position in the data set.
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView ( int position , View convertView , ViewGroup parent ) {
		// Declare the row view holder
		ViewHolder viewHolder;
		// Check if an inflated view is provided
		if ( convertView == null ) {
			// A new view must be inflated
			convertView = layoutInflater.inflate ( layout , null );
			// Declare and initialize a view holder
			viewHolder = new ViewHolder ();
			// Retrieve a reference to the client icon
			viewHolder.icon = (ImageView) convertView.findViewById ( R.id.icon_client_item );
			// Retrieve a reference to the client code
			viewHolder._code = (TextView) convertView.findViewById ( R.id.label_client_code );
			// Retrieve a reference to the client name
			viewHolder.name = (TextView) convertView.findViewById ( R.id.label_client_name );
			// Retrieve a reference to the cycle sequence
			viewHolder.sequence = (TextView) convertView.findViewById ( R.id.label_cycle_sequence );
			// Retrieve a reference to the cycle sequence
			viewHolder.dragHandle = (ImageView) convertView.findViewById ( R.id.drag_handle );
			// Store the view holder as tag
			convertView.setTag ( viewHolder );
		} // End if
		else
			// An inflated view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) convertView.getTag ();
		
		// Retrieve a reference to the call
		Call call = null;
		try {
			call = getItem ( position );
		} catch ( Exception exception ) {
			// List is modified, exit method
			return convertView;
		} // End of try-catch block
		
		// Store the call code
		viewHolder.callCode = call.getCode ();
		// Store the client code
		viewHolder.clientCode = call.getClient () == null ? "" : call.getClient ().getClientCode ();
		// Build a spannable string out of the client code in order to apply various spans
		SpannableString code = new SpannableString ( codeLabel + " : " + viewHolder.clientCode );
		// Apply style span
		code.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , codeLabel.length () , 0 );
		// Apply color span
		code.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , codeLabel.length () , 0 );		
		// Build a spannable string out of the cycle name in order to apply various spans
		SpannableString cycle = new SpannableString ( cycleLabel + " : " + call.getCycleName () );
		// Apply style span
		cycle.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , cycleLabel.length () , 0 );
		// Apply color span
		cycle.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , cycleLabel.length () , 0 );
		// Build a list of spannable string out of the area(s) in order to apply various spans
		List < SpannableString > areas = new ArrayList < SpannableString > ();
		// Check if the current call has at least one related area
		if ( call.hasAreas () ) {
			// Compute the total number of areas
			List < String > _areaLevels = call.getAreaLevels ();
			List < String > _areas = call.getAreas ();
			int size = _areaLevels.size ();
			// Iterate over all areas
			for ( int i = 0 ; i < size ; i ++ ) {
				// Retrieve the area level description
				String areaLevel = _areaLevels.get ( i );
				// Build a spannable string out of the area in order to apply various spavns
				SpannableString area = new SpannableString ( areaLevel + " : " + _areas.get ( i ) );
				// Apply style span
				area.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , areaLevel.length () , 0 );
				// Apply color span
				area.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , areaLevel.length () , 0 );
				// Add the spannable string to the list
				areas.add ( area );
			} // End for loop
		} // End if
		
		// Concatenate all the spannable strings
		CharSequence concatenation = TextUtils.concat ( code , newLine , cycle );
		for ( SpannableString area : areas )
			concatenation = TextUtils.concat ( concatenation , newLine , area );

		// Display the client code & cycle name
		viewHolder._code.setText ( concatenation , BufferType.SPANNABLE );
		// Display the client name
		viewHolder.name.setText ( call.getClient () == null ? "" : call.getClient ().getClientName () );
		
		// Set the cycle sequence
		viewHolder.sequence.setText ( String.valueOf ( position + 1 ) );
		// Determine if the call is editable
		if ( call.getEditable () ) {
			// The call is editable
			// Display / hide the drag handle accordingly
			viewHolder.dragHandle.setVisibility ( isEdit ? View.VISIBLE : View.GONE );
			// Set the icon alpha accordingly
			viewHolder.icon.setAlpha ( isEdit ? 0.5f : 1f );
		} // End if
		else {
			// The call is NOT editable
			// Hide the drag handle
			viewHolder.dragHandle.setVisibility ( View.GONE );
			// Make the icon opaque
			viewHolder.icon.setAlpha ( 1f );
		} // End if
		
		// Return the view
		return convertView;
	}
	
}