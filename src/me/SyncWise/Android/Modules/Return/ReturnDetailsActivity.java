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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
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

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.AppToast;
import me.SyncWise.Android.DateTime;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Vibration;
import me.SyncWise.Android.Database.ClientCurrenciesDao;
import me.SyncWise.Android.Database.ClientPriceListsDao;
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

import me.SyncWise.Android.Database.Prices;
import me.SyncWise.Android.Database.PricesDao;
import me.SyncWise.Android.Database.Reasons;
import me.SyncWise.Android.Database.ReasonsDao;
import me.SyncWise.Android.Database.ReasonsUtils;
import me.SyncWise.Android.Database.StatusUtils;
import me.SyncWise.Android.Database.Taxes;
import me.SyncWise.Android.Database.TransactionDetails;
import me.SyncWise.Android.Database.TransactionDetailsDao;
import me.SyncWise.Android.Database.TransactionHeaders;
import me.SyncWise.Android.Database.TransactionHeadersDao;
import me.SyncWise.Android.Database.Units;
import me.SyncWise.Android.Database.Users;
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Database.Vehicles;
import me.SyncWise.Android.Database.VehiclesStock;
import me.SyncWise.Android.Database.VehiclesStockDao;
import me.SyncWise.Android.Database.Visits;
import me.SyncWise.Android.Gson.BaseTimerListActivity;
import me.SyncWise.Android.Modules.FilterDivisions.FilterDivisionsActivity;
import me.SyncWise.Android.Modules.Journey.Call;

import me.SyncWise.Android.Modules.Printing.PrintingActivity;

import me.SyncWise.Android.SectionsList.MultipleListAdapter;
import me.SyncWise.Android.SectionsList.Section;
import me.SyncWise.Android.Utilities.ActivityInstance;
import me.SyncWise.Android.Widgets.Baguette;
import me.SyncWise.Android.Widgets.CustomLinearLayout;
import me.SyncWise.Android.Widgets.NumberPicker;
import me.SyncWise.Android.Widgets.QuickAction.ActionItem;
import me.SyncWise.Android.Widgets.QuickAction.QuickAction;

/**
 * Activity implemented to perform, view or edit a return.
 * 
 * @author Elias
 * @sw.todo <b>Return Details Activity Implementation :</b><br>
 * Simply add this class in the {@code AndroidManifest.xml} file along with the permission :<br>
 * {@code <uses-permission android:name="android.permission.VIBRATE"/>}<br>
 * AND disable activity recreation for orientation events by adding the following to the activity tag in the manifest file :<br>
 * {@code android:configChanges="orientation|screenSize"}
 * 
 */
public class ReturnDetailsActivity extends BaseTimerListActivity implements SearchView.OnQueryTextListener , QuickAction.OnActionItemClickListener , OnDateSetListener , OnClickListener , OnFocusChangeListener , TextWatcher {

	/**
	 * Boolean used to indicate if the activity is displayed on the screen.
	 */
	public static boolean isDisplayed;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #requestCode}.
	 */
	public static final String REQUEST_CODE = ReturnDetailsActivity.class.getName () + ".REQUEST_CODE";
	/**
	 * Constant integer holding the request code used to print orders.
	 */
	private static final int REQUEST_CODE_PRINT = 10;
	/**
	 * Integer used to host the request code.
	 * @see #REQUEST_CODE_NEW
	 * @see #REQUEST_CODE_INFO
	 */
	protected int requestCode;
	
	/**
	 * Constant integer holding the request code to create a new return.
	 */
	public static final int REQUEST_CODE_NEW = 1;
	
	/**
	 * Constant integer holding the request code to display the return info.<br>
	 * The moved items should be displayed.
	 */
	public static final int REQUEST_CODE_INFO = 2;
	
	/**
	 * Constant integer holding the request code to display the return details.<br>
	 * The return details (tax, discount, total, ...) should be displayed.
	 */
	public static final int REQUEST_CODE_DETAILS = 3;
	
	/**
	 * Bundle key used to put/retrieve the content of the current call.
	 */
	public static final String CALL = ReturnDetailsActivity.class.getName () + ".CALL";
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #visit}.
	 */
	public static final String VISIT = ReturnDetailsActivity.class.getName () + ".VISIT";

	/**
	 * Bundle key used to put/retrieve the content of the edit flag.
	 */
	public static final String IS_EDIT = ReturnDetailsActivity.class.getName () + ".IS_EDIT";
	
	/**
	 * Bundle key used to put/retrieve the content of the view only flag.
	 */
	public static final String IS_VIEW_ONLY = ReturnDetailsActivity.class.getName () + ".IS_VIEW_ONLY";
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #displayTransactionDetails}.
	 */
	private static final String DISPLAY_TRANSACTION_DETAILS = ReturnDetailsActivity.class.getName () + ".DISPLAY_TRANSACTION_DETAILS";
	/**
	 * Bundle key used to put/retrieve the content of {@link #displayPrintingConfirmation}.
	 */
	private static final String PRINTING_TRANSACTION_CODE = ReturnDetailsActivity.class.getName () + ".PRINTING_TRANSACTION_CODE";
	
	/**
	 * String used to host the transaction code of the transaction to print.
	 */
	private String printingTransactionCode;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #displayPrintingConfirmation}.
	 */
	private static final String DISPLAY_PRINTING_CONFIRMATION = ReturnDetailsActivity.class.getName () + ".DISPLAY_PRINTING_CONFIRMATION";
	
	/**
	 * Boolean used to determine whether to display the printing confirmation.
	 */
	private boolean displayPrintingConfirmation;
	
	/**
	 * Boolean used to determine whether to display the transaction details UI or not.<br>
	 * This boolean is mainly used to save the return state.
	 */
	protected boolean displayTransactionDetails;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #displayPasscode}.
	 */
	private static final String DISPLAY_PASSCODE = ReturnDetailsActivity.class.getName () + ".DISPLAY_PASSCODE";
	
	/**
	 * Boolean used to determine whether to display the passcode UI or not.
	 */
	protected boolean displayPasscode;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #returnModification}.
	 */
	private static final String RETURN_MODIFICATION = ReturnDetailsActivity.class.getName () + ".RETURN_MODIFICATION";
	
	/**
	 * Reference to the {@link me.SyncWise.Android.Modules.Return.Return Return} object being modified.
	 */
	protected Return returnModification;
	
	/**
	 * Bundle key used to put/retrieve the content of the transaction header code.<br>
	 * This is used mainly to edit a return.
	 */
	public static final String TRANSACTION_HEADER_CODE = ReturnDetailsActivity.class.getName () + ".TRANSACTION_HEADER_CODE";
	
	/**
	 * List of {@link me.SyncWise.Android.Database.Divisions Divisions} objects used to host all the available objects for the current return.
	 */
	protected ArrayList < Divisions > divisions;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #SELECTED_DIVISIONS}.
	 */
	public static final String SELECTED_DIVISIONS = ReturnDetailsActivity.class.getName () + ".SELECTED_DIVISIONS";
	
