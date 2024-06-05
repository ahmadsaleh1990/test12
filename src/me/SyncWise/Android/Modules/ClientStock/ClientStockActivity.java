/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.ClientStock;

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
import me.SyncWise.Android.Database.ClientStockCountHeadersDao;
import me.SyncWise.Android.Database.ClientStockCountHeadersUtils.CountType;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Gson.BaseTimerListActivity;
import me.SyncWise.Android.Modules.Call.CallMenuActivity;
import me.SyncWise.Android.SectionsList.MultipleListAdapter;
import me.SyncWise.Android.SectionsList.Section;
import me.SyncWise.Android.Widgets.Baguette;
import me.SyncWise.Android.Widgets.QuickAction.ActionItem;
import me.SyncWise.Android.Widgets.QuickAction.QuickAction;
import me.SyncWise.Android.Modules.ClientStock.ClientStockAdapter.ViewHolder;
import me.SyncWise.Android.Modules.Journey.Call;
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
 * Activity implemented to view all the performed client Stock.
 * 
 * @author Elias
 * @sw.todo <b>Client Stock Activity Implementation :</b><br>
 * Simply add this class in the {@code AndroidManifest.xml} file.
 *
 */
public class ClientStockActivity extends BaseTimerListActivity implements QuickAction.OnActionItemClickListener {

	/**
	 * Bundle key used to put/retrieve the content of {@link #itemsListMode}.
	 */
	public static final String ITEMS_LIST_MODE = ClientStockActivity.class.getName () + ".ITEMS_LIST_MODE";

	/**
	 * Reference to a {@link me.SyncWise.Android.Widgets.QuickAction.QuickAction QuickAction} used in this activity.<br>
	 * This widget is used for final client stock counts.
	 */
	protected QuickAction quickAction_Final;
	
