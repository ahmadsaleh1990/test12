/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.CompetitorItemsList;

import java.text.DecimalFormat;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.CompetitorItemsDao;
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
import android.widget.AlphabetIndexer;
import android.widget.CursorAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.TextView.BufferType;

/**
 * Adapter that exposes data from a Cursor to a ListView widget.
 * 
 * @author Elias
 *
 */
public class CompetitorItemsCursorAdapter extends CursorAdapter implements SectionIndexer {

	/**
	 * {@link android.view.LayoutInflater LayoutInflater} used to instantiate a layout XML file into its corresponding View objects.
	 */
	private final LayoutInflater layoutInflater;
	
	/**
	 * XML Layout (layout) resource id.
	 */
	private final int layout;
	
	/**
	 * Reference to AlphabetIndexter which provides a way to do fast indexing of large lists.
	 */
	private AlphabetIndexer alphabetIndexer;
	
	/**
	 * Nested class implementation of the view holder pattern used to keep references to children views in order to avoid unnecessary calls to {@link android.view.View#findViewById(int) findViewById(id)} on each row.
	 * 
	 * @author Elias
	 *
	 */
	public static class ViewHolder {
		public TextView code;
		public TextView description;
		public TextView points;
	}
	
	/**
	 * String used to hold the code label.
	 */
	protected final String codeLabel;
	
	/**
	 * String used to hold the price label.
	 */
	protected final String priceLabel;
	
	/**
	 * String used to hold the N/A label.
	 */
	protected final String NALabel;
	
	/**
	 * String used to hold the new line character.
	 */
	protected final String newLine = "\n";
	
	/**
	 * String used to hold the strong point label.
	 */
	protected final String strongPointLabel;
	
	/**
	 * String used to hold the weak point label.
	 */
	protected final String weakPointLabel;
	
	/**
	 * Integer hosting the dark green color.
	 */
	protected final int darkGreenColor;
	
	/**
	 * A money string formatter where no rounding is used.
	 */
	private final DecimalFormat money;
	
	/**
	 * Constructor
	 * 
	 * @param context	The application context.
	 * @param cursor	Cursor holding a result set returned by a database query.
	 * @param layout	Integer holding the item layout resource id.
	 */
	public CompetitorItemsCursorAdapter ( Context context , Cursor cursor , int layout ) {
		// Superclass method call
		super ( context , cursor , false );
		// Initialize the alphabet indexer
		alphabetIndexer = new AlphabetIndexer ( cursor , cursor.getColumnIndex ( CompetitorItemsDao.Properties.CompetitorItemName.columnName ) , " 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ" );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
		codeLabel = AppResources.getInstance ( context ).getString ( context , R.string.code_label );
		priceLabel = AppResources.getInstance ( context ).getString ( context , R.string.price_label );
		NALabel =  AppResources.getInstance ( context ).getString ( context , R.string.not_available_abbreviation );
		strongPointLabel = AppResources.getInstance ( context ).getString ( context , R.string.competitor_item_strong_point_label );
		weakPointLabel = AppResources.getInstance ( context ).getString ( context , R.string.competitor_item_weak_point_label );
		darkGreenColor = context.getResources ().getColor ( R.color.DarkGreen );
		money = new DecimalFormat ( "#,##0" );
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
			// Retrieve a reference to the competitor item code
			viewHolder.code = (TextView) view.findViewById ( R.id.label_item_code );
			// Retrieve a reference to the competitor item description
			viewHolder.description = (TextView) view.findViewById ( R.id.label_item_description );
			// Retrieve a reference to the competitor item points
			viewHolder.points = (TextView) view.findViewById ( R.id.label_points );
			// Store the view holder as tag
			view.setTag ( viewHolder );
		} // End if
		else
			// An recycled view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) view.getTag ();
		
