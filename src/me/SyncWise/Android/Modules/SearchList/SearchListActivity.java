/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.SearchList;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.Property;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.ListViewPosition;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Gson.BaseTimerListActivity;

import android.app.ListActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.SearchView;

/**
 * Activity module used to implement a searchable list activity.<br>
 * The search criteria are applied on several properties that should be added via {@link #addProperty(Property)}.
 * 
 * @author Elias
 * @see me.SyncWise.Android.Modules.SearchList.SearchCursorListActivity
 *
 */
public abstract class SearchListActivity extends BaseTimerListActivity implements SearchView.OnQueryTextListener {

	/**
	 * Bundle key used to put/retrieve the content of {@link #searchQuery}.
	 */
	protected static final String SEARCH_QUERY = SearchCursorListActivity.class.getName () + ".SEARCH_QUERY";
	
	/**
	 * String holding the search query.
	 */
	private String searchQuery;

	/**
	 * Bundle key used to put/retrieve the content of {@link #listViewPosition}.
	 */
	protected static final String LIST_VIEW_POSITION = SearchCursorListActivity.class.getName () + ".LIST_VIEW_POSITION";
	
	/**
	 * Object used to save/restore the list views' position.
	 */
	private ListViewPosition listViewPosition;
	
	/**
	 * String hosting the property prefix used to build the selection query.
	 */
	private String propertyPrefix;
	
	/**
	 * List used to host the properties on which the filter (search) is applied.
	 */
	private List < Property > properties;
	
	/**
	 * Integer used to hold the layout resource ID (if any).<br>
	 * If none is set, the layout {@code R.layout.search_list_activity} is applied.<br>
	 * <em>Note : the variable's state is not saved !</em>
	 */
	private Integer layoutResourceID;

	/**
	 * Setter - {@link #searchQuery}
	 * 
	 * @param searchQuery	String holding the search query.
	 */
	public void setSearchQuery ( final String searchQuery ) {
		// Store the current search query
		this.searchQuery = searchQuery;
	}
	
	/**
	 * Getter - {@link #searchQuery}
	 * 
	 * @return	String holding the search query.
	 */
	public String getSearchQuery () {
		// Return the current search query
		return searchQuery;
	}
	
	/**
	 * Setter - {@link #listViewPosition}
	 * 
	 * @param listViewPosition	Object used to save/restore the list views' position.
	 */
	public void setListViewPosition ( final ListViewPosition listViewPosition ) {
		// Store the list view position
		this.listViewPosition = listViewPosition;
	}
	
	/**
	 * Getter - {@link #listViewPosition}
	 * 
	 * @return	Object used to save/restore the list views' position.
	 */
	public ListViewPosition getListViewPosition () {
		// Return the list view position
		return listViewPosition;
	}
	
	/**
	 * Setter - {@link #propertyPrefix}
	 * 
	 * @param propertyPrefix	String hosting the property prefix used to build the selection query.
	 */
	public void setPropertyPrefix ( final String propertyPrefix ) {
		// Set the property prefix
		this.propertyPrefix = propertyPrefix;
	}
	
	/**
	 * Getter - {@link #propertyPrefix}
	 * 
	 * @return	String hosting the property prefix used to build the selection query.
	 */
	public String getPropertyPrefix () {
		// Return the property prefix or empty string if none
		return TextUtils.isEmpty ( propertyPrefix ) ? "" : propertyPrefix;
	}
	
	/**
	 * Adds a property to the searchable list on which the filter (search) is applied.
	 * 
	 * @param property	String holding the property name to add.
	 */
	public void addProperty ( final Property property ) {
		// Check if the provided property is valid
		if ( property == null )
			// Invalid property, exit method
			return;
		// Check if the list of properties is valid
		if ( properties == null )
			// Initialize the list of properties before adding the provided property
			properties = new ArrayList < Property > ();
		// Add the provided property
		properties.add ( property );
	}
	
	/**
	 * Computes and returns the total number of added properties.
	 * 
	 * @return	Integer hosting the total number of properties.
	 */
	public int getPropertiesCount () {
		try {
			// Return the size of the list holding the properties
			return properties.size ();
		} catch ( Exception exception ) {
			// Invalid list
			return 0;
		} // End of try-catch block
	}
	
	/**
	 * Retrieves the property at the specified position.
	 * 
	 * @param position	Integer holding the property location in the list.
	 * @return	String hosting the property at the specified position in the list.
	 */
	public Property getProperty ( final int position ) {
		try {
			// Return the appropriate property
			return properties.get ( position );
		} catch ( Exception exception ) {
			// The provided position is invalid and/or the list of properties is invalid
			return null;
		} // End of try-catch block
	}
	
