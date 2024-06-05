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

import java.lang.ref.WeakReference;
import java.util.List;

import me.SyncWise.Android.Database.Reasons;
import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;

/**
 * A concrete BaseAdapter that is backed by an array of {@link me.SyncWise.Android.Database.Reasons Reasons} objects.
 * 
 * @author Elias
 *
 */
public class ReasonAdapter extends ArrayAdapter < Reasons > {

	/**
	 * Weak reference to the list of of {@link me.SyncWise.Android.Database.Reasons Reasons} objects.
	 */
	private final WeakReference < List < Reasons > > reasons;
	
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
	 * Getter - The list of reasons through the weak reference {@link #reasons}.<br>
	 * <b>Warning :</b> might be null !
	 * 
	 * @return	List of {@link me.SyncWise.Android.Database.Reasons Reasons} objects.
	 */
	public List < Reasons > getReasons () {
		// Return the list of reasons
		return reasons.get ();
	}
	
	/*
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
	 * @param reasons	List of {@link me.SyncWise.Android.Database.Reasons Reasons} objects.
	 */
	public ReasonAdapter ( Context context, int layout , List < Reasons > reasons ) {
		// Superclass method call
		super ( context , layout , reasons );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
		this.reasons = new WeakReference < List < Reasons > > ( reasons );
	}
	
	/*
	 * Indicates whether all the items in this adapter are enabled.
	 *
	 * @see android.widget.BaseAdapter#areAllItemsEnabled()
	 */
	@Override
	public boolean areAllItemsEnabled () {
		return false;
	}
	
	/*
	 * Returns true if the item at the specified position is not a separator.
	 *
	 * @see android.widget.BaseAdapter#isEnabled(int)
	 */
	@Override
	public boolean isEnabled ( int position ) {
		// All positions are enabled except for the first
		if ( position == 0 )
			return false;
		return true;
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
			// Retrieve a reference to the reason label
			viewHolder.label = (TextView) convertView.findViewById ( android.R.id.text1 );
			// Store the view holder as tag
			convertView.setTag ( viewHolder );
		} // End if
		else
			// An inflated view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) convertView.getTag ();

		// Set the reason label
		viewHolder.label.setText ( getItem ( position ).getReasonName () );
			
		// Return the view
		return convertView;
	}
	
	/*
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
			// Retrieve a reference to the calendar label
			viewHolder.label = (TextView) convertView.findViewById ( android.R.id.text1 );
			// Store the view holder as tag
			convertView.setTag ( viewHolder );
		} // End if
		else
			// An inflated view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) convertView.getTag ();

		// Set the calendar label based on the reason position (the first reason with position 0 should be invalid)
		if ( position == 0 )
			// Set the reason label
			viewHolder.label.setText ( getItem ( position ).getReasonName () );
		else {
			// Compute the index
			String index = position + " - ";
			// Build a spannable string out of the string in order to apply various spans
			SpannableString text = new SpannableString ( index + getItem ( position ).getReasonName () );
			// Apply style span (for the index)
			text.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , index.length () , 0 );
			// Set the reason label
			viewHolder.label.setText ( text , BufferType.SPANNABLE );
		} // End else
			
		// Return the view
		return convertView;
	}
	
}