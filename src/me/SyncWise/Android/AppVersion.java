/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android;

import java.text.DecimalFormat;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

/**
 * Helper Class used to manage the application version info.
 * 
 * @author Elias
 *
 */
public class AppVersion {

	/**
	 * Retrieves the application version code.<br>
	 * If not available, the method throws a {@linkplain android.content.pm.PackageManager.NameNotFoundException} exception.<br>
	 * The method should be surrounded by a try-catch block.
	 * 
	 * @param context The application context.
	 * @return	Integer holding the application version code.
	 * @throws NameNotFoundException	Exception thrown if the application version code is not found.
	 * @see	#getVersionName(Context)
	 * @see #getText(Context)
	 * @see #getSpannableText(Context)
	 */
	public static int getVersionCode ( final Context context ) throws NameNotFoundException {
		// Retrieve and return the application version code
		return context.getPackageManager ().getPackageInfo ( context.getPackageName () , 0 ).versionCode;
	}
	
	/**
	 * Retrieves the application version name.<br>
	 * If not available, the method throws a {@linkplain android.content.pm.PackageManager.NameNotFoundException NameNotFoundException} exception.<br>
	 * The method should be surrounded by a try-catch block.
	 * 
	 * @param context The application context.
	 * @return	String holding the application version name.
	 * @throws NameNotFoundException	Exception thrown if the application version name is not found.
	 * @see	#getVersionCode(Context)
	 * @see #getText(Context)
	 * @see #getSpannableText(Context)
	 */
	public static String getVersionName ( final Context context ) throws NameNotFoundException {
		// Retrieve and return the application version name
		return context.getPackageManager ().getPackageInfo ( context.getPackageName () , 0 ).versionName;
	}
	
	/**
	 * Retrieves the application version in a string formatted manner.<br>
	 * If it is invalid, a Not Available notification is returned.
	 * 
	 * @param context	The application context.
	 * @return	String holding the application version, or NA if not available.
	 * @see #getVersionCode(Context)
	 * @see #getVersionName(Context)
	 * @see #getSpannableText(Context)
	 */
	public static String getText ( final Context context ) {
		// Retrieve and display the version code and name
		try {
			// Declare and initialize a one digit string formatter
			DecimalFormat oneDigit = new DecimalFormat ( ".0" );
			// Retrieve the application version code
			int versionCode = getVersionCode ( context );
			// Retrieve the application version name
			String versionName = getVersionName ( context );
			// Retrieve the version label
			String versionLabel = AppResources.getInstance ( context ).getString ( context , R.string.version_label ) + " ";
			// Compute the application version description
			String version = versionLabel + oneDigit.format ( versionCode );
			if ( ! TextUtils.isEmpty ( versionName ) )
				version += " [ " + versionName + " ]";
			// Return the application version description
			return version;
		} catch ( NameNotFoundException exception ) {
			// Invalid version
			return AppResources.getInstance ( context ).getString ( context , R.string.version_label ) + " " + AppResources.getInstance ( context ).getString ( context , R.string.not_available_abbreviation );
		} // End of try-catch block
	}
	
	/**
	 * Retrieves the application version in a {@link android.text.SpannableString SpannableString} formatted manner.<br>
	 * If it is invalid, a Not Available notification is returned.<br>
	 * The version code and name (or the <em>not available</em> label) are highlighted in BOLD and in BLUE.<br>
	 * Set the returned spannable string using the {@link android.widget.TextView#setText(CharSequence, android.widget.TextView.BufferType) setText(...)} method with {@link android.widget.TextView.BufferType#SPANNABLE SPANNABLE} as buffer type.
	 * 
	 * @param context	The application context.
	 * @return	Spannable string holding the application version, or NA if not available.
	 * @see #getVersionCode(Context)
	 * @see #getVersionName(Context)
	 * @see #getText(Context)
	 */
	public static SpannableString getSpannableText ( final Context context ) {
		// Retrieve the version label
		String versionLabel = AppResources.getInstance ( context ).getString ( context , R.string.version_label ) + " ";
		// Retrieve and display the version code and name
		try {
			// Declare and initialize a one digit string formatter
			DecimalFormat oneDigit = new DecimalFormat ( ".0" );
			// Retrieve the application version code
			String versionCode = oneDigit.format ( getVersionCode ( context ) );
			// Retrieve the application version name
			String versionName = getVersionName ( context );
			// Build a spannable string out of the retrieved strings in order to apply various spans
			SpannableString version = new SpannableString ( versionLabel + versionCode + ( TextUtils.isEmpty ( versionName ) ? "" : " [ " + versionName + " ]" ) );
			// Apply style span (for the version code)
			version.setSpan ( new StyleSpan ( Typeface.BOLD ) , versionLabel.length () , versionLabel.length () + versionCode.length () , 0 );
			// Apply style span (for the version name)
			version.setSpan ( new StyleSpan ( Typeface.BOLD ) , versionLabel.length () + versionCode.length () + 3 , version.length () - 2 , 0 );			
			// Apply foreground color span (for the version code)
			version.setSpan ( new ForegroundColorSpan ( Color.BLUE ) , versionLabel.length () , versionLabel.length () + versionCode.length () , 0 );
			// Apply foreground color span (for the version name)
			version.setSpan ( new ForegroundColorSpan ( Color.BLUE ) , versionLabel.length () + versionCode.length () + 3 , version.length () - 2 , 0 );	
			// Return the application version description
			return version;
		} catch ( NameNotFoundException exception ) {
			// Invalid version
			// Retrieve the not available label
			String NA = AppResources.getInstance ( context ).getString ( context , R.string.not_available_abbreviation );
			// Build a spannable string out of the retrieved strings in order to apply various spans
			SpannableString version = new SpannableString ( versionLabel + NA );
			// Apply style span
			version.setSpan ( new StyleSpan ( Typeface.BOLD ) , versionLabel.length () , version.length () , 0 );
			// Apply color span
			version.setSpan ( new ForegroundColorSpan ( Color.BLUE ) , versionLabel.length () , version.length () , 0 );
			// Return the version
			return version;
		} // End of try-catch block
	}
	
}
