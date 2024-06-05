/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Return;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;

import de.greenrobot.dao.AbstractDao;

import me.SyncWise.Android.ActivityTransition;
import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.AppToast;
import me.SyncWise.Android.DateTime;
import me.SyncWise.Android.ListViewPosition;
import me.SyncWise.Android.Network;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.ConnectionSettings;
import me.SyncWise.Android.Database.ConnectionSettingsDao;
import me.SyncWise.Android.Database.ConnectionSettingsUtils;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.StatusUtils;
import me.SyncWise.Android.Database.TransactionDetails;
import me.SyncWise.Android.Database.TransactionDetailsDao;
import me.SyncWise.Android.Database.TransactionHeaders;
import me.SyncWise.Android.Database.TransactionHeadersDao;
import me.SyncWise.Android.Database.TransactionHeadersUtils;
import me.SyncWise.Android.Database.Users;
import me.SyncWise.Android.Gson.BaseTimerListActivity;
import me.SyncWise.Android.Modules.Call.CallMenuActivity;
import me.SyncWise.Android.Modules.DataManagement.DataManagementActivity.RequestCode;
import me.SyncWise.Android.Modules.Journey.Call;
import me.SyncWise.Android.Modules.Return.ReturnAdapter.ViewHolder;
import me.SyncWise.Android.Modules.Sync.SyncHelper;
import me.SyncWise.Android.Modules.Sync.SyncListener;
import me.SyncWise.Android.SectionsList.MultipleListAdapter;
import me.SyncWise.Android.SectionsList.Section;
import me.SyncWise.Android.Widgets.Baguette;
import me.SyncWise.Android.Widgets.QuickAction.ActionItem;
import me.SyncWise.Android.Widgets.QuickAction.QuickAction;
import android.app.ListActivity;
import android.content.Context;
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
 * Activity implemented to view all the performed returns.
 * 
 * @author Elias
 *
 */
public class ReturnActivity extends BaseTimerListActivity implements QuickAction.OnActionItemClickListener , SyncListener {

	/**
	 * List used to host the retrieved approved {@link me.SyncWise.Android.Database.TransactionHeaders TransactionHeaders}.
	 */
	private static List < TransactionHeaders > approvedTransactionHeaders;
	
	/**
	 * List used to host the retrieved approved {@link me.SyncWise.Android.Database.TransactionDetails TransactionDetails}.
	 */
	private static List < TransactionDetails > approvedTransactionDetails;
	
	/**
	 * Reference to a {@link me.SyncWise.Android.Widgets.QuickAction.QuickAction QuickAction} used in this activity.<br>
	 * This widget is used for editable returns.
	 */
	protected QuickAction quickAction_Editable;
	
	/**
	 * Reference to a {@link me.SyncWise.Android.Widgets.QuickAction.QuickAction QuickAction} used in this activity.<br>
	 * This widget is used for final returns.
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
		public static final int APPLY = 10;
	}
	
	/**
	 * Bundle key used to put/retrieve the content of the current call.
	 */
	public static final String CALL = ReturnActivity.class.getName () + ".CALL";
	
	/**
	 * Bundle key used to put/retrieve the content of the visit object.
	 */
	public static final String VISIT = ReturnActivity.class.getName () + ".VISIT";
	
	/**
	 * Bundle key used to put/retrieve the content of the is view flag.<br>
	 * This flag is used to determine if the user can edit / create or only view the previous returns.
	 */
	public static final String IS_VIEW = ReturnActivity.class.getName () + ".IS_VIEW";
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #listViewPosition}.
	 */
	private static final String LIST_VIEW_POSITION = ReturnActivity.class.getName () + ".LIST_VIEW_POSITION";
	
	/**
	 * Object used to save/restore the list views' position.
	 */
	protected ListViewPosition listViewPosition;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #saveSuccess}.
	 */
	public static final String SAVE_SUCCESS = ReturnActivity.class.getName () + ".SAVE_SUCCESS";
	
	/**
	 * Flag used to indicate if a return save was successful.
	 */
	private boolean saveSuccess;
	
