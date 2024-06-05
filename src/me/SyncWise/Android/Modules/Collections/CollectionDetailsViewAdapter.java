/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Collections;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.DateTime;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.CollectionDetailsDao;
import me.SyncWise.Android.Database.CollectionUtils;
import me.SyncWise.Android.Database.Currencies;
import me.SyncWise.Android.Database.CurrenciesDao;
import me.SyncWise.Android.Database.DatabaseUtils;
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
public class CollectionDetailsViewAdapter extends CursorAdapter {

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
		public ImageView icon;
		public TextView amount;
		public TextView bank;
		public TextView check;
	}
	
	/**
	 * Map of {@link me.SyncWise.Android.Database.Currencies Currencies} objects mapped to their currency codes.
	 */
	private final HashMap < String , Currencies > currencies;
	
	/**
	 * String used to host the total amount label.
	 */
	private final String totalAmountLabel;
	
	/**
	 * String used to host the bank label.
	 */
	private final String bankLabel;
	
	/**
	 * String used to host the check code label.
	 */
	private final String checkCodeLabel;
	
	/**
	 * String used to host the check date label.
	 */
	private final String checkDateLabel;
	
	/**
	 * String used to host the new line character
	 */
	private final String newLine = "\n";
	
	/**
	 * Constructor
	 * 
	 * @param context	The application context.
	 * @param cursor	Cursor holding a result set returned by a database query.
	 * @param layout	Integer holding the item layout resource id.
	 */
	public CollectionDetailsViewAdapter ( Context context , Cursor cursor , int layout ) {
		// Superclass method call
		super ( context , cursor , false );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
		totalAmountLabel = AppResources.getInstance ( context ).getString ( context , R.string.collection_header_total_amount_label );
		bankLabel = AppResources.getInstance ( context).getString ( context, R.string.collection_bank_label ) + " : ";
		checkCodeLabel = AppResources.getInstance ( context ).getString ( context, R.string.check_code_label ) + " : ";
		checkDateLabel = AppResources.getInstance ( context ).getString ( context , R.string.check_date_label ) + " : ";
		// Retrieve all the currency codes
		List < String > currencyCodes = new ArrayList < String > ();
		// Iterate over all raws
		if ( cursor.moveToFirst () ) {
			do {
				// Retrieve the current currency code
				String currencyCode = cursor.getString ( cursor.getColumnIndex ( CollectionDetailsDao.Properties.CurrencyCode.columnName ) );
				// Check if the currency code is previously added
				if ( ! currencyCodes.contains ( currencyCode ) )
					// Add the current currency code
					currencyCodes.add ( currencyCode );
			} while ( cursor.moveToNext () );
		} // End if
		// Retrieve all the required currencies
		List < Currencies > currencies = DatabaseUtils.getInstance ( context ).getDaoSession ().getCurrenciesDao ().queryBuilder ()
				.where ( CurrenciesDao.Properties.CurrencyCode.in ( currencyCodes ) ).list ();
		// Map the currencies to their currency codes
		this.currencies = new HashMap < String , Currencies > ();
		// Iterate over currencies
		for ( Currencies currency : currencies )
			// Map the current currency to its code
			this.currencies.put ( currency.getCurrencyCode () , currency );
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
			// Retrieve a reference to the payment icon
			viewHolder.icon = (ImageView) view.findViewById ( R.id.icon_collection_icon );
			// Retrieve a reference to the collection amount label
			viewHolder.amount = (TextView) view.findViewById ( R.id.label_collection_amount );
			// Retrieve a reference to the collection bank label
			viewHolder.bank = (TextView) view.findViewById ( R.id.label_bank_code );
			// Retrieve a reference to the collection check label
			viewHolder.check = ( TextView ) view.findViewById(R.id.label_check_code);
			// Store the view holder as tag
			view.setTag ( viewHolder );
		} // End if
		else
			// An recycled view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) view.getTag ();
		
		// Retrieve the payment type
		Integer paymentType = cursor.getInt ( cursor.getColumnIndex ( CollectionDetailsDao.Properties.CollectionDetailType.columnName ) );
		// Determine the payment type
		if ( paymentType != null && paymentType.equals ( CollectionUtils.PaymentType.CASH ) )
			// Display the cash icon
			viewHolder.icon.setImageResource ( R.drawable.cash );
		else if ( paymentType != null && paymentType.equals ( CollectionUtils.PaymentType.CHECK ) )
			// Display the check icon
			viewHolder.icon.setImageResource ( R.drawable.check );
		else
			// Remove the icon
			viewHolder.icon.setImageResource ( 0 );
		
		// Retrieve the currency code
		String currencyCode = cursor.getString ( cursor.getColumnIndex ( CollectionDetailsDao.Properties.CurrencyCode.columnName ) );
		// Declare and initialize a currency object used to display its description
		Currencies currency = null;
		// Check if the currency code is valid
		if ( currencyCode != null && currencies.containsKey ( currencyCode ) )
			// Retrieve the appropriate currency
			currency = currencies.get ( currencyCode );
		// Display the collection amount
		viewHolder.amount.setText ( totalAmountLabel + " : " + 
			cursor.getDouble ( cursor.getColumnIndex ( CollectionDetailsDao.Properties.CollectionAmount.columnName ) ) +
			( currency == null ? "" : " " + currency.getCurrencySymbol () ) );
		
		// Retrieve the bank description
		String bankDescription = cursor.getString ( cursor.getColumnIndex ( CollectionDetailsDao.Properties.BankDescription.columnName ) );
		// Check if the bank description is valid
		if ( ! TextUtils.isEmpty ( bankDescription ) ) {
			// Build a spannable string out of the bank description in order to apply various spans
			SpannableString bank = new SpannableString ( bankLabel + bankDescription );
			// Apply style span
			bank.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , bankLabel.length () , 0 );
			// Apply color span
			bank.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , bankLabel.length () , 0 );
			// Display the bank label
			viewHolder.bank.setText ( bank , BufferType.SPANNABLE );
			viewHolder.bank.setVisibility ( View.VISIBLE );
		} // End if
		else
			// Hide the bank label
			viewHolder.bank.setVisibility ( View.GONE );
		
		// Retrieve the check code
		String checkCode = cursor.getString ( cursor.getColumnIndex ( CollectionDetailsDao.Properties.CheckCode.columnName ) );
		// Retrieve the check date
		Long checkDate = cursor.getLong ( cursor.getColumnIndex ( CollectionDetailsDao.Properties.CheckDate.columnName ) );
		// Check if the check code and date are valid
		if ( ! TextUtils.isEmpty ( checkCode ) && checkDate != null ) {
			// Format the check date
			String checkDateFormatted = DateTime.getBriefDate ( context , new Date ( checkDate ) ); 
			// Build a spannable string out of the check code in order to apply various spans
			SpannableString checkCodeStr = new SpannableString ( checkCodeLabel + checkCode );
			// Apply style span
			checkCodeStr.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , checkCodeLabel.length () , 0 );
			// Apply color span
			checkCodeStr.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , checkCodeLabel.length () , 0 );
			// Build a spannable string out of the check code in order to apply various spans
			SpannableString checkDateStr = new SpannableString ( checkDateLabel + checkDateFormatted );
			// Apply style span
			checkDateStr.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , checkDateLabel.length () , 0 );
			// Apply color span
			checkDateStr.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , checkDateLabel.length () , 0 );
			// Display the check code and date
			viewHolder.check.setText ( TextUtils.concat ( checkCodeStr , newLine , checkDateStr ) , BufferType.SPANNABLE );
			viewHolder.check.setVisibility ( View.VISIBLE );
		} // End if
		else
			// Hide the check code
			viewHolder.check.setVisibility ( View.GONE );
	}
	
	/*
	 * Returns true if the item at the specified position is not a separator. (A separator is a non-selectable, non-clickable item).
	 *
	 * @see android.widget.BaseAdapter#isEnabled(int)
	 */
	@Override
	public boolean isEnabled ( int position ) {
		// Indicate that the item is disabled
		return false;
	}
	
}