/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Collections;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.DateTime;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.Banks;
import me.SyncWise.Android.Database.CollectionDetails;
import me.SyncWise.Android.Database.CollectionUtils;
import me.SyncWise.Android.Database.Currencies;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

/**
 * A concrete BaseAdapter that is backed by an array of {@link me.SyncWise.Android.Database.CollectionDetails CollectionDetails} objects.
 * 
 * @author Elias
 *
 */
public class PaymentsAdapter extends ArrayAdapter < CollectionDetails > {

	/**
	 * {@link android.view.LayoutInflater} used to instantiate a layout XML file into its corresponding View objects.
	 */
	private final LayoutInflater layoutInflater;
	
	/**
	 * XML Layout (layout) resource id.
	 */
	private final int layout;
	
	/**
	 * Nested class implementation of the view holder pattern used to keep references to children views in order to avoid unnecessary calls to {@link android.view.View#findViewById(int) findViewById(id)} on each row.
	 * 
	 * @author Elias
	 *
	 */
	public static class ViewHolder {
		public CollectionDetails collectionDetail;
		public ImageView icon;
		public TextView data;
	}
	
	/**
	 * List of {@link me.SyncWise.Android.Database.Banks Banks} mapped to their appropriate codes.
	 */
	private final HashMap < String , Banks > banks;
	
	/**
	 * List of {@link me.SyncWise.Android.Database.Currencies Currencies} mapped to their appropriate codes.
	 */
	private final HashMap < String , Currencies > currencies;
	
	/**
	 * A money format.
	 */
	private final DecimalFormat moenyFormat = new DecimalFormat ( "#,##0.00" );
	
	/**
	 * String used to host the amount label.
	 */
	private final String amountLabel;
	
	/**
	 * String used to host the check code label.
	 */
	private final String checkCodeLabel;
	
	/**
	 * String used to host the check date label.
	 */
	private final String checkDateLabel;
	
	/**
	 * String used to host the bank label.
	 */
	private final String bankLabel;
	
	/**
	 * String used to host the new line character
	 */
	private final String newLine = "\n";
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param layout	Integer used to host the layout resource ID.
	 * @param collectionDetails	List of {@link me.SyncWise.Android.Database.CollectionDetails CollectionDetails} objects.
	 */
	public PaymentsAdapter ( Context context , int layout , List < CollectionDetails > collectionDetails , HashMap < String , Currencies > currencies , HashMap < String , Banks > banks ) {
		// Overloaded constructor
		super ( context , layout , collectionDetails );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
		this.currencies = currencies;
		this.banks = banks;
		AppResources appResources = AppResources.getInstance ( context );
		amountLabel = appResources.getString ( context , R.string.payment_amount_label ) + " : ";
		checkCodeLabel = appResources.getString ( context , R.string.check_code_label ) + " : ";
		checkDateLabel = appResources.getString ( context , R.string.check_date_label ) + " : ";
		bankLabel = appResources.getString ( context , R.string.bank_label ) + " : ";
	}
	
	/*
	 * Get a View that displays the data at the specified position in the data set.
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView ( int position , View convertView , ViewGroup parent ) {
		// Declare the row view holder
		ViewHolder viewHolder;
		// Check if an inflated view is provided
		if ( convertView == null ) {
			// A new view must be inflated
			convertView = layoutInflater.inflate ( layout , null );
			// Declare and initialize a view holder
			viewHolder = new ViewHolder ();
			
			// Retrieve a reference to the payment icon
			viewHolder.icon = (ImageView) convertView.findViewById ( R.id.icon_payment );
			// Retrieve a reference to the payment data
			viewHolder.data = (TextView) convertView.findViewById ( R.id.label_payment_data );
			
			// Store the view holder as tag
			convertView.setTag ( viewHolder );
		} // End if
		else
			// An inflated view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) convertView.getTag ();
		
		// Set the current collection detail
		viewHolder.collectionDetail = getItem ( position );
		// Determine the payment type
		if ( viewHolder.collectionDetail.getCollectionDetailType () != null && viewHolder.collectionDetail.getCollectionDetailType () == CollectionUtils.PaymentType.CASH )
			// Display the cash icon
			viewHolder.icon.setBackgroundResource ( R.drawable.cash );
		else if ( viewHolder.collectionDetail.getCollectionDetailType () != null && viewHolder.collectionDetail.getCollectionDetailType () == CollectionUtils.PaymentType.CHECK )
			// Display the check icon
			viewHolder.icon.setBackgroundResource ( R.drawable.check );
		else
			// Remove the icon
			viewHolder.icon.setBackgroundResource ( 0 );
		
		// Build a spannable string out of the payment amount in order to apply various spans
		SpannableString amount = new SpannableString ( amountLabel + moenyFormat.format ( viewHolder.collectionDetail.getCollectionAmount () ) + " " + currencies.get ( viewHolder.collectionDetail.getCurrencyCode () ).getCurrencySymbol () );
		// Apply style span
		amount.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , amountLabel.length () , 0 );
		// Apply color span
		amount.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , amountLabel.length () , 0 );
		
		// Declare and initialize vairables to hold check data
		SpannableString checkCode = null;
		SpannableString checkDate = null;
		SpannableString bank = null;
		
		// Check if the current payment is a check
		if ( viewHolder.collectionDetail.getCollectionDetailType () != null && viewHolder.collectionDetail.getCollectionDetailType () == CollectionUtils.PaymentType.CHECK ) {
			// Build a spannable string out of the payment amount in order to apply various spans
			checkCode = new SpannableString ( checkCodeLabel + viewHolder.collectionDetail.getCheckCode () );
			// Apply style span
			checkCode.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , checkCodeLabel.length () , 0 );
			// Apply color span
			checkCode.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , checkCodeLabel.length () , 0 );
			
			// Format the check date
			String checkDateFormatted = DateTime.getBriefDate ( getContext () , viewHolder.collectionDetail.getCheckDate () ); 
			// Build a spannable string out of the payment amount in order to apply various spans
			checkDate = new SpannableString ( checkDateLabel + checkDateFormatted );
			// Apply style span
			checkDate.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , checkDateLabel.length () , 0 );
			// Apply color span
			checkDate.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , checkDateLabel.length () , 0 );
			
			// Build a spannable string out of the payment amount in order to apply various spans
			bank = new SpannableString ( bankLabel + banks.get ( viewHolder.collectionDetail.getBankCode () ).getBankDescription () );
			// Apply style span
			bank.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , bankLabel.length () , 0 );
			// Apply color span
			bank.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , bankLabel.length () , 0 );
		} // End if
		
		// Concatenate the results
		CharSequence data = amount;
		// Check if the current payment is a check
		if ( viewHolder.collectionDetail.getCollectionDetailType () != null && viewHolder.collectionDetail.getCollectionDetailType () == CollectionUtils.PaymentType.CHECK )
			// Concatenate the results
			data = TextUtils.concat ( data , newLine , checkCode , newLine , checkDate , newLine , bank );
			
		// Display the payment data
		viewHolder.data.setText ( data , BufferType.SPANNABLE );
		
		// Return the view
		return convertView;
	}
	
}