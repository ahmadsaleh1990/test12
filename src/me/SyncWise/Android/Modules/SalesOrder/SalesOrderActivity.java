/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.SalesOrder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import me.SyncWise.Android.ActivityTransition;
import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppDialog.ButtonsType;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.AppToast;
import me.SyncWise.Android.DateTime;
import me.SyncWise.Android.ListViewPosition;
import me.SyncWise.Android.Network;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.ConnectionSettings;
import me.SyncWise.Android.Database.ConnectionSettingsDao;
import me.SyncWise.Android.Database.ConnectionSettingsUtils;
import me.SyncWise.Android.Database.CurrenciesDao;
import me.SyncWise.Android.Database.DaoSession;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Database.PriceLists;
import me.SyncWise.Android.Database.PriceListsDao;
import me.SyncWise.Android.Database.StatusUtils;
import me.SyncWise.Android.Database.TransactionDetails;
import me.SyncWise.Android.Database.TransactionDetailsDao;
import me.SyncWise.Android.Database.TransactionHeaders;
import me.SyncWise.Android.Database.TransactionHeadersDao;
import me.SyncWise.Android.Database.TransactionHeadersUtils;
import me.SyncWise.Android.Database.TransactionSequences;
import me.SyncWise.Android.Database.TransactionSequencesDao;
import me.SyncWise.Android.Database.Users;
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Database.Visits;
import me.SyncWise.Android.Gson.BaseTimerListActivity;
import me.SyncWise.Android.Gson.CommonUtilities;
import me.SyncWise.Android.Modules.Call.CallMenuActivity;
import me.SyncWise.Android.Modules.DataManagement.DataManagementActivity.RequestCode;
import me.SyncWise.Android.Modules.Journey.Call;
import me.SyncWise.Android.Modules.SalesOrder.SalesOrderAdapter.ViewHolder;
import me.SyncWise.Android.Modules.Sync.SyncHelper;
import me.SyncWise.Android.Modules.Sync.SyncListener;
import me.SyncWise.Android.Modules.Sync.SyncUtils;
import me.SyncWise.Android.SectionsList.MultipleListAdapter;
import me.SyncWise.Android.SectionsList.Section;
import me.SyncWise.Android.Widgets.Baguette;
import me.SyncWise.Android.Widgets.QuickAction.ActionItem;
import me.SyncWise.Android.Widgets.QuickAction.QuickAction;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import de.greenrobot.dao.AbstractDao;

/**
 * Activity implemented to view all the performed sales orders.
 * 
 * @author Elias
 * @sw.todo <b>Sales Order Activity Implementation :</b><br>
 * Simply add this class in the {@code AndroidManifest.xml} file.
 *
 */
public class SalesOrderActivity extends BaseTimerListActivity implements QuickAction.OnActionItemClickListener , SyncListener{

	/**
	 * Reference to a {@link me.SyncWise.Android.Widgets.QuickAction.QuickAction QuickAction} used in this activity.<br>
	 * This widget is used for editable sales orders.
	 */
	protected QuickAction quickAction_Editable;
	
	/**
	 * Reference to a {@link me.SyncWise.Android.Widgets.QuickAction.QuickAction QuickAction} used in this activity.<br>
	 * This widget is used for final sales orders.
	 */
	protected QuickAction quickAction_Final;
	
	/**
	 * Helper Class used to manage the fragment's action items IDs for the quick action widgets.<br>
	 * The main purpose of this class is to maintain a unique identifier for action items within the activity.
	 * 
	 * @author Elias
	 *
	 */
	protected class ActionItemID {
		public static final int VIEW = 0;
		public static final int EDIT = 1;
		public static final int DELETE = 2;
	}
	
	/**
	 * Bundle key used to put/retrieve the content of the current call.
	 */
	public static final String CALL = SalesOrderActivity.class.getName () + ".CALL";
	
	/**
	 * Bundle key used to put/retrieve the content of the visit object.
	 */
	public static final String VISIT = SalesOrderActivity.class.getName () + ".VISIT";
	
	/**
	 * Bundle key used to put/retrieve the content of the is view flag.<br>
	 * This flag is used to determine if the user can edit / create sales orders or only view the previous orders.
	 */
	public static final String IS_VIEW = SalesOrderActivity.class.getName () + ".IS_VIEW";
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #listViewPosition}.
	 */
	private static final String LIST_VIEW_POSITION = SalesOrderActivity.class.getName () + ".LIST_VIEW_POSITION";
	
	/**
	 * Object used to save/restore the list views' position.
	 */
	private ListViewPosition listViewPosition;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #saveSuccess}.
	 */
	public static final String SAVE_SUCCESS = SalesOrderActivity.class.getName () + ".SAVE_SUCCESS";
	
	/**
	 * Flag used to indicate if a sales order save was successful.
	 */
	private boolean saveSuccess;
	
	/**
	 * Bundle key used to put/retrieve the content of the successful delete flag.
	 */
	public static final String DELETE_SUCCESS = SalesOrderActivity.class.getName () + ".DELETE_SUCCESS";

