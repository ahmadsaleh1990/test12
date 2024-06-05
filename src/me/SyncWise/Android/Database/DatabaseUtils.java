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

import java.lang.reflect.Method;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import me.SyncWise.Android.Database.DaoMaster;
import me.SyncWise.Android.Database.DaoMaster.DevOpenHelper;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.text.TextUtils;

/**
 * Utilities class for SQLLite database and greenDao ORM.
 * 
 * @author Elias
 *
 */
public class DatabaseUtils {

	/**
	 * Singleton instance of {@code Utilities}.
	 */
    private static DatabaseUtils singleton;
	
	/**
	 * Reference to DaoMaster which holds the database object (SQLiteDatabase) and manages DAO classes (not objects) for a specific schema.<br>
	 * DaoMaster represents the entry point for using greenDAO.
	 */
    private final DaoMaster daoMaster;
    
    /**
     * Reference to DaoSession which manages all available DAO objects for a specific schema.
     */
    private final DaoSession daoSession;
    
	/**
	 * Shared preference file name.
	 */
	private static final String FILENAME = DatabaseUtils.class.getName () + ".OWNER";
	
	/**
	 * Current licensed key used in the shared preference file.
	 */
	private static final String LICENSED = DatabaseUtils.class.getName () + ".LICENSED";
	
	/**
	 * Current user code key used in the shared preference file.
	 */
	private static final String USER_CODE = DatabaseUtils.class.getName () + ".USER_CODE";
	
	/**
	 * Current company code key used in the shared preference file.
	 */
	private static final String COMPANY_CODE = DatabaseUtils.class.getName () + ".COMPANY_CODE";

	/**
	 * Current company code key used in the shared preference file.
	 */
	private static final String DIVISION_CODE = DatabaseUtils.class.getName () + ".DIVISION_CODE";
	
	/**
	 * Current company code key used in the shared preference file.
	 */
	private static final String PARENT_DIVISION_CODE = DatabaseUtils.class.getName () + ".PARENT_DIVISION_CODE";
	
    /**
     * Initializes and keeps track of only one class instance.
     * 
     * @param context	The application context.
     * @return	An instance of {@link me.SyncWise.Android.Database.DatabaseUtils}.
     */
    public static DatabaseUtils getInstance ( final Context context ) {
    	// Check if the singleton instance is initialized
    	if ( singleton == null )
    		// The singleton instance is not initialized
    		singleton = new DatabaseUtils ( context );
    	// Return the singleton instance
        return singleton;
    }
    
    /**
     * Getter - {@link #daoSession}
     * 
     * @return	Reference to the DAO session.
     */
    public DaoSession getDaoSession () {
    	// Return the DaoSession reference
    	return daoSession;
    }
    
    /**
     * Constructor.
     * Made private for singleton implementation.
     * 
     * @param context	The application context.
     */
    private DatabaseUtils ( final Context context ) {
    	// Retrieve a convenience SQLiteOpenHelper
    	DevOpenHelper helper = new DaoMaster.DevOpenHelper ( context.getApplicationContext () , "SWDB" , null );
    	// Build a DaoMaster using the opened database
		daoMaster = new DaoMaster ( helper.getWritableDatabase () );
		// Retrieve a new session
		daoSession = daoMaster.newSession ();
    }
	
    /**
     * Gets the {@code IsProcessed} {@link de.greenrobot.dao.Property Property} of the provided {@link de.greenrobot.dao.AbstractDao AbstractDao} table.
     * 
     * @param dao	Reference to the AbstractDao, which in turns references the main table.
     * @return	{@code IsProcessed} {@link de.greenrobot.dao.Property Property} of the provided dao, or {@code NULL} if invalid.
     */
    public static Property getIsProcessed ( final AbstractDao < ? , ? > dao ) {
    	// Check if the dao is valid
    	if ( dao == null )
    		// Invalid null
    		return null;
    	// Retrieve all the dao's properties
    	Property properties [] = dao.getProperties ();
    	// Iterate over all properties
    	for ( Property property : properties )
    		// Determine if the property is IsProcessed
    		if ( property.name.equalsIgnoreCase ( "isprocessed" ) )
    			// Return the IsProcessed property
    			return property;
    	// Otherwise, return null
    	return null;
    }
    
	/**
	 * Retrieves the licensed flag.
	 * 
	 * @param context The application context.
	 */
	public static boolean IsLicensed ( final Context context ) {
		// Open the shared preference file
		SharedPreferences language = context.getSharedPreferences ( FILENAME , Context.MODE_PRIVATE );
		// Retrieve the stored licensed flag (if none found, use false)
		return language.getBoolean ( LICENSED , false );
	}
	
