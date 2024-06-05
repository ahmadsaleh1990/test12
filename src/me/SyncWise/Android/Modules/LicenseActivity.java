/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules;

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.AppVersion;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.DatabaseUtils;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
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
 * Activity used as activation screen, used to setup the application used a license key.
 * 
 * @author Elias
 * @sw.todo	<b>License Activity Implementation :</b><br>
 * <ul>
 * <li>Extend this class.</li>
 * <li>Set the next activity to start using the {@link #setActivity(Class)} method in the {@link Activity#onCreate(Bundle)} method.</li>
 * <li>Assign the solution logo resource id (drawable). The ID of the image view is {@code R.id.icon_logo_solution}. <em>Optional but Recommended</em><br>
 * The width and height are automatically computed according to the current screen size / orientation.</li>
 * <li>Do not forget to add your new class in the {@code AndroidManifest.xml} file.</li>
 * </ul>
 */
public class LicenseActivity extends Activity implements TextWatcher , OnEditorActionListener {
	
	/**
	 * Reference to the license field
	 */
	private EditText licenseEditText;
	
	/**
	 * Reference to the license clear button
	 */
	private Button licenseClearButton;
	
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
		// Start the new activity
		startActivity ( new Intent ( this , getActivity () ) );
		// Close this activity
		finish ();
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
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate ( Bundle savedInstanceState ) {
		// Superclass method call	
		super.onCreate ( savedInstanceState );
		
		// Hide title bar
		requestWindowFeature ( Window.FEATURE_NO_TITLE );
		// Hide status bar
		getWindow().setFlags ( WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN );
		// Set the activity content from a layout resource.
		setContentView ( R.layout.license_activity );
		
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
		licenseClearButton = (Button) findViewById ( R.id.button_clear_license );
		
		// Retrieve references to the license key fields
		EditText serialCodeEditText = (EditText) findViewById ( R.id.edittext_serial_code );
		licenseEditText = (EditText) findViewById ( R.id.edittext_license );
		
		// Display the field hints
		licenseEditText.setHint ( AppResources.getInstance ( this ).getString ( this , R.string.license_key_hint ) );
		
		// Set a special listener to be called when an action is performed on the text view
		licenseEditText.setOnEditorActionListener ( this );
		
		// Assign a text changed listener to the fields in order to display/hide the clear button accordingly
		licenseEditText.addTextChangedListener ( this );
		
		// Display the current serial code
		serialCodeEditText.setText ( android.os.Build.SERIAL );
		// Disable the serial code field
		serialCodeEditText.setEnabled ( false );
		// Focus the license key field
		licenseEditText.requestFocus ();
		
		// Display the button label
		( (TextView) findViewById ( R.id.label_button_activate ) ).setText ( AppResources.getInstance ( this ).getString ( this , R.string.activate_label ) );
		
		// Apply the login panel layout gravity attribute
		// Due to a bug, it cannot be applied in XML when using the include tag (and hence should be done programmatically)
		( (FrameLayout.LayoutParams) findViewById ( R.id.layout_activate_panel ).getLayoutParams () ).gravity = Gravity.CENTER;
		
		// Retrieve a reference to the application version label
		TextView appVersion = (TextView) findViewById ( R.id.label_app_version );
		// Apply the layout gravity attribute
		// Due to a bug, it cannot be applied in XML when using the include tag (and hence should be done programmatically)
		( (FrameLayout.LayoutParams) appVersion.getLayoutParams () ).gravity = Gravity.BOTTOM | Gravity.RIGHT;
		// Display the application version
		appVersion.setText ( AppVersion.getSpannableText ( this ) , BufferType.SPANNABLE );
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
				AppResources.getInstance ( LicenseActivity.this ).getString ( LicenseActivity.this , R.string.exit_confirmation_message ) ,
				AppDialog.ButtonsType.YesNo ,
				new DialogInterface.OnClickListener () {
					@Override
					public void onClick ( DialogInterface dialog , int which ) {
						// Determine the clicked button
						switch ( which ) {
						case DialogInterface.BUTTON_POSITIVE:
							// Finish activity
							LicenseActivity.this.finish ();
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
			if ( licenseEditText != null ) {
				licenseEditText.removeTextChangedListener ( this );
				licenseEditText.setOnEditorActionListener ( null );
			}
			licenseEditText = null;
			licenseClearButton = null;
		} // End if
    }
	
	/*
	 * Method called to notify before the text is changed.
	 * 
	 * @see android.text.TextWatcher#afterTextChanged(Editable)
	 */
	@Override
	public void afterTextChanged ( Editable s ) {
		// Check if the license field is empty and the clear button visible
		if ( licenseEditText.getText ().toString ().isEmpty () && licenseClearButton.getVisibility () == View.VISIBLE )
			// Hide the clear button
			licenseClearButton.setVisibility ( View.GONE );
		// Otherwise check if the license field is NOT empty and the clear button is hidden
		else if ( ( ! licenseEditText.getText ().toString ().isEmpty () ) && licenseClearButton.getVisibility () == View.GONE )
			// Display the clear button
			licenseClearButton.setVisibility ( View.VISIBLE );
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
			// Validate license
			validateLicense ();
			// Indicate that the action is consumed
			return true;
		} // End if
		// Indicate that the action is NOT consumed
		return false;
	}
	
	/**
	 * Called when a view has been clicked.<br>
	 * If the view id corresponds to the license clear button, the corresponding edit text is cleared.
	 * 
	 * @param view	The clicked view.
	 */
	public void clearText ( View view ) {
		// Determine if the provided view is valid
		if ( view == null )
			// Invalid view
			return;
		// Check the view id
		if ( view.getId () == R.id.button_clear_license )
			// Clear the password field
			( (EditText) findViewById ( R.id.edittext_license ) ).setText ( "" );
	}
	
	/**
	 * Called when a view has been clicked.<br>
	 * The license key is validated.
	 * 
	 * @param view	The clicked view.
	 */
	public void activate ( View view ) {
		// Validate license
		validateLicense ();
	}
	
	/**
	 * Validates the license key provided by the user.
	 */
	private void validateLicense () {
		// Hide the software keyboard
        ( (InputMethodManager) getBaseContext ().getSystemService ( INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow ( licenseEditText.getWindowToken (), 0 );
		
		// Reset previous errors
        licenseEditText.setError ( null );
        
        // Retrieve the license key
        String licenseKey = licenseEditText.getText ().toString ();
        // Declare and initialize a string used to host the extracted key
        String extractedKey = "";
        
        // Check if the key is valid (should be 20 characters with no white space
        if ( licenseKey.length () != 20 || licenseKey.indexOf ( ' ' ) >= 0 ) {
			// Display error on field
        	licenseEditText.setError ( AppResources.getInstance ( this ).getString ( this , R.string.error_invalid_license_key_message ) );
        	// Exit method
        	return;
        } // End if
        else
        	// Iterate over characters
        	for ( int i = 3 ; i < 20 ; i += 4 )
        		// Extract key character
        		extractedKey += licenseKey.charAt ( i );
        
        // Check if extracted key is valid
        if ( extractedKey.equalsIgnoreCase ( "1SYNC" )
        		|| extractedKey.equalsIgnoreCase ( "2WISE" )
        		|| extractedKey.equalsIgnoreCase ( "3PROF" )
        		|| extractedKey.equalsIgnoreCase ( "4DATA" )
        		|| extractedKey.equalsIgnoreCase ( "5MOBI" )
        		|| extractedKey.equalsIgnoreCase ( "6EMSD" ) ) {
        	// Indicate that the device is licensed
        	DatabaseUtils.setIsLicensed ( this );
			// Successful validation
			startActivity ();
        } // End if
        else
			// Display error on field
        	licenseEditText.setError ( AppResources.getInstance ( this ).getString ( this , R.string.error_invalid_license_key_message ) );
	}

}