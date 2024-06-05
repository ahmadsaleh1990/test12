/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Call;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Utility class that provides helper methods for related actions during a client call.
 * 
 * @author Elias
 *
 */
public class CallAction {

	/**
	 * Flag used to indicate if the debug output is to be displayed or not.
	 */
    static boolean showDebugOutput = false;
	
    /**
     * Tag used to identify the source of a log message.
     */
    public static final String TAG = "CallAction";
    
    /**
     * String holding the default value used to determine if a visit and a call should be differentiated.
     */
    public static final String VISIT_CALL_DIFFERENCE = "N";
    
    /**
     * String holding the default value used to determine if no reason is prompted during a visit if a blank visit is performed.
     */
    public static final String NO_REASON_IF_BLANK_VISIT = "Y";
    
    /**
     * String holding the default value used to determine if a reason is prompted during a visit if no objective is performed.
     */
    public static final String PROMPT_OBJECTIVE_REASON = "N";
    
    /**
     * String holding the default value used to determine if a reason is prompted during a visit if no survey is performed.
     */
    public static final String PROMPT_SURVEY_REASON = "N";
    
    /**
     * String holding the default value used to determine if a reason is prompted during a visit if no sales is performed.
     */
    public static final String PROMPT_SALES_REASON = "N";
    
	/**
	 * Helper Class used to manage the call actions IDs for the call menu.<br>
	 * The main purpose of this class is to maintain a unique identifier for call actions within the call menu.<br>
	 * <b>Note : </b> The ID 0 is reserved !
	 * 
	 * @author Elias
	 *
	 */
	public static class ID {
		public static final int VISIT_NOTE = 1;
		public static final int BLANK_VISIT = 2;
		public static final int SALES_ORDER = 3;
		public static final int OBJECTIVE = 4;
		public static final int SURVEY = 5;
		public static final int SALES_INVOICE = 6;
		public static final int COLLECTION = 7;
		public static final int CLIENT_STOCK = 8;
		public static final int SALES_RFR = 9;
		public static final int SHARE_OF_SHELF = 10;
		public static final int SALES_RETURN = 11;
		public static final int DirectReturn = 12;
		public static final int OUTLET_STOCK_COUNT=13;
		public static final int NEWCOLLECTION=14;
		public static final int SHARE_OF_SHELF1 = 11;
	}
	
	/**
	 * Helper Class used to manage the call actions IDs that represent an non empty visit.<br>
	 * The main purpose of this class is to maintain the various IDs of all call actions that represent an non empty visit.<br>
	 * <b>Note : </b>The {@link CallAction.ID} class is used to retrieve the values ! No new IDs are generated !
	 * 
	 * @author Elias
	 *
	 */
	public static class NotEmptyID {
		public static final int BLANK_VISIT = ID.BLANK_VISIT;
		public static final int SALES_ORDER = ID.SALES_ORDER;
		public static final int SALES_RFR = ID.SALES_RFR;
		public static final int OBJECTIVE = ID.OBJECTIVE;
		public static final int SURVEY = ID.SURVEY;
		public static final int SALES_INVOICE = ID.SALES_INVOICE;
		public static final int COLLECTION = ID.COLLECTION;
		public static final int CLIENT_STOCK = ID.CLIENT_STOCK;
		public static final int SHARE_OF_SHELF = ID.SHARE_OF_SHELF;
		public static final int SALES_RETURN = ID.SALES_RETURN;
	}
	
	/**
	 * Helper Class used to manage the call actions IDs that does not represent a blank visit.<br>
	 * The main purpose of this class is to maintain the various IDs of all call actions that does not represent a blank visit.<br>
	 * <b>Note : </b>The {@link CallAction.ID} class is used to retrieve the values ! No new IDs are generated !
	 * 
	 * @author Elias
	 *
	 */
	public static class NotBlankVisitID {
		public static final int SALES_ORDER = ID.SALES_ORDER;
		public static final int SALES_RFR = ID.SALES_RFR;
		public static final int SURVEY = ID.SURVEY;
		public static final int SALES_INVOICE = ID.SALES_INVOICE;
		public static final int COLLECTION = ID.COLLECTION;
		public static final int CLIENT_STOCK = ID.CLIENT_STOCK;
		public static final int SHARE_OF_SHELF = ID.SHARE_OF_SHELF;
		public static final int SALES_RETURN = ID.SALES_RETURN;
	}
	
