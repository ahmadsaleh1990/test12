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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.AppToast;
import me.SyncWise.Android.DateTime;
import me.SyncWise.Android.Network;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Vibration;

import me.SyncWise.Android.Database.ConnectionSettings;
import me.SyncWise.Android.Database.ConnectionSettingsDao;
import me.SyncWise.Android.Database.ConnectionSettingsUtils;
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
import me.SyncWise.Android.Database.ItemVolumes;
import me.SyncWise.Android.Database.ItemVolumesDao;
import me.SyncWise.Android.Database.Items;
import me.SyncWise.Android.Database.ItemsDao;
import me.SyncWise.Android.Database.ItemsUtils;
import me.SyncWise.Android.Database.MovementDetails;
import me.SyncWise.Android.Database.MovementDetailsDao;
import me.SyncWise.Android.Database.MovementHeaders;
import me.SyncWise.Android.Database.MovementHeadersDao;
import me.SyncWise.Android.Database.MovementHeadersUtils;
import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Database.Prices;
import me.SyncWise.Android.Database.PricesDao;
 
import me.SyncWise.Android.Database.Reasons;
import me.SyncWise.Android.Database.ReasonsDao;
import me.SyncWise.Android.Database.ReasonsUtils;
import me.SyncWise.Android.Database.StatusUtils;
import me.SyncWise.Android.Database.SuggestedUserLoad;
import me.SyncWise.Android.Database.SuggestedUserLoadDao;
import me.SyncWise.Android.Database.Taxes;
import me.SyncWise.Android.Database.TransactionDetails;
 
import me.SyncWise.Android.Database.TransactionDetailsUtils;
import me.SyncWise.Android.Database.TransactionHeaders;
 
import me.SyncWise.Android.Database.TransactionHeadersUtils;
import me.SyncWise.Android.Database.TransactionSequencesDao;
import me.SyncWise.Android.Database.Units;
import me.SyncWise.Android.Database.UserLimits;
import me.SyncWise.Android.Database.UserLimitsDao;
import me.SyncWise.Android.Database.UserPasswordsUtils;
import me.SyncWise.Android.Database.Users;
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Database.Vehicles;
import me.SyncWise.Android.Database.VehiclesStock;
import me.SyncWise.Android.Database.VehiclesStockDao;
import me.SyncWise.Android.Database.VisitsUtils;
import me.SyncWise.Android.Database.WareHouseBarcodeScanning;
import me.SyncWise.Android.Database.WareHouseBarcodesDao;
import me.SyncWise.Android.Database.WarehouseQuantities;
import me.SyncWise.Android.Gson.BaseTimerListActivity;
import me.SyncWise.Android.Modules.DataManagement.DataManagementActivity.RequestCode;
import me.SyncWise.Android.Modules.FilterDivisions.FilterDivisionsActivity;
 
import me.SyncWise.Android.Modules.Printing.PrintingActivity;
   
import me.SyncWise.Android.Modules.SalesOrder.Order;
import me.SyncWise.Android.Modules.SalesOrder.SalesOrderActivity;
import me.SyncWise.Android.Modules.SalesOrder.WarehouseQuantity;
 
import me.SyncWise.Android.Modules.Sync.SyncHelper;
import me.SyncWise.Android.Modules.Sync.SyncListener;
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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import de.greenrobot.dao.AbstractDao;

/**
 * Activity implemented to perform, view or edit a movement.
 * 
 * @author Elias- Ahmad
 * @sw.todo <b>Movement Details Activity Implementation :</b><br>
 * Simply add this class in the {@code AndroidManifest.xml} file along with the permission :<br>
 * {@code <uses-permission android:name="android.permission.VIBRATE"/>}<br>
 * AND disable activity recreation for orientation events by adding the following to the activity tag in the manifest file :<br>
 * {@code android:configChanges="orientation|screenSize"}
 * 
 */
public class MovementDetailsActivity extends BaseTimerListActivity implements SearchView.OnQueryTextListener , QuickAction.OnActionItemClickListener , OnDateSetListener , OnClickListener , OnFocusChangeListener , TextWatcher, SyncListener {
	
	/**
	 * Boolean used to indicate if the activity is displayed on the screen.
	 */
	public static boolean isDisplayed;
	
	/**
	 * Bundle key used to put/retrieve the movement settings.
	 */
	public static final String MOVEMENT_SETTINGS = MovementDetailsActivity.class.getName () + ".MOVEMENT_SETTINGS";
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #requestCode}.
	 */
	public static final String REQUEST_CODE = MovementDetailsActivity.class.getName () + ".REQUEST_CODE";
	private String movementHeaderCodes = null;
	/**
	 * Integer used to host the request code.
	 * @see #REQUEST_CODE_NEW
	 * @see #REQUEST_CODE_INFO
	 */
	protected int requestCode;
	/**
	 * Bundle key used to put/retrieve the content of {@link #isCreated}.
	 */
	private static final String IS_CREATED = MovementDetailsActivity.class.getName () + ".IS_CREATED";
	
	
	/**
	 * Boolean used to indicate if the activity has been previously created.<br>
	 * This flag is mainly used to determine if the current activity creation is the first or not (activity re-creation due to phone rotation for example).
	 */
	private boolean isCreated;
	/**
	 * List of {@link me.SyncWise.Android.AlfaLabs.Modules.SalesOrder.WarehouseQuantity WarehouseQuantity} hosting all the items' warehouses quantities.
	 */
	public ArrayList < WarehouseQuantity > warehouseQuantities;
	/**
	 * Constant integer holding the request code to create a new movement.
	 */
	public static final int REQUEST_CODE_NEW = 1;
	
	/**
	 * Constant integer holding the request code to display the movement info.<br>
	 * The moved items should be displayed.
	 */
	public static final int REQUEST_CODE_INFO = 2;
	
	/**
	 * Constant integer holding the request code to display the movement details.<br>
	 * The movement details (tax, discount, total, ...) should be displayed.
	 */
	public static final int REQUEST_CODE_DETAILS = 3;
	
	/**
	 * Constant integer holding the request code used to print orders.
	 */
	private static final int REQUEST_CODE_PRINT = 10;
	
	/**
	 * Constant integer holding the request code used to print orders.
	 */
	private static final int REQUEST_CODE_PRE_PRINT = 11;

	/**
	 * Bundle key used to put/retrieve the content of the edit flag.
	 */
	public static final String IS_EDIT = MovementDetailsActivity.class.getName () + ".IS_EDIT";
	
	/**
	 * Bundle key used to put/retrieve the content of the view only flag.
	 */
	public static final String IS_VIEW_ONLY = MovementDetailsActivity.class.getName () + ".IS_VIEW_ONLY";
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #displayMovementDetails}.
	 */
	private static final String DISPLAY_MOVEMENT_DETAILS = MovementDetailsActivity.class.getName () + ".DISPLAY_MOVEMENT_DETAILS";
	
	/**
	 * Boolean used to determine whether to display the movement details UI or not.<br>
	 * This boolean is mainly used to save the movement state.
	 */
	protected boolean displayMovementDetails;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #displayPasscode}.
	 */
	private static final String DISPLAY_PASSCODE = MovementDetailsActivity.class.getName () + ".DISPLAY_PASSCODE";
	
	/**
	 * Boolean used to determine whether to display the passcode UI or not.
	 */
	protected boolean displayPasscode;

	/**
	 * Bundle key used to put/retrieve the content of {@link #displayWarehouseKeeperApproval}.
	 */
	private static final String DISPLAY_WAREHOUSE_KEEPER_APPROVAL = MovementDetailsActivity.class.getName () + ".DISPLAY_WAREHOUSE_KEEPER_APPROVAL";
	
	/**
	 * Boolean used to determine whether to display the warehouse keeper approval UI or not.
	 */
	protected boolean displayWarehouseKeeperApproval;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #allowWarehouseBarcodeScanning}.
	 */
	private static final String ALLOW_WAREHOUSE_BARCODE_SCANNING = MovementDetailsActivity.class.getName () + ".ALLOW_WAREHOUSE_BARCODE_SCANNING";
	
	/**
	 * Boolean used to determine whether to warehouse barcode scanning is allowed or not.
	 */
	protected boolean allowWarehouseBarcodeScanning;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #warehouseKeeperStart}.
	 */
	private static final String WAREHOUSE_KEEPER_START = MovementDetailsActivity.class.getName () + ".WAREHOUSE_KEEPER_START";
	
	/**
	 * A {@link me.SyncWise.Android.Database.Users Users} object used to host the warehouse keeper for approval during the start of the movement.
	 */
	protected Users warehouseKeeperStart;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #warehouseKeeperEnd}.
	 */
	private static final String WAREHOUSE_KEEPER_END = MovementDetailsActivity.class.getName () + ".WAREHOUSE_KEEPER_END";
	
	/**
	 * A {@link me.SyncWise.Android.Database.Users Users} object used to host the warehouse keeper for approval during the end of the movement.
	 */
	protected Users warehouseKeeperEnd;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #displayVanType}.
	 */
	private static final String DISPLAY_VAN_TYPE = MovementDetailsActivity.class.getName () + ".DISPLAY_VAN_TYPE";
	
	/**
	 * Boolean used to determine whether to display the van type UI or not.
	 */
	protected boolean displayVanType;

	/**
	 * Constant integer used to host the van transfer in value.
	 */
	public static final int VAN_TYPE_IN = 1;
	
	/**
	 * Constant integer used to host the van transfer out value.
	 */
	public static final int VAN_TYPE_OUT = 2;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #vanType}.
	 */
	private static final String VAN_TYPE = MovementDetailsActivity.class.getName () + ".VAN_TYPE";
	
	/**
	 * Integer used to hold the van type transfer.
	 */
	private Integer vanType;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #displayCashVanApproval}.
	 */
	private static final String DISPLAY_CASH_VAN_APPROVAL = MovementDetailsActivity.class.getName () + ".DISPLAY_CASH_VAN_APPROVAL";
	
	/**
	 * Boolean used to determine whether to display the cash van approval UI or not.
	 */
	protected boolean displayCashVanApproval;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #cashVan}.
	 */
	private static final String CASH_VAN = MovementDetailsActivity.class.getName () + ".CASH_VAN";
	
	/**
	 * A {@link me.SyncWise.Android.Database.Users Users} object used to host the cash van user for approval during the end of the movement.
	 */
	protected Users cashVan;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #displayPrintingConfirmation}.
	 */
	private static final String PRINTING_TRANSACTION_CODE = MovementDetailsActivity.class.getName () + ".PRINTING_TRANSACTION_CODE";
	
	/**
	 * String used to host the transaction code of the transaction to print.
	 */
	private String printingTransactionCode;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #displayPrintingConfirmation}.
	 */
	private static final String DISPLAY_PRINTING_CONFIRMATION = MovementDetailsActivity.class.getName () + ".DISPLAY_PRINTING_CONFIRMATION";
	
	/**
	 * Boolean used to determine whether to display the printing confirmation.
	 */
	private boolean displayPrintingConfirmation;
	
	private static final String ERROR = MovementDetailsActivity.class.getName () + ".ERROR";
	
	private boolean error;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #movementModification}.
	 */
	private static final String MOVEMENT_MODIFICATION = MovementDetailsActivity.class.getName () + ".MOVEMENT_MODIFICATION";
	
	/**
	 * Reference to the {@link me.SyncWise.Android.Modules.Movements.Movement Movement} object being modified.
	 */
	protected Movement movementModification;
	
	/**
	 * Bundle key used to put/retrieve the content of the movement header code.<br>
	 * This is used mainly to edit a movement.
	 */
	public static final String MOVEMENT_HEADER_CODE = MovementDetailsActivity.class.getName () + ".MOVEMENT_HEADER_CODE";
	
	/**
	 * List of {@link me.SyncWise.Android.Database.Divisions Divisions} objects used to host all the available objects for the current movement.
	 */
	protected ArrayList < Divisions > divisions;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #SELECTED_DIVISIONS}.
	 */
	public static final String SELECTED_DIVISIONS = MovementDetailsActivity.class.getName () + ".SELECTED_DIVISIONS";
	
