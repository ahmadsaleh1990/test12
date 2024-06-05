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

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import me.SyncWise.Android.ActivityTransition;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.ListViewPosition;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.BlankVisitsDao;
import me.SyncWise.Android.Database.ClientStockCountHeadersDao;
import me.SyncWise.Android.Database.Clients;
import me.SyncWise.Android.Database.ClientsDao;
import me.SyncWise.Android.Database.CollectionHeadersDao;
import me.SyncWise.Android.Database.CompaniesDao;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.DivisionsDao;
import me.SyncWise.Android.Database.ObjectiveAchievementsDao;
import me.SyncWise.Android.Database.ObjectivesDao;
import me.SyncWise.Android.Database.StatusUtils;
import me.SyncWise.Android.Database.TransactionHeadersDao;
import me.SyncWise.Android.Database.TransactionHeadersUtils;
import me.SyncWise.Android.Database.Users;
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Database.VisitsDao;
import me.SyncWise.Android.Modules.ClientStock.ClientStockDetailsActivity;

/*
 * List Activity of all the clients with search capabilities.
 * 
 * @see me.SyncWise.Android.Modules.ClientsList.ClientsListActivity
 */
public class SummaryActivity extends me.SyncWise.Android.Modules.ClientsList.ClientsListActivity implements OnItemSelectedListener {
	
	/**
	 * Integer holding the selected summary ID.
	 */
	protected Integer summaryID;
	
	/**
	 * Reference to the spinner holding the summary drop down list.
	 */
	private Spinner summarySpinner;
	
	private boolean isSO = false;
	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		// Set the activity content from a layout resource.
		setLayoutResourceID ( R.layout.summary_activity );
		// Superclass method call
		super.onCreate ( savedInstanceState );
		
		// Retrieve a reference to the spinner
		summarySpinner = (Spinner) findViewById ( R.id.spinner );
		// Display the summary selection label
		( (TextView) findViewById ( R.id.summary_selection_label ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.summary_selection_label ) );
		
		String userCode = DatabaseUtils.getCurrentUserCode ( this );
		// Retrieve the company code
		String companyCode = DatabaseUtils.getCurrentCompanyCode ( this );
		// Retrieve the user object
		Users user = DatabaseUtils.getInstance ( this ).getDaoSession ().getUsersDao ().queryBuilder ()
		.where ( UsersDao.Properties.UserCode.eq (userCode ) ,
				UsersDao.Properties.CompanyCode.eq ( companyCode ) ).unique ();
		if(user.getUserType()==10)
			isSO=true;
		
