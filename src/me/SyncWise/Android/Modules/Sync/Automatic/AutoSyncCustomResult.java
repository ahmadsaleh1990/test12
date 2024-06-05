/**
 * Copyright 2014 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Sync.Automatic;

import java.util.List;

import me.SyncWise.Android.Database.BlankVisitContacts;
import me.SyncWise.Android.Database.BlankVisitContactsDao;
import me.SyncWise.Android.Database.BlankVisits;
import me.SyncWise.Android.Database.BlankVisitsDao;
import me.SyncWise.Android.Database.ClientDocumentImages;
import me.SyncWise.Android.Database.ClientDocumentImagesDao;
import me.SyncWise.Android.Database.ClientStockCountHeaders;
import me.SyncWise.Android.Database.ClientStockCountHeadersDao;
import me.SyncWise.Android.Database.CollectionHeaders;
import me.SyncWise.Android.Database.CollectionHeadersDao;
import me.SyncWise.Android.Database.DaoSession;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.TransactionHeaders;
import me.SyncWise.Android.Database.TransactionHeadersDao;
import me.SyncWise.Android.Database.TransactionSequencesDao;
import me.SyncWise.Android.Database.VisitReasons;
import me.SyncWise.Android.Database.VisitReasonsDao;
import me.SyncWise.Android.Database.VisitTypes;
import me.SyncWise.Android.Database.VisitTypesDao;
import me.SyncWise.Android.Database.Visits;
import me.SyncWise.Android.Database.VisitsDao;
import me.SyncWise.Android.Modules.Call.CallAction;
import me.SyncWise.Android.Modules.Sync.CustomResult;
import android.content.Context;
import de.greenrobot.dao.AbstractDao;

/**
 * Class related to data synchronization, holding all helper methods related to custom result execution specific for the <b>AUTO SYNC MODULE</b>.
 * 
 * @author Elias
 *
 */
public class AutoSyncCustomResult {
	
	/**
	 * Gets a custom result specific for the <b>AUTO SYNC MODULE</b>, for the provided abstract dao table, based on a custom query.<br>
	 * If no custom queries are defined for the provided table, {@code NULL} is returned.
	 * 
	 * @param context	The application context.
	 * @param dao	Reference to the AbstractDao, which in turns references the main table.
	 * @param daoSession	Reference to the current DaoSession which gives access to all DAOs, offers convenient persistence methods, and also serves as a session cache.
	 * @return	List of GreenDao entities containing the result of the custom query, or {@code NULL} if none is defined.
	 */
	public static List < ? > getCustomResultAutoSync ( final Context context , final AbstractDao < ? , ? > dao , final DaoSession daoSession ) {
		// Determine if the abstract dao table has a custom result defined
		if ( dao instanceof TransactionSequencesDao )
			// Compute and return the transaction sequences custom query result
			return CustomResult.getCustomResult ( context , dao , daoSession );
		// Determine if the abstract dao table has a custom result defined
		else if ( dao instanceof VisitReasonsDao )
			// Compute and return the visit reasons custom query result
			return getVisitReasonsQueryResultAutoSync ( context , daoSession );
		// Determine if the abstract dao table has a custom result defined
		else if ( dao instanceof VisitTypesDao )
			// Compute and return the visit types custom query result
			return getVisitTypesQueryResultAutoSync ( context , daoSession );
		// Determine if the abstract dao table has a custom result defined
		else if ( dao instanceof ClientDocumentImagesDao )
			// Compute and return the visit types custom query result
			return getClientDocumentImagesQueryResultAutoSync ( context , daoSession );
		// Determine if the abstract dao table has a custom result defined
		else if ( dao instanceof VisitsDao )
			// Compute and return the visits custom query result
			return getVisitsQueryResultAutoSync ( context , daoSession );
		// Determine if the abstract dao table has a custom result defined
		else if ( dao instanceof BlankVisitsDao )
			// Compute and return the blank visits custom query result
			return getBlankVisitsQueryResultAutoSync ( context , daoSession );
		// Determine if the abstract dao table has a custom result defined
		else if ( dao instanceof BlankVisitContactsDao )
			// Compute and return the blank visit contacts custom query result
			return getBlankVisitContactsQueryResultAutoSync ( context , daoSession );
		// Determine if the abstract dao table has a custom result defined
		else if ( dao instanceof TransactionHeadersDao )
			// Compute and return the transaction headers custom query result
			return getTransactionHeadersQueryResultAutoSync ( context , daoSession );
		// Determine if the abstract dao table has a custom result defined
		else if ( dao instanceof CollectionHeadersDao )
			// Compute and return the collection headers custom query result
			return getCollectionHeadersQueryResultAutoSync ( context , daoSession );
		// Determine if the abstract dao table has a custom result defined
		else if ( dao instanceof ClientStockCountHeadersDao )
			// Compute and return the client stock count headers custom query result
			return getClientStockCountHeadersQueryResultAutoSync ( context , daoSession );
		else
			// No custom result defined
			return null;
	}
	
