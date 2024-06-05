/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Target;

import java.text.DecimalFormat;

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import android.content.Context;
import android.text.TextUtils;

/**
 * Class used to represent the target coverage related to order amounts per client per brand.
 * 
 * @author Elias
 *
 */
public class TargetOrdersPerClientPerBrand implements Target {

	/**
	 * String holding the targeted client code.
	 */
	private final String clientCode;
	
	/**
	 * String holding the targeted client description.
	 */
	private final String clientDescription;
	
	/**
	 * String holding the targeted item code.
	 */
	private final String itemCode;
	
	/**
	 * String holding the targeted item description.
	 */
	private final String itemDescription;
	
	/**
	 * String holding the targeted division code.
	 */
	private final String divisionCode;
	
	/**
	 * String holding the targeted division description.
	 */
	private final String divisionDescription;
	
	/**
	 * String holding the targeted oder currency description.
	 */
	private final String currencyDescription;
	
	/**
	 * String holding the required order amount.
	 */
	private final String requiredAmountStr;

	/**
	 * String holding the achieved order amount.
	 */
	private final String achievedAmountStr;
	
	/**
	 * Constant double holding the required order amount.
	 */
	private final double requiredAmount;

	/**
	 * Constant double holding the achieved order amount.
	 */
	private final double achievedAmount;
	
	/**
	 * String holding the coverage label.
	 */
	private final String coverageLabel;
	
	/**
	 * String holding the pre target label.
	 */
	private final String targetPreLabel;
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param clientCode	String hosting the targeted client code.
	 * @param clientDescription	String hosting the targeted client description.
	 * @param itemCode	String hosting the targeted item code.
	 * @param itemDescription	String holding the targeted item description.
	 * @param divisionCode	String hosting the targeted division code.
	 * @param divisionDescription	String holding the targeted division description.
	 * @param currencyDescription	String hosting the targeted order currency.
	 * @param requiredAmount	Double holding the required order amount.
	 * @param achievedAmount	Double holding the achieved order amount.
	 * @param moneyFormat	Decimal format initialized using the appropriate format based on the corresponding targeted order currency rounding.
	 */
	public TargetOrdersPerClientPerBrand ( final Context context , final String clientCode , final String clientDescription , final String itemCode , final String itemDescription , final String divisionCode , final String divisionName ,
			final String currencyDescription , final double requiredAmount , final double achievedAmount , final DecimalFormat moneyFormat ) {
		// Store the item code and description and unit
		this.itemCode = itemCode;
		this.itemDescription = itemDescription;
		// Store the division code and description
		this.divisionCode = divisionCode;
		this.divisionDescription = divisionName;
		// Store the client code and description
		this.clientCode = clientCode;
		this.clientDescription = clientDescription;
		// Store the currency description
		this.currencyDescription = currencyDescription;
		// Store the required and achieved amounts
		this.requiredAmount = requiredAmount < 0 ? 0 : requiredAmount;
		this.achievedAmount = achievedAmount < 0 ? 0 : achievedAmount;
		// Compute the required and achieved amounts strings
		this.requiredAmountStr = moneyFormat.format ( requiredAmount );
		this.achievedAmountStr = moneyFormat.format ( achievedAmount );
		// Retrieve the remaining labels
		coverageLabel = AppResources.getInstance ( context ).getString ( context , R.string.order_amount_coverage_label );
		targetPreLabel = AppResources.getInstance ( context ).getString ( context , R.string.order_amount_target_pre_label );
	}
	
	/*
	 * Gets the target subject code.
	 *
	 * @see me.SyncWise.Android.Modules.Target.Target#getSubjectCode()
	 */
	@Override
	public String getSubjectCode () {
		// Return the client code
		return clientCode;
	}

	/*
	 * Gets the target subject description.
	 *
	 * @see me.SyncWise.Android.Modules.Target.Target#getSubjectDescription()
	 */
	@Override
	public String getSubjectDescription () {
		// Return the client description
		return clientDescription;
	}

	/*
	 * Gets the target subject icon ID.
	 *
	 * @see me.SyncWise.Android.Modules.Target.Target#getSubjectIconID()
	 */
	@Override
	public int getSubjectIconID () {
		// Return the client icon resource id
		return R.drawable.user_1;
	}

	/*
	 * Gets the target description.
	 *
	 * @see me.SyncWise.Android.Modules.Target.Target#getTargetDescription()
	 */
	@Override
	public String getTargetDescription () {
		// Compute and return the required target amount label
		return targetPreLabel + " " + requiredAmountStr + " " + currencyDescription;
	}
	
	/*
	 * Gets the target sub subject code.
	 *
	 * @see me.SyncWise.Android.Modules.Target.Target#getSubSubjectCode()
	 */
	@Override
	public String getSubSubjectCode () {
		// Return the item code
		return ( ! TextUtils.isEmpty ( itemCode ) ) ? itemCode : divisionCode;
	}

	/*
	 * Gets the target sub subject description.
	 *
	 * @see me.SyncWise.Android.Modules.Target.Target#getSubjectSubDescription()
	 */
	@Override
	public String getSubSubjectDescription () {
		// Return the item / division description
		return ( ! TextUtils.isEmpty ( itemCode ) ) ? itemDescription : divisionDescription;
	}

	/*
	 * Gets the target sub subject icon ID.
	 *
	 * @see me.SyncWise.Android.Modules.Target.Target#getSubSubjectIconID()
	 */
	@Override
	public int getSubSubjectIconID () {
		// Return the item / division icon resource id
		return ( ! TextUtils.isEmpty ( itemCode ) ) ? R.drawable.boxes : ( ! TextUtils.isEmpty ( divisionCode ) ) ? R.drawable.boxes_1 : 0;
	}

	/*
	 * Gets the target coverage completion description.
	 *
	 * @see me.SyncWise.Android.Modules.Target.Target#getCoverageDescription()
	 */
	@Override
	public String getCoverageDescription () {
		// Compute and return the achieved coverage amount label
		return coverageLabel + " : " +  achievedAmountStr;
	}

	/*
	 * Gets the target coverage completion percentage.
	 *
	 * @see me.SyncWise.Android.Modules.Target.Target#getCoverage()
	 */
	@Override
	public int getCoverage () {
		try {
			// Compute the coverage percentage
			int coverage = (int) ( 100.0 / requiredAmount * achievedAmount );
			return coverage > 100 ? 100 : coverage;
		} catch ( Exception exception ) {
			// Invalid equation
			return 0;
		} // End of try-catch block
	}
	
}