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

import java.util.Calendar;
import java.util.List;

import de.greenrobot.dao.Query;

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.AppVersion;
import me.SyncWise.Android.DateTime;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Database.UserVersions;
import me.SyncWise.Android.Database.UserVersionsDao;
import me.SyncWise.Android.Database.Users;
import me.SyncWise.Android.Database.UsersDao;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.TextView.OnEditorActionListener;

/**
 * Activity used as login screen, used to identify the user and verify his/her credentials.
 * 
 * @author Elias
 * @sw.todo	<b>Login Activity Implementation :</b><br>
 * <ul>
 * <li>Extend this class.</li>
 * <li>Set the next activity to start using the {@link #setActivity(Class)} method in the {@link Activity#onCreate(Bundle)} method.</li>
 * <li>Assign the solution logo resource id (drawable). The ID of the image view is {@code R.id.icon_logo_solution}. <em>Optional but Recommended</em><br>
 * The width and height are automatically computed according to the current screen size / orientation.</li>
 * <li>Do not forget to add your new class in the {@code AndroidManifest.xml} file.</li>
 * </ul>
 */
public class LoginActivity extends Activity implements TextWatcher , OnEditorActionListener {
	
	/**
	 * Bundle key used to put/retrieve the content of {@link #isCreated}.
	 */
	private static final String IS_CREATED = LoginActivity.class.getName () + ".IS_CREATED";
	
	/**
	 * Boolean used to indicate if the activity has been previously created.<br>
	 * This flag is mainly used to determine if the current activity creation is the first or not (activity re-creation due to phone rotation for example).
	 */
	private boolean isCreated;
	
	/**
	 * Boolean used to indicate if the activity is displayed on the screen.
	 */
	private boolean isDisplayed;
	
	/**
	 * Reference to the password field
	 */
	private EditText passwordEditText;
	
	/**
	 * Reference to the password clear button
	 */
	private Button passwordClearButton;
	
	/**
	 * Query for the entity Users used to retrieve the current user.
	 */
	private Query < Users > currentUserQuery;
	
	/**
	 * Query for the entity Users used to search for the provided credentials.
	 */
	private Query < Users > userCredentials;
	
