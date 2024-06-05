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

import android.app.Activity;

/**
 * Helper Class used to handle activity animated transitions.
 * 
 * @author Elias
 *
 */
public class ActivityTransition {

	/**
	 * Applies an animated activity transition where the current activity slides out to the top of the screen.
	 * 
	 * @param activity	The activity to apply to the animated transition
	 */
	public static void SlideOutTop ( final Activity activity ) {
		// Check if the activity is valid
		if ( activity != null )
			// Specify an explicit transition animation to perform next
			activity.overridePendingTransition ( 0 , R.anim.activity_slide_out_top );
	}
	
	/**
	 * Applies an animated activity transition where the current activity slides out to the left of the screen.
	 * 
	 * @param activity	The activity to apply to the animated transition
	 */
	public static void SlideOutLeft ( final Activity activity ) {
		// Check if the activity is valid
		if ( activity != null )
			// Specify an explicit transition animation to perform next
			activity.overridePendingTransition ( 0 , R.anim.activity_slide_out_left );
	}
	
	/**
	 * Applies an animated activity transition where the current activity slides out to the right of the screen.
	 * 
	 * @param activity	The activity to apply to the animated transition
	 */
	public static void SlideOutRight ( final Activity activity ) {
		// Check if the activity is valid
		if ( activity != null )
			// Specify an explicit transition animation to perform next
			activity.overridePendingTransition ( 0 , R.anim.activity_slide_out_right );
	}
	
}