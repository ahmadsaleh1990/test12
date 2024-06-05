/**
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.SalesOrder;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.Currencies;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.ItemsUtils;
import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Database.Reasons;
import me.SyncWise.Android.Database.ReasonsDao;
import me.SyncWise.Android.Database.ReasonsUtils;
import me.SyncWise.Android.Modules.SalesInvoice.SalesInvoiceDetailsAdapter.ViewHolder;
import me.SyncWise.Android.Utilities.ExternalStorageFilter;
import me.SyncWise.Android.Utilities.ItemCard;
import me.SyncWise.Android.Utilities.PictureFilter;
import me.SyncWise.Android.Widgets.TinyNumberPicker;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
 
import android.widget.TextView.BufferType;

/**
 * A concrete BaseAdapter that is backed by an array of {@link me.SyncWise.Android.Modules.SalesOrder.Order Order} objects.
 * 
 * @author  ahmad
 *
 */
public class SalesOrderDetailsAdapter extends ArrayAdapter < Order > implements OnClickListener , OnFocusChangeListener {

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
		public Order order;
		public int position;
		public ImageView icon;
		public TextView code;
		public TextView description;
		public ImageView arrow;
		public TextView taxDiscount;
		public TextView sequenceLable;
		public LinearLayout salesOrder;
		public LinearLayout big;
		public LinearLayout medium;
		public LinearLayout small;
		public LinearLayout free;
		public LinearLayout freeCase;
		public TextView quantityBig;
		public TextView quantityMedium;
		public TextView quantitySmall;
		public TextView quantityFree;
		public TextView quantityFreeCase;
		public TextView priceBig;
		public TextView priceMedium;
		public TextView priceSmall;
		public TextView priceFree;
		public TextView priceFreeCase;
		public TextView stockBig;
		public TextView stockMedium;
		public TextView stockSmall;
		public TinyNumberPicker pickerBig;
		public TinyNumberPicker pickerMedium;
		public TinyNumberPicker pickerSmall;
		public TinyNumberPicker pickerFree;
		public TinyNumberPicker pickerFreeCase;
		public TinyNumberPicker  PickerSequence;
		public CheckBox checkbox_av;
		public CheckBox checkbox_not_av;
		public int invoiceStates;
		public LinearLayout  layout_reason ;
		public TextView lable_reason ;
		public Spinner reason_spinner  ;
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
	 * String used to host the suggested label
	 */
	private final String suggestedLabel;
	/**
	 * String used to host the new line character.
	 */
	private final String newLine = "\n";
	
	/**
	 * String used to hold the code label.
	 */
	protected final String codeLabel;
	
	/**
	 * String used to host the quantity label.
	 */
	private final String quantityLabel;
	
	/**
	 * String used to host the total label.
	 */
	private final String totalLabel;
	
	/**
	 * String used to host the Item Barcode.
	 */
	private final String itemBarcode;
	
	/**
	 * String used to host the Client Item Code.
	 */
	private final String clientItemCode;
	
	 
	/**
	 * String used to host the discount label
	 */
	private final String discountLabel;
	
	/**
	 * String used to host the tax label
	 */
	private final String taxLabel;
	private final ExecutorService executorService;
	
	/**
	 * Constant integer holding the client icon size in pixels.
	 */
	private final int iconSize;
	/**
	 * String used to host the counted label
	 */
	private final String countedLabel;
	
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
	private final int type;
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param layout	XML Layout (layout) resource id.
	 * @param orderItems	List of {@link me.SyncWise.Android.Modules.SalesOrder.Order Order} objects.
	 * @param currency	{@link me.SyncWise.Android.Database.Currencies Currencies} object hosting the current sales order currency.
	 * @param itemsEnabled	Flag indicating if the items are enabled or not.
	 * @param usePicker	Flag indicating if the number picker should be enabled or not.
	 */
	 
