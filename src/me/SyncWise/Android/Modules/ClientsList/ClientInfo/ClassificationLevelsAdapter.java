/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.ClientsList.ClientInfo;

import java.util.List;

import me.SyncWise.Android.Database.ClientItemClassificationLevels;
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
 * A concrete BaseAdapter that is backed by an array of {@link me.SyncWise.Android.Database.ClientItemClassificationLevels ClientItemClassificationLevels} objects.
 * 
 * @author Elias
 *
 */
public class ClassificationLevelsAdapter extends ArrayAdapter < ClientItemClassificationLevels > {

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
	 * @param classificationLevels	List of {@link me.SyncWise.Android.Database.ClientItemClassificationLevels ClientItemClassificationLevels} objects.
	 */
	public ClassificationLevelsAdapter ( Context context, int layout , List < ClientItemClassificationLevels > classificationLevels ) {
		// Superclass method call
		super ( context , layout , classificationLevels );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
	}
	
	/**
	 * Gets the custom view used to display the classification level at the specified position.
	 * 
	 * @param position	Integer hosting the custom view's position.
	 * @param convertView	The recycled view (if any) or {@code NULL}.
	 * @param parent	ViewGroup reference that will hold the custom view.
	 * @param layout	Integer hosting the layout resource ID.
	 * @return	The custom view.
	 */
	public View getCustomView ( int position , View convertView , ViewGroup parent , int layout ) {
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
		
		// Build a spannable string out of the classification level description in order to apply various spans
		SpannableString level = new SpannableString ( getItem ( position ).getClassificationLevelDescription () );
		// Apply style span
		level.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , level.length () , 0 );
		// Set the classification level
		viewHolder.label.setText ( level , BufferType.SPANNABLE );
		
		// Return the view
		return convertView;
	}
	
	/*
	 * Get a View that displays the data at the specified position in the data set.
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView ( int position , View convertView , ViewGroup parent ) {
		return getCustomView ( position , convertView , parent , layout );
	}
	
	/*
	 * Get a View that displays in the drop down popup the data at the specified position in the data set.
	 *
	 * @see android.widget.ArrayAdapter#getDropDownView(int, android.view.View, android.view.ViewGroup)
	 */
	public View getDropDownView ( int position , View convertView , ViewGroup parent ) {
		return getCustomView ( position , convertView , parent , dropDownLayout );
	}
	
}