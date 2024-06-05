/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Printing;

import java.sql.Date;
 
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
 
 
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
 
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
 
import android.os.AsyncTask;
import android.os.Bundle;
 
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
 
import android.widget.TextView;

 
 
import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Vibration;
import me.SyncWise.Android.Database.CollectionHeaders;
import me.SyncWise.Android.Database.CollectionHeadersDao;
import me.SyncWise.Android.Database.CycleCalls;
import me.SyncWise.Android.Database.CycleCallsDao;
import me.SyncWise.Android.Database.DaoSession;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.Items;
import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Database.UserPasswordsUtils;
import me.SyncWise.Android.Database.Users;
import me.SyncWise.Android.Database.UsersDao;
 

import me.SyncWise.Android.Database.StatusUtils;
import me.SyncWise.Android.Database.TransactionHeaders;
import me.SyncWise.Android.Database.TransactionHeadersDao;
import me.SyncWise.Android.Database.TransactionHeadersUtils;

 
import me.SyncWise.Android.Modules.MenuList.Action;
import me.SyncWise.Android.Modules.MenuList.MenuItem;

 
import me.SyncWise.Android.Widgets.Baguette;


 
 
/**
 * Activity implemented to display the movement modules.
 * 
 * @author Elias
 * @sw.todo	<b>End Journey Activity Implementation :</b><br>
 * <ul>
 * <li>Extend this class.</li>
 * <li>Implement the {@link #populateCallMenu()} method, which should define the menu items using the {@link #addMenuItem(MenuItem)} method.</li>
 * <li>Do not forget to add your new class in the {@code AndroidManifest.xml} file.</li>
 * </ul>
 *
 */
public class EndJourneyActivity  extends me.SyncWise.Android.Modules.MenuList.MenuListActivity     {
	public static final String SELECTED_YEAR = EndJourneyActivity.class.getName () + ".SELECTED_YEAR";
	
	/**
	 * Integer holding the selected year of the delivery date.
	 */
	protected int selectedYear;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #selectedMonth}.
	 */
	public static final String SELECTED_MONTH = EndJourneyActivity.class.getName () + ".SELECTED_MONTH";
	
	/**
	 * Integer holding the selected month of the delivery date.
	 */
	protected int selectedMonth;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #selectedDay}.
	 */
	public static final String SELECTED_DAY = EndJourneyActivity.class.getName () + ".SELECTED_DAY";
	
	/**
	 * Integer holding the selected day of the delivery date.
	 */
	protected int selectedDay;
	private Calendar calendar = Calendar.getInstance ();
	private 	long startDay,   	 min ,endDay;
	
	
	
   	   /**
		 * Bundle key used to put/retrieve the content of {@link #passcode}.
		 */
		public static final String PASSCODE = EndJourneyActivity.class.getName () + ".PASSCODE";
		
		/**
		 * String holding the passcode.
		 */
		protected String passcode;
	   	
		/**
		 * Bundle key used to put/retrieve the content of {@link #displayPasscode}.
		 */
		private static final String DISPLAY_PASSCODE = EndJourneyActivity.class.getName () + ".DISPLAY_PASSCODE";
		
