/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.SalesRFR;

import java.io.Serializable;
import java.util.ArrayList;

import me.SyncWise.Android.Database.PromotionDetails;
import me.SyncWise.Android.Database.PromotionHeaders;

/**
 * Class used to represent basket promotions.
 * 
 * @author Elias
 *
 */
public class BasketPromotionRFR implements Serializable {

	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;
	
	private PromotionHeaders promotionHeader;
	
	private ArrayList < PromotionDetails > promotionDetails;
	
	private ArrayList < OrderRFR > invoices;
	
	private int quantity;
	
	public boolean isExpanded() {
		return isExpanded;
	}

	public void setExpanded(boolean isExpanded) {
		this.isExpanded = isExpanded;
	}

	private boolean isExpanded = false;
	/**
	 * Getter - {@link #promotionHeader}
	 * 
	 * @return the promotionHeader
	 */
	public PromotionHeaders getPromotionHeader () {
		return promotionHeader;
	}
	
	/**
	 * Setter - {@link #promotionHeader}
	 * 
	 * @param promotionHeader the promotionHeader to set
	 */
	public void setPromotionHeader ( PromotionHeaders promotionHeader ) {
		this.promotionHeader = promotionHeader;
	}
	
	/**
	 * Getter - {@link #promotionDetails}
	 * 
	 * @return the promotionDetails
	 */
	public ArrayList < PromotionDetails > getPromotionDetails () {
		return promotionDetails;
	}
	
	/**
	 * Setter - {@link #promotionDetails}
	 * 
	 * @param promotionDetails the promotionDetails to set
	 */
	public void setPromotionDetails ( ArrayList < PromotionDetails > promotionDetails ) {
		this.promotionDetails = promotionDetails;
	}
	
	/**
	 * Getter - {@link #invoices}
	 * 
	 * @return the invoices
	 */
	public ArrayList < OrderRFR > getInvoices () {
		return invoices;
	}
	
	/**
	 * Setter - {@link #invoices}
	 * 
	 * @param invoices the invoices to set
	 */
	public void setInvoices ( ArrayList < OrderRFR > invoices ) {
		this.invoices = invoices;
	}
	
	/**
	 * Getter - {@link #quantity}
	 * 
	 * @return the quantity
	 */
	public int getQuantity () {
		return quantity;
	}
	
	/**
	 * Setter - {@link #quantity}
	 * 
	 * @param quantity the quantity to set
	 */
	public void setQuantity ( int quantity ) {
		this.quantity = quantity;
	}
	
}