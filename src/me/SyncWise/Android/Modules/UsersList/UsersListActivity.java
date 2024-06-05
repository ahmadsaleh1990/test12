package me.SyncWise.Android.Modules.UsersList;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.Items;
import me.SyncWise.Android.Database.Users;
import me.SyncWise.Android.Database.UsersDao;
 
import me.SyncWise.Android.Modules.SearchList.SearchCursorListActivity;
import me.SyncWise.Android.Modules.UsersList.UserCursorAdapter.ViewHolder;
import me.SyncWise.Android.Widgets.QuickAction.ActionItem;
import me.SyncWise.Android.Widgets.QuickAction.QuickAction;

public class UsersListActivity extends SearchCursorListActivity implements QuickAction.OnActionItemClickListener , QuickAction.OnDismissListener {
 
	private QuickAction quickAction;
	/**
	 * Helper Class used to manage the activity's action items IDs for the quick action widgets.<br>
	 * The main purpose of this class is to maintain a unique identifier for action items within the activity.
	 * 
	 * @author ahmad
	 *
	 */
	protected class ActionItemID {
		public static final int EMAIL = 0;
		public static final int ADDRESS = 1;
	}
	@Override
	protected void onCreate ( Bundle savedInstanceState ) {
		// Define the main table
		setTable ( DatabaseUtils.getInstance ( this ).getDaoSession ().getUsersDao()   );
		// Define the list order
		setOrder ( UsersDao.Properties.UserName.columnName + " COLLATE NOCASE ASC" );
		// Define the properties which applies for a filter
		addProperty ( UsersDao.Properties.UserCode );
		addProperty ( UsersDao.Properties.UserName );
	
		// Superclass method call
		super.onCreate ( savedInstanceState );
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.users_list_activity_title ) );
		// Enable fast scrolling
		getListView ().setFastScrollEnabled ( true );
		
		
		  setupQuickAction ();
		
		
		// Define the empty list view
		getListView ().setEmptyView ( findViewById ( R.id.empty_list_view ) );
		// Display empty list label
		( (TextView) findViewById ( R.id.empty_list_view ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_users_list_label ) );
		} 

	/*
	 * Builds a ListAdapter that will be set to the list.
	 *
	 * @see me.SyncWise.Android.Modules.SearchList.SearchCursorListActivity#populateList(android.database.Cursor)
	 */
	@Override
	protected ListAdapter populateList ( Cursor cursor ) {
		// Initialize and return a client cursor adapter using the provided cursor
		return new UserCursorAdapter ( this , cursor , R.layout.user_list );
	}
	
	
//	
//	
	//QuickAction
	
    private void setupQuickAction () {
		// Initialize the quick action widget
		quickAction = new QuickAction ( this , QuickAction.VERTICAL );
		// Action Item : Email
		ActionItem email = new ActionItem ( ActionItemID.EMAIL ,
				AppResources.getInstance ( this ).getString ( this , R.string.quick_action_email_label ) ,
				getResources ().getDrawable ( R.drawable.quick_action_ok ) );
		// Action Item : Address
		ActionItem address = new ActionItem ( ActionItemID.ADDRESS ,
				AppResources.getInstance ( this ).getString ( this , R.string.quick_action_address_label ) ,
				getResources ().getDrawable ( R.drawable.quick_action_cancel ) );
    	// Populate the quick action widget with quick action items
		quickAction.addActionItem ( email );
		quickAction.addActionItem ( address );
		// Assign an action item click listener
		quickAction.setOnActionItemClickListener ( this );
		// Assign a quick action dismiss listener
		quickAction.setOnDismissListener ( this );
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
		case ActionItemID.EMAIL:
			
			if ( object instanceof Cursor ) {
				// this is client
				
				Cursor dataCursor = (Cursor) object;
				// lets say there are 2 cursors
				int dataType = dataCursor.getInt(dataCursor.getColumnIndex("DataType"));
				if ( dataType == 1 ) {
					// this is client
				}
				else if ( dataType == 2 ) {
					// this is item
				}
					
			}
			else if ( object instanceof Users ) {
				// this is user
			}
			else if ( object instanceof Items ) {
				// this is item
			}
			
			
			
			
			
			
			Cursor cursor = (Cursor) object;
			int emailColumnIndex = cursor.getColumnIndex(UsersDao.Properties.UserEmail.columnName);
			//addUserEmail( ( (Users) object ).getUserEmail() );
			Toast.makeText(getApplicationContext(), cursor.isNull(emailColumnIndex) ? "N/A" : cursor.getString(emailColumnIndex) , Toast.LENGTH_LONG).show();
			break;
		case ActionItemID.ADDRESS:
			cursor = (Cursor) object;
			int addressColumnIndex = cursor.getColumnIndex(UsersDao.Properties.UserAddress.columnName);
			//addUserEmail( ( (Users) object ).getUserEmail() );
			Toast.makeText(getApplicationContext(), cursor.isNull(addressColumnIndex) ? "N/A" : cursor.getString(addressColumnIndex) , Toast.LENGTH_LONG).show();
			break;
		} // End of switch
	}
    
	/*
	 * This method will be invoked when the quick action is dismissed.
	 *
	 * @see me.SyncWise.Android.Widgets.QuickAction.QuickAction.OnDismissListener#onDismiss(me.SyncWise.Android.Widgets.QuickAction.QuickAction, android.view.View)
	 */
	@Override
	public void onDismiss ( QuickAction source , View anchor ) {
		// Retrieve a reference to the anchor view's tag
		ViewHolder viewHolder = (ViewHolder) anchor.getTag ();
		Toast.makeText(this, "Quick action was cancelled for user : " + viewHolder.userName , Toast.LENGTH_LONG).show();
	}
	
    /* This method will be called when an item in the list is selected.
     *
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
	@Override
   protected void onListItemClick ( ListView listView , View view , final int position , long id ) {
		// Show the quick action widget over the current view 
		quickAction.show ( view , listView.getItemAtPosition ( position ) , getResources () );
	}
	
}
