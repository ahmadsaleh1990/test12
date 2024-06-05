/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.ClientsList;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.ListViewPosition;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.AreaLevels;
import me.SyncWise.Android.Database.AreaLevelsDao;
import me.SyncWise.Android.Database.Areas;
import me.SyncWise.Android.Database.AreasDao;
import me.SyncWise.Android.Database.ClientAreasDao;
import me.SyncWise.Android.Database.ClientDivisionsDao;
import me.SyncWise.Android.Database.ClientPropertiesDao;
import me.SyncWise.Android.Database.ClientPropertyLevels;
import me.SyncWise.Android.Database.ClientPropertyValues;
import me.SyncWise.Android.Database.ClientPropertyValuesDao;
import me.SyncWise.Android.Database.ClientsDao;
import me.SyncWise.Android.Database.CompaniesDao;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.DivisionsDao;
import me.SyncWise.Android.Modules.ClientsList.ClientFilterAdapter.ChildViewHolder;
import me.SyncWise.Android.Modules.SearchList.SearchCursorListActivity;
import me.SyncWise.Android.Utilities.ActivityInstance;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * List Activity of all the clients with search capabilities.
 * 
 * @author Elias
 * @sw.todo	<b>Clients List Activity Implementation :</b><br>
 * Simply add this class in the {@code AndroidManifest.xml} file.
 *
 */
public class ClientsListActivity extends SearchCursorListActivity {

	/**
	 * Bundle key used to put/retrieve the content of {@link #displayFilter}.
	 */
	private static final String DISPLAY_FILTER = ClientsListActivity.class.getName () + ".DISPLAY_FILTER";
	
	/**
	 * Boolean used to determine whether to display the clients filter or not.
	 */
	protected boolean displayFilter;

	/**
	 * Bundle key used to put/retrieve the content of {@link #expanded}.
	 */
	private static final String EXPANDED = ClientsListActivity.class.getName () + ".GROUPS_EXPANDED";	
	
	/**
	 * An array of booleans used to store the expanded state of each group in the client filter expandable list.
	 */
	private boolean expanded [];
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #expandableListViewPosition}.
	 */
	private static final String EXPANDABLE_LIST_VIEW_POSITION = ClientsListActivity.class.getName () + ".LIST_VIEW_POSITION";
	
