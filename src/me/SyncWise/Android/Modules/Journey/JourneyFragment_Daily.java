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
import me.SyncWise.Android.Vibration;
import me.SyncWise.Android.Database.ClientUserCollection;
import me.SyncWise.Android.Database.ClientUserCollectionDao;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.Divisions;
import me.SyncWise.Android.Database.DivisionsDao;
import me.SyncWise.Android.Database.DivisionsUtils;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.JourneysUtils;
import me.SyncWise.Android.Database.Reasons;
import me.SyncWise.Android.Database.ReasonsUtils;
import me.SyncWise.Android.Database.StatusUtils;
import me.SyncWise.Android.Database.TargetHeadersUtils;
 
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Database.VisitReasons;
import me.SyncWise.Android.Database.VisitTypes;
import me.SyncWise.Android.Database.VisitTypesUtils;
import me.SyncWise.Android.Database.VisitsUtils;
import me.SyncWise.Android.Database.Visits;
import me.SyncWise.Android.Database.VisitsDao;
import me.SyncWise.Android.Modules.Call.CallAction;
import me.SyncWise.Android.Modules.Call.CallMenuActivity;
import me.SyncWise.Android.Modules.ClientsList.ClientInfo.ClientInfoActivity;
import me.SyncWise.Android.Modules.ClientsList.NewClient.NewClientActivity;
import me.SyncWise.Android.Modules.Collections.CollectionDetailsActivity;
 
import me.SyncWise.Android.Modules.Reason.ReasonActivity;
import me.SyncWise.Android.Modules.Target.TargetUpdate;
import me.SyncWise.Android.Utilities.ActivityInstance;
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
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
 
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
 

/**
 * Fragment used to represent a modular section for daily visits.
 * 
 * @author Elias
 *
 */
public class JourneyFragment_Daily extends JourneyFragment {
	
