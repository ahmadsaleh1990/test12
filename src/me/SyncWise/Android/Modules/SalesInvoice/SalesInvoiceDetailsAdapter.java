/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.SalesInvoice;

import java.text.DecimalFormat;
import java.util.List;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.Currencies;
import me.SyncWise.Android.Database.ItemsUtils;
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
public class SalesInvoiceDetailsAdapter extends ArrayAdapter < Invoice > implements OnClickListener , OnFocusChangeListener {

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
		public int invoiceState;
		public Invoice invoice;
		public int position;
		public ImageView icon;
		public TextView code;
		public TextView description;
		public ImageView arrow;
		public TextView taxDiscount;
		public LinearLayout salesInvoice;
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
	 * {@link me.SyncWise.Android.Database.Currencies Currencies} object hosting the current sales invoice currency.
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
	 * String used to host the vehicle label.
	 */
	private final String vehicleLabel;
	
	/**
	 * String used to host the total label.
	 */
	private final String totalLabel;
	
	/**
	 * String used to host the discount label
	 */
	private final String discountLabel;
	
	/**
	 * String used to host the tax label
	 */
	private final String taxLabel;
	
	/**
	 * String used to host the counted label
	 */
	private final String countedLabel;
	
	/**
	 * String used to host the suggested label
	 */
	private final String suggestedLabel;
	
