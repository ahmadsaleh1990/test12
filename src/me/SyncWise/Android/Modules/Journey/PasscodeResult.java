/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Journey;

import java.io.Serializable;

import me.SyncWise.Android.Database.Reasons;


public class PasscodeResult implements Serializable {
	
	/**
	 * Default serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	public static final int TYPE_OUT_OF_ROUTE = 1;
	
	public static final int TYPE_USER_ACCOUNT = 2;
	
	private final int type;
	private final int fragmentIndex;
	private final Call call;
	private final boolean enforceVisitType;
	private final int visitType;
	private final Reasons barcodeReason;

	
	
	/**
	 * Constructor.
	 * 
	 * @param type
	 * @param fragmentIndex
	 * @param call
	 * @param enforceVisitType
	 * @param visitType
	 * @param barcodeReason
	 */
	public PasscodeResult ( int type , int fragmentIndex , Call call , boolean enforceVisitType , int visitType , Reasons barcodeReason ) {
		super ();
		this.type = type;
		this.fragmentIndex = fragmentIndex;
		this.call = call;
		this.enforceVisitType = enforceVisitType;
		this.visitType = visitType;
		this.barcodeReason = barcodeReason;
	}

	/**
	 * Getter - {@link #type}
	 * 
	 * @return the type
	 */
	public int getType () {
		return type;
	}

	/**
	 * Getter - {@link #fragmentIndex}
	 * 
	 * @return the fragmentIndex
	 */
	public int getFragmentIndex () {
		return fragmentIndex;
	}

	/**
	 * Getter - {@link #call}
	 * 
	 * @return the call
	 */
	public Call getCall () {
		return call;
	}
	
	/**
	 * Getter - {@link #enforceVisitType}
	 * 
	 * @return the enforceVisitType
	 */
	public boolean isEnforceVisitType () {
		return enforceVisitType;
	}
	
	/**
	 * Getter - {@link #visitType}
	 * 
	 * @return the visitType
	 */
	public int getVisitType () {
		return visitType;
	}
	
	/**
	 * Getter - {@link #barcodeReason}
	 * 
	 * @return the barcodeReason
	 */
	public Reasons getBarcodeReason () {
		return barcodeReason;
	}

}
