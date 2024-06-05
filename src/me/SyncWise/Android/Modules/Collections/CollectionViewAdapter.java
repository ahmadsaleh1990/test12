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

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.CollectionHeadersDao;
import me.SyncWise.Android.Database.CurrenciesDao;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.TextUtils;
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
public class CollectionViewAdapter extends CursorAdapter {

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
		public TextView info;
	}
	
	/**
	 * String used to hold the code label.
	 */
	private final String codeLabel;
	
	/**
	 * String used to hold the total amount label.
	 */
	private final String totalAmountLabel;
	
	/**
	 * Constant integer holding the Deep Sky Blue color.
	 */
	private final int deepSkyBlueColor;
	
	/**
	 * Constant integer holding the Yellow Green color.
	 */
	private final int yellowGreenColor;
	
	/**
	 * Constructor
	 * 
	 * @param context	The application context.
	 * @param cursor	Cursor holding a result set returned by a database query.
	 * @param layout	Integer holding the item layout resource id.
	 */
	public CollectionViewAdapter ( Context context , Cursor cursor , int layout ) {
		// Superclass method call
		super ( context , cursor , false );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
		codeLabel = AppResources.getInstance ( context ).getString ( context , R.string.code_label );
		totalAmountLabel = AppResources.getInstance ( context ).getString ( context , R.string.collection_header_total_amount_label );
		deepSkyBlueColor = context.getResources ().getColor ( R.color.DeepSkyBlue );
		yellowGreenColor = context.getResources ().getColor ( R.color.YellowGreen );
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
			// Retrieve a reference to the collection header info
			viewHolder.info = (TextView) view.findViewById ( R.id.label_collection_info );
			// Store the view holder as tag
			view.setTag ( viewHolder );
		} // End if
		else
			// An recycled view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) view.getTag ();
		
		// Store the collection header code
		viewHolder.code = cursor.getString ( cursor.getColumnIndex ( CollectionHeadersDao.Properties.CollectionCode.columnName ) );
		
		// Build a spannable string out of the collection header code in order to apply various spans
		SpannableString code = new SpannableString ( codeLabel + " : " + viewHolder.code );
		// Apply style span
		code.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , codeLabel.length () , 0 );
		// Apply color span
		code.setSpan ( new ForegroundColorSpan ( deepSkyBlueColor ) , codeLabel.length () , code.length () , 0 );
		
		// Retrieve the total amount
		String amountValue = String.valueOf ( cursor.getDouble ( cursor.getColumnIndex ( CollectionHeadersDao.Properties.TotalAmount.columnName ) ) );
		// Retrieve the currency symbol
		String currencySymbol = cursor.getString ( cursor.getColumnIndex ( CurrenciesDao.Properties.CurrencySymbol.columnName ) );
		// Merge both the value and symbol
		String totalAmount = currencySymbol == null ? amountValue : amountValue + " " + currencySymbol;
		// Build a spannable string out of the collection header amount in order to apply various spans
		SpannableString amount = new SpannableString ( totalAmountLabel + " : " + totalAmount );
		// Apply style span
		amount.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , amount.length () , 0 );
		// Apply color span
		amount.setSpan ( new ForegroundColorSpan ( yellowGreenColor ) , totalAmountLabel.length () , amount.length () , 0 );
		
		// Display the collection header code
		viewHolder.info.setText ( TextUtils.concat ( code , "\n" , amount ) , BufferType.SPANNABLE );
	}
	
}