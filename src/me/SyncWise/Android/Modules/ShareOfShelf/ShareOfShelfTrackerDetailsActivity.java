/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.ShareOfShelf;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppDialog.ButtonsType;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.AppToast;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Vibration;
import me.SyncWise.Android.Database.Brands;
import me.SyncWise.Android.Database.BrandsDao;
import me.SyncWise.Android.Database.DaoSession;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.Divisions;
import me.SyncWise.Android.Database.DivisionsDao;
import me.SyncWise.Android.Database.DivisionsUtils;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.ShareOfShelfTracker;
import me.SyncWise.Android.Database.ShareOfShelfTrackerDao;
import me.SyncWise.Android.Database.ShareOfShelves;
import me.SyncWise.Android.Database.ShareOfShelvesDao;
import me.SyncWise.Android.Database.Visits;
import me.SyncWise.Android.Database.VisitsUtils;
import me.SyncWise.Android.Gson.BaseTimerListActivity;
import me.SyncWise.Android.Modules.BarcodeIntegration.IntentIntegrator;
import me.SyncWise.Android.Modules.BarcodeIntegration.IntentResult;
import me.SyncWise.Android.Modules.Call.CallAction;
import me.SyncWise.Android.Modules.Call.CallMenuActivity;
import me.SyncWise.Android.Modules.FilterDivisions.FilterDivisionsActivity;
import me.SyncWise.Android.Modules.Journey.Call;
import me.SyncWise.Android.Modules.ShareOfShelf.ShareOfShelfTrackerDetailsAdapter.ViewHolder;
import me.SyncWise.Android.Utilities.ActivityInstance;
import me.SyncWise.Android.Widgets.Baguette;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity implemented to perform, view or edit share of shelf tracking.
 * 
 * @author Elias
 * @sw.todo <b>Share Of Shelf Tracker Details Activity Implementation :</b><br>
 * Simply add this class in the {@code AndroidManifest.xml} file along with the permission :<br>
 * {@code <uses-permission android:name="android.permission.VIBRATE"/>}<br>
 * AND disable activity recreation for orientation events by adding the following to the activity tag in the manifest file :<br>
 * {@code android:configChanges="orientation|screenSize"}
 * 
 */
public class ShareOfShelfTrackerDetailsActivity extends BaseTimerListActivity implements SearchView.OnQueryTextListener {
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #isCreated}.
	 */
	private static final String IS_CREATED = ShareOfShelfTrackerDetailsActivity.class.getName () + ".IS_CREATED";
	
	/**
	 * Boolean used to indicate if the activity has been previously created.<br>
	 * This flag is mainly used to determine if the current activity creation is the first or not (activity re-creation due to phone rotation for example).
	 */
	private boolean isCreated;
	
	/**
	 * Bundle key used to put/retrieve the content of the view only flag.
	 */
	public static final String IS_VIEW_ONLY = ShareOfShelfTrackerDetailsActivity.class.getName () + ".IS_VIEW_ONLY";	
	
	/**
	 * Bundle key used to put/retrieve the content of the note only flag.
	 */
	public static final String IS_NOTE_ONLY = ShareOfShelfTrackerDetailsActivity.class.getName () + ".IS_NOTE_ONLY";	
	
	/**
	 * Boolean used to indicate if the activity is displayed on the screen.
	 */
	public static boolean isDisplayed;
	
	/**
	 * Reference to the search view in the action bar.
	 */
	private SearchView searchView;
	
	/**
	 * Bundle key used to put/retrieve the content of the current call.
	 */
	public static final String CALL = ShareOfShelfTrackerDetailsActivity.class.getName () + ".CALL";
	
	/**
	 * Bundle key used to put/retrieve the content of the current visit.
	 */
	public static final String VISIT = ShareOfShelfTrackerDetailsActivity.class.getName () + ".VISIT";
	
	/**
	 * List of {@link me.SyncWise.Android.Database.Divisions Divisions} objects used to host all the available objects for the current transaction.
	 */
	protected ArrayList < Divisions > divisions;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #SELECTED_DIVISIONS}.
	 */
	public static final String SELECTED_DIVISIONS = ShareOfShelfTrackerDetailsActivity.class.getName () + ".SELECTED_DIVISIONS";
	