	public static final String PRINT = SalesOrderActivity.class.getName () + ".PRINT";
	
	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate ( Bundle savedInstanceState ) {
		// Superclass method call 
		super.onCreate ( savedInstanceState );
		// Set the activity content from a layout resource.
		setContentView ( R.layout.sales_order_activity );
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.sales_order_activity_title ) );
		// Define the empty list view
		getListView ().setEmptyView ( findViewById ( R.id.empty_list_view ) );
		// Display empty list label
		( (TextView) findViewById ( R.id.empty_list_view ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_sales_order_list_label ) );
        // Perform the quick action setup
        setupQuickAction ();

		// Check if both the visit and the call are valid
		if ( getIntent ().getSerializableExtra ( CALL ) == null || getIntent ().getSerializableExtra ( VISIT ) == null ) {
			// Indicate that the activity cannot be displayed
			new AppToast ( this ).setIcon ( R.drawable.warning ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.invalid_selection_label ) ).show ();
			// Finish activity
			finish ();
			// Exit method
			return;
		} // End if
		
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state
			saveSuccess = savedInstanceState.getBoolean ( SAVE_SUCCESS , saveSuccess );
			listViewPosition = (ListViewPosition) savedInstanceState.getSerializable ( LIST_VIEW_POSITION );
		} // End if
		
		// Determine if the list view has a valid adapter
		if ( getListAdapter () == null )
			// Populate the list by setting a new adapter
			new PopulateList ().execute ();
	}
	
	/*
	 * Performs all necessary setup in order to properly display the quick action widget.
	 *
	 * @see me.SyncWise.Android.Modules.Journey.JourneyFragment#setupQuickAction()
	 */
    private void setupQuickAction () {
		// Initialize the quick action widgets
		quickAction_Final = new QuickAction ( this , QuickAction.VERTICAL );
		quickAction_Editable = new QuickAction ( this , QuickAction.VERTICAL );
		// Action Item : View
		ActionItem view = new ActionItem ( ActionItemID.VIEW ,
				AppResources.getInstance ( this ).getString ( this , R.string.view_label ) ,
				getResources ().getDrawable ( R.drawable.quick_action_search ) );
		// Action Item : Edit
		ActionItem edit = new ActionItem ( ActionItemID.EDIT ,
				AppResources.getInstance ( this ).getString ( this , R.string.edit_label ) ,
				getResources ().getDrawable ( R.drawable.quick_action_edit ) );
		// Action Item : Delete
		ActionItem delete = new ActionItem ( ActionItemID.DELETE ,
				AppResources.getInstance ( this ).getString ( this , R.string.quick_action_sales_order_remove_label ) ,
				getResources ().getDrawable ( R.drawable.quick_action_cancel ) );
    	// Populate the quick action widget with quick action items
		quickAction_Final.addActionItem ( view );
		quickAction_Editable.addActionItem ( view );
		quickAction_Editable.addActionItem ( edit );
		quickAction_Editable.addActionItem ( delete );
		// Assign an action item click listener
		quickAction_Final.setOnActionItemClickListener ( this );
		quickAction_Editable.setOnActionItemClickListener ( this );
    }
    
	/*
	 * Called as part of the activity lifecycle when an activity is going into the background, but has not (yet) been killed.
	 *
	 * @see android.app.Activity#onPause()
	 */
	@Override
    protected void onPause () {
		// Superclass method call
		super.onPause ();
		// Remove any displayed baguette
		Baguette.remove ( this );
	}
	
	/*
	 * Called when the activity has detected the user's press of the back key. 
	 *
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed () {
		// Determine if a successful sales order save occurred
		if ( saveSuccess )
	    	// Call this to set the result that your activity will return to its caller
	    	setResult ( RESULT_OK , new Intent ().putExtra ( CallMenuActivity.SKIP_ACTION_RESULT , true ) );
		// Superclass method call
		super.onBackPressed ();
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
			listViewPosition = null;
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
    	// Save the content of searchQuery in the outState bundle
    	outState.putBoolean ( SAVE_SUCCESS , saveSuccess );
    	// Save the list view's position in the outState bundle
    	outState.putSerializable ( LIST_VIEW_POSITION , new ListViewPosition ( getListView () ) );
    }
	
    /*
     * This method will be called when an item in the list is selected.
     *
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
	@Override
    protected void onListItemClick ( ListView listView , View view , final int position , long id ) {
		// Determine if the current sales order is processed or deleted (final)
		// OR if the IS_VIEW flag is set
		if ( ( (ViewHolder) view.getTag () ).isFinal || getIntent ().getBooleanExtra ( IS_VIEW , false ) )
			// The current sales order is final
			// Display the appropriate quick action widget
			quickAction_Final.show ( view , null , getResources () );
		else
			// Otherwise the current sales order is editable
			// Display the appropriate quick action widget
			quickAction_Editable.show ( view , null , getResources () );
	}
    
	/*
	 * Callback method to be invoked when an action item in this QuickAction has been clicked.
	 *
	 * @see me.SyncWise.Android.Widgets.QuickAction.QuickAction.OnActionItemClickListener#onItemClick(me.SyncWise.Android.Widgets.QuickAction.QuickAction, int, int)
	 */
	@Override
	public void onItemClick ( QuickAction source , View anchor , Object object , int pos , int actionId ) {
		// Determine the clicked action item
		switch ( actionId ) {
		case ActionItemID.EDIT:
			// Start the new activity
			startActivityForResult ( getIntent_SalesOrderDetailsActivity_Previous ( ( (ViewHolder) anchor.getTag () ).code , false ) ,
					SalesOrderDetailsActivity.REQUEST_CODE_INFO );
			// Specify an explicit transition animation to perform next
			ActivityTransition.SlideOutLeft ( this );
			break;
		case ActionItemID.VIEW:
			// Start the new activity
			startActivityForResult ( getIntent_SalesOrderDetailsActivity_Previous ( ( (ViewHolder) anchor.getTag () ).code , true ) ,
					SalesOrderDetailsActivity.REQUEST_CODE_INFO );
			// Specify an explicit transition animation to perform next
			ActivityTransition.SlideOutLeft ( this );
			break;
		case ActionItemID.DELETE:
			// Retrieve the transaction code
			final String transactionCode = ( (ViewHolder) anchor.getTag () ).code;
    		// Display delete confirmation
			AppDialog.getInstance ().displayAlert ( this ,
					null ,
					AppResources.getInstance ( this ).getString ( this , R.string.delete_confirmation_message ) ,
					AppDialog.ButtonsType.YesNo ,
					new DialogInterface.OnClickListener () {
						@Override
						public void onClick ( DialogInterface dialog , int which ) {
							// Determine the clicked button
							switch ( which ) {
							case DialogInterface.BUTTON_POSITIVE:
								try {
									// Compute the SQL string
									String SQL = "UPDATE " + TransactionHeadersDao.TABLENAME + " " +
											"SET " + TransactionHeadersDao.Properties.TransactionStatus.columnName + " = ? " +
											"WHERE " + TransactionHeadersDao.Properties.TransactionCode.columnName + " = ? ";
									// Compute the selection arguments
									String selectionArguments [] = new String [] {
											String.valueOf ( StatusUtils.isDeleted () ) , transactionCode
									};
									// Execute the transaction update query
									DatabaseUtils.getInstance ( SalesOrderActivity.this ).getDaoSession ().getDatabase ().execSQL ( SQL , selectionArguments );
									// Display baguette message
									Baguette.showText ( SalesOrderActivity.this , AppResources.getInstance ( SalesOrderActivity.this ).getString ( SalesOrderActivity.this , R.string.sales_order_delete_success_message ) , Baguette.BackgroundColor.GREEN );
									// Clear list adapter
									setListAdapter ( null );
									// Populate the list by setting a new adapter
									new PopulateList ().execute ();
								} catch ( Exception exception ) {
									// Display baguette message
									Baguette.showText ( SalesOrderActivity.this , AppResources.getInstance ( SalesOrderActivity.this ).getString ( SalesOrderActivity.this , R.string.sales_order_delete_failure_message ) , Baguette.BackgroundColor.RED );
								} // End of try-catch block
								break;
							case DialogInterface.BUTTON_NEGATIVE:
								// Dismiss dialog
								AppDialog.getInstance ().dismiss ();
								break;
							} // End switch
						}
					} );
			break;
		} // End of switch
	}
	
	/*
	 * Initialize the contents of the Activity's standard options menu.
	 *
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
    @Override
    public boolean onCreateOptionsMenu ( Menu menu ) {
    	// Use the MenuInflater of this context to inflate a menu hierarchy from the specified XML resource
    	getMenuInflater ().inflate ( R.menu.action_bar , menu );
    	// Check the IS_VIEW flag
    	if ( ! getIntent ().getBooleanExtra ( IS_VIEW , false ) ) {
	    	// Enable the required menu items
	    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_add ) );
	    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_refresh ) );
    	} // End if
		// Display the menu
        return true;
    }
	
    /*
     * This hook is called whenever an item in your options menu is selected.
     *
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected ( MenuItem menuItem ) {
    	// Determine if the action add is selected
    	if ( menuItem.getItemId () == R.id.action_add ) {
    		int hasPrices=0;
    		
    		String SQL1=null;
		 	String selectionArguments1 [] = null;
		 	SQL1=	"select  COUNT(ClientCode) from ClientPriceLists where clientCode = ?" 
		 		 ; 
		 	Call	call1=(Call) getIntent ().getSerializableExtra ( CALL );
		 	selectionArguments1 = new String [] {
					String.valueOf (call1.getClientDivision ().getClientCode () )  

				};
				Cursor cursor1 = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
						        .rawQuery(SQL1 , selectionArguments1) ;
				
		
				
				// Move the cursor to the first raw
				if ( cursor1.moveToFirst () ) {
					// Iterate over all raws
					do {
						 
						hasPrices = cursor1.getInt(0) ;
						 
					 
					} while ( cursor1.moveToNext () );
				} // End if
				// Close the cursor
				cursor1.close ();
				cursor1 = null;
			if(hasPrices==0){
//				Baguette.showText ( SalesOrderActivity.this , 
//			 			 AppResources.getInstance ( SalesOrderActivity.this )
//			 			 .getString ( SalesOrderActivity.this , R.string.sales_order_periode_pricelist_message )
//			 			 , Baguette.BackgroundColor.RED );

				AppDialog.getInstance ().displayAlert (  SalesOrderActivity.this ,
          				null ,
          				AppResources.getInstance ( SalesOrderActivity.this ).getString ( SalesOrderActivity.this , R.string.sales_order_no_pricelist_message ) ,
          				ButtonsType.OK ,
          				new DialogInterface.OnClickListener() {
          				 
          											@Override
          											public void onClick(DialogInterface dialog, int which) {
          												switch (which) {
          												case DialogInterface.BUTTON_NEUTRAL:
          												 
          													break;
          												}
          											}
          											});	 
          		
				
				return false; 
			} 
    		
    		
    		if ( PermissionsUtils.getEnableSalesOrderCashOrCredit ( SalesOrderActivity.this ,   DatabaseUtils.getCurrentUserCode ( SalesOrderActivity.this ) ,  DatabaseUtils.getCurrentCompanyCode(SalesOrderActivity.this) ) ) {
    			ArrayList < String > priceCodes = new ArrayList < String > ();
    			
    			String SQL=null;
    		 	String selectionArguments [] = null;
    		 	SQL=	"select PriceListCode from ClientPriceLists where clientCode = ? "+
    		 			" union select PriceListCode from UserSpecialPriceLists "; 
    		 	Call	call=(Call) getIntent ().getSerializableExtra ( CALL );
    		 	selectionArguments = new String [] {
    					String.valueOf (call.getClientDivision ().getClientCode () )  

    				};
    				Cursor cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
    						        .rawQuery(SQL , selectionArguments) ;
    				
    		
    				
    				// Move the cursor to the first raw
    				if ( cursor.moveToFirst () ) {
    					// Iterate over all raws
    					do {
    						 
    						String pricecode =cursor.getString(0);
    						 
    						priceCodes.add ( pricecode );
    					} while ( cursor.moveToNext () );
    				} // End if
    				// Close the cursor
    				cursor.close ();
    				cursor = null;

    		 	
    		 	
    			 List < PriceLists > priceLists = new ArrayList < PriceLists > ();
    			 if ( PermissionsUtils.getEnableSalesOrderCashOrCreditClientPriceList ( SalesOrderActivity.this ,   DatabaseUtils.getCurrentUserCode ( SalesOrderActivity.this ) ,  DatabaseUtils.getCurrentCompanyCode(SalesOrderActivity.this) ) ) {
    		    		
    			 priceLists =  DatabaseUtils.getInstance ( this ).getDaoSession ().getPriceListsDao().queryBuilder ()
    	        		 .where ( PriceListsDao.Properties.PriceListSource.eq ( 2 )
    	        				 ,PriceListsDao.Properties.PriceListCode.in( priceCodes )
    	        				 )
    				.list ();
    			 }
    			 else{
    				 priceLists =  DatabaseUtils.getInstance ( this ).getDaoSession ().getPriceListsDao().queryBuilder ()
        	        		 .where ( PriceListsDao.Properties.PriceListSource.eq ( 2 )
        	        				 
        	        				 )
        				.list ();
    			 }
    	         if(priceLists.size()==0)
    	         {
//    	        	 Baguette.showText ( SalesOrderActivity.this , 
//    	        			 AppResources.getInstance ( SalesOrderActivity.this )
//    	        			 .getString ( SalesOrderActivity.this , R.string.sales_order_periode_pricelist_message )
//    	        			 , Baguette.BackgroundColor.RED );
    	        	 startNewSalesOrder (null);
    	        	 
    	         }
    	         else
    			 displayalertWarning ();
    		}
    		else
    		// Start a new sales order
    		startNewSalesOrder (null);
    		// Consume event
    		return true;
    	} // End if
    	// Determine if the action refresh is selected
    	else if ( menuItem.getItemId () == R.id.action_refresh ) {
    		// Import approved returns
    		AppDialog.getInstance ().displayIndeterminateProgress ( SalesOrderActivity.this , null , getString ( R.string.connecting_label ) );
 
        	 ConnectionSettings connection = DatabaseUtils.getInstance ( SalesOrderActivity.this ).getDaoSession ().getConnectionSettingsDao ().queryBuilder ()
		        		.where ( ConnectionSettingsDao.Properties.ConnectionSettingType.eq ( ConnectionSettingsUtils.Type.GPRS ) ).unique ();
		        // Check if the connection is valid
		        if ( connection != null ) {
        	DownloadTextTask d = new DownloadTextTask(SalesOrderActivity.this);
		
			
		 
		    d.execute( ConnectionSettingsUtils.getSetMethodPath(connection.getConnectionSettingURL()) ) ;

		        }
   
    		// Consume event
    		return true;
    	} // End if
    	// Allow normal menu processing to proceed
    	return false;
    }
	/**
	 * List used to host the retrieved approved {@link me.SyncWise.Android.Database.TransactionHeaders TransactionHeaders}.
	 */
	private static List < TransactionHeaders > approvedTransactionHeaders;
	
	/**
	 * List used to host the retrieved approved {@link me.SyncWise.Android.Database.TransactionDetails TransactionDetails}.
	 */
	private static List < TransactionDetails > approvedTransactionDetails;
    private void importRejectesOrders () {
		// Determine if the network is available
		if ( Network.networkAvailability ( this ) ) {
	        // Search for the GPRS link
	        ConnectionSettings connection = DatabaseUtils.getInstance ( this ).getDaoSession ().getConnectionSettingsDao ().queryBuilder ()
	        		.where ( ConnectionSettingsDao.Properties.ConnectionSettingType.eq ( ConnectionSettingsUtils.Type.GPRS ) ).unique ();
	        // Check if the connection is valid
	        if ( connection != null ) {
	        	// Valid connection
	        	// Clear lists
	        	approvedTransactionHeaders = null;
	        	approvedTransactionDetails = null;
				// Retrieve the dao table data
				SyncHelper syncHelper = new SyncHelper ( ConnectionSettingsUtils.getConnectMethodPath ( connection.getConnectionSettingURL () ) );
				syncHelper.get ( this , DatabaseUtils.getInstance ( this ).getDaoSession ().getUsersDao () , new TypeToken < List < Users > > () {}.getType () , DatabaseUtils.getCurrentUserCode ( this ) , DatabaseUtils.getCurrentCompanyCode ( this ) , this , RequestCode.PING );
				// Display indeterminate progress
				AppDialog.getInstance ().displayIndeterminateProgress ( this , null , getString ( R.string.connecting_label ) );
	        } // End if
	        else
				// Display alert dialog
				AppDialog.getInstance ().displayAlert ( this ,
						null ,
						getString ( R.string.no_network_connection_message ) ,
						AppDialog.ButtonsType.OK , null );
		} // End if
    }
    
    private void chooseCountType () {
    	  AlertDialog dialog; 
    	     
    	  
    	  ArrayList < String > priceCodes = new ArrayList < String > ();
    	  String SQL=null;
		 	String selectionArguments [] = null;
		 	SQL=	" select PriceListCode from ClientPriceLists where clientCode = ? "+
		 			" union select PriceListCode from UserSpecialPriceLists "; 
		 	Call	call=(Call) getIntent ().getSerializableExtra ( CALL );
		 	selectionArguments = new String [] {
					String.valueOf (call.getClientDivision ().getClientCode () )  

				};
				Cursor cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
						        .rawQuery(SQL , selectionArguments) ;
				
		
				
				// Move the cursor to the first raw
				if ( cursor.moveToFirst () ) {
					// Iterate over all raws
					do {
						 
						String pricecode =cursor.getString(0);
						 
						priceCodes.add ( pricecode );
					} while ( cursor.moveToNext () );
				} // End if
				// Close the cursor
				cursor.close ();
				cursor = null;

    	  
    	  
    	  
         // arraylist to keep the selected items
         final List < String > seletedItems = new ArrayList < String >();
         List < PriceLists > priceLists = new ArrayList < PriceLists > ();
         if ( PermissionsUtils.getEnableSalesOrderCashOrCreditClientPriceList ( SalesOrderActivity.this ,   DatabaseUtils.getCurrentUserCode ( SalesOrderActivity.this ) ,  DatabaseUtils.getCurrentCompanyCode(SalesOrderActivity.this) ) ) {
	    		
			 priceLists =  DatabaseUtils.getInstance ( this ).getDaoSession ().getPriceListsDao().queryBuilder ()
	        		 .where ( PriceListsDao.Properties.PriceListSource.eq ( 2 )
	        				 ,PriceListsDao.Properties.PriceListCode.in( priceCodes )
	        				 )
				.list ();
			 }
			 else{
				 priceLists =  DatabaseUtils.getInstance ( this ).getDaoSession ().getPriceListsDao().queryBuilder ()
    	        		 .where ( PriceListsDao.Properties.PriceListSource.eq ( 2 )
    	        				 
    	        				 )
    				.list ();
			 }
         if(priceLists.size()==0)
         {
//        	 Baguette.showText ( SalesOrderActivity.this , 
//        			 AppResources.getInstance ( SalesOrderActivity.this )
//        			 .getString ( SalesOrderActivity.this , R.string.sales_order_periode_pricelist_message )
//        			 , Baguette.BackgroundColor.RED );
        	 startNewSalesOrder (null);
        	 return;
         }
         final String[] items =  new String [priceLists.size()] ;
         int i=0;
         for(PriceLists p:priceLists)
         {
        	items[i] = p.getPriceListCode();
        	i= i+1;
         }
         AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setTitle("Select The PriceList");
         builder.setMultiChoiceItems(items, null,
                 new DialogInterface.OnMultiChoiceClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int indexSelected,
                  boolean isChecked) {
              if (isChecked) {
                  // If the user checked the item, add it to the selected items
                  seletedItems.add(items[indexSelected]);
              } else if (seletedItems.contains(items[indexSelected])) {
                  // Else, if the item is already in the array, remove it
                  seletedItems.remove(items[indexSelected]);
              }
          }
      })
       // Set the action buttons
      .setPositiveButton("OK", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int id) {
        	  if(seletedItems.isEmpty())
        	  { 
        		// Display warning
          		AppDialog.getInstance ().displayAlert (  SalesOrderActivity.this ,
          				null ,
          				AppResources.getInstance ( SalesOrderActivity.this ).getString ( SalesOrderActivity.this , R.string.sales_order_select_pricelist_message ) ,
          				ButtonsType.OK ,
          				new DialogInterface.OnClickListener() {
          				 
          											@Override
          											public void onClick(DialogInterface dialog, int which) {
          												switch (which) {
          												case DialogInterface.BUTTON_NEUTRAL:
          													chooseCountType()	;
          													break;
          												}
          											}
          											});	 
          		
          		
        		 // Baguette.showText ( SalesOrderActivity.this , AppResources.getInstance ( SalesOrderActivity.this ).getString ( SalesOrderActivity.this , R.string.sales_order_select_pricelist_message ) , Baguette.BackgroundColor.RED );
        	    //   chooseCountType();
        	 
        	  }else
        		  if(seletedItems.size()>1)
            	  { 
        			  AppDialog.getInstance ().displayAlert (  SalesOrderActivity.this ,
                				null ,
                				AppResources.getInstance ( SalesOrderActivity.this ).getString ( SalesOrderActivity.this , R.string.sales_order_select_one_pricelist_message ) ,
                				ButtonsType.OK ,
                				new DialogInterface.OnClickListener() {
           				 
							@Override
							public void onClick(DialogInterface dialog, int which) {
								switch (which) {
								case DialogInterface.BUTTON_NEUTRAL:
									chooseCountType()	;
									break;
								}
							}
							});	                	 
                		
        			  //  Baguette.showText ( SalesOrderActivity.this , AppResources.getInstance ( SalesOrderActivity.this ).getString ( SalesOrderActivity.this , R.string.sales_order_select_one_pricelist_message ) , Baguette.BackgroundColor.RED );
            	 //      chooseCountType();
            	 }
        		  else
        	     startNewSalesOrder(seletedItems);

          }
      })
      .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int id) {
             //  Your code when user clicked on Cancel

          }
      });

         dialog = builder.create();//AlertDialog dialog; create like this outside onClick
         dialog.show();
 }
    
    
    /**
   	 * Displays a warning regarding unpaid invoices for cash client.<br>
   	 * The user has the option to use a pass code.
   	 */
   	private void displayalertWarning () {
   		// Declare and initialize an alert dialog builder
   		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder ( this );
   		// By default, the alert dialog cannot be cancelled UNLESS it has no buttons
   		alertDialogBuilder.setCancelable ( false );
   		// Set the title
   		alertDialogBuilder.setTitle ( R.string.warning_label );
   		// Set the description
   		alertDialogBuilder.setMessage ( R.string.cannot_start_sales_order_client_message );
   		// Map the positive and negative buttons
   		alertDialogBuilder.setPositiveButton ( R.string.normal_price , new DialogInterface.OnClickListener() {
   			@Override
   			public void onClick ( DialogInterface dialog , int which ) {
   	    		 // Start the new activity
   				startNewSalesOrder (null);
   			}	
   			} );
   		alertDialogBuilder.setNegativeButton ( R.string.special_price , new DialogInterface.OnClickListener() {
   			@Override
   			public void onClick ( DialogInterface dialog , int which ) {
   				chooseCountType () ;
   				 
   			}
   		} );
   		// Create and show the alert dialog
   		alertDialogBuilder.create ().show ();
   	}
   	
    /**
     * Starts the {@link me.SyncWise.Android.Modules.SalesOrder.SalesOrderDetailsActivity SalesOrderDetailsActivity} activity in order to create a new sales order.
     */
    protected void startNewSalesOrder (List < String > selectedPrice) {
    	
		// Start the new activity
		startActivityForResult ( getIntent_SalesOrderDetailsActivity_New (selectedPrice) , SalesOrderDetailsActivity.REQUEST_CODE_NEW );
		// Specify an explicit transition animation to perform next
		ActivityTransition.SlideOutLeft ( this );
    }
    
    /**
     * Gets an intent for the sales order details activity for a new sales order.<br>
     * The default implementation uses {@link me.SyncWise.Android.Modules.SalesOrder.SalesOrderDetailsActivity SalesOrderDetailsActivity}.<br>
     * In order to used a custom adapter, override this method.<br>
	 * <b>Note : </b> This method should not return {@code NULL}.
     * 
     * @return	Intent used to start the sales order details activity.
     */
    protected Intent getIntent_SalesOrderDetailsActivity_New (List < String > selectedPrice ) {
		// Create a new intent to start a new activity
		Intent intent = new Intent ( this , SalesOrderDetailsActivity.class );
		// Add the call object to the intent
		intent.putExtra ( SalesOrderDetailsActivity.CALL , getIntent ().getSerializableExtra ( CALL ) );
		// Add the visit object to the intent
		intent.putExtra ( SalesOrderDetailsActivity.VISIT , getIntent ().getSerializableExtra ( VISIT ) );
		// Add the request code to the intent
		intent.putExtra ( SalesOrderDetailsActivity.REQUEST_CODE , SalesOrderDetailsActivity.REQUEST_CODE_NEW );
		if(selectedPrice != null){
			 String  selItemArray = new String ();
			  
		         for(String p:selectedPrice)
		         {
		        	 selItemArray =selItemArray+p+",";
		        	 
		         }
		         selItemArray = selItemArray.substring(0, selItemArray.length()-1);
			// Add the request code to the intent
			 intent.putExtra ( SalesOrderDetailsActivity.PriceList , selItemArray );
		}
		// Return the computed intent
		return intent;
    }
    
    /**
     * Gets an intent for the sales order details activity for a previous (edit or view) sales order.<br>
     * The default implementation uses {@link me.SyncWise.Android.Modules.SalesOrder.SalesOrderDetailsActivity SalesOrderDetailsActivity}.<br>
     * In order to used a custom adapter, override this method.<br>
	 * <b>Note : </b> This method should not return {@code NULL}.
     * 
     * @param transactionHeaderCode	String hosting the transaction header code.
     * @param isView	Boolean used to indicate if the previous transaction will be viewed or edited.
     * @return	Intent used to start the sales order details activity.
     */
    protected Intent getIntent_SalesOrderDetailsActivity_Previous ( final String transactionHeaderCode , final boolean isView ) {
		// Create a new intent to start a new activity
		Intent intent = new Intent ( this , SalesOrderDetailsActivity.class );
		// Add the call object to the intent
		intent.putExtra ( SalesOrderDetailsActivity.CALL , getIntent ().getSerializableExtra ( CALL ) );
		// Add the visit object to the intent
		intent.putExtra ( SalesOrderDetailsActivity.VISIT , getIntent ().getSerializableExtra ( VISIT ) );
		// Add the request code to the intent
		intent.putExtra ( SalesOrderDetailsActivity.REQUEST_CODE , SalesOrderDetailsActivity.REQUEST_CODE_INFO );
		// Add the transaction header code to the intent
		intent.putExtra ( SalesOrderDetailsActivity.TRANSACTION_HEADER_CODE , transactionHeaderCode );
		// Check if the is view flag is set
		if ( isView )
			// Add the view only flag to the intent
			intent.putExtra ( SalesOrderDetailsActivity.IS_VIEW_ONLY , true );
		else
			// Add the edit flag to the intent
			intent.putExtra ( SalesOrderDetailsActivity.IS_EDIT , true );
		// Return the computed intent
		return intent;
    }
    
	/*
	 * Called when an activity you launched exits, giving you the requestCode you started it with, the resultCode it returned, and any additional data from it.
	 *
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	protected void onActivityResult ( int requestCode , int resultCode , final Intent data ) {
    	// Check if the result is successful and the intent is valid
    	if ( resultCode != RESULT_OK || data == null )
    		// Exit method
    		return;

    	// Check if the sales order has been successfully deleted
    	if ( data.getBooleanExtra ( DELETE_SUCCESS , false ) ) {
			// Display baguette message
			Baguette.showText ( this , AppResources.getInstance ( this ).getString ( this , R.string.sales_order_delete_success_message ) , Baguette.BackgroundColor.GREEN );
			// Clear list adapter
			setListAdapter ( null );
			// Populate the list by setting a new adapter
			new PopulateList ().execute ();
			// Exit method
			return;
    	} // End if
    	if(data.getStringExtra(PRINT) != null){ // TODO
//    		AppDialog.getInstance().displayAlert(SalesOrderActivity.this, "Sales Order",
//					"Do You Want to print the order?",
//					AppDialog.ButtonsType.YesNo,
//					new DialogInterface.OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							switch (which) {
//							case DialogInterface.BUTTON_POSITIVE:
//								Intent intent = new Intent( SalesOrderActivity.this, PrintingActivity.class);
//								intent.putExtra(PrintingActivity.TYPE, PrintingActivity.PType.ORDER);
//								intent.putExtra(PrintingActivity.HEADER, new ArrayList < TransactionHeaders > ().add ( DatabaseUtils.getInstance(SalesOrderActivity.this).getDaoSession()
//										.getTransactionHeadersDao().queryBuilder().where(TransactionHeadersDao.Properties.TransactionCode.eq(data.getStringExtra(PRINT))).unique()));
//										startActivityForResult ( intent, 0);
//								break;
//							}
//						}
//						});
    	}
    	
    	// Determine the provided request code
    	switch ( requestCode ) {
		case SalesOrderDetailsActivity.REQUEST_CODE_NEW:
			// Set flag
			saveSuccess = saveSuccess || data.getBooleanExtra ( SAVE_SUCCESS , false );
		case SalesOrderDetailsActivity.REQUEST_CODE_INFO:
			// Display baguette message
			Baguette.showText ( this ,
					AppResources.getInstance ( this ).getString ( this ,
							data.getBooleanExtra ( SAVE_SUCCESS , false ) ? R.string.sales_order_save_success_message : R.string.sales_order_save_failure_message ) ,
							data.getBooleanExtra ( SAVE_SUCCESS , false ) ? Baguette.BackgroundColor.GREEN : Baguette.BackgroundColor.RED );
			// Clear list adapter
			setListAdapter ( null );
			
			// Populate the list by setting a new adapter
			new PopulateList ().execute ();

			break;
		} // End switch
    	
	}

	/**
	 * Populates the sales order list by generating a map of cursors holding sales orders, that belongs for a specific day, for the specified user.
	 * 
	 * @param context	The application context.
	 * @param call	The call object for which the sales orders belong to.
	 * @return	Cursors holding sales orders data set mapped to their appropriate dates.
	 */
	public static Map < String , Cursor > populateList ( final Context context , final Call call ) {
		// Compute the selection clause
		String selection = TransactionHeadersDao.Properties.ClientCode.columnName + " = ? AND " + TransactionHeadersDao.Properties.CompanyCode.columnName + " = ? AND " + TransactionHeadersDao.Properties.DivisionCode.columnName + " = ? AND " 
				+ TransactionHeadersDao.Properties.TransactionType.columnName + " = ? AND "
				+ TransactionHeadersDao.Properties.TransactionStatus.columnName + " = ?";
		// Compute the selection arguments
		String selectionArgs [] = new String [] {
				call.getClientDivision ().getClientCode () , call.getClientDivision ().getCompanyCode () , call.getClientDivision ().getDivisionCode () ,
				String.valueOf ( TransactionHeadersUtils.Type.SALES_ORDER ) ,
				String.valueOf ( StatusUtils.isAvailable () )
		};
		// Compute the order by clause
		String orderBy = TransactionHeadersDao.Properties.IssueDate.columnName;
		// Declare and initialize a cursor in order to query DB
		Cursor cursor = DatabaseUtils.getInstance ( context ).getDaoSession ().getDatabase ()
				.query ( TransactionHeadersDao.TABLENAME , new String [] { TransactionHeadersDao.Properties.IssueDate.columnName } , selection , selectionArgs , null , null , orderBy );
		
		// Declare and initialize a list of calendars, used to compute the sales orders dates
		List < Calendar > dates = new ArrayList < Calendar > ();
		// Move the cursor to the first raw
		if ( cursor.moveToFirst () ) {
			// Iterate over all raws
			do {
				// Compute the current transaction header date
				Calendar transactionDate = Calendar.getInstance ();
				transactionDate.setTimeInMillis ( cursor.getLong ( cursor.getColumnIndex ( TransactionHeadersDao.Properties.IssueDate.columnName ) ) );
				transactionDate.set ( Calendar.HOUR_OF_DAY , 0 );
				transactionDate.set ( Calendar.MINUTE , 0 );
				transactionDate.set ( Calendar.SECOND , 0 );
				transactionDate.set ( Calendar.MILLISECOND , 0 );
				// Check if the list of dates is empty
				if ( dates.isEmpty () )
					// Add the current transaction header date
					dates.add ( transactionDate );
				// Otherwise the list of dates is NOT empty
				// Check if the last date is in the same day (the cursor is ordered by date)
				else if ( dates.get ( dates.size () - 1 ).get ( Calendar.DAY_OF_YEAR ) == transactionDate.get ( Calendar.DAY_OF_YEAR )
						&& dates.get ( dates.size () - 1 ).get ( Calendar.YEAR ) == transactionDate.get ( Calendar.YEAR ) ) {
					// They are both the same day, do nothing
				} // End else if
				// Otherwise they are not the same day
				else
					// Add the current transaction header date
					dates.add ( transactionDate );
			} while ( cursor.moveToNext () );
		} // End if
		// Close the cursor
		cursor.close ();
		cursor = null;
		
		// Adjust the selection clause
		selection += " AND " + TransactionHeadersDao.Properties.IssueDate.columnName + " BETWEEN ? AND ?";
		// Adjust the selection arguments
		selectionArgs = new String [] {
				selectionArgs [ 0 ] ,
				selectionArgs [ 1 ] ,
				selectionArgs [ 2 ] ,
				selectionArgs [ 3 ] ,
				selectionArgs [ 4 ] ,
				null ,
				null ,
		};
		// Compute the SQL string
		String sql = "SELECT T."
				+ TransactionHeadersDao.Properties.Id.columnName + ","
				+ TransactionHeadersDao.Properties.TransactionCode.columnName + ","
				+ TransactionHeadersDao.Properties.VisitID.columnName + ","
				+ TransactionHeadersDao.Properties.IssueDate.columnName + ","
				+ TransactionHeadersDao.Properties.DeliveryDate.columnName + ","
				+ TransactionHeadersDao.Properties.TotalTaxAmount.columnName + ","
				+ TransactionHeadersDao.Properties.IsProcessed.columnName + ","
				+ TransactionHeadersDao.Properties.TransactionStatus.columnName + ","
				+ CurrenciesDao.Properties.CurrencySymbol.columnName + ","
				+ " CASE WHEN " + CurrenciesDao.Properties.CurrencyRounding.columnName + " IS NULL THEN 0 ELSE " + CurrenciesDao.Properties.CurrencyRounding.columnName + " END " + CurrenciesDao.Properties.CurrencyRounding.columnName
				+ " FROM " + TransactionHeadersDao.TABLENAME + " T LEFT JOIN " + CurrenciesDao.TABLENAME + " C"
				+ " ON T." + TransactionHeadersDao.Properties.CurrencyCode.columnName + "=C." + CurrenciesDao.Properties.CurrencyCode.columnName
				+ " WHERE " + selection
				+ " ORDER BY " + orderBy;
		
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
			selectionArgs [ 5 ] = String.valueOf ( start.getTimeInMillis () );
			selectionArgs [ 6 ] = String.valueOf ( end.getTimeInMillis () );
			// Map a new cursor to its date
			cursors.put ( DateTime.getFullDate ( context , start ) ,
					DatabaseUtils.getInstance ( context ).getDaoSession ().getDatabase ()
						.rawQuery ( sql , selectionArgs ) );
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
	private class PopulateList extends AsyncTask < Void , Void , Map < String , Cursor > > {
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Map < String , Cursor > doInBackground ( Void ... params ) {
			// Populate the list using the current client
			return populateList ( SalesOrderActivity.this , (Call) getIntent ().getSerializableExtra ( CALL ) );
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( Map < String , Cursor > cursors ) {
			// Compute the total number of sales orders
			int totalSales = 0;
			// Iterate over all cursors
			for ( String date : cursors.keySet () )
				// Add the number of sales orders
				totalSales += cursors.get ( date ).getCount ();
			// Set the action bar's subtitle
			if ( getActionBar () != null )
				getActionBar ().setSubtitle ( totalSales == 0 ? "" : AppResources.getInstance ( SalesOrderActivity.this ).getString ( SalesOrderActivity.this , R.string.total_of_label ) + " : " + totalSales );
			// Declare and initialize a multiple list adapter
			MultipleListAdapter adapter = new MultipleListAdapter ( SalesOrderActivity.this );
			// Iterate over all cursors
			for ( String date : cursors.keySet () )
				// Add a new sales order adapter using the current cursor
				adapter.addSection ( new Section ( date , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
						new SalesOrderAdapter ( SalesOrderActivity.this , cursors.get ( date ) , R.layout.sales_order_activity_item ) );
			// Set the list adapter
			setListAdapter ( adapter );
			// Restore the list view position (if any)
			if ( listViewPosition != null ) {
				listViewPosition.restore ( getListView () );
				listViewPosition = null;
			} // End if
		}
		
	}
	/*
	 * Call back method executed before determining if the sync result is a success or a failure.
	 *
	 * @see me.SyncWise.Android.Modules.Sync.SyncListener#onPreFinish(int)
	 */
	@Override
	public void onPreFinish ( int requestCode ) {
		// Do nothing.
	}

	@Override
	public void onGetSuccess ( AbstractDao < ? , ? > dao , ArrayList < Object > entities , int requestCode ) {
        // Search for the GPRS link
        ConnectionSettings connection = DatabaseUtils.getInstance ( this ).getDaoSession ().getConnectionSettingsDao ().queryBuilder ()
        		.where ( ConnectionSettingsDao.Properties.ConnectionSettingType.eq ( ConnectionSettingsUtils.Type.GPRS ) ).unique ();
		// Retrieve the user code
		String userCode = DatabaseUtils.getCurrentUserCode ( this );
		// Retrieve the company code
		String companyCode = DatabaseUtils.getCurrentCompanyCode ( this );
		// Determine if this is a ping to the server
		if ( requestCode == RequestCode.PING ) {
			// Retrieve the dao table data
			SyncHelper syncHelper = new SyncHelper ( ConnectionSettingsUtils.getGetMethodPath ( connection.getConnectionSettingURL () ) );
			syncHelper.get ( this , DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionHeadersDao () , // WarehouseQuantities table
					new TypeToken < List < TransactionHeaders > > () {}.getType () , // Expected Type
					userCode , // UserCode
					companyCode , // CompanyCode
					this , // SyncListener
					RequestCode.ON_GOING , // Sync Request Code
					true ); // Execute in parallel
		} // End if
		// Determine the sync is on going
		else if ( requestCode == RequestCode.ON_GOING ) {
			// Determine if the retrieved data are transaction headers
			if ( dao.getTablename ().equals ( TransactionHeadersDao.TABLENAME ) ) {
				// Store the list
				approvedTransactionHeaders = new ArrayList < TransactionHeaders > ();
				for ( Object entity : entities )
					approvedTransactionHeaders.add ( (TransactionHeaders) entity );
				// Check if there is at least one return
				if ( ! entities.isEmpty () ) {
					// Retrieve the dao table data
					SyncHelper syncHelper = new SyncHelper ( ConnectionSettingsUtils.getGetMethodPath ( connection.getConnectionSettingURL () ) );
					syncHelper.get ( this , DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsDao () , // WarehouseQuantities table
							new TypeToken < List < TransactionDetails > > () {}.getType () , // Expected Type
							userCode , // UserCode
							companyCode , // CompanyCode
							this , // SyncListener
							RequestCode.ON_GOING , // Sync Request Code
							true ); // Execute in parallel
				} // End if
				else {
					// Dismiss any displayed dialogs (to avoid activity context leak)
					AppDialog.getInstance ().dismiss ();
					approvedTransactionDetails = new ArrayList < TransactionDetails > ();
					// Save imported returns
					saveImportedRejectedOrder ();
				} // End else
			} // End if
			// Determine if the retrieved data are transaction details
			else if ( dao.getTablename ().equals ( TransactionDetailsDao.TABLENAME ) ) {
				// Store the list
				approvedTransactionDetails = new ArrayList < TransactionDetails > ();
				for ( Object entity : entities )
					approvedTransactionDetails.add ( (TransactionDetails) entity );
				// Dismiss any displayed dialogs (to avoid activity context leak)
				AppDialog.getInstance ().dismiss ();
				// Save imported returns
				saveImportedRejectedOrder ();
			} // End else if
		} // End else if
		else
			// Dismiss any displayed dialogs (to avoid activity context leak)
			AppDialog.getInstance ().dismiss ();
	}

	/*
	 * Call back method executed if the set sync process is a success.
	 *
	 * @see me.SyncWise.Android.Modules.Sync.SyncListener#onSetSuccess(int)
	 */
	@Override
	public void onSetSuccess ( ArrayList < ArrayList < Object >> entites , int requestCode ) {
		// Not implemented
	}

	/*
	 * Call back method executed if the get sync process is a failure.
	 *
	 * @see me.SyncWise.Android.Modules.Sync.SyncListener#onGetFailure(de.greenrobot.dao.AbstractDao, int)
	 */
	@Override
	public void onGetFailure ( AbstractDao < ? , ? > dao , int requestCode ) {
		// Clear lists
		approvedTransactionHeaders = null;
		approvedTransactionDetails = null;
		// Dismiss any displayed dialogs (to avoid activity context leak)
		AppDialog.getInstance ().dismiss ();
		// Display alert dialog
		AppDialog.getInstance ().displayAlert ( this , getString ( R.string.error_label ) , getString ( R.string.sync_failed_message ) , AppDialog.ButtonsType.OK , null );
	}

	/*
	 * Call back method executed if the set sync process is a failure.
	 *
	 * @see me.SyncWise.Android.Modules.Sync.SyncListener#onSetFailure(int)
	 */
	@Override
	public void onSetFailure ( int requestCode ) {
		// Not implemented
	}

	/*
	 * Call back method executed after the sync process executed.
	 *
	 * @see me.SyncWise.Android.Modules.Sync.SyncListener#onPostFinish(int)
	 */
	@Override
	public void onPostFinish ( int requestCode ) {
		// Do nothing.
	}
	
	/**
	 * Saves the imported approved returns.
	 */
	private void saveImportedRejectedOrder () {
		// Start by deleting non modified returns
		List < TransactionHeaders > transactionHeaders = DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionHeadersDao ().queryBuilder ()
				.where (
						TransactionHeadersDao.Properties.IsProcessed.eq ( IsProcessedUtils.isRejected () )
						).list ();
		ArrayList < String > transactionHeaderCodes = new ArrayList < String > ();
		for ( TransactionHeaders transactionHeader : transactionHeaders )
			transactionHeaderCodes.add ( transactionHeader.getTransactionCode () );
		DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionHeadersDao ().deleteInTx ( transactionHeaders );
		List < TransactionDetails > transactionDetails = DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsDao ().queryBuilder ()
				.where ( TransactionDetailsDao.Properties.TransactionCode.in ( transactionHeaderCodes ) ).list ();
		DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsDao ().deleteInTx ( transactionDetails );
		// Retrieve the approved transaction codes
		ArrayList < String > approvedTransactionCodes = new ArrayList < String > ();
		for ( TransactionHeaders transactionHeader : approvedTransactionHeaders )
			approvedTransactionCodes.add ( transactionHeader.getTransactionCode () );
		// Check for modified returns
		transactionHeaders = DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionHeadersDao ().queryBuilder ()
				.where ( TransactionHeadersDao.Properties.TransactionCode.in ( approvedTransactionCodes ) ).list ();
		ArrayList < String > modifiedTransactionCodes = new ArrayList < String > ();
		for ( TransactionHeaders transactionHeader : transactionHeaders )
			modifiedTransactionCodes.add ( transactionHeader.getTransactionCode () );
		try {
			// Begin transaction
			DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().beginTransaction ();
			// Add the retrieved returns except for the modified ones
			for ( TransactionHeaders transactionHeader : approvedTransactionHeaders )
				if ( ! modifiedTransactionCodes.contains ( transactionHeader.getTransactionCode () ) )
					DatabaseUtils.getInstance ( this ).getDaoSession ().insert ( transactionHeader );
				else
				{ TransactionHeaders   tt = DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionHeadersDao ().queryBuilder ()
				.where ( TransactionHeadersDao.Properties.TransactionCode.in ( transactionHeader.getTransactionCode() ) ).unique  ();
				if(tt!=null){
					tt.setIsProcessed(0);
					DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionHeadersDao ().update ( tt );}
				}
			for ( TransactionDetails transactionDetail : approvedTransactionDetails ){
				if (  modifiedTransactionCodes.contains ( transactionDetail.getTransactionCode () ))
						{
					List < TransactionDetails > tt = DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsDao ().queryBuilder ()
							.where ( TransactionDetailsDao.Properties.TransactionCode.in ( transactionDetail.getTransactionCode() ) ).list ();
							 if(tt!=null)
						DatabaseUtils.getInstance ( this ).getDaoSession ().getTransactionDetailsDao ().deleteInTx(tt);
					 

			}
				}
			
			for ( TransactionDetails transactionDetail : approvedTransactionDetails )
				if ( ! modifiedTransactionCodes.contains ( transactionDetail.getTransactionCode () ) )
					DatabaseUtils.getInstance ( this ).getDaoSession ().insert ( transactionDetail );
				else
					DatabaseUtils.getInstance ( this ).getDaoSession ().insert ( transactionDetail );
			// Commit changes
			DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().setTransactionSuccessful ();
			// Populate the list by setting a new adapter
			populate ();
			// Display alert dialog
			AppDialog.getInstance ().displayAlert ( this , null , getString ( R.string.sync_success_message ) , AppDialog.ButtonsType.OK , null );
		} catch ( Exception exception ) {
			// Display alert dialog
			AppDialog.getInstance ().displayAlert ( this , getString ( R.string.error_label ) , getString ( R.string.populate_data_error_message ) , AppDialog.ButtonsType.OK , null );
		} finally {
			 //End transaction
			DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().endTransaction ();
			// Clear lists
			approvedTransactionHeaders = null;
			approvedTransactionDetails = null;
		} // End try-catch-finally block
	}
	/**
	 * Retrieve all the returns asynchronously.
	 */
	protected void populate () {
		// Populate the list by setting a new adapter
		new PopulateList ().execute ();
	}
	private String sendData () {
 
        // Retrieve a reference to the dao session
        DaoSession daoSession = DatabaseUtils.getInstance (  this ).getDaoSession ();
        // Search for the GPRS link
        ConnectionSettings connection = daoSession.getConnectionSettingsDao ().queryBuilder ()
        		.where ( ConnectionSettingsDao.Properties.ConnectionSettingType.eq ( ConnectionSettingsUtils.Type.GPRS ) ).unique ();
        // Check if the connection is valid
        if ( connection == null ) {
            // Display debug output if required
        	// Invalid connection
        	return "ERROR";
        } // End if
        
        // Retrieve the user code
        String userCode = DatabaseUtils.getCurrentUserCode ( getApplicationContext () );
    

      Visits  visits=(Visits) getIntent ().getSerializableExtra ( VISIT ) ;
     List<TransactionHeaders> transactionHeaders = daoSession.getTransactionHeadersDao ().queryRaw ( 
				"WHERE " + TransactionHeadersDao.Properties.IsProcessed.columnName + "=? and visitid!=?"  ,
				new String [] { String.valueOf ( IsProcessedUtils.isNotSync () ),
						String.valueOf ( visits.getVisitID () )} );
		// Declare and initialize a list of strings used to host the transaction codes
		List < String > codes = new ArrayList < String > ();
		// Iterate over all transactions
		for ( TransactionHeaders header : transactionHeaders )
			// Add the transaction code to the list
			codes.add ( header.getTransactionCode () );
		// Compute and return a query builder used to query all the transaction details of the not processed transaction headers
		 List < TransactionDetails > transactionDetails = daoSession.getTransactionDetailsDao ()
				 .queryBuilder ().where ( TransactionDetailsDao.Properties.TransactionCode.in ( codes ) ).list();
		 List < TransactionSequences > transactionSequences = daoSession.getTransactionSequencesDao ()
				 .queryBuilder ().where ( TransactionSequencesDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode(this) ) ).list();

		 // Display debug output if required
        
		// Build a set Gson object
		Gson gson = CommonUtilities.buildSetGson ();
		// Declare and initialize a map used to host the Http post arguments
		Map < String , String > arguments = new HashMap < String , String > ();
		// Compute the server url
		String url = ConnectionSettingsUtils.getSetMethodPath ( connection.getConnectionSettingURL () );
		// Declare and initialize a string used to host the table argument
		String table = null;
 
			// Add the device serial table
		arguments.put ( UsersDao.Properties.UserCode.columnName , userCode );
		 
		arguments.put ( SyncUtils.TABLES_COUNT , String.valueOf ( 3) );
          // Initialize / update the table argument
			table = daoSession.getTransactionHeadersDao().getTablename ()  +"," + daoSession.getTransactionDetailsDao ().getTablename ()+"," + daoSession.getTransactionSequencesDao().getTablename ();
			// Add the device tracking to the argument
			arguments.put ( daoSession.getTransactionHeadersDao ().getTablename () , gson.toJson ( transactionHeaders ) );
			arguments.put ( daoSession.getTransactionDetailsDao() .getTablename () , gson.toJson ( transactionDetails ) );
	    	arguments.put(daoSession.getTransactionSequencesDao().getTablename () , gson.toJson ( transactionSequences ) );
			// Add the checksum argument
			arguments.put ( SyncUtils.CHECKSUM , SyncUtils.getArgumentsChecksum (   this , arguments ) );
	 
		// Add the table argument
		arguments.put ( SyncUtils.TABLE , table );
		
		// Get an http response (via an http post resquest) for the provided url and arguments
		HttpResponse response = SyncUtils.post ( url , arguments );
		// Check if the response is valid
		if ( response == null )
			// Invalid response
			return "ERROR";
		try {
			// Retrieve the string response
			String result = org.apache.http.util.EntityUtils.toString ( response.getEntity () );
			// Check if no errors occurred
			if ( result.equalsIgnoreCase ( SyncUtils.SUCCESS ) ) {
				if(transactionHeaders!=null && !transactionHeaders.isEmpty()){
				for(TransactionHeaders cc :transactionHeaders)
				{
					cc.setIsProcessed(2);
					 daoSession.getTransactionHeadersDao() .update(cc);
				}	} // End if
	             return result;
	}
			} catch ( ParseException exception ) {
			// Do nothing
		} catch ( IOException exception ) {
			// Do nothing
		} catch ( JsonSyntaxException exception ) {
			// Do nothing
		} // End of try-catch block
		return "ERROR";
    }
    private class DownloadTextTask extends AsyncTask<String, Void, String>  {
		private ProgressDialog dialog1;
		 private Activity activity;
		
		public DownloadTextTask(Activity activity) {
		    this.activity = activity;
		    this.dialog1 = new ProgressDialog(activity);
		}

		@Override
	protected void onPreExecute () {
		//disable();
		// Display indeterminate progress before starting a new thread
	//	ProgressDialog progressDialog = ProgressDialog.show(context, "Wait", "Downloading...");
            super.onPreExecute();
			  this.dialog1.setMessage(" Send Data " );
			  this.dialog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			  this.dialog1.show();
		//	Baguette.showText ( activity , "Please Wait To Connecting" , Baguette.BackgroundColor.RED );
			 	  }
	  
	@Override
	protected String doInBackground(String... urls) {
		
	return 	        	sendData ();
    
	}
	
	@Override
	protected void onPostExecute(String result) {
		 if (this.dialog1.isShowing()) {
	           this.dialog1.dismiss();
	        }
		if(result.equals("Success"))
			{	// Check if there are device tracking to send
			 
					 		importRejectesOrders ();
							 
					 
			

			}
		else 
		{
			   AppDialog.getInstance ().displayAlert(activity , null , getString ( R.string.sync_failed_message ) , AppDialog.ButtonsType.OK,
						new DialogInterface.OnClickListener () {
					@Override
					public void onClick ( DialogInterface dialog , int which ) {
					 
						 
					} } ); 
 
		}
		
		 
		}
	}
}