/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Collections;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import me.SyncWise.Android.ActivityTransition;
import me.SyncWise.Android.App;
import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.AppToast;
import me.SyncWise.Android.DateTime;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Vibration;
import me.SyncWise.Android.Database.Banks;
import me.SyncWise.Android.Database.BanksDao;
import me.SyncWise.Android.Database.ClientDues;
 
import me.SyncWise.Android.Database.CollectionDetails;
import me.SyncWise.Android.Database.CollectionHeaders;
import me.SyncWise.Android.Database.CollectionInvoices;
import me.SyncWise.Android.Database.CollectionUtils;
import me.SyncWise.Android.Database.Companies;
import me.SyncWise.Android.Database.CompaniesDao;
import me.SyncWise.Android.Database.Currencies;
import me.SyncWise.Android.Database.CurrenciesDao;
import me.SyncWise.Android.Database.DaoSession;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Database.StatusUtils;
import me.SyncWise.Android.Database.TargetHeadersUtils;
import me.SyncWise.Android.Database.TotalClientDues;
import me.SyncWise.Android.Database.TotalClientDuesDao;
import me.SyncWise.Android.Database.TransactionHeaders;
import me.SyncWise.Android.Database.TransactionHeadersDao;
import me.SyncWise.Android.Database.TransactionHeadersUtils;
import me.SyncWise.Android.Database.TransactionSequences;
import me.SyncWise.Android.Database.TransactionSequencesDao;
import me.SyncWise.Android.Database.Users;
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Database.Visits;
import me.SyncWise.Android.Gson.BaseTimerActivity;
import me.SyncWise.Android.Modules.Call.CallAction;
import me.SyncWise.Android.Modules.Call.CallMenuActivity;
import me.SyncWise.Android.Modules.ClientsList.ClientInfo.ClientInfoActivity;
import me.SyncWise.Android.Modules.Collections.PaymentsAdapter.ViewHolder;
import me.SyncWise.Android.Modules.Journey.Call;
import me.SyncWise.Android.Modules.Printing.PrintingActivity;
 
import me.SyncWise.Android.Modules.Target.TargetUpdate;
import me.SyncWise.Android.Modules.Target.TargetUpdate.UpdateType;
import me.SyncWise.Android.Utilities.ClientCard;
import me.SyncWise.Android.Utilities.Collection;
import me.SyncWise.Android.Widgets.Baguette;
import me.SyncWise.Android.Widgets.CustomLinearLayout;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

/**
 * Activity implemented to perform collections.
 * 
 * @author Elias
 * @sw.todo <b>Collection Details Activity Implementation :</b><br>
 *          Simply add this class in the {@code AndroidManifest.xml} file along
 *          with the permission :<br>
 *          {@code <uses-permission android:name="android.permission.VIBRATE"/>}<br>
 *          AND disable activity recreation for orientation events by adding the
 *          following to the activity tag in the manifest file :<br>
 *          {@code android:configChanges="orientation|screenSize"}
 * 
 */
