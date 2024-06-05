/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Journey;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.ClientDivisions;
import me.SyncWise.Android.Database.Clients;
import me.SyncWise.Android.Database.CompaniesDao;
import me.SyncWise.Android.Database.CycleCalls;
import me.SyncWise.Android.Database.CycleCallsDao;
import me.SyncWise.Android.Database.DaoSession;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.DivisionsDao;
import me.SyncWise.Android.Database.JourneysUtils;
import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Database.Reasons;
import me.SyncWise.Android.Database.ReasonsDao;
import me.SyncWise.Android.Database.ReasonsUtils;
import me.SyncWise.Android.Database.StatusUtils;
import me.SyncWise.Android.Database.SurveyAnswersDao;
import me.SyncWise.Android.Database.SurveysUtils;
import me.SyncWise.Android.Database.TransactionHeaders;
import me.SyncWise.Android.Database.TransactionHeadersDao;
import me.SyncWise.Android.Database.Visits;
import me.SyncWise.Android.Database.VisitsDao;
import me.SyncWise.Android.Widgets.Baguette;
import me.SyncWise.Android.Widgets.QuickAction.ActionItem;
import me.SyncWise.Android.Widgets.QuickAction.QuickAction;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.widget.SearchView;
import android.widget.TextView;

/**
 * Abstract fragment used to represent a modular section for the journey activity.<br>
 * In order to implement a journey fragment, simply extend this class.
 * 
 * @author Elias
 *
 */
public abstract class JourneyFragment extends ListFragment implements SearchView.OnQueryTextListener , QuickAction.OnActionItemClickListener {

	/**
	 * Constant integer holding the request code used to start an activity for result to perform a client call.
	 */
	public static final int REQUEST_CODE_VISIT = 1;
	
	/**
	 * Constant integer holding the request code used to start an activity for result to cancel a client call.
	 */
	public static final int REQUEST_CODE_CANCEL = 2;
	
	/**
	 * Constant integer holding the request code used to start an activity for result to collect from a client.
	 */
	public static final int REQUEST_CODE_COLLECTION = 3;
	
	/**
	 * Constant integer holding the request code used to print orders.
	 */
	public static final int REQUEST_CODE_NEW_CLIENT = 4;
	
	/**
	 * Reference to a {@link me.SyncWise.Android.Widgets.QuickAction.QuickAction QuickAction} used in this fragment.<br>
	 * This widget is used for unvisited clients.
	 */
	protected QuickAction quickAction_Unvisited;
	
	/**
	 * Reference to a {@link me.SyncWise.Android.Widgets.QuickAction.QuickAction QuickAction} used in this fragment.<br>
	 * This widget is used for visited clients.
	 */
	protected QuickAction quickAction_Visited;
	
	/**
	 * Reference to the {@link android.widget.SearchView SearchView} displayed in the menu / action bar (if any).<br>
	 * This reference is set in the {@link #setupSearchView(SearchView)} method.
	 */
	protected SearchView searchView;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #searchQuery}.
	 */
	private static final String SEARCH_QUERY = JourneyFragment_Daily.class.getName () + ".SEARCH_QUERY";
	
	/**
	 * String holding the search query.
	 */
	private String searchQuery;
	
	/**
	 * Setter - {@link #searchQuery}
	 * 
	 * @param searchQuery	String holding the search query.
	 */
	public void setSearchQuery ( String searchQuery ) {
		// Set the search query
		this.searchQuery = searchQuery;
	}
	
	/**
	 * Getter - {@link #searchQuery}
	 * 
	 * @return	String holding the search query.
	 */
	public String getSearchQuery () {
		// Return the current search query
		return searchQuery;
	}
	
	/**
	 * Helper Class used to manage the fragment's action items IDs for the quick action widgets.<br>
	 * The main purpose of this class is to maintain a unique identifier for action items within the fragment.
	 * 
	 * @author Elias
	 *
	 */
	protected class ActionItemID {
		public static final int VISIT = 0;
		public static final int CALL = 1;
		public static final int CANCEL = 2;
		public static final int INFO = 3;
	}
	
