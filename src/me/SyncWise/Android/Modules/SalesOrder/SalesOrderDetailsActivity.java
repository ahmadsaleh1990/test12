/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.SalesOrder;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppDialog.ButtonsType;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.AppToast;
import me.SyncWise.Android.Network;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Vibration;
import me.SyncWise.Android.Database.ClientCreditingsDao;
import me.SyncWise.Android.Database.ClientCurrenciesDao;
import me.SyncWise.Android.Database.ClientDuesDao;
import me.SyncWise.Android.Database.ClientItemCodes;
import me.SyncWise.Android.Database.ClientItemCodesDao;
import me.SyncWise.Android.Database.ClientItemHistory;
import me.SyncWise.Android.Database.ClientItemHistoryDao;
import me.SyncWise.Android.Database.ClientMustStockList;
import me.SyncWise.Android.Database.ClientNewSkuList;
import me.SyncWise.Android.Database.ClientPriceListsDao;
import me.SyncWise.Android.Database.ClientPropertiesDao;
import me.SyncWise.Android.Database.ClientSellingSuggestion;
import me.SyncWise.Android.Database.ClientSellingSuggestionDao;
import me.SyncWise.Android.Database.ClientStockCountDetails;
import me.SyncWise.Android.Database.ClientStockCountDetailsDao;
import me.SyncWise.Android.Database.ClientStockCountHeaders;
import me.SyncWise.Android.Database.ClientStockCountHeadersDao;
import me.SyncWise.Android.Database.Clients;
import me.SyncWise.Android.Database.CollectionHeadersDao;
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
import me.SyncWise.Android.Database.Items;
import me.SyncWise.Android.Database.ItemsDao;
import me.SyncWise.Android.Database.ItemsUtils;
import me.SyncWise.Android.Database.MSLDivisions;
import me.SyncWise.Android.Database.MSLDivisionsDao;
import me.SyncWise.Android.Database.NewSkuListDivisions;
import me.SyncWise.Android.Database.NewSkuListDivisionsDao;
import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Database.PriceListsDao;
import me.SyncWise.Android.Database.Prices;
import me.SyncWise.Android.Database.PricesDao;
import me.SyncWise.Android.Database.PromotionAssignmentsDao;
import me.SyncWise.Android.Database.PromotionAssignmentsUtils;
import me.SyncWise.Android.Database.PromotionDetails;
import me.SyncWise.Android.Database.PromotionDetailsDao;
import me.SyncWise.Android.Database.PromotionHeaders;
import me.SyncWise.Android.Database.PromotionHeadersDao;
import me.SyncWise.Android.Database.PromotionUtils;
import me.SyncWise.Android.Database.StatusUtils;
import me.SyncWise.Android.Database.TargetHeadersUtils;
import me.SyncWise.Android.Database.Taxes;
import me.SyncWise.Android.Database.TransactionDetails;
import me.SyncWise.Android.Database.TransactionDetailsDao;
import me.SyncWise.Android.Database.TransactionDetailsMissedMSL;
import me.SyncWise.Android.Database.TransactionDetailsMissedMSLDao;
import me.SyncWise.Android.Database.TransactionDetailsUtils;
import me.SyncWise.Android.Database.TransactionHeaders;
import me.SyncWise.Android.Database.TransactionHeadersDao;
import me.SyncWise.Android.Database.TransactionHeadersUtils;
import me.SyncWise.Android.Database.TransactionPromotionDetails;
import me.SyncWise.Android.Database.TransactionSequences;
import me.SyncWise.Android.Database.TransactionSequencesDao;
import me.SyncWise.Android.Database.Units;
import me.SyncWise.Android.Database.Users;
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Database.Visits;
import me.SyncWise.Android.Database.WarehouseQuantities;
import me.SyncWise.Android.Gson.BaseTimerListActivity;
import me.SyncWise.Android.Modules.BarcodeIntegration.IntentIntegrator;
import me.SyncWise.Android.Modules.BarcodeIntegration.IntentResult;
import me.SyncWise.Android.Modules.Call.CallAction;
import me.SyncWise.Android.Modules.DataManagement.DataManagementActivity.RequestCode;
import me.SyncWise.Android.Modules.FilterDivisions.FilterDivisionsActivity;
import me.SyncWise.Android.Modules.Journey.Call;
import me.SyncWise.Android.Modules.Printing.PrintingActivity;
import me.SyncWise.Android.Modules.SalesInvoice.Invoice;
import me.SyncWise.Android.Modules.SalesOrder.SalesOrderDetailsAdapter.ViewHolder;
import me.SyncWise.Android.Modules.Sync.SyncHelper;
import me.SyncWise.Android.Modules.Sync.SyncListener;
import me.SyncWise.Android.Modules.Target.TargetUpdate;
import me.SyncWise.Android.Modules.Target.TargetUpdate.UpdateType;
import me.SyncWise.Android.SectionsList.MultipleListAdapter;
import me.SyncWise.Android.SectionsList.Section;
import me.SyncWise.Android.Utilities.ActivityInstance;
import me.SyncWise.Android.Utilities.ClientCard;
import me.SyncWise.Android.Utilities.ExternalStorageFilter;
import me.SyncWise.Android.Utilities.ItemCard;
import me.SyncWise.Android.Utilities.PictureFilter;
import me.SyncWise.Android.Widgets.Baguette;
import me.SyncWise.Android.Widgets.CustomLinearLayout;
import me.SyncWise.Android.Widgets.CoverFlow.CoverFlow;
import me.SyncWise.Android.Widgets.CoverFlow.PathImageAdapter;
import me.SyncWise.Android.Widgets.CoverFlow.ReflectingImageAdapter;
import me.SyncWise.Android.Widgets.QuickAction.ActionItem;
import me.SyncWise.Android.Widgets.QuickAction.QuickAction;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import de.greenrobot.dao.AbstractDao;

/**
 * Activity implemented to perform, view or edit a sales order.
 * 
 * @author Elias -- Ahmad 
 * @sw.todo <b>Sales Order Details Activity Implementation :</b><br>
 * Simply add this class in the {@code AndroidManifest.xml} file along with the permission :<br>
 * {@code <uses-permission android:name="android.permission.VIBRATE"/>}<br>
 * AND disable activity recreation for orientation events by adding the following to the activity tag in the manifest file :<br>
 * {@code android:configChanges="orientation|screenSize"}
 * 
 */
public class SalesOrderDetailsActivity extends BaseTimerListActivity implements SearchView.OnQueryTextListener ,OnClickListener , QuickAction.OnActionItemClickListener , SyncListener, OnItemLongClickListener {
	private boolean saveMovements ;
	private static  final  String SAVE_MOVEMENTS = SalesOrderDetailsActivity.class.getName () + ".SAVE_MOVEMENTS";

	/**
	 * Bundle key used to put/retrieve the content of {@link #isCreated}.
	 */
	private static final String IS_CREATED = SalesOrderDetailsActivity.class.getName () + ".IS_CREATED";
	
	/**
	 * Boolean used to indicate if the activity has been previously created.<br>
	 * This flag is mainly used to determine if the current activity creation is the first or not (activity re-creation due to phone rotation for example).
	 */
	private boolean isCreated;
	private HashMap < String , ArrayList < PromotionDetails > > instantPromotions;
public static final String BASKET_PROMOTIONS = SalesOrderDetailsActivity.class.getName () + ".BASKET_PROMOTIONS";
protected boolean displayBasketPromotions;
private static final String DISPLAY_BASKET_PROMOTIONS = SalesOrderDetailsActivity.class.getName () + ".DISPLAY_BASKET_PROMOTIONS";

	/**
	 * List of {@linkme.SyncWise.Android.SalesInvoice.BasketPromotion BasketPromotion} holding the basket promotions.
	 */
	private ArrayList < BasketPromotion > basketPromotions;
	/**
	 * Boolean used to indicate if the activity is displayed on the screen.
	 */
	public static boolean isDisplayed;
	
	/**
	 * Reference to the search view in the action bar.
	 */
	private SearchView searchView;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #requestCode}.
	 */
	public static final String REQUEST_CODE = SalesOrderDetailsActivity.class.getName () + ".REQUEST_CODE";
	/**
	 * Bundle key used to put/retrieve the content of {@link #requestCode}.
	 */
	public static final String PriceList = SalesOrderDetailsActivity.class.getName () + ".PriceList";
	
	/**
	 * Integer used to host the request code.
	 * @see #REQUEST_CODE_NEW
	 * @see #REQUEST_CODE_INFO
	 */
	protected int requestCode;
	
	/**
	 * Constant integer holding the request code to create a new sales order.
	 */
	public static final int REQUEST_CODE_NEW = 1;
	
	/**
	 * Constant integer holding the request code to display the sales order info.<br>
	 * The ordered items should be displayed.
	 */
	public static final int REQUEST_CODE_INFO = 2;
	
	/**
	 * Constant integer holding the request code to display the sales order details.<br>
	 * The sales order details (tax, discount, total, ...) should be displayed.
	 */
	public static final int REQUEST_CODE_DETAILS = 3;
	
	/**
	 * Constant integer holding the request code used to print orders.
	 */
	private static final int REQUEST_CODE_PRINT = 10;

	/**
	 * Bundle key used to put/retrieve the content of the edit flag.
	 */
	public static final String IS_EDIT = SalesOrderDetailsActivity.class.getName () + ".IS_EDIT";
	
	/**
	 * Bundle key used to put/retrieve the content of the view only flag.
	 */
	public static final String IS_VIEW_ONLY = SalesOrderDetailsActivity.class.getName () + ".IS_VIEW_ONLY";	
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #orderModification}.
	 */
	private static final String ORDER_MODIFICATION = SalesOrderDetailsActivity.class.getName () + ".ORDER_MODIFICATION";
	
	/**
	 * Reference to the {@link me.SyncWise.Android.Modules.SalesOrder.Order Order} object being modified.
	 */
	protected Order orderModification;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #displayOrderNote}.
	 */
	private static final String DISPLAY_ORDER_NOTE = SalesOrderDetailsActivity.class.getName () + ".DISPLAY_ORDER_NOTE";
	
	/**
	 * Boolean used to determine whether to display the order note UI or not.<br>
	 * This boolean is mainly used to save the order state.
	 */
	protected boolean displayOrderNote;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #displayOrderNote}.
	 */
	private static final String DISPLAY_ORDER_NOTE_NEW = SalesOrderDetailsActivity.class.getName () + ".DISPLAY_ORDER_NOTE_NEW";
	
	/**
	 * Boolean used to determine whether to display the order note UI or not.<br>
	 * This boolean is mainly used to save the order state.
	 */
	protected boolean displayOrderNoteNew;
	private String priceListf;
	/**
	 * Bundle key used to put/retrieve the content of {@link #displayPrintingConfirmation}.
	 */
	private static final String PRINTING_TRANSACTION_CODE = SalesOrderDetailsActivity.class.getName () + ".PRINTING_TRANSACTION_CODE";
	
	/**
	 * String used to host the transaction code of the transaction to print.
	 */
	private String printingTransactionCode;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #displayPrintingConfirmation}.
	 */
	private static final String DISPLAY_PRINTING_CONFIRMATION = SalesOrderDetailsActivity.class.getName () + ".DISPLAY_PRINTING_CONFIRMATION";
	
	/**
	 * Boolean used to determine whether to display the printing confirmation.
	 */
	private boolean displayPrintingConfirmation;
	
	/**
	 * Bundle key used to put/retrieve the content of the transaction header code.<br>
	 * This is used mainly to edit a sales order.
	 */
	public static final String TRANSACTION_HEADER_CODE = SalesOrderDetailsActivity.class.getName () + ".TRANSACTION_HEADER_CODE";
	
	/**
	 * Bundle key used to put/retrieve the content of the current call.
	 */
	public static final String CALL = SalesOrderDetailsActivity.class.getName () + ".CALL";
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #visit}.
	 */
	public static final String VISIT = SalesOrderDetailsActivity.class.getName () + ".VISIT";
	
	/**
	 * Boolean indicating if the promotions are enabled in the sales order taking.
	 */
	private boolean isPromotionEnabled;
	
	/**
	 * {@link me.SyncWise.Android.Database.Visits Visits} object holding a reference to the call in progress.
	 */
	protected Visits visit = null;
	
	/**
	 * List of {@link me.SyncWise.Android.Database.Divisions Divisions} objects used to host all the available objects for the current transaction.
	 */
	protected ArrayList < Divisions > divisions;
	protected String priceList;
	/**
	 * Bundle key used to put/retrieve the content of {@link #SELECTED_DIVISIONS}.
	 */
	public static final String SELECTED_DIVISIONS = SalesOrderDetailsActivity.class.getName () + ".SELECTED_DIVISIONS";
	
