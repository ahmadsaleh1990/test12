/**
 * Copyright 2014 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.SalesInvoice;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.Prices;
import me.SyncWise.Android.Database.PricesDao;
import me.SyncWise.Android.Database.PromotionDetails;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

/**
 * Text Watcher implemented to update the invoice quantities.
 * 
 * @author Elias
 *
 */
public class InvoiceWatcher implements TextWatcher {

	/**
	 * Helper Class used to manage the text watcher types in order to properly update the provided invoice.<br>
	 * The main purpose of this class is to maintain a unique identifier.
	 * 
	 * @author Elias
	 *
	 */
	protected class Type {
		public static final int QUANTITY_BIG = 0;
		public static final int QUANTITY_MEDIUM = 1;
		public static final int QUANTITY_SMALL = 2;
	}
	
	/**
	 * Weak reference to the {@link me.SyncWise.Android.Modules.SalesInvoice.SalesInvoiceDetailsActivity SalesInvoiceDetailsActivity} screen.
	 */
	private WeakReference < SalesInvoiceDetailsActivity > activity;
	
	/**
	 * Weak reference to the {@link android.view.View View} holding the invoice in the {@link me.SyncWise.Android.Modules.SalesInvoice.SalesInvoiceDetailsAdapter SalesInvoiceDetailsAdapter}.
	 */
	private WeakReference < View > invoiceView;
	
	/**
	 * {@link me.SyncWise.Android.Modules.SalesInvoice.Invoice Invoice} object reference to update.
	 */
	private final Invoice invoice;
	
	/**
	 * Integer holding the text watcher type.
	 */
	private final int type;
	
	/**
	 * Constructor.
	 * 
	 * @param activity	Reference to the {@link me.SyncWise.Android.Modules.SalesInvoice.SalesInvoiceDetailsActivity SalesInvoiceDetailsActivity} screen.
	 * @param invoiceView	Weak reference to the {@link android.view.View View} holding the invoice in the {@link me.SyncWise.Android.Modules.SalesInvoice.SalesInvoiceDetailsAdapter SalesInvoiceDetailsAdapter}.
	 * @param invoice	{@link me.SyncWise.Android.Modules.SalesInvoice.Invoice Invoice} object reference to update.
	 * @param type	Integer holding the text watcher type.
	 */
	public InvoiceWatcher ( final SalesInvoiceDetailsActivity activity , final View invoiceView , final Invoice invoice , final int type ) {
		// Initialize variables
		this.activity = new WeakReference < SalesInvoiceDetailsActivity > ( activity );
		this.invoiceView = new WeakReference < View > ( invoiceView );
		this.invoice = invoice;
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
		// Check if the invoice is valid
		if ( invoice == null )
			// Invalid invoice, do nothing
			return;
		
		// Determine the watcher type and update the invoice accordingly
		switch ( type ) {
		case Type.QUANTITY_BIG:
			invoice.setQuantityBig ( (int) getNumber ( s.toString () ) );
			break;
		case Type.QUANTITY_MEDIUM:
			invoice.setQuantityMedium ( (int) getNumber ( s.toString () ) );
			break;
		case Type.QUANTITY_SMALL:
			invoice.setQuantitySmall ( (int) getNumber ( s.toString () ) );
			break;
		default:
			// Do nothing
			return;
		} // End switch
		
		try {
			if ( activity != null && activity.get () != null ) {
				if(activity.get()._promotionsPricelist.containsKey(invoice.getItem().getItemCode()))
				{
					ArrayList< PromotionDetails > tempPromotionDetails =  activity.get()._promotionsPricelist.get ( invoice.getItem().getItemCode() ); 
					double unitMediumSmall = invoice.getItem().getUnitMediumSmall();
					double basicOfferedQuantity = tempPromotionDetails.get(0).getBasicOrderedQuantity();
					double freeOfferedQuantityUnit = basicOfferedQuantity;
					int	   freeOfferedQuantityCase = (int)(freeOfferedQuantityUnit / unitMediumSmall);
					if(invoice.getQuantitySmall() > freeOfferedQuantityUnit || 
							invoice.getQuantityMedium() > freeOfferedQuantityCase){
//						Prices pc=DatabaseUtils.getInstance(activity.get()).getDaoSession().getPricesDao().queryBuilder().where( 
//								PricesDao.Properties.PriceListCode.eq (  tempPromotionDetails.get(0).getOfferedItemCode() ),
//								PricesDao.Properties.ItemCode.eq(invoice.getItem().getItemCode())).uniqueOrThrow();	
//					
//						invoice.setPriceMedium(pc.getPriceMedium());
//						invoice.setPriceSmall(pc.getPriceSmall());
						List<	Prices> pc = DatabaseUtils.getInstance(activity.get()).getDaoSession().getPricesDao().queryBuilder().where( 
								PricesDao.Properties.PriceListCode.eq (  tempPromotionDetails.get(0).getOfferedItemCode() ),
								PricesDao.Properties.ItemCode.eq(invoice.getItem().getItemCode())).list();	
					if(pc.size()>0)
					{Prices p=pc.get(0);		
					//order.setPriceMediumOld(order.getPriceMedium());
					//order.setPriceSmallOld(order.getPriceSmall());
					invoice.setPriceMedium(p.getPriceMedium());
					invoice.setPriceSmall(p.getPriceSmall());
					}
					}
					else{
						invoice.setPriceMedium(invoice.getPriceMediumOld());
						invoice.setPriceSmall(invoice.getPriceSmallOld());
					}
				}
				}
			// Determine if the activity reference is valid
			if ( activity != null && activity.get () != null ) {
				// Set the invoice being modified
				activity.get ().setInvoiceModification ( invoice );
				// Update invoice
				activity.get ().updateInvoice ( null );
			} // End if
			// Determine if the invoice view reference is valid
			if ( invoiceView != null && invoiceView.get () != null && activity != null && activity.get () != null )
				// Refresh the invoice view being modified
				SalesInvoiceDetailsAdapter.refreshInvoiceView ( activity.get () , invoiceView.get () , activity.get ().getMoneyFormat () , activity.get ().getCurrency () ,
						activity.get ().getCodeLabel () , activity.get ().getTotalLabel () , activity.get ().getNewLine () , activity.get ().getBrownColor () , activity.get ().getMistyRose () );
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