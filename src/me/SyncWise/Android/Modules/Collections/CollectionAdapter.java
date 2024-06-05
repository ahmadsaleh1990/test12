/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Collections;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.DateTime;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.ClientDues;
import me.SyncWise.Android.Database.CollectionUtils;
import me.SyncWise.Android.Database.Currencies;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Modules.SalesOrder.Order;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView.BufferType;

/**
* A concrete BaseAdapter that is backed by an array of {@link me.SyncWise.Android.Modules.Collections.CollectionAdapter} objects.
* 
* @author Rabee
*
*/
public class CollectionAdapter  extends ArrayAdapter < ClientDues >{
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
	 * @author ahmad
	 *
	 */
	public static class ViewHolder{
		public ImageView icon;
		public TextView invoiceCode;
		public TextView originalAmount;
		public TextView remainingAmount;
		public TextView issueDate;
		public TextView dueDate;
		public TextView type;
		public CheckBox checkbox_av;
		public ClientDues clientDues;
	}
	
	/**
	 * String used to hold the invoice Code label.
	 */
	protected final String invoiceCodeLabel;
	
	/**
	 * String used to hold the  original amount label.
	 */
	protected final String originalAmountLabel;
	/**
	 * String used to hold the  Remaining amount label.
	 */
	protected final String remainingAmountLabel;
	
	/**
	 * String used to hold the Issue date label.
	 */
	protected final String issueDateLabel;
	/**
	 * String used to hold the Due date label.
	 */
	protected final String dueDateLabel;

	private List<Currencies> currencies;
	
	private HashMap<String, Currencies> _currencies;
	