	public SalesOrderDetailsAdapter ( Context context , int layout , List < Order > orderItems , final Currencies currency , final boolean itemsEnabled , final boolean usePicker,final int invoiceState ) {
		// Overloaded constructor
		this ( context , layout , orderItems , currency,  invoiceState );
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
	 * @param currency	{@link me.SyncWise.Android.Database.Currencies Currencies} object hosting the current sales order currency.
	 */
	private SalesOrderDetailsAdapter ( Context context , int layout , List < Order > orderItems , final Currencies currency ,final int invoiceState) {
		// Superclass method call
		super ( context , layout , orderItems );
		// Initialize the attributes
		layoutInflater = LayoutInflater.from ( context );
		this.layout = layout;
		this.currency = currency;
		codeLabel = AppResources.getInstance ( context ).getString ( context , R.string.code_label );
		this.type = invoiceState;
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
		quantityLabel = AppResources.getInstance ( context ).getString ( context , R.string.quantity_label );
		totalLabel = AppResources.getInstance ( context ).getString ( context , R.string.total_label );
		itemBarcode= AppResources.getInstance ( context ).getString ( context , R.string.item_barcode_label );
		clientItemCode=AppResources.getInstance ( context ).getString ( context , R.string.client_ItemCode_label );
		discountLabel = AppResources.getInstance ( context ).getString ( context , R.string.discount_label );
		taxLabel = AppResources.getInstance ( getContext () ).getString ( getContext () , R.string.tax_label );
		countedLabel = AppResources.getInstance ( getContext () ).getString ( getContext () , R.string.counted_label );
		suggestedLabel = AppResources.getInstance ( getContext () ).getString ( getContext () , R.string.suggested_label1 );
		itemsEnabled = true;
		executorService = Executors.newFixedThreadPool ( 5 );
		iconSize = (int) TypedValue.applyDimension ( TypedValue.COMPLEX_UNIT_DIP , 120 , context.getResources ().getDisplayMetrics () );

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
	
	public String getItemBarcode() {
		return itemBarcode;
	}

	public String getClientItemCode() {
		return clientItemCode;
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
	 * Refreshes the order list item displayed data.
	 * 
	 * @param context	The application context.
	 * @param view	A {@link android.view.View View} reference of the list item that should be refreshed.
	 * @param _moneyFormat	A {@link java.text.DecimalFormat DecimalFormat} object used to format and display monetary values.
	 * @param currency	{@link me.SyncWise.Android.Database.Currencies Currencies} object hosting the current sales order currency.
	 * @param _codeLabel	String used to hold the code label. <em>(Optional)</em>
	 * @param _totalLabel	String used to host the total label. <em>(Optional)</em>
	 * @param _newLine	String used to host the new line character. <em>(Optional)</em>
	 * @param _brownColor	Integer hosting the brown color. <em>(Optional)</em>
	 * @param _mistyRose	Integer holding the misty rose color <em>(Optional)</em>
	 */
	public static void refreshOrderView ( final Context context , final View view , final DecimalFormat _moneyFormat , final Currencies currency , final String _codeLabel , final String _totalLabel,final String _itemBarcode,final String _clientItemCode , final String _newLine , final Integer _brownColor , final Integer _mistyRose,int type1 ) {
		// Retrieve the stored view holder
		ViewHolder viewHolder = (ViewHolder) view.getTag ();
		
		// Declare and initialize the needed strings and colors
		String codeLabel = _codeLabel == null ? AppResources.getInstance ( context ).getString ( context , R.string.code_label ) : _codeLabel;
		String totalLabel = _totalLabel == null ? AppResources.getInstance ( context ).getString ( context , R.string.total_label ) : _totalLabel;
		String itemBarcode=_itemBarcode==null? AppResources.getInstance ( context ).getString ( context , R.string.item_barcode_label ) : _itemBarcode;
		String clientItemCode=_clientItemCode==null? AppResources.getInstance ( context ).getString ( context , R.string.client_ItemCode_label ) : _clientItemCode;
		String newLine = _newLine == null ? "\n" : _newLine;
		Integer brownColor = _brownColor == null ? context.getResources ().getColor ( R.color.Brown ) : _brownColor;
		Integer mistyRose = _mistyRose == null ? context.getResources ().getColor ( R.color.MistyRose ) : _mistyRose;
		boolean isMSLConfirmationRequired = PermissionsUtils.getEnforceMSLConfirmation (  context , DatabaseUtils.getCurrentUserCode(context) , DatabaseUtils.getCurrentCompanyCode(context) );
		
	
		// Declare and initialize a warehouse quantity object
		WarehouseQuantity quantities = new WarehouseQuantity ( viewHolder.order.getItem ().getItemCode () );
		try {
			// Retrieve a reference to the warehouse quantity list
			ArrayList < WarehouseQuantity > warehouseQuantities = ( (SalesOrderDetailsActivity)context).warehouseQuantities;
			// Search for the current item in the warehouse quantities
			int index = warehouseQuantities.indexOf ( quantities );
			// Check if the index is valid
			if ( index != -1 )
				// Retrieve the quantities
				quantities = warehouseQuantities.get ( index );
		} catch ( Exception exception ) {
			// Do nothing
		} // End of try-catch block
		
		// Check if the current item belongs to the must stock list
		// AND is not confirmed 
		if( isMSLConfirmationRequired ){
		// AND has no color background
		if ( viewHolder.order.isMustStockList () &&   !viewHolder.order.isConfirmed () )
			// Modify the raw background to indicate that the current must stock item is not confirmed
			view.setBackgroundColor ( mistyRose );
		else if ( viewHolder.order.isMustStockList () &&    viewHolder.order.isConfirmed () )
			// Modify the raw background to indicate that the current must stock item is not confirmed
			view.setBackgroundColor ( 0 );
		else{
			Integer greenYellow3 = Color.rgb( 204, 229, 255);
			Integer greenYellow = Color.rgb( 255, 204, 229); //  getContext().getResources ().getColor ( R.color.DodgerBlue )  ;
			Integer yellowColor = Color.rgb(255, 229, 204);//  getContext ().getResources ().getColor ( R.color.Green ) ;
			if (viewHolder.order.isNewSKUList()   )
				// Modify the raw background to indicate that the current must stock item is not confirmed
				view.setBackgroundColor ( greenYellow3 );
			else if (viewHolder.order.getItem().getIsHighlight()!=null && viewHolder.order.getItem().getIsHighlight()==1 )
				// Modify the raw background to indicate that the current must stock item is not confirmed
				view.setBackgroundColor ( greenYellow );
			else 	if( quantities.getQuantityMedium  ()==0 && quantities.getQuantitySmall()  ==0)
				view.setBackgroundColor ( yellowColor );
			else
				// Otherwise remove the raw background
				view.setBackgroundColor ( 0 );
		}	
		}else{
			if ( viewHolder.order.isMustStockList () && ! viewHolder.order.isConfirmed () )
				// Modify the raw background to indicate that the current must stock item is not confirmed
				view.setBackgroundColor (   0 );
			else if(viewHolder.order.isMustStockList () &&   viewHolder.order.isConfirmed ())
				view.setBackgroundColor ( mistyRose );
			else{
				Integer greenYellow = Color.rgb( 255, 204, 229); //  getContext().getResources ().getColor ( R.color.DodgerBlue )  ;
				Integer yellowColor = Color.rgb(255, 229, 204);//  getContext ().getResources ().getColor ( R.color.Green ) ;
				Integer greenYellow3 = Color.rgb( 204, 229, 255);
		      if (viewHolder.order.isNewSKUList()   )
					// Modify the raw background to indicate that the current must stock item is not confirmed
					view.setBackgroundColor ( greenYellow3 );
				else
				if (viewHolder.order.getItem().getIsHighlight()!=null && viewHolder.order.getItem().getIsHighlight()==1 )
					// Modify the raw background to indicate that the current must stock item is not confirmed
					view.setBackgroundColor ( greenYellow );
				else 	if( quantities.getQuantityMedium  ()==0 && quantities.getQuantitySmall()  ==0)
					view.setBackgroundColor ( yellowColor );
				else
					// Otherwise remove the raw background
					view.setBackgroundColor ( 0 );
			}	
		}
//		else
//			// Otherwise remove the raw background
//			view.setBackgroundColor ( 0 );
		
		// Build a spannable string out of the item code in order to apply various spans
		SpannableString code = new SpannableString ( codeLabel + " : " + viewHolder.order.getItem ().getItemCode () );
		// Apply style span
		code.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , codeLabel.length () , 0 );
		// Apply color span
		code.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , codeLabel.length () , 0 );
		
		// Compute the net value
		double netValue = viewHolder.order.getQuantityBig () * viewHolder.order.getPriceBig () 
				+ viewHolder.order.getQuantityMedium () * viewHolder.order.getPriceMedium ()
				+ viewHolder.order.getQuantitySmall () * viewHolder.order.getPriceSmall ()
				+
				viewHolder.order.getBasketBig () * viewHolder.order.getPriceBig () 
				+ viewHolder.order.getBasketMedium () * viewHolder.order.getPriceMedium ()
				+ viewHolder.order.getBasketSmall () * viewHolder.order.getPriceSmall ();
		// Compute the calculated value
		double calculatedValue = netValue * ( ( 100 - viewHolder.order.getDiscountPercentage ()+viewHolder.order.getPromotionDiscountPercentageBasket() ) / 100 )
				- viewHolder.order.getDiscountAmount ();
		// Compute the taxes
		double taxesAmount = calculatedValue * viewHolder.order.getTax () / 100;
		
		double taxeAmount=viewHolder.order.getBasketMedium()*viewHolder.order.getCaseTaxAmount()
				 
				+ viewHolder.order.getBasketSmall (  )*viewHolder.order.getUnitTaxAmount()  
				+ viewHolder.order.getQuantityMedium () * viewHolder.order.getCaseTaxAmount ()
				+ viewHolder.order.getQuantitySmall () * viewHolder.order.getUnitTaxAmount ()
				+ (viewHolder.order.appFree? viewHolder.order.getFreeMedium()   * viewHolder.order.getCaseTaxAmount ()
				+ viewHolder.order.getFreeSmall()   * viewHolder.order.getUnitTaxAmount ()
				+ viewHolder.order.getQuantityFreeCase()   * viewHolder.order.getCaseTaxAmount ()
				+ viewHolder.order.getQuantityFree()    * viewHolder.order.getUnitTaxAmount ():0);
		// Compute the total amount 
		double _totalAmount = calculatedValue + taxesAmount+taxeAmount;
		
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
		//viewHolder.code.setText ( TextUtils.concat ( code , newLine , totalAmount ) , BufferType.SPANNABLE );
	
		
		
		
		 
		 
		SpannableString itemBarcodes = new SpannableString ( itemBarcode + " : " + viewHolder.order.getItemBarcodes()   );
		// Apply style span
		itemBarcodes.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , itemBarcode.length () , 0 );
		// Apply color span
		itemBarcodes.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , itemBarcode.length () , 0 );
	 