	/**
	 * Bundle key used to put/retrieve data.
	 */
	public static final String LAYOUT = JourneyFragment_Daily.class.getName () + ".LAYOUT";
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #calls}.
	 */
	private static final String CALLS = JourneyFragment_Daily.class.getName () + ".CALLS";
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.Journey.Call Call} objects used to define the daily calls.<br>
	 * <b>NOTE :</b> <br>
	 * If a filter is applied, this list remains untouched. A new list is generated, filtered and passed to the list adapter.
	 */
	private ArrayList < Call > calls;
	
	/**
	 * Reference to the calls list population task.
	 */
	private static PopulateList populateList;
	/**
	 * Boolean used to determine whether to display the passcode UI or not.
	 */
	protected boolean displayPasscode;
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
		// Display the current full date
		( (TextView) getView ().findViewById ( R.id.label_date ) ).setText ( "" + DateTime.getFullDate ( getActivity () , Calendar.getInstance () ) );
		
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state 
			calls = (ArrayList < Call >) ActivityInstance.readDataGson ( getActivity () , JourneyFragment_Daily.class.getName () , CALLS , new TypeToken < ArrayList < Call > > () {}.getType () );
		} // End if

		// Check if the list of calls is valid
		if ( calls == null && populateList == null ) {
			// Retrieve all the daily calls asynchronously
			populateList = new PopulateList ();
			populateList.execute ();
		} // End if
		// Check if there is a valid search query
		else if ( calls != null && ! getSearchQuery ().isEmpty () ) {
			// Set a new (empty) adapter
			setListAdapter ( new DailyCallAdapter ( getActivity () , R.layout.journey_scheduled_client_item , new ArrayList < Call > () ) );
			// Set a new adapter based on the saved filter, asynchronously
			new FilterList ( getSearchQuery () ).execute ();
		} // End else if
		else if ( calls != null ) {
			// Set a new adapter
			setListAdapter ( new DailyCallAdapter ( getActivity () , R.layout.journey_scheduled_client_item , new ArrayList < Call > ( calls ) ) );
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
				// Save the content of calls using GSON
				ActivityInstance.saveDataGson ( getActivity () , JourneyFragment_Daily.class.getName () , CALLS , calls );
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
	 * AsyncTask helper class used to populate the journey daily visits list with the appropriate calls belonging to the current date.
	 * 
	 * @author Elias
	 *
	 */
	private class PopulateList extends AsyncTask < Void , Void , ArrayList < Call > > {
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected ArrayList < Call > doInBackground ( Void ... params ) {
			try {
			
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
				// Compute the current journey day
				Day day = new Day ( DateTime.getDayAbbreviation ( getActivity () , today ) , DateTime.getDay ( getActivity () , today ) );
				// Indicate that the day is today
				day.setToday ( true );
				
				// Compute today's journey code
				String journeyCode = JourneysUtils.getJourneyCode ( prefixID , today );
				// Retrieve the calls list for the current journey day
				calls = getCalls ( userCode , companyCode , prefixID , week , day , today , journeyCode );
				
				// Check if there is a valid search query
				if ( ! getSearchQuery ().isEmpty () )
					// Compute and return the filtered list based on the provided search query
					return filter ();
				else
					// There is no filter, return the calls list
					return calls;
				
			} catch ( Exception exception ) {
				// Activity has ended, do nothing
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
		protected void onPostExecute ( ArrayList < Call > calls ) {
			// Check if the activity is valid
			if ( getActivity () == null || calls == null )
				// Invalid activity
				return;
			try {
				// Set a new adapter
				setListAdapter ( new DailyCallAdapter ( getActivity () , R.layout.journey_scheduled_client_item , new ArrayList < Call > ( calls ) ) );
			} catch ( Exception exception ) {
				// Activity has ended, do nothing
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
		Call selectedCall = (Call) getListAdapter ().getItem ( position );
		// Determine if the client is visited (or cancelled)
		if ( selectedCall.isVisited () || selectedCall.isCancelled () )
			// The client is either visited or cancelled
			// Display the appropriate quick action widget
			quickAction_Visited.show ( view , selectedCall , getResources () );
		else
			// The client is NOT visited or cancelled
			// Display the appropriate quick action widget
			quickAction_Unvisited.show ( view , selectedCall , getResources () );
		
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
						clientCodes = ( cursor.getString(0) );
					 	headers.add ( clientCodes );
					} while ( cursor.moveToNext () );
				} // End if
				// Close the cursor
				cursor.close ();
				cursor = null;
				
				//credit client
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
//								AppDialog.getInstance ().displayAlert ( getActivity () 
//										,AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.client_credit_client_message ) 
//										,codeName
//									  ,	AppDialog.ButtonsType.OkCancel ,
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
				// Determine if a visit and a call are different
//				final boolean enforceVisitType = source.containsActionItem ( ActionItemID.CALL );
//				final int visitType = actionId;
//				PasscodeResult passcodeResult = new PasscodeResult ( PasscodeResult.TYPE_OUT_OF_ROUTE , 2 , call , enforceVisitType , visitType , null );
				( (JourneyActivity) getActivity () ).promptPasscode1 ( codeName );
				
				 
				
//				
//				// Declare and initialize an alert dialog builder
//				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder ( getActivity() );
//				// By default, the alert dialog cannot be cancelled UNLESS it has no buttons
//				alertDialogBuilder.setCancelable ( false );
//				// Set the title
//				alertDialogBuilder.setTitle ( R.string.warning_label );
//				// Set the description
//				alertDialogBuilder.setMessage ( codeName );
//				// Map the positive and negative buttons
//				alertDialogBuilder.setPositiveButton ( R.string.ok_label , null );
//				alertDialogBuilder.setNegativeButton ( R.string.use_passcode , new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick ( DialogInterface dialog , int which ) {
//			    		// Set flag
//			    		displayPasscode = true;
//			    		// Initialize the tertiary view
//			    		initializeTertiaryView ( false );
//			    		// Retrieve a reference to the tertiary view
//			    		View tertiaryView =  getActivity().findViewById ( R.id.layout_passcode );
//			    		// Display the tertiary view
//			    		tertiaryView.setVisibility ( View.VISIBLE );
//			    		// Animate the tertiary view
//			    		tertiaryView.startAnimation ( getViewAnimationIn() );
//			    		// Refresh the action bar
//			    		//invalidateOptionsMenu ();
//					}
//				} );
//				// Create and show the alert dialog
//				alertDialogBuilder.create ().show ();
//				
				
				
				
				
        	//for( String c : headers )
			//{
			//	Toast.makeText( getActivity(),codes , Toast.LENGTH_LONG).show();
				// Display call confirmation
				
				
				
				
//				AppDialog.getInstance ().displayAlert ( getActivity () ,
//						AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.client_cash_client_message )  ,codeName
//					  ,
//						AppDialog.ButtonsType.OK ,
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick ( DialogInterface dialog , int which ) {
//							//	 Determine the clicked button
//								switch ( which ) {
//								case DialogInterface.BUTTON_NEUTRAL:
//									
//									break;
//								} // End switch
//							}
//						} );
//			
			
				
				
                }
        	   }
           
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
	/**
	 * Initializes the passcode (tertiary) view.
	 * 
	 * @param restore	Boolean used to determine if the tertiary view is being initialized for the first time or is being restored.
	 */
	protected void initializeTertiaryView ( final boolean restore ) {
		// Retrieve a reference to the parent view
		View parent =getActivity ().findViewById ( R.id.layout_passcode );
		// Retrieve a reference to the passcode edit text
		EditText passcodeEditText = (EditText) parent.findViewById ( R.id.edittext_passcode );		
		// Retrieve a reference to the passcode title label
		TextView titleLabel = (TextView) parent.findViewById ( R.id.label_passcode );
		// Retrieve a reference to the passcode message label
		TextView messageLabel = (TextView) parent.findViewById ( R.id.label_passcode_message );
		
		// Display the title
		titleLabel.setText ( AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.passcode_label ) );
		// Display the message
		messageLabel.setText ( AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.movement_passcode_required_message ) );
		// Check if this the first time creation
		if ( ! restore )
			// Clear any previous entries
			passcodeEditText.setText ( "" );
		// Enable the save icon
		parent.findViewById ( R.id.icon_save_passcode ).setEnabled ( true );
		// Enable the edit text
		passcodeEditText.setEnabled ( true );
		// Display the field hint
		passcodeEditText.setHint ( AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.movement_passcode_hint ) );
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
		View tertiary = getActivity ().findViewById ( R.id.layout_passcode );
		// Retrieve a reference to the passcode edit text
		EditText passcodeEditText = (EditText) tertiary.findViewById ( R.id.edittext_passcode );
		// Store the passcode
		String passcode = passcodeEditText.getText ().toString ().trim ();
		
		// Validate pass code
		//if ( ! UserPasswordsUtils.validateTimePasswordClients ( getActivity () , UserPasswordsUtils.TransactionHeaderType.TRANSACTION , TransactionHeadersUtils.Type.SALES_INVOICE , passcode , call.getClient ().getClientCode ()) ) {
			// Indicate that the passcode is not valid
		//	Baguette.showText ( getActivity () ,
		//			AppResources.getInstance ( getActivity () ).getString ( getActivity () , R.string.time_passcode_invalid_message ) ,
		//			Baguette.BackgroundColor.RED );
			// Exit method
		//	return;
		//} // End if
		
		// Reset flag
		displayPasscode = false;
		
		// Reset field
		passcodeEditText.setText ( "" );
		// Disable the save icon
		tertiary.findViewById ( R.id.icon_save_passcode ).setEnabled ( false );
		// Hide the software keyboard
	//( (InputMethodManager) getActivity ().getSystemService ( INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow ( tertiary.getWindowToken (), 0 );
        
		// Hide the tertiary view
        tertiary.startAnimation ( getViewAnimationOut ( tertiary ) );
		// Indicate that the save was successful
		Vibration.vibrate ( getActivity () );
		
		// Retrieve the unpaid invoices
		//List < TransactionHeaders > invoices = DatabaseUtils.getInstance ( getActivity () ).getDaoSession ().getTransactionHeadersDao ().queryBuilder ()
	//	.where ( TransactionHeadersDao.Properties.VisitID.eq ( visit.getVisitID () ) ,
	//			TransactionHeadersDao.Properties.RemainingAmount.gt ( 0 ) ,
	//			TransactionHeadersDao.Properties.TransactionType.eq ( TransactionHeadersUtils.Type.SALES_INVOICE ) ,
	//			TransactionHeadersDao.Properties.IsProcessed.eq ( IsProcessedUtils.isNotSync () ) ,
	//			TransactionHeadersDao.Properties.TransactionStatus.eq ( StatusUtils.isAvailable () ) ).list ();
		// Iterate over the list
//		for ( TransactionHeaders invoice : invoices )
			// Update the passcode
//			invoice.setTransactionPasswordCode ( passcode );
		// Update the invoices
//	DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionHeadersDao ().updateInTx ( invoices );
		// Refresh the visit object
//		DatabaseUtils.getInstance ( this ).getDaoSession ().getVisitsDao ().refresh ( visit );
		// End the visit
	//	visit.setEndDate ( Calendar.getInstance ().getTime () );
		// Update the visit in DB
	//	DatabaseUtils.getInstance ( this ).getDaoSession ().getVisitsDao ().update ( visit );
		
	
    	// Call this to set the result that your activity will return to its caller
    // ( RESULT_OK , new Intent ().putExtra ( VISIT , visit ).putExtra ( CALL , call ) );
    	// Exit activity
		getActivity ().finish ();
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
    	case REQUEST_CODE_NEW_CLIENT:
    		// Check if the pass code result is valid
    		if ( data.getSerializableExtra ( NewClientActivity.PASS_CODE_RESULT ) != null )
    			// Prompt pass code
    			( (JourneyActivity) getActivity () ).promptPasscode ( (PasscodeResult) data.getSerializableExtra ( NewClientActivity.PASS_CODE_RESULT ) );
    		// Exit method
    		return;
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
		// Perform post visit action
		postVisitClientCall ( call );
		// Perform post visit action in weekly section
		// Retrieve a reference of the view pager (which has the app fragment pager adapter linked to)
		// The view pager is used to retrieve a reference to the weekly fragment, via its tag
		ViewPager viewPager = (ViewPager) getActivity ().findViewById ( R.id.pager );
		// Retrieve a reference to the app fragment pager adapter
		// This adapter can compute the expected weekly fragment tag so it can be referenced (via its tag)
		AppFragmentPagerAdapter adapter = viewPager != null ? (AppFragmentPagerAdapter) viewPager.getAdapter () : null;
		// Compute the weekly section (having index 1) tag
		String tag = adapter != null ? adapter.getFragmentName ( viewPager , 1 ) : null;
		// Retrieve a reference to the journey fragment of weekly calls based on its tag
		JourneyFragment_Weekly weeklySection = (JourneyFragment_Weekly) getFragmentManager ().findFragmentByTag ( tag == null ? "" : tag );
		// Perform post visit action in weekly section (if valid)
		if ( weeklySection != null )
			weeklySection.postVisitClientCall ( call );
	}
	
	/**
	 * Determines if there are any remaining calls.
	 * 
	 * @return	Boolean indicating if there is at least one remaining call.
	 */
	public boolean hasRemainingCalls () {
		try {
			// Iterate over the calls
			for ( Call call : calls )
				// The call should either be visited or cancelled
				if ( ! call.isVisited () && ! call.isCancelled () )
					// At least one remaining call
					return true;
			// Otherwise no remaining calls
			return false;
		} catch ( Exception exception ) {
			// Error occurred
			return false;
		}
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
		// Check if a time pass code is required
		if ( ! skipChecking && call.getUseTimePasscode () ) {
			// Save required fields
			PasscodeResult passcodeResult = new PasscodeResult ( PasscodeResult.TYPE_USER_ACCOUNT , 0 , call , enforceVisitType , visitType , barcodeReason );
//			// Prompt pass code
//			( (JourneyActivity) getActivity () ).promptPasscode ( passcodeResult );
			Intent intent = new Intent ( getActivity () , NewClientActivity.class );
			intent.putExtra ( NewClientActivity.PASS_CODE_RESULT , passcodeResult );
			startActivityForResult ( intent , REQUEST_CODE_NEW_CLIENT );
			// Exit method
			return;
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
				VisitsUtils.Type.IN_ROUTE , // VisitType
				call.getJourneyCode () , // JourneyCode
				call.getClientDivision ().getDivisionCode () , // DivisionCode
				call.getClient ().getCompanyCode () , // Company Code
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
	
	/**
	 * Performs finalization for a performed visit.
	 * 
	 * @param call	The client call performed. 
	 */
	@SuppressWarnings("unchecked")
	public void postVisitClientCall ( final Call call ) {
		// Iterate over all the calls
		for ( int i = 0 ; i < calls.size () ; i ++ )
			// Check if the calls match
			if ( calls.get ( i ).equals ( call ) )
				// Update the call
				calls.get ( i ).update ( call );
		// Notifies the attached observers that the underlying data has been changed
		( (ArrayAdapter < Call >) getListAdapter () ).notifyDataSetChanged ();
	}
	
	/**
	 * Cancels the provided client call for the specified reason code.
	 * 
	 * @param call	The client call to cancel.
	 * @param reasonCode	The code of the client call cancellation reason.
	 */
	private void preCancelClientCall ( final Call call , final String reasonCode ) {
		// Retrieve current date
		Calendar today = Calendar.getInstance ();
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
				call.getJourneyCode () , // JourneyCode
				call.getClientDivision ().getDivisionCode () , // DivisionCode
				call.getClient ().getCompanyCode () , // CompanyCode
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
	 * Filters the {@link #calls} list based on the indicated {@link #searchQuery}.<br>
	 * <b>NOTE : </b> This method does not perform any kind of integrity check what so ever !
	 * 
	 * @return	List of {@link me.SyncWise.Android.Modules.Journey.Call Call} objects filtered according to the search filter.
	 */
	@SuppressLint("DefaultLocale") 
	public ArrayList < Call > filter () {
		// Declare and initialize a new calls list
		ArrayList < Call > _calls = new ArrayList < Call > ();
		// Iterate over all the calls
		for ( Call call : calls )
			// Match the filter with the client name, the client code (contains) and client barcode (equals)
			if ( call.getClient ().getClientName ().toLowerCase ().contains ( getSearchQuery ().toLowerCase () ) 
					|| call.getClient ().getClientCode ().toLowerCase ().contains ( getSearchQuery ().toLowerCase () )
					|| ( call.getClientBarcodes () != null && call.getClientBarcodes ().contains ( getSearchQuery ().toLowerCase () ) ) )
				// Add the client to the list
				_calls.add ( call );
		// Return the new calls list
		return _calls;
	}
	
	/**
	 * AsyncTask helper class used to filter the list.
	 * 
	 * @author Elias
	 *
	 */
	private class FilterList extends AsyncTask < Void , Void , ArrayList < Call > > {
		
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
		protected ArrayList < Call > doInBackground ( Void ... params ) {
			try {
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
		@SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute ( ArrayList < Call > calls ) {
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
				// Check if the list adapter is an ArrayAdapter
				if ( ! ( getListAdapter () instanceof ArrayAdapter < ? > ) )
					// Invalid list adapter
					return;

				// Clear the previous list of calls
				( (ArrayAdapter < Call >) getListAdapter () ).clear ();
				// Add the new filtered list of calls
				( (ArrayAdapter < Call >) getListAdapter () ).addAll ( calls );
				// Notifies the attached observers that the underlying data has been changed
				( (ArrayAdapter < Call >) getListAdapter () ).notifyDataSetChanged ();
			} catch ( Exception exception ) {
				// Cannot cast to array adapter of call objects
			} // End try-catch block
		}
		
	}
	
}