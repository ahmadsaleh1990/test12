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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.SyncWise.Android.Database.Banks;
import me.SyncWise.Android.Database.BanksDao;
import me.SyncWise.Android.Database.ClientDues;
import me.SyncWise.Android.Database.ClientDuesDao;
import me.SyncWise.Android.Database.ClientItemCodes;
import me.SyncWise.Android.Database.ClientItemCodesDao;
import me.SyncWise.Android.Database.ClientMouvementStock;
import me.SyncWise.Android.Database.ClientMouvementStockDao;
import me.SyncWise.Android.Database.ClientSellingSuggestion;
import me.SyncWise.Android.Database.ClientSellingSuggestionDao;
import me.SyncWise.Android.Database.Clients;
import me.SyncWise.Android.Database.ClientsDao;
import me.SyncWise.Android.Database.CollectionDetails;
import me.SyncWise.Android.Database.CollectionDetailsDao;
import me.SyncWise.Android.Database.CollectionHeaders;
import me.SyncWise.Android.Database.CollectionInvoices;
import me.SyncWise.Android.Database.CollectionInvoicesDao;
import me.SyncWise.Android.Database.CollectionUtils;
import me.SyncWise.Android.Database.Companies;
import me.SyncWise.Android.Database.CompaniesDao;
import me.SyncWise.Android.Database.Currencies;
import me.SyncWise.Android.Database.CurrenciesDao;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.ItemBarcodes;
import me.SyncWise.Android.Database.ItemBarcodesDao;
import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Database.TransactionDetails;
import me.SyncWise.Android.Database.TransactionDetailsDao;
import me.SyncWise.Android.Database.TransactionHeaders;
import me.SyncWise.Android.Database.TransactionHeadersDao;
import me.SyncWise.Android.Database.Users;
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Modules.Printing.PrintingActivity.Type;
import android.content.Context;
import android.database.Cursor;

public class FormatCreatorZebraR implements FormatCreator {
	
	public final int TOTAL_LENGTH = 48;
	public final int CODE_LENGTH = 12;
	public final int QUANTITY_LENGTH = 10;
	public final int UNIT_PRICE_LENGTH = 10;
	public final int LINE_AMOUNT_LENGTH = 13;
	public final int BARCODE_LENGTH = 16;
	public final int NINE = 8;
	public final int UserCode_Length=10;
 	public final int UserName_Length=25;
 	
 	
 	
	private Companies  company;
	private TransactionHeaders transactionHeader;
	private ArrayList<TransactionHeaders> transactionHeaders;
	private ArrayList<TransactionDetails> transactionDetails;
	private Clients client;
	private Currencies currency;
	private   CollectionHeaders collectionHeaders;
	private ArrayList<CollectionInvoices>collectionInvoices;
	private ArrayList<CollectionDetails> collectionDetails;
	private ArrayList < CollectionSummaryPrinting > collectionSummaryHeaderss;
    private ArrayList < SummaryReturnPrinting > summaryReturnPrintings;
	
	
	private double total = 0;
	public final String PAYMENT					= "Payment";
	public final String AMOUNT					= "Amount";
	 
	public final String INVOICE_NUM				= "INVOICE#   : ";
 
