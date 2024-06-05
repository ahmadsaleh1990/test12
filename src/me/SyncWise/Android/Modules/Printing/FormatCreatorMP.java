/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Printing;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
 

 
import me.SyncWise.Android.Database.Clients;
import me.SyncWise.Android.Database.ClientsDao;
import me.SyncWise.Android.Database.DatabaseUtils;
 
import me.SyncWise.Android.Database.ClientStockCountDetails;
import me.SyncWise.Android.Database.ClientStockCountDetailsDao;
import me.SyncWise.Android.Database.ClientStockCountHeaders;
import me.SyncWise.Android.Database.CollectionDetails;
 
import me.SyncWise.Android.Database.CollectionHeaders;
 
import me.SyncWise.Android.Database.ClientItemCodes;
import me.SyncWise.Android.Database.ClientItemCodesDao;
import me.SyncWise.Android.Database.CollectionInvoices;
import me.SyncWise.Android.Database.CollectionInvoicesDao;
 
import me.SyncWise.Android.Database.CurrenciesDao;
import me.SyncWise.Android.Database.CycleCalls;
import me.SyncWise.Android.Database.ItemBarcodes;
import me.SyncWise.Android.Database.ItemBarcodesDao;
import me.SyncWise.Android.Database.Items;
import me.SyncWise.Android.Database.ItemsDao;
import me.SyncWise.Android.Database.ItemsUtils;
import me.SyncWise.Android.Database.MovementDetails;
import me.SyncWise.Android.Database.MovementDetailsDao;
import me.SyncWise.Android.Database.MovementHeaders;
import me.SyncWise.Android.Database.PaymentTerms;
import me.SyncWise.Android.Database.PaymentTermsDao;
import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Database.Visits;
import me.SyncWise.Android.Database.VisitsDao;
 
import me.SyncWise.Android.Database.TransactionDetails;
import me.SyncWise.Android.Database.TransactionDetailsDao;
import me.SyncWise.Android.Database.TransactionHeaders;
import me.SyncWise.Android.Database.TransactionHeadersDao;
 
 
import me.SyncWise.Android.Database.Vehicles;
import me.SyncWise.Android.Database.VehiclesDao;
 
import me.SyncWise.Android.Database.Users;
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Modules.Printing.PrintingActivity.Type;
import android.content.Context;
import android.database.Cursor;
 

public class FormatCreatorMP implements FormatCreator {
	
 	public final int UserCode_Length=10;
 	public final int UserName_Length=25;
 	public final int ISSUE_DATE_Length=25;
 	public final int TOTAL_LENGTH = 20;
	public final int TOTAL_LENGTH_LOAD = 32;
	public final int CODE_LENGTH = 10;
	public final int QUANTITY_LENGTH = 9;
	public final int UNIT_PRICE_LENGTH = 8;
	public final int LINE_AMOUNT_LENGTH = 10;
	public final int price_LENGTH =9;
	public final int uom_LENGTH  =6;
	public final int BARCODE_LENGTH = 15;
	public final int CLIENT_ID_length=10;
	public final int CLIENT_Name_length=40;
	public final int CASES_UNITS_Length=15;
	public final int INVOICE_Length=17;
	public final int INVOICE_DATE_Length=20;
	public final int Amount_Length=20;
	public final int QTY_LENGTH =9;
	public final int Bank_Length =15;
	public final  int Cheque_Length =20;
	public final int Values_Length=14;
	public final int warehouse_length=18;
	
	public final int DISCOUNT_LENGTH = 5;
	
	// Transaction
	 private TransactionHeaders transactionHeader;
	 private ArrayList<TransactionDetails> transactionDetails;
	 private Clients client;
	 private ArrayList<SalesRoutingReport> SalesRoutingReport; 
	 private ArrayList<SchedulePercentage> schedulePercentage; 
	// Movement
	 private MovementHeaders movementHeaders;
	 private ArrayList<MovementDetails> movementDetails;
	 private List < LoadPrinting > loadPrintings = new ArrayList < LoadPrinting > ();
	 
	 private Vehicles vehicle;

	 //VehiclesStock
	 private ArrayList<vehiclesStockPrinting> vehiclesStock;
	
	 //Receipt  collectionHeaders
	 String RouteReportHeader="INV/DOC #    T-IN  T-OUT CUSTOMER NUMBER                TRXN       AMOUNT";
	 private CollectionHeaders collectionHeaders;
	 private ArrayList<CollectionDetails> collectionDetails;
     private ArrayList<CollectionInvoices>collectionInvoices;
    
     //collection Summary
     private ArrayList<CollectionHeaders> collectionHeaderss;
     private ArrayList < CollectionSummaryPrinting > collectionSummaryHeaderss;
     private ArrayList < SummaryReturnPrinting > summaryReturnPrintings;
    
     //clientStockCountHeaders
     private ClientStockCountHeaders clientStockCountHeaders;
	 private ArrayList<ClientStockCountDetails> clientStockCountDetails;
    
	 //loadInSummaryPrinting
	 private ArrayList<LoadInSummaryPrinting>loadInSummaryPrinting;
	
	 //TransactionHeaderss
	 private ArrayList<TransactionHeaders> transactionHeaderss;
	
	 //MissedItems
	 private ArrayList <MissedItems>  missedItems;
	
	 //Item Distribution
	 private ArrayList <Items>  items;
	 private ArrayList<ItemDistrbutionPrinting>itemDistrbutionPrinting;
	
	 //clientAssetPrinting
	 private ArrayList<ClientAssetPrinting>clientAssetPrinting;
	 
	 //PLAN VISIT REPORT
	 private ArrayList<CycleCalls> cycleCalls;
	 
	 
	public final String SALES_MAN				= "SalesMan ";
	public final String CLIENT_ID				= "Cust# : ";
	public final String CLIENT_NAME				= "Client Name    : ";
	public final String CLIENT_ADDRESS			= "Client Address : ";
	public final String CLIENT_PHONE 			= "Client Phone   : ";
	public final String DATE					= "Date:%d/%d/%d";
	public final String PRINT_DATE				= "Print Date : ";
	public final String ISSUE_DATE				= "IssueDate ";
	public final String dATE				    = "Date ";
	public final String ORDER_NUM				= "Invoice#     : ";
	public final String LOAD_NUM				= "DOCUMENT#  : ";
	public final String RFR_NUM					= "REQUEST#   : ";
	public final String BARCODE					= "BC. : ";
	public final String HORIZANTAL_LINE			= "-------------------------------------------------------------------------------";
	public final String RECEPIT					= "                    PRINTOUT";
	public final String RECEPIT_COPY			= "             COPY - PRINTOUT - COPY";
	
	//invoice
	public final String OI_DETIAL_HEADER		= "PROD#     PRODUCT DESCRIPTION         | UOM  |  QTY  | PRICE | U/PRICE | TOTAL";
	
	//invoice-discount
	public final String OI_DETIAL_HEADER_DS		= "PROD#     PRODUCT DESCRIPTION  | UOM  |  QTY  | PRICE | U/PRICE | DIS% | TOTAL";
	
	//order
	public final String Order_OI_DETIAL_HEADER  = "PROD#     PRODUCT DESCRIPTION         | CIC  |  QTY  | PRICE | U/PRICE | TOTAL";
	 
	public final String Order_DETIAL_HEADER		= "PROD#     PRODUCT DESCRIPTION    |    QTY   |  PRICE  | TOTAL";
	
	//transfer
	public final String LOAD_DETIAL_HEADER		= "PROD#     PRODUCT DESCRIPTION           | UOM  |CASES/UNITS|   QTY   | VALUES";
	
	public final String LOADREQUEST_DETIAL_HEADER = "ITEM#     PRODUCT DESCRIPTION                | REQUESTED";
	public final String LOAD_HEADER               = "ITEM# PRODUCT DESCRIPTION      |Box/Unit|MBox/Unt|STK BOX/UNIT/QTY| MBU  | BUQ ";
	public final String STOCKRECONCILIATION_DETIAL_HEADER = "ITEM# PRODUCT DESCRIPTION              | Box/Unit | MBox/Unit | STOCK QTY ";
	public final String STOCKRECONCILIATION_LOAD_HEADER   = "ITEM# PRODUCT DESCRIPTION      |  UOM | COUNT C/U|VAR C/U|STK HAND C/U|STK QTY   ";
	public final String LOADREQUEST_DETIAL_HEADER_DirectLoad = "ITEM#     PRODUCT DESCRIPTION                | LOADED";
	public final String LOADREQUEST_DETIAL_HEADER_DirectLoad1 = "ITEM#     PRODUCT DESCRIPTION                | UNLOADED";

	public final String STOCKRECONCILIATION_LOAD_HEADER_new   = "ITEM# PRODUCT DESCRIPTION       |  UOM  |STK HAND C/U| COUNT C/U|VAR C/U|Amount ";
	
	
	//VEHICLES STOCK
	public final String Vehicules_Stock_Header= "VEHICLE# |  VEHICLE NAME   | ITEM# |     ITEM NAME     | GOOD QTY |DAMAGE QTY";
		
	//Approve Return
	public final String Approve_Return_HEADER = "PROD# PRODUCT DESCRIPTION        |  Box/Unit  |   ABox/AUnit  |  MBox/MUnit";
	
	//RECEIPT
	public final String Receipt_DETIAL_HEADER = "INVOICE#               INVOICE DATE            | AMOUNT";
	public final String Receipt_DETIAL_Cash_Check_HEADER ="CHQ DATE          |       BANK        |     CHEQUE#        |   AMOUNT     ";
	
	//ClientStockCount
	public final String Client_Stock_Count_HEADER = "PROD# PRODUCT DESCRIPTION        |QTYM/QTYS|PRICE M/S|TOTAL AMOUNT|MERCHANDIZE";
	
	//COLLECTION_SUMMARY
	public final String Return_SUMMARY_Header= "Transaction#  | CLIENT#   |     CLIENT NAME     |  TOTAL AMOUNT | ADDRESS ";
    public final String COLLECTION_SUMMARY_Header= "COLLECTION#   | CLIENT#   |     CLIENT NAME     |  TOTAL AMOUNT | ADDRESS ";
    public final String COLLECTION_SUMMARY_CHECK_Header= "COLLECTION#| CLIENT# |     CLIENT NAME   |CHQ DATE  |  BANK  | CHEQUE#|AMOUNT ";
   
    //INVOICE_SUMMARY_Header
    public final String INVOICE_SUMMARY_Header= "Transaction#   | CLIENT#  |     CLIENT NAME      |  TOTAL AMOUNT | ADDRESS ";
    
    //MISSED_ITEM_Header
    public final String MISSED_ITEM_Header= " ITEM#    |     ITEM NAME      |      BOX      |     UNIT      | BASEUNIT ";
	
    //ITEM_DISTRIBUTION_Header
    public final String ITEM_DISTRIBUTION_Header= " ITEM#    |     ITEM NAME      |    Client Name    |     BOX      |  UNIT  ";
  
    //ITEM_Return_Header
    public final String ITEM_Return_Header= " ITEM#    |     ITEM NAME        | Client# |    Client Name    |  BOX   |UNIT  ";
   
    //ITEM_Free_Header
    public final String ITEM_FREE_Header=   " Inv#      | ITEM#  | ITEM NAME  |Client#| ClientName  | BOX | UNIT |Am U|T Am  ";
   
    //Client_Asset_Printing
    public final String Client_Asset_Printing_Header= " ASSET#   |     ASSET NAME     |  STATUS NAME  | EXISTANCE NAME | ITEM NAME ";
 
    // Plan visit report
    public final String Plan_visit_report =  " CLIENT#  |    CLIENT NAME         |      WEEK     |     DAY       | SEQUENCE ";
 
    //jose-print
  	public final String LOADREQUEST_DETIAL_HEADER1 = "ITEM#      PRODUCT DESCRIPTION                | REQUESTED        | AMOUNT";
  	
    public final String GROSS_AMOUNT			= "Gross Amount:";
	public final String DISCOUNT_AMOUNT			= "DiscountAmount:";
	public final String TOTAL_AMOUNT			= "Total Amount:";
	public final String NEWLINE 				= "\r\n";
	public final String TABSPACES 				= "   ";
 
	//LOAD In Summary
 	public final String LOAD_HEADER_In_Summary           = "ITEM# PRODUCT DESCRIPTION      | UPC |LOADOUT|ADDON|OFFLOAD|RETURN|GSALES|Stock";
 	public final String LOAD_HEADER_In_Summary_Movement  = "ITEM# PRODUCT DESCRIPTION      | UPC |LOADREQ|LOAD |OFFLOAD|RETURN|GSALES|Stock";
	
	
 	public final String LocationNumber= " Location# ";
	public final String REFERENCENumber= "                  REFERENCE# ";
	public final String REFERENCENumber1= "  REFERENCE# ";
	private int pageNumber = 1;
	/**
	 * String hosting the formatted content of the print out.
	 */
	private String formatString; 
	
	/**
	 * The application context.
	 */
	private Context context;
	
	/**
	 * Getter - {@link #formatString}
	 * 
	 * @return	String hosting the formatted content of the print out.
	 */
	public String getFormat () {
		return formatString;
	}
	
	//Invoice - Approved Return//RETURN//FreshQQP
	public FormatCreatorMP ( Context context , TransactionHeaders transactionHeaders , int type , int printout ) {
		this.transactionHeader = transactionHeaders;
		this.context = context;
		if(type==Type.INVOICE)
			if( transactionHeader.getPrintingTimes() == null || transactionHeader.getPrintingTimes() == 0 )
			{	
			    transactionHeader.setPrintingTimes( 1 );
				DatabaseUtils.getInstance (  context ).getDaoSession ().getTransactionHeadersDao().update(transactionHeader);
			}
		 	else
		 	{ 
		 		transactionHeader.setPrintingTimes( transactionHeader.getPrintingTimes() + 1);
				DatabaseUtils.getInstance (  context ).getDaoSession ().getTransactionHeadersDao() .update(transactionHeader);
			
		 	}
		if(type==Type.ORDER &&PermissionsUtils.getDisplaySequenceChange ( context , DatabaseUtils.getCurrentUserCode(context) , DatabaseUtils.getCurrentCompanyCode(context ))){
			transactionDetails = (ArrayList<TransactionDetails>) DatabaseUtils.getInstance ( context ).getDaoSession ().getTransactionDetailsDao ().queryBuilder ().orderDesc(TransactionDetailsDao.Properties.OrderedType)
					.where ( TransactionDetailsDao.Properties.TransactionCode.eq ( transactionHeaders.getTransactionCode () ) ).orderAsc(TransactionDetailsDao.Properties.LineNote).list ();

		}else
		
		transactionDetails = (ArrayList<TransactionDetails>) DatabaseUtils.getInstance ( context ).getDaoSession ().getTransactionDetailsDao ().queryBuilder ().orderDesc(TransactionDetailsDao.Properties.OrderedType)
				.where ( TransactionDetailsDao.Properties.TransactionCode.eq ( transactionHeaders.getTransactionCode () ) ).list ();
		client = DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao ().queryBuilder ()
				.where ( ClientsDao.Properties.ClientCode.eq ( transactionHeader.getClientCode () ) ).unique ();
		formatString = Creator ( type , 0 );
	}
	public FormatCreatorMP ( Context context ,ArrayList<SalesRoutingReport> salesRoutingReport, ArrayList<SchedulePercentage> schedulePercentage , int type , int printout ,int t) {
		this.schedulePercentage = schedulePercentage; 
		this.SalesRoutingReport= salesRoutingReport;
		this.context = context;
	 	formatString = Creator ( type , 0 );
	}
	
	//client stock count
	public FormatCreatorMP ( Context context , ClientStockCountHeaders  clientStockCountHeaders , int type , int printout ) {
		this.clientStockCountHeaders = clientStockCountHeaders;
		this.context = context;
		clientStockCountDetails = (ArrayList<ClientStockCountDetails>) DatabaseUtils.getInstance ( context ).getDaoSession ().getClientStockCountDetailsDao ().queryBuilder ()
				.where ( ClientStockCountDetailsDao.Properties.TransactionCode.eq ( clientStockCountHeaders.getTransactionCode () ) ).list ();
		client = DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao ().queryBuilder ()
				.where ( ClientsDao.Properties.ClientCode.eq ( clientStockCountHeaders.getClientCode () ) ).unique ();
		formatString = Creator ( type , 0 );
	}
	
	//VehiclesStock
	public FormatCreatorMP ( Context context , ArrayList < vehiclesStockPrinting> vehiclesStock , int type , int printout ) {
		this.vehiclesStock = vehiclesStock;
		this.context = context;
	     formatString = Creator ( type , 0 );
	}
	
	//Plan visit report
	public FormatCreatorMP ( Context context   , ArrayList < CycleCalls> cycleCalls , int type,int printout ,String a  ) {
		this.cycleCalls = cycleCalls;
		this.context = context;
	     formatString = Creator ( type , 0 );
	}
	
	//COLLECTIONSUMMARY
	public FormatCreatorMP ( Context context , int type , ArrayList <CollectionSummaryPrinting> collectionSummaryHeaderss ,ArrayList <SummaryReturnPrinting> summaryReturnPrinting , int printout ) {
		this.collectionSummaryHeaderss = collectionSummaryHeaderss;
		this.summaryReturnPrintings=summaryReturnPrinting;
		this.context = context;
	     formatString = Creator ( type , 0 );
	}
	
	//loadInSummary--returnSummary--movementSummary
	public FormatCreatorMP ( Context context, ArrayList <LoadInSummaryPrinting> loadInSummaryPrinting  , int type ,int i,int i1  ) {
		this.loadInSummaryPrinting = loadInSummaryPrinting;
		this.context = context;
	     formatString = Creator ( type , 0 );
	}
	
	//COLLECTIONISSETTELMENT
	public FormatCreatorMP (  int type , ArrayList <CollectionHeaders> collectionHeaderss ,Context context , int printout ) {
		this.collectionHeaderss = collectionHeaderss;
		this.context = context;
	     formatString = Creator ( type , 0 );
	}
	
	//INVOICESUMMARY 
	public FormatCreatorMP ( Context context , int type ,   int printout , ArrayList < TransactionHeaders> transactionHeaderss) {
		this.transactionHeaderss = transactionHeaderss;
		this.context = context;
	     formatString = Creator ( type , 0 );
	}
	
	//clientAssetPrinting
	public FormatCreatorMP ( Context context   , ArrayList <ClientAssetPrinting>  clientAssetPrinting, int type) {
		this.clientAssetPrinting = clientAssetPrinting;
		this.context = context;
	     formatString = Creator ( type , 0 );
	}
	
	//missed items
	public FormatCreatorMP ( Context context , int type  , ArrayList <MissedItems>  missedItems) {
		this.missedItems = missedItems;
		this.context = context;
	     formatString = Creator ( type , 0 );
	}
	
	
	//item Distribution
	public FormatCreatorMP ( Context context , ArrayList <Items>  items  , ArrayList <ItemDistrbutionPrinting>  itemDistrbutionPrinting, int type , int printout ) {
		this.items = items;
		this.itemDistrbutionPrinting=itemDistrbutionPrinting;
		this.context = context;
	    formatString = Creator ( type , 0 );
	}
	
	
	//STOCKRECONCILIATION -Load--MOVEMENTS-MOVEMENTLoadRequest-MOVEMENTLoadTransfer
	public FormatCreatorMP ( Context context , MovementHeaders movementHeaders , ArrayList<MovementDetails> details , int type , int printout ) {
		this.movementHeaders = movementHeaders;
		this.context = context;
		//Load--MOVEMENTS -STOCKRECONCILIATION
		if( type == 5 || type == 7 || type == 18 || type == 6  ){
			if(type == 6){
				
				if ( details == null ) {
				 	String SQL=null;
				 	String selectionArguments [] = null;
	     			SQL =   " select MovementDetails.ItemCode ,  items.ItemName ,  QuantityMedium ,   QuantitySmall ," +
	     					" COALESCE(((cast( COALESCE( StockQuantity,0)as integer) - BasicUnitQuantity)	- (((cast( COALESCE( StockQuantity,0)as integer)-BasicUnitQuantity) % items.UnitMediumSmall)))  /UnitMediumSmall,0)  as box," +
	     					" COALESCE((cast( COALESCE( StockQuantity,0)as integer) - BasicUnitQuantity) % items.UnitMediumSmall,0) as Unit , " +
	     					" case UnitMediumSmall when 1 then cast( COALESCE( StockQuantity,0)as integer) else  (cast( COALESCE( StockQuantity,0)as integer) % cast(COALESCE(UnitMediumSmall,1) as integer )) end as StockUnit, " +
	     					" case items.UnitMediumSmall when 1 then 0 else " +
	     					" (COALESCE( StockQuantity,0)  - (cast(COALESCE( StockQuantity,0) as integer ) % cast(UnitMediumSmall as integer  ))) /UnitMediumSmall end as StockBox  " +
	     					" ,StockQuantity , MissedBasicUnitQuantity , UnitMediumSmall,COALESCE(( COALESCE(StockQuantity,0)- COALESCE(BasicUnitQuantity,0) ) * MovementDetails.PriceSmall,0) from MovementDetails " +
	     					"  inner join Items on items.itemcode  =  MovementDetails.itemcode where movementcode = ? ";
	 		 	// Compute the selection arguments
	 				selectionArguments = new String [] {
	 					String.valueOf ( movementHeaders.getMovementCode())  
	 
	 				};
	 				Cursor cursor = DatabaseUtils.getInstance ( context ).getDaoSession ().getDatabase ()
	 						.rawQuery(SQL , selectionArguments) ;
	 				
	 		 
	 				
	 				// Move the cursor to the first raw
	 				if ( cursor.moveToFirst () ) {
	 					// Iterate over all raws
	 					do {
	 						LoadPrinting loadPrinting = new LoadPrinting();
	 						loadPrinting.setItemCode(  cursor.getString(0) );
	 						loadPrinting.setItemName(cursor.getString(1)) ;
	 						loadPrinting.setQuantityMedium(cursor.getDouble(2)) ;
	 						loadPrinting.setQuantitySmall(cursor.getDouble(3)) ;
	 						loadPrinting.setMissedQuantityMedium(cursor.getDouble(4));
	 						loadPrinting.setMissedQuantitySmall(cursor.getDouble(5));
	 						loadPrinting.setStockUnit(cursor.getDouble(6));
	 						loadPrinting.setStockBox(cursor.getDouble(7));
	 						loadPrinting.setStockQuantity(cursor.getDouble(8));
	 						loadPrinting.setMissedBasicUnitQuantity(cursor.getDouble(9));
	 						loadPrinting.setBasicUnitQuantity(cursor.getDouble(10));
	 						loadPrinting.setPriceAmount(cursor.getDouble(11));
	 						loadPrintings.add ( loadPrinting );
	 					} while ( cursor.moveToNext () );
	 				} // End if
	 				// Close the cursor
	 				cursor.close ();
	 				cursor = null;
				}
				else {
					for ( MovementDetails movementDetail : details ) {
						LoadPrinting loadPrinting = new LoadPrinting();
						loadPrinting.setItemCode(  movementDetail.getItemCode () );
						loadPrinting.setItemName( movementDetail.getItemName ()) ;
						loadPrinting.setQuantityMedium( movementDetail.getQuantityMedium ()) ;
						loadPrinting.setQuantitySmall( movementDetail.getQuantitySmall ()) ;
					
						Items item = DatabaseUtils.getInstance ( context ).getDaoSession ().getItemsDao ().queryBuilder ()
								.where ( ItemsDao.Properties.ItemCode.eq ( loadPrinting.getItemCode () ) , ItemsDao.Properties.CompanyCode.eq ( movementHeaders.getCompanyCode () ) ).unique ();
						int mediumSmall = item.getUnitMediumSmall () <= 1 ? 1 : item.getUnitMediumSmall ();
						int stockMedium = ItemsUtils.isMedium ( item ) ? (int) ( ( movementDetail.getStockQuantity () -(movementDetail.getStockQuantity ()% mediumSmall)) / mediumSmall ) : 0;
						//int stockSmall = (int) ( movementDetail.getStockQuantity () - stockMedium * mediumSmall );
						int stockSmall = mediumSmall==1?movementDetail.getStockQuantity ().intValue(): (int) ( movementDetail.getStockQuantity () % mediumSmall );
						
						loadPrinting.setStockUnit((double) stockSmall);
						loadPrinting.setStockBox((double) stockMedium);
						loadPrinting.setStockQuantity(movementDetail.getStockQuantity ());
						loadPrinting.setMissedBasicUnitQuantity(movementDetail.getMissedBasicUnitQuantity ());
						
						
						loadPrinting.setBasicUnitQuantity(  DatabaseUtils.getInstance ( context ).getDaoSession ().getItemsDao ().queryBuilder ()
		 	   			        .where ( ItemsDao.Properties.ItemCode.eq (  movementDetail.getItemCode()   ) ).unique().getUnitMediumSmall().doubleValue());
						loadPrinting.setPriceAmount( (movementDetail.getStockQuantity ()-movementDetail.getBasicUnitQuantity ()) * movementDetail.getPriceSmall());
						int unitMedium=DatabaseUtils.getInstance ( context ).getDaoSession ().getItemsDao ().queryBuilder ()
		 	   			        .where ( ItemsDao.Properties.ItemCode.eq (  movementDetail.getItemCode()   ) ).unique().getUnitMediumSmall();
					
						
						loadPrinting.setMissedQuantityMedium( (
								(movementDetail.getStockQuantity ()-movementDetail.getBasicUnitQuantity ()) - ((movementDetail.getStockQuantity () - movementDetail.getBasicUnitQuantity ())) % unitMedium)/unitMedium);
						loadPrinting.setMissedQuantitySmall( ((movementDetail.getStockQuantity ()-movementDetail.getBasicUnitQuantity ()) % unitMedium));
						
						
						
						
						
						loadPrintings.add ( loadPrinting );
					}
				}
			}
			if(type == 5 || type ==7 || type == 18 ){
			if ( details == null ) {
			 	String SQL=null;
			 	String selectionArguments [] = null;
     		    	SQL =" select MovementDetails.ItemCode , items.ItemName , QuantityMedium , QuantitySmall , MissedQuantityMedium,MissedQuantitySmall ,  " +
     					" case UnitMediumSmall when 1 then COALESCE(StockQuantity,0) else  (cast( COALESCE( StockQuantity,0)as integer) % cast(COALESCE(UnitMediumSmall,1) as integer )) end as StockUnit, " +
     					" case items.UnitMediumSmall when 1 then 0 else  " +
     					" (COALESCE( StockQuantity,0)  - (cast(COALESCE( StockQuantity,0) as integer ) % cast(UnitMediumSmall as integer  ))) /UnitMediumSmall end as StockBox  " +
     					" ,StockQuantity , MissedBasicUnitQuantity , BasicUnitQuantity from MovementDetails " +
     					" inner join Items on items.itemcode  =  MovementDetails.itemcode where movementcode = ? ";
 		 	// Compute the selection arguments
 				selectionArguments = new String [] {
 					String.valueOf ( movementHeaders.getMovementCode())  
 
 				};
 				Cursor cursor = DatabaseUtils.getInstance ( context ).getDaoSession ().getDatabase ()
 						.rawQuery(SQL , selectionArguments) ;
 				
 				// Declare and initialize a list of calendars, used to compute the sales orders dates
 				
 				// Move the cursor to the first raw
 				if ( cursor.moveToFirst () ) {
 					// Iterate over all raws
 					do {
 						LoadPrinting loadPrinting =new LoadPrinting();
 						loadPrinting.setItemCode(  cursor.getString(0) );
 						loadPrinting.setItemName(cursor.getString(1)) ;
 						loadPrinting.setQuantityMedium(cursor.getDouble(2)) ;
 						loadPrinting.setQuantitySmall(cursor.getDouble(3)) ;
 						loadPrinting.setMissedQuantityMedium(cursor.getDouble(4));
 						loadPrinting.setMissedQuantitySmall(cursor.getDouble(5));
 						loadPrinting.setStockUnit(cursor.getDouble(6));
 						loadPrinting.setStockBox(cursor.getDouble(7));
 						loadPrinting.setStockQuantity(cursor.getDouble(8));
 						loadPrinting.setMissedBasicUnitQuantity(cursor.getDouble(9));
 						loadPrinting.setBasicUnitQuantity(cursor.getDouble(10));
 					//	loadPrinting.setTotaLineAmount(cursor.getDouble(11));
 						loadPrintings.add ( loadPrinting );
 					} while ( cursor.moveToNext () );
 				} // End if
 				// Close the cursor
 				cursor.close ();
 				cursor = null;
			}
			else {
				for ( MovementDetails movementDetail : details ) {
					LoadPrinting loadPrinting = new LoadPrinting();
					loadPrinting.setItemCode(  movementDetail.getItemCode () );
					loadPrinting.setItemName( movementDetail.getItemName ()) ;
					loadPrinting.setQuantityMedium(movementDetail.getQuantityMedium ()) ;
					loadPrinting.setQuantitySmall(movementDetail.getQuantitySmall ()) ;
					loadPrinting.setMissedQuantityMedium(movementDetail.getMissedQuantityMedium ());
					loadPrinting.setMissedQuantitySmall(movementDetail.getMissedQuantitySmall ());
					Items item = DatabaseUtils.getInstance ( context ).getDaoSession ().getItemsDao ().queryBuilder ()
							.where ( ItemsDao.Properties.ItemCode.eq ( loadPrinting.getItemCode () ) , ItemsDao.Properties.CompanyCode.eq ( movementHeaders.getCompanyCode () ) ).unique ();
					int mediumSmall = item.getUnitMediumSmall () <= 1 ? 1 : item.getUnitMediumSmall ();
					int stockMedium = ItemsUtils.isMedium ( item ) ? (int) ( ( movementDetail.getStockQuantity () ) / mediumSmall ) : 0;
					int stockSmall = (int) ( movementDetail.getStockQuantity () - stockMedium * mediumSmall );
					loadPrinting.setStockUnit((double) stockSmall);
					loadPrinting.setStockBox((double) stockMedium);
					loadPrinting.setStockQuantity(movementDetail.getStockQuantity ());
					loadPrinting.setMissedBasicUnitQuantity(movementDetail.getMissedBasicUnitQuantity ());
					loadPrinting.setBasicUnitQuantity(movementDetail.getBasicUnitQuantity ());
					loadPrintings.add ( loadPrinting );
				}
			}
		}
			}//load transfer and load req
		if(type==27 ){
			String SQL=null;
		 	String selectionArguments [] = null;
 			SQL ="  select  i.ItemCode,i.ItemName, cast ( case UnitMediumSmall when 1 then 0 else " +
 					"  (COALESCE( md.BasicUnitQuantity,0)  - (cast( COALESCE( md.BasicUnitQuantity,0)as integer) % cast(   UnitMediumSmall as integer))) /UnitMediumSmall end as integer) as Box " +
 					"  ,  cast(case UnitMediumSmall when 1 then COALESCE( md.BasicUnitQuantity,0) else  (cast(COALESCE(  md.BasicUnitQuantity,0)as integer) % cast( COALESCE(UnitMediumSmall,1)as integer)) end as integer ) as Piece " +
 					" ,TotalLineAmount  from MovementDetails md inner join Items i on md.ItemCode=i.ItemCode where MovementCode=? ";
		 	// Compute the selection arguments
				selectionArguments = new String [] {
					String.valueOf ( movementHeaders.getMovementCode())  

				};
				Cursor cursor = DatabaseUtils.getInstance ( context ).getDaoSession ().getDatabase ()
						.rawQuery(SQL , selectionArguments) ;
				
		 
				
				// Move the cursor to the first raw
				if ( cursor.moveToFirst () ) {
					// Iterate over all raws
					do {
						LoadPrinting loadPrinting =new LoadPrinting();
						loadPrinting.setItemCode(  cursor.getString(0) );
						loadPrinting.setItemName(cursor.getString(1)) ;
						loadPrinting.setQuantityMedium( (double) cursor.getInt(2)) ;
						loadPrinting.setQuantitySmall( (double)cursor.getInt(3)) ;
						loadPrinting.setTotaLineAmount(cursor.getDouble(4));
				 
						loadPrintings.add ( loadPrinting );
					} while ( cursor.moveToNext () );
				} // End if
				// Close the cursor
				cursor.close ();
				cursor = null;
		}
		else{
			 movementDetails = (ArrayList<MovementDetails>) DatabaseUtils.getInstance ( context ).getDaoSession ().getMovementDetailsDao().queryBuilder ()//.orderDesc(TransactionDetailsDao.Properties.OrderedType)
			 .where ( MovementDetailsDao.Properties.MovementCode.eq ( movementHeaders.getMovementCode () ) ).list ();
 		     }
		     vehicle   = DatabaseUtils.getInstance ( context ).getDaoSession ().getVehiclesDao().queryBuilder ()
				         .where ( VehiclesDao.Properties.VehicleCode .eq ( movementHeaders.getVehicleCode ()  ),
						 VehiclesDao.Properties.CompanyCode .eq ( movementHeaders.getCompanyCode()  ),
						 VehiclesDao.Properties.DivisionCode .eq ( movementHeaders.getDivisionCode()  )).unique ();
		     formatString = Creator ( type , 0 );
	}



	//receipt
	public FormatCreatorMP ( Context context , CollectionHeaders collectionHeaders , int type , int printout ) {
		this.collectionHeaders = collectionHeaders;
		this.context = context;
		collectionDetails = (ArrayList<CollectionDetails>) DatabaseUtils.getInstance ( context ).getDaoSession ().getCollectionDetailsDao()
						.queryRaw ( " WHERE CollectionCode  IN ( SELECT CollectionCode FROM CollectionHeaders WHERE CollectionCode = ? or ParentCode=? ) " ,
						new String [] { collectionHeaders.getCollectionCode() , collectionHeaders.getCollectionCode() } );
//						.queryBuilder ()
//						.where (CollectionHeadersDao.Properties.CollectionCode.eq ( collectionHeaders.getCollectionCode () ) , CollectionHeadersDao.Properties.ParentCode.eq ( collectionHeaders.getCollectionCode () )  ).unique ())   ).list ();
		switch ( collectionHeaders.getCollectionType() ) {
	case 1:
	case 3:
	Visits	visit = DatabaseUtils.getInstance ( context ).getDaoSession ().getVisitsDao ().queryBuilder ()
			.where ( VisitsDao.Properties.VisitID.eq ( collectionHeaders.getVisitID () ) ).unique ();
		client = DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao ().queryBuilder ()
		.where ( ClientsDao.Properties.ClientCode.eq ( visit.getClientCode () ) ).unique ();

		break;
	case 2:
	
		client = DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao ().queryBuilder ()
		.where ( ClientsDao.Properties.ClientCode.eq ( collectionHeaders.getClientCode () ) ).unique ();

		break;
	}
		
			collectionInvoices = (ArrayList<CollectionInvoices>) DatabaseUtils.getInstance ( context ).getDaoSession ().getCollectionInvoicesDao().queryBuilder () 
 				.where (CollectionInvoicesDao.Properties.CollectionCode.eq ( collectionHeaders.getCollectionCode() ) ).list ();
 		
		formatString = Creator ( type , 0 );
	}
	
	
	public String Creator ( int type , int copyType ) {
		// Retrieve the current user
		Users user = DatabaseUtils.getInstance ( context ).getDaoSession ().getUsersDao ().queryBuilder ()
				.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( context ) ) ,
						UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( context ) ) ).unique ();
		// Retrieve the current date
		Calendar now = Calendar.getInstance ();
		// Declare and initialize string builders used to build the print out
		StringBuilder mainHeader , subHeader, body , footer;
		// Build the main header
		mainHeader = new StringBuilder ();
		mainHeader.append ( NEWLINE );
		mainHeader.append ( NEWLINE );
		mainHeader.append ( NEWLINE );
		mainHeader.append ( NEWLINE );
		mainHeader.append ( NEWLINE );
		mainHeader.append ( NEWLINE );
		mainHeader.append ( NEWLINE );
		mainHeader.append ( NEWLINE );

		// Build the sub header
		subHeader = new StringBuilder ();
		
		if ( type == Type.INVOICE){
			if(user.getUserEmail() != null)
			if(user.getUserType()==11)
				if(user.getCompanyCode().equals("08") || user.getCompanyCode().equals("09")  )
			subHeader.append ("Supervisor Number: "+user.getUserEmail()+NEWLINE);
		}
		if ( type == Type.INVOICE || type == Type.ORDER || type==Type.RETURN ||  type == Type.DirectReturn || type==Type.RETURNCashCredit || type == Type.INVOICE_DISCOUNT) {
			
			subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
			subHeader.append ( " "+ISSUE_DATE +subString ( transactionHeader.getIssueDate ().toString()  , ISSUE_DATE_Length )  + NEWLINE );
			subHeader.append (  NEWLINE);
		}
		SimpleDateFormat sdfa = new SimpleDateFormat("dd/MM/yyyy",  java.util.Locale.getDefault());
//		if ( type == Type.RouteReport ) {
//			 
//		 
//		    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
//		    subHeader.append (  NEWLINE);
//		    subHeader.append (  NEWLINE);
//		    subHeader.append (   "          ROUTE ACTIVITY REPORT"  + NEWLINE);
//			
//		    subHeader.append ( PRINT_DATE + now.getTime ()   + NEWLINE );
//			 subHeader.append ( HORIZANTAL_LINE + NEWLINE );
//	       subHeader.append ( RouteReportHeader + NEWLINE + HORIZANTAL_LINE + NEWLINE );
//		 
//			
//			subHeader.append (  NEWLINE);
//		}
		
	
		
		if(type == Type.FreshQQP){
		     mainHeader = new StringBuilder ();
		     subHeader.append ("                             "+ PRINT_DATE + now.getTime ()  + NEWLINE  + NEWLINE);
			 PaymentTerms paymentTerms= DatabaseUtils.getInstance ( context ).getDaoSession ().getPaymentTermsDao ().queryBuilder ()
	 		 .where ( PaymentTermsDao.Properties.PaymentTermCode.eq ( client.getPaymentTermCode() )  ).unique ();
			 Vehicles	    vehicle   = DatabaseUtils.getInstance ( context ).getDaoSession ().getVehiclesDao().queryBuilder ()
				          .where (  
						  VehiclesDao.Properties.CompanyCode .eq ( transactionHeader.getCompanyCode()  ),
						  VehiclesDao.Properties.DivisionCode .eq ( transactionHeader.getDivisionCode()  )).unique ();	 
			 subHeader.append ("   Invoice: " +subString (transactionHeader.getTransactionCode (),CLIENT_ID_length)+" " );
			 subHeader.append ( paymentTerms==null?" ":subString (paymentTerms.getPaymentTermName(),30) 
							 +" INV DATE:"+ sdfa.format(transactionHeader.getIssueDate ()).toString() 
							 +" Page:1of1" + NEWLINE );
			   subHeader.append ("   Customer: " +subString ( client.getClientCode () ,CLIENT_ID_length)  +" ");
			   subHeader.append ( client==null?" ":subString (client.getClientName ()  ,30) + NEWLINE ); 
			   subHeader.append ( "   Adress: "+ subString (  client.getClientAddress ()  ,CLIENT_ID_length));
			   subHeader.append ( NEWLINE );
			   subHeader.append ( "   whse: ");
			   subHeader.append (  vehicle==null?"":subString (  vehicle.getVehicleCode()  ,CLIENT_ID_length));
	 	       subHeader.append ( " SM: "+ subString ( user.getUserCode() , UserCode_Length ) + " " +subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
	 	  
	  }
 
  
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy",  java.util.Locale.getDefault());
		if (  type==Type.RETURN  ) {
			
			  subHeader.append ( CLIENT_ID +subString ( client.getClientCode () ,CLIENT_ID_length) );
			  subHeader.append ( " "+ subString (client.getClientName ()  ,CLIENT_Name_length) + NEWLINE );
			  subHeader.append (  "                "+   client.getClientAddress ()    + NEWLINE );
	      
			 
	    	    subHeader.append ("             RETURN    ");
	   	        subHeader.append (   transactionHeader.getTransactionCode () + NEWLINE );
	   	        subHeader.append (  NEWLINE);
	   	        subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
	    			
			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
		}
		
		if ( type == Type.INVOICE     || type == Type.INVOICE_DISCOUNT ) {
		
			subHeader.append ( CLIENT_ID +subString ( client.getClientCode () ,CLIENT_ID_length) );
			subHeader.append ( " "+ subString (client.getClientName ()  ,CLIENT_Name_length) + NEWLINE );
			subHeader.append (  "                "+   client.getClientAddress ()    + NEWLINE );
	      
			if( client.getClientType() == 1)
	    	   {
	    	   subHeader.append ("             CASH INVOICE    ");
	   	       subHeader.append (   transactionHeader.getTransactionCode () + NEWLINE );
	   	       }
	       
	       else{ 
	    	   if(transactionHeader.getInfo1() == null && transactionHeader.getInfo2() == null)
	    	       
		    	   subHeader.append ("             CREDIT INVOICE    "); 
		      
		     else if((transactionHeader.getInfo1() .equals( "" ) && transactionHeader.getInfo2() == null))
			      
		    	   subHeader.append ("             CREDIT INVOICE    "); 
		     
		     
		     else if((transactionHeader.getInfo1() .equals( "" ) && transactionHeader.getInfo2().equals( "" )))
		      
		    	   subHeader.append ("             CREDIT INVOICE    "); 
		       
		    
		    // else{
		    	//   if(transactionHeader.getInfo1() != null && transactionHeader.getInfo2() == null )
		    	  // {
		     else  if( transactionHeader.getInfo1().equals( "1" ) || transactionHeader.getInfo2().equals( "1" )  )
		    		   subHeader.append ("             CASH INVOICE    ");
	    	  // }
	    	  // if(transactionHeader.getInfo1() == null && transactionHeader.getInfo2() != null)
	    		//   {
	    		   //if( )
	    		     //    subHeader.append ("             CASH INVOICE    ");
	    		  // }
	   // 	    else
	    //		   subHeader.append ("             CREDIT INVOICE    "); 
	       //}
	       
	       subHeader.append (  transactionHeader.getTransactionCode () + NEWLINE );	
	       
	       }	   
	       subHeader.append (  NEWLINE);
	       subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
 	    			
			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
		}
		if ( type == Type.DirectReturn) {
			subHeader.append ( CLIENT_ID +subString ( client.getClientCode () ,CLIENT_ID_length) );
			subHeader.append ( " "+ subString (client.getClientName ()  ,CLIENT_Name_length) + NEWLINE );
			subHeader.append (  "                "+   client.getClientAddress ()    + NEWLINE );
	       if( client.getClientType() == 1  )
	    	   {
	    	   subHeader.append ("           Cash  Return   ");
	   	       subHeader.append (   transactionHeader.getTransactionCode () + NEWLINE );
	   	       }
	       
	       else{
	    	   
	    	 //  subHeader.append ("          Credit Return    ");
	    	
		    	   if(transactionHeader.getInfo1().equals("1")  )
	    		   subHeader.append ("             CASH Return    ");
	    	   else
	    		   subHeader.append ("             CREDIT Return    ");
	    		
		    	   subHeader.append (  transactionHeader.getTransactionCode () + NEWLINE );		
	    	   }
	       	subHeader.append (  NEWLINE);
	       	subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
 	    	
			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
		}
		if (     type == Type.ORDER   ) {
			subHeader.append ( CLIENT_ID +subString ( client.getClientCode () ,CLIENT_ID_length) );
			subHeader.append ( " "+ subString (client.getClientName ()  ,CLIENT_Name_length) + NEWLINE );
			subHeader.append (  "                "+   client.getClientAddress ()    + NEWLINE );
	        
	    	   subHeader.append ("             Order # :   ");
	   	       subHeader.append (   transactionHeader.getTransactionCode () + NEWLINE );
	   	        
	       
	       
	        
	       subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
 	    	
	
			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
		}
		
		if (     type == Type.RETURNCashCredit   ) {
			subHeader.append ( CLIENT_ID +subString ( client.getClientCode () ,CLIENT_ID_length) );
			subHeader.append ( " "+ subString (client.getClientName ()  ,CLIENT_Name_length) + NEWLINE );
			subHeader.append (  "                "+   client.getClientAddress ()    + NEWLINE );
			   if(client.getClientType()==1)
			    	subHeader.append ("           Cash Return #:    " );
			      else
			    	  subHeader.append ("         Credit Return #:    " );
			
	    	   
	   	       subHeader.append (   transactionHeader.getTransactionCode () + NEWLINE );
	   	        
	       
	       
	        
	       subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
 	    	
	
			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
		}
		if ( type == Type.INVOICE || type == Type.RETURN){
			subHeader.append ( OI_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
		}
		
		if ( type == Type.INVOICE_DISCOUNT ){
			subHeader.append ( OI_DETIAL_HEADER_DS + NEWLINE + HORIZANTAL_LINE + NEWLINE );
		}
		
		if(type==Type.ORDER || type==Type.RETURNCashCredit)
		{
			subHeader.append (Order_OI_DETIAL_HEADER   + NEWLINE + HORIZANTAL_LINE + NEWLINE );
					
		}
		
		
		if ( type == Type.VANTRANSFER  ) {
			subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
			subHeader.append ( " "+dATE +subString ( movementHeaders.getMovementDate().toString()  , ISSUE_DATE_Length )  + NEWLINE );
		 	subHeader.append ( LOAD_NUM + movementHeaders.getMovementCode() + NEWLINE );
		 		
	 
		 
			subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
		 
			subHeader.append ("                  VAN TRANSFER         "+ NEWLINE);
			subHeader.append( NEWLINE);
			subHeader.append(  LocationNumber + subString ( ( movementHeaders.getWarehouseCode () == null ? "" : movementHeaders.getWarehouseCode().toString() ) ,  warehouse_length )
				        	+" " + REFERENCENumber + subString ( movementHeaders.getVehicleCode().toString()  , warehouse_length ) +"  "+  subString ( vehicle.getVehicleName().toString()  , warehouse_length )+ NEWLINE );
					Users  u1 = DatabaseUtils.getInstance ( context ).getDaoSession ().getUsersDao ().queryBuilder ()
								.where ( UsersDao.Properties.UserCode.eq ( movementHeaders.getSource () ) ).list ().get ( 0 );
					Users  u2 = DatabaseUtils.getInstance ( context ).getDaoSession ().getUsersDao ().queryBuilder ()
							.where ( UsersDao.Properties.UserCode.eq ( movementHeaders.getDestination () ) ).list ().get ( 0 );
					 subHeader.append(" SOURCE:"+subString (u1==null?" ":u1.getUserName().toString()  , warehouse_length )
					         +"   DESTINATION:"+ subString (u2==null?" ":u2.getUserName().toString()   , warehouse_length )
					         +"Type:"+(movementHeaders.getMovementType()==1 ? " RECEIVING " : " GIVING ")+ NEWLINE);
			
			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
	    	subHeader.append ( LOAD_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
		}
		
	
		if ( type == Type.LOAD  ) {
			subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
			subHeader.append ( " "+dATE +subString ( movementHeaders.getMovementDate().toString()  , ISSUE_DATE_Length )  + NEWLINE );
		 	subHeader.append ( LOAD_NUM + movementHeaders.getMovementCode() + NEWLINE );
		 		
		 
		 
			subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
		 
			subHeader.append ("             LOAD         "+ NEWLINE);
			subHeader.append ( NEWLINE );
			subHeader.append(  LocationNumber + subString ( ( movementHeaders.getWarehouseCode () == null ? "" : movementHeaders.getWarehouseCode().toString() ) ,  warehouse_length )
				        	+" " + REFERENCENumber + subString ( movementHeaders.getVehicleCode().toString()  , warehouse_length ) +"  "+  subString ( vehicle.getVehicleName().toString()  , warehouse_length )+ NEWLINE );
			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
	    	//subHeader.append ( LOAD_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
			subHeader.append ( STOCKRECONCILIATION_LOAD_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
			
		}
		
		if ( type == Type.UNLOAD  ) {
			subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
			subHeader.append ( " "+dATE +subString ( movementHeaders.getMovementDate().toString()  , ISSUE_DATE_Length )  + NEWLINE );
		 	subHeader.append ( LOAD_NUM + movementHeaders.getMovementCode() + NEWLINE );
		 
		 
			subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
		 
			subHeader.append ("             UNLOAD         "+ NEWLINE);
			subHeader.append ( NEWLINE );
			subHeader.append(  LocationNumber + subString ( movementHeaders.getWarehouseCode()==null?"": movementHeaders.getWarehouseCode().toString()  ,  warehouse_length )
				        	+" " + REFERENCENumber + subString ( movementHeaders.getVehicleCode().toString()  , warehouse_length ) +"  "+  subString ( vehicle.getVehicleName().toString()  , warehouse_length )+ NEWLINE );
			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
	    //	subHeader.append ( LOAD_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
			subHeader.append ( STOCKRECONCILIATION_LOAD_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
			
		}
		
		if ( type == Type.STOCKRECONCILIATION  ) {
			subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
			subHeader.append ( " "+dATE +subString ( movementHeaders.getMovementDate().toString()  , ISSUE_DATE_Length )  + NEWLINE );
		 	subHeader.append ( LOAD_NUM + movementHeaders.getMovementCode() + NEWLINE );
		 		
	 
		 
			subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
		 
			subHeader.append ("                       STOCK RECONCILIATION         "+ NEWLINE);
			subHeader.append ( NEWLINE );
			subHeader.append(  LocationNumber + subString (movementHeaders.getWarehouseCode()==null?"": movementHeaders.getWarehouseCode().toString() ,  warehouse_length ) 
				        	+" " + REFERENCENumber + subString ( movementHeaders.getVehicleCode().toString()  , warehouse_length ) +"  "+ subString ( vehicle.getVehicleName().toString()  , warehouse_length )+NEWLINE );
			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
	    	subHeader.append ( STOCKRECONCILIATION_LOAD_HEADER_new + NEWLINE + HORIZANTAL_LINE + NEWLINE );
		}
	
		
		if ( type == Type.VANMANAGMENT   ) {
			subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
			subHeader.append ( " "+dATE +subString ( movementHeaders.getMovementDate().toString()  , ISSUE_DATE_Length )  + NEWLINE );
		 	subHeader.append ( LOAD_NUM + movementHeaders.getMovementCode() + NEWLINE );
		 		
	 
		 
			subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
		 
			subHeader.append ("                      VAN MANAGMENT        "+ NEWLINE);
			subHeader.append ( NEWLINE );
			subHeader.append(  LocationNumber + subString ( movementHeaders.getWarehouseCode()==null?"": movementHeaders.getWarehouseCode().toString(),  warehouse_length ) 
				       +" " + REFERENCENumber + subString ( movementHeaders.getVehicleCode().toString()  , warehouse_length ) +"  "+ subString ( vehicle.getVehicleName().toString()  , warehouse_length ) + NEWLINE);
			 subHeader.append(" SOURCE "+subString (movementHeaders.getSource()==null?"":movementHeaders.getSource().toString()  , warehouse_length )+"   DESTINATION "+ subString (movementHeaders.getDestination()==null?"":movementHeaders.getDestination().toString()  , warehouse_length )+ NEWLINE);
			subHeader.append ( NEWLINE +HORIZANTAL_LINE + NEWLINE );
	    	subHeader.append ( LOAD_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
		}
		
		if ( type == Type.VANSTOCKCOUNT   ) {
			subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
			subHeader.append ( " "+dATE +subString ( movementHeaders.getMovementDate().toString()  , ISSUE_DATE_Length )  + NEWLINE );
		 	subHeader.append ( LOAD_NUM + movementHeaders.getMovementCode() + NEWLINE );
		 		
		 
		 
			subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
		 
			subHeader.append ("                       VAN STOCK COUNT        "+ NEWLINE);
			subHeader.append ( NEWLINE );
			subHeader.append(  LocationNumber + subString ( movementHeaders.getWarehouseCode()==null?"": movementHeaders.getWarehouseCode().toString()  ,  warehouse_length ) 
				       +" " + REFERENCENumber + subString ( movementHeaders.getVehicleCode().toString()  , warehouse_length ) +"  "+ subString ( vehicle.getVehicleName().toString()  , warehouse_length ) + NEWLINE);
			 subHeader.append(" SOURCE "+subString (movementHeaders.getSource()==null?"":movementHeaders.getSource().toString()  , warehouse_length )+"   DESTINATION "+ subString (movementHeaders.getDestination()==null?"":movementHeaders.getDestination().toString()  , warehouse_length )+ NEWLINE);
				
			subHeader.append (NEWLINE+  HORIZANTAL_LINE + NEWLINE );
	    	subHeader.append ( LOAD_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
		}
		if ( type == Type.MOVEMENTS   ) {
			subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
			subHeader.append ( " "+dATE +subString ( movementHeaders.getMovementDate().toString()  , ISSUE_DATE_Length )  + NEWLINE );
		 	subHeader.append ( LOAD_NUM + movementHeaders.getMovementCode() + NEWLINE );
		 		
		 
		 
			subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
		 
			subHeader.append ("                       MOVEMENTS        "+ NEWLINE);
			subHeader.append ( NEWLINE );
			subHeader.append(  LocationNumber + subString ( movementHeaders.getWarehouseCode()==null?"": movementHeaders.getWarehouseCode().toString() ,  warehouse_length ) 
				       +" " + REFERENCENumber + subString ( movementHeaders.getVehicleCode().toString()  , warehouse_length ) +"  "+ subString ( vehicle.getVehicleName().toString()  , warehouse_length ) + NEWLINE);
			 subHeader.append(" SOURCE "+subString (movementHeaders.getSource()==null?"":movementHeaders.getSource().toString()  , warehouse_length )+"   DESTINATION "+ subString (movementHeaders.getDestination()==null?"":movementHeaders.getDestination().toString()  , warehouse_length )+ NEWLINE);
					subHeader.append ( HORIZANTAL_LINE + NEWLINE );
	    	subHeader.append (LOAD_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
		}
		if ( type == Type.MOVEMENTLoadTransfer || type== Type.MOVEMENTLoadRequest || type==Type.UNLOADREQUEST || type == Type.DirectUnload || type==Type.DirectLoad ) {
			subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
			subHeader.append ( "    "+dATE +subString ( movementHeaders.getMovementDate().toString()  , ISSUE_DATE_Length )  + NEWLINE );
		}
		if ( type == Type.MOVEMENTLoadTransfer  || type== Type.MOVEMENTLoadRequest || type==Type.UNLOADREQUEST || type == Type.DirectUnload || type==Type.DirectLoad)
			{
			subHeader.append ( LOAD_NUM + movementHeaders.getMovementCode() + NEWLINE );
		 		
		 
			}
		
		//MOVEMENTLoadTransfer
		if ( type == Type.MOVEMENTLoadTransfer ){
	 
			subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
		
			subHeader.append ("                       LOAD TRANFER REPORT       "+ NEWLINE);
			subHeader.append ( NEWLINE );
			subHeader.append(  LocationNumber + subString ( movementHeaders.getWarehouseCode()==null?"": movementHeaders.getWarehouseCode().toString() ,  warehouse_length )
				        	 + REFERENCENumber + subString ( movementHeaders.getMovementReferenceCode().toString()  , warehouse_length )+NEWLINE);
			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
	    	subHeader.append ( LOAD_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
		}
		
		if ( type == Type.MOVEMENTLoadRequest ){
		 
			subHeader.append ("            LOAD REQUEST REPORT       "+ NEWLINE);
		 	subHeader.append(  REFERENCENumber1 + subString ( movementHeaders.getVehicleCode().toString()  , warehouse_length ) +"  "+  subString ( vehicle.getVehicleName().toString()  , warehouse_length )+ NEWLINE );
			subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
	    	subHeader.append ( LOADREQUEST_DETIAL_HEADER1 + NEWLINE + HORIZANTAL_LINE + NEWLINE );
		}
		if ( type == Type.DirectLoad ){
			 
			subHeader.append ("            LOAD REPORT       "+ NEWLINE);
		 	subHeader.append(  REFERENCENumber1 + subString ( movementHeaders.getVehicleCode().toString()  , warehouse_length ) +"  "+  subString ( vehicle.getVehicleName().toString()  , warehouse_length )+ NEWLINE );
			subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
	    	subHeader.append ( LOADREQUEST_DETIAL_HEADER_DirectLoad + NEWLINE + HORIZANTAL_LINE + NEWLINE );
		}
		if ( type == Type.UNLOADREQUEST ){
		 	subHeader.append ("            UNLOAD REQUEST REPORT       "+ NEWLINE);
		 	subHeader.append(  REFERENCENumber1 + subString ( movementHeaders.getVehicleCode().toString()  , warehouse_length ) +"  "+  subString ( vehicle.getVehicleName().toString()  , warehouse_length )+ NEWLINE );
			subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
	    	subHeader.append ( LOADREQUEST_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
		}
		 
				if ( type == Type.DirectUnload ){
				 	subHeader.append ("            UNLOAD REPORT       "+ NEWLINE);
				 	subHeader.append(  REFERENCENumber1 + subString ( movementHeaders.getVehicleCode().toString()  , warehouse_length ) +"  "+  subString ( vehicle.getVehicleName().toString()  , warehouse_length )+ NEWLINE );
					subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
					subHeader.append ( HORIZANTAL_LINE + NEWLINE );
			    	subHeader.append ( LOADREQUEST_DETIAL_HEADER_DirectLoad1 + NEWLINE + HORIZANTAL_LINE + NEWLINE );
				}
		//RECEIPT
		if ( type == Type.RECEIPT   ) {
			subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
			subHeader.append ( " "+dATE +subString ( collectionHeaders.getCollectionDate().toString()  , ISSUE_DATE_Length )  + NEWLINE );
		}
		if ( type == Type.RECEIPT )
			{
			subHeader.append ( LOAD_NUM + collectionHeaders.getCollectionCode() + NEWLINE );
		 		
		 
			}
		if ( type == Type.RECEIPT ){
	 
			subHeader.append ( CLIENT_ID +subString ( client.getClientCode () ,CLIENT_ID_length) );
			subHeader.append ( " "+ subString (client.getClientName ()  ,CLIENT_Name_length) + NEWLINE );
			subHeader.append (  "                "+   client.getClientAddress ()    + NEWLINE );
			subHeader.append (NEWLINE);
			
			subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
		
			subHeader.append ("                              RECEIPT "+ NEWLINE);
			
//			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
//	    	subHeader.append ( Receipt_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
		}
		
		if ( type == Type.VEHICULESSTOCK ){
			subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
			 
     		subHeader.append ("            VEHICULES STOCK       "+ NEWLINE);
			subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
	    	subHeader.append ( Vehicules_Stock_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
		}	
		
		if ( type == Type.CLIENTASSET ){
			subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
			 
     		subHeader.append ("                 CLIENT ASSET STATUS       "+ NEWLINE);
			subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
	    	subHeader.append ( Client_Asset_Printing_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
		}	
		
		if( type==Type.APPROVERETURN){
			subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
			subHeader.append ( " "+ISSUE_DATE +subString ( transactionHeader.getIssueDate ().toString()  , ISSUE_DATE_Length )  + NEWLINE );
			subHeader.append ( ORDER_NUM + transactionHeader.getTransactionCode () + NEWLINE );	
	 
        	subHeader.append ( CLIENT_ID +subString ( client.getClientCode () ,CLIENT_ID_length) );
	    	subHeader.append ( " "+ subString (client.getClientName ()  ,CLIENT_Name_length) + NEWLINE );
	     	subHeader.append (  "                "+   client.getClientAddress ()    + NEWLINE );
	        subHeader.append ( PRINT_DATE + now.getTime ()   + NEWLINE );
	       	subHeader.append ( HORIZANTAL_LINE + NEWLINE );
			subHeader.append ( Approve_Return_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
		 }
		
		if( type==Type.CLIENTSTOCKCOUNT){
			subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
			subHeader.append ( " "+ISSUE_DATE +subString ( clientStockCountHeaders.getDate().toString()  , ISSUE_DATE_Length )  + NEWLINE );
			subHeader.append ( LOAD_NUM + clientStockCountHeaders.getTransactionCode () + NEWLINE );	
	
		 
        	subHeader.append ( CLIENT_ID +subString ( client.getClientCode () ,CLIENT_ID_length) );
	    	subHeader.append ( " "+ subString (client.getClientName ()  ,CLIENT_Name_length) + NEWLINE );
	     	subHeader.append (  "                "+   client.getClientAddress ()    + NEWLINE );
	        subHeader.append ( PRINT_DATE + now.getTime ()   + NEWLINE );
	        subHeader.append ( NEWLINE );
	        subHeader.append ("TYPE: " +(clientStockCountHeaders.getCountType()==1 ?"BY SHELF":"STOCK"));
	       	subHeader.append ( HORIZANTAL_LINE + NEWLINE );
			subHeader.append ( Client_Stock_Count_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
		 }
		
		if(type==Type.loadInSummary){
			subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
		  	subHeader.append ("            LOAD IN SUMMARY       "+ NEWLINE);
			subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
			subHeader.append ( LOAD_HEADER_In_Summary  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
			}
		if(type==Type.MovementInSummary){
			subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
		  	subHeader.append ("            MOVEMENT SUMMARY       "+ NEWLINE);
			subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
			subHeader.append ( LOAD_HEADER_In_Summary_Movement  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
			}
		if(type==Type.SalesReturnSummeryReport){
			subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
		  	subHeader.append ("           Sales Return Summary Report       "+ NEWLINE);
			subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
			subHeader.append ( ITEM_Return_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
			}
			if(type==Type.FreeSummeryReport){
				subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
			  	subHeader.append ("           FOC Summary Report       "+ NEWLINE);
				subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
				subHeader.append ( HORIZANTAL_LINE + NEWLINE );
				subHeader.append ( ITEM_FREE_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
				}
		if ( type == Type.COLLECTIONSUMMARY ){
			subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
		  	subHeader.append ("            COLLECTION SUMMARY       "+ NEWLINE);
			subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
	    //	subHeader.append ( COLLECTION_SUMMARY_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
	    	}
		if ( type == Type.COLLECTIONISSETTEL ){
			subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
		  	subHeader.append ("            COLLECTION SETTEL       "+ NEWLINE);
			subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
	    	subHeader.append ( COLLECTION_SUMMARY_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
	    	}
		
		if ( type == Type.INVOICESUMMARY ){
     			  ArrayList< TransactionHeaders>  transactionCredit=new ArrayList<TransactionHeaders>();
 	    	      ArrayList< TransactionHeaders>  transactionCash=new ArrayList<TransactionHeaders>(); 
			for( int i = 0 ; i < transactionHeaderss.size () ; i ++ ) {
			    	 TransactionHeaders    tr = transactionHeaderss.get ( i );
	 	     			if(tr.getRemainingAmount() > 0 )
	 	     				transactionCredit.add(tr);
	 	     			else
	 	     				transactionCash.add(tr);
	 	     				
	 	     		}
			
			subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
			subHeader.append ("                                     INVOICE SUMMARY       "+ NEWLINE);
			subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
			
		}

		
		
		if(type==Type.MISSEDITEMS){
		        subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
			 	subHeader.append ("               MISSED ITEMS       "+ NEWLINE);
				subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
				subHeader.append ( HORIZANTAL_LINE + NEWLINE );
		    	subHeader.append (MISSED_ITEM_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
		
		}
		
		if(type==Type.PLANVISITSREPORT){
	        subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
		 	subHeader.append ("               Plan visit report       "+ NEWLINE);
			subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
	    	subHeader.append (Plan_visit_report  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
		}
		
		if(type==Type.ITEMDISTRIBUTION){
	        subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
		 	subHeader.append ("               ITEM DISTRIBUTION       "+ NEWLINE);
			subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
	    	subHeader.append (ITEM_DISTRIBUTION_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
	
	     }  	 
			// Build a decimal format used to properly display monetary values
			//DecimalFormat moneyFormat = new DecimalFormat ( "#,##0.000" );
		      DecimalFormat moneyFormat = new DecimalFormat ( "#,##0.00" );
		//	moneyFormat.setRoundingMode(RoundingMode.UP);
			// Build the body
			body = new StringBuilder ();
			footer = new StringBuilder ();
		
			int count=0;
			double quantityS=0d;
			double quantityM=0d;
			double totalSales=0d;
		
			if (   type == Type.ORDER ) {
				for( int i = 0 ; i < transactionDetails.size () ; i ++ ) {
					TransactionDetails tr = transactionDetails.get ( i );
				//	body.append ( subString ( tr.getItemName () , TOTAL_LENGTH ) );
					List < ItemBarcodes > itemBarcodes = DatabaseUtils.getInstance ( context ).getDaoSession ().getItemBarcodesDao ().queryBuilder ()
							.where ( ItemBarcodesDao.Properties.ItemCode.eq ( tr.getItemCode () ) ).list ();
					if ( ! itemBarcodes.isEmpty () ) {
						String barcodeOne = BARCODE + String.format ( "%-" + BARCODE_LENGTH + "s" , subString ( itemBarcodes.get ( 0 ).getItemBarcode () , BARCODE_LENGTH ) );
						if ( itemBarcodes.size () > 1 ) {
							String barcodeTwo = BARCODE + String.format ( "%" + BARCODE_LENGTH + "s" , subString ( itemBarcodes.get ( 1 ).getItemBarcode () , BARCODE_LENGTH ) );
							int remainingWhiteSpace = TOTAL_LENGTH - barcodeOne.length () - barcodeTwo.length ();
							for ( int j = remainingWhiteSpace ; j > 0 ; j -- )
								barcodeOne += " ";
							barcodeOne += barcodeTwo;
						}
						body.append (  barcodeOne+NEWLINE );
						
					     
						if(count % 36 == 0 && count != 0    )  
						{    
			     			body.append ( NEWLINE );
			     		   	body.append ("                      Continued   "+ NEWLINE);
						
					   		body.append ( NEWLINE );
							body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    subHeader = new StringBuilder ();
					    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
					    
					    subHeader.append ( " "+ISSUE_DATE +subString ( transactionHeader.getIssueDate ().toString()  , ISSUE_DATE_Length )  + NEWLINE );
					    subHeader.append (  NEWLINE);
						 	
						pageNumber = pageNumber +  1;
						// body.append ( pageNumber );
						  
					        	subHeader.append ( CLIENT_ID +subString ( client.getClientCode () ,CLIENT_ID_length) );
						    	subHeader.append ( " "+ subString (client.getClientName ()  ,CLIENT_Name_length) + NEWLINE );
						     	subHeader.append (  "                "+   client.getClientAddress ()    + NEWLINE );
						      
						    	subHeader.append ("             Order #:    " );
						    	subHeader.append (   transactionHeader.getTransactionCode () + NEWLINE );	
						       
						     	subHeader.append ( PRINT_DATE + now.getTime ()   + NEWLINE );
						       	subHeader.append ( HORIZANTAL_LINE + NEWLINE );
								subHeader.append ( Order_OI_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
							    body.append(subHeader);
					 
					}
			     		count = count+1;
					}
				 
				
			         		
						//String itemCode = "IC  : " + tr.getItemCode ();
						//itemCode = itemCode.length () > CODE_LENGTH ? itemCode.substring ( 0 , CODE_LENGTH ) : itemCode;
						List < ClientItemCodes > clientItems = DatabaseUtils.getInstance ( context ).getDaoSession ().getClientItemCodesDao ().queryBuilder ()
								.where ( ClientItemCodesDao.Properties.ItemCode.eq ( tr.getItemCode () ) ).list ();
						String clientItemCode = "";
						if ( ! clientItems.isEmpty () ) {
							clientItemCode = clientItems.get ( 0 ).getClientItemCode ();
							clientItemCode = clientItemCode.length () > CODE_LENGTH ? clientItemCode.substring ( 0 , CODE_LENGTH ) : clientItemCode;
						}
						
						body.append ( String.format ( "%-"+(CODE_LENGTH-2)+"s %"+(TOTAL_LENGTH+2)+"s %"+(uom_LENGTH + 4) +"s %"+ (QUANTITY_LENGTH -2)+ "s %"+price_LENGTH+"s%" + UNIT_PRICE_LENGTH + "s %" + (LINE_AMOUNT_LENGTH - 2) + "s" , 
		 	   				    tr.getItemCode ().length () > CODE_LENGTH-2 ? tr.getItemCode ().substring ( 0 , CODE_LENGTH -2 ) : tr.getItemCode ()	
		 	   				    , tr.getItemName ().length () >  (TOTAL_LENGTH + 2) ? tr.getItemName ().substring ( 0 , (TOTAL_LENGTH+2)  ) : tr.getItemName (),	
		 	   				    clientItemCode	,	   tr.getQuantityMedium().intValue()+"/" +  tr.getQuantitySmall().intValue()  ,
		 	   				    moneyFormat.format ( tr.getPriceMedium() ),	moneyFormat.format ( tr.getPriceSmall () ) , moneyFormat.format ( tr.getTotalLineAmount () ) ) + NEWLINE );

						
//						body.append ( String.format ( "%-" + CODE_LENGTH + "s %" + QUANTITY_LENGTH + "s %" + UNIT_PRICE_LENGTH + "s %" + LINE_AMOUNT_LENGTH + "s" , 
//								itemCode , tr.getQuantityMedium ().toString () + " /C",
//								moneyFormat.format ( tr.getPriceMedium () ) , "" ) + NEWLINE );
//						body.append ( String.format ( "%-" + CODE_LENGTH + "s %" + QUANTITY_LENGTH + "s %" + UNIT_PRICE_LENGTH + "s %" + LINE_AMOUNT_LENGTH + "s" , 
//								clientItemCode , tr.getQuantitySmall ().toString () + " /U",
//								moneyFormat.format ( tr.getPriceSmall () ) , moneyFormat.format ( tr.getTotalLineAmount () ) ) + NEWLINE );
					 
					 quantityM = quantityM + tr.getQuantityMedium();
					 quantityS = quantityS + tr.getQuantitySmall();
					 totalSales = totalSales + tr.getTotalLineAmount ();
				     
					 if(count % 36 == 0 && count != 0    )  
						{    
			     			body.append ( NEWLINE );
			     		   	body.append ("                      Continued   "+ NEWLINE);
						
					   		body.append ( NEWLINE );
							body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    subHeader = new StringBuilder ();
					    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
					    
					    subHeader.append ( " "+ISSUE_DATE +subString ( transactionHeader.getIssueDate ().toString()  , ISSUE_DATE_Length )  + NEWLINE );
					    subHeader.append (  NEWLINE);
						 	
						pageNumber = pageNumber +  1;
						// body.append ( pageNumber );
						  
					        	subHeader.append ( CLIENT_ID +subString ( client.getClientCode () ,CLIENT_ID_length) );
						    	subHeader.append ( " "+ subString (client.getClientName ()  ,CLIENT_Name_length) + NEWLINE );
						     	subHeader.append (  "                "+   client.getClientAddress ()    + NEWLINE );
						      
						    	subHeader.append ("             Order #:    " );
						    	subHeader.append (   transactionHeader.getTransactionCode () + NEWLINE );	
						       
						     	subHeader.append ( PRINT_DATE + now.getTime ()   + NEWLINE );
						       	subHeader.append ( HORIZANTAL_LINE + NEWLINE );
								subHeader.append ( Order_OI_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
							    body.append(subHeader);
					 
					}
				         		
			     		count = count + 1;		
//					if ( i != transactionDetails.size () - 1 )
//						body.append ( NEWLINE );
				} // End for loop
				  body.append ( HORIZANTAL_LINE + NEWLINE );
				 
        		  body.append ( "                            SUB - TOTAL SALES  :"+(int)quantityM  +"/"+(int)quantityS  +"            "+ moneyFormat.format (totalSales)+NEWLINE );
        		    count = count + 2;
					int lastPage1 = count / 36;
					int rest1 = count % 36; 
					
					body.append ( GROSS_AMOUNT + moneyFormat.format ( transactionHeader.getGrossAmount () ) + " " + transactionHeader.getCurrencyCode () +" " );
					body.append ( DISCOUNT_AMOUNT + moneyFormat.format ( transactionHeader.getDiscountAmount () ) + " " + transactionHeader.getCurrencyCode () + " "   );
					body.append ( TOTAL_AMOUNT + moneyFormat.format ( transactionHeader.getTotalTaxAmount() ) + " " + transactionHeader.getCurrencyCode () + NEWLINE );
					count=count+3;
					if(pageNumber == 1)
						if( ( 36 -( count + 6) ) > 0)
		 			{	  for(int u = 0 ; u < 36 -( count + 6)   ;u++)
		 				body.append ( NEWLINE );
		 			}
					 if(pageNumber == ( lastPage1 + 1 )   )
				      {
					if(rest1>4)
		    	     for(int u=0 ; u < 36  -  rest1 - 3 ; u++)
		    	    	 body.append ( NEWLINE );
				      }
					 body.append(  HORIZANTAL_LINE + NEWLINE);
					 body.append ( "SALES REP                  Last Page                        CUSTOMER SIGN/STAMP" );
				 

					
		 
			} // End if

			
			
			if (   type == Type.RETURNCashCredit ) {
				for( int i = 0 ; i < transactionDetails.size () ; i ++ ) {
					TransactionDetails tr = transactionDetails.get ( i );
				//	body.append ( subString ( tr.getItemName () , TOTAL_LENGTH ) );
					List < ItemBarcodes > itemBarcodes = DatabaseUtils.getInstance ( context ).getDaoSession ().getItemBarcodesDao ().queryBuilder ()
							.where ( ItemBarcodesDao.Properties.ItemCode.eq ( tr.getItemCode () ) ).list ();
					if ( ! itemBarcodes.isEmpty () ) {
						String barcodeOne = BARCODE + String.format ( "%-" + BARCODE_LENGTH + "s" , subString ( itemBarcodes.get ( 0 ).getItemBarcode () , BARCODE_LENGTH ) );
						if ( itemBarcodes.size () > 1 ) {
							String barcodeTwo = BARCODE + String.format ( "%" + BARCODE_LENGTH + "s" , subString ( itemBarcodes.get ( 1 ).getItemBarcode () , BARCODE_LENGTH ) );
							int remainingWhiteSpace = TOTAL_LENGTH - barcodeOne.length () - barcodeTwo.length ();
							for ( int j = remainingWhiteSpace ; j > 0 ; j -- )
								barcodeOne += " ";
							barcodeOne += barcodeTwo;
						}
						body.append (  barcodeOne+NEWLINE );
						
					     
			     		if(count % 38 == 0 && count != 0    )  
				    	{  
							body.append ( NEWLINE );
						   	body.append ("                      Continued   "+ NEWLINE);
							body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    subHeader = new StringBuilder ();
					    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
					    
					    subHeader.append ( " "+ISSUE_DATE +subString ( transactionHeader.getIssueDate ().toString()  , ISSUE_DATE_Length )  + NEWLINE );
					    subHeader.append (  NEWLINE);
						 	
						pageNumber = pageNumber +  1;
						// body.append ( pageNumber );
						  
					        	subHeader.append ( CLIENT_ID +subString ( client.getClientCode () ,CLIENT_ID_length) );
						    	subHeader.append ( " "+ subString (client.getClientName ()  ,CLIENT_Name_length) + NEWLINE );
						     	subHeader.append (  "                "+   client.getClientAddress ()    + NEWLINE );
						      if(client.getClientType()==1)
						    	subHeader.append ("           Cash Return #:    " );
						      else
						    	  subHeader.append ("         Credit Return #:    " );
						    	subHeader.append (   transactionHeader.getTransactionCode () + NEWLINE );	
						       
						     	subHeader.append ( PRINT_DATE + now.getTime ()   + NEWLINE );
						       	subHeader.append ( HORIZANTAL_LINE + NEWLINE );
								subHeader.append ( Order_OI_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
							    body.append(subHeader);
					 
					}
			     		count = count+1;
					}
				 
				
			         		
				 	List < ClientItemCodes > clientItems = DatabaseUtils.getInstance ( context ).getDaoSession ().getClientItemCodesDao ().queryBuilder ()
								.where ( ClientItemCodesDao.Properties.ItemCode.eq ( tr.getItemCode () ) ).list ();
						String clientItemCode = "";
						if ( ! clientItems.isEmpty () ) {
							clientItemCode = clientItems.get ( 0 ).getClientItemCode ();
							clientItemCode = clientItemCode.length () > CODE_LENGTH ? clientItemCode.substring ( 0 , CODE_LENGTH ) : clientItemCode;
						}
						
						body.append ( String.format ( "%-"+(CODE_LENGTH-2)+"s %"+(TOTAL_LENGTH+2)+"s %"+(uom_LENGTH + 4) +"s %"+ (QUANTITY_LENGTH -2)+ "s %"+price_LENGTH+"s%" + UNIT_PRICE_LENGTH + "s %" + (LINE_AMOUNT_LENGTH - 2) + "s" , 
		 	   				    tr.getItemCode ().length () > CODE_LENGTH-2 ? tr.getItemCode ().substring ( 0 , CODE_LENGTH -2 ) : tr.getItemCode ()	
		 	   				    , tr.getItemName ().length () >  (TOTAL_LENGTH + 2) ? tr.getItemName ().substring ( 0 , (TOTAL_LENGTH+2)  ) : tr.getItemName (),	
		 	   				    clientItemCode	,	   tr.getQuantityMedium().intValue()+"/" +  tr.getQuantitySmall().intValue()  ,
		 	   				    moneyFormat.format ( tr.getPriceMedium() ),	moneyFormat.format ( tr.getPriceSmall () ) , moneyFormat.format ( tr.getTotalLineAmount () ) ) + NEWLINE );

 				 
					 quantityM = quantityM + tr.getQuantityMedium();
					 quantityS = quantityS + tr.getQuantitySmall();
					 totalSales = totalSales + tr.getTotalLineAmount ();
				     
			     		if(count % 38 == 0 && count != 0    )  
				    	{  
							body.append ( NEWLINE );
						   	body.append ("                      Continued   "+ NEWLINE);
							body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    subHeader = new StringBuilder ();
					    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
					    
					    subHeader.append ( " "+ISSUE_DATE +subString ( transactionHeader.getIssueDate ().toString()  , ISSUE_DATE_Length )  + NEWLINE );
					    subHeader.append (  NEWLINE);
						 	
						pageNumber = pageNumber +  1;
						// body.append ( pageNumber );
						  
					        	subHeader.append ( CLIENT_ID +subString ( client.getClientCode () ,CLIENT_ID_length) );
						    	subHeader.append ( " "+ subString (client.getClientName ()  ,CLIENT_Name_length) + NEWLINE );
						     	subHeader.append (  "                "+   client.getClientAddress ()    + NEWLINE );
						      
						        if(client.getClientType()==1)
							    	subHeader.append ("           Cash Return #:    " );
							      else
							    	  subHeader.append ("         Credit Return #:    " );
							
						    	subHeader.append (   transactionHeader.getTransactionCode () + NEWLINE );	
						       
						     	subHeader.append ( PRINT_DATE + now.getTime ()   + NEWLINE );
						       	subHeader.append ( HORIZANTAL_LINE + NEWLINE );
								subHeader.append ( Order_OI_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
							    body.append(subHeader);
					 
					}
				         		
			     		count = count + 1;		
//					if ( i != transactionDetails.size () - 1 )
//						body.append ( NEWLINE );
				} // End for loop
				  body.append ( HORIZANTAL_LINE + NEWLINE );
				 
        		  body.append ( "                            SUB - TOTAL SALES  :"+(int)quantityM  +"/"+(int)quantityS  +"            "+ moneyFormat.format (totalSales)+NEWLINE );
        		    count = count + 2;
					int lastPage1 = count / 38;
					int rest1 = count % 38; 
					
					body.append ( GROSS_AMOUNT + moneyFormat.format ( transactionHeader.getGrossAmount () ) + " " + transactionHeader.getCurrencyCode () +" " );
					body.append ( DISCOUNT_AMOUNT + moneyFormat.format ( transactionHeader.getDiscountAmount () ) + " " + transactionHeader.getCurrencyCode () + " "   );
					body.append ( TOTAL_AMOUNT + moneyFormat.format ( transactionHeader.getTotalTaxAmount() ) + " " + transactionHeader.getCurrencyCode () + NEWLINE );
					if(pageNumber == 1)
						if(( 38 - count - 3 )>0)
		 			{	  for(int u = 0 ; u < 36 - count - 3  ;u++)
		 				body.append ( NEWLINE );
		 			}
					 if(pageNumber == ( lastPage1 + 1 )   )
				      {
					if(rest1>4)
		    	     for(int u=0 ; u < 38 -  rest1 - 3 ; u++)
		    	    	 body.append ( NEWLINE );
				      }
					 body.append(  HORIZANTAL_LINE + NEWLINE);
					 body.append ( "SALES REP                  Last Page                        CUSTOMER SIGN/STAMP" );
				 

					
		 
			} // End if
			
			
			
			
//		 
//		 if ( type == Type.ORDER   ) 
//		     {
//			  ArrayList< TransactionDetails>  transactionDetailsO=new ArrayList<TransactionDetails>();
//		      ArrayList< TransactionDetails>  transactionDetailsF=new ArrayList<TransactionDetails>();
//			
//	 	     	for( int i = 0 ; i < transactionDetails.size () ; i ++ ) {
//			
//	 	     		
//	 	     			TransactionDetails tr = transactionDetails.get ( i );
//	 	     			if(tr.getOrderedType().equals("O") )
//	 	     				transactionDetailsO.add(tr);
//	 	     			else
//	 	     				transactionDetailsF.add(tr);
//	 	     				
//	 	     		}
//	 	     		
//	 	     		for( int i = 0 ; i < transactionDetailsO.size () ; i ++ ) {
//	 	   			 
//		 	     			TransactionDetails tr = transactionDetailsO.get ( i ); 
//		 	      	 	
//		 	     			body.append ( String.format ( "%-"+(CODE_LENGTH-2)+"s %"+(TOTAL_LENGTH+7)+"s %"+(uom_LENGTH-1) +"s %"+ (QUANTITY_LENGTH -2)+ "s %"+price_LENGTH+"s%" + UNIT_PRICE_LENGTH + "s %" + (LINE_AMOUNT_LENGTH - 2) + "s" , 
//		 	   				    tr.getItemCode ().length () > CODE_LENGTH ? tr.getItemCode ().substring ( 0 , CODE_LENGTH ) : tr.getItemCode ()	
//		 	   				    , tr.getItemName ().length () >  (TOTAL_LENGTH+7) ? tr.getItemName ().substring ( 0 , (TOTAL_LENGTH+7)  ) : tr.getItemName (),	
//		 	   				    DatabaseUtils.getInstance ( context ).getDaoSession ().getItemsDao ().queryBuilder ()
//		 	   			        .where ( ItemsDao.Properties.ItemCode.eq (  tr.getItemCode()   ) ).unique().getUnitMediumSmall()	,	   tr.getQuantityMedium().intValue()+"/" +  tr.getQuantitySmall().intValue()  ,
//		 	   				    moneyFormat.format ( tr.getPriceMedium() ),	moneyFormat.format ( tr.getPriceSmall () ) , moneyFormat.format ( tr.getTotalLineAmount () ) ) + NEWLINE );
//
//			 
//			 quantityM= quantityM+tr.getQuantityMedium();
//			 quantityS=quantityS+tr.getQuantitySmall();
//			 totalSales=totalSales+tr.getTotalLineAmount ();
//		
//			if(count % 36 == 0 && count != 0    )  
//			{  
//				
//			   	body.append ( NEWLINE );
//				body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    
//			    subHeader = new StringBuilder ();
//			    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
//			    
//			    subHeader.append ( " "+ISSUE_DATE +subString ( transactionHeader.getIssueDate ().toString()  , ISSUE_DATE_Length )  + NEWLINE );
//			    subHeader.append (  NEWLINE);
//				 	
//				pageNumber =pageNumber+  1;
//				// body.append ( pageNumber );
//				  
//			        	subHeader.append ( CLIENT_ID +subString ( client.getClientCode () ,CLIENT_ID_length) );
//				    	subHeader.append ( " "+ subString (client.getClientName ()  ,CLIENT_Name_length) + NEWLINE );
//				     	subHeader.append (  "                "+   client.getClientAddress ()    + NEWLINE );
//				     
//				  	  if(client.getClientType()==1){
//				    	   subHeader.append ("             CASH INVOICE    " );
//				    	   subHeader.append (   transactionHeader.getTransactionCode () + NEWLINE );	
//				    	   }
//				       else 
//				    	   {
//				    	   subHeader.append ("             CREDIT INVOICE    "); 
//				    	   subHeader.append (   transactionHeader.getTransactionCode () + NEWLINE );	
//				    	   }
//				     	subHeader.append (  NEWLINE);
//				     	subHeader.append ( PRINT_DATE + now.getTime ()   + NEWLINE );
//				       	subHeader.append ( HORIZANTAL_LINE + NEWLINE );
//						subHeader.append ( OI_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
//					    body.append(subHeader);
//			 
//			}
//		         		 count=count+1;
//		} // End for loop
//	  
//	 	     		 body.append ( HORIZANTAL_LINE + NEWLINE );
//	        		 body.append ( "                            SUB - TOTAL SALES  :"+(int)quantityM  +"/"+(int)quantityS  +"            "+ moneyFormat.format (totalSales)+NEWLINE );
//	        	 	
//		  
//		
//		if(transactionDetailsF.size() > 0){
//			 quantityM = 0d;
//	         quantityS = 0d;
//	         totalSales= 0d;
//		
//		body.append (  HORIZANTAL_LINE + NEWLINE);
//		body.append ( "                       FREE GOODS"+ NEWLINE);	
//		count=count+6;
//		 
//		for( int i = 0 ; i < transactionDetailsF.size () ; i ++ ) {
//	   			
// 			TransactionDetails tr = transactionDetailsF.get ( i ); 
//  	 	
// 			body.append ( String.format ( "%-"+(CODE_LENGTH-2)+"s %"+(TOTAL_LENGTH+7)+"s %"+(uom_LENGTH-1) +"s %"+ (QUANTITY_LENGTH -2)+ "s %"+price_LENGTH+"s%" + UNIT_PRICE_LENGTH + "s %" + (LINE_AMOUNT_LENGTH - 2) + "s" , 
//	   				    tr.getItemCode ().length () > CODE_LENGTH ? tr.getItemCode ().substring ( 0 , CODE_LENGTH ) : tr.getItemCode ()	
//	   				    , tr.getItemName ().length () >  (TOTAL_LENGTH+7) ? tr.getItemName ().substring ( 0 , (TOTAL_LENGTH+7)  ) : tr.getItemName (),	
//	   				    DatabaseUtils.getInstance ( context ).getDaoSession ().getItemsDao ().queryBuilder ()
//	   			        .where ( ItemsDao.Properties.ItemCode.eq (  tr.getItemCode()   ) ).unique().getUnitMediumSmall()	,	   tr.getQuantityMedium().intValue()+"/" +  tr.getQuantitySmall().intValue()  ,
//	   				     moneyFormat.format ( tr.getPriceMedium() ),	moneyFormat.format ( tr.getPriceSmall () ) , moneyFormat.format ( tr.getTotalLineAmount () ) ) + NEWLINE );
//
//
//		         quantityM = quantityM + tr.getQuantityMedium();
//		         quantityS = quantityS + tr.getQuantitySmall();
//		         totalSales = totalSales + tr.getTotalLineAmount ();
//       
//        if(count % 36 == 0 && count != 0    )  
//			{  
//				
//			   	body.append ( NEWLINE );
//				body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    
//			    subHeader = new StringBuilder ();
//			    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
//			    
//			    subHeader.append ( " "+ISSUE_DATE +subString ( transactionHeader.getIssueDate ().toString()  , ISSUE_DATE_Length )  + NEWLINE );
//			    subHeader.append (  NEWLINE);
//				 
//				pageNumber =pageNumber+  1;
//				// body.append ( pageNumber );
//			 
//			        	subHeader.append ( CLIENT_ID +subString ( client.getClientCode () ,CLIENT_ID_length) );
//				    	subHeader.append ( " "+ subString (client.getClientName ()  ,CLIENT_Name_length) + NEWLINE );
//				     	subHeader.append (  "                "+   client.getClientAddress ()    + NEWLINE );
//				     
//				  	  if(client.getClientType()==1){
//				    	   subHeader.append ("             CASH INVOICE    " );
//				    	   subHeader.append (   transactionHeader.getTransactionCode () + NEWLINE );	
//				    	   }
//				       else 
//				    	   {
//				    	   subHeader.append ("             CREDIT INVOICE    "); 
//				    	   subHeader.append (   transactionHeader.getTransactionCode () + NEWLINE );	
//				    	   }
//				     	subHeader.append (  NEWLINE);
//				     	subHeader.append ( PRINT_DATE + now.getTime ()   + NEWLINE );
//				       	subHeader.append ( HORIZANTAL_LINE + NEWLINE );
//						subHeader.append ( OI_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
//					    body.append(subHeader);
//			 
//			}
//		         		 count=count+1;
//		         				
//	} // End for loop
//		 body.append ( HORIZANTAL_LINE + NEWLINE );
//		 body.append ( "                            SUB - TOTAL SALES  :"+(int)quantityM  +"/"+(int)quantityS  +"            "+ moneyFormat.format (totalSales)+NEWLINE );
//	 	
//
//		}
//		Double remainingAmount=0d;
//		if(transactionHeader.getRemainingAmount()>0)
//		{	String SQL=null;
//			String selectionArguments [] = null;
//			SQL = "  select SUM(RemainingAmount)  from ClientDues where   ClientCode=? and companycode=?  ";
//		 
//		// Compute the selection arguments
//			selectionArguments = new String [] {	   
//					String.valueOf (transactionHeader.getClientCode()),
//					String.valueOf (transactionHeader.getCompanyCode())
//			};
//			Cursor cursor = DatabaseUtils.getInstance ( context ).getDaoSession ().getDatabase ()
//					        .rawQuery(SQL , selectionArguments) ;
//		
//				// Move the cursor to the first raw
//			if ( cursor.moveToFirst () ) {
//				// Iterate over all raws
//				do {
//					remainingAmount= cursor.getDouble(0);
//				} while ( cursor.moveToNext () );
//			} // End if
//			// Close the cursor
//			cursor.close ();
//			cursor = null;
//			}
//		if(client.getClientType()==1){
//		
//			body.append ( "     NET SALES:                      " + moneyFormat.format ( transactionHeader.getGrossAmount () ) + " " + transactionHeader.getCurrencyCode () + NEWLINE );
//		//  body.append ( "     CREDIT:                         " + moneyFormat.format ( transactionHeader.getRemainingAmount() ) + " " + transactionHeader.getCurrencyCode () + NEWLINE   );
//	    //  body.append ( "     CURRENT OUTSTANDING BALANCE     " + moneyFormat.format ( transactionHeader.getTotalTaxAmount() ) + " " + transactionHeader.getCurrencyCode () + NEWLINE );
//		}
//		else{
//			body.append ( "     NET SALES:                      " + moneyFormat.format ( transactionHeader.getGrossAmount () ) + " " + transactionHeader.getCurrencyCode () + NEWLINE );
//		    body.append ( "     CREDIT:                         " + moneyFormat.format ( transactionHeader.getRemainingAmount() ) + " " + transactionHeader.getCurrencyCode () + NEWLINE   );
//	    	body.append ( "     CURRENT OUTSTANDING BALANCE     " + moneyFormat.format ( transactionHeader.getTotalTaxAmount() + remainingAmount ) + " " + transactionHeader.getCurrencyCode () + NEWLINE );
//		
//		}
//		//body.append(  HORIZANTAL_LINE + NEWLINE);
//		//body.append ( "SALES REP                  Last Page                        CUSTOMER SIGN/STAMP" );
//		int lastPage1 = (count + 3) / 36;
//		int rest1 = (count + 3) % 36; 
//		
//		 
//		 if( pageNumber == ( lastPage1 + 1 )   )
//	      {
//			 if( pageNumber == 1){
//  	  				if(( 36 - (count+3) - 3 ) > 0)
//  				{	    
//  	  			for(int u=0 ; u< 36-(count+1)  - 3 ;u++)
//  	  			body.append ( NEWLINE );
//  				}}
//  	  		else{
//  	  		if(rest1>4)
//  	  		for(int u=0 ; u< 37-rest1-3 ;u++)
//  	  		body.append ( NEWLINE );
//  	  			}
//			}
//		 body.append ( NEWLINE );
//		 body.append(  HORIZANTAL_LINE + NEWLINE);
//		 body.append ( "SALES REP                  Last Page                        CUSTOMER SIGN/STAMP" );
//		 } 
		
		
//			footer = new StringBuilder ();
//			if ( type == Type.ORDER  ) {
//				int lastPage1= transactionDetails.size() / 36;
//				int rest1 = transactionDetails.size() % 36; 
//				
//				footer.append ( GROSS_AMOUNT + moneyFormat.format ( transactionHeader.getGrossAmount () ) + " " + transactionHeader.getCurrencyCode () +" " );
//				footer.append ( DISCOUNT_AMOUNT + moneyFormat.format ( transactionHeader.getDiscountAmount () ) + " " + transactionHeader.getCurrencyCode () + " "   );
//				footer.append ( TOTAL_AMOUNT + moneyFormat.format ( transactionHeader.getTotalTaxAmount() ) + " " + transactionHeader.getCurrencyCode () + NEWLINE );
//				 
//				 if(pageNumber == ( lastPage1 + 1 )   )
//			      {
//					 if(pageNumber==1)
//		  	  				if(( 36 - transactionDetails.size() - 3 )>0)
//		  				{	    for(int u=0 ; u< 36-transactionDetails.size()  - 3 ;u++)
//		  					footer.append ( NEWLINE );
//		  				}
//		  	  				else{
//		  	  					if(rest1>4)
//		  	  						for(int u=0 ; u< 36-rest1-3 ;u++)
//		  	  							footer.append ( NEWLINE );
//		  	  					}
//					 }
//				footer.append(  HORIZANTAL_LINE + NEWLINE);
//				footer.append ( "SALES REP                  Last Page                        CUSTOMER SIGN/STAMP" );
//			 
//			} // End if
//			
//		
		
//		
//		if (  type == Type.ORDER    ) {
//				 	
//					for( int i = 0 ; i < transactionDetails.size () ; i ++ ) {
//						TransactionDetails tr = transactionDetails.get ( i );
//						if(tr.getOrderedType().equals("F") ){
//							body.append ( String.format ( "%-"+CODE_LENGTH+"s %"+TOTAL_LENGTH +"s %"+ (QUANTITY_LENGTH +2)+ "s %"+price_LENGTH + "s %" + LINE_AMOUNT_LENGTH + "s" , 
//									tr.getItemCode ().length () > CODE_LENGTH ? tr.getItemCode ().substring ( 0 , CODE_LENGTH ) : tr.getItemCode ()	, tr.getItemName ().length () >  TOTAL_LENGTH  ? tr.getItemName ().substring ( 0 ,  TOTAL_LENGTH  ) : tr.getItemName ()
//									, 	 	 tr.getQuantityMedium().toString ()+"/" +tr.getQuantitySmall(),
//									moneyFormat.format ( tr.getPriceMedium() )  , moneyFormat.format ( tr.getTotalLineAmount () ) )+"F " + NEWLINE );
//							
//						}else{
//						body.append ( String.format ( "%-"+CODE_LENGTH+"s %"+TOTAL_LENGTH +"s %"+ (QUANTITY_LENGTH +2)+ "s %"+price_LENGTH + "s %" + LINE_AMOUNT_LENGTH + "s" , 
//							  tr.getItemCode ().length () > CODE_LENGTH ? tr.getItemCode ().substring ( 0 , CODE_LENGTH ) : tr.getItemCode ()	
//							  , tr.getItemName ().length () >  TOTAL_LENGTH  ? tr.getItemName ().substring ( 0 ,  TOTAL_LENGTH  ) : tr.getItemName (),	
//							 	 	 tr.getQuantityMedium().toString ()+"/" +tr.getQuantitySmall(),
//							   moneyFormat.format ( tr.getPriceMedium() )  , moneyFormat.format ( tr.getTotalLineAmount () ) ) + NEWLINE );
//						}
//						 quantityM= quantityM+tr.getQuantityMedium();
//						 quantityS=quantityS+tr.getQuantitySmall();
//						 totalSales=totalSales+tr.getTotalLineAmount ();
//					
//						if(count % 38 == 0 && count != 0    )  
//						{  
//							
//						   	body.append ( NEWLINE );
//							body.append ( NEWLINE );
//						    body.append ( NEWLINE );
//						    body.append ( NEWLINE );
//						    body.append ( NEWLINE );
//						    body.append ( NEWLINE );
//						    body.append ( NEWLINE );
//						    
//						    body.append ( NEWLINE );
//						    body.append ( NEWLINE );
//						    body.append ( NEWLINE );
//						    body.append ( NEWLINE );
//						    body.append ( NEWLINE );
//						    body.append ( NEWLINE );
//						    body.append ( NEWLINE );
//						    body.append ( NEWLINE );
//						    body.append ( NEWLINE );
//						    body.append ( NEWLINE );
//						    body.append ( NEWLINE );
//						    
//						    subHeader = new StringBuilder ();
//						    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
//							subHeader.append ( " "+ISSUE_DATE +subString ( transactionHeader.getIssueDate ().toString()  , ISSUE_DATE_Length )  + NEWLINE );
//							subHeader.append ( ORDER_NUM + transactionHeader.getTransactionCode () + NEWLINE );	
//							pageNumber =pageNumber+  1;
//							// body.append ( pageNumber );
//							   if ( copyType == 0)
//							    		subHeader.append ( RECEPIT + NEWLINE );
//							  	else 
//									subHeader.append ( RECEPIT_COPY + NEWLINE );
//						        	subHeader.append ( CLIENT_ID +subString ( client.getClientCode () ,CLIENT_ID_length) );
//							    	subHeader.append ( " "+ subString (client.getClientName ()  ,CLIENT_Name_length) + NEWLINE );
//							     	subHeader.append (  "                "+   client.getClientAddress ()    + NEWLINE );
//							     
//									
//							     	subHeader.append ( PRINT_DATE + now.getTime ()   + NEWLINE );
//							       	subHeader.append ( HORIZANTAL_LINE + NEWLINE );
//									subHeader.append (Order_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
//								    body.append(subHeader);
//						 
//						}
//
//					         	 count=count+1;
//						 
//					} // End for loop
//				
//					body.append ( HORIZANTAL_LINE + NEWLINE );
//					body.append ( "                              Total Sales :"+quantityM +"/"+quantityS +"            "+ moneyFormat.format (totalSales)+NEWLINE );
//				 
//					int lastPage1= transactionDetails.size() / 38;
//					int rest1 = transactionDetails.size() % 38; 
//					
//					body.append ( GROSS_AMOUNT + moneyFormat.format ( transactionHeader.getGrossAmount () ) + " " + transactionHeader.getCurrencyCode () +" " );
//					body.append ( DISCOUNT_AMOUNT + moneyFormat.format ( transactionHeader.getDiscountAmount () ) + " " + transactionHeader.getCurrencyCode () + " "   );
//					body.append ( TOTAL_AMOUNT + moneyFormat.format ( transactionHeader.getTotalTaxAmount() ) + " " + transactionHeader.getCurrencyCode () + NEWLINE );
//					if(pageNumber==1)
//						if(( 38 - transactionDetails.size() - 3 )>0)
//		 			{	  for(int u=0 ; u< 38-transactionDetails.size()-3  ;u++)
//		 				body.append ( NEWLINE );
//		 			}
//					 if(pageNumber == ( lastPage1 + 1 )   )
//				      {
//					if(rest1>4)
//		    	     for(int u=0 ; u< 38-rest1-3 ;u++)
//		    	    	 body.append ( NEWLINE );
//				      }
//					 body.append(  HORIZANTAL_LINE + NEWLINE);
//					 body.append ( "SALES REP                  Last Page                        CUSTOMER SIGN/STAMP" );
//				 
//
//					
//				} // End if
//				// Build the footer
//	 
// 			
		
		
		
			 SimpleDateFormat sdfaa = new SimpleDateFormat("HH:mm",  java.util.Locale.getDefault());
		//	 if ( type == Type.RouteReport) 
//		     {
//			  //SchedulePercentage 
//	 	     		for( int i = 0 ; i < SalesRoutingReport.size () ; i ++ ) { 
//	 	   			 	SalesRoutingReport tr = SalesRoutingReport.get ( i ); 
//		 	      	 	 	body.append ( String.format ( "%-"+(CODE_LENGTH+2)+"s %"+(5)+"s %"+(5) +"s %"+ (QUANTITY_LENGTH -2)+ "s %"+20+"s%-" + UNIT_PRICE_LENGTH + "s %" + (LINE_AMOUNT_LENGTH - 2) + "s" , 
//		 	   			    tr.getInv ().length () > 12? tr.getInv ().substring ( 0 , 12 ) : tr.getInv () ,
//		 	   				sdfaa.format( tr.getTin ()).toString() ,	
//		 	   			(tr.getTout()==0?"":		sdfaa.format( 	tr.getTout() ).toString()),	  tr.getClientCode() ,
//		 	   		 tr.getClientName().length () > 20?  tr.getClientName().substring ( 0 , 20 )	: tr.getClientName()   
//		 	   				 ,	 "   "+ tr.getType() ,  tr.getAmount ()   ) + NEWLINE );
//
//			 
//	 
//		
//			if(count % 36 == 0 && count != 0    )  
//			{  
//				
//				body.append ( NEWLINE );
//			   	body.append ("                      Continued   "+ NEWLINE);
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
//			    body.append ( NEWLINE );
////			    body.append ( NEWLINE );
////			    body.append ( NEWLINE );
////			    body.append ( NEWLINE );
////			    body.append ( NEWLINE );
////			    body.append ( NEWLINE );
//			    subHeader = new StringBuilder ();
//			    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
//			    subHeader.append ( NEWLINE );
//			    subHeader.append ( NEWLINE );
//			    subHeader.append (   "          ROUTE ACTIVITY REPORT" );
//			    subHeader.append (  NEWLINE);
//			
//				pageNumber =pageNumber +  1;
//			 
//				 
//			        	subHeader.append (  NEWLINE);
//				     	subHeader.append ( PRINT_DATE + now.getTime ()   + NEWLINE );
//				       	subHeader.append ( HORIZANTAL_LINE + NEWLINE );
//						subHeader.append ( RouteReportHeader + NEWLINE + HORIZANTAL_LINE + NEWLINE );
//					    body.append(subHeader);
//			 
//			} 
//			count=count+1;
//		} // End for loop
//	  
//		body.append ( HORIZANTAL_LINE + NEWLINE );
//		body.append (   "                          TOTAl DOCUMENT COUNT  "+count );
//		body.append ( NEWLINE );
//	    body.append ( NEWLINE );
//		body.append ( NEWLINE );
//	    body.append ( NEWLINE );
//      //second 
//	    
//		 
//		for(int i=0;i<6;i++){
//		if(count % 36 == 0 && count != 0    )  
//		{  
//			
//			body.append ( NEWLINE );
//		   	body.append ("                      Continued   "+ NEWLINE);
//		    body.append ( NEWLINE );
//		    body.append ( NEWLINE );
//		    body.append ( NEWLINE );
//		    body.append ( NEWLINE );
//		    body.append ( NEWLINE );
//		    body.append ( NEWLINE );
//		    
//		    body.append ( NEWLINE );
//		    body.append ( NEWLINE );
//		    body.append ( NEWLINE );
//		    body.append ( NEWLINE );
//		    body.append ( NEWLINE );
//		    body.append ( NEWLINE );
//		    body.append ( NEWLINE );
//		    body.append ( NEWLINE );
//		    body.append ( NEWLINE );
//		    body.append ( NEWLINE );
//		    body.append ( NEWLINE );
//		    body.append ( NEWLINE );
//		    body.append ( NEWLINE );
////		    body.append ( NEWLINE );
////		    body.append ( NEWLINE );
////		    body.append ( NEWLINE );
////		    body.append ( NEWLINE );
////		    body.append ( NEWLINE );
//		    subHeader = new StringBuilder ();
//		    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
//		    subHeader.append ( NEWLINE );
//		    subHeader.append ( NEWLINE );
//		    subHeader.append (   "          ROUTE ACTIVITY REPORT" );
//		    subHeader.append (  NEWLINE);
//			pageNumber =pageNumber +  1;
//			 
//			    	subHeader.append (  NEWLINE);
//			     	subHeader.append ( PRINT_DATE + now.getTime ()   + NEWLINE );
//			       	subHeader.append ( HORIZANTAL_LINE + NEWLINE );
//					subHeader.append ( RouteReportHeader + NEWLINE + HORIZANTAL_LINE + NEWLINE );
//				    body.append(subHeader);
//		 
//		}
//		count=count+1;
//		}
//		body.append ("                            TOTAL    PERCENTAGE"+ NEWLINE);
//	    for( int i = 0 ; i < schedulePercentage.size () ; i ++ ) {
//	    	SchedulePercentage tr = schedulePercentage.get ( i ); 
//	      	 	 	
//	    	body.append (String.format ( "%-"+20+"s %"+(10)+"s %"+(10)+"s ",
//	    			tr.getType().length () > 20 ? tr.getType ().substring ( 0 , 20 ) : tr.getType ()  ,	
//	    					 tr.getTotal()  ,	 tr.getPercentage() +(i==0?"":"%")	) + NEWLINE  );
//	    	
//	  
//
//				 
//				
//				
//				if(count % 36 == 0 && count != 0    )  
//				{  
//					
//					body.append ( NEWLINE );
//				   	body.append ("                      Continued   "+ NEWLINE);
//				    body.append ( NEWLINE );
//				    body.append ( NEWLINE );
//				    body.append ( NEWLINE );
//				    body.append ( NEWLINE );
//				    body.append ( NEWLINE );
//				    body.append ( NEWLINE );
//				    
//				    body.append ( NEWLINE );
//				    body.append ( NEWLINE );
//				    body.append ( NEWLINE );
//				    body.append ( NEWLINE );
//				    body.append ( NEWLINE );
//				    body.append ( NEWLINE );
//				    body.append ( NEWLINE );
//				    body.append ( NEWLINE );
//				    body.append ( NEWLINE );
//				    body.append ( NEWLINE );
//				    body.append ( NEWLINE );
//				    body.append ( NEWLINE );
//				    body.append ( NEWLINE );
////				    body.append ( NEWLINE );
////				    body.append ( NEWLINE );
////				    body.append ( NEWLINE );
////				    body.append ( NEWLINE );
////				    body.append ( NEWLINE );
//				    subHeader = new StringBuilder ();
//				    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
//				    subHeader.append ( NEWLINE );
//				    subHeader.append ( NEWLINE );
//				    subHeader.append (   "          ROUTE ACTIVITY REPORT" );
//				    subHeader.append (  NEWLINE);
//				
//					pageNumber =pageNumber +  1;
//					 
//					    	subHeader.append (  NEWLINE);
//					     	subHeader.append ( PRINT_DATE + now.getTime ()   + NEWLINE );
//					       	subHeader.append ( HORIZANTAL_LINE + NEWLINE );
//							subHeader.append ( RouteReportHeader + NEWLINE + HORIZANTAL_LINE + NEWLINE );
//						    body.append(subHeader);
//						    body.append ("                                 TOTAL    PERCENTAGE"+ NEWLINE);
//				 
//				}
//				     		 count=count+1;
//				} // End for loop
//				
//				body.append ( HORIZANTAL_LINE + NEWLINE );
//				
//				
//				body.append ("                               END ROUTE ACTIVITY REPORT "+ NEWLINE);
//				body.append ( NEWLINE );
//				body.append ( NEWLINE );
//				body.append ( NEWLINE );
//				body.append ( NEWLINE );
//			
//				
//				int lastPage1 = ( count+5 ) / 36;
//				int rest1 = ( count+5 ) % 36; 
//				
//				 
//				 if(pageNumber == ( lastPage1 + 1 )   )
//			      {
//					 if(pageNumber==1){
//		  	  				if(( 36 - (count+3) - 3 )>0)
//		  				{	    
//		  	  			for(int u=0 ; u< 36-(count+3)  - 3 ;u++)
//		  	  			body.append ( NEWLINE );
//		  				}}
//		  	  		else{
//		  	  		if(rest1>4)
//		  	  		for(int u=0 ; u< 36-rest1-3 ;u++)
//		  	  		body.append ( NEWLINE );
//		  	  			}
//					}
//		     }
			 
			 
			 if ( type == Type.DirectReturn) 
		     {
			  ArrayList< TransactionDetails>  transactionDetailsO=new ArrayList<TransactionDetails>();
		      ArrayList< TransactionDetails>  transactionDetailsF=new ArrayList<TransactionDetails>();
	 	     	for( int i = 0 ; i < transactionDetails.size () ; i ++ ) {
			 
	 	     			TransactionDetails tr = transactionDetails.get ( i );
	 	     			if(tr.getOrderedType().equals("O") )
	 	     				transactionDetailsO.add(tr);
	 	     			else
	 	     				transactionDetailsF.add(tr);
	 	     				
	 	     		}
	 	     		
	 	     		for( int i = 0 ; i < transactionDetailsO.size () ; i ++ ) {
	 	   			
		 	     		 
		 	     			TransactionDetails tr = transactionDetailsO.get ( i ); 
		 	      	 	
		 	     			body.append ( String.format ( "%-"+(CODE_LENGTH-2)+"s %"+(TOTAL_LENGTH+7)+"s %"+(uom_LENGTH-1) +"s %"+ (QUANTITY_LENGTH -2)+ "s %"+price_LENGTH+"s%" + UNIT_PRICE_LENGTH + "s %" + (LINE_AMOUNT_LENGTH - 2) + "s" , 
		 	   				    tr.getItemCode ().length () > CODE_LENGTH ? tr.getItemCode ().substring ( 0 , CODE_LENGTH ) : tr.getItemCode ()	
		 	   				    , tr.getItemName ().length () >  (TOTAL_LENGTH+7) ? tr.getItemName ().substring ( 0 , (TOTAL_LENGTH+7)  ) : tr.getItemName (),	
		 	   				    DatabaseUtils.getInstance ( context ).getDaoSession ().getItemsDao ().queryBuilder ()
		 	   			        .where ( ItemsDao.Properties.ItemCode.eq (  tr.getItemCode()   ) ).unique().getUnitMediumSmall()	,	   tr.getQuantityMedium().intValue()+"/" +  tr.getQuantitySmall().intValue()  ,
		 	   				     moneyFormat.format ( tr.getPriceMedium() ),	moneyFormat.format ( tr.getPriceSmall () ) , moneyFormat.format ( tr.getTotalLineAmount () ) ) + NEWLINE );

			 
			 quantityM= quantityM+tr.getQuantityMedium();
			 quantityS=quantityS+tr.getQuantitySmall();
			 totalSales=totalSales+tr.getTotalLineAmount ();
		
			if(count % 36 == 0 && count != 0    )  
			{  
				
			   	body.append ( NEWLINE );
			   	body.append ("                      Continued   "+ NEWLINE);
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    
			    subHeader = new StringBuilder ();
			    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
			    
			    subHeader.append ( " "+ISSUE_DATE +subString ( transactionHeader.getIssueDate ().toString()  , ISSUE_DATE_Length )  + NEWLINE );
			    subHeader.append (  NEWLINE);
			
				pageNumber =pageNumber +  1;
				// body.append ( pageNumber );
				 
			        	subHeader.append ( CLIENT_ID +subString ( client.getClientCode () ,CLIENT_ID_length) );
				    	subHeader.append ( " "+ subString (client.getClientName ()  ,CLIENT_Name_length) + NEWLINE );
				     	subHeader.append (  "                "+   client.getClientAddress ()    + NEWLINE );
				     
				     	  if(client.getClientType()==1){
				     		  	subHeader.append ("           Cash Return    ");
					    		subHeader.append (  transactionHeader.getTransactionCode () + NEWLINE );	 
				     	  }
					       else
					       {
					    	   subHeader.append ("        Credit Return    "); 
					    	   subHeader.append (   transactionHeader.getTransactionCode () + NEWLINE );	 
					       }
				     	subHeader.append (  NEWLINE);
				     	subHeader.append ( PRINT_DATE + now.getTime ()   + NEWLINE );
				       	subHeader.append ( HORIZANTAL_LINE + NEWLINE );
						subHeader.append ( OI_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
					    body.append(subHeader);
			 
			}
		         		 count=count+1;
		} // End for loop
	  
		body.append ( HORIZANTAL_LINE + NEWLINE );
		body.append ( "                            SUB - TOTAL SALES  :"+(int)quantityM  +"/"+(int)quantityS  +"            "+ moneyFormat.format (totalSales)+NEWLINE );
	 
		  
		
		if(transactionDetailsF.size() > 0){
			 quantityM = 0d;
	         quantityS = 0d;
	         totalSales= 0d;
		
		body.append (  HORIZANTAL_LINE + NEWLINE);
		body.append ( "                       FREE GOODS"+ NEWLINE);	
		count=count+6;
		 
		for( int i = 0 ; i < transactionDetailsF.size () ; i ++ ) {
	   			
  			TransactionDetails tr = transactionDetailsF.get ( i ); 
   	 	
  			body.append ( String.format ( "%-"+(CODE_LENGTH-2)+"s %"+(TOTAL_LENGTH+7)+"s %"+(uom_LENGTH-1) +"s %"+ (QUANTITY_LENGTH -2)+ "s %"+price_LENGTH+"s%" + UNIT_PRICE_LENGTH + "s %" + (LINE_AMOUNT_LENGTH - 2) + "s" , 
	   				    tr.getItemCode ().length () > CODE_LENGTH ? tr.getItemCode ().substring ( 0 , CODE_LENGTH ) : tr.getItemCode ()	
	   				    , tr.getItemName ().length () >  (TOTAL_LENGTH+7) ? tr.getItemName ().substring ( 0 , (TOTAL_LENGTH+7)  ) : tr.getItemName (),	
	   				    DatabaseUtils.getInstance ( context ).getDaoSession ().getItemsDao ().queryBuilder ()
	   			        .where ( ItemsDao.Properties.ItemCode.eq (  tr.getItemCode()   ) ).unique().getUnitMediumSmall()	,	   tr.getQuantityMedium().intValue()+"/" +  tr.getQuantitySmall().intValue()  ,
	   				     moneyFormat.format ( tr.getPriceMedium() ),	moneyFormat.format ( tr.getPriceSmall () ) , moneyFormat.format ( tr.getTotalLineAmount () ) ) + NEWLINE );


		         quantityM = quantityM + tr.getQuantityMedium();
		         quantityS = quantityS + tr.getQuantitySmall();
		         totalSales = totalSales + tr.getTotalLineAmount ();
        
         if(count % 36 == 0 && count != 0    )  
			{  
				body.append ( NEWLINE );
			   	body.append ("                      Continued   "+ NEWLINE);
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    
			    subHeader = new StringBuilder ();
			    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
			    
			    subHeader.append ( " "+ISSUE_DATE +subString ( transactionHeader.getIssueDate ().toString()  , ISSUE_DATE_Length )  + NEWLINE );
			    subHeader.append (  NEWLINE);
				
				pageNumber =pageNumber +  1;
				// body.append ( pageNumber );
				 
	        	subHeader.append ( CLIENT_ID +subString ( client.getClientCode () ,CLIENT_ID_length) );
		    	subHeader.append ( " "+ subString (client.getClientName ()  ,CLIENT_Name_length) + NEWLINE );
		     	subHeader.append (  "                "+   client.getClientAddress ()    + NEWLINE );
				     
				     if(client.getClientType() == 1){
					      subHeader.append ("             Cash Return    " );
					      subHeader.append (   transactionHeader.getTransactionCode () + NEWLINE );	
					    	   }
					       else 
					    	   {
					    	   subHeader.append ("            Credit Return    "); 
					    	   subHeader.append (   transactionHeader.getTransactionCode () + NEWLINE );	
					    	   }
				     	subHeader.append (  NEWLINE);
				     	subHeader.append ( PRINT_DATE + now.getTime ()   + NEWLINE );
				       	subHeader.append ( HORIZANTAL_LINE + NEWLINE );
						subHeader.append ( OI_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
					    body.append(subHeader);
			 
			}
		         		 count=count+1;
		
		
	} // End for loop
		body.append ( HORIZANTAL_LINE + NEWLINE );
		body.append ( "                            SUB - TOTAL SALES  :"+(int)quantityM  +"/"+(int)quantityS  +"            "+ moneyFormat.format (totalSales)+NEWLINE );
	   
    }
		Double remainingAmount=0d;
		if(transactionHeader.getRemainingAmount()>0)
		{	String SQL=null;
			String selectionArguments [] = null;
			SQL = "  select SUM(RemainingAmount)  from ClientDues where   ClientCode=? and companycode=?  ";
		 
		// Compute the selection arguments
			selectionArguments = new String [] {	   
					String.valueOf (transactionHeader.getClientCode()),
					String.valueOf (transactionHeader.getCompanyCode())
			};
			Cursor cursor = DatabaseUtils.getInstance ( context ).getDaoSession ().getDatabase ()
					        .rawQuery(SQL , selectionArguments) ;
		
				// Move the cursor to the first raw
			if ( cursor.moveToFirst () ) {
				// Iterate over all raws
				do {
					remainingAmount= cursor.getDouble(0);
				} while ( cursor.moveToNext () );
			} // End if
			// Close the cursor
			cursor.close ();
			cursor = null;
			}
		if(client.getClientType()==1){
		
			body.append ( "     NET SALES:                      " + moneyFormat.format ( transactionHeader.getGrossAmount () ) + " " + transactionHeader.getCurrencyCode () + NEWLINE );
		//  body.append ( "     CREDIT:                         " + moneyFormat.format ( transactionHeader.getRemainingAmount() ) + " " + transactionHeader.getCurrencyCode () + NEWLINE   );
	    //  body.append ( "     CURRENT OUTSTANDING BALANCE     " + moneyFormat.format ( transactionHeader.getTotalTaxAmount() ) + " " + transactionHeader.getCurrencyCode () + NEWLINE );
		}
		else{
			body.append ( "     NET SALES:                      " + moneyFormat.format ( transactionHeader.getGrossAmount () ) + " " + transactionHeader.getCurrencyCode () + NEWLINE );
		    body.append ( "     CREDIT:                         " + moneyFormat.format ( transactionHeader.getRemainingAmount() ) + " " + transactionHeader.getCurrencyCode () + NEWLINE   );
	    	body.append ( "     CURRENT OUTSTANDING BALANCE     " + moneyFormat.format ( transactionHeader.getTotalTaxAmount() + remainingAmount ) + " " + transactionHeader.getCurrencyCode () + NEWLINE );
		
		}
		//body.append(  HORIZANTAL_LINE + NEWLINE);
		//body.append ( "SALES REP                  Last Page                        CUSTOMER SIGN/STAMP" );
		int lastPage1 = ( count+3 ) / 36;
		int rest1 = ( count+3 ) % 36; 
		
		 
		 if(pageNumber == ( lastPage1 + 1 )   )
	      {
			 if(pageNumber==1){
  	  				if(( 36 - (count+3) - 3 )>0)
  				{	    
  	  			for(int u=0 ; u< 36-(count+3)  - 3 ;u++)
  	  			body.append ( NEWLINE );
  				}}
  	  		else{
  	  		if(rest1>4)
  	  		for(int u=0 ; u< 36-rest1-3 ;u++)
  	  		body.append ( NEWLINE );
  	  			}
			}
		 body.append ( NEWLINE );
		 body.append(  HORIZANTAL_LINE + NEWLINE);
		 body.append ( "SALES REP                  Last Page                        CUSTOMER SIGN/STAMP" );
		 } 
		
		
	 if(type == Type.FreshQQP) 
		 {    	
			 body.append ( NEWLINE );
			 body.append ( NEWLINE );
			 body.append ( NEWLINE );
			 ArrayList< TransactionDetails>  transactionDetailsCase = new ArrayList<TransactionDetails>();
			 ArrayList< TransactionDetails>  transactionDetailsUnit = new ArrayList<TransactionDetails>(); 
			 for( int i = 0 ; i < transactionDetails.size () ; i ++ ) {
			   	TransactionDetails tr = transactionDetails.get ( i );
			    	if( tr.getQuantityMedium() > 0  )
			        		transactionDetailsCase.add(tr);
			        	if(tr.getQuantitySmall() > 0 )
			        		transactionDetailsUnit.add(tr);
			        }
				  for( int i = 0 ; i < transactionDetailsCase.size () ; i ++ ) {
		 	   			
			 	   TransactionDetails tr = transactionDetailsCase.get ( i ); 
			 	     		 
			       body.append ( String.format ( "%-"+4+"s %-"+(CODE_LENGTH-3)+"s %-"+(TOTAL_LENGTH+8)+"s %-" + (QUANTITY_LENGTH -1)+ "s %-" + price_LENGTH  +"s%-" + (UNIT_PRICE_LENGTH )+ "s %" + (LINE_AMOUNT_LENGTH - 2) + "s" , 
			       "" , tr.getItemCode ().length () > CODE_LENGTH ? tr.getItemCode ().substring ( 0 , CODE_LENGTH ) : tr.getItemCode ()	
			 	   , tr.getItemName ().length () >  (TOTAL_LENGTH+5) ? tr.getItemName ().substring ( 0 , (TOTAL_LENGTH+5)  ) : tr.getItemName (),	
			 	   tr.getQuantityMedium().intValue()  , "  " ,
			 	   moneyFormat.format ( tr.getPriceMedium() ) , moneyFormat.format ( tr.getTotalLineAmount () ) ) + NEWLINE );

				 
				 // totalSales=totalSales+tr.getTotalLineAmount ();
					
						if(count % 32 == 0 && count != 0    )  
						  {   	
							body.append ( NEWLINE );
						   	body.append ("                      Continued   "+ NEWLINE);
						    body.append ( NEWLINE );  
						   	body.append ( NEWLINE );
							body.append ( NEWLINE );
						    body.append ( NEWLINE );
						    body.append ( NEWLINE );
						    body.append ( NEWLINE );
						    body.append ( NEWLINE );
						    body.append ( NEWLINE );
						    body.append ( NEWLINE );
						    
						    body.append ( NEWLINE );
						    body.append ( NEWLINE );
						    body.append ( NEWLINE );
						    body.append ( NEWLINE );
						    body.append ( NEWLINE );
						    body.append ( NEWLINE );
						    body.append ( NEWLINE );
						    body.append ( NEWLINE );
						    body.append ( NEWLINE );
						    body.append ( NEWLINE );
						    body.append ( NEWLINE );
						    subHeader = new StringBuilder ();
						   
						
							  pageNumber = pageNumber +  1;
							  subHeader.append ("                             "+ PRINT_DATE + now.getTime ()  + NEWLINE + NEWLINE );
							  PaymentTerms paymentTerms= DatabaseUtils.getInstance ( context ).getDaoSession ().getPaymentTermsDao ().queryBuilder ()
						 		 .where ( PaymentTermsDao.Properties.PaymentTermCode.eq ( client.getPaymentTermCode() )  ).unique ();
								 Vehicles	    vehicle   = DatabaseUtils.getInstance ( context ).getDaoSession ().getVehiclesDao().queryBuilder ()
									          .where (  
											  VehiclesDao.Properties.CompanyCode .eq ( transactionHeader.getCompanyCode()  ),
											  VehiclesDao.Properties.DivisionCode .eq ( transactionHeader.getDivisionCode()  )).unique ();	 
								 subHeader.append ("   Invoice: " +subString (transactionHeader.getTransactionCode (),CLIENT_ID_length)+" " );
								 subHeader.append ( paymentTerms==null?" ":subString (paymentTerms.getPaymentTermName(),30) 
												 +" INV DATE:"+ sdfa.format(transactionHeader.getIssueDate ()).toString() 
												 +" Page: "+pageNumber + NEWLINE );
								   subHeader.append ("   Customer: " +subString ( client.getClientCode () ,CLIENT_ID_length)  +" ");
								   subHeader.append ( client==null?" ":subString (client.getClientName ()  ,30)  + NEWLINE ); 
								   subHeader.append ( "   Adress: "+ subString (  client.getClientAddress ()  ,CLIENT_ID_length));
								   subHeader.append ( NEWLINE );
								   subHeader.append ( "   whse: ");
								   subHeader.append (  vehicle==null?"":subString (  vehicle.getVehicleCode()  ,CLIENT_ID_length));
						 	       subHeader.append ( " SM: "+ subString ( user.getUserCode() , UserCode_Length ) + " " +subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
						 	  
								    body.append(subHeader);
								    body.append ( NEWLINE );
									body.append ( NEWLINE );
									body.append ( NEWLINE );
						}
					         		 count=count+1;
					} // End for loop
						  for( int i = 0 ; i < transactionDetailsUnit.size () ; i ++ ) {
				 	   			
			 	     			TransactionDetails tr = transactionDetailsUnit.get ( i ); 
			 	     		 
			 	     			body.append ( String.format ( "%-"+ 4 +"s %-"+(CODE_LENGTH-3)+"s %-"+(TOTAL_LENGTH+8)+"s %" + (QUANTITY_LENGTH  -1)+ "s %-"+price_LENGTH+"s%-" + ( UNIT_PRICE_LENGTH ) + "s %" + (LINE_AMOUNT_LENGTH - 2) + "s" , 
			 	   				"" ,    tr.getItemCode ().length () > CODE_LENGTH ? tr.getItemCode ().substring ( 0 , CODE_LENGTH ) : tr.getItemCode ()	
			 	   				   , tr.getItemName ().length () >  (TOTAL_LENGTH+5) ? tr.getItemName ().substring ( 0 , (TOTAL_LENGTH+5)  ) : tr.getItemName (),	
			 	   						 "  " ,  tr.getQuantitySmall ().intValue()   ,
			 	   				   	moneyFormat.format ( tr.getPriceSmall () ) , moneyFormat.format ( tr.getTotalLineAmount () ) ) + NEWLINE );

				 
				 // totalSales=totalSales+tr.getTotalLineAmount ();
			
				if(count % 32 == 0 && count != 0    )  
				{  
				  	body.append ( NEWLINE );
				   	body.append ("                      Continued   "+ NEWLINE);
				    body.append ( NEWLINE );
				   	body.append ( NEWLINE );
					body.append ( NEWLINE );
				    body.append ( NEWLINE );
				    body.append ( NEWLINE );
				    body.append ( NEWLINE );
				    body.append ( NEWLINE );
				    body.append ( NEWLINE );
				    body.append ( NEWLINE );
				    
				    body.append ( NEWLINE );
				    body.append ( NEWLINE );
				    body.append ( NEWLINE );
				    body.append ( NEWLINE );
				    body.append ( NEWLINE );
				    body.append ( NEWLINE );
				    body.append ( NEWLINE );
				    body.append ( NEWLINE );
				    body.append ( NEWLINE );
				    body.append ( NEWLINE );
				    body.append ( NEWLINE );
				    
				    subHeader = new StringBuilder ();
				   
				
					     pageNumber = pageNumber +  1;
					     subHeader.append ("                             "+ PRINT_DATE + now.getTime ()  + NEWLINE  + NEWLINE);
					 	 PaymentTerms paymentTerms= DatabaseUtils.getInstance ( context ).getDaoSession ().getPaymentTermsDao ().queryBuilder ()
				 		 .where ( PaymentTermsDao.Properties.PaymentTermCode.eq ( client.getPaymentTermCode() )  ).unique ();
						 Vehicles	    vehicle   = DatabaseUtils.getInstance ( context ).getDaoSession ().getVehiclesDao().queryBuilder ()
							          .where (  
									  VehiclesDao.Properties.CompanyCode .eq ( transactionHeader.getCompanyCode()  ),
									  VehiclesDao.Properties.DivisionCode .eq ( transactionHeader.getDivisionCode()  )).unique ();	 
						 subHeader.append ("   Invoice: " +subString (transactionHeader.getTransactionCode (),CLIENT_ID_length)+" " );
						 subHeader.append ( paymentTerms==null?" ":subString (paymentTerms.getPaymentTermName(),30) 
										 +" INV DATE:"+ sdfa.format(transactionHeader.getIssueDate ()).toString() 
										 +" Page: "+pageNumber + NEWLINE );
						   subHeader.append ("   Customer: " +subString ( client.getClientCode () ,CLIENT_ID_length)  +" ");
						   subHeader.append ( client==null?" ":subString (client.getClientName ()  ,30) + NEWLINE ); 
						   subHeader.append ( "   Adress: "+ subString (  client.getClientAddress ()  ,CLIENT_ID_length));
						   subHeader.append ( NEWLINE );
						   subHeader.append ( "   whse: ");
						   subHeader.append (  vehicle==null?"":subString (  vehicle.getVehicleCode()  ,CLIENT_ID_length));
				 	       subHeader.append ( " SM: "+ subString ( user.getUserCode() , UserCode_Length ) + " " +subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
				 	  
						    body.append(subHeader);
						    body.append ( NEWLINE );
							body.append ( NEWLINE );
							body.append ( NEWLINE );
				}
			         		 count=count+1;
			} // End for loop
					
				 
					 
					//body.append(  HORIZANTAL_LINE + NEWLINE);
					//body.append ( "SALES REP                  Last Page                        CUSTOMER SIGN/STAMP" );
					int lastPage1 = ( count ) / 32;
					int rest1 = ( count ) % 32; 
					  
					 if(pageNumber == ( lastPage1 + 1 )   )
				      {
						 if(pageNumber==1){
			  	  				if(( 32 - (count ) - 3 )>0)
			  				{	    
			  	   for(int u=0 ; u< 32-(count+3)  - 3 ;u++)
			  	  		 body.append ( NEWLINE );
			  		 }
			  	  	 }
			  	   else{
			  	  		if( rest1 > 4)
			  	  		for(int u=0 ; u< 32-rest1-3 ;u++)
			  	  		body.append ( NEWLINE );
			  	  			}
						}
		                body.append ( NEWLINE );
						body.append ( NEWLINE );
						body.append ( NEWLINE );
						body.append ( NEWLINE );
						body.append ( NEWLINE );
						body.append ( NEWLINE );
						body.append ( "                                            GROSS TOTAL AMMOUNT:   " + moneyFormat.format ( transactionHeader.getGrossAmount () ) + NEWLINE );
					    body.append ( "                                            TOTAL DISC AMMOUNT :   " + moneyFormat.format ( transactionHeader.getDiscountAmount()  )  + NEWLINE   );
				    	body.append ( "                                            TOTAL NET AMMOUNT  :   " + moneyFormat.format ( transactionHeader.getTotalTaxAmount()   )  + NEWLINE );
				 
					 } 
 
					
					
				 
		 
		 if (   type == Type.RETURN) 
		     {
			  ArrayList< TransactionDetails>  transactionDetailsO=new ArrayList<TransactionDetails>();
		      ArrayList< TransactionDetails>  transactionDetailsF=new ArrayList<TransactionDetails>();
	 	     	for( int i = 0 ; i < transactionDetails.size () ; i ++ ) {
			 
	 	     			TransactionDetails tr = transactionDetails.get ( i );
//	 	     			if(tr.getOrderedType().equals("O") )
//	 	     				transactionDetailsO.add(tr);
//	 	     			else
//	 	     				transactionDetailsF.add(tr);
	 	     			if(tr.getDiscountPercentage() == 100)
	 	     			{
	 	     				transactionDetailsF.add( tr );
	 	     			}
	 	     			else
	 	     			{
	 	     				transactionDetailsO.add( tr );	 	     				
	 	     			}
	 	     		}
	 	     		
	 	     		for( int i = 0 ; i < transactionDetailsO.size () ; i ++ ) {
	 	   			
		 	     		 
		 	     			TransactionDetails tr = transactionDetailsO.get ( i ); 
		 	      	 	
		 	     			body.append ( String.format ( "%-"+(CODE_LENGTH-2)+"s %"+(TOTAL_LENGTH+7)+"s %"+(uom_LENGTH-1) +"s %"+ (QUANTITY_LENGTH -2)+ "s %"+price_LENGTH+"s%" + UNIT_PRICE_LENGTH + "s %" + (LINE_AMOUNT_LENGTH - 2) + "s" , 
		 	   				    tr.getItemCode ().length () > CODE_LENGTH ? tr.getItemCode ().substring ( 0 , CODE_LENGTH ) : tr.getItemCode ()	
		 	   				    , tr.getItemName ().length () >  (TOTAL_LENGTH+7) ? tr.getItemName ().substring ( 0 , (TOTAL_LENGTH+7)  ) : tr.getItemName (),	
		 	   				    DatabaseUtils.getInstance ( context ).getDaoSession ().getItemsDao ().queryBuilder ()
		 	   			        .where ( ItemsDao.Properties.ItemCode.eq (  tr.getItemCode()   ) ).unique().getUnitMediumSmall()	,	   tr.getQuantityMedium().intValue()+"/" +  tr.getQuantitySmall().intValue()  ,
		 	   				     moneyFormat.format ( tr.getPriceMedium() ),	moneyFormat.format ( tr.getPriceSmall () ) , moneyFormat.format ( tr.getTotalLineAmount () ) ) + NEWLINE );

			 
			 quantityM = quantityM+tr.getQuantityMedium();
			 quantityS = quantityS+tr.getQuantitySmall();
			 totalSales = totalSales+tr.getTotalLineAmount ();
		
			if(count % 36 == 0 && count != 0    )  
			{  
				
			   	body.append ( NEWLINE );
			   	body.append ("                      Continued   "+ NEWLINE);
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    
			    subHeader = new StringBuilder ();
			    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
			    
			    subHeader.append ( " "+ISSUE_DATE +subString ( transactionHeader.getIssueDate ().toString()  , ISSUE_DATE_Length )  + NEWLINE );
			    subHeader.append (  NEWLINE);
			
				pageNumber =pageNumber +  1;
				// body.append ( pageNumber );
				 
			        	subHeader.append ( CLIENT_ID +subString ( client.getClientCode () ,CLIENT_ID_length) );
				    	subHeader.append ( " "+ subString (client.getClientName ()  ,CLIENT_Name_length) + NEWLINE );
				     	subHeader.append (  "                "+   client.getClientAddress ()    + NEWLINE );
				     
				     	 
				     		  	subHeader.append ("             RETURN   ");
					    		subHeader.append (  transactionHeader.getTransactionCode () + NEWLINE );	 
				     	  
				     	subHeader.append (  NEWLINE);
				     	subHeader.append ( PRINT_DATE + now.getTime ()   + NEWLINE );
				       	subHeader.append ( HORIZANTAL_LINE + NEWLINE );
						subHeader.append ( OI_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
					    body.append(subHeader);
			 
			}
		         		 count=count+1;
		} // End for loop
	  
		body.append ( HORIZANTAL_LINE + NEWLINE );
		body.append ( "                            SUB - TOTAL SALES  :"+(int)quantityM  +"/"+(int)quantityS  +"            "+ moneyFormat.format (totalSales)+NEWLINE );
	 
		
	
		if(transactionDetailsF.size() > 0){
			 quantityM = 0d;
	         quantityS = 0d;
	         totalSales= 0d;
		
		body.append (  HORIZANTAL_LINE + NEWLINE);
		body.append ( "                       FREE GOODS"+ NEWLINE);	
		count=count+6;
		 
		for( int i = 0 ; i < transactionDetailsF.size () ; i ++ ) {
	   			
  			TransactionDetails tr = transactionDetailsF.get ( i ); 
   	 	
  			body.append ( String.format ( "%-"+(CODE_LENGTH-2)+"s %"+(TOTAL_LENGTH+7)+"s %"+(uom_LENGTH-1) +"s %"+ (QUANTITY_LENGTH -2)+ "s %"+price_LENGTH+"s%" + UNIT_PRICE_LENGTH + "s %" + (LINE_AMOUNT_LENGTH - 2) + "s" , 
	   				    tr.getItemCode ().length () > CODE_LENGTH ? tr.getItemCode ().substring ( 0 , CODE_LENGTH ) : tr.getItemCode ()	
	   				    , tr.getItemName ().length () >  (TOTAL_LENGTH+7) ? tr.getItemName ().substring ( 0 , (TOTAL_LENGTH+7)  ) : tr.getItemName (),	
	   				    DatabaseUtils.getInstance ( context ).getDaoSession ().getItemsDao ().queryBuilder ()
	   			        .where ( ItemsDao.Properties.ItemCode.eq (  tr.getItemCode()   ) ).unique().getUnitMediumSmall()	,	   tr.getQuantityMedium().intValue()+"/" +  tr.getQuantitySmall().intValue()  ,
	   				     moneyFormat.format ( tr.getPriceMedium() ),	moneyFormat.format ( tr.getPriceSmall () ) , moneyFormat.format ( tr.getTotalLineAmount () ) ) + NEWLINE );


		         quantityM = quantityM + tr.getQuantityMedium();
		         quantityS = quantityS + tr.getQuantitySmall();
		         totalSales = totalSales + tr.getTotalLineAmount ();
        
         if(count % 36 == 0 && count != 0    )  
			{  
				body.append ( NEWLINE );
			   	body.append ("                      Continued   "+ NEWLINE);
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    
			    subHeader = new StringBuilder ();
			    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
			    
			    subHeader.append ( " "+ISSUE_DATE +subString ( transactionHeader.getIssueDate ().toString()  , ISSUE_DATE_Length )  + NEWLINE );
			    subHeader.append (  NEWLINE);
				
				pageNumber =pageNumber +  1;
				// body.append ( pageNumber );
				 
	        	subHeader.append ( CLIENT_ID +subString ( client.getClientCode () ,CLIENT_ID_length) );
		    	subHeader.append ( " "+ subString (client.getClientName ()  ,CLIENT_Name_length) + NEWLINE );
		     	subHeader.append (  "                "+   client.getClientAddress ()    + NEWLINE );
				     
				    
					      subHeader.append ("             RETURN    " );
					      subHeader.append (   transactionHeader.getTransactionCode () + NEWLINE );	
					     
				     	subHeader.append (  NEWLINE);
				     	subHeader.append ( PRINT_DATE + now.getTime ()   + NEWLINE );
				       	subHeader.append ( HORIZANTAL_LINE + NEWLINE );
						subHeader.append ( OI_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
					    body.append(subHeader);
			 
			}
		         		 count=count+1;
		
		
	} // End for loop
		body.append ( HORIZANTAL_LINE + NEWLINE );
		body.append ( "                            SUB - TOTAL SALES  :"+(int)quantityM  +"/"+(int)quantityS  +"            "+ moneyFormat.format (totalSales)+NEWLINE );
	   
    }
	 
		
			body.append ( "     NET SALES:                      " + moneyFormat.format ( transactionHeader.getTotalTaxAmount () ) + " " + transactionHeader.getCurrencyCode () + NEWLINE );
		 
		 
	 
		
		 
		if( transactionHeader.getPrintingTimes() == null || transactionHeader.getPrintingTimes() == 1 )
		{	
			body.append ("-------------------------------- Original ------------------------------ " +NEWLINE);
		    //collectionHeaders.setPrintingTimes(1);
			//DatabaseUtils.getInstance ( context ).getDaoSession ().getCollectionHeadersDao() .update(collectionHeaders);
		}
	 	else
	 	{
	 		body.append ("------------------------------ Reprint -------------------------------  " +NEWLINE);
		 
	 	}
		//body.append(  HORIZANTAL_LINE + NEWLINE);
		//body.append ( "SALES REP                  Last Page                        CUSTOMER SIGN/STAMP" );
		int lastPage1 = ( count + 3 ) / 36;
		int rest1 = ( count + 3 ) % 36; 
		 
		 
		 if(pageNumber == ( lastPage1 + 1 )   )
	      {
			 if(pageNumber==1){
  	  				if(( 36 - (count+3) - 3 )>0)
  				{	    
  	  			for(int u=0 ; u< 36-(count+3)  - 3 ;u++)
  	  			body.append ( NEWLINE );
  				}}
  	  		else{
  	  		if(rest1>4)
  	  		for(int u=0 ; u< 36-rest1-3 ;u++)
  	  		body.append ( NEWLINE );
  	  			}
			}
		 body.append ( NEWLINE );
		 body.append(  HORIZANTAL_LINE + NEWLINE);
		 body.append ( "SALES REP                  Last Page                        CUSTOMER SIGN/STAMP" );
		 } 
		 if ( type == Type.INVOICE    ) 
	     {
		  ArrayList< TransactionDetails>  transactionDetailsO=new ArrayList<TransactionDetails>();
	      ArrayList< TransactionDetails>  transactionDetailsF=new ArrayList<TransactionDetails>();
 	     	for( int i = 0 ; i < transactionDetails.size () ; i ++ ) {
		 
 	     			TransactionDetails tr = transactionDetails.get ( i );
// 	     			if(tr.getOrderedType().equals("O") )
// 	     				transactionDetailsO.add(tr);
// 	     			else
// 	     				transactionDetailsF.add(tr);
 	     			if(tr.getDiscountPercentage() == 100)
 	     			{
 	     				transactionDetailsF.add( tr );
 	     			}
 	     			else
 	     			{
 	     				transactionDetailsO.add( tr );	 	     				
 	     			}
 	     		}
 	     		
 	     		for( int i = 0 ; i < transactionDetailsO.size () ; i ++ ) {
 	   			
	 	     		 
	 	     			TransactionDetails tr = transactionDetailsO.get ( i ); 
	 	      	 	
	 	     			body.append ( String.format ( "%-"+(CODE_LENGTH-2)+"s %"+(TOTAL_LENGTH+7)+"s %"+(uom_LENGTH-1) +"s %"+ (QUANTITY_LENGTH -2)+ "s %"+price_LENGTH+"s%" + UNIT_PRICE_LENGTH + "s %" + (LINE_AMOUNT_LENGTH - 2) + "s" , 
	 	   				    tr.getItemCode ().length () > CODE_LENGTH ? tr.getItemCode ().substring ( 0 , CODE_LENGTH ) : tr.getItemCode ()	
	 	   				    , tr.getItemName ().length () >  (TOTAL_LENGTH+7) ? tr.getItemName ().substring ( 0 , (TOTAL_LENGTH+7)  ) : tr.getItemName (),	
	 	   				    DatabaseUtils.getInstance ( context ).getDaoSession ().getItemsDao ().queryBuilder ()
	 	   			        .where ( ItemsDao.Properties.ItemCode.eq (  tr.getItemCode()   ) ).unique().getUnitMediumSmall()	,	   tr.getQuantityMedium().intValue()+"/" +  tr.getQuantitySmall().intValue()  ,
	 	   				     moneyFormat.format ( tr.getPriceMedium() ),	moneyFormat.format ( tr.getPriceSmall () ) , moneyFormat.format ( tr.getTotalLineAmount () ) ) + NEWLINE );

		 
		 quantityM = quantityM+tr.getQuantityMedium();
		 quantityS = quantityS+tr.getQuantitySmall();
		 totalSales = totalSales+tr.getTotalLineAmount ();
	
		if(count % 36 == 0 && count != 0    )  
		{  
			
		   	body.append ( NEWLINE );
		   	body.append ("                      Continued   "+ NEWLINE);
		    body.append ( NEWLINE );
		    body.append ( NEWLINE );
		    body.append ( NEWLINE );
		    body.append ( NEWLINE );
		    body.append ( NEWLINE );
		    body.append ( NEWLINE );
		    
		    body.append ( NEWLINE );
		    body.append ( NEWLINE );
		    body.append ( NEWLINE );
		    body.append ( NEWLINE );
		    body.append ( NEWLINE );
		    body.append ( NEWLINE );
		    body.append ( NEWLINE );
		    body.append ( NEWLINE );
		    body.append ( NEWLINE );
		    body.append ( NEWLINE );
		    body.append ( NEWLINE );
		    
		    subHeader = new StringBuilder ();
		    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
		    
		    subHeader.append ( " "+ISSUE_DATE +subString ( transactionHeader.getIssueDate ().toString()  , ISSUE_DATE_Length )  + NEWLINE );
		    subHeader.append (  NEWLINE);
		
			pageNumber =pageNumber +  1;
			// body.append ( pageNumber );
			 
		        	subHeader.append ( CLIENT_ID +subString ( client.getClientCode () ,CLIENT_ID_length) );
			    	subHeader.append ( " "+ subString (client.getClientName ()  ,CLIENT_Name_length) + NEWLINE );
			     	subHeader.append (  "                "+   client.getClientAddress ()    + NEWLINE );
			     
			     	  if(client.getClientType()==1){
			     		  	subHeader.append ("             CASH INVOICE    ");
				    		subHeader.append (  transactionHeader.getTransactionCode () + NEWLINE );	 
			     	  }
				       else
				       {
				    	   if(transactionHeader.getInfo1().equals("1")  || transactionHeader.getInfo2().equals("1"))
			    		   subHeader.append ("             CASH INVOICE    ");
			    	   else
			    		   subHeader.append ("             CREDIT INVOICE    ");
				    	 
				    	   subHeader.append (   transactionHeader.getTransactionCode () + NEWLINE );	 
				       }
			     	subHeader.append (  NEWLINE);
			     	subHeader.append ( PRINT_DATE + now.getTime ()   + NEWLINE );
			       	subHeader.append ( HORIZANTAL_LINE + NEWLINE );
					subHeader.append ( OI_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
				    body.append(subHeader);
		 
		}
	         		 count=count+1;
	} // End for loop
  
	body.append ( HORIZANTAL_LINE + NEWLINE );
	body.append ( "                            SUB - TOTAL SALES  :"+(int)quantityM  +"/"+(int)quantityS  +"            "+ moneyFormat.format (totalSales)+NEWLINE );
 
	

	if(transactionDetailsF.size() > 0){
		 quantityM = 0d;
         quantityS = 0d;
         totalSales= 0d;
	
	body.append (  HORIZANTAL_LINE + NEWLINE);
	body.append ( "                       FREE GOODS"+ NEWLINE);	
	count=count+6;
	 
	for( int i = 0 ; i < transactionDetailsF.size () ; i ++ ) {
   			
			TransactionDetails tr = transactionDetailsF.get ( i ); 
	 	
			body.append ( String.format ( "%-"+(CODE_LENGTH-2)+"s %"+(TOTAL_LENGTH+7)+"s %"+(uom_LENGTH-1) +"s %"+ (QUANTITY_LENGTH -2)+ "s %"+price_LENGTH+"s%" + UNIT_PRICE_LENGTH + "s %" + (LINE_AMOUNT_LENGTH - 2) + "s" , 
   				    tr.getItemCode ().length () > CODE_LENGTH ? tr.getItemCode ().substring ( 0 , CODE_LENGTH ) : tr.getItemCode ()	
   				    , tr.getItemName ().length () >  (TOTAL_LENGTH+7) ? tr.getItemName ().substring ( 0 , (TOTAL_LENGTH+7)  ) : tr.getItemName (),	
   				    DatabaseUtils.getInstance ( context ).getDaoSession ().getItemsDao ().queryBuilder ()
   			        .where ( ItemsDao.Properties.ItemCode.eq (  tr.getItemCode()   ) ).unique().getUnitMediumSmall()	,	   tr.getQuantityMedium().intValue()+"/" +  tr.getQuantitySmall().intValue()  ,
   				     moneyFormat.format ( tr.getPriceMedium() ),	moneyFormat.format ( tr.getPriceSmall () ) , moneyFormat.format ( tr.getTotalLineAmount () ) ) + NEWLINE );


	         quantityM = quantityM + tr.getQuantityMedium();
	         quantityS = quantityS + tr.getQuantitySmall();
	         totalSales = totalSales + tr.getTotalLineAmount ();
    
     if(count % 36 == 0 && count != 0    )  
		{  
			body.append ( NEWLINE );
		   	body.append ("                      Continued   "+ NEWLINE);
		    body.append ( NEWLINE );
		    body.append ( NEWLINE );
		    body.append ( NEWLINE );
		    body.append ( NEWLINE );
		    body.append ( NEWLINE );
		    body.append ( NEWLINE );
		    
		    body.append ( NEWLINE );
		    body.append ( NEWLINE );
		    body.append ( NEWLINE );
		    body.append ( NEWLINE );
		    body.append ( NEWLINE );
		    body.append ( NEWLINE );
		    body.append ( NEWLINE );
		    body.append ( NEWLINE );
		    body.append ( NEWLINE );
		    body.append ( NEWLINE );
		    body.append ( NEWLINE );
		    
		    subHeader = new StringBuilder ();
		    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
		    
		    subHeader.append ( " "+ISSUE_DATE +subString ( transactionHeader.getIssueDate ().toString()  , ISSUE_DATE_Length )  + NEWLINE );
		    subHeader.append (  NEWLINE);
			
			pageNumber =pageNumber +  1;
			// body.append ( pageNumber );
			 
        	subHeader.append ( CLIENT_ID +subString ( client.getClientCode () ,CLIENT_ID_length) );
	    	subHeader.append ( " "+ subString (client.getClientName ()  ,CLIENT_Name_length) + NEWLINE );
	     	subHeader.append (  "                "+   client.getClientAddress ()    + NEWLINE );
			     
			     if(client.getClientType() == 1){
				      subHeader.append ("             CASH INVOICE    " );
				      subHeader.append (   transactionHeader.getTransactionCode () + NEWLINE );	
				    	   }
				       else 
				    	   {
				    	   subHeader.append ("             CREDIT INVOICE    "); 
				    	   subHeader.append (   transactionHeader.getTransactionCode () + NEWLINE );	
				    	   }
			     	subHeader.append (  NEWLINE);
			     	subHeader.append ( PRINT_DATE + now.getTime ()   + NEWLINE );
			       	subHeader.append ( HORIZANTAL_LINE + NEWLINE );
					subHeader.append ( OI_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
				    body.append(subHeader);
		 
		}
	         		 count=count+1;
	
	
} // End for loop
	body.append ( HORIZANTAL_LINE + NEWLINE );
	body.append ( "                            SUB - TOTAL SALES  :"+(int)quantityM  +"/"+(int)quantityS  +"            "+ moneyFormat.format (totalSales)+NEWLINE );
   
}
	Double remainingAmount=0d;
	if(transactionHeader.getRemainingAmount()>0)
	{	
		String SQL=null;
		String selectionArguments [] = null;
		SQL = "  select SUM(RemainingAmount)  from ClientDues where   ClientCode=? and companycode=?  ";
	 
	// Compute the selection arguments
		selectionArguments = new String [] {	   
				String.valueOf (transactionHeader.getClientCode()),
				String.valueOf (transactionHeader.getCompanyCode())
		};
		Cursor cursor = DatabaseUtils.getInstance ( context ).getDaoSession ().getDatabase ()
				        .rawQuery(SQL , selectionArguments) ;
	
			// Move the cursor to the first raw
		if ( cursor.moveToFirst () ) {
			// Iterate over all raws
			do {
				remainingAmount= cursor.getDouble(0);
			} while ( cursor.moveToNext () );
		} // End if
		// Close the cursor
		cursor.close ();
		cursor = null;
		}
	if(client.getClientType() == 1){
	
		body.append ( "     NET SALES:                      " + moneyFormat.format ( transactionHeader.getTotalTaxAmount () ) + " " + transactionHeader.getCurrencyCode () + NEWLINE );
	//  body.append ( "     CREDIT:                         " + moneyFormat.format ( transactionHeader.getRemainingAmount() ) + " " + transactionHeader.getCurrencyCode () + NEWLINE   );
    //  body.append ( "     CURRENT OUTSTANDING BALANCE     " + moneyFormat.format ( transactionHeader.getTotalTaxAmount() ) + " " + transactionHeader.getCurrencyCode () + NEWLINE );
	}
	else{
		body.append ( "     NET SALES:                      " + moneyFormat.format ( transactionHeader.getTotalTaxAmount () ) + " " + transactionHeader.getCurrencyCode () + NEWLINE );
	    body.append ( "     CREDIT:                         " + moneyFormat.format ( transactionHeader.getRemainingAmount() ) + " " + transactionHeader.getCurrencyCode () + NEWLINE   );
    	body.append ( "     CURRENT OUTSTANDING BALANCE     " + moneyFormat.format ( transactionHeader.getTotalTaxAmount() + remainingAmount ) + " " + transactionHeader.getCurrencyCode () + NEWLINE );
	
	}
	if(client.getClientType()==1){
		count = count + 6;
		if ( PermissionsUtils.getEnableInvoicePrint ( context ,   DatabaseUtils.getCurrentUserCode ( context ) ,  DatabaseUtils.getCurrentCompanyCode(context) ) ) {
			{
				body.append (  NEWLINE+"                Company Shall not responsible for "+NEWLINE);
				body.append ( "                any payment made without an official "+NEWLINE);
				body.append ( "                 receipt. In Cash invoices only the invoice "+NEWLINE);
				body.append ( "                  itself is the receipt"+NEWLINE +NEWLINE);
			}
		
		}
		}
	
	 
	if( transactionHeader.getPrintingTimes() == null || transactionHeader.getPrintingTimes() == 1 )
	{	
		body.append ("-------------------------------- Original ------------------------------ " +NEWLINE);
	    //collectionHeaders.setPrintingTimes(1);
		//DatabaseUtils.getInstance ( context ).getDaoSession ().getCollectionHeadersDao() .update(collectionHeaders);
	}
 	else
 	{
 		body.append ("------------------------------ Reprint -------------------------------  " +NEWLINE);
	 
 	}
	//body.append(  HORIZANTAL_LINE + NEWLINE);
	//body.append ( "SALES REP                  Last Page                        CUSTOMER SIGN/STAMP" );
	int lastPage1 = ( count + 3 ) / 36;
	int rest1 = ( count + 3 ) % 36; 
	 
	 
	 if(pageNumber == ( lastPage1 + 1 )   )
      {
		 if(pageNumber==1){
	  				if(( 36 - (count+3) - 3 )>0)
				{	    
	  			for(int u=0 ; u< 36-(count+3)  - 3 ;u++)
	  			body.append ( NEWLINE );
				}}
	  		else{
	  		if(rest1>4)
	  		for(int u=0 ; u< 36-rest1-3 ;u++)
	  		body.append ( NEWLINE );
	  			}
		}
	 body.append ( NEWLINE );
	 body.append(  HORIZANTAL_LINE + NEWLINE);
	 body.append ( "SALES REP                  Last Page                        CUSTOMER SIGN/STAMP" );
	 } 
		 if (type == Type.INVOICE_DISCOUNT ){

			  ArrayList< TransactionDetails>  transactionDetailsO=new ArrayList<TransactionDetails>();
		      ArrayList< TransactionDetails>  transactionDetailsF=new ArrayList<TransactionDetails>();
	 	     	for( int i = 0 ; i < transactionDetails.size () ; i ++ ) {
			 
	 	     			TransactionDetails tr = transactionDetails.get ( i );
//	 	     			if(tr.getOrderedType().equals("O") )
//	 	     				transactionDetailsO.add(tr);
//	 	     			else
//	 	     				transactionDetailsF.add(tr);
	 	     			if(tr.getDiscountPercentage() == 100)
	 	     			{
	 	     				transactionDetailsF.add( tr );
	 	     			}
	 	     			else
	 	     			{
	 	     				transactionDetailsO.add( tr );	 	     				
	 	     			}
	 	     		}
	 	     		
	 	     		for( int i = 0 ; i < transactionDetailsO.size () ; i ++ ) {
	 	   			
		 	     		 
		 	     			TransactionDetails tr = transactionDetailsO.get ( i ); 
		 	      	 	
		 	     			body.append ( String.format ( "%-"+(CODE_LENGTH-2)+"s %"+(TOTAL_LENGTH+2)+"s %"+(uom_LENGTH-1) +"s %"+ (QUANTITY_LENGTH -2)+ "s %"+price_LENGTH+"s%" + UNIT_PRICE_LENGTH + "s %" + DISCOUNT_LENGTH + "s %" + (LINE_AMOUNT_LENGTH - 2) + "s" , 
		 	   				    tr.getItemCode ().length () > CODE_LENGTH ? tr.getItemCode ().substring ( 0 , CODE_LENGTH ) : tr.getItemCode ()	
		 	   				    , tr.getItemName ().length () >  (TOTAL_LENGTH+2) ? tr.getItemName ().substring ( 0 , (TOTAL_LENGTH+2)  ) : tr.getItemName (),	
		 	   				    DatabaseUtils.getInstance ( context ).getDaoSession ().getItemsDao ().queryBuilder ()
		 	   			        .where ( ItemsDao.Properties.ItemCode.eq (  tr.getItemCode()   ) ).unique().getUnitMediumSmall()	,	   tr.getQuantityMedium().intValue()+"/" +  tr.getQuantitySmall().intValue()  ,
		 	   				     moneyFormat.format ( tr.getPriceMedium() ),	moneyFormat.format ( tr.getPriceSmall () ) , tr.getDiscountPercentage() , moneyFormat.format ( tr.getTotalLineAmount () ) ) + NEWLINE );

			 
			 quantityM = quantityM+tr.getQuantityMedium();
			 quantityS = quantityS+tr.getQuantitySmall();
			 totalSales = totalSales+tr.getTotalLineAmount ();
			    
			if(count % 36 == 0 && count != 0    )  
			{  
				
			   	body.append ( NEWLINE );
			   	body.append ("                      Continued   "+ NEWLINE);
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    
			    subHeader = new StringBuilder ();
			    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
			    
			    subHeader.append ( " "+ISSUE_DATE +subString ( transactionHeader.getIssueDate ().toString()  , ISSUE_DATE_Length )  + NEWLINE );
			    subHeader.append (  NEWLINE);
			
				pageNumber =pageNumber +  1;
				// body.append ( pageNumber );
				 
			        	subHeader.append ( CLIENT_ID +subString ( client.getClientCode () ,CLIENT_ID_length) );
				    	subHeader.append ( " "+ subString (client.getClientName ()  ,CLIENT_Name_length) + NEWLINE );
				     	subHeader.append (  "                "+   client.getClientAddress ()    + NEWLINE );
				     
				     	  if(client.getClientType()==1){
				     		  	subHeader.append ("             CASH INVOICE    ");
					    		subHeader.append (  transactionHeader.getTransactionCode () + NEWLINE );	 
				     	  }
					       else
					       {
					    	   if(transactionHeader.getInfo1().equals("1")  || transactionHeader.getInfo2().equals("1"))
				    		   subHeader.append ("             CASH INVOICE    ");
				    	   else
				    		   subHeader.append ("             CREDIT INVOICE    ");
					    	 
					    	   subHeader.append (   transactionHeader.getTransactionCode () + NEWLINE );	 
					       }
				     	subHeader.append (  NEWLINE);
				     	subHeader.append ( PRINT_DATE + now.getTime ()   + NEWLINE );
				       	subHeader.append ( HORIZANTAL_LINE + NEWLINE );
						subHeader.append ( OI_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
					    body.append(subHeader);
			 
			}
		         		 count=count+1;
		} // End for loop
	  
		body.append ( HORIZANTAL_LINE + NEWLINE );
		body.append ( "                            SUB - TOTAL SALES  :"+(int)quantityM  +"/"+(int)quantityS  +"            "+ moneyFormat.format (totalSales)+NEWLINE );
	 
		
	
		if(transactionDetailsF.size() > 0){
			 quantityM = 0d;
	         quantityS = 0d;
	         totalSales= 0d;
		
		body.append (  HORIZANTAL_LINE + NEWLINE);
		body.append ( "                       FREE GOODS"+ NEWLINE);	
		count=count+6;
		 
		for( int i = 0 ; i < transactionDetailsF.size () ; i ++ ) {
	   			
			TransactionDetails tr = transactionDetailsF.get ( i ); 
 	 	
			body.append ( String.format ( "%-"+(CODE_LENGTH-2)+"s %"+(TOTAL_LENGTH+2)+"s %"+(uom_LENGTH-1) +"s %"+ (QUANTITY_LENGTH -2)+ "s %"+price_LENGTH+"s%" + UNIT_PRICE_LENGTH + "s %" + DISCOUNT_LENGTH + "s %" + (LINE_AMOUNT_LENGTH - 2) + "s" , 
	   				    tr.getItemCode ().length () > CODE_LENGTH ? tr.getItemCode ().substring ( 0 , CODE_LENGTH ) : tr.getItemCode ()	
	   				    , tr.getItemName ().length () >  (TOTAL_LENGTH+2) ? tr.getItemName ().substring ( 0 , (TOTAL_LENGTH+2)  ) : tr.getItemName (),	
	   				    DatabaseUtils.getInstance ( context ).getDaoSession ().getItemsDao ().queryBuilder ()
	   			        .where ( ItemsDao.Properties.ItemCode.eq (  tr.getItemCode()   ) ).unique().getUnitMediumSmall()	,	   tr.getQuantityMedium().intValue()+"/" +  tr.getQuantitySmall().intValue()  ,
	   				     moneyFormat.format ( tr.getPriceMedium() ),	moneyFormat.format ( tr.getPriceSmall () ) , tr.getDiscountPercentage() , moneyFormat.format ( tr.getTotalLineAmount () ) ) + NEWLINE );


		         quantityM = quantityM + tr.getQuantityMedium();
		         quantityS = quantityS + tr.getQuantitySmall();
		         totalSales = totalSales + tr.getTotalLineAmount ();
      
       if(count % 36 == 0 && count != 0    )  
			{  
				body.append ( NEWLINE );
			   	body.append ("                      Continued   "+ NEWLINE);
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    body.append ( NEWLINE );
			    
			    subHeader = new StringBuilder ();
			    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
			    
			    subHeader.append ( " "+ISSUE_DATE +subString ( transactionHeader.getIssueDate ().toString()  , ISSUE_DATE_Length )  + NEWLINE );
			    subHeader.append (  NEWLINE);
				
				pageNumber =pageNumber +  1;
				// body.append ( pageNumber );
				 
	        	subHeader.append ( CLIENT_ID +subString ( client.getClientCode () ,CLIENT_ID_length) );
		    	subHeader.append ( " "+ subString (client.getClientName ()  ,CLIENT_Name_length) + NEWLINE );
		     	subHeader.append (  "                "+   client.getClientAddress ()    + NEWLINE );
				     
				     if(client.getClientType() == 1){
					      subHeader.append ("             CASH INVOICE    " );
					      subHeader.append (   transactionHeader.getTransactionCode () + NEWLINE );	
					    	   }
					       else 
					    	   {
					    	   subHeader.append ("             CREDIT INVOICE    "); 
					    	   subHeader.append (   transactionHeader.getTransactionCode () + NEWLINE );	
					    	   }
				     	subHeader.append (  NEWLINE);
				     	subHeader.append ( PRINT_DATE + now.getTime ()   + NEWLINE );
				       	subHeader.append ( HORIZANTAL_LINE + NEWLINE );
						subHeader.append ( OI_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
					    body.append(subHeader);
			 
			}
		         		 count=count+1;
		
		
	} // End for loop
		body.append ( HORIZANTAL_LINE + NEWLINE );
		body.append ( "                            SUB - TOTAL SALES  :"+(int)quantityM  +"/"+(int)quantityS  +"            "+ moneyFormat.format (totalSales)+NEWLINE );
	   
  }
		Double remainingAmount=0d;
		if(transactionHeader.getRemainingAmount()>0)
		{	String SQL=null;
			String selectionArguments [] = null;
			SQL = "  select SUM(RemainingAmount)  from ClientDues where   ClientCode=? and companycode=?  ";
		 
		// Compute the selection arguments
			selectionArguments = new String [] {	   
					String.valueOf (transactionHeader.getClientCode()),
					String.valueOf (transactionHeader.getCompanyCode())
			};
			Cursor cursor = DatabaseUtils.getInstance ( context ).getDaoSession ().getDatabase ()
					        .rawQuery(SQL , selectionArguments) ;
		
				// Move the cursor to the first raw
			if ( cursor.moveToFirst () ) {
				// Iterate over all raws
				do {
					remainingAmount= cursor.getDouble(0);
				} while ( cursor.moveToNext () );
			} // End if
			// Close the cursor
			cursor.close ();
			cursor = null;
			}
		if(client.getClientType() == 1){
			body.append ( "     DISCOUNT AMOUNT:                " + moneyFormat.format ( transactionHeader.getDiscountAmount() ) + " " + transactionHeader.getCurrencyCode () + NEWLINE );
			body.append ( "     NET SALES:                      " + moneyFormat.format ( transactionHeader.getTotalTaxAmount() ) + " " + transactionHeader.getCurrencyCode () + NEWLINE );
		//  body.append ( "     CREDIT:                         " + moneyFormat.format ( transactionHeader.getRemainingAmount() ) + " " + transactionHeader.getCurrencyCode () + NEWLINE   );
	    //  body.append ( "     CURRENT OUTSTANDING BALANCE     " + moneyFormat.format ( transactionHeader.getTotalTaxAmount() ) + " " + transactionHeader.getCurrencyCode () + NEWLINE );
		}
		else{
			body.append ( "     DISCOUNT AMOUNT:                " + moneyFormat.format ( transactionHeader.getDiscountAmount() ) + " " + transactionHeader.getCurrencyCode () + NEWLINE );
			body.append ( "     NET SALES:                      " + moneyFormat.format ( transactionHeader.getTotalTaxAmount () ) + " " + transactionHeader.getCurrencyCode () + NEWLINE );
		    body.append ( "     CREDIT:                         " + moneyFormat.format ( transactionHeader.getRemainingAmount() ) + " " + transactionHeader.getCurrencyCode () + NEWLINE   );
	    	body.append ( "     CURRENT OUTSTANDING BALANCE     " + moneyFormat.format ( transactionHeader.getTotalTaxAmount() + remainingAmount ) + " " + transactionHeader.getCurrencyCode () + NEWLINE );
		
		}
			count += 1;
		if(client.getClientType()==1){
			count = count + 6;
			if ( PermissionsUtils.getEnableInvoicePrint ( context ,   DatabaseUtils.getCurrentUserCode ( context ) ,  DatabaseUtils.getCurrentCompanyCode(context) ) ) {
				{
					body.append (  NEWLINE+"                Company Shall not responsible for "+NEWLINE);
					body.append ( "                any payment made without an official "+NEWLINE);
					body.append ( "                 receipt. In Cash invoices only the invoice "+NEWLINE);
					body.append ( "                  itself is the receipt"+NEWLINE +NEWLINE);
				}
			
			}
			}	
		//body.append(  HORIZANTAL_LINE + NEWLINE);
		//body.append ( "SALES REP                  Last Page                        CUSTOMER SIGN/STAMP" );
		int lastPage1 = ( count + 3 ) / 36;
		int rest1 = ( count + 3 ) % 36; 
		 
		 
		 if(pageNumber == ( lastPage1 + 1 )   )
	      {
			 if(pageNumber==1){
	  				if(( 36 - (count+3) - 3 )>0)
				{	    
	  			for(int u=0 ; u< 36-(count+3)  - 3 ;u++)
	  			body.append ( NEWLINE );
				}}
	  		else{
	  		if(rest1>4)
	  		for(int u=0 ; u< 36-rest1-3 ;u++)
	  		body.append ( NEWLINE );
	  			}
			}
		 body.append ( NEWLINE );
		 body.append(  HORIZANTAL_LINE + NEWLINE);
		 body.append ( "SALES REP                  Last Page                        CUSTOMER SIGN/STAMP" );
		 
		 }
//			footer = new StringBuilder ();
//			if ( type == Type.INVOICE  ) {
//			
//			 
//			} // End if
			
		
		
		
//		if ( type == Type.INVOICE   ) {
//		 	
//			for( int i = 0 ; i < transactionDetails.size () ; i ++ ) {
//				TransactionDetails tr = transactionDetails.get ( i );
//				if(tr.getOrderedType().equals("F") ){
//					body.append ( String.format ( "%-"+CODE_LENGTH+"s %"+(TOTAL_LENGTH+4)+"s %"+uom_LENGTH +"s %"+ (QUANTITY_LENGTH +2)+ "s %"+price_LENGTH+"s%" + UNIT_PRICE_LENGTH + "s %" + LINE_AMOUNT_LENGTH + "s" , 
//							tr.getItemCode ().length () > CODE_LENGTH ? tr.getItemCode ().substring ( 0 , CODE_LENGTH ) : tr.getItemCode ()	, tr.getItemName ().length () >  (TOTAL_LENGTH+4)  ? tr.getItemName ().substring ( 0 ,  (TOTAL_LENGTH+4)  ) : tr.getItemName ()
//							, DatabaseUtils.getInstance ( context ).getDaoSession ().getItemsDao ().queryBuilder ()
//							.where ( ItemsDao.Properties.ItemCode.eq (  tr.getItemCode()   ) ).unique().getUnitMediumSmall()	,	 tr.getQuantityMedium().toString ()+"/" +tr.getQuantitySmall(),
//							moneyFormat.format ( tr.getPriceMedium() ),	moneyFormat.format ( tr.getPriceSmall () ) , moneyFormat.format ( tr.getTotalLineAmount () ) )+"F " + NEWLINE );
//					
//				}else{
//					body.append ( String.format ( "%-"+CODE_LENGTH+"s %"+(TOTAL_LENGTH+4)+"s %"+uom_LENGTH +"s %"+ (QUANTITY_LENGTH +2)+ "s %"+price_LENGTH+"s%" + UNIT_PRICE_LENGTH + "s %" + LINE_AMOUNT_LENGTH + "s" , 
//							  tr.getItemCode ().length () > CODE_LENGTH ? tr.getItemCode ().substring ( 0 , CODE_LENGTH ) : tr.getItemCode ()	
//							  , tr.getItemName ().length () >  (TOTAL_LENGTH+4) ? tr.getItemName ().substring ( 0 , (TOTAL_LENGTH+4)  ) : tr.getItemName (),	
//							  DatabaseUtils.getInstance ( context ).getDaoSession ().getItemsDao ().queryBuilder ()
//						      .where ( ItemsDao.Properties.ItemCode.eq (  tr.getItemCode()   ) ).unique().getUnitMediumSmall()	,	 tr.getQuantityMedium().toString ()+"/" +tr.getQuantitySmall(),
//							   moneyFormat.format ( tr.getPriceMedium() ),	moneyFormat.format ( tr.getPriceSmall () ) , moneyFormat.format ( tr.getTotalLineAmount () ) ) + NEWLINE );
//				}
//				 quantityM= quantityM+tr.getQuantityMedium();
//				 quantityS=quantityS+tr.getQuantitySmall();
//				 totalSales=totalSales+tr.getTotalLineAmount ();
//			
//				if(count % 36 == 0 && count != 0    )  
//				{  
//					
//				   	body.append ( NEWLINE );
//					body.append ( NEWLINE );
//				    body.append ( NEWLINE );
//				    body.append ( NEWLINE );
//				    body.append ( NEWLINE );
//				    body.append ( NEWLINE );
//				    body.append ( NEWLINE );
//				    
//				    body.append ( NEWLINE );
//				    body.append ( NEWLINE );
//				    body.append ( NEWLINE );
//				    body.append ( NEWLINE );
//				    body.append ( NEWLINE );
//				    body.append ( NEWLINE );
//				    body.append ( NEWLINE );
//				    body.append ( NEWLINE );
//				    body.append ( NEWLINE );
//				    body.append ( NEWLINE );
//				    body.append ( NEWLINE );
//				    
//				    subHeader = new StringBuilder ();
//				    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
//				    
//				    subHeader.append ( " "+ISSUE_DATE +subString ( transactionHeader.getIssueDate ().toString()  , ISSUE_DATE_Length )  + NEWLINE );
//				    subHeader.append (  NEWLINE);
//					subHeader.append ( ORDER_NUM + transactionHeader.getTransactionCode () + NEWLINE );	
//					pageNumber =pageNumber+  1;
//					// body.append ( pageNumber );
//					   if ( copyType == 0)
//					    		subHeader.append ( RECEPIT + NEWLINE );
//					  	else 
//							subHeader.append ( RECEPIT_COPY + NEWLINE );
//				        	subHeader.append ( CLIENT_ID +subString ( client.getClientCode () ,CLIENT_ID_length) );
//					    	subHeader.append ( " "+ subString (client.getClientName ()  ,CLIENT_Name_length) + NEWLINE );
//					     	subHeader.append (  "                "+   client.getClientAddress ()    + NEWLINE );
//					     
//					     	  if(client.getClientType()==1)
//						    	   subHeader.append ("             CASH INVOICE   "+NEWLINE);
//						       else
//						    	   subHeader.append ("             CREDIT INVOICE   "+NEWLINE); 
//					     	  subHeader.append (  NEWLINE);
//					     	  subHeader.append ( PRINT_DATE + now.getTime ()   + NEWLINE );
//					       	  subHeader.append ( HORIZANTAL_LINE + NEWLINE );
//							  subHeader.append ( OI_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
//						      body.append(subHeader);
//				 
//				}
//
//			         	 count=count+1;
//				 
//			} // End for loop
//		
//			body.append ( HORIZANTAL_LINE + NEWLINE );
//			body.append ( "                              Total Sales :"+quantityM +"/"+quantityS +"            "+ moneyFormat.format (totalSales)+NEWLINE );
//		 
//			
//			
//		} // End if
//		// Build the footer
//	
//		footer = new StringBuilder ();
//		if ( type == Type.INVOICE  ) {
//			int lastPage1= transactionDetails.size() / 36;
//			int rest1 = transactionDetails.size() % 36; 
//			
//			footer.append ( GROSS_AMOUNT + moneyFormat.format ( transactionHeader.getGrossAmount () ) + " " + transactionHeader.getCurrencyCode () +" " );
//			footer.append ( DISCOUNT_AMOUNT + moneyFormat.format ( transactionHeader.getDiscountAmount () ) + " " + transactionHeader.getCurrencyCode () + " "   );
//			footer.append ( TOTAL_AMOUNT + moneyFormat.format ( transactionHeader.getTotalTaxAmount() ) + " " + transactionHeader.getCurrencyCode () + NEWLINE );
//			 
//			 if(pageNumber == ( lastPage1 + 1 )   )
//		      {
//				 if(pageNumber==1)
//	  	  				if(( 36 - transactionDetails.size() - 3 )>0)
//	  				{	    for(int u=0 ; u< 36-transactionDetails.size()  - 3 ;u++)
//	  					footer.append ( NEWLINE );
//	  				}
//	  	  				else{
//	  	  					if(rest1>4)
//	  	  						for(int u=0 ; u< 36-rest1-3 ;u++)
//	  	  							footer.append ( NEWLINE );
//	  	  					}
//				 }
//			footer.append(  HORIZANTAL_LINE + NEWLINE);
//			footer.append ( "SALES REP                  Last Page                        CUSTOMER SIGN/STAMP" );
//		 
//		} // End if
//		
		
		
		 
		
		
		if ( type == Type.VANTRANSFER   ) {
		 
			for( int i = 0 ; i < movementDetails.size () ; i ++ ) {
			
				MovementDetails tr = movementDetails.get ( i );
			                         
				body.append (   String.format ( "%-"+CODE_LENGTH+"s %"+(TOTAL_LENGTH+7)+"s %"+uom_LENGTH +"s %"+ (CASES_UNITS_Length-4 )+ "s %"+(QTY_LENGTH)+"s%" + (Values_Length-4) + "s "   , 
						tr.getItemCode ().length () > CODE_LENGTH ? tr.getItemCode ().substring ( 0 , CODE_LENGTH ) : tr.getItemCode ()	,
						tr.getItemName ().length () > (TOTAL_LENGTH+7)  ? tr.getItemName ().substring ( 0 ,  (TOTAL_LENGTH+7)  ) : tr.getItemName (),	
						DatabaseUtils.getInstance ( context ).getDaoSession ().getItemsDao ().queryBuilder ()
						.where ( ItemsDao.Properties.ItemCode.eq (  tr.getItemCode()   ) ).unique().getUnitMediumSmall()	,
						tr.getQuantityMedium().intValue()+"/" +tr.getQuantitySmall().intValue(),
						tr.getBasicUnitQuantity() ,	moneyFormat.format ( tr.getPriceSmall () ))  + NEWLINE );

	 
				 
				 quantityM= quantityM+tr.getQuantityMedium();
				 quantityS=quantityS+tr.getQuantitySmall();
				 totalSales=totalSales+tr.getPriceSmall ();
			
				if(count % 38 == 0 && count != 0    )  
				{  
						body.append ( NEWLINE );
					   	body.append ("                      Continued   "+ NEWLINE);
						body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				 	   	subHeader = new StringBuilder ();
				 	    pageNumber =pageNumber +1;
				 	  
				 	    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
						subHeader.append ( " "+dATE +subString ( movementHeaders.getMovementDate().toString()  , ISSUE_DATE_Length )  + NEWLINE );
					 	subHeader.append ( LOAD_NUM + movementHeaders.getMovementCode() + NEWLINE );
					 		
					 
					 
						subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
						
						subHeader.append ("                  VAN TRANSFER         "+ NEWLINE);
						subHeader.append ( NEWLINE );
						subHeader.append(  LocationNumber + subString ( ( movementHeaders.getWarehouseCode () == null ? "" : movementHeaders.getWarehouseCode().toString() ) ,  warehouse_length )
							        	+" " + REFERENCENumber + subString ( movementHeaders.getVehicleCode().toString()  , warehouse_length ) +"  "+  subString ( vehicle.getVehicleName().toString()  , warehouse_length )+ NEWLINE );
						Users  u1 = DatabaseUtils.getInstance ( context ).getDaoSession ().getUsersDao ().queryBuilder ()
											.where ( UsersDao.Properties.UserCode.eq ( movementHeaders.getSource () ) ).unique ();
						Users  u2 = DatabaseUtils.getInstance ( context ).getDaoSession ().getUsersDao ().queryBuilder ()
									.where ( UsersDao.Properties.UserCode.eq ( movementHeaders.getDestination () ) ).unique ();
					    subHeader.append(" SOURCE:"+subString (u1==null?" ":u1.getUserName().toString()  , warehouse_length )
								         + "   DESTINATION:"+ subString (u2==null?" ":u2.getUserName().toString()   , warehouse_length )
								         + "Type:"+(movementHeaders.getMovementType()==1 ? " RECEIVING " : " GIVING ")+ NEWLINE);
						
						subHeader.append ( HORIZANTAL_LINE + NEWLINE );
				    	subHeader.append ( LOAD_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
				    	body.append(subHeader);
							 
				  }

			         	 count=count+1;
				
			} // End for loop
		  
			body.append ( HORIZANTAL_LINE + NEWLINE );
			body.append ( "                                   Total  :" + quantityM + "/" + quantityS +"            "+ moneyFormat.format (totalSales)+NEWLINE );
		 
		} // End if
		// Build the footer
		
 		if ( type == Type.VANTRANSFER   ) {
	       int lastPageM= movementDetails.size() / 38;
 			int restM = movementDetails.size() % 38;
 			footer = new StringBuilder ();
 			 
 			 if(pageNumber == ( lastPageM + 1 )   )
		      {
 				 if(pageNumber == 1)
	  	  				if(( 38 - movementDetails.size() - 3 )>0)
	  				{	    for(int u=0 ; u< 38-movementDetails.size()  - 3 ;u++)
	  					footer.append ( NEWLINE );
	  				}
	  	  				else{
			if(restM>4)
   	     for(int u=0 ; u< 38-restM-3 ;u++)
   	    	footer.append ( NEWLINE );
		       }
 				 }
 			footer.append ( "SALES REP                       WAREHOUSE IN CHARGE                   SECURITY" );
        	footer.append ( "                                LAST PAGE");
 	} // End if
 		  
	 
		if ( type == Type.MOVEMENTLoadTransfer   ) {
			
			
			for( int i = 0 ; i < movementDetails.size () ; i ++ ) {
			
				MovementDetails tr = movementDetails.get ( i );
				                                
				body.append (   String.format ( "%-"+CODE_LENGTH+"s %"+(TOTAL_LENGTH+3)+"s %"+uom_LENGTH +"s %"+ (CASES_UNITS_Length )+ "s %"+(QTY_LENGTH)+"s%" + (Values_Length-3) + "s "   , 
						tr.getItemCode ().length () > CODE_LENGTH ? tr.getItemCode ().substring ( 0 , CODE_LENGTH ) : tr.getItemCode ()	,
						tr.getItemName ().length () > (TOTAL_LENGTH+3)  ? tr.getItemName ().substring ( 0 ,  (TOTAL_LENGTH+3)  ) : tr.getItemName (),	
								DatabaseUtils.getInstance ( context ).getDaoSession ().getItemsDao ().queryBuilder ()
								.where ( ItemsDao.Properties.ItemCode.eq (  tr.getItemCode()   ) ).unique().getUnitMediumSmall()	,
						tr.getQuantityMedium().toString ()+"/" +tr.getQuantitySmall(),
						tr.getBasicUnitQuantity() ,	moneyFormat.format ( tr.getPriceSmall () ))  + NEWLINE );

	 
				 
				 quantityM= quantityM+tr.getQuantityMedium();
				 quantityS=quantityS+tr.getQuantitySmall();
				 totalSales=totalSales+tr.getPriceSmall ();
			
				if(count % 38 == 0 && count != 0    )  
				{  
						body.append ( NEWLINE );
					   	body.append ("                      Continued   "+ NEWLINE);
						body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				 	   	subHeader = new StringBuilder ();
				 	    pageNumber =pageNumber +1;
				  		subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
						subHeader.append ( " "+ISSUE_DATE +subString ( movementHeaders.getMovementDate().toString()  , ISSUE_DATE_Length )  + NEWLINE );
						subHeader.append ( LOAD_NUM + movementHeaders.getMovementCode() + NEWLINE );
					 	
				 
					     subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
					
						subHeader.append ("            LOAD TRANFER REPORT       "+ NEWLINE);
						subHeader.append ( NEWLINE );
						subHeader.append(LocationNumber+subString (movementHeaders.getWarehouseCode()==null?"": movementHeaders.getWarehouseCode().toString() , warehouse_length )
							           	+REFERENCENumber+subString ( movementHeaders.getMovementReferenceCode().toString()  , warehouse_length ));
						subHeader.append ( HORIZANTAL_LINE + NEWLINE );
				    	subHeader.append ( LOAD_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
					 
				    	body.append(subHeader);
							 
				  }

			         	 count=count+1;
				
			} // End for loop
		  
			body.append ( HORIZANTAL_LINE + NEWLINE );
			body.append(NEWLINE);
			body.append ( "                                   Total  :"+(int)quantityM +"/"+(int)quantityS +"            "+ moneyFormat.format (totalSales)+NEWLINE );
		 
		} // End if
		// Build the footer
		
 		if ( type == Type.MOVEMENTLoadTransfer   ) {
	       int lastPageM= movementDetails.size() / 38;
 			int restM = movementDetails.size() % 38;
 			footer = new StringBuilder ();
 			 
 			 if(pageNumber == ( lastPageM + 1 )   )
		      {
 				 if(pageNumber==1)
	  	  				if(( 38 - movementDetails.size() - 3 )>0)
	  				{	    for(int u=0 ; u< 38-movementDetails.size()  - 3 ;u++)
	  					footer.append ( NEWLINE );
	  				}
	  	  				else{
			if(restM>4)
   	     for(int u=0 ; u< 38-restM-3 ;u++)
   	    	footer.append ( NEWLINE );
		      }}
 			footer.append ( "SALES REP                       WAREHOUSE IN CHARGE                   SECURITY" );
        	footer.append ( "                                LAST PAGE");
 	} // End if
 		  
 				
 				 if (  type == Type.UNLOADREQUEST   ) {
 					 double tAmount=0;
 				 	for( int i = 0 ; i < movementDetails.size () ; i ++ ) {
 				
 					MovementDetails tr = movementDetails.get ( i );
 					 
 					body.append (   String.format ( "%-"+CODE_LENGTH+"s %-"+TOTAL_LENGTH_LOAD+"s %" +  (CASES_UNITS_Length +2)+ "s " ,
 								    tr.getItemCode ().length () > CODE_LENGTH ? tr.getItemCode ().substring ( 0 , CODE_LENGTH ) : tr.getItemCode ()	,
 								    tr.getItemName ().length () >  TOTAL_LENGTH_LOAD  ? tr.getItemName ().substring ( 0 ,  TOTAL_LENGTH_LOAD  ) : tr.getItemName (),
 								    tr.getQuantityMedium().intValue()+"  /  " +tr.getQuantitySmall().intValue()
 									  )  + NEWLINE );
 						
 				 
 					tAmount=tAmount+tr.getTotalLineAmount();
 					 quantityM= quantityM+tr.getQuantityMedium();
 					 quantityS=quantityS+tr.getQuantitySmall();
 				 
 				
 					if(count % 38 == 0 && count != 0    )  
 					{  
 							body.append ( NEWLINE );
 						   	body.append ("                      Continued   "+ NEWLINE);
 							body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					 	  
 					    	subHeader = new StringBuilder ();

 					  		subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
 							subHeader.append ( " "+ISSUE_DATE +subString ( movementHeaders.getMovementDate().toString()  , ISSUE_DATE_Length )  + NEWLINE );
 							subHeader.append ( LOAD_NUM + movementHeaders.getMovementCode() + NEWLINE );
 						 	
 						 
 						     subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
 						
 						     subHeader.append ("            UNLOAD REQUEST REPORT       "+ NEWLINE);
 						    subHeader.append(  REFERENCENumber1 + subString ( movementHeaders.getVehicleCode().toString()  , warehouse_length ) +"  "+  subString ( vehicle.getVehicleName().toString()  , warehouse_length )+ NEWLINE );
 							
 							 subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
 							 subHeader.append ( HORIZANTAL_LINE + NEWLINE );
 						     subHeader.append ( LOADREQUEST_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
 					    	 body.append(subHeader);
 					    	  
 							
 					    	 pageNumber  =	pageNumber + 1;	 
 					  }

 				         	 count=count+1;
 					
 				} // End for loop
 			  	body.append ( HORIZANTAL_LINE + NEWLINE );
 			  	body.append(NEWLINE);
 				body.append ( "                               Total Quantity  :"+(int)quantityM +"  /  "+(int)quantityS  +NEWLINE );
 				body.append ( "                               Total Amount  :"+ moneyFormat.format (tAmount)  +NEWLINE );
 	 			 
 				 } // End if
 			// Build the footer
 			
 	 			if ( type == Type.UNLOADREQUEST   ) {
 	 				  int lastPageM= movementDetails.size() / 38;
 	 		 			int restM = movementDetails.size() % 38;
 	 				footer = new StringBuilder ();
 	 			 
 	 			 	if(pageNumber == ( lastPageM + 1 )    )
 	 			    	{
 	 			 		if(pageNumber==1)
 	 			    	 
	  	  				if(( 38 - movementDetails.size() - 3 )>0)
		  				{	    for(int u=0 ; u< 38-movementDetails.size()  - 3 ;u++)
		  					footer.append ( NEWLINE );
		  				}
		  	  				else{
 	 				       if(restM>4)
 	 				    	   for(int u=0 ; u< 38-restM-3 ;u++)
 	 				    		   footer.append ( NEWLINE );
 	 			    	}}
 	 		    	footer.append ( "SALES REP              WAREHOUSE IN CHARGE              SECURITY" );
 	            	footer.append ( "                         LAST PAGE");
 	 	     } // End if
 	 			if (  type == Type.DirectUnload   ) {
 	 				double totalAmount=0;
 				 	for( int i = 0 ; i < movementDetails.size () ; i ++ ) {
 				
 					MovementDetails tr = movementDetails.get ( i );
 					 
 					body.append (   String.format ( "%-"+CODE_LENGTH+"s %-"+TOTAL_LENGTH_LOAD+"s %" +  (CASES_UNITS_Length +2)+ "s " ,
 								    tr.getItemCode ().length () > CODE_LENGTH ? tr.getItemCode ().substring ( 0 , CODE_LENGTH ) : tr.getItemCode ()	,
 								    tr.getItemName ().length () >  TOTAL_LENGTH_LOAD  ? tr.getItemName ().substring ( 0 ,  TOTAL_LENGTH_LOAD  ) : tr.getItemName (),
 								    tr.getQuantityMedium().intValue()+"  /  " +tr.getQuantitySmall().intValue()
 									  )  + NEWLINE );
 						
 				 
 					 
 					 quantityM= quantityM+tr.getQuantityMedium();
 					 quantityS=quantityS+tr.getQuantitySmall();
 					totalAmount+=tr.getTotalLineAmount();
 				
 					if(count % 38 == 0 && count != 0    )  
 					{  
 							body.append ( NEWLINE );
 						   	body.append ("                      Continued   "+ NEWLINE);
 							body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					 	  
 					    	subHeader = new StringBuilder ();

 					  		subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
 							subHeader.append ( " "+ISSUE_DATE +subString ( movementHeaders.getMovementDate().toString()  , ISSUE_DATE_Length )  + NEWLINE );
 							subHeader.append ( LOAD_NUM + movementHeaders.getMovementCode() + NEWLINE );
 						 	
 						 
 						     subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
 						
 						     subHeader.append ("            UNLOAD REPORT       "+ NEWLINE);
 						    subHeader.append(  REFERENCENumber1 + subString ( movementHeaders.getVehicleCode().toString()  , warehouse_length ) +"  "+  subString ( vehicle.getVehicleName().toString()  , warehouse_length )+ NEWLINE );
 							
 							 subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
 							 subHeader.append ( HORIZANTAL_LINE + NEWLINE );
 						     subHeader.append ( LOADREQUEST_DETIAL_HEADER_DirectLoad1 + NEWLINE + HORIZANTAL_LINE + NEWLINE );
 					    	 body.append(subHeader);
 					    	  
 							
 					    	 pageNumber  =	pageNumber + 1;	 
 					  }

 				         	 count=count+1;
 					
 				} // End for loop
 			  	body.append ( HORIZANTAL_LINE + NEWLINE );
 			  	body.append(NEWLINE);
 				body.append ( "                               Total Quantity :"+(int)quantityM +"  /  "+(int)quantityS  +NEWLINE );
 				body.append ( "                               Total Amount   :"+ moneyFormat.format (totalAmount)  +NEWLINE );
 	 			 
 	 			} // End if
 			// Build the footer
 			
 	 			if ( type == Type.DirectUnload   ) {
 	 				  int lastPageM= movementDetails.size() / 38;
 	 		 			int restM = movementDetails.size() % 38;
 	 				footer = new StringBuilder ();
 	 			 
 	 			 	if(pageNumber == ( lastPageM + 1 )    )
 	 			    	{
 	 			 		if(pageNumber==1)
 	 			    	 
	  	  				if(( 38 - movementDetails.size() - 3 )>0)
		  				{	    for(int u=0 ; u< 38-movementDetails.size()  - 3 ;u++)
		  					footer.append ( NEWLINE );
		  				}
		  	  				else{
 	 				       if(restM>4)
 	 				    	   for(int u=0 ; u< 38-restM-3 ;u++)
 	 				    		   footer.append ( NEWLINE );
 	 			    	}}
 	 		    	footer.append ( "SALES REP              WAREHOUSE IN CHARGE              SECURITY" );
 	            	footer.append ( "                         LAST PAGE");
 	 	     } // End 		
 	 			
 	 			
 	 			
        if ( type == Type.MOVEMENTLoadRequest   ) {
        	double amount = 0d;
			double total = 0d;
			 	for( int i = 0 ; i < movementDetails.size () ; i ++ ) {
			
				MovementDetails tr = movementDetails.get ( i );
				 
				body.append (   String.format ( "%-"+CODE_LENGTH+"s %-"+TOTAL_LENGTH_LOAD+"s %" +  (CASES_UNITS_Length +2) + "s %" + QTY_LENGTH + "s"  ,
					    tr.getItemCode ().length () > CODE_LENGTH ? tr.getItemCode ().substring ( 0 , CODE_LENGTH ) : tr.getItemCode ()	,
					    tr.getItemName ().length () >  TOTAL_LENGTH_LOAD  ? tr.getItemName ().substring ( 0 ,  TOTAL_LENGTH_LOAD  ) : tr.getItemName (),
					    tr.getQuantityMedium().intValue()+"  /  " +tr.getQuantitySmall().intValue(),
					    moneyFormat.format (tr.getTotalLineAmount())
						  )  + NEWLINE );
			
	 
					
			 
				 
				 quantityM= quantityM+tr.getQuantityMedium();
				 quantityS=quantityS+tr.getQuantitySmall();
			 
				 //jose-print
				 amount = amount + tr.getTotalLineAmount();
				 total =+ amount;
				if(count % 38 == 0 && count != 0    )  
				{  
						body.append ( NEWLINE );
					   	body.append ("                      Continued   "+ NEWLINE);
						body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				 	  
				    	 subHeader = new StringBuilder ();

				  		 subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
						 subHeader.append ( " "+ISSUE_DATE +subString ( movementHeaders.getMovementDate().toString()  , ISSUE_DATE_Length )  + NEWLINE );
						 subHeader.append ( LOAD_NUM + movementHeaders.getMovementCode() + NEWLINE );
					 	
					    
					     subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
					
					     subHeader.append ("            LOAD REQUEST REPORT       "+ NEWLINE);
					     subHeader.append(  REFERENCENumber1 + subString ( movementHeaders.getVehicleCode().toString()  , warehouse_length ) +"  "+  subString ( vehicle.getVehicleName().toString()  , warehouse_length )+ NEWLINE );
						 subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
						 subHeader.append ( HORIZANTAL_LINE + NEWLINE );
					     subHeader.append ( LOADREQUEST_DETIAL_HEADER1 + NEWLINE + HORIZANTAL_LINE + NEWLINE );
				    	 body.append(subHeader);
				    	  
						
				    	 pageNumber  =	pageNumber + 1;	 
				  }

			         	 count=count+1;
				
			} // End for loop
		  	body.append ( HORIZANTAL_LINE + NEWLINE );
			body.append ( "                               Total Quantity  :"+(int)quantityM +"  /  "+(int)quantityS  +NEWLINE );
			body.append ( "                               Total Amount    : " + moneyFormat.format (total) + NEWLINE );
			count = count+1; 	
        } // End if
		// Build the footer
		
 			if ( type == Type.MOVEMENTLoadRequest   ) {
 				  int lastPageM= movementDetails.size() / 38;
 		 			int restM = movementDetails.size() % 38;
 				footer = new StringBuilder ();
 				 
 			 	if(pageNumber == ( lastPageM + 1 )   )
 			    	
 			 	{
 			 		if(pageNumber==1)
  	  				if(( 38 - movementDetails.size() - 3 )>0)
  				{	  for(int u=0 ; u< ( 38 - movementDetails.size() - 3 )  ;u++)
  					footer.append ( NEWLINE );
  				}
  	  				else{
 			 		
 				       if(restM>4)
 				    	   for(int u=0 ; u< 38-restM-3 ;u++)
 				    		   footer.append ( NEWLINE );
 			    	}
 			 		}
 		    	footer.append ( "SALES REP              WAREHOUSE IN CHARGE              SECURITY" );
            	footer.append ( "                         LAST PAGE");
 	     } // End if
 	    	
 			
 	        if ( type == Type.DirectLoad   ) {
 	        	double totalAmount=0;
 				 	for( int i = 0 ; i < loadPrintings.size () ; i ++ ) {
 				
 					LoadPrinting tr = loadPrintings.get ( i );
 					 
 					body.append (   String.format ( "%-"+CODE_LENGTH+"s %-"+TOTAL_LENGTH_LOAD+"s %" +  (CASES_UNITS_Length +2)+ "s " ,
 								    tr.getItemCode ().length () > CODE_LENGTH ? tr.getItemCode ().substring ( 0 , CODE_LENGTH ) : tr.getItemCode ()	,
 								    tr.getItemName ().length () >  TOTAL_LENGTH_LOAD  ? tr.getItemName ().substring ( 0 ,  TOTAL_LENGTH_LOAD  ) : tr.getItemName (),
 								    tr.getQuantityMedium().intValue()+"  /  " +tr.getQuantitySmall().intValue()
 									  )  + NEWLINE );
 						
 				 
 					 
 					 quantityM= quantityM+tr.getQuantityMedium();
 					 quantityS=quantityS+tr.getQuantitySmall();
 					totalAmount+=tr.getTotaLineAmount();
 				
 					if(count % 38 == 0 && count != 0    )  
 					{  
 							body.append ( NEWLINE );
 						   	body.append ("                      Continued   "+ NEWLINE);
 							body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					    	body.append ( NEWLINE );
 					 	  
 					    	 subHeader = new StringBuilder ();

 					  		 subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
 							 subHeader.append ( " "+ISSUE_DATE +subString ( movementHeaders.getMovementDate().toString()  , ISSUE_DATE_Length )  + NEWLINE );
 							 subHeader.append ( LOAD_NUM + movementHeaders.getMovementCode() + NEWLINE );
 						 	
 						    
 						     subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
 						
 						     subHeader.append ("            LOAD REPORT       "+ NEWLINE);
 						     subHeader.append(  REFERENCENumber1 + subString ( movementHeaders.getVehicleCode().toString()  , warehouse_length ) +"  "+  subString ( vehicle.getVehicleName().toString()  , warehouse_length )+ NEWLINE );
 							 subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
 							 subHeader.append ( HORIZANTAL_LINE + NEWLINE );
 						     subHeader.append ( LOADREQUEST_DETIAL_HEADER_DirectLoad + NEWLINE + HORIZANTAL_LINE + NEWLINE );
 					    	 body.append(subHeader);
 					    	  
 							
 					    	 pageNumber  =	pageNumber + 1;	 
 					  }

 				         	 count=count+1;
 					
 				} // End for loop
 			  	body.append ( HORIZANTAL_LINE + NEWLINE );
 				body.append ( "                               Total Quantity  :"+(int)quantityM +"  /  "+(int)quantityS  +NEWLINE );
 				body.append ( "                               Total Amount  :"+ moneyFormat.format (totalAmount)  +NEWLINE );
 			 	} // End if
 			// Build the footer
 			
 	 			if ( type == Type.DirectLoad   ) {
 	 				  int lastPageM= loadPrintings.size() / 38;
 	 		 			int restM = loadPrintings.size() % 38;
 	 				footer = new StringBuilder ();
 	 				 
 	 			 	if(pageNumber == ( lastPageM + 1 )   )
 	 			    	
 	 			 	{
 	 			 		if(pageNumber==1)
 	  	  				if(( 38 - loadPrintings.size() - 3 )>0)
 	  				{	  for(int u=0 ; u< ( 38 - loadPrintings.size() - 3 )  ;u++)
 	  					footer.append ( NEWLINE );
 	  				}
 	  	  				else{
 	 			 		
 	 				       if(restM>4)
 	 				    	   for(int u=0 ; u< 38-restM-3 ;u++)
 	 				    		   footer.append ( NEWLINE );
 	 			    	}
 	 			 		}
 	 		    	footer.append ( "SALES REP              WAREHOUSE IN CHARGE              SECURITY" );
 	            	footer.append ( "                         LAST PAGE");
 	 	     } // End if
 	 	    	
 			//RECEIPT
 	   if ( type == Type.RECEIPT   ) 
 		     {//SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy",  java.util.Locale.getDefault());
 	 	     
 			   if(collectionInvoices.size () > 0){
 				
 				   subHeader.append ( HORIZANTAL_LINE + NEWLINE );
			       subHeader.append ( Receipt_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
			    	
			   for( int i = 0 ; i < collectionInvoices.size () ; i ++ ) {
			          CollectionInvoices tr = collectionInvoices.get ( i );
			          String mdy = sdf.format(tr.getInvoiceDate());
						
			 	       	 	
		      	      body.append ( String.format ( "%-"+(INVOICE_Length-2) + "s %"+INVOICE_DATE_Length + "s %"+Amount_Length + "s " ,
					            	  tr.getInvoiceCode ().length () > (INVOICE_Length-2) ? tr.getInvoiceCode  ().substring ( 0 , (INVOICE_Length-2) ) : tr.getInvoiceCode   ()	
					            	 , subString (mdy , INVOICE_DATE_Length ) 
					             	 , moneyFormat.format (tr.getTotalAmount()) )+ NEWLINE );
					                  totalSales = totalSales + tr.getTotalAmount();
				 
				if(count % 38 == 0 && count != 0    )  
				    {  
						body.append ( NEWLINE );
					   	body.append ("                      Continued   "+ NEWLINE);
						body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				 	   
				    	subHeader = new StringBuilder ();

				     	subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
						subHeader.append ( " "+dATE +subString ( collectionHeaders.getCollectionDate().toString()  , ISSUE_DATE_Length )  + NEWLINE );
						subHeader.append ( LOAD_NUM + collectionHeaders.getCollectionCode() + NEWLINE );
					 		
					 
					     subHeader.append ( CLIENT_ID +subString ( client.getClientCode () ,CLIENT_ID_length) );
					     subHeader.append ( " "+ subString (client.getClientName ()  ,CLIENT_Name_length) + NEWLINE );
					     subHeader.append (  "                "+   client.getClientAddress ()    + NEWLINE );
				         subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
					 	 subHeader.append ("                              RECEIPT");
					
					 	 
					     subHeader.append ( HORIZANTAL_LINE + NEWLINE );
				    	 subHeader.append ( Receipt_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
				    	 body.append(subHeader);
				    	 pageNumber  =	pageNumber + 1;	 
				  }

			         	 count=count+1;
				
			} // End for loop
		  
			body.append ( HORIZANTAL_LINE + NEWLINE );
			body.append ( "                               Total RECEIVED  :"+totalSales + NEWLINE );
 		  }
			
			double totalCollectionAmount=0;
			body.append ( NEWLINE+"                       CASH/CHEQUE DETAILS"+ NEWLINE);	
			body.append ( Receipt_DETIAL_Cash_Check_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE);	
			for( int i = 0 ; i < collectionDetails.size () ; i ++ ) {
				
				CollectionDetails tr = collectionDetails.get ( i );
			if(tr.getCollectionDetailType()==1)	 
					{
			                String mdy = sdf.format(tr.getCheckDate());	
							body.append (  String.format ( "%-"+INVOICE_DATE_Length+"s %-"+Bank_Length+"s %"+(Cheque_Length-3)+"s %"+(Amount_Length-2)+ "s " ,
							subString (mdy , INVOICE_DATE_Length ) 
							,subString(tr.getBankCode(), Bank_Length)
							,subString(tr.getCheckCode(),Cheque_Length),moneyFormat.format (tr.getCollectionAmount() ) ) + NEWLINE );
					}else
						{  
						
				   	 	
			      	      body.append ( String.format ( "%-"+INVOICE_DATE_Length+"s %-"+Bank_Length+"s %"+(Cheque_Length-3)+"s %"+(Amount_Length-2)+ "s " ,
			      	    		"   --------  " 
									,"CASH  "
									,"      --------  ",moneyFormat.format (tr.getCollectionAmount() ) ) + NEWLINE );
						
//							body.append ( String.format ( "%-"+ INVOICE_DATE_Length +"s %"+ Bank_Length +"s % "+ Cheque_Length +"s %" + Amount_Length + "s " ,
//							"   --------  " 
//							," CASH  "
//							,"   --------  ",moneyFormat.format (tr.getCollectionAmount() ) ) + NEWLINE );
						}
				totalCollectionAmount = totalCollectionAmount + tr.getCollectionAmount();
			 
			if(count % 38 == 0 && count != 0    )  
			{  
					body.append ( NEWLINE );
				   	body.append ("                      Continued   "+ NEWLINE);
					body.append ( NEWLINE );
			    	body.append ( NEWLINE );
			    	body.append ( NEWLINE );
			    	body.append ( NEWLINE );
			    	body.append ( NEWLINE );
			    
			    	body.append ( NEWLINE );
			    	body.append ( NEWLINE );
			    	body.append ( NEWLINE );
			    	body.append ( NEWLINE );
			    	body.append ( NEWLINE );
			    	body.append ( NEWLINE );
			    	body.append ( NEWLINE );
			    	body.append ( NEWLINE );
			    	body.append ( NEWLINE );
			    	body.append ( NEWLINE );
			 	  
			    	subHeader = new StringBuilder ();

			    	subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
					subHeader.append ( " "+dATE +subString ( collectionHeaders.getCollectionDate().toString()  , ISSUE_DATE_Length )  + NEWLINE );
				  
					subHeader.append ( LOAD_NUM + collectionHeaders.getCollectionCode() + NEWLINE );
				 		
			 
				     subHeader.append ( CLIENT_ID +subString ( client.getClientCode () ,CLIENT_ID_length) );
				     subHeader.append ( " "+ subString (client.getClientName ()  ,CLIENT_Name_length) + NEWLINE );
				     subHeader.append (  "                "+ client.getClientAddress ()  + NEWLINE );
			         subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
				     subHeader.append ("                              RECEIPT");
				 
					subHeader.append (  NEWLINE);
					 
				     subHeader.append ( HORIZANTAL_LINE + NEWLINE );
			    	 subHeader.append ( Receipt_DETIAL_Cash_Check_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
			    	 body.append(subHeader);
			    	  
					
			    	 pageNumber  =	pageNumber + 1;	 
			  }

		         	 count=count+1;
			
		} // End for loop
	  
		body.append ( HORIZANTAL_LINE + NEWLINE );
		body.append ( "     CURRENT OUTSTANDING BALANCE  :       " + moneyFormat.format (totalCollectionAmount ) + NEWLINE );
		    int lastPageM = ( collectionDetails.size() > 0 ? collectionDetails.size() + 5:0 + collectionInvoices.size()>0?collectionInvoices.size() + 5:0 )/ 38;
			int restM = (  collectionDetails.size() > 0 ? collectionDetails.size() + 5:0 + collectionInvoices.size()>0?collectionInvoices.size() + 5:0  ) % 38;
			 if(pageNumber == ( lastPageM + 1 )   )
		      {
				 if(pageNumber == 1){
	  	  				if(( 38 - ((collectionDetails.size() > 0 ? collectionDetails.size() + 5:0 + collectionInvoices.size()>0?collectionInvoices.size() + 5:0  ) ) - 3 ) > 0)
	  				{	    
	  	  					for(int u = 0 ; u < 38-(collectionDetails.size() > 0 ? collectionDetails.size() + 5:0 + collectionInvoices.size()>0?collectionInvoices.size() + 5:0  )  - 3 ;u++)
	  	  							body.append ( NEWLINE );
	  				}
	  	  		}
	  	  		else{
	  	  		if(restM>4)
	  	  		for(int u=0 ; u< 38 - restM - 3 ;u++)
	  	  		body.append ( NEWLINE );
	  	  			}
				}
	  	body.append ( NEWLINE  +  "    SALESMAN                     CUSTOMER SIGN/STAMP " + NEWLINE );
	  	 
		if(collectionHeaders.getPrintingTimes() == null || collectionHeaders.getPrintingTimes()==0 )
		{	
			body.append ("                                           Original  " +NEWLINE);
		//	collectionHeaders.setPrintingTimes(1);
			//DatabaseUtils.getInstance ( context ).getDaoSession ().getCollectionHeadersDao() .update(collectionHeaders);
		}
	 	else
	 	{
	 		body.append ("     Reprint  " +NEWLINE);
		//	 collectionHeaders.setPrintingTimes(collectionHeaders.getPrintingTimes() + 1);
			// DatabaseUtils.getInstance ( context ).getDaoSession ().getCollectionHeadersDao() .update(collectionHeaders);
		
	 	}
	  	body.append ("                                                        LAST PAGE   ");
 		 } 
// 	  if ( type == Type.RECEIPT   ) {
//	        int lastPageM= (collectionDetails.size() +collectionInvoices.size())/ 38;
//			int restM = loadPrintings.size() % 38;
//			footer = new StringBuilder ();
//			 if(pageNumber == ( lastPageM + 1 ) )
//		      {
//			if(restM>4)
//  	     for(int u=0 ; u< 38-restM-3 ;u++)
//  	    	footer.append ( NEWLINE );
//		      }
//			footer.append ( "SALES REP                       WAREHOUSE IN CHARGE                   SECURITY" );
//       	footer.append ( "                                LASTE PAGE");
//	} // End if
// 	   
 	   
 		 if ( type == Type.LOAD   ) {
 			 
 			double   MquantityM=0,MquantityS=0,StockBox=0,StockUnit=0,totalAmount=0;
 			for( int i = 0 ; i < loadPrintings.size () ; i ++ ) {
 			
 				LoadPrinting  tr = loadPrintings.get ( i );
 				                                
// 				body.append (   String.format ( "%-"+8+"s %-"+21+"s %"+ 7 +"s %"+8 + "s %"+9+"s %" + 12 + "s %" + 6 + "s "  , 
// 							    tr.getItemCode ().length () > 8 ? tr.getItemCode ().substring ( 0 , 8 ) : tr.getItemCode ()	,
// 							    tr.getItemName ().length () >21 ? tr.getItemName ().substring ( 0 , 21 ) : tr.getItemName (),
// 							    tr.getQuantityMedium().intValue()+"/" + tr.getQuantitySmall().intValue(),
// 							    tr.getMissedQuantityMedium().intValue()+"/" + tr.getMissedQuantitySmall().intValue(),
// 								tr.getStockBox().intValue()+"/"+tr.getStockUnit().intValue() +"/"+tr.getStockQuantity().intValue(),
// 								tr.getMissedBasicUnitQuantity().intValue(),
// 								tr.getBasicUnitQuantity().intValue()  )  + NEWLINE );
 					
 				
				body.append (   String.format ( "%-"+8+"s %-"+21+"s %"+ 5 +"s %"+11 + "s %"+6+"s %" + 10 + "s %" + 6 + "s "  , 
						    tr.getItemCode ().length () > 8 ? tr.getItemCode ().substring ( 0 , 8 ) : tr.getItemCode ()	,
						    tr.getItemName ().length () >21 ? tr.getItemName ().substring ( 0 , 21 ) : tr.getItemName (),
						    tr.getBasicUnitQuantity().intValue() ,
						    tr.getQuantityMedium().intValue()+"/" + tr.getQuantitySmall().intValue(),
						    tr.getMissedQuantityMedium().intValue()+"/" + tr.getMissedQuantitySmall().intValue(),
							tr.getStockBox().intValue()+"/"+tr.getStockUnit().intValue() ,
							tr.getStockQuantity().intValue()
							 )  + NEWLINE );
			 
			     StockBox=StockBox+tr.getStockBox().intValue();
			     StockUnit=StockUnit+tr.getStockUnit().intValue();
 				
 				//totalAmount+=tr.getTotaLineAmount();
 				
 				
 				 quantityM= quantityM+tr.getQuantityMedium();
 				 quantityS=quantityS+tr.getQuantitySmall();
 				 MquantityM=MquantityM+tr.getMissedQuantityMedium();
 			     MquantityS=MquantityS+tr.getMissedQuantitySmall();
 				
 				if(count % 38 == 0 && count != 0    )  
 				{  
 						body.append ( NEWLINE );
 					   	body.append ("                      Continued   "+ NEWLINE);
 						body.append ( NEWLINE );
 				    	body.append ( NEWLINE );
 				    	body.append ( NEWLINE );
 				    	body.append ( NEWLINE );
 				    	body.append ( NEWLINE );
 				    
 				    	body.append ( NEWLINE );
 				    	body.append ( NEWLINE );
 				    	body.append ( NEWLINE );
 				    	body.append ( NEWLINE );
 				    	body.append ( NEWLINE );
 				    	body.append ( NEWLINE );
 				    	body.append ( NEWLINE );
 				    	body.append ( NEWLINE );
 				    	body.append ( NEWLINE );
 				    	body.append ( NEWLINE );
 				 	   	subHeader = new StringBuilder ();
 				 	    pageNumber =pageNumber +1;
 				 	    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
 			 			subHeader.append ( " "+dATE +subString ( movementHeaders.getMovementDate().toString()  , ISSUE_DATE_Length )  + NEWLINE );
 			 		 	subHeader.append ( LOAD_NUM + movementHeaders.getMovementCode() + NEWLINE );
 			 	 
 			 		 
 			 			subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
 			 		 
 			 			subHeader.append ("             LOAD         "+ NEWLINE);
 			 			subHeader.append ( NEWLINE );
 			 			subHeader.append(  LocationNumber + subString ( movementHeaders.getWarehouseCode()==null?"": movementHeaders.getWarehouseCode().toString()  ,  warehouse_length )
 			 				        	+" " + REFERENCENumber + subString ( movementHeaders.getVehicleCode().toString()  , warehouse_length ) +"  "+ subString ( vehicle.getVehicleName().toString()  , warehouse_length ) );
 			 			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
 			 	    	subHeader.append ( STOCKRECONCILIATION_LOAD_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
 				    	body.append(subHeader);
 							 
 				  }

 			         	 count=count+1;
 				
 			} // End for loop
 		  
 			body.append ( HORIZANTAL_LINE + NEWLINE );
 			body.append ( "                         Total  :" + (int)quantityM +"/" + (int)quantityS + "  | "+ (int)MquantityM + "/" + (int)MquantityS +"  | "+(int)StockBox + "/"+(int)StockUnit  +NEWLINE );
 		//	body.append ( "                         Total Amount  :"+ (int)totalAmount +NEWLINE );
 		     
 			int lastPageM= loadPrintings.size() / 38;
  			int restM = loadPrintings.size() % 38;
  			footer = new StringBuilder ();
  			
  			 if(pageNumber == ( lastPageM + 1 )   )
 		      {
  				if(pageNumber==1)
  	  				if(( 38 - loadPrintings.size() - 3 )>0)
  				{	  for(int u=0 ; u< ( 38 - loadPrintings.size() - 3 )  ;u++)
  					body.append ( NEWLINE );
  				}
  	  				else{
 			if(restM>4)
    	     for(int u=0 ; u< 38-restM-3 ;u++)
    	    	 body.append ( NEWLINE );
 		      }
  				}
  			body.append ( "SALES REP                       WAREHOUSE IN CHARGE                   SECURITY" );
  			body.append ( "                                LAST PAGE");
  
 			
 			
 		} // End if
 		// Build the footer
 		
  		//if ( type == Type.LOAD   ) {
// 	        int lastPageM= loadPrintings.size() / 38;
//  			int restM = loadPrintings.size() % 38;
//  			footer = new StringBuilder ();
//  			if(pageNumber==1)
//  				if(( 38 - loadPrintings.size() - 3 )>0)
//			{	  for(int u=0 ; u< ( 38 - loadPrintings.size() - 3 )  ;u++)
//				footer.append ( NEWLINE );
//			}
//  			 if(pageNumber == ( lastPageM + 1 )   )
// 		      {
// 			if(restM>4)
//    	     for(int u=0 ; u< 38-restM-3 ;u++)
//    	    	footer.append ( NEWLINE );
// 		      }
//  			footer.append ( "SALES REP                       WAREHOUSE IN CHARGE                   SECURITY" );
//         	footer.append ( "                                LASTE PAGE");
//  	} // End if
  		
  		

		 if ( type == Type.UNLOAD   ) {
			
			double   MquantityM = 0 , MquantityS = 0 ,StockBox = 0 ,  StockUnit = 0;
			for( int i = 0 ; i < loadPrintings.size () ; i ++ ) {
			
				LoadPrinting  tr = loadPrintings.get ( i );
				                                
//				body.append (   String.format ( "%-"+8+"s %-"+21+"s %"+ 7 +"s %"+8 + "s %"+9+"s %" + 12 + "s %" + 6 + "s "  , 
//								tr.getItemCode ().length () > 8 ? tr.getItemCode ().substring ( 0 , 8 ) : tr.getItemCode ()	,
//								tr.getItemName ().length () >21 ? tr.getItemName ().substring ( 0 , 21 ) : tr.getItemName (),
//							     tr.getQuantityMedium().intValue()+"/" + tr.getQuantitySmall().intValue(),
//						         tr.getMissedQuantityMedium().intValue()+"/" + tr.getMissedQuantitySmall().intValue(),
//							    tr.getStockBox().intValue()+'/'+ tr.getStockUnit().intValue() +'/'+ tr.getStockQuantity().intValue(),
//							     tr.getMissedBasicUnitQuantity().intValue(),
//							    tr.getBasicUnitQuantity().intValue()  )  + NEWLINE );
//				

				body.append (   String.format ( "%-"+8+"s %-"+21+"s %"+ 5 +"s %"+11 + "s %"+6+"s %" + 10 + "s %" + 6 + "s "  , 
						    tr.getItemCode ().length () > 8 ? tr.getItemCode ().substring ( 0 , 8 ) : tr.getItemCode ()	,
						    tr.getItemName ().length () >21 ? tr.getItemName ().substring ( 0 , 21 ) : tr.getItemName (),
						    tr.getBasicUnitQuantity().intValue() ,
						    tr.getQuantityMedium().intValue()+"/" + tr.getQuantitySmall().intValue(),
						    tr.getMissedQuantityMedium().intValue()+"/" + tr.getMissedQuantitySmall().intValue(),
							 tr.getStockBox().intValue()+"/"+tr.getStockUnit().intValue() ,
							 tr.getStockQuantity().intValue()
							 )  + NEWLINE );
			 
			     StockBox=StockBox+tr.getStockBox().intValue();
			     StockUnit=StockUnit+tr.getStockUnit().intValue();
				 quantityM= quantityM+tr.getQuantityMedium();
				 quantityS=quantityS+tr.getQuantitySmall();
				 MquantityM=MquantityM+tr.getMissedQuantityMedium();
			     MquantityS=MquantityS+tr.getMissedQuantitySmall();
				
				if(count % 38 == 0 && count != 0    )  
				{  
						body.append ( NEWLINE );
					   	body.append ("                      Continued   "+ NEWLINE);
						body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				 	   	subHeader = new StringBuilder ();
				 	    pageNumber =pageNumber +1;
				 	    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
			 			subHeader.append ( " "+dATE +subString ( movementHeaders.getMovementDate().toString()  , ISSUE_DATE_Length )  + NEWLINE );
			 		 	subHeader.append ( LOAD_NUM + movementHeaders.getMovementCode() + NEWLINE );
			 		 		
			 		 
			 		 
			 			subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
			 		 
			 			subHeader.append ("             UNLOAD         "+ NEWLINE);
			 			subHeader.append ( NEWLINE );
			 			subHeader.append(  LocationNumber + subString ( movementHeaders.getWarehouseCode()==null?"": movementHeaders.getWarehouseCode().toString()  ,  warehouse_length )
			 				        	+" " + REFERENCENumber + subString ( movementHeaders.getVehicleCode().toString()  , warehouse_length ) +"  "+ subString ( vehicle.getVehicleName().toString()  , warehouse_length ) );
			 			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
			 	    	subHeader.append ( STOCKRECONCILIATION_LOAD_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
				    	body.append(subHeader);
							 
				  }

			         	 count=count+1;
				
			} // End for loop
		  
			body.append ( HORIZANTAL_LINE + NEWLINE );
			body.append ( "                          Total  :" + (int)quantityM +"/" + (int)quantityS + "  | "+ (int)MquantityM + "/" + (int)MquantityS +"  | "+(int)StockBox + "/"+(int)StockUnit  +NEWLINE );
		 
		} // End if
		// Build the footer
		
 		if ( type == Type.UNLOAD   ) {
	        int lastPageM= loadPrintings.size() / 38;
 			int restM = loadPrintings.size() % 38;
 			footer = new StringBuilder ();
 			
 			 if(pageNumber == ( lastPageM + 1 )   )
		      {
 				if(pageNumber==1)
 	 				if(( 38 - loadPrintings.size() - 3 )>0)
 	 				{	  for(int u=0 ; u< 38-loadPrintings.size()-3  ;u++)
 	 						footer.append ( NEWLINE );
 	 				}else{
			if(restM>4)
   	     for(int u=0 ; u< 38-restM-3 ;u++)
   	    	footer.append ( NEWLINE );
		      }}
 			footer.append ( "SALES REP                       WAREHOUSE IN CHARGE                   SECURITY" );
        	footer.append ( "                                LAST PAGE");
 	} // End if
 		

  		
  		
  		
if ( type == Type.VANSTOCKCOUNT   ) {
   			
   			 
for( int i = 0 ; i < movementDetails.size () ; i ++ ) {
  				
				MovementDetails tr = movementDetails.get ( i );
				                                
                
				body.append (   String.format ( "%-"+CODE_LENGTH+"s %"+(TOTAL_LENGTH+7)+"s %"+uom_LENGTH +"s %"+ (CASES_UNITS_Length-4 )+ "s %"+(QTY_LENGTH)+"s%" + (Values_Length-4) + "s "   , 
								tr.getItemCode ().length () > CODE_LENGTH ? tr.getItemCode ().substring ( 0 , CODE_LENGTH ) : tr.getItemCode ()	,
								tr.getItemName ().length () > (TOTAL_LENGTH+7)  ? tr.getItemName ().substring ( 0 ,  (TOTAL_LENGTH+7)  ) : tr.getItemName (),	
								DatabaseUtils.getInstance ( context ).getDaoSession ().getItemsDao ().queryBuilder ()
								.where ( ItemsDao.Properties.ItemCode.eq (  tr.getItemCode()   ) ).unique().getUnitMediumSmall()	,
								tr.getQuantityMedium().intValue()+"/" +tr.getQuantitySmall().intValue(),
								tr.getBasicUnitQuantity() ,	moneyFormat.format ( tr.getPriceSmall () ))  + NEWLINE );
	
			 
				 
				 quantityM= quantityM+tr.getQuantityMedium();
				 quantityS=quantityS+tr.getQuantitySmall();
				 totalSales=totalSales+tr.getPriceSmall ();
   				
   				
   				if(count % 38 == 0 && count != 0    )  
   				{  
   						body.append ( NEWLINE );
   					   	body.append ("                      Continued   "+ NEWLINE);
   						body.append ( NEWLINE );
   				    	body.append ( NEWLINE );
   				    	body.append ( NEWLINE );
   				    	body.append ( NEWLINE );
   				    	body.append ( NEWLINE );
   				    
   				    	body.append ( NEWLINE );
   				    	body.append ( NEWLINE );
   				    	body.append ( NEWLINE );
   				    	body.append ( NEWLINE );
   				    	body.append ( NEWLINE );
   				    	body.append ( NEWLINE );
   				    	body.append ( NEWLINE );
   				    	body.append ( NEWLINE );
   				    	body.append ( NEWLINE );
   				    	body.append ( NEWLINE );
   				 	   	subHeader = new StringBuilder ();
   				 	    pageNumber =pageNumber +1;
   				 	    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
   			 			subHeader.append ( " "+dATE +subString ( movementHeaders.getMovementDate().toString()  , ISSUE_DATE_Length )  + NEWLINE );
   			 		 	subHeader.append ( LOAD_NUM + movementHeaders.getMovementCode() + NEWLINE );
   			 		 		
   			 		 
   			 		 
   			 			subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
   			 		 
   			 			subHeader.append ("             VAN STOCK COUNT        "+ NEWLINE);
   			 			subHeader.append ( NEWLINE );
   			 			subHeader.append(  LocationNumber + subString ( movementHeaders.getWarehouseCode()==null?"": movementHeaders.getWarehouseCode().toString() ,  warehouse_length )
   			 				        	+" " + REFERENCENumber + subString ( movementHeaders.getVehicleCode().toString()  , warehouse_length ) +"  "+ subString ( vehicle.getVehicleName().toString()  , warehouse_length ) );
   			 			subHeader.append (NEWLINE + HORIZANTAL_LINE + NEWLINE );
   			 	    	subHeader.append ( LOAD_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
   				    	body.append(subHeader);
   							 
   				  }

   			         	 count=count+1;
   				
   			} // End for loop
   		  
   			body.append ( HORIZANTAL_LINE + NEWLINE );
   			body.append ( "                        Total  :"+quantityM +"/"+quantityS + "  | "     +NEWLINE );
   		 
   		} // End if
   		// Build the footer
   		
    		if ( type == Type.VANSTOCKCOUNT   ) {
   	        int lastPageM = movementDetails.size() / 38;
    			int restM = movementDetails.size() % 38;
    			footer = new StringBuilder ();
    			 
    			 if(pageNumber == ( lastPageM + 1 )    )
   		      {
    				 if(pageNumber==1)
    	  	  				if(( 38 - movementDetails.size() - 3 )>0)
    	  				{	  for(int u=0 ; u< ( 38 - movementDetails.size() - 3 )  ;u++)
    	  					footer.append ( NEWLINE );
    	  				}
    	  	  				else{
   			if(restM>4)
      	     for(int u=0 ; u< 38-restM-3 ;u++)
      	    	footer.append ( NEWLINE );
   		      }}
    			footer.append ( "SALES REP                       WAREHOUSE IN CHARGE                   SECURITY" );
           	footer.append ( "                                LAST PAGE");
    	} // End if
    	
  		 if ( type == Type.VANMANAGMENT   ) {
   			
  			for( int i = 0 ; i < movementDetails.size () ; i ++ ) {
  				
				MovementDetails tr = movementDetails.get ( i );
				                                
				body.append (   String.format ( "%-"+CODE_LENGTH+"s %"+(TOTAL_LENGTH+7)+"s %"+uom_LENGTH +"s %"+ (CASES_UNITS_Length-4 )+ "s %"+(QTY_LENGTH)+"s%" + (Values_Length-4) + "s "   , 
						tr.getItemCode ().length () > CODE_LENGTH ? tr.getItemCode ().substring ( 0 , CODE_LENGTH ) : tr.getItemCode ()	,
						tr.getItemName ().length () > (TOTAL_LENGTH+7)  ? tr.getItemName ().substring ( 0 ,  (TOTAL_LENGTH+7)  ) : tr.getItemName (),	
						DatabaseUtils.getInstance ( context ).getDaoSession ().getItemsDao ().queryBuilder ()
						.where ( ItemsDao.Properties.ItemCode.eq (  tr.getItemCode()   ) ).unique().getUnitMediumSmall()	,
						tr.getQuantityMedium().intValue()+"/" +tr.getQuantitySmall().intValue(),
						tr.getBasicUnitQuantity() ,	moneyFormat.format ( tr.getPriceSmall () ))  + NEWLINE );

				 
				 quantityM= quantityM+tr.getQuantityMedium();
				 quantityS=quantityS+tr.getQuantitySmall();
				 totalSales=totalSales+tr.getPriceSmall ();
   				
   				if(count % 38 == 0 && count != 0    )  
   				{  
   						body.append ( NEWLINE );
   				   		body.append ( NEWLINE );
   						body.append ( NEWLINE );
   				    	body.append ( NEWLINE );
   				    	body.append ( NEWLINE );
   				    	body.append ( NEWLINE );
   				    	body.append ( NEWLINE );
   				    	body.append ( NEWLINE );
   				    	body.append ( NEWLINE );
   				    	body.append ( NEWLINE );
   				    	body.append ( NEWLINE );
   				    	body.append ( NEWLINE );
   				    	body.append ( NEWLINE );
   				    	body.append ( NEWLINE );
   				    	body.append ( NEWLINE );
   				    	body.append ( NEWLINE );
   				    	body.append ( NEWLINE );
   				    	body.append ( NEWLINE );
   				 	   	subHeader = new StringBuilder ();
   				 	    pageNumber =pageNumber +1;
   				 	    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
   			 			subHeader.append ( " "+dATE +subString ( movementHeaders.getMovementDate().toString()  , ISSUE_DATE_Length )  + NEWLINE );
   			 		 	subHeader.append ( LOAD_NUM + movementHeaders.getMovementCode() + NEWLINE );
   			 		 		
   			 		 
   			 		 
   			 			subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
   			 		 
   			 			subHeader.append ("             VAN MANAGMENT        "+ NEWLINE);
   			 			subHeader.append ( NEWLINE );
   			 			subHeader.append(  LocationNumber + subString ( movementHeaders.getWarehouseCode()==null?"": movementHeaders.getWarehouseCode().toString() ,  warehouse_length )
   			 				        	+" " + REFERENCENumber + subString ( movementHeaders.getVehicleCode().toString()  , warehouse_length ) +"  "+ subString ( vehicle.getVehicleName().toString()  , warehouse_length ) );
   			 			subHeader.append (NEWLINE+ HORIZANTAL_LINE + NEWLINE );
   			 	    	subHeader.append ( LOAD_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
   				    	body.append(subHeader);
   							 
   				  }

   			         	 count=count+1;
   				
   			} // End for loop
   		  
   			body.append ( HORIZANTAL_LINE + NEWLINE );
   			body.append ( "                        Total  :"+quantityM +"/"+quantityS + "  |  " +NEWLINE );
   		 
   		} // End if
   		// Build the footer
   		
    		if ( type == Type.VANMANAGMENT   ) {
   	        int lastPageM= movementDetails.size() / 38;
    			int restM = movementDetails.size() % 38;
    			footer = new StringBuilder ();
    			 
    			 if(pageNumber == ( lastPageM + 1 )   )
   		      {
    				 if(pageNumber==1)
 	  	  				if(( 38 - movementDetails.size() - 3 )>0)
 	  				{	    for(int u=0 ; u< 38-movementDetails.size()  - 3 ;u++)
 	  					footer.append ( NEWLINE );
 	  				}
 	  	  				else{
   			if(restM>4)
      	     for(int u=0 ; u< 38-restM-3 ;u++)
      	    	footer.append ( NEWLINE );
   		      }}
    			footer.append ( "SALES REP                       WAREHOUSE IN CHARGE                   SECURITY" );
           	footer.append ( "                                LAST PAGE");
    	} // End if
    		
  		 if ( type == Type.MOVEMENTS   ) {
  			
  			double   MquantityM=0,MquantityS=0;
  			for( int i = 0 ; i < loadPrintings.size () ; i ++ ) {
  			
  				LoadPrinting  tr = loadPrintings.get ( i );
  				 
  				body.append (    String.format ( "%-"+8+"s %-"+16+"s %"+ 10 +"s %"+8 + "s %"+8+"s %" + 8+ "s %"  + 8 + "s %" + 6 + "s " , 
  							    tr.getItemCode ().length () > 8 ? tr.getItemCode ().substring ( 0 , 8 ) : tr.getItemCode ()	,
  							    tr.getItemName ().length () > 16 ? tr.getItemName ().substring ( 0 , 16 ) : tr.getItemName (), 
  							  	tr.getQuantityMedium().intValue()+"/" +tr.getQuantitySmall().intValue(),
  								tr.getMissedQuantityMedium().toString ()+"/" +tr.getMissedQuantitySmall(),
  								 tr.getStockBox().intValue() , tr.getStockQuantity().intValue(),
  								tr.getMissedBasicUnitQuantity().toString(),
  								tr.getBasicUnitQuantity().toString()  )  + NEWLINE );
  					
  			 
  				
  				  quantityM= quantityM+tr.getQuantityMedium();
  				  quantityS=quantityS+tr.getQuantitySmall();
  			      MquantityM=MquantityM+tr.getMissedQuantityMedium();
  				  MquantityS=MquantityS+tr.getMissedQuantitySmall();
  				
  				if(count % 38 == 0 && count != 0    )  
  				{  
  						body.append ( NEWLINE );
  					   	body.append ("                      Continued   "+ NEWLINE);
  						body.append ( NEWLINE );
  				    	body.append ( NEWLINE );
  				    	body.append ( NEWLINE );
  				    	body.append ( NEWLINE );
  				    	body.append ( NEWLINE );
  				    
  				    	body.append ( NEWLINE );
  				    	body.append ( NEWLINE );
  				    	body.append ( NEWLINE );
  				    	body.append ( NEWLINE );
  				    	body.append ( NEWLINE );
  				    	body.append ( NEWLINE );
  				    	body.append ( NEWLINE );
  				    	body.append ( NEWLINE );
  				    	body.append ( NEWLINE );
  				    	body.append ( NEWLINE );
  				 	   	subHeader = new StringBuilder ();
  				 	    pageNumber =pageNumber +1;
  				 	    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
  			 			subHeader.append ( " "+dATE +subString ( movementHeaders.getMovementDate().toString()  , ISSUE_DATE_Length )  + NEWLINE );
  			 		 	subHeader.append ( LOAD_NUM + movementHeaders.getMovementCode() + NEWLINE );
  			 		 		
  			 		 
  			 		 
  			 			subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
  			 		 
  			 			subHeader.append ("             MOVEMENTS         "+ NEWLINE);
  			 			subHeader.append ( NEWLINE );
  			 			subHeader.append(  LocationNumber + subString ( movementHeaders.getWarehouseCode()==null?"": movementHeaders.getWarehouseCode().toString() ,  warehouse_length )
  			 				        	+" " + REFERENCENumber + subString ( movementHeaders.getVehicleCode().toString()  , warehouse_length ) +"  "+ subString ( vehicle.getVehicleName().toString()  , warehouse_length ) );
  			 			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
  			 	    	subHeader.append ( LOAD_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
  				    	body.append(subHeader);
  							 
  				  }

  			         	 count=count+1;
  				
  			} // End for loop
  		  
  			body.append ( HORIZANTAL_LINE + NEWLINE );
  			body.append ( "                        Total  :"+quantityM +"/"+quantityS + "  | "+ MquantityM+"/"+MquantityS  +NEWLINE );
  		 
  		} // End if
  		// Build the footer
  		
   		if ( type == Type.MOVEMENTS   ) {
  	        int lastPageM= loadPrintings.size() / 38;
   			int restM = loadPrintings.size() % 38;
   			footer = new StringBuilder ();
   		 
   			 if(pageNumber == ( lastPageM + 1 )   )
  		      {
   				 if(pageNumber==1)
	  	  				if(( 38 - loadPrintings.size() - 3 )>0)
	  				{	    for(int u=0 ; u< 38-loadPrintings.size()  - 3 ;u++)
	  					footer.append ( NEWLINE );
	  				}
	  	  				else{
  			if(restM>4)
     	     for(int u=0 ; u< 38-restM-3 ;u++)
     	    	footer.append ( NEWLINE );
  		      }}
   			footer.append ( "SALES REP                       WAREHOUSE IN CHARGE                   SECURITY" );
          	footer.append ( "                                LAST PAGE");
   	} // End if
   		
   	 if ( type == Type.STOCKRECONCILIATION   ) {
			
			double   MquantityM=0,MquantityS=0,StockBox=0d,StockUnit=0d,totalAmount=0d;
			for( int i = 0 ; i < loadPrintings.size () ; i ++ ) {
			
				LoadPrinting  tr = loadPrintings.get ( i );
				                 
			//	Prices p=DatabaseUtils.getInstance ( context ).getDaoSession ().getPricesDao().queryBuilder ()
			//	         .where ( PricesDao.Properties.ItemCode .eq ( tr.getItemCode()  )).unique();
				body.append (String.format ( "%-"+8+"s %-"+18+"s %"+ 8 +"s %"+8 + "s %"+10+"s %" + 11 + "s %-" + 6 + "s "  , 
						    tr.getItemCode ().length () > 8 ? tr.getItemCode ().substring ( 0 , 8 ) : tr.getItemCode ()	,
						    tr.getItemName ().length () >21 ? tr.getItemName ().substring ( 0 , 21 ) : tr.getItemName (),
						    tr.getBasicUnitQuantity().intValue() ,
						    tr.getStockBox().intValue()+"/"+tr.getStockUnit().intValue() ,
						    tr.getQuantityMedium().intValue()+"/" + tr.getQuantitySmall().intValue(),
						    tr.getMissedQuantityMedium().intValue()+"/" + tr.getMissedQuantitySmall().intValue(),
						    tr.getPriceAmount()==null ? 0 :  moneyFormat.format (tr.getPriceAmount() )
							//tr.getStockQuantity().intValue()
							 )  + NEWLINE );
				 
				 quantityM= quantityM+tr.getQuantityMedium();
				 quantityS=quantityS+tr.getQuantitySmall();
				 MquantityM=MquantityM+tr.getMissedQuantityMedium();
			     MquantityS=MquantityS+tr.getMissedQuantitySmall();
			     StockBox=StockBox+tr.getStockBox().intValue();
			     StockUnit=StockUnit+tr.getStockUnit().intValue();
			     totalAmount=totalAmount+tr.getPriceAmount();
				if(count % 38 == 0 && count != 0    )  
				{  
						body.append ( NEWLINE );
					   	body.append ("                      Continued   "+ NEWLINE);
						body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE ); 
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	subHeader = new StringBuilder ();
  				 	    pageNumber =pageNumber +1;
  				 	    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
  			 			subHeader.append ( " "+dATE +subString ( movementHeaders.getMovementDate().toString()  , ISSUE_DATE_Length )  + NEWLINE );
  			 		 	subHeader.append ( LOAD_NUM + movementHeaders.getMovementCode() + NEWLINE );
  			 		 		
  			 	 
  			 		 
  			 			subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
  			 		 
  			 			subHeader.append ("            STOCK RECONCILIATION         "+ NEWLINE);
  			 			subHeader.append ( NEWLINE );
  			 			subHeader.append(  LocationNumber + subString ( movementHeaders.getWarehouseCode() == null ? "": movementHeaders.getWarehouseCode().toString() ,  warehouse_length )
  			 				        	+" " + REFERENCENumber + subString ( movementHeaders.getVehicleCode().toString()  , warehouse_length ) +"  "+ subString ( vehicle.getVehicleName().toString()  , warehouse_length ) );
  			 			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
			 	    	subHeader.append ( STOCKRECONCILIATION_LOAD_HEADER_new + NEWLINE + HORIZANTAL_LINE + NEWLINE );
				    	body.append(subHeader);
							 
				  }

			         	 count=count+1;
				
			} // End for loop
		  
			body.append ( HORIZANTAL_LINE + NEWLINE );
			body.append ( "                            Total  :"+(int)StockBox+"/" +(int)StockUnit +" | " +(int)quantityM +"/"+(int)quantityS + "  | "+ (int)MquantityM+"/"+(int)MquantityS+NEWLINE+
					      "                            Total Amount :"+  moneyFormat.format (totalAmount) );
	        
			int lastPageM= loadPrintings.size() / 38;
			int restM = loadPrintings.size() % 38;
			footer = new StringBuilder ();
			
			 if(pageNumber == ( lastPageM + 1 )   )
		      {
				if(pageNumber==1)
	  				if(( 38 - loadPrintings.size() - 3 )>0)
				{	  for(int u=0 ; u< ( 38 - loadPrintings.size() - 3 )  ;u++)
					body.append ( NEWLINE );
				}
	  				else{
			if(restM>4)
 	     for(int u=0 ; u< 38-restM-3 ;u++)
 	    	 body.append ( NEWLINE );
		      }
				}
			body.append ( "SALES REP                       WAREHOUSE IN CHARGE                   SECURITY" );
			body.append ( "                                LAST PAGE");

			
			
		} // End if
		// Build the footer
   		
//  		 if ( type == Type.STOCKRECONCILIATION   ) {
//  			
//  			double   MquantityM=0,MquantityS=0,stockQ=0;
//  			for( int i = 0 ; i < movementDetails.size () ; i ++ ) {
//  			
//  				MovementDetails  tr = movementDetails.get ( i );
//  				 
//  				body.append (   String.format ( "%-"+10+"s %-"+21+"s %"+10 +"s %"+ 12 + "s %"+12+"s"  , 
//  							    tr.getItemCode ().length () > 10 ? tr.getItemCode ().substring ( 0 , 10 ) : tr.getItemCode ()	,
//  							    tr.getItemName ().length () > 23 ? tr.getItemName ().substring ( 0 , 23 ) : tr.getItemName (),
//  							    tr.getQuantityMedium().intValue()+"/" +tr.getQuantitySmall().intValue(),
//  								tr.getMissedQuantityMedium().intValue()+"/" +tr.getMissedQuantitySmall().intValue(),
//  								tr.getStockQuantity().intValue()  )  + NEWLINE );
//  					
//  			 
//  				
//  				 quantityM= quantityM+tr.getQuantityMedium();
//  				 quantityS=quantityS+tr.getQuantitySmall();
//  				 MquantityM=MquantityM+tr.getMissedQuantityMedium();
//  				 MquantityS=MquantityS+tr.getMissedQuantitySmall();
//  				 stockQ=stockQ+tr.getStockQuantity();
//  				if(count % 38 == 0 && count != 0    )  
//  				{  
//  						body.append ( NEWLINE );
//  				   		body.append ( NEWLINE );
//  						body.append ( NEWLINE );
//  				    	body.append ( NEWLINE );
//  				    	body.append ( NEWLINE );
//  				    	body.append ( NEWLINE );
//  				    	body.append ( NEWLINE );
//  				    
//  				    	body.append ( NEWLINE );
//  				    	body.append ( NEWLINE );
//  				    	body.append ( NEWLINE );
//  				    	body.append ( NEWLINE );
//  				    	body.append ( NEWLINE );
//  				    	body.append ( NEWLINE );
//  				    	body.append ( NEWLINE );
//  				    	body.append ( NEWLINE );
//  				    	body.append ( NEWLINE );
//  				    	body.append ( NEWLINE );
//  				 	   	subHeader = new StringBuilder ();
//  				 	    pageNumber =pageNumber +1;
//  				 	    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
//  			 			subHeader.append ( " "+dATE +subString ( movementHeaders.getMovementDate().toString()  , ISSUE_DATE_Length )  + NEWLINE );
//  			 		 	subHeader.append ( LOAD_NUM + movementHeaders.getMovementCode() + NEWLINE );
//  			 		 		
//  			 	 
//  			 		 
//  			 			subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
//  			 		 
//  			 			subHeader.append ("            STOCK RECONCILIATION         "+ NEWLINE);
//  			 			subHeader.append ( NEWLINE );
//  			 			subHeader.append(  LocationNumber + subString ( movementHeaders.getWarehouseCode()==null?"": movementHeaders.getWarehouseCode().toString() ,  warehouse_length )
//  			 				        	+" " + REFERENCENumber + subString ( movementHeaders.getVehicleCode().toString()  , warehouse_length ) +"  "+ subString ( vehicle.getVehicleName().toString()  , warehouse_length ) );
//  			 			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
//  			 	    	subHeader.append ( STOCKRECONCILIATION_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
//  				    	body.append(subHeader);
//  							 
//  				  }
//
//  			         	 count=count+1;
//  				
//  			} // End for loop
//  		  
//  			body.append ( HORIZANTAL_LINE + NEWLINE );
//  			body.append ( "                                   Total  :"+(int)quantityM +"/"+(int)quantityS + "  | "+ (int)MquantityM+"/"+(int)MquantityS   +"  |  "+stockQ +NEWLINE );
//  		 
//  		} // End if
//  		// Build the footer
//  		
//   		if ( type == Type.STOCKRECONCILIATION   ) {
//  	        int lastPageM= movementDetails.size() / 38;
//   			int restM = movementDetails.size() % 38;
//   			footer = new StringBuilder ();
//   		 
//   			 if(pageNumber == ( lastPageM + 1 )    )
//  		      {
//   				 if(pageNumber==1)
//	  	  				if(( 38 - movementDetails.size() - 3 )>0)
//	  				{	    for(int u=0 ; u< 38-movementDetails.size()  - 3 ;u++)
//	  					footer.append ( NEWLINE );
//	  				}
//	  	  				else{
//  			if(restM>4)
//     	     for(int u=0 ; u< 38-restM-3 ;u++)
//     	    	footer.append ( NEWLINE );
//  		      }}
//   			footer.append ( "SALES REP                       WAREHOUSE IN CHARGE                   SECURITY" );
//          	footer.append ( "                                LASTE PAGE");
//   	 } // End if 
  		
        	//VEHICULES STOCK	
        	 if ( type == Type.VEHICULESSTOCK   ) {
			
		 	 
		       for( int i = 0 ; i < vehiclesStock.size () ; i ++ ) {
			
		    	   vehiclesStockPrinting  tr = vehiclesStock.get ( i );
			    	 
				 
				body.append (   String.format ( "%-"+9+"s %"+17+"s %"+7 +"s %"+ 20 + "s %"+7+"s%"+10+"s"  ,
						        tr.getVehicleCode().length () >9 ? tr.getVehicleCode().substring ( 0 , 9 ) : tr.getVehicleCode (),	
						        tr.getVehicleName().length () >17 ? tr.getVehicleName().substring ( 0 , 17 ) : tr.getVehicleName()	 ,		
							    tr.getItemCode ().length () >7 ? tr.getItemCode ().substring ( 0 , 7 ) : tr.getItemCode ()	,
							    tr.getItemName().length () >20 ? tr.getItemName().substring ( 0 , 20 ) : tr.getItemName() ,
							  	tr.getGooodquantityMedium().intValue()+" / "+tr.getGoodquantitySmall().intValue() ,
								tr.getDamageQuantityMedium().intValue()+" / "+tr.getDamageQuantitySmall() )  + NEWLINE );
			 	
				 
				if(count % 38 == 0 && count != 0    )  
				{  
						body.append ( NEWLINE );
					   	body.append ("                      Continued   "+ NEWLINE);
						body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				 	   	subHeader = new StringBuilder ();
				 	    pageNumber =pageNumber +1;
				 	   
				 	    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
					 	subHeader.append ("            VEHICULES STOCK       "+ NEWLINE);
						subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
						subHeader.append ( HORIZANTAL_LINE + NEWLINE );
				    	subHeader.append ( Vehicules_Stock_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
				    	
				    	body.append(subHeader);
							 
				  }

			         	 count=count+1;
				
			} // End for loop
 		} // End if
		// Build the footer
		
		if ( type == Type.VEHICULESSTOCK   ) {
	        int lastPageM= vehiclesStock.size() / 38;
			int restM = vehiclesStock.size() % 38;
			footer = new StringBuilder ();
		 
			 if(pageNumber == ( lastPageM + 1 )  )
		      {
				 if(pageNumber==1)
	  	  				if(( 38 - vehiclesStock.size() - 3 )>0)
	  				{	    for(int u=0 ; u< 38-vehiclesStock.size()  - 3 ;u++)
	  					footer.append ( NEWLINE );
	  				}
	  	  				else{
			if(restM>4)
  	     for(int u=0 ; u< 38-restM-3 ;u++)
  	    	footer.append ( NEWLINE );
		      }}
			footer.append ( "SALES REP                       WAREHOUSE IN CHARGE                   SECURITY" );
       	    footer.append ( "                                LAST PAGE");
     	 } // End if 
		
		  if ( type == Type.APPROVERETURN ) {
			   double AquantityM=0d, AquantityS=0d,MquantityM=0d,MquantityS=0d;
			   for( int i = 0 ; i < transactionDetails.size () ; i ++ ) {
				 TransactionDetails tr = transactionDetails.get ( i );
						
					body.append ( String.format ( "%-"+CODE_LENGTH+"s %"+TOTAL_LENGTH+"s %"+12 +"s %"+ 14+ "s %"+14+"s "  , 
				    tr.getItemCode ().length () > CODE_LENGTH ? tr.getItemCode ().substring ( 0 , CODE_LENGTH ) : tr.getItemCode ()	
					, tr.getItemName ().length () >  TOTAL_LENGTH  ? tr.getItemName ().substring ( 0 ,  TOTAL_LENGTH  ) : tr.getItemName (),	
					tr.getQuantityMedium()+"/"+tr.getQuantitySmall(),tr.getApprovedQuantityMedium()+"/"+tr.getApprovedQuantitySmall(),
					tr.getMissedQuantityMedium()+"/"+tr.getMissedQuantitySmall()) + NEWLINE );
					
					 quantityM= quantityM+tr.getQuantityMedium();
					 quantityS=quantityS+tr.getQuantitySmall();
					 AquantityM=AquantityM+tr.getApprovedQuantityMedium();
					 AquantityS=AquantityS+tr.getApprovedQuantitySmall();
					 MquantityM=MquantityM+tr.getMissedQuantityMedium();
					 MquantityS=MquantityS+tr.getMissedQuantitySmall();
					
					 if(count % 38 == 0 && count != 0    )  
					{  
						
					   	body.append ( NEWLINE );
					   	body.append ("                      Continued   "+ NEWLINE);
					    body.append ( NEWLINE );
					    body.append ( NEWLINE );
					    body.append ( NEWLINE );
					    body.append ( NEWLINE );
					    body.append ( NEWLINE );
					    
					    body.append ( NEWLINE );
					    body.append ( NEWLINE );
					    body.append ( NEWLINE );
					    body.append ( NEWLINE );
					    body.append ( NEWLINE );
					    body.append ( NEWLINE );
					    body.append ( NEWLINE );
					    body.append ( NEWLINE );
					    body.append ( NEWLINE );
					    body.append ( NEWLINE );
					    body.append ( NEWLINE );
					    pageNumber =pageNumber +1;
					 	   
					    subHeader = new StringBuilder ();
					    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length )  );
						subHeader.append ( " "+ISSUE_DATE +subString ( transactionHeader.getIssueDate ().toString()  , ISSUE_DATE_Length )  + NEWLINE );
						subHeader.append ( ORDER_NUM + transactionHeader.getTransactionCode () + NEWLINE );	
				
						subHeader.append ( NEWLINE );
			        	subHeader.append ( CLIENT_ID +subString ( client.getClientCode () ,CLIENT_ID_length) );
				    	subHeader.append ( " "+ subString (client.getClientName ()  ,CLIENT_Name_length) + NEWLINE );
				     	subHeader.append (  "                "+   client.getClientAddress ()    + NEWLINE );
				        subHeader.append ( PRINT_DATE + now.getTime ()   + NEWLINE );
				       	subHeader.append ( HORIZANTAL_LINE + NEWLINE );
						subHeader.append ( Approve_Return_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
						body.append(subHeader);
					 
					}
	
				   count=count+1;
					 
				} // End for loop
			
		    	body.append ( HORIZANTAL_LINE + NEWLINE );
		    	body.append ( "                          Total Sales :"+quantityM +"/"+quantityS +"  "+ AquantityM+"/"+AquantityS +"  "+ MquantityM+"/"+MquantityS+NEWLINE );
 
		 	   }
			if ( type == Type.APPROVERETURN    ) {
				int lastPage1= transactionDetails.size() / 38;
				int rest1 = transactionDetails.size() % 38; 
				 
				 if(pageNumber == ( lastPage1 + 1 )    )
			      {
					 if(pageNumber==1)
		  	  				if(( 38 - transactionDetails.size() - 3 )>0)
		  				{	    for(int u=0 ; u< 38-transactionDetails.size()  - 3 ;u++)
		  					footer.append ( NEWLINE );
		  				}
		  	  				else{
				if(rest1>4)
	    	     for(int u=0 ; u< 38-rest1-3 ;u++)
	    	    	footer.append ( NEWLINE );
			      }}
				footer.append(  HORIZANTAL_LINE + NEWLINE);
				footer.append ( "SALES REP                  Last Page                        CUSTOMER SIGN/STAMP" );
			 
			} // End if
			
			   if ( type == Type.CLIENTSTOCKCOUNT ) {
				   double  totalAmount=0d ,priceM=0d,priceS=0d,totalAmounts=0d;
				   for( int i = 0 ; i < clientStockCountDetails.size () ; i ++ ) {
					    ClientStockCountDetails tr = clientStockCountDetails.get ( i );
					    totalAmount=0d;
					    String itemName= DatabaseUtils.getInstance ( context ).getDaoSession ().getItemsDao().queryBuilder ()
								        .where ( ItemsDao.Properties.ItemCode.eq (  tr.getItemCode()   ) ).unique().getItemName() ;
					    totalAmount=tr.getQuantityMedium()*tr.getPriceMedium()+tr.getQuantitySmall()*tr.getPriceSmall();
						body.append ( String.format ( "%-"+CODE_LENGTH+"s %"+TOTAL_LENGTH+"s %"+9 +"s %"+ 9+ "s %"+12+"s%" +11+"s" , 
					    tr.getItemCode ().length () > CODE_LENGTH ? tr.getItemCode ().substring ( 0 , CODE_LENGTH ) : tr.getItemCode ()	
						, itemName.length () >  TOTAL_LENGTH  ? itemName.substring ( 0 ,  TOTAL_LENGTH  ) : itemName,	
						tr.getQuantityMedium()+"/"+tr.getQuantitySmall(), moneyFormat.format (tr.getPriceMedium())
						+"/"+  moneyFormat.format (tr.getPriceSmall()),
						moneyFormat.format (totalAmount),(tr.getIsMerchandize()==1?"MERCHIDIZE":" ")) + NEWLINE );
						
						 quantityM= quantityM+tr.getQuantityMedium();
						 quantityS=quantityS+tr.getQuantitySmall();
						 priceM=priceM+tr.getPriceMedium();
						 priceS=priceS+tr.getPriceSmall();
						 totalAmounts=totalAmounts+totalAmount;
						 if(count % 38 == 0 && count != 0    )  
						{  
							
						   	body.append ( NEWLINE );
						   	body.append ("                      Continued   "+ NEWLINE);
						    body.append ( NEWLINE );
						    body.append ( NEWLINE );
						    body.append ( NEWLINE );
						    body.append ( NEWLINE );
						    body.append ( NEWLINE );
						    
						    body.append ( NEWLINE );
						    body.append ( NEWLINE );
						    body.append ( NEWLINE );
						    body.append ( NEWLINE );
						    body.append ( NEWLINE );
						    body.append ( NEWLINE );
						    body.append ( NEWLINE );
						    body.append ( NEWLINE );
						    body.append ( NEWLINE );
						    body.append ( NEWLINE );
						    body.append ( NEWLINE );
						    pageNumber =pageNumber +1;
						 	   
						    subHeader = new StringBuilder ();
						    subHeader.append ( " "+ISSUE_DATE +subString ( clientStockCountHeaders.getDate().toString()  , ISSUE_DATE_Length )  + NEWLINE );
							subHeader.append ( LOAD_NUM + clientStockCountHeaders.getTransactionCode () + NEWLINE );	
					
							subHeader.append ( NEWLINE );
				        	subHeader.append ( CLIENT_ID +subString ( client.getClientCode () ,CLIENT_ID_length) );
					    	subHeader.append ( " "+ subString (client.getClientName ()  ,CLIENT_Name_length) + NEWLINE );
					     	subHeader.append (  "                "+   client.getClientAddress ()    + NEWLINE );
					        subHeader.append ( PRINT_DATE + now.getTime ()   + NEWLINE );
					        subHeader.append ("TYPE: " +(clientStockCountHeaders.getCountType()==1 ?"BY SHELF":"STOCK"));
					       	subHeader.append ( HORIZANTAL_LINE + NEWLINE );
							subHeader.append ( Client_Stock_Count_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
							body.append(subHeader);
						 
						}
		
					   count=count+1;
						 
					} // End for loop
				
			    	body.append ( HORIZANTAL_LINE + NEWLINE );
			    	body.append ( "                         Total  :"+quantityM +"/"+quantityS +"  "+ priceM+"/"+priceS +"  "+ moneyFormat.format (totalAmounts) + NEWLINE );
	 
			 	   }
				if ( type == Type.CLIENTSTOCKCOUNT    ) {
					int lastPage1= clientStockCountDetails.size() / 38;
					int rest1 = clientStockCountDetails.size() % 38; 
				 
					 
					 if(pageNumber == ( lastPage1 + 1 )    )
				      {
						 if(pageNumber==1)
			  	  				if(( 38 - clientStockCountDetails.size() - 3 )>0)
			  				{	    for(int u=0 ; u< 38-clientStockCountDetails.size()  - 3 ;u++)
			  					footer.append ( NEWLINE );
			  				}
			  	  				else{
					if(rest1>4)
		    	     for(int u=0 ; u< 38-rest1-3 ;u++)
		    	    	footer.append ( NEWLINE );
				         }
						 }
					footer.append(  HORIZANTAL_LINE + NEWLINE);
					footer.append ( "SALES REP                  Last Page                        CUSTOMER SIGN/STAMP" );
				 
				} // End if
		     	 
				 //COLLECTION SUMMARY	
	        	 if ( type == Type.COLLECTIONSUMMARY   ) {
	        		   ArrayList < CollectionSummaryPrinting > collectionHeadersCash = new ArrayList < CollectionSummaryPrinting > ();
	        		   ArrayList < CollectionSummaryPrinting > collectionHeadersCheck = new ArrayList < CollectionSummaryPrinting > ();
	        	 
	        		   ArrayList < CollectionSummaryPrinting > collectionHeadersCashCredit = new ArrayList < CollectionSummaryPrinting > ();
	        		   ArrayList < CollectionSummaryPrinting > collectionHeadersCashCash= new ArrayList < CollectionSummaryPrinting > ();
	        		   ArrayList < CollectionSummaryPrinting > collectionHeadersCredit= new ArrayList < CollectionSummaryPrinting > ();
	        		 	   
	        		   ArrayList < SummaryReturnPrinting > summaryReturnPrintingCash  = new ArrayList < SummaryReturnPrinting > ();
 	        		   ArrayList < SummaryReturnPrinting > summaryReturnPrintingCredit= new ArrayList < SummaryReturnPrinting > ();
	        		  
	        			  for( int i = 0 ; i < summaryReturnPrintings.size () ; i ++ ) {
	        				  SummaryReturnPrinting    tr = summaryReturnPrintings.get ( i );
						         if( tr.getClientType() == 1 || (tr.getInfo1()!=null && tr.getInfo1().equals("1")))
						        	 summaryReturnPrintingCash.add( tr );
						         else
						        	 summaryReturnPrintingCredit.add( tr );
						         } 
	        		   for( int i = 0 ; i < collectionSummaryHeaderss.size () ; i ++ ) {
					         CollectionSummaryPrinting   tr = collectionSummaryHeaderss.get ( i );
					         if( tr.getCollectionDetailType() == 1 )
					        	 collectionHeadersCheck.add( tr );
					         else
					        	 collectionHeadersCash.add( tr );
					         }
	        		   double totalCash=0d ,totalCheck=0d,tcash=0d,tcredit=0d,trcash,trcredit;
	        		   double totalReturnCash=0d,totalReturnCredit=0d;
	        		   double totalcred=0;
	        		   if( collectionHeadersCash.size() > 0 ){
	        			  body.append ( COLLECTION_SUMMARY_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
	        			 //  body.append ("        Cash      " +NEWLINE);
	        			
	        		   for( int i = 0 ; i < collectionHeadersCash.size () ; i ++ ) {
	        			   CollectionSummaryPrinting   tr = collectionHeadersCash.get ( i );
	        			   List < CollectionInvoices > collectionInvoices = new ArrayList < CollectionInvoices > ();
	        			  
	        			   collectionInvoices = DatabaseUtils.getInstance ( context ).getDaoSession ().getCollectionInvoicesDao().queryBuilder ()
						            .where ( CollectionInvoicesDao.Properties.CollectionCode.eq (  tr.getCollectionCode()),CollectionInvoicesDao.Properties.CompanyCode.eq (  tr.getCompanyCode()),CollectionInvoicesDao.Properties.DivisionCode.eq (  tr.getDivisionCode()) ).list();
	        			   CollectionInvoices   collectionInvoice = new CollectionInvoices();
	        			 
	        			 
	        			   if(collectionInvoices.isEmpty())
	        				  collectionHeadersCashCash.add(tr);
	        			  // if(  collectionInvoices.getInvoiceSource () == 1)
	        			  //{
	        				 
	        			 // }
	        			else{ 
	        				collectionInvoice = collectionInvoices.get(0);
         				List <TransactionHeaders>	transHeader = DatabaseUtils.getInstance ( context ).getDaoSession ().getTransactionHeadersDao().queryBuilder ()
 					            .where ( TransactionHeadersDao.Properties.TransactionCode.eq (  collectionInvoice.getInvoiceCode()) ).list();
 	        			if(transHeader.size()>0){
 	        	
	        				if( transHeader.get(0).getInfo1().equals("") && transHeader.get(0).getInfo2().equals(""))
	        				{
	        				if( collectionInvoice.getInvoiceSource () != null){
	           			if( collectionInvoice.getInvoiceSource () == 2){
						   Clients c=  DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
							        .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  ),ClientsDao.Properties.CompanyCode.eq (  tr.getCompanyCode())   ).unique()  ;
						   if(c.getClientType()==1)
	        				  //collectionHeadersCashCredit.add(tr);
	        				  collectionHeadersCashCash.add(tr);
							else
                            collectionHeadersCashCredit.add(tr);							
	        			 }
						 else if( collectionInvoice.getInvoiceSource () == 1){//jose
							   Clients c=  DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
						        .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  ),ClientsDao.Properties.CompanyCode.eq (  tr.getCompanyCode())   ).unique()  ;
							   if(c.getClientType()==2 && transHeader.get(0).getRemainingAmount() == 0)
								   collectionHeadersCashCredit.add(tr);
							   else
								   collectionHeadersCashCash.add(tr);
						 }
	        			
	        		   }
	        				else 
		        				  collectionHeadersCashCash.add(tr);
	        			   
	        		   }
					   else
					   if(transHeader.get(0).getInfo1().equals("1") || transHeader.get(0).getInfo2().equals("1"))
						   collectionHeadersCashCash.add(tr);	
 	        			}
 	        			else{
 	        				collectionInvoice = collectionInvoices.get(0);
	        				if( collectionInvoice.getInvoiceSource () != null){
	           			if( collectionInvoice.getInvoiceSource () == 2){
						   Clients c=  DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
							        .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  ),ClientsDao.Properties.CompanyCode.eq (  tr.getCompanyCode())   ).unique()  ;	    ;
						   
							  if( c.getClientType()==1 )
	        				  //collectionHeadersCashCredit.add(tr);
	        				  collectionHeadersCashCash.add(tr);
							else
                            collectionHeadersCashCredit.add(tr);							
	        			  }
						  else if( collectionInvoice.getInvoiceSource () == 1)
	        				  collectionHeadersCashCash.add(tr);
	        			
	        		   }
	        				else 
		        				  collectionHeadersCashCash.add(tr);
	        			   
	        		   
 	        			}
//	        				collectionInvoice = collectionInvoices.get(0);
//	        				List <TransactionHeaders>	transHeader = DatabaseUtils.getInstance ( context ).getDaoSession ().getTransactionHeadersDao().queryBuilder ()
//						            .where ( TransactionHeadersDao.Properties.TransactionCode.eq (  collectionInvoice.getInvoiceCode()) ).list();
//	        			if(transHeader.size()>0)
//	        				if( transHeader.get(0).getInfo1().equals("") && transHeader.get(0).getInfo2().equals("")&& transHeader.get(0).getTransactionPasswordCode()==null)
//	        				{
//	        					collectionHeadersCredit.add(tr);
//	        				}
//	        				if( collectionInvoice.getInvoiceSource () != null){
//	           			if( collectionInvoice.getInvoiceSource () == 2){
//	        				  collectionHeadersCashCredit.add(tr);
//	        				  
//	        			  }else if( collectionInvoice.getInvoiceSource () == 1)
//	        				  collectionHeadersCashCash.add(tr);
//	        			
//	        		   }
//	        				else 
//		        				  collectionHeadersCashCash.add(tr);
//	        			   
	        		   
	        			
	        			
	        			}
	        			}
	        		     
//	        		   for ( CollectionSummaryPrinting cd : collectionHeadersCredit )
//	        		   {
//	        			 for( CollectionSummaryPrinting ccc : collectionHeadersCashCash)
//	        			   if(ccc.equals(cd))
//	        				  collectionHeadersCashCash.remove(ccc);
//	        				 
//	        		   }
	        		   
	        		   
//	        		   for(int i = 0; i<collectionHeadersCredit.size(); i++){
//	        			   
//	        			   for(int j = 0; j<collectionHeadersCashCash.size(); j++){
//	        			   
//	        			       if(collectionHeadersCashCash.get(j).equals(collectionHeadersCredit.get(i)))
//	        			    		 {
//	        			   
//	        			    	   collectionHeadersCashCash.remove(j);
//	        			   
//	        			           j--;
//	        			   
//	        			      			   
//	        			       }
//	        			   
//	        			   }
//	        			   }

	        		   if( collectionHeadersCashCash.size() > 0 ) {
	        			   body.append (  " Cash Collection"+NEWLINE);
	        			   count = count + 1;
	        		   for( int i = 0 ; i < collectionHeadersCashCash.size () ; i ++ ) {
		        			CollectionSummaryPrinting   tr = collectionHeadersCashCash.get ( i ); 
	        			   
				    	 //String clientName = DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
					       //     .where ( ClientsDao.Properties.ParentCode.eq (  tr.getClientCode()  ),ClientsDao.Properties.CompanyCode.eq (  tr.getCompanyCode())   ).unique().getClientName();
					    
		        			    String clientName="",clientAddress="";
			        			switch ( tr.getCollectionType() ) {
			        			case 1:
			        			case 3:
			        				ArrayList< Clients> cc=(ArrayList<Clients>) DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
						            .where ( ClientsDao.Properties.ParentCode.eq (  tr.getClientCode()  )  ).list();
			        				if(cc !=null && cc.size()>0){
			        					clientName=cc.get(0).getClientName();
			        					clientAddress=cc.get(0).getClientAddress();
			        				}
			        				break;
			        			case 2:
			        		
			        				 clientName = DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
							            .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  ), ClientsDao.Properties.CompanyCode.eq (  tr.getCompanyCode()) ).unique().getClientName();
			        				 clientAddress=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
									            .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  ), ClientsDao.Properties.CompanyCode.eq (  tr.getCompanyCode()) ).unique().getClientAddress();
			        		
			        				break;
			        			}
			        		
		        			String currencyName = DatabaseUtils.getInstance ( context ).getDaoSession ().getCurrenciesDao().queryBuilder ()
							 .where ( CurrenciesDao.Properties.CurrencyCode.eq (  tr.getCurrencyCode()   )   ).unique().getCurrencyName() ;
					    
					     count = count + 1;
					 	 body.append ( String.format ( "%-"+15+"s %-"+10+"s %"+20 +"s %"+ 11 + "s %"+15+"s"   ,
					 			 	tr.getCollectionCode(),
					 			 	tr.getClientCode().length () >10 ? tr.getClientCode().substring ( 0 , 10 ) : tr.getClientCode (),	
					 			 	clientName.length () >20 ? clientName.substring ( 0 , 20 ) : clientName	 ,
								    moneyFormat.format( tr.getTotalAmount()), 
								    clientAddress.length () >10 ? clientAddress.substring ( 0 , 10 ) : clientAddress
								    		
									     )  + NEWLINE );
					 	totalCash = totalCash+tr.getTotalAmount();
					 	tcash +=tr.getTotalAmount();
					if(count % 36 == 0 && count != 0    )  
					{  
							body.append ( NEWLINE );
						   	body.append ("                      Continued   "+ NEWLINE);
							body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					 	   	subHeader = new StringBuilder ();
					 	    pageNumber = pageNumber +1;
					 	    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
						 	subHeader.append ("            COLLECTION SUMMARY       "+ NEWLINE);
							subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
							subHeader.append ( HORIZANTAL_LINE + NEWLINE );
					    	subHeader.append ( COLLECTION_SUMMARY_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
					    	
					    	body.append(subHeader);
								 
					  }
						//count = count+1;
					
				} // End for loop
	        		   body.append ( HORIZANTAL_LINE + NEWLINE );
	        		   body.append ( "        Total Cash Collection  "+moneyFormat.format(totalCash)  +  NEWLINE);
	        		   count = count + 2;
	        		   }
	        		 
//	        		   if(collectionHeadersCashCredit.size()>0){
//	        			   tcredit  =0d;
//	        			 	body.append (  "Cash Credit Collection"+NEWLINE);
//	        		     for( int i = 0 ; i < collectionHeadersCashCredit.size () ; i ++ ) {
//		        			CollectionSummaryPrinting   tr = collectionHeadersCashCredit.get ( i ); 
//		        			 String clientName="",clientAddress="";
//		        			switch ( tr.getCollectionType() ) {
//		        			case 1:
//		        			case 3:
//		        				  clientName=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
//						            .where ( ClientsDao.Properties.ParentCode.eq (  tr.getClientCode()  ), ClientsDao.Properties.CompanyCode.eq (  tr.getCompanyCode()) ).unique().getClientName();
//		        				  clientAddress=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
//								            .where ( ClientsDao.Properties.ParentCode.eq (  tr.getClientCode()  ), ClientsDao.Properties.CompanyCode.eq (  tr.getCompanyCode()) ).unique().getClientAddress();
//		        		
//		        				break;
//		        			case 2:
//		        		
//		        				 clientName=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
//						            .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  ), ClientsDao.Properties.CompanyCode.eq (  tr.getCompanyCode()) ).unique().getClientName();
//		        				  clientAddress=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
//								            .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  ), ClientsDao.Properties.CompanyCode.eq (  tr.getCompanyCode()) ).unique().getClientAddress();
//		        		
//		        				break;
//		        			}
//		        			 
//				    	  String currencyName= DatabaseUtils.getInstance ( context ).getDaoSession ().getCurrenciesDao().queryBuilder ()
//							 .where ( CurrenciesDao.Properties.CurrencyCode.eq (  tr.getCurrencyCode()   ) ).unique().getCurrencyName() ;
//					
//					 	  count = count + 1;
//					 	body.append ( String.format ( "%-"+15+"s %-"+10+"s %"+20 +"s %"+ 11 + "s %"+15+"s"   ,
//								   tr.getCollectionCode(),
//								   tr.getClientCode().length () >10 ? tr.getClientCode().substring ( 0 , 10 ) : tr.getClientCode (),	
//								   clientName.length () >20 ? clientName.substring ( 0 , 20 ) : clientName	 ,
//								   moneyFormat.format( tr.getTotalAmount()), 
//								   clientAddress.length () >10 ? clientAddress.substring ( 0 , 10 ) : clientAddress	 
//									     )  + NEWLINE );
//					 	
//					 tcredit+=tr.getTotalAmount();
//					if(count % 36 == 0 && count != 0    )  
//					{  
//							body.append ( NEWLINE );
//					   		body.append ( NEWLINE );
//							body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					 	   	subHeader = new StringBuilder ();
//					 	    pageNumber =pageNumber +1;
//					 	    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
//						 	subHeader.append ("            COLLECTION SUMMARY       "+ NEWLINE);
//							subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
//							subHeader.append ( HORIZANTAL_LINE + NEWLINE );
//					    	subHeader.append ( COLLECTION_SUMMARY_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
//					    	
//					    	body.append(subHeader);
//								 
//					  }
//
//				         	 count=count+1;
//					
//				} // End for loop
//	        		   body.append ( HORIZANTAL_LINE + NEWLINE );
//	        		   body.append ( "        Total Cash Credit Collection  "+moneyFormat.format( tcredit )  +  NEWLINE);
//	        		   count=count+2;
//	        		   } 
//	        		   if(collectionHeadersCashCash.size()>0 && collectionHeadersCashCredit.size()>0)
//	        		   {  count=count+1;
//	        		   body.append ( "        Total Cash Collection  "+moneyFormat.format( tcredit + tcash )  +  NEWLINE);
//	        		   }
	        		   }// End if
	        		   collectionHeadersCredit.addAll( collectionHeadersCashCredit);
	        		   if( collectionHeadersCredit.size() > 0 ) {
	        			   body.append (  " Credit Collection"+NEWLINE);
	        			  
	        		   for( int i = 0 ; i < collectionHeadersCredit.size () ; i ++ ) {
		        			CollectionSummaryPrinting   tr = collectionHeadersCredit.get ( i ); 
	        			   
				    	 //String clientName = DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
					       //     .where ( ClientsDao.Properties.ParentCode.eq (  tr.getClientCode()  ),ClientsDao.Properties.CompanyCode.eq (  tr.getCompanyCode())   ).unique().getClientName();
					    
		        			    String clientName="",clientAddress="";
			        			switch ( tr.getCollectionType() ) {
			        			case 1:
			        			case 3:
			        				ArrayList< Clients> cc=(ArrayList<Clients>) DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
						            .where ( ClientsDao.Properties.ParentCode.eq (  tr.getClientCode()  )  ).list();
			        				if(cc !=null && cc.size()>0){
			        					clientName=cc.get(0).getClientName();
			        					clientAddress=cc.get(0).getClientAddress();
			        				}
			        				break;
			        			case 2:
			        				 clientName = DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
							            .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  ), ClientsDao.Properties.CompanyCode.eq (  tr.getCompanyCode()) ).unique().getClientName();
			        				 clientAddress=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
									            .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  ), ClientsDao.Properties.CompanyCode.eq (  tr.getCompanyCode()) ).unique().getClientAddress();
			        		
			        				break;
			        			}
			        		
		        			String currencyName = DatabaseUtils.getInstance ( context ).getDaoSession ().getCurrenciesDao().queryBuilder ()
							 .where ( CurrenciesDao.Properties.CurrencyCode.eq (  tr.getCurrencyCode()   )   ).unique().getCurrencyName() ;
					    
					    count = count + 1;
					 	 body.append ( String.format ( "%-"+15+"s %-"+10+"s %"+20 +"s %"+ 11 + "s %"+15+"s"   ,
					 			 	tr.getCollectionCode(),
					 			 	tr.getClientCode().length () >10 ? tr.getClientCode().substring ( 0 , 10 ) : tr.getClientCode (),	
					 			 	clientName.length () >20 ? clientName.substring ( 0 , 20 ) : clientName	 ,
								    moneyFormat.format( tr.getTotalAmount()), 
								    clientAddress.length () >10 ? clientAddress.substring ( 0 , 10 ) : clientAddress
								    		
									     )  + NEWLINE );
					 	totalcred = totalcred + tr.getTotalAmount();
					 	//tcash +=tr.getTotalAmount();
					if(count % 36 == 0 && count != 0    )  
					{  
							body.append ( NEWLINE );
						   	body.append ("                      Continued   "+ NEWLINE);
							body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					 	   	subHeader = new StringBuilder ();
					 	    pageNumber = pageNumber +1;
					 	    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
						 	subHeader.append ("            COLLECTION SUMMARY       "+ NEWLINE);
							subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
							subHeader.append ( HORIZANTAL_LINE + NEWLINE );
					    	subHeader.append ( COLLECTION_SUMMARY_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
					    	
					    	body.append(subHeader);
								 
					  }
					//	count = count+1;
					
				} // End for loop
	        		   body.append ( HORIZANTAL_LINE + NEWLINE );
	        		   body.append ( "        Total Credit Collection  "+moneyFormat.format(totalcred)  +  NEWLINE);
	        		   count = count + 2;
	        		   }
	        		   
	        		   
	        		  if(collectionHeadersCheck.size()>0){
	        			   //body.append ( HORIZANTAL_LINE + NEWLINE );
	        			   body.append ( COLLECTION_SUMMARY_CHECK_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
					    	
	        			   body.append ("      Check   " +NEWLINE);
	        			   
	        			  // body.append ( HORIZANTAL_LINE + NEWLINE );
	        			   count=count+2;
	        		   for( int i = 0 ; i < collectionHeadersCheck.size () ; i ++ ) {
	        			   CollectionSummaryPrinting   tr = collectionHeadersCheck.get ( i );
	        			   String clientName="",clientAddress="";
		        			switch ( tr.getCollectionType() ) {
		        			case 1:
		        			case 3:
		        				ArrayList< Clients> cc=(ArrayList<Clients>) DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
					            .where ( ClientsDao.Properties.ParentCode.eq (  tr.getClientCode()  )  ).list();
		        				if(cc !=null && cc.size()>0){
		        					clientName=cc.get(0).getClientName();
		        					clientAddress=cc.get(0).getClientAddress();
		        				}	        		
		        				break;
		        			case 2:
		        		
		        				 clientName=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
						            .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  ), ClientsDao.Properties.CompanyCode.eq (  tr.getCompanyCode()) ).unique().getClientName();
		        				//  clientAddress=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
								//            .where ( ClientsDao.Properties.ParentCode.eq (  tr.getClientCode()  ), ClientsDao.Properties.CompanyCode.eq (  tr.getCompanyCode()) ).unique().getClientAddress();
		        		
		        				break;
		        			}
		        			
//					     String currencyName= DatabaseUtils.getInstance ( context ).getDaoSession ().getCurrenciesDao().queryBuilder ()
//							 .where ( CurrenciesDao.Properties.CurrencyCode.eq (  tr.getCurrencyCode()   ) ).unique().getCurrencyName() ;
 					  	  count=count+1;
					 	body.append (   String.format (  "%-"+10+"s %"+10+"s %"+18 +"s %"+ 11+"s %"+ 8 +"s %"+ 8+"s %"+ 8 +"s "   ,
								   tr.getCollectionCode(),
								      tr.getClientCode().length () >10 ? tr.getClientCode().substring ( 0 , 10 ) : tr.getClientCode (),	
								      clientName.length () >18 ? clientName.substring ( 0 ,18 ) : clientName	 ,
								      tr.getCollectionDate().toString().length () >11 ? tr.getCollectionDate().toString().substring ( 0 , 11 ) : tr.getCollectionDate (),
								      tr.getCheckCode().length () >8 ? tr.getCheckCode().substring ( 0 , 8 ) : tr.getCheckCode (),
								       tr.getBankName().length () >8 ? tr.getBankName().substring ( 0 , 8 ) : tr.getBankName (),
								        moneyFormat.format( tr.getCollectionAmount() ) 
								       	 
									     )  + NEWLINE );
						totalCheck = totalCheck + tr.getCollectionAmount();
					if(count % 36 == 0 && count != 0    )  
					{  
							body.append ( NEWLINE );
						   	body.append ("                      Continued   "+ NEWLINE);
							body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					 	   	subHeader = new StringBuilder ();
					 	    pageNumber =pageNumber +1;
					 	    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
						 	subHeader.append ("            COLLECTION SUMMARY       "+ NEWLINE);
							subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
							subHeader.append ( HORIZANTAL_LINE + NEWLINE );
					    	subHeader.append ( COLLECTION_SUMMARY_CHECK_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
					    	
					    	body.append(subHeader);
								 
					  }

				         	
					
				} // End for loop
	        		   body.append ( HORIZANTAL_LINE + NEWLINE );
	        		   body.append ( "          Total Check "+moneyFormat.format(totalCheck)  +  NEWLINE);
	        		   count=count+2;
	           }// End if
	        		  
	        		   
	        		   
	        		   
	        		   
	        		   
	        		   
	        		   
	        		   
	        		   if(summaryReturnPrintingCash.size() > 0){
	        			   //body.append ( HORIZANTAL_LINE + NEWLINE );
	        			   body.append (   NEWLINE  +  NEWLINE  +Return_SUMMARY_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
					    	
	        			   body.append ("      Return Cash   " +NEWLINE);
	        			   
	        			  // body.append ( HORIZANTAL_LINE + NEWLINE );
	        			   count=count+4;
	        		   for( int i = 0 ; i < summaryReturnPrintingCash.size () ; i ++ ) {
	        			  SummaryReturnPrinting	    tr = summaryReturnPrintingCash.get ( i );
	         			
//					     String currencyName= DatabaseUtils.getInstance ( context ).getDaoSession ().getCurrenciesDao().queryBuilder ()
//							 .where ( CurrenciesDao.Properties.CurrencyCode.eq (  tr.getCurrencyCode()   ) ).unique().getCurrencyName() ;
//					  	 
	        			   
		        			String clientAddress=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
							             .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  )  ).unique().getClientAddress();
		        			 count=count+1;
	        			  body.append ( String.format ( "%-"+15+"s %-"+10+"s %"+20 +"s %"+ 11 + "s %"+15+"s"   ,
						 			 	tr.getTransactionCode(),
						 			 	tr.getClientCode().length () >10 ? tr.getClientCode().substring ( 0 , 10 ) : tr.getClientCode (),	
						 			 	tr.getClientName().length () >20 ?tr.getClientName().substring ( 0 , 20 ) : tr.getClientName()	 ,
									    moneyFormat.format( tr.getTotalTaxAmount()), 
									    clientAddress.length () >10 ? clientAddress.substring ( 0 , 10 ) : clientAddress	 
										     )  + NEWLINE );
								       	 
									    
	        			 	totalReturnCash = totalReturnCash + tr.getTotalTaxAmount();
					if(count % 36 == 0 && count != 0    )  
					{  
							body.append ( NEWLINE );
						   	body.append ("                      Continued   "+ NEWLINE);
							body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					 	   	subHeader = new StringBuilder ();
					 	    pageNumber =pageNumber +1;
					 	    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
						 	subHeader.append ("            COLLECTION SUMMARY       "+ NEWLINE);
							subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
							subHeader.append ( HORIZANTAL_LINE + NEWLINE );
					    	subHeader.append ( Return_SUMMARY_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
					    	
					    	body.append(subHeader);
								 
					  }
                	// count=count+1;
					
				} // End for loop
	        
	        		   body.append ( HORIZANTAL_LINE + NEWLINE );
	        		   body.append ( "          Total Return Cash "+moneyFormat.format( totalReturnCash )  +  NEWLINE);
	        		   count=count+2;
	        		   }// End if  
	        		   
	        		   
	        		   
	        		   if (! PermissionsUtils.getRemoveCreditCollectionPrint ( context ,   DatabaseUtils.getCurrentUserCode ( context ) ,  DatabaseUtils.getCurrentCompanyCode(context) ) ) 
		       				 
			        		  
	        		   if(summaryReturnPrintingCredit.size() > 0){
	        			   //body.append ( HORIZANTAL_LINE + NEWLINE );
	        			   body.append (  NEWLINE  + Return_SUMMARY_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
					    	
	        			   body.append ("      Return Credit   " +NEWLINE);
	        			   
	        			  // body.append ( HORIZANTAL_LINE + NEWLINE );
	        			   count=count+4;
	        		   for( int i = 0 ; i < summaryReturnPrintingCredit.size () ; i ++ ) {
	        			  SummaryReturnPrinting	    tr = summaryReturnPrintingCredit.get ( i );
	        			   
	        			String clientAddress=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
						             .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  )  ).unique().getClientAddress();
	        		
		        			
//					     String currencyName= DatabaseUtils.getInstance ( context ).getDaoSession ().getCurrenciesDao().queryBuilder ()
//							 .where ( CurrenciesDao.Properties.CurrencyCode.eq (  tr.getCurrencyCode()   ) ).unique().getCurrencyName() ;
	        			 count=count+1;
	        			 	 body.append ( String.format ( "%-"+15+"s %-"+10+"s %"+20 +"s %"+ 11 + "s %"+15+"s"   ,
						 			 	tr.getTransactionCode(),
						 			 	tr.getClientCode().length () >10 ? tr.getClientCode().substring ( 0 , 10 ) : tr.getClientCode (),	
						 			 	tr.getClientName().length () >20 ?tr.getClientName().substring ( 0 , 20 ) : tr.getClientName()	 ,
									    moneyFormat.format( tr.getTotalTaxAmount()), 
									    clientAddress.length () >10 ?clientAddress.substring ( 0 , 10 ) : clientAddress	 
										     )  + NEWLINE );
								       	 
									    
	        			 	totalReturnCredit = totalReturnCredit + tr.getTotalTaxAmount();
					if(count % 36 == 0 && count != 0    )  
					{  
							body.append ( NEWLINE );
						   	body.append ("                      Continued   "+ NEWLINE);
							body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					 	   	subHeader = new StringBuilder ();
					 	    pageNumber =pageNumber +1;
					 	    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
						 	subHeader.append ("            COLLECTION SUMMARY       "+ NEWLINE);
							subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
							subHeader.append ( HORIZANTAL_LINE + NEWLINE );
					    	subHeader.append ( Return_SUMMARY_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
					    	
					    	body.append(subHeader);
								 
					  }

				         //	 count=count+1;
					
				} // End for loop
	           
	        		   body.append ( HORIZANTAL_LINE + NEWLINE );
	        		   body.append ( "          Total Return Credit "+moneyFormat.format( totalReturnCredit )  +  NEWLINE);
	        		   count=count+3;
	        		   }// End if  
	        		// body.append(" Total Collect:" +moneyFormat.format(totalCash +totalCheck));
	        	    // body.append ( "Net Cash Collection:"+ moneyFormat.format((tcredit+totalCash +totalCheck-totalReturnCash))+ NEWLINE );
	        		
	        		  // body.append ( "Net Collection Amount:"+ moneyFormat.format((tcredit+totalCash +totalCheck + totalcred -totalReturnCash -totalReturnCredit))+ NEWLINE );
	        		   
	        		   
	        		 //  body.append ( "Net Collection Amount:"+ moneyFormat.format((totalCash +totalCheck  -totalReturnCash ))+ NEWLINE );
	        		// body.append(" Total Collect:" +moneyFormat.format(totalCash +totalCheck));
		        	    // body.append ( "Net Cash Collection:"+ moneyFormat.format((tcredit+totalCash +totalCheck-totalReturnCash))+ NEWLINE );
		        		
		        		 //  body.append ( "Net Collection Amount:"+ moneyFormat.format((tcredit+totalCash +totalCheck + totalcred -totalReturnCash -totalReturnCredit))+ NEWLINE );
		        		   if ( PermissionsUtils.getRemoveCreditCollectionPrint ( context ,   DatabaseUtils.getCurrentUserCode ( context ) ,  DatabaseUtils.getCurrentCompanyCode(context) ) ) {
		       				 
		        		 
		        		   body.append ( "Net Collection Amount:"+ moneyFormat.format((tcredit+totalCash +totalCheck + totalcred -totalReturnCash ))+ NEWLINE );
		        			  
		        		   if(summaryReturnPrintingCredit.size() > 0){
		        			   //body.append ( HORIZANTAL_LINE + NEWLINE );
		        			   body.append (  NEWLINE  + Return_SUMMARY_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
						    	
		        			   body.append ("      Return Credit   " +NEWLINE);
		        			   
		        			  // body.append ( HORIZANTAL_LINE + NEWLINE );
		        			   count=count+4;
		        		   for( int i = 0 ; i < summaryReturnPrintingCredit.size () ; i ++ ) {
		        			  SummaryReturnPrinting	    tr = summaryReturnPrintingCredit.get ( i );
		        			   
		        			String clientAddress=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
							             .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  )  ).unique().getClientAddress();
		        		
			        			
//						     String currencyName= DatabaseUtils.getInstance ( context ).getDaoSession ().getCurrenciesDao().queryBuilder ()
//								 .where ( CurrenciesDao.Properties.CurrencyCode.eq (  tr.getCurrencyCode()   ) ).unique().getCurrencyName() ;
		        			 count=count+1;
		        			 	 body.append ( String.format ( "%-"+15+"s %-"+10+"s %"+20 +"s %"+ 11 + "s %"+15+"s"   ,
							 			 	tr.getTransactionCode(),
							 			 	tr.getClientCode().length () >10 ? tr.getClientCode().substring ( 0 , 10 ) : tr.getClientCode (),	
							 			 	tr.getClientName().length () >20 ?tr.getClientName().substring ( 0 , 20 ) : tr.getClientName()	 ,
										    moneyFormat.format( tr.getTotalTaxAmount()), 
										    clientAddress.length () >10 ?clientAddress.substring ( 0 , 10 ) : clientAddress	 
											     )  + NEWLINE );
									       	 
										    
		        			 	totalReturnCredit = totalReturnCredit + tr.getTotalTaxAmount();
						if(count % 36 == 0 && count != 0    )  
						{  
								body.append ( NEWLINE );
							   	body.append ("                      Continued   "+ NEWLINE);
								body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						 	   	subHeader = new StringBuilder ();
						 	    pageNumber =pageNumber +1;
						 	    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
							 	subHeader.append ("            COLLECTION SUMMARY       "+ NEWLINE);
								subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
								subHeader.append ( HORIZANTAL_LINE + NEWLINE );
						    	subHeader.append ( Return_SUMMARY_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
						    	
						    	body.append(subHeader);
									 
						  }

					         //	 count=count+1;
						
					} // End for loop
		           
		        		   body.append ( HORIZANTAL_LINE + NEWLINE );
		        		   body.append ( "          Total Return Credit "+moneyFormat.format( totalReturnCredit )  +  NEWLINE);
		        		   count=count+3;
		        		   }// End if  
		        		   
		        		   }
		       				else
		       				{ 
		       					body.append ( "Net Collection Amount:"+ moneyFormat.format((tcredit+totalCash +totalCheck + totalcred -totalReturnCash -totalReturnCredit))+ NEWLINE );
		  	        		
		       					
		       				}  
	 		} // End if
			// Build the footer
			
			if ( type == Type.COLLECTIONSUMMARY   ) {
		        int lastPageM = collectionSummaryHeaderss.size() / 34;
				int restM = collectionSummaryHeaderss.size() % 34;
				footer = new StringBuilder ();
				 
				 if( pageNumber == ( lastPageM + 1 )    )
			    {
					 if( pageNumber == 1 )
		  	  				if(( 34 - collectionSummaryHeaderss.size() - 3 )>0)
		  				{	    for(int u = 0 ; u< 34 - collectionSummaryHeaderss.size()  - 3 ; u++)
		  					footer.append ( NEWLINE );
		  				}
		  	  	else{
				 if( restM > 4 )
	    	     for( int u=0 ; u< 34-restM-3 ;u++ )
	  	        	footer.append (  NEWLINE  );
			      }
				}
				//footer.append ( "SALES REP                       WAREHOUSE IN CHARGE                   SECURITY" );
	       	    footer.append ( "                          End Of Collection Summary");
	     	 } // End if 		
				
			
			if(type == Type.MovementInSummary){
				
				
				Integer  firstLoadQuantityM=0;
				Integer	firstLoadQuantityS=0;
				 
				
				int 	totalLoadOutM = 0,
						totalLoadOutS = 0,
						totalAddOnM = 0, 
						totalAddOnS = 0,
						totalOffLoadM = 0,
						totalOffLoadS = 0,
						totalReturnM = 0,
						totalReturnS = 0, 
						totalGSalesM = 0,
						totalGSalesS = 0,
						totalStockM = 0,
						totalStockS = 0;
						for( int i = 0 ; i < loadInSummaryPrinting.size () ; i ++ ) {
						  LoadInSummaryPrinting   tr = loadInSummaryPrinting.get ( i );
						  if(tr.getItemCode()!=null)
							body.append (String.format ( "%-"+8+"s %-"+16+"s %"+ 8 +"s %"+ 6 +"s %"+ 7 + "s %" + 7 + "s %" + 7+ "s %"  + 6 + "s %" + 3 + "s " , 
									    tr.getItemCode ().length () > 8 ? tr.getItemCode ().substring ( 0 , 8 ) : tr.getItemCode ()	,
									    tr.getItemName ().length () > 16 ? tr.getItemName ().substring ( 0 , 16 ) : tr.getItemName (), 
									    tr.getBaseUnit().intValue(),
									    tr.getFirstLoadQuantityMedium().intValue() + "/" + tr.getFirstLoadQuantitySmall().intValue(),
									    tr.getQuantityMedium().intValue() + "/" + tr.getQuantitySmall().intValue(),
									
									    tr.getOffLoadQuantityMedium().intValue() + "/" + tr.getOffLoadQuantitySmall().intValue(),
										tr.getQuantityMediumReturn().intValue() + "/" + tr.getQuantitySmallReturn().intValue() ,
										tr.getQuantityMediumSales().intValue() + "/" + tr.getQuantitySmallSales().intValue(),
										tr.getStockQuantityMedium().intValue() + "/" + tr.getStockQuantitySmall().intValue() )  + NEWLINE );
						  
						  firstLoadQuantityM=firstLoadQuantityM+ tr.getFirstLoadQuantityMedium().intValue();
						  firstLoadQuantityS=firstLoadQuantityS+tr.getFirstLoadQuantitySmall().intValue();
						  
						  totalLoadOutM += tr.getFirstLoadQuantityMedium();
						  totalLoadOutS += tr.getFirstLoadQuantitySmall();
						  totalAddOnM += tr.getQuantityMedium();
						  totalAddOnS += tr.getQuantitySmall();
						  totalOffLoadM += tr.getOffLoadQuantityMedium();
						  totalOffLoadS += tr.getOffLoadQuantitySmall();
						  totalReturnM += tr.getQuantityMediumReturn();
						  totalReturnS += tr.getQuantitySmallReturn(); 
						  totalGSalesM += tr.getQuantityMediumSales();
						  totalGSalesS += tr.getQuantitySmallSales();
						  totalStockM += tr.getStockQuantityMedium();
						  totalStockS += tr.getStockQuantitySmall();
				
						  if(count % 38 == 0 && count != 0    )  
						{  
								body.append ( NEWLINE );
						   		body.append ( NEWLINE );
								body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						   		body.append ( NEWLINE );
								body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
//						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE ); 
						 	   	subHeader = new StringBuilder ();
						 	    pageNumber =pageNumber +1;
						 	    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
							  	subHeader.append ("           MOVEMENT IN SUMMARY      "+ NEWLINE);
								subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
								subHeader.append ( HORIZANTAL_LINE + NEWLINE );
								subHeader.append ( LOAD_HEADER_In_Summary_Movement  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
								
						    	body.append(subHeader);
									 
						  }
						
						     	 count=count+1;
						
						} // End for loop
						body.append(HORIZANTAL_LINE);
						 body.append (String.format ( "%-"+8+"s %-"+16+"s %"+ 8 +"s %"+ 6 +"s %"+ 7 + "s %" + 7 + "s %" + 7+ "s %"  + 6 + "s %" + 3 + "s " , 
								    " "	,
								    " ", 
								    " TOTAL ",
								    totalLoadOutM + "/" + totalLoadOutS,
								    totalAddOnM + "/" + totalAddOnS,
								    totalOffLoadM + "/" + totalOffLoadS,
								    totalReturnM + "/" + totalReturnS ,
								    totalGSalesM + "/" + totalGSalesS,
								    totalStockM + "/" + totalStockS )  + NEWLINE );
						} // End if
						// Build the footer
						
						if ( type == Type.MovementInSummary   ) {
						int lastPageM=loadInSummaryPrinting.size() / 38;
						int restM = loadInSummaryPrinting.size() % 38;
						footer = new StringBuilder ();
						
						if(pageNumber == ( lastPageM + 1 )    )
						{ if(pageNumber == 1)
								if(( 38 - loadInSummaryPrinting.size() - 3 )>0)
						{	    for(int u=0 ; u< 38-loadInSummaryPrinting.size()  - 3 ;u++)
							footer.append ( NEWLINE );
						}
								else{
						if(restM>4)
						for(int u=0 ; u< 38-restM-3 ;u++)
							footer.append ( NEWLINE );
						}}
						//footer.append ( "SALES REP                       WAREHOUSE IN CHARGE                   SECURITY" );
						footer.append ( "                              END OF MOVEMENT SUMMARY");
						} // End if 		

			if(type == Type.loadInSummary){
									Integer  firstLoadQuantityM=0;
									Integer	firstLoadQuantityS=0;
									Integer	quantityMe=0;
									Integer	quantitySm=0;
									Integer	offLoadQuantityMedium=0;
									Integer	offLoadQuantitySmall=0;
									Integer	quantityMediumReturn=0;
									Integer	quantitySmallReturn=0;
									Integer	quantityMediumSales=0;
									Integer	quantitySmallSales=0;
									Integer  stockQuantityMedium=0;
									Integer  stockQuantitySmall=0;
									
									int 	totalLoadOutM = 0,
											totalLoadOutS = 0,
											totalAddOnM = 0, 
											totalAddOnS = 0,
											totalOffLoadM = 0,
											totalOffLoadS = 0,
											totalReturnM = 0,
											totalReturnS = 0, 
											totalGSalesM = 0,
											totalGSalesS = 0,
											totalStockM = 0,
											totalStockS = 0;
									
				  for( int i = 0 ; i < loadInSummaryPrinting.size () ; i ++ ) {
					  LoadInSummaryPrinting   tr = loadInSummaryPrinting.get ( i );
					  if(tr.getItemCode()!=null)
						body.append (String.format ( "%-"+8+"s %-"+16+"s %"+ 8 +"s %"+ 6 +"s %"+ 7 + "s %" + 7 + "s %" + 7+ "s %"  + 6 + "s %" + 3 + "s " , 
  							    tr.getItemCode ().length () > 8 ? tr.getItemCode ().substring ( 0 , 8 ) : tr.getItemCode ()	,
  							    tr.getItemName ().length () > 16 ? tr.getItemName ().substring ( 0 , 16 ) : tr.getItemName (), 
  							    tr.getBaseUnit().intValue(),
  							    tr.getFirstLoadQuantityMedium().intValue() + "/" + tr.getFirstLoadQuantitySmall().intValue(),
  							    tr.getQuantityMedium().intValue() + "/" + tr.getQuantitySmall().intValue(),
  							
  							    tr.getOffLoadQuantityMedium().intValue() + "/" + tr.getOffLoadQuantitySmall().intValue(),
  								tr.getQuantityMediumReturn().intValue() + "/" + tr.getQuantitySmallReturn().intValue() ,
  								tr.getQuantityMediumSales().intValue() + "/" + tr.getQuantitySmallSales().intValue(),
  								tr.getStockQuantityMedium().intValue() + "/" + tr.getStockQuantitySmall().intValue() )  + NEWLINE );
					  
					  firstLoadQuantityM=firstLoadQuantityM+ tr.getFirstLoadQuantityMedium().intValue();
					  firstLoadQuantityS=firstLoadQuantityS+tr.getFirstLoadQuantitySmall().intValue();
					  
					  totalLoadOutM += tr.getFirstLoadQuantityMedium();
					  totalLoadOutS += tr.getFirstLoadQuantitySmall();
					  totalAddOnM += tr.getQuantityMedium();
					  totalAddOnS += tr.getQuantitySmall();
					  totalOffLoadM += tr.getOffLoadQuantityMedium();
					  totalOffLoadS += tr.getOffLoadQuantitySmall();
					  totalReturnM += tr.getQuantityMediumReturn();
					  totalReturnS += tr.getQuantitySmallReturn(); 
					  totalGSalesM += tr.getQuantityMediumSales();
					  totalGSalesS += tr.getQuantitySmallSales();
					  totalStockM += tr.getStockQuantityMedium();
					  totalStockS += tr.getStockQuantitySmall();
					  
//				 	quantityMe=0;
//				 	quantitySm=0;
//						 offLoadQuantityMedium=0;
//					 	offLoadQuantitySmall=0;
//						 quantityMediumReturn=0;
//						 	quantitySmallReturn=0;
//						 	quantityMediumSales=0;
//						 	quantitySmallSales=0;
//						   stockQuantityMedium=0;
//						   stockQuantitySmall=0;
					  if(count % 38 == 0 && count != 0    )  
					{  
							body.append ( NEWLINE );
						   	body.append ("                      Continued   "+ NEWLINE);
							body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					   		body.append ( NEWLINE );
//							body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE ); 
					 	   	subHeader = new StringBuilder ();
					 	    pageNumber =pageNumber +1;
					 	   subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
						  	subHeader.append ("            LOAD IN SUMMARY       "+ NEWLINE);
							subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
							subHeader.append ( HORIZANTAL_LINE + NEWLINE );
							subHeader.append ( LOAD_HEADER_In_Summary  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
							
					    	body.append(subHeader);
								 
					  }

				         	 count=count+1;
					
				} // End for loop
				  
				  body.append(HORIZANTAL_LINE);
				  body.append (String.format ( "%-"+8+"s %-"+16+"s %"+ 8 +"s %"+ 6 +"s %"+ 7 + "s %" + 7 + "s %" + 7+ "s %"  + 6 + "s %" + 3 + "s " , 
						    " "	,
						    " ", 
						    " TOTAL",
						    totalLoadOutM + "/" + totalLoadOutS,
						    totalAddOnM + "/" + totalAddOnS,
						    totalOffLoadM + "/" + totalOffLoadS,
						    totalReturnM + "/" + totalReturnS ,
						    totalGSalesM + "/" + totalGSalesS,
						    totalStockM + "/" + totalStockS )  + NEWLINE );
				  
			} // End if
			// Build the footer
			
			if ( type == Type.loadInSummary   ) {
		        int lastPageM=loadInSummaryPrinting.size() / 38;
				int restM = loadInSummaryPrinting.size() % 38;
				footer = new StringBuilder ();
				 
				if(pageNumber == ( lastPageM + 1 )    )
			    { if(pageNumber==1)
		  				if(( 38 - loadInSummaryPrinting.size() - 3 )>0)
					{	    for(int u=0 ; u< 38-loadInSummaryPrinting.size()  - 3 ;u++)
						footer.append ( NEWLINE );
					}
		  				else{
				 if(restM>4)
	   	     for(int u=0 ; u< 38-restM-3 ;u++)
	 	        	footer.append ( NEWLINE );
			      }}
				//footer.append ( "SALES REP                       WAREHOUSE IN CHARGE                   SECURITY" );
	      	    footer.append ( "                                END OF LOAD IN SUMMARY");
	    	 } // End if 		
			
			
			 
			if(type == Type.FreeSummeryReport){
				double totalAmount=0;
				  for( int i = 0 ; i < loadInSummaryPrinting.size () ; i ++ ) {
					  LoadInSummaryPrinting   tr = loadInSummaryPrinting.get ( i );
					
					  body.append (   String.format ( "%-"+8+"s %"+8+"s %"+12+"s %"+8+"s %"+8 +"s %"+ 5 + "s %"+6 + "s %"+ 5 + "s %"+5+"s"   ,
							          tr.getTransactionCode(),
							          tr.getItemCode().length () >8 ? tr.getItemCode().substring ( 0 , 8 ) : tr.getItemCode (),	
							          tr.getItemName().length () >12 ? tr.getItemName().substring ( 0 , 12 ) : tr.getItemName(),
							          tr.getClientCode().length () >8 ? tr.getClientCode().substring ( 0 , 8 ) : tr.getClientCode (),	
						              tr.getClientName().length () >12 ? tr.getClientName().substring ( 0 , 12 ) : tr.getClientName()	,		
						              tr.getQuantityMediumReturn()  ,tr.getQuantitySmallReturn(),   moneyFormat.format ( tr.getQuantitySmall()	) , 
						              moneyFormat.format ( tr.getQuantityMedium()) )  + NEWLINE );
					  
				 totalAmount+=tr.getQuantityMedium();
					  
					if(count % 38 == 0 && count != 0    )  
					{  
							body.append ( NEWLINE );
						   	body.append ("                      Continued   "+ NEWLINE);
							body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE ); 
					 	   	subHeader = new StringBuilder ();
					 	    pageNumber =pageNumber +1;
					 	   subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
						  	subHeader.append ("            FOC summary report       "+ NEWLINE);
							subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
							subHeader.append ( HORIZANTAL_LINE + NEWLINE );
							subHeader.append ( ITEM_FREE_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
							 
					    	body.append(subHeader);
								 
					  }

				         	 count=count+1;
					
				} // End for loop
				  body.append("                       Total Amount :"+   moneyFormat.format (totalAmount));
			} // End if
			// Build the footer
			
			if ( type == Type.FreeSummeryReport   ) {
		        int lastPageM=loadInSummaryPrinting.size() / 38;
				int restM = loadInSummaryPrinting.size() % 38;
				footer = new StringBuilder ();
				 
				if(pageNumber == ( lastPageM + 1 )    )
			    { if(pageNumber==1)
		  				if(( 38 - loadInSummaryPrinting.size() - 3 )>0)
					{	    for(int u=0 ; u< 38-loadInSummaryPrinting.size()  - 3 ;u++)
						footer.append ( NEWLINE );
					}
		  				else{
				 if(restM>4)
	   	     for(int u=0 ; u< 38-restM-3 ;u++)
	 	        	footer.append ( NEWLINE );
			      }}
				//footer.append ( "SALES REP                       WAREHOUSE IN CHARGE                   SECURITY" );
	      	    footer.append ( "                                LAST PAGE");
	    	 } // End if 	
			if(type == Type.SalesReturnSummeryReport){
				double totalAmount=0;
				  for( int i = 0 ; i < loadInSummaryPrinting.size () ; i ++ ) {
					  LoadInSummaryPrinting   tr = loadInSummaryPrinting.get ( i );
					
					  body.append (   String.format ( "%-"+10+"s %"+20+"s %"+10+"s %"+18 +"s %"+ 6 + "s %"+6+"s"   ,
							    tr.getItemCode().length () >10 ? tr.getItemCode().substring ( 0 , 10 ) : tr.getItemCode (),	
							    tr.getItemName().length () >20 ? tr.getItemName().substring ( 0 , 20 ) : tr.getItemName(),
							    tr.getClientCode().length () >10 ? tr.getClientCode().substring ( 0 , 10 ) : tr.getClientCode (),	
						        tr.getClientName().length () >20 ? tr.getClientName().substring ( 0 , 20 ) : tr.getClientName()	,		
						        tr.getQuantityMediumReturn()  ,tr.getQuantitySmallReturn()	  )  + NEWLINE );
					  
					  
					  totalAmount=totalAmount+tr.getTotalAmount();
					if(count % 38 == 0 && count != 0    )  
					{  
							body.append ( NEWLINE );
						   	body.append ("                      Continued   "+ NEWLINE);
							body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					   		body.append ( NEWLINE );
							body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE ); 
					 	   	subHeader = new StringBuilder ();
					 	    pageNumber =pageNumber +1;
					 	   subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
						  	subHeader.append ("            Sales Return Summary Report       "+ NEWLINE);
							subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
							subHeader.append ( HORIZANTAL_LINE + NEWLINE );
							subHeader.append ( ITEM_Return_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
							 
					    	body.append(subHeader);
								 
					  }

				         	 count=count+1;
					
				} // End for loop
				  body.append("                         Total Amount :"+  moneyFormat.format (totalAmount) + NEWLINE);
			} // End if
			// Build the footer
			
			if ( type == Type.SalesReturnSummeryReport   ) {
		        int lastPageM=loadInSummaryPrinting.size() / 38;
				int restM = loadInSummaryPrinting.size() % 38;
				footer = new StringBuilder ();
				 
				if(pageNumber == ( lastPageM + 1 )    )
			    { if(pageNumber==1)
		  				if(( 38 - loadInSummaryPrinting.size() - 3 )>0)
					{	    for(int u=0 ; u< 38-loadInSummaryPrinting.size()  - 3 ;u++)
						footer.append ( NEWLINE );
					}
		  				else{
				 if(restM>4)
	   	     for(int u=0 ; u< 38-restM-3 ;u++)
	 	        	footer.append ( NEWLINE );
			      }}
				//footer.append ( "SALES REP                       WAREHOUSE IN CHARGE                   SECURITY" );
	      	    footer.append ( "                                LAST PAGE");
	    	 } // End if 		
			
			
			
			 
			
			//COLLECTION SETTEL	
       	 if ( type == Type.COLLECTIONISSETTEL   ) {
			     for( int i = 0 ; i < collectionHeaderss.size () ; i ++ ) {
			         CollectionHeaders   tr = collectionHeaderss.get ( i );
			         String clientName="";
	        			switch ( tr.getCollectionType() ) {
	        			case 1:
	        			case 3:
	        				  clientName=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
					            .where ( ClientsDao.Properties.ParentCode.eq (  tr.getClientCode()  ), ClientsDao.Properties.CompanyCode.eq (  tr.getCompanyCode()) ).unique().getClientName();
					   
	        				break;
	        			case 2:
	        	
	        				 clientName=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
					            .where ( ClientsDao.Properties.ClientName.eq (  tr.getClientCode()  ), ClientsDao.Properties.CompanyCode.eq (  tr.getCompanyCode()) ).unique().getClientName();
					   
	        				break;
	        			}
	        				     String currencyName= DatabaseUtils.getInstance ( context ).getDaoSession ().getCurrenciesDao().queryBuilder ()
						 .where ( CurrenciesDao.Properties.CurrencyCode.eq (  tr.getCurrencyCode()   ) ).unique().getCurrencyName() ;
				  	 
					 	body.append (   String.format ( "%-"+15+"s %-"+10+"s %"+20 +"s %"+ 11 + "s %"+15+"s"   ,
								   tr.getCollectionCode(),
								      tr.getClientCode().length () >10 ? tr.getClientCode().substring ( 0 , 10 ) : tr.getClientCode (),	
								        clientName.length () >20 ? clientName.substring ( 0 , 20 ) : clientName	 ,
								        		moneyFormat.format( tr.getTotalAmount()), 
								        currencyName.length () >10 ? currencyName.substring ( 0 , 10 ) : currencyName	 
									     )  + NEWLINE );
				 
				if(count % 38 == 0 && count != 0    )  
				{  
						body.append ( NEWLINE );
					   	body.append ("                      Continued   "+ NEWLINE);
						body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				 	   	subHeader = new StringBuilder ();
				 	    pageNumber =pageNumber +1;
				 	    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
					 	subHeader.append ("            COLLECTION SETTEL       "+ NEWLINE);
						subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
						subHeader.append ( HORIZANTAL_LINE + NEWLINE );
				    	subHeader.append ( COLLECTION_SUMMARY_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
				    	
				    	body.append(subHeader);
							 
				  }

			         	 count=count+1;
				
			} // End for loop
		} // End if
		// Build the footer
		
		if ( type == Type.COLLECTIONISSETTEL   ) {
	        int lastPageM=collectionHeaderss.size() / 38;
			int restM = collectionHeaderss.size() % 38;
			footer = new StringBuilder ();
			 
			if(pageNumber == ( lastPageM + 1 )    )
		    { if(pageNumber==1)
	  				if(( 38 - collectionHeaderss.size() - 3 )>0)
				{	    for(int u=0 ; u< 38-collectionHeaderss.size()  - 3 ;u++)
					footer.append ( NEWLINE );
				}
	  				else{
			 if(restM>4)
   	     for(int u=0 ; u< 38-restM-3 ;u++)
 	        	footer.append ( NEWLINE );
		      }}
			//footer.append ( "SALES REP                       WAREHOUSE IN CHARGE                   SECURITY" );
      	    footer.append ( "                                LAST PAGE");
    	 } // End if 		
		
		
		
			  //INVOICE SUMMARY	
		if ( type == Type.INVOICESUMMARY   ) {
			 
			  ArrayList< TransactionHeaders >  transactionAvailable = new ArrayList<TransactionHeaders>() ;
			  ArrayList< TransactionHeaders >  transactionVoid = new ArrayList<TransactionHeaders>() ;
			  
			  ArrayList< TransactionHeaders >  transactionCredit = new ArrayList<TransactionHeaders>() ;
		      ArrayList< TransactionHeaders >  transactionCash = new ArrayList<TransactionHeaders>() ;
		      ArrayList< TransactionHeaders >  transactionCashCash = new ArrayList<TransactionHeaders>() ;
		      ArrayList< TransactionHeaders >  transactionCashCredit = new ArrayList<TransactionHeaders>() ;
		      ArrayList < TransactionHeaders >  transactionCreditUncollected = new ArrayList< TransactionHeaders >() ;
		      ArrayList < TransactionHeaders >  transactionCredits = new ArrayList < TransactionHeaders >() ;
			  double totalCashInvoice=0d;
		      double totalCashCreditInvoice= 0d;
		      double totalCreditInvoice= 0d;
		      double totalCreditInvoiceUnco= 0d;
		      for( int i = 0 ; i < transactionHeaderss.size () ; i ++ ) {
			    	 TransactionHeaders    tr = transactionHeaderss.get ( i );
		      if(tr.getInfo4()!=null || tr.getInfo5() !=null )
		    	  transactionHeaderss.remove(tr);
		      }
			  for( int i = 0 ; i < transactionHeaderss.size () ; i ++ ) {
			    	 TransactionHeaders    tr = transactionHeaderss.get ( i );
	 	     			if(tr.getTransactionStatus() == 1 )
	 	     				transactionAvailable.add( tr );
	 	     			else if(tr.getTransactionStatus() == 2)
	 	     				transactionVoid.add( tr );
	 	     			 }
		     
			  
			  for( int i = 0 ; i < transactionAvailable.size () ; i ++ ) {
			    	 TransactionHeaders    tr = transactionAvailable.get ( i );
			    	 	if( tr.getRemainingAmount() > 0 )
	 	     				transactionCredit.add( tr );
	 	     			else
	 	     				transactionCash.add( tr );
	 	     				
	 	     		}
			  
			  for( int i = 0 ; i < transactionCash.size () ; i ++ ) {
			    	 TransactionHeaders    tr = transactionCash.get ( i );
//			    	 List < CollectionInvoices > collectionInvoices = new ArrayList < CollectionInvoices > ();
//			    	 collectionInvoices = DatabaseUtils.getInstance ( context ).getDaoSession ().getCollectionInvoicesDao().queryBuilder ()
//					            .where ( CollectionInvoicesDao.Properties.InvoiceCode.eq (  tr.getTransactionCode()) ).list();
     			
	 	     			//if( tr.getInfo1().equals("1") || tr.getInfo2().equals("1") ||  collectionInvoices.size()>0)
	 	     				//transactionCashCredit.add(tr);
			    	  if( tr.getInfo1().equals( "1" ) || tr.getInfo2().equals( "1" ) )
	 	     			  transactionCashCash.add( tr );
	 	     			else
	 	     			{
	 	     				Clients c =	  DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
	 	     					     .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  ) ).unique();
	 	     				if( c.getClientType() == 1 )
	 	     					transactionCashCash.add(tr);
	 	     				//	transactionCashCredit.add(tr);
	 	     				else
	 	     					transactionCashCredit.add(tr);
	 	     			}
	 	     				
	 	     		}
			  
		      if( transactionCashCash.size () > 0 ){
		    	 
		    	  body.append (" CASH INVOICE "+ NEWLINE);
		    	  body.append ( INVOICE_SUMMARY_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
		     for( int i = 0 ; i < transactionCashCash.size () ; i ++ ) {
		    	 TransactionHeaders    tr = transactionCashCash.get ( i );
			   	 String clientName=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
			     .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  ) ).unique().getClientName();
			   	 String currencyName= DatabaseUtils.getInstance ( context ).getDaoSession ().getCurrenciesDao().queryBuilder ()
				 .where ( CurrenciesDao.Properties.CurrencyCode.eq (  tr.getCurrencyCode()   ) ).unique().getCurrencyName() ;
			   	 String clientaddress=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
					     .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  ) ).unique().getClientAddress();
					   	 
				 body.append ( String.format ( "%-"+13+"s %"+10+"s %"+22 +"s %"+ 12 + "s %"+15+"s "   ,
						 tr.getTransactionCode()  , tr.getClientCode().length () >10 ? tr.getClientCode().substring ( 0 , 10 ) : tr.getClientCode (),	
							       clientName.length () >22 ? clientName.substring ( 0 , 22 ) : clientName	 ,
							       moneyFormat.format( tr.getTotalTaxAmount()),
							       clientaddress.length () >10 ? clientaddress.substring ( 0 , 10 ) : clientaddress	 
								     )  + NEWLINE );
				 totalCashInvoice=totalCashInvoice+tr.getTotalTaxAmount();
					 
					if(count % 36 == 0 && count != 0    )  
					{  
						subHeader.append ("                      Continued   "+ NEWLINE);
							body.append ( NEWLINE );
					   		body.append ( NEWLINE );
							body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					 	   	subHeader = new StringBuilder ();
					 	    pageNumber =pageNumber +1;
					 	    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
						 	subHeader.append ("               SALES SUMMARY       "+ NEWLINE);
							subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
							subHeader.append ( HORIZANTAL_LINE + NEWLINE );
							subHeader.append (" CASH INVOICE "+ NEWLINE);
					    	subHeader.append (INVOICE_SUMMARY_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
					    	
					    	
					    	body.append(subHeader);
								 
					  }

				         	 count=count+1;
					
				} // End for loop
		     
		      }
//		 	if ( PermissionsUtils.getEnableCreditUncollectedPrint ( context ,   DatabaseUtils.getCurrentUserCode ( context ) ,  DatabaseUtils.getCurrentCompanyCode(context) ) ) {
//				 
//		 		  
//			      if( transactionCashCash.isEmpty() ){
//			    	 
//			    	  body.append (" CASH INVOICE "+ NEWLINE);
//			    	  body.append ( INVOICE_SUMMARY_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
//			      }			
//			      for( int i = 0 ; i < transactionCredit.size () ; i ++ ) {
//			    		 TransactionHeaders    tr = transactionCredit.get ( i );
//			    		 Clients c =	  DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
//		     					     .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  ) ).unique();
//		     				if(c.getClientType() == 1)
//		     					  transactionCreditUncollected.add(tr);
//		     				else {
//		     					if(tr.getInfo1().equals( "1" ) || tr.getInfo2().equals( "1" ) )
//		     				
//		     					transactionCreditUncollected.add(tr);
//		     					else
//		     					transactionCredits.add(tr);
//		     				}
//		     				}
//			      			transactionCredits.addAll( transactionCashCredit );
//			     // New
//			      if( transactionCreditUncollected.size () > 0){
//				    	//  totalCashInvoice=0d;
//				    	  count = count+5;
//				    //	  body.append (" UNCOLLECTED  INVOICE "+ NEWLINE);
//				    //	  body.append (INVOICE_SUMMARY_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
//						     for( int i = 0 ; i < transactionCreditUncollected.size () ; i ++ ) {
//						    	 TransactionHeaders    tr = transactionCreditUncollected.get ( i );
//							   	 String clientName=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
//							     .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  ) ).unique().getClientName();
//							   	 String currencyName= DatabaseUtils.getInstance ( context ).getDaoSession ().getCurrenciesDao().queryBuilder ()
//								 .where ( CurrenciesDao.Properties.CurrencyCode.eq (  tr.getCurrencyCode()   ) ).unique().getCurrencyName() ;
//							   	 String clientaddress=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
//									     .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  ) ).unique().getClientAddress();
//								 
//								 body.append ( String.format ( "%-"+13+"s %"+10+"s %"+22 +"s %"+ 12 + "s %"+15+"s "   ,
//										 tr.getTransactionCode()  , tr.getClientCode().length () >10 ? tr.getClientCode().substring ( 0 , 10 ) : tr.getClientCode (),	
//											        clientName.length () >22 ? clientName.substring ( 0 , 22 ) : clientName	 ,
//											       moneyFormat.format( tr.getTotalTaxAmount()),
//											       clientaddress.length () >10 ? clientaddress.substring ( 0 , 10 ) : clientaddress	 
//												     )  + NEWLINE );
//								// totalCashInvoice=totalCashInvoice+tr.getTotalTaxAmount();
//								 totalCreditInvoiceUnco = totalCreditInvoiceUnco +  tr.getTotalTaxAmount();
//									if( count % 36 == 0 && count != 0 )  
//									{  
//										subHeader.append ("                      Continued   "+ NEWLINE);
//											body.append ( NEWLINE );
//									   		body.append ( NEWLINE );
//											body.append ( NEWLINE );
//									    	body.append ( NEWLINE );
//									    	body.append ( NEWLINE );
//									    	body.append ( NEWLINE );
//									    	body.append ( NEWLINE );
//									    
//									    	body.append ( NEWLINE );
//									    	body.append ( NEWLINE );
//									    	body.append ( NEWLINE );
//									    	body.append ( NEWLINE );
//									    	body.append ( NEWLINE );
//									    	body.append ( NEWLINE );
//									    	body.append ( NEWLINE );
//									    	body.append ( NEWLINE );
//									    	body.append ( NEWLINE );
//									    	body.append ( NEWLINE );
//									 	   	subHeader = new StringBuilder ();
//									 	    pageNumber =pageNumber +1;
//									 	    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
//										 	subHeader.append ("               SALES SUMMARY       "+ NEWLINE);
//											subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
//											subHeader.append ( HORIZANTAL_LINE + NEWLINE );
//											subHeader.append (" UNCOLLECTED INVOICE "+ NEWLINE);
//									    	subHeader.append (INVOICE_SUMMARY_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
//									      	body.append(subHeader);
//												 
//									  }
//
//								         	 count=count+1;
//									
//								} // End for loop
//						//     body.append ( HORIZANTAL_LINE + NEWLINE ); 
//						 //    body.append ("              TOTAL UNCOLLECTED INVOICE:                "+moneyFormat.format(totalCreditInvoiceUnco)+    NEWLINE);
//						//     
//						     
//						     
//					 	} // End if
//			       	}
		 	  
		      if( transactionCashCash.size () > 0 || transactionCreditUncollected.size () > 0){
		    	 
		
		     body.append ( HORIZANTAL_LINE + NEWLINE ); 
		     body.append ("              TOTAL CASH INVOICE:                " + moneyFormat.format( totalCreditInvoiceUnco + totalCashCreditInvoice + totalCashInvoice)+    NEWLINE);
	
		      }		     //body.append ( HORIZANTAL_LINE + NEWLINE ); 
		     //body.append ("              TOTAL CASH INVOICE:                "+moneyFormat.format( totalCashInvoice )+    NEWLINE);
		     
	 	//	} // End if
//		      if( transactionCashCredit.size ()>0){
//			    	 
//		    	  body.append (" CASH CREDIT INVOICE "+ NEWLINE);
//		    	  body.append ( INVOICE_SUMMARY_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
//		     for( int i = 0 ; i < transactionCashCredit.size () ; i ++ ) {
//		    	 TransactionHeaders    tr = transactionCashCredit.get ( i );
//			   	 String clientName=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
//			     .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  ) ).unique().getClientName();
//			   	 String currencyName= DatabaseUtils.getInstance ( context ).getDaoSession ().getCurrenciesDao().queryBuilder ()
//				 .where ( CurrenciesDao.Properties.CurrencyCode.eq (  tr.getCurrencyCode()   ) ).unique().getCurrencyName() ;
//			   	 String clientaddress=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
//					     .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  ) ).unique().getClientAddress();
//					   	 
//				 body.append ( String.format ( "%-"+13+"s %"+10+"s %"+22 +"s %"+ 12 + "s %"+15+"s "   ,
//						 tr.getTransactionCode()  , tr.getClientCode().length () >10 ? tr.getClientCode().substring ( 0 , 10 ) : tr.getClientCode (),	
//							        clientName.length () >22 ? clientName.substring ( 0 , 22 ) : clientName	 ,
//							       moneyFormat.format( tr.getTotalTaxAmount()),
//							       clientaddress.length () >10 ? clientaddress.substring ( 0 , 10 ) : clientaddress	 
//								     )  + NEWLINE );
//				 totalCashCreditInvoice = totalCashCreditInvoice + tr.getTotalTaxAmount();
//					 
//					if(count % 36 == 0 && count != 0    )  
//					{  
//						subHeader.append ("                      Continued   "+ NEWLINE);
//							body.append ( NEWLINE );
//					   		body.append ( NEWLINE );
//							body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					 	   	subHeader = new StringBuilder ();
//					 	    pageNumber =pageNumber +1;
//					 	    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
//						 	subHeader.append ("               SALES SUMMARY       "+ NEWLINE);
//							subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
//							subHeader.append ( HORIZANTAL_LINE + NEWLINE );
//							subHeader.append (" CASH CREDIT INVOICE                                  "+ NEWLINE);
//					    	subHeader.append (INVOICE_SUMMARY_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
//					    	
//					    	
//					    	body.append(subHeader);
//								 
//					  }
//
//				         	 count=count+1;
//					
//				} // End for loop
//		     body.append ( HORIZANTAL_LINE + NEWLINE ); 
//		     body.append ("              TOTAL CASH CREDIT INVOICE:                "+moneyFormat.format(totalCashCreditInvoice)+    NEWLINE);
//		     
//	 		} // End if
		      
		      
//			  
//		      if( transactionCash.size ()>0){
//		    	 
//		    	  body.append (" CASH INVOICE "+ NEWLINE);
//		    	  body.append ( INVOICE_SUMMARY_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
//		     for( int i = 0 ; i < transactionCash.size () ; i ++ ) {
//		    	 TransactionHeaders    tr = transactionCash.get ( i );
//			   	 String clientName=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
//			     .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  ) ).unique().getClientName();
//			   	 String currencyName= DatabaseUtils.getInstance ( context ).getDaoSession ().getCurrenciesDao().queryBuilder ()
//				 .where ( CurrenciesDao.Properties.CurrencyCode.eq (  tr.getCurrencyCode()   ) ).unique().getCurrencyName() ;
//			   	 String clientaddress=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
//					     .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  ) ).unique().getClientAddress();
//					   	 
//				 body.append ( String.format ( "%-"+13+"s %"+10+"s %"+22 +"s %"+ 12 + "s %"+15+"s "   ,
//						 tr.getTransactionCode()  , tr.getClientCode().length () >10 ? tr.getClientCode().substring ( 0 , 10 ) : tr.getClientCode (),	
//							        clientName.length () >22 ? clientName.substring ( 0 , 22 ) : clientName	 ,
//							       moneyFormat.format( tr.getTotalTaxAmount()),
//							       clientaddress.length () >10 ? clientaddress.substring ( 0 , 10 ) : clientaddress	 
//								     )  + NEWLINE );
//				 totalCashInvoice=totalCashInvoice+tr.getTotalTaxAmount();
//					 
//					if(count % 36 == 0 && count != 0    )  
//					{  
//						subHeader.append ("                      Continued   "+ NEWLINE);
//							body.append ( NEWLINE );
//					   		body.append ( NEWLINE );
//							body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					    	body.append ( NEWLINE );
//					 	   	subHeader = new StringBuilder ();
//					 	    pageNumber =pageNumber +1;
//					 	    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
//						 	subHeader.append ("               SALES SUMMARY       "+ NEWLINE);
//							subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
//							subHeader.append ( HORIZANTAL_LINE + NEWLINE );
//							subHeader.append (" CASH INVOICE "+ NEWLINE);
//					    	subHeader.append (INVOICE_SUMMARY_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
//					    	
//					    	
//					    	body.append(subHeader);
//								 
//					  }
//
//				         	 count=count+1;
//					
//				} // End for loop
//		     body.append ( HORIZANTAL_LINE + NEWLINE ); 
//		     body.append ("              TOTAL CASH INVOICE:                "+moneyFormat.format(totalCashInvoice)+    NEWLINE);
//		     
//	 		} // End if
		     
		     
		      
		     
		    
		  
		    
//		  	if ( PermissionsUtils.getEnableCreditUncollectedPrint ( context ,   DatabaseUtils.getCurrentUserCode ( context ) ,  DatabaseUtils.getCurrentCompanyCode(context) ) ) {
//				 
//		
//		      if( transactionCredits.size () > 0){
//		    	//  totalCashInvoice=0d;
//		    	  count = count+5;
//		    	  body.append (" CREDIT INVOICE "+ NEWLINE);
//		    	  body.append (INVOICE_SUMMARY_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
//				     for( int i = 0 ; i < transactionCredits.size () ; i ++ ) {
//				    	 TransactionHeaders    tr = transactionCredits.get ( i );
//					   	 String clientName=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
//					     .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  ) ).unique().getClientName();
//					   	 String currencyName= DatabaseUtils.getInstance ( context ).getDaoSession ().getCurrenciesDao().queryBuilder ()
//						 .where ( CurrenciesDao.Properties.CurrencyCode.eq (  tr.getCurrencyCode()   ) ).unique().getCurrencyName() ;
//					   	 String clientaddress=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
//							     .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  ) ).unique().getClientAddress();
//						 
//						 body.append ( String.format ( "%-"+13+"s %"+10+"s %"+22 +"s %"+ 12 + "s %"+15+"s "   ,
//								 tr.getTransactionCode()  , tr.getClientCode().length () >10 ? tr.getClientCode().substring ( 0 , 10 ) : tr.getClientCode (),	
//									        clientName.length () >22 ? clientName.substring ( 0 , 22 ) : clientName	 ,
//									       moneyFormat.format( tr.getTotalTaxAmount()),
//									       clientaddress.length () >10 ? clientaddress.substring ( 0 , 10 ) : clientaddress	 
//										     )  + NEWLINE );
//						// totalCashInvoice=totalCashInvoice+tr.getTotalTaxAmount();
//						 totalCreditInvoice = totalCreditInvoice + tr.getTotalTaxAmount();
//							if(count % 36== 0 && count != 0    )  
//							{  
//								subHeader.append ("                      Continued   "+ NEWLINE);
//									body.append ( NEWLINE );
//							   		body.append ( NEWLINE );
//									body.append ( NEWLINE );
//							    	body.append ( NEWLINE );
//							    	body.append ( NEWLINE );
//							    	body.append ( NEWLINE );
//							    	body.append ( NEWLINE );
//							    
//							    	body.append ( NEWLINE );
//							    	body.append ( NEWLINE );
//							    	body.append ( NEWLINE );
//							    	body.append ( NEWLINE );
//							    	body.append ( NEWLINE );
//							    	body.append ( NEWLINE );
//							    	body.append ( NEWLINE );
//							    	body.append ( NEWLINE );
//							    	body.append ( NEWLINE );
//							    	body.append ( NEWLINE );
//							 	   	subHeader = new StringBuilder ();
//							 	    pageNumber =pageNumber +1;
//							 	    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
//								 	subHeader.append ("               SALES SUMMARY       "+ NEWLINE);
//									subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
//									subHeader.append ( HORIZANTAL_LINE + NEWLINE );
//									subHeader.append (" CREDIT INVOICE "+ NEWLINE);
//							    	subHeader.append (INVOICE_SUMMARY_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
//							     
//							    	
//							    	body.append(subHeader);
//										 
//							  }
//
//						         	 count=count+1;
//							
//						} // End for loop
//				     body.append ( HORIZANTAL_LINE + NEWLINE ); 
//				     body.append ("              TOTAL CREDIT INVOICE:                "+moneyFormat.format(totalCreditInvoice)+    NEWLINE);
//				     
//				     
//				     
//			 		} // End if
//		      
//				}
				
	else{		
		transactionCredit.addAll( transactionCashCredit );
		  if( transactionCredit.size () > 0){
		    	//  totalCashInvoice=0d;
		    	  count = count+5;
		    	  body.append (" CREDIT INVOICE "+ NEWLINE);
		    	  body.append (INVOICE_SUMMARY_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
				     for( int i = 0 ; i < transactionCredit.size () ; i ++ ) {
				    	 TransactionHeaders    tr = transactionCredit.get ( i );
					   	 String clientName=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
					     .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  ) ).unique().getClientName();
					   	 String currencyName= DatabaseUtils.getInstance ( context ).getDaoSession ().getCurrenciesDao().queryBuilder ()
						 .where ( CurrenciesDao.Properties.CurrencyCode.eq (  tr.getCurrencyCode()   ) ).unique().getCurrencyName() ;
					   	 String clientaddress=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
							     .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  ) ).unique().getClientAddress();
						 
						 body.append ( String.format ( "%-"+13+"s %"+10+"s %"+22 +"s %"+ 12 + "s %"+15+"s "   ,
								 tr.getTransactionCode()  , tr.getClientCode().length () >10 ? tr.getClientCode().substring ( 0 , 10 ) : tr.getClientCode (),	
									        clientName.length () >22 ? clientName.substring ( 0 , 22 ) : clientName	 ,
									       moneyFormat.format( tr.getTotalTaxAmount()),
									       clientaddress.length () >10 ? clientaddress.substring ( 0 , 10 ) : clientaddress	 
										     )  + NEWLINE );
						// totalCashInvoice=totalCashInvoice+tr.getTotalTaxAmount();
						 totalCreditInvoice = totalCreditInvoice + tr.getTotalTaxAmount();
							if(count % 36== 0 && count != 0    )  
							{  
								subHeader.append ("                      Continued   "+ NEWLINE);
									body.append ( NEWLINE );
							   		body.append ( NEWLINE );
									body.append ( NEWLINE );
							    	body.append ( NEWLINE );
							    	body.append ( NEWLINE );
							    	body.append ( NEWLINE );
							    	body.append ( NEWLINE );
							    
							    	body.append ( NEWLINE );
							    	body.append ( NEWLINE );
							    	body.append ( NEWLINE );
							    	body.append ( NEWLINE );
							    	body.append ( NEWLINE );
							    	body.append ( NEWLINE );
							    	body.append ( NEWLINE );
							    	body.append ( NEWLINE );
							    	body.append ( NEWLINE );
							    	body.append ( NEWLINE );
							 	   	subHeader = new StringBuilder ();
							 	    pageNumber =pageNumber +1;
							 	    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
								 	subHeader.append ("               SALES SUMMARY       "+ NEWLINE);
									subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
									subHeader.append ( HORIZANTAL_LINE + NEWLINE );
									subHeader.append (" CREDIT INVOICE "+ NEWLINE);
							    	subHeader.append (INVOICE_SUMMARY_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
							      	body.append(subHeader);
										 
							  }
								count = count+1;
							
						} // End for loop
				     body.append ( HORIZANTAL_LINE + NEWLINE ); 
				     body.append ("              TOTAL CREDIT INVOICE:                "+moneyFormat.format(totalCreditInvoice)+    NEWLINE);
				     
				     
				     
			 		} // End if	
					
				}
				
				
		      body.append ("              TOTAL CASH AND CREDIT INVOICE:                "+moneyFormat.format(totalCreditInvoice+totalCashCreditInvoice+totalCashInvoice+totalCreditInvoiceUnco)+    NEWLINE);
			     
			     
		      if(transactionVoid.size() > 0   ){
		    	  totalCashInvoice = 0d;
		      body.append ( "   Void Invoice  "+ NEWLINE ); 
			  body.append ( INVOICE_SUMMARY_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
			     for( int i = 0 ; i < transactionVoid.size () ; i ++ ) {
			    	 TransactionHeaders    tr = transactionVoid.get ( i );
				   	 String clientName=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
				     .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  ) ).unique().getClientName();
				   	 String currencyName= DatabaseUtils.getInstance ( context ).getDaoSession ().getCurrenciesDao().queryBuilder ()
					 .where ( CurrenciesDao.Properties.CurrencyCode.eq (  tr.getCurrencyCode()   ) ).unique().getCurrencyName() ;
				 	 String clientaddress=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
						     .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  ) ).unique().getClientAddress();
					 
					 body.append ( String.format ( "%-"+13+"s %"+10+"s %"+22 +"s %"+ 12 + "s %"+15+"s "   ,
							 tr.getTransactionCode()  , tr.getClientCode().length () >10 ? tr.getClientCode().substring ( 0 , 10 ) : tr.getClientCode (),	
								        clientName.length () >22 ? clientName.substring ( 0 , 22 ) : clientName	 ,
								       moneyFormat.format( tr.getTotalTaxAmount()),
								       clientaddress.length () >10 ? clientaddress.substring ( 0 , 10 ) : clientaddress	 
									     )  + NEWLINE );
					 totalCashInvoice=totalCashInvoice+tr.getTotalTaxAmount();
						 
						if(count % 36== 0 && count != 0    )  
						{  
							subHeader.append ("                      Continued   "+ NEWLINE);
								body.append ( NEWLINE );
						   		body.append ( NEWLINE );
								body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						    	body.append ( NEWLINE );
						 	   	subHeader = new StringBuilder ();
						 	    pageNumber = 
						 	    		pageNumber +1;
						 	    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
							 	subHeader.append ("               SALES SUMMARY       "+ NEWLINE);
								subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
								subHeader.append ( HORIZANTAL_LINE + NEWLINE );
								subHeader.append ("   Void Invoice  "+ NEWLINE ); 
						    	subHeader.append (INVOICE_SUMMARY_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
						    	
						    	body.append(subHeader);
									 
						  }

					         	 count=count+1;
						
					} // End for loop
			     body.append ( HORIZANTAL_LINE + NEWLINE ); 
			     body.append ("              TOTAL Void INVOICE:                "+moneyFormat.format(totalCashInvoice)+    NEWLINE);

		      }
		   
		      int lastPageM=count / 34;
				int restM = count % 34;
				footer = new StringBuilder ();
				if(pageNumber == 1)
				{	  for(int u = 0 ; u< 29-count - 3  ;u++)
					footer.append ( NEWLINE );
				} 
				 if(pageNumber == ( lastPageM + 1 )   )
			    {
				 if(restM>4)
	    	     for(int u = 0 ; u< 29 - restM - 3 ;u++)
	  	        	footer.append ( NEWLINE );
			      }
				//footer.append ( "SALES REP                       WAREHOUSE IN CHARGE                   SECURITY" );
	       	    footer.append ( "                                End Of Sales Summary");
		      // footer.append ( NEWLINE );
		       //footer.append ( NEWLINE );
		      // footer.append ( "                                LAST PAGE");
			// Build the footer
		}
//			if ( type == Type.INVOICESUMMARY   ) {
//		        int lastPageM=count / 38;
//				int restM = count % 38;
//				footer = new StringBuilder ();
//				if(pageNumber==1)
//				{	  for(int u=0 ; u< 38-transactionHeaderss.size() -3  ;u++)
//					footer.append ( NEWLINE );
//				} 
//				 if(pageNumber == ( lastPageM + 1 )   )
//			    {
//				 if(restM>4)
//	    	     for(int u=0 ; u< 38-restM-3 ;u++)
//	  	        	footer.append ( NEWLINE );
//			      }
//				//footer.append ( "SALES REP                       WAREHOUSE IN CHARGE                   SECURITY" );
//	       	    footer.append ( "                                LASTE PAGE");
//	     	 } // End if 
//			
//			if ( type == Type.COLLECTIONSUMMARY   ) {
//		        int lastPageM=collectionHeaderss.size() / 38;
//				int restM = collectionHeaderss.size() % 38;
//				footer = new StringBuilder ();
//				
//				 if(pageNumber == ( lastPageM + 1 ) )
//			    { if(pageNumber==1)
//  	  				if(( 38 - collectionHeaderss.size() - 3 )>0)
//  				{	    for(int u=0 ; u< 38-collectionHeaderss.size()  - 3 ;u++)
//  					footer.append ( NEWLINE );
//  				}
//  	  				else{
//				 if(restM>4)
//	    	     for(int u=0 ; u< 38-restM-3 ;u++)
//	  	        	footer.append ( NEWLINE );
//			      }}
//				//footer.append ( "SALES REP                       WAREHOUSE IN CHARGE                   SECURITY" );
	  //     	    footer.append ( "                                LASTE PAGE");
//	     	 } // End if 		
				
			 	
			 //MISSED ITEMS	
	        	 if ( type == Type.MISSEDITEMS   ) {
				     for( int i = 0 ; i < missedItems.size () ; i ++ ) {
				    	 MissedItems    tr = missedItems.get ( i );
				    	  
					body.append (   String.format ( "%-"+10+"s %"+20+"s %"+15 +"s %"+ 15 + "s %"+10+"s"   ,
							        tr.getItemCode().length () >10 ? tr.getItemCode().substring ( 0 , 10 ) : tr.getItemCode (),	
							        tr.getItemName().length () >20 ? tr.getItemName().substring ( 0 , 20 ) : tr.getItemName()	 ,
							        tr.getQuantityMedium()  ,tr.getQuantitySmall(),
							       tr.getBaseUnit() 
								     )  + NEWLINE );
				 	
					 
					if(count % 38 == 0 && count != 0    )  
					{  
							body.append ( NEWLINE );
					   		body.append ( NEWLINE );
							body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					 	   	subHeader = new StringBuilder ();
					 	    pageNumber =pageNumber +1;
					 	    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
						 	subHeader.append ("               MISSED ITEMS       "+ NEWLINE);
							subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
							subHeader.append ( HORIZANTAL_LINE + NEWLINE );
					    	subHeader.append (MISSED_ITEM_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
					    	
					    	body.append(subHeader);
								 
					  }

				         	 count=count+1;
					
				} // End for loop
	 		} // End if
			// Build the footer
			
			if ( type == Type.MISSEDITEMS   ) {
		        int lastPageM=missedItems.size() / 38;
				int restM = missedItems.size() % 38;
				footer = new StringBuilder ();
			 
				 if(pageNumber == ( lastPageM + 1 )   || pageNumber == 1)
			    {
					 if(pageNumber==1)
		  	  				if(( 38 - missedItems.size() - 3 )>0)
		  				{	    for(int u=0 ; u< 38-missedItems.size()  - 3 ;u++)
		  					footer.append ( NEWLINE );
		  				}
		  	  				else{
				 if(restM>4)
	    	     for(int u=0 ; u< 38-restM-3 ;u++)
	  	        	footer.append ( NEWLINE );
			      }}
				//footer.append ( "SALES REP                       WAREHOUSE IN CHARGE                   SECURITY" );
	       	    footer.append ( "                                LAST PAGE");
	     	 } // End if 		
		 		

		 //CLIENTASSET	
	       	 if ( type == Type.CLIENTASSET   ) {
	       	      for( int i = 0 ; i < clientAssetPrinting.size () ; i ++ ) {
				    ClientAssetPrinting    tr = clientAssetPrinting.get ( i );
				  	body.append (   String.format ( "%-"+10+"s %"+20+"s %"+15 +"s %"+ 15 + "s %"+15+"s"   ,
							        tr.getAssetCode().length () >10 ? tr.getAssetCode().substring ( 0 , 10 ) : tr.getAssetCode (),	
							        tr.getAssetName().length () >20 ? tr.getAssetName().substring ( 0 , 20 ) : tr.getAssetName()	 ,
							        tr.getStatusName().length () >15 ? tr.getStatusName().substring ( 0 , 15 ) : tr.getStatusName (),	
								    tr.getExistanceName().length () >15? tr.getExistanceName().substring ( 0 , 15 ) : tr.getExistanceName()	 ,
									tr.getItemName().length () >15? tr.getItemName().substring ( 0 , 15 ) : tr.getItemName()	  
													
								     )  + NEWLINE );
				 	
					 
					if(count % 38 == 0 && count != 0    )  
					{  
							body.append ( NEWLINE );
					   		body.append ( NEWLINE );
							body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					 	   	subHeader = new StringBuilder ();
					 	    pageNumber =pageNumber +1;
					 	    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
						 	subHeader.append ("                  CLIENT ASSET      "+ NEWLINE);
							subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
							subHeader.append ( HORIZANTAL_LINE + NEWLINE );
					    	subHeader.append (Client_Asset_Printing_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
					    	
					    	body.append(subHeader);
								 
					  }

				         	 count=count+1;
					
				} // End for loop
	 		} // End if
			// Build the footer
			
			if ( type == Type.CLIENTASSET   ) {
		        int lastPageM=clientAssetPrinting.size() / 38;
				int restM = clientAssetPrinting.size() % 38;
				footer = new StringBuilder ();
			 
				 if(pageNumber == ( lastPageM + 1 )    )
			    {
					 if(pageNumber==1)
		  	  				if(( 38 - clientAssetPrinting.size() - 3 )>0)
		  				{	    for(int u=0 ; u< 38-clientAssetPrinting.size()  - 3 ;u++)
		  					footer.append ( NEWLINE );
		  				}
		  	  				else{
				 if(restM>4)
	    	     for(int u=0 ; u< 38-restM-3 ;u++)
	  	        	footer.append ( NEWLINE );
			      }}
				//footer.append ( "SALES REP                       WAREHOUSE IN CHARGE                   SECURITY" );
	       	    footer.append ( "                                LAST PAGE");
	     	 } // End if 		
			
			//PLAN VISITS
		      if ( type == Type.PLANVISITSREPORT   ) {
	       	      for( int i = 0 ; i < cycleCalls.size () ; i ++ ) {
	       	    	CycleCalls    tr = cycleCalls.get ( i );
	       	     String clientName=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
					               .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  ) ).unique().getClientName();
					   
				  	body.append (   String.format ( "%-"+10+"s %"+20+"s %"+13 +"s %"+ 13 + "s %"+15+"s"   ,
							        tr.getClientCode().length () >10 ? tr.getClientCode().substring ( 0 , 10 ) :tr.getClientCode(),	
							        clientName.length () >20 ? clientName.substring ( 0 , 20 ) : clientName	 ,
							        tr.getWeek() ,	
								    tr.getDay()  ,tr.getSequence()	  
													
								     )  + NEWLINE );
				 	
					 
					if(count % 38 == 0 && count != 0    )  
					{  
							body.append ( NEWLINE );
					   		body.append ( NEWLINE );
							body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					    	body.append ( NEWLINE );
					 	   	subHeader = new StringBuilder ();
					 	    pageNumber =pageNumber +1;
					 	    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
						 	subHeader.append ("            PLAN VISITS REPORT     "+ NEWLINE);
							subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
							subHeader.append ( HORIZANTAL_LINE + NEWLINE );
					    	subHeader.append (Plan_visit_report  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
					    	
					    	body.append(subHeader);
								 
					  }

				         	 count=count+1;
					
				} // End for loop
	 		} // End if
			// Build the footer
			
			if ( type == Type.PLANVISITSREPORT   ) {
		        int lastPageM=cycleCalls.size() / 38;
				int restM = cycleCalls.size() % 38;
				footer = new StringBuilder ();
				 
				 if(pageNumber == ( lastPageM + 1 )   )
			    {
					 if(pageNumber==1)
		  	  				if(( 38 - cycleCalls.size() - 3 )>0)
		  				{	    for(int u=0 ; u< 38-cycleCalls.size()  - 3 ;u++)
		  					footer.append ( NEWLINE );
		  				}
		  	  				else{
		  	  					if(restM>4)
		  	  						for(int u=0 ; u< 38-restM-3 ;u++)
		  	  							footer.append ( NEWLINE );
		  	  					}
			    }
				//footer.append ( "SALES REP                       WAREHOUSE IN CHARGE                   SECURITY" );
	       	    footer.append ( "                                LAST PAGE");
	     	 } // End if 		
			
			
			//ITEMDISTRIBUTION	
			if ( type == Type.ITEMDISTRIBUTION   ) {
			     for( int i = 0 ; i < items.size () ; i ++ ) {
			    	  Items    tr = items.get ( i );
			    	body.append (String.format ( "%-"+10+"s %"+20+"s ", tr.getItemCode().length () >10 ? tr.getItemCode().substring ( 0 , 10 ) : tr.getItemCode (),	
				    tr.getItemName().length () >20 ? tr.getItemName().substring ( 0 , 20 ) : tr.getItemName()	) + NEWLINE  );
			    	  count=count+1;
			    	for( int j = 0 ; j < itemDistrbutionPrinting.size () ; j ++ ) {
			        ItemDistrbutionPrinting    id = itemDistrbutionPrinting.get ( j );				       
			        if(tr.getItemCode().equals( id.getItemCode()) ) 
				      {
				                body.append (   String.format ( "%-"+10+"s %"+20+"s %"+20 +"s %"+ 8 + "s %"+10+"s"   ,
				                " ",	
						        " "	 ,
						        id.getClientName().length () >20 ? id.getClientName().substring ( 0 , 20 ) : id.getClientName()	,		
						        id.getQuantityMedium()  ,id.getQuantitySmall()							     )  + NEWLINE );
			 	
				                count=count+1;
		     		if(count % 38 == 0 && count != 0    )  
			    	{  
						body.append ( NEWLINE );
				   		body.append ( NEWLINE );
						body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				    	body.append ( NEWLINE );
				 	   	subHeader = new StringBuilder ();
				 	    pageNumber =pageNumber +1;
				 	    subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
					 	subHeader.append ("               ITEM DISTRIBUTION       "+ NEWLINE);
						subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
						subHeader.append ( HORIZANTAL_LINE + NEWLINE );
				    	subHeader.append (ITEM_DISTRIBUTION_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
				    	
				    	body.append(subHeader);
							 
				     }

				     }  	
				
			        }
			    	} // End for loop
		} // End if
		     
  		// Build the print out
		StringBuilder mainFormat = new StringBuilder ();
		mainFormat.append ( mainHeader );
		mainFormat.append ( subHeader );
		mainFormat.append ( body );
		mainFormat.append ( footer );
		return mainFormat.toString ();
	}
	
	
	/**
	 * Return a substring of the provided string if it exceeds the provided maximum length, or the string as is otherwise.<br>
	 * The purpose if to properly format the printed line.
	 * 
	 * @param string	The string to format.
	 * @param maxLength	The maximum length of the formatted string.
	 * @return	The formatted string.
	 */
	private String subString ( String string , int maxLength ) {
		return string.length () > maxLength ? string.substring ( 0 , maxLength ) : string;
	}
	
}