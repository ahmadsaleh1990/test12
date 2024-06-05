/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.SalesRFR;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.DateTime;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.Currencies;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.ItemsUtils;
import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Modules.SalesOrder.SalesOrderDetailsActivity;
import me.SyncWise.Android.Widgets.TinyNumberPicker;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
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
public class SalesRFRDetailsAdapter extends ArrayAdapter < OrderRFR > implements OnClickListener , OnFocusChangeListener {

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
		public OrderRFR order;
		public int position;
		public ImageView icon;
		public TextView code;
		public TextView description;
		public ImageView arrow;
		public TextView tax;
		public LinearLayout salesOrder;
		public LinearLayout big;
		public LinearLayout medium;
		public LinearLayout small;
		public LinearLayout free;
		public TextView quantityBig;
		public TextView quantityMedium;
		public TextView quantitySmall;
		public TextView quantityFree;
		public TextView priceBig;
		public TextView priceMedium;
		public TextView priceSmall;
		public TextView priceFree;
		public TextView stockBig;
		public TextView stockMedium;
		public TextView stockSmall;
		public TinyNumberPicker pickerBig;
		public TinyNumberPicker pickerMedium;
		public TinyNumberPicker pickerSmall;
		public TinyNumberPicker pickerFree;
	}
	
	/**
	 * {@link me.SyncWise.Android.Database.Currencies Currencies} object hosting the current sales order currency.
	 */
	private final Currencies currency;
	
	/**
	 * A {@link java.text.DecimalFormat DecimalFormat} object used to format and display monetary values.<br>
	 * It is mainly used to separate thousands with commas and display a fix rounding value.
	 */
	private final DecimalFormat moneyFormat;
	
	/**
	 * String used to host the new line character.
	 */
	private final String newLine = "\n";
	
	/**
	 * String used to hold the code label.
	 */
	protected final String codeLabel;
	
	/**
	 * String used to host the total label.
	 */
	private final String totalLabel;
	
	/**
	 * String used to host the reason label.
	 */
	private final String reasonLabel;
	
	/**
	 * String used to host the expiry date label.
	 */
	private final String expiryDateLabel;
	
	/**
	 * String used to host the tax label
	 */
	private final String taxLabel;
	
	/**
	 * Integer hosting the brown color.
	 */
	private final int brownColor;
	
	/**
	 * Flag indicating if the items are enabled or not.
	 */
	private boolean itemsEnabled;
	
	/**
	 * Flag indicating if the number picker should be enabled or not.
	 */
	private boolean usePicker;
	/**
	 * String used to host the Item Barcode.
	 */
	private final String itemBarcode;
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param layout	XML Layout (layout) resource id.
	 * @param orderItems	List of {@link me.SyncWise.Android.Modules.SalesRFR.OrderRFR OrderRFR} objects.
	 * @param currency	{@link me.SyncWise.Android.Database.Currencies Currencies} object hosting the current sales order currency.
	 * @param itemsEnabled	Flag indicating if the items are enabled or not.
	 * @param usePicker	Flag indicating if the number picker should be enabled or not.
	 */
	public SalesRFRDetailsAdapter ( Context context , int layout , List < OrderRFR > orderItems , final Currencies currency , final boolean itemsEnabled , final boolean usePicker ) {
		// Overloaded constructor
		this ( context , layout , orderItems , currency );
		// Initialize the attributes
		this.itemsEnabled = itemsEnabled;
		this.usePicker = usePicker;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param layout	XML Layout (layout) resource id.
	 * @param orderItems	List of {@link me.SyncWise.Android.Modules.SalesRFR.OrderRFR OrderRFR} objects.
	 * @param currency	{@link me.SyncWise.Android.Database.Currencies Currencies} object hosting the current sales order currency.
	 */
	private SalesRFRDetailsAdapter ( Context context , int layout , List < OrderRFR > orderItems , final Currencies currency ) {
		// Superclass method call
		super ( context , layout , orderItems );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
		this.currency = currency;
		codeLabel = AppResources.getInstance ( context ).getString ( context , R.string.code_label );
		StringBuilder pattern = new StringBuilder ();
		pattern.append ( " #,##0" );
		if ( currency.getCurrencyRounding () >= 1 ) {
			pattern.append ( ".0" );
			for ( int i = 1 ; i < currency.getCurrencyRounding () ; i ++ )
				pattern.append ( "0" );
		} // End if
		moneyFormat = new DecimalFormat ( pattern.toString () );
		brownColor = context.getResources ().getColor ( R.color.Brown );
		totalLabel = AppResources.getInstance ( context ).getString ( context , R.string.total_label );
		reasonLabel = AppResources.getInstance ( context ).getString ( context , R.string.reason_label );
		expiryDateLabel = AppResources.getInstance ( context ).getString ( context , R.string.expiry_date_label );
		taxLabel = AppResources.getInstance ( getContext () ).getString ( getContext () , R.string.tax_label );
		itemBarcode= AppResources.getInstance ( context ).getString ( context , R.string.item_barcode_label );
		itemsEnabled = true;
	}
	
	/**
	 * Getter - {@link #moneyFormat}
	 * 
	 * @return	A {@link java.text.DecimalFormat DecimalFormat} object used to format and display monetary values.
	 */
	public DecimalFormat getMoneyFormat () {
		return moneyFormat;
	}
	
	/**
	 * Getter - {@link #usePicker}
	 * 
	 * @return	Flag indicating if the number picker should be enabled or not.
	 */
	public boolean getUsePicker () {
		return usePicker;
	}
	
	/**
	 * Getter - {@link #codeLabel}
	 * 
	 * @return	String used to hold the code label.
	 */
	public String getCodeLabel () {
		return codeLabel;
	}
	
	/**
	 * Getter - {@link #totalLabel}
	 * 
	 * @return	String used to host the total label.
	 */
	public String getTotalLabel () {
		return totalLabel;
	}
	public String getItemBarcode() {
		return itemBarcode;
	}
	/**
	 * Getter - {@link #reasonLabel}
	 * 
	 * @return	String used to host the reason label.
	 */
	public String getReasonLabel () {
		return reasonLabel;
	}
	
	/**
	 * Getter - {@link #expiryDateLabel}
	 * 
	 * @return	String used to host the expiry date label.
	 */
	public String getExpiryDateLabel () {
		return expiryDateLabel;
	}
	
	/**
	 * Getter - {@link #newLine}
	 * 
	 * @return	String used to host the new line character.
	 */
	public String getNewLine () {
		return newLine;
	}
	
	/**
	 * Getter - {@link #brownColor}
	 * 
	 * @return	Integer hosting the brown color.
	 */
	public Integer getBrownColor () {
		return brownColor;
	}
	
	/**
	 * Refreshes the RFR list item displayed data.
	 * 
	 * @param context	The application context.
	 * @param view	A {@link android.view.View View} reference of the list item that should be refreshed.
	 * @param usePicker	Flag indicating if the number picker should be enabled or not.
	 * @param _moneyFormat	A {@link java.text.DecimalFormat DecimalFormat} object used to format and display monetary values.
	 * @param currency	{@link me.SyncWise.Android.Database.Currencies Currencies} object hosting the current sales order currency.
	 * @param _codeLabel	String used to hold the code label. <em>(Optional)</em>
	 * @param _totalLabel	String used to host the total label. <em>(Optional)</em>
	 * @param _reasonLabel	String used to host the reason label. <em>(Optional)</em>
	 * @param _expiryDateLabel	String used to host the expiry label. <em>(Optional)</em>
	 * @param _newLine	String used to host the new line character. <em>(Optional)</em>
	 * @param _brownColor	Integer hosting the brown color. <em>(Optional)</em>
	 */
	public static void refreshOrderView ( final Context context , final View view , final boolean usePicker , final DecimalFormat _moneyFormat , final Currencies currency , final String _codeLabel , final String _totalLabel , final String _reasonLabel , final String _expiryDateLabel , final String _newLine , final Integer _brownColor,final String _itemBarcode ) {
		// Retrieve the stored view holder
		ViewHolder viewHolder = (ViewHolder) view.getTag ();
		
		// Declare and initialize the needed strings and colors
		String codeLabel = _codeLabel == null ? AppResources.getInstance ( context ).getString ( context , R.string.code_label ) : _codeLabel;
		String totalLabel = _totalLabel == null ? AppResources.getInstance ( context ).getString ( context , R.string.total_label ) : _totalLabel;
		String reasonLabel = _reasonLabel == null ? AppResources.getInstance ( context ).getString ( context , R.string.reason_label ) : _reasonLabel;
		String expiryDateLabel = _expiryDateLabel == null ? AppResources.getInstance ( context ).getString ( context , R.string.expiry_date_label ) : _expiryDateLabel;
		String newLine = _newLine == null ? "\n" : _newLine;
		Integer brownColor = _brownColor == null ? context.getResources ().getColor ( R.color.Brown ) : _brownColor;
		String itemBarcode=_itemBarcode==null? AppResources.getInstance ( context ).getString ( context , R.string.item_barcode_label ) : _itemBarcode;
		// Build a spannable string out of the item code in order to apply various spans
		SpannableString code = new SpannableString ( codeLabel + " : " + viewHolder.order.getItem ().getItemCode () );
		// Apply style span
		code.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , codeLabel.length () , 0 );
		// Apply color span
		code.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , codeLabel.length () , 0 );
		SpannableString itemBarcodes = new SpannableString ( itemBarcode + " : " + viewHolder.order.getItemBarcodes()   );
		// Apply style span
		itemBarcodes.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , itemBarcode.length () , 0 );
		// Apply color span
		itemBarcodes.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , itemBarcode.length () , 0 );
	 
		// Determine if the current line has at least one reason 
		// AND the number pickers are NOT used
		if ( ! viewHolder.order.getReasonOfReturn ().isEmpty () && ! usePicker ) {
			
			// Compute the net value
			double netValue = viewHolder.order.getQuantityBig ( viewHolder.order.getReasonOfReturn ().get ( 0 ).getReasonCode () ) * viewHolder.order.getPriceBig () 
					+ viewHolder.order.getQuantityMedium ( viewHolder.order.getReasonOfReturn ().get ( 0 ).getReasonCode () ) * viewHolder.order.getPriceMedium ()
					+ viewHolder.order.getQuantitySmall ( viewHolder.order.getReasonOfReturn ().get ( 0 ).getReasonCode () ) * viewHolder.order.getPriceSmall ();
			// Compute the taxes
			double taxesAmount = netValue * viewHolder.order.getTax () / 100;
			double taxeAmount=viewHolder.order.getQuantitySmall ( viewHolder.order.getReasonOfReturn ().get ( 0 ).getReasonCode () ) *viewHolder.order.getUnitTaxAmount()
					 
					+ viewHolder.order.getQuantityMedium ( viewHolder.order.getReasonOfReturn ().get ( 0 ).getReasonCode () ) *viewHolder.order.getCaseTaxAmount()
					 ;
			
			// Compute the total amount 
			double _totalAmount = netValue + taxesAmount+taxeAmount;
			
			// Build a spannable string out of the item total amount in order to apply various spans
			SpannableString totalAmount = new SpannableString ( totalLabel + " : " + _moneyFormat.format ( _totalAmount ) + " " + currency.getCurrencySymbol () );
			// Apply style span
			totalAmount.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , totalLabel.length () , 0 );
			// Apply color span
			totalAmount.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , totalLabel.length () , 0 );
			// Apply style span
			totalAmount.setSpan ( new StyleSpan ( Typeface.BOLD ) , totalAmount.length () - currency.getCurrencySymbol ().length () , totalAmount.length () , 0 );
			// Apply color span
			totalAmount.setSpan ( new ForegroundColorSpan ( brownColor ) , totalAmount.length () - currency.getCurrencySymbol ().length () , totalAmount.length () , 0 );
			
			// Build a spannable string out of the reason in order to apply various spans
			SpannableString reason = new SpannableString ( reasonLabel + " : " + viewHolder.order.getReasonOfReturn ().get ( 0 ) );
			// Apply style span
			reason.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , reasonLabel.length () , 0 );
			// Apply color span
			reason.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , reasonLabel.length () , 0 );
			
			// Retrieve the expiry date
			Calendar calendar = Calendar.getInstance ();
			calendar.setTime ( viewHolder.order.getExpiryDate ( viewHolder.order.getReasonOfReturn ().get ( 0 ).getReasonCode () ) );
			// Build a spannable string out of the reason in order to apply various spans
			SpannableString expiryDate = new SpannableString ( expiryDateLabel + " : " + DateTime.getFullDate ( context , calendar ) );
			// Apply style span
			expiryDate.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , expiryDateLabel.length () , 0 );
			// Apply color span
			expiryDate.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , expiryDateLabel.length () , 0 );
			
			// Display the item code and total amount
			viewHolder.code.setText ( TextUtils.concat ( code , newLine , itemBarcodes , newLine , totalAmount , newLine , reason , newLine , expiryDate ) , BufferType.SPANNABLE );

		} // End if
		else
			// Either the current item has no reasons or number pickers are disabled
			// Simply display the item code
			viewHolder.code.setText (TextUtils.concat ( code, newLine , itemBarcodes ), BufferType.SPANNABLE );
	}
	
	/*
	 * Get a View that displays the data at the specified position in the data set.
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView ( int position , View convertView , ViewGroup parent ) {
		try {
		
			// Declare the row view holder
			ViewHolder viewHolder;
			// Check if an inflated view is provided
			if ( convertView == null ) {
				// A new view must be inflated
				convertView = layoutInflater.inflate ( layout , null );
				// Register a callback to be invoked when this view is clicked
				convertView.setOnClickListener ( this );
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
				viewHolder.free = (LinearLayout) convertView.findViewById ( R.id.layout_free_uom );
				// Display the free quantity layout
				viewHolder.free.setVisibility ( View.INVISIBLE );
				// Retrieve a reference to the big quantity label
				viewHolder.quantityBig = (TextView) convertView.findViewById ( R.id.label_quantity_big );
				// Retrieve a reference to the medium quantity label
				viewHolder.quantityMedium = (TextView) convertView.findViewById ( R.id.label_quantity_medium );
				// Retrieve a reference to the small quantity label
				viewHolder.quantitySmall = (TextView) convertView.findViewById ( R.id.label_quantity_small );
				viewHolder.quantityFree = (TextView) convertView.findViewById ( R.id.label_quantity_free );
				// Retrieve a reference to the big price label
				viewHolder.priceBig = (TextView) convertView.findViewById ( R.id.label_price_big );
				// Retrieve a reference to the medium price label
				viewHolder.priceMedium = (TextView) convertView.findViewById ( R.id.label_price_medium );
				// Retrieve a reference to the small price label
				viewHolder.priceSmall = (TextView) convertView.findViewById ( R.id.label_price_small );
				viewHolder.priceFree = (TextView) convertView.findViewById ( R.id.label_price_free );
				// Retrieve a reference to the big tiny number picker
				viewHolder.pickerBig = (TinyNumberPicker) convertView.findViewById ( R.id.tiny_number_picker_quantity_big );
				// Retrieve a reference to the medium tiny number picker
				viewHolder.pickerMedium = (TinyNumberPicker) convertView.findViewById ( R.id.tiny_number_picker_quantity_medium );
				// Retrieve a reference to the small tiny number picker
				viewHolder.pickerSmall = (TinyNumberPicker) convertView.findViewById ( R.id.tiny_number_picker_quantity_small );
				// Retrieve a reference to the small tiny number picker
				viewHolder.pickerFree = (TinyNumberPicker) convertView.findViewById ( R.id.tiny_number_picker_quantity_free );
				// Retrieve a reference to the big stock label
				viewHolder.stockBig = (TextView) convertView.findViewById ( R.id.label_stock_big );
				// Retrieve a reference to the medium stock label
				viewHolder.stockMedium = (TextView) convertView.findViewById ( R.id.label_stock_medium );
				// Retrieve a reference to the small stock label
				viewHolder.stockSmall = (TextView) convertView.findViewById ( R.id.label_stock_small );
				
				if ( ! PermissionsUtils.getDisplayRfrPrice (getContext()  , DatabaseUtils.getCurrentUserCode ( getContext() ) , DatabaseUtils.getCurrentCompanyCode ( getContext()  ) ) )
					
				{
				// Hide the prices
				viewHolder.priceBig.setVisibility ( View.GONE );
				viewHolder.priceMedium.setVisibility ( View.GONE );
				viewHolder.priceSmall.setVisibility ( View.GONE );
				viewHolder.priceFree.setVisibility ( View.GONE );
				}else{
					viewHolder.priceBig.setVisibility ( View.VISIBLE );
					viewHolder.priceMedium.setVisibility ( View.VISIBLE );
					viewHolder.priceSmall.setVisibility ( View.VISIBLE );
					viewHolder.priceFree.setVisibility ( View.VISIBLE );
				}
				// Display / hide the quantity labels
				int labelVisibility = usePicker ? View.GONE : View.VISIBLE;
				viewHolder.quantityBig.setVisibility ( labelVisibility );
				viewHolder.quantityMedium.setVisibility ( labelVisibility );
				viewHolder.quantitySmall.setVisibility ( labelVisibility );
				viewHolder.quantityFree.setVisibility ( labelVisibility );
				// Display / hide the tiny number pickers
				int pickerVisibility = usePicker ? View.VISIBLE : View.GONE;
				viewHolder.pickerBig.setVisibility ( pickerVisibility );
				viewHolder.pickerMedium.setVisibility ( pickerVisibility );
				viewHolder.pickerSmall.setVisibility ( pickerVisibility );
				viewHolder.pickerFree.setVisibility ( pickerVisibility );
				// Determine if the tiny number pickers are used
				if ( usePicker ) {
					viewHolder.pickerBig.getEditText ().setOnFocusChangeListener ( this );
					viewHolder.pickerMedium.getEditText ().setOnFocusChangeListener ( this );
					viewHolder.pickerSmall.getEditText ().setOnFocusChangeListener ( this );
					viewHolder.pickerFree.getEditText ().setOnFocusChangeListener ( this );
				} // End if
				
				// Store the view holder as tag
				convertView.setTag ( viewHolder );

			} // End if
			else
				// An inflated view is already provided, retrieve the stored view holder
				viewHolder = (ViewHolder) convertView.getTag ();
			
			// Set the current order
			viewHolder.order = getItem ( position );
			// Set the current position
			viewHolder.position = position;
			
			// Declare and initialize an order watcher
			OrderRFRWatcher orderWatcher = null;
			
			// Refresh the order view
			refreshOrderView ( getContext () , convertView , usePicker , moneyFormat , currency , codeLabel , totalLabel , reasonLabel , expiryDateLabel , newLine , brownColor ,itemBarcode);
			
			// Set the description text
			viewHolder.description.setText ( getItem ( position ).getItem ().getItemName () );
			// Display / hide the arrow accordingly
			viewHolder.arrow.setVisibility ( itemsEnabled ? View.VISIBLE : View.GONE );
			// Check if the current item is taxable
			if ( getItem ( position ).getTax () != 0 ) {
				// Make sure the tax label
				viewHolder.tax.setVisibility ( View.VISIBLE );
				// Compute the tax label
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
			if ( ItemsUtils.isBig ( getItem ( position ).getItem () ) ) {
				// Make sure the big UOM layout is visible
				viewHolder.big.setVisibility ( View.VISIBLE );
				// Check if the picker is enabled
				if ( usePicker ) {
					// Clear the previous text watcher
					clearTextWatcher ( viewHolder.pickerBig.getEditText () );
					// Display the big quantity in the picker
					if ( SalesRFRDetailsActivity.selectedReasonCode == null )
						// Reset the big quantity
						viewHolder.pickerBig.setNumber ( 0 );
					else 
						// Display the big quantity of the selected reason
						viewHolder.pickerBig.setNumber ( getItem ( position ).getQuantityBig ( SalesRFRDetailsActivity.selectedReasonCode ) );
					// Assign a new text watcher
					orderWatcher = new OrderRFRWatcher ( (SalesRFRDetailsActivity) getContext () , convertView , getItem ( position ) , OrderRFRWatcher.Type.QUANTITY_BIG );
					viewHolder.pickerBig.getEditText ().addTextChangedListener ( orderWatcher );
					viewHolder.pickerBig.getEditText ().setTag ( orderWatcher );
					// Clear the edit text focus
					checkEditTextFocus ( viewHolder.pickerBig.getEditText () );
					// Make sure the current item is selected with a valid reason and expiry date
					if ( getItem ( position ).getItem ().getItemCode ().equals ( SalesRFRDetailsActivity.selectedItemCode ) && SalesRFRDetailsActivity.selectedReasonCode != null && SalesRFRDetailsActivity.expiryDateEdit != null ) 
						// Make sure the big UOM layout is visible
						viewHolder.big.setVisibility ( View.VISIBLE );
					else
						// Hide the big UOM layout 
						viewHolder.big.setVisibility ( View.INVISIBLE );
						
				} // End if
				else {
					// Compute the big quantity label
					SpannableString quantityBig = new SpannableString ( getItem ( position ).getUnit ().getUnitBigDescription () + " : " + getItem ( position ).getQuantityBigSummation () );
					// Apply foreground color span
					quantityBig.setSpan ( new ForegroundColorSpan ( getItem ( position ).getQuantityBigSummation () == 0 ? Color.RED : Color.BLUE ) ,
							getItem ( position ).getUnit ().getUnitBigDescription ().length () + 3 ,
							quantityBig.length () ,
							0 );
					// Display the big quantity label
					viewHolder.quantityBig.setText ( quantityBig , BufferType.SPANNABLE );
				} // End else
			} // End if
			else
				// Hide the big UOM layout
				viewHolder.big.setVisibility ( View.INVISIBLE );
			
			// Check if the current item has a medium UOM and a valid unit price
			if ( ItemsUtils.isMedium ( getItem ( position ).getItem () ) ) {
				// Make sure the medium UOM layout is visible
				viewHolder.medium.setVisibility ( View.VISIBLE );
				viewHolder.priceMedium.setText ( moneyFormat.format ( getItem ( position ).getPriceMedium () ) + " " + currency.getCurrencySymbol () );
				
				// Check if the picker is enabled
				if ( usePicker ) {
					// Clear the previous text watcher
					clearTextWatcher ( viewHolder.pickerMedium.getEditText () );
					// Display the medium quantity in the picker
					if ( SalesRFRDetailsActivity.selectedReasonCode == null )
						// Reset the medium quantity
						viewHolder.pickerMedium.setNumber ( 0 );
					else 
						// Display the medium quantity of the selected reason
						viewHolder.pickerMedium.setNumber ( getItem ( position ).getQuantityMedium ( SalesRFRDetailsActivity.selectedReasonCode ) );
					// Assign a new text watcher
					orderWatcher = new OrderRFRWatcher ( (SalesRFRDetailsActivity) getContext () , convertView , getItem ( position ) , OrderRFRWatcher.Type.QUANTITY_MEDIUM );
					viewHolder.pickerMedium.getEditText ().addTextChangedListener ( orderWatcher );
					viewHolder.pickerMedium.getEditText ().setTag ( orderWatcher );
					// Clear the edit text focus
					checkEditTextFocus ( viewHolder.pickerMedium.getEditText () );
					// Make sure the current item is selected with a valid reason and expiry date
					if ( getItem ( position ).getItem ().getItemCode ().equals ( SalesRFRDetailsActivity.selectedItemCode ) && SalesRFRDetailsActivity.selectedReasonCode != null && SalesRFRDetailsActivity.expiryDateEdit != null )
						// Make sure the medium UOM layout is visible
						viewHolder.medium.setVisibility ( View.VISIBLE );
					else
						// Hide the medium UOM layout 
						viewHolder.medium.setVisibility ( View.INVISIBLE );
				} // End if
				else {
					// Compute the medium quantity label
					SpannableString quantityMed = new SpannableString ( getItem ( position ).getUnit ().getUnitMediumDescription () + " : " + getItem ( position ).getQuantityMediumSummation () );
					// Apply foreground color span
					quantityMed.setSpan ( new ForegroundColorSpan ( getItem ( position ).getQuantityMediumSummation () == 0 ? Color.RED : Color.BLUE ) ,
							getItem ( position ).getUnit ().getUnitMediumDescription ().length () + 3 ,
							quantityMed.length () ,
							0 );
					// Display the medium quantity label
					viewHolder.quantityMedium.setText ( quantityMed , BufferType.SPANNABLE );
				} // End else
			} // End if
			else
				// Hide the medium UOM layout
				viewHolder.medium.setVisibility ( View.INVISIBLE );
			
			// Make sure the small UOM layout is visible
			viewHolder.small.setVisibility ( View.VISIBLE );
			viewHolder.priceSmall.setText ( moneyFormat.format ( getItem ( position ).getPriceSmall () ) + " " + currency.getCurrencySymbol () );
			
			// Check if the picker is enabled
			if ( usePicker ) {
				// Clear the previous text watcher
				clearTextWatcher ( viewHolder.pickerSmall.getEditText () );
				// Display the small quantity in the picker
				if( SalesRFRDetailsActivity.selectedReasonCode == null)
					// Reset the small quantity
					viewHolder.pickerSmall.setNumber ( 0 );
				else 
					// Display the small quantity of the selected reason
					viewHolder.pickerSmall.setNumber(getItem ( position ).getQuantitySmall ( SalesRFRDetailsActivity.selectedReasonCode ) );
				// Assign a new text watcher
				orderWatcher = new OrderRFRWatcher ( (SalesRFRDetailsActivity) getContext () , convertView , getItem ( position ) , OrderRFRWatcher.Type.QUANTITY_SMALL );
				viewHolder.pickerSmall.getEditText ().addTextChangedListener ( orderWatcher );
				viewHolder.pickerSmall.getEditText ().setTag ( orderWatcher );
				// Clear the edit text focus
				checkEditTextFocus ( viewHolder.pickerSmall.getEditText () );
				// Make sure the current item is selected with a valid reason and expiry date
				if ( getItem ( position ).getItem ().getItemCode ().equals ( SalesRFRDetailsActivity.selectedItemCode ) && SalesRFRDetailsActivity.selectedReasonCode != null && SalesRFRDetailsActivity.expiryDateEdit != null )
					// Make sure the small UOM layout is visible
					viewHolder.small.setVisibility ( View.VISIBLE );
				else
					// Hide the small UOM layout 
					viewHolder.small.setVisibility ( View.INVISIBLE );
			} // End if
			else {
				// Compute the small quantity label
				SpannableString quantitySmall = new SpannableString ( getItem ( position ).getUnit ().getUnitSmallDescription () + " : " + getItem ( position ).getQuantitySmallSummation () );
				// Apply foreground color span
				quantitySmall.setSpan ( new ForegroundColorSpan ( getItem ( position ).getQuantitySmallSummation () == 0 ? Color.RED : Color.BLUE ) ,
						getItem ( position ).getUnit ().getUnitSmallDescription ().length () + 3 ,
						quantitySmall.length () ,
						0 );
				// Display the small quantity label
				viewHolder.quantitySmall.setText ( quantitySmall , BufferType.SPANNABLE );
			} // End else
			
			// Determine if all UOMs are hidden
			if ( viewHolder.big.getVisibility () == View.INVISIBLE
					&& viewHolder.medium.getVisibility () == View.INVISIBLE
					&& viewHolder.small.getVisibility () == View.INVISIBLE 
				&& viewHolder.free.getVisibility () == View.INVISIBLE )
				// Hide the sales order layout
				viewHolder.salesOrder.setVisibility ( View.GONE );
			else
				// Make sure the sales order layout is visible
				viewHolder.salesOrder.setVisibility ( View.VISIBLE );
			
			// Check if the number pickers are used AND if the current item is selected 
			if(  usePicker && SalesRFRDetailsActivity.selectedItemCode != null &&viewHolder.order.getItem ().getItemCode ().equals ( SalesRFRDetailsActivity.selectedItemCode ) )
				// Highlight the current item in light gray
				convertView.setBackgroundColor ( Color.LTGRAY );
			else
				// Restore default background color
				convertView.setBackgroundColor ( Color.WHITE );
			// Return the view
			return convertView;
		
		} catch ( Exception exception ) {
			// Do nothing
			return new View ( getContext () );
		} // End of try-catch block
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
	
	/**
	 * Clears the associated text watcher as tag from the provided edit text (if any).
	 * 
	 * @param editText	Edit text object to clear the text watcher from.
	 */
	private void clearTextWatcher ( final EditText editText ) {
		try {
			// Retrieve the edit text's tag
			Object tag = editText.getTag ();
			// Check if the tag is a text watcher
			if ( tag instanceof TextWatcher )
				// Remove the text watcher
				editText.removeTextChangedListener ( (TextWatcher) tag );
		} catch ( Exception exception ) {
			// Do nothing
		} // End of try-catch block
	}
	
	/**
	 * Checks the provided edit text focus and disables it as needed.
	 * 
	 * @param editText	Edit text object to clear focus if needed.
	 */
	private void checkEditTextFocus ( final EditText editText ) {
		// Check if the edit text has focus
		if ( editText.hasFocus () )
			// Clear focus by setting focus to another view
			( (Activity) getContext () ).findViewById ( R.id.view_clear_focus ).requestFocus ();
	}

	/*
	 * Called when a view has been clicked.
	 *
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick ( View view ) {
		// Select the current view
		( (SalesRFRDetailsActivity) getContext () ).selectedView ( view , ! usePicker , true );
	}


	/*
	 * Called when the focus state of a view has changed.
	 *
	 * @see android.view.View.OnFocusChangeListener#onFocusChange(android.view.View, boolean)
	 */
	@Override
	public void onFocusChange ( View view , boolean hasFocus ) {
		// Check if the view is and edit text
		if ( ! ( view instanceof EditText ) )
			// Invalid view
			return;
		try {
			// Check if the text is equal to zero
			if ( ( (EditText) view ).getText ().toString ().equals ( "0" ) )
				// Clear the edit text
				( (EditText) view ).setText ( "" );
		} catch ( Exception exception ) {
			// Do nothing
		} // End of try-catch block
	}
	
}