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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppDialog.ButtonsType;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.ClientStockCountHeaders;
import me.SyncWise.Android.Database.CollectionHeaders;
import me.SyncWise.Android.Database.CycleCalls;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.Items;
import me.SyncWise.Android.Database.MovementDetails;
import me.SyncWise.Android.Database.MovementHeaders;
import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Database.Printers;
import me.SyncWise.Android.Database.PrintersDao;
import me.SyncWise.Android.Database.TransactionDetailsDao;
import me.SyncWise.Android.Database.TransactionHeaders;

import me.SyncWise.Android.Widgets.Baguette;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.widget.EditText;
import datamaxoneil.connection.Connection_Bluetooth;

/**
 * Printing activity used to print copies in the background.
 * @author Elias - Michel - Ahmad
 *
 */
public class PrintingActivity extends Activity implements Runnable {
	
	/**
	 * Helper class used to define the various types of printed copies.
	 * 
	 * @author Elias
	 *
	 */
	public static class Type {
		public final static int INVOICE = 0; // tested
		public final static int ORDER = 1; // tested
		public final static int MOVEMENTLoadTransfer = 2; // TODO
		public final static int MOVEMENTLoadRequest = 3; // tested
		public final static int RECEIPT = 4; // tested
		public final static int LOAD = 5; // tested
		public final static int STOCKRECONCILIATION = 6; // TODO
		public final static int MOVEMENTS=7; // TODO
		public final static int VEHICULESSTOCK=8; // TODO
		public final static int APPROVERETURN=9; // TODO
		public final static int CLIENTSTOCKCOUNT=10; // TODO
		public final static int COLLECTIONSUMMARY=11; // TODO
		public final static int INVOICESUMMARY=12; // TODO
		public final static int MISSEDITEMS=13; // TODO
		public final static int ITEMDISTRIBUTION=14; // TODO
		public final static int CLIENTASSET=15; // TODO
		public final static int PLANVISITSREPORT=16; // TODO
		public final static int RFR = 17; // TODO
		public final static int UNLOAD=18; // tested
		public final static int UNLOADREQUEST=19; // tested
		public final static int VANMANAGMENT=20; // TODO
		public final static int VANSTOCKCOUNT=21; // TODO
		public final static int COLLECTIONISSETTEL=22;
		public final static int VANTRANSFER=23;
		public final static int RETURN=24;
		public final static int DirectReturn=25;
		public final static int FreshQQP=26;
		public final static int DirectLoad = 27;
		public final static int DirectUnload = 28;
		public final static int loadInSummary=29;
		public final static int SalesReturnSummeryReport=30; 
		public final static int FreeSummeryReport=31;
		public final static int RETURNCashCredit=32;
		
		public final static int MovementInSummary=33;
		
		public final static int SALESORDERSUMMARY = 34;
		
		public final static int INVOICE_DISCOUNT = 35;
	}
	
	/**
	 * Helper class used to define the various types of printouts.
	 * 
	 * @author Elias
	 *
	 */
	public static class Printout {
		public final static int ORIGINAL = 0;
		public final static int COPY = 1;
	}
	
	/**
	 * String used to host the printer bluetooth address.
	 */
	private String m_printerBluetoothAddr = null;
	
	private Integer pages;
	
	private final int FIVE = 5000;
	/**
	 * Object used to initiate a bluetooth connection.
	 */
	Connection_Bluetooth connBT = null;
	
    BluetoothAdapter mBluetoothAdapter1;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
 
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
 
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;

	
	
	public static final String HEADER = PrintingActivity.class.getName () + ".HEADER";
	public static final String TYPE = PrintingActivity.class.getName () + ".TYPE";
	public static final String PRINTOUT = PrintingActivity.class.getName () + ".PRINTOUT";
	public static final String DETAILS = PrintingActivity.class.getName () + ".DETAILS";
    
    // Key names received from the BluetoothChatService Handler
	private static final int REQUEST_ENABLE_BT = 0;
	
	//Data properties
	private HashMap<String, String> deviceAddress;
	private String m_activeDeviceAddr;
	private ArrayList < FormatCreator > fc;
	private BluetoothAdapter mBluetoothAdapter;
	private Integer printerType ;
	
	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate ( Bundle savedInstanceState ) {
		// Superclass method call
		super.onCreate ( savedInstanceState );
		// Set the activity content from a layout resource.
		setContentView ( R.layout.printing_layout );
		// Get a handle to the default local Bluetooth adapter
	  //	mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

	 	findBT();
	 	//checked();
		// Check if the bluetooth adapter is enabled
		if ( ! mBluetoothAdapter1.isEnabled () ) {
			// Warn the user
			Baguette.showText ( this , "BlueTooth Adapter Disabled ... \n Enabling it Now... " , Baguette.BackgroundColor.RED );
			// Create an intent to enable bluetooth
			Intent intent = new Intent ( BluetoothAdapter.ACTION_REQUEST_ENABLE );
			// Start the settings page
            startActivityForResult ( intent , REQUEST_ENABLE_BT );
		} // End if
		else
		{
			/*restartBT(FIVE);
			new Thread(new Runnable() {
			    @Override
			    public void run() {
			        try {
			            Thread.sleep(FIVE);
			        } catch (InterruptedException e) {
			            e.printStackTrace();
			        }
			        runOnUiThread(new Runnable() {
			            @Override
			            public void run() {
			            	
			            	AppDialog.getInstance().displayAlert(PrintingActivity.this ,
									null ,
									AppResources.getInstance ( PrintingActivity.this ).getString ( PrintingActivity.this , R.string.printing_activity_turn_printer_on_message ) ,
									AppDialog.ButtonsType.OK ,
									new DialogInterface.OnClickListener () {
										@Override
										public void onClick ( DialogInterface dialog , int which ) {
											// Determine the clicked button
											switch ( which ) {
											case DialogInterface.BUTTON_NEUTRAL:
												// Select the appropriate printer
												check();
												//finish ();
												break;
											} // End switch
										}
									} );
			            	
			            }
			        });
			    }
			}).start();*/
			AppDialog.getInstance().displayAlert(PrintingActivity.this ,
					null ,
					AppResources.getInstance ( PrintingActivity.this ).getString ( PrintingActivity.this , R.string.printing_activity_turn_printer_on_message ) ,
					AppDialog.ButtonsType.OK ,
					new DialogInterface.OnClickListener () {
						@Override
						public void onClick ( DialogInterface dialog , int which ) {
							// Determine the clicked button
							switch ( which ) {
							case DialogInterface.BUTTON_NEUTRAL:
								// Select the appropriate printer
								
								check();
								//finish ();
								break;
							} // End switch
						}
					} );
//			AppDialog.getInstance().displayAlert(PrintingActivity.this ,
//					null ,
//					AppResources.getInstance ( PrintingActivity.this ).getString ( PrintingActivity.this , R.string.printing_activity_turn_printer_on_message ) ,
//					AppDialog.ButtonsType.OK ,
//					new DialogInterface.OnClickListener () {
//						@Override
//						public void onClick ( DialogInterface dialog , int which ) {
//							// Determine the clicked button
//							switch ( which ) {
//							case DialogInterface.BUTTON_NEUTRAL:
//								// Select the appropriate printer
//								
//								check();
//								//finish ();
//								break;
//							} // End switch
//						}
//					} );
	
	}
	}

