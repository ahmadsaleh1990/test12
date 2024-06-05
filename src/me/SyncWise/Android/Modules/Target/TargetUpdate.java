/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Target;

import java.util.Calendar;

import me.SyncWise.Android.Database.ClientsDao;
import me.SyncWise.Android.Database.CurrenciesDao;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.DivisionsDao;
import me.SyncWise.Android.Database.ItemsDao;
import me.SyncWise.Android.Database.StatusUtils;
import me.SyncWise.Android.Database.TargetAchievementsDao;
import me.SyncWise.Android.Database.TargetAssignmentsDao;
import me.SyncWise.Android.Database.TargetAssignmentsUtils;
import me.SyncWise.Android.Database.TargetDetailsDao;
import me.SyncWise.Android.Database.TargetHeadersDao;
import me.SyncWise.Android.Database.TargetHeadersUtils;
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Database.VisitsUtils;
import android.content.Context;

/**
 * Utilities class providing methods and constants related to target coverage.
 * 
 * @author Elias
 *
 */
public class TargetUpdate {
	
	/**
	 * Enumeration used to define the target coverage update type to apply.
	 * 
	 * @author Elias
	 *
	 */
	public static enum UpdateType {
		INCREASE ,
		DECREASE
	}
	
	/**
	 * Updates the target coverage for the provided category and type.<br>
	 * The category indicates the target coverage action to update : Visits number, collection amount, ...<br>
	 * The type indicates the update type to perform : either an increase or decrease in number.
	 * 
	 * @param context	The application context.
	 * @param targetType	Integer holding the target type to update.
	 * @param subjectCode	String holding the subject code related to the target.
	 * @param subjectType	Integer holding the type of the provided subject code.
	 * @param subSubjectCode	String holding the sub subject code related to the target.
	 * @param subSubjectType	Integer holding the type of the provided sub subject code.
	 * @param companyCode	String hosting the company code.
	 * @param divisionCode	String hosting the division code.
	 * @param type	An enumeration constant of {@link me.SyncWise.Android.Modules.Target.TargetUpdate.UpdateType UpdateType} indicating the target coverage type to perform.
	 * @param coverageAmount	Double holding a target coverage amount (the achieved amount) of targeted collection / order (if any).
	 * @param currencyCode	String holding the amount's currency code (if any).
	 * 
	 * @see me.SyncWise.Android.Database.TargetHeadersUtils.Type
	 * @see me.SyncWise.Android.Database.TargetHeadersUtils.SubType
	 */
	public static void updateCoverage ( final Context context , final int targetType , String subjectCode , final int subjectType , String subSubjectCode , final int subSubjectType , final String companyCode , final String divisionCode , final UpdateType type , final Double coverageAmount , final String currencyCode ) {
		// Retrieve the current date in the UNIX epoch format
		long today = Calendar.getInstance ().getTimeInMillis ();
		// Retrieve the current date
		Calendar calendar = Calendar.getInstance ();
		// Retrieve the start of the day
		calendar.set ( Calendar.HOUR_OF_DAY , 0 );
		calendar.set ( Calendar.MINUTE , 0 );
		calendar.set ( Calendar.SECOND , 0 );
		calendar.set ( Calendar.MILLISECOND , 0 );
		long startDay = calendar.getTimeInMillis ();
		// Retrieve the end of the day
		calendar.set ( Calendar.HOUR_OF_DAY , 23 );
		calendar.set ( Calendar.MINUTE , 59 );
		calendar.set ( Calendar.SECOND , 59 );
		calendar.set ( Calendar.MILLISECOND , 999 );
		long endDay = calendar.getTimeInMillis ();
		// Retrieve the user code
		String userCode = DatabaseUtils.getCurrentUserCode ( context );
		// Declare and initialize a string used to host the query
		String SQL = null;
		// Declare and initialize an array of string used to host the selection arguments
		String selectionArguments [] = null;
		
		// Determine the target type
		switch ( targetType ) {
		case TargetHeadersUtils.Type.TOTAL_COVERAGE:
			// Update the total coverage
			// Compute the SQL query
			SQL = "UPDATE " + TargetAchievementsDao.TABLENAME + " " + 
					"SET " + TargetAchievementsDao.Properties.AmountAcheived.columnName + " = " + TargetAchievementsDao.Properties.AmountAcheived.columnName + " + ( SELECT CASE WHEN COUNT ( * ) != 1 THEN 0 ELSE 1 END FROM Visits WHERE VisitType = '" + VisitsUtils.Type.IN_ROUTE + "' AND VisitStatus = " + StatusUtils.isActive () + " AND StartDate BETWEEN " + startDay + " AND " + endDay + " AND ClientCode = ? ) " +
					"WHERE ( TargetCode || '--' || LineID || '--' || SubjectCode || '--' || TargetDetailType || '--' || CompanyCode || '--' || DivisionCode ) IN ( " +
					"SELECT ( TD.TargetCode || '--' || TD.LineID || '--' || TD.SubSubjectCode || '--' || TD.TargetDetailTypeSub || '--' || TH.CompanyCode || '--' || TH.DivisionCode ) " +
					"FROM ( ( ( " + TargetHeadersDao.TABLENAME + " TH INNER JOIN " + TargetAssignmentsDao.TABLENAME + " TAS ON TH." + TargetHeadersDao.Properties.TargetCode.columnName + " = TAS." + TargetAssignmentsDao.Properties.TargetCode.columnName + " " +
					"AND ( TAS." + TargetAssignmentsDao.Properties.AssignmentType.columnName + " = ? OR TAS." + TargetAssignmentsDao.Properties.AssignmentType.columnName + " = ? ) ) " +
					"INNER JOIN " + TargetDetailsDao.TABLENAME + " TD ON TH." + TargetHeadersDao.Properties.TargetCode.columnName + " = TD." + TargetDetailsDao.Properties.TargetCode.columnName + " ) " +
					"INNER JOIN " + UsersDao.TABLENAME + " U ON TD." + TargetDetailsDao.Properties.SubjectCode.columnName + " = ? AND TD." + TargetDetailsDao.Properties.TargetDetailType.columnName + " = ? " +
							"AND TD." + TargetDetailsDao.Properties.SubSubjectCode.columnName + " = U." + UsersDao.Properties.UserCode.columnName + " AND TH." + TargetHeadersDao.Properties.CompanyCode.columnName + " = U." + UsersDao.Properties.CompanyCode.columnName + " " +
							"AND TD." + TargetDetailsDao.Properties.TargetDetailTypeSub.columnName + " = ? ) " +
					"WHERE ( ( TH." + TargetHeadersDao.Properties.StartDate.columnName + " IS NOT NULL AND ? > TH." + TargetHeadersDao.Properties.StartDate.columnName + " ) " +
					"AND ( TH." + TargetHeadersDao.Properties.EndDate.columnName + " IS NOT NULL AND ? < TH." + TargetHeadersDao.Properties.EndDate.columnName + " ) ) " +
					"AND TH." + TargetHeadersDao.Properties.TargetType.columnName + " = ? " +
					"AND TD." + TargetDetailsDao.Properties.SubSubjectCode.columnName + " = ? " +
					"AND TD." + TargetDetailsDao.Properties.TargetDetailTypeSub.columnName + " = ? " +
					"AND TH." + TargetHeadersDao.Properties.CompanyCode.columnName + " = ? " +
					"AND TH." + TargetHeadersDao.Properties.DivisionCode.columnName + " = ? ) ";
			
			// Compute the selection arguments
			selectionArguments = new String [] {
				subSubjectCode ,
				String.valueOf ( TargetAssignmentsUtils.Type.USER ) ,
				String.valueOf ( TargetAssignmentsUtils.Type.GROUP_USERS ) ,
				subjectCode ,
				String.valueOf ( TargetHeadersUtils.SubType.USER ) ,
				String.valueOf ( TargetHeadersUtils.SubType.USER ) ,
				String.valueOf ( today ) ,
				String.valueOf ( today ) ,
				String.valueOf ( TargetHeadersUtils.Type.TOTAL_COVERAGE ) ,
				subjectCode ,
				String.valueOf ( subjectType ) ,
				companyCode ,
				divisionCode
			};
			break;
		case TargetHeadersUtils.Type.TOTAL_COLLECTION:
			// Update the target collection amounts
			// Compute the SQL query
			SQL = "UPDATE " + TargetAchievementsDao.TABLENAME + " " +
					"SET " + TargetAchievementsDao.Properties.AmountAcheived.columnName + " = " + TargetAchievementsDao.Properties.AmountAcheived.columnName + " " + ( type == UpdateType.INCREASE ? "+" : "-" ) + coverageAmount + " " +
					"WHERE ( TargetCode || '--' || LineID || '--' || SubjectCode || '--' || TargetDetailType || '--' || CompanyCode || '--' || DivisionCode ) IN ( " +
					"SELECT ( TD.TargetCode || '--' || TD.LineID || '--' || TD.SubSubjectCode || '--' || TD.TargetDetailTypeSub || '--' || TH.CompanyCode || '--' || TH.DivisionCode ) " +
					"FROM ( ( ( " + TargetHeadersDao.TABLENAME + " TH INNER JOIN " + TargetAssignmentsDao.TABLENAME + " TAS ON TH." + TargetHeadersDao.Properties.TargetCode.columnName + " = TAS." + TargetAssignmentsDao.Properties.TargetCode.columnName + " " +
					"AND ( TAS." + TargetAssignmentsDao.Properties.AssignmentType.columnName + " = ? OR TAS." + TargetAssignmentsDao.Properties.AssignmentType.columnName + " = ? ) ) " +
					"INNER JOIN " + TargetDetailsDao.TABLENAME + " TD ON TH." + TargetHeadersDao.Properties.TargetCode.columnName + " = TD." + TargetDetailsDao.Properties.TargetCode.columnName + " AND TD." + TargetDetailsDao.Properties.SubjectCode.columnName + " = ? AND TD." + TargetDetailsDao.Properties.TargetDetailType.columnName + " = ? " +
							"AND TD." + TargetDetailsDao.Properties.SubjectCode.columnName + " = TD." + TargetDetailsDao.Properties.SubSubjectCode.columnName + " AND TD." + TargetDetailsDao.Properties.TargetDetailType.columnName + " = TD." + TargetDetailsDao.Properties.TargetDetailTypeSub.columnName + " ) " +
					"INNER JOIN " + CurrenciesDao.TABLENAME + " C ON TD." + TargetDetailsDao.Properties.CurrencyCode.columnName + " = C." + CurrenciesDao.Properties.CurrencyCode.columnName + " ) " +
					"WHERE ( ( TH." + TargetHeadersDao.Properties.StartDate.columnName + " IS NOT NULL AND ? > TH." + TargetHeadersDao.Properties.StartDate.columnName + " ) " +
					"AND ( TH." + TargetHeadersDao.Properties.EndDate.columnName + " IS NOT NULL AND ? < TH." + TargetHeadersDao.Properties.EndDate.columnName + " ) ) " +
					"AND TH." + TargetHeadersDao.Properties.TargetType.columnName + " = ? ) ";
			
			// Compute the selection arguments
			selectionArguments = new String [] {
				String.valueOf ( TargetAssignmentsUtils.Type.USER ) ,
				String.valueOf ( TargetAssignmentsUtils.Type.GROUP_USERS ) ,
				subjectCode ,
				String.valueOf ( subjectType ) ,
				String.valueOf ( today ) ,
				String.valueOf ( today ) ,
				String.valueOf ( TargetHeadersUtils.Type.TOTAL_COLLECTION )
			};
			break;
		case TargetHeadersUtils.Type.TOTAL_SALES:
			// Update the target order amounts
			// Compute the SQL query
			SQL = "UPDATE " + TargetAchievementsDao.TABLENAME + " " +
					"SET " + TargetAchievementsDao.Properties.AmountAcheived.columnName + " = " + TargetAchievementsDao.Properties.AmountAcheived.columnName + " " + ( type == UpdateType.INCREASE ? "+" : "-" ) + coverageAmount + " " +
					"WHERE ( TargetCode || '--' || LineID || '--' || SubjectCode || '--' || TargetDetailType || '--' || CompanyCode || '--' || DivisionCode ) IN ( " +
					"SELECT ( TD.TargetCode || '--' || TD.LineID || '--' || TD.SubSubjectCode || '--' || TD.TargetDetailTypeSub || '--' || TH.CompanyCode || '--' || TH.DivisionCode ) " +
					"FROM ( ( ( " + TargetHeadersDao.TABLENAME + " TH INNER JOIN " + TargetAssignmentsDao.TABLENAME + " TAS ON TH." + TargetHeadersDao.Properties.TargetCode.columnName + " = TAS." + TargetAssignmentsDao.Properties.TargetCode.columnName + " " +
					"AND ( TAS." + TargetAssignmentsDao.Properties.AssignmentType.columnName + " = ? OR TAS." + TargetAssignmentsDao.Properties.AssignmentType.columnName + " = ? ) ) " +
					"INNER JOIN " + TargetDetailsDao.TABLENAME + " TD ON TH." + TargetHeadersDao.Properties.TargetCode.columnName + " = TD." + TargetDetailsDao.Properties.TargetCode.columnName + " AND TD." + TargetDetailsDao.Properties.SubjectCode.columnName + " = ? AND TD." + TargetDetailsDao.Properties.TargetDetailType.columnName + " = ? " +
							"AND TD." + TargetDetailsDao.Properties.SubjectCode.columnName + " = TD." + TargetDetailsDao.Properties.SubSubjectCode.columnName + " AND TD." + TargetDetailsDao.Properties.TargetDetailType.columnName + " = TD." + TargetDetailsDao.Properties.TargetDetailTypeSub.columnName + " ) " +
					"INNER JOIN " + CurrenciesDao.TABLENAME + " C ON TD." + TargetDetailsDao.Properties.CurrencyCode.columnName + " = C." + CurrenciesDao.Properties.CurrencyCode.columnName + " ) " +
					"WHERE ( ( TH." + TargetHeadersDao.Properties.StartDate.columnName + " IS NOT NULL AND ? > TH." + TargetHeadersDao.Properties.StartDate.columnName + " ) " +
					"AND ( TH." + TargetHeadersDao.Properties.EndDate.columnName + " IS NOT NULL AND ? < TH." + TargetHeadersDao.Properties.EndDate.columnName + " ) ) " +
					"AND TH." + TargetHeadersDao.Properties.TargetType.columnName + " = ? )";
			
			// Compute the selection arguments
			selectionArguments = new String [] {
				String.valueOf ( TargetAssignmentsUtils.Type.USER ) ,
				String.valueOf ( TargetAssignmentsUtils.Type.GROUP_USERS ) ,
				userCode ,
				String.valueOf ( TargetHeadersUtils.SubType.USER ) ,
				String.valueOf ( today ) ,
				String.valueOf ( today ) ,
				String.valueOf ( TargetHeadersUtils.Type.TOTAL_SALES )
			};
			break;
		case TargetHeadersUtils.Type.TOTAL_SALES_PER_CLIENT:
			// Retrieve the target order amounts per client
			// Compute the SQL query
			SQL = "UPDATE " + TargetAchievementsDao.TABLENAME + " " +
					"SET " + TargetAchievementsDao.Properties.AmountAcheived.columnName + " = " + TargetAchievementsDao.Properties.AmountAcheived.columnName + " " + ( type == UpdateType.INCREASE ? "+" : "-" ) + coverageAmount + " " +
					"WHERE ( TargetCode || '--' || LineID || '--' || SubjectCode || '--' || TargetDetailType || '--' || CompanyCode || '--' || DivisionCode ) IN ( " +
					"SELECT ( TD.TargetCode || '--' || TD.LineID || '--' || TD.SubSubjectCode || '--' || TD.TargetDetailTypeSub || '--' || TH.CompanyCode || '--' || TH.DivisionCode ) " +
					"FROM ( ( ( ( " + TargetHeadersDao.TABLENAME + " TH INNER JOIN " + TargetAssignmentsDao.TABLENAME + " TAS ON TH." + TargetHeadersDao.Properties.TargetCode.columnName + " = TAS." + TargetAssignmentsDao.Properties.TargetCode.columnName + " " +
					"AND ( TAS." + TargetAssignmentsDao.Properties.AssignmentType.columnName + " = ? OR TAS." + TargetAssignmentsDao.Properties.AssignmentType.columnName + " = ? ) ) " +
					"INNER JOIN " + TargetDetailsDao.TABLENAME + " TD ON TH." + TargetHeadersDao.Properties.TargetCode.columnName + " = TD." + TargetDetailsDao.Properties.TargetCode.columnName + " AND TD." + TargetDetailsDao.Properties.SubjectCode.columnName + " = ? AND TD." + TargetDetailsDao.Properties.TargetDetailType.columnName + " = ? ) " +
					"INNER JOIN " + CurrenciesDao.TABLENAME + " CU ON TD." + TargetDetailsDao.Properties.CurrencyCode.columnName + " = CU." + CurrenciesDao.Properties.CurrencyCode.columnName + " ) " +
					"INNER JOIN " + ClientsDao.TABLENAME + " C ON TD." + TargetDetailsDao.Properties.SubSubjectCode.columnName + " = C." + ClientsDao.Properties.ClientCode.columnName + " AND TH." + TargetHeadersDao.Properties.CompanyCode.columnName + " = C." + ClientsDao.Properties.CompanyCode.columnName + " " +
							"AND " + TargetDetailsDao.Properties.TargetDetailTypeSub.columnName + " = ? ) " +
					"WHERE ( ( TH." + TargetHeadersDao.Properties.StartDate.columnName + " IS NOT NULL AND ? > TH." + TargetHeadersDao.Properties.StartDate.columnName + " ) " +
					"AND ( TH." + TargetHeadersDao.Properties.EndDate.columnName + " IS NOT NULL AND ? < TH." + TargetHeadersDao.Properties.EndDate.columnName + " ) ) " +
					"AND TH." + TargetHeadersDao.Properties.TargetType.columnName + " = ? " +
					"AND TD." + TargetDetailsDao.Properties.SubSubjectCode.columnName + " = ? " +
					"AND TD." + TargetDetailsDao.Properties.TargetDetailTypeSub.columnName + " = ? " +
					"AND TH." + TargetHeadersDao.Properties.CompanyCode.columnName + " = ? " +
					"AND TH." + TargetHeadersDao.Properties.DivisionCode.columnName + " = ? ) ";
			
			// Compute the selection arguments
			selectionArguments = new String [] {
				String.valueOf ( TargetAssignmentsUtils.Type.USER ) ,
				String.valueOf ( TargetAssignmentsUtils.Type.GROUP_USERS ) ,
				userCode ,
				String.valueOf ( TargetHeadersUtils.SubType.USER ) ,
				String.valueOf ( TargetHeadersUtils.SubType.CLIENT ) ,
				String.valueOf ( today ) ,
				String.valueOf ( today ) ,
				String.valueOf ( TargetHeadersUtils.Type.TOTAL_SALES_PER_CLIENT ) ,
				subjectCode ,
				String.valueOf ( subjectType ) ,
				companyCode ,
				divisionCode
			};
			break;
		case TargetHeadersUtils.Type.TOTAL_SALES_PER_CLIENT_PER_BRAND:
			// Update the target order amounts per client per brand
			// Compute the SQL query
			SQL = "UPDATE " + TargetAchievementsDao.TABLENAME + " " +
					"SET " + TargetAchievementsDao.Properties.AmountAcheived.columnName + " = " + TargetAchievementsDao.Properties.AmountAcheived.columnName + " " + ( type == UpdateType.INCREASE ? "+" : "-" ) + coverageAmount + " " +
					"WHERE ( TargetCode || '--' || LineID || '--' || SubjectCode || '--' || TargetDetailType || '--' || CompanyCode || '--' || DivisionCode ) IN ( " +
					"SELECT ( TD.TargetCode || '--' || TD.LineID || '--' || TD.SubSubjectCode || '--' || TD.TargetDetailTypeSub || '--' || TH.CompanyCode || '--' || TH.DivisionCode ) " +
					"FROM ( ( ( ( ( ( " + TargetHeadersDao.TABLENAME + " TH INNER JOIN " + TargetAssignmentsDao.TABLENAME + " TAS ON TH." + TargetHeadersDao.Properties.TargetCode.columnName + " = TAS." + TargetAssignmentsDao.Properties.TargetCode.columnName + " " +
					"AND ( TAS." + TargetAssignmentsDao.Properties.AssignmentType.columnName + " = ? OR TAS." + TargetAssignmentsDao.Properties.AssignmentType.columnName + " = ? ) ) " +
					"INNER JOIN " + TargetDetailsDao.TABLENAME + " TD ON TH." + TargetHeadersDao.Properties.TargetCode.columnName + " = TD." + TargetDetailsDao.Properties.TargetCode.columnName + " ) " +
					"INNER JOIN " + CurrenciesDao.TABLENAME + " CU ON TD." + TargetDetailsDao.Properties.CurrencyCode.columnName + " = CU." + CurrenciesDao.Properties.CurrencyCode.columnName + " ) " +
					"INNER JOIN " + ClientsDao.TABLENAME + " C ON TD." + TargetDetailsDao.Properties.SubjectCode.columnName + " = C." + ClientsDao.Properties.ClientCode.columnName + " AND TH." + TargetHeadersDao.Properties.CompanyCode.columnName + " = C." + ClientsDao.Properties.CompanyCode.columnName + " " +
							"AND TD." + TargetDetailsDao.Properties.TargetDetailType.columnName + " = ? ) " +
					"LEFT JOIN " + ItemsDao.TABLENAME + " I ON TD." + TargetDetailsDao.Properties.SubSubjectCode.columnName + " = I." + ItemsDao.Properties.ItemCode.columnName + " AND TH." + TargetHeadersDao.Properties.CompanyCode.columnName + " = I." + ItemsDao.Properties.CompanyCode.columnName + " " +
							"AND " + TargetDetailsDao.Properties.TargetDetailTypeSub.columnName + " = ? ) " +
					"LEFT JOIN " + DivisionsDao.TABLENAME + " D ON TD." + TargetDetailsDao.Properties.SubSubjectCode.columnName + " = D." + DivisionsDao.Properties.DivisionCode.columnName + " AND TH." + TargetHeadersDao.Properties.CompanyCode.columnName + " = D." + DivisionsDao.Properties.CompanyCode.columnName + " " +
							"AND " + TargetDetailsDao.Properties.TargetDetailTypeSub.columnName + " = ? ) " +
					"WHERE ( ( TH." + TargetHeadersDao.Properties.StartDate.columnName + " IS NOT NULL AND ? > TH." + TargetHeadersDao.Properties.StartDate.columnName + " ) " +
					"AND ( TH." + TargetHeadersDao.Properties.EndDate.columnName + " IS NOT NULL AND ? < TH." + TargetHeadersDao.Properties.EndDate.columnName + " ) ) " +
					"AND TH." + TargetHeadersDao.Properties.TargetType.columnName + " = ? AND NOT ( I." + ItemsDao.Properties.ItemCode.columnName + " IS NOT NULL AND D." + DivisionsDao.Properties.DivisionCode.columnName + " IS NOT NULL ) " +
					"AND TD." + TargetDetailsDao.Properties.SubjectCode.columnName + " = ? " +
					"AND TD." + TargetDetailsDao.Properties.TargetDetailType.columnName + " = ? " +
					"AND TD." + TargetDetailsDao.Properties.SubSubjectCode.columnName + " = ? " +
					"AND TD." + TargetDetailsDao.Properties.TargetDetailTypeSub.columnName + " = ? " +
					"AND TH." + TargetHeadersDao.Properties.CompanyCode.columnName + " = ? " +
					"AND TH." + TargetHeadersDao.Properties.DivisionCode.columnName + " = ? )";
			
			// Compute the selection arguments
			selectionArguments = new String [] {
				String.valueOf ( TargetAssignmentsUtils.Type.USER ) ,
				String.valueOf ( TargetAssignmentsUtils.Type.GROUP_USERS ) ,
				String.valueOf ( TargetHeadersUtils.SubType.CLIENT ) ,
				String.valueOf ( TargetHeadersUtils.SubType.ITEM ) ,
				String.valueOf ( TargetHeadersUtils.SubType.DIVISION ) ,
				String.valueOf ( today ) ,
				String.valueOf ( today ) ,
				String.valueOf ( TargetHeadersUtils.Type.TOTAL_SALES_PER_CLIENT_PER_BRAND ) ,
				subjectCode ,
				String.valueOf ( subjectType ) ,
				subSubjectCode ,
				String.valueOf ( subSubjectType ) ,
				companyCode ,
				divisionCode
			};
			break;
		default:
			// Invalid target type
			return;
		} // End switch
		
		// Check if the SQL variables are valid
		if ( SQL == null || selectionArguments == null )
			// Invalid variables
			return;
		
		// Execute the target update query
		DatabaseUtils.getInstance ( context ).getDaoSession ().getDatabase ().execSQL ( SQL , selectionArguments );
	}
	
}