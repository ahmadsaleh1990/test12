/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.ClientsList.ClientInfo;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppFragmentPagerAdapter;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.AppToast;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.AreaLevels;
import me.SyncWise.Android.Database.AreaLevelsDao;
import me.SyncWise.Android.Database.Areas;
import me.SyncWise.Android.Database.AreasDao;
import me.SyncWise.Android.Database.ClientAreas;
import me.SyncWise.Android.Database.ClientAreasDao;
import me.SyncWise.Android.Database.ClientAvailabilities;
import me.SyncWise.Android.Database.ClientAvailabilitiesDao;
import me.SyncWise.Android.Database.ClientContacts;
import me.SyncWise.Android.Database.ClientContactsDao;
import me.SyncWise.Android.Database.ClientCreditings;
import me.SyncWise.Android.Database.ClientCreditingsDao;
import me.SyncWise.Android.Database.ClientCurrencies;
import me.SyncWise.Android.Database.ClientCurrenciesDao;
import me.SyncWise.Android.Database.ClientDues;
import me.SyncWise.Android.Database.ClientDuesDao;
import me.SyncWise.Android.Database.ClientItemClassificationLevelsDao;
import me.SyncWise.Android.Database.ClientItemClassificationsDao;
import me.SyncWise.Android.Database.ClientProperties;
import me.SyncWise.Android.Database.ClientPropertiesDao;
import me.SyncWise.Android.Database.ClientPropertyLevels;
import me.SyncWise.Android.Database.ClientPropertyLevelsDao;
import me.SyncWise.Android.Database.ClientPropertyValues;
import me.SyncWise.Android.Database.ClientPropertyValuesDao;
import me.SyncWise.Android.Database.Clients;
import me.SyncWise.Android.Database.ClientsDao;
import me.SyncWise.Android.Database.CompaniesDao;
import me.SyncWise.Android.Database.Currencies;
import me.SyncWise.Android.Database.CurrenciesDao;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.Divisions;
import me.SyncWise.Android.Database.DivisionsDao;
import me.SyncWise.Android.Database.DivisionsUtils;
import me.SyncWise.Android.Database.ItemsDao;
import me.SyncWise.Android.Database.StatusUtils;
import me.SyncWise.Android.Gson.BaseTimerFragmentActivity;
import me.SyncWise.Android.SectionsList.MultipleListAdapter;
import me.SyncWise.Android.SectionsList.Section;
import me.SyncWise.Android.SectionsList.Section.BackgroundType;
import me.SyncWise.Android.Utilities.ActivityInstance;
import me.SyncWise.Android.Utilities.AppFragment;
import me.SyncWise.Android.Utilities.ClientCard;
import me.SyncWise.Android.Widgets.Baguette;
import me.SyncWise.Android.Widgets.AvailabilityCalendar.AvailabilityCalendar;
import me.SyncWise.Android.Widgets.AvailabilityCalendar.AvailabilityCalendarView;
import me.SyncWise.Android.Widgets.ViewPagerIndicator.TabPageIndicator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;

/**
 * Activity implemented to display Client information.
 * 
 * @author Elias - Ahmad
 * @sw.todo	<b>Client Info Activity Implementation :</b><br>
 * <ul>
 * <li>If you want the default implementation, simply add this class in the {@code AndroidManifest.xml} file.<br>
 * Do not forget to set a reference to this class in the {@link me.SyncWise.Android.App App} class, using the {@link me.SyncWise.Android.App#setCallMenuActivityClass(Class) setCallMenuActivityClass} method.<br>
 * The default behavior <em>should</em> display in this activity only the basic client information (along with the client contacts) <em>({@link me.SyncWise.Android.Modules.ClientsList.ClientInfo.ClientInfoFragment ClientInfoFragment})</em>.</li>
 * </ul>
 * <br><em><h1>OR</h1></em><br>
 * In order to customize the module, please follow the steps below :
 * <ul>
 * <li>Extend this class.</li>
 * <li>Override the {@link #getAppFragmentPagerAdapter()} method in order to define what client data to display <em>(via the various fragments made for this activity)</em>.</li>
 * <li>Do not forget to add your new class in the {@code AndroidManifest.xml} file.</li>
 * <li>Set a reference to the subclass in the {@link me.SyncWise.Android.App App} class, using the {@link me.SyncWise.Android.App#setCallMenuActivityClass(Class) setCallMenuActivityClass} method.</li>
 * </ul>
 *
 */
@SuppressLint("DefaultLocale") 
public class ClientInfoActivity extends BaseTimerFragmentActivity {
	
	/**
	 * List used to host the fragments descriptions.
	 */
	protected ArrayList < AppFragment > fragments;
	
	/**
	 * Bundle key used to put/retrieve the content of the current client code.
	 */
	public static final String CLIENT_CODE = ClientInfoActivity.class.getName () + ".CLIENT_CODE";
	
	/**
	 * Bundle key used to put/retrieve the content of the current company code.
	 */
	public static final String COMPANY_CODE = ClientInfoActivity.class.getName () + ".COMPANY_CODE";
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #client}.
	 */
	private static final String CLIENT = ClientInfoActivity.class.getName () + ".CLIENT";
	
	/**
	 * {@link me.SyncWise.Android.Database.Clients Clients} object holding a reference to the client.
	 */
	private Clients client;
	
	/**
	 * List {@link me.SyncWise.Android.Database.Divisions Divisions} object holding references to the client's divisions.
	 */
	private ArrayList < Divisions > clientDivisions;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #clientInfo}.
	 */
	private static final String CLIENT_INFO = ClientInfoActivity.class.getName () + ".CLIENT_INFO";
	
