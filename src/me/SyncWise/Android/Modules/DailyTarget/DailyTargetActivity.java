package me.SyncWise.Android.Modules.DailyTarget;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import de.greenrobot.dao.AbstractDao;

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.Network;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.ConnectionSettings;
import me.SyncWise.Android.Database.ConnectionSettingsDao;
import me.SyncWise.Android.Database.ConnectionSettingsUtils;
import me.SyncWise.Android.Database.DailyTargetAchievements;
import me.SyncWise.Android.Database.DailyTargetAchievementsDao;
import me.SyncWise.Android.Database.DaoSession;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.Divisions;
import me.SyncWise.Android.Database.DivisionsDao;
import me.SyncWise.Android.Database.DivisionsUtils;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.MovementDetails;
import me.SyncWise.Android.Database.MovementDetailsDao;
import me.SyncWise.Android.Database.MovementHeaders;
import me.SyncWise.Android.Database.MovementHeadersDao;
import me.SyncWise.Android.Database.TransactionDetails;
import me.SyncWise.Android.Database.TransactionDetailsDao;
import me.SyncWise.Android.Database.TransactionHeaders;
import me.SyncWise.Android.Database.TransactionHeadersDao;
 
import me.SyncWise.Android.Database.TransactionSequences;
import me.SyncWise.Android.Database.TransactionSequencesDao;
import me.SyncWise.Android.Database.Users;
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Database.Visits;
import me.SyncWise.Android.Gson.CommonUtilities;
import me.SyncWise.Android.Modules.DataManagement.DataManagementActivity.RequestCode;
import me.SyncWise.Android.Modules.ItemsList.ItemsListActivity;
import me.SyncWise.Android.Modules.Sync.SyncHelper;
import me.SyncWise.Android.Modules.Sync.SyncListener;
import me.SyncWise.Android.Modules.Sync.SyncUtils;
import me.SyncWise.Android.Widgets.QuickAction.ActionItem;
import me.SyncWise.Android.Widgets.QuickAction.QuickAction;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;

public class DailyTargetActivity extends Activity implements SyncListener {
	private double TargetAmount = 0.0;
	private double RemainingDays = 0.0;
	private double D_WorkingDays = 0.0;
	private double D_BalanceDays = 0.0;
	private double MonthlyAchievementAmount = 0.0;
	private double Monthly_remaining = 0.0;
	private double DailyBalanceToAchieve = 0.0;
	private double DailyAchievementAmount = 0.0;
	private double Daily_remaining = 0.0;
	private double MonthlyAchievementPercentage = 0.0;
	private double DailyAchievementPercentage = 0.0;
	private double Monthly_remaining_percentage = 0.0;
	private double Daily_remaining_percentage = 0.0;

	private TextView desscription_daily;
	private TextView valid_from_daily;
	private TextView valid_to_daily;
	private TextView total_target;
	private TextView remainig_days;
	private TextView WorkingDays;
	private TextView BalanceDays;
	private List<DailyTargetAchievements> target;