	/**
	 * List of string used to host the selected divisions codes, in movement to filter the items / movement list.
	 */
	protected ArrayList < String > selectedDivisionsCodes;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #movements}.
	 */
	public static final String MOVEMENT = MovementDetailsActivity.class.getName () + ".MOVEMENT";
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.Movements.Movement Movement} objects used to define the movements.<br>
	 * <b>NOTE :</b> <br>
	 * If a filter is applied, this list remains untouched. A new list is generated, filtered and passed to the list adapter.
	 */
	protected ArrayList < Movement > movements;
	
	/**
	 * Boolean used to indicate if there saved movements that should be retrieved.
	 */
	protected boolean retrieveMovements;
	
	/**
	 * Reference to the movements list population task.
	 */
	protected static PopulateList populateList;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #searchQuery}.
	 */
	public static final String SEARCH_QUERY = MovementDetailsActivity.class.getName () + ".SEARCH_QUERY";
	
	/**
	 * String holding the search query.
	 */
	private String searchQuery;
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.Movements.Movement Movement} objects used to define the selected movements .
	 */
	protected ArrayList < Movement > selectedMovements;
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.Movements.MovementInfo MovementInfo} objects used to define the movement details.
	 */
	protected ArrayList < MovementInfo > details;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #passcode}.
	 */
	public static final String PASSCODE = MovementDetailsActivity.class.getName () + ".PASSCODE";
	
	/**
	 * String holding the passcode.
	 */
	protected String passcode;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #expiryDate}.
	 */
	public static final String EXPIRY_DATE = MovementDetailsActivity.class.getName () + ".EXPIRY_DATE";
	
	/**
	 * Date object used to host the expiry date during a movement modification.
	 */
	private Date expiryDate;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #currency}.
	 */
	public static final String CURRENCY = MovementDetailsActivity.class.getName () + ".CURRENCY";
	
	/**
	 * Reference to the currency object.
	 */
	protected Currencies currency;
	
	/**
	 * List of the reasons.
	 */
	private ArrayList < Reasons > reasons;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #movementHeader}.
	 */
	public static final String MOVEMENT_HEADER = MovementDetailsActivity.class.getName () + ".MOVEMENT_HEADER";
	
	/**
	 * Reference to the movement header stored in DB (if previously saved)
	 */
	protected MovementHeaders movementHeader;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #movementDetails}.
	 */
	public static final String MOVEMENT_DETAILS = MovementDetailsActivity.class.getName () + ".MOVEMENT_DETAILS";
	
	/**
	 * List of movement details references stored in DB (if previously saved)
	 */
	protected ArrayList < MovementDetails > movementDetails;
	
	/**
	 * Constant integer used to host the warehouse keeper approval type value.
	 */
	private static final int APPROVAL_TYPE_WAREHOUSE = 1;
	
	/**
	 * Constant integer used to host the cash van approval type value.
	 */
	private static final int APPROVAL_TYPE_CASH_VAN = 2;
	
	/**
	 * List of {@link me.SyncWise.Android.Database.Users Users} used to host the warehouse keepers.
	 */
	private ArrayList < Users > warehouseKeepers;
	
	/**
	 * List of {@link me.SyncWise.Android.Database.Users Users} used to host the cash van users.
	 */
	private ArrayList < Users > cashVanUsers;
	
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
		
		// Retrieve the warehouse quantities
		new RetrieveWarehouseQuantities ().execute ();
		// Superclass method call
		super.onCreate ( savedInstanceState );
		// Set the activity content from a layout resource.
		setContentView ( R.layout.movement_details_activity );
		// Enable fast scrolling
		getListView ().setFastScrollEnabled ( true );
		// Define the empty list view
		getListView ().setEmptyView ( findViewById ( R.id.empty_list_view ) );
		// Display empty list label
		( (TextView) findViewById ( R.id.empty_list_view ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_items_list_label ) );
		
        // Retrieve the movement settings
        MovementSettings movementSettings = (MovementSettings) getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS );
		// Check if the movement setting is valid
		if ( getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS ) == null ) {
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
		
		// Check if the warehouse keepers list is valid
		if ( warehouseKeepers == null ) {
			// Initialize the list
			warehouseKeepers = new ArrayList < Users > ();
			// Add the default selection
			warehouseKeepers.add ( new Users ( null , null , null , null , "----------" , null , 0 , null , null , null , null , null , null , null , null , null ) );
			// Retrieve the warehouse keepers list
			warehouseKeepers.addAll ( DatabaseUtils.getInstance ( MovementDetailsActivity.this ).getDaoSession ().getUsersDao ().queryBuilder ()
					.where ( UsersDao.Properties.UserType.eq ( 6 ) , UsersDao.Properties.UserStatus.eq ( StatusUtils.isActive () ) ).list () );
		} // End if
		
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state 
			searchQuery = savedInstanceState.getString ( SEARCH_QUERY );
			selectedDivisionsCodes = savedInstanceState.getStringArrayList ( SELECTED_DIVISIONS );
			retrieveMovements = savedInstanceState.getBoolean ( MOVEMENT );
			isCreated = savedInstanceState.getBoolean ( IS_CREATED , isCreated );
			requestCode = savedInstanceState.getInt ( REQUEST_CODE );
			passcode = savedInstanceState.getString ( PASSCODE );
			expiryDate = (Date) savedInstanceState.getSerializable ( EXPIRY_DATE );
			currency = (Currencies) savedInstanceState.getSerializable ( CURRENCY );
			movementHeader = (MovementHeaders) savedInstanceState.getSerializable ( MOVEMENT_HEADER );
			movementDetails = (ArrayList < MovementDetails >) savedInstanceState.getSerializable ( MOVEMENT_DETAILS );
			displayMovementDetails = savedInstanceState.getBoolean ( DISPLAY_MOVEMENT_DETAILS );
			displayPasscode = savedInstanceState.getBoolean ( DISPLAY_PASSCODE );
			displayWarehouseKeeperApproval = savedInstanceState.getBoolean ( DISPLAY_WAREHOUSE_KEEPER_APPROVAL );
			displayCashVanApproval = savedInstanceState.getBoolean ( DISPLAY_CASH_VAN_APPROVAL );
			allowWarehouseBarcodeScanning = savedInstanceState.getBoolean ( ALLOW_WAREHOUSE_BARCODE_SCANNING );
			displayVanType = savedInstanceState.getBoolean ( DISPLAY_VAN_TYPE );
			displayPrintingConfirmation = savedInstanceState.getBoolean ( DISPLAY_PRINTING_CONFIRMATION );
			printingTransactionCode = savedInstanceState.getString ( PRINTING_TRANSACTION_CODE );
			int temp = savedInstanceState.getInt ( VAN_TYPE , -1 );
			vanType = temp < 0 ? null : temp;
			warehouseKeeperStart = (Users) savedInstanceState.getSerializable ( WAREHOUSE_KEEPER_START );
			warehouseKeeperEnd = (Users) savedInstanceState.getSerializable ( WAREHOUSE_KEEPER_END );
			movementModification = (Movement) savedInstanceState.getSerializable ( MOVEMENT_MODIFICATION );
			cashVan = (Users) savedInstanceState.getSerializable ( CASH_VAN );
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
		
		// Hide the movement detail layout
		findViewById ( R.id.layout_movement_details ).setVisibility ( View.GONE );
		// Hide the passcode layout
		findViewById ( R.id.layout_passcode ).setVisibility ( View.GONE );
		// Hide the warehouse keeper approval layout
		findViewById ( R.id.layout_warehouse_keeper_approval ).setVisibility ( View.GONE );
		// Assign a text changed listener to the fields in order to display/hide the clear button accordingly
		( (EditText) findViewById ( R.id.edittext_password ) ).addTextChangedListener ( this );
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
				AppDialog.getInstance ().displayDatePicker ( MovementDetailsActivity.this , defaultDate.get ( Calendar.YEAR ) , defaultDate.get ( Calendar.MONTH ) , defaultDate.get ( Calendar.DAY_OF_MONTH ) , null , false , true , MovementDetailsActivity.this , MovementDetailsActivity.this );
			}
		} );
		
		// Make sure the movement type is not van transfer
		if ( movementSettings.getMovementType () != MovementHeadersUtils.Type.VAN_TRANSFER )
			// Retrieve all the movement counts asynchronously
			populate ();
		// Otherwise populate is the view mode is enabled
		else if ( getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) )
			// Retrieve all the movement counts asynchronously
			populate ();
		
	if( movementSettings.getMovementType () == MovementHeadersUtils.Type.Physical_Direct_Load 
	    || movementSettings.getMovementType () == MovementHeadersUtils.Type.PHYSICAL_DIRECT_UNLOAD
	    || movementSettings.getMovementType () == MovementHeadersUtils.Type.LOAD_REQUEST )
	{		
		String userCode = DatabaseUtils.getCurrentUserCode ( this );
		// Retrieve the company code
		String companyCode = DatabaseUtils.getCurrentCompanyCode ( this );
		// Determine if this is the first activity creation or a re-creation
				// AND if the view only flag is NOT set
				// AND if the network is available
				if ( ! isCreated
						&& ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false )
						&& Network.networkAvailability ( this ) ) {
					// Set the flag
					isCreated = true;
			        // Search for the GPRS link
			        ConnectionSettings connection = DatabaseUtils.getInstance ( this ).getDaoSession ().getConnectionSettingsDao ().queryBuilder ()
			        		.where ( ConnectionSettingsDao.Properties.ConnectionSettingType.eq ( ConnectionSettingsUtils.Type.GPRS ) ).unique ();
			        // Check if the connection is valid
			        if ( connection != null ) {
			        	// Valid connection
						// Retrieve the dao table data
						SyncHelper syncHelper = new SyncHelper ( ConnectionSettingsUtils.getGetMethodPath ( connection.getConnectionSettingURL () ) );
						syncHelper.get ( this , DatabaseUtils.getInstance ( this ).getDaoSession ().getWarehouseQuantitiesDao () , // WarehouseQuantities table
								new TypeToken < List < WarehouseQuantities > > () {}.getType () , // Expected Type
								userCode , // UserCode
								companyCode , // CompanyCode
								this , // SyncListener
								RequestCode.ON_GOING , // Sync Request Code`
								true ); // Execute in parallel
			        } // End if
				}
				} // End if
	}
	
	/**
	 * Retrieve all the movements asynchronously.
	 */
	protected void populate () {
		// Retrieve all the movements asynchronously
		populateList = new PopulateList ();
		populateList.execute ();
	}
	@Override
	public void onPreFinish(int requestCode) {
		// TODO Auto-generated method stub
		
	}
	@SuppressWarnings("unchecked")
	@Override
	public void onGetSuccess(AbstractDao<?, ?> dao, ArrayList<Object> entities,
			int requestCode) {
		// Process the retrieved data asynchronously
 	Object object = entities;
 	new SyncWarehouseQuantities ( (List < WarehouseQuantities >) object ).execute ();
		
	}

	@Override
	public void onSetSuccess(ArrayList<ArrayList<Object>> entites,
			int requestCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetFailure(AbstractDao<?, ?> dao, int requestCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSetFailure(int requestCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPostFinish(int requestCode) {
		// TODO Auto-generated method stub
		
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
		
		// Check if this is the view mode
		if ( getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) )
			// Do nothing
			return;
		
        // Retrieve the movement settings
        MovementSettings movementSettings = (MovementSettings) getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS );
    	// Check if the movement type is van type 
    	if ( movementSettings.getMovementType () == MovementHeadersUtils.Type.VAN_TRANSFER && vanType == null ) {
    		// Set flag
    		displayVanType = true;
    		// Prompt user
    		displayVanType ();
    	} // End if
    	// Check if the movement type is load 
   	if ( movementSettings.getMovementType () == MovementHeadersUtils.Type.LOAD && warehouseKeeperStart == null ) {
    		// Set flag
   		Users user = DatabaseUtils.getInstance ( this ).getDaoSession ().getUsersDao ().queryBuilder ()
				.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( this ) ) ,
						UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( this ) ) ).unique ();
   		if( !PermissionsUtils.getRemoveWareHouseKeeper ( MovementDetailsActivity.this , user.getUserCode () , user.getCompanyCode () ))
        	{
    		displayWarehouseKeeperApproval = true;
    		allowWarehouseBarcodeScanning = true;
    		// Initialize the quaternary view
    		initializeQuaternaryView ( APPROVAL_TYPE_WAREHOUSE , true );
    		// Disable the main list
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( false );
    		// Retrieve a reference to the quaternary view
    		View quaternaryView = findViewById ( R.id.layout_warehouse_keeper_approval );
    		// Display the quaternary view
    		quaternaryView.setVisibility ( View.VISIBLE );
    		// Animate the quaternary view
    		quaternaryView.startAnimation ( getViewAnimationIn() );
           } // End if
        }
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
    	// Save the content of isCreated in the outState bundle
    	outState.putBoolean ( IS_CREATED , isCreated );
		// Save the content of the movements using GSON
		ActivityInstance.saveDataGson ( this , MovementDetailsActivity.class.getName () , MOVEMENT , movements );
		// Indicate that there is saved movements data
		outState.putBoolean ( MOVEMENT , true );
    	
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
    	// Save the content of movementHeader in the outState bundle
    	outState.putSerializable ( MOVEMENT_HEADER , movementHeader );
    	// Save the content of movementDetails in the outState bundle
    	outState.putSerializable ( MOVEMENT_DETAILS , movementDetails );
    	// Save the content of displayMovementDetails in the outState bundle
    	outState.putBoolean ( DISPLAY_MOVEMENT_DETAILS , displayMovementDetails );
    	// Save the content of displayPasscode in the outState bundle
    	outState.putBoolean ( DISPLAY_PASSCODE , displayPasscode );
    	// Save the content of displayWarehouseKeeperApproval in the outState bundle
    	outState.putBoolean ( DISPLAY_WAREHOUSE_KEEPER_APPROVAL , displayWarehouseKeeperApproval );
    	// Save the content of displayCashVanApproval in the outState bundle
    	outState.putBoolean ( DISPLAY_CASH_VAN_APPROVAL , displayCashVanApproval );
    	// Save the content of allowWarehouseBarcodeScanning in the outState bundle
    	outState.putBoolean ( ALLOW_WAREHOUSE_BARCODE_SCANNING , allowWarehouseBarcodeScanning );
    	// Check if the warehouseKeeperStart is valid
    	if ( warehouseKeeperStart != null )
	    	// Save the content of warehouseKeeper in the outState bundle
	    	outState.putSerializable ( WAREHOUSE_KEEPER_START , warehouseKeeperStart );
    	// Check if the warehouseKeeperEnd is valid
    	if ( warehouseKeeperEnd != null )
	    	// Save the content of warehouseKeeper in the outState bundle
	    	outState.putSerializable ( WAREHOUSE_KEEPER_END , warehouseKeeperEnd );
    	// Save the content of displayVanType in the outState bundle
    	outState.putBoolean ( DISPLAY_VAN_TYPE , displayVanType );
    	// Check if the vanType is valid
    	if ( vanType != null )
	    	// Save the content of vanType in the outState bundle
	    	outState.putSerializable ( VAN_TYPE , vanType );
    	// Check if the cashVan is valid
    	if ( cashVan != null )
	    	// Save the content of cashVan in the outState bundle
	    	outState.putSerializable ( CASH_VAN , cashVan );
    	// Save the content of movementModification in the outState bundle
    	outState.putSerializable ( MOVEMENT_MODIFICATION , movementModification );
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
		// Determine if a movement is undergoing any modifications
		if ( displayMovementDetails ) {
			// Reset flag
			displayMovementDetails = false;
			// Retrieve a reference to the secondary view
			View secondary = findViewById ( R.id.layout_movement_details );
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
		// Determine if the passcode is undergoing any modifications
		else if ( displayWarehouseKeeperApproval ) {
			// Check if the movement is load
			if ( ( (MovementSettings) getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS ) ).getMovementType () == MovementHeadersUtils.Type.LOAD && warehouseKeeperStart == null ) {
				// Exit activity
				finish ();
	    		// Exit method
	    		return;
			} // End if
			// Reset flag
			displayWarehouseKeeperApproval = false;
			// Retrieve a reference to the quaternary view
			View quaternary = findViewById ( R.id.layout_warehouse_keeper_approval );
			// Hide the tertiary view
			quaternary.startAnimation ( getViewAnimationOut ( quaternary ) );
    		// Enable the main list
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( true );
    		// Refresh the action bar
    		invalidateOptionsMenu ();
    		// Exit method
    		return;
		} // End else if
		// Determine if the passcode is undergoing any modifications
		else if ( displayCashVanApproval ) {
			// Reset flag
			displayCashVanApproval = false;
			// Retrieve a reference to the quaternary view
			View quaternary = findViewById ( R.id.layout_warehouse_keeper_approval );
			// Hide the tertiary view
			quaternary.startAnimation ( getViewAnimationOut ( quaternary ) );
    		// Enable the main list
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( true );
    		// Refresh the action bar
    		invalidateOptionsMenu ();
    		// Exit method
    		return;
		} // End else if
		// Determine if the request code is INFO
		else if ( requestCode == REQUEST_CODE_INFO ) {
			if(autoUnload == true)
			{
				for ( Movement movement : movements ) {
	     		    
	     		 
	     			 {  
	     				movement.setExpiryDate(null);  
	     	            movement.setReason(  null);
	 		        	movement.setQuantityMedium(0);
	 		        	movement.setQuantitySmall( 0);
	     		   	}
	     		  }
			    autoUnload = false;
			}
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
				setListAdapter ( getMovementDetailsAdapter ( R.layout.sales_order_details_activity_item , new ArrayList < Movement > () , currency.getCurrencyRounding () , true ) );
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
								MovementDetailsActivity.this.finish ();
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
			movementModification = null;
			isCreated = false;
			searchQuery = null;
			movements = null;
			passcode = null;
			selectedMovements = null;
			details = null;
			movementHeader = null;
			movementDetails = null;
			currency = null;
			printingTransactionCode = null;
			warehouseKeeperStart = null;
			warehouseKeeperEnd = null;
			vanType = null;
			warehouseKeepers = null;
			cashVanUsers = null;
			cashVan = null;
			expiryDate = null;
			quickAction = null;
			if ( findViewById ( R.id.edittext_password ) != null ) 
				( (EditText) findViewById ( R.id.edittext_password ) ).removeTextChangedListener ( this );
		} // End if
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
		// Retrieve the movement settings
		MovementSettings movementSetting = (MovementSettings) getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS );
		// Determine the printing type
		int type = -1;
		if ( movementSetting.getMovementType () == MovementHeadersUtils.Type.LOAD )
			type = PrintingActivity.Type.LOAD;
		else if ( movementSetting.getMovementType () == MovementHeadersUtils.Type.Physical_Direct_Load )
			type = PrintingActivity.Type.DirectLoad;
		else if ( movementSetting.getMovementType () == MovementHeadersUtils.Type.PHYSICAL_DIRECT_UNLOAD )
			type = PrintingActivity.Type.DirectUnload;
		else if ( movementSetting.getMovementType () == MovementHeadersUtils.Type.LOAD_REQUEST )
			type = PrintingActivity.Type.MOVEMENTLoadRequest;
		else if ( movementSetting.getMovementType () == MovementHeadersUtils.Type.UNLOAD )
			type = PrintingActivity.Type.UNLOAD;
		else if ( movementSetting.getMovementType () == MovementHeadersUtils.Type.UNLOAD_REQUEST )
			type = PrintingActivity.Type.UNLOADREQUEST;
		else if ( movementSetting.getMovementType () == MovementHeadersUtils.Type.STOCK_MANAGEMENT )
			type = PrintingActivity.Type.VANMANAGMENT;
		else if ( movementSetting.getMovementType () == MovementHeadersUtils.Type.VAN_STOCK_COUNT )
			type = PrintingActivity.Type.VANSTOCKCOUNT;
		else if ( movementSetting.getMovementType () == MovementHeadersUtils.Type.STOCK_RECONCILIATION )
			type = PrintingActivity.Type.STOCKRECONCILIATION;
		else if ( movementSetting.getMovementType () == MovementHeadersUtils.Type.VAN_TRANSFER )
			type = PrintingActivity.Type.VANTRANSFER;
		// Check if the type is valid
		if ( type < 0 )
			// Invalid type
			return;
		// Get a final reference
		final int printingType = type;
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
							Intent intent = new Intent ( MovementDetailsActivity.this, PrintingActivity.class );
							intent.putExtra ( PrintingActivity.TYPE , printingType );
							intent.putExtra ( PrintingActivity.PRINTOUT , PrintingActivity.Printout.ORIGINAL );
							intent.putExtra ( PrintingActivity.HEADER , new ArrayList < MovementHeaders > ( DatabaseUtils.getInstance ( MovementDetailsActivity.this ).getDaoSession ().getMovementHeadersDao ().queryBuilder ()
									.where ( MovementHeadersDao.Properties.MovementCode.eq ( transactionCode ) ).list () ) );
							startActivityForResult ( intent , REQUEST_CODE_PRINT );
							// Reset flag
							displayPrintingConfirmation = false;
							break;
						case DialogInterface.BUTTON_NEGATIVE:
					    	// Call this to set the result that your activity will return to its caller
							intent = new Intent();
							intent.putExtra ( MovementActivity.SAVE_SUCCESS , ! error );
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
		// Check if there is any ongoing movements modifications
		if ( displayMovementDetails || displayPasscode || displayWarehouseKeeperApproval || displayVanType || displayCashVanApproval )
			// Do nothing
			return;
		
		// Retrieve the selected movement
		movementModification = (Movement) ( (BaseAdapter) getListAdapter () ).getItem ( position );

    	// Determine if the request code is NOT info
    	if ( requestCode != REQUEST_CODE_INFO ) {
    		// Set flag
    		displayMovementDetails = true;
    		// Initialize the secondary view
    		initializeSecondaryView ( false );
    		// Disable the main list
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( false );
    		// Retrieve a reference to the secondary view
    		View secondaryView = findViewById ( R.id.layout_movement_details );
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
    			&& ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) && autoUnload==false)
    		// Display the quick action widget
    		quickAction.show ( view , movementModification , getResources () );
     
    	 
	}
	private Boolean autoUnload=false;
	/*
	 * Callback method to be invoked when an action item in this QuickAction has been clicked.
	 *
	 * @see me.SyncWise.Android.Widgets.QuickAction.QuickAction.OnActionItemClickListener#onItemClick(me.SyncWise.Android.Widgets.QuickAction.QuickAction, int, int)
	 */
	@Override
	public void onItemClick ( QuickAction source , View anchor , Object object , int pos , int actionId ) {
		
		
		// Retrieve the selected movement
		Movement selectedMovement = (Movement) object;
		
		// Determine the clicked action item
		switch ( actionId ) {
		case ActionItemID.EDIT:
			// Retrieve the selected movement
			movementModification = selectedMovement;
    		// Set flag
			displayMovementDetails = true;
    		// Initialize the secondary view
    		initializeSecondaryView ( false );
    		// Disable the main list
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( false );
    		// Retrieve a reference to the secondary view
    		View secondaryView = findViewById ( R.id.layout_movement_details );
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
			selectedMovement.setQuantityBig ( 0 );
			selectedMovement.setQuantityMedium ( 0 );
			selectedMovement.setQuantitySmall ( 0 );
			selectedMovement.setExpiryDate ( null );
			selectedMovement.setReason ( null );
			// Remove item from the selected movements (if valid)
			if ( selectedMovements != null )
				selectedMovements.remove ( selectedMovement );
			// Refresh movement details
			refreshDetails ();
			// Check if the request code is INFO
			// AND if the selected movements list is empty
			if ( requestCode == REQUEST_CODE_INFO && selectedMovements != null && selectedMovements.isEmpty () )
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
	 * Initializes the movement modification (secondary) view.
	 * 
	 * @param restore	Boolean used to determine if the secondary view is being initialized for the first time or is being restored.
	 */
	protected void initializeSecondaryView ( final boolean restore ) {
		// Retrieve a reference to the parent view
		View parent = findViewById ( R.id.layout_movement_details );
		// Retrieve a reference to the movement quantity layout
		View quantityLayout = parent.findViewById ( R.id.layout_movement_quantity );
		// Retrieve a reference to the movement data layout
		View dataLayout = parent.findViewById ( R.id.layout_movement_data );
		// Retrieve a reference to the movement data layout
		View expiryDateLayout = dataLayout.findViewById ( R.id.layout_date_picker );
		// Retrieve a reference to the movement data layout
		View reasonLayout = dataLayout.findViewById ( R.id.layout_reason );
		// Retrieve a reference to the item name label
		TextView itemNameLabel = (TextView) parent.findViewById ( R.id.label_item_name );
		// Retrieve a reference to the save icon
		ImageView saveIcon = (ImageView) parent.findViewById ( R.id.icon_save_movement );
		// Retrieve a reference to the movement quantity label
		TextView quantityLabel = (TextView) parent.findViewById ( R.id.label_movement_quantity );
		// Retrieve a reference to the movement data label
		TextView dataLabel = (TextView) parent.findViewById ( R.id.label_movement_data );
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
		// Retrieve a reference to the movement settings
		MovementSettings movementSettings = (MovementSettings) getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS );
		
		// Display the item name
		itemNameLabel.setText ( movementModification.getItem ().getItemName () );
		// Enable the save button
		saveIcon.setEnabled ( true );
		// Display the quantity label
		quantityLabel.setText ( AppResources.getInstance ( this ).getString ( this , R.string.quantity_label ) );
		// Assign on focus listeners to the number pickers
		quantityBig.getEditText ().setOnFocusChangeListener ( this );
		quantityMedium.getEditText ().setOnFocusChangeListener ( this );
		quantitySmall.getEditText ().setOnFocusChangeListener ( this );
		// Display the data label
		dataLabel.setText ( AppResources.getInstance ( this ).getString ( this , R.string.movement_label ) );
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
		quantityBigLabel_quantity.setText ( movementModification.getUnit ().getUnitBigDescription () );
		quantityMediumLabel_quantity.setText ( movementModification.getUnit ().getUnitMediumDescription () );
		quantitySmallLabel_quantity.setText ( movementModification.getUnit ().getUnitSmallDescription () );
		// Dim the appropriate unit of measurements labels
		quantityBigLayout.setAlpha ( ItemsUtils.isBig ( movementModification.getItem () ) ? 1f : 0.25f );
		quantityMediumLayout.setAlpha ( ItemsUtils.isMedium ( movementModification.getItem () ) ? 1f : 0.25f );
		
		// Display the expiry date layout accordingly
		expiryDateLayout.setVisibility ( movementSettings.isUseExpiryDate () ? View.VISIBLE : View.GONE );
		// Display the reason layout accordingly
		reasonLayout.setVisibility ( movementSettings.isUseReason () ? View.VISIBLE : View.GONE );
		// Display the data layout accordingly
		dataLayout.setVisibility ( movementSettings.isUseReason () || movementSettings.isUseExpiryDate () ? View.VISIBLE : View.GONE );
		
		// Check if the view is being restored
		if ( ! restore ) {
			// Set the current movement quantities
			quantityBig.setNumber ( movementModification.getQuantityBig () );
			quantityMedium.setNumber ( movementModification.getQuantityMedium () );
			quantitySmall.setNumber ( movementModification.getQuantitySmall () );
			// Disable the appropriate number pickers
			quantityBig.setEnabled ( ItemsUtils.isBig ( movementModification.getItem () ) );
			quantityMedium.setEnabled ( ItemsUtils.isMedium ( movementModification.getItem () ) );
			// Set expiry date
			expiryDate = movementModification.getExpiryDate ();
			// Track the selected index
			int index = 0;
			// Retrieve a reference to the reason spinner
			Spinner spinner = (Spinner) reasonLayout.findViewById ( R.id.spinner_reason );
			// Select the appropriate reason
			if ( movementModification.getReason () != null ) {
				// Iterate over all reasons
				for ( int i = 1 ; i < spinner.getAdapter ().getCount () ; i ++ )
					// Match reason codes
					if ( ( (Reasons) spinner.getAdapter ().getItem ( i ) ).getReasonCode ().equals ( movementModification.getReason ().getReasonCode () ) ) {
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
		priceBigLabel_quantity.setText ( moneyFormat.format ( movementModification.getPriceBig () ) );
		priceMediumLabel_quantity.setText ( moneyFormat.format ( movementModification.getPriceMedium () ) );
		priceSmallLabel_quantity.setText ( moneyFormat.format ( movementModification.getPriceSmall () ) );
	}
	
	/**
	 * Initializes the passcode (tertiary) view.
	 * 
	 * @param message	String hosting the message to display for the user regarding the passcode.
	 */
	protected void initializeTertiaryView ( String message ) {
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
		
		// Check if a message is provided
		if ( message != null ) {
			// Display the passcode title label
			messageLabel.setText ( message );
			// Clear any previous entries
			passcodeEditText.setText ( "" );
		} // End if
		// Enable the save icon
		parent.findViewById ( R.id.icon_save_passcode ).setEnabled ( true );
		// Enable the edit text
		passcodeEditText.setEnabled ( true );
		// Display the field hint
		passcodeEditText.setHint ( AppResources.getInstance ( this ).getString ( this , R.string.movement_passcode_hint ) );
	}
	
	/**
	 * Initializes the user approval (quaternary) view.
	 * 
	 * @param approvalType	Integer used to determine the approval type.
	 */
	protected void initializeQuaternaryView ( final int approvalType , boolean allowWarehouseBarcodeScanning ) {
		// Retrieve the movement settings
		MovementSettings movementSetting = (MovementSettings) getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS );
		// Retrieve a reference to the parent view
		View parent = findViewById ( R.id.layout_warehouse_keeper_approval );
		// Retrieve a reference to the users spinner
		final Spinner usersSpinner = (Spinner) parent.findViewById ( R.id.spinner_users );
		// Retrieve a reference to the types spinner
		Spinner typeSpinner = (Spinner) parent.findViewById ( R.id.spinner_type );
		// Retrieve a reference to the password edit text
		final EditText passwordEditText = (EditText) parent.findViewById ( R.id.edittext_password );
		// Display the warehouse keeper approval title and button labels
		String approvalTitle = "" , buttonLabel = "";
    	switch ( approvalType ) {
		case APPROVAL_TYPE_CASH_VAN:
			approvalTitle = AppResources.getInstance ( this ).getString ( this , R.string.movement_cash_van_approval );
			buttonLabel = AppResources.getInstance ( this ).getString ( this , R.string.movement_cash_van_approve_label );
			break;
		case APPROVAL_TYPE_WAREHOUSE:
			approvalTitle = AppResources.getInstance ( this ).getString ( this , R.string.movement_warehouse_keeper_approval );
			if ( allowWarehouseBarcodeScanning )
				buttonLabel = AppResources.getInstance ( this ).getString ( this , R.string.movement_warehouse_keeper_approve_label );
			else if ( movementSetting.getMovementType () == MovementHeadersUtils.Type.LOAD )
				buttonLabel = AppResources.getInstance ( this ).getString ( this , R.string.movement_warehouse_keeper_load_approve_label );
			else if ( movementSetting.getMovementType () == MovementHeadersUtils.Type.UNLOAD )
				buttonLabel = AppResources.getInstance ( this ).getString ( this , R.string.movement_warehouse_keeper_unload_approve_label );
			else
				buttonLabel = AppResources.getInstance ( this ).getString ( this , R.string.movement_warehouse_keeper_movement_approve_label );
			break;
		} // End switch
		( (TextView) findViewById ( R.id.label_firm_license ) ).setText ( approvalTitle ); 
		( (TextView) findViewById ( R.id.label_button_login ) ).setText ( buttonLabel );
		
    	// Declare and initialize a new users adapter used to populate the users spinner
    	UserAdapter userAdapter = null;
    	switch ( approvalType ) {
		case APPROVAL_TYPE_CASH_VAN:
			userAdapter = new UserAdapter ( this , android.R.layout.simple_spinner_item , cashVanUsers );
			break;
		case APPROVAL_TYPE_WAREHOUSE:
			userAdapter = new UserAdapter ( this , android.R.layout.simple_spinner_item , warehouseKeepers );
			break;
		} // End switch
		// Sets the layout resource to create the drop down views
    	userAdapter.setDropDownViewResource ( R.layout.data_management_activity_connection_item );
    	// Set the spinner adapter
    	usersSpinner.setAdapter ( userAdapter );
    	// Determine if the barcode scanning is allowed
    	if ( allowWarehouseBarcodeScanning ) {
    		// Declare, initialize and populate the approval types list
    		List < ApprovalType > approvalTypes = new ArrayList < ApprovalType > ();
    		approvalTypes.add ( new ApprovalType ( ApprovalType.TYPE_BARCODE , AppResources.getInstance ( this ).getString ( this , R.string.movement_approval_barcode_label ) ) );
    		approvalTypes.add ( new ApprovalType ( ApprovalType.TYPE_USER , AppResources.getInstance ( this ).getString ( this , R.string.movement_approval_password_label ) ) );
    		// Display the types spinner
    		typeSpinner.setVisibility ( View.VISIBLE );
        	// Declare and initialize a new types adapter used to populate the types spinner
        	ApprovalTypeAdapter approvalTypeAdapter = new ApprovalTypeAdapter ( this , android.R.layout.simple_spinner_item , approvalTypes );
    		// Sets the layout resource to create the drop down views
        	approvalTypeAdapter.setDropDownViewResource ( R.layout.data_management_activity_connection_item );
        	// Set the spinner adapter
        	typeSpinner.setAdapter ( approvalTypeAdapter );
        	// Register a callback to be invoked when a type is selected
        	typeSpinner.setOnItemSelectedListener ( new OnItemSelectedListener () {
				@Override
				public void onItemSelected ( AdapterView < ? > parent , View view , int position , long id ) {
					// Retrieve the type
					ApprovalType type = (ApprovalType) parent.getItemAtPosition ( position );
					// Determine the selected type
					switch ( type.getType () ) {
					case ApprovalType.TYPE_BARCODE:
						// Hide users spinner
						usersSpinner.setVisibility ( View.GONE );
						// Reset field
						passwordEditText.setText ( "" );
			    		// Display the field hint
			    		passwordEditText.setHint ( AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this , R.string.barcode_hint ) );
						// Clear error messages 
						passwordEditText.setError ( "" );
						break;
					case ApprovalType.TYPE_USER:
						// Display users spinner
						usersSpinner.setVisibility ( View.VISIBLE );
						// Reset field
						passwordEditText.setText ( "" );
			    		// Display the field hint
			    		passwordEditText.setHint ( AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this , R.string.password_hint ) );
						// Clear error messages 
						passwordEditText.setError ( "" );
						break;
					} // End switch
				}
				@Override
				public void onNothingSelected ( AdapterView < ? > parent ) {
					// Do nothing
				}
			} );
    		// Display the field hint
    		passwordEditText.setHint ( AppResources.getInstance ( this ).getString ( this , R.string.barcode_hint ) );
    	} // End if
    	else {
			// Display users spinner
			usersSpinner.setVisibility ( View.VISIBLE );
    		// Hide the types spinner
    		typeSpinner.setVisibility ( View.GONE );
    		// Display the field hint
    		passwordEditText.setHint ( AppResources.getInstance ( this ).getString ( this , R.string.password_hint ) );
    	} // End else
		
		// Enable the save icon
		parent.findViewById ( R.id.layout_button_login ).setEnabled ( true );
	}
	
	/**
	 * Called when a view has been clicked.<br>
	 * If the view id corresponds to the username or password clear buttons, the corresponding edit text is cleared.
	 * 
	 * @param view	The clicked view.
	 */
	public void clearText ( View view ) {
		// Determine if the provided view is valid
		if ( view == null )
			// Invalid view
			return;
		// Check the view id
		if ( view.getId () == R.id.button_clear_password ) {
			// Retrieve a reference to the password edit text
			EditText passwordEditText = (EditText) findViewById ( R.id.edittext_password );
			// Clear the password field
			passwordEditText.setText ( "" );
			// Clear any error messages
			passwordEditText.setError ( "" );
		} // End if
	}
	
	/**
	 * Prompts the user for the van type transfer.
	 */
	protected void displayVanType () {
		// Display list dialog
		AppDialog.getInstance ().displayList ( this ,
				AppResources.getInstance ( this ).getString ( this , R.string.movement_van_type_dialog_title ) ,
				new String [] { AppResources.getInstance ( this ).getString ( this , R.string.movement_type_van_transfer_in_label ) ,
								AppResources.getInstance ( this ).getString ( this , R.string.movement_type_van_transfer_out_label ) } ,
				AppDialog.Cancelable.BACK_ONLY ,
				new DialogInterface.OnClickListener () {
					@Override
					public void onClick ( DialogInterface dialog , int which ) {
						// Determine the click item
						switch ( which ) {
						case 0:
							vanType = VAN_TYPE_IN;
							break;
						case 1:
							vanType = VAN_TYPE_OUT;
							break;
						} // End switch
						// Reset flag
						displayVanType = false;
						// Retrieve all the movement counts asynchronously
						populate ();
					}
				} ,
				new DialogInterface.OnCancelListener () {
					@Override
					public void onCancel ( DialogInterface dialog ) {
						// Exit activity
						finish ();
					}
				} );
	}
	
	/**
	 * Called when a view has been clicked.<br>
	 * The movement modifications are saved.
	 * 
	 * @param view	The clicked view.
	 */
	public void updateMovement ( View view ) {
		// Determine if a movement is undergoing any modifications
		if ( ! displayMovementDetails )
			// No modifications
			return;
		
		// Retrieve the movement settings
		MovementSettings movementSettings = (MovementSettings) getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS );
		// Retrieve a reference to the secondary view
		View secondary = findViewById ( R.id.layout_movement_details );
		// Retrieve a reference to the movement quantity layout
		View quantityLayout = secondary.findViewById ( R.id.layout_movement_quantity );
		// Retrieve references to the quantity number pickers
		NumberPicker quantityBig = (NumberPicker) quantityLayout.findViewById ( R.id.number_picker_quantity_big );
		NumberPicker quantityMedium = (NumberPicker) quantityLayout.findViewById ( R.id.number_picker_quantity_medium );
		NumberPicker quantitySmall = (NumberPicker) quantityLayout.findViewById ( R.id.number_picker_quantity_small );
		// Retrieve references to the reason spinner
		Spinner reasonSpinner = (Spinner) secondary.findViewById ( R.id.spinner_reason );
		// Retrieve the reason
		Reasons reason = (Reasons) reasonSpinner.getSelectedItem ();
		
		// Check if any of the movement quantities / prices are not valid
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
		
		// Make sure the current movement has valid quantities
		if ( quantityBig.getNumber () != 0 || quantityMedium.getNumber () != 0 || quantitySmall.getNumber () != 0 ) {
							
			// Check if the movement type is load or unload
			if ( movementSettings.getMovementType () == MovementHeadersUtils.Type.LOAD || 
				 movementSettings.getMovementType () == MovementHeadersUtils.Type.UNLOAD) {
				// Locate the movement detail
				MovementDetails movementDetail = null;
				for ( MovementDetails detail : movementDetails )
					if ( detail.getItemCode ().equals ( movementModification.getItem ().getItemCode () ) ) {
						movementDetail = detail;
						break;
					}
				// Determine if the item expiry of the detail is null
				boolean isExpiryDetailNull = movementDetail.getItemExpiryDate () == null;
				// Determine if the item expiry of the movement is null
				boolean isExpiryMovementNull = expiryDate == null;
				// Determine if the reason of the detail is null
				boolean isReasonDetailNull = movementDetail.getReasonCode () == null;
				// Determine if the reason of the movement is null
				boolean isReasonMovementNull = reason == null || reason.getReasonCode () == null;
				// Check for inconsistencies
				if ( isExpiryDetailNull != isExpiryMovementNull 
						|| ( movementDetail.getItemExpiryDate () != null && ! movementDetail.getItemExpiryDate ().equals ( expiryDate ) ) ) {
					// Indicate that the expiry date is inconsistent
					Baguette.showText ( MovementDetailsActivity.this ,
							AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this , R.string.movement_expiry_date_inconsistency_message ) ,
							Baguette.BackgroundColor.RED );
					// Exit method
					return;
				} // End if
				if ( isReasonDetailNull != isReasonMovementNull 
						|| ( ! isReasonDetailNull && ! isReasonMovementNull && ! movementDetail.getReasonCode ().equals ( reason.getReasonCode () ) )  ) {
					// Indicate that the reason is inconsistent
					Baguette.showText ( MovementDetailsActivity.this ,
							AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this , R.string.movement_reason_inconsistency_message ) ,
							Baguette.BackgroundColor.RED );
					// Exit method
					return;
				} // End if	
			} // End if
			
			
			
		
			
			// Check if the expiry date is mandatory
			if ( movementSettings.isUseExpiryDate () && movementSettings.isEnforceExpiryDate () && expiryDate == null ) {
				// Indicate that the expiry date is missing
				Baguette.showText ( MovementDetailsActivity.this ,
						AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this , R.string.movement_expiry_date_inconsistency_message ) ,
						Baguette.BackgroundColor.RED );
				// Exit method
				return;
			} // End if
			// Check if the reason is mandatory
			if ( movementSettings.isUseReason () && movementSettings.isEnforceReason () && ( reason == null || reason.getReasonCode () == null ) ) {
				// Indicate that the reason is missing
				Baguette.showText ( MovementDetailsActivity.this ,
						AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this , R.string.movement_invalid_reason_message ) ,
						Baguette.BackgroundColor.RED );
				// Exit method
				return;
			} // End if
			// Check if the suggestions are enforced as limits
			if ( ( movementSettings.isEnforceSuggestionLimit () || ( movementSettings.getMovementType () == MovementHeadersUtils.Type.VAN_TRANSFER && vanType == VAN_TYPE_OUT ) ))
					{
				double  minimumQuantity =movementModification.getItem().getUnitMediumSmall();
				double totalSuggestedQtty;
				
				 totalSuggestedQtty =     movementModification.getSuggestedMedium () * minimumQuantity
						  + movementModification.getSuggestedSmall ();
				double totalEnteredQtty = quantityMedium.getNumber () * minimumQuantity+quantitySmall.getNumber ();
//					&& ( quantityBig.getNumber () > movementModification.getSuggestedBig ()
	//						|| quantityMedium.getNumber () > movementModification.getSuggestedMedium ()
		//					|| quantitySmall.getNumber () > movementModification.getSuggestedSmall () ) ) {
				// Indicate that the suggestions are exceeded
				if(totalSuggestedQtty<totalEnteredQtty){
				Baguette.showText ( MovementDetailsActivity.this ,
						AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this , R.string.movement_suggestion_exceeded_message ) ,
						Baguette.BackgroundColor.RED );
				// Exit method
				return;
			} // End if
			//		}
		}
			
			if( movementSettings.getMovementType () == MovementHeadersUtils.Type.PHYSICAL_DIRECT_UNLOAD
					|| movementSettings.getMovementType () == MovementHeadersUtils.Type.UNLOAD ){
			if ( PermissionsUtils.getEnableUnloadLessVehicleStock ( MovementDetailsActivity.this ,   DatabaseUtils.getCurrentUserCode ( MovementDetailsActivity.this ) ,  DatabaseUtils.getCurrentCompanyCode(MovementDetailsActivity.this) ) ) 
			{	
				if( movementSettings.getMovementType () == MovementHeadersUtils.Type.PHYSICAL_DIRECT_UNLOAD
				|| movementSettings.getMovementType () == MovementHeadersUtils.Type.UNLOAD ){
					
					if(reason.getReasonAffectStock() == null)
					{
						
						String SQL=null;
		     	   	 	String selectionArguments [] = null;
		     	     	ArrayList < ItemLoadQuantity >	itemLoadQuantity  = new ArrayList < ItemLoadQuantity > (); 
						
					  	SQL =" Select  VehiclesStock.ItemCode, sum(cast ( case UnitMediumSmall when 1 then 0 else  " +
		   	   				 " (COALESCE( VehiclesStock.goodQuantity,0)  - (cast( COALESCE( VehiclesStock.goodQuantity,0)as integer) % cast(   UnitMediumSmall as integer))) /UnitMediumSmall end as integer)) as Box , " +
		   	   				 " SUM(  cast(case UnitMediumSmall when 1 then COALESCE(VehiclesStock.goodQuantity,0) else  (cast(COALESCE( VehiclesStock.goodQuantity,0)as integer) % cast( COALESCE(UnitMediumSmall,1)as integer)) end as integer ) )as Piece " +
		   	   				 " ,SUM(goodQuantity)  from VehiclesStock    inner join Items on Items.ItemCode = VehiclesStock .ItemCode where GoodQuantity > 0  group by VehiclesStock.ItemCode ";
		   	   					
		   	   		// Compute the selection arguments
		   	   			selectionArguments = new String [] {
		   	   				  
		   	   					//String.valueOf (startDay)
		   	   			};
		   	   			Cursor  cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
		   	   					        .rawQuery(SQL , selectionArguments) ;
		   	   			
		   	   	
		   	   			
		   	   			// Move the cursor to the first raw
		   	   			if ( cursor.moveToFirst () ) {
		   	   				// Iterate over all raws
		   	   				do {
		   	   					ItemLoadQuantity itemInvoiceQuantity =new ItemLoadQuantity();
		   	   					itemInvoiceQuantity.setItemCode(  cursor.getString(0) );
		   	   					itemInvoiceQuantity.setQttMedium( cursor.getDouble (1) ) ;
		   	   					itemInvoiceQuantity.setQttySmall( cursor.getDouble(2) ) ;
		   	   				    itemInvoiceQuantity.setGoodQuantity(cursor.getDouble(3));
		   	   					itemLoadQuantity.add ( itemInvoiceQuantity );
		   	   				} while ( cursor.moveToNext () );
		   	   			} // End if
		   	   			// Close the cursor
		   	   			cursor.close ();
		   	   			cursor = null;
		   	   		 Map < String , ItemLoadQuantity > _movementD = new HashMap < String , ItemLoadQuantity > ();	
		   	   		 for ( ItemLoadQuantity ILQ : itemLoadQuantity )
		    	    		   // Map the current movement detail to its item code
		   	    		   _movementD.put ( ILQ.getItemCode () , ILQ );
		   	   		 if(_movementD.containsKey( movementModification.getItem().getItemCode() ))
		   	   		 {
		   	   			 double unitMediumSmall=movementModification.getItem().getUnitMediumSmall();
		   	   			 double qtt = quantityMedium.getNumber () * unitMediumSmall+quantitySmall.getNumber ();
		   	   			// if( _movementD.get( movementModification.getItem().getItemCode()).getQttMedium()< quantityMedium.getNumber () || _movementD.get(movementModification.getItem().getItemCode()).getQttySmall()< quantitySmall.getNumber () ){
		   	   			if( _movementD.get( movementModification.getItem().getItemCode()).getGoodQuantity()
		   	   					< qtt){
			   	   			
		   	   				 
		   	   				 String message=AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this ,R.string.movement_unload_limit_exceeded_message)
		   	   					+" MAX Case: "+_movementD.get(movementModification.getItem().getItemCode()).getQttMedium()+"and Max Unit: "+_movementD.get(movementModification.getItem().getItemCode()).getQttySmall()
		   	   					+" Or Basic Unit Quantity: "+_movementD.get(movementModification.getItem().getItemCode()).getGoodQuantity();
		   	   				 // Indicate that the expiry date is inconsistent
		 					Baguette.showText ( MovementDetailsActivity.this ,
		 							   message  ,
		  							Baguette.BackgroundColor.RED );
		 					// Exit method

		 					return;
		   	   			 }
		   	   		 }else{
		   
							Baguette.showText ( MovementDetailsActivity.this ,
									AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this , R.string.movement_unload_not_exceeded_message ) ,
									Baguette.BackgroundColor.RED );
							// Exit method
							return;
		   	   		 }
		   	   		 
						
						
					}
				//	if( reason.getReasonAffectStock() != null )
					else{
					if(   reason.getReasonAffectStock().toLowerCase().equals("g") )	{
					
					String SQL=null;
	     	   	 	String selectionArguments [] = null;
	     	     	ArrayList < ItemLoadQuantity >	itemLoadQuantity  = new ArrayList < ItemLoadQuantity > (); 
					
				  	SQL =" Select  VehiclesStock.ItemCode, sum(cast ( case UnitMediumSmall when 1 then 0 else  " +
	   	   				 " (COALESCE( VehiclesStock.goodQuantity,0)  - (cast( COALESCE( VehiclesStock.goodQuantity,0)as integer) % cast(   UnitMediumSmall as integer))) /UnitMediumSmall end as integer)) as Box , " +
	   	   				 " SUM(  cast(case UnitMediumSmall when 1 then COALESCE(VehiclesStock.goodQuantity,0) else  (cast(COALESCE( VehiclesStock.goodQuantity,0)as integer) % cast( COALESCE(UnitMediumSmall,1)as integer)) end as integer ) )as Piece " +
	   	   				 " ,SUM(goodQuantity) from VehiclesStock    inner join Items on Items.ItemCode = VehiclesStock .ItemCode where GoodQuantity > 0  group by VehiclesStock.ItemCode ";
	   	   					
	   	   		// Compute the selection arguments
	   	   			selectionArguments = new String [] {
	   	   				  
	   	   					//String.valueOf (startDay)
	   	   			};
	   	   			Cursor  cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
	   	   					        .rawQuery(SQL , selectionArguments) ;
	   	   			
	   	   	
	   	   			
	   	   			// Move the cursor to the first raw
	   	   			if ( cursor.moveToFirst () ) {
	   	   				// Iterate over all raws
	   	   				do {
	   	   					ItemLoadQuantity itemInvoiceQuantity =new ItemLoadQuantity();
	   	   					itemInvoiceQuantity.setItemCode(  cursor.getString(0) );
	   	   					itemInvoiceQuantity.setQttMedium( cursor.getDouble (1) ) ;
	   	   					itemInvoiceQuantity.setQttySmall( cursor.getDouble(2) ) ;
	   	   					itemInvoiceQuantity.setGoodQuantity(cursor.getDouble(3));
	   	   					itemLoadQuantity.add ( itemInvoiceQuantity );
	   	   				} while ( cursor.moveToNext () );
	   	   			} // End if
	   	   			// Close the cursor
	   	   			cursor.close ();
	   	   			cursor = null;
	   	   		 Map < String , ItemLoadQuantity > _movementD = new HashMap < String , ItemLoadQuantity > ();	
	   	   		 for ( ItemLoadQuantity ILQ : itemLoadQuantity )
	    	    		   // Map the current movement detail to its item code
	   	    		   _movementD.put ( ILQ.getItemCode () , ILQ );
	   	   		 if(_movementD.containsKey( movementModification.getItem().getItemCode() ))
	   	   		 {
	   	   			// if( _movementD.get( movementModification.getItem().getItemCode()).getQttMedium()< quantityMedium.getNumber () || _movementD.get(movementModification.getItem().getItemCode()).getQttySmall()< quantitySmall.getNumber () ){
	   	   		 double unitMediumSmall=movementModification.getItem().getUnitMediumSmall();
   	   			 double qtt = quantityMedium.getNumber () * unitMediumSmall+quantitySmall.getNumber ();
   	   			// if( _movementD.get( movementModification.getItem().getItemCode()).getQttMedium()< quantityMedium.getNumber () || _movementD.get(movementModification.getItem().getItemCode()).getQttySmall()< quantitySmall.getNumber () ){
   	   			if( _movementD.get( movementModification.getItem().getItemCode()).getGoodQuantity() < qtt){
	   	   			
   	   				 
   	   				 String message=AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this ,R.string.movement_unload_limit_exceeded_message)
   	   					+" MAX Case: "+_movementD.get(movementModification.getItem().getItemCode()).getQttMedium()+"and Max Unit: "+_movementD.get(movementModification.getItem().getItemCode()).getQttySmall()
   	   					+" Or Basic Unit Quantity: "+_movementD.get(movementModification.getItem().getItemCode()).getGoodQuantity();
   	   				 // Indicate that the expiry date is inconsistent
	 					Baguette.showText ( MovementDetailsActivity.this ,
	 							   message  ,
	  							Baguette.BackgroundColor.RED );
	 					// Exit method

	 					return;
	   	   			 }
	   	   		 }else{
	   
						Baguette.showText ( MovementDetailsActivity.this ,
								AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this , R.string.movement_unload_not_exceeded_message ) ,
								Baguette.BackgroundColor.RED );
						// Exit method
						return;
	   	   		 }
	   	   		 
				}
				
				
				else{
					if(    reason.getReasonAffectStock().toLowerCase().equals("d") )	

					if( movementSettings.getMovementType () == MovementHeadersUtils.Type.PHYSICAL_DIRECT_UNLOAD 
							
							|| movementSettings.getMovementType () == MovementHeadersUtils.Type.UNLOAD ){
						
						
						
						String SQL=null;
		     	   	 	String selectionArguments [] = null;
		     	     	ArrayList < ItemLoadQuantity >	itemLoadQuantity  = new ArrayList < ItemLoadQuantity > (); 
						
					  	SQL =" Select  VehiclesStock.ItemCode, sum(cast ( case UnitMediumSmall when 1 then 0 else  " +
		   	   				 " (COALESCE( VehiclesStock.damageQuantity,0)  - (cast( COALESCE( VehiclesStock.damageQuantity,0)as integer) % cast(   UnitMediumSmall as integer))) /UnitMediumSmall end as integer)) as Box , " +
		   	   				 " SUM(  cast(case UnitMediumSmall when 1 then COALESCE(VehiclesStock.damageQuantity,0) else  (cast(COALESCE( VehiclesStock.damageQuantity,0)as integer) % cast( COALESCE(UnitMediumSmall,1)as integer)) end as integer ) )as Piece " +
		   	   				 " ,SUM(DamageQuantity) from VehiclesStock    inner join Items on Items.ItemCode = VehiclesStock .ItemCode where damageQuantity > 0  group by VehiclesStock.ItemCode ";
		   	   					
		   	   		
		   	   		
		   	   		// Compute the selection arguments
		   	   			selectionArguments = new String [] {
		   	   				  
		   	   					//String.valueOf (startDay)
		   	   			};
		   	   			Cursor  cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
		   	   					        .rawQuery(SQL , selectionArguments) ;
		   	   			
		   	   	
		   	   			
		   	   			// Move the cursor to the first raw
		   	   			if ( cursor.moveToFirst () ) {
		   	   				// Iterate over all raws
		   	   				do {
		   	   					 
		   	   					ItemLoadQuantity itemInvoiceQuantity =new ItemLoadQuantity();
		   	   					itemInvoiceQuantity.setItemCode( cursor.getString(0) ) ;
		   	   					itemInvoiceQuantity.setQttMedium( cursor.getDouble (1) ) ;
		   	   					itemInvoiceQuantity.setQttySmall( cursor.getDouble(2) ) ;
		   	   				    itemInvoiceQuantity.setGoodQuantity( cursor.getDouble(3) ) ;
		   	   					itemLoadQuantity.add ( itemInvoiceQuantity );
		   	   				} while ( cursor.moveToNext () );
		   	   			} // End if
		   	   			// Close the cursor
		   	   			cursor.close ();
		   	   			cursor = null;
		   	   		 Map < String , ItemLoadQuantity > _movementD = new HashMap < String , ItemLoadQuantity > ();	
		   	   		 for ( ItemLoadQuantity ILQ : itemLoadQuantity )
		    	    		   // Map the current movement detail to its item code
		   	    		   _movementD.put ( ILQ.getItemCode () , ILQ );
		   	   		 if(_movementD.containsKey(movementModification.getItem().getItemCode()))
		   	   		 {
		   	   		// if( _movementD.get(movementModification.getItem().getItemCode()).getQttMedium()< quantityMedium.getNumber () || _movementD.get(movementModification.getItem().getItemCode()).getQttySmall()< quantitySmall.getNumber ()){
		   	   		 double unitMediumSmall=movementModification.getItem().getUnitMediumSmall();
	   	   			 double qtt = quantityMedium.getNumber () * unitMediumSmall+quantitySmall.getNumber ();
	   	   			// if( _movementD.get( movementModification.getItem().getItemCode()).getQttMedium()< quantityMedium.getNumber () || _movementD.get(movementModification.getItem().getItemCode()).getQttySmall()< quantitySmall.getNumber () ){
	   	   			if( _movementD.get( movementModification.getItem().getItemCode()).getGoodQuantity() < qtt){
		   	   			 
	   	   				 String message=AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this ,R.string.movement_unload_limit_exceeded_message)
	   	   					+" MAX Case: "+_movementD.get(movementModification.getItem().getItemCode()).getQttMedium()+"and Max Unit: "+_movementD.get(movementModification.getItem().getItemCode()).getQttySmall()
	   	   					+" Or Basic Unit Quantity: "+_movementD.get(movementModification.getItem().getItemCode()).getGoodQuantity();
	   	   				 // Indicate that the expiry date is inconsistent
		 					Baguette.showText ( MovementDetailsActivity.this ,
		 							   message  ,
		  							Baguette.BackgroundColor.RED );
		 					// Exit method

		 					return;
		   	   			 }
		   	   		 }else{
		   
							Baguette.showText ( MovementDetailsActivity.this ,
									AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this , R.string.movement_unload_not_exceeded_message ) ,
									Baguette.BackgroundColor.RED );
							// Exit method
							return;
		   	   		 }
		   	   		 } 
				}
				}
					}
				}
				if ( PermissionsUtils.getEnableUnloadEqualVehiculeStock ( MovementDetailsActivity.this ,   DatabaseUtils.getCurrentUserCode ( MovementDetailsActivity.this ) ,  DatabaseUtils.getCurrentCompanyCode(MovementDetailsActivity.this) ) ) 
					{
					if ( movementSettings.isUseExpiryDate () && movementSettings.isEnforceExpiryDate () && expiryDate == null ) {
						// Indicate that the expiry date is missing
						Baguette.showText ( MovementDetailsActivity.this ,
								AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this , R.string.movement_unload_not_exceeded_message ) ,
								Baguette.BackgroundColor.RED );
						// Exit method
						return;
					} // End if
					// Check if the reason is mandatory
					if ( movementSettings.isUseReason () && movementSettings.isEnforceReason () && ( reason == null || reason.getReasonCode () == null ) ) {
						// Indicate that the reason is missing
						Baguette.showText ( MovementDetailsActivity.this ,
								AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this , R.string.movement_invalid_reason_message ) ,
								Baguette.BackgroundColor.RED );
						// Exit method
						return;
					} // End if 
					
					if(reason.getReasonAffectStock()==null){

						
						if( movementSettings.getMovementType () == MovementHeadersUtils.Type.PHYSICAL_DIRECT_UNLOAD || movementSettings.getMovementType () == MovementHeadersUtils.Type.UNLOAD ){
									
							String SQL=null;
			     	   	 	String selectionArguments [] = null;
			     	     	ArrayList < ItemLoadQuantity >	itemLoadQuantity  = new ArrayList < ItemLoadQuantity > (); 
							
						  	SQL =" Select  VehiclesStock.ItemCode, sum(cast ( case UnitMediumSmall when 1 then 0 else  " +
			   	   				 " (COALESCE( VehiclesStock.goodQuantity,0)  - (cast( COALESCE( VehiclesStock.goodQuantity,0)as integer) % cast(   UnitMediumSmall as integer))) /UnitMediumSmall end as integer)) as Box , " +
			   	   				 " SUM(  cast(case UnitMediumSmall when 1 then COALESCE(VehiclesStock.goodQuantity,0) else  (cast(COALESCE( VehiclesStock.goodQuantity,0)as integer) % cast( COALESCE(UnitMediumSmall,1)as integer)) end as integer ) )as Piece " +
			   	   				 " , SUM(goodQuantity) from VehiclesStock    inner join Items on Items.ItemCode = VehiclesStock .ItemCode where GoodQuantity > 0  group by VehiclesStock.ItemCode ";
			   	   					
			   	   		
			   	   		
			   	   		// Compute the selection arguments
			   	   			selectionArguments = new String [] {
			   	   				  
			   	   					//String.valueOf (startDay)
			   	   			};
			   	   			Cursor  cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
			   	   					        .rawQuery(SQL , selectionArguments) ;
			   	   			
			   	   	
			   	   			
			   	   			// Move the cursor to the first raw
			   	   			if ( cursor.moveToFirst () ) {
			   	   				// Iterate over all raws
			   	   				do {
			   	   					 
			   	   					ItemLoadQuantity itemInvoiceQuantity =new ItemLoadQuantity();
			   	   					itemInvoiceQuantity.setItemCode(  cursor.getString(0) );
			   	   					itemInvoiceQuantity.setQttMedium(cursor.getDouble (1)) ;
			   	   					itemInvoiceQuantity.setQttySmall(cursor.getDouble(2)) ;
			   	   					itemInvoiceQuantity.setGoodQuantity( cursor.getDouble(3) ) ;
			   	   					itemLoadQuantity.add ( itemInvoiceQuantity );
			   	   				} while ( cursor.moveToNext () );
			   	   			} // End if
			   	   			// Close the cursor
			   	   			cursor.close ();
			   	   			cursor = null;
			   	   		 Map < String , ItemLoadQuantity > _movementD = new HashMap < String , ItemLoadQuantity > ();	
			   	   		 for ( ItemLoadQuantity ILQ : itemLoadQuantity )
			    	    		   // Map the current movement detail to its item code
			   	    		   _movementD.put ( ILQ.getItemCode () , ILQ );
			   	   		 if(_movementD.containsKey(movementModification.getItem().getItemCode()))
			   	   		 {
			   	   			// if( _movementD.get(movementModification.getItem().getItemCode()).getQttMedium()!= quantityMedium.getNumber () || _movementD.get(movementModification.getItem().getItemCode()).getQttySmall() != quantitySmall.getNumber ()){
			   	   		//	String message=AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this ,R.string.movement_unload_equal_exceeded_message)
			   	   		//			+"  Case:"+_movementD.get(movementModification.getItem().getItemCode()).getQttMedium()+"and Unit "+_movementD.get(movementModification.getItem().getItemCode()).getQttySmall();
			   	   		 double unitMediumSmall=movementModification.getItem().getUnitMediumSmall();
		   	   			 double qtt = quantityMedium.getNumber () * unitMediumSmall+quantitySmall.getNumber ();
		   	   			// if( _movementD.get( movementModification.getItem().getItemCode()).getQttMedium()< quantityMedium.getNumber () || _movementD.get(movementModification.getItem().getItemCode()).getQttySmall()< quantitySmall.getNumber () ){
		   	   			if( _movementD.get( movementModification.getItem().getItemCode()).getGoodQuantity() != qtt){
			   	   			 
		   	   				 String message=AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this ,R.string.movement_unload_limit_exceeded_message)
		   	   					+" MAX Case: "+_movementD.get(movementModification.getItem().getItemCode()).getQttMedium()+"and Max Unit: "+_movementD.get(movementModification.getItem().getItemCode()).getQttySmall()
		   	   					+" Or Basic Unit Quantity: "+_movementD.get(movementModification.getItem().getItemCode()).getGoodQuantity();
		   	   		
			   	   			 
			   	   			 // Indicate that the expiry date is inconsistent
			 					Baguette.showText ( MovementDetailsActivity.this ,
			 							   message  ,
			  							Baguette.BackgroundColor.RED );
			 					// Exit method

			 					return;
			   	   			 }
			   	   		 }else{
			   
								Baguette.showText ( MovementDetailsActivity.this ,
										AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this , R.string.movement_unload_not_exceeded_message ) ,
										Baguette.BackgroundColor.RED );
								// Exit method
								return;
			   	   		 }
			   	   		 }
			   	   		
							
					}
					//if(reason.getReasonAffectStock()!=null)
					else{
					if(reason.getReasonAffectStock().toLowerCase().equals("g"))	{
											
					if( movementSettings.getMovementType () == MovementHeadersUtils.Type.PHYSICAL_DIRECT_UNLOAD || movementSettings.getMovementType () == MovementHeadersUtils.Type.UNLOAD ){
								
						String SQL=null;
		     	   	 	String selectionArguments [] = null;
		     	     	ArrayList < ItemLoadQuantity >	itemLoadQuantity  = new ArrayList < ItemLoadQuantity > (); 
						
					  	SQL =" Select  VehiclesStock.ItemCode, sum(cast ( case UnitMediumSmall when 1 then 0 else  " +
		   	   				 " (COALESCE( VehiclesStock.GoodQuantity,0)  - (cast( COALESCE( VehiclesStock.GoodQuantity,0)as integer) % cast(   UnitMediumSmall as integer))) /UnitMediumSmall end as integer)) as Box , " +
		   	   				 " SUM(  cast(case UnitMediumSmall when 1 then COALESCE(VehiclesStock.GoodQuantity,0) else  (cast(COALESCE( VehiclesStock.GoodQuantity,0)as integer) % cast( COALESCE(UnitMediumSmall,1)as integer)) end as integer ) )as Piece " +
		   	   				 " , SUM(goodQuantity) from VehiclesStock    inner join Items on Items.ItemCode = VehiclesStock .ItemCode where GoodQuantity > 0  group by VehiclesStock.ItemCode ";
		   	   					
		   	   		
		   	   		
		   	   		// Compute the selection arguments
		   	   			selectionArguments = new String [] {
		   	   				  
		   	   					//String.valueOf (startDay)
		   	   			};
		   	   			Cursor  cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
		   	   					        .rawQuery(SQL , selectionArguments) ;
		   	   			
		   	   	
		   	   			
		   	   			// Move the cursor to the first raw
		   	   			if ( cursor.moveToFirst () ) {
		   	   				// Iterate over all raws
		   	   				do {
		   	   					 
		   	   					ItemLoadQuantity itemInvoiceQuantity = new ItemLoadQuantity();
		   	   					itemInvoiceQuantity.setItemCode(  cursor.getString(0) );
		   	   					itemInvoiceQuantity.setQttMedium(cursor.getDouble (1)) ;
		   	   					itemInvoiceQuantity.setQttySmall(cursor.getDouble(2)) ;
		   	   			    	itemInvoiceQuantity.setGoodQuantity( cursor.getDouble(3) ) ;
		   	   				
		   	   			    	itemLoadQuantity.add ( itemInvoiceQuantity );
		   	   				} while ( cursor.moveToNext () );
		   	   			} // End if
		   	   			// Close the cursor
		   	   			cursor.close ();
		   	   			cursor = null;
		   	   		 Map < String , ItemLoadQuantity > _movementD = new HashMap < String , ItemLoadQuantity > ();	
		   	   		 for ( ItemLoadQuantity ILQ : itemLoadQuantity )
		    	    		   // Map the current movement detail to its item code
		   	    		   _movementD.put ( ILQ.getItemCode () , ILQ );
		   	   		 if(_movementD.containsKey(movementModification.getItem().getItemCode()))
		   	   		 {
//		   	   			 if( _movementD.get(movementModification.getItem().getItemCode()).getQttMedium()!= quantityMedium.getNumber () || _movementD.get(movementModification.getItem().getItemCode()).getQttySmall() != quantitySmall.getNumber ()){
//		   	   			String message=AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this ,R.string.movement_unload_equal_exceeded_message)
//		   	   					+"  Case:"+_movementD.get(movementModification.getItem().getItemCode()).getQttMedium()+"and Unit "+_movementD.get(movementModification.getItem().getItemCode()).getQttySmall();
		   	   				
		   	   		 double unitMediumSmall=movementModification.getItem().getUnitMediumSmall();
	   	   			 double qtt = quantityMedium.getNumber () * unitMediumSmall+quantitySmall.getNumber ();
	   	   			 	if( _movementD.get( movementModification.getItem().getItemCode()).getGoodQuantity() != qtt){
		   	   			 
	   	   				 String message=AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this ,R.string.movement_unload_limit_exceeded_message)
	   	   					+" MAX Case: "+_movementD.get(movementModification.getItem().getItemCode()).getQttMedium()+"and Max Unit: "+_movementD.get(movementModification.getItem().getItemCode()).getQttySmall()
	   	   					+" Or Basic Unit Quantity: "+_movementD.get(movementModification.getItem().getItemCode()).getGoodQuantity();
	   	   		
		   	   			 
		   	   			 // Indicate that the expiry date is inconsistent
		 					Baguette.showText ( MovementDetailsActivity.this ,
		 							   message  ,
		  							Baguette.BackgroundColor.RED );
		 					// Exit method

		 					return;
		   	   			 }
		   	   		 }else{
		   
							Baguette.showText ( MovementDetailsActivity.this ,
									AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this , R.string.movement_unload_not_exceeded_message ) ,
									Baguette.BackgroundColor.RED );
							// Exit method
							return;
		   	   		 }
		   	   		 }
		   	   		
					} else
		   	   		 {	if(   reason.getReasonAffectStock().toLowerCase().equals("d") )	
						if( movementSettings.getMovementType () == MovementHeadersUtils.Type.PHYSICAL_DIRECT_UNLOAD || movementSettings.getMovementType () == MovementHeadersUtils.Type.UNLOAD ){
							
							
							String SQL=null;
			     	   	 	String selectionArguments [] = null;
			     	     	ArrayList < ItemLoadQuantity >	itemLoadQuantity  = new ArrayList < ItemLoadQuantity > (); 
							
						  	SQL =" Select  VehiclesStock.ItemCode, sum(cast ( case UnitMediumSmall when 1 then 0 else  " +
			   	   				 " (COALESCE( VehiclesStock.damageQuantity,0)  - (cast( COALESCE( VehiclesStock.damageQuantity,0)as integer) % cast(   UnitMediumSmall as integer))) /UnitMediumSmall end as integer)) as Box , " +
			   	   				 " SUM(  cast(case UnitMediumSmall when 1 then COALESCE(VehiclesStock.damageQuantity,0) else  (cast(COALESCE( VehiclesStock.damageQuantity,0)as integer) % cast( COALESCE(UnitMediumSmall,1)as integer)) end as integer ) )as Piece " +
			   	   				 " ,SUM(DamageQuantity)  from VehiclesStock    inner join Items on Items.ItemCode = VehiclesStock .ItemCode where damageQuantity > 0  group by VehiclesStock.ItemCode ";
			   	   					
			   	   		
			   	   		
			   	   		// Compute the selection arguments
			   	   			selectionArguments = new String [] {
			   	   				  
			   	   					//String.valueOf (startDay)
			   	   			};
			   	   			Cursor  cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
			   	   					        .rawQuery(SQL , selectionArguments) ;
			   	   			
			   	   	
			   	   			
			   	   			// Move the cursor to the first raw
			   	   			if ( cursor.moveToFirst () ) {
			   	   				// Iterate over all raws
			   	   				do {
			   	   					 
			   	   					ItemLoadQuantity itemInvoiceQuantity =new ItemLoadQuantity();
			   	   					itemInvoiceQuantity.setItemCode(  cursor.getString(0) );
			   	   					itemInvoiceQuantity.setQttMedium(cursor.getDouble (1)) ;
			   	   					itemInvoiceQuantity.setQttySmall(cursor.getDouble(2)) ;
			   	   				    itemInvoiceQuantity.setGoodQuantity(cursor.getDouble(3));
			   	   					itemLoadQuantity.add ( itemInvoiceQuantity );
			   	   				} while ( cursor.moveToNext () );
			   	   			} // End if
			   	   			// Close the cursor
			   	   			cursor.close ();
			   	   			cursor = null;
			   	   		 Map < String , ItemLoadQuantity > _movementD = new HashMap < String , ItemLoadQuantity > ();	
			   	   		 for ( ItemLoadQuantity ILQ : itemLoadQuantity )
			    	    		   // Map the current movement detail to its item code
			   	    		   _movementD.put ( ILQ.getItemCode () , ILQ );
			   	   		 if(_movementD.containsKey(movementModification.getItem().getItemCode()))
			   	   		 {
//			   	   			 if( _movementD.get(movementModification.getItem().getItemCode()).getQttMedium()!= quantityMedium.getNumber () || _movementD.get(movementModification.getItem().getItemCode()).getQttySmall() != quantitySmall.getNumber ()){
//			   	   			String message=AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this ,R.string.movement_unload_equal_exceeded_message)
//			   	   					+"  Case:"+_movementD.get(movementModification.getItem().getItemCode()).getQttMedium()+"and Unit: "+_movementD.get(movementModification.getItem().getItemCode()).getQttySmall();
			   	   				 // Indicate that the expiry date is inconsistent
			   	   		 double unitMediumSmall=movementModification.getItem().getUnitMediumSmall();
		   	   			 double qtt = quantityMedium.getNumber () * unitMediumSmall+quantitySmall.getNumber ();
		   	   			 	if( _movementD.get( movementModification.getItem().getItemCode()).getGoodQuantity() != qtt){
			   	   			 
		   	   				 String message=AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this ,R.string.movement_unload_limit_exceeded_message)
		   	   					+" MAX Case: "+_movementD.get(movementModification.getItem().getItemCode()).getQttMedium()+"and Max Unit: "+_movementD.get(movementModification.getItem().getItemCode()).getQttySmall()
		   	   					+" Or Basic Unit Quantity: "+_movementD.get(movementModification.getItem().getItemCode()).getGoodQuantity();
		   	   		
			   	   			 Baguette.showText ( MovementDetailsActivity.this ,
			 						    message  ,
			  							Baguette.BackgroundColor.RED );
			 					// Exit method
	                        	return;
			   	   			 }
			   	   		 }else{
			   
								Baguette.showText ( MovementDetailsActivity.this ,
								AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this , R.string.movement_unload_not_exceeded_message ) ,
								Baguette.BackgroundColor.RED );
								// Exit method
								return;
			   	   		 }
			   	   		 } 
		   	   		 }
					}
					}
		} // End if
		}
		// Reset flag
		displayMovementDetails = false;
		
		
		// Update the movement quantities
		movementModification.setQuantityBig ( (int) quantityBig.getNumber () );
		movementModification.setQuantityMedium ( (int) quantityMedium.getNumber () );
		movementModification.setQuantitySmall ( (int) quantitySmall.getNumber () );
		// Update the expiry date
		movementModification.setExpiryDate ( expiryDate );
		// Update the existence
		movementModification.setReason ( reason == null || reasonSpinner.getSelectedItemPosition () == 0 ? null : reason );
		
		// Disable the save icon
	secondary.findViewById ( R.id.icon_save_movement ).setEnabled ( true );
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
		
		// Update the movement / UI
		onMovementModificationResult ();
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
		passcode = passcodeEditText.getText ().toString ().trim ();
		// Retrieve the movement settings
		MovementSettings movementSetting = (MovementSettings) getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS );
		
		// Validate pass code
		if ( ! UserPasswordsUtils.validateTimePasswordUsers ( this , UserPasswordsUtils.TransactionHeaderType.MOVEMENT , movementSetting.getMovementType () , passcode , DatabaseUtils.getCurrentUserCode ( this )) ) {
			// Reset passcode
			passcode = null;
			// Indicate that the passcode is not valid
			Baguette.showText ( MovementDetailsActivity.this ,
					AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this , R.string.time_passcode_invalid_message ) ,
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
		// Enable the main list
		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( true );
		// Indicate that the save was successful
		Vibration.vibrate ( this );
		// Refresh the action bar
		invalidateOptionsMenu ();
		
		// Simulate a save button click
		saveMovement ();
	}
	
	/**
	 * Called when a view has been clicked.<br>
	 * The user approval is saved.
	 * 
	 * @param view	The clicked view.
	 */
	public void updateUserApproval ( View view ) {
		// Determine if the warehouse keeper approval is undergoing any modifications
		if ( ! displayWarehouseKeeperApproval && ! displayCashVanApproval )
			// No modifications
			return;
		
		// Retrieve a reference to the quaternary view
		View quaternary = findViewById ( R.id.layout_warehouse_keeper_approval );
		// Retrieve a reference to the users spinner
		Spinner usersSpinner = (Spinner) quaternary.findViewById ( R.id.spinner_users );
		// Retrieve a reference to the types spinner
		Spinner typesSpinner = (Spinner) quaternary.findViewById ( R.id.spinner_type );
		// Retrieve a reference to the password edit text
		EditText passwordEditText = (EditText) quaternary.findViewById ( R.id.edittext_password );
		// Store the password
		String password = passwordEditText.getText ().toString ();
		// Store the user
		Users user = (Users) usersSpinner.getSelectedItem ();
		// Declare and initialize a boolean used to determine if the movement is approved
		boolean approved = false;
		// Retrieve the current time
		Calendar now = Calendar.getInstance ();
		// Retrieve the movement settings
		MovementSettings movementSetting = (MovementSettings) getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS );
		// Retrieve the user, company and division codes
		String userCode = DatabaseUtils.getCurrentUserCode ( this );
		String companyCode = DatabaseUtils.getCurrentCompanyCode ( this );
		String divisionCode = DatabaseUtils.getCurrentDivisionCode ( this );
		
		// Check if the barcode scanning is allowed
		if ( allowWarehouseBarcodeScanning ) {
			// Store the type
			ApprovalType approvalType = (ApprovalType) typesSpinner.getSelectedItem ();
			// Check if the type is barcode
			if ( approvalType.getType () == ApprovalType.TYPE_BARCODE ) {
				// Query the barcode 
				long count = DatabaseUtils.getInstance ( this ).getDaoSession ().getWareHouseBarcodesDao ().queryBuilder ()
						.where ( WareHouseBarcodesDao.Properties.Barcode.eq ( password ) ).count ();
				// Check if the barcode exists
				if ( count > 0 ) {
					// Indicate the movement is approved
					warehouseKeeperStart = new Users ();
					approved = true;
					// Insert a warehouse barcode scanning
					DatabaseUtils.getInstance ( this ).getDaoSession ().getWareHouseBarcodeScanningDao ().insert ( new WareHouseBarcodeScanning ( null , //  ID
							String.valueOf ( VisitsUtils.getVisitID ( now ) ) , // TransactionCode
							userCode , // UserCode
							companyCode , // CompanyCode
							divisionCode , // CompanyCode
							movementSetting.getMovementType () , // MovementType
							null , // WarehouseKeeperCode
							password , // WarehouseBarcode
							now.getTime () , // ArrivalDate
							IsProcessedUtils.isNotSync () , // ArrivalDate
							now.getTime () ) ); // StampDate
				} // End if
				else {
					// Display an error message 
					passwordEditText.setError ( AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this , R.string.no_warehouse_barcode_message ) );
					// Exit method
					return;
				} // End else
			} // End if
		} // End if
		
		// Check if the movement is not approved
		if ( ! approved ) {
			// Check if the user is valid
			if ( user == null || user.getUserCode () == null ) {
				// Indicate that the passcode is not valid
				Baguette.showText ( MovementDetailsActivity.this ,
						AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this , R.string.movement_select_user_message ) ,
						Baguette.BackgroundColor.RED );
				// Exit method
				return;
			} // End if 
			// Make sure the user password is valid
			if ( user.getUserPassword () == null )
				// Set the user password
				user.setUserPassword ( "" );
			// Determine if the password is valid
			if ( ! password.equals ( user.getUserPassword () ) ) {
				// Display an error message 
				passwordEditText.setError ( AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this , R.string.error_invalid_password_message ) );
				// Exit method
				return;
			} // End if
			// Update the appropriate user reference
			if ( ( (MovementSettings) getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS ) ).getMovementType () == MovementHeadersUtils.Type.LOAD && warehouseKeeperStart == null ) {
				warehouseKeeperStart = user;
				// Insert a warehouse barcode scanning
				DatabaseUtils.getInstance ( this ).getDaoSession ().getWareHouseBarcodeScanningDao ().insert ( new WareHouseBarcodeScanning ( null , //  ID
						String.valueOf ( VisitsUtils.getVisitID ( now ) ) , // TransactionCode
						userCode , // UserCode
						companyCode , // CompanyCode
						divisionCode , // CompanyCode
						movementSetting.getMovementType () , // MovementType
						warehouseKeeperStart.getUserCode () , // WarehouseKeeperCode
						null , // WarehouseBarcode
						now.getTime () , // ArrivalDate
						IsProcessedUtils.isNotSync () , // ArrivalDate
						now.getTime () ) ); // StampDate
			} // End if
			else if ( ( ( (MovementSettings) getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS ) ).getMovementType () == MovementHeadersUtils.Type.LOAD 
					|| ( (MovementSettings) getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS ) ).getMovementType () == MovementHeadersUtils.Type.UNLOAD 
					|| ( (MovementSettings) getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS ) ).getMovementType () == MovementHeadersUtils.Type.STOCK_RECONCILIATION
					|| ( (MovementSettings) getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS ) ).getMovementType () == MovementHeadersUtils.Type.Physical_Direct_Load
					|| ( (MovementSettings) getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS ) ).getMovementType () == MovementHeadersUtils.Type.PHYSICAL_DIRECT_UNLOAD ) 
					&& warehouseKeeperEnd == null )
				warehouseKeeperEnd = user;
			else if ( ( (MovementSettings) getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS ) ).getMovementType () == MovementHeadersUtils.Type.VAN_TRANSFER && cashVan == null )
				cashVan = user;
		} // End if
		
		// Reset flags
		displayWarehouseKeeperApproval = false;
		displayCashVanApproval = false;
		allowWarehouseBarcodeScanning = false;
		
		// Disable the save icon
		quaternary.findViewById ( R.id.layout_button_login ).setEnabled ( false );
		// Hide the software keyboard
        ( (InputMethodManager) getBaseContext ().getSystemService ( INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow ( quaternary.getWindowToken (), 0 );
		// Reset field
		passwordEditText.setText ( "" );
        
		// Hide the quaternary view
        quaternary.startAnimation ( getViewAnimationOut ( quaternary ) );
		// Enable the main list
		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( true );
		// Indicate that the save was successful
		Vibration.vibrate ( this );
		// Refresh the action bar
		invalidateOptionsMenu ();
		
		// Check if this was an approval for the end of the movement
		if ( warehouseKeeperEnd != null || cashVan != null )
			// Simulate a save button click
			saveMovement ();
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
			intent.putExtra ( MovementActivity.SAVE_SUCCESS , ! error );
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
	}
	
	/**
	 * Called after a successful movement modification : price, quantity, expiry date or reason modification.<br>
	 * The corresponding object is modified in the main list and the appropriate action is performed (based on the current request code).
	 */
	private void onMovementModificationResult () {
		// Retrieve a reference to the movement object from the main list
		Movement movement = null;
		// Iterate over all the movements
		for ( int i = 0 ; i < movements.size () ; i ++ )
			// Match the movement
			if ( movements.get ( i ).getItem ().getItemCode ().equals ( movementModification.getItem ().getItemCode () ) ) {
				// Update the selected movement position
				movement = movements.get ( i );
				// Exit loop
				break;
			} // End if
		// Check if the movement object is valid
		if ( movement == null )
			// Invalid object
			return;
    	// Update the movement object
		movement.setQuantityBig ( movementModification.getQuantityBig () );
		movement.setQuantityMedium ( movementModification.getQuantityMedium () );
		movement.setQuantitySmall ( movementModification.getQuantitySmall () );
		movement.setExpiryDate ( movementModification.getExpiryDate () );
		movement.setReason ( movementModification.getReason () );
		// Remove item from the selected movements (if valid)
		if ( selectedMovements != null && movement.getQuantityBig () == 0 && movement.getQuantityMedium () == 0 && movement.getQuantitySmall () == 0 )
			selectedMovements.remove ( movement );
		// Refresh movement details
		refreshDetails ();
		// Check if the request code is INFO
		// AND if the selected movements list is empty
		if ( MovementDetailsActivity.this.requestCode == REQUEST_CODE_INFO && selectedMovements != null && selectedMovements.isEmpty () )
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
    	// Check if the list of movements is valid
    	// OR if a movement is undergoing modifications
    	if ( movements == null || displayMovementDetails || displayPasscode || displayWarehouseKeeperApproval || displayVanType || displayCashVanApproval )
    		// Hide the menu
            return false;
    	// Retrieve the movement settings
    	MovementSettings movementSetting = (MovementSettings) getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS );
    	// Use the MenuInflater of this context to inflate a menu hierarchy from the specified XML resource
    	getMenuInflater ().inflate ( R.menu.action_bar , menu );
    	// Determine if the request code is info
    	if ( requestCode != REQUEST_CODE_INFO ) {
    		// The request code is NOT INFO
	    	// Enable the required menu items
    		me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_list ) );
	    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_basket ) );
	    	
	    	
	    	if(movementSetting.getMovementType () == MovementHeadersUtils.Type.PHYSICAL_DIRECT_UNLOAD)
			me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_forward));
	    	if ( PermissionsUtils.getEnableLoadUnloadDirect (  MovementDetailsActivity.this ,   DatabaseUtils.getCurrentUserCode (   this ) ,  DatabaseUtils.getCurrentCompanyCode(  this) ) ) 
	        	
	    	if(movementSetting.getMovementType () == MovementHeadersUtils.Type.LOAD ||    movementSetting.getMovementType () == MovementHeadersUtils.Type.UNLOAD)
				me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_forward));
	    	
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
    		if ( movementHeader == null
    				|| ( movementHeader.getIsProcessed () != IsProcessedUtils.isSync () ) ) {
            	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_save ) );
            	if ( movementSetting.getMovementType () == MovementHeadersUtils.Type.LOAD || movementSetting.getMovementType () == MovementHeadersUtils.Type.UNLOAD || movementSetting.getMovementType () == MovementHeadersUtils.Type.STOCK_RECONCILIATION )
            		me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_print ) );
    		} // End if
    	} // End else
    	else if (  movementSetting.getMovementType () == MovementHeadersUtils.Type.LOAD
				|| movementSetting.getMovementType () == MovementHeadersUtils.Type.LOAD_REQUEST
				|| movementSetting.getMovementType () == MovementHeadersUtils.Type.UNLOAD
				|| movementSetting.getMovementType () == MovementHeadersUtils.Type.UNLOAD_REQUEST
				|| movementSetting.getMovementType () == MovementHeadersUtils.Type.STOCK_MANAGEMENT
				|| movementSetting.getMovementType () == MovementHeadersUtils.Type.VAN_STOCK_COUNT
				|| movementSetting.getMovementType () == MovementHeadersUtils.Type.STOCK_RECONCILIATION
				|| movementSetting.getMovementType () == MovementHeadersUtils.Type.VAN_TRANSFER 
				|| movementSetting.getMovementType () == MovementHeadersUtils.Type.Physical_Direct_Load
				|| movementSetting.getMovementType () == MovementHeadersUtils.Type.PHYSICAL_DIRECT_UNLOAD)
    		// Order can be printed in view mode only
    		me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_print ) );
		// Display the menu
        return true;
    }
    
	/**
	 * Performs all necessary setup in movement to properly display the search view widget.
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
    	// Check if the movements list is valid
    	if ( movements == null )
    		// Consume event
    		return true;
    	if ( menuItem.getItemId () == R.id.action_forward ) {
    		MovementSettings movementSetting = (MovementSettings) getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS );
    		if(movementSetting.getMovementType () == MovementHeadersUtils.Type.PHYSICAL_DIRECT_UNLOAD){
    		
    		Calendar calendar = Calendar.getInstance ();
    	     calendar.set ( Calendar.HOUR_OF_DAY , 0 );
    	     calendar.set ( Calendar.MINUTE , 0 );
    	     calendar.set ( Calendar.SECOND , 0 );
    	     calendar.set ( Calendar.MILLISECOND , 0 );
    	    long startDay = calendar.getTimeInMillis ();
    		Calendar c = Calendar.getInstance(); 
    		
    		autoUnload=true;
    	 
  	    		
     	   		String SQL=null;
     	   	 	String selectionArguments [] = null;
 
    	   	
    	    	   
    	   			ArrayList < ItemLoadQuantity >	itemLoadQuantity  = new ArrayList < ItemLoadQuantity > (); 
     			
    	    	   		 	SQL =" Select  VehiclesStock.ItemCode, sum(cast ( case UnitMediumSmall when 1 then 0 else  " +
    	    	   				 " (COALESCE( VehiclesStock.goodQuantity,0)  - (cast( COALESCE( VehiclesStock.goodQuantity,0)as integer) % cast(   UnitMediumSmall as integer))) /UnitMediumSmall end as integer)) as Box , " +
    	    	   				 " SUM(  cast(case UnitMediumSmall when 1 then COALESCE(VehiclesStock.goodQuantity,0) else  (cast(COALESCE( VehiclesStock.goodQuantity,0)as integer) % cast( COALESCE(UnitMediumSmall,1)as integer)) end as integer ) )as Piece " +
    	    	   				 " from VehiclesStock    inner join Items on Items.ItemCode = VehiclesStock .ItemCode where GoodQuantity > 0  group by VehiclesStock.ItemCode ";
    	    	   					
    	    	   		 
    	    	   		
    	    	   		// Compute the selection arguments
    	    	   			selectionArguments = new String [] {
    	    	   				  
    	    	   					//String.valueOf (startDay)
    	    	   			};
    	    	   			Cursor  cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
    	    	   					        .rawQuery(SQL , selectionArguments) ;
    	    	   			
    	    	   	
    	    	   			
    	    	   			// Move the cursor to the first raw
    	    	   			if ( cursor.moveToFirst () ) {
    	    	   				// Iterate over all raws
    	    	   				do {
    	    	   					 
    	    	   					ItemLoadQuantity itemInvoiceQuantity =new ItemLoadQuantity();
    	    	   					itemInvoiceQuantity.setItemCode(  cursor.getString(0) );
    	    	   					itemInvoiceQuantity.setQttMedium(cursor.getDouble (1)) ;
    	    	   					itemInvoiceQuantity.setQttySmall(cursor.getDouble(2)) ;
    	    	   				 
    	    	   					itemLoadQuantity.add ( itemInvoiceQuantity );
    	    	   				} while ( cursor.moveToNext () );
    	    	   			} // End if
    	    	   			// Close the cursor
    	    	   			cursor.close ();
    	    	   			cursor = null;
    	    	   		 Map < String , ItemLoadQuantity > _movementD = new HashMap < String , ItemLoadQuantity > ();	
    	    	   		 for ( ItemLoadQuantity ILQ : itemLoadQuantity )
     	    	    		   // Map the current movement detail to its item code
    	    	    		   _movementD.put ( ILQ.getItemCode () , ILQ );
    	    	   
    	    	 
        		  for ( Movement movement : movements ) {
        		   if( _movementD.containsKey (movement.getItem().getItemCode()))
        				   //movement.getItem().getItemCode().equals(td.getItemCode())){
        			 {  movement.setExpiryDate(c.getTime());  
        	            movement.setReason(reasons.size()== 1 ? null : reasons.get(4));
    		        	movement.setQuantityMedium(_movementD.get(movement.getItem().getItemCode()).getQttMedium().intValue());
    		        	movement.setQuantitySmall( _movementD.get(movement.getItem().getItemCode()).getQttySmall().intValue());
        		   	}
        		  }
        		}
    		
    		if(movementSetting.getMovementType () == MovementHeadersUtils.Type.LOAD || movementSetting.getMovementType () == MovementHeadersUtils.Type.UNLOAD){
    			autoUnload = true;
    	    	 
    			  for ( Movement movement : movements ) {
    				   
    				  if( movement.getSuggestedSmall() > 0 || movement.getSuggestedMedium() > 0 )
    				  {
    					  movement.setQuantityMedium(movement.getSuggestedMedium());
    					  movement.setQuantitySmall(movement.getSuggestedSmall());

    				  }
    				  
    			  }( (MovementDetailsAdapter) getListAdapter () ).notifyDataSetChanged ();
    			
    		}
    		
    	}
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
    	// Determine if the action basket is selected
    	else if ( menuItem.getItemId () == R.id.action_basket ) {
    		// Declare and initialize a list used to host the valid movements
    		ArrayList < Movement > validMovements = new ArrayList < Movement > ();
    		// Iterate over all movements
    		for ( Movement movement : movements ) {
    			// Check if the current movement has valid quantities
    			if ( movement.getQuantityBig () != 0 || movement.getQuantityMedium () != 0 || movement.getQuantitySmall () != 0 )
    				// The movement contains at least one quantity, add it to the movement list
    				validMovements.add ( movement );
            } // End for each
    		// Check if there is at least one valid movement
    		if ( validMovements.isEmpty () ) {
				// Indicate that no movement is valid
				Baguette.showText ( MovementDetailsActivity.this ,
						AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this , R.string.no_movement_message ) ,
						Baguette.BackgroundColor.RED );
        		// Consume event
        		return true;
    		} // End if
    		// Otherwise there is at least one movement
    		// Set the new request code
    		requestCode = REQUEST_CODE_INFO;
    		// Refresh the action bar
    		invalidateOptionsMenu ();
    		// Set the selected movements list
    		selectedMovements = validMovements;
			// Initialize the movement details
			initializeDetails ();
			
			// Declare and initialize a multiple list adapter
			MultipleListAdapter adapter = new MultipleListAdapter ( this );
			// Add the movement details adapter
			adapter.addSection ( new Section ( AppResources.getInstance ( this ).getString ( this , R.string.movement_details_label ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
					new MovementInfoAdapter ( this , R.layout.order_info_item , details ) );
			// Add the movement adapter
			adapter.addSection ( new Section ( AppResources.getInstance ( this ).getString ( this , R.string.movement_products_label ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
					getMovementDetailsAdapter ( R.layout.sales_order_details_activity_item , selectedMovements , currency.getCurrencyRounding () , ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) ) );
			// Set the list adapter
			setListAdapter ( adapter );

    		// Consume event
    		return true;
    	} // End else if
    	// Determine if the action save is selected
    	else if ( menuItem.getItemId () == R.id.action_save ) {
			// Save current movement
    		saveMovement ();
    		// Consume event
    		return true;
    	} // End else if
    	// Determine if the action print is selected
    	else if ( menuItem.getItemId () == R.id.action_print ) {
    		// Retrieve the movement settings
    		MovementSettings movementSetting = (MovementSettings) getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS );
    		// Determine the printing type
    		int type = -1;
    		if ( movementSetting.getMovementType () == MovementHeadersUtils.Type.LOAD )
    			type = PrintingActivity.Type.LOAD;
    		else if ( movementSetting.getMovementType () == MovementHeadersUtils.Type.LOAD_REQUEST )
    			type = PrintingActivity.Type.MOVEMENTLoadRequest;
    		else if ( movementSetting.getMovementType () == MovementHeadersUtils.Type.UNLOAD )
    			type = PrintingActivity.Type.UNLOAD;
    		else if ( movementSetting.getMovementType () == MovementHeadersUtils.Type.UNLOAD_REQUEST )
    			type = PrintingActivity.Type.UNLOADREQUEST;
    		else if ( movementSetting.getMovementType () == MovementHeadersUtils.Type.STOCK_MANAGEMENT )
    			type = PrintingActivity.Type.VANMANAGMENT;
			else if ( movementSetting.getMovementType () == MovementHeadersUtils.Type.VAN_STOCK_COUNT )
    			type = PrintingActivity.Type.VANSTOCKCOUNT;
			else if ( movementSetting.getMovementType () == MovementHeadersUtils.Type.STOCK_RECONCILIATION )
    			type = PrintingActivity.Type.STOCKRECONCILIATION;
			else if ( movementSetting.getMovementType () == MovementHeadersUtils.Type.VAN_TRANSFER )
				type = PrintingActivity.Type.VANTRANSFER;
			else if ( movementSetting.getMovementType () == MovementHeadersUtils.Type.Physical_Direct_Load )
				type = PrintingActivity.Type.DirectLoad;
			else if ( movementSetting.getMovementType () == MovementHeadersUtils.Type.PHYSICAL_DIRECT_UNLOAD )
				type = PrintingActivity.Type.DirectUnload;
    		// Check if the type is valid
    		if ( type < 0 )
    			// Invalid type
    			return true;
    		// Get a final reference
    		final int printingType = type;
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
								MovementSettings movementSetting = (MovementSettings) getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS );
								Intent intent = new Intent ( MovementDetailsActivity.this, PrintingActivity.class );
								intent.putExtra ( PrintingActivity.TYPE , printingType );
								intent.putExtra ( PrintingActivity.PRINTOUT , PrintingActivity.Printout.COPY );
								ArrayList < MovementHeaders > headers = new ArrayList < MovementHeaders > ();
								ArrayList < MovementDetails > details = null;
								int requestCode = REQUEST_CODE_PRINT;
								if ( ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) && ( movementSetting.getMovementType () == MovementHeadersUtils.Type.LOAD || movementSetting.getMovementType () == MovementHeadersUtils.Type.UNLOAD )  ) {
									headers.add ( getHeaderPrePrint () );
									details = getDetailsPrePrint ();
									requestCode = REQUEST_CODE_PRE_PRINT;
								}
								if(! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) && movementSetting.getMovementType () == MovementHeadersUtils.Type.STOCK_RECONCILIATION )
								{
									headers.add ( getHeaderPrePrintStockReconciliation () );
									details = getDetailsPrePrintStockReconciliation ();
									requestCode = REQUEST_CODE_PRE_PRINT;	
								}
								else
								headers.add ( movementHeader );
								intent.putExtra ( PrintingActivity.HEADER , headers );
								if ( details != null )
									intent.putExtra ( PrintingActivity.DETAILS , details );
								startActivityForResult ( intent , requestCode );
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
    	// Allow normal menu processing to proceed
    	return false;
    }
    
    
    
    /**
     * Computes and returns a pre print header object.
     * 
     * @return	{@link me.SyncWise.Android.Database.MovementHeaders MovementHeaders} object used for pre print.
     */
    private MovementHeaders getHeaderPrePrintStockReconciliation () {
       	// Compute the movement details
    	double calValue = 0;
    	double grossAmount = 0;
    	double taxes = 0;
    	double totalValue = 0;
    	// Iterate over all the selected movements
    	for ( Movement movement : selectedMovements ) {
    		// Compute the current net value
    		grossAmount += movement.getQuantityBig () * movement.getPriceBig () + movement.getQuantityMedium () * movement.getPriceMedium () + movement.getQuantitySmall () * movement.getPriceSmall ();
    		// Compute the value after applying the discount percentage and amount
			double _calValue = ( movement.getQuantityBig () * movement.getPriceBig () + movement.getQuantityMedium () * movement.getPriceMedium ()
					+ movement.getQuantitySmall () * movement.getPriceSmall () );
			// Compute the current tax value
			double _taxes = 0;
			// Check if the item is taxable
			if ( movement.getTax () != 0 )
				// Compute the current taxes
				_taxes = _calValue * movement.getTax () / 100;
			// Compute the current total value
			double _totalValue = _calValue + _taxes;
			// Update all the values
			calValue += _calValue;
			taxes += _taxes;
			totalValue += _totalValue;
    	} // End for each
    	// Compute current time
    	Calendar now = Calendar.getInstance ();
     
		// Declare and initialize a sequence formatter
		DecimalFormat sequence = new DecimalFormat ( "000000" );
		// Retrieve the user object
		Users user = DatabaseUtils.getInstance ( this ).getDaoSession ().getUsersDao ().queryBuilder ()
				.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( this ) ) ,
						UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( this ) ) ).unique ();
		// Retrieve the division code
		String divisionCode = DatabaseUtils.getCurrentDivisionCode ( this );
		// Retrieve the movement settings

		// Retrieve a vehicle
				Vehicles vehicle = DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesDao ().queryBuilder ().limit ( 1 ).unique ();
				
    	
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
		// Determine if there is a previously stored movement
		//String movementHeaderCode = null;
		MovementSettings movementSettings = (MovementSettings) getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS );
		MovementHeaders prePrintHeader = new MovementHeaders();
		if ( movementHeader == null && movementDetails == null ) {
		    		try {
						// Retrieve the movement sequence
						int movementSequence = DatabaseUtils.getUserSequence ( this , user.getUserCode () , user.getCompanyCode () , TransactionSequencesDao.Properties.Movements );
						// Begin transaction
						//DatabaseUtils.getInstance ( MovementDetailsActivity.this ).getDaoSession ().getDatabase ().beginTransaction ();
			    		// Compute the movement header code
			    		movementHeaderCodes = String.valueOf ( movementSettings.getMovementType () ) + String.valueOf ( user.getPrefixID () ) + sequence.format ( movementSequence );
						// Update the movement sequence
					//	DatabaseUtils.setUserSequence ( this , user.getUserCode () , user.getCompanyCode () , TransactionSequencesDao.Properties.Movements , movementSequence + 1 );
						
						// Determine the source, destination and transfer type
						String source = null , destination = null;
						Integer TransferType = null;
						
						// Determine the warehouse keeper code
						String warehouseKeeperCode = null;
						if ( warehouseKeeperEnd != null )
							warehouseKeeperCode = warehouseKeeperEnd.getUserCode ();
						
			    		// TODO : Details of a movement header
			    		// Declare and initialize a movement header
			    		  prePrintHeader = new MovementHeaders ( null , // ID
			    				movementHeaderCodes , // MovementCode
			    				divisionCode , // DivisionCode
			    				user.getCompanyCode () , // CompanyCode
			    				null , // SupervisorCode
			    				warehouseKeeperCode , // WarehouseKeeperCode
			    				user.getUserCode () , // UserCode
			    				null , // WarehouseCode
			    				movementSettings.getMovementType () , // MovementType
			    				now.getTime () , // MovementDate
			    				grossAmount , // GrossAmount
			    				0.0 , // DiscAmount
			    				calValue , // NetAmount
			    				taxes , // TaxAmount
			    				totalValue , // TotalTaxAmount
			    				StatusUtils.isActive () , // MovementStatus
			    				source , // Source
			    				TransferType , // TransferType
			    				destination , // Destination
			    				vehicle.getVehicleCode () , // VehicleCode
			    				null , // MovementReferenceCode
			    				passcode , // PasswordCode
			    				IsProcessedUtils.isNotSync () , // IsProcessed
			    				now.getTime () ); // StampDate
		    		} catch ( Exception exception ) {}
		// Initialize a pre print header
//		MovementHeaders prePrintHeader = new MovementHeaders ( null ,
//				movementHeader.getMovementCode () ,
//				movementHeader.getDivisionCode () ,
//				movementHeader.getCompanyCode () ,
//				movementHeader.getSupervisorCode () ,
//				movementHeader.getWarehouseKeeperCode () ,
//				movementHeader.getUserCode () , 
//				movementHeader.getWarehouseCode () ,
//				movementHeader.getMovementType () , 
//				now.getTime (), 
//				grossAmount ,
//				movementHeader.getDiscAmount () , 
//				calValue , 
//				taxes , 
//				totalValue , 
//				movementHeader.getMovementStatus () , 
//				movementHeader.getSource () , 
//				movementHeader.getTransferType () , 
//				movementHeader.getDestination () , 
//				movementHeader.getVehicleCode () , 
//				movementHeader.getMovementReferenceCode () , 
//				movementHeader.getPasswordCode () , 
//				movementHeader.getIsProcessed () , 
//				movementHeader.getStampDate () );
		}	return prePrintHeader;
    }
    /**
     *  Computes and returns a pre print details list.
     *  
     * @return	{@link me.SyncWise.Android.Database.MovementDetails MovementDetails} list used for pre print.
     */
    private ArrayList < MovementDetails > getDetailsPrePrintStockReconciliation () {
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
		
		// Initialize a list of pre print details
		ArrayList < MovementDetails > prePrintDetails = new ArrayList < MovementDetails > ();
		// Map the selected movements to their item codes
		Map < String , Movement > _selectedMovements = new HashMap < String , Movement > ();
		// Iterate over all selected movements
		for ( Movement movement : selectedMovements ) 
			// Map the current movement to its item code
			_selectedMovements.put ( movement.getItem ().getItemCode () , movement );
	
		// Compute current time
    	Calendar now = Calendar.getInstance ();
    	// Initialize a list of pre print details
	 
    	
		//HashMap < String , MovementDetails > movementDetailss = new HashMap < String , MovementDetails > ();
		for ( int i = 0 ; i < selectedMovements.size () ; i ++ ) {
			// TODO : Details of a movement detail
			// Insert a new movement detail in DB
			MovementDetails movementDetail = new MovementDetails ( null , // ID
					movementHeaderCodes , // MovementCode
					i , // LineID
					selectedMovements.get ( i ).getItem ().getItemCode () , // ItemCode
					(double) selectedMovements.get ( i ).getQuantityBig () , // QuantityBig
					(double) selectedMovements.get ( i ).getQuantityMedium () , // QuantityMedium
					(double) selectedMovements.get ( i ).getQuantitySmall () , // QuantitySmall
					(double) selectedMovements.get ( i ).getQuantityBig () * selectedMovements.get ( i ).getItem ().getUnitBigMedium () * selectedMovements.get ( i ).getItem ().getUnitMediumSmall ()
						+ selectedMovements.get ( i ).getQuantityMedium () * selectedMovements.get ( i ).getItem ().getUnitMediumSmall ()
						+ selectedMovements.get ( i ).getQuantitySmall () , // BasicUnitQuantity
					(double) selectedMovements.get ( i ).getPriceBig () , // PriceBig
					(double) selectedMovements.get ( i ).getPriceMedium () , // PriceMedium
					(double) selectedMovements.get ( i ).getPriceSmall () , // PriceSmall
					(double) selectedMovements.get ( i ).getQuantityBig () , // ApprovedQuantityBig
					(double) selectedMovements.get ( i ).getQuantityMedium () , // ApprovedQuantityMedium
					(double) selectedMovements.get ( i ).getQuantitySmall () , // ApprovedQuantitySmall
					(double) selectedMovements.get ( i ).getQuantityBig () * selectedMovements.get ( i ).getItem ().getUnitBigMedium () * selectedMovements.get ( i ).getItem ().getUnitMediumSmall ()
						+ selectedMovements.get ( i ).getQuantityMedium () * selectedMovements.get ( i ).getItem ().getUnitMediumSmall ()
						+ selectedMovements.get ( i ).getQuantitySmall () , // ApprovedBasicUnitQuantity
					0.0 , // MissedQuantityBig
					(goodQuantities.get ( selectedMovements.get ( i ).getItem ().getItemCode () )==null?0:goodQuantities.get ( selectedMovements.get ( i ).getItem ().getItemCode () ) - (double)  selectedMovements.get ( i ).getQuantityMedium () * selectedMovements.get ( i ).getItem ().getUnitMediumSmall ())/selectedMovements.get ( i ).getItem ().getUnitMediumSmall () , // MissedQuantityMedium
					goodQuantities.get ( selectedMovements.get ( i ).getItem ().getItemCode () )==null?0:goodQuantities.get ( selectedMovements.get ( i ).getItem ().getItemCode () ) - (double) selectedMovements.get ( i ).getQuantitySmall () , // MissedQuantitySmall
					0.0 , // MissedBasicUnitQuantity
					selectedMovements.get ( i ).getQuantityBig () * selectedMovements.get ( i ).getPriceBig () + selectedMovements.get ( i ).getQuantityMedium ()
						* selectedMovements.get ( i ).getPriceMedium () + selectedMovements.get ( i ).getQuantitySmall () * selectedMovements.get ( i ).getPriceSmall () , // TotalLineAmount
					0.0 , // DiscountAmount
					0.0 , // DiscountPercentage
					goodQuantities.get ( selectedMovements.get ( i ).getItem ().getItemCode () )==null?0:goodQuantities.get ( selectedMovements.get ( i ).getItem ().getItemCode () ) , // StockQuantity
					selectedMovements.get ( i ).getItem ().getItemName () , // ItemName
					selectedMovements.get ( i ).getItem ().getItemAltName () , // ItemAltName
					selectedMovements.get ( i ).getReason () == null ? null : selectedMovements.get ( i ).getReason ().getReasonCode () , // ReasonCode
					selectedMovements.get ( i ).getReason () == null ? null : selectedMovements.get ( i ).getReason ().getReasonAffectStock () , // ReasonAffectStock
					null , // ItemLot
					selectedMovements.get ( i ).getExpiryDate () , // ItemExpiryDate
					selectedMovements.get ( i ).getTax () , // ItemTaxPercentage
					now.getTime () ); // StampDate
		        	//DatabaseUtils.getInstance ( this ).getDaoSession ().getMovementDetailsDao ().insert ( movementDetail );
		        	//movementDetailss.put ( movementDetail.getItemCode () , movementDetail );
		        	prePrintDetails.add(movementDetail);
		
	 
//			// Update stock quantity
//			prePrintDetail.setStockQuantity ( goodQuantities.get ( prePrintDetail.getItemCode () ) );
//			if ( prePrintDetail.getStockQuantity () == null )
//				prePrintDetail.setStockQuantity ( 0.0 );
//			// Check if the current detail is selected
//			if ( _selectedMovements.containsKey ( prePrintDetail.getItemCode () ) ) {
//				Movement movement = _selectedMovements.get ( prePrintDetail.getItemCode () );
//				prePrintDetail.setQuantityBig ( (double) movement.getQuantityBig () );
//				prePrintDetail.setQuantityMedium ( (double) movement.getQuantityMedium () );
//				prePrintDetail.setQuantitySmall ( (double) movement.getQuantitySmall () );
//				prePrintDetail.setBasicUnitQuantity ( (double) movement.getQuantityBig () * movement.getItem ().getUnitBigMedium () * movement.getItem ().getUnitMediumSmall ()
//						+ movement.getQuantityMedium () * movement.getItem ().getUnitMediumSmall ()
//						+ movement.getQuantitySmall () );
//				prePrintDetail.setMissedQuantityBig ( prePrintDetail.getApprovedQuantityBig () - prePrintDetail.getQuantityBig () );
//				prePrintDetail.setMissedQuantityMedium ( prePrintDetail.getApprovedQuantityMedium () - prePrintDetail.getQuantityMedium () );
//				prePrintDetail.setMissedQuantitySmall ( prePrintDetail.getApprovedQuantitySmall () - prePrintDetail.getQuantitySmall () );
//				prePrintDetail.setMissedBasicUnitQuantity ( prePrintDetail.getApprovedBasicUnitQuantity () - prePrintDetail.getBasicUnitQuantity () );
//				prePrintDetail.setTotalLineAmount ( movement.getQuantityBig () * movement.getPriceBig () + movement.getQuantityMedium ()
//							* movement.getPriceMedium () + movement.getQuantitySmall () * movement.getPriceSmall () );
//			} // End if
//			else {
//				// Reset quantities and set missed quantities
//				prePrintDetail.setQuantityBig ( 0.0 );
//				prePrintDetail.setQuantityMedium ( 0.0 );
//				prePrintDetail.setQuantitySmall ( 0.0 );
//				prePrintDetail.setBasicUnitQuantity ( 0.0 );
//				prePrintDetail.setMissedQuantityBig ( prePrintDetail.getApprovedQuantityBig () );
//				prePrintDetail.setMissedQuantityMedium ( prePrintDetail.getApprovedQuantityMedium () );
//				prePrintDetail.setMissedQuantitySmall ( prePrintDetail.getApprovedQuantitySmall () );
//				prePrintDetail.setMissedBasicUnitQuantity ( prePrintDetail.getApprovedBasicUnitQuantity () );
//				prePrintDetail.setTotalLineAmount ( 0.0 );
//			} // End if
			// Add the pre print detail to the list
		//	prePrintDetails.add ( prePrintDetail );
		} // End for each
		// Return the pre print details list
    	return prePrintDetails;
    }   
    
    
    /**
     * Computes and returns a pre print header object.
     * 
     * @return	{@link me.SyncWise.Android.Database.MovementHeaders MovementHeaders} object used for pre print.
     */
    private MovementHeaders getHeaderPrePrint () {
       	// Compute the movement details
    	double calValue = 0;
    	double grossAmount = 0;
    	double taxes = 0;
    	double totalValue = 0;
    	// Iterate over all the selected movements
    	for ( Movement movement : selectedMovements ) {
    		// Compute the current net value
    		grossAmount += movement.getQuantityBig () * movement.getPriceBig () + movement.getQuantityMedium () * movement.getPriceMedium () + movement.getQuantitySmall () * movement.getPriceSmall ();
    		// Compute the value after applying the discount percentage and amount
			double _calValue = ( movement.getQuantityBig () * movement.getPriceBig () + movement.getQuantityMedium () * movement.getPriceMedium ()
					+ movement.getQuantitySmall () * movement.getPriceSmall () );
			// Compute the current tax value
			double _taxes = 0;
			// Check if the item is taxable
			if ( movement.getTax () != 0 )
				// Compute the current taxes
				_taxes = _calValue * movement.getTax () / 100;
			// Compute the current total value
			double _totalValue = _calValue + _taxes;
			// Update all the values
			calValue += _calValue;
			taxes += _taxes;
			totalValue += _totalValue;
    	} // End for each
    	// Compute current time
    	Calendar now = Calendar.getInstance ();
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
		// Initialize a pre print header
		MovementHeaders prePrintHeader = new MovementHeaders ( null ,
				movementHeader.getMovementCode () ,
				movementHeader.getDivisionCode () ,
				movementHeader.getCompanyCode () ,
				movementHeader.getSupervisorCode () ,
				movementHeader.getWarehouseKeeperCode () ,
				movementHeader.getUserCode () , 
				movementHeader.getWarehouseCode () ,
				movementHeader.getMovementType () , 
				now.getTime (), 
				grossAmount ,
				movementHeader.getDiscAmount () , 
				calValue , 
				taxes , 
				totalValue , 
				movementHeader.getMovementStatus () , 
				movementHeader.getSource () , 
				movementHeader.getTransferType () , 
				movementHeader.getDestination () , 
				movementHeader.getVehicleCode () , 
				movementHeader.getMovementReferenceCode () , 
				movementHeader.getPasswordCode () , 
				movementHeader.getIsProcessed () , 
				movementHeader.getStampDate () );
    	return prePrintHeader;
    }
    
    /**
     *  Computes and returns a pre print details list.
     *  
     * @return	{@link me.SyncWise.Android.Database.MovementDetails MovementDetails} list used for pre print.
     */
    private ArrayList < MovementDetails > getDetailsPrePrint () {
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
		
		// Initialize a list of pre print details
		ArrayList < MovementDetails > prePrintDetails = new ArrayList < MovementDetails > ();
		// Map the selected movements to their item codes
		Map < String , Movement > _selectedMovements = new HashMap < String , Movement > ();
		// Iterate over all selected movements
		for ( Movement movement : selectedMovements ) 
			// Map the current movement to its item code
			_selectedMovements.put ( movement.getItem ().getItemCode () , movement );
		// Iterate over all movement details
		for ( MovementDetails movementDetail : movementDetails ) {
			// Initialize the pre print detail
			MovementDetails prePrintDetail = new MovementDetails ( null ,
					movementDetail.getMovementCode () , 
					movementDetail.getLineID () , 
					movementDetail.getItemCode () , 
					movementDetail.getQuantityBig () , 
					movementDetail.getQuantityMedium () , 
					movementDetail.getQuantitySmall () , 
					movementDetail.getBasicUnitQuantity () , 
					movementDetail.getPriceBig () , 
					movementDetail.getPriceMedium () , 
					movementDetail.getPriceSmall () , 
					movementDetail.getApprovedQuantityBig () , 
					movementDetail.getApprovedQuantityMedium () , 
					movementDetail.getApprovedQuantitySmall () , 
					movementDetail.getApprovedBasicUnitQuantity () , 
					movementDetail.getMissedQuantityBig () , 
					movementDetail.getMissedQuantityMedium () , 
					movementDetail.getMissedQuantitySmall () , 
					movementDetail.getMissedBasicUnitQuantity () , 
					movementDetail.getTotalLineAmount () , 
					movementDetail.getDiscountAmount () , 
					movementDetail.getDiscountPercentage () , 
					movementDetail.getStockQuantity () , 
					movementDetail.getItemName () , 
					movementDetail.getItemAltName () , 
					movementDetail.getReasonCode () , 
					movementDetail.getReasonAffectStock () , 
					movementDetail.getItemLot () , 
					movementDetail.getItemExpiryDate () , 
					movementDetail.getItemTaxPercentage () , 
					movementDetail.getStampDate () );
			// Update stock quantity
			prePrintDetail.setStockQuantity ( goodQuantities.get ( prePrintDetail.getItemCode () ) );
			if ( prePrintDetail.getStockQuantity () == null )
				prePrintDetail.setStockQuantity ( 0.0 );
			// Check if the current detail is selected
			if ( _selectedMovements.containsKey ( prePrintDetail.getItemCode () ) ) {
				Movement movement = _selectedMovements.get ( prePrintDetail.getItemCode () );
				prePrintDetail.setQuantityBig ( (double) movement.getQuantityBig () );
				prePrintDetail.setQuantityMedium ( (double) movement.getQuantityMedium () );
				prePrintDetail.setQuantitySmall ( (double) movement.getQuantitySmall () );
				prePrintDetail.setBasicUnitQuantity ( (double) movement.getQuantityBig () * movement.getItem ().getUnitBigMedium () * movement.getItem ().getUnitMediumSmall ()
						+ movement.getQuantityMedium () * movement.getItem ().getUnitMediumSmall ()
						+ movement.getQuantitySmall () );
				prePrintDetail.setMissedQuantityBig ( prePrintDetail.getApprovedQuantityBig () - prePrintDetail.getQuantityBig () );
				prePrintDetail.setMissedQuantityMedium ( prePrintDetail.getApprovedQuantityMedium () - prePrintDetail.getQuantityMedium () );
				prePrintDetail.setMissedQuantitySmall ( prePrintDetail.getApprovedQuantitySmall () - prePrintDetail.getQuantitySmall () );
				prePrintDetail.setMissedBasicUnitQuantity ( prePrintDetail.getApprovedBasicUnitQuantity () - prePrintDetail.getBasicUnitQuantity () );
				prePrintDetail.setTotalLineAmount ( movement.getQuantityBig () * movement.getPriceBig () + movement.getQuantityMedium ()
							* movement.getPriceMedium () + movement.getQuantitySmall () * movement.getPriceSmall () );
			} // End if
			else {
				// Reset quantities and set missed quantities
				prePrintDetail.setQuantityBig ( 0.0 );
				prePrintDetail.setQuantityMedium ( 0.0 );
				prePrintDetail.setQuantitySmall ( 0.0 );
				prePrintDetail.setBasicUnitQuantity ( 0.0 );
				prePrintDetail.setMissedQuantityBig ( prePrintDetail.getApprovedQuantityBig () );
				prePrintDetail.setMissedQuantityMedium ( prePrintDetail.getApprovedQuantityMedium () );
				prePrintDetail.setMissedQuantitySmall ( prePrintDetail.getApprovedQuantitySmall () );
				prePrintDetail.setMissedBasicUnitQuantity ( prePrintDetail.getApprovedBasicUnitQuantity () );
				prePrintDetail.setTotalLineAmount ( 0.0 );
			} // End if
			// Add the pre print detail to the list
			prePrintDetails.add ( prePrintDetail );
		} // End for each
		// Return the pre print details list
    	return prePrintDetails;
    }
    
    /**
     * Determines if the current movement can be saved.
     * 
     * @param movementSettings	The movement settings currently applied.
     * @param user	The current user.
     * @param vehicle	The user's vehicle.
     * @param totalValue	The movement's total value.
     * @return	Boolean indicating if the current movement can be saved.
     */
    protected boolean canSave ( MovementSettings movementSettings , Users user , Vehicles vehicle , double totalValue ) {
    	
    	
    		
    	// Check if the movement type is either load or unload request
    	if ( warehouseKeeperEnd == null &&
    			( movementSettings.getMovementType () == MovementHeadersUtils.Type.LOAD 
    				|| movementSettings.getMovementType () == MovementHeadersUtils.Type.UNLOAD
    				|| movementSettings.getMovementType () == MovementHeadersUtils.Type.STOCK_RECONCILIATION ) ) {
    		
     if( !PermissionsUtils.getRemoveWareHouseKeeper ( MovementDetailsActivity.this , user.getUserCode () , user.getCompanyCode () ))
        {
    		// Set flag
    		displayWarehouseKeeperApproval = true;
    		// Initialize the quaternary view
    		initializeQuaternaryView ( APPROVAL_TYPE_WAREHOUSE , false );
    		// Disable the main list
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( false );
    		// Retrieve a reference to the quaternary view
    		View quaternaryView = findViewById ( R.id.layout_warehouse_keeper_approval );
    		// Display the quaternary view
    		quaternaryView.setVisibility ( View.VISIBLE );
    		// Animate the quaternary view
    		quaternaryView.startAnimation ( getViewAnimationIn() );
    		// Refresh the action bar
    		invalidateOptionsMenu ();
    		// Exit method
    		return false;
    		}
     } // End if
    	// Check if the movement type is van transfer
    	if ( movementSettings.getMovementType () == MovementHeadersUtils.Type.VAN_TRANSFER && cashVan == null ) {
    		// Set flag
    		displayCashVanApproval = true;
    		// Initialize the quaternary view
    		initializeQuaternaryView ( APPROVAL_TYPE_CASH_VAN , false );
    		// Disable the main list
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( false );
    		// Retrieve a reference to the quaternary view
    		View quaternaryView = findViewById ( R.id.layout_warehouse_keeper_approval );
    		// Display the quaternary view
    		quaternaryView.setVisibility ( View.VISIBLE );
    		// Animate the quaternary view
    		quaternaryView.startAnimation ( getViewAnimationIn() );
    		// Refresh the action bar
    		invalidateOptionsMenu ();
    		// Exit method
    		return false;
    	} // End if
   
    	// Check if the movement type is van transfer
    	if (warehouseKeeperEnd == null &&( movementSettings.getMovementType () == MovementHeadersUtils.Type.Physical_Direct_Load || movementSettings.getMovementType () == MovementHeadersUtils.Type.PHYSICAL_DIRECT_UNLOAD) ) {
    		// Set flag
    		displayWarehouseKeeperApproval = true;
    		// Initialize the quaternary view
    		initializeQuaternaryView ( APPROVAL_TYPE_WAREHOUSE , false );
    		// Disable the main list
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( false );
    		// Retrieve a reference to the quaternary view
    		View quaternaryView = findViewById ( R.id.layout_warehouse_keeper_approval );
    		// Display the quaternary view
    		quaternaryView.setVisibility ( View.VISIBLE );
    		// Animate the quaternary view
    		quaternaryView.startAnimation ( getViewAnimationIn() );
    		// Refresh the action bar
    		invalidateOptionsMenu ();
    		// Exit method
    		return false;
    	} // End if
    	// Check if the movement type is van transfer
    	if ( movementSettings.getMovementType () == MovementHeadersUtils.Type.VAN_TRANSFER && passcode == null ) {
    		// Set flag
    		displayPasscode = true;
    		// Initialize the tertiary view
    		initializeTertiaryView ( AppResources.getInstance ( this ).getString ( this , R.string.movement_passcode_required_message ) );
    		// Disable the main list
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( false );
    		// Retrieve a reference to the tertiary view
    		View tertiaryView = findViewById ( R.id.layout_passcode );
    		// Display the tertiary view
    		tertiaryView.setVisibility ( View.VISIBLE );
    		// Animate the tertiary view
    		tertiaryView.startAnimation ( getViewAnimationIn() );
    		// Refresh the action bar
    		invalidateOptionsMenu ();
    		// Exit method
    		return false;
    	} // End if
		// Check if the movement type is load request
		if ( movementSettings.getMovementType () == MovementHeadersUtils.Type.LOAD_REQUEST && passcode == null ) {
			boolean volumeLimitExceeded = false;
			boolean priceLimitExceeded = false;
			String SQL = "select SUM( case UnitMediumSmall when  1 then (COALESCE(GoodQuantity,0) + COALESCE(DamageQuantity,0)) * " +
					" COALESCE(SmallVolumeCM,0)  else   ((((( cast(COALESCE(goodquantity,0) as integer)  +  " +
					" cast(COALESCE(DamageQuantity,0) as integer)) - ((cast(COALESCE(goodquantity,0) as integer) +  " +
					" cast(COALESCE(DamageQuantity,0) as integer))% cast(COALESCE(UnitMediumSmall,0) as integer))) /  " +
					" COALESCE(UnitMediumSmall,0)) * COALESCE(MediumVolumeCM,0))   + (((cast(COALESCE(goodquantity,0) as integer) + " +
					" cast(COALESCE(DamageQuantity,0) as integer))% cast(COALESCE(UnitMediumSmall,0) as integer)) *  " +
					" COALESCE(SmallVolumeCM,0))) end) as Volumes, SUM( case UnitMediumSmall when  1 then (COALESCE(GoodQuantity,0) + " +
					" COALESCE(DamageQuantity,0)) * COALESCE(PriceSmall,0) else   ((((( cast(COALESCE(goodquantity,0) as integer) +  " +
					" cast(COALESCE(DamageQuantity,0) as integer)) - ((cast(COALESCE(goodquantity,0) as integer)+cast(COALESCE(DamageQuantity,0) as integer))%  " +
					" cast(COALESCE(UnitMediumSmall,0) as integer))) / COALESCE(UnitMediumSmall,0)) * COALESCE(priceMedium,0))  +  " +
					" (((cast(COALESCE(goodquantity,0) as integer) + " +
					" cast(COALESCE(DamageQuantity,0) as integer))% cast(COALESCE(UnitMediumSmall,0) as integer)) * COALESCE(PriceSmall,0))) end) as Prices  " +
					" from (select vehiclesstock.ItemCode , ( case when VehiclesStock.GoodQuantity < 0 then 0 else vehiclesstock.goodquantity end  + COALESCE(movementdetails.goodquantity,0)) as goodquantity, " +
					" case when VehiclesStock.DamageQuantity < 0 then 0 else vehiclesstock.DamageQuantity end as DamageQuantity from VehiclesStock " +
					" left join  ( select MovementDetails.itemcode , sum(BasicUnitQuantity) as GoodQuantity , 0 as damageQuantity from MovementDetails  " +
					" inner join   MovementHeaders on MovementHeaders.MovementCode = MovementDetails.MovementCode and  " +
					" movementheaders.MovementType = '" + MovementHeadersUtils.Type.LOAD_REQUEST + "'  and movementheaders.movementcode  " +
					" not in (select movementheaders.MovementReferenceCode from  MovementHeaders " +
					" where movementheaders.MovementType ='" + MovementHeadersUtils.Type.LOAD + "' and movementheaders.IsProcessed in(2,3)) " +
					" group by MovementDetails.ItemCode) as MOvementDetails on  VehiclesStock.ItemCode = MovementDetails.ItemCode   " +
					"    union all " +
					" select MovementDetails.itemcode , sum(BasicUnitQuantity) as GoodQuantity , 0 as damageQuantity from MovementDetails " +
					" inner join   MovementHeaders on MovementHeaders.MovementCode = MovementDetails.MovementCode and  " +
					" movementheaders.MovementType = '" + MovementHeadersUtils.Type.LOAD_REQUEST + "'  and movementheaders.movementcode  " +
					" not in (select movementheaders.MovementReferenceCode from  MovementHeaders " +
					" where movementheaders.MovementType ='" + MovementHeadersUtils.Type.LOAD + "' and movementheaders.IsProcessed in(2,3)) " +
					" where Movementdetails.ItemCode not in (select ItemCode from VehiclesStock) " +
					" group by MovementDetails.ItemCode) as VehiclesStock " +
					" left join ItemVolumes on ItemVolumes.ItemCode = VehiclesStock.itemcode  " +
					" left join Prices on prices.ItemCode = VehiclesStock.ItemCode and " +
					" prices.PriceListCode =  '" + PermissionsUtils.getDefaultPriceListCode ( MovementDetailsActivity.this , user.getUserCode () , user.getCompanyCode () ) + "' " +
					" inner join items on Items.ItemCode = VehiclesStock.ItemCode " +
					" where DamageQuantity > 0 or GoodQuantity > 0 ";
			Cursor cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().rawQuery ( SQL , new String [] {} );
			cursor.moveToFirst ();
			double vehicleLoad = cursor.getDouble ( 0 );
			double vehiclePrice = cursor.getDouble ( 1 );
			cursor.close ();
			cursor = null;
			double loadLimit = 0.0;
			double currentLoad = 0.0;
			if ( vehicle.getLoadLimit () != null ) {
				loadLimit = vehicle.getLoadLimit () * PermissionsUtils.getVehicleLoadPercentage ( this , user.getUserCode () , user.getCompanyCode () ) / 100;
				for ( int i = 0 ; i < selectedMovements.size () ; i ++ ) {
					ItemVolumes itemVolume = DatabaseUtils.getInstance ( this ).getDaoSession ().getItemVolumesDao ().queryBuilder ()
							.where ( ItemVolumesDao.Properties.ItemCode.eq ( selectedMovements.get ( i ).getItem ().getItemCode () ) ,
									ItemVolumesDao.Properties.CompanyCode.eq ( selectedMovements.get ( i ).getItem ().getCompanyCode () ) ).unique ();
					if ( itemVolume != null )
						currentLoad += selectedMovements.get ( i ).getQuantityBig () * ( itemVolume.getBigVolumeCM () == null || itemVolume.getBigVolumeCM () < 0 ? 0.0 : itemVolume.getBigVolumeCM () )
										+ selectedMovements.get ( i ).getQuantityMedium () * ( itemVolume.getMediumVolumeCM () == null || itemVolume.getMediumVolumeCM () < 0 ? 0.0 : itemVolume.getMediumVolumeCM () )
										+ selectedMovements.get ( i ).getQuantitySmall () *  ( itemVolume.getSmallVolumeCM () == null || itemVolume.getSmallVolumeCM () < 0 ? 0.0 : itemVolume.getSmallVolumeCM () );
				}
				if ( vehicleLoad + currentLoad > loadLimit )
					volumeLimitExceeded = true;
			}
			UserLimits userLimit = DatabaseUtils.getInstance ( this ).getDaoSession ().getUserLimitsDao ().queryBuilder ()
					.where ( UserLimitsDao.Properties.UserCode.eq ( user.getUserCode () ) ,
							UserLimitsDao.Properties.CompanyCode.eq ( user.getCompanyCode () ) ,
							UserLimitsDao.Properties.CurrencyCode.eq ( currency.getCurrencyCode () ) ).unique ();
			if ( userLimit != null && userLimit.getUserCreditLimit () >= 0 && ( vehiclePrice + totalValue ) > userLimit.getUserCreditLimit () )
				priceLimitExceeded = true;
			if ( volumeLimitExceeded || priceLimitExceeded ) {
				// Build the message string
				String message = null;
				if ( volumeLimitExceeded ) {
					// Declare and initialize a two digit formator
					DecimalFormat twoDigit = new DecimalFormat ( " #,##0.00" );
					message = AppResources.getInstance ( this ).getString ( this , R.string.movement_load_limit_exceeded_message ) + twoDigit.format ( vehicleLoad + currentLoad - loadLimit );
				}
				else
					message = "";
				if ( volumeLimitExceeded && priceLimitExceeded )
					message += "\n";
				if ( priceLimitExceeded ) {
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
					message += AppResources.getInstance ( this ).getString ( this , R.string.movement_price_limit_exceeded_message ) + moneyFormat.format ( vehiclePrice + totalValue - userLimit.getUserCreditLimit () ) + " " + currency.getCurrencySymbol ();
				}
				message += "\n" + AppResources.getInstance ( this ).getString ( this , R.string.movement_passcode_required_message );
	    		// Set flag
	    		displayPasscode = true;
	    		// Initialize the tertiary view
	    		initializeTertiaryView ( message );
	    		// Disable the main list
	    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( false );
	    		// Retrieve a reference to the tertiary view
	    		View tertiaryView = findViewById ( R.id.layout_passcode );
	    		// Display the tertiary view
	    		tertiaryView.setVisibility ( View.VISIBLE );
	    		// Animate the tertiary view
	    		tertiaryView.startAnimation ( getViewAnimationIn() );
	    		// Refresh the action bar
	    		invalidateOptionsMenu ();
	    		// Exit method
	    		return false;
			}
		} // End if
		// Check if the movement type is load request
		if ( movementSettings.getMovementType () == MovementHeadersUtils.Type.STOCK_RECONCILIATION ) {
			if ( DatabaseUtils.getInstance ( this ).getDaoSession ().getUserAccountNumbersDao ().count () == 0 ) {
    			// Warn user
				Baguette.showText ( this , AppResources.getInstance ( this ).getString ( this , R.string.movement_missing_user_account_message ) , Baguette.BackgroundColor.RED );
	    		// Exit method
	    		return false;
			} // End if
		} // End if
		return true;
    }
    
    /**
     * Saves the current movement.
     */
    protected void saveMovement () {
		// Check if there is a previously stored movement
		if ( movementHeader != null && movementDetails != null )
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
			Baguette.showText ( this , AppResources.getInstance ( this ).getString ( this , R.string.movement_no_vehicle_message ) , Baguette.BackgroundColor.RED );
    		// Exit method
    		return;
    	} // End if
		// Otherwise the movement can be saved
		// Flag used to indicate whether an error occurred or not
		error = false;
    	// Compute the movement details
    	double calValue = 0;
    	double grossAmount = 0;
    	double taxes = 0;
    	double totalValue = 0;
    	// Iterate over all the selected movements
    	for ( Movement movement : selectedMovements ) {
    		// Compute the current net value
    		grossAmount += movement.getQuantityBig () * movement.getPriceBig () + movement.getQuantityMedium () * movement.getPriceMedium () + movement.getQuantitySmall () * movement.getPriceSmall ();
    		// Compute the value after applying the discount percentage and amount
			double _calValue = ( movement.getQuantityBig () * movement.getPriceBig () + movement.getQuantityMedium () * movement.getPriceMedium ()
					+ movement.getQuantitySmall () * movement.getPriceSmall () );
			// Compute the current tax value
			double _taxes = 0;
			// Check if the item is taxable
			if ( movement.getTax () != 0 )
				// Compute the current taxes
				_taxes = _calValue * movement.getTax () / 100;
			// Compute the current total value
			double _totalValue = _calValue + _taxes;
			// Update all the values
			calValue += _calValue;
			taxes += _taxes;
			totalValue += _totalValue;
    	} // End for each
    	// Compute current time
    	Calendar now = Calendar.getInstance ();
		// Declare and initialize a sequence formatter
		DecimalFormat sequence = new DecimalFormat ( "000000" );
		// Retrieve the user object
		Users user = DatabaseUtils.getInstance ( this ).getDaoSession ().getUsersDao ().queryBuilder ()
				.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( this ) ) ,
						UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( this ) ) ).unique ();
		// Retrieve the division code
		String divisionCode = DatabaseUtils.getCurrentDivisionCode ( this );
		// Retrieve the movement settings
		MovementSettings movementSettings = (MovementSettings) getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS );
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
		// Declare and initialize a string used to host the movement code used
		String movementHeaderCode = null;
		
		// Check if the current movement can be saved
		if ( ! canSave ( movementSettings , user , vehicle , totalValue ) )
			// Exit method
			return;
		
		// Determine if there is a previously stored movement
		if ( movementHeader == null && movementDetails == null ) {
    		try {
				// Retrieve the movement sequence
				int movementSequence = DatabaseUtils.getUserSequence ( this , user.getUserCode () , user.getCompanyCode () , TransactionSequencesDao.Properties.Movements );
				// Begin transaction
				DatabaseUtils.getInstance ( MovementDetailsActivity.this ).getDaoSession ().getDatabase ().beginTransaction ();
	    		// Compute the movement header code
	    		movementHeaderCode = String.valueOf ( movementSettings.getMovementType () == MovementHeadersUtils.Type.Physical_Direct_Load ? 2 : movementSettings.getMovementType () == MovementHeadersUtils.Type.PHYSICAL_DIRECT_UNLOAD?3:movementSettings.getMovementType ()) + String.valueOf ( user.getPrefixID () ) + sequence.format ( movementSequence );
				// Update the movement sequence
				DatabaseUtils.setUserSequence ( this , user.getUserCode () , user.getCompanyCode () , TransactionSequencesDao.Properties.Movements , movementSequence + 1 );
				
				// Determine the source, destination and transfer type
				String source = null , destination = null;
				Integer TransferType = null;
				if ( movementSettings.getMovementType () == MovementHeadersUtils.Type.VAN_TRANSFER ) {
					source = vanType == VAN_TYPE_IN ? cashVan.getUserCode () : user.getUserCode ();
					destination = vanType == VAN_TYPE_OUT ? cashVan.getUserCode () : user.getUserCode ();
					TransferType = vanType;
				}
				// Determine the warehouse keeper code
				String warehouseKeeperCode = null;
				if ( warehouseKeeperEnd != null )
					warehouseKeeperCode = warehouseKeeperEnd.getUserCode ();
				
	    		// TODO : Details of a movement header
	    		// Declare and initialize a movement header
	    		MovementHeaders movementHeader = new MovementHeaders ( null , // ID
	    				movementHeaderCode , // MovementCode
	    				divisionCode , // DivisionCode
	    				user.getCompanyCode () , // CompanyCode
	    				null , // SupervisorCode
	    				warehouseKeeperCode , // WarehouseKeeperCode
	    				user.getUserCode () , // UserCode
	    				null , // WarehouseCode
	    				movementSettings.getMovementType () == MovementHeadersUtils.Type.Physical_Direct_Load?2:movementSettings.getMovementType () == MovementHeadersUtils.Type.PHYSICAL_DIRECT_UNLOAD?3:movementSettings.getMovementType (), // MovementType
	    				now.getTime () , // MovementDate
	    				grossAmount , // GrossAmount
	    				0.0 , // DiscAmount
	    				calValue , // NetAmount
	    				taxes , // TaxAmount
	    				totalValue , // TotalTaxAmount
	    				StatusUtils.isActive () , // MovementStatus
	    				source , // Source
	    				TransferType , // TransferType
	    				destination , // Destination
	    				vehicle.getVehicleCode () , // VehicleCode
	    				null , // MovementReferenceCode
	    				passcode , // PasswordCode
	    				IsProcessedUtils.isNotSync () , // IsProcessed
	    				now.getTime () ); // StampDate
	    		// Insert the movement header in DB
	    		DatabaseUtils.getInstance ( this ).getDaoSession ().getMovementHeadersDao ().insert ( movementHeader );
	    		// Iterate over all selected movements
	    		HashMap < String , MovementDetails > movementDetails = new HashMap < String , MovementDetails > ();
				for ( int i = 0 ; i < selectedMovements.size () ; i ++ ) {
					// TODO : Details of a movement detail
					// Insert a new movement detail in DB
					MovementDetails movementDetail = new MovementDetails ( null , // ID
							movementHeaderCode , // MovementCode
							i , // LineID
							selectedMovements.get ( i ).getItem ().getItemCode () , // ItemCode
							(double) selectedMovements.get ( i ).getQuantityBig () , // QuantityBig
							(double) selectedMovements.get ( i ).getQuantityMedium () , // QuantityMedium
							(double) selectedMovements.get ( i ).getQuantitySmall () , // QuantitySmall
							(double) selectedMovements.get ( i ).getQuantityBig () * selectedMovements.get ( i ).getItem ().getUnitBigMedium () * selectedMovements.get ( i ).getItem ().getUnitMediumSmall ()
								+ selectedMovements.get ( i ).getQuantityMedium () * selectedMovements.get ( i ).getItem ().getUnitMediumSmall ()
								+ selectedMovements.get ( i ).getQuantitySmall () , // BasicUnitQuantity
							(double) selectedMovements.get ( i ).getPriceBig () , // PriceBig
							(double) selectedMovements.get ( i ).getPriceMedium () , // PriceMedium
							(double) selectedMovements.get ( i ).getPriceSmall () , // PriceSmall
							(double) selectedMovements.get ( i ).getQuantityBig () , // ApprovedQuantityBig
							(double) selectedMovements.get ( i ).getQuantityMedium () , // ApprovedQuantityMedium
							(double) selectedMovements.get ( i ).getQuantitySmall () , // ApprovedQuantitySmall
							(double) selectedMovements.get ( i ).getQuantityBig () * selectedMovements.get ( i ).getItem ().getUnitBigMedium () * selectedMovements.get ( i ).getItem ().getUnitMediumSmall ()
								+ selectedMovements.get ( i ).getQuantityMedium () * selectedMovements.get ( i ).getItem ().getUnitMediumSmall ()
								+ selectedMovements.get ( i ).getQuantitySmall () , // ApprovedBasicUnitQuantity
							0.0 , // MissedQuantityBig
							0.0 , // MissedQuantityMedium
							0.0 , // MissedQuantitySmall
							0.0 , // MissedBasicUnitQuantity
							selectedMovements.get ( i ).getQuantityBig () * selectedMovements.get ( i ).getPriceBig () + selectedMovements.get ( i ).getQuantityMedium ()
								* selectedMovements.get ( i ).getPriceMedium () + selectedMovements.get ( i ).getQuantitySmall () * selectedMovements.get ( i ).getPriceSmall () , // TotalLineAmount
							0.0 , // DiscountAmount
							0.0 , // DiscountPercentage
							goodQuantities.get ( selectedMovements.get ( i ).getItem ().getItemCode () ) , // StockQuantity
							selectedMovements.get ( i ).getItem ().getItemName () , // ItemName
							selectedMovements.get ( i ).getItem ().getItemAltName () , // ItemAltName
							selectedMovements.get ( i ).getReason () == null ? null : selectedMovements.get ( i ).getReason ().getReasonCode () , // ReasonCode
							selectedMovements.get ( i ).getReason () == null ? null : selectedMovements.get ( i ).getReason ().getReasonAffectStock () , // ReasonAffectStock
							null , // ItemLot
							selectedMovements.get ( i ).getExpiryDate () , // ItemExpiryDate
							selectedMovements.get ( i ).getTax () , // ItemTaxPercentage
							now.getTime () ); // StampDate
					DatabaseUtils.getInstance ( this ).getDaoSession ().getMovementDetailsDao ().insert ( movementDetail );
					movementDetails.put ( movementDetail.getItemCode () , movementDetail );
				} // End for loop
				List < VehiclesStock > addStockload = new ArrayList < VehiclesStock > ();
    			List < VehiclesStock > updateStockload = new ArrayList < VehiclesStock > ();
				if ( movementSettings.getMovementType () == MovementHeadersUtils.Type.Physical_Direct_Load ) {
					//String SQL  ;
					for ( String itemCode : movementDetails.keySet () ) {
						MovementDetails movementDetail = movementDetails.get ( itemCode );
					
							// Compute the SQL string
							// Update vehicle stock
							VehiclesStock stock = DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesStockDao ().queryBuilder ()
									.where ( VehiclesStockDao.Properties.ItemCode.eq ( movementDetail.getItemCode () ) ).limit ( 1 ).unique ();
							if ( stock == null ) {
								 stock = new VehiclesStock ( null , vehicle.getVehicleCode () , movementDetail.getItemCode () , 0.0 , 0.0 , divisionCode , user.getCompanyCode () , null , now.getTime () );
								 addStockload.add ( stock );
							} // End if
							else
								 updateStockload.add ( stock );
							stock.setGoodQuantity ( stock.getGoodQuantity () + movementDetail.getBasicUnitQuantity ()  );
						//	SQL = " UPDATE " + VehiclesStockDao.TABLENAME + " " +
						//			"SET " +  VehiclesStockDao.Properties.GoodQuantity.columnName   + " = "+VehiclesStockDao.Properties.GoodQuantity.columnName + "+ ? WHERE " + VehiclesStockDao.Properties.ItemCode.columnName + " = ? ";
						//	
						//	// Execute the movement update query
						//	DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().execSQL ( SQL , new String [] { movementDetail.getBasicUnitQuantity ().toString () , movementDetail.getItemCode () } );
			
				
					}
					for ( VehiclesStock stock : updateStockload )
						// Update the stock in DB
						DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesStockDao ().update ( stock );
					for ( VehiclesStock stock : addStockload )
						// Insert the new stock into DB
						DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesStockDao ().insert ( stock );
					}
				List < VehiclesStock > addStockUnload = new ArrayList < VehiclesStock > ();
    			List < VehiclesStock > updateStockUnload = new ArrayList < VehiclesStock > ();
				if ( movementSettings.getMovementType () == MovementHeadersUtils.Type.PHYSICAL_DIRECT_UNLOAD ) {
					//String SQL  ;
					for ( String itemCode : movementDetails.keySet () ) {
						MovementDetails movementDetail = movementDetails.get ( itemCode );
					//	if ( goodQuantities.containsKey ( movementDetail.getItemCode () ) ) {
							// Compute the SQL string
							// Update vehicle stock
							VehiclesStock stock = DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesStockDao ().queryBuilder ()
									.where ( VehiclesStockDao.Properties.ItemCode.eq ( movementDetail.getItemCode () ) ).limit ( 1 ).unique ();
							if ( stock == null ) {
								stock = new VehiclesStock ( null , vehicle.getVehicleCode () , movementDetail.getItemCode () , 0.0 , 0.0 , divisionCode , user.getCompanyCode () , null , now.getTime () );
								addStockUnload.add ( stock );
							} // End if
							else
								updateStockUnload.add ( stock );
						
							if ( ReasonsUtils.isDamages ( movementDetail.getReasonAffectStock() ) )
								stock.setDamageQuantity ( stock.getDamageQuantity () + movementDetail.getBasicUnitQuantity  () );
						    else
								stock.setGoodQuantity ( stock.getGoodQuantity () - movementDetail.getBasicUnitQuantity ()  );

							
							
							
							//SQL = " UPDATE " + VehiclesStockDao.TABLENAME + " " +
							//	  " SET " +  VehiclesStockDao.Properties.GoodQuantity.columnName   + " = "+VehiclesStockDao.Properties.GoodQuantity.columnName + "- ? WHERE " + VehiclesStockDao.Properties.ItemCode.columnName + " = ? ";
							
							// Execute the movement update query
							//DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().execSQL ( SQL , new String [] { movementDetail.getBasicUnitQuantity ().toString () , movementDetail.getItemCode () } );
			
				//}
					}
					for ( VehiclesStock stock : updateStockUnload )
						// Update the stock in DB
						DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesStockDao ().update ( stock );
					for ( VehiclesStock stock : addStockUnload )
						// Insert the new stock into DB
						DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesStockDao ().insert ( stock );
					}
			
				// Check if the movement type is stock reconciliation
				if ( movementSettings.getMovementType () == MovementHeadersUtils.Type.STOCK_RECONCILIATION ) {
					// Get the user client account number
					String clientCode = DatabaseUtils.getInstance ( this ).getDaoSession ().getUserAccountNumbersDao ().queryBuilder ().limit ( 1 ).unique ().getAccountNumber ();
					// Change stock
					List < TransactionDetails > invoices = new ArrayList < TransactionDetails > ();
					List < TransactionDetails > returns = new ArrayList < TransactionDetails > ();
					for ( String itemCode : movementDetails.keySet () ) {
						MovementDetails movementDetail = movementDetails.get ( itemCode );
						int stockQuantities = (int) ( ( goodQuantities.containsKey ( itemCode ) ? goodQuantities.get ( itemCode ) : 0 ) + ( damagedQuantities.containsKey ( itemCode ) ? damagedQuantities.get ( itemCode ) : 0 ) );
						int difference = (int) ( movementDetail.getBasicUnitQuantity () - stockQuantities );
						if ( difference != 0 ) {
							TransactionDetails transactionDetail = new TransactionDetails ( null , 
									null ,
									0 , 
									itemCode , 
									TransactionDetailsUtils.Type.ORDER , 
									0.0 , 
									0.0 , 
									(double) Math.abs ( difference ) , 
									(double) Math.abs ( difference ) , 
									0.0 , 
									0.0 , 
									0.0 , 
									0.0 , 
									0.0 , 
									0.0 , 
									0.0 , 
									0.0 , 
									movementDetail.getPriceBig () , 
									movementDetail.getPriceMedium () , 
									movementDetail.getPriceSmall () , 
									movementDetail.getPriceBig () , 
									movementDetail.getPriceMedium () , 
									movementDetail.getPriceSmall () , 
									0.0 , 
									0.0 , 
									Math.abs ( difference ) * movementDetail.getPriceSmall () , 
									null , 
									null , 
									null , 
									movementDetail.getItemTaxPercentage () , 
									movementDetail.getItemName () , 
									movementDetail.getItemAltName () , 
									null , 
									null , 
									null , 
									null , 
									null , 
									null , 
									null , 
									null , 
									null , 
									null , 
									now.getTime () );
							if ( difference > 0 )
								returns.add ( transactionDetail );
							else
								invoices.add ( transactionDetail );
						}
					} 
					Set < String > processedItemCodes = movementDetails.keySet ();
					String priceListCode = PermissionsUtils.getDefaultPriceListCode ( this , user.getUserCode () , user.getCompanyCode () );
					for ( String itemCode : goodQuantities.keySet () ) {
						if ( processedItemCodes.contains ( itemCode ) )
							continue;
						int stockQuantities = (int) ( goodQuantities.get ( itemCode ) + damagedQuantities.get ( itemCode ) );
						if ( stockQuantities > 0 ) {
							Prices prices = DatabaseUtils.getInstance ( this ).getDaoSession ().getPricesDao ().queryBuilder ()
									.where ( PricesDao.Properties.PriceListCode.eq ( priceListCode ) , PricesDao.Properties.ItemCode.eq ( itemCode ) , PricesDao.Properties.CurrencyCode.eq ( currency.getCurrencyCode () ) ).unique ();
							Items item = DatabaseUtils.getInstance ( this ).getDaoSession ().getItemsDao ().queryBuilder ()
									.where ( ItemsDao.Properties.ItemCode.eq ( itemCode ) ).unique ();
							invoices.add ( new TransactionDetails ( null , 
									null ,
									0 , 
									itemCode , 
									TransactionDetailsUtils.Type.ORDER , 
									0.0 , 
									0.0 , 
									(double) stockQuantities , 
									(double) stockQuantities , 
									0.0 , 
									0.0 , 
									0.0 , 
									0.0 , 
									0.0 , 
									0.0 , 
									0.0 , 
									0.0 , 
									prices != null ? prices.getPriceBig () : 0.0 , 
									prices != null ? prices.getPriceMedium () : 0.0 , 
									prices != null ? prices.getPriceSmall () : 0.0 , 
									prices != null ? prices.getPriceBig () : 0.0 , 
									prices != null ? prices.getPriceMedium () : 0.0 ,
									prices != null ? prices.getPriceSmall () : 0.0 , 
									0.0 , 
									0.0 , 
									stockQuantities * ( prices != null ? prices.getPriceSmall () : 0.0 ) , 
									null , 
									null , 
									null , 
									0.0 , 
									item.getItemName () , 
									item.getItemAltName () , 
									null , 
									null , 
									null , 
									null , 
									null , 
									null , 
									null , 
									null , 
									null , 
									null , 
									now.getTime () ) );
						}
					}
					if ( ! invoices.isEmpty () ) {
				    	calValue = 0;
				    	grossAmount = 0;
				    	taxes = 0;
				    	totalValue = 0;
				    	for ( TransactionDetails invoice : invoices ) {
				    		// Compute the current net value
				    		grossAmount += invoice.getQuantityBig () * invoice.getPriceBig () + invoice.getQuantityMedium () * invoice.getPriceMedium () + invoice.getQuantitySmall () * invoice.getPriceSmall ();
				    		// Compute the value after applying the discount percentage and amount
							double _calValue = ( invoice.getQuantityBig () * invoice.getPriceBig () + invoice.getQuantityMedium () * invoice.getPriceMedium ()
									+ invoice.getQuantitySmall () * invoice.getPriceSmall () );
							// Compute the current tax value
							double _taxes = 0;
							// Check if the item is taxable
							if ( invoice.getItemTaxPercentage () != 0 )
								// Compute the current taxes
								_taxes = _calValue * invoice.getItemTaxPercentage () / 100;
							// Compute the current total value
							double _totalValue = _calValue + _taxes;
							// Update all the values
							calValue += _calValue;
							taxes += _taxes;
							totalValue += _totalValue;
				    	} // End for each
						int invoiceSequence = DatabaseUtils.getUserSequence ( this , user.getUserCode () , user.getCompanyCode () , TransactionSequencesDao.Properties.SalesInvoice );
			    		// Compute the transaction header code
			    		String transactionHeaderCode = String.valueOf ( TransactionHeadersUtils.Type.SALES_INVOICE ) + String.valueOf ( user.getPrefixID () ) + sequence.format ( invoiceSequence );
						// Update the sales order sequence
						DatabaseUtils.setUserSequence ( this , user.getUserCode () , user.getCompanyCode () , TransactionSequencesDao.Properties.SalesInvoice , invoiceSequence + 1 );
						DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionHeadersDao ().insert ( new TransactionHeaders ( null , 
								transactionHeaderCode , 
								TransactionHeadersUtils.Type.SALES_INVOICE , 
								DatabaseUtils.getCurrentDivisionCode ( this ) , 
								user.getCompanyCode () , 
								currency.getCurrencyCode () , 
								clientCode , 
								user.getUserCode () , 
								null , 
								null , 
								now.getTime () , 
								now.getTime () , 
								grossAmount , 
								0.0 , 
								calValue , 
								taxes , 
								totalValue , 
								totalValue , 
								StatusUtils.isAvailable () , 
								null , 
								null , 
								null , 
								null , 
								movementHeader.getMovementCode () , 
								null , 
								null , 
								null , 
								null , 
								null , 
								null , 
								IsProcessedUtils.isNotSync () , 
								now.getTime () ) );
						for ( int i = 0 ; i < invoices.size () ; i ++ ) {
							invoices.get ( i ).setTransactionCode ( transactionHeaderCode );
							invoices.get ( i ).setLineID ( i );
							DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsDao ().insert ( invoices.get ( i ) );
						}
					}
					if ( ! returns.isEmpty () ) {
				    	calValue = 0;
				    	grossAmount = 0;
				    	taxes = 0;
				    	totalValue = 0;
				    	for ( TransactionDetails _return : returns ) {
				    		// Compute the current net value
				    		grossAmount += _return.getQuantityBig () * _return.getPriceBig () + _return.getQuantityMedium () * _return.getPriceMedium () + _return.getQuantitySmall () * _return.getPriceSmall ();
				    		// Compute the value after applying the discount percentage and amount
							double _calValue = ( _return.getQuantityBig () * _return.getPriceBig () + _return.getQuantityMedium () * _return.getPriceMedium ()
									+ _return.getQuantitySmall () * _return.getPriceSmall () );
							// Compute the current tax value
							double _taxes = 0;
							// Check if the item is taxable
							if ( _return.getItemTaxPercentage () != 0 )
								// Compute the current taxes
								_taxes = _calValue * _return.getItemTaxPercentage () / 100;
							// Compute the current total value
							double _totalValue = _calValue + _taxes;
							// Update all the values
							calValue += _calValue;
							taxes += _taxes;
							totalValue += _totalValue;
				    	} // End for each
						int returnSequence = DatabaseUtils.getUserSequence ( this , user.getUserCode () , user.getCompanyCode () , TransactionSequencesDao.Properties.SalesReturn );
			    		// Compute the transaction header code
			    		String transactionHeaderCode = String.valueOf ( TransactionHeadersUtils.Type.SALES_RETURN ) + String.valueOf ( user.getPrefixID () ) + sequence.format ( returnSequence );
						// Update the sales order sequence
						DatabaseUtils.setUserSequence ( this , user.getUserCode () , user.getCompanyCode () , TransactionSequencesDao.Properties.SalesReturn , returnSequence + 1 );
						DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionHeadersDao ().insert ( new TransactionHeaders ( null , 
								transactionHeaderCode , 
								TransactionHeadersUtils.Type.SALES_RETURN , 
								DatabaseUtils.getCurrentDivisionCode ( this ) , 
								user.getCompanyCode () , 
								currency.getCurrencyCode () , 
								clientCode , 
								user.getUserCode () , 
								null , 
								null , 
								now.getTime () , 
								now.getTime () , 
								grossAmount , 
								0.0 , 
								calValue , 
								taxes , 
								totalValue , 
								totalValue , 
								StatusUtils.isAvailable () , 
								null , 
								null , 
								null , 
								null , 
								movementHeader.getMovementCode () , 
								null , 
								null , 
								null , 
								null , 
								null , 
								null , 
								IsProcessedUtils.isNotSync () , 
								now.getTime () ) );
						for ( int i = 0 ; i < returns.size () ; i ++ ) {
							returns.get ( i ).setTransactionCode ( transactionHeaderCode );
							returns.get ( i ).setLineID ( i );
							DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsDao ().insert ( returns.get ( i ) );
						}
					}
					// Reset all vehicle stock
					// Compute the SQL string
					String SQL = "UPDATE " + VehiclesStockDao.TABLENAME + " " +
							"SET " + VehiclesStockDao.Properties.GoodQuantity.columnName + " = 0 , " + VehiclesStockDao.Properties.DamageQuantity.columnName + " = 0 ";
					// Execute the movement update query
					DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().execSQL ( SQL , new String [] {} );
					for ( String itemCode : movementDetails.keySet () ) {
						MovementDetails movementDetail = movementDetails.get ( itemCode );
						if ( goodQuantities.containsKey ( movementDetail.getItemCode () ) ) {
							// Compute the SQL string
							SQL = "UPDATE " + VehiclesStockDao.TABLENAME + " " +
									"SET " + ( ReasonsUtils.isDamage ( movementDetail.getReasonAffectStock () ) ? VehiclesStockDao.Properties.DamageQuantity.columnName : VehiclesStockDao.Properties.GoodQuantity.columnName ) + " = ? WHERE " + VehiclesStockDao.Properties.ItemCode.columnName + " = ? ";
							// Execute the movement update query
							DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().execSQL ( SQL , new String [] { movementDetail.getBasicUnitQuantity ().toString () , movementDetail.getItemCode () } );
						}
						else
							DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesStockDao ().insert ( new VehiclesStock ( null , vehicle.getVehicleCode () , movementDetail.getItemCode () ,
									( ReasonsUtils.isDamage ( movementDetail.getReasonAffectStock () ) ? 0.0 : movementDetail.getBasicUnitQuantity () ) ,
									( ReasonsUtils.isDamage ( movementDetail.getReasonAffectStock () ) ? movementDetail.getBasicUnitQuantity () : 0.0 ) ,
									DatabaseUtils.getCurrentDivisionCode ( this ) , user.getCompanyCode () , null , now.getTime () ) );
					}
				} // End if
				// Check if the movement type is van transfer
				else if ( movementSettings.getMovementType () == MovementHeadersUtils.Type.VAN_TRANSFER ) {
					// Declare and initialize vehicle stock lists used to add and update 
					List < VehiclesStock > addStock = new ArrayList < VehiclesStock > ();
					List < VehiclesStock > updateStock = new ArrayList < VehiclesStock > ();
					// Iterate over the movement details
					for ( String itemCode : movementDetails.keySet () ) {
						MovementDetails movementDetail = movementDetails.get ( itemCode );
					
						// Update vehicle stock
						VehiclesStock stock = DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesStockDao ().queryBuilder ()
								.where ( VehiclesStockDao.Properties.ItemCode.eq ( movementDetail.getItemCode () ) ).limit ( 1 ).unique ();
						if ( stock == null ) {
							stock = new VehiclesStock ( null , vehicle.getVehicleCode () , movementDetail.getItemCode () , 0.0 , 0.0 , divisionCode , user.getCompanyCode () , null , now.getTime () );
							addStock.add ( stock );
						} // End if
						else
							updateStock.add ( stock );
						int coefficient = 0;
						if ( vanType == VAN_TYPE_IN )
							coefficient = 1;
						else if ( vanType == VAN_TYPE_OUT )
							coefficient = -1;
						stock.setGoodQuantity ( stock.getGoodQuantity () + coefficient * movementDetail.getBasicUnitQuantity () );
					} // End for each
					// Iterate over all new stock
					for ( VehiclesStock stock : addStock )
						// Insert the new stock into DB
						DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesStockDao ().insert ( stock );
					// Iterate over modified stock
					for ( VehiclesStock stock : updateStock )
						// Update the stock in DB
						DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesStockDao ().update ( stock );
				} // End else if
				// Check if the movement type is stock management
				else if ( movementSettings.getMovementType () == MovementHeadersUtils.Type.STOCK_MANAGEMENT ) {
					// Declare and initialize vehicle stock lists used to add and update 
					List < VehiclesStock > updateStock = new ArrayList < VehiclesStock > ();
					// Iterate over the movement details
					for ( String itemCode : movementDetails.keySet () ) {
						MovementDetails movementDetail = movementDetails.get ( itemCode );
					
						// Update vehicle stock
						VehiclesStock stock = DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesStockDao ().queryBuilder ()
								.where ( VehiclesStockDao.Properties.ItemCode.eq ( movementDetail.getItemCode () ) ).limit ( 1 ).unique ();
						updateStock.add ( stock );
						stock.setGoodQuantity ( stock.getGoodQuantity () - movementDetail.getBasicUnitQuantity () );
						stock.setDamageQuantity ( stock.getDamageQuantity () + movementDetail.getBasicUnitQuantity () );
					} // End for each
					// Iterate over modified stock
					for ( VehiclesStock stock : updateStock )
						// Update the stock in DB
						DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesStockDao ().update ( stock );
				} // End else if
				
				// Commit changes
				DatabaseUtils.getInstance ( MovementDetailsActivity.this ).getDaoSession ().getDatabase ().setTransactionSuccessful ();
				// Indicate that the save was successful
				Vibration.vibrate ( MovementDetailsActivity.this );
    		} catch ( Exception exception ) {
				// Indicate that an error occurred
				error = true;
    		} finally {
				// End transaction
				DatabaseUtils.getInstance ( MovementDetailsActivity.this ).getDaoSession ().getDatabase ().endTransaction ();
    		} // End try-catch-finally block
		} // End if
		else if ( movementHeader != null && movementDetails != null && ( movementSettings.getMovementType () == MovementHeadersUtils.Type.LOAD || movementSettings.getMovementType () == MovementHeadersUtils.Type.UNLOAD ) ) {
			// Update the movement header
			movementHeaderCode = movementHeader.getMovementCode ();
			movementHeader.setGrossAmount ( grossAmount );
			movementHeader.setNetAmount ( calValue );
			movementHeader.setTaxAmount ( taxes );
			movementHeader.setTotalTaxAmount ( totalValue );
			movementHeader.setMovementDate ( now.getTime () );
			movementHeader.setWarehouseKeeperCode ( warehouseKeeperEnd==null?null: warehouseKeeperEnd.getUserCode () );
			movementHeader.setIsProcessed ( IsProcessedUtils.isNotSync () );
			// Declare and initialize vehicle stock lists used to add and update 
			List < VehiclesStock > addStock = new ArrayList < VehiclesStock > ();
			List < VehiclesStock > updateStock = new ArrayList < VehiclesStock > ();
			List < MovementDetails > missedDetails = new ArrayList < MovementDetails > ();
			// Map the selected movements to their item codes
			Map < String , Movement > _selectedMovements = new HashMap < String , Movement > ();
			// Iterate over all selected movements
			for ( Movement movement : selectedMovements ) 
				// Map the current movement to its item code
				_selectedMovements.put ( movement.getItem ().getItemCode () , movement );
			// Iterate over all movement details
			for ( MovementDetails movementDetail : movementDetails ) {
				// Update stock quantity
				movementDetail.setStockQuantity ( goodQuantities.get ( movementDetail.getItemCode () ) );
				if ( movementDetail.getStockQuantity () == null )
					movementDetail.setStockQuantity ( 0.0 );
				// Check if the current detail is selected
				if ( _selectedMovements.containsKey ( movementDetail.getItemCode () ) ) {
					Movement movement = _selectedMovements.get ( movementDetail.getItemCode () );
					movementDetail.setQuantityBig ( (double) movement.getQuantityBig () );
					movementDetail.setQuantityMedium ( (double) movement.getQuantityMedium () );
					movementDetail.setQuantitySmall ( (double) movement.getQuantitySmall () );
					movementDetail.setBasicUnitQuantity ( (double) movement.getQuantityBig () * movement.getItem ().getUnitBigMedium () * movement.getItem ().getUnitMediumSmall ()
							+ movement.getQuantityMedium () * movement.getItem ().getUnitMediumSmall ()
							+ movement.getQuantitySmall () );
					movementDetail.setMissedQuantityBig ( movementDetail.getApprovedQuantityBig () - movementDetail.getQuantityBig () );
					movementDetail.setMissedQuantityMedium ( movementDetail.getApprovedQuantityMedium () - movementDetail.getQuantityMedium () );
					movementDetail.setMissedQuantitySmall ( movementDetail.getApprovedQuantitySmall () - movementDetail.getQuantitySmall () );
					movementDetail.setMissedBasicUnitQuantity ( movementDetail.getApprovedBasicUnitQuantity () - movementDetail.getBasicUnitQuantity () );
					movementDetail.setTotalLineAmount ( movement.getQuantityBig () * movement.getPriceBig () + movement.getQuantityMedium ()
								* movement.getPriceMedium () + movement.getQuantitySmall () * movement.getPriceSmall () );
				//	movementDetail.setReasonAffectStock(movement.getReason().getReasonAffectStock());
				//	movementDetail.setReasonCode(movement.getReason().getReasonCode());
					// Update vehicle stock
					VehiclesStock stock = DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesStockDao ().queryBuilder ()
							.where ( VehiclesStockDao.Properties.ItemCode.eq ( movementDetail.getItemCode () ) ).limit ( 1 ).unique ();
					if ( stock == null ) {
						stock = new VehiclesStock ( null , vehicle.getVehicleCode () , movementDetail.getItemCode () , 0.0 , 0.0 , divisionCode , user.getCompanyCode () , null , now.getTime () );
						addStock.add ( stock );
					} // End if
					else
						updateStock.add ( stock );
					
					if ( movementSettings.getMovementType () == MovementHeadersUtils.Type.LOAD || ( movementSettings.getMovementType () == MovementHeadersUtils.Type.UNLOAD && ! ReasonsUtils.isDamage ( movementDetail.getReasonAffectStock () ) ) )
						stock.setGoodQuantity ( stock.getGoodQuantity () + ( movementSettings.getMovementType () == MovementHeadersUtils.Type.LOAD ? 1 : -1 ) * movementDetail.getBasicUnitQuantity () );
					else if ( movementSettings.getMovementType () == MovementHeadersUtils.Type.UNLOAD && ReasonsUtils.isDamage ( movementDetail.getReasonAffectStock () ) )
						stock.setDamageQuantity ( stock.getDamageQuantity () - movementDetail.getBasicUnitQuantity () );
					if ( movementSettings.getMovementType () == MovementHeadersUtils.Type.LOAD ) {
						if ( movementDetail.getMissedBasicUnitQuantity () > 0 )
							missedDetails.add ( new MovementDetails ( null , 
									null , 
									0 , 
									movementDetail.getItemCode () , 
									0.0 , 
									0.0 , 
									0.0 , 
									0.0 , 
									0.0 , 
									0.0 , 
									0.0 , 
									0.0 , 
									0.0 , 
									0.0 , 
									0.0 , 
									movementDetail.getMissedQuantityBig () , 
									movementDetail.getMissedQuantityMedium () , 
									movementDetail.getMissedQuantitySmall () , 
									movementDetail.getMissedBasicUnitQuantity () , 
									0.0 , 
									0.0 , 
									0.0 , 
									null , 
									movementDetail.getItemName () , 
									movementDetail.getItemAltName () , 
									movementDetail.getReasonCode () , 
									movementDetail.getReasonAffectStock () , 
									null , 
									movementDetail.getItemExpiryDate () , 
									0.0 , 
									now.getTime () ) );
					}
				} // End if
				else {
					// Reset quantities and set missed quantities
					movementDetail.setQuantityBig ( 0.0 );
					movementDetail.setQuantityMedium ( 0.0 );
					movementDetail.setQuantitySmall ( 0.0 );
					movementDetail.setBasicUnitQuantity ( 0.0 );
					movementDetail.setMissedQuantityBig ( movementDetail.getApprovedQuantityBig () );
					movementDetail.setMissedQuantityMedium ( movementDetail.getApprovedQuantityMedium () );
					movementDetail.setMissedQuantitySmall ( movementDetail.getApprovedQuantitySmall () );
					movementDetail.setMissedBasicUnitQuantity ( movementDetail.getApprovedBasicUnitQuantity () );
					movementDetail.setTotalLineAmount ( 0.0 );
				} // End if
			} // End for each
			
    		try {
				// Begin transaction
				DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().beginTransaction ();
				
				// Update the movement header in DB
				DatabaseUtils.getInstance ( this ).getDaoSession ().getMovementHeadersDao ().update ( movementHeader );
				// Iterate over all movement details
				for ( MovementDetails movementDetail : movementDetails )
					// Update the movement detail in DB
					DatabaseUtils.getInstance ( this ).getDaoSession ().getMovementDetailsDao ().update ( movementDetail );
				// Iterate over all new stock
				for ( VehiclesStock stock : addStock )
					// Insert the new stock into DB
					DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesStockDao ().insert ( stock );
				// Iterate over modified stock
				for ( VehiclesStock stock : updateStock )
					// Update the stock in DB
					DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesStockDao ().update ( stock );
	
				// Check if there is at least one missed detail to insert
				if ( ! missedDetails.isEmpty () ) {
					// Retrieve the movement sequence
					int movementSequence = DatabaseUtils.getUserSequence ( this , user.getUserCode () , user.getCompanyCode () , TransactionSequencesDao.Properties.Movements );
		    		// Compute the movement header code
		    		String _movementHeaderCode = String.valueOf ( MovementHeadersUtils.Type.MISSED_UNLOAD ) + String.valueOf ( user.getPrefixID () ) + sequence.format ( movementSequence );
					// Update the movement sequence
					DatabaseUtils.setUserSequence ( this , user.getUserCode () , user.getCompanyCode () , TransactionSequencesDao.Properties.Movements , movementSequence + 1 );
					// Insert header
					DatabaseUtils.getInstance ( this ).getDaoSession ().getMovementHeadersDao ().insert ( new MovementHeaders ( null , 
							_movementHeaderCode , 
							divisionCode , 
							user.getCompanyCode () , 
							null , 
							null , 
							user.getUserCode () , 
							null , 
							MovementHeadersUtils.Type.MISSED_UNLOAD , 
							now.getTime () , 
							0.0 , 
							0.0 , 
							0.0 , 
							0.0 , 
							0.0 , 
							StatusUtils.isAvailable () , 
							null , 
							null , 
							null , 
							vehicle.getVehicleCode () , 
							movementHeader.getMovementCode () , 
							null , 
							IsProcessedUtils.isNotSync () , 
							now.getTime () ) );
					// Insert details
					for ( int i = 0 ; i < missedDetails.size () ; i ++ ) {
						missedDetails.get ( i ).setMovementCode ( _movementHeaderCode );
						missedDetails.get ( i ).setLineID ( i );
						DatabaseUtils.getInstance ( this ).getDaoSession ().getMovementDetailsDao ().insert ( missedDetails.get ( i ) );
					} // End for loop
				} // End if
				
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
		else if ( movementHeader != null && movementDetails != null ) {
			// Before saving, refresh the objects to make sure the movement is not already sent to the server ( via auto sync )
			DatabaseUtils.getInstance ( MovementDetailsActivity.this ).getDaoSession ().getMovementHeadersDao ().refresh ( movementHeader );
			// Check that the movement header is not processed to the server
			if ( movementHeader.getIsProcessed () == IsProcessedUtils.isSync () ) {
    			// Indicate that the movement cannot be modified anymore
				Baguette.showText ( this ,
						AppResources.getInstance ( this ).getString ( this , R.string.movement_already_processed_message ) ,
						Baguette.BackgroundColor.RED );
	    		// Exit method
	    		return;
			} // End if
			
			// Update the movement header
			movementHeaderCode = movementHeader.getMovementCode ();
			movementHeader.setGrossAmount ( grossAmount );
			movementHeader.setNetAmount ( calValue );
			movementHeader.setTaxAmount ( taxes );
			movementHeader.setTotalTaxAmount ( totalValue );
			movementHeader.setMovementDate ( now.getTime () );
			movementHeader.setPasswordCode ( passcode );
			// Map the movement details to their item codes
			Map < String , MovementDetails > _movementDetails = new HashMap < String , MovementDetails > ();
    		// Compute the maximum movement detail line id
    		int maxId = -1;
			// Iterate over all movement details
			for ( MovementDetails movementDetail : movementDetails ) {
				// Map the current movement detail to its item code
				_movementDetails.put ( movementDetail.getItemCode () , movementDetail );
				// Check if the maximum line id is exceeded
				if ( movementDetail.getLineID () > maxId )
					// Set the new line id
					maxId = movementDetail.getLineID ();
			} // End for each
			
			// Declare and initialize movement details used to add, update and remove the movement details
			List < MovementDetails > addItems = new ArrayList < MovementDetails > ();
			List < MovementDetails > updateItems = new ArrayList < MovementDetails > ();
			// Iterate over all selected movements
			for ( Movement movement : selectedMovements ) {
				// Determine the corresponding movement detail
				if ( _movementDetails.containsKey ( movement.getItem ().getItemCode () ) ) {
					// Retrieve a reference to the movement detail
					MovementDetails movementDetail = _movementDetails.get ( movement.getItem ().getItemCode () );
					// Add the movement to the list of updated movement details
					updateItems.add ( movementDetail );
					// Update the movement details
					movementDetail.setQuantityBig ( (double) movement.getQuantityBig () );
					movementDetail.setQuantityMedium ( (double) movement.getQuantityMedium () );
					movementDetail.setQuantitySmall ( (double) movement.getQuantitySmall () );
					movementDetail.setBasicUnitQuantity ( (double) movement.getQuantityBig () * movement.getItem ().getUnitBigMedium () * movement.getItem ().getUnitMediumSmall ()
							+ movement.getQuantityMedium () * movement.getItem ().getUnitMediumSmall ()
							+ movement.getQuantitySmall () );
					movementDetail.setPriceBig ( movement.getPriceBig () );
					movementDetail.setPriceMedium ( movement.getPriceMedium () );
					movementDetail.setPriceSmall ( movement.getPriceSmall () );
					movementDetail.setApprovedQuantityBig ( (double) movement.getQuantityBig () );
					movementDetail.setApprovedQuantityMedium ( (double) movement.getQuantityMedium () );
					movementDetail.setApprovedQuantitySmall ( (double) movement.getQuantitySmall () );
					movementDetail.setApprovedBasicUnitQuantity ( (double) movement.getQuantityBig () * movement.getItem ().getUnitBigMedium () * movement.getItem ().getUnitMediumSmall ()
							+ movement.getQuantityMedium () * movement.getItem ().getUnitMediumSmall ()
							+ movement.getQuantitySmall () );
					movementDetail.setTotalLineAmount ( movement.getQuantityBig () * movement.getPriceBig () + movement.getQuantityMedium ()
							* movement.getPriceMedium () + movement.getQuantitySmall () * movement.getPriceSmall () );
					movementDetail.setItemTaxPercentage ( movement.getTax () );
					movementDetail.setStockQuantity ( goodQuantities.get ( movement.getItem ().getItemCode () ) );
					movementDetail.setItemExpiryDate ( movement.getExpiryDate () );
					movementDetail.setReasonCode ( movement.getReason () == null ? null : movement.getReason ().getReasonCode () );
					movementDetail.setReasonAffectStock ( movement.getReason () == null ? null : movement.getReason ().getReasonAffectStock () );
					movementDetail.setStampDate ( now.getTime () );
				} // End if
				else
					// TODO : Details of a movement detail
					// Create a new movement detail
					addItems.add ( new MovementDetails ( null , // ID
							movementHeader.getMovementCode () , // MovementCode
							++ maxId , // LineID
							movement.getItem ().getItemCode () , // ItemCode
							(double) movement.getQuantityBig () , // QuantityBig
							(double) movement.getQuantityMedium () , // QuantityMedium
							(double) movement.getQuantitySmall () , // QuantitySmall
							(double) movement.getQuantityBig () * movement.getItem ().getUnitBigMedium () * movement.getItem ().getUnitMediumSmall ()
								+ movement.getQuantityMedium () * movement.getItem ().getUnitMediumSmall ()
								+ movement.getQuantitySmall () , // BasicUnitQuantity
							(double) movement.getPriceBig () , // PriceBig
							(double) movement.getPriceMedium () , // PriceMedium
							(double) movement.getPriceSmall () , // PriceSmall
							(double) movement.getQuantityBig () , // ApprovedQuantityBig
							(double) movement.getQuantityMedium () , // ApprovedQuantityMedium
							(double) movement.getQuantitySmall () , // ApprovedQuantitySmall
							(double) movement.getQuantityBig () * movement.getItem ().getUnitBigMedium () * movement.getItem ().getUnitMediumSmall ()
								+ movement.getQuantityMedium () * movement.getItem ().getUnitMediumSmall ()
								+ movement.getQuantitySmall () , // ApprovedBasicUnitQuantity
							0.0 , // MissedQuantityBig
							0.0 , // MissedQuantityMedium
							0.0 , // MissedQuantitySmall
							0.0 , // MissedBasicUnitQuantity
							movement.getQuantityBig () * movement.getPriceBig () + movement.getQuantityMedium ()
								* movement.getPriceMedium () + movement.getQuantitySmall () * movement.getPriceSmall () , // TotalLineAmount
							0.0 , // DiscountAmount
							0.0 , // DiscountPercentage
							goodQuantities.get ( movement.getItem ().getItemCode () ) , // StockQuantity
							movement.getItem ().getItemName () , // ItemName
							movement.getItem ().getItemAltName () , // ItemAltName
							movement.getReason () == null ? null : movement.getReason ().getReasonCode () , // ReasonCode
							movement.getReason () == null ? null : movement.getReason ().getReasonAffectStock () , // ReasonAffectStock
							null , // ItemLot
							movement.getExpiryDate () , // ItemExpiryDate
							movement.getTax () , // ItemTaxPercentage
							now.getTime () ) ); // StampDate
			} // End for each
			
    		try {
				// Begin transaction
				DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().beginTransaction ();
				
				// Update the movement header in DB
				DatabaseUtils.getInstance ( this ).getDaoSession ().getMovementHeadersDao ().update ( movementHeader );
				// Iterate over all movement details
				for ( MovementDetails movementDetail : movementDetails )
					// Determine if the current movement detail should be updated
					if ( updateItems.contains ( movementDetail ) ) {
						// Update the movement detail in DB
						DatabaseUtils.getInstance ( this ).getDaoSession ().getMovementDetailsDao ().update ( movementDetail );
					} // End if
					else {
						// Otherwise the movement detail should be removed
						DatabaseUtils.getInstance ( this ).getDaoSession ().getMovementDetailsDao ().delete ( movementDetail );
					} // End else
				// Iterate over all new movement details
				for ( MovementDetails movementDetail : addItems ) {
					// Insert the new movement detail into DB
					DatabaseUtils.getInstance ( this ).getDaoSession ().getMovementDetailsDao ().insert ( movementDetail );
				} // End for each
				if ( movementSettings.getMovementType () == MovementHeadersUtils.Type.Physical_Direct_Load ) {
					String SQL  ;
					for ( String itemCode : _movementDetails.keySet () ) {
						MovementDetails movementDetail  = _movementDetails.get ( itemCode );
						if ( goodQuantities.containsKey ( movementDetail.getItemCode () ) ) {
							// Compute the SQL string
							SQL = "UPDATE " + VehiclesStockDao.TABLENAME + " " +
									"SET " +      VehiclesStockDao.Properties.GoodQuantity.columnName   + " = "+VehiclesStockDao.Properties.GoodQuantity.columnName +"+ ? WHERE " + VehiclesStockDao.Properties.ItemCode.columnName + " = ? ";
							// Execute the movement update query
							DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().execSQL ( SQL , new String [] { movementDetail.getBasicUnitQuantity ().toString () , movementDetail.getItemCode () } );
			
				}
					}
					}
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
		if ( ! error && movementHeaderCode != null
				&& ( movementSettings.getMovementType () == MovementHeadersUtils.Type.LOAD
				|| movementSettings.getMovementType () == MovementHeadersUtils.Type.LOAD_REQUEST
				|| movementSettings.getMovementType () == MovementHeadersUtils.Type.UNLOAD
				|| movementSettings.getMovementType () == MovementHeadersUtils.Type.UNLOAD_REQUEST
				|| movementSettings.getMovementType () == MovementHeadersUtils.Type.STOCK_MANAGEMENT 
				|| movementSettings.getMovementType () == MovementHeadersUtils.Type.VAN_STOCK_COUNT
				|| movementSettings.getMovementType () == MovementHeadersUtils.Type.STOCK_RECONCILIATION
				|| movementSettings.getMovementType () == MovementHeadersUtils.Type.VAN_TRANSFER
				|| movementSettings.getMovementType () == MovementHeadersUtils.Type.Physical_Direct_Load
				|| movementSettings.getMovementType () == MovementHeadersUtils.Type.PHYSICAL_DIRECT_UNLOAD ) ) {
			// Retrieve a constant reference to the transaction code
			printingTransactionCode = movementHeaderCode;
			// Set flag
			displayPrintingConfirmation = true;
			// Display printing confirmation
			displayPrintingConfirmation ();
		} // End if
		else {
	    	// Call this to set the result that your activity will return to its caller
	    	setResult ( RESULT_OK , new Intent ().putExtra ( MovementActivity.SAVE_SUCCESS , ! error ) );
	    	// Finish this activity
	    	finish ();
			// Indicate that the save was successful
			Vibration.vibrate ( MovementDetailsActivity.this );
		} // End else
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
	 * Properly initializes and returns a list adapter for the movement list.
	 * 
	 * @param layout	XML Layout (layout) resource id.
	 * @param movementItems	List of {@link me.SyncWise.Android.Modules.Movements.Movement Movement} objects.
	 * @param currencyRounding	Integer used to indicate the currency rounding.
	 * @param itemsEnabled	Flag indicating if the items are enabled or not.
	 * @return	A list adapter used to populate the movement list.
	 */
	protected ListAdapter getMovementDetailsAdapter ( int layout , List < Movement > movementItems , final int currencyRounding , final boolean itemsEnabled ) {
		MovementSettings movementSettings = (MovementSettings) getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS );
		if ( movementSettings.getMovementType () == MovementHeadersUtils.Type.VAN_TRANSFER && vanType == VAN_TYPE_OUT ) {
			movementSettings.setEnforceSuggestionLimit ( true );
			movementSettings.setDisplaySuggestions ( true );
			movementSettings.setColorCodedSuggestions ( true );
		}
		return new MovementDetailsAdapter ( this , layout , movementItems , movementSettings , currencyRounding , itemsEnabled );
	}
	
	/**
	 * Indicates whether the movement has new / unsaved modifications.<br>
	 * This method iterates over the list of all selected movements and compares them with the movements list.<br>
	 * <b>Note : </b> This method always returns false if there are no previously stored movement details.
	 * 
	 * @return	Boolean stating if there are new modifications.
	 */
	private boolean hasModifications () {
		// Check if there is a previously stored movement
		if ( movementHeader == null || movementDetails == null || selectedMovements == null )
			// No saved movement to match to
			return false;
		// Map the movement details to their item codes
		Map < String , MovementDetails > _movementDetails = new HashMap < String , MovementDetails > ();
		// Iterate over all movement details
		for ( MovementDetails movementDetail : movementDetails )
			// Map the current movement detail to its item code
			_movementDetails.put ( movementDetail.getItemCode () , movementDetail );
		// Declare and initialize a list used to host the valid movement
		ArrayList < Movement > movementItems = new ArrayList < Movement > ();
		// Iterate over all movements
		for ( Movement movement : movements )
			// Check if the current movement has valid quantities
			if ( movement.getQuantityBig () != 0 || movement.getQuantityMedium () != 0 || movement.getQuantitySmall () != 0 )
				// The movement contains at least one quantity, add it to the movement list
				movementItems.add ( movement );
		// Check if both sizes differ
		if ( movementItems.size () != _movementDetails.size () )
			// There is at least one modification
			return true;
		// Iterate over the movement
		for ( Movement movement : movementItems )
			// Determine the corresponding movement detail
			if ( _movementDetails.containsKey ( movement.getItem ().getItemCode () ) ) {
				// Retrieve a reference to the movement detail
				MovementDetails movementDetail = _movementDetails.get ( movement.getItem ().getItemCode () );
				// Determine if the item expiry of the detail is null
				boolean isExpiryDetailNull = movementDetail.getItemExpiryDate () == null;
				// Determine if the item expiry of the movement is null
				boolean isExpiryMovementNull = movement.getExpiryDate () == null;
				// Determine if the reason of the detail is null
				boolean isReasonDetailNull = movementDetail.getReasonCode () == null;
				// Determine if the reason of the movement is null
				boolean isReasonMovementNull = movement.getReason () == null;
				// Check for modifications
				if ( ! movementDetail.getQuantityBig ().equals ( (double) movement.getQuantityBig () )
						|| ! movementDetail.getQuantityMedium ().equals ( (double) movement.getQuantityMedium () )
						|| ! movementDetail.getQuantitySmall ().equals ( (double) movement.getQuantitySmall () )
						|| ! movementDetail.getPriceBig ().equals ( movement.getPriceBig () )
						|| ! movementDetail.getPriceMedium ().equals ( movement.getPriceMedium () )
						|| ! movementDetail.getPriceSmall ().equals ( movement.getPriceSmall () )
						|| ( isExpiryDetailNull != isExpiryMovementNull 
							|| ( movementDetail.getItemExpiryDate () != null && ! movementDetail.getItemExpiryDate ().equals ( movement.getExpiryDate () ) ) )
						|| ( isReasonDetailNull != isReasonMovementNull 
								|| ( ! isReasonDetailNull && ! isReasonMovementNull && ! movementDetail.getReasonCode ().equals ( movement.getReason ().getReasonCode () ) ) ) )
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
	 * Filters the {@link #movements} list based on the indicated {@link #searchQuery} string and the {@link #selectedDivisionsCodes} list.<br>
	 * <b>NOTE : </b> This method does not perform any kind of integrity check what so ever !
	 * 
	 * @return	List of {@link me.SyncWise.Android.Modules.Movements.Movement Movement} objects filtered according to the search and divisions filter.
	 */
	@SuppressLint("DefaultLocale") 
	protected ArrayList < Movement > filter () {
		// Declare and initialize a new movements list
		ArrayList < Movement > _movements = new ArrayList < Movement > ();
		// Iterate over all the movements
		for ( Movement movement : movements ) {
			// Determine if at least one of the movement's divisions is selected
			boolean skip = true;
			// Iterate over all movement's division
			for ( String divisionCode : movement.getDivisionCodes () )
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
			if ( movement.getItem ().getItemName ().toLowerCase ().contains ( searchQuery.toLowerCase () )
					|| movement.getItem ().getItemCode ().toLowerCase ().contains ( searchQuery.toLowerCase () )
					|| ( movement.getBarCodes () != null && movement.getBarCodes ().contains ( searchQuery.toLowerCase () ) ) )
				// Add the movement to the list
				_movements.add ( movement );
		} // End for each
		// Return the new movements list
		return _movements;
	}
	
	
	/**
	 * AsyncTask helper class used to retrieve warehouse quantities data from the database.
	 * 
	 * @author Elias
	 *
	 */
	private class RetrieveWarehouseQuantities extends AsyncTask < Void , Void , ArrayList < WarehouseQuantity > > {
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected ArrayList < WarehouseQuantity > doInBackground ( Void ... params ) {
			// Retrieve the warehouse quantities
			List < WarehouseQuantities > _warehouseQuantities = DatabaseUtils.getInstance ( MovementDetailsActivity.this ).getDaoSession ().getWarehouseQuantitiesDao ().loadAll ();
			// Initialize the list used to host item's warehouse quantities
			warehouseQuantities = new ArrayList < WarehouseQuantity > ();
			// Iterate over all quantities
			for ( WarehouseQuantities quantity : _warehouseQuantities ) {
				// Declare and initialize a new warehouse quantity related to the current item
				WarehouseQuantity item = new WarehouseQuantity ( quantity.getItemCode () );
				// Search for the current item in the list
				int index = warehouseQuantities.indexOf ( item );
				// Check if the item is already added to the list
				if ( index == -1 ) {
					// Update the quantities
					item.setQuantityBig ( quantity.getQuantityBig () == null ? 0 : quantity.getQuantityBig () );
					item.setQuantityMedium ( quantity.getQuantityMedium () == null ? 0 : quantity.getQuantityMedium () );
					item.setQuantitySmall ( quantity.getQuantitySmall () == null ? 0 : quantity.getQuantitySmall () );
					item.setMinimumQuantity ( quantity.getMinimumQuantity () == null ? 0 : quantity.getMinimumQuantity () );
					// Add the new item to the list
					warehouseQuantities.add ( item );
				} // End if
				else {
					// Retrieve a reference to the existing item
					WarehouseQuantity _item = warehouseQuantities.get ( index );
					// Update the quantities
					_item.setQuantityBig ( _item.getQuantityBig () +  ( quantity.getQuantityBig () == null ? 0 : quantity.getQuantityBig () ) );
					_item.setQuantityMedium ( _item.getQuantityMedium () + ( quantity.getQuantityMedium () == null ? 0 : quantity.getQuantityMedium () ) );
					_item.setQuantitySmall ( _item.getQuantitySmall () + ( quantity.getQuantitySmall () == null ? 0 : quantity.getQuantitySmall () ) );
					_item.setMinimumQuantity ( _item.getMinimumQuantity () + ( quantity.getMinimumQuantity () == null ? 0 : quantity.getMinimumQuantity () ) );
				} // End else
			} // End for loop
			return null;
		}
		
	}
	
	/**
	 * AsyncTask helper class used to populate the movement items list with the appropriate items.
	 * 
	 * @author Elias
	 *
	 */
	private class PopulateList extends AsyncTask < Void , Void , ArrayList < Movement > > {
		
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
		protected ArrayList < Movement > doInBackground ( Void ... params ) {
			try {
				
				// Declare and initialize a cursor object used to retrieve data sets from query results
				Cursor cursor = null;
				// Declare the SQL string and selection arguments array of strings used to to query DB
				String SQL = null;
				String selectionArguments [] = null;
				// Retrieve the user and company codes
				String userCode = DatabaseUtils.getCurrentUserCode ( MovementDetailsActivity.this );
				String companyCode = DatabaseUtils.getCurrentCompanyCode ( MovementDetailsActivity.this );
				// Retrieve the movement settings
				MovementSettings movementSettings = (MovementSettings) getIntent ().getSerializableExtra ( MOVEMENT_SETTINGS );
				
				// Temporary list
				HashMap < String , Reasons > _reasons = new HashMap < String , Reasons > ();
				// Check if the statuses list is valid
				if ( reasons == null ) {
					// Initialize the list
					reasons = new ArrayList < Reasons > ();
					// Add the default selection
					reasons.add ( new Reasons ( null , null , null , "----------" , null , null , null , null , null , null ) );
					// Retrieve the statuses list
					reasons.addAll ( DatabaseUtils.getInstance ( MovementDetailsActivity.this ).getDaoSession ().getReasonsDao ().queryBuilder ()
							.where ( ReasonsDao.Properties.ReasonType.eq ( ReasonsUtils.Type.ITEM_STATUS ) ).list () );
				} // End if
				// Iterate over the reasons
				for ( int i = 1 ; i < reasons.size () ; i ++ )
					// Map the reason to its code
					_reasons.put ( reasons.get ( i ).getReasonCode () , reasons.get ( i ) );
				
				// Check if the currency is valid
				if ( currency == null ) {
					// Retrieve the currency with the utmost priority
					currency = DatabaseUtils.getInstance ( MovementDetailsActivity.this ).getDaoSession ().getCurrenciesDao ().queryBuilder ()
							.orderAsc ( CurrenciesDao.Properties.CurrencyPriority ).unique ();
				} // End if
				
				// So far here, the currency or header code objects must be valid objects
				// If not, cannot continue
				// Check if both the header code or the currency are valid
				if ( getIntent ().getStringExtra ( MOVEMENT_HEADER_CODE ) == null && currency == null ) {
					// Set flag to indicate an error
					error = true;
					// Clear the task reference
					populateList = null;
					// Exit method
					return null;
				} // End if
				
				// Check if the cash van list is valid
				if ( cashVanUsers == null ) {
					// Initialize the list
					cashVanUsers = new ArrayList < Users > ();
					// Add the default selection
					cashVanUsers.add ( new Users ( null , null , null , null , "----------" , null , 0 , null , null , null , null , null , null , null , null , null ) );
					// Retrieve the cash van list
					cashVanUsers.addAll ( DatabaseUtils.getInstance ( MovementDetailsActivity.this ).getDaoSession ().getUsersDao ().queryBuilder ()
							.where ( UsersDao.Properties.UserType.eq ( 11 ) , UsersDao.Properties.UserStatus.eq ( StatusUtils.isActive () ) ).list () );
					// Iterate over the cash van list
					for ( int i = 1 ; i < cashVanUsers.size () ; i ++ ) {
						// Retrieve the user object
						Users user = cashVanUsers.get ( i );
						// Check if the current user is the tablet owner
						if ( user.getUserCode ().equals ( userCode ) && user.getCompanyCode ().equals ( companyCode ) ) {
							// Remove the user from the list
							cashVanUsers.remove ( i );
							// Exit loop
							break;
						} // End if
					} // End for each
				} // End if
				
				// Declare and initialize a map used to map the movement details to their item codes
				Map < String , MovementDetails > _movementDetails = new HashMap < String , MovementDetails > ();
				// Determine if there is an available movement header code
				if ( getIntent ().getStringExtra ( MOVEMENT_HEADER_CODE ) != null && movementHeader == null ) {
					// Try to retrieve the movement header with the specified movement code
					movementHeader = DatabaseUtils.getInstance ( MovementDetailsActivity.this ).getDaoSession ().getMovementHeadersDao ().queryBuilder ()
	 					.where ( MovementHeadersDao.Properties.MovementCode.eq ( getIntent ().getStringExtra ( MOVEMENT_HEADER_CODE ) ) ).unique ();
					// Check if the movement is van transfer
					if ( movementSettings.getMovementType () == MovementHeadersUtils.Type.VAN_TRANSFER )
						// Load van type
						vanType = movementHeader.getTransferType ();
					// Check if the movement header is valid
					if ( movementHeader != null ) {
						// Refresh object
						DatabaseUtils.getInstance ( MovementDetailsActivity.this ).getDaoSession ().getMovementHeadersDao ().refresh ( movementHeader );
						// Initialize the movement details list
						movementDetails = new ArrayList < MovementDetails > ();
						// Retrieve the appropriate movement details
						movementDetails.addAll ( DatabaseUtils.getInstance ( MovementDetailsActivity.this ).getDaoSession ().getMovementDetailsDao ().queryBuilder ()
							.where ( MovementDetailsDao.Properties.MovementCode.eq ( movementHeader.getMovementCode () ) ).list () );
						// Iterate over all movement details
						for ( MovementDetails movementDetail : movementDetails )
							// Refresh object
							DatabaseUtils.getInstance ( MovementDetailsActivity.this ).getDaoSession ().getMovementDetailsDao ().refresh ( movementDetail );
						// Retrieve the currency with the utmost priority
						currency = DatabaseUtils.getInstance ( MovementDetailsActivity.this ).getDaoSession ().getCurrenciesDao ().queryBuilder ()
								.orderAsc ( CurrenciesDao.Properties.CurrencyPriority ).unique ();
					} // End if
					// Iterate over all movement details
					for ( MovementDetails movementDetail : movementDetails )
						// Map the current movement detail to its item code
						_movementDetails.put ( movementDetail.getItemCode () , movementDetail );
				} // End if
				
				// Retrieve all divisions
				List < Divisions > allDivisions = DatabaseUtils.getInstance ( MovementDetailsActivity.this ).getDaoSession ().getDivisionsDao ().queryBuilder ()
						.where ( DivisionsDao.Properties.CompanyCode.eq ( companyCode ) ).list ();
				// Retrieve the divisions linked to the user
				List < Divisions > directUserDivisions = DatabaseUtils.getInstance ( MovementDetailsActivity.this ).getDaoSession ().getDivisionsDao ()
						.queryRaw ( " WHERE CompanyCode = ? AND DivisionCode IN ( SELECT DivisionCode FROM UserDivisions WHERE CompanyCode = ? ) " ,
								new String [] { companyCode , companyCode } );
				allDivisions.removeAll ( directUserDivisions );
				// Retrieve the user children division
				List < Divisions > allUserDivisions = DivisionsUtils.getChildren ( directUserDivisions , allDivisions );
				// Add the direct user divisions to the main list
				allUserDivisions.addAll ( directUserDivisions );
				
				// Initialize the map
				HashMap < String , Prices > itemPrices = new HashMap < String , Prices > ();
				// Retrieve the user price list code
				String userPriceListCode = PermissionsUtils.getDefaultPriceListCode ( MovementDetailsActivity.this , userCode , companyCode );
				// Compute the SQL query
				SQL = "SELECT P.* " +
						"FROM " + PricesDao.TABLENAME + " P " +
						"WHERE P." + PricesDao.Properties.PriceListCode.columnName + " = ? " +
						"ORDER BY P." + PricesDao.Properties.ItemCode.columnName;
				// Compute the selection arguments
				selectionArguments = new String [] { userPriceListCode };
				// Query DB in order to retrieve the prices
				cursor = DatabaseUtils.getInstance ( MovementDetailsActivity.this ).getDaoSession ().getDatabase ().rawQuery ( SQL , selectionArguments );
				// Move the cursor to the first raw
				if ( cursor.moveToFirst () ) {
					do {
						// Retrieve the current price
						Prices price = DatabaseUtils.getInstance ( MovementDetailsActivity.this ).getDaoSession ().getPricesDao ().readEntity ( cursor , 0 );
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
					MovementDetailsActivity.this.divisions.addAll ( allUserDivisions );
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
				
				// Check if there are saved movements
				if ( retrieveMovements ) {
					// Retrieve modified movements
					movements = (ArrayList < Movement >) ActivityInstance.readDataGson ( MovementDetailsActivity.this , MovementDetailsActivity.class.getName () , MOVEMENT , new TypeToken < ArrayList < Movement > > () {}.getType () );
					// Initialize the list used to host the selected movements
					selectedMovements = new ArrayList < Movement > ();
					// Iterate over all movements
					for ( Movement movement : movements )
		    			// Check if the current movement has valid quantities
		    			if ( movement.getQuantityBig () != 0 || movement.getQuantityMedium () != 0 || movement.getQuantitySmall () != 0 )
		    				// The movement contains at least one quantity, add it to the movement list
		    				selectedMovements.add ( movement );
				} // End if
				else {
					// Retrieve the client item divisions
					List < ItemDivisions > itemDivisions = EntityUtils.queryByGroup ( selectedDivisionsCodes ,
							DatabaseUtils.getInstance ( MovementDetailsActivity.this ).getDaoSession ().getItemDivisionsDao () ,
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
							DatabaseUtils.getInstance ( MovementDetailsActivity.this ).getDaoSession ().getItemsDao () ,
							ItemsDao.Properties.ItemCode );
					
					// Retrieve the item barcodes
					List < ItemBarcodes > itemBarcodes = EntityUtils.queryByGroup ( itemCodes ,
							DatabaseUtils.getInstance ( MovementDetailsActivity.this ).getDaoSession ().getItemBarcodesDao () ,
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
					List < Units > units = DatabaseUtils.getInstance ( MovementDetailsActivity.this ).getDaoSession ().getUnitsDao ().loadAll ();
					// Map all the Units using their codes
					Map < String , Units > _units = new HashMap < String , Units > ();
					// Iterate over all UOM
					for ( Units unit : units )
						// Map the unit to its code
						_units.put ( unit.getUnitCode () , unit );
					
					// Retrieve all the taxes
					List < Taxes > taxes = DatabaseUtils.getInstance ( MovementDetailsActivity.this ).getDaoSession ().getTaxesDao ().loadAll ();
					// Declare and initialize a map of taxes
					Map < String , Taxes > _taxes = new HashMap < String , Taxes > ();
					// Iterate over all taxes
					for ( Taxes tax : taxes )
						// Map the tax object to its tax code
						_taxes.put ( tax.getTaxCode () , tax );
					
					HashMap < String , SuggestedUserLoad > loadRequestSuggestions = new HashMap < String , SuggestedUserLoad > ();
					// Check if the movement type is load request
					if ( movementSettings.getMovementType () == MovementHeadersUtils.Type.LOAD_REQUEST || movementSettings.getMovementType () == MovementHeadersUtils.Type.Physical_Direct_Load ) {
						Calendar suggestionDate = Calendar.getInstance ();
						suggestionDate.add ( Calendar.DATE , 1 );
						if ( suggestionDate.get ( Calendar.DAY_OF_WEEK ) == Calendar.FRIDAY )
							suggestionDate.add ( Calendar.DATE , 1 );
						String suggestedDate = DateTime.getDayAbbreviation ( MovementDetailsActivity.this , suggestionDate );
						List < SuggestedUserLoad > suggestedUserLoad = DatabaseUtils.getInstance ( MovementDetailsActivity.this ).getDaoSession ().getSuggestedUserLoadDao ().queryBuilder ()
								.where ( SuggestedUserLoadDao.Properties.SuggestedDay.eq ( suggestedDate ) ).list ();
						for ( SuggestedUserLoad load : suggestedUserLoad )
							loadRequestSuggestions.put ( load.getItemCode () , load );
					} // End if
					
					// Retrieve the vehicle stock
					List < VehiclesStock > vehicleStock = DatabaseUtils.getInstance ( MovementDetailsActivity.this ).getDaoSession ().getVehiclesStockDao ().queryBuilder ()
							.where ( VehiclesStockDao.Properties.GoodQuantity.gt ( 0 ) ).list ();
					// Get the item codes of the item history
					HashMap < String , Double > _vehicleStock = new HashMap < String , Double > ();
					for ( VehiclesStock stock : vehicleStock )
						_vehicleStock.put ( stock.getItemCode () , stock.getGoodQuantity () );
					
					// Determine if the view only mode is enabled
					boolean isView = getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false );
					// Determine if the movement is applied (in the case of load or unload only)
					boolean isApplied = movementHeader == null ? false : movementHeader.getIsProcessed () == IsProcessedUtils.isNotSync () && movementHeader.getMovementStatus () == StatusUtils.isAvailable ();
					// Initialize the lists used to host the movements and the selected movements
					movements = new ArrayList < Movement > ();
					selectedMovements = new ArrayList < Movement > ();
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
						// Create a new movement using the current item and its appropriate unit of measurement
						Movement movement = new Movement ( item , _units.get ( item.getUnitCode () ) );
						// Calculate the big medium small conversations
						int mediumSmall = item.getUnitMediumSmall () <= 1 ? 1 : item.getUnitMediumSmall ();
						int bigSmall = item.getUnitBigMedium () <= 1 ? mediumSmall : item.getUnitBigMedium () * mediumSmall;
						// Set the divisions codes list
						movement.setDivisionCodes ( _itemDivisions.get ( item.getItemCode () ) );
						// Check that the division codes list is valid
						if ( movement.getDivisionCodes () == null )
							// Initialize the division codes list
							movement.setDivisionCodes ( new ArrayList < String > () );
						// Set the item bar codes list
						movement.setBarCodes ( _itemBarcodes.get ( item.getItemCode () ) );
						// Set the movement prices
						if ( itemPrices.containsKey ( movement.getItem ().getItemCode () ) ) {
							Prices prices = itemPrices.get ( movement.getItem ().getItemCode () );
							movement.setPriceBig ( prices.getPriceBig () );
							movement.setPriceMedium ( prices.getPriceMedium () );
							movement.setPriceSmall ( prices.getPriceSmall () );
						} // End if
						// Check if the movement type is load request
						if (( movementSettings.getMovementType () == MovementHeadersUtils.Type.LOAD_REQUEST && loadRequestSuggestions.containsKey ( movement.getItem ().getItemCode () )) || ( movementSettings.getMovementType () == MovementHeadersUtils.Type.LOAD_REQUEST && loadRequestSuggestions.containsKey ( movement.getItem ().getItemCode () ))) {
							SuggestedUserLoad suggestedUserLoad = loadRequestSuggestions.get ( movement.getItem ().getItemCode () );
							if ( ItemsUtils.isBig ( item ) )
								movement.setSuggestedBig ( (int) ( suggestedUserLoad.getSuggestedQuantity () / bigSmall ) );
							if ( ItemsUtils.isMedium ( item ) )
								movement.setSuggestedMedium ( (int) ( ( suggestedUserLoad.getSuggestedQuantity () - ( movement.getSuggestedBig () * bigSmall ) ) / mediumSmall ) );
							movement.setSuggestedSmall ( (int) ( suggestedUserLoad.getSuggestedQuantity () - ( movement.getSuggestedBig () * bigSmall ) - ( movement.getSuggestedMedium () * mediumSmall ) ) );
						} // End if
						// Check if the movement type is van transfer out or stock management
						if ( ( movementSettings.getMovementType () == MovementHeadersUtils.Type.VAN_TRANSFER && vanType == VAN_TYPE_OUT )
								|| movementSettings.getMovementType () == MovementHeadersUtils.Type.STOCK_MANAGEMENT ) {
							Double goodQuantity = _vehicleStock.get ( movement.getItem ().getItemCode () );
							if ( goodQuantity == null || goodQuantity <= 0 )
								continue;
							if ( ItemsUtils.isBig ( item ) )
								movement.setSuggestedBig ( (int) ( goodQuantity / bigSmall ) );
							if ( ItemsUtils.isMedium ( item ) )
								movement.setSuggestedMedium ( (int) ( ( goodQuantity - ( movement.getSuggestedBig () * bigSmall ) ) / mediumSmall ) );
							movement.setSuggestedSmall ( (int) ( goodQuantity - ( movement.getSuggestedBig () * bigSmall ) - ( movement.getSuggestedMedium () * mediumSmall ) ) );
						} // End if
						// Set the item tax
						if ( ItemsUtils.isTaxable ( movement.getItem () ) && _taxes.get ( movement.getItem ().getTaxCode () ) != null )
							movement.setTax ( _taxes.get ( movement.getItem ().getTaxCode () ).getTaxPercentage () );
						// Check if there is a previously saved movement detail for the current item
						if ( _movementDetails.containsKey ( movement.getItem ().getItemCode () ) ) {
							// Retrieve a reference to the movement detail
							MovementDetails movementDetails = _movementDetails.get ( movement.getItem ().getItemCode () );
							// Set the movement quantities
							movement.setQuantityBig ( movementDetails.getQuantityBig ().intValue () );
							movement.setQuantityMedium ( movementDetails.getQuantityMedium ().intValue () );
							movement.setQuantitySmall ( movementDetails.getQuantitySmall ().intValue () );
							// Set the movement prices
							movement.setPriceBig ( movementDetails.getPriceBig () );
							movement.setPriceMedium ( movementDetails.getPriceMedium () );
							movement.setPriceSmall ( movementDetails.getPriceSmall () );
							// Set the movement data
							movement.setExpiryDate ( movementDetails.getItemExpiryDate () );
							movement.setSuggestedExpiryDate ( movementDetails.getItemExpiryDate () );
							movement.setReason ( _reasons.get ( movementDetails.getReasonCode () ) );
							movement.setSuggestedReason ( _reasons.get ( movementDetails.getReasonCode () ) );
							// Check if the movement type is load or unload
							if ( movementSettings.getMovementType () == MovementHeadersUtils.Type.LOAD || movementSettings.getMovementType () == MovementHeadersUtils.Type.UNLOAD ) {
								movement.setQuantityBig ( isView ? isApplied ? movementDetails.getQuantityBig ().intValue () : movementDetails.getApprovedQuantityBig ().intValue () : 0 );
								movement.setQuantityMedium ( isView ? isApplied ? movementDetails.getQuantityMedium ().intValue () : movementDetails.getApprovedQuantityMedium ().intValue () : 0 );
								movement.setQuantitySmall ( isView ? isApplied ? movementDetails.getQuantitySmall ().intValue () : movementDetails.getApprovedQuantitySmall ().intValue () : 0 );
								movement.setExpiryDate ( isView ? movementDetails.getItemExpiryDate () : null );
								movement.setReason ( isView ? _reasons.get ( movementDetails.getReasonCode () ) : null );
								movement.setSuggestedBig ( movementDetails.getApprovedQuantityBig ().intValue () );
								movement.setSuggestedMedium ( movementDetails.getApprovedQuantityMedium ().intValue () );
								movement.setSuggestedSmall ( movementDetails.getApprovedQuantitySmall ().intValue () );
								movements.add ( movement );
								if ( isView )
									selectedMovements.add ( movement );
							} // End if
							else
								// Add the current movement to the list of selected movements
								selectedMovements.add ( movement );
						} // End if
						// Check if the movement type is NOT load NOR unload
						if ( movementSettings.getMovementType () != MovementHeadersUtils.Type.LOAD && movementSettings.getMovementType () != MovementHeadersUtils.Type.UNLOAD ) 
							// Add the current item in the movements list
							movements.add ( movement );
					} // End for each
				} // End else
				
				 
				
				
				// Sort the items by alphabetical order
				Collections.sort ( movements , new Comparator < Movement > () {
					@Override
					public int compare ( Movement movement1 , Movement movement2 ) {
						// Sort the items 
						 
							int sequence1 = movement1.getItem ().getItemSequence () == null ? -1 : movement1.getItem ().getItemSequence ();
							int sequence2 = movement2.getItem ().getItemSequence () == null ? -1 : movement2.getItem ().getItemSequence ();
							int order = sequence1 - sequence2;
							if ( order != 0 )
							return order;
						   return movement1.getItem ().getItemName ().compareToIgnoreCase ( movement2.getItem ().getItemName () );
					 
								
								//movement1.getItem ().getItemName ().compareToIgnoreCase ( movement2.getItem ().getItemName () );
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
					// Initialize the movement details
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
		protected void onPostExecute ( ArrayList < Movement > movements ) {
			// Check if the activity has ended
			if ( activityEnded )
				// Do nothing
				return;
			// Determine if the activity must be finished
			if ( error ) {
				// Indicate that the activity cannot be displayed
				new AppToast ( MovementDetailsActivity.this )
					.setIcon ( R.drawable.warning )
					.setText ( AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this , R.string.missing_client_currency_price_list_message ) )
					.setDuration ( Toast.LENGTH_LONG )
					.show ();
				// Initialize the movements list
				MovementDetailsActivity.this.movements = new ArrayList < Movement > ();
				// Exit method
				return;
			} // End if
			
			// Retrieve a reference to the secondary view
			View secondary = findViewById ( R.id.layout_movement_details );
			// Display the secondary view accordingly
			secondary.setVisibility ( displayMovementDetails ? View.VISIBLE : View.GONE );
    		// Enable the main list accordingly
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( ! displayMovementDetails );
    		// Determine if an movement is undergoing any modifications
    		if ( displayMovementDetails )
	    		// Restore the secondary view
    			initializeSecondaryView ( true );
    		
    		// Retrieve a reference to the tertiary view
    		View tertiary = findViewById ( R.id.layout_passcode );
			// Display the tertiary view accordingly
    		tertiary.setVisibility ( displayPasscode ? View.VISIBLE : View.GONE );
    		// Enable the main list accordingly
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( ! displayPasscode );
    		// Determine if the passcode is undergoing any modifications
    		if ( displayPasscode )
	    		// Restore the tertiary view
	    		initializeTertiaryView ( null );
    		
    		// Retrieve a reference to the quaternary view
    		View quaternary = findViewById ( R.id.layout_warehouse_keeper_approval );
			// Display the quaternary view accordingly
    		quaternary.setVisibility ( displayWarehouseKeeperApproval || displayCashVanApproval ? View.VISIBLE : View.GONE );
    		// Enable the main list accordingly
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( ! ( displayWarehouseKeeperApproval || displayCashVanApproval ) );
    		// Determine if the warehouse keeper approval is undergoing any modifications
    		if ( displayWarehouseKeeperApproval )
	    		// Restore the quaternary view
	    		initializeQuaternaryView ( APPROVAL_TYPE_WAREHOUSE , allowWarehouseBarcodeScanning );
    		// Determine if the cash van approval is undergoing any modifications
    		if ( displayCashVanApproval )
	    		// Restore the quaternary view
	    		initializeQuaternaryView ( APPROVAL_TYPE_CASH_VAN , false );
    		
	    	// Declare and initialize a new reason adapter used to populate the reason spinner
    		ReasonAdapter reasonAdapter = new ReasonAdapter ( MovementDetailsActivity.this , android.R.layout.simple_spinner_item , reasons );
			// Sets the layout resource to create the drop down views
    		reasonAdapter.setDropDownViewResource ( R.layout.data_management_activity_connection_item );
    		// Set the spinner adapter
    		( (Spinner) secondary.findViewById ( R.id.spinner_reason ) ).setAdapter ( reasonAdapter );
    		
			// Determine if the request code is INFO
			if ( requestCode != REQUEST_CODE_INFO )
				// Set a new adapter
				setListAdapter ( getMovementDetailsAdapter ( R.layout.sales_order_details_activity_item , new ArrayList < Movement > ( movements ) , currency.getCurrencyRounding () , true ) );
			// Otherwise the request code is INFO
			else {
				// Declare and initialize a multiple list adapter
				MultipleListAdapter adapter = new MultipleListAdapter ( MovementDetailsActivity.this );
				// Add the movement details adapter
				adapter.addSection ( new Section ( AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this , R.string.movement_details_label ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
						new MovementInfoAdapter ( MovementDetailsActivity.this , R.layout.order_info_item , details ) );
				// Add the movement adapter
				adapter.addSection ( new Section ( AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this , R.string.movement_products_label ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
						getMovementDetailsAdapter ( R.layout.sales_order_details_activity_item , selectedMovements , currency.getCurrencyRounding () , ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) ) );
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
	 * Performs all the required initialization for the movement details :
	 * <ul>
	 * <li>The {@link #selectedMovements} list is populated.</li>
	 * <li>The {@link #details} list is populated.</li>
	 * </ul>
	 */
	protected void initializeDetails () {
		// Compute the movement details
		double netValue = 0;
//		double taxes = 0;
		double totalValue = 0;
		// Iterate over all the selected movements
		for ( Movement movement : selectedMovements ) {
			// Compute the current net value
			double _netValue = movement.getQuantityBig () * movement.getPriceBig ()
					+ movement.getQuantityMedium () * movement.getPriceMedium ()
					+ movement.getQuantitySmall () * movement.getPriceSmall ();
			// Compute the current tax value
			double _taxes = 0;
			// Check if the item is taxable
			if ( movement.getTax () != 0 )
				// Compute the current taxes
				_taxes = _netValue * movement.getTax () / 100;
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
		// Initialize the list of movement details
		details = new ArrayList < MovementInfo > ();
		// Populate the details list
		details.add ( new MovementInfo ( MovementInfo.ID.NET_VALUE , AppResources.getInstance ( this ).getString ( this , R.string.net_amount_label ) , moneyFormat.format ( netValue ) ) );
//		details.add ( new MovementInfo ( MovementInfo.ID.TAXES , AppResources.getInstance ( this ).getString ( this , R.string.taxes_label ) , moneyFormat.format ( taxes ) ) );
		details.add ( new MovementInfo ( MovementInfo.ID.TOTAL_VALUE , AppResources.getInstance ( this ).getString ( this , R.string.total_value_label ) , moneyFormat.format ( totalValue ) ) );
		// Check if the movement is valid and is van tranfer
		if ( movementHeader != null && movementHeader.getMovementType () == MovementHeadersUtils.Type.VAN_TRANSFER ) {
			String vanType = AppResources.getInstance ( this ).getString ( this , R.string.not_available_abbreviation );
			if ( movementHeader.getTransferType () == VAN_TYPE_IN )
				vanType = AppResources.getInstance ( this ).getString ( this , R.string.movement_type_van_transfer_in_label );
			else if ( movementHeader.getTransferType () == VAN_TYPE_OUT )
				vanType = AppResources.getInstance ( this ).getString ( this , R.string.movement_type_van_transfer_out_label );
			details.add ( new MovementInfo ( MovementInfo.ID.VAN_TYPE , AppResources.getInstance ( this ).getString ( this , R.string.movement_type_van_transfer_label ) ,vanType ) );
		} // End if
	}
	
	/**
	 * Refreshes / recomputes all the movement details (if any).
	 */
	private void refreshDetails () {
		// Check if the selected movements and movement details lists are valid
		if ( selectedMovements == null || details == null )
			// Invalid list, do nothing
			return;
		
		// Compute the movement details
		double netValue = 0;
		double taxes = 0;
		double totalValue = 0;
		// Iterate over all the selected movements
		for ( Movement movement : selectedMovements ) {
			// Compute the current net value
			double _netValue = movement.getQuantityBig () * movement.getPriceBig ()
					+ movement.getQuantityMedium () * movement.getPriceMedium ()
					+ movement.getQuantitySmall () * movement.getPriceSmall ();
			// Compute the current tax value
			double _taxes = 0;
			// Check if the item is taxable
			if ( movement.getTax () != 0 )
				// Compute the current taxes
				_taxes = _netValue * movement.getTax () / 100;
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
		for ( MovementInfo detail : details ) {
			// Check if the current detail is about the movement net value
			if ( detail.getId () == MovementInfo.ID.NET_VALUE ) {
				// Update the movement net value
				detail.setValue ( moneyFormat.format ( netValue ) );
			} // End if
			// Check if the current detail is about the movement taxes
			else if ( detail.getId () == MovementInfo.ID.TAXES ) {
				// Update the movement taxes
				detail.setValue ( moneyFormat.format ( taxes ) );
			} // End else if
			// Check if the current detail is about the movement total value
			else if ( detail.getId () == MovementInfo.ID.TOTAL_VALUE ) {
				// Update the movement total value
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
	private class FilterList extends AsyncTask < Void , Void , ArrayList < Movement > > {
		
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
		protected ArrayList < Movement > doInBackground ( Void ... params ) {
			// Check if the list of movements is valid
			if ( movements == null || movements.isEmpty () )
				// Invalid movements list
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
		protected void onPostExecute ( ArrayList < Movement > movements ) {
			// Check if the request code is INFO
			if ( requestCode == REQUEST_CODE_INFO )
				// Cannot filter, do nothing
				return;
			// Check if the result is valid
			if ( movements == null )
				// Invalid result, do nothing
				return;
			// Check if the search query has changed
			if ( ! MovementDetailsActivity.this.searchQuery.equals ( searchQuery ) )
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
				// Clear the previous list of movements
				( (ArrayAdapter < Movement >) getListAdapter () ).clear ();
				// Add the new filtered list of movements
				( (ArrayAdapter < Movement >) getListAdapter () ).addAll ( movements );
				// Notifies the attached observers that the underlying data has been changed
				( (ArrayAdapter < Movement >) getListAdapter () ).notifyDataSetChanged ();
			} catch ( Exception exception ) {
				// Cannot cast to array adapter of movement objects
			} // End try-catch block
		}
		
	}
	
	
	/**
	 * AsyncTask helper class used to receive warehouse quantities data from the server asynchronously.
	 * 
	 * @author Elias
	 *
	 */
	private class SyncWarehouseQuantities extends AsyncTask < Void , Void , Boolean > {
		
		/**
		 * List of {@link me.SyncWise.Android.Database.WarehouseQuantities WarehouseQuantities} hosting warehouse quantities.
		 */
		private List < WarehouseQuantities > warehouseQuantities;
		
		/**
		 * Constructor.
		 * 
		 * @param warehouseQuantities	List of {@link me.SyncWise.Android.Database.WarehouseQuantities WarehouseQuantities} hosting warehouse quantities.
		 */
		public SyncWarehouseQuantities ( final List < WarehouseQuantities > warehouseQuantities ) {
			this.warehouseQuantities = warehouseQuantities;
		}
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Boolean doInBackground ( Void ... params ) {
			try {
				// Begin transaction
				DatabaseUtils.getInstance ( getApplicationContext () ).getDaoSession ().getDatabase ().beginTransaction ();
				// Delete all previous warehouse quantities
				DatabaseUtils.getInstance ( getApplicationContext () ).getDaoSession ().getWarehouseQuantitiesDao ().deleteAll ();
				// Iterate over each warehouse quantity
				for ( Object entity : warehouseQuantities )
					// Insert data
					DatabaseUtils.getInstance ( getApplicationContext () ).getDaoSession ().insert ( entity );
				// Commit changes
				DatabaseUtils.getInstance ( getApplicationContext () ).getDaoSession ().getDatabase ().setTransactionSuccessful ();
				
				// Declare and initialize a list used to host item's warehouse quantities
				ArrayList < WarehouseQuantity > itemsQuantities = new ArrayList < WarehouseQuantity > ();
				// Iterate over all quantities
				for ( WarehouseQuantities quantity : warehouseQuantities ) {
					// Declare and initialize a new warehouse quantity related to the current item
					WarehouseQuantity item = new WarehouseQuantity ( quantity.getItemCode () );
					// Search for the current item in the list
					int index = itemsQuantities.indexOf ( item );
					// Check if the item is already added to the list
					if ( index == -1 ) {
						// Update the quantities
						item.setQuantityBig ( quantity.getQuantityBig () );
						item.setQuantityMedium ( quantity.getQuantityMedium () );
						item.setQuantitySmall ( quantity.getQuantitySmall () );
						// Add the new item to the list
						itemsQuantities.add ( item );
					} // End if
					else {
						// Retrieve a reference to the existing item
						WarehouseQuantity _item = itemsQuantities.get ( index );
						// Update the quantities
						_item.setQuantityBig ( _item.getQuantityBig () + quantity.getQuantityBig () );
						_item.setQuantityMedium ( _item.getQuantityMedium () + quantity.getQuantityMedium () );
						_item.setQuantitySmall ( _item.getQuantitySmall () + quantity.getQuantitySmall () );
					} // End else
				} // End for loop
				
				// Update the item's warehouse quantities
				MovementDetailsActivity.this.warehouseQuantities = itemsQuantities;
			} catch ( Exception exception ) {
				// Indicate that an error occurred
				return false;
			} finally {
				// End transaction
				DatabaseUtils.getInstance ( getApplicationContext () ).getDaoSession ().getDatabase ().endTransaction ();
				// Clear reference
				warehouseQuantities = null;
			} // End of try-catch-finally block
			
			// Indicate that the warehouse quantities were successfully retrieved
			return true;
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( Boolean success ) {
			try {
				// Check if an error occurred
				if ( success == null || ! success )
					// Do nothing
					return;
				
				// Indicate that the warehouse quantities were updated
				Baguette.showText ( MovementDetailsActivity.this ,
						AppResources.getInstance ( MovementDetailsActivity.this ).getString ( MovementDetailsActivity.this , R.string.warehouse_quantities_updated_message ) ,
						Baguette.BackgroundColor.BLUE );
				
				// Refresh the list
				if ( getListAdapter () instanceof MovementDetailsAdapter )
					( (MovementDetailsAdapter) getListAdapter () ).notifyDataSetChanged ();
				else if ( getListAdapter () instanceof MultipleListAdapter )
					( (MultipleListAdapter) getListAdapter () ).notifyDataSetChanged ();
			} catch ( Exception exception ) {
				// Do nothing
			} // End of try-catch block
		}
		
	}
	
	
}