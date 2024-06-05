/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.SalesOrder;

import java.text.DecimalFormat;
import java.util.Calendar;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.CurrenciesDao;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.StatusUtils;
import me.SyncWise.Android.Database.TransactionHeadersDao;
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
public class SalesOrderAdapter extends CursorAdapter {

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
		public String code;
		public long visitID;
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
	 * A money string formatter where no rounding is used.
	 */
	private final DecimalFormat money_noRouding;
	
	/**
	 * String used to hold the delivery date label.
	 */
	private final String deliveryDateLabel;
	
	/**
	 * String used to hold the new line string.
	 */
	private final String newLine = "\n";
	
	/**
	 * String used to hold the total amount label.
	 */
	private final String totalAmountLabel;
	
	/**
	 * String used to hold the status label.
	 */
	private final String statusLabel;
	/**
	 * String used to hold the code label.
	 */
	private final String codeLabel;
	/**
	 * String used to hold the deleted label
	 */
	private final String deletedLabel;
	
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
	public SalesOrderAdapter ( Context context , Cursor cursor , int layout ) {
		// Superclass method call
		super ( context , cursor , false );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
    	twoDigits = new DecimalFormat ( "00" );
    	money_noRouding = new DecimalFormat ( " #,##0" );
    	deliveryDateLabel = AppResources.getInstance ( context ).getString ( context , R.string.delivery_date_label ) + " : ";
    	totalAmountLabel = AppResources.getInstance ( context ).getString ( context , R.string.total_amount_label ) + " : ";
    	statusLabel = AppResources.getInstance ( context ).getString ( context , R.string.status_label ) + " : ";
    	deletedLabel ="REJECTED TO EDIT";
    	brownColor = context.getResources ().getColor ( R.color.Brown );
     	codeLabel = AppResources.getInstance ( context ).getString ( context , R.string.code_label ) + " : ";
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
		viewHolder.code = cursor.getString ( cursor.getColumnIndex ( TransactionHeadersDao.Properties.TransactionCode.columnName ) );
		// Store the visit ID
		viewHolder.visitID = cursor.getLong ( cursor.getColumnIndex ( TransactionHeadersDao.Properties.VisitID.columnName ) );
		
		// Determine if the current item is enabled
		boolean isEnabled = getCursor ().getInt ( getCursor ().getColumnIndex ( TransactionHeadersDao.Properties.IsProcessed.columnName ) ) == IsProcessedUtils.isNotSync ();
		// Display / hide the check box accordingly
		viewHolder.checkBox.setVisibility ( isEnabled ? View.GONE : View.VISIBLE );
		// Display / hide the sync icon accordingly
		viewHolder.sync.setVisibility ( isEnabled ? View.VISIBLE : View.GONE );
		// Determine if the current sales order is deleted
		boolean isDeleted = StatusUtils.isDeleted () == cursor.getInt ( cursor.getColumnIndex ( TransactionHeadersDao.Properties.TransactionStatus.columnName ) );
		// Determine if the current sales order is processed
		boolean isProcessed = IsProcessedUtils.isSync () == cursor.getInt ( cursor.getColumnIndex ( TransactionHeadersDao.Properties.IsProcessed.columnName ) );
		boolean isRejected = IsProcessedUtils.isRejected()  == cursor.getInt ( cursor.getColumnIndex ( TransactionHeadersDao.Properties.IsProcessed.columnName ) );
		
		// Determine if the current sales order is final
		// A sales order is final if it is either deleted or processed or both
		viewHolder.isFinal = isDeleted || isProcessed;
		// Compute the sales order data label
		Calendar delivery = Calendar.getInstance ();
		delivery.setTimeInMillis ( cursor.getLong ( cursor.getColumnIndex ( TransactionHeadersDao.Properties.DeliveryDate.columnName ) ) );
		String deliveryDate = twoDigits.format ( delivery.get ( Calendar.DATE ) ) + "-" + twoDigits.format ( delivery.get ( Calendar.MONTH ) + 1 ) + "-" + delivery.get ( Calendar.YEAR );
		DecimalFormat money = money_noRouding;
		int currencyRounding = cursor.getInt ( cursor.getColumnIndex ( CurrenciesDao.Properties.CurrencyRounding.columnName ) );
		if ( currencyRounding != 0 ) {
			StringBuilder pattern = new StringBuilder ();
			pattern.append ( " #,##0" );
			if ( currencyRounding >= 1 ) {
				pattern.append ( ".0" );
				for ( int i = 1 ; i < currencyRounding ; i ++ )
					pattern.append ( "0" );
			} // End if
	    	money = new DecimalFormat ( pattern.toString () );
		} // End if
		String totalAmount = money.format ( cursor.getDouble ( cursor.getColumnIndex ( TransactionHeadersDao.Properties.TotalTaxAmount.columnName ) ) );
		String currencySymbol = cursor.getString ( cursor.getColumnIndex ( CurrenciesDao.Properties.CurrencySymbol.columnName ) );
		if ( currencySymbol != null )
			totalAmount += " " + currencySymbol;
		SpannableString data = new SpannableString (codeLabel + viewHolder.code + newLine + deliveryDateLabel + deliveryDate + newLine + totalAmountLabel + totalAmount + ( isRejected ? newLine + statusLabel + deletedLabel : "" ) );
	
		// Apply style span
				data.setSpan ( new StyleSpan ( Typeface.BOLD ) ,
						codeLabel.length () ,
						codeLabel.length () + viewHolder.code.length () ,
						0 );
				// Apply foreground color span
				data.setSpan ( new ForegroundColorSpan ( brownColor ) ,
						codeLabel.length () ,
						codeLabel.length () + viewHolder.code.length () ,
						0 );
		// Apply style span
		data.setSpan ( new StyleSpan ( Typeface.BOLD ) ,
				codeLabel.length () + viewHolder.code.length () + newLine.length () +	deliveryDateLabel.length () ,
				codeLabel.length () + viewHolder.code.length () + newLine.length () +	deliveryDateLabel.length () + deliveryDate.length () ,
				0 );
		// Apply style span
		data.setSpan ( new StyleSpan ( Typeface.BOLD ) ,
				codeLabel.length () + viewHolder.code.length () + newLine.length () +	deliveryDateLabel.length () + deliveryDate.length () + newLine.length () + totalAmountLabel.length () ,
				codeLabel.length () + viewHolder.code.length () + newLine.length () +	deliveryDateLabel.length () + deliveryDate.length () + newLine.length () + totalAmountLabel.length () + totalAmount.length () ,
				0 );
		// Apply foreground color span
		data.setSpan ( new ForegroundColorSpan ( brownColor ) ,
				codeLabel.length () + viewHolder.code.length () + newLine.length () +		deliveryDateLabel.length () ,
				codeLabel.length () + viewHolder.code.length () + newLine.length () +		deliveryDateLabel.length () + deliveryDate.length () ,
				0 );
		// Apply foreground color span
		data.setSpan ( new ForegroundColorSpan ( brownColor ) ,
				codeLabel.length () + viewHolder.code.length () + newLine.length () +	deliveryDateLabel.length () + deliveryDate.length () + newLine.length () + totalAmountLabel.length () ,
				codeLabel.length () + viewHolder.code.length () + newLine.length () +	deliveryDateLabel.length () + deliveryDate.length () + newLine.length () + totalAmountLabel.length () + totalAmount.length () ,
				0 );
		// Check if the current sales order is deleted
//		if ( isDeleted ) {
//			// Apply style span
//			data.setSpan ( new StyleSpan ( Typeface.BOLD ) ,
//					codeLabel.length () + viewHolder.code.length () + newLine.length () +	deliveryDateLabel.length () + deliveryDate.length () + newLine.length () + totalAmountLabel.length () + totalAmount.length () + newLine.length () + statusLabel.length () ,
//					codeLabel.length () + viewHolder.code.length () + newLine.length () +	deliveryDateLabel.length () + deliveryDate.length () + newLine.length () + totalAmountLabel.length () + totalAmount.length () + newLine.length () + statusLabel.length () + deletedLabel.length () ,
//					0 );
//			// Apply foreground color span
//			data.setSpan ( new ForegroundColorSpan ( Color.RED ) ,
//					codeLabel.length () + viewHolder.code.length () + newLine.length () +deliveryDateLabel.length () + deliveryDate.length () + newLine.length () + totalAmountLabel.length () + totalAmount.length () + newLine.length () + statusLabel.length () ,
//					codeLabel.length () + viewHolder.code.length () + newLine.length () +	deliveryDateLabel.length () + deliveryDate.length () + newLine.length () + totalAmountLabel.length () + totalAmount.length () + newLine.length () + statusLabel.length () + deletedLabel.length () ,
//					0 );
//		} // End if
		if(isRejected){
			// Apply style span
						data.setSpan ( new StyleSpan ( Typeface.BOLD ) ,
								codeLabel.length () + viewHolder.code.length () + newLine.length () +	deliveryDateLabel.length () + deliveryDate.length () + newLine.length () + totalAmountLabel.length () + totalAmount.length () + newLine.length () + statusLabel.length () ,
								codeLabel.length () + viewHolder.code.length () + newLine.length () +	deliveryDateLabel.length () + deliveryDate.length () + newLine.length () + totalAmountLabel.length () + totalAmount.length () + newLine.length () + statusLabel.length () + deletedLabel.length () ,
								0 );
						// Apply foreground color span
						data.setSpan ( new ForegroundColorSpan ( Color.RED ) ,
								codeLabel.length () + viewHolder.code.length () + newLine.length () +deliveryDateLabel.length () + deliveryDate.length () + newLine.length () + totalAmountLabel.length () + totalAmount.length () + newLine.length () + statusLabel.length () ,
								codeLabel.length () + viewHolder.code.length () + newLine.length () +	deliveryDateLabel.length () + deliveryDate.length () + newLine.length () + totalAmountLabel.length () + totalAmount.length () + newLine.length () + statusLabel.length () + deletedLabel.length () ,
								0 );
		}
		// Display the sales order data label
		viewHolder.data.setText ( data , BufferType.SPANNABLE );
	}
	
}