	/**
	 * Sets the licensed flag as true.
	 * 
	 * @param context The application context.
	 */
	public static void setIsLicensed ( final Context context ) {
		// Open the shared preference file
		SharedPreferences sharedPref = context.getSharedPreferences ( FILENAME , Context.MODE_PRIVATE );
		// Retrieve a new Editor for these preferences to modify them
		SharedPreferences.Editor editor = sharedPref.edit ();
		// Set the licensed flag in the preferences editor
		editor.putBoolean ( LICENSED , true );
		// Commit the preferences changes
		editor.commit ();
	}
    
	/**
	 * Retrieves the current user code.
	 * 
	 * @param context The application context.
	 * @return String hosting the user code, or {@code NULL} if none is found.
	 */
	public static String getCurrentUserCode  ( final Context context ) {
		// Open the shared preference file
		SharedPreferences language = context.getSharedPreferences ( FILENAME , Context.MODE_PRIVATE );
		// Retrieve the stored user code (if none found, use NULL)
		return language.getString ( USER_CODE , null );
	}
	
	/**
	 * Saves the current user code.
	 * 
	 * @param context The application context.
	 * @param userCode The new user code to save.
	 */
	public static void setCurrentUserCode ( final Context context , final String userCode ) {
		// Check if the provided user code is valid
		if ( TextUtils.isEmpty ( userCode ) )
			// Invalid user code, exit method
			return;
		// Open the shared preference file
		SharedPreferences sharedPref = context.getSharedPreferences ( FILENAME , Context.MODE_PRIVATE );
		// Retrieve a new Editor for these preferences to modify them
		SharedPreferences.Editor editor = sharedPref.edit ();
		// Set the new user code in the preferences editor
		editor.putString ( USER_CODE , userCode );
		// Commit the preferences changes
		editor.commit ();
	}
	
	/**
	 * Retrieves the current company code.
	 * 
	 * @param context The application context.
	 * @return String hosting the company code, or {@code NULL} if none is found.
	 */
	public static String getCurrentCompanyCode  ( final Context context ) {
		// Open the shared preference file
		SharedPreferences language = context.getSharedPreferences ( FILENAME , Context.MODE_PRIVATE );
		// Retrieve the stored user company (if none found, use NULL)
		return language.getString ( COMPANY_CODE , null );
	}
	
	/**
	 * Saves the current user code.
	 * 
	 * @param context The application context.
	 * @param companyCode The new company code to save.
	 */
	public static void setCurrentCompanyCode ( final Context context , final String companyCode ) {
		// Check if the provided company code is valid
		if ( TextUtils.isEmpty ( companyCode ) )
			// Invalid company code, exit method
			return;
		// Open the shared preference file
		SharedPreferences sharedPref = context.getSharedPreferences ( FILENAME , Context.MODE_PRIVATE );
		// Retrieve a new Editor for these preferences to modify them
		SharedPreferences.Editor editor = sharedPref.edit ();
		// Set the new company code in the preferences editor
		editor.putString ( COMPANY_CODE , companyCode );
		// Commit the preferences changes
		editor.commit ();
	}
	
	/**
	 * Retrieves the current division code.
	 * 
	 * @param context The application context.
	 * @return String hosting the division code, or {@code NULL} if none is found.
	 */
	public static String getCurrentDivisionCode  ( final Context context ) {
		// Open the shared preference file
		SharedPreferences language = context.getSharedPreferences ( FILENAME , Context.MODE_PRIVATE );
		// Retrieve the stored user division (if none found, use NULL)
		return language.getString ( DIVISION_CODE , null );
	}
	
	
	
	/**
	 * Retrieves the current division code.
	 * 
	 * @param context The application context.
	 * @return String hosting the division code, or {@code NULL} if none is found.
	 */
	public static String getCurrentDivisionParent  ( final Context context ) {
		// Open the shared preference file
		SharedPreferences language = context.getSharedPreferences ( FILENAME , Context.MODE_PRIVATE );
		// Retrieve the stored user division (if none found, use NULL)
		return language.getString ( PARENT_DIVISION_CODE , null );
	}
	
	
	
