/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.ClientStock;

import java.text.DecimalFormat;
import java.util.Calendar;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.ClientStockCountHeadersDao;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.TransactionHeadersDao;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.text.SpannableString;
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
public class ClientStockAdapter extends CursorAdapter {

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
	 * @author Rabee
	 *
	 */
	public static class ViewHolder {
		public String code;
		public Long visitID;
		public int itemType;
		public TextView	data;
		public CheckBox checkBox;
		public ImageView sync;
		public boolean isFinal;
	}
	
	/**
	 * A two digits string formatter.
	 */
	private final DecimalFormat twoDigits;

	/**
	 * String used to hold the  date label.
	 */
	private final String DateLabel;
	
	/**
	 * Integer used to hold the brown color value.
	 */
	private final int brownColor;
	
	/**
	 * Constructor
	 * 
	 * @param context	The application context.
	 * @param cursor	Cursor holding a result set returned by a database query.
	 * @param layout	Integer holding the item layout resource id.
	 */
	public ClientStockAdapter ( Context context , Cursor cursor , int layout ) {
		// Superclass method call
		super ( context , cursor , false );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
    	twoDigits = new DecimalFormat ( "00" );
    	DateLabel = AppResources.getInstance ( context ).getString ( context , R.string.client_stock_date_label ) + " : ";
    	brownColor = context.getResources ().getColor ( R.color.Brown );
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
			// Retrieve a reference to the sales order data label
			viewHolder.data = (TextView) view.findViewById ( R.id.label_sales_order_data );
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
		
		// Store the sales order code
		viewHolder.code = cursor.getString ( cursor.getColumnIndex ( ClientStockCountHeadersDao.Properties.TransactionCode.columnName ) );
		// Store the visit ID
		viewHolder.visitID = cursor.getLong ( cursor.getColumnIndex ( ClientStockCountHeadersDao.Properties.VisitID.columnName ) );
		// Store the item type
		viewHolder.itemType = cursor.getInt ( cursor.getColumnIndex ( ClientStockCountHeadersDao.Properties.ItemType.columnName ) );
		
		// Determine if the current item is enabled
		boolean isEnabled = getCursor ().getInt ( getCursor ().getColumnIndex ( ClientStockCountHeadersDao.Properties.IsProcessed.columnName ) ) == IsProcessedUtils.isNotSync ();
		// Display / hide the check box accordingly
		viewHolder.checkBox.setVisibility ( isEnabled ? View.GONE : View.VISIBLE );
		// Display / hide the sync icon accordingly
		viewHolder.sync.setVisibility ( isEnabled ? View.VISIBLE : View.GONE );
		// Determine if the current sales order is processed
		boolean isProcessed = IsProcessedUtils.isSync () == cursor.getInt ( cursor.getColumnIndex ( TransactionHeadersDao.Properties.IsProcessed.columnName ) );
		// Determine if the current sales order is final
		// A sales order is final if it is either deleted or processed or both
		viewHolder.isFinal =isProcessed;
		// Compute the sales order data label
		Calendar date = Calendar.getInstance ();
		date.setTimeInMillis ( cursor.getLong ( cursor.getColumnIndex ( ClientStockCountHeadersDao.Properties.Date.columnName ) ) );
		String SDate = twoDigits.format ( date.get ( Calendar.DATE ) ) + "-" + twoDigits.format ( date.get ( Calendar.MONTH ) + 1 ) + "-" + date.get ( Calendar.YEAR );
		
		SpannableString data = new SpannableString ( DateLabel + date );
		// Apply style span
		data.setSpan ( new StyleSpan ( Typeface.BOLD ) ,
				DateLabel.length () ,
				DateLabel.length () + SDate.length () ,
				0 );
		// Apply style span
		data.setSpan ( new StyleSpan ( Typeface.BOLD ) ,
				DateLabel.length () + SDate.length (),
				DateLabel.length () + SDate.length () ,
				0 );
		// Apply foreground color span
		data.setSpan ( new ForegroundColorSpan ( brownColor ) ,
				SDate.length () ,
				SDate.length () + SDate.length () ,
				0 );
		// Apply foreground color span
		data.setSpan ( new ForegroundColorSpan ( brownColor ) ,
				DateLabel.length () + SDate.length ()  ,
				DateLabel.length () + SDate.length () ,
				0 );
	
		// Display the sales order data label
		viewHolder.data.setText ( SDate , BufferType.SPANNABLE );
	}
	
}