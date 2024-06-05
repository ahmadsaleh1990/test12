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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.SyncWise.Android.ActivityTransition;
import me.SyncWise.Android.App;
import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppFragmentPagerAdapter;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.AppToast;
import me.SyncWise.Android.DateTime;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.ClientDivisions;
import me.SyncWise.Android.Database.ClientDivisionsDao;
import me.SyncWise.Android.Database.ClientUserCollection;
import me.SyncWise.Android.Database.ClientUserCollectionDao;
import me.SyncWise.Android.Database.Clients;
import me.SyncWise.Android.Database.ClientsDao;
import me.SyncWise.Android.Database.CompaniesDao;
import me.SyncWise.Android.Database.CursorUtils;
import me.SyncWise.Android.Database.CycleCallsDao;
import me.SyncWise.Android.Database.DaoSession;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.Divisions;
import me.SyncWise.Android.Database.DivisionsDao;
import me.SyncWise.Android.Database.DivisionsUtils;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.JourneysUtils;
import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Database.Reasons;
import me.SyncWise.Android.Database.ReasonsUtils;
import me.SyncWise.Android.Database.StatusUtils;
import me.SyncWise.Android.Database.SurveyAnswersDao;
import me.SyncWise.Android.Database.SurveysUtils;
import me.SyncWise.Android.Database.TargetHeadersUtils;
import me.SyncWise.Android.Database.TransactionHeaders;
import me.SyncWise.Android.Database.TransactionHeadersDao;
import me.SyncWise.Android.Database.UserAccountNumbersDao;
import me.SyncWise.Android.Database.Users;
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Database.VisitReasons;
import me.SyncWise.Android.Database.VisitTypes;
import me.SyncWise.Android.Database.VisitTypesUtils;
import me.SyncWise.Android.Database.Visits;
import me.SyncWise.Android.Database.VisitsDao;
import me.SyncWise.Android.Database.VisitsUtils;
import me.SyncWise.Android.Modules.Call.CallAction;
import me.SyncWise.Android.Modules.Call.CallMenuActivity;
import me.SyncWise.Android.Modules.ClientsList.ClientsCursorAdapter;
import me.SyncWise.Android.Modules.ClientsList.ClientsCursorAdapter.ViewHolder;
import me.SyncWise.Android.Modules.ClientsList.ClientInfo.ClientInfoActivity;
import me.SyncWise.Android.Modules.Collections.CollectionDetailsActivity;


import me.SyncWise.Android.Modules.Target.TargetUpdate;
import me.SyncWise.Android.SectionsList.MultipleListAdapter;
import me.SyncWise.Android.SectionsList.Section;
import me.SyncWise.Android.Utilities.Cycle;
import me.SyncWise.Android.Widgets.Baguette;
import me.SyncWise.Android.Widgets.QuickAction.QuickAction;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

/**
 * Fragment used to represent a modular section for out of route visits.
 * 
 * @author Elias -ahmad
 *
 */
public class JourneyFragment_OutOfRoute extends JourneyFragment {

	/**
	 * Bundle key used to put/retrieve data.
	 */
	public static final String LAYOUT = JourneyFragment_OutOfRoute.class.getName () + ".LAYOUT";
	
	/**
	 * Map holding lists of {@link me.SyncWise.Android.Modules.Journey.Call Call} objects keyed to their corresponding day, used to define the out of route calls.<br>
	 * <b>NOTE :</b> <br>
	 * If a filter is applied, this map remains untouched. New lists are generated, filtered and passed to the list adapters.
	 */
	private LinkedHashMap < Day , ArrayList < Call > > calls;
	
	/**
	 * Reference to the calls list population task.
	 */
	private static PopulateList populateList;
	
	/*
	 * Called to have the fragment instantiate its user interface view.
	 *
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
    @Override
    public View onCreateView ( LayoutInflater inflater , ViewGroup container , Bundle savedInstanceState ) {
    	// Inflate and return the fragment's view
    	return inflater.inflate ( getArguments ().getInt ( LAYOUT ) , container , false );
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
		
		// Retrieve all the daily calls asynchronously
		refresh ();
	}
	
	/**
	 * Refreshes the list view by re-populating it.
	 */
	public void refresh () {
		// Retrieve all the daily calls asynchronously
		populateList = new PopulateList ();
		populateList.execute ();
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
    }
	
	/*
	 * Initialize the contents of the Activity's standard options menu.
	 *
	 * @see android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu ( Menu menu , MenuInflater menuInflater ) {
		// Superclass method call
		super.onCreateOptionsMenu ( menu , menuInflater );
		// Check if the item clicks are enabled
		if ( ! ( (JourneyActivity) getActivity () ).isClickEnabled () )
			// Hide menu
			return;
		
		// Inflate a menu hierarchy from the specified XML resource
		menuInflater.inflate ( R.menu.action_bar , menu );
		// Enable the required menu items
		me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_search ) );
		setupSearchView ( (SearchView) menu.findItem ( R.id.action_search ).getActionView () );
		// Check if there is a search query
		if ( ! getSearchQuery ().isEmpty () ) {
			// Set the new query
			searchView.setQuery ( getSearchQuery () , false );
			// Expand the SearchView
			searchView.setIconified ( false );
		} // End if
	}

	/**
	 * Gets the matching out of route call.<br>
	 * This is mainly used in order to retrieve the flag states of the call (is sales, objective, survey ...)
	 * 
	 * @param call	The client call to perform as out of route.
	 * @return	The performed out of route client call, or {@code NULL} if no previous out of route calls were made.
	 */
	public Call getOutOfRouteCall ( final Call call ) {
		// Check if the calls map is valid
		if ( calls == null || calls.isEmpty () )
			// Invalid map
			return null;
		
		// Otherwise the map is valid
		// Iterate over the map
		for ( Day day : calls.keySet () )
			// Determine if the current day is today
			if ( day.isToday () )
				// Iterate over the calls of the current day
				for ( Call _call : calls.get ( day ) )
					// Match both calls
					if ( _call.getClient ().getClientCode ()
							.equals ( call.getClient ().getClientCode () ) )
						// Return the call
						return _call;
				
		// Otherwise the call is not previously performed
		return null;
	}
	