	public CollectionAdapter (Context context , int layout, List < ClientDues > clientDues ){
		
		super(context, layout, clientDues);
		
		layoutInflater = LayoutInflater.from(context);
		this.layout = layout;
		invoiceCodeLabel = AppResources.getInstance( context ). getString (context , R.string.invoice_code_label);
		originalAmountLabel = AppResources.getInstance( context ).getString( context, R.string.invoice_original_amount_label);
		remainingAmountLabel = AppResources.getInstance( context ).getString( context, R.string.invoice_remaining_amount_label);
		issueDateLabel = AppResources.getInstance( context ).getString(context, R.string.invoice_issue_date_label);
		dueDateLabel = AppResources.getInstance( context ).getString(context, R.string.invoice_due_date_label);
		currencies = DatabaseUtils.getInstance(context).getDaoSession().getCurrenciesDao().loadAll();
		_currencies = new HashMap<String, Currencies>();
		for(Currencies currency : currencies)
			_currencies.put(currency.getCurrencyCode(),currency);
		
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
		
		if ( convertView == null){
			
			convertView = layoutInflater.inflate (layout, parent , false);
			viewHolder = new ViewHolder();
			
			viewHolder.icon = ( ImageView ) convertView.findViewById(R.id.icon_collection_invoice);
			viewHolder.invoiceCode = ( TextView ) convertView.findViewById ( R.id.label_invoice_code);
			viewHolder.originalAmount = ( TextView ) convertView.findViewById( R.id.label_invoice_original_amount);
			viewHolder.remainingAmount = ( TextView ) convertView.findViewById( R.id.label_invoice_remaining_amount);
			viewHolder.issueDate = ( TextView ) convertView.findViewById( R.id.label_invoice_issue_date);
			viewHolder.dueDate = ( TextView ) convertView.findViewById( R.id.label_invoice_due_date);
			viewHolder.type = ( TextView ) convertView.findViewById( R.id.label_invoice_type);
			viewHolder.checkbox_av = (CheckBox) convertView.findViewById(R.id.checkbox_available);
			
			convertView.setTag(viewHolder);
		}
		else
			viewHolder = ( ViewHolder ) convertView.getTag();
		
		// Set the current order
	    viewHolder.clientDues = getItem ( position );
		//final CheckBox checkboxAvailable = viewHolder.checkbox_av;
		viewHolder.checkbox_av.setOnCheckedChangeListener ( null );
		final ClientDues finalClientDues = viewHolder.clientDues;
		viewHolder.checkbox_av.setChecked ( viewHolder.clientDues.availability == null || viewHolder.clientDues.availability == false ? false : true );
	 	viewHolder.checkbox_av.setOnCheckedChangeListener ( new OnCheckedChangeListener () {
			@Override
			public void onCheckedChanged ( CompoundButton buttonView , boolean isChecked ) {
				if ( isChecked ) {
				 
					finalClientDues.availability = true;
				}
				else {
					finalClientDues.availability = false;
				}
			}
		} );
		
		SpannableString invoiceCode = new SpannableString ( invoiceCodeLabel + " : "+ getItem( position ).getInvoiceCode ( ) );
		
		invoiceCode.setSpan(new StyleSpan ( Typeface.BOLD), 0, invoiceCodeLabel.length(), 0);
		
		invoiceCode.setSpan(new ForegroundColorSpan(Color.BLACK), 0, invoiceCodeLabel.length(), 0);
		
		viewHolder.invoiceCode.setText(invoiceCode , BufferType.SPANNABLE);
		
		SpannableString originalAmount = new SpannableString ( originalAmountLabel + " : "+ getItem( position ).getOriginalAmount ( )+" " + _currencies.get(getItem( position ).getCurrencyCode()).getCurrencyName() );
		
		originalAmount.setSpan(new StyleSpan ( Typeface.BOLD), 0, originalAmountLabel.length(), 0);
		
		originalAmount.setSpan(new ForegroundColorSpan(Color.BLACK), 0, originalAmountLabel.length(), 0);
		
		viewHolder.originalAmount.setText(originalAmount , BufferType.SPANNABLE);
		
		SpannableString remainingAmount = new SpannableString ( remainingAmountLabel + " : "+ getItem( position ).getRemainingAmount( ) +" " + _currencies.get(getItem( position ).getCurrencyCode()).getCurrencyName() );
		
		remainingAmount.setSpan(new StyleSpan ( Typeface.BOLD), 0, remainingAmountLabel.length(), 0);
		
		remainingAmount.setSpan(new ForegroundColorSpan(Color.BLACK), 0, remainingAmountLabel.length(), 0);
		
		viewHolder.remainingAmount.setText(remainingAmount , BufferType.SPANNABLE);
		
		
		String issueDateFormatted = DateTime.getBriefDate ( getContext () ,getItem( position ).getIssueDate ( ) );
		SpannableString invoiceIssueDate = new SpannableString ( issueDateLabel + " : "+ issueDateFormatted );
		
		invoiceIssueDate.setSpan(new StyleSpan ( Typeface.BOLD), 0, issueDateLabel.length(), 0);
		
		invoiceIssueDate.setSpan(new ForegroundColorSpan(Color.BLACK), 0, issueDateLabel.length(), 0);
		
		viewHolder.issueDate.setText(invoiceIssueDate , BufferType.SPANNABLE);

		String issuedueDateFormatted = DateTime.getBriefDate ( getContext () ,getItem( position ).getDueDate ( ) );
	
		
		SpannableString invoiceDueDate = new SpannableString ( dueDateLabel + " : "+issuedueDateFormatted );
		
		invoiceDueDate.setSpan(new StyleSpan ( Typeface.BOLD), 0, dueDateLabel.length(), 0);
		
		invoiceDueDate.setSpan(new ForegroundColorSpan(Color.BLACK), 0, dueDateLabel.length(), 0);
		
	 
		viewHolder.dueDate.setText(invoiceDueDate , BufferType.SPANNABLE);
		
		// Check if the type is valid
		if ( getItem( position ).getInvoiceType() != null 
				&& ( getItem( position ).getInvoiceType() == CollectionUtils.DuesType.CREDIT ||  getItem( position ).getInvoiceType () == CollectionUtils.DuesType.DEBIT ) ) {
			// Determine the client dues type
			String clientDuesType =  getItem( position ).getInvoiceType() == CollectionUtils.DuesType.CREDIT ? "Credit" : 
				 getItem( position ).getInvoiceType() == CollectionUtils.DuesType.DEBIT ? "Invoice" : ""; 
			// Build a spannable string out of the client dues type in order to apply various spans
			SpannableString type = new SpannableString ( "Type: " + clientDuesType );
			// Apply style span
			type.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , "Type: ".length () , 0 );
			// Apply color span
			type.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , "Type: ".length () , 0 );
			viewHolder.type.setText(type, BufferType.SPANNABLE);
		
		}
		
		 Calendar calendar = Calendar.getInstance ();
		 	long startDay    	  ;
		 	calendar.set ( Calendar.HOUR_OF_DAY , 0 );
			calendar.set ( Calendar.MINUTE , 0 );
			calendar.set ( Calendar.SECOND , 0 );
			calendar.set ( Calendar.MILLISECOND , 0 );
			 startDay = calendar.getTimeInMillis ();
		if ( getItem(position).getDueDate().getTime() <= startDay  )
		{
			convertView.setBackgroundColor(Color.rgb(255, 228, 225));
		}
		else
		{
			convertView.setBackgroundColor(Color.rgb(255, 255, 255));
		}
		return convertView;
	}

}