	/**
	 * Helper Class used to manage the call actions IDs that represent a sales action.<br>
	 * The main purpose of this class is to maintain the various IDs of all sales call actions.<br>
	 * <b>Note : </b>The {@link CallAction.ID} class is used to retrieve the values ! No new IDs are generated !
	 * 
	 * @author Elias
	 *
	 */
	public static class SalesID {
		public static final int SALES_ORDER = ID.SALES_ORDER;
		public static final int SALES_RFR = ID.SALES_RFR;
		public static final int SALES_INVOICE = ID.SALES_INVOICE;
		public static final int COLLECTION = ID.COLLECTION;
		public static final int SALES_RETURN = ID.SALES_RETURN;
	}
	
	/**
	 * Helper Class used to manage the call actions IDs that represent valid actions for client user collection.<br>
	 * The main purpose of this class is to maintain the various IDs of all client user collection actions.<br>
	 * <b>Note : </b>The {@link CallAction.ID} class is used to retrieve the values ! No new IDs are generated !
	 * 
	 * @author Elias
	 *
	 */
	public static class ClientUserCollectionID {
		public static final int COLLECTION = ID.COLLECTION;
	}
	
	/**
	 * Shared preference file name.
	 */
	private static final String FILENAME = CallAction.class.getName () + ".CALL_ACTION";
	
	/**
	 * visit ID key used in the shared preference call action file.
	 */
	private static final String VISIT_ID = CallAction.class.getName () + ".VISIT_ID";
	
	/**
	 * Gets all the IDs of the provided class ID in a list.
	 * 
	 * @param classID	{@link java.lang.Class} ID reference.
	 * @return	List of integers hosting the IDs of the provided class ID.
	 */
	public static ArrayList < Integer > getIDs ( final Class < ? > classID ) {
		// Declare and initialize a list of integers used to host the IDs
		ArrayList < Integer > IDs = new ArrayList < Integer > ();
		// Check if the provided class is one of the valid ID classes above
		if ( classID != ID.class && classID != NotEmptyID.class && classID != NotBlankVisitID.class && classID != SalesID.class && classID != ClientUserCollectionID.class )
			// Invalid class
			return IDs;
		// Otherwise the class is valid
		// Retrieve all the fields of the provided class
		Field fields [] = classID.getFields ();
		// Iterate over all the fields
		for ( Field field : fields )
			try {
				// Retrieve and add the ID to the list
				IDs.add ( field.getInt ( null ) );
			} catch ( Exception exception ) {
				// Do nothing
			} // End of try-catch block
		// Return the IDs
		return IDs;
	}
	
	/**
	 * Clears all the data in the Call Action shared preferences file.
	 * 
	 * @param context	The application context.
	 */
	public static void clearData ( final Context context ) {
		// Open the shared preference language file
		SharedPreferences sharedPref = context.getSharedPreferences ( FILENAME , Context.MODE_PRIVATE );
		// Retrieve a new Editor for these preferences to modify them
		SharedPreferences.Editor editor = sharedPref.edit();
		// Mark in the editor to remove all values from the preferences
		editor.clear ();
		// Commit the preferences changes
		editor.commit();
    	// Display debug output if required
        if ( showDebugOutput )
        	Log.d ( TAG , TAG + " : clearData: data cleared in file [" + FILENAME + "]" );
	}
	
	/**
	 * Sets the specified call action status for the provided visit ID.
	 * 
	 * @param context	The application context.
	 * @param visitID	Long holding the visit ID.
	 */
	public static void setCall ( final Context context , final long visitID ) {
		// Open the shared preference language file
		SharedPreferences sharedPref = context.getSharedPreferences ( FILENAME , Context.MODE_PRIVATE );
		// Retrieve the stored visit ID (if none found, use 0)
		long storedVisitID = sharedPref.getLong ( VISIT_ID , 0 );
		// Check if the visit IDs differ
		if ( visitID == storedVisitID ) {
			// Similar IDs, do nothing
	    	// Display debug output if required
	        if ( showDebugOutput )
	        	Log.d ( TAG , TAG + " : setCall: similar visit IDs detected [" + visitID + "], no need to set it again." );
		} // End if
		else {
	        // Clear any call action data
	        clearData ( context );
	    	// Display debug output if required
	        if ( showDebugOutput )
	        	Log.d ( TAG , TAG + " : setCall: different visit IDs detected [" + visitID + "] VS [" + storedVisitID + "]. All previous data cleared." );

			// Retrieve a new Editor for these preferences to modify them
			SharedPreferences.Editor editor = sharedPref.edit();
			// Set the visit ID in the preferences editor
			editor.putLong ( VISIT_ID , visitID );
			// Commit the preferences changes
			editor.commit();
	    	// Display debug output if required
	        if ( showDebugOutput )
	        	Log.d ( TAG , TAG + " : setCall: visit [" + visitID + "] set in file [" + FILENAME + "]" );
		} // End else
	}
	
