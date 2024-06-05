/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Objective;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.ClientsDao;
import me.SyncWise.Android.Database.ObjectivesDao;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;

/**
 * Adapter that exposes data from a Cursor to a ListView widget.
 * 
 * @author Elias
 *
 */
public class ClientsCursorAdapter extends CursorAdapter {

	/**
	 * {@link android.view.LayoutInflater LayoutInflater} used to instantiate a layout XML file into its corresponding View objects.
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
		public TextView _code;
		public TextView name;
		public TextView notification;
	}
	
	/**
	 * String used to hold the code label.
	 */
	private final String codeLabel;
	
	/**
	 * Constructor
	 * 
	 * @param context	The application context.
	 * @param cursor	Cursor holding a result set returned by a database query.
	 * @param layout	Integer holding the item layout resource id.
	 */
	public ClientsCursorAdapter ( Context context , Cursor cursor , int layout ) {
		// Superclass method call
		super ( context , cursor , false );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
		codeLabel = AppResources.getInstance ( context ).getString ( context , R.string.code_label );
	}
	
	/*
	 * Makes a new view to hold the data pointed to by cursor.
	 *
	 * @see android.widget.CursorAdapter#newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)
	 */
	@Override
	public View newView ( Context context , Cursor cursor , ViewGroup parent ) {
		// Inflate and return a new view hierarchy from the specified xml resource
		return layoutInflater.inflate ( layout , null );
	}
	
	/*
	 * Makes a new view to hold the data pointed to by cursor.
	 *
	 * @see android.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
	 */
	@Override
	public void bindView ( View view , Context context , Cursor cursor ) {
		// Declare a view holder
		ViewHolder viewHolder;
		// Check if a recycled view is provided
		if ( view.getTag () == null ) {
			// Initialize the view holder
			viewHolder = new ViewHolder ();
			// Retrieve a reference to the client code
			viewHolder._code = (TextView) view.findViewById ( R.id.label_client_code );
			// Retrieve a reference to the client name
			viewHolder.name = (TextView) view.findViewById ( R.id.label_client_name );
			// Retrieve a reference to the notification number
			viewHolder.notification = (TextView) view.findViewById ( R.id.label_notification_number );
			// Store the view holder as tag
			view.setTag ( viewHolder );
		} // End if
		else
			// An recycled view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) view.getTag ();
		
		// Build a spannable string out of the client code in order to apply various spans
		SpannableString code = new SpannableString ( codeLabel + " : " + cursor.getString ( cursor.getColumnIndex ( ClientsDao.Properties.ClientCode.columnName ) ) );
		// Apply style span
		code.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , codeLabel.length () , 0 );
		// Apply color span
		code.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , codeLabel.length () , 0 );
		// Display the client code
		viewHolder._code.setText ( code , BufferType.SPANNABLE );
		// Display the client name
		viewHolder.name.setText ( cursor.getString ( cursor.getColumnIndex ( ClientsDao.Properties.ClientName.columnName ) ) );
		
		// Retrieve the number of pending objectives
		int pendingObjectives = cursor.getInt ( cursor.getColumnIndex ( ObjectivesDao.TABLENAME ) );
		// Determine if there is a valid notification number
		if ( pendingObjectives == 0 )
			// Hide the notification number
			viewHolder.notification.setVisibility ( View.GONE );
		// Determine if the notification number is greater than 99
		else if ( pendingObjectives > 99 ) {
			// Indicate a large number of notifications
			// Display the notification number
			viewHolder.notification.setVisibility ( View.VISIBLE );
			viewHolder.notification.setText ( ".." );
		} // End else if
		else {
			// Display the notification number
			viewHolder.notification.setVisibility ( View.VISIBLE );
			viewHolder.notification.setText ( String.valueOf ( pendingObjectives ) );
		} // End else
	}
	
}