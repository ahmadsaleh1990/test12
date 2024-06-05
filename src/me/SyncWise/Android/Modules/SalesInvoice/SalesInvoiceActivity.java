/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.SalesInvoice;

 
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
import me.SyncWise.Android.ListViewPosition;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Vibration; 
import me.SyncWise.Android.AppDialog.ButtonsType;
import me.SyncWise.Android.Database.CollectionHeadersDao;
import me.SyncWise.Android.Database.CollectionInvoices;
import me.SyncWise.Android.Database.CollectionInvoicesDao;
import me.SyncWise.Android.Database.CollectionUtils;
 
import me.SyncWise.Android.Database.CurrenciesDao;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Database.PriceLists;
import me.SyncWise.Android.Database.PriceListsDao;
import me.SyncWise.Android.Database.StatusUtils;
import me.SyncWise.Android.Database.TransactionDetails;
import me.SyncWise.Android.Database.TransactionDetailsDao;
import me.SyncWise.Android.Database.TransactionHeaders;
import me.SyncWise.Android.Database.TransactionHeadersUtils;
import me.SyncWise.Android.Database.TransactionHeadersDao;
import me.SyncWise.Android.Database.UserPasswordsUtils;
import me.SyncWise.Android.Database.Users;
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Database.VehiclesStock;
import me.SyncWise.Android.Database.VehiclesStockDao;
import me.SyncWise.Android.Gson.BaseTimerListActivity;
import me.SyncWise.Android.Modules.Call.CallMenuActivity;
 
import me.SyncWise.Android.Modules.Journey.Call;
 
import me.SyncWise.Android.Modules.SalesInvoice.SalesInvoiceAdapter.ViewHolder;
import me.SyncWise.Android.Modules.SalesOrder.SalesOrderActivity;
 
import me.SyncWise.Android.SectionsList.MultipleListAdapter;
import me.SyncWise.Android.SectionsList.Section;
import me.SyncWise.Android.Utilities.ClientCard;
import me.SyncWise.Android.Widgets.Baguette;
import me.SyncWise.Android.Widgets.QuickAction.ActionItem;
import me.SyncWise.Android.Widgets.QuickAction.QuickAction;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
 
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
 

/**
 * Activity implemented to view all the performed sales invoices.
 * 
 * @author Elias
 * @sw.todo <b>Sales Invoice Activity Implementation :</b><br>
 * Simply add this class in the {@code AndroidManifest.xml} file.
 *
 */
public class SalesInvoiceActivity extends BaseTimerListActivity implements QuickAction.OnActionItemClickListener {

	/**
	 * Bundle key used to put/retrieve the content of {@link #displayPasscode}.
	 */
	private static final String DISPLAY_PASSCODE = SalesInvoiceActivity.class.getName () + ".DISPLAY_PASSCODE";
	
	/**
	 * Boolean used to determine whether to display the passcode UI or not.
	 */
	protected boolean displayPasscode;
	
	/**
	 * Reference to a {@link me.SyncWise.Android.Widgets.QuickAction.QuickAction QuickAction} used in this activity.<br>
	 * This widget is used for non processed sales invoices.
	 */
	protected QuickAction quickAction;
	
	/**
	 * Reference to a {@link me.SyncWise.Android.Widgets.QuickAction.QuickAction QuickAction} used in this activity.<br>
	 * This widget is used for final sales invoices.
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
		public static final int DELETE = 2;
	}
	
	/**
	 * Bundle key used to put/retrieve the content of the current call.
	 */
	public static final String CALL = SalesInvoiceActivity.class.getName () + ".CALL";
	
	/**
	 * Bundle key used to put/retrieve the content of the visit object.
	 */
	public static final String VISIT = SalesInvoiceActivity.class.getName () + ".VISIT";
	
	/**
	 * Bundle key used to put/retrieve the content of the is view flag.<br>
	 * This flag is used to determine if the user can edit / create sales invoices or only view the previous invoices.
	 */
	public static final String IS_VIEW = SalesInvoiceActivity.class.getName () + ".IS_VIEW";
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #listViewPosition}.
	 */
	private static final String LIST_VIEW_POSITION = SalesInvoiceActivity.class.getName () + ".LIST_VIEW_POSITION";
	
	/**
	 * Object used to save/restore the list views' position.
	 */
	private ListViewPosition listViewPosition;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #saveSuccess}.
	 */
	public static final String SAVE_SUCCESS = SalesInvoiceActivity.class.getName () + ".SAVE_SUCCESS";
	
	/**
	 * Flag used to indicate if a sales invoice save was successful.
	 */
	private boolean saveSuccess;
	
	/**
	 * Bundle key used to put/retrieve the content of the successful delete flag.
	 */
	public static final String DELETE_SUCCESS = SalesInvoiceActivity.class.getName () + ".DELETE_SUCCESS";
	