	/**
	 * List of string used to host the selected divisions codes, in return to filter the items / return list.
	 */
	protected ArrayList < String > selectedDivisionsCodes;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #returns}.
	 */
	public static final String RETURN = ReturnDetailsActivity.class.getName () + ".RETURN";
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.Return.Return Return} objects used to define the returns.<br>
	 * <b>NOTE :</b> <br>
	 * If a filter is applied, this list remains untouched. A new list is generated, filtered and passed to the list adapter.
	 */
	protected ArrayList < Return > returns;
	
	/**
	 * Boolean used to indicate if there saved returns that should be retrieved.
	 */
	protected boolean retrieveReturns;
	
	/**
	 * Reference to the returns list population task.
	 */
	protected static PopulateList populateList;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #searchQuery}.
	 */
	public static final String SEARCH_QUERY = ReturnDetailsActivity.class.getName () + ".SEARCH_QUERY";
	
	/**
	 * String holding the search query.
	 */
	private String searchQuery;
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.Return.Return Return} objects used to define the selected returns .
	 */
	protected ArrayList < Return > selectedReturns;
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.Return.ReturnInfo ReturnInfo} objects used to define the transaction details.
	 */
	protected ArrayList < ReturnInfo > details;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #passcode}.
	 */
	public static final String PASSCODE = ReturnDetailsActivity.class.getName () + ".PASSCODE";
	
	/**
	 * String holding the passcode.
	 */
	protected String passcode;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #expiryDate}.
	 */
	public static final String EXPIRY_DATE = ReturnDetailsActivity.class.getName () + ".EXPIRY_DATE";
	
	/**
	 * Date object used to host the expiry date during a return modification.
	 */
	private Date expiryDate;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #currency}.
	 */
	public static final String CURRENCY = ReturnDetailsActivity.class.getName () + ".CURRENCY";
	
	/**
	 * Reference to the currency object.
	 */
	protected Currencies currency;
	private static final String ERROR = ReturnDetailsActivity.class.getName () + ".ERROR";
	
	private boolean error;
	/**
	 * List of the reasons.
	 */
	private ArrayList < Reasons > reasons;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #transactionHeader}.
	 */
	public static final String TRANSACTION_HEADER = ReturnDetailsActivity.class.getName () + ".TRANSACTION_HEADER";
	
	/**
	 * Reference to the transaction header stored in DB (if previously saved)
	 */
	protected TransactionHeaders transactionHeader;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #transactionDetails}.
	 */
	public static final String TRANSACTION_DETAILS = ReturnDetailsActivity.class.getName () + ".TRANSACTION_DETAILS";
	
	/**
	 * List of transaction details references stored in DB (if previously saved)
	 */
	protected ArrayList < TransactionDetails > transactionDetails;
	
	/**
	 * Reference to a {@link me.SyncWise.Android.Widgets.QuickAction.QuickAction QuickAction} used in this activity.
	 */
	private QuickAction quickAction;
	
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
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.return_activity_title ) );
		// Set the activity content from a layout resource.
		setContentView ( R.layout.return_details_activity );
		// Enable fast scrolling
		getListView ().setFastScrollEnabled ( true );
		// Define the empty list view
		getListView ().setEmptyView ( findViewById ( R.id.empty_list_view ) );
		// Display empty list label
		( (TextView) findViewById ( R.id.empty_list_view ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_items_list_label ) );

		// Initialize the search query (if not initialized)
		if ( searchQuery == null )
			searchQuery = "";

        // Perform the quick action setup
        setupQuickAction ();
		
		// Retrieve the request code
		requestCode = getIntent ().getIntExtra ( REQUEST_CODE , -1 );
		// Check if the edit flag is set
		// AND if the request code is INFO
		if ( requestCode == REQUEST_CODE_INFO && getIntent ().getBooleanExtra ( IS_EDIT , false ) )
			// Change the request code to new
			requestCode = REQUEST_CODE_NEW;
		
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state 
			searchQuery = savedInstanceState.getString ( SEARCH_QUERY );
			selectedDivisionsCodes = savedInstanceState.getStringArrayList ( SELECTED_DIVISIONS );
			retrieveReturns = savedInstanceState.getBoolean ( RETURN );
			requestCode = savedInstanceState.getInt ( REQUEST_CODE );
			passcode = savedInstanceState.getString ( PASSCODE );
			expiryDate = (Date) savedInstanceState.getSerializable ( EXPIRY_DATE );
			currency = (Currencies) savedInstanceState.getSerializable ( CURRENCY );
			transactionHeader = (TransactionHeaders) savedInstanceState.getSerializable ( TRANSACTION_HEADER );
			transactionDetails = (ArrayList < TransactionDetails >) savedInstanceState.getSerializable ( TRANSACTION_DETAILS );
			displayTransactionDetails = savedInstanceState.getBoolean ( DISPLAY_TRANSACTION_DETAILS );
			displayPasscode = savedInstanceState.getBoolean ( DISPLAY_PASSCODE );
			returnModification = (Return) savedInstanceState.getSerializable ( RETURN_MODIFICATION );
			displayPrintingConfirmation = savedInstanceState.getBoolean ( DISPLAY_PRINTING_CONFIRMATION );
			printingTransactionCode = savedInstanceState.getString ( PRINTING_TRANSACTION_CODE );
			error = savedInstanceState.getBoolean ( ERROR );
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
		
		// Hide the return detail layout
		findViewById ( R.id.layout_return_details ).setVisibility ( View.GONE );
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
				AppDialog.getInstance ().displayDatePicker ( ReturnDetailsActivity.this , defaultDate.get ( Calendar.YEAR ) , defaultDate.get ( Calendar.MONTH ) , defaultDate.get ( Calendar.DAY_OF_MONTH ) , null , false , true , ReturnDetailsActivity.this , ReturnDetailsActivity.this );
			}
		} );
		
		// Retrieve all the returns asynchronously
		populate ();
	}
	
	/**
	 * Retrieve all the returns asynchronously.
	 */
	protected void populate () {
		// Retrieve all the returns asynchronously
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
		// Check if the printing confirmation should be displayed
				if ( displayPrintingConfirmation )
					// Display printing confirmation
					displayPrintingConfirmation ();
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

		// Save the content of the returns using GSON
		ActivityInstance.saveDataGson ( this , ReturnDetailsActivity.class.getName () , RETURN , returns );
		// Indicate that there is saved returns data
		outState.putBoolean ( RETURN , true );
    	
    	// Save the content of requestCode in the outState bundle
    	outState.putInt ( REQUEST_CODE , requestCode );
    	// Check if the expiry date is valid
    	if ( expiryDate != null )
	    	// Save the content of expiryDate in the outState bundle
	    	outState.putSerializable ( EXPIRY_DATE , expiryDate );
    	// Check if the passcode is valid
    	if ( passcode != null )
	    	// Save the content of passcode in the outState bundle
	    	outState.putString ( PASSCODE , passcode );
    	// Save the content of currency in the outState bundle
    	outState.putSerializable ( CURRENCY , currency );
    	// Save the content of transactionHeader in the outState bundle
    	outState.putSerializable ( TRANSACTION_HEADER , transactionHeader );
    	// Save the content of transactionDetails in the outState bundle
    	outState.putSerializable ( TRANSACTION_DETAILS , transactionDetails );
    	// Save the content of displayTransactionDetails in the outState bundle
    	outState.putBoolean ( DISPLAY_TRANSACTION_DETAILS , displayTransactionDetails );
    	// Save the content of displayPasscode in the outState bundle
    	outState.putBoolean ( DISPLAY_PASSCODE , displayPasscode );
    	// Save the content of returnModification in the outState bundle
    	outState.putSerializable ( RETURN_MODIFICATION , returnModification );
    	// Save the content of displayPrintingConfirmation in the outState bundle
    	outState.putBoolean ( DISPLAY_PRINTING_CONFIRMATION , displayPrintingConfirmation );
    	// Save the content of printingTransactionCode in the outState bundle
    	outState.putString ( PRINTING_TRANSACTION_CODE , printingTransactionCode );
    	// Save the content of error in the outState bundle
    	outState.putBoolean ( ERROR , error );
    }
    
	/*
	 * Called when the activity has detected the user's press of the back key. 
	 *
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed () {
		// Determine if a return is undergoing any modifications
		if ( displayTransactionDetails ) {
			// Reset flag
			displayTransactionDetails = false;
			// Retrieve a reference to the secondary view
			View secondary = findViewById ( R.id.layout_return_details );
			// Hide the secondary view
			secondary.startAnimation ( getViewAnimationOut ( secondary ) );
    		// Enable the main list
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( true );
    		// Refresh the action bar
    		invalidateOptionsMenu ();
    		// Exit method
    		return;
		} // End if
		// Determine if the passcode is undergoing any modifications
		else if ( displayPasscode ) {
			// Reset flag
			displayPasscode = false;
			// Retrieve a reference to the tertiary view
			View tertiary = findViewById ( R.id.layout_passcode );
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
				// Set a new (empty) adapter
				setListAdapter ( getReturnDetailsAdapter ( R.layout.sales_order_details_activity_item , new ArrayList < Return > () , currency.getCurrencyRounding () , true ) );
				// Set a new adapter based on the saved filter, asynchronously
				new FilterList ( searchQuery ).execute ();
    		} // End else
    	} // End else if
    	else 
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
								ReturnDetailsActivity.this.finish ();
								break;
							case DialogInterface.BUTTON_NEGATIVE:
								// Dismiss dialog
								AppDialog.getInstance ().dismiss ();
								break;
							} // End switch
						}
					} );
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
			returnModification = null;
			searchQuery = null;
			returns = null;
			passcode = null;
			selectedReturns = null;
			details = null;
			transactionHeader = null;
			transactionDetails = null;
			currency = null;
			expiryDate = null;
			quickAction = null;
			if ( findViewById ( R.id.edittext_password ) != null ) 
				( (EditText) findViewById ( R.id.edittext_password ) ).removeTextChangedListener ( this );
		} // End if
	}
    
	/*
	 * Method called to notify before the text is changed.
	 * 
	 * @see android.text.TextWatcher#afterTextChanged(Editable)
	 */
	@Override
	public void afterTextChanged ( Editable s ) {
		// Check if the password field is empty and the clear button visible
		if ( ( (EditText) findViewById ( R.id.edittext_password ) ).getText ().toString ().isEmpty () && findViewById ( R.id.button_clear_password ).getVisibility () == View.VISIBLE )
			// Hide the clear button
			findViewById ( R.id.button_clear_password ).setVisibility ( View.GONE );
		// Otherwise check if the password field is NOT empty and the clear button is hidden
		else if ( ( ! ( (EditText) findViewById ( R.id.edittext_password ) ).getText ().toString ().isEmpty () ) && findViewById ( R.id.button_clear_password ).getVisibility () == View.GONE )
			// Display the clear button
			findViewById ( R.id.button_clear_password ).setVisibility ( View.VISIBLE );
	}
	
	/*
	 * Method called to notify after the text is changed.
	 * 
	 * @see android.text.TextWatcher#beforeTextChanged(CharSequence, int, int, int)
	 */
	@Override
	public void beforeTextChanged ( CharSequence s , int start , int count , int after ) {
		// Do nothing
	}

	/*
	 * Method called to notify that the text has just been changed.
	 * 
	 * @see android.text.TextWatcher#onTextChanged(CharSequence, int, int, int)
	 */
	@Override
	public void onTextChanged ( CharSequence arg0 , int start , int before , int count ) {
		// Do nothing
	}
	
    /*
     * This method will be called when an item in the list is selected.
     *
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
	@Override
    protected void onListItemClick ( ListView listView , View view , final int position , long id ) {
		// Check if there is any ongoing returns modifications
		if ( displayTransactionDetails || displayPasscode )
			// Do nothing
			return;
		
		// Retrieve the selected return
		returnModification = (Return) ( (BaseAdapter) getListAdapter () ).getItem ( position );

    	// Determine if the request code is NOT info
    	if ( requestCode != REQUEST_CODE_INFO ) {
    		// Set flag
    		displayTransactionDetails = true;
    		// Initialize the secondary view
    		initializeSecondaryView ( false );
    		// Disable the main list
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( false );
    		// Retrieve a reference to the secondary view
    		View secondaryView = findViewById ( R.id.layout_return_details );
    		// Display the secondary view
    		secondaryView.setVisibility ( View.VISIBLE );
    		// Animate the secondary view
    		secondaryView.startAnimation ( getViewAnimationIn() );
    		// Hide the software keyboard
            ( (InputMethodManager) getBaseContext ().getSystemService ( INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow ( secondaryView.getWindowToken (), 0 );
    		// Refresh the action bar
    		invalidateOptionsMenu ();
    	} // End if
		// Determine if the request code is info
    	// AND if the view only flag is provided
    	else if ( requestCode == REQUEST_CODE_INFO
    			&& ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) )
    		// Display the quick action widget
    		quickAction.show ( view , returnModification , getResources () );
	}
	
	/*
	 * Callback method to be invoked when an action item in this QuickAction has been clicked.
	 *
	 * @see me.SyncWise.Android.Widgets.QuickAction.QuickAction.OnActionItemClickListener#onItemClick(me.SyncWise.Android.Widgets.QuickAction.QuickAction, int, int)
	 */
	@Override
	public void onItemClick ( QuickAction source , View anchor , Object object , int pos , int actionId ) {
		// Retrieve the selected return
		Return selectedReturn = (Return) object;
		
		// Determine the clicked action item
		switch ( actionId ) {
		case ActionItemID.EDIT:
			// Retrieve the selected return
			returnModification = selectedReturn;
    		// Set flag
			displayTransactionDetails = true;
    		// Initialize the secondary view
    		initializeSecondaryView ( false );
    		// Disable the main list
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( false );
    		// Retrieve a reference to the secondary view
    		View secondaryView = findViewById ( R.id.layout_return_details );
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
			selectedReturn.setQuantityBig ( 0 );
			selectedReturn.setQuantityMedium ( 0 );
			selectedReturn.setQuantitySmall ( 0 );
			selectedReturn.setExpiryDate ( null );
			selectedReturn.setReason ( null );
			// Remove item from the selected returns (if valid)
			if ( selectedReturns != null )
				selectedReturns.remove ( selectedReturn );
			// Refresh return details
			refreshDetails ();
			// Check if the request code is INFO
			// AND if the selected returns list is empty
			if ( requestCode == REQUEST_CODE_INFO && selectedReturns != null && selectedReturns.isEmpty () )
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
	 * Initializes the return modification (secondary) view.
	 * 
	 * @param restore	Boolean used to determine if the secondary view is being initialized for the first time or is being restored.
	 */
	protected void initializeSecondaryView ( final boolean restore ) {
		// Retrieve a reference to the parent view
		View parent = findViewById ( R.id.layout_return_details );
		// Retrieve a reference to the return quantity layout
		View quantityLayout = parent.findViewById ( R.id.layout_return_quantity );
		// Retrieve a reference to the return data layout
		View dataLayout = parent.findViewById ( R.id.layout_return_data );
		// Retrieve a reference to the return data layout
		View expiryDateLayout = dataLayout.findViewById ( R.id.layout_date_picker );
		// Retrieve a reference to the return data layout
		View reasonLayout = dataLayout.findViewById ( R.id.layout_reason );
		// Retrieve a reference to the item name label
		TextView itemNameLabel = (TextView) parent.findViewById ( R.id.label_item_name );
		// Retrieve a reference to the save icon
		ImageView saveIcon = (ImageView) parent.findViewById ( R.id.icon_save_return );
		// Retrieve a reference to the return quantity label
		TextView quantityLabel = (TextView) parent.findViewById ( R.id.label_return_quantity );
		// Retrieve a reference to the return data label
		TextView dataLabel = (TextView) parent.findViewById ( R.id.label_return_data );
		// Retrieve a reference to the quantity UOMs layouts
		LinearLayout quantityBigLayout = (LinearLayout) quantityLayout.findViewById ( R.id.layout_big_uom );
		LinearLayout quantityMediumLayout = (LinearLayout) quantityLayout.findViewById ( R.id.layout_medium_uom );
		// Retrieve references to the quantity number pickers
		NumberPicker quantityBig = (NumberPicker) quantityLayout.findViewById ( R.id.number_picker_quantity_big );
		NumberPicker quantityMedium = (NumberPicker) quantityLayout.findViewById ( R.id.number_picker_quantity_medium );
		NumberPicker quantitySmall = (NumberPicker) quantityLayout.findViewById ( R.id.number_picker_quantity_small );
		// Retrieve references to the quantity descriptive labels
		TextView quantityBigLabel_quantity = (TextView) quantityLayout.findViewById ( R.id.label_quantity_big );
		TextView quantityMediumLabel_quantity = (TextView) quantityLayout.findViewById ( R.id.label_quantity_medium );
		TextView quantitySmallLabel_quantity = (TextView) quantityLayout.findViewById ( R.id.label_quantity_small );
		// Retrieve references to the prices labels for quantity
		TextView priceBigLabel_quantity = (TextView) quantityLayout.findViewById ( R.id.label_price_big );
		TextView priceMediumLabel_quantity = (TextView) quantityLayout.findViewById ( R.id.label_price_medium );
		TextView priceSmallLabel_quantity = (TextView) quantityLayout.findViewById ( R.id.label_price_small );
		// Retrieve a reference to the expiry date label
		TextView expiryDateLabel = (TextView) expiryDateLayout.findViewById ( R.id.label_date_picker );
		// Retrieve a reference to the reason label
		TextView reasonLabel = (TextView) reasonLayout.findViewById ( R.id.label_reason );
		
		// Display the item name
		itemNameLabel.setText ( returnModification.getItem ().getItemName () );
		// Enable the save button
		saveIcon.setEnabled ( true );
		// Display the quantity label
		quantityLabel.setText ( AppResources.getInstance ( this ).getString ( this , R.string.quantity_label ) );
		// Assign on focus listeners to the number pickers
		quantityBig.getEditText ().setOnFocusChangeListener ( this );
		quantityMedium.getEditText ().setOnFocusChangeListener ( this );
		quantitySmall.getEditText ().setOnFocusChangeListener ( this );
		// Display the data label
		dataLabel.setText ( AppResources.getInstance ( this ).getString ( this , R.string.return_label ) );
		// Display the reason label
		reasonLabel.setText ( AppResources.getInstance ( this ).getString ( this , R.string.reason_label ) );
		
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
		
		// Display labels descriptions
		quantityBigLabel_quantity.setText ( returnModification.getUnit ().getUnitBigDescription () );
		quantityMediumLabel_quantity.setText ( returnModification.getUnit ().getUnitMediumDescription () );
		quantitySmallLabel_quantity.setText ( returnModification.getUnit ().getUnitSmallDescription () );
		// Dim the appropriate unit of measurements labels
		quantityBigLayout.setAlpha ( ItemsUtils.isBig ( returnModification.getItem () ) ? 1f : 0.25f );
		quantityMediumLayout.setAlpha ( ItemsUtils.isMedium ( returnModification.getItem () ) ? 1f : 0.25f );
		
		// Display the expiry date layout accordingly
		expiryDateLayout.setVisibility ( View.VISIBLE );
		// Display the reason layout accordingly
		reasonLayout.setVisibility ( View.VISIBLE );
		// Display the data layout accordingly
		dataLayout.setVisibility ( View.VISIBLE );
		
		// Check if the view is being restored
		if ( ! restore ) {
			// Set the current return quantities
			quantityBig.setNumber ( returnModification.getQuantityBig () );
			quantityMedium.setNumber ( returnModification.getQuantityMedium () );
			quantitySmall.setNumber ( returnModification.getQuantitySmall () );
			// Disable the appropriate number pickers
			quantityBig.setEnabled ( ItemsUtils.isBig ( returnModification.getItem () ) );
			quantityMedium.setEnabled ( ItemsUtils.isMedium ( returnModification.getItem () ) );
			// Set expiry date
			expiryDate = returnModification.getExpiryDate ();
			// Track the selected index
			int index = 0;
			// Retrieve a reference to the reason spinner
			Spinner spinner = (Spinner) reasonLayout.findViewById ( R.id.spinner_reason );
			// Select the appropriate reason
			if ( returnModification.getReason () != null ) {
				// Iterate over all reasons
				for ( int i = 1 ; i < spinner.getAdapter ().getCount () ; i ++ )
					// Match reason codes
					if ( ( (Reasons) spinner.getAdapter ().getItem ( i ) ).getReasonCode ().equals ( returnModification.getReason ().getReasonCode () ) ) {
						// Set the current index
						index = i;
						// Exit loop
						break;
					} // End if
			} // End if
			// Set the current existence
			spinner.setSelection ( index );
		} // End if
		
		// Display the expiry date label
		expiryDateLabel.setText ( getFormattedExpiryDate ( expiryDate ) );
		// Display labels prices
		priceBigLabel_quantity.setText ( moneyFormat.format ( returnModification.getPriceBig () ) );
		priceMediumLabel_quantity.setText ( moneyFormat.format ( returnModification.getPriceMedium () ) );
		priceSmallLabel_quantity.setText ( moneyFormat.format ( returnModification.getPriceSmall () ) );
	}
	
	/**
	 * Called when a view has been clicked.<br>
	 * The return modifications are saved.
	 * 
	 * @param view	The clicked view.
	 */
	public void updateReturn ( View view ) {
		// Determine if a return is undergoing any modifications
		if ( ! displayTransactionDetails )
			// No modifications
			return;
		
		// Retrieve a reference to the secondary view
		View secondary = findViewById ( R.id.layout_return_details );
		// Retrieve a reference to the return quantity layout
		View quantityLayout = secondary.findViewById ( R.id.layout_return_quantity );
		// Retrieve references to the quantity number pickers
		NumberPicker quantityBig = (NumberPicker) quantityLayout.findViewById ( R.id.number_picker_quantity_big );
		NumberPicker quantityMedium = (NumberPicker) quantityLayout.findViewById ( R.id.number_picker_quantity_medium );
		NumberPicker quantitySmall = (NumberPicker) quantityLayout.findViewById ( R.id.number_picker_quantity_small );
		// Retrieve references to the reason spinner
		Spinner reasonSpinner = (Spinner) secondary.findViewById ( R.id.spinner_reason );
		// Retrieve the reason
		Reasons reason = (Reasons) reasonSpinner.getSelectedItem ();
		
		// Check if any of the return quantities / prices are not valid
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
		
		// Make sure the current return has valid quantities
		if ( quantityBig.getNumber () != 0 || quantityMedium.getNumber () != 0 || quantitySmall.getNumber () != 0 ) {
			// Locate the return detail
			TransactionDetails transactionDetail = null;
			for ( TransactionDetails detail : transactionDetails )
				if ( detail.getItemCode ().equals ( returnModification.getItem ().getItemCode () ) ) {
					transactionDetail = detail;
					break;
				}
			// Determine if the item expiry of the detail is null
			boolean isExpiryDetailNull = transactionDetail.getItemExpiryDate () == null;
			// Determine if the item expiry of the return is null
			boolean isExpiryReturnNull = expiryDate == null;
			// Determine if the reason of the detail is null
			boolean isReasonDetailNull = transactionDetail.getReasonCode () == null;
			// Determine if the reason of the return is null
			boolean isReasonReturnNull = reason == null || reason.getReasonCode () == null;
			// Check for inconsistencies
			if ( isExpiryDetailNull != isExpiryReturnNull 
					|| ( transactionDetail.getItemExpiryDate () != null && ! transactionDetail.getItemExpiryDate ().equals ( expiryDate ) ) ) {
				// Indicate that the expiry date is inconsistent
				Baguette.showText ( ReturnDetailsActivity.this ,
						AppResources.getInstance ( ReturnDetailsActivity.this ).getString ( ReturnDetailsActivity.this , R.string.return_expiry_date_inconsistency_message ) ,
						Baguette.BackgroundColor.RED );
				// Exit method
				return;
			} // End if
			if ( isReasonDetailNull != isReasonReturnNull 
					|| ( ! isReasonDetailNull && ! isReasonReturnNull && ! transactionDetail.getReasonCode ().equals ( reason.getReasonCode () ) )  ) {
				// Indicate that the reason is inconsistent
				Baguette.showText ( ReturnDetailsActivity.this ,
						AppResources.getInstance ( ReturnDetailsActivity.this ).getString ( ReturnDetailsActivity.this , R.string.return_reason_inconsistency_message ) ,
						Baguette.BackgroundColor.RED );
				// Exit method
				return;
			} // End if	
			// Check if the suggestions are enforced as limits
			if ( quantityBig.getNumber () > returnModification.getSuggestedBig ()
							|| quantityMedium.getNumber () > returnModification.getSuggestedMedium ()
							|| quantitySmall.getNumber () > returnModification.getSuggestedSmall () ) {
				// Indicate that the suggestions are exceeded
				Baguette.showText ( ReturnDetailsActivity.this ,
						AppResources.getInstance ( ReturnDetailsActivity.this ).getString ( ReturnDetailsActivity.this , R.string.return_suggestion_exceeded_message ) ,
						Baguette.BackgroundColor.RED );
				// Exit method
				return;
			} // End if
		} // End if
		
		// Reset flag
		displayTransactionDetails = false;
		
		// Update the return quantities
		returnModification.setQuantityBig ( (int) quantityBig.getNumber () );
		returnModification.setQuantityMedium ( (int) quantityMedium.getNumber () );
		returnModification.setQuantitySmall ( (int) quantitySmall.getNumber () );
		// Update the expiry date
		returnModification.setExpiryDate ( expiryDate );
		// Update the existence
		returnModification.setReason ( reason == null || reasonSpinner.getSelectedItemPosition () == 0 ? null : reason );
		
		// Disable the save icon
		secondary.findViewById ( R.id.icon_save_return ).setEnabled ( false );
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
		
		// Update the return / UI
		onReturnModificationResult ();
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
	}
	
	/**
	 * Called after a successful return modification : price, quantity, expiry date or reason modification.<br>
	 * The corresponding object is modified in the main list and the appropriate action is performed (based on the current request code).
	 */
	private void onReturnModificationResult () {
		// Retrieve a reference to the return object from the main list
		Return salesReturn = null;
		// Iterate over all the returns
		for ( int i = 0 ; i < returns.size () ; i ++ )
			// Match the return
			if ( returns.get ( i ).getItem ().getItemCode ().equals ( returnModification.getItem ().getItemCode () ) ) {
				// Update the selected return position
				salesReturn = returns.get ( i );
				// Exit loop
				break;
			} // End if
		// Check if the return object is valid
		if ( salesReturn == null )
			// Invalid object
			return;
    	// Update the return object
		salesReturn.setQuantityBig ( returnModification.getQuantityBig () );
		salesReturn.setQuantityMedium ( returnModification.getQuantityMedium () );
		salesReturn.setQuantitySmall ( returnModification.getQuantitySmall () );
		salesReturn.setExpiryDate ( returnModification.getExpiryDate () );
		salesReturn.setReason ( returnModification.getReason () );
		// Remove item from the selected returns (if valid)
		if ( selectedReturns != null && salesReturn.getQuantityBig () == 0 && salesReturn.getQuantityMedium () == 0 && salesReturn.getQuantitySmall () == 0 )
			selectedReturns.remove ( salesReturn );
		// Refresh return details
		refreshDetails ();
		// Check if the request code is INFO
		// AND if the selected returns list is empty
		if ( ReturnDetailsActivity.this.requestCode == REQUEST_CODE_INFO && selectedReturns != null && selectedReturns.isEmpty () )
			// Emulate a back key press
			onBackPressed ();
		else
	    	// Refresh the list
	    	( (BaseAdapter) getListAdapter () ).notifyDataSetChanged ();
	}
	
	/*
	 * Initialize the contents of the Activity's standard options menu.
	 *
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
    @Override
    public boolean onCreateOptionsMenu ( Menu menu ) {
    	// Check if the list of returns is valid
    	// OR if a return is undergoing modifications
    	if ( returns == null || displayTransactionDetails || displayPasscode )
    		// Hide the menu
            return false;
    	// Use the MenuInflater of this context to inflate a menu hierarchy from the specified XML resource
    	getMenuInflater ().inflate ( R.menu.action_bar , menu );
    	// Determine if the request code is info
    	if ( requestCode != REQUEST_CODE_INFO ) {
    		// The request code is NOT INFO
	    	// Enable the required menu items
    		me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_list ) );
	    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_basket ) );
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
    		if ( transactionHeader == null
    				|| ( transactionHeader.getIsProcessed () != IsProcessedUtils.isSync () ) ) {
            	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_save ) );
            
    		} // End if
    		 
    		
    	} // End else
    	if ( requestCode == REQUEST_CODE_INFO ) 
    	{me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_print ) );}
		// Display the menu
        return true;
    }
    
	/**
	 * Performs all necessary setup in return to properly display the search view widget.
	 * 
	 * @param searchView	A {@link android.widget.SearchView SearchView} object used to perform the setup.
	 */
    protected void setupSearchView ( SearchView searchView ) {
    	// Check if the search view is valid
    	if ( searchView == null )
    		// Invalid search view
    		return;
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
    	// Check if the returns list is valid
    	if ( returns == null )
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
    	else if ( menuItem.getItemId () == R.id.action_print ) {
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
								Intent intent = new Intent ( ReturnDetailsActivity.this, PrintingActivity.class );
								intent.putExtra ( PrintingActivity.TYPE , PrintingActivity.Type.RETURN );
								intent.putExtra ( PrintingActivity.PRINTOUT , PrintingActivity.Printout.ORIGINAL );//COPY
								ArrayList < TransactionHeaders > list = new ArrayList < TransactionHeaders > ();
								list.add ( transactionHeader );
								intent.putExtra ( PrintingActivity.HEADER , list );
								startActivityForResult ( intent , REQUEST_CODE_PRINT );
								
								
								
							 		break;
							case DialogInterface.BUTTON_NEGATIVE:
								// Dismiss dialog
								AppDialog.getInstance ().dismiss ();
								break;
							} // End switch
						} } );
    		// Consume event
    		return true;
    	} // End if
    	// Determine if the action basket is selected
    	else if ( menuItem.getItemId () == R.id.action_basket ) {
    		// Declare and initialize a list used to host the valid returns
    		ArrayList < Return > validReturns = new ArrayList < Return > ();
    		// Iterate over all returns
    		for ( Return salesReturn : returns ) {
    			// Check if the current return has valid quantities
    			if ( salesReturn.getQuantityBig () != 0 || salesReturn.getQuantityMedium () != 0 || salesReturn.getQuantitySmall () != 0 )
    				// The return contains at least one quantity, add it to the return list
    				validReturns.add ( salesReturn );
            } // End for each
    		// Check if there is at least one valid return
    		if ( validReturns.isEmpty () ) {
				// Indicate that no return is valid
				Baguette.showText ( ReturnDetailsActivity.this ,
						AppResources.getInstance ( ReturnDetailsActivity.this ).getString ( ReturnDetailsActivity.this , R.string.no_return_message ) ,
						Baguette.BackgroundColor.RED );
        		// Consume event
        		return true;
    		} // End if
    		// Otherwise there is at least one return
    		// Set the new request code
    		requestCode = REQUEST_CODE_INFO;
    		// Refresh the action bar
    		invalidateOptionsMenu ();
    		// Set the selected returns list
    		selectedReturns = validReturns;
			// Initialize the return details
			initializeDetails ();
			
			// Declare and initialize a multiple list adapter
			MultipleListAdapter adapter = new MultipleListAdapter ( this );
			// Add the return details adapter
			adapter.addSection ( new Section ( AppResources.getInstance ( this ).getString ( this , R.string.return_details_label ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
					new ReturnInfoAdapter ( this , R.layout.order_info_item , details ) );
			// Add the return adapter
			adapter.addSection ( new Section ( AppResources.getInstance ( this ).getString ( this , R.string.return_products_label ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
					getReturnDetailsAdapter ( R.layout.sales_order_details_activity_item , selectedReturns , currency.getCurrencyRounding () , ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) ) );
			// Set the list adapter
			setListAdapter ( adapter );

    		// Consume event
    		return true;
    	} // End else if
    	// Determine if the action save is selected
    	else if ( menuItem.getItemId () == R.id.action_save ) {
			// Save current return
    		saveReturn ();
    		// Consume event
    		return true;
    	} // End else if
    	// Allow normal menu processing to proceed
    	return false;
    }
    
    /**
     * Saves the current return.
     */
    protected void saveReturn () {
    	//error = false;
		// Check if there is a previously stored transaction
		if ( transactionHeader != null && transactionDetails != null )
    		// Check if there are modifications
    		if ( ! hasModifications () ) {
    			// Indicate that there are no new modifications
				Baguette.showText ( this ,
						AppResources.getInstance ( this ).getString ( this , R.string.no_new_modifications_message ) ,
						Baguette.BackgroundColor.BLUE );
	    		// Exit method
	    		return;
    		} // End if
    	// Check if the user has at least one vehicle
    	if ( DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesDao ().count () == 0 ) {
			// Display baguette message
			Baguette.showText ( this , AppResources.getInstance ( this ).getString ( this , R.string.return_no_vehicle_message ) , Baguette.BackgroundColor.RED );
    		// Exit method
    		return;
    	} // End if
		// Otherwise the return can be saved

    	// Flag used to indicate whether an error occurred or not
	  error = false;
    	
    	// Compute the return details
    	double calValue = 0;
    	double grossAmount = 0;
    	double taxes = 0;
    	double totalValue = 0;
    	// Iterate over all the selected returns
    	for ( Return salesReturn : selectedReturns ) {
    		// Compute the current net value
    		grossAmount += salesReturn.getQuantityBig () * salesReturn.getPriceBig () + salesReturn.getQuantityMedium () * salesReturn.getPriceMedium () + salesReturn.getQuantitySmall () * salesReturn.getPriceSmall ();
    		// Compute the value after applying the discount percentage and amount
			double _calValue = ( salesReturn.getQuantityBig () * salesReturn.getPriceBig () + salesReturn.getQuantityMedium () * salesReturn.getPriceMedium ()
					+ salesReturn.getQuantitySmall () * salesReturn.getPriceSmall () );
			// Compute the current tax value
			double _taxes = 0;
			// Check if the item is taxable
			if ( salesReturn.getTax () != 0 )
				// Compute the current taxes
				_taxes = _calValue * salesReturn.getTax () / 100;
			// Compute the current total value
			double _totalValue = _calValue + _taxes;
			// Update all the values
			calValue += _calValue;
			taxes += _taxes;
			totalValue += _totalValue;
    	} // End for each
    	// Compute current time
    	Calendar now = Calendar.getInstance ();
		// Retrieve the user object
		Users user = DatabaseUtils.getInstance ( this ).getDaoSession ().getUsersDao ().queryBuilder ()
				.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( this ) ) ,
						UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( this ) ) ).unique ();
		// Retrieve the division code
		String divisionCode = DatabaseUtils.getCurrentDivisionCode ( this );
		// Retrieve the visit
		Visits visit = (Visits) getIntent ().getSerializableExtra ( VISIT );
		// Retrieve the vehicle stock
		List < VehiclesStock > vehicleStock = DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesStockDao ().loadAll ();
		// Map good & damaged quantities
		HashMap < String , Double > goodQuantities = new HashMap < String , Double > ();
		HashMap < String , Double > damagedQuantities = new HashMap < String , Double > ();
		// Iterate over the vehicle stock
		for ( VehiclesStock stock : vehicleStock ) {
			DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesStockDao ().refresh ( stock );
			// Map the good & damaged quantity
			goodQuantities.put ( stock.getItemCode () , stock.getGoodQuantity () );
			damagedQuantities.put ( stock.getItemCode () , stock.getDamageQuantity () );
		} // End for each
		// Retrieve a vehicle
		Vehicles vehicle = DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesDao ().queryBuilder ().limit ( 1 ).unique ();
		
		// Determine if there is a previously stored return
		if ( transactionHeader != null && transactionDetails != null ) {
			// Update the return header
			transactionHeader.setVisitID ( visit.getVisitID () );
			transactionHeader.setGrossAmount ( grossAmount );
			transactionHeader.setNetAmount ( calValue );
			transactionHeader.setTaxAmount ( taxes );
			transactionHeader.setTotalTaxAmount ( totalValue );
			transactionHeader.setIsProcessed ( IsProcessedUtils.isNotSync () );
			transactionHeader.setIssueDate(now.getTime ());
			// Declare and initialize vehicle stock lists used to add and update 
			List < VehiclesStock > addStock = new ArrayList < VehiclesStock > ();
			List < VehiclesStock > updateStock = new ArrayList < VehiclesStock > ();
			// Map the selected returns to their item codes
			Map < String , Return > _selectedReturns = new HashMap < String , Return > ();
			// Iterate over all selected returns
			for ( Return salesReturn : selectedReturns ) 
				// Map the current return to its item code
				_selectedReturns.put ( salesReturn.getItem ().getItemCode () , salesReturn );
			// Iterate over all return details
			for ( TransactionDetails transactionDetail : transactionDetails ) {
				// Check if the current detail is selected
				if ( _selectedReturns.containsKey ( transactionDetail.getItemCode () ) ) {
					Return salesReturn = _selectedReturns.get ( transactionDetail.getItemCode () );
					transactionDetail.setQuantityBig ( (double) salesReturn.getQuantityBig () );
					transactionDetail.setQuantityMedium ( (double) salesReturn.getQuantityMedium () );
					transactionDetail.setQuantitySmall ( (double) salesReturn.getQuantitySmall () );
					transactionDetail.setBasicUnitQuantity ( (double) salesReturn.getQuantityBig () * salesReturn.getItem ().getUnitBigMedium () * salesReturn.getItem ().getUnitMediumSmall ()
							+ salesReturn.getQuantityMedium () * salesReturn.getItem ().getUnitMediumSmall ()
							+ salesReturn.getQuantitySmall () );
					transactionDetail.setMissedQuantityBig ( transactionDetail.getApprovedQuantityBig () - transactionDetail.getQuantityBig () );
					transactionDetail.setMissedQuantityMedium ( transactionDetail.getApprovedQuantityMedium () - transactionDetail.getQuantityMedium () );
					transactionDetail.setMissedQuantitySmall ( transactionDetail.getApprovedQuantitySmall () - transactionDetail.getQuantitySmall () );
					transactionDetail.setMissedBasicUnitQuantity ( transactionDetail.getApprovedBasicUnitQuantity () - transactionDetail.getBasicUnitQuantity () );
					transactionDetail.setTotalLineAmount ( salesReturn.getQuantityBig () * salesReturn.getPriceBig () + salesReturn.getQuantityMedium ()
								* salesReturn.getPriceMedium () + salesReturn.getQuantitySmall () * salesReturn.getPriceSmall () );
					// Update vehicle stock
					VehiclesStock stock = DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesStockDao ().queryBuilder ()
							.where ( VehiclesStockDao.Properties.ItemCode.eq ( transactionDetail.getItemCode () ) ).limit ( 1 ).unique ();
					if ( stock == null ) {
						stock = new VehiclesStock ( null , vehicle.getVehicleCode () , transactionDetail.getItemCode () , 0.0 , 0.0 , divisionCode , user.getCompanyCode () , null , now.getTime () );
						addStock.add ( stock );
					} // End if
					else
						updateStock.add ( stock );
					if ( ReasonsUtils.isDamage ( salesReturn.getReason () ) )
						stock.setDamageQuantity ( stock.getDamageQuantity () + transactionDetail.getBasicUnitQuantity () );
					else
						stock.setGoodQuantity ( stock.getGoodQuantity () + transactionDetail.getBasicUnitQuantity () );
				} // End if
				else {
					// Reset quantities and set missed quantities
					transactionDetail.setQuantityBig ( 0.0 );
					transactionDetail.setQuantityMedium ( 0.0 );
					transactionDetail.setQuantitySmall ( 0.0 );
					transactionDetail.setBasicUnitQuantity ( 0.0 );
					transactionDetail.setMissedQuantityBig ( transactionDetail.getApprovedQuantityBig () );
					transactionDetail.setMissedQuantityMedium ( transactionDetail.getApprovedQuantityMedium () );
					transactionDetail.setMissedQuantitySmall ( transactionDetail.getApprovedQuantitySmall () );
					transactionDetail.setMissedBasicUnitQuantity ( transactionDetail.getApprovedBasicUnitQuantity () );
					transactionDetail.setTotalLineAmount ( 0.0 );
				} // End if
			} // End for each
			
    		try {
				// Begin transaction
				DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().beginTransaction ();
				
				// Update the return header in DB
				DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionHeadersDao ().update ( transactionHeader );
				// Iterate over all return details
				for ( TransactionDetails transactionDetail : transactionDetails )
					// Update the return detail in DB
					DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsDao ().update ( transactionDetail );
				// Iterate over all new stock
				for ( VehiclesStock stock : addStock )
					// Insert the new stock into DB
					DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesStockDao ().insert ( stock );
				// Iterate over modified stock
				for ( VehiclesStock stock : updateStock )
					// Update the stock in DB
					DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesStockDao ().update ( stock );
				
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
		// Check if an error occurred
				if ( ! error && transactionHeader.getTransactionCode() != null ) {
					// Retrieve a constant reference to the transaction code
					printingTransactionCode = transactionHeader.getTransactionCode();
					// Set flag
					displayPrintingConfirmation=true;
					// Display printing confirmation
					displayPrintingConfirmation ();
					 
				} // End if
				else {
    	// Call this to set the result that your activity will return to its caller
    	setResult ( RESULT_OK , new Intent ().putExtra ( ReturnActivity.SAVE_SUCCESS , ! error ) );
    	// Finish this activity
    	finish ();
    	}
		// Indicate that the save was successful
		Vibration.vibrate ( ReturnDetailsActivity.this );
    }
    
    /**
	 * Displays a confirmation dialog used to prompt the user to print the current transaction.
	 */
	private void displayPrintingConfirmation () {
		// Retrieve the transaction code
		final String transactionCode = printingTransactionCode;
		// Check if the transaction code is valid
		if ( transactionCode == null )
			// Do nothing
			return;
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
							Intent intent = new Intent ( ReturnDetailsActivity.this, PrintingActivity.class );
							intent.putExtra ( PrintingActivity.TYPE , PrintingActivity.Type.RETURN );
							intent.putExtra ( PrintingActivity.PRINTOUT , PrintingActivity.Printout.ORIGINAL );
							intent.putExtra ( PrintingActivity.HEADER , new ArrayList < TransactionHeaders > ( DatabaseUtils.getInstance ( ReturnDetailsActivity.this ).getDaoSession ().getTransactionHeadersDao ().queryBuilder ()
									.where ( TransactionHeadersDao.Properties.TransactionCode.eq ( transactionCode ) ).list () ) );
							startActivityForResult ( intent , REQUEST_CODE_PRINT );
							// Reset flag
							displayPrintingConfirmation = false;
							intent = new Intent();
							intent.putExtra ( ReturnActivity.SAVE_SUCCESS , ! error );
					    	setResult ( RESULT_OK , intent );
				    		// Finish this activity
				    		finish ();
						
							break;
						case DialogInterface.BUTTON_NEGATIVE:
					    	// Call this to set the result that your activity will return to its caller
							intent = new Intent();
							intent.putExtra ( ReturnActivity.SAVE_SUCCESS , ! error );
					    	setResult ( RESULT_OK , intent );
				    		// Finish this activity
				    		finish ();
							// Dismiss dialog
							AppDialog.getInstance ().dismiss ();
							// Reset flag
							displayPrintingConfirmation = false;
							break;
						} // End switch
					} } );
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
	 * Properly initializes and returns a list adapter for the return list.
	 * 
	 * @param layout	XML Layout (layout) resource id.
	 * @param returnItems	List of {@link me.SyncWise.Android.Modules.Return.Return Return} objects.
	 * @param currencyRounding	Integer used to indicate the currency rounding.
	 * @param itemsEnabled	Flag indicating if the items are enabled or not.
	 * @return	A list adapter used to populate the return list.
	 */
	protected ListAdapter getReturnDetailsAdapter ( int layout , List < Return > returnItems , final int currencyRounding , final boolean itemsEnabled ) {
		return new ReturnDetailsAdapter ( this , layout , returnItems , currencyRounding , itemsEnabled );
	}
	
	/**
	 * Indicates whether the return has new / unsaved modifications.<br>
	 * This method iterates over the list of all selected returns and compares them with the returns list.<br>
	 * <b>Note : </b> This method always returns false if there are no previously stored return details.
	 * 
	 * @return	Boolean stating if there are new modifications.
	 */
	private boolean hasModifications () {
		// Check if there is a previously stored return
		if ( transactionHeader == null || transactionDetails == null || selectedReturns == null )
			// No saved return to match to
			return false;
		// Map the return details to their item codes
		Map < String , TransactionDetails > _transactionDetails = new HashMap < String , TransactionDetails > ();
		// Iterate over all return details
		for ( TransactionDetails transactionDetail : transactionDetails )
			// Map the current return detail to its item code
			_transactionDetails.put ( transactionDetail.getItemCode () , transactionDetail );
		// Declare and initialize a list used to host the valid return
		ArrayList < Return > returnItems = new ArrayList < Return > ();
		// Iterate over all returns
		for ( Return salesReturn : returns )
			// Check if the current return has valid quantities
			if ( salesReturn.getQuantityBig () != 0 || salesReturn.getQuantityMedium () != 0 || salesReturn.getQuantitySmall () != 0 )
				// The return contains at least one quantity, add it to the return list
				returnItems.add ( salesReturn );
		// Check if both sizes differ
		if ( returnItems.size () != _transactionDetails.size () )
			// There is at least one modification
			return true;
		// Iterate over the return
		for ( Return salesReturn : returnItems )
			// Determine the corresponding return detail
			if ( _transactionDetails.containsKey ( salesReturn.getItem ().getItemCode () ) ) {
				// Retrieve a reference to the return detail
				TransactionDetails transactionDetail = _transactionDetails.get ( salesReturn.getItem ().getItemCode () );
				// Determine if the item expiry of the detail is null
				boolean isExpiryDetailNull = transactionDetail.getItemExpiryDate () == null;
				// Determine if the item expiry of the return is null
				boolean isExpiryReturnNull = salesReturn.getExpiryDate () == null;
				// Determine if the reason of the detail is null
				boolean isReasonDetailNull = transactionDetail.getReasonCode () == null;
				// Determine if the reason of the return is null
				boolean isReasonReturnNull = salesReturn.getReason () == null;
				// Check for modifications
				if ( ! transactionDetail.getQuantityBig ().equals ( (double) salesReturn.getQuantityBig () )
						|| ! transactionDetail.getQuantityMedium ().equals ( (double) salesReturn.getQuantityMedium () )
						|| ! transactionDetail.getQuantitySmall ().equals ( (double) salesReturn.getQuantitySmall () )
						|| ! transactionDetail.getPriceBig ().equals ( salesReturn.getPriceBig () )
						|| ! transactionDetail.getPriceMedium ().equals ( salesReturn.getPriceMedium () )
						|| ! transactionDetail.getPriceSmall ().equals ( salesReturn.getPriceSmall () )
						|| ( isExpiryDetailNull != isExpiryReturnNull 
							|| ( transactionDetail.getItemExpiryDate () != null && ! transactionDetail.getItemExpiryDate ().equals ( salesReturn.getExpiryDate () ) ) )
						|| ( isReasonDetailNull != isReasonReturnNull 
								|| ( ! isReasonDetailNull && ! isReasonReturnNull && ! transactionDetail.getReasonCode ().equals ( salesReturn.getReason ().getReasonCode () ) ) ) )
					// There is at least one modification
					return true;
			} // End if
			else
				// There is at least one modification
				return true;
		// Otherwise there are no modifications
		return false;
	}
	
	/**
	 * Filters the {@link #returns} list based on the indicated {@link #searchQuery} string and the {@link #selectedDivisionsCodes} list.<br>
	 * <b>NOTE : </b> This method does not perform any kind of integrity check what so ever !
	 * 
	 * @return	List of {@link me.SyncWise.Android.Modules.Return.Return Return} objects filtered according to the search and divisions filter.
	 */
	@SuppressLint("DefaultLocale") 
	protected ArrayList < Return > filter () {
		// Declare and initialize a new returns list
		ArrayList < Return > _returns = new ArrayList < Return > ();
		// Iterate over all the returns
		for ( Return salesReturn : returns ) {
			// Determine if at least one of the return's divisions is selected
			boolean skip = true;
			// Iterate over all return's division
			for ( String divisionCode : salesReturn.getDivisionCodes () )
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
			
			// Match the filter with the item code and the item description (contains)
			if ( salesReturn.getItem ().getItemName ().toLowerCase ().contains ( searchQuery.toLowerCase () )
					|| salesReturn.getItem ().getItemCode ().toLowerCase ().contains ( searchQuery.toLowerCase () )
					|| ( salesReturn.getBarCodes () != null && salesReturn.getBarCodes ().contains ( searchQuery.toLowerCase () ) ) )
				// Add the return to the list
				_returns.add ( salesReturn );
		} // End for each
		// Return the new returns list
		return _returns;
	}
	
	/**
	 * AsyncTask helper class used to populate the return items list with the appropriate items.
	 * 
	 * @author Elias
	 *
	 */
	private class PopulateList extends AsyncTask < Void , Void , ArrayList < Return > > {
		
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
		protected ArrayList < Return > doInBackground ( Void ... params ) {
			try {
				
				// Declare and initialize a cursor object used to retrieve data sets from query results
				Cursor cursor = null;
				// Declare the SQL string and selection arguments array of strings used to to query DB
				String SQL = null;
				String selectionArguments [] = null;
				// Retrieve the user, company, client and division codes
				String companyCode = DatabaseUtils.getCurrentCompanyCode ( ReturnDetailsActivity.this );
				String clientCode = ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClient ().getClientCode ();
				
				// Temporary list
				HashMap < String , Reasons > _reasons = new HashMap < String , Reasons > ();
				// Check if the statuses list is valid
				if ( reasons == null ) {
					// Initialize the list
					reasons = new ArrayList < Reasons > ();
					// Add the default selection
					reasons.add ( new Reasons ( null , null , null , "----------" , null , null , null , null , null , null ) );
					// Retrieve the statuses list
					reasons.addAll ( DatabaseUtils.getInstance ( ReturnDetailsActivity.this ).getDaoSession ().getReasonsDao ().queryBuilder ()
							.where ( ReasonsDao.Properties.ReasonType.eq ( ReasonsUtils.Type.ITEM_STATUS ) ).list () );
				} // End if
				// Iterate over the reasons
				for ( int i = 1 ; i < reasons.size () ; i ++ )
					// Map the reason to its code
					_reasons.put ( reasons.get ( i ).getReasonCode () , reasons.get ( i ) );
				
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
					cursor = DatabaseUtils.getInstance ( ReturnDetailsActivity.this ).getDaoSession ().getDatabase ().rawQuery ( SQL , selectionArguments );
					// Move the cursor to the first raw
					if ( cursor.moveToFirst () ) {
						do {
							// Retrieve the current currency
							currency = DatabaseUtils.getInstance ( ReturnDetailsActivity.this ).getDaoSession ().getCurrenciesDao ().readEntity ( cursor , 0 );
						} while ( cursor.moveToNext () );
					} // End if
					// Clear cursor
					cursor.close ();
					cursor = null;
				} // End if
				
				// So far here, the currency or header code objects must be valid objects
				// If not, cannot continue
				// Check if both the header code or the currency are valid
				if ( getIntent ().getStringExtra ( TRANSACTION_HEADER_CODE ) == null && currency == null ) {
					// Set flag to indicate an error
					error = true;
					// Clear the task reference
					populateList = null;
					// Exit method
					return null;
				} // End if
				
				// Declare and initialize a map used to map the return details to their item codes
				Map < String , TransactionDetails > _transactionDetails = new HashMap < String , TransactionDetails > ();
				// Determine if there is an available transaction header code
				if ( getIntent ().getStringExtra ( TRANSACTION_HEADER_CODE ) != null && transactionHeader == null ) {
					// Try to retrieve the transaction header with the specified transaction code
					transactionHeader = DatabaseUtils.getInstance ( ReturnDetailsActivity.this ).getDaoSession ().getTransactionHeadersDao ().queryBuilder ()
							.where ( TransactionHeadersDao.Properties.TransactionCode.eq ( getIntent ().getStringExtra ( TRANSACTION_HEADER_CODE ) ) ).unique ();
					// Check if the transaction header is valid
					if ( transactionHeader != null ) {
						// Refresh object
						DatabaseUtils.getInstance ( ReturnDetailsActivity.this ).getDaoSession ().getTransactionHeadersDao ().refresh ( transactionHeader );
						// Initialize the transaction details list
						transactionDetails = new ArrayList < TransactionDetails > ();
						// Retrieve the appropriate transaction details
						transactionDetails.addAll ( DatabaseUtils.getInstance ( ReturnDetailsActivity.this ).getDaoSession ().getTransactionDetailsDao ().queryBuilder ()
							.where ( TransactionDetailsDao.Properties.TransactionCode.eq ( transactionHeader.getTransactionCode () ) ).list () );
						// Iterate over all transaction details
						for ( TransactionDetails transactionDetail : transactionDetails )
							// Refresh object
							DatabaseUtils.getInstance ( ReturnDetailsActivity.this ).getDaoSession ().getTransactionDetailsDao ().refresh ( transactionDetail );
						// Retrieve the currency with the utmost priority
						currency = DatabaseUtils.getInstance ( ReturnDetailsActivity.this ).getDaoSession ().getCurrenciesDao ().queryBuilder ()
								.orderAsc ( CurrenciesDao.Properties.CurrencyPriority ).unique ();
					} // End if
					// Iterate over all transaction details
					for ( TransactionDetails transactionDetail : transactionDetails )
						// Map the current transaction detail to its item code
						_transactionDetails.put ( transactionDetail.getItemCode () , transactionDetail );
				} // End if
				
				// Retrieve all divisions
				List < Divisions > allDivisions = DatabaseUtils.getInstance ( ReturnDetailsActivity.this ).getDaoSession ().getDivisionsDao ().queryBuilder ()
						.where ( DivisionsDao.Properties.CompanyCode.eq ( companyCode ) ).list ();
				// Retrieve the divisions linked to the user
				List < Divisions > directUserDivisions = DatabaseUtils.getInstance ( ReturnDetailsActivity.this ).getDaoSession ().getDivisionsDao ()
						.queryRaw ( " WHERE CompanyCode = ? AND DivisionCode IN ( SELECT DivisionCode FROM UserDivisions WHERE CompanyCode = ? ) " ,
								new String [] { companyCode , companyCode } );
				allDivisions.removeAll ( directUserDivisions );
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
						"WHERE P." + PricesDao.Properties.CurrencyCode.columnName + " = ? " +
						"ORDER BY P." + PricesDao.Properties.ItemCode.columnName + " , ClientPriceListPriority";
				// Compute the selection arguments
				selectionArguments = new String [] { clientCode , currency.getCurrencyCode () };
				// Query DB in order to retrieve the prices
				cursor = DatabaseUtils.getInstance ( ReturnDetailsActivity.this ).getDaoSession ().getDatabase ().rawQuery ( SQL , selectionArguments );
				// Move the cursor to the first raw
				if ( cursor.moveToFirst () ) {
					do {
						// Retrieve the current price
						Prices price = DatabaseUtils.getInstance ( ReturnDetailsActivity.this ).getDaoSession ().getPricesDao ().readEntity ( cursor , 0 );
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
					ReturnDetailsActivity.this.divisions.addAll ( allUserDivisions );
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
				
				// Check if there are saved returns
				if ( retrieveReturns ) {
					// Retrieve modified returns
					returns = (ArrayList < Return >) ActivityInstance.readDataGson ( ReturnDetailsActivity.this , ReturnDetailsActivity.class.getName () , RETURN , new TypeToken < ArrayList < Return > > () {}.getType () );
					// Initialize the list used to host the selected returns
					selectedReturns = new ArrayList < Return > ();
					// Iterate over all returns
					for ( Return salesReturn : returns )
		    			// Check if the current return has valid quantities
		    			if ( salesReturn.getQuantityBig () != 0 || salesReturn.getQuantityMedium () != 0 || salesReturn.getQuantitySmall () != 0 )
		    				// The return contains at least one quantity, add it to the return list
		    				selectedReturns.add ( salesReturn );
				} // End if
				else {
					// Retrieve the client item divisions
					List < ItemDivisions > itemDivisions = EntityUtils.queryByGroup ( selectedDivisionsCodes ,
							DatabaseUtils.getInstance ( ReturnDetailsActivity.this ).getDaoSession ().getItemDivisionsDao () ,
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
							DatabaseUtils.getInstance ( ReturnDetailsActivity.this ).getDaoSession ().getItemsDao () ,
							ItemsDao.Properties.ItemCode );
					
					// Retrieve the item barcodes
					List < ItemBarcodes > itemBarcodes = EntityUtils.queryByGroup ( itemCodes ,
							DatabaseUtils.getInstance ( ReturnDetailsActivity.this ).getDaoSession ().getItemBarcodesDao () ,
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
					List < Units > units = DatabaseUtils.getInstance ( ReturnDetailsActivity.this ).getDaoSession ().getUnitsDao ().loadAll ();
					// Map all the Units using their codes
					Map < String , Units > _units = new HashMap < String , Units > ();
					// Iterate over all UOM
					for ( Units unit : units )
						// Map the unit to its code
						_units.put ( unit.getUnitCode () , unit );
					
					// Retrieve all the taxes
					List < Taxes > taxes = DatabaseUtils.getInstance ( ReturnDetailsActivity.this ).getDaoSession ().getTaxesDao ().loadAll ();
					// Declare and initialize a map of taxes
					Map < String , Taxes > _taxes = new HashMap < String , Taxes > ();
					// Iterate over all taxes
					for ( Taxes tax : taxes )
						// Map the tax object to its tax code
						_taxes.put ( tax.getTaxCode () , tax );
					
					// Determine if the view only mode is enabled
					boolean isView = getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false );
					// Determine if the return is applied
					boolean isApplied = transactionHeader == null ? false : transactionHeader.getIsProcessed () == IsProcessedUtils.isNotSync () && transactionHeader.getTransactionStatus () == StatusUtils.isAvailable ();
					// Initialize the lists used to host the returns and the selected returns
					returns = new ArrayList < Return > ();
					selectedReturns = new ArrayList < Return > ();
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
						// Create a new return using the current item and its appropriate unit of measurement
						Return salesReturn = new Return ( item , _units.get ( item.getUnitCode () ) );
						// Set the divisions codes list
						salesReturn.setDivisionCodes ( _itemDivisions.get ( item.getItemCode () ) );
						// Check that the division codes list is valid
						if ( salesReturn.getDivisionCodes () == null )
							// Initialize the division codes list
							salesReturn.setDivisionCodes ( new ArrayList < String > () );
						// Set the item bar codes list
						salesReturn.setBarCodes ( _itemBarcodes.get ( item.getItemCode () ) );
						// Set the return prices
						if ( itemPrices.containsKey ( salesReturn.getItem ().getItemCode () ) ) {
							Prices prices = itemPrices.get ( salesReturn.getItem ().getItemCode () );
							salesReturn.setPriceBig ( prices.getPriceBig () );
							salesReturn.setPriceMedium ( prices.getPriceMedium () );
							salesReturn.setPriceSmall ( prices.getPriceSmall () );
						} // End if
						// Set the item tax
						if ( ItemsUtils.isTaxable ( salesReturn.getItem () ) && _taxes.get ( salesReturn.getItem ().getTaxCode () ) != null )
							salesReturn.setTax ( _taxes.get ( salesReturn.getItem ().getTaxCode () ).getTaxPercentage () );
						// Check if there is a previously saved transaction detail for the current item
						if ( _transactionDetails.containsKey ( salesReturn.getItem ().getItemCode () ) ) {
							// Retrieve a reference to the transaction detail
							TransactionDetails transactionDetails = _transactionDetails.get ( salesReturn.getItem ().getItemCode () );
							// Set the return quantities
							salesReturn.setQuantityBig ( transactionDetails.getQuantityBig ().intValue () );
							salesReturn.setQuantityMedium ( transactionDetails.getQuantityMedium ().intValue () );
							salesReturn.setQuantitySmall ( transactionDetails.getQuantitySmall ().intValue () );
							// Set the return prices
							salesReturn.setPriceBig ( transactionDetails.getPriceBig () );
							salesReturn.setPriceMedium ( transactionDetails.getPriceMedium () );
							salesReturn.setPriceSmall ( transactionDetails.getPriceSmall () );
							// Set the return data
							salesReturn.setExpiryDate ( transactionDetails.getItemExpiryDate () );
							salesReturn.setSuggestedExpiryDate ( transactionDetails.getItemExpiryDate () );
							salesReturn.setReason ( _reasons.get ( transactionDetails.getReasonCode () ) );
							salesReturn.setSuggestedReason ( _reasons.get ( transactionDetails.getReasonCode () ) );
							salesReturn.setQuantityBig ( isView ? isApplied ? transactionDetails.getQuantityBig ().intValue () : transactionDetails.getApprovedQuantityBig ().intValue () : 0 );
							salesReturn.setQuantityMedium ( isView ? isApplied ? transactionDetails.getQuantityMedium ().intValue () : transactionDetails.getApprovedQuantityMedium ().intValue () : 0 );
							salesReturn.setQuantitySmall ( isView ? isApplied ? transactionDetails.getQuantitySmall ().intValue () : transactionDetails.getApprovedQuantitySmall ().intValue () : 0 );
							salesReturn.setExpiryDate ( isView ? transactionDetails.getItemExpiryDate () : null );
							salesReturn.setReason ( isView ? _reasons.get ( transactionDetails.getReasonCode () ) : null );
							salesReturn.setSuggestedBig ( transactionDetails.getApprovedQuantityBig ().intValue () );
							salesReturn.setSuggestedMedium ( transactionDetails.getApprovedQuantityMedium ().intValue () );
							salesReturn.setSuggestedSmall ( transactionDetails.getApprovedQuantitySmall ().intValue () );
							returns.add ( salesReturn );
							if ( isView )
								selectedReturns.add ( salesReturn );
						} // End if
					} // End for each
				} // End else
				
				// Sort the items by alphabetical order
				Collections.sort ( returns , new Comparator < Return > () {
					@Override
					public int compare ( Return return1 , Return return2 ) {
						// Sort the items 
						return return1.getItem ().getItemName ().compareToIgnoreCase ( return2.getItem ().getItemName () );
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
					// Initialize the return details
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
		protected void onPostExecute ( ArrayList < Return > returns ) {
			// Check if the activity has ended
			if ( activityEnded )
				// Do nothing
				return;
			// Determine if the activity must be finished
			if ( error ) {
				// Indicate that the activity cannot be displayed
				new AppToast ( ReturnDetailsActivity.this )
					.setIcon ( R.drawable.warning )
					.setText ( AppResources.getInstance ( ReturnDetailsActivity.this ).getString ( ReturnDetailsActivity.this , R.string.missing_client_currency_price_list_message ) )
					.setDuration ( Toast.LENGTH_LONG )
					.show ();
				// Initialize the returns list
				ReturnDetailsActivity.this.returns = new ArrayList < Return > ();
				// Exit method
				return;
			} // End if
			
			// Retrieve a reference to the secondary view
			View secondary = findViewById ( R.id.layout_return_details );
			// Display the secondary view accordingly
			secondary.setVisibility ( displayTransactionDetails ? View.VISIBLE : View.GONE );
    		// Enable the main list accordingly
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( ! displayTransactionDetails );
    		// Determine if an return is undergoing any modifications
    		if ( displayTransactionDetails )
	    		// Restore the secondary view
    			initializeSecondaryView ( true );
    		
	    	// Declare and initialize a new reason adapter used to populate the reason spinner
    		ReasonAdapter reasonAdapter = new ReasonAdapter ( ReturnDetailsActivity.this , android.R.layout.simple_spinner_item , reasons );
			// Sets the layout resource to create the drop down views
    		reasonAdapter.setDropDownViewResource ( R.layout.data_management_activity_connection_item );
    		// Set the spinner adapter
    		( (Spinner) secondary.findViewById ( R.id.spinner_reason ) ).setAdapter ( reasonAdapter );
    		
			// Determine if the request code is INFO
			if ( requestCode != REQUEST_CODE_INFO )
				// Set a new adapter
				setListAdapter ( getReturnDetailsAdapter ( R.layout.sales_order_details_activity_item , new ArrayList < Return > ( returns ) , currency.getCurrencyRounding () , true ) );
			// Otherwise the request code is INFO
			else {
				// Declare and initialize a multiple list adapter
				MultipleListAdapter adapter = new MultipleListAdapter ( ReturnDetailsActivity.this );
				// Add the return details adapter
				adapter.addSection ( new Section ( AppResources.getInstance ( ReturnDetailsActivity.this ).getString ( ReturnDetailsActivity.this , R.string.return_details_label ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
						new ReturnInfoAdapter ( ReturnDetailsActivity.this , R.layout.order_info_item , details ) );
				// Add the return adapter
				adapter.addSection ( new Section ( AppResources.getInstance ( ReturnDetailsActivity.this ).getString ( ReturnDetailsActivity.this , R.string.return_products_label ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
						getReturnDetailsAdapter ( R.layout.sales_order_details_activity_item , selectedReturns , currency.getCurrencyRounding () , ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) ) );
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
	 * Performs all the required initialization for the return details :
	 * <ul>
	 * <li>The {@link #selectedReturns} list is populated.</li>
	 * <li>The {@link #details} list is populated.</li>
	 * </ul>
	 */
	protected void initializeDetails () {
		// Compute the return details
		double netValue = 0;
//		double taxes = 0;
		double totalValue = 0;
		// Iterate over all the selected returns
		for ( Return salesReturn : selectedReturns ) {
			// Compute the current net value
			double _netValue = salesReturn.getQuantityBig () * salesReturn.getPriceBig ()
					+ salesReturn.getQuantityMedium () * salesReturn.getPriceMedium ()
					+ salesReturn.getQuantitySmall () * salesReturn.getPriceSmall ();
			// Compute the current tax value
			double _taxes = 0;
			// Check if the item is taxable
			if ( salesReturn.getTax () != 0 )
				// Compute the current taxes
				_taxes = _netValue * salesReturn.getTax () / 100;
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
		// Initialize the list of return details
		details = new ArrayList < ReturnInfo > ();
		// Populate the details list
		details.add ( new ReturnInfo ( ReturnInfo.ID.NET_VALUE , AppResources.getInstance ( this ).getString ( this , R.string.net_amount_label ) , moneyFormat.format ( netValue ) ) );
//		details.add ( new ReturnInfo ( ReturnInfo.ID.TAXES , AppResources.getInstance ( this ).getString ( this , R.string.taxes_label ) , moneyFormat.format ( taxes ) ) );
		details.add ( new ReturnInfo ( ReturnInfo.ID.TOTAL_VALUE , AppResources.getInstance ( this ).getString ( this , R.string.total_value_label ) , moneyFormat.format ( totalValue ) ) );
	}
	
	/**
	 * Refreshes / recomputes all the return details (if any).
	 */
	private void refreshDetails () {
		// Check if the selected returns and return details lists are valid
		if ( selectedReturns == null || details == null )
			// Invalid list, do nothing
			return;
		
		// Compute the return details
		double netValue = 0;
		double taxes = 0;
		double totalValue = 0;
		// Iterate over all the selected returns
		for ( Return salesReturn : selectedReturns ) {
			// Compute the current net value
			double _netValue = salesReturn.getQuantityBig () * salesReturn.getPriceBig ()
					+ salesReturn.getQuantityMedium () * salesReturn.getPriceMedium ()
					+ salesReturn.getQuantitySmall () * salesReturn.getPriceSmall ();
			// Compute the current tax value
			double _taxes = 0;
			// Check if the item is taxable
			if ( salesReturn.getTax () != 0 )
				// Compute the current taxes
				_taxes = _netValue * salesReturn.getTax () / 100;
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
		for ( ReturnInfo detail : details ) {
			// Check if the current detail is about the return net value
			if ( detail.getId () == ReturnInfo.ID.NET_VALUE ) {
				// Update the return net value
				detail.setValue ( moneyFormat.format ( netValue ) );
			} // End if
			// Check if the current detail is about the return taxes
			else if ( detail.getId () == ReturnInfo.ID.TAXES ) {
				// Update the return taxes
				detail.setValue ( moneyFormat.format ( taxes ) );
			} // End else if
			// Check if the current detail is about the return total value
			else if ( detail.getId () == ReturnInfo.ID.TOTAL_VALUE ) {
				// Update the return total value
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
	private class FilterList extends AsyncTask < Void , Void , ArrayList < Return > > {
		
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
		protected ArrayList < Return > doInBackground ( Void ... params ) {
			// Check if the list of returns is valid
			if ( returns == null || returns.isEmpty () )
				// Invalid returns list
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
		protected void onPostExecute ( ArrayList < Return > returns ) {
			// Check if the request code is INFO
			if ( requestCode == REQUEST_CODE_INFO )
				// Cannot filter, do nothing
				return;
			// Check if the result is valid
			if ( returns == null )
				// Invalid result, do nothing
				return;
			// Check if the search query has changed
			if ( ! ReturnDetailsActivity.this.searchQuery.equals ( searchQuery ) )
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
				// Clear the previous list of returns
				( (ArrayAdapter < Return >) getListAdapter () ).clear ();
				// Add the new filtered list of returns
				( (ArrayAdapter < Return >) getListAdapter () ).addAll ( returns );
				// Notifies the attached observers that the underlying data has been changed
				( (ArrayAdapter < Return >) getListAdapter () ).notifyDataSetChanged ();
			} catch ( Exception exception ) {
				// Cannot cast to array adapter of return objects
			} // End try-catch block
		}
		
	}
	
}
