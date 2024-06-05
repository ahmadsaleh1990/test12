/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.CompetitorItemsList;

import java.util.ArrayList;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.TextView;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.CompetitorItemsDao;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.Items;
import me.SyncWise.Android.Modules.SearchList.SearchCursorListActivity;
import me.SyncWise.Android.Utilities.ItemCard;

/**
 * List Activity of all the competitor items for the provided item with search capabilities.
 * 
 * @author Elias
 * @sw.todo <b>Competitor Items List Activity :</b><br>
 * Simply add this class in the {@code AndroidManifest.xml} file.
 *
 */
public class CompetitorItemsListActivity extends SearchCursorListActivity {

	/**
	 * Bundle key used to put/retrieve the item object.
	 */
	public static final String ITEM = CompetitorItemsListActivity.class.getName () + ".ITEM";
	
	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		// Retrieve a reference to the item object
		Items item = (Items) getIntent ().getSerializableExtra ( ITEM );
		// Define the main table
		setTable ( DatabaseUtils.getInstance ( this ).getDaoSession ().getCompetitorItemsDao () );
		// Define the list selection
		setSelection ( CompetitorItemsDao.Properties.ItemCode.columnName + " = ? AND " + CompetitorItemsDao.Properties.CompanyCode.columnName + " = ?" );
		// Define the list selection arguments
		ArrayList < String > selectionArguments = new ArrayList < String > ();
		selectionArguments.add ( item == null ? "" : item.getItemCode () );
		selectionArguments.add ( item == null ? "" : item.getCompanyCode () );
		setSelectionArguments ( selectionArguments );
		// Define the list order
		setOrder ( CompetitorItemsDao.Properties.CompetitorItemName.columnName + " COLLATE NOCASE ASC" );
		// Define the properties which applies for a filter
		addProperty ( CompetitorItemsDao.Properties.CompetitorItemName );
		addProperty ( CompetitorItemsDao.Properties.CompetitorItemCode );
		// Superclass method call
		super.onCreate ( savedInstanceState );
		// Set the activity content from a layout resource.
		setContentView ( R.layout.competitor_items_list_activity );
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.competitor_items_list_activity_title ) );
		// Initialize the item card
		ItemCard.initializeItemCard ( this , item );
		// Enable fast scrolling
		getListView ().setFastScrollEnabled ( true );
		// Define the empty list view
		getListView ().setEmptyView ( findViewById ( R.id.empty_list_view ) );
		// Display empty list label
		( (TextView) findViewById ( R.id.empty_list_view ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_competitor_items_list_label ) );
	}
	
	/*
	 * Builds a ListAdapter that will be set to the list.
	 *
	 * @see me.SyncWise.Android.Modules.SearchList.SearchCursorListActivity#populateList(android.database.Cursor)
	 */
	@Override
	protected ListAdapter populateList ( Cursor cursor ) {
		// Check if the item object is valid
		if ( getIntent ().getSerializableExtra ( ITEM ) != null )
			// Initialize and return a competitor items cursor adapter using the provided cursor
			return new CompetitorItemsCursorAdapter ( this , cursor , R.layout.competitor_item );
		// Otherwise the item object is not valid
		return null;
	}
	
}