/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Sync;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.google.gson.reflect.TypeToken;

import de.greenrobot.dao.AbstractDao;
import me.SyncWise.Android.ActivityTransition;
import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.AppToast;
import me.SyncWise.Android.AppVersion;
import me.SyncWise.Android.Network;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Vibration;
import me.SyncWise.Android.Database.ConnectionSettingsUtils;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.Divisions;
import me.SyncWise.Android.Database.DivisionsDao;
import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Database.StatusUtils;
import me.SyncWise.Android.Database.UserDivisions;
import me.SyncWise.Android.Database.UserDivisionsDao;
import me.SyncWise.Android.Database.Users;
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Modules.LoginActivity;
import me.SyncWise.Android.Modules.GCM.RegisterDeviceService;
import me.SyncWise.Android.Modules.Sync.Automatic.AutomaticSyncUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.TextView.BufferType;

/**
 * Activity used as the synchronization screen, used to send and receive data from and to the server.
 * 
 * @author Elias
 * @sw.todo <b>Sync Activity Implementation :</b><br>
 * 
 * TODO
 *
 */
public abstract class SyncActivity extends Activity implements TextWatcher , OnEditorActionListener , SyncListener , Synchronization {

	/**
	 * Integer used to host the total number of synchronizations.
	 */
	private static int total;
	
	/**
	 * Map of objects lists used to host the retrieved entities.
	 */
	private static HashMap < String , ArrayList < Object > > objects;
	
	/**
	 * List of abstract dao group tables used to host the failed tables.
	 */
	private static ArrayList < String > failedTables;
	
	/**
	 * Reference to the user code field
	 */
	private EditText userCodeEditText;
	
	/**
	 * Reference to the user code clear button
	 */
	private Button userCodeClearButton;
	
	/**
	 * Reference to the company code field
	 */
	private EditText companyCodeEditText;
	
	/**
	 * Reference to the company code clear button
	 */
	private Button companyCodeClearButton;
	
	/**
	 * Reference to the url field
	 */
	private EditText urlEditText;
	
	/**
	 * Reference to the url clear button
	 */
	private Button urlClearButton;
	
	/**
	 * Reference to the sync button
	 */
	private LinearLayout syncButton;
	
	/**
	 * Helper class used to define the various sync request codes.
	 * 
	 * @author Elias
	 *
	 */
	public static class RequestCode {
		public static final int PING = 0;
		public static final int INIT = 1;
		public static final int ON_GOING = 2;
	}
	
	/**
	 * {@link java.lang.Class Class} activity reference.
	 */
	private Class < ? > activity;
	
	/**
	 * Setter - {@link #activity}
	 * 
	 * @param activity	{@link java.lang.Class Class} activity reference.
	 */
	public void setActivity ( final Class < ? > activity ) {		
		// Check if the provided class is valid
		if ( activity != null )
			// Store the new activity
			this.activity = activity;
	}
	
	/**
	 * Getter - {@link #activity}
	 * 
	 * @return	{@link java.lang.Class Class} activity reference.
	 */
	public Class < ? > getActivity () {
		// Return the activity class
		return activity;
	}
	
	/**
	 * Starts the new activity defined by {@link #activity} and finishes the current activity (if {@link #activity} is valid).
	 */
	public void startActivity () {
		try {
			// Start the new activity
			startActivity ( new Intent ( this , getActivity () ) );
			// Close this activity
			finish ();
		} catch ( Exception exception ) {
			// Invalid activity, display toast
			new AppToast ( this ).setGravity ( Gravity.BOTTOM )
				.setYOffset ( (int) TypedValue.applyDimension ( TypedValue.COMPLEX_UNIT_DIP , 40 , getResources ().getDisplayMetrics () ) )
				.setDuration ( Toast.LENGTH_LONG )
				.setIcon ( R.drawable.warning )
				.setText ( getString ( R.string.invalid_activity_message ) ).show ();
		} // End of try-catch block
	}
	
	/**
	 * Returns the firm title for whom the application is licensed.<br>
	 * The firm title should be stored as a string resource under the name of {@code firm_title}.
	 * Otherwise this method can be overridden to implement the desired case.
	 * 
	 * @return	String holding the firm title.
	 */
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
	
	/*
	 * Gets all the abstract dao tables that should be requested during sync, along with their appropriate entities types.
	 *
	 * @see me.SyncWise.Android.Modules.Sync.Synchronization#getSyncTables_Get()
	 */
	public abstract Map < AbstractDao < ? , ? > , Type > getSyncTables_Get ();
	
