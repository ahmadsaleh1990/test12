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

import java.util.ArrayList;
import java.util.List;

import me.SyncWise.Android.Database.ClientAssetStatus;
import me.SyncWise.Android.Database.ClientAssetStatusDao;
import me.SyncWise.Android.Database.ClientAssetsPictures;
import me.SyncWise.Android.Database.ClientAssetsPicturesDao;
import me.SyncWise.Android.Database.ClientStockCountDetails;
import me.SyncWise.Android.Database.ClientStockCountDetailsDao;
import me.SyncWise.Android.Database.ClientStockCountHeaders;
import me.SyncWise.Android.Database.ClientStockCountHeadersDao;
import me.SyncWise.Android.Database.CollectionDetails;
import me.SyncWise.Android.Database.CollectionDetailsDao;
import me.SyncWise.Android.Database.CollectionHeaders;
import me.SyncWise.Android.Database.CollectionHeadersDao;
import me.SyncWise.Android.Database.CollectionInvoices;
import me.SyncWise.Android.Database.CollectionInvoicesDao;
import me.SyncWise.Android.Database.DaoSession;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.MovementDetails;
import me.SyncWise.Android.Database.MovementDetailsDao;
import me.SyncWise.Android.Database.MovementHeaders;
import me.SyncWise.Android.Database.MovementHeadersDao;
import me.SyncWise.Android.Database.NewClientImages;
import me.SyncWise.Android.Database.NewClientImagesDao;
import me.SyncWise.Android.Database.NewClients;
import me.SyncWise.Android.Database.TransactionDetails;
import me.SyncWise.Android.Database.TransactionDetailsDao;
import me.SyncWise.Android.Database.TransactionDetailsMissedMSL;
import me.SyncWise.Android.Database.TransactionDetailsMissedMSLDao;
import me.SyncWise.Android.Database.TransactionHeaders;
import me.SyncWise.Android.Database.TransactionHeadersDao;
import me.SyncWise.Android.Database.TransactionPromotionDetails;
import me.SyncWise.Android.Database.TransactionPromotionDetailsDao;
import me.SyncWise.Android.Modules.Call.CallAction;
import android.content.Context;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.QueryBuilder;

/**
 * Class related to data synchronization, holding all helper methods related to query builder execution specific for the <b>AUTO SYNC MODULE</b>.
 * 
 * @author Elias
 *
 */
public class AutoSyncCustomQueryBuilder {