	private PieChart piechart_Monthly;
	private PieChart piechart_daily;
    private DecimalFormat withouZero;
    private DecimalFormat twoDigits;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.daily_target_activity);
		setTitle(AppResources.getInstance(this).getString(this,
				R.string. data_daily_target));
		setTheme(R.style.Theme_PageIndicatorDefaults);
		setupQuickAction();
	}

	private void setupQuickAction() {
		piechart_Monthly = (PieChart) findViewById(R.id.piechart_Monthly);
		piechart_daily = (PieChart) findViewById(R.id.piechart_daily);
		desscription_daily = (TextView) findViewById(R.id.description_daily);
		valid_from_daily = (TextView) findViewById(R.id.valid_from_daily);
		valid_to_daily = (TextView) findViewById(R.id.valid_to_daily);
		total_target = (TextView) findViewById(R.id.total_target);
		remainig_days= (TextView) findViewById(R.id.remainig_days); 
		WorkingDays= (TextView) findViewById(R.id.WorkingDays); 
		BalanceDays= (TextView) findViewById(R.id.BalanceDays); 
		withouZero = new DecimalFormat("###.#");
    	twoDigits = new DecimalFormat("#.####");

		try {
			target = DatabaseUtils.getInstance(DailyTargetActivity.this)
					.getDaoSession().getDailyTargetAchievementsDao()
					.queryBuilder().list();
		} catch (Exception e) {
			String ex;
			ex = e.toString();
			// TODO: handle exception
		}
 
		exportdata();
		if(target!=null && target.size()>0){
		SetData();
		set_piechart_Monthly();
		set_piechart_daily();
		}
	}
    
	private void SetData(){
		 TargetAmount = 0.0;
		 RemainingDays=0.0;
		 D_WorkingDays=0.0;
		 D_BalanceDays=0.0;	 
		 MonthlyAchievementAmount = 0.0;
		 Monthly_remaining = 0.0;
		 DailyBalanceToAchieve = 0.0;
		 DailyAchievementAmount = 0.0;
		 Daily_remaining = 0.0;
		 MonthlyAchievementPercentage = 0.0;
		 DailyAchievementPercentage = 0.0;
		 Monthly_remaining_percentage = 0.0;
	     Daily_remaining_percentage = 0.0;
		if (target != null && target.size() > 0) {
		

			java.text.DateFormat dateFormat = android.text.format.DateFormat
					.getDateFormat(this);

			String Description = "Description: ";
			SpannableString strDescription = new SpannableString(Description
					+ target.get(0).getTargetDescription());
			strDescription.setSpan(new StyleSpan(Typeface.BOLD), 0,
					Description.length(), 0);
			strDescription.setSpan(new ForegroundColorSpan(Color.BLACK), 0,
					Description.length(), 0);

			String valid_from = "Valid From: ";
			SpannableString strvalid_from = new SpannableString(valid_from
					+ dateFormat.format(target.get(0).getValidFrom()));
			strvalid_from.setSpan(new StyleSpan(Typeface.BOLD), 0,
					valid_from.length(), 0);
			strvalid_from.setSpan(new ForegroundColorSpan(Color.BLACK), 0,
					valid_from.length(), 0);

			String valid_to = "Valid To: ";
			SpannableString strvalid_to = new SpannableString(valid_to
					+ dateFormat.format(target.get(0).getValidTo()));
			strvalid_to.setSpan(new StyleSpan(Typeface.BOLD), 0,
					valid_to.length(), 0);
			strvalid_to.setSpan(new ForegroundColorSpan(Color.BLACK), 0,
					valid_to.length(), 0);

		 
			desscription_daily.setText(strDescription);
			valid_from_daily.setText(strvalid_from);
			valid_to_daily.setText(strvalid_to);
		//	total_target.setText(strtotal);
		//	remainig_days.setText(strremaining);
			
			//WorkingDays.setText(strworking_d);
		//	BalanceDays.setText(strbalance_d);
		}
	}
	private void set_piechart_Monthly() {
		if (target != null && target.size() > 0) {
				ArrayList<PieEntry> NoOfEmp2 = new ArrayList<PieEntry>();

		float pMA=Float.valueOf(twoDigits.format(Math.round ( MonthlyAchievementPercentage * 10000.0) / 10000.0));
//		NoOfEmp2.add(new PieEntry(pMA,
//				"Achvd MTD " +withouZero.format((Math.round (MonthlyAchievementAmount * 10000.0) / 10000.0))));
//		
//		NoOfEmp2.add(new PieEntry((float) (Math.round ( Monthly_remaining_percentage * 10000.0) / 10000.0),
//				"Remain MTD "));
       
		double VolumeMissed=target.get(0).getVolumeMissed() !=null?target.get(0).getVolumeMissed():0.0;
        double CalculatedMissed=target.get(0).getCalculatedMissed() !=null?target.get(0).getCalculatedMissed():0;
        double perc=0;
        if(VolumeMissed!=0 && CalculatedMissed!=0)
      	  perc=CalculatedMissed/VolumeMissed;
        
        String Str_currentTarget="MISSED Target: ";
		SpannableString SpanTargetAmount = new SpannableString(Str_currentTarget
				+withouZero.format((float)Math.round (perc   * 100.0) / 100.0 ));
		SpanTargetAmount.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()),
	       Str_currentTarget.length(), SpanTargetAmount.length(), 0);
      		
		NoOfEmp2.add(new PieEntry((float) (Math.round (VolumeMissed * 10000.0) / 10000.0),
				"VolumeMissed: "+(float) (Math.round (VolumeMissed * 10000.0) / 10000.0) ));
		
		NoOfEmp2.add(new PieEntry((float) (Math.round (CalculatedMissed * 10000.0) / 10000.0),
				"CalculatedMissed:  "+(float) (Math.round (CalculatedMissed * 10000.0) / 10000.0)));
       
		
