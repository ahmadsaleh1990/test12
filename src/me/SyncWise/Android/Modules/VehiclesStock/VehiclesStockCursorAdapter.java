/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.VehiclesStock;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.ItemsDao;
import me.SyncWise.Android.Database.VehiclesStockDao;
import me.SyncWise.Android.Modules.ItemsList.ItemsCursorAdapter;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView.BufferType;

/*
 * Adapter that exposes data from a Cursor to a ListView widget.
 * 
 * @see me.SyncWise.Android.Modules.ItemsList.ItemsCursorAdapter
 */
public class VehiclesStockCursorAdapter extends ItemsCursorAdapter {

	/**
	 * String used to hold the good quantity label.
	 */
	private final String goodQuantityLabel;
	
	/**
	 * String used to hold the bad quantity label.
	 */
	private final String badQuantityLabel;

	/**
	 * String used to hold the new line string.
	 */
	private final String newLine = "\n";
	
	/**
	 * Constructor
	 * 
	 * @param context	The application context.
	 * @param cursor	Cursor holding a result set returned by a database query.
	 * @param layout	Integer holding the item layout resource id.
	 */
	public VehiclesStockCursorAdapter ( Context context , Cursor cursor , int layout ) {
		// Superclass method call
		super ( context , cursor , layout );
		// Initialize attribute
		goodQuantityLabel = AppResources.getInstance ( context ).getString ( context , R.string.vehicles_stock_good_quantity_label ) + " : ";
		badQuantityLabel = AppResources.getInstance ( context ).getString ( context , R.string.vehicles_stock_damaged_quantity_label ) + " : ";
	}

	/*
	 * Makes a new view to hold the data pointed to by cursor.
	 *
	 * @see android.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
	 */
	@Override
	public void bindView ( View view , Context context , Cursor cursor ) {
		// Super class method call
		super.bindView ( view , context , cursor );
		
		// Retrieve the stored view holder
		ViewHolder viewHolder = (ViewHolder) view.getTag ();
		
		// Retrieve the minimum quantity
		Integer minimumQuantity = cursor.getInt ( cursor.getColumnIndex ( ItemsDao.Properties.MinimumQuantity.columnName ) );
		minimumQuantity = minimumQuantity == null ? 0 : minimumQuantity;
		// Retrieve the vehicle stock
		Integer vehicleStockGood = cursor.getInt ( cursor.getColumnIndex ( VehiclesStockDao.Properties.GoodQuantity.columnName ) );
		String _vehicleStockGood = String.valueOf ( vehicleStockGood );
		Integer vehicleStockDamaged = cursor.getInt ( cursor.getColumnIndex ( VehiclesStockDao.Properties.DamageQuantity.columnName ) );
		String _vehicleStockDamaged = String.valueOf ( vehicleStockDamaged );
		// Set the item icon based on if the current vehicle stock is less than the minimum quantity
		viewHolder.icon.setImageResource ( vehicleStockGood > minimumQuantity ? R.drawable.boxes : R.drawable.boxes_2 );
		
		// Build a spannable string out of the item code in order to apply various spans
		SpannableString code = new SpannableString ( codeLabel + " : " + viewHolder.itemCode );
		// Apply style span
		code.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , codeLabel.length () , 0 );
		// Apply color span
		code.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , codeLabel.length () , 0 );
		
		// Build a spannable string out of the good quantity in order to apply various spans
		SpannableString goodQuantity = new SpannableString ( goodQuantityLabel + _vehicleStockGood ); 
		// Apply style span
		goodQuantity.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , goodQuantityLabel.length () , 0 );
		// Apply color span
		goodQuantity.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , goodQuantityLabel.length () , 0 );
		
		// Build a spannable string out of the damaged quantity in order to apply various spans
		SpannableString damagedQuantity = new SpannableString ( badQuantityLabel + _vehicleStockDamaged ); 
		// Apply style span
		damagedQuantity.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , badQuantityLabel.length () , 0 );
		// Apply color span
		damagedQuantity.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , badQuantityLabel.length () , 0 );
		
		// Display the item code and quantities
		viewHolder.data.setText ( TextUtils.concat ( code , newLine , goodQuantity , newLine , damagedQuantity ) , BufferType.SPANNABLE );
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