		// Store the competitor item code
		String code = cursor.getString ( cursor.getColumnIndex ( CompetitorItemsDao.Properties.CompetitorItemCode.columnName ) );
		// Store the competitor item price
		Double _price = cursor.getDouble ( cursor.getColumnIndex ( CompetitorItemsDao.Properties.Price.columnName ) );
		String price = _price == null ? NALabel : money.format ( _price );
		// Build a spannable string out of the competitor item code and price in order to apply various spans
		SpannableString code_price = new SpannableString ( codeLabel + " : " + code + newLine + priceLabel + " : " + price );
		// Apply style span
		code_price.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , codeLabel.length () , 0 );
		// Apply color span
		code_price.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , codeLabel.length () , 0 );
		// Apply style span
		code_price.setSpan ( new StyleSpan ( Typeface.BOLD ) ,
				code_price.length () - price.length () - " : ".length () - priceLabel.length () ,
				code_price.length () - price.length () - " : ".length () , 0 );
		// Apply color span
		code_price.setSpan ( new ForegroundColorSpan ( Color.BLACK ) ,
				code_price.length () - price.length () - " : ".length () - priceLabel.length () ,
				code_price.length () - price.length () - " : ".length () , 0 );
		// Display the competitor item code and price
		viewHolder.code.setText ( code_price , BufferType.SPANNABLE );
		// Display the competitor item description
		viewHolder.description.setText ( cursor.getString ( cursor.getColumnIndex ( CompetitorItemsDao.Properties.CompetitorItemName.columnName ) ) );
		
		// Retrieve the strong points
		String _strongPoints = cursor.getString ( cursor.getColumnIndex ( CompetitorItemsDao.Properties.StrongPoints.columnName ) );
		// Retrieve the weak points
		String _weakPoints = cursor.getString ( cursor.getColumnIndex ( CompetitorItemsDao.Properties.WeakPoints.columnName ) );
		
		// Declare a spannable string used to host the competitor item's strong points
		SpannableString strongPoints = null;
		// Declare a spannable string used to host the competitor item's weak points
		SpannableString weakPoints = null;
		
		// Determine if at least one strong point is provided
		if ( _strongPoints !=null && ! _strongPoints.isEmpty () ) {
			// Build the spannable string using the competitor item's strong points in order to apply various spans
			strongPoints = new SpannableString ( strongPointLabel + " : " + _strongPoints );
			// Apply style span
			strongPoints.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , strongPointLabel.length () + 3 , 0 );
			// Apply color span
			strongPoints.setSpan ( new ForegroundColorSpan ( darkGreenColor ) , 0 , strongPointLabel.length () + 3 , 0 );
		} // End if
		
		// Determine if at least one weak point is provided
		if (_weakPoints!=null && ! _weakPoints.isEmpty () ) {
			// Build the spannable string using the competitor item's weak points in order to apply various spans
			weakPoints = new SpannableString ( weakPointLabel + " : " + _weakPoints );
			// Apply style span
			weakPoints.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , weakPointLabel.length () + 3 , 0 );
			// Apply color span
			weakPoints.setSpan ( new ForegroundColorSpan ( Color.RED ) , 0 , weakPointLabel.length () + 3 , 0 );
		} // End if
		
		// Declare and initialize a char sequence used to hold the competitor item's points
		CharSequence points = null;
		// Check if at least one strong point is provided
		if ( strongPoints != null )
			// Add the strong points
			points = points == null ? strongPoints : TextUtils.concat ( points , newLine , strongPoints );
		// Check if at least one weak point is provided
		if ( weakPoints != null )
			// Add the weak points
			points = points == null ? weakPoints : TextUtils.concat ( points , newLine , weakPoints );
		
		// Check if the competitor item has points to display
		if ( points == null )
			// Hide the competitor item points
			viewHolder.points.setVisibility ( View.GONE );
		else {
			// Make sure the competitor item points label is visible
			viewHolder.points.setVisibility ( View.VISIBLE );
			// Display the competitor item points
			viewHolder.points.setText ( points , BufferType.SPANNABLE );
		} // End else
	}
	
	/*
	 * Swap in a new Cursor, returning the old Cursor.
	 *
	 * @see android.widget.CursorAdapter#swapCursor(android.database.Cursor)
	 */
	@Override
	public Cursor swapCursor ( Cursor newCursor ) {
		// Assign the new cursor for the alphabet indexer
		alphabetIndexer.setCursor ( newCursor );
		// Superclass method call
		return super.swapCursor ( newCursor );
	}
	
	/*
	 * Provides the starting index in the list for a given section.
	 *
	 * @see android.widget.SectionIndexer#getPositionForSection(int)
	 */
	@Override
	public int getPositionForSection ( int section ) {
		// Perform a binary search or cache lookup to find the first row that matches a given section's starting letter
		return alphabetIndexer.getPositionForSection ( section );
	}

	/*
	 * This is a reverse mapping to fetch the section index for a given position in the list.
	 *
	 * @see android.widget.SectionIndexer#getSectionForPosition(int)
	 */
	@Override
	public int getSectionForPosition ( int position ) {
		// Returns the section index for a given position in the list by querying the item and comparing it with all items in the section array
		return alphabetIndexer.getSectionForPosition ( position );
	}

	/*
	 * This provides the list view with an array of section objects.
	 *
	 * @see android.widget.SectionIndexer#getSections()
	 */
	@Override
	public Object [] getSections () {
		// Returns the section array constructed from the alphabet provided in the constructor.
		return alphabetIndexer.getSections ();
	}
	
}