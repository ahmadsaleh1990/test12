package me.SyncWise.Android.Modules.DirectReturn;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.AppToast;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Vibration;
import me.SyncWise.Android.Database.ClientCurrenciesDao;
import me.SyncWise.Android.Database.ClientItemHistory;
import me.SyncWise.Android.Database.ClientItemHistoryDao;
import me.SyncWise.Android.Database.ClientMustStockList;
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
import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Database.Prices;
import me.SyncWise.Android.Database.PricesDao;
import me.SyncWise.Android.Database.Reasons;
import me.SyncWise.Android.Database.ReasonsDao;
import me.SyncWise.Android.Database.ReasonsUtils;
import me.SyncWise.Android.Database.StatusUtils;
import me.SyncWise.Android.Database.Taxes;
import me.SyncWise.Android.Database.TransactionDetails;
import me.SyncWise.Android.Database.TransactionDetailsDao;
import me.SyncWise.Android.Database.TransactionDetailsUtils;
import me.SyncWise.Android.Database.TransactionHeaders;
import me.SyncWise.Android.Database.TransactionHeadersDao;
import me.SyncWise.Android.Database.TransactionHeadersUtils;
import me.SyncWise.Android.Database.TransactionSequencesDao;
import me.SyncWise.Android.Database.Units;
import me.SyncWise.Android.Database.Users;
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Database.Vehicles;
import me.SyncWise.Android.Database.VehiclesStock;
import me.SyncWise.Android.Database.VehiclesStockDao;
import me.SyncWise.Android.Database.Visits;
import me.SyncWise.Android.Gson.BaseTimerListActivity;
import me.SyncWise.Android.Modules.BarcodeIntegration.IntentIntegrator;
import me.SyncWise.Android.Modules.BarcodeIntegration.IntentResult;
import me.SyncWise.Android.Modules.Call.CallAction;
import me.SyncWise.Android.Modules.FilterDivisions.FilterDivisionsActivity;
import me.SyncWise.Android.Modules.Journey.Call;
import me.SyncWise.Android.Modules.Printing.PrintingActivity;
import me.SyncWise.Android.Modules.SalesRFR.CashOrderRFRAdapter;
import me.SyncWise.Android.Modules.SalesRFR.OrderRFRInfo;

 
 
import me.SyncWise.Android.Modules.DirectReturn.DirectReturnDetailAdapter.ViewHolder;
import me.SyncWise.Android.SectionsList.MultipleListAdapter;
import me.SyncWise.Android.SectionsList.Section;
import me.SyncWise.Android.Utilities.ActivityInstance;
import me.SyncWise.Android.Utilities.ClientCard;
import me.SyncWise.Android.Widgets.Baguette;
import me.SyncWise.Android.Widgets.CustomLinearLayout;
import me.SyncWise.Android.Widgets.QuickAction.ActionItem;
import me.SyncWise.Android.Widgets.QuickAction.QuickAction;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
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
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
/**
 * Activity implemented to view all the performed movements.
 * 
 * @author Ahmad
 *
 */
public class DirectReturnDetailsActivity  extends BaseTimerListActivity implements SearchView.OnQueryTextListener , QuickAction.OnActionItemClickListener{
	
	/**
	 * Boolean used to indicate if the activity is displayed on the screen.
	 */
	public static boolean isDisplayed;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #requestCode}.
	 */
	public static final String REQUEST_CODE = DirectReturnDetailsActivity.class.getName () + ".REQUEST_CODE";
	
	/**
	 * Integer used to host the request code.
	 * @see #REQUEST_CODE_NEW
	 * @see #REQUEST_CODE_INFO
	 */
	public int requestCode;
	
	/**
	 * Constant integer holding the request code to create a new sales order.
	 */
	public static final int REQUEST_CODE_NEW = 1;
	
	/**
	 * Constant integer holding the request code to display the sales order info.
	 */
	public static final int REQUEST_CODE_INFO = 2;
	
	/**
	 * Constant integer holding the request code used to print orders.
	 */
	private static final int REQUEST_CODE_PRINT = 10;

	/**
	 * Bundle key used to put/retrieve the content of the edit flag.
	 */
	public static final String IS_EDIT = DirectReturnDetailsActivity.class.getName () + ".IS_EDIT";
	
	/**
	 * Bundle key used to put/retrieve the content of the view only flag.
	 */
	public static final String IS_VIEW_ONLY = DirectReturnDetailsActivity.class.getName () + ".IS_VIEW_ONLY";
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #orderModification}.
	 */
	private static final String ORDER_MODIFICATION = DirectReturnDetailsActivity.class.getName () + ".ORDER_MODIFICATION";
	
	/**
	 * Reference to the {@link me.SyncWise.Android.Modules.SalesRFR.OrderDirectReturn OrderDirectReturn} object being modified.
	 */
	protected OrderDirectReturn orderModification;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #displayOrderNote}.
	 */
	private static final String DISPLAY_ORDER_NOTE = DirectReturnDetailsActivity.class.getName () + ".DISPLAY_ORDER_NOTE";
	
	/**
	 * Boolean used to determine whether to display the order note UI or not.<br>
	 * This boolean is mainly used to save the order state.
	 */
	protected boolean displayOrderNote;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #displayClientReferenceNumber}.
	 */
	private static final String DISPLAY_CLIENT_REFERENCE_NUMBER = DirectReturnDetailsActivity.class.getName () + ".DISPLAY_CLIENT_REFERENCE_NUMBER";
	
	/**
	 * Boolean used to determine whether to display the client reference number UI or not.<br>
	 * This boolean is mainly used to save the order state.
	 */
	protected boolean displayClientReferenceNumber;
	
	/**
	 * Bundle key used to put/retrieve the content of the transaction header code.<br>
	 * This is used mainly to edit a sales order.
	 */
	public static final String TRANSACTION_HEADER_CODE = DirectReturnDetailsActivity.class.getName () + ".TRANSACTION_HEADER_CODE";
	
	/**
	 * Bundle key used to put/retrieve the content of the current call.
	 */
	public static final String CALL = DirectReturnDetailsActivity.class.getName () + ".CALL";
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #visit}.
	 */
	public static final String VISIT = DirectReturnDetailsActivity.class.getName () + ".VISIT";
	
	/**
	 * {@link me.SyncWise.Android.Database.Visits Visits} object holding a reference to the call in progress.
	 */
	protected Visits visit = null;
	
	/**
	 * List of {@link me.SyncWise.Android.Database.Divisions Divisions} objects used to host all the available objects for the current transaction.
	 */
	protected ArrayList < Divisions > divisions;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #SELECTED_DIVISIONS}.
	 */
	public static final String SELECTED_DIVISIONS = DirectReturnDetailsActivity.class.getName () + ".SELECTED_DIVISIONS";
	