	/**
	 * List of {@link me.SyncWise.Android.Database.Clients Clients} objects used to host the client personal information.
	 */
	private ArrayList < ClientInfo > clientInfo;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #clientContacts}.
	 */
	private static final String CLIENT_CONTACTS = ClientInfoActivity.class.getName () + ".CLIENT_CONTACTS";	
	
	/**
	 * List of {@link me.SyncWise.Android.Database.ClientContacts ClientContacts} objects used to host the client's contacts.
	 */
	private ArrayList < ClientContacts > clientContacts;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #calendarSlots}.
	 */
	private static final String CALENDAR_SLOTS = ClientInfoActivity.class.getName () + ".CALENDAR_SLOTS";	
	
	/**
	 * Map of {@link me.SyncWise.Android.Widgets.AvailabilityCalendar.AvailabilityCalendar AvailabilityCalendar} objects hosting the availability slots of the current availability calendar view.<br>
	 * The lists are mapped to the appropriate day number based on the {@link java.util.Calendar Calendar} class.
	 */
	private HashMap < Integer , ArrayList < AvailabilityCalendar > > calendarSlots;
	
	/**
	 * Flag used to indicate if current client belongs to the user.
	 */
	private boolean belongsToUser;
	
	/**
	 * Reference to the client info population task.
	 */
	private static PopulateList populateList;
	
	/**
	 * Setter - {@link #calendarSlots}
	 * 
	 * @param calendarSlots Map of {@link me.SyncWise.Android.Widgets.AvailabilityCalendar.AvailabilityCalendar AvailabilityCalendar} objects hosting the availability slots of the current availability calendar view.
	 */
	public void setCalendarSlots ( final HashMap < Integer , ArrayList < AvailabilityCalendar > > calendarSlots ) {
		this.calendarSlots = calendarSlots;
	}

	/**
	 * Getter - {@link #calendarSlots}
	 * 
	 * @return Map of {@link me.SyncWise.Android.Widgets.AvailabilityCalendar.AvailabilityCalendar AvailabilityCalendar} objects hosting the availability slots of the current availability calendar view.
	 */
	public HashMap < Integer , ArrayList < AvailabilityCalendar > > getCalendarSlots () {
		return calendarSlots;
	}
	
	/**
	 * Setter - {@link #belongsToUser}
	 * 
	 * @param belongsToUser	Flag used to indicate if current client belongs to the user.
	 */
	public void setBelongsToUser ( final boolean belongsToUser ) {
		this.belongsToUser = belongsToUser;
	}
	
	/**
	 * Getter - {@link #belongsToUser}
	 * 
	 * @return	Flag used to indicate if current client belongs to the user.
	 */
	public boolean getBelongsToUser () {
		return belongsToUser;
	}
	
	/**
	 * Getter - {@link #client}
	 * 
	 * @return {@link me.SyncWise.Android.Database.Clients Clients} object holding a reference to the client.
	 */
	public Clients getClient () {
		return client;
	}
	
