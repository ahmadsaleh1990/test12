/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.SalesSummary;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppDialog.ButtonsType;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.DateTime;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.Currencies;
import me.SyncWise.Android.Database.CurrenciesDao;
import me.SyncWise.Android.Database.DaoSession;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.TransactionHeadersDao;
import me.SyncWise.Android.Gson.BaseTimerActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Activity implemented as a daily collection report.
 * 
 * @author Elias
 * @sw.todo <b>Daily Collection Report Activity Implementation :</b><br>
 * Simply add this class in the {@code AndroidManifest.xml}.
 *
 */
public class Summary2Activity extends BaseTimerActivity {
	
	/**
	 * Reference to the spinner holding the summary drop down list.
	 */
	private Spinner summarySpinner;
	/**
	 * String used to host he selected reason code.
	 */
	private static int selectedSummary;
	/**
	 * Bundle key used to put/retrieve the summary ID.
	 */
	public static final String SUMMARY_ID = Summary2Activity.class.getName () + ".SUMMARY_ID";
	

	/**
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@SuppressLint("DefaultLocale") @Override
	public void onCreate ( Bundle savedInstanceState ) {
		// Superclass method call
		super.onCreate ( savedInstanceState );
		
		selectedSummary=0;
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.summarydetails_view_activity_title ) );
		// Set the activity content from a layout resource.
		setContentView ( R.layout.daily_summary_report_activity );
		
		// Retrieve a reference to the spinner
		summarySpinner = (Spinner) findViewById ( R.id.spinner );
		// Display the summary selection label
		( (TextView) findViewById ( R.id.summary_selection_label ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.summary_selection_label ) );
		// Retrieve the summaries list
		ArrayList < Summary2 > summaries = populateSummaries ();
		// Declare and initialize a new summary adapter used to populate the summaries drop down list
		Summary2Adapter summaryAdapter = new Summary2Adapter ( this , android.R.layout.simple_spinner_item , summaries );
		
		// Sets the layout resource to create the drop down views
		summaryAdapter.setDropDownViewResource ( R.layout.data_management_activity_connection_item );
		// Set the spinner listener
		summarySpinner.setOnItemSelectedListener ( new OnItemSelectedListener () {
			@Override
			public void onItemSelected ( AdapterView < ? > parent , View view , int position , long id ) {
					// Check if a reason is selected	
					// Retrieve the reason name
					selectedSummary = ( (Summary2) summarySpinner.getSelectedItem () ).getId();
					if ( selectedSummary == Summary2.ID.SALES_ORDERS ) {
						findViewById ( R.id.label_cash ).setVisibility ( View.VISIBLE );
						findViewById ( R.id.layout_cash ).setVisibility ( View.VISIBLE );
						findViewById ( R.id.label_check ).setVisibility ( View.GONE );
						findViewById ( R.id.layout_check ).setVisibility ( View.GONE );
						
						return; //keep free items visibility to not_visible
					} // End if
					else if(selectedSummary == Summary2.ID.SALES_INVOICE){
						findViewById ( R.id.label_cash ).setVisibility ( View.GONE );
						findViewById ( R.id.layout_cash ).setVisibility ( View.GONE );
						findViewById ( R.id.label_check ).setVisibility ( View.VISIBLE );
						findViewById ( R.id.layout_check ).setVisibility ( View.VISIBLE );

						return;
					}// End if
					
				}
				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub	
				}
			});
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state
			selectedSummary = (Integer) savedInstanceState.getInt ( SUMMARY_ID );
				} // End if
		// Set the spinner adapter
		summarySpinner.setAdapter ( summaryAdapter );
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.summary_activity_title ) );
		
		
		// Retrieve a reference to the cash layout
		View cashLayout = findViewById ( R.id.layout_cash );
		// Retrieve a reference to the cash list
		ListView cashList = (ListView) cashLayout.findViewById ( R.id.list_cash );
		// Retrieve a reference to the cash empty view
		TextView cashEmptyView = (TextView) cashLayout.findViewById ( R.id.empty_list_view );
		// Enable fast scrolling
		cashList.setFastScrollEnabled ( true );
		// Define the empty list view
		cashList.setEmptyView ( cashEmptyView );
		// Display empty list label
		cashEmptyView.setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_sale_order_list_label ) );
		// Display the total cash label
		( (TextView) findViewById ( R.id.label_cash ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.total_sale_order_label ).toUpperCase () );
		
		// Retrieve a reference to the check layout
		View checkLayout = findViewById ( R.id.layout_check );
		// Retrieve a reference to the check list
		ListView checkList = (ListView) checkLayout.findViewById ( R.id.list_check );
		// Retrieve a reference to the check empty view
		TextView checkEmptyView = (TextView) checkLayout.findViewById ( R.id.empty_list_view );
		// Enable fast scrolling
		checkList.setFastScrollEnabled ( true );
		// Define the empty list view
		checkList.setEmptyView ( checkEmptyView );
		// Display empty list label
		checkEmptyView.setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_sale_invoice_list_label ) );
		// Display the total check label
		( (TextView) findViewById ( R.id.label_check ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.total_sale_invoice_label ).toUpperCase () );
		
		// Display the current date as sub title
		getActionBar ().setSubtitle ( DateTime.getBriefDate ( this , Calendar.getInstance () ) );
		
		// Retrieve all the collections asynchronously
		new PopulateList ().execute ();
	}
	
	 /**
     * Called to retrieve per-instance state from an activity before being killed so that the state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle).
     *
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState ( Bundle outState ) {
    	// Superclass method call
    	super.onSaveInstanceState ( outState );
    	// Save the content of summaryID in the outState bundle
    	outState.putInt ( SUMMARY_ID , selectedSummary );
    }
	/**
	 * Populates the summary list.<br>
	 * Override this method to provide the summaries list.<br>
	 * <b>Note : </b> This method should never return {@code NULL}.
	 * 
	 * @return	List of {@link me.SyncWise.Android.Modules.Summary.Summary Summary} objects.
	 */
	protected ArrayList < Summary2 > populateSummaries () {
		// Populate the list of summaries that the user can select for the current client
		ArrayList < Summary2 > summaries = new ArrayList < Summary2 > ();
		summaries.add ( new Summary2 ( Summary2.ID.SALES_ORDERS , AppResources.getInstance ( Summary2Activity.this ).getString ( Summary2Activity.this , R.string.sales_order_activity_title ) ) );
		summaries.add ( new Summary2 ( Summary2.ID.SALES_INVOICE , AppResources.getInstance ( Summary2Activity.this ).getString ( Summary2Activity.this , R.string.sales_invoice_activity_title ) ) );
		return summaries;
	}

