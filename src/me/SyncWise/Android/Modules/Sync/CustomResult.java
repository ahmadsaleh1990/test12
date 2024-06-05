/**
 * Copyright 2014 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Sync;

import java.util.ArrayList;
import java.util.List;

import me.SyncWise.Android.Database.DaoSession;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.TransactionSequences;
import me.SyncWise.Android.Database.TransactionSequencesDao;
import me.SyncWise.Android.Database.VisitReasons;
import me.SyncWise.Android.Database.VisitReasonsDao;
import me.SyncWise.Android.Database.VisitTypes;
import me.SyncWise.Android.Database.VisitTypesDao;
import me.SyncWise.Android.Database.VisitsDao;
import android.content.Context;
import android.database.Cursor;
import de.greenrobot.dao.AbstractDao;

/**
 * Class related to data synchronization, holding all helper methods related to custom result execution.
 * 
 * @author Elias
 *
 */
public class CustomResult {

	/**
	 * Gets a custom result for the provided abstract dao table, based on a custom query.<br>
	 * If no custom queries are defined for the provided table, {@code NULL} is returned.
	 * 
	 * @param context	The application context.
	 * @param dao	Reference to the AbstractDao, which in turns references the main table.
	 * @param daoSession	Reference to the current DaoSession which gives access to all DAOs, offers convenient persistence methods, and also serves as a session cache.
	 * @return	List of GreenDao entities containing the result of the custom query, or {@code NULL} if none is defined.
	 */
	public static List < ? > getCustomResult ( final Context context , final AbstractDao < ? , ? > dao , final DaoSession daoSession ) {
		// Determine if the abstract dao table has a custom query defined
		if ( dao instanceof TransactionSequencesDao )
			// Compute and return the transaction sequences custom query result
			return getTransactionSequencesQueryResult ( context , daoSession );
		// Determine if the abstract dao table has a custom query defined
		else if ( dao instanceof VisitReasonsDao )
			// Compute and return the visit reasons custom query result
			return getVisitReasonsQueryResult ( context , daoSession );
		// Determine if the abstract dao table has a custom query defined
		else if ( dao instanceof VisitTypesDao )
			// Compute and return the visit types custom query result
			return getVisitTypesQueryResult ( context , daoSession );
		// No custom result defined
		return null;
	}
	
	/**
	 * Gets a custom query result for the {@link me.SyncWise.Android.Database.TransactionSequences TransactionSequences}.
	 * 
	 * @param context	The application context.
	 * @param daoSession	Reference to the current DaoSession which gives access to all DAOs, offers convenient persistence methods, and also serves as a session cache.
	 * @return	List of {@link me.SyncWise.Android.Database.TransactionSequences TransactionSequences} containing the result of the custom query, or {@code NULL} if the dao session is invalid.
	 */
	private static List < TransactionSequences > getTransactionSequencesQueryResult ( final Context context , final DaoSession daoSession ) {
		// Check if the dao session is valid
		if ( daoSession == null )
			// Invalid dao session
			return null;
		// Retrieve the current user code
		String userCode = DatabaseUtils.getCurrentUserCode ( context );
		// Retrieve the current company code
		String companyCode = DatabaseUtils.getCurrentCompanyCode ( context );
		// Query for the transaction sequences of the current user
		Cursor cursor = daoSession.getDatabase ()
				.query ( TransactionSequencesDao.TABLENAME , // table
						daoSession.getTransactionSequencesDao ().getAllColumns () , // columns
						TransactionSequencesDao.Properties.UserCode.columnName + " = ? AND " +
							TransactionSequencesDao.Properties.CompanyCode.columnName + " = ?", // selection
						new String [] { userCode , companyCode } , // selectionArgs
						null , // groupBy
						null , // having
						null ); // orderBy
		// Declare and initialize a list used to host the result
		List < TransactionSequences > transactionSequences = new ArrayList < TransactionSequences > ();
		// Move the cursor to the first row
		if ( cursor.moveToFirst () ) 
			// Load the transaction sequences values and add them to the main list
			transactionSequences.add ( daoSession.getTransactionSequencesDao ().readEntity ( cursor , 0 ) );
		// Close the cursor
		cursor.close ();
		cursor = null;
		// Return the transaction sequences list
		return transactionSequences;
	}

	/**
	 * Gets a custom query result for the {@link me.SyncWise.Android.Database.VisitReasons VisitReasons}.
	 * 
	 * @param context	The application context.
	 * @param daoSession	Reference to the current DaoSession which gives access to all DAOs, offers convenient persistence methods, and also serves as a session cache.
	 * @return	List of {@link me.SyncWise.Android.Database.VisitReasons VisitReasons} containing the result of the custom query, or {@code NULL} if the dao session is invalid.
	 */
	private static List < VisitReasons > getVisitReasonsQueryResult ( final Context context , final DaoSession daoSession ) {
		// Check if the dao session is valid
		if ( daoSession == null )
			// Invalid dao session
			return null;
		// Retrieve and return all the visit reasons of the not processed visits
		return daoSession.getVisitReasonsDao ().queryRaw ( 
				"WHERE " + VisitReasonsDao.Properties.VisitID.columnName + " IN ( SELECT " + VisitsDao.Properties.VisitID.columnName + " FROM " + VisitsDao.TABLENAME +
					" WHERE " + VisitsDao.Properties.IsProcessed.columnName + "=? )" ,
				new String [] { String.valueOf ( IsProcessedUtils.isNotSync () ) } );
	}
	
	/**
	 * Gets a custom query result for the {@link me.SyncWise.Android.Database.VisitTypes VisitTypes}.
	 * 
	 * @param context	The application context.
	 * @param daoSession	Reference to the current DaoSession which gives access to all DAOs, offers convenient persistence methods, and also serves as a session cache.
	 * @return	List of {@link me.SyncWise.Android.Database.VisitTypes VisitTypes} containing the result of the custom query, or {@code NULL} if the dao session is invalid.
	 */
	private static List < VisitTypes > getVisitTypesQueryResult ( final Context context , final DaoSession daoSession ) {
		// Check if the dao session is valid
		if ( daoSession == null )
			// Invalid dao session
			return null;
		// Retrieve and return all the visit types of the not processed visits
		return daoSession.getVisitTypesDao ().queryRaw ( 
				"WHERE " + VisitTypesDao.Properties.VisitID.columnName + " IN ( SELECT " + VisitsDao.Properties.VisitID.columnName + " FROM " + VisitsDao.TABLENAME +
					" WHERE " + VisitsDao.Properties.IsProcessed.columnName + "=? )" ,
				new String [] { String.valueOf ( IsProcessedUtils.isNotSync () ) } );
	}
	
}