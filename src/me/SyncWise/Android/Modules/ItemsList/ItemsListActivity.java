/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.ItemsList;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.TextView;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.Divisions;
import me.SyncWise.Android.Database.DivisionsDao;
import me.SyncWise.Android.Database.DivisionsUtils;
import me.SyncWise.Android.Database.ItemBarcodesDao;
import me.SyncWise.Android.Database.ItemDivisionsDao;
import me.SyncWise.Android.Database.ItemsDao;
import me.SyncWise.Android.Database.ItemsUtils;
import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Modules.BarcodeIntegration.IntentIntegrator;
import me.SyncWise.Android.Modules.BarcodeIntegration.IntentResult;
import me.SyncWise.Android.Modules.FilterDivisions.FilterDivisionsActivity;
import me.SyncWise.Android.Modules.SearchList.SearchCursorListActivity;

/**
 * List Activity of all the items with search capabilities.
 * 
 * @author Elias
 * @sw.todo	<b>Items List Activity Implementation :</b><br>
 * Simply add this class in the {@code AndroidManifest.xml} file.
 *
 */
public class ItemsListActivity extends SearchCursorListActivity {

	/**
	 * List of {@link me.SyncWise.Android.Database.Divisions Divisions} objects.
	 */
	private ArrayList < Divisions > divisions;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #SELECTED_DIVISIONS}.
	 */
	private static final String SELECTED_DIVISIONS = ItemsListActivity.class.getName () + ".SELECTED_DIVISIONS";
	