	/**
	 * List of string used to host the selected divisions codes, in order to filter the items / sales orders list.
	 */
	protected ArrayList < String > selectedDivisionsCodes;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #orders}.
	 */
	public static final String ORDERS = DirectReturnDetailsActivity.class.getName () + ".ORDERS";
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.SalesRFR.OrderDirectReturn OrderDirectReturn} objects used to define the sales orders.<br>
	 * <b>NOTE :</b> <br>
	 * If a filter is applied, this list remains untouched. A new list is generated, filtered and passed to the list adapter.
	 */
	protected ArrayList < OrderDirectReturn > orders;
	
	/**
	 * Boolean used to indicate if there saved orders that should be retrieved.
	 */
	protected boolean retrieveOrders;
	
	/**
	 * Reference to the orders list population task.
	 */
	protected static PopulateList populateList;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #searchQuery}.
	 */
	public static final String SEARCH_QUERY = DirectReturnDetailsActivity.class.getName () + ".SEARCH_QUERY";
	
	/**
	 * String holding the search query.
	 */
	private String searchQuery;
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.SalesRFR.OrderDirectReturn OrderDirectReturn} objects used to define the selected sales orders.
	 */
	protected ArrayList < OrderDirectReturn > selectedOrders;
	private ArrayList<OrderRFRInfo> list1 = new ArrayList<OrderRFRInfo>();
	private String cash="";
	/**
	 * List of {@link me.SyncWise.Android.Modules.SalesRFR.OrderDirectReturnInfo OrderDirectReturnInfo} objects used to define the sales orders details.
	 */
	protected ArrayList < OrderDirectReturnInfo > details;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #selectedYear}.
	 */
	public static final String SELECTED_YEAR = DirectReturnDetailsActivity.class.getName () + ".SELECTED_YEAR";
	
	/**
	 * Integer holding the selected year of the delivery date.
	 */
	protected int selectedYear;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #selectedMonth}.
	 */
	public static final String SELECTED_MONTH = DirectReturnDetailsActivity.class.getName () + ".SELECTED_MONTH";
	
	/**
	 * Integer holding the selected month of the delivery date.
	 */
	protected int selectedMonth;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #selectedDay}.
	 */
	public static final String SELECTED_DAY = DirectReturnDetailsActivity.class.getName () + ".SELECTED_DAY";
	
	/**
	 * Integer holding the selected day of the delivery date.
	 */
	protected int selectedDay;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #note}.
	 */
	public static final String NOTE = DirectReturnDetailsActivity.class.getName () + ".NOTE";
	
	/**
	 * String holding the sales order note.
	 */
	protected String note;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #currency}.
	 */
	public static final String CURRENCY = DirectReturnDetailsActivity.class.getName () + ".CURRENCY";
	
	/**
	 * Reference to the currency object.
	 */
	protected Currencies currency;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #transactionHeader}.
	 */
	public static final String TRANSACTION_HEADER = DirectReturnDetailsActivity.class.getName () + ".TRANSACTION_HEADER";
	
	/**
	 * Reference to the transaction header stored in DB (if previously saved)
	 */
	protected TransactionHeaders transactionHeader;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #transactionDetails}.
	 */
	public static final String TRANSACTION_DETAILS = DirectReturnDetailsActivity.class.getName () + ".TRANSACTION_DETAILS";
	
	/**
	 * List of transaction details references stored in DB (if previously saved)
	 */
	protected ArrayList < TransactionDetails > transactionDetails;
	
	/**
	 * Reference to a {@link me.SyncWise.Android.Widgets.QuickAction.QuickAction QuickAction} used in this activity.
	 */
	private QuickAction quickAction;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #clientReferenceNumber}.
	 */
	private static final String CLIENT_REFERENCE_NUMBER = DirectReturnDetailsActivity.class.getName () + ".CLIENT_REFERENCE_NUMBER";
	
	/**
	 * String used to host the client reference number
	 */
	private String clientReferenceNumber;
	
	/**
	 * String hosting the selected item code.
	 */
	public static String selectedItemCode;

	/**
	 * A {@link android.view.View View} reference holding the selected list item holding the approprirate RFR detail line.
	 */
	public static View selectedView;
	
	/**
	 * String used to host he selected reason code.
	 */
	public static String selectedReasonCode;
	
	/**
	 * {@link android.widget.Spinner Spinner} reference used to display the various RFR reasons.
	 */
	private Spinner sp_reasons;
	
	/**
	 * A {@link java.util.Calendar Calendar} object used to host the temporary value of the expiry date.
	 */
	public static Calendar expiryDateEdit ;

	/**
	 * List of {@link me.SyncWise.Android.Modules.SalesRFR.OrderDirectReturn OrderDirectReturn} objects used to define the RFR lines in the info screen.<br>
	 */
	private ArrayList < OrderDirectReturn > infoOrderDirectReturn;
	
	private static final String ERROR = DirectReturnDetailsActivity.class.getName () + ".ERROR";
	
	private boolean error;

	/**
	 * Helper Class used to manage the activity's action items IDs for the quick action widgets.<br>
	 * The main purpose of this class is to maintain a unique identifier for action items within the activity.
	 * 
	 * @author Elias
	 *
	 */
	protected class ActionItemID {
		public static final int REMOVE = 1;
	}
	
	/**
	 * Performs all necessary setup in order to properly display the quick action widget.
	 */
    protected void setupQuickAction () {
		// Initialize the quick action widgets
    	quickAction = new QuickAction ( this , QuickAction.VERTICAL );
		// Action Item : Remove
		ActionItem remove = new ActionItem ( ActionItemID.REMOVE ,
				AppResources.getInstance ( this ).getString ( this , R.string.quick_action_sales_order_remove_label ) ,
				getResources ().getDrawable ( R.drawable.quick_action_cancel ) );
    	// Populate the quick action widget with quick action items
		quickAction.addActionItem ( remove );
		// Assign an action item click listener
		quickAction.setOnActionItemClickListener ( this );
    }
    
    /**
     * Setter - {@link #orderModification}
     * 
     * @param orderModification	Reference to the {@link me.SyncWise.Android.Modules.SalesRFR.OrderDirectReturn OrderDirectReturn} object being modified.
     */
    public void setOrderModification ( final OrderDirectReturn orderModification ) {
    	this.orderModification = orderModification;
    }
    
    /**
     * Getter - {@link #currency}
     * 
     * @return Reference to the currency object.
     */
    public Currencies getCurrency () {
    	return currency;
    }
    
    /**
     * Returns the appropriate money format used to properly display monetary values.
     * 
     * @return	A {@link java.text.DecimalFormat DecimalFormat} object used to format and display monetary values.
     */
    public DecimalFormat getMoneyFormat () {
    	// Check if the list view adapter AND currency object are valid
    	if ( getListAdapter () == null && currency == null )
    		// Invalid data
    		return null;
    	// Determine if the list view adapter's instance is of SalesRFRAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof DirectReturnDetailAdapter ) {
    		// Return the adapter's money format
    		return ( (DirectReturnDetailAdapter) getListAdapter () ).getMoneyFormat ();
    	} // End if
    	// Determine if the list view adapter's instance is MultipleListAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof MultipleListAdapter ) {
    		// Return the SalesOrderDetailsAdapter's money format
    		return ( (DirectReturnDetailAdapter) ( (MultipleListAdapter) getListAdapter () ).getAdapter ( ( (MultipleListAdapter) getListAdapter () ).getCount () - 1 ) )
    				.getMoneyFormat ();
    	} // End if
		// Otherwise declare and initialize a string builder used to compute the decimal format pattern
		StringBuilder pattern = new StringBuilder ();
		// Initialize the whole part of decimal format pattern
		pattern.append ( " #,##0" );
		// Determine if the currency has a valid currency rounding (at least one digit after the decimal point)
		if ( currency.getCurrencyRounding () >= 1 ) {
			// The current currency as at least one digit after the decimal point
			pattern.append ( ".0" );
			// Iterate over all the digits after the decimal point
			for ( int i = 1 ; i < currency.getCurrencyRounding () ; i ++ )
				// Append the digit
				pattern.append ( "0" );
		} // End if
		// Initialize and return the appropriate decimal format
		return new DecimalFormat ( pattern.toString () );
    }
    
    /**
     * Returns a boolean used to indicate if the number picker should be enabled or not.
     * 
     * @return	Flag indicating if the number picker should be enabled or not.
     */
    public Boolean getUsePicker () {
    	// Check if the list view adapter 
    	if ( getListAdapter () == null )
    		// Invalid data
    		return null;
    	// Determine if the list view adapter's instance is of SalesOrderDetailsAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof DirectReturnDetailAdapter ) {
    		// Return the adapter's money format
    		return ( (DirectReturnDetailAdapter) getListAdapter () ).getUsePicker ();
    	} // End if
    	// Determine if the list view adapter's instance is MultipleListAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof MultipleListAdapter ) {
    		// Return the SalesOrderDetailsAdapter's money format
    		return ( (DirectReturnDetailAdapter) ( (MultipleListAdapter) getListAdapter () ).getAdapter ( ( (MultipleListAdapter) getListAdapter () ).getCount () - 1 ) )
    				.getUsePicker ();
    	} // End if
		// Otherwise return null
		return null;
    }
    
    /**
     * Returns the appropriate String used to hold the code label.
     * 
     * @return	String used to hold the code label.
     */
    public String getCodeLabel () {
    	// Check if the list view adapter 
    	if ( getListAdapter () == null )
    		// Invalid data
    		return null;
    	// Determine if the list view adapter's instance is of SalesOrderDetailsAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof DirectReturnDetailAdapter ) {
    		// Return the adapter's money format
    		return ( (DirectReturnDetailAdapter) getListAdapter () ).getCodeLabel ();
    	} // End if
    	// Determine if the list view adapter's instance is MultipleListAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof MultipleListAdapter ) {
    		// Return the SalesOrderDetailsAdapter's money format
    		return ( (DirectReturnDetailAdapter) ( (MultipleListAdapter) getListAdapter () ).getAdapter ( ( (MultipleListAdapter) getListAdapter () ).getCount () - 1 ) )
    				.getCodeLabel ();
    	} // End if
		// Otherwise return null
		return null;
    }
    
    /**
     * Returns the appropriate String used to host the total label.
     * 
     * @return	String used to host the total label.
     */
    public String getTotalLabel () {
    	// Check if the list view adapter 
    	if ( getListAdapter () == null )
    		// Invalid data
    		return null;
    	// Determine if the list view adapter's instance is of SalesOrderDetailsAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof DirectReturnDetailAdapter ) {
    		// Return the adapter's money format
    		return ( (DirectReturnDetailAdapter) getListAdapter () ).getTotalLabel ();
    	} // End if
    	// Determine if the list view adapter's instance is MultipleListAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof MultipleListAdapter ) {
    		// Return the SalesOrderDetailsAdapter's money format
    		return ( (DirectReturnDetailAdapter) ( (MultipleListAdapter) getListAdapter () ).getAdapter ( ( (MultipleListAdapter) getListAdapter () ).getCount () - 1 ) )
    				.getTotalLabel ();
    	} // End if
		// Otherwise return null
		return null;
    }
    
    /**
     * Returns the appropriate String used to host the reason label.
     * 
     * @return	String used to host the reason label.
     */
    public String getReasonLabel () {
    	// Check if the list view adapter 
    	if ( getListAdapter () == null )
    		// Invalid data
    		return null;
    	// Determine if the list view adapter's instance is of SalesOrderDetailsAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof DirectReturnDetailAdapter ) {
    		// Return the adapter's money format
    		return ( (DirectReturnDetailAdapter) getListAdapter () ).getReasonLabel ();
    	} // End if
    	// Determine if the list view adapter's instance is MultipleListAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof MultipleListAdapter ) {
    		// Return the SalesOrderDetailsAdapter's money format
    		return ( (DirectReturnDetailAdapter) ( (MultipleListAdapter) getListAdapter () ).getAdapter ( ( (MultipleListAdapter) getListAdapter () ).getCount () - 1 ) )
    				.getReasonLabel ();
    	} // End if
		// Otherwise return null
		return null;
    }
    
    /**
     * Returns the appropriate String used to host the expiry date label.
     * 
     * @return	String used to host the expiry date label.
     */
    public String getExpiryDateLabel () {
    	// Check if the list view adapter 
    	if ( getListAdapter () == null )
    		// Invalid data
    		return null;
    	// Determine if the list view adapter's instance is of SalesOrderDetailsAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof DirectReturnDetailAdapter ) {
    		// Return the adapter's money format
    		return ( (DirectReturnDetailAdapter) getListAdapter () ).getExpiryDateLabel ();
    	} // End if
    	// Determine if the list view adapter's instance is MultipleListAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof MultipleListAdapter ) {
    		// Return the SalesOrderDetailsAdapter's money format
    		return ( (DirectReturnDetailAdapter) ( (MultipleListAdapter) getListAdapter () ).getAdapter ( ( (MultipleListAdapter) getListAdapter () ).getCount () - 1 ) )
    				.getExpiryDateLabel ();
    	} // End if
		// Otherwise return null
		return null;
    }
    
    /**
     * Returns the appropriate Integer hosting the brown color.
     * 
     * @return	Integer hosting the brown color.
     */
    public Integer getBrownColor () {
    	// Check if the list view adapter 
    	if ( getListAdapter () == null )
    		// Invalid data
    		return null;
    	// Determine if the list view adapter's instance is of SalesOrderDetailsAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof DirectReturnDetailAdapter ) {
    		// Return the adapter's money format
    		return ( (DirectReturnDetailAdapter) getListAdapter () ).getBrownColor ();
    	} // End if
    	// Determine if the list view adapter's instance is MultipleListAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof MultipleListAdapter ) {
    		// Return the SalesOrderDetailsAdapter's money format
    		return ( (DirectReturnDetailAdapter) ( (MultipleListAdapter) getListAdapter () ).getAdapter ( ( (MultipleListAdapter) getListAdapter () ).getCount () - 1 ) )
    				.getBrownColor ();
    	} // End if
		// Otherwise return null
		return null;
    }
    
    /**
     * Returns the appropriate String used to host the new line character.
     * 
     * @return	String used to host the new line character.
     */
    public String getNewLine () {
    	// Check if the list view adapter 
    	if ( getListAdapter () == null )
    		// Invalid data
    		return null;
    	// Determine if the list view adapter's instance is of SalesOrderDetailsAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof DirectReturnDetailAdapter ) {
    		// Return the adapter's money format
    		return ( (DirectReturnDetailAdapter) getListAdapter () ).getNewLine ();
    	} // End if
    	// Determine if the list view adapter's instance is MultipleListAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof MultipleListAdapter ) {
    		// Return the SalesOrderDetailsAdapter's money format
    		return ( (DirectReturnDetailAdapter) ( (MultipleListAdapter) getListAdapter () ).getAdapter ( ( (MultipleListAdapter) getListAdapter () ).getCount () - 1 ) )
    				.getNewLine ();
    	} // End if
		// Otherwise return null
		return null;
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
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.sales_return_activity_titles ) );
		// Set the activity content from a layout resource.
		setContentView ( R.layout.sales_order_details_activity );
		// Enable fast scrolling
		getListView ().setFastScrollEnabled ( true );
		// Define the empty list view
		getListView ().setEmptyView ( findViewById ( R.id.empty_list_view ) );
		// Display empty list label
		( (TextView) findViewById ( R.id.empty_list_view ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_items_list_label ) );
		
		// Retrieve the visit object
		visit = (Visits) getIntent ().getSerializableExtra ( VISIT );
		
		// Check if both the visit and the call are valid
		if ( getIntent ().getSerializableExtra ( CALL ) == null || visit == null ) {
			// Indicate that the activity cannot be displayed
			new AppToast ( this ).setIcon ( R.drawable.warning ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.invalid_selection_label ) ).show ();
			// Finish activity
			finish ();
			// Exit method
			return;
		} // End if
		// Check if the visit object is valid
		if ( visit != null )
			// Refresh the visit object
			DatabaseUtils.getInstance ( this ).getDaoSession ().getVisitsDao ().refresh ( visit );

		// Initialize the search query (if not initialized)
		if ( searchQuery == null )
			searchQuery = "";

        // Perform the quick action setup
		setupQuickAction ();
		
		
				OrderRFRInfo i = new	OrderRFRInfo ( 1 , "1" , "" );
		    	
		    	list1.add(i);
		// Initialize the sales order note
		note = "";
		
		// Initialize the delivery date
		Calendar today = Calendar.getInstance ();
		selectedYear = today.get ( Calendar.YEAR );
		selectedMonth = today.get ( Calendar.MONTH );
		selectedDay = today.get ( Calendar.DATE );
		
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
			retrieveOrders = savedInstanceState.getBoolean ( ORDERS );
			requestCode = savedInstanceState.getInt ( REQUEST_CODE );
			selectedYear = savedInstanceState.getInt ( SELECTED_YEAR );
			selectedMonth = savedInstanceState.getInt ( SELECTED_MONTH );
			selectedDay = savedInstanceState.getInt ( SELECTED_DAY );
			note = savedInstanceState.getString ( NOTE );
			currency = (Currencies) savedInstanceState.getSerializable ( CURRENCY );
			clientReferenceNumber = savedInstanceState.getString ( CLIENT_REFERENCE_NUMBER );
			transactionHeader = (TransactionHeaders) savedInstanceState.getSerializable ( TRANSACTION_HEADER );
			transactionDetails = (ArrayList < TransactionDetails >) savedInstanceState.getSerializable ( TRANSACTION_DETAILS );
			displayOrderNote = savedInstanceState.getBoolean ( DISPLAY_ORDER_NOTE );
			displayClientReferenceNumber = savedInstanceState.getBoolean ( DISPLAY_CLIENT_REFERENCE_NUMBER );
			orderModification = (OrderDirectReturn) savedInstanceState.getSerializable ( ORDER_MODIFICATION );
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
		
		// Hide the sales order detail layout
		findViewById ( R.id.layout_order_detail ).setVisibility ( View.GONE );
		// Hide the sales order note layout
		findViewById ( R.id.layout_order_note ).setVisibility ( View.GONE );
		// Hide the sales order note layout
		findViewById ( R.id.layout_client_reference_number ).setVisibility ( View.GONE );
		
		// Check if the info screen is displayed
		if ( requestCode != REQUEST_CODE_INFO )
			// Info screen NOT displayed, make sure the RFR layout is visible
			findViewById ( R.id.layout_rfr ).setVisibility ( View.VISIBLE );
		
		// Display the expiry date
		displayExpiryDate ();
		// Retrieve a reference to the reasons spinner
		sp_reasons = (Spinner) findViewById ( R.id.sp_rfr_reason );
		// Retrieve the RFR reasons
		ArrayList < Reasons > reasons = new ArrayList < Reasons > ();
		reasons.add ( new Reasons ( null , null , null , "----------" , null , null , null , null , null , null ) );
		reasons .addAll ( DatabaseUtils.getInstance ( this ).getDaoSession ().getReasonsDao ().queryBuilder ()
				.where ( ReasonsDao.Properties.ReasonType.eq ( ReasonsUtils.Type.ITEM_STATUS ) ).list () );
		// Set the reason spinner adapter
		sp_reasons.setAdapter ( new ArrayAdapter < Reasons > ( this , android.R.layout.simple_list_item_1 , reasons ) );
		// Assign a click listener to the set expiry date button
		findViewById ( R.id.button_date_picker ).setOnClickListener ( new OnClickListener () {
			
			/*
			 * Called when a view has been clicked
			 *
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick ( View view ) {
				// Retrieve current date
				Calendar now = Calendar.getInstance ();
				// Display date picker
				AppDialog.getInstance ().displayDatePicker ( DirectReturnDetailsActivity.this ,
						expiryDateEdit != null ? expiryDateEdit.get ( Calendar.YEAR ) : now.get ( Calendar.YEAR ) ,
						expiryDateEdit != null ? expiryDateEdit.get ( Calendar.MONTH ) : now.get ( Calendar.MONTH ) ,
						expiryDateEdit != null ? expiryDateEdit.get ( Calendar.DAY_OF_MONTH ) : now.get ( Calendar.DAY_OF_MONTH ) ,
						null , false , true , new DatePickerDialog.OnDateSetListener () {
							@Override
							public void onDateSet ( DatePicker view , int year , int monthOfYear , int dayOfMonth ) {
								// Check if the expiry date is valid
								if ( expiryDateEdit == null )
									// Initialize the expiry date
									expiryDateEdit = Calendar.getInstance ();
								// Update the expiry date
								expiryDateEdit.set ( year , monthOfYear , dayOfMonth , 0 , 0 , 0 );
								expiryDateEdit.set ( Calendar.MILLISECOND , 0 );
								// Retrieve the minimum number of days for the expiry date
								int minExpiryDays = PermissionsUtils.getRFRMinExpiryDate ( DirectReturnDetailsActivity.this , DatabaseUtils.getCurrentUserCode ( DirectReturnDetailsActivity.this ) , DatabaseUtils.getCurrentCompanyCode ( DirectReturnDetailsActivity.this ) );
								// Check if the number is valid
								if ( minExpiryDays >= 0 ) {
									// Retrieve the current date
									Calendar now = Calendar.getInstance ();
									// Add the required min period
									now.add ( Calendar.DAY_OF_MONTH , minExpiryDays );
									// Check if the expiry date min period is not reached
									if ( expiryDateEdit.before ( now ) ) {
										// Indicate that the expiry date is missing
										Baguette.showText ( DirectReturnDetailsActivity.this ,
												AppResources.getInstance ( DirectReturnDetailsActivity.this ).getString ( DirectReturnDetailsActivity.this , R.string.rfr_min_expiry_date_messages ) ,
												Baguette.BackgroundColor.RED );
										// Exit method
										return;
									} // End if
								} // End if
								// Display the expiry date
								displayExpiryDate ();
								// Make sure all input variables are valid
								if ( selectedView != null && selectedItemCode != null && selectedReasonCode != null && orderModification.containsReasonCode ( selectedReasonCode ) )
									// Set the expiry date 
									orderModification.setExpiryDate ( selectedReasonCode , expiryDateEdit.getTime () );
								// Update the selected view
								updateOrder ( selectedView );
								// Refresh the list
								( (BaseAdapter) getListView().getAdapter() ).notifyDataSetChanged (); 
							}
						}  , null );
			}
		});
		// Assign an item selected listener to the reasons spinner
		sp_reasons.setOnItemSelectedListener ( new OnItemSelectedListener () {

			/*
			 * Callback method to be invoked when an item in this view has been selected.
			 *
			 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView, android.view.View, int, long)
			 */
			@Override
			public void onItemSelected ( AdapterView < ? > parent , View view , int position , long id ) {
				// Check if a reason is selected
				if ( sp_reasons.getSelectedItemPosition () == 0 ) {
					// Clear variables
					selectedReasonCode = null;
					expiryDateEdit = null;
					// Display expiry date
					displayExpiryDate ();
					// Exit method
					return;
				} // End if
				// Otherwise a reason is selected
				// Retrieve the reason name
				selectedReasonCode = ( (Reasons) sp_reasons.getSelectedItem () ).getReasonCode ();
				// Make sure a valid reason and view are selected
				if ( selectedReasonCode != null && selectedView != null ) {
					// Clear the expiry date
					expiryDateEdit = null;
					// Make sure the expiry date is valid for the selected reason
					if ( ( (ViewHolder) selectedView.getTag () ).order.getExpiryDate ( selectedReasonCode ) != null ) {
						// Set the expiry date
						expiryDateEdit = Calendar.getInstance ();
						expiryDateEdit.setTime ( ( (ViewHolder) selectedView.getTag () ).order.getExpiryDate ( selectedReasonCode ) );
					} // End if
					// Display the corresponding numbers for the selected reason
					( (ViewHolder) selectedView.getTag () ).pickerBig.setNumber ( ( (ViewHolder) selectedView.getTag () ).order.getQuantityBig ( selectedReasonCode ) );
					( (ViewHolder) selectedView.getTag () ).pickerMedium.setNumber ( ( (ViewHolder) selectedView.getTag () ).order.getQuantityMedium ( selectedReasonCode ) );
					( (ViewHolder) selectedView.getTag () ).pickerSmall.setNumber ( ( (ViewHolder) selectedView.getTag () ).order.getQuantitySmall ( selectedReasonCode ) );
					// Display expiry date
					displayExpiryDate ();
					// Refresh list
					( (BaseAdapter) getListView ().getAdapter () ).notifyDataSetChanged (); 
				} // End if
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
		} );
		
		// Populate the list
		populate ();
	}
	
	/**
	 * Retrieve all the orders asynchronously.
	 */
	protected void populate () {
		// Retrieve all the orders asynchronously
		populateList = new PopulateList ();
		populateList.execute ();
	}
	
	/**
	 * Displays the selected expiry date in the RFR layout.
	 */
	private void displayExpiryDate () {
		// Retrieve a reference to the expiry date label
		String expiryDateLabel = AppResources.getInstance ( this ).getString ( this , R.string.expiry_date_label ) + " : ";
		// Check if the expiry date is valid
		if ( expiryDateEdit != null )
			// Display the expiry date
			( (TextView) findViewById ( R.id.label_date_picker ) ).setText ( expiryDateLabel +
					expiryDateEdit.get ( Calendar.DAY_OF_MONTH ) + "-" + ( expiryDateEdit.get ( Calendar.MONTH ) + 1 ) + "-" + expiryDateEdit.get ( Calendar.YEAR ) );
		else
			// Display not available label
			// Display the expiry date
			( (TextView) findViewById ( R.id.label_date_picker ) ).setText ( expiryDateLabel + AppResources.getInstance ( this ).getString ( this , R.string.not_available_abbreviation ) );
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
    	// Save the content of searchQuery in the outState bundle
    	outState.putString ( SEARCH_QUERY , searchQuery );
    	// Save the content of selectedDivisions in the outState bundle
    	outState.putStringArrayList ( SELECTED_DIVISIONS , selectedDivisionsCodes );

		// Save the content of the orders using GSON
		ActivityInstance.saveDataGson ( this , DirectReturnDetailsActivity.class.getName () , ORDERS , orders );
		// Indicate that there is saved orders data
		outState.putBoolean ( ORDERS , true );
    	
    	// Save the content of requestCode in the outState bundle
    	outState.putInt ( REQUEST_CODE , requestCode );
    	// Save the content of selectedYear in the outState bundle
    	outState.putInt ( SELECTED_YEAR , selectedYear );
    	// Save the content of selectedMonth in the outState bundle
    	outState.putInt ( SELECTED_MONTH , selectedMonth );
    	// Save the content of selectedDay in the outState bundle
    	outState.putInt ( SELECTED_DAY , selectedDay );
    	// Save the content of note in the outState bundle
    	outState.putString ( NOTE , note );
    	// Save the content of currency in the outState bundle
    	outState.putSerializable ( CURRENCY , currency );
    	// Save the content of clientReferenceNumber in the outState bundle
    	outState.putString ( CLIENT_REFERENCE_NUMBER , clientReferenceNumber );    	
    	// Save the content of transactionHeader in the outState bundle
    	outState.putSerializable ( TRANSACTION_HEADER , transactionHeader );
    	// Save the content of transactionDetails in the outState bundle
    	outState.putSerializable ( TRANSACTION_DETAILS , transactionDetails );
    	// Save the content of orderModification in the outState bundle
    	outState.putSerializable ( ORDER_MODIFICATION , orderModification );
    	// Save the content of displayOrderNote in the outState bundle
    	outState.putBoolean ( DISPLAY_ORDER_NOTE , displayOrderNote );
    	// Save the content of displayClientReferenceNumber in the outState bundle
    	outState.putBoolean ( DISPLAY_CLIENT_REFERENCE_NUMBER , displayClientReferenceNumber );
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
		// Determine if the order note is undergoing any modifications
		if ( displayOrderNote ) {
			// Reset flag
			displayOrderNote = false;
			// Retrieve a reference to the secondary view
			View secondary = findViewById ( R.id.layout_order_note );
			// Hide the secondary view
			secondary.startAnimation ( getViewAnimationOut ( secondary ) );
    		// Enable the main list
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( true );
    		// Refresh the action bar
    		invalidateOptionsMenu ();
    		// Exit method
    		return;
		} // End if
		// Determine if the client reference number is undergoing any modifications
		else if ( displayClientReferenceNumber ) {
			// Reset flag
			displayClientReferenceNumber = false;
			// Retrieve a reference to the tertiary view
			View tertiary = findViewById ( R.id.layout_client_reference_number );
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
			// Display the RFR layout
			findViewById ( R.id.layout_rfr ).setVisibility ( View.VISIBLE );
			// Reset reasons selection
			sp_reasons.setSelection ( 0 );
			// Clear expiry date
			expiryDateEdit = null;
			// Display the expiry date
			displayExpiryDate ();
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
				setListAdapter ( getSalesOrderDetailsAdapter ( R.layout.sales_order_details_activity_item , new ArrayList < OrderDirectReturn > () , true , true ) );
				// Set a new adapter based on the saved filter, asynchronously
				new FilterList ( searchQuery ).execute ();
    		} // End else
    	} // End else if
		// Determine if this is a new sales order with selected orders
		// OR a previously saved sales order with modifications
    	else if ( ( getIntent ().getIntExtra ( REQUEST_CODE , -1 ) == REQUEST_CODE_NEW && hasSalesOrders () )
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
								DirectReturnDetailsActivity.this.finish ();
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
			visit = null;
			divisions = null;
			selectedDivisionsCodes = null;
			orderModification = null;
			searchQuery = null;
			currency = null;
			orders = null;
			selectedOrders = null;
			clientReferenceNumber = null;
			note = null;
			sp_reasons = null;
			quickAction = null;
			details = null;
			transactionHeader = null;
			selectedReasonCode = null;
			transactionDetails = null;
			expiryDateEdit = null;
			infoOrderDirectReturn = null;
			selectedItemCode = null;
			selectedView =  null;
		} // End if
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
	 * @param view	Reference to the view to hide after the animation ends.
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
	 * Initializes the sales order note (secondary) view.
	 * 
	 * @param restore	Boolean used to determine if the secondary view is being initialized for the first time or is being restored.
	 */
	protected void initializeSecondaryView ( final boolean restore ) {
		// Retrieve a reference to the parent view
		View parent = findViewById ( R.id.layout_order_note );
		// Retrieve a reference to the sales order note edit text
		EditText noteEditText = (EditText) parent.findViewById ( R.id.edittext_sales_order_note );		
		// Retrieve a reference to the sales order note title label
		TextView titleLabel = (TextView) parent.findViewById ( R.id.label_sales_order_note );
		
		// Display the sales order note title label
		titleLabel.setText ( AppResources.getInstance ( this ).getString ( this , R.string.sales_rfr_note_titles ) );
		// Enable the save icon
		parent.findViewById ( R.id.icon_save_note ).setEnabled ( true );
		// Enable edit text
		noteEditText.setEnabled ( true );
		// Display the field hint
		noteEditText.setHint ( AppResources.getInstance ( this ).getString ( this , R.string.sales_rfr_note_hints ) );
		// Set the maximum number of allowed characters
		noteEditText.setFilters ( new InputFilter [] { new InputFilter.LengthFilter ( TransactionHeadersUtils.getFreeRemarksMaxLength () ) } );
		// Check if the view is being restored
		if ( ! restore )
			// Set the sales order note
			noteEditText.setText ( note );
	}
	
	/**
	 * Initializes the client reference number (tertiary) view.
	 * 
	 * @param restore	Boolean used to determine if the tertiary view is being initialized for the first time or is being restored.
	 */
	@SuppressLint("DefaultLocale") 
	protected void initializeTertiaryView ( final boolean restore ) {
		// Retrieve a reference to the parent view
		View parent = findViewById ( R.id.layout_client_reference_number );
		// Retrieve a reference to the client reference number edit text
		EditText editText = (EditText) parent.findViewById ( R.id.edittext_client_reference_number );		
		// Retrieve a reference to the client reference number title label
		TextView titleLabel = (TextView) parent.findViewById ( R.id.label_client_reference_number );
		
		// Display the client reference number title label
		titleLabel.setText ( AppResources.getInstance ( this ).getString ( this , R.string.client_reference_number ).toUpperCase () );
		// Enable the save icon
		parent.findViewById ( R.id.icon_save_client_reference_number ).setEnabled ( true );
		// Enable edit text
		editText.setEnabled ( true );
		// Display the field hint
		editText.setHint ( AppResources.getInstance ( this ).getString ( this , R.string.client_reference_number_hint ) );
		// Set the maximum number of allowed characters
		editText.setFilters ( new InputFilter [] { new InputFilter.LengthFilter ( TransactionHeadersUtils.getClientReferenceNumberMaxLength () ) } );
		// Check if the view is being restored
		if ( ! restore )
			// Set the client reference number
			editText.setText ( clientReferenceNumber );
	}
	
	/**
	 * Called when a view has been clicked.<br>
	 * The order modifications are saved.
	 * 
	 * @param view	The clicked view.
	 */
	public void updateOrder ( View view ) {
		// Make sure the expiry date is valid
		if ( expiryDateEdit == null ) {
			// Invalid expiry date
			Baguette.showText ( DirectReturnDetailsActivity.this , AppResources.getInstance ( this ).getString ( this , R.string.rfr_invalid_expiry_date_messages ) , Baguette.BackgroundColor.RED );
			// Exit method
			return;
		} // End if
		// Make sure all input variables are valid
		if ( selectedReasonCode != null && selectedView != null && selectedItemCode != null && sp_reasons.getSelectedItemPosition () != 0 ) {
			// Set the reason and expiry date
			orderModification.setReasonOfReturn ( ( (Reasons) sp_reasons.getSelectedItem () ) );
			orderModification.setExpiryDate ( selectedReasonCode , expiryDateEdit.getTime () );
		} // End if
	}
	
	/**
	 * Handler used upon a view selection among the list items of the RFR module.
	 * 
	 * @param view	List item view selected.
	 * @param showDetails	Boolean indicating if the details are displayed (info screen) or not (new / edit screen).
	 */
	public void selectedView ( View view , boolean showDetails , boolean refreshListView ) {
		// Make sure the info screen is displayed in a NON VIEW mode
		if ( requestCode == REQUEST_CODE_INFO  && !getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) )
			// Display quick action over selected view
			quickAction.show ( view , orderModification , getResources () );
		// Check if a new view is selected
		else if ( ! ( (ViewHolder) view.getTag () ).order.getItem ().getItemCode ().equals ( selectedItemCode ) ) {
			// Reset RFR input variables
			selectedReasonCode = null;
			sp_reasons.setSelection ( 0 );
			// Determine if the details are not displayed (new / edit screen)
			if ( ! showDetails ) {
				// Set the RFR input variables
				selectedItemCode = ( (ViewHolder) view.getTag () ).order.getItem ().getItemCode ();
				orderModification = ( (ViewHolder) view.getTag () ).order;
				selectedView = view;
				// Determine if it has a valid expiry date
				if ( ( (ViewHolder) selectedView.getTag () ).order.getExpiryDate ( selectedReasonCode ) != null ) {
					// Set the expiry date
					expiryDateEdit = Calendar.getInstance ();
					expiryDateEdit.setTime( ( (ViewHolder) selectedView.getTag () ).order.getExpiryDate ( selectedReasonCode ) );
				} // End if
				else
					// Reset the expiry date
					expiryDateEdit = null;
				// Display the expiry date
				displayExpiryDate ();
				// Check if the list should be refreshed
				if ( refreshListView )
					// Refresh list
					getListView ().invalidateViews ();
			} // End if
		} // End if
	}
	
	/**
	 * Called when a view has been clicked.<br>
	 * The order note is saved.
	 * 
	 * @param view	The clicked view.
	 */
	public void updateNote ( View view ) {
		// Determine if the order note is undergoing any modifications
		if ( ! displayOrderNote )
			// No modifications
			return;
		
		// Reset flag
		displayOrderNote = false;
		
		// Retrieve a reference to the secondary view
		View secondary = findViewById ( R.id.layout_order_note );
		// Retrieve a reference to the sales order note edit text
		EditText noteEditText = (EditText) secondary.findViewById ( R.id.edittext_sales_order_note );
		// Store the sales order note
		note = noteEditText.getText ().toString ().trim ();
		// Disable the edit text
		noteEditText.setEnabled ( false );
		
		// Disable the save icon
		secondary.findViewById ( R.id.icon_save_note ).setEnabled ( false );
		// Hide the software keyboard
        ( (InputMethodManager) getBaseContext ().getSystemService ( INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow ( secondary.getWindowToken () , 0 );
        
		// Hide the secondary view
        secondary.startAnimation ( getViewAnimationOut ( secondary ) );
		// Enable the main list
		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( true );
		// Indicate that the save was successful
		Vibration.vibrate ( this );
		// Refresh the action bar
		invalidateOptionsMenu ();
		
		// Update the sales order details list with the new note
		onNoteModificationResult ();
	}
	
	/**
	 * Called when a view has been clicked.<br>
	 * The order note is saved.
	 * 
	 * @param view	The clicked view.
	 */
	public void updateClientReferenceNumber ( View view ) {
		// Determine if the client reference number is undergoing any modifications
		if ( ! displayClientReferenceNumber )
			// No modifications
			return;
		
		// Retrieve a reference to the tertiary view
		View tertiary = findViewById ( R.id.layout_client_reference_number );
		// Retrieve a reference to the client reference number edit text
		EditText editText = (EditText) tertiary.findViewById ( R.id.edittext_client_reference_number );
		// Check if the client reference number is valid
		if ( editText.getText ().toString ().trim ().isEmpty () ) {
			// Indicate that some clients were removed
			Baguette.showText ( DirectReturnDetailsActivity.this ,
					AppResources.getInstance ( DirectReturnDetailsActivity.this ).getString ( DirectReturnDetailsActivity.this , R.string.invalid_client_reference_number_message) ,
					Baguette.BackgroundColor.RED );
			// Exit method
			return;
		} // End if
		
		// Search for any previous uses of the provided client reference number
		long count = DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionHeadersDao ().queryBuilder ()
				.where ( TransactionHeadersDao.Properties.ClientReferenceNumber.isNotNull () ,
						TransactionHeadersDao.Properties.ClientReferenceNumber.eq ( editText.getText ().toString ().trim () ) ,
						TransactionHeadersDao.Properties.TransactionCode.notEq ( transactionHeader == null ? "" : transactionHeader.getTransactionCode () ) ).count ();
		// Check if it is previously used
		if ( count > 0 ) {
			// Indicate that some clients were removed
			Baguette.showText ( DirectReturnDetailsActivity.this ,
					AppResources.getInstance ( DirectReturnDetailsActivity.this ).getString ( DirectReturnDetailsActivity.this , R.string.used_client_reference_number_message) ,
					Baguette.BackgroundColor.RED );
			// Exit method
			return;
		} // End if
			
		// Reset flag
		displayClientReferenceNumber = false;
		// Store the client reference number
		clientReferenceNumber = editText.getText ().toString ().trim ();
		// Disable the edit text
		editText.setEnabled ( false );
		
		// Disable the save icon
		tertiary.findViewById ( R.id.icon_save_client_reference_number ).setEnabled ( false );
		// Hide the software keyboard
        ( (InputMethodManager) getBaseContext ().getSystemService ( INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow ( tertiary.getWindowToken () , 0 );
        
		// Hide the tertiary view
        tertiary.startAnimation ( getViewAnimationOut ( tertiary ) );
		// Enable the main list
		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( true );
		// Indicate that the save was successful
		Vibration.vibrate ( this );
		// Refresh the action bar
		invalidateOptionsMenu ();
		
		// Update the sales RFR details list with the new client reference number
		onClientReferenceNumberModificationResult ();
		
		// Save transaction
		saveTransaction ();
	}
	
	/*
	 * Called when an activity you launched exits, giving you the requestCode you started it with, the resultCode it returned, and any additional data from it.
	 *
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	protected void onActivityResult ( int requestCode , int resultCode , Intent data ) {
    	// Check if the transaction has been printed in a non view mode
    	if ( requestCode == REQUEST_CODE_PRINT && ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) ) {
	    	// Call this to set the result that your activity will return to its caller
			Intent intent = new Intent();
			intent.putExtra ( DirectReturnActivity.SAVE_SUCCESS , ! error );
	    	setResult ( RESULT_OK , intent );
    		// Finish activity
    		finish ();
    	} // End if
    	
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
    	if ( scanResult != null) {
    		// Update the new search query
    		searchQuery = scanResult.getContents () == null ? "" : scanResult.getContents ();
    		// Filter the list asynchronously
    		new FilterList ( searchQuery ).execute ();
    	} // End if
	}
	
	/**
	 * Called after a successful note modification.<br>
	 * The note in the sales RFR details list is updated.
	 */
	private void onNoteModificationResult () {
		// Iterate over all details
		for ( OrderDirectReturnInfo detail : details )
			// Check if the current detail is about the sales order note
			if ( detail.getId () == OrderDirectReturnInfo.ID.NOTE ) {
				// Update the sales order note
				detail.setValue ( note );
				// Exit loop
				break;
			} // End if
		// Refresh the list
		( (MultipleListAdapter) getListAdapter () ).notifyDataSetChanged ();
	}
	
	/**
	 * Called after a successful client reference number modification.<br>
	 * The client reference number in the sales RFR details list is updated.
	 */
	private void onClientReferenceNumberModificationResult () {
		// Iterate over all details
		for ( OrderDirectReturnInfo detail : details )
			// Check if the current detail is about the client reference number
			if ( detail.getId () == OrderDirectReturnInfo.ID.CLIENT_REFERENCE_NUMBER ) {
				// Update the client reference number
				detail.setValue ( clientReferenceNumber );
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
    	// Check if the list of orders is valid
    	// OR if a note is undergoing modifications
    	// OR if a client reference number is undergoing modifications
    	if ( orders == null || displayOrderNote || displayClientReferenceNumber )
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
			// Determine if item barcodes are allowed
			if ( PermissionsUtils.getItemBarcodes ( this , DatabaseUtils.getCurrentUserCode ( this ) , DatabaseUtils.getCurrentCompanyCode ( this ) ) )
				me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_barcode ) );
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
    				|| ( transactionHeader.getIsProcessed () != IsProcessedUtils.isSync () && transactionHeader.getTransactionStatus () != StatusUtils.isDeleted () ) ) {
            	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_save ) );
            	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_calendar ) );
            	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_keyboard ) );
    			me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_remove ) );
    		} // End if
    	} // End else
    	else
    		// Order can be printed in view mode only
    		me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_print ) );
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
    	// Check if the orders list is valid
    	if ( orders == null )
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
    	// Determine if the action barcode is selected
    	else if ( menuItem.getItemId () == R.id.action_barcode ) {
    		// Declare and initialize a barcode intent integrator
    		IntentIntegrator integrator = new IntentIntegrator ( this );
    		// Initiate a barcode scan
    		integrator.initiateScan();
    		// Consume event
    		return true;
    	} // End else if
    	// Determine if the action print is selected
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
								Intent intent = new Intent ( DirectReturnDetailsActivity.this, PrintingActivity.class );
								intent.putExtra ( PrintingActivity.TYPE , PrintingActivity.Type.DirectReturn );
								intent.putExtra ( PrintingActivity.PRINTOUT , PrintingActivity.Printout.COPY );
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

    		// Hide the software keyboard
            ( (InputMethodManager) getBaseContext ().getSystemService ( INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow ( findViewById ( R.id.view_clear_focus ).getWindowToken (), 0 );

    		// Declare and initialize a list used to host the valid sales orders
    		ArrayList < OrderDirectReturn > salesOrder = new ArrayList < OrderDirectReturn > ();
    
    		// Iterate over all orders
    		for ( OrderDirectReturn order : orders )
    			// Check if the current order has valid quantities
    			if ( order.getQuantityBigSummation () != 0 || order.getQuantityMediumSummation () != 0 || order.getQuantitySmallSummation () != 0 )
    				// The order contains at least one quantity, add it to the sales order list
    				salesOrder.add ( order );
    		// Check if there is at least one valid sales order
    		if ( salesOrder.isEmpty () ) {
				// Indicate that some clients were removed
				Baguette.showText ( DirectReturnDetailsActivity.this ,
						AppResources.getInstance ( DirectReturnDetailsActivity.this ).getString ( DirectReturnDetailsActivity.this , R.string.no_sales_return_messages ) ,
						Baguette.BackgroundColor.RED );
        		// Consume event
        		return true;
    		} // End if
		    if ( selectedView != null )
		    	selectedView.setBackgroundColor ( Color.WHITE );
		    selectedItemCode = null;
    		// Otherwise there is at least one sales order
    		// Set the new request code
    		requestCode = REQUEST_CODE_INFO;
    		// Refresh the action bar
    		invalidateOptionsMenu ();
    		// Set the selected orders list
    		selectedOrders = salesOrder;
			// Initialize the sales order details
			initializeDetails ();
			findViewById ( R.id.layout_rfr ).setVisibility(View.GONE);
			// Declare and initialize a multiple list adapter
			MultipleListAdapter adapter = new MultipleListAdapter ( this );
			// Add the sales order details adapter
			adapter.addSection ( new Section ( AppResources.getInstance ( this ).getString ( this , R.string.sales_rfr_details_labels ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
					new OrderDirectReturnInfoAdapter ( this , R.layout.order_info_item , details ) );
			Users user = DatabaseUtils.getInstance ( this ).getDaoSession ().getUsersDao ().queryBuilder ()
					.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( this ) ) ,
							UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( this ) ) ).unique ();
			// Check if the logged in user is a cash van
			if ( user.getUserType () == 11 ) 
			 
			if(ClientCard.isCreditClient(( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClient()))
				adapter.addSection ( new Section ( AppResources.getInstance ( this ).getString ( this , R.string.sales_order_details_label_cash ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
						new CashOrderRFRAdapter( this , R.layout.cash_order ,list1 ,! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false )) );
			
			
			infoOrderDirectReturn = new ArrayList < OrderDirectReturn > ();
			for ( OrderDirectReturn order : selectedOrders ) {
				for ( int i = 0 ;i < order.getReasonOfReturn ().size () ; i ++ ) {
					OrderDirectReturn io = new OrderDirectReturn ( order.getItem (), order.getUnit () );
					io.setQuantityBig ( order.getReasonOfReturn ().get ( i ).getReasonCode () , order.getQuantityBig ( order.getReasonOfReturn ().get( i ).getReasonCode () ) );
					io.setQuantityMedium ( order.getReasonOfReturn ().get ( i ).getReasonCode () , order.getQuantityMedium ( order.getReasonOfReturn ().get( i ).getReasonCode () ) );
					io.setQuantitySmall ( order.getReasonOfReturn ().get ( i ).getReasonCode () , order.getQuantitySmall ( order.getReasonOfReturn ().get ( i ).getReasonCode () ) );
					io.setExpiryDate ( order.getReasonOfReturn ().get ( i ).getReasonCode () , order.getExpiryDate ( order.getReasonOfReturn ().get ( i ).getReasonCode () ) );
					io.setBarCodes ( order.getBarCodes () );
					io.setDivisionCodes ( order.getDivisionCodes () );
					io.setPriceBig ( order.getPriceBig () );
					io.setPriceMedium ( order.getPriceMedium () );
					io.setPriceSmall ( order.getPriceSmall () );
					io.setTax ( order.getTax () );
					io.setReasonOfReturn ( order.getReasonOfReturn ().get ( i ) );
					if ( io.getBasicQuantity () != 0 )
						infoOrderDirectReturn.add ( io );
				} // End for loop
			} // End for each
			// Add the sales order adapter
			adapter.addSection ( new Section ( AppResources.getInstance ( this ).getString ( this , R.string.sales_rfr_products_labels ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
					getSalesOrderDetailsAdapter ( R.layout.sales_order_details_activity_item , infoOrderDirectReturn , ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) , false ) );
			// Set the list adapter
			setListAdapter ( adapter );
			
    		// Consume event
    		return true;
    	} // End else if
    	// Determine if the action keyboard is selected
    	else if ( menuItem.getItemId () == R.id.action_keyboard ) {
    		// Set flag
    		displayOrderNote = true;
    		// Initialize the secondary view
    		initializeSecondaryView ( false );
    		// Disable the main list
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( false );
    		// Retrieve a reference to the secondary view
    		View secondaryView = findViewById ( R.id.layout_order_note );
    		// Display the secondary view
    		secondaryView.setVisibility ( View.VISIBLE );
    		// Animate the secondary view
    		secondaryView.startAnimation ( getViewAnimationIn () );
    		// Refresh the action bar
    		invalidateOptionsMenu ();
    		// Consume event
    		return true;
    	} // End else if
    	// Determine if the action calendar is selected
    	else if ( menuItem.getItemId () == R.id.action_calendar ) {
    		// Display a date picker dialog
    		AppDialog.getInstance ().displayDatePicker ( this , selectedYear , selectedMonth , selectedDay , Calendar.getInstance ().getTimeInMillis () , false , true ,
    				new DatePickerDialog.OnDateSetListener () {
						@Override
						public void onDateSet ( DatePicker view , int year , int monthOfYear , int dayOfMonth ) {
							// Update the delivery day
							selectedYear = year;
							selectedMonth = monthOfYear;
							selectedDay = dayOfMonth;
							// Declare and initialize a two digits format
							DecimalFormat twoDigits = new DecimalFormat ( "00" );
							// Iterate over all details
							for ( OrderDirectReturnInfo detail : details )
								// Check if the current detail is about the delivery date
								if ( detail.getId () == OrderDirectReturnInfo.ID.DELIVERY_DATE ) {
									// Update the delivery date
									detail.setValue ( twoDigits.format ( selectedDay ) + "-" + twoDigits.format ( selectedMonth + 1 ) + "-" + selectedYear );
									// Exit loop
									break;
								} // End if
							// Refresh the list
							( (MultipleListAdapter) getListAdapter () ).notifyDataSetChanged ();
						}
					} , null );
    		// Consume event
    		return true;
    	} // End else if
    	// Determine if the action remove is selected
    	else if ( menuItem.getItemId () == R.id.action_remove ) {
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
					    		// Check if there is a previously saved transaction
					    		if ( transactionHeader != null ) {
					    			// Modify the transaction header accordingly
					    			transactionHeader.setTransactionStatus ( StatusUtils.isDeleted () );
					    			// Update the transaction header object in DB
					    			DatabaseUtils.getInstance ( DirectReturnDetailsActivity.this ).getDaoSession ().getTransactionHeadersDao ().update ( transactionHeader );
					            	// Call this to set the result that your activity will return to its caller
					            	setResult ( RESULT_OK , new Intent ().putExtra ( DirectReturnActivity.DELETE_SUCCESS , true ) );
					    		} // End if
					        	// Finish this activity
					        	finish ();
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
    	// Determine if the action save is selected
    	else if ( menuItem.getItemId () == R.id.action_save ) {
    		// Check if there is a previously stored sales order
    		if ( transactionHeader != null && transactionDetails != null )
	    		// Check if there are modifications
	    		if ( ! hasModifications () ) {
	    			// Indicate that there are no new modifications
					Baguette.showText ( this ,
							AppResources.getInstance ( this ).getString ( this , R.string.no_new_modifications_message ) ,
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
								// Check for the RFR limit
								if ( checkRFRLimit () )
									// Save the transaction(s)
									saveTransaction ();
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
    	}
    	// Allow normal menu processing to proceed
    	return false;
    }
    
    /**
     * Checks for the RFR limits in order to properly save the RFR.
     * 
     * @return	Flag used to indicate if the RFR can be saved or not.
     */
    private boolean checkRFRLimit () {
		// Retrieve the user object
		Users user = DatabaseUtils.getInstance ( this ).getDaoSession ().getUsersDao ().queryBuilder ()
				.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( this ) ) ,
						UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( this ) ) ).unique ();
		// Check if the logged in user is a cash van
		if ( user.getUserType () == 11 ) 
			// No RFR limit applied
			return true;
    	// Retrieve the user and company codes
    	String userCode = DatabaseUtils.getCurrentUserCode ( this );
    	String companyCode = DatabaseUtils.getCurrentCompanyCode ( this );
    	// Retrieve the limit amount
    	int limit = PermissionsUtils.getRFRLimitAmount ( this , userCode , companyCode ) + PermissionsUtils.getRFRLimitMargin ( this , userCode , companyCode );
    	// Check if the limit is valid
    	if ( limit <= 0 )
    		// Invalid limit and hence it is not applied
    		// Indicate that the RFR is checked
    		return true;
		// Compute the sales RFR details
		double totalValue = 0;

		// Iterate over all the selected orders 
		for ( OrderDirectReturn order : selectedOrders ) {
			// Compute the current net value
			double _calValue = order.getQuantityBigSummation () * order.getPriceBig () + order.getQuantityMediumSummation () * order.getPriceMedium ()
					+ order.getQuantitySmallSummation () * order.getPriceSmall ();
			// Compute the current tax value
			double _taxes = 0;
			// Check if the item is taxable
			if ( order.getTax () != 0 )
				// Compute the current taxes
				_taxes = _calValue * order.getTax () / 100;
			// Compute the current total value
			double _totalValue = _calValue + _taxes;
			// Update all the values
			totalValue += _totalValue;
		} // End for each
		
		// Check if the total amount is beneath the limit
		if ( totalValue <= limit ) {
    		// Set flag
    		displayClientReferenceNumber = true;
    		// Initialize the tertiary view
    		initializeTertiaryView ( false );
    		// Disable the main list
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( false );
    		// Retrieve a reference to the tertiary view
    		View tertiaryView = findViewById ( R.id.layout_client_reference_number );
    		// Display the tertiary view
    		tertiaryView.setVisibility ( View.VISIBLE );
    		// Animate the tertiary view
    		tertiaryView.startAnimation ( getViewAnimationIn () );
    		// Refresh the action bar
    		invalidateOptionsMenu ();
    		// Indicate that the RFR is not checked and cannot be saved
    		return false;
		} // End if
		// Otherwise the RFR is checked and can be saved
		return true;
    }
    
    /**
     * Saves the current transaction.
     */
    private void saveTransaction () {
		// Otherwise the sales order can be saved
		// Flag used to indicate whether an error occurred or not
		error = false;
		// Flag used to indicate if the activity can be finished or not
		// This flag is mainly used to display a dialog before the activity finishes
		boolean finish = true;
		
		// Retrieve the user code
		String userCode = DatabaseUtils.getCurrentUserCode ( this );
		// Retrieve the division code
		String divisionCode = ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClientDivision ().getDivisionCode ();
		// Retrieve the company code
		String companyCode = DatabaseUtils.getCurrentCompanyCode ( this );
    	// Retrieve the limit amount
    	int limit = PermissionsUtils.getRFRLimitAmount ( this , userCode , companyCode ) + PermissionsUtils.getRFRLimitMargin ( this , userCode , companyCode );
		// Declare and initialize a string used to host the transaction code used
		String transactionHeaderCode = null;
		
		
		 
		divisionCode = DatabaseUtils.getCurrentDivisionCode ( this );
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
		
	 
		
		
		// Compute the sales order details
		double calValue = 0;
		double netValue = 0;
		double taxes = 0;
		double totalValue = 0;

		// Iterate over all the selected orders 
		for ( OrderDirectReturn order : selectedOrders ) {
			netValue += order.getQuantityBigSummation () * order.getPriceBig () + order.getQuantityMediumSummation () * order.getPriceMedium ()
					+ order.getQuantitySmallSummation () * order.getPriceSmall ();
			// Compute the current net value
			double _calValue = order.getQuantityBigSummation() * order.getPriceBig () + order.getQuantityMediumSummation () * order.getPriceMedium ()
					+ order.getQuantitySmallSummation () * order.getPriceSmall ();
			// Compute the current tax value
			double _taxes = 0;
			// Check if the item is taxable
			if ( order.getTax () != 0 )
				// Compute the current taxes
				_taxes = _calValue * order.getTax () / 100;
			// Compute the current total value
			double _totalValue = _calValue + _taxes;
			// Update all the values
			calValue += _calValue;
			taxes += _taxes;
			totalValue += _totalValue;
		} // End for each
    	// Compute current time
    	Calendar now = Calendar.getInstance ();
		// Compute delivery date
		Calendar deliveryDate = Calendar.getInstance ();
		deliveryDate.set ( selectedYear , selectedMonth , selectedDay );
		// Declare and initialize a sequence formatter
		DecimalFormat sequence = new DecimalFormat ( "000000" );
		// Retrieve the user object
		Users user = DatabaseUtils.getInstance ( this ).getDaoSession ().getUsersDao ().queryBuilder ()
				.where ( UsersDao.Properties.UserCode.eq ( visit.getUserCode () ) ,
						UsersDao.Properties.CompanyCode.eq ( visit.getCompanyCode () ) ).unique ();
		
		// Determine if there is a previously stored sales order
		if ( transactionHeader == null && transactionDetails == null ) {
    		try {
    			if ( user.getUserType () == 11 ) 
        			if(ClientCard.isCreditClient(( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClient()))
        				
        				if( list1.get(0).getValue().equals("1") && cash == "" )
        					{
        					cash="1";
        				 
        					}
    			// Declare and initialize vehicle stock lists used to add and update 
    			List < VehiclesStock > addStock = new ArrayList < VehiclesStock > ();
    			List < VehiclesStock > updateStock = new ArrayList < VehiclesStock > ();
				// Retrieve the sales invoice sequence
				int salesRFRSequence = DatabaseUtils.getUserSequence ( this , user.getUserCode () , companyCode , TransactionSequencesDao.Properties.SalesReturn );
				// Begin transaction
				DatabaseUtils.getInstance ( DirectReturnDetailsActivity.this ).getDaoSession ().getDatabase ().beginTransaction ();
	    		// Compute the transaction header code
	    		transactionHeaderCode = String.valueOf ( TransactionHeadersUtils.Type.SALES_RETURN ) + String.valueOf ( user.getPrefixID () ) + sequence.format ( salesRFRSequence );
				// Update the sales rfr sequence
				DatabaseUtils.setUserSequence ( this , user.getUserCode () , companyCode , TransactionSequencesDao.Properties.SalesReturn , salesRFRSequence + 1 );
	    		// TODO : Details of a sales order transaction header
	    		// Declare and initialize a transaction header
	    		TransactionHeaders transactionHeader = new TransactionHeaders  ( null , // ID
	    				transactionHeaderCode , // TransactionCode
	    				TransactionHeadersUtils.Type.SALES_RETURN , // TransactionType
	    				divisionCode , // DivisionCode
	    				companyCode , // CompanyCode
	    				currency.getCurrencyCode () , // CurrencyCode
	    				visit.getClientCode ()  , // ClientCode
	    				visit.getUserCode () , // UserCode
	    				visit.getVisitID () , // VisitID
	    				visit.getJourneyCode () , // JourneyCode
	    				Calendar.getInstance ().getTime () , // IssueDate
	    				deliveryDate.getTime () , // DeliveryDate
	    				netValue , // GrossAmount
	    				netValue - calValue , // DiscountAmount
	    				calValue , // NetAmount
	    				taxes , // TaxAmount
	    				totalValue , // TotalTaxAmount
	    				0.0 , // RemainingAmount
	    				StatusUtils.isAvailable () , // TransactionStatus
	    				cash , // Info1
	    				null , // Info2
	    				null , // Info3
	    				null , // Info4
	    				null , // Info5
	    				null , // ApprovedRequestReturnReference
	    				null , // TransactionPasswordCode
	    				note , // Notes
	    				limit > 0 && totalValue <= limit ? clientReferenceNumber : null, // ClientReferenceNumber
	    				null , // PrintingTimes
	    				2 , // DeviceType
	    				IsProcessedUtils.isNotSync () , // IsProcessed
	    				now.getTime () ); // StampDate
	    		// Insert the transaction header in DB
	    		DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionHeadersDao ().insert ( transactionHeader );
	    		// Retrieve the orders list of the current division
	    		ArrayList < OrderDirectReturn > divisionOrders = selectedOrders;
	    		// Iterate over all selected orders of the current division
	    		int counter  = 0 ;
				for ( int i = 0 ; i < divisionOrders.size () ; i ++ ) {
					// TODO : Details of a sales order transaction detail
					for ( Reasons reason : divisionOrders.get ( i ).getReasonOfReturn () ) {
						// Make sure there is a valid returned quantity of the current item for the current reason
						if ( divisionOrders.get ( i ).getQuantitySummation ( reason.getReasonCode () ) == 0 )
							// Skip the current item
							continue;
			    		// Declare and initialize a transaction detail
						TransactionDetails transactionDetail = new TransactionDetails ( null , // ID
								transactionHeader.getTransactionCode () , // TransactionCode
								counter++ , // LineID
								divisionOrders.get ( i ).getItem ().getItemCode () , // ItemCode
								TransactionDetailsUtils.Type.ORDER , // OrderedType
								(double) divisionOrders.get ( i ).getQuantityBig ( reason.getReasonCode () ) , // QuantityBig
								(double) divisionOrders.get ( i ).getQuantityMedium ( reason.getReasonCode () ) , // QuantityMedium
								(double) divisionOrders.get ( i ).getQuantitySmall ( reason.getReasonCode () ) , // QuantitySmall
								(double) divisionOrders.get ( i ).getQuantityBig ( reason.getReasonCode () ) * divisionOrders.get ( i ).getItem ().getUnitBigMedium () * divisionOrders.get ( i ).getItem ().getUnitMediumSmall ()
									+ divisionOrders.get ( i ).getQuantityMedium ( reason.getReasonCode () ) * divisionOrders.get ( i ).getItem ().getUnitMediumSmall ()
									+ divisionOrders.get ( i ).getQuantitySmall ( reason.getReasonCode () )  , // BasicUnitQuantity
								0.0 , // ApprovedQuantityBig
								0.0 , // ApprovedQuantityMedium
								0.0 , // ApprovedQuantitySmall
								0.0 , // ApprovedBasicUnitQuantity
								0.0 , // MissedQuantityBig
								0.0 , // MissedQuantityMedium
								0.0 , // MissedQuantitySmall
								0.0 , // MissedBasicUnitQuantity
								(double) divisionOrders.get ( i ).getPriceBig () , // PriceBig
								(double) divisionOrders.get ( i ).getPriceMedium () , // PriceMedium
								(double) divisionOrders.get ( i ).getPriceSmall () , // PriceSmall
								(double) divisionOrders.get ( i ).getPriceBig () , // UserPriceBig
								(double) divisionOrders.get ( i ).getPriceMedium () , // UserPriceMedium
								(double) divisionOrders.get ( i ).getPriceSmall () , // UserPriceSmall
								0.0 , // DiscountPercentage
								0.0 , // DiscountAmount
								( divisionOrders.get ( i ).getQuantityBig ( reason.getReasonCode () ) * divisionOrders.get ( i ).getPriceBig () + divisionOrders.get ( i ).getQuantityMedium ( reason.getReasonCode () ) 
												* divisionOrders.get ( i ).getPriceMedium () + divisionOrders.get ( i ).getQuantitySmall ( reason.getReasonCode () ) * divisionOrders.get ( i ).getPriceSmall () )
												* ( 100 + divisionOrders.get ( i ).getTax () ) / 100, // TotalLineAmount
								null , // LineNote
								null , // ItemLot
								reason.getReasonCode () , // ReasonCode
								divisionOrders.get ( i ).getTax () , // ItemTaxPercentage
								divisionOrders.get ( i ).getItem ().getItemName () , // ItemName
								divisionOrders.get ( i ).getItem ().getItemAltName () , // ItemAltName
								 -1  , // ParentLineID
								 divisionOrders.get( i ).getExpiryDate ( reason.getReasonCode () ) , // ItemExpiryDate
	    						null , // ItemAffectedStock
	    						null , // IsInvoiceRelated
	    						null , // IsCompanyRelated
	    						null , 
								null , 
								null , 
								null , 
								null , 
								now.getTime ()  ); // StampDate
						
						// Update vehicle stock
						VehiclesStock stock = DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesStockDao ().queryBuilder ()
								.where ( VehiclesStockDao.Properties.ItemCode.eq ( transactionDetail.getItemCode () ) ).limit ( 1 ).unique ();
						if ( stock == null ) {
							stock = new VehiclesStock ( null , vehicle.getVehicleCode () , transactionDetail.getItemCode () , 0.0 , 0.0 , divisionCode , user.getCompanyCode () , null , now.getTime () );
							addStock.add ( stock );
						} // End if
						else
							updateStock.add ( stock );
					
						if ( ReasonsUtils.isDamages ( reason.getReasonAffectStock() ) )
							stock.setDamageQuantity ( stock.getDamageQuantity () + transactionDetail.getBasicUnitQuantity () );
					    else
							stock.setGoodQuantity ( stock.getGoodQuantity () + transactionDetail.getBasicUnitQuantity () );

						// Insert the transaction detail in DB
						DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsDao ().insert ( transactionDetail ); // StampDate
					} // End for each
					for ( VehiclesStock stock : addStock )
						// Insert the new stock into DB
						DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesStockDao ().insert ( stock );
					// Iterate over modified stock
					for ( VehiclesStock stock : updateStock )
						// Update the stock in DB
						DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesStockDao ().update ( stock );


					// Indicate that a sales order was successfully performed during this visit
					CallAction.setCallActionStatus ( this , visit.getVisitID () , CallAction.ID.SALES_RFR , true );
				} // End for loop
				
				// Commit changes
				DatabaseUtils.getInstance ( DirectReturnDetailsActivity.this ).getDaoSession ().getDatabase ().setTransactionSuccessful ();
				// Indicate that the save was successful
				Vibration.vibrate ( DirectReturnDetailsActivity.this );
    		} catch ( Exception exception ) {
				// Indicate that an error occurred
				error = true;
    		} finally {
				// End transaction
				DatabaseUtils.getInstance ( DirectReturnDetailsActivity.this ).getDaoSession ().getDatabase ().endTransaction ();
    		} // End try-catch-finally block
		} // End if
		else if ( transactionHeader != null && transactionDetails != null ) {
			// Before saving, refresh the objects to make sure the sales order is not already sent to the server ( via auto sync )
			DatabaseUtils.getInstance ( DirectReturnDetailsActivity.this ).getDaoSession ().getTransactionHeadersDao ().refresh ( transactionHeader );
			// Check that the transaction header is not processed to the server
			if ( transactionHeader.getIsProcessed () == IsProcessedUtils.isSync () ) {
    			// Indicate that the sales order cannot be modified anymore
				Baguette.showText ( this ,
						AppResources.getInstance ( this ).getString ( this , R.string.sales_order_already_processed_message ) ,
						Baguette.BackgroundColor.RED );
	    		// Consume event
	    		return ;
			} // End if
			
    		try {
    			if ( user.getUserType () == 11 ) 
        			if(ClientCard.isCreditClient(( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClient()))
        				
        				if( list1.get(0).getValue().equals("1") && cash == "" )
        					{
        					cash="1";
        				 
        					}
        				else
        						cash="";
				// Begin transaction
				DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().beginTransaction ();
				// Retrieve a reference to the transaction code
				transactionHeaderCode = transactionHeader.getTransactionCode ();
				// Update the transaction header
				transactionHeader.setTaxAmount ( taxes );
				transactionHeader.setGrossAmount ( netValue );
				transactionHeader.setNetAmount ( calValue );
				transactionHeader.setDiscountAmount ( netValue - calValue );
				transactionHeader.setTotalTaxAmount ( totalValue );
				transactionHeader.setDeliveryDate ( deliveryDate.getTime () );
				transactionHeader.setNotes ( note );
				transactionHeader.setInfo1( cash );
				transactionHeader.setClientReferenceNumber ( limit > 0 && totalValue <= limit ? clientReferenceNumber : null );
				transactionHeader.setStampDate ( now.getTime () );
				// Compute the maximum transaction detail line id
				int maxId = -1;
				// Map the transaction details to their item codes
				Map < String , ArrayList < TransactionDetails >  > _transactionDetails = new HashMap < String , ArrayList < TransactionDetails > > ();
				// Iterate over all transaction details
				for ( TransactionDetails transactionDetail : transactionDetails ) {
					// Map the current transaction detail to its item code
					if ( _transactionDetails.containsKey ( transactionDetail.getItemCode () ) )
						// Map the current transaction detail to its item code
						_transactionDetails.get ( transactionDetail.getItemCode () ).add( transactionDetail );
					else { 
						ArrayList< TransactionDetails > tempDetails =  new ArrayList < TransactionDetails > ();
						tempDetails.add ( transactionDetail );
						_transactionDetails.put ( transactionDetail.getItemCode () , tempDetails );
					} // End if
					// Compute the max line id
					if ( transactionDetail.getLineID () > maxId )
						// Set the new line id
						maxId = transactionDetail.getLineID ();
				} // End for each
				// Declare and initialize transaction details used to add, update and remove the transaction details
				List < TransactionDetails > add = new ArrayList < TransactionDetails > ();
				List < TransactionDetails > update = new ArrayList < TransactionDetails > ();
		 



 
				// Iterate over all selected orders
				for ( OrderDirectReturn order : selectedOrders )
					// Determine the corresponding transaction detail
					if ( _transactionDetails.containsKey ( order.getItem ().getItemCode () ) ) {
						ArrayList < TransactionDetails >  transactionDetail = _transactionDetails.get ( order.getItem ().getItemCode () );
						for ( String reasonCode : order.getQuantitySmall ().keySet () ) {
							// Make sure there is a valid returned quantity of the current item for the current reason
							if ( order.getQuantitySummation ( reasonCode ) == 0 )
								// Skip the current item
								continue;
							boolean flag = false;
							for ( TransactionDetails transDet : transactionDetail ) {
								if ( reasonCode.equals ( transDet.getReasonCode () ) ) {
									flag = true;
									// Retrieve a reference to the transaction detail
									// Add the transaction to the list of updated transaction details
									update.add ( transDet );
									/*
									 * Update the transaction detail with the following fields :
									 * - Quantity big
									 * - Quantity medium
									 * - Quantity small
									 * - Basic Unit Quantity
									 * - Price Big
									 * - Price Medium
									 * - Price Small
									 * - User Price Big
									 * - User Price Medium
									 * - User Price Small
									 * - Total Line Amount
									 * - Tax Percentage
									 * - Expiry date
									 * - Details Note (Total Line amount)
									 * - Quantity BU
									 * - Stamp Date
									 */
									transDet.setQuantityBig ( (double) order.getQuantityBig (  transDet.getReasonCode()) );
									transDet.setQuantityMedium ( (double) order.getQuantityMedium (  transDet.getReasonCode()));
									transDet.setQuantitySmall ( (double) order.getQuantitySmall (  transDet.getReasonCode()) );
									transDet.setBasicUnitQuantity ( (double) order.getQuantityBig (  transDet.getReasonCode()) * order.getItem ().getUnitBigMedium () * order.getItem ().getUnitMediumSmall ()
											+ order.getQuantityMedium ( transDet.getReasonCode()) * order.getItem ().getUnitMediumSmall ()
											+ order.getQuantitySmall (  transDet.getReasonCode()) );
									transDet.setPriceBig ( order.getPriceBig () );
									transDet.setPriceMedium ( order.getPriceMedium () );
									transDet.setPriceSmall ( order.getPriceSmall () );
									transDet.setUserPriceBig ( order.getPriceBig () );
									transDet.setUserPriceMedium ( order.getPriceMedium () );
									transDet.setUserPriceSmall ( order.getPriceSmall () );
									transDet.setTotalLineAmount(order.getQuantityBig(  transDet.getReasonCode()) * order.getPriceBig() + order.getQuantityMedium( transDet.getReasonCode())
																* order.getPriceMedium() + order.getQuantitySmall( transDet.getReasonCode()) * order.getPriceSmall());
									transDet.setItemTaxPercentage ( order.getTax () );
									transDet.setItemExpiryDate ( order.getExpiryDate ( reasonCode ) );
									transDet.setDiscountAmount(0.0);
									transDet.setDiscountPercentage(0.0);
									transDet.setStampDate ( now.getTime () );
									break;
								}
							}// end for each 
							if ( ! flag ) {
								add.add ( new TransactionDetails ( null , // Id
										transactionHeader.getTransactionCode(), // TransactionCode
										++maxId , // LineID
										order.getItem ().getItemCode(), // ItemCode
										TransactionDetailsUtils.Type.ORDER, // OrderedType
										(double) order.getQuantityBig( reasonCode) , // QuantityBig
										(double) order.getQuantityMedium( reasonCode) , // QuantityMedium
										(double) order.getQuantitySmall( reasonCode) , // QuantitySmall
										(double) order.getQuantityBig ( reasonCode) * order.getItem ().getUnitBigMedium () * order.getItem ().getUnitMediumSmall ()
										+ order.getQuantityMedium ( reasonCode ) * order.getItem ().getUnitMediumSmall () + order.getQuantitySmall ( reasonCode ) , // BasicUnitQuantity
										0.0 , // ApprovedQuantityBig
										0.0 , // ApprovedQuantityMedium
										0.0 , // ApprovedQuantitySmall
										0.0 , // ApprovedBasicUnitQuantity
										0.0 , // MissedQuantityBig
										0.0 , // MissedQuantityMedium
										0.0 , // MissedQuantitySmall
										0.0 , // MissedBasicUnitQuantity
										(double) order.getPriceBig () , // PriceBig
										(double) order.getPriceMedium () , // PriceMedium
										(double) order.getPriceSmall () , // PriceSmall
										(double) order.getPriceBig () , // UserPriceBig
										(double) order.getPriceMedium () , // UserPriceMedium
										(double) order.getPriceSmall () , // UserPriceSmall
										0.0 , // DiscountPercentage
										0.0 , // DiscountAmount
										order.getQuantityBig ( reasonCode ) * order.getPriceBig () + order.getQuantityMedium ( reasonCode ) * order.getPriceMedium ()
										+ order.getQuantitySmall ( reasonCode) * order.getPriceSmall () , //  TotalLineAmount
										null , // LineNote
										null , // ItemLot
										reasonCode , //  ReasonCode
										order.getTax (), // ItemTaxPercentage
										order.getItem ().getItemName () , //  ItemName
										order.getItem ().getItemAltName (), // ItemAltName
										-1 , // ParentLineID
			    						order.getExpiryDate ( reasonCode ) , // ItemExpiryDate
			    						null , // ItemAffectedStock
			    						null , // IsInvoiceRelated
			    						null , // IsCompanyRelated
			    						null , 
										null , 
										null , 
										null , 
										null , 
										now.getTime () ) ); // StampDate
							}
						}// end for loop
					} // End if
					else
						// TODO : Details of a sales order transaction detail
						// Create a new transaction detail
						for ( String reasonCode : order.getQuantitySmall ().keySet () ) {
							// Make sure there is a valid returned quantity of the current item for the current reason
							if ( order.getQuantitySummation ( reasonCode ) == 0 )
								// Skip the current item
								continue;
							add.add ( new TransactionDetails ( null , // ID
									transactionHeader.getTransactionCode () , // TransactionCode
									++ maxId , // LineID
									order.getItem ().getItemCode () , // ItemCode
									TransactionDetailsUtils.Type.ORDER  , // OrderedType
									(double) order.getQuantityBig (  reasonCode), // QuantityBig
									(double) order.getQuantityMedium (  reasonCode), // QuantityMedium
									(double) order.getQuantitySmall (  reasonCode), // QuantitySmall
									(double) order.getQuantityBig (reasonCode)* order.getItem ().getUnitBigMedium () * order.getItem ().getUnitMediumSmall ()
										+ order.getQuantityMedium ( reasonCode)* order.getItem ().getUnitMediumSmall ()
										+ order.getQuantitySmall (  reasonCode), // BasicUnitQuantity
									0.0 , // ApprovedQuantityBig
									0.0 , // ApprovedQuantityMedium
									0.0 , // ApprovedQuantitySmall
									0.0 , // ApprovedBasicUnitQuantity
									0.0 , // MissedQuantityBig
									0.0 , // MissedQuantityMedium
									0.0 , // MissedQuantitySmall
									0.0 , // MissedBasicUnitQuantity
									(double) order.getPriceBig () , // PriceBig
									(double) order.getPriceMedium () , // PriceMedium
									(double) order.getPriceSmall () , // PriceSmall
									(double) order.getPriceBig () , // UserPriceBig
									(double) order.getPriceMedium () , // UserPriceMedium
									(double) order.getPriceSmall () , // UserPriceSmall
									0.0 , // DiscountPercentage
									0.0 , // DiscountAmount
									order.getQuantityBig( reasonCode)* order.getPriceBig() + order.getQuantityMedium( reasonCode)
														* order.getPriceMedium() + order.getQuantitySmall( reasonCode)* order.getPriceSmall(), // TotalLineAmount
									null , // LineNote
									null , // ItemLot
									reasonCode  , // ReasonCode
									order.getTax () , // ItemTaxPercentage
									order.getItem ().getItemName () , // ItemName
									order.getItem ().getItemAltName () , // ItemAltName
									-1 , // ParentLineID
		    						order.getExpiryDate ( reasonCode ) , // ItemExpiryDate
		    						null , // ItemAffectedStock
		    						null , // IsInvoiceRelated
		    						null , // IsCompanyRelated
		    						null , 
									null , 
									null , 
									null , 
									null , 
									now.getTime () ) ); // StampDate
						} // End for each
				// Update the transaction header in DB
				DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionHeadersDao ().update ( transactionHeader );
							
				// Iterate over all transaction details
				for ( TransactionDetails transactionDetail : transactionDetails )
					// Determine if the current transaction detail should be updated
					if ( update.contains ( transactionDetail ) ) {
						// Update the transaction detail in DB
						DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsDao ().update ( transactionDetail );
					} // End if
					else {
						// Otherwise the transaction detail should be removed
						DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsDao ().delete ( transactionDetail );
					} // End else
				// Iterate over all new transaction details
				for ( TransactionDetails transactionDetail : add ) {
					// Insert the new transaction detail into DB
					DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsDao ().insert ( transactionDetail );
				
					
					
				
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
		
		// Check if an error occurred
		if ( ! error && transactionHeaderCode != null ) {
			// Retrieve a constant reference to the transaction code
			final String transactionCode = transactionHeaderCode;
			// Prompt the user to print a copy
			AppDialog.getInstance ().displayAlert ( this ,
					null ,
					AppResources.getInstance ( this ).getString ( this , R.string.printing_activity_print_request_message ) ,
					AppDialog.ButtonsType.YesNo ,
					new DialogInterface.OnClickListener () {
						@Override
						public void onClick ( DialogInterface dialog , int which ) {
							Intent intent = null;
							// Determine the clicked button
							switch ( which ) {
							case DialogInterface.BUTTON_POSITIVE:
								// Print the transaction
								intent = new Intent ( DirectReturnDetailsActivity.this, PrintingActivity.class );
								intent.putExtra ( PrintingActivity.TYPE , PrintingActivity.Type.DirectReturn );
								intent.putExtra ( PrintingActivity.PRINTOUT , PrintingActivity.Printout.ORIGINAL );
								intent.putExtra ( PrintingActivity.HEADER , new ArrayList < TransactionHeaders > ( DatabaseUtils.getInstance ( DirectReturnDetailsActivity.this ).getDaoSession ().getTransactionHeadersDao ().queryBuilder ()
										.where ( TransactionHeadersDao.Properties.TransactionCode.eq ( transactionCode ) ).list () ) );
								startActivityForResult ( intent , REQUEST_CODE_PRINT );
								break;
							case DialogInterface.BUTTON_NEGATIVE:
						    	// Call this to set the result that your activity will return to its caller
								intent = new Intent();
								intent.putExtra ( DirectReturnActivity.SAVE_SUCCESS , ! error );
						    	setResult ( RESULT_OK , intent );
					    		// Finish this activity
					    		finish ();
								// Dismiss dialog
								AppDialog.getInstance ().dismiss ();
								break;
							} // End switch
						} } );
		} // End if
		else {
	    	// Call this to set the result that your activity will return to its caller
			Intent intent = new Intent();
			intent.putExtra ( DirectReturnActivity.SAVE_SUCCESS , ! error );
	    	setResult ( RESULT_OK , intent );
	    	// Finish this activity (if allowed)
	    	if ( finish )
	    		// Finish this activity
	    		finish ();
		} // End else
	} // End else if
    /**
     * Saves the current return.
     */
  
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
	 * Gets the sales order details adapter. The default implementation uses {@link me.SyncWise.Android.Modules.SalesOrder.SalesOrderDetailsAdapter SalesOrderDetailsAdapter}.<br>
	 * In order to used a custom adapter, override this method.<br>
	 * <b>Note : </b> This method should not return {@code NULL}.
	 * 
	 * @param layout	XML Layout (layout) resource id.
	 * @param orderItems	List of {@link me.SyncWise.Android.Modules.SalesOrder.Order Order} objects.
	 * @param itemsEnabled	Flag indicating if the items are enabled or not.
	 * @return	A sales order details adapter.
	 */
	protected ListAdapter getSalesOrderDetailsAdapter ( int layout , List < OrderDirectReturn > orderItems , final boolean itemsEnabled , final boolean userpicker  ) {
		return new DirectReturnDetailAdapter ( DirectReturnDetailsActivity.this , layout , orderItems , currency , itemsEnabled , userpicker );
	}
	
	/**
	 * Indicates if there are valid sales orders (with non-zero quantities).<br>
	 * This method iterates over the list of orders and checks their quantities.
	 * 
	 * @return	Boolean indicating if there is at least one valid order.
	 */
	private boolean hasSalesOrders () {
		// Check if the orders list is valid
		if ( orders == null )
			// Invalid list
			return false;
		// Declare and initialize a list used to host the valid sales orders
		ArrayList < OrderDirectReturn > salesOrder = new ArrayList < OrderDirectReturn > ();
		// Iterate over all orders
		for ( OrderDirectReturn order : orders )
			// Check if the current order has valid quantities
			if ( order.getQuantityBigSummation () != 0 || order.getQuantityMediumSummation () != 0 || order.getQuantitySmallSummation () != 0 )
				// The order contains at least one quantity, add it to the sales order list
				salesOrder.add ( order );
		// Check if there is at least one valid sales order
		if ( salesOrder.isEmpty () )
			// No sales order
			return false;
		else
			// At least one order
			return true;
	}
	
	/**
	 * Indicates whether the sales order has new / unsaved modifications.<br>
	 * This method iterates over the list of all selected orders and compares them with the transaction details list, along with the transaction note and delivery date.<br>
	 * <b>Note : </b> This method always returns false if there are no previously stored transaction details.
	 * 
	 * @return	Boolean stating if there are new modifications.
	 */
	private boolean hasModifications () {
		// Check if there is a previously stored sales order
		if ( transactionHeader == null || transactionDetails == null || selectedOrders == null )
			// No saved sales order to match to
			return false;
		// Compare the sales order note
		if ( ! note.equals ( transactionHeader.getNotes () ) )
			// There is at least one modification
			return true;
		// Compute the delivery date
		Calendar deliveryDate = Calendar.getInstance ();
		deliveryDate.setTimeInMillis ( transactionHeader.getDeliveryDate ().getTime () );
		// Compare the delivery dates
		if ( deliveryDate.get ( Calendar.YEAR ) != selectedYear
				|| deliveryDate.get ( Calendar.MONTH ) != selectedMonth
				|| deliveryDate.get ( Calendar.DATE ) != selectedDay )
			// There is at least one modification
			return true;
		// Map the transaction details to their item codes
		Map < String , ArrayList< TransactionDetails > > _transactionDetails = new HashMap < String , ArrayList < TransactionDetails >  > ();
		// Iterate over all transaction details
		for ( TransactionDetails transactionDetail : transactionDetails )
			// Map the current transaction detail to its item code
			if ( _transactionDetails.containsKey(transactionDetail.getItemCode()))
				// Map the current transaction detail to its item code
				_transactionDetails.get ( transactionDetail.getItemCode () ).add( transactionDetail );
			else { 
				ArrayList< TransactionDetails > tempDetails =  new ArrayList<TransactionDetails>();
				tempDetails.add( transactionDetail );
				_transactionDetails.put(transactionDetail.getItemCode(), tempDetails);
			}
		// Declare and initialize a list used to host the valid sales orders
		ArrayList < OrderDirectReturn > salesOrder = new ArrayList < OrderDirectReturn > ();
		// Iterate over all orders
		for ( OrderDirectReturn order : orders )
			// Check if the current order has valid quantities
			if ( order.getQuantityBigSummation() != 0 || order.getQuantityMediumSummation() != 0 || order.getQuantitySmallSummation() != 0 )
				// The order contains at least one quantity, add it to the sales order list
				salesOrder.add ( order );
		// Check if both sizes differ
		if ( salesOrder.size () != _transactionDetails.size () )
			// There is at least one modification
			return true;
		// Iterate over the sales orders
		for ( OrderDirectReturn order : salesOrder )
			// Determine the corresponding transaction detail
			if ( _transactionDetails.containsKey ( order.getItem ().getItemCode () ) ) {
				// Retrieve a reference to the transaction detail
				ArrayList< TransactionDetails >  transactionDetails = _transactionDetails.get ( order.getItem ().getItemCode () );
				/*
				 * Compare the following fields :
				 * - Quantity big
				 * - Quantity medium
				 * - Quantity small
				 * - Price Big Default
				 * - Price Medium Default
				 * - Price Small Default
				 * - Price Big Used
				 * - Price Medium Used
				 * - Price Small Used
				 * - Tax Percentage
				 */
				int total = 0 ;
				for (int i = 0 ; i < transactionDetails.size() ; i++ )
					total += transactionDetails.get( i ).getBasicUnitQuantity().intValue();
				for (int i = 0 ; i < transactionDetails.size() ; i++ )
				if ( ! transactionDetails.get( i ).getQuantityBig ().equals ( (double) order.getQuantityBig ( transactionDetails.get ( i ).getReasonCode () ) )
						|| ! transactionDetails.get( i ).getQuantityMedium ().equals ( (double) order.getQuantityMedium ( transactionDetails.get ( i ).getReasonCode () ) )
						|| ! transactionDetails.get( i ).getQuantitySmall ().equals ( (double) order.getQuantitySmall ( transactionDetails.get ( i ).getReasonCode () ) )
						|| ! transactionDetails.get( i ).getPriceBig ().equals ( order.getPriceBig () )
						|| ! transactionDetails.get( i ).getPriceMedium ().equals ( order.getPriceMedium () )
						|| ! transactionDetails.get( i ).getPriceSmall ().equals ( order.getPriceSmall () )
						|| ! transactionDetails.get( i ).getUserPriceBig ().equals ( order.getPriceBig () )
						|| ! transactionDetails.get( i ).getUserPriceMedium ().equals ( order.getPriceMedium () )
						|| ! transactionDetails.get( i ).getUserPriceSmall ().equals ( order.getPriceSmall () )
						|| ! transactionDetails.get( i ).getItemTaxPercentage ().equals ( order.getTax () ) )
					// There is at least one modification
					return true;

				if( order.getBasicQuantity() != total )
					return true;
			} // End if
		
			else
				// There is at least one modification
				return true;
		// Otherwise there are no modifications
		return false;
	}
	
	/**
	 * Filters the {@link #orders} list based on the indicated {@link #searchQuery} string and the {@link #selectedDivisionsCodes} list.<br>
	 * <b>NOTE : </b> This method does not perform any kind of integrity check what so ever !
	 * 
	 * @return	List of {@link me.SyncWise.Android.Modules.SalesOrder.Order Order} objects filtered according to the search and divisions filter.
	 */
	@SuppressLint("DefaultLocale") 
	protected ArrayList < OrderDirectReturn > filter () {
		// Declare and initialize a new orders list
		ArrayList < OrderDirectReturn > _orders = new ArrayList < OrderDirectReturn > ();
		// Iterate over all the orders
		for ( OrderDirectReturn order : orders ) {
				// Determine if at least one of the order's divisions is selected
				boolean skip = true;
				// Iterate over all order's division
				for ( String divisionCode : order.getDivisionCodes () )
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
				if ( order.getItem ().getItemName ().toLowerCase ().contains ( searchQuery.toLowerCase () )
						|| order.getItem ().getItemCode ().toLowerCase ().contains ( searchQuery.toLowerCase () )
						|| ( order.getBarCodes () != null && order.getBarCodes ().contains ( searchQuery.toLowerCase () ) ) )
					// Add the order to the list
					_orders.add ( order );
			} // End for each
		
		// Return the new orders list
		return _orders;
	}
	
	/**
	 * AsyncTask helper class used to populate the sales order items list with the appropriate items / orders.
	 * 
	 * @author Elias
	 *
	 */
	private class PopulateList extends AsyncTask < Void , Void , ArrayList < OrderDirectReturn > > {
		
		/**
		 * Flag used to determine if an error occurred.
		 */
		private boolean error;
		
		/**
		 * Flag used to indicated if the activity ended before the asynctask finished executing.
		 */
		private boolean activityEnded;

		/**
		 * Temporary map of orders.
		 */
		private HashMap < String , OrderDirectReturn > _orders;
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@SuppressWarnings("unchecked")
		@Override
		protected ArrayList < OrderDirectReturn > doInBackground ( Void ... params ) {
			try {

				// Declare and initialize a cursor object used to retrieve data sets from query results
				Cursor cursor = null;
				// Declare the SQL string and selection arguments array of strings used to to query DB
				String SQL = null;
				String selectionArguments [] = null;
				// Retrieve the user, company, client and division codes
				String companyCode = DatabaseUtils.getCurrentCompanyCode ( DirectReturnDetailsActivity.this );
				String clientCode = ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClient ().getClientCode ();
				String divisionCode = ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClientDivision ().getDivisionCode ();
				
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
					cursor = DatabaseUtils.getInstance ( DirectReturnDetailsActivity.this ).getDaoSession ().getDatabase ().rawQuery ( SQL , selectionArguments );
					// Move the cursor to the first raw
					if ( cursor.moveToFirst () ) {
						do {
							// Retrieve the current currency
							currency = DatabaseUtils.getInstance ( DirectReturnDetailsActivity.this ).getDaoSession ().getCurrenciesDao ().readEntity ( cursor , 0 );
						} while ( cursor.moveToNext () );
					} // End if
					// Clear cursor
					cursor.close ();
					cursor = null;
				} // End if
					
				// So far here, the currency and company objects must all be valid objects
				// If not, cannot continue
				// Check if both the company and the currency are valid
				if ( getIntent ().getStringExtra ( TRANSACTION_HEADER_CODE ) == null && currency == null ) {
					// Set flag to indicate an error
					error = true;
					// Clear the task reference
					populateList = null;
					// Exit method
					return null;
				} // End if
				
				// Determine if there is an available transaction header code
				if ( getIntent ().getStringExtra ( TRANSACTION_HEADER_CODE ) != null && transactionHeader == null ) {
					// Try to retrieve the transaction header with the specified transaction code
					transactionHeader = DatabaseUtils.getInstance ( DirectReturnDetailsActivity.this ).getDaoSession ().getTransactionHeadersDao ().queryBuilder ()
							.where ( TransactionHeadersDao.Properties.TransactionCode.eq ( getIntent ().getStringExtra ( TRANSACTION_HEADER_CODE ) ) ).unique ();
				
					// Check if the transaction header is valid
					if ( transactionHeader != null ) {
						// Refresh object
						DatabaseUtils.getInstance ( DirectReturnDetailsActivity.this ).getDaoSession ().getTransactionHeadersDao ().refresh ( transactionHeader );
						// Set the sales order note
						note = transactionHeader.getNotes ();
						// Set the client reference number
						clientReferenceNumber = transactionHeader.getClientReferenceNumber () == null ? "" : transactionHeader.getClientReferenceNumber ();
						// Set the delivery date
						Calendar deliveryDate = Calendar.getInstance ();
						deliveryDate.setTimeInMillis ( transactionHeader.getDeliveryDate ().getTime () );
						selectedYear = deliveryDate.get ( Calendar.YEAR );
						selectedMonth = deliveryDate.get ( Calendar.MONTH );
						selectedDay = deliveryDate.get ( Calendar.DATE );
						// Initialize the transaction details list
						transactionDetails = new ArrayList < TransactionDetails > ();
						// Retrieve the appropriate transaction details
						transactionDetails.addAll ( DatabaseUtils.getInstance ( DirectReturnDetailsActivity.this ).getDaoSession ().getTransactionDetailsDao ().queryBuilder ()
							.where ( TransactionDetailsDao.Properties.TransactionCode.eq ( getIntent ().getStringExtra ( TRANSACTION_HEADER_CODE ) ) )
							.orderAsc ( TransactionDetailsDao.Properties.LineID ).list () );
						// Iterate over all transaction details
						for ( TransactionDetails transactionDetail : transactionDetails ) {
							// Refresh object
							DatabaseUtils.getInstance ( DirectReturnDetailsActivity.this ).getDaoSession ().getTransactionDetailsDao ().refresh ( transactionDetail );
						} // End for each
						
						// Retrieve the appropriate currency
						currency = DatabaseUtils.getInstance ( DirectReturnDetailsActivity.this ).getDaoSession ().getCurrenciesDao ().queryBuilder ()
							.where ( CurrenciesDao.Properties.CurrencyCode.eq ( transactionHeader.getCurrencyCode () ) ).unique ();
						OrderRFRInfo i1 = new	OrderRFRInfo ( 1 , "1" , transactionHeader.getInfo1() );
						list1.clear();
						list1.add(i1);
						cash=list1.get(0).getValue().toString();
					} // End if
				} // End if
				
				// Check if the client reference number is valid
				if ( clientReferenceNumber == null )
					// Set the client reference number
					clientReferenceNumber = "";
				
				// Retrieve all divisions
				List < Divisions > allDivisions = DatabaseUtils.getInstance ( DirectReturnDetailsActivity.this ).getDaoSession ().getDivisionsDao ().queryBuilder ()
						.where ( DivisionsDao.Properties.CompanyCode.eq ( companyCode ) ).list ();
				// Retrieve the divisions linked to the user
				List < Divisions > directUserDivisions = DatabaseUtils.getInstance ( DirectReturnDetailsActivity.this ).getDaoSession ().getDivisionsDao ()
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
				cursor = DatabaseUtils.getInstance ( DirectReturnDetailsActivity.this ).getDaoSession ().getDatabase ().rawQuery ( SQL , selectionArguments );
				// Move the cursor to the first raw
				if ( cursor.moveToFirst () ) {
					do {
						// Retrieve the current price
						Prices price = DatabaseUtils.getInstance ( DirectReturnDetailsActivity.this ).getDaoSession ().getPricesDao ().readEntity ( cursor , 0 );
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
					// Otherwise, display all the user divisions
					DirectReturnDetailsActivity.this.divisions.addAll ( allUserDivisions );
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
				
				// Check if there are saved orders
				if ( retrieveOrders ) {
					// Retrieve modified calls
					orders = (ArrayList < OrderDirectReturn >) ActivityInstance.readDataGson ( DirectReturnDetailsActivity.this , DirectReturnDetailsActivity.class.getName () , ORDERS , new TypeToken < ArrayList < OrderDirectReturn > > () {}.getType () );
					// Initialize the list used to host the selected sales orders
					selectedOrders = new ArrayList < OrderDirectReturn > ();
					// Iterate over all orders
					for ( OrderDirectReturn order : orders )
		    			// Check if the current order has valid quantities
		    			if ( order.getQuantityBigSummation () != 0 || order.getQuantityMediumSummation () != 0 || order.getQuantitySmallSummation () != 0 )
		    				// The order contains at least one quantity, add it to the sales order list
		    				selectedOrders.add ( order );
				} // End if
				else {
					// Retrieve the client item divisions
					List < ItemDivisions > itemDivisions = EntityUtils.queryByGroup ( selectedDivisionsCodes ,
							DatabaseUtils.getInstance ( DirectReturnDetailsActivity.this ).getDaoSession ().getItemDivisionsDao () ,
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
							DatabaseUtils.getInstance ( DirectReturnDetailsActivity.this ).getDaoSession ().getItemsDao () ,
							ItemsDao.Properties.ItemCode );
					
					// Retrieve the item barcodes
					List < ItemBarcodes > itemBarcodes = EntityUtils.queryByGroup ( itemCodes ,
							DatabaseUtils.getInstance ( DirectReturnDetailsActivity.this ).getDaoSession ().getItemBarcodesDao () ,
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
					List < Units > units = DatabaseUtils.getInstance ( DirectReturnDetailsActivity.this ).getDaoSession ().getUnitsDao ().loadAll ();
					// Map all the Units using their codes
					Map < String , Units > _units = new HashMap < String , Units > ();
					// Iterate over all UOM
					for ( Units unit : units )
						// Map the unit to its code
						_units.put ( unit.getUnitCode () , unit );
					
					// Retrieve all the taxes
					List < Taxes > taxes = DatabaseUtils.getInstance ( DirectReturnDetailsActivity.this ).getDaoSession ().getTaxesDao ().loadAll ();
					// Declare and initialize a map of taxes
					Map < String , Taxes > _taxes = new HashMap < String , Taxes > ();
					// Iterate over all taxes
					for ( Taxes tax : taxes )
						// Map the tax object to its tax code
						_taxes.put ( tax.getTaxCode () , tax );
					
					// Retrieve the client must stock list
					List < ClientMustStockList > clientMustStockList = DatabaseUtils.getInstance ( DirectReturnDetailsActivity.this ).getDaoSession ().getClientMustStockListDao ()
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
					List < ClientItemHistory > clientItemHistory = DatabaseUtils.getInstance ( DirectReturnDetailsActivity.this ).getDaoSession ().getClientItemHistoryDao ().queryBuilder ()
							.where ( ClientItemHistoryDao.Properties.ClientCode.eq ( clientCode ) ,
									ClientItemHistoryDao.Properties.DivisionCode.eq ( DatabaseUtils.getCurrentDivisionCode ( DirectReturnDetailsActivity.this ) ) ,
									ClientItemHistoryDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( DirectReturnDetailsActivity.this ) ) ).list ();
					// Get the item codes of the item history
					ArrayList < String > clientItemHistoryCodes = new ArrayList < String > ();
					for ( ClientItemHistory itemHistory : clientItemHistory )
						clientItemHistoryCodes.add ( itemHistory.getItemCode () );
					
					// Initialize the lists used to host the sales orders and the selected sales orders
					orders = new ArrayList < OrderDirectReturn > ();
					selectedOrders = new ArrayList < OrderDirectReturn > ();
					// Iterate over all items
					for ( Items item : items ) {
						// Check if the item is NOT an item (ex: asset ...)
						if ( item.getItemType () == null || item.getItemType () != ItemsUtils.Type.REGULAR )
							// Skip the current item
							continue;
						// Check if current item has a valid unit of measurement
						if ( _units.get ( item.getUnitCode () ) == null )
							// Skip the current item
							continue;
						// Create a new order using the current item and its appropriate unit of measurement and currency
						OrderDirectReturn order = new OrderDirectReturn ( item , _units.get ( item.getUnitCode () ) );
						// Set the divisions codes list
						order.setDivisionCodes ( _itemDivisions.get ( item.getItemCode () ) );
						// Check that the division codes list is valid
						if ( order.getDivisionCodes () == null )
							// Initialize the division codes list
							order.setDivisionCodes ( new ArrayList < String > () );
						// Set the item bar codes list
						order.setBarCodes ( _itemBarcodes.get ( item.getItemCode () ) );
						// Set the order prices
						if ( itemPrices.containsKey ( order.getItem ().getItemCode () ) ) {
							Prices orderPrices = itemPrices.get ( order.getItem ().getItemCode () );
							order.setPriceBig ( orderPrices.getPriceBig () );
							order.setPriceMedium ( orderPrices.getPriceMedium () );
							order.setPriceSmall ( orderPrices.getPriceSmall () );
						} // End if
						// Set the item tax
						if ( ItemsUtils.isTaxable ( order.getItem () ) && _taxes.get ( order.getItem ().getTaxCode () ) != null )
							order.setTax ( _taxes.get ( order.getItem ().getTaxCode () ).getTaxPercentage () );
						// Determine if the item is a must stock list
						if ( mustStockListItems.contains ( item.getItemCode () ) )
							// Indicate that the item is a must stock item
							order.setMustStockList ( true );
						// Otherwise check if the item is a history item
						else if ( clientItemHistoryCodes.contains ( item.getItemCode () ) )
							// Indicate that the item is a must stock item
							order.setHistory ( true );
						
						// Add the current item in the orders list
						orders.add ( order );
					} // End for each
				} // End else
				
				// Sort the items, and put the MSL first , then the history items, then the remaining items
				Collections.sort ( orders , new Comparator < OrderDirectReturn > () {
					@Override
					public int compare ( OrderDirectReturn order1 , OrderDirectReturn order2 ) {
						// Sort the items 
						if ( order1.isMustStockList () && ! order2.isMustStockList () )
							return -1;
						else if ( ! order1.isMustStockList () && order2.isMustStockList () )
							return 1;
						else if ( order1.isMustStockList () && order2.isMustStockList () )
							return order1.getItem ().getItemName ().compareToIgnoreCase ( order2.getItem ().getItemName () );
						
						// Otherwise both are not must stock list
						if ( order1.isHistory () && ! order2.isHistory () )
							return -1;
						else if ( ! order1.isHistory () && order2.isHistory () )
							return 1;
						else
							return order1.getItem ().getItemName ().compareToIgnoreCase ( order2.getItem ().getItemName () );
					}
				} );
				
				// Map the orders by their item codes
				_orders = new HashMap < String , OrderDirectReturn > ();
				// Iterate over the orders
				for ( OrderDirectReturn order : orders )
					// Map the current order to its item code
					_orders.put ( order.getItem ().getItemCode (), order);

				// Check if the transaction details list is valid
				if ( transactionDetails != null ) {
					// Keep track of the added item codes
					HashSet < String > itemCodes = new HashSet < String > ();
					// Iterate over all transaction details
					for ( TransactionDetails transactionDetail : transactionDetails ) {
						// Retrieve the transaction reason
						Reasons reason = DatabaseUtils.getInstance ( DirectReturnDetailsActivity.this ).getDaoSession ().getReasonsDao ().queryBuilder ()
								.where ( ReasonsDao.Properties.ReasonCode.eq ( transactionDetail.getReasonCode () ) ).unique ();
						// Check if the reason is valid
						if ( reason == null )
							// Invalid reason, skip the current transaction detail
							continue;
						// Set the appropriate reason
						_orders.get ( transactionDetail.getItemCode () ).setReasonOfReturn ( reason );
						// Set the item expiry date
						_orders.get ( transactionDetail.getItemCode () ).setExpiryDate ( transactionDetail.getReasonCode () , transactionDetail.getItemExpiryDate () );
						// Set the quantities
						_orders.get ( transactionDetail.getItemCode () ).setQuantityBig ( transactionDetail.getReasonCode () , transactionDetail.getQuantityBig ().intValue () );
						_orders.get ( transactionDetail.getItemCode () ).setQuantityMedium ( transactionDetail.getReasonCode () , transactionDetail.getQuantityMedium ().intValue () );
						_orders.get ( transactionDetail.getItemCode () ).setQuantitySmall ( transactionDetail.getReasonCode () , transactionDetail.getQuantitySmall ().intValue () );
						// Make the sure the quantities are valid
						if ( transactionDetail.getQuantityBig () != 0 || transactionDetail.getQuantityMedium () != 0 || transactionDetail.getQuantitySmall () != 0 ) {
							// Check if the item is previously added
							if ( ! itemCodes.contains ( transactionDetail.getItemCode () ) ) {
								// Add the order to the selected list
								selectedOrders.add ( _orders.get ( transactionDetail.getItemCode () ) );
								// Add the item code to the list
								itemCodes.add ( transactionDetail.getItemCode () );
							} // End if
						} // End if
					} // End for each
				} // End if
				
				// Clear the task reference
				populateList = null;
				
				// Determine if the request code is INFO
				if ( requestCode != REQUEST_CODE_INFO )
					// Compute and return the filtered list based on the provided search query and selected divisions
					return filter ();
				// Otherwise the request code is INFO
				else {
					// Initialize the sales order details
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
		protected void onPostExecute ( ArrayList < OrderDirectReturn > orders ) {
			// Check if the activity has ended
			if ( activityEnded )
				// Do nothing
				return;
			// Determine if the activity must be finished
			if ( error ) {
				// Indicate that the activity cannot be displayed
				new AppToast ( DirectReturnDetailsActivity.this )
					.setIcon ( R.drawable.warning )
					.setText ( AppResources.getInstance ( DirectReturnDetailsActivity.this ).getString ( DirectReturnDetailsActivity.this , R.string.error_label ) )
					.setDuration ( Toast.LENGTH_LONG )
					.show ();
				// Initialize the orders list
				DirectReturnDetailsActivity.this.orders = new ArrayList < OrderDirectReturn > ();
				// Exit method
				return;
			} // End if
    		
    		// Retrieve a reference to the secondary view
    		View secondary = findViewById ( R.id.layout_order_note );
			// Display the secondary view accordingly
    		secondary.setVisibility ( displayOrderNote ? View.VISIBLE : View.GONE );
    		// Enable the main list accordingly
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( ! displayOrderNote );
    		// Determine if the sales order note is undergoing any modifications
    		if ( displayOrderNote )
	    		// Restore the secondary view
    			initializeSecondaryView ( true );
    		
    		// Retrieve a reference to the tertiary view
    		View tertiary = findViewById ( R.id.layout_client_reference_number );
			// Display the tertiary view accordingly
    		tertiary.setVisibility ( displayClientReferenceNumber ? View.VISIBLE : View.GONE );
    		// Enable the main list accordingly
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( ! displayClientReferenceNumber );
    		// Determine if the client reference number is undergoing any modifications
    		if ( displayClientReferenceNumber )
	    		// Restore the tertiary view
    			initializeTertiaryView ( true );
				
			// Determine if the request code is INFO
			if ( requestCode != REQUEST_CODE_INFO )
				// Set a new adapter
				setListAdapter ( getSalesOrderDetailsAdapter( R.layout.sales_order_details_activity_item , new ArrayList < OrderDirectReturn > ( orders ) , true , true ) );
			// Otherwise the request code is INFO
			else {
				infoOrderDirectReturn = new ArrayList < OrderDirectReturn > ();
				for ( OrderDirectReturn order : selectedOrders ) {
					for ( int i = 0 ;i < order.getReasonOfReturn ().size () ; i ++ ) {
						OrderDirectReturn io = new OrderDirectReturn ( order.getItem (), order.getUnit () );
						io.setQuantityBig ( order.getReasonOfReturn ().get ( i ).getReasonCode () , order.getQuantityBig ( order.getReasonOfReturn ().get( i ).getReasonCode () ) );
						io.setQuantityMedium ( order.getReasonOfReturn ().get ( i ).getReasonCode () , order.getQuantityMedium ( order.getReasonOfReturn ().get( i ).getReasonCode () ) );
						io.setQuantitySmall ( order.getReasonOfReturn ().get ( i ).getReasonCode () , order.getQuantitySmall ( order.getReasonOfReturn ().get ( i ).getReasonCode () ) );
						io.setExpiryDate ( order.getReasonOfReturn ().get ( i ).getReasonCode () , order.getExpiryDate ( order.getReasonOfReturn ().get ( i ).getReasonCode () ) );
						io.setBarCodes ( order.getBarCodes () );
						io.setDivisionCodes ( order.getDivisionCodes () );
						io.setPriceBig ( order.getPriceBig () );
						io.setPriceMedium ( order.getPriceMedium () );
						io.setPriceSmall ( order.getPriceSmall () );
						io.setTax ( order.getTax () );
						io.setReasonOfReturn ( order.getReasonOfReturn ().get ( i ) );
						if ( io.getBasicQuantity () != 0 )
							infoOrderDirectReturn.add ( io );
					} // End for loop
				} // End for each
				
				// Declare and initialize a multiple list adapter
				MultipleListAdapter adapter = new MultipleListAdapter ( DirectReturnDetailsActivity.this );
				// Add the sales order details adapter
				adapter.addSection ( new Section ( AppResources.getInstance ( DirectReturnDetailsActivity.this ).getString ( DirectReturnDetailsActivity.this , R.string.sales_order_details_label ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
						new OrderDirectReturnInfoAdapter ( DirectReturnDetailsActivity.this , R.layout.order_info_item , details ) );
				Users user = DatabaseUtils.getInstance (DirectReturnDetailsActivity.this ).getDaoSession ().getUsersDao ().queryBuilder ()
						.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode (DirectReturnDetailsActivity.this ) ) ,
								UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( DirectReturnDetailsActivity.this ) ) ).unique ();
				// Check if the logged in user is a cash van
				if ( user.getUserType () == 11 ) 
				if(ClientCard.isCreditClient(( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClient()))
					adapter.addSection ( new Section ( AppResources.getInstance (DirectReturnDetailsActivity.this ).getString ( DirectReturnDetailsActivity.this , R.string.sales_order_details_label_cash ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
							new CashOrderRFRAdapter( DirectReturnDetailsActivity.this , R.layout.cash_order,list1,! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) ) );
			
				
				// Add the sales order adapter
				adapter.addSection ( new Section ( AppResources.getInstance ( DirectReturnDetailsActivity.this ).getString ( DirectReturnDetailsActivity.this , R.string.sales_order_products_label ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
						getSalesOrderDetailsAdapter ( R.layout.sales_order_details_activity_item , infoOrderDirectReturn , ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) , false ) );
				// Set the list adapter
				setListAdapter ( adapter );
			} // End else
    		// Refresh the action bar
    		invalidateOptionsMenu ();
    		// Clear temporary variable
    		_orders = null;
		}
		
	}
	
	/*
	 * Callback method to be invoked when an action item in this QuickAction has been clicked.
	 *
	 * @see me.SyncWise.Android.Widgets.QuickAction.QuickAction.OnActionItemClickListener#onItemClick(me.SyncWise.Android.Widgets.QuickAction.QuickAction, int, int)
	 */
	@Override
	public void onItemClick ( QuickAction source , View anchor , Object object , int pos , int actionId ) {
		// Retrieve the selected order
		OrderDirectReturn order = ( (ViewHolder) anchor.getTag () ).order;
		OrderDirectReturn selectedOrder = null;
		// Iterate over the RFR details
		for ( int i = 0 ; i < infoOrderDirectReturn.size () ; i++ ) {
			// Check the current item matches the current RFR detail
			if ( infoOrderDirectReturn.get ( i ).getItem ().getItemCode ().equals ( order.getItem ().getItemCode () ) )
				// Iterate over the reasons
				for ( int j = 0 ; j < order.getReasonOfReturn ().size () ; j ++ )
					// Check if the reasons match
					if ( infoOrderDirectReturn.get ( i ).getReasonOfReturn ().get ( 0 ).equals ( order.getReasonOfReturn ().get ( j ) ) ) {
						// Set the selected order
						selectedOrder = infoOrderDirectReturn.get ( i );
						break;
					} // End if
		} // End for loop
		switch ( actionId ) {
		case ActionItemID.REMOVE:
			// Iterate over the selected orders
			for ( int i = 0 ; i < selectedOrders.size () ; i ++ )
				// Check if orders match
				if ( selectedOrder != null && selectedOrders.get ( i ).getItem ().getItemCode ().equals ( selectedOrder.getItem ().getItemCode () ) ) {
					// Iterate over the reasons to clear the appropriate reason's quantities
					for ( int j = 0 ; j < selectedOrders.get ( i ).getReasonOfReturn ().size () ; j ++ )
						// Check if the reasons match
						if ( selectedOrders.get ( i ).getReasonOfReturn ().get( j ).equals ( selectedOrder.getReasonOfReturn ().get ( 0 ) ) ) {
							// Clear quantities
							selectedOrders.get ( i ).setExpiryDate ( selectedOrders.get ( i ).getReasonOfReturn ().get ( j ).getReasonCode () , null );
							selectedOrders.get ( i ).setQuantityBig ( selectedOrders.get ( i ).getReasonOfReturn ().get ( j ).getReasonCode () , 0 );
							selectedOrders.get ( i ).setQuantityMedium ( selectedOrders.get ( i ).getReasonOfReturn ().get ( j ).getReasonCode () , 0 );
							selectedOrders.get ( i ).setQuantitySmall ( selectedOrders.get ( i ).getReasonOfReturn ().get ( j ).getReasonCode () , 0 );
							break;
						} // End if
					break;
				} // End if
			// Do the same for the info order lists
			if ( infoOrderDirectReturn != null && selectedOrder !=null && ! infoOrderDirectReturn.isEmpty () ) {
				if ( ! selectedOrder.getReasonOfReturn ().isEmpty () ) {
					selectedOrder.setExpiryDate ( selectedOrder.getReasonOfReturn().get( 0 ).getReasonCode () , null );
					selectedOrder.setQuantityBig ( selectedOrder.getReasonOfReturn().get( 0 ).getReasonCode () , 0 );
					selectedOrder.setQuantityMedium ( selectedOrder.getReasonOfReturn().get( 0 ).getReasonCode () , 0 );
					selectedOrder.setQuantitySmall ( selectedOrder.getReasonOfReturn().get( 0 ).getReasonCode () , 0 );
				} // End if
				// Remove item from the selected orders (if valid)
				infoOrderDirectReturn.remove ( selectedOrder );
			} // End if
			
			// Refresh sales order details
			refreshDetails ();
			// Check if the request code is INFO
			// AND if the selected orders list is empty
			if ( requestCode == REQUEST_CODE_INFO && infoOrderDirectReturn != null && infoOrderDirectReturn.isEmpty () )
				// Emulate a back key press
				onBackPressed ();
			else
				// Refresh the list
				( (MultipleListAdapter) getListAdapter () ).notifyDataSetChanged ();
			break;
		} // End of switch
	}
	
	/**
	 * Performs all the required initialization for the sales order details :
	 * <ul>
	 * <li>The {@link #selectedOrders} list is populated.</li>
	 * <li>The {@link #details} list is populated.</li>
	 * </ul>
	 */
	@SuppressLint("DefaultLocale") 
	protected void initializeDetails () {
		// Retrieve the user code
		String userCode = DatabaseUtils.getCurrentUserCode ( this );
		// Retrieve the company code
		String companyCode = DatabaseUtils.getCurrentCompanyCode ( this );
    	// Retrieve the limit amount
    	int limit = PermissionsUtils.getRFRLimitAmount ( this , userCode , companyCode ) + PermissionsUtils.getRFRLimitMargin ( this , userCode , companyCode );
    	
		// Retrieve the user object
		Users user = DatabaseUtils.getInstance ( this ).getDaoSession ().getUsersDao ().queryBuilder ()
				.where ( UsersDao.Properties.UserCode.eq ( userCode ) ,
						UsersDao.Properties.CompanyCode.eq ( companyCode ) ).unique ();
    	
		// Compute the sales order details
    	double grossAmount = 0;
//    	double taxes = 0;
    	double totalValue = 0;
		// Iterate over all the selected orders
		for ( OrderDirectReturn order : selectedOrders ) {
			// Compute the current gross value for non free items
			double _grossAmount = order.getQuantityBigSummation () * order.getPriceBig () + order.getQuantityMediumSummation () * order.getPriceMedium () + order.getQuantitySmallSummation () * order.getPriceSmall ();
			// Compute the current tax value
			double _taxes = 0;
			// Check if the item is taxable
			if ( order.getTax () != 0 )
				// Compute the current taxes
				_taxes = _grossAmount * order.getTax () / 100;
			// Compute the current total value
			double _totalValue = _grossAmount + _taxes;
			// Update all the values
			grossAmount += _grossAmount;
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
		// Declare and initialize a two digits format
		DecimalFormat twoDigits = new DecimalFormat ( "00" );
		// Initialize the list of sales order details
		details = new ArrayList < OrderDirectReturnInfo > ();
		// Populate the details list
		details.add ( new OrderDirectReturnInfo ( OrderDirectReturnInfo.ID.DELIVERY_DATE , AppResources.getInstance ( this ).getString ( this , R.string.delivery_date_label ).toUpperCase () , twoDigits.format ( selectedDay ) + "-" + twoDigits.format ( selectedMonth + 1 ) + "-" + selectedYear ) );
		details.add ( new OrderDirectReturnInfo ( OrderDirectReturnInfo.ID.NOTE , AppResources.getInstance ( this ).getString ( this , R.string.sales_order_note_label ) , note ) );
		if ( user.getUserType () != 11 && limit > 0 && totalValue <= limit )
			details.add ( new OrderDirectReturnInfo ( OrderDirectReturnInfo.ID.CLIENT_REFERENCE_NUMBER , AppResources.getInstance ( this ).getString ( this , R.string.client_reference_number ).toUpperCase () , TextUtils.isEmpty ( clientReferenceNumber ) ? "" : clientReferenceNumber ) );
		details.add ( new OrderDirectReturnInfo ( OrderDirectReturnInfo.ID.CURRENCY , AppResources.getInstance ( this ).getString ( this , R.string.currency_label ).toUpperCase () , currency.getCurrencyName () ) );
		details.add ( new OrderDirectReturnInfo ( OrderDirectReturnInfo.ID.GROSS_VALUE , AppResources.getInstance ( this ).getString ( this , R.string.gross_amount_label ) , moneyFormat.format ( grossAmount ) ) );
//		details.add ( new OrderDirectReturnInfo ( OrderDirectReturnInfo.ID.TAXES , AppResources.getInstance ( this ).getString ( this , R.string.taxes_label ) , moneyFormat.format ( taxes ) ) );
		details.add ( new OrderDirectReturnInfo ( OrderDirectReturnInfo.ID.TOTAL_VALUE , AppResources.getInstance ( this ).getString ( this , R.string.total_value_label ) , moneyFormat.format ( totalValue ) ) );
	}
	
	/**
	 * Refreshes / recomputes all the sales order details (if any).
	 */
	private void refreshDetails () {
		// Check if the selected orders and sales orders details lists are valid
		if ( selectedOrders == null || details == null )
			// Invalid list, do nothing
			return;
		
		// Compute the sales order details
    	double grossAmount = 0;
    	double taxes = 0;
    	double totalValue = 0;
		// Iterate over all the selected orders
		for ( OrderDirectReturn order : selectedOrders ) {
			// Compute the current gross value for non free items
			double _grossAmount = order.getQuantityBigSummation () * order.getPriceBig () + order.getQuantityMediumSummation () * order.getPriceMedium () + order.getQuantitySmallSummation () * order.getPriceSmall ();
			// Compute the current tax value
			double _taxes = 0;
			// Check if the item is taxable
			if ( order.getTax () != 0 )
				// Compute the current taxes
				_taxes = _grossAmount * order.getTax () / 100;
			// Compute the current total value
			double _totalValue = _grossAmount + _taxes;
			// Update all the values
			grossAmount += _grossAmount;
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
		for ( OrderDirectReturnInfo detail : details ) {
			// Check if the current detail is about the sales order gross amount
			if ( detail.getId () == OrderDirectReturnInfo.ID.GROSS_VALUE ) {
				// Update the sales order gross amount
				detail.setValue ( moneyFormat.format ( grossAmount ) );
			} // End if
			// Check if the current detail is about the sales order taxes
			else if ( detail.getId () == OrderDirectReturnInfo.ID.TAXES ) {
				// Update the sales order total taxes
				detail.setValue ( moneyFormat.format ( taxes ) );
			} // End else if
			// Check if the current detail is about the sales order total amount
			else if ( detail.getId () == OrderDirectReturnInfo.ID.TOTAL_VALUE ) {
				// Update the sales order total total amount
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
	private class FilterList extends AsyncTask < Void , Void , ArrayList < OrderDirectReturn > > {
		
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
		protected ArrayList < OrderDirectReturn > doInBackground ( Void ... params ) {
			// Check if the list of orders is valid
			if ( orders == null || orders.isEmpty () )
				// Invalid orders list
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
		protected void onPostExecute ( ArrayList < OrderDirectReturn > orders ) {
			// Check if the request code is INFO
			if ( requestCode == REQUEST_CODE_INFO )
				// Cannot filter, do nothing
				return;
			// Check if the result is valid
			if ( orders == null )
				// Invalid result, do nothing
				return;
			// Check if the search query has changed
			if ( ! DirectReturnDetailsActivity.this.searchQuery.equals ( searchQuery ) )
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
				// Clear the previous list of orders
				( (ArrayAdapter < OrderDirectReturn >) getListAdapter () ).clear ();
				// Add the new filtered list of orders
				( (ArrayAdapter < OrderDirectReturn >) getListAdapter () ).addAll ( orders );
				// Notifies the attached observers that the underlying data has been changed
				( (ArrayAdapter < OrderDirectReturn >) getListAdapter () ).notifyDataSetChanged ();
			} catch ( Exception exception ) {
				// Cannot cast to array adapter of orders objects
			} // End try-catch block
		}
		
	}

}