	/**
	 * Gets a custom query builder specific for the <b>AUTO SYNC MODULE</b>, for the provided abstract dao table, based on custom query based on specific tables.<br>
	 * If no custom queries are defined for the provided table, {@code NULL} is returned.
	 * 
	 * @param context	The application context.
	 * @param dao	Reference to the AbstractDao, which in turns references the main table.
	 * @param daoSession Reference to the current DaoSession which gives access to all DAOs, offers convenient persistence methods, and also serves as a session cache.
	 * @return	A custom QueryBuilder for the provided table if one is defined, or {@code NULL} if none is defined.
	 */
	public static QueryBuilder < ? > getCustomQueryBuilderAutoSync ( final Context context , final AbstractDao < ? , ? > dao , final DaoSession daoSession ) {
		// Determine if the abstract dao table has a custom query defined
		if ( dao instanceof TransactionDetailsDao )
			// Compute and return the transaction details custom query builder
			return getTransactionDetailsQueryBuilderAutoSync ( context , daoSession );
		if ( dao instanceof TransactionDetailsMissedMSLDao  )
			// Compute and return the transaction details custom query builder
			return getTransactionDetailsMissedMSLQueryBuilderAutoSync ( context , daoSession );
		// Determine if the abstract dao table has a custom query defined
		else if ( dao instanceof TransactionPromotionDetailsDao )
			// Compute and return the transaction promotion details custom query builder
			return getTransactionPromotionDetailsQueryBuilderAutoSync ( context , daoSession );
		// Determine if the abstract dao table has a custom query defined
		else if ( dao instanceof MovementDetailsDao )
			// Compute and return the movement details custom query builder
			return getMovementDetailsQueryBuilderAutoSync ( context , daoSession );
		// Determine if the abstract dao table has a custom query defined
		else if ( dao instanceof CollectionDetailsDao )
			// Compute and return the collection details custom query builder
			return getCollectionDetailsQueryBuilderAutoSync ( context , daoSession );
		// Determine if the abstract dao table has a custom query defined
		else if ( dao instanceof CollectionInvoicesDao )
			// Compute and return the collection invoices custom query builder
			return getCollectionInvoicesQueryBuilderAutoSync ( context , daoSession );
		// Determine if the abstract dao table has a custom query defined
		else if ( dao instanceof ClientStockCountDetailsDao )
			// Compute and return the client stock count details custom query builder
			return getClientStockCountDetailsQueryBuilderAutoSync ( context , daoSession );
			// Determine if the abstract dao table has a custom query defined
		else if ( dao instanceof ClientAssetStatusDao )
			// Compute and return the client asset statuses custom query builder
			return getClientAssetStatusQueryBuilderAutoSync ( context , daoSession );
		// Determine if the abstract dao table has a custom query defined
		else if ( dao instanceof ClientAssetsPicturesDao )
			// Compute and return the client assets pictures custom query builder
			return getClientAssetsPicturesQueryBuilderAutoSync ( context , daoSession );
		// Determine if the abstract dao table has a custom query defined
		else if ( dao instanceof NewClientImagesDao )
			// Compute and return the new client images custom query builder
			return getNewClientImagesAutoSync ( context , daoSession );
		else
			// No custom query defined
			return null;
	}
	
	private static QueryBuilder < TransactionDetailsMissedMSL > getTransactionDetailsMissedMSLQueryBuilderAutoSync ( final Context context , final DaoSession daoSession ) {
		// Check if the dao session is valid
		if ( daoSession == null )
			// Invalid dao session
			return null;
		// Retrieve all the transaction headers that are not processed OR their visit ID is in still on-going
		List < TransactionHeaders > headers = daoSession.getTransactionHeadersDao ().queryRaw ( 
				"WHERE " + TransactionHeadersDao.Properties.IsProcessed.columnName + "=? AND " + TransactionHeadersDao.Properties.VisitID.columnName + "!=?" ,
				new String [] {
						String.valueOf ( IsProcessedUtils.isNotSync () ) ,
						String.valueOf ( CallAction.getVisitID ( context ) )
				} );
		// Declare and initialize a list of strings used to host the transaction codes
		List < String > codes = new ArrayList < String > ();
		// Iterate over all transactions
		for ( TransactionHeaders header : headers )
			// Add the transaction code to the list
			codes.add ( header.getTransactionCode () );
		// Compute and return a query builder used to query all the transaction details of the not processed transaction headers
		return daoSession.getTransactionDetailsMissedMSLDao ().queryBuilder ().where (TransactionDetailsMissedMSLDao.Properties.TransactionCode.in ( codes ) );
	}
	/**
	 * Gets a custom query builder for the {@link me.SyncWise.Android.Database.TransactionDetails TransactionDetails} specific for the <b>AUTO SYNC MODULE</b>.
	 * 
	 * @param context	The application context.
	 * @param daoSession	Reference to the current DaoSession which gives access to all DAOs, offers convenient persistence methods, and also serves as a session cache.
	 * @return	A custom QueryBuilder for the {@link me.SyncWise.Android.Database.TransactionDetails TransactionDetails}, or {@code NULL} if the dao session is invalid.
	 */
	private static QueryBuilder < TransactionDetails > getTransactionDetailsQueryBuilderAutoSync ( final Context context , final DaoSession daoSession ) {
		// Check if the dao session is valid
		if ( daoSession == null )
			// Invalid dao session
			return null;
		// Retrieve all the transaction headers that are not processed OR their visit ID is in still on-going
		List < TransactionHeaders > headers = daoSession.getTransactionHeadersDao ().queryRaw ( 
				"WHERE " + TransactionHeadersDao.Properties.IsProcessed.columnName + "=? AND " + TransactionHeadersDao.Properties.VisitID.columnName + "!=?" ,
				new String [] {
						String.valueOf ( IsProcessedUtils.isNotSync () ) ,
						String.valueOf ( CallAction.getVisitID ( context ) )
				} );
		// Declare and initialize a list of strings used to host the transaction codes
		List < String > codes = new ArrayList < String > ();
		// Iterate over all transactions
		for ( TransactionHeaders header : headers )
			// Add the transaction code to the list
			codes.add ( header.getTransactionCode () );
		// Compute and return a query builder used to query all the transaction details of the not processed transaction headers
		return daoSession.getTransactionDetailsDao ().queryBuilder ().where ( TransactionDetailsDao.Properties.TransactionCode.in ( codes ) );
	}
	