	/**
	 * Integer holding the misty rose color
	 */
	private final int mistyRose;
	
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
	 * Integer used to host the invoice state.
	 */
	private int invoiceState;
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param layout	XML Layout (layout) resource id.
	 * @param invoiceItems	List of {@link me.SyncWise.Android.Modules.SalesInvoice.Invoice Invoice} objects.
	 * @param currency	{@link me.SyncWise.Android.Database.Currencies Currencies} object hosting the current sales invoice currency.
	 * @param itemsEnabled	Flag indicating if the items are enabled or not.
	 * @param usePicker	Flag indicating if the number picker should be enabled or not.
	 * @param invoiceState	Integer used to host the invoice state
	 */
	public SalesInvoiceDetailsAdapter ( Context context , int layout , List < Invoice > invoiceItems , final Currencies currency , final boolean itemsEnabled , final boolean usePicker , final int invoiceState ) {
		// Superclass method call
		super ( context , layout , invoiceItems );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
		this.currency = currency;
		this.itemsEnabled = itemsEnabled;
		this.usePicker = usePicker;
		this.invoiceState = invoiceState;
		codeLabel = AppResources.getInstance ( context ).getString ( context , R.string.code_label );
		StringBuilder pattern = new StringBuilder ();
		pattern.append ( " #,##0" );
		if ( currency.getCurrencyRounding () >= 1 ) {
			pattern.append ( ".0" );
			for ( int i = 1 ; i < currency.getCurrencyRounding () ; i ++ )
				pattern.append ( "0" );
		} // End if
		moneyFormat = new DecimalFormat ( pattern.toString () );
		mistyRose = context.getResources ().getColor ( R.color.MistyRose );
		brownColor = context.getResources ().getColor ( R.color.Brown );
		vehicleLabel = AppResources.getInstance ( context ).getString ( context , R.string.vehicle_label );
		totalLabel = AppResources.getInstance ( context ).getString ( context , R.string.total_label );
		discountLabel = AppResources.getInstance ( context ).getString ( context , R.string.discount_label );
		taxLabel = AppResources.getInstance ( getContext () ).getString ( getContext () , R.string.tax_label );
		countedLabel = AppResources.getInstance ( getContext () ).getString ( getContext () , R.string.counted_label );
		suggestedLabel = AppResources.getInstance ( getContext () ).getString ( getContext () , R.string.suggested_label );
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
	 * Getter - {@link #mistyRose}
	 * 
	 * @return	Integer hosting the misty rose color.
	 */
	public Integer getMistyRose () {
		return mistyRose;
	}
	
	/**
	 * Refreshes the invoice list item displayed data.
	 * 
	 * @param context	The application context.
	 * @param view	A {@link android.view.View View} reference of the list item that should be refreshed.
	 * @param _moneyFormat	A {@link java.text.DecimalFormat DecimalFormat} object used to format and display monetary values.
	 * @param currency	{@link me.SyncWise.Android.Database.Currencies Currencies} object hosting the current sales invoice currency.
	 * @param _codeLabel	String used to hold the code label. <em>(Optional)</em>
	 * @param _totalLabel	String used to host the total label. <em>(Optional)</em>
	 * @param _newLine	String used to host the new line character. <em>(Optional)</em>
	 * @param _brownColor	Integer hosting the brown color. <em>(Optional)</em>
	 * @param _mistyRose	Integer holding the misty rose color <em>(Optional)</em>
	 */
	public static void refreshInvoiceView ( final Context context , final View view , final DecimalFormat _moneyFormat , final Currencies currency , final String _codeLabel , final String _totalLabel , final String _newLine , final Integer _brownColor , final Integer _mistyRose ) {
		// Retrieve the stored view holder
		ViewHolder viewHolder = (ViewHolder) view.getTag ();
		
		// Declare and initialize the needed strings and colors
		String codeLabel = _codeLabel == null ? AppResources.getInstance ( context ).getString ( context , R.string.code_label ) : _codeLabel;
		String totalLabel = _totalLabel == null ? AppResources.getInstance ( context ).getString ( context , R.string.total_label ) : _totalLabel;
		String newLine = _newLine == null ? "\n" : _newLine;
		Integer brownColor = _brownColor == null ? context.getResources ().getColor ( R.color.Brown ) : _brownColor;
		Integer mistyRose = _mistyRose == null ? context.getResources ().getColor ( R.color.MistyRose ) : _mistyRose;
		
		// Check if the current item belongs to the must stock list
		// AND is not confirmed 
		// AND has no color background
		Integer greenYellow1 = Color.rgb( 204, 204 ,255); 
		 if (viewHolder.invoice.getItem().getIsHighlight()!=null && viewHolder.invoice.getItem().getIsHighlight()==1 && viewHolder.invoice.isMustStockList () )
			// Otherwise remove the raw background
			 view.setBackgroundColor (greenYellow1 );
		 else if ( viewHolder.invoice.isMustStockList () )
			// Modify the raw background to indicate that the current must stock item is not confirmed
			view.setBackgroundColor ( mistyRose );
//		else
//			// Otherwise remove the raw background
//			view.setBackgroundColor ( 0 );
		
		// Build a spannable string out of the item code in order to apply various spans
		SpannableString code = new SpannableString ( codeLabel + " : " + viewHolder.invoice.getItem ().getItemCode () );
		// Apply style span
		code.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , codeLabel.length () , 0 );
		// Apply color span
		code.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , codeLabel.length () , 0 );
		
		// Compute the total amount 
		double _totalAmount = viewHolder.invoice.getTotalAmount ( viewHolder.invoiceState , true , true );
		
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
		
		// Display the item code and total amount
		viewHolder.code.setText ( TextUtils.concat ( code , ( viewHolder.invoiceState == Invoice.STATE_MISSED ? "" : newLine ) , ( viewHolder.invoiceState == Invoice.STATE_MISSED ? "" : totalAmount ) ) , BufferType.SPANNABLE );
		viewHolder.priceSmall.setText(_moneyFormat.format (viewHolder.invoice.getPriceSmall())+ " " + currency.getCurrencySymbol ());
		viewHolder.priceMedium.setText(_moneyFormat.format (viewHolder.invoice.getPriceMedium())+ " " + currency.getCurrencySymbol ());
	}
	
