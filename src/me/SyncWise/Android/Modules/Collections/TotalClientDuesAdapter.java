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
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.CollectionUtils;
import me.SyncWise.Android.Database.Currencies;
import me.SyncWise.Android.Database.TotalClientDues;
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
 * A concrete BaseAdapter that is backed by an array of {@link me.SyncWise.Android.Database.ClientDues ClientDues} objects.
 * 
 * @author Elias
 *
 */
public class TotalClientDuesAdapter extends ArrayAdapter < TotalClientDues > {

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
		public TotalClientDues totalClientDues;
		public ImageView icon;
		public TextView invoice;
		public TextView type;
		public TextView originalAmount;
		public TextView remainingAmount;
	}
	
	/**
	 * List of {@link me.SyncWise.Android.Database.Currencies Currencies} mapped to their appropriate codes.
	 */
	private final HashMap < String , Currencies > currencies;
	
	/**
	 * A money format.
	 */
	private final DecimalFormat moenyFormat = new DecimalFormat ( "#,##0.00" );
	
	/**
	 * String used to host the invoice code label.
	 */
	private final String invoiceCodeLabel;
	
	/**
	 * String used to host the type label.
	 */
	private final String typeLabel;
	
	/**
	 * String used to host the invoice label.
	 */
	private final String invoiceTypeLabel;
	
	/**
	 * String used to host the credit label.
	 */
	private final String creditTypeLabel;
	
	/**
	 * String used to host the original amount label.
	 */
	private final String originalAmountLabel;
	
	/**
	 * String used to host the remaining amount label.
	 */
	private final String remainingAmountLabel;
	
	/**
	 * String used to host the overdue amount label.
	 */
	private final String overdueAmountLabel;
	
	/**
	 * String used to host the over due label.
	 */
	private final String naLabel;
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param layout	Integer used to host the layout resource ID.
	 * @param totalClientDues	List of {@link me.SyncWise.Android.Database.TotalClientDues} TotalClientDues objects.
	 */
	public TotalClientDuesAdapter ( Context context , int layout , List < TotalClientDues > totalClientDues , HashMap < String , Currencies > currencies ) {
		// Overloaded constructor
		super ( context , layout , totalClientDues );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
		this.currencies = currencies;
		AppResources appResources = AppResources.getInstance ( context );
		invoiceCodeLabel = appResources.getString ( context , R.string.total_company_dues_label ) + " : ";
		typeLabel = appResources.getString ( context , R.string.type_label ) + " : ";
		invoiceTypeLabel = appResources.getString ( context , R.string.invoice_label );
		creditTypeLabel = appResources.getString ( context , R.string.credit_label );
		originalAmountLabel = appResources.getString ( context , R.string.collection_original_amount_label ) + " : ";
		remainingAmountLabel = appResources.getString ( context , R.string.collection_remaining_amount_label ) + " : ";
		overdueAmountLabel = appResources.getString ( context , R.string.collection_overdue_amount_label ) + " : ";
		naLabel = appResources.getString ( context , R.string.not_available_abbreviation );
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
			// Retrieve a reference to the item icon
			viewHolder.icon = (ImageView) convertView.findViewById ( R.id.icon_client_dues );
			// Retrieve a reference to the item code
			viewHolder.invoice = (TextView) convertView.findViewById ( R.id.label_invoice_code );
			// Retrieve a reference to type label
			viewHolder.type = (TextView) convertView.findViewById ( R.id.label_type );
			// Retrieve a reference to original amount
			viewHolder.originalAmount = (TextView) convertView.findViewById ( R.id.label_original_amount );
			// Retrieve a reference to remaining amount
			viewHolder.remainingAmount = (TextView) convertView.findViewById ( R.id.label_remaining_amount );
			// Store the view holder as tag
			convertView.setTag ( viewHolder );
		} // End if
		else
			// An inflated view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) convertView.getTag ();
		
		// Set the current total client due
		viewHolder.totalClientDues = getItem ( position );
		
		// Build a spannable string out of the invoice code in order to apply various spans
		SpannableString code = new SpannableString ( invoiceCodeLabel + viewHolder.totalClientDues.getCompanyName () );
		// Apply style span
		code.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , invoiceCodeLabel.length () , 0 );
		// Apply color span
		code.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , invoiceCodeLabel.length () , 0 );
		// Display the invoice code
		viewHolder.invoice.setText ( code , BufferType.SPANNABLE );
		
		// Check if the type is valid
		if ( viewHolder.totalClientDues.getInvoiceType () == CollectionUtils.DuesType.CREDIT || viewHolder.totalClientDues.getInvoiceType () == CollectionUtils.DuesType.DEBIT ) {
			// Determine the client dues type
			String clientDuesType = viewHolder.totalClientDues.getInvoiceType () == CollectionUtils.DuesType.CREDIT ? creditTypeLabel : 
				viewHolder.totalClientDues.getInvoiceType () == CollectionUtils.DuesType.DEBIT ? invoiceTypeLabel : ""; 
			// Build a spannable string out of the client dues type in order to apply various spans
			SpannableString type = new SpannableString ( typeLabel + clientDuesType );
			// Apply style span
			type.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , typeLabel.length () , 0 );
			// Apply color span
			type.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , typeLabel.length () , 0 );
			// Display the client dues type
			viewHolder.type.setText ( type , BufferType.SPANNABLE );
		} // End if
		else
			// Invalid type, clear the type label
			viewHolder.type.setText ( "" );
		
		// Build a spannable string out of the original amount in order to apply various spans
		SpannableString originalAmount = new SpannableString ( originalAmountLabel + ( currencies.containsKey ( viewHolder.totalClientDues.getCurrencyCode () ) ? 
				moenyFormat.format ( viewHolder.totalClientDues.getOriginalAmount () ) + " " + currencies.get ( viewHolder.totalClientDues.getCurrencyCode () ).getCurrencySymbol () : naLabel ) );
		// Apply style span
		originalAmount.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , originalAmountLabel.length () , 0 );
		// Apply color span
		originalAmount.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , originalAmountLabel.length () , 0 );
		// Display the invoice code
		viewHolder.originalAmount.setText ( originalAmount , BufferType.SPANNABLE );
		
		// Build a spannable string out of the remaining amount in order to apply various spans
		SpannableString remainingAmount = new SpannableString ( remainingAmountLabel + ( currencies.containsKey ( viewHolder.totalClientDues.getCurrencyCode () ) ? 
				moenyFormat.format ( viewHolder.totalClientDues.getRemainingAmount () ) + " " + currencies.get ( viewHolder.totalClientDues.getCurrencyCode () ).getCurrencySymbol () : naLabel ) );
		// Apply style span
		remainingAmount.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , remainingAmountLabel.length () , 0 );
		// Apply color span
		remainingAmount.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , remainingAmountLabel.length () , 0 );
		
		// Build a spannable string out of the remaining amount in order to apply various spans
		SpannableString overdueAmount = new SpannableString ( overdueAmountLabel + ( currencies.containsKey ( viewHolder.totalClientDues.getCurrencyCode () ) ? 
				moenyFormat.format ( viewHolder.totalClientDues.getOverdueAmount () ) + " " + currencies.get ( viewHolder.totalClientDues.getCurrencyCode () ).getCurrencySymbol () : naLabel ) );
		// Apply style span
		overdueAmount.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , overdueAmountLabel.length () , 0 );
		// Apply color span
		overdueAmount.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , overdueAmountLabel.length () , 0 );
		
		// Display the remaining amount
		viewHolder.remainingAmount.setText ( TextUtils.concat ( remainingAmount , "\n" , overdueAmount ) , BufferType.SPANNABLE );
		
		// Return the view
		return convertView;
	}
	
	/*
	 * Returns true if the item at the specified position is not a separator. (A separator is a non-selectable, non-clickable item).
	 *
	 * @see android.widget.BaseAdapter#isEnabled(int)
	 */
	@Override
	public boolean isEnabled ( int position ) {
		return false;
	}
	
}