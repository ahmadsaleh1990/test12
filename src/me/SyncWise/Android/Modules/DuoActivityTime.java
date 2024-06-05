/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules;


import java.util.Date;

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.DuoUsers;
import me.SyncWise.Android.Database.DuoUsersDao;
import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Gson.BaseTimerActivity;
import me.SyncWise.Android.Gson.Http;
import me.SyncWise.Android.Widgets.Baguette;

import org.json.JSONObject;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.BufferType;

public class DuoActivityTime extends Activity   { 
	
	private EditText Otp;
	private TextView result;
	private String UserName = "";
	private  String IntegrationKey = "DI9TELVXWN40VAJT4C9O";
	private  String SecretKey = "FejZmJ0XrjAR7e5pFOx3FiH9DyiC7eOLKDo9bC8w";
	private String HostName = "api-9c5519ac.duosecurity.com";
	private   Date ResponseDate;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.duo_activity);
        Otp = (EditText) findViewById(R.id.TBResult);
        result = (TextView) findViewById(R.id.TResult);
        if(!PermissionsUtils.getEnableDuo(this, DatabaseUtils.getCurrentUserCode(this), DatabaseUtils.getCurrentCompanyCode(this)) 
        		){
        	finish();
        }
     //   setUserName("Syncwise.Mansour2"); 
        String userCode = me.SyncWise.Android.Database.DatabaseUtils.getCurrentUserCode (   this );
		// Retrieve the company code
		String companyCode = me.SyncWise.Android.Database.DatabaseUtils.getCurrentCompanyCode ( this );
     
        IntegrationKey= PermissionsUtils.getIntegrationKey(this, userCode, companyCode);
        SecretKey = PermissionsUtils.getSecretKey(this, userCode, companyCode);
        HostName = PermissionsUtils.getHostName(this, userCode, companyCode);
        String userna = PermissionsUtils.getDuoDefaultUser(this, userCode, companyCode);
        if(!PermissionsUtils.getENABLEUserDefaultDUO(this, userCode, companyCode))
        { DuoUsers duo =me.SyncWise.Android.Database.DatabaseUtils.getInstance (  this ).getDaoSession ().getDuoUsersDao ().queryBuilder ()
				.where ( DuoUsersDao.Properties.UserCode.eq (userCode ), 	DuoUsersDao.Properties.CompanyCode .eq (companyCode )  ).unique ();
        if(duo != null)
             setUserName( duo.getDuoCode() );
        else 
    	     setUserName( userna );	 
        }else //	setResult(RESULT_OK);finish();
    		  setUserName( userna );
      Point size = new Point();
      // Compute the screen size
      getWindowManager ().getDefaultDisplay ().getSize ( size );
      ImageView solutionLogo = (ImageView) findViewById ( R.id.icon_logo_solution );
		// Compute the min height allowed (100 dp) in pixels
		int minHeight = (int) TypedValue.applyDimension ( TypedValue.COMPLEX_UNIT_DIP , 100 , getResources ().getDisplayMetrics () );
		// Determine if the new solution logo height is good enough (greater than 100 dp)
		if ( size.y * 1 / 5 > minHeight )
			// Assign the icon height
			solutionLogo.getLayoutParams ().height = size.y * 1 / 5;
		( (ImageView) findViewById ( R.id.icon_logo_solution ) ).setImageResource ( R.drawable.logo_cash_van );
		Button usernameClearButton = (Button) findViewById ( R.id.button_clear_username );
		usernameClearButton.setEnabled ( true );
		usernameClearButton.setVisibility ( View.VISIBLE );
		
		// Retrieve the license label
		String licenseLabel = AppResources.getInstance ( this ).getString ( this , R.string.firm_license_label ) + " ";
		// Retrieve the firm title
		String firmTitle = getFirmTitle ();
		// Determine if the firm title is valid
		if ( TextUtils.isEmpty ( firmTitle ) )
			// Firm title not available
			firmTitle = AppResources.getInstance ( this ).getString ( this , R.string.not_available_abbreviation );
		// Build a spannable string out of the retrieved strings in order to apply various spans
		SpannableString firmLicense = new SpannableString ( licenseLabel + firmTitle );
		// Apply style span
		firmLicense.setSpan ( new StyleSpan ( Typeface.BOLD ) , licenseLabel.length () , firmLicense.length () , 0 );
		// Apply foreground color span
		firmLicense.setSpan ( new ForegroundColorSpan ( Color.BLUE ) , licenseLabel.length () , firmLicense.length () , 0 );
		// Apply size span
		firmLicense.setSpan ( new TextAppearanceSpan ( this , R.style.TextAppearance_Large ) , licenseLabel.length () , firmLicense.length () , 0 );
		// Display the firm license
		( (TextView) findViewById ( R.id.label_firm_license ) ).setText ( firmLicense , BufferType.SPANNABLE );
		( (TextView) findViewById ( R.id.label_firm_license ) ).setText ( getIntent ().getStringExtra  ( "aaa" )  );
      	//setUserName("Syncwise.Mansour2"); 
    }
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	//	super.onBackPressed();
	
	}
	public void clearText ( View view ) {
		// Determine if the provided view is valid
		if ( view == null )
			// Invalid view
			return;
		// Check the view id
		if ( view.getId () == R.id.button_clear_username )
			// Clear the password field
			((EditText) findViewById(R.id.TBResult)).setText ( "" );
	}
	protected String getFirmTitle () {
		try {
			// Retrieve a reference to the R.string class
			Class < ? > stringResources = Class.forName ( getPackageName () + ".R$string" );
			// Search for the field 'firm_title' and retrieve its value
			int firmTitleId = stringResources.getField ( "firm_title" ).getInt ( null );
			// Display the content of the firm title string resource
			return AppResources.getInstance ( this ).getString ( this , firmTitleId );
		} catch ( Exception exception ) {
			// Invalid string resource
			return null;
		} // End of try-catch block
	}
    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    
    public void SendOtp(View view) {
   	   String userCode = me.SyncWise.Android.Database.DatabaseUtils.getCurrentUserCode (   this );
    		// Retrieve the company code
    		String companyCode = me.SyncWise.Android.Database.DatabaseUtils.getCurrentCompanyCode ( this );
     	if(Otp.getText().toString().equals( PermissionsUtils.getavoidDuoUser(this, userCode, companyCode) )){
     	//	setResult(RESULT_OK);a
     		finish();
     	}else
        new PingRequest().execute();

    }


    class PingRequest extends AsyncTask<String, String, String> {
   
    	@Override
		protected void onPreExecute() {
		 AppDialog.getInstance().displayIndeterminateProgress(DuoActivityTime.this, "Wait Duo ", "");
		}

		@Override
        protected void onPostExecute(String s) {
			
            if (s.equals("Success")) {
                new CheckRequest().execute();
            }else
            {
            	 AppDialog.getInstance().dismiss();
            	 Baguette.showText ( DuoActivityTime.this , "probem Duo "+s , Baguette.BackgroundColor.RED );
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            Http http = new Http("GET", HostName, "/auth/v2/ping");
            try {
                String jsonData = http.executeRequestRaw();
                JSONObject Jobject = new JSONObject(jsonData);
                JSONObject JResponse = Jobject.getJSONObject("response");
                long Time = JResponse.getLong("time");
                Date Ddate = new Date(Time * 1000);
                ResponseDate = Ddate;
                return "Success";
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
            //return "Error";
        }
    }

    class CheckRequest extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String s) {
            if (s.equals("Success")) {
                new PreauthRequest().execute();
            }else
            {
           	 AppDialog.getInstance().dismiss();
           	 Baguette.showText ( DuoActivityTime.this , "probem Duo "+s , Baguette.BackgroundColor.RED );
           }
        }

        @Override
        protected String doInBackground(String... strings) {
            Http http = new Http("GET", HostName, "/auth/v2/check");
            try {
                http.signRequest(IntegrationKey, SecretKey);
                String jsonData = http.executeRequestRaw();
                JSONObject Jobject = new JSONObject(jsonData);
                JSONObject JResponse = Jobject.getJSONObject("response");
                long Time = JResponse.getLong("time");
                Date Ddate = new Date(Time * 1000);
                ResponseDate = Ddate;
                if (!Jobject.getString("stat").equals("OK")) {
                    return "Error";
                }
                return "Success";
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
      
        }
    }

    class PreauthRequest extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String s) {
            if (s.equals("Success")) {
                new AuthRequest().execute();
            }else
            {
           	 AppDialog.getInstance().dismiss();
           	 Baguette.showText ( DuoActivityTime.this , "probem Duo "+s , Baguette.BackgroundColor.RED );
           }
        }

        @Override
        protected String doInBackground(String... strings) {
            Http http = new Http("POST", HostName, "/auth/v2/preauth", 40000);
            try {
                http.signHMAC(SecretKey, UserName);
                http.addParam("username", UserName);
                http.signRequest(IntegrationKey, SecretKey, 2);

                String jsonData = http.executeRequestRaw();
                JSONObject Jobject = new JSONObject(jsonData);
                JSONObject JResponse = Jobject.getJSONObject("response");
                if (!Jobject.getString("stat").equals("OK")) {
                    return "Error";
                }
                return "Success";
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
  

        }
    }

    class AuthRequest extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String s) {
        	 AppDialog.getInstance().dismiss();
        	if(s.equals("Success")){
        		//setResult(RESULT_OK);
        		finish();
        	}
        	else{
        		// ERROR
        		
        		AppDialog.getInstance ().displayAlert ( DuoActivityTime.this ,
        				null ,
        				s ,
        				AppDialog.ButtonsType.OK ,new DialogInterface.OnClickListener () {
        			@Override
        			public void onClick ( DialogInterface dialog , int which ) {
        				// Determine the clicked button
        				switch ( which ) {
        				case DialogInterface.BUTTON_NEUTRAL: 
        				 
        				} // End switch
        			} } );
        	}
            Toast.makeText( DuoActivityTime.this, s, Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            Http http = new Http("POST", HostName, "/auth/v2/auth", 40000);
            try {
                http.signHMAC(SecretKey, UserName);
                http.addParam("username", UserName);
                http.addParam("factor", "passcode");
                http.addParam("passcode", Otp.getText().toString());
                http.signRequest(IntegrationKey, SecretKey, 2);

                String jsonData = http.executeRequestRaw();
                JSONObject Jobject = new JSONObject(jsonData);
                JSONObject JResponse = Jobject.getJSONObject("response");
                if (!Jobject.getString("stat").equals("OK")) {
                    return "Error";
                }
                if (!JResponse.getString("status").equals("allow")) {
                    return  "status" +JResponse.getString("status_msg");// "Error";
                }
                return "Success";
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
    

        }
    }
    
    
	
}