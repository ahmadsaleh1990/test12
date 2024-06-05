/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.ClientDivisionsDao;
import me.SyncWise.Android.Database.ClientDues;
import me.SyncWise.Android.Database.ClientDuesDao;
import me.SyncWise.Android.Database.Clients;
import me.SyncWise.Android.Database.Companies;
import me.SyncWise.Android.Database.CompaniesDao;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.Divisions;
import me.SyncWise.Android.Database.DivisionsDao;
import me.SyncWise.Android.Database.TotalClientDuesDao;

/**
 * Utility class that provides helper methods related to the client card.<br>
 * In order to enabled the client pictures feature, use the {@link #setClientPicturesEnabled(boolean)} method in {@link me.SyncWise.Android.App#onCreate() App.onCreate()}.
 * 
 * @author Elias
 *
 */
public class ClientCard {
	
	/**
	 * String holding the default value used to indicate whether or not the user is should scan client barcodes to visit them.
	 */
	public static final String DEFAULT_CLIENT_BARCODES = "N";
	public static final String ENABLE_DUO = "Y";
	/**
	 * String hosting the Date format used to properly convert the client birth date object.
	 */
	public static final String BIRTH_DATE_FORMAT = "dd/MM/yyyy";
	
	/**
	 * String holding the default value used to indicate whether or not the client is allowed to exceed his/her credit limit.
	 */
	public static final String DEFAULT_APPLY_CLIENT_CREDIT_LIMIT = "N";
	
	/**
	 * Initializes the client card using the provided client object.
	 * <ul>
	 * <li>The client name is displayed.</li>
	 * <li>The client image is linked to the profile screen.</li>
	 * </ul>
	 * 
	 * @param activity	Reference to the activity that contains the client card.
	 * @param client	{@link me.SyncWise.Android.Database.Clients Clients} object holding a reference to the current client.
	 */
	@SuppressLint("SimpleDateFormat")
	public static void initializeClientCard ( final Activity activity , final Clients client ) {
		try {
			// Retrieve a reference to the layout client card
			FrameLayout layout = (FrameLayout) activity.findViewById ( R.id.layout_client_card );
			
			// Initialize the code label
			String codeLabel = AppResources.getInstance ( activity ).getString ( activity , R.string.code_label );
			String companyLabel = AppResources.getInstance ( activity ).getString ( activity , R.string.company_label );
			String divisionLabel = AppResources.getInstance ( activity ).getString ( activity , R.string.division_label );
			String notAvailableLabel = AppResources.getInstance ( activity ).getString ( activity , R.string.not_available_abbreviation );
			
			// Build a spannable string out of the client code in order to apply various spans
			SpannableString clientCode = new SpannableString ( codeLabel + " : " + client.getClientCode () );
			// Apply color span
			clientCode.setSpan ( new ForegroundColorSpan ( Color.BLUE ) , clientCode.length () - client.getClientCode ().length () , clientCode.length () , 0 );
			
			// Retrieve the client company
			Companies company = DatabaseUtils.getInstance ( activity ).getDaoSession ().getCompaniesDao ().queryBuilder ()
					.where ( CompaniesDao.Properties.CompanyCode.eq ( client.getCompanyCode () ) ).unique ();
			// Build a spannable string out of the company in order to apply various spans
			SpannableString companyData = new SpannableString ( companyLabel + " : " + company.getCompanyName () );
			// Apply color span
			companyData.setSpan ( new ForegroundColorSpan ( Color.BLUE ) , companyData.length () - company.getCompanyName ().length () , companyData.length () , 0 );
			
			// Retrieve the client division
			List < Divisions > divisions = DatabaseUtils.getInstance ( activity ).getDaoSession ().getDivisionsDao ()
					.queryRaw ( " WHERE " + DivisionsDao.Properties.DivisionCode.columnName + " = " +
							"( SELECT " + ClientDivisionsDao.Properties.DivisionCode.columnName + " FROM " + ClientDivisionsDao.TABLENAME +
								" WHERE " + ClientDivisionsDao.Properties.ClientCode.columnName + " = ? AND " + ClientDivisionsDao.Properties.CompanyCode.columnName + " = ? ) " +
							"AND " + DivisionsDao.Properties.CompanyCode.columnName + " = ?",
							new String [] { client.getClientCode () , client.getCompanyCode () , client.getCompanyCode () } );
			// Declare and initialize a string used to host the division name
			String divisionName = divisions.isEmpty () ? notAvailableLabel : divisions.get ( 0 ).getDivisionName ();
			// Build a spannable string out of the division in order to apply various spans
			SpannableString divisionData = new SpannableString ( divisionLabel + " : " + divisionName );
			// Apply color span
			divisionData.setSpan ( new ForegroundColorSpan ( Color.BLUE ) , divisionData.length () - divisionName.length () , divisionData.length () , 0 );
			
			// Declare and initialize a char sequence in order to merge the formatted client data
			CharSequence clientData = TextUtils.concat ( client.getClientName () + "\n" + clientCode + "\n" + companyData + "\n" + divisionData );
			
			// Display the client data
			( (TextView) layout.findViewById ( R.id.label_client_name ) ).setText ( clientData , BufferType.SPANNABLE );
			
			// Check if the client has a birth date
			if ( client.getDateOfBirth () != null ) {
				try {
					// Retrieve the birth date
					Calendar birthDate = Calendar.getInstance ();
					birthDate.setTimeInMillis ( new SimpleDateFormat ( BIRTH_DATE_FORMAT ).parse ( client.getDateOfBirth () ).getTime () );
					// Retrieve the current date
					Calendar today = Calendar.getInstance ();
					// Determine if today is the client's birthday
					if ( today.get ( Calendar.DAY_OF_MONTH ) == birthDate.get ( Calendar.DAY_OF_MONTH )
							&& today.get ( Calendar.MONTH ) == birthDate.get ( Calendar.MONTH ) )
						// Display the birthday cake
						layout.findViewById ( R.id.icon_birthday_cake ).setVisibility ( View.VISIBLE );
				} catch ( Exception exception ) {
					// Invalid date format
					// Hide the birthday cake
					layout.findViewById ( R.id.icon_birthday_cake ).setVisibility ( View.GONE );
				} // End of try-catch block
			} // End if
			else
				// Hide the birthday cake
				layout.findViewById ( R.id.icon_birthday_cake ).setVisibility ( View.GONE );
		} catch ( Exception exception ) {
			// Do nothing
		} // End of try-catch block
	}
	
