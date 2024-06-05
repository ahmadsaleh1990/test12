package me.SyncWise.Android.Modules.ShareOfShelf;

 
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.AppToast;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Vibration;
import me.SyncWise.Android.Database.DaoSession;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.SOSTrackerDetails;
import me.SyncWise.Android.Database.SOSTrackerDetailsDao;
import me.SyncWise.Android.Database.SOSTrackerHeaders;
import me.SyncWise.Android.Database.SOSTrackerHeadersDao;
import me.SyncWise.Android.Database.ShareOfShelfTracker;
import me.SyncWise.Android.Database.Visits;
import me.SyncWise.Android.Database.VisitsUtils;
import me.SyncWise.Android.Gson.BaseTimerListActivity;
import me.SyncWise.Android.Modules.Call.CallAction;
import me.SyncWise.Android.Modules.Call.CallMenuActivity;
import me.SyncWise.Android.Modules.Journey.Call;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SOSTrackerDetailActivity extends BaseTimerListActivity  {
	public static final String CALL = SOSTrackerDetailActivity.class.getName () + ".CALL";
	public static final String VISIT = SOSTrackerDetailActivity.class.getName () + ".VISIT";
	private static final int SOS_BRAND = 21;
	public static final String SOSSELECTEDBRAND = SOSTrackerDetailActivity.class.getName () + ".SOSSELECTEDBRAND";
	public static final String SELECTEDCATEGORY = SOSTrackerDetailActivity.class.getName () + ".SELECTEDCATEGORY";
    public HashMap<String, ArrayList<SOSTracker>> selectedBrands ;
	
	private ArrayList <SOSTracker>	trackers = new ArrayList < SOSTracker > ();
	protected static PopulateList populateList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	// Change the title associated with this activity
			setTitle ( AppResources.getInstance ( this ).getString ( this , R.string.share_of_shelf_tracker_details_activity_title ) );
			// Set the activity content from a layout resource.
			setContentView ( R.layout.sos_tracker_details_activity );
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
			selectedBrands= new HashMap<String, ArrayList<SOSTracker>>();
			TextView label_item = (TextView) findViewById(R.id.label_item);
			label_item.setText("Category");
			trackers=null;
//			getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
//	            @Override
//	            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//	            	SOSTracker 	returnModification = (SOSTracker) ( (BaseAdapter) getListAdapter () ).getItem ( position );
//	        	    Toast.makeText(SOSTrackerDetailActivity.this, ""+returnModification.getBdaMeter(), 100000).show();
//	        	    Intent intent = new Intent(   SOSTrackerDetailActivity.this , SOSBrandDetailActivity.class);
//	        		intent.putExtra( SOSBrandDetailActivity.CATEGORY , returnModification.getBrandCode() );
//	        		intent.putExtra( SOSBrandDetailActivity.CALL ,getIntent ().getSerializableExtra ( CALL ) );
//	        		    
//	        		//    intent.putExtra( DivisionPromotionActivity.SELECTED_PROMOTIONNEW , selectedPromotion );
//	        	 
//	        		    startActivityForResult( intent , SOS_BRAND);
//	            }
//	            });
			 
		 	
			TextView label_bda_meter = (TextView) findViewById(R.id.label_bda_meter);
			label_bda_meter.setText("Full Size") ;
			SOSMode=0;
			promptType();
		//	populate ();
	}
	private int SOSMode;
	/**
	 * Prompts the user to select a company.
	 */
	private void promptType() {
	 
		 
			// Retrieve the collection mode descriptions
			String[] collectionModeDescriptions = new String[] {
				//	AppResources.getInstance(this).getString(this, R.string.collection_mode_on_account_label),
					"Share Of Shelf",
					"Share of Display" 
					};
			// Prompt the user to choose a mode
			AppDialog.getInstance().displayList(
					this,
					 
							"Select Share Of Shelf Type" ,
					collectionModeDescriptions, AppDialog.Cancelable.BACK_ONLY,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Set the selected mode
							SOSMode = which +1 ;
						 populate();
							
						 
						}
					}, new DialogInterface.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							 
							finish();
						}
					});
	}
	   @Override
	    public boolean onCreateOptionsMenu ( Menu menu ) {
 
	  	getMenuInflater ().inflate ( R.menu.action_bar , menu );
		me.SyncWise.Android.Utilities.MenuItem.enable ( menu.findItem ( R.id.action_save  ) );
	    return true;
	    }
	   
	   
	   public boolean onOptionsItemSelected ( MenuItem menuItem ) {
	    	  if ( menuItem.getItemId () == R.id.action_save ) {
	    		  AppDialog.getInstance ().displayAlert ( this ,
	      				null ,
	      				AppResources.getInstance ( this ).getString ( this , R.string.save_confirmation_message ) ,
	      				AppDialog.ButtonsType.YesNo ,
	      				new DialogInterface.OnClickListener () {
	  						@Override
	  						public void onClick ( DialogInterface dialog , int which ) {
	  							// Determine the clicked button
	  							switch ( which ) {
	  							case DialogInterface.BUTTON_POSITIVE:
	  								// Save the transaction(s)
	  								saveTrackers ();
	  								break;
	  							case DialogInterface.BUTTON_NEGATIVE:
	  								// Dismiss dialog
	  								AppDialog.getInstance ().dismiss ();
	  								break;
	  							} // End switch
	  						}
	  					} );
	      		
	      		// Consume event
	      		return true;
	    		  
	    		  
//	     ArrayList <SOSTracker>	tracker  = new ArrayList < SOSTracker > ();
//  		 for(SOSTracker t: trackers ){
//  			 if(t.getCurrentShareOfShelf()>0)
//  				tracker.add(t) ;
	    	  }
	 return true;
	 }
	   private void saveTrackers () {
	    	// Make sure the trackers list is valid
	    	if ( trackers == null )
	    		// Exit method
	    		return;
	    	
			// Flag used to indicate whether an error occurred or not
			boolean error = false;
			// Retrieve the user code
			String userCode = DatabaseUtils.getCurrentUserCode ( this );
			// Retrieve the client code
			String clientCode = ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClientDivision ().getClientCode ();
			// Retrieve the company code
			String companyCode = ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClientDivision ().getCompanyCode ();
			// Retrieve the division code
			String divisionCode = ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClientDivision ().getDivisionCode ();
			// Retrieve the visit ID
			Long visitID = ( (Visits) getIntent ().getSerializableExtra ( VISIT ) ).getVisitID ();
			String journeyCode = ( (Visits) getIntent ().getSerializableExtra ( VISIT ) ).getJourneyCode ();
			// Retrieve the current date
			Calendar now = Calendar.getInstance ();
			// Build a new transaction ID
			Long transactionID = VisitsUtils.getVisitID ( now );
			// Retrieve a reference to the dao session
			DaoSession daoSession = DatabaseUtils.getInstance ( SOSTrackerDetailActivity.this ).getDaoSession ();
			
			try {
				ArrayList<SOSTrackerHeaders> ssss=(ArrayList<SOSTrackerHeaders>) daoSession.getSOSTrackerHeadersDao() .queryBuilder().where(SOSTrackerHeadersDao.Properties.JounreyCode.eq( journeyCode)
						,SOSTrackerHeadersDao.Properties.ClientCode.eq( clientCode)
						,SOSTrackerHeadersDao.Properties.MeasurementType.eq( SOSMode)).list();
				
				if(ssss!=null && ssss.size()>1){
					for(SOSTrackerHeaders ss:ssss)
						daoSession.getSOSTrackerHeadersDao().delete(ss);
				}
				
			 	SOSTrackerHeaders sosh=	daoSession.getSOSTrackerHeadersDao() .queryBuilder().where(SOSTrackerHeadersDao.Properties.JounreyCode.eq( journeyCode)
						,SOSTrackerHeadersDao.Properties.ClientCode.eq( clientCode)
						,SOSTrackerHeadersDao.Properties.MeasurementType.eq( SOSMode)).unique();
				if(sosh!=null ){
					transactionID=Long.valueOf( sosh.getSOSCode());
					daoSession.getSOSTrackerHeadersDao() .delete(sosh);
					 List< SOSTrackerDetails>  sosd=	daoSession.getSOSTrackerDetailsDao() .queryBuilder()
							.where(SOSTrackerDetailsDao.Properties.SOSCode.eq( sosh.getSOSCode())
							 ).list() ;
					 daoSession.getSOSTrackerDetailsDao() .deleteInTx (sosd);
					}
				
				
				// Begin transaction
				daoSession.getDatabase ().beginTransaction ();
				daoSession.getSOSTrackerHeadersDao()  .insert(new SOSTrackerHeaders(null,
						transactionID+"",
						clientCode,
						userCode, 
						visitID, 
						companyCode,
						journeyCode,
						SOSMode,
						IsProcessedUtils.isNotSync () , // IsProcessed
						now.getTime () ) ); // StampDate 
				// Iterate over the trackers
				int lineID=0;
				for ( SOSTracker tracker : trackers )
				{	
					daoSession.getSOSTrackerDetailsDao()  .insert(new SOSTrackerDetails(null, 
							transactionID+"",
							lineID, 
							tracker.getBrandCode(),
							1, 
							tracker.getBdaMeter(),
							null, tracker.getCurrentShareOfShelf(),
							tracker.getHeadCompCode(),
							now.getTime ()) );
					lineID++;
				if(selectedBrands.containsKey(tracker.getBrandCode())){
					ArrayList<SOSTracker> SOSTRa=selectedBrands.get(tracker.getBrandCode());
					for ( SOSTracker tracker1 : SOSTRa )
					{	
						daoSession.getSOSTrackerDetailsDao()  .insert(new SOSTrackerDetails(null, 
								transactionID+"",
								lineID, 
								tracker1.getBrandCode(),
								2, 
								tracker1.getCurrentShareOfShelf(),//
								tracker.getBrandCode(), tracker1.getBDAMeter(),
								tracker1.getHeadCompCode(),
								now.getTime ()) );
						lineID++;}
			  
				}else{
					String SQL=null;
				 	String selectionArguments [] = null;
 
				 	SQL = 	"select sosD.SubjectCode,sosd.SubjectName,sosd.SubjectType,sosD.TargetValue,sosD.HeadCompCode,sosD.HeadCompName from SOSTargetHeaders sosh " +
				 			" inner join SOSTargetDetails sosD on sosh.TargetCode=sosD.TargetCode " +
				 			" and sosh.CompanyCode=sosD.CompanyCode " +
				 			" inner join SOSTargetAssignment sosa on sosa.TargetCode=sosD.TargetCode " +
				 			" inner join SOSTargetHeaders soh on soh.TargetCode=sosD.TargetCode" +
				 			" where sosh.companycode= ? and  sosa.AssignmentCode in ( ?  ) " +
				 			//"  and sosh.TargetType=? " +
				 			" and sosd.SubjectType=?" + 
				 			" and SubjectCode in (select SubjectCode from SOSTargetDetails where  ParentCode =?  " +
 				 			" and CompanyCode = ?)" +
 				 			" and soh.TargetCode =?  " +
			 				" and soh.TargetType=?"; 
				 	// Compute the selection arguments
					 	
								selectionArguments = new String [] {	
										String.valueOf(companyCode),
										 String.valueOf ( clientCode )
									//	,String.valueOf ( clientCode )
										,String.valueOf ( 2 ),
										String.valueOf(tracker.getBrandCode()),
										//String.valueOf(categoryCode),
										String.valueOf(companyCode),
										String.valueOf(tracker.getTargetCode()),
										String.valueOf( SOSMode )
										
								 
								
						};
						Cursor cursor = DatabaseUtils.getInstance (  this ).getDaoSession ().getDatabase ()
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
							 
									sOSTracker.setCurrentShareOfShelf(0.0);
									daoSession.getSOSTrackerDetailsDao()  .insert(new SOSTrackerDetails(null, 
											transactionID+"",
											lineID, 
											cursor.getString(0),
											2, 
											0.0,
											tracker.getBrandCode(),cursor.getDouble(3),cursor.getString(4),
											now.getTime ()) );
									lineID++;
							} while ( cursor.moveToNext () );
						} // End if
					if(cursor.getCount()==0){
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
		 				 			" and CompanyCode = ?)" +
		 				 			" and soh.TargetCode =?  " +
		 				 			 
			 				 		" and   soh.TargetType=? ";
						 	// Compute the selection arguments
							 	
							selectionArguments = new String [] {	
									String.valueOf(companyCode),
									 String.valueOf ( clientCode )
								//	,String.valueOf ( clientCode )
									,String.valueOf ( 2 ),
									String.valueOf(tracker.getBrandCode()),
									//String.valueOf(categoryCode),
									String.valueOf(companyCode),
									String.valueOf(tracker.getTargetCode()),
									String.valueOf( SOSMode )
									
							};
								Cursor cursor1 = DatabaseUtils.getInstance (  this ).getDaoSession ().getDatabase ()
										        .rawQuery(SQL , selectionArguments) ;
								
									// Move the cursor to the first raw
								if ( cursor1.moveToFirst () ) {
									// Iterate over all raws
									do {
										SOSTracker sOSTracker = new SOSTracker ();
										sOSTracker.setBrandCode(  cursor1.getString(0) );
										sOSTracker.setBrandName(  cursor1.getString(1) );
										sOSTracker.setBdaMeter (  cursor1.getDouble(3)) ;
										sOSTracker.setBrandtype(  cursor1.getInt( 2));
										 
									 
											sOSTracker.setCurrentShareOfShelf(0.0);
											daoSession.getSOSTrackerDetailsDao()  .insert(new SOSTrackerDetails(null, 
													transactionID+"",
													lineID, 
													cursor1.getString(0),
													2, 
													0.0,
													tracker.getBrandCode(),cursor1.getDouble(3),cursor1.getString(4)
													,now.getTime ()) );
											lineID++;
									} while ( cursor1.moveToNext () );
								} // End if
								// Close the cursor
								cursor1.close ();
								cursor1 = null;
						}
						// Close the cursor
						cursor.close ();
						cursor = null;
						 
				}
				}
	    		// Indicate that a share of shelf tracking was successfully performed during this visit
	    		CallAction.setCallActionStatus ( this , visitID , CallAction.ID.SHARE_OF_SHELF1 , true );	
				// Commit changes
	    		daoSession.getDatabase ().setTransactionSuccessful ();
				// Indicate that the save was successful
				Vibration.vibrate ( SOSTrackerDetailActivity.this );
			} catch ( Exception exception ) {
				// Indicate that an error occurred
				error = true;
			} finally {
				// End transaction
				daoSession.getDatabase ().endTransaction ();
			} // End try-catch-finally block
			
	    	// Call this to set the result that your activity will return to its caller
			Intent intent = new Intent();
			intent.putExtra ( CallMenuActivity.ACTION_RESULT_SUCCESS , ! error );
			//intent.putExtra ( SalesOrderActivity.PRINT , transactionHeaderCode );
	    	setResult ( RESULT_OK , intent );
			// Finish this activity
			finish ();
			// Indicate that the save was successful
			Vibration.vibrate ( this );
	    }
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
    	SOSTracker 	returnModification = (SOSTracker) ( (BaseAdapter) getListAdapter () ).getItem ( position );
 
	    Intent intent = new Intent(   SOSTrackerDetailActivity.this , SOSBrandDetailActivity.class);
		intent.putExtra( SOSBrandDetailActivity.CATEGORY , returnModification.getBrandCode() );
		intent.putExtra( SOSBrandDetailActivity.CALL ,getIntent ().getSerializableExtra ( CALL ) );
		intent.putExtra( SOSBrandDetailActivity.VISIT ,getIntent ().getSerializableExtra ( VISIT ) );
		 intent.putExtra( SOSBrandDetailActivity.SELECTED_BRAND , selectedBrands );
		 intent.putExtra( SOSBrandDetailActivity.TARGETCODE , returnModification.getTargetCode() );
		 intent.putExtra( SOSBrandDetailActivity.SOSTYPE ,  SOSMode);
		    startActivityForResult( intent , SOS_BRAND);
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
					
					if(trackers==null){
					trackers = new ArrayList < SOSTracker > ();
					
					// Retrieve the client code
					String clientCode = ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClientDivision ().getClientCode ();
					// Retrieve the company code
					String companyCode = ( (Call) getIntent ().getSerializableExtra ( CALL ) ).getClientDivision ().getCompanyCode ();
					DaoSession daoSession = DatabaseUtils.getInstance ( SOSTrackerDetailActivity.this ).getDaoSession ();
					String journeyCode = ( (Visits) getIntent ().getSerializableExtra ( VISIT ) ).getJourneyCode ();
				 
						
				 
					
					String SQL=null;
				 	String selectionArguments [] = null;
if(SOSMode==1){
	SQL = 	"select sosD.SubjectCode,sosd.SubjectName,sosd.SubjectType,sosD.TargetValue, sosh.TargetCode from SOSTargetHeaders sosh " +
 			" inner join SOSTargetDetails sosD on sosh.TargetCode=sosD.TargetCode " +
 			" and sosh.CompanyCode=sosD.CompanyCode " +
 		 
 			" where sosh.companycode= ?   " +
 			"  and sosh.TargetType=? and sosd.SubjectType=?  and sosd.ClientCode=? ";
 			
 			
// 			" SELECT subjectcode,cb.CategoryName,SubjectType ,targetvalue   " +
// 			" FROM SOSTargetAssignment ta inner join SOSCategories cb on cb.CategoryCode=ta.subjectcode " +
// 			" WHERE ta.CompanyCode = ? " +
// 			" and ( ta.AssignmentCode in (?) " +
// 			" or  ( ta.AssignmentCode IN ( SELECT ClientPropertyValueCode FROM ClientProperties" +
// 			" WHERE ClientCode IN (  ? ) ) )) " +
// 			" and ta.SubjectType=  ? ";
 	// Compute the selection arguments
		selectionArguments = new String [] {	
				String.valueOf(companyCode)
				
				,String.valueOf (  SOSMode )
				,String.valueOf ( 1 ),
				 String.valueOf ( clientCode )
				
		};
		Cursor cursor = DatabaseUtils.getInstance (SOSTrackerDetailActivity. this ).getDaoSession ().getDatabase ()
				        .rawQuery(SQL , selectionArguments) ;
		
			// Move the cursor to the first raw
		if ( cursor.moveToFirst () ) {
			// Iterate over all raws
			do {
				SOSTracker sOSTracker = new SOSTracker ();
				sOSTracker.setBrandCode(  cursor.getString(0) );
				sOSTracker.setBrandName(  cursor.getString(1) );
//				if(SOSMode==1)
				sOSTracker.setBdaMeter (  cursor.getDouble(3)) ; //
//				else
//					sOSTracker.setBdaMeter (  0) ; //	
				sOSTracker.setCurrentShareOfShelf (  cursor.getDouble(3)) ; 
				sOSTracker.setBrandtype(  cursor.getInt( 2));
				sOSTracker.setTargetCode(cursor.getString(4));
				sOSTracker.setPreviousShareOfShelf(0);
				SOSTrackerHeaders sosh=	daoSession.getSOSTrackerHeadersDao() .queryBuilder().where(SOSTrackerHeadersDao.Properties.JounreyCode.eq( journeyCode)
						,SOSTrackerHeadersDao.Properties.ClientCode.eq( clientCode),SOSTrackerHeadersDao.Properties.MeasurementType.eq( SOSMode)
						).unique();
				if(sosh!=null){
					 List< SOSTrackerDetails>  sosdCat=	daoSession.getSOSTrackerDetailsDao() .queryBuilder()
								.where(SOSTrackerDetailsDao.Properties.SOSCode.eq( sosh.getSOSCode()),
										SOSTrackerDetailsDao.Properties.SubjectCode.eq( sOSTracker.getBrandCode()) ).list() ;
						if(sosdCat!=null && (sosdCat!=null && sosdCat.size()  ==0)){
					 sOSTracker.setBdaMeter ( sosdCat.get(0).getTargetValue());}
					 List< SOSTrackerDetails>  sosd=	daoSession.getSOSTrackerDetailsDao() .queryBuilder()
								.where(SOSTrackerDetailsDao.Properties.SOSCode.eq( sosh.getSOSCode()),
										SOSTrackerDetailsDao.Properties.ParentCode.eq( sOSTracker.getBrandCode()) ).list() ;
					ArrayList<SOSTracker> viewSos=new ArrayList<SOSTracker>();
					 for(SOSTrackerDetails sd:sosd)
					{ SOSTracker s =new SOSTracker();
					s.setBrandCode(  sd.getSubjectCode() );
					s.setBrandName(   sd.getSubjectCode() );
				 	s.setCurrentShareOfShelf (  sd.getTargetValue()) ; 
					s.setBrandtype(  2);
				
					viewSos.add(s);
					} 
					selectedBrands.put(sOSTracker.getBrandCode(), viewSos);
					sOSTracker.setPreviousShareOfShelf(1);
					 
				}
				
				trackers.add ( sOSTracker );
			} while ( cursor.moveToNext () );
		} // End if
		// Close the cursor
		cursor.close ();
		cursor = null;
}else{
				 	SQL = 	"select sosD.SubjectCode,sosd.SubjectName,sosd.SubjectType,sosD.TargetValue, sosh.TargetCode from SOSTargetHeaders sosh " +
				 			" inner join SOSTargetDetails sosD on sosh.TargetCode=sosD.TargetCode " +
				 			" and sosh.CompanyCode=sosD.CompanyCode " +
				 			" inner join SOSTargetAssignment sosa on sosa.TargetCode=sosD.TargetCode " +
				 			" where sosh.companycode= ? and  sosa.AssignmentCode in (?) " +
				 			"  and sosh.TargetType=? and sosd.SubjectType=? and AssignmentType=3";
				 			
				 			
//				 			" SELECT subjectcode,cb.CategoryName,SubjectType ,targetvalue   " +
//				 			" FROM SOSTargetAssignment ta inner join SOSCategories cb on cb.CategoryCode=ta.subjectcode " +
//				 			" WHERE ta.CompanyCode = ? " +
//				 			" and ( ta.AssignmentCode in (?) " +
//				 			" or  ( ta.AssignmentCode IN ( SELECT ClientPropertyValueCode FROM ClientProperties" +
//				 			" WHERE ClientCode IN (  ? ) ) )) " +
//				 			" and ta.SubjectType=  ? ";
				 	// Compute the selection arguments
						selectionArguments = new String [] {	
								String.valueOf(companyCode),
								 String.valueOf ( clientCode )
								,String.valueOf (  SOSMode )
								,String.valueOf ( 1 )
								
						};
						Cursor cursor = DatabaseUtils.getInstance (SOSTrackerDetailActivity. this ).getDaoSession ().getDatabase ()
								        .rawQuery(SQL , selectionArguments) ;
						
							// Move the cursor to the first raw
						if ( cursor.moveToFirst () ) {
							// Iterate over all raws
							do {
								SOSTracker sOSTracker = new SOSTracker ();
								sOSTracker.setBrandCode(  cursor.getString(0) );
								sOSTracker.setBrandName(  cursor.getString(1) );
//								if(SOSMode==1)
								sOSTracker.setBdaMeter (  cursor.getDouble(3)) ; //
//								else
//									sOSTracker.setBdaMeter (  0) ; //	
								sOSTracker.setCurrentShareOfShelf (  cursor.getDouble(3)) ; 
								sOSTracker.setBrandtype(  cursor.getInt( 2));
								sOSTracker.setTargetCode(cursor.getString(4));
								sOSTracker.setPreviousShareOfShelf(0);
								SOSTrackerHeaders sosh=	daoSession.getSOSTrackerHeadersDao() .queryBuilder().where(SOSTrackerHeadersDao.Properties.JounreyCode.eq( journeyCode)
										,SOSTrackerHeadersDao.Properties.ClientCode.eq( clientCode),SOSTrackerHeadersDao.Properties.MeasurementType.eq( SOSMode)
										).unique();
								if(sosh!=null){
									 List< SOSTrackerDetails>  sosdCat=	daoSession.getSOSTrackerDetailsDao() .queryBuilder()
												.where(SOSTrackerDetailsDao.Properties.SOSCode.eq( sosh.getSOSCode()),
														SOSTrackerDetailsDao.Properties.SubjectCode.eq( sOSTracker.getBrandCode()) ).list() ;
										if(sosdCat!=null && (sosdCat!=null && sosdCat.size()  ==0)){
									 sOSTracker.setBdaMeter ( sosdCat.get(0).getTargetValue());
									 }
									 List< SOSTrackerDetails>  sosd=	daoSession.getSOSTrackerDetailsDao() .queryBuilder()
												.where(SOSTrackerDetailsDao.Properties.SOSCode.eq( sosh.getSOSCode()),
														SOSTrackerDetailsDao.Properties.ParentCode.eq( sOSTracker.getBrandCode()) ).list() ;
									ArrayList<SOSTracker> viewSos=new ArrayList<SOSTracker>();
									 for(SOSTrackerDetails sd:sosd)
									{ SOSTracker s =new SOSTracker();
									s.setBrandCode(  sd.getSubjectCode() );
									s.setBrandName(   sd.getSubjectCode() );
								 	s.setCurrentShareOfShelf (  sd.getTargetValue()) ; 
									s.setBrandtype(  2);
								
									viewSos.add(s);
									} 
									selectedBrands.put(sOSTracker.getBrandCode(), viewSos);
									sOSTracker.setPreviousShareOfShelf(1);
									
								}
								
								trackers.add ( sOSTracker );
							} while ( cursor.moveToNext () );
						} // End if
						// Close the cursor
						cursor.close ();
						cursor = null;
						 
					 if(trackers ==null || (trackers!=null && trackers.size()==0)){
					 
						 	SQL = 	"select sosD.SubjectCode,sosd.SubjectName,sosd.SubjectType,sosD.TargetValue,sosh.TargetCode from SOSTargetHeaders sosh " +
						 			" inner join SOSTargetDetails sosD on sosh.TargetCode=sosD.TargetCode " +
						 			" and sosh.CompanyCode=sosD.CompanyCode " +
						 			" inner join SOSTargetAssignment sosa on sosa.TargetCode=sosD.TargetCode " +
						 			" where sosh.companycode= ? and  sosa.AssignmentCode IN ( SELECT ClientPropertyValueCode FROM ClientProperties" +
 						 			" WHERE ClientCode IN (  ? ) ) " +
						 			"  and sosh.TargetType=? and sosd.SubjectType=? and AssignmentType=4";
						 			
						 			
//						 			" SELECT subjectcode,cb.CategoryName,SubjectType ,targetvalue   " +
//						 			" FROM SOSTargetAssignment ta inner join SOSCategories cb on cb.CategoryCode=ta.subjectcode " +
//						 			" WHERE ta.CompanyCode = ? " +
//						 			" and ( ta.AssignmentCode in (?) " +
//						 			" or  ( ta.AssignmentCode IN ( SELECT ClientPropertyValueCode FROM ClientProperties" +
//						 			" WHERE ClientCode IN (  ? ) ) )) " +
//						 			" and ta.SubjectType=  ? ";
						 	// Compute the selection arguments
								selectionArguments = new String [] {	
										String.valueOf(companyCode),
										 String.valueOf ( clientCode )
										,String.valueOf (  SOSMode )
										,String.valueOf ( 1 )
										
								};
								  cursor = DatabaseUtils.getInstance (SOSTrackerDetailActivity. this ).getDaoSession ().getDatabase ()
										        .rawQuery(SQL , selectionArguments) ;
								
									// Move the cursor to the first raw
								if ( cursor.moveToFirst () ) {
									// Iterate over all raws
									do {
										SOSTracker sOSTracker = new SOSTracker ();
										sOSTracker.setBrandCode(  cursor.getString(0) );
										sOSTracker.setBrandName(  cursor.getString(1) );
										sOSTracker.setBdaMeter (  cursor.getDouble(3)) ; 
										sOSTracker.setCurrentShareOfShelf (  cursor.getDouble(3)) ;
										sOSTracker.setTargetCode(cursor.getString(4));
										sOSTracker.setBrandtype(  cursor.getInt( 2));
										sOSTracker.setPreviousShareOfShelf(0);
										SOSTrackerHeaders sosh=	daoSession.getSOSTrackerHeadersDao() .queryBuilder().where(SOSTrackerHeadersDao.Properties.JounreyCode.eq( journeyCode)
												,SOSTrackerHeadersDao.Properties.ClientCode.eq( clientCode),SOSTrackerHeadersDao.Properties.MeasurementType.eq( SOSMode)
												).unique();
										if(sosh!=null){
											 List< SOSTrackerDetails>  sosdCat=	daoSession.getSOSTrackerDetailsDao() .queryBuilder()
														.where(SOSTrackerDetailsDao.Properties.SOSCode.eq( sosh.getSOSCode()),
																SOSTrackerDetailsDao.Properties.SubjectCode.eq( sOSTracker.getBrandCode()) ).list() ;
											if(sosdCat!=null && (sosdCat!=null && sosdCat.size()  ==0)){
											 sOSTracker.setBdaMeter ( sosdCat.get(0).getTargetValue());}
											 List< SOSTrackerDetails>  sosd=	daoSession.getSOSTrackerDetailsDao() .queryBuilder()
														.where(SOSTrackerDetailsDao.Properties.SOSCode.eq( sosh.getSOSCode()),
																SOSTrackerDetailsDao.Properties.ParentCode.eq( sOSTracker.getBrandCode()) ).list() ;
											ArrayList<SOSTracker> viewSos=new ArrayList<SOSTracker>();
											 for(SOSTrackerDetails sd:sosd)
											{ SOSTracker s =new SOSTracker();
											s.setBrandCode(  sd.getSubjectCode() );
											s.setBrandName(   sd.getSubjectCode() );
										 
										    s.setCurrentShareOfShelf (  sd.getTargetValue()) ; 
											s.setBrandtype(  2);
											viewSos.add(s);
											} 
											selectedBrands.put(sOSTracker.getBrandCode(), viewSos);
											sOSTracker.setPreviousShareOfShelf(1);
											
										}
										
										trackers.add ( sOSTracker );
									} while ( cursor.moveToNext () );
								} // End if
								// Close the cursor
								cursor.close ();
								cursor = null;
								 
					 }
}
					// Return the trackers list
					return trackers;
					}
					else{
						for(SOSTracker s:trackers)
						{
							if(selectedBrands.containsKey(s.getBrandCode()))
								s.setPreviousShareOfShelf(1);
							else
								s.setPreviousShareOfShelf(0);
						}
					return 	trackers;
					}
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
					new AppToast ( SOSTrackerDetailActivity.this )
						.setIcon ( R.drawable.warning )
						.setText ( AppResources.getInstance ( SOSTrackerDetailActivity.this ).getString ( SOSTrackerDetailActivity.this , R.string.share_of_shelf_tracker_populate_list_error_message ) )
						.setDuration ( Toast.LENGTH_LONG )
						.show ();
					// Initialize the orders list
					SOSTrackerDetailActivity.this.trackers = new ArrayList < SOSTracker > ();
					// Exit method
					return;
				} // End if
				// Set a new adapter
				setListAdapter ( new SOSTrackerDetailsAdapter ( SOSTrackerDetailActivity.this , R.layout.sos_item , new ArrayList < SOSTracker > ( trackers )   ,SOSMode==1?false:true ) );
	    		// Refresh the action bar
	    		invalidateOptionsMenu ();
			}
			
		}
		
		@SuppressWarnings("unchecked")
		protected void onActivityResult ( int requestCode , int resultCode , Intent data ) {
	    	 
	    	// Check if the result is successful and the intent is valid
	    	if ( resultCode != RESULT_OK || data == null )
	    		// Exit method
	    		return;
	    	if (requestCode == SOS_BRAND && data != null) { 
	    	String category=	data.getStringExtra(SELECTEDCATEGORY);
	    	ArrayList<SOSTracker> tracker = new ArrayList<SOSTracker>(); 
	    	  //promoted = (  HashMap<String, ArrayList<Tracker>>)  data.getSerializableExtra ( Family_COLORS_QTTY ); 
	    	  tracker=(ArrayList<SOSTracker>)data.getSerializableExtra ( SOSSELECTEDBRAND ); 
	            ( (InputMethodManager) getBaseContext ().getSystemService ( INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow ( findViewById ( R.id.view_clear_focus ).getWindowToken (), 0 );
	    
	            selectedBrands.put(category, tracker);
	             
	     
	            	 populate();
	    		} 
	    	}
	    
 
	    
			
	  
	 
		
	}