/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Collections;

import java.util.ArrayList;

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.CollectionHeaders;
import me.SyncWise.Android.Database.CollectionHeadersDao;
import me.SyncWise.Android.Database.CollectionUtils;
import me.SyncWise.Android.Database.DaoSession;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Gson.BaseTimerListActivity;
import me.SyncWise.Android.Modules.Collections.CollectionSettlementAdapter.ViewHolder;
import me.SyncWise.Android.Modules.Printing.PrintingActivity;
import me.SyncWise.Android.Widgets.Baguette;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Activity used to settle collections.
 * 
 * @author Elias
 *
 */
public class CollectionSettlementActivity extends BaseTimerListActivity {
	
	/**
	 * List of strings hosting the settled collection codes.
	 */
	private static final String SETTLED_COLLECTION_CODES = CollectionSettlementActivity.class.getName () + ".SETTLED_COLLECTION_CODES";
	
	/**
	 * List of strings hosting the settled collection codes.
	 */
	private ArrayList < String > settledCollectionCodes;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #displayPrintingConfirmation}.
	 */
	private static final String DISPLAY_PRINTING_CONFIRMATION = CollectionSettlementActivity.class.getName () + ".DISPLAY_PRINTING_CONFIRMATION";
	
	/**
	 * Boolean used to determine whether to display the printing confirmation.
	 */
	private boolean displayPrintingConfirmation;
	
	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate ( Bundle savedInstanceState ) {
		// Superclass method call
		super.onCreate ( savedInstanceState );
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.collection_settlement_activity_title ) );
		// Set the activity content from a layout resource.
		setContentView ( R.layout.collection_details_view_activity );
		// Enable fast scrolling
		getListView ().setFastScrollEnabled ( true );
		// Define the empty list view
		getListView ().setEmptyView ( findViewById ( R.id.empty_list_view ) );
		// Display empty list label
		( (TextView) findViewById ( R.id.empty_list_view ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_collections_list_label ) );
		
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state 
			settledCollectionCodes = (ArrayList < String >) savedInstanceState.getSerializable ( SETTLED_COLLECTION_CODES );
			displayPrintingConfirmation = savedInstanceState.getBoolean ( DISPLAY_PRINTING_CONFIRMATION );
		} // End if
		
		// Initialize the list if needed
		if ( settledCollectionCodes == null )
			settledCollectionCodes = new ArrayList < String > ();
		
		// Retrieve all the collections asynchronously
		new PopulateList ().execute ();
	}
	
	/*
	 * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause(), for your activity to start interacting with the user.
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume () {
		// Superclass method call
		super.onResume ();
		// Check if the printing confirmation should be displayed
		if ( displayPrintingConfirmation )
			// Display printing confirmation
			displayPrintingConfirmation ();
	}
	
	/**
	 * Displays a confirmation dialog used to prompt the user to print the settled collections.
	 */
	private void displayPrintingConfirmation () {
		// Prompt the user to print a copy
		AppDialog.getInstance ().displayAlert ( this ,
				null ,
				AppResources.getInstance ( this ).getString ( this , R.string.printing_activity_print_request_message ) ,
				AppDialog.ButtonsType.YesNo ,
				new DialogInterface.OnClickListener () {
					@Override
					public void onClick ( DialogInterface dialog , int which ) {
						// Determine the clicked button
						switch ( which ) {
						case DialogInterface.BUTTON_POSITIVE: 
							// Print the transaction
							Intent intent = new Intent ( CollectionSettlementActivity.this, PrintingActivity.class );
							intent.putExtra ( PrintingActivity.TYPE , PrintingActivity.Type.COLLECTIONISSETTEL );
							intent.putExtra ( PrintingActivity.PRINTOUT , PrintingActivity.Printout.ORIGINAL );
							ArrayList < CollectionHeaders > list = new ArrayList < CollectionHeaders > ();
							list.addAll ( DatabaseUtils.getInstance ( CollectionSettlementActivity.this ).getDaoSession ().getCollectionHeadersDao ().queryBuilder ()
									.where ( CollectionHeadersDao.Properties.CollectionCode.in ( settledCollectionCodes ) ).list () );
							intent.putExtra ( PrintingActivity.HEADER , list );
							startActivity ( intent );
							// Reset flag
							displayPrintingConfirmation = false;
							break;
						case DialogInterface.BUTTON_NEGATIVE:
							// Dismiss dialog
							AppDialog.getInstance ().dismiss ();
							// Reset flag
							displayPrintingConfirmation = false;
							break;
						} // End switch
						// Clear settlement list
						settledCollectionCodes.clear ();
			    		// Refresh the action bar
			    		invalidateOptionsMenu ();
					} } );
	}
	
    /*
     * Called to retrieve per-instance state from an activity before being killed so that the state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle).
     *
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState ( Bundle outState ) {
    	// Superclass method call
    	super.onSaveInstanceState ( outState );
    	// Save the content of settledCollectionCodes in the outState bundle
    	outState.putSerializable ( SETTLED_COLLECTION_CODES , settledCollectionCodes );
    	// Save the content of displayPrintingConfirmation in the outState bundle
    	outState.putBoolean ( DISPLAY_PRINTING_CONFIRMATION , displayPrintingConfirmation );
    }
	
	/*
	 * Initialize the contents of the Activity's standard options menu.
	 *
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
    @Override
    public boolean onCreateOptionsMenu ( Menu menu ) {
    	// Check if the printing confirmation is displayed
    	if ( displayPrintingConfirmation )
    		// Hide the menu
            return true;
    	
    	// Use the MenuInflater of this context to inflate a menu hierarchy from the specified XML resource
    	getMenuInflater ().inflate ( R.menu.action_bar , menu );
    	// Enable the required menu items
    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_save ) );
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
    	// Determine if the action save is selected
    	if ( menuItem.getItemId () == R.id.action_save ) {
    		// Check if there is at least one selected collection
    		if ( settledCollectionCodes.isEmpty () ) {
    			// Display baguette message
    			Baguette.showText ( this , AppResources.getInstance ( this ).getString ( this , R.string.collection_settlement_no_header_selected_message ) , Baguette.BackgroundColor.RED );
        		// Consume event
        		return true;
    		} // End if
    		// Save settlement
    		saveSettlement ();
    		// Consume event
    		return true;
		} // End if
    	// Allow normal menu processing to proceed
    	return false;
    }
    
    /**
     * Saves the collection settlement.
     */
    private void saveSettlement () {
    	// Update collections
		// Compute the SQL string
		String SQL = "UPDATE CollectionHeaders " +
				"SET IsSetteled = ? " +
				"WHERE CollectionCode IN ( '" + settledCollectionCodes.get ( 0 ) + "'";
		for ( int i = 1 ; i < settledCollectionCodes.size () ; i ++ )
			SQL += ",'" + settledCollectionCodes.get ( i ) + "'";
		SQL += ")";
		// Compute the selection arguments
		String selectionArguments [] = new String [] { String.valueOf ( CollectionUtils.Settlement.IsSettled ) };
		// Execute the movement update query
		DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().execSQL ( SQL , selectionArguments );
		// Display baguette message
		Baguette.showText ( this , AppResources.getInstance ( this ).getString ( this , R.string.collection_settlement_save_success_message ) , Baguette.BackgroundColor.GREEN );
		// Set flag
		displayPrintingConfirmation = true;
		// Display printing confirmation
		displayPrintingConfirmation ();
		// Retrieve all the collections asynchronously
		new PopulateList ().execute ();
    }
    
    /**
     * Determines if the provided collection code is checked for settlement.
     * 
     * @param collectionCode	String hosting the collection code to check.
     * @return	Boolean indicating if the provided collection code is checked for settlement.
     */
    public boolean isChecked ( String collectionCode ) {
    	return settledCollectionCodes.contains ( collectionCode );
    }
    
    /*
     * This method will be called when an item in the list is selected.
     *
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
	@Override
    protected void onListItemClick ( ListView listView , View view , final int position , long id ) {
		// Retrieve a reference to the view holder
		ViewHolder viewHolder = (ViewHolder) view.getTag ();
		// Check if the collection is settled
		if ( isChecked ( viewHolder.code ) ) {
			// Remove the code from the list
			settledCollectionCodes.remove ( viewHolder.code );
			// Un-check the check box
			viewHolder.checkBox.setChecked ( false );
		} // End if
		else {
			// Add the code to the list
			settledCollectionCodes.add ( viewHolder.code );
			// Check the check box
			viewHolder.checkBox.setChecked ( true );
		} // End else
	}
    
	/**
	 * AsyncTask helper class used to populate the collections details list.
	 * 
	 * @author Elias
	 *
	 */
	private class PopulateList extends AsyncTask < Void , Void , Cursor > {
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Cursor doInBackground ( Void ... params ) {
			// Retrieve a reference to the dao session
			DaoSession daoSession = DatabaseUtils.getInstance ( CollectionSettlementActivity.this ).getDaoSession ();
			// Compute and return a cursor with the corresponding collection headers that are unsettled
			return daoSession.getDatabase ().rawQuery ( " SELECT CH.* , C.* , CU.* FROM CollectionHeaders CH " +
					"INNER JOIN Clients C ON CH.ClientCode = C.ParentCode " +
					"INNER JOIN Currencies CU ON CH.CurrencyCode = CU.CurrencyCode " +
					"WHERE CH.IsSetteled = ? " +
					"ORDER BY CH.CollectionDate DESC " ,
					new String [] { String.valueOf ( CollectionUtils.Settlement.IsNotSettled ) } );
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( Cursor cursor ) {
			// Check if the cursor is valid
			if ( cursor == null ) {
				// Invalid cursor
				setListAdapter ( null );
				// Exit method
				return;
			} // End if
			// Otherwise the cursor is valid
			// Set the new adapter
			setListAdapter ( new CollectionSettlementAdapter ( CollectionSettlementActivity.this , cursor , R.layout.collection_settlement_item ) );
		}
		
	}
	
}