	public static void	setCurrentDivisionParent( final Context context , final String divisionCode ) {
		// Check if the provided division code is valid
		if ( TextUtils.isEmpty ( divisionCode ) )
			// Invalid division code, exit method
			return;
		// Open the shared preference file
		SharedPreferences sharedPref = context.getSharedPreferences ( FILENAME , Context.MODE_PRIVATE );
		// Retrieve a new Editor for these preferences to modify them
		SharedPreferences.Editor editor = sharedPref.edit ();
		// Set the new division code in the preferences editor
		editor.putString ( PARENT_DIVISION_CODE , divisionCode );
		// Commit the preferences changes
		editor.commit ();
	}
	/**
	 * Saves the current division code.
	 * 
	 * @param context The application context.
	 * @param divisionCode The new division code to save.
	 */
	public static void setCurrentDivisionCode ( final Context context , final String divisionCode ) {
		// Check if the provided division code is valid
		if ( TextUtils.isEmpty ( divisionCode ) )
			// Invalid division code, exit method
			return;
		// Open the shared preference file
		SharedPreferences sharedPref = context.getSharedPreferences ( FILENAME , Context.MODE_PRIVATE );
		// Retrieve a new Editor for these preferences to modify them
		SharedPreferences.Editor editor = sharedPref.edit ();
		// Set the new division code in the preferences editor
		editor.putString ( DIVISION_CODE , divisionCode );
		// Commit the preferences changes
		editor.commit ();
	}
	
	/**
	 * Returns the user sequence.
	 * 
	 * @param context	The application context.
	 * @param userCode	The user code.
	 * @param companyCode	The company code.
	 * @param property	A {@link de.greenrobot.dao.Property Property} object hosting the required field from the transaction sequence.
	 * @return	Integer hosting the user sequence as requested and indicated by the property.
	 */
	public static int getUserSequence ( final Context context , final String userCode , final String companyCode , final Property property ) throws Exception {
		// Retrieve a cursor over the transaction sequences
		Cursor cursor = DatabaseUtils.getInstance ( context ).getDaoSession ().getDatabase ().rawQuery ( 
				"SELECT * FROM TransactionSequences WHERE UserCode = ? AND CompanyCode = ?", new String [] { userCode , companyCode } );
		// Move the cursor to the first raw
		cursor.moveToFirst ();
		// Read the raw as transaction sequence
		TransactionSequences transactionSequence = DatabaseUtils.getInstance ( context ).getDaoSession ().getTransactionSequencesDao ().readEntity ( cursor , 0 );
		// Close the cursor
		cursor.close ();
		cursor = null;
		// Retrieve the appropriate method of TransactionSequences based on the provided property
		Method getter = TransactionSequences.class.getMethod ( "get" + property.columnName );
		// Execute the getter and return the user sequence
		return (Integer) getter.invoke ( transactionSequence );
	}
	