	/**
	 * Prompts the user for a paging.
	 */
	private void promptPages () {
		// Retrieve the previous quantity
		int oldQuantity = 15;
		// Declare and initialize an alert dialog builder
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder ( this );
		// By default, the alert dialog cannot be cancelled UNLESS it has no buttons
		alertDialogBuilder.setCancelable ( false );
		// Create view
		final EditText editText = new EditText ( this );
		editText.setId ( 1 );
		editText.setInputType ( InputType.TYPE_CLASS_NUMBER );
		editText.setText ( oldQuantity <= 0 ? "" : String.valueOf ( oldQuantity ) );
		// Create click listener
		DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener () {
			@Override
			public void onClick ( DialogInterface dialog , int which ) {
				// Determine the clicked button
				switch ( which ) {
				case DialogInterface.BUTTON_POSITIVE:
					int newQuantity = 0;
					try {
						newQuantity = Integer.parseInt ( ( (EditText) ( (AlertDialog) dialog ).findViewById ( 1 ) ).getText ().toString ().trim () );
					} catch ( Exception exception ) {
						newQuantity = 0;
					}
					pages = newQuantity;
					// Select the appropriate printer
					check ();
				case DialogInterface.BUTTON_NEGATIVE:
					// Finish activity
					finish ();
					break;
				} // End switch
			}
		};
		// Setup dialog
		alertDialogBuilder.setTitle ( "Details per page :" );
		alertDialogBuilder.setView ( editText );
		alertDialogBuilder.setPositiveButton ( AppResources.getInstance ( this ).getString ( this , R.string.ok_label ) , onClickListener );
		alertDialogBuilder.setNegativeButton ( AppResources.getInstance ( this ).getString ( this , R.string.cancel_label ) , onClickListener );
		alertDialogBuilder.create ();
		alertDialogBuilder.show ();
	}
	public void checked(){
		if ( ! mBluetoothAdapter1.isEnabled () ) {
			// Bluetooth is disabled
			// Exit activity
			finish();
			// Exit method
			return;
		}
		// Retrieve a list of the paired devices via bluetooth
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter1.getBondedDevices ();
	
		// Get the MAC addresses
		List < String > pairedAddresses = new ArrayList < String > ();
		for ( BluetoothDevice device : pairedDevices )
			// Store the device's MAC address
			pairedAddresses.add ( device.getAddress ().toLowerCase () );
		if(pairedAddresses.isEmpty()){
			AppDialog.getInstance ().displayAlert ( this ,
					AppResources.getInstance ( this ).getString ( this , R.string.error_label ),
					AppResources.getInstance ( this ).getString ( this , R.string.printing_activity_no_pairing_printers_message ) ,
					ButtonsType.OK , new DialogInterface.OnClickListener () {
						@Override
						public void onClick ( DialogInterface dialog , int which ) {
							// Finish activity
							finish ();
						}
					} );
			// Exit method
			return;
		}// Warn the user to turn on the printer
	
		
		// Retrieve the user's printers
		List < Printers > printers = DatabaseUtils.getInstance ( this ).getDaoSession ().getPrintersDao ().queryBuilder ()
				.where ( PrintersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( this ) ) ,
						PrintersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( this ) ) ).list ();
		
		// Make sure there is at least one printer
		if ( printers.isEmpty () ) {
			// Warn the user
			AppDialog.getInstance ().displayAlert ( this ,
					AppResources.getInstance ( this ).getString ( this , R.string.error_label ),
					AppResources.getInstance ( this ).getString ( this , R.string.printing_activity_no_user_printers_message ) ,
					ButtonsType.OK , new DialogInterface.OnClickListener () {
						@Override
						public void onClick ( DialogInterface dialog , int which ) {
							// Finish activity
							finish ();
						}
					} );
			// Exit method
			return;
		} // End if
		// Search for linked printer that is paired
		// Iterate over the linked printers
		for ( Printers printer : printers ) {
			// Make sure the MAC address is well formatted
			if ( ! printer.getPrinterMAC ().contains ( ":" ) ) {
				String mac = "";
				// Iterate over the characters
				for ( int i = 0 ; i < printer.getPrinterMAC ().length () ; i = i + 2 ) 
					// Properly format the MAC address
					mac += printer.getPrinterMAC ().substring ( i , i + 2 ) + ":";
				// Set the properly formatted address
				printer.setPrinterMAC ( mac.substring ( 0 , mac.length () - 1 ) );
			} // End if
			// Check if the current printer is paired
			if ( pairedAddresses.contains ( printer.getPrinterMAC ().toLowerCase () ) ) {
				// Select the current printer 
				m_printerBluetoothAddr = printer.getPrinterMAC ();
				printerType = printer.getType ();
				// Exit loop
				break;
			} // End if
		} // End for each
		// Make sure the selected printer MAC address is valid
		if ( m_printerBluetoothAddr == null ) {
		
//			AppDialog.getInstance().displayAlert(PrintingActivity.this ,
//					AppResources.getInstance ( this ).getString ( this , R.string.error_label ) , 
//					AppResources.getInstance ( PrintingActivity.this ).getString ( PrintingActivity.this , R.string.printing_activity_not_connected_message ) ,
//					AppDialog.ButtonsType.OK ,
//					new DialogInterface.OnClickListener () {
//						@Override
//						public void onClick ( DialogInterface dialog , int which ) {
//							// Determine the clicked button
//							switch ( which ) {
//							case DialogInterface.BUTTON_NEUTRAL:
								// Select the appropriate printer
								
								finish ();
								//finish ();
//								break;
//							} // End switch
//						}
//					} );
		
//			// Warn the user
//	 		AppDialog.getInstance ().displayAlert ( this ,
//					AppResources.getInstance ( this ).getString ( this , R.string.error_label ),
//					AppResources.getInstance ( this ).getString ( this , R.string.printing_activity_not_connected_message ) ,
//					ButtonsType.OK , new DialogInterface.OnClickListener () {
//						@Override
//						public void onClick ( DialogInterface dialog , int which ) {
//							// Finish activity
//							finish ();
//						}
//					} );
		//	 Exit method
		 	return;
		} // End if
	
		if(printerType == null){
			// Warn the user
			AppDialog.getInstance ().displayAlert ( this ,
					AppResources.getInstance ( this ).getString ( this , R.string.error_label ),
					AppResources.getInstance ( this ).getString ( this , R.string.printing_activity_no_pairing_printers_message ) ,
					ButtonsType.OK , new DialogInterface.OnClickListener () {
						@Override
						public void onClick ( DialogInterface dialog , int which ) {
							// Finish activity
							finish ();
						}
					} );
			// Exit method
		 	return;
		}
	}
	/**
	 * Checks and selects the appropriate printer before firing a parallel thread.
	 */
	@SuppressLint("DefaultLocale") 
	@SuppressWarnings("unchecked")
	public void check () {
	//	checked();
		if ( ! mBluetoothAdapter1.isEnabled () ) {
			// Bluetooth is disabled
			// Exit activity
			finish();
			// Exit method
			return;
		}
		// Retrieve a list of the paired devices via bluetooth
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter1.getBondedDevices ();
	
		// Get the MAC addresses
		List < String > pairedAddresses = new ArrayList < String > ();
		for ( BluetoothDevice device : pairedDevices )
			// Store the device's MAC address
		//as
				pairedAddresses.add ( device.getAddress ().toLowerCase () );
    	//	pairedAddresses.add ( device.getName().toLowerCase () );
		if(pairedAddresses.isEmpty()){
			AppDialog.getInstance ().displayAlert ( this ,
					AppResources.getInstance ( this ).getString ( this , R.string.error_label ),
					AppResources.getInstance ( this ).getString ( this , R.string.printing_activity_no_pairing_printers_message ) ,
					ButtonsType.OK , new DialogInterface.OnClickListener () {
						@Override
						public void onClick ( DialogInterface dialog , int which ) {
							// Finish activity
							finish ();
						}
					} );
			// Exit method
			return;
		}// Warn the user to turn on the printer
	
		
		// Retrieve the user's printers
		List < Printers > printers = DatabaseUtils.getInstance ( this ).getDaoSession ().getPrintersDao ().queryBuilder ()
				.where ( PrintersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( this ) ) ,
						PrintersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( this ) ) ).list ();