	/**
	 * Gets a custom query builder for the {@link me.SyncWise.Android.Database.TransactionPromotionDetails TransactionPromotionDetails} specific for the <b>AUTO SYNC MODULE</b>.
	 * 
	 * @param context	The application context.
	 * @param daoSession	Reference to the current DaoSession which gives access to all DAOs, offers convenient persistence methods, and also serves as a session cache.
	 * @return	A custom QueryBuilder for the {@link me.SyncWise.Android.Database.TransactionPromotionDetails TransactionPromotionDetails}, or {@code NULL} if the dao session is invalid.
	 */
	private static QueryBuilder < TransactionPromotionDetails > getTransactionPromotionDetailsQueryBuilderAutoSync ( final Context context , final DaoSession daoSession ) {
		// Check if the dao session is valid
		if ( daoSession == null )
			// Invalid dao session
			return null;
		// Retrieve all the transaction headers that are not processed OR their visit ID is in still on-going
		List < TransactionHeaders > headers = daoSession.getTransactionHeadersDao ().queryRaw ( 
				"WHERE " + TransactionHeadersDao.Properties.IsProcessed.columnName + "=? AND " + TransactionHeadersDao.Properties.VisitID.columnName + "!=?" ,
				new String [] {
						String.valueOf ( IsProcessedUtils.isNotSync () ) ,
						String.valueOf ( CallAction.getVisitID ( context ) )
				} );
		// Declare and initialize a list of strings used to host the transaction codes
		List < String > codes = new ArrayList < String > ();
		// Iterate over all transactions
		for ( TransactionHeaders header : headers )
			// Add the transaction code to the list
			codes.add ( header.getTransactionCode () );
		// Compute and return a query builder used to query all the transaction promotion details of the not processed transaction headers
		return daoSession.getTransactionPromotionDetailsDao ().queryBuilder ().where ( TransactionPromotionDetailsDao.Properties.TransactionCode.in ( codes ) );
	}
	
	/**
	 * Gets a custom query builder for the {@link me.SyncWise.Android.Database.MovementDetails MovementDetails} specific for the <b>AUTO SYNC MODULE</b>.
	 * 
	 * @param context	The application context.
	 * @param daoSession	Reference to the current DaoSession which gives access to all DAOs, offers convenient persistence methods, and also serves as a session cache.
	 * @return	A custom QueryBuilder for the {@link me.SyncWise.Android.Database.MovementDetails MovementDetails}, or {@code NULL} if the dao session is invalid.
	 */
	private static QueryBuilder < MovementDetails > getMovementDetailsQueryBuilderAutoSync ( final Context context , final DaoSession daoSession ) {
		// Check if the dao session is valid
		if ( daoSession == null )
			// Invalid dao session
			return null;
		// Retrieve all the movement headers that are not processed
		List < MovementHeaders > headers = daoSession.getMovementHeadersDao ().queryRaw ( 
				"WHERE " + MovementHeadersDao.Properties.IsProcessed.columnName + "=?" ,
				new String [] { String.valueOf ( IsProcessedUtils.isNotSync () ) } );
		// Declare and initialize a list of strings used to host the movement codes
		List < String > codes = new ArrayList < String > ();
		// Iterate over all movements
		for ( MovementHeaders header : headers )
			// Add the movement code to the list
			codes.add ( header.getMovementCode () );
		// Compute and return a query builder used to query all the movement details of the not processed movement headers
		return daoSession.getMovementDetailsDao ().queryBuilder ().where ( MovementDetailsDao.Properties.MovementCode.in ( codes ) );
	}
	
