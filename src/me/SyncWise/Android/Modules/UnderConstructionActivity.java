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

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

/**
 * Activity used as an indication to a unavailable module.
 * 
 * @author Elias
 * @sw.todo	<b>Under Construction Activity Implementation :</b><br>
 * <ul>
 * <li>Add to the intent the module title using {@link #MODULE_TITLE} as key. <em>Optional</em></li>
 * <li>Start this activity.</li>
 * <li>Do not forget to add this class in the {@code AndroidManifest.xml} file.<br>
 * <b>AND</b> set the base theme for this context by adding the following to the activity tag in the manifest file :<br>
 * {@code android:theme="@android:style/Theme.Holo.Light.Dialog"}</li>
 * </ul>
 *
 */
public class UnderConstructionActivity extends Activity {

	/**
	 * Bundle key used to put/retrieve the module title (if any).
	 */
	public static final String MODULE_TITLE = UnderConstructionActivity.class.getName () + ".MODULE_TITLE";
	
	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate ( Bundle savedInstanceState ) {
		// Superclass method call
		super.onCreate ( savedInstanceState );
		// Set the activity content from a layout resource.
		setContentView ( R.layout.under_construction_activity );
		// Retrieve the label description
		String description = AppResources.getInstance ( this ).getString ( this , R.string.module_na_label );
		// Change the title associated with this activity
		setTitle ( TextUtils.isEmpty ( getIntent ().getStringExtra ( MODULE_TITLE ) ) ? description : getIntent ().getStringExtra ( MODULE_TITLE ) );
		// Display the description label
		( (TextView) findViewById ( R.id.label_description ) ).setText ( description );
	}
	
}