	/*
	 * Get a View that displays the data at the specified position in the data set.
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView ( int position , View convertView , ViewGroup parent ) {
//		try {
		
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
				viewHolder.taxDiscount = (TextView) convertView.findViewById ( R.id.label_item_tax );
				// Retrieve a reference to the sales invoice layout
				viewHolder.salesInvoice = (LinearLayout) convertView.findViewById ( R.id.layout_sales_order );
				// Retrieve a reference to the big unit of measurement layout
				viewHolder.big = (LinearLayout) convertView.findViewById ( R.id.layout_big_uom );
				// Retrieve a reference to the medium unit of measurement layout
				viewHolder.medium = (LinearLayout) convertView.findViewById ( R.id.layout_medium_uom );
				// Retrieve a reference to the small unit of measurement layout
				viewHolder.small = (LinearLayout) convertView.findViewById ( R.id.layout_small_uom );
				// Retrieve a reference to the free unit of measurement layout
				viewHolder.free = (LinearLayout) convertView.findViewById ( R.id.layout_free_uom );
				// Hide the free quantity layout
				viewHolder.free.setVisibility ( View.GONE );
				// Retrieve a reference to the big quantity label
				viewHolder.quantityBig = (TextView) convertView.findViewById ( R.id.label_quantity_big );
				// Retrieve a reference to the medium quantity label
				viewHolder.quantityMedium = (TextView) convertView.findViewById ( R.id.label_quantity_medium );
				// Retrieve a reference to the small quantity label
				viewHolder.quantitySmall = (TextView) convertView.findViewById ( R.id.label_quantity_small );
				// Retrieve a reference to the free quantity label
				viewHolder.quantityFree = (TextView) convertView.findViewById ( R.id.label_quantity_free );
				// Retrieve a reference to the big price label
				viewHolder.priceBig = (TextView) convertView.findViewById ( R.id.label_price_big );
				// Retrieve a reference to the medium price label
				viewHolder.priceMedium = (TextView) convertView.findViewById ( R.id.label_price_medium );
				// Retrieve a reference to the small price label
				viewHolder.priceSmall = (TextView) convertView.findViewById ( R.id.label_price_small );
				// Retrieve a reference to the free price label
				viewHolder.priceFree = (TextView) convertView.findViewById ( R.id.label_price_free );
				// Retrieve a reference to the big tiny number picker
				viewHolder.pickerBig = (TinyNumberPicker) convertView.findViewById ( R.id.tiny_number_picker_quantity_big );
				// Retrieve a reference to the medium tiny number picker
				viewHolder.pickerMedium = (TinyNumberPicker) convertView.findViewById ( R.id.tiny_number_picker_quantity_medium );
				// Retrieve a reference to the small tiny number picker
				viewHolder.pickerSmall = (TinyNumberPicker) convertView.findViewById ( R.id.tiny_number_picker_quantity_small );
				// Retrieve a reference to the free tiny number picker
				viewHolder.pickerFree = (TinyNumberPicker) convertView.findViewById ( R.id.tiny_number_picker_quantity_free );
				// Retrieve a reference to the big stock label
				viewHolder.stockBig = (TextView) convertView.findViewById ( R.id.label_stock_big );
				// Retrieve a reference to the medium stock label
				viewHolder.stockMedium = (TextView) convertView.findViewById ( R.id.label_stock_medium );
				// Retrieve a reference to the small stock label
				viewHolder.stockSmall = (TextView) convertView.findViewById ( R.id.label_stock_small );
				
				// Display / hide the quantity labels
				int labelVisibility = usePicker ? View.GONE : View.VISIBLE;
				viewHolder.quantityBig.setVisibility ( labelVisibility );
				viewHolder.quantityMedium.setVisibility ( labelVisibility );
				viewHolder.quantitySmall.setVisibility ( labelVisibility );
				// Display / hide the tiny number pickers
				int pickerVisibility = usePicker ? View.VISIBLE : View.GONE;
				viewHolder.pickerBig.setVisibility ( pickerVisibility );
				viewHolder.pickerMedium.setVisibility ( pickerVisibility );
				viewHolder.pickerSmall.setVisibility ( pickerVisibility );
				// Determine if the tiny number pickers are used
				if ( usePicker ) {
					viewHolder.pickerBig.getEditText ().setOnFocusChangeListener ( this );
					viewHolder.pickerMedium.getEditText ().setOnFocusChangeListener ( this );
					viewHolder.pickerSmall.getEditText ().setOnFocusChangeListener ( this );
				} // End if
				
				// Set the invoice state
				viewHolder.invoiceState = this.invoiceState;
				
				// Store the view holder as tag
				convertView.setTag ( viewHolder );
			} // End if
			else
				// An inflated view is already provided, retrieve the stored view holder
				viewHolder = (ViewHolder) convertView.getTag ();
	
			// Set the current invoice
			viewHolder.invoice = getItem ( position );
			// Set the current position
			viewHolder.position = position;
			
			// Declare and initialize an invoice watcher
			InvoiceWatcher invoiceWatcher = null;
			Integer greenYellow = Color.rgb(255, 204, 229); 
		
			if (viewHolder.invoice.getItem().getIsHighlight()!=null && viewHolder.invoice.getItem().getIsHighlight()==1 )
				// Modify the raw background to indicate that the current must stock item is not confirmed
				convertView.setBackgroundColor ( greenYellow );
		
			else
				convertView.setBackgroundColor ( 0 );	
			// Refresh the invoice view
			refreshInvoiceView ( getContext () , convertView , moneyFormat , currency , codeLabel , totalLabel , newLine , brownColor , mistyRose );
			
			// Set the description text
			viewHolder.description.setText ( viewHolder.invoice.getItem ().getItemName () );
			// Display / hide the arrow accordingly
			viewHolder.arrow.setVisibility ( itemsEnabled ? View.VISIBLE : View.GONE );
			// Check if the current item is taxable or has discount
			if ( viewHolder.invoice.getTax () != 0 ||viewHolder.invoice.getDiscountPercentage ( viewHolder.invoiceState ) != 0 ) {
				// Make sure the tax label
				viewHolder.taxDiscount.setVisibility ( View.VISIBLE );
				String percentageSign = AppResources.getInstance ( getContext () ).getString ( getContext () , R.string.percentage_sign );
				// Compute the tax label
				SpannableString tax = null;
				// Check if the current item is taxable
				if ( viewHolder.invoice.getTax () != 0 ) {
					// Compute the tax label
					tax = new SpannableString ( taxLabel + " : " + getItem ( position ).getTax () + percentageSign );
					// Apply foreground color span
					tax.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , taxLabel.length () , 0 );
					// Apply style span
					tax.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , taxLabel.length () , 0 );
				} // End if
				// Compute the discount label
				SpannableString discount = null;
				// Check if the current item is taxable
				if ( viewHolder.invoice.getDiscountPercentage ( viewHolder.invoiceState ) != 0 ) {
					// Compute the tax label
					discount = new SpannableString ( discountLabel + " : " + viewHolder.invoice.getDiscountPercentage ( viewHolder.invoiceState ) + percentageSign );
					// Apply foreground color span
					discount.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , discountLabel.length () , 0 );
					// Apply style span
					discount.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , discountLabel.length () , 0 );
				} // End if
				// Concatenate the strings
				CharSequence taxDiscount = tax == null ? discount : ( discount == null ? tax : TextUtils.concat ( tax , newLine , discount ) );
				// Display the tax / discount label
				viewHolder.taxDiscount.setText ( taxDiscount , BufferType.SPANNABLE );
			} // End if
			else
				// Hide the tax label
				viewHolder.taxDiscount.setVisibility ( View.INVISIBLE );
			
			// Check if the current item has a big UOM and a valid unit price
			if ( ItemsUtils.isBig ( viewHolder.invoice.getItem () ) ) {
				// Make sure the big UOM layout is visible
				viewHolder.big.setVisibility ( View.VISIBLE );
				// Display the unit price
				viewHolder.priceBig.setText ( moneyFormat.format ( viewHolder.invoice.getPriceBig () ) + " " + currency.getCurrencySymbol () );
				// Check if the picker is enabled
				if ( usePicker ) {
					// Clear the previous text watcher
					clearTextWatcher ( viewHolder.pickerBig.getEditText () );
					// Display the big quantity in the picker
					viewHolder.pickerBig.setNumber ( viewHolder.invoice.getQuantityBig () );
					// Assign a new text watcher
					invoiceWatcher = new InvoiceWatcher ( (SalesInvoiceDetailsActivity) getContext () , convertView , viewHolder.invoice , InvoiceWatcher.Type.QUANTITY_BIG );
					viewHolder.pickerBig.getEditText ().addTextChangedListener ( invoiceWatcher );
					viewHolder.pickerBig.getEditText ().setTag ( invoiceWatcher );
					// Clear the edit text focus
					checkEditTextFocus ( viewHolder.pickerBig.getEditText () );
				} // End if
				else {
					// Determine the quantity
					double quantity = viewHolder.invoice.getBig ( viewHolder.invoiceState );
					// Compute the big quantity label
					SpannableString quantityBig = new SpannableString ( ItemsUtils.getBigDescription ( viewHolder.invoice.getUnit () ) + " : " + quantity );
					// Apply foreground color span
					quantityBig.setSpan ( new ForegroundColorSpan ( quantity == 0 ? Color.RED : Color.BLUE ) ,
							ItemsUtils.getBigDescription ( viewHolder.invoice.getUnit () ).length () + 3 ,
							quantityBig.length () ,
							0 );
					// Display the big quantity label
					viewHolder.quantityBig.setText ( quantityBig , BufferType.SPANNABLE );
				} // End else
				
				// Compute the big vehicle label
				SpannableString vehicleBig = new SpannableString ( vehicleLabel + " : " + ( (int) viewHolder.invoice.getVehicleBig () ) );
				// Apply foreground color span
				vehicleBig.setSpan ( new ForegroundColorSpan ( viewHolder.invoice.getVehicleBig () == 0 ? Color.RED : Color.BLUE ) , vehicleLabel.length () + 3 , vehicleBig.length () , 0 );
				
				// Compute the counted big label
				SpannableString countedBig = new SpannableString ( countedLabel + " : " + ( (int) viewHolder.invoice.getCountedBig () ) );
				// Apply foreground color span
				countedBig.setSpan ( new ForegroundColorSpan ( viewHolder.invoice.getCountedBig () == 0 ? Color.RED : Color.BLUE ) , countedLabel.length () + 3 , countedBig.length () , 0 );
				
				// Compute the suggested big label
				SpannableString suggestedBig = new SpannableString ( suggestedLabel + " : " + ( (int) viewHolder.invoice.getSuggestedBig () ) );
				// Apply foreground color span
				suggestedBig.setSpan ( new ForegroundColorSpan ( viewHolder.invoice.getSuggestedBig () == 0 ? Color.RED : Color.BLUE ) , suggestedLabel.length () + 3 , suggestedBig.length () , 0 );
				
				// Display the big stock label
				viewHolder.stockBig.setText ( TextUtils.concat ( ( usePicker ? ItemsUtils.getBigDescription ( viewHolder.invoice.getUnit () ) : "" ) , ( usePicker ? newLine : "" ) , vehicleBig , newLine , countedBig , newLine , suggestedBig ) , BufferType.SPANNABLE );
			} // End if
			else
				// Hide the big UOM layout
				viewHolder.big.setVisibility ( View.INVISIBLE );
			
			// Check if the current item has a medium UOM and a valid unit price
			if ( ItemsUtils.isMedium ( viewHolder.invoice.getItem () ) ) {
				// Make sure the medium UOM layout is visible
				viewHolder.medium.setVisibility ( View.VISIBLE );
				// Display the unit price
				viewHolder.priceMedium.setText ( moneyFormat.format ( viewHolder.invoice.getPriceMedium () ) + " " + currency.getCurrencySymbol () );
				// Check if the picker is enabled
				if ( usePicker ) {
					// Clear the previous text watcher
					clearTextWatcher ( viewHolder.pickerMedium.getEditText () );
					// Display the medium quantity in the picker
					viewHolder.pickerMedium.setNumber ( viewHolder.invoice.getQuantityMedium () );
					// Assign a new text watcher
					invoiceWatcher = new InvoiceWatcher ( (SalesInvoiceDetailsActivity) getContext () , convertView , viewHolder.invoice , InvoiceWatcher.Type.QUANTITY_MEDIUM );
					viewHolder.pickerMedium.getEditText ().addTextChangedListener ( invoiceWatcher );
					viewHolder.pickerMedium.getEditText ().setTag ( invoiceWatcher );
					// Clear the edit text focus
					checkEditTextFocus ( viewHolder.pickerMedium.getEditText () );
				} // End if
				else {
					// Determine the quantity
					double quantity = viewHolder.invoice.getMedium ( viewHolder.invoiceState );
					// Compute the medium quantity label
					SpannableString quantityMed = new SpannableString ( ItemsUtils.getMediumDescription ( viewHolder.invoice.getUnit () ) + " : " + quantity );
					// Apply foreground color span
					quantityMed.setSpan ( new ForegroundColorSpan ( quantity == 0 ? Color.RED : Color.BLUE ) ,
							ItemsUtils.getMediumDescription ( viewHolder.invoice.getUnit () ).length () + 3 ,
							quantityMed.length () ,
							0 );
					// Display the medium quantity label
					viewHolder.quantityMedium.setText ( quantityMed , BufferType.SPANNABLE );
				} // End else
				
				// Compute the medium vehicle label
				SpannableString vehicleMedium = new SpannableString ( vehicleLabel + " : " + ( (int) viewHolder.invoice.getVehicleMedium () ) );
				// Apply foreground color span
				vehicleMedium.setSpan ( new ForegroundColorSpan ( viewHolder.invoice.getVehicleMedium () == 0 ? Color.RED : Color.BLUE ) , vehicleLabel.length () + 3 , vehicleMedium.length () , 0 );
				
				// Compute the counted medium label
				SpannableString countedMedium = new SpannableString ( countedLabel + " : " + ( (int) viewHolder.invoice.getCountedMedium () ) );
				// Apply foreground color span
				countedMedium.setSpan ( new ForegroundColorSpan ( viewHolder.invoice.getCountedMedium () == 0 ? Color.RED : Color.BLUE ) , countedLabel.length () + 3 , countedMedium.length () , 0 );
				
				// Compute the suggested medium label
				SpannableString suggestedMedium = new SpannableString ( suggestedLabel + " : " + ( (int) viewHolder.invoice.getSuggestedMedium () ) );
				// Apply foreground color span
				suggestedMedium.setSpan ( new ForegroundColorSpan ( viewHolder.invoice.getSuggestedMedium () == 0 ? Color.RED : Color.BLUE ) , suggestedLabel.length () + 3 , suggestedMedium.length () , 0 );
				
				// Display the big stock label
				viewHolder.stockMedium.setText ( TextUtils.concat ( ( usePicker ? ItemsUtils.getMediumDescription ( viewHolder.invoice.getUnit () ) : "" ) , ( usePicker ? newLine : "" ) , vehicleMedium , newLine , countedMedium , newLine , suggestedMedium ) , BufferType.SPANNABLE );
			} // End if
			else
				// Hide the medium UOM layout
				viewHolder.medium.setVisibility ( View.INVISIBLE );
			
			// Make sure the small UOM layout is visible
			viewHolder.small.setVisibility ( View.VISIBLE );
			// Display the unit price
			viewHolder.priceSmall.setText ( moneyFormat.format ( viewHolder.invoice.getPriceSmall () ) + " " + currency.getCurrencySymbol () );
			// Check if the picker is enabled
			if ( usePicker ) {
				// Clear the previous text watcher
				clearTextWatcher ( viewHolder.pickerSmall.getEditText () );
				// Display the small quantity in the picker
				viewHolder.pickerSmall.setNumber ( viewHolder.invoice.getQuantitySmall () );
				// Assign a new text watcher
				invoiceWatcher = new InvoiceWatcher ( (SalesInvoiceDetailsActivity) getContext () , convertView , viewHolder.invoice , InvoiceWatcher.Type.QUANTITY_SMALL );
				viewHolder.pickerSmall.getEditText ().addTextChangedListener ( invoiceWatcher );
				viewHolder.pickerSmall.getEditText ().setTag ( invoiceWatcher );
				// Clear the edit text focus
				checkEditTextFocus ( viewHolder.pickerSmall.getEditText () );
			} // End if
			else {
				// Determine the quantity
				double quantity = viewHolder.invoice.getSmall ( viewHolder.invoiceState );
				// Compute the small quantity label
				SpannableString quantitySmall = new SpannableString ( ItemsUtils.getSmallDescription ( viewHolder.invoice.getUnit () ) + " : " + quantity );
				// Apply foreground color span
				quantitySmall.setSpan ( new ForegroundColorSpan ( quantity == 0 ? Color.RED : Color.BLUE ) ,
						ItemsUtils.getSmallDescription ( viewHolder.invoice.getUnit () ).length () + 3 ,
						quantitySmall.length () ,
						0 );
				// Display the small quantity label
				viewHolder.quantitySmall.setText ( quantitySmall , BufferType.SPANNABLE );
			} // End else
			
			// Compute the small vehicle label
			SpannableString vehicleSmall = new SpannableString ( vehicleLabel + " : " + ( (int) viewHolder.invoice.getVehicleSmall () ) );
			// Apply foreground color span
			vehicleSmall.setSpan ( new ForegroundColorSpan ( viewHolder.invoice.getVehicleSmall () == 0 ? Color.RED : Color.BLUE ) , vehicleLabel.length () + 3 , vehicleSmall.length () , 0 );
			
			// Compute the counted small label
			SpannableString countedSmall = new SpannableString ( countedLabel + " : " + ( (int) viewHolder.invoice.getCountedSmall () ) );
			// Apply foreground color span
			countedSmall.setSpan ( new ForegroundColorSpan ( viewHolder.invoice.getCountedSmall () == 0 ? Color.RED : Color.BLUE ) , countedLabel.length () + 3 , countedSmall.length () , 0 );
			
			// Compute the suggested small label
			SpannableString suggestedSmall = new SpannableString ( suggestedLabel + " : " + ( (int) viewHolder.invoice.getSuggestedSmall () ) );
			// Apply foreground color span
			suggestedSmall.setSpan ( new ForegroundColorSpan ( viewHolder.invoice.getSuggestedSmall () == 0 ? Color.RED : Color.BLUE ) , suggestedLabel.length () + 3 , suggestedSmall.length () , 0 );
			
			// Display the big stock label
			viewHolder.stockSmall.setText ( TextUtils.concat ( ( usePicker ? ItemsUtils.getSmallDescription ( viewHolder.invoice.getUnit () ) : "" ) , ( usePicker ? newLine : "" ) , vehicleSmall , newLine , countedSmall , newLine , suggestedSmall ) , BufferType.SPANNABLE );
			
			// Determine if all UOMs are hidden
			if ( viewHolder.big.getVisibility () == View.INVISIBLE
					&& viewHolder.medium.getVisibility () == View.INVISIBLE
					&& viewHolder.small.getVisibility () == View.INVISIBLE )
				// Hide the sales order layout
				viewHolder.salesInvoice.setVisibility ( View.GONE );
			else
				// Make sure the sales order layout is visible
				viewHolder.salesInvoice.setVisibility ( View.VISIBLE );
			
	
			
			
			
			// Return the view
			return convertView;
		
//		} catch ( Exception exception ) {
//			// Do nothing
//			return new View ( getContext () );
//		} // End of try-catch block
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
		// Retrieve the view holder
		ViewHolder viewHolder = (ViewHolder) view.getTag ();
		// Perform manual item click call back
		( (SalesInvoiceDetailsActivity) getContext () ).onListItemClick ( 
				( (SalesInvoiceDetailsActivity) getContext () ).getListView () ,
				view ,
				( (SalesInvoiceDetailsActivity) getContext () ).getListView ().getPositionForView ( view ) ,
				getItemId ( viewHolder.position ) );
	}

	/*
	 * Called when the focus state of a view has changed.
	 *
	 * @see android.view.View.OnFocusChangeListener#onFocusChange(android.view.View, boolean)
	 */
	@Override
	public void onFocusChange ( final View view , boolean hasFocus ) {
		// Check if the view is and edit text
		if ( ! ( view instanceof EditText ) )
			// Invalid view
			return;
		try {
			// Schedule to run a thread after 250 milliseconds
			view.postDelayed ( new Runnable () {
				@Override
				public void run () {
					// Check if the edit text has focus AND the text is equal to zero
					if ( view.hasFocus () && ( (EditText) view ).getText ().toString ().equals ( "0" ) )
						// Clear the edit text
						( (EditText) view ).setText ( "" );
				}
			} , 250 );

		} catch ( Exception exception ) {
			// Do nothing
		} // End of try-catch block
	}
	
}