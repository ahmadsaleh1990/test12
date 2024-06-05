/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.VehiclesStock;

import java.util.ArrayList;

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.ListViewPosition;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.ItemsDao;
import me.SyncWise.Android.Database.VehiclesStock;
import me.SyncWise.Android.Database.VehiclesStockDao;
import me.SyncWise.Android.Modules.Printing.PrintingActivity;
import me.SyncWise.Android.Modules.Printing.vehiclesStockPrinting;
import me.SyncWise.Android.Modules.SearchList.SearchListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.MergeCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CursorAdapter;
import android.widget.SearchView;
import android.widget.TextView;

/**
 * List Activity of all the vehicles stock with search capabilities and additional actions/details, in addition to the number of pending objectives.
 * 
 * @author Elias
 * @sw.todo	<b>Vehicles Stock List Activity Implementation :</b><br>
 * Simply add this class in the {@code AndroidManifest.xml} file.
 *
 */
public class VehiclesStockListActivity extends SearchListActivity {

	/**
	 * Bundle key used to put/retrieve the content of {@link #condition}.
	 */
	private static final String CONDITION = VehiclesStockListActivity.class.getName () + ".CONDITION";
	
	/**
	 * Integer used to hold the current condition for the vehicle stock list.<br>
	 * The condition can either imply to display all the stock or only the stock having non zero quantities.
	 */
	private Integer condition;
	
	/**
	 * Constant integer holding the condition value used to display all the stock (regardless the quantity).
	 */
	private static final int ALL_STOCK_CONDITION = 1;
	
	/**
	 * Constant integer holding the condition value used to display the valid stock (having valid quantities).
	 */	
	private static final int VALID_STOCK_CONDITION = 2;
	
	/**
	 * Constant integer holding the condition value used to display the low stock (having quantities below minimum).
	 */	
	private static final int LOW_STOCK_CONDITION = 3;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #promptCondition}.
	 */
	private static final String PROMPT_CONDITION = VehiclesStockListActivity.class.getName () + ".PROMPT_CONDITION";
	