		SpannableString clientItemCodes = new SpannableString ( clientItemCode + " : " + viewHolder.order.getClientItemCode()   );
		// Apply style span
		clientItemCodes.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , clientItemCode.length () , 0 );
		// Apply color span
		clientItemCodes.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , clientItemCode.length () , 0 );
		
		 
		if(viewHolder.invoiceStates ==1|| viewHolder.invoiceStates ==3){
		// Display the item code, total amount, item barcode and clientItemCode
		viewHolder.code.setText ( TextUtils.concat ( code , newLine , totalAmount , newLine , itemBarcodes, newLine , clientItemCodes ) , BufferType.SPANNABLE );
		}else 
			viewHolder.code.setText ( TextUtils.concat ( code , newLine , "Total: 0" , newLine , itemBarcodes, newLine  ) , BufferType.SPANNABLE );
		
		
	//	if(  viewHolder.order.getMaxQuantityFreeCase()  < 0  );
		//{
     
		if( viewHolder.order.getMaxQuantityFreeCase()==0)
		{
			String s =  " Free Case " +   viewHolder.order.getMaxQuantityFreeCase() ;
	        SpannableString freeCase = new SpannableString ( s );//+   viewHolder.order.getMaxQuantityFreeCase()
// 			// Apply style span
			freeCase.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , s.length() , 0 );
 			// Apply color span
     		freeCase.setSpan ( new ForegroundColorSpan ( Color.BLUE ) , 0 , s.length() , 0 );
     	//	Toast.makeText(view.getContext(),  ""+viewHolder.order.getMaxQuantityFreeCase(), Toast.LENGTH_LONG).show();
    		viewHolder.priceFreeCase.setText( TextUtils.concat ( freeCase  ) , BufferType.SPANNABLE );
		}
		else
		{
		if( viewHolder.order.getQuantityFreeCase() > viewHolder.order.getMaxQuantityFreeCase()   ){
		//if ( viewHolder.order.isMax() ){
	        String s =  " Free Case " +   viewHolder.order.getMaxQuantityFreeCase() ;
	        SpannableString freeCase = new SpannableString ( s );//+   viewHolder.order.getMaxQuantityFreeCase()
// 			// Apply style span
			freeCase.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , s.length() , 0 );
 			// Apply color span
     		freeCase.setSpan ( new ForegroundColorSpan ( Color.BLUE ) , 0 , s.length() , 0 );
     	//	Toast.makeText(view.getContext(),  ""+viewHolder.order.getMaxQuantityFreeCase(), Toast.LENGTH_LONG).show();
    		viewHolder.priceFreeCase.setText( TextUtils.concat ( freeCase  ) , BufferType.SPANNABLE );
 
		}
		else{
			String s =  " Free Case "  ;
			 SpannableString freeCase = new SpannableString (s);//+  viewHolder.order.getMaxQuantityFreeCase() 
			// Apply style span
		     freeCase.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 ,s.length() , 0 );
			// Apply color span
			 freeCase.setSpan ( new ForegroundColorSpan ( Color.RED ) , 0 , s.length() , 0 );
			
    		 viewHolder.priceFreeCase.setText(TextUtils.concat ( freeCase) , BufferType.SPANNABLE );

		}
		}
		if( viewHolder.order.getMaxQuantityFreeUnit()==0)
	 
			{
			String s = " Free Unit   "+   viewHolder.order.getMaxQuantityFreeUnit() ;
			SpannableString freeCase = new SpannableString (s  );//+   viewHolder.order.getMaxQuantityFreeCase()
			// Apply style span
			freeCase.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , s.length () , 0 );
			// Apply color span
			freeCase.setSpan ( new ForegroundColorSpan ( Color.BLUE ) , 0 , s.length () , 0 );
			//	Toast.makeText(view.getContext(),  ""+viewHolder.order.getMaxQuantityFreeCase(), Toast.LENGTH_LONG).show();
			viewHolder.priceFree.setText( TextUtils.concat ( freeCase  ) , BufferType.SPANNABLE );
			} else{
			if( viewHolder.order.getQuantityFree () > viewHolder.order.getMaxQuantityFreeUnit()  ){
			//if ( viewHolder.order.isMax() ){
					String s = " Free Unit   "+   viewHolder.order.getMaxQuantityFreeUnit() ;
					SpannableString freeCase = new SpannableString (s  );//+   viewHolder.order.getMaxQuantityFreeCase()
					// Apply style span
					freeCase.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , s.length () , 0 );
					// Apply color span
					freeCase.setSpan ( new ForegroundColorSpan ( Color.BLUE ) , 0 , s.length () , 0 );
				//	Toast.makeText(view.getContext(),  ""+viewHolder.order.getMaxQuantityFreeCase(), Toast.LENGTH_LONG).show();
					viewHolder.priceFree.setText( TextUtils.concat ( freeCase  ) , BufferType.SPANNABLE );
			
			}else{
				String s =  " Free Unit   ";
				 SpannableString freeCase = new SpannableString (s );//+  viewHolder.order.getMaxQuantityFreeCase() 
				
				 // Apply style span
			     freeCase.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , s.length () , 0 );
				// Apply color span
				 freeCase.setSpan ( new ForegroundColorSpan ( Color.RED ) , 0 , s.length () , 0 );
				
				 viewHolder.priceFree.setText(TextUtils.concat ( freeCase) , BufferType.SPANNABLE );
			
			}
			}
			
		
			viewHolder.priceSmall.setText(_moneyFormat.format (viewHolder.order.getPriceSmall())+ " " + currency.getCurrencySymbol ());
			viewHolder.priceMedium.setText(_moneyFormat.format (viewHolder.order.getPriceMedium())+ " " + currency.getCurrencySymbol ());

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
			final ViewHolder viewHolder;
			// Check if an inflated view is provided
			if ( convertView == null ) {
				// A new view must be inflated
				convertView = layoutInflater.inflate ( layout , parent ,false );
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
				viewHolder.sequenceLable = (TextView) convertView.findViewById ( R.id.label_item_batch );
				
				// Retrieve a reference to the sales order layout
				viewHolder.salesOrder = (LinearLayout) convertView.findViewById ( R.id.layout_sales_order );
				// Retrieve a reference to the big unit of measurement layout
				viewHolder.big = (LinearLayout) convertView.findViewById ( R.id.layout_big_uom );
				// Retrieve a reference to the big unit of measurement layout
				viewHolder.medium = (LinearLayout) convertView.findViewById ( R.id.layout_medium_uom );
				// Retrieve a reference to the big unit of measurement layout
				viewHolder.small = (LinearLayout) convertView.findViewById ( R.id.layout_small_uom );
				viewHolder.free = (LinearLayout) convertView.findViewById ( R.id.layout_free_uom );
				
				
				viewHolder.freeCase = (LinearLayout) convertView.findViewById ( R.id.layout_free_uom_case );
				 
				
				// Display the free quantity layout
				viewHolder.free.setVisibility ( View.VISIBLE );
				viewHolder.freeCase.setVisibility(View.VISIBLE);
				
				// Retrieve a reference to the big quantity label
				viewHolder.quantityBig = (TextView) convertView.findViewById ( R.id.label_quantity_big );
				// Retrieve a reference to the medium quantity label
				viewHolder.quantityMedium = (TextView) convertView.findViewById ( R.id.label_quantity_medium );
				// Retrieve a reference to the small quantity label
				viewHolder.quantitySmall = (TextView) convertView.findViewById ( R.id.label_quantity_small );
				viewHolder.quantityFree = (TextView) convertView.findViewById ( R.id.label_quantity_free );
				viewHolder.quantityFreeCase=(TextView) convertView.findViewById ( R.id.label_quantity_free_case );
				
				// Retrieve a reference to the big price label
				viewHolder.priceBig = (TextView) convertView.findViewById ( R.id.label_price_big );
				// Retrieve a reference to the medium price label
				viewHolder.priceMedium = (TextView) convertView.findViewById ( R.id.label_price_medium );
				// Retrieve a reference to the small price label
				viewHolder.priceSmall = (TextView) convertView.findViewById ( R.id.label_price_small );
				viewHolder.priceFree = (TextView) convertView.findViewById ( R.id.label_price_free );
				viewHolder.priceFreeCase = (TextView) convertView.findViewById ( R.id.label_price_free_case );
				// Retrieve a reference to the big tiny number picker
				viewHolder.pickerBig = (TinyNumberPicker) convertView.findViewById ( R.id.tiny_number_picker_quantity_big );
				// Retrieve a reference to the medium tiny number picker
				viewHolder.pickerMedium = (TinyNumberPicker) convertView.findViewById ( R.id.tiny_number_picker_quantity_medium );
				// Retrieve a reference to the small tiny number picker
				viewHolder.pickerSmall = (TinyNumberPicker) convertView.findViewById ( R.id.tiny_number_picker_quantity_small );
				// Retrieve a reference to the small tiny number picker
				viewHolder.pickerFree = (TinyNumberPicker) convertView.findViewById ( R.id.tiny_number_picker_quantity_free );
				// Retrieve a reference to the small tiny number picker
				viewHolder.pickerFreeCase = (TinyNumberPicker) convertView.findViewById ( R.id.tiny_number_picker_quantity_free_case );
				viewHolder.PickerSequence = (TinyNumberPicker) convertView.findViewById ( R.id.tiny_number_picker_sequence );
	
				
				viewHolder.layout_reason = (LinearLayout) convertView.findViewById ( R.id.layout_reason );
				viewHolder.lable_reason = (TextView) convertView.findViewById ( R.id.lable_reason );
				viewHolder.reason_spinner = (Spinner) convertView.findViewById ( R.id.reason_spinner );
			
				// Retrieve a reference to the big stock label
				viewHolder.stockBig = (TextView) convertView.findViewById ( R.id.label_stock_big );
				// Retrieve a reference to the medium stock label
				viewHolder.stockMedium = (TextView) convertView.findViewById ( R.id.label_stock_medium );
				// Retrieve a reference to the small stock label
				viewHolder.stockSmall = (TextView) convertView.findViewById ( R.id.label_stock_small );
				
				viewHolder.checkbox_av = (CheckBox) convertView.findViewById(R.id.checkbox_available);
				viewHolder.checkbox_not_av = (CheckBox) convertView.findViewById(R.id.checkbox_not_available);
			
				
				
				// Display / hide the quantity labels
				int labelVisibility = usePicker ? View.GONE : View.VISIBLE;
				viewHolder.quantityBig.setVisibility ( labelVisibility );
				viewHolder.quantityMedium.setVisibility ( labelVisibility );
				viewHolder.quantitySmall.setVisibility ( labelVisibility );
				viewHolder.quantityFree.setVisibility ( labelVisibility );
				viewHolder.quantityFreeCase.setVisibility ( labelVisibility );
	
				// Display / hide the tiny number pickers
				int pickerVisibility = usePicker ? View.VISIBLE : View.GONE;
				viewHolder.pickerBig.setVisibility ( View.GONE );
				viewHolder.pickerMedium.setVisibility ( pickerVisibility );
				viewHolder.pickerSmall.setVisibility ( pickerVisibility );
				viewHolder.pickerFree.setVisibility ( pickerVisibility );
				viewHolder.pickerFreeCase.setVisibility ( pickerVisibility );
				if(PermissionsUtils.getDisplaySequenceChange ( getContext() , DatabaseUtils.getCurrentUserCode(getContext()) , DatabaseUtils.getCurrentCompanyCode(getContext() )  )){
				  viewHolder.PickerSequence .setVisibility ( pickerVisibility );
				viewHolder.sequenceLable.setVisibility ( pickerVisibility );
				}else{
					  viewHolder.PickerSequence .setVisibility ( View.GONE );
						viewHolder.sequenceLable.setVisibility ( View.GONE );
				}
				// Determine if the tiny number pickers are used
				if ( usePicker ) {
					viewHolder.pickerBig.getEditText ().setOnFocusChangeListener ( this );
					viewHolder.pickerMedium.getEditText ().setOnFocusChangeListener ( this );
					viewHolder.pickerSmall.getEditText ().setOnFocusChangeListener ( this );
					viewHolder.pickerFree.getEditText ().setOnFocusChangeListener ( this );
					viewHolder.pickerFreeCase.getEditText ().setOnFocusChangeListener ( this );
					viewHolder.PickerSequence.getEditText ().setOnFocusChangeListener ( this );
				} // End if
				
				// Store the view holder as tag
				convertView.setTag ( viewHolder );
			} // End if
			else
				// An inflated view is already provided, retrieve the stored view holder
				viewHolder = (ViewHolder) convertView.getTag ();
	
			// Set the current order
			viewHolder.order = getItem ( position );
			viewHolder.invoiceStates = this.type;
			ArrayList < Reasons > reasons = new ArrayList < Reasons > ();
			reasons.add ( new Reasons ( null , null , null , "----------" , null , null , null , null , null , null ) );
				// Set the reason spinner adapter
			if(viewHolder.order.isMustStockList() )
			reasons .addAll ( DatabaseUtils.getInstance ( getContext() ).getDaoSession ().getReasonsDao ().queryBuilder ()
					.where ( ReasonsDao.Properties.ReasonType.eq ( ReasonsUtils.Type.MSL )
							 ).list () );
			else if(viewHolder.order.isNewSKUList()  )
				reasons .addAll ( DatabaseUtils.getInstance ( getContext() ).getDaoSession ().getReasonsDao ().queryBuilder ()
						.where ( ReasonsDao.Properties.ReasonType.eq ( ReasonsUtils.Type.NEWSKU )
								 ).list () );	
			viewHolder.reason_spinner.setAdapter ( new ArrayAdapter < Reasons > ( getContext() , android.R.layout.simple_list_item_1 , reasons ) );

			