	/**
	 * Gets a custom query result for the {@link me.SyncWise.Android.Database.VisitReasons VisitReasons} specific for the <b>AUTO SYNC MODULE</b>.
	 * 
	 * @param context	The application context.
	 * @param daoSession	Reference to the current DaoSession which gives access to all DAOs, offers convenient persistence methods, and also serves as a session cache.
	 * @return	List of {@link me.SyncWise.Android.Database.VisitReasons VisitReasons} containing the result of the custom query, or {@code NULL} if the dao session is invalid.
	 */
	private static List < VisitReasons > getVisitReasonsQueryResultAutoSync ( final Context context , final DaoSession daoSession ) {
		// Check if the dao session is valid
		if ( daoSession == null )
			// Invalid dao session
			return null;
		// Retrieve and return all the visit reasons of the not processed visits
		return daoSession.getVisitReasonsDao ().queryRaw ( 
				"WHERE " + VisitReasonsDao.Properties.VisitID.columnName + " IN ( SELECT " + VisitsDao.Properties.VisitID.columnName + " FROM " + VisitsDao.TABLENAME +
					" WHERE " + VisitsDao.Properties.IsProcessed.columnName + "=? ) AND " + VisitsDao.Properties.VisitID.columnName + "!=?" ,
				new String [] { String.valueOf ( IsProcessedUtils.isNotSync () ) , String.valueOf ( CallAction.getVisitID ( context ) ) } );
	}
	
	/**
	 * Gets a custom query result for the {@link me.SyncWise.Android.Database.VisitTypes VisitTypes} specific for the <b>AUTO SYNC MODULE</b>.
	 * 
	 * @param context	The application context.
	 * @param daoSession	Reference to the current DaoSession which gives access to all DAOs, offers convenient persistence methods, and also serves as a session cache.
	 * @return	List of {@link me.SyncWise.Android.Database.VisitTypes VisitTypes} containing the result of the custom query, or {@code NULL} if the dao session is invalid.
	 */
	private static List < VisitTypes > getVisitTypesQueryResultAutoSync ( final Context context , final DaoSession daoSession ) {
		// Check if the dao session is valid
		if ( daoSession == null )
			// Invalid dao session
			return null;
		// Retrieve and return all the visit types of the not processed visits
		return daoSession.getVisitTypesDao ().queryRaw ( 
				"WHERE " + VisitTypesDao.Properties.VisitID.columnName + " IN ( SELECT " + VisitsDao.Properties.VisitID.columnName + " FROM " + VisitsDao.TABLENAME +
					" WHERE " + VisitsDao.Properties.IsProcessed.columnName + "=? ) AND " + VisitsDao.Properties.VisitID.columnName + "!=?" ,
				new String [] { String.valueOf ( IsProcessedUtils.isNotSync () ) , String.valueOf ( CallAction.getVisitID ( context ) ) } );
	}
	