	/**
	 * Bundle key used to put/retrieve the content of the successful print flag.
	 */
	public static final String PRINT = SalesInvoiceActivity.class.getName () + ".PRINT";
	
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
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.sales_invoice_activity_title ) );
		// Define the empty list view
		getListView ().setEmptyView ( findViewById ( R.id.empty_list_view ) );
		// Display empty list label
		( (TextView) findViewById ( R.id.empty_list_view ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_sales_invoice_list_label ) );
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
			displayPasscode = savedInstanceState.getBoolean ( DISPLAY_PASSCODE );
			saveSuccess = savedInstanceState.getBoolean ( SAVE_SUCCESS , saveSuccess );
			listViewPosition = (ListViewPosition) savedInstanceState.getSerializable ( LIST_VIEW_POSITION );
		} // End if
		
		// Determine if the list view has a valid adapter
		if ( getListAdapter () == null )
			// Populate the list by setting a new adapter
			new PopulateList ().execute ();
	}
	
	/*
	 * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause(), for your activity to start interacting with the user.
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume () {
		// Superclass method call
		super.onResume ();
		// Check if the pass code UI should be displayed
		if ( displayPasscode ) {
			// Initialize the tertiary view
			initializeTertiaryView ( null );
			// Retrieve a reference to the tertiary view
			View tertiaryView = findViewById ( R.id.layout_passcode );
			// Display the tertiary view
			tertiaryView.setVisibility ( View.VISIBLE );
			// Refresh the action bar
			invalidateOptionsMenu ();
		} // End if
	}
	
	/*
	 * Performs all necessary setup in order to properly display the quick action widget.
	 *
	 * @see me.SyncWise.Android.Modules.Journey.JourneyFragment#setupQuickAction()
	 */
    private void setupQuickAction () {
		// Initialize the quick action widgets
		quickAction_Final = new QuickAction ( this , QuickAction.VERTICAL );
		quickAction = new QuickAction ( this , QuickAction.VERTICAL );
		// Action Item : View
		ActionItem view = new ActionItem ( ActionItemID.VIEW ,
				AppResources.getInstance ( this ).getString ( this , R.string.view_label ) ,
				getResources ().getDrawable ( R.drawable.quick_action_search ) );
		// Action Item : Delete
		ActionItem delete = new ActionItem ( ActionItemID.DELETE ,
				AppResources.getInstance ( this ).getString ( this , R.string.quick_action_sales_order_remove_label ) ,
				getResources ().getDrawable ( R.drawable.quick_action_cancel ) );
    	// Populate the quick action widget with quick action items
		quickAction_Final.addActionItem ( view );
		quickAction.addActionItem ( view );
		quickAction.addActionItem ( delete );
		// Assign an action item click listener
		quickAction_Final.setOnActionItemClickListener ( this );
		quickAction.setOnActionItemClickListener ( this );
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
		// Determine if the passcode is undergoing any modifications
		if ( displayPasscode ) {
			// Reset flag
			displayPasscode = false;
			// Retrieve a reference to the tertiary view
			View tertiary = findViewById ( R.id.layout_passcode );
			// Hide the tertiary view
			tertiary.startAnimation ( getViewAnimationOut ( tertiary ) );
    		// Refresh the action bar
    		invalidateOptionsMenu ();
    		// Exit method
    		return;
		} // End else if
		// Determine if a successful sales invoice save occurred
		if ( saveSuccess )
	    	// Call this to set the result that your activity will return to its caller
	    	setResult ( RESULT_OK , new Intent ().putExtra ( CallMenuActivity.SKIP_ACTION_RESULT , true ) );
	 
		
        
//        Toast.makeText(this,"This is a Toast ", 15000).show();
//                
//        try {
//                   Thread.sleep(15000); // As I am using LENGTH_LONG in Toast
//                //  SalesInvoiceActivity.this.finish();
//             } catch (Exception e) {
//                   e.printStackTrace();
//               }
     
      
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
    	// Save the content of saveSuccess in the outState bundle
    	outState.putBoolean ( SAVE_SUCCESS , saveSuccess );
    	// Save the list view's position in the outState bundle
    	outState.putSerializable ( LIST_VIEW_POSITION , new ListViewPosition ( getListView () ) );
    	// Save the content of displayPasscode in the outState bundle
    	outState.putBoolean ( DISPLAY_PASSCODE , displayPasscode );
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
		message+="Client Code: "+((Call) getIntent ().getSerializableExtra ( CALL ) ).getClient ().getClientCode()+"\n"; 
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
		String passcode = passcodeEditText.getText ().toString ().trim ();
	  	Call call = (Call) getIntent ().getSerializableExtra ( CALL );
    
		// Validate pass code
		if ( ! UserPasswordsUtils.validateTimePasswordClientsCreditDays ( this , UserPasswordsUtils.TransactionHeaderType.TRANSACTION , TransactionHeadersUtils.Type.SALES_INVOICE , passcode,	call.getClient ().getClientCode() )) {
			// Reset passcode
			passcode = null;
			// Indicate that the passcode is not valid
			Baguette.showText ( this ,
					AppResources.getInstance ( this ).getString ( this , R.string.time_passcode_invalid_message ) ,
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
		// Indicate that the save was successful
		Vibration.vibrate ( this );
		// Refresh the action bar
		invalidateOptionsMenu ();
		
		// Start a new sales invoice
		startNewSalesInvoice ( passcode ,seletedItems.isEmpty()?null:seletedItems);
	}
	
    /*
     * This method will be called when an item in the list is selected.
     *
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
	@Override
    protected void onListItemClick ( ListView listView , View view , final int position , long id ) {
		// Check if there is any ongoing note modifications
		if ( displayPasscode )
			// Do nothing
			return;
		
		// Determine if the current sales invoice is processed or deleted (final)
		// OR if the IS_VIEW flag is set
		if ( ( (ViewHolder) view.getTag () ).isFinal || getIntent ().getBooleanExtra ( IS_VIEW , false ) )
			// The current sales invoice is final
			// Display the appropriate quick action widget
			quickAction_Final.show ( view , null , getResources () );
		else
			// Otherwise the current sales invoice is not processed
			// Display the appropriate quick action widget
			quickAction.show ( view , null , getResources () );
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
		case ActionItemID.VIEW:
			// Start the new activity
			startActivityForResult ( getIntent_SalesInvoiceDetailsActivity_Previous ( ( (ViewHolder) anchor.getTag () ).code , true ) ,
					SalesInvoiceDetailsActivity.REQUEST_CODE_INFO );
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
									// Retrieve the current transaction header object
									TransactionHeaders transactionHeader = DatabaseUtils.getInstance ( SalesInvoiceActivity.this ).getDaoSession ().getTransactionHeadersDao ().queryBuilder ()
											.where ( TransactionHeadersDao.Properties.TransactionCode.eq ( transactionCode ) ).unique ();
									CollectionInvoices collectionInvoices = DatabaseUtils.getInstance ( SalesInvoiceActivity.this ).getDaoSession ().getCollectionInvoicesDao ().queryBuilder ()
											.where ( CollectionInvoicesDao.Properties.InvoiceCode.eq ( transactionCode ) ).unique ();
								
									
								//	TransactionHeaders transactionHeader1 = DatabaseUtils.getInstance ( SalesInvoiceActivity.this ).getDaoSession ().getTransactionHeadersDao ().queryBuilder ()
								//			.where ( TransactionHeadersDao.Properties.TransactionCode.in( collectionInvoices.getInvoiceCode()) ).unique ();
								 	// Refresh the object
									DatabaseUtils.getInstance ( SalesInvoiceActivity.this ).getDaoSession ().getTransactionHeadersDao ().refresh ( transactionHeader );
									// Make sure it is not processed
									if ( transactionHeader.getIsProcessed () == IsProcessedUtils.isSync () || collectionInvoices!=null ){//|| transactionHeader.getRemainingAmount() ==0 ) {
										// Display baguette message
										Baguette.showText ( SalesInvoiceActivity.this , AppResources.getInstance ( SalesInvoiceActivity.this ).getString ( SalesInvoiceActivity.this , R.string.sales_invoice_already_processed_message ) , Baguette.BackgroundColor.RED );
										break;
									} // End if
									
									// Retrieve the transaction details with valid quantities
									List < TransactionDetails > transactionDetails = DatabaseUtils.getInstance ( SalesInvoiceActivity.this ).getDaoSession ().getTransactionDetailsDao ().queryBuilder ()
											.where ( TransactionDetailsDao.Properties.TransactionCode.eq ( transactionCode ) , TransactionDetailsDao.Properties.BasicUnitQuantity.gt ( 0 ) ).list ();
									// Iterate over the transaction details list
									for ( TransactionDetails transactionDetail : transactionDetails ) {
										// Retrieve the vehicle stock
										VehiclesStock vehicleStock = DatabaseUtils.getInstance ( SalesInvoiceActivity.this ).getDaoSession ().getVehiclesStockDao ().queryBuilder ()
												.where ( VehiclesStockDao.Properties.ItemCode.eq ( transactionDetail.getItemCode () ) ).unique ();
										// Check if the vehicle stock is valid
										if ( vehicleStock != null ) {
											// Reduce the good quantity
											vehicleStock.setGoodQuantity ( vehicleStock.getGoodQuantity () + transactionDetail.getBasicUnitQuantity () );
											// Update the vehicle stock
											DatabaseUtils.getInstance ( SalesInvoiceActivity.this ).getDaoSession ().getVehiclesStockDao ().update ( vehicleStock );
										} // End if
									} // End for each
									
//									ContentValues args = new ContentValues();
//								    args.put( TransactionHeadersDao.Properties.TransactionStatus.columnName, 2 );
//								
//								    DatabaseUtils.getInstance ( SalesInvoiceActivity.this ).getDaoSession ().getDatabase ()
//								    .update(TransactionHeadersDao.TABLENAME, args, TransactionHeadersDao.Properties.TransactionCode.columnName+ "=" + transactionCode, null);
////								
									
									
									
									DatabaseUtils.getInstance ( SalesInvoiceActivity.this ).getDaoSession ().getDatabase ().beginTransaction();
									// Compute the SQL string
									String SQL = "UPDATE " + TransactionHeadersDao.TABLENAME + " " +
											"SET " + TransactionHeadersDao.Properties.TransactionStatus.columnName + " = ? " +
											"WHERE " + TransactionHeadersDao.Properties.TransactionCode.columnName + " = ? ";
									// Compute the selection arguments
									String selectionArguments [] = new String [] {
											String.valueOf ( StatusUtils.isDeleted () ) , transactionCode
									};
									// Execute the transaction update query
									DatabaseUtils.getInstance ( SalesInvoiceActivity.this ).getDaoSession ().getDatabase ().execSQL ( SQL , selectionArguments );
									// Commit changes
									DatabaseUtils.getInstance ( SalesInvoiceActivity.this ).getDaoSession ().getDatabase ().setTransactionSuccessful ();
									
									DatabaseUtils.getInstance ( SalesInvoiceActivity.this ).getDaoSession ().getDatabase ().endTransaction();
									// End the visit
								
									TransactionHeaders th = DatabaseUtils.getInstance (  SalesInvoiceActivity.this ).getDaoSession ().getTransactionHeadersDao().queryBuilder ()
											.where ( TransactionHeadersDao.Properties.TransactionCode.eq (  transactionCode )  
													    ).unique ();
								
									th.setTransactionStatus(2);
									// Update the visit in DB
									DatabaseUtils.getInstance (  SalesInvoiceActivity.this ).getDaoSession ().getTransactionHeadersDao ().update ( th );
		 
									
									// Display baguette message
									Baguette.showText ( SalesInvoiceActivity.this , AppResources.getInstance ( SalesInvoiceActivity.this ).getString ( SalesInvoiceActivity.this , R.string.sales_invoice_delete_success_message ) , Baguette.BackgroundColor.GREEN );
								 
									
									
//									Intent hello1 = new Intent(SalesInvoiceActivity.this, JourneyActivity.class);
//							        startActivity(hello1);
//								   
									// Clear list adapter
									setListAdapter ( null );
								//	 Populate the list by setting a new adapter
									new PopulateList ().execute ();
								} catch ( Exception exception ) {
									// Display baguette message
									Baguette.showText ( SalesInvoiceActivity.this , AppResources.getInstance ( SalesInvoiceActivity.this ).getString ( SalesInvoiceActivity.this , R.string.sales_invoice_delete_failure_message ) , Baguette.BackgroundColor.RED );
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
    	// Check if a note is undergoing modifications
    	if ( displayPasscode )
    		// Hide the menu
            return false;
    	
    	// Use the MenuInflater of this context to inflate a menu hierarchy from the specified XML resource
    	getMenuInflater ().inflate ( R.menu.action_bar , menu );
    	// Check the IS_VIEW flag
    	if ( ! getIntent ().getBooleanExtra ( IS_VIEW , false ) ) {
	    	// Enable the required menu items
	    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_add ) );
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
		 	SQL1=	"select  COUNT(ClientCode) from ClientPriceLists where clientCode = ?"; 
		 	Call	call1 = (Call) getIntent ().getSerializableExtra ( CALL );
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

				AppDialog.getInstance ().displayAlert (  this ,
          				null ,
          				AppResources.getInstance ( this ).getString ( this , R.string.sales_order_no_pricelist_message ) ,
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
    		if ( PermissionsUtils.getEnableSalesInvoceCashOrCredit ( SalesInvoiceActivity.this ,   DatabaseUtils.getCurrentUserCode ( SalesInvoiceActivity.this ) ,  DatabaseUtils.getCurrentCompanyCode(SalesInvoiceActivity.this) ) ) {
    			ArrayList < String > priceCodes = new ArrayList < String > ();
    			
    			String SQL = null;
    		 	String selectionArguments [] = null;
    		 	SQL=	" select PriceListCode from ClientPriceLists where clientCode = ? "; 
    		 	Call	call = (Call) getIntent ().getSerializableExtra ( CALL );
    		 	selectionArguments = new String [] {
    					String.valueOf (call.getClientDivision ().getClientCode () )  
                    };
    				Cursor cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
    						        .rawQuery(SQL , selectionArguments) ;
    				
    		
    				
    				// Move the cursor to the first raw
    				if ( cursor.moveToFirst () ) {
    					// Iterate over all raws
    					do {  
    						String pricecode = cursor.getString(0);
    						priceCodes.add ( pricecode );
    					} while ( cursor.moveToNext () );
    				} // End if
    				// Close the cursor
    				cursor.close ();
    				cursor = null;

    		 	
    		 	
    				 List < PriceLists > priceLists = new ArrayList < PriceLists > ();
        			 if ( PermissionsUtils.getEnableSalesOrderCashOrCreditClientPriceList ( SalesInvoiceActivity.this ,   DatabaseUtils.getCurrentUserCode ( SalesInvoiceActivity.this ) ,  DatabaseUtils.getCurrentCompanyCode(SalesInvoiceActivity.this) ) ) {
        		    		
        			 priceLists =  DatabaseUtils.getInstance ( this ).getDaoSession ().getPriceListsDao().queryBuilder ()
        	        		 .where ( PriceListsDao.Properties.PriceListSource.eq ( 2 )
        	        				 ,PriceListsDao.Properties.PriceListCode.in( priceCodes ) ).list ();
        			// new ahmad
//        			 if( priceLists.size() == 1 ){
//        				 List < String > seletedItems = new ArrayList < String >();
//        				 seletedItems.add(priceLists.get(0).getPriceListCode());
//        				 startNewSalesInvoice (null,seletedItems);
//        			 }
        			 }
        			 else{
        				 priceLists =  DatabaseUtils.getInstance ( this ).getDaoSession ().getPriceListsDao().queryBuilder ()
            	        		 .where ( PriceListsDao.Properties.PriceListSource.eq ( 2 )	 ).list ();
        			 }
    	         if( priceLists.size() == 0 )
    	         {
//    	        	 Baguette.showText ( SalesOrderActivity.this , 
//    	        			 AppResources.getInstance ( SalesOrderActivity.this )
//    	        			 .getString ( SalesOrderActivity.this , R.string.sales_order_periode_pricelist_message )
//    	        			 , Baguette.BackgroundColor.RED );
    	        	 startNewSalesInvoice (null,null);
    	        	 
    	         }
    	         else
    			 displayalertWarning ();
    		}
    		else   		
    		// Start a new sales invoice
    		startNewSalesInvoice ( null,null );
    		// Consume event
    		return true;
    	} // End if
    	// Allow normal menu processing to proceed
    	return false;
    }
    private     // arraylist to keep the selected items
    final List < String > seletedItems = new ArrayList < String >();
    private void chooseCountType () {
    	
    
  	  AlertDialog dialog; 
  	     
  	  
  	  ArrayList < String > priceCodes = new ArrayList < String > ();
  	  String SQL=null;
		 	String selectionArguments [] = null;
		 	SQL=	" select PriceListCode from ClientPriceLists where clientCode = ? "; 
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
       if ( PermissionsUtils.getEnableSalesOrderCashOrCreditClientPriceList ( SalesInvoiceActivity.this ,   DatabaseUtils.getCurrentUserCode ( SalesInvoiceActivity.this ) ,  DatabaseUtils.getCurrentCompanyCode(SalesInvoiceActivity.this) ) ) {
   		
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
//      	 Baguette.showText ( SalesOrderActivity.this , 
//      			 AppResources.getInstance ( SalesOrderActivity.this )
//      			 .getString ( SalesOrderActivity.this , R.string.sales_order_periode_pricelist_message )
//      			 , Baguette.BackgroundColor.RED );
    	   startNewSalesInvoice (null,null);
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
   		seletedItems.clear();
       builder.setMultiChoiceItems(items, null,
               new DialogInterface.OnMultiChoiceClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int indexSelected,
                boolean isChecked) {
            if (isChecked) {
                // If the user checked the item, add it to the selected items
                seletedItems.add( items[ indexSelected ] );
            } else if (seletedItems.contains(items[ indexSelected ])) {
                // Else, if the item is already in the array, remove it
                seletedItems.remove(items[ indexSelected ]);
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
        		AppDialog.getInstance ().displayAlert (  SalesInvoiceActivity.this ,
        				null ,
        				AppResources.getInstance ( SalesInvoiceActivity.this ).getString ( SalesInvoiceActivity.this , R.string.sales_order_select_pricelist_message ) ,
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
      			  AppDialog.getInstance ().displayAlert (  SalesInvoiceActivity.this ,
              				null ,
              				AppResources.getInstance ( SalesInvoiceActivity.this ).getString ( SalesInvoiceActivity.this , R.string.sales_order_select_one_pricelist_message ) ,
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
      	     startNewSalesInvoice(null,seletedItems);

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
 				startNewSalesInvoice ( null , null );
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
	 * Initializes the passcode (tertiary) view.
	 * 
	 * @param restore	Boolean used to determine if the tertiary view is being initialized for the first time or is being restored.
	 */
	protected void initializeTertiaryView ( final boolean restore ) {
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
		String message ="Client Code: "+((Call) getIntent ().getSerializableExtra ( CALL ) ).getClient ().getClientCode()+"\n"+ AppResources.getInstance ( this ).getString ( this , R.string.cannot_start_invoice_credit_day_client_message ) ; 
		// Display the message
		messageLabel.setText (message);
		// Check if this the first time creation
		if ( ! restore )
			// Clear any previous entries
			passcodeEditText.setText ( "" );
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
	private void displayCashClientWarning () {
		// Declare and initialize an alert dialog builder
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder ( this );
		// By default, the alert dialog cannot be cancelled UNLESS it has no buttons
		alertDialogBuilder.setCancelable ( false );
		// Set the title
		alertDialogBuilder.setTitle ( R.string.warning_label );
		// Set the description
		alertDialogBuilder.setMessage ( R.string.cannot_start_invoice_credit_client_message );
		// Map the positive and negative buttons
		alertDialogBuilder.setPositiveButton ( R.string.Cash_label , new DialogInterface.OnClickListener() {
			@Override
			public void onClick ( DialogInterface dialog , int which ) {
	    		// Set flag
				
				// Start the new activity
				startActivityForResult ( getIntent_SalesInvoiceDetailsActivity_New ( null,"1",seletedItems.isEmpty()?null:seletedItems ) , SalesInvoiceDetailsActivity.REQUEST_CODE_NEW );
				// Specify an explicit transition animation to perform next
				ActivityTransition.SlideOutLeft ( SalesInvoiceActivity.this );
			}	
			} );
		alertDialogBuilder.setNegativeButton ( R.string.use_passcode , new DialogInterface.OnClickListener() {
			@Override
			public void onClick ( DialogInterface dialog , int which ) {
	    		// Set flag
	    		displayPasscode = true;
	    		// Initialize the tertiary view
	    		initializeTertiaryView ( false );
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
     * Starts the {@link me.SyncWise.Android.Modules.SalesInvoice.SalesInvoiceDetailsActivity SalesInvoiceDetailsActivity} activity in order to create a new sales invoice.
     * 
     * @param passcode	String hosting any used pass code.
     */
    protected void startNewSalesInvoice ( final String passcode,List < String > seletedItems ) {
    	// Check if the passcode is not valid
    	if ( passcode == null ) {
	    	// Retrieve the call
	    	Call call = (Call) getIntent ().getSerializableExtra ( CALL );
	    	call.getClient ().setDivisionCode ( call.getClientDivision ().getDivisionCode () );
			// Check if the client has over due amounts for the current division
			boolean hasOverDuesCurrentDivision = ClientCard.hasOverduesCurrentDivision ( this , call.getClient () );
		
			// Check if the client has over due amounts for the current company
			boolean hasOverDuesCurrentCompany = ClientCard.hasOverduesCurrentCompany ( this , call.getClient () );
			// Retrieve the current date
			Calendar calendar = Calendar.getInstance ();
			// Retrieve the start of the day
			calendar.set ( Calendar.HOUR_OF_DAY , 0 );
			calendar.set ( Calendar.MINUTE , 0 );
			calendar.set ( Calendar.SECOND , 0 );
			calendar.set ( Calendar.MILLISECOND , 0 );
			
			String userCode = DatabaseUtils.getCurrentUserCode ( this );
			// Retrieve the company code
			String companyCode = DatabaseUtils.getCurrentCompanyCode ( this );
			// Retrieve the user
			Users user = DatabaseUtils.getInstance ( this ).getDaoSession ().getUsersDao ().queryBuilder ()
					.where ( UsersDao.Properties.UserCode.eq ( userCode ) , UsersDao.Properties.CompanyCode.eq ( companyCode ) ).unique ();
	
			
			
			
	    	// Check if the client paid on account money today
			boolean paidOnAccount = DatabaseUtils.getInstance ( this ).getDaoSession ().getCollectionHeadersDao ().queryBuilder ()
					.where ( CollectionHeadersDao.Properties.CollectionType.eq ( CollectionUtils.CollectionType.ONACCOUNT ) ,
							CollectionHeadersDao.Properties.ClientCode.eq ( call.getClient ().getParentCode () ) ,
							CollectionHeadersDao.Properties.CompanyCode.eq ( call.getClient ().getCompanyCode () ) ).count () > 0;
 		
	 		String SQL = null;
	 	 	String selectionArguments [] = null;
 
	 	  
	 	 	SQL = 	" select COUNT(*) from CollectionHeaders ch inner join CollectionDetails cd on cd.CollectionCode  = ch.CollectionCode    " +
	 	 			" and cd.DivisionCode=ch.DivisionCode where cd.CollectionDetailType = ? and ch.ClientCode= ? 	 ";
	 	 	// Compute the selection arguments
	 			selectionArguments = new String [] {	   
	 					String.valueOf ( CollectionUtils.PaymentType.CHECK),
	 					String.valueOf(call.getClient ().getParentCode ())
	 					
	 			};
	 			Cursor cursor = DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ()
	 					        .rawQuery(SQL , selectionArguments) ;
	 			int isCheck=0;
	 				// Move the cursor to the first raw
	 			if ( cursor.moveToFirst () ) {
	 				// Iterate over all raws
	 				do {
	 					isCheck=  cursor.getInt(0) ;
	 				 
	 				} while ( cursor.moveToNext () );
	 			} // End if
	 			// Close the cursor
	 			cursor.close ();
	 			cursor = null;
			boolean paidCheck = isCheck > 0;
			if( call.getClient().getClientVATNumber () != null )
				{
				if( call.getClient().getClientVATNumber().equals ("Y") )
				{
					
				}
				else
				if ( hasOverDuesCurrentDivision  || paidCheck) {//|| ( hasOverDuesCurrentCompany && ! paidOnAccount )
					// Check the logged in user is cash van and the client is of type credit
					if ( user.getUserType () == 11 && ClientCard.isCreditClient( call.getClient () ) ) {
						// Display warning
							displayCashClientWarning ();
							// End method
							return;
					}
					
//					// Set flag
//					displayPasscode = true;
//					// Build the description
//					String description = null;
//					if ( hasOverDuesCurrentDivision  )//&& ! ( hasOverDuesCurrentCompany && ! paidOnAccount )
//						description = AppResources.getInstance ( this ).getString ( this , R.string.sales_invoice_passcode_required_day );
////	 				else if ( ! hasOverDuesCurrentDivision  )//&& ( hasOverDuesCurrentCompany && ! paidOnAccount )
////	 					description = AppResources.getInstance ( this ).getString ( this , R.string.has_overdues_other_divisions );
////					else if ( hasOverDuesCurrentDivision  )//&& ( hasOverDuesCurrentCompany && ! paidOnAccount )
////						description = AppResources.getInstance ( this ).getString ( this , R.string.has_overdues_current_company );
//					else if ( paidCheck  )//&& ( hasOverDuesCurrentCompany && ! paidOnAccount )
//					description = AppResources.getInstance ( this ).getString ( this , R.string.has_overdues_check );
//					
//					
//					// Initialize the tertiary view
//					initializeTertiaryView ( description );
//					// Retrieve a reference to the tertiary view
//					View tertiaryView = findViewById ( R.id.layout_passcode );
//					// Display the tertiary view
//					tertiaryView.setVisibility ( View.VISIBLE );
//					// Animate the tertiary view
//					tertiaryView.startAnimation ( getViewAnimationIn() );
//					// Refresh the action bar
//					invalidateOptionsMenu ();
//					// Exit method
//					return;
				} // End if	
				}
				else
			// Check if any condition is violated
			if ( hasOverDuesCurrentDivision  || paidCheck) {//|| ( hasOverDuesCurrentCompany && ! paidOnAccount )
				// Check the logged in user is cash van and the client is of type credit
				if ( user.getUserType () == 11 && ClientCard.isCreditClient( call.getClient () ) ) {
					// Display warning
						displayCashClientWarning ();
						// End method
						return;
				}
			
				// Set flag
//				displayPasscode = true;
//				// Build the description
//				String description = null;
//				if ( hasOverDuesCurrentDivision  )//&& ! ( hasOverDuesCurrentCompany && ! paidOnAccount )
//					description = AppResources.getInstance ( this ).getString ( this , R.string.sales_invoice_passcode_required_day );
//// 				else if ( ! hasOverDuesCurrentDivision  )//&& ( hasOverDuesCurrentCompany && ! paidOnAccount )
//// 					description = AppResources.getInstance ( this ).getString ( this , R.string.has_overdues_other_divisions );
////				else if ( hasOverDuesCurrentDivision  )//&& ( hasOverDuesCurrentCompany && ! paidOnAccount )
////					description = AppResources.getInstance ( this ).getString ( this , R.string.has_overdues_current_company );
//				else if ( paidCheck  )//&& ( hasOverDuesCurrentCompany && ! paidOnAccount )
//				description = AppResources.getInstance ( this ).getString ( this , R.string.has_overdues_check );
//				
//				
//				// Initialize the tertiary view
//				initializeTertiaryView ( description );
//				// Retrieve a reference to the tertiary view
//				View tertiaryView = findViewById ( R.id.layout_passcode );
//				// Display the tertiary view
//				tertiaryView.setVisibility ( View.VISIBLE );
//				// Animate the tertiary view
//				tertiaryView.startAnimation ( getViewAnimationIn() );
//				// Refresh the action bar
//				invalidateOptionsMenu ();
//				// Exit method
//				return;
			} // End if
    	} // End if
    	
		// Start the new activity
		startActivityForResult ( getIntent_SalesInvoiceDetailsActivity_New ( passcode ,null,seletedItems) , SalesInvoiceDetailsActivity.REQUEST_CODE_NEW );
		// Specify an explicit transition animation to perform next
		ActivityTransition.SlideOutLeft ( this );
    }
    
    /**
     * Gets an intent for the sales invoice details activity for a new sales invoice.<br>
     * The default implementation uses {@link me.SyncWise.Android.Modules.SalesInvoice.SalesInvoiceDetailsActivity SalesInvoiceDetailsActivity}.<br>
     * In order to used a custom adapter, override this method.<br>
	 * <b>Note : </b> This method should not return {@code NULL}.
     * 
     * @param passcode	String hosting any used pass code.
     * @return	Intent used to start the sales invoice details activity.
     */
    protected Intent getIntent_SalesInvoiceDetailsActivity_New ( final String passcode,final String cash ,final List < String > price) {
		// Create a new intent to start a new activity
		Intent intent = new Intent ( this , SalesInvoiceDetailsActivity.class );
		// Add the call object to the intent
		intent.putExtra ( SalesInvoiceDetailsActivity.CALL , getIntent ().getSerializableExtra ( CALL ) );
		// Add the visit object to the intent
		intent.putExtra ( SalesInvoiceDetailsActivity.VISIT , getIntent ().getSerializableExtra ( VISIT ) );
		// Add the request code to the intent
		intent.putExtra ( SalesInvoiceDetailsActivity.REQUEST_CODE , SalesInvoiceDetailsActivity.REQUEST_CODE_NEW );
		if ( cash != null )
		// Add the request code to the intent
		 intent.putExtra ( SalesInvoiceDetailsActivity.CASH , cash );
		// Check if the passcode is not null
		if ( passcode != null )
			// Add the request code to the intent
			intent.putExtra ( SalesInvoiceDetailsActivity.OTHER_PASSCODE , passcode );
		
		if(price != null){
			 String  selItemArray = new String ();
			  
		         for(String p:price)
		         {
		        	 selItemArray =selItemArray+p+",";
		        	 
		         }
		         selItemArray = selItemArray.substring(0, selItemArray.length()-1);
			// Add the request code to the intent
			 intent.putExtra ( SalesInvoiceDetailsActivity.PRICE , selItemArray );
		}
		// Return the computed intent
		return intent;
    }
    
    /**
     * Gets an intent for the sales invoice details activity for a previous (edit or view) sales invoice.<br>
     * The default implementation uses {@link me.SyncWise.Android.Modules.SalesInvoice.SalesInvoiceDetailsActivity SalesInvoiceDetailsActivity}.<br>
     * In order to used a custom adapter, override this method.<br>
	 * <b>Note : </b> This method should not return {@code NULL}.
     * 
     * @param transactionHeaderCode	String hosting the transaction header code.
     * @param isView	Boolean used to indicate if the previous transaction will be viewed or edited.
     * @return	Intent used to start the sales invoice details activity.
     */
    protected Intent getIntent_SalesInvoiceDetailsActivity_Previous ( final String transactionHeaderCode , final boolean isView ) {
		// Create a new intent to start a new activity
		Intent intent = new Intent ( this , SalesInvoiceDetailsActivity.class );
		// Add the call object to the intent
		intent.putExtra ( SalesInvoiceDetailsActivity.CALL , getIntent ().getSerializableExtra ( CALL ) );
		// Add the visit object to the intent
		intent.putExtra ( SalesInvoiceDetailsActivity.VISIT , getIntent ().getSerializableExtra ( VISIT ) );
		// Add the request code to the intent
		intent.putExtra ( SalesInvoiceDetailsActivity.REQUEST_CODE , SalesInvoiceDetailsActivity.REQUEST_CODE_INFO );
		// Add the transaction header code to the intent
		intent.putExtra ( SalesInvoiceDetailsActivity.TRANSACTION_HEADER_CODE , transactionHeaderCode );
		// Check if the is view flag is set
		if ( isView )
			// Add the view only flag to the intent
			intent.putExtra ( SalesInvoiceDetailsActivity.IS_VIEW_ONLY , true );
		else
			// Add the edit flag to the intent
			intent.putExtra ( SalesInvoiceDetailsActivity.IS_EDIT , true );
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

    	// Check if the sales invoice has been successfully deleted
    	if ( data.getBooleanExtra ( DELETE_SUCCESS , false ) ) {
			// Display baguette message
			Baguette.showText ( this , AppResources.getInstance ( this ).getString ( this , R.string.sales_invoice_delete_success_message ) , Baguette.BackgroundColor.GREEN );
			// Clear list adapter
			setListAdapter ( null );
			// Populate the list by setting a new adapter
			new PopulateList ().execute ();
			// Exit method
			return;
    	} // End if
    	
    	// Determine the provided request code
    	switch ( requestCode ) {
		case SalesInvoiceDetailsActivity.REQUEST_CODE_NEW:
			// Set flag
			saveSuccess = saveSuccess || data.getBooleanExtra ( SAVE_SUCCESS , false );
		case SalesInvoiceDetailsActivity.REQUEST_CODE_INFO:
			// Display baguette message
			Baguette.showText ( this ,
					AppResources.getInstance ( this ).getString ( this ,
							data.getBooleanExtra ( SAVE_SUCCESS , false ) ? R.string.sales_invoice_save_success_message : R.string.sales_invoice_save_failure_message ) ,
							data.getBooleanExtra ( SAVE_SUCCESS , false ) ? Baguette.BackgroundColor.GREEN : Baguette.BackgroundColor.RED );
			// Clear list adapter
			setListAdapter ( null );
			
			// Populate the list by setting a new adapter
			new PopulateList ().execute ();

			break;
		} // End switch
    	
	}

	/**
	 * Populates the sales invoice list by generating a map of cursors holding sales invoices, that belongs for a specific day, for the specified user.
	 * 
	 * @param context	The application context.
	 * @param call	The call object for which the sales invoices belong to.
	 * @return	Cursors holding sales invoices data set mapped to their appropriate dates.
	 */
	public static Map < String , Cursor > populateList ( final Context context , final Call call ) {
		// Compute the selection clause
		String selection = TransactionHeadersDao.Properties.ClientCode.columnName + " = ? AND " + TransactionHeadersDao.Properties.CompanyCode.columnName + " = ? AND " + TransactionHeadersDao.Properties.DivisionCode.columnName + " = ? AND " 
				+ TransactionHeadersDao.Properties.TransactionType.columnName + " = ? AND "
				+ TransactionHeadersDao.Properties.TransactionStatus.columnName + " = ?";
		// Compute the selection arguments
		String selectionArgs [] = new String [] {
				call.getClientDivision ().getClientCode () , call.getClientDivision ().getCompanyCode () , call.getClientDivision ().getDivisionCode () ,
				String.valueOf ( TransactionHeadersUtils.Type.SALES_INVOICE ) ,
				String.valueOf ( StatusUtils.isAvailable () )
		};
		// Compute the order by clause
		String orderBy = TransactionHeadersDao.Properties.IssueDate.columnName;
		// Declare and initialize a cursor in order to query DB
		Cursor cursor = DatabaseUtils.getInstance ( context ).getDaoSession ().getDatabase ()
				.query ( TransactionHeadersDao.TABLENAME , new String [] { TransactionHeadersDao.Properties.IssueDate.columnName } , selection , selectionArgs , null , null , orderBy );
		
		// Declare and initialize a list of calendars, used to compute the sales invoices dates
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
				+ TransactionHeadersDao.Properties.RemainingAmount.columnName + ","
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
	 * AsyncTask helper class used to populate the sales invoice items list.
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
			return populateList ( SalesInvoiceActivity.this , (Call) getIntent ().getSerializableExtra ( CALL ) );
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( Map < String , Cursor > cursors ) {
			// Compute the total number of sales invoices
			int totalSales = 0;
			// Iterate over all cursors
			for ( String date : cursors.keySet () )
				// Add the number of sales invoices
				totalSales += cursors.get ( date ).getCount ();
			// Set the action bar's subtitle
			if ( getActionBar () != null )
				getActionBar ().setSubtitle ( totalSales == 0 ? "" : AppResources.getInstance ( SalesInvoiceActivity.this ).getString ( SalesInvoiceActivity.this , R.string.total_of_label ) + " : " + totalSales );
			// Declare and initialize a multiple list adapter
			MultipleListAdapter adapter = new MultipleListAdapter ( SalesInvoiceActivity.this );
			// Iterate over all cursors
			for ( String date : cursors.keySet () )
				// Add a new sales invoice adapter using the current cursor
				adapter.addSection ( new Section ( date , null , Section.BackgroundType.COLOR , Color.argb ( 63 , 0 , 0 , 0 ) , null ) ,
						new SalesInvoiceAdapter ( SalesInvoiceActivity.this , cursors.get ( date ) , R.layout.sales_order_activity_item ) );
			// Set the list adapter
			setListAdapter ( adapter );
			// Restore the list view position (if any)
			if ( listViewPosition != null ) {
				listViewPosition.restore ( getListView () );
				listViewPosition = null;
			} // End if
		}
		
	}
    
}