	public final String SALES_MAN				= "Sales Man : ";
	public final String CLIENT_ID				= "Client Number  : ";
	public final String CLIENT_NAME				= "Client Name    : ";
	public final String CLIENT_ADDRESS			= "Client Address : ";
	public final String CLIENT_PHONE 			= "Client Phone   : ";
	public final String ITEM_CODE 				= "IC  : ";
	public final String CLIENT_ITEM_CODE 		= "CIC : ";
	public final String CASE_LABEL 				= " /C";
	public final String UNIT_LABEL 				= " /U";
	public final String DATE					= "Date:%d/%d/%d";
	public final String PRINT_DATE				= "Print Date : ";
	public final String ISSUE_DATE				= "Issue Date : ";
	public final String ORDER_NUM				= "ORDER#     : ";
	public final String RFR_NUM					= "REQUEST#   : ";
	public final String BARCODE					= "BC. : ";
	public final String HORIZANTAL_LINE			= "------------------------------------------------";
	public final String PAGE					= "                    PAGE ";
	public final String RECEPIT					= "                    PRINTOUT";
	public final String RECEPIT_COPY			= "             COPY - PRINTOUT - COPY";
	public final String OI_DETIAL_HEADER		= "NAME / CODE |    QTY   |   PRICE  |   TOTAL";
	public final String GROSS_AMOUNT			= "Gross Amount   : ";
	public final String DISCOUNT_AMOUNT			= "DiscountAmount : ";
	public final String TOTAL_AMOUNT			= "Total Amount   : ";
	public final String NEWLINE 				= "\r";
	public final String TABSPACES 				= "   ";
	public final String HEADER 					= "      QQP - QATAR QUALITY PRODUCTS " + NEWLINE +
												  "      Tel.	: (+974) 44469823" + NEWLINE +
												  "      Fax.	: (+974) 44437463" + NEWLINE +
												  "      P. O. Box	: 4792" + NEWLINE +
												  "      C. R. No.	: 16700" + NEWLINE +
												  "      Doha - Qatar" + NEWLINE +
												  "      E-mail:	qqp@alibinali.com" + NEWLINE;
	//Jose-print
	public final String SALES_ORDER_SUMMARY		= "Sales Order Summary";
	public final String TRANSACTION				= "Transaction# ";
	public final String CLIENT_Name				= "Client Name ";
	public final String CURRENCY				= "Currency";
	public final String TOTAL					= "Total Amount";
	public final String TEL						= "Tel.:";
	public final String FAX						= "FAX.:";
	public final String POBOX					= "P.O.BOX:";
	public final String CRNO					= "C.R. No.:";
	public final String EMAIL					= "E-mail:";
	public final String space					= " ";
	public final String CASH					= "CASH";
	public final String CHECK					= "CHECK";
	public final String CHECK_NUM				= "Check# : ";
	public final String BANK					= "Bank   : ";
	public final String date					= "Date   : ";
	public final String INVOICE					= "INVOICE#";
	public final String COLLECTION_DETAILS		= "Collection Details";
	public final String INVOICE_ALLOCATION		= "Invoice Allocation";
	public final String COLLECTION_AMOUNT		= "Collection Amount : ";
	public final String TOTAL_PAYMENT			= "Total Payment : ";
	public final String TOTAL_CASH				= "Total CASH";
	public final String TOTAL_CHECK				= "Total CHEQUE";
	public final String COLLECTION_NUM			= " RECEIPT#  : ";
	private List < LoadInSummaryPrinting > loadPrintings = new ArrayList < LoadInSummaryPrinting > ();
    public final String ITEM_NAME				= "Name/Code";
	public final String QUANTITY				= "Qty";
	public final String PRICE					= "Price";
	
//	//COLLECTION_SUMMARY
//		public final String Return_SUMMARY_Header= "Transaction#  | CLIENT#   |     CLIENT NAME     |  TOTAL AMOUNT | ADDRESS ";
//	    public final String COLLECTION_SUMMARY_Header= "COLLECTION#   | CLIENT#   |     CLIENT NAME     |  TOTAL AMOUNT | ADDRESS ";
//	    public final String COLLECTION_SUMMARY_CHECK_Header= "COLLECTION#| CLIENT# |     CLIENT NAME   |CHQ DATE  |  BANK  | CHEQUE#|AMOUNT ";
//	   
	
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
	@Override
	public String getFormat () {
		return formatString;
	}
	//Jose-print
	//SUMMARY 
		public FormatCreatorZebraR ( Context context , ArrayList < TransactionHeaders> transactionHeaderss, int type , int printout , Integer pages , Integer currentPage , Integer lastPage) {

				this.transactionHeaders = transactionHeaderss;
            	this.context = context;
				formatString = Creator ( type , 0 , pages , currentPage , lastPage );		
		}
	//end 	Jose-print
		public FormatCreatorZebraR ( Context context , TransactionHeaders transactionHeaders , int type , int printout ) {
			this.transactionHeader = transactionHeaders;
			this.context = context;
			transactionDetails = (ArrayList<TransactionDetails>) DatabaseUtils.getInstance ( context ).getDaoSession ().getTransactionDetailsDao ().queryBuilder ().orderDesc(TransactionDetailsDao.Properties.OrderedType)
					.where ( TransactionDetailsDao.Properties.TransactionCode.eq ( transactionHeaders.getTransactionCode () ) ).list ();
			
			
			String SQL=null;
		 	String selectionArguments [] = null;
	 
//		 	SQL =       " select  count(td.ItemCode),td.ItemCode,td.ItemName,SUM(TotalLineAmount),th.TotalTaxAmount ,td.OrderedType, th.DiscountAmount, th.GrossAmount" +
//		 				" from TransactionDetails td inner join TransactionHeaders th on td.TransactionCode=th.TransactionCode " +
//		 				" where td.TransactionCode=?" +
//		 				" group by td.ItemCode,td.ItemName,td.OrderedType";
		 	
		 	SQL ="  select  sum (td.BasicUnitQuantity),td.ItemCode,td.ItemName, Sum(TotalLineAmount) ,th.TotalTaxAmount ,td.OrderedType, th.DiscountAmount, th.GrossAmount " +
		 			"  from TransactionDetails td inner join TransactionHeaders th on td.TransactionCode=th.TransactionCode  " +
		 			" where td.TransactionCode=?" +
		 			" group by td.ItemCode, th.DiscountAmount, th.GrossAmount ,td.ItemCode,td.ItemName, th.TotalTaxAmount,td.OrderedType";
		 	// Compute the selection arguments
				selectionArguments = new String [] {	
						String.valueOf(transactionHeaders.getTransactionCode () ),
						
				};
				Cursor cursor = DatabaseUtils.getInstance ( context ).getDaoSession ().getDatabase ()
						        .rawQuery(SQL , selectionArguments) ;
				
					// Move the cursor to the first raw
				if ( cursor.moveToFirst () ) {
					// Iterate over all raws
					do {
						LoadInSummaryPrinting loadPrinting = new LoadInSummaryPrinting();
 						loadPrinting.setItemCode(  cursor.getString( 1 ) );
 						loadPrinting.setItemName(  cursor.getString( 2 ) );
 						loadPrinting.setQuantityMedium( (double)cursor.getInt( 0 ) );
 						loadPrinting.setQuantitySmall( cursor.getDouble( 3 ) );
 						loadPrinting.setQuantityMediumReturn(cursor.getDouble( 4 ));
 						loadPrinting.setClientCode(cursor.getString(5));
 						loadPrinting.setFirstLoadQuantityMedium(cursor.getDouble(6));
 						loadPrinting.setFirstLoadQuantitySmall(cursor.getDouble(7));
 						loadPrintings.add ( loadPrinting ); 		 
					} while ( cursor.moveToNext () );
				} // End if
				// Close the cursor
				cursor.close ();
				cursor = null;
		 		
			client = DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao ().queryBuilder ()
					.where ( ClientsDao.Properties.ClientCode.eq ( transactionHeader.getClientCode () ) ).unique ();
			currency = DatabaseUtils.getInstance(context).getDaoSession().getCurrenciesDao().queryBuilder()
					 .where ( CurrenciesDao.Properties.CurrencyCode.eq(transactionHeader.getCurrencyCode())).unique();
			formatString = Creator ( type , 0 , 0 , 0 , 0 );
		}
		
		//COLLECTIONSUMMARY
		public FormatCreatorZebraR ( Context context , int type , ArrayList <CollectionSummaryPrinting> collectionSummaryHeaderss ,ArrayList <SummaryReturnPrinting> summaryReturnPrinting , int printout , Integer pages , Integer currentPage , Integer lastPage ) {
			this.collectionSummaryHeaderss = collectionSummaryHeaderss;
			this.summaryReturnPrintings=summaryReturnPrinting;
			this.context = context;
			formatString = Creator ( type , 0 , pages , currentPage , lastPage );
		}
		
		
		public FormatCreatorZebraR ( Context context , CollectionHeaders collectionHeader , int type , int printout , Integer pages , Integer currentPage , Integer lastPage ) {
			this.collectionHeaders = collectionHeader;
			this.context = context;
			collectionDetails = (ArrayList<CollectionDetails>) DatabaseUtils.getInstance ( context ).getDaoSession ().getCollectionDetailsDao ().queryBuilder ()
					.where ( CollectionDetailsDao.Properties.CollectionCode.eq ( collectionHeader.getCollectionCode() ) ).list ();
//			if ( pages != null && currentPage != null && lastPage != null ) {
//				ArrayList<CollectionDetails> details = new ArrayList < CollectionDetails > ();
//				int rangeStart = currentPage * pages;
//				int rangeEnd = ( currentPage + 1 ) * pages - 1; 
//				int maxRange = collectionDetails.size () - 1;
//				rangeEnd = rangeEnd > maxRange ? maxRange : rangeEnd;
//				for ( int i = rangeStart ; i <= rangeEnd ; i ++ ) {
//					details.add ( collectionDetails.get ( i ) );
//				}
//				collectionDetails = details;
//			}
			switch ( collectionHeaders.getCollectionType() ) {
			case 1:
			case 3:
//				client = DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao ().queryBuilder ()
//				.where ( ClientsDao.Properties.ParentCode.eq ( collectionHeaders.getClientCode () ) ).unique ();
				ArrayList< Clients> cc=(ArrayList<Clients>) DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
			            .where ( ClientsDao.Properties.ParentCode.eq (  collectionHeaders.getClientCode ()  )  ).list();
        				if(cc !=null && cc.size()>0){
        					client=cc.get(0) ;
        					 
        				}
				break;
			case 2:
			
				client = DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao ().queryBuilder ()
				.where ( ClientsDao.Properties.ClientCode.eq ( collectionHeaders.getClientCode () ) ).unique ();

				break;
			}
				
			currency = DatabaseUtils.getInstance(context).getDaoSession().getCurrenciesDao().queryBuilder()
					 .where ( CurrenciesDao.Properties.CurrencyCode.eq(collectionHeader.getCurrencyCode())).unique();		
			collectionInvoices = (ArrayList<CollectionInvoices>) DatabaseUtils.getInstance ( context ).getDaoSession ().getCollectionInvoicesDao().queryBuilder () 
	 				.where (CollectionInvoicesDao.Properties.CollectionCode.eq ( collectionHeader.getCollectionCode() ) ).list ();
			formatString = Creator ( type , 0 , pages , currentPage , lastPage );

		}