	/**
	 * Updates the user sequence in the database.
	 * 
	 * @param context	The application context.
	 * @param userCode	The user code.
	 * @param companyCode	The company code.
	 * @param property	A {@link de.greenrobot.dao.Property Property} object hosting the field to update in the transaction sequence.
	 * @param sequence	Integer hosting the updated user sequence.
	 */
	public static void setUserSequence ( final Context context , final String userCode , final String companyCode , final Property property , final int sequence ) throws Exception {
		// Declare and initialize the content values to update the sequence
		ContentValues contentValues = new ContentValues ();
		contentValues.put ( property.columnName , sequence );
		// Execute the update query
		int rowsAffected = DatabaseUtils.getInstance ( context ).getDaoSession ().getDatabase ().update ( TransactionSequencesDao.TABLENAME , // Table
				contentValues , // Values
				TransactionSequencesDao.Properties.UserCode.columnName + " = ? AND " + TransactionSequencesDao.Properties.CompanyCode.columnName + " = ?", // WhereClause
				new String [] { userCode , companyCode } ); // WhereArguments
		// Check if the number of rows affected is not valid
		if ( rowsAffected != 1 )
			// Indicated that an error occurred
			throw new Exception ();
	}
	public static Integer getSequence ( final Context context , final String userCode , final String companyCode,final int type ) {
		// Retrieve a cursor over the transaction sequences
		TransactionSequences transactionSequences=DatabaseUtils.getInstance(context).getDaoSession ().getTransactionSequencesDao().queryBuilder().where
				(TransactionSequencesDao.Properties.UserCode.eq( userCode),
						TransactionSequencesDao.Properties.CompanyCode.eq(companyCode)).unique();
	
	 
		TransactionSequences transactionSequence2 = transactionSequences;
		 if(PermissionsUtils.getForceSolveSequence(context, userCode, companyCode)){

				// Sales Orders
				if(type==TransactionHeadersUtils.Type.SALES_ORDER ){
				Integer orderSequence =null;
				if(orderSequence ==null){
					orderSequence=transactionSequence2.getSalesOrder();
					return orderSequence;
				}
			
					 
				}
			 
				// Sales Invoices
				if(type==TransactionHeadersUtils.Type.SALES_INVOICE ){
				Integer invoiceSequence = null;
				if(invoiceSequence ==null){
					invoiceSequence=transactionSequence2.getSalesInvoice() ;
					return invoiceSequence;
				}
				 
					}
				if(type==TransactionHeadersUtils.Type.SALES_RETURN ){
				Integer salesReturn = null;
				if(salesReturn ==null){
					salesReturn=transactionSequence2.getSalesReturn()  ;
					return salesReturn;
				}
				  
					}
				if(type==TransactionHeadersUtils.Type.SALES_RFR ){
				Integer salesRfr = null;
				if(salesRfr ==null){
					salesRfr=transactionSequence2.getSalesRFR()   ;
					return salesRfr;
				}
				 
				}
				if(type==TransactionHeadersUtils.Type.COLLECTION ){
					Integer collection = null;
					if(collection ==null){
						collection=transactionSequence2.getCollection()   ;
						return collection;
					}
					 
				}
				
				 
			 
				
				if(type==TransactionHeadersUtils.Type.Movement ){
					Integer collection =null;
					if(collection ==null){
						collection=transactionSequence2.getMovements ()   ;
						return collection;
					}
					
						 
				}
				
		 
				 
		 }
		 else{
		// Sales Orders
		if(type==TransactionHeadersUtils.Type.SALES_ORDER ){
		Integer orderSequence = getMaxSequence ( context , TransactionHeadersUtils.Type.SALES_ORDER );
		if(orderSequence!=null && transactionSequence2.getSalesOrder().intValue()>(orderSequence.intValue()+1)){
			orderSequence=transactionSequence2.getSalesOrder();
			return orderSequence ;
		}
		if(orderSequence ==null){
			orderSequence=transactionSequence2.getSalesOrder();
			return orderSequence;
		}
	
			return orderSequence+1;
		}
		 
		// Sales Invoices
		if(type==TransactionHeadersUtils.Type.SALES_INVOICE ){
		Integer invoiceSequence = getMaxSequence ( context , TransactionHeadersUtils.Type.SALES_INVOICE );
	 
		if(invoiceSequence!=null && transactionSequence2.getSalesInvoice().intValue()>(invoiceSequence.intValue()+1) )
			{
			invoiceSequence=transactionSequence2.getSalesInvoice();
			return invoiceSequence ;
			}
			if(invoiceSequence ==null){
			invoiceSequence=transactionSequence2.getSalesInvoice() ;
			return invoiceSequence;
		}
		
			return invoiceSequence+1;
			}
		if(type==TransactionHeadersUtils.Type.SALES_RETURN ){
		Integer salesReturn = getMaxSequence ( context , TransactionHeadersUtils.Type.SALES_RETURN );
		if(salesReturn!=null && transactionSequence2.getSalesReturn().intValue()>(salesReturn.intValue()+1)){
			salesReturn=transactionSequence2.getSalesReturn();
			return salesReturn ;
		}
		if(salesReturn ==null){
			salesReturn=transactionSequence2.getSalesReturn()  ;
			return salesReturn;
		}
		 
			return salesReturn+1;
			}
		if(type==TransactionHeadersUtils.Type.SALES_RFR ){
		Integer salesRfr = getMaxSequence ( context , TransactionHeadersUtils.Type.SALES_RFR );
		if(salesRfr!=null && transactionSequence2.getSalesRFR().intValue()>(salesRfr.intValue()+1)){
			 salesRfr=transactionSequence2.getSalesRFR();
			return salesRfr ;
		}
		if(salesRfr ==null){
			salesRfr=transactionSequence2.getSalesRFR()   ;
			return salesRfr;
		}
		
			return salesRfr+1;
		}
		if(type==TransactionHeadersUtils.Type.COLLECTION ){
			Integer collection = getMaxCollectionSequence(context,type) ;
			if(collection!=null && transactionSequence2.getCollection().intValue()>(collection+1))
			{
				 collection=transactionSequence2.getCollection();
				return collection ;
			}
			if(collection ==null){
				collection=transactionSequence2.getCollection()   ;
				return collection;
			}
			
				return collection+1;
		}
		
		 
 
		
		if(type==TransactionHeadersUtils.Type.Movement ){
			Integer collection = getMaxMovementCodeSequence( context,type) ;
			if(collection!=null && transactionSequence2.getMovements().intValue()>(collection.intValue()+1))
			{
				 collection=transactionSequence2.getMovements();
				return collection ;
			}
				if(collection ==null){
				collection=transactionSequence2.getMovements ()   ;
				return collection;
			}
			
				return collection+1;
		}
		
 
		 }
	 
 
		return -1;
	}
	
