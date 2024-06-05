/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Movements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import me.SyncWise.Android.ActivityTransition;
import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.AppToast;
import me.SyncWise.Android.DateTime;
import me.SyncWise.Android.ListViewPosition;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.MovementHeaders;
import me.SyncWise.Android.Database.MovementHeadersDao;
import me.SyncWise.Android.Database.MovementHeadersUtils;
import me.SyncWise.Android.Database.StatusUtils;
import me.SyncWise.Android.Gson.BaseTimerListActivity;
import me.SyncWise.Android.Modules.Call.CallMenuActivity;
import me.SyncWise.Android.Modules.Movements.MovementAdapter.ViewHolder;
import me.SyncWise.Android.SectionsList.MultipleListAdapter;
import me.SyncWise.Android.SectionsList.Section;
import me.SyncWise.Android.Widgets.Baguette;
import me.SyncWise.Android.Widgets.QuickAction.ActionItem;
import me.SyncWise.Android.Widgets.QuickAction.QuickAction;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Activity implemented to view all the performed movements.
 * 
 * @author Elias
 *
 */
public class MovementActivity extends BaseTimerListActivity implements QuickAction.OnActionItemClickListener {

	/**
	 * Reference to a {@link me.SyncWise.Android.Widgets.QuickAction.QuickAction QuickAction} used in this activity.<br>
	 * This widget is used for editable movements.
	 */
	protected QuickAction quickAction_Editable;
	
	/**
	 * Reference to a {@link me.SyncWise.Android.Widgets.QuickAction.QuickAction QuickAction} used in this activity.<br>
	 * This widget is used for final movements.
	 */
	protected QuickAction quickAction_Final;
	
	/**
	 * Helper Class used to manage the fragment's action items IDs for the quick action widgets.<br>
	 * The main purpose of this class is to maintain a unique identifier for action items within the activity.
	 * 
	 * @author Elias
	 *
	 */
	protected class ActionItemID {
		public static final int VIEW = 0;
		public static final int EDIT = 1;
		public static final int DELETE = 2;
	}
	
	/**
	 * Bundle key used to put/retrieve the movement settings.
	 */
	public static final String MOVEMENT_SETTINGS = MovementActivity.class.getName () + ".MOVEMENT_SETTINGS";
	
	/**
	 * Bundle key used to put/retrieve the content of the is view flag.<br>
	 * This flag is used to determine if the user can edit / create movements or only view the previous movements.
	 */
	public static final String IS_VIEW = MovementActivity.class.getName () + ".IS_VIEW";
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #listViewPosition}.
	 */
	private static final String LIST_VIEW_POSITION = MovementActivity.class.getName () + ".LIST_VIEW_POSITION";
	
	/**
	 * Object used to save/restore the list views' position.
	 */
	protected ListViewPosition listViewPosition;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #saveSuccess}.
	 */
	public static final String SAVE_SUCCESS = MovementActivity.class.getName () + ".SAVE_SUCCESS";
	
	/**
	 * Flag used to indicate if a movement save was successful.
	 */
	private boolean saveSuccess;
	
