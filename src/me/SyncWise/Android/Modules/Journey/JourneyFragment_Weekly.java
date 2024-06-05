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
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import me.SyncWise.Android.ActivityTransition;
import me.SyncWise.Android.App;
import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppFragmentPagerAdapter;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.AppToast;
import me.SyncWise.Android.DateTime;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.ClientDivisions;
import me.SyncWise.Android.Database.ClientUserCollection;
import me.SyncWise.Android.Database.ClientUserCollectionDao;
import me.SyncWise.Android.Database.Clients;
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
import me.SyncWise.Android.Database.TargetHeadersUtils;
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
import me.SyncWise.Android.Modules.ClientsList.ClientInfo.ClientInfoActivity;
import me.SyncWise.Android.Modules.Collections.CollectionDetailsActivity;
import me.SyncWise.Android.Modules.Journey.JourneyFragment.ActionItemID;

import me.SyncWise.Android.Modules.Reason.ReasonActivity;
import me.SyncWise.Android.Modules.Target.TargetUpdate;
import me.SyncWise.Android.SectionsList.MultipleListAdapter;
import me.SyncWise.Android.SectionsList.Section;
import me.SyncWise.Android.Utilities.ActivityInstance;
import me.SyncWise.Android.Utilities.Cycle;
import me.SyncWise.Android.Utilities.EmptyListAdapter;
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
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Fragment used to represent a modular section for weekly visits.
 * 
 * @author Elias
 *
 */
public class JourneyFragment_Weekly extends JourneyFragment {

	/**
	 * Bundle key used to put/retrieve data.
	 */
	public static final String LAYOUT = JourneyFragment_Weekly.class.getName () + ".LAYOUT";
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #calls}.
	 */
	private static final String CALLS = JourneyFragment_Weekly.class.getName () + ".CALLS";
	
