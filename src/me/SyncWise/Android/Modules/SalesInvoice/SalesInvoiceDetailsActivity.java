/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.SalesInvoice;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppDialog.ButtonsType;
 
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.AppToast;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Vibration;
import me.SyncWise.Android.Database.ClientCreditingsDao;
import me.SyncWise.Android.Database.ClientCurrenciesDao;
import me.SyncWise.Android.Database.ClientDuesDao;
import me.SyncWise.Android.Database.ClientItemHistory;
import me.SyncWise.Android.Database.ClientItemHistoryDao;
import me.SyncWise.Android.Database.ClientMustStockList;
import me.SyncWise.Android.Database.ClientPriceListsDao;
import me.SyncWise.Android.Database.ClientSellingSuggestion;
import me.SyncWise.Android.Database.ClientSellingSuggestionDao;
import me.SyncWise.Android.Database.ClientStockCountDetails;
import me.SyncWise.Android.Database.ClientStockCountDetailsDao;
import me.SyncWise.Android.Database.ClientStockCountHeaders;
import me.SyncWise.Android.Database.ClientStockCountHeadersDao;
import me.SyncWise.Android.Database.CollectionHeadersDao;
import me.SyncWise.Android.Database.CollectionUtils;
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
import me.SyncWise.Android.Database.TransactionDetailsUtils;
import me.SyncWise.Android.Database.TransactionHeaders;
import me.SyncWise.Android.Database.TransactionHeadersDao;
import me.SyncWise.Android.Database.TransactionHeadersUtils;
import me.SyncWise.Android.Database.TransactionSequencesDao;
import me.SyncWise.Android.Database.Units;
import me.SyncWise.Android.Database.UserPasswordsUtils;
import me.SyncWise.Android.Database.Users;
import me.SyncWise.Android.Database.UsersDao;
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
import me.SyncWise.Android.Modules.SalesInvoice.SalesInvoiceDetailsAdapter.ViewHolder;
import me.SyncWise.Android.Modules.SalesOrder.SalesOrderDetailsActivity;
 
import me.SyncWise.Android.Modules.Target.TargetUpdate;
import me.SyncWise.Android.Modules.Target.TargetUpdate.UpdateType;
import me.SyncWise.Android.SectionsList.MultipleListAdapter;
import me.SyncWise.Android.SectionsList.Section;
import me.SyncWise.Android.Utilities.ActivityInstance;
import me.SyncWise.Android.Utilities.ClientCard;
import me.SyncWise.Android.Widgets.Baguette;
import me.SyncWise.Android.Widgets.CustomLinearLayout;
import me.SyncWise.Android.Widgets.QuickAction.ActionItem;
import me.SyncWise.Android.Widgets.QuickAction.QuickAction;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

/**
 * Activity implemented to perform, view or edit a sales invoice.
 * 
 * @author Elias -Ahmad
 * @sw.todo <b>Sales Invoice Details Activity Implementation :</b><br>
 * Simply add this class in the {@code AndroidManifest.xml} file along with the permission :<br>
 * {@code <uses-permission android:name="android.permission.VIBRATE"/>}<br>
 * AND disable activity recreation for orientation events by adding the following to the activity tag in the manifest file :<br>
 * {@code android:configChanges="orientation|screenSize"}
 * 
 */
public class SalesInvoiceDetailsActivity extends BaseTimerListActivity implements SearchView.OnQueryTextListener , QuickAction.OnActionItemClickListener , OnItemLongClickListener {

	/**
	 * Bundle key used to put/retrieve the content of {@link #isCreated}.
	 */
	private static final String IS_CREATED = SalesInvoiceDetailsActivity.class.getName () + ".IS_CREATED";
	
	/**
	 * Boolean used to indicate if the activity has been previously created.<br>
	 * This flag is mainly used to determine if the current activity creation is the first or not (activity re-creation due to phone rotation for example).
	 */
	private boolean isCreated;
	private boolean isStock=false;
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
	public static final String REQUEST_CODE = SalesInvoiceDetailsActivity.class.getName () + ".REQUEST_CODE";
	
	/**
	 * Integer used to host the request code.
	 * @see #REQUEST_CODE_NEW
	 * @see #REQUEST_CODE_INFO
	 */
	protected int requestCode;
	
	/**
	 * Constant integer holding the request code to create a new sales invoice.
	 */
	public static final int REQUEST_CODE_NEW = 1;
	
	/**
	 * Constant integer holding the request code to display the sales invoice info.<br>
	 * The invoiced items should be displayed.
	 */
	public static final int REQUEST_CODE_INFO = 2;
	
	/**
	 * Constant integer holding the request code to display the sales invoice details.<br>
	 * The sales invoice details (tax, discount, total, ...) should be displayed.
	 */
	public static final int REQUEST_CODE_DETAILS = 3;
	
	/**
	 * Constant integer holding the request code used to print invoices.
	 */
	private static final int REQUEST_CODE_PRINT = 10;

	/**
	 * Bundle key used to put/retrieve the content of the edit flag.
	 */
	public static final String IS_EDIT = SalesInvoiceDetailsActivity.class.getName () + ".IS_EDIT";
	
	/**
	 * Bundle key used to put/retrieve the content of the view only flag.
	 */
	public static final String IS_VIEW_ONLY = SalesInvoiceDetailsActivity.class.getName () + ".IS_VIEW_ONLY";	
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #invoiceModification}.
	 */
	private static final String INVOICE_MODIFICATION = SalesInvoiceDetailsActivity.class.getName () + ".INVOICE_MODIFICATION";
	
	/**
	 * Reference to the {@link me.SyncWise.Android.Modules.SalesInvoice.Invoice Invoice} object being modified.
	 */
	protected Invoice invoiceModification;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #displayPasscode}.
	 */
	private static final String DISPLAY_PASSCODE = SalesInvoiceDetailsActivity.class.getName () + ".DISPLAY_PASSCODE";
	
	/**
	 * Boolean used to determine whether to display the passcode UI or not.
	 */
	protected boolean displayPasscode;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #displayInvoiceNote}.
	 */
	private static final String DISPLAY_INVOICE_NOTE = SalesInvoiceDetailsActivity.class.getName () + ".DISPLAY_INVOICE_NOTE";
	
	/**
	 * Boolean used to determine whether to display the invoice note UI or not.<br>
	 * This boolean is mainly used to save the invoice state.
	 */
	protected boolean displayInvoiceNote;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #displayBasketPromotions}.
	 */
	private static final String DISPLAY_BASKET_PROMOTIONS = SalesInvoiceDetailsActivity.class.getName () + ".DISPLAY_BASKET_PROMOTIONS";
	
	/**
	 * Boolean used to determine whether to display the basket promotions UI or not.<br>
	 * This boolean is mainly used to save the invoice state.
	 */
	protected boolean displayBasketPromotions;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #displayPrintingConfirmation}.
	 */
	private static final String PRINTING_TRANSACTION_CODE = SalesInvoiceDetailsActivity.class.getName () + ".PRINTING_TRANSACTION_CODE";
	
	/**
	 * String used to host the transaction code of the transaction to print.
	 */
	private String printingTransactionCode;
	
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #displayClientType}.
	 */
	private static final String DISPLAY_CLIENT_TYPE = SalesInvoiceDetailsActivity.class.getName () + ".DISPLAY_CLIENT_TYPE";
	
	/**
	 * Boolean used to determine whether to display the Client Type.
	 */
	private boolean displayClientType;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #displayPrintingConfirmation}.
	 */
	private static final String DISPLAY_PRINTING_CONFIRMATION = SalesInvoiceDetailsActivity.class.getName () + ".DISPLAY_PRINTING_CONFIRMATION";
private SparseArray < PromotionHeaders > promotionHeadersPricelist;
	
	 
	
	public HashMap < String , ArrayList < PromotionDetails > > _promotionsPricelist;
	
 

	private ArrayList < Integer > headerIDPricelist;
	/**
	 * Boolean used to determine whether to display the printing confirmation.
	 */
	private boolean displayPrintingConfirmation;
	
	/**
	 * Bundle key used to put/retrieve the content of the transaction header code.<br>
	 * This is used mainly to view a sales invoice.
	 */
	public static final String TRANSACTION_HEADER_CODE = SalesInvoiceDetailsActivity.class.getName () + ".TRANSACTION_HEADER_CODE";
	
	/**
	 * Bundle key used to put/retrieve the content of the current call.
	 */
	public static final String CALL = SalesInvoiceDetailsActivity.class.getName () + ".CALL";
	/**
	 * Bundle key used to put/retrieve the content of the current call.
	 */
	public static final String CASH = SalesInvoiceDetailsActivity.class.getName () + ".CASH";

	public static final String PRICE=SalesInvoiceDetailsActivity.class.getName() + ".PRICE";
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #visit}.
	 */
	public static final String VISIT = SalesInvoiceDetailsActivity.class.getName () + ".VISIT";
	
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
	public static final String SELECTED_DIVISIONS = SalesInvoiceDetailsActivity.class.getName () + ".SELECTED_DIVISIONS";
	