	/**
	 * Bundle key used to put/retrieve the content of the successful delete flag.
	 */
	public static final String DELETE_SUCCESS = MovementActivity.class.getName () + ".DELETE_SUCCESS";
	
	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate ( Bundle savedInstanceState ) {
		// Superclass method call 
		super.onCreate ( savedInstanceState );
		// Set the activity content from a layout resource.
		setContentView ( R.layout.movement_activity );
		// Define the empty list view
		getListView ().setEmptyView ( findViewById ( R.id.empty_list_view ) );
		// Display empty list label
		( (TextView) findViewById ( R.id.empty_list_view ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_movement_list_label ) );

        // Retrieve the movement settings
        MovementSettings movementSettings = (MovementSettings) getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS );
		// Check if the movement type is valid
		if ( movementSettings == null ) {
			// Indicate that the activity cannot be displayed
			new AppToast ( this ).setIcon ( R.drawable.warning ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.invalid_selection_label ) ).show ();
			// Finish activity
			finish ();
			// Exit method
			return;
		} // End if
		// Change the title associated with this activity
		switch ( movementSettings.getMovementType () ) {
		case MovementHeadersUtils.Type.LOAD_REQUEST:
			setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.movement_type_load_request_label ) );
			break;
		case MovementHeadersUtils.Type.Physical_Direct_Load:
			setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.movement_type_physical_direct_load_label ) );
			 break;
		case MovementHeadersUtils.Type.PHYSICAL_DIRECT_UNLOAD:
			setTitle( AppResources.getInstance ( this ).getString ( this , R.string.movement_type_physical_direct_Unload_label ));
		 break;
		case MovementHeadersUtils.Type.LOAD:
			setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.movement_type_load_label ) );
			break;
		case MovementHeadersUtils.Type.UNLOAD:
			setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.movement_type_unload_label ) );
			break;
		case MovementHeadersUtils.Type.UNLOAD_REQUEST:
			setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.movement_type_unload_request_label ) );
			break;
		case MovementHeadersUtils.Type.STOCK_RECONCILIATION:
			setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.movement_type_stock_reconciliation_label ) );
			break;
		case MovementHeadersUtils.Type.STOCK_MANAGEMENT:
			setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.movement_type_stock_management_label ) );
			break;
		case MovementHeadersUtils.Type.VAN_STOCK_COUNT:
			setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.movement_type_van_stock_count_label ) );
			break;
		case MovementHeadersUtils.Type.VAN_TRANSFER:
			setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.movement_type_van_transfer_label ) );
			break;
		default:
			setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.movement_activity_title ) );
			break;
		} // End switch
        // Perform the quick action setup
        setupQuickAction ();
		
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state
			saveSuccess = savedInstanceState.getBoolean ( SAVE_SUCCESS , saveSuccess );
			listViewPosition = (ListViewPosition) savedInstanceState.getSerializable ( LIST_VIEW_POSITION );
		} // End if
		
		// Determine if the list view has a valid adapter
		if ( getListAdapter () == null )
			// Populate the list by setting a new adapter
			populate ();
	}
	
	/*
	 * Performs all necessary setup in order to properly display the quick action widget.
	 *
	 * @see me.SyncWise.Android.Modules.Journey.JourneyFragment#setupQuickAction()
	 */
    protected void setupQuickAction () {
		// Initialize the quick action widgets
		quickAction_Final = new QuickAction ( this , QuickAction.VERTICAL );
		quickAction_Editable = new QuickAction ( this , QuickAction.VERTICAL );
		// Action Item : View
		ActionItem view = new ActionItem ( ActionItemID.VIEW ,
				AppResources.getInstance ( this ).getString ( this , R.string.view_label ) ,
				getResources ().getDrawable ( R.drawable.quick_action_search ) );
		// Action Item : Edit
		ActionItem edit = null;
		// Determine if the movments are editable
		if ( ( (MovementSettings) getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS ) ).isEditable () )
			// Action Item : Edit
			edit = new ActionItem ( ActionItemID.EDIT ,
					AppResources.getInstance ( this ).getString ( this , R.string.edit_label ) ,
					getResources ().getDrawable ( R.drawable.quick_action_edit ) );
		// Action Item : Delete
		ActionItem delete = null;
		// Determine if the movments are editable
		if ( ( (MovementSettings) getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS ) ).isDeletable () )
			// Action Item : Delete
			delete = new ActionItem ( ActionItemID.DELETE ,
				AppResources.getInstance ( this ).getString ( this , R.string.delete_label ) ,
				getResources ().getDrawable ( R.drawable.quick_action_cancel ) );
    	// Populate the quick action widget with quick action items
		quickAction_Final.addActionItem ( view );
		quickAction_Editable.addActionItem ( view );
		if ( edit != null )
			quickAction_Editable.addActionItem ( edit );
		if ( delete != null )
			quickAction_Editable.addActionItem ( delete );
		// Assign an action item click listener
		quickAction_Final.setOnActionItemClickListener ( this );
		quickAction_Editable.setOnActionItemClickListener ( this );
    }
    
	/*
	 * Called as part of the activity lifecycle when an activity is going into the background, but has not (yet) been killed.
	 *
	 * @see android.app.Activity#onPause()
	 */
	@Override
    protected void onPause () {
		// Superclass method call
		super.onPause ();
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
		// Determine if a successful movement save occurred
		if ( saveSuccess )
	    	// Call this to set the result that your activity will return to its caller
	    	setResult ( RESULT_OK , new Intent ().putExtra ( CallMenuActivity.SKIP_ACTION_RESULT , true ) );
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
			listViewPosition = null;
		} // End if
	}
	
    /*
     * Called to retrieve per-instance state from an activity before being killed so that the state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle).
     *
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState ( Bundle outState ) {
    	// Superclass method call
    	super.onSaveInstanceState ( outState );
    	// Save the content of searchQuery in the outState bundle
    	outState.putBoolean ( SAVE_SUCCESS , saveSuccess );
    	// Save the list view's position in the outState bundle
    	outState.putSerializable ( LIST_VIEW_POSITION , new ListViewPosition ( getListView () ) );
    }
    
    /*
     * This method will be called when an item in the list is selected.
     *
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
	@Override
    protected void onListItemClick ( ListView listView , View view , final int position , long id ) {
		// Determine if the current movement is processed or deleted (final)
		// OR if the IS_VIEW flag is set
		if ( ( (ViewHolder) view.getTag () ).isFinal || getIntent ().getBooleanExtra ( IS_VIEW , false ) )
			// The current movement is final
			// Display the appropriate quick action widget
			quickAction_Final.show ( view , null , getResources () );
		else
			// Otherwise the current movement is editable
			// Display the appropriate quick action widget
			quickAction_Editable.show ( view , null , getResources () );
	}
	
	/*
	 * Callback method to be invoked when an action item in this QuickAction has been clicked.
	 *
	 * @see me.SyncWise.Android.Widgets.QuickAction.QuickAction.OnActionItemClickListener#onItemClick(me.SyncWise.Android.Widgets.QuickAction.QuickAction, int, int)
	 */
	@Override
	public void onItemClick ( QuickAction source , View anchor , Object object , int pos , int actionId ) {
		// Determine the clicked action item
		switch ( actionId ) {
		case ActionItemID.EDIT:
	    	// Check if the user has at least one vehicle
	    	if ( DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesDao ().count () == 0 ) {
				// Display baguette message
				Baguette.showText ( this , AppResources.getInstance ( this ).getString ( this , R.string.movement_no_vehicle_message ) , Baguette.BackgroundColor.RED );
				// Exit method
				return;
	    	} // End if
			// Start the new activity
			startActivityForResult ( getIntent_MovementDetailsActivity_Previous ( ( (ViewHolder) anchor.getTag () ).movementCode , false ) ,
					MovementDetailsActivity.REQUEST_CODE_INFO );
			// Specify an explicit transition animation to perform next
			ActivityTransition.SlideOutLeft ( this );
			break;
		case ActionItemID.VIEW:
			// Start the new activity
			startActivityForResult ( getIntent_MovementDetailsActivity_Previous ( ( (ViewHolder) anchor.getTag () ).movementCode , true ) ,
					MovementDetailsActivity.REQUEST_CODE_INFO );
			// Specify an explicit transition animation to perform next
			ActivityTransition.SlideOutLeft ( this );
			break;
		case ActionItemID.DELETE:
			// Retrieve the movement code
			final String movementCode = ( (ViewHolder) anchor.getTag () ).movementCode;
    		// Display delete confirmation
			AppDialog.getInstance ().displayAlert ( this ,
					null ,
					AppResources.getInstance ( this ).getString ( this , R.string.delete_confirmation_message ) ,
					AppDialog.ButtonsType.YesNo ,
					new DialogInterface.OnClickListener () {
						@Override
						public void onClick ( DialogInterface dialog , int which ) {
							// Determine the clicked button
							switch ( which ) {
							case DialogInterface.BUTTON_POSITIVE:
								try {
									// Retrieve the current movement
									MovementHeaders movementHeader = DatabaseUtils.getInstance ( MovementActivity.this ).getDaoSession ().getMovementHeadersDao ().queryBuilder ()
											.where ( MovementHeadersDao.Properties.MovementCode.eq ( movementCode ) ).unique ();
									// Refresh the movement object
									DatabaseUtils.getInstance ( MovementActivity.this ).getDaoSession ().getMovementHeadersDao ().refresh ( movementHeader );
									// Check if the movement is not processed
									if ( movementHeader.getIsProcessed () == IsProcessedUtils.isSync () ) {
										// Display baguette message
										Baguette.showText ( MovementActivity.this , AppResources.getInstance ( MovementActivity.this ).getString ( MovementActivity.this , R.string.movement_already_processed_message ) , Baguette.BackgroundColor.RED );
										break;
									} // End if
									// Compute the SQL string
									String SQL = "UPDATE " + MovementHeadersDao.TABLENAME + " " +
											"SET " + MovementHeadersDao.Properties.MovementStatus.columnName + " = ? , " +
													MovementHeadersDao.Properties.IsProcessed.columnName + " = ? " +
											"WHERE " + MovementHeadersDao.Properties.MovementCode.columnName + " = ? ";
									// Compute the selection arguments
									String selectionArguments [] = new String [] {
											String.valueOf ( StatusUtils.isDeleted () ) , String.valueOf ( IsProcessedUtils.isNotSync () ) , movementCode
									};
									// Execute the movement update query
									DatabaseUtils.getInstance ( MovementActivity.this ).getDaoSession ().getDatabase ().execSQL ( SQL , selectionArguments );
									// Display baguette message
									Baguette.showText ( MovementActivity.this , AppResources.getInstance ( MovementActivity.this ).getString ( MovementActivity.this , R.string.movement_delete_success_message ) , Baguette.BackgroundColor.GREEN );
									// Clear list adapter
									setListAdapter ( null );
									// Populate the list by setting a new adapter
									populate ();
								} catch ( Exception exception ) {
									// Display baguette message
									Baguette.showText ( MovementActivity.this , AppResources.getInstance ( MovementActivity.this ).getString ( MovementActivity.this , R.string.movement_delete_failure_message ) , Baguette.BackgroundColor.RED );
								} // End of try-catch block
								break;
							case DialogInterface.BUTTON_NEGATIVE:
								// Dismiss dialog
								AppDialog.getInstance ().dismiss ();
								break;
							} // End switch
						}
					} );
			break;
		} // End of switch
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
    	// Check the IS_VIEW flag
    	if ( ! getIntent ().getBooleanExtra ( IS_VIEW , false ) ) {
	    	// Enable the required menu items
	    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_add ) );
    	} // End if
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
    	// Determine if the action add is selected
    	if ( menuItem.getItemId () == R.id.action_add ) {
    		// Start a new movement
    		startNewMovement ();
    		// Consume event
    		return true;
    	} // End if
    	// Allow normal menu processing to proceed
    	return false;
    }
    
    /**
     * Starts the {@link me.SyncWise.Android.Modules.Movements.MovementDetailsActivity MovementDetailsActivity} activity in order to create a new movement.
     */
    protected void startNewMovement () {
    	// Check if the user has at least one vehicle
    	if ( DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesDao ().count () == 0 ) {
			// Display baguette message
			Baguette.showText ( this , AppResources.getInstance ( this ).getString ( this , R.string.movement_no_vehicle_message ) , Baguette.BackgroundColor.RED );
			// Exit method
			return;
    	} // End if
		// Start the new activity
		startActivityForResult ( getIntent_MovementDetailsActivity_New () , MovementDetailsActivity.REQUEST_CODE_NEW );
		// Specify an explicit transition animation to perform next
		ActivityTransition.SlideOutLeft ( this );
    }
    
    /**
     * Gets an intent for the movement details activity for a new movement.<br>
     * The default implementation uses {@link me.SyncWise.Android.Modules.Movements.MovementDetailsActivity MovementDetailsActivity}.<br>
     * In order to used a custom adapter, override this method.<br>
	 * <b>Note : </b> This method should not return {@code NULL}.
     * 
     * @return	Intent used to start the movement details activity.
     */
    protected Intent getIntent_MovementDetailsActivity_New () {
		// Create a new intent to start a new activity
		Intent intent = new Intent ( this , MovementDetailsActivity.class );
		// Add the movement settings to the intent
		intent.putExtra ( MovementDetailsActivity.MOVEMENT_SETTINGS , getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS ) );
		// Add the request code to the intent
		intent.putExtra ( MovementDetailsActivity.REQUEST_CODE , MovementDetailsActivity.REQUEST_CODE_NEW );
		// Return the computed intent
		return intent;
    }
    
    /**
     * Gets an intent for the movement details activity for a previous (edit or view) movement.<br>
     * The default implementation uses {@link me.SyncWise.Android.Modules.Movements.MovementDetailsActivity MovementDetailsActivity}.<br>
     * In order to used a custom adapter, override this method.<br>
	 * <b>Note : </b> This method should not return {@code NULL}.
     * 
     * @param movementHeaderCode	String hosting the movement header code.
     * @param isView	Boolean used to indicate if the previous movement will be viewed or edited.
     * @return	Intent used to start the movement details activity.
     */
    protected Intent getIntent_MovementDetailsActivity_Previous ( final String movementHeaderCode , final boolean isView ) {
		// Create a new intent to start a new activity
		Intent intent = new Intent ( this , MovementDetailsActivity.class );
		// Add the movement settings to the intent
		intent.putExtra ( MovementDetailsActivity.MOVEMENT_SETTINGS , getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS ) );
		// Add the request code to the intent
		intent.putExtra ( MovementDetailsActivity.REQUEST_CODE , MovementDetailsActivity.REQUEST_CODE_INFO );
		// Add the movement header code to the intent
		intent.putExtra ( MovementDetailsActivity.MOVEMENT_HEADER_CODE , movementHeaderCode );
		// Check if the is view flag is set
		if ( isView )
			// Add the view only flag to the intent
			intent.putExtra ( MovementDetailsActivity.IS_VIEW_ONLY , true );
		else
			// Add the edit flag to the intent
			intent.putExtra ( MovementDetailsActivity.IS_EDIT , true );
		// Return the computed intent
		return intent;
    }
    
	/*
	 * Called when an activity you launched exits, giving you the requestCode you started it with, the resultCode it returned, and any additional data from it.
	 *
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	protected void onActivityResult ( int requestCode , int resultCode , final Intent data ) {
    	// Check if the result is successful and the intent is valid
    	if ( resultCode != RESULT_OK || data == null )
    		// Exit method
    		return;

    	// Check if the movement has been successfully deleted
    	if ( data.getBooleanExtra ( DELETE_SUCCESS , false ) ) {
			// Display baguette message
			Baguette.showText ( this , AppResources.getInstance ( this ).getString ( this , R.string.movement_delete_success_message ) , Baguette.BackgroundColor.GREEN );
			// Clear list adapter
			setListAdapter ( null );
			// Populate the list by setting a new adapter
			populate ();
			// Exit method
			return;
    	} // End if
    	
    	// Determine the provided request code
    	switch ( requestCode ) {
		case MovementDetailsActivity.REQUEST_CODE_NEW:
			// Set flag
			saveSuccess = saveSuccess || data.getBooleanExtra ( SAVE_SUCCESS , false );
		case MovementDetailsActivity.REQUEST_CODE_INFO:
			// Display baguette message
			Baguette.showText ( this ,
					AppResources.getInstance ( this ).getString ( this ,
							data.getBooleanExtra ( SAVE_SUCCESS , false ) ? R.string.movement_save_success_message : R.string.movement_save_failure_message ) ,
							data.getBooleanExtra ( SAVE_SUCCESS , false ) ? Baguette.BackgroundColor.GREEN : Baguette.BackgroundColor.RED );
			// Clear list adapter
			setListAdapter ( null );
			
			// Populate the list by setting a new adapter
			populate ();

			break;
		} // End switch
    	
	}
	
	/**
	 * Retrieve all the movements asynchronously.
	 */
	protected void populate () {
		// Populate the list by setting a new adapter
		new PopulateList ().execute ();
	}

	/**
	 * Populates the movement list by generating a map of cursors holding movements, that belongs for a specific day, for the specified user.
	 * 
	 * @param context	The application context.
	 * @param movementType	Integer holding the movement type.
	 * @return	Cursors holding movements data set mapped to their appropriate dates.
	 */
	public static Map < String , Cursor > populateList ( final Context context ,   int movementType ) {
		// Retrieve the user, company and division codes
		String userCode = DatabaseUtils.getCurrentUserCode ( context );
		String companyCode = DatabaseUtils.getCurrentCompanyCode ( context );
		String divisionCode = DatabaseUtils.getCurrentDivisionCode ( context );
		if(movementType == MovementHeadersUtils.Type.Physical_Direct_Load)
			movementType= MovementHeadersUtils.Type.LOAD;
		
		if(movementType == MovementHeadersUtils.Type.PHYSICAL_DIRECT_UNLOAD)
			movementType= MovementHeadersUtils.Type.UNLOAD;
		// Compute the selection clause
		String selection = MovementHeadersDao.Properties.UserCode.columnName + " = ? AND " + MovementHeadersDao.Properties.CompanyCode.columnName + " = ? AND " + MovementHeadersDao.Properties.DivisionCode.columnName + " in( ? ) AND " 
				+ MovementHeadersDao.Properties.MovementType.columnName + " = ? AND "
				+ MovementHeadersDao.Properties.MovementStatus.columnName + " = ?";
		// Compute the selection arguments
		String selectionArgs [] = new String [] {
				userCode , companyCode , divisionCode ,
				String.valueOf ( movementType ) ,
				String.valueOf ( StatusUtils.isAvailable () )
		};
		// Compute the order by clause
		String orderBy = MovementHeadersDao.Properties.MovementDate.columnName;
		// Declare and initialize a cursor in order to query DB
		Cursor cursor = DatabaseUtils.getInstance ( context ).getDaoSession ().getDatabase ()
				.query ( MovementHeadersDao.TABLENAME , new String [] { MovementHeadersDao.Properties.MovementDate.columnName } , selection , selectionArgs , null , null , orderBy );
		
		// Declare and initialize a list of calendars, used to compute the movement dates
		List < Calendar > dates = new ArrayList < Calendar > ();
		// Move the cursor to the first raw
		if ( cursor.moveToFirst () ) {
			// Iterate over all raws
			do {
				// Compute the current movement header date
				Calendar movementDate = Calendar.getInstance ();
				movementDate.setTimeInMillis ( cursor.getLong ( cursor.getColumnIndex ( MovementHeadersDao.Properties.MovementDate.columnName ) ) );
				movementDate.set ( Calendar.HOUR_OF_DAY , 0 );
				movementDate.set ( Calendar.MINUTE , 0 );
				movementDate.set ( Calendar.SECOND , 0 );
				movementDate.set ( Calendar.MILLISECOND , 0 );
				// Check if the list of dates is empty
				if ( dates.isEmpty () )
					// Add the current movement header date
					dates.add ( movementDate );
				// Otherwise the list of dates is NOT empty
				// Check if the last date is in the same day (the cursor is ordered by date)
				else if ( dates.get ( dates.size () - 1 ).get ( Calendar.DAY_OF_YEAR ) == movementDate.get ( Calendar.DAY_OF_YEAR )
						&& dates.get ( dates.size () - 1 ).get ( Calendar.YEAR ) == movementDate.get ( Calendar.YEAR ) ) {
					// They are both the same day, do nothing
				} // End else if
				// Otherwise they are not the same day
				else
					// Add the current movement header date
					dates.add ( movementDate );
			} while ( cursor.moveToNext () );
		} // End if
		// Close the cursor
		cursor.close ();
		cursor = null;
		
		// Adjust the selection clause
		selection += " AND " + MovementHeadersDao.Properties.MovementDate.columnName + " BETWEEN ? AND ?";
		// Adjust the selection arguments
		selectionArgs = new String [] {
				selectionArgs [ 0 ] ,
				selectionArgs [ 1 ] ,
				selectionArgs [ 2 ] ,
				selectionArgs [ 3 ] ,
				selectionArgs [ 4 ] ,
				null ,
				null ,
		};
		// Compute the SQL string
		String sql = "SELECT M."
				+ MovementHeadersDao.Properties.Id.columnName + ","
				+ MovementHeadersDao.Properties.MovementCode.columnName + ","
				+ MovementHeadersDao.Properties.MovementDate.columnName + ","
				+ MovementHeadersDao.Properties.IsProcessed.columnName + ","
				+ MovementHeadersDao.Properties.MovementStatus.columnName + ","
				+ MovementHeadersDao.Properties.TransferType.columnName + " "
				+ " FROM " + MovementHeadersDao.TABLENAME + " M "
				+ " WHERE " + selection
				+ " ORDER BY " + orderBy;
		
		// Declare and initialize a map of cursors mapped to strings (their date representation)
		Map < String , Cursor > cursors = new LinkedHashMap < String , Cursor > ();
		// Iterate over all dates (from the last one to the first)
		for ( int i = dates.size () - 1 ; i >= 0 ; i -- ) {
			// Compute the start date
			Calendar start = dates.get ( i );
			// Compute the end date
			Calendar end = Calendar.getInstance ();
			end.setTimeInMillis ( start.getTimeInMillis () );
			end.set ( Calendar.HOUR_OF_DAY , 23 );
			end.set ( Calendar.MINUTE , 59 );
			end.set ( Calendar.SECOND , 59 );
			end.set ( Calendar.MILLISECOND , 999 );
			// Set the start and end date in the selection arguments
			selectionArgs [ 5 ] = String.valueOf ( start.getTimeInMillis () );
			selectionArgs [ 6 ] = String.valueOf ( end.getTimeInMillis () );
			// Map a new cursor to its date
			cursors.put ( DateTime.getFullDate ( context , start ) ,
					DatabaseUtils.getInstance ( context ).getDaoSession ().getDatabase ()
						.rawQuery ( sql , selectionArgs ) );
		} // End for each
		
		// Iterate over all cursors
		for ( String date : cursors.keySet () )
			// Execute internal computations
			cursors.get ( date ).getCount ();
		
		// Return the cursors map
		return cursors;
	}
	
	/**
	 * AsyncTask helper class used to populate the movement items list.
	 * 
	 * @author Elias
	 *
	 */
	private class PopulateList extends AsyncTask < Void , Void , Map < String , Cursor > > {
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Map < String , Cursor > doInBackground ( Void ... params ) {
	        // Retrieve the movement settings
	        MovementSettings movementSettings = (MovementSettings) getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS );
			// Populate the list using the current client
			return populateList ( MovementActivity.this , movementSettings.getMovementType () );
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( Map < String , Cursor > cursors ) {
			// Compute the total number of movements
			int totalMovements = 0;
			// Iterate over all cursors
			for ( String date : cursors.keySet () )
				// Add the number of movements
				totalMovements += cursors.get ( date ).getCount ();
			// Set the action bar's subtitle
			if ( getActionBar () != null )
				getActionBar ().setSubtitle ( totalMovements == 0 ? "" : AppResources.getInstance ( MovementActivity.this ).getString ( MovementActivity.this , R.string.total_of_label ) + " : " + totalMovements );
			// Declare and initialize a multiple list adapter
			MultipleListAdapter adapter = new MultipleListAdapter ( MovementActivity.this );
			// Iterate over all cursors
			for ( String date : cursors.keySet () )
				// Add a new movement adapter using the current cursor
				adapter.addSection ( new Section ( date , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
						new MovementAdapter ( MovementActivity.this , cursors.get ( date ) , R.layout.movement_activity_item , (MovementSettings) getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS ) ) );
			// Set the list adapter
			setListAdapter ( adapter );
			// Restore the list view position (if any)
			if ( listViewPosition != null ) {
				listViewPosition.restore ( getListView () );
				listViewPosition = null;
			} // End if
		}
		
	}
	
}