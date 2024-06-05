/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Target;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import me.SyncWise.Android.AppFragmentPagerAdapter;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.ClientsDao;
import me.SyncWise.Android.Database.CurrenciesDao;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.DivisionsDao;
import me.SyncWise.Android.Database.ItemsDao;
import me.SyncWise.Android.Database.TargetAchievementsDao;
import me.SyncWise.Android.Database.TargetAssignmentsDao;
import me.SyncWise.Android.Database.TargetAssignmentsUtils;
import me.SyncWise.Android.Database.TargetDetailsDao;
import me.SyncWise.Android.Database.TargetHeadersDao;
import me.SyncWise.Android.Database.TargetHeadersUtils;
import me.SyncWise.Android.Database.UnitsDao;
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Gson.BaseTimerFragmentActivity;
import me.SyncWise.Android.Modules.Journey.Call;
import me.SyncWise.Android.Utilities.ActivityInstance;
import me.SyncWise.Android.Utilities.AppFragment;
import me.SyncWise.Android.Widgets.ViewPagerIndicator.TabPageIndicator;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

/**
 * Activity implemented to display target coverage information.
 * 
 * @author Elias
 * @sw.todo	<b>Target Activity Implementation :</b><br>
 * <ul>
 * <li>If you want the default implementation, simply add this class in the {@code AndroidManifest.xml} file.<br>
 * The default behavior <em>should</em> display in this activity only the basic target coverage related to the visits number <em>({@link me.SyncWise.Android.Modules.Target.TargetVisits TargetVisits})</em>.</li>
 * </ul>
 * <br><em><h1>OR</h1></em><br>
 * In order to customize the module, please follow the steps below :
 * <ul>
 * <li>Extend this class.</li>
 * <li>Override the {@link #getAppFragmentPagerAdapter()} method in order to define what client data to display <em>(via the various fragments made for this activity)</em>.</li>
 * <li>Do not forget to add your new class in the {@code AndroidManifest.xml} file.</li>
 * </ul>
 * 
 */
public class TargetActivity extends BaseTimerFragmentActivity {

	/**
	 * Bundle key used to put/retrieve the content of the current call.
	 */
	public static final String CALL = TargetActivity.class.getName () + ".CALL";
	
