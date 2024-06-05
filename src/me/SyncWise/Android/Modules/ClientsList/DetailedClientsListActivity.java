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

import me.SyncWise.Android.ActivityTransition;
import me.SyncWise.Android.App;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Modules.ClientsList.ClientInfo.ClientInfoActivity;
import me.SyncWise.Android.Modules.ClientsList.ClientsCursorAdapter.ViewHolder;
import me.SyncWise.Android.Widgets.QuickAction.ActionItem;
import me.SyncWise.Android.Widgets.QuickAction.QuickAction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

/**
 * List Activity of all the clients with search capabilities and additional actions/details.
 * 
 * @author Elias
 * @sw.todo	<b>Detailed Clients List Activity Implementation :</b><br>
 * Simply add this class in the {@code AndroidManifest.xml} file.
 *
 */
public class DetailedClientsListActivity extends ClientsListActivity implements QuickAction.OnActionItemClickListener {

	/**
	 * Reference to a {@link me.SyncWise.Android.Widgets.QuickAction.QuickAction QuickAction} used in this activity.
	 */
	private QuickAction quickAction;
	
	/**
	 * Helper Class used to manage the fragment's action items IDs for the quick action widgets.<br>
	 * The main purpose of this class is to maintain a unique identifier for action items within the activity.
	 * 
	 * @author Elias
	 *
	 */
	private class ActionItemID {
		public static final int INFO = 0;
	}
	
	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		// Superclass method call
		super.onCreate ( savedInstanceState );
        // Perform the quick action setup
        setupQuickAction ();
	}
	
	/**
	 * Performs all necessary setup in order to properly display the quick action widget.
	 */
    private void setupQuickAction () {
		// Initialize the quick action widgets
		quickAction = new QuickAction ( this , QuickAction.VERTICAL );
		// Action Item : Info
		ActionItem info = new ActionItem ( ActionItemID.INFO ,
				AppResources.getInstance ( this ).getString ( this , R.string.info_label ) ,
				getResources ().getDrawable ( R.drawable.info ) );
    	// Populate the quick action widget with quick action items
		quickAction.addActionItem ( info );
		// Assign an action item click listener
		quickAction.setOnActionItemClickListener ( this );
    }
	
	/*
	 * This method will be called when an item in the list is selected.
	 *
	 * @see android.support.v4.app.ListFragment#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	public void onListItemClick ( ListView listView , View view , int position , long id ) {
		// Check if the client filter is displayed
		if ( ! displayFilter )
			// Display the quick action widget
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
		case ActionItemID.INFO:
			// Create an intent to start a new activity
			Intent intent = new Intent ( DetailedClientsListActivity.this , App.getClientInfoActicityClass () );
			// Add the client code to the intent
			intent.putExtra ( ClientInfoActivity.CLIENT_CODE , ( (ViewHolder) anchor.getTag () ).clientCode );
			// Add the company code to the intent
			intent.putExtra ( ClientInfoActivity.COMPANY_CODE , ( (ViewHolder) anchor.getTag () ).companyCode );
			// Start the new activity
			startActivity ( intent );
			// Specify an explicit transition animation to perform next
			ActivityTransition.SlideOutLeft ( this );
			break;
		} // End of switch
	}
	
}