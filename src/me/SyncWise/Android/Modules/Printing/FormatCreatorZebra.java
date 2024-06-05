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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.SyncWise.Android.Database.ClientItemCodes;
import me.SyncWise.Android.Database.ClientItemCodesDao;
import me.SyncWise.Android.Database.ClientMouvementStock;
import me.SyncWise.Android.Database.ClientMouvementStockDao;
import me.SyncWise.Android.Database.ClientSellingSuggestion;
import me.SyncWise.Android.Database.ClientSellingSuggestionDao;
import me.SyncWise.Android.Database.Clients;
import me.SyncWise.Android.Database.ClientsDao;
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
import me.SyncWise.Android.Database.Users;
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Modules.Printing.PrintingActivity.Type;
import android.content.Context;

public class FormatCreatorZebra implements FormatCreator {
	
	public final int TOTAL_LENGTH = 48;
	public final int CODE_LENGTH = 12;
	public final int QUANTITY_LENGTH = 10;
	public final int UNIT_PRICE_LENGTH = 10;
	public final int LINE_AMOUNT_LENGTH = 13;
	public final int BARCODE_LENGTH = 16;
	public final int NINE = 8;
	
	private Companies  company;
	private TransactionHeaders transactionHeader;
	private ArrayList<TransactionHeaders> transactionHeaders;
	private ArrayList<TransactionDetails> transactionDetails;
	private Clients client;
	private Currencies currency;
	
	private double total=0;
	
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
		public FormatCreatorZebra ( Context context , ArrayList < TransactionHeaders> transactionHeaderss, int type , int printout , Integer pages , Integer currentPage , Integer lastPage) {

				this.transactionHeaders = transactionHeaderss;

				this.context = context;
				formatString = Creator ( type , 0 , pages , currentPage , lastPage );		
		}
	//end 	Jose-print
		
	public FormatCreatorZebra ( Context context , TransactionHeaders transactionHeaders , int type , int printout , Integer pages , Integer currentPage , Integer lastPage ) {
		this.transactionHeader = transactionHeaders;
		this.context = context;
		
		if(type==Type.ORDER &&PermissionsUtils.getDisplaySequenceChange ( context , DatabaseUtils.getCurrentUserCode(context) , DatabaseUtils.getCurrentCompanyCode(context ))){
			transactionDetails = (ArrayList<TransactionDetails>) DatabaseUtils.getInstance ( context ).getDaoSession ().getTransactionDetailsDao ().queryBuilder ().orderDesc(TransactionDetailsDao.Properties.OrderedType)
					.where ( TransactionDetailsDao.Properties.TransactionCode.eq ( transactionHeaders.getTransactionCode () ) ).orderAsc(TransactionDetailsDao.Properties.LineNote).list ();

		}else
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
		subHeader.append ( CLIENT_ID + client.getClientCode () + NEWLINE );
		subHeader.append ( CLIENT_NAME + subString ( client.getClientName () , TOTAL_LENGTH - CLIENT_NAME.length () ) + NEWLINE );
		subHeader.append ( CLIENT_ADDRESS + subString ( client.getClientAddress () , TOTAL_LENGTH - CLIENT_NAME.length () ) + NEWLINE );
		if ( type == Type.RFR || type == Type.ORDER ) {
			subHeader.append ( SALES_MAN + user.getUserName () + NEWLINE );
			subHeader.append ( ISSUE_DATE + transactionHeader.getIssueDate () + NEWLINE );
		}
		subHeader.append ( PRINT_DATE + now.getTime () + NEWLINE );
		if ( type == Type.RFR || type == Type.ORDER ) {
			subHeader.append ( HORIZANTAL_LINE + NEWLINE );
			subHeader.append ( OI_DETIAL_HEADER + NEWLINE + HORIZANTAL_LINE + NEWLINE );
		}
		}//end else
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
		// Build the footer
		footer = new StringBuilder ();
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