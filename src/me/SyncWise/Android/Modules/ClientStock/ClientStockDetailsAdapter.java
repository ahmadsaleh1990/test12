/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.ClientStock;

import java.text.DecimalFormat;
import java.util.List;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.DateTime;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.ItemsUtils;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

/**
 * A concrete BaseAdapter that is backed by an array of {@link me.SyncWise.Android.Modules.SalesOrder.Order Order} objects.
 * 
 * @author Elias
 *
 */
public class ClientStockDetailsAdapter extends ArrayAdapter < Stock > {

	/**
	 * {@link android.view.LayoutInflater} used to instantiate a layout XML file into its corresponding View objects.
	 */
	private final LayoutInflater layoutInflater;
	
	/**
	 * XML Layout (layout) resource id.
	 */
	private final int layout;
	private boolean usePicker;
	/**
	 * Nested class implementation of the view holder pattern used to keep references to children views in order to avoid unnecessary calls to {@link android.view.View#findViewById(int) findViewById(id)} on each row.
	 * 
	 * @author Elias
	 *
	 */
	public static class ViewHolder {
		public ImageView icon;
		public TextView code;
		public TextView description;
		public ImageView arrow;
		public TextView tax;
		public LinearLayout salesOrder;
		public LinearLayout big;
		public LinearLayout medium;
		public LinearLayout small;
		public TextView quantityBig;
		public TextView quantityMedium;
		public TextView quantitySmall;
		public TextView priceBig;
		public TextView priceMedium;
		public TextView priceSmall;
		public TextView shelfPriceSmall;
		public TextView shelfPriceMedium;
		public EditText et_shelfPriceSmall;
		public EditText et_shelfPriceMedium;
	}
	
	/**
	 * A {@link java.text.DecimalFormat DecimalFormat} object used to format and display monetary values.<br>
	 * It is mainly used to separate thousands with commas and display a fix rounding value.
	 */
	private final DecimalFormat moneyFormat;
	
	/**
	 * Getter - {@link #moneyFormatter}
	 * 
	 * @return	A {@link java.text.DecimalFormat DecimalFormat} object used to format and display monetary values.
	 */
	public DecimalFormat getMoneyFormat () {
		return moneyFormat;
	}
	
	/**
	 * String used to host the new line character.
	 */
	private final String newLine = "\n";
	
	/**
	 * Integer holding the misty rose color
	 */
	private final int mistyRose;
	
	/**
	 * String used to host the expiry date label.
	 */
	private final String expiryDateLabel;
	
	/**
	 * String used to host the merchandize label.
	 */
	private final String merchandizeLabel;
	
	/**
	 * String used to host the over six months label.
	 */
	private final String overSixMonthsLabel;
	

	/**
	 * String used to host the over six months label.
	 */
	private final String availableLabel;
	/**
	 * String used to host the not available label.
	 */
	private final String notAvailabelLabel;
	
	/**
	 * String used to hold the code label.
	 */
	protected final String codeLabel;
	
	/**
	 * String used to hold the status label.
	 */
	protected final String statusLabel;
	
	/**
	 * String used to hold the existence label.
	 */
	protected final String existenceLabel;
	
	/**
	 * String used to hold the yes label.
	 */
	protected final String yesLabel;
	
	/**
	 * String used to hold the no label.
	 */
	protected final String noLabel;
	
