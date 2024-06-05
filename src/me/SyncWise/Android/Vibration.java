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

import android.content.Context;
import android.media.AudioManager;
import android.os.Vibrator;

/**
 * Helper Class used to handle hardware controls related to vibration.
 * 
 * @author Elias
 * @sw.todo <b>Note :</b><br>This class requires the caller to hold the permission VIBRATE.
 *
 */
public class Vibration {

	/**
	 * Gets the default vibration duration used if no duration is specified.
	 * 	
	 * @param context	The application context.
	 * @return	Integer holding the default vibration duration in milliseconds.
	 */
	public static int getDefaultDuration ( final Context context ) {
		// Return the default vibration duration
		return context.getResources ().getInteger ( R.integer.default_vibration_duration );
	}
	
	/**
	 * Vibrates constantly for the default period of time.<br>
	 * The default vibration period is stored as an integer resource defined in XML.
	 * 
	 * @param context	The application context.
	 */
	public static void vibrate ( final Context context ) {
		// Overloaded method call
		vibrate ( context , context.getResources ().getInteger ( R.integer.default_vibration_duration )  );
	}
	
	/**
	 * Vibrates constantly for the specified period of time.<br>
	 * <b>Note :</b> The device will not vibrate unless the ringer mode is in <b>NORMAL</b> or <b>VIBRATE</b> <em>(not SILENT)</em>
	 * 
	 * @param context	The application context.
	 * @param milliseconds	Long holding the duration of the vibration in milliseconds.
	 * @sw.todo <b>Note :</b><br>This method requires the caller to hold the permission VIBRATE.
	 */
	public static void vibrate ( final Context context , final long milliseconds ) {
		// Return handle to the the audio manager system-level service
		final AudioManager audioManager = (AudioManager) context.getSystemService ( Context.AUDIO_SERVICE );
		// Check if the phone is in normal ringer mode
		if ( audioManager.getRingerMode () != AudioManager.RINGER_MODE_SILENT ) {
			// Retrieve a reference of the vibration hardware control
			Vibrator vibrator = (Vibrator) context.getSystemService ( Context.VIBRATOR_SERVICE );
			// Apply a device vibration for the indicated period (milliseconds)
			vibrator.vibrate ( milliseconds );
		} // End if
	}
	
}