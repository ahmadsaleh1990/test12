/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.ClientStock;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.SyncWise.Android.AppDialog;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.AppToast;
import me.SyncWise.Android.DateTime;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Vibration;
import me.SyncWise.Android.AppDialog.ButtonsType;
import me.SyncWise.Android.Database.AssetsStatus;
import me.SyncWise.Android.Database.AssetsStatusDao;
import me.SyncWise.Android.Database.ClientAssetStatus;
import me.SyncWise.Android.Database.ClientAssetStatusDao;
import me.SyncWise.Android.Database.ClientAssetsPictures;
import me.SyncWise.Android.Database.ClientAssetsPicturesDao;
import me.SyncWise.Android.Database.ClientAssetsPicturesUtils;
import me.SyncWise.Android.Database.ClientCurrenciesDao;
 
import me.SyncWise.Android.Database.ClientItemHistory;
import me.SyncWise.Android.Database.ClientItemHistoryDao;
import me.SyncWise.Android.Database.ClientMustStockList;
import me.SyncWise.Android.Database.ClientPriceListsDao;
import me.SyncWise.Android.Database.ClientStockCountDetails;
import me.SyncWise.Android.Database.ClientStockCountDetailsDao;
import me.SyncWise.Android.Database.ClientStockCountHeaders;
import me.SyncWise.Android.Database.ClientStockCountHeadersDao;
 
import me.SyncWise.Android.Database.Currencies;
import me.SyncWise.Android.Database.CurrenciesDao;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.Divisions;
import me.SyncWise.Android.Database.DivisionsDao;
import me.SyncWise.Android.Database.DivisionsUtils;
import me.SyncWise.Android.Database.EntityUtils;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.ItemBarcodes;
import me.SyncWise.Android.Database.ItemBarcodesDao;
import me.SyncWise.Android.Database.ItemDivisions;
import me.SyncWise.Android.Database.ItemDivisionsDao;
import me.SyncWise.Android.Database.Items;
import me.SyncWise.Android.Database.ItemsDao;
import me.SyncWise.Android.Database.ItemsUtils;

import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Database.Prices;
import me.SyncWise.Android.Database.PricesDao;
import me.SyncWise.Android.Database.Taxes;
import me.SyncWise.Android.Database.TransactionHeadersUtils;
import me.SyncWise.Android.Database.TransactionSequencesDao;
import me.SyncWise.Android.Database.Units;
import me.SyncWise.Android.Database.Users;
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Database.Visits;
import me.SyncWise.Android.Gson.BaseTimerListActivity;
 
import me.SyncWise.Android.Modules.Call.CallAction;
import me.SyncWise.Android.Modules.FilterDivisions.FilterDivisionsActivity;
import me.SyncWise.Android.Modules.Journey.Call;
 
import me.SyncWise.Android.SectionsList.MultipleListAdapter;
import me.SyncWise.Android.SectionsList.Section;
import me.SyncWise.Android.Utilities.ActivityInstance;
 
import me.SyncWise.Android.Widgets.Baguette;
import me.SyncWise.Android.Widgets.CustomLinearLayout;
import me.SyncWise.Android.Widgets.NumberPicker;
import me.SyncWise.Android.Widgets.QuickAction.ActionItem;
import me.SyncWise.Android.Widgets.QuickAction.QuickAction;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog.OnDateSetListener;
 
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
 
import android.text.InputFilter;
 
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

/**
 * Activity implemented to perform, view or edit a client stock count.
 * 
 * @author Elias
 * @sw.todo <b>Client Stock Details Activity Implementation :</b><br>
 * Simply add this class in the {@code AndroidManifest.xml} file along with the permission :<br>
 * {@code <uses-permission android:name="android.permission.VIBRATE"/>}<br>
 * AND disable activity recreation for orientation events by adding the following to the activity tag in the manifest file :<br>
 * {@code android:configChanges="orientation|screenSize"}
 * 
 */
public class ClientStockDetailsActivity extends BaseTimerListActivity implements SearchView.OnQueryTextListener , QuickAction.OnActionItemClickListener , OnDateSetListener , OnClickListener , OnItemSelectedListener , OnFocusChangeListener {

	/**
	 * Boolean used to indicate if the activity is displayed on the screen.
	 */
	public static boolean isDisplayed;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #requestCode}.
	 */
	public static final String REQUEST_CODE = ClientStockDetailsActivity.class.getName () + ".REQUEST_CODE";
	
	/**
	 * Integer used to host the request code.
	 * @see #REQUEST_CODE_NEW
	 * @see #REQUEST_CODE_INFO
	 */
	protected int requestCode;
	
	/**
	 * Constant integer holding the request code to create a new client stock count.
	 */
	public static final int REQUEST_CODE_NEW = 1;
	
	/**
	 * Constant integer holding the request code to display the client stock count info.
	 */
	public static final int REQUEST_CODE_INFO = 2;

	/**
	 * Bundle key used to put/retrieve the content of the edit flag.
	 */
	public static final String IS_EDIT = ClientStockDetailsActivity.class.getName () + ".IS_EDIT";
	
	/**
	 * Bundle key used to put/retrieve the content of the view only flag.
	 */
	public static final String IS_VIEW_ONLY = ClientStockDetailsActivity.class.getName () + ".IS_VIEW_ONLY";	
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #displaysTOCKDetails}.
	 */
	private static final String DISPLAY_STOCK_DETAILS = ClientStockDetailsActivity.class.getName () + ".DISPLAY_STOCK_DETAILS";
	
	/**
	 * Boolean used to determine whether to display the client stock count details UI or not.<br>
	 * This boolean is mainly used to save the client stock count state.
	 */
	protected boolean displayStockDetails;
	/**
	 * Reference to the search view in the action bar.
	 */
	private SearchView searchView;
	/**
	 * Bundle key used to put/retrieve the content of {@link #stockModification}.
	 */
	private static final String STOCK_MODIFICATION = ClientStockDetailsActivity.class.getName () + ".STOCK_MODIFICATION";
	
	/**
	 * Reference to the {@link me.SyncWise.Android.Modules.ClientStock ClientStock} object being modified.
	 */
	protected Stock stockModification;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #displayStockNote}.
	 */
	private static final String DISPLAY_STOCK_NOTE = ClientStockDetailsActivity.class.getName () + ".DISPLAY_STOCK_NOTE";
	
	/**
	 * Boolean used to determine whether to display the client stock count note UI or not.<br>
	 * This boolean is mainly used to save the client stock count state.
	 */
	protected boolean displayStockNote;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #itemsListMode}.
	 */
	public static final String ITEMS_LIST_MODE = ClientStockDetailsActivity.class.getName () + ".ITEMS_LIST_MODE";
	
	/**
	 * Integer used to host the item list mode.
	 * @see #REGULAR_ITEMS_LIST
	 * @see #ASSETS_LIST
	 */
	private int itemsListMode;
	
	/**
	 * Constant integer holding the items list mode used to display the regular items list.
	 */
	public static final int REGULAR_ITEMS_LIST = 1;
	
	/**
	 * Constant integer holding the items list mode used to display the assets list.
	 */
	public static final int ASSETS_LIST = 2;
	
	/**
	 * Bundle key used to put/retrieve the content of the transaction header code.<br>
	 * This is used mainly to edit a client stock count.
	 */
	public static final String CLIENT_STOCK_HEADER_CODE = ClientStockDetailsActivity.class.getName () + ".CLIENT_STOCK_HEADER_CODE";
	
	/**
	 * Bundle key used to put/retrieve the content of the current call.
	 */
	public static final String CALL = ClientStockDetailsActivity.class.getName () + ".CALL";
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #visit}.
	 */
	public static final String VISIT = ClientStockDetailsActivity.class.getName () + ".VISIT";
	
	/**
	 * List of {@link me.SyncWise.Android.Database.Divisions Divisions} objects used to host all the available objects for the current transaction.
	 */
	protected ArrayList < Divisions > divisions;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #SELECTED_DIVISIONS}.
	 */
	public static final String SELECTED_DIVISIONS = ClientStockDetailsActivity.class.getName () + ".SELECTED_DIVISIONS";
	