	/*
	 * Called to do initial creation of a fragment.
	 *
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate ( Bundle savedInstanceState ) {
		// Superclass method call
	    super.onCreate ( savedInstanceState );
	    // Report that this fragment would like to participate in populating the options menu
        setHasOptionsMenu ( true );
        // Perform the quick action setup
        setupQuickAction ();
	}
	
	/**
	 * Performs all necessary setup in order to properly display the quick action widget.
	 */
    private void setupQuickAction () {
		// Initialize the quick action widgets
		quickAction_Visited = new QuickAction ( getActivity () , QuickAction.VERTICAL );
		quickAction_Unvisited = new QuickAction ( getActivity () , QuickAction.VERTICAL );
		// Action Item : Visit
		ActionItem visit = new ActionItem ( ActionItemID.VISIT ,
				AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.quick_action_visit_label ) ,
				getResources ().getDrawable ( R.drawable.quick_action_ok ) );
		// Declare and initialize an action item used to perform a call
		ActionItem call = null;
		// Determine if a visit and a call should be differentiated
		if ( PermissionsUtils.getVisitCallDifference ( getActivity () , DatabaseUtils.getCurrentUserCode ( getActivity () ) , DatabaseUtils.getCurrentCompanyCode ( getActivity () ) ) )
		// Action Item : Call
			call = new ActionItem ( ActionItemID.CALL ,
					AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.quick_action_call_label ) ,
					getResources ().getDrawable ( R.drawable.quick_action_ok ) );
		// Action Item : Cancel
		ActionItem cancel = new ActionItem ( ActionItemID.CANCEL ,
				AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.quick_action_cancel_visit_label ) ,
				getResources ().getDrawable ( R.drawable.quick_action_cancel ) );
		// Action Item : Info
		ActionItem clientInfo = new ActionItem ( ActionItemID.INFO ,
				AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.quick_action_client_info_label ) ,
				getResources ().getDrawable ( R.drawable.info ) );
    	// Populate the quick action widget with quick action items
		quickAction_Visited.addActionItem ( visit );
		if ( call != null )
			quickAction_Visited.addActionItem ( call );
		quickAction_Visited.addActionItem ( clientInfo );
		quickAction_Unvisited.addActionItem ( visit );
		if ( call != null )
			quickAction_Unvisited.addActionItem ( call );
		quickAction_Unvisited.addActionItem ( cancel );
		quickAction_Unvisited.addActionItem ( clientInfo );
		// Assign an action item click listener
		quickAction_Unvisited.setOnActionItemClickListener ( this );
		quickAction_Visited.setOnActionItemClickListener ( this );
    }
	
    /*
     * Called when the fragment's activity has been created and this fragment's view hierarchy instantiated.
     *
     * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
     */
	@Override
	public void onActivityCreated ( Bundle savedInstanceState ) {
		// Superclass method call
		super.onActivityCreated ( savedInstanceState );
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null )
			// Restore the content provided by the saved instance state 
			searchQuery = savedInstanceState.getString ( SEARCH_QUERY );
		// Initialize the search query (if not initialized)
		if ( searchQuery == null )
			searchQuery = "";
		// Enable fast scrolling
		getListView ().setFastScrollEnabled ( true );
		// Define the empty list view
		getListView ().setEmptyView ( getView ().findViewById ( R.id.empty_list_view ) );
		// Display empty list label
		( (TextView) getView ().findViewById ( R.id.empty_list_view ) ).setText ( AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.empty_visits_list_label ) );
	}
	
	/*
	 * Called to ask the fragment to save its current dynamic state, so it can later be reconstructed in a new instance of its process is restarted.
	 *
	 * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
    public void onSaveInstanceState ( Bundle outState ) {
    	// Superclass method call
    	super.onSaveInstanceState ( outState );
    	// Save the content of searchQuery in the outState bundle
    	outState.putString ( SEARCH_QUERY , searchQuery );
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
		searchView.setQueryHint ( AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.search_label ) );
		// Set a query listener to apply search filters
		searchView.setOnQueryTextListener ( this );
		// Disable showing a submit button when the query is non-empty
		searchView.setSubmitButtonEnabled ( false );
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
	
	/**
	 * Displays a dialog used to prompt the user for a reason for no client barcode.
	 * Once the reason selection is successful, the visit starts.
	 * 
	 * @param call	The client call to perform.
	 * @param enforceVisitType	Boolean indicating the visit type (difference between visit and call) should be enforced or not.
	 * @param visitType	Integer holding the visit type (action ID).
	 */
	protected void displayBarcodeReason ( final Call call , final boolean enforceVisitType , final int visitType ) {
		try {
			// Retrieve all the no client barcode reasons
			final List < Reasons > reasons = DatabaseUtils.getInstance ( getActivity() ).getDaoSession ().getReasonsDao ().queryBuilder ()
					.where ( ReasonsDao.Properties.ReasonType.eq ( ReasonsUtils.Type.NO_CLIENT_BARCODE ) )
					.orderAsc ( ReasonsDao.Properties.ReasonName ).list ();
			// Check if the list is valid
			if ( reasons.isEmpty () ) {
				// Invalid list
				// Display warning
				displayNoBarcodeWarning ();
				// Exit method
				return;
			} // End if
			// Retrieve the reasons names in an array
			String companyNames [] = new String [ reasons.size () ];
			for ( int i = 0 ; i < companyNames.length ; i ++ )
				companyNames [ i ] = reasons.get ( i ).getReasonName ();
			// Prompt the user to choose a reason
			AppDialog.getInstance ().displayList ( getActivity () ,
					AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.no_barcode_reasons_list_dialog_title ) ,
					companyNames ,
					AppDialog.Cancelable.BACK_BUTTON_AND_TOUCH ,
					new DialogInterface.OnClickListener () {
						@Override
						public void onClick ( DialogInterface dialog , int which ) {
							// Start a new visit without barcode using the selected reason
							startVisit ( call , enforceVisitType , visitType , reasons.get ( which ) );
						}
					} , null );
		} catch ( Exception exception) {
			// Display warning
			displayNoBarcodeWarning ();
		} // End of try-catch block
	}
	
	/**
	 * Displays a warning baguette in order to indicate to the user that there are no client barcode reasons.
	 */
	private void displayNoBarcodeWarning () {
		try {
			// Display baguette message
			Baguette.showText ( getActivity () , AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.no_client_barcode_reasons_message ) , Baguette.BackgroundColor.RED );
		} catch ( Exception exception ) {
			// Do nothing
		} // End of try-catch block
	}
	
	/**
	 * Gets the calls list for the indicated journey day.
	 * 
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @param prefixID	Integer hosting the user prefix ID.
	 * @param week	Integer hosting the week number.
	 * @param day	{@link me.SyncWise.Android.Modules.Journey.Day Day} object representing the current journey day.
	 * @param calendar	{@link java.util.Calendar Calendar} object representing the current journey day.
	 * @param journeyCode_today	String hosting the journey code for <b>today</b>'s journey.
	 * @return	List of the appropriate {@link me.SyncWise.Android.Modules.Journey.Call Call} objects related to the provided journey day. 
	 */
	@SuppressLint("DefaultLocale") 
	protected ArrayList < Call > getCalls ( final String userCode , final String companyCode , final int prefixID , final int week , final Day day , final Calendar calendar , final String journeyCode_today ) {
		// Declare and initialize a cursor used to retrieve data sets from query results
		Cursor cursor = null;
		
		// Retrieve a reference to the dao session
		DaoSession daoSession = DatabaseUtils.getInstance ( getActivity () ).getDaoSession ();
		// Compute the day's journey code
		String journeyCode = JourneysUtils.getJourneyCode ( prefixID , calendar );
		// Declare and initialize the list of calls for the current day
		ArrayList < Call > calls = new ArrayList < Call > ();
		// Query from the database all the cycle calls for the provided day and week, along with the appropriate clients and client divisions
		cursor = daoSession.getDatabase ().rawQuery ( 
				"SELECT CC.* , C.* , CD.* , CO.CompanyName , D.DivisionName FROM CycleCalls CC " +
				"INNER JOIN Clients C ON CC.ClientCode = C.ClientCode AND CC.CompanyCode = C.CompanyCode " +
				"INNER JOIN ClientDivisions CD ON CC.ClientCode = CD.ClientCode AND CC.CompanyCode = CD.CompanyCode AND CC.DivisionCode = CD.DivisionCode " +
				"INNER JOIN Companies CO ON C.CompanyCode = CO.CompanyCode " +
				"INNER JOIN Divisions D ON D.DivisionCode = CD.DivisionCode AND D.CompanyCode = CD.CompanyCode " +
				"WHERE CC.CycleCallStatus = ? AND CC.Day = ? AND CC.Week = ? " +
				"ORDER BY CC.Sequence" , 
				new String [] { String.valueOf ( StatusUtils.isAvailable () ) , day.getDay () , String.valueOf ( week ) } );
		// Compute the columns offsets
		int cycleCallOffset = 0;
		int clientOffset = daoSession.getCycleCallsDao ().getAllColumns ().length;
		int clientDivisionOffset = clientOffset + daoSession.getClientsDao ().getAllColumns ().length;
		// Move the cursor to the first row
		if ( cursor.moveToFirst () == true ) {
			do {
				// Retrieve the cycle call, client and client division objects
				CycleCalls cycleCall = daoSession.getCycleCallsDao ().readEntity ( cursor , cycleCallOffset );
				Clients client = daoSession.getClientsDao ().readEntity ( cursor , clientOffset );
				ClientDivisions clientDivision = daoSession.getClientDivisionsDao ().readEntity ( cursor , clientDivisionOffset );
				// Retrieve the company and division names
				String companyName = cursor.getString ( cursor.getColumnIndex ( CompaniesDao.Properties.CompanyName.columnName ) );
				String divisionName = cursor.getString ( cursor.getColumnIndex ( DivisionsDao.Properties.DivisionName.columnName ) );
				// Create a new journey call using the current client
				Call call = new Call ( cycleCall , client , clientDivision );
				// Set the company and division names
				client.setCompanyName ( companyName );
				client.setDivisionCode ( clientDivision.getDivisionCode () );
				client.setDivisionName ( divisionName );
				// Specify the journey code for the today
				call.setJourneyCode ( journeyCode_today );
				// Specify if the current call is scheduled or not
				call.setScheduled ( day.isToday () );
				// Specify the current call day
				call.setDay ( day.getDay () );
				// Add the client to the list of calls
				calls.add ( call );
			} while ( cursor.moveToNext () );
		} // End if
		// Close the cursor
		cursor.close ();
		cursor = null;

		// Declare and initialize the map of barcodes for the calls for the current day
		HashMap < String , ArrayList < String > > clientBarcodes = new HashMap < String , ArrayList < String > > ();
		// Query from the database all the barcodes of the cycle calls for the provided day and week
		cursor = daoSession.getDatabase ().rawQuery ( 
				"SELECT CB.ClientCode || '--' || CB.DivisionCode || '--' || CB.CompanyCode , CB.ClientBarcode FROM CycleCalls CC " +
				"INNER JOIN ClientBarcodes CB ON CC.ClientCode = CB.ClientCode AND CC.CompanyCode = CB.CompanyCode AND CC.DivisionCode = CB.DivisionCode " +
				"WHERE CC.CycleCallStatus = ? AND CC.Day = ? AND CC.Week = ? AND CB.ClientBarcode IS NOT NULL " , 
				new String [] { String.valueOf ( StatusUtils.isAvailable () ) , day.getDay () , String.valueOf ( week ) } );
		// Move the cursor to the first row
		if ( cursor.moveToFirst () == true ) {
			do {
				// Retrieve the barcode key and barcode string
				String barcodeKey = cursor.getString ( 0 );
				String barcodeValue = cursor.getString ( 1 ).toLowerCase ();
				// Validate the barcode
				if ( barcodeValue.trim ().isEmpty () )
					// Invalid barcode value
					continue;
				// Map the barcode
				ArrayList < String > list = null;
				if ( clientBarcodes.containsKey ( barcodeKey ) ) {
					list = clientBarcodes.get ( barcodeKey );
					list.add ( barcodeValue );
				} // End if
				else {
					list = new ArrayList < String > ();
					list.add ( barcodeValue );
					clientBarcodes.put ( barcodeKey , list );
				} // End else
			} while ( cursor.moveToNext () );
		} // End if
		// Close the cursor
		cursor.close ();
		cursor = null;
		
		// Retrieve all the performed visits during the current journey for the current list of clients
		List < Visits > _visits = DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getVisitsDao ().queryRaw ( 
				"WHERE " + VisitsDao.Properties.JourneyCode.columnName + "=? AND " + VisitsDao.Properties.ClientCode.columnName + " IN ( " +
					"SELECT " + CycleCallsDao.Properties.ClientCode.columnName + " FROM " + CycleCallsDao.TABLENAME + " " +
					"WHERE " + CycleCallsDao.Properties.CycleCallStatus.columnName + "=? " +
					"AND " + CycleCallsDao.Properties.Day.columnName + "=? " +
					"AND " + CycleCallsDao.Properties.Week.columnName + "=? )" ,
				new String [] { journeyCode ,
						String.valueOf ( StatusUtils.isAvailable () ) ,
						day.getDay () ,
						String.valueOf ( week ) } );
		// Map the client visits by their client codes
		Map < String , List < Visits > > visits = new HashMap < String , List<Visits> > ();
		// Iterate over all visits
		for ( Visits visit : _visits )
			// Determine if the current client has already mapped visits
			if ( visits.containsKey ( visit.getClientCode () ) )
				// Add the visit to the list
				visits.get ( visit.getClientCode () ).add ( visit );
			// Otherwise the current client has no mapped visits yet
			else {
				// Map the client visit to its client code
				visits.put ( visit.getClientCode () , new ArrayList < Visits > () );
				visits.get ( visit.getClientCode () ).add ( visit );
			} // End else

		// Retrieve all the performed sales operations during the current journey for the current list of clients
		List < TransactionHeaders > transactionHeaders = DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getTransactionHeadersDao ().queryRaw (
				"WHERE " + TransactionHeadersDao.Properties.JourneyCode.columnName + "=? AND " + TransactionHeadersDao.Properties.ClientCode.columnName + " IN ( " +
					"SELECT " + CycleCallsDao.Properties.ClientCode.columnName + " FROM " + CycleCallsDao.TABLENAME + " " +
					"WHERE " + CycleCallsDao.Properties.CycleCallStatus.columnName + "=? " +
					"AND " + CycleCallsDao.Properties.Day.columnName + "=? " +
					"AND " + CycleCallsDao.Properties.Week.columnName + "=? )" ,
				new String [] { journeyCode ,
						String.valueOf ( StatusUtils.isAvailable () ) ,
						day.getDay () ,
						String.valueOf ( week ) } );
		// Declare and initialize a set of longs used to host the visit IDs having transactions
		Set < Long > transactionHeaderVisitIDs = new HashSet < Long > ();
		// Iterate over all transaction headers
		for ( TransactionHeaders transactionHeader : transactionHeaders )
			// Add the visit ID to the set
			transactionHeaderVisitIDs.add ( transactionHeader.getVisitID () );

		// Retrieve the client codes having survey answers during the current journey
		cursor = DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getDatabase ().rawQuery (
				"SELECT " + SurveyAnswersDao.Properties.ClientCode.columnName + " FROM " + SurveyAnswersDao.TABLENAME + " " +
				"WHERE " + SurveyAnswersDao.Properties.SurveyType.columnName + "=? AND " + SurveyAnswersDao.Properties.UserCode.columnName + "=? " +
				"AND " + SurveyAnswersDao.Properties.CompanyCode.columnName + "=? AND " + SurveyAnswersDao.Properties.VisitID.columnName + " IS NOT NULL " +
				"AND " + SurveyAnswersDao.Properties.VisitID.columnName + " IN ( SELECT " + VisitsDao.Properties.VisitID.columnName + " " +
				"FROM " + VisitsDao.TABLENAME + " WHERE " + VisitsDao.Properties.JourneyCode.columnName + "=? ) GROUP BY " + SurveyAnswersDao.Properties.ClientCode.columnName ,
				new String [] {
						String.valueOf ( SurveysUtils.Type.CLIENT_SURVEY ) ,
						userCode ,
						companyCode ,
						journeyCode
				} );
		// Declare and initialize a set of strings used to host the client codes having survey answers during the current journey
		Set < String > surveyAnswerClientCodes = new HashSet < String > ();
		// Move the cursor to the first raw
		if ( cursor.moveToFirst () )
			// Iterate over the data set
			do {
				// Add the client code to the set
				surveyAnswerClientCodes.add ( cursor.getString ( 0 ) );
			} while ( cursor.moveToNext () );
		// Close the cursor
		cursor.close ();
		cursor = null;

		// Iterate over all calls
		for ( Call call : calls ) {
			// Add the barcodes
			ArrayList < String > barcode = clientBarcodes.get ( call.getClient ().getClientCode () + "--" + call.getClientDivision ().getDivisionCode () + "--" + call.getClient ().getCompanyCode () );
			if ( barcode != null && ! barcode.isEmpty () )
				call.setClientBarcodes ( barcode );
			// Check if the current client has performed visits
			if ( ! visits.containsKey ( call.getClient ().getClientCode () ) )
				// Skip the current call
				continue;
			// Otherwise the current client has performed visits
			boolean isVisited = false;
			boolean isCancelled = false;
			boolean isSales = false;
			// Iterate over all the performed visits
			for ( Visits visit : visits.get ( call.getClient ().getClientCode () ) )
				// Determine if the visit is cancelled
				if ( ! TextUtils.isEmpty ( visit.getVisitReasonCode () ) )
					// The current visit is cancelled
					isCancelled = true;
				// Otherwise the visit is not cancelled
				else {
					// The visit might be blank, sales, ...
					isVisited = true;
					// Check if the visit has related transactions
					if ( transactionHeaderVisitIDs.contains ( visit.getVisitID () ) )
						// The visit is a sales one
						isSales = true;
				} // End else
			// Update the call
			call.setCancelled ( isCancelled );
			call.setVisited ( isVisited );
			call.setSales ( isSales );
			call.setSurvey ( surveyAnswerClientCodes.contains ( call.getClient ().getClientCode () ) );
		} // End for each
		
		// Check if the day is today for the daily visits
		if ( day.isToday () && this instanceof JourneyFragment_Daily ) {
			// Query the user account
			cursor = DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getDatabase ().rawQuery (
					"SELECT NULL , -1 , C.ClientCode , '" + day.getDay () + "' , " + week + " , CD.DivisionCode , C.CompanyCode , -1 , NULL , 1 , 2 , strftime('%s','now') , " +
							"C.* , CD.* , CO.CompanyName , D.DivisionName FROM Clients C " +
							"INNER JOIN UserAccountNumbers UAN ON C.ClientCode = UAN.AccountNumber AND C.CompanyCode = UAN.CompanyCode " +
							"INNER JOIN ClientDivisions CD ON C.ClientCode = CD.ClientCode AND C.CompanyCode = CD.CompanyCode AND UAN.DivisionCode = CD.DivisionCode " +
							"INNER JOIN Companies CO ON C.CompanyCode = CO.CompanyCode " +
							"INNER JOIN Divisions D ON D.DivisionCode = CD.DivisionCode AND D.CompanyCode = CD.CompanyCode " , new String [] {} );
			// Move the cursor to the first raw
			if ( cursor.moveToFirst () ) {
				// Retrieve the cycle call, client and client division objects
				CycleCalls cycleCall = daoSession.getCycleCallsDao ().readEntity ( cursor , cycleCallOffset );
				Clients client = daoSession.getClientsDao ().readEntity ( cursor , clientOffset );
				ClientDivisions clientDivision = daoSession.getClientDivisionsDao ().readEntity ( cursor , clientDivisionOffset );
				// Retrieve the company and division names
				String companyName = cursor.getString ( cursor.getColumnIndex ( CompaniesDao.Properties.CompanyName.columnName ) );
				String divisionName = cursor.getString ( cursor.getColumnIndex ( DivisionsDao.Properties.DivisionName.columnName ) );
				// Create a new journey call using the current client
				Call call = new Call ( cycleCall , client , clientDivision );
				// Set the company and division names
				client.setCompanyName ( companyName );
				client.setDivisionCode ( clientDivision.getDivisionCode () );
				client.setDivisionName ( divisionName );
				// Specify the journey code for the today
				call.setJourneyCode ( journeyCode_today );
				// Specify if the current call is scheduled or not
				call.setScheduled ( true );
				// Specify the current call day
				call.setDay ( day.getDay () );
				// Update the call flags
				call.setVisited ( true );
				call.setSales ( true );
				call.setSurvey ( true );
				call.setUseTimePasscode ( true );
				// Add the client to the list of calls
				calls.add ( 0 , call );
			} // End if
			// Close the cursor
			cursor.close ();
			cursor = null;
		} // End if
		
		// Return the calls list
		return calls;
	}
	
	/**
	 * Starts a client visit.
	 * 
	 * @param call	The client call to perform.
	 * @param enforceVisitType	Boolean indicating the visit type (difference between visit and call) should be enforced or not.
	 * @param visitType	Integer holding the visit type (action ID).
	 * @param reason	If the visit was initiated from a barcode scan use {@code NULL}, or the user reason otherwise.
	 */
	protected abstract void startVisit ( final Call call , final boolean enforceVisitType , final int visitType , final Reasons reason );
	
	/**
	 * Performs initialize to perform a visit.
	 * 
	 * @param call	The client call to perform.
	 * @param enforceVisitType	Boolean indicating the visit type (difference between visit and call) should be enforced or not.
	 * @param visitType	Integer holding the visit type (action ID).
	 * @param barcodeReason	A no client barcode reason if the user did not scan a barcode to initiate a client call, or {@code NULL} otherwise.
	 * @param skipChecking	Boolean used to indicate if the in route calls checking is to be skipped.
	 */
	protected abstract void preVisitClientCall ( final Call call , final boolean enforceVisitType , final int visitType , final Reasons barcodeReason , final boolean skipChecking );
	
}