	public FormatCreatorZebraR ( Context context , TransactionHeaders transactionHeaders , int type , int printout , Integer pages , Integer currentPage , Integer lastPage ) {
		this.transactionHeader = transactionHeaders;
		this.context = context;
		transactionDetails = (ArrayList<TransactionDetails>) DatabaseUtils.getInstance ( context ).getDaoSession ().getTransactionDetailsDao ().queryBuilder ()
				.where ( TransactionDetailsDao.Properties.TransactionCode.eq ( transactionHeaders.getTransactionCode () ) ).list ();
		if ( pages != null && currentPage != null && lastPage != null ) {
			ArrayList<TransactionDetails> details = new ArrayList < TransactionDetails > ();
			int rangeStart = currentPage * pages;
			int rangeEnd = ( currentPage + 1 ) * pages - 1; 
			int maxRange = transactionDetails.size () - 1;
			rangeEnd = rangeEnd > maxRange ? maxRange : rangeEnd;
			for ( int i = rangeStart ; i <= rangeEnd ; i ++ ) {
				details.add ( transactionDetails.get ( i ) );
			}
			transactionDetails = details;
		}
		client = DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao ().queryBuilder ()
				.where ( ClientsDao.Properties.ClientCode.eq ( transactionHeader.getClientCode () ) ).unique ();
		formatString = Creator ( type , 0 , pages , currentPage , lastPage );
		currency = DatabaseUtils.getInstance(context).getDaoSession().getCurrenciesDao().queryBuilder()
				 .where ( CurrenciesDao.Properties.CurrencyCode.eq(transactionHeader.getCurrencyCode())).unique();
	 

	}
	public FormatCreatorZebraR( Context context , int type , ArrayList <CollectionSummaryPrinting> collectionSummaryHeaderss ,ArrayList <SummaryReturnPrinting> summaryReturnPrinting , int printout ) {
		this.collectionSummaryHeaderss = collectionSummaryHeaderss;
		this.summaryReturnPrintings=summaryReturnPrinting;
		this.context = context;
	     formatString =   Creator ( type , 0 , 0 , 0 , 0 );
	}
	
