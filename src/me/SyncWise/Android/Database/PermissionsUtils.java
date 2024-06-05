/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Database;

import me.SyncWise.Android.DateTime;
import me.SyncWise.Android.Modules.Call.CallAction;
import me.SyncWise.Android.Modules.GPSTracking.TrackingUtils;
import me.SyncWise.Android.Modules.Sync.Automatic.AutomaticSyncUtils;
import me.SyncWise.Android.Utilities.BlankVisit;
import me.SyncWise.Android.Utilities.ClientCard;
import me.SyncWise.Android.Utilities.ClientItemClassification;
import me.SyncWise.Android.Utilities.Collection;
import me.SyncWise.Android.Utilities.Cycle;
import me.SyncWise.Android.Utilities.ItemCard;
import me.SyncWise.Android.Utilities.Objective;
import me.SyncWise.Android.Utilities.Transaction;
import android.content.Context;
import android.database.Cursor;

/**
 * Utilities class for generated GreenDao Entities, holding helper methods for additional functionality related to the {@link me.SyncWise.Android.Database.Permissions Permissions} and {@link me.SyncWise.Android.Database.UserPermissions UserPermissions} objects.<br>
 * The following describes the behavior of permissions look up :
 * <ol>
 * <li>The user permission is searched for and used. If none is found, continue to step 2.</li>
 * <li>The general permission is searched for and used. If none is found, continue to step 3.</li>
 * <li>The default hard coded permission is used.</li>
 * </ol>
 * 
 * @author Elias
 *
 */
public class PermissionsUtils {

	/**
	 * String holding the SQL statement used to look up permissions.
	 * 
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	private static final String SQL = "SELECT coalesce ("
			+ "(SELECT " + UserPermissionsDao.Properties.PermissionValue.columnName + " FROM " + UserPermissionsDao.TABLENAME + " WHERE " + UserPermissionsDao.Properties.UserCode.columnName + "=? " +
					"AND " + UserPermissionsDao.Properties.CompanyCode.columnName + "=? AND " + UserPermissionsDao.Properties.PermissionName.columnName + "=?),"
			+ "(SELECT " + PermissionsDao.Properties.PermissionDefaultValue.columnName + " FROM " + PermissionsDao.TABLENAME + " WHERE " + PermissionsDao.Properties.PermissionName.columnName + "=?) ,"
			+ "? ) Permission";
	
	/**
	 * Gets the cursor using {@link #SQL} as the SQL statement along with the provided user code, permission name and default value.
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @param permissionName	String hosting the permission name.
	 * @param defaultValue	String hosting the default value to use if the permission is not found.
	 * @return	A {@link android.database.Cursor Cursor} containing one raw and one column, holding the permission value and positioned before the first entry.
	 */
	private static Cursor getCursor ( final Context context , final String userCode , final String companyCode , final String permissionName , final String defaultValue ) {
		// Compute selection arguments
		String selectionArguments [] = new String [] {
				userCode == null ? "" : userCode ,
				companyCode == null ? "" : companyCode ,
				permissionName ,
				permissionName ,
				defaultValue
		};
		// Query DB and return the cursor result
		return DatabaseUtils.getInstance ( context ).getDaoSession ().getDatabase ().rawQuery ( SQL , selectionArguments );
	}
	
