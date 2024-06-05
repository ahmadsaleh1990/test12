/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.ShareOfShelf;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Text Watcher implemented to update the share of shelf notes.
 * 
 * @author Elias
 *
 */
public class NotesWatcher implements TextWatcher {
	
	/**
	 * {@link me.SyncWise.Android.Modules.ShareOfShelf.Tracker Tracker} object reference to update.
	 */
	private final Tracker tracker;
	
	/**
	 * Constructor.
	 * 
	 * @param tracker	{@link me.SyncWise.Android.Modules.SalesOrder.Order Order} object reference to update.
	 */
	public NotesWatcher ( final Tracker tracker ) {
		// Initialize variables
		this.tracker = tracker;
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
	public void onTextChanged ( CharSequence s , int start , int before , int count ) {
		// Do nothing
	}

	/*
	 * Method called to notify before the text is changed.
	 * 
	 * @see android.text.TextWatcher#afterTextChanged(Editable)
	 */
	@Override
	public void afterTextChanged ( Editable s ) {
		// Check if the tracker is valid
		if ( tracker == null )
			// Invalid order, do nothing
			return;
		
		// Update the notes accordingly
		tracker.setCurrentNotes ( s.toString ().trim () );
	}
	
}