	/*
	 * Gets all the abstract dao tables that should be delivered during sync, grouped in multiple lists to preserver relationships.
	 *
	 * @see me.SyncWise.Android.Modules.Sync.Synchronization#getSyncTables_Set()
	 */
	@Override
	public ArrayList < ArrayList < AbstractDao < ? , ? > > > getSyncTables_Set () {
		// Not implemented
		return null;
	}
	
	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@SuppressLint("DefaultLocale") 
	@Override
	public void onCreate ( Bundle savedInstanceState ) {
		// Superclass method call	
		super.onCreate ( savedInstanceState );
		
		// Set the activity to start
		setActivity ( LoginActivity.class );
		
		// Hide title bar
		requestWindowFeature ( Window.FEATURE_NO_TITLE );
		// Hide status bar
		getWindow().setFlags ( WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN );
		// Set the activity content from a layout resource.
		setContentView ( R.layout.sync_activity );
		// Indicate that the screen should remain on
		findViewById ( android.R.id.content ).setKeepScreenOn ( true );
		
		// Declare and initialize a point object used to store the screen size
		Point size = new Point();
		// Compute the screen size
		getWindowManager ().getDefaultDisplay ().getSize ( size );
		
		// Retrieve a reference to the solution logo
		ImageView solutionLogo = (ImageView) findViewById ( R.id.icon_logo_solution );
		// Set the solution logo icon
		solutionLogo.setImageResource ( R.drawable.logo_sales );
		// Compute the min height allowed (100 dp) in pixels
		int minHeight = (int) TypedValue.applyDimension ( TypedValue.COMPLEX_UNIT_DIP , 100 , getResources ().getDisplayMetrics () );
		// Determine if the new solution logo height is good enough (greater than 100 dp)
		if ( size.y * 1 / 5 > minHeight )
			// Assign the icon height
			solutionLogo.getLayoutParams ().height = size.y * 1 / 5;
		
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
		
		// Retrieve a reference to the sync button
		syncButton = (LinearLayout) findViewById ( R.id.layout_button_sync );
		
		// Retrieve a reference to the clear buttons
		
		userCodeClearButton = (Button) findViewById ( R.id.button_clear_user_code );
		companyCodeClearButton = (Button) findViewById ( R.id.button_clear_company_code );
		urlClearButton = (Button) findViewById ( R.id.button_clear_url );
		
		// Retrieve references to the fields
		userCodeEditText = (EditText) findViewById ( R.id.edittext_user_code );
		companyCodeEditText = (EditText) findViewById ( R.id.edittext_company_code );
		urlEditText = (EditText) findViewById ( R.id.edittext_url );
		
		// Display the field hints
		userCodeEditText.setHint ( AppResources.getInstance ( this ).getString ( this , R.string.user_code_hint ) );
		companyCodeEditText.setHint ( AppResources.getInstance ( this ).getString ( this , R.string.company_code_hint ) );
		urlEditText.setHint ( AppResources.getInstance ( this ).getString ( this , R.string.url_hint ) );
		
		// Set a special listener to be called when an action is performed on the text view
		urlEditText.setOnEditorActionListener ( this );
		
		// Assign a text changed listener to the fields in order to display/hide the clear button accordingly
		userCodeEditText.addTextChangedListener ( this );
		companyCodeEditText.addTextChangedListener ( this );
		urlEditText.addTextChangedListener ( this );
		
		// Display the button label
		( (TextView) findViewById ( R.id.label_button_sync ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.start_label ).toUpperCase () );
		
		// Apply the sync panel layout gravity attribute
		// Due to a bug, it cannot be applied in XML when using the include tag (and hence should be done programmatically)
		( (FrameLayout.LayoutParams) findViewById ( R.id.layout_sync_panel ).getLayoutParams () ).gravity = Gravity.CENTER;
		
		// Retrieve a reference to the application version label
		TextView appVersion = (TextView) findViewById ( R.id.label_app_version );
		// Apply the layout gravity attribute
		// Due to a bug, it cannot be applied in XML when using the include tag (and hence should be done programmatically)
		( (FrameLayout.LayoutParams) appVersion.getLayoutParams () ).gravity = Gravity.BOTTOM | Gravity.RIGHT;
		// Display the application version
		appVersion.setText ( AppVersion.getSpannableText ( this ) , BufferType.SPANNABLE );
		
	}
	
	/*
	 * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause(), for your activity to start interacting with the user.
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume () {
		// Superclass method call
		super.onResume ();
		// Check if there is an on going sync process
		if ( total != 0 ) {
			// Display UI widgets
			disable ();
			// Display a progress dialog
			AppDialog.getInstance ().displayProgress ( this ,
					null ,
					getString ( R.string.loading_label ) );
			// Update progress
			AppDialog.getInstance ().setProgress ( objects.size () * 100 / total );
		} // End if
		else
			// Enable UI widgets
			enable ();
	}
	
	/*
	 * Called as part of the activity lifecycle when an activity is going into the background, but has not (yet) been killed.
	 *
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause () {
		// Superclass method call
		super.onPause ();
		// Dismiss any displayed dialogs (to avoid activity context leak)
		AppDialog.getInstance ().dismiss ();
	}
	
	/*
	 * Called when you are no longer visible to the user.
	 *
	 * @see android.app.Activity#onStop()
	 */
	@Override
    protected void onStop () {
    	// Superclass method call
		super.onStop ();
		// Determine if the activity is finishing
		if ( isFinishing () ) {
			activity = null;
			if ( userCodeEditText != null )
				userCodeEditText.removeTextChangedListener ( this );
			if ( companyCodeEditText != null )
				companyCodeEditText.removeTextChangedListener ( this );
			if ( urlEditText != null )
				urlEditText.removeTextChangedListener ( this );
			userCodeEditText = null;
			userCodeClearButton = null;
			companyCodeEditText = null;
			companyCodeClearButton = null;
			urlEditText = null;
			urlClearButton = null;
			syncButton = null;
		} // End if
    }
	