//			viewHolder.PickerSequence.getEditText().addTextChangedListener(new TextWatcher() {
//
//	                @Override
//	                public void onTextChanged(CharSequence s, int start,
//	                        int before, int count) {
//	           
//	                }
//
//	                @Override
//	                public void beforeTextChanged(CharSequence s, int start,
//	                        int count, int after) {
//
//	                }
//
//	                @Override
//	                public void afterTextChanged(Editable s) {
//	                	viewHolder.order.setSequence( (int) getNumber ( s.toString () ) );
//	                }
//	            });
			// Set the current position
			viewHolder.position = position;
			
			final Order finalOrder = viewHolder.order;
	 
			if(viewHolder.order.isMustStockList() && usePicker){
				viewHolder.layout_reason.setVisibility ( View.VISIBLE ); 
				viewHolder.lable_reason .setVisibility (  View.VISIBLE  ); 
				viewHolder.reason_spinner.setVisibility (  View.VISIBLE  );
				viewHolder.reason_spinner.setEnabled(true)  ;
			}else if(viewHolder.order.isMustStockList() && !usePicker){
				viewHolder.layout_reason.setVisibility ( View.VISIBLE ); 
				viewHolder.lable_reason .setVisibility (  View.VISIBLE  ); 
				viewHolder.reason_spinner.setVisibility (  View.VISIBLE  );
				viewHolder.reason_spinner.setEnabled(false)  ;
				}
			else if(viewHolder.order.isNewSKUList()   && usePicker){
				viewHolder.layout_reason.setVisibility ( View.VISIBLE ); 
				viewHolder.lable_reason .setVisibility (  View.VISIBLE  ); 
				viewHolder.reason_spinner.setVisibility (  View.VISIBLE  );
				viewHolder.reason_spinner.setEnabled(true)  ;
			}else if(viewHolder.order.isNewSKUList() && !usePicker){
				viewHolder.layout_reason.setVisibility ( View.VISIBLE ); 
				viewHolder.lable_reason .setVisibility (  View.VISIBLE  ); 
				viewHolder.reason_spinner.setVisibility (  View.VISIBLE  );
				viewHolder.reason_spinner.setEnabled(false)  ;
				}
			else{
				
				viewHolder.layout_reason.setVisibility ( View.VISIBLE ); 
				viewHolder.lable_reason.setVisibility (  View.VISIBLE  ); 
				viewHolder.reason_spinner.setVisibility (  View.INVISIBLE  );
		 
				
			}
viewHolder.reason_spinner.setSelection(viewHolder.order.getReasonPos()) ;
			
			viewHolder.reason_spinner.setOnItemSelectedListener ( new OnItemSelectedListener () {
 
				@Override
				public void onItemSelected ( AdapterView < ? > parent , View view , int position , long id ) {
					// Check if a reason is selected
					if ( viewHolder.reason_spinner.getSelectedItemPosition () == 0 ) {
					 	viewHolder.order.setReasonCode(null) ;
						viewHolder.order.setReasonPos( viewHolder.reason_spinner.getSelectedItemPosition () );
						
					 	return;
					}  
					viewHolder.order.setReasonCode(( (Reasons) viewHolder.reason_spinner.getSelectedItem () ).getReasonCode ());

		 
					viewHolder.order.setReasonPos( viewHolder.reason_spinner.getSelectedItemPosition () );
				}

				/*
				 * Callback method to be invoked when the selection disappears from this view.
				 *
				 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android.widget.AdapterView)
				 */
				@Override
				public void onNothingSelected ( AdapterView < ? > parent ) {
					// Do nothing
				}
			} );

			
			
			final CheckBox checkboxAvailable = viewHolder.checkbox_av;
			final CheckBox checkboxNotAvailable = viewHolder.checkbox_not_av;
			viewHolder.checkbox_av.setOnCheckedChangeListener ( null );
			viewHolder.checkbox_not_av.setOnCheckedChangeListener ( null );
			viewHolder.checkbox_av.setChecked ( viewHolder.order.availability == null || viewHolder.order.availability == false ? false : true );
			viewHolder.checkbox_not_av.setChecked ( viewHolder.order.availability == null || viewHolder.order.availability == true ? false : true );
			viewHolder.checkbox_av.setOnCheckedChangeListener ( new OnCheckedChangeListener () {
				@Override
				public void onCheckedChanged ( CompoundButton buttonView , boolean isChecked ) {
					if ( isChecked ) {
						if ( checkboxNotAvailable.isChecked () )
							checkboxNotAvailable.setChecked ( false );
						finalOrder.availability = true;
					}
					else {
						finalOrder.availability = null;
					}
				}
			} );
			viewHolder.checkbox_not_av.setOnCheckedChangeListener ( new OnCheckedChangeListener () {
				@Override
				public void onCheckedChanged ( CompoundButton buttonView , boolean isChecked ) {
					if ( isChecked ) {
						if ( checkboxAvailable.isChecked () )
							checkboxAvailable.setChecked ( false );
			     			finalOrder.availability = false;
					}
					else {
						    finalOrder.availability = null;
					}
				}
			} );
			
			
			// Declare and initialize an order watcher
			OrderWatcher orderWatcher = null;
			