	/**
	 * Map holding lists of {@link me.SyncWise.Android.Modules.Journey.Call Call} objects keyed to their corresponding day, used to define the weekly calls.<br>
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
	@SuppressWarnings("unchecked")
	@Override
	public void onActivityCreated ( Bundle savedInstanceState ) {
		// Superclass method call
		super.onActivityCreated ( savedInstanceState );
		// Retrieve current date
		Calendar calendar = Calendar.getInstance ();
		// Display the start of the current week
		switch ( calendar.get ( Calendar.DAY_OF_WEEK ) ) {
		case Calendar.MONDAY:
			// Do nothing
			break;
		case Calendar.TUESDAY:
			// Go back one day (reach Monday)
			calendar.add ( Calendar.DATE , -1 );
			break;
		case Calendar.WEDNESDAY:
			// Go back 2 days (reach Monday)
			calendar.add ( Calendar.DATE , -2 );
			break;
		case Calendar.THURSDAY:
			// Go back 3 days (reach Monday)
			calendar.add ( Calendar.DATE , -3 );
			break;
		case Calendar.FRIDAY:
			// Go back 4 days (reach Monday)
			calendar.add ( Calendar.DATE , -4 );
			break;
		case Calendar.SATURDAY:
			// Go back 5 days (reach Monday)
			calendar.add ( Calendar.DATE , -5 );
			break;
		case Calendar.SUNDAY:
			// Go back 6 days (reach Monday)
			calendar.add ( Calendar.DATE , -6 );
			break;
		} // End of switch
		// Display the start of the current week
		( (TextView) getView ().findViewById ( R.id.label_date_from ) ).setText ( DateTime.getMonth ( getActivity () , calendar ) + " " + calendar.get ( Calendar.DAY_OF_MONTH ) + ", " + calendar.get ( Calendar.YEAR ) );
		// Add 6 days (reach Sunday)
		calendar.add ( Calendar.DATE , 6 );
		// Display the end of the current week
		( (TextView) getView ().findViewById ( R.id.label_date_to ) ).setText ( DateTime.getMonth ( getActivity () , calendar ) + " " + calendar.get ( Calendar.DAY_OF_MONTH ) + ", " + calendar.get ( Calendar.YEAR ) );
		
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state 
			if ( savedInstanceState.getBoolean ( CALLS ) )
				calls = (LinkedHashMap < Day , ArrayList < Call > >) ActivityInstance.readDataGson ( getActivity () , JourneyFragment_Weekly.class.getName () , CALLS , new TypeToken < LinkedHashMap < Day , ArrayList < Call > > > () {}.getType () );
		} // End if
		
		// Check if the list of calls is valid
		if ( calls == null && populateList == null ) {
			// Retrieve all the daily calls asynchronously
			populateList = new PopulateList ();
			populateList.execute ();
		} // End if
		// Check if there is a valid search query
		else if ( calls != null && ! getSearchQuery ().isEmpty () ) {
			// Initialize the empty label
			String emptyLabel = AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.empty_visits_list_label );
			// Set a new (empty) adapter for each section
			// Declare and initialize a multiple list adapter
			MultipleListAdapter adapter = new MultipleListAdapter ( getActivity () );
			// Iterate over the calls map
			for ( Day day : calls.keySet () )
				// Add an empty adapter for the current section
				adapter.addSection ( new Section ( day.getLabel () , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , day.isToday () ? 255 : 0 , 0 ) , null ) ,
						new EmptyListAdapter ( getActivity () , emptyLabel ) );
			// Set the list adapter
			setListAdapter ( adapter );
			// Set a new adapter based on the saved filter, asynchronously
			new FilterList ( getSearchQuery () ).execute ();
		} // End else if
		else if ( calls != null ) {
			// Initialize the empty label
			String emptyLabel = AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.empty_visits_list_label );
			// Declare and initialize a multiple list adapter
			MultipleListAdapter adapter = new MultipleListAdapter ( getActivity () );
			// Iterate over the calls map
			for ( Day day : calls.keySet () ) {
				// Declare and initialize an adapter for the current section
				Adapter _adapter = null;
				// Check if the calls list is empty
				if ( calls.get ( day ).isEmpty () ) {
					// Indicate that there are no calls for the current day adapter
					_adapter = new EmptyListAdapter ( getActivity () , emptyLabel );
				} // End if
				else
					// Declare and initialize a new daily call adapter
					_adapter = new DailyCallAdapter ( getActivity () , R.layout.journey_scheduled_client_item , new ArrayList < Call > ( calls.get ( day ) ) );
				// Add the new daily call adapter
				adapter.addSection ( new Section ( day.getLabel () , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , day.isToday () ? 255 : 0 , 0 ) , null ) , _adapter );
			} // End for each
			// Set the list adapter
			setListAdapter ( adapter );
		} // End else
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
    	
    	new Thread ( new Runnable() {
			@Override
			public void run () {
				// Save the content of the calls using GSON
				ActivityInstance.saveComplexMapDataGson ( getActivity () , JourneyFragment_Weekly.class.getName () , CALLS , calls );
			}
		} ).start ();
    	
    	// Indicate that there is saved calls data
    	outState.putBoolean ( CALLS , true );
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
	 * AsyncTask helper class used to populate the journey weekly visits list with the appropriate calls belonging to the current date.
	 * 
	 * @author Elias
	 *
	 */
	private class PopulateList extends AsyncTask < Void , Void , LinkedHashMap < Day , ArrayList < Call > > > {
		/**
		 * String used to host the empty list view label.
		 */
		private String emptyLabel;
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected LinkedHashMap < Day , ArrayList < Call > > doInBackground ( Void ... params ) {
			try {
				
				// Initialize the empty label
				emptyLabel = AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.empty_visits_list_label );
				
				// Retrieve the user code
				String userCode = DatabaseUtils.getCurrentUserCode ( getActivity () );
				// Retrieve the company code
				String companyCode = DatabaseUtils.getCurrentCompanyCode ( getActivity () );
				// Retrieve the user prefix ID
				int prefixID = DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getUsersDao ().queryBuilder ()
						.where ( UsersDao.Properties.UserCode.eq ( userCode ) ,
								UsersDao.Properties.CompanyCode.eq ( companyCode ) ).unique ().getPrefixID ();
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
					if ( day.get ( Calendar.DAY_OF_WEEK ) == today.get ( Calendar.DAY_OF_WEEK ) )
						// Modify the day object
						_day.setToday ( true );
					
					// Retrieve the calls list for the current journey day and map it
					calls.put ( _day , getCalls ( userCode , companyCode , prefixID , week , _day , day , journeyCode_today ) );
					
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
			if ( getActivity () == null || calls == null ) {
				// Invalid activity
				emptyLabel = null;
				return;
			} // End if
			
			try {
				// Otherwise the activity is valid
				// Declare and initialize a multiple list adapter
				MultipleListAdapter adapter = new MultipleListAdapter ( getActivity () );
				// Iterate over the calls map
				for ( Day day : calls.keySet () ) {
					// Declare and initialize an adapter for the current section
					Adapter _adapter = null;
					// Check if the calls list is empty
					if ( calls.get ( day ).isEmpty () ) {
						// Indicate that there are no calls for the current day adapter
						_adapter = new EmptyListAdapter ( getActivity () , emptyLabel );
					} // End if
					else
						// Declare and initialize a new daily call adapter
						_adapter = new DailyCallAdapter ( getActivity () , R.layout.journey_scheduled_client_item , new ArrayList < Call > ( calls.get ( day ) ) );
					// Add the new daily call adapter
					adapter.addSection ( new Section ( day.getLabel () , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , day.isToday () ? 255 : 0 , 0 ) , null ) , _adapter );
				} // End for each
				// Set the list adapter
				setListAdapter ( adapter );
			} catch ( Exception exception ) {
				// Activity has ended, do nothing
			} finally {
				emptyLabel = null;
			} // End of try-catch-finally block
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
		