	/**
	 * Gets a custom query builder for the {@link me.SyncWise.Android.Database.CollectionDetails CollectionDetails} specific for the <b>AUTO SYNC MODULE</b>.
	 * 
	 * @param context	The application context.
	 * @param daoSession	Reference to the current DaoSession which gives access to all DAOs, offers convenient persistence methods, and also serves as a session cache.
	 * @return	A custom QueryBuilder for the {@link me.SyncWise.Android.Database.CollectionDetails CollectionDetails}, or {@code NULL} if the dao session is invalid.
	 */
	private static QueryBuilder < CollectionDetails > getCollectionDetailsQueryBuilderAutoSync ( final Context context , final DaoSession daoSession ) {
		// Check if the dao session is valid
		if ( daoSession == null )
			// Invalid dao session
			return null;
		// Retrieve all the collection headers that are not processed OR their visit ID is in still on-going
		List < CollectionHeaders > headers = daoSession.getCollectionHeadersDao ().queryRaw ( 
				"WHERE " + CollectionHeadersDao.Properties.IsProcessed.columnName + "=? AND " + CollectionHeadersDao.Properties.VisitID.columnName + "!=?" ,
				new String [] {
						String.valueOf ( IsProcessedUtils.isNotSync () ) ,
						String.valueOf ( CallAction.getVisitID ( context ) )
				} );
		// Declare and initialize a list of strings used to host the collection codes
		List < String > codes = new ArrayList < String > ();
		// Iterate over all collections
		for ( CollectionHeaders header : headers )
			// Add the transaction code to the list
			codes.add ( header.getCollectionCode () );
		// Compute and return a query builder used to query all the collection details of the not processed collection headers
		return daoSession.getCollectionDetailsDao ().queryBuilder ().where ( CollectionDetailsDao.Properties.CollectionCode.in ( codes ) );
	}
	
	/**
	 * Gets a custom query builder for the {@link me.SyncWise.Android.Database.CollectionInvoices CollectionInvoices} specific for the <b>AUTO SYNC MODULE</b>.
	 * 
	 * @param context	The application context.
	 * @param daoSession	Reference to the current DaoSession which gives access to all DAOs, offers convenient persistence methods, and also serves as a session cache.
	 * @return	A custom QueryBuilder for the {@link me.SyncWise.Android.Database.CollectionInvoices CollectionInvoices}, or {@code NULL} if the dao session is invalid.
	 */
	private static QueryBuilder < CollectionInvoices > getCollectionInvoicesQueryBuilderAutoSync ( final Context context , final DaoSession daoSession ) {
		// Check if the dao session is valid
		if ( daoSession == null )
			// Invalid dao session
			return null;
		// Retrieve all the collection headers that are not processed OR their visit ID is in still on-going
		List < CollectionHeaders > headers = daoSession.getCollectionHeadersDao ().queryRaw ( 
				"WHERE " + CollectionHeadersDao.Properties.IsProcessed.columnName + "=? AND " + CollectionHeadersDao.Properties.VisitID.columnName + "!=?" ,
				new String [] {
						String.valueOf ( IsProcessedUtils.isNotSync () ) ,
						String.valueOf ( CallAction.getVisitID ( context ) )
				} );
		// Declare and initialize a list of strings used to host the collection codes
		List < String > codes = new ArrayList < String > ();
		// Iterate over all collections
		for ( CollectionHeaders header : headers )
			// Add the transaction code to the list
			codes.add ( header.getCollectionCode () );
		// Compute and return a query builder used to query all the collection invoices of the not processed collection headers
		return daoSession.getCollectionInvoicesDao ().queryBuilder ().where ( CollectionInvoicesDao.Properties.CollectionCode.in ( codes ) );
	}
	