	/**
	 * AsyncTask helper class used to populate the collections details list.
	 * 
	 * @author Elias
	 *
	 */
	private class PopulateList extends AsyncTask < Void , Void , Boolean > {
		
		/**
		 * String used to host the total amount of cash payments.
		 */
		private String totalCashStr;
		
		/**
		 * String used to host the total amount of cash payments.
		 */
		private double totalCash;
		
		/**
		 * Cursor used to host the result set of daily cash payments.
		 */
		private Cursor cashCursor;
		

		/**
		 * Double used to host the total amount of check payments.
		 */
		private double totalCheck;
		
		/**
		 * Double used to host the total amount of check payments.
		 */
		private String totalCheckStr;

		/**
		 * Cursor used to host the result set of daily check payments.
		 */
		private Cursor checkCursor;
		
		/**
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Boolean doInBackground ( Void ... params ) {
			try {
				// Retrieve the current date
				Calendar calendar = Calendar.getInstance ();
				// Retrieve the start of the day
				calendar.set ( Calendar.HOUR_OF_DAY , 0 );
				calendar.set ( Calendar.MINUTE , 0 );
				calendar.set ( Calendar.SECOND , 0 );
				calendar.set ( Calendar.MILLISECOND , 0 );
				long startDay = calendar.getTimeInMillis ();
				// Retrieve the end of the day
				calendar.set ( Calendar.HOUR_OF_DAY , 23 );
				calendar.set ( Calendar.MINUTE , 59 );
				calendar.set ( Calendar.SECOND , 59 );
				calendar.set ( Calendar.MILLISECOND , 999 );
				long endDay = calendar.getTimeInMillis ();
				// Retrieve a reference to the dao session
				DaoSession daoSession = DatabaseUtils.getInstance ( Summary2Activity.this ).getDaoSession ();
				
				// Retrieve all the required currencies
				List < Currencies > _currencies = daoSession.getCurrenciesDao ().queryBuilder ()
						.orderAsc ( CurrenciesDao.Properties.CurrencyPriority ).list ();
				// Retrieve the main currency
				Currencies mainCurrency = _currencies.get ( 0 );
				// Map the currencies to their currency codes
				HashMap < String , Currencies > currencies = new HashMap < String , Currencies > ();
				// Iterate over currencies
				for ( Currencies currency : _currencies )
					// Map the current currency to its code
					currencies.put ( currency.getCurrencyCode () , currency );
				
				
				String SQL=null;
			 	String selectionArguments [] = null;
				// Compute the cursor with the corresponding sales orders details data
			 	SQL =  " select  TransactionHeaders.ClientCode as _id, TransactionType,  TransactionHeaders.ClientCode, Clients.ClientName, TotalTaxAmount , CurrencyCode" +
				 		" from TransactionHeaders " +
				 		" inner join Clients " +
				 		" on TransactionHeaders.ClientCode = Clients.ClientCode" +
				 		" where TransactionType=? and IssueDate>=? and IssueDate<=?  ";
				 	// Compute the selection arguments
						selectionArguments = new String [] {	   
								String.valueOf("1"),
								 
								String.valueOf (startDay) ,String.valueOf ( endDay )
						};
			 	
						cashCursor = DatabaseUtils.getInstance ( Summary2Activity.this ).getDaoSession ().getDatabase ()
							        .rawQuery(SQL , selectionArguments) ;
				

				
			
				// Compute the cursor with the corresponding sales invoice details data
					 SQL =" select  TransactionHeaders.ClientCode as _id, TransactionType, TransactionHeaders.ClientCode, Clients.ClientName, TotalTaxAmount , CurrencyCode" +
							  " from TransactionHeaders " +
							  " inner join Clients " +
							  " on TransactionHeaders.ClientCode = Clients.ClientCode" +
							  " where TransactionType=? and IssueDate>=? and IssueDate<=?  ";
							 	// Compute the selection arguments
									selectionArguments = new String [] {	   
											String.valueOf("2"),
											String.valueOf (startDay) ,String.valueOf ( endDay )
									};		 	
									checkCursor = DatabaseUtils.getInstance ( Summary2Activity.this ).getDaoSession ().getDatabase ()
										        .rawQuery(SQL , selectionArguments) ;		
						
						
						
				// Compute the total cash amount
				if ( cashCursor.moveToFirst () )
					do {
						// Retrieve the currency code
						String currencyCode = cashCursor.getString ( cashCursor.getColumnIndex ( "CurrencyCode" ) );
						// Compute the denominator
						double denominator = currencyCode.equals ( mainCurrency.getCurrencyCode () ) ? 1 : currencies.get ( currencyCode ).getCurrencyRate () / mainCurrency.getCurrencyRate ();
						// Add the amount to the total value
						totalCash += ( cashCursor.getDouble ( cashCursor.getColumnIndex ( TransactionHeadersDao.Properties.TotalTaxAmount.columnName ) ) / denominator );
					} while ( cashCursor.moveToNext () );
				// Compute the total cash amount
				if ( checkCursor.moveToFirst () )
					do {
						// Retrieve the currency code
						String currencyCode = checkCursor.getString ( checkCursor.getColumnIndex ( TransactionHeadersDao.Properties.CurrencyCode.columnName ) );
						// Compute the denominator
						double denominator = currencyCode.equals ( mainCurrency.getCurrencyCode () ) ? 1 : currencies.get ( currencyCode ).getCurrencyRate () / mainCurrency.getCurrencyRate ();
						// Add the amount to the total value
						totalCheck += ( checkCursor.getDouble ( checkCursor.getColumnIndex ( TransactionHeadersDao.Properties.TotalTaxAmount.columnName ) ) / denominator );
					} while ( checkCursor.moveToNext () );
				
				// Declare and initialize a decimal format used to properly format monetary values
				DecimalFormat moenyFormat = new DecimalFormat ( "#,##0.00" );
				// Compute the total cash amount string
				totalCashStr = moenyFormat.format ( totalCash ) + " " + mainCurrency.getCurrencySymbol ();
				// Compute the total check amount string
				totalCheckStr = moenyFormat.format ( totalCheck ) + " " + mainCurrency.getCurrencySymbol ();
				return false;
			} catch ( Exception exception ) {
				// Indicate that an error occurred
				return true;
			}
		}
		
		
		
		/**
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@SuppressLint("DefaultLocale") @Override
		protected void onPostExecute ( Boolean error ) {
			// Check if an error occurred
			if ( error )
				// Display message
				AppDialog.getInstance ().displayAlert ( Summary2Activity.this ,
						AppResources.getInstance ( Summary2Activity.this ).getString ( Summary2Activity.this , R.string.error_label ) ,
						AppResources.getInstance ( Summary2Activity.this ).getString ( Summary2Activity.this , R.string.daily_summary_report_error_message ) ,
						ButtonsType.OK , new DialogInterface.OnClickListener () {
							@Override
							public void onClick ( DialogInterface dialog , int which ) {
								// Finish the activity
								Summary2Activity.this.finish ();
							}
						} );
			
			//( (TextView) findViewById ( R.id.label_cash ) ).setText ( AppResources.getInstance ( Summary2Activity.this ).getString ( Summary2Activity.this , R.string.client_name_label )
			//		+ " : " + cltCursor );
			// Check if the cash cursor is valid
			if ( cashCursor != null ) {
				// Display the total cash label
				( (TextView) findViewById ( R.id.label_cash ) ).setText ( AppResources.getInstance ( Summary2Activity.this ).getString ( Summary2Activity.this , R.string.total_sale_order_label )
						+ " : " + totalCashStr );
				// Set the new cash list adapter
				( (ListView) findViewById ( R.id.list_cash ) ).setAdapter ( new SummaryDetailsViewAdapter ( Summary2Activity.this , cashCursor , R.layout.summary_details_view_activity_item ) );
			} // End if
			
			// Check if the check amount is valid
			if ( totalCheck > 0 && checkCursor != null ) {
				// Display the total check label
				( (TextView) findViewById ( R.id.label_check ) ).setText ( AppResources.getInstance ( Summary2Activity.this ).getString ( Summary2Activity.this , R.string.total_sale_invoice_label ).toUpperCase ()
						+ " : " + totalCheckStr );
				// Set the new check list adapter
				( (ListView) findViewById ( R.id.list_check ) ).setAdapter ( new SummaryDetailsViewAdapter ( Summary2Activity.this , checkCursor , R.layout.summary_details_view_activity_item ) );
			} // End if
			else {
				// Otherwise hide the check label and check list
				findViewById ( R.id.label_check ).setVisibility ( View.GONE );
				findViewById ( R.id.layout_check ).setVisibility ( View.GONE );
			} // End else
			
			findViewById ( R.id.label_cash ).setVisibility ( View.GONE );
			findViewById ( R.id.layout_cash ).setVisibility ( View.GONE );
			findViewById ( R.id.label_check ).setVisibility ( View.GONE );
			findViewById ( R.id.layout_check ).setVisibility ( View.GONE );
			
		}
		
		
		
	}
	
}