	/**
	 * List of string used to host the selected divisions codes, in order to filter the share of shelf list.
	 */
	protected ArrayList < String > selectedDivisionsCodes;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #trackers}.
	 */
	public static final String TRACKERS = ShareOfShelfTrackerDetailsActivity.class.getName () + ".TRACKERS";
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.ShareOfShelf.Tracker Tracker} objects used to define the share of shelf trackers.<br>
	 * <b>NOTE :</b> <br>
	 * If a filter is applied, this list remains untouched. A new list is generated, filtered and passed to the list adapter.
	 */
	protected ArrayList < Tracker > trackers;
	
	/**
	 * Boolean used to indicate if there are trackers that should be retrieved.
	 */
	protected boolean retrieveTrackers;
	
	/**
	 * Reference to the share of shelf list population task.
	 */
	protected static PopulateList populateList;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #searchQuery}.
	 */
	public static final String SEARCH_QUERY = ShareOfShelfTrackerDetailsActivity.class.getName () + ".SEARCH_QUERY";
	
	/**
	 * String holding the search query.
	 */
	private String searchQuery;
	
    /**
     * Returns the appropriate decimal format used to properly display decimal values.
     * 
     * @return	A {@link java.text.DecimalFormat DecimalFormat} object used to format and display two digits after the decimal point.
     */
    public DecimalFormat getDecimalFormat () {
    	// Check if the list view adapter 
    	if ( getListAdapter () == null )
    		// Invalid data
    		return null;
    	// Determine if the list view adapter's instance is of ShareOfShelfTrackerDetailsAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof ShareOfShelfTrackerDetailsAdapter ) {
    		// Return the adapter's decimal format
    		return ( (ShareOfShelfTrackerDetailsAdapter) getListAdapter () ).getDecimalFormat ();
    	} // End if
		// Otherwise initialize and return the appropriate decimal format
		return new DecimalFormat ( "#,##0.00" );
    }
	
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
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.share_of_shelf_tracker_details_activity_title ) );
		// Set the activity content from a layout resource.
		setContentView ( R.layout.share_of_shelf_tracker_details_activity );
		// Enable fast scrolling
		getListView ().setFastScrollEnabled ( true );
		// Define the empty list view
		getListView ().setEmptyView ( findViewById ( R.id.empty_list_view ) );
		// Display empty list label
		( (TextView) findViewById ( R.id.empty_list_view ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_share_of_shelf_list_label ) );
		// Retrieve the current year
		int year = Calendar.getInstance ().get ( Calendar.YEAR );
		// Retrieve references to the BDA labels
		TextView bdaMeter = (TextView) findViewById ( R.id.label_bda_meter );
		TextView bdaPercentage = (TextView) findViewById ( R.id.label_bda_percentage );
		// Add the year to the BDA Meter label
		bdaMeter.setText ( bdaMeter.getText () + " " + year );
		bdaPercentage.setText ( bdaPercentage.getText () + " " + year );
		
		// Check if both the visit and the call are valid
		if ( getIntent ().getSerializableExtra ( CALL ) == null || getIntent ().getSerializableExtra ( VISIT ) == null ) {
			// Indicate that the activity cannot be displayed
			new AppToast ( this ).setIcon ( R.drawable.warning ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.invalid_selection_label ) ).show ();
			// Finish activity
			finish ();
			// Exit method
			return;
		} // End if
		
		// Initialize the search query (if not initialized)
		if ( searchQuery == null )
			searchQuery = "";
		
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state 
			isCreated = savedInstanceState.getBoolean ( IS_CREATED , isCreated );
			searchQuery = savedInstanceState.getString ( SEARCH_QUERY );
			selectedDivisionsCodes = savedInstanceState.getStringArrayList ( SELECTED_DIVISIONS );
			retrieveTrackers = savedInstanceState.getBoolean ( TRACKERS );
		} // End if
		
		// Retrieve all the trackers asynchronously
		populate ();
		
		// Determine if this is the first activity creation or a re-creation
		if ( ! isCreated )
			// Set the flag
			isCreated = true;
	}
	
	/**
	 * Retrieve all the share of shelves asynchronously.
	 */
	protected void populate () {
		// Retrieve all the share of shelves asynchronously
		populateList = new PopulateList ();
		populateList.execute ();
	}
	
	/*
	 * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause(), for your activity to start interacting with the user.
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume () {
		// Indicate that the activity is displayed
		isDisplayed = true;
		// Superclass method call
		super.onResume ();
	}
	
	/*
	 * Called as part of the activity lifecycle when an activity is going into the background, but has not (yet) been killed.
	 *
	 * @see android.app.Activity#onPause()
	 */
	@Override
    protected void onPause () {
		// Indicate that the activity is NOT displayed
		isDisplayed = false;
		// Superclass method call
		super.onPause ();
		// Remove any displayed baguette
		Baguette.remove ( this );
		// Dismiss any displayed dialogs (to avoid activity context leak)
		AppDialog.getInstance ().dismiss ();
	}
	
    /*
     * Called to retrieve per-instance state from an activity before being killed so that the state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle).
     *
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState ( Bundle outState ) {
    	// Superclass method call
    	super.onSaveInstanceState ( outState );
    	// Save the content of isCreated in the outState bundle
    	outState.putBoolean ( IS_CREATED , isCreated );
    	// Save the content of searchQuery in the outState bundle
    	outState.putString ( SEARCH_QUERY , searchQuery );
    	// Save the content of selectedDivisions in the outState bundle
    	outState.putStringArrayList ( SELECTED_DIVISIONS , selectedDivisionsCodes );

    	new Thread ( new Runnable () {
			@Override
			public void run () {
				// Save the content of the trackers using GSON
				ActivityInstance.saveDataGson ( ShareOfShelfTrackerDetailsActivity.this , ShareOfShelfTrackerDetailsActivity.class.getName () , TRACKERS , trackers );
			}
		} ).start ();
		// Indicate that there is saved trackers data
		outState.putBoolean ( TRACKERS , true );
    }
    
	/*
	 * Called when the activity has detected the user's press of the back key. 
	 *
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed () {
		// Determine if there is at least one modification
    	if ( hasModifications () )
			// There is at least one modification
			// Display exit confirmation
			AppDialog.getInstance ().displayAlert ( this ,
					null ,
					AppResources.getInstance ( this ).getString ( this , R.string.discard_changes_confirmation_message ) ,
					AppDialog.ButtonsType.YesNo ,
					new DialogInterface.OnClickListener () {
						@Override
						public void onClick ( DialogInterface dialog , int which ) {
							// Determine the clicked button
							switch ( which ) {
							case DialogInterface.BUTTON_POSITIVE:
								// Finish this activity
								ShareOfShelfTrackerDetailsActivity.this.finish ();
								break;
							case DialogInterface.BUTTON_NEGATIVE:
								// Dismiss dialog
								AppDialog.getInstance ().dismiss ();
								break;
							} // End switch
						}
					} );
		else 
			// Superclass method call
			super.onBackPressed ();
	}
	
	/*
	 * Called when you are no longer visible to the user.
	 *
	 * @see android.app.Activity#onStop()
	 */
	@Override
    public void onStop () {
    	// Superclass method call
		super.onStop ();
		// Determine if the activity is finishing
		if ( isFinishing () ) {
			isCreated = false;
			divisions = null;
			searchQuery = null;
			searchView = null;
			trackers = null;
			selectedDivisionsCodes = null;
		} // End if
	}
	
	/**
	 * Validates the tracker being modified (via pickers).<br>
	 * Mainly both the share of shelf and Category values should be valid percentages (between 0 and 100).
	 * 
	 * @param view
	 */
	public void validateTracker ( final View view ) {
		try {
			// Track errors
			boolean error = false;
			// Retrieve the view holder
			ViewHolder viewHolder = (ViewHolder) view.getTag ();
			// Retrieve the tracker
			Tracker tracker = viewHolder.tracker;
			// Check if the share of shelf is valid (between 0 and 100)
			if ( tracker.getShareOfShelf () < 0 ) {
				// Invalid value, correct to 0 from the picker (to go through the watcher)
				viewHolder.pickerShareOfShelf.setText ( "0" );
				// Set flag
				error = true;
			} // End if
			// Check if the share of shelf is valid (between 0 and 100)
			else if ( tracker.getShareOfShelf () > 100 ) {
				// Invalid value, correct to 100 from the picker (to go through the watcher)
				viewHolder.pickerShareOfShelf.setText ( "100" );
				// Set flag
				error = true;
			} // End if
			// Check if the competitor share of shelf is valid (between 0 and 100)
			if ( tracker.getCompetitorShareOfShelf () < 0 ) {
				// Invalid value, correct to 0 from the picker (to go through the watcher)
				viewHolder.pickerCompetitorShareOfShelf.setText ( "0" );
				// Set flag
				error = true;
			} // End if
			// Check if the competitor share of shelf is valid (between 0 and 100)
			else if ( tracker.getCompetitorShareOfShelf () > 100 ) {
				// Invalid value, correct to 100 from the picker (to go through the watcher)
				viewHolder.pickerCompetitorShareOfShelf.setText ( "100" );
				// Set flag
				error = true;
			} // End if
			// Check if the category is valid (between 0 and 100)
			if ( tracker.getCategory () < 0 ) {
				// Invalid value, correct to 0 from the picker (to go through the watcher)
				viewHolder.pickerCategory.setText ( "0" );
				// Set flag
				error = true;
			} // End if
			else if ( tracker.getCategory () > 100 ) {
				// Invalid value, correct to 100 from the picker (to go through the watcher)
				viewHolder.pickerCategory.setText ( "100" );
				// Set flag
				error = true;
			} // End if
			// Check if an error occurred
			if ( error )
				// Warn the user to input valid values
				AppDialog.getInstance ().displayAlert ( this ,
						AppResources.getInstance ( this ).getString ( this , R.string.warning_label ) ,
						AppResources.getInstance ( this ).getString ( this , R.string.share_of_shelf_invalid_percentage_value ) ,
						ButtonsType.OK , null );
		} catch ( Exception exception) {
			// Indicate that the activity cannot be displayed
			new AppToast ( this ).setIcon ( R.drawable.warning ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.invalid_selection_label ) ).show ();
		} // End of try-catch block
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
    	
    	// Check if the child activity returned a list of filtered divisions
    	if ( data.getStringArrayListExtra ( FilterDivisionsActivity.SELECTED_DIVISIONS ) != null ) {
    		// Store the list of selected divisions
    		selectedDivisionsCodes = data.getStringArrayListExtra ( FilterDivisionsActivity.SELECTED_DIVISIONS );
			// Set a new adapter based on the saved filter, asynchronously
			new FilterList ( searchQuery ).execute ();
    		// Exit method
    		return;
    	} // End if
    	
    	// Parse the activity result to an barcode intent integration result
    	IntentResult scanResult = IntentIntegrator.parseActivityResult ( requestCode , resultCode , data );
    	// Check if the barcode scan result is valid
    	if ( scanResult != null && searchView != null ) {
			// Set the new query
			searchView.setQuery ( scanResult.getContents () == null ? "" : scanResult.getContents () , false );
			// Expand the SearchView
			searchView.setIconified ( false );
    	} // End if
	}
	
	/*
	 * Initialize the contents of the Activity's standard options menu.
	 *
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
    @Override
    public boolean onCreateOptionsMenu ( Menu menu ) {
    	// Check if the list of trackers is valid
    	if ( trackers == null )
    		// Hide the menu
            return false;
    	// Use the MenuInflater of this context to inflate a menu hierarchy from the specified XML resource
    	getMenuInflater ().inflate ( R.menu.action_bar , menu );
    	// Enable the required menu items
//		me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_list ) );
		if ( ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) )
			me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_save ) );
		me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_search ) );
		SearchView searchView = (SearchView) menu.findItem ( R.id.action_search ).getActionView ();
		setupSearchView ( searchView );
		// Check if there is a search query
		if ( ! searchQuery.isEmpty () ) {
			// Set the new query
			searchView.setQuery ( searchQuery , false );
			// Expand the SearchView
			searchView.setIconified ( false );
		} // End if
		// Display the menu
        return true;
    }
    
	/**
	 * Performs all necessary setup in order to properly display the search view widget.
	 * 
	 * @param searchView	A {@link android.widget.SearchView SearchView} object used to perform the setup.
	 */
    protected void setupSearchView ( SearchView searchView ) {
    	// Check if the search view is valid
    	if ( searchView == null )
    		// Invalid search view
    		return;
    	// Store a reference to the search view
    	this.searchView = searchView;
    	// Set a query hint string to be displayed in the empty query field
		searchView.setQueryHint ( AppResources.getInstance ( this ).getString ( this , R.string.search_label ) );
		// Set a query listener to apply search filters
		searchView.setOnQueryTextListener ( this );
		// Disable showing a submit button when the query is non-empty
		searchView.setSubmitButtonEnabled ( false );
	}
    
    /*
     * This hook is called whenever an item in your options menu is selected.
     *
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected ( MenuItem menuItem ) {
    	// Check if the trackers list is valid
    	if ( trackers == null )
    		// Consume event
    		return true;
    	
    	// Determine if the action list is selected
    	if ( menuItem.getItemId () == R.id.action_list ) {
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
    	// Determine if the action save is selected
    	else if ( menuItem.getItemId () == R.id.action_save ) {
    		// Check if there are modifications
    		if ( ! hasModifications () ) {
    			// Indicate that there are no new modifications
				Baguette.showText ( this ,
						AppResources.getInstance ( this ).getString ( this , R.string.no_new_modifications_message1 ) ,
						Baguette.BackgroundColor.BLUE );
	    		// Consume event
	    		return true;
    		} // End if
    		
    		// Display alert dialog
    		AppDialog.getInstance ().displayAlert ( this ,
    				null ,
    				AppResources.getInstance ( this ).getString ( this , R.string.save_confirmation_message ) ,
    				AppDialog.ButtonsType.YesNo ,
    				new DialogInterface.OnClickListener () {
						@Override
						public void onClick ( DialogInterface dialog , int which ) {
							// Determine the clicked button
							switch ( which ) {
							case DialogInterface.BUTTON_POSITIVE:
								// Save the transaction(s)
								saveTrackers ();
								break;
							case DialogInterface.BUTTON_NEGATIVE:
								// Dismiss dialog
								AppDialog.getInstance ().dismiss ();
								break;
							} // End switch
						}
					} );
    		
    		// Consume event
    		return true;
    	} // End else if
    	// Allow normal menu processing to proceed
    	return false;
    }
    
    /**
     * Saves the updated trackers.
     */
    private void saveTrackers () {
    	// Make sure the trackers list is valid
    	if ( trackers == null )
    		// Exit method
    		return;
    	
		// Flag used to indicate whether an error occurred or not
		boolean error = false;
		// Retrieve the user code
		String userCode = DatabaseUtils.getCurrentUserCode ( this );
		// Retrieve the client code
		String clientCode = ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClientDivision ().getClientCode ();
		// Retrieve the company code
		String companyCode = ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClientDivision ().getCompanyCode ();
		// Retrieve the division code
		String divisionCode = ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClientDivision ().getDivisionCode ();
		// Retrieve the visit ID
		Long visitID = ( (Visits) getIntent ().getSerializableExtra ( VISIT ) ).getVisitID ();
		// Retrieve the current date
		Calendar now = Calendar.getInstance ();
		// Build a new transaction ID
		Long transactionID = VisitsUtils.getVisitID ( now );
		// Retrieve a reference to the dao session
		DaoSession daoSession = DatabaseUtils.getInstance ( ShareOfShelfTrackerDetailsActivity.this ).getDaoSession ();
		
		try {
			// Begin transaction
			daoSession.getDatabase ().beginTransaction ();
			
			// Iterate over the trackers
			for ( Tracker tracker : trackers )
				// Check if the tracker is modified
			//	if ( tracker.isModified (this,userCode,companyCode) )
					// Insert a new tracker
					daoSession.getShareOfShelfTrackerDao ().insert ( new ShareOfShelfTracker ( null , // ID
							transactionID , // TransactionID
							userCode , // UserCode
							clientCode , // ClientCode
							companyCode , // CompanyCode
							divisionCode , // DivisionCode
							tracker.getBrand ().getBrandCode () , // BrandCode
							visitID , // VisitID
							tracker.getShareOfShelf () , // ShareOfShelf
							tracker.getCompetitorShareOfShelf () , // ShareOfShelfCompetitor
							tracker.getCategory () , // Category
							tracker.getNotes () , // Notes
							IsProcessedUtils.isNotSync () , // IsProcessed
							now.getTime () ) ); // StampDate
			
    		// Indicate that a share of shelf tracking was successfully performed during this visit
    		CallAction.setCallActionStatus ( this , visitID , CallAction.ID.SHARE_OF_SHELF , true );	
			// Commit changes
    		daoSession.getDatabase ().setTransactionSuccessful ();
			// Indicate that the save was successful
			Vibration.vibrate ( ShareOfShelfTrackerDetailsActivity.this );
		} catch ( Exception exception ) {
			// Indicate that an error occurred
			error = true;
		} finally {
			// End transaction
			daoSession.getDatabase ().endTransaction ();
		} // End try-catch-finally block
		
    	// Call this to set the result that your activity will return to its caller
		Intent intent = new Intent();
		intent.putExtra ( CallMenuActivity.ACTION_RESULT_SUCCESS , ! error );
		//intent.putExtra ( SalesOrderActivity.PRINT , transactionHeaderCode );
    	setResult ( RESULT_OK , intent );
		// Finish this activity
		finish ();
		// Indicate that the save was successful
		Vibration.vibrate ( this );
    }
    
	/*
	 * Called when the user submits the query.
	 *
	 * @see android.widget.SearchView.OnQueryTextListener#onQueryTextSubmit(java.lang.String)
	 */
	@Override
	public boolean onQueryTextSubmit ( String query ) {
		// No queries are submitted
		return false;
	}
    
	/*
	 * Called when the query text is changed by the user.
	 *
	 * @see android.widget.SearchView.OnQueryTextListener#onQueryTextChange(java.lang.String)
	 */
	@Override
	public boolean onQueryTextChange ( String newText ) {
		// Check if the new search query is equal to the current search query
		if ( newText.equals ( searchQuery ) )
			// Do nothing
			// Indicate that the action was handled by the listener
			return true;
		// Update the new search query
		searchQuery = newText;
		// Filter the list asynchronously
		new FilterList ( newText ).execute ();
		// Indicate that the action was handled by the listener
		return true;
	}
	
	/**
	 * Indicates whether the trackers has new / unsaved modifications.<br>
	 * This method iterates over the list of all trackers and compares the previous versus the current values.<br>
	 * 
	 * @return	Boolean stating if there are new modifications.
	 */
	private boolean hasModifications () {
		String userCode = DatabaseUtils.getCurrentUserCode ( this );
		 // Retrieve the company code
		String companyCode = ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClientDivision ().getCompanyCode ();
		
		// Check if the trackers list is valid
		if ( trackers != null )
			// Iterate over all trackers
			for ( Tracker tracker : trackers )
				// Check if the current tracker is modified
				//if ( tracker.isModified (this,userCode,companyCode) )
					// Indicate that there is at least one modification
					return true;
		// Otherwise there are no new modifications
		return false;
	}
	
	/**
	 * Filters the {@link #trackers} list based on the indicated {@link #searchQuery} string and the {@link #selectedDivisionsCodes} list.<br>
	 * <b>NOTE : </b> This method does not perform any kind of integrity check what so ever !
	 * 
	 * @return	List of {@link me.SyncWise.Android.Modules.ShareOfShelf.Tracker Tracker} objects filtered according to the search and divisions filter.
	 */
	@SuppressLint("DefaultLocale") 
	protected ArrayList < Tracker > filter () {
		// Declare and initialize a new trackers list
		ArrayList < Tracker > _trackers = new ArrayList < Tracker > ();
		// Iterate over all the trackers
		for ( Tracker tracker : trackers )
			// Match the filter with the brand description (contains) and division
			if ( tracker.getBrand ().getBrandDescription ().toLowerCase ().contains ( searchQuery.toLowerCase () ) )
//					&& selectedDivisionsCodes.contains ( tracker.getBrand ().getDivisionCode () ) )
				// Add the tracker to the list
				_trackers.add ( tracker );
		
		// Return the new trackers list
		return _trackers;
	}
	
	/**
	 * AsyncTask helper class used to populate the brands list with the appropriate share of shelves.
	 * 
	 * @author Elias
	 *
	 */
	private class PopulateList extends AsyncTask < Void , Void , ArrayList < Tracker > > {
		
		/**
		 * Flag used to determine if an error occurred.
		 */
		private boolean error;
		
		/**
		 * Flag used to indicated if the activity ended before the asynctask finished executing.
		 */
		private boolean activityEnded;
		
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@SuppressWarnings("unchecked")
		@Override
		protected ArrayList < Tracker > doInBackground ( Void ... params ) {
			try {
				// Check if the trackers are to be retrieved
				if ( retrieveTrackers )
					// Retrieve stored trackers
					trackers = (ArrayList < Tracker >) ActivityInstance.readDataGson ( ShareOfShelfTrackerDetailsActivity.this , ShareOfShelfTrackerDetailsActivity.class.getName () , TRACKERS , new TypeToken < ArrayList < Tracker > > () {}.getType () );
				// Check if the trackers list is valid
				if ( trackers != null )
					// Return the trackers list
					return trackers;
				
				// Retrieve the client code
				String clientCode = ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClientDivision ().getClientCode ();
				// Retrieve the company code
				String companyCode = ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClientDivision ().getCompanyCode ();
				// Retrieve the division code
				String divisionCode = ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClientDivision ().getDivisionCode ();
				// Retrieve a reference to the dao session
				DaoSession daoSession = DatabaseUtils.getInstance ( ShareOfShelfTrackerDetailsActivity.this ).getDaoSession ();
				// Retrieve the current date
				Calendar calendar = Calendar.getInstance ();
				// Retrieve the start of the day
				calendar.set ( Calendar.HOUR_OF_DAY , 0 );
				calendar.set ( Calendar.MINUTE , 0 );
				calendar.set ( Calendar.SECOND , 0 );
				calendar.set ( Calendar.MILLISECOND , 0 );
				long startDay = calendar.getTimeInMillis ();
				// Retrieve the end of the day
				calendar.set ( Calendar.HOUR_OF_DAY , 23 );
				calendar.set ( Calendar.MINUTE , 59 );
				calendar.set ( Calendar.SECOND , 59 );
				calendar.set ( Calendar.MILLISECOND , 999 );
				long endDay = calendar.getTimeInMillis ();
				
				// Retrieve all divisions
				List < Divisions > allDivisions = daoSession.getDivisionsDao ().queryBuilder ()
						.where ( DivisionsDao.Properties.CompanyCode.eq ( companyCode ) ).list ();
				// Retrieve the divisions linked to the user
				List < Divisions > directUserDivisions = daoSession.getDivisionsDao ()
						.queryRaw ( " WHERE CompanyCode = ? AND DivisionCode IN ( SELECT DivisionCode FROM UserDivisions WHERE CompanyCode = ? ) " ,
								new String [] { companyCode , companyCode } );
				allDivisions.removeAll ( directUserDivisions );
				// Retrieve the user children division
				List < Divisions > allUserDivisions = DivisionsUtils.getChildren ( directUserDivisions , allDivisions );
				// Add the direct user divisions to the main list
				allUserDivisions.addAll ( directUserDivisions );
				
				// Check if the divisions list is valid
				if ( divisions == null ) {
					// Initialize the divisions list
					divisions = new ArrayList < Divisions > ();
					// Display all the user divisions
					divisions.addAll ( allUserDivisions );
				} // End if
				
				// Check if the selected divisions codes is valid
				if ( selectedDivisionsCodes == null ) {
					// Initialize the selected divisions codes
					selectedDivisionsCodes = new ArrayList < String > ();
					// Iterate over all the divisions
					for ( Divisions division : divisions )
						// Add the division code to the list
						selectedDivisionsCodes.add ( division.getDivisionCode () );
				} // End if
				
				// Retrieve the share of shelves for the current client
				List < ShareOfShelves > shareOfShelves = daoSession.getShareOfShelvesDao ().queryBuilder ()
						.where ( ShareOfShelvesDao.Properties.ClientCode.eq ( clientCode ) ,
								ShareOfShelvesDao.Properties.CompanyCode.eq ( companyCode ) ,
								ShareOfShelvesDao.Properties.DivisionCode.eq ( divisionCode ) ).list ();
				
				// Initialize the main list
				trackers = new ArrayList < Tracker > ();
				
				// Iterate over the share of shelves
				for ( ShareOfShelves shareOfShelf : shareOfShelves ) {
					try {
						// Retrieve the brand
						Brands brand = daoSession.getBrandsDao ().queryBuilder ().where ( BrandsDao.Properties.BrandCode.eq ( shareOfShelf.getBrandCode () ) ).unique ();
						// Create a new tracker object
						Tracker tracker = new Tracker ( brand );
						// Set the BDA meter
						tracker.setBDAMeter ( shareOfShelf.getBDAYearlyMeter () );
						// Set the BDA percentage
						tracker.setBDAPercentage ( shareOfShelf.getBDAYearlyPercentage () );
						// Retrieve the latest user input
						ShareOfShelfTracker shareOfShelfTracker = daoSession.getShareOfShelfTrackerDao ().queryBuilder ()
								.where ( ShareOfShelfTrackerDao.Properties.ClientCode.eq ( clientCode ) ,
										ShareOfShelfTrackerDao.Properties.CompanyCode.eq ( companyCode ) ,
										ShareOfShelfTrackerDao.Properties.DivisionCode.eq ( divisionCode ) ,
										ShareOfShelfTrackerDao.Properties.BrandCode.eq ( shareOfShelf.getBrandCode () ) ,
										ShareOfShelfTrackerDao.Properties.StampDate.between ( startDay , endDay ) )
								.orderDesc ( ShareOfShelfTrackerDao.Properties.TransactionID ).limit ( 1 ).unique ();
						// Check if the tracker is valid
						if ( shareOfShelfTracker != null ) {
							// Set the previous share of shelf
							tracker.setPreviousShareOfShelf ( shareOfShelfTracker.getShareOfShelf () );
							// Set the previous competitor share of shelf
							tracker.setPreviousCompetitorShareOfShelf ( shareOfShelfTracker.getShareOfShelfCompetitor () );
							// Set the previous category
							tracker.setPreviousCategory ( shareOfShelfTracker.getCategory () );
							// Set the previous notes
							tracker.setPreviousNotes ( shareOfShelfTracker.getNotes () );
							// Set the current notes
							tracker.setCurrentNotes ( shareOfShelfTracker.getNotes () );
						} // End if
						else {
							// Set the previous share of shelf
							tracker.setPreviousShareOfShelf ( shareOfShelf.getShareOfShelf () );
							// Set the previous competitor share of shelf
							tracker.setPreviousCompetitorShareOfShelf ( shareOfShelf.getShareOfShelfCompetitor () );
							// Set the previous category
							tracker.setPreviousCategory ( shareOfShelf.getCategory () );
						} // End if
						// Add the tracker to the list
						trackers.add ( tracker );
					} catch ( Exception exception ) {
						// Ignore the current iteration
						exception.printStackTrace ();
					} // End of try-catch block
				} // End for each
	    		// Sort the trackers based on the brand names
	    		Collections.sort ( trackers , new Comparator < Tracker > () {
					@Override
					public int compare ( Tracker tracker1 , Tracker tracker2 ) {
						// Sort the trackers based on the brand names
						return tracker1.getBrand ().getBrandDescription ().compareToIgnoreCase ( tracker2.getBrand ().getBrandDescription () );
					}
				} );
				// Return the trackers list
				return trackers;
			} catch ( Exception exception) {
				// Set flag
				activityEnded = true;
				// Activity ended prematurely
				return null;
			} // End of try-catch block
				
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( ArrayList < Tracker > trackers ) {
			// Check if the activity has ended
			if ( activityEnded )
				// Do nothing
				return;
			// Determine if the activity must be finished
			if ( error ) {
				// Indicate that the activity cannot be displayed
				new AppToast ( ShareOfShelfTrackerDetailsActivity.this )
					.setIcon ( R.drawable.warning )
					.setText ( AppResources.getInstance ( ShareOfShelfTrackerDetailsActivity.this ).getString ( ShareOfShelfTrackerDetailsActivity.this , R.string.share_of_shelf_tracker_populate_list_error_message ) )
					.setDuration ( Toast.LENGTH_LONG )
					.show ();
				// Initialize the orders list
				ShareOfShelfTrackerDetailsActivity.this.trackers = new ArrayList < Tracker > ();
				// Exit method
				return;
			} // End if
			// Set a new adapter
			setListAdapter ( new ShareOfShelfTrackerDetailsAdapter ( ShareOfShelfTrackerDetailsActivity.this , R.layout.share_of_shelf_item , new ArrayList < Tracker > ( trackers ) , ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) , getIntent ().getBooleanExtra ( IS_NOTE_ONLY , false ) ) );
    		// Refresh the action bar
    		invalidateOptionsMenu ();
		}
		
	}
	
	/**
	 * AsyncTask helper class used to filter the list.
	 * 
	 * @author Elias
	 *
	 */
	private class FilterList extends AsyncTask < Void , Void , ArrayList < Tracker > > {
		
		/**
		 * String holding the search query being applied on the list.
		 */
		private String searchQuery;
		
		/**
		 * Constructor.
		 * 
		 * @param searchQuery	String holding the search query being applied on the list.
		 */
		public FilterList ( String searchQuery ) {
			// Superclass method call
			super ();
			// Initialize attribute
			this.searchQuery = searchQuery;
		}
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected ArrayList < Tracker > doInBackground ( Void ... params ) {
			// Check if the list of trackers is valid
			if ( trackers == null || trackers.isEmpty () )
				// Invalid trackers list
				return null;
			// Otherwise there is a filter
			// Compute and return the filtered list based on the provided search query
			return filter ();
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute ( ArrayList < Tracker > trackers ) {
			// Check if the result is valid
			if ( trackers == null )
				// Invalid result, do nothing
				return;
			// Check if the search query has changed
			if ( ! ShareOfShelfTrackerDetailsActivity.this.searchQuery.equals ( searchQuery ) )
				// Do nothing, as another AsyncTask is executing and will return the appropriate result
				return;
			// Otherwise, determine if the list adapter is valid
			if ( getListAdapter () == null )
				// Invalid list adapter
				return;
			// Check if the list adapter is an ArrayAdapter
			if ( ! ( getListAdapter () instanceof ArrayAdapter < ? > ) )
				// Invalid list adapter
				return;
			try {
				// Clear the previous list of trackers
				( (ArrayAdapter < Tracker >) getListAdapter () ).clear ();
				// Add the new filtered list of trackers
				( (ArrayAdapter < Tracker >) getListAdapter () ).addAll ( trackers );
				// Notifies the attached observers that the underlying data has been changed
				( (ArrayAdapter < Tracker >) getListAdapter () ).notifyDataSetChanged ();
			} catch ( Exception exception ) {
				// Cannot cast to array adapter of orders objects
			} // End try-catch block
		}
		
	}
    
}