//			viewHolder.star_icon.setVisibility(View.GONE);
//			if(_promoD != null && _promoD.containsKey(getItem ( position ).getItem ().getItemCode ())){
//				ArrayList< PromotionDetails > tempPromo = _promoD.get(getItem ( position ).getItem ().getItemCode ());
//				for(int i = 0; i<tempPromo.size() ; i++){
//				if(_promoH.get(tempPromo.get( i ).getPromotionID()).getPromotionType().equals(PromotionUtils.Type.INSTANT_PROMOTION)){
//					viewHolder.ins_p_icon.setVisibility(View.VISIBLE);
//				}
//				else if (_promoH.get(tempPromo.get( i ).getPromotionID()).getPromotionType().equals(PromotionUtils.Type.BASKET_PROMOTION)){
//					viewHolder.bas_p_icon.setVisibility(View.VISIBLE);
//				}
//				else {
//					viewHolder.ins_p_icon.setVisibility(View.GONE);
//					viewHolder.bas_p_icon.setVisibility(View.GONE);
//					viewHolder.free_p_icon.setVisibility(View.GONE);
//				}
//				}
//			}
//			else if ( getItem ( position ).getQuantityFree () > 0 ){
//				viewHolder.free_p_icon.setVisibility(View.VISIBLE);
//				viewHolder.ins_p_icon.setVisibility(View.GONE);
//				viewHolder.bas_p_icon.setVisibility(View.GONE);
//			}
//			else{
//				viewHolder.ins_p_icon.setVisibility(View.GONE);
//				viewHolder.bas_p_icon.setVisibility(View.GONE);
//				viewHolder.free_p_icon.setVisibility(View.GONE);
//			}
			
			// Declare and initialize a warehouse quantity object
			WarehouseQuantity quantities = new WarehouseQuantity ( getItem ( position ).getItem ().getItemCode () );
			try {
				// Retrieve a reference to the warehouse quantity list
				ArrayList < WarehouseQuantity > warehouseQuantities = ( (SalesOrderDetailsActivity) getContext () ).warehouseQuantities;
				// Search for the current item in the warehouse quantities
				int index = warehouseQuantities.indexOf ( quantities );
				// Check if the index is valid
				if ( index != -1 )
					// Retrieve the quantities
					quantities = warehouseQuantities.get ( index );
			} catch ( Exception exception ) {
				// Do nothing
			} // End of try-catch block
