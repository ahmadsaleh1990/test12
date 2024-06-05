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

import android.database.Cursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Utilities class for database Cursors, holding helper methods for additional functionality.
 * 
 * @author Elias
 *
 */
public class CursorUtils {

	/**
	 * Constant double holding the default group size used when querying by group.
	 */
	public static final double DEFAULT_GROUP_SIZE = 500;

//	/**
//	 * Retrieves an array of cursors merged together to form one cursor by executing a query for every group of conditions.<br>
//	 * This resort is used due to SQLite limitations : Cannot execute query using a list with more than 1000 elements in the IN statement.
//	 * 
//	 * @param db	SQLiteDatabase object exposing methods to manage a SQLite database.
//	 * @param SQL_left	The left part of the String holding the SQL query statement.
//	 * @param SQL_right	The right part of the String holding the SQL query statement.
//	 * @param selectionArguments_Left	The left array of strings holding the SQL query arguments.
//	 * @param selectionArguments_Right	The right array of strings holding the SQL query arguments.
//	 * @param conditions	List of conditions (of primitive data types) used in the query.
//	 * @return	A single linear cursor holding the resulted data set(s).
//	 */
//	public static Cursor rawQueryByGroup ( final SQLiteDatabase db , final String SQL_left , final String SQL_right , final String [] selectionArguments_Left , final String [] selectionArguments_Right , final List < ? > conditions ) {
//		try {
//			// Compute the arguments size
//			int sizeArgumentsLeft = selectionArguments_Left != null ? selectionArguments_Left.length : 0;
//			int sizeArgumentsRight = selectionArguments_Right != null ? selectionArguments_Right.length : 0;
//			// Define the group size
//			final double groupSize = DEFAULT_GROUP_SIZE - sizeArgumentsLeft - sizeArgumentsRight;
//			// Determine if the computed group size is valid (selection arguments should not exceed the group size limit)
//			if ( groupSize <= 0 )
//				// Cannot execute method
//				return null;
//			
//			// Declare and initialize a list of objects to host groups of conditions
//			List < Object > group = new ArrayList < Object > ();
//			// Determine how many cursors (one cursor for every group) are needed
//			int iterations = (int) Math.ceil ( conditions.size () / groupSize );
//			// Declare and initialize an array of cursors
//			Cursor cursors [] = new Cursor [ iterations ];
//			
//			// Perform the specified number of iterations, and execute the query in each iterations for a group
//			for ( int i = 0 ; i < iterations ; i ++ ) {
//				// Clear any previous group
//				group.clear ();
//				// Compute the condition group limit
//				int condition = (int) ( ( ( i + 1 ) * groupSize ) > conditions.size () ? conditions.size () : ( i + 1 ) * groupSize ) ;
//				// Retrieve the new group list
//				for ( int j = (int) ( i * groupSize ) ; j < condition ; j ++ )
//					group.add ( conditions.get ( j ) );
//				// Declare and initialize an array to hold the current selection arguments
//				String group [] = new String [ selectionArguments_Left.length + selectionArguments_Right.length + condition - offset ];
//				// Add the initial arguments to the group
//				int sizeLeft = selectionArguments_Left.length;
//				for ( int j = 0 ; j < sizeLeft ; j ++ )
//					group [ j ] = selectionArguments_Left [ j ];
//				
//				// Retrieve the new group array
//				
//			} // End for loop
//				
//			
//		} catch ( Exception exception ) {
//			// An error occurred
//			return null;
//		} // End of try-catch block
//	}
	
	/**
	 * Retrieves an array of cursors merged together to form one cursor by executing a raw query by limits.<br>
	 * This resort is used to avoid flickering and lags during list view scrolling, due to heavy cursors and big data sets which make the cursor's window exceed its size limit.
	 * 
	 * @param db	SQLiteDatabase object exposing methods to manage a SQLite database.
	 * @param SQL	String holding the SQL query statement.
	 * @param selectionArguments	Array of strings holding the SQL query arguments.
	 * @return	A single linear cursor holding the resulted data set(s).
	 */
	public static Cursor rawQueryByLimit ( final SQLiteDatabase db , final String SQL , final String [] selectionArguments ) {
		try {
			// Define the group size
			final double groupSize = DEFAULT_GROUP_SIZE;
			// Execute query without grouping (to compute the result size)
			Cursor cursor = db.rawQuery ( SQL , selectionArguments );
			// Retrieve the cursor size
			int size = cursor.getCount ();
			// Check if the cursor size exceeds the group limit
			if ( size > groupSize ) {
				// Close the cursor
				cursor.close ();
				cursor = null;
				// Determine how many cursors (one cursor for every group) are needed
				int number = (int) Math.ceil ( size / groupSize );
				// Declare and initialize an array of cursors
				Cursor cursors [] = new Cursor [ number ];
				// Iterate over the array of cursors
				for ( int i = 0 ; i < number ; i ++ ) {
					// Initialize the current cursor with the appropriate limit / offset
					cursors [ i ] = db.rawQuery ( SQL + " LIMIT " + ( i * ( (int) groupSize ) ) + "," + ( (int) groupSize ) , selectionArguments );
					// Perform internal computations
					cursors [ i ].getCount ();
				} // End for loop
				
				// Merge the array of cursor as a single linear Cursor
				Cursor mergeCursor = new MergeCursor ( cursors );
				// Perform internal computations
				mergeCursor.getCount ();
				// Return the single cursor
				return mergeCursor;
			} // End if

			// Otherwise the cursor size does not exceed the limit
			// Simply return the cursor
			return cursor;
		} catch ( Exception exception ) {
			// An error occurred
			return null;
		} // End of try-catch block
	}
	
}