	/*
	 * Method called to notify before the text is changed.
	 * 
	 * @see android.text.TextWatcher#afterTextChanged(Editable)
	 */
	@Override
	public void afterTextChanged ( Editable s ) {
		// Check if the user code field is empty and the clear button visible
		if ( userCodeEditText.getText ().toString ().isEmpty () && userCodeClearButton.getVisibility () == View.VISIBLE )
			// Hide the clear button
			userCodeClearButton.setVisibility ( View.GONE );
		// Otherwise check if the user code field is NOT empty and the clear button is hidden
		else if ( ( ! userCodeEditText.getText ().toString ().isEmpty () ) && userCodeClearButton.getVisibility () == View.GONE )
			// Display the clear button
			userCodeClearButton.setVisibility ( View.VISIBLE );
		
		// Check if the company code field is empty and the clear button visible
		if ( companyCodeEditText.getText ().toString ().isEmpty () && companyCodeClearButton.getVisibility () == View.VISIBLE )
			// Hide the clear button
			companyCodeClearButton.setVisibility ( View.GONE );
		// Otherwise check if the company code field is NOT empty and the clear button is hidden
		else if ( ( ! companyCodeEditText.getText ().toString ().isEmpty () ) && companyCodeClearButton.getVisibility () == View.GONE )
			// Display the clear button
			companyCodeClearButton.setVisibility ( View.VISIBLE );
		
		// Check if the url field is empty and the clear button visible
		if ( urlEditText.getText ().toString ().isEmpty () && urlClearButton.getVisibility () == View.VISIBLE )
			// Hide the clear button
			urlClearButton.setVisibility ( View.GONE );
		// Otherwise check if the url field is NOT empty and the clear button is hidden
		else if ( ( ! urlEditText.getText ().toString ().isEmpty () ) && urlClearButton.getVisibility () == View.GONE )
			// Display the clear button
			urlClearButton.setVisibility ( View.VISIBLE );
	}

	/*
	 * Method called to notify after the text is changed.
	 * 
	 * @see android.text.TextWatcher#beforeTextChanged(CharSequence, int, int, int)
	 */
	@Override
	public void beforeTextChanged ( CharSequence s , int start , int count , int after ) {
		// Do nothing
	}

	/*
	 * Method called to notify that the text has just been changed.
	 * 
	 * @see android.text.TextWatcher#onTextChanged(CharSequence, int, int, int)
	 */
	@Override
	public void onTextChanged ( CharSequence arg0 , int start , int before , int count ) {
		// Do nothing
	}
	
	/*
	 * Called when an action is being performed.
	 *
	 * @see android.widget.TextView.OnEditorActionListener#onEditorAction(android.widget.TextView, int, android.view.KeyEvent)
	 */
	@Override
	public boolean onEditorAction ( TextView textView , int actionId , KeyEvent event ) {
		// Determine if the IME Action GO is set
		if ( actionId == EditorInfo.IME_ACTION_GO ) {
			// Sync data
			validateInput ();
			// Indicate that the action is consumed
			return true;
		} // End if
		// Indicate that the action is NOT consumed
		return false;
	}
	
	/**
	 * Called when a view has been clicked.<br>
	 * If the view id corresponds to the user code, company code or url clear buttons, the corresponding edit text is cleared.
	 * 
	 * @param view	The clicked view.
	 */
	public void clearText ( View view ) {
		// Determine if the provided view is valid
		if ( view == null )
			// Invalid view
			return;
		// Check the view id
		if ( view.getId () == R.id.button_clear_user_code )
			// Clear the user code field
			( (EditText) findViewById ( R.id.edittext_user_code ) ).setText ( "" );
		else if ( view.getId () == R.id.button_clear_company_code )
			// Clear the user code field
			( (EditText) findViewById ( R.id.edittext_company_code ) ).setText ( "" );
		else if ( view.getId () == R.id.button_clear_url )
			// Clear the url field
			( (EditText) findViewById ( R.id.edittext_url ) ).setText ( "" );
	}
	
	/**
	 * Called when a view has been clicked.<br>
	 * Device will try to sync data with the server.
	 * 
	 * @param view	The clicked view.
	 */
	public void sync ( View view ) {
		// Sync Data
		validateInput ();
	}
	