	/**
	 * List of string used to host the selected divisions codes, in order to filter the items / sales invoices list.
	 */
	protected ArrayList < String > selectedDivisionsCodes;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #invoices}.
	 */
	public static final String INVOICES = SalesInvoiceDetailsActivity.class.getName () + ".INVOICES";
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.SalesInvoice.Invoice Invoice} objects used to define the sales invoices.<br>
	 * <b>NOTE :</b> <br>
	 * If a filter is applied, this list remains untouched. A new list is generated, filtered and passed to the list adapter.
	 */
	protected ArrayList < Invoice > invoices;
	
	/**
	 * Boolean used to indicate if there saved invoices that should be retrieved.
	 */
	protected boolean retrieveInvoices;
	
	/**
	 * Reference to the invoices list population task.
	 */
	protected static PopulateList populateList;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #searchQuery}.
	 */
	public static final String SEARCH_QUERY = SalesInvoiceDetailsActivity.class.getName () + ".SEARCH_QUERY";
	
	/**
	 * String holding the search query.
	 */
	private String searchQuery;
	
	private String cash;
	/**
	 * List of {@link me.SyncWise.Android.Modules.SalesInvoice.Invoice Invoice} objects used to define the selected sales invoices.
	 */
	protected ArrayList < Invoice > selectedInvoices;
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.SalesInvoice.Invoice Invoice} objects used to define the ordered sales invoices.
	 */
	protected ArrayList < Invoice > orderedInvoices;
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.SalesInvoice.Invoice Invoice} objects used to define the missed sales invoices.
	 */
	protected ArrayList < Invoice > missedInvoices;
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.SalesInvoice.Invoice Invoice} objects used to define the promoted sales invoices.
	 */
	protected ArrayList < Invoice > promotedInvoices;
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.SalesInvoice.Invoice Invoice} objects used to define the free sales invoices.
	 */
	protected ArrayList < Invoice > freeInvoices;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #selectedInvoicesIndex}.
	 */
	public static final String SELECTED_INVOICES_INDEX = SalesInvoiceDetailsActivity.class.getName () + ".SELECTED_INVOICES_INDEX";
	
	/**
	 * List of strings holding the selected invoices' item codes in the order in which the user used.
	 */
	protected ArrayList < String > selectedInvoicesIndex;

	/**
	 * List of {@link me.SyncWise.Android.Modules.SalesInvoice.InvoiceInfo InvoiceInfo} objects used to define the sales invoice details.
	 */
	protected ArrayList < InvoiceInfo > details;
	
	/**
	 * Bundle key used to put/retrieve the content of any other passcode.
	 */
	public static final String OTHER_PASSCODE = SalesInvoiceDetailsActivity.class.getName () + ".OTHER_PASSCODE";
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #passcode}.
	 */
	public static final String PASSCODE = SalesInvoiceDetailsActivity.class.getName () + ".PASSCODE";
	
	/**
	 * String holding the passcode.
	 */
	protected String passcode;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #note}.
	 */
	public static final String NOTE = SalesInvoiceDetailsActivity.class.getName () + ".NOTE";
	
	/**
	 * String holding the sales invoice note.
	 */
	protected String note;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #currency}.
	 */
	public static final String CURRENCY = SalesInvoiceDetailsActivity.class.getName () + ".CURRENCY";
	
	/**
	 * Reference to the currency object.
	 */
	protected Currencies currency;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #transactionHeader}.
	 */
	public static final String TRANSACTION_HEADER = SalesInvoiceDetailsActivity.class.getName () + ".TRANSACTION_HEADER";
	
	/**
	 * Reference to the transaction header stored in DB (if previously saved)
	 */
	protected TransactionHeaders transactionHeader;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #transactionDetails}.
	 */
	public static final String TRANSACTION_DETAILS = SalesInvoiceDetailsActivity.class.getName () + ".TRANSACTION_DETAILS";
	
	/**
	 * List of transaction details references stored in DB (if previously saved)
	 */
	protected ArrayList < TransactionDetails > transactionDetails;
	
	/**
	 * Reference to a {@link me.SyncWise.Android.Widgets.QuickAction.QuickAction QuickAction} used in this activity.
	 */
	private QuickAction quickAction;
	
	
	private ArrayList<InvoiceInfo> list1 = new ArrayList<InvoiceInfo>();
	
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #error}.
	 */
	private static final String ERROR = SalesInvoiceDetailsActivity.class.getName () + ".ERROR";
	
	/**
	 * Boolean used to indicate if an error occurred during the save process.
	 */
	private boolean error;
	
	/**
	 * List of {@linkme.SyncWise.Android.Database.PromotionDetails PromotionDetails} holding the instant promotions mapped to the ordered item code.
	 */
	private HashMap < String , ArrayList < PromotionDetails > > instantPromotions;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #basketPromotions}.
	 */
	public static final String BASKET_PROMOTIONS = SalesInvoiceDetailsActivity.class.getName () + ".BASKET_PROMOTIONS";
	
	/**
	 * List of {@linkme.SyncWise.Android.SalesInvoice.BasketPromotion BasketPromotion} holding the basket promotions.
	 */
	private ArrayList < BasketPromotion > basketPromotions;
	
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
     * Setter - {@link #invoiceModification}
     * 
     * @param invoiceModification	Reference to the {@link me.SyncWise.Android.Modules.SalesInvoice.Invoice Invoice} object being modified.
     */
    public void setInvoiceModification ( final Invoice invoiceModification ) {
    	this.invoiceModification = invoiceModification;
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
    	// Determine if the list view adapter's instance is of SalesInvoiceDetailsAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof SalesInvoiceDetailsAdapter ) {
    		// Return the adapter's money format
    		return ( (SalesInvoiceDetailsAdapter) getListAdapter () ).getMoneyFormat ();
    	} // End if
    	// Determine if the list view adapter's instance is MultipleListAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof MultipleListAdapter ) {
    		// Return the SalesInvoiceDetailsAdapter's money format
    		return ( (SalesInvoiceDetailsAdapter) ( (MultipleListAdapter) getListAdapter () ).getAdapter ( ( (MultipleListAdapter) getListAdapter () ).getCount () - 1 ) )
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
    	// Determine if the list view adapter's instance is of SalesInvoiceDetailsAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof SalesInvoiceDetailsAdapter ) {
    		// Return the adapter's code label
    		return ( (SalesInvoiceDetailsAdapter) getListAdapter () ).getCodeLabel ();
    	} // End if
    	// Determine if the list view adapter's instance is MultipleListAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof MultipleListAdapter ) {
    		// Return the SalesInvoiceDetailsAdapter's code label
    		return ( (SalesInvoiceDetailsAdapter) ( (MultipleListAdapter) getListAdapter () ).getAdapter ( ( (MultipleListAdapter) getListAdapter () ).getCount () - 1 ) )
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
    	// Determine if the list view adapter's instance is of SalesInvoiceDetailsAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof SalesInvoiceDetailsAdapter ) {
    		// Return the adapter's total label
    		return ( (SalesInvoiceDetailsAdapter) getListAdapter () ).getTotalLabel ();
    	} // End if
    	// Determine if the list view adapter's instance is MultipleListAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof MultipleListAdapter ) {
    		// Return the SalesInvoiceDetailsAdapter's total label
    		return ( (SalesInvoiceDetailsAdapter) ( (MultipleListAdapter) getListAdapter () ).getAdapter ( ( (MultipleListAdapter) getListAdapter () ).getCount () - 1 ) )
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
    	// Determine if the list view adapter's instance is of SalesInvoiceDetailsAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof SalesInvoiceDetailsAdapter ) {
    		// Return the adapter's new line character
    		return ( (SalesInvoiceDetailsAdapter) getListAdapter () ).getNewLine ();
    	} // End if
    	// Determine if the list view adapter's instance is MultipleListAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof MultipleListAdapter ) {
    		// Return the SalesInvoiceDetailsAdapter's new line character
    		return ( (SalesInvoiceDetailsAdapter) ( (MultipleListAdapter) getListAdapter () ).getAdapter ( ( (MultipleListAdapter) getListAdapter () ).getCount () - 1 ) )
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
    	// Determine if the list view adapter's instance is of SalesInvoiceDetailsAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof SalesInvoiceDetailsAdapter ) {
    		// Return the adapter's brown color
    		return ( (SalesInvoiceDetailsAdapter) getListAdapter () ).getBrownColor ();
    	} // End if
    	// Determine if the list view adapter's instance is MultipleListAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof MultipleListAdapter ) {
    		// Return the SalesInvoiceDetailsAdapter's brown color
    		return ( (SalesInvoiceDetailsAdapter) ( (MultipleListAdapter) getListAdapter () ).getAdapter ( ( (MultipleListAdapter) getListAdapter () ).getCount () - 1 ) )
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
    	// Determine if the list view adapter's instance is of SalesInvoiceDetailsAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof SalesInvoiceDetailsAdapter ) {
    		// Return the adapter's misty rose color
    		return ( (SalesInvoiceDetailsAdapter) getListAdapter () ).getMistyRose ();
    	} // End if
    	// Determine if the list view adapter's instance is MultipleListAdapter
    	if ( getListAdapter () != null && getListAdapter () instanceof MultipleListAdapter ) {
    		// Return the SalesInvoiceDetailsAdapter's misty rose color
    		return ( (SalesInvoiceDetailsAdapter) ( (MultipleListAdapter) getListAdapter () ).getAdapter ( ( (MultipleListAdapter) getListAdapter () ).getCount () - 1 ) )
    				.getMistyRose ();
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
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.sales_invoice_activity_title ) );
		// Set the activity content from a layout resource.
		setContentView ( R.layout.sales_order_details_activity );
		// Enable fast scrolling
		getListView ().setFastScrollEnabled ( true );
		// Define the empty list view
		getListView ().setEmptyView ( findViewById ( R.id.empty_list_view ) );
		// Display empty list label
		( (TextView) findViewById ( R.id.empty_list_view ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_items_list_label ) );
		// Retrieve a reference to the quaternary view
		View quaternary = findViewById ( R.id.layout_basket_promotions );
		// Define the empty list view
		( (ExpandableListView) quaternary.findViewById ( R.id.basket_promotions_list ) ).setEmptyView ( quaternary.findViewById ( R.id.empty_list_view ) );
		// Set the expandable list view listener
		( (ExpandableListView) quaternary.findViewById ( R.id.basket_promotions_list ) ).setOnItemLongClickListener ( this );
		// Display empty list label
		( (TextView) quaternary.findViewById ( R.id.empty_list_view ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_items_list_label ) );
		
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
		 
		
		//select cash after invoice
		cash = (String)( getIntent ().getSerializableExtra ( CASH ) == null ? "" :getIntent ().getSerializableExtra ( CASH ));
		
		
		//ArrayList<String> list = new ArrayList<String>();
    	InvoiceInfo i = new	InvoiceInfo ( 1 , "1" , "" );
    	
    	list1.add(i);
    	//String a="1";
    	//list.add(a);
		
		
		
        // Perform the quick action setup
        setupQuickAction ();
		
		// Initialize the sales invoice note
		note = "";
		
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
			retrieveInvoices = savedInstanceState.getBoolean ( INVOICES );
			selectedInvoicesIndex = savedInstanceState.getStringArrayList ( SELECTED_INVOICES_INDEX );
			requestCode = savedInstanceState.getInt ( REQUEST_CODE );
			passcode = savedInstanceState.getString ( PASSCODE );
			note = savedInstanceState.getString ( NOTE );
			transactionHeader = (TransactionHeaders) savedInstanceState.getSerializable ( TRANSACTION_HEADER );
			transactionDetails = (ArrayList < TransactionDetails >) savedInstanceState.getSerializable ( TRANSACTION_DETAILS );
			displayInvoiceNote = savedInstanceState.getBoolean ( DISPLAY_INVOICE_NOTE );
			displayBasketPromotions = savedInstanceState.getBoolean ( DISPLAY_BASKET_PROMOTIONS );
			displayPasscode = savedInstanceState.getBoolean ( DISPLAY_PASSCODE );
			displayPrintingConfirmation = savedInstanceState.getBoolean ( DISPLAY_PRINTING_CONFIRMATION );
			printingTransactionCode = savedInstanceState.getString ( PRINTING_TRANSACTION_CODE );
			invoiceModification = (Invoice) savedInstanceState.getSerializable ( INVOICE_MODIFICATION );
			displayClientType=savedInstanceState.getBoolean ( DISPLAY_CLIENT_TYPE );
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
		
		// Hide the sales invoice detail layout
		findViewById ( R.id.layout_order_detail ).setVisibility ( View.GONE );
		// Hide the sales invoice note layout
		findViewById ( R.id.layout_order_note ).setVisibility ( View.GONE );
		// Hide the client reference number layout
		findViewById ( R.id.layout_client_reference_number ).setVisibility ( View.GONE );
		
		// Retrieve all the invoices asynchronously
		populate ();
	}
	
	/**
	 * Retrieve all the invoices asynchronously.
	 */
	protected void populate () {
		// Retrieve all the invoices asynchronously
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
		
		if ( displayClientType )
			// Display printing confirmation
			displayClientTypeConfirmation();
		
		// Check if the printing confirmation should be displayed
		else if ( displayPrintingConfirmation )
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
    	// Save the content of isCreated in the outState bundle
    	outState.putBoolean ( IS_CREATED , isCreated );
    	// Save the content of searchQuery in the outState bundle
    	outState.putString ( SEARCH_QUERY , searchQuery );
    	// Save the content of selectedDivisions in the outState bundle
    	outState.putStringArrayList ( SELECTED_DIVISIONS , selectedDivisionsCodes );

    	new Thread ( new Runnable () {
			@Override
			public void run () {
				// Save the content of the invoices using GSON
				ActivityInstance.saveDataGson ( SalesInvoiceDetailsActivity.this , SalesInvoiceDetailsActivity.class.getName () , INVOICES , invoices );
			}
		} ).start ();
    	new Thread ( new Runnable () {
			@Override
			public void run () {
				// Save the content of the basketPromotions using GSON
				ActivityInstance.saveDataGson ( SalesInvoiceDetailsActivity.this , SalesInvoiceDetailsActivity.class.getName () , BASKET_PROMOTIONS , basketPromotions );
			}
		} ).start ();
		// Indicate that there is saved invoices data
		outState.putBoolean ( INVOICES , true );
    	
    	// Save the content of requestCode in the outState bundle
    	outState.putInt ( REQUEST_CODE , requestCode );
    	// Save the content of selectedInvoicesIndex in the outState bundle
    	outState.putStringArrayList ( SELECTED_INVOICES_INDEX , selectedInvoicesIndex );
    	if ( passcode != null )
	    	// Save the content of passcode in the outState bundle
	    	outState.putString ( PASSCODE , passcode );
    	// Save the content of note in the outState bundle
    	outState.putString ( NOTE , note );
    	// Save the content of transactionHeader in the outState bundle
    	outState.putSerializable ( TRANSACTION_HEADER , transactionHeader );
    	// Save the content of transactionDetails in the outState bundle
    	outState.putSerializable ( TRANSACTION_DETAILS , transactionDetails );
    	// Save the content of invoiceModification in the outState bundle
    	outState.putSerializable ( INVOICE_MODIFICATION , invoiceModification );
    	// Save the content of displayInvoiceNote in the outState bundle
    	outState.putBoolean ( DISPLAY_INVOICE_NOTE , displayInvoiceNote );
    	// Save the content of displayBasketPromotions in the outState bundle
    	outState.putBoolean ( DISPLAY_BASKET_PROMOTIONS , displayBasketPromotions );
    	// Save the content of displayPasscode in the outState bundle
    	outState.putBoolean ( DISPLAY_PASSCODE , displayPasscode );
    	// Save the content of displayPrintingConfirmation in the outState bundle
    	outState.putBoolean ( DISPLAY_PRINTING_CONFIRMATION , displayPrintingConfirmation );
    	// Save the content of CLIENT_TYPE in the outState bundle
    	outState.putBoolean ( DISPLAY_CLIENT_TYPE,displayClientType );
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
		// Determine if the invoice note is undergoing any modifications
		if ( displayInvoiceNote ) {
			// Reset flag
			displayInvoiceNote = false;
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
		// Determine if the basket promotions is undergoing any modifications
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
				setListAdapter ( getSalesInvoiceDetailsAdapter ( R.layout.sales_order_details_activity_item , new ArrayList < Invoice > () , true , true , Invoice.STATE_DEFAULT ) );
				// Set a new adapter based on the saved filter, asynchronously
				new FilterList ( searchQuery ).execute ();
    		} // End else
    	} // End else if
		// Determine if this is a new sales invoice with selected invoices
		// OR a previously saved sales invoice with modifications
    	else if ( ( getIntent ().getIntExtra ( REQUEST_CODE , -1 ) == REQUEST_CODE_NEW && hasSalesInvoices () )
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
								SalesInvoiceDetailsActivity.this.finish ();
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
			invoices = null;
			invoiceModification = null;
			basketPromotions = null;
			selectedInvoices = null;
			orderedInvoices = null;
			missedInvoices = null;
			freeInvoices = null;
			promotedInvoices = null;
			selectedInvoicesIndex = null;
			selectedDivisionsCodes = null;
			printingTransactionCode = null;
			passcode = null;
			note = null;
			details = null;
			transactionHeader = null;
			transactionDetails = null;
		} // End if
	}
	
	
	/**
	 * Displays a confirmation dialog used to prompt the user to Client Type the current transaction.
	 */
	 
	private void displayClientTypeConfirmation () {
		String description = AppResources.getInstance ( this ).getString ( this , R.string.Credit_invoice );
		if ( ClientCard.isCashClient ( ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClient () ) ) 
			description = AppResources.getInstance ( this ).getString ( this , R.string.Cash_invoice );
		AppDialog.getInstance ().displayAlert ( this ,
				null ,
				description ,
				AppDialog.ButtonsType.OK ,new DialogInterface.OnClickListener () {
			@Override
			public void onClick ( DialogInterface dialog , int which ) {
				// Determine the clicked button
				switch ( which ) {
				case DialogInterface.BUTTON_NEUTRAL: 
					displayPrintingConfirmation = true;
					// Display printing confirmation
					displayPrintingConfirmation ();
					displayClientType=false;
					break;
				} // End switch
			} } );
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
							Intent intent = new Intent ( SalesInvoiceDetailsActivity.this, PrintingActivity.class );
							intent.putExtra ( PrintingActivity.TYPE , PrintingActivity.Type.INVOICE );
							intent.putExtra ( PrintingActivity.PRINTOUT , PrintingActivity.Printout.ORIGINAL );
							intent.putExtra ( PrintingActivity.HEADER , new ArrayList < TransactionHeaders > ( DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getTransactionHeadersDao ().queryBuilder ()
									.where ( TransactionHeadersDao.Properties.TransactionCode.eq ( transactionCode )  ).list () ) );
							startActivityForResult ( intent , REQUEST_CODE_PRINT );
							// Reset flag
							displayPrintingConfirmation = false;
							break;
						case DialogInterface.BUTTON_NEGATIVE:
					    	// Call this to set the result that your activity will return to its caller
							intent = new Intent();
							intent.putExtra ( SalesInvoiceActivity.SAVE_SUCCESS , ! error );
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
		if ( displayInvoiceNote || displayPasscode || displayBasketPromotions )
			// Do nothing
			return;
		
		// Retrieve the selected object
		Object object = ( (BaseAdapter) getListAdapter () ).getItem ( position );
		// Check if the selected object is an invoice
		if ( object instanceof Invoice )
			// Retrieve the selected invoice
			invoiceModification = (Invoice) object;
		else
			// Invalid object
			// Exit method
			return;
		
		// Determine if the request code is info
    	// AND if the view only flag is provided
    	if ( requestCode == REQUEST_CODE_INFO && ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) ) {
			// Check if the invoice state is ordered
			if ( ( (ViewHolder) view.getTag () ).invoiceState == Invoice.STATE_ORDERED )
	    		// Display the quick action widget
	    		quickAction.show ( view , invoiceModification , getResources () );
			// Otherwise check if the invoice state is free
			else if ( ( (ViewHolder) view.getTag () ).invoiceState == Invoice.STATE_FREE ) {
				// Retrieve a reference to the view holder
				ViewHolder viewHolder = (ViewHolder) view.getTag ();
				// Retrieve a reference to the invoice
				final Invoice invoice = viewHolder.invoice;
				// Retrieve the previous free quantity
				final int oldQuantity = invoice.getBasicUnitQuantity ( Invoice.STATE_FREE );
				// Retrieve the maximum allowed free quantity
				final int allowedQuantity = invoice.getAllowedFreeQuantity ();
				// Declare and initialize an alert dialog builder
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder ( this );
				// By default, the alert dialog cannot be cancelled UNLESS it has no buttons
				alertDialogBuilder.setCancelable ( false );
				// Create view
				final EditText editText = new EditText ( this );
				editText.setId ( 1 );
				editText.setInputType ( InputType.TYPE_CLASS_NUMBER );
				String displayText = oldQuantity <= 0 ? "" : String.valueOf ( oldQuantity );
				editText.setText ( displayText );
				editText.setSelection ( displayText.length () );
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
							if ( newQuantity > allowedQuantity )
								AppDialog.getInstance ().displayAlert ( SalesInvoiceDetailsActivity.this , 
										AppResources.getInstance ( SalesInvoiceDetailsActivity.this ).getString ( SalesInvoiceDetailsActivity.this , R.string.warning_label ) ,
										"The free quantity cannot exceed " + allowedQuantity + "." ,
										ButtonsType.OK , null );
							else {
								// Set the new free quantity
								invoice.setReflectedFreeQuantity ( newQuantity );
								// Refresh the list
								( (MultipleListAdapter) getListAdapter () ).notifyDataSetChanged ();
							}
						case DialogInterface.BUTTON_NEGATIVE:
				    		// Hide the software keyboard
				            ( (InputMethodManager) getBaseContext ().getSystemService ( INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow ( editText.getWindowToken (), 0 );
							break;
						} // End switch
					}
				};
				// Setup dialog
				alertDialogBuilder.setTitle ( R.string.invoice_free_quantity_label );
				alertDialogBuilder.setView ( editText );
				alertDialogBuilder.setPositiveButton ( AppResources.getInstance ( this ).getString ( this , R.string.ok_label ) , onClickListener );
				alertDialogBuilder.setNegativeButton ( AppResources.getInstance ( this ).getString ( this , R.string.cancel_label ) , onClickListener );
				alertDialogBuilder.create ();
				alertDialogBuilder.show (); 
			} // End else if
    	} // End if
	}
	
	/*
	 * Callback method to be invoked when an action item in this QuickAction has been clicked.
	 *
	 * @see me.SyncWise.Android.Widgets.QuickAction.QuickAction.OnActionItemClickListener#onItemClick(me.SyncWise.Android.Widgets.QuickAction.QuickAction, int, int)
	 */
	@Override
	public void onItemClick ( QuickAction source , View anchor , Object object , int pos , int actionId ) {
		// Retrieve the selected invoice
		final Invoice selectedInvoice = (Invoice) object;
		// Retrieve a reference to the view holder
		ViewHolder viewHolder = (ViewHolder) anchor.getTag ();
		
		// Determine the clicked action item
		switch ( actionId ) {
		case ActionItemID.REMOVE:
			// Check if the invoice is ordered and does not have missed quantities
			if ( viewHolder.invoiceState == Invoice.STATE_ORDERED && ! selectedInvoice.hasValidQuantities ( Invoice.STATE_MISSED ) ) {
				// Removed the ordered invoice
				removeOrderedInvoice ( selectedInvoice );
				// Refresh the selected invoices list
				refreshSelectedInvoices ();
				// Refresh sales invoice details
				refreshDetails ();
				// Check if the request code is INFO
				// AND if the selected invoices list is empty
				if ( requestCode == REQUEST_CODE_INFO && ( selectedInvoices == null || selectedInvoices.isEmpty () ) )
					// Emulate a back key press
					onBackPressed ();
				else
					// Refresh the list
					setMultipleAdapter ();
			} // End else if
			else {
				// Prompt user 
				AppDialog.getInstance ().displayAlert ( this , null , AppResources.getInstance ( this ).getString ( this , R.string.sales_invoice_remove_missed_quantities_message ) , AppDialog.ButtonsType.YesNo , new DialogInterface.OnClickListener () {
					@Override
					public void onClick ( DialogInterface dialog , int which ) {
						// Determine the clicked button
						switch ( which ) {
						case DialogInterface.BUTTON_POSITIVE:
							// Removed the ordered invoice
							removeOrderedInvoice ( selectedInvoice );
							// Refresh the selected invoices list
							refreshSelectedInvoices ();
							// Refresh sales invoice details
							refreshDetails ();
							// Check if the request code is INFO
							// AND if the selected invoices list is empty
							if ( requestCode == REQUEST_CODE_INFO && ( selectedInvoices == null || selectedInvoices.isEmpty () ) )
								// Emulate a back key press
								onBackPressed ();
							else
								// Refresh the list
								setMultipleAdapter ();
							break;
						case DialogInterface.BUTTON_NEGATIVE:
							// Dismiss dialog
							AppDialog.getInstance ().dismiss ();
							break;
						} // End switch
					}
				} );
			} // End else
			break;
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
	 * Removes the provided ordered invoice quantities.
	 * 
	 * @param invoice	The ordered {@link me.SyncWise.Android.Modules.SalesInvoice.Invoice Invoice} to remove.
	 */
	private void removeOrderedInvoice ( final Invoice invoice ) {
		// Clear quantities
		invoice.setQuantityBig ( 0 );
		invoice.setQuantityMedium ( 0 );
		invoice.setQuantitySmall ( 0 );
		if ( selectedInvoicesIndex.contains ( invoice.getItem ().getItemCode () )  )
			selectedInvoicesIndex.remove ( invoice.getItem ().getItemCode () );
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
	 * Initializes the sales invoice note (secondary) view.
	 * 
	 * @param restore	Boolean used to determine if the tertiary view is being initialized for the first time or is being restored.
	 */
	protected void initializeSecondaryView ( final boolean restore ) {
		// Retrieve a reference to the parent view
		View parent = findViewById ( R.id.layout_order_note );
		// Retrieve a reference to the sales invoice note edit text
		EditText noteEditText = (EditText) parent.findViewById ( R.id.edittext_sales_order_note );		
		// Retrieve a reference to the sales invoice note title label
		TextView titleLabel = (TextView) parent.findViewById ( R.id.label_sales_order_note );
		
		// Display the sales invoice note title label
		titleLabel.setText ( AppResources.getInstance ( this ).getString ( this , R.string.sales_order_note_title ) );
		// Enable the save icon
		parent.findViewById ( R.id.icon_save_note ).setEnabled ( true );
		// Enable edit text
		noteEditText.setEnabled ( true );
		// Display the field hint
		noteEditText.setHint ( AppResources.getInstance ( this ).getString ( this , R.string.sales_invoice_note_hint ) );
		// Set the maximum number of allowed characters
		noteEditText.setFilters ( new InputFilter [] { new InputFilter.LengthFilter ( TransactionHeadersUtils.getFreeRemarksMaxLength () ) } );
		// Check if the view is being restored
		if ( ! restore )
			// Set the sales invoice note
			noteEditText.setText ( note );
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
		message = "Client Code: "+((Call) getIntent ().getSerializableExtra ( CALL ) ).getClient ().getClientCode()+"\n"+message; 
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
	 * The invoice modifications are saved.
	 * 
	 * @param view	The clicked view.
	 */
	public void updateInvoice ( View view ) {
		// Update the invoice / UI
		onInvoiceModificationResult ();
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
		
		// Validate pass code
		//if ( ! UserPasswordsUtils.validateTimePasswordClients ( this , UserPasswordsUtils.TransactionHeaderType.TRANSACTION , TransactionHeadersUtils.Type.SALES_INVOICE , passcode  ,( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClient ().getClientCode() ) ) {
		if ( ! UserPasswordsUtils.validateTimePasswordClientsCreditLimit( this , UserPasswordsUtils.TransactionHeaderType.TRANSACTION , TransactionHeadersUtils.Type.SALES_INVOICE , passcode  ,( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClient ().getClientCode() ) ) {
			
		// Reset passcode
			passcode = null;
			// Indicate that the passcode is not valid
			Baguette.showText ( SalesInvoiceDetailsActivity.this ,
					AppResources.getInstance ( SalesInvoiceDetailsActivity.this ).getString ( SalesInvoiceDetailsActivity.this , R.string.time_passcode_invalid_message ) ,
					Baguette.BackgroundColor.RED );
			// Exit method
			return;
		} // End if
		
		// Reset flag
		displayPasscode = false;
		
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
		saveTransaction ();
	}
	
	/**
	 * Called when a view has been clicked.<br>
	 * The invoice note is saved.
	 * 
	 * @param view	The clicked view.
	 */
	public void updateNote ( View view ) {
		// Determine if the invoice note is undergoing any modifications
		if ( ! displayInvoiceNote )
			// No modifications
			return;
		
		// Reset flag
		displayInvoiceNote = false;
		
		// Retrieve a reference to the secondary view
		View secondary = findViewById ( R.id.layout_order_note );
		// Retrieve a reference to the sales invoice note edit text
		EditText noteEditText = (EditText) secondary.findViewById ( R.id.edittext_sales_order_note );
		// Store the sales invoice note
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
		
		// Update the sales invoice details list with the new note
		onNoteModificationResult ();
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
			intent.putExtra ( SalesInvoiceActivity.SAVE_SUCCESS , ! error );
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
	 * Called after a successful invoice modification : price or quantity modification.<br>
	 * The corresponding object is modified in the main list and the appropriate action is performed (based on the current request code).
	 */
	private void onInvoiceModificationResult () {
		// Retrieve a reference to the sales invoice object from the main list
		Invoice invoice = null;
		// Iterate over all the invoices
		for ( int i = 0 ; i < invoices.size () ; i ++ )
			// Match the invoice
			if ( invoices.get ( i ).getItem ().getItemCode ().equals ( invoiceModification.getItem ().getItemCode () ) ) {
					// Update the selected invoice position
				invoice = invoices.get ( i );
				// Exit loop
				break;
			} // End if
		// Check if the invoice object is valid
		if ( invoice == null )
			// Invalid object
			return;
		// Check if the item has zero quantities
		if ( invoice.getQuantityBig () == 0
				&& invoice.getQuantityMedium () == 0
				&& invoice.getQuantitySmall () == 0 ) {
			// Check if the selected invoices list is valid
			if ( selectedInvoices != null )
				// Remove item from the selected invoices
				selectedInvoices.remove ( invoice );
			// Check if the selected invoices item codes list contains the appropriate item code
			if ( selectedInvoicesIndex.contains ( invoice.getItem ().getItemCode () )  )
				// Remove the index
				selectedInvoicesIndex.remove ( invoice.getItem ().getItemCode () );
		} // End if
		else {
			// Otherwise the item has non-zero quantities
			// Check if the selected invoices item codes list does NOT contain the appropriate item code
			if ( ! selectedInvoicesIndex.contains ( invoice.getItem ().getItemCode () )  )
				// Add the index
				selectedInvoicesIndex.add ( invoice.getItem ().getItemCode () );
		} // end else

		// Refresh sales invoice details
		refreshDetails ();
		// Check if the request code is INFO
		// AND if the selected invoices list is empty
		if ( SalesInvoiceDetailsActivity.this.requestCode == REQUEST_CODE_INFO && selectedInvoices != null && selectedInvoices.isEmpty () )
			// Emulate a back key press
			onBackPressed ();
	}
	
	/**
	 * Called after a successful note modification.<br>
	 * The note in the sales invoice details list is updated.
	 */
	private void onNoteModificationResult () {
		// Iterate over all details
		for ( InvoiceInfo detail : details )
			// Check if the current detail is about the sales invoice note
			if ( detail.getId () == InvoiceInfo.ID.NOTE ) {
				// Update the sales invoice note
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
    	// Check if the list of invoices is valid
    	// OR if a note is undergoing modifications
    	if ( invoices == null || displayInvoiceNote || displayPasscode || displayBasketPromotions )
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
		//	 me.SyncWise.Android.Utilities.MenuItem.enable(menu.findItem(R.id.action_refresh));
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
            	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_keyboard ) );
    			me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_remove ) );
    		} // End if
    	} // End else
    	else
    		// Invoice can be printed in view mode only
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
    	// Check if the invoices list is valid
    	if ( invoices == null )
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
    	// Determine if the action list is selected
//    	if ( menuItem.getItemId () == R.id.action_refresh ) {
//    		// Create a new intent to start a new activity
//    		if(isStock==true)
//    		{ 
//    		//	populate ();
//    		  new	FilterListStock( searchQuery ).execute() ;
//    		   isStock=false;
//    		}
//    		else{
//    			populate ();
//    			isStock=true;
//    		}
//    		// Consume event
//    		return true;
//    	} // End if
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
								Intent intent = new Intent ( SalesInvoiceDetailsActivity.this, PrintingActivity.class );
								intent.putExtra ( PrintingActivity.TYPE , PrintingActivity.Type.INVOICE );
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
            
            // Refresh the selected invoices list
            if ( ! refreshSelectedInvoices () ) {
    			// Indicate that there are no sales invoices
    			Baguette.showText ( SalesInvoiceDetailsActivity.this ,
    					AppResources.getInstance ( SalesInvoiceDetailsActivity.this ).getString ( SalesInvoiceDetailsActivity.this , R.string.no_sales_invoice_message ) ,
    					Baguette.BackgroundColor.RED );
        		// Consume event
        		return true;
            } // End if
            
			// Initialize the sales invoice details
			initializeDetails ();

			// Set the multiple adapter
			setMultipleAdapter ();
			
    		// Consume event
    		return true;
    	} // End else if
    	// Determine if the action keyboard is selected
    	else if ( menuItem.getItemId () == R.id.action_keyboard ) {
    		// Set flag
    		displayInvoiceNote = true;
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
    	// Determine if the action mark all is selected
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
					    			DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getTransactionHeadersDao ().update ( transactionHeader );
									// Update target coverage (if any)
			    					TargetUpdate.updateCoverage ( SalesInvoiceDetailsActivity.this , TargetHeadersUtils.Type.TOTAL_SALES , null , -1 , null , -1 , null , null , UpdateType.DECREASE , transactionHeader.getTotalTaxAmount () , transactionHeader.getCurrencyCode () );
									// Update target coverage (if any)
			    					TargetUpdate.updateCoverage ( SalesInvoiceDetailsActivity.this , TargetHeadersUtils.Type.TOTAL_SALES_PER_CLIENT , transactionHeader.getClientCode () , TargetHeadersUtils.SubType.CLIENT , null , -1 , transactionHeader.getCompanyCode () , transactionHeader.getDivisionCode () , UpdateType.DECREASE , transactionHeader.getTotalTaxAmount () , transactionHeader.getCurrencyCode () );
									// Update target coverage (if any)
			    					TargetUpdate.updateCoverage ( SalesInvoiceDetailsActivity.this , TargetHeadersUtils.Type.TOTAL_SALES_PER_CLIENT_PER_BRAND , transactionHeader.getClientCode () , TargetHeadersUtils.SubType.CLIENT , transactionHeader.getDivisionCode () , TargetHeadersUtils.SubType.DIVISION , transactionHeader.getCompanyCode () , transactionHeader.getDivisionCode () , UpdateType.DECREASE , transactionHeader.getTotalTaxAmount () , transactionHeader.getCurrencyCode () );
					    			// Check if the transaction details list is valid
					    			if ( transactionDetails != null )
					    				// Iterate over all transaction details
					    				for ( TransactionDetails transactionDetail : transactionDetails )
											// Update target coverage (if any)
					    					TargetUpdate.updateCoverage ( SalesInvoiceDetailsActivity.this , TargetHeadersUtils.Type.TOTAL_SALES_PER_CLIENT_PER_BRAND , transactionHeader.getClientCode () , TargetHeadersUtils.SubType.CLIENT , transactionDetail.getItemCode () , TargetHeadersUtils.SubType.ITEM , transactionHeader.getCompanyCode () , transactionHeader.getDivisionCode () , UpdateType.DECREASE , transactionDetail.getTotalLineAmount () , transactionHeader.getCurrencyCode () );
					            	// Call this to set the result that your activity will return to its caller
					            	setResult ( RESULT_OK , new Intent ().putExtra ( SalesInvoiceActivity.DELETE_SUCCESS , true ) );
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
    		// Check if there is a previously stored sales invoice
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
     * Refreshes the selected invoices lists.
     * 
     * @return	Boolean indicating if the list was successfully refreshed or not.
     */
    private boolean refreshSelectedInvoices () {
        // Determine if there is at least one basket ordered
        boolean basketQuantities = false;
        for ( BasketPromotion basketPromotion : basketPromotions )
        	if ( basketPromotion.getQuantity () > 0 ) {
        		basketQuantities = true;
        		break;
        	} // End if
		// Declare and initialize a list used to host the valid sales invoices
		ArrayList < Invoice > salesInvoice = new ArrayList < Invoice > ();
		// Map the invoices
		HashMap < String , Invoice > mappedInvoices = new HashMap < String , Invoice > ();
        // Iterate over all the invoices
        for ( Invoice invoice : invoices ) {
        	//invoice.setBasketSmall(0);
        	
        	// Map the current item
			mappedInvoices.put ( invoice.getItem ().getItemCode () , invoice );
			// Clear previous instant promotions
			invoice.clearInstantPromotions ();
			invoice.restoreDetailDiscountPercentage ();
			//invoice.setBasketSmall(0);
			// Check if the current invoice has valid quantities
			if ( invoice.hasValidQuantities ( Invoice.STATE_DEFAULT ) ) {
				// The invoice contains at least one quantity, add it to the sales invoice list
				salesInvoice.add ( invoice );
			} // End if
        } // End for each
		selectedInvoices = salesInvoice;
		// Sort the invoices based on their indexes
		Collections.sort ( salesInvoice , new Comparator < Invoice > () {
			@Override
			public int compare ( Invoice invoice1 , Invoice invoice2 ) {
				// Sort the invoices based on their indexes
				return selectedInvoicesIndex.indexOf ( invoice1.getItem ().getItemCode () ) - selectedInvoicesIndex.indexOf ( invoice2.getItem ().getItemCode () );
			}
		} );
        
        // Compute instant promotion quantities and discounts
        refreshInstantPromotions ( mappedInvoices );
       
       int count=0; 
//        for ( BasketPromotion basketPromotion : basketPromotions )
//        	// Check if the basket is ordered
//        	if ( basketPromotion.getQuantity () > 0 )  
//        		{
//        		count=count+1;
//        		selectedInvoices
//        		}
        		// Iterate over the basket promotions
        for ( BasketPromotion basketPromotion : basketPromotions )
        	// Check if the basket is ordered
        	if ( basketPromotion.getQuantity () > 0 ){
        		// Iterate over the promotion details
        		for ( PromotionDetails promotionDetail : basketPromotion.getPromotionDetails () ) {
        			// Retrieve the appropriate invoice
        			Invoice invoice = mappedInvoices.get ( promotionDetail.getOrderedItemCode () );
        			// Set the quantity
        			invoice.setQuantityPromotedBasket ( (int) ( basketPromotion.getQuantity () * promotionDetail.getBasicOrderedQuantity () ) );
        			// Set the discount percentage
        			invoice.setPromotionDiscountPercentageBasket ( promotionDetail.getDiscountPercentage () );
        			// Check if the invoice is previously added to the list
        			if ( ! selectedInvoices.contains ( invoice ) )
        				// Add the invoice to the list
        				selectedInvoices.add ( invoice );
        			
        			
        		} // End for each
        	}
        	 else{
        		for ( PromotionDetails promotionDetail : basketPromotion.getPromotionDetails () ) {
        			// Retrieve the appropriate invoice
        			Invoice invoice = mappedInvoices.get ( promotionDetail.getOrderedItemCode () );
        			// Set the quantity
        			invoice.setQuantityPromotedBasket ( (int) ( basketPromotion.getQuantity () * promotionDetail.getBasicOrderedQuantity () ) );
        			if(invoice.getBasketSmall()>0)
        			{		// Set the discount percentage
        			
        			invoice.setPromotionDiscountPercentageBasket (0 );
        			invoice.setBasketSmall(0);
        			//invoice.setQuantitySmall(0);
        			//invoice.setPriceSmall(0);
        			
        			// Check if the invoice is previously added to the list
        			if ( ! selectedInvoices.contains ( invoice ) )
        				// Add the invoice to the list
        				selectedInvoices.add ( invoice );
        			}
        			} // End for each
        		
        }

        
		// Check if there is at least one valid sales invoice
		if ( salesInvoice.isEmpty () && ! basketQuantities ) {
			// Clear lists
			selectedInvoices = null;
			freeInvoices = null;
			// Indicate that the lists cannot be refreshed
    		return false;
		} // End if
		// Otherwise there is at least one sales invoice
		// Set the new request code
		requestCode = REQUEST_CODE_INFO;
		// Refresh the action bar
		invalidateOptionsMenu ();
		// Set the selected invoices list
		orderedInvoices = new ArrayList < Invoice > ();
		missedInvoices = new ArrayList < Invoice > ();
		promotedInvoices = new ArrayList < Invoice > ();
		for ( Invoice selectedInvoice : selectedInvoices ) {
			selectedInvoice.refreshInvoiceState ();
			if ( selectedInvoice.hasValidQuantities ( Invoice.STATE_ORDERED ) )
				orderedInvoices.add ( selectedInvoice );
			if ( selectedInvoice.hasValidQuantities ( Invoice.STATE_MISSED ) )
				missedInvoices.add ( selectedInvoice );
			//if(count>0)
			if ( selectedInvoice.hasValidQuantities ( Invoice.STATE_BASKET ) )
				promotedInvoices.add ( selectedInvoice );
		//	else
			//	promotedInvoices.removeAll(collection)
			if ( ! selectedInvoice.hasValidQuantities ( Invoice.STATE_FREE ) && freeInvoices.contains ( selectedInvoice ) )
				freeInvoices.remove ( selectedInvoice );
		} // End for each
		// Indicate that the lists were successfully refreshed
		return true;
    }
    
    /**
     * Refreshes the instant promotion calculations and populates the lists accordingly.
     * 
     * @param mappedInvoices	Map hosting all the invoices list mapped to their appropriate item code.
     */
    private void refreshInstantPromotions ( final HashMap < String , Invoice > mappedInvoices ) {
    	// Set the free invoices list
    	freeInvoices = new ArrayList < Invoice > ();
    	// Make sure the lists are valid
    	if ( mappedInvoices != null && selectedInvoices != null && ! selectedInvoices.isEmpty () && instantPromotions != null && ! instantPromotions.isEmpty () ) {
    		// Iterate over the selected invoices
    		for ( Invoice invoice : selectedInvoices ) {
    			// Retrieve the list of applied promotions for the current invoiced item
    			ArrayList < PromotionDetails > instantPromotionDetails = instantPromotions.get ( invoice.getItem ().getItemCode () );
    			// Check if the list is valid
    			if ( instantPromotionDetails != null && ! instantPromotionDetails.isEmpty () ) {
    				// Iterate over the instant promotions
    				for ( PromotionDetails promotionDetail : instantPromotionDetails ) {
    					// Reference to the applied invoice
    					Invoice appliedInvoice = invoice;
    					// Check if the promotion is for the current item
    					if ( promotionDetail.getOfferedItemCode () != null && ! promotionDetail.getOfferedItemCode ().equals ( promotionDetail.getOrderedItemCode () ) ) 
    						// Set the applied invoice
    						appliedInvoice = mappedInvoices.get ( promotionDetail.getOfferedItemCode () );
    					// Check if the applied invoice is valid
    					if ( appliedInvoice == null )
    						// Skip the current promotion detail
    						continue;
    					// Check if the ordered quantity has reached the required quantity
    					if ( invoice.getBasicUnitQuantity ( Invoice.STATE_DEFAULT ) < promotionDetail.getBasicOrderedQuantity () )
    						// Skip the current promotion detail
    						continue; 
						// Check if the promotion offers discount
						if ( promotionDetail.getPromotionDetailType ().equals ( PromotionUtils.DetailType.DISCOUNT_PERCENTAGE ) ) {
							// Set the discount percentage
							appliedInvoice.setPromotionDiscountPercentage ( appliedInvoice.getPromotionDiscountPercentage () + promotionDetail.getDiscountPercentage () );
							// Check if the original discount is applied
							if ( ! promotionDetail.getApplyOriginalDiscount ().equals ( StatusUtils.isAvailable () ) )
								// Clear previous discount
								appliedInvoice.clearDetailDiscountPercentage ();
						} // End if
						// Otherwise check if the promotion offers quantities
						else if ( promotionDetail.getPromotionDetailType ().equals ( PromotionUtils.DetailType.OFFERED_QUANTITY ) ) {
							// Add the free quantities
							appliedInvoice.setQuantityPromotedFree ( appliedInvoice.getQuantityPromotedFree () + (int) ( ( (int) ( invoice.getBasicUnitQuantity ( Invoice.STATE_DEFAULT ) / promotionDetail.getBasicOrderedQuantity () ) ) * promotionDetail.getBasicOfferedQuantity () ) );
							// Check if the original discount is applied
							if ( ! promotionDetail.getApplyOriginalDiscount ().equals ( StatusUtils.isAvailable () ) )
								// Clear previous discount
								appliedInvoice.clearDetailDiscountPercentage ();
							// Check if the invoice is already added to the free invoices list
							if ( ! freeInvoices.contains ( appliedInvoice ) )
								// Add the free invoice to the list
								freeInvoices.add ( appliedInvoice );
						} // End if
    				} // End for each
    			} // End if
    		} // End for each
    		// Iterate over the free invoices
    		for ( Invoice invoice : freeInvoices )
				// Check if the invoice is already added to the selected invoices list
				if ( ! selectedInvoices.contains ( invoice ) )
					// Add the free invoice to the list
					selectedInvoices.add ( invoice );
    	} // End if
    }

    /**
     * Sets the multiple adapter based on the selected invoices list.
     */
    private void setMultipleAdapter () {
    
    	
		// Declare and initialize a multiple list adapter
		MultipleListAdapter adapter = new MultipleListAdapter ( this );
		// Add the sales invoice details adapter
		adapter.addSection ( new Section ( AppResources.getInstance ( this ).getString ( this , R.string.sales_invoice_details_label ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
				new InvoiceInfoAdapter ( this , R.layout.order_info_item , details ) );
		 
		if(transactionHeader!=null)
		{	
		InvoiceInfo i1 = new	InvoiceInfo ( 1 , "1" , transactionHeader.getInfo1() );
		list1.clear();
		list1.add(i1);
		}
		if ( PermissionsUtils.getEnableCashInvoiceCredit ( this , DatabaseUtils.getCurrentUserCode ( this ) , DatabaseUtils.getCurrentCompanyCode ( this ) ) )
			
		if(ClientCard.isCreditClient(( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClient()))
		
		adapter.addSection ( new Section ( AppResources.getInstance ( this ).getString ( this , R.string.sales_invoice_details_label ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
				new CashInvoiceAdapter( this , R.layout.cash_invoice ,list1 ) );
		// Check if there is at least one ordered invoice
		if ( ! orderedInvoices.isEmpty () )
			// Add the sales invoice adapter
			adapter.addSection ( new Section ( AppResources.getInstance ( this ).getString ( this , R.string.sales_invoice_products_label ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 255 , 0 ) , null ) ,
					getSalesInvoiceDetailsAdapter ( R.layout.sales_order_details_activity_item , orderedInvoices , ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) , false , Invoice.STATE_ORDERED ) );
		// Check if there is at least one promoted invoice
		if ( ! promotedInvoices.isEmpty () )
			// Add the sales invoice adapter
			adapter.addSection ( new Section ( AppResources.getInstance ( this ).getString ( this , R.string.sales_invoice_promoted_label ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 255 , 0 ) , null ) ,
					getSalesInvoiceDetailsAdapter ( R.layout.sales_order_details_activity_item , promotedInvoices , ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) , false , Invoice.STATE_BASKET ) );
		// Check if there is at least one free invoice
		if ( ! freeInvoices.isEmpty () )
			// Add the sales invoice adapter
			adapter.addSection ( new Section ( AppResources.getInstance ( this ).getString ( this , R.string.sales_invoice_free_label ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 255 ) , null ) ,
					getSalesInvoiceDetailsAdapter ( R.layout.sales_order_details_activity_item , freeInvoices , ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) , false , Invoice.STATE_FREE ) );
		// Check if there is at least one missed invoice
		if ( ! missedInvoices.isEmpty () )
			// Add the sales invoice adapter
			adapter.addSection ( new Section ( AppResources.getInstance ( this ).getString ( this , R.string.sales_invoice_missed_label ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 255 , 0 , 0 ) , null ) ,
					getSalesInvoiceDetailsAdapter ( R.layout.sales_order_details_activity_item , missedInvoices , ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false ) , false , Invoice.STATE_MISSED ) );
		// Set the list adapter
		setListAdapter ( adapter );
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
   		alertDialogBuilder.setMessage ( R.string.cannot_start_invoice_credit_client_message );
   		// Map the positive and negative buttons
   		alertDialogBuilder.setPositiveButton ( R.string.Cash_label , new DialogInterface.OnClickListener() {
   			@Override
   			public void onClick ( DialogInterface dialog , int which ) {
   	    		// Set flag
   				cash="1";
   				saveTransaction ();
   				// Start the new activity
   				//startActivityForResult ( getIntent_SalesInvoiceDetailsActivity_New ( null,"cash" ) , SalesInvoiceDetailsActivity.REQUEST_CODE_NEW );
   				// Specify an explicit transition animation to perform next
   				//ActivityTransition.SlideOutLeft ( SalesInvoiceActivity.this );
   			}	
   			} );
   	    final String description = AppResources.getInstance ( this ).getString ( this , R.string.sales_invoice_passcode_required_message );
	    	
   		alertDialogBuilder.setNegativeButton ( R.string.use_passcode , new DialogInterface.OnClickListener() {
   			@Override
   			public void onClick ( DialogInterface dialog , int which ) {
   	    		// Set flag
   				displayPasscode = true;
   			    // Disable the main list
	    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( false );
   	    			// Initialize the tertiary view
   	    		initializeTertiaryView ( description );
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
   	
   	private String cashCredit="";
    /* Displays a warning regarding unpaid invoices for credit client.<br>
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
  		alertDialogBuilder.setMessage ( R.string.cannot_start_invoice_zero_credit_client_message );
  		// Map the positive and negative buttons
  		alertDialogBuilder.setPositiveButton ( R.string.Cash_label , new DialogInterface.OnClickListener() {
  			@Override
  			public void onClick ( DialogInterface dialog , int which ) {
  	    		// Set flag
  				cashCredit="1";
  				saveTransaction ();
  				// Start the new activity
  				//startActivityForResult ( getIntent_SalesInvoiceDetailsActivity_New ( null,"cash" ) , SalesInvoiceDetailsActivity.REQUEST_CODE_NEW );
  				// Specify an explicit transition animation to perform next
  				//ActivityTransition.SlideOutLeft ( SalesInvoiceActivity.this );
  			}	
  			} );
  	    //final String description = AppResources.getInstance ( this ).getString ( this , R.string.sales_invoice_passcode_required_message );
	    	
  		alertDialogBuilder.setNegativeButton ( R.string.cancel_label , new DialogInterface.OnClickListener() {
  			@Override
  			public void onClick ( DialogInterface dialog , int which ) {
  	    		
  			}
  		} );
  		// Create and show the alert dialog
  		alertDialogBuilder.create ().show ();
  	}
     /**
     * Saves the current transaction.
     */
    @SuppressLint("UseSparseArrays") 
    private void saveTransaction () {
    	// Otherwise the sales invoice can be saved
		// Flag used to indicate whether an error occurred or not
		error = false;
		// Flag used to indicate if the activity can be finished or not
		// This flag is mainly used to display a dialog before the activity finishes
		boolean finish = true;
    	// Compute the sales invoice details
		SalesInvoiceDetails salesInvoiceDetail = computeDetails ();
    	// Compute current time
    	Calendar now = Calendar.getInstance ();
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

    	// Determine if there is a previously stored sales invoice
		if ( transactionHeader == null && transactionDetails == null ) {
    		try {
    			
    			if ( PermissionsUtils.getEnableCashInvoiceCredit ( this , DatabaseUtils.getCurrentUserCode ( this ) , DatabaseUtils.getCurrentCompanyCode ( this ) ) )
    				
    				if(ClientCard.isCreditClient(( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClient()))
    				
    			if( list1.get(0).getValue().equals("1") && cash == "" )
					{
    				cash="1";
    				cashCredit="1";
					}
				// Retrieve the sales invoice sequence
				int salesInvoiceSequence = DatabaseUtils.getUserSequence ( this , user.getUserCode () , companyCode , TransactionSequencesDao.Properties.SalesInvoice );
				// Begin transaction
				DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getDatabase ().beginTransaction ();
				if( ((Call) getIntent ().getSerializableExtra ( CALL ) ).getClient().getClientVATNumber() != null )
				{ 
					if( ((Call) getIntent ().getSerializableExtra ( CALL ) ).getClient().getClientVATNumber().equals("Y"))
					{
						
					}else
					{
						if ( passcode == null)
						if(  cashCredit == "" && user.getUserType () == 11  && ClientCard.isCreditClient(( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClient()) 
								&& exceedsCreditLimitEqualZero ( visit.getClientCode () , companyCode , visit.getDivisionCode () , currency.getCurrencyCode () , salesInvoiceDetail.getTotalValue () ))
						{	
							displayCreditClientWarning ();
			    			return;
			    		}
							
						if ( passcode == null)
						  //cash != null &&(String) getIntent ().getSerializableExtra ( CASH ) == null &&
						if (cashCredit== "" && cash == "" && user.getUserType () == 11 && ClientCard.isCreditClient(( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClient())
						&& exceedsCreditLimit ( visit.getClientCode () , companyCode , visit.getDivisionCode () , currency.getCurrencyCode () , salesInvoiceDetail.getTotalValue () ) ) {
							displayCashClientWarning ();
				    		return;
						}
						//if( !(((Call) getIntent ().getSerializableExtra ( CALL ) ).getClient().getClientVATNumber().equals("Y")))
						// Check if the new invoice can be invoiced without the credit exceeding the allowed limit
//						if (  cash == "" && passcode == null && exceedsCreditLimit ( visit.getClientCode () , companyCode , visit.getDivisionCode () , currency.getCurrencyCode () , salesInvoiceDetail.getTotalValue () ) ) {
//				    		// Set flag
//				    		displayPasscode = true;
//				    		// Initialize the tertiary view
//				    		initializeTertiaryView ( AppResources.getInstance ( this ).getString ( this , R.string.sales_invoice_passcode_required_message ) );
//				    		// Disable the main list
//				    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( false );
//				    		// Retrieve a reference to the tertiary view
//				    		View tertiaryView = findViewById ( R.id.layout_passcode );
//				    		// Display the tertiary view
//				    		tertiaryView.setVisibility ( View.VISIBLE );
//				    		// Animate the tertiary view
//				    		tertiaryView.startAnimation ( getViewAnimationIn() );
//				    		// Refresh the action bar
//				    		invalidateOptionsMenu ();
//				    		// Exit method
//				    		return;
//						} // End if
					}
					}
					else
					{
						if ( passcode == null)
							if( cashCredit==""  && user.getUserType () == 11 && ClientCard.isCreditClient(( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClient()) 
									&& exceedsCreditLimitEqualZero ( visit.getClientCode () , companyCode , visit.getDivisionCode () , currency.getCurrencyCode () , salesInvoiceDetail.getTotalValue () ))
							{	
								displayCreditClientWarning ();
				    			return;
				    		}	
						
						if ( passcode == null)
						
					if (  cash == "" && user.getUserType () == 11 && ClientCard.isCreditClient(( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClient()) 
							&& exceedsCreditLimit ( visit.getClientCode () , companyCode , visit.getDivisionCode () , currency.getCurrencyCode () , salesInvoiceDetail.getTotalValue () ) ) {
						displayCashClientWarning ();
				    	return;
					}
						 
				//if( !(((Call) getIntent ().getSerializableExtra ( CALL ) ).getClient().getClientVATNumber().equals("Y")))
				// Check if the new invoice can be invoiced without the credit exceeding the allowed limit
//				if (cash=="" && passcode == null && exceedsCreditLimit ( visit.getClientCode () , companyCode , visit.getDivisionCode () , currency.getCurrencyCode () , salesInvoiceDetail.getTotalValue () ) ) {
//		    		// Set flag
//		    		displayPasscode = true;
//		    		// Initialize the tertiary view
//		    		initializeTertiaryView ( AppResources.getInstance ( this ).getString ( this , R.string.sales_invoice_passcode_required_message ) );
//		    		// Disable the main list
//		    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( false );
//		    		// Retrieve a reference to the tertiary view
//		    		View tertiaryView = findViewById ( R.id.layout_passcode );
//		    		// Display the tertiary view
//		    		tertiaryView.setVisibility ( View.VISIBLE );
//		    		// Animate the tertiary view
//		    		tertiaryView.startAnimation ( getViewAnimationIn() );
//		    		// Refresh the action bar
//		    		invalidateOptionsMenu ();
//		    		// Exit method
//		    		return;
//				} // End if
					}	 
				// Check if the pass code is not valid, and otherwise use any other passcode provided
				passcode = passcode == null ? getIntent ().getStringExtra ( OTHER_PASSCODE ) : passcode;
				
				
			
					
	    		// Compute the transaction header code
	    		transactionHeaderCode = String.valueOf ( TransactionHeadersUtils.Type.SALES_INVOICE ) + String.valueOf ( user.getPrefixID () ) + sequence.format ( salesInvoiceSequence );
				// Update the sales invoice sequence
				DatabaseUtils.setUserSequence ( this , user.getUserCode () , companyCode , TransactionSequencesDao.Properties.SalesInvoice , salesInvoiceSequence + 1 );
	    		// TODO : Details of a sales invoice transaction header
	    		// Declare and initialize a transaction header
	    		transactionHeader = new TransactionHeaders ( null , // ID
	    				transactionHeaderCode , // TransactionCode
	    				TransactionHeadersUtils.Type.SALES_INVOICE , // TransactionType
	    				divisionCode , // DivisionCode
	    				companyCode , // CompanyCode
	    				currency.getCurrencyCode () , // CurrencyCode
	    				visit.getClientCode () , // ClientCode
	    				visit.getUserCode () , // UserCode
	    				visit.getVisitID () , // VisitID
	    				visit.getJourneyCode () , // JourneyCode
	    				Calendar.getInstance ().getTime () , // IssueDate
	    				null , // DeliveryDate
	    				salesInvoiceDetail.getGrossAmount () , // GrossAmount
	    				salesInvoiceDetail.getDiscountAmount () , // DiscountAmount
	    				salesInvoiceDetail.getGrossAmount () - salesInvoiceDetail.getDiscountAmount () , // NetAmount
	    				salesInvoiceDetail.getTaxes () , // TaxAmount
	    				salesInvoiceDetail.getTotalValue () , // TotalTaxAmount
	    				salesInvoiceDetail.getTotalValue () , // RemainingAmount
	    				StatusUtils.isAvailable () , // TransactionStatus
	    				cash , // Info1
	    				cashCredit , // Info2
	    				null , // Info3
	    				null , // Info4
	    				null , // Info5
	    				null , // ApprovedRequestReturnReference
	    				passcode , // TransactionPasswordCode
	    				note , // Notes
	    				null , // ClientReferenceNumber
	    				null , // PrintingTimes
	    				1 , // DeviceType
	    				IsProcessedUtils.isNotSync () , // IsProcessed
	    				now.getTime () ); // StampDate	
	    		

	   	   	
	    		// Insert the transaction header in DB
	    		DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionHeadersDao ().insert ( transactionHeader );
				// Update target coverage (if any)
				TargetUpdate.updateCoverage ( SalesInvoiceDetailsActivity.this , TargetHeadersUtils.Type.TOTAL_SALES , null , -1 , null , -1 , null , null , UpdateType.INCREASE , transactionHeader.getTotalTaxAmount () , transactionHeader.getCurrencyCode () );
				// Update target coverage (if any)
				TargetUpdate.updateCoverage ( SalesInvoiceDetailsActivity.this , TargetHeadersUtils.Type.TOTAL_SALES_PER_CLIENT , transactionHeader.getClientCode () , TargetHeadersUtils.SubType.CLIENT , null , -1 , transactionHeader.getCompanyCode () , transactionHeader.getDivisionCode () , UpdateType.INCREASE , transactionHeader.getTotalTaxAmount () , transactionHeader.getCurrencyCode () );
				// Update target coverage (if any)
				TargetUpdate.updateCoverage ( SalesInvoiceDetailsActivity.this , TargetHeadersUtils.Type.TOTAL_SALES_PER_CLIENT_PER_BRAND , transactionHeader.getClientCode () , TargetHeadersUtils.SubType.CLIENT , transactionHeader.getDivisionCode () , TargetHeadersUtils.SubType.DIVISION , transactionHeader.getCompanyCode () , transactionHeader.getDivisionCode () , UpdateType.INCREASE , transactionHeader.getTotalTaxAmount () , transactionHeader.getCurrencyCode () );
	    		// Iterate over all selected invoices 
				final int TOTAL_STATES [] = { Invoice.STATE_ORDERED , Invoice.STATE_BASKET , Invoice.STATE_FREE };
	    		int lineID = 0;
				for ( int i = 0 ; i < selectedInvoices.size () ; i ++ ) {
					// Retrieve the current invoice
					Invoice invoice = selectedInvoices.get ( i );
					// Iterate over the states
					for ( int j = 0 ; j < TOTAL_STATES.length ; j ++ ) {
						// Check if the current state is ORDERED and if the invoice has either ordered or missed quantities
						if ( TOTAL_STATES [ j ] == Invoice.STATE_ORDERED && invoice.hasValidQuantities ( Invoice.STATE_MISSED ) )
							; // Do nothing, do not skip
	    				// Check if the current item has valid quantities
						else if ( ! invoice.hasValidQuantities ( TOTAL_STATES [ j ] ) ) 
	    					continue;
    					// Initialize a transaction detail
    					TransactionDetails transactionDetail = initializeTransactionDetail ( invoice , lineID ++ , TOTAL_STATES [ j ] , TOTAL_STATES [ j ] == Invoice.STATE_ORDERED , now );
						// Insert the transaction detail in DB
						DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsDao ().insert ( transactionDetail );
						// Retrieve the vehicle stock
						VehiclesStock vehicleStock = DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesStockDao ().queryBuilder ()
								.where ( VehiclesStockDao.Properties.ItemCode.eq ( transactionDetail.getItemCode () ) ).unique ();
						// Check if the vehicle stock is valid
						if ( vehicleStock != null ) {
							// Reduce the good quantity
							vehicleStock.setGoodQuantity ( vehicleStock.getGoodQuantity () - invoice.getBasicUnitQuantity ( TOTAL_STATES [ j ] ) );
							// Update the vehicle stock
							DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesStockDao ().update ( vehicleStock );
						} // End if
						// Update target coverage (if any)
    					TargetUpdate.updateCoverage ( SalesInvoiceDetailsActivity.this , TargetHeadersUtils.Type.TOTAL_SALES_PER_CLIENT_PER_BRAND , transactionHeader.getClientCode () , TargetHeadersUtils.SubType.CLIENT , transactionDetail.getItemCode () , TargetHeadersUtils.SubType.ITEM , transactionHeader.getCompanyCode () , transactionHeader.getDivisionCode () , UpdateType.INCREASE , transactionDetail.getTotalLineAmount () , transactionHeader.getCurrencyCode () );
					} // End for loop
				} // End for loop
	    		// Indicate that a sales invoice was successfully performed during this visit
	    		CallAction.setCallActionStatus ( this , visit.getVisitID () , CallAction.ID.SALES_INVOICE , true );	
				
				// Commit changes
				DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getDatabase ().setTransactionSuccessful ();
				// Indicate that the save was successful
				Vibration.vibrate ( SalesInvoiceDetailsActivity.this );
    		} catch ( Exception exception ) {
				// Indicate that an error occurred
				error = true;
    		} finally {
				// End transaction
				DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getDatabase ().endTransaction ();
    		} // End try-catch-finally block
		} // End if
    	else
    		// Indicate that an error occurred
    		error = true;
		
		// Determine if an error occurred
		if ( error ) {
			// Dismiss any displayed dialogs
			AppDialog.getInstance ().dismiss ();
			// Display baguette message
			Baguette.showText ( this , AppResources.getInstance ( this ).getString ( this , R.string.sales_invoice_save_failure_message ) , Baguette.BackgroundColor.RED );
    		// Consume event
    		return;
		} // End if
		
		// Indicate that the save was successful
		Vibration.vibrate ( SalesInvoiceDetailsActivity.this );
		
		// Check if an error occurred
		if ( ! error && transactionHeaderCode != null ) {
			// Retrieve a constant reference to the transaction code
			printingTransactionCode = transactionHeaderCode;
			// Set flag
			displayClientType = true;
			// Display printing confirmation
			displayClientTypeConfirmation ();
		} // End if
		else {
			// Otherwise no errors occurred
	    	// Call this to set the result that your activity will return to its caller
			Intent intent = new Intent();
			intent.putExtra ( SalesInvoiceActivity.SAVE_SUCCESS , ! error );
	    	setResult ( RESULT_OK , intent );
	    	// Finish this activity (if allowed)
	    	if ( finish )
				// Finish this activity
	    		finish ();
		} // End else
    }
    
    /**
     * Initializes and returns a {@link me.SyncWise.Android.Database.TransactionDetails TransactionDetails} object for the indicated invoice based on the provided state.
     * 
     * @param invoice	The {@link me.SyncWise.Android.Modules.SalesInvoice.Invoice Invoice} reference.
     * @param lineID	Integer holding the line ID.
     * @param state	Integer holding the required state
     * @param addMissed	Boolean indicating if the missed quantities should be added.
     * @param now	A {@link java.util.Calendar Calendar} object hosting the current time.
     * @return	A {@link me.SyncWise.Android.Database.TransactionDetails TransactionDetails} object based on the provided state.
     */
    private TransactionDetails initializeTransactionDetail ( final Invoice invoice , final int lineID , final int state , final boolean addMissed , final Calendar now ) {
    	// Determine the parent line ID
    	int parentLineID = -1;
    	switch ( state ) {
    	case Invoice.STATE_BASKET:
    		parentLineID = -2;
    		break;
    	case Invoice.STATE_FREE:
    		parentLineID = -3;
    		break;
		case Invoice.STATE_DEFAULT:
			return null;
		case Invoice.STATE_ORDERED:
		case Invoice.STATE_MISSED:
		default:
			break;
		}
		// TODO : Details of a sales invoice transaction detail
		// Initialize and return a transaction detail
		return new TransactionDetails  ( null , // ID
				transactionHeader.getTransactionCode () , // TransactionCode
				lineID , // LineID
				invoice.getItem ().getItemCode () , // ItemCode
				state == Invoice.STATE_FREE ? TransactionDetailsUtils.Type.FREE : TransactionDetailsUtils.Type.ORDER , // OrderedType
				(double) invoice.getBig ( state ) , // QuantityBig
				(double) invoice.getMedium ( state ) , // QuantityMedium
				(double) invoice.getSmall ( state ) , // QuantitySmall
				(double) invoice.getBasicUnitQuantity ( state ) , // BasicUnitQuantity
				0.0 , // ApprovedQuantityBig
				0.0 , // ApprovedQuantityMedium
				0.0 , // ApprovedQuantitySmall
				0.0 , // ApprovedBasicUnitQuantity
				addMissed ? (double) invoice.getMissedBig () : 0.0 , // MissedQuantityBig
				addMissed ? (double) invoice.getMissedMedium () : 0.0 , // MissedQuantityMedium
				addMissed ? (double) invoice.getMissedSmall () : 0.0 , // MissedQuantitySmall
				addMissed ? (double) invoice.getBasicUnitQuantity ( Invoice.STATE_MISSED ) : 0.0 , // MissedBasicUnitQuantity
				invoice.getPriceBig () , // PriceBig
				invoice.getPriceMedium () , // PriceMedium
				invoice.getPriceSmall () , // PriceSmall
				invoice.getPriceBig () , // UserPriceBig
				invoice.getPriceMedium () , // UserPriceMedium
				invoice.getPriceSmall () , // UserPriceSmall
				invoice.getDiscountPercentage ( state ) , // DiscountPercentage
				invoice.getDiscountAmount ( state ) , // DiscountAmount
				invoice.getTotalAmount ( state , true , true ) , // TotalLineAmount
				state == Invoice.STATE_ORDERED ? String.valueOf ( invoice.getHeaderDiscountPercentage () ) + "," 
						+ String.valueOf ( invoice.getDetailDiscountPercentage () ) + "," 
						+ String.valueOf ( invoice.getPromotionDiscountPercentage () ) : null , // LineNote
				null , // ItemLot
				null , // ReasonCode
				invoice.getTax () , // ItemTaxPercentage
				invoice.getItem ().getItemName () , // ItemName
				invoice.getItem ().getItemAltName () , // ItemAltName
				parentLineID , // ParentLineID
				null , // ItemExpiryDate
				null , // ItemAffectedStock
				null , // IsInvoiceRelated
				null , // IsCompanyRelated
				null , 
				null , 
				null , 
				null , 
				null , 
				now.getTime () ); // StampDate
    }
   
    
	/**
     * Determines if the current invoice price value exceeds the client credit limit equal zero.
     * 
     * @param clientcode	String hosting the client code.
     * @param companyCode	String hosting the company code.
     * @param divisionCode	String hosting the division code.
     * @param currencyCode	String hosting the currency code.
     * @param invoicePrice	The current invoice price.
     * @return	Boolean indicating if the current client credit limit has been exceeded or not.
     */
    private boolean exceedsCreditLimitEqualZero ( final String clientCode , final String companyCode , final String divisionCode , final String currencyCode , final double invoicePrice ) {
    	// Check if the current client has any credit limits
    	// Retrieve the total client credit limit amount and balance
    	Cursor cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().rawQuery ( 
    			"SELECT COALESCE ( SUM ( " + ClientCreditingsDao.Properties.CreditAmount.columnName + " ) , 0 ) " + ClientCreditingsDao.Properties.CreditAmount.columnName + " " +
    			"FROM " + ClientCreditingsDao.TABLENAME + " " +
    			"WHERE " + ClientCreditingsDao.Properties.ClientCode.columnName + " = ? " +
    			"AND " + ClientCreditingsDao.Properties.CompanyCode.columnName + " = ? " +
    			"AND " + ClientCreditingsDao.Properties.CurrencyCode.columnName + " = ? " +
    			"AND " + ClientCreditingsDao.Properties.DivisionCode.columnName + " = ? ",
    			new String [] {
    					clientCode ,
    					companyCode ,
    					currencyCode,
    					divisionCode
    			} );
    	// Move the cursor to the first raw
    	cursor.moveToFirst ();
    	// Retrieve the total credit and balance amounts
    	double totalCredit = cursor.getDouble ( cursor.getColumnIndex ( ClientCreditingsDao.Properties.CreditAmount.columnName ) );
    	
    	// Clear cursor
    	cursor.close ();
    	cursor = null;
    	if(totalCredit==0)
    		return true;
    				
    	return  false;
    				
    }
    
    
    
    
    
	/**
     * Determines if the current invoice price value exceeds the client credit limit.
     * 
     * @param clientcode	String hosting the client code.
     * @param companyCode	String hosting the company code.
     * @param divisionCode	String hosting the division code.
     * @param currencyCode	String hosting the currency code.
     * @param invoicePrice	The current invoice price.
     * @return	Boolean indicating if the current client credit limit has been exceeded or not.
     */
    private boolean exceedsCreditLimit ( final String clientCode , final String companyCode , final String divisionCode , final String currencyCode , final double invoicePrice ) {
    	// Check if the current client has any credit limits
    	// Retrieve the total client credit limit amount and balance
    	Cursor cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().rawQuery ( 
    			"SELECT COALESCE ( SUM ( " + ClientCreditingsDao.Properties.CreditAmount.columnName + " ) , 0 ) " + ClientCreditingsDao.Properties.CreditAmount.columnName + " " +
    			"FROM " + ClientCreditingsDao.TABLENAME + " " +
    			"WHERE " + ClientCreditingsDao.Properties.ClientCode.columnName + " = ? " +
    			"AND " + ClientCreditingsDao.Properties.CompanyCode.columnName + " = ? " +
    			"AND " + ClientCreditingsDao.Properties.CurrencyCode.columnName + " = ? " +
    			"AND " + ClientCreditingsDao.Properties.DivisionCode.columnName + " = ? ",
    			new String [] {
    					clientCode ,
    					companyCode ,
    					currencyCode,
    					divisionCode
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
    	// In order to do that, compute the total credit balance, not processed invoices, remaining dues , current invoice amount after deduction of the not processed collections
  
 
    	cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().rawQuery ( 
    			"SELECT " + invoicePrice +
//    			" - ( SELECT COALESCE ( SUM ( " + ClientCreditingsDao.Properties.CreditBalance.columnName + " ) , 0 ) " +
//        			"FROM " + ClientCreditingsDao.TABLENAME + " " +
//        			"WHERE " + ClientCreditingsDao.Properties.ClientCode.columnName + " = ? " +
//        			"AND " + ClientCreditingsDao.Properties.CompanyCode.columnName + " = ? " +
//        			"AND " + ClientCreditingsDao.Properties.CurrencyCode.columnName + " = ? )" +
    			" + ( SELECT COALESCE ( SUM ( " + ClientDuesDao.Properties.RemainingAmount.columnName + " ) , 0 ) " +
	    			"FROM " + ClientDuesDao.TABLENAME + " " +
	    			"WHERE " + //ClientDuesDao.Properties.DueDate.columnName + " < " + Calendar.getInstance ().getTimeInMillis () + " " +
	    			//"AND " 
	    			  ClientDuesDao.Properties.ClientCode.columnName + " = ? " +
	    			"AND " + ClientDuesDao.Properties.DivisionCode.columnName + " = ? " +
	    			"AND " + ClientDuesDao.Properties.CompanyCode.columnName + " = ? )" +
	    		" + ( SELECT COALESCE ( SUM ( " + TransactionHeadersDao.Properties.RemainingAmount.columnName + " ) , 0 ) " +
	    			"FROM " + TransactionHeadersDao.TABLENAME + " " +
	    			"WHERE " + TransactionHeadersDao.Properties.TransactionType.columnName + " = ? " +
	    			"AND " + TransactionHeadersDao.Properties.TransactionStatus.columnName + " = ? " +
	    			"AND " + TransactionHeadersDao.Properties.IsProcessed.columnName + " = ? " + 
	    			"AND " + TransactionHeadersDao.Properties.ClientCode.columnName + " = ? " +
	    			"AND " + TransactionHeadersDao.Properties.CompanyCode.columnName + " = ? " +
	    			"AND " + TransactionHeadersDao.Properties.CurrencyCode.columnName + " = ? )" 
//                  +
//	    		" - ( SELECT COALESCE ( SUM ( " + CollectionHeadersDao.Properties.TotalAmount.columnName + " ) , 0 ) " +
//	    			"FROM " + CollectionHeadersDao.TABLENAME + " " +
//	    			"WHERE " + CollectionHeadersDao.Properties.ClientCode.columnName + " = ? " +
//	    			"AND " + CollectionHeadersDao.Properties.CollectionStatus.columnName + " = ? " +
//	    			"AND " + CollectionHeadersDao.Properties.CompanyCode.columnName + " = ? " +
//	    			"AND " + CollectionHeadersDao.Properties.CurrencyCode.columnName + " = ? " +
//	    			"AND " + CollectionHeadersDao.Properties.IsProcessed.columnName + " = ? " +
//	    			"And " + CollectionHeadersDao.Properties.CollectionType.columnName+ " = ? "+ " )"
	    			,
    			new String [] {
    				//	clientCode ,
    				//	companyCode ,
					//	currencyCode ,
						clientCode ,
						divisionCode ,
						companyCode ,
						String.valueOf ( TransactionHeadersUtils.Type.SALES_INVOICE ) ,
						String.valueOf ( StatusUtils.isAvailable () ) ,
						String.valueOf ( IsProcessedUtils.isNotSync () ) ,
						clientCode ,
						companyCode ,
						currencyCode ,
						//clientCode ,
						//String.valueOf ( StatusUtils.isAvailable () ) ,
						//companyCode ,
						//currencyCode ,
					//	String.valueOf ( IsProcessedUtils.isNotSync () ),
				//		String.valueOf(CollectionUtils.PaymentType.CASH)
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
		//if(isStock)
		//new FilterListStock ( newText ).execute ();
		// Indicate that the action was handled by the listener
		return true;
	}
	
	/**
	 * Gets the sales invoice details adapter. The default implementation uses {@link me.SyncWise.Android.Modules.SalesInvoice.SalesInvoiceDetailsAdapter SalesInvoiceDetailsAdapter}.<br>
	 * In order to used a custom adapter, override this method.<br>
	 * <b>Note : </b> This method should not return {@code NULL}.
	 * 
	 * @param layout	XML Layout (layout) resource id.
	 * @param invoiceItems	List of {@link me.SyncWise.Android.Modules.SalesInvoice.Invoice Invoice} objects.
	 * @param itemsEnabled	Flag indicating if the items are enabled or not.
	 * @param usePicker	Flag indicating if the number picker should be enabled or not.
	 * @param invoiceState	Integer used to host the invoice state
	 * @return	A sales invoice details adapter.
	 */
	protected ListAdapter getSalesInvoiceDetailsAdapter ( int layout , List < Invoice > invoiceItems , final boolean itemsEnabled , final boolean usePicker , final int invoiceState ) {
		return new SalesInvoiceDetailsAdapter ( this , layout , invoiceItems , currency , itemsEnabled , usePicker , invoiceState );
	}
	
	/**
	 * Indicates if there are valid sales invoices (with non-zero quantities).<br>
	 * This method iterates over the list of invoices and checks their quantities.
	 * 
	 * @return	Boolean indicating if there is at least one valid invoice.
	 */
	private boolean hasSalesInvoices () {
		try {
			// Check if the invoices list is valid
			if ( invoices == null || basketPromotions == null )
				// Invalid list
				return false;
	        // Determine if there is at least one basket ordered
	        boolean basketQuantities = false;
	        for ( BasketPromotion basketPromotion : basketPromotions )
	        	if ( basketPromotion.getQuantity () > 0 ) {
	        		basketQuantities = true;
	        		break;
	        	} // End if
			// Declare and initialize a list used to host the valid sales invoices
			ArrayList < Invoice > salesInvoice = new ArrayList < Invoice > ();
			// Iterate over all invoices
			for ( Invoice invoice : invoices )
				// Check if the current invoice has valid quantities
				if ( invoice.hasValidQuantities ( Invoice.STATE_DEFAULT ) )
					// The invoice contains at least one quantity, add it to the sales invoice list
					salesInvoice.add ( invoice );
			// Check if there is at least one valid sales invoice
			if ( salesInvoice.isEmpty () && ! basketQuantities )
				// No sales invoice
				return false;
			else
				// At least one invoice
				return true;
		} catch ( Exception exception ) {
			// No sales invoice
			return false;
		} // End of try-catch block
	}
	
	/**
	 * Indicates whether the sales invoice has new / unsaved modifications.<br>
	 * This method iterates over the list of all selected invoices and compares them with the transaction details list, along with the transaction note.<br>
	 * <b>Note : </b> This method always returns false if there are no previously stored transaction details.
	 * 
	 * @return	Boolean stating if there are new modifications.
	 */
	private boolean hasModifications () {
		// Check if there is a previously stored sales invoice
		if ( transactionHeader == null || transactionDetails == null || selectedInvoices == null )
			// No saved sales invoice to match to
			return false;
		// Compare the sales invoice note
		if ( ! note.equals ( transactionHeader.getNotes () ) )
			// There is at least one modification
			return true;
		// Get the selected item codes
		HashSet < String > selectedItemCodes = new HashSet < String > ();
		for ( Invoice invoice : selectedInvoices )
			selectedItemCodes.add ( invoice.getItem ().getItemCode () );
		// Map the transaction details to their item codes
		HashSet < String > transactionItemCodes = new HashSet < String > ();
		Map < String , TransactionDetails > orderedTransactionDetails = new HashMap < String , TransactionDetails > ();
		Map < String , TransactionDetails > basketTransactionDetails = new HashMap < String , TransactionDetails > ();
		Map < String , TransactionDetails > freeTransactionDetails = new HashMap < String , TransactionDetails > ();
		// Iterate over all transaction details
		for ( TransactionDetails transactionDetail : transactionDetails ) {
			// Map the current transaction detail to its item code
			transactionItemCodes.add ( transactionDetail.getItemCode () );
			if ( transactionDetail.getOrderedType ().equals ( TransactionDetailsUtils.Type.FREE ) )
				freeTransactionDetails.put ( transactionDetail.getItemCode () , transactionDetail );
			else if ( transactionDetail.getParentLineID ().equals ( -1 ) )
				orderedTransactionDetails.put ( transactionDetail.getItemCode () , transactionDetail );
			else if ( transactionDetail.getParentLineID ().equals ( -2 ) )
				basketTransactionDetails.put ( transactionDetail.getItemCode () , transactionDetail );
		}
		// Declare and initialize a list used to host the valid sales invoices
		ArrayList < Invoice > salesInvoice = new ArrayList < Invoice > ();
		// Iterate over all invoices
		for ( Invoice invoice : invoices ) {
			// Check if the current invoice has valid quantities
			if ( invoice.getQuantityBig () != 0 || invoice.getQuantityMedium () != 0 || invoice.getQuantitySmall () != 0 )
				// The invoice contains at least one quantity, add it to the sales invoice list
				salesInvoice.add ( invoice );		
		} // End for each
		// Check if both sizes differ
		if ( selectedItemCodes.size () != transactionItemCodes.size () )
			// There is at least one modification
			return true;

		// Define the states
		final int TOTAL_STATES [] = { Invoice.STATE_ORDERED , Invoice.STATE_BASKET , Invoice.STATE_FREE };
		// Iterate over the sales invoices
		for ( Invoice invoice : salesInvoice ) {
			// Iterate over all the states
			for ( int i = 0 ; i < TOTAL_STATES.length ; i ++ ) {
				// Retrieve the appropriate transaction detail
				TransactionDetails transactionDetail = null;
				boolean hasValidTransactionDetail = false;
				switch ( TOTAL_STATES [ i ] ) {
				case Invoice.STATE_ORDERED:
					transactionDetail = orderedTransactionDetails.get ( invoice.getItem ().getItemCode () );
					hasValidTransactionDetail = true;
					break;
				case Invoice.STATE_BASKET:
					transactionDetail = basketTransactionDetails.get ( invoice.getItem ().getItemCode () );
					hasValidTransactionDetail = true;
					break;
				case Invoice.STATE_FREE:
					transactionDetail = freeTransactionDetails.get ( invoice.getItem ().getItemCode () );
					hasValidTransactionDetail = true;
					break;
				default:
					break;
				} // End switch
				// Check if the invoice has valid quantities for the corresponding state
				boolean hasValidQuantities = invoice.hasValidQuantities ( TOTAL_STATES [ i ] );
				if ( TOTAL_STATES [ i ] == Invoice.STATE_ORDERED )
					hasValidQuantities = hasValidQuantities || invoice.hasValidQuantities ( Invoice.STATE_MISSED );
				// Check for inconsistencies
				if ( hasValidQuantities == hasValidTransactionDetail && hasValidQuantities ) {
					/*
					 * Compare the following fields :
					 * - Quantity big 
					 * - Quantity medium 
					 * - Quantity small 
					 * - Total Line Amount
					 */
					if ( ! transactionDetail.getQuantityBig ().equals ( (double) invoice.getBig ( TOTAL_STATES [ i ] ) ) 
							|| ! transactionDetail.getQuantityMedium ().equals ( (double) invoice.getMedium ( TOTAL_STATES [ i ] ) ) 
							|| ! transactionDetail.getQuantitySmall ().equals ( (double)  invoice.getSmall ( TOTAL_STATES [ i ] ) )
							|| ! transactionDetail.getTotalLineAmount ().equals ( invoice.getTotalAmount ( TOTAL_STATES [ i ] , true , true ) ) )
						// There is at least one modification
						return true;
					/*
					 * Compare the following fields IFF the state is ordered:
					 * - Missed Quantity big 
					 * - Missed Quantity medium 
					 * - Missed Quantity small 
					 */
					if (  ! transactionDetail.getMissedQuantityBig ().equals ( (double) invoice.getBig ( Invoice.STATE_MISSED ) ) 
							|| ! transactionDetail.getMissedQuantityMedium ().equals ( (double) invoice.getMedium ( Invoice.STATE_MISSED ) ) 
							|| ! transactionDetail.getMissedQuantitySmall ().equals ( (double)  invoice.getSmall ( Invoice.STATE_MISSED ) ) )
						// There is at least one modification
						return true;
				} // End if
				else if ( hasValidQuantities != hasValidTransactionDetail )
					// There is at least one modification
					return true;
			} // End for loop
		} // End for each
		// Otherwise there are no modifications
		return false;
	}
	
	/**
	 * Filters the {@link #invoices} list based on the indicated {@link #searchQuery} string and the {@link #selectedDivisionsCodes} list.<br>
	 * <b>NOTE : </b> This method does not perform any kind of integrity check what so ever !
	 * 
	 * @return	List of {@link me.SyncWise.Android.Modules.SalesInvoice.Invoice Invoice} objects filtered according to the search and divisions filter.
	 */
	@SuppressLint("DefaultLocale") 
	protected ArrayList < Invoice > filter () {
		// Declare and initialize a new invoices list
		ArrayList < Invoice > _invoices = new ArrayList < Invoice > ();
		// Iterate over all the invoices
		for ( Invoice invoice : invoices ) {
				// Determine if at least one of the invoice's divisions is selected
				boolean skip = true;
				// Iterate over all invoice's division
				for ( String divisionCode : invoice.getDivisionCodes () )
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
				if ( invoice.getItem ().getItemName ().toLowerCase ().contains ( searchQuery.toLowerCase () )
						|| invoice.getItem ().getItemCode ().toLowerCase ().contains ( searchQuery.toLowerCase () )
						|| ( invoice.getBarCodes () != null && invoice.getBarCodes ().contains ( searchQuery.toLowerCase () ) ) )
					// Add the invoice to the list
					_invoices.add ( invoice );
			} // End for each
		
		// Return the new invoices list
		return _invoices;
	}
	
	/**
	 * AsyncTask helper class used to populate the sales invoice items list with the appropriate items / invoices.
	 * 
	 * @author Elias
	 *
	 */
	private class PopulateList extends AsyncTask < Void , Void , ArrayList < Invoice > > {
		
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
		protected ArrayList < Invoice > doInBackground ( Void ... params ) {
			try {
				
				// Declare and initialize a cursor object used to retrieve data sets from query results
				Cursor cursor = null;
				// Declare the SQL string and selection arguments array of strings used to to query DB
				String SQL = null;
				String selectionArguments [] = null;
				// Retrieve the company, client and division codes
				String companyCode = DatabaseUtils.getCurrentCompanyCode ( SalesInvoiceDetailsActivity.this );
				String clientCode = ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClient ().getClientCode ();
				String divisionCode = ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClientDivision ().getDivisionCode ();
				// Retrieve the current date
				Calendar calendar = Calendar.getInstance ();
				// Retrieve the start of the day
				calendar.set ( Calendar.HOUR_OF_DAY , 0 );
				calendar.set ( Calendar.MINUTE , 0 );
				calendar.set ( Calendar.SECOND , 0 );
				calendar.set ( Calendar.MILLISECOND , 0 );
				long startDay = calendar.getTimeInMillis ();
				
				// Check if the currency is valid
				if ( currency == null ) {
					// Retrieve the currency with the utmost priority
					// Compute the SQL query
					SQL =   " SELECT C.* FROM " + CurrenciesDao.TABLENAME + " C INNER JOIN " + ClientCurrenciesDao.TABLENAME + " CC " +
							" ON C." + CurrenciesDao.Properties.CurrencyCode.columnName + " = CC." + ClientCurrenciesDao.Properties.CurrencyCode.columnName + " " +
							" AND CC." + ClientCurrenciesDao.Properties.ClientCode.columnName + " = ? " +
							" ORDER BY CC." + ClientCurrenciesDao.Properties.Priority.columnName;
					// Compute the selection arguments
					selectionArguments = new String [] { clientCode };
					// Query DB in order to retrieve the currencies
					cursor = DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getDatabase ().rawQuery ( SQL , selectionArguments );
					// Move the cursor to the first raw
					if ( cursor.moveToFirst () ) {
						do {
							// Retrieve the current currency
							currency = DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getCurrenciesDao ().readEntity ( cursor , 0 );
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
				
				// Check if the instant promotion list is valid
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
					cursor = DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getDatabase ().rawQuery ( SQL , selectionArguments );
					// Move the cursor to the first raw
					if ( cursor.moveToFirst () ) {
						do {
							// Retrieve the current instant promotion
							PromotionDetails promotionDetail = DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getPromotionDetailsDao ().readEntity ( cursor , 0 );
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
				
				// Check if the selected invoice item code index list is valid
				if ( selectedInvoicesIndex == null )
					// Initialize the list
					selectedInvoicesIndex = new ArrayList < String > ();
				
				// Map the transaction details to their item codes
				HashSet < String > transactionItemCodes = new HashSet < String > ();
				Map < String , TransactionDetails > orderedTransactionDetails = new HashMap < String , TransactionDetails > ();
				Map < String , TransactionDetails > basketTransactionDetails = new HashMap < String , TransactionDetails > ();
				Map < String , TransactionDetails > freeTransactionDetails = new HashMap < String , TransactionDetails > ();
				// Determine if there is an available transaction header code
				if ( getIntent ().getStringExtra ( TRANSACTION_HEADER_CODE ) != null && transactionHeader == null ) {
					// Try to retrieve the transaction header with the specified transaction code
					transactionHeader = DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getTransactionHeadersDao ().queryBuilder ()
							.where ( TransactionHeadersDao.Properties.TransactionCode.eq ( getIntent ().getStringExtra ( TRANSACTION_HEADER_CODE ) ) ).unique ();
					// Check if the transaction header is valid
					if ( transactionHeader != null ) {
						
						String ss="j";
						// Refresh object
						DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getTransactionHeadersDao ().refresh ( transactionHeader );
					
				   	    
				    	
						
						// Set the sales invoice note
						note = transactionHeader.getNotes ();
						// Initialize the transaction details list
						transactionDetails = new ArrayList < TransactionDetails > ();
						// Retrieve the appropriate transaction details
						transactionDetails.addAll ( DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getTransactionDetailsDao ().queryBuilder ()
							.where ( TransactionDetailsDao.Properties.TransactionCode.eq ( getIntent ().getStringExtra ( TRANSACTION_HEADER_CODE ) ) )
							.orderAsc ( TransactionDetailsDao.Properties.LineID ).list () );
						
					
						
						// Iterate over all transaction details
						for ( TransactionDetails transactionDetail : transactionDetails ) {
							// Refresh object
							DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getTransactionDetailsDao ().refresh ( transactionDetail );
							// Map the current transaction detail to its item code
							transactionItemCodes.add ( transactionDetail.getItemCode () );
							if ( transactionDetail.getOrderedType ().equals ( TransactionDetailsUtils.Type.FREE ) )
								freeTransactionDetails.put ( transactionDetail.getItemCode () , transactionDetail );
							else if ( transactionDetail.getParentLineID ().equals ( -1 ) )
								orderedTransactionDetails.put ( transactionDetail.getItemCode () , transactionDetail );
							else if ( transactionDetail.getParentLineID ().equals ( -2 ) )
								basketTransactionDetails.put ( transactionDetail.getItemCode () , transactionDetail );
						} // End for each
						
						// Retrieve the appropriate currency
						currency = DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getCurrenciesDao ().queryBuilder ()
							.where ( CurrenciesDao.Properties.CurrencyCode.eq ( transactionHeader.getCurrencyCode () ) ).unique ();
					} // End if

				} // End if
				
//NEW promotion pricelist:
				String userCode = DatabaseUtils.getCurrentUserCode ( SalesInvoiceDetailsActivity.this );
				String ca=(String)getIntent ().getStringExtra( SalesInvoiceDetailsActivity.CASH ) ;
			 Boolean b = false;
			if(ca!=null && ca.equals("1"))
			{
				b=true;
			}
				
					// Initialize the map
			if(b==false && ClientCard.isCreditClient ((  (Call)getIntent ().getSerializableExtra ( CALL ) ).getClient () )){
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
			 
				cursor = DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getDatabase ().rawQuery  ( SQL , selectionArguments );
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
				List < PromotionHeaders > _promotionHeadersPriceList = DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getPromotionHeadersDao ().queryBuilder ()
						.where ( PromotionHeadersDao.Properties.PromotionID.in ( headerIDPricelist ) ).list ();
				// Iterate over all promotion headers in order to map them to their promotion IDs
				for ( PromotionHeaders promotionHeader : _promotionHeadersPriceList ) {
					// Map the current promotion header to its ID
					promotionHeadersPricelist.put ( promotionHeader.getPromotionID () , promotionHeader );
					if ( promotionHeader.getPromotionType() == PromotionUtils.Type.PRICELIST_PROMOTION ) {
						List < PromotionDetails > promotionsPriceList = DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getPromotionDetailsDao ()
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
				// Retrieve all divisions
				List < Divisions > allDivisions = DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getDivisionsDao ().queryBuilder ()
						.where ( DivisionsDao.Properties.CompanyCode.eq ( companyCode ) ).list ();
				// Retrieve the divisions linked to the user
				List < Divisions > directUserDivisions = DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getDivisionsDao ()
						.queryRaw ( " WHERE CompanyCode = ? AND DivisionCode IN ( SELECT DivisionCode FROM UserDivisions WHERE CompanyCode = ? ) " ,
								new String [] { companyCode , companyCode } );
				allDivisions.removeAll ( directUserDivisions );
				// Retrieve the user children division
				List < Divisions > allUserDivisions = DivisionsUtils.getChildren ( directUserDivisions , allDivisions );
				// Add the direct user divisions to the main list
				allUserDivisions.addAll ( directUserDivisions );
				
				// Initialize the map
				HashMap < String , Prices > itemPrices = new HashMap < String , Prices > ();
				
				String priceListf=(String)getIntent ().getStringExtra( PRICE ) ;
				List < String > codes = new ArrayList < String > ();
			
					// Initialize the map
					if(priceListf == null){
					
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
				cursor = DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getDatabase ().rawQuery ( SQL , selectionArguments );
				// Move the cursor to the first raw
				if ( cursor.moveToFirst () ) {
					do {
						// Retrieve the current price
						Prices price = DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getPricesDao ().readEntity ( cursor , 0 );
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
				    else{
				    	String[] parts = priceListf.split(",");
				    	for(int i=0;i<parts.length;i++)
				    		codes.add( parts[i] );
				    List<Prices> pricesList =	DatabaseUtils.getInstance(SalesInvoiceDetailsActivity.this).getDaoSession().getPricesDao().queryBuilder ().where
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
					SalesInvoiceDetailsActivity.this.divisions.addAll ( allUserDivisions );
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
				
				// Check if there are saved invoices
				if ( retrieveInvoices ) {
					// Retrieve basket promotions
					basketPromotions = (ArrayList < BasketPromotion >) ActivityInstance.readDataGson ( SalesInvoiceDetailsActivity.this , SalesInvoiceDetailsActivity.class.getName () , BASKET_PROMOTIONS , new TypeToken < ArrayList < BasketPromotion > > () {}.getType () );
					// Retrieve modified invoices
					invoices = (ArrayList < Invoice >) ActivityInstance.readDataGson ( SalesInvoiceDetailsActivity.this , SalesInvoiceDetailsActivity.class.getName () , INVOICES , new TypeToken < ArrayList < Invoice > > () {}.getType () );
					// Initialize the list used to host the selected sales invoices
					selectedInvoices = new ArrayList < Invoice > ();
					// Iterate over all invoices
					for ( Invoice invoice : invoices )
		    			// Check if the current invoice has valid quantities
		    			if ( invoice.getQuantityBig () != 0 || invoice.getQuantityMedium () != 0 || invoice.getQuantitySmall () != 0 )
		    				// The invoice contains at least one quantity, add it to the sales invoice list
		    				selectedInvoices.add ( invoice );
				} // End if
				else {
					// Retrieve the client item divisions
					List < ItemDivisions > itemDivisions = EntityUtils.queryByGroup ( selectedDivisionsCodes ,
							DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getItemDivisionsDao () ,
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
							DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getItemsDao () ,
							ItemsDao.Properties.ItemCode );
					
					// Retrieve the item barcodes
					List < ItemBarcodes > itemBarcodes = EntityUtils.queryByGroup ( itemCodes ,
							DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getItemBarcodesDao () ,
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
					List < Units > units = DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getUnitsDao ().loadAll ();
					// Map all the Units using their codes
					Map < String , Units > _units = new HashMap < String , Units > ();
					// Iterate over all UOM
					for ( Units unit : units )
						// Map the unit to its code
						_units.put ( unit.getUnitCode () , unit );
					
					// Retrieve all the taxes
					List < Taxes > taxes = DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getTaxesDao ().loadAll ();
					// Declare and initialize a map of taxes
					Map < String , Taxes > _taxes = new HashMap < String , Taxes > ();
					// Iterate over all taxes
					for ( Taxes tax : taxes )
						// Map the tax object to its tax code
						_taxes.put ( tax.getTaxCode () , tax );
					
					// Retrieve the client must stock list
					List < ClientMustStockList > clientMustStockList = DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getClientMustStockListDao ()
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
					List < ClientItemHistory > clientItemHistory = DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getClientItemHistoryDao ().queryBuilder ()
							.where ( ClientItemHistoryDao.Properties.ClientCode.eq ( clientCode ) ,
									ClientItemHistoryDao.Properties.DivisionCode.eq ( DatabaseUtils.getCurrentDivisionCode ( SalesInvoiceDetailsActivity.this ) ) ,
									ClientItemHistoryDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( SalesInvoiceDetailsActivity.this ) ) ).list ();
					// Get the item codes of the item history
					ArrayList < String > clientItemHistoryCodes = new ArrayList < String > ();
					for ( ClientItemHistory itemHistory : clientItemHistory )
						clientItemHistoryCodes.add ( itemHistory.getItemCode () );
					
					// Retrieve the client item history
					List < ClientSellingSuggestion > clientSellingSuggestions = DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getClientSellingSuggestionDao ().queryBuilder ()
							.where ( ClientSellingSuggestionDao.Properties.ClientCode.eq ( clientCode ) ,
									ClientSellingSuggestionDao.Properties.DivisionCode.eq ( DatabaseUtils.getCurrentDivisionCode ( SalesInvoiceDetailsActivity.this ) ) ,
									ClientSellingSuggestionDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( SalesInvoiceDetailsActivity.this ) ) ).list ();
					// Get the item codes of the item history
					HashMap < String , ClientSellingSuggestion > _clientSellingSuggestions = new HashMap < String , ClientSellingSuggestion > ();
					for ( ClientSellingSuggestion sellingSuggestion : clientSellingSuggestions )
						_clientSellingSuggestions.put ( sellingSuggestion.getItemCode () , sellingSuggestion );
					
					// Retrieve the vehicle stock
					List < VehiclesStock > vehicleStock = DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getVehiclesStockDao ().queryBuilder ()
							.where ( VehiclesStockDao.Properties.GoodQuantity.gt ( 0 ) ).list ();
					// Get the item codes of the item history
					HashMap < String , Double > _vehicleStock = new HashMap < String , Double > ();
					for ( VehiclesStock stock : vehicleStock )
						_vehicleStock.put ( stock.getItemCode () , stock.getGoodQuantity () );
					
					// Retrieve the client stock count headers performed today
					List < ClientStockCountHeaders > clientStockCountHeaders = DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getClientStockCountHeadersDao ().queryBuilder ()
							.where ( ClientStockCountHeadersDao.Properties.StampDate.gt ( startDay ) ,
									ClientStockCountHeadersDao.Properties.ClientCode.eq ( clientCode ) ,
									ClientStockCountHeadersDao.Properties.CompanyCode.eq ( companyCode ) ).list ();
					// Store the codes in a list
					ArrayList < String > stockTransactionCodes = new ArrayList < String > ();
					// Iterate over all the client stock count headers
					for ( ClientStockCountHeaders clientStockCountHeader : clientStockCountHeaders )
						// Add the transaction code to the list
						stockTransactionCodes.add ( clientStockCountHeader.getTransactionCode () );
					
					// Initialize the lists used to host the sales invoices and the selected sales invoices
					HashMap < String , Invoice > mappedInvoices = new HashMap < String , Invoice > ();
					invoices = new ArrayList < Invoice > ();
					selectedInvoices = new ArrayList < Invoice > ();
		    		orderedInvoices = new ArrayList < Invoice > ();
		    		missedInvoices = new ArrayList < Invoice > ();
		    		freeInvoices = new ArrayList < Invoice > ();
		    		promotedInvoices = new ArrayList < Invoice > ();
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
						// Create a new invoice using the current item and its appropriate unit of measurement and currency
						Invoice invoice = new Invoice ( item , _units.get ( item.getUnitCode () ) );
						// Set the divisions codes list
						invoice.setDivisionCodes ( _itemDivisions.get ( item.getItemCode () ) );
						// Check that the division codes list is valid
						if ( invoice.getDivisionCodes () == null )
							// Initialize the division codes list
							invoice.setDivisionCodes ( new ArrayList < String > () );
						// Set the item bar codes list
						invoice.setBarCodes ( _itemBarcodes.get ( item.getItemCode () ) );
						// Set the invoice prices
						if ( itemPrices.containsKey ( invoice.getItem ().getItemCode () ) ) {
							Prices invoicePrices = itemPrices.get ( invoice.getItem ().getItemCode () );
							invoice.setPriceBig ( invoicePrices.getPriceBig () );
							invoice.setPriceMedium ( invoicePrices.getPriceMedium () );
							invoice.setPriceSmall ( invoicePrices.getPriceSmall () );
							invoice.setPriceMediumOld( invoicePrices.getPriceMedium () );
							invoice.setPriceSmallOld( invoicePrices.getPriceSmall () );
							invoice.setDetailDiscountPercentage ( invoicePrices.getDiscountPercentage () );
						} // End if
						// Set the item tax
						if ( ItemsUtils.isTaxable ( invoice.getItem () ) && _taxes.get ( invoice.getItem ().getTaxCode () ) != null )
							invoice.setTax ( _taxes.get ( invoice.getItem ().getTaxCode () ).getTaxPercentage () );
						// Determine if the item is a must stock list
						if ( mustStockListItems.contains ( item.getItemCode () ) ) {
							// Indicate that the item is a must stock item
							invoice.setMustStockList ( true );
						} // End if
						// Otherwise check if the item is a history item
						else if ( clientItemHistoryCodes.contains ( item.getItemCode () ) )
							// Indicate that the item is a must stock item
							invoice.setHistory ( true );
						// Check if there are suggestions for the current item
						if ( _clientSellingSuggestions.containsKey ( item.getItemCode () ) ) {
							// Retrieve the client selling suggestion
							ClientSellingSuggestion clientSellingSuggestion = _clientSellingSuggestions.get ( item.getItemCode () );
							// Set the suggestions
							invoice.setSuggestedMedium ( clientSellingSuggestion.getSuggestedQuantityCase ().intValue () );
							invoice.setSuggestedSmall ( clientSellingSuggestion.getSuggestedQuantityUnit ().intValue () );
						} // End if
						// Check if there the item is available in the vehicle
						if ( _vehicleStock.containsKey ( item.getItemCode () ) ) {
							// Calculate the big medium small conversations
							int mediumSmall = item.getUnitMediumSmall () <= 1 ? 1 : item.getUnitMediumSmall ();
							int bigSmall = item.getUnitBigMedium () <= 1 ? mediumSmall : item.getUnitBigMedium () * mediumSmall;
							// Retrieve the basic unit good quantity
							double goodQuantity = _vehicleStock.get ( item.getItemCode () );
							// Compute the big, medium and small quantities
							double bigQuantity = ItemsUtils.isBig ( item ) ? (int) ( goodQuantity / bigSmall ) : 0;
							double mediumQuantity = ItemsUtils.isMedium ( item ) ? (int) ( ( goodQuantity - bigQuantity * bigSmall ) / mediumSmall ) : 0;
							double smallQuantity = (int) ( goodQuantity - bigQuantity * bigSmall - mediumQuantity * mediumSmall );
							// Set the vehicle quantities
							invoice.setVehicleBig ( (int) bigQuantity );
							invoice.setVehicleMedium ( (int) mediumQuantity );
							invoice.setVehicleSmall ( (int) smallQuantity );
						} // End if
						// Check if there is at least one stock transaction code
						if ( ! stockTransactionCodes.isEmpty () ) {
							// Retrieve the counted stock for the current item
							int countedBig = 0 , countedMedium = 0 , countedSmall = 0;
							// Perform query
							List < ClientStockCountDetails > clientStockCountDetails = DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getClientStockCountDetailsDao ().queryBuilder ()
									.where ( ClientStockCountDetailsDao.Properties.ItemCode.eq ( item.getItemCode () ) , ClientStockCountDetailsDao.Properties.TransactionCode.in ( stockTransactionCodes ) ).list ();
							// Iterate over the detail lines
							for ( ClientStockCountDetails clientStockCountDetail : clientStockCountDetails ) {
								// Add the counted quantities
								countedBig += clientStockCountDetail.getQuantityBig ();
								countedMedium += clientStockCountDetail.getQuantityMedium ();
								countedSmall += clientStockCountDetail.getQuantitySmall ();
							} // End for each
							// Set the new counted quantities
							invoice.setCountedBig ( countedBig );
							invoice.setCountedMedium ( countedMedium );
							invoice.setCountedSmall ( countedSmall );
						} // End if

						// Check if the item has an existing transaction detail
						if ( transactionItemCodes.contains ( item.getItemCode () ) ) {
							// Update the ordered (and missed) quantities
							invoice = updateInvoiceFromDetail ( invoice , orderedTransactionDetails.get ( item.getItemCode () ) , Invoice.STATE_ORDERED );
							// Update the basket quantities
							invoice = updateInvoiceFromDetail ( invoice , basketTransactionDetails.get ( item.getItemCode () ) , Invoice.STATE_BASKET );
							// Update the free quantities
							invoice = updateInvoiceFromDetail ( invoice , freeTransactionDetails.get ( item.getItemCode () ) , Invoice.STATE_FREE );
							// Add the invoice to the appropriate lists
							if ( invoice.hasValidQuantities ( Invoice.STATE_ORDERED ) || invoice.hasValidQuantities ( Invoice.STATE_MISSED ) || invoice.hasValidQuantities ( Invoice.STATE_BASKET ) || invoice.hasValidQuantities ( Invoice.STATE_FREE ) )
								selectedInvoices.add ( invoice );
			    			if ( invoice.hasValidQuantities ( Invoice.STATE_ORDERED ) )
			    				orderedInvoices.add ( invoice );
			    			if ( invoice.hasValidQuantities ( Invoice.STATE_MISSED ) )
			    				missedInvoices.add ( invoice );
			    			if ( invoice.hasValidQuantities ( Invoice.STATE_BASKET ) )
			    				promotedInvoices.add ( invoice );
			    			
			    			if ( !invoice.hasValidQuantities ( Invoice.STATE_BASKET ) )
			    				promotedInvoices.remove ( invoice );
			    			if ( invoice.hasValidQuantities ( Invoice.STATE_FREE ) )
			    				freeInvoices.add ( invoice );
						} // End if
						
						// Add the current item in the invoices list
						invoices.add ( invoice );
						// Map the invoice
						mappedInvoices.put ( item.getItemCode () , invoice );
					} // End for each
					
					// Check if the basket promotion list is valid
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
						cursor = DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getDatabase ().rawQuery ( SQL , selectionArguments );
						int promotionHeaderOffset = 0;
						int promotionDetailOffset = DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getPromotionHeadersDao ().getAllColumns ().length;
						// Move the cursor to the first raw
						if ( cursor.moveToFirst () ) {
							do {
								PromotionHeaders promotionHeader = DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getPromotionHeadersDao ().readEntity ( cursor , promotionHeaderOffset );
								PromotionDetails promotionDetail = DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getPromotionDetailsDao ().readEntity ( cursor , promotionDetailOffset );
								ArrayList < PromotionDetails > detailsList = null;
								ArrayList < Invoice > invoicesList = null;
								Invoice invoice = mappedInvoices.get ( promotionDetail.getOrderedItemCode () );
								if ( invoice == null )
									continue;
								if ( basketPromotions.isEmpty () || basketPromotions.get ( basketPromotions.size () - 1 ).getPromotionHeader ().getPromotionID () != promotionHeader.getPromotionID () ) {
									detailsList = new ArrayList < PromotionDetails > ();
									detailsList.add ( promotionDetail );
									invoicesList = new ArrayList < Invoice > ();
									invoicesList.add ( invoice );
									BasketPromotion basketPromotion = new BasketPromotion ();
									basketPromotion.setPromotionHeader ( promotionHeader );
									basketPromotion.setPromotionDetails ( detailsList );
									basketPromotion.setInvoices ( invoicesList );
									basketPromotions.add ( basketPromotion );
								}
								else {
									BasketPromotion basketPromotion = basketPromotions.get ( basketPromotions.size () - 1 );
									basketPromotion.getPromotionDetails ().add ( promotionDetail );
									basketPromotion.getInvoices ().add ( invoice );
								}
							} while ( cursor.moveToNext () );
						} // End if
						// Clear cursor
						cursor.close ();
						cursor = null;
					} // End if
				} // End else
			if(isStock){	
				ArrayList < Invoice > _invoices = new ArrayList < Invoice > ();
			
				List < VehiclesStock > vehicleStocks =  DatabaseUtils.getInstance ( SalesInvoiceDetailsActivity.this ).getDaoSession ().getVehiclesStockDao ().queryBuilder ()
						.where ( VehiclesStockDao.Properties.GoodQuantity.gt(0)  ).list();
				
				// Iterate over all the invoices
				//for ( Invoice invoice : invoices ) {
						// Determine if at least one of the invoice's divisions is selected
						//boolean skip = true;
						// Iterate over all invoice's division
				//		for ( VehiclesStock vehicleStock : vehicleStocks )
							// Check if the current division is selected
					//		if ( invoice.getItem().getItemCode().equals(vehicleStock.getItemCode() )) {
					//		    	_invoices.add ( invoice );
		 			//		}
					//	}
			//	invoices.clear();
			//	invoices.addAll(_invoices);
			
			
			}
			
			
			Collections.sort ( invoices , new Comparator < Invoice > () {
				@Override
				public int compare ( Invoice invoice1 , Invoice invoice2 ) {
					 
						
					 
						int sequence1 = invoice1.getItem ().getItemSequence () == null ? -1 : invoice1.getItem ().getItemSequence ();
						int sequence2 = invoice2.getItem ().getItemSequence () == null ? -1 : invoice2.getItem ().getItemSequence ();
						int order = sequence1 - sequence2;
						if ( order != 0 )
						return order;
					   return invoice1.getItem ().getItemName ().compareToIgnoreCase ( invoice2.getItem ().getItemName () );
					 
				
				
				}
			} );
			
				// Sort the items, and put the MSL first , then the history items, then the remaining items
				Collections.sort ( invoices , new Comparator < Invoice > () {
					@Override
					public int compare ( Invoice invoice1 , Invoice invoice2 ) {
						// Sort the items 
						if ( invoice1.isMustStockList () && ! invoice2.isMustStockList () )
							return -1;
						else if ( ! invoice1.isMustStockList () && invoice2.isMustStockList () )
							return 1;
						else if ( invoice1.isMustStockList () && invoice2.isMustStockList () )
						{
							int sequence1 = invoice1.getItem ().getItemSequence () == null ? -1 : invoice1.getItem ().getItemSequence ();
							int sequence2 = invoice2.getItem ().getItemSequence () == null ? -1 : invoice2.getItem ().getItemSequence ();
							int order = sequence1 - sequence2;
							if ( order != 0 )
							return order;
						   return invoice1.getItem ().getItemName ().compareToIgnoreCase ( invoice2.getItem ().getItemName () );
						}
							//return invoice1.getItem ().getItemName ().compareToIgnoreCase ( invoice2.getItem ().getItemName () );
						
						// Otherwise both are not must stock list
						if ( invoice1.isHistory () && ! invoice2.isHistory () )
							return -1;
						else if ( ! invoice1.isHistory () && invoice2.isHistory () )
							return 1;
						else
							
						{
							int sequence1 = invoice1.getItem ().getItemSequence () == null ? -1 : invoice1.getItem ().getItemSequence ();
							int sequence2 = invoice2.getItem ().getItemSequence () == null ? -1 : invoice2.getItem ().getItemSequence ();
							int order = sequence1 - sequence2;
							if ( order != 0 )
							return order;
						   return invoice1.getItem ().getItemName ().compareToIgnoreCase ( invoice2.getItem ().getItemName () );
						}
					
					
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
					// Initialize the sales invoice details
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
		protected void onPostExecute ( ArrayList < Invoice > invoices ) {
			// Check if the activity has ended
			if ( activityEnded )
				// Do nothing
				return;
			// Determine if the activity must be finished
			if ( error ) {
				// Indicate that the activity cannot be displayed
				new AppToast ( SalesInvoiceDetailsActivity.this )
					.setIcon ( R.drawable.warning )
					.setText ( AppResources.getInstance ( SalesInvoiceDetailsActivity.this ).getString ( SalesInvoiceDetailsActivity.this , R.string.missing_client_currency_price_list_message ) )
					.setDuration ( Toast.LENGTH_LONG )
					.show ();
				// Initialize the invoices list
				SalesInvoiceDetailsActivity.this.invoices = new ArrayList < Invoice > ();
				// Exit method
				return;
			} // End if
    		
    		// Retrieve a reference to the secondary view
    		View secondary = findViewById ( R.id.layout_order_note );
			// Display the tertiary view accordingly
    		secondary.setVisibility ( displayInvoiceNote ? View.VISIBLE : View.GONE );
    		// Enable the main list accordingly
    		( (CustomLinearLayout) findViewById ( R.id.layout_list ) ).setLinearLayoutEnabled ( ! displayInvoiceNote );
    		// Determine if the sales invoice note is undergoing any modifications
    		if ( displayInvoiceNote )
	    		// Restore the secondary view
    			initializeSecondaryView ( true );
				
			// Determine if the request code is INFO
			if ( requestCode != REQUEST_CODE_INFO )
				// Set a new adapter
				setListAdapter ( getSalesInvoiceDetailsAdapter ( R.layout.sales_order_details_activity_item , new ArrayList < Invoice > ( invoices ) , true , true , Invoice.STATE_DEFAULT ) );
			// Otherwise the request code is INFO
			else
				// Set a multiple adapter
				setMultipleAdapter ();
    		// Refresh the action bar
    		invalidateOptionsMenu ();
		}
		
	}
	
	/**
	 * Updates and returns the invoice object based on the provided transaction detail and indicated state.
	 * 
	 * @param invoice	The {@link me.SyncWise.Android.Modules.SalesInvoice.Invoice Invoice} object to update.
	 * @param transactionDetail	The {@link me.SyncWise.Android.Database.TransactionDetails TransactionDetails} object to update from.
	 * @param state	Integer holding the required state.
	 * @return	The updated {@link me.SyncWise.Android.Modules.SalesInvoice.Invoice Invoice} object.
	 */
	private Invoice updateInvoiceFromDetail ( final Invoice invoice , final TransactionDetails transactionDetail , final int state ) {
		// TODO : POSSIBILITY TO IMPROVE LOADING (discounts ...)
		// Check if the objects are valid
		if ( invoice == null || transactionDetail == null )
			return invoice;
		// Determine the state
		switch ( state ) {
		case Invoice.STATE_ORDERED:
			invoice.setQuantityBig ( transactionDetail.getQuantityBig ().intValue () );
			invoice.setQuantityMedium ( transactionDetail.getQuantityMedium ().intValue () );
			invoice.setQuantitySmall ( transactionDetail.getQuantitySmall ().intValue () );
			invoice.setOrderedBig ( transactionDetail.getQuantityBig ().intValue () );
			invoice.setOrderedMedium ( transactionDetail.getQuantityMedium ().intValue () );
			invoice.setOrderedSmall ( transactionDetail.getQuantitySmall ().intValue () );
			invoice.setMissedBig ( transactionDetail.getMissedQuantityBig ().intValue () );
			invoice.setMissedMedium ( transactionDetail.getMissedQuantityMedium ().intValue () );
			invoice.setMissedSmall ( transactionDetail.getMissedQuantitySmall ().intValue () );
			String discounts [] = transactionDetail.getLineNote ().split ( "," );
			invoice.setHeaderDiscountPercentage ( Double.parseDouble ( discounts [ 0 ] ) );
			invoice.setDetailDiscountPercentage ( Double.parseDouble ( discounts [ 1 ] ) );
			invoice.setPromotionDiscountPercentage ( Double.parseDouble ( discounts [ 2 ] ) );
			break;
		case Invoice.STATE_BASKET:
			invoice.setBasketBig ( transactionDetail.getQuantityBig ().intValue () );
			invoice.setBasketMedium ( transactionDetail.getQuantityMedium ().intValue () );
			invoice.setBasketSmall ( transactionDetail.getQuantitySmall ().intValue () );
			invoice.setQuantityPromotedBasket ( transactionDetail.getBasicUnitQuantity ().intValue () );
			invoice.setPromotionDiscountPercentageBasket ( transactionDetail.getDiscountPercentage () );
			break;
		case Invoice.STATE_FREE:
			invoice.setFreeBig ( transactionDetail.getQuantityBig ().intValue () );
			invoice.setFreeMedium ( transactionDetail.getQuantityMedium ().intValue () );
			invoice.setFreeSmall ( transactionDetail.getQuantitySmall ().intValue () );
			invoice.setQuantityPromotedFree ( transactionDetail.getBasicUnitQuantity ().intValue () );
			invoice.setPromotionDiscountPercentage ( transactionDetail.getDiscountPercentage () );
			break;
		default:
			break;
		} // End switch
		// Return the updated invoice
		return invoice;
	}
	
	/**
	 * Computes and returns the gross, discount, tax and total amounts for the current sales invoice.
	 * 
	 * @return	A {@link me.SyncWise.Android.Modules.SalesInvoice.SalesInvoiceDetails SalesInvoiceDetails} object hosting the gross, discount, tax and total amounts.
	 */
	protected SalesInvoiceDetails computeDetails () {
		// Declare and initialize the amounts
		SalesInvoiceDetails salesInvoiceDetail = new SalesInvoiceDetails ();
		// Check if the selected invoice is valid
		if ( selectedInvoices == null )
			// Invalid list, do nothing
			return salesInvoiceDetail;
		// Compute the sales invoice details
    	double grossAmount = 0;
    	double discountAmount = 0;
    	double taxes = 0;
    	double totalValue = 0;
		// Iterate over all the selected invoices
		for ( Invoice invoice : selectedInvoices ) {
			// Compute the current gross value
			double _grossAmount = invoice.getTotalAmount ( Invoice.STATE_ORDERED , false , false ) + invoice.getTotalAmount ( Invoice.STATE_BASKET , false , false ) + invoice.getTotalAmount ( Invoice.STATE_FREE , false , false );
    		// Compute the discount amount // invoice.getDiscountAmount ( Invoice.STATE_ORDERED ) + invoice.getDiscountAmount ( Invoice.STATE_BASKET )
    		double _discountAmount =  invoice.getDiscountAmount ( Invoice.STATE_ORDERED )  + invoice.getDiscountAmount ( Invoice.STATE_BASKET ) + invoice.getDiscountAmount ( Invoice.STATE_FREE );
			// Compute the current tax value
			double _taxes = 0;
			// Check if the item is taxable
			if ( invoice.getTax () != 0 )
				// Compute the current taxes
				_taxes = ( _grossAmount - _discountAmount ) * invoice.getTax () / 100;
			// Compute the current total value
			double _totalValue = ( _grossAmount - _discountAmount ) + _taxes;
			// Update all the values
			grossAmount += _grossAmount;
			discountAmount += _discountAmount;
			taxes += _taxes;
			totalValue += _totalValue;
		} // End for each
		// Return the computed amounts
		salesInvoiceDetail.setGrossAmount ( grossAmount );
		salesInvoiceDetail.setDiscountAmount ( discountAmount );
		salesInvoiceDetail.setTaxes ( taxes );
		salesInvoiceDetail.setTotalValue ( totalValue );
		return salesInvoiceDetail;
	}
	
	/**
	 * Initializes and computes all the sales invoice details (if any).
	 */
	@SuppressLint("DefaultLocale") 
	protected void initializeDetails () {

		//getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false )
		if (  requestCode != REQUEST_CODE_INFO  ||  ! getIntent ().getBooleanExtra ( IS_VIEW_ONLY , false )  ){
			// Compute the details
			SalesInvoiceDetails salesInvoiceDetail = computeDetails ();
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
			// Initialize the list of sales invoice details
			details = new ArrayList < InvoiceInfo > ();
			// Populate the details list
			details.add ( new InvoiceInfo ( InvoiceInfo.ID.NOTE , AppResources.getInstance ( this ).getString ( this , R.string.sales_order_note_label ) , note ) );
			details.add ( new InvoiceInfo ( InvoiceInfo.ID.CURRENCY , AppResources.getInstance ( this ).getString ( this , R.string.currency_label ).toUpperCase () , currency.getCurrencyName () ) );
			details.add ( new InvoiceInfo ( InvoiceInfo.ID.GROSS_VALUE , AppResources.getInstance ( this ).getString ( this , R.string.gross_amount_label ) , moneyFormat.format ( salesInvoiceDetail.getGrossAmount () ) ) );
			details.add ( new InvoiceInfo ( InvoiceInfo.ID.DISC_VALUE , AppResources.getInstance ( this ).getString ( this , R.string.discount_amount_label ) , moneyFormat.format ( salesInvoiceDetail.getDiscountAmount () ) ) );
			details.add ( new InvoiceInfo ( InvoiceInfo.ID.NET_VALUE , AppResources.getInstance ( this ).getString ( this , R.string.net_amount_label ) , moneyFormat.format ( salesInvoiceDetail.getGrossAmount () - salesInvoiceDetail.getDiscountAmount () ) ) );
//			details.add ( new InvoiceInfo ( InvoiceInfo.ID.TAXES , AppResources.getInstance ( this ).getString ( this , R.string.taxes_label ) , moneyFormat.format ( salesInvoiceDetail.getTaxes () ) ) );
			details.add ( new InvoiceInfo ( InvoiceInfo.ID.TOTAL_VALUE , AppResources.getInstance ( this ).getString ( this , R.string.total_value_label ) , moneyFormat.format ( salesInvoiceDetail.getTotalValue () ) ) );
			details.add ( new InvoiceInfo ( InvoiceInfo.ID.TOTAL_SKU_SO , AppResources.getInstance ( this ).getString ( this , R.string.total_sku_so_label ) ," " + selectedInvoices.size()  ) );
			double cases=0d,units=0d;
		
			for ( Invoice detail : selectedInvoices ) {
				cases=cases+detail.getQuantityMedium();
				units=units+detail.getQuantitySmall();
			}
			details.add ( new InvoiceInfo ( InvoiceInfo.ID.TOTAL_CASES , AppResources.getInstance ( this ).getString ( this , R.string.total_cases_si_label ) ," " + cases  ) );
			details.add ( new InvoiceInfo ( InvoiceInfo.ID.TOTAL_UNITS , AppResources.getInstance ( this ).getString ( this , R.string.total_units_si_label ) ," " + units  ) );
		
		}
		else{
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
			// Initialize the list of sales invoice details
			details = new ArrayList < InvoiceInfo > ();
			// Populate the details list
			details.add ( new InvoiceInfo ( InvoiceInfo.ID.NOTE , AppResources.getInstance ( this ).getString ( this , R.string.sales_order_note_label ) , note ) );
			details.add ( new InvoiceInfo ( InvoiceInfo.ID.CURRENCY , AppResources.getInstance ( this ).getString ( this , R.string.currency_label ).toUpperCase () , currency.getCurrencyName () ) );
			details.add ( new InvoiceInfo ( InvoiceInfo.ID.GROSS_VALUE , AppResources.getInstance ( this ).getString ( this , R.string.gross_amount_label ) , moneyFormat.format ( transactionHeader.getGrossAmount () ) ) );
			details.add ( new InvoiceInfo ( InvoiceInfo.ID.DISC_VALUE , AppResources.getInstance ( this ).getString ( this , R.string.discount_amount_label ) , moneyFormat.format ( transactionHeader.getDiscountAmount () ) ) );
			details.add ( new InvoiceInfo ( InvoiceInfo.ID.NET_VALUE , AppResources.getInstance ( this ).getString ( this , R.string.net_amount_label ) , moneyFormat.format ( transactionHeader.getNetAmount() ) ) );
//			details.add ( new InvoiceInfo ( InvoiceInfo.ID.TAXES , AppResources.getInstance ( this ).getString ( this , R.string.taxes_label ) , moneyFormat.format ( salesInvoiceDetail.getTaxes () ) ) );
			details.add ( new InvoiceInfo ( InvoiceInfo.ID.TOTAL_VALUE , AppResources.getInstance ( this ).getString ( this , R.string.total_value_label ) , moneyFormat.format ( transactionHeader.getTotalTaxAmount() ) ) );
			details.add ( new InvoiceInfo ( InvoiceInfo.ID.TOTAL_SKU_SO , AppResources.getInstance ( this ).getString ( this , R.string.total_sku_si_label ) ," " + selectedInvoices.size()  ) );
			
			//Compute the total case and unit 
			double cases=0d,units=0d;
		
			for ( Invoice detail : selectedInvoices ) {
				cases=cases+detail.getQuantityMedium();
				units=units+detail.getQuantitySmall();
			}
			details.add ( new InvoiceInfo ( InvoiceInfo.ID.TOTAL_CASES , AppResources.getInstance ( this ).getString ( this , R.string.total_cases_si_label ) ," " + cases  ) );
			details.add ( new InvoiceInfo ( InvoiceInfo.ID.TOTAL_UNITS , AppResources.getInstance ( this ).getString ( this , R.string.total_units_si_label ) ," " + units  ) );
		}
		}
	
	/**
	 * Refreshes / recomputes all the sales invoice details (if any).
	 */
	private void refreshDetails () {
		// Check if the sales invoices details lists is valid
		if ( details == null )
			// Invalid list, do nothing
			return;
		
		// Compute the details
		SalesInvoiceDetails salesInvoiceDetail = computeDetails ();
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
		for ( InvoiceInfo detail : details ) {
			// Check if the current detail is about the sales invoice gross amount
			if ( detail.getId () == InvoiceInfo.ID.GROSS_VALUE ) {
				// Update the sales invoice gross amount
				detail.setValue ( moneyFormat.format ( salesInvoiceDetail.getGrossAmount () ) );
			} // End if
			// Check if the current detail is about the sales invoice discount amount
			else if ( detail.getId () == InvoiceInfo.ID.DISC_VALUE ) {
				// Update the sales invoice discount amount
				detail.setValue ( moneyFormat.format ( salesInvoiceDetail.getDiscountAmount () ) );
			} // End else if
			// Check if the current detail is about the sales invoice net amount
			else if ( detail.getId () == InvoiceInfo.ID.NET_VALUE ) {
				// Update the sales invoice total net amount
				detail.setValue ( moneyFormat.format ( salesInvoiceDetail.getGrossAmount () - salesInvoiceDetail.getDiscountAmount () ) );
			} // End else if
			// Check if the current detail is about the sales invoice taxes
			else if ( detail.getId () == InvoiceInfo.ID.TAXES ) {
				// Update the sales invoice total taxes
				detail.setValue ( moneyFormat.format ( salesInvoiceDetail.getTaxes () ) );
			} // End else if
			// Check if the current detail is about the sales invoice total amount
			else if ( detail.getId () == InvoiceInfo.ID.TOTAL_VALUE ) {
				// Update the sales invoice total amount
				detail.setValue ( moneyFormat.format ( salesInvoiceDetail.getTotalValue () ) );
			} // End else if
		} // End for each
	}
	
	/**
	 * AsyncTask helper class used to filter the list.
	 * 
	 * @author Elias
	 *
	 */
	private class FilterList extends AsyncTask < Void , Void , ArrayList < Invoice > > {
		
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
		protected ArrayList < Invoice > doInBackground ( Void ... params ) {
			// Check if the list of invoices is valid
			if ( invoices == null || invoices.isEmpty () )
				// Invalid invoices list
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
		protected void onPostExecute ( ArrayList < Invoice > invoices ) {
			// Check if the request code is INFO
			if ( requestCode == REQUEST_CODE_INFO )
				// Cannot filter, do nothing
				return;
			// Check if the result is valid
			if ( invoices == null )
				// Invalid result, do nothing
				return;
			// Check if the search query has changed
			if ( ! SalesInvoiceDetailsActivity.this.searchQuery.equals ( searchQuery ) )
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
				// Clear the previous list of invoices
				( (ArrayAdapter < Invoice >) getListAdapter () ).clear ();
				// Add the new filtered list of invoices
				( (ArrayAdapter < Invoice >) getListAdapter () ).addAll ( invoices );
				// Notifies the attached observers that the underlying data has been changed
				( (ArrayAdapter < Invoice >) getListAdapter () ).notifyDataSetChanged ();
			} catch ( Exception exception ) {
				// Cannot cast to array adapter of invoices objects
			} // End try-catch block
		}
		
	}
	/**
	 * Filters the {@link #invoices} list based on the indicated {@link #searchQuery} string and the {@link #selectedDivisionsCodes} list.<br>
	 * <b>NOTE : </b> This method does not perform any kind of integrity check what so ever !
	 * 
	 * @return	List of {@link me.SyncWise.Android.Modules.SalesInvoice.Invoice Invoice} objects filtered according to the search and divisions filter.
	 */
	@SuppressLint("DefaultLocale") 
	protected ArrayList < Invoice > filterStock () {
		// Declare and initialize a new invoices list
		ArrayList < Invoice > _invoices = new ArrayList < Invoice > ();
		List < VehiclesStock > vehicleStocks =  DatabaseUtils.getInstance ( this ).getDaoSession ().getVehiclesStockDao ().queryBuilder ()
				.where ( VehiclesStockDao.Properties.GoodQuantity.gt(0)  ).list();
		
		
		
		
		// Determine if at least one of the invoice's divisions is selected
		boolean skip = true;
	 
		// Iterate over all the invoices
		for ( Invoice invoice : invoices ) {
				// Determine if at least one of the invoice's divisions is selected
				//boolean skip = true;
				// Iterate over all invoice's division
				for ( VehiclesStock vehicleStock : vehicleStocks )
					// Check if the current division is selected
					if ( invoice.getItem().getItemCode().equals(vehicleStock.getItemCode() )) {
					    	_invoices.add ( invoice );
 					}
//				if ( invoice.getItem ().getItemName ().toLowerCase ().contains ( searchQuery.toLowerCase () )
//						|| invoice.getItem ().getItemCode ().toLowerCase ().contains ( searchQuery.toLowerCase () )
//						|| ( invoice.getBarCodes () != null && invoice.getBarCodes ().contains ( searchQuery.toLowerCase () ) ) )
//					_invoices.add ( invoice );
			} // End for each
	 
		//invoices.clear();
		//invoices.addAll(_invoices);
		
		// Return the new invoices list
		return _invoices;
	}
	/**
	 * AsyncTask helper class used to filter the list.
	 * 
	 * @author Elias
	 *
	 */
	private class FilterListStock extends AsyncTask < Void , Void , ArrayList < Invoice > > {
		
		/**
		 * String holding the search query being applied on the list.
		 */
		private String searchQuery;
		/**
		 * Constructor.
		 * 
		 * @param searchQuery	String holding the search query being applied on the list.
		 */
		public FilterListStock ( String searchQuery ) {
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
		protected ArrayList < Invoice > doInBackground ( Void ... params ) {
			// Check if the list of invoices is valid
			if ( invoices == null || invoices.isEmpty () )
				// Invalid invoices list
				return null;
			// Otherwise there is a filter
			// Compute and return the filtered list based on the provided search query
			return filterStock ();
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute ( ArrayList < Invoice > invoices ) {
			// Check if the request code is INFO
			if ( requestCode == REQUEST_CODE_INFO )
				// Cannot filter, do nothing
				return;
			// Check if the result is valid
			if ( invoices == null )
				// Invalid result, do nothing
				return;
			// Check if the search query has changed
			if ( ! SalesInvoiceDetailsActivity.this.searchQuery.equals ( searchQuery ) )
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
				// Clear the previous list of invoices
				( (ArrayAdapter < Invoice >) getListAdapter () ).clear ();
				// Add the new filtered list of invoices
				( (ArrayAdapter < Invoice >) getListAdapter () ).addAll ( invoices );
				// Notifies the attached observers that the underlying data has been changed
				( (ArrayAdapter < Invoice >) getListAdapter () ).notifyDataSetChanged ();
			} catch ( Exception exception ) {
				// Cannot cast to array adapter of invoices objects
			} // End try-catch block
		}
		
	}
}