/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Call;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.SyncWise.Android.ActivityTransition;
import me.SyncWise.Android.App;
import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppDialog.ButtonsType;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.DateTime;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Vibration;
import me.SyncWise.Android.Database.ClientCreditingsDao;
import me.SyncWise.Android.Database.ClientPropertiesDao;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.Divisions;
import me.SyncWise.Android.Database.DivisionsDao;
import me.SyncWise.Android.Database.DivisionsUtils;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Database.ReasonsUtils;
import me.SyncWise.Android.Database.StatusUtils;
import me.SyncWise.Android.Database.SurveyAssignmentsDao;
import me.SyncWise.Android.Database.SurveyAssignmentsUtils;
import me.SyncWise.Android.Database.SurveysDao;
import me.SyncWise.Android.Database.SurveysUtils;
import me.SyncWise.Android.Database.TransactionHeaders;
import me.SyncWise.Android.Database.TransactionHeadersDao;
import me.SyncWise.Android.Database.TransactionHeadersUtils;
import me.SyncWise.Android.Database.UserPasswordsUtils;
import me.SyncWise.Android.Database.Users;
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Database.VisitReasons;
import me.SyncWise.Android.Database.Visits;
import me.SyncWise.Android.Modules.ClientsList.ClientInfo.ClientInfoActivity;
import me.SyncWise.Android.Modules.Journey.Call;
import me.SyncWise.Android.Modules.MenuList.MenuItem;
import me.SyncWise.Android.Modules.MenuList.MenuListActivity;
import me.SyncWise.Android.Modules.MenuList.MenuListAdapter;
 
import me.SyncWise.Android.Modules.Reason.ReasonActivity;
 