	/**
	 * Computes an appropriate cursor based on the provided filter.
	 * 
	 * @param	prefixID	Integer holding the user's prefix ID.
	 * @param	week	String hosting the week number.
	 * @param	day		String hosting the day number.
	 * @return	Cursor	Cursor holding a result set returned by a database query.
	 */
	@SuppressLint("DefaultLocale") 
	private Cursor computeCursor ( final int prefixID , final String week , final String day ) {
		// Declare and initialize a string used to host the search query (part of the selection clause)
		String searchQuery = "";
		// Check if the search criteria is valid
		if ( ! getSearchQuery ().isEmpty () )
			// Compute the SQL search query
			searchQuery = "C." + ClientsDao.Properties.ClientCode.columnName + " LIKE '%" + getSearchQuery () + "%' OR C." + ClientsDao.Properties.ClientName.columnName + " LIKE '%" + getSearchQuery () + "%' OR C.ClientCode IN (" +
					" SELECT CB.ClientCode FROM ClientBarcodes CB WHERE LOWER ( CB.ClientBarcode ) = '" + getSearchQuery ().toLowerCase () + "' )";
		
		// Compute the day's journey code
		String journeyCode = JourneysUtils.getJourneyCode ( prefixID );
		
		// Compute SQL statement
		String SQL = "SELECT C.* , CO.CompanyName , D.DivisionCode , D.DivisionName " +
				"FROM " + ClientsDao.TABLENAME + " C " +
				"INNER JOIN " + CompaniesDao.TABLENAME + " CO ON C.CompanyCode = CO.CompanyCode " +
				"INNER JOIN " + ClientDivisionsDao.TABLENAME + " CD ON C.ClientCode = CD.ClientCode AND C.CompanyCode = CD.CompanyCode " +
				"INNER JOIN " + DivisionsDao.TABLENAME + " D ON D.DivisionCode = CD.DivisionCode AND D.CompanyCode = CD.CompanyCode " +
				"WHERE C." + ClientsDao.Properties.ClientCode.columnName + " IN (" +
				"SELECT " + ClientsDao.Properties.ClientCode.columnName + " FROM " + ClientsDao.TABLENAME + " WHERE " + ClientsDao.Properties.ClientCode.columnName + " NOT IN (" +
				"SELECT " + CycleCallsDao.Properties.ClientCode.columnName + " FROM " + CycleCallsDao.TABLENAME + " WHERE " + CycleCallsDao.Properties.Week.columnName + "=? " +
				"AND " + CycleCallsDao.Properties.Day.columnName + "=? AND " + CycleCallsDao.Properties.CycleCallStatus.columnName + "=? ))" +
				"AND C." + ClientsDao.Properties.ClientCode.columnName + " NOT IN ( " +
				"SELECT " + VisitsDao.Properties.ClientCode.columnName + " FROM " + VisitsDao.TABLENAME + " WHERE " + VisitsDao.Properties.ClientCode.columnName + " IN ( " +
				"SELECT " + ClientsDao.Properties.ClientCode.columnName + " FROM " + ClientsDao.TABLENAME + " WHERE " + ClientsDao.Properties.ClientCode.columnName + " NOT IN (" +
				"SELECT " + CycleCallsDao.Properties.ClientCode.columnName + " FROM " + CycleCallsDao.TABLENAME + " WHERE " + CycleCallsDao.Properties.Week.columnName + "=? " +
				"AND " + CycleCallsDao.Properties.Day.columnName + "=? AND " + CycleCallsDao.Properties.CycleCallStatus.columnName + "=? )) " + 
				"AND " + VisitsDao.Properties.JourneyCode.columnName + " = ? )" +
				"AND C." + ClientsDao.Properties.ClientCode.columnName + " NOT IN ( " +
				"SELECT UAN." + UserAccountNumbersDao.Properties.AccountNumber.columnName + " FROM " + UserAccountNumbersDao.TABLENAME + " UAN )" +
				"AND C." + ClientsDao.Properties.ParentCode.columnName + " IS NOT NULL" +
				( searchQuery.isEmpty () ? "" : " AND (" + searchQuery + ")") +
				" ORDER BY C." + ClientsDao.Properties.ClientName.columnName;
		// Compute the SQL arguments
		String selectionArguments [] = new String [] {
			week ,
			day ,
			String.valueOf ( StatusUtils.isAvailable () ) ,
			week ,
			day ,
			String.valueOf ( StatusUtils.isAvailable () ) ,
			journeyCode
		};
		
		// Execute the SQL query by limit and return the resulting cursor
		return CursorUtils.rawQueryByLimit ( DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getDatabase () , SQL , selectionArguments );
	}
	
