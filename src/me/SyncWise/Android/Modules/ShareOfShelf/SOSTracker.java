/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.ShareOfShelf;

import java.io.Serializable;

import android.content.Context;
import android.text.TextUtils;

import me.SyncWise.Android.Database.Brands;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.PermissionsUtils;

/**
 * Class used to represent share of shelf tracker.
 * 
 * @author Elias
 *
 */
public class SOSTracker implements Serializable {

	public double getBdaMeter() {
		return bdaMeter;
	}



	public void setBdaMeter(double bdaMeter) {
		this.bdaMeter = bdaMeter;
	}



	public String getBrandName() {
		return brandName;
	}

	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Reference to the brand object.
	 */ 
	 private String headCompCode;
	 public String getHeadCompCode() {
		return headCompCode;
	}



	public void setHeadCompCode(String headCompCode) {
		this.headCompCode = headCompCode;
	}



	public String getHeadCompName() {
		return headCompName;
	}



	public void setHeadCompName(String headCompName) {
		this.headCompName = headCompName;
	}

	private String headCompName;
	 
	private String targetCode;
	public String getTargetCode() {
		return targetCode;
	}



	public void setTargetCode(String targetCode) {
		this.targetCode = targetCode;
	}

	private   String brandCode;
	private   String brandName;
	private   Integer brandtype;
	public Integer getBrandtype() {
		return brandtype;
	}



	public void setBrandtype(Integer brandtype) {
		this.brandtype = brandtype;
	}

	/**
	 * Double used to host the previous share of shelf value.
	 */
	private int previousShareOfShelf;
	
	/**
	 * Double used to host the current share of shelf value.
	 */
	private Double currentShareOfShelf;
 
	
 

	/**
	 * String used to host the current notes.
	 */
	private String currentNotes = "";
	
	/**
	 * Double used to host  the BDA meter.
	 */
	private double bdaMeter;
	
	 
	
	/**
	 * Getter - {@link #brand}
	 * 
	 * @return	Reference to the brand object.
	 */
	public String getBrandCode () {
		return brandCode;
	}
	
	 

	/**
	 * Getter - {@link #previousShareOfShelf}
	 * 
	 * @return	Double used to host the previous share of shelf value.
	 */
	public int getPreviousShareOfShelf () {
		return previousShareOfShelf;
	}

	/**
	 * Setter - {@link #previousShareOfShelf}
	 * 
	 * @param previousShareOfShelf	Double used to host the previous share of shelf value.
	 */
	public void setPreviousShareOfShelf ( final int previousShareOfShelf ) {
		 
			this.previousShareOfShelf = previousShareOfShelf;
	}

	/**
	 * Getter - {@link #currentShareOfShelf}
	 * 
	 * @return	Double used to host the current share of shelf value.
	 */
	public Double getCurrentShareOfShelf () {
		return currentShareOfShelf;
	}

	/**
	 * Setter - {@link #currentShareOfShelf}
	 * 
	 * @param currentShareOfShelf	Double used to host the current share of shelf value.
	 */
	public void setCurrentShareOfShelf ( final Double currentShareOfShelf ) {
		this.currentShareOfShelf = currentShareOfShelf;
	}
	
	 
	 

	 
 
	/**
	 * Getter - {@link #currentNotes}
	 * 
	 * @return	String used to host the current notes.
	 */
	public String getCurrentNotes () {
		return currentNotes;
	}

	/**
	 * Setter - {@link #currentNotes}
	 * 
	 * @param currentNotes	String used to host the current notes.
	 */
	public void setCurrentNotes ( final String currentNotes ) {
		this.currentNotes = currentNotes;
	}
	
 
	
	 
	
	 
	
	 
 
	
	/**
	 * Getter - {@link #bdaMeter}
	 * 
	 * @return	Double used to host the BDA meter.
	 */
	public double getBDAMeter () {
		return bdaMeter;
	}
	
	/**
	 * Setter - {@link #bdaMeter}
	 * 
	 * @param dbaMeter	Double used to host the BDA meter.
	 */
	public void setBDAMeter ( final double bdaMeter ) {
		this.bdaMeter = bdaMeter;
	}



	public void setBrandCode(String brandCode) {
		this.brandCode = brandCode;
	}



	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	
	 
	
}