public class CollectionDetailsActivity extends BaseTimerActivity implements
		OnItemSelectedListener, OnItemClickListener,
		DatePickerDialog.OnDateSetListener {

	/**
	 * Constant integer holding the request code used to print invoices.
	 */
	private static final int REQUEST_CODE_PRINT = 10;

	/**
	 * Reference to the collection list population task.
	 */
	protected static PopulateList populateList;
	private boolean saveMovements ;
	private static  final  String SAVE_MOVEMENTS = CollectionDetailsActivity.class.getName () + ".SAVE_MOVEMENTS";

	/**
	 * Bundle key used to determine if the call is a collection only.
	 */
	public static final String COLLECTION_ONLY = CollectionDetailsActivity.class
			.getName() + ".COLLECTION_ONLY";

	/**
	 * Bundle key used to put/retrieve the content of {@link #isCreated}.
	 */
	private static final String IS_CREATED = CollectionDetailsActivity.class
			.getName() + ".IS_CREATED";

	/**
	 * Boolean used to indicate if the activity has been previously created.<br>
	 * This flag is mainly used to determine if the current activity creation is
	 * the first or not (activity re-creation due to phone rotation for
	 * example).
	 */
	private boolean isCreated;

	/**
	 * Boolean used to indicate if the activity is displayed on the screen.
	 */
	public static boolean isDisplayed;

	/**
	 * Bundle key used to put/retrieve the content of the current call.
	 */
	public static final String CALL = CollectionDetailsActivity.class.getName()
			+ ".CALL";

	/**
	 * Bundle key used to put/retrieve the content of the current visit.
	 */
	public static final String VISIT = CollectionDetailsActivity.class
			.getName() + ".VISIT";

	/**
	 * Bundle key used to put/retrieve the content of
	 * {@link #selectedCurrencyIndex}.
	 */
	public static final String COLLECTION_MODE = CollectionDetailsActivity.class
			.getName() + ".COLLECTION_MODE";

	/**
	 * Integer used to host the current collection mode.
	 */
	private Integer collectionMode;

	/**
	 * Constant integer hosting the payment on account mode.
	 */
	public static final int PAYMENT_ON_ACCOUNT = 1;

	/**
	 * Constant integer hosting the payment client dues mode.
	 */
	public static final int PAYMENT_CLIENT_DUES = 2;

	/**
	 * Constant integer hosting the payment invoices mode.
	 */
	public static final int PAYMENT_INVOICES = 3;

	/**
	 * Bundle key used to put/retrieve the content of the check only flag.
	 */
	public static final String CHECK_ONLY = CollectionDetailsActivity.class
			.getName() + ".CHECK_ONLY";

	/**
	 * Reference to the currencies list.
	 */
	private ArrayList<Currencies> currencies;

	/**
	 * Reference to the banks list.
	 */
	private ArrayList<Banks> banks;

	/**
	 * Bundle key used to put/retrieve the content of
	 * {@link #selectedCurrencyIndex}.
	 */
	private static final String SELECTED_CURRENCY_INDEX = CollectionDetailsActivity.class
			.getName() + ".SELECTED_CURRENCY";

	/**
	 * Integer hosting the selected currency index.
	 */
	private int selectedCurrencyIndex;

	/**
	 * Bundle key used to put/retrieve the content of
	 * {@link #selectedCurrencyPaymentIndex}.
	 */
	private static final String SELECTED_CURRENCY_PAYMENT_INDEX = CollectionDetailsActivity.class
			.getName() + ".SELECTED_CURRENCY_PAYMENT_INDEX";

	/**
	 * Integer hosting the selected currency payment index.
	 */
	private int selectedCurrencyPaymentIndex;

	/**
	 * Bundle key used to put/retrieve the content of
	 * {@link #selectedBankPaymentIndex}.
	 */
	private static final String SELECTED_BANK_PAYMENT_INDEX = CollectionDetailsActivity.class
			.getName() + ".SELECTED_BANK_PAYMENT_INDEX";

	/**
	 * Integer hosting the selected bank payment index.
	 */
	private int selectedBankPaymentIndex;

	/**
	 * Bundle key used to put/retrieve the content of
	 * {@link #selectedPaymentIndex}.
	 */
	private static final String SELECTED_PAYMENT_INDEX = CollectionDetailsActivity.class
			.getName() + ".SELECTED_PAYMENT_INDEX";

	/**
	 * Integer hosting the selected payment index.
	 */
	private Integer selectedPaymentIndex;

	/**
	 * Bundle key used to put/retrieve the content of {@link #totalClientDues}.
	 */
	private static final String TOTAL_CLIENT_DUES = CollectionDetailsActivity.class
			.getName() + ".TOTAL_CLIENT_DUES";

	/**
	 * Reference to the total collection dues list.
	 */
	private ArrayList<TotalClientDues> totalClientDues;

	/**
	 * Bundle key used to put/retrieve the content of {@link #clientDues}.
	 */
	private static final String CLIENT_DUES = CollectionDetailsActivity.class
			.getName() + ".CLIENT_DUES";

	/**
	 * Reference to the client dues list.
	 */
	private ArrayList<ClientDues> clientDues;

	/**
	 * Bundle key used to put/retrieve the content of {@link #invoices}.
	 */
	private static final String INVOICES = CollectionDetailsActivity.class
			.getName() + ".INVOICES";

	/**
	 * Reference to the invoices.
	 */
	private ArrayList<TransactionHeaders> invoices;

	/**
	 * Bundle key used to put/retrieve the content of {@link #collectionHeader}.
	 */
	private static final String COLLECTION_HEADER = CollectionDetailsActivity.class
			.getName() + ".COLLECTION_HEADER";

	/**
	 * Reference to the collection header.
	 */
	private CollectionHeaders collectionHeader;

	/**
	 * Bundle key used to put/retrieve the content of {@link #collectionDetails}
	 * .
	 */
	private static final String COLLECTION_DETAILS = CollectionDetailsActivity.class
			.getName() + ".COLLECTION_HEADERS";

	/**
	 * Reference to the collection details list.
	 */
	private ArrayList<CollectionDetails> collectionDetails;

	/**
	 * Bundle key used to indicate if the company is to be prompted.
	 */
	public static final String PROMT_COMPANY = CollectionDetailsActivity.class
			.getName() + ".PROMT_COMPANY";

	/**
	 * Bundle key used to put/retrieve the content of {@link #company}.
	 */
	private static final String COMPANY = CollectionDetailsActivity.class
			.getName() + ".COMPANY";

	/**
	 * A {@link me.SyncWise.Android.Database.Companies Companies} object used to
	 * host the selected company.
	 */
	protected Companies company;

	/**
	 * Bundle key used to put/retrieve the content of
	 * {@link #displayCollectionNote}.
	 */
	private static final String DISPLAY_COLLECTION_NOTE = CollectionDetailsActivity.class
			.getName() + ".DISPLAY_COLLECTION_NOTE";

	/**
	 * Boolean used to determine whether to display the collection note UI or
	 * not.<br>
	 * This boolean is mainly used to save the collection state.
	 */
	protected boolean displayCollectionNote;

	/**
	 * Bundle key used to put/retrieve the content of
	 * {@link #displayCollectionManualReference}.
	 */
	private static final String DISPLAY_COLLECTION_MANUAL_REFERENCE = CollectionDetailsActivity.class
			.getName() + ".DISPLAY_COLLECTION_MANUAL_REFERENCE";

	/**
	 * Boolean used to determine whether to display the collection manual
	 * reference UI or not.<br>
	 * This boolean is mainly used to save the collection state.
	 */
	protected boolean displayCollectionManualReference;

	/**
	 * Bundle key used to put/retrieve the content of
	 * {@link #displayPaymentCash}.
	 */
	private static final String DISPLAY_PAYMENT_CASH = CollectionDetailsActivity.class
			.getName() + ".DISPLAY_PAYMENT_CASH";

	/**
	 * Boolean used to determine whether to display the cash payment UI or not.<br>
	 * This boolean is mainly used to save the collection state.
	 */
	protected boolean displayPaymentCash;

	/**
	 * Bundle key used to put/retrieve the content of
	 * {@link #displayPaymentCheck}.
	 */
	private static final String DISPLAY_PAYMENT_CHECK = CollectionDetailsActivity.class
			.getName() + ".DISPLAY_PAYMENT_CHECK";

	/**
	 * Boolean used to determine whether to display the check payment UI or not.<br>
	 * This boolean is mainly used to save the collection state.
	 */
	protected boolean displayPaymentCheck;

	/**
	 * Bundle key used to put/retrieve the content of {@link #displayPaidAmount}
	 * .
	 */
	private static final String DISPLAY_PAID_AMOUNT = CollectionDetailsActivity.class
			.getName() + ".DISPLAY_PAID_AMOUNT";

	/**
	 * Boolean used to determine whether to display the paid amount UI or not.<br>
	 * This boolean is mainly used to save the collection state.
	 */
	protected boolean displayPaidAmount;

	/**
	 * Bundle key used to put/retrieve the content of
	 * {@link #displayPrintingConfirmation}.
	 */
	private static final String DISPLAY_PRINTING_CONFIRMATION = CollectionDetailsActivity.class
			.getName() + ".DISPLAY_PRINTING_CONFIRMATION";

	/**
	 * Boolean used to determine whether to display the printing confirmation.
	 */
	private boolean displayPrintingConfirmation;

	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@SuppressLint("DefaultLocale")
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Superclass method call
		super.onCreate(savedInstanceState);
		// Change the title associated with this activity
		setTitle(AppResources.getInstance(this).getString(this,
				R.string.collection_details_activity_title));
		// Set the activity content from a layout resource.
		setContentView(R.layout.collection_details_activity);
		// Retrieve a reference to the payments and dues layouts
		View paymentsLayout = findViewById(R.id.layout_payments);
		View duesLayout = findViewById(R.id.layout_dues);
		// Retrieve a reference to the payments and dues lists
		ListView paymentsList = (ListView) paymentsLayout
				.findViewById(R.id.list_payments);
		ListView duesList = (ListView) duesLayout.findViewById(R.id.list_dues);
		// Retrieve a reference to the empty views
		TextView paymentsEmptyView = (TextView) paymentsLayout
				.findViewById(R.id.empty_list_view);
		TextView duesEmptyView = (TextView) duesLayout
				.findViewById(R.id.empty_list_view);
		// Define the empty list view for the lists
		paymentsList.setEmptyView(paymentsEmptyView);
		duesList.setEmptyView(duesEmptyView);
		// Display empty list label
		paymentsEmptyView.setText(AppResources.getInstance(this).getString(
				this, R.string.empty_collection_payment_list_label));
		duesEmptyView.setText(AppResources.getInstance(this).getString(this,
				R.string.empty_collection_dues_list_label));
		// Display the add cash and check button label
		((TextView) findViewById(R.id.label_button_cash)).setText(AppResources
				.getInstance(this).getString(this, R.string.cash_label)
				.toUpperCase());
		((TextView) findViewById(R.id.label_button_check)).setText(AppResources
				.getInstance(this).getString(this, R.string.check_label)
				.toUpperCase());
		// Disable the add cash and check buttons
		disableAddButtons();
		saveMovements=true;
		// Check if both the visit and the call are valid
		if (getIntent().getSerializableExtra(CALL) == null
				&& getIntent().getSerializableExtra(VISIT) == null) {
			// Indicate that the activity cannot be displayed
			new AppToast(this)
					.setIcon(R.drawable.warning)
					.setText(
							AppResources.getInstance(this).getString(this,
									R.string.invalid_selection_label)).show();
			// Check if this is a collection only call
			if (getIntent().getBooleanExtra(COLLECTION_ONLY, false)) {
				// Retrieve the visit object
				Visits visit = (Visits) getIntent().getSerializableExtra(VISIT);
				// Refresh the visit object
				DatabaseUtils.getInstance(this).getDaoSession().getVisitsDao()
						.refresh(visit);
				// End the visit
				visit.setEndDate(Calendar.getInstance().getTime());
				// Update the visit in DB
				DatabaseUtils.getInstance(this).getDaoSession().getVisitsDao()
						.update(visit);

				// Clear call action data
				CallAction.clearData(this);
				// Call this to set the result that your activity will return to
				// its caller
				setResult(
						RESULT_OK,
						new Intent().putExtra(CallMenuActivity.VISIT, visit)
								.putExtra(CallMenuActivity.CALL,
										getIntent().getSerializableExtra(CALL)));
			} // End if
				// Finish activity
			finish();
			// Exit method
			return;
		} // End if

		// Initialize the collection mode
		int temp = getIntent().getIntExtra(COLLECTION_MODE, -1);
		collectionMode = temp < 0 ? null : temp;

		// Determine if a saved instance state is provided
		if (savedInstanceState != null) {
			// Restore the content provided by the saved instance state
			isCreated = savedInstanceState.getBoolean(IS_CREATED, isCreated);
			selectedCurrencyIndex = savedInstanceState.getInt(
					SELECTED_CURRENCY_INDEX, selectedCurrencyIndex);
			temp = savedInstanceState.getInt(SELECTED_PAYMENT_INDEX, -1);
			selectedPaymentIndex = temp < 0 ? null : temp;
			temp = savedInstanceState.getInt(COLLECTION_MODE, -1);
			collectionMode = temp < 0 ? null : temp;
			selectedCurrencyPaymentIndex = savedInstanceState.getInt(
					SELECTED_CURRENCY_PAYMENT_INDEX,
					selectedCurrencyPaymentIndex);
			selectedBankPaymentIndex = savedInstanceState.getInt(
					SELECTED_BANK_PAYMENT_INDEX, selectedBankPaymentIndex);
			totalClientDues = (ArrayList<TotalClientDues>) savedInstanceState
					.getSerializable(TOTAL_CLIENT_DUES);
			clientDues = (ArrayList<ClientDues>) savedInstanceState
					.getSerializable(CLIENT_DUES);
			invoices = (ArrayList<TransactionHeaders>) savedInstanceState
					.getSerializable(INVOICES);
			collectionHeader = (CollectionHeaders) savedInstanceState
					.getSerializable(COLLECTION_HEADER);
			collectionDetails = (ArrayList<CollectionDetails>) savedInstanceState
					.getSerializable(COLLECTION_DETAILS);
			displayCollectionNote = savedInstanceState
					.getBoolean(DISPLAY_COLLECTION_NOTE);
			displayCollectionManualReference = savedInstanceState
					.getBoolean(DISPLAY_COLLECTION_MANUAL_REFERENCE);
			displayPaymentCash = savedInstanceState
					.getBoolean(DISPLAY_PAYMENT_CASH);
			displayPaymentCheck = savedInstanceState
					.getBoolean(DISPLAY_PAYMENT_CHECK);
			displayPaidAmount = savedInstanceState
					.getBoolean(DISPLAY_PAID_AMOUNT);
			company = (Companies) savedInstanceState.getSerializable(COMPANY);
			displayPrintingConfirmation = savedInstanceState
					.getBoolean(DISPLAY_PRINTING_CONFIRMATION);
			saveMovements = savedInstanceState.getBoolean ( SAVE_MOVEMENTS , saveMovements );
		} // End if

		// Display the header labels
		((TextView) findViewById(R.id.label_payments)).setText(AppResources
				.getInstance(this).getString(this, R.string.payments_label));
		((TextView) findViewById(R.id.label_dues))
				.setText(AppResources
						.getInstance(this)
						.getString(
								this,
								collectionMode == null
										|| collectionMode != PAYMENT_INVOICES ? R.string.dues_label
										: R.string.invoices_label));
		// Hide additional layouts
		findViewById(R.id.layout_collection_note).setVisibility(View.GONE);
		findViewById(R.id.layout_collection_manual_reference).setVisibility(
				View.GONE);
		findViewById(R.id.layout_payment_cash).setVisibility(View.GONE);
		findViewById(R.id.layout_payment_check).setVisibility(View.GONE);
		findViewById(R.id.layout_paid_amount).setVisibility(View.GONE);
		findViewById(R.id.layout_payment_info).setVisibility(View.GONE);

		// Determine if this is the first activity creation or a re-creation
		if (!isCreated)
			// Set flag
			isCreated = true;
		// Check if the collection mode is valid
		if (collectionMode == null)
			// Prompt for the collection mode
			promptCollectionMode();
		else
			// Validate the company
			validateCompany();
	}

	/**
	 * Validates the company and populates the module accordingly.
	 */
	private void validateCompany() {
		// Check if the company is to be prompted
		if (company == null
				&& getIntent().getBooleanExtra(PROMT_COMPANY, false))
			// Prompt for the company
			promptCompany();
		// Check if the company is valid
		else if (company == null) {
			// Initialize the user's company
			company = DatabaseUtils
					.getInstance(this)
					.getDaoSession()
					.getCompaniesDao()
					.queryBuilder()
					.where(CompaniesDao.Properties.CompanyCode.eq(DatabaseUtils
							.getCurrentCompanyCode(this))).unique();
			// Populate asynchronously
			populate();
		} // end if
		else
			// Otherwise populate asynchronously
			populate();
	}

	/**
	 * Updates and displays the payment info.
	 */
	public void updatePaymentInfo() {
		// Check if the payment info are needed
		if (collectionMode == null || collectionMode == PAYMENT_ON_ACCOUNT) {
			// Hide the layout accordingly
			findViewById(R.id.layout_payment_info).setVisibility(View.GONE);
			// Exit method
			return;
		} // End if
			// Display the layout accordingly
		findViewById(R.id.layout_payment_info).setVisibility(View.VISIBLE);
		// Map the currencies to their codes
		HashMap<String, Currencies> _currencies = new HashMap<String, Currencies>();
		// Iterate over the currencies
		for (Currencies currency : currencies)
			// Map the currency to its code
			_currencies.put(currency.getCurrencyCode(), currency);
		// Retrieve the main currency
		Currencies currency = (Currencies) ((Spinner) findViewById(R.id.spinner_currencies))
				.getSelectedItem();
		// Compute the total payments label
		DecimalFormat moenyFormat = new DecimalFormat("#,##0.00");
		String totalPaymentsLabel = AppResources.getInstance(this).getString(
				this, R.string.collection_total_payments_label)
				+ " : ";
		double totalPayments = 0.0;
		for (CollectionDetails collectionDetail : collectionDetails)
			totalPayments += collectionDetail.getCollectionAmount()
					/ _currencies.get(collectionDetail.getCurrencyCode())
							.getCurrencyRate() * currency.getCurrencyRate();
		SpannableString payments = new SpannableString(totalPaymentsLabel
				+ moenyFormat.format(totalPayments) + " "
				+ currency.getCurrencySymbol());
		payments.setSpan(new ForegroundColorSpan(Color.BLACK), 0,
				totalPaymentsLabel.length(), 0);
		((TextView) findViewById(R.id.label_total_payments)).setText(payments,
				BufferType.SPANNABLE);
		// Compute the total dues label
		String totalDuesLabel = AppResources
				.getInstance(this)
				.getString(
						this,
						collectionMode == PAYMENT_CLIENT_DUES ? R.string.collection_total_dues_label
								: R.string.collection_total_invoices_label)
				+ " : ";
		double totalDues = 0.0;
		if (collectionMode == PAYMENT_CLIENT_DUES)
			for (ClientDues clientDue : clientDues)
				if (clientDue.getInvoiceType() != null)
					totalDues += (clientDue.getInvoiceType() == CollectionUtils.DuesType.DEBIT ? 1
							: -1)
							* (clientDue.getPaidAmount()
									/ _currencies.get(
											clientDue.getCurrencyCode())
											.getCurrencyRate() * currency
										.getCurrencyRate());
		if (collectionMode == PAYMENT_INVOICES)
			for (TransactionHeaders invoice : invoices)
				totalDues += invoice.getPaidAmount()
						/ _currencies.get(invoice.getCurrencyCode())
								.getCurrencyRate() * currency.getCurrencyRate();
		SpannableString dues = new SpannableString(totalDuesLabel
				+ moenyFormat.format(totalDues) + " "
				+ currency.getCurrencySymbol());
		dues.setSpan(new ForegroundColorSpan(Color.BLACK), 0,
				totalDuesLabel.length(), 0);
		((TextView) findViewById(R.id.label_total_dues)).setText(dues,
				BufferType.SPANNABLE);
	}

	/**
	 * Retrieve all the collections asynchronously.
	 */
	protected void populate() {
		// Retrieve all the collections / payments / dues asynchronously
		populateList = new PopulateList();
		populateList.execute();
	}

	/**
	 * Refresh the add buttons state.<br>
	 * The add buttons are disabled if there are no dues / invoices selected.
	 */
	public void refreshAddButtonsState() {
		// Check if the collection mode is on account
		if (collectionMode == PAYMENT_ON_ACCOUNT) {
			// Enable buttons
			enableAddButtons();
			// Exit method
			return;
		} // End if
			// Map the currencies to their codes
		HashMap<String, Currencies> _currencies = new HashMap<String, Currencies>();
		// Iterate over the currencies
		for (Currencies currency : currencies)
			// Map the currency to its code
			_currencies.put(currency.getCurrencyCode(), currency);
		// Compute the total selected dues / invoices
		Currencies currency = (Currencies) ((Spinner) findViewById(R.id.spinner_currencies))
				.getSelectedItem();
		double totalDues = 0.0;
		if (collectionMode == PAYMENT_CLIENT_DUES)
			for (ClientDues clientDue : clientDues)
				if (clientDue.getInvoiceType() != null)
					totalDues += (clientDue.getInvoiceType() == CollectionUtils.DuesType.DEBIT ? 1
							: -1)
							* (clientDue.getPaidAmount()
									/ _currencies.get(
											clientDue.getCurrencyCode())
											.getCurrencyRate() * currency
										.getCurrencyRate());
		if (collectionMode == PAYMENT_INVOICES)
			for (TransactionHeaders invoice : invoices)
				totalDues += invoice.getPaidAmount()
						/ _currencies.get(invoice.getCurrencyCode())
								.getCurrencyRate() * currency.getCurrencyRate();
		totalDues = new BigDecimal(totalDues).setScale(
				currency.getCurrencyRounding(), RoundingMode.HALF_UP)
				.doubleValue();
		// Check if the total dues is valid
		if (totalDues > 0)
			// Enable buttons
			enableAddButtons();
		else
			// Disable buttons
			disableAddButtons();
	}

	/**
	 * Disables the add buttons.
	 */
	private void disableAddButtons() {
		// Disable the add cash and check buttons
		findViewById(R.id.layout_button_cash).setEnabled(false);
		findViewById(R.id.layout_button_check).setEnabled(false);
	}

	/**
	 * Enables the add buttons.
	 */
	private void enableAddButtons() {
		// Retrieve the current user
		Users user = DatabaseUtils
				.getInstance(this)
				.getDaoSession()
				.getUsersDao()
				.queryBuilder()
				.where(UsersDao.Properties.UserCode.eq(DatabaseUtils
						.getCurrentUserCode(this)),
						UsersDao.Properties.CompanyCode.eq(DatabaseUtils
								.getCurrentCompanyCode(this))).unique();
		boolean enablecheck=false;
		if(user.getUserType()==10 &&
		   PermissionsUtils.getEnableCheckCollection ( CollectionDetailsActivity.this ,   DatabaseUtils.getCurrentUserCode ( CollectionDetailsActivity.this ) ,  DatabaseUtils.getCurrentCompanyCode(CollectionDetailsActivity.this) ) ) {
			 
			enablecheck=true;
			}
			if(user.getCompanyCode().equals("ABP") && collectionMode==PAYMENT_CLIENT_DUES){
		// Enable the add cash and check buttons
		findViewById(R.id.layout_button_cash).setEnabled(
			 true);
		
		findViewById(R.id.layout_button_check)
				.setEnabled(
						user.getUserType() == 11 ? (ClientCard
								.isCreditClient(((Call) getIntent()
										.getSerializableExtra(CALL))
										.getClient()) ? true : false) : enablecheck);
			}else{
				// Enable the add cash and check buttons
				findViewById(R.id.layout_button_cash).setEnabled(true);
				
				findViewById(R.id.layout_button_check)
						.setEnabled(
								user.getUserType() == 11 ? (ClientCard
										.isCreditClient(((Call) getIntent()
												.getSerializableExtra(CALL))
												.getClient()) ? true : false) : enablecheck);	
			}
	}

	/**
	 * Computes and returns the remaining value.
	 * 
	 * @return Double hosting the remaining value from the dues / invoices.
	 */
	private double computeRemainingValue() {
		// Check if the collection mode is on account
		if (collectionMode == PAYMENT_ON_ACCOUNT)
			// No remaining value
			return 0;
		// Map the currencies to their codes
		HashMap<String, Currencies> _currencies = new HashMap<String, Currencies>();
		// Iterate over the currencies
		for (Currencies currency : currencies)
			// Map the currency to its code
			_currencies.put(currency.getCurrencyCode(), currency);
		// Retrieve the main currency
		Currencies currency = (Currencies) ((Spinner) findViewById(R.id.spinner_currencies))
				.getSelectedItem();
		// Compute the total payments
		double totalPayments = 0.0;
		for (CollectionDetails collectionDetail : collectionDetails)
			totalPayments += collectionDetail.getCollectionAmount()
					/ _currencies.get(collectionDetail.getCurrencyCode())
							.getCurrencyRate() * currency.getCurrencyRate();
		// Compute the total selected dues / invoices
		double totalDues = 0.0;
		if (collectionMode == PAYMENT_CLIENT_DUES)
			for (ClientDues clientDue : clientDues)
				if (clientDue.getInvoiceType() != null)
					totalDues += (clientDue.getInvoiceType() == CollectionUtils.DuesType.DEBIT ? 1
							: -1)
							* (clientDue.getPaidAmount()
									/ _currencies.get(
											clientDue.getCurrencyCode())
											.getCurrencyRate() * currency
										.getCurrencyRate());
		if (collectionMode == PAYMENT_INVOICES)
			for (TransactionHeaders invoice : invoices)
				totalDues += invoice.getPaidAmount()
						/ _currencies.get(invoice.getCurrencyCode())
								.getCurrencyRate() * currency.getCurrencyRate();
		totalDues = new BigDecimal(totalDues).setScale(
				currency.getCurrencyRounding(), RoundingMode.HALF_UP)
				.doubleValue();
		double remainainValue = totalDues - totalPayments;
		return remainainValue < 0 ? 0 : remainainValue;
	}

	/**
	 * Prompts the user to select a collection mode.
	 */
	private void promptCollectionMode() {
		// Retrieve the collection mode descriptions
		String[] collectionModeDescriptions = new String[] {
				AppResources.getInstance(this).getString(this,
						R.string.collection_mode_on_account_label),
				AppResources.getInstance(this).getString(this,
						R.string.collection_mode_client_dues_label),
//				AppResources.getInstance(this).getString(this,
//						R.string.collection_mode_invoices_label)
						};
		// Prompt the user to choose a mode
		AppDialog.getInstance().displayList(
				this,
				AppResources.getInstance(this).getString(this,
						R.string.collection_mode_dialog_title),
				collectionModeDescriptions, AppDialog.Cancelable.BACK_ONLY,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Set the selected mode
						collectionMode = which + 1;
						// Update header display
						((TextView) findViewById(R.id.label_dues))
								.setText(AppResources
										.getInstance(
												CollectionDetailsActivity.this)
										.getString(
												CollectionDetailsActivity.this,
												collectionMode == null
														|| collectionMode != PAYMENT_INVOICES ? R.string.dues_label
														: R.string.invoices_label));
						// Validate the company
						validateCompany();
					}
				}, new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						// Check if this is a collection only call
						if (getIntent().getBooleanExtra(COLLECTION_ONLY, false)) {
							// Retrieve the visit object
							Visits visit = (Visits) getIntent()
									.getSerializableExtra(VISIT);
							// Refresh the visit object
							DatabaseUtils
									.getInstance(CollectionDetailsActivity.this)
									.getDaoSession().getVisitsDao()
									.refresh(visit);
							// End the visit
							visit.setEndDate(Calendar.getInstance().getTime());
							// Update the visit in DB
							DatabaseUtils
									.getInstance(CollectionDetailsActivity.this)
									.getDaoSession().getVisitsDao()
									.update(visit);

							// Clear call action data
							CallAction
									.clearData(CollectionDetailsActivity.this);
							// Call this to set the result that your activity
							// will return to its caller
							setResult(
									RESULT_OK,
									new Intent()
											.putExtra(CallMenuActivity.VISIT,
													visit)
											.putExtra(
													CallMenuActivity.CALL,
													getIntent()
															.getSerializableExtra(
																	CALL)));
						} // End if
							// Close the activity
						finish();
					}
				});
	}

	/**
	 * Prompts the user to select a company.
	 */
	private void promptCompany() {
		// Retrieve the companies
		List<Companies> companies = DatabaseUtils.getInstance(this)
				.getDaoSession().getCompaniesDao().queryBuilder()
				.orderAsc(CompaniesDao.Properties.CompanyName).list();
		// Make sure there is at least one company to select from
		if (companies.isEmpty()) {
			// Display baguette message
			Baguette.showText(
					this,
					AppResources.getInstance(this).getString(this,
							R.string.collection_no_companies_warning),
					Baguette.BackgroundColor.RED);
			// Exit method
			return;
		} // End if
			// Retrieve the company names
		String[] companyNames = new String[companies.size()];
		// Iterate over the companies
		for (int i = 0; i < companies.size(); i++)
			// Add the company name to the array
			companyNames[i] = companies.get(i).getCompanyName();
		// Prompt the user to choose a company
		AppDialog.getInstance().displayList(
				this,
				AppResources.getInstance(this).getString(this,
						R.string.collection_company_dialog_title),
				companyNames, AppDialog.Cancelable.BACK_ONLY,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Retrieve the companies
						List<Companies> companies = DatabaseUtils
								.getInstance(CollectionDetailsActivity.this)
								.getDaoSession().getCompaniesDao()
								.queryBuilder()
								.orderAsc(CompaniesDao.Properties.CompanyName)
								.list();
						// Set the selected company
						company = companies.get(which);
						// Populate asynchronously
						populate();
					}
				}, new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						// Check if this is a collection only call
						if (getIntent().getBooleanExtra(COLLECTION_ONLY, false)) {
							// Retrieve the visit object
							Visits visit = (Visits) getIntent()
									.getSerializableExtra(VISIT);
							// Refresh the visit object
							DatabaseUtils
									.getInstance(CollectionDetailsActivity.this)
									.getDaoSession().getVisitsDao()
									.refresh(visit);
							// End the visit
							visit.setEndDate(Calendar.getInstance().getTime());
							// Update the visit in DB
							DatabaseUtils
									.getInstance(CollectionDetailsActivity.this)
									.getDaoSession().getVisitsDao()
									.update(visit);

							// Clear call action data
							CallAction
									.clearData(CollectionDetailsActivity.this);
							// Call this to set the result that your activity
							// will return to its caller
							setResult(
									RESULT_OK,
									new Intent()
											.putExtra(CallMenuActivity.VISIT,
													visit)
											.putExtra(
													CallMenuActivity.CALL,
													getIntent()
															.getSerializableExtra(
																	CALL)));
						} // End if
							// Close the activity
						finish();
					}
				});
	}

	/*
	 * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause(),
	 * for your activity to start interacting with the user.
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume() {
		// Indicate that the activity is displayed
		isDisplayed = true;
		// Superclass method call
		super.onResume();
		// Check if the printing confirmation should be displayed
		if (displayPrintingConfirmation)
			// Display printing confirmation
			displayPrintingConfirmation();
	}

	/*
	 * Initialize the contents of the Activity's standard options menu.
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Check if the collection header is valid
		// OR if a note is undergoing modifications
		// OR if a cash payment is undergoing modifications
		// OR if a check payment is undergoing modifications
		// OR if a paid amount is undergoing modifications
		if (collectionHeader == null || displayCollectionNote
				|| displayCollectionManualReference || displayPaymentCash
				|| displayPaymentCheck || displayPaidAmount)
			// Hide the menu
			return false;
		// Use the MenuInflater of this context to inflate a menu hierarchy from
		// the specified XML resource
		getMenuInflater().inflate(R.menu.action_bar, menu);
		// Enable the required menu items
		me.SyncWise.Android.Utilities.MenuItem.enable(menu
				.findItem(R.id.action_save));
		me.SyncWise.Android.Utilities.MenuItem.enable(menu
				.findItem(R.id.action_keyboard));
		me.SyncWise.Android.Utilities.MenuItem.enable(menu
				.findItem(R.id.action_info));
		// Display the menu
		return true;
	}

	/*
	 * This hook is called whenever an item in your options menu is selected.
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		// Check if the collection header is valid
		// OR if a note is undergoing modifications
		// OR if a cash payment is undergoing modifications
		// OR if a check payment is undergoing modifications
		// OR if a paid amount is undergoing modifications
		if (collectionHeader == null || displayCollectionNote
				|| displayCollectionManualReference || displayPaymentCash
				|| displayPaymentCheck || displayPaidAmount)
			// Consume event
			return true;
		// Determine if the action keyboard is selected
		else if (menuItem.getItemId() == R.id.action_keyboard) {
			// Set flag
			displayCollectionNote = true;
			// Initialize the secondary view
			initializeSecondaryView(false);
			// Disable the main list
			setMainLayoutEnabled(false);
			// Retrieve a reference to the secondary view
			View secondaryView = findViewById(R.id.layout_collection_note);
			// Display the tertiary view
			secondaryView.setVisibility(View.VISIBLE);
			// Animate the tertiary view
			secondaryView.startAnimation(getViewAnimationIn());
			// Refresh the action bar
			invalidateOptionsMenu();
			// Consume event
			return true;
		} // End else if
			// Determine if the action info is selected
		else if (menuItem.getItemId() == R.id.action_info) {
			// Create an intent to start a new activity
			Intent intent = new Intent(this, App.getClientInfoActicityClass());
			// Retrieve the current call
			Call call = (Call) getIntent().getSerializableExtra(CALL);
			// Add the client code to the intent
			intent.putExtra(ClientInfoActivity.CLIENT_CODE, call.getClient()
					.getClientCode());
			// Add the company code to the intent
			intent.putExtra(ClientInfoActivity.COMPANY_CODE, call.getClient()
					.getCompanyCode());
			// Start the new activity
			startActivity(intent);
			// Specify an explicit transition animation to perform next
			ActivityTransition.SlideOutLeft(this);
			// Consume event
			return true;
		} // End else if
			// Determine if the action save is selected
		else if (menuItem.getItemId() == R.id.action_save) {
			// Make sure the collection can be saved
			if (collectionDetails.isEmpty()) {
				// Display baguette message
				Baguette.showText(
						this,
						AppResources.getInstance(this).getString(this,
								R.string.collection_no_payments_done_warning),
						Baguette.BackgroundColor.RED);
				// Consume event
				return true;
			} // End if
				// Display alert dialog
			AppDialog.getInstance().displayAlert(
					this,
					null,
					AppResources.getInstance(this).getString(this,
							R.string.save_confirmation_message),
					AppDialog.ButtonsType.YesNo,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Determine the clicked button
							switch (which) {
							case DialogInterface.BUTTON_POSITIVE:
								// Save the collection
								saveCollection();
								break;
							case DialogInterface.BUTTON_NEGATIVE:
								// Dismiss dialog
								AppDialog.getInstance().dismiss();
								break;
							} // End switch
						}
					});

			// Consume event
			return true;
		} // End else if
			// Allow normal menu processing to proceed
		return false;
	}

	/*
	 * Called as part of the activity lifecycle when an activity is going into
	 * the background, but has not (yet) been killed.
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// Indicate that the activity is NOT displayed
		isDisplayed = false;
		// Superclass method call
		super.onPause();
		// Remove any displayed baguette
		Baguette.remove(this);
		// Dismiss any displayed dialogs (to avoid activity context leak)
		AppDialog.getInstance().dismiss();
	}

	/*
	 * Called to retrieve per-instance state from an activity before being
	 * killed so that the state can be restored in onCreate(Bundle) or
	 * onRestoreInstanceState(Bundle).
	 * 
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	protected void onSaveInstanceState(Bundle outState) {
		// Superclass method call
		super.onSaveInstanceState(outState);
		// Save the content of isCreated in the outState bundle
		outState.putBoolean(IS_CREATED, isCreated);

		// Save the content of selectedCurrencyIndex in the outState bundle
		outState.putInt(SELECTED_CURRENCY_INDEX, selectedCurrencyIndex);
		// Check if the collectionMode is valid
		if (collectionMode != null)
			// Save the content of collectionMode in the outState bundle
			outState.putInt(COLLECTION_MODE, collectionMode);
		// Check if the selectedPaymentIndex is valid
		if (selectedPaymentIndex != null)
			// Save the content of selectedPaymentIndex in the outState bundle
			outState.putInt(SELECTED_PAYMENT_INDEX, selectedPaymentIndex);
		outState.putBoolean ( SAVE_MOVEMENTS , saveMovements );
		// Check if the payment cash UI is displayed
		if (displayPaymentCash)
			// Save the content of selectedCurrencyPaymentIndex in the outState
			// bundle
			outState.putInt(SELECTED_CURRENCY_PAYMENT_INDEX,
					((Spinner) findViewById(R.id.spinner_cash_currencies))
							.getSelectedItemPosition());
		// Otherwise check if the payment check UI is displayed
		else if (displayPaymentCheck)
			// Save the content of selectedCurrencyPaymentIndex in the outState
			// bundle
			outState.putInt(SELECTED_CURRENCY_PAYMENT_INDEX,
					((Spinner) findViewById(R.id.spinner_check_currencies))
							.getSelectedItemPosition());
		// Save the content of selectedBankPaymentIndex in the outState bundle
		outState.putInt(SELECTED_BANK_PAYMENT_INDEX,
				((Spinner) findViewById(R.id.spinner_check_banks))
						.getSelectedItemPosition());
		// Check if the list is valid
		if (totalClientDues != null)
			// Save the content of totalClientDues in the outState bundle
			outState.putSerializable(TOTAL_CLIENT_DUES, totalClientDues);
		// Check if the list is valid
		if (clientDues != null)
			// Save the content of clientDues in the outState bundle
			outState.putSerializable(CLIENT_DUES, clientDues);
		// Check if the list is valid
		if (invoices != null)
			// Save the content of invoices in the outState bundle
			outState.putSerializable(INVOICES, invoices);
		// Save the content of collectionHeader in the outState bundle
		outState.putSerializable(COLLECTION_HEADER, collectionHeader);
		// Save the content of collectionDetails in the outState bundle
		outState.putSerializable(COLLECTION_DETAILS, collectionDetails);
		// Save the content of displayCollectionNote in the outState bundle
		outState.putBoolean(DISPLAY_COLLECTION_NOTE, displayCollectionNote);
		// Save the content of displayCollectionManualReference in the outState
		// bundle
		outState.putBoolean(DISPLAY_COLLECTION_MANUAL_REFERENCE,
				displayCollectionManualReference);
		// Save the content of displayPaymentCash in the outState bundle
		outState.putBoolean(DISPLAY_PAYMENT_CASH, displayPaymentCash);
		// Save the content of displayPaymentCheck in the outState bundle
		outState.putBoolean(DISPLAY_PAYMENT_CHECK, displayPaymentCheck);
		// Save the content of displayPaidAmount in the outState bundle
		outState.putBoolean(DISPLAY_PAID_AMOUNT, displayPaidAmount);
		// Check if the company is valid
		if (company != null)
			// Save the content of company in the outState bundle
			outState.putSerializable(COMPANY, company);
		// Save the content of displayPrintingConfirmation in the outState
		// bundle
		outState.putBoolean(DISPLAY_PRINTING_CONFIRMATION,
				displayPrintingConfirmation);
	}

	/*
	 * Called when the activity has detected the user's press of the back key.
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// Determine if the collection note is undergoing any modifications
		if (displayCollectionNote) {
			// Reset flag
			displayCollectionNote = false;
			// Retrieve a reference to the secondary view
			View secondary = findViewById(R.id.layout_collection_note);
			// Hide the secondary view
			secondary.startAnimation(getViewAnimationOut(secondary));
			// Enable the main list
			setMainLayoutEnabled(true);
			// Refresh the action bar
			invalidateOptionsMenu();
			// Exit method
			return;
		} // End else if
			// Determine if the collection manual reference is undergoing any
			// modifications
		else if (displayCollectionManualReference) {
			// Reset flag
			displayCollectionManualReference = false;
			// Retrieve a reference to the secondary view
			View secondary = findViewById(R.id.layout_collection_manual_reference);
			// Hide the secondary view
			secondary.startAnimation(getViewAnimationOut(secondary));
			// Enable the main list
			setMainLayoutEnabled(true);
			// Refresh the action bar
			invalidateOptionsMenu();
			// Exit method
			return;
		} // End else if
			// Determine if the payment cash is undergoing any modifications
		else if (displayPaymentCash) {
			// Reset flag
			displayPaymentCash = false;
			// Retrieve a reference to the tertiary view
			View tertiary = findViewById(R.id.layout_payment_cash);
			// Hide the tertiary view
			tertiary.startAnimation(getViewAnimationOut(tertiary));
			// Enable the main list
			setMainLayoutEnabled(true);
			// Refresh the action bar
			invalidateOptionsMenu();
			// Exit method
			return;
		} // End else if
			// Determine if the payment check is undergoing any modifications
		else if (displayPaymentCheck) {
			// Reset flag
			displayPaymentCheck = false;
			// Retrieve a reference to the quaternary view
			View quaternary = findViewById(R.id.layout_payment_check);
			// Hide the quaternary view
			quaternary.startAnimation(getViewAnimationOut(quaternary));
			// Enable the main list
			setMainLayoutEnabled(true);
			// Refresh the action bar
			invalidateOptionsMenu();
			// Exit method
			return;
		} // End else if
			// Determine if the paid amount is undergoing any modifications
		else if (displayPaidAmount) {
			// Reset flag
			displayPaidAmount = false;
			// Retrieve a reference to the quinary view
			View quinary = findViewById(R.id.layout_paid_amount);
			// Hide the quinary view
			quinary.startAnimation(getViewAnimationOut(quinary));
			// Enable the main list
			setMainLayoutEnabled(true);
			// Refresh the action bar
			invalidateOptionsMenu();
			// Exit method
			return;
		} // End else if
			// Otherwise check if there is at least one modification
		else if (hasModifications())
			// Display exit confirmation
			AppDialog.getInstance().displayAlert(
					this,
					null,
					AppResources.getInstance(this).getString(this,
							R.string.discard_changes_confirmation_message),
					AppDialog.ButtonsType.YesNo,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Determine the clicked button
							switch (which) {
							case DialogInterface.BUTTON_POSITIVE:
								// Check if this is a collection only call
								if (getIntent().getBooleanExtra(
										COLLECTION_ONLY, false)) {
									// Retrieve the visit object
									Visits visit = (Visits) getIntent()
											.getSerializableExtra(VISIT);
									// Refresh the visit object
									DatabaseUtils
											.getInstance(
													CollectionDetailsActivity.this)
											.getDaoSession().getVisitsDao()
											.refresh(visit);
									// End the visit
									visit.setEndDate(Calendar.getInstance()
											.getTime());
									// Update the visit in DB
									DatabaseUtils
											.getInstance(
													CollectionDetailsActivity.this)
											.getDaoSession().getVisitsDao()
											.update(visit);

									// Clear call action data
									CallAction
											.clearData(CollectionDetailsActivity.this);
									// Call this to set the result that your
									// activity will return to its caller
									setResult(
											RESULT_OK,
											new Intent()
													.putExtra(
															CallMenuActivity.VISIT,
															visit)
													.putExtra(
															CallMenuActivity.CALL,
															getIntent()
																	.getSerializableExtra(
																			CALL)));
								} // End if
									// Finish this activity
								CollectionDetailsActivity.this.finish();
								break;
							case DialogInterface.BUTTON_NEGATIVE:
								// Dismiss dialog
								AppDialog.getInstance().dismiss();
								break;
							} // End switch
						}
					});
		else {
			// There is no modifications
			// Check if this is a collection only call
			if (getIntent().getBooleanExtra(COLLECTION_ONLY, false)) {
				// Retrieve the visit object
				Visits visit = (Visits) getIntent().getSerializableExtra(VISIT);
				// Refresh the visit object
				DatabaseUtils.getInstance(this).getDaoSession().getVisitsDao()
						.refresh(visit);
				// End the visit
				visit.setEndDate(Calendar.getInstance().getTime());
				// Update the visit in DB
				DatabaseUtils.getInstance(this).getDaoSession().getVisitsDao()
						.update(visit);

				// Clear call action data
				CallAction.clearData(this);
				// Call this to set the result that your activity will return to
				// its caller
				setResult(
						RESULT_OK,
						new Intent().putExtra(CallMenuActivity.VISIT, visit)
								.putExtra(CallMenuActivity.CALL,
										getIntent().getSerializableExtra(CALL)));
			} // End if
				// Superclass method call
			super.onBackPressed();
		} // End else
	}

	/*
	 * Called when you are no longer visible to the user.
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	public void onStop() {
		// Superclass method call
		super.onStop();
		// Determine if the activity is finishing
		if (isFinishing()) {
			collectionMode = null;
			isCreated = false;
			totalClientDues = null;
			clientDues = null;
			invoices = null;
			currencies = null;
			banks = null;
			collectionHeader = null;
			collectionDetails = null;
			company = null;
		} // End if
	}

	/**
	 * Displays a confirmation dialog used to prompt the user to print the
	 * current transaction.
	 */
	private void displayPrintingConfirmation() {
		// Prompt the user to print a copy
		AppDialog.getInstance().displayAlert(
				this,
				null,
				AppResources.getInstance(this).getString(this,
						R.string.printing_activity_print_request_message),
				AppDialog.ButtonsType.YesNo,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Determine the clicked button
						switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							// Print the transaction
							Intent intent = new Intent(
									CollectionDetailsActivity.this,
									PrintingActivity.class);
							intent.putExtra(PrintingActivity.TYPE,
									PrintingActivity.Type.RECEIPT);
							intent.putExtra(PrintingActivity.PRINTOUT,
									PrintingActivity.Printout.ORIGINAL);
							ArrayList<CollectionHeaders> list = new ArrayList<CollectionHeaders>();
							list.add(collectionHeader);
							intent.putExtra(PrintingActivity.HEADER, list);
							startActivityForResult(intent, REQUEST_CODE_PRINT);
							// Reset flag
							displayPrintingConfirmation = false;
							break;
						case DialogInterface.BUTTON_NEGATIVE:
							// Retrieve the visit
							Visits visit = (Visits) getIntent()
									.getSerializableExtra(VISIT);
							// Call this to set the result that your activity
							// will return to its caller
							intent = new Intent();
							intent.putExtra(
									CallMenuActivity.ACTION_RESULT_SUCCESS,
									true);
							// Check if this is a collection only call
							if (getIntent().getBooleanExtra(COLLECTION_ONLY,
									false)) {
								// Refresh the visit object
								DatabaseUtils
										.getInstance(
												CollectionDetailsActivity.this)
										.getDaoSession().getVisitsDao()
										.refresh(visit);
								// End the visit
								visit.setEndDate(Calendar.getInstance()
										.getTime());
								// Update the visit in DB
								DatabaseUtils
										.getInstance(
												CollectionDetailsActivity.this)
										.getDaoSession().getVisitsDao()
										.update(visit);

								// Clear call action data
								CallAction
										.clearData(CollectionDetailsActivity.this);
								// Call this to set the result that your
								// activity will return to its caller
								intent.putExtra(CallMenuActivity.VISIT, visit)
										.putExtra(
												CallMenuActivity.CALL,
												getIntent()
														.getSerializableExtra(
																CALL));
							} // End if
							setResult(RESULT_OK, intent);
							// Finish activity
							finish();
							// Dismiss dialog
							AppDialog.getInstance().dismiss();
							// Reset flag
							displayPrintingConfirmation = false;
							break;
						} // End switch
					}
				});
	}

	/*
	 * Called when an activity you launched exits, giving you the requestCode
	 * you started it with, the resultCode it returned, and any additional data
	 * from it.
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check if the transaction has been printed in a non view mode
		if (requestCode == REQUEST_CODE_PRINT) {
			// Retrieve the visit
			Visits visit = (Visits) getIntent().getSerializableExtra(VISIT);
			// Call this to set the result that your activity will return to its
			// caller
			Intent intent = new Intent();
			intent.putExtra(CallMenuActivity.ACTION_RESULT_SUCCESS, true);
			// Check if this is a collection only call
			if (getIntent().getBooleanExtra(COLLECTION_ONLY, false)) {
				// Refresh the visit object
				DatabaseUtils.getInstance(CollectionDetailsActivity.this)
						.getDaoSession().getVisitsDao().refresh(visit);
				// End the visit
				visit.setEndDate(Calendar.getInstance().getTime());
				// Update the visit in DB
				DatabaseUtils.getInstance(CollectionDetailsActivity.this)
						.getDaoSession().getVisitsDao().update(visit);

				// Clear call action data
				CallAction.clearData(CollectionDetailsActivity.this);
				// Call this to set the result that your activity will return to
				// its caller
				intent.putExtra(CallMenuActivity.VISIT, visit).putExtra(
						CallMenuActivity.CALL,
						getIntent().getSerializableExtra(CALL));
			} // End if
			setResult(RESULT_OK, intent);
			// Finish activity
			finish();
		} // End if

		// Check if the result is successful and the intent is valid
		if (resultCode != RESULT_OK || data == null)
			// Exit method
			return;
	}

	/**
	 * Indicates whether the collection has new / unsaved modifications.
	 * 
	 * @return Boolean stating if there are new modifications.
	 */
	private boolean hasModifications() {
		// Check if the collection details list is empty
		return !collectionDetails.isEmpty();
	}

	/**
	 * Enables / disables the main layout of the collection module.
	 * 
	 * @param enabled
	 *            Flag used to enable or disable the main layout.
	 */
	private void setMainLayoutEnabled(final boolean enabled) {
		// Enable / disable the main layout accordingly
		((CustomLinearLayout) findViewById(R.id.layout_main))
				.setLinearLayoutEnabled(enabled);
	}

	/**
	 * Builds and returns a slide in from top animation.<br>
	 * The slide animation duration is set by
	 * {@code R.integer.default_activity_transition_duration}.
	 * 
	 * @return An {@link android.view.animation.Animation Animation} used to
	 *         perform a slide in from top animation.
	 */
	private Animation getViewAnimationIn() {
		// Declare and initialize the slide in animation
		Animation in = new TranslateAnimation(Animation.ABSOLUTE, 0,
				Animation.ABSOLUTE, 0, Animation.RELATIVE_TO_SELF, -1,
				Animation.RELATIVE_TO_SELF, 0);
		// Set the animation duration
		in.setDuration(getResources().getInteger(
				R.integer.default_activity_transition_duration));
		// Return the animation
		return in;
	}

	/**
	 * Builds and returns a slide out to bottom animation.<br>
	 * The slide animation duration is set by
	 * {@code R.integer.default_activity_transition_duration}.
	 * 
	 * @param view
	 *            Reference to the view to hide after the animation ends.
	 * @return An {@link android.view.animation.Animation Animation} used to
	 *         perform a slide out to bottom animation.
	 */
	private Animation getViewAnimationOut(final View view) {
		// Declare and initialize the slide in animation
		Animation out = new TranslateAnimation(Animation.ABSOLUTE, 0,
				Animation.ABSOLUTE, 0, Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, -1);
		// Set the animation duration
		out.setDuration(getResources().getInteger(
				R.integer.default_activity_transition_duration));
		// Set an animation listener mainly used to remove the view after it is
		// slid out
		out.setAnimationListener(new AnimationListener() {
			/*
			 * Notifies the start of the animation.
			 * 
			 * @see
			 * android.view.animation.Animation.AnimationListener#onAnimationStart
			 * (android.view.animation.Animation)
			 */
			@Override
			public void onAnimationStart(Animation animation) {
				// Do nothing
			}

			/*
			 * Notifies the repetition of the animation.
			 * 
			 * @see
			 * android.view.animation.Animation.AnimationListener#onAnimationRepeat
			 * (android.view.animation.Animation)
			 */
			@Override
			public void onAnimationRepeat(Animation animation) {
				// Do nothing
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// Hide the view
				view.setVisibility(View.GONE);
			}
		});
		// Return the animation
		return out;
	}

	/*
	 * Callback method to be invoked when an item in this view has been
	 * selected.
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android
	 * .widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// Update the selected currency
		selectedCurrencyIndex = ((Spinner) findViewById(R.id.spinner_currencies))
				.getSelectedItemPosition();
	}

	/*
	 * Callback method to be invoked when the selection disappears from this
	 * view.
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android
	 * .widget.AdapterView)
	 */
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// Do nothing
	}

	/**
	 * Called when a view has been clicked.<br>
	 * The check date is prompted.
	 * 
	 * @param view
	 *            The clicked view.
	 */
	public void setCheckDate(View view) {
		// Retrieve current date
		Calendar defaultDate = Calendar.getInstance();
		// Check if the current check date is valid
		if (collectionHeader.getStampDate() != null)
			// Set the check date
			defaultDate.setTime(collectionHeader.getStampDate());
		// Display date picker
		AppDialog.getInstance().displayDatePicker(
				CollectionDetailsActivity.this, defaultDate.get(Calendar.YEAR),
				defaultDate.get(Calendar.MONTH),
				defaultDate.get(Calendar.DAY_OF_MONTH), null, false, true,
				this, null);
	}

	/*
	 * Callback method to be invoked when a date is selected form the date
	 * picker.
	 * 
	 * @see
	 * me.SyncWise.Android.Modules.Collections.CollectionDetailsActivity.onDateSet
	 * (DatePicker view, int year, int monthOfYear, int dayOfMonth)
	 */
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// Declare and initialize a calendar
		Calendar calendar = Calendar.getInstance();
		// Update the check date
		calendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		// Set the check date
		collectionHeader.setStampDate(calendar.getTime());
		// Display the check date
		((TextView) findViewById(R.id.label_date_picker)).setText(AppResources
				.getInstance(this).getString(this, R.string.check_date_label)
				+ " : "
				+ DateTime.getBriefDate(this, collectionHeader.getStampDate()));
	}

	/*
	 * Callback method to be invoked when an item in this AdapterView has been
	 * clicked.
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// Retrieve the collection detail
		CollectionDetails collectionDetail = ((ViewHolder) view.getTag()).collectionDetail;
		// Declare and initialize the pop up view
		View popUpView = null;
		// Determine if the collection detail is cash or check
		if (collectionDetail.getCollectionDetailType() != null
				&& collectionDetail.getCollectionDetailType() == CollectionUtils.PaymentType.CASH) {
			// Set flag
			displayPaymentCash = true;
			// Initialize the tertiary view
			initializeTertiaryView(collectionDetail, false);
			// Set the pop up view
			popUpView = findViewById(R.id.layout_payment_cash);
		} // End if
			// Determine if the collection detail is cash or check
		else if (collectionDetail.getCollectionDetailType() != null
				&& collectionDetail.getCollectionDetailType() == CollectionUtils.PaymentType.CHECK) {
			// Set flag
			displayPaymentCheck = true;
			// Initialize the quaternary view
			initializeQuaternaryView(collectionDetail, false);
			// Set the pop up view
			popUpView = findViewById(R.id.layout_payment_check);
		} // End if
		else
			// Invalid type
			return;

		// Store the selected payment index
		selectedPaymentIndex = parent.getPositionForView(view);
		// Disable the main list
		setMainLayoutEnabled(false);
		// Display the pop up view
		popUpView.setVisibility(View.VISIBLE);
		// Animate the pop up view
		popUpView.startAnimation(getViewAnimationIn());
		// Refresh the action bar
		invalidateOptionsMenu();
	}

	/**
	 * Called when a view has been clicked.<br>
	 * This handler is used to start the payment cash add process.
	 * 
	 * @param view
	 *            The clicked view.
	 */
	public void addCash(View view) {
		// Set flag
		displayPaymentCash = true;
		// Initialize the tertiary view
		initializeTertiaryView(null, false);
		// Disable the main list
		setMainLayoutEnabled(false);
		// Retrieve a reference to the tertiary view
		View tertiaryView = findViewById(R.id.layout_payment_cash);
		// Display the tertiary view
		tertiaryView.setVisibility(View.VISIBLE);
		// Animate the tertiary view
		tertiaryView.startAnimation(getViewAnimationIn());
		// Refresh the action bar
		invalidateOptionsMenu();
	}

	/**
	 * Called when a view has been clicked.<br>
	 * This handler is used to start the payment check add process.
	 * 
	 * @param view
	 *            The clicked view.
	 */
	public void addCheck(View view) {
		// Set flag
		displayPaymentCheck = true;
		// Clear stamp date used as check date
		collectionHeader.setStampDate(null);
		// Initialize the quaternary view
		initializeQuaternaryView(null, false);
		// Disable the main list
		setMainLayoutEnabled(false);
		// Retrieve a reference to the tertiary view
		View quaternaryView = findViewById(R.id.layout_payment_check);
		// Display the quaternary view
		quaternaryView.setVisibility(View.VISIBLE);
		// Animate the quaternary view
		quaternaryView.startAnimation(getViewAnimationIn());
		// Refresh the action bar
		invalidateOptionsMenu();
	}

	/**
	 * Handler used to perform a modification on the indicated paid amount.
	 * 
	 * @param paidAmountIndex
	 *            Integer holding the index of the paid amount object in the
	 *            appropriate list.
	 */
	public void editPaidAmount(int paidAmountIndex) {
		// Set flag
		displayPaidAmount = true;
		// Store the selected payment index
		selectedPaymentIndex = paidAmountIndex;
		// Initialize the quinary view
		initializeQuinaryView(false);
		// Disable the main list
		setMainLayoutEnabled(false);
		// Retrieve a reference to the tertiary view
		View quinaryView = findViewById(R.id.layout_paid_amount);
		// Display the quinary view
		quinaryView.setVisibility(View.VISIBLE);
		// Animate the quinary view
		quinaryView.startAnimation(getViewAnimationIn());
		// Refresh the action bar
		invalidateOptionsMenu();
	}

	/**
	 * Initializes the collection note (secondary) view.
	 * 
	 * @param restore
	 *            Boolean used to determine if the secondary view is being
	 *            initialized for the first time or is being restored.
	 */
	protected void initializeSecondaryView(final boolean restore) {
		// Retrieve a reference to the parent view
		View parent = findViewById(R.id.layout_collection_note);
		// Retrieve a reference to the collection note edit text
		EditText noteEditText = (EditText) parent
				.findViewById(R.id.edittext_collection_note);
		// Retrieve a reference to the collection note title label
		TextView titleLabel = (TextView) parent
				.findViewById(R.id.label_collection_note);

		// Display the collection note title label
		titleLabel.setText(AppResources.getInstance(this).getString(this,
				R.string.collection_note_title));
		// Enable the save icon
		parent.findViewById(R.id.icon_save_note).setEnabled(true);
		// Enable edit text
		noteEditText.setEnabled(true);
		// Display the field hint
		noteEditText.setHint(AppResources.getInstance(this).getString(this,
				R.string.collection_note_hint));
		// Set the maximum number of allowed characters
		noteEditText
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
						Collection.getFreeRemarksMaxLength()) });
		// Check if the view is being restored
		if (!restore)
			// Set the collection note
			noteEditText.setText(collectionHeader.getNotes());
	}

	/**
	 * Initializes the collection manual reference (secondary prime) view.
	 * 
	 * @param restore
	 *            Boolean used to determine if the secondary prime view is being
	 *            initialized for the first time or is being restored.
	 */
	protected void initializeSecondaryPrimeView(final boolean restore) {
		// Retrieve a reference to the parent view
		View parent = findViewById(R.id.layout_collection_manual_reference);
		// Retrieve a reference to the collection manual reference edit text
		EditText noteEditText = (EditText) parent
				.findViewById(R.id.edittext_collection_manual_reference);
		// Retrieve a reference to the collection manual reference title label
		TextView titleLabel = (TextView) parent
				.findViewById(R.id.label_collection_manual_reference);

		// Display the collection manual reference title label
		titleLabel.setText(AppResources.getInstance(this).getString(this,
				R.string.collection_manual_reference_title));
		// Enable the save icon
		parent.findViewById(R.id.icon_save_manual_reference).setEnabled(true);
		// Enable edit text
		noteEditText.setEnabled(true);
		// Display the field hint
		noteEditText.setHint(AppResources.getInstance(this).getString(this,
				R.string.collection_manual_reference_hint));
		// Set the maximum number of allowed characters
		noteEditText
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
						Collection.getFreeRemarksMaxLength()) });
		// Check if the view is being restored
		if (!restore)
			// Set the collection manual reference
			noteEditText.setText(collectionHeader.getManualReference());
	}

	/**
	 * Initializes the payment cash (tertiary) view.
	 * 
	 * @param collectionDetail
	 *            A {@link me.SyncWise.Android.Database.CollectionDetails
	 *            CollectionDetails} object used to host the selected payment
	 *            (if any).
	 * @param restore
	 *            Boolean used to determine if the tertiary view is being
	 *            initialized for the first time or is being restored.
	 */
	protected void initializeTertiaryView(
			final CollectionDetails collectionDetail, final boolean restore) {
		// Retrieve a reference to the parent view
		View parent = findViewById(R.id.layout_payment_cash);
		// Retrieve a reference to the payment edit text
		EditText paymentEditText = (EditText) parent
				.findViewById(R.id.edittext_payment_cash);
		// Retrieve a reference to the payment cash title label
		TextView titleLabel = (TextView) parent
				.findViewById(R.id.label_payment_cash);

		// Display the payment cash title label
		titleLabel.setText(AppResources.getInstance(this).getString(this,
				R.string.payment_cash_label));
		// Enable the save icon
		parent.findViewById(R.id.icon_save_payment_cash).setEnabled(true);
		// Enable edit text
		paymentEditText.setEnabled(true);
		// Display the field hint
		paymentEditText.setHint(AppResources.getInstance(this).getString(this,
				R.string.collection_payment_hint));
		// Enable currencies spinner
		findViewById(R.id.spinner_cash_currencies).setEnabled(true);
		// Check if a collection detail is provided
		if (collectionDetail != null) {
			// Set the amount
			paymentEditText.setText(String.valueOf(collectionDetail
					.getCollectionAmount()));
			// Iterate over all currencies
			for (int i = 0; i < currencies.size(); i++)
				// Match currencies
				if (currencies.get(i).getCurrencyCode()
						.equals(collectionDetail.getCurrencyCode()))
					// Set the appropriate currency
					((Spinner) findViewById(R.id.spinner_cash_currencies))
							.setSelection(i);
		} // End if
			// Otherwise check if the view is being restored
		else if (restore)
			// Set the appropriate currency
			((Spinner) findViewById(R.id.spinner_cash_currencies))
					.setSelection(selectedCurrencyPaymentIndex);
		// Otherwise check if there is a remaining value
		else {
			// Compute the remaining value
			double remainingValue = computeRemainingValue();
			// Check if the remaining value is valid
			if (remainingValue > 0)
				// Display the remaining value
				paymentEditText.setText(String.valueOf(remainingValue));
		} // End if
	}

	/**
	 * Initializes the payment check (quaternary) view.
	 * 
	 * @param collectionDetail
	 *            A {@link me.SyncWise.Android.Database.CollectionDetails
	 *            CollectionDetails} object used to host the selected payment
	 *            (if any).
	 * @param restore
	 *            Boolean used to determine if the quaternary view is being
	 *            initialized for the first time or is being restored.
	 */
	@SuppressLint("DefaultLocale")
	protected void initializeQuaternaryView(
			final CollectionDetails collectionDetail, final boolean restore) {
		// Retrieve a reference to the parent view
		View parent = findViewById(R.id.layout_payment_check);
		// Retrieve a reference to the payment edit text
		EditText paymentEditText = (EditText) parent
				.findViewById(R.id.edittext_payment_check);
		// Retrieve a reference to the check code edit text
		EditText checkCodeEditText = (EditText) parent
				.findViewById(R.id.edittext_check_code);
		// Retrieve a reference to the payment check title label
		TextView titleLabel = (TextView) parent
				.findViewById(R.id.label_payment_check);

		// Display the payment check title label
		titleLabel.setText(AppResources.getInstance(this)
				.getString(this, R.string.payment_check_label).toUpperCase());
		// Enable the save icon
		parent.findViewById(R.id.icon_save_payment_check).setEnabled(true);
		// Enable edit text
		paymentEditText.setEnabled(true);
		checkCodeEditText.setEnabled(true);
		// Display the field hint
		paymentEditText.setHint(AppResources.getInstance(this).getString(this,
				R.string.collection_payment_hint));
		checkCodeEditText.setHint(AppResources.getInstance(this).getString(
				this, R.string.collection_check_code_hint));
		// Set the maximum number of allowed characters
		checkCodeEditText
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
						Collection.getCheckCodeMaxLength()) });
		// Enabled date picker
		findViewById(R.id.button_date_picker).setEnabled(true);
		// Enable currencies spinner
		findViewById(R.id.spinner_check_currencies).setEnabled(true);
		// Enable banks spinner
		findViewById(R.id.spinner_check_banks).setEnabled(true);
		// Check if a collection detail is provided
		if (collectionDetail != null) {
			// Set the amount
			paymentEditText.setText(String.valueOf(collectionDetail
					.getCollectionAmount()));
			// Set the check code
			checkCodeEditText.setText(collectionDetail.getCheckCode());
			// Iterate over all currencies
			for (int i = 0; i < currencies.size(); i++)
				// Match currencies
				if (currencies.get(i).getCurrencyCode()
						.equals(collectionDetail.getCurrencyCode()))
					// Set the appropriate currency
					((Spinner) findViewById(R.id.spinner_check_currencies))
							.setSelection(i);
			// Iterate over all banks
			for (int i = 0; i < currencies.size(); i++)
				// Match banks
				if (banks.get(i).getBankCode()
						.equals(collectionDetail.getBankCode()))
					// Set the appropriate currency
					((Spinner) findViewById(R.id.spinner_check_banks))
							.setSelection(i);
			// Display the check date
			((TextView) findViewById(R.id.label_date_picker))
					.setText(AppResources.getInstance(this).getString(this,
							R.string.check_date_label)
							+ " : "
							+ DateTime.getBriefDate(this,
									collectionDetail.getCheckDate()));
			// Save a temporary value of the check date
			collectionHeader.setStampDate(new Date(collectionDetail
					.getCheckDate().getTime()));
		} // End if
			// Otherwise check if the view is being restored
		else if (restore) {
			// Display the check date
			((TextView) findViewById(R.id.label_date_picker))
					.setText(AppResources.getInstance(this).getString(this,
							R.string.check_date_label)
							+ " : "
							+ (collectionHeader.getStampDate() != null ? DateTime
									.getBriefDate(this,
											collectionHeader.getStampDate())
									: AppResources
											.getInstance(this)
											.getString(
													this,
													R.string.not_available_abbreviation)));
			// Set the appropriate currency
			((Spinner) findViewById(R.id.spinner_check_currencies))
					.setSelection(selectedCurrencyPaymentIndex);
			// Set the appropriate bank
			((Spinner) findViewById(R.id.spinner_check_banks))
					.setSelection(selectedBankPaymentIndex);
		} // End else if
			// Otherwise this is a first time initialization
		else {
			// There is no check date
			((TextView) findViewById(R.id.label_date_picker))
					.setText(AppResources.getInstance(this).getString(this,
							R.string.check_date_label)
							+ " : "
							+ AppResources.getInstance(this).getString(this,
									R.string.not_available_abbreviation));
			// Compute the remaining value
			double remainingValue = computeRemainingValue();
			// Check if the remaining value is valid
			if (remainingValue > 0)
				// Display the remaining value
				paymentEditText.setText(String.valueOf(remainingValue));
		} // End if
	}

	/**
	 * Initializes the paid amount (quinary) view.
	 * 
	 * @param restore
	 *            Boolean used to determine if the quinary view is being
	 *            initialized for the first time or is being restored.
	 */
	protected void initializeQuinaryView(final boolean restore) {
		// Retrieve a reference to the parent view
		View parent = findViewById(R.id.layout_paid_amount);
		// Retrieve a reference to the paid amount edit text
		EditText amountEditText = (EditText) parent
				.findViewById(R.id.edittext_paid_amount);
		// Retrieve a reference to the paid amount title label
		TextView titleLabel = (TextView) parent
				.findViewById(R.id.label_paid_amount);

		// Display the payment cash title label
		titleLabel.setText(AppResources.getInstance(this).getString(this,
				R.string.paid_amount_label));
		// Enable the save icon
		parent.findViewById(R.id.icon_save_paid_amount).setEnabled(true);
		// Enable edit text
		amountEditText.setEnabled(true);
		// Display the field hint
		amountEditText.setHint(AppResources.getInstance(this).getString(this,
				R.string.collection_payment_hint));
		// Check if the view is being built for the first time
		if (!restore) {
			// Declare and initialize the amount variable
			double amount = 0.0;
			// Determine the collection mode
			switch (collectionMode) {
			case PAYMENT_CLIENT_DUES:
				amount = clientDues.get(selectedPaymentIndex).getPaidAmount();
				break;
			case PAYMENT_INVOICES:
				amount = invoices.get(selectedPaymentIndex).getPaidAmount();
				break;
			} // End switch
				// Set the amount
			amountEditText.setText(String.valueOf(amount));
		} // End if
	}

	/**
	 * Called when a view has been clicked.<br>
	 * The collection note is saved.
	 * 
	 * @param view
	 *            The clicked view.
	 */
	public void updateNote(View view) {
		// Determine if the collection note is undergoing any modifications
		if (!displayCollectionNote)
			// No modifications
			return;

		// Reset flag
		displayCollectionNote = false;

		// Retrieve a reference to the secondary view
		View secondary = findViewById(R.id.layout_collection_note);
		// Retrieve a reference to the collection note edit text
		EditText noteEditText = (EditText) secondary
				.findViewById(R.id.edittext_collection_note);
		// Store the collection note
		collectionHeader.setNotes(noteEditText.getText().toString().trim());
		// Disable the edit text
		noteEditText.setEnabled(false);

		// Disable the save icon
		secondary.findViewById(R.id.icon_save_note).setEnabled(false);
		// Hide the software keyboard
		((InputMethodManager) getBaseContext().getSystemService(
				INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
				secondary.getWindowToken(), 0);

		// Hide the secondary view
		secondary.startAnimation(getViewAnimationOut(secondary));
		// Enable the main list
		setMainLayoutEnabled(true);
		// Indicate that the save was successful
		Vibration.vibrate(this);
		// Refresh the action bar
		invalidateOptionsMenu();
	}

	/**
	 * Called when a view has been clicked.<br>
	 * The collection manual reference is saved.
	 * 
	 * @param view
	 *            The clicked view.
	 */
	public void updateManualReference(View view) {
		// Determine if the collection manual reference is undergoing any
		// modifications
		if (!displayCollectionManualReference)
			// No modifications
			return;

		// Retrieve a reference to the secondary view
		View secondary = findViewById(R.id.layout_collection_manual_reference);
		// Retrieve a reference to the collection manual reference edit text
		EditText manualReferenceEditText = (EditText) secondary
				.findViewById(R.id.edittext_collection_manual_reference);

		// Check if the manual reference is valid
		if (manualReferenceEditText.getText().toString().trim().isEmpty()) {
			// Display baguette message
			Baguette.showText(
					this,
					AppResources
							.getInstance(this)
							.getString(
									this,
									R.string.collection_invalid_manual_reference_warning),
					Baguette.BackgroundColor.RED);
			// Exit method
			return;
		} // End if

		// Reset flag
		displayCollectionManualReference = false;

		// Store the collection manual reference
		collectionHeader.setManualReference(manualReferenceEditText.getText()
				.toString().trim());
		// Disable the edit text
		manualReferenceEditText.setEnabled(false);

		// Disable the save icon
		secondary.findViewById(R.id.icon_save_manual_reference).setEnabled(
				false);
		// Hide the software keyboard
		((InputMethodManager) getBaseContext().getSystemService(
				INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
				secondary.getWindowToken(), 0);

		// Hide the secondary view
		secondary.startAnimation(getViewAnimationOut(secondary));
		// Enable the main list
		setMainLayoutEnabled(true);
		// Indicate that the save was successful
		Vibration.vibrate(this);
		// Refresh the action bar
		invalidateOptionsMenu();
		// Save the collection
		saveCollection();
	}

	/**
	 * Called when a view has been clicked.<br>
	 * The payment cash is saved.
	 * 
	 * @param view
	 *            The clicked view.
	 */
	public void updatePaymentCash(View view) {
		// Determine if the payment cash is undergoing any modifications
		if (!displayPaymentCash)
			// No modifications
			return;

		// Retrieve a reference to the tertiary view
		View tertiary = findViewById(R.id.layout_payment_cash);
		// Retrieve a reference to the currenices spinner
		Spinner currenciesSpinner = (Spinner) findViewById(R.id.spinner_cash_currencies);
		// Retrieve a reference to the payment cash edit text
		EditText paymentEditText = (EditText) tertiary
				.findViewById(R.id.edittext_payment_cash);
		// Retrieve the amount
		String amountString = paymentEditText.getText().toString().trim();
		// Extract the amount
		Double amount = amountString.isEmpty() ? 0.0 : Double
				.parseDouble(amountString);
		// Check if the amount is strictly positive
		if (amount <= 0) {
			// Display baguette message
			Baguette.showText(
					this,
					AppResources.getInstance(this).getString(this,
							R.string.collection_invalid_amount_warning),
					Baguette.BackgroundColor.RED);
			// Exit method
			return;
		} // End if

		// Reset flag
		displayPaymentCash = false;

		// Retrieve the currency
		Currencies currency = (Currencies) currenciesSpinner.getSelectedItem();
		// Retrieve the call
		Call call = (Call) getIntent().getSerializableExtra(CALL);
		// Declare and initialize a collection detail for the current payment
		CollectionDetails collectionDetail = selectedPaymentIndex != null ? collectionDetails
				.get(selectedPaymentIndex) : null;
		// Declare a flag to indicate if the collection detail is to be updated
		boolean addAmount = false;
		// Check if the collection detail is null
		if (collectionDetail == null)
			// Iterate over the collection details
			for (CollectionDetails _collectionDetail : collectionDetails)
				// Check if the current detail is a cash payment for the same
				// currency
				if (_collectionDetail.getCollectionDetailType() == CollectionUtils.PaymentType.CASH
						&& _collectionDetail.getCurrencyCode().equals(
								currency.getCurrencyCode())) {
					// Set the collection detail
					collectionDetail = _collectionDetail;
					// Set flag
					addAmount = true;
					// Exit loop
					break;
				} // End if
		// Check if the collection detail is null
		if (collectionDetail == null)
			// Initialize a new collection detail
			collectionDetail = new CollectionDetails(null, // ID
					null, // CollectionCode
					0, // LineID
					call.getClientDivision().getDivisionCode(), // DivisionCode
					CollectionUtils.PaymentType.CASH, // CollectionDetailType
					null, // CollectionAmount
					null, // CurrencyCode
					null, // CurrencyRate
					null, // CheckCode
					null, // CheckDate
					null, // BankCode
					null, // BankDescription
					null, // BankBranchCode
					null); // StampDate
		// Set the amount
		collectionDetail.setCollectionAmount(addAmount ? collectionDetail
				.getCollectionAmount() + amount : amount);
		// Set the currency code
		collectionDetail.setCurrencyCode(currency.getCurrencyCode());
		// Set the currency rate
		collectionDetail.setCurrencyRate(currency.getCurrencyRate());

		// Disable the edit text
		paymentEditText.setEnabled(false);
		// Clear the edit text
		paymentEditText.setText("");
		// Disable the spinner
		currenciesSpinner.setEnabled(false);
		// Check if the current payment is new
		if (selectedPaymentIndex == null
				&& !collectionDetails.contains(collectionDetail))
			// Add the new payment to the list
			collectionDetails.add(collectionDetail);
		// Reset the selected payment index
		selectedPaymentIndex = null;
		// Refresh list
		((PaymentsAdapter) ((ListView) findViewById(R.id.list_payments))
				.getAdapter()).notifyDataSetChanged();

		// Disable the save icon
		tertiary.findViewById(R.id.icon_save_payment_cash).setEnabled(false);
		// Hide the software keyboard
		((InputMethodManager) getBaseContext().getSystemService(
				INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
				tertiary.getWindowToken(), 0);

		// Hide the tertiary view
		tertiary.startAnimation(getViewAnimationOut(tertiary));
		// Enable the main list
		setMainLayoutEnabled(true);
		// Indicate that the save was successful
		Vibration.vibrate(this);
		// Refresh the action bar
		invalidateOptionsMenu();
		// Update the payment info (if any)
		updatePaymentInfo();
	}

	/**
	 * Called when a view has been clicked.<br>
	 * The payment check is saved.
	 * 
	 * @param view
	 *            The clicked view.
	 */
	public void updatePaymentCheck(View view) {
		// Determine if the payment check is undergoing any modifications
		if (!displayPaymentCheck)
			// No modifications
			return;

		// Retrieve a reference to the quaternary view
		View quaternary = findViewById(R.id.layout_payment_check);
		// Retrieve a reference to the currencies spinner
		Spinner currenciesSpinner = (Spinner) findViewById(R.id.spinner_check_currencies);
		// Retrieve a reference to the banks spinner
		Spinner banksSpinner = (Spinner) findViewById(R.id.spinner_check_banks);
		// Retrieve a reference to the payment check edit text
		EditText paymentEditText = (EditText) quaternary
				.findViewById(R.id.edittext_payment_check);
		// Retrieve a reference to the check code edit text
		EditText codeEditText = (EditText) quaternary
				.findViewById(R.id.edittext_check_code);
		// Retrieve the amount
		String amountString = paymentEditText.getText().toString().trim();
		// Extract the amount
		Double amount = amountString.isEmpty() ? 0.0 : Double
				.parseDouble(amountString);
		// Check if the amount is strictly positive
		if (amount <= 0) {
			// Display baguette message
			Baguette.showText(
					this,
					AppResources.getInstance(this).getString(this,
							R.string.collection_invalid_amount_warning),
					Baguette.BackgroundColor.RED);
			// Exit method
			return;
		} // End if
			// Check if the check code is valid
		else if (codeEditText.getText().toString().trim().isEmpty()) {
			// Display baguette message
			Baguette.showText(
					this,
					AppResources.getInstance(this).getString(this,
							R.string.collection_invalid_check_code_warning),
					Baguette.BackgroundColor.RED);
			// Exit method
			return;
		} // End else if
			// Check if the check code is valid
		else if (collectionHeader.getStampDate() == null) {
			// Display baguette message
			Baguette.showText(
					this,
					AppResources.getInstance(this).getString(this,
							R.string.collection_invalid_check_date_warning),
					Baguette.BackgroundColor.RED);
			// Exit method
			return;
		} // End else if
			// Check if there is at least one bank
		else if (banks.isEmpty()) {
			// Display baguette message
			Baguette.showText(
					this,
					AppResources.getInstance(this).getString(this,
							R.string.collection_invalid_check_code_warning),
					Baguette.BackgroundColor.RED);
			// Exit method
			return;
		} // End else if
			// Check if this is a new check payment
		if (selectedPaymentIndex == null)
			// Iterate over the collection details
			for (CollectionDetails _collectionDetail : collectionDetails)
				// Check if the current detail is a check the same check code is
				// used
				if (_collectionDetail.getCollectionDetailType() == CollectionUtils.PaymentType.CHECK
						&& _collectionDetail.getCheckCode().equals(
								codeEditText.getText().toString().trim())) {
					// Display baguette message
					Baguette.showText(
							this,
							AppResources
									.getInstance(this)
									.getString(
											this,
											R.string.collection_similar_check_code_warning),
							Baguette.BackgroundColor.RED);
					// Exit method
					return;
				} // End if

		// Reset flag
		displayPaymentCheck = false;

		// Retrieve the currency
		Currencies currency = (Currencies) currenciesSpinner.getSelectedItem();
		// Retrieve the bank
		Banks bank = (Banks) banksSpinner.getSelectedItem();
		// Retrieve the call
		Call call = (Call) getIntent().getSerializableExtra(CALL);
		// Declare and initialize a collection detail for the current payment
		CollectionDetails collectionDetail = selectedPaymentIndex != null ? collectionDetails
				.get(selectedPaymentIndex) : new CollectionDetails(null, // ID
				null, // CollectionCode
				0, // LineID
				call.getClientDivision().getDivisionCode(), // DivisionCode
				CollectionUtils.PaymentType.CHECK, // CollectionDetailType
				null, // CollectionAmount
				null, // CurrencyCode
				null, // CurrencyRate
				null, // CheckCode
				null, // CheckDate
				null, // BankCode
				null, // BankDescription
				null, // BankBranchCode
				null); // StampDate
		// Set the amount
		collectionDetail.setCollectionAmount(amount);
		// Set the currency code
		collectionDetail.setCurrencyCode(currency.getCurrencyCode());
		// Set the currency rate
		collectionDetail.setCurrencyRate(currency.getCurrencyRate());
		// Set the check code
		collectionDetail.setCheckCode(codeEditText.getText().toString());
		// Set the bank code and description
		collectionDetail.setBankCode(bank.getBankCode());
		collectionDetail.setBankDescription(bank.getBankDescription());
		// Set the check date
		collectionDetail.setCheckDate(collectionHeader.getStampDate());
		collectionHeader.setStampDate(null);

		// Disable the edit text
		paymentEditText.setEnabled(false);
		// Clear the edit text
		paymentEditText.setText("");
		// Disable the edit text
		codeEditText.setEnabled(false);
		// Clear the edit text
		codeEditText.setText("");
		// Disable the spinner
		currenciesSpinner.setEnabled(false);
		// Check if the current payment is new
		if (selectedPaymentIndex == null)
			// Add the new payment to the list
			collectionDetails.add(collectionDetail);
		// Reset the selected payment index
		selectedPaymentIndex = null;
		// Refresh list
		((PaymentsAdapter) ((ListView) findViewById(R.id.list_payments))
				.getAdapter()).notifyDataSetChanged();

		// Disable the save icon
		quaternary.findViewById(R.id.icon_save_payment_check).setEnabled(false);
		// Hide the software keyboard
		((InputMethodManager) getBaseContext().getSystemService(
				INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
				quaternary.getWindowToken(), 0);

		// Hide the tertiary view
		quaternary.startAnimation(getViewAnimationOut(quaternary));
		// Enable the main list
		setMainLayoutEnabled(true);
		// Indicate that the save was successful
		Vibration.vibrate(this);
		// Refresh the action bar
		invalidateOptionsMenu();
		// Update the payment info (if any)
		updatePaymentInfo();
	}

	/**
	 * Called when a view has been clicked.<br>
	 * The paid amount is saved.
	 * 
	 * @param view
	 *            The clicked view.
	 */
	public void updatePaidAmount(View view) {
		// Determine if the paid amount is undergoing any modifications
		if (!displayPaidAmount)
			// No modifications
			return;

		// Retrieve a reference to the quinary view
		View quinary = findViewById(R.id.layout_paid_amount);
		// Retrieve a reference to the paid amount edit text
		EditText amountEditText = (EditText) quinary
				.findViewById(R.id.edittext_paid_amount);
		// Retrieve the amount
		String amountString = amountEditText.getText().toString().trim();
		// Extract the amount
		Double amount = amountString.isEmpty() ? 0.0 : Double
				.parseDouble(amountString);
		// Declare and initialize a variable used to host the remaining amount
		double remainingAmount = 0.0;
		// Determine the collection mode
		switch (collectionMode) {
		case PAYMENT_CLIENT_DUES:
			remainingAmount = clientDues.get(selectedPaymentIndex)
					.getRemainingAmount();
			break;
		case PAYMENT_INVOICES:
			remainingAmount = invoices.get(selectedPaymentIndex)
					.getRemainingAmount();
			break;
		} // End switch

		// Check if the amount is strictly positive
		if (amount < 0 || amount > remainingAmount) {
			// Display baguette message
			Baguette.showText(
					this,
					AppResources.getInstance(this).getString(this,
							R.string.collection_invalid_amount_warning),
					Baguette.BackgroundColor.RED);
			// Exit method
			return;
		} // End if

		// Reset flag
		displayPaidAmount = false;

		// Update the paid amount
		// Determine the collection mode
		switch (collectionMode) {
		case PAYMENT_CLIENT_DUES:
			clientDues.get(selectedPaymentIndex).setPaidAmount(amount);
			break;
		case PAYMENT_INVOICES:
			invoices.get(selectedPaymentIndex).setPaidAmount(amount);
			break;
		} // End switch
			// Disable the edit text
		amountEditText.setEnabled(false);
		// Clear the edit text
		amountEditText.setText("");
		// Reset the selected payment index
		selectedPaymentIndex = null;
		// Refresh list
		switch (collectionMode) {
		case PAYMENT_CLIENT_DUES:
			((ClientDuesAdapter) ((ListView) findViewById(R.id.list_dues))
					.getAdapter()).notifyDataSetChanged();
			break;
		case PAYMENT_INVOICES:
			((InvoicesAdapter) ((ListView) findViewById(R.id.list_dues))
					.getAdapter()).notifyDataSetChanged();
			break;
		} // End switch

		// Disable the save icon
		quinary.findViewById(R.id.icon_save_paid_amount).setEnabled(false);
		// Hide the software keyboard
		((InputMethodManager) getBaseContext().getSystemService(
				INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
				quinary.getWindowToken(), 0);

		// Hide the tertiary view
		quinary.startAnimation(getViewAnimationOut(quinary));
		// Enable the main list
		setMainLayoutEnabled(true);
		// Indicate that the save was successful
		Vibration.vibrate(this);
		// Refresh the action bar
		invalidateOptionsMenu();
		// Enable the add buttons accordingly
		refreshAddButtonsState();
		// Update the payment info (if any)
		updatePaymentInfo();
	}

	/**
	 * Called when a view has been clicked.<br>
	 * The payment is cancelled.
	 * 
	 * @param view
	 *            The clicked view.
	 */
	public void cancelPayment(View view) {
		// Retrieve the parent's view holder
		ViewHolder viewHolder = (ViewHolder) ((View) view.getParent()).getTag();
		// Remove the payment
		collectionDetails.remove(viewHolder.collectionDetail);
		// Refresh the list
		((PaymentsAdapter) ((ListView) findViewById(R.id.list_payments))
				.getAdapter()).notifyDataSetChanged();
		// Update the payment info (if any)
		updatePaymentInfo();
	}

	/**
	 * Saves the current collection.
	 */
	private void saveCollection() {
		// Make sure the collection can be saved
		if (collectionDetails.isEmpty()) {
			// Display baguette message
			Baguette.showText(
					this,
					AppResources.getInstance(this).getString(this,
							R.string.collection_no_payments_done_warning),
					Baguette.BackgroundColor.RED);
			// Exit method
			return;
		} // End if
		
		// Retrieve the user company code
		String userCompanyCode = DatabaseUtils.getCurrentCompanyCode(this);
		// Retrieve the user division code
		String userDivisionCode = DatabaseUtils.getCurrentDivisionCode(this);
		// Retrieve the user object
		Users user = DatabaseUtils
				.getInstance(this)
				.getDaoSession()
				.getUsersDao()
				.queryBuilder()
				.where(UsersDao.Properties.UserCode.eq(DatabaseUtils
						.getCurrentUserCode(this)),
						UsersDao.Properties.CompanyCode.eq(userCompanyCode))
				.unique();

		// Determine if the user is not cash van
		// AND the manual reference is invalid
		if (user.getUserType() != 11
				&& collectionHeader.getManualReference().isEmpty()) {
			// Set flag
			displayCollectionManualReference = true;
			// Initialize the secondary view
			initializeSecondaryPrimeView(false);
			// Disable the main list
			setMainLayoutEnabled(false);
			// Retrieve a reference to the secondary view
			View secondaryView = findViewById(R.id.layout_collection_manual_reference);
			// Display the tertiary view
			secondaryView.setVisibility(View.VISIBLE);
			// Animate the tertiary view
			secondaryView.startAnimation(getViewAnimationIn());
			// Refresh the action bar
			invalidateOptionsMenu();
			// Exit method
			return;
		} // End if

		// Retrieve the main currency
		Currencies currency = (Currencies) ((Spinner) findViewById(R.id.spinner_currencies))
				.getSelectedItem();
		// Map the currencies to their codes
		HashMap<String, Currencies> _currencies = new HashMap<String, Currencies>();
		// Iterate over the currencies
		for (Currencies _currency : currencies)
			// Map the currency to its code
			_currencies.put(_currency.getCurrencyCode(), _currency);
		// Compute the total payments
		double totalPayments = 0.0;
		for (CollectionDetails collectionDetail : collectionDetails)
			totalPayments += collectionDetail.getCollectionAmount()
					/ _currencies.get(collectionDetail.getCurrencyCode())
							.getCurrencyRate() * currency.getCurrencyRate();
		// Compute the total and available dues label
		double totalDues = 0.0;
		double availableDues = 0.0;
		if (collectionMode == PAYMENT_CLIENT_DUES)
			for (ClientDues clientDue : clientDues)
				if (clientDue.getInvoiceType() != null) {
					totalDues += (clientDue.getInvoiceType() == CollectionUtils.DuesType.DEBIT ? 1
							: -1)
							* (clientDue.getPaidAmount()
									/ _currencies.get(
											clientDue.getCurrencyCode())
											.getCurrencyRate() * currency
										.getCurrencyRate());
					availableDues += clientDue.getRemainingAmount()
							/ _currencies.get(clientDue.getCurrencyCode())
									.getCurrencyRate()
							* currency.getCurrencyRate();
				} // End if
		if (collectionMode == PAYMENT_INVOICES)
			for (TransactionHeaders invoice : invoices) {
				totalDues += invoice.getPaidAmount()
						/ _currencies.get(invoice.getCurrencyCode())
								.getCurrencyRate() * currency.getCurrencyRate();
				availableDues += invoice.getRemainingAmount()
						/ _currencies.get(invoice.getCurrencyCode())
								.getCurrencyRate() * currency.getCurrencyRate();
			}
		double difference = new BigDecimal(totalPayments - totalDues).setScale(
				2, RoundingMode.HALF_UP).doubleValue();

		// Check if the collection mode is not on account
		if (collectionMode == PAYMENT_CLIENT_DUES
				|| collectionMode == PAYMENT_INVOICES) {
			// Check if the total dues is valid
			if (totalDues <= 0) {
				// Display baguette message
				Baguette.showText(
						this,
						AppResources.getInstance(this).getString(this,
								R.string.collection_invalid_payment_warning),
						Baguette.BackgroundColor.RED);
				// Exit method
				return;
			} // End if
				// Check if the client is cash
				// AND if the remaining amounts are not totally selected
			if (availableDues != totalDues
					&& ClientCard.isCashClient(((Call) getIntent()
							.getSerializableExtra(CALL)).getClient())) {
				// Display baguette message
				Baguette.showText(
						this,
						AppResources
								.getInstance(this)
								.getString(
										this,
										R.string.collection_cash_client_payments_warning),
						Baguette.BackgroundColor.RED);
				// Exit method
				return;
			} // End if
				// Make sure the difference between the payments and the dues
				// does not exceed the currency margins
			if (difference == 0
					|| (difference > 0 && Math.abs(difference) <= currency
							.getCurrencyUpperMargin())
					|| (difference < 0 && Math.abs(difference) <= currency
							.getCurrencyLowerMargin()))
				; // Valid cases
			else {
				// Display baguette message
				Baguette.showText(
						this,
						AppResources
								.getInstance(this)
								.getString(
										this,
										R.string.collection_payments_dues_inconsistency_warning),
						Baguette.BackgroundColor.RED);
				// Exit method
				return;
			} // End else
				// Check if the collection mode is payment invoices
				// if ( collectionMode == PAYMENT_INVOICES ) {
				// Declare a flag
				// boolean refresh = false;
				// Iterate over the invoices
			// for ( TransactionHeaders invoice : invoices ) {
			// Refresh the invoice for any changes
			// DatabaseUtils.getInstance ( this ).getDaoSession
			// ().getTransactionHeadersDao ().refresh ( invoice );
			// Check if the invoice is processed
			// if ( invoice.getIsProcessed () == IsProcessedUtils.isSync () ) {
			// Display baguette message
			// Baguette.showText ( this , AppResources.getInstance ( this
			// ).getString ( this ,
			// R.string.collection_processed_invoice_warning ) ,
			// Baguette.BackgroundColor.RED );
			// Set flag
			// refresh = true;
			// Exit loop
			// break;
			// } // End if
			// } // End for each
			// Check if the invoices list should be refreshed
			// if ( refresh ) {
			// Clear invoices list
			// invoices = null;
			// Populate asynchronously
			// populate ();
			// // Exit method
			// return;
			// } // End if
			// } // End if
		} // End if
		if (!saveMovements ) {
			// Display baguette message
			Baguette.showText(
					this,
					AppResources.getInstance(this).getString(this,
							R.string.collection_no_payments_done_warning),
					Baguette.BackgroundColor.RED);
			// Exit method
			return;
		} // End if
		else
			saveMovements=false;
		// Flag used to indicate whether an error occurred or not
		boolean error = false;
		// Retrieve the current date
		Calendar now = Calendar.getInstance();
		// Declare and initialize a sequence formatter
		DecimalFormat sequence = new DecimalFormat("000000");
		// Retrieve the current call
		Call call = (Call) getIntent().getSerializableExtra(CALL);
		// Retrieve the visit
		Visits visit = (Visits) getIntent().getSerializableExtra(VISIT);

		// Compute the total amounts
		double totalAmountCash = 0;
		double totalAmountCheck = 0;
		// Declare and initialize lists to host the cash and check payments
		ArrayList<CollectionDetails> collectionDetailsCash = new ArrayList<CollectionDetails>();
		ArrayList<CollectionDetails> collectionDetailsCheck = new ArrayList<CollectionDetails>();
		// Iterate over the collection details
		for (CollectionDetails collectionDetail : collectionDetails) {
			// Determine if the current collection is cash
			if (collectionDetail.getCollectionDetailType() == CollectionUtils.PaymentType.CASH) {
				// Add the collection to the cash list
				collectionDetailsCash.add(collectionDetail);
				// Convert and add to the total amount
				totalAmountCash += collectionDetail.getCollectionAmount()
						/ _currencies.get(collectionDetail.getCurrencyCode())
								.getCurrencyRate() * currency.getCurrencyRate();
			} // End if
				// Otherwise determine if the current collection is check
			else if (collectionDetail.getCollectionDetailType() == CollectionUtils.PaymentType.CHECK) {
				// Add the collection to the check list
				collectionDetailsCheck.add(collectionDetail);
				// Convert and add to the total amount
				totalAmountCheck += collectionDetail.getCollectionAmount()
						/ _currencies.get(collectionDetail.getCurrencyCode())
								.getCurrencyRate() * currency.getCurrencyRate();
			} // End else if
		} // End for each

		try {
			String clientCode = "";
			// Determine the type
			int type = 0;
			switch (collectionMode) {
			case PAYMENT_ON_ACCOUNT:
				type = CollectionUtils.CollectionType.ONACCOUNT;
				clientCode = call.getClient().getParentCode();
				break;
			case PAYMENT_CLIENT_DUES:
			case PAYMENT_INVOICES:
				type = CollectionUtils.CollectionType.PARTIAL;
				clientCode = call.getClient().getClientCode();
				break;
			} // End switch
				// Retrieve the collection sequence
//			int collectionSequence = DatabaseUtils.getUserSequence(this,
//					user.getUserCode(), userCompanyCode,
//					TransactionSequencesDao.Properties.Collection);
			int collectionSequence = DatabaseUtils.getSequence( this , user.getUserCode () , DatabaseUtils.getCurrentCompanyCode ( this ) ,	TransactionHeadersUtils.Type.COLLECTION );
			
			// Begin transaction
			DatabaseUtils.getInstance(CollectionDetailsActivity.this)
					.getDaoSession().getDatabase().beginTransaction();
			// Declare and initialize a collection header for cash
			CollectionHeaders collectionHeaderCash = collectionDetailsCash
					.isEmpty() ? null : new CollectionHeaders(null, // ID
					String.valueOf(5) + String.valueOf(user.getPrefixID())
							+ sequence.format(collectionSequence++), // CollectionCode String.valueOf(type)
					visit.getVisitID(), // VisitID
					visit.getJourneyCode(), // JourneyCode
					clientCode,// call.getClient ().getParentCode () , //
								// ClientCode
					totalAmountCash, // TotalAmount
					currency.getCurrencyCode(), // CurrencyCode
					user.getUserCode(), // UserCode
					userDivisionCode, // DivisionCode
					userDivisionCode, // UserDivisionCode
					company.getCompanyCode(), // CompanyCode
					userCompanyCode, // UserCompanyCode
					now.getTime(), // CollectionDate
					type, // CollectionType
					StatusUtils.isActive(), // CollectionStatus
					null, // IsPDCCleared
					CollectionUtils.PaymentType.CASH, // PaymentType
					CollectionUtils.Settlement.IsNotSettled, // IsSetteled
					null, // PasswordCode
					null, // ParentCode
					null, // PrintingTimes
					collectionHeader.getNotes(), // Notes
					collectionHeader.getManualReference(), // ManualReference
					IsProcessedUtils.isNotSync(), // IsProcessed
					now.getTime()); // StampDate
			// Declare and initialize a collection header for check
			CollectionHeaders collectionHeaderCheck = collectionDetailsCheck
					.isEmpty() ? null : new CollectionHeaders(null, // ID
					String.valueOf(5) + String.valueOf(user.getPrefixID())
							+ sequence.format(collectionSequence++), // CollectionCode
					visit.getVisitID(), // VisitID
					visit.getJourneyCode(), // JourneyCode
					clientCode,// call.getClient ().getParentCode () , //
								// ClientCode
					totalAmountCheck, // TotalAmount
					currency.getCurrencyCode(), // CurrencyCode
					user.getUserCode(), // UserCode
					userDivisionCode, // DivisionCode
					userDivisionCode, // UserDivisionCode
					company.getCompanyCode(), // CompanyCode
					userCompanyCode, // UserCompanyCode
					now.getTime(), // CollectionDate
					type, // CollectionType
					StatusUtils.isActive(), // CollectionStatus
					null, // IsPDCCleared
					CollectionUtils.PaymentType.CHECK, // PaymentType
					CollectionUtils.Settlement.IsNotSettled, // IsSetteled
					null, // PasswordCode
					collectionHeaderCash == null ? null
							: collectionHeaderCash.getCollectionCode(), // ParentCode
					null, // PrintingTimes
					collectionHeader.getNotes(), // Notes
					collectionHeader.getManualReference(), // ManualReference
					IsProcessedUtils.isNotSync(), // IsProcessed
					now.getTime()); // StampDate
			// Update reference
			collectionHeader = collectionHeaderCash != null ? collectionHeaderCash
					: collectionHeaderCheck;

			// Check if the collection header of cash is valid
			if (collectionHeaderCash != null) {
				// Insert the header
				DatabaseUtils.getInstance(CollectionDetailsActivity.this)
						.getDaoSession().insert(collectionHeaderCash);
				// Update target coverage (if any)
				TargetUpdate.updateCoverage(CollectionDetailsActivity.this,
						TargetHeadersUtils.Type.TOTAL_COLLECTION,
						user.getUserCode(), TargetHeadersUtils.SubType.USER,
						null, -1, null, null, UpdateType.INCREASE,
						collectionHeaderCash.getTotalAmount(),
						collectionHeaderCash.getCurrencyCode());
			} // End if
				// Iterate over the collection details for cash
			for (int i = 0; i < collectionDetailsCash.size(); i++) {
				// Update the detail
				CollectionDetails collectionDetail = collectionDetailsCash
						.get(i);
				collectionDetail.setCollectionCode(collectionHeaderCash
						.getCollectionCode());
				collectionDetail.setLineID(i);
				collectionDetail.setDivisionCode(collectionHeaderCash
						.getDivisionCode());
				collectionDetail.setStampDate(now.getTime());
				// Insert the detail
				DatabaseUtils.getInstance(CollectionDetailsActivity.this)
						.getDaoSession().insert(collectionDetail);
			} // End for each

			// Check if the collection header of check is valid
			if (collectionHeaderCheck != null) {
				// Insert the header
				DatabaseUtils.getInstance(CollectionDetailsActivity.this)
						.getDaoSession().insert(collectionHeaderCheck);
				// Update target coverage (if any)
				TargetUpdate.updateCoverage(CollectionDetailsActivity.this,
						TargetHeadersUtils.Type.TOTAL_COLLECTION,
						user.getUserCode(), TargetHeadersUtils.SubType.USER,
						null, -1, null, null, UpdateType.INCREASE,
						collectionHeaderCheck.getTotalAmount(),
						collectionHeaderCheck.getCurrencyCode());
			} // End if
				// Iterate over the collection details for check
			for (int i = 0; i < collectionDetailsCheck.size(); i++) {
				// Update the detail
				CollectionDetails collectionDetail = collectionDetailsCheck
						.get(i);
				collectionDetail.setCollectionCode(collectionHeaderCheck
						.getCollectionCode());
				collectionDetail.setLineID(i);
				collectionDetail.setDivisionCode(collectionHeaderCheck
						.getDivisionCode());
				collectionDetail.setStampDate(now.getTime());
				// Insert the detail
				DatabaseUtils.getInstance(CollectionDetailsActivity.this)
						.getDaoSession().insert(collectionDetail);
			} // End for each

			// Update the collection sequence
//			DatabaseUtils.setUserSequence(this, user.getUserCode(),
//					userCompanyCode,
//					TransactionSequencesDao.Properties.Collection,
//					collectionSequence);
			TransactionSequences transactionSequences=DatabaseUtils.getInstance(this).getDaoSession ().getTransactionSequencesDao().queryBuilder().where
					(TransactionSequencesDao.Properties.UserCode.eq( user.getUserCode ()),
							TransactionSequencesDao.Properties.CompanyCode.eq(DatabaseUtils.getCurrentCompanyCode(this) )).unique();
    		transactionSequences.setCollection(collectionSequence  );
    		DatabaseUtils.getInstance( this).getDaoSession ().getTransactionSequencesDao().update(transactionSequences);
			

			// Check if the collection mode is not on account
			if (collectionMode == PAYMENT_CLIENT_DUES
					|| collectionMode == PAYMENT_INVOICES) {
				// Create temp lists for dues / invoices
				ArrayList<ClientDues> _clientDues = new ArrayList<ClientDues>();
				if (clientDues != null)
					_clientDues.addAll(clientDues);
				ArrayList<TransactionHeaders> _invoices = new ArrayList<TransactionHeaders>();
				if (invoices != null)
					_invoices.addAll(invoices);
				// Validate the total amounts
				if ((totalAmountCash + totalAmountCheck) < totalDues) {
					// Check if the cash amount is valid
					if (totalAmountCash > 0)
						// Add the difference to the cash amount
						totalAmountCash += totalDues
								- (totalAmountCash + totalAmountCheck);
					// Check if the check amount is valid
					else if (totalAmountCheck > 0)
						// Add the difference to the check amount
						totalAmountCheck += totalDues
								- (totalAmountCash + totalAmountCheck);
				} // End if
					// Initialize the payments array
				String collectionCodes[] = new String[] {
						collectionHeaderCash == null ? null
								: collectionHeaderCash.getCollectionCode(),
						collectionHeaderCheck == null ? null
								: collectionHeaderCheck.getCollectionCode() };
				Double totalCollections[] = new Double[] { totalAmountCash,
						totalAmountCheck };
				// Iterate over the payments
				for (int i = 0; i < collectionCodes.length; i++) {
					// Iterate over the dues
					for (int j = 0; j < _clientDues.size(); j++) {
						// Retrieve the client due
						ClientDues clientDue = _clientDues.get(j);
						// Check if the paid amount is valid
						if (clientDue.getPaidAmount() <= 0)
							// Skip current due
							continue;
						// Check if the payment is valid
						if (totalCollections[i] <= 0)
							// Exit loop
							break;
						// Convert paid amount
						double convertedPaidAmount = clientDue.getPaidAmount()
								/ _currencies.get(clientDue.getCurrencyCode())
										.getCurrencyRate()
								* currency.getCurrencyRate();
						// Deduce the applied amount
						double convertedAppliedAmount = totalCollections[i] >= convertedPaidAmount ? convertedPaidAmount
								: totalCollections[i];
						// Compute the remaining total amount
						totalCollections[i] = totalCollections[i] >= convertedPaidAmount ? totalCollections[i]
								- convertedPaidAmount
								: 0;
						// Insert a collection invoice using the converted
						// applied amount
						DatabaseUtils.getInstance(this).getDaoSession()
								.getCollectionInvoicesDao()
								.insert(new CollectionInvoices(null, // ID
										collectionCodes[i], // CollectionCode
										clientDue.getInvoiceCode(), // InvoiceCode
										clientDue.getDivisionCode(), // DivisionCode
										clientDue.getCompanyCode(), // CompanyCode
										convertedAppliedAmount, // TotalAmount
										currency.getCurrencyCode(), // CurrencyCode
										2, // InvoiceSource
										currency.getCurrencyRate(), // CurrencyRate
										clientDue.getIssueDate(), // InvoiceDate
										now.getTime())); // StampDate
						// Convert the applied amount back to the due currency
						double appliedAmount = convertedAppliedAmount
								/ currency.getCurrencyRate()
								* _currencies.get(clientDue.getCurrencyCode())
										.getCurrencyRate();
						// Update the remaining and paid amount
						double remainingAmount = clientDue.getRemainingAmount()
								- appliedAmount;
						remainingAmount = remainingAmount < 0 ? 0
								: remainingAmount;
						double paidAmount = clientDue.getPaidAmount()
								- appliedAmount;
						paidAmount = paidAmount < 0 ? 0 : paidAmount;
						double roundOff = Math.round(remainingAmount * 100.0) / 100.0;
						clientDue.setRemainingAmount( roundOff);
						clientDue.setPaidAmount( paidAmount);
						// Update the client due
						DatabaseUtils.getInstance(this).getDaoSession()
								.getClientDuesDao().update(clientDue);
					 
					} // End for loop
						// Iterate over the invoices
					for (int j = 0; j < _invoices.size(); j++) {
						// Retrieve the client due
						TransactionHeaders transactionHeader = _invoices.get(j);
						// Check if the paid amount is valid
						if (transactionHeader.getPaidAmount() <= 0)
							// Skip current due
							continue;
						// Check if the payment is valid
						if (totalCollections[i] <= 0)
							// Exit loop
							break;
						// Convert paid amount
						double convertedPaidAmount = transactionHeader
								.getPaidAmount()
								/ _currencies.get(
										transactionHeader.getCurrencyCode())
										.getCurrencyRate()
								* currency.getCurrencyRate();
						// Deduce the applied amount
						double convertedAppliedAmount = totalCollections[i] >= convertedPaidAmount ? convertedPaidAmount
								: totalCollections[i];
						// Compute the remaining total amount
						totalCollections[i] = totalCollections[i] >= convertedPaidAmount ? totalCollections[i]
								- convertedPaidAmount
								: 0;
						// Insert a collection invoice using the converted
						// applied amount
						DatabaseUtils.getInstance(this).getDaoSession()
								.getCollectionInvoicesDao()
								.insert(new CollectionInvoices(null, // ID
										collectionCodes[i], // CollectionCode
										transactionHeader.getTransactionCode(), // InvoiceCode
										transactionHeader.getDivisionCode(), // DivisionCode
										transactionHeader.getCompanyCode(), // CompanyCode
										convertedAppliedAmount, // TotalAmount
										currency.getCurrencyCode(), // CurrencyCode
										1, // InvoiceSource
										currency.getCurrencyRate(), // CurrencyRate
										transactionHeader.getIssueDate(), // InvoiceDate
										now.getTime())); // StampDate
						// Convert the applied amount back to the due currency
						double appliedAmount = convertedAppliedAmount
								/ currency.getCurrencyRate()
								* _currencies.get(
										transactionHeader.getCurrencyCode())
										.getCurrencyRate();
						// Update the remaining and paid amount
						double remainingAmount = transactionHeader
								.getRemainingAmount() - appliedAmount;
						remainingAmount = remainingAmount < 0 ? 0
								: remainingAmount;
						remainingAmount = Math.round(remainingAmount * 1000.0) / 1000.0;
						double paidAmount = transactionHeader.getPaidAmount()
								- appliedAmount;
						paidAmount = paidAmount < 0 ? 0 : paidAmount;
						transactionHeader.setRemainingAmount( remainingAmount );
						transactionHeader.setPaidAmount(paidAmount);
						if (transactionHeader.getIsProcessed() == 2)
							transactionHeader.setIsProcessed(IsProcessedUtils
									.isNotSync());
						// Update the client due
						DatabaseUtils.getInstance(this).getDaoSession()
								.getTransactionHeadersDao()
								.update(transactionHeader);
					} // End for loop
				} // End for loop
			} // End if

			// Indicate that a sales order was successfully performed during
			// this visit
			CallAction.setCallActionStatus(this, visit.getVisitID(),
					CallAction.ID.COLLECTION, true);

			// Commit changes
			DatabaseUtils.getInstance(CollectionDetailsActivity.this)
					.getDaoSession().getDatabase().setTransactionSuccessful();
			// Indicate that the save was successful
			Vibration.vibrate(CollectionDetailsActivity.this);
		} catch (Exception exception) {
			// Indicate that an error occurred
			error = true;
		} finally {
			// End transaction
			DatabaseUtils.getInstance(CollectionDetailsActivity.this)
					.getDaoSession().getDatabase().endTransaction();
		} // End try-catch-finally block

		// Determine if an error occurred
		if (error) {
			// Dismiss any displayed dialogs
			AppDialog.getInstance().dismiss();
			// Display baguette message
			Baguette.showText(
					this,
					AppResources.getInstance(this).getString(this,
							R.string.collection_save_failure_message),
					Baguette.BackgroundColor.RED);
			// Exit method
			return;
		} // End if
			// Indicate that the save was successful
		Vibration.vibrate(CollectionDetailsActivity.this);
		// Set flag
		displayPrintingConfirmation = true;
		// Display printing confirmation
		displayPrintingConfirmation();
	}

	/**
	 * AsyncTask helper class used to populate the collection lists with the
	 * appropriate payments / dues.
	 * 
	 * @author Elias
	 * 
	 */
	private class PopulateList extends AsyncTask<Void, Void, Void> {

		/**
		 * Flag used to determine if an error occurred.
		 */
		private boolean error;

		/**
		 * Flag used to indicate if the activity should be finished.
		 */
		private boolean finish;

		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground(Void... params) {
			// Get dao session reference
			DaoSession daoSession = DatabaseUtils.getInstance(
					CollectionDetailsActivity.this).getDaoSession();
			// Retrieve the current call
			Call call = (Call) getIntent().getSerializableExtra(CALL);
			// Retrieve the current date
			Calendar calendar = Calendar.getInstance();
			// Retrieve the start of the day
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			// Check if the currencies list is valid
			if (currencies == null)
				// Initialize variable
				currencies = new ArrayList<Currencies>(daoSession
						.getCurrenciesDao().queryBuilder()
						.orderAsc(CurrenciesDao.Properties.CurrencyPriority)
						.list());

			// Check if the banks list is valid
			if (banks == null)
				// Initialize variable
				banks = new ArrayList<Banks>(daoSession.getBanksDao()
						.queryBuilder()
						.orderAsc(BanksDao.Properties.BankDescription).list());

			// Make sure there is at least one currency
			if (currencies.isEmpty()) {
				// Exit module
				finish = true;
				return null;
			} // End if

			// Check if the collection header or detail is invalid
			if (collectionHeader == null || collectionDetails == null) {
				// Initialize variables
				collectionHeader = new CollectionHeaders();
				collectionHeader.setNotes("");
				collectionHeader.setManualReference("");
				collectionDetails = new ArrayList<CollectionDetails>();
			} // End if

			// Check if the total client dues list is invalid
			if (totalClientDues == null && collectionMode == PAYMENT_ON_ACCOUNT)
				// Initialize variable
				totalClientDues = new ArrayList<TotalClientDues>(daoSession
						.getTotalClientDuesDao()
						.queryBuilder()
						.where(TotalClientDuesDao.Properties.ParentCode.eq(call
								.getClient().getParentCode()),
								TotalClientDuesDao.Properties.CompanyCode
										.eq(company.getCompanyCode())).list());

			// Check if the client dues list is invalid
			if (clientDues == null && collectionMode == PAYMENT_CLIENT_DUES) {
				// Initialize variable
				// Compute the selection arguments
			//	String userCompanyCode = DatabaseUtils.getCurrentCompanyCode(CollectionDetailsActivity.this);
				// Retrieve the user division code
				//String userDivisionCode = DatabaseUtils.getCurrentDivisionCode(this);
			//	DatabaseUtils.getInstance ( CollectionDetailsActivity.this ).getDaoSession ().getDatabase ().beginTransaction();
				clientDues = new ArrayList<ClientDues>(
						daoSession
								.getClientDuesDao()
								.queryRaw ( " WHERE ClientCode= ?  And  CompanyCode= ? And DivisionCode=? And RemainingAmount > ? " ,
								new String [] { 
								String.valueOf ( call.getClient().getClientCode() ) , 
								String.valueOf ( call.getClient().getCompanyCode()),
								String.valueOf ( call.getClientDivision().getDivisionCode()) , 
								String.valueOf ( 0 ) } ));
				//DatabaseUtils.getInstance (  CollectionDetailsActivity.this ).getDaoSession ().getDatabase ().setTransactionSuccessful ();
				//DatabaseUtils.getInstance (  CollectionDetailsActivity.this ).getDaoSession ().getDatabase ().endTransaction();		
				//	.queryBuilder()
								//.where(ClientDuesDao.Properties.ClientCode
									//	.eq(call.getClient().getClientCode()),
									//	ClientDuesDao.Properties.CompanyCode
										//		.eq(call.getClient()
											//			.getCompanyCode()),
									//	ClientDuesDao.Properties.DivisionCode
										//		.eq(call.getClientDivision()
											//			.getDivisionCode()),
										//ClientDuesDao.Properties.RemainingAmount
											//	.gt(0)).list());
				// Iterate over the client dues
				for (ClientDues clientDue : clientDues)
					// Clear the paid amount
					clientDue.setPaidAmount(0);
			} // End if

			// Check if the invoices list is invalid
			if (invoices == null && collectionMode == PAYMENT_INVOICES) {
				// Initialize variable
				invoices = new ArrayList<TransactionHeaders>(
						daoSession
								.getTransactionHeadersDao()
								.queryBuilder()
								.where(TransactionHeadersDao.Properties.ClientCode
										.eq(call.getClient().getClientCode()),
										TransactionHeadersDao.Properties.CompanyCode
												.eq(call.getClient()
														.getCompanyCode()),
										TransactionHeadersDao.Properties.DivisionCode
												.eq(call.getClientDivision()
														.getDivisionCode()),
										TransactionHeadersDao.Properties.RemainingAmount
												.gt(0),
										TransactionHeadersDao.Properties.TransactionType
												.eq(TransactionHeadersUtils.Type.SALES_INVOICE),
										TransactionHeadersDao.Properties.IssueDate
												.ge(calendar.getTime()),
										// TransactionHeadersDao.Properties.IsProcessed.eq
										// ( IsProcessedUtils.isNotSync () ) ,
										TransactionHeadersDao.Properties.TransactionStatus
												.eq(StatusUtils.isAvailable()))
								.list());
				// Iterate over the invoices
				for (TransactionHeaders invoice : invoices)
					// Clear the paid amount
					invoice.setPaidAmount(0);
			} // End if

			// Clear the task reference
			populateList = null;
			return null;
		}

		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Void arg) {
			// Check if the activity should end
			if (finish) {
				// Indicate that the activity cannot be displayed
				new AppToast(CollectionDetailsActivity.this)
						.setIcon(R.drawable.warning)
						.setText(
								AppResources
										.getInstance(
												CollectionDetailsActivity.this)
										.getString(
												CollectionDetailsActivity.this,
												R.string.collection_no_currencies_warning))
						.show();
				// Check if this is a collection only call
				if (getIntent().getBooleanExtra(COLLECTION_ONLY, false)) {
					// Retrieve the visit object
					Visits visit = (Visits) getIntent().getSerializableExtra(
							VISIT);
					// Refresh the visit object
					DatabaseUtils.getInstance(CollectionDetailsActivity.this)
							.getDaoSession().getVisitsDao().refresh(visit);
					// End the visit
					visit.setEndDate(Calendar.getInstance().getTime());
					// Update the visit in DB
					DatabaseUtils.getInstance(CollectionDetailsActivity.this)
							.getDaoSession().getVisitsDao().update(visit);

					// Clear call action data
					CallAction.clearData(CollectionDetailsActivity.this);
					// Call this to set the result that your activity will
					// return to its caller
					setResult(
							RESULT_OK,
							new Intent()
									.putExtra(CallMenuActivity.VISIT, visit)
									.putExtra(
											CallMenuActivity.CALL,
											getIntent().getSerializableExtra(
													CALL)));
				} // End if
					// Finish activity
				CollectionDetailsActivity.this.finish();
				// Exit method
				return;
			} // End if
				// Determine if the activity must be finished
			if (error) {
				// Indicate that the activity cannot be displayed
				new AppToast(CollectionDetailsActivity.this)
						.setIcon(R.drawable.warning)
						.setText(
								AppResources
										.getInstance(
												CollectionDetailsActivity.this)
										.getString(
												CollectionDetailsActivity.this,
												R.string.missing_client_currency_price_list_message))
						.setDuration(Toast.LENGTH_LONG).show();
				// Exit method
				return;
			} // End if

			// Retrieve a reference to the currencies spinner
			Spinner currenciesSpinner = (Spinner) findViewById(R.id.spinner_currencies);
			// Retrieve a reference to the currencies payment cash spinner
			Spinner currenciesPaymentCashSpinner = (Spinner) findViewById(R.id.spinner_cash_currencies);
			// Retrieve a reference to the currencies payment check spinner
			Spinner currenciesPaymentCheckSpinner = (Spinner) findViewById(R.id.spinner_check_currencies);
			// Retrieve a reference to the banks spinner
			Spinner banksSpinner = (Spinner) findViewById(R.id.spinner_check_banks);

			// Declare and initialize a new currency adapter used to populate
			// the currencies spinner
			CurrencyAdapter currencyAdapter = new CurrencyAdapter(
					CollectionDetailsActivity.this,
					android.R.layout.simple_spinner_item, currencies);
			// Sets the layout resource to create the drop down views
			currencyAdapter
					.setDropDownViewResource(R.layout.data_management_activity_connection_item);
			// Set the spinner adapter
			currenciesSpinner.setAdapter(currencyAdapter);
			// Select the correct currency
			currenciesSpinner.setSelection(selectedCurrencyIndex);
			// Assign an item selected listener
			currenciesSpinner
					.setOnItemSelectedListener(CollectionDetailsActivity.this);
			// Enabled the spinner
			currenciesSpinner.setEnabled(true);

			// Declare and initialize a new currency adapter used to populate
			// the currencies spinner
			currencyAdapter = new CurrencyAdapter(
					CollectionDetailsActivity.this,
					android.R.layout.simple_spinner_item, currencies);
			// Sets the layout resource to create the drop down views
			currencyAdapter
					.setDropDownViewResource(R.layout.data_management_activity_connection_item);
			// Set the spinner adapter
			currenciesPaymentCashSpinner.setAdapter(currencyAdapter);

			// Declare and initialize a new currency adapter used to populate
			// the currencies spinner
			currencyAdapter = new CurrencyAdapter(
					CollectionDetailsActivity.this,
					android.R.layout.simple_spinner_item, currencies);
			// Sets the layout resource to create the drop down views
			currencyAdapter
					.setDropDownViewResource(R.layout.data_management_activity_connection_item);
			// Set the spinner adapter
			currenciesPaymentCheckSpinner.setAdapter(currencyAdapter);

			// Declare and initialize a new banks adapter used to populate the
			// banks spinner
			BanksAdapter banksAdapter = new BanksAdapter(
					CollectionDetailsActivity.this,
					android.R.layout.simple_spinner_item, banks);
			// Sets the layout resource to create the drop down views
			banksAdapter
					.setDropDownViewResource(R.layout.data_management_activity_connection_item);
			// Set the spinner adapter
			banksSpinner.setAdapter(banksAdapter);

			// Retrieve a reference to the secondary view
			View secondary = findViewById(R.id.layout_collection_note);
			// Display the tertiary view accordingly
			secondary.setVisibility(displayCollectionNote ? View.VISIBLE
					: View.GONE);
			// Enable the main list accordingly
			setMainLayoutEnabled(!displayCollectionNote);
			// Determine if the collection note is undergoing any modifications
			if (displayCollectionNote)
				// Restore the secondary view
				initializeSecondaryView(true);

			// Retrieve a reference to the secondary view
			View secondaryPrime = findViewById(R.id.layout_collection_manual_reference);
			// Display the tertiary view accordingly
			secondaryPrime
					.setVisibility(displayCollectionManualReference ? View.VISIBLE
							: View.GONE);
			// Enable the main list accordingly
			setMainLayoutEnabled(!displayCollectionManualReference);
			// Determine if the collection note is undergoing any modifications
			if (displayCollectionManualReference)
				// Restore the secondary view
				initializeSecondaryPrimeView(true);

			// Retrieve a reference to the tertiary view
			View tertiary = findViewById(R.id.layout_payment_cash);
			// Display the tertiary view accordingly
			tertiary.setVisibility(displayPaymentCash ? View.VISIBLE
					: View.GONE);
			// Enable the main list accordingly
			setMainLayoutEnabled(!displayPaymentCash);
			// Determine if the payment cash is undergoing any modifications
			if (displayPaymentCash)
				// Restore the tertiary view
				initializeTertiaryView(null, true);

			// Retrieve a reference to the quaternary view
			View quaternary = findViewById(R.id.layout_payment_check);
			// Display the quaternary view accordingly
			quaternary.setVisibility(displayPaymentCheck ? View.VISIBLE
					: View.GONE);
			// Enable the main list accordingly
			setMainLayoutEnabled(!displayPaymentCheck);
			// Determine if the payment check is undergoing any modifications
			if (displayPaymentCheck)
				// Restore the tertiary view
				initializeQuaternaryView(null, true);

			// Retrieve a reference to the quinary view
			View quinary = findViewById(R.id.layout_paid_amount);
			// Display the quinary view accordingly
			quinary.setVisibility(displayPaidAmount ? View.VISIBLE : View.GONE);
			// Enable the main list accordingly
			setMainLayoutEnabled(!displayPaidAmount);
			// Determine if the paid amount is undergoing any modifications
			if (displayPaidAmount)
				// Restore the tertiary view
				initializeQuinaryView(true);

			// Map the currencies to their codes
			HashMap<String, Currencies> _currencies = new HashMap<String, Currencies>();
			// Iterate over the currencies
			for (Currencies currency : currencies)
				// Map the currency to its code
				_currencies.put(currency.getCurrencyCode(), currency);

			// Map the banks to their codes
			HashMap<String, Banks> _banks = new HashMap<String, Banks>();
			// Iterate over the currencies
			for (Banks bank : banks)
				// Map the bank to its code
				_banks.put(bank.getBankCode(), bank);

			// Retrieve a reference to the payments and dues layouts
			View paymentsLayout = findViewById(R.id.layout_payments);
			View duesLayout = findViewById(R.id.layout_dues);
			// Retrieve a reference to the payments and dues lists
			ListView paymentsList = (ListView) paymentsLayout
					.findViewById(R.id.list_payments);
			ListView duesList = (ListView) duesLayout
					.findViewById(R.id.list_dues);
			// Assign a click listener for the payments list
			paymentsList.setOnItemClickListener(CollectionDetailsActivity.this);
			// Set the list adapters
			paymentsList.setAdapter(new PaymentsAdapter(
					CollectionDetailsActivity.this,
					R.layout.collection_details_activity_payments_item,
					collectionDetails, _currencies, _banks));
			switch (collectionMode) {
			case PAYMENT_ON_ACCOUNT:
				duesList.setAdapter(new TotalClientDuesAdapter(
						CollectionDetailsActivity.this,
						R.layout.collection_details_activity_dues_item,
						totalClientDues, _currencies));
				break;
			case PAYMENT_CLIENT_DUES:
				for(ClientDues c : clientDues)
					if(c.getRemainingAmount()==0)
						clientDues.remove(c);
					
				duesList.setAdapter(new ClientDuesAdapter(
						CollectionDetailsActivity.this,
						R.layout.collection_details_activity_dues_item,
						clientDues, _currencies, ClientCard
								.isCreditClient(((Call) getIntent()
										.getSerializableExtra(CALL))
										.getClient())));
				break;
			case PAYMENT_INVOICES:
				duesList.setAdapter(new InvoicesAdapter(
						CollectionDetailsActivity.this,
						R.layout.collection_details_activity_dues_item,
						invoices, _currencies, ClientCard
								.isCreditClient(((Call) getIntent()
										.getSerializableExtra(CALL))
										.getClient())));
				break;
			default:
				break;
			} // End switch

			// Refresh the action bar
			invalidateOptionsMenu();
			// Enable the add buttons accordingly
			refreshAddButtonsState();
			// Update the payment info (if any)
			updatePaymentInfo();
		}

	}

}