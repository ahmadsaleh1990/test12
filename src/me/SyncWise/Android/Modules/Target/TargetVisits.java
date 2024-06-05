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

import android.content.Context;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;

/**
 * Class used to represent the target coverage related to visits number.
 * 
 * @author Elias
 *
 */
public class TargetVisits implements Target {

	/**
	 * String holding the targeted user code.
	 */
	private final String userCode;
	
	/**
	 * String holding the targeted user name.
	 */
	private final String userName;
	
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
	 * String holding the post target label.
	 */
	private final String targetPostLabel;
	
	/**
	 * Constructor.
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the targeted user code.
	 * @param userName	String hosting the targeted user description.
	 * @param requiredNumber	Integer holding the required number of visits.
	 * @param achievedNumber	Integer holding the achieved number of visits.
	 */
	public TargetVisits ( final Context context , final String userCode , final String userName , final int requiredNumber , final int achievedNumber ) {
		// Store the user code and description
		this.userCode = userCode;
		this.userName = userName;
		// Store the required and achieved numbers
		this.requiredNumber = requiredNumber < 0 ? 0 : requiredNumber;
		this.achievedNumber = achievedNumber < 0 ? 0 : achievedNumber;
		// Retrieve the remaining labels
		coverageLabel = AppResources.getInstance ( context ).getString ( context , R.string.visits_frequency_coverage_label );
		targetPreLabel = AppResources.getInstance ( context ).getString ( context , R.string.visits_frequency_target_pre_label );
		targetPostLabel = AppResources.getInstance ( context ).getString ( context , R.string.visits_frequency_target_post_label );
	}
	
	/*
	 * Gets the target subject code.
	 *
	 * @see me.SyncWise.Android.Modules.Target.Target#getSubjectCode()
	 */
	@Override
	public String getSubjectCode () {
		// Return the user code
		return userCode;
	}
	
	/*
	 * Gets the target subject description.
	 *
	 * @see me.SyncWise.Android.Modules.Target.Target#getSubjectDescription()
	 */
	@Override
	public String getSubjectDescription () {
		// Return the user description
		return userName;
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
		return targetPreLabel + " " + requiredNumber + " " + targetPostLabel;
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
		// Compute and return the achieved coverage number label
		return achievedNumber + " " + coverageLabel;
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