	/**
	 * Gets the {@code WORKING_HOUR_START} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.DateTime#WORKING_HOUR_START DateTime.WORKING_HOUR_START}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Integer holding the starting working hour.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static Integer getStartingWorkingHour ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"WORKING_HOUR_START" ,
				String.valueOf ( DateTime.WORKING_HOUR_START ) );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Retrieve the maximum daily calls per cycle
		Integer value = Integer.parseInt ( cursor.getString ( 0 ) );
		// Close cursor
		cursor.close ();
		// Check if the value is valid
		if ( value < 0 || value > 23 )
			// Use the default value
			value = DateTime.WORKING_HOUR_START;
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code WORKING_HOUR_END} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.DateTime#WORKING_HOUR_END DateTime.WORKING_HOUR_END}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Integer holding the starting working hour.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static Integer getEndingWorkingHour ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"WORKING_HOUR_END" ,
				String.valueOf ( DateTime.WORKING_HOUR_END ) );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Retrieve the maximum daily calls per cycle
		Integer value = Integer.parseInt ( cursor.getString ( 0 ) );
		// Close cursor
		cursor.close ();
		// Check if the value is valid
		if ( value < 0 || value > 23 )
			// Use the default value
			value = DateTime.WORKING_HOUR_END;
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code MAX_DAILY_CALLS_PER_CYCLE} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.Cycle#MAX_DAILY_CALLS_PER_CYCLE Cycle.MAX_DAILY_CALLS_PER_CYCLE}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Integer holding the maximum daily calls per cycle.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static Integer getMaxDailyCallsPerCycle ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"MAX_DAILY_CALLS_PER_CYCLE" ,
				String.valueOf ( Cycle.MAX_DAILY_CALLS_PER_CYCLE ) );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Retrieve the maximum daily calls per cycle
		Integer value = Integer.parseInt ( cursor.getString ( 0 ) );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code VISIT_HISTORY_WEEKS} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Database.VisitsUtils#DEFAULT_VISIT_HISTORY_WEEKS VisitsUtils.DEFAULT_VISIT_HISTORY_WEEKS}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Integer holding the number of weeks that the visit history covers.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static Integer getVisitHistoryWeeks ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"VISIT_HISTORY_WEEKS" ,
				String.valueOf ( VisitsUtils.DEFAULT_VISIT_HISTORY_WEEKS ) );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Retrieve the number of weeks that the visit history covers
		Integer value = Integer.parseInt ( cursor.getString ( 0 ) );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code NOTIFY_PROVIDER_STATUS} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Modules.GPSTracking.TrackingUtils#DEFAULT_NOTIFY_PROVIDER_STATUS TrackingUtils.DEFAULT_NOTIFY_PROVIDER_STATUS}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether notify the provided status or not.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getNotifyProviderStatus ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"NOTIFY_PROVIDER_STATUS" ,
				TrackingUtils.DEFAULT_NOTIFY_PROVIDER_STATUS );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to notify the provided status or not
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code TRACKING_CORE_DISTANCE} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Modules.GPSTracking.TrackingUtils#DEFAULT_TRACKING_CORE_DISTANCE TrackingUtils.DEFAULT_TRACKING_CORE_DISTANCE}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Integer holding the maximum daily calls per cycle.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static Integer getTrackingCoreDistance ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"TRACKING_CORE_DISTANCE" ,
				String.valueOf ( TrackingUtils.DEFAULT_TRACKING_CORE_DISTANCE ) );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Retrieve the tracking core distance
		Integer value = Integer.parseInt ( cursor.getString ( 0 ) );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code TRACKING_FREQUENCY} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Modules.GPSTracking.TrackingUtils#DEFAULT_TRACKING_CORE_DISTANCE TrackingUtils.DEFAULT_TRACKING_CORE_DISTANCE}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Long holding the tracking frequency in milliseconds.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static Long getTrackingFrequency ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"TRACKING_FREQUENCY" ,
				String.valueOf ( TrackingUtils.DEFAULT_ALARM_FREQUENCY ) );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Retrieve the tracking core distance
		Long value = Long.parseLong ( cursor.getString ( 0 ) );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code ENABLE_AUTO_SYNC} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Modules.Sync.Automatic.AutomaticSyncUtils#DEFAULT_ENABLE_AUTO_SYNC AutomaticSyncUtils.DEFAULT_ENABLE_AUTO_SYNC}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether to enable auto sync or not.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnableAutoSync ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"ENABLE_AUTO_SYNC" ,
				AutomaticSyncUtils.DEFAULT_ENABLE_AUTO_SYNC );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to enable auto sync or not
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code AUTO_SYNC_FREQUENCY} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Modules.Sync.Automatic.AutomaticSyncUtils#DEFAULT_AUTO_SYNC_FREQUENCY AutomaticSyncUtils.DEFAULT_AUTO_SYNC_FREQUENCY}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Long holding the auto sync frequency in milliseconds.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static Long getAutoSyncFrequency ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"AUTO_SYNC_FREQUENCY" ,
				String.valueOf ( AutomaticSyncUtils.DEFAULT_AUTO_SYNC_FREQUENCY ) );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Retrieve the tracking core distance
		Long value = Long.parseLong ( cursor.getString ( 0 ) );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code ENFORCE_OBJECTIVE_COMPLETION_NOTE} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.Objective#DEFAULT_ENFORCE_OBJECTIVE_COMPLETION_NOTE Objective.DEFAULT_ENFORCE_OBJECTIVE_COMPLETION_NOTE}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether or not to enforce the objective completion note.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnforceObjectiveCompletionNote ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"ENFORCE_OBJECTIVE_COMPLETION_NOTE" ,
				Objective.DEFAULT_ENFORCE_OBJECTIVE_COMPLETION_NOTE );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether or not to enforce the objective completion note.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code ALLOW_PUSH_CYCLE} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.Cycle#DEFAULT_ALLOW_PUSH_CYCLE Cycle.DEFAULT_ALLOW_PUSH_CYCLE}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether to allow the cycle to be pushed.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getAllowPushCycle ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"ALLOW_PUSH_CYCLE" ,
				Cycle.DEFAULT_ALLOW_PUSH_CYCLE );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to allow the cycle to be pushed
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	/** ahmad
	 * Gets the {@code Enable_Direct_Return} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.TransactionClientCard#DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS TransactionClientCard.DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether or not the promotions are applied in the sales order taking.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnableDirectReturn ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"ENABLE_Direct_Return" ,
				Transaction.ENABLE_Direct_Return );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to apply or not the promotions are applied in the sales order taking.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	/** ahmad
	 * Gets the {@code Enable_Direct_Return} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.TransactionClientCard#DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS TransactionClientCard.DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether or not the promotions are applied in the sales order taking.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnableSalesOrderCashOrCredit ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"Enable_Sales_Order_Cash_Or_Credit" ,
				Transaction.Enable_Sales_Order_Cash_Or_Credit );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to apply or not the promotions are applied in the sales order taking.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/** ahmad
	 * Gets the {@code Enable_Physical_Direct_Unload} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.TransactionClientCard#DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS TransactionClientCard.DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether or not the promotions are applied in the sales order taking.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnablePhysicalDirectUnload ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"Enable_Physical_Direct_Unload" ,
				Transaction.ENABLE_Direct_Return );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to apply or not the promotions are applied in the sales order taking.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	/** ahmad
	 * Gets the {@code Enable_Company_Collection} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.TransactionClientCard#DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS TransactionClientCard.DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether or not the promotions are applied in the sales order taking.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnableCompanyCollection ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"Enable_Company_Collection" ,
				Transaction.ENABLE_Direct_Return );
		 // Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to apply or not the promotions are applied in the sales order taking.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	
	public static boolean getEnableTypeCollection ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"Enable_Type_Collection" ,
				Transaction.DEFAULT_ENFORCE_MSL_CONFIRMATION );
		 // Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to apply or not the promotions are applied in the sales order taking.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/** ahmad
	 * Gets the {@code Enable_Company_Collection} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.TransactionClientCard#DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS TransactionClientCard.DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether or not the promotions are applied in the sales order taking.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnableUnloadLessVehicleStock ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"Enable_Unload_Max_Vehicle_Stock" ,
				Transaction.ENABLE_Direct_Return );
		 // Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to apply or not the promotions are applied in the sales order taking.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/** ahmad
	 * Gets the {@code Enable_Company_Collection} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.TransactionClientCard#DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS TransactionClientCard.DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether or not the promotions are applied in the sales order taking.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnableUnloadEqualVehiculeStock ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"Enable_Unload_Equal_Vehicle_Stock" ,
				Transaction.ENABLE_Direct_Return );
		 // Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to apply or not the promotions are applied in the sales order taking.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	
	
	
	
	/** ahmad
	 * Gets the {@code Enable_Physical_Direct_Unload} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.TransactionClientCard#DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS TransactionClientCard.DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether or not the promotions are applied in the sales order taking.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnablePhysicalDirectLoad ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"Enable_Physical_Direct_Load" ,
				Transaction.ENABLE_Direct_Return );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to apply or not the promotions are applied in the sales order taking.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code NO_REASON_IF_BLANK_VISIT} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Modules.Call.CallAction#NO_REASON_IF_BLANK_VISIT CallAction.NO_REASON_IF_BLANK_VISIT}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether to prompt for a reason if a blank visit is performed during a call.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getNoReasonIfBlankVisit ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"NO_REASON_IF_BLANK_VISIT" ,
				CallAction.NO_REASON_IF_BLANK_VISIT );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to prompt for a reason
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code PROMPT_OBJECTIVE_REASON} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Modules.Call.CallAction#PROMPT_OBJECTIVE_REASON CallAction.PROMPT_OBJECTIVE_REASON}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether to prompt for a reason if no objective is performed during a call.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getPromptObjectiveReason ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"PROMPT_OBJECTIVE_REASON" ,
				CallAction.PROMPT_OBJECTIVE_REASON );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to prompt for a reason
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code PROMPT_SURVEY_REASON} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Modules.Call.CallAction#PROMPT_SURVEY_REASON CallAction.PROMPT_SURVEY_REASON}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether to prompt for a reason if no survey is performed during a call.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getPromptSurveyReason ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"PROMPT_SURVEY_REASON" ,
				CallAction.PROMPT_SURVEY_REASON );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to prompt for a reason
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code PROMPT_SALES_REASON} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Modules.Call.CallAction#PROMPT_SALES_REASON CallAction.PROMPT_SALES_REASON}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether to prompt for a reason if no sales is performed during a call.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getPromptSalesReason ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"PROMPT_SALES_REASON" ,
				CallAction.PROMPT_SALES_REASON );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to prompt for a reason
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code ADD_CLIENT_ITEM_CLASSIFICATION} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.ClientItemClassification#ADD_CLIENT_ITEM_CLASSIFICATION ClientItemClassification.ADD_CLIENT_ITEM_CLASSIFICATION}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether to the user can add client item classifications or not.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getAddClientItemClassification ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"ADD_CLIENT_ITEM_CLASSIFICATION" ,
				ClientItemClassification.ADD_CLIENT_ITEM_CLASSIFICATION );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to allow the user to add client item classifications
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code EDIT_CLIENT_ITEM_CLASSIFICATION} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.ClientItemClassification#EDIT_CLIENT_ITEM_CLASSIFICATION ClientItemClassification.EDIT_CLIENT_ITEM_CLASSIFICATION}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether to the user can edit client item classifications or not.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEditClientItemClassification ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"EDIT_CLIENT_ITEM_CLASSIFICATION" ,
				ClientItemClassification.EDIT_CLIENT_ITEM_CLASSIFICATION );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to allow the user to edit client item classifications
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code ACHIEVEMENTS_INCLUDE_OUTOFROUTE} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Database.VisitsUtils#DEFAULT_ACHIEVEMENTS_INCLUDE_OUTOFROUTE VisitsUtils.DEFAULT_ACHIEVEMENTS_INCLUDE_OUTOFROUTE}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether or not to indicate if the achievements include the out of route visits.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getAchievementsIncludeOutOfRoute ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"ACHIEVEMENTS_INCLUDE_OUTOFROUTE" ,
				VisitsUtils.DEFAULT_ACHIEVEMENTS_INCLUDE_OUTOFROUTE );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether or not to indicate if the achievements include the out of route visits.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code VISIT_CALL_DIFFERENCE} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Modules.Call.CallAction#VISIT_CALL_DIFFERENCE CallAction.VISIT_CALL_DIFFERENCE}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether a visit and a call should be differentiated or not.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getVisitCallDifference ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"VISIT_CALL_DIFFERENCE" ,
				CallAction.VISIT_CALL_DIFFERENCE );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to lock the user's call action after a blank visit
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code BLANK_VISIT_LOCK} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.BlankVisit#DEFAULT_BLANK_VISIT_LOCK BlankVisit.DEFAULT_BLANK_VISIT_LOCK}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether to the user call actions are lock after a blank visit or not.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getBlankVisitLock ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"BLANK_VISIT_LOCK" ,
				BlankVisit.DEFAULT_BLANK_VISIT_LOCK );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to lock the user's call action after a blank visit
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code ITEM_BARCODES} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.ItemCard#DEFAULT_ITEM_BARCODES ItemCard.DEFAULT_ITEM_BARCODES}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether or not the user is allowed to use item barcodes.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getItemBarcodes ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"ITEM_BARCODES" ,
				ItemCard.DEFAULT_ITEM_BARCODES );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to allow the user to use item barcodes
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code CLIENT_BARCODES} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.ClientCard#DEFAULT_CLIENT_BARCODES ClientCard.DEFAULT_CLIENT_BARCODES}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether or not the user should scan client barcodes.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getClientBarcodes ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"CLIENT_BARCODES" , 
				ClientCard.DEFAULT_CLIENT_BARCODES );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to allow the user to use client barcodes
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code ENABLE_SALES_ORDER_PROMOTIONS} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.TransactionClientCard#DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS TransactionClientCard.DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether or not the promotions are applied in the sales order taking.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnableSalesOrderPromotions ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"ENABLE_SALES_ORDER_PROMOTIONS" ,
				Transaction.DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to apply or not the promotions are applied in the sales order taking.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	/**
	 * Gets the {@code ENABLE_SALES_ORDER_PROMOTIONS} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.TransactionClientCard#DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS TransactionClientCard.DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether or not the promotions are applied in the sales order taking.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnableSalesOrderPromotionsLimit ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"ENABLE_SALES_ORDER_PROMOTIONS_LIMIT" ,
				Transaction.DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to apply or not the promotions are applied in the sales order taking.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}/**
	 * Gets the {@code ENABLE_SALES_ORDER_PROMOTIONS} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.TransactionClientCard#DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS TransactionClientCard.DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether or not the promotions are applied in the sales order taking.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnablePrintInvoiceFreshQQP ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"ENABLE_PRINT_INVOICE_FRESH_QQP" ,
				Transaction.DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to apply or not the promotions are applied in the sales order taking.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	/**
	 * Gets the {@code APPLY_CLIENT_CREDIT_LIMIT} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.ClientCard#DEFAULT_APPLY_CLIENT_CREDIT_LIMIT ClientCard.DEFAULT_APPLY_CLIENT_CREDIT_LIMIT}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether or not the client credit limit is applied.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getApplyClientCreditLimit ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"APPLY_CLIENT_CREDIT_LIMIT" ,
				ClientCard.DEFAULT_APPLY_CLIENT_CREDIT_LIMIT );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to apply or not the client credit limit.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code RFR_LIMIT_AMOUNT} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.Transaction#DEFAULT_RFR_LIMIT_AMOUNT Transaction.DEFAULT_RFR_LIMIT_AMOUNT}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Integer holding the value used to as RFR limit amount.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static Integer getRFRLimitAmount ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"RFR_LIMIT_AMOUNT" ,
				String.valueOf ( Transaction.DEFAULT_RFR_LIMIT_AMOUNT ) );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Retrieve the integer holding the value used to as RFR limit amount
		Integer value = Integer.parseInt ( cursor.getString ( 0 ) );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code ENFORCE_MSL_CONFIRMATION} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.Transaction#DEFAULT_ENFORCE_MSL_CONFIRMATION Transaction.DEFAULT_ENFORCE_MSL_CONFIRMATION}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether to enforce the client must stock list item confirmation.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnforceMSLConfirmation ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"ENFORCE_MSL_CONFIRMATION" ,
				Transaction.DEFAULT_ALLOW_MULTIPLE_COLLECTION );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to enforce the client must stock list item confirmation
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code RFR_LIMIT_MARGIN} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.Transaction#DEFAULT_RFR_LIMIT_MARGIN Transaction.DEFAULT_RFR_LIMIT_MARGIN}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Integer holding the value used to as RFR limit margin.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static Integer getRFRLimitMargin ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"RFR_LIMIT_MARGIN" ,
				String.valueOf ( Transaction.DEFAULT_RFR_LIMIT_MARGIN ) );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Retrieve the integer holding the value used to as RFR limit margin
		Integer value = Integer.parseInt ( cursor.getString ( 0 ) );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code RFR_MIN_EXPIRY_DATE} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.Transaction#DEFAULT_MIN_RFR_EXPIRY_DATE Transaction.DEFAULT_MIN_RFR_EXPIRY_DATE}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Integer holding the minimum RFR expiry date period.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static int getRFRMinExpiryDate ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"RFR_MIN_EXPIRY_DATE" ,
				String.valueOf ( Transaction.DEFAULT_MIN_RFR_EXPIRY_DATE ) );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Retrieve the the minimum RFR expiry date period
		int  value = cursor.getInt ( 0 );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code ALLOW_COLLECTION_REPRINT} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.Collection#DEFAULT_ALLOW_COLLECTION_REPRINT Collection.DEFAULT_ALLOW_COLLECTION_REPRINT}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether to allow the collections to be reprinted.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getAllowCollectionReprint ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"ALLOW_COLLECTION_REPRINT" ,
				Collection.DEFAULT_ALLOW_COLLECTION_REPRINT );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to allow the collections to be reprinted or not
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code ALLOW_CASH_CHECK} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.Collection#DEFAULT_ALLOW_CASH_CHECK Collection.DEFAULT_ALLOW_CASH_CHECK}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether to forward the sales order screen to the RFR screen or not.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getAllowCashCheck ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"ALLOW_CASH_CHECK" ,
				Collection.DEFAULT_ALLOW_CASH_CHECK);
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to forward the sales order screen to the RFR screen or not
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code PRINT_COLLECTION_STAMP} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.Collection#DEFAULT_PRINT_COLLECTION_STAMP Collection.DEFAULT_PRINT_COLLECTION_STAMP}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether to print a duty stamp on the printed collection receipt.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getPrintCollectionStamp ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"PRINT_COLLECTION_STAMP" ,
				Collection.DEFAULT_PRINT_COLLECTION_STAMP);
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to print the collection stamp or not
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code USE_PREDEFINED_COLLECTION_STAMP} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.Collection#DEFAULT_USE_PREDEFINED_COLLECTION_STAMP Collection.DEFAULT_USE_PREDEFINED_COLLECTION_STAMP}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether to use a static / pre-defined duty stamp on the printed collection receipt.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getUsePredefinedCollectionStamp ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"USE_PREDEFINED_COLLECTION_STAMP" ,
				Collection.DEFAULT_USE_PREDEFINED_COLLECTION_STAMP );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to use a pre-defined collection stamp or not
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code PREDEFINED_COLLECTION_STAMP} permission.<br>
	 * The default value is an empty string.
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	String hosting the static / pre-defined duty stamp used on the printed collection receipt.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static String getPredefinedCollectionStamp ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"PREDEFINED_COLLECTION_STAMP" ,
				"" );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Store the predefined stamp
		String value = cursor.getString ( 0 );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code ENFORCE_STOCK_EXPIRY_DATE} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.ClientStockCountHeadersUtils#DEFAULT_ENFORCE_STOCK_EXPIRY_DATE ClientStockCountHeadersUtils.DEFAULT_ENFORCE_STOCK_EXPIRY_DATE}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether to enforce the expiry date or not during a client stock count.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnforceStockExpiryDate ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"ENFORCE_STOCK_EXPIRY_DATE" ,
				ClientStockCountHeadersUtils.DEFAULT_ENFORCE_STOCK_EXPIRY_DATE );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to enforce the expiry date or not during a client stock count
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}

	/**
	 * Gets the {@code ENFORCE_UNLOAD_REQUEST_EXPIRY_DATE} permission.<br>
	 * The default value is <b>N</b>.
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether to enforce the expiry date or not during a unload request.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnforceUnloadRequestExpiryDate ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"ENFORCE_UNLOAD_REQUEST_EXPIRY_DATE" ,
				"N" );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to enforce the expiry date or not during an unload request
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code ENFORCE_LOAD_EXPIRY_DATE} permission.<br>
	 * The default value is <b>N</b>.
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether to enforce the expiry date or not during a load.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnforceLoadExpiryDate ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"ENFORCE_LOAD_EXPIRY_DATE" ,
				"N" );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to enforce the expiry date or not during a load
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code ENFORCE_UNLOAD_EXPIRY_DATE} permission.<br>
	 * The default value is <b>N</b>.
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether to enforce the expiry date or not during an unload.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnforceUnloadExpiryDate ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"ENFORCE_UNLOAD_EXPIRY_DATE" ,
				"N" );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to enforce the expiry date or not during an unload
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code ENFORCE_STOCK_RECONCILIATION_EXPIRY_DATE} permission.<br>
	 * The default value is <b>N</b>.
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether to enforce the expiry date or not during a stock reconciliation.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnforceStockReconciliationExpiryDate ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"ENFORCE_STOCK_RECONCILIATION_EXPIRY_DATE" ,
				"N" );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to enforce the expiry date or not during a stock reconciliation
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code ENFORCE_UNLOAD_REQUEST_REASON} permission.<br>
	 * The default value is <b>N</b>.
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether to enforce the reason or not during a unload request.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnforceUnloadRequestReason ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"ENFORCE_UNLOAD_REQUEST_REASON" ,
				"N" );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to enforce the reason or not during an unload request
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code ENFORCE_LOAD_REASON} permission.<br>
	 * The default value is <b>N</b>.
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether to enforce the reason or not during a load.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnforceLoadReason ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"ENFORCE_LOAD_REASON" ,
				"N" );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to enforce the reason or not during a load
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code ENFORCE_UNLOAD_REASON} permission.<br>
	 * The default value is <b>N</b>.
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether to enforce the reason or not during an unload.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnforceUnloadReason ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"ENFORCE_UNLOAD_REASON" ,
				"N" );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to enforce the reason or not during an unload
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code Sys_PassCode_Interval} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Database.UserPasswordsUtils#DEFAULT_PASSWORD_MINUTE_INTERVAL UserPasswordsUtils.DEFAULT_PASSWORD_MINUTE_INTERVAL}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether to forward the sales order screen to the RFR screen or not.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static int getPasswordMinuteInterval ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"Sys_PassCode_Interval" ,
				String.valueOf ( UserPasswordsUtils.DEFAULT_PASSWORD_MINUTE_INTERVAL ) );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Retrieve the interval
		int  value = cursor.getInt ( 0 );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code SKIP_REGULAR_PASSCODE} permission.<br>
	 * The default value is an empty string.
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	String hosting the default passcode.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static String getDefaultRegularPassword ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"SKIP_REGULAR_PASSCODE" ,
				"" );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Retrieve the pass code
		String value = cursor.getString ( 0 );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code SKIP_TIME_PASSCODE} permission.<br>
	 * The default value is an empty string.
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	String hosting the default passcode.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static String getDefaultTimePassword ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"SKIP_TIME_PASSCODE" ,
				"" );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Retrieve the pass code
		String value = cursor.getString ( 0 );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code SYS_Default_Price_List} permission.<br>
	 * The default value is <b>RETL</b>.
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	String hosting the user default price list code.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static String getDefaultPriceListCode ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"SYS_Default_Price_List" ,
				"RETL" );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Store the price list code
		String value = cursor.getString ( 0 );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/**
	 * Gets the {@code Allowed_VehicleLoad_Volume} permission.<br>
	 * The default value is <b>75 %</b>.
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Double hosting the vehicle load percentage.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static double getVehicleLoadPercentage ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"Allowed_VehicleLoad_Volume" ,
				"75" );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Retrieve the percentage
		double value = cursor.getDouble ( 0 );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	/** ahmad
	 * Gets the {@code Enable_Direct_Return} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.TransactionClientCard#DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS TransactionClientCard.DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether or not the promotions are applied in the sales order taking.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnableInvoicePrint ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"ENABLE_INVOICE_PRINT_MESSAGE" ,
				Transaction.ENABLE_Direct_Return );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to apply or not the promotions are applied in the sales order taking.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	/** ahmad
	 * Gets the {@code Enable_Direct_Return} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.TransactionClientCard#DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS TransactionClientCard.DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether or not the promotions are applied in the sales order taking.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnableCashInvoiceCredit ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"ENABLE_CASH_INVOICE_CREDIT" ,
				Transaction.ENABLE_Direct_Return );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to apply or not the promotions are applied in the sales order taking.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	
	/** ahmad
	 * Gets the {@code Enable_Direct_Return} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.TransactionClientCard#DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS TransactionClientCard.DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether or not the promotions are applied in the sales order taking.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnableDateSummary ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"ENABLE_DATE_SUMMARY" ,
				Transaction.ENABLE_Direct_Return );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to apply or not the promotions are applied in the sales order taking.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	/** ahmad
	 * Gets the {@code Enable_Direct_Return} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.TransactionClientCard#DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS TransactionClientCard.DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether or not the promotions are applied in the sales order taking.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnableSalesInvoceCashOrCredit ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"Enable_Sales_INVOICE_Cash_Or_Credit" ,
				Transaction.Enable_Sales_Order_Cash_Or_Credit );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to apply or not the promotions are applied in the sales order taking.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	/** ahmad
	 * Gets the {@code Enable_Direct_Return} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.TransactionClientCard#DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS TransactionClientCard.DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether or not the promotions are applied in the sales order taking.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnableOutOfRoutePasscode ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"Enable_Out_Of_Route_Passcode" ,
				Transaction.ENABLE_Direct_Return );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to apply or not the promotions are applied in the sales order taking.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/** ahmad
	 * Gets the {@code Enable_Direct_Return} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.TransactionClientCard#DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS TransactionClientCard.DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether or not the promotions are applied in the sales order taking.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnableSalesOrderCashOrCreditClientPriceList ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"Enable_Sales_Order_Client_Price_List" ,
				Transaction.Enable_Sales_Order_Cash_Or_Credit );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to apply or not the promotions are applied in the sales order taking.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	/**
	 * Print the {@code Discount_Invoice} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.TransactionClientCard#DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS TransactionClientCard.DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether or not the promotions are applied in the sales order taking.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getDiscountInvoicePrint ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"PRINT_INVOICE_DISCOUNT" ,
				Transaction.ENABLE_Direct_Return );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to apply or not the promotions are applied in the sales order taking.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/** ahmad
	 * Gets the {@code Enable_Direct_Return} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.TransactionClientCard#DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS TransactionClientCard.DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether or not the promotions are applied in the sales order taking.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnableLoadUnloadDirect ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"Enable_Direct_Load_Unload_Suggested" ,
				Transaction.Enable_Sales_Order_Cash_Or_Credit );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to apply or not the promotions are applied in the sales order taking.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/** ahmad
	 * Gets the {@code Enable_Direct_Return} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.TransactionClientCard#DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS TransactionClientCard.DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether or not the promotions are applied in the sales order taking.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnableCheckCollection ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"Enable_Check_Collection" ,
				Transaction.Enable_Sales_Order_Cash_Or_Credit );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to apply or not the promotions are applied in the sales order taking.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	/**Print the {@code Horizontal_Line} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.TransactionClientCard#DEFAULT_PRINT_HORIZONTAL_LINE }
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether or not the promotions are applied in the sales order taking.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getPrintHorizontalLine ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"PRINT_HORIZONTAL_LINE" ,
				Transaction.ENABLE_Direct_Return );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to apply or not the promotions are applied in the sales order taking.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	
	
	/**
	 * Gets the {@code FORCE_OBJECTIVE_END} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Modules.Sync.Automatic.AutomaticSyncUtils#DEFAULT_AUTO_SYNC_FREQUENCY AutomaticSyncUtils.DEFAULT_AUTO_SYNC_FREQUENCY}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Long holding the auto sync frequency in milliseconds.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static Double getForceObjectiveEnd ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"FORCE_OBJECTIVE_END" ,
				String.valueOf ( 90) );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Retrieve the tracking core distance
		Double value = Double.parseDouble ( cursor.getString ( 0 ) );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	/** ahmad
	 * Gets the {@code Enable_Company_Collection} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.TransactionClientCard#DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS TransactionClientCard.DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether or not the promotions are applied in the sales order taking.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnableCashEndVisitsNoPassword ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"Enable_Cash_End_Visit_No_Password" ,
				Transaction.ENABLE_Direct_Return );
		 // Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to apply or not the promotions are applied in the sales order taking.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	/** ahmad
	 * Gets the {@code Enable_Company_Collection} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.TransactionClientCard#DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS TransactionClientCard.DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether or not the promotions are applied in the sales order taking.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnableCreditEndVisitsNoPassword ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"Enable_Credit_End_Visit_No_Password" ,
				Transaction.ENABLE_Direct_Return );
		 // Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to apply or not the promotions are applied in the sales order taking.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/** ahmad
	 * Gets the {@code Enable_Company_Collection} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.TransactionClientCard#DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS TransactionClientCard.DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether or not the promotions are applied in the sales order taking.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getPriceListFromBO ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"PriceList_From_bo" ,
			  Transaction.ENABLE_Direct_Return );
		 // Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to apply or not the promotions are applied in the sales order taking.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	/** ahmad
	 * Gets the {@code Enable_Company_Collection} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.TransactionClientCard#DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS TransactionClientCard.DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether or not the promotions are applied in the sales order taking.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnableCreditEndVisitsCashSelected( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"Enable_Credit_End_Visit_Cash_Selected" ,
				Transaction.ENABLE_Direct_Return );
		 // Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to apply or not the promotions are applied in the sales order taking.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	/** ahmad
	 * Gets the {@code Enable_Direct_Return} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.TransactionClientCard#DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS TransactionClientCard.DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether or not the promotions are applied in the sales order taking.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getRemoveCreditCollectionPrint ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"Remove_Credit_Collection_Summary" ,
				Transaction.ENABLE_Direct_Return );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to apply or not the promotions are applied in the sales order taking.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	/** ahmad
	 * Gets the {@code Enable_Direct_Return} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Utilities.TransactionClientCard#DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS TransactionClientCard.DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether or not the promotions are applied in the sales order taking.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getRemoveWareHouseKeeper ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"Remove_Ware_House_keeper" ,
				Transaction.ENABLE_Direct_Return );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to apply or not the promotions are applied in the sales order taking.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	/**
	 * Gets the {@code AUTO_SYNC_FREQUENCY} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Modules.Sync.Automatic.AutomaticSyncUtils#DEFAULT_AUTO_SYNC_FREQUENCY AutomaticSyncUtils.DEFAULT_AUTO_SYNC_FREQUENCY}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Long holding the auto sync frequency in milliseconds.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static Integer getTimeOut ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"TIME_OUT_TIME" ,
				String.valueOf ( 30000 ) );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Retrieve the tracking core distance
		Integer value = Integer.parseInt ( cursor.getString ( 0 ) );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	/**
	 * Gets the {@code ENABLE_AUTO_SYNC} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Modules.Sync.Automatic.AutomaticSyncUtils#DEFAULT_ENABLE_AUTO_SYNC AutomaticSyncUtils.DEFAULT_ENABLE_AUTO_SYNC}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @param companyCode	String hosting the company code.
	 * @return	Boolean indicating whether to enable auto sync or not.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static boolean getEnableGpsTracking ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"ENABLE_GPS_TRACKING" ,
				AutomaticSyncUtils.DEFAULT_ENABLE_AUTO_SYNC );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to enable auto sync or not
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	/**
	 * Gets the {@code TRACKING_CORE_DISTANCE} permission.<br>
	 * The default value is defined by {@link me.SyncWise.Android.Modules.GPSTracking.TrackingUtils#DEFAULT_TRACKING_CORE_DISTANCE TrackingUtils.DEFAULT_TRACKING_CORE_DISTANCE}
	 * 
	 * @param context	The application context.
	 * @param userCode	String hosting the user code.
	 * @return	Integer holding the maximum daily calls per cycle.
	 * @see me.SyncWise.Android.Database.PermissionsUtils
	 */
	public static Integer getSummaryWeeks ( final Context context , final String userCode , final String companyCode) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode ,companyCode,
				"SUMMARY_WEEKS1" ,
				String.valueOf ( 60 ) );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Retrieve the tracking core distance
		Integer value = Integer.parseInt ( cursor.getString ( 0 ) );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	public static boolean getFillAllShareOfShalve ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"Fill_All_Share_Of_Shalve" ,
				Collection.DEFAULT_ALLOW_COLLECTION_REPRINT);
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to forward the sales order screen to the RFR screen or not
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	public static boolean getSendSerialTablet ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"ENABLE_SEND_SERIAL_TABLET" ,
				Transaction.DEFAULT_ENFORCE_MSL_CONFIRMATION );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to apply or not the promotions are applied in the sales order taking.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	public static boolean getNewSortingOrder ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"NEW_SORTING_ORDER" ,
				ItemCard.DEFAULT_ITEM_BARCODES );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to allow the user to use item barcodes
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	public static boolean getDisplayRfrPrice ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"DISPLAY_RFR_PRICE" ,
				ItemCard.DEFAULT_ITEM_BARCODES );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to allow the user to use item barcodes
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	public static boolean getDisplaySequenceChange ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"DISPLAY_SEQUENCE_CHANGE" ,
				ClientItemClassification.ADD_CLIENT_ITEM_CLASSIFICATION );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to allow the user to add client item classifications
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	public static Long getDisconnectedDuo ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"DISCONNECTED_DUO" ,
				String.valueOf ( 60000  ) );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Retrieve the tracking core distance
		Long value = Long.parseLong ( cursor.getString ( 0 ) );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	public static String getIntegrationKey ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"IntegrationKey" ,
				String.valueOf ( "DI9TELVXWN40VAJT4C9O" ) );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Retrieve the tracking core distance
		String value =  cursor.getString ( 0 )  ;
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	public static String getSecretKey ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"SecretKey" ,
				String.valueOf ( "FejZmJ0XrjAR7e5pFOx3FiH9DyiC7eOLKDo9bC8w" ) );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Retrieve the tracking core distance
		String value =  cursor.getString ( 0 )  ;
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	public static String getHostName ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"HostName" ,
				String.valueOf ( "api-9c5519ac.duosecurity.com" ) );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Retrieve the tracking core distance
		String value =  cursor.getString ( 0 )  ;
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	
	public static String getDuoDefaultUser ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"DEFAULT_DUO_USER" ,
				String.valueOf ( "Syncwise.Ahmad" ) );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Retrieve the tracking core distance
		String value =  cursor.getString ( 0 )  ;
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	public static boolean getENABLEUserDefaultDUO ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"ENABLE_DEFAULT_DUE" ,
				Transaction.DEFAULT_ALLOW_MULTIPLE_COLLECTION );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to apply or not the promotions are applied in the sales order taking.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	public static String getavoidDuoUser ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"AVOID_DUO_USER" ,
				String.valueOf ( "1122" ) );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Retrieve the tracking core distance
		String value =  cursor.getString ( 0 )  ;
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	public static double getSleepDuo ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"Sleep_Time_Duo" ,
				"1000" );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Retrieve the percentage
		double value = cursor.getDouble ( 0 );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
	public static boolean getEnableDuo ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"Enable_Duo" , 
				ClientCard.ENABLE_DUO );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to allow the user to use client barcodes
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
		public static boolean getEnableDisplayOldShareOfShalf ( final Context context , final String userCode , final String companyCode ) {
		// Query DB
		Cursor cursor = getCursor ( context ,
				userCode , companyCode ,
				"Display_Old_Share_Of_Shalf" ,
				Transaction.DEFAULT_ENFORCE_MSL_CONFIRMATION );
		// Move cursor to the first raw
		cursor.moveToFirst ();
		// Determine whether to apply or not the promotions are applied in the sales order taking.
		boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
		// Close cursor
		cursor.close ();
		// Return the value
		return value;
	}
		public static boolean getEnableTargets ( final Context context , final String userCode , final String companyCode ) {
			// Query DB
			Cursor cursor = getCursor ( context ,
					userCode , companyCode ,
					"Enable_Targets" , 
					ClientCard.DEFAULT_APPLY_CLIENT_CREDIT_LIMIT );
			// Move cursor to the first raw
			cursor.moveToFirst ();
			// Determine whether to allow the user to use client barcodes
			boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
			// Close cursor
			cursor.close ();
			// Return the value
			return value;
		}
		public static boolean getForceSolveSequence ( final Context context , final String userCode , final String companyCode ) {
			// Query DB
			Cursor cursor = getCursor ( context ,
					userCode , companyCode ,
					"FORCE_SOLVE_SEQUENCE" ,
					Transaction.DEFAULT_ENABLE_SALES_ORDER_PROMOTIONS );
			// Move cursor to the first raw
			cursor.moveToFirst ();
			// Determine whether to apply or not the promotions are applied in the sales order taking.
			boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
			// Close cursor
			cursor.close ();
			// Return the value
			return value;
		}
		public static String getDefaultTimePassword1 ( final Context context , final String userCode , final String companyCode ) {
			// Query DB
			Cursor cursor = getCursor ( context ,
					userCode , companyCode ,
					"SKIP_TIME_PASSCODE123" ,
					"135" );
			// Move cursor to the first raw
			cursor.moveToFirst ();
			// Retrieve the pass code
			String value = cursor.getString ( 0 );
			// Close cursor
			cursor.close ();
			// Return the value
			return value;
		}

		public static String getJourneyTimePassword ( final Context context , final String userCode , final String companyCode ) {
			// Query DB
			Cursor cursor = getCursor ( context ,
					userCode , companyCode ,
					"SKIP_TIME_PASSCODE_JOURNEY" ,
					"324" );
			// Move cursor to the first raw
			cursor.moveToFirst ();
			// Retrieve the pass code
			String value = cursor.getString ( 0 );
			// Close cursor
			cursor.close ();
			// Return the value
			return value;
		}
		
		public static boolean getEnable_Force_Daily_Sync( final Context context , final String userCode , final String companyCode ) {
			// Query DB
			Cursor cursor = getCursor ( context ,
					userCode , companyCode ,
					"Enable_Force_Daily_Sync" ,
					Transaction.DEFAULT_ENFORCE_MSL_CONFIRMATION );
			 // Move cursor to the first raw
			cursor.moveToFirst ();
			// Determine whether to apply or not the promotions are applied in the sales order taking.
			boolean value = cursor.getString ( 0 ).equalsIgnoreCase ( "Y" );
			// Close cursor
			cursor.close ();
			// Return the value
			return value;
		}
}