	public String Creator ( int type , int copyType , Integer pages , Integer currentPage , Integer lastPage ) {
		// Retrieve the current user
		Users user = DatabaseUtils.getInstance ( context ).getDaoSession ().getUsersDao ().queryBuilder ()
				.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( context ) ) ,
						UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( context ) ) ).unique ();
		
		company = DatabaseUtils.getInstance(context).getDaoSession().getCompaniesDao().queryBuilder()
				.where(CompaniesDao.Properties.CompanyCode.eq(DatabaseUtils.getCurrentCompanyCode ( context ) )).unique();
	
		// Retrieve the current date
		Calendar now = Calendar.getInstance ();
		// Declare and initialize string builders used to build the print out
		StringBuilder mainHeader , subHeader, body , footer;
		// Build the main header
		mainHeader = new StringBuilder ();
		mainHeader.append ( NEWLINE );
		mainHeader.append ( NEWLINE );
		if ( type == Type.RECEIPT   )
			mainHeader.append ( COLLECTION_NUM + collectionHeaders.getCollectionCode() + NEWLINE ); 
		
	 
		if(company.getCompanyName() != null)
			mainHeader.append( " " + company.getCompanyName() );
		mainHeader.append(NEWLINE);
		
		mainHeader.append(" " + printAllignment( TEL , TOTAL_LENGTH , TEL.length() ) ) ;
		if(company.getCompanyPhone() != null)
			mainHeader.append(" " + company.getCompanyPhone() );
		mainHeader.append(NEWLINE);
		
		mainHeader.append(" " + printAllignment( FAX , TOTAL_LENGTH , FAX.length() ) );
		if(company.getCompanyFax() != null)
			mainHeader.append(" " +  company.getCompanyFax() );
		mainHeader.append(NEWLINE);
		
		mainHeader.append(" " + printAllignment( POBOX , TOTAL_LENGTH , POBOX.length() ));
		if(company.getPOBox() != null)
			mainHeader.append(" " + company.getPOBox() );
		mainHeader.append(NEWLINE);
		
		mainHeader.append(" " +printAllignment( CRNO , TOTAL_LENGTH , CRNO.length() ));
		if(company.getCRNo() != null)
			mainHeader.append(" " + company.getCRNo() );
		mainHeader.append(NEWLINE);
		
		if(company.getCompanyAddress() != null)
			mainHeader.append( " " + company.getCompanyAddress() );
		mainHeader.append(NEWLINE);
		
		mainHeader.append(" " +printAllignment( EMAIL , TOTAL_LENGTH , EMAIL.length() ));
		if (company.getEmail() != null)
			mainHeader.append(" " + company.getEmail() );


	//	mainHeader.append ( HEADER );
		mainHeader.append ( NEWLINE );
		mainHeader.append ( NEWLINE );
		mainHeader.append ( NEWLINE );
		mainHeader.append ( NEWLINE );
		// Build the sub header
		subHeader = new StringBuilder ();
		
		if ( type == Type.INVOICE  )	//jose20.1
			subHeader.append ( INVOICE_NUM + transactionHeader.getTransactionCode () + NEWLINE );
		if ( type == Type.COLLECTIONSUMMARY ){
			subHeader.append ( subString ( user.getUserCode() , UserCode_Length )  +"  " +SALES_MAN +  subString (user.getUserName ()  , UserName_Length ) + NEWLINE );
		  	subHeader.append ("            COLLECTION SUMMARY       "+ NEWLINE);
			subHeader.append ( PRINT_DATE + now.getTime ()  + NEWLINE );
			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
	    //	subHeader.append ( COLLECTION_SUMMARY_Header  +  NEWLINE  +  HORIZANTAL_LINE  +  NEWLINE );
	    	}
		
		//Jose-print
		if ( type == Type.SALESORDERSUMMARY ){
			subHeader.append ( addSpace("", (TOTAL_LENGTH/2)-((SALES_ORDER_SUMMARY.length())/2) ) + SALES_ORDER_SUMMARY + NEWLINE );
			subHeader.append ( NEWLINE );
			subHeader.append ( " " + printAllignment(SALES_MAN, TOTAL_LENGTH , PRINT_DATE.length()) );
			subHeader.append ( user.getUserName () + NEWLINE );
			subHeader.append ( " " + printAllignment(PRINT_DATE, TOTAL_LENGTH , PRINT_DATE.length()) );
			subHeader.append ( now.getTime () + NEWLINE );
			subHeader.append ( NEWLINE );
			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
			
			subHeader.append  ( " " + addSpace(TRANSACTION, (TOTAL_LENGTH/3) - TRANSACTION.length() ) 
					+ addSpace( TOTAL ,(TOTAL_LENGTH/3) - TOTAL.length() )
					+ addSpace( CURRENCY,(TOTAL_LENGTH/3) - CURRENCY.length() - 1 )
					); 
			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
		}
		else{
		if ( type == Type.RFR )
			subHeader.append ( RFR_NUM + transactionHeader.getTransactionCode () + NEWLINE );
		if ( type == Type.ORDER )
			subHeader.append ( ORDER_NUM + transactionHeader.getTransactionCode () + NEWLINE );			
		if ( copyType == 0)
			subHeader.append ( RECEPIT + NEWLINE );
		else 
			subHeader.append ( RECEPIT_COPY + NEWLINE );
		
		if(type!=Type.COLLECTIONSUMMARY)
		{
			subHeader.append ( CLIENT_ID + client.getClientCode () + NEWLINE );
		
		subHeader.append ( CLIENT_NAME + subString ( client.getClientName () , TOTAL_LENGTH - CLIENT_NAME.length () ) + NEWLINE );
		subHeader.append ( CLIENT_ADDRESS + subString ( client.getClientAddress () , TOTAL_LENGTH - CLIENT_NAME.length () ) + NEWLINE );
		}
		if ( type == Type.RFR || type == Type.ORDER ) {
			subHeader.append ( SALES_MAN + user.getUserName () + NEWLINE );
			subHeader.append ( ISSUE_DATE + transactionHeader.getIssueDate () + NEWLINE );
		}
		if(type!=Type.COLLECTIONSUMMARY)
		{
		subHeader.append ( PRINT_DATE + now.getTime () + NEWLINE );
		}
		if ( type == Type.RFR || type == Type.ORDER ) {
			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
			subHeader.append ( OI_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
		}
		}//end else
		
		if ( type == Type.RECEIPT   ) {
			SimpleDateFormat sdfa1 = new SimpleDateFormat("dd/MM/yyyy",  java.util.Locale.getDefault()); 
			subHeader.append ( SALES_MAN +user.getUserCode()+"  "+ subString ( user.getUserName (),30) + NEWLINE );
			subHeader.append ( ISSUE_DATE + sdfa1.format(collectionHeaders.getCollectionDate()) + NEWLINE );
			subHeader.append ( PRINT_DATE + sdfa1.format(now.getTime ()) + NEWLINE );
			subHeader.append(HORIZANTAL_LINE + NEWLINE);
		 	subHeader.append("I received the following amount as payment:" + NEWLINE);
		 	subHeader.append(HORIZANTAL_LINE + NEWLINE);
		}
		
		
		// Build a decimal format used to properly display monetary values
		DecimalFormat moneyFormat = new DecimalFormat ( "#,##0.00" );
		// Build the body
		body = new StringBuilder ();
		//Jose-print
		if ( type == Type.SALESORDERSUMMARY ){
			for( int i = 0 ; i < transactionHeaders.size () ; i ++ ) {
				TransactionHeaders trH = transactionHeaders.get ( i );
				currency = DatabaseUtils.getInstance(context).getDaoSession().getCurrenciesDao().queryBuilder()
						 .where ( CurrenciesDao.Properties.CurrencyCode.eq(
								 trH.getCurrencyCode())).unique();
				client = DatabaseUtils.getInstance(context).getDaoSession().getClientsDao().queryBuilder()
						.where ( ClientsDao.Properties.ClientCode.eq(
								trH.getClientCode())).unique();
				if (i==0)
					body.append  ( " *" + client.getClientName() + NEWLINE );
				else{
					TransactionHeaders trH1 = transactionHeaders.get ( i - 1);
					if ( !trH.getClientCode().equals(trH1.getClientCode() ) )					
						body.append  ( " *" + client.getClientName() + NEWLINE );
				}
				body.append  ( "     " 
						+ addSpace( trH.getTransactionCode(), (TOTAL_LENGTH/3) - trH.getTransactionCode().length() ) 
						+ addSpace( moneyFormat.format ( trH.getTotalTaxAmount()).toString() ,(TOTAL_LENGTH/3) - trH.getTotalTaxAmount().toString().length() )
						+ addSpace( currency.getCurrencySymbol(), 5 )
						+ NEWLINE
						); 

				total += trH.getTotalTaxAmount();
			
				}
			body.append ( HORIZANTAL_LINE + NEWLINE );
		}
		else{
		if ( type == Type.RFR || type == Type.ORDER ) {
			for( int i = 0 ; i < transactionDetails.size () ; i ++ ) {
				TransactionDetails tr = transactionDetails.get ( i );
				body.append ( subString ( tr.getLineID() + "-" + tr.getItemName () , TOTAL_LENGTH ) );
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
					body.append ( NEWLINE + barcodeOne );
				}
				body.append ( NEWLINE );
				if ( type == Type.ORDER ) {
					String itemCode = ITEM_CODE + tr.getItemCode ();
					itemCode = itemCode.length () > TOTAL_LENGTH ? itemCode.substring ( 0 , TOTAL_LENGTH ) : itemCode; //CODE_LENGTH jose8
					List < ClientItemCodes > clientItems = DatabaseUtils.getInstance ( context ).getDaoSession ().getClientItemCodesDao ().queryBuilder ()
							.where ( ClientItemCodesDao.Properties.ItemCode.eq ( tr.getItemCode () ) ).list ();
					
					
					ClientSellingSuggestion clientSS =  DatabaseUtils.getInstance( context ).getDaoSession().getClientSellingSuggestionDao().queryBuilder()
							.where(
							ClientSellingSuggestionDao.Properties.ClientCode.eq( client.getClientCode () ),
								ClientSellingSuggestionDao.Properties.ItemCode.eq( tr.getItemCode() ) ,
								ClientSellingSuggestionDao.Properties.DivisionCode.eq( DatabaseUtils.getCurrentDivisionCode ( context ) ) ,
								ClientSellingSuggestionDao.Properties.CompanyCode.eq( DatabaseUtils.getCurrentCompanyCode ( context ) )								
								).unique();
					
					
					ClientMouvementStock clientMS =  DatabaseUtils.getInstance( context ).getDaoSession().getClientMouvementStockDao().queryBuilder()
							.where(ClientMouvementStockDao.Properties.ClientCode.eq( client.getClientCode () ), 
									ClientMouvementStockDao.Properties.ItemCode.eq( tr.getItemCode() ),
									ClientMouvementStockDao.Properties.DivisionCode.eq( DatabaseUtils.getCurrentDivisionCode ( context ) ) ,
									ClientMouvementStockDao.Properties.CompanyCode.eq( DatabaseUtils.getCurrentCompanyCode ( context ) )								 )
									.unique();
					
					
					String clientItemCode = "";
					if ( ! clientItems.isEmpty () ) {
						clientItemCode = CLIENT_ITEM_CODE + clientItems.get ( 0 ).getClientItemCode ();
						clientItemCode = clientItemCode.length () > CODE_LENGTH ? clientItemCode.substring ( 0 , CODE_LENGTH ) : clientItemCode;
					}
							
					body.append( itemCode + NEWLINE);
					body.append ( String.format ( "%-" + CODE_LENGTH + "s %" + QUANTITY_LENGTH + "s %" + UNIT_PRICE_LENGTH + "s %" + LINE_AMOUNT_LENGTH + "s" , 
							"" , tr.getQuantityMedium ().toString () + CASE_LABEL,
							moneyFormat.format ( tr.getPriceMedium () ) , "" ) + NEWLINE );
					body.append ( String.format ( "%-" + CODE_LENGTH + "s %" + QUANTITY_LENGTH + "s %" + UNIT_PRICE_LENGTH + "s %" + LINE_AMOUNT_LENGTH + "s" , 
							clientItemCode , tr.getQuantitySmall ().toString () + UNIT_LABEL,
							moneyFormat.format ( tr.getPriceSmall () ) , moneyFormat.format ( tr.getTotalLineAmount () ) ) + NEWLINE );
					
					if (  clientSS != null ){
						Double SQC = clientSS.getSuggestedQuantityCase();
						Double SQU = clientSS.getSuggestedQuantityUnit();
					body.append ( String.format ( "%-" + CODE_LENGTH + "s %" + NINE + "s %" + NINE + "s %" + NINE + "s" , 
							"Suggestions" ,SQC.toString () + CASE_LABEL, SQU.toString () + UNIT_LABEL, "" ) + NEWLINE );
					}
					if ( clientMS != null ){
						Double week1 = clientMS.getWeek1 ();
						Double week2 = clientMS.getWeek2 ();
						Double week3 = clientMS.getWeek3 ();
						Double week4 = clientMS.getWeek4 ();
					body.append ( String.format ( "%-" + 9 + "s %" + NINE + "s %" + NINE + "s %" + NINE + "s %" + NINE + "s" , 
							"Mvt" , week1.toString () , week2.toString () , week3.toString () , week4.toString () ) + NEWLINE );

					}
				}
				else
					body.append ( String.format ( "%-" + CODE_LENGTH + "s %" + QUANTITY_LENGTH + "s %" + UNIT_PRICE_LENGTH + "s %" + LINE_AMOUNT_LENGTH + "s" , 
							tr.getItemCode ().length () > CODE_LENGTH ? tr.getItemCode ().substring ( 0 , CODE_LENGTH ) : tr.getItemCode () , tr.getBasicUnitQuantity ().toString () ,
							moneyFormat.format ( tr.getPriceSmall () ) , moneyFormat.format ( tr.getTotalLineAmount () ) ) + NEWLINE );
				if ( i != transactionDetails.size () - 1 )
				{
					body.append ( NEWLINE + NEWLINE );
					if ( PermissionsUtils.getPrintHorizontalLine (context , DatabaseUtils.getCurrentUserCode (context) ,DatabaseUtils.getCurrentCompanyCode(context) ))
						body.append ( HORIZANTAL_LINE );
					}
			} // End for loop
			body.append ( HORIZANTAL_LINE + NEWLINE );
			body.append ( NEWLINE );
		} // End if
		}//  End else
		if ( type == Type.RECEIPT ){
			SimpleDateFormat sdfa = new SimpleDateFormat("dd/MM/yyyy",  java.util.Locale.getDefault()); 
			if ( collectionHeaders.getCollectionType() != CollectionUtils.CollectionType.ONACCOUNT ){
				
				if(collectionInvoices != null && collectionInvoices.size () > 0){
					 Double tot=0.0;
					 String currencies="";
					 body.append ("Invoice Allocation"+NEWLINE+HORIZANTAL_LINE +NEWLINE);
					 body.append(" INV #            Currency    Amount" +NEWLINE+HORIZANTAL_LINE+NEWLINE );
					for( int i = 0 ; i < collectionInvoices.size () ; i ++ ) {
						 CollectionInvoices ci = collectionInvoices.get ( i );
//						 ClientDues c= DatabaseUtils.getInstance ( context ).getDaoSession ().getClientDuesDao ().queryBuilder ()
//									.where ( ClientDuesDao.Properties.InvoiceCode.eq ( ci.getInvoiceCode() ) ).unique ();
						 Currencies cu= DatabaseUtils.getInstance ( context ).getDaoSession ().getCurrenciesDao ().queryBuilder ()
									.where ( CurrenciesDao.Properties.CurrencyCode.eq ( ci.getCurrencyCode() ) ).unique ();
						 currencies=cu.getCurrencySymbol();
						 double payment =0;
						//	if( c  != null)
							 //double open=c.getInvoiceType()==2?-(ci.getTotalAmount()+c.getRemainingAmount()):ci.getTotalAmount()+c.getRemainingAmount();
							 // payment = c.getInvoiceType() == 2? -ci.getTotalAmount() : ci.getTotalAmount();
							 // else
								  payment=  ci.getTotalAmount();
						
							body.append ( printAllignment(    ci.getInvoiceCode().length () > 10 ? ci.getInvoiceCode().substring ( 0 ,10 ) 
									: ci.getInvoiceCode()	 , 17-PAYMENT.length(), TOTAL_LENGTH) 
									+  printAllignment( cu.getCurrencySymbol() , 15-CURRENCY.length(), TOTAL_LENGTH)
									+  printAllignment(  moneyFormat.format ( payment  ).toString() , 15-AMOUNT.length() , TOTAL_LENGTH)+ NEWLINE) 
									;
							tot+=payment;
					}
					}
			}
			
			
			body.append (NEWLINE+HORIZANTAL_LINE+NEWLINE + " Collection Details:"+NEWLINE+HORIZANTAL_LINE+NEWLINE);
			body.append(" Payment Type    Currency   Amount"+NEWLINE +HORIZANTAL_LINE+NEWLINE  );
					for( int i = 0 ; i < collectionDetails.size () ; i ++ ) {				
						CollectionDetails tr = collectionDetails.get ( i );
						//body.append (NEWLINE + " Payment Methode:");
						currency = DatabaseUtils.getInstance(context).getDaoSession ().getCurrenciesDao ().queryBuilder ()
								.where( CurrenciesDao.Properties.CurrencyCode.eq( tr.getCurrencyCode() ) ).unique(); 
						if ( tr.getCollectionDetailType() == CollectionUtils.PaymentType.CASH ){			
							
							body.append ( printAllignment( CASH , 20-PAYMENT.length(), TOTAL_LENGTH) 
									+  printAllignment( currency.getCurrencySymbol() , 16-CURRENCY.length(), TOTAL_LENGTH)
									+  printAllignment( moneyFormat.format(tr.getCollectionAmount()).toString() , 16-AMOUNT.length() , TOTAL_LENGTH) 
									 );
						//	tot+=tr.getCollectionAmount();
							body.append ( NEWLINE );
						}
						else if ( tr.getCollectionDetailType() ==  CollectionUtils.PaymentType.CHECK ){
					//		String mdy = sdfa.format(tr.getCheckDate());
							Banks bank =  DatabaseUtils.getInstance(context).getDaoSession ().getBanksDao ().queryBuilder ()
									.where( BanksDao.Properties.BankCode.eq( tr.getBankCode() ) ).unique();
							body.append ( printAllignment( CHECK , 20-PAYMENT.length(), TOTAL_LENGTH) 
									+  printAllignment( currency.getCurrencySymbol() , 16-CURRENCY.length(), TOTAL_LENGTH)
									+  printAllignment( moneyFormat.format(tr.getCollectionAmount()).toString() , 16-AMOUNT.length() , TOTAL_LENGTH) 
									);
							body.append (NEWLINE);
							body.append( addSpace("", 2) + "-" 
									+ CHECK_NUM + tr.getCheckCode() + NEWLINE );
							body.append( addSpace("", 2) + "-" 
									+ BANK + bank.getBankDescription() + NEWLINE );
							body.append( addSpace("", 2) + "-" 
									+ "Date   : " + sdfa.format(tr.getCheckDate()) + NEWLINE );
							//body.append( addSpace("", 2) + "-" 		
							//		+ date + mdy );
							body.append ( NEWLINE );
						//	tot+=tr.getCollectionAmount();
						}
					}
					body.append ( NEWLINE + HORIZANTAL_LINE + NEWLINE );
				
					body.append ( COLLECTION_AMOUNT + moneyFormat.format (collectionHeaders.getTotalAmount()) + "  " + currency.getCurrencySymbol() );
		
					//body.append (EnglishNumberToWords.convert(collectionHeaders.getTotalAmount().longValue())+" "+  currency.getCurrencySymbol());
					//body.append ( TOTAL_PAYMENT + moneyFormat.format (collectionHeaders.getTotalAmount()) + "  " + currency.getCurrencySymbol() );
				}
		if (  type == Type.INVOICE  ) {
			body.append ( HORIZANTAL_LINE + NEWLINE );
			//subHeader.append ( OI_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
			body.append ( printAllignment(ITEM_NAME, 20-ITEM_NAME.length(), TOTAL_LENGTH) 
					+ printAllignment( QUANTITY, 5-QUANTITY.length() , TOTAL_LENGTH) 
					+ printAllignment(PRICE, 8-PRICE.length(), TOTAL_LENGTH) 
				 );
			body.append ( NEWLINE + HORIZANTAL_LINE + NEWLINE );
		}
		if ( type == Type.INVOICE  ) {
			
			List < LoadInSummaryPrinting > loadPrintingsO = new ArrayList < LoadInSummaryPrinting > ();
			List < LoadInSummaryPrinting > loadPrintingsF = new ArrayList < LoadInSummaryPrinting > ();
			for( int i = 0 ; i < loadPrintings.size () ; i ++ ) {
				
				LoadInSummaryPrinting tr = loadPrintings.get ( i );
				if(tr.getClientCode().equals( "O") )
					{
					loadPrintingsO.add(tr);
					
					}
				else{
					loadPrintingsF.add(tr);
				}
			}
				for( int i = 0 ; i < loadPrintingsO.size () ; i ++ ) {
				
				LoadInSummaryPrinting tr = loadPrintingsO.get ( i );
				
				body.append ( subString ( tr.getItemName () , TOTAL_LENGTH ) );
				body.append ( NEWLINE );
				body.append ( printAllignment( tr.getItemCode (), 22-ITEM_NAME.length(), TOTAL_LENGTH) 
						+  printAllignment( tr.getQuantityMedium().toString (), 5-QUANTITY.length() , TOTAL_LENGTH) 
						+  printAllignment(moneyFormat.format ( tr.getQuantitySmall() ).toString() , 8-PRICE.length(), TOTAL_LENGTH) 
						 );
			body.append ( NEWLINE );
		
			
			}
				if(loadPrintingsF.size()>0)
				body.append (NEWLINE+  HORIZANTAL_LINE + NEWLINE +"FREE:"+NEWLINE);
			for( int i = 0 ; i < loadPrintingsF.size () ; i ++ ) {
				
				LoadInSummaryPrinting tr = loadPrintingsF.get ( i );
				
				body.append ( subString ( tr.getItemName () , TOTAL_LENGTH ) );
				body.append ( NEWLINE );
				body.append ( printAllignment( tr.getItemCode (), 22-ITEM_NAME.length(), TOTAL_LENGTH) 
						+  printAllignment( tr.getQuantityMedium().toString (), 5-QUANTITY.length() , TOTAL_LENGTH) 
						+  printAllignment(moneyFormat.format ( tr.getQuantitySmall() ).toString() , 8-PRICE.length(), TOTAL_LENGTH) 
						 );
			body.append ( NEWLINE );
			
			
			}
			body.append (  HORIZANTAL_LINE + NEWLINE );
		}
		// Build the footer
		footer = new StringBuilder ();
		if ( type == Type.INVOICE ){ 
			total=loadPrintings.get ( 0 ).getQuantityMediumReturn() ;
			Double discountAount=loadPrintings.get ( 0 ).getFirstLoadQuantityMedium();
			Double grossAount=loadPrintings.get ( 0 ).getFirstLoadQuantitySmall(); 
			footer.append ( addSpace(" " + GROSS_AMOUNT , (TOTAL_LENGTH/2) - GROSS_AMOUNT.length() ) +  moneyFormat.format (  grossAount )+NEWLINE);
			footer.append ( addSpace(" " + DISCOUNT_AMOUNT , (TOTAL_LENGTH/2) - DISCOUNT_AMOUNT.length() ) +  moneyFormat.format (  discountAount )+NEWLINE);
			footer.append ( addSpace(" " + TOTAL_AMOUNT , (TOTAL_LENGTH/2) - TOTAL_AMOUNT.length() ) +  moneyFormat.format (  total )+NEWLINE);
			
			
			footer.append ( NEWLINE );
			footer.append ( NEWLINE );
			footer.append ( NEWLINE );
			footer.append ( NEWLINE );
			footer.append ( NEWLINE );
			footer.append ( NEWLINE );
		}
		//Jose-print
		if ( type == Type.SALESORDERSUMMARY ){
			footer.append ( addSpace(" " + TOTAL_AMOUNT , (TOTAL_LENGTH/2) - TOTAL_AMOUNT.length() ) +  moneyFormat.format (  total ));
			footer.append ( NEWLINE );
			footer.append ( NEWLINE );
			footer.append ( NEWLINE );
			footer.append ( NEWLINE );
			footer.append ( NEWLINE );
			footer.append ( NEWLINE );
		}
		else{
		if ( type == Type.RFR || type == Type.ORDER ) {
			if ( pages != null && currentPage != null && lastPage != null && currentPage != lastPage ) {
				footer.append ( PAGE + " " + ( currentPage + 1 ) );
			}
			else {
				footer.append ( GROSS_AMOUNT + moneyFormat.format ( transactionHeader.getGrossAmount () ) + " " + transactionHeader.getCurrencyCode () + NEWLINE );
				footer.append ( DISCOUNT_AMOUNT + moneyFormat.format ( transactionHeader.getDiscountAmount () ) + " " + transactionHeader.getCurrencyCode () + NEWLINE );
				footer.append ( TOTAL_AMOUNT + moneyFormat.format ( transactionHeader.getTotalTaxAmount() ) + " " + transactionHeader.getCurrencyCode () + NEWLINE );
				if ( pages != null && currentPage != null && lastPage != null && lastPage != 0 ) 
					footer.append ( PAGE + " " + ( currentPage + 1 ) );
			}
			footer.append ( NEWLINE );
			footer.append ( NEWLINE );
			footer.append ( NEWLINE );
			footer.append ( NEWLINE );
			footer.append ( NEWLINE );
			footer.append ( NEWLINE );
		} // End if
		}//  End else
		

		
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
			         if( tr.getClientType() == 1 )
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
 
		   
			
			
			}
			}
		     
 

		   if( collectionHeadersCashCash.size() > 0 ) {
			   body.append (  " Cash Collection"+NEWLINE);
		 
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
     		
 		 
	 
 			 body.append ( "Collection #:"+tr.getCollectionCode() + NEWLINE);
		 	 
		 	body.append ("    -Client:" 	);
		 	body.append ( tr.getClientCode().length () >10 ? tr.getClientCode().substring ( 0 , 10 ) 
		 			 : tr.getClientCode () );
			body.append ("   ");
			body.append ((clientName.length () >20 ? clientName.substring ( 0 , 20 ) : clientName	)+NEWLINE);
		 	body.append ("    -Total Amount:" );
			body.append (moneyFormat.format( tr.getTotalAmount()) +NEWLINE);
		 	body.append ("    -Address:");
			body.append ( (  clientAddress.length () >18 ? clientAddress.substring ( 0 , 18 ) : clientAddress) + NEWLINE );
		 	totalCash = totalCash+tr.getTotalAmount();
		 	tcash +=tr.getTotalAmount();
		 
					 
		  }
			//count = count+1;
		
	} // End for loop
		   body.append ( HORIZANTAL_LINE + NEWLINE );
		   body.append ( "        Total Cash Collection  "+moneyFormat.format(totalCash)  +  NEWLINE);
		   body.append ( HORIZANTAL_LINE + NEWLINE );
		   }
  
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
     		
 			     

		 	 body.append ( "Collection #:"+tr.getCollectionCode() + NEWLINE);
		 	 
		 	body.append ("    -Client:" 	);
		 	body.append ( tr.getClientCode().length () >10 ? tr.getClientCode().substring ( 0 , 10 ) 
		 			 : tr.getClientCode () );
			body.append ("   ");
			body.append ((clientName.length () >20 ? clientName.substring ( 0 , 20 ) : clientName	)+NEWLINE);
		 	body.append ("    -Total Amount:" );
			body.append (moneyFormat.format( tr.getTotalAmount())+NEWLINE );
		 	body.append ("    -Address:");
			body.append ( (  clientAddress.length () >18 ? clientAddress.substring ( 0 , 18 ) : clientAddress) + NEWLINE );
		 	totalcred = totalcred + tr.getTotalAmount();
	 
		 
		 
		
	} // End for loop
		   body.append ( HORIZANTAL_LINE + NEWLINE );
		   body.append ( "        Total Credit Collection  "+moneyFormat.format(totalcred)  +  NEWLINE);
		   body.append ( HORIZANTAL_LINE + NEWLINE );
		   }
		   
		   
		  if(collectionHeadersCheck.size()>0){
			   //body.append ( HORIZANTAL_LINE + NEWLINE );
			   
		    	
			   body.append ("    Check   " +NEWLINE);
		 
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
				} 				//  clientAddress=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
					//            .where ( ClientsDao.Properties.ParentCode.eq (  tr.getClientCode()  ), ClientsDao.Properties.CompanyCode.eq (  tr.getCompanyCode()) ).unique().getClientAddress();
 		
 				break;
 			case 2:
 		
 				 clientName=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
			            .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  ), ClientsDao.Properties.CompanyCode.eq (  tr.getCompanyCode()) ).unique().getClientName();
 		 		
 				break;
 			}
  

 			 body.append ( "Collection #:"+tr.getCollectionCode() + NEWLINE);
		 	 
 		 	body.append ("    -Client:" 	);
 		 	body.append ( tr.getClientCode().length () >10 ? tr.getClientCode().substring ( 0 , 10 ) 
 		 			 : tr.getClientCode () );
 			body.append ("   ");
 			body.append ((clientName.length () >20 ? clientName.substring ( 0 , 20 ) : clientName	)+NEWLINE);
 		 	body.append ("    -Total Amount:" );
 			body.append (moneyFormat.format( tr.getTotalAmount()) +NEWLINE);
 		 	body.append ("    -Address:");
			body.append ( (  clientAddress.length () >10 ? clientAddress.substring ( 0 , 10 ) : clientAddress) + NEWLINE );
 			body.append ("    -Collection Date:");
 			body.append ( 	 tr.getCollectionDate().toString().length () >16 ? tr.getCollectionDate().toString().substring ( 0 , 16 ) : tr.getCollectionDate () + NEWLINE);
 			body.append ("    -Check Code:");	   
 			body.append ( (tr.getCheckCode().length () >18 ? tr.getCheckCode().substring ( 0 , 18 ) : tr.getCheckCode ()) + NEWLINE);
 			body.append ("    -Bank Name:");		 
 			body.append ((tr.getBankName().length () >18 ? tr.getBankName().substring ( 0 ,18 ) : tr.getBankName ()) + NEWLINE);
 			
		 	
			totalCheck = totalCheck + tr.getCollectionAmount();
	 
	         	
		
	} // End for loop
		   body.append ( HORIZANTAL_LINE + NEWLINE );
		   body.append ( "          Total Check "+moneyFormat.format(totalCheck)  +  NEWLINE);
		   body.append ( HORIZANTAL_LINE + NEWLINE );
}// End if
		  
		   
		   
		   
		   
		   
		   
		   
		   
		   if(summaryReturnPrintingCash.size() > 0){
		 
		 
			   body.append ("      Return Cash   " +NEWLINE);
			   
			 
		   for( int i = 0 ; i < summaryReturnPrintingCash.size () ; i ++ ) {
			  SummaryReturnPrinting	    tr = summaryReturnPrintingCash.get ( i );
			
//		     String currencyName= DatabaseUtils.getInstance ( context ).getDaoSession ().getCurrenciesDao().queryBuilder ()
//				 .where ( CurrenciesDao.Properties.CurrencyCode.eq (  tr.getCurrencyCode()   ) ).unique().getCurrencyName() ;
//		  	 
			   
 			String clientAddress=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
				             .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  )  ).unique().getClientAddress();
 			body.append ( "Collection #:"+tr.getTransactionCode() + NEWLINE);
		 	 
  		 	body.append ("    -Client:" 	);
  		 	body.append ( tr.getClientCode().length () >10 ? tr.getClientCode().substring ( 0 , 10 ) 
  		 			 : tr.getClientCode () );
  			body.append ("   ");
  			body.append ((tr.getClientName().length () >20 ? tr.getClientName().substring ( 0 , 20 ) : tr.getClientName().length ()	)+NEWLINE);
  		 	body.append ("    -Total Amount:" );
  			body.append (moneyFormat.format( tr.getTotalTaxAmount()) + NEWLINE);
  		 	body.append ("    -Address:");
  			body.append (  ( clientAddress.length () >16 ? clientAddress.substring ( 0 , 16 ) : clientAddress)  + NEWLINE );
  			 
					       	 
						    
			 	totalReturnCash = totalReturnCash + tr.getTotalTaxAmount();
		 
 	// count=count+1;
		
	} // End for loop

		   body.append ( HORIZANTAL_LINE + NEWLINE );
		   body.append ( "          Total Return Cash "+moneyFormat.format( totalReturnCash )  +  NEWLINE);
		   body.append ( HORIZANTAL_LINE + NEWLINE );
		   }// End if  
		   
		   
		   
		   if (! PermissionsUtils.getRemoveCreditCollectionPrint ( context ,   DatabaseUtils.getCurrentUserCode ( context ) ,  DatabaseUtils.getCurrentCompanyCode(context) ) ) 
				 
     		  
		   if(summaryReturnPrintingCredit.size() > 0){
			 
		    	
			   body.append ("      Return Credit   " +NEWLINE);
			   
		 
		   for( int i = 0 ; i < summaryReturnPrintingCredit.size () ; i ++ ) {
			  SummaryReturnPrinting	    tr = summaryReturnPrintingCredit.get ( i );
			   
			String clientAddress=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
			             .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  )  ).unique().getClientAddress();
		
 	 			 
			body.append ( "Collection #:"+tr.getTransactionCode() + NEWLINE);
		 	 
  		 	body.append ("    -Client:" 	);
  		 	body.append ( tr.getClientCode().length () >10 ? tr.getClientCode().substring ( 0 , 10 ) 
  		 			 : tr.getClientCode () );
  			body.append ("   ");
  			body.append ((tr.getClientName().length () >20 ? tr.getClientName().substring ( 0 , 20 ) : tr.getClientName().length ()	)+NEWLINE);
  		 	body.append ("    -Total Amount:" );
  			body.append (moneyFormat.format( tr.getTotalTaxAmount()) +NEWLINE);
  		 	body.append ("    -Address:");
  			body.append (  ( clientAddress.length () >16 ? clientAddress.substring ( 0 , 16 ) : clientAddress ) + NEWLINE );
  			 
					       	 
						    
			 	totalReturnCredit = totalReturnCredit + tr.getTotalTaxAmount();
	 
 
	} // End for loop

		   body.append ( HORIZANTAL_LINE + NEWLINE );
		   body.append ( "          Total Return Credit "+moneyFormat.format( totalReturnCredit )  +  NEWLINE);
		   body.append ( HORIZANTAL_LINE + NEWLINE );
		   }// End if  
	 
		   
		   if ( PermissionsUtils.getRemoveCreditCollectionPrint ( context ,   DatabaseUtils.getCurrentUserCode ( context ) ,  DatabaseUtils.getCurrentCompanyCode(context) ) ) {
				 
 		 
 		   body.append ( "Net Collection Amount:"+ moneyFormat.format((tcredit+totalCash +totalCheck + totalcred -totalReturnCash ))+ NEWLINE );
 			  
 		   if(summaryReturnPrintingCredit.size() > 0){
 			   //body.append ( HORIZANTAL_LINE + NEWLINE );
 			 
			    	
 			   body.append ("      Return Credit   " +NEWLINE);
 			   
 			  // body.append ( HORIZANTAL_LINE + NEWLINE );
 		 
 		   for( int i = 0 ; i < summaryReturnPrintingCredit.size () ; i ++ ) {
 			  SummaryReturnPrinting	    tr = summaryReturnPrintingCredit.get ( i );
 			   
 			String clientAddress=DatabaseUtils.getInstance ( context ).getDaoSession ().getClientsDao().queryBuilder ()
				             .where ( ClientsDao.Properties.ClientCode.eq (  tr.getClientCode()  )  ).unique().getClientAddress();
 		
 			body.append ( "Collection Code:"+tr.getTransactionCode() + NEWLINE);
		 	 
  		 	body.append ("    -Client:" 	);
  		 	body.append ( tr.getClientCode().length () >10 ? tr.getClientCode().substring ( 0 , 10 ) 
  		 			 : tr.getClientCode () );
  			body.append ("   ");
  			body.append ((tr.getClientName().length () >20 ? tr.getClientName().substring ( 0 , 20 ) : tr.getClientName().length ())	+NEWLINE);
  		 	body.append ("    -Total Amount:" );
  			body.append (moneyFormat.format( tr.getTotalTaxAmount())  + NEWLINE);
  		 	body.append ("    -Address:");
  			body.append ( (  clientAddress.length () >16 ? clientAddress.substring ( 0 , 16 ) : clientAddress)  + NEWLINE );
  			totalReturnCredit = totalReturnCredit + tr.getTotalTaxAmount();
		 

		         //	 count=count+1;
			
		} // End for loop
    
 		   body.append ( HORIZANTAL_LINE + NEWLINE );
 		   body.append ( "          Total Return Credit "+moneyFormat.format( totalReturnCredit )  +  NEWLINE);
 	 
 		   }// End if  
 		   
 		   }
				else
				{ 
					body.append ( "Net Collection Amount:"+ moneyFormat.format((tcredit+totalCash +totalCheck + totalcred -totalReturnCash -totalReturnCredit))+ NEWLINE );
       		
					
				}  
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
	//Jose-print
	/*
	 * Adds empty space at the end of a string
	 * 
	 * @param string string to format
	 * @param count  integer used to count the empty space to be added
	 * @return the formatted string
	 */
	private String addSpace (String string, int count){
		for (int i=0; i< count ; i++)
			string+=" ";
		return string;
	}
	/*
	 * Create an alignment
	 * 
	 * @param string string to align
	 * @param count integer used to create the blank space for the alignment
	 * @param maxlength integer used to determine the maximum length of the alignment
	 * @return the formatted string
	 */
	private String printAllignment (String string, int count, int maxlength){
		string = addSpace(string, count); 
		return subString(string, maxlength);
	}
	//end Jose-print
}