		// Determine if the item is a call
		if ( ! ( getListAdapter ().getItem ( position ) instanceof Call ) )
			// Invalid list item click
			return;
		// Retrieve the selected call
		Call selectedCall = (Call) getListAdapter ().getItem ( position );
		// Determine if the item is scheduled or not
		if ( selectedCall.isScheduled () ) {
			// Determine if the client is visited (or cancelled)
			if ( selectedCall.isVisited () || selectedCall.isCancelled () )
				// The client is either visited or cancelled
				// Display the appropriate quick action widget
				quickAction_Visited.show ( view , selectedCall , getResources () );
			else
				// The client is NOT visited or cancelled
				// Display the appropriate quick action widget
				quickAction_Unvisited.show ( view , selectedCall , getResources () );
		} // End if
		else
			// The client is not scheduled
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
		// Create a new intent
		Intent intent = null;
		// Retrieve the selected client
		final Call call = (Call) object;
		// Determine the clicked action item
		switch ( actionId ) {
		case ActionItemID.INFO:
			// Create an intent to start a new activity
			intent = new Intent ( getActivity () , App.getClientInfoActicityClass () );
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
			SQL =     " select  ClientCode from TransactionHeaders where RemainingAmount > 0 and TransactionPasswordCode is null " +
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
								//String.valueOf ( DatabaseUtils.getCurrentUserCode (getActivity() ) ),
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
									
								
//								// Display call confirmation
//								AppDialog.getInstance ().displayAlert ( getActivity () ,
//										AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.client_credit_client_message )  ,codeName
//									  ,
//										AppDialog.ButtonsType.OK ,
//										new DialogInterface.OnClickListener() {
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
//							
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
       			// Determine if the call is scheduled
       			// AND if the visit is previously cancelled AND not visited (other than the cancellation call)
       			if ( call.isScheduled () && call.isCancelled () && ! call.isVisited () ) {
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
       			else
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
			
			//}
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
			// Determine if the call is scheduled
			// AND if the visit is previously cancelled AND not visited (other than the cancellation call)
			if ( call.isScheduled () && call.isCancelled () && ! call.isVisited () ) {
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
			else
				// Prompt for a no client barcode reason before performing call initialization
				displayBarcodeReason ( call , enforceVisitType , visitType );
			}
			break;
		case ActionItemID.CANCEL:
			// Determine if the selected client is blocked
			if ( call.getClient ().getClientStatus () != StatusUtils.isActive () ) {
				// Indicate that the visit cannot be performed
				Baguette.showText ( getActivity () ,
						AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.client_not_active_message ) ,
						Baguette.BackgroundColor.BLUE );
				// Exit method
				return;
			} // End if
			// Create a new intent to start a new activity
			intent = new Intent ( getActivity () , ReasonActivity.class );
			// Add the client object to the intent
			intent.putExtra ( ReasonActivity.CLIENT , call.getClient () );
			// Add the reason type to the intent
			intent.putExtra ( ReasonActivity.REASON_TYPES , new String [] { ReasonsUtils.Type.CANCEL_VISIT } );
			// Add the activity sub title to the intent
			intent.putExtra ( ReasonActivity.ACTIVITY_SUB_TITLE , new String [] { AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.visit_cancellation_label ) } );
			// Add the call to the intent
			intent.putExtra ( ReasonActivity.EXTRA_DATA , call );
			// Start the new activity
			startActivityForResult ( intent , REQUEST_CODE_CANCEL );
			// Specify an explicit transition animation to perform next
			ActivityTransition.SlideOutLeft ( getActivity () );
			break;
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
    	
