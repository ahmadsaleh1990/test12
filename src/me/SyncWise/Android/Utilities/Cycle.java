/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Utilities;

import java.util.Calendar;

import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.Users;
import me.SyncWise.Android.Database.UsersDao;

import android.content.Context;

/**
 * Utility class that provides helper methods for related Cycle planning logic computations.
 * 
 * @author Elias
 *
 */
public class Cycle {

	/**
	 * Constant holding the maximum number of allowed daily calls per cycle.
	 */
	public static final int MAX_DAILY_CALLS_PER_CYCLE = 100;
	
	/**
	 * Constant holding the default number of weeks per cycle.
	 * @see Cycle#MAX_WEEK_NUMBER_PER_CYCLE
	 * @see Cycle#MIN_WEEK_NUMBER_PER_CYCLE
	 */
	public static final int DEFAULT_WEEK_NUMBER_PER_CYCLE = 4;
	
	/**
	 * Constant holding the maximum number of weeks per cycle.
	 * @see Cycle#MAX_WEEK_NUMBER_PER_CYCLE
	 * @see Cycle#DEFAULT_WEEK_NUMBER_PER_CYCLE
	 */
	public static final int MAX_WEEK_NUMBER_PER_CYCLE = 6;
	
	/**
	 * Constant holding the minimum number of weeks per cycle.
	 * @see Cycle#DEFAULT_WEEK_NUMBER_PER_CYCLE
	 * @see Cycle#MIN_WEEK_NUMBER_PER_CYCLE
	 */
	public static final int MIN_WEEK_NUMBER_PER_CYCLE = 1;
	
	/**
	 * String holding the default value used to indicate whether or not to allow the cycle to be pushed.
	 */
	public static final String DEFAULT_ALLOW_PUSH_CYCLE = "N";
	
	/**
	 * Gets the number of weeks per cycle for the provided user.<br>
	 * If the number is invalid ({@code NULL}, greater than {@link #MAX_WEEK_NUMBER_PER_CYCLE} or smaller than {@link #MIN_WEEK_NUMBER_PER_CYCLE}), the default value is returned.
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the current user code.
	 * @param companyCode	String hosting the current company code.
	 * @return	Integer holding the weeks number.
	 * @see Cycle#DEFAULT_WEEK_NUMBER_PER_CYCLE
	 */
	public static int getWeekNumberPerCycle ( final Context context , final String userCode , final String companyCode ) {
		// Retrieve the user with the provided user code
		Users user = DatabaseUtils.getInstance ( context ).getDaoSession ().getUsersDao ().queryBuilder ()
			.where ( UsersDao.Properties.UserCode.eq ( userCode ) ,
					UsersDao.Properties.CompanyCode.eq ( companyCode ) ).unique ();
		// Check if the retrieved user is valid
		if ( user == null )
			// Invalid user, return the default value
			return DEFAULT_WEEK_NUMBER_PER_CYCLE;
		// Otherwise the user is valid
		// Check if the user coefficient number is valid
		if ( user.getCoefficientNumber () == null )
			// Invalid coefficient number, return the default value
			return DEFAULT_WEEK_NUMBER_PER_CYCLE;
		// Otherwise the coefficient number is valid
		// Determine if the coefficient number is between max and min
		if ( user.getCoefficientNumber () > MAX_WEEK_NUMBER_PER_CYCLE || user.getCoefficientNumber () < MIN_WEEK_NUMBER_PER_CYCLE )
			// Invalid coefficient number, return the default value
			return DEFAULT_WEEK_NUMBER_PER_CYCLE;
		// Otherwise the coefficient number can be used
		return user.getCoefficientNumber ();
	}
	
	/**
	 * Gets the current cycle number.
	 * 
	 * @param weekNumberPerCycle	Integer holding the number of weeks per cycle, used to compute the current cycle number.
	 * @return	Integer holding the cycle number.
	 */
	public static int getCycleNumber ( final int weekNumberPerCycle ) {
		// Retrieve the current date
		Calendar today = Calendar.getInstance ();
		// Set the first day of the week as Monday
		today.setFirstDayOfWeek ( Calendar.MONDAY );
		// Clear the week number of year to zero to apply the first day of the week new value 
		today.clear ( Calendar.WEEK_OF_YEAR );
		// Return the current cycle number
		return 1 + ( today.get ( Calendar.WEEK_OF_YEAR ) - 1 ) / weekNumberPerCycle;
	}
	
	/**
	 * Gets the current week number in the current cycle.
	 * 
	 * @param weekNumberPerCycle	Integer holding the number of weeks per cycle, used to compute the current cycle number.
	 * @return	Integer holding the week number.
	 */
	public static int getWeekNumber ( final int weekNumberPerCycle ) {
		// Retrieve the current date
		Calendar today = Calendar.getInstance ();
		// Set the first day of the week as Monday
		today.setFirstDayOfWeek ( Calendar.MONDAY );
		// Clear the week number of year to zero to apply the first day of the week new value 
		today.clear ( Calendar.WEEK_OF_YEAR );
		// Compute and return the week number based on the week number per cycle
		return 1 + ( ( today.get ( Calendar.WEEK_OF_YEAR ) - 1 ) % weekNumberPerCycle );
	}
	
}