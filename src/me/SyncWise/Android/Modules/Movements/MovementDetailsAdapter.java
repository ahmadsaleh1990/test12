/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Movements;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.DateTime;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.ItemsUtils;
import me.SyncWise.Android.Database.MovementHeadersUtils;
 
import me.SyncWise.Android.Modules.SalesOrder.WarehouseQuantity;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

/**
 * A concrete BaseAdapter that is backed by an array of {@link me.SyncWise.Android.Modules.Movements.Movement Movement} objects.
 * 
 * @author Elias
 *
 */
public class MovementDetailsAdapter extends ArrayAdapter < Movement > {

	/**
	 * {@link android.view.LayoutInflater} used to instantiate a layout XML file into its corresponding View objects.
	 */
	private final LayoutInflater layoutInflater;
	
	/**
	 * XML Layout (layout) resource id.
	 */
	private final int layout;
	
	/**
	 * A {@link me.SyncWise.Android.Modules.Movements.MovementSettings MovementSettings} object.
	 */
	private final MovementSettings movementSettings;
	
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
		public TextView stockBig;
		public TextView stockMedium;
		public TextView stockSmall;
		public TextView mediumWarhouse;
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
	 * String used to host the expiry date label.
	 */
	private final String expiryDateLabel;
	
	 
	/**
	 * String used to host the not available label.
	 */
	private final String notAvailabelLabel;
	
	/**
	 * String used to hold the code label.
	 */
	protected final String codeLabel;
	
	/**
	 * String used to hold the suggested label.
	 */
	protected final String suggestedLabel;
	
	/**
	 * String used to hold the reason label.
	 */
	protected final String reasonLabel;
	