	/**
	 * List of string used to host the selected divisions codes, in order to filter the items / sales orders list.
	 */
	private ArrayList < String > selectedDivisionsCodes;
	
	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		// Define the main table
		setTable ( DatabaseUtils.getInstance ( this ).getDaoSession ().getItemsDao () );
		// Define the selection
		setSelection ( ItemsDao.Properties.ItemType.columnName + " = ?" );
		// Define the selection arguments
		ArrayList < String > selectionArguments = new ArrayList < String > ();
		selectionArguments.add ( String.valueOf ( ItemsUtils.Type.REGULAR ) );
		setSelectionArguments ( selectionArguments );
		// Define the list order
		setOrder ( ItemsDao.Properties.ItemName.columnName + " COLLATE NOCASE ASC" );
		// Define the properties which applies for a filter
		addProperty ( ItemsDao.Properties.ItemName );
		addProperty ( ItemsDao.Properties.ItemCode );
		// Superclass method call
		super.onCreate ( savedInstanceState );
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.items_list_activity_title ) );
		// Enable fast scrolling
		getListView ().setFastScrollEnabled ( true );
		// Define the empty list view
		getListView ().setEmptyView ( findViewById ( R.id.empty_list_view ) );
		// Display empty list label
		( (TextView) findViewById ( R.id.empty_list_view ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_items_list_label ) );
		// Populate the divisions list
		new PopulateDivisions ().execute ();
	}
	
	/*
	 * Builds a ListAdapter that will be set to the list.
	 *
	 * @see me.SyncWise.Android.Modules.SearchList.SearchCursorListActivity#populateList(android.database.Cursor)
	 */
	@Override
	protected ListAdapter populateList ( Cursor cursor ) {
		// Initialize and return an items cursor adapter using the provided cursor
		return new ItemsCursorAdapter ( this , cursor , R.layout.item_item );
	}
	
    /*
     * Called to retrieve per-instance state from an activity before being killed so that the state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle).
     *
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState ( Bundle outState ) {
    	// Superclass method call
    	super.onSaveInstanceState ( outState );
    	// Save the content of selectedDivisions in the outState bundle
    	outState.putStringArrayList ( SELECTED_DIVISIONS , selectedDivisionsCodes );
    }
	
	/*
	 * Initialize the contents of the Activity's standard options menu.
	 *
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
    @Override
    public boolean onCreateOptionsMenu ( Menu menu ) {
    	// Use the MenuInflater of this context to inflate a menu hierarchy from the specified XML resource
    	getMenuInflater ().inflate ( R.menu.action_bar , menu );
    	// Enable the required menu items
		me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_list ) );
		// Determine if item barcodes are allowed
		if ( PermissionsUtils.getItemBarcodes ( this , DatabaseUtils.getCurrentUserCode ( this ) , DatabaseUtils.getCurrentCompanyCode ( this ) ) )
	    	// Enable the required menu items
			me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_barcode ) );
		// Display the menu
        return true;
    }
	
    /*
     * This hook is called whenever an item in your options menu is selected.
     *
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected ( MenuItem menuItem ) {
    	// Determine if the action list is selected
    	if ( menuItem.getItemId () == R.id.action_list ) {
    		// Check if the selected divisions list is valid
    		if ( selectedDivisionsCodes == null )
    	    	// Allow normal menu processing to proceed
    	    	return false;
    		
    		// Create a new intent to start a new activity
    		Intent intent = new Intent ( this , FilterDivisionsActivity.class );
    		// Add the divisions list to the intent
    		intent.putExtra ( FilterDivisionsActivity.DIVISIONS , divisions );
    		// Add the selected divisions codes to the intent
    		intent.putExtra ( FilterDivisionsActivity.SELECTED_DIVISIONS , selectedDivisionsCodes );
    		// Start the new activity
    		startActivityForResult ( intent , 0 );
    		// Consume event
    		return true;
    	} // End if
    	// Determine if the action barcode is selected
    	else if ( menuItem.getItemId () == R.id.action_barcode ) {
    		// Declare and initialize a barcode intent integrator
    		IntentIntegrator integrator = new IntentIntegrator ( this );
    		// Initiate a barcode scan
    		integrator.initiateScan();
    		// Consume event
    		return true;
    	} // End else if
    	// Allow normal menu processing to proceed
    	return false;
    }
    
    /*
     * Computes the SQL search query based on the provided filter criteria.
     *
     * @see me.SyncWise.Android.Modules.SearchList.SearchCursorListActivity#computeSearchQuery()
     */
    @Override
	protected String computeSearchQuery () {
		// Declare and initialize a string to host the search SQL query
		StringBuilder _searchQuery = new StringBuilder ();
		// Check if the search criteria is valid and if there is at least one property to filter
		if ( ! TextUtils.isEmpty ( getSearchQuery () ) && getPropertiesCount () != 0 ) {
			// Escape the character ' to protect from SQL injection
			String searchQuery = getSearchQuery ().replace ( "'" , "''" );
			// Build the first search condition out of the first property
			_searchQuery.append ( getProperty ( 0 ).columnName ).append ( " LIKE '%" ).append ( searchQuery ).append ( "%'" );
			// Iterate over the remaining properties
			for ( int i = 1 ; i < getPropertiesCount () ; i ++ )
				// Build the remaining conditions
				_searchQuery.append ( " OR " ).append ( getProperty ( i ).columnName ).append ( " LIKE '%" ).append ( searchQuery ).append ( "%'" );
			// Build the last condition related to item barcodes
			_searchQuery.append ( " OR " ).append ( ItemsDao.Properties.ItemCode.columnName ).append ( " IN ( SELECT " )
				.append ( ItemBarcodesDao.Properties.ItemCode.columnName ).append ( " FROM " ).append ( ItemBarcodesDao.TABLENAME )
				.append ( " WHERE " ).append ( ItemBarcodesDao.Properties.ItemBarcode.columnName ).append ( " = '" )
				.append ( searchQuery ).append ( "' )" );
		} // End if
		// Return the computed search query
		return _searchQuery.toString ();
	}
    
	/*
	 * Called when an activity you launched exits, giving you the requestCode you started it with, the resultCode it returned, and any additional data from it.
	 *
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	protected void onActivityResult ( int requestCode , int resultCode , Intent data ) {
    	// Check if the result is successful and the intent is valid
    	if ( resultCode != RESULT_OK || data == null )
    		// Exit method
    		return;
    	
    	// Parse the activity result to an barcode intent integration result
    	IntentResult scanResult = IntentIntegrator.parseActivityResult ( requestCode , resultCode , data );
    	// Check if the barcode scan result is valid
    	if ( scanResult != null) {
    		// Retrieve the barcode and apply it in the search filed
    		setQuery ( scanResult.getContents () == null ? "" : scanResult.getContents () );
    	} // End if
    	
    	// Check if the child activity returned a list of filtered divisions
    	if ( data.getStringArrayListExtra ( FilterDivisionsActivity.SELECTED_DIVISIONS ) != null ) {
    		// Store the list of selected divisions
    		selectedDivisionsCodes = data.getStringArrayListExtra ( FilterDivisionsActivity.SELECTED_DIVISIONS );
    		// Set the new selection based on the retrieved divisions codes
    		setSelection ();
			// Set a new adapter based on the selected visions, asynchronously
    		repopulateList ();
    	} // End if
	}
    
	/**
	 * Sets the selection statement (and arguments) based on selected divisions codes stored in {@link #selectedDivisionsCodes}.
	 */
	private void setSelection () {
		// Check if the selected divisions codes is valid
		if ( selectedDivisionsCodes == null ) {
			// Clear selection
			setSelection ( null );
			setSelectionArguments ( null );
		} // End if
		// Check if the selected divisions codes is empty
		else if ( selectedDivisionsCodes.isEmpty () ) {
			// Display no items
			setSelection ( ItemsDao.Properties.ItemCode.columnName + " IN ()" );
			setSelectionArguments ( null );
		} // End else if
		// Otherwise check if all the divisions are selected
		else if ( divisions != null && selectedDivisionsCodes.size () == divisions.size () ) {
			// No need to set a selection
			// Clear selection
			setSelection ( null );
			setSelectionArguments ( null );
		} // End else if
		// Otherwise compute and set the selection
		else {
			// Compute selection string
			String selection = ItemsDao.Properties.ItemCode.columnName + " IN " +
					"( SELECT " + ItemDivisionsDao.Properties.ItemCode.columnName + " FROM " + ItemDivisionsDao.TABLENAME + " WHERE " + ItemDivisionsDao.Properties.DivisionCode.columnName + " IN (";
			// Iterate over all divisions codes
			int size = selectedDivisionsCodes.size ();
			for ( int i = 0 ; i < size ; i ++ )
				// Add the current division code indicator
				selection += "?" + ( i == size - 1 ? "" : "," );
			// Close selection statement
			selection += "))";
			// Set the selection statement
			setSelection ( selection );
			// Set the selection arguments
			setSelectionArguments ( selectedDivisionsCodes );
		} // End else
	}
	
	/**
	 * AsyncTask helper class used to populate the divisions list.
	 * 
	 * @author Elias
	 *
	 */
	private class PopulateDivisions extends AsyncTask < Void , Void , Void > {
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground ( Void ... params ) {
			// Retrieve the company code
			String companyCode = DatabaseUtils.getCurrentCompanyCode ( ItemsListActivity.this );
			// Retrieve all divisions
			List < Divisions > allDivisions = DatabaseUtils.getInstance ( ItemsListActivity.this ).getDaoSession ().getDivisionsDao ().queryBuilder ()
					.where ( DivisionsDao.Properties.CompanyCode.eq ( companyCode ) ).list ();
			// Retrieve the divisions linked to the user
			List < Divisions > directUserDivisions = DatabaseUtils.getInstance ( ItemsListActivity.this ).getDaoSession ().getDivisionsDao ()
					.queryRaw ( " WHERE CompanyCode = ? AND DivisionCode IN ( SELECT DivisionCode FROM UserDivisions WHERE CompanyCode = ? ) " ,
							new String [] { companyCode , companyCode } );
			allDivisions.removeAll ( directUserDivisions );
			// Retrieve the user children division
			List < Divisions > allUserDivisions = DivisionsUtils.getChildren ( directUserDivisions , allDivisions );
			// Add the direct user divisions to the main list
			allUserDivisions.addAll ( directUserDivisions );
			// Initialize the selected divisions codes list
			ArrayList < String > _selectedDivisionsCodes = new ArrayList < String > ();
			// Iterate over all divisions
			for ( Divisions division : allUserDivisions )
				// Add the division code
				_selectedDivisionsCodes.add ( division.getDivisionCode () );
			// Set the lists
			divisions = new ArrayList < Divisions > ( allUserDivisions );
			selectedDivisionsCodes = _selectedDivisionsCodes;
			return null;
		}
		
	}
    
}