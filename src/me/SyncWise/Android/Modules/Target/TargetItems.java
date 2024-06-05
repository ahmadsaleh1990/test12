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

import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import android.content.Context;
import android.text.TextUtils;

/**
 * Class used to represent the target coverage related to items quantity.
 * 
 * @author Elias
 *
 */
public class TargetItems implements Target {
	
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
	 * String holding the targeted item unit description.
	 */
	private final String itemUnitDescription;
	
	/**
	 * Constant integer holding the required number of visits.
	 */
	private final int requiredNumber;

	/**
	 * Constant integer holding the achieved number of visits.
	 */
	private final int achievedNumber;
	
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
	 * @param itemDescription	String hosting the targeted item description.
	 * @param itemUnitDescription	String holding the item unit unit description.
	 * @param requiredNumber	Integer holding the required number of visits.
	 * @param achievedNumber	Integer holding the achieved number of visits.
	 */
	public TargetItems ( final Context context , final String clientCode , final String clientDescription , final String itemCode , final String itemDescription , final String itemUnitDescription , 
			final int requiredNumber , final int achievedNumber ) {
		// Store the client code and description
		this.clientCode = clientCode;
		this.clientDescription = clientDescription;
		// Store the item code and description and unit
		this.itemCode = itemCode;
		this.itemDescription = itemDescription;
		this.itemUnitDescription =itemUnitDescription;
		// Store the required and achieved numbers
		this.requiredNumber = requiredNumber < 0 ? 0 : requiredNumber;
		this.achievedNumber = achievedNumber < 0 ? 0 : achievedNumber;
		// Retrieve the remaining labels
		coverageLabel = AppResources.getInstance ( context ).getString ( context , R.string.items_quantity_coverage_label );
		targetPreLabel = AppResources.getInstance ( context ).getString ( context , R.string.items_quantity_target_pre_label );
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
		// Compute and return the required target number label
		return targetPreLabel + " " + requiredNumber + " " + ( TextUtils.isEmpty ( itemUnitDescription ) ? "" : "(" + itemUnitDescription + ")" );
	}
	
	/*
	 * Gets the target sub subject code.
	 *
	 * @see me.SyncWise.Android.Modules.Target.Target#getSubSubjectCode()
	 */
	@Override
	public String getSubSubjectCode () {
		// Return the item code
		return itemCode;
	}

	/*
	 * Gets the target sub subject description.
	 *
	 * @see me.SyncWise.Android.Modules.Target.Target#getSubjectSubDescription()
	 */
	@Override
	public String getSubSubjectDescription () {
		// Return the item description
		return itemDescription;
	}

	/*
	 * Gets the target sub subject icon ID.
	 *
	 * @see me.SyncWise.Android.Modules.Target.Target#getSubSubjectIconID()
	 */
	@Override
	public int getSubSubjectIconID () {
		// Return the item icon resource id
		return R.drawable.boxes;
	}

	/*
	 * Gets the target coverage completion description.
	 *
	 * @see me.SyncWise.Android.Modules.Target.Target#getCoverageDescription()
	 */
	@Override
	public String getCoverageDescription () {
		// Compute and return the achieved coverage number label
		return coverageLabel + " : " +  achievedNumber + " " + ( TextUtils.isEmpty ( itemUnitDescription ) ? "" : "(" + itemUnitDescription + ")" );
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
			int coverage = (int) ( 100.0 / requiredNumber * achievedNumber );
			return coverage > 100 ? 100 : coverage;
		} catch ( Exception exception ) {
			// Invalid equation
			return 0;
		} // End of try-catch block
	}

}