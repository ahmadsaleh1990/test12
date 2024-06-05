/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Collections;

 
 import java.util.ArrayList;
 import java.util.List;

import me.SyncWise.Android.ActivityTransition;
import me.SyncWise.Android.App;
import me.SyncWise.Android.AppDialog;
 import me.SyncWise.Android.AppResources;
 import me.SyncWise.Android.AppToast;
 import me.SyncWise.Android.R;
 import me.SyncWise.Android.Database.ClientDues;
 import me.SyncWise.Android.Database.ClientDuesDao;
  
import me.SyncWise.Android.Database.CollectionDetails;
import me.SyncWise.Android.Database.CollectionHeaders;
import me.SyncWise.Android.Database.CollectionInvoices;
 import me.SyncWise.Android.Database.Companies;
 import me.SyncWise.Android.Database.DatabaseUtils;
 import me.SyncWise.Android.Database.Visits;
import me.SyncWise.Android.Gson.BaseTimerListActivity;
import me.SyncWise.Android.Modules.ClientsList.ClientInfo.ClientInfoActivity;
import me.SyncWise.Android.Modules.Journey.Call;
import me.SyncWise.Android.Modules.Printing.PrintingActivity;
import me.SyncWise.Android.Widgets.Baguette;
 import me.SyncWise.Android.Widgets.QuickAction.QuickAction;
 import android.app.ListActivity;
import android.content.DialogInterface;
 import android.content.Intent;
 import android.os.AsyncTask;
 import android.os.Bundle;
 
import android.view.Menu;
import android.view.MenuItem;
 import android.view.View;
 import android.widget.ListAdapter;
 import android.widget.ListView;
import android.widget.TextView;

public class CollectionActivity extends BaseTimerListActivity implements QuickAction.OnActionItemClickListener{
 	/**
	 * Bundle key used to put/retrieve the content of the current client.
	 */
	public static final String CLIENT = CollectionActivity.class.getName () + ".CLIENT";
	/**
	 * Bundle key used to put/retrieve the content of the current client.
	 */
	public static final String CALL = CollectionActivity.class.getName () + ".CALL";
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #visit}.
	 */
	public static final String VISIT = CollectionActivity.class.getName () + ".VISIT";
	

	/**
	 * {@link me.SyncWise.Android.Database.Visits Visits} object holding a reference to the call in progress.
	 */
	private Visits visit = null;

	/**
	 * List of {@link me.SyncWise.Android.Modules.Collections.CollectionActivity} objects used to define the collection invoices
	 */
	
	private List < ClientDues > companyClientDues;
	/**
	 * Bundle key used to put/retrieve the content of {@link #company}.
	 */
	public static final String COMPANY = CollectionActivity.class.getName () + ".COMPANY";
	
	/**
	 * Reference to the company object.
	 */
	protected Companies company;
	
 	/**
	 * Reference to a {@link me.SyncWise.Android.Widgets.QuickAction.QuickAction QuickAction} used in this activity.<br>
	 */
 
	private PopulateList populateList;
 