	/**
	 * List of string used to host the selected divisions codes, in order to filter the items / sales orders list.
	 */
	protected ArrayList < String > selectedDivisionsCodes;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #orders}.
	 */
	public static final String ORDERS = SalesOrderDetailsActivity.class.getName () + ".ORDERS";
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.SalesOrder.Order Order} objects used to define the sales orders.<br>
	 * <b>NOTE :</b> <br>
	 * If a filter is applied, this list remains untouched. A new list is generated, filtered and passed to the list adapter.
	 */
	protected ArrayList < Order > orders;
	
	/**
	 * Boolean used to indicate if there saved orders that should be retrieved.
	 */
	protected boolean retrieveOrders;
	
	/**
	 * List of {@link me.SyncWise.Android.AlfaLabs.Modules.SalesOrder.WarehouseQuantity WarehouseQuantity} hosting all the items' warehouses quantities.
	 */
	public ArrayList < WarehouseQuantity > warehouseQuantities;
	
	/**
	 * Reference to the orders list population task.
	 */
	protected static PopulateList populateList;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #searchQuery}.
	 */
	public static final String SEARCH_QUERY = SalesOrderDetailsActivity.class.getName () + ".SEARCH_QUERY";
	
	/**
	 * String holding the search query.
	 */
	private String searchQuery;
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.SalesOrder.Order Order} objects used to define the selected sales orders.
	 */
	protected ArrayList < Order > selectedOrders;
	protected ArrayList < Order > freeOrders;
	protected ArrayList < Order > salesOrderMSL = new ArrayList < Order > ();
	protected ArrayList < Order > salesOrderNEWSKU = new ArrayList < Order > ();
	protected ArrayList < Order > salesOrders = new ArrayList < Order > ();
	/**
	 * Bundle key used to put/retrieve the content of {@link #selectedOrdersIndex}.
	 */
	public static final String SELECTED_ORDERS_INDEX = SalesOrderDetailsActivity.class.getName () + ".SELECTED_ORDERS_INDEX";
	
	/**
	 * List of strings holding the selected orders' item codes in the order in which the user used.
	 */
	protected ArrayList < String > selectedOrdersIndex;

	/**
	 * List of {@link me.SyncWise.Android.Modules.SalesOrder.OrderInfo SalesOrderDetail} objects used to define the sales orders details.
	 */
	protected ArrayList < OrderInfo > details;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #selectedYear}.
	 */
	public static final String SELECTED_YEAR = SalesOrderDetailsActivity.class.getName () + ".SELECTED_YEAR";
	
	/**
	 * Integer holding the selected year of the delivery date.
	 */
	protected int selectedYear;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #selectedMonth}.
	 */
	public static final String SELECTED_MONTH = SalesOrderDetailsActivity.class.getName () + ".SELECTED_MONTH";
	
	/**
	 * Integer holding the selected month of the delivery date.
	 */
	protected int selectedMonth;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #selectedDay}.
	 */
	public static final String SELECTED_DAY = SalesOrderDetailsActivity.class.getName () + ".SELECTED_DAY";
	
	/**
	 * Integer holding the selected day of the delivery date.
	 */
	protected int selectedDay;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #note}.
	 */
	public static final String NOTE = SalesOrderDetailsActivity.class.getName () + ".NOTE";
	
	/**
	 * String holding the sales order note.
	 */
	protected String note;
	
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #note}.
	 */
	public static final String NOTENEW = SalesOrderDetailsActivity.class.getName () + ".NOTENEW";
	
	/**
	 * String holding the sales order note.
	 */
	protected String noteNew;
	
	
	
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #currency}.
	 */
	public static final String CURRENCY = SalesOrderDetailsActivity.class.getName () + ".CURRENCY";
	public static String selectedItemIconCode; 
	protected boolean displayOrderImage;
	protected boolean isdisable;
	private static final String DISPLAY_ORDER_IMAGE = SalesOrderDetailsActivity.class.getName () + ".DISPLAY_ORDER_IMAGE";

	/**
	 * Reference to the currency object.
	 */
	protected Currencies currency;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #transactionHeader}.
	 */
	public static final String TRANSACTION_HEADER = SalesOrderDetailsActivity.class.getName () + ".TRANSACTION_HEADER";
	
	/**
	 * Reference to the transaction header stored in DB (if previously saved)
	 */
	protected TransactionHeaders transactionHeader;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #transactionDetails}.
	 */
	public static final String TRANSACTION_DETAILS = SalesOrderDetailsActivity.class.getName () + ".TRANSACTION_DETAILS";
	
	/**
	 * List of transaction details references stored in DB (if previously saved)
	 */
	protected ArrayList < TransactionDetails > transactionDetails;
	
	/**
	 * Reference to a {@link me.SyncWise.Android.Widgets.QuickAction.QuickAction QuickAction} used in this activity.
	 */
	private QuickAction quickAction;
	
	private static final String ERROR = SalesOrderDetailsActivity.class.getName () + ".ERROR";
	
	private boolean error;
	
	/**
	 * Map hosting the promotion headers related to current client or user, mapped to their promotion IDs.
	 */
	private SparseArray < PromotionHeaders > promotionHeaders;
	
	protected ArrayList < Order > promotedOrders;
	
	private HashMap < String , ArrayList < PromotionDetails > > _promotions;
	
	private ArrayList < Integer > headerID;

	private HashMap < String , Order > _orders;
	
	private ArrayList < TransactionPromotionDetails > transactionPromotionDetails;
	/**
	 * Map hosting the promotion headers related to current client or user, mapped to their promotion IDs.
	 */
 
	
	private SparseArray < PromotionHeaders > promotionHeadersF;
	
	 
	
	private HashMap < String , ArrayList < PromotionDetails > > _promotionsF;
	
 

	private ArrayList < Integer > headerIDF;
	
	
	private SparseArray < PromotionHeaders > promotionHeadersPricelist;
	
	 
	
	public HashMap < String , ArrayList < PromotionDetails > > _promotionsPricelist;
	
 

	private ArrayList < Integer > headerIDPricelist;
	
	
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
     * @param orderModification	Reference to the {@link me.SyncWise.Android.Modules.SalesOrder.Order Order} object being modified.
     */
    public void setOrderModification ( final Order orderModification ) {
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
    	// Determine if the list view adapter's instance is of SalesOrderDetailsAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof SalesOrderDetailsAdapter ) {
    		// Return the adapter's money format
    		return ( (SalesOrderDetailsAdapter) getListAdapter () ).getMoneyFormat ();
    	} // End if
    	// Determine if the list view adapter's instance is MultipleListAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof MultipleListAdapter ) {
    		// Return the SalesOrderDetailsAdapter's money format
    		return ( (SalesOrderDetailsAdapter) ( (MultipleListAdapter) getListAdapter () ).getAdapter ( ( (MultipleListAdapter) getListAdapter () ).getCount () - 1 ) )
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
    	if ( getListAdapter () != null && getListAdapter () instanceof SalesOrderDetailsAdapter ) {
    		// Return the adapter's money format
    		return ( (SalesOrderDetailsAdapter) getListAdapter () ).getCodeLabel ();
    	} // End if
    	// Determine if the list view adapter's instance is MultipleListAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof MultipleListAdapter ) {
    		// Return the SalesOrderDetailsAdapter's money format
    		return ( (SalesOrderDetailsAdapter) ( (MultipleListAdapter) getListAdapter () ).getAdapter ( ( (MultipleListAdapter) getListAdapter () ).getCount () - 1 ) )
    				.getCodeLabel ();
    	} // End if
		// Otherwise return null
		return null;
    }
   
    public String getItemBarcodeLabel () {
    	// Check if the list view adapter 
    	if ( getListAdapter () == null )
    		// Invalid data
    		return null;
    	// Determine if the list view adapter's instance is of SalesOrderDetailsAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof SalesOrderDetailsAdapter ) {
    		// Return the adapter's money format
    		return ( (SalesOrderDetailsAdapter) getListAdapter () ).getItemBarcode  ();
    	} // End if
    	// Determine if the list view adapter's instance is MultipleListAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof MultipleListAdapter ) {
    		// Return the SalesOrderDetailsAdapter's money format
    		return ( (SalesOrderDetailsAdapter) ( (MultipleListAdapter) getListAdapter () ).getAdapter ( ( (MultipleListAdapter) getListAdapter () ).getCount () - 1 ) )
    				.getItemBarcode ();
    	} // End if
		// Otherwise return null
		return null;
    }
    public String getClientItemBarcodeLabel () {
    	// Check if the list view adapter 
    	if ( getListAdapter () == null )
    		// Invalid data
    		return null;
    	// Determine if the list view adapter's instance is of SalesOrderDetailsAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof SalesOrderDetailsAdapter ) {
    		// Return the adapter's money format
    		return ( (SalesOrderDetailsAdapter) getListAdapter () ).getClientItemCode()  ;
    	} // End if
    	// Determine if the list view adapter's instance is MultipleListAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof MultipleListAdapter ) {
    		// Return the SalesOrderDetailsAdapter's money format
    		return ( (SalesOrderDetailsAdapter) ( (MultipleListAdapter) getListAdapter () ).getAdapter ( ( (MultipleListAdapter) getListAdapter () ).getCount () - 1 ) )
    				.getClientItemCode ();
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
    	if ( getListAdapter () != null && getListAdapter () instanceof SalesOrderDetailsAdapter ) {
    		// Return the adapter's money format
    		return ( (SalesOrderDetailsAdapter) getListAdapter () ).getTotalLabel ();
    	} // End if
    	// Determine if the list view adapter's instance is MultipleListAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof MultipleListAdapter ) {
    		// Return the SalesOrderDetailsAdapter's money format
    		return ( (SalesOrderDetailsAdapter) ( (MultipleListAdapter) getListAdapter () ).getAdapter ( ( (MultipleListAdapter) getListAdapter () ).getCount () - 1 ) )
    				.getTotalLabel ();
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
    	if ( getListAdapter () != null && getListAdapter () instanceof SalesOrderDetailsAdapter ) {
    		// Return the adapter's money format
    		return ( (SalesOrderDetailsAdapter) getListAdapter () ).getNewLine ();
    	} // End if
    	// Determine if the list view adapter's instance is MultipleListAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof MultipleListAdapter ) {
    		// Return the SalesOrderDetailsAdapter's money format
    		return ( (SalesOrderDetailsAdapter) ( (MultipleListAdapter) getListAdapter () ).getAdapter ( ( (MultipleListAdapter) getListAdapter () ).getCount () - 1 ) )
    				.getNewLine ();
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
    	if ( getListAdapter () != null && getListAdapter () instanceof SalesOrderDetailsAdapter ) {
    		// Return the adapter's money format
    		return ( (SalesOrderDetailsAdapter) getListAdapter () ).getBrownColor ();
    	} // End if
    	// Determine if the list view adapter's instance is MultipleListAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof MultipleListAdapter ) {
    		// Return the SalesOrderDetailsAdapter's money format
    		return ( (SalesOrderDetailsAdapter) ( (MultipleListAdapter) getListAdapter () ).getAdapter ( ( (MultipleListAdapter) getListAdapter () ).getCount () - 1 ) )
    				.getBrownColor ();
    	} // End if
		// Otherwise return null
		return null;
    }
    
    /**
     * Returns the appropriate Integer hosting the misty rose color.
     * 
     * @return	Integer hosting the misty rose color.
     */
    public Integer getMistyRose () {
    	// Check if the list view adapter 
    	if ( getListAdapter () == null )
    		// Invalid data
    		return null;
    	// Determine if the list view adapter's instance is of SalesOrderDetailsAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof SalesOrderDetailsAdapter ) {
    		// Return the adapter's money format
    		return ( (SalesOrderDetailsAdapter) getListAdapter () ).getMistyRose ();
    	} // End if
    	// Determine if the list view adapter's instance is MultipleListAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof MultipleListAdapter ) {
    		// Return the SalesOrderDetailsAdapter's money format
    		return ( (SalesOrderDetailsAdapter) ( (MultipleListAdapter) getListAdapter () ).getAdapter ( ( (MultipleListAdapter) getListAdapter () ).getCount () - 1 ) )
    				.getMistyRose ();
    	} // End if
		// Otherwise return null
		return null;
    }
     private  int lastExpandedPosition = -1;
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
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.sales_order_activity_title ) );
		// Set the activity content from a layout resource.
		setContentView ( R.layout.sales_order_details_activity );
		// Enable fast scrolling
		getListView ().setFastScrollEnabled ( true );
		// Define the empty list view
		getListView ().setEmptyView ( findViewById ( R.id.empty_list_view ) );
		// Display empty list label
		( (TextView) findViewById ( R.id.empty_list_view ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_items_list_label ) );
		 final View quaternary = findViewById ( R.id.layout_basket_promotions );
		// Define the empty list view
		( (ExpandableListView) quaternary.findViewById ( R.id.basket_promotions_list ) ).setEmptyView ( quaternary.findViewById ( R.id.empty_list_view ) );
		// Set the expandable list view listener
		( (ExpandableListView) quaternary.findViewById ( R.id.basket_promotions_list ) ).setOnItemLongClickListener ( this );
		saveMovements=true;
		( (ExpandableListView) quaternary.findViewById ( R.id.basket_promotions_list ) ).setOnGroupExpandListener(new OnGroupExpandListener() {

			  @Override
			  public void onGroupExpand(int groupPosition) {
			      if (lastExpandedPosition != -1
			          && groupPosition != lastExpandedPosition) {
			    	  ( (ExpandableListView) quaternary.findViewById ( R.id.basket_promotions_list ) )
			    	  .collapseGroup(lastExpandedPosition);
			      }
			      lastExpandedPosition = groupPosition;
			  }
			});
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
		
		// Retrieve the user code
		String userCode = DatabaseUtils.getCurrentUserCode ( this );
		// Retrieve the company code
		String companyCode = DatabaseUtils.getCurrentCompanyCode ( this );

        // Perform the quick action setup
        setupQuickAction ();
		
		// Initialize the sales order note
		note = "";
		noteNew = "";
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
			isCreated = savedInstanceState.getBoolean ( IS_CREATED , isCreated );
			searchQuery = savedInstanceState.getString ( SEARCH_QUERY );
			selectedDivisionsCodes = savedInstanceState.getStringArrayList ( SELECTED_DIVISIONS );
			retrieveOrders = savedInstanceState.getBoolean ( ORDERS );
			selectedOrdersIndex = savedInstanceState.getStringArrayList ( SELECTED_ORDERS_INDEX );
			requestCode = savedInstanceState.getInt ( REQUEST_CODE );
			selectedYear = savedInstanceState.getInt ( SELECTED_YEAR );
			selectedMonth = savedInstanceState.getInt ( SELECTED_MONTH );
			selectedDay = savedInstanceState.getInt ( SELECTED_DAY );
			note = savedInstanceState.getString ( NOTE );
			noteNew=savedInstanceState.getString(NOTENEW);
			saveMovements = savedInstanceState.getBoolean ( SAVE_MOVEMENTS , saveMovements );
			transactionHeader = (TransactionHeaders) savedInstanceState.getSerializable ( TRANSACTION_HEADER );
			transactionDetails = (ArrayList < TransactionDetails >) savedInstanceState.getSerializable ( TRANSACTION_DETAILS );
			displayOrderNote = savedInstanceState.getBoolean ( DISPLAY_ORDER_NOTE );
			displayOrderNoteNew = savedInstanceState.getBoolean ( DISPLAY_ORDER_NOTE_NEW );
			displayPrintingConfirmation = savedInstanceState.getBoolean ( DISPLAY_PRINTING_CONFIRMATION );
			printingTransactionCode = savedInstanceState.getString ( PRINTING_TRANSACTION_CODE );
			orderModification = (Order) savedInstanceState.getSerializable ( ORDER_MODIFICATION );
			error = savedInstanceState.getBoolean ( ERROR );
			displayBasketPromotions = savedInstanceState.getBoolean ( DISPLAY_BASKET_PROMOTIONS );
			displayOrderImage = savedInstanceState.getBoolean ( DISPLAY_ORDER_IMAGE );
			
			
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
		
		// Initialize the promoted orders list
		promotedOrders = new ArrayList < Order > ();
		// Determine if promotions are enabled
		isPromotionEnabled = PermissionsUtils.getEnableSalesOrderPromotions ( this , userCode , companyCode );
		
		// Retrieve all the orders asynchronously
		populate ();
		
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
		} // End if
	}
	
	/**
	 * Retrieve all the orders asynchronously.
	 */
	protected void populate () {
		// Retrieve all the orders asynchronously
		populateList = new PopulateList ();
		populateList.execute ();
	}
	
	/*
	 * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause(), for your activity to start interacting with the user.
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public	 void onResume () {
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
	 * Call back method executed before determining if the sync result is a success or a failure.
	 *
	 * @see me.SyncWise.Android.Modules.Sync.SyncListener#onPreFinish(int)
	 */
	@Override
	public void onPreFinish ( int requestCode ) {
		// Do nothing.
	}

	/*
	 * Call back method executed if the get sync process is a success.
	 *
	 * @see me.SyncWise.Android.Modules.Sync.SyncListener#onGetSuccess(de.greenrobot.dao.AbstractDao, java.util.ArrayList, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onGetSuccess ( AbstractDao < ? , ? > dao , ArrayList < Object > entities , int requestCode ) {
		// Process the retrieved data asynchronously
		Object object = entities;
		new SyncWarehouseQuantities ( (List < WarehouseQuantities >) object ).execute ();
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
		// Do nothing.
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
    	outState.putBoolean ( SAVE_MOVEMENTS , saveMovements );
    	new Thread ( new Runnable () {
			@Override
			public void run () {
				// Save the content of the orders using GSON
				ActivityInstance.saveDataGson ( SalesOrderDetailsActivity.this , SalesOrderDetailsActivity.class.getName () , ORDERS , orders );
			}
		} ).start ();
		// Indicate that there is saved orders data
		outState.putBoolean ( ORDERS , true );
	 	// Save the content of displayBasketPromotions in the outState bundle
    	outState.putBoolean ( DISPLAY_BASKET_PROMOTIONS , displayBasketPromotions );
    	// Save the content of requestCode in the outState bundle
    	outState.putInt ( REQUEST_CODE , requestCode );
    	// Save the content of selectedYear in the outState bundle
    	outState.putStringArrayList ( SELECTED_ORDERS_INDEX , selectedOrdersIndex );
    	// Save the content of selectedYear in the outState bundle
    	outState.putInt ( SELECTED_YEAR , selectedYear );
    	// Save the content of selectedMonth in the outState bundle
    	outState.putInt ( SELECTED_MONTH , selectedMonth );
    	// Save the content of selectedDay in the outState bundle
    	outState.putInt ( SELECTED_DAY , selectedDay );
    	// Save the content of note in the outState bundle
    	outState.putString ( NOTE , note );
    	// Save the content of transactionHeader in the outState bundle
    	outState.putSerializable ( TRANSACTION_HEADER , transactionHeader );
    	// Save the content of transactionDetails in the outState bundle
    	outState.putSerializable ( TRANSACTION_DETAILS , transactionDetails );
    	// Save the content of orderModification in the outState bundle
    	outState.putSerializable ( ORDER_MODIFICATION , orderModification );
    	// Save the content of displayOrderNote in the outState bundle
    	outState.putBoolean ( DISPLAY_ORDER_NOTE , displayOrderNote );
    	outState.putBoolean ( DISPLAY_ORDER_IMAGE , displayOrderImage );
        
    	outState.putBoolean(DISPLAY_ORDER_NOTE_NEW, displayOrderNoteNew);
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
		// Determine if the order note is undergoing any modifications
		if ( displayOrderNote ) {
			// Reset flag
			displayOrderNote = false;
			// Retrieve a reference to the tertiary view
			View secondary = findViewById ( R.id.layout_order_note );
			// Hide the secondary view
			secondary.startAnimation ( getViewAnimationOut ( secondary ) );
    		// Enable the main list
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( true );
    		// Refresh the action bar
    		invalidateOptionsMenu ();
    		// Exit method
    		return;
		} // End else if
		else if ( displayOrderImage ){
			// Reset the flag
			displayOrderImage = false;
			// Retrieve a reference to the cover flow
			CoverFlow coverFlow = (CoverFlow) findViewById ( R.id.coverflowReflect );
			// Check if the cover flow adapter is valid
			if ( coverFlow.getAdapter () != null ) {
				// Declare a PathImageAdapter object
				BaseAdapter mainAdapter = null;
				PathImageAdapter adapter = null;
				// Determine if the adapter is an instance of PathImageAdapter
				if ( coverFlow.getAdapter () instanceof PathImageAdapter ) {
					// Initialize the adapter
					adapter = (PathImageAdapter) coverFlow.getAdapter ();
					mainAdapter = adapter;
				} // End if
				// Determine if the adapter is an instance of ReflectingImageAdapter
				// AND if its linked adapter is an instance of PathImageAdapter
				else if ( coverFlow.getAdapter () instanceof ReflectingImageAdapter
						&& ( (ReflectingImageAdapter) coverFlow.getAdapter () ).getLinkedAdapter () instanceof PathImageAdapter ) {
					// Initialize the adapter
					mainAdapter = (BaseAdapter) coverFlow.getAdapter ();
					adapter = (PathImageAdapter) ( (ReflectingImageAdapter) mainAdapter ).getLinkedAdapter ();
				} // End else if
				// Check if the adapter is valid
				if ( adapter != null ) {
			        // Clear the cover flow adapter
					adapter.clear ();
					mainAdapter.notifyDataSetChanged ();
				} // End if
			} // End if
			// Hide the cover flow view
			coverFlow.setVisibility ( View.GONE );
			// Exit method
			return;
			}
		else if ( displayBasketPromotions ) {
			// Reset flag
			displayBasketPromotions = false;
			// Retrieve a reference to the quaternary view
			View quaternary = findViewById ( R.id.layout_basket_promotions );
			// Hide the quaternary view
			quaternary.startAnimation ( getViewAnimationOut ( quaternary ) );
    		// Enable the main list
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( true );
    		// Refresh the action bar
    		invalidateOptionsMenu ();
    		// Exit method
    		return;
		} // End else if
		else if ( displayOrderNoteNew ) {
				// Reset flag
			    displayOrderNoteNew = false;
				// Retrieve a reference to the tertiary view
				View secondary = findViewById ( R.id.layout_order_note );
				// Hide the secondary view
				secondary.startAnimation ( getViewAnimationOut ( secondary ) );
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
				setListAdapter ( getSalesOrderDetailsAdapter ( R.layout.sales_order_details_activity_item , new ArrayList < Order > () , true , true,1 ) );
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
								SalesOrderDetailsActivity.this.finish ();
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
	 * Call this when your activity is done and should be closed.
	 *
	 * @see android.app.Activity#finish()
	 */
	@Override
	public void finish () {
		// Clear adapter
		setListAdapter ( null );
		// Superclass method call
		super.finish ();
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
			quickAction = null;
			visit = null;
			divisions = null;
			searchQuery = null;
			searchView = null;
			orders = null;
			orderModification = null;
			basketPromotions = null;
			selectedOrders = null;
			freeOrders=null;
			selectedOrdersIndex = null;
			selectedDivisionsCodes = null;
			printingTransactionCode = null;
			warehouseQuantities = null;
			note = null;
			noteNew = null;
			details = null;
			transactionHeader = null;
			transactionDetails = null;
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
							Intent intent = new Intent ( SalesOrderDetailsActivity.this, PrintingActivity.class );
							intent.putExtra ( PrintingActivity.TYPE , PrintingActivity.Type.ORDER );
							intent.putExtra ( PrintingActivity.PRINTOUT , PrintingActivity.Printout.ORIGINAL );
							intent.putExtra ( PrintingActivity.HEADER , new ArrayList < TransactionHeaders > ( DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getTransactionHeadersDao ().queryBuilder ()
									.where ( TransactionHeadersDao.Properties.TransactionCode.eq ( transactionCode ) ).list () ) );
							startActivityForResult ( intent , REQUEST_CODE_PRINT );
							// Reset flag
							displayPrintingConfirmation = false;
							break;
						case DialogInterface.BUTTON_NEGATIVE:
					    	// Call this to set the result that your activity will return to its caller
							intent = new Intent();
							intent.putExtra ( SalesOrderActivity.SAVE_SUCCESS , ! error );
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
     * This method will be called when an item in the list is selected.
     *
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
	@Override
    protected void onListItemClick ( ListView listView , View view , final int position , long id ) {
		// Check if there is any ongoing note modifications
		if ( displayOrderNote )
			// Do nothing
			return;
		if(displayOrderNoteNew)
			//Do nothing
			return;
		// Retrieve the selected object
		Object object = ( (BaseAdapter) getListAdapter () ).getItem ( position );
		// Check if the selected object is an order
		if ( object instanceof Order )
			// Retrieve the selected order
			orderModification = (Order) object;
		else
			// Invalid object
			// Exit method
			return;
		
		// Determine if the request code is info
    	// AND if the view only flag is provided
    	if ( requestCode == REQUEST_CODE_INFO && ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) ){
    		// Display the quick action widget
    		if ( ( (ViewHolder) view.getTag () ).invoiceStates ==1)
    		quickAction.show ( view , orderModification , getResources () );
    		}
    	// Otherwise determine if the request code is not info
    	// AND if the view only flag is provided
    	if ( requestCode != REQUEST_CODE_INFO && ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) ){
    		// Check if the order belongs to the must stock list and is not confirmed
    		if ( ( (Order) object ).isMustStockList () && ! ( (Order) object ).isConfirmed () ) {
    			// Confirm the current order
    			( ( Order ) object ).setConfirmed ( true );
    			 
    		// Refresh the list
    			( ( SalesOrderDetailsAdapter ) getListAdapter () ) . notifyDataSetChanged ();
    		} // End if
//    	if( ( ( Order ) object ).getQuantityFreeCase () > 1 ){//order.getMaxQuantityFreeCase()
//				( ( Order ) object ).setMax( true );
//				( ( SalesOrderDetailsAdapter ) getListAdapter () ) . notifyDataSetChanged ();
//			  }
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
		Order selectedOrder = (Order) object;
		ViewHolder viewHolder = (ViewHolder) anchor.getTag ();
		// Determine the clicked action item
		switch ( actionId ) {
		case ActionItemID.REMOVE:
			if (  viewHolder.invoiceStates ==1  ) {
			// Clear quantities
			selectedOrder.setQuantityBig( 0 );
			selectedOrder.setQuantityMedium ( 0 );
			selectedOrder.setQuantitySmall ( 0 );
			selectedOrder.setQuantityFree ( 0 );
			// Remove item from the selected orders (if valid)
			if ( selectedOrders != null )
				selectedOrders.remove ( selectedOrder );
			// Check if the selected orders item codes list contains the appropriate item code
			if ( selectedOrdersIndex.contains ( selectedOrder.getItem ().getItemCode () )  )
				// Remove the index
				selectedOrdersIndex.remove ( selectedOrder.getItem ().getItemCode () );
			// Refresh sales order details
			refreshDetails ();
			// Check if the request code is INFO
			// AND if the selected orders list is empty
			if ( requestCode == REQUEST_CODE_INFO && selectedOrders != null && selectedOrders.isEmpty () )
				// Emulate a back key press
				onBackPressed ();
			else
				// Refresh the list
				( (MultipleListAdapter) getListAdapter () ).notifyDataSetChanged ();
			break;
			}
		} // End of switch
	}
	/*
	 * Callback method to be invoked when an item in this view has been clicked and held.
	 *
	 * @see android.widget.AdapterView.OnItemLongClickListener#onItemLongClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public boolean onItemLongClick ( AdapterView < ? > parent , View view , final int position , long id ) {
		// Retrieve the previous quantity
		int oldQuantity = basketPromotions.get ( position ).getQuantity ();
		// Declare and initialize an alert dialog builder
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder ( this );
		// By default, the alert dialog cannot be cancelled UNLESS it has no buttons
		alertDialogBuilder.setCancelable ( false );
		// Create view
		final EditText editText = new EditText ( this );
		editText.setId ( 1 );
		editText.setInputType ( InputType.TYPE_CLASS_NUMBER );
		editText.setText ( oldQuantity <= 0 ? "" : String.valueOf ( oldQuantity ) );
		// Create click listener
		DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener () {
			@Override
			public void onClick ( DialogInterface dialog , int which ) {
				// Determine the clicked button
				switch ( which ) {
				case DialogInterface.BUTTON_POSITIVE:
					int newQuantity = 0;
					try {
						newQuantity = Integer.parseInt ( ( (EditText) ( (AlertDialog) dialog ).findViewById ( 1 ) ).getText ().toString ().trim () );
					} catch ( Exception exception ) {
						newQuantity = 0;
					}
					basketPromotions.get ( position ).setQuantity ( newQuantity );
					initializeQuaternaryView ();
				case DialogInterface.BUTTON_NEGATIVE:
		    		// Hide the software keyboard
		            ( (InputMethodManager) getBaseContext ().getSystemService ( INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow ( editText.getWindowToken (), 0 );
					// Dismiss dialog
					AppDialog.getInstance ().dismiss ();
					break;
				} // End switch
			}
		};
		// Setup dialog
		alertDialogBuilder.setTitle ( R.string.quantity_label );
		alertDialogBuilder.setView ( editText );
		alertDialogBuilder.setPositiveButton ( AppResources.getInstance ( this ).getString ( this , R.string.ok_label ) , onClickListener );
		alertDialogBuilder.setNegativeButton ( AppResources.getInstance ( this ).getString ( this , R.string.cancel_label ) , onClickListener );
		alertDialogBuilder.create ();
		alertDialogBuilder.show ();
		// Consume event
		return true;
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
	 * @param restore	Boolean used to determine if the tertiary view is being initialized for the first time or is being restored.
	 */
	protected void initializeSecondaryView ( final boolean restore ) {
		// Retrieve a reference to the parent view
		View parent = findViewById ( R.id.layout_order_note );
		// Retrieve a reference to the sales order note edit text
		EditText noteEditText = (EditText) parent.findViewById ( R.id.edittext_sales_order_note );		
		// Retrieve a reference to the sales order note title label
		TextView titleLabel = (TextView) parent.findViewById ( R.id.label_sales_order_note );
		
		// Display the sales order note title label
		titleLabel.setText ( AppResources.getInstance ( this ).getString ( this , R.string.sales_order_note_title ) );
		// Enable the save icon
		parent.findViewById ( R.id.icon_save_note ).setEnabled ( true );
		// Enable edit text
		noteEditText.setEnabled ( true );
		// Display the field hint
		noteEditText.setHint ( AppResources.getInstance ( this ).getString ( this , R.string.sales_order_note_hint ) );
		// Set the maximum number of allowed characters
		noteEditText.setFilters ( new InputFilter [] { new InputFilter.LengthFilter ( TransactionHeadersUtils.getFreeRemarksMaxLength () ) } );
		// Check if the view is being restored
		if ( ! restore )
			// Set the sales order note
			noteEditText.setText ( note );
	}
	//ahmad
	/**
	 * Initializes the sales order note (secondary) view.
	 * 
	 * @param restore	Boolean used to determine if the tertiary view is being initialized for the first time or is being restored.
	 */
	protected void initializeSecondaryViewNew ( final boolean restore ) {
		// Retrieve a reference to the parent view
		View parent = findViewById ( R.id.layout_order_note );
		// Retrieve a reference to the sales order note edit text
		EditText noteEditText = (EditText) parent.findViewById ( R.id.edittext_sales_order_note );		
		// Retrieve a reference to the sales order note title label
		TextView titleLabel = (TextView) parent.findViewById ( R.id.label_sales_order_note );
		
		// Display the sales order note title label
		titleLabel.setText ( AppResources.getInstance ( this ).getString ( this , R.string.sales_order_note_title ) );
		// Enable the save icon
		parent.findViewById ( R.id.icon_save_note ).setEnabled ( true );
		// Enable edit text
		noteEditText.setEnabled ( true );
		// Display the field hint
		noteEditText.setHint ( AppResources.getInstance ( this ).getString ( this , R.string.sales_order_note_hint ) );
		// Set the maximum number of allowed characters
		noteEditText.setFilters ( new InputFilter [] { new InputFilter.LengthFilter ( TransactionHeadersUtils.getFreeRemarksMaxLength () ) } );
		// Check if the view is being restored
		if ( ! restore )
			// Set the sales order note
			noteEditText.setText ( noteNew );
	}
	/**
	 * Initializes the basket promotions (quaternary) view.
	 */
	protected void initializeQuaternaryView () {
		// Retrieve a reference to the parent view
		View parent = findViewById ( R.id.layout_basket_promotions );
		// Retrieve a reference to the list view
		ExpandableListView basketPromotionsList = (ExpandableListView) parent.findViewById ( R.id.basket_promotions_list );
		// Check if the list has an adapter
		if ( basketPromotionsList.getAdapter () == null )
			// Set a new adapter
			basketPromotionsList.setAdapter ( new BasketPromotionsAdapter ( this , R.layout.basket_item , R.layout.item_item , basketPromotions ) );
		else
			// Otherwise simply refresh the adapter
			( (BasketPromotionsAdapter) basketPromotionsList.getExpandableListAdapter () ).notifyDataSetChanged ();
	}
	/**
	 * Called when a view has been clicked.<br>
	 * The order modifications are saved.
	 * 
	 * @param view	The clicked view.
	 */
	public void updateOrder ( View view ) {
	 
		// Update the order / UI
		onOrderModificationResult ();
	}
	
	/**
	 * Called when a view has been clicked.<br>
	 * The order note is saved.
	 * 
	 * @param view	The clicked view.
	 */
	public void updateNote ( View view ) {
		// Determine if the order note is undergoing any modifications
		if ( ! displayOrderNote && ! displayOrderNoteNew )
			// No modifications
			return;
		if(displayOrderNote){
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
        ( (InputMethodManager) getBaseContext ().getSystemService ( INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow ( secondary.getWindowToken (), 0 );
        
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
		
		
		//ahmad
	
				if(displayOrderNoteNew){
				// Reset flag
					displayOrderNoteNew = false;
				
				// Retrieve a reference to the secondary view
				View secondary = findViewById ( R.id.layout_order_note );
				// Retrieve a reference to the sales order note edit text
				EditText noteEditText = (EditText) secondary.findViewById ( R.id.edittext_sales_order_note );
				// Store the sales order note
				noteNew = noteEditText.getText ().toString ().trim ();
				// Disable the edit text
				noteEditText.setEnabled ( false );
				
				// Disable the save icon
				secondary.findViewById ( R.id.icon_save_note ).setEnabled ( false );
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
				
				// Update the sales order details list with the new note
				onNoteModificationResultNew ();
				}
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
			intent.putExtra ( SalesOrderActivity.SAVE_SUCCESS , ! error );
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
    	if ( scanResult != null && searchView != null ) {
			// Set the new query
			searchView.setQuery ( scanResult.getContents () == null ? "" : scanResult.getContents () , false );
			// Expand the SearchView
			searchView.setIconified ( false );
    	} // End if
	}
	
	/**
	 * Called after a successful order modification : price or quantity modification.<br>
	 * The corresponding object is modified in the main list and the appropriate action is performed (based on the current request code).
	 */
	private void onOrderModificationResult () {
		// Retrieve a reference to the sales order object from the main list
		Order order = null;
		// Iterate over all the orders
		for ( int i = 0 ; i < orders.size () ; i ++ )
			// Match the order
			if ( orders.get ( i ).getItem ().getItemCode ().equals ( orderModification.getItem ().getItemCode () ) ) {
					// Update the selected order position
					order = orders.get ( i );
				// Exit loop
				break;
			} // End if
		// Check if the order object is valid
		if ( order == null )
			// Invalid object
			return;
		// Check if the item has zero quantities
		if (       order.getQuantityBig () == 0
				&& order.getQuantityMedium () == 0
				&& order.getQuantitySmall () == 0 
				&& order.getQuantityFree() == 0 ) {
			// Check if the selected orders list is valid
			if ( selectedOrders != null )
				// Remove item from the selected orders
				selectedOrders.remove ( order );
			// Check if the selected orders item codes list contains the appropriate item code
			if ( selectedOrdersIndex.contains ( order.getItem ().getItemCode () )  )
				// Remove the index
				selectedOrdersIndex.remove ( order.getItem ().getItemCode () );
		} // End if
		else {
			// Otherwise the item has non-zero quantities
			// Check if the selected orders item codes list does NOT contain the appropriate item code
			if ( ! selectedOrdersIndex.contains ( order.getItem ().getItemCode () )  )
				// Add the index
				selectedOrdersIndex.add ( order.getItem ().getItemCode () );
		} // end else

		// Refresh sales order details
		refreshDetails ();
		// Check if the request code is INFO
		// AND if the selected orders list is empty
		if ( SalesOrderDetailsActivity.this.requestCode == REQUEST_CODE_INFO && selectedOrders != null && selectedOrders.isEmpty () )
			// Emulate a back key press
			onBackPressed ();
	}
	 
	/**
	 * Called after a successful note modification.<br>
	 * The note in the sales order details list is updated.
	 */
	private void onNoteModificationResult () {
		// Iterate over all details
		for ( OrderInfo detail : details )
			// Check if the current detail is about the sales order note
			if ( detail.getId () == OrderInfo.ID.NOTE ) {
				// Update the sales order note
				detail.setValue ( note );
				// Exit loop
				break;
			} // End if
		// Refresh the list
		( (MultipleListAdapter) getListAdapter () ).notifyDataSetChanged ();
	}
	/**
	 * Called after a successful note modification.<br>
	 * The note in the sales order details list is updated.
	 */
	private void onNoteModificationResultNew () {
		// Iterate over all details
		for ( OrderInfo detail : details )
			// Check if the current detail is about the sales order note
			if ( detail.getId () == OrderInfo.ID.NOTENEW ) {
				// Update the sales order note
				detail.setValue ( noteNew );
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
    	if ( orders == null || displayOrderNote || displayOrderNoteNew|| displayBasketPromotions )
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
	       	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_mark_all ) );
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
            	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_edit ) );
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
    	else if ( menuItem.getItemId () == R.id.action_mark_all ) {
    		// Set flag
    		displayBasketPromotions = true;
    		// Initialize the quaternary view
    		initializeQuaternaryView ();
    		// Disable the main list
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( false );
    		// Retrieve a reference to the quaternary view
    		View quaternaryView = findViewById ( R.id.layout_basket_promotions );
    		// Display the tertiary view
    		quaternaryView.setVisibility ( View.VISIBLE );
    		// Animate the tertiary view
    		quaternaryView.startAnimation ( getViewAnimationIn() );
    		// Refresh the action bar
    		invalidateOptionsMenu ();
    		// Consume event
    		return true;
    	} // End else if
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
								Intent intent = new Intent ( SalesOrderDetailsActivity.this, PrintingActivity.class );
								intent.putExtra ( PrintingActivity.TYPE , PrintingActivity.Type.ORDER );
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
            boolean basketQuantities = false;
            for ( BasketPromotion basketPromotion : basketPromotions )
            	if ( basketPromotion.getQuantity () > 0 ) {
            		basketQuantities = true;
            		break;
            	} // End if
    		// Declare and initialize a list used to host the valid sales orders
    		ArrayList < Order > salesOrder = new ArrayList < Order > ();
    		salesOrders = new ArrayList < Order > ();
    		salesOrderMSL = new ArrayList < Order > ();
    	    salesOrderNEWSKU = new ArrayList < Order > ();
            
    		
    		// Make sure the entire client must stock list is confirmed
            boolean isStockListConfirmed = true;
        	boolean isMSLConfirmationRequired = PermissionsUtils.getEnforceMSLConfirmation ( SalesOrderDetailsActivity.this , DatabaseUtils.getCurrentUserCode(SalesOrderDetailsActivity.this ) , DatabaseUtils.getCurrentCompanyCode(SalesOrderDetailsActivity.this )  );
        	HashMap < String , Order > mappedInvoices = new HashMap < String , Order > ();
       	 boolean bb=false;
         for ( Order order : orders ) {
          	//invoice.setBasketSmall(0);
  			  
  			 if( order.isMustStockList() &&order.isForcedMustStockList()   &&( order.getReasonCode()==null   &&  order.getQuantityBig () == 0 && order.getQuantityMedium () == 0 && order.getQuantitySmall () == 0 && order.getQuantityFree () == 0 && order.getQuantityFreeCase()==0 )){
          		bb=true;
          		break;
          	}
  		 }
         if(bb){
         	Baguette.showText ( SalesOrderDetailsActivity.this ,
 					" please add reason or quatity to item in must stock list",
 						Baguette.BackgroundColor.RED );
         	return false;
         }
         bb=false;
         for ( Order order : orders ) {
          	//invoice.setBasketSmall(0);
  			  
  			 if( order.isForcedNewItemList() &&order.isNewSKUList()   &&( order.getReasonCode()==null   &&  order.getQuantityBig () == 0 && order.getQuantityMedium () == 0 && order.getQuantitySmall () == 0 && order.getQuantityFree () == 0 && order.getQuantityFreeCase()==0 )){
          		bb=true;
          		break;
          	}
  		 }
         if(bb){
         	Baguette.showText ( SalesOrderDetailsActivity.this ,
 					" please add reason or quatity to item in New Item list",
 						Baguette.BackgroundColor.RED );
         	return false;
         }
         // Iterate over all the invoices
// 		
 		
 			 // Iterate over all the orders
            for ( Order order : orders ) {
            	// Check if the current item belongs to the must stock list and is not confirmed
           	 order.clearInstantPromotions ();
            	if( isMSLConfirmationRequired)
            	if ( order.isMustStockList () && ! order.isConfirmed () ) {
            		// Reset flag
            		isStockListConfirmed = false;
            		// Exit loop
            		break;
            	} // End if
            	
    			// Check if the current order has valid quantities
    			if ( order.getQuantityBig () != 0 || order.getQuantityMedium () != 0 || order.getQuantitySmall () != 0 || order.getQuantityFree () != 0 || order.getQuantityFreeCase()!=0 || order.getAvailability() != null||  (order.isMustStockList() && order.getReasonCode()!=null) || (order.isNewSKUList() && order.getReasonCode()!=null))
    			{
    	 
    	        	//invoice.setBasketSmall(0);
    	        	
    	        	// Map the current item
    			
    				// Clear previous instant promotions
    				order.clearInstantPromotions ();
    			//	order.restoreDetailDiscountPercentage ();
    				
    				// The order contains at least one quantity, add it to the sales order list
    				salesOrder.add ( order );
    				if ( order.getQuantityBig () != 0 || order.getQuantityMedium () != 0 || order.getQuantitySmall () != 0 || order.getQuantityFree () != 0 || order.getQuantityFreeCase()!=0 || order.getAvailability() != null)
        			salesOrders.add(order);
    				if(  order.isMustStockList()  && order.getQuantityBig () == 0 && order.getQuantityMedium () == 0 && order.getQuantitySmall () == 0 && order.getQuantityFree () == 0 && order.getQuantityFreeCase()==0 )
    					salesOrderMSL.add(order);
    				if( order.isNewSKUList() && order.getQuantityBig () == 0 && order.getQuantityMedium () == 0 && order.getQuantitySmall () == 0 && order.getQuantityFree () == 0 && order.getQuantityFreeCase()==0  )
    					salesOrderNEWSKU.add(order);
    				}
    			mappedInvoices.put ( order.getItem ().getItemCode () , order );
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
//         
            
     
//            for ( Order order : orders ) {
//        		 
//        		if(order.getQuantityFreeCase() > order.getMaxQuantityFreeCase())
//        		{	
//        			Baguette.showText ( this ,
//        						AppResources.getInstance ( this ).getString ( this , R.string.sales_order_free_unit_case_message ) ,
//        						Baguette.BackgroundColor.RED );
//        	    		// Consume event
//        	    		return true;
//        			} // End if
//        		if( order.getMaxQuantityFreeUnit () != -1 )
//        			if( order.getQuantityFree() > order.getMaxQuantityFreeUnit() )
//            		{	
//            			Baguette.showText ( this ,
//            						AppResources.getInstance ( this ).getString ( this , R.string.sales_order_free_unit_case_message ) ,
//            						Baguette.BackgroundColor.RED );
//            	    		// Consume event
//            	    		return true;
//            			} // End if
//            }
    		// Check if there is at least one valid sales order
            promotedOrders=new ArrayList<Order>();
            if ( basketQuantities )
                for ( BasketPromotion basketPromotion : basketPromotions )
    	        	// Check if the basket is ordered
    	        	if ( basketPromotion.getQuantity () > 0 ){
    	        		// Iterate over the promotion details
    	        		for ( PromotionDetails promotionDetail : basketPromotion.getPromotionDetails () ) {
    	        			DecimalFormat moneyFormat = new DecimalFormat ("###0.0000");
    	        			// Retrieve the appropriate invoice
    	        			Order _order = mappedInvoices.get ( promotionDetail.getOrderedItemCode () );
    	        		    Order order = new Order(_order.getItem(), _order.getUnit() );
    	        			order.setQuantityBig( _order.getQuantityBig());
    	        			order.setQuantityMedium( _order.getQuantityMedium());
    	        			order.setQuantitySmall( _order.getQuantitySmall());
    	        			order.setPriceBig(_order.getPriceBig());
    	        		 	order.setDetailDiscountPercentage( _order.getDetailDiscountPercentage() );
    	        			order.setBarCodes ( _order.getBarCodes () );
    	        			order.setPromotionDiscountPercentageBasket ( promotionDetail.getDiscountPercentage () );
    	        			order.setPromotionID(promotionDetail.getPromotionID()+"");
    	        			order.setDivisionCodes ( _order.getDivisionCodes () );
    	        			order.setPriceBig(Double.valueOf(moneyFormat.format(_order.getPriceBig()-(_order.getPriceBig()*order.getPromotionDiscountPercentageBasket()/100))));
    	        			order.setPriceMedium(Double.valueOf(moneyFormat.format(_order.getPriceMedium()-(_order.getPriceMedium()*order.getPromotionDiscountPercentageBasket()/100))));
    	        			order.setPriceSmall(Double.valueOf(moneyFormat.format(_order.getPriceSmall()-(_order.getPriceSmall()*order.getPromotionDiscountPercentageBasket()/100))));
    	        			order.setBasketPriceBig(_order.getPriceBig());
    	        			order.setBasketPriceMedium(_order.getPriceMedium());
    	        			order.setBasketPriceSmall(_order.getPriceSmall());
//    	        			order.setPriceBig(_order.getBasketPriceBig()  );
//    	        			order.setPriceMedium(_order.getBasketPriceMedium() );
//    	        			order.setPriceSmall( _order.getBasketPriceSmall());
    	    			//	order.setHasPromotion(_order.getHasPromotion());
    	    				order.setTax ( _order.getTax () );
    	    				order.setCaseTaxAmount(_order.getCaseTaxAmount());
    	    				order.setUnitTaxAmount(_order.getUnitTaxAmount());
    				   // 	order.setQuantityPromotedBasket ( ( (int) ( basketPromotion.getQuantity () * promotionDetail.getOrderedQuantity () ) ) );
    					//	order.setPromoID ( String.valueOf( promotionDetail.getPromotionID() ) );
    					//	order.setBasketSmall ( basketPromotion.getQuantity ());
    					//	order.setBundelCode(basketPromotion.getPromotionHeader().getDivisionCode());
    	    				order.setQuantityBasket ( (int) ( basketPromotion.getQuantity ()  ) );
    	    				order.setAppFree(_order.getAppFree());
//    	        			if(promotionDetail.getOfferedUnit().equals("U"))
//    	        			// Set the quantity
//    	        			{
//    	        				order.setPriceSmall(promotionDetail.getTotalAmount());
    	        				order.setQuantityPromotedBasket ( (int) ( basketPromotion.getQuantity () * promotionDetail.getOrderedQuantity () ) );
//    	        			}else{
//    	        				order.setPriceMedium(promotionDetail.getTotalAmount());
//    	        				order.setQuantityPromotedBasketCase((int) ( basketPromotion.getQuantity () * promotionDetail.getOrderedQuantity () ));
//    	        			}//	        			order.setPromoID ( String.valueOf( promotionDetail.getPromotionID() ) );
//    	        			order.setBasketSmall ( basketPromotion.getQuantity ());
    	        			// Check if the original discount should be applied
    	        			order.refreshInvoiceState();
    		        					//order.getDiscountPercentage () );
    	        		
    	        			// Check if the invoice is previously added to the list
    	        			if ( ! promotedOrders.contains ( order ) ) {	
//    	        				Order o = new Order(order.getItem(), order.getUnit());
//    	        			
//    	        			o.setQuantityMedium(0);
//    	        			o.setPriceSmall(order.getPriceSmall());
    	        		//	o.set
    	        				// Add the invoice to the list
    	        				promotedOrders.add ( order );
    	        			
    	        			}
    	        			} // End for each
    	        	}
                
    		// Check if there is at least one valid sales order
    		if ( salesOrder.isEmpty () && ! basketQuantities ) {
				// Indicate that some clients were removed
				Baguette.showText ( SalesOrderDetailsActivity.this ,
						AppResources.getInstance ( SalesOrderDetailsActivity.this ).getString ( SalesOrderDetailsActivity.this , R.string.no_sales_order_message ) ,
						Baguette.BackgroundColor.RED );
        		// Consume event
        		return true;
    		} // End if
    		// Otherwise there is at least one sales order
    		// Sort the orders based on their indexes
    		if(PermissionsUtils.getDisplaySequenceChange ( SalesOrderDetailsActivity.this , DatabaseUtils.getCurrentUserCode(SalesOrderDetailsActivity.this ) , DatabaseUtils.getCurrentCompanyCode(SalesOrderDetailsActivity.this )  )){
			
				Collections.sort ( salesOrder , new Comparator < Order > () {
				@Override
				public int compare ( Order order1 , Order order2 ) {
					 
						int sequence1 = order1.getSequence()  ;
						int sequence2 = order2.getSequence();
						int order = sequence1 - sequence2;
					 
						return order;
					    
				
				
				}
			} );
    		}else{
    		Collections.sort ( salesOrder , new Comparator < Order > () {
				@Override
				public int compare ( Order order1 , Order order2 ) {
					// Sort the orders based on their indexes
					return selectedOrdersIndex.indexOf ( order1.getItem ().getItemCode () ) - selectedOrdersIndex.indexOf ( order2.getItem ().getItemCode () );
				}
			} );
    		}
    		// Set the new request code
    		requestCode = REQUEST_CODE_INFO;
    		// Refresh the action bar
    		invalidateOptionsMenu ();
    		// Set the selected orders list
    		selectedOrders = salesOrder;
    		
    		refreshInstantPromotions(mappedInvoices);
			// Initialize the sales order details
			initializeDetails ();

			// Declare and initialize a multiple list adapter
			MultipleListAdapter adapter = new MultipleListAdapter ( this );
			// Add the sales order details adapter
			adapter.addSection ( new Section ( AppResources.getInstance ( this ).getString ( this , R.string.sales_order_details_label ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
					new OrderInfoAdapter ( this , R.layout.order_info_item , details ) );
			// Add the sales order adapter
	 	if( selectedOrders!=null && !selectedOrders.isEmpty())
			adapter.addSection ( new Section ( AppResources.getInstance ( this ).getString ( this , R.string.sales_order_products_label ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
					getSalesOrderDetailsAdapter ( R.layout.sales_order_details_activity_item , salesOrders , ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) , false,1 ) );
		if(salesOrderMSL!=null && !salesOrderMSL.isEmpty())
			adapter.addSection ( new Section ("MSL PRODUCTS REASONS" , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
					getSalesOrderDetailsAdapter ( R.layout.sales_order_details_activity_item , salesOrderMSL , ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) , false,1 ) );
		if(salesOrderNEWSKU!=null && !salesOrderNEWSKU.isEmpty())
			adapter.addSection ( new Section ( "New SKU LIST PRODUCTS REASONS" , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
					getSalesOrderDetailsAdapter ( R.layout.sales_order_details_activity_item , salesOrderNEWSKU , ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) , false,1 ) );
		
			if ( ! freeOrders.isEmpty () )
				// Add the sales invoice adapter
				adapter.addSection ( new Section ( AppResources.getInstance ( this ).getString ( this , R.string.sales_order_free_label ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 255 ) , null ) ,
						getSalesOrderDetailsAdapter ( R.layout.sales_order_details_activity_item , freeOrders , ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) , false,2 ) );
			 if ( ! promotedOrders.isEmpty () )
					// Add the sales invoice adapter
					adapter.addSection ( new Section ( AppResources.getInstance ( this ).getString ( this , R.string.sales_order_promoted_label ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 255 , 0 ) , null ) ,
							getSalesOrderDetailsAdapter ( R.layout.sales_order_details_activity_item , promotedOrders , ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) , false ,3) );

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
    		// Retrieve a reference to the tertiary view
    		View tertiaryView = findViewById ( R.id.layout_order_note );
    		// Display the tertiary view
    		tertiaryView.setVisibility ( View.VISIBLE );
    		// Animate the tertiary view
    		tertiaryView.startAnimation ( getViewAnimationIn() );
    		// Refresh the action bar
    		invalidateOptionsMenu ();
    		// Consume event
    		return true;
    	} // End else if
    	// Determine if the action keyboard is selected
    	else if ( menuItem.getItemId () == R.id.action_edit ) {
    		// Set flag
    		displayOrderNoteNew = true;
    		// Initialize the secondary view
    		initializeSecondaryViewNew ( false );
    		// Disable the main list
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( false );
    		// Retrieve a reference to the tertiary view
    		View tertiaryView = findViewById ( R.id.layout_order_note );
    		// Display the tertiary view
    		tertiaryView.setVisibility ( View.VISIBLE );
    		// Animate the tertiary view
    		tertiaryView.startAnimation ( getViewAnimationIn() );
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
							for ( OrderInfo detail : details )
								// Check if the current detail is about the delivery date
								if ( detail.getId () == OrderInfo.ID.DELIVERY_DATE ) {
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
					    			DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getTransactionHeadersDao ().update ( transactionHeader );
									// Update target coverage (if any)
			    					TargetUpdate.updateCoverage ( SalesOrderDetailsActivity.this , TargetHeadersUtils.Type.TOTAL_SALES , null , -1 , null , -1 , null , null , UpdateType.DECREASE , transactionHeader.getTotalTaxAmount () , transactionHeader.getCurrencyCode () );
									// Update target coverage (if any)
			    					TargetUpdate.updateCoverage ( SalesOrderDetailsActivity.this , TargetHeadersUtils.Type.TOTAL_SALES_PER_CLIENT , transactionHeader.getClientCode () , TargetHeadersUtils.SubType.CLIENT , null , -1 , transactionHeader.getCompanyCode () , transactionHeader.getDivisionCode () , UpdateType.DECREASE , transactionHeader.getTotalTaxAmount () , transactionHeader.getCurrencyCode () );
									// Update target coverage (if any)
			    					TargetUpdate.updateCoverage ( SalesOrderDetailsActivity.this , TargetHeadersUtils.Type.TOTAL_SALES_PER_CLIENT_PER_BRAND , transactionHeader.getClientCode () , TargetHeadersUtils.SubType.CLIENT , transactionHeader.getDivisionCode () , TargetHeadersUtils.SubType.DIVISION , transactionHeader.getCompanyCode () , transactionHeader.getDivisionCode () , UpdateType.DECREASE , transactionHeader.getTotalTaxAmount () , transactionHeader.getCurrencyCode () );
					    			// Check if the transaction details list is valid
					    			if ( transactionDetails != null )
					    				// Iterate over all transaction details
					    				for ( TransactionDetails transactionDetail : transactionDetails )
											// Update target coverage (if any)
					    					TargetUpdate.updateCoverage ( SalesOrderDetailsActivity.this , TargetHeadersUtils.Type.TOTAL_SALES_PER_CLIENT_PER_BRAND , transactionHeader.getClientCode () , TargetHeadersUtils.SubType.CLIENT , transactionDetail.getItemCode () , TargetHeadersUtils.SubType.ITEM , transactionHeader.getCompanyCode () , transactionHeader.getDivisionCode () , UpdateType.DECREASE , transactionDetail.getTotalLineAmount () , transactionHeader.getCurrencyCode () );
					            	// Call this to set the result that your activity will return to its caller
					            	setResult ( RESULT_OK , new Intent ().putExtra ( SalesOrderActivity.DELETE_SUCCESS , true ) );
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
    	} // End else if
    	// Allow normal menu processing to proceed
    	return false;
    }
    
    /**
     * Saves the current transaction.
     */
    @SuppressLint("UseSparseArrays") 
    private void saveTransaction () {
    	// Otherwise the sales order can be saved
		// Flag used to indicate whether an error occurred or not
		error = false;
		// Flag used to indicate if the activity can be finished or not
		// This flag is mainly used to display a dialog before the activity finishes
		boolean finish = true;
    	// Compute the sales order details
    	double calValue = 0;
    	double grossAmount = 0;
    	double taxes = 0;
    	double totalValue = 0;
    	
    	Divisions div=DatabaseUtils.getInstance( this).getDaoSession().getDivisionsDao().queryBuilder ().where
		    	(  DivisionsDao.Properties.DivisionCode.eq( DatabaseUtils.getCurrentDivisionCode( this) ),
		    	   DivisionsDao.Properties.CompanyCode.eq( DatabaseUtils.getCurrentCompanyCode( this))).unique();
    	boolean freetaxe=false;
    	if(div!=null && div.getApplyFreeExiceTax()!=null &&div.getApplyFreeExiceTax() ==1	)
    		freetaxe=true;
    	double taxesAmount = 0; 
    	
    	// Iterate over all the selected orders
    	for ( Order order : selectedOrders ) {
    		// Compute the current net value
    		grossAmount += order.getQuantityBig () * order.getPriceBig () + order.getQuantityMedium () * order.getPriceMedium () + order.getQuantitySmall () * order.getPriceSmall () + order.getQuantityFreeAdded () * order.getPriceSmall ();
    		// Compute the value after applying the discount percentage and amount
			double _calValue = ( order.getQuantityBig () * order.getPriceBig () + order.getQuantityMedium () * order.getPriceMedium ()
					+ order.getQuantitySmall () * order.getPriceSmall () ) * ( ( 100 - ( order.getDiscountPercentage () ) ) / 100 )
					- order.getDiscountAmount ();
			// Compute the current tax value
			double _taxes = 0;
			// Check if the item is taxable
			if ( order.getTax () != 0 )
				// Compute the current taxes
				_taxes = _calValue * order.getTax () / 100;
			double taxeAmount=order.getQuantityMedium()*order.getCaseTaxAmount()
					 
					+ order.getQuantitySmall (  )*order.getUnitTaxAmount()+
				(freetaxe?	order.getQuantityFreeAdded ()*order.getUnitTaxAmount()+
					order.getQuantityFreeCase()*order.getCaseTaxAmount()
					:0);
		
			taxesAmount +=taxeAmount;
			// Compute the current total value
			double _totalValue = _calValue + _taxes+taxeAmount;
			// Update all the values
			// Update all the values
			calValue += _calValue;
			taxes += _taxes;
			totalValue += _totalValue;
    	} // End for each
    	for ( Order order : promotedOrders ) {
			// Compute the current gross value for non free items
    		double _grossAmount = order.getBasketBig  () * order.getBasketPriceBig () + order.getBasketMedium () * order.getBasketPriceMedium () + order.getBasketSmall () * order.getBasketPriceSmall ();
    	    	// Compute the discount amount for non free items
    		double percentage = order.getPromotionDiscountPercentageBasket();
    		double _calValue = ( _grossAmount  ) * ( ( 100 - ( percentage ) ) / 100 )
					- order.getDiscountAmount ();
//    		double _discountAmount = _grossAmount * ( percentage ) / 100;
    		// Update values above for free items
    		 
			// Compute the current tax value
			double _taxes = 0;
			// Check if the item is taxable
			if ( order.getTax () != 0 )
				// Compute the current taxes
				_taxes = (_calValue ) * order.getTax () / 100;
			double taxeAmount=0;
			if(div!=null && div.getApplyExiceTax()!=null &&div.getApplyExiceTax()==1	&&  percentage !=100)
				
			  taxeAmount=order.getBasketMedium()*order.getCaseTaxAmount()
					 
					+ order.getBasketSmall (  )*order.getUnitTaxAmount() 
				 
					;
		
			taxesAmount +=taxeAmount;
			// Compute the current total value
			double _totalValue = _calValue + _taxes+taxeAmount;
			// Update all the values
			grossAmount += _grossAmount;
			calValue += _calValue;
//			taxes += _taxes;
			totalValue += _totalValue;
		} // End for each
    	double discountAmount=0;
		if ( freeOrders  != null && !freeOrders .isEmpty() ){
			// Iterate over all the free orders LBP
			for ( Order o : freeOrders ){
				// Get the price small
				double priceSmallFree = o.getPriceSmall();
				double priceMediumFree = o.getPriceMedium();
				// Compute the discount amount
				discountAmount  +=    o. getFreeSmall()   * priceSmallFree+o.getFreeMedium()*priceMediumFree ;
			
				grossAmount+=  o.getFreeSmall()   * priceSmallFree+o.getFreeMedium()*priceMediumFree ;
				double taxeAmount=(freetaxe?o.getFreeMedium()*o.getCaseTaxAmount()
						 
						+ o.getFreeSmall (  )*o.getUnitTaxAmount() 
					 :0) ;
				taxesAmount +=taxeAmount;
				totalValue += taxeAmount;
				}
			}
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
		// Retrieve the company code
		String companyCode = visit.getCompanyCode ();
		// Retrieve the division code
		String divisionCode = ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClientDivision ().getDivisionCode ();
		// Declare and initialize a string used to host the transaction code used
		String transactionHeaderCode = null;
		
		// Determine if there is a previously stored sales order
		if ( transactionHeader == null && transactionDetails == null  ) {
    		try {   saveMovements=false;
				// Retrieve the sales order sequence
			//	int salesOrderSequence = DatabaseUtils.getUserSequence ( this , user.getUserCode () , companyCode , TransactionSequencesDao.Properties.SalesOrder );
    			int   salesOrderSequence = DatabaseUtils.getSequence( this , user.getUserCode () , DatabaseUtils.getCurrentCompanyCode ( this ) ,	TransactionHeadersUtils.Type.SALES_ORDER );//DatabaseUtils.getUserSequence ( this , user.getUserCode () , DatabaseUtils.getCurrentCompanyCode ( this ) , TransactionSequencesDao.Properties.SalesOrder );
    			TransactionSequences transactionSequences=DatabaseUtils.getInstance(this).getDaoSession ().getTransactionSequencesDao().queryBuilder().where
						(TransactionSequencesDao.Properties.UserCode.eq( user.getUserCode ()),
								TransactionSequencesDao.Properties.CompanyCode.eq(companyCode)).unique();

    			// Begin transaction
				DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getDatabase ().beginTransaction ();
				
				// Check if the new order can be ordered without the credit exceeding the allowed limit
				if ( exceedsCreditLimit ( ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClient ().getClientCode () , companyCode , currency.getCurrencyCode () , totalValue ) ) {
					// Otherwise just inform the user that the client has exceeded his/her credit limit
					finish = false;
					AppDialog.getInstance ().displayAlert ( this ,
							null ,
							AppResources.getInstance ( this ).getString ( this , R.string.sales_order_client_credit_limit_exceeded_message ) ,
							AppDialog.ButtonsType.OK ,
							new DialogInterface.OnClickListener () {
								@Override
								public void onClick ( DialogInterface dialog , int which ) {
						        	// Finish this activity
					        		finish ();
								}
							} );
				} // End if
	    		// Compute the transaction header code
	    		transactionHeaderCode = String.valueOf ( TransactionHeadersUtils.Type.SALES_ORDER ) + String.valueOf ( user.getPrefixID () ) + sequence.format ( salesOrderSequence );
				// Update the sales order sequence
			//	DatabaseUtils.setUserSequence ( this , user.getUserCode () , companyCode , TransactionSequencesDao.Properties.SalesOrder , salesOrderSequence + 1 );
	    		transactionSequences.setSalesOrder (salesOrderSequence + 1);
	    	 	DatabaseUtils.getInstance( this).getDaoSession ().getTransactionSequencesDao().update(transactionSequences);
	    	 	saveMovements=false;
	    		// TODO : Details of a sales order transaction header
	    		// Declare and initialize a transaction header
	    		TransactionHeaders transactionHeader = new TransactionHeaders ( null , // ID
	    				transactionHeaderCode , // TransactionCode
	    				TransactionHeadersUtils.Type.SALES_ORDER , // TransactionType
	    				divisionCode , // DivisionCode
	    				companyCode , // CompanyCode
	    				currency.getCurrencyCode () , // CurrencyCode
	    				visit.getClientCode () , // ClientCode
	    				visit.getUserCode () , // UserCode
	    				visit.getVisitID () , // VisitID
	    				visit.getJourneyCode () , // JourneyCode
	    				Calendar.getInstance ().getTime () , // IssueDate
	    				deliveryDate.getTime () , // DeliveryDate
	    				grossAmount , // GrossAmount
	    				grossAmount - calValue , // DiscountAmount
	    				calValue , // NetAmount
	    				taxes +taxesAmount, // TaxAmount
	    				totalValue , // TotalTaxAmount
	    				0.0 , // RemainingAmount
	    				StatusUtils.isAvailable () , // TransactionStatus
	    				null , // Info1
	    				null , // Info2
	    				noteNew , // Info3
	    				priceListf , // Info4
	    				null , // Info5
	    				null , // ApprovedRequestReturnReference
	    				null , // TransactionPasswordCode
	    				note , // Notes
	    				null , // ClientReferenceNumber
	    				null , // PrintingTimes
	    				1 , // DeviceType
	    				IsProcessedUtils.isNotSync () , // IsProcessed
	    				now.getTime () ); // StampDate
	    		// Insert the transaction header in DB
	    		DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionHeadersDao ().insert ( transactionHeader );
		 
	    		int freeAddition = 0;
	    		int freeAddition1 = 0;
	    		int  i = 0;
				for ( i = 0 ; i < selectedOrders.size () ; i ++ ) {
    				// Check if the current item is ordered (not free) 
    				if ( selectedOrders.get ( i ).getQuantityBig () != 0 || selectedOrders.get ( i ).getQuantityMedium () != 0 || selectedOrders.get ( i ).getQuantitySmall () != 0 || selectedOrders.get(i).getAvailability() != null ) {
        				// TODO : Details of a sales order transaction detail
        				// Declare and initialize a transaction detail
	    				TransactionDetails transactionDetail = new TransactionDetails  ( null , // ID
	    						transactionHeader.getTransactionCode () , // TransactionCode
	    						  freeAddition , // LineID
	    						selectedOrders.get ( i ).getItem ().getItemCode () , // ItemCode
	    						TransactionDetailsUtils.Type.ORDER, // OrderedType
	    						(double) selectedOrders.get ( i ).getQuantityBig () , // QuantityBig
	    						(double) selectedOrders.get ( i ).getQuantityMedium () , // QuantityMedium
	    						(double) selectedOrders.get ( i ).getQuantitySmall () , // QuantitySmall
	    						(double) ( selectedOrders.get ( i ).getQuantityBig () * selectedOrders.get ( i ).getItem ().getUnitBigMedium () * selectedOrders.get ( i ).getItem ().getUnitMediumSmall ()
	    							+ selectedOrders.get ( i ).getQuantityMedium () * selectedOrders.get ( i ).getItem ().getUnitMediumSmall () )
	    							+ selectedOrders.get ( i ).getQuantitySmall () , // BasicUnitQuantity
    							(double) selectedOrders.get ( i ).getQuantityBig () , // ApprovedQuantityBig
    							(double) selectedOrders.get ( i ).getQuantityMedium () , // ApprovedQuantityMedium
    							(double) selectedOrders.get ( i ).getQuantitySmall () , // ApprovedQuantitySmall
    							(double) ( selectedOrders.get ( i ).getQuantityBig () * selectedOrders.get ( i ).getItem ().getUnitBigMedium () * selectedOrders.get ( i ).getItem ().getUnitMediumSmall ()
    	    							+ selectedOrders.get ( i ).getQuantityMedium () * selectedOrders.get ( i ).getItem ().getUnitMediumSmall () )
    	    							+ selectedOrders.get ( i ).getQuantitySmall () , // ApprovedBasicUnitQuantity
	    						0.0 , // MissedQuantityBig
	    						0.0 , // MissedQuantityMedium
	    						0.0 , // MissedQuantitySmall
	    						0.0 , // MissedBasicUnitQuantity
	    						(double) selectedOrders.get ( i ).getPriceBig () , // PriceBig
	    						(double) selectedOrders.get ( i ).getPriceMedium () , // PriceMedium
	    						(double) selectedOrders.get ( i ).getPriceSmall () , // PriceSmall
	    						(double) selectedOrders.get ( i ).getPriceBig () , // UserPriceBig
	    						(double) selectedOrders.get ( i ).getPriceMedium () , // UserPriceMedium
	    						(double) selectedOrders.get ( i ).getPriceSmall () , // UserPriceSmall
	    						selectedOrders.get ( i ).getDiscountPercentage () , // DiscountPercentage
	    						( selectedOrders.get ( i ).getQuantityBig () * selectedOrders.get ( i ).getPriceBig () + selectedOrders.get ( i ).getQuantityMedium ()
										* selectedOrders.get ( i ).getPriceMedium () + selectedOrders.get ( i ).getQuantitySmall () * selectedOrders.get ( i ).getPriceSmall () )
										* selectedOrders.get ( i ).getDiscountPercentage () / 100 , // DiscountAmount
	    						( selectedOrders.get ( i ).getQuantityBig () * selectedOrders.get ( i ).getPriceBig () + selectedOrders.get ( i ).getQuantityMedium ()
	    										* selectedOrders.get ( i ).getPriceMedium () + selectedOrders.get ( i ).getQuantitySmall () * selectedOrders.get ( i ).getPriceSmall () )
	    										* ( 100 - selectedOrders.get ( i ).getDiscountPercentage () ) / 100
	    					 +
		    					 selectedOrders.get ( i ).getQuantityMedium (   )
						    	 *selectedOrders.get ( i ).getCaseTaxAmount()+ (double)  selectedOrders.get ( i ).getQuantitySmall (   )
						    	 *selectedOrders.get ( i ).getUnitTaxAmount(), // TotalLineAmount
	    					 	selectedOrders.get ( i ).getSequence()+"" , // LineNote
	    						null , // ItemLot
	    						null,//selectedOrders.get ( i ).getReasonCode() , // ReasonCode
	    						selectedOrders.get ( i ).getTax () , // ItemTaxPercentage
	    						selectedOrders.get ( i ).getItem ().getItemName () , // ItemName
	    						selectedOrders.get ( i ).getItem ().getItemAltName () , // ItemAltName
	    						- 1 , // ParentLineID
	    						null , // ItemExpiryDate
	    						null , // ItemAffectedStock
	    						selectedOrders.get ( i ).getAvailability () , // IsInvoiceRelated
	    						null , // IsCompanyRelated
	    						null , 
	    						selectedOrders.get ( i ).getCaseTaxAmount() , 
								selectedOrders.get ( i ).getUnitTaxAmount() , 
								 selectedOrders.get ( i ).getQuantityMedium (   )
	    						*selectedOrders.get ( i ).getCaseTaxAmount()+ (double)  selectedOrders.get ( i ).getQuantitySmall (   )
	    						*selectedOrders.get ( i ).getUnitTaxAmount() , 
	    						selectedOrders.get ( i ).isMustStockList()?1:selectedOrders.get ( i ).isNewSKUList()?2:0,
	    						now.getTime () ); // StampDate
						// Insert the transaction detail in DB
						DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsDao ().insert ( transactionDetail );
						// Update target coverage (if any)
    					TargetUpdate.updateCoverage ( SalesOrderDetailsActivity.this , TargetHeadersUtils.Type.TOTAL_SALES_PER_CLIENT_PER_BRAND , transactionHeader.getClientCode () , TargetHeadersUtils.SubType.CLIENT , transactionDetail.getItemCode () , TargetHeadersUtils.SubType.ITEM , transactionHeader.getCompanyCode () , transactionHeader.getDivisionCode () , UpdateType.INCREASE , transactionDetail.getTotalLineAmount () , transactionHeader.getCurrencyCode () );
    					freeAddition++;}
    				if ( selectedOrders.get ( i ).getQuantityBig () == 0 && selectedOrders.get ( i ).getQuantityMedium () == 0 && selectedOrders.get ( i ).getQuantitySmall () == 0 
    						&& selectedOrders.get ( i ).getQuantityFreeAdded () == 0 && selectedOrders.get ( i ).getQuantityFreeCase () ==0
    			 			) {
        				// TODO : Details of a sales order transaction detail
        				// Declare and initialize a transaction detail
    					TransactionDetailsMissedMSL transactionDetail = new TransactionDetailsMissedMSL  ( null , // ID
	    						transactionHeader.getTransactionCode () , // TransactionCode
	    						  freeAddition1 , // LineID
	    						selectedOrders.get ( i ).getItem ().getItemCode () , // ItemCode
	    						TransactionDetailsUtils.Type.ORDER, // OrderedType
	    						(double) selectedOrders.get ( i ).getQuantityBig () , // QuantityBig
	    						(double) selectedOrders.get ( i ).getQuantityMedium () , // QuantityMedium
	    						(double) selectedOrders.get ( i ).getQuantitySmall () , // QuantitySmall
	    						(double) ( selectedOrders.get ( i ).getQuantityBig () * selectedOrders.get ( i ).getItem ().getUnitBigMedium () * selectedOrders.get ( i ).getItem ().getUnitMediumSmall ()
	    							+ selectedOrders.get ( i ).getQuantityMedium () * selectedOrders.get ( i ).getItem ().getUnitMediumSmall () )
	    							+ selectedOrders.get ( i ).getQuantitySmall () , // BasicUnitQuantity
    							(double) selectedOrders.get ( i ).getQuantityBig () , // ApprovedQuantityBig
    							(double) selectedOrders.get ( i ).getQuantityMedium () , // ApprovedQuantityMedium
    							(double) selectedOrders.get ( i ).getQuantitySmall () , // ApprovedQuantitySmall
    							(double) ( selectedOrders.get ( i ).getQuantityBig () * selectedOrders.get ( i ).getItem ().getUnitBigMedium () * selectedOrders.get ( i ).getItem ().getUnitMediumSmall ()
    	    							+ selectedOrders.get ( i ).getQuantityMedium () * selectedOrders.get ( i ).getItem ().getUnitMediumSmall () )
    	    							+ selectedOrders.get ( i ).getQuantitySmall () , // ApprovedBasicUnitQuantity
	    						0.0 , // MissedQuantityBig
	    						0.0 , // MissedQuantityMedium
	    						0.0 , // MissedQuantitySmall
	    						0.0 , // MissedBasicUnitQuantity
	    						(double) selectedOrders.get ( i ).getPriceBig () , // PriceBig
	    						(double) selectedOrders.get ( i ).getPriceMedium () , // PriceMedium
	    						(double) selectedOrders.get ( i ).getPriceSmall () , // PriceSmall
	    						(double) selectedOrders.get ( i ).getPriceBig () , // UserPriceBig
	    						(double) selectedOrders.get ( i ).getPriceMedium () , // UserPriceMedium
	    						(double) selectedOrders.get ( i ).getPriceSmall () , // UserPriceSmall
	    						selectedOrders.get ( i ).getDiscountPercentage () , // DiscountPercentage
	    						( selectedOrders.get ( i ).getQuantityBig () * selectedOrders.get ( i ).getPriceBig () + selectedOrders.get ( i ).getQuantityMedium ()
										* selectedOrders.get ( i ).getPriceMedium () + selectedOrders.get ( i ).getQuantitySmall () * selectedOrders.get ( i ).getPriceSmall () )
										* selectedOrders.get ( i ).getDiscountPercentage () / 100 , // DiscountAmount
	    						( selectedOrders.get ( i ).getQuantityBig () * selectedOrders.get ( i ).getPriceBig () + selectedOrders.get ( i ).getQuantityMedium ()
	    										* selectedOrders.get ( i ).getPriceMedium () + selectedOrders.get ( i ).getQuantitySmall () * selectedOrders.get ( i ).getPriceSmall () )
	    										* ( 100 - selectedOrders.get ( i ).getDiscountPercentage () ) / 100
	    					 +
	    					 selectedOrders.get ( i ).getQuantityMedium (   )
					    	 *selectedOrders.get ( i ).getCaseTaxAmount()+ (double)  selectedOrders.get ( i ).getQuantitySmall (   )
					    	 *selectedOrders.get ( i ).getUnitTaxAmount(), // TotalLineAmount
	    					 	selectedOrders.get ( i ).getSequence()+"" , // LineNote
	    						null , // ItemLot
	    						selectedOrders.get ( i ).getReasonCode() , // ReasonCode
	    						selectedOrders.get ( i ).getTax () , // ItemTaxPercentage
	    						selectedOrders.get ( i ).getItem ().getItemName () , // ItemName
	    						selectedOrders.get ( i ).getItem ().getItemAltName () , // ItemAltName
	    						- 1 , // ParentLineID
	    						null , // ItemExpiryDate
	    						null , // ItemAffectedStock
	    						selectedOrders.get ( i ).getAvailability () , // IsInvoiceRelated
	    						null , // IsCompanyRelated
	    						null , 
	    						selectedOrders.get ( i ).getCaseTaxAmount() , 
								selectedOrders.get ( i ).getUnitTaxAmount() , 
								 selectedOrders.get ( i ).getQuantityMedium (   )
	    						*selectedOrders.get ( i ).getCaseTaxAmount()+ (double)  selectedOrders.get ( i ).getQuantitySmall (   )
	    						*selectedOrders.get ( i ).getUnitTaxAmount() , 
	    						selectedOrders.get ( i ).isMustStockList()?1:selectedOrders.get ( i ).isNewSKUList()?2:0,
	    						now.getTime () ); // StampDate
						// Insert the transaction detail in DB
						DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsMissedMSLDao ().insert ( transactionDetail );
						// Update target coverage (if any)
    			 		freeAddition1++;} // End if// End if
    				// Clear the promotion counter
    				int promocounter = 0 ;
    				// Check if the current item has promotional offers
    				if ( ! selectedOrders.get ( i ).getTransactionPromotionDetails ().isEmpty () )
    					// Iterate over the promotional offers of the current item
    					for ( TransactionPromotionDetails transPromotionDetail : selectedOrders.get ( i ).getTransactionPromotionDetails () ) {
    						// Check if the current offer is an offered quantity (not a discount)
    						if ( transPromotionDetail.getDiscountAmount () == 0 && transPromotionDetail.getDiscountPercentage () == 0
    								&& transPromotionDetail.getItemCodeOffered () != null && transPromotionDetail.getQuantityOffered () != 0 ) {
    	        				// TODO : Details of a sales order transaction detail
    	        				// Declare and initialize a transaction detail
    							TransactionDetails transactionDetailFree = new TransactionDetails ( null , // ID
    									transactionHeader.getTransactionCode () , // TransactionCode
    									freeAddition  , // LineID
		    							selectedOrders.get ( i ).getItem ().getItemCode () , // ItemCode
		    							TransactionDetailsUtils.Type.FREE , // OrderedType
		    							0.0 , // QuantityBig
		    							0.0 , // QuantityMedium
		    							(double) selectedOrders.get ( i ).getQuantityFree () , // QuantitySmall
		    							(double) selectedOrders.get ( i ).getQuantityFree (), // BasicUnitQuantity
		    							0.0 , // ApprovedQuantityBig
		    							0.0 , // ApprovedQuantityMedium
		    							(double) selectedOrders.get ( i ).getQuantityFree () , // ApprovedQuantitySmall
		    							(double) selectedOrders.get ( i ).getQuantityFree () , // ApprovedBasicUnitQuantity
		    							0.0 , // MissedQuantityBig
		    							0.0 , // MissedQuantityMedium
		    							0.0 , // MissedQuantitySmall
		    							0.0 , // MissedBasicUnitQuantity
		    							(double) selectedOrders.get ( i ).getPriceBig () , // PriceBig
		    							(double) selectedOrders.get ( i ).getPriceMedium () , // PriceMedium
		    							(double) selectedOrders.get ( i ).getPriceSmall () , // PriceSmall
		    							(double) selectedOrders.get ( i ).getPriceBig () , // UserPriceBig
		    							(double) selectedOrders.get ( i ).getPriceMedium () , // UserPriceMedium
		    							(double) selectedOrders.get ( i ).getPriceSmall () , // UserPriceSmall
		    							100.0 , // DiscountPercentage
		    							selectedOrders.get ( i ).getQuantityFree () * selectedOrders.get ( i ).getPriceSmall () , // DiscountAmount
		    							0.0 +selectedOrders.get ( i ).getQuantityMedium (   )
			    						*selectedOrders.get ( i ).getCaseTaxAmount()+ (double)  selectedOrders.get ( i ).getQuantitySmall (   )
			    						*selectedOrders.get ( i ).getUnitTaxAmount(), // TotalLineAmount
		    							selectedOrders.get ( i ).getSequence()+"" , // LineNote
		    							null , // ItemLot
		    							selectedOrders.get ( i ).getReasonCode() , // ReasonCode
		    							selectedOrders.get ( i ).getTax () , // ItemTaxPercentage    							
		    							selectedOrders.get ( i ).getItem ().getItemName () , // ItemName
		    							selectedOrders.get ( i ).getItem ().getItemAltName () , // ItemAltName
		    							i + freeAddition , // ParentLineID
		    							null , // ItemExpiryDate
		    							null , // ItemAffectedStock
		    							selectedOrders.get ( i ).getAvailability () ,  // IsInvoiceRelated
		    							null , // IsCompanyRelated
		    							null , 
		    							selectedOrders.get ( i ).getCaseTaxAmount() , 
										selectedOrders.get ( i ).getUnitTaxAmount() , 
										 selectedOrders.get ( i ).getQuantityMedium (   )
			    						*selectedOrders.get ( i ).getCaseTaxAmount()+ (double)  selectedOrders.get ( i ).getQuantitySmall (   )
			    						*selectedOrders.get ( i ).getUnitTaxAmount() ,
			    						selectedOrders.get ( i ).isMustStockList()?1:selectedOrders.get ( i ).isNewSKUList()?2:0,
			    		    					
		    							now.getTime () ); // StampDate
    							// Increase the line ID
    						 freeAddition ++;
    							// Insert the transaction detail
    							DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsDao ().insert ( transactionDetailFree );
    						} // End if
    						// Insert the transaction promotion details
    						transPromotionDetail.setLineID ( promocounter ++ );
    						transPromotionDetail.setParentLineID ( i + freeAddition );
    						transPromotionDetail.setTransactionCode ( transactionHeaderCode );
    						transPromotionDetail.setStampDate ( Calendar.getInstance ().getTime () );
    						DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionPromotionDetailsDao ().insertOrReplace ( transPromotionDetail );
    					} // End for each
    				// Otherwise check if the current item has free items provided by the user
    				if ( selectedOrders.get ( i ).getQuantityFreeAdded () > 0 || selectedOrders.get ( i ).getQuantityFreeCase () > 0 ) {
        				// TODO : Details of a sales order transaction detail
        				// Declare and initialize a transaction detail
						TransactionDetails transactionDetailp = new TransactionDetails ( null , // ID
								transactionHeader.getTransactionCode () , // TransactionCode
								freeAddition , // LineID
								selectedOrders.get ( i ).getItem ().getItemCode () , // ItemCode
								TransactionDetailsUtils.Type.FREE, // OrderedType
								0.0 , // QuantityBig
								(double) selectedOrders.get ( i ).getQuantityFreeCase () , // QuantityMedium
								(double) selectedOrders.get ( i ).getQuantityFreeAdded () , // QuantitySmall
								(double) selectedOrders.get ( i ).getQuantityFreeAdded () + (double) selectedOrders.get ( i ).getQuantityFreeCase ()*selectedOrders.get ( i ).getItem().getUnitMediumSmall(), // BasicUnitQuantity
    							0.0 , // ApprovedQuantityBig
    							(double) selectedOrders.get ( i ).getQuantityFreeCase () , // ApprovedQuantityMedium
    							(double) selectedOrders.get ( i ).getQuantityFreeAdded () , // ApprovedQuantitySmall
    							(double) selectedOrders.get ( i ).getQuantityFreeAdded () + (double) selectedOrders.get ( i ).getQuantityFreeCase ()*selectedOrders.get ( i ).getItem().getUnitMediumSmall(),//(double) selectedOrders.get ( i ).getQuantityFreeAdded () , // ApprovedBasicUnitQuantity
    							0.0 , // MissedQuantityBig
    							0.0 , // MissedQuantityMedium
    							0.0 , // MissedQuantitySmall
    							0.0 , // MissedBasicUnitQuantity
								(double) selectedOrders.get ( i ).getPriceBig () , // PriceBig
								(double) selectedOrders.get ( i ).getPriceMedium () , // PriceMedium
								(double) selectedOrders.get ( i ).getPriceSmall () , // PriceSmall
								(double) selectedOrders.get ( i ).getPriceBig () , // UserPriceBig
								(double) selectedOrders.get ( i ).getPriceMedium () , // UserPriceMedium
								(double) selectedOrders.get ( i ).getPriceSmall () , // UserPriceSmall
								100.0 , // DiscountPercentage
								selectedOrders.get ( i ).getQuantityFreeAdded () * selectedOrders.get ( i ).getPriceSmall () +  (double) selectedOrders.get ( i ).getQuantityFreeCase ()*selectedOrders.get ( i ).getItem().getUnitMediumSmall()* selectedOrders.get ( i ).getPriceSmall (), // DiscountAmount
								0.0 +(freetaxe? selectedOrders.get ( i ).getQuantityFreeCase (   )
	    						*selectedOrders.get ( i ).getCaseTaxAmount()
	    						+ (double)  selectedOrders.get ( i ).getQuantityFreeAdded (   )
	    			 			*selectedOrders.get ( i ).getUnitTaxAmount():0), // TotalLineAmount
								selectedOrders.get ( i ).getSequence()+"" , // LineNote
								null , // ItemLot
								null,//selectedOrders.get ( i ).getReasonCode() , // ReasonCode
								selectedOrders.get ( i ).getTax () , // ItemTaxPercentage
								selectedOrders.get ( i ).getItem ().getItemName () , // ItemName
								selectedOrders.get ( i ).getItem ().getItemAltName () , // ItemAltName
								-2 , // ParentLineID
    							null , // ItemExpiryDate
    							null , // ItemAffectedStock
    							selectedOrders.get ( i ).getAvailability () , // IsInvoiceRelated
    							null , // IsCompanyRelated
    							null , 
    							selectedOrders.get ( i ).getCaseTaxAmount() , 
								selectedOrders.get ( i ).getUnitTaxAmount() , 
								(freetaxe? selectedOrders.get ( i ).getQuantityFreeCase (   )
	    						*selectedOrders.get ( i ).getCaseTaxAmount()
	    						+ (double)  selectedOrders.get ( i ).getQuantityFreeAdded (   )
	    						*selectedOrders.get ( i ).getUnitTaxAmount():0),
	    						selectedOrders.get ( i ).isMustStockList()?1:selectedOrders.get ( i ).isNewSKUList()?2:0,
	    		    					
    							now.getTime () ); // StampDate
						// Increase counter
						 freeAddition++;
						// Insert the transaction detail in DB
						DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsDao ().insert ( transactionDetailp ); // StampDate
					} // End if
    				
    				
    				
    				
    				
    				
    				
    				// Otherwise check if the current item has free items provided by the user
//    				if ( selectedOrders.get ( i ).getQuantityFreeCase () > 0 ) {
//        				// TODO : Details of a sales order transaction detail
//        				// Declare and initialize a transaction detail
//						TransactionDetails transactionDetailp = new TransactionDetails ( null , // ID
//								transactionHeader.getTransactionCode () , // TransactionCode
//								i + freeAddition +1, // LineID
//								selectedOrders.get ( i ).getItem ().getItemCode () , // ItemCode
//								TransactionDetailsUtils.Type.FREE, // OrderedType
//								0.0 , // QuantityBig
//								(double) selectedOrders.get ( i ).getQuantityFreeCase (), // QuantityMedium
//								0.0 , // QuantitySmall
//								(double) selectedOrders.get ( i ).getQuantityFreeCase () * selectedOrders.get(i).getItem().getUnitMediumSmall() , // BasicUnitQuantity
//    							0.0 , // ApprovedQuantityBig
//    							(double) selectedOrders.get ( i ).getQuantityFreeCase () , // ApprovedQuantityMedium
//    							0.0 , // ApprovedQuantitySmall
//    							(double) selectedOrders.get ( i ).getQuantityFreeCase () , // ApprovedBasicUnitQuantity
//    							0.0 , // MissedQuantityBig
//    							0.0 , // MissedQuantityMedium
//    							0.0 , // MissedQuantitySmall
//    							0.0 , // MissedBasicUnitQuantity
//								(double) selectedOrders.get ( i ).getPriceBig () , // PriceBig
//								(double) selectedOrders.get ( i ).getPriceMedium () , // PriceMedium
//								(double) selectedOrders.get ( i ).getPriceSmall () , // PriceSmall
//								(double) selectedOrders.get ( i ).getPriceBig () , // UserPriceBig
//								(double) selectedOrders.get ( i ).getPriceMedium () , // UserPriceMedium
//								(double) selectedOrders.get ( i ).getPriceSmall () , // UserPriceSmall
//								100.0 , // DiscountPercentage
//								selectedOrders.get ( i ).getQuantityFreeCase () * selectedOrders.get ( i ).getPriceSmall () , // DiscountAmount
//								0.0 , // TotalLineAmount
//								null , // LineNote
//								null , // ItemLot
//								null , // ReasonCode
//								selectedOrders.get ( i ).getTax () , // ItemTaxPercentage
//								selectedOrders.get ( i ).getItem ().getItemName () , // ItemName
//								selectedOrders.get ( i ).getItem ().getItemAltName () , // ItemAltName
//								-2 , // ParentLineID
//    							null , // ItemExpiryDate
//    							null , // ItemAffectedStock
//    							null , // IsInvoiceRelated
//    							null , // IsCompanyRelated
//    							now.getTime () ); // StampDate
//						// Increase counter
//						freeAddition++;
//						// Insert the transaction detail in DB
//						DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsDao ().insert ( transactionDetailp ); // StampDate
//					} // End if
//    				
    			
    				
    				
    				
    				
				} 
				if(freeOrders!=null)
				for ( i = 0 ; i < freeOrders.size () ; i ++ ) {
				if ( freeOrders.get ( i ).getFreeMedium () > 0 || freeOrders.get ( i ).getFreeSmall () > 0 ) {
    				// TODO : Details of a sales order transaction detail
    				// Declare and initialize a transaction detail
					TransactionDetails transactionDetailp = new TransactionDetails ( null , // ID
							transactionHeader.getTransactionCode () , // TransactionCode
							freeAddition , // LineID
							freeOrders.get ( i ).getItem ().getItemCode () , // ItemCode
							TransactionDetailsUtils.Type.FREE, // OrderedType
							0.0 , // QuantityBig
							(double) freeOrders.get ( i ).getFreeMedium()   , // QuantityMedium
							(double) freeOrders.get ( i ).getFreeSmall()  , // QuantitySmall
							(double) freeOrders.get ( i ).getFreeSmall () + (double) freeOrders.get ( i ).getFreeMedium ()*freeOrders.get ( i ).getItem().getUnitMediumSmall(),//(double) selectedOrders.get ( i ).getQuantityFreeAdded () , // ApprovedBasicUnitQuantity
							 0.0 , // ApprovedQuantityBig
							(double) freeOrders.get ( i ).getFreeMedium () , // ApprovedQuantityMedium
							(double) freeOrders.get ( i ).getFreeSmall () , // ApprovedQuantitySmall
							(double) freeOrders.get ( i ).getFreeSmall () + (double) freeOrders.get ( i ).getFreeMedium ()*freeOrders.get ( i ).getItem().getUnitMediumSmall(),//(double) selectedOrders.get ( i ).getQuantityFreeAdded () , // ApprovedBasicUnitQuantity
							0.0 , // MissedQuantityBig
							0.0 , // MissedQuantityMedium
							0.0 , // MissedQuantitySmall
							0.0 , // MissedBasicUnitQuantity
							(double) freeOrders.get ( i ).getPriceBig () , // PriceBig
							(double) freeOrders.get ( i ).getPriceMedium () , // PriceMedium
							(double) freeOrders.get ( i ).getPriceSmall () , // PriceSmall
							(double) freeOrders.get ( i ).getPriceBig () , // UserPriceBig
							(double) freeOrders.get ( i ).getPriceMedium () , // UserPriceMedium
							(double) freeOrders.get ( i ).getPriceSmall () , // UserPriceSmall
							100.0 , // DiscountPercentage
							freeOrders.get ( i ).getFreeSmall () * freeOrders.get ( i ).getPriceSmall () +  (double) freeOrders.get ( i ).getFreeMedium ()*freeOrders.get ( i ).getItem().getUnitMediumSmall()* freeOrders.get ( i ).getPriceMedium()  , // DiscountAmount
							0.0 +(freetaxe?freeOrders.get ( i ).getFreeMedium ( )
	    				    *freeOrders.get ( i ).getCaseTaxAmount()
	    				    + (double)  freeOrders.get ( i ).getFreeSmall ( )
	    				    *freeOrders.get ( i ).getUnitTaxAmount():0), // TotalLineAmount
							freeOrders.get ( i ).getSequence()+"" , // LineNote
							null , // ItemLot
							null , // ReasonCode
							freeOrders.get ( i ).getTax () , // ItemTaxPercentage
							freeOrders.get ( i ).getItem ().getItemName () , // ItemName
							freeOrders.get ( i ).getItem ().getItemAltName () , // ItemAltName
							-33 , // ParentLineID
							null , // ItemExpiryDate
							null , // ItemAffectedStock
							freeOrders.get ( i ).getAvailability () , // IsInvoiceRelated
							null , // IsCompanyRelated
							null , 
							freeOrders.get ( i ).getCaseTaxAmount() , 
							freeOrders.get ( i ).getUnitTaxAmount() , 
							freeOrders.get ( i ).getFreeMedium ( )
	    				    *freeOrders.get ( i ).getCaseTaxAmount()
	    				    + (double)  freeOrders.get ( i ).getFreeSmall ( )
	    				    *freeOrders.get ( i ).getUnitTaxAmount(),
	    				    freeOrders.get ( i ).isMustStockList()?1:freeOrders.get ( i ).isNewSKUList()?2:0,
	    	    					
							now.getTime () ); // StampDate
					// Increase counter
					freeAddition++;
					// Insert the transaction detail in DB
					DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsDao ().insert ( transactionDetailp ); // StampDate
				} // End if
				
				}
				
				if(promotedOrders!=null)
					for ( i = 0 ; i < promotedOrders.size () ; i ++ ) {
					if ( promotedOrders.get ( i ).getBasketMedium () > 0 || promotedOrders.get ( i ).getBasketSmall () > 0 || promotedOrders.get ( i ).getBasketBig () > 0 ) {
	    				// TODO : Details of a sales order transaction detail
	    				// Declare and initialize a transaction detail
						TransactionDetails transactionDetailp = new TransactionDetails ( null , // ID
								transactionHeader.getTransactionCode () , // TransactionCode
								freeAddition , // LineID
								promotedOrders.get ( i ).getItem ().getItemCode () , // ItemCode
								TransactionDetailsUtils.Type.ORDER, // OrderedType
								0.0 , // QuantityBig
								(double) promotedOrders.get ( i ).getBasketMedium()   , // QuantityMedium
								(double) promotedOrders.get ( i ).getBasketSmall()  , // QuantitySmall
								(double) promotedOrders.get ( i ).getBasketSmall () + (double) promotedOrders.get ( i ).getBasketMedium ()*promotedOrders.get ( i ).getItem().getUnitMediumSmall(), // BasicUnitQuantity
								0.0 , // ApprovedQuantityBig
								(double) promotedOrders.get ( i ).getBasketMedium () , // ApprovedQuantityMedium
								(double) promotedOrders.get ( i ).getBasketSmall () , // ApprovedQuantitySmall
								(double) promotedOrders.get ( i ).getBasketSmall () + (double) promotedOrders.get ( i ).getBasketMedium ()*promotedOrders.get ( i ).getItem().getUnitMediumSmall(),//(double) selectedOrders.get ( i ).getQuantityFreeAdded () , // ApprovedBasicUnitQuantity
								(double) promotedOrders.get ( i ).getQuantityBasket() , // MissedQuantityBig
								0.0 , // MissedQuantityMedium
								0.0 , // MissedQuantitySmall
								0.0 , // MissedBasicUnitQuantity
								(double) promotedOrders.get ( i ).getPriceBig () , // PriceBig 
								(double) promotedOrders.get ( i ).getPriceMedium () , //  PriceMedium
								(double) promotedOrders.get ( i ).getPriceSmall () , // PriceSmall
								(double) promotedOrders.get ( i ).getBasketPriceBig() , //USERPriceBig
								(double) promotedOrders.get ( i ).getBasketPriceMedium () , //  PriceMedium
								(double) promotedOrders.get ( i ).getBasketPriceSmall () , //  PriceSmall
								(double) promotedOrders.get ( i ).getPromotionDiscountPercentageBasket(), // DiscountPercentage
								(	promotedOrders.get ( i ).getBasketSmall () * promotedOrders.get ( i ).getBasketPriceSmall ()
										+  (double) promotedOrders.get ( i ).getBasketMedium ()* 
										promotedOrders.get ( i ).getBasketPriceMedium ())*promotedOrders.get(i).getPromotionDiscountPercentageBasket()/100, // DiscountAmount
										(	promotedOrders.get ( i ).getBasketSmall () * promotedOrders.get ( i ).getBasketPriceSmall ()
												+  (double) promotedOrders.get ( i ).getBasketMedium () *
												promotedOrders.get ( i ).getBasketPriceMedium ())-	(	promotedOrders.get ( i ).getBasketSmall () * promotedOrders.get ( i ).getBasketPriceSmall ()
												+  (double) promotedOrders.get ( i ).getBasketMedium ()*  
												promotedOrders.get ( i ).getBasketPriceMedium ())*promotedOrders.get(i).getPromotionDiscountPercentageBasket()/100+	promotedOrders.get ( i ).getBasketMedium ( )
						    				    *promotedOrders.get ( i ).getCaseTaxAmount()
						    				    +((freetaxe&&promotedOrders.get(i).getPromotionDiscountPercentageBasket()!=100)?
						    				    		(double)  promotedOrders.get ( i ).getBasketSmall ( )
						    				    *promotedOrders.get ( i ).getUnitTaxAmount():0 ) , // TotalLineAmount
										promotedOrders.get ( i ).getSequence()+"" , // LineNote
										null , // ItemLot
										null, // ReasonCode
								promotedOrders.get ( i ).getTax () , // ItemTaxPercentage
								promotedOrders.get ( i ).getItem ().getItemName () , // ItemName
								promotedOrders.get ( i ).getPromotionID() , // ItemAltName
								-22 , // ParentLineID
								null , // ItemExpiryDate
								null , // ItemAffectedStock
								promotedOrders.get ( i ).getAvailability () , // IsInvoiceRelated
								null , // IsCompanyRelated
								null , 
								promotedOrders.get ( i ).getCaseTaxAmount() , 
								promotedOrders.get ( i ).getUnitTaxAmount() , 
								((freetaxe&&promotedOrders.get(i).getPromotionDiscountPercentageBasket()!=100)?
		    				    		(double)  promotedOrders.get ( i ).getBasketSmall ( )
		    				    *promotedOrders.get ( i ).getUnitTaxAmount():0 ) , 
		    				    null
		    				    ,	now.getTime () ); // StampDate
						// Increase counter
						freeAddition++;
						// Insert the transaction detail in DB
						DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsDao ().insert ( transactionDetailp ); // StampDate
					} // End if
					
					}
	    		// Indicate that a sales order was successfully performed during this visit
	    		CallAction.setCallActionStatus ( this , visit.getVisitID () , CallAction.ID.SALES_ORDER , true );	
				
				// Commit changes
				DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getDatabase ().setTransactionSuccessful ();
				// Indicate that the save was successful
				Vibration.vibrate ( SalesOrderDetailsActivity.this );
    		} catch ( Exception exception ) {
				// Indicate that an error occurred
				error = true;
    		} finally {
				// End transaction
				DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getDatabase ().endTransaction ();
    		} // End try-catch-finally block
		} // End if
		else if ( transactionHeader != null && transactionDetails != null  && saveMovements) {
			  saveMovements=false;
			// Before saving, refresh the objects to make sure the sales order is not already sent to the server ( via auto sync )
			DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getTransactionHeadersDao ().refresh ( transactionHeader );
			// Check that the transaction header is not processed to the server
			if ( transactionHeader.getIsProcessed () == IsProcessedUtils.isSync () ) {
    			// Indicate that the sales order cannot be modified anymore
				Baguette.showText ( this ,
						AppResources.getInstance ( this ).getString ( this , R.string.sales_order_already_processed_message ) ,
						Baguette.BackgroundColor.RED );
	    		// Consume event
	    		return;
			} // End if
			
			DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getDatabase ().beginTransaction ();
			 
			List <TransactionDetails> transactionDetails=		DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsDao ().queryBuilder().where(TransactionDetailsDao .Properties.TransactionCode.eq(transactionHeader.getTransactionCode ())
					 ).list();
			for(TransactionDetails tds :transactionDetails)
				DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsDao ().delete ( tds );
			List <TransactionDetailsMissedMSL> transactionDetailsMissedMSL=		DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsMissedMSLDao ().queryBuilder().where(TransactionDetailsMissedMSLDao .Properties.TransactionCode.eq(transactionHeader.getTransactionCode ())
					 ).list();
			if(transactionDetailsMissedMSL!=null)
			for(TransactionDetailsMissedMSL tds :transactionDetailsMissedMSL)
				DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsMissedMSLDao ().delete ( tds );
			

			// Retrieve a reference to the transaction code
			transactionHeaderCode = transactionHeader.getTransactionCode ();
			// Declare and initialize variable used to store the orders previous total tax amount, in order to remove it from the target coverage (if any) 
			double previousTotalTaxAmount = transactionHeader.getTotalTaxAmount ();
    		// Update the transaction header
    		transactionHeader.setTaxAmount ( taxes+taxesAmount );
    		transactionHeader.setGrossAmount ( grossAmount );
    		transactionHeader.setNetAmount ( calValue );
    		transactionHeader.setDiscountAmount ( grossAmount  - calValue+discountAmount );
    		transactionHeader.setTotalTaxAmount ( totalValue );
    		transactionHeader.setDeliveryDate ( deliveryDate.getTime () );
    		transactionHeader.setNotes ( note );
    		transactionHeader.setInfo3(noteNew);
    		transactionHeader.setStampDate ( now.getTime () );
    		transactionHeader.setIsProcessed(1);
    		transactionHeader.setJourneyCode(visit.getJourneyCode());
    	
	
			
			try { 
				DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionHeadersDao ().update ( transactionHeader ); // StampDate
				// Begin transaction
		 		int freeAddition = 0;
		 		int freeAddition1 = 0;
	    		int  i = 0;
				for ( i = 0 ; i < selectedOrders.size () ; i ++ ) {
    				// Check if the current item is ordered (not free) 
    				if ( selectedOrders.get ( i ).getQuantityBig () != 0 || selectedOrders.get ( i ).getQuantityMedium () != 0 || selectedOrders.get ( i ).getQuantitySmall () != 0 || selectedOrders.get(i).getAvailability() != null ) {
        				// TODO : Details of a sales order transaction detail
        				// Declare and initialize a transaction detail
	    				TransactionDetails transactionDetail = new TransactionDetails  ( null , // ID
	    						transactionHeader.getTransactionCode () , // TransactionCode
	    						  freeAddition , // LineID
	    						selectedOrders.get ( i ).getItem ().getItemCode () , // ItemCode
	    						TransactionDetailsUtils.Type.ORDER, // OrderedType
	    						(double) selectedOrders.get ( i ).getQuantityBig () , // QuantityBig
	    						(double) selectedOrders.get ( i ).getQuantityMedium () , // QuantityMedium
	    						(double) selectedOrders.get ( i ).getQuantitySmall () , // QuantitySmall
	    						(double) ( selectedOrders.get ( i ).getQuantityBig () * selectedOrders.get ( i ).getItem ().getUnitBigMedium () * selectedOrders.get ( i ).getItem ().getUnitMediumSmall ()
	    							+ selectedOrders.get ( i ).getQuantityMedium () * selectedOrders.get ( i ).getItem ().getUnitMediumSmall () )
	    							+ selectedOrders.get ( i ).getQuantitySmall () , // BasicUnitQuantity
    							(double) selectedOrders.get ( i ).getQuantityBig () , // ApprovedQuantityBig
    							(double) selectedOrders.get ( i ).getQuantityMedium () , // ApprovedQuantityMedium
    							(double) selectedOrders.get ( i ).getQuantitySmall () , // ApprovedQuantitySmall
    							(double) ( selectedOrders.get ( i ).getQuantityBig () * selectedOrders.get ( i ).getItem ().getUnitBigMedium () * selectedOrders.get ( i ).getItem ().getUnitMediumSmall ()
    	    							+ selectedOrders.get ( i ).getQuantityMedium () * selectedOrders.get ( i ).getItem ().getUnitMediumSmall () )
    	    							+ selectedOrders.get ( i ).getQuantitySmall () , // ApprovedBasicUnitQuantity
	    						0.0 , // MissedQuantityBig
	    						0.0 , // MissedQuantityMedium
	    						0.0 , // MissedQuantitySmall
	    						0.0 , // MissedBasicUnitQuantity
	    						(double) selectedOrders.get ( i ).getPriceBig () , // PriceBig
	    						(double) selectedOrders.get ( i ).getPriceMedium () , // PriceMedium
	    						(double) selectedOrders.get ( i ).getPriceSmall () , // PriceSmall
	    						(double) selectedOrders.get ( i ).getPriceBig () , // UserPriceBig
	    						(double) selectedOrders.get ( i ).getPriceMedium () , // UserPriceMedium
	    						(double) selectedOrders.get ( i ).getPriceSmall () , // UserPriceSmall
	    						selectedOrders.get ( i ).getDiscountPercentage () , // DiscountPercentage
	    						( selectedOrders.get ( i ).getQuantityBig () * selectedOrders.get ( i ).getPriceBig () + selectedOrders.get ( i ).getQuantityMedium ()
										* selectedOrders.get ( i ).getPriceMedium () + selectedOrders.get ( i ).getQuantitySmall () * selectedOrders.get ( i ).getPriceSmall () )
										* selectedOrders.get ( i ).getDiscountPercentage () / 100 , // DiscountAmount
	    						( selectedOrders.get ( i ).getQuantityBig () * selectedOrders.get ( i ).getPriceBig () + selectedOrders.get ( i ).getQuantityMedium ()
	    										* selectedOrders.get ( i ).getPriceMedium () + selectedOrders.get ( i ).getQuantitySmall () * selectedOrders.get ( i ).getPriceSmall () )
	    										* ( 100 - selectedOrders.get ( i ).getDiscountPercentage () ) / 100 
	    										+		 selectedOrders.get ( i ).getQuantityMedium (   )
	    			    						*selectedOrders.get ( i ).getCaseTaxAmount()+ (double)  selectedOrders.get ( i ).getQuantitySmall (   )
	    			    						*selectedOrders.get ( i ).getUnitTaxAmount(), // TotalLineAmount
	    					 	selectedOrders.get ( i ).getSequence()+"" , // LineNote
	    						null , // ItemLot
	    						null,//selectedOrders.get ( i ).getReasonCode() , // ReasonCode
	    						selectedOrders.get ( i ).getTax () , // ItemTaxPercentage
	    						selectedOrders.get ( i ).getItem ().getItemName () , // ItemName
	    						selectedOrders.get ( i ).getItem ().getItemAltName () , // ItemAltName
	    						- 1 , // ParentLineID
	    						null , // ItemExpiryDate
	    						null , // ItemAffectedStock
	    						selectedOrders.get ( i ).getAvailability () , // IsInvoiceRelated
	    						null , // IsCompanyRelated
	    						null , 
	    						selectedOrders.get ( i ).getCaseTaxAmount() , 
								selectedOrders.get ( i ).getUnitTaxAmount() , 
								 selectedOrders.get ( i ).getQuantityMedium (   )
	    						*selectedOrders.get ( i ).getCaseTaxAmount()+ (double)  selectedOrders.get ( i ).getQuantitySmall (   )
	    						*selectedOrders.get ( i ).getUnitTaxAmount() , 
	    						selectedOrders.get ( i ).isMustStockList()?1:selectedOrders.get ( i ).isNewSKUList()?2:0,
	    				    	    	
	    						now.getTime () ); // StampDate
	    				freeAddition++;
						// Insert the transaction detail in DB
						DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsDao ().insert ( transactionDetail );
						// Update target coverage (if any)
    					TargetUpdate.updateCoverage ( SalesOrderDetailsActivity.this , TargetHeadersUtils.Type.TOTAL_SALES_PER_CLIENT_PER_BRAND , transactionHeader.getClientCode () , TargetHeadersUtils.SubType.CLIENT , transactionDetail.getItemCode () , TargetHeadersUtils.SubType.ITEM , transactionHeader.getCompanyCode () , transactionHeader.getDivisionCode () , UpdateType.INCREASE , transactionDetail.getTotalLineAmount () , transactionHeader.getCurrencyCode () );
					} // End if
    				if ( selectedOrders.get ( i ).getQuantityBig () == 0 && selectedOrders.get ( i ).getQuantityMedium () == 0 && selectedOrders.get ( i ).getQuantitySmall () == 0 
    						&& selectedOrders.get ( i ).getQuantityFreeAdded () == 0 && selectedOrders.get ( i ).getQuantityFreeCase () ==0
    			 			) {
        				// TODO : Details of a sales order transaction detail
        				// Declare and initialize a transaction detail
    					TransactionDetailsMissedMSL transactionDetail = new TransactionDetailsMissedMSL  ( null , // ID
	    						transactionHeader.getTransactionCode () , // TransactionCode
	    						  freeAddition1 , // LineID
	    						selectedOrders.get ( i ).getItem ().getItemCode () , // ItemCode
	    						TransactionDetailsUtils.Type.ORDER, // OrderedType
	    						(double) selectedOrders.get ( i ).getQuantityBig () , // QuantityBig
	    						(double) selectedOrders.get ( i ).getQuantityMedium () , // QuantityMedium
	    						(double) selectedOrders.get ( i ).getQuantitySmall () , // QuantitySmall
	    						(double) ( selectedOrders.get ( i ).getQuantityBig () * selectedOrders.get ( i ).getItem ().getUnitBigMedium () * selectedOrders.get ( i ).getItem ().getUnitMediumSmall ()
	    							+ selectedOrders.get ( i ).getQuantityMedium () * selectedOrders.get ( i ).getItem ().getUnitMediumSmall () )
	    							+ selectedOrders.get ( i ).getQuantitySmall () , // BasicUnitQuantity
    							(double) selectedOrders.get ( i ).getQuantityBig () , // ApprovedQuantityBig
    							(double) selectedOrders.get ( i ).getQuantityMedium () , // ApprovedQuantityMedium
    							(double) selectedOrders.get ( i ).getQuantitySmall () , // ApprovedQuantitySmall
    							(double) ( selectedOrders.get ( i ).getQuantityBig () * selectedOrders.get ( i ).getItem ().getUnitBigMedium () * selectedOrders.get ( i ).getItem ().getUnitMediumSmall ()
    	    							+ selectedOrders.get ( i ).getQuantityMedium () * selectedOrders.get ( i ).getItem ().getUnitMediumSmall () )
    	    							+ selectedOrders.get ( i ).getQuantitySmall () , // ApprovedBasicUnitQuantity
	    						0.0 , // MissedQuantityBig
	    						0.0 , // MissedQuantityMedium
	    						0.0 , // MissedQuantitySmall
	    						0.0 , // MissedBasicUnitQuantity
	    						(double) selectedOrders.get ( i ).getPriceBig () , // PriceBig
	    						(double) selectedOrders.get ( i ).getPriceMedium () , // PriceMedium
	    						(double) selectedOrders.get ( i ).getPriceSmall () , // PriceSmall
	    						(double) selectedOrders.get ( i ).getPriceBig () , // UserPriceBig
	    						(double) selectedOrders.get ( i ).getPriceMedium () , // UserPriceMedium
	    						(double) selectedOrders.get ( i ).getPriceSmall () , // UserPriceSmall
	    						selectedOrders.get ( i ).getDiscountPercentage () , // DiscountPercentage
	    						( selectedOrders.get ( i ).getQuantityBig () * selectedOrders.get ( i ).getPriceBig () + selectedOrders.get ( i ).getQuantityMedium ()
										* selectedOrders.get ( i ).getPriceMedium () + selectedOrders.get ( i ).getQuantitySmall () * selectedOrders.get ( i ).getPriceSmall () )
										* selectedOrders.get ( i ).getDiscountPercentage () / 100 , // DiscountAmount
	    						( selectedOrders.get ( i ).getQuantityBig () * selectedOrders.get ( i ).getPriceBig () + selectedOrders.get ( i ).getQuantityMedium ()
	    										* selectedOrders.get ( i ).getPriceMedium () + selectedOrders.get ( i ).getQuantitySmall () * selectedOrders.get ( i ).getPriceSmall () )
	    										* ( 100 - selectedOrders.get ( i ).getDiscountPercentage () ) / 100
	    					 +
	    					 selectedOrders.get ( i ).getQuantityMedium (   )
					    	 *selectedOrders.get ( i ).getCaseTaxAmount()+ (double)  selectedOrders.get ( i ).getQuantitySmall (   )
					    	 *selectedOrders.get ( i ).getUnitTaxAmount(), // TotalLineAmount
	    					 	selectedOrders.get ( i ).getSequence()+"" , // LineNote
	    						null , // ItemLot
	    						selectedOrders.get ( i ).getReasonCode() , // ReasonCode
	    						selectedOrders.get ( i ).getTax () , // ItemTaxPercentage
	    						selectedOrders.get ( i ).getItem ().getItemName () , // ItemName
	    						selectedOrders.get ( i ).getItem ().getItemAltName () , // ItemAltName
	    						- 1 , // ParentLineID
	    						null , // ItemExpiryDate
	    						null , // ItemAffectedStock
	    						selectedOrders.get ( i ).getAvailability () , // IsInvoiceRelated
	    						null , // IsCompanyRelated
	    						null , 
	    						selectedOrders.get ( i ).getCaseTaxAmount() , 
								selectedOrders.get ( i ).getUnitTaxAmount() , 
								 selectedOrders.get ( i ).getQuantityMedium (   )
	    						*selectedOrders.get ( i ).getCaseTaxAmount()+ (double)  selectedOrders.get ( i ).getQuantitySmall (   )
	    						*selectedOrders.get ( i ).getUnitTaxAmount() , 
	    						selectedOrders.get ( i ).isMustStockList()?1:selectedOrders.get ( i ).isNewSKUList()?2:0,
	    						now.getTime () ); // StampDate
						// Insert the transaction detail in DB
						DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsMissedMSLDao ().insert ( transactionDetail );
						// Update target coverage (if any)
    			 		freeAddition1++;} // End if// End if
    				// Clear the promotion counter
    				int promocounter = 0 ;
    				// Check if the current item has promotional offers
    				if ( ! selectedOrders.get ( i ).getTransactionPromotionDetails ().isEmpty () )
    					// Iterate over the promotional offers of the current item
    					for ( TransactionPromotionDetails transPromotionDetail : selectedOrders.get ( i ).getTransactionPromotionDetails () ) {
    						// Check if the current offer is an offered quantity (not a discount)
    						if ( transPromotionDetail.getDiscountAmount () == 0 && transPromotionDetail.getDiscountPercentage () == 0
    								&& transPromotionDetail.getItemCodeOffered () != null && transPromotionDetail.getQuantityOffered () != 0 ) {
    	        				// TODO : Details of a sales order transaction detail
    	        				// Declare and initialize a transaction detail
    							TransactionDetails transactionDetailFree = new TransactionDetails ( null , // ID
    									transactionHeader.getTransactionCode () , // TransactionCode
		    							i + freeAddition + 1 , // LineID
		    							selectedOrders.get ( i ).getItem ().getItemCode () , // ItemCode
		    							TransactionDetailsUtils.Type.FREE , // OrderedType
		    							0.0 , // QuantityBig
		    							0.0 , // QuantityMedium
		    							(double) selectedOrders.get ( i ).getQuantityFree () , // QuantitySmall
		    							(double) selectedOrders.get ( i ).getQuantityFree (), // BasicUnitQuantity
		    							0.0 , // ApprovedQuantityBig
		    							0.0 , // ApprovedQuantityMedium
		    							(double) selectedOrders.get ( i ).getQuantityFree () , // ApprovedQuantitySmall
		    							(double) selectedOrders.get ( i ).getQuantityFree () , // ApprovedBasicUnitQuantity
		    							0.0 , // MissedQuantityBig
		    							0.0 , // MissedQuantityMedium
		    							0.0 , // MissedQuantitySmall
		    							0.0 , // MissedBasicUnitQuantity
		    							(double) selectedOrders.get ( i ).getPriceBig () , // PriceBig
		    							(double) selectedOrders.get ( i ).getPriceMedium () , // PriceMedium
		    							(double) selectedOrders.get ( i ).getPriceSmall () , // PriceSmall
		    							(double) selectedOrders.get ( i ).getPriceBig () , // UserPriceBig
		    							(double) selectedOrders.get ( i ).getPriceMedium () , // UserPriceMedium
		    							(double) selectedOrders.get ( i ).getPriceSmall () , // UserPriceSmall
		    							100.0 , // DiscountPercentage
		    							selectedOrders.get ( i ).getQuantityFree () * selectedOrders.get ( i ).getPriceSmall () , // DiscountAmount
		    							0.0 , // TotalLineAmount
		    							selectedOrders.get ( i ).getSequence()+"" , // LineNote
		    							null , // ItemLot
		    							null , // ReasonCode
		    							selectedOrders.get ( i ).getTax () , // ItemTaxPercentage    							
		    							selectedOrders.get ( i ).getItem ().getItemName () , // ItemName
		    							selectedOrders.get ( i ).getItem ().getItemAltName () , // ItemAltName
		    							i + freeAddition , // ParentLineID
		    							null , // ItemExpiryDate
		    							null , // ItemAffectedStock
		    							selectedOrders.get ( i ).getAvailability () ,  // IsInvoiceRelated
		    							null , // IsCompanyRelated
		    							null , 
		    							selectedOrders.get ( i ).getCaseTaxAmount() , 
										selectedOrders.get ( i ).getUnitTaxAmount() , 
								       (double)  selectedOrders.get ( i ).getQuantityFree (   )
				    				   *selectedOrders.get ( i ).getUnitTaxAmount() , 
				    					selectedOrders.get ( i ).isMustStockList()?1:selectedOrders.get ( i ).isNewSKUList()?2:0,
				    	    				    
		    							now.getTime () ); // StampDate
    							// Increase the line ID
    							freeAddition ++;
    							// Insert the transaction detail
    							DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsDao ().insert ( transactionDetailFree );
    						} // End if
    						// Insert the transaction promotion details
    						transPromotionDetail.setLineID ( promocounter ++ );
    						transPromotionDetail.setParentLineID ( i + freeAddition );
    						transPromotionDetail.setTransactionCode ( transactionHeaderCode );
    						transPromotionDetail.setStampDate ( Calendar.getInstance ().getTime () );
    						DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionPromotionDetailsDao ().insertOrReplace ( transPromotionDetail );
    					} // End for each
    				// Otherwise check if the current item has free items provided by the user
    				if ( selectedOrders.get ( i ).getQuantityFreeAdded () > 0 || selectedOrders.get ( i ).getQuantityFreeCase () > 0 ) {
        				// TODO : Details of a sales order transaction detail
        				// Declare and initialize a transaction detail
						TransactionDetails transactionDetailp = new TransactionDetails ( null , // ID
								transactionHeader.getTransactionCode () , // TransactionCode
								  freeAddition , // LineID
								selectedOrders.get ( i ).getItem ().getItemCode () , // ItemCode
								TransactionDetailsUtils.Type.FREE, // OrderedType
								0.0 , // QuantityBig
								(double) selectedOrders.get ( i ).getQuantityFreeCase () , // QuantityMedium
								(double) selectedOrders.get ( i ).getQuantityFreeAdded () , // QuantitySmall
						 		(double) selectedOrders.get ( i ).getQuantityFreeAdded () + (double) selectedOrders.get ( i ).getQuantityFreeCase ()*selectedOrders.get ( i ).getItem().getUnitMediumSmall(), // BasicUnitQuantity
    							0.0 , // ApprovedQuantityBig
    							(double) selectedOrders.get ( i ).getQuantityFreeCase () , // ApprovedQuantityMedium
    							(double) selectedOrders.get ( i ).getQuantityFreeAdded () , // ApprovedQuantitySmall
    							(double) selectedOrders.get ( i ).getQuantityFreeAdded () + (double) selectedOrders.get ( i ).getQuantityFreeCase ()*selectedOrders.get ( i ).getItem().getUnitMediumSmall(),//(double) selectedOrders.get ( i ).getQuantityFreeAdded () , // ApprovedBasicUnitQuantity
    							0.0 , // MissedQuantityBig
    							0.0 , // MissedQuantityMedium
    							0.0 , // MissedQuantitySmall
    							0.0 , // MissedBasicUnitQuantity
								(double) selectedOrders.get ( i ).getPriceBig () , // PriceBig
								(double) selectedOrders.get ( i ).getPriceMedium () , // PriceMedium
								(double) selectedOrders.get ( i ).getPriceSmall () , // PriceSmall
								(double) selectedOrders.get ( i ).getPriceBig () , // UserPriceBig
								(double) selectedOrders.get ( i ).getPriceMedium () , // UserPriceMedium
								(double) selectedOrders.get ( i ).getPriceSmall () , // UserPriceSmall
								100.0 , // DiscountPercentage
								selectedOrders.get ( i ).getQuantityFreeAdded () * selectedOrders.get ( i ).getPriceSmall () +  (double) selectedOrders.get ( i ).getQuantityFreeCase ()*selectedOrders.get ( i ).getItem().getUnitMediumSmall()* selectedOrders.get ( i ).getPriceSmall (), // DiscountAmount
								0.0+ (freetaxe?selectedOrders.get ( i ).getQuantityFreeCase (   )
	    						*selectedOrders.get ( i ).getCaseTaxAmount()+ (double)  selectedOrders.get ( i ).getQuantityFreeAdded (   )
	    						*selectedOrders.get ( i ).getUnitTaxAmount():0)  , // TotalLineAmount
								selectedOrders.get ( i ).getSequence()+"" , // LineNote
								null , // ItemLot
								null,//selectedOrders.get ( i ).getReasonCode() , // ReasonCode
								selectedOrders.get ( i ).getTax () , // ItemTaxPercentage
								selectedOrders.get ( i ).getItem ().getItemName () , // ItemName
								selectedOrders.get ( i ).getItem ().getItemAltName () , // ItemAltName
								-2 , // ParentLineID
    							null , // ItemExpiryDate
    							null , // ItemAffectedStock
    							selectedOrders.get ( i ).getAvailability () , // IsInvoiceRelated
    							null , // IsCompanyRelated
    							null , 
    							selectedOrders.get ( i ).getCaseTaxAmount() , 
								selectedOrders.get ( i ).getUnitTaxAmount() , 
								 selectedOrders.get ( i ).getQuantityFreeCase (   )
	    						*selectedOrders.get ( i ).getCaseTaxAmount()+ (double)  selectedOrders.get ( i ).getQuantityFreeAdded (   )
	    						*selectedOrders.get ( i ).getUnitTaxAmount() , 
	    						selectedOrders.get ( i ).isMustStockList()?1:selectedOrders.get ( i ).isNewSKUList()?2:0,
	    		    				    
	    						now.getTime () ); // StampDate
						// Increase counter
						freeAddition++;
						// Insert the transaction detail in DB
						DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsDao ().insert ( transactionDetailp ); // StampDate
					} // End if
    				
    				
    				
    				
    				
    				
    				
    				// Otherwise check if the current item has free items provided by the user
//    				if ( selectedOrders.get ( i ).getQuantityFreeCase () > 0 ) {
//        				// TODO : Details of a sales order transaction detail
//        				// Declare and initialize a transaction detail
//						TransactionDetails transactionDetailp = new TransactionDetails ( null , // ID
//								transactionHeader.getTransactionCode () , // TransactionCode
//								i + freeAddition +1, // LineID
//								selectedOrders.get ( i ).getItem ().getItemCode () , // ItemCode
//								TransactionDetailsUtils.Type.FREE, // OrderedType
//								0.0 , // QuantityBig
//								(double) selectedOrders.get ( i ).getQuantityFreeCase (), // QuantityMedium
//								0.0 , // QuantitySmall
//								(double) selectedOrders.get ( i ).getQuantityFreeCase () * selectedOrders.get(i).getItem().getUnitMediumSmall() , // BasicUnitQuantity
//    							0.0 , // ApprovedQuantityBig
//    							(double) selectedOrders.get ( i ).getQuantityFreeCase () , // ApprovedQuantityMedium
//    							0.0 , // ApprovedQuantitySmall
//    							(double) selectedOrders.get ( i ).getQuantityFreeCase () , // ApprovedBasicUnitQuantity
//    							0.0 , // MissedQuantityBig
//    							0.0 , // MissedQuantityMedium
//    							0.0 , // MissedQuantitySmall
//    							0.0 , // MissedBasicUnitQuantity
//								(double) selectedOrders.get ( i ).getPriceBig () , // PriceBig
//								(double) selectedOrders.get ( i ).getPriceMedium () , // PriceMedium
//								(double) selectedOrders.get ( i ).getPriceSmall () , // PriceSmall
//								(double) selectedOrders.get ( i ).getPriceBig () , // UserPriceBig
//								(double) selectedOrders.get ( i ).getPriceMedium () , // UserPriceMedium
//								(double) selectedOrders.get ( i ).getPriceSmall () , // UserPriceSmall
//								100.0 , // DiscountPercentage
//								selectedOrders.get ( i ).getQuantityFreeCase () * selectedOrders.get ( i ).getPriceSmall () , // DiscountAmount
//								0.0 , // TotalLineAmount
//								null , // LineNote
//								null , // ItemLot
//								null , // ReasonCode
//								selectedOrders.get ( i ).getTax () , // ItemTaxPercentage
//								selectedOrders.get ( i ).getItem ().getItemName () , // ItemName
//								selectedOrders.get ( i ).getItem ().getItemAltName () , // ItemAltName
//								-2 , // ParentLineID
//    							null , // ItemExpiryDate
//    							null , // ItemAffectedStock
//    							null , // IsInvoiceRelated
//    							null , // IsCompanyRelated
//    							now.getTime () ); // StampDate
//						// Increase counter
//						freeAddition++;
//						// Insert the transaction detail in DB
//						DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsDao ().insert ( transactionDetailp ); // StampDate
//					} // End if
//    				
    			
    				
    				
    				
    				
				} 
				for ( i = 0 ; i < freeOrders.size () ; i ++ ) {
				if ( freeOrders.get ( i ).getFreeMedium () > 0 || freeOrders.get ( i ).getFreeSmall () > 0 ) {
    				// TODO : Details of a sales order transaction detail
    				// Declare and initialize a transaction detail
					TransactionDetails transactionDetailp = new TransactionDetails ( null , // ID
							transactionHeader.getTransactionCode () , // TransactionCode
						     freeAddition  , // LineID
							freeOrders.get ( i ).getItem ().getItemCode () , // ItemCode
							TransactionDetailsUtils.Type.FREE, // OrderedType
							0.0 , // QuantityBig
							(double) freeOrders.get ( i ).getFreeMedium()   , // QuantityMedium
							(double) freeOrders.get ( i ).getFreeSmall()  , // QuantitySmall
							(double) freeOrders.get ( i ).getFreeSmall () + (double) freeOrders.get ( i ).getFreeMedium ()*freeOrders.get ( i ).getItem().getUnitMediumSmall(), // BasicUnitQuantity
							0.0 , // ApprovedQuantityBig
							(double) freeOrders.get ( i ).getFreeMedium () , // ApprovedQuantityMedium
							(double) freeOrders.get ( i ).getFreeSmall () , // ApprovedQuantitySmall
							(double) freeOrders.get ( i ).getFreeSmall () + (double) freeOrders.get ( i ).getFreeMedium ()*freeOrders.get ( i ).getItem().getUnitMediumSmall(),//(double) selectedOrders.get ( i ).getQuantityFreeAdded () , // ApprovedBasicUnitQuantity
							0.0 , // MissedQuantityBig
							0.0 , // MissedQuantityMedium
							0.0 , // MissedQuantitySmall
							0.0 , // MissedBasicUnitQuantity
							(double) freeOrders.get ( i ).getPriceBig () , // PriceBig
							(double) freeOrders.get ( i ).getPriceMedium () , // PriceMedium
							(double) freeOrders.get ( i ).getPriceSmall () , // PriceSmall
							(double) freeOrders.get ( i ).getPriceBig () , // UserPriceBig
							(double) freeOrders.get ( i ).getPriceMedium () , // UserPriceMedium
							(double) freeOrders.get ( i ).getPriceSmall () , // UserPriceSmall
							100.0 , // DiscountPercentage
							freeOrders.get ( i ).getFreeSmall () * freeOrders.get ( i ).getPriceSmall () +  (double) freeOrders.get ( i ).getFreeMedium ()*freeOrders.get ( i ).getItem().getUnitMediumSmall()* freeOrders.get ( i ).getPriceMedium(), // DiscountAmount
							0.0+(freetaxe?freeOrders.get ( i ).getFreeMedium (   )
    						*freeOrders.get ( i ).getCaseTaxAmount()+ (double)  freeOrders.get ( i ).getFreeSmall() 
    						*freeOrders.get ( i ).getUnitTaxAmount() :0), // TotalLineAmount
							freeOrders.get ( i ).getSequence()+"" , // LineNote
							null , // ItemLot
							null , // ReasonCode
							freeOrders.get ( i ).getTax () , // ItemTaxPercentage
							freeOrders.get ( i ).getItem ().getItemName () , // ItemName
							freeOrders.get ( i ).getItem ().getItemAltName () , // ItemAltName
							-33 , // ParentLineID
							null , // ItemExpiryDate
							null , // ItemAffectedStock
							freeOrders.get ( i ).getAvailability () , // IsInvoiceRelated
							null , // IsCompanyRelated
							null , 
							freeOrders.get ( i ).getCaseTaxAmount() , 
							freeOrders.get ( i ).getUnitTaxAmount() , 
						(freetaxe?	freeOrders.get ( i ).getFreeMedium (   )
	    						*freeOrders.get ( i ).getCaseTaxAmount()+ (double)  freeOrders.get ( i ).getFreeSmall() 
	    						*freeOrders.get ( i ).getUnitTaxAmount():0)  , 
	    						freeOrders.get ( i ).isMustStockList()?1:freeOrders.get ( i ).isNewSKUList()?2:0,
	    		    				    
	    						now.getTime () ); // StampDate
					// Increase counter
					freeAddition++;
					// Insert the transaction detail in DB
					DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsDao ().insert ( transactionDetailp ); // StampDate
				} // End if
				
				}	 
				if(promotedOrders!=null)
					for ( i = 0 ; i < promotedOrders.size () ; i ++ ) {
					if ( promotedOrders.get ( i ).getBasketMedium () > 0 || promotedOrders.get ( i ).getBasketSmall () > 0 || promotedOrders.get ( i ).getBasketBig () > 0 ) {
	    				// TODO : Details of a sales order transaction detail
	    				// Declare and initialize a transaction detail
						TransactionDetails transactionDetailp = new TransactionDetails ( null , // ID
								transactionHeader.getTransactionCode () , // TransactionCode
								freeAddition , // LineID
								promotedOrders.get ( i ).getItem ().getItemCode () , // ItemCode
								TransactionDetailsUtils.Type.ORDER, // OrderedType
								0.0 , // QuantityBig
								(double) promotedOrders.get ( i ).getBasketMedium()   , // QuantityMedium
								(double) promotedOrders.get ( i ).getBasketSmall()  , // QuantitySmall
								(double) promotedOrders.get ( i ).getBasketSmall () + (double) promotedOrders.get ( i ).getBasketMedium ()*promotedOrders.get ( i ).getItem().getUnitMediumSmall(), // BasicUnitQuantity
								0.0 , // ApprovedQuantityBig
								(double) promotedOrders.get ( i ).getBasketMedium () , // ApprovedQuantityMedium
								(double) promotedOrders.get ( i ).getBasketSmall () , // ApprovedQuantitySmall
								(double) promotedOrders.get ( i ).getBasketSmall () + (double) promotedOrders.get ( i ).getBasketMedium ()*promotedOrders.get ( i ).getItem().getUnitMediumSmall(),//(double) selectedOrders.get ( i ).getQuantityFreeAdded () , // ApprovedBasicUnitQuantity
								(double) promotedOrders.get ( i ).getQuantityBasket() , // MissedQuantityBig
								0.0 , // MissedQuantityMedium
								0.0 , // MissedQuantitySmall
								0.0 , // MissedBasicUnitQuantity
								(double) promotedOrders.get ( i ).getPriceBig () , // PriceBig
								(double) promotedOrders.get ( i ).getPriceMedium () , // PriceMedium
								(double) promotedOrders.get ( i ).getPriceSmall () , // PriceSmall
								(double) promotedOrders.get ( i ).getBasketPriceBig() , // UserPriceBig
								(double) promotedOrders.get ( i ).getBasketPriceMedium () , // UserPriceMedium
								(double) promotedOrders.get ( i ).getBasketPriceSmall () , // UserPriceSmall
								(double) promotedOrders.get ( i ).getPromotionDiscountPercentageBasket(), // DiscountPercentage
							(	promotedOrders.get ( i ).getBasketSmall () * promotedOrders.get ( i ).getBasketPriceSmall ()
								+  (double) promotedOrders.get ( i ).getBasketMedium ()* 
								promotedOrders.get ( i ).getBasketPriceMedium ())*promotedOrders.get(i).getPromotionDiscountPercentageBasket()/100, // DiscountAmount
								(	promotedOrders.get ( i ).getBasketSmall () * promotedOrders.get ( i ).getBasketPriceSmall ()
										+  (double) promotedOrders.get ( i ).getBasketMedium () *
										promotedOrders.get ( i ).getBasketPriceMedium ())-	(	promotedOrders.get ( i ).getBasketSmall () * promotedOrders.get ( i ).getBasketPriceSmall ()
										+  (double) promotedOrders.get ( i ).getBasketMedium ()*  
										promotedOrders.get ( i ).getBasketPriceMedium ())*promotedOrders.get(i).getPromotionDiscountPercentageBasket()/100
										+	((freetaxe&&promotedOrders.get(i).getPromotionDiscountPercentageBasket()!=100)?
				    				    		(double)  promotedOrders.get ( i ).getBasketSmall ( )
				    				    *promotedOrders.get ( i ).getUnitTaxAmount():0 ), // TotalLineAmount
										promotedOrders.get ( i ).getSequence()+"" , // LineNote
										null , // ItemLot
										null, // ReasonCode
								promotedOrders.get ( i ).getTax () , // ItemTaxPercentage
								promotedOrders.get ( i ).getItem ().getItemName () , // ItemName
								promotedOrders.get ( i ).getPromotionID() , // ItemAltName
								-22 , // ParentLineID
								null , // ItemExpiryDate
								null , // ItemAffectedStock
								promotedOrders.get ( i ).getAvailability () , // IsInvoiceRelated
								null , // IsCompanyRelated
								null , 
								promotedOrders.get ( i ).getCaseTaxAmount() , 
								promotedOrders.get ( i ).getUnitTaxAmount() , 
								((freetaxe&&promotedOrders.get(i).getPromotionDiscountPercentageBasket()!=100)?
		    				    		(double)  promotedOrders.get ( i ).getBasketSmall ( )
		    				    *promotedOrders.get ( i ).getUnitTaxAmount():0 )
	    						*promotedOrders.get ( i ).getUnitTaxAmount() , 
								null,now.getTime () ); // StampDate
						// Increase counter
						freeAddition++;
						// Insert the transaction detail in DB
						DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsDao ().insert ( transactionDetailp ); // StampDate
					} // End if
					
					}
				 
				// Iterate over the transaction promotion details to add
				 
    			// Indicate that a sales order was successfully performed during this visit
    			CallAction.setCallActionStatus ( this , visit.getVisitID () , CallAction.ID.SALES_ORDER , true );	
    		 	
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
		
		// Determine if an error occurred
		if ( error ) {
			// Dismiss any displayed dialogs
			AppDialog.getInstance ().dismiss ();
			// Display baguette message
			Baguette.showText ( this , AppResources.getInstance ( this ).getString ( this , R.string.sales_order_save_failure_message ) , Baguette.BackgroundColor.RED );
    		// Consume event
    		return;
		} // End if
		
		// Indicate that the save was successful
		Vibration.vibrate ( SalesOrderDetailsActivity.this );
		
		// Check if an error occurred
		if ( ! error && transactionHeaderCode != null ) {
			// Retrieve a constant reference to the transaction code
			printingTransactionCode = transactionHeaderCode;
			// Set flag
			displayPrintingConfirmation = true;
			// Display printing confirmation
			displayPrintingConfirmation ();
		} // End if
		else {
			// Otherwise no errors occurred
	    	// Call this to set the result that your activity will return to its caller
			Intent intent = new Intent();
			intent.putExtra ( SalesOrderActivity.SAVE_SUCCESS , ! error );
	    	setResult ( RESULT_OK , intent );
	    	// Finish this activity (if allowed)
	    	if ( finish )
				// Finish this activity
	    		finish ();
		} // End else
    }
    
	/**
     * Determines if the current order price value exceeds the client credit limit.
     * 
     * @param clientcode	String hosting the client code.
     * @param companyCode	String hosting the company code.
     * @param currencyCode	String hosting the currency code.
     * @param orderPrice	The current order price.
     * @return	Boolean indicating if the current client credit limit has been exceeded or not.
     */
    private boolean exceedsCreditLimit ( final String clientCode , final String companyCode , final String currencyCode , final double orderPrice ) {
    	// Check if the current client has any credit limits
    	// Retrieve the total client credit limit amount and balance
    	Cursor cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().rawQuery ( 
    			"SELECT COALESCE ( SUM ( " + ClientCreditingsDao.Properties.CreditAmount.columnName + " ) , 0 ) " + ClientCreditingsDao.Properties.CreditAmount.columnName + " " +
    			"FROM " + ClientCreditingsDao.TABLENAME + " " +
    			"WHERE " + ClientCreditingsDao.Properties.ClientCode.columnName + " = ? " +
    			"AND " + ClientCreditingsDao.Properties.CompanyCode.columnName + " = ? " +
    			"AND " + ClientCreditingsDao.Properties.CurrencyCode.columnName + " = ? " ,
    			new String [] {
    					clientCode ,
    					companyCode ,
    					currencyCode
    			} );
    	// Move the cursor to the first raw
    	cursor.moveToFirst ();
    	// Retrieve the total credit and balance amounts
    	double totalCredit = cursor.getDouble ( cursor.getColumnIndex ( ClientCreditingsDao.Properties.CreditAmount.columnName ) );
    	// Clear cursor
    	cursor.close ();
    	cursor = null;
    	// Check if the total credit amount is valid
    	if ( totalCredit <= 0 )
    		// No / invalid credit limit
    		return false;
    	// Otherwise compute the client current credit
    	// In order to do that, compute the total credit balance, not processed orders, remaining dues , current order amount after deduction of the not processed collections
    	cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().rawQuery ( 
    			"SELECT " + orderPrice +
    			" + ( SELECT COALESCE ( SUM ( " + ClientCreditingsDao.Properties.CreditBalance.columnName + " ) , 0 ) " +
        			"FROM " + ClientCreditingsDao.TABLENAME + " " +
        			"WHERE " + ClientCreditingsDao.Properties.ClientCode.columnName + " = ? " +
        			"AND " + ClientCreditingsDao.Properties.CompanyCode.columnName + " = ? " +
        			"AND " + ClientCreditingsDao.Properties.CurrencyCode.columnName + " = ? )" +
    			" + ( SELECT COALESCE ( SUM ( " + ClientDuesDao.Properties.RemainingAmount.columnName + " ) , 0 ) " +
	    			"FROM " + ClientDuesDao.TABLENAME + " " +
	    			"WHERE " + ClientDuesDao.Properties.DueDate.columnName + " < " + Calendar.getInstance ().getTimeInMillis () + " )" +
	    		" + ( SELECT COALESCE ( SUM ( " + TransactionHeadersDao.Properties.TotalTaxAmount.columnName + " ) , 0 ) " +
	    			"FROM " + TransactionHeadersDao.TABLENAME + " " +
	    			"WHERE " + TransactionHeadersDao.Properties.TransactionType.columnName + " = ? " +
	    			"AND " + TransactionHeadersDao.Properties.TransactionStatus.columnName + " = ? " +
	    			"AND " + TransactionHeadersDao.Properties.IsProcessed.columnName + " = ? " + 
	    			"AND " + TransactionHeadersDao.Properties.ClientCode.columnName + " = ? " +
	    			"AND " + TransactionHeadersDao.Properties.CompanyCode.columnName + " = ? " +
	    			"AND " + TransactionHeadersDao.Properties.CurrencyCode.columnName + " = ? )" +
	    		" - ( SELECT COALESCE ( SUM ( " + CollectionHeadersDao.Properties.TotalAmount.columnName + " ) , 0 ) " +
	    			"FROM " + CollectionHeadersDao.TABLENAME + " " +
	    			"WHERE " + CollectionHeadersDao.Properties.ClientCode.columnName + " = ? " +
	    			"AND " + CollectionHeadersDao.Properties.CollectionStatus.columnName + " = ? " +
	    			"AND " + CollectionHeadersDao.Properties.CompanyCode.columnName + " = ? " +
	    			"AND " + CollectionHeadersDao.Properties.CurrencyCode.columnName + " = ? " +
	    			"AND " + CollectionHeadersDao.Properties.IsProcessed.columnName + " = ? )",
    			new String [] {
    					clientCode ,
    					companyCode ,
						currencyCode ,
						String.valueOf ( TransactionHeadersUtils.Type.SALES_ORDER ) ,
						String.valueOf ( StatusUtils.isAvailable () ) ,
						String.valueOf ( IsProcessedUtils.isNotSync () ) ,
						clientCode ,
						companyCode ,
						currencyCode ,
						clientCode ,
						String.valueOf ( StatusUtils.isAvailable () ) ,
						companyCode ,
						currencyCode ,
						String.valueOf ( IsProcessedUtils.isNotSync () )
    			} );
    	// Move the cursor to the first raw
    	cursor.moveToFirst ();
    	// Retrieve the total client current credit
    	double clientCredit = cursor.getDouble ( 0 );
    	// Clear cursor
    	cursor.close ();
    	cursor = null;
    	// Determine if the total credit amount is exceeded
    	if ( totalCredit > clientCredit )
    		// Indicate that the credit limit is NOT exceeded
    		return false;
    	// Otherwise indicate that the credit limit is exceeded
    	return true;
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
	 * Gets the sales order details adapter. The default implementation uses {@link me.SyncWise.Android.Modules.SalesOrder.SalesOrderDetailsAdapter SalesOrderDetailsAdapter}.<br>
	 * In order to used a custom adapter, override this method.<br>
	 * <b>Note : </b> This method should not return {@code NULL}.
	 * 
	 * @param layout	XML Layout (layout) resource id.
	 * @param orderItems	List of {@link me.SyncWise.Android.Modules.SalesOrder.Order Order} objects.
	 * @param itemsEnabled	Flag indicating if the items are enabled or not.
	 * @param usePicker	Flag indicating if the number picker should be enabled or not.
	 * @return	A sales order details adapter.
	 */
	protected ListAdapter getSalesOrderDetailsAdapter ( int layout , List < Order > orderItems , final boolean itemsEnabled , final boolean usePicker,int type ) {
		return new SalesOrderDetailsAdapter ( this , layout , orderItems , currency , itemsEnabled , usePicker ,type);
	}
	
	/**
	 * Indicates if there are valid sales orders (with non-zero quantities).<br>
	 * This method iterates over the list of orders and checks their quantities.
	 * 
	 * @return	Boolean indicating if there is at least one valid order.
	 */
	private boolean hasSalesOrders () {
		try {
			// Check if the orders list is valid
			if ( orders == null )
				// Invalid list
				return false;
			// Declare and initialize a list used to host the valid sales orders
			ArrayList < Order > salesOrder = new ArrayList < Order > ();
			// Iterate over all orders
			for ( Order order : orders )
				// Check if the current order has valid quantities
				if ( order.getQuantityBig () != 0 || order.getQuantityMedium () != 0 || order.getQuantitySmall () != 0 || order.getQuantityFree () != 0 || order.getAvailability() != null)
					// The order contains at least one quantity, add it to the sales order list
					salesOrder.add ( order );
			// Check if there is at least one valid sales order
			if ( salesOrder.isEmpty () )
				// No sales order
				return false;
			else
				// At least one order
				return true;
		} catch ( Exception exception ) {
			// No sales order
			return false;
		} // End of try-catch block
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
		if ( ! noteNew.equals ( transactionHeader.getInfo3() ) )
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
		return true;
//		// Map the transaction details to their item codes
//		Map < String , TransactionDetails > _transactionDetails = new HashMap < String , TransactionDetails > ();
//		Map < String , TransactionDetails > _promotedTransactionDetails = new HashMap < String , TransactionDetails > ();
//		// Iterate over all transaction details
//		for ( TransactionDetails transactionDetail : transactionDetails ) {
//			// Map the current transaction detail to its item code
//			if ( transactionDetail.getOrderedType ().equals ( TransactionDetailsUtils.Type.ORDER ) )
//				_transactionDetails.put ( transactionDetail.getItemCode () , transactionDetail );
//			else if ( transactionDetail.getOrderedType ().equals ( TransactionDetailsUtils.Type.FREE ) )
//				_promotedTransactionDetails.put ( transactionDetail.getItemCode () , transactionDetail );
//		}
//		// Declare and initialize a list used to host the valid sales orders
//		ArrayList < Order > salesOrder = new ArrayList < Order > ();
//		ArrayList < Order > freeOfCharge = new ArrayList < Order > ();
//		// Iterate over all orders
//		for ( Order order : orders ) {
//			// Check if the current order has valid quantities
//			if ( order.getQuantityBig () != 0 || order.getQuantityMedium () != 0 || order.getQuantitySmall () != 0 || order.getAvailability() != null)
//				// The order contains at least one quantity, add it to the sales order list
//				salesOrder.add ( order );
//			// Check if the current order has FOC quantities
//			if ( order.getQuantityFree () != 0 || order.getQuantityFreeCase() != 0)
//				// The order contains at least one FOC quantity, add it to the FOC list
//				freeOfCharge.add ( order );			
//		} // End for each
//		// Check if both sizes differ
//		if ( salesOrder.size () != _transactionDetails.size () || freeOfCharge.size () != _promotedTransactionDetails.size () )
//			// There is at least one modification
//			return true;
//
//		// Iterate over the sales orders
//		for ( Order order : salesOrder ) {
//			// Determine the corresponding transaction detail
//			if ( _transactionDetails.containsKey ( order.getItem ().getItemCode () ) ) {
//				// Retrieve a reference to the transaction detail
//				TransactionDetails transactionDetail = _transactionDetails.get ( order.getItem ().getItemCode () );
//				/*
//				 * Compare the following fields :
//				 * - Quantity big (if NOT FREE)
//				 * - Quantity medium (if NOT FREE)
//				 * - Quantity small
//				 * - Price Big Default
//				 * - Price Medium Default
//				 * - Price Small Default
//				 * - Price Big Used
//				 * - Price Medium Used
//				 * - Price Small Used
//				 * - Tax Percentage
//				 */
//				if (  ! transactionDetail.getQuantityBig ().equals ( (double) order.getQuantityBig () ) 
//						|| ! transactionDetail.getQuantityMedium ().equals ( (double) order.getQuantityMedium () ) 
//						|| ! transactionDetail.getQuantitySmall ().equals ( (double)  order.getQuantitySmall ()  )
//						|| ! transactionDetail.getPriceBig ().equals ( order.getPriceBig () )
//						|| ! transactionDetail.getPriceMedium ().equals ( order.getPriceMedium () )
//						|| ! transactionDetail.getPriceSmall ().equals ( order.getPriceSmall () )
//						|| ! transactionDetail.getUserPriceBig ().equals ( order.getPriceBig () )
//						|| ! transactionDetail.getUserPriceMedium ().equals ( order.getPriceMedium () )
//						|| ! transactionDetail.getUserPriceSmall ().equals ( order.getPriceSmall () )
//						|| ! transactionDetail.getItemTaxPercentage ().equals ( order.getTax () ))
//					// There is at least one modification
//					return true;
//			} // End if
//			else
//				// There is at least one modification
//				return true;
//		} // End for each
//		
//		// Iterate over the free of charge items
//		for ( Order order : freeOfCharge ) {
//			// Determine the corresponding transaction detail
//			if ( _promotedTransactionDetails.containsKey ( order.getItem ().getItemCode () ) ) {
//				// Retrieve a reference to the transaction detail
//				TransactionDetails transactionDetail = _promotedTransactionDetails.get ( order.getItem ().getItemCode () );
//				/*
//				 * Compare the following fields :
//				 * - Quantity big (if NOT FREE)
//				 * - Quantity medium (if NOT FREE)
//				 * - Quantity small
//				 * - Price Big Default
//				 * - Price Medium Default
//				 * - Price Small Default
//				 * - Price Big Used
//				 * - Price Medium Used
//				 * - Price Small Used
//				 * - Tax Percentage
//				 */
//				if (  ! transactionDetail.getQuantitySmall ().equals ( (double) ( order.getQuantityFree () ) )
//						|| ! transactionDetail.getPriceBig ().equals ( order.getPriceBig () )
//						|| ! transactionDetail.getPriceMedium ().equals ( order.getPriceMedium () )
//						|| ! transactionDetail.getPriceSmall ().equals ( order.getPriceSmall () )
//						|| ! transactionDetail.getUserPriceBig ().equals ( order.getPriceBig () )
//						|| ! transactionDetail.getUserPriceMedium ().equals ( order.getPriceMedium () )
//						|| ! transactionDetail.getUserPriceSmall ().equals ( order.getPriceSmall () )
//						|| ! transactionDetail.getItemTaxPercentage ().equals ( order.getTax () ) )
//					// There is at least one modification
//					return true;
//				} // End if
//				else
//					// There is at least one modification
//					return true;
//		} // End for each
//		// Otherwise there are no modifications
//		return false;
	}
	
	/**
	 * Filters the {@link #orders} list based on the indicated {@link #searchQuery} string and the {@link #selectedDivisionsCodes} list.<br>
	 * <b>NOTE : </b> This method does not perform any kind of integrity check what so ever !
	 * 
	 * @return	List of {@link me.SyncWise.Android.Modules.SalesOrder.Order Order} objects filtered according to the search and divisions filter.
	 */
	@SuppressLint("DefaultLocale") 
	protected ArrayList < Order > filter () {
		// Declare and initialize a new orders list
		ArrayList < Order > _orders = new ArrayList < Order > ();
		// Iterate over all the orders
		for ( Order order : orders ) {
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
				SalesOrderDetailsActivity.this.warehouseQuantities = itemsQuantities;
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
				Baguette.showText ( SalesOrderDetailsActivity.this ,
						AppResources.getInstance ( SalesOrderDetailsActivity.this ).getString ( SalesOrderDetailsActivity.this , R.string.warehouse_quantities_updated_message ) ,
						Baguette.BackgroundColor.BLUE );
				
				// Refresh the list
				if ( getListAdapter () instanceof SalesOrderDetailsAdapter )
					( (SalesOrderDetailsAdapter) getListAdapter () ).notifyDataSetChanged ();
				else if ( getListAdapter () instanceof MultipleListAdapter )
					( (MultipleListAdapter) getListAdapter () ).notifyDataSetChanged ();
			} catch ( Exception exception ) {
				// Do nothing
			} // End of try-catch block
		}
		
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
			List < WarehouseQuantities > _warehouseQuantities = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getWarehouseQuantitiesDao ().loadAll ();
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
	 * AsyncTask helper class used to populate the sales order items list with the appropriate items / orders.
	 * 
	 * @author ahmad
	 *
	 */
	private class PopulateList extends AsyncTask < Void , Void , ArrayList < Order > > {
		
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
		protected ArrayList < Order > doInBackground ( Void ... params ) {
			try {
				HashMap < String , Order > mappedInvoices = new HashMap < String , Order > ();
				
				// Declare and initialize a cursor object used to retrieve data sets from query results
				Cursor cursor = null;
				// Declare the SQL string and selection arguments array of strings used to to query DB
				String SQL = null;
				String selectionArguments [] = null;
				// Retrieve the user, company, client and division codes
				String userCode = DatabaseUtils.getCurrentUserCode ( SalesOrderDetailsActivity.this );
				String companyCode = DatabaseUtils.getCurrentCompanyCode ( SalesOrderDetailsActivity.this );
				String clientCode = ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClient ().getClientCode ();
				String divisionCode = ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClientDivision ().getDivisionCode ();
				
				
				// Retrieve the client item history
				List < ClientSellingSuggestion > clientSellingSuggestions = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getClientSellingSuggestionDao ().queryBuilder ()
						.where ( ClientSellingSuggestionDao.Properties.ClientCode.eq ( clientCode ) ,
								ClientSellingSuggestionDao.Properties.DivisionCode.eq ( DatabaseUtils.getCurrentDivisionCode ( SalesOrderDetailsActivity.this ) ) ,
								ClientSellingSuggestionDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( SalesOrderDetailsActivity.this ) ) ).list ();
				// Get the item codes of the item history
				HashMap < String , ClientSellingSuggestion > _clientSellingSuggestions = new HashMap < String , ClientSellingSuggestion > ();
				for ( ClientSellingSuggestion sellingSuggestion : clientSellingSuggestions )
					_clientSellingSuggestions.put ( sellingSuggestion.getItemCode () , sellingSuggestion );
				
				// Initialize promotions variables
				headerID = new ArrayList < Integer > ();
				promotionHeaders = new SparseArray < PromotionHeaders > ();
				_promotions = new HashMap<String, ArrayList< PromotionDetails > > ();
				// Check if the promotions are enabled in the sales order
				if ( PermissionsUtils.getEnableSalesOrderPromotions ( SalesOrderDetailsActivity.this , userCode , companyCode ) ) {
					// Query the promotions
					SQL = " SELECT DISTINCT * FROM "+ PromotionAssignmentsDao.TABLENAME + " WHERE "+
							PromotionAssignmentsDao.Properties.AssignmentType.columnName + "= ? OR "+
							PromotionAssignmentsDao.Properties.AssignmentType.columnName + "= ? OR ("+
							PromotionAssignmentsDao.Properties.AssignmentType.columnName + "= ? AND "+
							PromotionAssignmentsDao.Properties.AssignmentCode.columnName + "= ?) OR ("+
							PromotionAssignmentsDao.Properties.AssignmentType.columnName + "= ? AND "+
							PromotionAssignmentsDao.Properties.AssignmentCode.columnName + " in (" +
							" SELECT " + ClientPropertiesDao.Properties.ClientPropertyValueCode.columnName +
							" FROM " + ClientPropertiesDao.TABLENAME + " WHERE " + ClientPropertiesDao.Properties.ClientCode.columnName + 
							"= ? ) )"; 
					selectionArguments = new String [] {
							String.valueOf ( PromotionAssignmentsUtils.Type.USER ) ,
							String.valueOf ( PromotionAssignmentsUtils.Type.GROUP_USERS ) ,
							String.valueOf ( PromotionAssignmentsUtils.Type.CLIENT ) ,
							clientCode ,
							String.valueOf ( PromotionAssignmentsUtils.Type.CLIENT_PROPERTIES ) ,
							clientCode };
					cursor = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getDatabase ().rawQuery  ( SQL , selectionArguments );
					if ( cursor.moveToFirst () ) {
						do{
							headerID.add ( cursor.getInt ( cursor.getColumnIndex(PromotionAssignmentsDao.Properties.PromotionID.columnName ) ) );
						} while ( cursor.moveToNext () );
						cursor.close ();
					} // End if
					// Clear cursor
					cursor.close ();
					cursor = null;
					// Retrieve the promotion headers
					List < PromotionHeaders > _promotionHeaders = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getPromotionHeadersDao ().queryBuilder ()
							.where ( PromotionHeadersDao.Properties.PromotionID.in ( headerID ) ).list ();
					// Iterate over all promotion headers in order to map them to their promotion IDs
					for ( PromotionHeaders promotionHeader : _promotionHeaders ) {
						// Map the current promotion header to its ID
						promotionHeaders.put ( promotionHeader.getPromotionID () , promotionHeader );
						if ( promotionHeader.getPromotionType() != PromotionUtils.Type.STRUCTURAL_PROMOTION 
								&& promotionHeader.getPromotionType() != PromotionUtils.Type.LIMITED_PROMOTION ) {
							List < PromotionDetails > promotions = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getPromotionDetailsDao ()
								.queryBuilder ().where ( PromotionDetailsDao.Properties.PromotionID.eq ( promotionHeader.getPromotionID () ) ).list ();
							for ( PromotionDetails promo : promotions ) {
								if (_promotions.containsKey(promo.getOrderedItemCode () ) )
									 _promotions.get(promo.getOrderedItemCode () ).add ( promo );
								else
									_promotions.put ( promo.getOrderedItemCode () , new ArrayList < PromotionDetails > () );
							} // End for each
						} // End if
					} // End for each
				} // End if
				
				
				
				
				
				
				
				//ahmad
				// Initialize promotions variables
			    	headerIDF = new ArrayList < Integer > ();
			    	promotionHeadersF = new SparseArray < PromotionHeaders > ();
				_promotionsF = new HashMap<String, ArrayList< PromotionDetails > > ();
				// Check if the promotions are enabled in the sales order
			 	if ( PermissionsUtils.getEnableSalesOrderPromotions ( SalesOrderDetailsActivity.this , userCode , companyCode ) ) {
					// Query the promotions
					SQL = " SELECT DISTINCT * FROM "+ PromotionAssignmentsDao.TABLENAME  + " WHERE ("+
				 	PromotionAssignmentsDao.Properties.AssignmentType.columnName + "= ? AND "+
				 	PromotionAssignmentsDao.Properties.AssignmentCode.columnName + "= ?)  OR ("+
				 	PromotionAssignmentsDao.Properties.AssignmentType.columnName + "= ? AND "+
				 	PromotionAssignmentsDao.Properties.AssignmentCode.columnName + "= ?) ";
					selectionArguments = new String [] {
							String.valueOf ( PromotionAssignmentsUtils.Type.USER ) ,
						    userCode,
							String.valueOf ( PromotionAssignmentsUtils.Type.CLIENT ) ,
							clientCode ,
							 };
				 
					cursor = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getDatabase ().rawQuery  ( SQL , selectionArguments );
					if ( cursor.moveToFirst () ) {
						do{
							headerIDF.add ( cursor.getInt ( cursor.getColumnIndex(PromotionAssignmentsDao.Properties.PromotionID.columnName ) ) );
						} while ( cursor.moveToNext () );
						cursor.close ();
					} // End if
					// Clear cursor
					cursor.close ();
					cursor = null;
					// Retrieve the promotion headers
					List < PromotionHeaders > _promotionHeadersF = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getPromotionHeadersDao ().queryBuilder ()
							.where ( PromotionHeadersDao.Properties.PromotionID.in ( headerIDF ) ).list ();
					// Iterate over all promotion headers in order to map them to their promotion IDs
					for ( PromotionHeaders promotionHeader : _promotionHeadersF ) {
						// Map the current promotion header to its ID
						promotionHeadersF.put ( promotionHeader.getPromotionID () , promotionHeader );
						if ( promotionHeader.getPromotionType() == PromotionUtils.Type.LIMITED_PROMOTION ) {
							List < PromotionDetails > promotionsF = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getPromotionDetailsDao ()
								.queryBuilder ().where ( PromotionDetailsDao.Properties.PromotionID.eq ( promotionHeader.getPromotionID () ) ).list ();
							for ( PromotionDetails promo : promotionsF ) {
								if ( _promotionsF.containsKey( promo.getOrderedItemCode () ) )
									 _promotionsF.get( promo.getOrderedItemCode () ).add ( promo );
								else
									{
									_promotionsF.put ( promo.getOrderedItemCode () , new ArrayList < PromotionDetails > () );
									_promotionsF.get( promo.getOrderedItemCode () ).add ( promo );
									}
						 } // End for each
						} // End if
					} // End for each
				 } // End if
				
				
			 	//String priceListff=(String)getIntent ().getStringExtra( PriceList ) ;
			 	 if( ClientCard.isCreditClient ((  (Call)getIntent ().getSerializableExtra ( CALL ) ).getClient () )   ){
			 	//NEW promotion pricelist:
				headerIDPricelist = new ArrayList < Integer > ();
		    	promotionHeadersPricelist = new SparseArray < PromotionHeaders > ();
		    	_promotionsPricelist = new HashMap<String, ArrayList< PromotionDetails > > ();
			 // Query the promotions
				SQL = " SELECT DISTINCT * FROM "+ PromotionAssignmentsDao.TABLENAME  + " WHERE ("+
			 	PromotionAssignmentsDao.Properties.AssignmentType.columnName + "= ? AND "+
			 	PromotionAssignmentsDao.Properties.AssignmentCode.columnName + "= ?)  OR ("+
			 	PromotionAssignmentsDao.Properties.AssignmentType.columnName + "= ? AND "+
			 	PromotionAssignmentsDao.Properties.AssignmentCode.columnName + "= ?) ";
				selectionArguments = new String [] {
						String.valueOf ( PromotionAssignmentsUtils.Type.USER ) ,
					    userCode,
						String.valueOf ( PromotionAssignmentsUtils.Type.CLIENT ) ,
						clientCode ,
						 };
			 
				cursor = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getDatabase ().rawQuery  ( SQL , selectionArguments );
				if ( cursor.moveToFirst () ) {
					do{
						headerIDPricelist.add ( cursor.getInt ( cursor.getColumnIndex(PromotionAssignmentsDao.Properties.PromotionID.columnName ) ) );
					} while ( cursor.moveToNext () );
					cursor.close ();
				} // End if
				// Clear cursor
				cursor.close ();
				cursor = null;
				// Retrieve the promotion headers
				List < PromotionHeaders > _promotionHeadersPriceList = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getPromotionHeadersDao ().queryBuilder ()
						.where ( PromotionHeadersDao.Properties.PromotionID.in ( headerIDPricelist ) ).list ();
				// Iterate over all promotion headers in order to map them to their promotion IDs
				for ( PromotionHeaders promotionHeader : _promotionHeadersPriceList ) {
					// Map the current promotion header to its ID
					promotionHeadersPricelist.put ( promotionHeader.getPromotionID () , promotionHeader );
					if ( promotionHeader.getPromotionType() == PromotionUtils.Type.PRICELIST_PROMOTION ) {
						List < PromotionDetails > promotionsPriceList = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getPromotionDetailsDao ()
							.queryBuilder ().where ( PromotionDetailsDao.Properties.PromotionID.eq ( promotionHeader.getPromotionID () ) ).list ();
						for ( PromotionDetails promo : promotionsPriceList ) {
							if ( _promotionsPricelist.containsKey( promo.getOrderedItemCode () ) )
								 _promotionsPricelist.get( promo.getOrderedItemCode () ).add ( promo );
							else
								{
								_promotionsPricelist.put ( promo.getOrderedItemCode () , new ArrayList < PromotionDetails > () );
								_promotionsPricelist.get( promo.getOrderedItemCode () ).add ( promo );
								}
					 } // End for each
					} // End if
				} // End for each
	  
			 	
			 	 }
			 	
			 	
			 	
			 	

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
					cursor = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getDatabase ().rawQuery ( SQL , selectionArguments );
					// Move the cursor to the first raw
					if ( cursor.moveToFirst () ) {
						do {
							// Retrieve the current currency
							currency = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getCurrenciesDao ().readEntity ( cursor , 0 );
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
				
				// Check if the selected order item code index list is valid
				if ( selectedOrdersIndex == null )
					// Initialize the list
					selectedOrdersIndex = new ArrayList < String > ();
				HashMap < String , TransactionDetailsMissedMSL > _transactionDetailsMissedMSL = new HashMap < String , TransactionDetailsMissedMSL > ();
				  List < TransactionDetailsMissedMSL	> transactionDetailsMissedMSL = null ;
				// Determine if there is an available transaction header code
				if ( getIntent ().getStringExtra ( TRANSACTION_HEADER_CODE ) != null && transactionHeader == null ) {
					// Try to retrieve the transaction header with the specified transaction code
					transactionHeader = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getTransactionHeadersDao ().queryBuilder ()
							.where ( TransactionHeadersDao.Properties.TransactionCode.eq ( getIntent ().getStringExtra ( TRANSACTION_HEADER_CODE ) ) ).unique ();
					// Check if the transaction header is valid
					if ( transactionHeader != null ) {
						// Refresh object
						DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getTransactionHeadersDao ().refresh ( transactionHeader );
						// Set the sales order note
						note = transactionHeader.getNotes ();
						noteNew=transactionHeader.getInfo3();
						// Set the delivery date
						Calendar deliveryDate = Calendar.getInstance ();
						deliveryDate.setTimeInMillis ( transactionHeader.getDeliveryDate ().getTime () );
						selectedYear = deliveryDate.get ( Calendar.YEAR );
						selectedMonth = deliveryDate.get ( Calendar.MONTH );
						selectedDay = deliveryDate.get ( Calendar.DATE );
						// Initialize the transaction details list
						transactionDetails = new ArrayList < TransactionDetails > ();
						// Retrieve the appropriate transaction details
						transactionDetails.addAll ( DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getTransactionDetailsDao ().queryBuilder ()
							.where ( TransactionDetailsDao.Properties.TransactionCode.eq ( getIntent ().getStringExtra ( TRANSACTION_HEADER_CODE ) ) )
							.orderAsc ( TransactionDetailsDao.Properties.LineID ).list () );
						// Initialize the transaction  promotion details list
						transactionPromotionDetails = new ArrayList < TransactionPromotionDetails > ();
						
						 transactionDetailsMissedMSL = new ArrayList < TransactionDetailsMissedMSL > ();
						 transactionDetailsMissedMSL.addAll( DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getTransactionDetailsMissedMSLDao ().queryBuilder ()
							.where ( TransactionDetailsMissedMSLDao.Properties.TransactionCode.eq ( getIntent ().getStringExtra ( TRANSACTION_HEADER_CODE ) ) )
							.orderAsc ( TransactionDetailsMissedMSLDao.Properties.LineID ).list ());
						 if(transactionDetailsMissedMSL != null)
						 for ( TransactionDetailsMissedMSL transactionDetail : transactionDetailsMissedMSL ){
							 _transactionDetailsMissedMSL.put(transactionDetail.getItemCode(), transactionDetail);
							 
						 }
						 // Iterate over all transaction details
						for ( TransactionDetails transactionDetail : transactionDetails )
							// Refresh object
							DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getTransactionDetailsDao ().refresh ( transactionDetail );
						
						// Retrieve the appropriate currency
						currency = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getCurrenciesDao ().queryBuilder ()
							.where ( CurrenciesDao.Properties.CurrencyCode.eq ( transactionHeader.getCurrencyCode () ) ).unique ();
					} // End if

				} // End if
				Calendar calendar = Calendar.getInstance ();
				// Retrieve the start of the day
				calendar.set ( Calendar.HOUR_OF_DAY , 0 );
				calendar.set ( Calendar.MINUTE , 0 );
				calendar.set ( Calendar.SECOND , 0 );
				calendar.set ( Calendar.MILLISECOND , 0 );
				long startDay = calendar.getTimeInMillis ();
				if ( instantPromotions == null ) {
					// Initialize the list
					instantPromotions = new HashMap < String , ArrayList<PromotionDetails> > ();
					// Query the instant promotions
					SQL = "SELECT DISTINCT PD.* FROM PromotionDetails PD INNER JOIN PromotionHeaders PH ON PH.PromotionID = PD.PromotionID INNER JOIN PromotionAssignments PA " +
							"ON PD.PromotionID = PA.PromotionID AND CompanyCode = ? " +
							"AND ( AssignmentType IN ( 1 , 2 ) OR ( AssignmentType = 3 AND AssignmentCode = ? ) OR ( AssignmentType = 4 AND AssignmentCode IN ( " +
							"SELECT ClientPropertyValueCode FROM ClientProperties WHERE ClientCode = ? ) ) ) " +
							"WHERE PH.PromotionType = ? AND PH.StartDate IS NOT NULL AND PH.StartDate <= ? AND PH.EndDate IS NOT NULL AND PH.EndDate >= ? ";
					selectionArguments = new String [] { companyCode , clientCode , clientCode , String.valueOf ( PromotionUtils.Type.INSTANT_PROMOTION ) , String.valueOf ( startDay ) , String.valueOf ( startDay ) };
					cursor = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getDatabase ().rawQuery ( SQL , selectionArguments );
					// Move the cursor to the first raw
					if ( cursor.moveToFirst () ) {
						do {
							// Retrieve the current instant promotion
							PromotionDetails promotionDetail = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getPromotionDetailsDao ().readEntity ( cursor , 0 );
							ArrayList < PromotionDetails > list = null;
							if ( instantPromotions.containsKey ( promotionDetail.getOrderedItemCode () ) )
								list = instantPromotions.get ( promotionDetail.getOrderedItemCode () );
							else {
								list = new ArrayList < PromotionDetails > ();
								instantPromotions.put ( promotionDetail.getOrderedItemCode () , list );
							}
							list.add ( promotionDetail );
						} while ( cursor.moveToNext () );
					} // End if
					// Clear cursor
					cursor.close ();
					cursor = null;
				} // End if
				// Retrieve all divisions
				List < Divisions > allDivisions = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getDivisionsDao ().queryBuilder ()
						.where ( DivisionsDao.Properties.CompanyCode.eq ( companyCode ) ).list ();
				// Retrieve the divisions linked to the user
				List < Divisions > directUserDivisions = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getDivisionsDao ()
						.queryRaw ( " WHERE CompanyCode = ? AND DivisionCode IN ( SELECT DivisionCode FROM UserDivisions WHERE CompanyCode = ? ) " ,
								new String [] { companyCode , companyCode } );
				allDivisions.removeAll ( directUserDivisions );
				// Retrieve the user children division
				List < Divisions > allUserDivisions = DivisionsUtils.getChildren ( directUserDivisions , allDivisions );
				// Add the direct user divisions to the main list
				allUserDivisions.addAll ( directUserDivisions );
				
				  priceListf=(String)getIntent ().getStringExtra( PriceList ) ;
				List < String > codes = new ArrayList < String > ();
				if(transactionHeader   != null){
					priceListf=transactionHeader.getInfo4();
				}
			// Initialize the map
			HashMap < String , Prices > itemPrices = new HashMap < String , Prices > ();
		    if(priceListf == null){
			
		    	if(PermissionsUtils.getPriceListFromBO(SalesOrderDetailsActivity.this , userCode , companyCode )){
		    		String sou="2";
					// Compute the SQL query
					SQL = "SELECT P.* , CPL." + ClientPriceListsDao.Properties.Priority.columnName + " ClientPriceListPriority " +
							" FROM " + PricesDao.TABLENAME + " P INNER JOIN " + ClientPriceListsDao.TABLENAME + " CPL " +
							"  ON P." + PricesDao.Properties.PriceListCode.columnName + " = CPL." + ClientPriceListsDao.Properties.PriceListCode.columnName + " " +
							" AND CPL." + ClientPriceListsDao.Properties.ClientCode.columnName + " = ? " +
							" INNER JOIN " +  PriceListsDao.TABLENAME + " PR " +
							" ON PR." + PriceListsDao.Properties.PriceListCode.columnName + " = CPL." + ClientPriceListsDao.Properties.PriceListCode.columnName +" "+
							" AND PR." + PriceListsDao.Properties.PriceListSource.columnName + " = ? " +
							" WHERE P." + PricesDao.Properties.CurrencyCode.columnName + " = ? " +
							" ORDER BY P." + PricesDao.Properties.ItemCode.columnName + " , ClientPriceListPriority";
					// Compute the selection arguments
					selectionArguments = new String [] { clientCode,sou , currency.getCurrencyCode () };
			    	
			    	
				
					 
					// Query DB in order to retrieve the prices
					cursor = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getDatabase ().rawQuery ( SQL , selectionArguments );
					// Move the cursor to the first raw
					if ( cursor.moveToFirst () ) {
						do {
							// Retrieve the current price
							Prices price = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getPricesDao ().readEntity ( cursor , 0 );
							// Check if the current item price is previously mapped
							if ( ! itemPrices.containsKey ( price.getItemCode () ) )
								// Map the item price
								itemPrices.put ( price.getItemCode () , price );
						} while ( cursor.moveToNext () );
					} // End if
					// Clear cursor
					cursor.close ();
					cursor = null;
					
					if(itemPrices!=null && itemPrices.isEmpty()){
						
						  sou="1";
						// Compute the SQL query
						SQL = "SELECT P.* , CPL." + ClientPriceListsDao.Properties.Priority.columnName + " ClientPriceListPriority " +
								" FROM " + PricesDao.TABLENAME + " P INNER JOIN " + ClientPriceListsDao.TABLENAME + " CPL " +
								"  ON P." + PricesDao.Properties.PriceListCode.columnName + " = CPL." + ClientPriceListsDao.Properties.PriceListCode.columnName + " " +
								" AND CPL." + ClientPriceListsDao.Properties.ClientCode.columnName + " = ? " +
								" INNER JOIN " +  PriceListsDao.TABLENAME + " PR " +
								" ON PR." + PriceListsDao.Properties.PriceListCode.columnName + " = CPL." + ClientPriceListsDao.Properties.PriceListCode.columnName +" "+
								" AND PR." + PriceListsDao.Properties.PriceListSource.columnName + " = ? " +
								" WHERE P." + PricesDao.Properties.CurrencyCode.columnName + " = ? " +
								" ORDER BY P." + PricesDao.Properties.ItemCode.columnName + " , ClientPriceListPriority";
						// Compute the selection arguments
						selectionArguments = new String [] { clientCode,sou , currency.getCurrencyCode () };
				    	
				    	
					
						 
						// Query DB in order to retrieve the prices
						cursor = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getDatabase ().rawQuery ( SQL , selectionArguments );
						// Move the cursor to the first raw
						if ( cursor.moveToFirst () ) {
							do {
								// Retrieve the current price
								Prices price = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getPricesDao ().readEntity ( cursor , 0 );
								// Check if the current item price is previously mapped
								if ( ! itemPrices.containsKey ( price.getItemCode () ) )
									// Map the item price
									itemPrices.put ( price.getItemCode () , price );
							} while ( cursor.moveToNext () );
						} // End if
						// Clear cursor
						cursor.close ();
						cursor = null;
						
						
						
					}
		    	}
		    	else{
		    	
				String sou="1";
				// Compute the SQL query
				SQL = "SELECT P.* , CPL." + ClientPriceListsDao.Properties.Priority.columnName + " ClientPriceListPriority " +
						" FROM " + PricesDao.TABLENAME + " P INNER JOIN " + ClientPriceListsDao.TABLENAME + " CPL " +
						"  ON P." + PricesDao.Properties.PriceListCode.columnName + " = CPL." + ClientPriceListsDao.Properties.PriceListCode.columnName + " " +
						" AND CPL." + ClientPriceListsDao.Properties.ClientCode.columnName + " = ? " +
						" INNER JOIN " +  PriceListsDao.TABLENAME + " PR " +
						" ON PR." + PriceListsDao.Properties.PriceListCode.columnName + " = CPL." + ClientPriceListsDao.Properties.PriceListCode.columnName +" "+
						" AND PR." + PriceListsDao.Properties.PriceListSource.columnName + " = ? " +
						" WHERE P." + PricesDao.Properties.CurrencyCode.columnName + " = ? " +
						" ORDER BY P." + PricesDao.Properties.ItemCode.columnName + " , ClientPriceListPriority";
				// Compute the selection arguments
				selectionArguments = new String [] { clientCode,sou , currency.getCurrencyCode () };
		    	
		    	
			
				 
				// Query DB in order to retrieve the prices
				cursor = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getDatabase ().rawQuery ( SQL , selectionArguments );
				// Move the cursor to the first raw
				if ( cursor.moveToFirst () ) {
					do {
						// Retrieve the current price
						Prices price = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getPricesDao ().readEntity ( cursor , 0 );
						// Check if the current item price is previously mapped
						if ( ! itemPrices.containsKey ( price.getItemCode () ) )
							// Map the item price
							itemPrices.put ( price.getItemCode () , price );
					} while ( cursor.moveToNext () );
				} // End if
				// Clear cursor
				cursor.close ();
				cursor = null;
		    	}
		    }
		    else{
		    	String[] parts = priceListf.split(",");
		    	for(int i=0;i<parts.length;i++)
		    		codes.add( parts[i] );
		    List<Prices> pricesList=	DatabaseUtils.getInstance(SalesOrderDetailsActivity.this).getDaoSession().getPricesDao().queryBuilder ().where
		    	(  PricesDao.Properties.PriceListCode.in( codes ) ).list();
		     
		    
						for(Prices price:pricesList){
		    	 		if ( ! itemPrices.containsKey ( price.getItemCode () ) )
							// Map the item price
							itemPrices.put ( price.getItemCode () , price );
					 
				}  
		    }
				// Check if the divisions list is valid
				if ( divisions == null ) {
					// Initialize the divisions list
					divisions = new ArrayList < Divisions > ();
					// Display all the user divisions
					SalesOrderDetailsActivity.this.divisions.addAll ( allUserDivisions );
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
					orders = (ArrayList < Order >) ActivityInstance.readDataGson ( SalesOrderDetailsActivity.this , SalesOrderDetailsActivity.class.getName () , ORDERS , new TypeToken < ArrayList < Order > > () {}.getType () );
					// Initialize the list used to host the selected sales orders
					selectedOrders = new ArrayList < Order > ();
					// Iterate over all orders
					for ( Order order : orders )
		    			// Check if the current order has valid quantities
		    			if ( order.getQuantityBig () != 0 || order.getQuantityMedium () != 0 || order.getQuantitySmall () != 0  || order.getQuantityFree () !=0 || order.getAvailability() != null)
		    				// The order contains at least one quantity, add it to the sales order list
		    				selectedOrders.add ( order );
				} // End if
				else {
					// Retrieve the client item divisions
					List < ItemDivisions > itemDivisions = EntityUtils.queryByGroup ( selectedDivisionsCodes ,
							DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getItemDivisionsDao () ,
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
							DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getItemsDao () ,
							ItemsDao.Properties.ItemCode );
					
					// Retrieve the item barcodes
					List < ItemBarcodes > itemBarcodes = EntityUtils.queryByGroup ( itemCodes ,
							DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getItemBarcodesDao () ,
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
					List < Units > units = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getUnitsDao ().loadAll ();
					// Map all the Units using their codes
					Map < String , Units > _units = new HashMap < String , Units > ();
					// Iterate over all UOM
					for ( Units unit : units )
						// Map the unit to its code
						_units.put ( unit.getUnitCode () , unit );
					
					// Retrieve all the taxes
					List < Taxes > taxes = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getTaxesDao ().loadAll ();
					// Declare and initialize a map of taxes
					Map < String , Taxes > _taxes = new HashMap < String , Taxes > ();
					// Iterate over all taxes
					for ( Taxes tax : taxes )
						// Map the tax object to its tax code
						_taxes.put ( tax.getTaxCode () , tax );
					
					// Retrieve the client must stock list
					List < ClientMustStockList > clientMustStockList = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getClientMustStockListDao ()
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
					List < ClientItemHistory > clientItemHistory = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getClientItemHistoryDao ().queryBuilder ()
							.where ( ClientItemHistoryDao.Properties.ClientCode.eq ( clientCode ) ,
									ClientItemHistoryDao.Properties.DivisionCode.eq ( DatabaseUtils.getCurrentDivisionCode ( SalesOrderDetailsActivity.this ) ) ,
									ClientItemHistoryDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( SalesOrderDetailsActivity.this ) ) ).list ();
					// Get the item codes of the item history
					ArrayList < String > clientItemHistoryCodes = new ArrayList < String > ();
					for ( ClientItemHistory itemHistory : clientItemHistory )
						clientItemHistoryCodes.add ( itemHistory.getItemCode () );
					
					// Retrieve the current date
//					Calendar calendar = Calendar.getInstance ();
//					// Retrieve the start of the day
//					calendar.set ( Calendar.HOUR_OF_DAY , 0 );
//					calendar.set ( Calendar.MINUTE , 0 );
//					calendar.set ( Calendar.SECOND , 0 );
//					calendar.set ( Calendar.MILLISECOND , 0 );
//					long startDay = calendar.getTimeInMillis ();
					// Retrieve the client stock count headers performed today
					List < ClientStockCountHeaders > clientStockCountHeaders = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getClientStockCountHeadersDao ().queryBuilder ()
							.where ( ClientStockCountHeadersDao.Properties.StampDate.gt ( startDay ) ,
									ClientStockCountHeadersDao.Properties.ClientCode.eq ( clientCode ) ,
									ClientStockCountHeadersDao.Properties.CompanyCode.eq ( companyCode ) ).list ();
					// Store the codes in a list
					ArrayList < String > stockTransactionCodes = new ArrayList < String > ();
					// Iterate over all the client stock count headers
					for ( ClientStockCountHeaders clientStockCountHeader : clientStockCountHeaders )
						// Add the transaction code to the list
						stockTransactionCodes.add ( clientStockCountHeader.getTransactionCode () );
					
					// Determine if this is an edit
					boolean isEdit = getIntent ().getBooleanExtra ( IS_EDIT , false );
					// Determine if the MSL confirmation is enforcedFact
					boolean isMSLConfirmationRequired = PermissionsUtils.getEnforceMSLConfirmation ( SalesOrderDetailsActivity.this , userCode , companyCode );
					// Determine if the current client has previous orders
					boolean hasPreviousOrders = visit == null ? false : DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getTransactionHeadersDao ().queryBuilder ()
							.where ( TransactionHeadersDao.Properties.JourneyCode.eq ( visit.getJourneyCode () ) , 
									TransactionHeadersDao.Properties.TransactionType.eq ( TransactionHeadersUtils.Type.SALES_ORDER ) ,
									TransactionHeadersDao.Properties.ClientCode.eq ( clientCode ) ,
									TransactionHeadersDao.Properties.DivisionCode.eq ( divisionCode ) ,
									TransactionHeadersDao.Properties.CompanyCode.eq ( companyCode ) ).count () > 0;
									
									List < ClientItemCodes > clientItems= DatabaseUtils.getInstance (  SalesOrderDetailsActivity.this ).getDaoSession ().getClientItemCodesDao ().queryBuilder ()
											.where (   ClientItemCodesDao.Properties.CompanyCode.eq ( companyCode  ),ClientItemCodesDao.Properties.ClientCode.eq(clientCode) ).list ();
				
									Map < String , ArrayList < String > > _clientItems = new HashMap < String , ArrayList < String > > ();
									for(ClientItemCodes item:clientItems){
										if ( _clientItems.containsKey ( item.getItemCode () ) )
											// Map the current division to the item code
											_clientItems.get ( item.getItemCode () ).add ( item.getClientItemCode()   );
										// Otherwise the current item code is NOT mapped
										else {
											// Declare and initialize a divisions list
											ArrayList < String > list = new ArrayList < String > ();
											// Add the division to the list
											list.add ( item.getClientItemCode () );
											// Map the divisions list to the item code
											_clientItems.put ( item.getItemCode () , list );
										} // End else
									}
									Clients client=	( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClient();
												 
					// Initialize the lists used to host the sales orders and the selected sales orders
					orders = new ArrayList < Order > ();
					selectedOrders = new ArrayList < Order > ();
					freeOrders = new ArrayList<Order>();
					salesOrderMSL = new ArrayList < Order > ();
		    		salesOrderNEWSKU= new ArrayList < Order > ();
					Divisions div=DatabaseUtils.getInstance(SalesOrderDetailsActivity.this).getDaoSession().getDivisionsDao().queryBuilder ().where
					    	(  DivisionsDao.Properties.DivisionCode.eq( DatabaseUtils.getCurrentDivisionCode(SalesOrderDetailsActivity.this) ),
					    	   DivisionsDao.Properties.CompanyCode.eq( DatabaseUtils.getCurrentCompanyCode(SalesOrderDetailsActivity.this))).unique();
				 List< MSLDivisions> mSLDivisions = DatabaseUtils.getInstance(SalesOrderDetailsActivity.this).getDaoSession().getMSLDivisionsDao().queryBuilder ().where
					    	(  MSLDivisionsDao.Properties.DivisionCode.eq( DatabaseUtils.getCurrentDivisionCode(SalesOrderDetailsActivity.this) ),
					    			MSLDivisionsDao.Properties.CompanyCode.eq( DatabaseUtils.getCurrentCompanyCode(SalesOrderDetailsActivity.this))).list();
				 List< NewSkuListDivisions> newSkuListDivisions = DatabaseUtils.getInstance(SalesOrderDetailsActivity.this).getDaoSession().getNewSkuListDivisionsDao().queryBuilder ().where
					    	(  NewSkuListDivisionsDao.Properties.DivisionCode.eq( DatabaseUtils.getCurrentDivisionCode(SalesOrderDetailsActivity.this) ),
					    			NewSkuListDivisionsDao.Properties.CompanyCode.eq( DatabaseUtils.getCurrentCompanyCode(SalesOrderDetailsActivity.this))).list();
				
				// Retrieve the client must stock list
					List < ClientNewSkuList > clientNewSkuList = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getClientNewSkuListDao ()
							.queryRaw ( " WHERE ( SubjectCode = ? AND DivisionCode = ? AND CompanyCode = ? ) OR ( SubjectCode IN ( " +
									"SELECT ClientPropertyValueCode FROM ClientProperties WHERE ClientCode = ? AND DivisionCode = ? AND CompanyCode = ? ) ) " ,
									new String [] { clientCode ,
									divisionCode ,
									companyCode ,
									clientCode ,
									divisionCode ,
									companyCode } );
					// Get the item codes of the must stock list
					ArrayList < String > newSkuListDivisionsItems = new ArrayList < String > ();
					for ( ClientNewSkuList mustStockItem : clientNewSkuList )
						newSkuListDivisionsItems.add ( mustStockItem.getItemCode () );
	
					//	Log.e(" first:","first");
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
						Order order = new Order ( item , _units.get ( item.getUnitCode () ) );
						// Set the divisions codes list
						order.setDivisionCodes ( _itemDivisions.get ( item.getItemCode () ) );
						// Check that the division codes list is valid
						if ( order.getDivisionCodes () == null )
							// Initialize the division codes list
							order.setDivisionCodes ( new ArrayList < String > () );
						// Set the item bar codes list
						order.setBarCodes ( _itemBarcodes.get ( item.getItemCode () ) );
						order.setCaseTaxAmount( 0 );
						order.setUnitTaxAmount ( 0 );
						order.setAppFree(false);
						if(_transactionDetailsMissedMSL!=null && _transactionDetailsMissedMSL.containsKey( item.getItemCode() ))
						order.setReasonCode(_transactionDetailsMissedMSL.get(item.getItemCode()).getReasonCode());
						// Set the order prices
						if ( itemPrices.containsKey ( order.getItem ().getItemCode () ) ) {
							Prices orderPrices = itemPrices.get ( order.getItem ().getItemCode () );
							order.setPriceBig ( orderPrices.getPriceBig () );
							order.setPriceMedium ( orderPrices.getPriceMedium () );
							order.setPriceSmall ( orderPrices.getPriceSmall () );
							order.setPriceMediumOld(orderPrices.getPriceMedium ());
							order.setPriceSmallOld(orderPrices.getPriceSmall () );
							order.setDetailDiscountPercentage ( orderPrices.getDiscountPercentage () );
							if(orderPrices.getUnitTaxAmount()!=null && orderPrices.getCaseTaxAmount()!=null &&	div!=null && div.getApplyExiceTax()!=null &&div.getApplyExiceTax()==1	&& client .getIsTaxable()!=null && client .getIsTaxable()==1)// && client .getIsTaxable()!=null && client .getIsTaxable()==1)
							{
								order.setCaseTaxAmount(orderPrices.getCaseTaxAmount()  );//*item.getUnitMediumSmall()
								order.setUnitTaxAmount(orderPrices.getUnitTaxAmount()  );//*item.getUnitMediumSmall()
								
							}	else
								{order.setCaseTaxAmount( 0 );
								order.setUnitTaxAmount ( 0 );
								}
							if(  	div!=null && div.getApplyExiceTax()!=null &&div.getApplyFreeExiceTax() ==1)
								order.setAppFree(true);
							else
								order.setAppFree(false);
						} // End if
						order.setLastChar(order .getItem ().getItemCode().substring(order .getItem ().getItemCode().length() - 1) );
						order.setSequence(0);
					if(mSLDivisions!=null && mSLDivisions.size()>0)
						order.setForcedMustStockList(true);
					else
						order.setForcedMustStockList(false);
					if(newSkuListDivisions!=null && newSkuListDivisions.size()>0)
						order.setForcedNewItemList(true);
					else
						order.setForcedNewItemList(false);
					if ( newSkuListDivisionsItems .contains ( item.getItemCode () ) ) {
						// Indicate that the item is a must stock item
						order.setNewSKUList ( true );
						}
					
						// Check if there are suggestions for the current item
						if ( _clientSellingSuggestions.containsKey ( item.getItemCode () ) ) {
							// Retrieve the client selling suggestion
							ClientSellingSuggestion clientSellingSuggestion = _clientSellingSuggestions.get ( item.getItemCode () );
							// Set the suggestions
							order.setSuggestedMedium ( clientSellingSuggestion.getSuggestedQuantityCase ().intValue () );
							order.setSuggestedSmall ( clientSellingSuggestion.getSuggestedQuantityUnit ().intValue () );
						} // End if
						// Set the item tax
						if ( ItemsUtils.isTaxable ( order.getItem () ) && _taxes.get ( order.getItem ().getTaxCode () ) != null )
							order.setTax ( _taxes.get ( order.getItem ().getTaxCode () ).getTaxPercentage () );
						// Determine if the item is a must stock list
						if ( mustStockListItems.contains ( item.getItemCode () ) ) {
							// Indicate that the item is a must stock item
							order.setMustStockList ( true );
							// Confirm the item if this is an edit or if previous orders were issued
							order.setConfirmed ( hasPreviousOrders || ( ! isMSLConfirmationRequired ) || isEdit );
						} // End if
						// Otherwise check if the item is a history item
						else if ( clientItemHistoryCodes.contains ( item.getItemCode () ) )
							// Indicate that the item is a must stock item
							order.setHistory ( true );
						if ( PermissionsUtils.getEnableSalesOrderPromotionsLimit ( SalesOrderDetailsActivity.this , userCode , companyCode ) ) 
						{
//							
						 if(_promotionsF.containsKey( item.getItemCode() ) )
						 { 
									// Retrieve the promotions list that belong to the current ordered item
									ArrayList< PromotionDetails > tempPromotionDetails =  _promotionsF.get ( item.getItemCode() ); 
									double unitMediumSmall=item.getUnitMediumSmall();
									double basicOfferedQuantity=tempPromotionDetails.get(0).getBasicOfferedQuantity();
									double freeOfferedQuantityUnit=basicOfferedQuantity;
									int	   freeOfferedQuantityCase=(int)(freeOfferedQuantityUnit/unitMediumSmall);
									
									order.setMaxQuantityFreeCase( freeOfferedQuantityCase );
									order.setMaxQuantityFreeUnit( freeOfferedQuantityUnit );
								
					 } else{ 	
						       // 	 order.setMaxQuantityFreeCase(RH230-0 );
						        	// order.setMaxQuantityFreeUnit(10000000);
							        order.setMaxQuantityFreeCase(  0 );
					        	    order.setMaxQuantityFreeUnit(  0 );
						 }
						 }else{
                          	 	  order.setMaxQuantityFreeCase(10000000 );
					        	  order.setMaxQuantityFreeUnit(10000000);
						 }
//								else{
//									    order.setMaxQuantityFreeCase( 0 );
//						        	    order.setMaxQuantityFreeUnit( 0 );
//								}
//						 }
					        String clientItemCode = "";
							if (   _clientItems!=null && _clientItems.containsKey(item.getItemCode()) ) {
								List <String > dd=_clientItems.get(item.getItemCode());
								clientItemCode = dd.get ( 0 ) ;
								if ( dd.size () > 1 ) {
						    		String clientItemCodeTwo =   dd.get ( 1 )   ;
						    		clientItemCode +="-"+ clientItemCodeTwo;
						      } 
								//order.setClientItemCode(clientItemCode);
							}
 
							order.setClientItemCode(clientItemCode);
 						  
						    String barcodeOne="";
 						    if (_itemBarcodes!=null&&   _itemBarcodes.containsKey(item.getItemCode())   ) {
 								List <String > itemBarcodess=_itemBarcodes.get(item.getItemCode());
 						    	for(String ss:itemBarcodess)
 						    		 barcodeOne += ss  +"-" ;
// 						     	 barcodeOne = itemBarcodess.get ( 0 ).getItemBarcode ()  ;
// 					    	 if ( itemBarcodess.size () > 1 ) {
// 						    	 	String barcodeTwo =   itemBarcodess.get ( 1 ).getItemBarcode ()  ;
// 							    	//barcodeOne += itemBarcodess.get ( i ).getItemBarcode ()+"-";
// 						    	 	barcodeOne +="-"+ barcodeTwo;
// 						       }
 					    	 
 					       } if (barcodeOne.length() > 0)
 						    barcodeOne = barcodeOne.substring(0, barcodeOne.length()-1);
 					        order.setItemBarcodes(barcodeOne);
					 	// Check if there is at least one stock transaction code
						if ( ! stockTransactionCodes.isEmpty () ) {
							// Retrieve the counted stock for the current item
							int countedBig = 0 , countedMedium = 0 , countedSmall = 0;
							// Perform query
							List < ClientStockCountDetails > clientStockCountDetails = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getClientStockCountDetailsDao ().queryBuilder ()
									.where ( ClientStockCountDetailsDao.Properties.ItemCode.eq ( item.getItemCode () ) , ClientStockCountDetailsDao.Properties.TransactionCode.in ( stockTransactionCodes ) ).list ();
						
							
							// Iterate over the detail lines
							for ( ClientStockCountDetails clientStockCountDetail : clientStockCountDetails ) {
								// Add the counted quantities
								countedBig += clientStockCountDetail.getQuantityBig ();
								countedMedium += clientStockCountDetail.getQuantityMedium ();
								countedSmall += clientStockCountDetail.getQuantitySmall ();
							} // End for each
							// Set the new counted quantities
							order.setCountedBig ( countedBig );
							order.setCountedMedium ( countedMedium );
							order.setCountedSmall ( countedSmall );
						} // End if
						mappedInvoices.put ( item.getItemCode () , order );
						
						// Add the current item in the orders list
						orders.add ( order );
					} // End for each
		 
					
				} // End else
				//Log.e(" first2:","first2");
//				Collections.sort ( orders , new Comparator < Order > () {
//					@Override
//					public int compare ( Order order1 , Order order2 ) {
//						 
//							int sequence1 = order1.getItem ().getItemSequence () == null ? -1 : order1.getItem ().getItemSequence ();
//							int sequence2 = order2.getItem ().getItemSequence () == null ? -1 : order2.getItem ().getItemSequence ();
//							int order = sequence1 - sequence2;
//							if ( order != 0 )
//							return order;
//						    return order1.getItem ().getItemName ().compareToIgnoreCase ( order2.getItem ().getItemName () );
//						 
//					
//					
//					}
//				} );
			
				if ( PermissionsUtils.getNewSortingOrder ( SalesOrderDetailsActivity.this  , DatabaseUtils.getCurrentUserCode ( SalesOrderDetailsActivity.this ) , DatabaseUtils.getCurrentCompanyCode ( SalesOrderDetailsActivity.this  ) ) )
				{	
				// Sort the items, and put the MSL first , then the history items, then the remaining items
				Collections.sort ( orders , new Comparator < Order > () {
					@Override
					public int compare ( Order order1 , Order order2 ) {
						// Sort the items 
						
						if ( order1.isMustStockList () && ! order2.isMustStockList () )
							return -1;
						else if ( ! order1.isMustStockList () && order2.isMustStockList () )
							return 1;
 
								else {	if ( order1.isNewSKUList()  && ! order2.isNewSKUList () )
									return -1;
								else if ( ! order1.isNewSKUList () && order2.isNewSKUList () )
									return 1;
								else{
									 			
								//	order1.getItem ().getItemCode().substring(order1.getItem ().getItemCode().length() - 1))
											return order1.getLastChar()  .compareToIgnoreCase (order2.getLastChar());
											 
										}											
								}
		 
					}
				} );}else{
					Collections.sort ( orders , new Comparator < Order > () {
						@Override
						public int compare ( Order order1 , Order order2 ) {
							// Sort the items 
							
							if ( order1.isMustStockList () && ! order2.isMustStockList () )
								return -1;
							else if ( ! order1.isMustStockList () && order2.isMustStockList () )
								return 1;
							else {
								if ( order1.isNewSKUList()  && ! order2.isNewSKUList () )
									return -1;
								else if ( ! order1.isNewSKUList () && order2.isNewSKUList () )
									return 1;
								else{
									if ( order1.isHistory () && ! order2.isHistory () )
										return -1;
									else if ( ! order1.isHistory () && order2.isHistory () )
										return 1;
									else {
											// Otherwise both items are neither stock list nor history
											if ( order1.getItem ().getIsPromotion () != 1 && order1.getItem ().getIsPromotion () == 1 )
												return -1;
											else if ( order1.getItem ().getIsPromotion () == 1 && order1.getItem ().getIsPromotion () != 1 )
												return 1;	
											else{			
												int sequence1 = order1.getItem ().getItemSequence () == null ? -1 : order1.getItem ().getItemSequence ();
												int sequence2 = order2.getItem ().getItemSequence () == null ? -1 : order2.getItem ().getItemSequence ();
												int order = sequence1 - sequence2;
												if ( order != 0 )
													return order; 	
												return order1.getItem ().getItemName ().compareToIgnoreCase ( order2.getItem ().getItemName () );
												}
											}											
	 							}
							}
							/*if ( order1.isMustStockList () && ! order2.isMustStockList () )
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
						
							// Otherwise both items are neither stock list nor history
							if ( order1.getItem ().getIsPromotion () != 1 && order1.getItem ().getIsPromotion () == 1 )
								return -1;
							else if ( order1.getItem ().getIsPromotion () == 1 && order1.getItem ().getIsPromotion () != 1 )
								return 1;
							
						
								//return order1.getItem ().getItemName ().compareToIgnoreCase ( order2.getItem ().getItemName () );
							
							else 	
							{
								int sequence1 = order1.getItem ().getItemSequence () == null ? -1 : order1.getItem ().getItemSequence ();
								int sequence2 = order2.getItem ().getItemSequence () == null ? -1 : order2.getItem ().getItemSequence ();
								int order = sequence2 - sequence1;
								if ( order != 0 )
									return order;
								return order1.getItem ().getItemName ().compareToIgnoreCase ( order2.getItem ().getItemName () );
							}*/
						}
					} );
				}
			//	Log.e(" first3:","first3");
				HashMap < String , Integer > mappedBasket = new HashMap < String , Integer > ();
				
				_orders = new HashMap<String, Order>();
				for(Order order : orders)
					_orders.put(order.getItem().getItemCode(), order);
	    		  salesOrderMSL = new ArrayList < Order > ();
	    		  salesOrderNEWSKU = new ArrayList < Order > ();
				if ( transactionDetails != null ){
					if(transactionDetailsMissedMSL!=null &&transactionDetailsMissedMSL.size()>0)
					for ( TransactionDetailsMissedMSL transactionDetail1 : transactionDetailsMissedMSL ){
						if(transactionDetail1.getItemMSL()!=null &&transactionDetail1.getItemMSL()==1)
						{
							_orders.get(transactionDetail1.getItemCode()).setReasonCode(transactionDetail1.getReasonCode());
							salesOrderMSL.add(_orders.get(transactionDetail1.getItemCode()));
							selectedOrders.add( _orders.get(transactionDetail1.getItemCode()) );
						}else if(transactionDetail1.getItemMSL()!=null &&transactionDetail1.getItemMSL()==2){
							_orders.get(transactionDetail1.getItemCode()).setReasonCode(transactionDetail1.getReasonCode());
							salesOrderNEWSKU.add(_orders.get(transactionDetail1.getItemCode()));
							selectedOrders.add( _orders.get(transactionDetail1.getItemCode()) );
						}
					}
					// Iterate over all transaction details
					for ( TransactionDetails transactionDetail : transactionDetails ){
						if ( transactionDetail.getOrderedType().equals(TransactionDetailsUtils.Type.ORDER) && transactionDetail.getParentLineID()==-22){
							Order order = new Order(_orders.get(transactionDetail.getItemCode()).getItem(), _orders.get(transactionDetail.getItemCode()).getUnit());
							order.setBasketSmall ( transactionDetail.getQuantitySmall().intValue());
							order.setBasketMedium (transactionDetail.getQuantityMedium().intValue());
							order.setPriceSmall  ( transactionDetail.getPriceSmall() );
							order.setPriceMedium (transactionDetail.getPriceMedium() );
							order.setPromotionID(  transactionDetail.getItemAltName()  );
							mappedBasket.put( transactionDetail.getItemAltName(),transactionDetail.getMissedQuantityBig() .intValue());
							order.setQuantityBasket ( transactionDetail.getMissedQuantityBig() .intValue () );
							order.setPromotionDiscountPercentageBasket ( transactionDetail.getDiscountPercentage () );
							order.setBasketPriceBig( transactionDetail.getUserPriceBig());
							order.setBasketPriceMedium(transactionDetail.getUserPriceMedium());
							order.setBasketPriceSmall(transactionDetail.getUserPriceSmall());
							order.setReasonCode(transactionDetail.getReasonCode());
							order.setSequence(transactionDetail.getLineNote()==null?0:Integer.valueOf(transactionDetail.getLineNote().trim()));
							promotedOrders.add(order);
							 
						}
						
						if( transactionDetail.getOrderedType().equals(TransactionDetailsUtils.Type.ORDER) && transactionDetail.getParentLineID()!=-22){
							_orders.get(transactionDetail.getItemCode()).setQuantityBig(transactionDetail.getQuantityBig().intValue());
							_orders.get(transactionDetail.getItemCode()).setQuantityMedium(transactionDetail.getQuantityMedium().intValue());
							_orders.get(transactionDetail.getItemCode () ).setAvailability ( transactionDetail.getIsInvoiceRelated() );
							_orders.get(transactionDetail.getItemCode () ).setSequence(transactionDetail.getLineNote()==null?0:Integer.valueOf(transactionDetail.getLineNote().trim()));
							_orders.get(transactionDetail.getItemCode()).setQuantitySmall(transactionDetail.getQuantitySmall().intValue());
							_orders.get(transactionDetail.getItemCode()).setReasonCode(transactionDetail.getReasonCode());
							
							if ( !selectedOrders.contains(_orders.get(transactionDetail.getItemCode())))
								selectedOrders.add( _orders.get(transactionDetail.getItemCode()) );
							if ( !salesOrders.contains(_orders.get(transactionDetail.getItemCode())))
								salesOrders.add( _orders.get(transactionDetail.getItemCode()) );
						 
						}
						if ( transactionDetail.getOrderedType().equals(TransactionDetailsUtils.Type.FREE) && transactionDetail.getParentLineID()==-2){
							_orders.get(transactionDetail.getItemCode()).setQuantityFree(transactionDetail.getQuantitySmall().intValue());
							_orders.get(transactionDetail.getItemCode()).setQuantityFreeCase(transactionDetail.getQuantityMedium().intValue());
							_orders.get(transactionDetail.getItemCode () ).setAvailability ( transactionDetail.getIsInvoiceRelated() );
							_orders.get(transactionDetail.getItemCode () ).setSequence(transactionDetail.getLineNote()==null?0:Integer.valueOf(transactionDetail.getLineNote().trim()));
							
							if ( !selectedOrders.contains(_orders.get(transactionDetail.getItemCode()))){
								selectedOrders.add( _orders.get(transactionDetail.getItemCode()) );
							}
							if ( !salesOrders.contains(_orders.get(transactionDetail.getItemCode())))
								salesOrders.add( _orders.get(transactionDetail.getItemCode()) );
						}
						if ( transactionDetail.getOrderedType().equals(TransactionDetailsUtils.Type.FREE) && transactionDetail.getParentLineID()==-33){
							_orders.get(transactionDetail.getItemCode()).setFreeSmall( transactionDetail.getQuantitySmall().intValue());
							_orders.get(transactionDetail.getItemCode()).setFreeMedium (transactionDetail.getQuantityMedium().intValue());
							_orders.get(transactionDetail.getItemCode () ).setAvailability ( transactionDetail.getIsInvoiceRelated() );
							_orders.get(transactionDetail.getItemCode () ).setSequence(transactionDetail.getLineNote()==null?0:Integer.valueOf(transactionDetail.getLineNote().trim()));
							freeOrders.add(_orders.get(transactionDetail.getItemCode()));
//							if ( !selectedOrders.contains(_orders.get(transactionDetail.getItemCode()))){
//								selectedOrders.add( _orders.get(transactionDetail.getItemCode()) );
//							}
						}
					}
				}
				
				
				if ( basketPromotions == null ) {
					// Initialize the list
					basketPromotions = new ArrayList < BasketPromotion > ();
					// Query the instant promotions
				 	SQL = "SELECT DISTINCT PH.* , PD.* FROM PromotionDetails PD INNER JOIN PromotionHeaders PH ON PH.PromotionID = PD.PromotionID INNER JOIN PromotionAssignments PA " +
							"ON PD.PromotionID = PA.PromotionID AND CompanyCode = ? " +
							"AND ( AssignmentType IN ( 1 , 2 ) OR ( AssignmentType = 3 AND AssignmentCode = ? ) OR ( AssignmentType = 4 AND AssignmentCode IN ( " +
							"SELECT ClientPropertyValueCode FROM ClientProperties WHERE ClientCode = ? ) ) ) " +
							"WHERE PH.PromotionType = ? AND PH.StartDate IS NOT NULL AND PH.StartDate <= ? AND PH.EndDate IS NOT NULL AND PH.EndDate >= ? ";
					selectionArguments = new String [] { companyCode , clientCode , clientCode , String.valueOf ( PromotionUtils.Type.BASKET_PROMOTION ) , String.valueOf ( startDay ) , String.valueOf ( startDay ) };
					cursor = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getDatabase ().rawQuery ( SQL , selectionArguments );
					int promotionHeaderOffset = 0;
					int promotionDetailOffset = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getPromotionHeadersDao ().getAllColumns ().length;
					// Move the cursor to the first raw
					if ( cursor.moveToFirst () ) {
						do {
							PromotionHeaders promotionHeader = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getPromotionHeadersDao ().readEntity ( cursor , promotionHeaderOffset );
							PromotionDetails promotionDetail = DatabaseUtils.getInstance ( SalesOrderDetailsActivity.this ).getDaoSession ().getPromotionDetailsDao ().readEntity ( cursor , promotionDetailOffset );
							ArrayList < PromotionDetails > detailsList = null;
							ArrayList < Order > invoicesList = null;
							Order invoice = mappedInvoices.get ( promotionDetail.getOrderedItemCode () );
							if ( invoice == null )
								continue;
							if ( basketPromotions.isEmpty () || basketPromotions.get ( basketPromotions.size () - 1 ).getPromotionHeader ().getPromotionID () != promotionHeader.getPromotionID () ) {
								detailsList = new ArrayList < PromotionDetails > ();
								detailsList.add ( promotionDetail );
								invoicesList = new ArrayList < Order > ();
								invoicesList.add ( invoice );
								BasketPromotion basketPromotion = new BasketPromotion ();
								basketPromotion.setPromotionHeader ( promotionHeader );
								basketPromotion.setPromotionDetails ( detailsList );
								if(mappedBasket.containsKey(promotionHeader.getPromotionID()+""))
								basketPromotion.setQuantity(mappedBasket.get(promotionHeader.getPromotionID()+""));
								basketPromotion.setInvoices ( invoicesList );
								basketPromotions.add ( basketPromotion );
							}
							else {
								BasketPromotion basketPromotion = basketPromotions.get ( basketPromotions.size () - 1 );
								if(mappedBasket.containsKey(promotionHeader.getPromotionID()+""))
									basketPromotion.setQuantity(mappedBasket.get(promotionHeader.getPromotionID()+""));
								basketPromotion.getPromotionDetails ().add ( promotionDetail );
								basketPromotion.getInvoices ().add ( invoice );
							}
						} while ( cursor.moveToNext () );
					} // End if
					// Clear cursor
					cursor.close ();
					cursor = null;
				} // End if
		 
				if(  transactionHeader!=null && PermissionsUtils.getDisplaySequenceChange (SalesOrderDetailsActivity.this, DatabaseUtils.getCurrentUserCode(SalesOrderDetailsActivity.this) , DatabaseUtils.getCurrentCompanyCode(SalesOrderDetailsActivity.this )))
			 {
					
					Collections.sort ( selectedOrders , new Comparator < Order > () {
					@Override
					public int compare ( Order order1 , Order order2 ) {
						 
							int sequence1 = order1.getSequence()  ;
							int sequence2 = order2.getSequence();
							int order = sequence1 - sequence2;
						 
							return order;
						    
					
					
					}
				} );
						}
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
		protected void onPostExecute ( ArrayList < Order > orders ) {
			// Check if the activity has ended
			if ( activityEnded )
				// Do nothing
				return;
			// Determine if the activity must be finished
			if ( error ) {
				// Indicate that the activity cannot be displayed
				new AppToast ( SalesOrderDetailsActivity.this )
					.setIcon ( R.drawable.warning )
					.setText ( AppResources.getInstance ( SalesOrderDetailsActivity.this ).getString ( SalesOrderDetailsActivity.this , R.string.missing_client_currency_price_list_message ) )
					.setDuration ( Toast.LENGTH_LONG )
					.show ();
				// Initialize the orders list
				SalesOrderDetailsActivity.this.orders = new ArrayList < Order > ();
				// Exit method
				return;
			} // End if
    		
    		// Retrieve a reference to the secondary view
    		View secondary = findViewById ( R.id.layout_order_note );
			// Display the tertiary view accordingly
    		secondary.setVisibility ( displayOrderNote ? View.VISIBLE : View.GONE );
    		// Enable the main list accordingly
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( ! displayOrderNote );
    		// Determine if the sales order note is undergoing any modifications
    		if ( displayOrderNote )
	    		// Restore the secondary view
    			initializeSecondaryView ( true );
    		
    		
    		//ahmad
    		// Retrieve a reference to the secondary view
    		View secondary1 = findViewById ( R.id.layout_order_note );
			// Display the tertiary view accordingly
    		secondary1.setVisibility ( displayOrderNoteNew ? View.VISIBLE : View.GONE );
    		// Enable the main list accordingly
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( ! displayOrderNoteNew );
    		// Determine if the sales order note is undergoing any modifications
    		if ( displayOrderNoteNew )
	    		// Restore the secondary view
    			initializeSecondaryViewNew( true );
    		
		
			// Determine if the request code is INFO
			if ( requestCode != REQUEST_CODE_INFO )
				// Set a new adapter
				setListAdapter ( getSalesOrderDetailsAdapter ( R.layout.sales_order_details_activity_item , new ArrayList < Order > ( orders ) , true , true ,1) );
			// Otherwise the request code is INFO
			else {
				// Declare and initialize a multiple list adapter
				MultipleListAdapter adapter = new MultipleListAdapter ( SalesOrderDetailsActivity.this );
				// Add the sales order details adapter
				adapter.addSection ( new Section ( AppResources.getInstance ( SalesOrderDetailsActivity.this ).getString ( SalesOrderDetailsActivity.this , R.string.sales_order_details_label ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
						new OrderInfoAdapter ( SalesOrderDetailsActivity.this , R.layout.order_info_item , details ) );
				// Add the sales order adapter
//				  adapter.addSection ( new Section ( AppResources.getInstance ( SalesOrderDetailsActivity.this ).getString ( SalesOrderDetailsActivity.this , R.string.sales_order_products_label ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
//						getSalesOrderDetailsAdapter ( R.layout.sales_order_details_activity_item , selectedOrders , ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) , false ,1) );
			 	if( selectedOrders!=null && !selectedOrders.isEmpty())
					adapter.addSection ( new Section ( AppResources.getInstance ( SalesOrderDetailsActivity.this ).getString ( SalesOrderDetailsActivity.this , R.string.sales_order_products_label ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
							getSalesOrderDetailsAdapter ( R.layout.sales_order_details_activity_item , salesOrders , ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) , false,1 ) );
				if(salesOrderMSL!=null && !salesOrderMSL.isEmpty())
					adapter.addSection ( new Section ("MSL PRODUCTS REASONS" , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
							getSalesOrderDetailsAdapter ( R.layout.sales_order_details_activity_item , salesOrderMSL , ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) , false,1 ) );
				if(salesOrderNEWSKU!=null && !salesOrderNEWSKU.isEmpty())
					adapter.addSection ( new Section ( "New SKU LIST PRODUCTS REASONS" , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
							getSalesOrderDetailsAdapter ( R.layout.sales_order_details_activity_item , salesOrderNEWSKU , ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) , false,1 ) );
				
				if(freeOrders!=null&& !freeOrders.isEmpty ())
				adapter.addSection ( new Section ( AppResources.getInstance ( SalesOrderDetailsActivity.this ).getString ( SalesOrderDetailsActivity.this , R.string.sales_order_free_label ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
						getSalesOrderDetailsAdapter ( R.layout.sales_order_details_activity_item , freeOrders , ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) , false ,2) );
				 if (promotedOrders!=null && ! promotedOrders.isEmpty () )
						// Add the sales invoice adapter
						adapter.addSection ( new Section ( AppResources.getInstance (  SalesOrderDetailsActivity.this ).getString (  SalesOrderDetailsActivity.this , R.string.sales_order_promoted_label ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 255 , 0 ) , null ) ,
								getSalesOrderDetailsAdapter ( R.layout.sales_order_details_activity_item , promotedOrders , ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) , false ,3) );

				// Set the list adapter
				setListAdapter ( adapter );
			} // End else
			 
    		// Refresh the action bar
    		invalidateOptionsMenu ();
		}
		
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
		// Compute the sales order details
    	double grossAmount = 0;
    	double discountAmount = 0;
//    	double taxes = 0;
    	double totalValue = 0;
    	double taxesAmount = 0;  
    	Divisions div=DatabaseUtils.getInstance( this).getDaoSession().getDivisionsDao().queryBuilder ().where
		    	(  DivisionsDao.Properties.DivisionCode.eq( DatabaseUtils.getCurrentDivisionCode( this) ),
		    	   DivisionsDao.Properties.CompanyCode.eq( DatabaseUtils.getCurrentCompanyCode( this))).unique();
    	boolean freetaxe=false;
    	if(div!=null && div.getApplyFreeExiceTax()!=null &&div.getApplyFreeExiceTax() ==1	)
    		freetaxe=true;
		// Iterate over all the selected orders
		for ( Order order : selectedOrders ) {
			// Compute the current gross value for non free items
			double _grossAmount = order.getQuantityBig () * order.getPriceBig () + order.getQuantityMedium () * order.getPriceMedium () + order.getQuantitySmall () * order.getPriceSmall ();
    		// Compute the discount amount for non free items
    		double percentage = order.getDiscountPercentage ();
    		double _discountAmount = _grossAmount * ( percentage ) / 100;
    		// Update values above for free items
    		_grossAmount += order.getQuantityFreeAdded () * order.getPriceSmall ();
    		_discountAmount += order.getQuantityFreeAdded () * order.getPriceSmall ();
    		_grossAmount += order.getQuantityFreeCase() * order.getPriceMedium();
    		_discountAmount += order.getQuantityFreeCase () * order.getPriceMedium ();
			// Compute the current tax value
			double _taxes = 0;
			// Check if the item is taxable
			if ( order.getTax () != 0 )
				// Compute the current taxes
				_taxes = ( _grossAmount - _discountAmount ) * order.getTax () / 100;
			double taxeAmount=order.getQuantityMedium()*order.getCaseTaxAmount()
					 
					+ order.getQuantitySmall (  )*order.getUnitTaxAmount()+
					(freetaxe?order.getQuantityFreeAdded ()*order.getUnitTaxAmount()+
					order.getQuantityFreeCase()*order.getCaseTaxAmount():0)
					;
		
			taxesAmount +=taxeAmount;
			// Compute the current total value
			double _totalValue = ( _grossAmount - _discountAmount ) + _taxes+taxeAmount;
			// Update all the values
			grossAmount += _grossAmount;
			discountAmount += _discountAmount;
//			taxes += _taxes;
			totalValue += _totalValue;
		} // End for each
		
		for ( Order order : promotedOrders ) {
			// Compute the current gross value for non free items
			double _grossAmount = order.getBasketBig  () * order.getBasketPriceBig () + order.getBasketMedium () * order.getBasketPriceMedium () + order.getBasketSmall () * order.getBasketPriceSmall ();
    		// Compute the discount amount for non free items
    		double percentage = order.getPromotionDiscountPercentageBasket();
    		double _discountAmount = _grossAmount * ( percentage ) / 100;
    		// Update values above for free items
    		 
			// Compute the current tax value
			double _taxes = 0;
			// Check if the item is taxable
			if ( order.getTax () != 0 )
				// Compute the current taxes
				_taxes = ( _grossAmount - _discountAmount ) * order.getTax () / 100;
			double taxeAmount=0;
			if(div!=null && div.getApplyExiceTax()!=null &&div.getApplyExiceTax()==1	&&  percentage !=100)
				
			  taxeAmount=order.getBasketMedium()*order.getCaseTaxAmount()
					 
					+ order.getBasketSmall (  )*order.getUnitTaxAmount() 
				 
					;
		
			taxesAmount +=taxeAmount;
			// Compute the current total value
			double _totalValue = ( _grossAmount - _discountAmount ) + _taxes+taxeAmount;
			// Update all the values
			grossAmount += _grossAmount;
			discountAmount += _discountAmount;
//			taxes += _taxes;
			totalValue += _totalValue;
		} // End for each
		
		if ( freeOrders  != null && !freeOrders .isEmpty() ){
			// Iterate over all the free orders LBP
			for ( Order o : freeOrders ){
				// Get the price small
				double priceSmallFree = o.getPriceSmall();
				double priceMediumFree = o.getPriceMedium();
				// Compute the discount amount
				discountAmount  +=    o. getFreeSmall()   * priceSmallFree+o.getFreeMedium()*priceMediumFree ;
				grossAmount+=  o.getFreeSmall()   * priceSmallFree+o.getFreeMedium()*priceMediumFree ;
				double taxeAmount=(freetaxe? o.getFreeMedium()*o.getCaseTaxAmount()
						 
						+ o.getFreeSmall (  )*o.getUnitTaxAmount() :0)
					 
						;
				taxesAmount +=taxeAmount;
				totalValue += taxeAmount;
				}
			}
		
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
		details = new ArrayList < OrderInfo > ();
		// Populate the details list
		details.add ( new OrderInfo ( OrderInfo.ID.DELIVERY_DATE , AppResources.getInstance ( this ).getString ( this , R.string.delivery_date_label ).toUpperCase () , twoDigits.format ( selectedDay ) + "-" + twoDigits.format ( selectedMonth + 1 ) + "-" + selectedYear ) );
		details.add ( new OrderInfo ( OrderInfo.ID.NOTE , AppResources.getInstance ( this ).getString ( this , R.string.sales_order_note_label ) , note ) );
		details.add ( new OrderInfo(OrderInfo.ID.NOTENEW , AppResources.getInstance ( this ).getString ( this , R.string.sales_order_note_label_1 ) , noteNew ));
		details.add ( new OrderInfo ( OrderInfo.ID.CURRENCY , AppResources.getInstance ( this ).getString ( this , R.string.currency_label ).toUpperCase () , currency.getCurrencyName () ) );
		details.add ( new OrderInfo ( OrderInfo.ID.GROSS_VALUE , AppResources.getInstance ( this ).getString ( this , R.string.gross_amount_label ) , moneyFormat.format ( grossAmount ) ) );
		details.add ( new OrderInfo ( OrderInfo.ID.DISC_VALUE , AppResources.getInstance ( this ).getString ( this , R.string.discount_amount_label ) , moneyFormat.format ( discountAmount ) ) );
		details.add ( new OrderInfo ( OrderInfo.ID.NET_VALUE , AppResources.getInstance ( this ).getString ( this , R.string.net_amount_label ) , moneyFormat.format ( grossAmount - discountAmount ) ) );
		if(div!=null && div.getApplyExiceTax()!=null &&div.getApplyExiceTax() ==1	)
		details.add ( new OrderInfo ( OrderInfo.ID.TAXES , AppResources.getInstance ( this ).getString ( this , R.string.taxes_label ) , moneyFormat.format ( taxesAmount ) ) );
		details.add ( new OrderInfo ( OrderInfo.ID.TOTAL_VALUE , AppResources.getInstance ( this ).getString ( this , R.string.total_value_label ) , moneyFormat.format ( totalValue ) ) );
		details.add ( new OrderInfo ( OrderInfo.ID.TOTAL_SKU_SO , AppResources.getInstance ( this ).getString ( this , R.string.total_sku_so_label ) ," " + selectedOrders.size()  ) );
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
    	double discountAmount = 0;
    	double taxes = 0;
    	double totalValue = 0;
		// Iterate over all the selected orders
		for ( Order order : selectedOrders ) {
    		// Compute the current gross value for non free items
    		double _grossAmount = order.getQuantityBig () * order.getPriceBig () + order.getQuantityMedium () * order.getPriceMedium () + order.getQuantitySmall () * order.getPriceSmall ();
    		// Compute the discount amount for non free items
    		double percentage = order.getDiscountPercentage ();
    		percentage = percentage > 100 ? 100 : percentage;
    		double _discountAmount = _grossAmount * ( percentage ) / 100;
    		// Update values above for free items
    		_grossAmount += order.getQuantityFreeAdded () * order.getPriceSmall ();
    		_discountAmount += order.getQuantityFreeAdded () * order.getPriceSmall ();
			// Compute the current tax value
			double _taxes = 0;
			// Check if the item is taxable
			if ( order.getTax () != 0 )
				// Compute the current taxes
				_taxes = ( _grossAmount - _discountAmount ) * order.getTax () / 100;
			// Compute the current total value
			double _totalValue = ( _grossAmount - _discountAmount ) + _taxes;
			// Update all the values
			grossAmount += _grossAmount;
			discountAmount += _discountAmount;
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
		for ( OrderInfo detail : details ) {
			// Check if the current detail is about the sales order gross amount
			if ( detail.getId () == OrderInfo.ID.GROSS_VALUE ) {
				// Update the sales order gross amount
				detail.setValue ( moneyFormat.format ( grossAmount ) );
			} // End if
			// Check if the current detail is about the sales order discount amount
			else if ( detail.getId () == OrderInfo.ID.DISC_VALUE ) {
				// Update the sales order discount amount
				detail.setValue ( moneyFormat.format ( discountAmount ) );
			} // End else if
			// Check if the current detail is about the sales order net amount
			else if ( detail.getId () == OrderInfo.ID.NET_VALUE ) {
				// Update the sales order total net amount
				detail.setValue ( moneyFormat.format ( grossAmount - discountAmount ) );
			} // End else if
			// Check if the current detail is about the sales order taxes
			else if ( detail.getId () == OrderInfo.ID.TAXES ) {
				// Update the sales order total taxes
				detail.setValue ( moneyFormat.format ( taxes ) );
			} // End else if
			// Check if the current detail is about the sales order total amount
			else if ( detail.getId () == OrderInfo.ID.TOTAL_VALUE ) {
				// Update the sales order total total amount
				detail.setValue ( moneyFormat.format ( totalValue ) );
			} // End else if
			else if ( detail.getId () == OrderInfo.ID.TOTAL_SKU_SO ) {
				// Update the sales order total total amount
				detail.setValue ( " " + selectedOrders.size() );
			} // End else if
		} // End for each
	}
	
	/**
	 * AsyncTask helper class used to filter the list.
	 * 
	 * @author Elias
	 *
	 */
	private class FilterList extends AsyncTask < Void , Void , ArrayList < Order > > {
		
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
		protected ArrayList < Order > doInBackground ( Void ... params ) {
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
		protected void onPostExecute ( ArrayList < Order > orders ) {
			// Check if the request code is INFO
			if ( requestCode == REQUEST_CODE_INFO )
				// Cannot filter, do nothing
				return;
			// Check if the result is valid
			if ( orders == null )
				// Invalid result, do nothing
				return;
			// Check if the search query has changed
			if ( ! SalesOrderDetailsActivity.this.searchQuery.equals ( searchQuery ) )
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
				( (ArrayAdapter < Order >) getListAdapter () ).clear ();
				// Add the new filtered list of orders
				( (ArrayAdapter < Order >) getListAdapter () ).addAll ( orders );
				// Notifies the attached observers that the underlying data has been changed
				( (ArrayAdapter < Order >) getListAdapter () ).notifyDataSetChanged ();
			} catch ( Exception exception ) {
				// Cannot cast to array adapter of orders objects
			} // End try-catch block
		}
		
	}
	private void refreshInstantPromotions ( final HashMap < String , Order > mappedInvoices ) {
    	// Set the free invoices list
    	freeOrders = new ArrayList < Order > ();
    	// Make sure the lists are valid
    	if ( mappedInvoices != null && selectedOrders != null && ! selectedOrders.isEmpty () && instantPromotions != null && ! instantPromotions.isEmpty () ) {
    		// Iterate over the selected invoices
    		for ( Order order : selectedOrders ) { 
    		  
    			// Retrieve the list of applied promotions for the current invoiced item
    			ArrayList < PromotionDetails > instantPromotionDetails = instantPromotions.get ( order.getItem ().getItemCode () );
    			// Check if the list is valid
    			if ( instantPromotionDetails != null && ! instantPromotionDetails.isEmpty () ) {
    				
    				Collections.sort ( instantPromotionDetails , new Comparator < PromotionDetails > () {
    					@Override
    					public int compare ( PromotionDetails order1 , PromotionDetails order2 ) {
    					 
    							 		
    						 	return  order2.getBasicOrderedQuantity().compareTo (order1.getBasicOrderedQuantity() );
    						 	 										
    								 
    					}
    				} );
    				// Iterate over the instant promotions
    				for ( PromotionDetails promotionDetail : instantPromotionDetails ) {
    					// Reference to the applied invoice
    					Order appliedInvoice = order;
    					// Check if the promotion is for the current item
    					if ( promotionDetail.getOfferedItemCode () != null && ! promotionDetail.getOfferedItemCode ().equals ( promotionDetail.getOrderedItemCode () ) ) 
    						// Set the applied invoice
    						appliedInvoice = mappedInvoices.get ( promotionDetail.getOfferedItemCode () );
    					// Check if the applied invoice is valid
    					if ( appliedInvoice == null )
    						// Skip the current promotion detail
    						continue;
    					// Check if the ordered quantity has reached the required quantity
    					if ( order.getQuantitySmall () + order.getQuantityMedium() * order.getItem().getUnitMediumSmall() < promotionDetail.getBasicOrderedQuantity () )
    						// Skip the current promotion detail
    						continue; 
						// Check if the promotion offers discount
//						if ( promotionDetail.getPromotionDetailType ().equals ( PromotionUtils.DetailType.DISCOUNT_PERCENTAGE ) ) {
//							// Set the discount percentage
//							appliedInvoice.setPromotionDiscountPercentage ( appliedInvoice.getPromotionDiscountPercentage () + promotionDetail.getDiscountPercentage () );
//							// Check if the original discount is applied
//							if ( ! promotionDetail.getApplyOriginalDiscount ().equals ( StatusUtils.isAvailable () ) )
//								// Clear previous discount
//								appliedInvoice.clearDetailDiscountPercentage ();
//						} // End if
//						// Otherwise check if the promotion offers quantities
//						else
							if ( promotionDetail.getPromotionDetailType ().equals ( PromotionUtils.DetailType.OFFERED_QUANTITY ) ) {
							// Add the free quantities
							appliedInvoice.setQuantityPromotedFree ( appliedInvoice.getQuantityPromotedFree () + (int) ( ( (int) (( order.getQuantitySmall () + order.getQuantityMedium() * order.getItem().getUnitMediumSmall() )/ promotionDetail.getBasicOrderedQuantity () ) ) * promotionDetail.getBasicOfferedQuantity () ) );
							// Check if the original discount is applied
							if ( ! promotionDetail.getApplyOriginalDiscount ().equals ( StatusUtils.isAvailable () ) )
								// Clear previous discount
								appliedInvoice.clearDetailDiscountPercentage ();
							// Check if the invoice is already added to the free invoices list
							if ( ! freeOrders.contains ( appliedInvoice ) )
								// Add the free invoice to the list
							{	freeOrders.add ( appliedInvoice );
							  break;
							  };
						} // End if
    				} // End for each
    			} // End if
    			 
    			
    		} // End for each
    		
    		
//    		for ( Order order : selectedOrders ) { 
//    		 
//    			// Retrieve the list of applied promotions for the current invoiced item
//    			ArrayList < PromotionDetails > instantPromotionDetails = instantPromotions.get ( order.getItem ().getItemCode () );
//    			// Check if the list is valid
//    			if ( instantPromotionDetails != null && ! instantPromotionDetails.isEmpty () ) {
//    				
//    				Collections.sort ( instantPromotionDetails , new Comparator < PromotionDetails > () {
//    					@Override
//    					public int compare ( PromotionDetails order1 , PromotionDetails order2 ) {
//    					 
//    							 		
//    						 	return  order2.getBasicOrderedQuantity().compareTo (order1.getBasicOrderedQuantity() );
//    						 	 										
//    								 
//    					}
//    				} );
//    				// Iterate over the instant promotions
//    				for ( PromotionDetails promotionDetail : instantPromotionDetails ) {
//    					// Reference to the applied invoice
//    					Order appliedInvoice = order;
//    					if ( promotionDetail.getPromotionDetailType ().equals ( PromotionUtils.DetailType.DISCOUNT_PERCENTAGE ) ) {
//    					// Check if the promotion is for the current item
//    					if ( promotionDetail.getOfferedItemCode () != null && ! promotionDetail.getOfferedItemCode ().equals ( promotionDetail.getOrderedItemCode () ) ) 
//    						// Set the applied invoice
//    						appliedInvoice = mappedInvoices.get ( promotionDetail.getOfferedItemCode () );
//    					// Check if the applied invoice is valid
//    					if ( appliedInvoice == null )
//    						// Skip the current promotion detail
//    						continue;
//    					// Check if the ordered quantity has reached the required quantity
//    					if ( order.getBasicUnitQuantity ( Invoice.STATE_DEFAULT ) < promotionDetail.getBasicOrderedQuantity () )
//    						// Skip the current promotion detail
//    						continue; 
//						// Check if the promotion offers discount
//						
//							// Set the discount percentage
//							appliedInvoice.setPromotionDiscountPercentage ( appliedInvoice.getPromotionDiscountPercentage () + promotionDetail.getDiscountPercentage () );
//							// Check if the original discount is applied
//							if ( ! promotionDetail.getApplyOriginalDiscount ().equals ( StatusUtils.isAvailable () ) )
//								// Clear previous discount
//								appliedInvoice.clearDetailDiscountPercentage ();
//							break;
//						} // End if
// 
//     				} // End for each
//    			} // End if
//    			 
//    			
//    		} // End for each
    		// Iterate over the free invoices
    		for ( Order invoice : freeOrders )
    			{invoice.refreshInvoiceState ();
    			// Check if the invoice is already added to the selected invoices list
//				if ( ! selectedOrders.contains ( invoice ) )
//					// Add the free invoice to the list
//					selectedOrders.add ( invoice );
    			}
    	} // End if
    	
    	
    	 
    	
    }
	/*
	 * Called when a view has been clicked.
	 *
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick ( View view ) {
		try {
			// Retrieve a reference to the view holder
			ViewHolder viewHolder = (ViewHolder)( (View) view.getParent().getParent() .getParent().getParent()  ).getTag ();
			// Store a reference to the selected item code
			selectedItemIconCode = viewHolder.order.getItem ().getItemCode ();
			// Refresh the action bar
			invalidateOptionsMenu ();
			// Setup the cover flow view asynchronously
			new SetupCoverFlow ().execute ();
		} catch ( Exception exception ) {
			// Do nothing
		} // End of try-catch block
	}
	
	
	/**
	 * AsyncTask helper class used to populate the sales order items list with the appropriate items / orders.
	 * 
	 * @author Elias
	 *
	 */
	private class SetupCoverFlow extends AsyncTask < Void , Void , String [] > {
		
		/*
		 * Runs on the UI thread before doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute () {
			// Display indeterminate progress dialog
			AppDialog.getInstance ().displayIndeterminateProgress ( SalesOrderDetailsActivity.this , null , null );
			// Retrieve a reference to the cover flow
			CoverFlow coverFlow = (CoverFlow) findViewById ( R.id.coverflowReflect );
			// Display the cover flow view
			coverFlow.setVisibility ( View.VISIBLE );
			
			// Declare and initialize a point object used to store the screen size
			Point size = new Point();
			// Compute the screen size
			getWindowManager ().getDefaultDisplay ().getSize ( size );
			// Determine which side is smaller
			int side = size.x > size.y ? size.y : size.x;
			// Compute the cover flow image size to be exactly the smallest side of the screen 
			
			// Set the cover flow image size
			coverFlow.setImageHeight ( side );
			coverFlow.setImageWidth ( side );
		}
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected String [] doInBackground ( Void ... params ) {
			try {
				
				displayOrderImage = true;
				// Retrieve a reference to the item code
				final String itemCode = selectedItemIconCode;
				// Check if the item code is valid
				if ( itemCode == null )
					// Do nothing
					return new String [] {};
				
				// Open the primary storage device folder
				File storagePrimary = Environment.getExternalStorageDirectory ();
				// Open the root storage folder
				File storageRoot = storagePrimary.getParentFile ();
				// Retrieve a list of other secondary storage
				File storageSecondaryArray [] = storageRoot.listFiles ( new ExternalStorageFilter () );
				
				// Determine what storage folder to use
				// Based on if they contain an item pictures directory inside a pictures directory
				// Priority is for secondary storage devices first
				File storage = pickStorage ( storagePrimary , storageSecondaryArray );
				// Open the required item pictures folder
				File folder = new File ( storage , "//" + Environment.DIRECTORY_PICTURES + "//" + ItemCard.ITEM_PICTURES_DIRECTORY );
				// Check if the folder exists
				if ( ! folder.exists () )
					// Do nothing
					return new String [] {};
				
				// Retrieve the list of pictures that belong to the current item
				File pictures [] = folder.listFiles ( new PictureFilter ( itemCode   ) );
				// Check if the list is valid
				if ( pictures == null )
					// No item pictures
					return new String [] {};
				
				// Otherwise return the item picture paths
				// Declare and initialize an array of strings
				String paths [] = new String [ pictures.length ];
				// Iterate over all picture files
				for ( int i = 0 ; i < pictures.length ; i ++ )
					// Add the current picture file path
					paths [ i ] = pictures [ i ].getAbsolutePath ();
				// Return the paths array
				return paths;
			} catch ( Exception exception ) {
				// Do nothing
				return new String [] {};
			} // End of try-catch block
		}
		
		/**
		 * Picks from within the storage list the first occurrence that contains a valid item pictures folder.<br>
		 * If none has the required folder, or if the list is empty / invalid, the default storage is returned.
		 * 
		 * @param defaultStorage	Reference to the default storage file to return, if the storage list is empty or has no storage with the required item pictures folder.
		 * @param storageList	Storage list hosting directories that might contain a valid item pictures folder.
		 * @return	Reference to a storage file hosting a valid item pictures folder.
		 */
		private File pickStorage ( final File defaultStorage , final File storageList [] ) {
			// Check if the storage list is valid
			if ( storageList == null )
				// Return the default storage
				return defaultStorage;
			// Iterate over the storage list
			for ( File storageSecondary : storageList ) {
				// Open the required item pictures folder
				File folder = new File ( storageSecondary , "//" + Environment.DIRECTORY_PICTURES + "//" + ItemCard.ITEM_PICTURES_DIRECTORY );
				// Check if the folder exists
				if ( folder.exists () )
					// Return the current storage file
					return storageSecondary;
			} // End for each
			// Otherwise return the default storage
			return defaultStorage;
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( String paths [] ) {
			// Dismiss dialog
			AppDialog.getInstance ().dismiss ();
			// Retrieve a reference to the cover flow
			CoverFlow coverFlow = (CoverFlow) findViewById ( R.id.coverflowReflect );
			coverFlow.setMaxRotationAngle ( 0 );
			coverFlow.setWithReflection ( false );
			// Check if the cover flow adapter is valid
			if ( coverFlow.getAdapter () != null ) {
				// Declare a PathImageAdapter object
				BaseAdapter mainAdapter = null;
				PathImageAdapter adapter = null;
				// Determine if the adapter is an instance of PathImageAdapter
				if ( coverFlow.getAdapter () instanceof PathImageAdapter ) {
					// Initialize the adapter
					adapter = (PathImageAdapter) coverFlow.getAdapter ();
					mainAdapter = adapter;
				} // End if
				// Determine if the adapter is an instance of ReflectingImageAdapter
				// AND if its linked adapter is an instance of PathImageAdapter
				else if ( coverFlow.getAdapter () instanceof ReflectingImageAdapter
						&& ( (ReflectingImageAdapter) coverFlow.getAdapter () ).getLinkedAdapter () instanceof PathImageAdapter ) {
					// Initialize the adapter
					mainAdapter = (BaseAdapter) coverFlow.getAdapter ();
					adapter = (PathImageAdapter) ( (ReflectingImageAdapter) mainAdapter ).getLinkedAdapter ();
				} // End else if
				// Check if the adapter is valid
				if ( adapter != null ) {
					// Add the new image paths to the adapter and refresh the cover flow view
					adapter.clearCache ();
					adapter.setAdapterCode ( selectedItemIconCode );
					adapter.addAll ( paths );
					mainAdapter.notifyDataSetChanged ();
				} // End if
			} // End if
			else
				// Set a new path image adapter to the cover flow view
				coverFlow.setAdapter ( new PathImageAdapter ( SalesOrderDetailsActivity.this , paths ) );
		}
		
	}
}