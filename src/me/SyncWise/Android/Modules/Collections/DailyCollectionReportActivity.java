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

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppDialog.ButtonsType;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.DateTime;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.CollectionDetailsDao;
import me.SyncWise.Android.Database.CollectionUtils;
import me.SyncWise.Android.Database.Currencies;
import me.SyncWise.Android.Database.CurrenciesDao;
import me.SyncWise.Android.Database.DaoSession;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Gson.BaseTimerActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Activity implemented as a daily collection report.
 * 
 * @author Elias
 * @sw.todo <b>Daily Collection Report Activity Implementation :</b><br>
 * Simply add this class in the {@code AndroidManifest.xml}.
 *
 */
public class DailyCollectionReportActivity extends BaseTimerActivity {

	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@SuppressLint("DefaultLocale") @Override
	public void onCreate ( Bundle savedInstanceState ) {
		// Superclass method call
		super.onCreate ( savedInstanceState );
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.daily_collection_report_activity_title ) );
		// Set the activity content from a layout resource.
		setContentView ( R.layout.daily_collection_report_activity );
		
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
		cashEmptyView.setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_cash_list_label ) );
		// Display the total cash label
		( (TextView) findViewById ( R.id.label_cash ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.total_cash_label ).toUpperCase () );
		
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
		checkEmptyView.setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_check_list_label ) );
		// Display the total check label
		( (TextView) findViewById ( R.id.label_check ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.total_check_label ).toUpperCase () );
		
		// Display the current date as sub title
		getActionBar ().setSubtitle ( DateTime.getBriefDate ( this , Calendar.getInstance () ) );
		
		// Retrieve all the collections asynchronously
		new PopulateList ().execute ();
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
		
		/*
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
				DaoSession daoSession = DatabaseUtils.getInstance ( DailyCollectionReportActivity.this ).getDaoSession ();
				
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
				
				// Compute the cursor with the corresponding collection details data set of cash payments
				cashCursor = daoSession.getDatabase ().query ( CollectionDetailsDao.TABLENAME , // Table name
						daoSession.getCollectionDetailsDao ().getAllColumns () , // Columns
						CollectionDetailsDao.Properties.CollectionDetailType.columnName + "=? AND " + CollectionDetailsDao.Properties.StampDate.columnName + ">? AND " + CollectionDetailsDao.Properties.StampDate.columnName + "<?", // Selection
						new String [] { String.valueOf ( CollectionUtils.PaymentType.CASH ) , String.valueOf ( startDay ) , String.valueOf ( endDay ) } , // Selection arguments
						null , null , null );
				// Compute the cursor with the corresponding collection details data set of cash payments
				checkCursor = daoSession.getDatabase ().query ( CollectionDetailsDao.TABLENAME , // Table name
						daoSession.getCollectionDetailsDao ().getAllColumns () , // Columns
						CollectionDetailsDao.Properties.CollectionDetailType.columnName + "=? AND " + CollectionDetailsDao.Properties.StampDate.columnName + ">? AND " + CollectionDetailsDao.Properties.StampDate.columnName + "<?", // Selection
						new String [] { String.valueOf ( CollectionUtils.PaymentType.CHECK ) , String.valueOf ( startDay ) , String.valueOf ( endDay ) } , // Selection arguments
						null , null , null );
				
				// Compute the total cash amount
				if ( cashCursor.moveToFirst () )
					do {
						// Retrieve the currency code
						String currencyCode = cashCursor.getString ( cashCursor.getColumnIndex ( CollectionDetailsDao.Properties.CurrencyCode.columnName ) );
						// Compute the denominator
						double denominator = currencyCode.equals ( mainCurrency.getCurrencyCode () ) ? 1 : currencies.get ( currencyCode ).getCurrencyRate () / mainCurrency.getCurrencyRate ();
						// Add the amount to the total value
						totalCash += ( cashCursor.getDouble ( cashCursor.getColumnIndex ( CollectionDetailsDao.Properties.CollectionAmount.columnName ) ) / denominator );
					} while ( cashCursor.moveToNext () );
				// Compute the total cash amount
				if ( checkCursor.moveToFirst () )
					do {
						// Retrieve the currency code
						String currencyCode = checkCursor.getString ( checkCursor.getColumnIndex ( CollectionDetailsDao.Properties.CurrencyCode.columnName ) );
						// Compute the denominator
						double denominator = currencyCode.equals ( mainCurrency.getCurrencyCode () ) ? 1 : currencies.get ( currencyCode ).getCurrencyRate () / mainCurrency.getCurrencyRate ();
						// Add the amount to the total value
						totalCheck += ( checkCursor.getDouble ( checkCursor.getColumnIndex ( CollectionDetailsDao.Properties.CollectionAmount.columnName ) ) / denominator );
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
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@SuppressLint("DefaultLocale") @Override
		protected void onPostExecute ( Boolean error ) {
			// Check if an error occurred
			if ( error )
				// Display message
				AppDialog.getInstance ().displayAlert ( DailyCollectionReportActivity.this ,
						AppResources.getInstance ( DailyCollectionReportActivity.this ).getString ( DailyCollectionReportActivity.this , R.string.error_label ) ,
						AppResources.getInstance ( DailyCollectionReportActivity.this ).getString ( DailyCollectionReportActivity.this , R.string.daily_collection_report_error_message ) ,
						ButtonsType.OK , new DialogInterface.OnClickListener () {
							@Override
							public void onClick ( DialogInterface dialog , int which ) {
								// Finish the activity
								DailyCollectionReportActivity.this.finish ();
							}
						} );
			
			// Check if the cash cursor is valid
			if ( cashCursor != null ) {
				// Display the total cash label
				( (TextView) findViewById ( R.id.label_cash ) ).setText ( AppResources.getInstance ( DailyCollectionReportActivity.this ).getString ( DailyCollectionReportActivity.this , R.string.total_cash_label )
						+ " : " + totalCashStr );
				// Set the new cash list adapter
				( (ListView) findViewById ( R.id.list_cash ) ).setAdapter ( new CollectionDetailsViewAdapter ( DailyCollectionReportActivity.this , cashCursor , R.layout.collection_details_view_activity_item ) );
			} // End if
			
			// Check if the check amount is valid
			if ( totalCheck > 0 && checkCursor != null ) {
				// Display the total check label
				( (TextView) findViewById ( R.id.label_check ) ).setText ( AppResources.getInstance ( DailyCollectionReportActivity.this ).getString ( DailyCollectionReportActivity.this , R.string.total_check_label ).toUpperCase ()
						+ " : " + totalCheckStr );
				// Set the new check list adapter
				( (ListView) findViewById ( R.id.list_check ) ).setAdapter ( new CollectionDetailsViewAdapter ( DailyCollectionReportActivity.this , checkCursor , R.layout.collection_details_view_activity_item ) );
			} // End if
			else {
				// Otherwise hide the check label and check list
				findViewById ( R.id.label_check ).setVisibility ( View.GONE );
				findViewById ( R.id.layout_check ).setVisibility ( View.GONE );
			} // End else
		}
		
	}
	
}