import me.SyncWise.Android.Utilities.ClientCard;
import me.SyncWise.Android.Widgets.Baguette;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * Activity implemented to display the available / performed actions during a call.
 * 
 * @author Elias
 * @sw.todo <b>Call Menu Activity Implementation :</b><br>
 * <ul>
 * <li>Extend this class.</li>
 * <li>Implement the {@link #populateCallMenu()} method, which should define the menu items using the {@link #addMenuItem(MenuItem)} method.</li>
 * <li>Do not forget to add your new class in the {@code AndroidManifest.xml} file.</li>
 * <li>Set a reference to the subclass in the {@link me.SyncWise.Android.App App} class, using the {@link me.SyncWise.Android.App#setCallMenuActivityClass(Class) setCallMenuActivityClass} method.</li>
 * </ul>
 *
 */
public abstract class CallMenuActivity extends MenuListActivity {
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #isCreated}.
	 */
	private static final String IS_CREATED = CallMenuActivity.class.getName () + ".IS_CREATED";
	
	/**
	 * Boolean used to indicate if the activity has been previously created.<br>
	 * This flag is mainly used to determine if the current activity creation is the first or not (activity re-creation due to phone rotation for example).
	 */
	private boolean isCreated;
	
	/**
	 * Constant integer holding the request code used to prompt the user for one or more reasons.
	 */
	private static final int REQUEST_CODE_REASONS = 0;
	
	/**
	 * Bundle key used to put/retrieve the content of the client call.
	 */
	public static final String CALL = CallMenuActivity.class.getName () + ".CALL";
	
	/**
	 * {@link me.SyncWise.Android.Modules.Journey.Call Call} object holding a reference to the call in progress.
	 */
	protected Call call;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #visit}.
	 */
	public static final String VISIT = CallMenuActivity.class.getName () + ".VISIT";
	
	/**
	 * {@link me.SyncWise.Android.Database.Visits Visits} object holding a reference to the call in progress.
	 */
	protected Visits visit;
	
	/**
	 * Bundle key used to put/retrieve the action result flag indicating if the action was successful or not.
	 */
	public static final String ACTION_RESULT_SUCCESS = CallMenuActivity.class.getName () + ".ACTION_RESULT_SUCCESS";
	
	/**
	 * Bundle key used to put/retrieve the skip result flag indicating if the result message is to be skipped or not.
	 */
	public static final String SKIP_ACTION_RESULT = CallMenuActivity.class.getName () + ".SKIP_ACTION_RESULT";
	
	/**
	 * Bundle key used to put/retrieve the skip result flag indicating if the menu items have their icons dimmed or not.
	 */
	private static final String ITEMS_DIM_ICON = CallMenuActivity.class.getName () + ".ITEMS_DIM_ICON";
	
	/**
	 * Bundle key used to put/retrieve the skip result flag indicating if the menu item actions are performed or not.
	 */
	private static final String ITEMS_ACTION_PERFORMED = CallMenuActivity.class.getName () + ".ITEMS_ACTION_PERFORMED";
	
	/**
	 * Reference to the {@link android.widget.Chronometer Chronometer} object used to provide information about the visit duration.
	 */
	private Chronometer chronometer;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #displayPasscode}.
	 */
	private static final String DISPLAY_PASSCODE = CallMenuActivity.class.getName () + ".DISPLAY_PASSCODE";
	
	/**
	 * Boolean used to determine whether to display the passcode UI or not.
	 */
	protected boolean displayPasscode;
	
	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate ( Bundle savedInstanceState ) {
		// Retrieve the visit object
		visit = (Visits) getIntent ().getSerializableExtra ( VISIT );
		// Retrieve the call object
		call = (Call) getIntent ().getSerializableExtra ( CALL );
		
		// Populate call menu
		populateCallMenu ();
		
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state
			isCreated = savedInstanceState.getBoolean ( IS_CREATED , isCreated );
			call = (Call) savedInstanceState.getSerializable ( CALL );
			displayPasscode = savedInstanceState.getBoolean ( DISPLAY_PASSCODE );
	    	boolean iconDimmed [] = savedInstanceState.getBooleanArray ( ITEMS_DIM_ICON );
	    	boolean actionPerformed [] = savedInstanceState.getBooleanArray ( ITEMS_ACTION_PERFORMED );
	    	// Compute the total number of menu items
	    	int count = getCount ();
	    	// Iterate over all the menu items
	    	for ( int i = 0 ; i < count ; i ++ ) {
	    		// Restore the icon dimmed state
	    		getMenuItem ( i ).setDimIcon ( iconDimmed [ i ] );
	    		// Restore the action performed state
	    		( (CallMenuItem) getMenuItem ( i ) ).setActionPerformed ( actionPerformed [ i ] );
	    	} // End for loop
		} // End if
		
		// Set the custom menu item layout
		setMenuItemLayout ( R.layout.call_menu_activity_item );
		
		// Superclass method call	
		super.onCreate ( savedInstanceState );
		// Set the activity content from a layout resource.
		setContentView ( R.layout.call_menu_activity );
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.call_menu_activity_title ) );
		
		// Check if both the visit and the call are valid
		if ( call == null || visit == null ) {
			// Invalid visit and/or client
			finish ();
			return;
		} // End if
		
		// Set the visit ID in the call action shared preferences
		CallAction.setCall ( this , visit.getVisitID () );
		// Display the visit start date as sub title
		Calendar startDate = Calendar.getInstance ();
		startDate.setTime ( visit.getStartDate () );
		getActionBar ().setSubtitle ( AppResources.getInstance ( this ).getString ( this , R.string.visit_start_date_label )
				+ " " + DateTime.getFullTime ( this , startDate ) );
		// Initialize the client card
		ClientCard.initializeClientCard ( this , call.getClient () );
		// Retrieve a reference to the chronometer
		chronometer = (Chronometer) findViewById ( R.id.chronometer );
		// Set the time that the count-up timer is in reference to
		chronometer.setBase ( SystemClock.elapsedRealtime () - ( Calendar.getInstance ().getTimeInMillis () - visit.getStartDate ().getTime () ) );
		
		// Determine if this is the first activity creation or a re-creation
		if ( ! isCreated ) {
			// Set the flag
			isCreated = true;
			// Retrieve the current user
			Users user = DatabaseUtils.getInstance ( this ).getDaoSession ().getUsersDao ().queryBuilder ()
					.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( this ) ) , UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( this ) ) ).unique ();
			// Check if the current user is cash van
			if ( user.getUserType () == 11 )
				// Display a warning if needed
				displayDuesWarning ();
		} // End if
	}
	
	/*
	 * Gets a new menu list adapter.<br>
	 * The default implementation returns null.<br>
	 * Subclasses can override this method to apply a custom list adapter.
	 *
	 * @see me.SyncWise.Android.Modules.MenuList.MenuListActivity#getMenuListAdapter(int, java.util.List)
	 */
	@Override
	protected ListAdapter getMenuListAdapter ( int layout , List < MenuItem > menuItems ) {
		// Initialize and return a custom call menu adapter
		return new CallMenuAdapter ( this , layout , menuItems );
	}
	
	/**
	 * Populate the call menu.
	 */
	protected abstract void populateCallMenu ();
	
	/*
	 * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause(), for your activity to start interacting with the user.
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume () {
		// Start counting up
		chronometer.start ();
		// Superclass method call
		super.onResume ();
		
		// Retrieve the company code of the user
		String companyCode = DatabaseUtils.getCurrentCompanyCode ( this );
		// Retrieve all divisions
		List < Divisions > allDivisions = DatabaseUtils.getInstance ( this ).getDaoSession ().getDivisionsDao ().queryBuilder ()
				.where ( DivisionsDao.Properties.CompanyCode.eq ( call.getClientDivision ().getCompanyCode () ) ).list ();
		// Retrieve the divisions linked to the user
		List < Divisions > directUserDivisions = DatabaseUtils.getInstance ( this ).getDaoSession ().getDivisionsDao ()
				.queryRaw ( " WHERE CompanyCode = ? AND DivisionCode IN ( SELECT DivisionCode FROM UserDivisions WHERE CompanyCode = ? ) " ,
						new String [] { companyCode , companyCode } );
		// Retrieve the user children division
		List < Divisions > allUserDivisions = DivisionsUtils.getChildren ( directUserDivisions , allDivisions );
		// Add the direct user divisions to the main list
		allUserDivisions.addAll ( directUserDivisions );
		// Check if the division of the current client belongs to the user
		boolean belongsToUser = false;
		// Iterate over all user divisions
		for ( Divisions division : allUserDivisions )
			// Check if the current division is the client's division
			if ( division.getDivisionCode ().equals ( call.getClientDivision ().getDivisionCode () ) && division.getCompanyCode ().equals ( call.getClientDivision ().getCompanyCode () ) ) {
				// Set flag
				belongsToUser = true;
				// Exit loop
				break;
			} // End if
		
		// Retrieve the client user collection action IDs
		ArrayList < Integer > clientUserCollectionActionIDs = CallAction.getIDs ( CallAction.ClientUserCollectionID.class );
		
		// Iterate over all the call menu items
		for ( int i = 0 ; i < getCount () ; i ++ ) {
			// Retrieve the current call menu item
			CallMenuItem callMenuItem = (CallMenuItem) getMenuItem ( i );
			// Enable current call menu item is accordingly.
			// Check if the current action is NOT a client user collection one
			if ( ! clientUserCollectionActionIDs.contains ( callMenuItem.getID () ) ) {
				// This action should be enabled for clients who belong to the user
				callMenuItem.setDisabled ( ! belongsToUser );
				// Check if the action is disabled
				if ( ! belongsToUser )
					// Assign the appropriate toast ID
					callMenuItem.setToastId ( R.string.cannot_perform_action_not_linked_client_message );
			} // End if
		} // End for loop
		
		// Display the cash client warning if needed
		if ( displayPasscode ) {
    		// Initialize the tertiary view
    		initializeTertiaryView ( true );
    		// Retrieve a reference to the tertiary view
    		View tertiaryView = findViewById ( R.id.layout_passcode );
    		// Display the tertiary view
    		tertiaryView.setVisibility ( View.VISIBLE );
    		// Refresh the action bar
    		invalidateOptionsMenu ();
		} // End if
	}
	
	/*
	 * Initialize the contents of the Activity's standard options menu.
	 *
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
    @Override
    public boolean onCreateOptionsMenu ( Menu menu ) {
		// Check if there is any ongoing movements modifications
		if ( displayPasscode )
    		// Hide the menu
            return false;
		
    	// Use the MenuInflater of this context to inflate a menu hierarchy from the specified XML resource
    	getMenuInflater ().inflate ( R.menu.action_bar , menu );
    	// Enable the required menu items
    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_info ) , R.string.info_label );
    	// Display the menu
    	return true;
    }
    
    /*
     * This hook is called whenever an item in your options menu is selected.
     *
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected ( android.view.MenuItem menuItem ) {
    	// Determine if the action save is selected
    	if ( menuItem.getItemId () == R.id.action_info ) {
			// Create an intent to start a new activity
			Intent intent = new Intent ( this , App.getClientInfoActicityClass () );
			// Add the client code to the intent
			intent.putExtra ( ClientInfoActivity.CLIENT_CODE , call.getClient ().getClientCode () );
			// Add the company code to the intent
			intent.putExtra ( ClientInfoActivity.COMPANY_CODE , call.getClient ().getCompanyCode () );
			// Start the new activity
			startActivity ( intent );
			// Specify an explicit transition animation to perform next
			ActivityTransition.SlideOutLeft ( this );
    		// Consume event
    		return true;
    	} // End if
    	// Allow normal menu processing to proceed
    	return false;
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
		// Refresh the visit object
		DatabaseUtils.getInstance ( this ).getDaoSession ().getVisitsDao ().refresh ( visit );
		
		// Determine if reason(s) are provided
		if ( requestCode == REQUEST_CODE_REASONS ) {
			// Retrieve the reason types
			String reasonTypes [] = data.getStringArrayExtra ( ReasonActivity.REASON_TYPES );
			// Retrieve the reason codes
			String reasonCodes [] = data.getStringArrayExtra ( ReasonActivity.REASON_CODES );
			// Iterate over all reason types
			for ( int i = 0 ; i < reasonTypes.length ; i ++ ) {
				// Determine the reason type
				if ( reasonTypes [ i ].equals ( ReasonsUtils.Type.NO_SALES ) )
					// Save the reason code in the visit
					visit.setTransactionReasonCode ( reasonCodes [ i ] );
				else
					// Save the reason code in a new visit reasons
					DatabaseUtils.getInstance ( this ).getDaoSession ().getVisitReasonsDao ().insert ( new VisitReasons ( null , // ID
							visit.getVisitID () , // VisitID
							reasonTypes [ i ] , // ReasonType
							reasonCodes [ i ] , // ReasonCode
							Calendar.getInstance ().getTime () ) ); // ReasonCode
			} // End for loop

			// End the visit
			visit.setEndDate ( Calendar.getInstance ().getTime () );
			// Update the visit in DB
			DatabaseUtils.getInstance ( this ).getDaoSession ().getVisitsDao ().update ( visit );
			
			// Clear call action data
			CallAction.clearData ( this );
	    	// Call this to set the result that your activity will return to its caller
	    	setResult ( RESULT_OK , new Intent ().putExtra ( VISIT , visit ).putExtra ( CallMenuActivity.CALL , call ) );
			// Finish this activity
			finish ();
			// Exit method
			return;
		} // End if
		
		// Refresh the call action menu based on the call actions that have been achieved / cancelled
		refreshCallActionMenu ( requestCode , data );
		
		// Refresh the list
		( (MenuListAdapter) getListAdapter () ).notifyDataSetChanged ();
    }
	
	/**
	 * Refreshes the call action menu based on the call actions that have been achieved / cancelled during the previously started activity(ies).<br>
	 * For further customization, override this method.<br>
	 * <b>Note : </b>No need to refresh the list view / adapter, this is done internally.
	 * 
	 * @param requestCode	The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
	 * @param data	An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
	 */
	protected void refreshCallActionMenu ( final int requestCode , final Intent data ) {
		// Determine if the visit is blank
		boolean isBlank = CallAction.getCallActionStatus ( this , visit.getVisitID () , CallAction.ID.BLANK_VISIT );
		// Determine if the visit is NOT blank (contains call actions that refers to a non blank visit)
		boolean isNotBlank = CallAction.isNotBlank ( this , visit.getVisitID () );
		// Determine if the blank visit lock should be enforced
		boolean blankVisitLock = PermissionsUtils.getBlankVisitLock ( this , DatabaseUtils.getCurrentUserCode ( this ) , DatabaseUtils.getCurrentCompanyCode ( this ) );
		
		// Compute the total number of menu items
		int count = getCount ();
		
		// Retrieve the IDs of a non blank visit call actions
		ArrayList < Integer > nonBlankVisitIDs = CallAction.getIDs ( CallAction.NotBlankVisitID.class );
		
		// Iterate over all menu items (in order to check the appropriate items, display the appropriate baguette messages, and lock / un-lock the appropriate items)
		for ( int i = 0 ; i < count ; i ++ ) {
			// Determine if the current item represents the current call action
			if ( getMenuItem ( i ).isID ( requestCode ) ) {
				// Modify the action performed status accordingly
				( (CallMenuItem) getMenuItem ( i ) ).setActionPerformed ( CallAction.getCallActionStatus ( this , visit.getVisitID () , requestCode ) );
				// Check if the action result is to be displayed or skipped
				if ( ! data.getBooleanExtra ( SKIP_ACTION_RESULT , false ) )
					// Display the action status (if available)
					if ( ( data.getBooleanExtra ( ACTION_RESULT_SUCCESS , false ) == true && ( (CallMenuItem) getMenuItem ( i ) ).getBaguetteSuccessId () != null )
							|| ( data.getBooleanExtra ( ACTION_RESULT_SUCCESS , false ) == false && ( (CallMenuItem) getMenuItem ( i ) ).getBaguetteFailreId () != null ) )
						Baguette.showText ( this ,
								AppResources.getInstance ( this ).getString ( this , ( data.getBooleanExtra ( ACTION_RESULT_SUCCESS , false ) ? ( (CallMenuItem) getMenuItem ( i ) ).getBaguetteSuccessId () : ( (CallMenuItem) getMenuItem ( i ) ).getBaguetteFailreId () ) ) ,
								( data.getBooleanExtra ( ACTION_RESULT_SUCCESS , false ) ? Baguette.BackgroundColor.GREEN : Baguette.BackgroundColor.RED ) );
			} // End if
			
			// Check if the blank visit lock is enforced
			if ( ! blankVisitLock )
				// Do nothing, skip the remaining of the iteration
				continue;
			// Otherwise enforce the blank visit lock :
			// If the visit is blank : none of the NOT BLANK VISIT actions are allowed
			// Otherwise, if the visit is NOT BLANK, the blank visit is not allowed
			
			// Determine if the visit is blank AND 
			if ( isBlank )
				// The visit is a blank visit
				// If the current menu item is among the NOT BLANK VISIT call actions, it should be disabled and otherwise enabled
				getMenuItem ( i )
					// Set / remove the menu item toast accordingly
					.setToastId ( nonBlankVisitIDs.contains ( getMenuItem ( i ).getID () ) ? R.string.invalid_action_call_message : null )
					// Dim the menu item icon accordingly
					.setDimIcon ( nonBlankVisitIDs.contains ( getMenuItem ( i ).getID () ) ? true : false );

			// Otherwise the visit is NOT blank, determine if the visit contains any non blank visit call actions (is really NOT BLANK)
			else if ( isNotBlank )
				// The visit is (really) NOT BLANK
				// If the current menu item is the BLANK VISIT item, it should be disabled and otherwise enabled
				getMenuItem ( i )
					// Set / remove the menu item toast accordingly
					.setToastId ( ! getMenuItem ( i ).isID ( CallAction.ID.BLANK_VISIT ) ? null : R.string.invalid_action_call_message )
					// Dim the menu item icon accordingly
					.setDimIcon ( ! getMenuItem ( i ).isID ( CallAction.ID.BLANK_VISIT ) ? false : true );
			
			// Otherwise the visit is neither BLANK or NOT BLANK
			else
				// Enable all actions
				getMenuItem ( i )
					// Remove the menu item toast
					.setToastId ( null )
					// Dim the menu item icon accordingly
					.setDimIcon ( false );
		} // End for loop
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
    	// Save the icon dimmed and action performed states of each menu item in an array list
    	// Compute the total number of menu items
    	int count = getCount ();
    	// Declare and initialize 2 arrays to store the states
    	boolean iconDimmed [] = new boolean [ count ];
    	boolean actionPerformed [] = new boolean [ count ];
    	// Iterate over all the menu items
    	for ( int i = 0 ; i < count ; i ++ ) {
    		// Save the icon dimmed state of the current menu item
    		iconDimmed [ i ] = getMenuItem ( i ).getDimIcon ();
    		// Save the action performed state of the current menu item
    		actionPerformed [ i ] = ( (CallMenuItem) getMenuItem ( i ) ).getActionPerformed ();
    	} // End for loop
    	// Save the content of the lists of states in the outState bundle
    	outState.putBooleanArray ( ITEMS_DIM_ICON , iconDimmed );
    	outState.putBooleanArray ( ITEMS_ACTION_PERFORMED , actionPerformed );
    	// Save the content of the call in the outState bundle
    	outState.putSerializable ( CALL , call );
    	// Save the content of displayPasscode in the outState bundle
    	outState.putBoolean ( DISPLAY_PASSCODE , displayPasscode );
    }
	
	/*
	 * Called as part of the activity lifecycle when an activity is going into the background, but has not (yet) been killed.
	 *
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause () {
		// Stop counting up
		chronometer.stop ();
		// Superclass method call
		super.onPause ();
		// Dismiss any displayed dialogs (to avoid activity context leak)
		AppDialog.getInstance ().dismiss ();
		// Remove any displayed baguette
		Baguette.remove ( this );
	}
	
	/*
	 * Called when the activity has detected the user's press of the back key. 
	 *
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed () {
		// Determine if the passcode is undergoing any modifications
		if ( displayPasscode ) {
			// Reset flag
			displayPasscode = false;
			// Retrieve a reference to the tertiary view
			View tertiary = findViewById ( R.id.layout_passcode );
			// Hide the tertiary view
			tertiary.startAnimation ( getViewAnimationOut ( tertiary ) );
    		// Refresh the action bar
    		invalidateOptionsMenu ();
    		// Exit method
    		return;
		} // End else if
		
		// Retrieve the user code
		String userCode = DatabaseUtils.getCurrentUserCode ( this );
		// Retrieve the company code
		String companyCode = DatabaseUtils.getCurrentCompanyCode ( this );
		// Retrieve the user
		Users user = DatabaseUtils.getInstance ( this ).getDaoSession ().getUsersDao ().queryBuilder ()
				.where ( UsersDao.Properties.UserCode.eq ( userCode ) , UsersDao.Properties.CompanyCode.eq ( companyCode ) ).unique ();
		// Declare and initialize a list of strings used to host the required reason types
		ArrayList < String > reasonTypes = new ArrayList < String > ();
		// Declare and initialize a list of strings used to host the appropriate reason sub titles
		ArrayList < String > subTitles = new ArrayList < String > ();
		// Determine if the visit is blank
		boolean isBlank = CallAction.getCallActionStatus ( this , visit.getVisitID () , CallAction.ID.BLANK_VISIT );

		// Check if the current client call is BLANK
		if ( isBlank ) {
			// The visit is blank
			// Check if the blank visit disables reason prompting
			if ( ! PermissionsUtils.getNoReasonIfBlankVisit ( this , userCode , companyCode ) )
				// Reason prompting is ENABLED
				isBlank = false;
		} // End if
		
		// Determine if the user should be prompted for reasons
		if ( ! isBlank ) {
			
			// Check if the call has a current / previous sales call action
			if ( call.isSales () || CallAction.isSales ( this , visit.getVisitID () ) )
				// No reason required for sales
				call.setSales ( true );
			// The call has no current / previous call action
			// Check if a reason is required
			else if ( PermissionsUtils.getPromptSalesReason ( this , userCode , companyCode ) ) {
				// Add the appropriate reason type
				reasonTypes.add ( ReasonsUtils.Type.NO_SALES );
				// Add the appropriate sub title
				subTitles.add ( AppResources.getInstance ( this ).getString ( this , R.string.no_sales_label ) );
			} // End else if
			
			// The call has no current / previous call action
			// Check if a reason is required
			else if ( PermissionsUtils.getPromptObjectiveReason ( this , userCode , companyCode ) ) {
				// Add the appropriate reason type
				reasonTypes.add ( ReasonsUtils.Type.NO_CLIENT_OBJECTIVE );
				// Add the appropriate sub title
				subTitles.add ( AppResources.getInstance ( this ).getString ( this , R.string.no_client_objective_label ) );
			} // End else if
			
			// Check if the call has a current / previous client survey call action
			if ( call.isSurvey () || CallAction.getCallActionStatus ( this , visit.getVisitID () , CallAction.ID.SURVEY ) )
				// No reason required for client survey
				call.setSurvey ( true );
			// The call has no current / previous call action
			// Check if a reason is required
			else if ( PermissionsUtils.getPromptSurveyReason ( this , userCode , companyCode ) ) {
				// Before adding the survey reason type
				// Check if there is at least one survey
				
				// Retrieve the current date
				Calendar today = Calendar.getInstance ();
				// Compute the SQL string
				String SQL = "SELECT COUNT (*) FROM " + SurveysDao.TABLENAME + " S INNER JOIN " + SurveyAssignmentsDao.TABLENAME + " SA ON " +
						"S." + SurveysDao.Properties.SurveyID.columnName + " = SA." + SurveyAssignmentsDao.Properties.SurveyID.columnName + " " +
						"AND ( SA." + SurveyAssignmentsDao.Properties.AssignmentType.columnName + " = ? OR SA." + SurveyAssignmentsDao.Properties.AssignmentType.columnName + " = ? " +
							"OR ( SA." + SurveyAssignmentsDao.Properties.AssignmentType.columnName + " = ? AND SA." + SurveyAssignmentsDao.Properties.AssignmentCode.columnName + " = ? ) " +
							"OR ( SA." + SurveyAssignmentsDao.Properties.AssignmentType.columnName + " = ? AND SA." + SurveyAssignmentsDao.Properties.AssignmentCode.columnName + " IN ( " +
							"SELECT CP." + ClientPropertiesDao.Properties.ClientPropertyValueCode.columnName + " FROM " + ClientPropertiesDao.TABLENAME + " CP WHERE CP." + ClientPropertiesDao.Properties.ClientCode.columnName + " = ? ) ) " +
						" ) WHERE S." + SurveysDao.Properties.SurveyType.columnName + " = ? " +
							"AND ( " + SurveysDao.Properties.StartDate.columnName + " IS NULL OR ? > " + SurveysDao.Properties.StartDate.columnName + " ) " +
							"AND ( " + SurveysDao.Properties.EndDate.columnName + " IS NULL OR ? < " + SurveysDao.Properties.EndDate.columnName + " )" ;
				// Compute the selection arguments
				String selectionArguments [] = new String [] {
							String.valueOf ( SurveyAssignmentsUtils.Type.USER ) ,
							String.valueOf ( SurveyAssignmentsUtils.Type.GROUP_USERS ) ,
							String.valueOf ( SurveyAssignmentsUtils.Type.CLIENT ) ,
							call.getClient ().getClientCode () ,
							String.valueOf ( SurveyAssignmentsUtils.Type.CLIENT_PROPERTIES ) ,
							call.getClient ().getClientCode () ,
							String.valueOf ( SurveysUtils.Type.CLIENT_SURVEY ) ,
							String.valueOf ( today ) ,
							String.valueOf ( today )
						};
				
				// Declare and initialize the number of client surveys
				int surveysNumber = 0;
				// Retrieve the corresponding survey IDs
				Cursor cursor = DatabaseUtils.getInstance ( CallMenuActivity.this ).getDaoSession ().getDatabase ().rawQuery ( SQL , selectionArguments );
				// Move the cursor to the first row
				if ( cursor.moveToFirst () == true )
					// Retrieve the surveys number
					surveysNumber = cursor.getInt ( 0 );
				// Close the cursor
				cursor.close ();
				cursor = null;
				
				// Check if there is at least one survey
				if ( surveysNumber != 0 ) {
					// Add the appropriate reason type
					reasonTypes.add ( ReasonsUtils.Type.NO_CLIENT_SURVEY );
					// Add the appropriate sub title
					subTitles.add ( AppResources.getInstance ( this ).getString ( this , R.string.no_client_survey_label ) );
				} // End if
			} // End else if
			
			// Determine if there is at least one required reason type
			if ( ! reasonTypes.isEmpty () ) {
				// Prompt the user for the required reason types
				// Create a new intent to start a new activity
				Intent intent = new Intent ( this , ReasonActivity.class );
				// Add the client object to the intent
				intent.putExtra ( ReasonActivity.CLIENT , call.getClient () );
				// Add the reason types to the intent
				intent.putExtra ( ReasonActivity.REASON_TYPES , reasonTypes.toArray ( new String [ reasonTypes.size () ] ) );
				// Add the activity sub title to the intent
				intent.putExtra ( ReasonActivity.ACTIVITY_SUB_TITLE , subTitles.toArray ( new String [ subTitles.size () ] ) );
				// Start the new activity
				startActivityForResult ( intent , REQUEST_CODE_REASONS );
				// Specify an explicit transition animation to perform next
				ActivityTransition.SlideOutLeft ( this );
				// Exit method
				return;
			} // End if
			
		} // End if
		
		// Check the logged in user is cash van and the client is of type cash
		if ( user.getUserType () == 11 && ClientCard.isCashClient ( call.getClient () ) ) {
			
				
			 
			
			// Check if there is at least one pending invoice
			if ( DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionHeadersDao ().queryBuilder ()
					.where ( TransactionHeadersDao.Properties.VisitID.eq ( visit.getVisitID () ) ,
							 TransactionHeadersDao.Properties.RemainingAmount.gt ( 0 ) ,
							 TransactionHeadersDao.Properties.TransactionType.eq ( TransactionHeadersUtils.Type.SALES_INVOICE ) ,
							 TransactionHeadersDao.Properties.IsProcessed.eq ( IsProcessedUtils.isNotSync () ) ,
							 TransactionHeadersDao.Properties.TransactionStatus.eq ( StatusUtils.isAvailable () ) ).count () > 0 ) {
				if ( PermissionsUtils.getEnableCashEndVisitsNoPassword ( this ,   DatabaseUtils.getCurrentUserCode (  this ) ,  DatabaseUtils.getCurrentCompanyCode( this) ) ) 
				{
				
				// Display warning
				displayCashClientWarningNew ();
				// End method
				return;
				}
				else{
					// Display warning
					displayCashClientWarning ();
					// End method
					return;
					
				}
				
			} // End if
		} // End if
		
		// Check the logged in user is cash van and the client is of type cash
		if ( user.getUserType () == 11 && ClientCard.isCreditClient( call.getClient () ) ) {
				 
			if(exceedsCreditAmmount ( call.getClient ().getClientCode() , call.getClient().getCompanyCode()
									 , call.getClient().getDivisionCode()))
			// Check if there is at least one pending invoice
 			if ( DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionHeadersDao ().queryBuilder ()
					.where ( TransactionHeadersDao.Properties.VisitID.eq ( visit.getVisitID () ) ,
							 TransactionHeadersDao.Properties.RemainingAmount.gt ( 0 ) ,
						     TransactionHeadersDao.Properties.TransactionType.eq ( TransactionHeadersUtils.Type.SALES_INVOICE ) ,
							 TransactionHeadersDao.Properties.IsProcessed.eq ( IsProcessedUtils.isNotSync () ) ,
							 TransactionHeadersDao.Properties.TransactionStatus.eq ( StatusUtils.isAvailable () ) ).count () > 0 ) {
 				if ( PermissionsUtils.getEnableCreditEndVisitsNoPassword ( this ,   DatabaseUtils.getCurrentUserCode (  this ) ,  DatabaseUtils.getCurrentCompanyCode( this) ) ) 
 				{
 				// Display warning
 			    	displayCreditClientWarningNew ();
 				    return;
 				}
 				else
 				{
 				// Display warning
	            displayCreditClientWarning ();
				// End method
				return;
 				}
			} // End if
			 
		} // End if
		String a="1";
		
		// Check the logged in user is cash van and the client is of type cash
				if ( user.getUserType () == 11 && ClientCard.isCreditClient( call.getClient () ) ) {
				//	if ( PermissionsUtils.getEnableCreditEndVisitsCashSelected ( this ,   DatabaseUtils.getCurrentUserCode (  this ) ,  DatabaseUtils.getCurrentCompanyCode( this) ) ) 
					//{
						 
					// Check if there is at least one pending invoice
		 			if ( DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionHeadersDao ().queryBuilder ()
							.where ( TransactionHeadersDao.Properties.VisitID.eq ( visit.getVisitID () ) ,
									 TransactionHeadersDao.Properties.RemainingAmount.gt ( 0 ) ,
								     TransactionHeadersDao.Properties.TransactionType.eq ( TransactionHeadersUtils.Type.SALES_INVOICE ) ,
									 TransactionHeadersDao.Properties.IsProcessed.eq ( IsProcessedUtils.isNotSync () ) ,
									 TransactionHeadersDao.Properties.TransactionStatus.eq ( StatusUtils.isAvailable () ),
									 TransactionHeadersDao.Properties.Info1.eq ( a ) ).count () > 0 ) {
				
		 				if ( PermissionsUtils.getEnableCreditEndVisitsNoPassword ( this ,   DatabaseUtils.getCurrentUserCode (  this ) ,  DatabaseUtils.getCurrentCompanyCode( this) ) ) 
		 				{
		 				// Display warning
		 			    	displayCreditClientWarningCashNew ();
		 				    return;
		 				}
		 				else
		 				{
		 				// Display warning
			            displayCreditClientWarningCash ();
						// End method
						return;
		 				}
					} // End if
				} // End if
				//}
				String SQL = "UPDATE " + TransactionHeadersDao.TABLENAME + " " +
						"SET " + TransactionHeadersDao.Properties.TransactionStatus.columnName + " = ? " +
						"WHERE " + TransactionHeadersDao.Properties.TransactionStatus.columnName + " = ? ";
				// Compute the selection arguments
				String selectionArguments [] = new String [] {
						String.valueOf ( StatusUtils.isDeleted () ) , String.valueOf ( StatusUtils.isDeleted () )
				};
				// Execute the transaction update query
				DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().execSQL ( SQL , selectionArguments );
				
		
		// If this line is reached, than the call menu activity can finish
		// Refresh the visit object
		DatabaseUtils.getInstance ( this ).getDaoSession ().getVisitsDao ().refresh ( visit );
		// End the visit
		visit.setEndDate ( Calendar.getInstance ().getTime () );
		// Update the visit in DB
		DatabaseUtils.getInstance ( this ).getDaoSession ().getVisitsDao ().update ( visit );
		
		// Clear call action data
		CallAction.clearData ( this );
    	// Call this to set the result that your activity will return to its caller
    	setResult ( RESULT_OK , new Intent ().putExtra ( VISIT , visit ).putExtra ( CALL , call ) );
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
			visit = null;
			call = null;
			chronometer = null;
		} // End if
	}
	
	/**
	 * Displays a warning regarding overdue amounts (in the current / other divisions of the same company).
	 */
	private void displayDuesWarning () {
		// Check if the client has over due amounts for the current division
		boolean hasOverDuesCurrentDivision = ClientCard.hasOverduesCurrentDivision ( this , call.getClient () );
		// Check if the client has over due amounts for the current company
		boolean hasOverDuesCurrentCompany = ClientCard.hasOverduesCurrentCompany ( this , call.getClient () );
		// Build description
		String description = null;
		if ( hasOverDuesCurrentDivision && ! hasOverDuesCurrentCompany )
			description = AppResources.getInstance ( this ).getString ( this , R.string.has_overdues_current_division );
		else if ( ! hasOverDuesCurrentDivision && hasOverDuesCurrentCompany )
			description = AppResources.getInstance ( this ).getString ( this , R.string.has_overdues_other_divisions );
		else if ( hasOverDuesCurrentDivision && hasOverDuesCurrentCompany )
			description = AppResources.getInstance ( this ).getString ( this , R.string.has_overdues_current_company );
		// Check if the message is valid
		if ( description == null )
			// Exit method
			return;
		// Otherwise display message
		AppDialog.getInstance ().displayAlert ( this ,
				AppResources.getInstance ( this ).getString ( this , R.string.warning_label ) ,
				description ,
				ButtonsType.OK , null );
	}
	
	/**
	 * Displays a warning regarding unpaid invoices for cash client.<br>
	 * The user has the option to use a pass code.
	 */
	private void displayCashClientWarning () {
		// Declare and initialize an alert dialog builder
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder ( this );
		// By default, the alert dialog cannot be cancelled UNLESS it has no buttons
		alertDialogBuilder.setCancelable ( false );
		// Set the title
		alertDialogBuilder.setTitle ( R.string.warning_label );
		// Set the description
		alertDialogBuilder.setMessage ( R.string.cannot_end_visit_unpaid_invoice_message );
		// Map the positive and negative buttons
		alertDialogBuilder.setPositiveButton ( R.string.ok_label , null );
		alertDialogBuilder.setNegativeButton ( R.string.use_passcode , new DialogInterface.OnClickListener() {
			@Override
			public void onClick ( DialogInterface dialog , int which ) {
	    		// Set flag
	    		displayPasscode = true;
	    		// Initialize the tertiary view
	    		initializeTertiaryView ( false );
	    		// Retrieve a reference to the tertiary view
	    		View tertiaryView = findViewById ( R.id.layout_passcode );
	    		// Display the tertiary view
	    		tertiaryView.setVisibility ( View.VISIBLE );
	    		// Animate the tertiary view
	    		tertiaryView.startAnimation ( getViewAnimationIn() );
	    		// Refresh the action bar
	    		invalidateOptionsMenu ();
			}
		} );
		// Create and show the alert dialog
		alertDialogBuilder.create ().show ();
	}
	

	/**
	 * Displays a warning regarding unpaid invoices for cash client.<br>
	 * The user has the option to use a pass code.
	 */
	private void displayCashClientWarningNew () {
		// Declare and initialize an alert dialog builder
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder ( this );
		// By default, the alert dialog cannot be cancelled UNLESS it has no buttons
		alertDialogBuilder.setCancelable ( false );
		// Set the title
		alertDialogBuilder.setTitle ( R.string.warning_label );
		// Set the description
		alertDialogBuilder.setMessage ( R.string.cannot_end_visit_unpaid_invoice_message12 );
		// Map the positive and negative buttons
		alertDialogBuilder.setPositiveButton ( R.string.ok_label , null );
		 
		// Create and show the alert dialog
		alertDialogBuilder.create ().show ();
	}
	
	
	/**
	 * Displays a warning regarding unpaid invoices for cash client.<br>
	 * The user has the option to use a pass code.
	 */
	private void displayCreditClientWarning () {
		// Declare and initialize an alert dialog builder
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder ( this );
		// By default, the alert dialog cannot be cancelled UNLESS it has no buttons
		alertDialogBuilder.setCancelable ( false );
		// Set the title
		alertDialogBuilder.setTitle ( R.string.warning_label );
		// Set the description
		alertDialogBuilder.setMessage ( R.string.cannot_end_visit_unpaid_invoice_message1 );
		// Map the positive and negative buttons
		alertDialogBuilder.setPositiveButton ( R.string.ok_label , null );
		alertDialogBuilder.setNegativeButton ( R.string.use_passcode , new DialogInterface.OnClickListener() {
			@Override
			public void onClick ( DialogInterface dialog , int which ) {
	    		// Set flag
	    		displayPasscode = true;
	    		// Initialize the tertiary view
	    		initializeTertiaryView ( false );
	    		// Retrieve a reference to the tertiary view
	    		View tertiaryView = findViewById ( R.id.layout_passcode );
	    		// Display the tertiary view
	    		tertiaryView.setVisibility ( View.VISIBLE );
	    		// Animate the tertiary view
	    		tertiaryView.startAnimation ( getViewAnimationIn() );
	    		// Refresh the action bar
	    		invalidateOptionsMenu ();
			}
		} );
		// Create and show the alert dialog
		alertDialogBuilder.create ().show ();
	}
	/**
	 * Displays a warning regarding unpaid invoices for cash client.<br>
	 * The user has the option to use a pass code.
	 */
	private void displayCreditClientWarningNew () {
		// Declare and initialize an alert dialog builder
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder ( this );
		// By default, the alert dialog cannot be cancelled UNLESS it has no buttons
		alertDialogBuilder.setCancelable ( false );
		// Set the title
		alertDialogBuilder.setTitle ( R.string.warning_label );
		// Set the description
		alertDialogBuilder.setMessage ( R.string.cannot_end_visit_unpaid_invoice_message112 );
		// Map the positive and negative buttons
		alertDialogBuilder.setPositiveButton ( R.string.ok_label , null );
//		alertDialogBuilder.setNegativeButton ( R.string.use_passcode , new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick ( DialogInterface dialog , int which ) {
//	    		// Set flag
//	    		displayPasscode = true;
//	    		// Initialize the tertiary view
//	    		initializeTertiaryView ( false );
//	    		// Retrieve a reference to the tertiary view
//	    		View tertiaryView = findViewById ( R.id.layout_passcode );
//	    		// Display the tertiary view
//	    		tertiaryView.setVisibility ( View.VISIBLE );
//	    		// Animate the tertiary view
//	    		tertiaryView.startAnimation ( getViewAnimationIn() );
//	    		// Refresh the action bar
//	    		invalidateOptionsMenu ();
//			}
//		} );
		// Create and show the alert dialog
		alertDialogBuilder.create ().show ();
	}
	/**
	 * Displays a warning regarding unpaid invoices for cash client.<br>
	 * The user has the option to use a pass code.
	 */
	private void displayCreditClientWarningCash () {
		// Declare and initialize an alert dialog builder
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder ( this );
		// By default, the alert dialog cannot be cancelled UNLESS it has no buttons
		alertDialogBuilder.setCancelable ( false );
		// Set the title
		alertDialogBuilder.setTitle ( R.string.warning_label );
		// Set the description
		alertDialogBuilder.setMessage ( R.string.cannot_end_visit_unpaid_invoice_message2 );
		// Map the positive and negative buttons
		alertDialogBuilder.setPositiveButton ( R.string.ok_label , null );
		alertDialogBuilder.setNegativeButton ( R.string.use_passcode , new DialogInterface.OnClickListener() {
			@Override
			public void onClick ( DialogInterface dialog , int which ) {
	    		// Set flag
	    		displayPasscode = true;
	    		// Initialize the tertiary view
	    		initializeTertiaryView ( false );
	    		// Retrieve a reference to the tertiary view
	    		View tertiaryView = findViewById ( R.id.layout_passcode );
	    		// Display the tertiary view
	    		tertiaryView.setVisibility ( View.VISIBLE );
	    		// Animate the tertiary view
	    		tertiaryView.startAnimation ( getViewAnimationIn() );
	    		// Refresh the action bar
	    		invalidateOptionsMenu ();
			}
		} );
		// Create and show the alert dialog
		alertDialogBuilder.create ().show ();
	}
	/**
	 * Displays a warning regarding unpaid invoices for cash client.<br>
	 * The user has the option to use a pass code.
	 */
	private void displayCreditClientWarningCashNew () {
		// Declare and initialize an alert dialog builder
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder ( this );
		// By default, the alert dialog cannot be cancelled UNLESS it has no buttons
		alertDialogBuilder.setCancelable ( false );
		// Set the title
		alertDialogBuilder.setTitle ( R.string.warning_label );
		// Set the description
		alertDialogBuilder.setMessage ( R.string.cannot_end_visit_unpaid_invoice_message1112 );
		// Map the positive and negative buttons
		alertDialogBuilder.setPositiveButton ( R.string.ok_label , null );
//		alertDialogBuilder.setNegativeButton ( R.string.use_passcode , new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick ( DialogInterface dialog , int which ) {
//	    		// Set flag
//	    		displayPasscode = true;
//	    		// Initialize the tertiary view
//	    		initializeTertiaryView ( false );
//	    		// Retrieve a reference to the tertiary view
//	    		View tertiaryView = findViewById ( R.id.layout_passcode );
//	    		// Display the tertiary view
//	    		tertiaryView.setVisibility ( View.VISIBLE );
//	    		// Animate the tertiary view
//	    		tertiaryView.startAnimation ( getViewAnimationIn() );
//	    		// Refresh the action bar
//	    		invalidateOptionsMenu ();
//			}
//		} );
		// Create and show the alert dialog
		alertDialogBuilder.create ().show ();
	}
	  private boolean exceedsCreditAmmount ( final String clientCode , final String companyCode , final String divisionCode     ) {
	    	// Check if the current client has any credit limits
	    	// Retrieve the total client credit limit amount and balance
	    	Cursor cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().rawQuery ( 
	    			"SELECT COALESCE ( SUM ( " + ClientCreditingsDao.Properties.CreditAmount.columnName + " ) , 0 ) " + ClientCreditingsDao.Properties.CreditAmount.columnName + " " +
	    			"FROM " + ClientCreditingsDao.TABLENAME + " " +
	    			"WHERE " + ClientCreditingsDao.Properties.ClientCode.columnName + " = ? " +
	    			"AND " + ClientCreditingsDao.Properties.CompanyCode.columnName + " = ? " +
	    		 	"AND " + ClientCreditingsDao.Properties.DivisionCode.columnName + " = ? ",
	    			new String [] {
	    					clientCode ,
	    					companyCode ,
	     					divisionCode
	    			} );
	    	// Move the cursor to the first raw
	    	cursor.moveToFirst ();
	    	// Retrieve the total credit and balance amounts
	    	double totalCredit = cursor.getDouble ( cursor.getColumnIndex ( ClientCreditingsDao.Properties.CreditAmount.columnName ) );
	    	// Clear cursor
	    	cursor.close ();
	    	cursor = null;
	    	if( totalCredit == 0)
	    	return true;
	    	
	    	return false;
	    	}
	
	/**
	 * Builds and returns a slide in from top animation.<br>
	 * The slide animation duration is set by {@code R.integer.default_activity_transition_duration}.
	 * 
	 * @return	An {@link android.view.animation.Animation Animation} used to perform a slide in from top animation.
	 */
	private Animation getViewAnimationIn () {
		// Declare and initialize the slide in animation
		Animation in = new TranslateAnimation ( Animation.ABSOLUTE , 0 , Animation.ABSOLUTE , 0 , Animation.RELATIVE_TO_SELF , -1 , Animation.RELATIVE_TO_SELF , 0 );
		// Set the animation duration
		in.setDuration ( getResources ().getInteger ( R.integer.default_activity_transition_duration ) );
		// Return the animation
		return in;
	}
	
	/**
	 * Builds and returns a slide out to bottom animation.<br>
	 * The slide animation duration is set by {@code R.integer.default_activity_transition_duration}.
	 * 
	 * @param	Referene to the view to hide after the animation ends.
	 * @return	An {@link android.view.animation.Animation Animation} used to perform a slide out to bottom animation.
	 */
	private Animation getViewAnimationOut ( final View view ) {
		// Declare and initialize the slide in animation
		Animation out = new TranslateAnimation ( Animation.ABSOLUTE , 0 , Animation.ABSOLUTE , 0 , Animation.RELATIVE_TO_SELF , 0 , Animation.RELATIVE_TO_SELF , -1 );
		// Set the animation duration
		out.setDuration ( getResources ().getInteger ( R.integer.default_activity_transition_duration ) );
		// Set an animation listener mainly used to remove the view after it is slid out
		out.setAnimationListener ( new AnimationListener () {
			/*
			 * Notifies the start of the animation.
			 *
			 * @see android.view.animation.Animation.AnimationListener#onAnimationStart(android.view.animation.Animation)
			 */
			@Override
			public void onAnimationStart ( Animation animation ) {
				// Do nothing
			}
			
			/*
			 * Notifies the repetition of the animation.
			 *
			 * @see android.view.animation.Animation.AnimationListener#onAnimationRepeat(android.view.animation.Animation)
			 */
			@Override
			public void onAnimationRepeat ( Animation animation ) {
				// Do nothing
			}
			
			@Override
			public void onAnimationEnd ( Animation animation ) {
				// Hide the view
				view.setVisibility ( View.GONE );
			}
		} );
		// Return the animation
		return out;
	}
	
	/*
	 * Determines if the item clicks are enabled.
	 *
	 * @see me.SyncWise.Android.Modules.MenuList.MenuListActivity#isItemClickEnabled()
	 */
	@Override
	public boolean isItemClickEnabled () {
		return ! displayPasscode;
	}
	
	/**
	 * Initializes the passcode (tertiary) view.
	 * 
	 * @param restore	Boolean used to determine if the tertiary view is being initialized for the first time or is being restored.
	 */
	protected void initializeTertiaryView ( final boolean restore ) {
		// Retrieve a reference to the parent view
		View parent = findViewById ( R.id.layout_passcode );
		// Retrieve a reference to the passcode edit text
		EditText passcodeEditText = (EditText) parent.findViewById ( R.id.edittext_passcode );		
		// Retrieve a reference to the passcode title label
		TextView titleLabel = (TextView) parent.findViewById ( R.id.label_passcode );
		// Retrieve a reference to the passcode message label
		TextView messageLabel = (TextView) parent.findViewById ( R.id.label_passcode_message );
		
		// Display the title
		titleLabel.setText ( AppResources.getInstance ( this ).getString ( this , R.string.passcode_label ) );
		String	message = "Client Code: "+((Call) getIntent ().getSerializableExtra ( CALL ) ).getClient ().getClientCode()+"\n"+ AppResources.getInstance ( this ).getString ( this , R.string.movement_passcode_required_message ); 
		
		// Display the message
		messageLabel.setText ( message );
		// Check if this the first time creation
		if ( ! restore )
			// Clear any previous entries
			passcodeEditText.setText ( "" );
		// Enable the save icon
		parent.findViewById ( R.id.icon_save_passcode ).setEnabled ( true );
		// Enable the edit text
		passcodeEditText.setEnabled ( true );
		// Display the field hint
		passcodeEditText.setHint ( AppResources.getInstance ( this ).getString ( this , R.string.movement_passcode_hint ) );
	}
	
	/**
	 * Called when a view has been clicked.<br>
	 * The passcode is saved.
	 * 
	 * @param view	The clicked view.
	 */
	public void updatePasscode ( View view ) {
		// Determine if the passcode is undergoing any modifications
		if ( ! displayPasscode )
			// No modifications
			return;
		
		// Retrieve a reference to the tertiary view
		View tertiary = findViewById ( R.id.layout_passcode );
		// Retrieve a reference to the passcode edit text
		EditText passcodeEditText = (EditText) tertiary.findViewById ( R.id.edittext_passcode );
		// Store the passcode
		String passcode = passcodeEditText.getText ().toString ().trim ();
		
		// Validate pass code
		if ( ! UserPasswordsUtils.validateTimePasswordClients ( this , UserPasswordsUtils.TransactionHeaderType.TRANSACTION , TransactionHeadersUtils.Type.SALES_INVOICE , passcode , call.getClient ().getClientCode ()) ) {
			// Indicate that the passcode is not valid
			Baguette.showText ( this ,
					AppResources.getInstance ( this ).getString ( this , R.string.time_passcode_invalid_message ) ,
					Baguette.BackgroundColor.RED );
			// Exit method
			return;
		} // End if
		
		// Reset flag
		displayPasscode = false;
		
		// Reset field
		passcodeEditText.setText ( "" );
		// Disable the save icon
		tertiary.findViewById ( R.id.icon_save_passcode ).setEnabled ( false );
		// Hide the software keyboard
        ( (InputMethodManager) getBaseContext ().getSystemService ( INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow ( tertiary.getWindowToken (), 0 );
        
		// Hide the tertiary view
        tertiary.startAnimation ( getViewAnimationOut ( tertiary ) );
		// Indicate that the save was successful
		Vibration.vibrate ( this );
		
		// Retrieve the unpaid invoices
		List < TransactionHeaders > invoices = DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionHeadersDao ().queryBuilder ()
		.where ( TransactionHeadersDao.Properties.VisitID.eq ( visit.getVisitID () ) ,
				TransactionHeadersDao.Properties.RemainingAmount.gt ( 0 ) ,
				TransactionHeadersDao.Properties.TransactionType.eq ( TransactionHeadersUtils.Type.SALES_INVOICE ) ,
				TransactionHeadersDao.Properties.IsProcessed.eq ( IsProcessedUtils.isNotSync () ) ,
				TransactionHeadersDao.Properties.TransactionStatus.eq ( StatusUtils.isAvailable () ) ).list ();
		// Iterate over the list
		for ( TransactionHeaders invoice : invoices )
			// Update the passcode
			invoice.setTransactionPasswordCode ( passcode );
		// Update the invoices
		DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionHeadersDao ().updateInTx ( invoices );
		// Refresh the visit object
		DatabaseUtils.getInstance ( this ).getDaoSession ().getVisitsDao ().refresh ( visit );
		// End the visit
		visit.setEndDate ( Calendar.getInstance ().getTime () );
		// Update the visit in DB
		DatabaseUtils.getInstance ( this ).getDaoSession ().getVisitsDao ().update ( visit );
		
		// Clear call action data
		CallAction.clearData ( this );
    	// Call this to set the result that your activity will return to its caller
    	setResult ( RESULT_OK , new Intent ().putExtra ( VISIT , visit ).putExtra ( CALL , call ) );
    	// Exit activity
    	finish ();
	}
	
}