	/**
	 * Gets a custom query builder for the {@link me.SyncWise.Android.Database.ClientStockCountDetails ClientStockCountDetails} specific for the <b>AUTO SYNC MODULE</b>.
	 * 
	 * @param context	The application context.
	 * @param daoSession	Reference to the current DaoSession which gives access to all DAOs, offers convenient persistence methods, and also serves as a session cache.
	 * @return	A custom QueryBuilder for the {@link me.SyncWise.Android.Database.ClientStockCountDetails ClientStockCountDetails}, or {@code NULL} if the dao session is invalid.
	 */
	private static QueryBuilder < ClientStockCountDetails > getClientStockCountDetailsQueryBuilderAutoSync ( final Context context , final DaoSession daoSession ) {
		// Check if the dao session is valid
		if ( daoSession == null )
			// Invalid dao session
			return null;
		// Retrieve all the client stock count headers that are not processed OR their visit ID is in still on-going
		List < ClientStockCountHeaders > headers = daoSession.getClientStockCountHeadersDao ().queryRaw ( 
				"WHERE " + ClientStockCountHeadersDao.Properties.IsProcessed.columnName + "=? AND " + ClientStockCountHeadersDao.Properties.VisitID.columnName + "!=?" ,
				new String [] {
						String.valueOf ( IsProcessedUtils.isNotSync () ) ,
						String.valueOf ( CallAction.getVisitID ( context ) )
				} );
		// Declare and initialize a list of strings used to host the transaction codes
		List < String > codes = new ArrayList < String > ();
		// Iterate over all client stock counts
		for ( ClientStockCountHeaders header : headers )
			// Add the transaction code to the list
			codes.add ( header.getTransactionCode () );
		// Compute and return a query builder used to query all the client stock count details of the not processed client stock count headers
		return daoSession.getClientStockCountDetailsDao ().queryBuilder ().where ( ClientStockCountDetailsDao.Properties.TransactionCode.in ( codes ) );
	}
	
	/**
	 * Gets a custom query builder for the {@link me.SyncWise.Android.Database.ClientAssetStatus ClientAssetStatus} specific for the <b>AUTO SYNC MODULE</b>.
	 * 
	 * @param context	The application context.
	 * @param daoSession	Reference to the current DaoSession which gives access to all DAOs, offers convenient persistence methods, and also serves as a session cache.
	 * @return	A custom QueryBuilder for the {@link me.SyncWise.Android.Database.ClientAssetStatus ClientAssetStatus}, or {@code NULL} if the dao session is invalid.
	 */
	private static QueryBuilder < ClientAssetStatus > getClientAssetStatusQueryBuilderAutoSync ( final Context context , final DaoSession daoSession ) {
		// Check if the dao session is valid
		if ( daoSession == null )
			// Invalid dao session
			return null;
		// Retrieve all the client stock count headers that are not processed OR their visit ID is in still on-going
		List < ClientStockCountHeaders > headers = daoSession.getClientStockCountHeadersDao ().queryRaw ( 
				"WHERE " + ClientStockCountHeadersDao.Properties.IsProcessed.columnName + "=? AND " + ClientStockCountHeadersDao.Properties.VisitID.columnName + "!=?" ,
				new String [] {
						String.valueOf ( IsProcessedUtils.isNotSync () ) ,
						String.valueOf ( CallAction.getVisitID ( context ) )
				} );
		// Declare and initialize a list of strings used to host the transaction codes
		List < String > codes = new ArrayList < String > ();
		// Iterate over all client stock counts
		for ( ClientStockCountHeaders header : headers )
			// Add the transaction code to the list
			codes.add ( header.getTransactionCode () );
		// Compute and return a query builder used to query all the client asset statuses of the not processed client stock count headers
		return daoSession.getClientAssetStatusDao ().queryBuilder ().where ( ClientAssetStatusDao.Properties.TransactionCode.in ( codes ) );
	}
	