		/**
		 * Boolean used to determine whether to display the passcode UI or not.
		 */
		protected boolean displayPasscode;
		
	
	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate ( Bundle savedInstanceState ) {
		calendar.set ( Calendar.HOUR_OF_DAY , 0 );
		calendar.set ( Calendar.MINUTE , 0 );
		calendar.set ( Calendar.SECOND , 0 );
		calendar.set ( Calendar.MILLISECOND , 0 );
		startDay = calendar.getTimeInMillis ();
		// Populate home screen menu
		 populateCallMenu ();
		//new PopulateList ().execute ();
		// Set the activity content from a layout resource.
		setContentView ( R.layout.home_screen_activity );
		// Superclass method call	
		super.onCreate ( savedInstanceState );
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.end_journey_title ) );
		
		
		// Determine if a saved instance state is provided
				if ( savedInstanceState != null ) {
		
		selectedYear = savedInstanceState.getInt ( SELECTED_YEAR );
		selectedMonth = savedInstanceState.getInt ( SELECTED_MONTH );
		selectedDay = savedInstanceState.getInt ( SELECTED_DAY );
		}
	//	findViewById ( R.id.layout_date_picker ).setVisibility ( View.GONE );
	}
	
	
	 protected void onSaveInstanceState ( Bundle outState ) {
		// Superclass method call
	    	super.onSaveInstanceState ( outState );
	    	// Save the content of selectedYear in the outState bundle
	    	outState.putInt ( SELECTED_YEAR , selectedYear );
	    	// Save the content of selectedMonth in the outState bundle
	    	outState.putInt ( SELECTED_MONTH , selectedMonth );
	    	// Save the content of selectedDay in the outState bundle
	    	outState.putInt ( SELECTED_DAY , selectedDay );
	    	
	    	// Save the content of selectedDay in the outState bundle
	    	outState.putBoolean ( DISPLAY_PASSCODE , displayPasscode );
	    	if ( passcode != null )
		    	// Save the content of passcode in the outState bundle
		    	outState.putString ( PASSCODE , passcode );
	     
	 }
	/*
	 * Initialize the contents of the Activity's standard options menu.
	 *
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
//    @Override
//    public boolean onCreateOptionsMenu ( Menu menu ) {
//    	// Use the MenuInflater of this context to inflate a menu hierarchy from the specified XML resource
//    	getMenuInflater ().inflate ( R.menu.action_bar , menu );
//    	// Enable the required menu items
//    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_calendar ) );
//    	
//    	
//		// Display the menu
//        return true;
//    }
   
	 
	 /*
		 * Called when the activity has detected the user's press of the back key. 
		 *
		 * @see android.app.Activity#onBackPressed()
		 */
		@Override
		public void onBackPressed () {	 
	 if ( displayPasscode ) {
			// Reset flag
			displayPasscode = false;
			// Retrieve a reference to the tertiary view
			View tertiary = findViewById ( R.id.layout_passcode );
			// Hide the tertiary view
			tertiary.startAnimation ( getViewAnimationOut ( tertiary ) );
 		// Enable the main list
	 
 	//	( (LinearLayout) findViewById ( R.id.linearlayout ) ).setEnabled ( true );
 		// Refresh the action bar
 		invalidateOptionsMenu ();
 		// Exit method
 		return;
		} // End else if
	 else 
			// Superclass method call
			super.onBackPressed ();
		}
		/**
		 * Initializes the passcode (tertiary) view.
		 * 
		 * @param message	String hosting the message to display for the user regarding the passcode.
		 */
		protected void initializeTertiaryView ( String message ) {
			// Retrieve a reference to the parent view
			View parent = findViewById ( R.id.layout_passcode );
			// Retrieve a reference to the passcode edit text
			EditText passcodeEditText = (EditText) parent.findViewById ( R.id.edittext_passcode );		
			// Retrieve a reference to the passcode title label
			TextView titleLabel = (TextView) parent.findViewById ( R.id.label_passcode );
			// Retrieve a reference to the passcode message label
			TextView messageLabel = (TextView) parent.findViewById ( R.id.label_passcode_message );
			
			// Display the title
			titleLabel.setText ( AppResources.getInstance ( this ).getString ( this , R.string.passcode_label ) );
			// Check if a message is provided
			if ( message != null ) {
				// Display the passcode title label
				messageLabel.setText ( message );
				// Clear any previous entries
				passcodeEditText.setText ( "" );
			} // End if
			// Enable the save icon
			parent.findViewById ( R.id.icon_save_passcode ).setEnabled ( true );
			// Enable the edit text
			passcodeEditText.setEnabled ( true );
			// Display the field hint
			passcodeEditText.setHint ( AppResources.getInstance ( this ).getString ( this , R.string.movement_passcode_hint ) );
		}
	 /**
	   	 * Displays a warning regarding unpaid invoices for cash client.<br>
	   	 * The user has the option to use a pass code.
	   	 */
	   	private void displayWarning () {
	   		// Declare and initialize an alert dialog builder
	   		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder ( this );
	   		// By default, the alert dialog cannot be cancelled UNLESS it has no buttons
	   		alertDialogBuilder.setCancelable ( false );
	   		// Set the title
	   		alertDialogBuilder.setTitle ( R.string.warning_label );
	   		// Set the description
	   		alertDialogBuilder.setMessage ( R.string.passcode_required_message );
	   		// Map the positive and negative buttons
	   		alertDialogBuilder.setPositiveButton ( R.string.ok_label , new DialogInterface.OnClickListener() {
	   			@Override
	   			public void onClick ( DialogInterface dialog , int which ) {
	   	    		// Set flag
	   			 
	   				// Start the new activity
	   				//startActivityForResult ( getIntent_EndJourneyActivity_New ( null,"cash" ) , EndJourneyActivity.REQUEST_CODE_NEW );
	   				// Specify an explicit transition animation to perform next
	   				//ActivityTransition.SlideOutLeft ( SalesInvoiceActivity.this );
	   			}	
	   			} );
	   	    final String description = AppResources.getInstance ( this ).getString ( this , R.string.passcode_required_message );
		    	
	   		alertDialogBuilder.setNegativeButton ( R.string.use_passcode , new DialogInterface.OnClickListener() {
	   			@Override
	   			public void onClick ( DialogInterface dialog , int which ) {
	   	    		// Set flag
	   				displayPasscode = true;
	   			// Disable the main list
	   			//	( (LinearLayout) findViewById ( R.id.linearlayout ) ).setEnabled ( false );
	   	    			// Initialize the tertiary view
	   	    		initializeTertiaryView ( description );
	   	    		// Retrieve a reference to the tertiary view
	   	    		View tertiaryView = findViewById ( R.id.layout_passcode );
	   	    		// Display the tertiary view
	   	    		tertiaryView.setVisibility ( View.VISIBLE );
	   	    		// Animate the tertiary view
	   	    		tertiaryView.startAnimation ( getViewAnimationIn() );
	   	    		// Refresh the action bar
	   	    		invalidateOptionsMenu ();
	   			}
	   		} );
	   		// Create and show the alert dialog
	   		alertDialogBuilder.create ().show ();
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
			View tertiary = findViewById ( R.id.layout_passcode );
			// Retrieve a reference to the passcode edit text
			EditText passcodeEditText = (EditText) tertiary.findViewById ( R.id.edittext_passcode );
			// Store the passcode
			passcode = passcodeEditText.getText ().toString ().trim ();
			
			// Validate pass code
			//if ( ! UserPasswordsUtils.validateTimePasswordClients ( this , UserPasswordsUtils.TransactionHeaderType.TRANSACTION , TransactionHeadersUtils.Type.SALES_INVOICE , passcode  ,( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClient ().getClientCode() ) ) {
			if ( ! UserPasswordsUtils.validateRegularPassword( this ,  passcode  ) ) {
				
			// Reset passcode
				passcode = null;
				// Indicate that the passcode is not valid
				Baguette.showText ( EndJourneyActivity.this ,
						AppResources.getInstance ( EndJourneyActivity.this ).getString ( EndJourneyActivity.this , R.string.time_passcode_invalid_message ) ,
						Baguette.BackgroundColor.RED );
				// Exit method
				return;
			} // End if
			
			// Reset flag
			displayPasscode = false;
			
			// Disable the save icon
			tertiary.findViewById ( R.id.icon_save_passcode ).setEnabled ( false );
			// Hide the software keyboard
	        ( (InputMethodManager) getBaseContext ().getSystemService ( INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow ( tertiary.getWindowToken (), 0 );
	        
			// Hide the tertiary view
	        tertiary.startAnimation ( getViewAnimationOut ( tertiary ) );
			// Enable the main list
	    	//( (LinearLayout) findViewById ( R.id.linearlayout ) ).setEnabled ( true );
			// Indicate that the save was successful
			Vibration.vibrate ( this );
			// Refresh the action bar
			invalidateOptionsMenu ();
			AppDialog.getInstance ().displayDatePicker ( EndJourneyActivity.this , selectedYear , selectedMonth , selectedDay , min , false , true ,
    				new DatePickerDialog.OnDateSetListener () {
						@Override
						public void onDateSet ( DatePicker view , int year , int monthOfYear , int dayOfMonth ) {
							// Update the delivery day
							selectedYear = year;
							selectedMonth = monthOfYear;
							selectedDay = dayOfMonth;
							
							// Retrieve the start of the day
							calendar.set ( Calendar.HOUR_OF_DAY , 0 );
							calendar.set ( Calendar.MINUTE , 0 );
							calendar.set ( Calendar.SECOND , 0 );
							calendar.set ( Calendar.MILLISECOND , 0 );
							calendar.set(Calendar.YEAR, selectedYear);
							calendar.set(Calendar.MONTH, selectedMonth);
							calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
							startDay = calendar.getTimeInMillis ();
							//Toast.makeText(getApplicationContext(), " "+startDay, Toast.LENGTH_LONG).show();
							//populateCallMenu ();
							 
						}
					} , null );
		 
		}
	// Retrieve the start of the day;
	/**
	 * Populate the call menu.
	 */
	protected void populateCallMenu () {
		DaoSession daoSession = DatabaseUtils.getInstance ( this ).getDaoSession ();
		// Retrieve the current date
		//Calendar calendar = Calendar.getInstance ();
		// Retrieve the start of the day
//		calendar.set ( Calendar.HOUR_OF_DAY , 0 );
//		calendar.set ( Calendar.MINUTE , 0 );
//		calendar.set ( Calendar.SECOND , 0 );
//		calendar.set ( Calendar.MILLISECOND , 0 );
//		 startDay = calendar.getTimeInMillis ();
		
		// Toast.makeText(getApplicationContext(), " "+startDay, Toast.LENGTH_LONG).show();
		 Calendar calendarMIN = Calendar.getInstance ();
			// Retrieve the start of the day
		 calendarMIN.set ( Calendar.HOUR_OF_DAY , 0 );
		 calendarMIN.set ( Calendar.MINUTE , 0 );
		 calendarMIN.set ( Calendar.SECOND , 0 );
		 calendarMIN.set ( Calendar.MILLISECOND , 0 );
		 calendarMIN.add (Calendar.DAY_OF_MONTH,-4 );
		  	 min= calendarMIN.getTimeInMillis ();
//		ArrayList < CollectionHeaders > collectionSummary = new ArrayList < CollectionHeaders > ();
//		collectionSummary.addAll ( daoSession.getCollectionHeadersDao ().queryBuilder ().where ( CollectionHeadersDao.Properties.CollectionDate.gt ( startDay ) ).list () );
		  	final boolean b;
		  	if(PermissionsUtils.getEnableDateSummary ( this , DatabaseUtils.getCurrentUserCode ( this ) , DatabaseUtils.getCurrentCompanyCode ( this ) ))
		  		b=true;
		  	else 
		  		b=false;
			String userCode = DatabaseUtils.getCurrentUserCode ( this );
			// Retrieve the company code
			String companyCode = DatabaseUtils.getCurrentCompanyCode ( this );
			// Retrieve the user
			Users user = DatabaseUtils.getInstance ( this ).getDaoSession ().getUsersDao ().queryBuilder ()
					.where ( UsersDao.Properties.UserCode.eq ( userCode ) , UsersDao.Properties.CompanyCode.eq ( companyCode ) ).unique ();
	       if(user.getUserType()==11){
	    	addMenuItem ( new MenuItem ()
	    	.setTitleId ( R.string.date_label )
	    	.setIconId ( R.drawable.calendar_1 )
		
	    	.setAction( new Action () {
	    		@Override
				public void onClick ( Activity activity ) {
				if ( b==true)
				displayWarning();  
	 else	 
	 {		AppDialog.getInstance ().displayDatePicker ( EndJourneyActivity.this , selectedYear , selectedMonth , selectedDay , min , false , true ,
	    				new DatePickerDialog.OnDateSetListener () {
							@Override
							public void onDateSet ( DatePicker view , int year , int monthOfYear , int dayOfMonth ) {
								// Update the delivery day
								selectedYear = year;
								selectedMonth = monthOfYear;
								selectedDay = dayOfMonth;
								
								// Retrieve the start of the day
								calendar.set ( Calendar.HOUR_OF_DAY , 0 );
								calendar.set ( Calendar.MINUTE , 0 );
								calendar.set ( Calendar.SECOND , 0 );
								calendar.set ( Calendar.MILLISECOND , 0 );
								calendar.set(Calendar.YEAR, selectedYear);
								calendar.set(Calendar.MONTH, selectedMonth);
								calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
								startDay = calendar.getTimeInMillis ();
								//Toast.makeText(getApplicationContext(), " "+startDay, Toast.LENGTH_LONG).show();
								//populateCallMenu ();
								 
							}
						} , null );
	    		// Consume event
	    	//	return true;
			}
	    		// Consume event
	    	 
			}
		} ));
	//	.setActivity ( PrintingActivity.class )
		//.putData ( PrintingActivity.PRINTOUT , PrintingActivity.Printout.ORIGINAL )
		//.putData ( PrintingActivity.TYPE , PrintingActivity.Type.COLLECTIONSUMMARY )
	  //  .putData ( PrintingActivity.HEADER , getCollectionSummaryPrinting ()  ) );
	
		addMenuItem ( new MenuItem ()
			.setTitleId ( R.string.end_journey_report_collection_summary_title )
			.setIconId ( R.drawable.money )
			.setAction( new Action () {
				@Override
				public void onClick ( Activity activity ) {
					 ArrayList<CollectionSummaryPrinting> list=getCollectionSummaryPrinting  (startDay);
					 ArrayList<SummaryReturnPrinting> list1=getCollectionSummaryReturnPrinting  (startDay);
						
					 if((list != null && list.size()> 0) || (list1 != null && list1.size()> 0) ){
					Intent intent = new Intent ( EndJourneyActivity.this, PrintingActivity.class );
					intent.putExtra ( PrintingActivity.TYPE , PrintingActivity.Type.COLLECTIONSUMMARY );
					intent.putExtra ( PrintingActivity.PRINTOUT , PrintingActivity.Printout.ORIGINAL );
					intent.putExtra ( PrintingActivity.HEADER ,getCollectionSummaryPrinting (startDay));
					intent.putExtra(PrintingActivity.DETAILS, getCollectionSummaryReturnPrinting (startDay));
					startActivityForResult ( intent , 10 );
    		// Consume event
		    	//	return true;
					
		    		// Consume event
					 }else{
						 Baguette.showText ( EndJourneyActivity .this ,
									"No Collection in this journey" ,
									Baguette.BackgroundColor.RED );
					 }
				}
			} )
			
				);
			//.setActivity ( PrintingActivity.class )
			//.putData ( PrintingActivity.PRINTOUT , PrintingActivity.Printout.ORIGINAL )
			//.putData ( PrintingActivity.TYPE , PrintingActivity.Type.COLLECTIONSUMMARY )
			//.putData ( PrintingActivity.HEADER , getCollectionSummaryPrinting (startDay)  ) );
		//TransactionHeadersDao.Properties.TransactionStatus.eq ( StatusUtils.isAvailable () ) ,
		 
       addMenuItem ( new MenuItem ()
			.setTitleId ( R.string.end_journey_report_invoice_summary_title )
			.setIconId ( R.drawable.cash )
			//.setActivity ( PrintingActivity.class )
			//.putData ( PrintingActivity.PRINTOUT , PrintingActivity.Printout.ORIGINAL )
		//	.putData ( PrintingActivity.TYPE , PrintingActivity.Type.INVOICESUMMARY )
			//.putData ( PrintingActivity.HEADER , invoiceSummary ) );
				.setAction( new Action () {
				@Override
				public void onClick ( Activity activity ) {

					 ArrayList<TransactionHeaders> list=getTransactionHeaders   (startDay);
						if(list!=null && list.size()>0){
					Intent intent = new Intent ( EndJourneyActivity.this, PrintingActivity.class );
					intent.putExtra ( PrintingActivity.TYPE , PrintingActivity.Type.INVOICESUMMARY );
					intent.putExtra ( PrintingActivity.PRINTOUT , PrintingActivity.Printout.ORIGINAL );
					intent.putExtra ( PrintingActivity.HEADER ,getTransactionHeaders  (startDay));
					startActivityForResult ( intent , 10 );
					}else{
						 Baguette.showText ( EndJourneyActivity .this ,
									"No Invoice in this journey" ,
									Baguette.BackgroundColor.RED );
					}
				}}));
		
		ArrayList < MissedItems > missedItems = getMissedItems ();
	 
		addMenuItem ( new MenuItem ()
			.setTitleId ( R.string.end_journey_report_missed_items_title )
			.setIconId ( R.drawable.boxes )
			.setActivity ( PrintingActivity.class )
			.putData ( PrintingActivity.PRINTOUT , PrintingActivity.Printout.ORIGINAL )
			.putData ( PrintingActivity.TYPE , PrintingActivity.Type.MISSEDITEMS )
			.putData ( PrintingActivity.HEADER , missedItems ) );
		
		ArrayList < Items > items = getItemDistributionHeaders ();
		ArrayList < ItemDistrbutionPrinting > itemDistributionDetails = getItemDistributionDetails ();
 
		addMenuItem ( new MenuItem ()
			.setTitleId ( R.string.end_journey_report_item_distribution_title )
			.setIconId ( R.drawable.boxes_1 )
			.setActivity ( PrintingActivity.class )
			.putData ( PrintingActivity.PRINTOUT , PrintingActivity.Printout.ORIGINAL )
			.putData ( PrintingActivity.TYPE , PrintingActivity.Type.ITEMDISTRIBUTION )
			.putData ( PrintingActivity.HEADER , items )
			.putData ( PrintingActivity.DETAILS , itemDistributionDetails ) );
		
		ArrayList < ClientAssetPrinting > clientAssetPrinting = getClientAssetPrinting ();
		 
		addMenuItem ( new MenuItem ()
			.setTitleId ( R.string.end_journey_report_client_asset_title )
			.setIconId ( R.drawable.boxes_3 )
			.setActivity ( PrintingActivity.class )
			.putData ( PrintingActivity.PRINTOUT , PrintingActivity.Printout.ORIGINAL )
			.putData ( PrintingActivity.TYPE , PrintingActivity.Type.CLIENTASSET )
			.putData ( PrintingActivity.HEADER , clientAssetPrinting ) );
		
		ArrayList < CycleCalls > cycleCalls = new ArrayList < CycleCalls > ();
		cycleCalls.addAll ( daoSession.getCycleCallsDao ().queryBuilder ().where ( CycleCallsDao.Properties.CycleCallStatus.eq ( StatusUtils.isAvailable () ) )
				.orderDesc ( CycleCallsDao.Properties.Week , CycleCallsDao.Properties.Day , CycleCallsDao.Properties.Sequence ).list () );
		addMenuItem ( new MenuItem ()
			.setTitleId ( R.string.end_journey_report_plan_visits_title )
			.setIconId ( R.drawable.users )
			.setActivity ( PrintingActivity.class )
			.putData ( PrintingActivity.PRINTOUT , PrintingActivity.Printout.ORIGINAL )
			.putData ( PrintingActivity.TYPE , PrintingActivity.Type.PLANVISITSREPORT )
			.putData ( PrintingActivity.HEADER , cycleCalls ) );
		
		ArrayList < CollectionHeaders > collectionSettel = new ArrayList < CollectionHeaders > ();
		collectionSettel.addAll ( daoSession.getCollectionHeadersDao ().queryBuilder ()
				 .where ( CollectionHeadersDao.Properties.CollectionDate.gt ( startDay ) , CollectionHeadersDao.Properties.IsSetteled.eq ( 1 ) ).list () );
		addMenuItem ( new MenuItem ()
			.setTitleId ( R.string.end_journey_report_Collection_Settel_title )
			.setIconId ( R.drawable.collection )
			.setActivity ( PrintingActivity.class )
			.putData ( PrintingActivity.PRINTOUT , PrintingActivity.Printout.ORIGINAL )
			.putData ( PrintingActivity.TYPE , PrintingActivity.Type.COLLECTIONISSETTEL )
			.putData ( PrintingActivity.HEADER , collectionSettel ) );
		
	//	ArrayList < LoadInSummaryPrinting > loadInSummaryPrinting = getLoadINSummaryPrintings ( startDay );
		addMenuItem ( new MenuItem ()
			.setTitleId ( R.string.end_journey_report_load_in_summary_title )
			.setIconId ( R.drawable.boxes )
			.setAction( new Action () {
				@Override
				public void onClick ( Activity activity ) {
					Intent intent = new Intent ( EndJourneyActivity.this, PrintingActivity.class );
					intent.putExtra ( PrintingActivity.TYPE , PrintingActivity.Type.loadInSummary );
					intent.putExtra ( PrintingActivity.PRINTOUT , PrintingActivity.Printout.ORIGINAL );
					intent.putExtra ( PrintingActivity.HEADER ,getLoadINSummaryPrintings ( startDay ));
					//intent.putExtra(PrintingActivity.DETAILS, getCollectionSummaryReturnPrinting (startDay));
					startActivityForResult ( intent , 10 );
    		// Consume event
		    	//	return true;
					
		    		// Consume event
		    	 
				}
			} )
			
			
			
			//.setActivity ( PrintingActivity.class )
			//.putData ( PrintingActivity.PRINTOUT , PrintingActivity.Printout.ORIGINAL )
			//.putData ( PrintingActivity.TYPE , PrintingActivity.Type.loadInSummary )
		//	.putData ( PrintingActivity.HEADER , loadInSummaryPrinting )
			);
		
		//ArrayList < LoadInSummaryPrinting > returnSummaryPrinting = getSalesReturnSummeryPrinting ( startDay );
		addMenuItem ( new MenuItem ()
			.setTitleId ( R.string.end_journey_report_sales_return_summary_title )
			.setIconId ( R.drawable.boxes_1 )
			.setAction( new Action () {
				@Override
				public void onClick ( Activity activity ) {
					 ArrayList<LoadInSummaryPrinting> list=getSalesReturnSummeryPrinting   (startDay);
						if(list!=null && list.size()>0){
					Intent intent = new Intent ( EndJourneyActivity.this, PrintingActivity.class );
					intent.putExtra ( PrintingActivity.TYPE , PrintingActivity.Type.SalesReturnSummeryReport );
					intent.putExtra ( PrintingActivity.PRINTOUT , PrintingActivity.Printout.ORIGINAL );
					intent.putExtra ( PrintingActivity.HEADER ,getSalesReturnSummeryPrinting ( startDay ));
					//intent.putExtra(PrintingActivity.DETAILS, getCollectionSummaryReturnPrinting (startDay));
					startActivityForResult ( intent , 10 );
						}else{
							 Baguette.showText ( EndJourneyActivity .this ,
										"No Return in this journey" ,
										Baguette.BackgroundColor.RED );
						}
		    	 
				}
			} )
			
			
//			.setActivity ( PrintingActivity.class )
//			.putData ( PrintingActivity.PRINTOUT , PrintingActivity.Printout.ORIGINAL )
//			.putData ( PrintingActivity.TYPE , PrintingActivity.Type.SalesReturnSummeryReport )
//			.putData ( PrintingActivity.HEADER , returnSummaryPrinting ) 
			
				);
		
		//ArrayList < LoadInSummaryPrinting > freeSummaryPrinting = getSalesFreeSummeryPrinting ( startDay );
		addMenuItem ( new MenuItem ()
			.setTitleId ( R.string.end_journey_report_sales_free_summary_title )
			.setIconId ( R.drawable.boxes_2)
			.setAction( new Action () {
				@Override
				public void onClick ( Activity activity ) {
					 ArrayList<LoadInSummaryPrinting> list=getSalesFreeSummeryPrinting   (startDay);
						if(list!=null && list.size()>0){
					
					Intent intent = new Intent ( EndJourneyActivity.this, PrintingActivity.class );
					intent.putExtra ( PrintingActivity.TYPE , PrintingActivity.Type.FreeSummeryReport );
					intent.putExtra ( PrintingActivity.PRINTOUT , PrintingActivity.Printout.ORIGINAL );
					intent.putExtra ( PrintingActivity.HEADER , getSalesFreeSummeryPrinting ( startDay ));
					//intent.putExtra(PrintingActivity.DETAILS, getCollectionSummaryReturnPrinting (startDay));
					startActivityForResult ( intent , 10 );
    		 
						}else{
							 Baguette.showText ( EndJourneyActivity .this ,
										"No Free in this journey" ,
										Baguette.BackgroundColor.RED );
						}
				}
			} )
			
			
//			.setActivity ( PrintingActivity.class )
//			.putData ( PrintingActivity.PRINTOUT , PrintingActivity.Printout.ORIGINAL )
//			.putData ( PrintingActivity.TYPE , PrintingActivity.Type.FreeSummeryReport )
//			.putData ( PrintingActivity.HEADER , freeSummaryPrinting )
			);
		
		//ArrayList < LoadInSummaryPrinting > movmentInSummaryPrinting = getMovementSummaryPrintings ( startDay );
		addMenuItem ( new MenuItem ()
			.setTitleId ( R.string.end_journey_report_movement_summary_title )
			.setIconId ( R.drawable.boxes_9 )
					.setAction( new Action () {
				@Override
				public void onClick ( Activity activity ) {
					 ArrayList<LoadInSummaryPrinting> list=getMovementSummaryPrintings   (startDay);
						if(list!=null && list.size()>0){
					Intent intent = new Intent ( EndJourneyActivity.this, PrintingActivity.class );
					intent.putExtra ( PrintingActivity.TYPE , PrintingActivity.Type.MovementInSummary );
					intent.putExtra ( PrintingActivity.PRINTOUT , PrintingActivity.Printout.ORIGINAL );
					intent.putExtra ( PrintingActivity.HEADER ,  getMovementSummaryPrintings ( startDay ));
					//intent.putExtra(PrintingActivity.DETAILS, getCollectionSummaryReturnPrinting (startDay));
					startActivityForResult ( intent , 10 );
						}else{
							 Baguette.showText ( EndJourneyActivity .this ,
										"No Movement in this journey" ,
										Baguette.BackgroundColor.RED );
						}
		    	 
				}
			} )
			
//			.setActivity ( PrintingActivity.class )
//			.putData ( PrintingActivity.PRINTOUT , PrintingActivity.Printout.ORIGINAL )
//			.putData ( PrintingActivity.TYPE , PrintingActivity.Type.MovementInSummary )
//			.putData ( PrintingActivity.HEADER , movmentInSummaryPrinting )
//			
				);
       }else{
		addMenuItem ( new MenuItem ()
		.setTitleId ( R.string.end_journey_report_sales_order_summary_title )
		.setIconId ( R.drawable.boxes_1 )
			.setAction( new Action () {
			@Override
			public void onClick ( Activity activity ) {
				 ArrayList<TransactionHeaders> list=getTransactionHeaders1   (startDay);
					if(list!=null && list.size()>0){
				Intent intent = new Intent ( EndJourneyActivity.this, PrintingActivity.class );
				intent.putExtra ( PrintingActivity.TYPE , PrintingActivity.Type.SALESORDERSUMMARY );
				intent.putExtra ( PrintingActivity.PRINTOUT , PrintingActivity.Printout.ORIGINAL );
				intent.putExtra ( PrintingActivity.HEADER ,getTransactionHeaders1  (startDay));
				startActivityForResult ( intent , 10 );
					}else{
						 Baguette.showText ( EndJourneyActivity .this ,
									"No Order in this journey" ,
									Baguette.BackgroundColor.RED );
					}
			}}));
       }
		
	}
	
	private ArrayList < ClientAssetPrinting > getClientAssetPrinting () {
		ArrayList < ClientAssetPrinting > headers = new ArrayList<ClientAssetPrinting>();
	 
		
		String SQL=null;
	 	String selectionArguments [] = null;
			SQL = " select ClientAssetStatus.AssetCode,ClientAssetStatus.AssetName , StatusTable.StatusName , ExistanceTable.StatusName as ExistanceName , Items.itemname from ClientAssetStatus" +
				  " left join AssetsStatus StatusTable on StatusTable.StatusID  = ClientAssetStatus.StatusCode and StatusTable.StatusType =2 " +
				  " left join AssetsStatus ExistanceTable on ExistanceTable.StatusID  = ClientAssetStatus.ExistanceCode and ExistanceTable.StatusType =1 " +
				  " inner join Items on items.ItemCode = ClientAssetStatus.AssetCode and ItemType =2 where transactionCode = ?  ";
		String trCode="1";
		// Compute the selection arguments
			selectionArguments = new String [] {
				String.valueOf (trCode)  

			};
			Cursor cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
					        .rawQuery(SQL , selectionArguments) ;
			
	
			
			// Move the cursor to the first raw
			if ( cursor.moveToFirst () ) {
				// Iterate over all raws
				do {
					 
					ClientAssetPrinting clientAssetPrinting =new ClientAssetPrinting();
					clientAssetPrinting.setAssetCode(  cursor.getString(0) );
					clientAssetPrinting.setAssetName(cursor.getString(1)) ;
					clientAssetPrinting.setStatusName(cursor.getString(2)) ;
					clientAssetPrinting.setExistanceName(cursor.getString(3));
					clientAssetPrinting.setItemName(cursor.getString(4));
					headers.add ( clientAssetPrinting );
				} while ( cursor.moveToNext () );
			} // End if
			// Close the cursor
			cursor.close ();
			cursor = null;
			return headers;
	}
	
	private ArrayList < ItemDistrbutionPrinting > getItemDistributionDetails () {
		ArrayList < ItemDistrbutionPrinting > details = new ArrayList<ItemDistrbutionPrinting>();
		
		String SQL=null;
	 	String selectionArguments [] = null;
		int trtype=1;
			
			SQL =   " select td.ItemCode ,td.QuantityMedium,td.QuantitySmall  ,c.ClientName " +
					" from TransactionDetails td inner join TransactionHeaders th on th.TransactionCode=td.TransactionCode " +
					" inner join Clients c on c.ClientCode=th.ClientCode  where transactiontype = ? order by ItemCode   ";
		 
		// Compute the selection arguments
			selectionArguments = new String [] {
				String.valueOf (trtype)  

			};
			 Cursor cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
					        .rawQuery(SQL , selectionArguments) ;
			
			// Declare and initialize a list of calendars, used to compute the sales orders dates
			
			// Move the cursor to the first raw
			if ( cursor.moveToFirst () ) {
				// Iterate over all raws
				do {
					 
					ItemDistrbutionPrinting loadPrinting =new ItemDistrbutionPrinting();
					loadPrinting.setItemCode(  cursor.getString(0) );
					loadPrinting.setQuantityMedium(cursor.getDouble(1)) ;
					loadPrinting.setQuantitySmall(cursor.getDouble(2));
					loadPrinting.setClientName(cursor.getString(3) );
					details.add ( loadPrinting );
				} while ( cursor.moveToNext () );
			} // End if
			// Close the cursor
			cursor.close ();
			cursor = null;
			return details;
	}
	
	private ArrayList < Items > getItemDistributionHeaders () {
		ArrayList < Items > headers = new ArrayList<Items>();
		
		String SQL=null;
	 	String selectionArguments [] = null;
			SQL = " select distinct td.ItemCode ,td.ItemName " +
				  " from TransactionDetails td inner join TransactionHeaders " +
				  " th on th.TransactionCode=td.TransactionCode where transactiontype = ?  order by ItemCode   ";
		int trtype=1;
		// Compute the selection arguments
			selectionArguments = new String [] {
				String.valueOf (trtype)  

			};
			Cursor cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
					        .rawQuery(SQL , selectionArguments) ;
			
			// Declare and initialize a list of calendars, used to compute the sales orders dates
			
			// Move the cursor to the first raw
			if ( cursor.moveToFirst () ) {
				// Iterate over all raws
				do {
					 
					Items loadPrinting =new Items();
					loadPrinting.setItemCode(  cursor.getString(0) );
					loadPrinting.setItemName(cursor.getString(1)) ;
					 
					headers.add ( loadPrinting );
				} while ( cursor.moveToNext () );
			} // End if
			// Close the cursor
			cursor.close ();
			cursor = null;
			return headers;
	}
	
	private ArrayList < MissedItems > getMissedItems () {
		ArrayList < MissedItems > headers = new ArrayList<MissedItems>();
		String SQL=null;
	 	String selectionArguments [] = null;
			SQL = " select SUM(missedQuantityMedium) as Box ,SUM(MissedQuantitySmall) as Unit ," +
					" SUM(MissedBasicUnitQuantity) as baseUnit , TransactionDetails.ItemCode , " +
					" items.ItemName from TransactionDetails inner join Items on items.ItemCode = TransactionDetails.ItemCode " +
					" where MissedBasicUnitQuantity > 0 and TransactionCode in " +
					" (select TransactionCode from TransactionHeaders  where TransactionType=  ?  and TransactionStatus=?  )" +
					" group by transactiondetails.itemcode , items.ItemName   ";
		int trtype=1;
		// Compute the selection arguments
		selectionArguments = new String [] {
			String.valueOf (trtype)  ,
			String.valueOf (trtype)  

		};
		Cursor cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
				        .rawQuery(SQL , selectionArguments) ;
		
		// Declare and initialize a list of calendars, used to compute the sales orders dates
		
		// Move the cursor to the first raw
		if ( cursor.moveToFirst () ) {
			// Iterate over all raws
			do {
				 
				MissedItems missedItems =new MissedItems();
				missedItems.setItemCode(  cursor.getString(3) );
				missedItems.setItemName(cursor.getString(4)) ;
				missedItems.setQuantityMedium(cursor.getDouble(0)) ;
				missedItems.setQuantitySmall(cursor.getDouble(1));
				missedItems.setBaseUnit(cursor.getDouble(2));
				headers.add ( missedItems );
			} while ( cursor.moveToNext () );
		} // End if
		// Close the cursor
		cursor.close ();
		cursor = null;
		return headers;
	}

	private ArrayList < TransactionHeaders > getTransactionHeaders (long date) {
		//Toast.makeText(getApplicationContext(), "date "+date, Toast.LENGTH_LONG).show();
		DaoSession daoSession = DatabaseUtils.getInstance ( this ).getDaoSession ();
	ArrayList < TransactionHeaders > invoiceSummary = new ArrayList < TransactionHeaders > ();
	invoiceSummary.addAll ( daoSession.getTransactionHeadersDao ().queryBuilder ().where ( TransactionHeadersDao.Properties.IssueDate.gt ( date ) ,
			 TransactionHeadersDao.Properties.TransactionType.eq ( TransactionHeadersUtils.Type.SALES_INVOICE ),TransactionHeadersDao.Properties.TransactionStatus.gt ( 1 ) 
		 ).list () );
	return invoiceSummary;
	}
	private ArrayList < CollectionSummaryPrinting > getCollectionSummaryPrinting (long date) {
		ArrayList < CollectionSummaryPrinting > headers = new ArrayList<CollectionSummaryPrinting>();
//		Calendar calendar = Calendar.getInstance ();
//		// Retrieve the start of the day
//		calendar.set ( Calendar.HOUR_OF_DAY , 0 );
//		calendar.set ( Calendar.MINUTE , 0 );
//		calendar.set ( Calendar.SECOND , 0 );
//		calendar.set ( Calendar.MILLISECOND , 0 );
//		long startDay = calendar.getTimeInMillis ();
//		
		
		String SQL=null;
	 	String selectionArguments [] = null;
//			SQL = "  select  ch.ClientCode,ch.CurrencyCode,ch.CollectionCode,ch.TotalAmount ,cd.CollectionDetailType from CollectionHeaders ch " +
//				  " inner join (select distinct CollectionCode, CollectionDetails.CollectionDetailType from CollectionDetails  ) cd on ch.CollectionCode=cd.CollectionCode " +
//				  " where CollectionDate > ?   ";
//		 
	 //	Toast.makeText(getApplicationContext(), "date "+date, Toast.LENGTH_LONG).show();
	 	SQL = 	" select ch.ClientCode,ch.CurrencyCode,ch.CollectionCode,ch.TotalAmount ,cd.CollectionDetailType,cd.CheckCode,cd.CheckDate,cd.BankDescription,cd.CollectionAmount,ch.CompanyCode,ch.DivisionCode " +
	 			" 	,ch.CollectionType  from CollectionHeaders ch inner join CollectionDetails cd on cd.CollectionCode  = ch.CollectionCode   " +
	 			" and cd.DivisionCode=ch.DivisionCode where CollectionDate > ? ";
	 	// Compute the selection arguments
			selectionArguments = new String [] {	   
					String.valueOf (date)
			};
			Cursor cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
					        .rawQuery(SQL , selectionArguments) ;
			
				// Move the cursor to the first raw
			if ( cursor.moveToFirst () ) {
				// Iterate over all raws
				do {
					CollectionSummaryPrinting collectionSummaryPrinting = new CollectionSummaryPrinting ();
					collectionSummaryPrinting.setClientCode(  cursor.getString(0) );
					collectionSummaryPrinting.setCurrencyCode(cursor.getString(1)) ;
					collectionSummaryPrinting.setCollectionCode(cursor.getString(2)) ;
					collectionSummaryPrinting.setTotalAmount(cursor.getDouble(3));
					collectionSummaryPrinting.setCollectionDetailType(cursor.getInt(4));
					collectionSummaryPrinting.setCheckCode(cursor.getString(5));
					collectionSummaryPrinting.setCollectionDate(new Date( cursor.getLong(6)));
					collectionSummaryPrinting.setBankName(cursor.getString(7));
					collectionSummaryPrinting.setCollectionAmount(cursor.getDouble(8));
					collectionSummaryPrinting.setCompanyCode(cursor.getString(9));
					collectionSummaryPrinting.setDivisionCode(cursor.getString(10));
					collectionSummaryPrinting.setCollectionType(cursor.getInt(11));
					headers.add ( collectionSummaryPrinting );
				} while ( cursor.moveToNext () );
			} // End if
			// Close the cursor
			cursor.close ();
			cursor = null;
			return headers;
	}
	private ArrayList < SummaryReturnPrinting > getCollectionSummaryReturnPrinting (long date) {
		ArrayList < SummaryReturnPrinting > headers = new ArrayList<SummaryReturnPrinting>();
  
		String SQL=null;
	 	String selectionArguments [] = null;
 		 
	   	SQL = 	" select c.ClientCode,c.ClientName,ClientType,th.TransactionCode," +
	   			" th.TotalTaxAmount,th.CurrencyCode,th.info1 from TransactionHeaders th inner join Clients c " +
	   			" on c.ClientCode=th.ClientCode where th.IssueDate> ? and (th.TransactionType=? or  th.TransactionType=?)and  th.TransactionStatus=? and  th.info4 is null and th.info5 is null "; 
	 	// Compute the selection arguments
			selectionArguments = new String [] {	   
					String.valueOf (date),
					String.valueOf("3"),
					String.valueOf("8"),
					String.valueOf("1")
					
			};
			Cursor cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
					        .rawQuery(SQL , selectionArguments) ;
			
				// Move the cursor to the first raw
			if ( cursor.moveToFirst () ) {
				// Iterate over all raws
				do {
					SummaryReturnPrinting summaryReturnPrinting = new SummaryReturnPrinting ();
					summaryReturnPrinting.setClientCode(  cursor.getString(0) );
					summaryReturnPrinting.setClientName(cursor.getString(1) );
					summaryReturnPrinting.setClientType(cursor.getInt(2));
					summaryReturnPrinting.setTransactionCode(cursor.getString(3));
					summaryReturnPrinting.setTotalTaxAmount(cursor.getDouble(4));
					summaryReturnPrinting.setCurrencyCode(cursor.getString(5)) ;
					summaryReturnPrinting.setInfo1(cursor.getString( 6 ));
					 				 
					headers.add ( summaryReturnPrinting );
				} while ( cursor.moveToNext () );
			} // End if
			// Close the cursor
			cursor.close ();
			cursor = null;
			return headers;
	}
	
	
	private ArrayList < LoadInSummaryPrinting > getLoadINSummaryPrinting (long date) {
	
		Calendar endDate = Calendar.getInstance();
		endDate.setTimeInMillis(date);
		endDate.set(Calendar.HOUR_OF_DAY, 23);
		endDate.set(Calendar.MINUTE, 59);
		endDate.set(Calendar.SECOND, 59);
		endDate.set ( Calendar.MILLISECOND , 0 );
		endDay = endDate.getTimeInMillis ();
     		
		
		
		ArrayList < LoadInSummaryPrinting > headers = new ArrayList<LoadInSummaryPrinting>();
    	String SQL=null;
	 	String selectionArguments [] = null;
 
	 	 SQL =      "  select  distinct  I.itemCode,I.itemName,i.UnitMediumSmall    from MovementDetails md inner join MovementHeaders mh on mh.MovementCode=md.MovementCode " +
		 	  		"  inner join Items i on i.ItemCode=md.ItemCode  where mh.MovementType =?    " +
		 	  		"  and mh.MovementDate > ?  and mh.MovementDate < ?  ";	
		 	          //"  select   md.QuantityMedium,md.QuantitySmall " +
		 		      //"  from MovementDetails md inner join MovementHeaders mh on mh.MovementCode=md.MovementCode" +
		 			  //   "  inner join Items i on i.ItemCode=md.ItemCode  where mh.MovementType =? and i.ItemCode= ? " +
		 			  //  "  and mh.MovementDate > ? LIMIT 1 ";
		 	// Compute the selection arguments
				selectionArguments = new String [] {	   
						String.valueOf("2"),
						 
						String.valueOf (date),
						String.valueOf (endDay)
				};
	 	
	 	
	 	
			Cursor cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
					        .rawQuery(SQL , selectionArguments) ;
			
				// Move the cursor to the first raw
			if ( cursor.moveToFirst () ) {
				// Iterate over all raws
				do {
					LoadInSummaryPrinting loadInSummaryPrinting = new LoadInSummaryPrinting ();
					loadInSummaryPrinting.setItemCode( cursor.getString(0) );
					loadInSummaryPrinting.setItemName( cursor.getString(1) ) ;
					loadInSummaryPrinting.setBaseUnit( cursor.getDouble(2) ) ;
					
					 
					headers.add ( loadInSummaryPrinting );
				} while ( cursor.moveToNext () );
			} // End if
			// Close the cursor
			cursor.close ();
			cursor = null;
			return headers;
	}
	private ArrayList < LoadInSummaryPrinting > getLoadINSummaryPrintings (long date) {
		
		  
		Calendar endDate = Calendar.getInstance();
		endDate.setTimeInMillis(date);
		endDate.set(Calendar.HOUR_OF_DAY, 23);
		endDate.set(Calendar.MINUTE, 59);
		endDate.set(Calendar.SECOND, 59);
		endDate.set ( Calendar.MILLISECOND , 0 );
		endDay = endDate.getTimeInMillis ();
		
		
		 
 
 
	
		
		
		
		ArrayList < LoadInSummaryPrinting > headers = new ArrayList<LoadInSummaryPrinting>();
		headers = getLoadINSummaryPrinting( date );
		for(LoadInSummaryPrinting loadInSummaryPrinting : headers)
		{
			if(loadInSummaryPrinting.getItemCode()==null)
				headers.remove(loadInSummaryPrinting);
		}
		for(LoadInSummaryPrinting loadInSummaryPrinting : headers)
		{
			String SQL=null;
		 	String selectionArguments [] = null;
	 
		 	SQL =       " select  sum(cast (case i.UnitMediumSmall when 1 then 0 else  " +
						" (COALESCE( md.basicUnitQuantity,0)  - (cast(COALESCE( md.basicUnitQuantity,0)as integer) % cast (i.UnitMediumSmall as integer))) /i.UnitMediumSmall end as integer)) as Box ,  " +
						" SUM(  cast(case i.UnitMediumSmall when 1 then COALESCE(md.basicUnitQuantity,0) else  ( cast(COALESCE( md.basicUnitQuantity,0) as integer) % cast (COALESCE(i.UnitMediumSmall,1) as integer)) end as integer ) )as Piece  " +
						" from MovementDetails md inner join MovementHeaders mh on mh.MovementCode=md.MovementCode " +
						" inner join Items i on i.ItemCode=md.ItemCode  where md.ItemCode=? and mh.MovementType =?  " +
						" and mh.MovementDate > ? and mh.MovementDate < ?    and md.MovementCode not in(  select  md.MovementCode " +
						" from MovementDetails md inner join MovementHeaders mh on mh.MovementCode=md.MovementCode " +
						" inner join Items i on i.ItemCode=md.ItemCode  where mh.MovementType =? and i.ItemCode = md.ItemCode   " +
						" and mh.MovementDate > ? and mh.MovementDate < ? and  md.ItemCode=? order by date(MovementDate) asc LIMIT 1)";
		 	//	" select  md.ItemCode,i.ItemName,i.UnitMediumSmall,sum(md.QuantityMedium) as QuantityMedium " +
		 	//" ,sum(md.QuantitySmall) as QuantitySmall " +
		 	//	" from MovementDetails md inner join MovementHeaders mh on mh.MovementCode=md.MovementCode " +
		 	//	" inner join Items i on i.ItemCode = md.ItemCode  where mh.MovementType = ?  and mh.MovementDate > ?" +
		 	//	" group by md.ItemCode,i.ItemName,i.UnitMediumSmall  ";
		 	// Compute the selection arguments
				selectionArguments = new String [] {	
						String.valueOf(loadInSummaryPrinting.getItemCode()),
						String.valueOf("2"),
						String.valueOf (date),
						String.valueOf (endDay),
						String.valueOf("2"),
						String.valueOf (date),
						String.valueOf (endDay),
						String.valueOf(loadInSummaryPrinting.getItemCode())
				};
				Cursor cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
						        .rawQuery(SQL , selectionArguments) ;
				
					// Move the cursor to the first raw
				if ( cursor.moveToFirst () ) {
					// Iterate over all raws
					do {
					
						loadInSummaryPrinting.setQuantityMedium( cursor.getDouble( 0 ) );
						loadInSummaryPrinting.setQuantitySmall( cursor.getDouble( 1 ) );
						 
						 
					} while ( cursor.moveToNext () );
				} // End if
				// Close the cursor
				cursor.close ();
				cursor = null;
			 
			
		 
			
			
			
			 
	     	 
 
	     	
	     	
	     	
	 	  SQL = "  select cast (case i.UnitMediumSmall when 1 then 0 else   " +
	 	  		"  (COALESCE( md.basicUnitQuantity,0)  - (cast(COALESCE( md.basicUnitQuantity,0)as integer) % cast (i.UnitMediumSmall as integer))) /i.UnitMediumSmall end as integer) as Box , " +
	 	  		"  cast(case i.UnitMediumSmall when 1 then COALESCE(md.basicUnitQuantity,0) else  ( cast(COALESCE( md.basicUnitQuantity,0) as integer) % cast (COALESCE(i.UnitMediumSmall,1) as integer)) end as integer ) as Piece " +
	 	  		"  from MovementDetails md inner join MovementHeaders mh on mh.MovementCode=md.MovementCode " +
	 	  		"  inner join Items i on i.ItemCode=md.ItemCode  where mh.MovementCode  in " +
	 	  		"  (select  md.MovementCode " +
						" from MovementDetails md inner join MovementHeaders mh on mh.MovementCode=md.MovementCode " +
						" inner join Items i on i.ItemCode=md.ItemCode  where mh.MovementType =? and i.ItemCode = md.ItemCode   " +
						" and mh.MovementDate > ?  and mh.MovementDate < ? and  md.ItemCode=? order by date(MovementDate) asc LIMIT 1) and " +
	 	  		"  mh.MovementType = ? and i.ItemCode= ?  " +
	 	  		"  and mh.MovementDate > ?  and mh.MovementDate < ? ";	
	 	          //"  select   md.QuantityMedium,md.QuantitySmall " +
	 		      //"  from MovementDetails md inner join MovementHeaders mh on mh.MovementCode=md.MovementCode" +
	 			  //   "  inner join Items i on i.ItemCode=md.ItemCode  where mh.MovementType =? and i.ItemCode= ? " +
	 			  //  "  and mh.MovementDate > ? LIMIT 1 ";
	 	// Compute the selection arguments
			selectionArguments = new String [] {
					String.valueOf("2"),
					String.valueOf (date),
					String.valueOf (endDay),
					String.valueOf(loadInSummaryPrinting.getItemCode()),
					String.valueOf("2"),
					String.valueOf(loadInSummaryPrinting.getItemCode()),
					String.valueOf (date),
					String.valueOf (endDay),
					//String.valueOf(loadInSummaryPrinting.getItemCode())
			};
			 cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
					        .rawQuery(SQL , selectionArguments) ;
			
				// Move the cursor to the first raw
			if ( cursor.moveToFirst () ) {
				// Iterate over all raws
				do {
				 
					  loadInSummaryPrinting.setFirstLoadQuantityMedium( cursor.getDouble(0) );
					  loadInSummaryPrinting.setFirstLoadQuantitySmall( cursor.getDouble(1) );
					 
					 
				} while ( cursor.moveToNext () );
			} // End if
			// Close the cursor
			cursor.close ();
			cursor = null;
			
			
			//Invoice
			SQL =   " select  sum(cast (case t.UnitMediumSmall when 1 then 0 else  " +
					"  (COALESCE( t.basicUnitQuantity,0)  - (cast(COALESCE( t.basicUnitQuantity,0)as integer) % cast (t.UnitMediumSmall as integer))) /t.UnitMediumSmall end as integer)) as Box , " +
					"  SUM(  cast(case t.UnitMediumSmall when 1 then COALESCE(t.basicUnitQuantity,0) else  ( cast(COALESCE( t.basicUnitQuantity,0) as integer) % cast (COALESCE(t.UnitMediumSmall,1) as integer)) end as integer ) )as Piece " +
					"  from( Select  COALESCE(sum(cast ( COALESCE(basicUnitQuantity,0)as integer)),0) as basicUnitQuantity, UnitMediumSmall, 	  TransactionDetails.ItemCode from TransactionDetails  " +
					"  inner join Items on Items.ItemCode = TransactionDetails .ItemCode  " +
					"  inner join TransactionHeaders th on th.TransactionCode=TransactionDetails.TransactionCode " +
					"  where Items.ItemCode = ? and IssueDate > ?  and IssueDate < ? and TransactionType=? and th.TransactionStatus=?  and th.info4 is null and th.info5 is null " +
					"  group by TransactionDetails.ItemCode,UnitMediumSmall ) as t  group by t.ItemCode ";
					
					
					
					//	"   select Sum(td.QuantityMedium),SUM(td.QuantitySmall) from TransactionDetails td " +
					//"inner join TransactionHeaders th on td.TransactionCode=th.TransactionCode " +
					//"where ItemCode = ? and IssueDate > ? and TransactionType=? group by ItemCode";
		 	// Compute the selection arguments
				selectionArguments = new String [] {	   
					
						String.valueOf( loadInSummaryPrinting.getItemCode() ),
						String.valueOf (date),
						String.valueOf (endDay),
						String.valueOf("2"),
						String.valueOf("1"),
						
				};
				 cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
						        .rawQuery(SQL , selectionArguments) ;
				
					// Move the cursor to the first raw
				if ( cursor.moveToFirst () ) {
					// Iterate over all raws
					do {
					 
						  loadInSummaryPrinting.setQuantityMediumSales( cursor.getDouble(0) );
						  loadInSummaryPrinting.setQuantitySmallSales( cursor.getDouble(1) );
						 
						 
					} while ( cursor.moveToNext () );
				} // End if
				// Close the cursor
				cursor.close ();
				cursor = null;
			
				
				//Return
				SQL =   " select  sum(cast (case t.UnitMediumSmall when 1 then 0 else  " +
						"  (COALESCE( t.basicUnitQuantity,0)  - (cast(COALESCE( t.basicUnitQuantity,0)as integer) % cast (t.UnitMediumSmall as integer))) /t.UnitMediumSmall end as integer)) as Box , " +
						"  SUM(  cast(case t.UnitMediumSmall when 1 then COALESCE(t.basicUnitQuantity,0) else  ( cast(COALESCE( t.basicUnitQuantity,0) as integer) % cast (COALESCE(t.UnitMediumSmall,1) as integer)) end as integer ) )as Piece " +
						"  from( Select  COALESCE(sum(cast ( COALESCE(basicUnitQuantity,0)as integer)),0) as basicUnitQuantity, UnitMediumSmall, 	  TransactionDetails.ItemCode from TransactionDetails  " +
						"  inner join Items on Items.ItemCode = TransactionDetails .ItemCode  " +
						"  inner join TransactionHeaders th on th.TransactionCode=TransactionDetails.TransactionCode " +
						"   where Items.ItemCode = ? and IssueDate > ?  and IssueDate < ? and (TransactionType=? or  TransactionType=?)   and th.info4 is null and th.info5 is null  " +
						"   group by TransactionDetails.ItemCode,UnitMediumSmall ) as t  group by t.ItemCode ";
						//	" select Sum(td.QuantityMedium),SUM(td.QuantitySmall) from TransactionDetails td " +
						//  " inner join TransactionHeaders th on td.TransactionCode=th.TransactionCode " +
					    //	" where ItemCode = ? and IssueDate > ? and TransactionType=? group by ItemCode ";
			 	// Compute the selection arguments
					selectionArguments = new String [] {	   
						
							String.valueOf(loadInSummaryPrinting.getItemCode()),
							String.valueOf (date),
							String.valueOf (endDay),
							String.valueOf("3"),
							String.valueOf("8")
					};
					 cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
							        .rawQuery(SQL , selectionArguments) ;
					
						// Move the cursor to the first raw
					if ( cursor.moveToFirst () ) {
						// Iterate over all raws
						do {
						 	  loadInSummaryPrinting.setQuantityMediumReturn( cursor.getDouble(0) );
							  loadInSummaryPrinting.setQuantitySmallReturn( cursor.getDouble(1) );
							} while ( cursor.moveToNext () );
					} // End if
					// Close the cursor
					cursor.close ();
					cursor = null;
					
					
					//Stock Quantity
					SQL = 	" Select  sum(cast ( case UnitMediumSmall when 1 then 0 else " +
							" (COALESCE( VehiclesStock.goodQuantity,0)  - (cast( COALESCE( VehiclesStock.goodQuantity,0)as integer) % " +
							" cast(   UnitMediumSmall as integer))) /UnitMediumSmall end as integer)) as Box  ," +
							" SUM(  cast(case UnitMediumSmall when 1 then COALESCE(VehiclesStock.goodQuantity,0) else" +
							" (cast(COALESCE( VehiclesStock.goodQuantity,0)as integer) %" +
							" cast( COALESCE(UnitMediumSmall,1)as integer)) end as integer ) )as Piece  " +
							" from VehiclesStock    inner join Items on Items.ItemCode = VehiclesStock .ItemCode where items.ItemCode=? " +
							" group by VehiclesStock.ItemCode  ";
				 	// Compute the selection arguments
						selectionArguments = new String [] {	   
							 	String.valueOf(loadInSummaryPrinting.getItemCode()),
						 		};
						 cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
								        .rawQuery(SQL , selectionArguments) ;
						
							// Move the cursor to the first raw
						if ( cursor.moveToFirst () ) {
							// Iterate over all raws
							do {
							 	  loadInSummaryPrinting.setStockQuantityMedium( cursor.getDouble(0) );
								  loadInSummaryPrinting.setStockQuantitySmall( cursor.getDouble(1) );
								} while ( cursor.moveToNext () );
						} // End if
						// Close the cursor
						cursor.close ();
						cursor = null;
					
						
						
						
						
						
						
						
						
						
						
						//Stock Quantity
						SQL = 	" select cast (case i.UnitMediumSmall when 1 then 0 else " +
								" (COALESCE( md.basicUnitQuantity,0)  - (cast(COALESCE( md.basicUnitQuantity,0)as integer) % cast (i.UnitMediumSmall as integer))) /i.UnitMediumSmall end as integer) as Box , " +
								" cast(case i.UnitMediumSmall when 1 then COALESCE(md.basicUnitQuantity,0) else  ( cast(COALESCE( md.basicUnitQuantity,0) as integer) % cast (COALESCE(i.UnitMediumSmall,1) as integer)) end as integer ) as Piece " +
								" from MovementDetails md inner join MovementHeaders mh on mh.MovementCode = md.MovementCode " +
								" inner join Items i on i.ItemCode = md.ItemCode  where " +
								" mh.MovementType = ? and i.ItemCode= ?   and mh.MovementDate > ?   and mh.MovementDate < ? ";
					 	// Compute the selection arguments
							selectionArguments = new String [] {	   
									String.valueOf("3"),
									String.valueOf(loadInSummaryPrinting.getItemCode()),
									String.valueOf (date),
									String.valueOf (endDay)
									
							 		};
							 cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
									        .rawQuery(SQL , selectionArguments) ;
							
								// Move the cursor to the first raw
							if ( cursor.moveToFirst () ) {
								// Iterate over all raws
								do {
									
									loadInSummaryPrinting.setOffLoadQuantityMedium( cursor.getDouble(0) );
									loadInSummaryPrinting.setOffLoadQuantitySmall(cursor.getDouble(1));
							 
									} while ( cursor.moveToNext () );
							} // End if
							// Close the cursor
							cursor.close ();
							cursor = null;
					
			//
		}
		return headers;
	}
	
	
	

	private ArrayList < LoadInSummaryPrinting > getMovementSummaryPrinting (long date) {
		Calendar endDate = Calendar.getInstance();
		endDate.setTimeInMillis(date);
		endDate.set(Calendar.HOUR_OF_DAY, 23);
		endDate.set(Calendar.MINUTE, 59);
		endDate.set(Calendar.SECOND, 59);
		endDate.set ( Calendar.MILLISECOND , 0 );
		endDay = endDate.getTimeInMillis ();
		
		ArrayList < LoadInSummaryPrinting > headers = new ArrayList<LoadInSummaryPrinting>();
    	String SQL=null;
	 	String selectionArguments [] = null;
	 	SQL =" select distinct i.ItemCode ,i.ItemName,i.UnitMediumSmall from ( select v.ItemCode from VehiclesStock v" +
	 		 " union select md.ItemCode from MovementDetails md  )t inner  join Items i on i.ItemCode =t.ItemCode"; 
	 			
	 			
//	 			" select distinct i.ItemCode,i.ItemName,i.UnitMediumSmall " +
//	 		  " from items i where ( i.itemcode in ( select  v.ItemCode from  VehiclesStock v ) or i.ItemCode " +
//	 		  " in ( select md.ItemCode from MovementDetails md ) ) ";
	
	 	
	 	// SQL=	" Select distinct Items.ItemCode ,Items.ItemName,Items.UnitMediumSmall  from ( " +
			// 		" Select coalesce( v.ItemCode,md.ItemCode ) as ItemCode  from 	VehiclesStock v " +
			// 		" left join MovementDetails md on md.ItemCode=v.ItemCode)  t " +
			// 		" inner join Items on Items.ItemCode = t.ItemCode ";
			 
			//	 	 SQL =  " Select distinct VehiclesStock.ItemCode,Items.ItemName,Items.UnitMediumSmall   " +
			//	 	 		" from VehiclesStock inner join Items on Items.ItemCode = VehiclesStock.ItemCode" +
			//	 	 		" left join MovementDetails md on md.ItemCode=VehiclesStock.ItemCode ";	
	 	
	 	
		 	          //"  select   md.QuantityMedium,md.QuantitySmall " +
		 		      //"  from MovementDetails md inner join MovementHeaders mh on mh.MovementCode=md.MovementCode" +
		 			  //   "  inner join Items i on i.ItemCode=md.ItemCode  where mh.MovementType =? and i.ItemCode= ? " +
		 			  //  "  and mh.MovementDate > ? LIMIT 1 ";
		 	// Compute the selection arguments
				selectionArguments = new String [] {	   
						
				};
	 	
	 	
	 	
			Cursor cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
					        .rawQuery(SQL , selectionArguments) ;
			
				// Move the cursor to the first raw
			if ( cursor.moveToFirst () ) {
				// Iterate over all raws
				do {
					LoadInSummaryPrinting loadInSummaryPrinting = new LoadInSummaryPrinting ();
					loadInSummaryPrinting.setItemCode(  cursor.getString(0) );
					loadInSummaryPrinting.setItemName( cursor.getString(1) ) ;
					loadInSummaryPrinting.setBaseUnit( cursor.getDouble(2) ) ;
					
					 
					headers.add ( loadInSummaryPrinting );
				} while ( cursor.moveToNext () );
			} // End if
			// Close the cursor
			cursor.close ();
			cursor = null;
			return headers;
	}
	
	private ArrayList < LoadInSummaryPrinting > getMovementSummaryPrintings (long date) {
		Calendar endDate = Calendar.getInstance();
		endDate.setTimeInMillis(date);
		endDate.set(Calendar.HOUR_OF_DAY, 23);
		endDate.set(Calendar.MINUTE, 59);
		endDate.set(Calendar.SECOND, 59);
		endDate.set ( Calendar.MILLISECOND , 0 );
		endDay = endDate.getTimeInMillis ();
		
		ArrayList < LoadInSummaryPrinting > headers = new ArrayList<LoadInSummaryPrinting>();
		headers = getMovementSummaryPrinting( date );
		for(LoadInSummaryPrinting loadInSummaryPrinting : headers)
		{
			if(loadInSummaryPrinting.getItemCode()==null)
				headers.remove(loadInSummaryPrinting);
		}
		for(LoadInSummaryPrinting loadInSummaryPrinting : headers)
		{
			String SQL=null;
		 	String selectionArguments [] = null;
	 
		 	SQL =       " select  sum(cast (case i.UnitMediumSmall when 1 then 0 else  " +
						" (COALESCE( md.basicUnitQuantity,0)  - (cast(COALESCE( md.basicUnitQuantity,0)as integer) % cast (i.UnitMediumSmall as integer))) /i.UnitMediumSmall end as integer)) as Box ,  " +
						" SUM(  cast(case i.UnitMediumSmall when 1 then COALESCE(md.basicUnitQuantity,0) else  ( cast(COALESCE( md.basicUnitQuantity,0) as integer) % cast (COALESCE(i.UnitMediumSmall,1) as integer)) end as integer ) )as Piece  " +
						" from MovementDetails md inner join MovementHeaders mh on mh.MovementCode=md.MovementCode " +
						" inner join Items i on i.ItemCode=md.ItemCode  where md.ItemCode=? and mh.MovementType =?  " +
						" and mh.MovementDate > ? and mh.MovementDate < ? " ;
//						" and md.MovementCode not in(  select  md.MovementCode " +
//						" from MovementDetails md inner join MovementHeaders mh on mh.MovementCode=md.MovementCode " +
//						" inner join Items i on i.ItemCode=md.ItemCode  where mh.MovementType =? and i.ItemCode = md.ItemCode   " +
//						" and mh.MovementDate > ? and mh.MovementDate < ? and  md.ItemCode=? order by date(MovementDate) asc LIMIT 1)";
		 	//	" select  md.ItemCode,i.ItemName,i.UnitMediumSmall,sum(md.QuantityMedium) as QuantityMedium " +
		 	//" ,sum(md.QuantitySmall) as QuantitySmall " +
		 	//	" from MovementDetails md inner join MovementHeaders mh on mh.MovementCode=md.MovementCode " +
		 	//	" inner join Items i on i.ItemCode = md.ItemCode  where mh.MovementType = ?  and mh.MovementDate > ?" +
		 	//	" group by md.ItemCode,i.ItemName,i.UnitMediumSmall  ";
		 	// Compute the selection arguments
				selectionArguments = new String [] {	
						String.valueOf(loadInSummaryPrinting.getItemCode()),
						String.valueOf("2"),
						String.valueOf (date),
						String.valueOf(endDay),
//						String.valueOf("1"),
//						String.valueOf (date),
//						String.valueOf(endDay),
//						String.valueOf(loadInSummaryPrinting.getItemCode())
				};
				Cursor cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
						        .rawQuery(SQL , selectionArguments) ;
				
					// Move the cursor to the first raw
				if ( cursor.moveToFirst () ) {
					// Iterate over all raws
					do {
					
						loadInSummaryPrinting.setQuantityMedium( cursor.getDouble( 0 ) );
						loadInSummaryPrinting.setQuantitySmall( cursor.getDouble( 1 ) );
						 
						 
					} while ( cursor.moveToNext () );
				} // End if
				// Close the cursor
				cursor.close ();
				cursor = null;
			 
			
		 
			
			
			
			 

			 	  SQL = "  select cast (case i.UnitMediumSmall when 1 then 0 else   " +
			 	  		"  (COALESCE( md.basicUnitQuantity,0)  - (cast(COALESCE( md.basicUnitQuantity,0)as integer) % cast (i.UnitMediumSmall as integer))) /i.UnitMediumSmall end as integer) as Box , " +
			 	  		"  cast(case i.UnitMediumSmall when 1 then COALESCE(md.basicUnitQuantity,0) else  ( cast(COALESCE( md.basicUnitQuantity,0) as integer) % cast (COALESCE(i.UnitMediumSmall,1) as integer)) end as integer ) as Piece " +
			 	  		"  from MovementDetails md inner join MovementHeaders mh on mh.MovementCode=md.MovementCode " +
			 	  		"  inner join Items i on i.ItemCode=md.ItemCode  where  " +
			 	  		"  mh.MovementType = ? and i.ItemCode= ?  " +
			 	  		"  and mh.MovementDate > ? and mh.MovementDate < ?";	
	   	
//	 	  SQL = "  select cast (case i.UnitMediumSmall when 1 then 0 else   " +
//	 	  		"  (COALESCE( md.basicUnitQuantity,0)  - (cast(COALESCE( md.basicUnitQuantity,0)as integer) % cast (i.UnitMediumSmall as integer))) /i.UnitMediumSmall end as integer) as Box , " +
//	 	  		"  cast(case i.UnitMediumSmall when 1 then COALESCE(md.basicUnitQuantity,0) else  ( cast(COALESCE( md.basicUnitQuantity,0) as integer) % cast (COALESCE(i.UnitMediumSmall,1) as integer)) end as integer ) as Piece " +
//	 	  		"  from MovementDetails md inner join MovementHeaders mh on mh.MovementCode=md.MovementCode " +
//	 	  		"  inner join Items i on i.ItemCode=md.ItemCode  where mh.MovementCode  in " +
//	 	  		"  (select  md.MovementCode " +
//						" from MovementDetails md inner join MovementHeaders mh on mh.MovementCode=md.MovementCode " +
//						" inner join Items i on i.ItemCode=md.ItemCode  where mh.MovementType =? and i.ItemCode = md.ItemCode   " +
//						" and mh.MovementDate > ? and mh.MovementDate < ? and  md.ItemCode=? order by date(MovementDate) asc LIMIT 1) and " +
//	 	  		"  mh.MovementType = ? and i.ItemCode= ?  " +
//	 	  		"  and mh.MovementDate > ? and mh.MovementDate < ?";	
	 	  
	 	          //"  select   md.QuantityMedium,md.QuantitySmall " +
	 		      //"  from MovementDetails md inner join MovementHeaders mh on mh.MovementCode=md.MovementCode" +
	 			  //   "  inner join Items i on i.ItemCode=md.ItemCode  where mh.MovementType =? and i.ItemCode= ? " +
	 			  //  "  and mh.MovementDate > ? LIMIT 1 ";
	 	// Compute the selection arguments
			selectionArguments = new String [] {
				 
					String.valueOf("1"),
					String.valueOf(loadInSummaryPrinting.getItemCode()),
					String.valueOf (date),
					String.valueOf(endDay),
					//String.valueOf(loadInSummaryPrinting.getItemCode())
			};
			 cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
					        .rawQuery(SQL , selectionArguments) ;
			
				// Move the cursor to the first raw
			if ( cursor.moveToFirst () ) {
				// Iterate over all raws
				do {
				 
					  loadInSummaryPrinting.setFirstLoadQuantityMedium( cursor.getDouble(0) );
					  loadInSummaryPrinting.setFirstLoadQuantitySmall( cursor.getDouble(1) );
					 
					 
				} while ( cursor.moveToNext () );
			} // End if
			// Close the cursor
			cursor.close ();
			cursor = null;
			
			//Invoice
			SQL =   " select  sum(cast (case t.UnitMediumSmall when 1 then 0 else  " +
					"  (COALESCE( t.basicUnitQuantity,0)  - (cast(COALESCE( t.basicUnitQuantity,0)as integer) % cast (t.UnitMediumSmall as integer))) /t.UnitMediumSmall end as integer)) as Box , " +
					"  SUM(  cast(case t.UnitMediumSmall when 1 then COALESCE(t.basicUnitQuantity,0) else  ( cast(COALESCE( t.basicUnitQuantity,0) as integer) % cast (COALESCE(t.UnitMediumSmall,1) as integer)) end as integer ) )as Piece " +
					"  from( Select  COALESCE(sum(cast ( COALESCE(basicUnitQuantity,0)as integer)),0) as basicUnitQuantity, UnitMediumSmall, 	  TransactionDetails.ItemCode from TransactionDetails  " +
					"  inner join Items on Items.ItemCode = TransactionDetails .ItemCode  " +
					"  inner join TransactionHeaders th on th.TransactionCode=TransactionDetails.TransactionCode " +
					"  where Items.ItemCode = ? and IssueDate > ? and IssueDate < ?  and TransactionType=? and th.TransactionStatus=? and th.info4 is null and th.info5 is null  " +
					"  group by TransactionDetails.ItemCode,UnitMediumSmall ) as t  group by t.ItemCode ";
					
					
					
					//	"   select Sum(td.QuantityMedium),SUM(td.QuantitySmall) from TransactionDetails td " +
					//"inner join TransactionHeaders th on td.TransactionCode=th.TransactionCode " +
					//"where ItemCode = ? and IssueDate > ? and TransactionType=? group by ItemCode";
		 	// Compute the selection arguments
				selectionArguments = new String [] {	   
					
						String.valueOf( loadInSummaryPrinting.getItemCode() ),
						String.valueOf (date),
						String.valueOf (endDay),
						String.valueOf("2"),
						String.valueOf("1"),
						
				};
				 cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
						        .rawQuery(SQL , selectionArguments) ;
				
					// Move the cursor to the first raw
				if ( cursor.moveToFirst () ) {
					// Iterate over all raws
					do {
					 
						  loadInSummaryPrinting.setQuantityMediumSales( cursor.getDouble(0) );
						  loadInSummaryPrinting.setQuantitySmallSales( cursor.getDouble(1) );
						 
						 
					} while ( cursor.moveToNext () );
				} // End if
				// Close the cursor
				cursor.close ();
				cursor = null;
			
				
				//Return
				SQL =   " select  sum(cast (case t.UnitMediumSmall when 1 then 0 else  " +
						"  (COALESCE( t.basicUnitQuantity,0)  - (cast(COALESCE( t.basicUnitQuantity,0)as integer) % cast (t.UnitMediumSmall as integer))) /t.UnitMediumSmall end as integer)) as Box , " +
						"  SUM(  cast(case t.UnitMediumSmall when 1 then COALESCE(t.basicUnitQuantity,0) else  ( cast(COALESCE( t.basicUnitQuantity,0) as integer) % cast (COALESCE(t.UnitMediumSmall,1) as integer)) end as integer ) )as Piece " +
						"  from( Select  COALESCE(sum(cast ( COALESCE(basicUnitQuantity,0)as integer)),0) as basicUnitQuantity, UnitMediumSmall, 	  TransactionDetails.ItemCode from TransactionDetails  " +
						"  inner join Items on Items.ItemCode = TransactionDetails .ItemCode  " +
						"  inner join TransactionHeaders th on th.TransactionCode=TransactionDetails.TransactionCode " +
						"   where Items.ItemCode = ? and IssueDate > ? and IssueDate < ? and (TransactionType=? or  TransactionType=?) and th.info4 is null and th.info5 is null    " +
						"   group by TransactionDetails.ItemCode,UnitMediumSmall ) as t  group by t.ItemCode ";
						//	" select Sum(td.QuantityMedium),SUM(td.QuantitySmall) from TransactionDetails td " +
						//  " inner join TransactionHeaders th on td.TransactionCode=th.TransactionCode " +
					    //	" where ItemCode = ? and IssueDate > ? and TransactionType=? group by ItemCode ";
			 	// Compute the selection arguments
					selectionArguments = new String [] {	   
						
							String.valueOf(loadInSummaryPrinting.getItemCode()),
							String.valueOf (date),
							String.valueOf (endDay),
							String.valueOf("3"),
							String.valueOf("8")
					};
					 cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
							        .rawQuery(SQL , selectionArguments) ;
					
						// Move the cursor to the first raw
					if ( cursor.moveToFirst () ) {
						// Iterate over all raws
						do {
						 	  loadInSummaryPrinting.setQuantityMediumReturn( cursor.getDouble(0) );
							  loadInSummaryPrinting.setQuantitySmallReturn( cursor.getDouble(1) );
							} while ( cursor.moveToNext () );
					} // End if
					// Close the cursor
					cursor.close ();
					cursor = null;
					
					
					//Stock Quantity
					SQL = 	" Select  sum(cast ( case UnitMediumSmall when 1 then 0 else " +
							" (COALESCE( VehiclesStock.goodQuantity,0)  - (cast( COALESCE( VehiclesStock.goodQuantity,0)as integer) % " +
							" cast(   UnitMediumSmall as integer))) /UnitMediumSmall end as integer)) as Box  ," +
							" SUM(  cast(case UnitMediumSmall when 1 then COALESCE(VehiclesStock.goodQuantity,0) else" +
							" (cast(COALESCE( VehiclesStock.goodQuantity,0)as integer) %" +
							" cast( COALESCE(UnitMediumSmall,1)as integer)) end as integer ) )as Piece  " +
							" from VehiclesStock    inner join Items on Items.ItemCode = VehiclesStock .ItemCode where items.ItemCode=? " +
							" group by VehiclesStock.ItemCode  ";
				 	// Compute the selection arguments
						selectionArguments = new String [] {	   
							 	String.valueOf(loadInSummaryPrinting.getItemCode()),
						 		};
						 cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
								        .rawQuery(SQL , selectionArguments) ;
						
							// Move the cursor to the first raw
						if ( cursor.moveToFirst () ) {
							// Iterate over all raws
							do {
//								 loadInSummaryPrinting.setFirstLoadQuantityMedium( cursor.getDouble(0) );
//								  loadInSummaryPrinting.setFirstLoadQuantitySmall( cursor.getDouble(1) );
							 	  loadInSummaryPrinting.setStockQuantityMedium( cursor.getDouble(0) );
								  loadInSummaryPrinting.setStockQuantitySmall( cursor.getDouble(1) );
								} while ( cursor.moveToNext () );
						} // End if
						// Close the cursor
						cursor.close ();
						cursor = null;
					
						
						
						
//						loadInSummaryPrinting.setFirstLoadQuantityMedium(loadInSummaryPrinting.getFirstLoadQuantityMedium()+
//																			loadInSummaryPrinting.getStockQuantityMedium( ));
//						
//						loadInSummaryPrinting.setFirstLoadQuantitySmall(loadInSummaryPrinting.getFirstLoadQuantitySmall()+
//																			loadInSummaryPrinting.getStockQuantitySmall( ));
//						
						
						
						
						//Stock Quantity
						SQL = 	" select cast (case i.UnitMediumSmall when 1 then 0 else " +
								" (COALESCE( md.basicUnitQuantity,0)  - (cast(COALESCE( md.basicUnitQuantity,0)as integer) % cast (i.UnitMediumSmall as integer))) /i.UnitMediumSmall end as integer) as Box , " +
								" cast(case i.UnitMediumSmall when 1 then COALESCE(md.basicUnitQuantity,0) else  ( cast(COALESCE( md.basicUnitQuantity,0) as integer) % cast (COALESCE(i.UnitMediumSmall,1) as integer)) end as integer ) as Piece " +
								" from MovementDetails md inner join MovementHeaders mh on mh.MovementCode = md.MovementCode " +
								" inner join Items i on i.ItemCode = md.ItemCode  where " +
								" mh.MovementType = ? and i.ItemCode= ?   and mh.MovementDate > ? and mh.MovementDate < ? ";
					 	// Compute the selection arguments
							selectionArguments = new String [] {	   
									String.valueOf("3"),
									String.valueOf(loadInSummaryPrinting.getItemCode()),
									String.valueOf (date),
									String.valueOf (endDay)
									
							 		};
							 cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
									        .rawQuery(SQL , selectionArguments) ;
							
								// Move the cursor to the first raw
							if ( cursor.moveToFirst () ) {
								// Iterate over all raws
								do {
									
									loadInSummaryPrinting.setOffLoadQuantityMedium( cursor.getDouble(0) );
									loadInSummaryPrinting.setOffLoadQuantitySmall(cursor.getDouble(1));
							 
									} while ( cursor.moveToNext () );
							} // End if
							// Close the cursor
							cursor.close ();
							cursor = null;
					
			//
		}
		return headers;
	}
	
	
	

	
	
	
	
	
	
	
	
	
	
	private ArrayList < LoadInSummaryPrinting > getSalesReturnSummeryPrinting (long date) {
		Calendar endDate = Calendar.getInstance();
		endDate.setTimeInMillis(date);
		endDate.set(Calendar.HOUR_OF_DAY, 23);
		endDate.set(Calendar.MINUTE, 59);
		endDate.set(Calendar.SECOND, 59);
		endDate.set ( Calendar.MILLISECOND , 0 );
		endDay = endDate.getTimeInMillis ();
		
		ArrayList < LoadInSummaryPrinting > headers = new ArrayList<LoadInSummaryPrinting>();
    	String SQL=null;
	 	String selectionArguments [] = null;
 
		//Return
		SQL =   "select t.ItemCode,t.ItemName,t.ClientCode,t.ClientName, sum(cast (case t.UnitMediumSmall when 1 then 0 else" +
				" (COALESCE( t.basicUnitQuantity,0)  - (cast(COALESCE( t.basicUnitQuantity,0)as integer) % cast (t.UnitMediumSmall as integer))) /t.UnitMediumSmall end as integer)) as Box , " +
				" SUM(  cast(case t.UnitMediumSmall when 1 then COALESCE(t.basicUnitQuantity,0) else  ( cast(COALESCE( t.basicUnitQuantity,0) as integer) % cast (COALESCE(t.UnitMediumSmall,1) as integer)) end as integer ) )as Piece " +
				" ,	t.TotalLineAmount  from(Select c.ClientCode,c.ClientName, Items.ItemName,  COALESCE(sum(cast ( COALESCE(basicUnitQuantity,0)as integer)),0) as basicUnitQuantity, UnitMediumSmall, 	  TransactionDetails.ItemCode,SUM(TotalLineAmount)  as  TotalLineAmount from TransactionDetails " +
				" inner join Items on Items.ItemCode = TransactionDetails.ItemCode " +
				" inner join TransactionHeaders th on th.TransactionCode=TransactionDetails.TransactionCode " +
				" inner join Clients c on c.ClientCode=th.ClientCode " +
				" where    IssueDate > ? and IssueDate < ?  and (TransactionType= ? or  TransactionType= ?) " +
				" group by TransactionDetails.ItemCode,UnitMediumSmall,c.ClientCode,c.ClientName, Items.ItemName " +
				" ) as t  group by t.ItemCode,t.ItemCode,t.ItemName,t.ClientCode,t.ClientName,t.TotalLineAmount";
				//	" select Sum(td.QuantityMedium),SUM(td.QuantitySmall) from TransactionDetails td " +
				//" inner join TransactionHeaders th on td.TransactionCode=th.TransactionCode " +
			//	" where ItemCode = ? and IssueDate > ? and TransactionType=? group by ItemCode ";
	 	// Compute the selection arguments
			selectionArguments = new String [] {	   
				
			 
					String.valueOf (date),
					String.valueOf (endDay),
					String.valueOf("8"),
					String.valueOf("3")
			};
		Cursor	 cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
					        .rawQuery(SQL , selectionArguments) ;
			
				// Move the cursor to the first raw
			if ( cursor.moveToFirst () ) {
				// Iterate over all raws
				do {
					LoadInSummaryPrinting loadInSummaryPrinting = new LoadInSummaryPrinting ();
					loadInSummaryPrinting.setItemCode ( cursor.getString(0) );
					loadInSummaryPrinting.setItemName( cursor.getString(1) );
					loadInSummaryPrinting.setClientCode( cursor.getString(2) );
					loadInSummaryPrinting.setClientName( cursor.getString(3) );
					loadInSummaryPrinting.setQuantityMediumReturn( cursor.getDouble(4) );
					loadInSummaryPrinting.setQuantitySmallReturn( cursor.getDouble(5) );
					loadInSummaryPrinting.setTotalAmount(cursor.getDouble(6));
					headers.add(loadInSummaryPrinting);
					} while ( cursor.moveToNext () );
			} // End if
			// Close the cursor
			cursor.close ();
			cursor = null;
		 
		 
			return headers;
	}
	private ArrayList < LoadInSummaryPrinting > getSalesFreeSummeryPrinting (long date) {
		Calendar endDate = Calendar.getInstance();
		endDate.setTimeInMillis(date);
		endDate.set(Calendar.HOUR_OF_DAY, 23);
		endDate.set(Calendar.MINUTE, 59);
		endDate.set(Calendar.SECOND, 59);
		endDate.set ( Calendar.MILLISECOND , 0 );
		endDay = endDate.getTimeInMillis ();
		
		ArrayList < LoadInSummaryPrinting > headers = new ArrayList<LoadInSummaryPrinting>();
    	String SQL=null;
	 	String selectionArguments [] = null;
 
		 
		SQL =   " select td.ItemCode,i.ItemName,c.ClientCode,c.ClientName,td.QuantityMedium, " +
				" td.QuantitySmall,td.DiscountAmount,td.PriceSmall,th.TransactionCode " +
				" from TransactionDetails td inner join TransactionHeaders th on td.TransactionCode=th.TransactionCode " +
				" inner join Items i on i.ItemCode=td.ItemCode inner join Clients c on c.ClientCode=th.ClientCode " +
				" where td.DiscountPercentage=?   and   IssueDate > ?  and   IssueDate < ? and th.TransactionStatus=? ";
			 
	 	   // Compute the selection arguments
			selectionArguments = new String [] {	   
				    String.valueOf(100),
					String.valueOf (date) ,
					String.valueOf(endDay),
					String.valueOf(1)
					 
			  };
		Cursor	 cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
					        .rawQuery(SQL , selectionArguments) ;
			
				// Move the cursor to the first raw
			if ( cursor.moveToFirst () ) {
				// Iterate over all raws
				do {
					LoadInSummaryPrinting loadInSummaryPrinting = new LoadInSummaryPrinting ();
					loadInSummaryPrinting.setItemCode ( cursor.getString(0) );
					loadInSummaryPrinting.setItemName( cursor.getString(1) );
					loadInSummaryPrinting.setClientCode( cursor.getString(2) );
					loadInSummaryPrinting.setClientName( cursor.getString(3) );
					loadInSummaryPrinting.setQuantityMediumReturn( cursor.getDouble(4) );
					loadInSummaryPrinting.setQuantitySmallReturn( cursor.getDouble(5) );
					//price
					loadInSummaryPrinting.setQuantityMedium(cursor.getDouble(6));
					//price Small
					loadInSummaryPrinting.setQuantitySmall(cursor.getDouble(7));
					loadInSummaryPrinting.setTransactionCode(cursor.getString(8));
					headers.add(loadInSummaryPrinting);
					} while ( cursor.moveToNext () );
			} // End if
			// Close the cursor
			cursor.close ();
			cursor = null;
		 
		 
			return headers;
	}
	private ArrayList < TransactionHeaders > getTransactionHeaders1 (long date) {
		//Toast.makeText(getApplicationContext(), "date "+date, Toast.LENGTH_LONG).show();
		DaoSession daoSession = DatabaseUtils.getInstance ( this ).getDaoSession ();
	ArrayList < TransactionHeaders > invoiceSummary = new ArrayList < TransactionHeaders > ();
	invoiceSummary.addAll ( daoSession.getTransactionHeadersDao ().queryBuilder ().where ( TransactionHeadersDao.Properties.IssueDate.gt ( date ) ,
			 TransactionHeadersDao.Properties.TransactionType.eq ( TransactionHeadersUtils.Type.SALES_ORDER ) ).orderAsc(TransactionHeadersDao.Properties.ClientCode).list () );
	return invoiceSummary;
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
	 * @param view	Reference to the view to hide after the animation ends.
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
	
	
//	/**
//	 * AsyncTask helper class used to populate the sales order items list.
//	 * 
//	 * @author Elias
//	 *
//	 */
//	private class PopulateList extends AsyncTask < Void , String ,String > {
//		
//		/*
//		 * Method to perform a computation on a background thread.
//		 * 
//		 * @see android.os.AsyncTask#doInBackground(Params[])
//		 */
//		@Override
//		protected String doInBackground ( Void ... params ) {
//			 
//
//			
//			return "a" ;
//		 
//		}
//		@Override
//		protected void onPostExecute (String file_url ) {
//		 
//			populateCallMenu();
//           
//           
//         }
//		
//	 
//		
//		
//	}
}