    	// Declare and initialize the call object
    	Call call = null;
    	// Determine the provided request code
    	switch ( requestCode ) {
    	case REQUEST_CODE_VISIT:
    	case REQUEST_CODE_COLLECTION:
        	// Retrieve the performed call
    		call = (Call) data.getSerializableExtra ( CallMenuActivity.CALL );
    		break;
    	case REQUEST_CODE_CANCEL:
    		// Retrieve the performed call
    		call = (Call) data.getSerializableExtra ( ReasonActivity.EXTRA_DATA );
    		// Cancel the visit
    		preCancelClientCall ( call , data.getStringArrayExtra ( ReasonActivity.REASON_CODES ) [ 0 ] );
    		break;
    	} // End switch
    	
    	// Check if the call object is valid
    	if ( call == null )
    		// Invalid call object
    		return;
		// Check if the call is scheduled
		if ( call.isScheduled () ) {
    		// Perform post visit action
    		postVisitClientCall ( call );
    		// Perform post visit action in daily section
    		// Retrieve a reference of the view pager (which has the app fragment pager adapter linked to)
    		// The view pager is used to retrieve a reference to the daily fragment, via its tag
    		ViewPager viewPager = (ViewPager) getActivity ().findViewById ( R.id.pager );
    		// Retrieve a reference to the app fragment pager adapter
    		// This adapter can compute the expected daily fragment tag so it can be referenced (via its tag)
    		AppFragmentPagerAdapter adapter = viewPager != null ? (AppFragmentPagerAdapter) viewPager.getAdapter () : null;
    		// Compute the daily section (having index 0) tag
    		String tag = adapter != null ? adapter.getFragmentName ( viewPager , 0 ) : null;
    		// Retrieve a reference to the journey fragment of daily calls based on its tag
    		JourneyFragment_Daily dailySection = (JourneyFragment_Daily) getFragmentManager ().findFragmentByTag ( tag == null ? "" : tag );
    		// Perform post visit action in daily section (if valid)
    		if ( dailySection != null )
    			dailySection.postVisitClientCall ( call );
		} // End if
		else {
    		// Perform post visit action in out of route section
    		// Retrieve a reference of the view pager (which has the app fragment pager adapter linked to)
    		// The view pager is used to retrieve a reference to the out of route fragment, via its tag
    		ViewPager viewPager = (ViewPager) getActivity ().findViewById ( R.id.pager );
    		// Retrieve a reference to the app fragment pager adapter
    		// This adapter can compute the expected out of route fragment tag so it can be referenced (via its tag)
    		AppFragmentPagerAdapter adapter = viewPager != null ? (AppFragmentPagerAdapter) viewPager.getAdapter () : null;
    		// Compute the out of route section (having index 2) tag
    		String tag = adapter != null ? adapter.getFragmentName ( viewPager , 2 ) : null;
    		// Retrieve a reference to the journey fragment of out of route calls based on its tag
    		JourneyFragment_OutOfRoute OutOfRouteSection = (JourneyFragment_OutOfRoute) getFragmentManager ().findFragmentByTag ( tag == null ? "" : tag );
    		// Refresh out of route section (if valid)
    		if ( OutOfRouteSection != null )
    			OutOfRouteSection.refresh ();
		} // End else
	}
	