	/**
	 * Setter - {@link #layoutResourceID}<br>
	 * <em>Note : In order to be able to use this method properly using inheritance, the layout resource ID is set if and only if {@link #layoutResourceID} is {@code NULL}<br>
	 * In this manner, if the {@link #layoutResourceID} is set from within the child class (using inheritance), the parent class will not override its value.</em>
	 * 
	 * @param layoutResourceID	Integer holding the layout resource ID.
	 */
	public void setLayoutResourceID ( Integer layoutResourceID ) {
		if ( this.layoutResourceID == null )
			this.layoutResourceID = layoutResourceID;
	}
	
	/**
	 * Performs all necessary setup in order to properly display the search view widget.<br>
	 * The search view widget should have a resource identifier <b><code>search_view</code></b>.
	 */
    private void setupSearchView () {
    	try {
	    	// Retrieve a reference to the search view widget
			SearchView searchView = (SearchView) findViewById ( R.id.search_view );
			// Set a query hint string to be displayed in the empty query field
			searchView.setQueryHint ( AppResources.getInstance ( this ).getString ( this , R.string.search_label ) );
			// Set a query listener to apply search filters
			searchView.setOnQueryTextListener ( this );
			// Disable showing a submit button when the query is non-empty
			searchView.setSubmitButtonEnabled ( false );
			// Display a single search icon and expand the search view widget to show the text field
			searchView.setIconifiedByDefault ( false );
    	} catch ( Exception exception ) {
			// Invalid search view widget
		} // End of try-catch block
	}
    
    /**
     * Initializes the search list activity components (i.e. array, cursor, ...).<br>
     * This method should be implemented in subclasses.
     * 
     * @param savedInstanceState	Bundle holding saved instance state(s).
     */
    protected abstract void initializeComponents ( final Bundle savedInstanceState );
    
	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		// Superclass method call
		super.onCreate ( savedInstanceState );
		// Set the activity content from a layout resource.
		setContentView ( layoutResourceID == null ? R.layout.search_list_activity : layoutResourceID );
		// Initialize components
		initializeComponents ( savedInstanceState );
	}
	
	/*
	 * Called when activity start-up is complete (after onStart() and onRestoreInstanceState(Bundle) have been called).
	 * 
	 * @see android.app.Activity#onPostCreate(android.os.Bundle)
	 */
	@Override
	protected void onPostCreate ( Bundle savedInstanceState ) {
		// Superclass method call
		super.onPostCreate ( savedInstanceState );
		// Perform necessary setup process to properly display the search view widget
		setupSearchView ();
		// Disable the search view (it should be enabled only after a valid adapter is set)
		( (SearchView) findViewById ( R.id.search_view ) ).setEnabled ( false );
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
    	outState.putString ( SEARCH_QUERY , getSearchQuery () );
    	// Save the list view's position in the outState bundle
    	outState.putSerializable ( LIST_VIEW_POSITION , new ListViewPosition ( getListView () ) );
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
			layoutResourceID = null;
			searchQuery = null;
			listViewPosition = null;
			properties = null;
		} // End if
	}
    
	/**
	 * Sets a new search query via the search view widget.<br>
	 * What this method actually does is simply display the search query, the actual functionality is handled via the {@link android.widget.SearchView.OnQueryTextListener#onQueryTextChange(String) onQueryTextChange(String)} callback.
	 * 
	 * @param query	String holding the search query.
	 */
	public void setQuery ( final String query ) {
		try {
			// Determine if the query is valid
			if ( query != null )
				// Display the new query
				// The new query will be applied via the onQueryTextChange listener
				( (SearchView) findViewById ( R.id.search_view ) ).setQuery ( query , false );
		} catch ( Exception exception ) {
			// Invalid search view widget
		} // End of try-catch block
	}

	/*
	 * Called when the user submits the query.
	 * 
	 * @see android.widget.SearchView.OnQueryTextListener#onQueryTextSubmit(java.lang.String)
	 */
	@Override
	public boolean onQueryTextSubmit ( String query ) {
		// No queries are submitted
		return false;
	}
	
	/*
	 * Called when the query text is changed by the user.
	 * 
	 * @see android.widget.SearchView.OnQueryTextListener#onQueryTextChange(java.lang.String)
	 */
	public abstract boolean onQueryTextChange ( String newText );
	
}