	/**
	 * Flag indicating if the items are enabled or not.
	 */
	private boolean itemsEnabled;
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param layout	XML Layout (layout) resource id.
	 * @param movementItems	List of {@link me.SyncWise.Android.Modules.Movements.Movement Movement} objects.
	 * @param movementSettings	A {@link me.SyncWise.Android.Modules.Movements.MovementSettings MovementSettings} object.
	 * @param currencyRounding	Integer used to indicate the currency rounding.
	 * @param itemsEnabled	Flag indicating if the items are enabled or not.
	 * @param allowZeroPrice	Flag indicating if the items with zero price are allowed or not.
	 */
	public MovementDetailsAdapter ( Context context , int layout , List < Movement > movementItems , MovementSettings movementSettings , final int currencyRounding , final boolean itemsEnabled ) {
		// Overloaded constructor
		this ( context , layout , movementItems , movementSettings , currencyRounding );
		// Initialize the attributes
		this.itemsEnabled = itemsEnabled;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param layout	XML Layout (layout) resource id.
	 * @param movementItems	List of {@link me.SyncWise.Android.Modules.Movements.Movement Movement} objects.
	 * @param movementSettings	A {@link me.SyncWise.Android.Modules.Movements.MovementSettings MovementSettings} object.
	 * @param currencyRounding	Integer used to indicate the currency rounding.
	 */
	public MovementDetailsAdapter ( Context context , int layout , List < Movement > movementItems , MovementSettings movementSettings , final int currencyRounding ) {
		// Superclass method call
		super ( context , layout , movementItems );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
		this.movementSettings = movementSettings;
		
		codeLabel = AppResources.getInstance ( context ).getString ( context , R.string.code_label );
		suggestedLabel = AppResources.getInstance ( context ).getString ( context , R.string.suggested_label );
		StringBuilder pattern = new StringBuilder ();
		pattern.append ( " #,##0" );
		if ( currencyRounding >= 1 ) {
			pattern.append ( ".0" );
			for ( int i = 1 ; i < currencyRounding ; i ++ )
				pattern.append ( "0" );
		} // End if
		moneyFormat = new DecimalFormat ( pattern.toString () );
	
		expiryDateLabel = AppResources.getInstance ( context ).getString ( context , R.string.expiry_date_label ) + " : ";
		notAvailabelLabel = AppResources.getInstance ( context ).getString ( context , R.string.not_available_abbreviation );
		reasonLabel = AppResources.getInstance ( context ).getString ( context , R.string.reason_label ) + " : ";
		itemsEnabled = true;
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
			// Retrieve a reference to the big stock label
			viewHolder.stockBig = (TextView) convertView.findViewById ( R.id.label_stock_big );
			// Retrieve a reference to the medium stock label
			viewHolder.stockMedium = (TextView) convertView.findViewById ( R.id.label_stock_medium );
			// Retrieve a reference to the small stock label
			viewHolder.stockSmall = (TextView) convertView.findViewById ( R.id.label_stock_small );
		
		//	viewHolder.mediumWarhouse=(TextView) convertView.findViewById(R.id.label_quantity_medium_warhouse);
			// Store the view holder as tag
			convertView.setTag ( viewHolder );
		} // End if
		else
			// An inflated view is already provided, retrieve the stored view holder
			viewHolder = (ViewHolder) convertView.getTag ();
		
		// Check if the suggestions are color coded AND if the view mode is disabled
		if ( movementSettings.isColorCodedSuggestions () && itemsEnabled ) {
			// Determine if the quantity is equal or greater to the suggestion
			if ( getItem ( position ).getQuantityBig () >= getItem ( position ).getSuggestedBig () 
					&& getItem ( position ).getQuantityMedium () >= getItem ( position ).getSuggestedMedium ()
					&& getItem ( position ).getQuantitySmall () >= getItem ( position ).getSuggestedSmall () ) {
				// Indicate that the suggestions are fully applied
				convertView.setBackgroundColor ( Color.GREEN );
				convertView.getBackground ().setAlpha ( 45 );
			} // End if
			// Determine if the quantity is 0
			else if ( getItem ( position ).getQuantityBig () == 0
					&& getItem ( position ).getQuantityMedium () == 0
					&& getItem ( position ).getQuantitySmall () == 0 ) {
				// Indicate that the suggestions are not applied
				convertView.setBackgroundColor ( Color.RED );
				convertView.getBackground ().setAlpha ( 45 );
			} // End if
			else {
				// Indicate that the suggestions are partially applied
				convertView.setBackgroundColor ( Color.YELLOW );
				convertView.getBackground ().setAlpha ( 45 );
			} // End else
		} // End if
		
		// Build a spannable string out of the item code in order to apply various spans
		SpannableString code = new SpannableString ( codeLabel + " : " + getItem ( position ).getItem ().getItemCode () );
		// Apply style span
		code.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , codeLabel.length () , 0 );
		// Apply color span
		code.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , codeLabel.length () , 0 );
		
		// Build a spannable string out of the expiry date in order to apply various spans
		SpannableString expiryDate = null;
		// Check if the expiry date is used
		if ( movementSettings.isUseExpiryDate () ) {
			// Build a spannable string out of the expiry date in order to apply various spans
			expiryDate = new SpannableString ( expiryDateLabel + ( getItem ( position ).getExpiryDate () == null ? notAvailabelLabel : DateTime.getBriefDate ( getContext () , getItem ( position ).getExpiryDate () ) ) );
			// Apply style span
			expiryDate.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , expiryDateLabel.length () , 0 );
			// Apply color span
			expiryDate.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , expiryDateLabel.length () , 0 );
		} // End if
		
		// Build a spannable string out of the suggested expiry date in order to apply various spans
		SpannableString suggestedExpiryDate = null;
		// Check if the suggested expiry date is used
		if ( movementSettings.isDisplaySuggestionedExpiryDate () ) {
			// Build a spannable string out of the suggested expiry date in order to apply various spans
			suggestedExpiryDate = new SpannableString ( suggestedLabel + " " + expiryDateLabel + ( getItem ( position ).getSuggestedExpiryDate () == null ? notAvailabelLabel : DateTime.getBriefDate ( getContext () , getItem ( position ).getSuggestedExpiryDate () ) ) );
			// Apply style span
			suggestedExpiryDate.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , suggestedLabel.length () + 1 + expiryDateLabel.length () , 0 );
			// Apply color span
			suggestedExpiryDate.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , suggestedLabel.length () + 1 + expiryDateLabel.length () , 0 );
		} // End if
		
		// Build a spannable string out of the reason in order to apply various spans
		SpannableString reason = null ;
		// Check if the reason is used
		if ( movementSettings.isUseReason () ) {
			// Build a spannable string out of the reason in order to apply various spans
			reason = new SpannableString ( reasonLabel + ( getItem ( position ).getReason () == null ? notAvailabelLabel : getItem ( position ).getReason ().getReasonName () ) );
			// Apply style span
			reason.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , reasonLabel.length () , 0 );
			// Apply color span
			reason.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , reasonLabel.length () , 0 );
		} // End if
		
		// Build a spannable string out of the suggested reason in order to apply various spans
		SpannableString suggestedReason = null ;
		// Check if the reason is used
		if ( movementSettings.isDisplaySuggestionedReason () ) {
			// Build a spannable string out of the suggested reason in order to apply various spans
			suggestedReason = new SpannableString ( suggestedLabel + " " + reasonLabel + ( getItem ( position ).getSuggestedReason () == null ? notAvailabelLabel : getItem ( position ).getSuggestedReason ().getReasonName () ) );
			// Apply style span
			suggestedReason.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , suggestedLabel.length () + 1 + reasonLabel.length () , 0 );
			// Apply color span
			suggestedReason.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , suggestedLabel.length () + 1 + reasonLabel.length () , 0 );
		} // End if
		
		// Display the item code
		CharSequence codeStr = code;
		if ( expiryDate != null )
			codeStr = TextUtils.concat ( codeStr , newLine , expiryDate );
		if ( suggestedExpiryDate != null )
			codeStr = TextUtils.concat ( codeStr , newLine , suggestedExpiryDate );
		if ( reason != null )
			codeStr = TextUtils.concat ( codeStr , newLine , reason );
		if ( suggestedReason != null )
			codeStr = TextUtils.concat ( codeStr , newLine , suggestedReason );
		viewHolder.code.setText ( codeStr , BufferType.SPANNABLE );
		WarehouseQuantity quantities = new WarehouseQuantity ( getItem ( position ).getItem ().getItemCode () );
		try {
			// Retrieve a reference to the warehouse quantity list
			ArrayList < WarehouseQuantity > warehouseQuantities = ( (MovementDetailsActivity) getContext () ).warehouseQuantities;
			// Search for the current item in the warehouse quantities
			int index = warehouseQuantities.indexOf ( quantities );
			// Check if the index is valid
			if ( index != -1 )
				// Retrieve the quantities
				quantities = warehouseQuantities.get ( index );
		} catch ( Exception exception ) {
			// Do nothing
		} // End of try-catch block
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
			// Check if the suggestion should be displayed
			if ( movementSettings.isDisplaySuggestions () ) 
				// Display the big suggestion label
				viewHolder.stockBig.setText ( suggestedLabel + " : " + getItem ( position ).getSuggestedBig () );
			
		} // End if
		else
			// Hide the big UOM layout
			viewHolder.big.setVisibility ( View.INVISIBLE );
		
		// Check if the current item has a medium UOM and a valid unit price
		if ( ItemsUtils.isMedium ( getItem ( position ).getItem () ) && ItemsUtils.isRegular ( getItem ( position ).getItem () ) ) {
			// Make sure the medium UOM layout is visible
			viewHolder.medium.setVisibility ( View.VISIBLE );
		//	String quantityLabel  = " Quantity: ";
			//if (movementSettings.getMovementType()==MovementHeadersUtils.Type.Physical_Direct_Load)
			// viewHolder.mediumWarhouse.setText(quantityLabel   , BufferType.SPANNABLE );
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
			String sugg="" ;
			SpannableString stockMedium = new SpannableString ("");
			// Check if the suggestion should be displayed
		 	if ( movementSettings.isDisplaySuggestions () ) 
				// Display the medium suggestion label
		 		sugg=suggestedLabel + " : " + getItem ( position ).getSuggestedMedium () ;
			if ( movementSettings.getMovementType()==MovementHeadersUtils.Type.Physical_Direct_Load ||
				 movementSettings.getMovementType()==MovementHeadersUtils.Type.PHYSICAL_DIRECT_UNLOAD ||
				 movementSettings.getMovementType()== MovementHeadersUtils.Type.LOAD_REQUEST)
			{
				// AppResources.getInstance ( (MovementDetailsActivity) getContext ()).getString ( (MovementDetailsActivity) getContext () , R.string.quantity_label );
				String quantityLabel  ="Quantity";
				stockMedium = new SpannableString (quantityLabel+ " : " + ( (int) quantities.getQuantityMedium () ) );
			    // Apply foreground color span
				stockMedium.setSpan ( new ForegroundColorSpan ( quantities.getQuantityMedium() == 0 ? Color.RED : Color.BLUE ) ,
				( quantityLabel ).length () + 3 , stockMedium.length () , 0 );
			 
				} 	
				// Display the small stock label
				viewHolder.stockMedium.setText (TextUtils.concat (sugg  , newLine ,stockMedium )  , BufferType.SPANNABLE );
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
			
			
			String sugg="" ;
			SpannableString stockSmall = new SpannableString ("");
			// Check if the suggestion should be displayed
			if ( movementSettings.isDisplaySuggestions () ) 
				// Display the small suggestion label
			//	viewHolder.stockSmall.setText (  );
				sugg=suggestedLabel + " : " + getItem ( position ).getSuggestedSmall ();
				if ( movementSettings.getMovementType() == MovementHeadersUtils.Type.Physical_Direct_Load ||
					 movementSettings.getMovementType() == MovementHeadersUtils.Type.PHYSICAL_DIRECT_UNLOAD ||
					 movementSettings.getMovementType()== MovementHeadersUtils.Type.LOAD_REQUEST)
				{
			 		 String quantityLabel  ="Quantity";
					 stockSmall = new SpannableString (quantityLabel+ " : " + ( (int) quantities.getQuantitySmall () ) );
		 
			// Apply foreground color span
		
			stockSmall.setSpan ( new ForegroundColorSpan ( quantities.getQuantitySmall () == 0 ? Color.RED : Color.BLUE ) ,
					( quantityLabel ).length () + 3 , stockSmall.length () , 0 );
		 
			} 	
			// Display the small stock label
			viewHolder.stockSmall.setText (TextUtils.concat (sugg  , newLine , stockSmall )  , BufferType.SPANNABLE );
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