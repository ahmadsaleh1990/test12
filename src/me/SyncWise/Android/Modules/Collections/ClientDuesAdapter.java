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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.DateTime;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.ClientDues;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

/**
 * A concrete BaseAdapter that is backed by an array of {@link me.SyncWise.Android.Database.ClientDues ClientDues} objects.
 * 
 * @author Elias
 *
 */
public class ClientDuesAdapter extends ArrayAdapter < ClientDues > implements OnClickListener {

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
		public int position;
		public ClientDues clientDues;
		public ImageView icon;
		public LinearLayout actionsLayout;
		public ImageView selectAll;
		public ImageView edit;
		public ImageView clear;
		public TextView invoice;
		public TextView type;
		public TextView overdue;
		public TextView originalAmount;
		public TextView remainingAmount;
		public TextView issueDate;
		public TextView dueDate;
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
	 * Boolean indicating if partial amounts are allowed.
	 */
	private final boolean allowPartialAmounts;
	
	/**
	 * Date object used to host today's date.
	 */
	private final Date today;
	
	/**
	 * String used to host the new line character.
	 */
	private final String newLine = "\n";
	
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
	 * String used to host the paid amount label.
	 */
	private final String paidAmountLabel;
	
	/**
	 * String used to host the original amount label.
	 */
	private final String originalAmountLabel;
	
	/**
	 * String used to host the remaining amount label.
	 */
	private final String remainingAmountLabel;
	
	/**
	 * String used to host the issue date label.
	 */
	private final String issueDateLabel;
	
	/**
	 * String used to host the due date label.
	 */
	private final String dueDateLabel;
	
	/**
	 * String used to host the over due label.
	 */
	private final String overdueLabel;
	
	/**
	 * String used to host the over due label.
	 */
	private final String naLabel;
	
	/**
	 * Integer used to host the misty rose color.
	 */
	private final Integer mistyRoseColor;
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param layout	Integer used to host the layout resource ID.
	 * @param clientDues	List of {@link me.SyncWise.Android.Database.ClientDues ClientDues} objects.
	 * @param allowPartialAmounts	Boolean indicating if partial amounts are allowed.
	 */
	public ClientDuesAdapter ( Context context , int layout , List < ClientDues > clientDues , HashMap < String , Currencies > currencies , final boolean allowPartialAmounts ) {
		// Overloaded constructor
		super ( context , layout , clientDues );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
		this.currencies = currencies;
		AppResources appResources = AppResources.getInstance ( context );
		this.allowPartialAmounts = allowPartialAmounts;
		today = Calendar.getInstance ().getTime ();
		invoiceCodeLabel = appResources.getString ( context , R.string.invoice_code_label ) + " : ";
		typeLabel = appResources.getString ( context , R.string.type_label ) + " : ";
		invoiceTypeLabel = appResources.getString ( context , R.string.invoice_label );
		creditTypeLabel = appResources.getString ( context , R.string.credit_label );
		paidAmountLabel = appResources.getString ( context , R.string.collection_paid_amount_label ) + " : ";
		originalAmountLabel = appResources.getString ( context , R.string.collection_original_amount_label ) + " : ";
		remainingAmountLabel = appResources.getString ( context , R.string.collection_remaining_amount_label ) + " : ";
		issueDateLabel = appResources.getString ( context , R.string.issue_date_label ) + " : ";
		dueDateLabel = appResources.getString ( context , R.string.due_date_label ) + " : ";
		overdueLabel = appResources.getString ( context , R.string.overdue_label );
		mistyRoseColor = context.getResources ().getColor ( R.color.MistyRose );
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
			// Retrieve a reference to the actions layout
			viewHolder.actionsLayout = (LinearLayout) convertView.findViewById ( R.id.layout_actions );
			// Retrieve a reference to the select all icon
			viewHolder.selectAll = (ImageView) convertView.findViewById ( R.id.icon_select_all );
			// Retrieve a reference to the edit icon
			viewHolder.edit = (ImageView) convertView.findViewById ( R.id.icon_edit );
			// Retrieve a reference to the clear icon
			viewHolder.clear = (ImageView) convertView.findViewById ( R.id.icon_clear );
			// Retrieve a reference to the item code
			viewHolder.invoice = (TextView) convertView.findViewById ( R.id.label_invoice_code );
			// Retrieve a reference to type label
			viewHolder.type = (TextView) convertView.findViewById ( R.id.label_type );
			// Retrieve a reference to over due label
			viewHolder.overdue = (TextView) convertView.findViewById ( R.id.label_overdue );
			// Retrieve a reference to original amount
			viewHolder.originalAmount = (TextView) convertView.findViewById ( R.id.label_original_amount );
			// Retrieve a reference to remaining amount
			viewHolder.remainingAmount = (TextView) convertView.findViewById ( R.id.label_remaining_amount );
			// Retrieve a reference to issue date label
			viewHolder.issueDate = (TextView) convertView.findViewById ( R.id.label_issue_date );
			// Retrieve a reference to the due date label
			viewHolder.dueDate = (TextView) convertView.findViewById ( R.id.label_due_date );
			// Display the over due label
			viewHolder.overdue.setText ( overdueLabel );
			
			// Display action layout
			viewHolder.actionsLayout.setVisibility ( View.VISIBLE );
			// Set the action listeners
			viewHolder.selectAll.setOnClickListener ( this );
			viewHolder.edit.setOnClickListener ( this );
			viewHolder.clear.setOnClickListener ( this );
			
			// Enable the edit icon accordingly
			viewHolder.edit.setEnabled ( allowPartialAmounts ? true : false );
			viewHolder.edit.setVisibility ( allowPartialAmounts ? View.VISIBLE : View.INVISIBLE );
			
			// Store the view holder as tag
			convertView.setTag ( viewHolder );
		} // End if
		else
			// An inflated view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) convertView.getTag ();
		