	/**
	 * Performs a scheduled and barcoded client call.<br>
	 * The purpose of this method is to perform a scheduled and barcoded client call from outside the fragment (API public access).
	 * 
	 * @param client	A {@link me.SyncWise.Android.Database.Clients Clients} object hosting the client whose barcode was scanned.
	 * @param clientDivision	A {@link me.SyncWise.Android.Database.ClientDivisions ClientDivisions} object hosting the client's division whose barcode was scanned.
	 */
	public void performScheduledBarcodedClientCall ( final Clients client , ClientDivisions clientDivision ) {
		// Iterate over the calls map
		for ( Day day : calls.keySet () ) {
			// Check if the day is the current day
			if ( ! day.isToday () )
				// Skip day
				continue;
			// Otherwise the day is the current day
			// Retrieve a reference to the calls list
			ArrayList < Call > list = calls.get ( day );
			// Iterate over the calls
			for ( final Call call : list )
				// Match the client, company and division codes
				if ( call.getClientDivision ().getClientCode ().equals ( clientDivision.getClientCode () )
						&& call.getClientDivision ().getCompanyCode ().equals ( clientDivision.getCompanyCode () )
						&& call.getClientDivision ().getDivisionCode ().equals ( clientDivision.getDivisionCode () ) ) {
					// Determine if a visit and a call are different
					final boolean enforceVisitType = quickAction_Visited.containsActionItem ( ActionItemID.CALL );
					// Determine if the selected client is blocked
					if ( call.getClient ().getClientStatus () != StatusUtils.isActive () ) {
						// Indicate that the visit cannot be performed
						Baguette.showText ( getActivity () ,
								AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.client_not_active_message ) ,
								Baguette.BackgroundColor.BLUE );
						// Exit method
						return;
					} // End if
					// Determine if the call is scheduled
					// AND if the visit is previously cancelled AND not visited (other than the cancellation call)
					if ( call.isScheduled () && call.isCancelled () && ! call.isVisited () ) {
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
											// Start a new visit with barcode
											startVisit ( call , enforceVisitType , ActionItemID.VISIT , null );
											break;
										case DialogInterface.BUTTON_NEGATIVE:
											// Dismiss dialog
											AppDialog.getInstance ().dismiss ();
											break;
										} // End switch
									}
								} );
					} // End if
					else
						// Start a new visit with barcode
						startVisit ( call , enforceVisitType , ActionItemID.VISIT , null );
					// Exit loop
					break;
				} // End if
		} // End for each
	}
	
	/**
	 * Performs an unscheduled and barcoded client call.<br>
	 * The purpose of this method is to perform an unscheduled and barcoded client call from outside the fragment (API public access).
	 * 
	 * @param unscheduledCall	Object holding the unscheduled call to perform.
	 */
	public void performUnscheduledBarcodedClientCall ( final Call unscheduledCall ) {
		// Determine if a visit and a call are different
		final boolean enforceVisitType = quickAction_Visited.containsActionItem ( ActionItemID.CALL );
		// Determine if the selected client is blocked
		if ( unscheduledCall.getClient ().getClientStatus () != StatusUtils.isActive () ) {
			// Indicate that the visit cannot be performed
			Baguette.showText ( getActivity () ,
					AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.client_not_active_message ) ,
					Baguette.BackgroundColor.BLUE );
			// Exit method
			return;
		} // End if
		// Start a new visit with barcode
		startVisit ( unscheduledCall , enforceVisitType , ActionItemID.VISIT , null );
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
	 * If no barcode reason is provided, the visit is considered as a barcode visit.
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
		if ( user.getUserType () == 11 && ! skipChecking && ! call.isScheduled () && DailySection != null && DailySection.hasRemainingCalls () ) {
			if(PermissionsUtils.getEnableOutOfRoutePasscode ( getActivity() , DatabaseUtils.getCurrentUserCode ( getActivity() ) , DatabaseUtils.getCurrentCompanyCode ( getActivity() ) ))
		 	{
			// Save required fields
			PasscodeResult passcodeResult = new PasscodeResult ( PasscodeResult.TYPE_OUT_OF_ROUTE , 1 , call , enforceVisitType , visitType , barcodeReason );
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
				call.isScheduled () ? VisitsUtils.Type.IN_ROUTE : VisitsUtils.Type.OUT_OF_ROUTE , // VisitType
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
		// Make another reference to the call
		Call _call = call;
		// Check if the call is scheduled
		if ( call.isScheduled () )
			// Indicate that the call is visited
			call.setVisited ( true );
		else {
    		// Perform post visit action in out of route section
    		// Compute the out of route section (having index 2) tag
    		tag = adapter != null ? adapter.getFragmentName ( viewPager , 2 ) : null;
    		// Retrieve a reference to the journey fragment of out of route calls based on its tag
    		JourneyFragment_OutOfRoute OutOfRouteSection = (JourneyFragment_OutOfRoute) getFragmentManager ().findFragmentByTag ( tag == null ? "" : tag );
    		// Retrieve the out of route call (if valid)
    		if ( OutOfRouteSection != null )
    			_call = OutOfRouteSection.getOutOfRouteCall ( _call );
    		// Check if the call is valid
    		if ( _call == null )
    			// Restore the call
    			_call = call;
		} // End else
		// Update visit location
		//TrackingUtils.forceLocationUpdate ( getActivity () , visit.getVisitID () );
		// Clear call action data
		CallAction.clearData ( getActivity () );
		
		// Retrieve the corresponding client user collection for the current client
		ClientUserCollection clientUserCollection = DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getClientUserCollectionDao ().queryBuilder ()
				.where ( ClientUserCollectionDao.Properties.ClientCode.eq ( _call.getClientDivision ().getClientCode () ) ,
						ClientUserCollectionDao.Properties.CompanyCode.eq ( _call.getClientDivision ().getCompanyCode () ) ,
						ClientUserCollectionDao.Properties.DivisionCode.eq ( _call.getClientDivision ().getDivisionCode () ) ).unique ();
		// Check if the current client is a client user collection 
		boolean isClientUserCollection = clientUserCollection != null;
		
		// Retrieve the company code of the user
		String companyCode = DatabaseUtils.getCurrentCompanyCode ( getActivity () );
		// Retrieve all divisions
		List < Divisions > allDivisions = DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getDivisionsDao ().queryBuilder ()
				.where ( DivisionsDao.Properties.CompanyCode.eq ( _call.getClientDivision ().getCompanyCode () ) ).list ();
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
			if ( division.getDivisionCode ().equals ( _call.getClientDivision ().getDivisionCode () ) && division.getCompanyCode ().equals ( _call.getClientDivision ().getCompanyCode () ) ) {
				// Set flag
				belongsToUser = true;
				// Exit loop
				break;
			} // End if
		
		try {
			// Create a new intent to start a new activity
			Intent intent = new Intent ( getActivity () , isClientUserCollection && ! belongsToUser ? CollectionDetailsActivity.class : App.getCallMenuActivityClass () );
			// Add the call object to the intent
			intent.putExtra ( isClientUserCollection && ! belongsToUser ? CollectionDetailsActivity.CALL : CallMenuActivity.CALL , _call );
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
	
	/**
	 * Performs finalization for a performed visit.
	 * 
	 * @param call	The client call performed. 
	 */
	public void postVisitClientCall ( final Call call ) {
		// Iterate over the calls map
		for ( Day day : calls.keySet () ) {
			// Determine if the current day is the call's day
			if ( ! day.getDay ().equals ( call.getDay () ) )
				// Invalid day
				continue;
			// Retrieve a reference to the calls list
			ArrayList < Call > list = calls.get ( day );
			// Iterate over all the calls
			for ( int i = 0 ; i < list.size () ; i ++ )
				// Check if the calls match
				if ( list.get ( i ).equals ( call ) )
					// Update the call
					list.get ( i ).update ( call );
			// Exit loop
			break;
		} // End for each
		// Notifies the attached observers that the underlying data has been changed
		( (MultipleListAdapter) getListAdapter () ).notifyDataSetChanged ();
	}
	
	/**
	 * Cancels the provided client call for the specified reason code.
	 * 
	 * @param call	The client call to cancel.
	 * @param reasonCode	The code of the client call cancellation reason.
	 */
	private void preCancelClientCall ( final Call call , final String reasonCode ) {
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
		// TODO : Details of a visit used to cancel a client call
		// Create a new visit for the current journey to cancel the client call
		Visits visit = new Visits ( null , // ID
				VisitsUtils.getVisitID ( today ) , // VisitID
				call.getClient ().getClientCode () , // ClientCode
				DatabaseUtils.getCurrentUserCode ( getActivity() ) , // UserCode
				today.getTime () , // StartDate
				today.getTime () , // EndDate
				StatusUtils.isDeleted () , // VisitStatus

				longitude!=null?longitude.toString():"" , // Longitude
			    latitude!=null?latitude.toString():"" , // Latitude

				"" , // Note
				reasonCode , // VisitReasonCode
				null , // TransactionReasonCode
				VisitsUtils.Barcode.NO , // IsBarcode
				VisitsUtils.Type.IN_ROUTE , // VisitType
				call.getClientDivision ().getDivisionCode () , // DivisionCode
				call.getClient ().getCompanyCode () , // CompanyCode
				call.getJourneyCode () , // JourneyCode
				IsProcessedUtils.isNotSync () , // IsProcessed
				today.getTime () ); // StampDate
		// Insert the visit in DB
		DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getVisitsDao ().insert ( visit );
		// Indicate that the call is cancelled
		call.setCancelled ( true );
		// Update visit location
		//TrackingUtils.forceLocationUpdate ( getActivity () , visit.getVisitID () );
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
		new FilterList ( newText ).execute ();
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
				// Match the filter with the client name, the client code (contains) and the client barcode (equals)
				if ( call.getClient ().getClientName ().toLowerCase ().contains ( getSearchQuery ().toLowerCase () ) 
						|| call.getClient ().getClientCode ().toLowerCase ().contains ( getSearchQuery ().toLowerCase () )
						|| ( call.getClientBarcodes () != null && call.getClientBarcodes ().contains ( getSearchQuery ().toLowerCase () ) ) )
					// Add the client to the list
					_list.add ( call );
			// Map the new calls list in the new calls map
			_calls.put ( day , _list );
		} // End for each
		// Return the new calls map
		return _calls;
	}
	
	/**
	 * AsyncTask helper class used to filter the list.
	 * 
	 * @author Elias
	 *
	 */
	private class FilterList extends AsyncTask < Void , Void , LinkedHashMap < Day , ArrayList < Call > > > {
		/**
		 * String used to host the empty list view label.
		 */
		private String emptyLabel;
		
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
		protected LinkedHashMap < Day , ArrayList < Call > > doInBackground ( Void ... params ) {
			try {
				// Initialize the empty label
				emptyLabel = AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.empty_visits_list_label );
				// Check if the list of calls is valid
				if ( calls == null || calls.isEmpty () )
					// Invalid calls list
					return null;
				// Check if the search query is empty
				if ( TextUtils.isEmpty ( searchQuery ) )
					// There is no filter
					return calls;
				// Otherwise there is a filter
				// Compute and return the filtered list based on the provided search query
				return filter ();
			} catch ( Exception exception ) {
				// Activity has ended, do nothing
			} // End of try-catch block
			return null;
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( LinkedHashMap < Day , ArrayList < Call > > calls ) {
			// Check if the result is valid
			if ( calls == null )
				// Invalid result, do nothing
				return;
			
			try {
				// Check if the search query has changed
				if ( ! getSearchQuery ().equals ( searchQuery ) )
					// Do nothing, as another AsyncTask is executing and will return the appropriate result
					return;
				// Otherwise, determine if the list adapter is valid
				if ( getListAdapter () == null )
					// Invalid list adapter
					return;

				// Declare an integer to track the position
				int index = 0;
				// Determine if the recycled list view items should be cleared
				boolean invalidate = false;
				// Retrieve a reference the main multiple list adapter
				MultipleListAdapter listAdapter = (MultipleListAdapter) getListAdapter ();
				// Iterate over all calls map
				for ( Day day : calls.keySet () ) {
					// Retrieve a reference to the calls list
					ArrayList < Call > list = calls.get ( day );
					// Retrieve a reference to the appropriate section
					Section section = listAdapter.getSection ( index );
					// Retrieve a reference to the appropriate adapter
					Adapter adapter = listAdapter.getAdapter ( index );
					
					// Determine if the list is empty
					// AND if the corresponding adapter is an the empty list adapter
					if ( list.isEmpty () && adapter instanceof EmptyListAdapter ) {
						// Do nothing
					} // End if
					// Otherwise, determine if the list is empty
					// AND if the corresponding adapter is NOT the empty list adapter
					else if ( list.isEmpty () && ! ( adapter instanceof EmptyListAdapter ) ) {
						// Set an empty list adapter for the current section
						listAdapter.addSection ( section , new EmptyListAdapter ( getActivity () , emptyLabel ) );
						invalidate = true;
					} // End else if
					// Otherwise, determine if the list is NOT empty
					// AND if the corresponding adapter is an empty list adapter
					else if ( ! list.isEmpty () && adapter instanceof EmptyListAdapter ) {
						// Set a daily call adapter for the current section
						listAdapter.addSection ( section , new DailyCallAdapter ( getActivity () , R.layout.journey_scheduled_client_item , new ArrayList < Call > ( list ) ) );
						invalidate = true;
					} // End else if
					// Otherwise, modify the adapter
					else {
						// Retrieve a reference the daily calls adapter
						DailyCallAdapter dailyCallAdapter = (DailyCallAdapter) adapter;
						// Clear the previous list of calls
						dailyCallAdapter.clear ();
						// Add the new filtered list of calls
						dailyCallAdapter.addAll ( list );
					} // End if
					// Increment the index
					index ++;
				} // End for each
				
				// Check if the recycled list view items should be cleared
				if ( invalidate )
					// Clear recycle bin by resetting the adapter
					getListView ().setAdapter ( listAdapter );
				// Notifies the attached observers that the underlying data has been changed
				listAdapter.notifyDataSetChanged ();
			} catch ( Exception exception ) {
				// Cannot cast to array adapter of call objects
			} finally {
				emptyLabel = null;
			} // End try-catch-finally block
		}
	}
	
}