	/**
	 * Boolean used to determine whether to prompt the user for a condition or not.
	 */
	private boolean promptCondition;
	
	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		// Define the properties which applies for a filter
		addProperty ( ItemsDao.Properties.ItemName );
		addProperty ( ItemsDao.Properties.ItemCode );
		
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state
			condition = savedInstanceState.getInt ( CONDITION );
			promptCondition = savedInstanceState.getBoolean ( PROMPT_CONDITION );
		} // End if
		// Check if the condition value is valid
		if ( condition == null )
			// Initialize the condition to all stock by default
			condition = ALL_STOCK_CONDITION;
		
		// Superclass method call
		super.onCreate ( savedInstanceState );
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.vehicles_stock_list_activity_title ) );
		// Enable fast scrolling
		getListView ().setFastScrollEnabled ( true );
		// Define the empty list view
		getListView ().setEmptyView ( findViewById ( R.id.empty_list_view ) );
		// Display empty list label
		( (TextView) findViewById ( R.id.empty_list_view ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_items_list_label ) );
	}
	
	/*
	 * Initializes the search list activity components.
	 *
	 * @see me.SyncWise.Android.Modules.SearchList.SearchListActivity#initializeComponents(android.os.Bundle)
	 */
	@Override
	protected void initializeComponents ( Bundle savedInstanceState ) {
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state
			setSearchQuery ( savedInstanceState.getString ( SEARCH_QUERY ) );
			setListViewPosition ( (ListViewPosition) savedInstanceState.getSerializable ( LIST_VIEW_POSITION ) );
		} // End if
		// Determine if the list view has a valid adapter
		if ( getListAdapter () == null ) {
			// Populate the list by setting a new adapter
			new PopulateList ().execute ();
		} // End if
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
    	// Display the currently displayed condition in the action bar
    	displayCondition ();
    	// Check if the user must select a condition
    	if ( promptCondition )
    		// Prompt the user
    		selectCondition ();
    }
    
    /**
     * Displays the currently selected condition in the action bar.
     */
    private void displayCondition () {
    	// Check if the condition is valid
    	if ( condition == null )
    		// Invalid condition
    		return;
    	// Declare and initialize a string used to host the condition description
    	String description = "";
    	// Determine the selected condition
    	switch ( condition ) {
		case ALL_STOCK_CONDITION:
			description = AppResources.getInstance ( this ).getString ( this , R.string.vehicles_stock_condition_all_items_message );
			break;
		case VALID_STOCK_CONDITION:
			description = AppResources.getInstance ( this ).getString ( this , R.string.vehicles_stock_condition_valid_items_message );
			break;
		case LOW_STOCK_CONDITION:
			description = AppResources.getInstance ( this ).getString ( this , R.string.vehicles_stock_condition_low_quantity_items_message );
			break;
		} // End switch
		// Display the appropriate subtitle
		getActionBar ().setSubtitle ( description );
    }
	
    /**
     * Prompts the user with the list of conditions for him/her to choose.
     */
    private void selectCondition () {
		// Build the conditions list
		String conditions [] = new String [] {
				AppResources.getInstance ( this ).getString ( this , R.string.vehicles_stock_condition_all_items_message ) ,
				AppResources.getInstance ( this ).getString ( this , R.string.vehicles_stock_condition_valid_items_message ) ,
				AppResources.getInstance ( this ).getString ( this , R.string.vehicles_stock_condition_low_quantity_items_message )
		};
		// Prompt the user to choose a condition
		AppDialog.getInstance ().displayList ( this ,
				AppResources.getInstance ( this ).getString ( this , R.string.filter_label ) ,
				conditions ,
				AppDialog.Cancelable.BACK_BUTTON_AND_TOUCH ,
				new DialogInterface.OnClickListener () {
					@Override
					public void onClick ( DialogInterface dialog , int which ) {
						// Store the previous condition
						int previousCondition = condition;
						// Update the current condition
						switch ( which ) {
						case 0:
							condition = ALL_STOCK_CONDITION;
							break;
						case 1:
							condition = VALID_STOCK_CONDITION;
							break;
						case 2:
							condition = LOW_STOCK_CONDITION;
							break;
						default:
							condition = ALL_STOCK_CONDITION;
							break;
						} // End switch
						// Check if the current and previous condition values are different
						if ( condition != previousCondition ) {
					    	// Display the new condition in the action bar
					    	displayCondition ();
							// Re-populate list view asynchronously
							new PopulateList ().execute ();
						} // End if
					}
				} ,
				new DialogInterface.OnCancelListener () {
					@Override
					public void onCancel ( DialogInterface dialog ) {
						// The user has chosen not to choose a condition
						// Clear the flag
						promptCondition = false;
					}
				});
    }
    
    /*
     * Called to retrieve per-instance state from an activity before being killed so that the state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle).
     *
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState ( Bundle outState ) {
    	// Superclass method call
    	super.onSaveInstanceState ( outState );
    	// Save the content of condition in the outState bundle
    	outState.putSerializable ( CONDITION , condition );
    	// Save the content of promptCondition in the outState bundle
    	outState.putSerializable ( PROMPT_CONDITION , promptCondition );
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
    	// Enable the required menu items
		me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_list ) );
		me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_print ) );
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
    	// Determine if the action list is selected
    	if ( menuItem.getItemId () == R.id.action_list ) {
    		// Prompt the user to choose a condition
    		selectCondition ();
    		// Consume event
    		return true;
    	} // End if
    	// Determine if the action print is selected
    	else if ( menuItem.getItemId () == R.id.action_print ) {
			// Prompt the user to print a copy
			AppDialog.getInstance ().displayAlert ( this ,
					null ,
					AppResources.getInstance ( this ).getString ( this , R.string.printing_activity_print_request_message ) ,
					AppDialog.ButtonsType.YesNo ,
					new DialogInterface.OnClickListener () {
						@Override
						public void onClick ( DialogInterface dialog , int which ) {
							// Determine the clicked button
							switch ( which ) {
							case DialogInterface.BUTTON_POSITIVE:
								// Print the transaction
								Intent intent = new Intent ( VehiclesStockListActivity.this, PrintingActivity.class );
								intent.putExtra ( PrintingActivity.TYPE , PrintingActivity.Type.VEHICULESSTOCK );
								intent.putExtra ( PrintingActivity.PRINTOUT , PrintingActivity.Printout.COPY );
								ArrayList < vehiclesStockPrinting > list = new ArrayList < vehiclesStockPrinting > ();
								String SQL = null;
						     	String selectionArguments [] = null;
								      SQL = " Select v.VehicleCode , v.VehicleName , items.ItemCode , items.ItemName,   cast ( case UnitMediumSmall when 1 then 0 else  " +
											" (COALESCE( VehiclesStock.goodQuantity,0)  - (cast( COALESCE( VehiclesStock.goodQuantity,0)as integer) %  " +
											" cast(   UnitMediumSmall as integer))) /UnitMediumSmall end as integer)  as Box  , " +
											" cast(case UnitMediumSmall when 1 then COALESCE(VehiclesStock.goodQuantity,0) else " +
											" (cast(COALESCE( VehiclesStock.goodQuantity,0)as integer) % " +
											" cast( COALESCE(UnitMediumSmall,1)as integer)) end as integer )  as Piece," +
											" cast ( case UnitMediumSmall when 1 then 0 else  " +
											" ( COALESCE( VehiclesStock.DamageQuantity,0)  - (cast( COALESCE( VehiclesStock.DamageQuantity,0)as integer) %  " +
											" cast(   UnitMediumSmall as integer))) /UnitMediumSmall end as integer)  as Box  , " +
											" cast(case UnitMediumSmall when 1 then COALESCE(VehiclesStock.DamageQuantity,0) else " +
											" (cast(COALESCE( VehiclesStock.DamageQuantity,0)as integer) % " +
											" cast( COALESCE(UnitMediumSmall,1)as integer)) end as integer )  as Piece   " +
											" from VehiclesStock    inner join Items on Items.ItemCode = VehiclesStock .ItemCode  " +
											" inner join Vehicles v    on v.VehicleCode=VehiclesStock.VehicleCode where GoodQuantity >  0 or DamageQuantity > 0 ";
								selectionArguments = new String [] {	   
									 	 
								 		};
								Cursor cursor = DatabaseUtils.getInstance (VehiclesStockListActivity.this ).getDaoSession ().getDatabase ()
										        .rawQuery(SQL , selectionArguments) ;
								
									// Move the cursor to the first raw
								if ( cursor.moveToFirst () ) {
									// Iterate over all raws
									do {
										vehiclesStockPrinting v=new vehiclesStockPrinting();
									 	v.setVehicleCode ( cursor.getString(0) );
										v.setVehicleName( cursor.getString(1) );
										v.setItemCode(cursor.getString(2));
										v.setItemName(cursor.getString(3));
										v.setGooodquantityMedium(cursor.getDouble(4));
										v.setGoodquantitySmall(cursor.getDouble(5));
										v.setDamageQuantityMedium(cursor.getDouble(6));
										v.setDamageQuantitySmall(cursor.getDouble(7));
										list.add(v);
										} while ( cursor.moveToNext () );
								} // End if
								// Close the cursor
								cursor.close ();
								cursor = null;
								
								//						list.addAll ( DatabaseUtils.getInstance ( VehiclesStockListActivity.this ).getDaoSession ().getVehiclesStockDao ().queryBuilder ()
			//							.whereOr ( VehiclesStockDao.Properties.GoodQuantity.gt ( 0 ) , VehiclesStockDao.Properties.DamageQuantity.gt ( 0 ) ).list () );
								intent.putExtra ( PrintingActivity.HEADER , list );
								startActivity ( intent );
								break;
							case DialogInterface.BUTTON_NEGATIVE:
								// Dismiss dialog
								AppDialog.getInstance ().dismiss ();
								break;
							} // End switch
						} } );
    		// Consume event
    		return true;
    	} // End if
    	// Allow normal menu processing to proceed
    	return false;
    }
    
	/**
	 * Computes the SQL search query based on the provided filter criteria.
	 * 
	 * @return	String holding the SQL search query.
	 */
	protected String computeSearchQuery () {
		// Declare and initialize a string to host the search SQL query
		StringBuilder _searchQuery = new StringBuilder ();
		// Check if the search criteria is valid and if there is at least one property to filter
		if ( ! TextUtils.isEmpty ( getSearchQuery () ) && getPropertiesCount () != 0 ) {
			// Escape the character ' to protect from SQL injection
			String searchQuery = getSearchQuery ().replace ( "'" , "''" );
			// Build the first search condition out of the first property
			_searchQuery.append ( "I. " ).append ( getProperty ( 0 ).columnName ).append ( " LIKE '%" ).append ( searchQuery ).append ( "%'" );
			// Iterate over the remaining properties
			for ( int i = 1 ; i < getPropertiesCount () ; i ++ )
				// Build the remaining conditions
				_searchQuery.append ( " OR I." ).append ( getProperty ( i ).columnName ).append ( " LIKE '%" ).append ( searchQuery ).append ( "%'" );
		} // End if
		// Return the computed search query
		return _searchQuery.toString ();
	}
	
	/*
	 * Called when the query text is changed by the user.
	 *
	 * @see me.SyncWise.Android.Modules.SearchList.SearchListActivity#onQueryTextChange(java.lang.String)
	 */
	@Override
	public boolean onQueryTextChange ( String newText ) {
		// Update the new search query
		setSearchQuery ( newText );
		// Filter the list asynchronously
		new FilterList ( newText ).execute ();
		// Indicate that the action was handled by the listener
		return true;
	}
	
	/**
	 * Computes an appropriate cursor based on the provided table, selection and filter.
	 * 
	 * @return	Cursor	Cursor holding a result set returned by a database query.
	 */
	protected Cursor computeCursor () {
		// Compute the SQL string
		String SQL = "SELECT * FROM " + ItemsDao.TABLENAME + " I INNER JOIN " + VehiclesStockDao.TABLENAME + " VS " +
				"ON I." + ItemsDao.Properties.ItemCode.columnName + " = VS." + VehiclesStockDao.Properties.ItemCode.columnName;
		
		// Compute the 'order by' statement
		String orderBy = " ORDER BY I." + ItemsDao.Properties.ItemName.columnName + " COLLATE NOCASE ASC";
		
		// Compute the SQL search query
		String _searchQuery = computeSearchQuery ();
		// Compute the selection statement
		String selection = "";
		// Determine if the VALID STOCK condition is selected
		// AND NO search query is provided
		if ( TextUtils.isEmpty ( _searchQuery ) && condition == VALID_STOCK_CONDITION )
			selection = " WHERE " + VehiclesStockDao.Properties.GoodQuantity.columnName + " > 0";
		// Otherwise determine if the VALID STOCK condition is selected
		// AND a search query is provided
		else if ( ! TextUtils.isEmpty ( _searchQuery ) && condition == VALID_STOCK_CONDITION )
			selection = " WHERE " + VehiclesStockDao.Properties.GoodQuantity.columnName + " > 0 AND ( " + _searchQuery + " )";
		
		// Otherwise determine if the LOW STOCK condition is selected
		// AND NOT search query is provided
		else if ( TextUtils.isEmpty ( _searchQuery ) && condition == LOW_STOCK_CONDITION )
			selection = " WHERE " + VehiclesStockDao.Properties.GoodQuantity.columnName + " <= " + ItemsDao.Properties.MinimumQuantity.columnName;
		// Otherwise determine if the LOW STOCK condition is selected
		// AND a search query is provided
		else if ( TextUtils.isEmpty ( _searchQuery ) && condition == LOW_STOCK_CONDITION )
			selection = " WHERE " + VehiclesStockDao.Properties.GoodQuantity.columnName + " <= " + ItemsDao.Properties.MinimumQuantity.columnName +
				" AND ( " + _searchQuery + " )";
			
		// Otherwise determine if the ALL STOCK condition is selected
		// AND a search query is provided
		else if ( ! TextUtils.isEmpty ( _searchQuery ) && condition == ALL_STOCK_CONDITION )
			selection = " WHERE " + _searchQuery;
		// Otherwise, there is no selection statement
		
		// Compute the selection arguments
		String selectionArguments [] = new String [] {};
		
		// Declare and initialize a cursor in order to query DB
		Cursor cursor = DatabaseUtils.getInstance ( VehiclesStockListActivity.this ).getDaoSession ().getDatabase ()
				.rawQuery ( SQL + selection + orderBy , selectionArguments );
		
		// Retrieve the cursor size
		int size = cursor.getCount ();
		// Check if the cursor size exceeds 1000 rows
		if ( size > 1000 ) {
			// Close the cursor
			cursor.close ();
			cursor = null;
			// Determine how many cursors (one cursor for every group of 1000 rows) are needed
			int number = (int) Math.ceil ( size / 1000.0 );
			// Declare and initialize an array of cursors
			Cursor cursors [] = new Cursor [ number ];
			// Iterate over the array of cursors
			for ( int i = 0 ; i < number ; i ++ )
				// Initialize the current cursor with the appropriate limit / offset
				cursors [ i ] = DatabaseUtils.getInstance ( VehiclesStockListActivity.this ).getDaoSession ().getDatabase ()
					.rawQuery ( SQL + selection + orderBy + " LIMIT " + ( i * 1000 ) + ",1000" , selectionArguments );
			
			// Merge the array of cursors and return the result
			return new MergeCursor ( cursors );
		} // End if

		// Otherwise the cursor size does not exceed 1000 rows
		// Simply return the cursor
		return cursor;
	}
	
	/**
	 * AsyncTask helper class used to populate the list.
	 * 
	 * @author Elias
	 *
	 */
	private class PopulateList extends AsyncTask < Void , Void , Cursor > {
		
		/*
		 * Runs on the UI thread before doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute () {
			// Display indeterminate progress dialog
			AppDialog.getInstance ().displayIndeterminateProgress ( VehiclesStockListActivity.this , null , null );
		}
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Cursor doInBackground ( Void ... params ) {
			// Compute and return a new cursor
			return computeCursor ();
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( Cursor cursor ) {
			// Dismiss dialog
			AppDialog.getInstance ().dismiss ();
			// Set the new adapter
			setListAdapter ( new VehiclesStockCursorAdapter ( VehiclesStockListActivity.this , cursor , R.layout.item_item ) );
			// Restore the list view position (if any)
			if ( getListViewPosition () != null ) {
				getListViewPosition ().restore ( getListView () );
				setListViewPosition ( null );
			} // End if
			// Enable the search view
			( (SearchView) findViewById ( R.id.search_view ) ).setEnabled ( true );
		}
		
	}
	
	/**
	 * AsyncTask helper class used to filter the list.
	 * 
	 * @author Elias
	 *
	 */
	private class FilterList extends AsyncTask < Void , Void , Cursor > {
		
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
    	 * Runs on the UI thread before doInBackground(Params...).
    	 * 
    	 * @see android.os.AsyncTask#onPreExecute()
    	 */
    	@Override
    	protected void onPreExecute () {
			// Set the visibility of the indeterminate progress bar in the title
			setProgressBarIndeterminateVisibility ( true );
    	}
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Cursor doInBackground ( Void ... params ) {
			// Compute and return a new cursor
			return computeCursor ();
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( Cursor cursor ) {
			// Set the visibility of the indeterminate progress bar in the title
			setProgressBarIndeterminateVisibility ( false );
			// Check if the search query has changed
			if ( ! getSearchQuery ().equals ( searchQuery ) )
				// Do nothing, as another AsyncTask is executing and will return the appropriate result
				return;
			// Otherwise, determine if the list adapter is valid
			if ( getListAdapter () == null )
				// Invalid list adapter
				return;
			// Check if the list adapter is a CursorAdapter
			if ( ! ( getListAdapter () instanceof CursorAdapter ) )
				// Invalid list adapter
				return;
			// Set the new cursor
			( (CursorAdapter) getListAdapter () ).changeCursor ( cursor );
		}
		
	}
	
}