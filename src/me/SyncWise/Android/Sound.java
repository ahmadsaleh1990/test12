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
import android.media.MediaPlayer;

/**
 * Helper Class used to handle volume, ringer modes and audio routing.
 * 
 * @author Elias
 *
 */
public class Sound {
	
	/**
	 * Plays the sound file using the provided media player object.<br>
	 * If the media player is currently playing, it is paused and rewinded, then started.<br>
	 * <b>Note :</b> The sound file will not be played unless the ringer mode is in <b>NORMAL</b> <em>(not SILENT or VIBRATE)</em>
	 * 
	 * @param context	The application context.
	 * @param mediaPlayer	MediaPlayer object used to control playback of audio/video files and streams.
	 * @return	Boolean indicating if the sound is being played or not.
	 */
	public static boolean playSound ( final Context context , final MediaPlayer mediaPlayer ) {
		// Check if any of the arguments are invalid
		if ( context == null || mediaPlayer == null )
			// At least on invalid argument
			return false;
		try {
			// Return handle to the the audio manager system-level service
			final AudioManager audioManager = (AudioManager) context.getSystemService ( Context.AUDIO_SERVICE );
			// Check if the phone is in normal ringer mode
			if ( audioManager.getRingerMode () == AudioManager.RINGER_MODE_NORMAL ) {
				// Check if the media player is currently playing audio
				if ( mediaPlayer.isPlaying () ) {
					// Pauses playback
					mediaPlayer.pause ();
					// Seek to start
					mediaPlayer.seekTo ( 0 );
				} // End if
				// Starts or resumes playback
				mediaPlayer.start ();
				// Indicate that the sound file is being played
				return true;
			} // End if
		} catch ( Exception exception ) {
			// Invalid media player state
			return false;
		} // End of try-catch block
		return false;
	}
	
}