		// Set the position
		viewHolder.position = position;
		// Set the current client due
		viewHolder.clientDues = getItem ( position );
		
		// Build a spannable string out of the invoice code in order to apply various spans
		SpannableString code = new SpannableString ( invoiceCodeLabel + viewHolder.clientDues.getInvoiceCode () );
		// Apply style span
		code.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , invoiceCodeLabel.length () , 0 );
		// Apply color span
		code.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , invoiceCodeLabel.length () , 0 );
		// Display the invoice code
		viewHolder.invoice.setText ( code , BufferType.SPANNABLE );
		
		// Check if the type is valid
		if ( viewHolder.clientDues.getInvoiceType () != null 
				&& ( viewHolder.clientDues.getInvoiceType () == CollectionUtils.DuesType.CREDIT || viewHolder.clientDues.getInvoiceType () == CollectionUtils.DuesType.DEBIT ) ) {
			// Determine the client dues type
			String clientDuesType = viewHolder.clientDues.getInvoiceType () == CollectionUtils.DuesType.CREDIT ? creditTypeLabel : 
				viewHolder.clientDues.getInvoiceType () == CollectionUtils.DuesType.DEBIT ? invoiceTypeLabel : ""; 
			// Build a spannable string out of the client dues type in order to apply various spans
			SpannableString type = new SpannableString ( typeLabel + clientDuesType );
			// Apply style span
			type.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , typeLabel.length () , 0 );
			// Apply color span
			type.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , typeLabel.length () , 0 );
			// Build a spannable string out of the paid amount in order to apply various spans
			SpannableString paidAmount = new SpannableString ( paidAmountLabel + ( currencies.containsKey ( viewHolder.clientDues.getCurrencyCode () ) ? 
					moenyFormat.format ( viewHolder.clientDues.getPaidAmount () ) + " " + currencies.get ( viewHolder.clientDues.getCurrencyCode () ).getCurrencySymbol () : naLabel ) );
			// Apply style span
			paidAmount.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , paidAmountLabel.length () , 0 );
			// Apply color span
			paidAmount.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , paidAmountLabel.length () , 0 );
			// Display the client dues type
			viewHolder.type.setText ( TextUtils.concat ( type , newLine , paidAmount ) , BufferType.SPANNABLE );
		} // End if
		else
			// Invalid type, clear the type label
			viewHolder.type.setText ( "" );
		
		// Build a spannable string out of the original amount in order to apply various spans
		SpannableString originalAmount = new SpannableString ( originalAmountLabel + ( currencies.containsKey ( viewHolder.clientDues.getCurrencyCode () ) ? 
				moenyFormat.format ( viewHolder.clientDues.getOriginalAmount () ) + " " + currencies.get ( viewHolder.clientDues.getCurrencyCode () ).getCurrencySymbol () : naLabel ) );
		// Apply style span
		originalAmount.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , originalAmountLabel.length () , 0 );
		// Apply color span
		originalAmount.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , originalAmountLabel.length () , 0 );
		// Display the invoice code
		viewHolder.originalAmount.setText ( originalAmount , BufferType.SPANNABLE );
		
		// Build a spannable string out of the remaining amount in order to apply various spans
		SpannableString remainingAmount = new SpannableString ( remainingAmountLabel + ( currencies.containsKey ( viewHolder.clientDues.getCurrencyCode () ) ? 
				moenyFormat.format ( viewHolder.clientDues.getRemainingAmount () ) + " " + currencies.get ( viewHolder.clientDues.getCurrencyCode () ).getCurrencySymbol () : naLabel ) );
		// Apply style span
		remainingAmount.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , remainingAmountLabel.length () , 0 );
		// Apply color span
		remainingAmount.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , remainingAmountLabel.length () , 0 );
		// Display the remaining amount
		viewHolder.remainingAmount.setText ( remainingAmount , BufferType.SPANNABLE );
		
		// Format the issue date
		String issueDateFormatted = DateTime.getBriefDate ( getContext () , viewHolder.clientDues.getIssueDate () );
		// Build a spannable string out of the issue date in order to apply various spans
		SpannableString issueDate = new SpannableString ( issueDateLabel + issueDateFormatted );
		// Apply style span
		issueDate.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , issueDateLabel.length () , 0 );
		// Apply color span
		issueDate.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , issueDateLabel.length () , 0 );
		// Display the remaining amount
		viewHolder.issueDate.setText ( issueDate , BufferType.SPANNABLE );
		
		// Format the due date
		String dueDateFormatted = DateTime.getBriefDate ( getContext () , viewHolder.clientDues.getDueDate () );
		// Build a spannable string out of the due date in order to apply various spans
		SpannableString dueDate = new SpannableString ( dueDateLabel + dueDateFormatted );
		// Apply style span
		dueDate.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , dueDateLabel.length () , 0 );
		// Apply color span
		dueDate.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , dueDateLabel.length () , 0 );
		// Display the remaining amount
		viewHolder.dueDate.setText ( dueDate , BufferType.SPANNABLE );
		
		// Determine if the current client due is over due
		if ( viewHolder.clientDues.getDueDate ().before ( today ) && viewHolder.clientDues.getRemainingAmount () > 0 ) {
			// Modify the raw background to indicate that the current client due is over due
			convertView.setBackgroundColor ( mistyRoseColor );
			// Display the over due label
			viewHolder.overdue.setVisibility ( View.VISIBLE );
		} // End if
		else {
			// Otherwise remove the raw background
			convertView.setBackgroundColor ( 0 );
			// Hide the over due label
			viewHolder.overdue.setVisibility ( View.GONE );
		} // End if
		
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

	/*
	 * Called when a view has been clicked.
	 *
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick ( View view ) {
		// Retrieve the view holder
		ViewHolder viewHolder = (ViewHolder) ( (View) view.getParent ().getParent ().getParent ().getParent () ).getTag ();
		// Check if the type is valid
		if ( viewHolder.clientDues.getInvoiceType () != null 
				&& ( viewHolder.clientDues.getInvoiceType () == CollectionUtils.DuesType.CREDIT || viewHolder.clientDues.getInvoiceType () == CollectionUtils.DuesType.DEBIT ) ) {
			// Determine if the action is select all
			if ( view.getId () == R.id.icon_select_all ) {
				// Select all the remaining quantity
				viewHolder.clientDues.setPaidAmount ( viewHolder.clientDues.getRemainingAmount () );
				// Refresh adapter
				notifyDataSetChanged ();
				// Enable the add buttons accordingly
				( (CollectionDetailsActivity) getContext () ).refreshAddButtonsState ();
				// Update payment info
				( (CollectionDetailsActivity) getContext () ).updatePaymentInfo ();
			} // End if
			// Determine if the action is clear
			else if ( view.getId () == R.id.icon_clear ) {
				// Select all the remaining quantity
				viewHolder.clientDues.setPaidAmount ( 0 );
				// Refresh adapter
				notifyDataSetChanged ();
				// Enable the add buttons accordingly
				( (CollectionDetailsActivity) getContext () ).refreshAddButtonsState ();
				// Update payment info
				( (CollectionDetailsActivity) getContext () ).updatePaymentInfo ();
			} // End else if
			// Determine if the action is edit
			else if ( view.getId () == R.id.icon_edit ) {
				// Perform edit on activity
				( (CollectionDetailsActivity) getContext () ).editPaidAmount ( viewHolder.position );
			} // End else if
		} // End if
	}
	
}