	public static Integer getMaxSequence ( final Context context , final int transactionType ) {
		Integer orderSequence = null;
		String SQL="select MAX(cast (TransactionCode as numeric(18,0) )) as tr from TransactionHeaders where  TransactionType=? ";
		Cursor cursor = DatabaseUtils.getInstance ( context ).getDaoSession ().getDatabase ().rawQuery ( 
				SQL , 
				new String [] { transactionType+"" } );
		
		String transactionCode="";
				
		// Move the cursor to the first raw
		if ( cursor.moveToFirst () ) {
			// Iterate over all raws
			do {
		 
				transactionCode=  cursor.getString(0) ;
				 
			} while ( cursor.moveToNext () );
		} // End if
		// Close the cursor
		cursor.close ();
		//		ArrayList <TransactionHeaders> orders = (ArrayList<TransactionHeaders>) DatabaseUtils.getInstance(context).getDaoSession().getTransactionHeadersDao().queryBuilder()
//				.where( TransactionHeadersDao.Properties.TransactionType.eq( transactionType ) )
//				.orderDesc( TransactionHeadersDao.Properties.IssueDate ).list();
		if (  transactionCode !=null &&!transactionCode.equals("")   ) {
 
			 String ss =transactionCode.substring( transactionCode.length()- (6) , transactionCode.length() );
			orderSequence = Integer.parseInt( ss );
		}
		return orderSequence;
	}
	public static Integer getMaxMovementCodeSequence ( final Context context , final int transactionType ) {
		Integer orderSequence = null;
		String SQL="select max(substr(movementcode,length(movementcode)-5,6)) as tr from MovementHeaders where  MovementType not  in (?,?,?,?) ";
		Cursor cursor = DatabaseUtils.getInstance ( context ).getDaoSession ().getDatabase ().rawQuery ( 
				SQL , 
				new String [] { "20","2" ,"13","15" } );
		
		String transactionCode="";
				
		// Move the cursor to the first raw
		if ( cursor.moveToFirst () ) {
			// Iterate over all raws
			do {
		 
				transactionCode=  cursor.getString(0) ;
				 
			} while ( cursor.moveToNext () );
		} // End if
		// Close the cursor
		cursor.close ();
		//		ArrayList <TransactionHeaders> orders = (ArrayList<TransactionHeaders>) DatabaseUtils.getInstance(context).getDaoSession().getTransactionHeadersDao().queryBuilder()
//				.where( TransactionHeadersDao.Properties.TransactionType.eq( transactionType ) )
//				.orderDesc( TransactionHeadersDao.Properties.IssueDate ).list();
		if (  transactionCode !=null &&!transactionCode.equals("")   ) {
 
			 String ss =transactionCode ;
			orderSequence = Integer.parseInt( ss );
		}
		return orderSequence;
	}
	public static Integer getMaxCollectionSequence ( final Context context ,final int type) {
		Integer orderSequence = null;
		String SQL="select MAX(cast (CollectionCode as numeric(18,0) )) as tr from CollectionHeaders where  substr(CollectionCode, 1, 1)=?  ";
		Cursor cursor = DatabaseUtils.getInstance ( context ).getDaoSession ().getDatabase ().rawQuery ( 
				SQL , 
				new String [] { type+ "" } );
		
		String transactionCode="";
				
		// Move the cursor to the first raw
		if ( cursor.moveToFirst () ) {
			// Iterate over all raws
			do {
		 
				transactionCode=  cursor.getString(0) ;
				 
			} while ( cursor.moveToNext () );
		} // End if
		// Close the cursor
		cursor.close ();
		//		ArrayList <TransactionHeaders> orders = (ArrayList<TransactionHeaders>) DatabaseUtils.getInstance(context).getDaoSession().getTransactionHeadersDao().queryBuilder()
//				.where( TransactionHeadersDao.Properties.TransactionType.eq( transactionType ) )
//				.orderDesc( TransactionHeadersDao.Properties.IssueDate ).list();
		if (  transactionCode !=null &&!transactionCode.equals("")   ) {
 
			 String ss =transactionCode.substring( transactionCode.length()- (6) , transactionCode.length() );
			orderSequence = Integer.parseInt( ss );
		}
		return orderSequence;
	}	
}