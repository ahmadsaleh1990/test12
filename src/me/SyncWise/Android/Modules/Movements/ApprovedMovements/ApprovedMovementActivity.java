/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Movements.ApprovedMovements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;

import de.greenrobot.dao.AbstractDao;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import me.SyncWise.Android.ActivityTransition;
import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.DateTime;
import me.SyncWise.Android.Network;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.ConnectionSettings;
import me.SyncWise.Android.Database.ConnectionSettingsDao;
import me.SyncWise.Android.Database.ConnectionSettingsUtils;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.MovementDetails;
import me.SyncWise.Android.Database.MovementDetailsDao;
import me.SyncWise.Android.Database.MovementHeaders;
import me.SyncWise.Android.Database.MovementHeadersDao;
import me.SyncWise.Android.Database.MovementHeadersUtils;
import me.SyncWise.Android.Database.Users;
import me.SyncWise.Android.Modules.DataManagement.DataManagementActivity.RequestCode;
import me.SyncWise.Android.Modules.Movements.MovementActivity;
import me.SyncWise.Android.Modules.Movements.MovementDetailsActivity;
import me.SyncWise.Android.Modules.Movements.MovementSettings;
import me.SyncWise.Android.Modules.Movements.MovementAdapter.ViewHolder;
import me.SyncWise.Android.Modules.Sync.SyncHelper;
import me.SyncWise.Android.Modules.Sync.SyncListener;
import me.SyncWise.Android.SectionsList.MultipleListAdapter;
import me.SyncWise.Android.SectionsList.Section;
import me.SyncWise.Android.Widgets.Baguette;
import me.SyncWise.Android.Widgets.QuickAction.ActionItem;
import me.SyncWise.Android.Widgets.QuickAction.QuickAction;

/**
 * Activity implemented to view all the approved movements.
 * 
 * @author Elias
 *
 */
public class ApprovedMovementActivity extends MovementActivity implements SyncListener {

	/**
	 * List used to host the retrieved approved {@link me.SyncWise.Android.Database.MovementHeaders MovementHeaders}.
	 */
	private static List < MovementHeaders > approvedMovementHeaders;
	
	/**
	 * List used to host the retrieved approved {@link me.SyncWise.Android.Database.MovementDetails MovementDetails}.
	 */
	private static List < MovementDetails > approvedMovementDetails;
	
	/**
	 * Helper Class used to manage the fragment's action items IDs for the quick action widgets.<br>
	 * The main purpose of this class is to maintain a unique identifier for action items within the activity.
	 * 
	 * @author Elias
	 *
	 */
	protected class ActionItemID extends MovementActivity.ActionItemID {
		public static final int APPLY = 10;
	}
	
