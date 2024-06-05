/**
 * Copyright 2013 SyncWise International SARL
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

/**
 * Class used to represent the target coverage related to collection amounts.
 * 
 * @author Elias
 *
 */
public class TargetCollections implements Target {

	/**
	 * String holding the targeted collection currency description.
	 */
	private final String currencyDescription;
	
	/**
	 * String holding the required collection amount.
	 */
	private final String requiredAmountStr;

	/**
	 * String holding the achieved collection amount.
	 */
	private final String achievedAmountStr;
	
	/**
	 * Constant double holding the required collection amount.
	 */
	private final double requiredAmount;

	/**
	 * Constant double holding the achieved collection amount.
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
	 * @param currencyDescription	String hosting the targeted collection currency.
	 * @param requiredAmount	Double holding the required collection amount.
	 * @param achievedAmount	Double holding the achieved collection amount.
	 * @param moneyFormat	Decimal format initialized using the appropriate format based on the corresponding targeted collection currency rounding.
	 */
	public TargetCollections ( final Context context , final String currencyDescription , final double requiredAmount , final double achievedAmount , final DecimalFormat moneyFormat ) {
		// Store the currency description
		this.currencyDescription = currencyDescription;
		// Store the required and achieved amounts
		this.requiredAmount = requiredAmount < 0 ? 0 : requiredAmount;
		this.achievedAmount = achievedAmount < 0 ? 0 : achievedAmount;
		// Compute the required and achieved amounts strings
		this.requiredAmountStr = moneyFormat.format ( requiredAmount );
		this.achievedAmountStr = moneyFormat.format ( achievedAmount );
		// Retrieve the remaining labels
		coverageLabel = AppResources.getInstance ( context ).getString ( context , R.string.collection_amount_coverage_label );
		targetPreLabel = AppResources.getInstance ( context ).getString ( context , R.string.collection_amount_target_pre_label );
	}
	
	/*
	 * Gets the target subject code.
	 *
	 * @see me.SyncWise.Android.Modules.Target.Target#getSubjectCode()
	 */
	@Override
	public String getSubjectCode () {
		// Return the currency description
		return currencyDescription;
	}

	/*
	 * Gets the target subject description.
	 *
	 * @see me.SyncWise.Android.Modules.Target.Target#getSubjectDescription()
	 */
	@Override
	public String getSubjectDescription () {
		// Return an empty string
		return "";
	}

	/*
	 * Gets the target subject icon ID.
	 *
	 * @see me.SyncWise.Android.Modules.Target.Target#getSubjectIconID()
	 */
	@Override
	public int getSubjectIconID () {
		// Return the currency icon resource id
		return R.drawable.money;
	}

	/*
	 * Gets the target description.
	 *
	 * @see me.SyncWise.Android.Modules.Target.Target#getTargetDescription()
	 */
	@Override
	public String getTargetDescription () {
		// Compute and return the required target amount label
		return targetPreLabel + " " + requiredAmountStr;
	}
	
	/*
	 * Gets the target sub subject code.
	 *
	 * @see me.SyncWise.Android.Modules.Target.Target#getSubSubjectCode()
	 */
	@Override
	public String getSubSubjectCode () {
		// Return an empty string
		return "";
	}

	/*
	 * Gets the target sub subject description.
	 *
	 * @see me.SyncWise.Android.Modules.Target.Target#getSubjectSubDescription()
	 */
	@Override
	public String getSubSubjectDescription () {
		// Return an empty string
		return "";
	}

	/*
	 * Gets the target sub subject icon ID.
	 *
	 * @see me.SyncWise.Android.Modules.Target.Target#getSubSubjectIconID()
	 */
	@Override
	public int getSubSubjectIconID () {
		// Return an empty icon
		return 0;
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