	/**
	 * List of string used to host the selected divisions codes, in client stock count to filter the items / client stock count list.
	 */
	protected ArrayList < String > selectedDivisionsCodes;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #stocks}.
	 */
	public static final String STOCK = ClientStockDetailsActivity.class.getName () + ".STOCK";
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.ClientStock.Stock Stock} objects used to define the client stock count.<br>
	 * <b>NOTE :</b> <br>
	 * If a filter is applied, this list remains untouched. A new list is generated, filtered and passed to the list adapter.
	 */
	protected ArrayList < Stock > stocks;
	
	/**
	 * Boolean used to indicate if there saved client stock counts that should be retrieved.
	 */
	protected boolean retrieveStocks;
	
	/**
	 * Reference to the client stock counts list population task.
	 */
	protected static PopulateList populateList;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #searchQuery}.
	 */
	public static final String SEARCH_QUERY = ClientStockDetailsActivity.class.getName () + ".SEARCH_QUERY";
	
	/**
	 * String holding the search query.
	 */
	private String searchQuery;
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.ClientStock.Stock Stock} objects used to define the selected stocks .
	 */
	protected ArrayList < Stock > selectedStocks;
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.ClientStock.StockInfo StockInfo} objects used to define the client stock count details.
	 */
	protected ArrayList < StockInfo > details;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #note}.
	 */
	public static final String NOTE = ClientStockDetailsActivity.class.getName () + ".NOTE";
	
	/**
	 * String holding the client stock count note.
	 */
	protected String note;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #expiryDate}.
	 */
	public static final String EXPIRY_DATE = ClientStockDetailsActivity.class.getName () + ".EXPIRY_DATE";
	
	/**
	 * Date object used to host the expiry date during a stock modification.
	 */
	private Date expiryDate;
	
	/**
	 * Bundle key used to put/retrieve the content of the count type.
	 */
	public static final String COUNT_TYPE = ClientStockDetailsActivity.class.getName () + ".COUNT_TYPE";
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #currency}.
	 */
	public static final String CURRENCY = ClientStockDetailsActivity.class.getName () + ".CURRENCY";
	
	/**
	 * Reference to the currency object.
	 */
	protected Currencies currency;
	
	/**
	 * List of the client stock statuses.
	 */
	private ArrayList < AssetsStatus > statuses;
	
	/**
	 * List of the client stock existence.
	 */
	private ArrayList < AssetsStatus > existence;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #clientStockHeader}.
	 */
	public static final String CLIENTSTOCK_HEADER = ClientStockDetailsActivity.class.getName () + ".CLIENTSTOCK_HEADER";
	
	/**
	 * Reference to the client stock header stored in DB (if previously saved)
	 */
	protected ClientStockCountHeaders clientStockHeader;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #clientStockDetails}.
	 */
	public static final String CLIENTSTOCK_DETAILS = ClientStockDetailsActivity.class.getName () + ".CLIENTSTOCK_DETAILS";
	
	/**
	 * List of client Stock details references stored in DB (if previously saved)
	 */
	protected ArrayList < ClientStockCountDetails > clientStockDetails;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #clientAssets}.
	 */
	public static final String CLIENT_ASSETS = ClientStockDetailsActivity.class.getName () + ".CLIENT_ASSETS";
	
	/**
	 * List of client assets references stored in DB (if previously saved)
	 */
	protected ArrayList < ClientAssetStatus > clientAssets;
	
	/**
	 * Reference to a {@link me.SyncWise.Android.Widgets.QuickAction.QuickAction QuickAction} used in this activity.
	 */
	private QuickAction quickAction;
	
	public static String shelfPrice_small ;
	public static String shelfPrice_medium ;
	public static Double  _shelfPrice_small = 0d;
	public static Double  _shelfPrice_medium = 0d;
	
	/**
	 * Helper Class used to manage the activity's action items IDs for the quick action widgets.<br>
	 * The main purpose of this class is to maintain a unique identifier for action items within the activity.
	 * 
	 * @author Elias
	 *
	 */
	protected class ActionItemID {
		public static final int EDIT = 0;
		public static final int REMOVE = 1;
	}
	
	/**
	 * Performs all necessary setup in order to properly display the quick action widget.
	 */
    protected void setupQuickAction () {
		// Initialize the quick action widgets
    	quickAction = new QuickAction ( this , QuickAction.VERTICAL );
		// Action Item : Edit
		ActionItem edit = new ActionItem ( ActionItemID.EDIT ,
				AppResources.getInstance ( this ).getString ( this , R.string.quick_action_sales_order_edit_label ) ,
				getResources ().getDrawable ( R.drawable.quick_action_edit ) );
		// Action Item : Remove
		ActionItem remove = new ActionItem ( ActionItemID.REMOVE ,
				AppResources.getInstance ( this ).getString ( this , R.string.quick_action_sales_order_remove_label ) ,
				getResources ().getDrawable ( R.drawable.quick_action_cancel ) );
    	// Populate the quick action widget with quick action items
		quickAction.addActionItem ( edit );
		quickAction.addActionItem ( remove );
		// Assign an action item click listener
		quickAction.setOnActionItemClickListener ( this );
    }
	
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
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.client_stock_activity_title ) );
		// Set the activity content from a layout resource.
		setContentView ( R.layout.client_stock_count_details_activity );
		// Enable fast scrolling
		getListView ().setFastScrollEnabled ( true );
		// Define the empty list view
		getListView ().setEmptyView ( findViewById ( R.id.empty_list_view ) );
		// Display empty list label
		( (TextView) findViewById ( R.id.empty_list_view ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_items_list_label ) );
		
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

        // Perform the quick action setup
        setupQuickAction ();
		
		// Initialize the client stock count note
		note = "";
		
		// Retrieve the request code
		requestCode = getIntent ().getIntExtra ( REQUEST_CODE , -1 );
		// Check if the edit flag is set
		// AND if the request code is INFO
		if ( requestCode == REQUEST_CODE_INFO && getIntent ().getBooleanExtra ( IS_EDIT , false ) )
			// Change the request code to new
			requestCode = REQUEST_CODE_NEW;
		
		// Load the items list mode (if available)
		itemsListMode = getIntent ().getIntExtra ( ITEMS_LIST_MODE , 0 );
		
		// Initialize the items list mode
		if ( itemsListMode == 0 )
			// Assign the priority list mode as default
			itemsListMode = REGULAR_ITEMS_LIST;
		
		// Display the applied list mode in the action bar
		displayItemsListMode ();
		
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state 
			searchQuery = savedInstanceState.getString ( SEARCH_QUERY );
			selectedDivisionsCodes = savedInstanceState.getStringArrayList ( SELECTED_DIVISIONS );
			retrieveStocks = savedInstanceState.getBoolean ( STOCK );
			requestCode = savedInstanceState.getInt ( REQUEST_CODE );
			note = savedInstanceState.getString ( NOTE );
			expiryDate = (Date) savedInstanceState.getSerializable ( EXPIRY_DATE );
			currency = (Currencies) savedInstanceState.getSerializable ( CURRENCY );
			clientStockHeader = (ClientStockCountHeaders) savedInstanceState.getSerializable ( CLIENTSTOCK_HEADER );
			clientStockDetails = (ArrayList < ClientStockCountDetails >) savedInstanceState.getSerializable ( CLIENTSTOCK_DETAILS );
			clientAssets = (ArrayList < ClientAssetStatus >) savedInstanceState.getSerializable ( CLIENT_ASSETS );
			displayStockDetails = savedInstanceState.getBoolean ( DISPLAY_STOCK_DETAILS );
			displayStockNote = savedInstanceState.getBoolean ( DISPLAY_STOCK_NOTE );
			itemsListMode = savedInstanceState.getInt ( ITEMS_LIST_MODE );
			stockModification = (Stock) savedInstanceState.getSerializable ( STOCK_MODIFICATION );
		} // End if
		
		// Check if the request code is valid
		switch ( requestCode ) {
		case REQUEST_CODE_INFO:
		case REQUEST_CODE_NEW:
			// Valid request code, do nothing
			break;
		default:
			// Invalid request code
			// Indicate that the activity cannot be displayed
			new AppToast ( this ).setIcon ( R.drawable.warning ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.invalid_selection_label ) ).show ();
			// Finish activity
			finish ();
			// Exit method
			return;
		} // End switch
	
		
				 
		   

			

		// Hide the client stock count detail layout
		findViewById ( R.id.layout_stock_details ).setVisibility ( View.GONE );
		// Hide the client stock count note layout
		findViewById ( R.id.layout_stock_note ).setVisibility ( View.GONE );
		// Button click listener for the date picker button
		findViewById ( R.id.button_date_picker ).setOnClickListener ( new View.OnClickListener () {
			@Override
			public void onClick ( View view ) {
				// Declare and initialize a default date for the date picker
				final Calendar defaultDate = Calendar.getInstance ();
				// Check if the expiry date is valid
				if ( expiryDate != null )
					// Set the expiry date as default
					defaultDate.setTime ( expiryDate );
	    		// Display a date picker dialog
				AppDialog.getInstance ().displayDatePicker ( ClientStockDetailsActivity.this , defaultDate.get ( Calendar.YEAR ) , defaultDate.get ( Calendar.MONTH ) , defaultDate.get ( Calendar.DAY_OF_MONTH ) , null , false , true , ClientStockDetailsActivity.this , ClientStockDetailsActivity.this );
			}
		} );
		
		// Retrieve all the client stock counts asynchronously
		populate ();
	}
	
	/**
	 * Retrieve all the client stock counts asynchronously.
	 */
	protected void populate () {
		// Retrieve all the client stock counts asynchronously
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
	
	/**
	 * Displays the currently applied list mode.
	 */
	private void displayItemsListMode () {
		// Check if the screen info is displayed
		if ( requestCode == REQUEST_CODE_INFO )
			// Clear the sub title bar
			getActionBar ().setSubtitle ( "" );
		// Determine the list mode
		else if ( itemsListMode == REGULAR_ITEMS_LIST )
			// Indicate that the priority items list mode is applied
			getActionBar ().setSubtitle ( R.string.client_stock_count_regular_items_label );
		else if ( itemsListMode == ASSETS_LIST )
			// Indicate that the main items list mode is applied
			getActionBar ().setSubtitle ( R.string.client_stock_count_assets_items_label );			
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
    	// Save the content of searchQuery in the outState bundle
    	outState.putString ( SEARCH_QUERY , searchQuery );
    	// Save the content of selectedDivisions in the outState bundle
    	outState.putStringArrayList ( SELECTED_DIVISIONS , selectedDivisionsCodes );

		// Save the content of the client stock counts using GSON
		ActivityInstance.saveDataGson ( this , ClientStockDetailsActivity.class.getName () , STOCK , stocks );
		// Indicate that there is saved client stock counts data
		outState.putBoolean ( STOCK , true );
    	
    	// Save the content of requestCode in the outState bundle
    	outState.putInt ( REQUEST_CODE , requestCode );
    	// Save the content of note in the outState bundle
    	outState.putString ( NOTE , note );
    	// Check if the expiry date is valid
    	if ( expiryDate != null )
	    	// Save the content of expiryDate in the outState bundle
	    	outState.putSerializable ( EXPIRY_DATE , expiryDate );
    	// Save the content of currency in the outState bundle
    	outState.putSerializable ( CURRENCY , currency );
    	// Save the content of transactionHeader in the outState bundle
    	outState.putSerializable ( CLIENTSTOCK_HEADER , clientStockHeader );
    	// Save the content of transactionDetails in the outState bundle
    	outState.putSerializable ( CLIENTSTOCK_DETAILS , clientStockDetails );
    	// Save the content of clientAssets in the outState bundle
    	outState.putSerializable ( CLIENT_ASSETS , clientAssets );
    	// Save the content of displayStockDetails in the outState bundle
    	outState.putBoolean ( DISPLAY_STOCK_DETAILS , displayStockDetails );
    	// Save the content of stockModification in the outState bundle
    	outState.putSerializable ( STOCK_MODIFICATION , stockModification );
    	// Save the content of displayStockNote in the outState bundle
    	outState.putBoolean ( DISPLAY_STOCK_NOTE , displayStockNote );
    	// Save the content of itemsListMode in the outState bundle
    	outState.putInt ( ITEMS_LIST_MODE , itemsListMode );
    }
    
	/*
	 * Called when the activity has detected the user's press of the back key. 
	 *
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed () {
		// Determine if an client stock count is undergoing any modifications
		if ( displayStockDetails ) {
			// Reset flag
			displayStockDetails = false;
			// Retrieve a reference to the secondary view
			View secondary = findViewById ( R.id.layout_stock_details );
			// Hide the secondary view
			secondary.startAnimation ( getViewAnimationOut ( secondary ) );
    		// Enable the main list
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( true );
    		// Refresh the action bar
    		invalidateOptionsMenu ();
    		// Exit method
    		return;
		} // End if
		// Determine if the client stock count note is undergoing any modifications
		else if ( displayStockNote ) {
			// Reset flag
			displayStockNote = false;
			// Retrieve a reference to the tertiary view
			View tertiary = findViewById ( R.id.layout_stock_note );
			// Hide the tertiary view
			tertiary.startAnimation ( getViewAnimationOut ( tertiary ) );
    		// Enable the main list
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( true );
    		// Refresh the action bar
    		invalidateOptionsMenu ();
    		// Exit method
    		return;
		} // End else if
		// Determine if the request code is INFO
		else if ( requestCode == REQUEST_CODE_INFO ) {
    		// The request code is INFO
    		// Check if the view only flag is set
    		if ( getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) )
    			// Superclass method call
    			super.onBackPressed ();
    		else {
	    		// Set the new request code
	    		requestCode = REQUEST_CODE_NEW;
	    		// Refresh the action bar
	    		invalidateOptionsMenu ();
	    		// Refresh the action bar
	    		displayItemsListMode ();
				// Set a new (empty) adapter
				setListAdapter ( getClientStockDetailsAdapter ( R.layout.sales_order_details_activity_item , new ArrayList < Stock > () , currency.getCurrencyRounding () , true,true ) );
				// Set a new adapter based on the saved filter, asynchronously
				new FilterList ( searchQuery ).execute ();
    		} // End else
    	} // End else if
		// Determine if this is a new client stock count with selected client stock counts
		// OR a previously saved client stock count with modifications
    	else if ( ( getIntent ().getIntExtra ( REQUEST_CODE , -1 ) == REQUEST_CODE_NEW && hasClientStock () )
				|| getIntent ().getIntExtra ( REQUEST_CODE , -1 ) != REQUEST_CODE_NEW && hasModifications () )
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
								ClientStockDetailsActivity.this.finish ();
								break;
							case DialogInterface.BUTTON_NEGATIVE:
								// Dismiss dialog
								AppDialog.getInstance ().dismiss ();
								break;
							} // End switch
						}
					} );
		else {
			// Check if the current transaction was an edit
			if ( clientStockHeader == null ) {
				try {
					// Retrieve the user object
					Users user = DatabaseUtils.getInstance ( this ).getDaoSession ().getUsersDao ().queryBuilder ()
							.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( this ) ) ,
									UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( this ) ) ).unique ();
					// Declare and initialize a sequence formatter
					DecimalFormat sequence = new DecimalFormat ( "000000" );
					// Retrieve the client stock count sequence
					int clientStockCountSequence = DatabaseUtils.getUserSequence ( this , user.getUserCode () , user.getCompanyCode () , TransactionSequencesDao.Properties.ClientStockCount );
					// Compute the transaction header code
					String clientstockHeaderCode = "1" + String.valueOf ( user.getPrefixID () ) + sequence.format ( clientStockCountSequence );
					// Open the private documents folder
					File folder = getDir ( ClientAssetsPicturesUtils.FOLDER , MODE_PRIVATE );
					File tempFolder = getDir ( ClientAssetsPicturesUtils.FOLDER_TEMP , MODE_PRIVATE );
					// Retrieve the asset pictures of the current transaction
					List < ClientAssetsPictures > documents = DatabaseUtils.getInstance ( this ).getDaoSession ().getClientAssetsPicturesDao ().queryBuilder ()
							.where ( ClientAssetsPicturesDao.Properties.TransactionCode.eq ( clientstockHeaderCode ) ).list ();
					// Iterate over the pictures
					for ( ClientAssetsPictures document : documents ) {
						// Get access to the picture
						File file = new File ( folder , document.getTransactionCode () + "--" + document.getItemCode () + "--" + document.getLineID () + ".png" );
						// Delete the file
						file.delete ();
						// Get access to the temporary picture
						file = new File ( tempFolder , document.getTransactionCode () + "--" + document.getItemCode () + "--" + document.getLineID () + ".png" );
						// Delete the file
						file.delete ();
					} // End for each
				} catch ( Exception exception ) {
					// Do nothing
				} // End of try-catch block
			} // End if
			// Superclass method call
			super.onBackPressed ();
		} // End else
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
			stockModification = null;
			searchQuery = null;
			stocks = null;
			selectedStocks = null;
			itemsListMode = 0;
			note = null;
			details = null;
			clientStockHeader = null;
			clientStockDetails = null;
			clientAssets = null;
			currency = null;
			expiryDate = null;
			quickAction = null;
			searchView = null;
		} // End if
	}
    
    /*
     * This method will be called when an item in the list is selected.
     *
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
	@Override
    protected void onListItemClick ( ListView listView , View view , final int position , long id ) {
		// Check if there is any ongoing client stock count / note modifications
		if ( displayStockDetails || displayStockNote )
			// Do nothing
			return;
		
		// Retrieve the selected client stock count
		stockModification = (Stock) ( (BaseAdapter) getListAdapter () ).getItem ( position );

    	// Determine if the request code is NOT info
    	if ( requestCode != REQUEST_CODE_INFO ) {
    		// Check if the client stock count belongs to the must stock list and is not confirmed
    		if ( ItemsUtils.isRegular ( stockModification.getItem () ) && stockModification.isMustStockList () && ! stockModification.isConfirmed () ) {
    			// Confirm the current client stock count
    			stockModification.setConfirmed ( true );
    			// Refresh the list
    			( (ClientStockDetailsAdapter) getListAdapter () ).notifyDataSetChanged ();
    		} // End if
    		else {
	    		// Set flag
	    		displayStockDetails = true;
	    		// Initialize the secondary view
	    		initializeSecondaryView ( false );
	    		// Disable the main list
	    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( false );
	    		// Retrieve a reference to the secondary view
	    		View secondaryView = findViewById ( R.id.layout_stock_details );
	    		// Display the secondary view
	    		secondaryView.setVisibility ( View.VISIBLE );
	    		// Animate the secondary view
	    		secondaryView.startAnimation ( getViewAnimationIn() );
	    		// Hide the software keyboard
	            ( (InputMethodManager) getBaseContext ().getSystemService ( INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow ( secondaryView.getWindowToken (), 0 );
	    		// Refresh the action bar
	    		invalidateOptionsMenu ();
    		} // End else
    	} // End if
		// Determine if the request code is info
    	// AND if the view only flag is provided
    	else if ( requestCode == REQUEST_CODE_INFO
    			&& ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) )
    		// Display the quick action widget
    		quickAction.show ( view , stockModification , getResources () );
	}
	
	/*
	 * Callback method to be invoked when an action item in this QuickAction has been clicked.
	 *
	 * @see me.SyncWise.Android.Widgets.QuickAction.QuickAction.OnActionItemClickListener#onItemClick(me.SyncWise.Android.Widgets.QuickAction.QuickAction, int, int)
	 */
	@Override
	public void onItemClick ( QuickAction source , View anchor , Object object , int pos , int actionId ) {
		// Retrieve the selected client stock count
		Stock selectedStock = (Stock) object;
		
		// Determine the clicked action item
		switch ( actionId ) {
		case ActionItemID.EDIT:
			// Retrieve the selected client stock count
			stockModification = selectedStock;
    		// Set flag
    		displayStockDetails = true;
    		// Initialize the secondary view
    		initializeSecondaryView ( false );
    		// Disable the main list
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( false );
    		// Retrieve a reference to the secondary view
    		View secondaryView = findViewById ( R.id.layout_stock_details );
    		// Display the secondary view
    		secondaryView.setVisibility ( View.VISIBLE );
    		// Animate the secondary view
    		secondaryView.startAnimation ( getViewAnimationIn() );
    		// Hide the software keyboard
            ( (InputMethodManager) getBaseContext ().getSystemService ( INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow ( secondaryView.getWindowToken (), 0 );
    		// Refresh the action bar
    		invalidateOptionsMenu ();
			break;
		case ActionItemID.REMOVE:
			// Clear quantities
			selectedStock.setQuantityBig ( 0 );
			selectedStock.setQuantityMedium ( 0 );
			selectedStock.setQuantitySmall ( 0 );
			selectedStock.setExpiryDate ( null );
			selectedStock.setMerchandize ( false );
			selectedStock.setAvailables(false);
			selectedStock.setExistence ( null );
			selectedStock.setStatus ( null );
			// Remove item from the selected client stock counts (if valid)
			if ( selectedStocks != null )
				selectedStocks.remove ( selectedStock );
			// Refresh client stock count details
			refreshDetails ();
			// Check if the request code is INFO
			// AND if the selected client stock counts list is empty
			if ( requestCode == REQUEST_CODE_INFO && selectedStocks != null && selectedStocks.isEmpty () )
				// Emulate a back key press
				onBackPressed ();
			else
				// Refresh the list
				( (MultipleListAdapter) getListAdapter () ).notifyDataSetChanged ();
			break;
		} // End of switch
	}
	
	/*
	 * Handler called when a date is set from the date picker.
	 *
	 * @see android.app.DatePickerDialog.OnDateSetListener#onDateSet(android.widget.DatePicker, int, int, int)
	 */
	@Override
	public void onDateSet ( DatePicker view , int year , int monthOfYear , int dayOfMonth ) {
		// Retrieve the selected date
		Calendar date = Calendar.getInstance ();
		date.set ( year , monthOfYear , dayOfMonth , 0 , 0 , 0 );
		date.set ( Calendar.MILLISECOND , 0 );

		// Update the expiry date
		expiryDate = date.getTime ();
		// Display the expiry date
		( (TextView) findViewById ( R.id.label_date_picker ) ).setText ( getFormattedExpiryDate ( expiryDate ) );
	}
	
	/*
	 * This method will be invoked when a button in the dialog is clicked.
	 *
	 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
	 */
	@Override
	public void onClick ( DialogInterface dialog , int which ) {
		// Clear the expiry date
		expiryDate = null;
		// Display the expiry date
		( (TextView) findViewById ( R.id.label_date_picker ) ).setText ( getFormattedExpiryDate ( expiryDate ) );
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
	
	/**
	 * Returns the provide expiry date in a formatted manner ready for display.
	 * 
	 * @param date	The expiry date to display.
	 * @return	String hosting the formatted expiry date.
	 */
	private String getFormattedExpiryDate ( final Date date ) {
		// Retrieve a reference to the expiry date label
		String expiryDateLabel = AppResources.getInstance ( this ).getString ( this , R.string.expiry_date_label ) + " : ";
		// Check if the expiry date is valid
		if ( date != null )
			// Format and return the expiry date
			return expiryDateLabel + DateTime.getBriefDate ( this , date );
		else
			// Indicate that the expiry date is not available
			return expiryDateLabel + AppResources.getInstance ( this ).getString ( this , R.string.not_available_abbreviation );
	} 
	
	/**
	 * Initializes the client stock count modification (secondary) view.
	 * 
	 * @param restore	Boolean used to determine if the secondary view is being initialized for the first time or is being restored.
	 */
	protected void initializeSecondaryView ( final boolean restore ) {
		// Retrieve a reference to the parent view
		View parent = findViewById ( R.id.layout_stock_details );
		// Retrieve a reference to the client stock count quantity layout
		View quantityLayout = parent.findViewById ( R.id.layout_stock_quantity );
		// Retrieve a reference to the client stock count data layout
		View dataLayout = parent.findViewById ( R.id.layout_stock_data );
		// Retrieve a reference to the client stock count data layout
		View assetLayout = parent.findViewById ( R.id.layout_stock_asset );
		// Retrieve a reference to the item name label
		TextView itemNameLabel = (TextView) parent.findViewById ( R.id.label_item_name );
		// Retrieve a reference to the save icon
		ImageView saveIcon = (ImageView) parent.findViewById ( R.id.icon_save_stock );
		// Retrieve a reference to the picture icon
		ImageView pictureIcon = (ImageView) parent.findViewById ( R.id.icon_picture_stock );
		// Retrieve a reference to the client stock count quantity label
		TextView quantityLabel = (TextView) parent.findViewById ( R.id.label_stock_quantity );
		// Retrieve a reference to the client stock count data label
		TextView dataLabel = (TextView) parent.findViewById ( R.id.label_stock_data );
		// Retrieve a reference to the client stock count data label
		TextView assetLabel = (TextView) parent.findViewById ( R.id.label_stock_asset );
		
		// Retrieve a reference to the client stock small shelf price data label
		EditText shelf_price_small =  (EditText) parent.findViewById(R.id.small_shelf_price);
		EditText shelf_price_medium = (EditText) parent.findViewById(R.id.medium_shelf_price);
		EditText shelf_price_big = (EditText) parent.findViewById(R.id.big_shelf_price);

		shelf_price_small.setVisibility(View.VISIBLE);
		shelf_price_medium.setVisibility(View.VISIBLE);
		shelf_price_big.setVisibility(View.INVISIBLE);
		
		shelfPrice_small = shelf_price_small.getText ().toString () ;
		shelfPrice_medium = shelf_price_medium.getText ().toString () ;
		
		if (shelfPrice_small != null && ! shelfPrice_small.trim().equals("") )
			_shelfPrice_small = Double.parseDouble (shelfPrice_small) ;
		if (shelfPrice_medium != null && ! shelfPrice_medium.trim().equals("") )
			_shelfPrice_medium = Double.parseDouble (shelfPrice_medium) ;
		
		// Retrieve a reference to the quantity UOMs layouts
		LinearLayout quantityBigLayout = (LinearLayout) quantityLayout.findViewById ( R.id.layout_big_uom );
		LinearLayout quantityMediumLayout = (LinearLayout) quantityLayout.findViewById ( R.id.layout_medium_uom );
		// Retrieve references to the quantity number pickers
		NumberPicker quantityBig = (NumberPicker) quantityLayout.findViewById ( R.id.number_picker_quantity_big );
		NumberPicker quantityMedium = (NumberPicker) quantityLayout.findViewById ( R.id.number_picker_quantity_medium );
		NumberPicker quantitySmall = (NumberPicker) quantityLayout.findViewById ( R.id.number_picker_quantity_small );
		//quantitySmall.setDecimal(true);//.setSigned( true );
		// Retrieve references to the quantity descriptive labels
		TextView quantityBigLabel_quantity = (TextView) quantityLayout.findViewById ( R.id.label_quantity_big );
		TextView quantityMediumLabel_quantity = (TextView) quantityLayout.findViewById ( R.id.label_quantity_medium );
		TextView quantitySmallLabel_quantity = (TextView) quantityLayout.findViewById ( R.id.label_quantity_small );
		// Retrieve references to the prices labels for quantity
		TextView priceBigLabel_quantity = (TextView) quantityLayout.findViewById ( R.id.label_price_big );
		TextView priceMediumLabel_quantity = (TextView) quantityLayout.findViewById ( R.id.label_price_medium );
		TextView priceSmallLabel_quantity = (TextView) quantityLayout.findViewById ( R.id.label_price_small );
		// Retrieve a reference to the expiry date label
		TextView expiryDateLabel = (TextView) dataLayout.findViewById ( R.id.label_date_picker );
		// Retrieve a reference to the merchandize check box
		CheckBox merchandizeCheckBox = (CheckBox) dataLayout.findViewById ( R.id.checkbox_merchandize );
		// Retrieve a reference to the over six months check box
		CheckBox isOverSixMonthsCheckBox = (CheckBox) dataLayout.findViewById ( R.id.checkbox_over_six_months );
		CheckBox availableCheckBox = (CheckBox) dataLayout.findViewById ( R.id.available );
		// Display the item name
		itemNameLabel.setText ( stockModification.getItem ().getItemName () );
		// Enable the save button
		saveIcon.setEnabled ( true );
		// Enable the picture button
		pictureIcon.setEnabled ( true );
		// Display the quantity label
		quantityLabel.setText ( AppResources.getInstance ( this ).getString ( this , R.string.quantity_label ) );
		// Assign on focus listeners to the number pickers
		quantityBig.getEditText ().setOnFocusChangeListener ( this );
		quantityMedium.getEditText ().setOnFocusChangeListener ( this );
		quantitySmall.getEditText ().setOnFocusChangeListener ( this );
		// Display the data label
		dataLabel.setText ( AppResources.getInstance ( this ).getString ( this , R.string.client_stock_count_data_label ) );
		// Display the asset label
		assetLabel.setText ( AppResources.getInstance ( this ).getString ( this , R.string.client_stock_count_asset_label ) );
		( (TextView) assetLayout.findViewById ( R.id.label_asset_status ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.status_label ) );
		( (TextView) assetLayout.findViewById ( R.id.label_asset_existence ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.existence_label ) );
		// Display the merchandize label
		merchandizeCheckBox.setText ( AppResources.getInstance ( this ).getString ( this , R.string.client_stock_count_is_merchandize_label ) );
		// Display the over six months label
		isOverSixMonthsCheckBox.setText ( AppResources.getInstance ( this ).getString ( this , R.string.client_stock_count_is_over_six_months_label ) );
		// Display the available label
		availableCheckBox.setText ( AppResources.getInstance ( this ).getString ( this , R.string.client_stock_count_is_available_label ) );
		 
		quantityMedium.setEnabled ( true ) ;
		quantitySmall.setEnabled ( true ) ;
		// Compute the money formatter pattern
		//availableCheckBox.setOnClickListener(this);
		findViewById ( R.id.available ).setOnClickListener (new View.OnClickListener () {
			   @Override
					  public void onClick(View v) {
				   View parent = findViewById ( R.id.layout_stock_details );
				   View quantityLayout = parent.findViewById ( R.id.layout_stock_quantity );
				  // NumberPicker quantityBig = (NumberPicker) quantityLayout.findViewById ( R.id.number_picker_quantity_big );
					NumberPicker quantityMedium = (NumberPicker) quantityLayout.findViewById ( R.id.number_picker_quantity_medium );
					NumberPicker quantitySmall = (NumberPicker) quantityLayout.findViewById ( R.id.number_picker_quantity_small );
					
				                //is chkIos checked?
						if (((CheckBox) v).isChecked()) {
							quantityMedium.setNumber(0d);
							quantityMedium.setEnabled(false);
							quantitySmall.setEnabled(false);
							quantitySmall.setNumber( 0d );
						}
						else{
							quantityMedium.setEnabled ( true ) ;
							quantitySmall.setEnabled ( true ) ;
						} 
					 

					  }
					});
				
		
		StringBuilder pattern = new StringBuilder ();
		pattern.append ( " #,##0" );
		// Check if the currency has a rounding value
		if ( currency.getCurrencyRounding () >= 1 ) {
			pattern.append ( ".0" );
			for ( int i = 1 ; i < currency.getCurrencyRounding () ; i ++ )
				pattern.append ( "0" );
		} // End if
		// Declare and initialize a money format
		DecimalFormat moneyFormat = new DecimalFormat ( pattern.toString () );
	
		// Check if the item is regular
		if ( ItemsUtils.isRegular ( stockModification.getItem () ) ) {
			// Hide asset layout
			assetLayout.setVisibility ( View.GONE );
			// Display quantity and data layouts
			quantityLayout.setVisibility ( View.VISIBLE );
			dataLayout.setVisibility ( View.VISIBLE );
			// Hide picture icon
			pictureIcon.setVisibility ( View.GONE );
			
			// Display labels descriptions
			quantityBigLabel_quantity.setText ( stockModification.getUnit ().getUnitBigDescription () );
			quantityMediumLabel_quantity.setText ( stockModification.getUnit ().getUnitMediumDescription () );
			quantitySmallLabel_quantity.setText ( stockModification.getUnit ().getUnitSmallDescription () );
			
			
			shelf_price_small.setText( String.valueOf( stockModification.getShelfPriceSmall() ) );
			shelf_price_medium.setText( String.valueOf( stockModification.getShelfPriceMedium() ) );
			
			// Dim the appropriate unit of measurements labels
			quantityBigLayout.setAlpha ( ItemsUtils.isBig ( stockModification.getItem () ) ? 1f : 0.25f );
			quantityMediumLayout.setAlpha ( ItemsUtils.isMedium ( stockModification.getItem () ) ? 1f : 0.25f );
		
		} // End if
		// Check if the item is asset
		else if ( ItemsUtils.isAsset ( stockModification.getItem () ) ) {
			// Display picture icon
			pictureIcon.setVisibility ( View.VISIBLE );
			// Display asset layout
			assetLayout.setVisibility ( View.VISIBLE );
			//  quantity and data layouts
			quantityLayout.setVisibility ( View.GONE );
			dataLayout.setVisibility ( View.GONE );
		} // End else if
		
		// Check if the view is being restored
		if ( ! restore ) {
			// Set the current client stock count quantities
			quantityBig.setNumber ( stockModification.getQuantityBig () );
			quantityMedium.setNumber ( stockModification.getQuantityMedium () );
			quantitySmall.setNumber ( stockModification.getQuantitySmall () );
			
			shelf_price_small.setText( String.valueOf( stockModification.getShelfPriceSmall() ) );
			shelf_price_medium.setText( String.valueOf( stockModification.getShelfPriceMedium() ) );
			
			// Disable the appropriate number pickers
			quantityBig.setEnabled ( ItemsUtils.isBig ( stockModification.getItem () ) );
			quantityMedium.setEnabled ( ItemsUtils.isMedium ( stockModification.getItem () ) );
			// Set expiry date
			expiryDate = stockModification.getExpiryDate ();
			// Display the merchandize flag
			merchandizeCheckBox.setChecked ( stockModification.isMerchandize () );
			// Display the over six months flag
			isOverSixMonthsCheckBox.setChecked ( stockModification.isOverSixMonths () );
			// Display the available flag
			availableCheckBox.setChecked( stockModification.isAvailables() );
			// Track the selected index
			int index = 0;
			// Retrieve a reference to the existence spinner
			Spinner spinner = (Spinner) assetLayout.findViewById ( R.id.spinner_existence );
			// Select the appropriate existence
			if ( stockModification.getExistence () != null ) {
				// Iterate over all existences
				for ( int i = 0 ; i < spinner.getAdapter ().getCount () ; i ++ )
					// Match status IDs
					if ( ( (AssetsStatus) spinner.getAdapter ().getItem ( i ) ).getStatusID () == stockModification.getExistence ().getStatusID () ) {
						// Set the current index
						index = i;
						// Exit loop
						break;
					} // End if
			} // End if
			// Set the current existence
			spinner.setSelection ( index );
			// Display / hide the asset features accordingly
			setAssetFeaturesVisibility ( stockModification.getExistence () != null && stockModification.getExistence ().getStatusID () == 1 ? true : false );
			// Assign a listener to the existence spinner
			spinner.setOnItemSelectedListener ( this );
			// Track the status selected index
			index = 0;
			// Retrieve a reference to the statuses spinner
			spinner = (Spinner) assetLayout.findViewById ( R.id.spinner_status );
			// Select the appropriate status
			if ( stockModification.getStatus () != null ) {
				// Iterate over all statuses
				for ( int i = 0 ; i < spinner.getAdapter ().getCount () ; i ++ )
					// Match status ID
					if ( ( (AssetsStatus) spinner.getAdapter ().getItem ( i ) ).getStatusID () == stockModification.getStatus ().getStatusID () ) {
						// Set the current index
						index = i;
						// Exit loop
						break;
					} // End if
			} // End if
			// Set the current status
			spinner.setSelection ( index );
		} // End if
		
		// Display the expiry date label
		expiryDateLabel.setText ( getFormattedExpiryDate ( expiryDate ) );
		// Display labels prices
		priceBigLabel_quantity.setText ( moneyFormat.format ( stockModification.getPriceBig () ) );
		priceMediumLabel_quantity.setText ( moneyFormat.format ( stockModification.getPriceMedium () ) );
		priceSmallLabel_quantity.setText ( moneyFormat.format ( stockModification.getPriceSmall () ) );
	}
	
	/*
	 * Callback method to be invoked when an item in this view has been selected.
	 *
	 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemSelected ( AdapterView < ? > parent , View view , int position , long id ) {
		// Display / hide the asset features accordingly
		setAssetFeaturesVisibility ( ( (AssetsStatus) parent.getSelectedItem () ).getStatusID () == 1 );
	}
	
	/*
	 * Callback method to be invoked when the selection disappears from this view.
	 *
	 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android.widget.AdapterView)
	 */
	@Override
	public void onNothingSelected ( AdapterView < ? > parent ) {
		// Do nothing
	}
	
	/**
	 * Displays / Hides the asset features according to the provided flag.<br>
	 * The asset features include the picture icon and status spinner.
	 * 
	 * @param isVisible	Boolean indicating the asset features visibility.
	 */
	private void setAssetFeaturesVisibility ( final boolean isVisible ) {
		// Retrieve a reference to the parent view
		View parent = findViewById ( R.id.layout_stock_details );
		// Retrieve a reference to the client stock count data layout
		View assetLayout = parent.findViewById ( R.id.layout_stock_asset );
		// Retrieve a reference to the picture icon
		ImageView pictureIcon = (ImageView) parent.findViewById ( R.id.icon_picture_stock );
		// Retrieve a reference to the statuses spinner
		Spinner spinnerStatus = (Spinner) assetLayout.findViewById ( R.id.spinner_status );
		// Display / hide the picture icon accordingly
		pictureIcon.setVisibility ( isVisible ? View.VISIBLE : View.GONE );
		// Display / hide the status spinner accordingly
		spinnerStatus.setVisibility ( isVisible ? View.VISIBLE : View.GONE );
		// Display / hide the status label accordingly
		assetLayout.findViewById ( R.id.label_asset_status ).setVisibility ( isVisible ? View.VISIBLE : View.GONE );
	}
	
	/**
	 * Initializes the client stock count note (tertiary) view.
	 * 
	 * @param restore	Boolean used to determine if the tertiary view is being initialized for the first time or is being restored.
	 */
	protected void initializeTertiaryView ( final boolean restore ) {
		// Retrieve a reference to the parent view
		View parent = findViewById ( R.id.layout_stock_note );
		// Retrieve a reference to the client stock count note edit text
		EditText noteEditText = (EditText) parent.findViewById ( R.id.edittext_stock_note );		
		// Retrieve a reference to the client stock count note title label
		TextView titleLabel = (TextView) parent.findViewById ( R.id.label_stock_note );
		
		// Display the client stock count note title label
		titleLabel.setText ( AppResources.getInstance ( this ).getString ( this , R.string.client_stock_count_note_title ) );
		if ( getIntent ().getIntExtra ( ITEMS_LIST_MODE , 0 ) == ClientStockDetailsActivity.ASSETS_LIST )
			titleLabel.setText ( AppResources.getInstance ( this ).getString ( this , R.string.client_asset_note_title ) );
		// Enable the save icon
		parent.findViewById ( R.id.icon_save_note ).setEnabled ( true );
		// Enable the edit text
		noteEditText.setEnabled ( true );
		// Display the field hint
		noteEditText.setHint ( AppResources.getInstance ( this ).getString ( this , R.string.client_stock_count_note_hint ) );
		if ( getIntent ().getIntExtra ( ITEMS_LIST_MODE , 0 ) == ClientStockDetailsActivity.ASSETS_LIST )
			noteEditText.setHint ( AppResources.getInstance ( this ).getString ( this , R.string.client_asset_note_hint ) );
		// Set the maximum number of allowed characters
		noteEditText.setFilters ( new InputFilter [] { new InputFilter.LengthFilter ( TransactionHeadersUtils.getFreeRemarksMaxLength () ) } );
		// Check if the view is being restored
		if ( ! restore )
			// Set the client stock count note
			noteEditText.setText ( note );
	}
	
	/**
	 * Called when a view has been clicked.<br>
	 * The pictures activity is opened.
	 * 
	 * @param view	The clicked view.
	 */
	public void takePicture ( View view ) {
		try {
			// Retrieve the user object
			Users user = DatabaseUtils.getInstance ( this ).getDaoSession ().getUsersDao ().queryBuilder ()
					.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( this ) ) ,
							UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( this ) ) ).unique ();
			// Declare and initialize a sequence formatter
			DecimalFormat sequence = new DecimalFormat ( "000000" );
			// Retrieve the client stock count sequence
			int clientStockCountSequence = DatabaseUtils.getUserSequence ( this , user.getUserCode () , user.getCompanyCode () , TransactionSequencesDao.Properties.ClientStockCount );
			// Compute the transaction header code
			String clientstockHeaderCode = "1" + String.valueOf ( user.getPrefixID () ) + sequence.format ( clientStockCountSequence );
			// Create a new intent to start a new activity
			Intent intent = new Intent ( this , PicturesActivity.class );
			// Add the transaction header code to the intent
			intent.putExtra ( PicturesActivity.TRANSACTION_CODE , clientstockHeaderCode );
			// Add the item code to the intent
			intent.putExtra ( PicturesActivity.ITEM_CODE , stockModification.getItem ().getItemCode () );
			// Start the new activity
			startActivity ( intent );
		} catch ( Exception exception ) {
			// An error occurred
		} // End of try-catch block
	}
	
	/**
	 * Called when a view has been clicked.<br>
	 * The client stock count modifications are saved.
	 * 
	 * @param view	The clicked view.
	 */
	public void updateStock ( View view ) {
		// Determine if an client stock count is undergoing any modifications
		if ( ! displayStockDetails )
			// No modifications
			return;
		
		// Retrieve a reference to the secondary view
		View secondary = findViewById ( R.id.layout_stock_details );
		// Retrieve a reference to the client stock count quantity layout
		View quantityLayout = secondary.findViewById ( R.id.layout_stock_quantity );
		// Retrieve references to the quantity number pickers
		NumberPicker quantityBig = (NumberPicker) quantityLayout.findViewById ( R.id.number_picker_quantity_big );
		NumberPicker quantityMedium = (NumberPicker) quantityLayout.findViewById ( R.id.number_picker_quantity_medium );
		NumberPicker quantitySmall = (NumberPicker) quantityLayout.findViewById ( R.id.number_picker_quantity_small );
		
		EditText shelf_price_small =  (EditText) secondary.findViewById(R.id.small_shelf_price);
		EditText shelf_price_medium = (EditText) secondary.findViewById(R.id.medium_shelf_price);
		
		shelfPrice_small = shelf_price_small.getText ().toString () ;
		shelfPrice_medium = shelf_price_medium.getText ().toString () ;
		
		if (shelfPrice_small != null && ! shelfPrice_small.trim().equals("") )
			_shelfPrice_small = Double.parseDouble (shelfPrice_small) ;
		if (shelfPrice_medium != null && ! shelfPrice_medium.trim().equals("") )
			_shelfPrice_medium = Double.parseDouble (shelfPrice_medium) ;
		
		// Retrieve references to the spinners
		Spinner existanceSpinner = (Spinner) secondary.findViewById ( R.id.spinner_existence );
		Spinner statusSpinner = (Spinner) secondary.findViewById ( R.id.spinner_status );
		// Retrieve the existence
		AssetsStatus existence = (AssetsStatus) existanceSpinner.getSelectedItem ();
		// Retrieve the over six months check flag
		boolean overSixMonths = ( (CheckBox) secondary.findViewById ( R.id.checkbox_over_six_months ) ).isChecked ();
		boolean available=((CheckBox) secondary.findViewById ( R.id.available ) ).isChecked ();
		// Check if the item is regular
		if ( ItemsUtils.isRegular ( stockModification.getItem () ) ) {
		
			// Check if any of the client stock count quantities / prices are not valid
			if ( ! quantityBig.isValid () || ! quantityMedium.isValid () || ! quantitySmall.isValid () ) {
				if ( ! quantityBig.isValid () )
					quantityBig.displayError ( null );
				if ( ! quantityMedium.isValid () )
					quantityMedium.displayError ( null );
				if ( ! quantitySmall.isValid () )
					quantitySmall.displayError ( null );
				// Exit method
				return;
			} // End if
			
			// Check if the expiry date is provided
			if ( expiryDate != null ) {
				// Retrieve the current date
				Calendar sixMonths = Calendar.getInstance ();
				// Add six months
				sixMonths.add ( Calendar.DATE , 180 );
				// Check if the expiry date and the over six months flag match
				if ( ( expiryDate.before ( sixMonths.getTime () ) && overSixMonths == true )
						|| ( expiryDate.after ( sixMonths.getTime () ) && overSixMonths == false ) ) {
					// Indicate that the expiry date is inconsistent
					Baguette.showText ( ClientStockDetailsActivity.this ,
							AppResources.getInstance ( ClientStockDetailsActivity.this ).getString ( ClientStockDetailsActivity.this , R.string.client_stock_count_expiry_date_inconsistency_message ) ,
							Baguette.BackgroundColor.RED );
					// Exit method
					return;
				} // End if
			} // End if
			
			// Make sure the current client stock count has valid quantities
			if ( quantityBig.getNumber () != 0 || quantityMedium.getNumber () != 0 || quantitySmall.getNumber () != 0 ) {
				// Check if the expiry date is mandatory
				if ( overSixMonths == false || ( overSixMonths == true && PermissionsUtils.getEnforceStockExpiryDate ( this , DatabaseUtils.getCurrentUserCode ( this ) , DatabaseUtils.getCurrentCompanyCode ( this ) ) ) )
					// Check if the expiry date is provided
					if ( expiryDate == null ) {
						// Indicate that the expiry date is missing
						Baguette.showText ( ClientStockDetailsActivity.this ,
								AppResources.getInstance ( ClientStockDetailsActivity.this ).getString ( ClientStockDetailsActivity.this , R.string.client_stock_count_invalid_expiry_date_message ) ,
								Baguette.BackgroundColor.RED );
						// Exit method
						return;
					} // End if
			} // End if
		
		} // End if
		
		// Check if the item is an asset
		else if ( ItemsUtils.isAsset ( stockModification.getItem () ) ) {
			
			// Check if the existence is affirmative
			if ( existence != null && existence.getStatusID () == 1 )
				// The existence is affirmative, make sure the status is valid
				if ( statusSpinner.getSelectedItemPosition () == 0 ) {
					// Indicate that the expiry date is missing
					Baguette.showText ( ClientStockDetailsActivity.this ,
							AppResources.getInstance ( ClientStockDetailsActivity.this ).getString ( ClientStockDetailsActivity.this , R.string.client_stock_count_invalid_asset_statuses_message ) ,
							Baguette.BackgroundColor.RED );
					// Exit method
					return;
				} // End if
			
		} // End else if
		
		
		// Reset flag
		displayStockDetails = false;
		
		// Update the client stock count quantities
		stockModification.setQuantityBig ( (int) quantityBig.getNumber () );
		stockModification.setQuantityMedium ( (int) quantityMedium.getNumber () );
		stockModification.setQuantitySmall ( (double) quantitySmall.getNumber () );
		
		
		stockModification.setShelfPriceMedium(_shelfPrice_medium);
		stockModification.setShelfPriceSmall(_shelfPrice_small);
		
		// Update the expiry date
		stockModification.setExpiryDate ( expiryDate );
		// Update the merchandize flag
		stockModification.setMerchandize ( ( (CheckBox) secondary.findViewById ( R.id.checkbox_merchandize ) ).isChecked () );
		// Update the availables flag
		stockModification.setAvailables(available);
		// Update the over six months flag
		stockModification.setOverSixMonths ( overSixMonths );
		// Update the existence
		stockModification.setExistence ( existence == null || existanceSpinner.getSelectedItemPosition () == 0 ? null : existence );
		// Update the status
		stockModification.setStatus ( existence != null && existence.getStatusID () == 1 ? statusSpinner.getSelectedItemPosition () == 0 ? null : (AssetsStatus) ( statusSpinner ).getSelectedItem () : null );
		
		// Disable the save icon
		secondary.findViewById ( R.id.icon_save_stock ).setEnabled ( false );
		// Disable the picture icon
		secondary.findViewById ( R.id.icon_picture_stock ).setEnabled ( false );
		// Remove the existence spinner listener
		existanceSpinner.setOnItemSelectedListener ( null );
		// Hide the software keyboard
        ( (InputMethodManager) getBaseContext ().getSystemService ( INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow ( secondary.getWindowToken (), 0 );
        
		// Hide the secondary view
		secondary.startAnimation ( getViewAnimationOut ( secondary ) );
		// Enable the main list
		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( true );
		// Indicate that the save was successful
		Vibration.vibrate ( this );
		// Refresh the action bar
		invalidateOptionsMenu ();
		
		// Update the client stock count / UI
		onOrderModificationResult ();
	}
	
	/**
	 * Called when a view has been clicked.<br>
	 * The client stock count note is saved.
	 * 
	 * @param view	The clicked view.
	 */
	public void updateNote ( View view ) {
		// Determine if the client stock count note is undergoing any modifications
		if ( ! displayStockNote )
			// No modifications
			return;
		
		// Reset flag
		displayStockNote = false;
		
		// Retrieve a reference to the tertiary view
		View tertiary = findViewById ( R.id.layout_stock_note );
		// Retrieve a reference to the client stock count note edit text
		EditText noteEditText = (EditText) tertiary.findViewById ( R.id.edittext_stock_note );
		// Store the client stock count note
		note = noteEditText.getText ().toString ().trim ();
		
		// Disable the save icon
		tertiary.findViewById ( R.id.icon_save_note ).setEnabled ( false );
		// Hide the software keyboard
        ( (InputMethodManager) getBaseContext ().getSystemService ( INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow ( tertiary.getWindowToken (), 0 );
        
		// Hide the secondary view
        tertiary.startAnimation ( getViewAnimationOut ( tertiary ) );
		// Enable the main list
		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( true );
		// Indicate that the save was successful
		Vibration.vibrate ( this );
		// Refresh the action bar
		invalidateOptionsMenu ();
		
		// Update the client stock count details list with the new note
		onNoteModificationResult ();
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
//    	// Parse the activity result to an barcode intent integration result
//    	IntentResult scanResult = IntentIntegrator.parseActivityResult ( requestCode , resultCode , data );
//    	// Check if the barcode scan result is valid
//    	if ( scanResult != null && searchView != null ) {
//			// Set the new query
//			searchView.setQuery ( scanResult.getContents () == null ? "" : scanResult.getContents () , false );
//			// Expand the SearchView
//			searchView.setIconified ( false );
//    	} // End if
    	
	}
	
	/**
	 * Called after a successful client stock count modification : price or quantity modification.<br>
	 * The corresponding object is modified in the main list and the appropriate action is performed (based on the current request code).
	 */
	private void onOrderModificationResult () {
		// Retrieve a reference to the client stock count object from the main list
		Stock stock = null;
		// Iterate over all the client stock counts
		for ( int i = 0 ; i < stocks.size () ; i ++ )
			// Match the client stock count
			if ( stocks.get ( i ).getItem ().getItemCode ().equals ( stockModification.getItem ().getItemCode () ) ) {
				// Update the selected client stock count position
				stock = stocks.get ( i );
				// Exit loop
				break;
			} // End if
		// Check if the stock object is valid
		if ( stock == null )
			// Invalid object
			return;
    	// Update the client stock count object
    	stock.setQuantityBig ( stockModification.getQuantityBig () );
    	stock.setQuantityMedium ( stockModification.getQuantityMedium () );
    	stock.setQuantitySmall ( stockModification.getQuantitySmall ()  );
    	stock.setExpiryDate ( stockModification.getExpiryDate () );
    	stock.setMerchandize ( stockModification.isMerchandize () );
    	stock.setAvailables( stockModification.isAvailables() );
    	stock.setExistence ( stockModification.getExistence () );
    	stock.setStatus ( stockModification.getStatus () );
    	
       	stock.setShelfPriceMedium ( stockModification.getShelfPriceMedium() );
    	stock.setShelfPriceSmall ( stockModification.getShelfPriceSmall() );
    	
		// Remove item from the selected client stock counts (if valid)
		if ( selectedStocks != null && ( ItemsUtils.isRegular ( stock.getItem () ) && stock.getQuantityBig () == 0 && stock.getQuantityMedium () == 0 && stock.getQuantitySmall () == 0 )
				|| ( ItemsUtils.isAsset ( stock.getItem () ) && stock.getExistence () == null ) )
			selectedStocks.remove ( stock );
		// Refresh client stock count details
		refreshDetails ();
		// Check if the request code is INFO
		// AND if the selected client stock counts list is empty
		if ( ClientStockDetailsActivity.this.requestCode == REQUEST_CODE_INFO && selectedStocks != null && selectedStocks.isEmpty () )
			// Emulate a back key press
			onBackPressed ();
		else
	    	// Refresh the list
	    	( (BaseAdapter) getListAdapter () ).notifyDataSetChanged ();
	}
	
	/**
	 * Called after a successful note modification.<br>
	 * The note in the client stock count details list is updated.
	 */
	private void onNoteModificationResult () {
		// Iterate over all details
		for ( StockInfo detail : details )
			// Check if the current detail is about the client stock count note
			if ( detail.getId () == StockInfo.ID.NOTE ) {
				// Update the client stock count note
				detail.setValue ( note );
				// Exit loop
				break;
			} // End if
		// Refresh the list
		( (MultipleListAdapter) getListAdapter () ).notifyDataSetChanged ();
	}
	
	/*
	 * Initialize the contents of the Activity's standard options menu.
	 *
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
    @Override
    public boolean onCreateOptionsMenu ( Menu menu ) {
    	// Check if the list of client stock counts is valid
    	// OR if a client stock count / note is undergoing modifications
    	if ( stocks == null || displayStockDetails || displayStockNote )
    		// Hide the menu
            return false;
    	// Use the MenuInflater of this context to inflate a menu hierarchy from the specified XML resource
    	getMenuInflater ().inflate ( R.menu.action_bar , menu );
    	// Determine if the request code is info
    	if ( requestCode != REQUEST_CODE_INFO ) {
    		// The request code is NOT INFO
	    	// Enable the required menu items
    		if ( itemsListMode == REGULAR_ITEMS_LIST )
    			me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_list ) );
	    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_basket ) );
	    	if ( getIntent ().getIntExtra ( ITEMS_LIST_MODE , 0 ) == 0 )
	    		me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_settings ) );
//	    	if ( PermissionsUtils.getItemBarcodes ( this , DatabaseUtils.getCurrentUserCode ( this ) , DatabaseUtils.getCurrentCompanyCode ( this ) ) )
//				me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_barcode ) );
	    	
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
    	} // End if
    	// Otherwise the request code is INFO
    	// Determine if the view only flag is provided
    	else if ( ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) ) {
        	// Enable the required menu items
    		if ( clientStockHeader == null
    				|| ( clientStockHeader.getIsProcessed () != IsProcessedUtils.isSync () ) ) {
            	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_save ) );
            	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_keyboard ) );
    		} // End if
    	} // End else
		// Display the menu
        return true;
    }
    
	/**
	 * Performs all necessary setup in client stock count to properly display the search view widget.
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
    	// Check if the client stock counts list is valid
    	if ( stocks == null )
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
    	
//    	else if ( menuItem.getItemId () == R.id.action_barcode ) {
//    		// Declare and initialize a barcode intent integrator
//    	//	IntentIntegrator integrator = new IntentIntegrator ( this );
//    		// Initiate a barcode scan
//    	//	integrator.initiateScan();
//    		// Consume event
//    	//	return true;
//    		
//    		// Declare and initialize an alert dialog builder
//    		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder ( this );
//    		// By default, the alert dialog cannot be cancelled UNLESS it has no buttons
//    		alertDialogBuilder.setCancelable ( false );
//    		// Create view
//    		final EditText editText = new EditText ( this );
//    		editText.setId ( 1 );
//    		editText.setInputType ( InputType.TYPE_CLASS_TEXT  );
//    		// Create click listener
//    		DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener () {
//    			@Override
//    			public void onClick ( DialogInterface dialog , int which ) {
//    				// Determine the clicked button
//    				switch ( which ) {
//    				case DialogInterface.BUTTON_POSITIVE:
//    	    			// Perform a client barcode scan
//    	    			new ItemBarcodeScan ( editText.getText ().toString () ).execute ();
//    				case DialogInterface.BUTTON_NEGATIVE:
//    		    		// Hide the software keyboard
//    		            ( (InputMethodManager) getBaseContext ().getSystemService ( INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow ( editText.getWindowToken (), 0 );
//    					// Dismiss dialog
//    					AppDialog.getInstance ().dismiss ();
//    					break;
//    				} // End switch
//    			}
//    		};
//    		// Setup dialog
//    		alertDialogBuilder.setTitle ( R.string.barcode_hint );
//    		alertDialogBuilder.setView ( editText );
//    		alertDialogBuilder.setPositiveButton ( AppResources.getInstance ( this ).getString ( this , R.string.ok_label ) , onClickListener );
//    		alertDialogBuilder.setNegativeButton ( AppResources.getInstance ( this ).getString ( this , R.string.cancel_label ) , onClickListener );
//    		alertDialogBuilder.create ();
//    		alertDialogBuilder.show ();
//    		
//    		// Consume event
//    		return true;
//    	} // End else if
    	// Determine if the action settings is selected
    	else if ( menuItem.getItemId () == R.id.action_settings ) {
    		// Declare and initialize the various items list modes
    		String modes [] = new String [] {
    				AppResources.getInstance ( this ).getString ( this , R.string.client_stock_count_regular_items_label ) ,
    				AppResources.getInstance ( this ).getString ( this , R.string.client_stock_count_assets_items_label )
    		};
    		// Display the settings list for the items list mode
    		AppDialog.getInstance ().displayList ( this ,
    				AppResources.getInstance ( this ).getString ( this , R.string.settings_label ) ,
    				modes ,
    				AppDialog.Cancelable.BACK_BUTTON_AND_TOUCH ,
    				new DialogInterface.OnClickListener () {
    					@Override
    					public void onClick ( DialogInterface dialog , int which ) {
    			    		// Hide the software keyboard
    			            ( (InputMethodManager) getBaseContext ().getSystemService ( INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow ( findViewById ( R.id.view_clear_focus ).getWindowToken (), 0 );
    				    	// Determine if the action priority items list is selected
    						// AND if the current mode is different than the priority items list mode
    				    	if ( which == 0 && itemsListMode != REGULAR_ITEMS_LIST ) {
				    			// Set the priority items list mode
				    			itemsListMode = REGULAR_ITEMS_LIST;
				    			// Display the applied list mode in the action bar
				    			displayItemsListMode ();
				        		// Refresh the action bar
				        		invalidateOptionsMenu ();
					    		// Filter the list asynchronously
					    		new FilterList ( searchQuery ).execute ();
    				    	} // End else if
    				    	// Otherwise determine if the action main items list is selected
    				    	// AND if the current mode is different than the main items list mode
    				    	else if ( which == 1 && itemsListMode != ASSETS_LIST ) {
				    			// Set the main items list mode
				    			itemsListMode = ASSETS_LIST;
				    			// Display the applied list mode in the action bar
				    			displayItemsListMode ();
				        		// Refresh the action bar
				        		invalidateOptionsMenu ();
					    		// Filter the list asynchronously
					    		new FilterList ( searchQuery ).execute ();
    				    	} // End else if
    					}
    				} ,
    				null );
    	} // End else if
    	// Determine if the action basket is selected
    	else if ( menuItem.getItemId () == R.id.action_basket ) {
    		// Declare and initialize a list used to host the valid client stock count
    		ArrayList < Stock >  clientstocks = new ArrayList < Stock > ();
            // Make sure the entire client must stock list is confirmed
            boolean isStockListConfirmed = true;
        	// Iterate over all client stock counts
    		for ( Stock stock : stocks ) {
            	// Check if the current item belongs to the must stock list and is not confirmed
            	if ( getIntent ().getIntExtra ( ITEMS_LIST_MODE , 0 ) != ClientStockDetailsActivity.ASSETS_LIST && stock.isMustStockList () && ! stock.isConfirmed () ) {
            		// Reset flag
            		isStockListConfirmed = false;
            		// Exit loop
            		break;
            	} // End if
            	stock.setQuantityBig ( stock.isAvailables() == true ? 1 : 0);
    			// Check if the current client stock count has valid quantities
    			if ( ItemsUtils.isRegular ( stock.getItem () ) && ( stock.getQuantityBig () != 0 || stock.getQuantityMedium () != 0 || stock.getQuantitySmall () != 0 )
    				|| ( ItemsUtils.isAsset ( stock.getItem () ) && stock.getExistence () != null ) )
    				// The client stock count contains at least one quantity, add it to the client stock count list
    				clientstocks.add ( stock );
            } // End for each
            // Check if at least one must stock item is NOT confirmed
            if ( ! isStockListConfirmed ) {
            	// Warn the user that all the must stock list should be confirmed
            	AppDialog.getInstance ().displayAlert ( this ,
            			AppResources.getInstance ( this ).getString ( this , R.string.warning_label ) ,
            			AppResources.getInstance ( this ).getString ( this , R.string.sales_order_confirm_must_stock_message ) ,
            			ButtonsType.OK , null );
        		// Consume event
        		return true;
            } // End if
    		// Check if there is at least one valid client stock count
    		if ( clientstocks.isEmpty () ) {
    			// Compute baguette message
    			String baguetteMessage = AppResources.getInstance ( ClientStockDetailsActivity.this ).getString ( ClientStockDetailsActivity.this , R.string.no_client_stock_message );
    			if ( getIntent ().getIntExtra ( ITEMS_LIST_MODE , 0 ) == ClientStockDetailsActivity.ASSETS_LIST )
    				baguetteMessage = AppResources.getInstance ( ClientStockDetailsActivity.this ).getString ( ClientStockDetailsActivity.this , R.string.no_client_asset_message );
				// Indicate that some clients were removed
				Baguette.showText ( ClientStockDetailsActivity.this , baguetteMessage , Baguette.BackgroundColor.RED );
        		// Consume event
        		return true;
    		} // End if
    		// Otherwise there is at least one client stock count
    		// Set the new request code
    		requestCode = REQUEST_CODE_INFO;
    		// Refresh the action bar
    		invalidateOptionsMenu ();
    		// Set the selected client stock counts list
    		selectedStocks = clientstocks;
			// Initialize the client stock count details
			initializeDetails ();
			
			// Compute labels
			String infoLabel = AppResources.getInstance ( this ).getString ( this , R.string.client_stock_count_details_label );
			if ( getIntent ().getIntExtra ( ITEMS_LIST_MODE , 0 ) == ClientStockDetailsActivity.ASSETS_LIST )
				infoLabel = AppResources.getInstance ( this ).getString ( this , R.string.client_asset_details_label );
			String detailsLabel = AppResources.getInstance ( this ).getString ( this , R.string.client_stock_count_products_label );
			if ( getIntent ().getIntExtra ( ITEMS_LIST_MODE , 0 ) == ClientStockDetailsActivity.ASSETS_LIST )
				detailsLabel = AppResources.getInstance ( this ).getString ( this , R.string.client_asset_products_label );
			
			// Declare and initialize a multiple list adapter
			MultipleListAdapter adapter = new MultipleListAdapter ( this );
			// Add the client stock count details adapter
			adapter.addSection ( new Section ( infoLabel , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
					new StockInfoAdapter ( this , R.layout.order_info_item , details ) );
			// Add the client stock count adapter
			adapter.addSection ( new Section ( detailsLabel , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
					getClientStockDetailsAdapter ( R.layout.sales_order_details_activity_item , selectedStocks , currency.getCurrencyRounding () , ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) ,false) );
			// Set the list adapter
			setListAdapter ( adapter );
			// Refresh the action bar
			displayItemsListMode ();

    		// Consume event
    		return true;
    	} // End else if
    	// Determine if the action keyboard is selected
    	else if ( menuItem.getItemId () == R.id.action_keyboard ) {
    		// Set flag
    		displayStockNote = true;
    		// Initialize the tertiary view
    		initializeTertiaryView ( false );
    		// Disable the main list
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( false );
    		// Retrieve a reference to the tertiary view
    		View tertiaryView = findViewById ( R.id.layout_stock_note );
    		// Display the tertiary view
    		tertiaryView.setVisibility ( View.VISIBLE );
    		// Animate the tertiary view
    		tertiaryView.startAnimation ( getViewAnimationIn() );
    		// Refresh the action bar
    		invalidateOptionsMenu ();
    		// Consume event
    		return true;
    	} // End else if
    	// Determine if the action save is selected
    	else if ( menuItem.getItemId () == R.id.action_save ) {
    		// Check if there is a previously stored client stock count
    		if ( clientStockHeader != null && clientStockDetails != null && clientAssets != null )
	    		// Check if there are modifications
	    		if ( ! hasModifications () ) {
	    			// Indicate that there are no new modifications
					Baguette.showText ( this ,
							AppResources.getInstance ( this ).getString ( this , R.string.no_new_modifications_message ) ,
							Baguette.BackgroundColor.BLUE );
		    		// Consume event
		    		return true;
	    		} // End if
    		// Otherwise the client stock count can be saved
			// Flag used to indicate whether an error occurred or not
			boolean error = false;
	    	// Compute current time
	    	Calendar now = Calendar.getInstance ();
			// Declare and initialize a sequence formatter
			DecimalFormat sequence = new DecimalFormat ( "000000" );
			// Retrieve the call object
			Call call = (Call) getIntent ().getSerializableExtra ( CALL );
			// Retrieve the visit object
			Visits visit = (Visits) getIntent ().getSerializableExtra ( VISIT );
			// Retrieve the user object
			Users user = DatabaseUtils.getInstance ( this ).getDaoSession ().getUsersDao ().queryBuilder ()
					.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( this ) ) ,
							UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( this ) ) ).unique ();

			// Determine if there is a previously stored client stock count
			if ( clientStockHeader == null && clientStockDetails == null && clientAssets == null ) {
	    		try {
					// Retrieve the client stock count sequence
					int clientStockCountSequence = DatabaseUtils.getUserSequence ( this , user.getUserCode () , user.getCompanyCode () , TransactionSequencesDao.Properties.ClientStockCount );
					// Begin transaction
					DatabaseUtils.getInstance ( ClientStockDetailsActivity.this ).getDaoSession ().getDatabase ().beginTransaction ();
		    		// Compute the transaction header code
		    		String clientstockHeaderCode = "1" + String.valueOf ( user.getPrefixID () ) + sequence.format ( clientStockCountSequence );
					// Update the client stock count sequence
					DatabaseUtils.setUserSequence ( this , user.getUserCode () , user.getCompanyCode () , TransactionSequencesDao.Properties.ClientStockCount , clientStockCountSequence + 1 );
		    		// TODO : Details of a Client Stock header
		    		// Declare and initialize a transaction header
		    		ClientStockCountHeaders slientStockCountHeader = new ClientStockCountHeaders ( null , // ID
		    				clientstockHeaderCode , // TransactionCode
		    				call.getClientDivision ().getClientCode () , // ClientCode
		    				user.getUserCode () , // UserCode
		    				visit.getVisitID () , // VisitID
		    				visit.getJourneyCode () , // JourneyCode
		    				call.getClientDivision ().getDivisionCode () , // DivisionCode
		    				call.getClientDivision ().getCompanyCode () , // CompanyCode
		    				currency.getCurrencyCode () , // CurrencyCode
		    				note , // Note
		    				now.getTime () , // Date
		    				getIntent ().getIntExtra ( COUNT_TYPE , -1 ) , // CountType
		    				getIntent ().getIntExtra ( ITEMS_LIST_MODE , -1 ) , // ItemType
		    				IsProcessedUtils.isNotSync () , // IsProcessed
		    				now.getTime () ); // StampDate
		    		// Insert the transaction header in DB
		    		DatabaseUtils.getInstance ( this ).getDaoSession ().getClientStockCountHeadersDao().insert ( slientStockCountHeader );
		    		// Track the line IDs
		    		int stockLineID = 0;
		    		int assetLineID = 0;
		    		// Iterate over all selected client stock counts
					for ( int i = 0 ; i < selectedStocks.size () ; i ++ ) {
						// Check if the item is regular
						if ( ItemsUtils.isRegular ( selectedStocks.get ( i ).getItem () ) )
							// TODO : Details of a client stock count detail
							// Insert a new transaction detail in DB
							DatabaseUtils.getInstance ( this ).getDaoSession ().getClientStockCountDetailsDao ().insert ( new ClientStockCountDetails ( null , // ID
									clientstockHeaderCode , // TransactionCode
									stockLineID ++ , // LineID
									selectedStocks.get ( i ).getItem ().getItemCode () , // ItemCode
									selectedStocks.get ( i ).isAvailables() ?(double) 1 : (double) 0 , // QuantityBig
									(double) selectedStocks.get ( i ).getQuantityMedium () , // QuantityMedium
									(double) selectedStocks.get ( i ).getQuantitySmall () , // QuantitySmall
									//(double) selectedStocks.get ( i ).getQuantityBig () * selectedStocks.get ( i ).getItem ().getUnitBigMedium () * selectedStocks.get ( i ).getItem ().getUnitMediumSmall ()
										//+
										selectedStocks.get ( i ).getQuantityMedium () * selectedStocks.get ( i ).getItem ().getUnitMediumSmall ()
										+ selectedStocks.get ( i ).getQuantitySmall () , // PriceBig
									(double) selectedStocks.get ( i ).getPriceBig () , // PriceBig
									(double) selectedStocks.get ( i ).getPriceMedium () , // PriceSmall
									(double) selectedStocks.get ( i ).getPriceSmall () , // PriceSmall
									selectedStocks.get ( i ).getShelfPriceBig () , // ShelfPriceBig
									selectedStocks.get ( i ).getShelfPriceMedium ()  , // ShelfPriceMedium
									selectedStocks.get ( i ).getShelfPriceSmall ()  , // ShelfPriceSmall
									note , // Note
									null , // ReasonCode
									selectedStocks.get ( i ).isMerchandize () ? 1 : 0 , // IsMerchandize
									selectedStocks.get ( i ).isOverSixMonths () ? 1 : 0 , // isOverSixMonths
									selectedStocks.get ( i ).getExpiryDate () , // ItemExpiryDate
									now.getTime () ) ); // StampDate
						// Check if the item is asset
						else if ( ItemsUtils.isAsset ( selectedStocks.get ( i ).getItem () ) )
							// TODO : Details of a client asset status
							// Insert a new transaction detail in DB
							DatabaseUtils.getInstance ( this ).getDaoSession ().getClientAssetStatusDao ().insert ( new ClientAssetStatus ( null , // ID
									clientstockHeaderCode , // LineId
									assetLineID , // LineId
									selectedStocks.get ( i ).getItem ().getItemCode () , // AssetCode
									String.valueOf ( assetLineID ++ ) , // StatusCode
									selectedStocks.get ( i ).getStatus () == null ? null : String.valueOf ( selectedStocks.get ( i ).getStatus ().getStatusID () ) , // StatusCode
									String.valueOf ( selectedStocks.get ( i ).getExistence ().getStatusID () ) , // ExistanceCode
									null , // Remark
									selectedStocks.get ( i ).getItem ().getItemName () , // AssetName
									selectedStocks.get ( i ).getItem ().getItemAltName () , // AssetAltName
									null , // AssetBarcode
									IsProcessedUtils.isNotSync () , // IsProcessed
									now.getTime () ) ); // StampDate
					} // End for loop
					
		    		// Indicate that a Client Stock was successfully performed during this visit
		    		CallAction.setCallActionStatus ( this , visit.getVisitID () , CallAction.ID.CLIENT_STOCK , true );
					
					// Commit changes
					DatabaseUtils.getInstance ( ClientStockDetailsActivity.this ).getDaoSession ().getDatabase ().setTransactionSuccessful ();
					// Indicate that the save was successful
					Vibration.vibrate ( ClientStockDetailsActivity.this );
	    		} catch ( Exception exception ) {
					// Indicate that an error occurred
					error = true;
	    		} finally {
					// End transaction
					DatabaseUtils.getInstance ( ClientStockDetailsActivity.this ).getDaoSession ().getDatabase ().endTransaction ();
	    		} // End try-catch-finally block
			} // End if
			else if ( clientStockHeader != null && clientStockDetails != null && clientAssets != null ) {
				// Before saving, refresh the objects to make sure the client stock count is not already sent to the server ( via auto sync )
				DatabaseUtils.getInstance ( ClientStockDetailsActivity.this ).getDaoSession ().getClientStockCountHeadersDao().refresh ( clientStockHeader );
				// Check that the transaction header is not processed to the server
				if ( clientStockHeader.getIsProcessed () == IsProcessedUtils.isSync () ) {
	    			// Indicate that the client stock count cannot be modified anymore
					Baguette.showText ( this ,
							AppResources.getInstance ( this ).getString ( this , R.string.sales_order_already_processed_message ) ,
							Baguette.BackgroundColor.RED );
		    		// Consume event
		    		return true;
				} // End if
				
				// Update the clientStock header
				clientStockHeader.setDate ( Calendar.getInstance ().getTime () );
				clientStockHeader.setNote ( note );
				clientStockHeader.setStampDate ( Calendar.getInstance ().getTime () );
				// Map the clientStock details to their item codes
				Map < String , ClientStockCountDetails > _clientStockDetails = new HashMap < String , ClientStockCountDetails > ();
				Map < String , ClientAssetStatus > _clientAsset = new HashMap < String , ClientAssetStatus > ();
	    		// Compute the maximum transaction detail line id
	    		int maxIdItems = -1;
	    		int maxIdAssets = -1;
				// Iterate over all clientStock details
				for ( ClientStockCountDetails clientStockDetail : clientStockDetails ) {
					// Map the current clientStock detail to its item code
					_clientStockDetails.put ( clientStockDetail.getItemCode () , clientStockDetail );
					// Check if the maximum line id is exceeded
					if ( clientStockDetail.getLineId () > maxIdItems )
						// Set the new line id
						maxIdItems = clientStockDetail.getLineId ();
				} // End for each
				// Iterate over all client assets
				for ( ClientAssetStatus clientAsset : clientAssets ) {
					// Map the current client asset to its item code
					_clientAsset.put ( clientAsset.getAssetCode () , clientAsset );
					// Check if the maximum line id is exceeded
					if ( clientAsset.getLineID () > maxIdAssets )
						// Set the new line id
						maxIdAssets = clientAsset.getLineID ();
				} // End for each
				
				// Declare and initialize clientStock details used to add, update and remove the clientStock details
				List < ClientStockCountDetails > addItems = new ArrayList < ClientStockCountDetails > ();
				List < ClientStockCountDetails > updateItems = new ArrayList < ClientStockCountDetails > ();
				List < ClientAssetStatus > addAssets = new ArrayList < ClientAssetStatus > ();
				List < ClientAssetStatus > updateAssets = new ArrayList < ClientAssetStatus > ();
				// Iterate over all selected client stock counts
				for ( Stock stock : selectedStocks )
					// Check if the item is an item
					if ( ItemsUtils.isRegular ( stock.getItem () ) ) {
						// Determine the corresponding transaction detail
						if ( _clientStockDetails.containsKey ( stock.getItem ().getItemCode () ) ) {
							// Retrieve a reference to the transaction detail
							ClientStockCountDetails clientStockDetail = _clientStockDetails.get ( stock.getItem ().getItemCode () );
							// Add the transaction to the list of updated transaction details
							updateItems.add ( clientStockDetail );
							// Update the client stock count details
						//	clientStockDetail.setQuantityBig ( (double) stock.getQuantityBig () );
							clientStockDetail.setQuantityMedium ( (double) stock.getQuantityMedium () );
							clientStockDetail.setQuantitySmall ( (double) stock.getQuantitySmall () );
							
							clientStockDetail.setShelfPriceBig( stock.getShelfPriceBig () );
							clientStockDetail.setShelfPriceMedium( stock.getShelfPriceMedium () );
							clientStockDetail.setShelfPriceSmall( stock.getShelfPriceSmall () );
							
							clientStockDetail.setBasicUnitQuantity (
									//(double) stock.getQuantityBig () * stock.getItem ().getUnitBigMedium () * stock.getItem ().getUnitMediumSmall ()
									//+ 
									stock.getQuantityMedium () * stock.getItem ().getUnitMediumSmall ()
									+ stock.getQuantitySmall () );
							clientStockDetail.setPriceBig ( stock.getPriceBig () );
							clientStockDetail.setPriceMedium ( stock.getPriceMedium () );
							clientStockDetail.setPriceSmall ( stock.getPriceSmall () );
							clientStockDetail.setIsMerchandize ( stock.isMerchandize () ? 1 : 0 );
							clientStockDetail.setQuantityBig(stock.isAvailables()  ?( double )1 : ( double )0);
							clientStockDetail.setItemExpiryDate ( stock.getExpiryDate () );
							clientStockDetail.setStampDate ( now.getTime () );
						} // End if
						else
							// TODO : Details of a client stock count transaction detail
							// Create a new transaction detail
							addItems.add ( new ClientStockCountDetails ( null , // ID
									clientStockHeader.getTransactionCode () , // TransactionCode
									++ maxIdItems , // LineID
									stock.getItem ().getItemCode () , // ItemCode
									stock.isMerchandize () ? ( double ) 1 : ( double ) 0  , // QuantityBig
									(double) stock.getQuantityMedium () , // QuantityMedium
									(double) stock.getQuantitySmall () , // QuantitySmall
									(double) stock.getQuantityBig () * stock.getItem ().getUnitBigMedium () * stock.getItem ().getUnitMediumSmall ()
										+ stock.getQuantityMedium () * stock.getItem ().getUnitMediumSmall ()
										+ stock.getQuantitySmall () , // PriceBig
									(double) stock.getPriceBig () , // PriceBig
									(double) stock.getPriceMedium () , // PriceSmall
									(double) stock.getPriceSmall () , // PriceSmall
									stock.getShelfPriceBig () , // ShelfPriceBig
									stock.getShelfPriceMedium () , // ShelfPriceMedium
									stock.getShelfPriceSmall () , // ShelfPriceSmall
									note , // Note
									null , // ReasonCode
									stock.isMerchandize () ? 1 : 0 , // IsMerchandize
									stock.isOverSixMonths () ? 1 : 0 , // isOverSixMonths
									stock.getExpiryDate () , // ItemExpiryDate
									now.getTime () ) ); // StampDate
					} // End if
					// Check if the item is an asset
					else if ( ItemsUtils.isAsset ( stock.getItem () ) ) {
						// Determine the corresponding transaction detail
						if ( _clientAsset.containsKey ( stock.getItem ().getItemCode () ) ) {
							// Retrieve a reference to the transaction detail
							ClientAssetStatus clientAssetStatus = _clientAsset.get ( stock.getItem ().getItemCode () );
							// Add the transaction to the list of updated transaction details
							updateAssets.add ( clientAssetStatus );
							// Update the client asset
							clientAssetStatus.setExistanceCode ( String.valueOf ( stock.getExistence ().getStatusID () ) );
							clientAssetStatus.setStatusCode ( stock.getStatus () == null ? null : String.valueOf ( stock.getStatus ().getStatusID () ) );
							clientAssetStatus.setStampDate ( now.getTime () );
						} // End if
						else
							// TODO : Details of a client stock count transaction detail
							// Create a new transaction detail
							addAssets.add ( new ClientAssetStatus ( null , // ID
									clientStockHeader.getTransactionCode () , // LineId
									++ maxIdAssets , // LineId
									stock.getItem ().getItemCode () , // AssetCode
									String.valueOf ( maxIdAssets ) , // StatusCode
									stock.getStatus () == null ? null : String.valueOf ( stock.getStatus ().getStatusID () ) , // StatusCode
									String.valueOf ( stock.getExistence ().getStatusID () ) , // ExistanceCode
									null , // Remark
									stock.getItem ().getItemName () , // AssetName
									stock.getItem ().getItemAltName () , // AssetAltName
									null , // AssetBarcode
									IsProcessedUtils.isNotSync () , // IsProcessed
									now.getTime () ) ); // StampDate
					} // End else if
				
	    		try {
					// Begin transaction
					DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().beginTransaction ();
					
					// Update the transaction header in DB
					DatabaseUtils.getInstance ( this ).getDaoSession ().getClientStockCountHeadersDao ().update ( clientStockHeader );
					// Iterate over all transaction details
					for ( ClientStockCountDetails clientStockDetail : clientStockDetails )
						// Determine if the current transaction detail should be updated
						if ( updateItems.contains ( clientStockDetail ) ) {
							// Update the transaction detail in DB
							DatabaseUtils.getInstance ( this ).getDaoSession ().getClientStockCountDetailsDao ().update ( clientStockDetail );
						} // End if
						else {
							// Otherwise the transaction detail should be removed
							DatabaseUtils.getInstance ( this ).getDaoSession ().getClientStockCountDetailsDao ().delete ( clientStockDetail );
						} // End else
					// Iterate over all new transaction details
					for ( ClientStockCountDetails clientStockDetail : addItems ) {
						// Insert the new transaction detail into DB
						DatabaseUtils.getInstance ( this ).getDaoSession ().getClientStockCountDetailsDao ().insert ( clientStockDetail );
					} // End for each
					
					// Iterate over all transaction details
					for ( ClientAssetStatus clientAsset : clientAssets )
						// Determine if the current transaction detail should be updated
						if ( updateAssets.contains ( clientAsset ) ) {
							// Update the transaction detail in DB
							DatabaseUtils.getInstance ( this ).getDaoSession ().getClientAssetStatusDao ().update ( clientAsset );
						} // End if
						else {
							// Otherwise the transaction detail should be removed
							DatabaseUtils.getInstance ( this ).getDaoSession ().getClientAssetStatusDao ().delete ( clientAsset );
						} // End else
					// Iterate over all new transaction details
					for ( ClientAssetStatus clientAsset : addAssets ) {
						// Insert the new transaction detail into DB
						DatabaseUtils.getInstance ( this ).getDaoSession ().getClientAssetStatusDao ().insert ( clientAsset );
					} // End for each
						
					// Commit changes
					DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().setTransactionSuccessful ();
					// Indicate that the save was successful
					Vibration.vibrate ( this );
	    		} catch ( Exception exception ) {
    				// Indicate that an error occurred
    				error = true;
	    		} finally {
    				// End transaction
    				DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().endTransaction ();
	    		} // End try-catch-finally block
			} // End else if
			else
				// Indicate that an error occurred
				error = true;
			
        	// Call this to set the result that your activity will return to its caller
        	setResult ( RESULT_OK , new Intent ().putExtra ( ClientStockActivity.SAVE_SUCCESS , ! error ) );
        	// Finish this activity
        	finish ();
			// Indicate that the save was successful
			Vibration.vibrate ( ClientStockDetailsActivity.this );
			
    		// Consume event
    		return true;
    	} // End else if
    	// Allow normal menu processing to proceed
    	return false;
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
	 * Properly initializes and returns a list adapter for the client stock list.
	 * 
	 * @param layout	XML Layout (layout) resource id.
	 * @param stockItems	List of {@link me.SyncWise.Android.Modules.ClientStock.Stock Stock} objects.
	 * @param currencyRounding	Integer used to indicate the currency rounding.
	 * @param itemsEnabled	Flag indicating if the items are enabled or not.
	 * @return	A list adapter used to populate the client stock list.
	 */
	protected ListAdapter getClientStockDetailsAdapter ( int layout , List < Stock > stockItems , final int currencyRounding , final boolean itemsEnabled, final boolean picker ) {
		return new ClientStockDetailsAdapter ( this , layout , stockItems , currencyRounding , itemsEnabled,picker );
	}
	

	/**
	 * Determines if there is at least one client stock.
	 * 
	 * @return	Flag indicating if there is at least one client stock.
	 */
	private boolean hasClientStock () {
		// Check if the client stock counts list is valid
		if ( stocks == null )
			// Invalid list
			return false;
		// Declare and initialize a list used to host the valid client stock count
		ArrayList < Stock > stocks = new ArrayList < Stock > ();
		// Iterate over all client stock counts
		for ( Stock stock : this.stocks ){
			stock.setQuantityBig(stock.isAvailables() == true ? 1 : 0);
			// Check if the current client stock count has valid quantities
			if ( ItemsUtils.isRegular ( stock.getItem () ) && ( stock.getQuantityBig () != 0 || stock.getQuantityMedium () != 0 || stock.getQuantitySmall () != 0 )
					|| ( ItemsUtils.isAsset ( stock.getItem () ) && stock.getExistence () != null ) )
				// The client stock count contains at least one quantity, add it to the client stock count list
				stocks.add ( stock );
			}
		// Check if there is at least one valid client stock count
		if ( stocks.isEmpty () )
			// No client stock count
			return false;
		else
			// At least one client stock count
			return true;
	}
	
	/**
	 * Indicates whether the client stock count has new / unsaved modifications.<br>
	 * This method iterates over the list of all selected client stock counts and compares them with the client stock count details list, along with the client stock count note.<br>
	 * <b>Note : </b> This method always returns false if there are no previously stored client stock count details.
	 * 
	 * @return	Boolean stating if there are new modifications.
	 */
	private boolean hasModifications () {
		// Check if there is a previously stored client stock count
		if ( clientStockHeader == null || clientStockDetails == null || clientAssets == null || selectedStocks == null )
			// No saved client stock count to match to
			return false;
		// Compare the client stock count note
		if ( ! note.equals ( clientStockHeader.getNote () ) )
			// There is at least one modification
			return true;
		// Map the transaction details to their item codes
		Map < String , ClientStockCountDetails > _clientStockDetails = new HashMap < String , ClientStockCountDetails > ();
		Map < String , ClientAssetStatus > _clientAssetStatus = new HashMap < String , ClientAssetStatus > ();
		// Iterate over all transaction details
		for ( ClientStockCountDetails clientStockDetail : clientStockDetails )
			// Map the current transaction detail to its item code
			_clientStockDetails.put ( clientStockDetail.getItemCode () , clientStockDetail );
		// Iterate over all transaction details
		for ( ClientAssetStatus clientAssetStatus : clientAssets )
			// Map the current transaction detail to its item code
			_clientAssetStatus.put ( clientAssetStatus.getAssetCode () , clientAssetStatus );
		// Declare and initialize a list used to host the valid client stock count
		ArrayList < Stock > stockItems = new ArrayList < Stock > ();
		ArrayList < Stock > stockAssets = new ArrayList < Stock > ();
		// Iterate over all client stock counts
		for ( Stock stock : stocks ){
			stock.setQuantityBig(stock.isAvailables()? 1 :  0);
			// Check if the current client stock count has valid quantities
			if ( ItemsUtils.isRegular ( stock.getItem () ) && ( stock.getQuantityBig () != 0 || stock.getQuantityMedium () != 0 || stock.getQuantitySmall () != 0 ) )
				// The client stock count contains at least one quantity, add it to the client stock count list
				stockItems.add ( stock );
		//	if(ItemsUtils.isRegular(stock.getItem())&&  ( stock.getQuantityBig () == 0 || stock.getQuantityMedium () == 0 || stock.getQuantitySmall () == 0 )  )
			// Check if the current client stock count is an asset
			else if ( ItemsUtils.isAsset ( stock.getItem () ) && stock.getExistence () != null )
				// The client stock count contains at least one quantity, add it to the client stock count list
				stockAssets.add ( stock );
			}
		// Check if both sizes differ
		if ( stockItems.size () != _clientStockDetails.size () || stockAssets.size () != _clientAssetStatus.size () )
			// There is at least one modification
			return true;
		// Iterate over the client stock count
		for ( Stock stock : stockItems )
			if ( ItemsUtils.isRegular ( stock.getItem () ) ) {
				// Determine the corresponding transaction detail
				if ( _clientStockDetails.containsKey ( stock.getItem ().getItemCode () ) ) {
					// Retrieve a reference to the transaction detail
					ClientStockCountDetails clientStockCountDetails = _clientStockDetails.get ( stock.getItem ().getItemCode () );
					// Determine if the item expiry of the detail is null
					boolean isExpiryDetailNull = clientStockCountDetails.getItemExpiryDate () == null;
					// Determine if the item expiry of the stock is null
					boolean isExpiryStockNull = stock.getExpiryDate () == null;
					// Check for modifications
					if ( ! clientStockCountDetails.getQuantityBig ().equals (  stock.isAvailables() == true ? 1 : 0 )
							|| ! clientStockCountDetails.getQuantityMedium ().equals ( (double) stock.getQuantityMedium () )
							|| ! clientStockCountDetails.getQuantitySmall ().equals ( (double) stock.getQuantitySmall () )
							|| ! clientStockCountDetails.getPriceBig ().equals ( stock.getPriceBig () )
							|| ! clientStockCountDetails.getPriceMedium ().equals ( stock.getPriceMedium () )
							|| ! clientStockCountDetails.getPriceSmall ().equals ( stock.getPriceSmall () )
							|| ( isExpiryDetailNull != isExpiryStockNull 
							|| ( clientStockCountDetails.getItemExpiryDate () != null && ! clientStockCountDetails.getItemExpiryDate ().equals ( stock.getExpiryDate () ) ) )
							|| ! clientStockCountDetails.getIsMerchandize ().equals ( stock.isMerchandize () ? 1 : 0 ) )
						// There is at least one modification
						return true;
				} // End if
				else
					// There is at least one modification
					return true;
			} // End if
			else if ( ItemsUtils.isAsset ( stock.getItem () ) ) {
				// Determine the corresponding transaction detail
				if ( _clientAssetStatus.containsKey ( stock.getItem ().getItemCode () ) ) {
					// Retrieve a reference to the transaction detail
					ClientAssetStatus clientAssetStatus = _clientAssetStatus.get ( stock.getItem ().getItemCode () );
					if ( ! clientAssetStatus.getExistanceCode ().equals ( stock.getExistence () == null ? null : String.valueOf ( stock.getExistence ().getStatusID () ) )
							|| ! clientAssetStatus.getStatusCode ().equals ( stock.getStatus () == null ? null : String.valueOf ( stock.getStatus ().getStatusID () ) ) )
						// There is at least one modification
						return true;
				} // End if
				else
					// There is at least one modification
					return true;
			} // End else if
		// Otherwise there are no modifications
		return false;
	}
	
	/**
	 * Filters the {@link #stocks} list based on the indicated {@link #searchQuery} string and the {@link #selectedDivisionsCodes} list.<br>
	 * <b>NOTE : </b> This method does not perform any kind of integrity check what so ever !
	 * 
	 * @return	List of {@link me.SyncWise.Android.Modules.ClientStock.Stock Stock} objects filtered according to the search and divisions filter.
	 */
	@SuppressLint("DefaultLocale") 
	protected ArrayList < Stock > filter () {
		// Declare and initialize a new client stock counts list
		ArrayList < Stock > _stocks = new ArrayList < Stock > ();
		// Iterate over all the client stock counts
		for ( Stock stock : stocks ) {
			// Determine if the items list mode is PRIORITY
			// AND if the item does NOT belong to the priority items list
			if ( itemsListMode != REGULAR_ITEMS_LIST && itemsListMode != ASSETS_LIST )
				continue;
			else if ( itemsListMode == REGULAR_ITEMS_LIST && ( ! ItemsUtils.isRegular ( stock.getItem () ) ) )
				continue;
			else if ( itemsListMode == ASSETS_LIST && ( ! ItemsUtils.isAsset ( stock.getItem () ) ) )
				continue;
			
			// Check if the item is a regular
			if ( itemsListMode == REGULAR_ITEMS_LIST ) {
				// Determine if at least one of the client stock count's divisions is selected
				boolean skip = true;
				// Iterate over all client stock count's division
				for ( String divisionCode : stock.getDivisionCodes () )
					// Check if the current division is selected
					if ( selectedDivisionsCodes.contains ( divisionCode ) ) {
						// Reset flag
						skip = false;
						// Exit loop
						break;
					} // End if
				// Skip if needed
				if ( skip )
					continue;
			} // End if
			
			// Match the filter with the item code and the item description (contains)
			if ( stock.getItem ().getItemName ().toLowerCase ().contains ( searchQuery.toLowerCase () )
					|| stock.getItem ().getItemCode ().toLowerCase ().contains ( searchQuery.toLowerCase () )
					|| ( stock.getBarCodes () != null && stock.getBarCodes ().contains ( searchQuery.toLowerCase () ) ) )
				// Add the client stock count to the list
				_stocks.add ( stock );
		} // End for each
		// Return the new client stock counts list
		return _stocks;
	}
	
	/**
	 * AsyncTask helper class used to populate the client stock count items list with the appropriate items.
	 * 
	 * @author Elias
	 *
	 */
	private class PopulateList extends AsyncTask < Void , Void , ArrayList < Stock > > {
		
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
		@SuppressLint("UseSparseArrays") 
		@SuppressWarnings("unchecked")
		@Override
		protected ArrayList < Stock > doInBackground ( Void ... params ) {
			try {
				
				// Declare and initialize a cursor object used to retrieve data sets from query results
				Cursor cursor = null;
				// Declare the SQL string and selection arguments array of strings used to to query DB
				String SQL = null;
				String selectionArguments [] = null;
				// Retrieve the user, company, client and division codes
				String userCode = DatabaseUtils.getCurrentUserCode ( ClientStockDetailsActivity.this );
				String companyCode = DatabaseUtils.getCurrentCompanyCode ( ClientStockDetailsActivity.this );
				String clientCode = ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClient ().getClientCode ();
				String divisionCode = ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClientDivision ().getDivisionCode ();
				// Retrieve the visit object
				Visits visit = (Visits) getIntent ().getSerializableExtra ( VISIT );
				
				// Temporary list
				HashMap < Integer , AssetsStatus > _statuses = new HashMap < Integer , AssetsStatus > ();
				// Check if the statuses list is valid
				if ( statuses == null ) {
					// Initialize the list
					statuses = new ArrayList < AssetsStatus > ();
					// Add the default selection
//					statuses.add ( new AssetsStatus ( null , Integer.MIN_VALUE , Integer.MIN_VALUE , null , "------" , "------" , null ) );
					// Retrieve the statuses list
					statuses.addAll ( DatabaseUtils.getInstance ( ClientStockDetailsActivity.this ).getDaoSession ().getAssetsStatusDao ().queryBuilder ()
							.where ( AssetsStatusDao.Properties.StatusType.eq ( 2 ) ).list () );
					// Iterate over the statuses
					for ( int i = 1 ; i < statuses.size () ; i ++ )
						// Map the status to its code
						_statuses.put ( statuses.get ( i ).getStatusID () , statuses.get ( i ) );
				} // End if
				
				// Temporary list
				HashMap < Integer , AssetsStatus > _existences = new HashMap < Integer , AssetsStatus > ();
				// Check if the existences list is valid
				if ( existence == null ) {
					// Initialize the list
					existence = new ArrayList < AssetsStatus > ();
					// Add the default selection
//					existence.add ( new AssetsStatus ( null , Integer.MIN_VALUE , Integer.MIN_VALUE , null , "------" , "------" , null ) );
					// Retrieve the existences list
					existence.addAll ( DatabaseUtils.getInstance ( ClientStockDetailsActivity.this ).getDaoSession ().getAssetsStatusDao ().queryBuilder ()
							.where ( AssetsStatusDao.Properties.StatusType.eq ( 1 ) ).list () );
					// Iterate over the existences
					for ( int i = 1 ; i < existence.size () ; i ++ )
						// Map the existence to its code
						_existences.put ( existence.get ( i ).getStatusID () , existence.get ( i ) );
				} // End if
				
				// Check if the currency is valid
				if ( currency == null ) {
					// Retrieve the currency with the utmost priority
					// Compute the SQL query
					SQL = "SELECT C.* FROM " + CurrenciesDao.TABLENAME + " C INNER JOIN " + ClientCurrenciesDao.TABLENAME + " CC " +
							"ON C." + CurrenciesDao.Properties.CurrencyCode.columnName + " = CC." + ClientCurrenciesDao.Properties.CurrencyCode.columnName + " " +
							"AND CC." + ClientCurrenciesDao.Properties.ClientCode.columnName + " = ? " +
							"ORDER BY CC." + ClientCurrenciesDao.Properties.Priority.columnName;
					// Compute the selection arguments
					selectionArguments = new String [] { clientCode };
					// Query DB in order to retrieve the currencies
					cursor = DatabaseUtils.getInstance ( ClientStockDetailsActivity.this ).getDaoSession ().getDatabase ().rawQuery ( SQL , selectionArguments );
					// Move the cursor to the first raw
					if ( cursor.moveToFirst () ) {
						do {
							// Retrieve the current currency
							currency = DatabaseUtils.getInstance ( ClientStockDetailsActivity.this ).getDaoSession ().getCurrenciesDao ().readEntity ( cursor , 0 );
						} while ( cursor.moveToNext () );
					} // End if
					// Clear cursor
					cursor.close ();
					cursor = null;
				} // End if
				
				// So far here, the currency and company objects must all be valid objects
				// If not, cannot continue
				// Check if both the company and the currency are valid
				if ( getIntent ().getStringExtra ( CLIENT_STOCK_HEADER_CODE ) == null && currency == null ) {
					// Set flag to indicate an error
					error = true;
					// Clear the task reference
					populateList = null;
					// Exit method
					return null;
				} // End if
				
				// Declare and initialize a map used to map the client Stock details to their item codes
				Map < String , ClientStockCountDetails > _clientStockDetails = new HashMap < String , ClientStockCountDetails > ();
				// Declare and initialize a map used to map the client Stock details to their item codes
				Map < String , ClientAssetStatus > _clientAssetStatsuses = new HashMap < String , ClientAssetStatus > ();
				// Determine if there is an available transaction header code
				if ( getIntent ().getStringExtra ( CLIENT_STOCK_HEADER_CODE ) != null && clientStockHeader == null ) {
					// Try to retrieve the transaction header with the specified transaction code
					clientStockHeader = DatabaseUtils.getInstance ( ClientStockDetailsActivity.this ).getDaoSession ().getClientStockCountHeadersDao ().queryBuilder ()
							.where ( ClientStockCountHeadersDao.Properties.TransactionCode.eq ( getIntent ().getStringExtra ( CLIENT_STOCK_HEADER_CODE ) ) ).unique ();
					// Check if the transaction header is valid
					if ( clientStockHeader != null ) {
						// Refresh object
						DatabaseUtils.getInstance ( ClientStockDetailsActivity.this ).getDaoSession ().getClientStockCountHeadersDao ().refresh ( clientStockHeader );
						// Set the client stock count note
						note = clientStockHeader.getNote ();
						// Initialize the transaction details list
						clientStockDetails = new ArrayList < ClientStockCountDetails > ();
						// Retrieve the appropriate transaction details
						clientStockDetails.addAll ( DatabaseUtils.getInstance ( ClientStockDetailsActivity.this ).getDaoSession ().getClientStockCountDetailsDao ().queryBuilder ()
							.where ( ClientStockCountDetailsDao.Properties.TransactionCode.eq ( getIntent ().getStringExtra ( CLIENT_STOCK_HEADER_CODE ) ) ).list () );
						// Initialize the transaction details list
						clientAssets = new ArrayList < ClientAssetStatus > ();
						// Retrieve the appropriate client asset statuses
						clientAssets.addAll ( DatabaseUtils.getInstance ( ClientStockDetailsActivity.this ).getDaoSession ().getClientAssetStatusDao ().queryBuilder ()
								.where ( ClientAssetStatusDao.Properties.TransactionCode.eq ( getIntent ().getStringExtra ( CLIENT_STOCK_HEADER_CODE ) ) ).list () );
						// Iterate over all transaction details
						for ( ClientStockCountDetails clientStockDetail : clientStockDetails )
							// Refresh object
							DatabaseUtils.getInstance ( ClientStockDetailsActivity.this ).getDaoSession ().getClientStockCountDetailsDao ().refresh ( clientStockDetail );
						// Iterate over all transaction details
						for ( ClientAssetStatus clientAssetStatus : clientAssets )
							// Refresh object
							DatabaseUtils.getInstance ( ClientStockDetailsActivity.this ).getDaoSession ().getClientAssetStatusDao ().refresh ( clientAssetStatus );
						// Retrieve the appropriate currency
						currency = DatabaseUtils.getInstance ( ClientStockDetailsActivity.this ).getDaoSession ().getCurrenciesDao ().queryBuilder ()
							.where ( CurrenciesDao.Properties.CurrencyCode.eq ( clientStockHeader.getCurrencyCode () ) ).unique ();
					} // End if
					// Iterate over all transaction details
					for ( ClientStockCountDetails clientStockDetail : clientStockDetails )
						// Map the current transaction detail to its item code
						_clientStockDetails.put ( clientStockDetail.getItemCode () , clientStockDetail );
					// Iterate over all transaction details
					for ( ClientAssetStatus clientAssetStatus : clientAssets )
						// Map the current transaction detail to its item code
						_clientAssetStatsuses.put ( clientAssetStatus.getAssetCode () , clientAssetStatus );
				} // End if
				
				// Retrieve all divisions
				List < Divisions > allDivisions = DatabaseUtils.getInstance ( ClientStockDetailsActivity.this ).getDaoSession ().getDivisionsDao ().queryBuilder ()
						.where ( DivisionsDao.Properties.CompanyCode.eq ( companyCode ) ).list ();
				// Retrieve the divisions linked to the user
				List < Divisions > directUserDivisions = DatabaseUtils.getInstance ( ClientStockDetailsActivity.this ).getDaoSession ().getDivisionsDao ()
						.queryRaw ( " WHERE CompanyCode = ? AND DivisionCode IN ( SELECT DivisionCode FROM UserDivisions WHERE CompanyCode = ? ) " ,
								new String [] { companyCode , companyCode } );
				// Retrieve the user children division
				List < Divisions > allUserDivisions = DivisionsUtils.getChildren ( directUserDivisions , allDivisions );
				// Add the direct user divisions to the main list
				allUserDivisions.addAll ( directUserDivisions );
				
				// Initialize the map
				HashMap < String , Prices > itemPrices = new HashMap < String , Prices > ();
				// Compute the SQL query
				SQL = "SELECT P.* , CPL." + ClientPriceListsDao.Properties.Priority.columnName + " ClientPriceListPriority " +
						"FROM " + PricesDao.TABLENAME + " P INNER JOIN " + ClientPriceListsDao.TABLENAME + " CPL " +
						"ON P." + PricesDao.Properties.PriceListCode.columnName + " = CPL." + ClientPriceListsDao.Properties.PriceListCode.columnName + " " +
						"AND CPL." + ClientPriceListsDao.Properties.ClientCode.columnName + " = ? " +
						"ORDER BY P." + PricesDao.Properties.ItemCode.columnName + " , ClientPriceListPriority";
				// Compute the selection arguments
				selectionArguments = new String [] { clientCode };
				// Query DB in order to retrieve the prices
				cursor = DatabaseUtils.getInstance ( ClientStockDetailsActivity.this ).getDaoSession ().getDatabase ().rawQuery ( SQL , selectionArguments );
				// Move the cursor to the first raw
				if ( cursor.moveToFirst () ) {
					do {
						// Retrieve the current price
						Prices price = DatabaseUtils.getInstance ( ClientStockDetailsActivity.this ).getDaoSession ().getPricesDao ().readEntity ( cursor , 0 );
						// Check if the current item price is previously mapped
						if ( ! itemPrices.containsKey ( price.getItemCode () ) )
							// Map the item price
							itemPrices.put ( price.getItemCode () , price );
					} while ( cursor.moveToNext () );
				} // End if
				// Clear cursor
				cursor.close ();
				cursor = null;
				
				// Check if the divisions list is valid
				if ( divisions == null ) {
					// Initialize the divisions list
					divisions = new ArrayList < Divisions > ();
					// Display all the user divisions
					ClientStockDetailsActivity.this.divisions.addAll ( allUserDivisions );
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
				
				// Declare and initialize a list used to host the items
				List < Items > items = null;
				// Declare and initialize a map used to host the item bar codes mapped to their item codes
				HashMap < String , ArrayList < String > > _itemBarcodes = null;
				// Map the item code to the related divisions code list
				Map < String , ArrayList < String > > _itemDivisions = new HashMap < String , ArrayList < String > > ();
				
				// Check if there are saved client stock counts
				if ( retrieveStocks ) {
					// Retrieve modified calls
					stocks = (ArrayList < Stock >) ActivityInstance.readDataGson ( ClientStockDetailsActivity.this , ClientStockDetailsActivity.class.getName () , STOCK , new TypeToken < ArrayList < Stock > > () {}.getType () );
					// Initialize the list used to host the selected client stock counts
					selectedStocks = new ArrayList < Stock > ();
					// Iterate over all client stock counts
					for ( Stock stock : stocks )
		    			// Check if the current client stock count has valid quantities
		    			if ( stock.getQuantityBig () != 0 || stock.getQuantityMedium () != 0 || stock.getQuantitySmall () != 0 )
		    				// The client stock count contains at least one quantity, add it to the client stock count list
		    				selectedStocks.add ( stock );
				} // End if
				else {
					// Retrieve the client item divisions
					List < ItemDivisions > itemDivisions = EntityUtils.queryByGroup ( selectedDivisionsCodes ,
							DatabaseUtils.getInstance ( ClientStockDetailsActivity.this ).getDaoSession ().getItemDivisionsDao () ,
							ItemDivisionsDao.Properties.DivisionCode );
					// Declare and initialize a list of strings used to host the item codes
					List < String > itemCodes = new ArrayList < String > ();
					// Iterate over all the item divisions
					for ( ItemDivisions item : itemDivisions ) {
						// Add the item code to the list
						itemCodes.add ( item.getItemCode () );
						// Check if the current item code is already mapped
						if ( _itemDivisions.containsKey ( item.getItemCode () ) )
							// Map the current division to the item code
							_itemDivisions.get ( item.getItemCode () ).add ( item.getDivisionCode () );
						// Otherwise the current item code is NOT mapped
						else {
							// Declare and initialize a divisions list
							ArrayList < String > list = new ArrayList < String > ();
							// Add the division to the list
							list.add ( item.getDivisionCode () );
							// Map the divisions list to the item code
							_itemDivisions.put ( item.getItemCode () , list );
						} // End else
					} // End for each
					
					// Retrieve the client related items
					items = EntityUtils.queryByGroup ( itemCodes ,
							DatabaseUtils.getInstance ( ClientStockDetailsActivity.this ).getDaoSession ().getItemsDao () ,
							ItemsDao.Properties.ItemCode );
					
					// Add all assets
					items.addAll ( DatabaseUtils.getInstance ( ClientStockDetailsActivity.this ).getDaoSession ().getItemsDao ().queryBuilder ()
							.where ( ItemsDao.Properties.ItemType.eq ( 2 ) ).list () );
					
					// Retrieve the item barcodes
					List < ItemBarcodes > itemBarcodes = EntityUtils.queryByGroup ( itemCodes ,
							DatabaseUtils.getInstance ( ClientStockDetailsActivity.this ).getDaoSession ().getItemBarcodesDao () ,
							ItemBarcodesDao.Properties.ItemCode );
					// Map the barcodes to the item codes
					_itemBarcodes = new HashMap < String , ArrayList < String > > ();
					// Iterate over all barcodes
					for ( ItemBarcodes itemBarcode : itemBarcodes ) {
						// Check if the barcode is valid
						if ( TextUtils.isEmpty ( itemBarcode.getItemBarcode () ) )
							// Invalid barcode
							continue;
						// Determine if the item is previously mapped
						if ( _itemBarcodes.containsKey ( itemBarcode.getItemCode () ) )
							// Simply add the barcode to the item's barcode list
							_itemBarcodes.get ( itemBarcode.getItemCode () ).add ( itemBarcode.getItemBarcode () );
						else {
							// The item is not mapped
							// Declare and initialize a barcode list
							ArrayList < String > list = new ArrayList < String > ();
							// Add the current barcode to the list
							list.add ( itemBarcode.getItemBarcode () );
							// Map the list to the item
							_itemBarcodes.put ( itemBarcode.getItemCode () , list );
						} // End else
					} // End for each
					
					// Retrieve all the units
					List < Units > units = DatabaseUtils.getInstance ( ClientStockDetailsActivity.this ).getDaoSession ().getUnitsDao ().loadAll ();
					// Map all the Units using their codes
					Map < String , Units > _units = new HashMap < String , Units > ();
					// Iterate over all UOM
					for ( Units unit : units )
						// Map the unit to its code
						_units.put ( unit.getUnitCode () , unit );
					
					// Retrieve all the taxes
					List < Taxes > taxes = DatabaseUtils.getInstance ( ClientStockDetailsActivity.this ).getDaoSession ().getTaxesDao ().loadAll ();
					// Declare and initialize a map of taxes
					Map < String , Taxes > _taxes = new HashMap < String , Taxes > ();
					// Iterate over all taxes
					for ( Taxes tax : taxes )
						// Map the tax object to its tax code
						_taxes.put ( tax.getTaxCode () , tax );
					
					// Retrieve the client must stock list
					List < ClientMustStockList > clientMustStockList = DatabaseUtils.getInstance ( ClientStockDetailsActivity.this ).getDaoSession ().getClientMustStockListDao ()
							.queryRaw ( " WHERE ( SubjectCode = ? AND DivisionCode = ? AND CompanyCode = ? ) OR ( SubjectCode IN ( " +
									"SELECT ClientPropertyValueCode FROM ClientProperties WHERE ClientCode = ? AND DivisionCode = ? AND CompanyCode = ? ) ) " ,
									new String [] { clientCode ,
									divisionCode ,
									companyCode ,
									clientCode ,
									divisionCode ,
									companyCode } );
					// Get the item codes of the must stock list
					ArrayList < String > mustStockListItems = new ArrayList < String > ();
					for ( ClientMustStockList mustStockItem : clientMustStockList )
						mustStockListItems.add ( mustStockItem.getItemCode () );
						
					// Retrieve the client item history
					List < ClientItemHistory > clientItemHistory = DatabaseUtils.getInstance ( ClientStockDetailsActivity.this ).getDaoSession ().getClientItemHistoryDao ().queryBuilder ()
							.where ( ClientItemHistoryDao.Properties.ClientCode.eq ( clientCode ) ,
									ClientItemHistoryDao.Properties.DivisionCode.eq ( DatabaseUtils.getCurrentDivisionCode ( ClientStockDetailsActivity.this ) ) ,
									ClientItemHistoryDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( ClientStockDetailsActivity.this ) ) ).list ();
					// Get the item codes of the item history
					ArrayList < String > clientItemHistoryCodes = new ArrayList < String > ();
					for ( ClientItemHistory itemHistory : clientItemHistory )
						clientItemHistoryCodes.add ( itemHistory.getItemCode () );
					
					// Determine if this is an edit
					boolean isEdit = getIntent ().getBooleanExtra ( IS_EDIT , false );
					// Determine if the MSL confirmation is enforced
					boolean isMSLConfirmationRequired = PermissionsUtils.getEnforceMSLConfirmation ( ClientStockDetailsActivity.this , userCode , companyCode );
					// Determine if the current client has previous client stock count
					boolean hasPreviousOrders = visit == null ? false : DatabaseUtils.getInstance ( ClientStockDetailsActivity.this ).getDaoSession ().getClientStockCountHeadersDao ().queryBuilder ()
							.where ( ClientStockCountHeadersDao.Properties.JourneyCode.eq ( visit.getJourneyCode () ) ,
									ClientStockCountHeadersDao.Properties.ClientCode.eq ( clientCode ) ,
									ClientStockCountHeadersDao.Properties.DivisionCode.eq ( divisionCode ) ,
									ClientStockCountHeadersDao.Properties.CompanyCode.eq ( companyCode ) ).count () > 0;
					// Initialize the lists used to host the client stock counts and the selected client stock counts
					stocks = new ArrayList < Stock > ();
					selectedStocks = new ArrayList < Stock > ();
					// Iterate over all items
					for ( Items item : items ) {
						// Check if the item type is valid
						if ( item.getItemType () == null )
							// Skip the current item
							continue;
						// Check if current item has a valid unit of measurement
						if ( _units.get ( item.getUnitCode () ) == null )
							// Skip the current item
							continue;
						// Create a new client stock count using the current item and its appropriate unit of measurement
						Stock stock = new Stock ( item , _units.get ( item.getUnitCode () ) );
						// Set the divisions codes list
						stock.setDivisionCodes ( _itemDivisions.get ( item.getItemCode () ) );
						// Check that the division codes list is valid
						if ( stock.getDivisionCodes () == null )
							// Initialize the division codes list
							stock.setDivisionCodes ( new ArrayList < String > () );
						// Set the item bar codes list
						stock.setBarCodes ( _itemBarcodes.get ( item.getItemCode () ) );
						// Set the client stock count prices
						if ( itemPrices.containsKey ( stock.getItem ().getItemCode () ) ) {
							Prices stockPrices = itemPrices.get ( stock.getItem ().getItemCode () );
							stock.setPriceBig ( stockPrices.getPriceBig () );
							stock.setPriceMedium ( stockPrices.getPriceMedium () );
							stock.setPriceSmall ( stockPrices.getPriceSmall () );
						} // End if
						// Set the item tax
						if ( ItemsUtils.isTaxable ( stock.getItem () ) && _taxes.get ( stock.getItem ().getTaxCode () ) != null )
							stock.setTax ( _taxes.get ( stock.getItem ().getTaxCode () ).getTaxPercentage () );
						// Determine if the item is a must stock list
						if ( mustStockListItems.contains ( item.getItemCode () ) ) {
							// Indicate that the item is a must stock item
							stock.setMustStockList ( true );
							// Confirm the item if this is an edit or if previous client stock counts were issued
							stock.setConfirmed ( hasPreviousOrders || ( ! isMSLConfirmationRequired ) || isEdit );
						} // End if
						// Otherwise check if the item is a history item
						else if ( clientItemHistoryCodes.contains ( item.getItemCode () ) )
							// Indicate that the item is a must stock item
							stock.setHistory ( true );
						// Check if there is a previously saved transaction detail for the current item
						if ( _clientStockDetails.containsKey ( stock.getItem ().getItemCode () ) ) {
							// Retrieve a reference to the transaction detail
							ClientStockCountDetails clientStockDetails = _clientStockDetails.get ( stock.getItem ().getItemCode () );
							// Set the client stock count quantities
							stock.setQuantityBig ( clientStockDetails.getQuantityBig ().intValue () );
							stock.setQuantityMedium ( clientStockDetails.getQuantityMedium ().intValue () );
							stock.setQuantitySmall ((double) clientStockDetails.getQuantitySmall () );
							// Set the client stock count prices
							stock.setPriceBig ( clientStockDetails.getPriceBig () );
							stock.setPriceMedium ( clientStockDetails.getPriceMedium () );
							stock.setPriceSmall ( clientStockDetails.getPriceSmall () );
							// Set the client stock count shelf prices
							stock.setShelfPriceBig( clientStockDetails.getShelfPriceBig() );
							stock.setShelfPriceMedium( clientStockDetails.getShelfPriceMedium() );
							stock.setShelfPriceSmall( clientStockDetails.getShelfPriceSmall() );
							
							// Set the client stock count data
							stock.setExpiryDate ( clientStockDetails.getItemExpiryDate () );
							stock.setMerchandize ( clientStockDetails.getIsMerchandize () == 1 );
							stock.setAvailables(  clientStockDetails.getQuantityBig().intValue() == 1 );
							stock.setOverSixMonths ( clientStockDetails.getIsOverSixMonths () == 1 );
							// Add the current client stock count to the list of selected client stock counts
							selectedStocks.add ( stock );
						} // End if
						// Check if there is a previously saved transaction detail for the current item
						else if ( _clientAssetStatsuses.containsKey ( stock.getItem ().getItemCode () ) ) {
							// Retrieve a reference to the transaction detail
							ClientAssetStatus clientAssetStatus = _clientAssetStatsuses.get ( stock.getItem ().getItemCode () );
							try {
								// Set the client asset existence
								stock.setExistence ( _existences.get ( Integer.parseInt ( clientAssetStatus.getExistanceCode () ) ) );
							} catch ( Exception exception ) {
								// Invalid parsing, the asset has no existence
								stock.setExistence ( null );
							} // End of try-catch block
							try {
								// Set the client asset status
								stock.setStatus ( _statuses.get ( Integer.parseInt ( clientAssetStatus.getStatusCode () ) ) );
							} catch ( Exception exception ) {
								// Invalid parsing, the asset has no status
								stock.setStatus ( null );
							} // End of try-catch block
							// Add the current client stock count to the list of selected client stock counts
							selectedStocks.add ( stock );
						} // End if
						// Add the current item in the client stock counts list
						stocks.add ( stock );
					} // End for each
				} // End else
				
				// Sort the items, and put the MSL first , then the history items, then the remaining items
				Collections.sort ( stocks , new Comparator < Stock > () {
					@Override
					public int compare ( Stock stock1 , Stock stock2 ) {
						// Sort the items 
						if ( stock1.isMustStockList () && ! stock2.isMustStockList () )
							return -1;
						else if ( ! stock1.isMustStockList () && stock2.isMustStockList () )
							return 1;
						else if ( stock1.isMustStockList () && stock2.isMustStockList () )
							return stock1.getItem ().getItemName ().compareToIgnoreCase ( stock2.getItem ().getItemName () );
						
						// Otherwise both are not must stock list
						if ( stock1.isHistory () && ! stock2.isHistory () )
							return -1;
						else if ( ! stock1.isHistory () && stock2.isHistory () )
							return 1;
						else
							return stock1.getItem ().getItemName ().compareToIgnoreCase ( stock2.getItem ().getItemName () );
					}
				} );
				
				// Clear the task reference
				populateList = null;
				
				// Determine if the request code is INFO
				if ( requestCode != REQUEST_CODE_INFO )
					// Compute and return the filtered list based on the provided search query and selected divisions
					return filter ();
				// Otherwise the request code is INFO
				else {
					// Initialize the client stock count details
					initializeDetails ();
					return null;
				} // End else
			
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
		protected void onPostExecute ( ArrayList < Stock > stocks ) {
			// Check if the activity has ended
			if ( activityEnded )
				// Do nothing
				return;
			// Determine if the activity must be finished
			if ( error ) {
				// Indicate that the activity cannot be displayed
				new AppToast ( ClientStockDetailsActivity.this )
					.setIcon ( R.drawable.warning )
					.setText ( AppResources.getInstance ( ClientStockDetailsActivity.this ).getString ( ClientStockDetailsActivity.this , R.string.missing_client_currency_price_list_message ) )
					.setDuration ( Toast.LENGTH_LONG )
					.show ();
				// Initialize the client stock counts list
				ClientStockDetailsActivity.this.stocks = new ArrayList < Stock > ();
				// Exit method
				return;
			} // End if
			
			// Retrieve a reference to the secondary view
			View secondary = findViewById ( R.id.layout_stock_details );
			// Display the secondary view accordingly
			secondary.setVisibility ( displayStockDetails ? View.VISIBLE : View.GONE );
    		// Enable the main list accordingly
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( ! displayStockDetails );
    		// Determine if an client stock count is undergoing any modifications
    		if ( displayStockDetails )
	    		// Restore the secondary view
    			initializeSecondaryView ( true );
    		
    		// Retrieve a reference to the tertiary view
    		View tertiary = findViewById ( R.id.layout_stock_note );
			// Display the tertiary view accordingly
    		tertiary.setVisibility ( displayStockNote ? View.VISIBLE : View.GONE );
    		// Enable the main list accordingly
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( ! displayStockNote );
    		// Determine if the client stock count note is undergoing any modifications
    		if ( displayStockNote )
	    		// Restore the tertiary view
	    		initializeTertiaryView ( true );
    		
	    	// Declare and initialize a new existence adapter used to populate the existence spinner
    		AssetStatusAdapter existenceAdapter = new AssetStatusAdapter ( ClientStockDetailsActivity.this , android.R.layout.simple_spinner_item , existence );
			// Sets the layout resource to create the drop down views
    		existenceAdapter.setDropDownViewResource ( R.layout.data_management_activity_connection_item );
    		// Set the spinner adapter
    		( (Spinner) secondary.findViewById ( R.id.spinner_existence ) ).setAdapter ( existenceAdapter );
    		
	    	// Declare and initialize a new status adapter used to populate the status spinner
    		AssetStatusAdapter statusesAdapter = new AssetStatusAdapter ( ClientStockDetailsActivity.this , android.R.layout.simple_spinner_item , statuses );
			// Sets the layout resource to create the drop down views
    		statusesAdapter.setDropDownViewResource ( R.layout.data_management_activity_connection_item );
    		// Set the spinner adapter
    		( (Spinner) secondary.findViewById ( R.id.spinner_status ) ).setAdapter ( statusesAdapter );
    		
			// Determine if the request code is INFO
			if ( requestCode != REQUEST_CODE_INFO )
				// Set a new adapter
				setListAdapter ( getClientStockDetailsAdapter ( R.layout.sales_order_details_activity_item , new ArrayList < Stock > ( stocks ) , currency.getCurrencyRounding () , true,true ) );
			// Otherwise the request code is INFO
			else {
				// Compute labels
				String infoLabel = AppResources.getInstance ( ClientStockDetailsActivity.this ).getString ( ClientStockDetailsActivity.this , R.string.client_stock_count_details_label );
				if ( getIntent ().getIntExtra ( ITEMS_LIST_MODE , 0 ) == ClientStockDetailsActivity.ASSETS_LIST )
					infoLabel = AppResources.getInstance ( ClientStockDetailsActivity.this ).getString ( ClientStockDetailsActivity.this , R.string.client_asset_details_label );
				String detailsLabel = AppResources.getInstance ( ClientStockDetailsActivity.this ).getString ( ClientStockDetailsActivity.this , R.string.client_stock_count_products_label );
				if ( getIntent ().getIntExtra ( ITEMS_LIST_MODE , 0 ) == ClientStockDetailsActivity.ASSETS_LIST )
					detailsLabel = AppResources.getInstance ( ClientStockDetailsActivity.this ).getString ( ClientStockDetailsActivity.this , R.string.client_asset_products_label );
				
				// Declare and initialize a multiple list adapter
				MultipleListAdapter adapter = new MultipleListAdapter ( ClientStockDetailsActivity.this );
				// Add the client stock count details adapter
				adapter.addSection ( new Section ( infoLabel , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
						new StockInfoAdapter ( ClientStockDetailsActivity.this , R.layout.order_info_item , details ) );
				// Add the client stock count adapter
				adapter.addSection ( new Section ( detailsLabel , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
						getClientStockDetailsAdapter ( R.layout.sales_order_details_activity_item , selectedStocks , currency.getCurrencyRounding () , ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) ,false) );
				// Set the list adapter
				setListAdapter ( adapter );
			} // End else
    		// Refresh the action bar
    		invalidateOptionsMenu ();
		}
		
	}
	
	/*
	 * Called when the focus state of a view has changed.
	 *
	 * @see android.view.View.OnFocusChangeListener#onFocusChange(android.view.View, boolean)
	 */
	@Override
	public void onFocusChange ( final View view , boolean hasFocus ) {
		// Check if the view is and edit text
		if ( ! ( view instanceof EditText ) )
			// Invalid view
			return;
		try {
			// Schedule to run a thread after 250 milliseconds
			view.postDelayed ( new Runnable () {
				@Override
				public void run () {
					// Check if the edit text has focus AND the text is equal to zero
					if ( view.hasFocus () && ( (EditText) view ).getText ().toString ().equals ( "0" ) )
						// Clear the edit text
						( (EditText) view ).setText ( "" );
				}
			} , 250 );

		} catch ( Exception exception ) {
			// Do nothing
		} // End of try-catch block
	}
	
	/**
	 * Performs all the required initialization for the client stock count details :
	 * <ul>
	 * <li>The {@link #selectedStocks} list is populated.</li>
	 * <li>The {@link #details} list is populated.</li>
	 * </ul>
	 */
	protected void initializeDetails () {
		// Compute the client stock count details
		double netValue = 0;
//		double taxes = 0;
		double totalValue = 0;
		// Iterate over all the selected client stock counts
		for ( Stock stock : selectedStocks ) {
			// Compute the current net value
			double _netValue = stock.getQuantityBig () * stock.getPriceBig ()
					+ stock.getQuantityMedium () * stock.getPriceMedium ()
					+ stock.getQuantitySmall () * stock.getPriceSmall ();
			// Compute the current tax value
			double _taxes = 0;
			// Check if the item is taxable
			if ( stock.getTax () != 0 )
				// Compute the current taxes
				_taxes = _netValue * stock.getTax () / 100;
			// Compute the current total value
			double _totalValue = _netValue + _taxes;
			// Update all the values
			netValue += _netValue;
//			taxes += _taxes;
			totalValue += _totalValue;
		} // End for each
		// Compute the money formatter pattern
		StringBuilder pattern = new StringBuilder ();
		pattern.append ( " #,##0" );
		// Check if the currency has a rounding value
		if ( currency.getCurrencyRounding () >= 1 ) {
			pattern.append ( ".0" );
			for ( int i = 1 ; i < currency.getCurrencyRounding () ; i ++ )
				pattern.append ( "0" );
		} // End if
		// Declare and initialize a money format
		DecimalFormat moneyFormat = new DecimalFormat ( pattern.toString () );
		// Initialize the list of client stock count details
		details = new ArrayList < StockInfo > ();
		// Populate the details list
		details.add ( new StockInfo ( StockInfo.ID.NOTE , AppResources.getInstance ( this ).getString ( this , R.string.sales_order_note_label ) , note ) );
		details.add ( new StockInfo ( StockInfo.ID.NET_VALUE , AppResources.getInstance ( this ).getString ( this , R.string.net_amount_label ) , moneyFormat.format ( netValue ) ) );
//		details.add ( new StockInfo ( StockInfo.ID.TAXES , AppResources.getInstance ( this ).getString ( this , R.string.taxes_label ) , moneyFormat.format ( taxes ) ) );
		details.add ( new StockInfo ( StockInfo.ID.TOTAL_VALUE , AppResources.getInstance ( this ).getString ( this , R.string.total_value_label ) , moneyFormat.format ( totalValue ) ) );
	}
	
	/**
	 * Refreshes / recomputes all the client stock count details (if any).
	 */
	private void refreshDetails () {
		// Check if the selected client stock counts and client stock count details lists are valid
		if ( selectedStocks == null || details == null )
			// Invalid list, do nothing
			return;
		
		// Compute the client stock count details
		double netValue = 0;
		double taxes = 0;
		double totalValue = 0;
		// Iterate over all the selected client stock counts
		for ( Stock stock : selectedStocks ) {
			// Compute the current net value
			double _netValue = stock.getQuantityBig () * stock.getPriceBig ()
					+ stock.getQuantityMedium () * stock.getPriceMedium ()
					+ stock.getQuantitySmall () * stock.getPriceSmall ();
			// Compute the current tax value
			double _taxes = 0;
			// Check if the item is taxable
			if ( stock.getTax () != 0 )
				// Compute the current taxes
				_taxes = _netValue * stock.getTax () / 100;
			// Compute the current total value
			double _totalValue = _netValue + _taxes;
			// Update all the values
			netValue += _netValue;
			taxes += _taxes;
			totalValue += _totalValue;
		} // End for each
		// Compute the money formatter pattern
		StringBuilder pattern = new StringBuilder ();
		pattern.append ( " #,##0" );
		// Check if the currency has a rounding value
		if ( currency.getCurrencyRounding () >= 1 ) {
			pattern.append ( ".0" );
			for ( int i = 1 ; i < currency.getCurrencyRounding () ; i ++ )
				pattern.append ( "0" );
		} // End if
		// Declare and initialize a money format
		DecimalFormat moneyFormat = new DecimalFormat ( pattern.toString () );
		
		// Iterate over all details
		for ( StockInfo detail : details ) {
			// Check if the current detail is about the client stock count net value
			if ( detail.getId () == StockInfo.ID.NET_VALUE ) {
				// Update the client stock count net value
				detail.setValue ( moneyFormat.format ( netValue ) );
			} // End if
			// Check if the current detail is about the client stock count taxes
			else if ( detail.getId () == StockInfo.ID.TAXES ) {
				// Update the client stock count taxes
				detail.setValue ( moneyFormat.format ( taxes ) );
			} // End else if
			// Check if the current detail is about the client stock count total value
			else if ( detail.getId () == StockInfo.ID.TOTAL_VALUE ) {
				// Update the client stock count total value
				detail.setValue ( moneyFormat.format ( totalValue ) );
			} // End else if
		} // End for each
	}
	
	/**
	 * AsyncTask helper class used to filter the list.
	 * 
	 * @author Elias
	 *
	 */
	private class FilterList extends AsyncTask < Void , Void , ArrayList < Stock > > {
		
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
		protected ArrayList < Stock > doInBackground ( Void ... params ) {
			// Check if the list of client stock counts is valid
			if ( stocks == null || stocks.isEmpty () )
				// Invalid client stock counts list
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
		protected void onPostExecute ( ArrayList < Stock > stocks ) {
			// Check if the request code is INFO
			if ( requestCode == REQUEST_CODE_INFO )
				// Cannot filter, do nothing
				return;
			// Check if the result is valid
			if ( stocks == null )
				// Invalid result, do nothing
				return;
			// Check if the search query has changed
			if ( ! ClientStockDetailsActivity.this.searchQuery.equals ( searchQuery ) )
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
				// Clear the previous list of client stock counts
				( (ArrayAdapter < Stock >) getListAdapter () ).clear ();
				// Add the new filtered list of client stock counts
				( (ArrayAdapter < Stock >) getListAdapter () ).addAll ( stocks );
				// Notifies the attached observers that the underlying data has been changed
				( (ArrayAdapter < Stock >) getListAdapter () ).notifyDataSetChanged ();
			} catch ( Exception exception ) {
				// Cannot cast to array adapter of client stock counts objects
			} // End try-catch block
		}
		
	}
	
	
	
	
	
	
	
	
//	/**
//	 * AsyncTask helper class used to search for the scanned item barcode.
//	 * 
//	 * @author Ahmad
//	 *
//	 */
//	protected class ItemBarcodeScan extends AsyncTask < Void , Void , Void > {
//		
//		/**
//		 * String holding the scanned barcode.
//		 */
//		private String barcode;
//		
//		/**
//		 * Flag used to indicate if the barcode was not found.
//		 */
//		private boolean barcodeNotFound;
//		
//		/**
//		 * Flag used to indicate if an error occurred.
//		 */
//		private boolean error;
//	   	
//		/**
//		 * Reference to the item whose barcode was scanned. 
//		 */
//		private ItemBarcodes item;
//		/**
//		 * Constructor.
//		 * 
//		 * @param barcode	String holding the scanned barcode.
//		 */
//		public ItemBarcodeScan ( String barcode ) {
//			// Superclass method call
//			super ();
//			// Initialize attribute
//			this.barcode = barcode;
//		}
//		
//		/*
//		 * Method to perform a computation on a background thread.
//		 * 
//		 * @see android.os.AsyncTask#doInBackground(Params[])
//		 */
//		@Override
//		protected Void doInBackground ( Void ... params ) {
//			try {
//				// Check if the barcode is valid
//				if ( barcode == null )
//					// Invalid barcode
//					return null;
//				// Otherwise the barcode is valid
//				// Search for the appropriate client barcode
//				 item = DatabaseUtils.getInstance ( ClientStockDetailsActivity.this ).getDaoSession ().getItemBarcodesDao ().queryBuilder ()
//						.where ( ItemBarcodesDao.Properties.ItemBarcode.eq ( barcode ) ).unique ();
//				// Check if the client barcode is valid
//				if ( item == null ) {
//					// Set flag
//					barcodeNotFound = true;
//					// Barcode not found
//					return null;
//				} // End if
//				
////				// Retrieve the week number per cycle
////			 
////				item = DatabaseUtils.getInstance ( ClientStockDetailsActivity.this ).getDaoSession ().getItemsDao().queryBuilder ()
////						.where ( ItemsDao.Properties.ItemCode.eq ( itemBarcode.getItemCode() ) ,
////								ItemsDao.Properties.CompanyCode.eq ( itemBarcode.getCompanyCode () ) ).unique ();
////				
//				 
//				
//				 
//				 
//				
//				return null;
//			} catch ( Exception exception ) {
//				// An error occurred
//				error = true;
//			} // End of try-catch block
//			return null;
//		}
//		
//		/*
//		 * Runs on the UI thread after doInBackground(Params...).
//		 * 
//		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
//		 */
//		@Override
//		protected void onPostExecute ( Void arg ) {
//			try {
//				// Check if an error occurred
//				if ( error )
//					// Indicate that an error occurred
//					Baguette.showText ( ClientStockDetailsActivity.this , AppResources.getInstance ( ClientStockDetailsActivity.this ).getString ( ClientStockDetailsActivity.this , R.string.item_barcode_scan_error_message ) , Baguette.BackgroundColor.RED );				
//				// Check if the barcode was not found
//				else if ( barcodeNotFound )
//					// Indicate that the barcode was not found
////					Baguette.showText ( JourneyActivity.this , AppResources.getInstance ( JourneyActivity.this ).getString ( JourneyActivity.this , R.string.client_barcode_not_found_message ) , Baguette.BackgroundColor.BLUE );
//					AppDialog.getInstance ().displayAlert ( ClientStockDetailsActivity.this ,
//							AppResources.getInstance ( ClientStockDetailsActivity.this ).getString ( ClientStockDetailsActivity.this , R.string.warning_label ) , 
//							AppResources.getInstance ( ClientStockDetailsActivity.this ).getString ( ClientStockDetailsActivity.this , R.string.item_barcode_not_found_message ) , 
//							ButtonsType.OK , null );
//				// Check if the call is a scheduled call
//				else if ( item!=null )
//					// Perform a scheduled client call
//				//	weeklySection.performScheduledBarcodedClientCall ( client , clientDivision );
//				{
//					searchView.setQuery ( item == null ? "" : item.getItemBarcode() , false );
//					// Expand the SearchView
//					searchView.setIconified ( false );
//				}
//					
//			} catch ( Exception exception ) {
//				// An error occurred
//				// Indicate that an error occurred
//				Baguette.showText ( ClientStockDetailsActivity.this , AppResources.getInstance ( ClientStockDetailsActivity.this ).getString ( ClientStockDetailsActivity.this , R.string.item_barcode_not_found_message ) , Baguette.BackgroundColor.RED );
//			} finally {
//				// Clear variables
//				barcode = null;
//			 
//			} // End of try-catch-fially block
//		}
//		
//	}
	
}