// 		printerType=2;
//Printers py = new Printers(null, "AC:3F:A4:19:CE:C7", DatabaseUtils.getCurrentUserCode(this), DatabaseUtils.getCurrentCompanyCode(this), 1, null);
//printers.add(py);
//for(Printers p:printers)
//			p.setPrinterMAC("00:16:A4:43:13:42");
		// Make sure there is at least one printer
		if ( printers.isEmpty () ) {
			// Warn the user
			AppDialog.getInstance ().displayAlert ( this ,
					AppResources.getInstance ( this ).getString ( this , R.string.error_label ),
					AppResources.getInstance ( this ).getString ( this , R.string.printing_activity_no_user_printers_message ) ,
					ButtonsType.OK , new DialogInterface.OnClickListener () {
						@Override
						public void onClick ( DialogInterface dialog , int which ) {
							// Finish activity
							finish ();
						}
					} );
			// Exit method
			return;
		} // End if
		// Search for linked printer that is paired
		// Iterate over the linked printers
		for ( Printers printer : printers ) {
			// Make sure the MAC address is well formatted
			
			if ( ! printer.getPrinterMAC ().contains ( ":" ) ) {
				String mac = "";
				// Iterate over the characters
//aa
				for ( int i = 0 ; i < printer.getPrinterMAC ().length () ; i = i + 2 ) 
					// Properly format the MAC address
					mac += printer.getPrinterMAC ().substring ( i , i + 2 ) + ":";
				// Set the properly formatted address
				printer.setPrinterMAC ( mac.substring ( 0 , mac.length () - 1 ) );
			} // End if
			// Check if the current printer is paired
			if ( pairedAddresses.contains ( printer.getPrinterMAC ().toLowerCase () ) ) {
				// Select the current printer 
				m_printerBluetoothAddr = printer.getPrinterMAC ();
				printerType = printer.getType ();
				// Exit loop
				break;
			} // End if
		} // End for each
		// Make sure the selected printer MAC address is valid
		if ( m_printerBluetoothAddr == null ) {
		
			
			// Warn the user
	 		AppDialog.getInstance ().displayAlert ( this ,
					AppResources.getInstance ( this ).getString ( this , R.string.error_label ),
					AppResources.getInstance ( this ).getString ( this , R.string.printing_activity_not_connected_message ) ,
					ButtonsType.OK , new DialogInterface.OnClickListener () {
						@Override
						public void onClick ( DialogInterface dialog , int which ) {
							// Finish activity
							finish ();
						}
					} );
		//	 Exit method
		 	return;
		} // End if

		if(printerType == null){
			// Warn the user
			AppDialog.getInstance ().displayAlert ( this ,
					AppResources.getInstance ( this ).getString ( this , R.string.error_label ),
					AppResources.getInstance ( this ).getString ( this , R.string.printing_activity_no_pairing_printers_message ) ,
					ButtonsType.OK , new DialogInterface.OnClickListener () {
						@Override
						public void onClick ( DialogInterface dialog , int which ) {
							// Finish activity
							finish ();
						}
					} );
			// Exit method
		 	return;
		}
	   
		// Declare and initialize a list used to host the lists to print
		ArrayList < TransactionHeaders > transactionHeaders = null;
		ArrayList < MovementHeaders > movementHeaders = null;
		ArrayList < MovementDetails > movementDetails = null;
		ArrayList < CollectionHeaders > collectionHeaders = null;
		ArrayList< CollectionSummaryPrinting > collectionSummaryPrinting=null;
		ArrayList<SummaryReturnPrinting>summaryReturnPrinting=null;
		ArrayList < ClientStockCountHeaders > clientStockCountHeaders = null;
		ArrayList < vehiclesStockPrinting > vehiclesStock = null;
		ArrayList < MissedItems > missedItems = null;
		ArrayList< Items > items = null;
		ArrayList< ItemDistrbutionPrinting > itemDistrbutionPrinting = null;
		ArrayList< ClientAssetPrinting > clientAssetPrinting = null;
		ArrayList< CycleCalls > cycleCalls = null;
		ArrayList<LoadInSummaryPrinting> loadInSummaryPrinting = null;
		String userCode = DatabaseUtils.getCurrentUserCode ( PrintingActivity.this );
		String companyCode = DatabaseUtils.getCurrentCompanyCode ( PrintingActivity.this );

		// Initialize the format creator to format the printout content
		fc = new ArrayList < FormatCreator > ();
		// Retrieve the print out type
		int printoutType = getIntent ().getIntExtra ( PRINTOUT , Printout.COPY );
		try {
			// Determine the content type
			switch ( getIntent ().getIntExtra ( TYPE , - 1 ) ) {
			case Type.RFR:
				// Retrieve the request for return headers
				transactionHeaders = (ArrayList < TransactionHeaders >) getIntent ().getSerializableExtra ( HEADER );
				// Make sure there is at least one valid transaction
				if ( transactionHeaders == null || transactionHeaders.isEmpty () )
					// No valid transactions
					return;
				// Iterate over every header
				for ( TransactionHeaders transactionHeader : transactionHeaders )
					// Check type
					if ( printerType.equals ( 1 ) )
						// Create a format creator for the current header
						fc.add ( new FormatCreatorZebra ( PrintingActivity.this , transactionHeader , Type.RFR , printoutType , null , null , null ) );
					else if ( printerType.equals ( 2 ) )
						// Not support on intermec
						throw new UnsupportedOperationException ();
						// Create a format creator for the current header
					//	fc.add ( new FormatCreatorIntermec ( PrintingActivity.this , transactionHeader , Type.ORDER , printoutType ) );
				break;
			case Type.SALESORDERSUMMARY:
				// Retrieve the request for return headers
				transactionHeaders = (ArrayList < TransactionHeaders >) getIntent ().getSerializableExtra ( HEADER );
				// Make sure there is at least one valid transaction
				if ( transactionHeaders == null || transactionHeaders.isEmpty () )
					// No valid transactions
					return;
				// Check type
				if ( printerType.equals ( 1 ) )
					// Create a format creator for the current header
					fc.add ( new FormatCreatorZebra ( PrintingActivity.this , transactionHeaders , Type.SALESORDERSUMMARY , printoutType , null , null , null ) );
				else if ( printerType.equals ( 2 ) )
					// Not support on intermec
					throw new UnsupportedOperationException ();
					// Create a format creator for the current header
				break;
			case Type.DirectReturn:
				// Retrieve the request for return headers
				transactionHeaders = (ArrayList < TransactionHeaders >) getIntent ().getSerializableExtra ( HEADER );
				// Make sure there is at least one valid transaction
				if ( transactionHeaders == null || transactionHeaders.isEmpty () )
					// No valid transactions
					return;
				// Iterate over every header
				for ( TransactionHeaders transactionHeader : transactionHeaders )
					// Check type
					if ( printerType.equals ( 1 ) )
						// Create a format creator for the current header
						//fc.add ( new FormatCreatorZebra ( PrintingActivity.this , transactionHeader , Type.RFR , printoutType , null , null , null ) );
						throw new UnsupportedOperationException ();
						else if ( printerType.equals ( 2 ) )
						// Not support on intermec
						//throw new UnsupportedOperationException ();
					 if ( PermissionsUtils.getEnablePrintInvoiceFreshQQP ( PrintingActivity.this , userCode , companyCode ) ) 
						 { 
						   fc.add ( new FormatCreatorIntermec ( PrintingActivity.this , transactionHeader , Type.FreshQQP , printoutType ) );
						  }
					 else{
						// Create a format creator for the current header
						fc.add ( new FormatCreatorIntermec ( PrintingActivity.this , transactionHeader , Type.DirectReturn , printoutType ) );
						 }
						break;	
			case Type.ORDER:
				transactionHeaders = (ArrayList < TransactionHeaders >) getIntent ().getSerializableExtra ( HEADER );
				// Make sure there is at least one valid transaction
				if ( transactionHeaders == null || transactionHeaders.isEmpty () )
					// No valid transactions
					return;
				// Iterate over every header
				for ( TransactionHeaders transactionHeader : transactionHeaders ) {
					// Check type
					if ( printerType.equals ( 1 ) ) {
						// Check if the pages number is valid
						if ( pages == null ) {
							// Prompt for the pages
							promptPages ();
							// Exit method
							return;
						} // End if
						// Create a format creator for the current header
						FormatCreatorZebra formatCreatorZebra = null;
						int currentPage = 0;
						Long detailsCount = DatabaseUtils.getInstance ( PrintingActivity.this ).getDaoSession ().getTransactionDetailsDao ().queryBuilder ()
								.where ( TransactionDetailsDao.Properties.TransactionCode.eq ( transactionHeader.getTransactionCode () ) ).count ();
						int lastPage = (int) ( Math.ceil ( detailsCount.doubleValue () / pages ) - 1 );
						do {
							formatCreatorZebra = new FormatCreatorZebra ( PrintingActivity.this , transactionHeader , Type.ORDER , printoutType , pages , currentPage , lastPage );
							fc.add ( formatCreatorZebra );
							currentPage ++;
						} while ( currentPage <= lastPage );
					} // End if
					else if ( printerType.equals ( 2 ) )
						// Create a format creator for the current header
						fc.add ( new FormatCreatorIntermec ( PrintingActivity.this , transactionHeader , Type.ORDER , printoutType ) );
					else if ( printerType.equals ( 3 ) )
						// Not support on intermec
						   fc.add (  new FormatCreatorMP ( PrintingActivity.this , transactionHeader , Type.ORDER , printoutType ) );
				 
				
		 
				} // End for each
				break;
			case Type.RETURN:
				transactionHeaders = (ArrayList < TransactionHeaders >) getIntent ().getSerializableExtra ( HEADER );
				// Make sure there is at least one valid transaction
				if ( transactionHeaders == null || transactionHeaders.isEmpty () )
					// No valid transactions
					return;
				// Iterate over every header
				for ( TransactionHeaders transactionHeader : transactionHeaders ) {
					// Check type
					if ( printerType.equals ( 1 ) )
						// Not support on zebra
						throw new UnsupportedOperationException ();
					else if ( printerType.equals ( 2 ) )
						if ( PermissionsUtils.getEnablePrintInvoiceFreshQQP ( PrintingActivity.this , userCode , companyCode ) ) 
						{ 
							fc.add ( new FormatCreatorIntermec ( PrintingActivity.this , transactionHeader , Type.FreshQQP , printoutType ) );
							
						}
						else{
						// Create a format creator for the current header
						//fc.add ( new FormatCreatorIntermec ( PrintingActivity.this , transactionHeader , Type.ORDER , printoutType ) );
							fc.add ( new FormatCreatorIntermec ( PrintingActivity.this , transactionHeader , Type.RETURNCashCredit , printoutType ) );
						}
						} // End for each
				break;
				
			case Type.INVOICE:
				// Retrieve the request for return headers
				transactionHeaders = (ArrayList < TransactionHeaders >) getIntent ().getSerializableExtra ( HEADER );
				// Make sure there is at least one valid transaction
				if ( transactionHeaders == null || transactionHeaders.isEmpty () )
					// No valid transactions
					return;
				// Iterate over every header
				for ( TransactionHeaders transactionHeader : transactionHeaders ) {
					// Check type
					if ( printerType.equals ( 1 ) )
						// Not support on zebra
						throw new UnsupportedOperationException ();
					else if ( printerType.equals ( 2 ) ){
						// Create a format creator for the current header
						if ( PermissionsUtils.getEnablePrintInvoiceFreshQQP ( PrintingActivity.this , userCode , companyCode ) ) 
						{ 
							fc.add ( new FormatCreatorIntermec ( PrintingActivity.this , transactionHeader , Type.FreshQQP , printoutType ) );
							
						}
						else{
							if(PermissionsUtils.getDiscountInvoicePrint ( PrintingActivity.this , userCode , companyCode ))
								fc.add ( new FormatCreatorIntermec ( PrintingActivity.this , transactionHeader , Type.INVOICE_DISCOUNT , printoutType ) );
							else
								fc.add ( new FormatCreatorIntermec ( PrintingActivity.this , transactionHeader , Type.INVOICE , printoutType ) );
						}
						} 
					}// End for each
				break;
			case Type.MOVEMENTLoadTransfer:
				movementHeaders = (ArrayList < MovementHeaders >) getIntent ().getSerializableExtra ( HEADER );
				// Make sure there is at least one valid transaction
				if ( movementHeaders == null || movementHeaders.isEmpty () )
					// No valid transactions
					return;
				// Iterate over every header
				for ( MovementHeaders movementHeader : movementHeaders ) {
					// Check type
					if ( printerType.equals ( 1 ) )
						// Not support on zebra
						throw new UnsupportedOperationException ();
					else if ( printerType.equals ( 2 ) )
						// Create a format creator for the current header
						fc.add ( new FormatCreatorIntermec( PrintingActivity.this , movementHeader , null , Type.MOVEMENTLoadTransfer , printoutType ) );
				} // End for each
				break;
			case Type.DirectLoad:
				movementHeaders = (ArrayList < MovementHeaders >) getIntent ().getSerializableExtra ( HEADER );
				// Make sure there is at least one valid transaction
				if ( movementHeaders == null || movementHeaders.isEmpty () )
					// No valid transactions
					return;
				// Iterate over every header
				for ( MovementHeaders movementHeader : movementHeaders ) {
					// Check type
					if ( printerType.equals ( 1 ) )
						// Not support on zebra
						throw new UnsupportedOperationException ();
					else if ( printerType.equals ( 2 ) )
						// Create a format creator for the current header
						fc.add ( new FormatCreatorIntermec( PrintingActivity.this , movementHeader , null , Type.DirectLoad , printoutType ) );
				} // End for each
				break;
			case Type.MOVEMENTLoadRequest:
				movementHeaders = (ArrayList < MovementHeaders >) getIntent ().getSerializableExtra ( HEADER );
				// Make sure there is at least one valid transaction
				if ( movementHeaders == null || movementHeaders.isEmpty () )
					// No valid transactions
					return;
				// Iterate over every header
				for ( MovementHeaders movementHeader : movementHeaders ) {
					// Check type
					if ( printerType.equals ( 1 ) )
						// Not support on zebra
						throw new UnsupportedOperationException ();
					else if ( printerType.equals ( 2 ) )
						// Create a format creator for the current header
						fc.add ( new FormatCreatorIntermec( PrintingActivity.this , movementHeader , null , Type.MOVEMENTLoadRequest , printoutType ) );
				} // End for each
				break;
			case Type.RECEIPT:
				collectionHeaders = (ArrayList < CollectionHeaders >) getIntent ().getSerializableExtra ( HEADER );
				// Make sure there is at least one valid collections
				if ( collectionHeaders == null || collectionHeaders.isEmpty () )
					// No valid collections
					return;
				// Iterate over every header
				for ( CollectionHeaders collectionHeader : collectionHeaders ) {
					// Check type
					if ( printerType.equals ( 1 ) )
						// Not support on zebra
					//	throw new UnsupportedOperationException ();
						fc.add ( new FormatCreatorZebraR ( PrintingActivity.this  , collectionHeader   , Type.RECEIPT ,printoutType , null , null , 0) );
					
						else if ( printerType.equals ( 2 ) )
						// Create a format creator for the current header
						fc.add ( new FormatCreatorIntermec( PrintingActivity.this , collectionHeader , Type.RECEIPT , printoutType ) );
				} // End for each
				break;
				
				
				 
			case Type.UNLOAD:
				movementHeaders = (ArrayList < MovementHeaders >) getIntent ().getSerializableExtra ( HEADER );
				movementDetails = (ArrayList < MovementDetails >) getIntent ().getSerializableExtra ( DETAILS );
				// Make sure there is at least one valid transaction
				if ( movementHeaders == null || movementHeaders.isEmpty () )
					// No valid  movements
					return;
				// Iterate over every header
				for ( MovementHeaders movementHeader : movementHeaders ) {
					// Check type
					if ( printerType.equals ( 1 ) )
						// Not support on zebra
						throw new UnsupportedOperationException ();
					else if ( printerType.equals ( 2 ) )
						// Create a format creator for the current header
						fc.add ( new FormatCreatorIntermec( PrintingActivity.this , movementHeader , movementDetails , Type.UNLOAD , printoutType ) );
				} // End for each
				break;
				
			case Type.LOAD:
				movementHeaders = (ArrayList < MovementHeaders >) getIntent ().getSerializableExtra ( HEADER );
				movementDetails = (ArrayList < MovementDetails >) getIntent ().getSerializableExtra ( DETAILS );
				// Make sure there is at least one valid transaction
				if ( movementHeaders == null || movementHeaders.isEmpty () )
					// No valid  movements
					return;
				// Iterate over every header
				for ( MovementHeaders movementHeader : movementHeaders ) {
					// Check type
					if ( printerType.equals ( 1 ) )
						// Not support on zebra
						throw new UnsupportedOperationException ();
					else if ( printerType.equals ( 2 ) )
						// Create a format creator for the current header
						fc.add ( new FormatCreatorIntermec( PrintingActivity.this , movementHeader , movementDetails , Type.LOAD , printoutType ) );
				} // End for each
				break;
			case Type.STOCKRECONCILIATION:
				movementHeaders = (ArrayList < MovementHeaders >) getIntent ().getSerializableExtra ( HEADER );
				movementDetails = (ArrayList < MovementDetails >) getIntent ().getSerializableExtra ( DETAILS );
				// Make sure there is at least one valid transaction
				if ( movementHeaders == null || movementHeaders.isEmpty () )
					// No valid  movements
					return;
				// Iterate over every header
				for ( MovementHeaders movementHeader : movementHeaders ) {
					// Check type
					if ( printerType.equals ( 1 ) )
						// Not support on zebra
						throw new UnsupportedOperationException ();
					else if ( printerType.equals ( 2 ) )
						// Create a format creator for the current header
						fc.add ( new FormatCreatorIntermec( PrintingActivity.this , movementHeader , movementDetails , Type.STOCKRECONCILIATION , printoutType ) );
				} // End for each
				break;
				
				
				
			case Type.UNLOADREQUEST:
				movementHeaders = (ArrayList < MovementHeaders >) getIntent ().getSerializableExtra ( HEADER );
				// Make sure there is at least one valid transaction
				if ( movementHeaders == null || movementHeaders.isEmpty () )
					// No valid  movements
					return;
				// Iterate over every header
				for ( MovementHeaders movementHeader : movementHeaders ) {
					// Check type
					if ( printerType.equals ( 1 ) )
						// Not support on zebra
						throw new UnsupportedOperationException ();
					else if ( printerType.equals ( 2 ) )
						// Create a format creator for the current header
						fc.add ( new FormatCreatorIntermec( PrintingActivity.this , movementHeader , null , Type.UNLOADREQUEST , printoutType ) );
				} // End for each
				break;
				
			case Type.DirectUnload:
				movementHeaders = (ArrayList < MovementHeaders >) getIntent ().getSerializableExtra ( HEADER );
				// Make sure there is at least one valid transaction
				if ( movementHeaders == null || movementHeaders.isEmpty () )
					// No valid  movements
					return;
				// Iterate over every header
				for ( MovementHeaders movementHeader : movementHeaders ) {
					// Check type
					if ( printerType.equals ( 1 ) )
						// Not support on zebra
						throw new UnsupportedOperationException ();
					else if ( printerType.equals ( 2 ) )
						// Create a format creator for the current header
						fc.add ( new FormatCreatorIntermec( PrintingActivity.this , movementHeader , null , Type.DirectUnload , printoutType ) );
				} // End for each
				break;
				
			case Type.VANMANAGMENT:
				movementHeaders = (ArrayList < MovementHeaders >) getIntent ().getSerializableExtra ( HEADER );
				// Make sure there is at least one valid transaction
				if ( movementHeaders == null || movementHeaders.isEmpty () )
					// No valid  movements
					return;
				// Iterate over every header
				for ( MovementHeaders movementHeader : movementHeaders ) {
					// Check type
					if ( printerType.equals ( 1 ) )
						// Not support on zebra
						throw new UnsupportedOperationException ();
					else if ( printerType.equals ( 2 ) )
						// Create a format creator for the current header
						fc.add ( new FormatCreatorIntermec( PrintingActivity.this , movementHeader , null , Type.VANMANAGMENT , printoutType ) );
				} // End for each
				break;
				
				
			case Type.VANSTOCKCOUNT:
				movementHeaders = (ArrayList < MovementHeaders >) getIntent ().getSerializableExtra ( HEADER );
				// Make sure there is at least one valid transaction
				if ( movementHeaders == null || movementHeaders.isEmpty () )
					// No valid  movements
					return;
				// Iterate over every header
				for ( MovementHeaders movementHeader : movementHeaders ) {
					// Check type
					if ( printerType.equals ( 1 ) )
						// Not support on zebra
						throw new UnsupportedOperationException ();
					else if ( printerType.equals ( 2 ) )
						// Create a format creator for the current header
						fc.add ( new FormatCreatorIntermec( PrintingActivity.this , movementHeader , null , Type.VANSTOCKCOUNT , printoutType ) );
				} // End for each
				break;
				
			case Type.VANTRANSFER:
				movementHeaders = (ArrayList < MovementHeaders >) getIntent ().getSerializableExtra ( HEADER );
				// Make sure there is at least one valid transaction
				if ( movementHeaders == null || movementHeaders.isEmpty () )
					// No valid  movements
					return;
				// Iterate over every header
				for ( MovementHeaders movementHeader : movementHeaders ) {
					// Check type
					if ( printerType.equals ( 1 ) )
						// Not support on zebra
						throw new UnsupportedOperationException ();
					else if ( printerType.equals ( 2 ) )
						// Create a format creator for the current header
						fc.add ( new FormatCreatorIntermec( PrintingActivity.this , movementHeader , null , Type.VANTRANSFER , printoutType ) );
				} // End for each
				break;
				
			case Type.MOVEMENTS:
				movementHeaders = (ArrayList < MovementHeaders >) getIntent ().getSerializableExtra ( HEADER );
				// Make sure there is at least one valid transaction
				if ( movementHeaders == null || movementHeaders.isEmpty () )
					// No valid  movements
					return;
				// Iterate over every header
				for ( MovementHeaders movementHeader : movementHeaders ) {
					// Check type
					if ( printerType.equals ( 1 ) )
						// Not support on zebra
						throw new UnsupportedOperationException ();
					else if ( printerType.equals ( 2 ) )
						// Create a format creator for the current header
						fc.add ( new FormatCreatorIntermec( PrintingActivity.this , movementHeader , null , Type.MOVEMENTS , printoutType ) );
				} // End for each
				break;
			 case Type.APPROVERETURN:
				transactionHeaders = (ArrayList < TransactionHeaders >) getIntent ().getSerializableExtra ( HEADER );
				// Make sure there is at least one valid transaction
				if ( transactionHeaders == null || transactionHeaders.isEmpty () )
					// No valid transactions
					return;
				// Iterate over every header
				for ( TransactionHeaders transactionHeader : transactionHeaders ) {
					// Check type
					if ( printerType.equals ( 1 ) )
						// Not support on zebra
						throw new UnsupportedOperationException ();
					else if ( printerType.equals ( 2 ) )
						// Create a format creator for the current header
						fc.add ( new FormatCreatorIntermec ( PrintingActivity.this , transactionHeader , Type.APPROVERETURN , printoutType ) );
				} // End for each
				break;
			 case Type.CLIENTSTOCKCOUNT:
				clientStockCountHeaders = (ArrayList < ClientStockCountHeaders >) getIntent ().getSerializableExtra ( HEADER );
				// Make sure there is at least one valid transaction
				if ( clientStockCountHeaders == null || clientStockCountHeaders.isEmpty () )
					// No valid  movements
					return;
				// Iterate over every header
				for ( ClientStockCountHeaders clientStockCountHeader  : clientStockCountHeaders ) {
					// Check type
					if ( printerType.equals ( 1 ) )
						// Not support on zebra
						throw new UnsupportedOperationException ();
					else if ( printerType.equals ( 2 ) )
						// Create a format creator for the current header
						fc.add ( new FormatCreatorIntermec( PrintingActivity.this , clientStockCountHeader , Type.CLIENTSTOCKCOUNT , printoutType ) );
				} // End for each
				break;
			case Type.VEHICULESSTOCK:
				vehiclesStock = (ArrayList <vehiclesStockPrinting >)getIntent().getSerializableExtra(HEADER);
				// Make sure there is at least one valid transaction
				if ( vehiclesStock == null || vehiclesStock.isEmpty () )
					// No valid  movements
					return;
				// Check type
				if ( printerType.equals ( 1 ) )
					// Not support on zebra
					throw new UnsupportedOperationException ();
				else if ( printerType.equals ( 2 ) )
					// Create a format creator for the current header
					fc.add ( new FormatCreatorIntermec ( PrintingActivity.this , vehiclesStock , Type.VEHICULESSTOCK , printoutType ) );
				break;
			case Type.COLLECTIONSUMMARY:
				collectionSummaryPrinting = (ArrayList <CollectionSummaryPrinting >)getIntent().getSerializableExtra(HEADER);
			 	summaryReturnPrinting = (ArrayList < SummaryReturnPrinting >) getIntent ().getSerializableExtra ( DETAILS );
				// Make sure there is at least one valid transaction
				if ( collectionSummaryPrinting == null || collectionSummaryPrinting.isEmpty () )
					// No valid  movements
					return;
				// Check type
				if ( printerType.equals ( 1 ) )
					// Not support on zebra
					fc.add ( new FormatCreatorZebraR( PrintingActivity.this  , Type.COLLECTIONSUMMARY, collectionSummaryPrinting,summaryReturnPrinting , printoutType ) );
				
				else if ( printerType.equals ( 2 ) )
					 
					// Create a format creator for the current header
					fc.add ( new FormatCreatorIntermec( PrintingActivity.this  , Type.COLLECTIONSUMMARY, collectionSummaryPrinting,summaryReturnPrinting , printoutType ) );
				break;
				
			case Type.FreeSummeryReport:
				loadInSummaryPrinting = (ArrayList <LoadInSummaryPrinting >)getIntent().getSerializableExtra(HEADER);
				// Make sure there is at least one valid transaction
				if ( loadInSummaryPrinting == null || loadInSummaryPrinting.isEmpty () )
					// No valid  movements
					return;
				// Check type
				if ( printerType.equals ( 1 ) )
					// Not support on zebra
					throw new UnsupportedOperationException ();
				else if ( printerType.equals ( 2 ) )
					 
					// Create a format creator for the current header
					fc.add ( new FormatCreatorIntermec( PrintingActivity.this  , loadInSummaryPrinting , Type.FreeSummeryReport, printoutType, printoutType) );
				break;
			case Type.SalesReturnSummeryReport:
				loadInSummaryPrinting = (ArrayList <LoadInSummaryPrinting >)getIntent().getSerializableExtra(HEADER);
				// Make sure there is at least one valid transaction
				if ( loadInSummaryPrinting == null || loadInSummaryPrinting.isEmpty () )
					// No valid  movements
					return;
				// Check type
				if ( printerType.equals ( 1 ) )
					// Not support on zebra
					throw new UnsupportedOperationException ();
				else if ( printerType.equals ( 2 ) )
					 
					// Create a format creator for the current header
					fc.add ( new FormatCreatorIntermec( PrintingActivity.this  , loadInSummaryPrinting , Type.SalesReturnSummeryReport, printoutType, printoutType) );
				break;
			case Type.loadInSummary: 
			loadInSummaryPrinting = (ArrayList <LoadInSummaryPrinting >)getIntent().getSerializableExtra(HEADER);
				// Make sure there is at least one valid transaction
				if ( loadInSummaryPrinting == null || loadInSummaryPrinting.isEmpty () )
					// No valid  movements
					return;
				// Check type
				if ( printerType.equals ( 1 ) )
					// Not support on zebra
					throw new UnsupportedOperationException ();
				else if ( printerType.equals ( 2 ) )
					 
					// Create a format creator for the current header
					fc.add ( new FormatCreatorIntermec( PrintingActivity.this  , loadInSummaryPrinting , Type.loadInSummary, printoutType, printoutType) );
				break;
			case Type.MovementInSummary: 
				loadInSummaryPrinting = (ArrayList <LoadInSummaryPrinting >)getIntent().getSerializableExtra(HEADER);
					// Make sure there is at least one valid transaction
					if ( loadInSummaryPrinting == null || loadInSummaryPrinting.isEmpty () )
						// No valid  movements
						return;
					// Check type
					if ( printerType.equals ( 1 ) )
						// Not support on zebra
						throw new UnsupportedOperationException ();
					else if ( printerType.equals ( 2 ) )
						 
						// Create a format creator for the current header
						fc.add ( new FormatCreatorIntermec( PrintingActivity.this  , loadInSummaryPrinting , Type.MovementInSummary, printoutType, printoutType) );
					break;
			case Type.COLLECTIONISSETTEL:
				collectionHeaders = (ArrayList <CollectionHeaders >)getIntent().getSerializableExtra(HEADER);
				// Make sure there is at least one valid transaction
				if ( collectionHeaders == null || collectionHeaders.isEmpty () )
					// No valid  movements
					return;
				// Check type
				if ( printerType.equals ( 1 ) )
					// Not support on zebra
					throw new UnsupportedOperationException ();
				else if ( printerType.equals ( 2 ) )
					// Create a format creator for the current header
					fc.add ( new FormatCreatorIntermec(  Type.COLLECTIONISSETTEL, collectionHeaders,PrintingActivity.this   , printoutType ) );
				break;
				
			case Type.INVOICESUMMARY://done
				transactionHeaders = (ArrayList <TransactionHeaders >)getIntent().getSerializableExtra(HEADER);
				// Make sure there is at least one valid transaction
				if ( transactionHeaders == null || transactionHeaders.isEmpty () )
					// No valid  transaction
					return;
				// Check type
				if ( printerType.equals ( 1 ) )
					// Not support on zebra
					throw new UnsupportedOperationException ();
				else if ( printerType.equals ( 2 ) )
					// Create a format creator for the current header
					fc.add ( new FormatCreatorIntermec( PrintingActivity.this  , Type.INVOICESUMMARY , printoutType ,transactionHeaders) );
				break;
			case Type.MISSEDITEMS:
				missedItems = (ArrayList <MissedItems >)getIntent().getSerializableExtra(HEADER);
				// Make sure there is at least one valid transaction
				if ( missedItems == null || missedItems.isEmpty () )
					// No valid  transaction
					return;
				// Check type
				if ( printerType.equals ( 1 ) )
					// Not support on zebra
					throw new UnsupportedOperationException ();
				else if ( printerType.equals ( 2 ) )
					// Create a format creator for the current header
					fc.add ( new FormatCreatorIntermec( PrintingActivity.this  , Type.MISSEDITEMS   ,missedItems) );
				break;
			case Type.ITEMDISTRIBUTION://done
				items = (ArrayList <Items >)getIntent().getSerializableExtra(HEADER);
				itemDistrbutionPrinting = (ArrayList <ItemDistrbutionPrinting >)getIntent().getSerializableExtra(DETAILS);
				// Make sure there is at least one valid transaction
				if ( items == null || items.isEmpty () )
					// No valid  transaction
					return;
				// Check type
				if ( printerType.equals ( 1 ) )
					// Not support on zebra
					throw new UnsupportedOperationException ();
				else if ( printerType.equals ( 2 ) )
			        // Create a format creator for the current header
					fc.add ( new FormatCreatorIntermec( PrintingActivity.this  ,items,itemDistrbutionPrinting, Type.ITEMDISTRIBUTION   , printoutType) );
				break;
			case Type.CLIENTASSET:
				//	ClientAssetPrinting clientAssetPrinting 
				clientAssetPrinting = (ArrayList < ClientAssetPrinting >)getIntent().getSerializableExtra(HEADER);
				// Make sure there is at least one valid transaction
				if ( clientAssetPrinting == null || clientAssetPrinting.isEmpty () )
					// No valid  transaction
					return;
				// Check type
				if ( printerType.equals ( 1 ) )
					// Not support on zebra
					throw new UnsupportedOperationException ();
				else if ( printerType.equals ( 2 ) )
					// Create a format creator for the current header
					fc.add ( new FormatCreatorIntermec( PrintingActivity.this  ,clientAssetPrinting, Type.CLIENTASSET   ) );
				break;
			case Type.PLANVISITSREPORT:
				//	ClientAssetPrinting clientAssetPrinting 
				cycleCalls = (ArrayList < CycleCalls >)getIntent().getSerializableExtra(HEADER);
				// Make sure there is at least one valid transaction
				if ( cycleCalls == null || cycleCalls.isEmpty () )
					// No valid  transaction
					return;
				// Check type
				if ( printerType.equals ( 1 ) )
					// Not support on zebra
					throw new UnsupportedOperationException ();
				else if ( printerType.equals ( 2 ) )
					// Create a format creator for the current header
					fc.add ( new FormatCreatorIntermec( PrintingActivity.this  ,cycleCalls, Type.PLANVISITSREPORT, printoutType,"d"  ) );
				break;
				
				
			} // End switch
		} catch ( UnsupportedOperationException exception ) {
			// Warn user
			AppDialog.getInstance ().displayAlert ( this ,
					AppResources.getInstance ( this ).getString ( this , R.string.error_label ) , 
					AppResources.getInstance ( this ).getString ( this , R.string.printing_activity_printer_not_supported ) ,
					ButtonsType.OK , new DialogInterface.OnClickListener () {
						@Override
						public void onClick ( DialogInterface dialog , int which ) {
							// Exit activity
							finish ();
						}
					} );
		} // End try-catch block
		if (printerType.equals ( 2 )){
		restartBT(FIVE);
		new Thread(new Runnable() {
		    @Override
		    public void run() {
		        try {
		            Thread.sleep(FIVE);
		        } catch (InterruptedException e) {
		            e.printStackTrace();
		        }
		        runOnUiThread(new Runnable() {
		            @Override
		            public void run() {
		            	
		            	AppDialog.getInstance().displayAlert(PrintingActivity.this ,
								null ,
								AppResources.getInstance ( PrintingActivity.this ).getString ( PrintingActivity.this , R.string.printing_activity_turn_printer_on_message ) ,
								AppDialog.ButtonsType.OK ,
								new DialogInterface.OnClickListener () {
									@Override
									public void onClick ( DialogInterface dialog , int which ) {
										// Determine the clicked button
										switch ( which ) {
										case DialogInterface.BUTTON_NEUTRAL:
											// Select the appropriate printer
											//check();
											new Thread ( PrintingActivity.this , "PrintingTask" ).start ();
											//finish ();
											break;
										} // End switch
									}
								} );
		            	
		            }
		        });
		    }
		}).start();
		}
		else
			// Start thread to send the printout content asynchronously via bluetooth
				new Thread ( PrintingActivity.this , "PrintingTask" ).start ();
		
		// Start thread to send the printout content asynchronously via bluetooth
		//new Thread ( PrintingActivity.this , "PrintingTask" ).start ();
	}
	
	public String getAddress(String x){
		return deviceAddress.get(x);
	}
	
	/**
	 * Performs communication operations(write/print) base on user communication method in another thread.
	 */
	public void runold () {
		try {
			if ( ! m_printerBluetoothAddr.equals ( m_activeDeviceAddr ) ) {
				m_activeDeviceAddr = m_printerBluetoothAddr;
				connBT = Connection_Bluetooth.createClient ( m_activeDeviceAddr );
			}
			if ( connBT == null )
				 connBT = Connection_Bluetooth.createClient ( m_activeDeviceAddr );
			//Open bluetooth socket
			if ( !connBT.getIsOpen () )
				  connBT.open ();
			int counterChecking = 0;
			while ( counterChecking <= 3 ) {
				if ( connBT.isAlive () )
					break;
				Thread.sleep ( 5000 );
				counterChecking ++;
				Baguette.showText ( PrintingActivity.this , "Check If Priniter is Turned On ..." , Baguette.BackgroundColor.RED );
			}
			byte [] data = { 0 };
			String content = "";
			for ( FormatCreator _fc : fc ) {
				content += _fc.getFormat ();
				content += "\n\n\n\n\n\n\n\n\n\n";
			}
			data = content.getBytes ();
			int counter = 10;
			while ( counter != 0 ) {
				if ( connBT.waitForEmptyBuffer ( 1000 ) )
					break;
				else
					counter --;
			}
			connBT.write ( data );
			Thread.sleep ( 1000 );
		} catch ( Exception e ) {
			e.getMessage ();
			e.printStackTrace ();
		} finally {
			finish ();
			try {
				if ( printerType.equals ( 2 ) );
					 Thread.sleep (50000 );
				     connBT.close ();
				     connBT = null;
			} catch ( Exception exception ) {
				     exception.getMessage ();
			         exception.printStackTrace ();
			}
		}

	}

	

	
	
	
	public void run () {
		try {	
			Thread.sleep ( 5000 );
    		openBlT();
		
			byte [] data = { 0 };
			String content = "";
			for ( FormatCreator _fc : fc ) {
				content += _fc.getFormat ().toString();
				content += "\n\n\n\n\n\n\n\n\n\n";
			}
			//data = content.getBytes ();
			//int counter = 10;
			//while ( counter != 0 ) {
				//if ( connBT.waitForEmptyBuffer ( 1000 ) )
					//break;
				//else
					//counter --;
			//}
			 mmOutputStream.write(content.getBytes());	
			 
		     Thread.sleep ( 1000 );
		} catch ( Exception e ) {
				e.getMessage ();
				e.printStackTrace ();
			
			//connBT.close ();
			//connBT = null;
		} finally {
		
			
			try {
				if ( printerType.equals ( 2 ) );
					Thread.sleep (10000 );
					 
					closeBT();
					
					switch ( getIntent ().getIntExtra ( TYPE , - 1 ) ) {
					case Type.RECEIPT:
						ArrayList < CollectionHeaders >	collectionHeaders = (ArrayList < CollectionHeaders >) getIntent ().getSerializableExtra ( HEADER );
						// Make sure there is at least one valid collections
						if ( collectionHeaders == null || collectionHeaders.isEmpty () )
							// No valid collections
							return;
						// Iterate over every header
						for ( CollectionHeaders collectionHeader : collectionHeaders ) {
							
							if(collectionHeader.getPrintingTimes() == null || collectionHeader.getPrintingTimes()==0 )
							{	
							 
								collectionHeader.setPrintingTimes(1);
								DatabaseUtils.getInstance ( PrintingActivity.this ).getDaoSession ().getCollectionHeadersDao().update(collectionHeader);
							}
						 	else
						 	{ 
								 collectionHeader.setPrintingTimes(collectionHeader.getPrintingTimes() + 1);
								 DatabaseUtils.getInstance ( PrintingActivity.this ).getDaoSession ().getCollectionHeadersDao() .update(collectionHeader);
							
						 	}
						}
						break;
					case Type.INVOICE:
						@SuppressWarnings("unchecked")
						ArrayList < TransactionHeaders >	transactionHeaders = (ArrayList < TransactionHeaders >) getIntent ().getSerializableExtra ( HEADER );
						// Make sure there is at least one valid collections
						if ( transactionHeaders == null || transactionHeaders.isEmpty () )
							// No valid collections
							return;
						// Iterate over every header
						for ( TransactionHeaders transactionHeader : transactionHeaders ) {
							
							if(transactionHeader.getPrintingTimes() == null || transactionHeader.getPrintingTimes() == 0 )
							{	
							 
								transactionHeader.setPrintingTimes(1);
								DatabaseUtils.getInstance ( PrintingActivity.this ).getDaoSession ().getTransactionHeadersDao().update(transactionHeader);
							}
						 	else
						 	{ 
						 		transactionHeader.setPrintingTimes(transactionHeader.getPrintingTimes() + 1);
								DatabaseUtils.getInstance ( PrintingActivity.this ).getDaoSession ().getTransactionHeadersDao() .update(transactionHeader);
							
						 	}
						}
						break;
					} // End switch
				//connBT.close ();
				//connBT = null;
			} catch ( Exception exception ) {
				exception.getMessage ();
				exception.printStackTrace ();
		
				//connBT.close ();
				//connBT = null;
			}
		
			finish ();
		}
	
	}
	
	
	
	
	
	//here I start the test of open and send test print
	//Find the Printer By Name
	//Function to find the printer
    void findBT() {
       try {
            mBluetoothAdapter1 = BluetoothAdapter.getDefaultAdapter();
             if (mBluetoothAdapter1 == null) {
                //myLabel.setText("No bluetooth adapter available");
    			Baguette.showText ( PrintingActivity.this , "No bluetooth adapter available" , Baguette.BackgroundColor.RED );
    			
            }
 
            if ( ! mBluetoothAdapter1.isEnabled()) {
                Intent enableBluetooth = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }
          //  check ();

//            Set < BluetoothDevice > pairedDevices = mBluetoothAdapter1
//                    .getBondedDevices();
//             if (pairedDevices.size() > 0) {
//                for (BluetoothDevice device : pairedDevices) {
// 
//                    // MP300 is the name of the bluetooth printer device
//                    if (device.getAddress().equals("00:13:E0:DC:82:3C")) {//.getName().equals("6820-7008347")
//                        mmDevice = device;
//                    
//                        break;
//                    }
//                }
//            }
//             








            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter1.getBondedDevices ();

           		// Get the MAC addresses
           		List < String > pairedAddresses = new ArrayList < String > ();
           		Map<String, BluetoothDevice> map1 = new HashMap<String, BluetoothDevice>();
           		for ( BluetoothDevice device : pairedDevices )
           			// Store the device's MAC address
           	
           		{	//aa
           			pairedAddresses.add ( device.getAddress().toLowerCase () );
                	map1.put( device.getAddress().toLowerCase (), device );
           			//pairedAddresses.add ( device.getName().toLowerCase () );
                	//map1.put( device.getName().toLowerCase (), device );
           		}
           		
           		// Retrieve the user's printers
           		List < Printers > printers = DatabaseUtils.getInstance ( this ).getDaoSession ().getPrintersDao ().queryBuilder ()
           				.where ( PrintersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( this ) ) ,
           						PrintersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( this ) ) ).list ();
           		// Make sure there is at least one printer
           		if ( printers.isEmpty () ) {
           			// Warn the user
           			AppDialog.getInstance ().displayAlert ( this ,
           					AppResources.getInstance ( this ).getString ( this , R.string.error_label ),
           					AppResources.getInstance ( this ).getString ( this , R.string.printing_activity_no_user_printers_message ) ,
           					ButtonsType.OK , new DialogInterface.OnClickListener () {
           						@Override
           						public void onClick ( DialogInterface dialog , int which ) {
           							// Finish activity
           							finish ();


           						}
           					} );
           			// Exit method
           			return;
           		} // End if
           		// Search for linked printer that is paired
           		// Iterate over the linked printers
           		for ( Printers printer : printers ) {
           			// Make sure the MAC address is well formatted
           			if ( ! printer.getPrinterMAC ().contains ( ":" ) ) {
           				String mac = "";
           				// Iterate over the characters
           				//aa
           			for ( int i = 0 ; i < printer.getPrinterMAC ().length () ; i = i + 2 ) 
           			// Properly format the MAC address
           					mac += printer.getPrinterMAC ().substring ( i , i + 2 ) + ":";
           		    // Set the properly formatted address
           				printer.setPrinterMAC ( mac.substring ( 0 , mac.length () - 1 ) );
           			} // End if
           			// Check if the current printer is paired
           			if ( pairedAddresses.contains ( printer.getPrinterMAC ().toLowerCase () ) ) {
           				// Select the current printer 
           				m_printerBluetoothAddr = printer.getPrinterMAC ();
           				printerType = printer.getType ();
           				if(map1.containsKey(printer.getPrinterMAC ().toLowerCase ()))
           			    mmDevice = map1.get(printer.getPrinterMAC ().toLowerCase ());
           				// Exit loop
           				break;
           			} // End if
           		} // End for each
           		// Make sure the selected printer MAC address is valid
           		if ( m_printerBluetoothAddr == null ) {

 // Warn the user
           			AppDialog.getInstance ().displayAlert ( this ,
           					AppResources.getInstance ( this ).getString ( this , R.string.error_label ),
           					AppResources.getInstance ( this ).getString ( this , R.string.printing_activity_not_connected_message ) ,
           					ButtonsType.OK , new DialogInterface.OnClickListener () {
           						@Override
           						public void onClick ( DialogInterface dialog , int which ) {
           							// Finish activity
           							finish ();
 
           						}
           					} );
           			
           			
           		}
           		if(printerType == null){
          			// Warn the user
          			AppDialog.getInstance ().displayAlert ( this ,
          					AppResources.getInstance ( this ).getString ( this , R.string.error_label ),
          					AppResources.getInstance ( this ).getString ( this , R.string.printing_activity_no_pairing_printers_message ) ,
          					ButtonsType.OK , new DialogInterface.OnClickListener () {
          						@Override
          						public void onClick ( DialogInterface dialog , int which ) {
          							// Finish activity
          							finish ();
          						}
          					} );
          			// Exit method
          		 	return;
          		} 
             
             
             
           // myLabel.setText("Bluetooth Device Found");
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
 //Open the Bluetooth function By UUID 
 // Tries to open a connection to the bluetooth printer device
    void openBlT() throws IOException {
        try {
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
 
            beginListenForData();
        	
          //  Baguette.showText ( PrintingActivity.this , "Bluetooth Opened" , Baguette.BackgroundColor.RED );
        	
            //myLabel.setText("Bluetooth Opened");
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
    
    // After opening a connection to bluetooth printer device,
    // we have to listen and check if a data were sent to be printed.
    void beginListenForData() {
        try {
            final Handler handler = new Handler();
 
            // This is the ASCII code for a newline character
            final byte delimiter = 10;
 
            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];
 
            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted()
                            && !stopWorker) {
 
                        try {
 
                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length);
                                        final String data = new String(
                                                encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;
 
                                        handler.post(new Runnable() {
                                            public void run() {
                                               // String myLabel = "Open";
												
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
 
                        } catch (IOException ex) {
                            stopWorker = true;
                        }
 
                    }
                }
            });
 
            workerThread.start();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    // Close the connection to bluetooth printer.
    void closeBT() throws IOException {
        try {
        	
        	
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
        
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
	
	@Override
	protected void onActivityResult ( int requestCode , int resultCode , Intent data ) {
		if ( requestCode == REQUEST_ENABLE_BT )
			check ();
	}
	//Disable BT connection
		public void disableBT(){
			try {
			closeBT();
		    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		    if (mBluetoothAdapter.isEnabled()){
		        mBluetoothAdapter.disable();
		    } 
		    }catch ( Exception exception ) {
				exception.getMessage ();
				exception.printStackTrace ();
		    }

		}
		//Enable BT connection
		public void enableBT(){
			try {
		    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		    if (!mBluetoothAdapter.isEnabled()){
		        mBluetoothAdapter.enable();
		    }
		    openBlT();
			} catch ( Exception e ) {
				e.getMessage ();
				e.printStackTrace ();
			}
		}
		//Restart BT connection 
		public void restartBT(final int msecondes){
			disableBT();
			new Thread(new Runnable() {
			    @Override
			    public void run() {
			        try {
			            Thread.sleep(msecondes);
			        } catch (InterruptedException e) {
			            e.printStackTrace();
			        }
			        runOnUiThread(new Runnable() {
			            @Override
			            public void run() {
			            	if ( ! mBluetoothAdapter1.isEnabled () ) {
			    				enableBT();
			    			}
			            	}
			        });
			    }
			}).start();
		}
}