	/**
	 * Bundle key used to put/retrieve the content of the successful delete flag.
	 */
	public static final String DELETE_SUCCESS = ReturnActivity.class.getName () + ".DELETE_SUCCESS";
	
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
		setContentView ( R.layout.return_activity );
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.return_activity_title ) );
		// Define the empty list view
		getListView ().setEmptyView ( findViewById ( R.id.empty_list_view ) );
		// Display empty list label
		( (TextView) findViewById ( R.id.empty_list_view ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_return_list_label ) );
        // Perform the quick action setup
        setupQuickAction ();

		// Check if both the visit and the call are valid
		if ( getIntent ().getSerializableExtra ( CALL ) == null || getIntent ().getSerializableExtra ( VISIT ) == null ) {
			// Indicate that the activity cannot be displayed
			new AppToast ( this ).setIcon ( R.drawable.warning ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.invalid_selection_label ) ).show ();
			// Finish activity
			finish ();
			// Exit method
			return;
		} // End if
		
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
		// Action Item : Apply
		ActionItem apply = new ActionItem ( ActionItemID.APPLY ,
				AppResources.getInstance ( this ).getString ( this , R.string.return_label ) ,
				getResources ().getDrawable ( R.drawable.quick_action_ok ) );
    	// Populate the quick action widget with quick action items
		quickAction_Final.addActionItem ( view );
		quickAction_Editable.addActionItem ( view );
		quickAction_Editable.addActionItem ( apply );
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
		// Dismiss any displayed dialogs (to avoid activity context leak)
		AppDialog.getInstance ().dismiss ();
	}
	
	/*
	 * Called when the activity has detected the user's press of the back key. 
	 *
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed () {
		// Determine if a successful return save occurred
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
		// Determine if the current return is processed or deleted (final)
		// OR if the IS_VIEW flag is set
		if ( ( (ViewHolder) view.getTag () ).isFinal || getIntent ().getBooleanExtra ( IS_VIEW , false ) )
			// The current return is final
			// Display the appropriate quick action widget
			quickAction_Final.show ( view , null , getResources () );
		else
			// Otherwise the current return is editable
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
		case ActionItemID.VIEW:
			// Start the new activity
			startActivityForResult ( getIntent_ReturnDetailsActivity_Previous ( ( (ViewHolder) anchor.getTag () ).transactionCode , true ) ,
					ReturnDetailsActivity.REQUEST_CODE_INFO );
			// Specify an explicit transition animation to perform next
			ActivityTransition.SlideOutLeft ( this );
			break;
		case ActionItemID.APPLY:
			// Retrieve the transaction code
			final String transactionCode = ( (ViewHolder) anchor.getTag () ).transactionCode;
			// Create a new intent to start a new activity
			Intent intent = new Intent ( this , ReturnDetailsActivity.class );
			// Add the call object to the intent
			intent.putExtra ( ReturnDetailsActivity.CALL , getIntent ().getSerializableExtra ( CALL ) );
			// Add the visit object to the intent
			intent.putExtra ( ReturnDetailsActivity.VISIT , getIntent ().getSerializableExtra ( VISIT ) );
			// Add the request code to the intent
			intent.putExtra ( ReturnDetailsActivity.REQUEST_CODE , ReturnDetailsActivity.REQUEST_CODE_INFO );
			// Add the edit flag to the intent
			intent.putExtra ( ReturnDetailsActivity.IS_EDIT , true );
			// Add the transaction header code to the intent
			intent.putExtra ( ReturnDetailsActivity.TRANSACTION_HEADER_CODE , transactionCode );
			// Start the new activity
			startActivityForResult ( intent , ReturnDetailsActivity.REQUEST_CODE_INFO );
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
    	// Check the IS_VIEW flag
    	if ( ! getIntent ().getBooleanExtra ( IS_VIEW , false ) ) {
        	// Enable the required menu items
        	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_refresh ) );
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
    	// Determine if the action refresh is selected
    	if ( menuItem.getItemId () == R.id.action_refresh ) {
    		// Import approved returns
    		importApprovedReturns ();
    		// Consume event
    		return true;
    	} // End if
    	// Allow normal menu processing to proceed
    	return false;
    }
    
    /**
     * Start the import process to get approved returns from the server.
     */
    private void importApprovedReturns () {
		// Determine if the network is available
		if ( Network.networkAvailability ( this ) ) {
	        // Search for the GPRS link
	        ConnectionSettings connection = DatabaseUtils.getInstance ( this ).getDaoSession ().getConnectionSettingsDao ().queryBuilder ()
	        		.where ( ConnectionSettingsDao.Properties.ConnectionSettingType.eq ( ConnectionSettingsUtils.Type.GPRS ) ).unique ();
	        // Check if the connection is valid
	        if ( connection != null ) {
	        	// Valid connection
	        	// Clear lists
	        	approvedTransactionHeaders = null;
	        	approvedTransactionDetails = null;
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
			syncHelper.get ( this , DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionHeadersDao () , // WarehouseQuantities table
					new TypeToken < List < TransactionHeaders > > () {}.getType () , // Expected Type
					userCode , // UserCode
					companyCode , // CompanyCode
					this , // SyncListener
					RequestCode.ON_GOING , // Sync Request Code
					true ); // Execute in parallel
		} // End if
		// Determine the sync is on going
		else if ( requestCode == RequestCode.ON_GOING ) {
			// Determine if the retrieved data are transaction headers
			if ( dao.getTablename ().equals ( TransactionHeadersDao.TABLENAME ) ) {
				// Store the list
				approvedTransactionHeaders = new ArrayList < TransactionHeaders > ();
				for ( Object entity : entities )
					approvedTransactionHeaders.add ( (TransactionHeaders) entity );
				// Check if there is at least one return
				if ( ! entities.isEmpty () ) {
					// Retrieve the dao table data
					SyncHelper syncHelper = new SyncHelper ( ConnectionSettingsUtils.getGetMethodPath ( connection.getConnectionSettingURL () ) );
					syncHelper.get ( this , DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsDao () , // WarehouseQuantities table
							new TypeToken < List < TransactionDetails > > () {}.getType () , // Expected Type
							userCode , // UserCode
							companyCode , // CompanyCode
							this , // SyncListener
							RequestCode.ON_GOING , // Sync Request Code
							true ); // Execute in parallel
				} // End if
				else {
					// Dismiss any displayed dialogs (to avoid activity context leak)
					AppDialog.getInstance ().dismiss ();
					approvedTransactionDetails = new ArrayList < TransactionDetails > ();
					// Save imported returns
					saveImportedApprovedReturns ();
				} // End else
			} // End if
			// Determine if the retrieved data are transaction details
			else if ( dao.getTablename ().equals ( TransactionDetailsDao.TABLENAME ) ) {
				// Store the list
				approvedTransactionDetails = new ArrayList < TransactionDetails > ();
				for ( Object entity : entities )
					approvedTransactionDetails.add ( (TransactionDetails) entity );
				// Dismiss any displayed dialogs (to avoid activity context leak)
				AppDialog.getInstance ().dismiss ();
				// Save imported returns
				saveImportedApprovedReturns ();
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
		approvedTransactionHeaders = null;
		approvedTransactionDetails = null;
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
	 * Saves the imported approved returns.
	 */
	private void saveImportedApprovedReturns () {
		// Start by deleting non modified returns
		List < TransactionHeaders > transactionHeaders = DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionHeadersDao ().queryBuilder ()
				.where ( TransactionHeadersDao.Properties.IsProcessed.eq ( IsProcessedUtils.isWebProcessed () ) ).list ();
		ArrayList < String > transactionHeaderCodes = new ArrayList < String > ();
		for ( TransactionHeaders transactionHeader : transactionHeaders )
			transactionHeaderCodes.add ( transactionHeader.getTransactionCode () );
		DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionHeadersDao ().deleteInTx ( transactionHeaders );
		List < TransactionDetails > transactionDetails = DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsDao ().queryBuilder ()
				.where ( TransactionDetailsDao.Properties.TransactionCode.in ( transactionHeaderCodes ) ).list ();
		DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsDao ().deleteInTx ( transactionDetails );
		// Retrieve the approved transaction codes
		ArrayList < String > approvedTransactionCodes = new ArrayList < String > ();
		for ( TransactionHeaders transactionHeader : approvedTransactionHeaders )
			approvedTransactionCodes.add ( transactionHeader.getTransactionCode () );
		// Check for modified returns
		transactionHeaders = DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionHeadersDao ().queryBuilder ()
				.where ( TransactionHeadersDao.Properties.TransactionCode.in ( approvedTransactionCodes ) ).list ();
		ArrayList < String > modifiedTransactionCodes = new ArrayList < String > ();
		for ( TransactionHeaders transactionHeader : transactionHeaders )
			modifiedTransactionCodes.add ( transactionHeader.getTransactionCode () );
		try {
			// Begin transaction
			DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().beginTransaction ();
			// Add the retrieved returns except for the modified ones
			for ( TransactionHeaders transactionHeader : approvedTransactionHeaders )
				if ( ! modifiedTransactionCodes.contains ( transactionHeader.getTransactionCode () ) )
					DatabaseUtils.getInstance ( this ).getDaoSession ().insert ( transactionHeader );
			for ( TransactionDetails transactionDetail : approvedTransactionDetails )
				if ( ! modifiedTransactionCodes.contains ( transactionDetail.getTransactionCode () ) )
					DatabaseUtils.getInstance ( this ).getDaoSession ().insert ( transactionDetail );
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
			approvedTransactionHeaders = null;
			approvedTransactionDetails = null;
		} // End try-catch-finally block
	}
    
    /**
     * Gets an intent for the return details activity for a previous (edit or view) return.<br>
     * The default implementation uses {@link me.SyncWise.Android.Modules.Return.ReturnDetailsActivity ReturnDetailsActivity}.<br>
     * In order to used a custom adapter, override this method.<br>
	 * <b>Note : </b> This method should not return {@code NULL}.
     * 
     * @param transactionHeaderCode	String hosting the transaction header code.
     * @param isView	Boolean used to indicate if the previous return will be viewed or edited.
     * @return	Intent used to start the return details activity.
     */
    protected Intent getIntent_ReturnDetailsActivity_Previous ( final String transactionHeaderCode , final boolean isView ) {
		// Create a new intent to start a new activity
		Intent intent = new Intent ( this , ReturnDetailsActivity.class );
		// Add the request code to the intent
		intent.putExtra ( ReturnDetailsActivity.REQUEST_CODE , ReturnDetailsActivity.REQUEST_CODE_INFO );
		// Add the transaction header code to the intent
		intent.putExtra ( ReturnDetailsActivity.TRANSACTION_HEADER_CODE , transactionHeaderCode );
		// Add the call object to the intent
		intent.putExtra ( ReturnDetailsActivity.CALL , getIntent ().getSerializableExtra ( CALL ) );
		// Add the visit object to the intent
		intent.putExtra ( ReturnDetailsActivity.VISIT , getIntent ().getSerializableExtra ( VISIT ) );
		// Check if the is view flag is set
		if ( isView )
			// Add the view only flag to the intent
			intent.putExtra ( ReturnDetailsActivity.IS_VIEW_ONLY , true );
		else
			// Add the edit flag to the intent
			intent.putExtra ( ReturnDetailsActivity.IS_EDIT , true );
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

    	// Check if the return has been successfully deleted
    	if ( data.getBooleanExtra ( DELETE_SUCCESS , false ) ) {
			// Display baguette message
			Baguette.showText ( this , AppResources.getInstance ( this ).getString ( this , R.string.return_delete_success_message ) , Baguette.BackgroundColor.GREEN );
			// Clear list adapter
			setListAdapter ( null );
			// Populate the list by setting a new adapter
			populate ();
			// Exit method
			return;
    	} // End if
    	
    	// Determine the provided request code
    	switch ( requestCode ) {
		case ReturnDetailsActivity.REQUEST_CODE_NEW:
			// Set flag
			saveSuccess = saveSuccess || data.getBooleanExtra ( SAVE_SUCCESS , false );
		case ReturnDetailsActivity.REQUEST_CODE_INFO:
			// Display baguette message
			Baguette.showText ( this ,
					AppResources.getInstance ( this ).getString ( this ,
							data.getBooleanExtra ( SAVE_SUCCESS , false ) ? R.string.return_save_success_message : R.string.return_save_failure_message ) ,
							data.getBooleanExtra ( SAVE_SUCCESS , false ) ? Baguette.BackgroundColor.GREEN : Baguette.BackgroundColor.RED );
			// Clear list adapter
			setListAdapter ( null );
			
			// Populate the list by setting a new adapter
			populate ();

			break;
		} // End switch
    	
	}
	
	/**
	 * Retrieve all the returns asynchronously.
	 */
	protected void populate () {
		// Populate the list by setting a new adapter
		new PopulateList ().execute ();
	}

	/**
	 * Populates the return list by generating a map of cursors holding returns, that belongs for a specific day, for the specified user.
	 * 
	 * @param context	The application context.
	 * @param call	The call object for which the returns belong to.
	 * @param appliedOnly	Boolean indicating if only the applied returns are to be displayed.
	 * @return	Cursors holding returns data set mapped to their appropriate dates.
	 */
	public static Map < String , Cursor > populateList ( final Context context , final Call call , final boolean appliedOnly ) {
		// Compute the selection clause
		String selection = TransactionHeadersDao.Properties.ClientCode.columnName + " = ? AND " + TransactionHeadersDao.Properties.CompanyCode.columnName + " = ? AND " + TransactionHeadersDao.Properties.DivisionCode.columnName + " = ? AND " 
				+ TransactionHeadersDao.Properties.TransactionType.columnName + " = ? AND "
				+ TransactionHeadersDao.Properties.TransactionStatus.columnName + " = ? " + ( appliedOnly ? " AND " + TransactionHeadersDao.Properties.IsProcessed.columnName + " = " + IsProcessedUtils.isNotSync () : "" ) ;
		// Compute the selection arguments
		String selectionArgs [] = new String [] {
				call.getClientDivision ().getClientCode () , call.getClientDivision ().getCompanyCode () , call.getClientDivision ().getDivisionCode () ,
				String.valueOf ( TransactionHeadersUtils.Type.RETURN ) ,
				String.valueOf ( StatusUtils.isAvailable () )
		};
		// Compute the order by clause
		String orderBy = TransactionHeadersDao.Properties.IssueDate.columnName;
		// Declare and initialize a cursor in order to query DB
		Cursor cursor = DatabaseUtils.getInstance ( context ).getDaoSession ().getDatabase ()
				.query ( TransactionHeadersDao.TABLENAME , new String [] { TransactionHeadersDao.Properties.IssueDate.columnName } , selection , selectionArgs , null , null , orderBy );
		
		// Declare and initialize a list of calendars, used to compute the return dates
		List < Calendar > dates = new ArrayList < Calendar > ();
		// Move the cursor to the first raw
		if ( cursor.moveToFirst () ) {
			// Iterate over all raws
			do {
				// Compute the current transaction header date
				Calendar transactionDate = Calendar.getInstance ();
				transactionDate.setTimeInMillis ( cursor.getLong ( cursor.getColumnIndex ( TransactionHeadersDao.Properties.IssueDate.columnName ) ) );
				transactionDate.set ( Calendar.HOUR_OF_DAY , 0 );
				transactionDate.set ( Calendar.MINUTE , 0 );
				transactionDate.set ( Calendar.SECOND , 0 );
				transactionDate.set ( Calendar.MILLISECOND , 0 );
				// Check if the list of dates is empty
				if ( dates.isEmpty () )
					// Add the current transaction header date
					dates.add ( transactionDate );
				// Otherwise the list of dates is NOT empty
				// Check if the last date is in the same day (the cursor is ordered by date)
				else if ( dates.get ( dates.size () - 1 ).get ( Calendar.DAY_OF_YEAR ) == transactionDate.get ( Calendar.DAY_OF_YEAR )
						&& dates.get ( dates.size () - 1 ).get ( Calendar.YEAR ) == transactionDate.get ( Calendar.YEAR ) ) {
					// They are both the same day, do nothing
				} // End else if
				// Otherwise they are not the same day
				else
					// Add the current transaction header date
					dates.add ( transactionDate );
			} while ( cursor.moveToNext () );
		} // End if
		// Close the cursor
		cursor.close ();
		cursor = null;
		
		// Adjust the selection clause
		selection += " AND " + TransactionHeadersDao.Properties.IssueDate.columnName + " BETWEEN ? AND ?";
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
		String sql = "SELECT T."
				+ TransactionHeadersDao.Properties.Id.columnName + ","
				+ TransactionHeadersDao.Properties.TransactionCode.columnName + ","
				+ TransactionHeadersDao.Properties.VisitID.columnName + ","
				+ TransactionHeadersDao.Properties.IssueDate.columnName + ","
				+ TransactionHeadersDao.Properties.IsProcessed.columnName + ","
				+ TransactionHeadersDao.Properties.TransactionStatus.columnName + " "
				+ " FROM " + TransactionHeadersDao.TABLENAME + " T "
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
	 * AsyncTask helper class used to populate the return items list.
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
			// Populate the list using the current client
			return populateList ( ReturnActivity.this , (Call) getIntent ().getSerializableExtra ( CALL ) , false );
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( Map < String , Cursor > cursors ) {
			// Compute the total number of returns
			int totalReturns = 0;
			// Iterate over all cursors
			for ( String date : cursors.keySet () )
				// Add the number of returns
				totalReturns += cursors.get ( date ).getCount ();
			// Set the action bar's subtitle
			if ( getActionBar () != null )
				getActionBar ().setSubtitle ( totalReturns == 0 ? "" : AppResources.getInstance ( ReturnActivity.this ).getString ( ReturnActivity.this , R.string.total_of_label ) + " : " + totalReturns );
			// Declare and initialize a multiple list adapter
			MultipleListAdapter adapter = new MultipleListAdapter ( ReturnActivity.this );
			// Iterate over all cursors
			for ( String date : cursors.keySet () )
				// Add a new return adapter using the current cursor
				adapter.addSection ( new Section ( date , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
						new ReturnAdapter ( ReturnActivity.this , cursors.get ( date ) , R.layout.return_activity_item ) );
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