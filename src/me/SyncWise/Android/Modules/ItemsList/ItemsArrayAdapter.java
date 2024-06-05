/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.ItemsList;

import java.util.List;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.Items;
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
 * A concrete BaseAdapter that is backed by an array of {@link me.SyncWise.Android.Database.Items Items} objects.
 * 
 * @author Elias
 *
 */
public class ItemsArrayAdapter extends ArrayAdapter < Items > {

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
		public String itemCode;
		public String companyCode;
		public ImageView icon;
		public TextView data;
		public TextView description;
	}
	
	/**
	 * String used to hold the code label.
	 */
	private final String codeLabel;
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param layout	XML Layout (layout) resource id.
	 * @param itemsItems	List of {@link me.SyncWise.Android.Database.Items Items} objects.
	 */
	public ItemsArrayAdapter ( Context context, int layout , List < Items > itemsItems ) {
		// Superclass method call
		super ( context , layout , itemsItems );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
		codeLabel = AppResources.getInstance ( context ).getString ( context , R.string.code_label );
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
			// Retrieve a reference to the item icon
			viewHolder.icon = (ImageView) convertView.findViewById ( R.id.icon_item_item );
			// Retrieve a reference to the item code
			viewHolder.data = (TextView) convertView.findViewById ( R.id.label_item_code );
			// Retrieve a reference to the item description
			viewHolder.description = (TextView) convertView.findViewById ( R.id.label_item_description );
			// Store the view holder as tag
			convertView.setTag ( viewHolder );
		} // End if
		else
			// An inflated view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) convertView.getTag ();
		
		// Store the item code
		viewHolder.itemCode = getItem ( position ).getItemCode ();
		// Store the company code
		viewHolder.companyCode = getItem ( position ).getCompanyCode ();
		// Build a spannable string out of the item code in order to apply various spans
		SpannableString code = new SpannableString ( codeLabel + " : " + getItem ( position ).getItemCode () );
		// Apply style span
		code.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , codeLabel.length () , 0 );
		// Apply color span
		code.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , codeLabel.length () , 0 );
		// Display the item code
		viewHolder.data.setText ( code , BufferType.SPANNABLE );
		// Display the item name
		viewHolder.description.setText ( getItem ( position ).getItemName () );
		// Return the view
		return convertView;
	}
	
}