//			// Check if the current item has a minimum quantity
//			if ( quantities.getMinimumQuantity () > 0 ) {
//				// Compute the current total number of available quantities in the warehouse
//				double totalWarehouse = quantities.getQuantityBig () * getItem ( position ).getItem ().getUnitBigMedium ()
//						+ quantities.getQuantityMedium () * getItem ( position ).getItem ().getUnitMediumSmall ()
//						+ quantities.getQuantitySmall ();
//				// Compute the current total number of ordered quantities
//				double totalOrder = getItem ( position ).getQuantityBig () * getItem ( position ).getItem ().getUnitBigMedium ()
//						+ getItem ( position ).getQuantityMedium () * getItem ( position ).getItem ().getUnitMediumSmall ()
//						+ getItem ( position ).getQuantitySmall ();
//				// Check if the remaining quantity is less than the minimum quantity
//				if ( ( totalWarehouse - totalOrder ) < quantities.getMinimumQuantity () )
//					// Modify the raw background to indicate the minimum quantity is reached
//					convertView.setBackgroundColor ( mistyRose );
//				else
//					// Remove the raw background
//					convertView.setBackgroundColor ( 0 );
//			} // End if
//			else
//				// Remove the raw background
//				convertView.setBackgroundColor ( 0 );
			
			
			
			
			
			
			// Refresh the order view
			refreshOrderView ( getContext () , convertView , moneyFormat , currency , codeLabel , totalLabel , itemBarcode, clientItemCode, newLine , brownColor , mistyRose,type );
			
			// Set the description text
			viewHolder.description.setText ( getItem ( position ).getItem ().getItemName () );
			// Display / hide the arrow accordingly
			viewHolder.arrow.setVisibility ( itemsEnabled ? View.VISIBLE : View.GONE );
			// Check if the current item is taxable or has discount
			if (getItem ( position ).getUnitTaxAmount() !=0 ||  getItem ( position ).getTax () != 0 || getItem ( position ).getDetailDiscountPercentage () != 0 ) {
				// Make sure the tax label
				viewHolder.taxDiscount.setVisibility ( View.VISIBLE );
				String percentageSign = AppResources.getInstance ( getContext () ).getString ( getContext () , R.string.percentage_sign );
				// Compute the tax label
				SpannableString tax = null;
				// Check if the current item is taxable
//				if ( getItem ( position ).getTax () != 0 ) {
//					// Compute the tax label
//					tax = new SpannableString ( taxLabel + " : " + getItem ( position ).getTax () + percentageSign );
//					// Apply foreground color span
//					tax.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , taxLabel.length () , 0 );
//					// Apply style span
//					tax.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , taxLabel.length () , 0 );
//				} // End if
				//SpannableString tax = null;
				if ( getItem ( position ).getUnitTaxAmount() != 0 ) {
					// Compute the tax label
					tax = new SpannableString ( taxLabel + " : " + getItem ( position ).getUnitTaxAmount() + " "+currency.getCurrencySymbol() );
					// Apply foreground color span
					tax.setSpan ( new ForegroundColorSpan ( Color.BLACK ) , 0 , taxLabel.length () , 0 );
					// Apply style span
					tax.setSpan ( new StyleSpan ( Typeface.BOLD ) , 0 , taxLabel.length () , 0 );
				} // End if
				// Compute the discount label
				SpannableString discount = null;
				// Check if the current item is taxable
				if ( getItem ( position ).getDetailDiscountPercentage () != 0 ) {
					// Compute the tax label
					discount = new SpannableString ( discountLabel + " : " + getItem ( position ).getDetailDiscountPercentage () + percentageSign );
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
//			if ( ItemsUtils.isBig ( getItem ( position ).getItem () ) ) {
//				// Make sure the big UOM layout is visible
//				viewHolder.big.setVisibility ( View.GONE );
//				// Display the unit price
//				viewHolder.priceBig.setText ( moneyFormat.format ( getItem ( position ).getPriceBig () ) + " " + currency.getCurrencySymbol () );
//				// Check if the picker is enabled
//				if ( usePicker ) {
//					// Clear the previous text watcher
//					clearTextWatcher ( viewHolder.pickerBig.getEditText () );
//					// Display the big quantity in the picker
//					viewHolder.pickerBig.setNumber ( getItem ( position ).getQuantityBig () );
//					// Assign a new text watcher
//					orderWatcher = new OrderWatcher ( (SalesOrderDetailsActivity) getContext () , convertView , getItem ( position ) , OrderWatcher.Type.QUANTITY_BIG );
//					viewHolder.pickerBig.getEditText ().addTextChangedListener ( orderWatcher );
//					viewHolder.pickerBig.getEditText ().setTag ( orderWatcher );
//					// Clear the edit text focus
//					checkEditTextFocus ( viewHolder.pickerBig.getEditText () );
//				} // End if
//				else {
//					// Compute the big quantity label
//					SpannableString quantityBig = new SpannableString ( getItem ( position ).getUnit ().getUnitBigDescription () + " : " + getItem ( position ).getQuantityBig () );
//					// Apply foreground color span
//					quantityBig.setSpan ( new ForegroundColorSpan ( getItem ( position ).getQuantityBig () == 0 ? Color.RED : Color.BLUE ) ,
//							getItem ( position ).getUnit ().getUnitBigDescription ().length () + 3 ,
//							quantityBig.length () ,
//							0 );
//					// Display the big quantity label
//					viewHolder.quantityBig.setText ( quantityBig , BufferType.SPANNABLE );
//				} // End else
//				
//				// Compute the big stock label
//				SpannableString stockBig = new SpannableString ( ( usePicker ? getItem ( position ).getUnit ().getUnitBigDescription () : quantityLabel ) + " : " + ( (int) quantities.getQuantityBig () ) );
//				// Apply foreground color span
//				stockBig.setSpan ( new ForegroundColorSpan ( quantities.getQuantityBig () == 0 ? Color.RED : Color.BLUE ) ,
//						( usePicker ? getItem ( position ).getUnit ().getUnitBigDescription () : quantityLabel ).length () + 3 ,
//						stockBig.length () ,
//						0 );
//				
//				// Compute the counted big label
//				SpannableString countedBig = new SpannableString ( countedLabel + " : " + ( (int) getItem ( position ).getCountedBig () ) );
//				// Apply foreground color span
//				countedBig.setSpan ( new ForegroundColorSpan ( getItem ( position ).getCountedBig () == 0 ? Color.RED : Color.BLUE ) , countedLabel.length () + 3 , countedBig.length () , 0 );
//				// Compute the suggested big label
//				SpannableString suggestedBig = new SpannableString ( suggestedLabel + " : " + ( (int) viewHolder.order.getSuggestedBig () ) );
//				// Apply foreground color span
//				suggestedBig.setSpan ( new ForegroundColorSpan ( viewHolder.order.getSuggestedBig () == 0 ? Color.RED : Color.BLUE ) , suggestedLabel.length () + 3 , suggestedBig.length () , 0 );
//				
//				// Display the big stock label
//				viewHolder.stockBig.setText ( TextUtils.concat ( stockBig , newLine , countedBig, newLine,suggestedBig ) , BufferType.SPANNABLE );
//			} // End if
//			else
//				// Hide the big UOM layout
 			viewHolder.big.setVisibility ( View.GONE );
//			
			
							
							
							
							
			// Check if the current item has a medium UOM and a valid unit price
			if ( ItemsUtils.isMedium ( getItem ( position ).getItem () ) ) {
				// Make sure the medium UOM layout is visible
				viewHolder.medium.setVisibility ( View.VISIBLE );
				// Display the unit price
				viewHolder.priceMedium.setText ( moneyFormat.format ( getItem ( position ).getPriceMedium () ) + " " + currency.getCurrencySymbol () );
				// Check if the picker is enabled
				if ( usePicker ) {
					// Clear the previous text watcher
					clearTextWatcher ( viewHolder.pickerMedium.getEditText () );
					// Display the medium quantity in the picker
					viewHolder.pickerMedium.setNumber ( getItem ( position ).getQuantityMedium () );
		
					// Assign a new text watcher
					orderWatcher = new OrderWatcher ( (SalesOrderDetailsActivity) getContext () , convertView , getItem ( position ) , OrderWatcher.Type.QUANTITY_MEDIUM );
					viewHolder.pickerMedium.getEditText ().addTextChangedListener ( orderWatcher );
					viewHolder.pickerMedium.getEditText ().setTag ( orderWatcher );
					// Clear the edit text focus
					checkEditTextFocus ( viewHolder.pickerMedium.getEditText () );
				} // End if
				else {
					if(type==1){
					// Compute the medium quantity label
					SpannableString quantityMed = new SpannableString ( getItem ( position ).getUnit ().getUnitMediumDescription () + " : " + getItem ( position ).getQuantityMedium () );
					// Apply foreground color span
					quantityMed.setSpan ( new ForegroundColorSpan ( getItem ( position ).getQuantityMedium () == 0 ? Color.RED : Color.BLUE ) ,
							getItem ( position ).getUnit ().getUnitMediumDescription ().length () + 3 ,
							quantityMed.length () ,
							0 );
					// Display the medium quantity label
					viewHolder.quantityMedium.setText ( quantityMed , BufferType.SPANNABLE );}
					else	if(type==3){
						// Compute the medium quantity label
						SpannableString quantityMed = new SpannableString ( getItem ( position ).getUnit ().getUnitMediumDescription () + " : " + getItem ( position ).getBasketMedium () );
						// Apply foreground color span
						quantityMed.setSpan ( new ForegroundColorSpan ( getItem ( position ).getBasketMedium () == 0 ? Color.RED : Color.BLUE ) ,
								getItem ( position ).getUnit ().getUnitMediumDescription ().length () + 3 ,
								quantityMed.length () ,
								0 );
						// Display the medium quantity label
						viewHolder.quantityMedium.setText ( quantityMed , BufferType.SPANNABLE );}
					else
					{
						SpannableString quantityMed = new SpannableString ( getItem ( position ).getUnit ().getUnitMediumDescription () + " : " + getItem ( position ).getFreeMedium  () );
						// Apply foreground color span
						quantityMed.setSpan ( new ForegroundColorSpan ( getItem ( position ).getFreeMedium () == 0 ? Color.RED : Color.BLUE ) ,
								getItem ( position ).getUnit ().getUnitMediumDescription ().length () + 3 ,
								quantityMed.length () ,
								0 );
						// Display the medium quantity label
						viewHolder.quantityMedium.setText ( quantityMed , BufferType.SPANNABLE );
						}
					 
				
				} // End else
				
				// Compute the medium stock label
				SpannableString stockMedium = new SpannableString ( ( usePicker ? getItem ( position ).getUnit ().getUnitMediumDescription () : quantityLabel ) + " : " + ( (int) quantities.getQuantityMedium () ) );
				// Apply foreground color span
				stockMedium.setSpan ( new ForegroundColorSpan ( quantities.getQuantityMedium () == 0 ? Color.RED : Color.BLUE ) ,
						( usePicker ? getItem ( position ).getUnit ().getUnitMediumDescription () : quantityLabel ).length () + 3 ,
						stockMedium.length () ,
						0 );
				
				// Compute the counted medium label
				SpannableString countedMedium = new SpannableString ( countedLabel + " : " + ( (int) getItem ( position ).getCountedMedium () ) );
				// Apply foreground color span
				countedMedium.setSpan ( new ForegroundColorSpan ( getItem ( position ).getCountedMedium () == 0 ? Color.RED : Color.BLUE ) , countedLabel.length () + 3 , countedMedium.length () , 0 );
				// Compute the suggested medium label
				SpannableString suggestedMedium = new SpannableString ( suggestedLabel + " : " + ( (int) viewHolder.order.getSuggestedMedium () ) );
				// Apply foreground color span
				suggestedMedium.setSpan ( new ForegroundColorSpan ( viewHolder.order.getSuggestedMedium () == 0 ? Color.RED : Color.BLUE ) , suggestedLabel.length () + 3 , suggestedMedium.length () , 0 );
				
				// Display the medium stock label
				viewHolder.stockMedium.setText ( TextUtils.concat ( stockMedium , newLine , countedMedium , newLine , suggestedMedium) , BufferType.SPANNABLE );
			} // End if
			else
				// Hide the medium UOM layout
				viewHolder.medium.setVisibility ( View.INVISIBLE );
			
			// Make sure the small UOM layout is visible
			viewHolder.small.setVisibility ( View.VISIBLE );
			// Display the unit price
			viewHolder.priceSmall.setText ( moneyFormat.format ( getItem ( position ).getPriceSmall () ) + " " + currency.getCurrencySymbol () );
			// Check if the picker is enabled
			if ( usePicker ) {
				// Clear the previous text watcher
				clearTextWatcher ( viewHolder.pickerSmall.getEditText () );
				// Display the small quantity in the picker
				viewHolder.pickerSmall.setNumber ( getItem ( position ).getQuantitySmall () );
				// Assign a new text watcher
				orderWatcher = new OrderWatcher ( (SalesOrderDetailsActivity) getContext () , convertView , getItem ( position ) , OrderWatcher.Type.QUANTITY_SMALL );
				viewHolder.pickerSmall.getEditText ().addTextChangedListener ( orderWatcher );
				viewHolder.pickerSmall.getEditText ().setTag ( orderWatcher );
				// Clear the edit text focus
				checkEditTextFocus ( viewHolder.pickerSmall.getEditText () );
				if(viewHolder.order.isMustStockList())
				    viewHolder.lable_reason.setText("Reason:");
				else
					viewHolder.lable_reason.setText(""); 
				if(   viewHolder.order.getReasonCode()!=null)
				{
					Reasons r=  DatabaseUtils.getInstance ( getContext() ).getDaoSession ().getReasonsDao ().queryBuilder ()
							.where ( ReasonsDao.Properties.ReasonCode.eq (viewHolder.order.getReasonCode() )).unique();
					if(r!=null)
					//viewHolder.lable_reason.setText(r.getReasonName());
					//viewHolder.reason_spinner.setVisibility(View.GONE );
						  {@SuppressWarnings("unchecked")
						ArrayAdapter<Reasons>   adapter = (ArrayAdapter<Reasons>) viewHolder.reason_spinner.getAdapter();
						  //  int spinnerPosition = 	viewHolder.reason_spinner.getAdapter().getPosition(r);
						  for (int spinnerPosition = 0;spinnerPosition < adapter.getCount(); spinnerPosition++)
						  {
						    if(adapter.getItem (spinnerPosition) == r)
						    {  
						  viewHolder.reason_spinner.setSelection(spinnerPosition);
						    }
						    }
				}}
			} // End if
			else {
				if(   viewHolder.order.getReasonCode()!=null)
				{
					Reasons r=  DatabaseUtils.getInstance ( getContext() ).getDaoSession ().getReasonsDao ().queryBuilder ()
							.where ( ReasonsDao.Properties.ReasonCode.eq (viewHolder.order.getReasonCode() )).unique();
					if(r!=null)
					viewHolder.lable_reason.setText(r.getReasonName());
					viewHolder.reason_spinner.setVisibility(View.GONE );
				}else viewHolder.lable_reason.setText("");

				 if(type==1){
				// Compute the small quantity label
				SpannableString quantitySmall = new SpannableString ( getItem ( position ).getUnit ().getUnitSmallDescription () + " : " + getItem ( position ).getQuantitySmall () );
				// Apply foreground color span
				quantitySmall.setSpan ( new ForegroundColorSpan ( getItem ( position ).getQuantitySmall () == 0 ? Color.RED : Color.BLUE ) ,
						getItem ( position ).getUnit ().getUnitSmallDescription ().length () + 3 ,
						quantitySmall.length () ,
						0 );
				// Display the small quantity label
				viewHolder.quantitySmall.setText ( quantitySmall , BufferType.SPANNABLE );}
				 else if(type==3){
				// Compute the small quantity label
				SpannableString quantitySmall = new SpannableString ( getItem ( position ).getUnit ().getUnitSmallDescription () + " : " + getItem ( position ).getBasketSmall () );
				// Apply foreground color span
				quantitySmall.setSpan ( new ForegroundColorSpan ( getItem ( position ).getBasketSmall () == 0 ? Color.RED : Color.BLUE ) ,
						getItem ( position ).getUnit ().getUnitSmallDescription ().length () + 3 ,
						quantitySmall.length () ,
						0 );
				// Display the small quantity label
				viewHolder.quantitySmall.setText ( quantitySmall , BufferType.SPANNABLE );}
				 else{	SpannableString quantitySmall = new SpannableString ( getItem ( position ).getUnit ().getUnitSmallDescription () + " : " + getItem ( position ).getFreeSmall  () );
					// Apply foreground color span
					quantitySmall.setSpan ( new ForegroundColorSpan ( getItem ( position ).getFreeSmall () == 0 ? Color.RED : Color.BLUE ) ,
							getItem ( position ).getUnit ().getUnitSmallDescription ().length () + 3 ,
							quantitySmall.length () ,
							0 );
					// Display the small quantity label
					viewHolder.quantitySmall.setText ( quantitySmall , BufferType.SPANNABLE );
					}

			} // End else
			
			// Compute the small stock label
			SpannableString stockSmall = new SpannableString ( ( usePicker ? getItem ( position ).getUnit ().getUnitSmallDescription () : quantityLabel ) + " : " + ( (int) quantities.getQuantitySmall () ) );
			// Apply foreground color span
			stockSmall.setSpan ( new ForegroundColorSpan ( quantities.getQuantitySmall () == 0 ? Color.RED : Color.BLUE ) ,
					( usePicker ? getItem ( position ).getUnit ().getUnitSmallDescription () : quantityLabel ).length () + 3 ,
					stockSmall.length () ,
					0 );
			
			// Compute the counted small label
			SpannableString countedSmall = new SpannableString ( countedLabel + " : " + ( (int) getItem ( position ).getCountedSmall () ) );
			// Apply foreground color span
			countedSmall.setSpan ( new ForegroundColorSpan ( getItem ( position ).getCountedSmall () == 0 ? Color.RED : Color.BLUE ) , countedLabel.length () + 3 , countedSmall.length () , 0 );
			// Compute the suggested small label
						SpannableString suggestedSmall = new SpannableString ( suggestedLabel + " : " + ( (int) viewHolder.order.getSuggestedSmall () ) );
						// Apply foreground color span
						suggestedSmall.setSpan ( new ForegroundColorSpan ( viewHolder.order.getSuggestedSmall () == 0 ? Color.RED : Color.BLUE ) , suggestedLabel.length () + 3 , suggestedSmall.length () , 0 );
						
			// Display the small stock label
			viewHolder.stockSmall.setText ( TextUtils.concat ( stockSmall , newLine , countedSmall ,newLine , suggestedSmall) , BufferType.SPANNABLE );
			
			
			
			// Check if the current item has a medium UOM and a valid unit price
			if ( ItemsUtils.isCase ( getItem ( position ).getItem () ) ) {
				// Make sure the medium UOM layout is visible
				viewHolder.freeCase.setVisibility ( View.VISIBLE );
				
				
				//viewHolder.priceFreeCase.setText("Free Case");
				if ( usePicker ) {
					// Clear the previous text watcher
					clearTextWatcher ( viewHolder.pickerFreeCase.getEditText () );
					// Display the free quantity in the picker
					viewHolder.pickerFreeCase.setNumber ( getItem ( position ).getQuantityFreeCase () );
					// Assign a new text watcher
					orderWatcher = new OrderWatcher ( (SalesOrderDetailsActivity) getContext () , convertView , getItem ( position ) , OrderWatcher.Type.QUANTITY_FREE_CASE );
					viewHolder.pickerFreeCase.getEditText ().addTextChangedListener ( orderWatcher );
					viewHolder.pickerFreeCase.getEditText ().setTag ( orderWatcher );
					// Clear the edit text focus
					checkEditTextFocus ( viewHolder.pickerFreeCase.getEditText () );
				} // End if
				else {
					if(type==1){
					// Compute the small quantity label
					SpannableString quantityFree = new SpannableString ( getItem ( position ).getUnit ().getUnitMediumDescription () + " : " + getItem ( position ).getQuantityFreeCase () );
					// Apply foreground color span
					quantityFree.setSpan ( new ForegroundColorSpan ( getItem ( position ).getQuantityFreeCase () == 0 ? Color.RED : Color.BLUE ) ,
							getItem ( position ).getUnit ().getUnitSmallDescription ().length () + 3 ,
							quantityFree.length () ,
							0 );
					// Display the small quantity label
					viewHolder.quantityFreeCase.setText ( quantityFree , BufferType.SPANNABLE );
					}
				} // End else
			}
		else{
				viewHolder.freeCase.setVisibility ( View.INVISIBLE );
			}		
			
			if(usePicker){
//				
					clearTextWatcher ( viewHolder.PickerSequence.getEditText () );
//				// Display the free quantity in the picker
				viewHolder.PickerSequence.setNumber ( getItem ( position ).getSequence()   );
//				// Assign a new text watcher
					orderWatcher = new OrderWatcher ( (SalesOrderDetailsActivity) getContext () , convertView , getItem ( position ) , OrderWatcher.Type.SEQUENCE );
					viewHolder.PickerSequence.getEditText ().addTextChangedListener ( orderWatcher );
					viewHolder.PickerSequence.getEditText ().setTag ( orderWatcher );
//				// Clear the edit text focus
					checkEditTextFocus ( viewHolder.PickerSequence.getEditText () );
			}
				else ;
			
			// Make sure the small UOM layout is visible
			viewHolder.free.setVisibility ( View.VISIBLE );
			// Display the unit price
			//viewHolder.priceFree.setText ( " Free Unit" );
			// Check if the picker is enabled
			if ( usePicker ) {
				// Clear the previous text watcher
				clearTextWatcher ( viewHolder.pickerFree.getEditText () );
				// Display the free quantity in the picker
				viewHolder.pickerFree.setNumber ( getItem ( position ).getQuantityFree () );
				// Assign a new text watcher
				orderWatcher = new OrderWatcher ( (SalesOrderDetailsActivity) getContext () , convertView , getItem ( position ) , OrderWatcher.Type.QUANTITY_FREE );
				viewHolder.pickerFree.getEditText ().addTextChangedListener ( orderWatcher );
				viewHolder.pickerFree.getEditText ().setTag ( orderWatcher );
				// Clear the edit text focus
				checkEditTextFocus ( viewHolder.pickerFree.getEditText () );
			} // End if
			else {
				if(type==1){
				// Compute the small quantity label
				SpannableString quantityFree = new SpannableString ( getItem ( position ).getUnit ().getUnitSmallDescription () + " : " + getItem ( position ).getQuantityFree () );
				// Apply foreground color span
				quantityFree.setSpan ( new ForegroundColorSpan ( getItem ( position ).getQuantityFree () == 0 ? Color.RED : Color.BLUE ) ,
						getItem ( position ).getUnit ().getUnitSmallDescription ().length () + 3 ,
						quantityFree.length () ,
						0 );
				// Display the small quantity label
				viewHolder.quantityFree.setText ( quantityFree , BufferType.SPANNABLE );
				}
				} // End else
			
			// Determine if all UOMs are hidden
			if ( viewHolder.big.getVisibility () == View.GONE
					&& viewHolder.medium.getVisibility () == View.INVISIBLE
					&& viewHolder.small.getVisibility () == View.INVISIBLE 
				&& viewHolder.free.getVisibility () == View.INVISIBLE )
				// Hide the sales order layout
				viewHolder.salesOrder.setVisibility ( View.GONE );
			else
				// Make sure the sales order layout is visible
				viewHolder.salesOrder.setVisibility ( View.VISIBLE );
			displayImage ( getContext () , convertView , getItem ( position ).getItem ().getItemCode () );
			viewHolder.icon.setOnClickListener ( (OnClickListener) getContext () );
			// Return the view
			return convertView;
		
		} catch ( Exception exception ) {
			// Do nothing
			return new View ( getContext () );
		} // End of try-catch block
	}
	@Override
	public long getItemId(int position) {
	    return position;
	}
	@Override
	public int getItemViewType(int position) {
	   return position;
	}

	@Override
	public int getViewTypeCount() {
	    return 10000;
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
		( (SalesOrderDetailsActivity) getContext () ).onListItemClick ( 
				( (SalesOrderDetailsActivity) getContext () ).getListView () ,
				view ,
				( (SalesOrderDetailsActivity) getContext () ).getListView ().getPositionForView ( view ) ,
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
	private double getNumber ( String text ) {
		try {
			// Cast to double
			return Double.parseDouble ( text );
		} catch ( Exception exception ) {
			// Invalid text
			return 0;
		} // End of try-catch block
	}
	/**
	 * Displays the image containing the item picture (if any).
	 * 
	 * @param view	Existing view, returned earlier by newView.
	 * @param itemCode	String hosting the item code whose picture should be displayed.
	 */
	private void displayImage ( final Context context , final View view , final String itemCode ) {
		// Submit a Runnable task for execution in order to query, search and display the appropriate item picture (if any)
		executorService.submit ( new Runnable () {
			/**
			 * Picks from within the storage list the first occurrence that contains a valid item pictures folder.<br>
			 * If none has the required folder, or if the list is empty / invalid, the default storage is returned.
			 * 
			 * @param defaultStorage	Reference to the default storage file to return, if the storage list is empty or has no storage with the required item pictures folder.
			 * @param storageList	Storage list hosting directories that might contain a valid item pictures folder.
			 * @return	Reference to a storage file hosting a valid item pictures folder.
			 */
			private File pickStorage ( final File defaultStorage , final File storageList [] ) {
				// Check if the storage list is valid
				if ( storageList == null )
					// Return the default storage
					return defaultStorage;
				// Iterate over the storage list
				for ( File storageSecondary : storageList ) {
					// Open the required item pictures folder
					File folder = new File ( storageSecondary , "//" + Environment.DIRECTORY_PICTURES + "//" + ItemCard.ITEM_PICTURES_DIRECTORY );
					// Check if the folder exists
					if ( folder.exists () )
						// Return the current storage file
						return storageSecondary;
				} // End for each
				// Otherwise return the default storage
				return defaultStorage;
			}
			
			/*
			 * Starts executing the active part of the class' code.
			 *
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run () {
				try {
					// Open the primary storage device folder
					File storagePrimary = Environment.getExternalStorageDirectory ();
					// Open the root storage folder
					File storageRoot = storagePrimary.getParentFile ();
					// Retrieve a list of other secondary storage
					File storageSecondaryArray [] = storageRoot.listFiles ( new ExternalStorageFilter () );
					
					// Determine what storage folder to use
					// Based on if they contain an item pictures directory inside a pictures directory
					// Priority is for secondary storage devices first
					File storage = pickStorage ( storagePrimary , storageSecondaryArray );
					// Open the required item pictures folder
					File folder = new File ( storage , "//" + Environment.DIRECTORY_PICTURES + "//" + ItemCard.ITEM_PICTURES_DIRECTORY );
					// Check if the folder exists
					if ( ! folder.exists () )
						// Do nothing
						return;
					
					// Retrieve the list of pictures that belong to the current item
					File pictures [] = folder.listFiles ( new PictureFilter ( itemCode   ) );
					if ( pictures == null ){
						// No item pictures
						  pictures  = folder.listFiles ( new PictureFilter (   "a0_" ) );
						}
					if(pictures.length==0)
						  pictures  = folder.listFiles ( new PictureFilter ( "a0_") );
					// Otherwise retrieve a reference to the fist picture
					File picture = pictures [ 0 ];
					
					// Decode the image size
		            BitmapFactory.Options options = new BitmapFactory.Options ();
		            options.inJustDecodeBounds = true;
		            BitmapFactory.decodeStream ( new FileInputStream ( picture ) , null , options );
		            // Compute the image sample size (scale)
		            int width = options.outWidth;
		            int height = options.outHeight;
		            int scale = 1;
		            // Keep iterating until the size is met
		            while ( true ) {
		            	// Check if the size is met
		            	if ( width / 2 < iconSize || height / 2 < iconSize )
		            		// Exit loop
		            		break;
		            	// Reduce width and height by half
		                width /= 2;
		                height /= 2;
		                // Double scale
		                scale *= 2;
		            } // End while loop
		            // Decode the image using the sample size
		            options = new BitmapFactory.Options ();
		            options.inSampleSize = scale;
		            final Bitmap bitmap = BitmapFactory.decodeStream ( new FileInputStream ( picture ) , null , options );
		            // Check if the bitmap is valid
		            if ( bitmap == null )
		            	// Do nothing
		            	return;
		            
		            // Set the bitmap to the image view on the UI thread
		            ( (Activity) context ).runOnUiThread ( new Runnable () {
						@Override
						public void run () {
							try {
								// Make sure the view is for the indicated item
								if ( ( (ViewHolder) view.getTag () ).order.getItem ().getItemCode ().equals ( itemCode ) ) {
									// Retrieve a reference to the icon
									ImageView icon = ( (ViewHolder) view.getTag () ).icon;
									// Display the bitmap on the image view
									icon.setImageBitmap ( bitmap );
									// Indicate that the image view is not displaying the default image (by setting a true as tag)
									// If the tag is NULL it means that the default image is displayed
									icon.setTag ( true );
								} // End if
								else{
									ImageView icon = ( (ViewHolder) view.getTag () ).icon;
									icon.setImageResource(R.drawable.boxes);
									icon.setTag ( true );
								}
							} catch  ( Exception exception ) {
								// An error occurred, do nothing
							} // End of try-catch block
						}
					} );
				} catch  ( Exception exception ) {
					// An error occurred, do nothing
				} // End of try-catch block
			}
		} );
	}
}