/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.SalesSummary;

import java.util.List;

import me.SyncWise.Android.R;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;

/**
 * A concrete BaseAdapter that is backed by an array of {@link me.SyncWise.Android.Modules.Summary.Summary Summary} objects.
 * 
 * @author Elias
 *
 */
public class Summary2Adapter extends ArrayAdapter < Summary2 > {

	/**
	 * {@link android.view.LayoutInflater} used to instantiate a layout XML file into its corresponding View objects.
	 */
	private final LayoutInflater layoutInflater;
	
	/**
	 * XML Layout (layout) resource id.
	 */
	private final int layout;
	
	/**
	 * XML Layout (layout) resource id.
	 */
	private int dropDownLayout;
	
	/**
	 * Integer holding the deep sky blue color.
	 */
	private final int colorDeepSkyBlue;
	
	/**
	 * Sets the layout resource to create the drop down views.
	 *
	 * @see android.widget.ArrayAdapter#setDropDownViewResource(int)
	 */
	@Override
	public void setDropDownViewResource ( int resource ) {
		// Superclass method call
		super.setDropDownViewResource ( resource );
		// Store the drop down layout
		this.dropDownLayout = resource;
	}
	
	/**
	 * Nested class implementation of the view holder pattern used to keep references to children views in order to avoid unnecessary calls to {@link android.view.View#findViewById(int) findViewById(id)} on each row.
	 * 
	 * @author Elias
	 *
	 */
	public static class ViewHolder {
		public TextView label;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param layout	XML Layout (layout) resource id.
	 * @param summaries	List of {@link me.SyncWise.Android.Modules.Summary.Summary Summary} objects.
	 */
	public Summary2Adapter ( Context context, int layout , List < Summary2 > summaries ) {
		// Superclass method call
		super ( context , layout , summaries );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
		colorDeepSkyBlue = context.getResources ().getColor ( R.color.DeepSkyBlue );
	}
	
	/**
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
			// Retrieve a reference to the reason label
			viewHolder.label = (TextView) convertView.findViewById ( android.R.id.text1 );
			// Store the view holder as tag
			convertView.setTag ( viewHolder );
		} // End if
		else
			// An inflated view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) convertView.getTag ();
		
		// Build a spannable string out of the summary description in order to apply various spans
		SpannableString summary = new SpannableString ( getItem ( position ).getDescription () );
		// Apply style span
		summary.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , summary.length () , 0 );
		// Set the summary label
		viewHolder.label.setText ( summary , BufferType.SPANNABLE );
		
		// Return the view
		return convertView;
	}
	
	/**
	 * Get a View that displays in the drop down popup the data at the specified position in the data set.
	 *
	 * @see android.widget.ArrayAdapter#getDropDownView(int, android.view.View, android.view.ViewGroup)
	 */
	public View getDropDownView ( int position , View convertView , ViewGroup parent ) {
		// Declare the row view holder
		ViewHolder viewHolder;
		// Check if an inflated view is provided
		if ( convertView == null ) {
			// A new view must be inflated
			convertView = layoutInflater.inflate ( dropDownLayout , null );
			// Declare and initialize a view holder
			viewHolder = new ViewHolder ();
			// Retrieve a reference to the reason label
			viewHolder.label = (TextView) convertView.findViewById ( android.R.id.text1 );
			// Store the view holder as tag
			convertView.setTag ( viewHolder );
		} // End if
		else
			// An inflated view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) convertView.getTag ();
		
		// Compute the item index
		final String index = String.valueOf ( position + 1 ) + " - ";
		// Build a spannable string out of the index and summary description in order to apply various spans
		SpannableString summary = new SpannableString ( index + getItem ( position ).getDescription () );
		// Apply style span
		summary.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , summary.length () , 0 );
		// Apply color span
		summary.setSpan ( new ForegroundColorSpan ( colorDeepSkyBlue ) , 0 , index.length () , 0 );
		// Set the summary label
		viewHolder.label.setText ( summary , BufferType.SPANNABLE );
		
		// Return the view
		return convertView;
	}
	
}