	/**
	 * Sets the specified call action status for the provided visit ID.
	 * 
	 * @param context	The application context.
	 * @param visitID	Long holding the visit ID.
	 * @param callActionID	Integer holding the specified call action ID.
	 * @param callActionStatus	Boolean indicating if the specified call action is performed.
	 */
	public static void setCallActionStatus ( final Context context , final long visitID , final int callActionID , final boolean callActionStatus ) {
		// Open the shared preference language file
		SharedPreferences sharedPref = context.getSharedPreferences ( FILENAME , Context.MODE_PRIVATE );
		// Retrieve a new Editor for these preferences to modify them
		SharedPreferences.Editor editor = sharedPref.edit();
		// Set the visit ID in the preferences editor
		editor.putLong ( VISIT_ID , visitID );
		// Set the call action status
		editor.putBoolean ( String.valueOf ( callActionID ) , callActionStatus );
		// Commit the preferences changes
		editor.commit();
    	// Display debug output if required
        if ( showDebugOutput )
        	Log.d ( TAG , TAG + " : setCallActionStatus: status [" + callActionStatus + "] for ID [" + callActionID + "] in visit [" + visitID + "] set in file [" + FILENAME + "]" );
	}
	
	/**
	 * Determines if the specified call action is performed during the provided visit ID.
	 * 
	 * @param context	The application context.
	 * @param visitID	Long holding the visit ID.
	 * @param callActionID	Integer holding the specified call action ID.
	 * @return	Boolean indicating if the specified call action is performed.
	 */
	public static boolean getCallActionStatus ( final Context context , final long visitID , final int callActionID ) {
		// Open the shared preference language file
		SharedPreferences sharedPref = context.getSharedPreferences ( FILENAME , Context.MODE_PRIVATE );
		// Retrieve the stored visit ID (if none found, use 0)
		long storedVisitID = sharedPref.getLong ( VISIT_ID , 0 );
		// Compare the visit IDs
		if ( storedVisitID != visitID )
			// Different visit IDs, indicate that the specified call action is NOT performed
			return false;
		// Otherwise the visit IDs match
		// Return the flag content
		return sharedPref.getBoolean ( String.valueOf ( callActionID ) , false );
	}
	
	/**
	 * Retrieves the last stored visit ID from the call action shared preferences.
	 * 
	 * @param context	he application context.
	 * @return	Long holding the last stored visit ID, or {@code 0} if invalid.
	 */
	public static long getVisitID ( final Context context ) {
		// Open the shared preference language file
		SharedPreferences sharedPref = context.getSharedPreferences ( FILENAME , Context.MODE_PRIVATE );
		// Retrieve and return the stored visit ID (if none found, use 0)
		return sharedPref.getLong ( VISIT_ID , 0 );
	}
	
	/**
	 * Determines if the provided visit ID is empty, meaning that none of the defined call actions were performed.
	 * 
	 * @param context	The application context.
	 * @param visitID	Long holding the visit ID.
	 * @return	Boolean indicating if the specified visit is empty.
	 */
	public static boolean isEmpty ( final Context context , final long visitID ) {
		// Open the shared preference language file
		SharedPreferences sharedPref = context.getSharedPreferences ( FILENAME , Context.MODE_PRIVATE );
		// Retrieve the stored visit ID (if none found, use 0)
		long storedVisitID = sharedPref.getLong ( VISIT_ID , 0 );
		// Compare the visit IDs
		if ( storedVisitID != visitID )
			// Different visit IDs, indicate that the visit is empty
			return true;
		// Otherwise the visit IDs match
		// Retrieve all the fields of the NotEmptyID class (i.e. all the NotEmptyID IDs)
		Field fields [] = NotEmptyID.class.getFields ();
		// Iterate over all the fields
		for ( Field field : fields )
			try {
				// Determine if the current call action is performed
				if ( sharedPref.getBoolean ( String.valueOf ( field.getInt ( null ) ) , false ) )
					// Indicate that the visit is not empty
					return false;
			} catch ( Exception exception ) {
				// Do nothing
			} // End of try-catch block
		// Otherwise the visit is empty
		return true;
	}
	
