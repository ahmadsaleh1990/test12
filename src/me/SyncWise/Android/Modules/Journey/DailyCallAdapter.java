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

import java.util.List;

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
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Modules.ClientsList.ClientsArrayAdapter;

/**
 * A concrete BaseAdapter that is backed by an array of {@link me.SyncWise.Android.Modules.Journey.Call Call} objects.
 * 
 * @author Elias
 *
 */
public class DailyCallAdapter extends ArrayAdapter < Call > {

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
	static class ViewHolder extends ClientsArrayAdapter.ViewHolder {
		public View visitBar;
		public TextView sequence;
	}
	
	/**
	 * String used to hold the code label.
	 */
	private final String codeLabel;
	
	/**
	 * String used to hold the company label.
	 */
	private final String companyLabel;
	
	/**
	 * String used to hold the division label.
	 */
	private final String divisionLabel;
	
	/**
	 * String used to hold the new line character.
	 */
	private final String newLine = "\n";
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param layout	XML Layout (layout) resource id.
	 * @param callItems	List of {@link me.SyncWise.Android.Modules.Journey.Call Call} objects.
	 */
	public DailyCallAdapter ( Context context , int layout , List < Call > callItems ) {
		// Superclass method call
		super ( context , layout , callItems );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
		codeLabel = AppResources.getInstance ( context ).getString ( context , R.string.code_label );
		companyLabel = AppResources.getInstance ( context ).getString ( context , R.string.company_label );
		divisionLabel = AppResources.getInstance ( context ).getString ( context , R.string.division_label );
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
			// Retrieve a reference to the visit bar
			viewHolder.visitBar = convertView.findViewById ( R.id.visit_bar );
			// Retrieve a reference to the client icon
			viewHolder.icon = (ImageView) convertView.findViewById ( R.id.icon_client_item );
			// Retrieve a reference to the client code
			viewHolder.data = (TextView) convertView.findViewById ( R.id.label_client_code );
			// Retrieve a reference to the client name
			viewHolder.name = (TextView) convertView.findViewById ( R.id.label_client_name );
			// Retrieve a reference to the cycle sequence
			viewHolder.sequence = (TextView) convertView.findViewById ( R.id.label_cycle_sequence );
			// Store the view holder as tag
			convertView.setTag ( viewHolder );
		} // End if
		else
			// An inflated view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) convertView.getTag ();
		
		// Determine if the client is visited
		if ( getItem ( position ).isVisited () )
			// Indicate that the client has been performed
			viewHolder.visitBar.setBackgroundColor ( Color.GREEN );
		// Determine if the client visit is cancelled
		else if ( getItem ( position ).isCancelled () )
			// Indicate that the client visit has been cancelled
			viewHolder.visitBar.setBackgroundColor ( Color.RED );
		else
			// Indicate that the client visit can be visited
			viewHolder.visitBar.setBackgroundColor ( Color.BLUE );
		
		// Build a spannable string out of the client code in order to apply various spans
		SpannableString clientCode = new SpannableString ( codeLabel + " : " + getItem ( position ).getClient ().getClientCode () );
		// Apply style span
		clientCode.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , codeLabel.length () , 0 );
		// Apply color span
		clientCode.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , codeLabel.length () , 0 );
		
		// Build a spannable string out of the company name in order to apply various spans
		SpannableString companyData = new SpannableString ( companyLabel + " : " + getItem ( position ).getClient ().getCompanyName () );
		// Apply style span
		companyData.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , companyLabel.length () , 0 );
		// Apply color span
		companyData.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , companyLabel.length () , 0 );
		
		// Build a spannable string out of the division name in order to apply various spans
		SpannableString divisionData = new SpannableString ( divisionLabel + " : " + getItem ( position ).getClient ().getDivisionName () );
		// Apply style span
		divisionData.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , divisionLabel.length () , 0 );
		// Apply color span
		divisionData.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , divisionLabel.length () , 0 );
		
		// Set the client data text
		viewHolder.data.setText ( TextUtils.concat ( clientCode , newLine , companyData , newLine , divisionData ) , BufferType.SPANNABLE );
		// Set the name text
		viewHolder.name.setText ( getItem ( position ).getClient ().getClientName () );
		// Set the cycle sequence
		viewHolder.sequence.setText ( String.valueOf ( position + 1 ) );
		
		// Return the view
		return convertView;
	}
	
}