	@Override
	public void onCreate ( Bundle savedInstanceState ) {
		// Superclass method call
		super.onCreate ( savedInstanceState );
		// Change the title associated with this activity
		setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.collection_activity_title ) );
		// Set the activity content from a layout resource.
		setContentView ( R.layout.collection_activity );
		// Enable fast scrolling
		getListView ().setFastScrollEnabled ( true );
		// Define the empty list view
		getListView ().setEmptyView ( findViewById ( R.id.empty_list_view ) );
		// Display empty list label
		( (TextView) findViewById ( R.id.empty_list_view ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_items_list_label ) );
		
		// Retrieve the visit object
		visit = (Visits) getIntent ().getSerializableExtra ( VISIT );
		// Check if both the visit and the client are valid
		if ( getIntent ().getSerializableExtra ( CALL ) == null || visit == null ) {
			// Indicate that the activity cannot be displayed
			new AppToast ( this ).setIcon ( R.drawable.warning ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.invalid_selection_label ) ).show ();
			// Finish activity
			finish ();
			// Exit method
			return;
		} // End if
		// Check if the visit object is valid
		if ( visit != null )
			// Refresh the visit object
			DatabaseUtils.getInstance ( this ).getDaoSession ().getVisitsDao ().refresh ( visit );


 
				
		populateList = new PopulateList ();
		populateList.execute ();
	}
    
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
    	super.onSaveInstanceState ( outState );
    	outState.putSerializable ( COMPANY , company );
	}
    
 
    
    protected void populateList () {
		populateList = new PopulateList ();
		populateList.execute ();
    }
    
    /*
     * This method will be called when an item in the list is selected.
     *
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
	@Override
     protected void onListItemClick ( ListView listView , View view , final int position , long id ) {

		//ClientDues clientDue = (ClientDues) ( (BaseAdapter) getListAdapter () ).getItem ( position );

	//	quickAction_partial.show ( view , clientDue , getResources () );
	}
	
	
	@Override
	public void onItemClick(QuickAction source, View anchor, Object object,
			int pos, final int actionId) {
		// TODO Auto-generated method stub
	 
		
	}
 
	/*
	 * Initialize the contents of the Activity's standard options menu.
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		 
		// Use the MenuInflater of this context to inflate a menu hierarchy from
		// the specified XML resource
		getMenuInflater().inflate(R.menu.action_bar, menu);
		// Enable the required menu items
		me.SyncWise.Android.Utilities.MenuItem.enable(menu
				.findItem(R.id.action_save));
		 
		
		// Display the menu
		return true;
	}
	
	/*
	 * This hook is called whenever an item in your options menu is selected.
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		  if (menuItem.getItemId() == R.id.action_save) {
			  List < ClientDues > clientDues=new ArrayList<ClientDues>();
			  
			  for (ClientDues cd :companyClientDues){
				  if(cd.getAvailability()==1){
					  clientDues.add(cd);
				  }
				  
			  }
			  
			// Make sure the collection can be saved
			if (clientDues.isEmpty()) {
				// Display baguette message
				Baguette.showText(
						this,
						AppResources.getInstance(this).getString(this,
								R.string.collection_no_payments_done_warning),
						Baguette.BackgroundColor.RED);
				// Consume event
				return true;
			} // End if
				// Display alert dialog
			AppDialog.getInstance().displayAlert(
					this,
					null,
					AppResources.getInstance(this).getString(this,
							R.string.save_confirmation_message),
					AppDialog.ButtonsType.YesNo,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Determine the clicked button
							switch (which) {
							case DialogInterface.BUTTON_POSITIVE:
								// Save the collection
								//saveCollection();
								break;
							case DialogInterface.BUTTON_NEGATIVE:
								// Dismiss dialog
								AppDialog.getInstance().dismiss();
								break;
							} // End switch
						}
					});

			// Consume event
			return true;
		} // End else if
		 
			// Allow normal menu processing to proceed
		return false;
	}
	
	
	
	/**
	 * AsyncTask helper class used to populate the Client Dues Invoices list with the appropriate collection .
	 * 
	 * @author ahmad
	 *
	 */
	private class PopulateList extends AsyncTask < Void , Void , ArrayList < ClientDues > > {
		 
		
		@Override
		protected ArrayList<ClientDues> doInBackground(Void... params) {
//			if ( company == null  ) {
//				populateList = null;
//				// Exit method
//				return null;
//			} // End if
			Call call = (Call) getIntent().getSerializableExtra(CALL);
			companyClientDues = DatabaseUtils.getInstance(CollectionActivity.this).getDaoSession().getClientDuesDao().queryBuilder()
					.where(ClientDuesDao.Properties.ClientCode.eq(
							  call.getClient().getClientCode()))
							.orderAsc(ClientDuesDao.Properties.DueDate).list();
 
	 			return (ArrayList<ClientDues>) companyClientDues;
		}


		@Override
		protected void onPostExecute(ArrayList<ClientDues> retCollectionInvoices) {
			if(companyClientDues != null){
//				StringBuilder	pattern = new StringBuilder ();
//				pattern.append ( " #,##0" );
//				 
//				moneyFormat = new DecimalFormat ( pattern.toString () );
//				TextView		 amount = (TextView) findViewById(R.id.total_dues);
//				amount.setText("Dues Amount= "+moneyFormat.format (remainingAmount) +" "+"Total Amount="+moneyFormat.format (totalAmount));
			setListAdapter( getCollectionDetailsAdapter(R.layout.collection_item, companyClientDues));
			invalidateOptionsMenu();
			}
		}
		
		
	}
	
	protected ListAdapter getCollectionDetailsAdapter ( int layout , List < ClientDues > clientdues ) {
		return new CollectionAdapter ( this , layout ,clientdues);
	}

	@Override
	public void onBackPressed() {
		company = null;
		companyClientDues = null;
 
		finish();
	}

    @Override
    public void onResume () {
    	super.onResume ();
    	 
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 1 && requestCode == 0){
			
    		populateList = new PopulateList ();
			populateList.execute ();
		}
	}
	
	
 
    
	
}