	/**
	 * Gets a custom query result for the {@link me.SyncWise.Android.Database.ClientDocumentImages ClientDocumentImages} specific for the <b>AUTO SYNC MODULE</b>.
	 * 
	 * @param context	The application context.
	 * @param daoSession	Reference to the current DaoSession which gives access to all DAOs, offers convenient persistence methods, and also serves as a session cache.
	 * @return	List of {@link me.SyncWise.Android.Database.ClientDocumentImages ClientDocumentImages} containing the result of the custom query, or {@code NULL} if the dao session is invalid.
	 */
	private static List < ClientDocumentImages > getClientDocumentImagesQueryResultAutoSync ( final Context context , final DaoSession daoSession ) {
		// Check if the dao session is valid
		if ( daoSession == null )
			// Invalid dao session
			return null;
		// Retrieve and return all the visit types of the not processed visits
		return daoSession.getClientDocumentImagesDao ().queryRaw ( 
				" WHERE " + ClientDocumentImagesDao.Properties.IsProcessed.columnName + "=? AND " + ClientDocumentImagesDao.Properties.VisitID.columnName + "!=?" ,
				new String [] { String.valueOf ( IsProcessedUtils.isNotSync () ) , String.valueOf ( CallAction.getVisitID ( context ) ) } );
	}
	
	/**
	 * Gets a custom query result for the {@link me.SyncWise.Android.Database.Visits Visits} specific for the <b>AUTO SYNC MODULE</b>.
	 * 
	 * @param context	The application context.
	 * @param daoSession	Reference to the current DaoSession which gives access to all DAOs, offers convenient persistence methods, and also serves as a session cache.
	 * @return	List of {@link me.SyncWise.Android.Database.Visits Visits} containing the result of the custom query, or {@code NULL} if the dao session is invalid.
	 */
	private static List < Visits > getVisitsQueryResultAutoSync ( final Context context , final DaoSession daoSession ) {
		// Check if the dao session is valid
		if ( daoSession == null )
			// Invalid dao session
			return null;
		// Retrieve and return all the visits that have different values for start and end dates OR equal dates but are not on-going
		return daoSession.getVisitsDao ().queryRaw ( 
				"WHERE " + VisitsDao.Properties.IsProcessed.columnName + "==? AND " + VisitsDao.Properties.VisitID.columnName + "!=?" ,
				new String [] { String.valueOf ( IsProcessedUtils.isNotSync () ) , String.valueOf ( CallAction.getVisitID ( context ) ) } );
	}
	
	/**
	 * Gets a custom query result for the {@link me.SyncWise.Android.Database.BlankVisits BlankVisits} specific for the <b>AUTO SYNC MODULE</b>.
	 * 
	 * @param context	The application context.
	 * @param daoSession	Reference to the current DaoSession which gives access to all DAOs, offers convenient persistence methods, and also serves as a session cache.
	 * @return	List of {@link me.SyncWise.Android.Database.BlankVisits BlankVisits} containing the result of the custom query, or {@code NULL} if the dao session is invalid.
	 */
	private static List < BlankVisits > getBlankVisitsQueryResultAutoSync ( final Context context , final DaoSession daoSession ) {
		// Check if the dao session is valid
		if ( daoSession == null )
			// Invalid dao session
			return null;
		// Retrieve and return all the blank visits that have different values for start and end dates OR equal dates but are not on-going
		return daoSession.getBlankVisitsDao ().queryRaw ( 
				"WHERE " + BlankVisitsDao.Properties.IsProcessed.columnName + "=? AND " + BlankVisitsDao.Properties.VisitID.columnName + "!=?" ,
				new String [] { String.valueOf ( IsProcessedUtils.isNotSync () ) , String.valueOf ( CallAction.getVisitID ( context ) ) } );
	}
	
	/**
	 * Gets a custom query result for the {@link me.SyncWise.Android.Database.BlankVisitContacts BlankVisitContacts} specific for the <b>AUTO SYNC MODULE</b>.
	 * 
	 * @param context	The application context.
	 * @param daoSession	Reference to the current DaoSession which gives access to all DAOs, offers convenient persistence methods, and also serves as a session cache.
	 * @return	List of {@link me.SyncWise.Android.Database.BlankVisitContacts BlankVisitContacts} containing the result of the custom query, or {@code NULL} if the dao session is invalid.
	 */
	private static List < BlankVisitContacts > getBlankVisitContactsQueryResultAutoSync ( final Context context , final DaoSession daoSession ) {
		// Check if the dao session is valid
		if ( daoSession == null )
			// Invalid dao session
			return null;
		// Retrieve and return all the blank visits that have different values for start and end dates OR equal dates but are not on-going
		return daoSession.getBlankVisitContactsDao ().queryRaw ( 
				"WHERE " + BlankVisitContactsDao.Properties.IsProcessed.columnName + "=? AND " + BlankVisitContactsDao.Properties.VisitID.columnName + "!=?" ,
				new String [] { String.valueOf ( IsProcessedUtils.isNotSync () ) , String.valueOf ( CallAction.getVisitID ( context ) ) } );
	}
	