	/**
	 * Determines if the provided client is a cash type client.
	 * 	
	 * @param client	A {@link me.SyncWise.Android.Database.Clients Clients} object hosting the client to check.
	 * @return	Boolean indicating if the client is cash.
	 */
	public static boolean isCashClient ( final Clients client ) {
		try {
			return client.getClientType ().equals ( 1 );
		} catch ( Exception exception ) {
			// Invalid client
			return false;
		} // End try-catch block
	}
	
	/**
	 * Determines if the provided client is a credit type client.
	 * 	
	 * @param client	A {@link me.SyncWise.Android.Database.Clients Clients} object hosting the client to check.
	 * @return	Boolean indicating if the client is credit.
	 */
	public static boolean isCreditClient ( final Clients client ) {
		try {
			return client.getClientType ().equals ( 2 );
		} catch ( Exception exception ) {
			// Invalid client
			return false;
		} // End try-catch block
	}
	
	/**
	 * Determines if the provided client has overdue amounts in the current division.
	 * 
	 * @param context	The application context.
	 * @param client	A {@link me.SyncWise.Android.Database.Clients Clients} object hosting the client to check.
	 * @return	Boolean indicating if the client has overdue amounts in the current division.
	 */
	public static boolean hasOverduesCurrentDivision ( final Context context , final Clients client ) {
		List < ClientDues > clientDues = DatabaseUtils.getInstance ( context ).getDaoSession ().getClientDuesDao ().queryBuilder ()
				.where ( ClientDuesDao.Properties.DueDate.le ( Calendar.getInstance ().getTime () ) ,
						ClientDuesDao.Properties.RemainingAmount.gt ( 0 ) ,
						ClientDuesDao.Properties.ClientCode.eq ( client.getClientCode () ) ,
						ClientDuesDao.Properties.CompanyCode.eq ( client.getCompanyCode () ) ,
						ClientDuesDao.Properties.DivisionCode.eq ( client.getDivisionCode () ) ).list ();
		return clientDues.size () > 0;
	}
	
	/**
	 * Determines if the provided client has overdue amounts in the current company.
	 * 
	 * @param context	The application context.
	 * @param client	A {@link me.SyncWise.Android.Database.Clients Clients} object hosting the client to check.
	 * @return	Boolean indicating if the client has overdue amounts in the current company.
	 */
	public static boolean hasOverduesCurrentCompany ( final Context context , final Clients client ) {
		return DatabaseUtils.getInstance ( context ).getDaoSession ().getTotalClientDuesDao ().queryBuilder ()
				.where ( TotalClientDuesDao.Properties.OverdueAmount.gt ( 0 ) ,
						 TotalClientDuesDao.Properties.ParentCode.eq ( client.getParentCode () ) ,
						 TotalClientDuesDao.Properties.CompanyCode.eq ( client.getCompanyCode () ) ).count () > 0;
	}
	
}