//		double MTDTarget=target.get(0).getVolumeMissed() !=null?target.get(0).getVolumeMissed():0.0;
//        double DropMTD=target.get(0).getCalculatedMissed() !=null?target.get(0).getCalculatedMissed():0;
		PieDataSet dataSet = new PieDataSet(NoOfEmp2,"   VolumeMissed: "+ VolumeMissed+"  CalculatedMissed: "+CalculatedMissed);
		dataSet.setSliceSpace(2f);
		dataSet.setSelectionShift(5f);
		dataSet.setHighlightEnabled(true);
		//dataSet.setValueFormatter(new PercentFormatter());
		// Set the color of the connecting line
		dataSet.setValueLineColor(Color.BLACK);
		// The connecting line is outside the pie chart
		dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
		dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
		dataSet.setDrawValues(false);
		dataSet.setColors(new int[] { Color.parseColor("#479ab3"),Color.parseColor("#9985a1") });

		dataSet.setValueLinePart1Length(0.3f); /** When valuePosition is OutsideSlice, indicates length of first half of the line */
		
		PieData data = new PieData(dataSet);
		piechart_Monthly.setData(data);
		piechart_Monthly.animateXY(500, 500);
		piechart_Monthly.setCenterText(SpanTargetAmount);
		piechart_Monthly.getDescription().setEnabled(false);
		piechart_Monthly.setEntryLabelColor(Color.BLACK);
		piechart_Monthly.setTouchEnabled(false);
	}
	}
	private void set_piechart_daily() {
		double VolumeMissed=target.get(0).getVolumeNewSKU()  !=null?target.get(0).getVolumeNewSKU():0.0;
        double CalculatedMissed=target.get(0).getCalculatedNewSKU()  !=null?target.get(0).getCalculatedNewSKU():0;
  double perc=0;
  if(VolumeMissed!=0 && CalculatedMissed!=0)
	  perc=CalculatedMissed/VolumeMissed;
  
		String Str_currentTarget="NEW SKU target: ";
			SpannableString SpanTargetAmount = new SpannableString(Str_currentTarget+ twoDigits.format(Math.round (perc* 10000.0) / 10000.0));
			SpanTargetAmount.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()),
			       Str_currentTarget.length(), SpanTargetAmount.length(), 0);
		// TODO Auto-generated method stub
		ArrayList<PieEntry> NoOfEmp = new ArrayList<PieEntry>();
		
		float pD=Float.valueOf(twoDigits.format(Math.round ( DailyAchievementPercentage * 10000.0) / 10000.0));

		