	/**
	 * Validates the input fields provided by the user.
	 */
	@SuppressLint("DefaultLocale") 
	private void validateInput () {
		// Hide the software keyboard
        ( (InputMethodManager) getBaseContext ().getSystemService ( INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow ( urlEditText.getWindowToken (), 0 );
        
		// Reset previous errors
		userCodeEditText.setError ( null );
		companyCodeEditText.setError ( null );
		urlEditText.setError ( null );
		
		// Retrieve the input fields
		String userCode = ( (EditText) findViewById ( R.id.edittext_user_code ) ).getText ().toString ();
		String companyCode = ( (EditText) findViewById ( R.id.edittext_company_code ) ).getText ().toString ();
		String url = ( (EditText) findViewById ( R.id.edittext_url ) ).getText ().toString ();
		
		// Determine if the provided user code and/or company code and/or url are valid
		if ( userCode.isEmpty () || companyCode.isEmpty () || url.isEmpty () ) {
			// Determine if the url field is invalid
			if ( url.isEmpty () ) {
				// Display an error message
				urlEditText.setError ( AppResources.getInstance ( this ).getString ( this , R.string.error_field_required_message ) );
				// Focus the url field
				urlEditText.requestFocus ();
			} // End if
			// Determine if the company code field is invalid
			if ( companyCode.isEmpty () ) {
				// Display an error message
				companyCodeEditText.setError ( AppResources.getInstance ( this ).getString ( this , R.string.error_field_required_message ) );
				// Focus the company code field
				companyCodeEditText.requestFocus ();
			} // End if
			// Determine if the user code field is invalid
			if ( userCode.isEmpty () ) {
				// Display an error message
				userCodeEditText.setError ( AppResources.getInstance ( this ).getString ( this , R.string.error_field_required_message ) );
				// Focus the user code field
				userCodeEditText.requestFocus ();
			} // End if
			// Exit method
			return;
		} // End if
		
		// Check if the url is valid
		if ( ! Patterns.WEB_URL.matcher ( url ).matches () ) {
			// Display an error message
			urlEditText.setError ( AppResources.getInstance ( this ).getString ( this , R.string.url_invalid_message ) );
			// Focus the url field
			urlEditText.requestFocus ();
			// Exit method
			return;
		} // End if
		
		// Change all characters to lower case in the URL
		urlEditText.setText ( urlEditText.getText ().toString () );
		
		// Check for network availability
		if ( ! Network.networkAvailability ( this ) ) {
			// Display alert dialog
			AppDialog.getInstance ().displayAlert ( this ,
					null ,
					getString ( R.string.no_network_connection_message ) ,
					AppDialog.ButtonsType.OK , null );
			// Exit method
			return;
		} // End if
		
		// Disable widgets
		disable ();
		
		// Otherwise the URL is valid
		// Display indeterminate progress
		AppDialog.getInstance ().displayIndeterminateProgress ( this , null , getString ( R.string.connecting_label ) );
		// Try to connect to service
//		SyncHelper syncHelper = new SyncHelper ( ConnectionSettingsUtils.getConnectMethodPath ( url ) );
//		syncHelper.get ( this , DatabaseUtils.getInstance ( this ).getDaoSession ().getUsersDao () , new TypeToken < List < Users > > () {}.getType () , userCode , companyCode , this , RequestCode.PING );
		  Handler handler = new Handler();
			TaskCanceler taskCanceler;
			IsLisencedGet d = new IsLisencedGet(SyncActivity.this, url,userCode,companyCode );
			taskCanceler = new TaskCanceler(d);
			handler.postDelayed(taskCanceler, 50000);
			d.execute() ;
	}
	public class TaskCanceler implements Runnable{
	    private AsyncTask task;
	    private Context context;
	    public TaskCanceler(AsyncTask task) {
	        this.task = task;
	    }

	     @Override
	     public void run() {
	        if (task.getStatus() == AsyncTask.Status.RUNNING )
	        {
	        	task.cancel(true);
	          
	   		 //	AppDialog.getInstance ().displayAlert(context , null , getString ( R.string.no_network_connection_message1 ) , AppDialog.ButtonsType.OK,null );
			
	        }
	        }
	}
	/**
	 * Enables all UI widgets.
	 */
	private void enable () {
		// Enable all UI widgets
		userCodeClearButton.setEnabled ( true );
		userCodeEditText.setEnabled ( true );
		companyCodeClearButton.setEnabled ( true );
		companyCodeEditText.setEnabled ( true );
		urlClearButton.setEnabled ( true );
		urlEditText.setEnabled ( true );
		syncButton.setEnabled ( true );
	}
	
	/**
	 * Disables all UI widgets.
	 */
	private void disable () {
		// Disable all UI widgets
		userCodeClearButton.setEnabled ( false );
		userCodeEditText.setEnabled ( false );
		companyCodeClearButton.setEnabled ( false );
		companyCodeEditText.setEnabled ( false );
		urlClearButton.setEnabled ( false );
		urlEditText.setEnabled ( false );
		syncButton.setEnabled ( false );
	}
	
	/**
	 * Determines if the sync progress has ended.<br>
	 * If so, a validation check is performed over the retrieved data, and a dialog is displayed accordingly.
	 */
	private void checkProgress () {
		// Check if the entities lists is valid
		if ( objects == null ) {
			// Invalid list
			// Dismiss any displayed dialogs (to avoid activity context leak)
			AppDialog.getInstance ().dismiss ();
			// Enable UI widgets
			enable ();
		} // End if
			
		// Check if download finished
		else if ( total == objects.size () ) {
			// Sync ended
			// Dismiss any displayed dialogs (to avoid activity context leak)
			AppDialog.getInstance ().dismiss ();
			// Check if all data is valid
			boolean valid = true;
			// Declare and initialize a string used to host the error message
			String errorMessage = getString ( R.string.sync_failed_message );
			// Declare and initialize a string used to host the user's division code
			String divisionCode = null;
			String divisionCo=null;
			// Retrieve the company code
			String companyCode = companyCodeEditText.getText ().toString ();
			// Iterate over all entities
			for ( String key : objects.keySet () ) {
				// Retrieve the entities of the current table
				ArrayList < Object > entities = objects.get ( key );
				// Check if list is valid
				if ( entities == null ) {
					// Invalid entities list
					valid = false;
					// Set the appropriate error message
					errorMessage += "\n" + getString ( R.string.web_service_import_error_message );
					break;
				} // End if
			} // End for each
			// Check if all entities are valid
			if ( valid ) {
				// Continue with the checking
				// Retrieve the user division entities
				ArrayList < Object > userDivisions = objects.get ( UserDivisionsDao.TABLENAME );
				// Retrieve the division entities
				ArrayList < Object > divisions = objects.get ( DivisionsDao.TABLENAME );
				// Make sure the user is linked to exactly one division
	
			if ( userDivisions == null   || divisions == null || divisions.isEmpty () ) {
				// Invalid user divisions list
					valid = false;
					// Set the appropriate error message
					errorMessage += "\n" + getString ( R.string.user_division_error_message );
				} // End if
			//	if ( userDivisions == null || userDivisions.size () != 1 || divisions == null || divisions.isEmpty () ) {
		
				else {
					boolean invalidDivisionLink = true;
					
					int count =0;
					int count1 =0;
				//	count=userDivisions.size();
					for(Object division :divisions)
					{
						Divisions d = (Divisions) division;
						for(Object userDivision :userDivisions)
						{	
							UserDivisions ud = (UserDivisions) userDivision;
						if( d.getCompanyCode ().equals ( companyCode ) && ud.getDivisionCode().equals(d.getDivisionCode()) && d.getParentCode()==null )
						{	// Invalid user divisions list
							count=count+1;
							//invalidDivisionLink=false;
							divisionCo=ud.getDivisionCode();
							
						}// Exit loop
						
//						if(  ud.getDivisionCode().equals(d.getDivisionCode()) &&(!d.getCompanyCode ().equals ( companyCode ) || d.getParentCode()!=null))
//						{	// Invalid user divisions list
//							count1=count1+1;
//							//invalidDivisionLink=false;
//							
//						}// Exit loop
						//break;
						 
						}
						//if(invalidDivisionLink==false)
						//	break;
					}
					
					for(Object userDivision :userDivisions)
					{	
						UserDivisions ud = (UserDivisions) userDivision;
						 if( !ud.getCompanyCode ().equals ( companyCode ))// &&  !d.getCompanyCode ().equals ( companyCode ) )
					           {	 
									count1 = count1 + 1;
								} 
					}
					 
					
					
					if(count>1 || count==0)
					{
						invalidDivisionLink=true;	
					}
					else{
						invalidDivisionLink=false;
					}
					if(count1>0 )
					{
						valid = false;
						// Set the appropriate error message
						errorMessage += "\n" + getString ( R.string.user_division_error_message1 );	
					}
					for(Object userDivision :userDivisions)
					{	
						UserDivisions ud = (UserDivisions) userDivision;
					    divisionCode=divisionCode+","+ud.getDivisionCode();
				     }
					
					// Otherwise make sure the linked division is a main one
					// Retrieve the division code
					//divisionCode = ( (UserDivisions) userDivisions.get ( 0 ) ).getDivisionCode ();
					// Declare and initialize a boolean used to indicate if the user is not linked to a division
				
//					// Iterate over all divisions
//					for ( Object object : divisions ) {
//						// Cast the object to division
//						Divisions division = (Divisions) object;
//						// Check if the current division is the linked division and is main
//						if ( division.getCompanyCode ().equals ( companyCode ) && division.getDivisionCode ().equals ( divisionCode ) && division.getParentCode () == null ) {
//							// Reset the flag
//							invalidDivisionLink = false;
//							// Exit loop
//							break;
//						} // End if
//					} // End for each
					// Check if the division link is invalid
					if ( invalidDivisionLink ) {
						// Invalid user divisions list
						valid = false;
						// Set the appropriate error message
						errorMessage += "\n" + getString ( R.string.user_division_error_message );
					} // End if
				} // End else
			} // End if
			// Check if all entities are valid
			if ( valid ) {
				try {
					// Begin transaction
					DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().beginTransaction ();
					
					// Initialize failed tables list if needed
					if ( failedTables == null )
						failedTables = new ArrayList < String > ();
					else
						// Clear the failed tables list
						failedTables.clear ();
					// Iterate over all entities list
					for ( String key : objects.keySet () ) {
						// Retrieve the entities for the current table
						ArrayList < Object > entities = objects.get ( key );
						// Clear the failed tables list
						failedTables.clear ();
						// Add the current table to the failed tables list
						// So that if an error occur, the table which caused the error can be identified
						failedTables.add ( key );
						// Iterate over each entity
						for ( Object entity : entities )
							// Insert data
							DatabaseUtils.getInstance ( this ).getDaoSession ().insert ( entity );
					} // End for each
					// Clear the failed tables list
					failedTables.clear ();
					failedTables = null;
					
					// Set the device owner's user code
					DatabaseUtils.setCurrentUserCode ( this , userCodeEditText.getText ().toString () );
					// Set the device owner's company code
					DatabaseUtils.setCurrentCompanyCode ( this , companyCodeEditText.getText ().toString () );
					// Set the device owner's division code
					DatabaseUtils.setCurrentDivisionCode ( this , divisionCo  );
				    //Set the device owner's division code parent
				//	DatabaseUtils.setCurrentDivisionParent(this ,divisionCode);
					// Commit changes
					DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().setTransactionSuccessful ();
					
					// Clear variables
					total = 0;
					objects.clear ();
					objects = null;
					
					// Create a new intent to start a new service
					Intent intent = new Intent ( this , RegisterDeviceService.class );
					// Start the register device service (used to register the device on GCM and own servers)
					startService ( intent );
					// Indicate that the save was successful
					Vibration.vibrate ( this );
					// Start the new activity
					startActivity ();
					// Specify an explicit transition animation to perform next
					ActivityTransition.SlideOutLeft ( this );
				} catch ( Exception exception ) {
					// Invalid entities list
					valid = false;
					// Set the appropriate error message
					errorMessage += "\n" + getString ( R.string.populate_data_error_message ) + "\n" + getString ( R.string.error_label ) + " : " + exception;
				} finally {
    				 //End transaction
    				DatabaseUtils.getInstance ( this ).getDaoSession ().getDatabase ().endTransaction ();
    			} // End try-catch-finally block
			} // End if
			// Check if at least one entities list is invalid
			if ( ! valid ) {
				// At least one invalid entities list
				// Clear all data
				total = 0;
				objects.clear ();
				objects = null;
				// Sync failed
				// Check if there is at least one failed table
				if ( failedTables != null && ! failedTables.isEmpty () ) {
					// Add the first failed table to the error message
					errorMessage += "\n[" + failedTables.get ( 0 );
					// Iterate over all the remaining failed tables
					for ( int i = 1 ; i < failedTables.size () ; i ++ )
						// Add the failed table to the error message
						errorMessage += " , " + failedTables.get ( i );
					// End the error message
					errorMessage += "]";
				} // End if
				// Clear the failed tables list
				if ( failedTables != null ) {
					failedTables.clear ();
					failedTables = null;
				} // End if
				// Display alert dialog
				AppDialog.getInstance ().displayAlert ( this ,
						getString ( R.string.error_label ) ,
						errorMessage ,
						AppDialog.ButtonsType.OK , null );
				// Enable UI widgets
				enable ();
			} // End if
			else {
				// Update the auto sync module 
				AutomaticSyncUtils.updateModule ( this );
			} // End else
		} // End if
	}
	
	/*
	 * Call back method executed before determining if the sync result is a success or a failure.
	 *
	 * @see me.SyncWise.Android.Modules.Sync.SyncListener#onPreFinish(int)
	 */
	@Override
	public void onPreFinish ( int requestCode ) {
		// Do nothing.
	}
	
	/*
	 * Call back method executed if the get sync process is a success.
	 *
	 * @see me.SyncWise.Android.Modules.Sync.SyncListener#onGetSuccess(de.greenrobot.dao.AbstractDao, java.util.ArrayList, int)
	 */
	@Override
	public void onGetSuccess ( AbstractDao < ? , ? > dao , ArrayList < Object > entities , int requestCode ) {
		// Determine if this is a ping to the server
		if ( requestCode == RequestCode.PING ) {
			// Dismiss any displayed dialogs (to avoid activity context leak)
			AppDialog.getInstance ().dismiss ();
			// Display indeterminate progress
			AppDialog.getInstance ().displayIndeterminateProgress ( this , null , getString ( R.string.loading_label ) );
			// Try to connect to service
			SyncHelper syncHelper = new SyncHelper ( ConnectionSettingsUtils.getGetMethodPath ( urlEditText.getText ().toString () ) );
			syncHelper.get ( this , DatabaseUtils.getInstance ( this ).getDaoSession ().getUsersDao () , new TypeToken < List < Users > > () {}.getType () , userCodeEditText.getText ().toString () , companyCodeEditText.getText ().toString () , this , RequestCode.INIT );
		} // End if
		// Determine if this is the sync initialization
		else if ( requestCode == RequestCode.INIT ) {
			// Dismiss any displayed dialogs (to avoid activity context leak)
			AppDialog.getInstance ().dismiss ();
			// Check if there is at least one retrieved user
			if ( entities.isEmpty () ) {
				// Sync failed, display alert
				AppDialog.getInstance ().displayAlert ( this ,
						null ,
						getString ( R.string.invalid_user_message ) ,
						AppDialog.ButtonsType.OK , null );
				// Re-enable widgets
				enable ();
			} // End else
			else {
				// Valid users list
				// Check if the user is active
				// Iterate over all users
				for ( Object object : entities ) {
					// Cast the object to user
					Users user = (Users) object;
					// Check if the current user is the owner
					if ( user.getUserCode ().equalsIgnoreCase ( userCodeEditText.getText ().toString () )
							&& user.getCompanyCode ().equalsIgnoreCase ( companyCodeEditText.getText ().toString () ) ) {
						// Set the appropriate user code
						userCodeEditText.setText ( user.getUserCode () );
						// Set the appropriate company code
						companyCodeEditText.setText ( user.getCompanyCode () );
						// Check if the user is active
						if ( ! user.getUserStatus ().equals ( StatusUtils.isActive () ) ) {
							// NOT active user, display alert
							AppDialog.getInstance ().displayAlert ( this ,
									null ,
									getString ( R.string.not_active_user_message ) ,
									AppDialog.ButtonsType.OK , null );
							// Re-enable widgets
							enable ();
							return;
						} // End if
					} // End if
				} // End for each
				// Retrieve sync tables
				Map<AbstractDao < ? , ? > , Type > tables = getSyncTables_Get ();
				// Check if the sync tables is valid
				if ( tables == null || tables.isEmpty () ) {
					// Sync failed, display alert
					AppDialog.getInstance ().displayAlert ( this ,
							null ,
							getString ( R.string.sync_failed_message ) ,
							AppDialog.ButtonsType.OK , null );
					// Re-enable widgets
					enable ();
					return;
				} // End if
				// Initialize objects list if needed
				if ( objects == null )
					objects = new HashMap < String , ArrayList < Object > > ();
				// Add the retrieved users to the entities list
				objects.put ( dao.getTablename () , entities );
				// Proceed with sync
				total = tables.size ();
				// Iterate over all sync tables
				for ( AbstractDao < ? , ? > _dao : tables.keySet () ) {
					// Check if the table is the Users table
					if ( _dao.getTablename ().equals ( UsersDao.TABLENAME ) )
						// Ignore the table
						continue;
					// Retrieve the dao table data
					SyncHelper syncHelper = new SyncHelper ( ConnectionSettingsUtils.getGetMethodPath ( urlEditText.getText ().toString () ) );
					syncHelper.get ( this , _dao , tables.get ( _dao ) , userCodeEditText.getText ().toString () , companyCodeEditText.getText ().toString () , this , RequestCode.ON_GOING );
				} // End for each
				// Display a progress dialog
				AppDialog.getInstance ().displayProgress ( this ,
						null ,
						getString ( R.string.loading_label ) );
				// Update progress
				AppDialog.getInstance ().setProgress ( objects.size () * 100 / total );
			} // End else
		} // End if
		else {
			// Sync succeeded
			// Check if total is valid
			if ( total == 0 )
				// Ignore sync
				return;
			// Initialize objects list if needed
			if ( objects == null )
				objects = new HashMap < String , ArrayList < Object > > ();
			// Add the retrieved objects to the entities list
			objects.put ( dao.getTablename () , entities );
			// Update progress
			AppDialog.getInstance ().setProgress ( objects.size () * 100 / total );
			// Check sync progress
			checkProgress ();
		} // End else
	}

	/*
	 * Call back method executed if the set sync process is a success.
	 *
	 * @see me.SyncWise.Android.Modules.Sync.SyncListener#onSetSuccess(int)
	 */
	@Override
	public void onSetSuccess ( final ArrayList < ArrayList < Object > > entites , int requestCode ) {
		// Not implemented
	}

	/*
	 * Call back method executed if the get sync process is a failure.
	 *
	 * @see me.SyncWise.Android.Modules.Sync.SyncListener#onGetFailure(de.greenrobot.dao.AbstractDao, int)
	 */
	@Override
	public void onGetFailure ( AbstractDao < ? , ? > dao , int requestCode ) {
		// Sync failed
		// Determine if this is a ping to the server or a sync initialization
		if ( requestCode == RequestCode.PING || requestCode == RequestCode.INIT ) {
			// Dismiss any displayed dialogs (to avoid activity context leak)
			AppDialog.getInstance ().dismiss ();
			// Display alert dialog
			AppDialog.getInstance ().displayAlert ( this ,
					null ,
					getString ( requestCode == RequestCode.PING ? R.string.server_not_reachable_message : R.string.sync_failed_message ) ,
					AppDialog.ButtonsType.OK , null );
			// Re-enable widgets
			enable ();
			// Exit method
			return;
		} // End if
		// Check if total is valid
		if ( total == 0 ) {
			// Dismiss any displayed dialogs (to avoid activity context leak)
			AppDialog.getInstance ().dismiss ();
			// Enable UI widgets
			enable ();
			// Ignore sync
			return;
		} // End if
		
		// Initialize objects list if needed
		if ( objects == null )
			objects = new HashMap < String , ArrayList < Object > > ();
		// Add null to the list
		objects.put ( dao.getTablename () , null );
		// Initialize failed tables list if needed
		if ( failedTables == null )
			failedTables = new ArrayList < String > ();
		// Add the failed table to the list
		failedTables.add ( dao.getTablename () );
		// Update progress
		AppDialog.getInstance ().setProgress ( objects.size () * 100 / total );
		// Check sync progress
		checkProgress ();
	}

	/*
	 * Call back method executed if the set sync process is a failure.
	 *
	 * @see me.SyncWise.Android.Modules.Sync.SyncListener#onSetFailure(int)
	 */
	@Override
	public void onSetFailure ( int requestCode ) {
		// Not implemented
	}

	/*
	 * Call back method executed after the sync process executed.
	 *
	 * @see me.SyncWise.Android.Modules.Sync.SyncListener#onPostFinish(int)
	 */
	@Override
	public void onPostFinish ( int requestCode ) {
		// Do nothing.
	}
	/**
	 * Gets an http response (via an http post request) for the provided url and arguments.
	 * 
	 * @param url	String holding the url.
	 * @param arguments	Map hosting the http arguments as key and value pairs.
	 * @return	The Http response as a result of the http post request.
	 */
	public static String postData ( final String url , Map < String , String > arguments ,Context context) {
		// Check if the url is valid
		if ( TextUtils.isEmpty ( url ) )
			// Invalid url
			return null;
		// Declare and initialize a collection of HTTP protocol and framework parameters.
		HttpParams httpParameters = new BasicHttpParams ();
		
		String userCode = DatabaseUtils.getCurrentUserCode ( context );
	      // Retrieve the company code
	    String companyCode = DatabaseUtils.getCurrentCompanyCode ( context );
	    int timeout=0;
	    if(userCode==null || companyCode==null)
	    timeout=30000;
	    else
	    	timeout=PermissionsUtils.getTimeOut (context,  userCode ,companyCode);

		// Set the timeout in milliseconds until a connection is established
		HttpConnectionParams.setConnectionTimeout ( httpParameters , timeout );
		// Sets the default socket timeout in milliseconds which is the timeout for waiting for data
		HttpConnectionParams.setSoTimeout ( httpParameters , timeout );
	//	SchemeRegistry schemeRegistry = new SchemeRegistry ();
		try {
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	    trustStore.load(null, null);

	   // SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
	   // sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
 
//    	schemeRegistry.register (new Scheme ("http",PlainSocketFactory.getSocketFactory (), 8090));
//    
//    	schemeRegistry.register(new Scheme("https", sf, 443));

		} catch (Exception e) {}

    	httpParameters.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
		// Initialize an http client using its default implementation
		//HttpClient httpClient = new DefaultHttpClient (new ThreadSafeClientConnManager(httpParameters, schemeRegistry), httpParameters );
    	SchemeRegistry registry = new SchemeRegistry();
    	registry.register(new Scheme("https", new TlsSniSocketFactory(), 443));
    	registry.register (new Scheme ("http",PlainSocketFactory.getSocketFactory (), 8090));
    	HttpClient httpClient  = new DefaultHttpClient(new ThreadSafeClientConnManager(httpParameters, registry),httpParameters);
    	
    	// Initialize a http post request using the specified url
		HttpPost httpPost = new HttpPost ( url );
		// Check if the arguments map is valid
		if ( arguments != null ) {
			// Declare a list of name value pairs, used to encapsulate the arguments
			List < NameValuePair > nameValuePairs = new ArrayList < NameValuePair > ( arguments.size () );
		    String key, value;
		    Iterator < String > itKeys = arguments.keySet ().iterator (); 
		 
		    // Iterate over all argument keys
		    while ( itKeys.hasNext () ) {
		    	// Retrieve the argument key
		    	key = itKeys.next ();
		    	// Retrieve the argument value
		    	value = arguments.get ( key );
		    	// Build a basic name value pair using the key and value above
		        nameValuePairs.add ( new BasicNameValuePair ( key , value ) );
		    } // End while loop
		 
		    try {
		    // Hand the entity to the request
		    	httpPost.setEntity ( new UrlEncodedFormEntity ( nameValuePairs , HTTP.UTF_8 ) );
		    	// Execute the http post request using the default context
		    	HttpResponse response = httpClient.execute ( httpPost );
		    	// Return the http response
		    	if ( response == null )
					// Invalid response
					return "-1:ERROR invalid response";
			 
					// Retrieve the string response
					String result = EntityUtils.toString ( response.getEntity () );
					Log.e( "Response", "  " + result.toString() );
					return result ;//answer[1];
				} catch ( ParseException exception ) {
					// Do nothing
				 
		    } catch ( UnsupportedEncodingException exception ) {
				// Do nothing.
			} catch ( ClientProtocolException exception ) {
				// Do nothing.
			} catch ( IOException exception ) {
			//	String error=exception.getMessage().toString();
			//	Log.e("ali","  "+error);
				// Do nothing.
			} // End try-catch block
		} // End if
		return null;
	}
	
	private void getData(String userCode,String companyCode,String url )
	{
		AppDialog.getInstance ().displayIndeterminateProgress ( this , null , getString ( R.string.connecting_label ) );
		SyncHelper syncHelper = new SyncHelper ( ConnectionSettingsUtils.getConnectMethodPath ( url ) );
		syncHelper.get ( this , DatabaseUtils.getInstance ( this ).getDaoSession ().getUsersDao () , new TypeToken < List < Users > > () {}.getType () , userCode , companyCode , this , RequestCode.PING );

	}
 
private class IsLisencedGet extends AsyncTask <Void, Void, String>  {
	
	private Activity activity;
	private ProgressDialog dialog1;
	private String url;
	private String userCode;
	private String companyCode;
	
	public IsLisencedGet (final Activity activity, final String url ,final String userCode,final String companyCode) {
	    this.url = url;
	   this.userCode=userCode;
	   this.companyCode=companyCode;
	    this.activity = activity;
	}

	@Override
	protected void onPreExecute () {
		// Display indeterminate progress before starting a new thread
        super.onPreExecute();
        this.dialog1 = new ProgressDialog( activity );
		this.dialog1.setMessage( " Checking Connection " );
		this.dialog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		this.dialog1.show();
	}
  
	@Override
	protected String doInBackground ( Void ... params ) {
		 
		
//	    Map < String , String > arguments = new HashMap < String , String > ();
//		
//		// Add the user code argument
//		arguments.put ( UsersDao.Properties.UserCode.columnName , userCode );
//		
//		// Add the company code argument
//		arguments.put ( UsersDao.Properties.CompanyCode.columnName , companyCode );
//		
//		String serial=android.os.Build.SERIAL;
//		
//		arguments.put ( "SerialNumber" , serial );
//	 
//		// Add the checksum argument
//		arguments.put ( SyncUtils.CHECKSUM , SyncUtils.getArgumentsChecksum ( activity , arguments ) );
//		String res= postData (   ConnectionSettingsUtils.getIsLisencedMethodPath ( url )   ,arguments ,activity ); 
//	 	// Fire the http post request and retrieve the http response
//		if(res==null)
			return "1";
		
//		return res;
	}

	@Override
	protected void onPostExecute(String result) {
		
		final String answer   = result.toString()  ;
		if(answer.equals("down"))
		{       this.dialog1.dismiss();
			AppDialog.getInstance ().displayAlert ( activity ,
					null ,
					getString ( R.string.server_not_reachable_message   ),
					AppDialog.ButtonsType.OK , new DialogInterface.OnClickListener () {
						@Override
						public void onClick ( DialogInterface dialog , int which ) {
							enable ();

						}
			} );
		return;
		}
	 
		 if (this.dialog1.isShowing()) 
	           this.dialog1.dismiss();
			// Dismiss any displayed dialogs (to avoid activity context leak)
	 
							// TODO Auto-generated method stub
							if ( answer  .equals("1" ) ) {
								try {
									getData(  userCode,  companyCode,  url );

								 
								}
								catch ( Exception ex) {
								 
								}
								finally {
								
								}
							}else{
								AppDialog.getInstance ().displayAlert ( activity ,
										null ,
										"this user is not lisenced " ,
										AppDialog.ButtonsType.OK , new DialogInterface.OnClickListener () {
											@Override
											public void onClick ( DialogInterface dialog , int which ) {
												enable ();

											}
								} );
							}
					 
	}
}
 
}