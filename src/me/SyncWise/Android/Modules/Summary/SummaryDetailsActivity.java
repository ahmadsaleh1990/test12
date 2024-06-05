/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Summary;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import me.SyncWise.Android.ActivityTransition;
import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.AppToast;
import me.SyncWise.Android.DateTime;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.ClientDivisions;
import me.SyncWise.Android.Database.ClientDivisionsDao;
import me.SyncWise.Android.Database.Clients;
import me.SyncWise.Android.Database.CollectionHeadersDao;
import me.SyncWise.Android.Database.CurrenciesDao;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.ObjectiveAchievements;
import me.SyncWise.Android.Database.ObjectiveAchievementsDao;
import me.SyncWise.Android.Database.ObjectivesDao;
import me.SyncWise.Android.Database.ReasonsDao;
import me.SyncWise.Android.Database.VisitsDao;
import me.SyncWise.Android.Gson.BaseTimerListActivity;
import me.SyncWise.Android.Modules.ClientStock.ClientStockActivity;
import me.SyncWise.Android.Modules.ClientStock.ClientStockAdapter;
import me.SyncWise.Android.Modules.ClientStock.ClientStockDetailsActivity;
import me.SyncWise.Android.Modules.Collections.CollectionDetailsViewActivity;
import me.SyncWise.Android.Modules.Collections.CollectionViewAdapter;
import me.SyncWise.Android.Modules.DirectReturn.DirectReturnActivity;
import me.SyncWise.Android.Modules.DirectReturn.DirectReturnAdapter;
import me.SyncWise.Android.Modules.DirectReturn.DirectReturnDetailsActivity;
import me.SyncWise.Android.Modules.Journey.Call;
import me.SyncWise.Android.Modules.Objective.ClientObjectiveActivity;
import me.SyncWise.Android.Modules.Return.ReturnActivity;
import me.SyncWise.Android.Modules.Return.ReturnAdapter;
import me.SyncWise.Android.Modules.Return.ReturnDetailsActivity;
import me.SyncWise.Android.Modules.SalesInvoice.SalesInvoiceActivity;
import me.SyncWise.Android.Modules.SalesInvoice.SalesInvoiceAdapter;
import me.SyncWise.Android.Modules.SalesInvoice.SalesInvoiceDetailsActivity;
import me.SyncWise.Android.Modules.SalesOrder.SalesOrderActivity;
import me.SyncWise.Android.Modules.SalesOrder.SalesOrderAdapter;
import me.SyncWise.Android.Modules.SalesOrder.SalesOrderDetailsActivity;
import me.SyncWise.Android.Modules.SalesRFR.SalesRFRActivity;
import me.SyncWise.Android.Modules.SalesRFR.SalesRFRAdapter;
import me.SyncWise.Android.Modules.SalesRFR.SalesRFRDetailsActivity;
import me.SyncWise.Android.SectionsList.MultipleListAdapter;
import me.SyncWise.Android.SectionsList.Section;
import me.SyncWise.Android.Utilities.ClientCard;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Activity implemented in order to display the summary details for a specific client.
 * 
 * @author Elias
 *
 */
public class SummaryDetailsActivity extends BaseTimerListActivity implements OnItemSelectedListener {

	/**
	 * Bundle key used to put/retrieve the client object.
	 */
	public static final String CLIENT = SummaryDetailsActivity.class.getName () + ".CLIENT";
	
	/**
	 * Bundle key used to put/retrieve the summary list.
	 */
	public static final String SUMMARY_LIST = SummaryDetailsActivity.class.getName () + ".SUMMARY_LIST";
	
	/**
	 * Bundle key used to put/retrieve the summary ID.
	 */
	public static final String SUMMARY_ID = SummaryDetailsActivity.class.getName () + ".SUMMARY_ID";
	
	/**
	 * Integer holding the selected summary ID.
	 */
	protected Integer summaryID;
	
