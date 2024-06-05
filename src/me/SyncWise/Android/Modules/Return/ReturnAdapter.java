/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Return;

import java.util.Calendar;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.DateTime;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.StatusUtils;
import me.SyncWise.Android.Database.TransactionHeadersDao;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
public class ReturnAdapter extends CursorAdapter {

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
		public String transactionCode;
		public long visitID;
		public TextView	data;
		public CheckBox checkBox;
		public ImageView sync;
		public boolean isFinal;
	}
	
	/**
	 * String used to hold the code label.
	 */
	private final String codeLabel;
	
	/**
	 * String used to hold the date label.
	 */
	private final String dateLabel;
	
	/**
	 * String used to hold the new line string.
	 */
	protected final String newLine = "\n";
	
	/**
	 * String used to hold the status label.
	 */
	protected final String statusLabel;
	
	/**
	 * String used to hold the deleted label
	 */
	protected final String deletedLabel;
	
	/**
	 * Integer used to hold the brown color value.
	 */
	private final int brownColor;
	
	/**
	 * String used to hold the pending label.
	 */
	private final String pendingLabel;
	
	/**
	 * String used to hold the returned label.
	 */
	private final String returnedLabel;
	
	/**
	 * Constructor
	 * 
	 * @param context	The application context.
	 * @param cursor	Cursor holding a result set returned by a database query.
	 * @param layout	Integer holding the item layout resource id.
	 */
	@SuppressLint("DefaultLocale") 
	public ReturnAdapter ( Context context , Cursor cursor , int layout ) {
		// Superclass method call
		super ( context , cursor , false );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
		codeLabel = AppResources.getInstance ( context ).getString ( context , R.string.code_label ) + " : ";
		dateLabel = AppResources.getInstance ( context ).getString ( context , R.string.date_label ) + " : ";
    	statusLabel = AppResources.getInstance ( context ).getString ( context , R.string.status_label ) + " : ";
    	deletedLabel = AppResources.getInstance ( context ).getString ( context , R.string.deleted_label ).toUpperCase ();
    	brownColor = context.getResources ().getColor ( R.color.Brown );
		pendingLabel = AppResources.getInstance ( context ).getString ( context , R.string.return_status_pending_label );
		returnedLabel = AppResources.getInstance ( context ).getString ( context , R.string.return_status_loaded_label );
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
			// Retrieve a reference to the transaction data label
			viewHolder.data = (TextView) view.findViewById ( R.id.label_return_data );
			// Retrieve a reference to the check box
			viewHolder.checkBox = (CheckBox) view.findViewById ( R.id.checkbox );
			// Retrieve a reference to the sync icon
			viewHolder.sync = (ImageView) view.findViewById ( R.id.icon_sync );
			// Store the view holder as tag
			view.setTag ( viewHolder );
		} // End if
		else
			// An recycled view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) view.getTag ();
		
		// Store the transaction code
		viewHolder.transactionCode = cursor.getString ( cursor.getColumnIndex ( TransactionHeadersDao.Properties.TransactionCode.columnName ) );
		// Store the visit ID
		viewHolder.visitID = cursor.getLong ( cursor.getColumnIndex ( TransactionHeadersDao.Properties.VisitID.columnName ) );
		
		// Determine if the current transaction is deleted
		boolean isDeleted = StatusUtils.isDeleted () == cursor.getInt ( cursor.getColumnIndex ( TransactionHeadersDao.Properties.TransactionStatus.columnName ) );
		// Determine if the current transaction is processed
		boolean isProcessed = IsProcessedUtils.isSync () == cursor.getInt ( cursor.getColumnIndex ( TransactionHeadersDao.Properties.IsProcessed.columnName ) );
		// Determine if the current item is enabled
		boolean isEnabled = ! isProcessed;
		// Display / hide the check box accordingly
		viewHolder.checkBox.setVisibility ( isEnabled ? View.GONE : View.VISIBLE );
		// Display / hide the sync icon accordingly
		viewHolder.sync.setVisibility ( isEnabled ? View.VISIBLE : View.GONE );
		// Determine if the current transaction is final
		// A transaction is final if it is either deleted or processed or both
		viewHolder.isFinal = isDeleted || isProcessed;
		// Compute the transaction data label
		Calendar calendar = Calendar.getInstance ();
		calendar.setTimeInMillis ( cursor.getLong ( cursor.getColumnIndex ( TransactionHeadersDao.Properties.IssueDate.columnName ) ) );
		String transactionDate = DateTime.getBriefDate ( context , calendar ) + " " + DateTime.getFullTime ( context , calendar );
		// Build the code label
		SpannableString code = new SpannableString ( codeLabel + viewHolder.transactionCode );
		// Apply style span
		code.setSpan ( new StyleSpan ( Typeface.BOLD ) ,
				codeLabel.length () ,
				code.length () ,
				0 );
		// Apply foreground color span
		code.setSpan ( new ForegroundColorSpan ( brownColor ) ,
				codeLabel.length () ,
				code.length () ,
				0 );
		// Build the date label
		SpannableString date = new SpannableString ( dateLabel + transactionDate ); 
		// Apply style span
		date.setSpan ( new StyleSpan ( Typeface.BOLD ) ,
				dateLabel.length () ,
				date.length () ,
				0 );
		// Apply foreground color span
		date.setSpan ( new ForegroundColorSpan ( brownColor ) ,
				dateLabel.length () ,
				date.length () ,
				0 );
		// Build the deleted label
		SpannableString deleted = new SpannableString ( statusLabel + deletedLabel );
		// Apply style span
		deleted.setSpan ( new StyleSpan ( Typeface.BOLD ) ,
				statusLabel.length () ,
				deleted.length () ,
				0 );
		// Apply foreground color span
		deleted.setSpan ( new ForegroundColorSpan ( Color.RED ) ,
				statusLabel.length () ,
				deleted.length () ,
				0 );
		// Build the data label
		CharSequence data = TextUtils.concat ( code , newLine , date );
		// Check if the current transaction is deleted
		if ( isDeleted ) 
			// Add the deleted label
			data = TextUtils.concat ( data , newLine , deleted );
		// Display the transaction data label
		viewHolder.data.setText ( data , BufferType.SPANNABLE );
		// Determine if the current transaction is deleted
		isDeleted = StatusUtils.isDeleted () == cursor.getInt ( cursor.getColumnIndex ( TransactionHeadersDao.Properties.TransactionStatus.columnName ) );
		// Determine if the current transaction is processed
		isProcessed = IsProcessedUtils.isSync () == cursor.getInt ( cursor.getColumnIndex ( TransactionHeadersDao.Properties.IsProcessed.columnName ) );
		// Determine if the current transaction is applied
		boolean isApplied = IsProcessedUtils.isWebProcessed () != cursor.getInt ( cursor.getColumnIndex ( TransactionHeadersDao.Properties.IsProcessed.columnName ) );
		// Display / hide the check box accordingly
		viewHolder.checkBox.setVisibility ( isProcessed ? View.VISIBLE : View.GONE );
		// Display / hide the sync icon accordingly
		viewHolder.sync.setVisibility ( ! isProcessed ? View.VISIBLE : View.GONE );
		// Determine if the current transaction is final
		// A transaction is final if it is applied (not pending)
		viewHolder.isFinal = isApplied;
		// Check if the transaction is not deleted
		if ( ! isDeleted ) {
			// Build an additional spannable string
			SpannableString additional = null;
			// Check if the transaction is pending
			if ( ! isApplied ) {
				// Build the pending label
				additional = new SpannableString ( statusLabel + pendingLabel );
				// Apply style span
				additional.setSpan ( new StyleSpan ( Typeface.BOLD ) , statusLabel.length () , additional.length () , 0 );
				// Apply foreground color span
				additional.setSpan ( new ForegroundColorSpan ( Color.BLUE ) , statusLabel.length () , additional.length () , 0 );
				// Display the transaction data label
				viewHolder.data.setText ( TextUtils.concat ( viewHolder.data.getText () , newLine , additional ) , BufferType.SPANNABLE );
			} // End if
			else {
				// Build the applied label
				additional = new SpannableString ( statusLabel + returnedLabel );
				// Apply style span
				additional.setSpan ( new StyleSpan ( Typeface.BOLD ) , statusLabel.length () , additional.length () , 0 );
				// Apply foreground color span
				additional.setSpan ( new ForegroundColorSpan ( Color.BLUE ) , statusLabel.length () , additional.length () , 0 );
				// Display the transaction data label
				viewHolder.data.setText ( TextUtils.concat ( viewHolder.data.getText () , newLine , additional ) , BufferType.SPANNABLE );
			} // End if
		} // End if
	}
	
}