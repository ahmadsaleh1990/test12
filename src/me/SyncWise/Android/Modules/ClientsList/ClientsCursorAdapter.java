/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.ClientsList;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.ClientDivisionsDao;
import me.SyncWise.Android.Database.ClientsDao;
import me.SyncWise.Android.Database.CompaniesDao;
import me.SyncWise.Android.Database.DivisionsDao;
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
public class ClientsCursorAdapter extends CursorAdapter implements SectionIndexer {
	
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
		public String clientCode;
		public String companyCode;
		public String divisionCode;
		public TextView data;
		public TextView name;
	}
	
	/**
	 * String used to hold the code label.
	 */
	private final String codeLabel;
	
	/**
	 * String used to hold the company label.
	 */
	private final String companyLabel;
	
	/**
	 * String used to hold the division label.
	 */
	private final String divisionLabel;
	
	/**
	 * String used to hold the new line character.
	 */
	private final String newLine = "\n";
	
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
		// Initialize the alphabet indexer
		alphabetIndexer = new AlphabetIndexer ( cursor , cursor.getColumnIndex ( ClientsDao.Properties.ClientName.columnName ) , " 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ" );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
		codeLabel = AppResources.getInstance ( context ).getString ( context , R.string.code_label );
		companyLabel = AppResources.getInstance ( context ).getString ( context , R.string.company_label );
		divisionLabel = AppResources.getInstance ( context ).getString ( context , R.string.division_label );
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
			// Retrieve a reference to the client data
			viewHolder.data = (TextView) view.findViewById ( R.id.label_client_code );
			// Retrieve a reference to the client name
			viewHolder.name = (TextView) view.findViewById ( R.id.label_client_name );
			// Store the view holder as tag
			view.setTag ( viewHolder );
		} // End if
		else
			// An recycled view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) view.getTag ();
		
		// Store the client code
		viewHolder.clientCode = cursor.getString ( cursor.getColumnIndex ( ClientsDao.Properties.ClientCode.columnName ) );
		// Store the company code
		viewHolder.companyCode = cursor.getString ( cursor.getColumnIndex ( ClientsDao.Properties.CompanyCode.columnName ) );
		// Store the division code
		viewHolder.divisionCode = cursor.getString ( cursor.getColumnIndex ( ClientDivisionsDao.Properties.DivisionCode.columnName ) );
		
		// Build a spannable string out of the client code in order to apply various spans
		SpannableString clientCode = new SpannableString ( codeLabel + " : " + cursor.getString ( cursor.getColumnIndex ( ClientsDao.Properties.ClientCode.columnName ) ) );
		// Apply style span
		clientCode.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , codeLabel.length () , 0 );
		// Apply color span
		clientCode.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , codeLabel.length () , 0 );
		
		// Build a spannable string out of the company name in order to apply various spans
		SpannableString companyData = new SpannableString ( companyLabel + " : " + cursor.getString ( cursor.getColumnIndex ( CompaniesDao.Properties.CompanyName.columnName ) ) );
		// Apply style span
		companyData.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , companyLabel.length () , 0 );
		// Apply color span
		companyData.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , companyLabel.length () , 0 );
		
		// Build a spannable string out of the division name in order to apply various spans
		SpannableString divisionData = new SpannableString ( divisionLabel + " : " + cursor.getString ( cursor.getColumnIndex ( DivisionsDao.Properties.DivisionName.columnName ) ) );
		// Apply style span
		divisionData.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , divisionLabel.length () , 0 );
		// Apply color span
		divisionData.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , divisionLabel.length () , 0 );

		// Display the client data
		viewHolder.data.setText ( TextUtils.concat ( clientCode , newLine , companyData , newLine , divisionData ) , BufferType.SPANNABLE );
		// Display the client name
		viewHolder.name.setText ( cursor.getString ( cursor.getColumnIndex ( ClientsDao.Properties.ClientName.columnName ) ) );
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