//		NoOfEmp.add(new PieEntry(pD,
//				"Todays achievmnt " +twoDigits.format(Math.round ( DailyAchievementPercentage* 10000.0) / 10000.0) + "%"));
//		
//		NoOfEmp.add(new PieEntry((float) Daily_remaining, "Remaining "
//				+twoDigits.format(Math.round ( Daily_remaining_percentage * 10000.0) / 10000.0)+ "%"));
		 	NoOfEmp.add(new PieEntry((float) (Math.round (VolumeMissed * 10000.0) / 10000.0),
				"VolumeNewSKU: "+(float) (Math.round (VolumeMissed * 10000.0) / 10000.0) ));
		
		NoOfEmp.add(new PieEntry((float) (Math.round (CalculatedMissed * 10000.0) / 10000.0),
				"CalculatedNewSKU:  "+(float) (Math.round (CalculatedMissed * 10000.0) / 10000.0)));
   
		PieDataSet dataSet2 = new PieDataSet(NoOfEmp ,"   VolumeNewSKU: "+ VolumeMissed+"  CalculatedNewSKU: "+CalculatedMissed);
		dataSet2.setSliceSpace(2f);
		dataSet2.setSelectionShift(5f);
		dataSet2.setHighlightEnabled(true);
		dataSet2.setDrawValues(false);

		//dataSet.setValueFormatter(new PercentFormatter());

		// Set the color of the connecting line
		dataSet2.setValueLineColor(Color.BLACK);
		// The connecting line is outside the pie chart
		dataSet2.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
		dataSet2.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
		dataSet2.setColors(new int[] { Color.parseColor("#479ab3"),Color.parseColor("#9985a1")});
		dataSet2.setValueLinePart1Length(0.2f); /** When valuePosition is OutsideSlice, indicates length of first half of the line */

		PieData data = new PieData(dataSet2);
		piechart_daily.setData(data);
		piechart_daily.animateXY(500, 500);
		piechart_daily.setCenterText(SpanTargetAmount);
		piechart_daily.getDescription().setEnabled(false);
		piechart_daily.setEntryLabelColor(Color.BLACK);
		piechart_daily.setTouchEnabled(false);

		
		
	}
	
    private void exportdata () {
    	AppDialog.getInstance ().displayIndeterminateProgress (  this , null , getString ( R.string.connecting_label ) );
		 
      	 ConnectionSettings connection = DatabaseUtils.getInstance (  this ).getDaoSession ().getConnectionSettingsDao ().queryBuilder ()
		        		.where ( ConnectionSettingsDao.Properties.ConnectionSettingType.eq ( ConnectionSettingsUtils.Type.GPRS ) ).unique ();
		        // Check if the connection is valid
		        if ( connection != null ) {
      	DownloadTextTask d = new DownloadTextTask( this);
		
			
		 
		    d.execute( ConnectionSettingsUtils.getSetMethodPath(connection.getConnectionSettingURL()) ) ;
 
		        }
    }
    private class DownloadTextTask extends AsyncTask<String, Void, String>  {
		private ProgressDialog dialog1;
		 private Activity activity;
		
		public DownloadTextTask(Activity activity) {
		    this.activity = activity;
		    this.dialog1 = new ProgressDialog(activity);
		}

		@Override
	protected void onPreExecute () {
		//disable();
		// Display indeterminate progress before starting a new thread
	//	ProgressDialog progressDialog = ProgressDialog.show(context, "Wait", "Downloading...");
            super.onPreExecute();
			  this.dialog1.setMessage(" Send Data " );
			  this.dialog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			  this.dialog1.show();
		//	Baguette.showText ( activity , "Please Wait To Connecting" , Baguette.BackgroundColor.RED );
			 	  }
	  
	@Override
	protected String doInBackground(String... urls) {
		
	return 	        	sendData ();
    
	}
	
	@Override
	protected void onPostExecute(String result) {
		 if (this.dialog1.isShowing()) {
	           this.dialog1.dismiss();
	        }
		if(result.trim().equals("Success"))
			{	// Check if there are device tracking to send
			 
					 		importdata();
							 
					 
			

			}
		else 
		{
			   AppDialog.getInstance ().displayAlert(activity , null , getString ( R.string.sync_failed_message ) , AppDialog.ButtonsType.OK,
						new DialogInterface.OnClickListener () {
					@Override
					public void onClick ( DialogInterface dialog , int which ) {
					 
						 
					} } ); 
 
		}
		
		 
		}
	}
	private String sendData () {
		 
        // Retrieve a reference to the dao session
		
        DaoSession daoSession = DatabaseUtils.getInstance (  this ).getDaoSession ();
        // Search for the GPRS link
        ConnectionSettings connection = daoSession.getConnectionSettingsDao ().queryBuilder ()
        		.where ( ConnectionSettingsDao.Properties.ConnectionSettingType.eq ( ConnectionSettingsUtils.Type.GPRS ) ).unique ();
        // Check if the connection is valid
        if ( connection == null ) {
            // Display debug output if required
        	// Invalid connection
        	return "ERROR";
        } // End if
        
        // Retrieve the user code
        String userCode = DatabaseUtils.getCurrentUserCode ( getApplicationContext () );
    
        String companyCode = DatabaseUtils.getCurrentCompanyCode ( getApplicationContext () );
        
     List<TransactionHeaders> transactionHeaders = daoSession.getTransactionHeadersDao ().queryRaw ( 
				"WHERE " + TransactionHeadersDao.Properties.IsProcessed.columnName + "=?  "  ,
				new String [] { String.valueOf ( IsProcessedUtils.isNotSync () ) } );
		// Declare and initialize a list of strings used to host the transaction codes
		List < String > codes = new ArrayList < String > ();
		// Iterate over all transactions
		for ( TransactionHeaders header : transactionHeaders )
			// Add the transaction code to the list
			codes.add ( header.getTransactionCode () );
		// Compute and return a query builder used to query all the transaction details of the not processed transaction headers
		 List < TransactionDetails > transactionDetails = daoSession.getTransactionDetailsDao ()
				 .queryBuilder ().where ( TransactionDetailsDao.Properties.TransactionCode.in ( codes ) ).list();
		 List < TransactionSequences > transactionSequences = daoSession.getTransactionSequencesDao ()
				 .queryBuilder ().where ( TransactionSequencesDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode(this) ) ).list();

		 // Display debug output if required
        
		// Build a set Gson object
		Gson gson = CommonUtilities.buildSetGson ();
		// Declare and initialize a map used to host the Http post arguments
		Map < String , String > arguments = new HashMap < String , String > ();
		// Compute the server url
		String url = ConnectionSettingsUtils.getSetMethodPath ( connection.getConnectionSettingURL () );
		// Declare and initialize a string used to host the table argument
		String table = null;
 
			// Add the device serial table
		arguments.put ( UsersDao.Properties.UserCode.columnName , userCode );
		arguments.put ( UsersDao.Properties.CompanyCode.columnName , companyCode );
		arguments.put ( SyncUtils.TABLES_COUNT , String.valueOf ( 3) );
          // Initialize / update the table argument
			table = daoSession.getTransactionHeadersDao().getTablename ()  +"," + daoSession.getTransactionDetailsDao ().getTablename ()+"," + daoSession.getTransactionSequencesDao().getTablename ();
			// Add the device tracking to the argument
			arguments.put ( daoSession.getTransactionHeadersDao ().getTablename () , gson.toJson ( transactionHeaders ) );
			arguments.put ( daoSession.getTransactionDetailsDao() .getTablename () , gson.toJson ( transactionDetails ) );
	    	arguments.put(daoSession.getTransactionSequencesDao().getTablename () , gson.toJson ( transactionSequences ) );
			// Add the checksum argument
			arguments.put ( SyncUtils.CHECKSUM , SyncUtils.getArgumentsChecksum (   this , arguments ) );
	 
		// Add the table argument
		arguments.put ( SyncUtils.TABLE , table );
		
		// Get an http response (via an http post resquest) for the provided url and arguments
		HttpResponse response = SyncUtils.post ( url , arguments );
		// Check if the response is valid
		if ( response == null )
			// Invalid response
			return "ERROR";
		try {
			// Retrieve the string response
			String result = org.apache.http.util.EntityUtils.toString ( response.getEntity () );
			// Check if no errors occurred
			if ( result.trim().equalsIgnoreCase ( SyncUtils.SUCCESS ) ) {
				if(transactionHeaders!=null && !transactionHeaders.isEmpty()){
				for(TransactionHeaders cc :transactionHeaders)
				{
					cc.setIsProcessed(2);
					 daoSession.getTransactionHeadersDao() .update(cc);
				}	} // End if
	             return result.trim();
	}
			} catch ( ParseException exception ) {
			// Do nothing
		} catch ( IOException exception ) {
			// Do nothing
		} catch ( JsonSyntaxException exception ) {
			// Do nothing
		} // End of try-catch block
		
		return "ERROR";
    }

	@Override
	public void onPreFinish(int requestCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetSuccess(AbstractDao<?, ?> dao, ArrayList<Object> entities,
			int requestCode) {
		
        // Search for the GPRS link
        ConnectionSettings connection = DatabaseUtils.getInstance ( this ).getDaoSession ().getConnectionSettingsDao ().queryBuilder ()
        		.where ( ConnectionSettingsDao.Properties.ConnectionSettingType.eq ( ConnectionSettingsUtils.Type.GPRS ) ).unique ();
		// Retrieve the user code
		String userCode = DatabaseUtils.getCurrentUserCode ( this );
		// Retrieve the company code
		String companyCode = DatabaseUtils.getCurrentCompanyCode ( this );
		// Determine if this is a ping to the server
		if ( requestCode == RequestCode.PING ) {
			// Retrieve the dao table data
			SyncHelper syncHelper = new SyncHelper ( ConnectionSettingsUtils.getGetMethodPath ( connection.getConnectionSettingURL () ) );
			syncHelper.get ( this , DatabaseUtils.getInstance ( this ).getDaoSession ().getDailyTargetAchievementsDao()  , // WarehouseQuantities table
					new TypeToken < List < DailyTargetAchievements > > () {}.getType () , // Expected Type
					userCode , // UserCode
					companyCode , // CompanyCode
					this , // SyncListener
					RequestCode.ON_GOING , // Sync Request Code`
					true ); // Execute in parallel
		} // End if
		// Determine the sync is on going
		else if ( requestCode == RequestCode.ON_GOING ) {
			// Determine if the retrieved data are movement headers

			// Determine if the retrieved data are movement details
			 if ( dao.getTablename ().equals ( DailyTargetAchievementsDao.TABLENAME) ) {
				// Store the list
				target = new ArrayList < DailyTargetAchievements > ();
				for ( Object entity : entities )
					target.add ( (DailyTargetAchievements) entity );
				// Dismiss any displayed dialogs (to avoid activity context leak)
				AppDialog.getInstance ().dismiss ();
			 if(target!=null && target.size()!=0){
				SetData();
				set_piechart_Monthly();
				set_piechart_daily();
				
		   // Save imported movements
				saveImported();
			 }else{
					AppDialog.getInstance ().displayAlert ( this , null , "No target" , AppDialog.ButtonsType.OK , new DialogInterface.OnClickListener () {
						@Override
						public void onClick ( DialogInterface dialog , int which ) {
						 finish();
						} } ); 
			 }
			}
		}// End else if
		 // End else if
		else
			// Dismiss any displayed dialogs (to avoid activity context leak)
			AppDialog.getInstance ().dismiss ();
	
			
	}

	private void saveImported() {
		// TODO Auto-generated method stub
		DatabaseUtils.getInstance ( this ).getDaoSession ().getDailyTargetAchievementsDao().deleteAll()   ;
		try {
			// Begin transaction
			// Add the retrieved returns except for the modified ones
			for ( DailyTargetAchievements dailyTargetAchievements : target )
	 
					DatabaseUtils.getInstance ( this ).getDaoSession ().getDailyTargetAchievementsDao().insert ( dailyTargetAchievements );


			// Display alert dialog
			AppDialog.getInstance ().displayAlert ( this , null , getString ( R.string.sync_success_message ) , AppDialog.ButtonsType.OK , new DialogInterface.OnClickListener () {
				@Override
				public void onClick ( DialogInterface dialog , int which ) {
				 
				} } ); 
		} catch ( Exception exception ) {
			// Display alert dialog
			AppDialog.getInstance ().displayAlert ( this , getString ( R.string.error_label ) , getString ( R.string.populate_data_error_message ) , AppDialog.ButtonsType.OK , null );
		} finally {

		 
		}
		
	}

	@Override
	public void onSetSuccess(ArrayList<ArrayList<Object>> entites,
			int requestCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetFailure(AbstractDao<?, ?> dao, int requestCode) {
		// Dismiss any displayed dialogs (to avoid activity context leak)
		AppDialog.getInstance ().dismiss ();
		// Display alert dialog
		AppDialog.getInstance ().displayAlert ( this , getString ( R.string.error_label ) , getString ( R.string.sync_failed_message ) , AppDialog.ButtonsType.OK , null );
	
		
	}

	@Override
	public void onSetFailure(int requestCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPostFinish(int requestCode) {
		// TODO Auto-generated method stub
		
	}
    private void importdata () {
		// Determine if the network is available
		if ( Network.networkAvailability ( this ) ) {
	        // Search for the GPRS link
	        ConnectionSettings connection = DatabaseUtils.getInstance ( this ).getDaoSession ().getConnectionSettingsDao ().queryBuilder ()
	        		.where ( ConnectionSettingsDao.Properties.ConnectionSettingType.eq ( ConnectionSettingsUtils.Type.GPRS ) ).unique ();
	        // Check if the connection is valid
	        if ( connection != null ) {
	        	// Valid connection
	        	// Clear lists
				// Retrieve the dao table data
				SyncHelper syncHelper = new SyncHelper ( ConnectionSettingsUtils.getConnectMethodPath ( connection.getConnectionSettingURL () ) );
				syncHelper.get ( this , DatabaseUtils.getInstance ( this ).getDaoSession ().getUsersDao () , new TypeToken < List < Users > > () {}.getType () , DatabaseUtils.getCurrentUserCode ( this ) , DatabaseUtils.getCurrentCompanyCode ( this ) , this , RequestCode.PING );
				// Display indeterminate progress
				AppDialog.getInstance ().displayIndeterminateProgress ( this , null , getString ( R.string.connecting_label ) );
	        } // End if
	        else
				// Display alert dialog
				AppDialog.getInstance ().displayAlert ( this ,
						null ,
						getString ( R.string.no_network_connection_message ) ,
						AppDialog.ButtonsType.OK , null );
		} // End if
    }

}