	/**
	 * Reference to a {@link me.SyncWise.Android.Widgets.QuickAction.QuickAction QuickAction} used in this activity.<br>
	 * This widget is used for editable client stock counts.
	 */
	private QuickAction quickAction_Editable;
	
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
	}
	
	/**
	 * Bundle key used to put/retrieve the content of the current call.
	 */
	public static final String CALL = ClientStockActivity.class.getName () + ".CALL";
	
	/**
	 * Bundle key used to put/retrieve the content of the current visit.
	 */
	public static final String VISIT = ClientStockActivity.class.getName () + ".VISIT";
	
	/**
	 * Bundle key used to put/retrieve the content of the is view flag.<br>
	 * This flag is used to determine if the user can  view the previous client stock.
	 */
	public static final String IS_VIEW = ClientStockActivity.class.getName () + ".IS_VIEW";
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #listViewPosition}.
	 */
	private static final String LIST_VIEW_POSITION = ClientStockActivity.class.getName () + ".LIST_VIEW_POSITION";
	
	/**
	 * Object used to save/restore the list views' position.
	 */
	private ListViewPosition listViewPosition;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #selectCountType}.
	 */
	private static final String SELECT_COUNT_TYPE = ClientStockActivity.class.getName () + ".SELECT_COUNT_TYPE";
	
	/**
	 * Flag used to determine if the user should be prompted to choose a count type.
	 */
	private boolean selectCountType;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #saveSuccess}.
	 */
	public static final String SAVE_SUCCESS = ClientStockActivity.class.getName () + ".SAVE_SUCCESS";
	
	/**
	 * Flag used to indicate if a client stock count save was successful.
	 */
	private boolean saveSuccess;
	
	/**
	 * Bundle key used to put/retrieve the content of the successful delete flag.
	 */
	public static final String DELETE_SUCCESS = ClientStockActivity.class.getName () + ".DELETE_SUCCESS";
	
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
		setContentView ( R.layout.sales_order_activity );
		// Compute title
		String title = AppResources.getInstance ( this ).getString ( this , R.string.client_stock_activity_title );
		if ( getIntent ().getIntExtra ( ITEMS_LIST_MODE , 0 ) == ClientStockDetailsActivity.ASSETS_LIST )
			title = AppResources.getInstance ( this ).getString ( this , R.string.client_asset_activity_title );
		// Change the title associated with this activity
		setTitle ( title );
		// Define the empty list view
		getListView ().setEmptyView ( findViewById ( R.id.empty_list_view ) );
		// Compute empty label
		String emptyLabel = AppResources.getInstance ( this ).getString ( this , R.string.empty_client_stock_list_label );
		if ( getIntent ().getIntExtra ( ITEMS_LIST_MODE , 0 ) == ClientStockDetailsActivity.ASSETS_LIST )
			emptyLabel = AppResources.getInstance ( this ).getString ( this , R.string.empty_client_asset_list_label );
		// Display empty list label
		( (TextView) findViewById ( R.id.empty_list_view ) ).setText ( emptyLabel );
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
			selectCountType = savedInstanceState.getBoolean ( SELECT_COUNT_TYPE , selectCountType );
			listViewPosition = (ListViewPosition) savedInstanceState.getSerializable ( LIST_VIEW_POSITION );
		} // End if
		
		// Determine if the list view has a valid adapter
		if ( getListAdapter () == null )
			// Populate the list by setting a new adapter
			new PopulateList ().execute ();
	}
	
	/*
	 * Performs all necessary setup in order to properly display the quick action widget.
	 *
	 * @see me.SyncWise.Android.Modules.Journey.JourneyFragment#setupQuickAction()
	 */
    private void setupQuickAction () {
    	// Initialize the quick action widgets
		quickAction_Final = new QuickAction ( this , QuickAction.VERTICAL );
		quickAction_Editable = new QuickAction ( this , QuickAction.VERTICAL );
		// Action Item : View
		ActionItem view = new ActionItem ( ActionItemID.VIEW ,
				AppResources.getInstance ( this ).getString ( this , R.string.view_label ) ,
				getResources ().getDrawable ( R.drawable.quick_action_search ) );
		// Action Item : Edit
		ActionItem edit = new ActionItem ( ActionItemID.EDIT ,
				AppResources.getInstance ( this ).getString ( this , R.string.edit_label ) ,
				getResources ().getDrawable ( R.drawable.quick_action_edit ) );
    	// Populate the quick action widget with quick action items
		quickAction_Final.addActionItem ( view );
		quickAction_Editable.addActionItem ( view );
		quickAction_Editable.addActionItem ( edit );
		// Assign an action item click listener
		quickAction_Final.setOnActionItemClickListener ( this );
		quickAction_Editable.setOnActionItemClickListener ( this );
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
    	// Check if the user must select a count type
    	if ( selectCountType )
    		// Prompt the user
    		chooseCountType ();
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
		// Determine if a successful client stock count save occurred
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
			quickAction_Editable = null;
			quickAction_Final = null;
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
    	// Save the content of selectCountType in the outState bundle
    	outState.putBoolean ( SELECT_COUNT_TYPE , selectCountType );
    }
	
    /*
     * This method will be called when an item in the list is selected.
     *
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
	@Override
    protected void onListItemClick ( ListView listView , View view , final int position , long id ) {
		// Determine if the current sales order is processed or deleted (final)
		// OR if the IS_VIEW flag is set
		if ( ( (ViewHolder) view.getTag () ).isFinal || getIntent ().getBooleanExtra ( IS_VIEW , false ) )
			// The current sales order is final
			// Display the appropriate quick action widget
			quickAction_Final.show ( view , null , getResources () );
		else
			// Otherwise the current sales order is editable
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
			// Start the new activity
			startActivityForResult ( getIntent_ClientStockDetailsActivity_Previous ( ( (ViewHolder) anchor.getTag () ).code , false ) , ClientStockDetailsActivity.REQUEST_CODE_INFO );
			// Specify an explicit transition animation to perform next
			ActivityTransition.SlideOutLeft ( this );
			break;
		case ActionItemID.VIEW:
			// Start the new activity
			startActivityForResult ( getIntent_ClientStockDetailsActivity_Previous ( ( (ViewHolder) anchor.getTag () ).code , true ) , ClientStockDetailsActivity.REQUEST_CODE_INFO );
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
		me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_add ) );
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
    		// Start a new client stock count
    		startNewClientStock ( null );
    		// Consume event
    		return true;
    	} // End if
    	// Allow normal menu processing to proceed
    	return false;
    }
    
    /**
     * Starts the {@link me.SyncWise.Android.Modules.ClientStock.ClientStockDetailsActivity ClientStockDetailsActivity} activity in order to create a new client stock count.
     */
    protected void startNewClientStock ( Integer countType ) {
    	// Check if a valid count type is provided
    	if ( countType == null || ( countType != CountType.SHELF && countType != CountType.STOCK ) ) {
    		// The provided count type is NOT valid
    		// Set the flag
    		selectCountType = true;
    		// Prompt the user for a count type
    		chooseCountType ();
    		// Exit method
    		return;
    	} // End if
    		
		// Reset the flag
    	selectCountType = false;

		// Start the new activity
		startActivityForResult ( getIntent_ClientStockDetailsActivity_New ( countType ) , ClientStockDetailsActivity.REQUEST_CODE_NEW );
		// Specify an explicit transition animation to perform next
		ActivityTransition.SlideOutLeft ( this );
    }
    
    /**
     * Gets an intent for the client stock count details activity for a new client Stock.<br>
     * The default implementation uses {@link me.SyncWise.Android.Modules.ClientStock.ClientStockActivity ClientStockActivity}.<br>
     * In order to used a custom adapter, override this method.<br>
	 * <b>Note : </b> This method should not return {@code NULL}.
     * 
     * @param countType	Integer holding the selected client stock count type.
     * @return	Intent used to start the client stock details activity.
     */
    protected Intent getIntent_ClientStockDetailsActivity_New ( final Integer countType ) {
		// Create a new intent to start a new activity
		Intent intent = new Intent ( this , ClientStockDetailsActivity.class );
		// Add the call object to the intent
		intent.putExtra ( ClientStockDetailsActivity.CALL , getIntent ().getSerializableExtra ( CALL ) );
		// Add the visit object to the intent
		intent.putExtra ( ClientStockDetailsActivity.VISIT , getIntent ().getSerializableExtra ( VISIT ) );
		// Add the request code to the intent
		intent.putExtra ( ClientStockDetailsActivity.REQUEST_CODE , ClientStockDetailsActivity.REQUEST_CODE_NEW );
		// Add the count type to the intent
		intent.putExtra ( ClientStockDetailsActivity.COUNT_TYPE , countType );
		// Add the item type to the intent
		intent.putExtra ( ClientStockDetailsActivity.ITEMS_LIST_MODE , getIntent ().getIntExtra ( ITEMS_LIST_MODE , 0 ) );
		// Return the computed intent
		return intent;
    }
    
    /**
     * Gets an intent for the client stock details activity for a previous (edit or view) client stock.<br>
     * The default implementation uses {@link me.SyncWise.Android.Modules.ClientStock.ClientStockActivity ClientStockActivity}.<br>
     * In order to used a custom adapter, override this method.<br>
	 * <b>Note : </b> This method should not return {@code NULL}.
     * 
     * @param ClientStockHeaderCode	String hosting the Client Stock header code.
     * @param isView	Boolean used to indicate if the previous client stock  will be viewed .
     * @return	Intent used to start the client stock count details activity.
     */
    protected Intent getIntent_ClientStockDetailsActivity_Previous ( final String ClientStockHeaderCode , final boolean isView ) {
		// Create a new intent to start a new activity
		Intent intent = new Intent ( this , ClientStockDetailsActivity.class );
		// Add the call object to the intent
		intent.putExtra ( ClientStockDetailsActivity.CALL , getIntent ().getSerializableExtra ( CALL ) );
		// Add the visit object to the intent
		intent.putExtra ( ClientStockDetailsActivity.VISIT , getIntent ().getSerializableExtra ( VISIT ) );
		// Add the item type to the intent
		intent.putExtra ( ClientStockDetailsActivity.ITEMS_LIST_MODE , getIntent ().getIntExtra ( ITEMS_LIST_MODE , 0 ) );
		// Add the request code to the intent
		intent.putExtra ( ClientStockDetailsActivity.REQUEST_CODE , ClientStockDetailsActivity.REQUEST_CODE_INFO );
		// Add the transaction header code to the intent
		intent.putExtra ( ClientStockDetailsActivity.CLIENT_STOCK_HEADER_CODE , ClientStockHeaderCode );
		// Check if the is view flag is set
		if ( isView )
			// Add the view only flag to the intent
			intent.putExtra ( ClientStockDetailsActivity.IS_VIEW_ONLY , true );
		else
			// Add the edit flag to the intent
			intent.putExtra ( ClientStockDetailsActivity.IS_EDIT , true );
		// Return the computed intent
		return intent;
    }
    
    /**
     * Prompts the user with the list of count types for him/her to choose.
     */
    private void chooseCountType () {
    	// Build the count type list
    	final String [] countTypes = new String [] {
	    	AppResources.getInstance ( this ).getString ( this , R.string.client_stock_count_type_shelf_label ) ,
	    	AppResources.getInstance ( this ).getString ( this , R.string.client_stock_count_type_stock_label ) };
		// Prompt the user to choose a count type
		AppDialog.getInstance ().displayList ( this ,
				AppResources.getInstance ( this ).getString ( this , R.string.client_stock_count_type_list_dialog_title ) ,
				countTypes ,
				AppDialog.Cancelable.BACK_BUTTON_AND_TOUCH ,
				new DialogInterface.OnClickListener () {
					@Override
					public void onClick ( DialogInterface dialog , int which ) {
						// Start a new client stock count using the selected count type
						startNewClientStock ( which + 1 );
					}
				} ,
				new DialogInterface.OnCancelListener () {
					@Override
					public void onCancel ( DialogInterface dialog ) {
						// The user has chosen not to choose a count type for the client stock count
						// Meaning he/she cannot start a new client stock count
						// Clear the flag
						selectCountType = false;
					}
				} );
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

    	// Check if the client stock count has been successfully deleted
    	if ( data.getBooleanExtra ( DELETE_SUCCESS , false ) ) {
			// Display baguette message
			Baguette.showText ( this , AppResources.getInstance ( this ).getString ( this , R.string.client_stock_delete_success_message ) , Baguette.BackgroundColor.GREEN );
			// Clear list adapter
			setListAdapter ( null );
			// Populate the list by setting a new adapter
			new PopulateList ().execute ();
			// Exit method
			return;
    	} // End if
    	
    	// Determine the provided request code
    	switch ( requestCode ) {
		case ClientStockDetailsActivity.REQUEST_CODE_NEW:
			// Set flag
			saveSuccess = saveSuccess || data.getBooleanExtra ( SAVE_SUCCESS , false );
		case ClientStockDetailsActivity.REQUEST_CODE_INFO:
			// Display baguette message
			Baguette.showText ( this ,
					AppResources.getInstance ( this ).getString ( this ,
							data.getBooleanExtra ( SAVE_SUCCESS , false ) ? R.string.client_stock_save_success_message : R.string.client_stock_save_failure_message ) ,
							data.getBooleanExtra ( SAVE_SUCCESS , false ) ? Baguette.BackgroundColor.GREEN : Baguette.BackgroundColor.RED );
			// Clear list adapter
			setListAdapter ( null );
			invalidateOptionsMenu();
			// Populate the list by setting a new adapter
			new PopulateList ().execute ();

			break;
		} // End switch
	}

	/**
	 * Populates the client stock list by generating a map of cursors holding client stock, that belongs for a specific day, for the specified user.
	 * 
	 * @param context	The application context.
	 * @param call	The call object for which the client stock belong to.
	 * @param itemCount	Integer holding the item count type.
	 * @return	Cursors holding client data set mapped to their appropriate dates.
	 */
	public static Map < String , Cursor > populateList ( final Context context , final Call call , final Integer itemCount ) {
		// Compute the selection clause
		String selection = ClientStockCountHeadersDao.Properties.ClientCode.columnName + " = ? AND " + ClientStockCountHeadersDao.Properties.CompanyCode.columnName + " = ? AND " + ClientStockCountHeadersDao.Properties.DivisionCode.columnName + " = ? " 
				+ ( itemCount != null ? " AND " + ClientStockCountHeadersDao.Properties.ItemType.columnName + " = " + itemCount : "" );
		// Compute the selection arguments
		String selectionArgs [] = new String [] { call.getClientDivision ().getClientCode () , call.getClientDivision ().getCompanyCode () , call.getClientDivision ().getDivisionCode () };
		// Compute the order by clause
		String orderBy = ClientStockCountHeadersDao.Properties.Date.columnName;
		// Declare and initialize a cursor in order to query DB
		Cursor cursor = DatabaseUtils.getInstance ( context ).getDaoSession ().getDatabase ()
				.query ( ClientStockCountHeadersDao.TABLENAME , new String [] { ClientStockCountHeadersDao.Properties.Date.columnName } , selection , selectionArgs , null , null , orderBy );
		
		// Declare and initialize a list of calendars, used to compute the client stock count dates
		List < Calendar > dates = new ArrayList < Calendar > ();
		// Move the cursor to the first raw
		if ( cursor.moveToFirst () ) {
			// Iterate over all raws
			do {
				// Compute the current ClientStock header date
				Calendar CountDate = Calendar.getInstance ();
				CountDate.setTimeInMillis ( cursor.getLong ( cursor.getColumnIndex ( ClientStockCountHeadersDao.Properties.Date.columnName ) ) );
				CountDate.set ( Calendar.HOUR_OF_DAY , 0 );
				CountDate.set ( Calendar.MINUTE , 0 );
				CountDate.set ( Calendar.SECOND , 0 );
				CountDate.set ( Calendar.MILLISECOND , 0 );
				// Check if the list of dates is empty
				if ( dates.isEmpty () )
					// Add the current transaction header date
					dates.add ( CountDate );
				// Otherwise the list of dates is NOT empty
				// Check if the last date is in the same day (the cursor is ordered by date)
				else if ( dates.get ( dates.size () - 1 ).get ( Calendar.DAY_OF_YEAR ) == CountDate.get ( Calendar.DAY_OF_YEAR )
						&& dates.get ( dates.size () - 1 ).get ( Calendar.YEAR ) == CountDate.get ( Calendar.YEAR ) ) {
					// They are both the same day, do nothing
				} // End else if
				// Otherwise they are not the same day
				else
					// Add the current transaction header date
					dates.add ( CountDate );
			} while ( cursor.moveToNext () );
		} // End if
		// Close the cursor
		cursor.close ();
		cursor = null;
		
		// Adjust the selection clause
		selection += " AND " + ClientStockCountHeadersDao.Properties.Date.columnName + " BETWEEN ? AND ?";
		// Adjust the selection arguments
		selectionArgs = new String [] {
				selectionArgs [ 0 ] ,
				selectionArgs [ 1 ] ,
				selectionArgs [ 2 ] ,
				null ,
				null ,
		};
		// Compute the SQL string
		String sql = "SELECT "
				+ ClientStockCountHeadersDao.Properties.Id.columnName + ","
				+ ClientStockCountHeadersDao.Properties.TransactionCode.columnName + ","
				+ ClientStockCountHeadersDao.Properties.ClientCode.columnName + ","
				+ ClientStockCountHeadersDao.Properties.UserCode.columnName + ","
				+ ClientStockCountHeadersDao.Properties.VisitID.columnName + ","
				+ ClientStockCountHeadersDao.Properties.CompanyCode.columnName + ","
				+ ClientStockCountHeadersDao.Properties.CurrencyCode.columnName + ","
				+ ClientStockCountHeadersDao.Properties.Note.columnName + ","
				+ ClientStockCountHeadersDao.Properties.Date.columnName + ","
				+ ClientStockCountHeadersDao.Properties.ItemType.columnName + ","
				+ ClientStockCountHeadersDao.Properties.IsProcessed.columnName +","
				+ ClientStockCountHeadersDao.Properties.StampDate.columnName 
				+ " FROM " + ClientStockCountHeadersDao.TABLENAME 
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
			selectionArgs [ 3 ] = String.valueOf ( start.getTimeInMillis () );
			selectionArgs [ 4 ] = String.valueOf ( end.getTimeInMillis () );
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
	 * AsyncTask helper class used to populate the client stock items list.
	 * 
	 * @author Rabee
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
			// Determine the item type
			Integer itemType = null;
			int temp = getIntent ().getIntExtra ( ITEMS_LIST_MODE , 0 );
			itemType = temp == 0 ? null : temp;
			// Populate the list using the current client
			return populateList ( ClientStockActivity.this , (Call) getIntent ().getSerializableExtra ( CALL ) , itemType );
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( Map < String , Cursor > cursors ) {
			
			// Declare and initialize a multiple list adapter
			MultipleListAdapter adapter = new MultipleListAdapter ( ClientStockActivity.this );
			// Iterate over all cursors
			for ( String date : cursors.keySet () )
				// Add a new client stock count adapter using the current cursor
				adapter.addSection ( new Section ( date , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
						new ClientStockAdapter ( ClientStockActivity.this , cursors.get ( date ) , R.layout.sales_order_activity_item ) );
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