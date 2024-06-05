/**
 * Copyright 2014 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Messages;

import java.util.Calendar;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.DateTime;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.ClientsDao;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

/**
 * Adapter that exposes data from a Cursor to a ListView widget.
 * 
 * @author Elias
 *
 */
public class MessagesAdapter extends CursorAdapter {

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
	static class ViewHolder {
		public ImageView icon;
		public TextView message;
	}
	
	/**
	 * String used to hold the new line character.
	 */
	private final String newLine = "\n";
	
	/**
	 * String used to hold the date label.
	 */
	private final String dateLabel;
	
	/**
	 * String used to hold the client label.
	 */
	private final String clientLabel;

	/**
	 * String used to hold the title label.
	 */
	private final String titleLabel;
	
	/**
	 * String used to hold the message label.
	 */
	private final String messageLabel;
	
	/**
	 * Constructor
	 * 
	 * @param context	The application context.
	 * @param cursor	Cursor holding a result set returned by a database query.
	 * @param layout	Integer holding the item layout resource id.
	 */
	public MessagesAdapter ( Context context , Cursor cursor , int layout ) {
		// Superclass method call
		super ( context , cursor , false );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
		dateLabel = AppResources.getInstance ( context ).getString ( context , R.string.date_label ) + " : ";
		clientLabel = AppResources.getInstance ( context ).getString ( context , R.string.messages_activity_client_label ) + " : ";
		titleLabel = AppResources.getInstance ( context ).getString ( context , R.string.title_label ) + " : ";
		messageLabel = AppResources.getInstance ( context ).getString ( context , R.string.note_label ) + " : ";
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
			// Retrieve a reference to the icon
			viewHolder.icon = (ImageView) view.findViewById ( R.id.icon_message_type );
			// Retrieve a reference to the message
			viewHolder.message = (TextView) view.findViewById ( R.id.label_message_description );
			// Store the view holder as tag
			view.setTag ( viewHolder );
		} // End if
		else
			// An recycled view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) view.getTag ();
		
		// Retrieve the message date
		Calendar _messageDate = Calendar.getInstance ();
		_messageDate.setTimeInMillis ( cursor.getLong ( cursor.getColumnIndex ( "StampDate" ) ) );
		// Retrieve the client name
		String _clientName = cursor.getString ( cursor.getColumnIndex ( ClientsDao.Properties.ClientName.columnName ) );
		// Retrieve the message title
		String _title = cursor.getString ( cursor.getColumnIndex ( "Title" ) );
		// Retrieve the message body
		String _note = cursor.getString ( cursor.getColumnIndex ( "Body" ) );
		// Retrieve the message type
		int type = cursor.getInt ( cursor.getColumnIndex ( "Type" ) );
		
		// Build a spannable string out of the message date in order to apply various spans
		SpannableString messageDate = new SpannableString ( dateLabel + DateTime.getFullDateTime ( context , _messageDate ) );
		// Apply style span
		messageDate.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , dateLabel.length () , 0 );
		// Apply underline span
		messageDate.setSpan ( new UnderlineSpan () , 0 , dateLabel.length () - 1 , 0 );
		// Apply color span
		messageDate.setSpan ( new ForegroundColorSpan ( Color.BLUE ) , dateLabel.length () , messageDate.length () , 0 );
		
		// Build a spannable string out of the message title in order to apply various spans
		SpannableString title = null;
		// Check if the title is valid
		if ( ! TextUtils.isEmpty ( _title ) ) {
			// Build a spannable string out of the message title in order to apply various spans
			title = new SpannableString ( titleLabel + _title );
			// Apply style span
			title.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , titleLabel.length () , 0 );
			// Apply underline span
			title.setSpan ( new UnderlineSpan () , 0 , titleLabel.length () - 1 , 0 );
			// Apply color span
			title.setSpan ( new ForegroundColorSpan ( Color.BLUE ) , titleLabel.length () , title.length () , 0 );
		} // End if
		
		// Build a spannable string out of the client name in order to apply various spans
		SpannableString clientName = null;
		// Check if the title is valid
		if ( ! TextUtils.isEmpty ( _clientName ) ) {
			// Build a spannable string out of the client name in order to apply various spans
			clientName = new SpannableString ( clientLabel + _clientName );
			// Apply style span
			clientName.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , clientLabel.length () , 0 );
			// Apply underline span
			clientName.setSpan ( new UnderlineSpan () , 0 , clientLabel.length () - 1 , 0 );
			// Apply color span
			clientName.setSpan ( new ForegroundColorSpan ( Color.BLUE ) , clientLabel.length () , clientName.length () , 0 );
		} // End if
		
		// Build a spannable string out of the message note in order to apply various spans
		SpannableString note = new SpannableString ( messageLabel + _note );
		// Apply style span
		note.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , messageLabel.length () , 0 );
		// Apply underline span
		note.setSpan ( new UnderlineSpan () , 0 , messageLabel.length () - 1 , 0 );
		// Apply color span
		note.setSpan ( new ForegroundColorSpan ( Color.BLUE ) , messageLabel.length () , note.length () , 0 );
		
		// Declare and initialize the message final description
		CharSequence finalDescription = TextUtils.concat ( messageDate , ( TextUtils.isEmpty ( _clientName ) ? "" : TextUtils.concat ( newLine , clientName ) ) , ( TextUtils.isEmpty ( _title ) ? "" : TextUtils.concat ( newLine , title ) ) , newLine , note );
		
		// Determine the message type
		switch ( type ) {
		case 1:
			// Mark the message as received
			viewHolder.icon.setImageResource ( R.drawable.received );
			break;
		case 2:
			// Mark the message as sent
			viewHolder.icon.setImageResource ( R.drawable.sent );
			break;
		default:
			// Remove the icon
			viewHolder.icon.setImageResource ( 0 );
			break;
		}
		
		// Display the message final description
		viewHolder.message.setText ( finalDescription , BufferType.SPANNABLE );
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