	/**
	 * AsyncTask helper class used to populate the journey daily visits list with the appropriate calls belonging to the current date.
	 * 
	 * @author Elias
	 *
	 */
	private class PopulateList extends AsyncTask < Void , Void , LinkedHashMap < Day , ArrayList < Call > > > {
		/**
		 * Cursor holding out of route clients result set.
		 */
		private Cursor outOfRouteCursor;
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@SuppressLint("DefaultLocale") @Override
		protected LinkedHashMap < Day , ArrayList < Call > > doInBackground ( Void ... params ) {
			try {
			
				// Retrieve the user code
				String userCode = DatabaseUtils.getCurrentUserCode ( getActivity () );
				// Retrieve the company code
				String companyCode = DatabaseUtils.getCurrentCompanyCode ( getActivity () );
				// Retrieve the user prefix ID
				int prefixID = DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getUsersDao ().queryBuilder ()
						.where ( UsersDao.Properties.UserCode.eq ( userCode ) ,
								UsersDao.Properties.CompanyCode.eq ( companyCode ) ).unique ().getPrefixID ();
				// Declare and initialize a cursor used to retrieve data sets from query results
				Cursor cursor = null;
				// Retrieve the week number per cycle
				int weekNumberPerCycle = Cycle.getWeekNumberPerCycle ( getActivity () , userCode , companyCode );
				// Retrieve the current week number
				int week = Cycle.getWeekNumber ( weekNumberPerCycle );
				// Retrieve the system date
				Calendar today = Calendar.getInstance ();
				Calendar day = Calendar.getInstance ();
				Day _day = null;
				// Compute the day's journey code
				String journeyCode_today = JourneysUtils.getJourneyCode ( prefixID , today );
				// Compute day as Monday
				switch ( today.get ( Calendar.DAY_OF_WEEK ) ) {
				case Calendar.MONDAY:
					// Do nothing
					break;
				case Calendar.TUESDAY:
					day.add ( Calendar.DATE , -1 );
					break;
				case Calendar.WEDNESDAY:
					day.add ( Calendar.DATE , -2 );
					break;
				case Calendar.THURSDAY:
					day.add ( Calendar.DATE , -3 );
					break;
				case Calendar.FRIDAY:
					day.add ( Calendar.DATE , -4 );
					break;
				case Calendar.SATURDAY:
					day.add ( Calendar.DATE , -5 );
					break;
				case Calendar.SUNDAY:
					day.add ( Calendar.DATE , -6 );
					break;
				} // End switch
				
				// Initialize the map of calls
				calls = new LinkedHashMap < Day , ArrayList < Call > > ();
				
				// Iterate from Monday to Sunday
				for ( int i = 0 ; i < 7 ; i ++ ) {
					// Compute the current journey day
					_day = new Day ( DateTime.getDayAbbreviation ( getActivity () , day ) , DateTime.getDay ( getActivity () , day ) );
					
					// Determine if the current day is today
					if ( day.get ( Calendar.DAY_OF_WEEK ) == today.get ( Calendar.DAY_OF_WEEK ) ) {
						// Initialize the out of route cursor (query for all clients except for today's)
						outOfRouteCursor = computeCursor ( prefixID , String.valueOf ( week ) , _day.getDay () );
						// Modify the day object
						_day.setToday ( true );
					} // End if
					
					// Compute the day's journey code
					String journeyCode = JourneysUtils.getJourneyCode ( prefixID , day );
					// Retrieve a reference to the dao session
					DaoSession daoSession = DatabaseUtils.getInstance ( getActivity () ).getDaoSession ();
					// Declare and initialize the list of calls for the current day
					ArrayList < Call > _calls = new ArrayList < Call > ();
					
					// Declare and initialize the map of barcodes for the calls for the current day
					HashMap < String , ArrayList < String > > clientBarcodes = new HashMap < String , ArrayList < String > > ();
					// Query from the database all the barcodes of the cycle calls for the provided day and week
					cursor = daoSession.getDatabase ().rawQuery ( 
							"SELECT CB.ClientCode || '--' || CB.DivisionCode || '--' || CB.CompanyCode , CB.ClientBarcode " +
							"FROM Clients C INNER JOIN ClientDivisions CD ON C.ClientCode = CD.ClientCode AND C.CompanyCode = CD.CompanyCode " +
							"INNER JOIN Companies CO ON C.CompanyCode = CO.CompanyCode " +
							"INNER JOIN Divisions D ON D.DivisionCode = CD.DivisionCode AND D.CompanyCode = CD.CompanyCode " +
							"INNER JOIN ClientBarcodes CB ON CB.ClientCode = CD.ClientCode AND CB.CompanyCode = CD.CompanyCode AND CB.DivisionCode = CD.DivisionCode " +
							"WHERE ( C.ClientCode || '--' || C.CompanyCode || '--' || CD.DivisionCode ) IN (" +
							"SELECT ( V.ClientCode || '--' || V.CompanyCode|| '--' || V.DivisionCode ) FROM Visits V WHERE V.JourneyCode = ? " +
							"AND V.VisitReasonCode IS NULL AND ( V.ClientCode || '--' || V.CompanyCode || '--' || V.DivisionCode ) IN (" +
							"SELECT ( CDD.ClientCode || '--' || CDD.CompanyCode || '--' || CDD.DivisionCode ) FROM ClientDivisions CDD WHERE ( CDD.ClientCode || '--' || CDD.CompanyCode || '--' || CDD.DivisionCode ) NOT IN (" +
							"SELECT ( CC.ClientCode || '--' || CC.CompanyCode || '--' || CC.DivisionCode ) FROM CycleCalls CC WHERE CC.Week = ? " +
							"AND CC.Day = ? AND CC.CycleCallStatus = ? ) ) )" , 
							new String [] { journeyCode , String.valueOf ( week ) , _day.getDay () , String.valueOf ( StatusUtils.isAvailable () ) } );
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
					
					// Query DB for all the clients except the current day's client
					// Having at least one valid (not cancelled) visit
					cursor = daoSession.getDatabase ().rawQuery ( 
							"SELECT C.* , CD.* , CO.CompanyName , D.DivisionName " +
							"FROM Clients C INNER JOIN ClientDivisions CD ON C.ClientCode = CD.ClientCode AND C.CompanyCode = CD.CompanyCode " +
							"INNER JOIN Companies CO ON C.CompanyCode = CO.CompanyCode " +
							"INNER JOIN Divisions D ON D.DivisionCode = CD.DivisionCode AND D.CompanyCode = CD.CompanyCode " +
							"WHERE ( C.ClientCode || '--' || C.CompanyCode || '--' || CD.DivisionCode ) IN (" +
							"SELECT ( V.ClientCode || '--' || V.CompanyCode|| '--' || V.DivisionCode ) FROM Visits V WHERE V.JourneyCode = ? " +
							"AND V.VisitReasonCode IS NULL AND ( V.ClientCode || '--' || V.CompanyCode || '--' || V.DivisionCode ) IN (" +
							"SELECT ( CDD.ClientCode || '--' || CDD.CompanyCode || '--' || CDD.DivisionCode ) FROM ClientDivisions CDD WHERE ( CDD.ClientCode || '--' || CDD.CompanyCode || '--' || CDD.DivisionCode ) NOT IN (" +
							"SELECT ( CC.ClientCode || '--' || CC.CompanyCode || '--' || CC.DivisionCode ) FROM CycleCalls CC WHERE CC.Week = ? " +
							"AND CC.Day = ? AND CC.CycleCallStatus = ? ) ) ) ORDER BY C.ClientCode" , 
							new String [] { journeyCode , String.valueOf ( week ) , _day.getDay () , String.valueOf ( StatusUtils.isAvailable () ) } );
					// Compute the columns offsets
					int clientOffset = 0;
					int clientDivisionOffset = clientOffset + daoSession.getClientsDao ().getAllColumns ().length;
					// Move the cursor to the first row
					if ( cursor.moveToFirst () == true ) {
						do {
							// Retrieve the client and client division objects
							Clients client = daoSession.getClientsDao ().readEntity ( cursor , clientOffset );
							ClientDivisions clientDivision = daoSession.getClientDivisionsDao ().readEntity ( cursor , clientDivisionOffset );
							// Retrieve the company and division names
							String companyName = cursor.getString ( cursor.getColumnIndex ( CompaniesDao.Properties.CompanyName.columnName ) );
							String divisionName = cursor.getString ( cursor.getColumnIndex ( DivisionsDao.Properties.DivisionName.columnName ) );
							// Create a new journey call using the current client
							Call call = new Call ( String.valueOf ( week ) + _day.getDay () + client.getClientCode () + _day.getDay () + clientDivision.getDivisionCode () , client , clientDivision );
							// Set the company and division names
							client.setCompanyName ( companyName );
							client.setDivisionName ( divisionName );
							// Specify the journey code for the current day
							call.setJourneyCode ( journeyCode_today );
							// Specify the current call day
							call.setDay ( _day.getDay () );
							// Add the client to the list of calls
							_calls.add ( call );
						} while ( cursor.moveToNext () );
					} // End if
					// Close the cursor
					cursor.close ();
					cursor = null;

					// Retrieve all the performed visits during the current journey for the current list of clients
					List < Visits > _visits = DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getVisitsDao ().queryRaw ( 
							"WHERE " + VisitsDao.Properties.JourneyCode.columnName + "=? AND " + VisitsDao.Properties.ClientCode.columnName + " IN ( " +
								"SELECT " + ClientsDao.Properties.ClientCode.columnName + " FROM " + ClientsDao.TABLENAME + " " +
								"WHERE " + ClientsDao.Properties.ClientCode.columnName + " NOT IN ( " +
								"SELECT " + CycleCallsDao.Properties.ClientCode.columnName + " FROM " + CycleCallsDao.TABLENAME + " " +
								"WHERE " + CycleCallsDao.Properties.CycleCallStatus.columnName + "=? " +
								"AND " + CycleCallsDao.Properties.Day.columnName + "=? " +
								"AND " + CycleCallsDao.Properties.Week.columnName + "=? ) )" ,
							new String [] { journeyCode ,
									String.valueOf ( StatusUtils.isAvailable () ) ,
									_day.getDay () ,
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
								"SELECT " + ClientsDao.Properties.ClientCode.columnName + " FROM " + ClientsDao.TABLENAME + " " +
								"WHERE " + ClientsDao.Properties.ClientCode.columnName + " NOT IN ( " +
								"SELECT " + CycleCallsDao.Properties.ClientCode.columnName + " FROM " + CycleCallsDao.TABLENAME + " " +
								"WHERE " + CycleCallsDao.Properties.CycleCallStatus.columnName + "=? " +
								"AND " + CycleCallsDao.Properties.Day.columnName + "=? " +
								"AND " + CycleCallsDao.Properties.Week.columnName + "=? ) )" ,
							new String [] { journeyCode ,
									String.valueOf ( StatusUtils.isAvailable () ) ,
									_day.getDay () ,
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
							"AND " + SurveyAnswersDao.Properties.VisitID.columnName + " IS NOT NULL AND " + SurveyAnswersDao.Properties.VisitID.columnName + " IN (" +
							"SELECT " + VisitsDao.Properties.VisitID.columnName + " FROM " + VisitsDao.TABLENAME + " WHERE " + VisitsDao.Properties.JourneyCode.columnName + "=? )" +
							"GROUP BY " + SurveyAnswersDao.Properties.ClientCode.columnName ,
							new String [] {
									String.valueOf ( SurveysUtils.Type.CLIENT_SURVEY ) ,
									userCode ,
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
					for ( Call call : _calls ) {
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
					
					// Check if the calls list is NOT empty
					if ( ! _calls.isEmpty () )
						// Map the calls list to the map
						calls.put ( _day , _calls );
					
					// Go to next day
					day.add ( Calendar.DATE , 1 );
				} // End for loop
	
				// Check if there is a valid search query
				if ( ! getSearchQuery ().isEmpty () )
					// Compute and return the filtered map based on the provided search query
					return filter ();
				else
					// There is no filter, return the calls map
					return calls;
			
			} catch ( Exception exception ) {
				// Activity has ended, do nothing
				exception.printStackTrace ();
			} finally {
				// Clear the task reference
				populateList = null;
			} // End of try-catch-finally block
			return null;
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( LinkedHashMap < Day , ArrayList < Call > > calls ) {
			// Check if the activity is valid
			if ( getActivity () == null || outOfRouteCursor == null ) {
				// Invalid activity
				outOfRouteCursor = null;
				return;
			} // End if
			
			try {
				// Declare and initialize a multiple list adapter
				MultipleListAdapter adapter = new MultipleListAdapter ( getActivity () );
				// Iterate over the calls map
				for ( Day day : calls.keySet () )
					// Add the new daily call adapter
					adapter.addSection ( new Section ( day.getLabel () , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , day.isToday () ? 255 : 0 , 0 ) , null ) ,
							new DailyCallAdapter ( getActivity () , R.layout.journey_scheduled_client_item , new ArrayList < Call > ( calls.get ( day ) ) ) );
				// Add all the other out of route clients
				adapter.addSection ( new Section ( AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.out_of_route_visits ) , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
						new ClientsCursorAdapter ( getActivity () , outOfRouteCursor , R.layout.client_item ) );
				// Set the list adapter
				setListAdapter ( adapter );
			} catch ( Exception exception ) {
				// Activity has ended, do nothing
			} finally {
				outOfRouteCursor = null;
			} // End of try-catch block
		}
	}
	
	/*
	 * This method will be called when an item in the list is selected.
	 *
	 * @see android.support.v4.app.ListFragment#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	public void onListItemClick ( ListView listView , View view , int position , long id ) {
		// Check if the item clicks are enabled
		if ( ! ( (JourneyActivity) getActivity () ).isClickEnabled () )
			// Do nothing
			return;
		
		// Retrieve the selected call
		Call selectedCall = null;
		Object object = getListAdapter ().getItem ( position );
		// Determine the type of the clicked item via its tag
		if ( view.getTag () instanceof DailyCallAdapter.ViewHolder )
			selectedCall = (Call) object;
		else {
			// Retrieve the user prefix ID
			int prefixID = DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getUsersDao ().queryBuilder ()
					.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( getActivity () ) ) ,
							UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( getActivity () ) ) ).unique ().getPrefixID ();
			// Compute the day's journey code
			String journeyCode = JourneysUtils.getJourneyCode ( prefixID );
			// Retrieve a reference to the dao session
			DaoSession daoSession = DatabaseUtils.getInstance ( getActivity () ).getDaoSession ();
			// Retrieve the client and client division objects from the cursor
			Clients client = daoSession.getClientsDao ().readEntity ( (Cursor) object , 0 );
			ClientDivisions clientDivision = daoSession.getClientDivisionsDao ().queryBuilder ()
					.where ( ClientDivisionsDao.Properties.DivisionCode.eq ( ( (ViewHolder) view.getTag () ).divisionCode ) ,
							ClientDivisionsDao.Properties.CompanyCode.eq ( client.getCompanyCode () ) ,
							ClientDivisionsDao.Properties.ClientCode.eq ( client.getClientCode () ) ).unique ();
			// Create a new call based on the selected client
			selectedCall = new Call ( "" , client , clientDivision );
			// Set the journey code
			selectedCall.setJourneyCode ( journeyCode );
			// Set the current day
			switch ( Calendar.getInstance ().get ( Calendar.DAY_OF_WEEK ) ) {
			case Calendar.MONDAY:
				selectedCall.setDay ( getString ( R.string.monday_abbreviation ) );
				break;
			case Calendar.TUESDAY:
				selectedCall.setDay ( getString ( R.string.tuesday_abbreviation ) );
				break;
			case Calendar.WEDNESDAY:
				selectedCall.setDay ( getString ( R.string.wednesday_abbreviation ) );
				break;
			case Calendar.THURSDAY:
				selectedCall.setDay ( getString ( R.string.thursday_abbreviation ) );
				break;
			case Calendar.FRIDAY:
				selectedCall.setDay ( getString ( R.string.friday_abbreviation ) );
				break;
			case Calendar.SATURDAY:
				selectedCall.setDay ( getString ( R.string.saturday_abbreviation ) );
				break;
			case Calendar.SUNDAY:
				selectedCall.setDay ( getString ( R.string.sunday_abbreviation ) );
				break;
			} // End switch
		} // End else
		// Display the appropriate quick action widget
		quickAction_Visited.show ( view , selectedCall , getResources () );
		
		// Determine if the selected client is blocked
		if ( selectedCall.getClient ().getClientStatus () != StatusUtils.isActive () ) {
			// Indicate that the visit cannot be performed
			Baguette.showText ( getActivity () ,
					AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.client_not_active_message ) ,
					Baguette.BackgroundColor.BLUE );
			// Exit method
			return;
		} // End if
	}
	
	/*
	 * Callback method to be invoked when an action item in this QuickAction has been clicked.
	 *
	 * @see me.SyncWise.Android.Widgets.QuickAction.QuickAction.OnActionItemClickListener#onItemClick(me.SyncWise.Android.Widgets.QuickAction.QuickAction, int, int)
	 */
	@Override
	public void onItemClick ( QuickAction source , View anchor , Object object , int pos , int actionId ) {
		// Retrieve the selected client
		final Call call = (Call) object;
		// Determine the clicked action item
		switch ( actionId ) {
		case ActionItemID.INFO:
			// Create an intent to start a new activity
			Intent intent = new Intent ( getActivity () , App.getClientInfoActicityClass () );
			// Add the client code to the intent
			intent.putExtra ( ClientInfoActivity.CLIENT_CODE , call.getClient ().getClientCode () );
			// Add the company code to the intent
			intent.putExtra ( ClientInfoActivity.COMPANY_CODE , call.getClient ().getCompanyCode () );
			// Start the new activity
			startActivity ( intent );
			// Specify an explicit transition animation to perform next
			ActivityTransition.SlideOutLeft ( getActivity () );
			break;
		case ActionItemID.VISIT:
		case ActionItemID.CALL:
			String codeName = "" ;
			String SQL = null;
		 	String selectionArguments [] = null ;
			SQL = " select  ClientCode from TransactionHeaders where RemainingAmount > 0 and TransactionPasswordCode is null " +
					  " and clientCode not in(select AccountNumber from UserAccountNumbers  where userCode = ?) and ClientCode in (select distinct ClientCode from Clients where ClientType = 1 and CompanyCode = ? ) and TransactionStatus = 1 " +
					  " and TransactionType = 2 and CompanyCode = ?  ";
		
			 	// Compute the selection arguments
					selectionArguments = new String [] {	   
							String.valueOf ( DatabaseUtils.getCurrentUserCode (getActivity() ) ),
							String.valueOf ( DatabaseUtils.getCurrentCompanyCode(getActivity() ) ),
							String.valueOf ( DatabaseUtils.getCurrentCompanyCode(getActivity() ) )
					};
				Cursor cursor = DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getDatabase ()
						        .rawQuery( SQL , selectionArguments ) ;

				ArrayList < String > headers = new ArrayList<String>();
			 
				if ( cursor.moveToFirst () ) {
					// Iterate over all raws
					do {
						String clientCodes =new String();
						clientCodes=(  cursor.getString(0) );
					
						headers.add ( clientCodes );
					} while ( cursor.moveToNext () );
				} // End if
				// Close the cursor
				cursor.close ();
				cursor = null;
				
				SQL =   " select distinct ClientCode from TransactionHeaders where RemainingAmount > 0" +
						"  and TransactionPasswordCode is null  and Info1=1  and ClientCode in (select distinct ClientCode from Clients where ClientType = 2 and CompanyCode = ?" +
						"  )   and TransactionStatus = 1   and TransactionType = 2 and CompanyCode = ?";
			
				 	// Compute the selection arguments
						selectionArguments = new String [] {	   
							//	String.valueOf ( DatabaseUtils.getCurrentUserCode (getActivity() ) ),
								String.valueOf ( DatabaseUtils.getCurrentCompanyCode(getActivity() ) ),
								String.valueOf ( DatabaseUtils.getCurrentCompanyCode(getActivity() ) )
						};
						 cursor = DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getDatabase ()
								        .rawQuery( SQL , selectionArguments ) ;

						ArrayList < String > headers1 = new ArrayList<String>();
					 
						if ( cursor.moveToFirst () ) {
							// Iterate over all raws
							do {
								String clientCodes =new String();
								clientCodes=(  cursor.getString(0) );
							    headers1.add ( clientCodes );
							} while ( cursor.moveToNext () );
						} // End if
						// Close the cursor
						cursor.close ();
						cursor = null;

				        if( !headers1.isEmpty() )
				           { 	   
				        	   boolean clientExist = false;
				        	   for(String c :headers1)
				        	   {
				        		   if( c.equals( call.getClient().getClientCode()) )
				        		   	{
				        			   	clientExist = true;
				        		   	}
				        	   }
				        	   if( clientExist == true )
				        	   {
				        		   // Determine if a visit and a call are different
				        		   final boolean enforceVisitType = source.containsActionItem ( ActionItemID.CALL );
				        		   final int visitType = actionId;
				        		   // Determine if the selected client is blocked
				        		   if ( call.getClient ().getClientStatus () != StatusUtils.isActive () ) {
									// Indicate that the visit cannot be performed
									Baguette.showText ( getActivity () ,
											AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.client_not_active_message ) ,
											Baguette.BackgroundColor.BLUE );
									// Exit method
									return;
								} // End if
								// Determine if the visit is previously cancelled AND not visited (other than the cancellation call)
								if ( call.isCancelled () && ! call.isVisited () ) {
									// Display call confirmation
									AppDialog.getInstance ().displayAlert ( getActivity () ,
											null ,
											AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.visit_cancelled_client_message ) ,
											AppDialog.ButtonsType.YesNo ,
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick ( DialogInterface dialog , int which ) {
													// Determine the clicked button
													switch ( which ) {
													case DialogInterface.BUTTON_POSITIVE:
														// Indicate that the call is NOT cancelled
														call.setCancelled ( false );
														// Delete the previous cancelled visit
														DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getDatabase ().delete ( VisitsDao.TABLENAME , // Table
																VisitsDao.Properties.JourneyCode.columnName + " = ? AND " +
																VisitsDao.Properties.ClientCode.columnName + " = ? AND " +
																VisitsDao.Properties.VisitStatus.columnName + " = ?" , // Where clause
																new String [] { call.getJourneyCode () ,
																	call.getClient ().getClientCode () ,
																	String.valueOf ( StatusUtils.isDeleted () ) } ); // Where arguments
														// Prompt for a no client barcode reason before performing call initialization
														displayBarcodeReason ( call , enforceVisitType , visitType );
														break;
													case DialogInterface.BUTTON_NEGATIVE:
														// Dismiss dialog
														AppDialog.getInstance ().dismiss ();
														break;
													} // End switch
												}
											} );
								} // End if
								// Determine if the visit should use a time pass code instead of a barcode shoot
								else if ( call.getUseTimePasscode () )
									// Start a new visit without barcode 
									startVisit ( call , enforceVisitType , visitType , null );
								else
									// Prompt for a no client barcode reason before performing call initialization
									displayBarcodeReason ( call , enforceVisitType , visitType );
								break;
								
						    	}
							else
							{
								for( String c : headers1 )
									{		
										SQL = 	" select ClientName from Clients where ClientCode = ? and CompanyCode = ? ";
								 	// Compute the selection arguments
										selectionArguments = new String [] {	   
												String.valueOf ( c )  ,
												String.valueOf ( DatabaseUtils.getCurrentCompanyCode(getActivity() ) )
										};
										  cursor = DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getDatabase ()
												        .rawQuery( SQL , selectionArguments ) ;

										 
									 
										if ( cursor.moveToFirst () ) {
											// Iterate over all raws
											do {
												SpannableString client = null;
												// Check if the current item is taxable
											    String codeLabel="Code";
													// Compute the tax label
												client = new SpannableString ( codeLabel + " : "  );
													// Apply foreground color span
												client.setSpan ( new ForegroundColorSpan ( Color.BLUE ) , 0 , codeLabel.length () , 0 );
													// Apply style span
												client.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , codeLabel.length () , 0 );
										
												String clientName = new String();
												clientName = (  cursor.getString(0) );
												codeName += client + c +" - Name: "+ clientName + ","+"\n"  ;
												 
											} while ( cursor.moveToNext () );
										} // End if
										// Close the cursor
										cursor.close ();
										cursor = null;
					 	 
							}
				        
								
								
								( (JourneyActivity) getActivity () ).promptPasscode1 ( codeName );
								
								
								
								
								
								
								
								
								// Display call confirmation
								//AppDialog.getInstance ().displayAlert ( getActivity () ,
									//	AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.client_credit_client_message )  ,codeName
								//	  ,
									//	AppDialog.ButtonsType.OK ,
								//		new DialogInterface.OnClickListener() {
//											@Override
//											public void onClick ( DialogInterface dialog , int which ) {
//												// Determine the clicked button
//												switch ( which ) {
//												case DialogInterface.BUTTON_NEUTRAL:
//													
//													break;
//												} // End switch
//											}
//										} );
								break;
						 
				                }
				        	   }
				
           if( !headers.isEmpty() )
           {
        	   
        	   boolean clientExist=false;
        	   for(String c :headers)
        	   {
        		   if( c.equals( call.getClient().getClientCode()) )
        		   	{
        			   	clientExist =true;
        		   	}
        	   }
        	   if(clientExist == true)
        	   {
        			// Determine if a visit and a call are different
       			final boolean enforceVisitType = source.containsActionItem ( ActionItemID.CALL );
       			final int visitType = actionId;
       			// Determine if the selected client is blocked
       			if ( call.getClient ().getClientStatus () != StatusUtils.isActive () ) {
       				// Indicate that the visit cannot be performed
       				Baguette.showText ( getActivity () ,
       						AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.client_not_active_message ) ,
       						Baguette.BackgroundColor.BLUE );
       				// Exit method
       				return;
       			} // End if
       			// Prompt for a no client barcode reason before performing call initialization
       			displayBarcodeReason ( call , enforceVisitType , visitType );
				
		    	}
			else
			{
				for( String c : headers )
					{		
						SQL = 	" select ClientName from Clients where ClientCode = ? and CompanyCode = ? ";
				 	// Compute the selection arguments
						selectionArguments = new String [] {	   
								String.valueOf ( c )  ,
								String.valueOf ( DatabaseUtils.getCurrentCompanyCode(getActivity() ) )
						};
						  cursor = DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getDatabase ()
								        .rawQuery( SQL , selectionArguments ) ;

						 
					 
						if ( cursor.moveToFirst () ) {
							// Iterate over all raws
							do {
								SpannableString client = null;
								// Check if the current item is taxable
							    String codeLabel="Code";
									// Compute the tax label
								client = new SpannableString ( codeLabel + " : "  );
									// Apply foreground color span
								client.setSpan ( new ForegroundColorSpan ( Color.BLUE ) , 0 , codeLabel.length () , 0 );
									// Apply style span
								client.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , codeLabel.length () , 0 );
						
								String clientName = new String();
								clientName = (  cursor.getString(0) );
								codeName += client + c +" - Name: "+ clientName + ","+"\n"  ;
								 
							} while ( cursor.moveToNext () );
						} // End if
						// Close the cursor
						cursor.close ();
						cursor = null;
			
			
					
			 
			}
        	//for( String c : headers )
			//{
			//	Toast.makeText( getActivity(),codes , Toast.LENGTH_LONG).show();
				// Display call confirmation
			
				
				( (JourneyActivity) getActivity () ).promptPasscode1 ( codeName );
				
				
				
				
//				AppDialog.getInstance ().displayAlert ( getActivity () ,
//						AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.client_cash_client_message )  ,codeName
//					  ,
//						AppDialog.ButtonsType.OK ,
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick ( DialogInterface dialog , int which ) {
//								// Determine the clicked button
//								switch ( which ) {
//								case DialogInterface.BUTTON_NEUTRAL:
//									
//									break;
//								} // End switch
//							}
//						} );
//			
			 
           }}
           else{
			// Determine if a visit and a call are different
			final boolean enforceVisitType = source.containsActionItem ( ActionItemID.CALL );
			final int visitType = actionId;
			// Determine if the selected client is blocked
			if ( call.getClient ().getClientStatus () != StatusUtils.isActive () ) {
				// Indicate that the visit cannot be performed
				Baguette.showText ( getActivity () ,
						AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.client_not_active_message ) ,
						Baguette.BackgroundColor.BLUE );
				// Exit method
				return;
			} // End if
			// Prompt for a no client barcode reason before performing call initialization
			displayBarcodeReason ( call , enforceVisitType , visitType );
			break;}
		} // End of switch
	}
	
	/*
	 * Receive the result from a previous call to startActivityForResult(Intent, int).
	 *
	 * @see android.support.v4.app.Fragment#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	public void onActivityResult ( int requestCode , int resultCode , Intent data ) {
    	// Check if the result is successful and the intent is valid
    	if ( resultCode != Activity.RESULT_OK || data == null )
    		// Exit method
    		return;

		// Retrieve all the daily calls asynchronously
    	refresh ();
	}
	
	/*
	 * Starts a client visit.
	 *
	 * @see me.SyncWise.Android.Modules.Journey.JourneyFragment#startVisit(me.SyncWise.Android.Database.Reasons)
	 */
	@Override
	protected void startVisit ( final Call call , final boolean enforceVisitType , final int visitType , final Reasons reason ) {
		// Perform call initialization
		preVisitClientCall ( call , enforceVisitType , visitType , reason , false );
	}
	
	/**
	 * Performs initialize to perform a visit.
	 * 
	 * @param call	The client call to perform.
	 * @param enforceVisitType	Boolean indicating the visit type (difference between visit and call) should be enforced or not.
	 * @param visitType	Integer holding the visit type (action ID).
	 * @param barcodeReason	A no client barcode reason if the user did not scan a barcode to initiate a client call, or {@code NULL} otherwise.
	 * @param skipChecking	Boolean used to indicate if the in route calls checking is to be skipped.
	 */
	@Override
	protected void preVisitClientCall ( final Call call , final boolean enforceVisitType , final int visitType , final Reasons barcodeReason , final boolean skipChecking ) {
		// Retrieve the current user
		Users user = DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getUsersDao ().queryBuilder ()
				.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( getActivity () ) ) , UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( getActivity () ) ) ).unique ();
		// Retrieve a reference of the view pager (which has the app fragment pager adapter linked to)
		// The view pager is used to retrieve a reference to the out of route fragment, via its tag
		ViewPager viewPager = (ViewPager) getActivity ().findViewById ( R.id.pager );
		// Retrieve a reference to the app fragment pager adapter
		// This adapter can compute the expected out of route fragment tag so it can be referenced (via its tag)
		AppFragmentPagerAdapter adapter = viewPager != null ? (AppFragmentPagerAdapter) viewPager.getAdapter () : null;
		// Compute the daily section (having index 0) tag
		String tag = adapter != null ? adapter.getFragmentName ( viewPager , 0 ) : null;
		// Retrieve a reference to the journey fragment of out of route calls based on its tag
		JourneyFragment_Daily DailySection = (JourneyFragment_Daily) getFragmentManager ().findFragmentByTag ( tag == null ? "" : tag );
		// Check if the call is out of route
		// AND the daily section is valid
		// AND if there is at least one remaining call
		if (  user.getUserType () == 11    && ! skipChecking && ! call.isScheduled () && DailySection != null && DailySection.hasRemainingCalls () ) {
			
			
		 	if(PermissionsUtils.getEnableOutOfRoutePasscode ( getActivity() , DatabaseUtils.getCurrentUserCode ( getActivity() ) , DatabaseUtils.getCurrentCompanyCode ( getActivity() ) ))
		 	{	 
			// Save required fields
			PasscodeResult passcodeResult = new PasscodeResult ( PasscodeResult.TYPE_OUT_OF_ROUTE , 2 , call , enforceVisitType , visitType , barcodeReason );
			// Prompt pass code
			( (JourneyActivity) getActivity () ).promptPasscode ( passcodeResult );
			// Exit method
			  return;
		 	}
			
		} // End if
		if (  user.getUserType () == 10  && ! skipChecking && ! call.isScheduled () && DailySection != null && DailySection.hasRemainingCalls ()  ) {
			if(PermissionsUtils.getEnableOutOfRoutePasscode ( getActivity() , DatabaseUtils.getCurrentUserCode ( getActivity() ) , DatabaseUtils.getCurrentCompanyCode ( getActivity() ) ))
		 	{
			// Save required fields
			PasscodeResult passcodeResult = new PasscodeResult ( PasscodeResult.TYPE_OUT_OF_ROUTE , 2 , call , enforceVisitType , visitType , barcodeReason );
			// Prompt pass code
			( (JourneyActivity) getActivity () ).promptPasscode ( passcodeResult );
			// Exit method
			return;
		 	}
		} // End if
		Double latitude = null;
		Double longitude = null;
		Location location = VisitsUtils.SwitchLocation(GPSTracker._lastLocationG, GPSTracker._lastLocationN);
		if (location != null) {
			latitude =  location.getLatitude();
			longitude = location.getLongitude();
		}
		int Counter = 0;
		while (location == null && Counter < 1) {

			try {
				Thread.sleep(1000);
				  location = VisitsUtils.SwitchLocation(GPSTracker._lastLocationG, GPSTracker._lastLocationN);
				
				if (location != null) {
					latitude = location.getLatitude();
					longitude =location.getLongitude();
				} else {
					Toast.makeText(getActivity(), "Null Location",
							Toast.LENGTH_LONG).show();
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			Counter++;

		}
		// Retrieve current date
		Calendar today = Calendar.getInstance ();
		// TODO : Details of a visit used to create a client call
		// Create a new visit for the current journey to start the client call
		Visits visit = new Visits ( null , // ID
				VisitsUtils.getVisitID ( today ) , // VisitID
				call.getClient ().getClientCode () , // ClientCode
				DatabaseUtils.getCurrentUserCode ( getActivity() ) , // UserCode
				today.getTime () , // StartDate
				today.getTime () , // EndDate
				StatusUtils.isAvailable () , // VisitStatus

				longitude!=null?longitude.toString():"" , // Longitude
			    latitude!=null?latitude.toString():"" , // Latitude

				"" , // Note
				null , // VisitReasonCode
				null , // TransactionReasonCode
				barcodeReason == null ? VisitsUtils.Barcode.YES : VisitsUtils.Barcode.NO , // IsBarcode
				VisitsUtils.Type.OUT_OF_ROUTE , // VisitType
				call.getJourneyCode () , // JourneyCode
				call.getClientDivision ().getDivisionCode () , // DivisionCode
				call.getClient ().getCompanyCode () , // CompanyCode
				IsProcessedUtils.isNotSync () , // IsProcessed
				today.getTime () ); // StampDate
		// Insert the visit in DB
		DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getVisitsDao ().insert ( visit );
		// Update target coverage (if any)
		TargetUpdate.updateCoverage ( getActivity () , TargetHeadersUtils.Type.TOTAL_COVERAGE , visit.getUserCode () , TargetHeadersUtils.SubType.USER , visit.getClientCode () , - 1 , visit.getCompanyCode () , visit.getDivisionCode () , TargetUpdate.UpdateType.INCREASE , null , null );
		// Check if a barcode reason is provided
		if ( barcodeReason != null ) {
			// Create a new visit reason for the the no client barcode reason
			VisitReasons visitReason = new VisitReasons ( null , // ID
					visit.getVisitID () , // VisitID
					ReasonsUtils.Type.NO_CLIENT_BARCODE , // ReasonType
					barcodeReason.getReasonCode () , // ReasonCode
					today.getTime () ); // StampDate
			// Insert the visit reason in DB
			DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getVisitReasonsDao ().insert ( visitReason );
		} // End if
		// Check if the visit type is enforced
		if ( enforceVisitType && ( visitType == ActionItemID.VISIT || visitType == ActionItemID.CALL ) ) {
			// Create a new visit type for the visit above
			VisitTypes _visitType = new VisitTypes ( null , // ID
					visit.getVisitID () , // VisitID
					visitType == ActionItemID.VISIT ? VisitTypesUtils.Type.VISIT : VisitTypesUtils.Type.CALL , // VisitTypeID
					today.getTime () ); // StampDate
			// Insert the visit type in DB
			DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getVisitTypesDao ().insert ( _visitType );
		} // End if
		// Indicate that the call is visited
		call.setVisited ( true );
		// Update visit location
		//TrackingUtils.forceLocationUpdate ( getActivity () , visit.getVisitID () );
		// Clear call action data
		CallAction.clearData ( getActivity () );

		// Retrieve the corresponding client user collection for the current client
		ClientUserCollection clientUserCollection = DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getClientUserCollectionDao ().queryBuilder ()
				.where ( ClientUserCollectionDao.Properties.ClientCode.eq ( call.getClientDivision ().getClientCode () ) ,
						ClientUserCollectionDao.Properties.CompanyCode.eq ( call.getClientDivision ().getCompanyCode () ) ,
						ClientUserCollectionDao.Properties.DivisionCode.eq ( call.getClientDivision ().getDivisionCode () ) ).unique ();
		// Check if the current client is a client user collection 
		boolean isClientUserCollection = clientUserCollection != null;
		
		// Retrieve the company code of the user
		String companyCode = DatabaseUtils.getCurrentCompanyCode ( getActivity () );
		// Retrieve all divisions
		List < Divisions > allDivisions = DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getDivisionsDao ().queryBuilder ()
				.where ( DivisionsDao.Properties.CompanyCode.eq ( call.getClientDivision ().getCompanyCode () ) ).list ();
		// Retrieve the divisions linked to the user
		List < Divisions > directUserDivisions = DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getDivisionsDao ()
				.queryRaw ( " WHERE CompanyCode = ? AND DivisionCode IN ( SELECT DivisionCode FROM UserDivisions WHERE CompanyCode = ? ) " ,
						new String [] { companyCode , companyCode } );
		// Retrieve the user children division
		List < Divisions > allUserDivisions = DivisionsUtils.getChildren ( directUserDivisions , allDivisions );
		// Add the direct user divisions to the main list
		allUserDivisions.addAll ( directUserDivisions );
		// Check if the division of the current client belongs to the user
		boolean belongsToUser = false;
		// Iterate over all user divisions
		for ( Divisions division : allUserDivisions )
			// Check if the current division is the client's division
			if ( division.getDivisionCode ().equals ( call.getClientDivision ().getDivisionCode () ) && division.getCompanyCode ().equals ( call.getClientDivision ().getCompanyCode () ) ) {
				// Set flag
				belongsToUser = true;
				// Exit loop
				break;
			} // End if
		
		try {
			// Create a new intent to start a new activity
			Intent intent = new Intent ( getActivity () , isClientUserCollection && ! belongsToUser ? CollectionDetailsActivity.class : App.getCallMenuActivityClass () );
			// Add the call object to the intent
			intent.putExtra ( isClientUserCollection && ! belongsToUser ? CollectionDetailsActivity.CALL : CallMenuActivity.CALL , call );
			// Add the visit object to the intent
			intent.putExtra ( isClientUserCollection && ! belongsToUser ? CollectionDetailsActivity.VISIT : CallMenuActivity.VISIT , visit );
			// Add the collection only flag to the intent
			intent.putExtra ( CollectionDetailsActivity.COLLECTION_ONLY , isClientUserCollection && ! belongsToUser ? true : false );
			// Start the new activity
			startActivityForResult ( intent , isClientUserCollection && ! belongsToUser ? REQUEST_CODE_COLLECTION : REQUEST_CODE_VISIT );
			// Specify an explicit transition animation to perform next
			ActivityTransition.SlideOutLeft ( getActivity () );
		} catch ( Exception exception ) {
			// Indicate that the activity cannot be displayed
			new AppToast ( getActivity () ).setIcon ( R.drawable.warning ).setText ( AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.invalid_selection_label ) ).show ();
		} // End of try-catch block
	}
	
	/*
	 * Called when the query text is changed by the user.
	 *
	 * @see android.widget.SearchView.OnQueryTextListener#onQueryTextChange(java.lang.String)
	 */
	@Override
	public boolean onQueryTextChange ( String newText ) {
		// Check if the new search query is equal to the current search query
		if ( newText.equals ( getSearchQuery () ) )
			// Do nothing
			// Indicate that the action was handled by the listener
			return true;
		// Update the new search query
		setSearchQuery ( newText );
		// Filter the list asynchronously
		new PopulateList ().execute ();
		// Indicate that the action was handled by the listener
		return true;
	}
	
	/**
	 * Filters the {@link #calls} map based on the indicated {@link #searchQuery}.<br>
	 * <b>NOTE : </b> This method does not perform any kind of integrity check what so ever !
	 * 
	 * @return	Map holding lists of {@link me.SyncWise.Android.Modules.Journey.Call Call} objects keyed to their corresponding day, filtered according to the search filter.
	 */
	@SuppressLint("DefaultLocale") 
	public LinkedHashMap < Day , ArrayList < Call > > filter () {
		// Declare and initialize a new calls map
		LinkedHashMap < Day , ArrayList < Call > > _calls = new LinkedHashMap < Day , ArrayList < Call > > ();
		// Iterate over the map
		for ( Day day : calls.keySet () ) {
			// Retrieve the the current calls list
			ArrayList < Call > list = calls.get ( day );
			// Declare and initialize a new calls list
			ArrayList < Call > _list = new ArrayList < Call > ();
			// Iterate over all the calls
			for ( Call call : list )
				// Match the filter with the client name, the client code (contains) and client barcode (equals)
				if ( call.getClient ().getClientName ().toLowerCase ().contains ( getSearchQuery ().toLowerCase () ) 
						|| call.getClient ().getClientCode ().toLowerCase ().contains ( getSearchQuery ().toLowerCase () )
						|| ( call.getClientBarcodes () != null && call.getClientBarcodes ().contains ( getSearchQuery ().toLowerCase () ) ) )
					// Add the client to the list
					_list.add ( call );
			// Check if the calls list is NOT empty
			if ( ! _list.isEmpty () )
				// Map the new calls list in the new calls map
				_calls.put ( day , _list );
		} // End for each
		// Return the new calls map
		return _calls;
	}
	
}