	/**
	 * List used to host the fragments descriptions.
	 */
	protected ArrayList < AppFragment > fragments;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #targetVisits}.
	 */
	private static final String TARGET_VISITS = TargetActivity.class.getName () + ".TARGET_VISITS";
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.Target.TargetVisits TargetVisits} objects used to host the targeted clients' visits coverage.
	 */
	private ArrayList < TargetVisits > targetVisits;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #targetCollections}.
	 */
	private static final String TARGET_COLLECTION = TargetActivity.class.getName () + ".TARGET_COLLECTION";
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.Target.TargetCollections TargetCollections} objects used to host the targeted collection amounts coverage.
	 */
	private ArrayList < TargetCollections > targetCollections;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #targetOrders}.
	 */
	private static final String TARGET_ORDER = TargetActivity.class.getName () + ".TARGET_ORDER";
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.Target.TargetOrders TargetOrder} objects used to host the targeted order amounts coverage.
	 */
	private ArrayList < TargetOrders > targetOrders;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #targetOrdersPerClient}.
	 */
	private static final String TARGET_ORDER_PER_CLIENT = TargetActivity.class.getName () + ".TARGET_ORDER_PER_CLIENT";
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.Target.TargetOrdersPerClient TargetOrdersPerClient} objects used to host the targeted order amounts per client coverage.
	 */
	private ArrayList < TargetOrdersPerClient > targetOrdersPerClient;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #targetOrdersPerClientPerBrand}.
	 */
	private static final String TARGET_ORDER_PER_CLIENT_PER_BRAND = TargetActivity.class.getName () + ".TARGET_ORDER_PER_CLIENT_PER_BRAND";
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.Target.TargetOrdersPerClientPerBrand TargetOrdersPerClientPerBrand} objects used to host the targeted order amounts per client per brand coverage.
	 */
	private ArrayList < TargetOrdersPerClientPerBrand > targetOrdersPerClientPerBrand;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #targetItems}.
	 */
	private static final String TARGET_ITEMS = TargetActivity.class.getName () + ".TARGET_ITEMS";
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.Target.TargetItems TargetItems} objects used to host the targeted items' quantity coverage.
	 */
	private ArrayList < TargetItems > targetItems;
	
	/**
	 * Reference to the target coverage population task.
	 */
	private static PopulateList populateList;
	
	/**
	 * Setter - {@link #targetVisits}
	 * 
	 * @param targetVisits	List of {@link me.SyncWise.Android.Modules.Target.TargetVisits TargetVisits} objects used to host the targeted clients' visits coverage.
	 */
	public void setTargetVisits ( ArrayList < TargetVisits > targetVisits ) {
		this.targetVisits = targetVisits;
	}
	
	/**
	 * Setter - {@link #targetItems}
	 * 
	 * @param targetItems	List of {@link me.SyncWise.Android.Modules.Target.TargetItems TargetItems} objects used to host the targeted items' quantity coverage.
	 */
	public void setTargetItems ( ArrayList < TargetItems > targetItems ) {
		this.targetItems = targetItems;
	}
	
	/**
	 * Setter - {@link #targetOrders}
	 * 
	 * @param targetOrders	List of {@link me.SyncWise.Android.Modules.Target.TargetOrders TargetOrder} objects used to host the targeted order amounts coverage.
	 */
	public void setTargetOrders ( ArrayList < TargetOrders > targetOrders ) {
		this.targetOrders = targetOrders;
	}
	
	/**
	 * Setter - {@link #targetOrdersPerClient}
	 * 
	 * @param targetOrdersPerClient	List of {@link me.SyncWise.Android.Modules.Target.TargetOrdersPerClient TargetOrdersPerClient} objects used to host the targeted order amounts per client coverage.
	 */
	public void setTargetOrdersPerClient ( ArrayList < TargetOrdersPerClient > targetOrdersPerClient ) {
		this.targetOrdersPerClient = targetOrdersPerClient;
	}
	
	/**
	 * Setter - {@link #targetOrdersPerClientPerBrand}
	 * 
	 * @param targetOrdersPerClientPerBrand	List of {@link me.SyncWise.Android.Modules.Target.TargetOrdersPerClient TargetOrdersPerClient} objects used to host the targeted order amounts per client per brand coverage.
	 */
	public void setTargetOrdersPerClientPerBrand ( ArrayList < TargetOrdersPerClientPerBrand > targetOrdersPerClientPerBrand ) {
		this.targetOrdersPerClientPerBrand = targetOrdersPerClientPerBrand;
	}
	
	/**
	 * Setter - {@link #targetCollections}
	 * 
	 * @param targetCollections	List of {@link me.SyncWise.Android.Modules.Target.TargetCollections TargetCollections} objects used to host the targeted collection amounts coverage.
	 */
	public void setTargetCollections ( ArrayList < TargetCollections > targetCollections ) {
		this.targetCollections = targetCollections;
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
		setContentView ( R.layout.target_activity );
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.target_activity_title ) );
		// Apply the view pager indicator theme
		setTheme ( R.style.Theme_PageIndicatorDefaults );
		
		// Retrieve a reference to the indicator
		TabPageIndicator indicator = (TabPageIndicator) findViewById ( R.id.indicator );
		// Retrieve a reference to the view pager
		ViewPager viewPager = (ViewPager) findViewById ( R.id.pager );
		
		// Initialize the list used to host the fragments descriptions
		fragments = new ArrayList < AppFragment > ();
		// Define the required fragments
		fragments.add ( new AppFragment ( AppResources.getInstance ( this ).getString ( this , R.string.visits_frequency_activity_title ) , R.layout.visits_frequency_fragment ) );
		fragments.add ( new AppFragment ( AppResources.getInstance ( this ).getString ( this , R.string.items_quantity_activity_title ) , R.layout.items_quantity_fragment ) );
		fragments.add ( new AppFragment ( AppResources.getInstance ( this ).getString ( this , R.string.order_amount_activity_title ) , R.layout.order_amount_fragment ) );
		fragments.add ( new AppFragment ( AppResources.getInstance ( this ).getString ( this , R.string.collection_amount_activity_title ) , R.layout.collection_amount_fragment ) );
		// Set the fragment pager adapter
		viewPager.setAdapter ( getAppFragmentPagerAdapter () );
		// Link the view pager to the indicator
		indicator.setViewPager ( viewPager );
		
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Check if there are saved target visits
			if ( savedInstanceState.getBoolean ( TARGET_VISITS ) )
				// Retrieve target visits
				targetVisits = (ArrayList < TargetVisits >) ActivityInstance.readDataGson ( this , TargetActivity.class.getName () , TARGET_VISITS , new TypeToken < ArrayList < TargetVisits > > () {}.getType () );
			// Check if there are saved target items
			if ( savedInstanceState.getBoolean ( TARGET_ITEMS ) )
				// Retrieve target items
				targetItems = (ArrayList < TargetItems >) ActivityInstance.readDataGson ( this , TargetActivity.class.getName () , TARGET_ITEMS , new TypeToken < ArrayList < TargetItems > > () {}.getType () );
			// Check if there are saved target order
			if ( savedInstanceState.getBoolean ( TARGET_ORDER ) )
				// Retrieve target order
				targetOrders = (ArrayList < TargetOrders >) ActivityInstance.readDataGson ( this , TargetActivity.class.getName () , TARGET_ORDER , new TypeToken < ArrayList < TargetOrders > > () {}.getType () );
			// Check if there are saved target order
			if ( savedInstanceState.getBoolean ( TARGET_ORDER_PER_CLIENT ) )
				// Retrieve target order per client
				targetOrdersPerClient = (ArrayList < TargetOrdersPerClient >) ActivityInstance.readDataGson ( this , TargetActivity.class.getName () , TARGET_ORDER_PER_CLIENT , new TypeToken < ArrayList < TargetOrdersPerClient > > () {}.getType () );
			// Check if there are saved target order
			if ( savedInstanceState.getBoolean ( TARGET_ORDER_PER_CLIENT_PER_BRAND ) )
				// Retrieve target order per client per brand
				targetOrdersPerClientPerBrand = (ArrayList < TargetOrdersPerClientPerBrand >) ActivityInstance.readDataGson ( this , TargetActivity.class.getName () , TARGET_ORDER_PER_CLIENT_PER_BRAND , new TypeToken < ArrayList < TargetOrdersPerClientPerBrand > > () {}.getType () );
			// Check if there are saved target collection
			if ( savedInstanceState.getBoolean ( TARGET_COLLECTION ) )
				// Retrieve target collection
				targetCollections = (ArrayList < TargetCollections >) ActivityInstance.readDataGson ( this , TargetActivity.class.getName () , TARGET_COLLECTION , new TypeToken < ArrayList < TargetCollections > > () {}.getType () );
		} // End if
		
		// Check if any of the data is NOT valid
		if ( populateList == null 
				&& ( targetVisits == null 
					|| targetItems == null
					|| targetOrders == null
					|| targetOrdersPerClient == null
					|| targetOrdersPerClientPerBrand == null
					|| targetCollections == null ) ) {
			// Retrieve the target coverage asynchronously
			populateList = new PopulateList ();
			populateList.execute ();
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
    	
		// Save the content of the targetVisits using GSON
		ActivityInstance.saveDataGson ( this , TargetActivity.class.getName () , TARGET_VISITS , targetVisits );
		// Indicate that there is saved targetVisits data
		outState.putBoolean ( TARGET_VISITS , true );
		
		// Save the content of the targetItems using GSON
		ActivityInstance.saveDataGson ( this , TargetActivity.class.getName () , TARGET_ITEMS , targetItems );
		// Indicate that there is saved targetItems data
		outState.putBoolean ( TARGET_ITEMS , true );
		
		// Save the content of the targetOrders using GSON
		ActivityInstance.saveDataGson ( this , TargetActivity.class.getName () , TARGET_ORDER , targetOrders );
		// Indicate that there is saved targetOrders data
		outState.putBoolean ( TARGET_ORDER , true );
		
		// Save the content of the targetOrdersPerClient using GSON
		ActivityInstance.saveDataGson ( this , TargetActivity.class.getName () , TARGET_ORDER_PER_CLIENT , targetOrdersPerClient );
		// Indicate that there is saved targetOrdersPerClient data
		outState.putBoolean ( TARGET_ORDER_PER_CLIENT , true );
		
		// Save the content of the targetOrdersPerClientPerBrand using GSON
		ActivityInstance.saveDataGson ( this , TargetActivity.class.getName () , TARGET_ORDER_PER_CLIENT_PER_BRAND , targetOrdersPerClientPerBrand );
		// Indicate that there is saved targetOrdersPerClientPerBrand data
		outState.putBoolean ( TARGET_ORDER_PER_CLIENT_PER_BRAND , true );
		
		// Save the content of the targetCollections using GSON
		ActivityInstance.saveDataGson ( this , TargetActivity.class.getName () , TARGET_COLLECTION , targetCollections );
		// Indicate that there is saved targetCollections data
		outState.putBoolean ( TARGET_COLLECTION , true );
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
			targetVisits = null;
			targetItems = null;
			targetOrders = null;
			targetOrdersPerClient = null;
			targetOrdersPerClientPerBrand = null;
			targetCollections = null;
		} // End if
	}
	
	/**
	 * Gets a new fragment pager adapter used to display multiple target coverage via a view pager.
	 * Gets by default all the fragments combinations.<br>
	 * Override this method if a other / modified class is needed.<br>
	 * <b>Note : </b> This method should never return {@code NULL}.<br>
	 * <b>Note : </b> This method should properly initialize the {@link #fragments} list !!
	 * 
	 * @return	{@link me.SyncWise.Android.AppFragmentPagerAdapter AppFragmentPagerAdapter} that represents each page as a Fragment that is persistently kept in the fragment manager as long as the user can return to the page.
	 */
	protected AppFragmentPagerAdapter getAppFragmentPagerAdapter () {
		// Initialize the list used to host the fragments descriptions
		fragments = new ArrayList < AppFragment > ();
		// Define the required fragments
		fragments.add ( new AppFragment ( AppResources.getInstance ( this ).getString ( this , R.string.visits_frequency_activity_title ) , R.layout.visits_frequency_fragment ) );
		// Initialize and return a fragment pager adapter using the fragments list above and the appropriate array of fragment classes
		return new AppFragmentPagerAdapter ( getSupportFragmentManager () , fragments , new Class < ? > [] { VisitsFrequencyFragment.class } );
	}
		
	/**
	 * Gets the list adapter for the target visits list, or {@code NULL} if target visits list is invalid.
	 * 
	 * @param targetVisits	List of {@link me.SyncWise.Android.Modules.Target.TargetVisits TargetVisits} objects used to host the targeted clients' visits coverage.
	 * @return	List Adapter for the target visits list, or {@code NULL} if the target visits list is invalid. 
	 */
	public TargetAdapter getTargetVisitsAdapter ( final ArrayList < TargetVisits > targetVisits ) {
		// Check if the target visits list is valid
		if ( this.targetVisits == null && targetVisits == null )
			// Invalid data
			return null;
		// Check if a target visits list is provided
		if ( targetVisits != null )
			// Initialize the target visits list
			this.targetVisits = targetVisits;
		
		// Declare and initialize a list of targets
		List < Target > targets = new ArrayList < Target > ( this.targetVisits );
		
		// Declare and initialize a list adapter
		TargetAdapter adapter = new TargetAdapter ( this , R.layout.target_activity_item , targets );
		// Return the list adapter
		return adapter;
	}
	
	/**
	 * Gets the list adapter for the target items list, or {@code NULL} if target items list is invalid.
	 * 
	 * @param targetItems	List of {@link me.SyncWise.Android.Modules.Target.TargetItems TargetItems} objects used to host the targeted items' quantity coverage.
	 * @return	List Adapter for the target items list, or {@code NULL} if the target items list is invalid. 
	 */
	public TargetAdapter getTargetItemsAdapter ( final ArrayList < TargetItems > targetItems ) {
		// Check if the target items list is valid
		if ( this.targetItems == null && targetItems == null )
			// Invalid data
			return null;
		// Check if a target items list is provided
		if ( targetItems != null )
			// Initialize the target items list
			this.targetItems = targetItems;
		
		// Declare and initialize a list of targets
		List < Target > targets = new ArrayList < Target > ( this.targetItems );
		
		// Declare and initialize a list adapter
		TargetAdapter adapter = new TargetAdapter ( this , R.layout.target_activity_item , targets );
		// Return the list adapter
		return adapter;
	}
	
	/**
	 * Gets the list adapter for the target order amounts list, or {@code NULL} if target order amounts list is invalid.
	 * 
	 * @param targetOrders	List of {@link me.SyncWise.Android.Modules.Target.TargetOrders TargetOrder} objects used to host the targeted order amounts coverage.
	 * @return	List Adapter for the target order amounts list, or {@code NULL} if the target order amounts list is invalid. 
	 */
	public TargetAdapter getTargetOrderAmountsAdapter ( final ArrayList < TargetOrders > targetOrders ) {
		// Check if the target order amounts list is valid
		if ( this.targetOrders == null && targetOrders == null )
			// Invalid data
			return null;
		// Check if a target order amounts list is provided
		if ( targetOrders != null )
			// Initialize the target order amounts list
			this.targetOrders = targetOrders;
		
		// Declare and initialize a list of targets
		List < Target > targets = new ArrayList < Target > ( this.targetOrders );
		
		// Declare and initialize a list adapter
		TargetAdapter adapter = new TargetAdapter ( this , R.layout.target_activity_item , targets );
		// Return the list adapter
		return adapter;
	}
	
	/**
	 * Gets the list adapter for the target order amounts per client list, or {@code NULL} if target order amounts per client list is invalid.
	 * 
	 * @param targetOrdersPerClient	List of {@link me.SyncWise.Android.Modules.Target.TargetOrders TargetOrder} objects used to host the targeted order amounts per client coverage.
	 * @return	List Adapter for the target order amounts per client list, or {@code NULL} if the target order amounts per client list is invalid. 
	 */
	public TargetAdapter getTargetOrderAmountsPerClientAdapter ( final ArrayList < TargetOrdersPerClient > targetOrdersPerClient ) {
		// Check if the target order amounts per client list is valid
		if ( this.targetOrdersPerClient == null && targetOrdersPerClient == null )
			// Invalid data
			return null;
		// Check if a target order amounts per client list is provided
		if ( targetOrdersPerClient != null )
			// Initialize the target order amounts per client list
			this.targetOrdersPerClient = targetOrdersPerClient;
		
		// Declare and initialize a list of targets
		List < Target > targets = new ArrayList < Target > ( this.targetOrdersPerClient );
		
		// Declare and initialize a list adapter
		TargetAdapter adapter = new TargetAdapter ( this , R.layout.target_activity_item , targets );
		// Return the list adapter
		return adapter;
	}
	
	
	/**
	 * Gets the list adapter for the target order amounts per client per brand list, or {@code NULL} if target order amounts per client per brand list is invalid.
	 * 
	 * @param targetOrdersPerClient	List of {@link me.SyncWise.Android.Modules.Target.TargetOrders TargetOrder} objects used to host the targeted order amounts per client per brand coverage.
	 * @return	List Adapter for the target order amounts per client per brand list, or {@code NULL} if the target order amounts per client per brand list is invalid. 
	 */
	public TargetAdapter getTargetOrderAmountsPerClientPerBrandAdapter ( final ArrayList < TargetOrdersPerClientPerBrand > targetOrdersPerClientPerBrand ) {
		// Check if the target order amounts per client per brand list is valid
		if ( this.targetOrdersPerClientPerBrand == null && targetOrdersPerClientPerBrand == null )
			// Invalid data
			return null;
		// Check if a target order amounts per client per brand list is provided
		if ( targetOrdersPerClientPerBrand != null )
			// Initialize the target order amounts per client per brand list
			this.targetOrdersPerClientPerBrand = targetOrdersPerClientPerBrand;
		
		// Declare and initialize a list of targets
		List < Target > targets = new ArrayList < Target > ( this.targetOrdersPerClientPerBrand );
		
		// Declare and initialize a list adapter
		TargetAdapter adapter = new TargetAdapter ( this , R.layout.target_activity_item , targets );
		// Return the list adapter
		return adapter;
	}
	
	/**
	 * Gets the list adapter for the target collection amounts list, or {@code NULL} if target collection amounts list is invalid.
	 * 
	 * @param targetCollections	List of {@link me.SyncWise.Android.Modules.Target.TargetCollections TargetCollections} objects used to host the targeted collection amounts coverage.
	 * @return	List Adapter for the target collection amounts list, or {@code NULL} if the target collection amounts list is invalid. 
	 */
	public TargetAdapter getTargetCollectionAmountsAdapter ( final ArrayList < TargetCollections > targetCollections ) {
		// Check if the target collection amounts list is valid
		if ( this.targetCollections == null && targetCollections == null )
			// Invalid data
			return null;
		// Check if a target collection amounts list is provided
		if ( targetCollections != null )
			// Initialize the target collection amounts list
			this.targetCollections = targetCollections;
		
		// Declare and initialize a list of targets
		List < Target > targets = new ArrayList < Target > ( this.targetCollections );
		
		// Declare and initialize a list adapter
		TargetAdapter adapter = new TargetAdapter ( this , R.layout.target_activity_item , targets );
		// Return the list adapter
		return adapter;
	}
	
	/**
	 * AsyncTask helper class used to populate the target coverage.
	 * 
	 * @author Elias
	 *
	 */
	private class PopulateList extends AsyncTask < Void , Void , Void > {
		
		/**
		 * List of {@link me.SyncWise.Android.Modules.Target.TargetVisits TargetVisits} objects used to host the targeted clients' visits coverage.
		 */
		private ArrayList < TargetVisits > targetVisits;
		
		/**
		 * List of {@link me.SyncWise.Android.Modules.Target.TargetCollections TargetCollections} objects used to host the targeted collection amounts coverage.
		 */
		private ArrayList < TargetCollections > targetCollections;
		
		/** 
		 * List of {@link me.SyncWise.Android.Modules.Target.TargetOrders TargetOrder} objects used to host the targeted order amounts coverage.
		 */
		private ArrayList < TargetOrders > targetOrders;
		
		/** 
		 * List of {@link me.SyncWise.Android.Modules.Target.TargetOrdersPerClient TargetOrdersPerClient} objects used to host the targeted order amounts per client coverage.
		 */
		private ArrayList < TargetOrdersPerClient > targetOrdersPerClient;
		
		/** 
		 * List of {@link me.SyncWise.Android.Modules.Target.TargetOrdersPerClientPerBrand TargetOrdersPerClientPerBrand} objects used to host the targeted order amounts per client per brand coverage.
		 */
		private ArrayList < TargetOrdersPerClientPerBrand > targetOrdersPerClientPerBrand;
		
		/** 
		 * List of {@link me.SyncWise.Android.Modules.Target.TargetItems TargetItems} objects used to host the targeted items' quantity coverage.
		 */
		private ArrayList < TargetItems > targetItems;
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground ( Void ... params ) {
			// Retrieve the current date in the UNIX epoch format
			long today = Calendar.getInstance ().getTimeInMillis ();
			// Retrieve the user code 
			String userCode = DatabaseUtils.getCurrentUserCode ( TargetActivity.this );
			// Declare and initialize a cursor object used to retrieve data sets from query results
			Cursor cursor = null;
			// Declare and initialize a string and an array of strings used to query target data from the database
			String SQL = null;
			String selectionArguments [] = null;
			// Declare and initialize a string builder along with a decimal format used to properly format monetary values
			StringBuilder pattern = null;
			DecimalFormat moneyFormat = null;
			// Retrieve the call object (if any)
			Call call = (Call) getIntent ().getSerializableExtra ( CALL );
			
			// Retrieve the total coverage
			// Compute the SQL query
			SQL = "SELECT DISTINCT TD." + TargetDetailsDao.Properties.TargetCode.columnName + " , " +
					"TD." + TargetDetailsDao.Properties.LineID.columnName + " , " +
					"U." + UsersDao.Properties.UserCode.columnName + " , " +
					"U." + UsersDao.Properties.UserName.columnName + " , " +
					"TD." + TargetDetailsDao.Properties.TargetAmount.columnName + " , " +
					"COALESCE ( TAC." + TargetAchievementsDao.Properties.AmountAcheived.columnName + " , 0 ) " + TargetAchievementsDao.Properties.AmountAcheived.columnName + " " +
					"FROM ( ( ( " + TargetHeadersDao.TABLENAME + " TH INNER JOIN " + TargetAssignmentsDao.TABLENAME + " TAS ON TH." + TargetHeadersDao.Properties.TargetCode.columnName + " = TAS." + TargetAssignmentsDao.Properties.TargetCode.columnName + " " +
					"AND ( TAS." + TargetAssignmentsDao.Properties.AssignmentType.columnName + " = ? OR TAS." + TargetAssignmentsDao.Properties.AssignmentType.columnName + " = ? ) ) " +
					"INNER JOIN " + TargetDetailsDao.TABLENAME + " TD ON TH." + TargetHeadersDao.Properties.TargetCode.columnName + " = TD." + TargetDetailsDao.Properties.TargetCode.columnName + " ) " +
					"INNER JOIN " + UsersDao.TABLENAME + " U ON TD." + TargetDetailsDao.Properties.SubjectCode.columnName + " = ? AND TD." + TargetDetailsDao.Properties.TargetDetailType.columnName + " = ? " +
							"AND TD." + TargetDetailsDao.Properties.SubSubjectCode.columnName + " = U." + UsersDao.Properties.UserCode.columnName + " AND TH." + TargetHeadersDao.Properties.CompanyCode.columnName + " = U." + UsersDao.Properties.CompanyCode.columnName + " " +
							"AND TD." + TargetDetailsDao.Properties.TargetDetailTypeSub.columnName + " = ? AND TD." + TargetDetailsDao.Properties.SubjectCode.columnName + " = TD." + TargetDetailsDao.Properties.SubSubjectCode.columnName + " ) " +
					"LEFT JOIN " + TargetAchievementsDao.TABLENAME + " TAC ON TAC." + TargetAchievementsDao.Properties.TargetCode.columnName + " = TD." + TargetDetailsDao.Properties.TargetCode.columnName + " AND " + "TAC." + TargetAchievementsDao.Properties.LineID.columnName + " = TD." + TargetDetailsDao.Properties.LineID.columnName + " AND " +
					"TAC." + TargetAchievementsDao.Properties.SubjectCode.columnName + " = TD." + TargetDetailsDao.Properties.SubSubjectCode.columnName + " AND TAC." + TargetAchievementsDao.Properties.TargetDetailType.columnName + " = TD." + TargetDetailsDao.Properties.TargetDetailTypeSub.columnName + " " +
					"WHERE ( ( TH." + TargetHeadersDao.Properties.StartDate.columnName + " IS NOT NULL AND ? > TH." + TargetHeadersDao.Properties.StartDate.columnName + " ) " +
					"AND ( TH." + TargetHeadersDao.Properties.EndDate.columnName + " IS NOT NULL AND ? < TH." + TargetHeadersDao.Properties.EndDate.columnName + " ) ) " +
					"AND TH." + TargetHeadersDao.Properties.TargetType.columnName + " = ? ";
			
			// Compute the selection arguments
			selectionArguments = new String [] {
				String.valueOf ( TargetAssignmentsUtils.Type.USER ) ,
				String.valueOf ( TargetAssignmentsUtils.Type.GROUP_USERS ) ,
				userCode ,
				String.valueOf ( TargetHeadersUtils.SubType.USER ) ,
				String.valueOf ( TargetHeadersUtils.SubType.USER ) ,
				String.valueOf ( today ) ,
				String.valueOf ( today ) ,
				String.valueOf ( TargetHeadersUtils.Type.TOTAL_COVERAGE )
			};
			
			// Query DB in order to retrieve the target visits
			cursor = DatabaseUtils.getInstance ( TargetActivity.this ).getDaoSession ().getDatabase ()
					.rawQuery ( SQL , selectionArguments );
			// Initialize a list used to host the target visits
			targetVisits = new ArrayList < TargetVisits > ();
			// Move the cursor to the first row
			if ( cursor.moveToFirst () == true ) {
				do {
					// Retrieve the user name
					String userName = cursor.getString ( cursor.getColumnIndex ( UsersDao.Properties.UserName.columnName ) );
					// Retrieve the required number
					int requiredNumber = cursor.getInt ( cursor.getColumnIndex ( TargetDetailsDao.Properties.TargetAmount.columnName ) );
					// Retrieve the achieved number
					int achievedNumber = cursor.getInt ( cursor.getColumnIndex ( TargetAchievementsDao.Properties.AmountAcheived.columnName ) );
					// Retrieve the current target visit data and add it to the list
					targetVisits.add ( new TargetVisits ( TargetActivity.this ,
							userCode ,
							userName , 
							requiredNumber ,
							achievedNumber ) );
				} while ( cursor.moveToNext () );
			} // End if
			// Close the cursor
			cursor.close ();
			cursor = null;
			
			// Retrieve the target collection amounts
			// Compute the SQL query
			SQL = "SELECT DISTINCT TD." + TargetDetailsDao.Properties.TargetCode.columnName + " , " +
					"TD." + TargetDetailsDao.Properties.LineID.columnName + " , " +
					"C." + CurrenciesDao.Properties.CurrencyName.columnName + " , " +
					"C." + CurrenciesDao.Properties.CurrencyRounding.columnName + " , " +
					"TD." + TargetDetailsDao.Properties.TargetAmount.columnName + " , " +
					"COALESCE ( TAC." + TargetAchievementsDao.Properties.AmountAcheived.columnName + " , 0.0 ) " + TargetAchievementsDao.Properties.AmountAcheived.columnName + " " +
					"FROM ( ( ( " + TargetHeadersDao.TABLENAME + " TH INNER JOIN " + TargetAssignmentsDao.TABLENAME + " TAS ON TH." + TargetHeadersDao.Properties.TargetCode.columnName + " = TAS." + TargetAssignmentsDao.Properties.TargetCode.columnName + " " +
					"AND ( TAS." + TargetAssignmentsDao.Properties.AssignmentType.columnName + " = ? OR TAS." + TargetAssignmentsDao.Properties.AssignmentType.columnName + " = ? ) ) " +
					"INNER JOIN " + TargetDetailsDao.TABLENAME + " TD ON TH." + TargetHeadersDao.Properties.TargetCode.columnName + " = TD." + TargetDetailsDao.Properties.TargetCode.columnName + " AND TD." + TargetDetailsDao.Properties.SubjectCode.columnName + " = ? AND TD." + TargetDetailsDao.Properties.TargetDetailType.columnName + " = ? " +
							"AND TD." + TargetDetailsDao.Properties.SubjectCode.columnName + " = TD." + TargetDetailsDao.Properties.SubSubjectCode.columnName + " AND TD." + TargetDetailsDao.Properties.TargetDetailType.columnName + " = TD." + TargetDetailsDao.Properties.TargetDetailTypeSub.columnName + " ) " +
					"INNER JOIN " + CurrenciesDao.TABLENAME + " C ON TD." + TargetDetailsDao.Properties.CurrencyCode.columnName + " = C." + CurrenciesDao.Properties.CurrencyCode.columnName + " ) " +
					"LEFT JOIN " + TargetAchievementsDao.TABLENAME + " TAC ON TAC." + TargetAchievementsDao.Properties.TargetCode.columnName + " = TD." + TargetDetailsDao.Properties.TargetCode.columnName + " AND " + "TAC." + TargetAchievementsDao.Properties.LineID.columnName + " = TD." + TargetDetailsDao.Properties.LineID.columnName + " AND " +
					"TAC." + TargetAchievementsDao.Properties.SubjectCode.columnName + " = TD." + TargetDetailsDao.Properties.SubSubjectCode.columnName + " AND TAC." + TargetAchievementsDao.Properties.TargetDetailType.columnName + " = TD." + TargetDetailsDao.Properties.TargetDetailTypeSub.columnName + " " +
					"WHERE ( ( TH." + TargetHeadersDao.Properties.StartDate.columnName + " IS NOT NULL AND ? > TH." + TargetHeadersDao.Properties.StartDate.columnName + " ) " +
					"AND ( TH." + TargetHeadersDao.Properties.EndDate.columnName + " IS NOT NULL AND ? < TH." + TargetHeadersDao.Properties.EndDate.columnName + " ) ) " +
					"AND TH." + TargetHeadersDao.Properties.TargetType.columnName + " = ? ";
			
			// Compute the selection arguments
			selectionArguments = new String [] {
				String.valueOf ( TargetAssignmentsUtils.Type.USER ) ,
				String.valueOf ( TargetAssignmentsUtils.Type.GROUP_USERS ) ,
				userCode ,
				String.valueOf ( TargetHeadersUtils.SubType.USER ) ,
				String.valueOf ( today ) ,
				String.valueOf ( today ) ,
				String.valueOf ( TargetHeadersUtils.Type.TOTAL_COLLECTION )
			};
			
			// Query DB in order to retrieve the target collection amounts
			cursor = DatabaseUtils.getInstance ( TargetActivity.this ).getDaoSession ().getDatabase ()
					.rawQuery ( SQL , selectionArguments );
			// Initialize a list used to host the target collection amounts
			targetCollections = new ArrayList < TargetCollections > ();
			// Move the cursor to the first row
			if ( cursor.moveToFirst () == true ) {
				do {
					// Retrieve the currency description
					String currencyDescription = cursor.getString ( cursor.getColumnIndex ( CurrenciesDao.Properties.CurrencyName.columnName ) );
					// Retrieve the currency rounding
					int currencyRounding = cursor.getInt ( cursor.getColumnIndex ( CurrenciesDao.Properties.CurrencyRounding.columnName ) );
					// Retrieve the required amount
					double requiredAmount = cursor.getDouble ( cursor.getColumnIndex ( TargetDetailsDao.Properties.TargetAmount.columnName ) );
					// Retrieve the achieved amount
					double achievedAmount = cursor.getDouble ( cursor.getColumnIndex ( TargetAchievementsDao.Properties.AmountAcheived.columnName ) );
					
					// Compute the money formatter pattern
					pattern = new StringBuilder ();
					pattern.append ( " #,##0" );
					// Check if the currency has a rounding value
					if ( currencyRounding >= 1 ) {
						pattern.append ( ".0" );
						for ( int i = 1 ; i < currencyRounding ; i ++ )
							pattern.append ( "0" );
					} // End if
					// Initialize a money format
					moneyFormat = new DecimalFormat ( pattern.toString () );
					
					// Retrieve the current target collection amount and add it to the list
					targetCollections.add ( new TargetCollections ( TargetActivity.this ,
							currencyDescription ,
							requiredAmount ,
							achievedAmount ,
							moneyFormat ) );
				} while ( cursor.moveToNext () );
			} // End if
			// Close the cursor
			cursor.close ();
			cursor = null;
			
			// Retrieve the target order amounts
			// Compute the SQL query
			SQL = "SELECT DISTINCT TD." + TargetDetailsDao.Properties.TargetCode.columnName + " , " +
					"TD." + TargetDetailsDao.Properties.LineID.columnName + " , " +
					"C." + CurrenciesDao.Properties.CurrencyName.columnName + " , " +
					"C." + CurrenciesDao.Properties.CurrencyRounding.columnName + " , " +
					"TD." + TargetDetailsDao.Properties.TargetAmount.columnName + " , " +
					"COALESCE ( TAC." + TargetAchievementsDao.Properties.AmountAcheived.columnName + " , 0.0 ) " + TargetAchievementsDao.Properties.AmountAcheived.columnName + " " +
					"FROM ( ( ( " + TargetHeadersDao.TABLENAME + " TH INNER JOIN " + TargetAssignmentsDao.TABLENAME + " TAS ON TH." + TargetHeadersDao.Properties.TargetCode.columnName + " = TAS." + TargetAssignmentsDao.Properties.TargetCode.columnName + " " +
					"AND ( TAS." + TargetAssignmentsDao.Properties.AssignmentType.columnName + " = ? OR TAS." + TargetAssignmentsDao.Properties.AssignmentType.columnName + " = ? ) ) " +
					"INNER JOIN " + TargetDetailsDao.TABLENAME + " TD ON TH." + TargetHeadersDao.Properties.TargetCode.columnName + " = TD." + TargetDetailsDao.Properties.TargetCode.columnName + " AND TD." + TargetDetailsDao.Properties.SubjectCode.columnName + " = ? AND TD." + TargetDetailsDao.Properties.TargetDetailType.columnName + " = ? " +
							"AND TD." + TargetDetailsDao.Properties.SubjectCode.columnName + " = TD." + TargetDetailsDao.Properties.SubSubjectCode.columnName + " AND TD." + TargetDetailsDao.Properties.TargetDetailType.columnName + " = TD." + TargetDetailsDao.Properties.TargetDetailTypeSub.columnName + " ) " +
					"INNER JOIN " + CurrenciesDao.TABLENAME + " C ON TD." + TargetDetailsDao.Properties.CurrencyCode.columnName + " = C." + CurrenciesDao.Properties.CurrencyCode.columnName + " ) " +
					"LEFT JOIN " + TargetAchievementsDao.TABLENAME + " TAC ON TAC." + TargetAchievementsDao.Properties.TargetCode.columnName + " = TD." + TargetDetailsDao.Properties.TargetCode.columnName + " AND " + "TAC." + TargetAchievementsDao.Properties.LineID.columnName + " = TD." + TargetDetailsDao.Properties.LineID.columnName + " AND " +
					"TAC." + TargetAchievementsDao.Properties.SubjectCode.columnName + " = TD." + TargetDetailsDao.Properties.SubSubjectCode.columnName + " AND TAC." + TargetAchievementsDao.Properties.TargetDetailType.columnName + " = TD." + TargetDetailsDao.Properties.TargetDetailTypeSub.columnName + " " +
					"WHERE ( ( TH." + TargetHeadersDao.Properties.StartDate.columnName + " IS NOT NULL AND ? > TH." + TargetHeadersDao.Properties.StartDate.columnName + " ) " +
					"AND ( TH." + TargetHeadersDao.Properties.EndDate.columnName + " IS NOT NULL AND ? < TH." + TargetHeadersDao.Properties.EndDate.columnName + " ) ) " +
					"AND TH." + TargetHeadersDao.Properties.TargetType.columnName + " = ? ";
			
			// Compute the selection arguments
			selectionArguments = new String [] {
				String.valueOf ( TargetAssignmentsUtils.Type.USER ) ,
				String.valueOf ( TargetAssignmentsUtils.Type.GROUP_USERS ) ,
				userCode ,
				String.valueOf ( TargetHeadersUtils.SubType.USER ) ,
				String.valueOf ( today ) ,
				String.valueOf ( today ) ,
				String.valueOf ( TargetHeadersUtils.Type.TOTAL_SALES )
			};
			
			// Query DB in order to retrieve the target order amounts
			cursor = DatabaseUtils.getInstance ( TargetActivity.this ).getDaoSession ().getDatabase ()
					.rawQuery ( SQL , selectionArguments );
			// Initialize a list used to host the target order amounts
			targetOrders = new ArrayList < TargetOrders > ();
			// Move the cursor to the first row
			if ( cursor.moveToFirst () == true ) {
				do {
					// Retrieve the currency description
					String currencyDescription = cursor.getString ( cursor.getColumnIndex ( CurrenciesDao.Properties.CurrencyName.columnName ) );
					// Retrieve the currency rounding
					int currencyRounding = cursor.getInt ( cursor.getColumnIndex ( CurrenciesDao.Properties.CurrencyRounding.columnName ) );
					// Retrieve the required amount
					double requiredAmount = cursor.getDouble ( cursor.getColumnIndex ( TargetDetailsDao.Properties.TargetAmount.columnName ) );
					// Retrieve the achieved amount
					double achievedAmount = cursor.getDouble ( cursor.getColumnIndex ( TargetAchievementsDao.Properties.AmountAcheived.columnName ) );
					
					// Compute the money formatter pattern
					pattern = new StringBuilder ();
					pattern.append ( " #,##0" );
					// Check if the currency has a rounding value
					if ( currencyRounding >= 1 ) {
						pattern.append ( ".0" );
						for ( int i = 1 ; i < currencyRounding ; i ++ )
							pattern.append ( "0" );
					} // End if
					// Initialize a money format
					moneyFormat = new DecimalFormat ( pattern.toString () );
					
					// Retrieve the current target order amount and add it to the list
					targetOrders.add ( new TargetOrders ( TargetActivity.this ,
							currencyDescription ,
							requiredAmount ,
							achievedAmount ,
							moneyFormat ) );
				} while ( cursor.moveToNext () );
			} // End if
			// Close the cursor
			cursor.close ();
			cursor = null;
			
			// Retrieve the target order amounts per client
			// Compute the SQL query
			SQL = "SELECT DISTINCT TD." + TargetDetailsDao.Properties.TargetCode.columnName + " , " +
					"TD." + TargetDetailsDao.Properties.LineID.columnName + " , " +
					"C." + ClientsDao.Properties.ClientCode.columnName + " , " +
					"C." + ClientsDao.Properties.ClientName.columnName + " , " +
					"CU." + CurrenciesDao.Properties.CurrencyName.columnName + " , " +
					"CU." + CurrenciesDao.Properties.CurrencyRounding.columnName + " , " +
					"TD." + TargetDetailsDao.Properties.TargetAmount.columnName + " , " +
					"COALESCE ( TAC." + TargetAchievementsDao.Properties.AmountAcheived.columnName + " , 0.0 ) " + TargetAchievementsDao.Properties.AmountAcheived.columnName + " " +
					"FROM ( ( ( ( " + TargetHeadersDao.TABLENAME + " TH INNER JOIN " + TargetAssignmentsDao.TABLENAME + " TAS ON TH." + TargetHeadersDao.Properties.TargetCode.columnName + " = TAS." + TargetAssignmentsDao.Properties.TargetCode.columnName + " " +
					"AND ( TAS." + TargetAssignmentsDao.Properties.AssignmentType.columnName + " = ? OR TAS." + TargetAssignmentsDao.Properties.AssignmentType.columnName + " = ? ) ) " +
					"INNER JOIN " + TargetDetailsDao.TABLENAME + " TD ON TH." + TargetHeadersDao.Properties.TargetCode.columnName + " = TD." + TargetDetailsDao.Properties.TargetCode.columnName + " AND TD." + TargetDetailsDao.Properties.SubjectCode.columnName + " = ? AND TD." + TargetDetailsDao.Properties.TargetDetailType.columnName + " = ? ) " +
					"INNER JOIN " + CurrenciesDao.TABLENAME + " CU ON TD." + TargetDetailsDao.Properties.CurrencyCode.columnName + " = CU." + CurrenciesDao.Properties.CurrencyCode.columnName + " ) " +
					"INNER JOIN " + ClientsDao.TABLENAME + " C ON TD." + TargetDetailsDao.Properties.SubSubjectCode.columnName + " = C." + ClientsDao.Properties.ClientCode.columnName + " AND TH." + TargetHeadersDao.Properties.CompanyCode.columnName + " = C." + ClientsDao.Properties.CompanyCode.columnName + " " +
							"AND " + TargetDetailsDao.Properties.TargetDetailTypeSub.columnName + " = ? ) " +
					"LEFT JOIN " + TargetAchievementsDao.TABLENAME + " TAC ON TAC." + TargetAchievementsDao.Properties.TargetCode.columnName + " = TD." + TargetDetailsDao.Properties.TargetCode.columnName + " AND " + "TAC." + TargetAchievementsDao.Properties.LineID.columnName + " = TD." + TargetDetailsDao.Properties.LineID.columnName + " AND " +
					"TAC." + TargetAchievementsDao.Properties.SubjectCode.columnName + " = TD." + TargetDetailsDao.Properties.SubSubjectCode.columnName + " AND TAC." + TargetAchievementsDao.Properties.TargetDetailType.columnName + " = TD." + TargetDetailsDao.Properties.TargetDetailTypeSub.columnName + " " +
					"WHERE ( ( TH." + TargetHeadersDao.Properties.StartDate.columnName + " IS NOT NULL AND ? > TH." + TargetHeadersDao.Properties.StartDate.columnName + " ) " +
					"AND ( TH." + TargetHeadersDao.Properties.EndDate.columnName + " IS NOT NULL AND ? < TH." + TargetHeadersDao.Properties.EndDate.columnName + " ) ) " +
					"AND TH." + TargetHeadersDao.Properties.TargetType.columnName + " = ? ";
			
			// Compute the selection arguments
			selectionArguments = new String [] {
				String.valueOf ( TargetAssignmentsUtils.Type.USER ) ,
				String.valueOf ( TargetAssignmentsUtils.Type.GROUP_USERS ) ,
				userCode ,
				String.valueOf ( TargetHeadersUtils.SubType.USER ) ,
				String.valueOf ( TargetHeadersUtils.SubType.CLIENT ) ,
				String.valueOf ( today ) ,
				String.valueOf ( today ) ,
				String.valueOf ( TargetHeadersUtils.Type.TOTAL_SALES_PER_CLIENT )
			};
			
			// Query DB in order to retrieve the target order amounts per client
			cursor = DatabaseUtils.getInstance ( TargetActivity.this ).getDaoSession ().getDatabase ()
					.rawQuery ( SQL , selectionArguments );
			// Initialize a list used to host the target order amounts per client
			targetOrdersPerClient = new ArrayList < TargetOrdersPerClient > ();
			// Move the cursor to the first row
			if ( cursor.moveToFirst () == true ) {
				do {
					// Retrieve the client code
					String clientCode = cursor.getString ( cursor.getColumnIndex ( ClientsDao.Properties.ClientCode.columnName ) );
					// Check if the call is provided and if the client codes match
					if ( call != null && ! call.getClient ().getClientCode ().equals ( clientCode ) )
						// Skip the current record
						continue;
					// Retrieve the client description
					String clientDescription = cursor.getString ( cursor.getColumnIndex ( ClientsDao.Properties.ClientName.columnName ) );
					// Retrieve the currency description
					String currencyDescription = cursor.getString ( cursor.getColumnIndex ( CurrenciesDao.Properties.CurrencyName.columnName ) );
					// Retrieve the currency rounding
					int currencyRounding = cursor.getInt ( cursor.getColumnIndex ( CurrenciesDao.Properties.CurrencyRounding.columnName ) );
					// Retrieve the required amount
					double requiredAmount = cursor.getDouble ( cursor.getColumnIndex ( TargetDetailsDao.Properties.TargetAmount.columnName ) );
					// Retrieve the achieved amount
					double achievedAmount = cursor.getDouble ( cursor.getColumnIndex ( TargetAchievementsDao.Properties.AmountAcheived.columnName ) );
					
					// Compute the money formatter pattern
					pattern = new StringBuilder ();
					pattern.append ( " #,##0" );
					// Check if the currency has a rounding value
					if ( currencyRounding >= 1 ) {
						pattern.append ( ".0" );
						for ( int i = 1 ; i < currencyRounding ; i ++ )
							pattern.append ( "0" );
					} // End if
					// Initialize a money format
					moneyFormat = new DecimalFormat ( pattern.toString () );
					
					// Retrieve the current target order amount per client and add it to the list
					targetOrdersPerClient.add ( new TargetOrdersPerClient ( TargetActivity.this ,
							clientCode ,
							clientDescription ,
							currencyDescription ,
							requiredAmount ,
							achievedAmount ,
							moneyFormat ) );
				} while ( cursor.moveToNext () );
			} // End if
			// Close the cursor
			cursor.close ();
			cursor = null;
			
			// Retrieve the target order amounts per client per brand
			// Compute the SQL query
			SQL = "SELECT DISTINCT TD." + TargetDetailsDao.Properties.TargetCode.columnName + " , " +
					"TD." + TargetDetailsDao.Properties.LineID.columnName + " , " +
					"C." + ClientsDao.Properties.ClientCode.columnName + " , " +
					"C." + ClientsDao.Properties.ClientName.columnName + " , " +
					"I." + ItemsDao.Properties.ItemCode.columnName + " , " +
					"I." + ItemsDao.Properties.ItemName.columnName + " , " +
					"D." + DivisionsDao.Properties.DivisionCode.columnName + " , " +
					"D." + DivisionsDao.Properties.DivisionName.columnName + " , " +
					"CU." + CurrenciesDao.Properties.CurrencyName.columnName + " , " +
					"CU." + CurrenciesDao.Properties.CurrencyRounding.columnName + " , " +
					"TD." + TargetDetailsDao.Properties.TargetAmount.columnName + " , " +
					"COALESCE ( TAC." + TargetAchievementsDao.Properties.AmountAcheived.columnName + " , 0.0 ) " + TargetAchievementsDao.Properties.AmountAcheived.columnName + " " +
					"FROM ( ( ( ( ( ( " + TargetHeadersDao.TABLENAME + " TH INNER JOIN " + TargetAssignmentsDao.TABLENAME + " TAS ON TH." + TargetHeadersDao.Properties.TargetCode.columnName + " = TAS." + TargetAssignmentsDao.Properties.TargetCode.columnName + " " +
					"AND ( TAS." + TargetAssignmentsDao.Properties.AssignmentType.columnName + " = ? OR TAS." + TargetAssignmentsDao.Properties.AssignmentType.columnName + " = ? ) ) " +
					"INNER JOIN " + TargetDetailsDao.TABLENAME + " TD ON TH." + TargetHeadersDao.Properties.TargetCode.columnName + " = TD." + TargetDetailsDao.Properties.TargetCode.columnName + " ) " +
					"INNER JOIN " + CurrenciesDao.TABLENAME + " CU ON TD." + TargetDetailsDao.Properties.CurrencyCode.columnName + " = CU." + CurrenciesDao.Properties.CurrencyCode.columnName + " ) " +
					"INNER JOIN " + ClientsDao.TABLENAME + " C ON TD." + TargetDetailsDao.Properties.SubjectCode.columnName + " = C." + ClientsDao.Properties.ClientCode.columnName + " AND TH." + TargetHeadersDao.Properties.CompanyCode.columnName + " = C." + ClientsDao.Properties.CompanyCode.columnName + " " +
							"AND TD." + TargetDetailsDao.Properties.TargetDetailType.columnName + " = ? ) " +
					"LEFT JOIN " + ItemsDao.TABLENAME + " I ON TD." + TargetDetailsDao.Properties.SubSubjectCode.columnName + " = I." + ItemsDao.Properties.ItemCode.columnName + " AND TH." + TargetHeadersDao.Properties.CompanyCode.columnName + " = I." + ItemsDao.Properties.CompanyCode.columnName + " " +
							"AND " + TargetDetailsDao.Properties.TargetDetailTypeSub.columnName + " = ? ) " +
					"LEFT JOIN " + DivisionsDao.TABLENAME + " D ON TD." + TargetDetailsDao.Properties.SubSubjectCode.columnName + " = D." + DivisionsDao.Properties.DivisionCode.columnName + " AND TH." + TargetHeadersDao.Properties.CompanyCode.columnName + " = D." + DivisionsDao.Properties.CompanyCode.columnName + " " +
							"AND " + TargetDetailsDao.Properties.TargetDetailTypeSub.columnName + " = ? ) " +
					"LEFT JOIN " + TargetAchievementsDao.TABLENAME + " TAC ON TAC." + TargetAchievementsDao.Properties.TargetCode.columnName + " = TD." + TargetDetailsDao.Properties.TargetCode.columnName + " AND " + "TAC." + TargetAchievementsDao.Properties.LineID.columnName + " = TD." + TargetDetailsDao.Properties.LineID.columnName + " AND " +
					"TAC." + TargetAchievementsDao.Properties.SubjectCode.columnName + " = TD." + TargetDetailsDao.Properties.SubSubjectCode.columnName + " AND TAC." + TargetAchievementsDao.Properties.TargetDetailType.columnName + " = TD." + TargetDetailsDao.Properties.TargetDetailTypeSub.columnName + " " +
					"WHERE ( ( TH." + TargetHeadersDao.Properties.StartDate.columnName + " IS NOT NULL AND ? > TH." + TargetHeadersDao.Properties.StartDate.columnName + " ) " +
					"AND ( TH." + TargetHeadersDao.Properties.EndDate.columnName + " IS NOT NULL AND ? < TH." + TargetHeadersDao.Properties.EndDate.columnName + " ) ) " +
					"AND TH." + TargetHeadersDao.Properties.TargetType.columnName + " = ? AND NOT ( I." + ItemsDao.Properties.ItemCode.columnName + " IS NOT NULL AND D." + DivisionsDao.Properties.DivisionCode.columnName + " IS NOT NULL ) ";
			
			// Compute the selection arguments
			selectionArguments = new String [] {
				String.valueOf ( TargetAssignmentsUtils.Type.USER ) ,
				String.valueOf ( TargetAssignmentsUtils.Type.GROUP_USERS ) ,
				String.valueOf ( TargetHeadersUtils.SubType.CLIENT ) ,
				String.valueOf ( TargetHeadersUtils.SubType.ITEM ) ,
				String.valueOf ( TargetHeadersUtils.SubType.DIVISION ) ,
				String.valueOf ( today ) ,
				String.valueOf ( today ) ,
				String.valueOf ( TargetHeadersUtils.Type.TOTAL_SALES_PER_CLIENT_PER_BRAND )
			};
			
			// Query DB in order to retrieve the target order amounts per client
			cursor = DatabaseUtils.getInstance ( TargetActivity.this ).getDaoSession ().getDatabase ()
					.rawQuery ( SQL , selectionArguments );
			// Initialize a list used to host the target order amounts per client per brand
			targetOrdersPerClientPerBrand = new ArrayList < TargetOrdersPerClientPerBrand > ();
			// Move the cursor to the first row
			if ( cursor.moveToFirst () == true ) {
				do {
					// Retrieve the client code
					String clientCode = cursor.getString ( cursor.getColumnIndex ( ClientsDao.Properties.ClientCode.columnName ) );
					// Check if the call is provided and if the client codes match
					if ( call != null && ! call.getClient ().getClientCode ().equals ( clientCode ) )
						// Skip the current record
						continue;
					// Retrieve the client description
					String clientDescription = cursor.getString ( cursor.getColumnIndex ( ClientsDao.Properties.ClientName.columnName ) );
					// Retrieve the item description
					String itemCode = cursor.getString ( cursor.getColumnIndex ( ItemsDao.Properties.ItemCode.columnName ) );
					// Retrieve the item description
					String itemDescription = cursor.getString ( cursor.getColumnIndex ( ItemsDao.Properties.ItemName.columnName ) );
					// Retrieve the client description
					String divisionCode = cursor.getString ( cursor.getColumnIndex ( DivisionsDao.Properties.DivisionCode.columnName ) );
					// Retrieve the division description
					String divisionDescription = cursor.getString ( cursor.getColumnIndex ( DivisionsDao.Properties.DivisionName.columnName ) );
					// Retrieve the currency description
					String currencyDescription = cursor.getString ( cursor.getColumnIndex ( CurrenciesDao.Properties.CurrencyName.columnName ) );
					// Retrieve the currency rounding
					int currencyRounding = cursor.getInt ( cursor.getColumnIndex ( CurrenciesDao.Properties.CurrencyRounding.columnName ) );
					// Retrieve the required amount
					double requiredAmount = cursor.getDouble ( cursor.getColumnIndex ( TargetDetailsDao.Properties.TargetAmount.columnName ) );
					// Retrieve the achieved amount
					double achievedAmount = cursor.getDouble ( cursor.getColumnIndex ( TargetAchievementsDao.Properties.AmountAcheived.columnName ) );
					
					// Compute the money formatter pattern
					pattern = new StringBuilder ();
					pattern.append ( " #,##0" );
					// Check if the currency has a rounding value
					if ( currencyRounding >= 1 ) {
						pattern.append ( ".0" );
						for ( int i = 1 ; i < currencyRounding ; i ++ )
							pattern.append ( "0" );
					} // End if
					// Initialize a money format
					moneyFormat = new DecimalFormat ( pattern.toString () );
					
					// Retrieve the current target order amount per client per brand and add it to the list
					targetOrdersPerClientPerBrand.add ( new TargetOrdersPerClientPerBrand ( TargetActivity.this ,
							clientCode ,
							clientDescription ,
							itemCode ,
							itemDescription ,
							divisionCode ,
							divisionDescription ,
							currencyDescription ,
							requiredAmount ,
							achievedAmount ,
							moneyFormat ) );
				} while ( cursor.moveToNext () );
			} // End if
			// Close the cursor
			cursor.close ();
			cursor = null;
			
			// Retrieve the target items 
			// Compute the SQL query
			SQL = "SELECT DISTINCT TD." + TargetDetailsDao.Properties.TargetCode.columnName + " , " +
					"TD." + TargetDetailsDao.Properties.LineID.columnName + " , " +
					"C." + ClientsDao.Properties.ClientCode.columnName + " , " +
					"C." + ClientsDao.Properties.ClientName.columnName + " , " +
					"I." + ItemsDao.Properties.ItemCode.columnName + " , " +
					"I." + ItemsDao.Properties.ItemName.columnName + " , " +
					"U." + UnitsDao.Properties.UnitSmallDescription.columnName + " , " +
					"TD." + TargetDetailsDao.Properties.TargetAmount.columnName + " , " +
					"TD." + TargetDetailsDao.Properties.TargetUnit.columnName + " , " +
					"COALESCE ( TAC." + TargetAchievementsDao.Properties.AmountAcheived.columnName + " , 0 ) " + TargetAchievementsDao.Properties.AmountAcheived.columnName + " " +
					"FROM ( ( ( ( ( " + TargetHeadersDao.TABLENAME + " TH INNER JOIN " + TargetAssignmentsDao.TABLENAME + " TAS ON TH." + TargetHeadersDao.Properties.TargetCode.columnName + " = TAS." + TargetAssignmentsDao.Properties.TargetCode.columnName + " " +
					"AND ( TAS." + TargetAssignmentsDao.Properties.AssignmentType.columnName + " = ? OR TAS." + TargetAssignmentsDao.Properties.AssignmentType.columnName + " = ? ) ) " +
					"INNER JOIN " + TargetDetailsDao.TABLENAME + " TD ON TH." + TargetHeadersDao.Properties.TargetCode.columnName + " = TD." + TargetDetailsDao.Properties.TargetCode.columnName + " ) " +
					"INNER JOIN " + ClientsDao.TABLENAME + " C ON TD." + TargetDetailsDao.Properties.SubjectCode.columnName + " = C." + ClientsDao.Properties.ClientCode.columnName + " AND TH." + TargetHeadersDao.Properties.CompanyCode.columnName + " = C." + ClientsDao.Properties.CompanyCode.columnName + " " +
						"AND TD." + TargetDetailsDao.Properties.TargetDetailType.columnName + " = ? ) " +
					"INNER JOIN " + ItemsDao.TABLENAME + " I ON TD." + TargetDetailsDao.Properties.SubSubjectCode.columnName + " = I." + ItemsDao.Properties.ItemCode.columnName + " AND TH." + TargetHeadersDao.Properties.CompanyCode.columnName + " = I." + ItemsDao.Properties.CompanyCode.columnName + " " +
						"AND " + TargetDetailsDao.Properties.TargetDetailTypeSub.columnName + " = ? ) " +
					"INNER JOIN " + UnitsDao.TABLENAME + " U ON I." + ItemsDao.Properties.UnitCode.columnName + " = U." + UnitsDao.Properties.UnitCode.columnName + " ) " +
					"LEFT JOIN " + TargetAchievementsDao.TABLENAME + " TAC ON TAC." + TargetAchievementsDao.Properties.TargetCode.columnName + " = TD." + TargetDetailsDao.Properties.TargetCode.columnName + " AND " + "TAC." + TargetAchievementsDao.Properties.LineID.columnName + " = TD." + TargetDetailsDao.Properties.LineID.columnName + " AND " +
					"TAC." + TargetAchievementsDao.Properties.SubjectCode.columnName + " = TD." + TargetDetailsDao.Properties.SubSubjectCode.columnName + " AND TAC." + TargetAchievementsDao.Properties.TargetDetailType.columnName + " = TD." + TargetDetailsDao.Properties.TargetDetailTypeSub.columnName + " " +
					"WHERE ( ( TH." + TargetHeadersDao.Properties.StartDate.columnName + " IS NOT NULL AND ? > TH." + TargetHeadersDao.Properties.StartDate.columnName + " ) " +
					"AND ( TH." + TargetHeadersDao.Properties.EndDate.columnName + " IS NOT NULL AND ? < TH." + TargetHeadersDao.Properties.EndDate.columnName + " ) ) " +
					"AND TH." + TargetHeadersDao.Properties.TargetType.columnName + " = ? ";
			
			// Compute the selection arguments
			selectionArguments = new String [] {
				String.valueOf ( TargetAssignmentsUtils.Type.USER ) ,
				String.valueOf ( TargetAssignmentsUtils.Type.GROUP_USERS ) ,
				String.valueOf ( TargetHeadersUtils.SubType.CLIENT ) ,
				String.valueOf ( TargetHeadersUtils.SubType.ITEM ) ,
				String.valueOf ( today ) ,
				String.valueOf ( today ) ,
				String.valueOf ( TargetHeadersUtils.Type.TOTAL_SALES_PER_CLIENT_PER_SKU_VOLUME )
			};
			
			// Query DB in order to retrieve the target items
			cursor = DatabaseUtils.getInstance ( TargetActivity.this ).getDaoSession ().getDatabase ()
					.rawQuery ( SQL , selectionArguments );
			// Initialize a list used to host the target items
			targetItems = new ArrayList < TargetItems > ();
			// Move the cursor to the first row
			if ( cursor.moveToFirst () == true ) {
				do {
					// Retrieve the client code
					String clientCode = cursor.getString ( cursor.getColumnIndex ( ClientsDao.Properties.ClientCode.columnName ) );
					// Check if the call is provided and if the client codes match
					if ( call != null && ! call.getClient ().getClientCode ().equals ( clientCode ) )
						// Skip the current record
						continue;
					// Retrieve the client description
					String clientDescription = cursor.getString ( cursor.getColumnIndex ( ClientsDao.Properties.ClientName.columnName ) );
					// Retrieve the item code
					String itemCode = cursor.getString ( cursor.getColumnIndex ( ItemsDao.Properties.ItemCode.columnName ) );
					// Retrieve the client description
					String itemDescription = cursor.getString ( cursor.getColumnIndex ( ItemsDao.Properties.ItemName.columnName ) );
					// Retrieve the unit small description
					String unitSmallDescription = cursor.getString ( cursor.getColumnIndex ( UnitsDao.Properties.UnitSmallDescription.columnName ) );
					// Retrieve the required number
					int requiredNumber = cursor.getInt ( cursor.getColumnIndex ( TargetDetailsDao.Properties.TargetAmount.columnName ) );
					// Retrieve the achieved number
					int achievedNumber = cursor.getInt ( cursor.getColumnIndex ( TargetAchievementsDao.Properties.AmountAcheived.columnName ) );
					// Retrieve the current target item data and add it to the list
					targetItems.add ( new TargetItems ( TargetActivity.this , 
							clientCode , 
							clientDescription , 
							itemCode , 
							itemDescription , 
							unitSmallDescription ,
							requiredNumber ,
							achievedNumber ) );
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
	    		// Retrieve a reference of the view pager (which has the app fragment pager adapter linked to)
	    		// The view pager is used to retrieve a reference to the daily fragment, via its tag
	    		ViewPager viewPager = (ViewPager) findViewById ( R.id.pager );
	    		// Retrieve a reference to the app fragment pager adapter
	    		// This adapter can compute the fragment tag so it can be referenced (via its tag)
	    		AppFragmentPagerAdapter adapter = viewPager != null ? (AppFragmentPagerAdapter) viewPager.getAdapter () : null;
	    		
	    		// Set all target lists
	    		setTargetVisits ( targetVisits );
	    		setTargetCollections ( targetCollections );
	    		setTargetOrders ( targetOrders );
	    		setTargetOrdersPerClient ( targetOrdersPerClient );
	    		setTargetOrdersPerClientPerBrand ( targetOrdersPerClientPerBrand );
	    		setTargetItems ( targetItems );
	    		
				// Iterate over all fragments
				for ( int i = 0 ; i < TargetActivity.this.fragments.size () ; i ++ ) {
		    		// Compute the section (having index i) tag
		    		String tag = adapter != null ? adapter.getFragmentName ( viewPager , i ) : null;
		    		// Retrieve a reference to the fragment based on its tag
		    		Object fragment = getSupportFragmentManager ().findFragmentByTag ( tag == null ? "" : tag );
		    		// Check if the fragment is valid
		    		if ( fragment == null )
		    			// Invalid fragment, skip it
		    			continue;
		    		
		    		// Determine if the fragment is Visits Frequency
		    		if ( fragment instanceof VisitsFrequencyFragment )
		    			// Set the visits frequency adapter
		    			( (VisitsFrequencyFragment) fragment ).setListAdapter ( getTargetVisitsAdapter ( targetVisits ) );
		    		// Determine if the fragment is Collection Amount
		    		else if ( fragment instanceof CollectionAmountFragment )
		    			// Set the collection amount adapter
		    			( (CollectionAmountFragment) fragment ).setListAdapter ( getTargetCollectionAmountsAdapter ( targetCollections ) );
		    		// Determine if the fragment is Order Amount
		    		else if ( fragment instanceof OrderAmountFragment )
		    			// Set the order amount adapter
		    			( (OrderAmountFragment) fragment ).setListAdapter ( getTargetOrderAmountsAdapter ( targetOrders ) );
		    		// Determine if the fragment is Order Amount Per Client
		    		else if ( fragment instanceof OrderAmountPerClientFragment )
		    			// Set the order amount per client adapter
		    			( (OrderAmountPerClientFragment) fragment ).setListAdapter ( getTargetOrderAmountsPerClientAdapter ( targetOrdersPerClient ) );
		    		// Determine if the fragment is Order Amount Per Client
		    		else if ( fragment instanceof OrderAmountPerClientPerBrandFragment )
		    			// Set the order amount per client adapter
		    			( (OrderAmountPerClientPerBrandFragment) fragment ).setListAdapter ( getTargetOrderAmountsPerClientPerBrandAdapter ( targetOrdersPerClientPerBrand ) );
		    		// Determine if the fragment is Items Quantity 
		    		else if ( fragment instanceof ItemsQuantityFragment )
		    			// Set the items quantity adapter
		    			( (ItemsQuantityFragment) fragment ).setListAdapter ( getTargetItemsAdapter ( targetItems ) );
		    		
				} // End for loop
			} catch ( Exception exception ) {
				// Do nothing
			} finally {
				// Clear variables
				this.targetVisits = null;
				this.targetCollections = null;
				this.targetOrders = null;
				this.targetOrdersPerClient = null;
				this.targetOrdersPerClientPerBrand = null;
				this.targetItems = null;
			} // End of try-catch block
		}
		
	}
	
}