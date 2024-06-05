/**
 * Copyright 2014 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.SalesRFR;

import java.lang.ref.WeakReference;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

/**
 * Text Watcher implemented to update the order quantities.
 * 
 * @author Elias
 *
 */
public class OrderRFRWatcher implements TextWatcher {

	/**
	 * Helper Class used to manage the text watcher types in order to properly update the provided order.<br>
	 * The main purpose of this class is to maintain a unique identifier.
	 * 
	 * @author Elias
	 *
	 */
	protected class Type {
		public static final int QUANTITY_BIG = 0;
		public static final int QUANTITY_MEDIUM = 1;
		public static final int QUANTITY_SMALL = 2;
	}
	
	/**
	 * Weak reference to the {@link me.SyncWise.Android.Modules.SalesOrder.SalesOrderDetailsActivity SalesOrderDetailsActivity} screen.
	 */
	private WeakReference < SalesRFRDetailsActivity > activity;
	
	/**
	 * Weak reference to the {@link android.view.View View} holding the order in the {@link me.SyncWise.Android.Modules.SalesOrder.SalesOrderDetailsAdapter SalesOrderDetailsAdapter}.
	 */
	private WeakReference < View > orderView;
	
	/**
	 * {@link me.SyncWise.Android.Modules.SalesOrder.Order Order} object reference to update.
	 */
	private final OrderRFR order;
	
	/**
	 * Integer holding the text watcher type.
	 */
	private final int type;
	
	/**
	 * Constructor.
	 * 
	 * @param activity	Reference to the {@link me.SyncWise.Android.Modules.SalesOrder.SalesOrderDetailsActivity SalesOrderDetailsActivity} screen.
	 * @param orderView	Weak reference to the {@link android.view.View View} holding the order in the {@link me.SyncWise.Android.Modules.SalesOrder.SalesOrderDetailsAdapter SalesOrderDetailsAdapter}.
	 * @param order	{@link me.SyncWise.Android.Modules.SalesOrder.Order Order} object reference to update.
	 * @param type	Integer holding the text watcher type.
	 */
	public OrderRFRWatcher ( final SalesRFRDetailsActivity activity , final View orderView , final OrderRFR order , final int type ) {
		// Initialize variables
		this.activity = new WeakReference < SalesRFRDetailsActivity > ( activity );
		this.orderView = new WeakReference < View > ( orderView );
		this.order = order;
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
		// Check if the order is valid
		if ( order == null )
			// Invalid order, do nothing
			return;
		if ( SalesRFRDetailsActivity.selectedReasonCode == null ||
				( SalesRFRDetailsActivity.selectedView != null && orderView != null && ! orderView.get ().equals ( SalesRFRDetailsActivity.selectedView ) ) )
			return;

		// Determine the watcher type and update the order accordingly
		switch ( type ) {
		case Type.QUANTITY_BIG:
			order.setQuantityBig ( SalesRFRDetailsActivity.selectedReasonCode , (int) getNumber ( s.toString () ) );
			break;
		case Type.QUANTITY_MEDIUM:
			order.setQuantityMedium ( SalesRFRDetailsActivity.selectedReasonCode , (int) getNumber ( s.toString () ) );
			break;
		case Type.QUANTITY_SMALL:
			order.setQuantitySmall ( SalesRFRDetailsActivity.selectedReasonCode , (int) getNumber ( s.toString () ) );
			break;
		default:
			// Do nothing
			return;
		} // End switch

		try {
			// Determine if the activity reference is valid
			if ( activity != null && activity.get () != null ) {
				// Set the order being modified
				activity.get ().setOrderModification ( order );
				// Invoice the view selection handler
				activity.get ().selectedView ( orderView.get () , false , false );
				// Update order
				activity.get ().updateOrder ( orderView.get () );
			} // End if
			// Determine if the order view reference is valid
			if ( orderView != null && orderView.get () != null && activity != null && activity.get () != null )
				// Refresh the order view being modified
				SalesRFRDetailsAdapter.refreshOrderView ( activity.get () , orderView.get () , activity.get ().getUsePicker () , activity.get ().getMoneyFormat () , 
						activity.get ().getCurrency () , activity.get ().getCodeLabel () , activity.get ().getTotalLabel () , activity.get ().getReasonLabel () , 
						activity.get ().getExpiryDateLabel () , activity.get ().getNewLine () , activity.get ().getBrownColor (),activity.get ().getItemBarCodeLabel() );
		} catch ( Exception exception ) {
			exception.getLocalizedMessage();
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