	/**
	 * AsyncTask helper class used to compute and display the the time elapsed since the previous login.
	 * 
	 * @author Elias
	 *
	 */
	private class PreviousLogin extends AsyncTask < Void , Void , String > {

		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected String doInBackground ( Void ... params ) {
			// Initialize the current user query (if needed)
			if ( currentUserQuery == null )
				currentUserQuery = DatabaseUtils.getInstance ( LoginActivity.this ).getDaoSession ().getUsersDao ().queryBuilder ()
					.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( LoginActivity.this ) ) ,
							UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( LoginActivity.this ) ) )
					.build ();
			// Execute the query to retrieve the current user
			Users currentUser = currentUserQuery.uniqueOrThrow ();
			// Retrieve the previous login date
			Calendar previousLogin = Calendar.getInstance ();
			previousLogin.setTimeInMillis ( currentUser.getStampDate ().getTime () );
			// Retrieve the current date
			Calendar calendar = Calendar.getInstance ();
			// Determine if the previous login occurred today
			if ( calendar.get ( Calendar.DATE ) == previousLogin.get ( Calendar.DATE )
					&& calendar.get ( Calendar.MONTH ) == previousLogin.get ( Calendar.MONTH )
					&& calendar.get ( Calendar.YEAR ) == previousLogin.get ( Calendar.YEAR ) ) {
				// The previous login occurred today
				// Determine if the elapsed time is less than one minute
				if ( calendar.get ( Calendar.HOUR_OF_DAY ) == previousLogin.get ( Calendar.HOUR_OF_DAY )
						&& calendar.get ( Calendar.MINUTE ) == previousLogin.get ( Calendar.MINUTE ) )
					// The previous login occurred few seconds ago
					return AppResources.getInstance ( LoginActivity.this ).getString ( LoginActivity.this , R.string.seconds_since_previous_login_message );
				// Otherwise compute the number of minutes elapsed
				else {
					// Compute the number of elapsed minutes since the previous login
					int minutes = ( calendar.get ( Calendar.HOUR_OF_DAY ) - previousLogin.get ( Calendar.HOUR_OF_DAY ) ) * 60
							+ ( calendar.get ( Calendar.MINUTE ) - previousLogin.get ( Calendar.MINUTE ) );
					// Determine the number of minutes exceed one hour
					if ( minutes < 60 )
						// Few minutes elapsed
						return minutes + " " + AppResources.getInstance ( LoginActivity.this ).getString ( LoginActivity.this , R.string.minutes_since_previous_login_message );
					else
						// Few hours elapsed
						return ( minutes / 60 ) + " " + AppResources.getInstance ( LoginActivity.this ).getString ( LoginActivity.this , R.string.hours_since_previous_login_message );
				} // End else
			} // End if
			// Set the calendar to yesterday
			calendar.add ( Calendar.DATE , -1 );
			// Determine if the previous login occurred yesterday
			if ( calendar.get ( Calendar.DATE ) == previousLogin.get ( Calendar.DATE )
					&& calendar.get ( Calendar.MONTH ) == previousLogin.get ( Calendar.MONTH )
					&& calendar.get ( Calendar.YEAR ) == previousLogin.get ( Calendar.YEAR ) ) {
				// The previous login occurred yesterday
				return AppResources.getInstance ( LoginActivity.this ).getString ( LoginActivity.this , R.string.yesterday_label ) + ", "
						+ AppResources.getInstance ( LoginActivity.this ).getString ( LoginActivity.this , R.string.at_label ) + " "
						+ previousLogin.get ( Calendar.HOUR_OF_DAY ) + ":" + ( previousLogin.get ( Calendar.MINUTE ) < 10 ? "0" : "" ) + previousLogin.get ( Calendar.MINUTE );
			} // End if
			else
				// Otherwise the previous login occurred in a previous date and time
				return DateTime.getFullDateTime ( LoginActivity.this , previousLogin );
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( String timeElapsed ) {
			// Determine if the result is valid
			if ( TextUtils.isEmpty ( timeElapsed ) )
				// Invalid result
				return;
			// Otherwise, display the result
			( (TextView) findViewById ( R.id.label_previous_login_date ) ).setText ( timeElapsed );
		}
		
	}
	
	/**
	 * AsyncTask helper class used to valid the user credentials asynchronously.
	 * 
	 * @author Elias
	 *
	 */
	private class ValidateCredentials extends AsyncTask < Void , Void , Boolean > {
		
		/**
		 * String holding an error message
		 */
		private String error;
		
		/**
		 * Reference to an edit text containing an error.
		 */
		private EditText errorField; 
		
		/**
		 * Reference to a view that needs focus.
		 */
		private View focusView;
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Boolean doInBackground ( Void ... params ) {
			// Retrieve the user password
			String password = ( (EditText) findViewById ( R.id.edittext_password ) ).getText ().toString ();
			
			// Determine if the user credentials query is valid
			if ( userCredentials == null )
				// Initialize the user credentials query
				userCredentials = DatabaseUtils.getInstance ( LoginActivity.this ).getDaoSession ().getUsersDao ().queryBuilder ()
					.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( LoginActivity.this ) ) ,
							UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( LoginActivity.this ) ) )
					.build ();
			
			// Query DB
			Users user = userCredentials.unique ();
			// Make sure the user password is valid
			if ( user.getUserPassword () == null )
				// Set the user password
				user.setUserPassword ( "" );
			
			// Determine if the password is valid
			if ( ! password.equals ( user.getUserPassword () ) ) {
				// Display an error message in UI thread
				errorField = passwordEditText;
				error = AppResources.getInstance ( LoginActivity.this ).getString ( LoginActivity.this , R.string.error_invalid_password_message );
				// Focus the password field
				focusView = passwordEditText;
				// Indicate that the validation failed
				return false;
			} // End if
			
			// Set the previous login stamp date
			user.setStampDate ( Calendar.getInstance ().getTime () );
			// Update the user in DB
			DatabaseUtils.getInstance ( LoginActivity.this ).getDaoSession ().getUsersDao ().update ( user );
			// Indicate that the validation is successful
			return true;
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( Boolean isValid ) {
			// Clear the task reference
			validateCredentials = null;
			
			// Determine if the current activity is displayed
			if ( ! isDisplayed ) {
				// Clear references
				clear ();
				// Do nothing
				return;
			} // End if
			
			// Determine if the validation is successful
			if ( isValid )
				// Successful validation
				startActivity ();
			else {
				// Check if a field contains an error
				if ( errorField != null && ! TextUtils.isEmpty ( error ) )
					// Display error on field
					errorField.setError ( error );
				
				// Check if a view needs focus
				if ( focusView != null )
					// Request view focus
					focusView.requestFocus ();
			} // End else
			
			// Clear references
			clear ();
		}
		
		/**
		 * Clears all references to avoid memory leak.
		 */
		private void clear () {
			error = null;
			errorField = null;
			focusView = null;
		}
		
	}
	
	/**
	 * Reference to the credentials validation task.
	 */
	private ValidateCredentials validateCredentials;
	
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
//	public void startActivity () {
//		// Start the new activity
//		startActivity ( new Intent ( this , getActivity () ) );
//		// Close this activity
//		finish ();
//	}
	public void startActivity () {
		// Start the new activity
//		startActivity ( new Intent ( this , getActivity () ) );
//		// Close this activity
//		finish ();
		
		if(PermissionsUtils.getEnableDuo(this, DatabaseUtils.getCurrentUserCode(this), DatabaseUtils.getCurrentCompanyCode(this)) 
				){Intent intent = new Intent ( this , DuoActivity.class );
				startActivityForResult(intent, 100);
					
			}else{	
		Intent intent = new Intent ( this , DuoActivity.class );
		startActivityForResult(intent, 100);
		
			}
	}
	@Override
	protected void onActivityResult ( int requestCode , int resultCode, Intent data ) {
    	// Check if the result is successful
		if ( requestCode == 100)
	 	{		 
	 		startActivity ( new Intent ( this , getActivity () ) );
	 		// Close this activity
	 		finish ();
	 		return;
	 	}
	 
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
	public static String getApplicationName(Context context) {
	    ApplicationInfo applicationInfo = context.getApplicationInfo();
	    int stringId = applicationInfo.labelRes;
	    return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
	}
	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate ( Bundle savedInstanceState ) {
		// Superclass method call	
		super.onCreate ( savedInstanceState );
		
		// Determine if a saved instance state is provided
		if ( savedInstanceState != null ) {
			// Restore the content provided by the saved instance state 
			isCreated = savedInstanceState.getBoolean ( IS_CREATED , isCreated );
		} // End if
		
		// Hide title bar
		requestWindowFeature ( Window.FEATURE_NO_TITLE );
		// Hide status bar
		getWindow().setFlags ( WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN );
		// Set the activity content from a layout resource.
		setContentView ( R.layout.login_activity );
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE) ; 
	       
		  
        // in the below line, we are checking for permissions
//if (ActivityCompat.checkSelfPermission(  this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
// ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);
// return;
//}      
String IMEINumber="";
if(telephonyManager!=null)
IMEINumber = telephonyManager.getDeviceId();
try {
    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
    Integer Code = pInfo.versionCode;
    String version = pInfo.versionName +"__"+Code;
    String type=pInfo.packageName; 
 List< UserVersions>   userVersions = DatabaseUtils.getInstance ( LoginActivity.this ).getDaoSession ().getUserVersionsDao ().queryBuilder ()
			.where ( UserVersionsDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( LoginActivity.this ) )  
				  )
			.list();
 if(userVersions.size()>0 && (!userVersions.get(0).getUserCode().equals(DatabaseUtils.getCurrentUserCode ( LoginActivity.this ))
		 ||  ! userVersions.get(0).getVersionCode ().equals( version))){
	 userVersions.get(0).setVersionCode(version+"- "+getApplicationName(this));
	 userVersions.get(0).setIMEI(IMEINumber +"");
	 DatabaseUtils.getInstance ( LoginActivity.this ).getDaoSession ().getUserVersionsDao ().update (  userVersions.get(0) );
 }
	 else if(userVersions.size()==0)
	 { UserVersions uv = new UserVersions(null, DatabaseUtils.getCurrentUserCode ( LoginActivity.this ), DatabaseUtils.getCurrentCompanyCode(LoginActivity.this), version+"-"+getApplicationName(this),IMEINumber, Calendar.getInstance ().getTime () );
	 DatabaseUtils.getInstance ( LoginActivity.this ).getDaoSession ().getUserVersionsDao ().insert (  uv );
		
	 }
		 
	 
 
    
 //   user.set
} catch (PackageManager.NameNotFoundException e) {
    e.printStackTrace();
}
		// Declare and initialize a point object used to store the screen size
		Point size = new Point();
		// Compute the screen size
		getWindowManager ().getDefaultDisplay ().getSize ( size );
		
		// Retrieve a reference to the solution logo
		ImageView solutionLogo = (ImageView) findViewById ( R.id.icon_logo_solution );
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
		
		// Retrieve a reference to the clear buttons
		Button usernameClearButton = (Button) findViewById ( R.id.button_clear_username );
		passwordClearButton = (Button) findViewById ( R.id.button_clear_password );
		
		// Retrieve references to the user credential fields
		EditText usernameEditText = (EditText) findViewById ( R.id.edittext_username );
		passwordEditText = (EditText) findViewById ( R.id.edittext_password );
		
		// Display the field hints
		usernameEditText.setHint ( AppResources.getInstance ( this ).getString ( this , R.string.user_login_name_hint ) );
		passwordEditText.setHint ( AppResources.getInstance ( this ).getString ( this , R.string.password_hint ) );
		
		// Set a special listener to be called when an action is performed on the text view
		passwordEditText.setOnEditorActionListener ( this );
		
		// Assign a text changed listener to the fields in order to display/hide the clear button accordingly
		passwordEditText.addTextChangedListener ( this );
		
		// Display the current user login name
		usernameEditText.setText ( DatabaseUtils.getInstance ( this ).getDaoSession ().getUsersDao ().queryBuilder ()
				.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( this ) ) ,
						UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( this ) ) ).unique ().getUserLoginName () );
		// Disable the user login name field
		usernameEditText.setEnabled ( false );
		usernameClearButton.setEnabled ( false );
		usernameClearButton.setVisibility ( View.GONE );
		// Focus the password field
		passwordEditText.requestFocus ();
		
		// Display the button label
		( (TextView) findViewById ( R.id.label_button_login ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.sign_in_label ) );
		
		// Display the previous login label
		( (TextView) findViewById ( R.id.label_previous_login ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.previous_login_label ) + " :" );
		
		// Apply the login panel layout gravity attribute
		// Due to a bug, it cannot be applied in XML when using the include tag (and hence should be done programmatically)
		( (FrameLayout.LayoutParams) findViewById ( R.id.layout_login_panel ).getLayoutParams () ).gravity = Gravity.CENTER;
		
		// Retrieve a reference to the application version label
		TextView appVersion = (TextView) findViewById ( R.id.label_app_version );
		// Apply the layout gravity attribute
		// Due to a bug, it cannot be applied in XML when using the include tag (and hence should be done programmatically)
		( (FrameLayout.LayoutParams) appVersion.getLayoutParams () ).gravity = Gravity.BOTTOM | Gravity.RIGHT;
		// Display the application version
		appVersion.setText ( AppVersion.getSpannableText ( this ) , BufferType.SPANNABLE );
		
		// Determine if this is the first activity creation or a re-creation
		if ( ! isCreated ) {
			// Set the flag
			isCreated = true;
			// Declare and initialize sliding animations
			Animation slideInRight = AnimationUtils.loadAnimation ( this , R.anim.slide_in_right );
			
			// Assign sliding animations to views
			findViewById ( R.id.layout_previous_login ).startAnimation ( slideInRight );
		} // End if
		
	}
	
	/*
	 * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause(), for your activity to start interacting with the user.
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume () {
		// Indicate that the activity is displayed
		isDisplayed = true;
		// Superclass method call
		super.onResume ();
		// Compute and display the previous login date asynchronously
		new PreviousLogin ().execute ();
	}
	
	/*
	 * Called as part of the activity lifecycle when an activity is going into the background, but has not (yet) been killed.
	 *
	 * @see android.app.Activity#onPause()
	 */
	@Override
    protected void onPause () {
		// Indicate that the activity is NOT displayed
		isDisplayed = false;
		// Superclass method call
		super.onPause ();
	}
	
	/*
	 * Called when the activity has detected the user's press of the back key. 
	 *
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed () {
		// Display exit confirmation
		AppDialog.getInstance ().displayAlert ( this ,
				null ,
				AppResources.getInstance ( LoginActivity.this ).getString ( LoginActivity.this , R.string.exit_confirmation_message ) ,
				AppDialog.ButtonsType.YesNo ,
				new DialogInterface.OnClickListener () {
					@Override
					public void onClick ( DialogInterface dialog , int which ) {
						// Determine the clicked button
						switch ( which ) {
						case DialogInterface.BUTTON_POSITIVE:
							// Finish activity
							LoginActivity.this.finish ();
							break;
						case DialogInterface.BUTTON_NEGATIVE:
							// Dismiss dialog
							AppDialog.getInstance ().dismiss ();
							break;
						} // End switch
					}
				} );
	}
	
    /*
     * Called to retrieve per-instance state from an activity before being killed so that the state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle).
     *
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState ( Bundle outState ) {
    	// Superclass method call
    	super.onSaveInstanceState ( outState );
    	// Save the content of isCreated in the outState bundle
    	outState.putBoolean ( IS_CREATED , isCreated );
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
			isCreated = false;
			activity = null;
			validateCredentials = null;
			currentUserQuery = null;
			userCredentials = null;
			if ( passwordEditText != null ) {
				passwordEditText.removeTextChangedListener ( this );
				passwordEditText.setOnEditorActionListener ( null );
			}
			passwordEditText = null;
			passwordClearButton = null;
		} // End if
    }
	
	/*
	 * Method called to notify before the text is changed.
	 * 
	 * @see android.text.TextWatcher#afterTextChanged(Editable)
	 */
	@Override
	public void afterTextChanged ( Editable s ) {
		// Check if the password field is empty and the clear button visible
		if ( passwordEditText.getText ().toString ().isEmpty () && passwordClearButton.getVisibility () == View.VISIBLE )
			// Hide the clear button
			passwordClearButton.setVisibility ( View.GONE );
		// Otherwise check if the password field is NOT empty and the clear button is hidden
		else if ( ( ! passwordEditText.getText ().toString ().isEmpty () ) && passwordClearButton.getVisibility () == View.GONE )
			// Display the clear button
			passwordClearButton.setVisibility ( View.VISIBLE );
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
			// Validate credentials
			validateCredentials ();
			// Indicate that the action is consumed
			return true;
		} // End if
		// Indicate that the action is NOT consumed
		return false;
	}
	
	/**
	 * Called when a view has been clicked.<br>
	 * If the view id corresponds to the username or password clear buttons, the corresponding edit text is cleared.
	 * 
	 * @param view	The clicked view.
	 */
	public void clearText ( View view ) {
		// Determine if the provided view is valid
		if ( view == null )
			// Invalid view
			return;
		// Check the view id
		if ( view.getId () == R.id.button_clear_password )
			// Clear the password field
			( (EditText) findViewById ( R.id.edittext_password ) ).setText ( "" );
	}
	
	/**
	 * Called when a view has been clicked.<br>
	 * The credentials entered by the users are validated.
	 * 
	 * @param view	The clicked view.
	 */
	public void signIn ( View view ) {
		// Validate credentials
		validateCredentials ();
	}
	
	/**
	 * Validates the credentials provided by the user.
	 */
	private void validateCredentials () {
		// Hide the software keyboard
        ( (InputMethodManager) getBaseContext ().getSystemService ( INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow ( passwordEditText.getWindowToken (), 0 );
		
        // Check if a validation check is currently running
        if ( validateCredentials != null )
        	// Validation in progress, exit method
        	return;
        
		// Reset previous errors
		passwordEditText.setError ( null );
		
		// Validate the user credentials asynchronously
		validateCredentials = new ValidateCredentials ();
		validateCredentials.execute ();
	}

}