	/*
	 * Performs all necessary setup in order to properly display the quick action widget.
	 *
	 * @see me.SyncWise.Android.Modules.Journey.JourneyFragment#setupQuickAction()
	 */
	@Override
    protected void setupQuickAction () {
		// Initialize the quick action widgets
		quickAction_Final = new QuickAction ( this , QuickAction.VERTICAL );
		quickAction_Editable = new QuickAction ( this , QuickAction.VERTICAL );
		// Action Item : View
		ActionItem view = new ActionItem ( ActionItemID.VIEW ,
				AppResources.getInstance ( this ).getString ( this , R.string.view_label ) ,
				getResources ().getDrawable ( R.drawable.quick_action_search ) );
		// Action Item : Delete
		ActionItem delete = new ActionItem ( ActionItemID.DELETE ,
				AppResources.getInstance ( this ).getString ( this , R.string.delete_label ) ,
				getResources ().getDrawable ( R.drawable.quick_action_cancel ) );
		// Action Item : Edit
		ActionItem apply = null;
		// Retrieve the movement settings
		MovementSettings movementSettings = (MovementSettings) getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS );
		// Make sure the movement type is either load or unload
		if ( movementSettings.getMovementType () == MovementHeadersUtils.Type.LOAD || movementSettings.getMovementType () == MovementHeadersUtils.Type.UNLOAD )
			// Action Item : Apply
			apply = new ActionItem ( ActionItemID.APPLY ,
					AppResources.getInstance ( this ).getString ( this , movementSettings.getMovementType () == MovementHeadersUtils.Type.LOAD ? R.string.movement_load_label : R.string.movement_unload_label ) ,
					getResources ().getDrawable ( R.drawable.quick_action_ok ) );
    	// Populate the quick action widget with quick action items
		quickAction_Final.addActionItem ( view );
		quickAction_Editable.addActionItem ( view );
		if ( movementSettings.getMovementType () == MovementHeadersUtils.Type.LOAD || movementSettings.getMovementType () == MovementHeadersUtils.Type.UNLOAD )
			quickAction_Editable.addActionItem ( apply );
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
		// Dismiss any displayed dialogs (to avoid activity context leak)
		AppDialog.getInstance ().dismiss ();
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
			break;
		case ActionItemID.VIEW:
		case ActionItemID.DELETE:
			super.onItemClick ( source , anchor , object , pos , actionId );
			break;
		case ActionItemID.APPLY:
	    	// Check if the user has at least one vehicle
	    	if ( DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesDao ().count () == 0 ) {
				// Display baguette message
				Baguette.showText ( this , AppResources.getInstance ( this ).getString ( this , R.string.movement_no_vehicle_message ) , Baguette.BackgroundColor.RED );
				// Exit method
				return;
	    	} // End if
			// Retrieve the movement code
			final String movementCode = ( (ViewHolder) anchor.getTag () ).movementCode;
			// Create a new intent to start a new activity
			Intent intent = new Intent ( this , MovementDetailsActivity.class );
			// Add the movement settings to the intent
			intent.putExtra ( MovementDetailsActivity.MOVEMENT_SETTINGS , getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS ) );
			// Add the request code to the intent
			intent.putExtra ( MovementDetailsActivity.REQUEST_CODE , MovementDetailsActivity.REQUEST_CODE_INFO );
			// Add the edit flag to the intent
			intent.putExtra ( MovementDetailsActivity.IS_EDIT , true );
			// Add the movement header code to the intent
			intent.putExtra ( MovementDetailsActivity.MOVEMENT_HEADER_CODE , movementCode );
			// Start the new activity
			startActivityForResult ( intent , MovementDetailsActivity.REQUEST_CODE_INFO );
			// Specify an explicit transition animation to perform next
			ActivityTransition.SlideOutLeft ( this );
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
    	// Enable the required menu items
    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_refresh ) );
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
    	if ( menuItem.getItemId () == R.id.action_refresh ) {
    		// Import approved movements
    		importApprovedMovements ();
    		// Consume event
    		return true;
    	} // End if
    	// Allow normal menu processing to proceed
    	return false;
    }
    
    /**
     * Start the import process to get approved movements from the server.
     */
    private void importApprovedMovements () {
		// Determine if the network is available
		if ( Network.networkAvailability ( this ) ) {
	        // Search for the GPRS link
	        ConnectionSettings connection = DatabaseUtils.getInstance ( this ).getDaoSession ().getConnectionSettingsDao ().queryBuilder ()
	        		.where ( ConnectionSettingsDao.Properties.ConnectionSettingType.eq ( ConnectionSettingsUtils.Type.GPRS ) ).unique ();
	        // Check if the connection is valid
	        if ( connection != null ) {
	        	// Valid connection
	        	// Clear lists
	        	approvedMovementHeaders = null;
	        	approvedMovementDetails = null;
				// Retrieve the dao table data
				SyncHelper syncHelper = new SyncHelper ( ConnectionSettingsUtils.getConnectMethodPath ( connection.getConnectionSettingURL () ) );
				syncHelper.get ( this , DatabaseUtils.getInstance ( this ).getDaoSession ().getUsersDao () , new TypeToken < List < Users > > () {}.getType () , DatabaseUtils.getCurrentUserCode ( this ) , DatabaseUtils.getCurrentCompanyCode ( this ) , this , RequestCode.PING );
				// Display indeterminate progress
				AppDialog.getInstance ().displayIndeterminateProgress ( this , null , getString ( R.string.connecting_label ) );
	        } // End if
	        else
				// Display alert dialog
				AppDialog.getInstance ().displayAlert ( this ,
						null ,
						getString ( R.string.no_network_connection_message ) ,
						AppDialog.ButtonsType.OK , null );
		} // End if
    }

	/*
	 * Call back method executed before determining if the sync result is a success or a failure.
	 *
	 * @see me.SyncWise.Android.Modules.Sync.SyncListener#onPreFinish(int)
	 */
	@Override
	public void onPreFinish ( int requestCode ) {
		// Do nothing.
	}

	@Override
	public void onGetSuccess ( AbstractDao < ? , ? > dao , ArrayList < Object > entities , int requestCode ) {
        // Search for the GPRS link
        ConnectionSettings connection = DatabaseUtils.getInstance ( this ).getDaoSession ().getConnectionSettingsDao ().queryBuilder ()
        		.where ( ConnectionSettingsDao.Properties.ConnectionSettingType.eq ( ConnectionSettingsUtils.Type.GPRS ) ).unique ();
		// Retrieve the user code
		String userCode = DatabaseUtils.getCurrentUserCode ( this );
		// Retrieve the company code
		String companyCode = DatabaseUtils.getCurrentCompanyCode ( this );
		// Determine if this is a ping to the server
		if ( requestCode == RequestCode.PING ) {
			// Retrieve the dao table data
			SyncHelper syncHelper = new SyncHelper ( ConnectionSettingsUtils.getGetMethodPath ( connection.getConnectionSettingURL () ) );
			syncHelper.get ( this , DatabaseUtils.getInstance ( this ).getDaoSession ().getMovementHeadersDao () , // WarehouseQuantities table
					new TypeToken < List < MovementHeaders > > () {}.getType () , // Expected Type
					userCode , // UserCode
					companyCode , // CompanyCode
					this , // SyncListener
					RequestCode.ON_GOING , // Sync Request Code`
					true ); // Execute in parallel
		} // End if
		// Determine the sync is on going
		else if ( requestCode == RequestCode.ON_GOING ) {
			// Determine if the retrieved data are movement headers
			if ( dao.getTablename ().equals ( MovementHeadersDao.TABLENAME ) ) {
				// Store the list
				approvedMovementHeaders = new ArrayList < MovementHeaders > ();
				for ( Object entity : entities )
					approvedMovementHeaders.add ( (MovementHeaders) entity );
				// Check if there is at least one movement
				if ( ! entities.isEmpty () ) {
					// Retrieve the dao table data
					SyncHelper syncHelper = new SyncHelper ( ConnectionSettingsUtils.getGetMethodPath ( connection.getConnectionSettingURL () ) );
					syncHelper.get ( this , DatabaseUtils.getInstance ( this ).getDaoSession ().getMovementDetailsDao () , // WarehouseQuantities table
							new TypeToken < List < MovementDetails > > () {}.getType () , // Expected Type
							userCode , // UserCode
							companyCode , // CompanyCode
							this , // SyncListener
							RequestCode.ON_GOING , // Sync Request Code`
							true ); // Execute in parallel
				} // End if
				else {
					// Dismiss any displayed dialogs (to avoid activity context leak)
					AppDialog.getInstance ().dismiss ();
					approvedMovementDetails = new ArrayList < MovementDetails > ();
					// Save imported movements
					saveImportedApprovedMovements ();
				} // End else
			} // End if
			// Determine if the retrieved data are movement details
			else if ( dao.getTablename ().equals ( MovementDetailsDao.TABLENAME ) ) {
				// Store the list
				approvedMovementDetails = new ArrayList < MovementDetails > ();
				for ( Object entity : entities )
					approvedMovementDetails.add ( (MovementDetails) entity );
				// Dismiss any displayed dialogs (to avoid activity context leak)
				AppDialog.getInstance ().dismiss ();
				// Save imported movements
				saveImportedApprovedMovements ();
			} // End else if
		} // End else if
		else
			// Dismiss any displayed dialogs (to avoid activity context leak)
			AppDialog.getInstance ().dismiss ();
	}

	/*
	 * Call back method executed if the set sync process is a success.
	 *
	 * @see me.SyncWise.Android.Modules.Sync.SyncListener#onSetSuccess(int)
	 */
	@Override
	public void onSetSuccess ( ArrayList < ArrayList < Object >> entites , int requestCode ) {
		// Not implemented
	}

	/*
	 * Call back method executed if the get sync process is a failure.
	 *
	 * @see me.SyncWise.Android.Modules.Sync.SyncListener#onGetFailure(de.greenrobot.dao.AbstractDao, int)
	 */
	@Override
	public void onGetFailure ( AbstractDao < ? , ? > dao , int requestCode ) {
		// Clear lists
		approvedMovementHeaders = null;
		approvedMovementDetails = null;
		// Dismiss any displayed dialogs (to avoid activity context leak)
		AppDialog.getInstance ().dismiss ();
		// Display alert dialog
		AppDialog.getInstance ().displayAlert ( this , getString ( R.string.error_label ) , getString ( R.string.sync_failed_message ) , AppDialog.ButtonsType.OK , null );
	}

	/*
	 * Call back method executed if the set sync process is a failure.
	 *
	 * @see me.SyncWise.Android.Modules.Sync.SyncListener#onSetFailure(int)
	 */
	@Override
	public void onSetFailure ( int requestCode ) {
		// Not implemented
	}

	/*
	 * Call back method executed after the sync process executed.
	 *
	 * @see me.SyncWise.Android.Modules.Sync.SyncListener#onPostFinish(int)
	 */
	@Override
	public void onPostFinish ( int requestCode ) {
		// Do nothing.
	}
	
	/**
	 * Saves the imported approved movements.
	 */
	private void saveImportedApprovedMovements () {
		// Start by deleting non modified movements
		List < MovementHeaders > movementHeaders = DatabaseUtils.getInstance ( this ).getDaoSession ().getMovementHeadersDao ().queryBuilder ()
				.where ( MovementHeadersDao.Properties.IsProcessed.eq ( IsProcessedUtils.isWebProcessed () ) ).list ();
		ArrayList < String > movementHeaderCodes = new ArrayList < String > ();
		for ( MovementHeaders movementHeader : movementHeaders )
			movementHeaderCodes.add ( movementHeader.getMovementCode () );
		DatabaseUtils.getInstance ( this ).getDaoSession ().getMovementHeadersDao ().deleteInTx ( movementHeaders );
		List < MovementDetails > movementDetails = DatabaseUtils.getInstance ( this ).getDaoSession ().getMovementDetailsDao ().queryBuilder ()
				.where ( MovementDetailsDao.Properties.MovementCode.in ( movementHeaderCodes ) ).list ();
		DatabaseUtils.getInstance ( this ).getDaoSession ().getMovementDetailsDao ().deleteInTx ( movementDetails );
		// Retrieve the approved movement codes
		ArrayList < String > approvedMovementCodes = new ArrayList < String > ();
		for ( MovementHeaders movementHeader : approvedMovementHeaders )
			approvedMovementCodes.add ( movementHeader.getMovementCode () );
		// Check for modified movements
		movementHeaders = DatabaseUtils.getInstance ( this ).getDaoSession ().getMovementHeadersDao ().queryBuilder ()
				.where ( MovementHeadersDao.Properties.MovementCode.in ( approvedMovementCodes ) ).list ();
		ArrayList < String > modifiedMovementCodes = new ArrayList < String > ();
		for ( MovementHeaders movementHeader : movementHeaders )
			modifiedMovementCodes.add ( movementHeader.getMovementCode () );
		try {
			// Begin transaction
			DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().beginTransaction ();
			// Add the retrieved movement except for the modified ones
			for ( MovementHeaders movementHeader : approvedMovementHeaders )
				if ( ! modifiedMovementCodes.contains ( movementHeader.getMovementCode () ) )
					DatabaseUtils.getInstance ( this ).getDaoSession ().insert ( movementHeader );
			for ( MovementDetails movementDetail : approvedMovementDetails )
				if ( ! modifiedMovementCodes.contains ( movementDetail.getMovementCode () ) )
					DatabaseUtils.getInstance ( this ).getDaoSession ().insert ( movementDetail );
			// Commit changes
			DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().setTransactionSuccessful ();
			// Populate the list by setting a new adapter
			populate ();
			// Display alert dialog
			AppDialog.getInstance ().displayAlert ( this , null , getString ( R.string.sync_success_message ) , AppDialog.ButtonsType.OK , null );
		} catch ( Exception exception ) {
			// Display alert dialog
			AppDialog.getInstance ().displayAlert ( this , getString ( R.string.error_label ) , getString ( R.string.populate_data_error_message ) , AppDialog.ButtonsType.OK , null );
		} finally {
			 //End transaction
			DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().endTransaction ();
			// Clear lists
			approvedMovementHeaders = null;
			approvedMovementDetails = null;
		} // End try-catch-finally block
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
	public static Map < String , Cursor > populateList ( final Context context , final int movementType ) {
		// Retrieve the user, company and division codes
		String userCode = DatabaseUtils.getCurrentUserCode ( context );
		String companyCode = DatabaseUtils.getCurrentCompanyCode ( context );
		String divisionCode = DatabaseUtils.getCurrentDivisionCode ( context );
		// Compute the selection clause
		String selection = MovementHeadersDao.Properties.UserCode.columnName + " = ? AND " + MovementHeadersDao.Properties.CompanyCode.columnName + " = ? AND " + MovementHeadersDao.Properties.DivisionCode.columnName + " = ? AND " 
				+ MovementHeadersDao.Properties.MovementType.columnName + " = ? ";
		// Compute the selection arguments
		String selectionArgs [] = new String [] {
				userCode , companyCode , divisionCode ,
				String.valueOf ( movementType )
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
				null ,
				null ,
		};
		// Compute the SQL string
		String sql = "SELECT M."
				+ MovementHeadersDao.Properties.Id.columnName + ","
				+ MovementHeadersDao.Properties.MovementCode.columnName + ","
				+ MovementHeadersDao.Properties.MovementDate.columnName + ","
				+ MovementHeadersDao.Properties.IsProcessed.columnName + ","
				+ MovementHeadersDao.Properties.MovementStatus.columnName + " "
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
			selectionArgs [ 4 ] = String.valueOf ( start.getTimeInMillis () );
			selectionArgs [ 5 ] = String.valueOf ( end.getTimeInMillis () );
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
			return populateList ( ApprovedMovementActivity.this , movementSettings.getMovementType () );
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
				getActionBar ().setSubtitle ( totalMovements == 0 ? "" : AppResources.getInstance ( ApprovedMovementActivity.this ).getString ( ApprovedMovementActivity.this , R.string.total_of_label ) + " : " + totalMovements );
			// Declare and initialize a multiple list adapter
			MultipleListAdapter adapter = new MultipleListAdapter ( ApprovedMovementActivity.this );
			// Iterate over all cursors
			for ( String date : cursors.keySet () )
				// Add a new movement adapter using the current cursor
				adapter.addSection ( new Section ( date , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
						new ApprovedMovementAdapter ( ApprovedMovementActivity.this , cursors.get ( date ) , R.layout.movement_activity_item , (MovementSettings) getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS ) ) );
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