	/**
	 * Determines if the provided visit ID is not blank visit, meaning that at least one of the call action determined by the {@link CallAction.NotBlankVisitID} class is performed.<br>
	 * <b>Note : </b>The {@link CallAction.ID#BLANK_VISIT} ID value is not evaluated !
	 * 
	 * @param context	The application context.
	 * @param visitID	Long holding the visit ID.
	 * @return	Boolean indicating if the specified visit is not a blank visit.<br>
	 * {@code TRUE} indicates that at least one call action defined by {@link CallAction.NotBlankVisitID} is performed.<br>
	 * {@code FALSE} means that none of the call action defined by {@link CallAction.NotBlankVisitID} were detected. It does not means that the visit is blank 
	 * (since the complement of <b>NOT BLANK</b> is <b>NOT NOT BLANK</b> or simply <b>BLANK</b>.<br>
	 * To determine if the visit is blank, please use {@link #getCallActionStatus(Context, long, int)} along with {@link CallAction.ID#BLANK_VISIT}.<br>
	 * However if both methods return {@code TRUE} (meaning the visit is blank and is not blank) than something is <b>PLAIN WRONG</b> !
	 */
	public static boolean isNotBlank ( final Context context , final long visitID ) {
		// Open the shared preference language file
		SharedPreferences sharedPref = context.getSharedPreferences ( FILENAME , Context.MODE_PRIVATE );
		// Retrieve the stored visit ID (if none found, use 0)
		long storedVisitID = sharedPref.getLong ( VISIT_ID , 0 );
		// Compare the visit IDs
		if ( storedVisitID != visitID )
			// Different visit IDs, indicate that the visit is NOT NOT BLANK
			return false;
		// Otherwise the visit IDs match
		// Retrieve all the fields of the NotBlankVisitID class
		Field fields [] = NotBlankVisitID.class.getFields ();
		// Iterate over all the fields
		for ( Field field : fields )
			try {
				// Determine if the current call action is performed
				if ( sharedPref.getBoolean ( String.valueOf ( field.getInt ( null ) ) , false ) )
					// Indicate that the visit is NOT BLANK
					return true;
			} catch ( Exception exception ) {
				// Do nothing
			} // End of try-catch block
		// Otherwise the visit is NOT NOT BLANK
		return false;
	}
	
	/**
	 * Determines if the provided visit ID is sales, meaning that at least one sales call action is performed.
	 * 
	 * @param context	The application context.
	 * @param visitID	Long holding the visit ID.
	 * @return	Boolean indicating if the specified visit is sales.
	 */
	public static boolean isSales ( final Context context , final long visitID ) {
		// Open the shared preference language file
		SharedPreferences sharedPref = context.getSharedPreferences ( FILENAME , Context.MODE_PRIVATE );
		// Retrieve the stored visit ID (if none found, use 0)
		long storedVisitID = sharedPref.getLong ( VISIT_ID , 0 );
		// Compare the visit IDs
		if ( storedVisitID != visitID )
			// Different visit IDs, indicate that the visit is NOT sales
			return false;
		// Otherwise the visit IDs match
		// Retrieve all the fields of the SalesID class (i.e. all the sales IDs such as Sales Order, ...)
		Field fields [] = SalesID.class.getFields ();
		// Iterate over all the fields
		for ( Field field : fields )
			try {
				// Determine if the current call action is performed
				if ( sharedPref.getBoolean ( String.valueOf ( field.getInt ( null ) ) , false ) )
					// Indicate that the visit is sales
					return true;
			} catch ( Exception exception ) {
				// Do nothing
			} // End of try-catch block
		// Otherwise the visit is NOT sales
		return false;
	}
	
}