	/**
	 * Getter - {@link #clientDivisions}
	 * 
	 * @return List {@link me.SyncWise.Android.Database.Divisions Divisions} object holding references to the client's divisions.
	 */
	public List < Divisions > getClientDivisions () {
		return clientDivisions;
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
		// Set the activity content from a layout resource.
		setContentView ( R.layout.client_info_activity );
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.client_info_activity_title ) );
		// Apply the view pager indicator theme
		setTheme ( R.style.Theme_PageIndicatorDefaults );
		
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state 
			client = (Clients) savedInstanceState.getSerializable ( CLIENT );
			clientInfo = (ArrayList < ClientInfo >) savedInstanceState.getSerializable ( CLIENT_INFO );
			
			// Check if there are saved client contacts
			if ( savedInstanceState.getBoolean ( CLIENT_CONTACTS ) )
				// Retrieve client contacts
			clientContacts = (ArrayList < ClientContacts >) ActivityInstance.readDataGson ( ClientInfoActivity.this , ClientInfoActivity.class.getName () , CLIENT_CONTACTS , new TypeToken < ArrayList < ClientContacts > > () {}.getType () );
			
			// Check if there are saved calendar slots
			if ( savedInstanceState.getBoolean ( CALENDAR_SLOTS ) )
				// Retrieve calendar slots
				calendarSlots = (HashMap < Integer , ArrayList < AvailabilityCalendar >>) ActivityInstance.readDataGson ( ClientInfoActivity.this , ClientInfoActivity.class.getName () , CALENDAR_SLOTS , new TypeToken < HashMap < Integer , ArrayList < AvailabilityCalendar > > > () {}.getType () );
		} // End if
		
		// Check if the client code is valid
		if ( getIntent ().getStringExtra ( CLIENT_CODE ) == null || getIntent ().getStringExtra ( COMPANY_CODE ) == null ) {
			// Indicate that the activity cannot be displayed
			new AppToast ( this ).setIcon ( R.drawable.warning ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.invalid_selection_label ) ).show ();
			// Finish activity
			finish ();
		} // End if
		
		// Retrieve the client company code
		String clientCompanyCode = getIntent ().getStringExtra ( COMPANY_CODE );
		// Retrieve the client division
		clientDivisions = new ArrayList < Divisions > (); 
		clientDivisions.addAll ( DatabaseUtils.getInstance ( ClientInfoActivity.this ).getDaoSession ().getDivisionsDao ()
				.queryRaw ( " WHERE DivisionCode = ( SELECT DivisionCode FROM ClientDivisions WHERE ClientCode = ? AND CompanyCode = ? ) AND CompanyCode = ? " ,
						new String [] { getIntent ().getStringExtra ( CLIENT_CODE ) , clientCompanyCode , clientCompanyCode } ) );
		
		// Retrieve the user company code
		String userCompanyCode = DatabaseUtils.getCurrentCompanyCode ( this );
		// Retrieve all divisions
		List < Divisions > allDivisions = DatabaseUtils.getInstance ( this ).getDaoSession ().getDivisionsDao ().queryBuilder ()
				.where ( DivisionsDao.Properties.CompanyCode.eq ( userCompanyCode ) ).list ();
		// Retrieve the divisions linked to the user
		List < Divisions > directUserDivisions = DatabaseUtils.getInstance ( this ).getDaoSession ().getDivisionsDao ()
				.queryRaw ( " WHERE CompanyCode = ? AND DivisionCode IN ( SELECT DivisionCode FROM UserDivisions WHERE CompanyCode = ? ) " ,
						new String [] { userCompanyCode , userCompanyCode } );
		// Retrieve the user children division
		List < Divisions > allUserDivisions = DivisionsUtils.getChildren ( directUserDivisions , allDivisions );
		// Add the direct user divisions to the main list
		allUserDivisions.addAll ( directUserDivisions );
		// Check if the division of the current client belongs to the user
		belongsToUser = false;
		// Make sure the client has at least one division
		if ( ! clientDivisions.isEmpty () )
			// Iterate over all user divisions
			for ( Divisions division : allUserDivisions )
				// Check if the current division is the client's division
				if ( division.getDivisionCode ().equals ( clientDivisions.get ( 0 ).getDivisionCode () ) && division.getCompanyCode ().equals ( clientCompanyCode ) ) {
					// Set flag
					belongsToUser = true;
					// Exit loop
					break;
				} // End if
		
		// Retrieve a reference to the indicator
		TabPageIndicator indicator = (TabPageIndicator) findViewById ( R.id.indicator );
		// Retrieve a reference to the view pager
		ViewPager viewPager = (ViewPager) findViewById ( R.id.pager );
		
		// Set the fragment pager adapter
		viewPager.setAdapter ( getAppFragmentPagerAdapter () );
		// Link the view pager to the indicator
		indicator.setViewPager ( viewPager );
		
		// Check if there is at least two fragments
		if ( fragments.size () < 2 )
			// Hide the tab indicator
			indicator.setVisibility ( View.GONE );
		
		// Check if any of the client data is NOT valid
		if ( populateList == null 
				&& ( client == null 
					|| clientInfo == null 
					|| clientContacts == null 
					|| calendarSlots == null ) ) {
			// Retrieve the client info asynchronously
			populateList = new PopulateList ();
			populateList.execute ();
		} // End if
		else {
			// Initialize the client card
			ClientCard.initializeClientCard ( this , client );
		} // End else
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
		// Remove any displayed baguette
		Baguette.remove ( this );
	}
	
    /*
     * Called to retrieve per-instance state from an activity before being killed so that the state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle).
     *
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState ( Bundle outState ) {
    	// Superclass method call
    	super.onSaveInstanceState ( outState );
    	// Save the content of client in the outState bundle
    	outState.putSerializable ( CLIENT , client );
    	// Save the content of clientInfo in the outState bundle
    	outState.putSerializable ( CLIENT_INFO , clientInfo );
    	
		// Save the content of the clientContacts using GSON
		ActivityInstance.saveDataGson ( this , ClientInfoActivity.class.getName () , CLIENT_CONTACTS , clientContacts );
		// Indicate that there is saved clientContacts data
		outState.putBoolean ( CLIENT_CONTACTS , true );
    	
		// Save the content of the calendarSlots using GSON
		ActivityInstance.saveDataGson ( this , ClientInfoActivity.class.getName () , CALENDAR_SLOTS , calendarSlots );
		// Indicate that there is saved calendarSlots data
		outState.putBoolean ( CALENDAR_SLOTS , true );
    }
	
	/*
	 * Called when the activity has detected the user's press of the back key. 
	 *
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed () {
		// Determine if there are modifications
		if ( ! hasModifications () )
			// Superclass method call
			super.onBackPressed ();
		else
			// Otherwise, there is at least one modification
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
								// Finish activity
								finish ();
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
			fragments = null;
			client = null;
			clientDivisions = null;
			clientInfo = null;
			clientContacts = null;
			calendarSlots = null;
		} // End if
	}
	
	/**
	 * Gets a new fragment pager adapter used to display multiple client information via a view pager.
	 * Gets by default all the fragments combinations.<br>
	 * Override this method if a other / modified class is needed.<br>
	 * <b>Note : </b> This method should never return {@code NULL}.<br>
	 * <b>Note : </b> This method should properly initialize the {@link #fragments} list !!
	 * 
	 * @return	{@link me.SyncWise.Android.AppFragmentPagerAdapter AppFragmentPagerAdapter} that represents each page as a Fragment that is persistently kept in the fragment manager as long as the user can return to the page.
	 */
	protected AppFragmentPagerAdapter getAppFragmentPagerAdapter () {
		// Declare and initialize a list used to host the fragments descriptions
		fragments = new ArrayList < AppFragment > ();
		// Define the required fragments
		fragments.add ( new AppFragment ( AppResources.getInstance ( this ).getString ( this , R.string.information_label ).toUpperCase () , R.layout.client_info_fragment ) );
		// Initialize and return a fragment pager adapter using the fragments list above and the appropriate array of fragment classes
		return new AppFragmentPagerAdapter ( getSupportFragmentManager () , fragments , new Class < ? > [] { ClientInfoFragment.class } );
	}
	
	/**
	 * Indicates whether the client info activity has new / unsaved modifications.
	 * 
	 * @return	Boolean stating if there are new modifications.
	 */
	private boolean hasModifications () {
		// Check the following :
		// - Modifications in the calendar slots
		return isCalendarSlotsModified ();
	}
	
	/**
	 * Determines whether or not the calendar has been modified.
	 * 
	 * @return	Boolean indicating if the calendar has been modified or not.
	 */
	public boolean isCalendarSlotsModified () {
		// Check if the availability slots map is valid
		if ( calendarSlots == null )
			// Invalid map
			return false;
		// Otherwise the map is valid, and should contain 7 lists mapped to their appropriate date indexes
		// Iterate over the availability lists
		for ( Integer day : calendarSlots.keySet () )
			// Iterate over the availability slots
			for ( AvailabilityCalendar slot : calendarSlots.get ( day ) )
				// Check if the current slot is modified
				if ( slot.isModified () )
					// Indicate that the current calendar is modified
					return true;
		// Otherwise the calendar is not modified
		return false;
	}
	
	/**
	 * Gets the list adapter for the client information list, or {@code NULL} if client info is invalid.
	 * 
	 * @param clientInfo	List of {@link me.SyncWise.Android.Database.Clients Clients} objects used to host the client personal information.
	 * @param clientContacts	List of {@link me.SyncWise.Android.Database.ClientContacts ClientContacts} objects used to host the client's contacts.
	 * @return	List Adapter for the client information list, or {@code NULL} if the client info is invalid. 
	 */
	public MultipleListAdapter getClientInfoAdapter ( final ArrayList < ClientInfo > clientInfo , final ArrayList < ClientContacts > clientContacts ) {
		// Check if the client info list and client contacts list are both valid
		if ( ( this.clientInfo == null && clientInfo == null ) 
				|| ( this.clientContacts == null && clientContacts == null ) )
			// Invalid data
			return null;
		// Check if a client info list is provided
		if ( clientInfo != null )
			// Initialize the client info list
			this.clientInfo = clientInfo;
		// Check if a client contacts list is provided
		if ( clientContacts != null )
			// Initialize the client contacts list
			this.clientContacts = clientContacts;
		
		// Declare and initialize a multiple list adapter
		MultipleListAdapter adapter = new MultipleListAdapter ( this );
		// Check if the client info list is valid
		if ( ! this.clientInfo.isEmpty () )
			// Add a client info section
			adapter.addSection ( new Section ( AppResources.getInstance ( this ).getString ( this , R.string.client_info_activity_title ) , null , BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
					new ClientInfoAdapter ( this , R.layout.client_info_fragment_item , this.clientInfo ) );
		return adapter;
	}
	
	/**
	 * AsyncTask helper class used to populate the client information.
	 * 
	 * @author Elias
	 *
	 */
	@SuppressLint("SimpleDateFormat") 
	private class PopulateList extends AsyncTask < Void , Void , Void > {
		
		/**
		 * List of {@link me.SyncWise.Android.Database.Clients Clients} objects used to host the client personal information.
		 */
		private ArrayList < ClientInfo > clientInfo;
		
		/**
		 * List of {@link me.SyncWise.Android.Database.ClientContacts ClientContacts} objects used to host the client's contacts.
		 */
		private ArrayList < ClientContacts > clientContacts;
		
		/**
		 * Map of {@link me.SyncWise.Android.Widgets.AvailabilityCalendar.AvailabilityCalendar AvailabilityCalendar} objects hosting the availability slots of the current availability calendar view.<br>
		 * The lists are mapped to the appropriate day number based on the {@link java.util.Calendar Calendar} class.
		 */
		private HashMap < Integer , ArrayList < AvailabilityCalendar > > calendarSlots;
		
		/**
		 * List of {@link me.SyncWise.Android.Modules.ClientsList.ClientInfo.ClientItemClassifications ClientItemClassifications} objects used to host the client items classification.
		 */
		private ArrayList < ClientItemClassifications > clientItemClassifications;
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground ( Void ... params ) {
			// Retrieve the client using the provided client code
			client = DatabaseUtils.getInstance ( ClientInfoActivity.this ).getDaoSession ().getClientsDao ().queryBuilder ()
					.where ( ClientsDao.Properties.ClientCode.eq ( getIntent ().getStringExtra ( CLIENT_CODE ) ) ,
							ClientsDao.Properties.CompanyCode.eq ( getIntent ().getStringExtra ( COMPANY_CODE ) ) ).unique ();
			// Check if the client object is valid
			if ( client == null )
				// Exit method
				return null;
			
			// Declare and initialize a cursor object used to retrieve data sets from query results
			Cursor cursor = null;
			// Declare the SQL string and selection arguments array of strings used to to query DB
			String SQL = null;
			String selectionArguments [] = null;
			
			// Get the client currencies
			List < ClientCurrencies > clientCurrencies = DatabaseUtils.getInstance ( ClientInfoActivity.this ).getDaoSession ().getClientCurrenciesDao ().queryBuilder ()
					.where ( ClientCurrenciesDao.Properties.ClientCode.eq ( getIntent ().getStringExtra ( CLIENT_CODE ) ) ).list ();
			// Map the client currencies to their currency codes
			final Map < String , ClientCurrencies > _clientCurrencies = new HashMap < String , ClientCurrencies > ();
			// Retrieve a list of the currency codes
			List < String > currencyCodes = new ArrayList < String > ();
			// Iterate over all client currencies
			for ( ClientCurrencies clientCurrency : clientCurrencies ) {
				// Add all the current currency code
				currencyCodes.add ( clientCurrency.getCurrencyCode () );
				// Map the the current client currency to its currency code
				_clientCurrencies.put ( clientCurrency.getCurrencyCode () , clientCurrency );
			} // End for each
			// Retrieve the currencies
			List < Currencies > currencies = DatabaseUtils.getInstance ( ClientInfoActivity.this ).getDaoSession ().getCurrenciesDao ().queryBuilder ()
					.where ( CurrenciesDao.Properties.CurrencyCode.in ( currencyCodes ) ).list ();
			// Sort the currencies according to their priorities
			Collections.sort ( currencies , new Comparator < Currencies > () {
				@Override
				public int compare ( Currencies currency1 , Currencies currency2 ) {
					// Sort the currencies based on their priorities
					return _clientCurrencies.get ( currency1.getCurrencyCode () ).getPriority () - _clientCurrencies.get ( currency2.getCurrencyCode () ).getPriority ();
				}
			} );
			// Get the parent client object
			Clients parent = null;
			if ( client.getParentCode () != null )
				parent = DatabaseUtils.getInstance ( ClientInfoActivity.this ).getDaoSession ().getClientsDao ().queryBuilder ()
					.where ( ClientsDao.Properties.ClientCode.eq ( client.getParentCode () ) ).unique ();
			// Initialize the client contacts list
			clientContacts = new ArrayList < ClientContacts > ();
			// Retrieve the related client contacts
			clientContacts.addAll ( DatabaseUtils.getInstance ( ClientInfoActivity.this ).getDaoSession ().getClientContactsDao ().queryBuilder ()
					.where ( ClientContactsDao.Properties.ClientCode.eq ( client.getClientCode () ) ).list () );
			
			// Retrieve the client info labels
			String statusLabel = AppResources.getInstance ( ClientInfoActivity.this ).getString ( ClientInfoActivity.this , R.string.status_label );
			String companyLabel = AppResources.getInstance ( ClientInfoActivity.this ).getString ( ClientInfoActivity.this , R.string.company_label );
			String balanceLabel = AppResources.getInstance ( ClientInfoActivity.this ).getString ( ClientInfoActivity.this , R.string.balance_label );
			String divisionLabel = AppResources.getInstance ( ClientInfoActivity.this ).getString ( ClientInfoActivity.this , R.string.division_label );
			String typeLabel = AppResources.getInstance ( ClientInfoActivity.this ).getString ( ClientInfoActivity.this , R.string.type_label );
			String cashLabel = AppResources.getInstance ( ClientInfoActivity.this ).getString ( ClientInfoActivity.this , R.string.cash_label );
			String creditLabel = AppResources.getInstance ( ClientInfoActivity.this ).getString ( ClientInfoActivity.this , R.string.credit_label );
			String phoneLabel = AppResources.getInstance ( ClientInfoActivity.this ).getString ( ClientInfoActivity.this , R.string.phone_label );
			String faxLabel = AppResources.getInstance ( ClientInfoActivity.this ).getString ( ClientInfoActivity.this , R.string.fax_label );
			String emailLabel = AppResources.getInstance ( ClientInfoActivity.this ).getString ( ClientInfoActivity.this , R.string.email_label );
			String addressLabel = AppResources.getInstance ( ClientInfoActivity.this ).getString ( ClientInfoActivity.this , R.string.address_label );
			String birthdateLabel = AppResources.getInstance ( ClientInfoActivity.this ).getString ( ClientInfoActivity.this , R.string.date_of_birth_label );
			String ageLabel = AppResources.getInstance ( ClientInfoActivity.this ).getString ( ClientInfoActivity.this , R.string.age_label );
			String yearsOldLabel = AppResources.getInstance ( ClientInfoActivity.this ).getString ( ClientInfoActivity.this , R.string.years_old_label );
			String currencyLabel = AppResources.getInstance ( ClientInfoActivity.this ).getString ( ClientInfoActivity.this , R.string.currency_label );
			String parentClientLabel = AppResources.getInstance ( ClientInfoActivity.this ).getString ( ClientInfoActivity.this , R.string.parent_client_label );
			String vatNumberLabel = AppResources.getInstance ( ClientInfoActivity.this ).getString ( ClientInfoActivity.this , R.string.vat_number_label );
			String naLabel = AppResources.getInstance ( ClientInfoActivity.this ).getString ( ClientInfoActivity.this , R.string.not_available_abbreviation );
			String activeLabel = AppResources.getInstance ( ClientInfoActivity.this ).getString ( ClientInfoActivity.this , R.string.active_label );
			String inactiveLabel = AppResources.getInstance ( ClientInfoActivity.this ).getString ( ClientInfoActivity.this , R.string.inactive_label );
			String totalOustanding= AppResources.getInstance ( ClientInfoActivity.this ).getString ( ClientInfoActivity.this , R.string.total_oustanding );
			String totalOverdueAmount = AppResources.getInstance ( ClientInfoActivity.this ).getString ( ClientInfoActivity.this , R.string.total_overdue_amount );
			String totalCreditLimitAmount = AppResources.getInstance ( ClientInfoActivity.this ).getString ( ClientInfoActivity.this , R.string.total_credit_limit_amount );
			
			
			
			
			
			
			// Initialize the client info list
			clientInfo = new ArrayList < ClientInfo > ();
			// Display the client status
			String clientStatus = naLabel;
			if ( client.getClientStatus () == StatusUtils.isActive () )
				clientStatus = activeLabel;
			else if ( client.getClientStatus () == StatusUtils.isInactive () || client.getClientStatus () == StatusUtils.isBlocked () )
				clientStatus = inactiveLabel;
			clientInfo.add ( new ClientInfo ( ClientInfo.ID.STATUS , statusLabel , clientStatus ) );
			// Display the client company
			clientInfo.add ( new ClientInfo ( companyLabel , DatabaseUtils.getInstance ( ClientInfoActivity.this ).getDaoSession ().getCompaniesDao ().queryBuilder ()
					.where ( CompaniesDao.Properties.CompanyCode.eq ( client.getCompanyCode () ) ).unique ().getCompanyCode () ) );
			// Display the client division
			clientInfo.add ( new ClientInfo ( divisionLabel , clientDivisions == null || clientDivisions.isEmpty () ? naLabel : clientDivisions.get ( 0 ).getDivisionName () ) );
			// Display the client type
			String clientTypeLabel = naLabel;
			if ( ClientCard.isCashClient ( client ) )
				clientTypeLabel = cashLabel;
			else if ( ClientCard.isCreditClient ( client ) )
				clientTypeLabel = creditLabel;
			clientInfo.add ( new ClientInfo ( typeLabel , clientTypeLabel ) );
			// Determine if the client has a phone number
			if ( ! TextUtils.isEmpty ( client.getClientPhone () ) )
				clientInfo.add ( new ClientInfo ( ClientInfo.ID.PHONE , phoneLabel , client.getClientPhone () ) );
			// Determine if the client has a fax number
			if ( ! TextUtils.isEmpty ( client.getClientFax () ) )
				clientInfo.add ( new ClientInfo ( faxLabel , client.getClientFax () ) );
			// Determine if the client has an email
			if ( ! TextUtils.isEmpty ( client.getClientEmail () ) )
				clientInfo.add ( new ClientInfo ( ClientInfo.ID.EMAIL , emailLabel , client.getClientEmail () ) );
			// Determine if the client has an address
			if ( ! TextUtils.isEmpty ( client.getClientAddress () ) )
				clientInfo.add ( new ClientInfo ( addressLabel , client.getClientAddress () ) );
			// Determine if the client has a date of birth
			if ( ! TextUtils.isEmpty ( client.getDateOfBirth () ) ) {
				clientInfo.add ( new ClientInfo ( birthdateLabel , client.getDateOfBirth () ) );
				try {
					// Retrieve the birth date
					Calendar birthDate = Calendar.getInstance ();
					birthDate.setTimeInMillis ( new SimpleDateFormat ( ClientCard.BIRTH_DATE_FORMAT ).parse ( client.getDateOfBirth () ).getTime () );
					// Retrieve the current date
					Calendar today = Calendar.getInstance ();
					// Compute the client's age
					int age = today.get ( Calendar.YEAR ) - birthDate.get ( Calendar.YEAR );
					if ( birthDate.get ( Calendar.MONTH ) > today.get ( Calendar.MONTH ) || 
						( birthDate.get ( Calendar.MONTH ) == today.get ( Calendar.MONTH ) && birthDate.get ( Calendar.DATE ) > today.get ( Calendar.DATE ) ) ) {
						age --;
					} // End if
					// Determine if the age is valid
					if ( age > 2 )
						clientInfo.add ( new ClientInfo ( ageLabel , age + " " + yearsOldLabel ) );
				} catch ( Exception exception ) {
					// Invalid date format, do nothing
				} // End of try-catch block
			} // End if
			// Determine if the client has at least one currency
			if ( ! currencies.isEmpty () )
				for ( Currencies currency : currencies )
					clientInfo.add ( new ClientInfo ( currencyLabel , currency.getCurrencyName () ) );
			// Determine if the client has a parent client
			if ( parent != null )
				clientInfo.add ( new ClientInfo ( ClientInfo.ID.PARENT_CLIENT , parentClientLabel , parent.getClientName () ) );
			// Determine if the client has a vat number
			if ( ! TextUtils.isEmpty ( client.getClientVATNumber () ) )
				clientInfo.add ( new ClientInfo ( ClientInfo.ID.VAT_NUMBER , vatNumberLabel , client.getClientVATNumber () ) );
			DecimalFormat moneyFormat = new DecimalFormat ( "#,##0.000" );
			// Display the client balance
			if ( ClientCard.isCreditClient( client ) )
			{
			List< ClientCreditings >  c= new ArrayList < ClientCreditings >();
				c=	DatabaseUtils.getInstance ( ClientInfoActivity.this ).getDaoSession ().getClientCreditingsDao().queryBuilder ()
						 .where ( ClientCreditingsDao.Properties.ClientCode.eq ( client.getClientCode() )).list();
				if(c.size()>0)
				{ 
					clientInfo.add ( new ClientInfo ( balanceLabel ,moneyFormat.format(c.get(0).getCreditBalance()).toString() ) );
					clientInfo.add ( new ClientInfo ( totalCreditLimitAmount ,moneyFormat.format(c.get(0).getCreditAmount()).toString() ) );
				}else
				{
					clientInfo.add ( new ClientInfo ( balanceLabel ,"0"));
					clientInfo.add ( new ClientInfo ( totalCreditLimitAmount ,"0"));
				}
			
				List< ClientDues >  clientDues= new ArrayList < ClientDues >();
				clientDues=	DatabaseUtils.getInstance ( ClientInfoActivity.this ).getDaoSession ().getClientDuesDao().queryBuilder ()
						 .where ( ClientDuesDao.Properties.ClientCode.eq ( client.getClientCode() )).list();
				Double remainAmount = 0d;
				Date today = Calendar.getInstance ().getTime ();
				
				Double overduesAmount = 0d;
				for(ClientDues cd:clientDues)
				{
				 	remainAmount=remainAmount + ( cd.getInvoiceType() == 1 ? cd.getRemainingAmount() : -1 * cd.getRemainingAmount() );
				 	if( cd.getDueDate ().before ( today ) && cd.getRemainingAmount () > 0 )
				 		overduesAmount = overduesAmount + ( cd.getInvoiceType() == 1 ? cd.getRemainingAmount() : -1 * cd.getRemainingAmount() );
				}
				
				if( clientDues.size() > 0 )
				{	
					clientInfo.add ( new ClientInfo ( totalOustanding ,moneyFormat.format(remainAmount).toString()) );
					clientInfo.add ( new ClientInfo ( totalOverdueAmount ,moneyFormat.format(overduesAmount).toString()) );
					
				}else
					{
					clientInfo.add ( new ClientInfo ( totalOustanding ,"0"));
					clientInfo.add ( new ClientInfo ( totalOverdueAmount ,"0"));
					}
			}// Get the client properties
			List < ClientProperties > _clientProperties = DatabaseUtils.getInstance ( ClientInfoActivity.this ).getDaoSession ().getClientPropertiesDao ().queryBuilder ()
					.where ( ClientPropertiesDao.Properties.ClientCode.eq ( client.getClientCode () ) ).list ();
			// Check if the client properties list is valid
			if ( ! _clientProperties.isEmpty () ) {
				// Retrieve the client property value codes list
				List < String > clientPropertyValueCodes = new ArrayList < String > ();
				// Iterate over all client properties
				for ( ClientProperties clientProperty : _clientProperties )
					// Store the current client property value code
					clientPropertyValueCodes.add ( clientProperty.getClientPropertyValueCode () );
				// Retrieve the corresponding client property values
				List < ClientPropertyValues > clientPropertyValues = DatabaseUtils.getInstance ( ClientInfoActivity.this ).getDaoSession ().getClientPropertyValuesDao ().queryBuilder ()
						.where ( ClientPropertyValuesDao.Properties.ClientPropertyValueCode.in ( clientPropertyValueCodes ) ).list ();
				// Map the client property values to their level codes
				Map < String , ClientPropertyValues > _clientPropertyValues = new HashMap < String , ClientPropertyValues > ();
				// Iterate over all client property values
				for ( ClientPropertyValues clientPropertyValue : clientPropertyValues )
					// Map the current client property value to its level code
					_clientPropertyValues.put ( clientPropertyValue.getClientPropertyLevelCode () , clientPropertyValue );
				// Retrieve the corresponding client property levels
				List < ClientPropertyLevels > clientPropertyLevels = DatabaseUtils.getInstance ( ClientInfoActivity.this ).getDaoSession ().getClientPropertyLevelsDao ().queryBuilder ()
						.where ( ClientPropertyLevelsDao.Properties.ClientPropertyLevelCode.in ( _clientPropertyValues.keySet () ) ).list ();
				
				// Iterate over all client property levels
				for ( ClientPropertyLevels clientPropertyLevel : clientPropertyLevels )
					// Check if the current level has a value for the current client
					if ( _clientPropertyValues.containsKey ( clientPropertyLevel.getClientPropertyLevelCode () ) )
						// Add the current level value pair to the client info list
						clientInfo.add ( new ClientInfo ( clientPropertyLevel.getClientPropertyLevelDescription () ,
								_clientPropertyValues.get ( clientPropertyLevel.getClientPropertyLevelCode () ).getClientPropertyValueDescription () ) );
			} // End if
			
			// Get the client areas
			List < ClientAreas > _clientAreas = DatabaseUtils.getInstance ( ClientInfoActivity.this ).getDaoSession ().getClientAreasDao ().queryBuilder ()
					.where ( ClientAreasDao.Properties.ClientCode.eq ( client.getClientCode () ) ).list ();
			// Check if the client areas list is valid
			if ( ! _clientAreas.isEmpty () ) {
				// Retrieve the area codes list
				List < String > clientAreaCodes = new ArrayList < String > ();
				// Iterate over all client areas
				for ( ClientAreas clientArea : _clientAreas )
					// Store the current area code
					clientAreaCodes.add ( clientArea.getAreaCode () );
				// Retrieve the corresponding areas
				List < Areas > areas = DatabaseUtils.getInstance ( ClientInfoActivity.this ).getDaoSession ().getAreasDao ().queryBuilder ()
						.where ( AreasDao.Properties.AreaCode.in ( clientAreaCodes ) ).list ();
				// Map the areas to their area level codes
				Map < String , Areas > _areas = new HashMap < String , Areas > ();
				// Iterate over all areas
				for ( Areas area : areas )
					// Map the current area to its code
					_areas.put ( area.getAreaLevelCode () , area );
				// Retrieve the corresponding area levels
				List < AreaLevels > areaLevels = DatabaseUtils.getInstance ( ClientInfoActivity.this ).getDaoSession ().getAreaLevelsDao ().queryBuilder ()
						.where ( AreaLevelsDao.Properties.AreaLevelCode.in ( _areas.keySet () ) )
						.orderAsc ( AreaLevelsDao.Properties.Sequence ).list ();
				
				// Iterate over all area levels
				for ( AreaLevels areaLevel : areaLevels )
					// Check if the current level has a value for the current client
					if ( _areas.containsKey ( areaLevel.getAreaLevelCode () ) )
						// Add the current level value pair to the client info list
						clientInfo.add ( new ClientInfo ( areaLevel.getAreaLevelDescription () ,
								_areas.get ( areaLevel.getAreaLevelCode () ).getAreaDescription () ) );
			} // End if
			
			// Retrieve all the client availabilities for the current client that are NOT deleted
			List < ClientAvailabilities > clientAvailabilities = DatabaseUtils.getInstance ( ClientInfoActivity.this ).getDaoSession ().getClientAvailabilitiesDao ().queryBuilder ()
				.where ( ClientAvailabilitiesDao.Properties.ClientCode.eq ( client.getClientCode () ) ,
						ClientAvailabilitiesDao.Properties.AvailabilityStatus.eq ( StatusUtils.isAvailable () ) ).list ();
			// Declare and initialize an availability calendar view in order to make internal computations and hence build the calendar slots list
			calendarSlots = new AvailabilityCalendarView ( ClientInfoActivity.this , null , clientAvailabilities ).getCalendarSlots ();
			
			// Compute the SQL string
			SQL = "SELECT I." + ItemsDao.Properties.ItemCode.columnName + " , I." + ItemsDao.Properties.ItemName.columnName + " , " +
					"CL." + ClientItemClassificationLevelsDao.Properties.ClassificationLevelCode.columnName + " , " +
					"CL." + ClientItemClassificationLevelsDao.Properties.ClassificationLevelDescription.columnName + " " +
					"FROM ( " + ClientItemClassificationsDao.TABLENAME + " C LEFT JOIN " + ClientItemClassificationLevelsDao.TABLENAME + " CL " +
					"ON C. " + ClientItemClassificationsDao.Properties.ClassificationLevelCode.columnName + 
					" = CL." + ClientItemClassificationLevelsDao.Properties.ClassificationLevelCode.columnName + " ) LEFT JOIN " + 
					ItemsDao.TABLENAME + " I ON C." + ClientItemClassificationsDao.Properties.ItemCode.columnName + " = I." + ItemsDao.Properties.ItemCode.columnName +
					" WHERE C." + ClientItemClassificationsDao.Properties.ClientCode.columnName + "=?";
			
			// Compute the selection arguments
			selectionArguments = new String [] { client.getClientCode () };
			
			// Query DB in order to retrieve the client item classifications
			cursor = DatabaseUtils.getInstance ( ClientInfoActivity.this ).getDaoSession ().getDatabase ()
					.rawQuery ( SQL , selectionArguments );
			// Initialize the client item classifications
			clientItemClassifications = new ArrayList < ClientItemClassifications > ();
			// Move the cursor to the first row
			if ( cursor.moveToFirst () == true ) {
				do {
					// Retrieve the current client item classification and add it to the list
					clientItemClassifications.add ( new ClientItemClassifications ( 
							cursor.getString ( cursor.getColumnIndex ( ItemsDao.Properties.ItemCode.columnName ) ) ,
							cursor.getString ( cursor.getColumnIndex ( ItemsDao.Properties.ItemName.columnName ) ) ,
							cursor.getString ( cursor.getColumnIndex ( ClientItemClassificationLevelsDao.Properties.ClassificationLevelCode.columnName ) ) ,
							cursor.getString ( cursor.getColumnIndex ( ClientItemClassificationLevelsDao.Properties.ClassificationLevelDescription.columnName ) ) ) );
				} while ( cursor.moveToNext () );
			} // End if
			// Close the cursor
			cursor.close ();
			cursor = null;
			
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
		protected void onPostExecute ( Void arg ) {
			try {
				// Initialize the client card
				ClientCard.initializeClientCard ( ClientInfoActivity.this , client );
				
    			// Check if the calendar slots map is valid
    			if ( getCalendarSlots () == null )
    				// Initialize the calendar slots map
    				setCalendarSlots ( calendarSlots );
				
	    		// Retrieve a reference of the view pager (which has the app fragment pager adapter linked to)
	    		// The view pager is used to retrieve a reference to the daily fragment, via its tag
	    		ViewPager viewPager = (ViewPager) findViewById ( R.id.pager );
	    		// Retrieve a reference to the app fragment pager adapter
	    		// This adapter can compute the fragment tag so it can be referenced (via its tag)
	    		AppFragmentPagerAdapter adapter = viewPager != null ? (AppFragmentPagerAdapter) viewPager.getAdapter () : null;
				
				// Iterate over all fragments
				for ( int i = 0 ; i < ClientInfoActivity.this.fragments.size () ; i ++ ) {
		    		// Compute the section (having index i) tag
		    		String tag = adapter != null ? adapter.getFragmentName ( viewPager , i ) : null;
		    		// Retrieve a reference to the fragment based on its tag
		    		Object fragment = getSupportFragmentManager ().findFragmentByTag ( tag == null ? "" : tag );
		    		// Check if the fragment is valid
		    		if ( fragment == null )
		    			// Invalid fragment, skip it
		    			continue;
		    		
		    		// Determine if the fragment is Client Info
		    		if ( fragment instanceof ClientInfoFragment )
		    			// Set the client info adapter
		    			( (ClientInfoFragment) fragment ).setListAdapter ( getClientInfoAdapter ( clientInfo , clientContacts ) );
		    		else if ( fragment instanceof ClientAvailabilityFragment )
		    			// Set the availability calendar view and store the used calendar slots map
		    			setCalendarSlots ( ( (ClientAvailabilityFragment) fragment ).setAvailabilityCalendarView ( getCalendarSlots () , null ) );
		    		
				} // End for loop
			} catch ( Exception exception ) {
				// Do nothing
			} finally {
				// Clear variables
				this.clientContacts = null;
				this.clientInfo = null;
				this.clientItemClassifications = null;
			} // End of try-catch block
		}
		
	}
	
}