	/**
	 * Flag indicating if the items are enabled or not.
	 */
	private boolean itemsEnabled;
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param layout	XML Layout (layout) resource id.
	 * @param orderItems	List of {@link me.SyncWise.Android.Modules.SalesOrder.Order Order} objects.
	 * @param currencyRounding	Integer used to indicate the currency rounding.
	 * @param itemsEnabled	Flag indicating if the items are enabled or not.
	 * @param allowZeroPrice	Flag indicating if the items with zero price are allowed or not.
	 */
	public ClientStockDetailsAdapter ( Context context , int layout , List < Stock > orderItems , final int currencyRounding , final boolean itemsEnabled , final boolean usePicker) {
		// Overloaded contructor
		this ( context , layout , orderItems , currencyRounding );
		// Initialize the attributes
		this.itemsEnabled = itemsEnabled;
		this.usePicker = usePicker;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param layout	XML Layout (layout) resource id.
	 * @param orderItems	List of {@link me.SyncWise.Android.Modules.SalesOrder.Order Order} objects.
	 * @param currencyRounding	Integer used to indicate the currency rounding.
	 */
	public ClientStockDetailsAdapter ( Context context , int layout , List < Stock > orderItems , final int currencyRounding ) {
		// Superclass method call
		super ( context , layout , orderItems );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
		codeLabel = AppResources.getInstance ( context ).getString ( context , R.string.code_label );
		StringBuilder pattern = new StringBuilder ();
		pattern.append ( " #,##0" );
		if ( currencyRounding >= 1 ) {
			pattern.append ( ".0" );
			for ( int i = 1 ; i < currencyRounding ; i ++ )
				pattern.append ( "0" );
		} // End if
		moneyFormat = new DecimalFormat ( pattern.toString () );
		expiryDateLabel = AppResources.getInstance ( context ).getString ( context , R.string.expiry_date_label ) + " : ";
		mistyRose = context.getResources ().getColor ( R.color.MistyRose );
		merchandizeLabel = AppResources.getInstance ( context ).getString ( context , R.string.client_stock_count_is_merchandize_label ) + " : ";
		overSixMonthsLabel = AppResources.getInstance ( context ).getString ( context , R.string.client_stock_count_is_over_six_months_label ) + " : ";
		notAvailabelLabel = AppResources.getInstance ( context ).getString ( context , R.string.not_available_abbreviation );
		yesLabel = AppResources.getInstance ( context ).getString ( context , R.string.yes_label );
		noLabel = AppResources.getInstance ( context ).getString ( context , R.string.no_label );
		statusLabel = AppResources.getInstance ( context ).getString ( context , R.string.status_label ) + " : ";
		existenceLabel = AppResources.getInstance ( context ).getString ( context , R.string.existence_label ) + " : ";
		itemsEnabled = true;
		availableLabel= AppResources.getInstance ( context ).getString ( context , R.string.client_stock_count_is_available_label ) + " : ";
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
			viewHolder.icon = (ImageView) convertView.findViewById ( R.id.icon_item_item );
			// Retrieve a reference to the item code
			viewHolder.code = (TextView) convertView.findViewById ( R.id.label_item_code );
			// Retrieve a reference to the item description
			viewHolder.description = (TextView) convertView.findViewById ( R.id.label_item_description );
			// Retrieve a reference to the arrow
			viewHolder.arrow = (ImageView) convertView.findViewById ( R.id.arrow_right );
			// Retrieve a reference to the tax label
			viewHolder.tax = (TextView) convertView.findViewById ( R.id.label_item_tax );
			// Retrieve a reference to the sales order layout
			viewHolder.salesOrder = (LinearLayout) convertView.findViewById ( R.id.layout_sales_order );
			// Retrieve a reference to the big unit of measurement layout
			viewHolder.big = (LinearLayout) convertView.findViewById ( R.id.layout_big_uom );
			// Retrieve a reference to the big unit of measurement layout
			viewHolder.medium = (LinearLayout) convertView.findViewById ( R.id.layout_medium_uom );
			// Retrieve a reference to the big unit of measurement layout
			viewHolder.small = (LinearLayout) convertView.findViewById ( R.id.layout_small_uom );
			// Retrieve a reference to the big quantity label
			viewHolder.quantityBig = (TextView) convertView.findViewById ( R.id.label_quantity_big );
			// Retrieve a reference to the medium quantity label
			viewHolder.quantityMedium = (TextView) convertView.findViewById ( R.id.label_quantity_medium );
			// Retrieve a reference to the small quantity label
			viewHolder.quantitySmall = (TextView) convertView.findViewById ( R.id.label_quantity_small );
			// Retrieve a reference to the big price label
			viewHolder.priceBig = (TextView) convertView.findViewById ( R.id.label_price_big );
			// Retrieve a reference to the medium price label
			viewHolder.priceMedium = (TextView) convertView.findViewById ( R.id.label_price_medium );
			// Retrieve a reference to the small price label
			viewHolder.priceSmall = (TextView) convertView.findViewById ( R.id.label_price_small );

			viewHolder.shelfPriceSmall= (TextView) convertView.findViewById ( R.id.label_small_shelf_price );
			viewHolder.shelfPriceMedium= (TextView) convertView.findViewById ( R.id.label_medium_shelf_price );			
					
			viewHolder.shelfPriceSmall.setVisibility(View.VISIBLE);
			viewHolder.shelfPriceMedium.setVisibility(View.VISIBLE);
			
			// Store the view holder as tag
			convertView.setTag ( viewHolder );
		} // End if
		else
			// An inflated view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) convertView.getTag ();
		
		// Build a spannable string out of the item code in order to apply various spans
		SpannableString code = new SpannableString ( codeLabel + " : " + getItem ( position ).getItem ().getItemCode () );
		// Apply style span
		code.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , codeLabel.length () , 0 );
		// Apply color span
		code.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , codeLabel.length () , 0 );
		
		// Check if the item is a regular item
		if ( ItemsUtils.isRegular ( getItem ( position ).getItem () ) ) {
			// Check if the current item belongs to the must stock list
			// AND is not confirmed 
			// AND has no color background
			if ( getItem ( position ).isMustStockList () && ! getItem ( position ).isConfirmed () )
				// Modify the raw background to indicate that the current must stock item is not confirmed
				convertView.setBackgroundColor ( mistyRose );
			else
				// Otherwise remove the raw background
				convertView.setBackgroundColor ( 0 );
			
			// Build a spannable string out of the expiry date in order to apply various spans
			SpannableString expiryDate = new SpannableString ( expiryDateLabel + ( getItem ( position ).getExpiryDate () == null ? notAvailabelLabel : DateTime.getBriefDate ( getContext () , getItem ( position ).getExpiryDate () ) ) );
			// Apply style span
			expiryDate.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , expiryDateLabel.length () , 0 );
			// Apply color span
			expiryDate.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , expiryDateLabel.length () , 0 );
			
			// Build a spannable string out of the merchandising flag in order to apply various spans
			SpannableString merchandize = new SpannableString ( merchandizeLabel + ( getItem ( position ).isMerchandize () ? yesLabel : noLabel ) );
			// Apply style span
			merchandize.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , merchandizeLabel.length () , 0 );
			// Apply color span
			merchandize.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , merchandizeLabel.length () , 0 );
			
			// Build a spannable string out of the over six months flag in order to apply various spans
			SpannableString overSixMonths = new SpannableString ( overSixMonthsLabel + ( getItem ( position ).isOverSixMonths () ? yesLabel : noLabel ) );
			// Apply style span
			overSixMonths.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , overSixMonthsLabel.length () , 0 );
			// Apply color span
			overSixMonths.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , overSixMonthsLabel.length () , 0 );
			
			// Build a spannable string out of the over six months flag in order to apply various spans
			SpannableString available = new SpannableString ( availableLabel + ( getItem ( position ).isAvailables() ? yesLabel : noLabel ) );
			// Apply style span 
			available.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , availableLabel.length () , 0 );
			// Apply color span
			available.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , availableLabel.length () , 0 );
			if(!usePicker)
				// Display the item code
				viewHolder.code.setText ( TextUtils.concat ( code , newLine , expiryDate , newLine , merchandize , newLine , overSixMonths,newLine,available ) , BufferType.SPANNABLE );
				else
					// Display the item code
					viewHolder.code.setText ( TextUtils.concat ( code   ) , BufferType.SPANNABLE );
		} // End if
		// Otherwise check if the item is an asset
		else if ( ItemsUtils.isAsset ( getItem ( position ).getItem () ) ) {
			// Otherwise remove the raw background
			convertView.setBackgroundColor ( 0 );
			
			// Build a spannable string out of the item code in order to apply various spans
			SpannableString status = new SpannableString ( statusLabel + ( getItem ( position ).getStatus () == null ? notAvailabelLabel : getItem ( position ).getStatus ().getStatusName () ) );
			// Apply style span
			status.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , statusLabel.length () , 0 );
			// Apply color span
			status.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , statusLabel.length () , 0 );
			
			// Build a spannable string out of the item code in order to apply various spans
			SpannableString existence = new SpannableString ( existenceLabel + ( getItem ( position ).getExistence () == null ? notAvailabelLabel : getItem ( position ).getExistence ().getStatusName () ) );
			// Apply style span
			existence.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , existenceLabel.length () , 0 );
			// Apply color span
			existence.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , existenceLabel.length () , 0 );
			
			// Display the item code
			viewHolder.code.setText ( TextUtils.concat ( code , newLine , existence , newLine , status ) , BufferType.SPANNABLE );
		} // End if
		
		// Set the description text
		viewHolder.description.setText ( getItem ( position ).getItem ().getItemName () );
		// Display / hide the arrow accordingly
		viewHolder.arrow.setVisibility ( itemsEnabled ? View.VISIBLE : View.GONE );
		// Check if the current item is taxable
		if ( getItem ( position ).getTax () != 0 ) {
			// Make sure the tax label
			viewHolder.tax.setVisibility ( View.VISIBLE );
			// Compute the tax label
			String taxLabel = AppResources.getInstance ( getContext () ).getString ( getContext () , R.string.tax_label );
			String percentageSign = AppResources.getInstance ( getContext () ).getString ( getContext () , R.string.percentage_sign );
			SpannableString tax = new SpannableString ( taxLabel + " : " + getItem ( position ).getTax () + percentageSign );
			// Apply foreground color span
			tax.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , taxLabel.length () , 0 );
			// Apply style span
			tax.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , taxLabel.length () , 0 );
			// Display the tax label
			viewHolder.tax.setText ( tax , BufferType.SPANNABLE );
		} // End if
		else
			// Hide the tax label
			viewHolder.tax.setVisibility ( View.INVISIBLE );
		
		// Check if the current item has a big UOM and a valid unit price
		if ( ItemsUtils.isBig ( getItem ( position ).getItem () ) && ItemsUtils.isRegular ( getItem ( position ).getItem () ) ) {
			// Make sure the big UOM layout is visible
			viewHolder.big.setVisibility ( View.VISIBLE );
			// Display the unit price
			viewHolder.priceBig.setText ( moneyFormat.format ( getItem ( position ).getPriceBig () ) );
			// Compute the big quantity label
			SpannableString quantityBig = new SpannableString ( getItem ( position ).getUnit ().getUnitBigDescription () + " : " + getItem ( position ).getQuantityBig () );
			// Apply foreground color span
			quantityBig.setSpan ( new ForegroundColorSpan ( getItem ( position ).getQuantityBig () == 0 ? Color.RED : Color.BLUE ) ,
					getItem ( position ).getUnit ().getUnitBigDescription ().length () + 3 ,
					quantityBig.length () ,
					0 );
			// Display the big quantity label
			viewHolder.quantityBig.setText ( quantityBig , BufferType.SPANNABLE );
		} // End if
		else
			// Hide the big UOM layout
			viewHolder.big.setVisibility ( View.INVISIBLE );
		
		// Check if the current item has a medium UOM and a valid unit price
		if ( ItemsUtils.isMedium ( getItem ( position ).getItem () ) && ItemsUtils.isRegular ( getItem ( position ).getItem () ) ) {
			// Make sure the medium UOM layout is visible
			viewHolder.medium.setVisibility ( View.VISIBLE );
			// Display the unit price
			viewHolder.priceMedium.setText ( moneyFormat.format ( getItem ( position ).getPriceMedium () ) );
			// Compute the medium quantity label
			SpannableString quantityMed = new SpannableString ( getItem ( position ).getUnit ().getUnitMediumDescription () + " : " + getItem ( position ).getQuantityMedium () );
			// Apply foreground color span
			quantityMed.setSpan ( new ForegroundColorSpan ( getItem ( position ).getQuantityMedium () == 0 ? Color.RED : Color.BLUE ) ,
					getItem ( position ).getUnit ().getUnitMediumDescription ().length () + 3 ,
					quantityMed.length () ,
					0 );			
			// Display the medium quantity label
			viewHolder.quantityMedium.setText ( quantityMed , BufferType.SPANNABLE );
			
			viewHolder.shelfPriceMedium.setText ( moneyFormat.format ( getItem ( position ).getShelfPriceMedium() ));
		} // End if
		else
			// Hide the medium UOM layout
			viewHolder.medium.setVisibility ( View.INVISIBLE );
		
		// Check if the items is a regular item
		if ( ItemsUtils.isRegular ( getItem ( position ).getItem () ) ) {
			// Make sure the small UOM layout is visible
			viewHolder.small.setVisibility ( View.VISIBLE );
			// Display the unit price
			viewHolder.priceSmall.setText ( moneyFormat.format ( getItem ( position ).getPriceSmall () ) );
			// Compute the small quantity label
			SpannableString quantitySmall = new SpannableString ( getItem ( position ).getUnit ().getUnitSmallDescription () + " : " + getItem ( position ).getQuantitySmall () );
			// Apply foreground color span
			quantitySmall.setSpan ( new ForegroundColorSpan ( getItem ( position ).getQuantitySmall () == 0 ? Color.RED : Color.BLUE ) ,
					getItem ( position ).getUnit ().getUnitSmallDescription ().length () + 3 ,
					quantitySmall.length () ,
					0 );
			// Display the small quantity label
			viewHolder.quantitySmall.setText ( quantitySmall , BufferType.SPANNABLE );
			
			viewHolder.shelfPriceSmall.setText ( moneyFormat.format ( getItem ( position ).getShelfPriceSmall() ));
		} // End if
		else
			// Hide the small UOM layout
			viewHolder.small.setVisibility ( View.INVISIBLE );
		
		// Determine if all UOMs are hidden
		if ( viewHolder.big.getVisibility () == View.INVISIBLE
				&& viewHolder.medium.getVisibility () == View.INVISIBLE
				&& viewHolder.small.getVisibility () == View.INVISIBLE )
			// Hide the sales order layout
			viewHolder.salesOrder.setVisibility ( View.GONE );
		else
			// Make sure the sales order layout is visible
			viewHolder.salesOrder.setVisibility ( View.VISIBLE );
			
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
		return itemsEnabled;
	}
	
}