	/**
	 * Object used to save/restore the expandable list views' position.
	 */
	private ListViewPosition expandableListViewPosition;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #clientPropertyFilter}.
	 */
	private static final String CLIENT_PROPERTY_FILTER = ClientsListActivity.class.getName () + ".CLIENT_PROPERTY_FILTER";
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.ClientsList.ClientPropertyFilter ClientPropertyFilter} objects used to define the client property filters along with their selections.
	 */
	private ArrayList < ClientPropertyFilter > clientPropertyFilter;
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #clientAreaFilter}.
	 */
	private static final String CLIENT_AREA_FILTER = ClientsListActivity.class.getName () + ".CLIENT_AREA_FILTER";
	
	/**
	 * List of {@link me.SyncWise.Android.Modules.ClientsList.ClientAreaFilter ClientAreaFilter} objects used to define the client area filters along with their selections.
	 */
	private ArrayList < ClientAreaFilter > clientAreaFilter;
	
	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		// Define the main query
		setMainQuery ( "SELECT C.* , CO.CompanyName , D.DivisionCode , D.DivisionName " +
				"FROM " + ClientsDao.TABLENAME + " C " +
				"INNER JOIN " + CompaniesDao.TABLENAME + " CO ON C.CompanyCode = CO.CompanyCode " +
				"INNER JOIN " + ClientDivisionsDao.TABLENAME + " CD ON C.ClientCode = CD.ClientCode AND C.CompanyCode = CD.CompanyCode " +
				"INNER JOIN " + DivisionsDao.TABLENAME + " D ON D.DivisionCode = CD.DivisionCode AND D.CompanyCode = CD.CompanyCode" );
		// Define the selection
		setSelection ( "C." + ClientsDao.Properties.ParentCode.columnName + " IS NOT NULL "  );
		// Define the list order
		setOrder ( "C." + ClientsDao.Properties.ClientName.columnName + " COLLATE NOCASE ASC" );
		// Define the properties which applies for a filter
		setPropertyPrefix ( "C." );
		addProperty ( ClientsDao.Properties.ClientName );
		addProperty ( ClientsDao.Properties.ClientCode );
		// Set the activity content from a layout resource.
		setLayoutResourceID ( R.layout.clients_list_activity );
		// Superclass method call
		super.onCreate ( savedInstanceState );
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.clients_list_activity_title ) );
		// Enable fast scrolling
		getListView ().setFastScrollEnabled ( true );
		// Define the empty list view
		getListView ().setEmptyView ( findViewById ( R.id.empty_list_view ) );
		// Display empty list label
		( (TextView) findViewById ( R.id.empty_list_view ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_clients_list_label ) );
		
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			displayFilter = savedInstanceState.getBoolean ( DISPLAY_FILTER );
			expandableListViewPosition = (ListViewPosition) savedInstanceState.getSerializable ( EXPANDABLE_LIST_VIEW_POSITION );
			expanded = savedInstanceState.getBooleanArray ( EXPANDED );
			if ( savedInstanceState.getBoolean ( CLIENT_PROPERTY_FILTER ) )
				clientPropertyFilter = (ArrayList < ClientPropertyFilter >) ActivityInstance.readDataGson ( this , ClientsListActivity.class.getName () , CLIENT_PROPERTY_FILTER , new TypeToken < ArrayList < ClientPropertyFilter > > () {}.getType () );
			if ( savedInstanceState.getBoolean ( CLIENT_AREA_FILTER ) )
				clientAreaFilter = (ArrayList < ClientAreaFilter >) ActivityInstance.readDataGson ( this , ClientsListActivity.class.getName () , CLIENT_AREA_FILTER , new TypeToken < ArrayList < ClientAreaFilter > > () {}.getType () );
		} // End if
		
		// Determine if the client filter should be displayed
		if ( displayFilter ) {
			// Display the client filter list secondary view
			findViewById ( R.id.layout_client_filter_list ).setVisibility ( View.VISIBLE );
			// Properly initialize the secondary list
			initializeSecondaryView ( true );
		} // End if
		else
			// Hide the client filter list secondary view
			findViewById ( R.id.layout_client_filter_list ).setVisibility ( View.GONE );
	}

	/*
	 * Builds a ListAdapter that will be set to the list.
	 *
	 * @see me.SyncWise.Android.Modules.SearchList.SearchCursorListActivity#populateList(android.database.Cursor)
	 */
	@Override
	protected ListAdapter populateList ( Cursor cursor ) {
		// Initialize and return a client cursor adapter using the provided cursor
		return new ClientsCursorAdapter ( this , cursor , R.layout.client_item );
	}
	
	/**
	 * Computes the client filter selection <em>(WHERE statement)</em> based on the selected filters.
	 */
	protected void computeClientFilterSelection () {
		try {
			// Declare and initialize the SQL property selection statement
			StringBuilder propertySelectionBuilder = new StringBuilder ();
			// Declare and initialize the SQL property selection arguments list
			ArrayList < String > propertyArguments = new ArrayList < String > ();
			// Check if the client property filter is valid
			if ( clientPropertyFilter != null && ! clientPropertyFilter.isEmpty () ) {
				// Iterate over all client property filters
				for ( ClientPropertyFilter _clientPropertyfilter : clientPropertyFilter ) {
					// Compute the total number of selected client properties
					int selectedClientPropertiesCount = _clientPropertyfilter.getSelectionCount ();
					// Check if all the selected filters are accessed
					if ( selectedClientPropertiesCount == 0 )
						// Do nothing
						continue;
					// Otherwise there is at least one selected client property
					// Initialize the selection statement
					propertySelectionBuilder.append ( propertySelectionBuilder.length () == 0 ? "( CD." : " AND CD." );
					propertySelectionBuilder.append ( ClientDivisionsDao.Properties.ClientCode.columnName );
					propertySelectionBuilder.append ( " || '--' || CD." );
					propertySelectionBuilder.append ( ClientDivisionsDao.Properties.CompanyCode.columnName );
					propertySelectionBuilder.append ( " || '--' || CD." );
					propertySelectionBuilder.append ( ClientDivisionsDao.Properties.DivisionCode.columnName );
					propertySelectionBuilder.append ( " IN ( SELECT " );
					propertySelectionBuilder.append ( ClientPropertiesDao.Properties.ClientCode.columnName );
					propertySelectionBuilder.append ( " || '--' || " );
					propertySelectionBuilder.append ( ClientPropertiesDao.Properties.CompanyCode.columnName );
					propertySelectionBuilder.append ( " || '--' || " );
					propertySelectionBuilder.append ( ClientPropertiesDao.Properties.DivisionCode.columnName );
					propertySelectionBuilder.append ( " FROM " );
					propertySelectionBuilder.append ( ClientPropertiesDao.TABLENAME );
					propertySelectionBuilder.append ( " WHERE " );
					propertySelectionBuilder.append ( ClientPropertiesDao.Properties.ClientPropertyValueCode.columnName );
					propertySelectionBuilder.append ( " IN (" );
					// Iterate over all the filter values
					for ( int i = 0 ; i < _clientPropertyfilter.getCount () ; i ++ ) {
						// Check if all the selected filters are accessed
						if ( selectedClientPropertiesCount == 0 )
							// Do nothing
							break;
						// Check if the current filter is selected
						if ( _clientPropertyfilter.isFilterSelected ( i ) ) {
							// Add the current filter to the selection
							propertySelectionBuilder.append ( '?' );
							propertyArguments.add ( ( (ClientPropertyValues) _clientPropertyfilter.getChild ( i ) ).getClientPropertyValueCode () );
							// Decrement the number of remaining filters
							selectedClientPropertiesCount --;
							// Check if the current filter is the last one
							if ( selectedClientPropertiesCount != 0 )
								// There is at least one remaining filter
								propertySelectionBuilder.append ( ',' );
							else
								// The current filter is the last one, close the selection statement
								propertySelectionBuilder.append ( ") )" );
						} // End if
					} // End for loop
				} // End for loop
				// Properly close the selection statement
				if ( propertySelectionBuilder.length () !=0 )
					propertySelectionBuilder.append ( " )" );
			}  // End if
			
			// Declare and initialize the SQL area selection statement
			StringBuilder areaSelectionBuilder = new StringBuilder ();
			// Declare and initialize the SQL area selection arguments list
			ArrayList < String > areaArguments = new ArrayList < String > ();
			// Check if the client area filter is valid
			if ( clientAreaFilter != null && ! clientAreaFilter.isEmpty () ) {
				// Iterate over all client area filters
				for ( ClientAreaFilter _clientAreaFilter : clientAreaFilter ) {
					// Compute the total number of selected client areas
					int selectedClientAreasCount = _clientAreaFilter.getSelectionCount ();
					// Check if all the selected filters are accessed
					if ( selectedClientAreasCount == 0 )
						// Do nothing
						continue;
					// Otherwise there is at least one selected client area
					// Initialize the selection statement
					areaSelectionBuilder.append ( areaSelectionBuilder.length () == 0 ? "( CD." : " AND CD." );
					areaSelectionBuilder.append ( ClientDivisionsDao.Properties.ClientCode.columnName );
					areaSelectionBuilder.append ( " || '--' || CD." );
					areaSelectionBuilder.append ( ClientDivisionsDao.Properties.CompanyCode.columnName );
					areaSelectionBuilder.append ( " || '--' || CD." );
					areaSelectionBuilder.append ( ClientDivisionsDao.Properties.DivisionCode.columnName );
					areaSelectionBuilder.append ( " IN ( SELECT " );
					areaSelectionBuilder.append ( ClientAreasDao.Properties.ClientCode.columnName );
					areaSelectionBuilder.append ( " || '--' || " );
					areaSelectionBuilder.append ( ClientAreasDao.Properties.CompanyCode.columnName );
					areaSelectionBuilder.append ( " || '--' || " );
					areaSelectionBuilder.append ( ClientAreasDao.Properties.DivisionCode.columnName );
					areaSelectionBuilder.append ( " FROM " );
					areaSelectionBuilder.append ( ClientAreasDao.TABLENAME );
					areaSelectionBuilder.append ( " WHERE " );
					areaSelectionBuilder.append ( ClientAreasDao.Properties.AreaCode.columnName );
					areaSelectionBuilder.append ( " IN (" );
					// Iterate over all the filter values
					for ( int i = 0 ; i < _clientAreaFilter.getCount () ; i ++ ) {
						// Check if all the selected filters are accessed
						if ( selectedClientAreasCount == 0 )
							// Do nothing
							break;
						// Check if the current filter is selected
						if ( _clientAreaFilter.isFilterSelected ( i ) ) {
							// Add the current filter to the selection
							areaSelectionBuilder.append ( '?' );
							areaArguments.add ( ( (Areas) _clientAreaFilter.getChild ( i ) ).getAreaCode () );
							// Decrement the number of remaining filters
							selectedClientAreasCount --;
							// Check if the current filter is the last one
							if ( selectedClientAreasCount != 0 )
								// There is at least one remaining filter
								areaSelectionBuilder.append ( ',' );
							else
								// The current filter is the last one, close the selection statement
								areaSelectionBuilder.append ( ") )" );
						} // End if
					} // End for loop
				} // End for loop
				// Properly close the selection statement
				if ( areaSelectionBuilder.length () !=0 )
					areaSelectionBuilder.append ( " )" );
			} // End if
			
			// Declare and initialize the SQL selection statement
			StringBuilder selectionBuilder = new StringBuilder ( "C." + ClientsDao.Properties.ParentCode.columnName + " IS NOT NULL AND C." + ClientsDao.Properties.CompanyCode.columnName + " = ?" );
			// Declare and initialize the SQL selection arguments list
			ArrayList < String > arguments = new ArrayList < String > ();
			arguments.add ( DatabaseUtils.getCurrentCompanyCode ( this ) );
			// Merge all the selection statements
			if ( propertySelectionBuilder.length () != 0 )
				selectionBuilder.append ( selectionBuilder.length () == 0 ? propertySelectionBuilder.toString () : "AND" + propertySelectionBuilder.toString () );
			if ( areaSelectionBuilder.length () != 0 )
				selectionBuilder.append ( selectionBuilder.length () == 0 ? areaSelectionBuilder.toString () : "AND" + areaSelectionBuilder.toString () );
			// Add all the arguments
			arguments.addAll ( propertyArguments );
			arguments.addAll ( areaArguments );
			// Set the new selection
			setSelection ( selectionBuilder.toString () );
			setSelectionArguments ( arguments );
		} catch ( Exception exception ) {
			// An error occurred, apply default selection
			setSelection ( "C." + ClientsDao.Properties.ParentCode.columnName + " IS NOT NULL "  );
		} // End of try-catch block
	}
	
    /*
     * Called to retrieve per-instance state from an activity before being killed so that the state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle).
     *
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState ( Bundle outState ) {
    	// Superclass method call
    	super.onSaveInstanceState ( outState );
    	// Save the content of displayFilter in the outState bundle
    	outState.putBoolean ( DISPLAY_FILTER , displayFilter );
    	
    	// Retrieve a reference to the expandable list view
    	ExpandableListView expandableListView = (ExpandableListView) findViewById ( R.id.expandablelistview_client_filter );
    	// Check if the client filter list is displayed
    	if ( displayFilter ) {
    		// Compute the total number of groups
    		int size = expandableListView.getExpandableListAdapter ().getGroupCount ();
    		// Initialize the array of booleans used to store the expanded state of each group
    		expanded = new boolean [ size ];
    		// Iterate over the array
    		for ( int i = 0 ; i < size ; i ++ )
    			// Determine and store the group expanded state
    			expanded [ i ] = expandableListView.isGroupExpanded ( i );
    		// Save the groups expanded state
    		outState.putBooleanArray ( EXPANDED , expanded );
    	} // End if
    	
    	// Save the content of the client property filter using GSON
		ActivityInstance.saveDataGson ( this , ClientsListActivity.class.getName () , CLIENT_PROPERTY_FILTER , clientPropertyFilter );
		// Indicate that there is client property filter data
		outState.putBoolean ( CLIENT_PROPERTY_FILTER , true );
		
    	// Save the content of the client area filter using GSON
		ActivityInstance.saveDataGson ( this , ClientsListActivity.class.getName () , CLIENT_AREA_FILTER , clientAreaFilter );
		// Indicate that there is client area filter data
		outState.putBoolean ( CLIENT_AREA_FILTER , true );
    }
    
	/*
	 * Called when the activity has detected the user's press of the back key. 
	 *
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed () {
		// Check if the filter (secondary view) is displayed
		if ( displayFilter )
			// Simply hide the filer screen (cancel the secondary view)
			cancelSecondaryView ( null );
		else
			// Otherwise the filter (secondary view) is NOT displayed
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
			clientPropertyFilter = null;
			clientAreaFilter = null;
			expandableListViewPosition= null;
		} // End if
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
		// Determine if the client filter list is currently displayed
		if ( ! displayFilter )
	    	// Enable the required menu items
	    	me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_list ) , R.string.filter_label );
		else {
			// Enable the required menu items
			me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_save ) , R.string.save_label );
			me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_unmark_all ) , R.string.unmark_label );
		} // End else
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
    		// Update flag
    		displayFilter = true;
    		// Refresh the menu items in the action bar
    		invalidateOptionsMenu ();
    		// Initialize the secondary view
    		initializeSecondaryView ( false );
    		// Retrieve a reference to the secondary client filter list view
    		View itemsList = findViewById ( R.id.layout_client_filter_list );
    		// Display the secondary client filter list view
    		itemsList.setVisibility ( View.VISIBLE );
    		// Start an animation to the secondary client filter list view
    		itemsList.startAnimation ( getViewAnimationIn () );
    		// Consume event
    		return true;
    	} // End if
    	// Determine if the action save is selected
    	else if ( menuItem.getItemId () == R.id.action_save ) {
    		// Retrieve a reference to the parent layout
    		FrameLayout parent = (FrameLayout) findViewById ( R.id.layout_client_filter_list );
    		// Retrieve a reference to the cancel view icon
    		View cancelView = parent.findViewById ( R.id.icon_cancel_secondary_view );
			// Disable the cancel view icon
    		cancelView.setEnabled ( false );
			// Enable the main list view
			getListView ().setEnabled ( true );
    		// Update the flag
    		displayFilter = false;
    		// Refresh the menu items in the action bar
    		invalidateOptionsMenu ();
    		
    		// Compute the client filter and refresh the list asynchronously
    		new ClientFilterList ().execute ();
    		
    		// Retrieve a reference to the secondary client filter list view
    		View _view = findViewById ( R.id.layout_client_filter_list );
			// Retrieve the slide out animation
			Animation out = getViewAnimationOut ( _view );
    		// Start an animation to the secondary client filter list view
			_view.startAnimation ( out );
    		// Consume event
    		return true;
    	} // End else if
    	// Determine if the action unmark all is selected
    	else if ( menuItem.getItemId () == R.id.action_unmark_all ) {
    		// Retrieve a reference of the expandable list view
    		ExpandableListView expandableListView = (ExpandableListView) findViewById ( R.id.expandablelistview_client_filter );
    		// Retrieve a reference to the client filter adapter
    		ClientFilterAdapter clientFilterAdapter = (ClientFilterAdapter) expandableListView.getExpandableListAdapter ();
    		// Check if the adapter is valid
    		if ( clientFilterAdapter == null )
        		// Consume event
        		return true;
    		// Iterate over all group items
    		int size = clientFilterAdapter.getGroupCount ();
    		for ( int i = 0 ; i < size ; i ++ )
    			// Clear the current client filter selections
    			( (ClientFilter) clientFilterAdapter.getGroup ( i ) ).clear ();
    		// Refresh the list
    		clientFilterAdapter.notifyDataSetChanged ();
    		// Consume event
    		return true;
    	} // End else if
    	// Allow normal menu processing to proceed
    	return false;
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
	 * Initializes the client filter list (secondary) view.
	 * 
	 * @param restore	Boolean used to determine if the secondary view is being initialized for the first time or is being restored.
	 */
	protected void initializeSecondaryView ( final boolean restore ) {
		// Retrieve a reference to the parent layout
		FrameLayout parent = (FrameLayout) findViewById ( R.id.layout_client_filter_list );
		// Retrieve a reference to the expandable list view
		ExpandableListView expandablelistView = (ExpandableListView) parent.findViewById ( R.id.expandablelistview_client_filter );
		// Retrieve a reference to the cancel view icon
		final View cancelView = parent.findViewById ( R.id.icon_cancel_secondary_view );
		
		// Disable the main list view
		getListView ().setEnabled ( false );
		// Enable the cancel view icon
		cancelView.setEnabled ( true );
		// Assign a click listener to the cancel view icon
		cancelView.setOnClickListener ( new OnClickListener () {
			@Override
			public void onClick ( View view ) {
				// Cancel the secondary view
				cancelSecondaryView ( cancelView );
			}
		} );
		
		// Enable fast scrolling
		expandablelistView.setFastScrollEnabled ( true );
		// Populate the client filter list asynchronously
		new PopulateSecondaryList ( restore ).execute ();
		// Register a callback to be invoked when a child item in this AdapterView has been clicked
		expandablelistView.setOnChildClickListener ( new OnChildClickListener () {
			@Override
			public boolean onChildClick ( ExpandableListView parent , View view , int groupPosition , int childPosition , long id ) {
				// Toggle the current client filter and update the check box
				( (ChildViewHolder) view.getTag () ).checkBox.setChecked (
						( (ClientFilter) ( (ClientFilterAdapter) parent.getExpandableListAdapter () ).getGroup ( groupPosition ) ).toggleFilter ( childPosition ) );
				// Indicate that the click was handled
				return true;
			}
		} );
	}
	
	/**
	 * Cancels and hides the secondary view.
	 * 
	 * @param cancelView	A {@link android.view.View View} reference to the cancel view icon
	 */
	protected void cancelSecondaryView ( View cancelView ) {
		// Enable the main list view
		getListView ().setEnabled ( true );
		// Check if the cancel view is valid
		if ( cancelView == null )
			// Retrieve a reference to the cancel view icon
			cancelView = findViewById ( R.id.icon_cancel_secondary_view );
		// Disable the current view
		cancelView.setEnabled ( false );
		// Update the flag
		displayFilter = false;
		// Refresh the menu items in the action bar
		invalidateOptionsMenu ();
		
		// Retrieve a reference of the expandable list view
		ExpandableListView expandableListView = (ExpandableListView) findViewById ( R.id.expandablelistview_client_filter );
		// Retrieve a reference to the client filter adapter
		ClientFilterAdapter clientFilterAdapter = (ClientFilterAdapter) expandableListView.getExpandableListAdapter ();
		// Check if the adapter is valid
		if ( clientFilterAdapter != null ) {
    		// Iterate over all group items
    		int size = clientFilterAdapter.getGroupCount ();
    		for ( int i = 0 ; i < size ; i ++ )
    			// Restore the client filter selections
    			( (ClientFilter) clientFilterAdapter.getGroup ( i ) ).restore ();
		} // End if
		
		// Retrieve a reference to the secondary client filter list view
		View _view = findViewById ( R.id.layout_client_filter_list );
		// Retrieve the slide out animation
		Animation out = getViewAnimationOut ( _view );
		// Start an animation to the secondary client filter list view
		_view.startAnimation ( out );
	}
    
	/**
	 * AsyncTask helper class used to populate the secondary list.
	 * 
	 * @author Elias
	 *
	 */
	private class PopulateSecondaryList extends AsyncTask < Void , Void , Void > {
		
		/**
		 * Boolean used to determine if the secondary view is being initialized for the first time or is being restored.
		 */
		private final boolean restore;
		
		/**
		 * Constructor.
		 * 
		 * @param restore	Boolean used to determine if the secondary view is being initialized for the first time or is being restored.
		 */
		public PopulateSecondaryList ( final boolean restore ) {
			// Initialize attribute
			this.restore = restore;
		}
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground ( Void ... params ) {
			// Check if the client property filter list is defined
			if ( clientPropertyFilter == null ) {
				// Initialize the list
				clientPropertyFilter = new ArrayList < ClientPropertyFilter > ();
				// Retrieve all the client property levels
				List < ClientPropertyLevels > clientPropertyLevels = DatabaseUtils.getInstance ( ClientsListActivity.this ).getDaoSession ().getClientPropertyLevelsDao ().loadAll ();
				// Iterate over all the client property levels
				for ( ClientPropertyLevels clientPropertyLevel : clientPropertyLevels ) {
					// Retrieve all the client property values of the current level
					ArrayList < ClientPropertyValues > clientPropertyValues = new ArrayList < ClientPropertyValues > ();
					clientPropertyValues.addAll ( DatabaseUtils.getInstance ( ClientsListActivity.this ).getDaoSession ().getClientPropertyValuesDao ().queryBuilder ()
							.where ( ClientPropertyValuesDao.Properties.ClientPropertyLevelCode.eq ( clientPropertyLevel.getClientPropertyLevelCode () ) ).list () );
					// Check if the values list is empty
					if ( clientPropertyValues.isEmpty () )
						// Do nothing
						continue;
					// Otherwise initialize a client property filter with the current property level and values
					ClientPropertyFilter _clientPropertyFilter = new ClientPropertyFilter ( clientPropertyLevel , clientPropertyValues );
					// Add the new client property filter to the list
					clientPropertyFilter.add ( _clientPropertyFilter );
				} // End for each
			} // End if
			// Otherwise the client property filter list is already defined
			// Check if the secondary view is being restored
			else if ( ! restore )
				// Make a backup of the client property filter
				for ( ClientPropertyFilter _clientPropertyfilter : clientPropertyFilter )
					_clientPropertyfilter.backup ();
			
			// Check if the client area filter list is defined
			if ( clientAreaFilter == null ) {
				// Initialize the list
				clientAreaFilter = new ArrayList < ClientAreaFilter > ();
				// Retrieve all the area levels
				List < AreaLevels > areaLevels = DatabaseUtils.getInstance ( ClientsListActivity.this ).getDaoSession ().getAreaLevelsDao ().queryBuilder ()
						.orderAsc ( AreaLevelsDao.Properties.Sequence ).list ();
				// Iterate over all the area levels
				for ( AreaLevels areaLevel : areaLevels ) {
					// Retrieve all the area values of the current level
					ArrayList < Areas > areaValues = new ArrayList < Areas > ();
					areaValues.addAll ( DatabaseUtils.getInstance ( ClientsListActivity.this ).getDaoSession ().getAreasDao ().queryBuilder ()
							.where ( AreasDao.Properties.AreaLevelCode.eq ( areaLevel.getAreaLevelCode () ) )
							.orderAsc ( AreasDao.Properties.AreaDescription ).list () );
					// Check if the values list is empty
					if ( areaValues.isEmpty () )
						// Do nothing
						continue;
					// Otherwise initialize a client area filter with the current area level and values
					ClientAreaFilter _clientAreaFilter = new ClientAreaFilter ( areaLevel , areaValues );
					// Add the new client area filter to the list
					clientAreaFilter.add ( _clientAreaFilter );
				} // End for each
			} // End if
			// Otherwise the client area filter list is already defined
			// Check if the secondary view is being restored
			else if ( ! restore )
				// Make a backup of the client area filter
				for ( ClientAreaFilter _clientAreaFilter : clientAreaFilter )
					_clientAreaFilter.backup ();
			return null;
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( Void arg ) {
			// Declare and initialize a client filter list
			ArrayList < ClientFilter > clientFilter = new ArrayList < ClientFilter > ();
			// Add the client property filters to the list
			clientFilter.addAll ( clientPropertyFilter );
			// Add the client area filters to the list
			clientFilter.addAll ( clientAreaFilter );
			
			// Retrieve a reference to the expandable list view
			ExpandableListView expandableListView = (ExpandableListView) findViewById ( R.id.expandablelistview_client_filter );
			// Set the new adapter
			expandableListView.setAdapter ( new ClientFilterAdapter ( ClientsListActivity.this , R.layout.clients_list_activity_filter_item_group , R.layout.clients_list_activity_filter_item_child , clientFilter ) );
			try {
				// Iterate over the expanded state of the groups
				for ( int i = 0 ; i < clientFilter.size () ; i ++ )
					// Restore the expanded state of the current group
					if ( expanded [ i ] )
						// Expand the current group
						expandableListView.expandGroup ( i );
			} catch ( Exception exception ) {
				// Do nothing
			} finally {
				expanded = null;
			} // End of try-catch block
			
			// Check if the expandable list view position is provided
			if ( expandableListViewPosition != null ) {
				// Restore the expandable list view position
				expandableListViewPosition.restore ( expandableListView );
				// Clear the stored expandable list view position
				expandableListViewPosition = null;
			} // End if
		}
		
	}
	
	/**
	 * AsyncTask helper class used to re-populate the main list after computing the client filter selections.
	 * 
	 * @author Elias
	 *
	 */
	private class ClientFilterList extends AsyncTask < Void , Void , Void > {
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground ( Void ... params ) {
    		// Compute the new selection
    		computeClientFilterSelection ();
    		// Check if the client property filter list is valid
    		if ( clientPropertyFilter != null )
				// Clear the backup of the client property filter
				for ( ClientPropertyFilter _clientPropertyfilter : clientPropertyFilter )
					_clientPropertyfilter.clearBackup ();
    		// Check if the client property filter list is valid
    		if ( clientAreaFilter != null )
				// Clear the backup of the client area filter
				for ( ClientAreaFilter _clientAreafilter : clientAreaFilter )
					_clientAreafilter.clearBackup ();
			return null;
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( Void arg ) {
    		// Populate the list asynchronously
    		repopulateList ();
		}
	
	}
		
}