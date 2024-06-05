package me.SyncWise.Android.Modules.SalesSummary;

/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.Currencies;
import me.SyncWise.Android.Database.CurrenciesDao;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.TransactionHeadersDao;
import me.SyncWise.Android.Database.TransactionHeadersUtils;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter that exposes data from a Cursor to a ListView widget.
 * 
 * @author Elias
 *
 */
public class SummaryDetailsViewAdapter extends CursorAdapter {

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
	 * String used to host the total amount label.
	 */
	private final String client_name;
	
	
	/**
	 * Constructor
	 * 
	 * @param context	The application context.
	 * @param cursor	Cursor holding a result set returned by a database query.
	 * @param layout	Integer holding the item layout resource id.
	 */
	public SummaryDetailsViewAdapter ( Context context , Cursor cursor , int layout ) {
		// Superclass method call
		super ( context , cursor , false );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
		totalAmountLabel = AppResources.getInstance ( context ).getString ( context , R.string.summary_header_total_amount_label );
		client_name = AppResources.getInstance ( context ).getString ( context , R.string.summary_client_name_label );
		List < String > currencyCodes = new ArrayList < String > ();
		// Iterate over all raws
		if ( cursor.moveToFirst () ) {
			do {
				// Retrieve the current currency code
				String currencyCode = cursor.getString ( cursor.getColumnIndex ( TransactionHeadersDao.Properties.CurrencyCode.columnName ) );
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
	
	/**
	 * Makes a new view to hold the data pointed to by cursor.
	 *
	 * @see android.widget.CursorAdapter#newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)
	 */
	@Override
	public View newView ( Context context , Cursor cursor , ViewGroup parent ) {
		// Inflate and return a new view hierarchy from the specified xml resource
		return layoutInflater.inflate ( layout , null );
	}
	
	/**
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
			
			// Store the view holder as tag
			view.setTag ( viewHolder );
		} // End if
		else
			// An recycled view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) view.getTag ();
		
		// Retrieve the payment type
		Integer paymentType = cursor.getInt ( cursor.getColumnIndex ( TransactionHeadersDao.Properties.TransactionType.columnName ) );
		// Determine the payment type
		if ( paymentType != null && paymentType.equals ( TransactionHeadersUtils.Type.SALES_ORDER ) )
			// Display the cash icon
			viewHolder.icon.setImageResource ( R.drawable.cash );
		else if ( paymentType != null && paymentType.equals ( TransactionHeadersUtils.Type.SALES_INVOICE ) )
			// Display the check icon
			viewHolder.icon.setImageResource ( R.drawable.check );
		else
			// Remove the icon
			viewHolder.icon.setImageResource ( 0 );
		
		// Retrieve the currency code
		String currencyCode = cursor.getString ( cursor.getColumnIndex ( TransactionHeadersDao.Properties.CurrencyCode.columnName ) );
		// Declare and initialize a currency object used to display its description
		Currencies currency = null;
		// Check if the currency code is valid
		if ( currencyCode != null && currencies.containsKey ( currencyCode ) )
			// Retrieve the appropriate currency
			currency = currencies.get ( currencyCode );
		//Display the client name
		//viewHolder.amount.setText ( )
		//				);
		// Display the collection amount
		viewHolder.amount.setText (  client_name + " : " + 
				cursor.getString ( cursor.getColumnIndex ( "ClientName" )) + "     " + totalAmountLabel + " : " + 
			cursor.getDouble ( cursor.getColumnIndex ( "TotalTaxAmount" ) ) +
			( currency == null ? "" : " " + currency.getCurrencySymbol () ) );
	}
	
	/**
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