	/**
	 * Reference to the spinner holding the summary drop down list.
	 */
	private Spinner summarySpinner;
	
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
		setContentView ( R.layout.summary_details_activity );
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.summary_activity_title ) );
		
		// Check if the client object and the summary list are valid
		if ( getIntent ().getSerializableExtra ( CLIENT ) == null || getIntent ().getSerializableExtra ( SUMMARY_LIST ) == null ) {
			// Indicate that the activity cannot be displayed
			new AppToast ( this ).setIcon ( R.drawable.warning ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.invalid_selection_label ) ).show ();
			// Finish activity
			finish ();
			// Exit method
			return;
		} // End if
		
		// Retrieve a reference to the spinner
		summarySpinner = (Spinner) findViewById ( R.id.spinner );
		// Set the summary ID provided from the intent
		summaryID = (Integer) getIntent ().getSerializableExtra ( SUMMARY_ID );
		
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state
			summaryID = (Integer) savedInstanceState.getSerializable ( SUMMARY_ID );
		} // End if
		
		// Initialize the client card
		ClientCard.initializeClientCard ( this , (Clients) getIntent ().getSerializableExtra ( CLIENT ) );

		// Display the summary selection label
		( (TextView) findViewById ( R.id.summary_selection_label ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.summary_selection_label ) );
		
		// Enable fast scrolling
		getListView ().setFastScrollEnabled ( true );
		// Define the empty list view
		getListView ().setEmptyView ( findViewById ( R.id.empty_list_view ) );
		// Display empty list label
		( (TextView) findViewById ( R.id.empty_list_view ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_summary_list_label ) );
		
		// Retrieve the summaries list
		ArrayList < Summary > summaries = (ArrayList < Summary >) getIntent ().getSerializableExtra ( SUMMARY_LIST );
    	// Declare and initialize a new summary adapter used to populate the summaries drop down list
		SummaryAdapter summaryAdapter = new SummaryAdapter ( this , android.R.layout.simple_spinner_item , summaries );
		// Sets the layout resource to create the drop down views
		summaryAdapter.setDropDownViewResource ( R.layout.data_management_activity_connection_item );
    	// Set the spinner adapter
		summarySpinner.setAdapter ( summaryAdapter );
		// Determine if a summary ID is set
		if ( summaryID != null ) {
			// Set the appropriate spinner selection
			int count = 0;
			// Iterate over all summaries
			for ( Summary summary : summaries ) {
				// Match the summary IDs
				if ( summary.getId () == summaryID ) {
					// Set the spinner selection
					summarySpinner.setSelection ( count );
					// Exit the loop
					break;
				} // End if
				// Otherwise increment the counter
				count ++;
			} // End for each
		} // End if
		// Set the spinner listener
		summarySpinner.setOnItemSelectedListener ( this );
		
		// Determine if NO summary ID is set
		if ( summaryID == null )
			// Set the summary ID from the first summary in the list
			summaryID = summaries.get ( 0 ).getId ();
		// Refresh list asynchronously
		new PopulateList ().execute ();
	}
	
    /*
     * Called to retrieve per-instance state from an activity before being killed so that the state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle).
     *
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState ( Bundle outState ) {
    	// Superclass method call
    	super.onSaveInstanceState ( outState );
    	// Save the content of summaryID in the outState bundle
    	outState.putSerializable ( SUMMARY_ID , summaryID );
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
			summaryID = null;
			summarySpinner = null;
		} // End if
	}
	
	/*
	 * Callback method to be invoked when an item in this view has been selected.
	 *
	 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemSelected ( AdapterView < ? > parent , View view , int position , long id ) {
		// Check if the selected summary ID is valid
		// AND if the previous summary ID is similar to the new summary ID
		if ( summaryID != null && summaryID.equals ( ( (Summary) summarySpinner.getAdapter ().getItem ( position ) ).getId () ) )
			// Similar summaries, do nothing
			return;
		
		// Otherwise, a new summary is selected
		// Set the new summary id
		summaryID = ( (Summary) summarySpinner.getAdapter ().getItem ( position ) ).getId ();
		
		// Refresh list asynchronously
		new PopulateList ().execute ();
	}

	/*
	 * Callback method to be invoked when the selection disappears from this view.
	 *
	 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android.widget.AdapterView)
	 */
	public void onNothingSelected ( AdapterView < ? > parent ) {
		// Do nothing
	}
	
    /*
     * This method will be called when an item in the list is selected.
     *
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
	@Override
    protected void onListItemClick ( ListView listView , View view , final int position , long id ) {
		// Create a new intent to start a new activity
		Intent intent = null;
		try {
			// Retrieve the client object
			Clients client = (Clients) getIntent ().getSerializableExtra ( CLIENT );
			// Query the appropriate client division
			ClientDivisions clientDivision = DatabaseUtils.getInstance ( this ).getDaoSession ().getClientDivisionsDao ().queryBuilder ()
					.where ( ClientDivisionsDao.Properties.ClientCode.eq ( client.getClientCode () ) ,
							ClientDivisionsDao.Properties.CompanyCode.eq ( client.getCompanyCode () ) , 
							ClientDivisionsDao.Properties.DivisionCode.eq ( client.getDivisionCode () ) ).uniqueOrThrow ();
			// Create a call object for the Sales Order module
			Call call = new Call ( "" , client , clientDivision );
			// Determine the summary to display
			switch ( summaryID ) {
			case Summary.ID.SALES_RFR:
				// Create a new intent to start a new activity
				intent = new Intent ( this , SalesRFRDetailsActivity.class );
				// Add the client object to the intent
				intent.putExtra ( SalesRFRDetailsActivity.CALL , call );
				// Add the visit object to the intent
				intent.putExtra ( SalesRFRDetailsActivity.VISIT , DatabaseUtils.getInstance ( this ).getDaoSession ().getVisitsDao ().queryBuilder ()
						.where ( VisitsDao.Properties.VisitID.eq ( ( (SalesRFRAdapter.ViewHolder) view.getTag () ).visitID ) ).uniqueOrThrow () );
				// Add the request code to the intent
				intent.putExtra ( SalesRFRDetailsActivity.REQUEST_CODE , SalesRFRDetailsActivity.REQUEST_CODE_INFO );
				// Add the transaction header code to the intent
				intent.putExtra ( SalesRFRDetailsActivity.TRANSACTION_HEADER_CODE , ( (SalesRFRAdapter.ViewHolder) view.getTag () ).code );
				// Add the view only flag to the intent
				intent.putExtra ( SalesRFRDetailsActivity.IS_VIEW_ONLY , true );
				// Start the new activity
				startActivity ( intent );
				// Specify an explicit transition animation to perform next
				ActivityTransition.SlideOutLeft ( this );
				break;
			case Summary.ID.SALES_ORDERS:
				// Create a new intent to start a new activity
				intent = new Intent ( this , SalesOrderDetailsActivity.class );
				// Add the client object to the intent
				intent.putExtra ( SalesOrderDetailsActivity.CALL , call );
				// Add the visit object to the intent
				intent.putExtra ( SalesOrderDetailsActivity.VISIT , DatabaseUtils.getInstance ( this ).getDaoSession ().getVisitsDao ().queryBuilder ()
						.where ( VisitsDao.Properties.VisitID.eq ( ( (SalesOrderAdapter.ViewHolder) view.getTag () ).visitID ) ).uniqueOrThrow () );
				// Add the request code to the intent
				intent.putExtra ( SalesOrderDetailsActivity.REQUEST_CODE , SalesOrderDetailsActivity.REQUEST_CODE_INFO );
				// Add the transaction header code to the intent
				intent.putExtra ( SalesOrderDetailsActivity.TRANSACTION_HEADER_CODE , ( (SalesOrderAdapter.ViewHolder) view.getTag () ).code );
				// Add the view only flag to the intent
				intent.putExtra ( SalesOrderDetailsActivity.IS_VIEW_ONLY , true );
				// Start the new activity
				startActivity ( intent );
				// Specify an explicit transition animation to perform next
				ActivityTransition.SlideOutLeft ( this );
				break;
			case Summary.ID.SALES_INVOICE:
				// Create a new intent to start a new activity
				intent = new Intent ( this , SalesInvoiceDetailsActivity.class );
				// Add the client object to the intent
				intent.putExtra ( SalesInvoiceDetailsActivity.CALL , call );
				// Add the visit object to the intent
				intent.putExtra ( SalesInvoiceDetailsActivity.VISIT , DatabaseUtils.getInstance ( this ).getDaoSession ().getVisitsDao ().queryBuilder ()
						.where ( VisitsDao.Properties.VisitID.eq ( ( (SalesInvoiceAdapter.ViewHolder) view.getTag () ).visitID ) ).uniqueOrThrow () );
				// Add the request code to the intent
				intent.putExtra ( SalesInvoiceDetailsActivity.REQUEST_CODE , SalesInvoiceDetailsActivity.REQUEST_CODE_INFO );
				// Add the transaction header code to the intent
				intent.putExtra ( SalesInvoiceDetailsActivity.TRANSACTION_HEADER_CODE , ( (SalesInvoiceAdapter.ViewHolder) view.getTag () ).code );
				// Add the view only flag to the intent
				intent.putExtra ( SalesInvoiceDetailsActivity.IS_VIEW_ONLY , true );
				// Start the new activity
				startActivity ( intent );
				// Specify an explicit transition animation to perform next
				ActivityTransition.SlideOutLeft ( this );
				break;
			case Summary.ID.SALES_RETURN:
				// Create a new intent to start a new activity
				intent = new Intent ( this , ReturnDetailsActivity.class );
				// Add the client object to the intent
				intent.putExtra ( ReturnDetailsActivity.CALL , call );
				// Add the visit object to the intent
				intent.putExtra ( ReturnDetailsActivity.VISIT , DatabaseUtils.getInstance ( this ).getDaoSession ().getVisitsDao ().queryBuilder ()
						.where ( VisitsDao.Properties.VisitID.eq ( ( (ReturnAdapter.ViewHolder) view.getTag () ).visitID ) ).uniqueOrThrow () );
				// Add the request code to the intent
				intent.putExtra ( ReturnDetailsActivity.REQUEST_CODE , ReturnDetailsActivity.REQUEST_CODE_INFO );
				// Add the transaction header code to the intent
				intent.putExtra ( ReturnDetailsActivity.TRANSACTION_HEADER_CODE , ( (ReturnAdapter.ViewHolder) view.getTag () ).transactionCode );
				// Add the view only flag to the intent
				intent.putExtra ( ReturnDetailsActivity.IS_VIEW_ONLY , true );
				// Start the new activity
				startActivity ( intent );
				// Specify an explicit transition animation to perform next
				ActivityTransition.SlideOutLeft ( this );
				break;
			case Summary.ID.DIRECT_RETURN:
				// Create a new intent to start a new activity
				intent = new Intent ( this , DirectReturnDetailsActivity.class );
				// Add the client object to the intent
				intent.putExtra ( DirectReturnDetailsActivity.CALL , call );
				// Add the visit object to the intent
				intent.putExtra ( DirectReturnDetailsActivity.VISIT , DatabaseUtils.getInstance ( this ).getDaoSession ().getVisitsDao ().queryBuilder ()
						.where ( VisitsDao.Properties.VisitID.eq ( ( (DirectReturnAdapter.ViewHolder) view.getTag () ).visitID ) ).uniqueOrThrow () );
				// Add the request code to the intent
				intent.putExtra ( DirectReturnDetailsActivity.REQUEST_CODE , DirectReturnDetailsActivity.REQUEST_CODE_INFO );
				// Add the transaction header code to the intent
				intent.putExtra ( DirectReturnDetailsActivity.TRANSACTION_HEADER_CODE , ( (DirectReturnAdapter.ViewHolder) view.getTag () ).code );
				// Add the view only flag to the intent
				intent.putExtra ( DirectReturnDetailsActivity.IS_VIEW_ONLY , true );
				// Start the new activity
				startActivity ( intent );
				// Specify an explicit transition animation to perform next
				ActivityTransition.SlideOutLeft ( this );
				break;
			case Summary.ID.COLLECTION:
				// Create a new intent to start a new activity
				intent = new Intent ( this , CollectionDetailsViewActivity.class );
				// Add the client object to the intent
				intent.putExtra ( CollectionDetailsViewActivity.COLLECTION_CODE , ( (CollectionViewAdapter.ViewHolder) view.getTag () ).code );
				// Start the new activity
				startActivity ( intent );
				// Specify an explicit transition animation to perform next
				ActivityTransition.SlideOutLeft ( this );
				break;
				
			case Summary.ID.OBJECTIVE:
				// Create a new intent to start a new activity
				 intent = new Intent ( this , ClientObjectiveActivity.class );
				// Add the client object to the intent
				intent.putExtra ( ClientObjectiveActivity.CLIENT , client );
				// Add the complete objective to the intent
				intent.putExtra ( ClientObjectiveActivity.ViewComplete , true );
				// Add the IS VIEW flag to the intent
				intent.putExtra ( ClientObjectiveActivity.IS_VIEW_OR_ADD , true );
				// Add the IS VIEW flag to the intent
				intent.putExtra ( ClientObjectiveActivity.IS_VIEW , true );
				// Start the new activity
				startActivity ( intent );
			 	// Specify an explicit transition animation to perform next
				ActivityTransition.SlideOutLeft ( this );
				break;
			case Summary.ID.CLIENT_STOCK_COUNT:
			case Summary.ID.CLIENT_ASSET:	
				// Create a new intent to start a new activity
				intent = new Intent ( this , ClientStockDetailsActivity.class );
				// Add the client object to the intent
				intent.putExtra ( ClientStockDetailsActivity.CALL , call );
				// Add the visit object to the intent
				intent.putExtra ( ClientStockDetailsActivity.VISIT , DatabaseUtils.getInstance ( this ).getDaoSession ().getVisitsDao ().queryBuilder ()
						.where ( VisitsDao.Properties.VisitID.eq ( ( (ClientStockAdapter.ViewHolder) view.getTag () ).visitID ) ).uniqueOrThrow () );
				// Add the request code to the intent
				intent.putExtra ( ClientStockDetailsActivity.REQUEST_CODE , SalesOrderDetailsActivity.REQUEST_CODE_INFO );
				// Add the transaction header code to the intent
				intent.putExtra ( ClientStockDetailsActivity.CLIENT_STOCK_HEADER_CODE , ( (ClientStockAdapter.ViewHolder) view.getTag () ).code );
				// Add the view only flag to the intent
				intent.putExtra ( ClientStockDetailsActivity.IS_VIEW_ONLY , true );
				// Add the item type
				intent.putExtra ( ClientStockDetailsActivity.ITEMS_LIST_MODE , ( (ClientStockAdapter.ViewHolder) view.getTag () ).itemType == -1 ? 0 : ( (ClientStockAdapter.ViewHolder) view.getTag () ).itemType );
				// Start the new activity
				startActivity ( intent );
				// Specify an explicit transition animation to perform next
				ActivityTransition.SlideOutLeft ( this );
				break;
			default:
				// Invalid summary ID
				break;
			} // End switch
		} catch ( Exception exception ) {
			// Indicate that the activity cannot be displayed
			new AppToast ( this ).setIcon ( R.drawable.warning ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.invalid_selection_label ) ).show ();
		} // End of try-catch block
	}
	
	/**
	 * Populates the summary details list by generating a map of cursors holding collection headers, that belongs for a specific day, for the specified user.
	 * 
	 * @param client	The client object for which the collection headers belong to.
	 * @return	Cursors holding collection headers data set mapped to their appropriate dates.
	 */
	private Object populateCollectionHeadersList ( final Clients client ) {
		// Compute the selection statement
		String selection = "SELECT " + CollectionHeadersDao.Properties.CollectionDate.columnName;
		// Compute the SQL statement
		String SQL = " FROM " + CollectionHeadersDao.TABLENAME + " WHERE (" + CollectionHeadersDao.Properties.ClientCode.columnName + " = ? OR "+ CollectionHeadersDao.Properties.ClientCode.columnName + " = ?) AND " + CollectionHeadersDao.Properties.ParentCode.columnName + " IS NULL ";
		// Compute the order by clause
		String orderBy = " ORDER BY " + CollectionHeadersDao.Properties.CollectionDate.columnName;
		// Compute the selection arguments
		String selectionArgs [] = new String [] {
				client.getParentCode (),
				client.getClientCode()
		};
		// Declare and initialize a cursor in order to query DB
		Cursor cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
				.rawQuery ( selection + SQL + orderBy , selectionArgs );
		
		// Declare and initialize a list of calendars, used to compute the collection headers dates
		List < Calendar > dates = new ArrayList < Calendar > ();
		// Move the cursor to the first raw
		if ( cursor.moveToFirst () ) {
			// Iterate over all raws
			do {
				// Compute the current blank visit date
				Calendar collectionHeaderDate = Calendar.getInstance ();
				collectionHeaderDate.setTimeInMillis ( cursor.getLong ( cursor.getColumnIndex ( CollectionHeadersDao.Properties.CollectionDate.columnName ) ) );
				collectionHeaderDate.set ( Calendar.HOUR_OF_DAY , 0 );
				collectionHeaderDate.set ( Calendar.MINUTE , 0 );
				collectionHeaderDate.set ( Calendar.SECOND , 0 );
				collectionHeaderDate.set ( Calendar.MILLISECOND , 0 );
				// Check if the list of dates is empty
				if ( dates.isEmpty () )
					// Add the current collection header date
					dates.add ( collectionHeaderDate );
				// Otherwise the list of dates is NOT empty
				// Check if the last date is in the same day (the cursor is ordered by date)
				else if ( dates.get ( dates.size () - 1 ).get ( Calendar.DAY_OF_YEAR ) == collectionHeaderDate.get ( Calendar.DAY_OF_YEAR )
						&& dates.get ( dates.size () - 1 ).get ( Calendar.YEAR ) == collectionHeaderDate.get ( Calendar.YEAR ) ) {
					// They are both the same day, do nothing
				} // End else if
				// Otherwise they are not the same day
				else
					// Add the current collection header date
					dates.add ( collectionHeaderDate );
			} while ( cursor.moveToNext () );
		} // End if
		// Close the cursor
		cursor.close ();
		cursor = null;
		
		// Compute the selection statement
		selection += " , CH." + CollectionHeadersDao.Properties.Id.columnName +
				" , " + CollectionHeadersDao.Properties.CollectionCode.columnName + 
				" , ( " + CollectionHeadersDao.Properties.TotalAmount.columnName + " + ( SELECT COALESCE ( MAX ( D." + CollectionHeadersDao.Properties.TotalAmount.columnName + " ) , 0 ) FROM " + CollectionHeadersDao.TABLENAME + 
						" D WHERE D." + CollectionHeadersDao.Properties.ParentCode.columnName + " = CH." + CollectionHeadersDao.Properties.CollectionCode.columnName + " ) ) " + CollectionHeadersDao.Properties.TotalAmount.columnName +
				" , " + CurrenciesDao.Properties.CurrencySymbol.columnName;
		// Compute the SQL statement
		SQL = " FROM " + CollectionHeadersDao.TABLENAME + " CH INNER JOIN " + CurrenciesDao.TABLENAME + " C " +
				"ON CH." + CollectionHeadersDao.Properties.CurrencyCode.columnName + "=C." + CurrenciesDao.Properties.CurrencyCode.columnName + " " +
				"WHERE (" + CollectionHeadersDao.Properties.ClientCode.columnName + " = ? OR " +CollectionHeadersDao.Properties.ClientCode.columnName + " = ?) AND " + CollectionHeadersDao.Properties.ParentCode.columnName + " IS NULL ";
		SQL += " AND " + CollectionHeadersDao.Properties.CollectionDate.columnName + " BETWEEN ? AND ?";
		// Adjust the selection arguments
		selectionArgs = new String [] {
				selectionArgs [ 0 ] ,selectionArgs [ 1 ],
				null ,
				null ,
		};
		
		// Declare and initialize a map of cursors mapped to strings (their date representation)
		Map < String , Cursor > cursors = new LinkedHashMap < String , Cursor > ();
		// Iterate over all dates (from the last one to the first)
		for ( int i = dates.size () - 1 ; i >= 0 ; i -- ) {
			// Compute the start date
			Calendar start = dates.get ( i );
			// Compute the end date
			Calendar end = Calendar.getInstance ();
			end.setTimeInMillis ( start.getTimeInMillis () );
			end.set ( Calendar.HOUR_OF_DAY , 23 );
			end.set ( Calendar.MINUTE , 59 );
			end.set ( Calendar.SECOND , 59 );
			end.set ( Calendar.MILLISECOND , 999 );
			// Set the start and end date in the selection arguments
			selectionArgs [ 2 ] = String.valueOf ( start.getTimeInMillis () );
			selectionArgs [ 3 ] = String.valueOf ( end.getTimeInMillis () );
			// Map a new cursor to its date
			cursors.put ( DateTime.getFullDate ( this , start ) ,
					DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
						.rawQuery ( selection + SQL + orderBy , selectionArgs ) );
		} // End for each
		
		// Iterate over all cursors
		for ( String date : cursors.keySet () )
			// Execute internal computations
			cursors.get ( date ).getCount ();
		
		// Return the cursors map
		return cursors;
	}
	

	/**
	 * Populates the summary details list by generating a map of cursors holding blank visits, that belongs for a specific day, for the specified user.
	 * 
	 * @param client	The client object for which the blank visits belong to.
	 * @return	Cursors holding blank visits data set mapped to their appropriate dates.
	 */
	private Object populateVisitsNoteList ( final Clients client ) {
		// Compute the selection statement
		String selection = "SELECT " + VisitsDao.Properties.StartDate.columnName;
		// Apply the appropriate selection to retrieve all the clients with blank visits
//					selection = ClientsDao.Properties.ClientCode.columnName + " IN ( " +
//							"SELECT " + VisitsDao.Properties.ClientCode.columnName + " FROM " + VisitsDao.TABLENAME + " " +
//							"WHERE " + VisitsDao.Properties.VisitStatus.columnName + " =2 )";
		// Compute the SQL statement
		String SQL = " FROM " + VisitsDao.TABLENAME + " V " +
				"WHERE " + VisitsDao.Properties.ClientCode.columnName + " = ? and "+
				VisitsDao.Properties.Note.columnName + " !=?";
		// Compute the order by clause
		String orderBy = " ORDER BY " + VisitsDao.Properties.StartDate.columnName;
		// Compute the selection arguments
		String selectionArgs [] = new String [] {
				client.getClientCode (),
				String.valueOf("")
		        
		};
		// Declare and initialize a cursor in order to query DB
		Cursor cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
				.rawQuery ( selection + SQL + orderBy , selectionArgs );
		
		// Declare and initialize a list of calendars, used to compute the blank visits dates
		List < Calendar > dates = new ArrayList < Calendar > ();
		// Move the cursor to the first raw
		if ( cursor.moveToFirst () ) {
			// Iterate over all raws
			do {
				// Compute the current blank visit date
				Calendar visitDate = Calendar.getInstance ();
				visitDate.setTimeInMillis ( cursor.getLong ( cursor.getColumnIndex ( VisitsDao.Properties.StartDate.columnName ) ) );
				visitDate.set ( Calendar.HOUR_OF_DAY , 0 );
				visitDate.set ( Calendar.MINUTE , 0 );
				visitDate.set ( Calendar.SECOND , 0 );
				visitDate.set ( Calendar.MILLISECOND , 0 );
				// Check if the list of dates is empty
				if ( dates.isEmpty () )
					// Add the current transaction header date
					dates.add ( visitDate );
				// Otherwise the list of dates is NOT empty
				// Check if the last date is in the same day (the cursor is ordered by date)
				else if ( dates.get ( dates.size () - 1 ).get ( Calendar.DAY_OF_YEAR ) == visitDate.get ( Calendar.DAY_OF_YEAR )
						&& dates.get ( dates.size () - 1 ).get ( Calendar.YEAR ) == visitDate.get ( Calendar.YEAR ) ) {
					// They are both the same day, do nothing
				} // End else if
				// Otherwise they are not the same day
				else
					// Add the current blank visit date
					dates.add ( visitDate );
			} while ( cursor.moveToNext () );
		} // End if
		// Close the cursor
		cursor.close ();
		cursor = null;
		
		// Compute the selection statement
		selection +=  " , V." + VisitsDao.Properties.VisitID.columnName +" as _id"+" , V." +VisitsDao.Properties.Note.columnName   	 ;
		// Compute the SQL statement
		SQL += " AND " + VisitsDao.Properties.StartDate.columnName + " BETWEEN ? AND ?";
		// Adjust the selection arguments
		selectionArgs = new String [] {
				selectionArgs [ 0 ] ,
				String.valueOf("") ,
				null ,null
		};
		
		// Declare and initialize a map of cursors mapped to strings (their date representation)
		Map < String , Cursor > cursors = new LinkedHashMap < String , Cursor > ();
		// Iterate over all dates (from the last one to the first)
		for ( int i = dates.size () - 1 ; i >= 0 ; i -- ) {
			// Compute the start date
			Calendar start = dates.get ( i );
			// Compute the end date
			Calendar end = Calendar.getInstance ();
			end.setTimeInMillis ( start.getTimeInMillis () );
			end.set ( Calendar.HOUR_OF_DAY , 23 );
			end.set ( Calendar.MINUTE , 59 );
			end.set ( Calendar.SECOND , 59 );
			end.set ( Calendar.MILLISECOND , 999 );
			// Set the start and end date in the selection arguments
			selectionArgs [ 2 ] = String.valueOf ( start.getTimeInMillis () );
			selectionArgs [ 3 ] = String.valueOf ( end.getTimeInMillis () );
			// Map a new cursor to its date
			cursors.put ( DateTime.getFullDate ( this , start ) ,
					DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
						.rawQuery ( selection + SQL + orderBy , selectionArgs ) );
		} // End for each
		
		// Iterate over all cursors
		for ( String date : cursors.keySet () )
			// Execute internal computations
			cursors.get ( date ).getCount ();
		
		// Return the cursors map
		return cursors;
	}
	/**
	 * Populates the summary details list by generating a map of cursors holding blank visits, that belongs for a specific day, for the specified user.
	 * 
	 * @param client	The client object for which the blank visits belong to.
	 * @return	Cursors holding blank visits data set mapped to their appropriate dates.
	 */
	private Object populateObjectivesList ( final Clients client ) {
		// Compute the selection statement
		String selection = "SELECT " + ObjectiveAchievementsDao.Properties.AchievementDate.columnName;
		// Apply the appropriate selection to retrieve all the clients with blank visits
//					selection = ClientsDao.Properties.ClientCode.columnName + " IN ( " +
//							"SELECT " + VisitsDao.Properties.ClientCode.columnName + " FROM " + VisitsDao.TABLENAME + " " +
//							"WHERE " + VisitsDao.Properties.VisitStatus.columnName + " =2 )";
		// Compute the SQL statement
		String SQL = " FROM " + ObjectiveAchievementsDao.TABLENAME + " V  inner join Objectives O " +
				" on o.ObjectiveID = v.ObjectiveID " +
				" WHERE " + ObjectiveAchievementsDao.Properties.ClientCode.columnName + " = ?   " 	 ;
		// Compute the order by clause
		String orderBy = " ORDER BY " + ObjectiveAchievementsDao.Properties.AchievementDate.columnName;
		// Compute the selection arguments
		String selectionArgs [] = new String [] {
				client.getClientCode () 
		        
		};
		// Declare and initialize a cursor in order to query DB
		Cursor cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
				.rawQuery ( selection + SQL + orderBy , selectionArgs );
		
		// Declare and initialize a list of calendars, used to compute the blank visits dates
		List < Calendar > dates = new ArrayList < Calendar > ();
		// Move the cursor to the first raw
		if ( cursor.moveToFirst () ) {
			// Iterate over all raws
			do {
				// Compute the current blank visit date
				Calendar visitDate = Calendar.getInstance ();
				visitDate.setTimeInMillis ( cursor.getLong ( cursor.getColumnIndex ( ObjectiveAchievementsDao.Properties.AchievementDate.columnName ) ) );
				visitDate.set ( Calendar.HOUR_OF_DAY , 0 );
				visitDate.set ( Calendar.MINUTE , 0 );
				visitDate.set ( Calendar.SECOND , 0 );
				visitDate.set ( Calendar.MILLISECOND , 0 );
				// Check if the list of dates is empty
				if ( dates.isEmpty () )
					// Add the current transaction header date
					dates.add ( visitDate );
				// Otherwise the list of dates is NOT empty
				// Check if the last date is in the same day (the cursor is ordered by date)
				else if ( dates.get ( dates.size () - 1 ).get ( Calendar.DAY_OF_YEAR ) == visitDate.get ( Calendar.DAY_OF_YEAR )
						&& dates.get ( dates.size () - 1 ).get ( Calendar.YEAR ) == visitDate.get ( Calendar.YEAR ) ) {
					// They are both the same day, do nothing
				} // End else if
				// Otherwise they are not the same day
				else
					// Add the current blank visit date
					dates.add ( visitDate );
			} while ( cursor.moveToNext () );
		} // End if
		// Close the cursor
		cursor.close ();
		cursor = null;
		
		// Compute the selection statement
		selection +=  " , V." + ObjectiveAchievementsDao.Properties.AchievementDate.columnName +" as _id"+" ,o." +ObjectivesDao.Properties.ObjectiveDescription.columnName +", v."+ ObjectiveAchievementsDao.Properties.ObjectiveAchievement.columnName 	 ;
		// Compute the SQL statement
		SQL += " AND " + ObjectiveAchievementsDao.Properties.AchievementDate.columnName + " BETWEEN ? AND ?";
		// Adjust the selection arguments
		selectionArgs = new String [] {
				selectionArgs [ 0 ]  ,
			 
				null ,null
		};
		
		// Declare and initialize a map of cursors mapped to strings (their date representation)
		Map < String , Cursor > cursors = new LinkedHashMap < String , Cursor > ();
		// Iterate over all dates (from the last one to the first)
		for ( int i = dates.size () - 1 ; i >= 0 ; i -- ) {
			// Compute the start date
			Calendar start = dates.get ( i );
			// Compute the end date
			Calendar end = Calendar.getInstance ();
			end.setTimeInMillis ( start.getTimeInMillis () );
			end.set ( Calendar.HOUR_OF_DAY , 23 );
			end.set ( Calendar.MINUTE , 59 );
			end.set ( Calendar.SECOND , 59 );
			end.set ( Calendar.MILLISECOND , 999 );
			// Set the start and end date in the selection arguments
			selectionArgs [ 1 ] = String.valueOf ( start.getTimeInMillis () );
			selectionArgs [ 2 ] = String.valueOf ( end.getTimeInMillis () );
			// Map a new cursor to its date
			cursors.put ( DateTime.getFullDate ( this , start ) ,
					DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
						.rawQuery ( selection + SQL + orderBy , selectionArgs ) );
		} // End for each
		
		// Iterate over all cursors
		for ( String date : cursors.keySet () )
			// Execute internal computations
			cursors.get ( date ).getCount ();
		
		// Return the cursors map
		return cursors;
	}
	
	/**
	 * AsyncTask helper class used to populate the sales order items list.
	 * 
	 * @author Elias
	 *
	 */
	private class PopulateList extends AsyncTask < Void , Void , Object > {
		
		/**
		 * Integer holding the selected summary ID.
		 */
		private int summaryID;
		
		/*
		 * Runs on the UI thread before doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute () {
			// Display indeterminate progress dialog
			AppDialog.getInstance ().displayIndeterminateProgress ( SummaryDetailsActivity.this , null , null );
		}
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Object doInBackground ( Void ... params ) {
			// Check if the summary id is valid
			if ( SummaryDetailsActivity.this.summaryID == null )
				// Invalid summary id, do nothing
				return null;
			try {
				// Retrieve the client object
				Clients client = (Clients) getIntent ().getSerializableExtra ( CLIENT );
				// Query the appropriate client division
				ClientDivisions clientDivision = DatabaseUtils.getInstance ( SummaryDetailsActivity.this ).getDaoSession ().getClientDivisionsDao ().queryBuilder ()
						.where ( ClientDivisionsDao.Properties.ClientCode.eq ( client.getClientCode () ) ,
								ClientDivisionsDao.Properties.CompanyCode.eq ( client.getCompanyCode () ) , 
								ClientDivisionsDao.Properties.DivisionCode.eq ( client.getDivisionCode () ) ).uniqueOrThrow ();
				// Create a call object for the Sales Order module
				Call call = new Call ( "" , client , clientDivision );
				// Store the summary id
				summaryID = SummaryDetailsActivity.this.summaryID;
				// Determine the summary to display
				switch ( summaryID ) {
				case Summary.ID.SALES_ORDERS:
					// Populate the sales orders list
					return SalesOrderActivity.populateList ( SummaryDetailsActivity.this , call );
				case Summary.ID.SALES_INVOICE:
					// Populate the sales invoices list
					return SalesInvoiceActivity.populateList ( SummaryDetailsActivity.this , call );
				case Summary.ID.SALES_RFR:
					// Populate the sales orders list
					return SalesRFRActivity.populateList ( SummaryDetailsActivity.this , call );
				case Summary.ID.DIRECT_RETURN:
					return DirectReturnActivity.populateList ( SummaryDetailsActivity.this , call );
				case Summary.ID.SALES_RETURN:
					// Populate the sales returns list
					return ReturnActivity.populateList ( SummaryDetailsActivity.this , call , true );
				case Summary.ID.COLLECTION:
					// Populate the collection headers list
					return populateCollectionHeadersList ( (Clients) getIntent ().getSerializableExtra ( CLIENT ) );
				case Summary.ID.CLIENT_STOCK_COUNT:
					// Populate the client stock count list
					return ClientStockActivity.populateList ( SummaryDetailsActivity.this , call , ClientStockDetailsActivity.REGULAR_ITEMS_LIST );
				case Summary.ID.VISIT_NOTE:
					// Populate the collection headers list
				return populateVisitsNoteList ( (Clients) getIntent ().getSerializableExtra ( CLIENT ) );
				case Summary.ID.OBJECTIVE:
					// Populate the collection headers list
				return populateObjectivesList( (Clients) getIntent ().getSerializableExtra ( CLIENT ) );
				
				case Summary.ID.CLIENT_ASSET:
					// Populate the client asset list
					return ClientStockActivity.populateList ( SummaryDetailsActivity.this , call , ClientStockDetailsActivity.ASSETS_LIST );
				default:
					// Invalid summary ID
					break;
				} // End switch
			} catch ( Exception exception ) {
				Log.d("error"," "+exception.getMessage().toString() );
				// Do nothing
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
		protected void onPostExecute ( Object result ) {
			// Dismiss dialog
			AppDialog.getInstance ().dismiss ();
			// Check if the result is valid
			if ( result == null ) {
				// Clear list view
				setListAdapter ( null );
				// Indicate that the activity cannot be displayed
				new AppToast ( SummaryDetailsActivity.this ).setIcon ( R.drawable.warning ).setText ( AppResources.getInstance ( SummaryDetailsActivity.this ).getString ( SummaryDetailsActivity.this , R.string.invalid_selection_label ) ).show ();
				// Invalid result, do nothing
				return;
			} // End if
			
			// Otherwise the result is valid
			Map < String , Cursor > cursors = null;
			MultipleListAdapter adapter = null;
			// Determine the summary to display
			switch ( summaryID ) {
			case Summary.ID.SALES_ORDERS:
				// Cast the result
				cursors = (Map < String , Cursor >) result;
				// Declare and initialize a multiple list adapter
				adapter = new MultipleListAdapter ( SummaryDetailsActivity.this );
				// Iterate over all cursors
				for ( String date : cursors.keySet () )
					// Add a new sales order adapter using the current cursor
					adapter.addSection ( new Section ( date , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
							new SalesOrderAdapter ( SummaryDetailsActivity.this , cursors.get ( date ) , R.layout.sales_order_activity_item ) );
				// Set the list adapter
				setListAdapter ( adapter );
				break;
			case Summary.ID.SALES_INVOICE:
				// Cast the result
				cursors = (Map < String , Cursor >) result;
				// Declare and initialize a multiple list adapter
				adapter = new MultipleListAdapter ( SummaryDetailsActivity.this );
				// Iterate over all cursors
				for ( String date : cursors.keySet () )
					// Add a new sales order adapter using the current cursor
					adapter.addSection ( new Section ( date , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
							new SalesInvoiceAdapter ( SummaryDetailsActivity.this , cursors.get ( date ) , R.layout.sales_order_activity_item ) );
				// Set the list adapter
				setListAdapter ( adapter );
				break;
			case Summary.ID.SALES_RFR:
				// Cast the result
				cursors = (Map < String , Cursor >) result;
				// Declare and initialize a multiple list adapter
				adapter = new MultipleListAdapter ( SummaryDetailsActivity.this );
				// Iterate over all cursors
				for ( String date : cursors.keySet () )
					// Add a new sales order adapter using the current cursor
					adapter.addSection ( new Section ( date , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
							new SalesRFRAdapter ( SummaryDetailsActivity.this , cursors.get ( date ) , R.layout.sales_order_activity_item ) );
				// Set the list adapter
				setListAdapter ( adapter );
				break;
			case Summary.ID.DIRECT_RETURN:
				// Cast the result
				cursors = (Map < String , Cursor >) result;
				// Declare and initialize a multiple list adapter
				adapter = new MultipleListAdapter ( SummaryDetailsActivity.this );
				// Iterate over all cursors
				for ( String date : cursors.keySet () )
					// Add a new sales order adapter using the current cursor
					adapter.addSection ( new Section ( date , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
							new DirectReturnAdapter ( SummaryDetailsActivity.this , cursors.get ( date ) , R.layout.sales_order_activity_item ) );
				// Set the list adapter
				setListAdapter ( adapter );
				break;
			case Summary.ID.VISIT_NOTE:
				// Cast the result
				cursors = (Map < String , Cursor >) result;
				// Declare and initialize a multiple list adapter
				adapter = new MultipleListAdapter ( SummaryDetailsActivity.this );
				// Iterate over all cursors
				for ( String date : cursors.keySet () )
					// Add a new collection header adapter using the current cursor
					adapter.addSection ( new Section ( date , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
							new VisitNoteAdapter ( SummaryDetailsActivity.this , cursors.get ( date ) , R.layout.summary_details_activity_item_blank_visit ) );
				// Set the list adapter
				setListAdapter ( adapter );
				break;
			case Summary.ID.OBJECTIVE:
				// Cast the result
				cursors = (Map < String , Cursor >) result;
				// Declare and initialize a multiple list adapter
				adapter = new MultipleListAdapter ( SummaryDetailsActivity.this );
				// Iterate over all cursors
				for ( String date : cursors.keySet () )
					// Add a new collection header adapter using the current cursor
					adapter.addSection ( new Section ( date , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
							new ObjectiveSummaryAdapter ( SummaryDetailsActivity.this , cursors.get ( date ) , R.layout.summary_details_activity_item_blank_visit ) );
				// Set the list adapter
				setListAdapter ( adapter );
				break;
			case Summary.ID.SALES_RETURN:
				// Cast the result
				cursors = (Map < String , Cursor >) result;
				// Declare and initialize a multiple list adapter
				adapter = new MultipleListAdapter ( SummaryDetailsActivity.this );
				// Iterate over all cursors
				for ( String date : cursors.keySet () )
					// Add a new sales order adapter using the current cursor
					adapter.addSection ( new Section ( date , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
							new ReturnAdapter ( SummaryDetailsActivity.this , cursors.get ( date ) , R.layout.return_activity_item ) );
				// Set the list adapter
				setListAdapter ( adapter );
				break;
			case Summary.ID.COLLECTION:
				// Cast the result
				cursors = (Map < String , Cursor >) result;
				// Declare and initialize a multiple list adapter
				adapter = new MultipleListAdapter ( SummaryDetailsActivity.this );
				// Iterate over all cursors
				for ( String date : cursors.keySet () )
					// Add a new collection header adapter using the current cursor
					adapter.addSection ( new Section ( date , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
							new CollectionViewAdapter ( SummaryDetailsActivity.this , cursors.get ( date ) , R.layout.collection_headers_view_item ) );
				// Set the list adapter
				setListAdapter ( adapter );
				break;
			case Summary.ID.CLIENT_STOCK_COUNT:
			case Summary.ID.CLIENT_ASSET:
				// Cast the result
				cursors = (Map < String , Cursor >) result;
				// Declare and initialize a multiple list adapter
				adapter = new MultipleListAdapter ( SummaryDetailsActivity.this );
				// Iterate over all cursors
				for ( String date : cursors.keySet () )
					// Add a new client stock count adapter using the current cursor
					adapter.addSection ( new Section ( date , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
							new ClientStockAdapter ( SummaryDetailsActivity.this , cursors.get ( date ) , R.layout.sales_order_activity_item ) );
				// Set the list adapter
				setListAdapter ( adapter );
				break;
			default:
				// Invalid summary ID
				// Clear list view
				setListAdapter ( null );
				break;
			} // End switch
		}
		
	}
	
}