/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Reason;

import java.util.List;

import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.Reasons;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
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
* A concrete BaseAdapter that is backed by an array of {@link me.SyncWise.Android.Database.Reasons Reasons} objects.
* 
* @author Elias
*
*/
public class ReasonAdapter extends ArrayAdapter < Reasons >{

	/**
	 * {@link android.view.LayoutInflater} used to instantiate a layout XML file into its corresponding View objects.
	 */
	private final LayoutInflater layoutInflater;
	
	/**
	 * XML Layout (layout) resource id.
	 */
	private final int layout;
	
	/**
	 * Nested class implementation of the view holder pattern used to keep references to children views in order to avoid unnecessary calls to {@link android.view.View#findViewById(int) findViewById(id)} on each row.
	 * 
	 * @author Elias
	 *
	 */
	public static class ViewHolder {
		public ImageView selection;
		public TextView label;
		public ImageView arrow;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param layout	XML Layout (layout) resource id.
	 * @param reasons	List of {@link me.SyncWise.Android.Database.Reasons Reasons} objects.
	 */
	public ReasonAdapter ( Context context , int layout , List < Reasons > reasons ) {
		// Superclass method 
		super ( context , layout , reasons );
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
		// Declare the row view holder
		ViewHolder viewHolder;
		// Check if an inflated view is provided
		if ( convertView == null ) {
			// A new view must be inflated
			convertView = layoutInflater.inflate ( layout , null );
			// Declare and initialize a view holder
			viewHolder = new ViewHolder ();
			// Retrieve a reference to the selection icon
			viewHolder.selection = (ImageView) convertView.findViewById ( R.id.icon_selection );
			// Retrieve a reference to the reason label
			viewHolder.label = (TextView) convertView.findViewById ( R.id.label_reason );
			// Retrieve a reference to the arrow icon
			viewHolder.arrow = (ImageView) convertView.findViewById ( R.id.icon_arrow );
			// Store the view holder as tag
			convertView.setTag ( viewHolder );
		} // End if
		else
			// An inflated view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) convertView.getTag ();
		
		// Display the default selection icon (not checked)
		viewHolder.selection.setImageResource ( R.drawable.selection );
		// Compute the index
		String index = ( position + 1 ) + " - ";
		// Build a spannable string out of the reason text in order to apply various spans
		SpannableString reason = new SpannableString ( index + getItem ( position ).getReasonName () );
		// Apply color span
		reason.setSpan ( new ForegroundColorSpan ( Color.BLUE ) , 0 , index.length () , 0 );
		// Apply style span
		reason.setSpan ( new StyleSpan ( Typeface.BOLD ) , index.length () , reason.length () , 0 );
		// Display the reason label
		viewHolder.label.setText ( reason.toString () , BufferType.SPANNABLE );
		// Display the default arrow icon (black)
		viewHolder.arrow.setImageResource ( R.drawable.arrow_right_black );
		// Return the view
		return convertView;
	}
	
}