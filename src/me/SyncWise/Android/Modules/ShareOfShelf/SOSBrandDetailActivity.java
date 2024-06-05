package me.SyncWise.Android.Modules.ShareOfShelf;

 
import java.util.ArrayList;
import java.util.HashMap;

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.AppToast;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Gson.BaseTimerListActivity;
import me.SyncWise.Android.Modules.Journey.Call;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SOSBrandDetailActivity extends BaseTimerListActivity  {
	public static final String CALL = SOSBrandDetailActivity.class.getName () + ".CALL";
	public static final String VISIT = SOSBrandDetailActivity.class.getName () + ".VISIT";
	public static final String CATEGORY = SOSBrandDetailActivity.class.getName () + ".CATEGORY"; 
	public static final String TARGETCODE = SOSBrandDetailActivity.class.getName () + ".TARGETCODE";
	public static final String SOSTYPE = SOSBrandDetailActivity.class.getName () + ".SOSTYPE";
	public static final String SELECTED_BRAND = SOSBrandDetailActivity.class.getName () + ".SELECTED_BRAND"; 
	private ArrayList <SOSTracker>	trackers = new ArrayList < SOSTracker > ();
	protected static PopulateList populateList;
	private String  targetCode ;
	private int   sosType  ;
    private String categoryCode;
	@Override
	public void onCreate(Bundle savedInstanceState) {
			AppDialog.showAsDialog ( this , new AppDialog.Property ( 90 , true ) );
		    super.onCreate(savedInstanceState);
		   // Change the title associated with this activity
			setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.share_of_shelf_tracker_details_activity_title ) );
			requestWindowFeature ( Window.FEATURE_INDETERMINATE_PROGRESS );
			// Set the activity content from a layout resource.
			setContentView ( R.layout.sos_brand_details_activity );
			 SOSBrandDetailActivity.this.setFinishOnTouchOutside(false);
			// Enable fast scrolling
			getListView ().setFastScrollEnabled ( true );
			// Define the empty list view
			getListView ().setEmptyView ( findViewById ( R.id.empty_list_view ) );
			// Display empty list label
			( (TextView) findViewById ( R.id.empty_list_view ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.empty_share_of_shelf_list_label ) );
			// Check if both the visit and the call are valid
			if ( getIntent ().getSerializableExtra ( CALL ) == null || getIntent ().getSerializableExtra ( VISIT ) == null ) {
				// Indicate that the activity cannot be displayed
				new AppToast ( this ).setIcon ( R.drawable.warning ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.invalid_selection_label ) ).show ();
				// Finish activity
				finish ();
				// Exit method
				return;
			} // End if
			
			TextView label_mesure_meter = (TextView) findViewById(R.id.label_mesure_meter);
			label_mesure_meter.setText("Mesure"); 
			TextView label_item = (TextView) findViewById(R.id.label_item);
			label_item.setText("Brand");
		
			  categoryCode= ((String) getIntent().getSerializableExtra( CATEGORY));
			  targetCode =((String) getIntent().getSerializableExtra( TARGETCODE));
			  sosType =((Integer) getIntent().getSerializableExtra( SOSTYPE));
		 
			TextView label_bda_meter = (TextView) findViewById(R.id.label_bda_meter);
			 	label_bda_meter.setText("Target Value") ;
		 
			  
		 	populate ();
	}
	   @Override
	    public boolean onCreateOptionsMenu ( Menu menu ) {
//	    	// Use the MenuInflater of this context to inflate a menu hierarchy from the specified XML resource
	  	getMenuInflater ().inflate ( R.menu.action_bar , menu );
		me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_basket ) );
	    return true;
	    }
		
	@Override
    protected void onListItemClick ( ListView listView , View view , final int position , long id ) {
	 
	SOSTracker 	returnModification = (SOSTracker) ( (BaseAdapter) getListAdapter () ).getItem ( position );
    Toast.makeText(this, ""+returnModification.getBdaMeter(), 100000).show();
     
    	 
	}
	protected void populate () {
		// Retrieve all the share of shelves asynchronously
		populateList = new PopulateList ();
		populateList.execute ();
	}
		private class PopulateList extends AsyncTask < Void , Void , ArrayList < SOSTracker > > {
			
			/**
			 * Flag used to determine if an error occurred.
			 */
			private boolean error;
			
			/**
			 * Flag used to indicated if the activity ended before the asynctask finished executing.
			 */
			private boolean activityEnded;
			
			
			/*
			 * Method to perform a computation on a background thread.
			 * 
			 * @see android.os.AsyncTask#doInBackground(Params[])
			 */
	 
			@Override
			protected ArrayList < SOSTracker > doInBackground ( Void ... params ) {
				try {
					trackers = new ArrayList < SOSTracker > ();
					
					// Retrieve the client code
					String clientCode = ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClientDivision ().getClientCode ();
					// Retrieve the company code
					String companyCode = ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClientDivision ().getCompanyCode ();
				 
					 HashMap<String, ArrayList<SOSTracker>>	 promoted = (  HashMap<String, ArrayList<SOSTracker>>)  getIntent().getSerializableExtra ( SELECTED_BRAND ); 
					 ArrayList<SOSTracker> oldSelected= new ArrayList<SOSTracker>();
					 HashMap<String, SOSTracker>	 oldtracker = new HashMap<String, SOSTracker>();
					 HashMap<String, Double>	 oldtracker1 = new HashMap<String, Double>();
						
					 if(promoted.containsKey(categoryCode))
					 {
						 oldSelected= promoted.get(categoryCode);
					 for(SOSTracker tt :oldSelected)
					 {	 oldtracker.put(tt.getBrandCode(), tt );
					 oldtracker1.put(tt.getBrandCode(), tt.getCurrentShareOfShelf());
					 }
					 }
					 
					String SQL=null;
				 	String selectionArguments [] = null;
				 	SQL = 	"select sosD.SubjectCode,sosd.SubjectName,sosd.SubjectType,sosD.TargetValue,sosD.HeadCompCode,sosD.HeadCompName from SOSTargetHeaders sosh " +
				 			" inner join SOSTargetDetails sosD on sosh.TargetCode=sosD.TargetCode " +
				 			" and sosh.CompanyCode=sosD.CompanyCode " +
				 			" inner join SOSTargetAssignment sosa on sosa.TargetCode=sosD.TargetCode " +
				 			" inner join SOSTargetHeaders soh on soh.TargetCode=sosD.TargetCode" +
				 			" where sosh.companycode= ? and  sosa.AssignmentCode in (?) " +
				 			//"  and sosh.TargetType=? " +
				 			" and sosd.SubjectType=?" +
				 			" and SubjectCode in (select SubjectCode  from SOSTargetDetails   " +
				 			 
				 			" where ParentCode =?  " +
 				 			" and CompanyCode = ?) and soh.TargetCode=?    " +
 				 			" and   soh.TargetType=? ";
//				 	SQL = 	" SELECT subjectcode,b.BrandDescription,SubjectType ,targetvalue  " +
//				 			" FROM SOSTargetAssignment ta inner join SOSBrands  b on  b.BrandCode=ta.subjectcode " +
//				 			" WHERE ta.CompanyCode = ? " +
//				 			" and( ta.AssignmentCode in (?) " +
//				 			" or  (   ta.AssignmentCode IN ( SELECT ClientPropertyValueCode FROM ClientProperties" +
//				 			" WHERE ClientCode IN (  ? ) ) )) " +
//				 			" and SubjectType=?  and SOSCategoryCode=?" +
//				 			" and Brandcode in (select Brandcode from SOSCategoryBrands where categorycode=?  " +
//				 			" and CompanyCode = ?)";
				 	// Compute the selection arguments
						selectionArguments = new String [] {	
								String.valueOf(companyCode),
								 String.valueOf ( clientCode )
							//	,String.valueOf ( clientCode )
								,String.valueOf ( 2 ),
								String.valueOf( categoryCode),
								//String.valueOf(categoryCode),
								String.valueOf( companyCode),
								String.valueOf( targetCode),
								String.valueOf( sosType )
								
								
						};
						Cursor cursor = DatabaseUtils.getInstance (SOSBrandDetailActivity. this ).getDaoSession ().getDatabase ()
								        .rawQuery(SQL , selectionArguments) ;
						
							// Move the cursor to the first raw
						if ( cursor.moveToFirst () ) {
							// Iterate over all raws
							do {
								SOSTracker sOSTracker = new SOSTracker ();
								sOSTracker.setBrandCode(  cursor.getString(0) );
								sOSTracker.setBrandName(  cursor.getString(1) );
								sOSTracker.setBdaMeter (  cursor.getDouble(3)) ;
								sOSTracker.setBrandtype(  cursor.getInt( 2));
								sOSTracker.setHeadCompCode(cursor.getString(4));
								sOSTracker.setHeadCompName(cursor.getString(5));
								
								if(oldtracker.containsKey( cursor.getString(0) ))
								sOSTracker.setCurrentShareOfShelf(oldtracker1.get(cursor.getString(0)) );
								else
									sOSTracker.setCurrentShareOfShelf(0.0);
								trackers.add ( sOSTracker );
							} while ( cursor.moveToNext () );
						} // End if
						// Close the cursor
						cursor.close ();
						cursor = null;
						 
					 
						 if(trackers ==null || (trackers!=null && trackers.size()==0)){
							 	SQL = 	"select sosD.SubjectCode,sosd.SubjectName,sosd.SubjectType,sosD.TargetValue,sosD.HeadCompCode,sosD.HeadCompName from SOSTargetHeaders sosh " +
							 			" inner join SOSTargetDetails sosD on sosh.TargetCode=sosD.TargetCode " +
							 			" and sosh.CompanyCode=sosD.CompanyCode " +
							 			" inner join SOSTargetAssignment sosa on sosa.TargetCode=sosD.TargetCode " +
							 			" inner join SOSTargetHeaders soh on soh.TargetCode=sosD.TargetCode" +
							 			" where sosh.companycode= ? and  sosa.AssignmentCode in (SELECT ClientPropertyValueCode FROM ClientProperties" +
							 			" WHERE ClientCode IN (  ? ) ) " +
							 			//"  and sosh.TargetType=? " +
							 			" and sosd.SubjectType=?" +
							 			" and SubjectCode in (select SubjectCode from SOSTargetDetails where  ParentCode =?  " +
			 				 			" and CompanyCode = ?) and soh.TargetCode=?  " +
			 				 			" and   soh.TargetType=?";
//							 	SQL = 	" SELECT subjectcode,b.BrandDescription,SubjectType ,targetvalue  " +
//							 			" FROM SOSTargetAssignment ta inner join SOSBrands  b on  b.BrandCode=ta.subjectcode " +
//							 			" WHERE ta.CompanyCode = ? " +
//							 			" and( ta.AssignmentCode in (?) " +
//							 			" or  (   ta.AssignmentCode IN ( SELECT ClientPropertyValueCode FROM ClientProperties" +
//							 			" WHERE ClientCode IN (  ? ) ) )) " +
//							 			" and SubjectType=?  and SOSCategoryCode=?" +
//							 			" and Brandcode in (select Brandcode from SOSCategoryBrands where categorycode=?  " +
//							 			" and CompanyCode = ?)";
							 	// Compute the selection arguments
									selectionArguments = new String [] {	
											String.valueOf(companyCode),
											 String.valueOf ( clientCode )
										//	,String.valueOf ( clientCode )
											,String.valueOf ( 2 ),
											String.valueOf(categoryCode),
											//String.valueOf(categoryCode),
											String.valueOf(companyCode),
											String.valueOf( targetCode),
											String.valueOf( sosType )
											
									};
									  cursor = DatabaseUtils.getInstance (SOSBrandDetailActivity. this ).getDaoSession ().getDatabase ()
											        .rawQuery(SQL , selectionArguments) ;
									
										// Move the cursor to the first raw
									if ( cursor.moveToFirst () ) {
										// Iterate over all raws
										do {
											SOSTracker sOSTracker = new SOSTracker ();
											sOSTracker.setBrandCode(  cursor.getString(0) );
											sOSTracker.setBrandName(  cursor.getString(1) );
											sOSTracker.setBdaMeter (  cursor.getDouble(3)) ;
											sOSTracker.setBrandtype(  cursor.getInt( 2));
											sOSTracker.setHeadCompCode(cursor.getString(4));
											sOSTracker.setHeadCompName(cursor.getString(5));
											if(oldtracker.containsKey( cursor.getString(0) ))
											sOSTracker.setCurrentShareOfShelf(oldtracker1.get(cursor.getString(0)) );
											else
												sOSTracker.setCurrentShareOfShelf(0.0);
											trackers.add ( sOSTracker );
										} while ( cursor.moveToNext () );
									} // End if
									// Close the cursor
									cursor.close ();
									cursor = null;
									 
								  
							 
						 }
						 
					// Return the trackers list
					return trackers;
				} catch ( Exception exception) {
					// Set flag
					activityEnded = true;
					// Activity ended prematurely
					return null;
				} // End of try-catch block
					
			}
			
			/*
			 * Runs on the UI thread after doInBackground(Params...).
			 * 
			 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
			 */
			@Override
			protected void onPostExecute ( ArrayList < SOSTracker > trackers ) {
				// Check if the activity has ended
				if ( activityEnded )
					// Do nothing
					return;
				// Determine if the activity must be finished
				if ( error ) {
					// Indicate that the activity cannot be displayed
					new AppToast ( SOSBrandDetailActivity.this )
						.setIcon ( R.drawable.warning )
						.setText ( AppResources.getInstance ( SOSBrandDetailActivity.this ).getString ( SOSBrandDetailActivity.this , R.string.share_of_shelf_tracker_populate_list_error_message ) )
						.setDuration ( Toast.LENGTH_LONG )
						.show ();
					// Initialize the orders list
					SOSBrandDetailActivity.this.trackers = new ArrayList < SOSTracker > ();
					// Exit method
					return;
				} // End if
				// Set a new adapter
				setListAdapter ( new SOSBrandDetailsAdapter ( SOSBrandDetailActivity.this , R.layout.sos_brand_item , new ArrayList < SOSTracker > ( trackers )   , true ) );
	    		// Refresh the action bar
	    		invalidateOptionsMenu ();
			}
			
		}
		 @SuppressWarnings("unchecked")
			@Override
		    public boolean onOptionsItemSelected ( MenuItem menuItem ) {
		    	  if ( menuItem.getItemId () == R.id.action_basket ) {
		    		  ArrayList <SOSTracker>	tracker  = new ArrayList < SOSTracker > ();
	 	    		 for(SOSTracker t: trackers ){
	 	    			 if(t.getCurrentShareOfShelf()>=0)
	 	    				tracker.add(t) ;
	 	    		 }
 
		    		    Intent returnIntent = new Intent();
		    	        returnIntent.putExtra( SOSTrackerDetailActivity.SOSSELECTEDBRAND, tracker );
		    	        returnIntent.putExtra( SOSTrackerDetailActivity.SELECTEDCATEGORY, categoryCode );
		    	        setResult(RESULT_OK, returnIntent	);
		    	        finish();
		    	        
		    			}
		 return true;
		 }
	}