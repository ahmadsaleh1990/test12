/**
 * Copyright 2014 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.ShareOfShelf;

import java.lang.ref.WeakReference;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

/**
 * Text Watcher implemented to update the share of shelf tracker.
 * 
 * @author Elias
 *
 */
public class SOSBrandWatcher implements TextWatcher {

	/**
	 * Helper Class used to manage the text watcher types in order to properly update the provided tracker.<br>
	 * The main purpose of this class is to maintain a unique identifier.
	 * 
	 * @author Elias
	 *
	 */
	protected class Type {
		public static final int SHARE_OF_SHELF = 0;
		public static final int COMPETITOR_SHARE_OF_SHELF = 1;
		public static final int CATEGORY = 2;
	}
	
	/**
	 * Weak reference to the {@link me.SyncWise.Android.Modules.ShareOfShelf.SOSTrackerDetailActivity TestActivity} screen.
	 */
	private WeakReference < SOSBrandDetailActivity > activity;
	
	/**
	 * Weak reference to the {@link android.view.View View} holding the order in the {@link me.SyncWise.Android.Modules.SalesOrder.SalesOrderDetailsAdapter SalesOrderDetailsAdapter}.
	 */
	private WeakReference < View > trackerView;
	
	/**
	 * {@link me.SyncWise.Android.Modules.ShareOfShelf.Tracker Tracker} object reference to update.
	 */
	private final SOSTracker tracker;
	
	/**
	 * Integer holding the text watcher type.
	 */
	private final int type;
	
	/**
	 * Constructor.
	 * 
	 * @param activity	Reference to the {@link me.SyncWise.Android.Modules.ShareOfShelf.SOSTrackerDetailActivity TestActivity} screen.
	 * @param trackerView	Weak reference to the {@link android.view.View View} holding the order in the {@link me.SyncWise.Android.Modules.ShareOfShelf.ShareOfShelfTrackerDetailsAdapter ShareOfShelfTrackerDetailsAdapter}.
	 * @param tracker	{@link me.SyncWise.Android.Modules.SalesOrder.Order Order} object reference to update.
	 * @param type	Integer holding the text watcher type.
	 */
	public SOSBrandWatcher ( final SOSBrandDetailActivity activity , final View orderView , final SOSTracker tracker , final int type ) {
		// Initialize variables
		this.activity = new WeakReference < SOSBrandDetailActivity > ( activity );
		this.trackerView = new WeakReference < View > ( orderView );
		this.tracker = tracker;
		this.type = type;
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
		
		// Determine the watcher type and update the order accordingly
		switch ( type ) {
		case Type.SHARE_OF_SHELF:
			tracker.setCurrentShareOfShelf ( getNumber ( s.toString () ) );
			break;
	 
		default:
			// Do nothing
			return;
		} // End switch
		
		try {

			// Determine if the references are valid
			if ( trackerView != null && trackerView.get () != null && activity != null && activity.get () != null ) {
				// Refresh the order view being modified
				SOSBrandDetailsAdapter.refreshOrderView ( activity.get () , trackerView.get ()  );
				// Validate the new tracker values
			//	activity.get ().validateTracker ( trackerView.get () );
			} // End if
		} catch ( Exception exception ) {
			// Do nothing
		} // End of try-catch block
	}
	
	/**
	 * Properly cast the provided text to double and returns its value.
	 * 
	 * @param text	String to cast to double.
	 * @return	Double hosting the real number provided in the string.
	 */
	private double getNumber ( String text ) {
		try {
			// Cast to double
			return Double.parseDouble ( text );
		} catch ( Exception exception ) {
			// Invalid text
			return 0;
		} // End of try-catch block
	}

}