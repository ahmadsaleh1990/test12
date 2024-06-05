/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Threads;

/**
 * Class used to manage thread signals / communication between each other / activities.<br>
 * This class is thread safe.
 * 
 * @author Elias
 *
 */
public class SimpleThreadSignal {

	/**
	 * Boolean used as flag.
	 */
	private boolean done;
	
	/**
	 * Sets the {@link #done} flag.
	 */
	public void setDone () {
		// Set flag
		done = true;
	}
	
	/**
	 * Returns the {@link #done} flag.
	 * 
	 * @return	Boolean indicating the signal state.
	 */
	public boolean isDone () {
		// Return the flag
		return done;
	}
	
	/**
	 * Sets the {@link #done} flag and notifies any sleeping thread.
	 */
	public synchronized void setDoneAndNotify () {
		// Set flag
		done = true;
		// Notify waiting thread (if any)
		this.notify ();
	}
	
	/**
	 * Makes the calling thread wait until another thread notifies it.
	 */
	public synchronized void waitUntilDone () {
		// Keep waiting until setDone is called
		while ( ! done ) {
			try {
				// Wait thread
				this.wait ();
			} catch ( InterruptedException exception ) {
				// Thread interrupted during sleep
			} // End of try-catch block
		} // End while loop
		// Clear flag
		done = false;
	}
	
}