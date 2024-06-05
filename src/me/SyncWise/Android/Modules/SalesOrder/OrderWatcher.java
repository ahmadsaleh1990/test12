/**
 * Copyright 2014 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.SalesOrder;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import me.SyncWise.Android.Database.ClientItemCodesDao;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.PriceLists;
import me.SyncWise.Android.Database.PriceListsDao;
import me.SyncWise.Android.Database.Prices;
import me.SyncWise.Android.Database.PricesDao;
import me.SyncWise.Android.Database.PromotionDetails;
 
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

/**
 * Text Watcher implemented to update the order quantities.
 * 
 * @author Elias -- Ahmad
 *
 */
public class OrderWatcher implements TextWatcher {

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
		public static final int QUANTITY_FREE = 3;
		public static final int QUANTITY_FREE_CASE = 4;
		public static final int SEQUENCE=5;
	}
	
	/**
	 * Weak reference to the {@link me.SyncWise.Android.Modules.SalesOrder.SalesOrderDetailsActivity SalesOrderDetailsActivity} screen.
	 */
	private WeakReference < SalesOrderDetailsActivity > activity;
	
	/**
	 * Weak reference to the {@link android.view.View View} holding the order in the {@link me.SyncWise.Android.Modules.SalesOrder.SalesOrderDetailsAdapter SalesOrderDetailsAdapter}.
	 */
	private WeakReference < View > orderView;
	
	/**
	 * {@link me.SyncWise.Android.Modules.SalesOrder.Order Order} object reference to update.
	 */
	private final Order order;
	
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
	public OrderWatcher ( final SalesOrderDetailsActivity activity , final View orderView , final Order order , final int type ) {
		// Initialize variables
		this.activity = new WeakReference < SalesOrderDetailsActivity > ( activity );
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
		
		// Determine the watcher type and update the order accordingly
		switch ( type ) {
		case Type.QUANTITY_BIG:
			order.setQuantityBig ( (int) getNumber ( s.toString () ) );
			break;
		case Type.QUANTITY_MEDIUM:
			order.setQuantityMedium ( (int) getNumber ( s.toString () ) );
			break;
		case Type.SEQUENCE:
			order.setSequence( (int) getNumber ( s.toString () ) );
			break;
		case Type.QUANTITY_SMALL:
			order.setQuantitySmall ( (int) getNumber ( s.toString () ) );
			break;
		case Type.QUANTITY_FREE:
			order.setQuantityFree ( (int) getNumber ( s.toString () ) );
			break;
		case Type.QUANTITY_FREE_CASE:
			order.setQuantityFreeCase ( (int) getNumber ( s.toString () ) );
//			if(order.getQuantityFreeCase() > 1 ){
//				order.setMax(true);
//			}
			break;
		default:
			// Do nothing
			return;
		} // End switch
		
		// Check if the item has zero quantities
		// AND if the selected order belongs to the item stock list and is not confirmed
		if ( ( order.getQuantityBig () != 0 || order.getQuantityMedium () != 0 || order.getQuantitySmall () != 0  || order.getQuantityFree() != 0 )  
				&& order.isMustStockList () && ! order.isConfirmed () )
			// Confirm the order
			order.setConfirmed ( true );
		
		try {
			if ( activity != null && activity.get () != null ) {
			if(activity.get()._promotionsPricelist!=null && activity.get()._promotionsPricelist.containsKey(order.getItem().getItemCode()))
			{
				ArrayList< PromotionDetails > tempPromotionDetails =  activity.get()._promotionsPricelist.get ( order.getItem().getItemCode() ); 
				double unitMediumSmall = order.getItem().getUnitMediumSmall();
				double basicOfferedQuantity = tempPromotionDetails.get(0).getBasicOrderedQuantity();
				double freeOfferedQuantityUnit = basicOfferedQuantity;
				int	   freeOfferedQuantityCase = (int)(freeOfferedQuantityUnit / unitMediumSmall);
			
				
				if(order.getQuantitySmall() > freeOfferedQuantityUnit || 
				   order.getQuantityMedium() > freeOfferedQuantityCase){
				List<	Prices> pc = DatabaseUtils.getInstance(activity.get()).getDaoSession().getPricesDao().queryBuilder().where( 
							PricesDao.Properties.PriceListCode.eq (  tempPromotionDetails.get(0).getOfferedItemCode() ),
							PricesDao.Properties.ItemCode.eq(order.getItem().getItemCode())).list();	
				if(pc.size()>0)
				{Prices p=pc.get(0);		
				//order.setPriceMediumOld(order.getPriceMedium());
				//order.setPriceSmallOld(order.getPriceSmall());
					order.setPriceMedium(p.getPriceMedium());
					order.setPriceSmall(p.getPriceSmall());
				}
				}
				else{
					order.setPriceMedium(order.getPriceMediumOld());
					order.setPriceSmall(order.getPriceSmallOld());
				}
			}
			}
				// Determine if the activity reference is valid
			if ( activity != null && activity.get () != null ) {
				// Set the order being modified
				activity.get ().setOrderModification ( order );
				// Update order
				activity.get ().updateOrder ( null );
			} // End if
			// Determine if the order view reference is valid
			if ( orderView != null && orderView.get () != null && activity != null && activity.get () != null )
				// Refresh the order view being modified
				SalesOrderDetailsAdapter.refreshOrderView ( activity.get () , orderView.get () , activity.get ().getMoneyFormat () , activity.get ().getCurrency () ,
						activity.get ().getCodeLabel () , activity.get ().getTotalLabel (),activity.get ().getItemBarcodeLabel(),activity.get().getClientItemBarcodeLabel() , activity.get ().getNewLine () , activity.get ().getBrownColor () , activity.get ().getMistyRose () ,1);
		} catch ( Exception exception ) {
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