		// Retrieve the summaries list
		ArrayList < Summary > summaries = populateSummaries ();
    	// Declare and initialize a new summary adapter used to populate the summaries drop down list
		SummaryAdapter summaryAdapter = new SummaryAdapter ( this , android.R.layout.simple_spinner_item , summaries );
		// Sets the layout resource to create the drop down views
		summaryAdapter.setDropDownViewResource ( R.layout.data_management_activity_connection_item );
		// Set the spinner listener
		summarySpinner.setOnItemSelectedListener ( this );
    	// Set the spinner adapter
		summarySpinner.setAdapter ( summaryAdapter );
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.summary_activity_title ) );
		
	
	
		
	}
	
	/*
	 * Initializes the search list activity components.
	 *
	 * @see me.SyncWise.Android.Modules.SearchList.SearchListActivity#initializeComponents(android.os.Bundle)
	 */
	@Override
	protected void initializeComponents ( Bundle savedInstanceState ) {
		// Check if the main table is valid
		if ( getTable () == null )
			// Invalid table
			return;
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state
			setSearchQuery ( savedInstanceState.getString ( SEARCH_QUERY ) );
			setListViewPosition ( (ListViewPosition) savedInstanceState.getSerializable ( LIST_VIEW_POSITION ) );
			setSelection ( savedInstanceState.getString ( SELECTION ) );
			setSelectionArguments ( savedInstanceState.getStringArrayList ( SELECTION_ARGUMENTS ) );
		} // End if
	}
	
	/*
	 * Computes an appropriate cursor based on the provided table, selection and filter.
	 *
	 * @see me.SyncWise.Android.Modules.SearchList.SearchCursorListActivity#computeCursor()
	 */
	@Override
	protected Cursor computeCursor () {
		// Declare and initialize a string used to host the selection string
		String selection = null;
		// Determine the selected summary ID
		switch ( summaryID ) {
		case Summary.ID.SALES_ORDERS:
			// Apply the appropriate selection to retrieve all the clients with sales orders
			selection = "( C.ClientCode || '--' || C.CompanyCode || '--' || CD.DivisionCode ) IN ( " + 
					"SELECT DISTINCT TH.ClientCode || '--' || TH.CompanyCode || '--' || TH.DivisionCode FROM " + TransactionHeadersDao.TABLENAME + " TH " +
					"WHERE " + TransactionHeadersDao.Properties.TransactionStatus.columnName + " = " + StatusUtils.isAvailable () + " " +
					"AND " + TransactionHeadersDao.Properties.TransactionType.columnName + " = " + TransactionHeadersUtils.Type.SALES_ORDER + " )";
			break;
		case Summary.ID.SALES_INVOICE:
			// Apply the appropriate selection to retrieve all the clients with sales invoices
			selection = "( C.ClientCode || '--' || C.CompanyCode || '--' || CD.DivisionCode ) IN ( " + 
					"SELECT DISTINCT TH.ClientCode || '--' || TH.CompanyCode || '--' || TH.DivisionCode FROM " + TransactionHeadersDao.TABLENAME + " TH " +
					"WHERE " + TransactionHeadersDao.Properties.TransactionStatus.columnName + " = " + StatusUtils.isAvailable () + " " +
					"AND " + TransactionHeadersDao.Properties.TransactionType.columnName + " = " + TransactionHeadersUtils.Type.SALES_INVOICE + " )";
			break;
		case Summary.ID.SALES_RFR:
			// Apply the appropriate selection to retrieve all the clients with RFR
			selection = "( C.ClientCode || '--' || C.CompanyCode || '--' || CD.DivisionCode ) IN ( " + 
					"SELECT DISTINCT TH.ClientCode || '--' || TH.CompanyCode || '--' || TH.DivisionCode FROM " + TransactionHeadersDao.TABLENAME + " TH " +
					"WHERE " + TransactionHeadersDao.Properties.TransactionStatus.columnName + " = " + StatusUtils.isAvailable () + " " +
					"AND " + TransactionHeadersDao.Properties.TransactionType.columnName + " = " + TransactionHeadersUtils.Type.SALES_RFR + " )";
			break;
		case Summary.ID.SALES_RETURN:
		case Summary.ID.DIRECT_RETURN:	
			// Apply the appropriate selection to retrieve all the clients with returns
			selection = "( C.ClientCode || '--' || C.CompanyCode || '--' || CD.DivisionCode ) IN ( " + 
					"SELECT DISTINCT TH.ClientCode || '--' || TH.CompanyCode || '--' || TH.DivisionCode FROM " + TransactionHeadersDao.TABLENAME + " TH " +
					"WHERE " + TransactionHeadersDao.Properties.TransactionStatus.columnName + " = " + StatusUtils.isAvailable () + " " +
					"AND " + TransactionHeadersDao.Properties.TransactionType.columnName + " = " + TransactionHeadersUtils.Type.SALES_RETURN + " )";
			break;
		case Summary.ID.COLLECTION:
			// Apply the appropriate selection to retrieve all the clients with collections
			selection = "C." + ClientsDao.Properties.ParentCode.columnName + " IN ( " +
					"SELECT " + CollectionHeadersDao.Properties.ClientCode.columnName + " FROM " + CollectionHeadersDao.TABLENAME + " " +
					"WHERE " + CollectionHeadersDao.Properties.CollectionStatus.columnName + " = " + StatusUtils.isAvailable () + " ) or C." + ClientsDao.Properties.ClientCode.columnName + " IN ( " +
					"SELECT " + CollectionHeadersDao.Properties.ClientCode.columnName + " FROM " + CollectionHeadersDao.TABLENAME + " " +
					"WHERE " + CollectionHeadersDao.Properties.CollectionStatus.columnName + " = " + StatusUtils.isAvailable () + " )";
			break;
		case Summary.ID.CLIENT_STOCK_COUNT:
			// Apply the appropriate selection to retrieve all the clients with client stock count
			selection = "( C.ClientCode || '--' || C.CompanyCode || '--' || CD.DivisionCode ) IN ( " + 
					"SELECT DISTINCT CSCH.ClientCode || '--' || CSCH.CompanyCode || '--' || CSCH.DivisionCode FROM " + ClientStockCountHeadersDao.TABLENAME + " CSCH " +
					"WHERE " + ClientStockCountHeadersDao.Properties.ItemType.columnName + " = " + ClientStockDetailsActivity.REGULAR_ITEMS_LIST + " )";
			break;
			
		case Summary.ID.VISIT_NOTE:
			// Apply the appropriate selection to retrieve all the clients with visits Note
			selection ="C."+ ClientsDao.Properties.ClientCode.columnName + " IN ( " +
			"SELECT " + VisitsDao.Properties.ClientCode.columnName + " FROM " + VisitsDao.TABLENAME + " " +
			"WHERE " + VisitsDao.Properties.Note.columnName + " !=''   )";//or " + VisitsDao.Properties.Note.columnName +" is not null
			break;
		case Summary.ID.CLIENT_ASSET:
			// Apply the appropriate selection to retrieve all the clients with client asset
			selection = "( C.ClientCode || '--' || C.CompanyCode || '--' || CD.DivisionCode ) IN ( " + 
					"SELECT DISTINCT CSCH.ClientCode || '--' || CSCH.CompanyCode || '--' || CSCH.DivisionCode FROM " + ClientStockCountHeadersDao.TABLENAME + " CSCH " +
					"WHERE " + ClientStockCountHeadersDao.Properties.ItemType.columnName + " = " + ClientStockDetailsActivity.ASSETS_LIST + " )";
			break;
			
		case Summary.ID.OBJECTIVE:
			// Apply the appropriate selection to retrieve all the clients with client asset
			selection = "( C.ClientCode ) IN ( SELECT DISTINCT OAC.ClientCode FROM " 
			+ ObjectivesDao.TABLENAME + " O inner join  " +
					ObjectiveAchievementsDao.TABLENAME +" OAC on OAC. "+ ObjectiveAchievementsDao.Properties.ObjectiveID.columnName+"= o."
					+ObjectivesDao.Properties.ObjectiveID.columnName+" and "+ObjectivesDao.Properties.ObjectiveType.columnName+"=2"
					 + " )";
			break;
		default:
			// Otherwise, the query must return NO clients
			selection = "C." + ClientsDao.Properties.ClientCode.columnName + " IS NULL";
			break;
		} // End switch
		
		// Compute the selection filter statement
		computeClientFilterSelection ();
		
		// Define the selection as such
		setSelection ( getSelection () == null ? selection : getSelection () + " AND " + selection );
		
		// Superclass method call
		return super.computeCursor ();
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
	
	/**
	 * Populates the summary list.<br>
	 * Override this method to provide the summaries list.<br>
	 * <b>Note : </b> This method should never return {@code NULL}.
	 * 
	 * @return	List of {@link me.SyncWise.Android.Modules.Summary.Summary Summary} objects.
	 */
	protected ArrayList < Summary > populateSummaries () {
		// Populate the list of summaries that the user can select for the current client
		ArrayList < Summary > summaries = new ArrayList < Summary > ();
		summaries.add ( new Summary ( Summary.ID.SALES_ORDERS , AppResources.getInstance ( this ).getString ( this , R.string.sales_order_activity_title ) ) );
		summaries.add ( new Summary ( Summary.ID.SALES_INVOICE , AppResources.getInstance ( this ).getString ( this , R.string.sales_invoice_activity_title ) ) );
		summaries.add ( new Summary ( Summary.ID.SALES_RFR , AppResources.getInstance ( this ).getString ( this , R.string.sales_return_activity_title) ) );
		summaries.add ( new Summary ( Summary.ID.SALES_RETURN , AppResources.getInstance ( this ).getString ( this , R.string.return_activity_title ) ) );
		summaries.add ( new Summary ( Summary.ID.DIRECT_RETURN , AppResources.getInstance ( this ).getString ( this , R.string.sales_return_activity_titles ) ) );
		
		summaries.add ( new Summary ( Summary.ID.COLLECTION , AppResources.getInstance ( this ).getString ( this , R.string.collection_details_activity_title ) ) );
		summaries.add ( new Summary ( Summary.ID.CLIENT_STOCK_COUNT , AppResources.getInstance ( this ).getString ( this , R.string.client_stock_activity_title ) ) );
		summaries.add ( new Summary ( Summary.ID.CLIENT_ASSET , AppResources.getInstance ( this ).getString ( this , R.string.client_asset_activity_title ) ) );
		if(isSO)
		{
			summaries.add ( new Summary ( Summary.ID.VISIT_NOTE , AppResources.getInstance ( this ).getString ( this , R.string.visit_note_activity_title ) ) );
			summaries.add ( new Summary ( Summary.ID.OBJECTIVE , AppResources.getInstance ( this ).getString ( this , R.string.client_objective_activity_title ) ) );
			
		}
		return summaries;
	}
	
	/*
	 * This method will be called when an item in the list is selected.
	 *
	 * @see android.support.v4.app.ListFragment#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	public void onListItemClick ( ListView listView , View view , int position , long id ) {
		// Retrieve a reference to the cursor
		Cursor cursor = (Cursor) getListAdapter ().getItem ( position );
		// Retrieve the selected client object from the cursor
		Clients client = DatabaseUtils.getInstance ( this ).getDaoSession ().getClientsDao ().readEntity ( cursor , 0 );
		// Set the company name
		client.setCompanyName ( cursor.getString ( cursor.getColumnIndex ( CompaniesDao.Properties.CompanyName.columnName ) ) );
		// Set the division code
		client.setDivisionCode ( cursor.getString ( cursor.getColumnIndex ( DivisionsDao.Properties.DivisionCode.columnName ) ) );
		// Set the division name
		client.setCompanyName ( cursor.getString ( cursor.getColumnIndex ( DivisionsDao.Properties.DivisionName.columnName ) ) );
		// Start the new activity
		startActivity ( getSummaryDetailsActivityIntent ( client ) );
		// Specify an explicit transition animation to perform next
		ActivityTransition.SlideOutLeft ( this );
	}
	
	/**
	 * Gets an intent for the summary details activity.<br>
     * The default implementation uses {@link me.SyncWise.Android.Modules.Summary.SummaryDetailsActivity SummaryDetailsActivity}.<br>
     * In order to used a custom class, override this method.<br>
	 * <b>Note : </b> This method should not return {@code NULL}.
	 * 
	 * @param client	The client object whose summary details are to be displayed.
	 * @return	Intent used to start the summary details activity.
	 */
	protected Intent getSummaryDetailsActivityIntent ( final Clients client ) {
		// Create an intent to start a new activity
		Intent intent = new Intent ( this , SummaryDetailsActivity.class );
		// Add the summary list to the intent
		intent.putExtra ( SummaryDetailsActivity.SUMMARY_LIST , populateSummaries () );
		// Add the client object to the intent
		intent.putExtra ( SummaryDetailsActivity.CLIENT , client );
		// Add the summary ID to the intent
		intent.putExtra ( SummaryDetailsActivity.SUMMARY_ID , summaryID );
		// Return the intent
		return intent;
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
		repopulateList ();
	}

	/*
	 * Callback method to be invoked when the selection disappears from this view.
	 *
	 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android.widget.AdapterView)
	 */
	public void onNothingSelected ( AdapterView < ? > parent ) {
		// Do nothing
	}
	
}