	/**
	 * Gets a custom query builder for the {@link me.SyncWise.Android.Database.TransactionHeaders TransactionHeaders} specific for the <b>AUTO SYNC MODULE</b>.
	 * 
	 * @param context	The application context.
	 * @param daoSession	Reference to the current DaoSession which gives access to all DAOs, offers convenient persistence methods, and also serves as a session cache.
	 * @return	List of {@link me.SyncWise.Android.Database.TransactionHeaders TransactionHeaders} containing the result of the custom query, or {@code NULL} if the dao session is invalid.
	 */
	private static List < TransactionHeaders > getTransactionHeadersQueryResultAutoSync ( final Context context , final DaoSession daoSession ) {
		// Check if the dao session is valid
		if ( daoSession == null )
			// Invalid dao session
			return null;
		// Retrieve and return all the transaction headers that are not processed OR their visit ID is in still on-going
		return daoSession.getTransactionHeadersDao ().queryRaw ( 
				"WHERE " + TransactionHeadersDao.Properties.IsProcessed.columnName + "=? AND " + TransactionHeadersDao.Properties.VisitID.columnName + "!=?" ,
				new String [] { String.valueOf ( IsProcessedUtils.isNotSync () ) , String.valueOf ( CallAction.getVisitID ( context ) ) } );
	}
	
	/**
	 * Gets a custom query builder for the {@link me.SyncWise.Android.Database.CollectionHeaders CollectionHeaders} specific for the <b>AUTO SYNC MODULE</b>.
	 * 
	 * @param context	The application context.
	 * @param daoSession	Reference to the current DaoSession which gives access to all DAOs, offers convenient persistence methods, and also serves as a session cache.
	 * @return	List of {@link me.SyncWise.Android.Database.CollectionHeaders CollectionHeaders} containing the result of the custom query, or {@code NULL} if the dao session is invalid.
	 */
	private static List < CollectionHeaders > getCollectionHeadersQueryResultAutoSync ( final Context context , final DaoSession daoSession ) {
		// Check if the dao session is valid
		if ( daoSession == null )
			// Invalid dao session
			return null;
		// Retrieve and return all the collection headers that are not processed OR their visit ID is in still on-going
		return daoSession.getCollectionHeadersDao ().queryRaw ( 
				"WHERE " + CollectionHeadersDao.Properties.IsProcessed.columnName + "=? AND " + CollectionHeadersDao.Properties.VisitID.columnName + "!=?" ,
				new String [] { String.valueOf ( IsProcessedUtils.isNotSync () ) , String.valueOf ( CallAction.getVisitID ( context ) ) } );
	}
	
	/**
	 * Gets a custom query builder for the {@link me.SyncWise.Android.Database.ClientStockCountHeaders ClientStockCountHeaders} specific for the <b>AUTO SYNC MODULE</b>.
	 * 
	 * @param context	The application context.
	 * @param daoSession	Reference to the current DaoSession which gives access to all DAOs, offers convenient persistence methods, and also serves as a session cache.
	 * @return	List of {@link me.SyncWise.Android.Database.ClientStockCountHeaders ClientStockCountHeaders} containing the result of the custom query, or {@code NULL} if the dao session is invalid.
	 */
	private static List < ClientStockCountHeaders > getClientStockCountHeadersQueryResultAutoSync ( final Context context , final DaoSession daoSession ) {
		// Check if the dao session is valid
		if ( daoSession == null )
			// Invalid dao session
			return null;
		// Retrieve and return all the client stock count headers that are not processed OR their visit ID is in still on-going
		return daoSession.getClientStockCountHeadersDao ().queryRaw ( 
				"WHERE " + ClientStockCountHeadersDao.Properties.IsProcessed.columnName + "=? AND " + ClientStockCountHeadersDao.Properties.VisitID.columnName + "!=?" ,
				new String [] { String.valueOf ( IsProcessedUtils.isNotSync () ) , String.valueOf ( CallAction.getVisitID ( context ) ) } );
	}

}