	/**
	 * Gets a custom query builder for the {@link me.SyncWise.Android.Database.ClientAssetsPictures ClientAssetsPictures} specific for the <b>AUTO SYNC MODULE</b>.
	 * 
	 * @param context	The application context.
	 * @param daoSession	Reference to the current DaoSession which gives access to all DAOs, offers convenient persistence methods, and also serves as a session cache.
	 * @return	A custom QueryBuilder for the {@link me.SyncWise.Android.Database.ClientAssetsPictures ClientAssetsPictures}, or {@code NULL} if the dao session is invalid.
	 */
	private static QueryBuilder < ClientAssetsPictures > getClientAssetsPicturesQueryBuilderAutoSync ( final Context context , final DaoSession daoSession ) {
		// Check if the dao session is valid
		if ( daoSession == null )
			// Invalid dao session
			return null;
		// Retrieve all the client stock count headers that are not processed OR their visit ID is in still on-going
		List < ClientStockCountHeaders > headers = daoSession.getClientStockCountHeadersDao ().queryRaw ( 
				"WHERE " + ClientStockCountHeadersDao.Properties.IsProcessed.columnName + "=? AND " + ClientStockCountHeadersDao.Properties.VisitID.columnName + "!=?" ,
				new String [] {
						String.valueOf ( IsProcessedUtils.isNotSync () ) ,
						String.valueOf ( CallAction.getVisitID ( context ) )
				} );
		// Declare and initialize a list of strings used to host the transaction codes
		List < String > codes = new ArrayList < String > ();
		// Iterate over all client stock counts
		for ( ClientStockCountHeaders header : headers )
			// Add the transaction code to the list
			codes.add ( header.getTransactionCode () );
		// Compute and return a query builder used to query all the client assets pictures of the not processed client stock count headers
		return daoSession.getClientAssetsPicturesDao ().queryBuilder ().where ( ClientAssetsPicturesDao.Properties.TransactionCode.in ( codes ) );
	}
	
	/**
	 * Gets a custom query builder for the {@link me.SyncWise.Android.Database.NewClientImages NewClientImages} specific for the <b>AUTO SYNC MODULE</b>.
	 * 
	 * @param context	The application context.
	 * @param daoSession	Reference to the current DaoSession which gives access to all DAOs, offers convenient persistence methods, and also serves as a session cache.
	 * @return	A custom QueryBuilder for the {@link me.SyncWise.Android.Database.NewClientImages NewClientImages}, or {@code NULL} if the dao session is invalid.
	 */
	private static QueryBuilder < NewClientImages > getNewClientImagesAutoSync ( final Context context , final DaoSession daoSession ) {
		// Check if the dao session is valid
		if ( daoSession == null )
			// Invalid dao session
			return null;
		// Retrieve all the new clients that are not processed
		List < NewClients > newClients = daoSession.getNewClientsDao ().queryRaw ( 
				"WHERE " + ClientStockCountHeadersDao.Properties.IsProcessed.columnName + "=?"  ,
				new String [] { String.valueOf ( IsProcessedUtils.isNotSync () ) } );
		// Declare and initialize a list of strings used to host the client codes
		List < String > codes = new ArrayList < String > ();
		// Iterate over all new clients
		for ( NewClients newClient : newClients )
			// Add the client code to the list
			codes.add ( newClient.getClientCode () );
		// Compute and return a query builder used to query all the new client images of the not processed new clients
		return daoSession.getNewClientImagesDao ().queryBuilder ().where ( NewClientImagesDao.Properties.ClientCode.in ( codes ) );
	}
	
}