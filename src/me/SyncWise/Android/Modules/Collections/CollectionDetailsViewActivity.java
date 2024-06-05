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

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.AppToast;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.CollectionDetailsDao;
import me.SyncWise.Android.Database.CollectionHeaders;
import me.SyncWise.Android.Database.CollectionHeadersDao;
import me.SyncWise.Android.Database.DaoSession;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Gson.BaseTimerListActivity;
import me.SyncWise.Android.Modules.Printing.PrintingActivity;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.MergeCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Activity used to just display / view the collection details of a specific collection.
 * 
 * @author Elias
 *
 */
public class CollectionDetailsViewActivity extends BaseTimerListActivity {
	
	/**
	 * Bundle key used to put/retrieve the content of the collection code
	 */
	public static final String COLLECTION_CODE = CollectionDetailsViewActivity.class.getName () + ".COLLECTION_CODE";
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #displayPrintingConfirmation}.
	 */
	private static final String DISPLAY_PRINTING_CONFIRMATION = CollectionDetailsViewActivity.class.getName () + ".DISPLAY_PRINTING_CONFIRMATION";
	
	/**
	 * Boolean used to determine whether to display the printing confirmation.
	 */
	private boolean displayPrintingConfirmation;
	
	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate ( Bundle savedInstanceState ) {
		// Superclass method call
		super.onCreate ( savedInstanceState );
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.collection_details_view_activity_title ) );
		// Set the activity content from a layout resource.
		setContentView ( R.layout.collection_details_view_activity );
		// Enable fast scrolling
		getListView ().setFastScrollEnabled ( true );
		// Define the empty list view
		getListView ().setEmptyView ( findViewById ( R.id.empty_list_view ) );
		// Display empty list label
		( (TextView) findViewById ( R.id.empty_list_view ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_collections_list_label ) );
		
		// Check if the the collection code is valid
		if ( getIntent ().getStringExtra ( COLLECTION_CODE ) == null ) {
			// Indicate that the activity cannot be displayed
			new AppToast ( this ).setIcon ( R.drawable.warning ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.invalid_selection_label ) ).show ();
			// Finish activity
			finish ();
			// Exit method
			return;
		} // End if
		
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null )
			displayPrintingConfirmation = savedInstanceState.getBoolean ( DISPLAY_PRINTING_CONFIRMATION );
		
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
	
    /*
     * Called to retrieve per-instance state from an activity before being killed so that the state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle).
     *
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState ( Bundle outState ) {
    	// Superclass method call
    	super.onSaveInstanceState ( outState );
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
    	// Use the MenuInflater of this context to inflate a menu hierarchy from the specified XML resource
    	getMenuInflater ().inflate ( R.menu.action_bar , menu );
    	// Enable the required menu items
    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_print ) );
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
    	// Determine if the action print is selected
    	if ( menuItem.getItemId () == R.id.action_print ) {
    		// Set flag
    		displayPrintingConfirmation = true;
    		// Display printing confirmation
    		displayPrintingConfirmation ();
    		// Consume event
    		return true;
    	} // End else if
    	// Allow normal menu processing to proceed
    	return false;
    }
    
	/**
	 * Displays a confirmation dialog used to prompt the user to print the current transaction.
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
							Intent intent = new Intent ( CollectionDetailsViewActivity.this, PrintingActivity.class );
							intent.putExtra ( PrintingActivity.TYPE , PrintingActivity.Type.RECEIPT );
							intent.putExtra ( PrintingActivity.PRINTOUT , PrintingActivity.Printout.ORIGINAL );
							ArrayList < CollectionHeaders > list = new ArrayList < CollectionHeaders > ();
							list.add ( DatabaseUtils.getInstance ( CollectionDetailsViewActivity.this ).getDaoSession ().getCollectionHeadersDao ().queryBuilder ()
									.where ( CollectionHeadersDao.Properties.CollectionCode.eq ( getIntent ().getStringExtra ( COLLECTION_CODE ) ) ).unique () );
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
					} } );
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
			DaoSession daoSession = DatabaseUtils.getInstance ( CollectionDetailsViewActivity.this ).getDaoSession ();
			// Compute and return a cursor with the corresponding collection details data set
			return new MergeCursor ( new Cursor [] { daoSession.getDatabase ().query ( CollectionDetailsDao.TABLENAME , // Table name
					daoSession.getCollectionDetailsDao ().getAllColumns () , // Columns
					CollectionDetailsDao.Properties.CollectionCode.columnName + "=?" , // Selection
					new String [] { getIntent ().getStringExtra ( COLLECTION_CODE ) } , // Selection arguments
					null , null , null ) , 
					daoSession.getDatabase ().query ( CollectionDetailsDao.TABLENAME , // Table name
							daoSession.getCollectionDetailsDao ().getAllColumns () , // Columns
							CollectionDetailsDao.Properties.CollectionCode.columnName + "=(SELECT CollectionCode FROM CollectionHeaders WHERE ParentCode=?) "+ " OR " +CollectionDetailsDao.Properties.CollectionCode.columnName + "=(SELECT CollectionCode FROM CollectionHeaders WHERE ClientCode=?) "  , // Selection
							new String [] { getIntent ().getStringExtra ( COLLECTION_CODE ) , getIntent ().getStringExtra ( COLLECTION_CODE )} , // Selection arguments
							null , null , null )
			} );
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
			setListAdapter ( new CollectionDetailsViewAdapter ( CollectionDetailsViewActivity.this , cursor , R.layout.collection_details_view_activity_item ) );
		}
		
	}
	
}