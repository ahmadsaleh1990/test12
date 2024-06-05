/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Database;

import java.util.Calendar;
import java.util.List;

import android.content.Context;


/**
 * Utilities class for generated GreenDao Entities, holding helper methods for additional functionality related to the {@link me.SyncWise.Android.Database.UserPasswords UserPasswords} objects.
 * 
 * @author Elias- Ahmad
 *
 */
public class UserPasswordsUtils {
	
	/**
	 * Constant integer holding the default time based password interval in minutes.
	 */
	public static final int DEFAULT_PASSWORD_MINUTE_INTERVAL = 5;
	
	/**
	 * Helper class used to define the various values of a transaction header type.
	 * 
	 * @author Elias
	 *
	 */
	public static class TransactionHeaderType {
		
		/**
		 * Integer holding the transaction type
		 */
		public static final int TRANSACTION = 1;
		
		/**
		 * Integer holding the movement type
		 */
		public static final int MOVEMENT = 2;
		
		/**
		 * Integer holding the collection type
		 */
		public static final int COLLECTION = 3;
		
		/**
		 * Integer holding the client stock count type
		 */
		public static final int CLIENT_STOCK_COUNT = 4;
		
	}
	
	/**
	 * Determines if the provided regular password is valid.
	 * 
	 * @param context	The application context.
	 * @param timePassword	String holding the regular password to check.
	 * @return	Boolean indicating if the provided regular password is valid.
	 */
	public static boolean validateRegularPassword ( final Context context , String regularPassword ) {
		if ( regularPassword == null )
			return false;
		String defaultPasscode = PermissionsUtils.getDefaultRegularPassword ( context , DatabaseUtils.getCurrentUserCode ( context ) , DatabaseUtils.getCurrentCompanyCode ( context ) );
		regularPassword = regularPassword.trim ();
		if ( defaultPasscode != null && ! defaultPasscode.trim ().isEmpty () ) {
			defaultPasscode = defaultPasscode.trim ();
			if ( defaultPasscode.equals ( regularPassword ) )
				return true;
		}
		List < UserPasswords > passwords = DatabaseUtils.getInstance ( context ).getDaoSession ().getUserPasswordsDao ().queryBuilder ()
				.where ( UserPasswordsDao.Properties.PasswordCode.eq ( regularPassword ) , UserPasswordsDao.Properties.IsProcessed.eq ( IsProcessedUtils.isWebProcessed () ) ).list ();
		if ( passwords.size () > 0 ) {
			for ( UserPasswords password : passwords )
				password.setIsProcessed ( IsProcessedUtils.isNotSync () );
			DatabaseUtils.getInstance ( context ).getDaoSession ().getUserPasswordsDao ().updateInTx ( passwords );
			return true;
		}
		return false;
	}
	
	/**
	 * Determines if the provided time password is valid.
	 * 
	 * @param context	The application context.
	 * @param header	Integer holding the header type.
	 * @param detail	Integer holding the detail type.
	 * @param timePassword	String holding the time password to check.
	 * @return	Boolean indicating if the provided time password is valid.
	 */
	public static boolean validateTimePassword ( final Context context , final int header , final int detail , String timePassword ) {
		if ( timePassword == null )
			return false;
		String defaultPasscode = PermissionsUtils.getDefaultTimePassword ( context , DatabaseUtils.getCurrentUserCode ( context ) , DatabaseUtils.getCurrentCompanyCode ( context ) );
		timePassword = timePassword.trim ();
		if ( defaultPasscode != null && ! defaultPasscode.trim ().isEmpty () ) {
			defaultPasscode = defaultPasscode.trim ();
			if ( defaultPasscode.equals ( timePassword ) )
				return true;
		}
		if ( timePassword.length () < 16 )
			return false;
		try {
			Users user = DatabaseUtils.getInstance ( context ).getDaoSession ().getUsersDao ().queryBuilder ()
					.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( context ) ) , UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( context ) ) ).unique ();
			int day = Integer.parseInt ( timePassword.substring ( timePassword.length () - 2 , timePassword.length () ) );
			int temp = Integer.parseInt ( timePassword.substring ( timePassword.length () - 4 , timePassword.length () - 2 ) );
			int hours = Integer.parseInt ( timePassword.substring ( timePassword.length () - 6 , timePassword.length () - 4 ) );
			int year = ( temp - hours ) + 2000;
			temp = Integer.parseInt ( timePassword.substring ( timePassword.length () - 8 , timePassword.length () - 6 ) );
			int minutes = Integer.parseInt ( timePassword.substring ( timePassword.length () - 10 , timePassword.length () - 8 ) );
			int month = temp - minutes;
			int typeDetail = Integer.parseInt ( timePassword.substring ( timePassword.length () - 11 , timePassword.length () - 10 ) );
			int typeHeader = Integer.parseInt ( timePassword.substring ( timePassword.length () - 12 , timePassword.length () - 11 ) );
			if ( typeHeader != header )
				return false;
			if ( ( typeHeader == TransactionHeaderType.TRANSACTION || typeHeader == TransactionHeaderType.MOVEMENT ) && typeDetail != detail )
				return false;
			temp = Integer.parseInt ( timePassword.substring ( 0 , timePassword.length () - 12 ) );
			if ( ( user.getPrefixID () * ( hours * 100 + minutes ) ) != temp )
				return false;
			Calendar savedTime = Calendar.getInstance ();
			Calendar allowedTime = Calendar.getInstance ();
			Calendar currentTime = Calendar.getInstance ();
			savedTime.set ( Calendar.MILLISECOND , 0 );
			currentTime.set ( Calendar.SECOND , 0 );
			currentTime.set ( Calendar.MILLISECOND , 0 );
			savedTime.set ( year , month - 1 , day , hours , minutes , 0 );
			allowedTime.setTimeInMillis ( savedTime.getTimeInMillis () );
			allowedTime.add ( Calendar.MINUTE , PermissionsUtils.getPasswordMinuteInterval ( context , user.getUserCode () , user.getCompanyCode () ) );
			long currentTimeMilli = currentTime.getTimeInMillis ();
			long savedTimeMilli = savedTime.getTimeInMillis ();
			long allowedTimeMilli = allowedTime.getTimeInMillis ();
			if ( currentTimeMilli >= savedTimeMilli && currentTimeMilli <= allowedTimeMilli )
				return true;
			else 
				return false;
		} catch ( Exception exception ) {
			// Invalid time password
			return false;
		} // End try-catch block
	}

	
	/**   Ahmad
	 * Determines if the provided time password is valid for Client.
	 * 
	 * @param context	The application context.
	 * @param header	Integer holding the header type.
	 * @param detail	Integer holding the detail type.
	 * @param timePassword	String holding the time password to check.
	 * @return	Boolean indicating if the provided time password is valid.
	 */
	public static boolean validateTimePasswordClient ( final Context context , final int header , final int detail , String timePassword,String clientCode ) {
		if ( timePassword == null )
			return false;
		String defaultPasscode = PermissionsUtils.getDefaultTimePassword ( context , DatabaseUtils.getCurrentUserCode ( context ) , DatabaseUtils.getCurrentCompanyCode ( context ) );
		timePassword = timePassword.trim ();
		if ( defaultPasscode != null && ! defaultPasscode.trim ().isEmpty () ) {
			defaultPasscode = defaultPasscode.trim ();
			if ( defaultPasscode.equals ( timePassword ) )
				return true;
		}
		if( timePassword.length() < 18 ||  ! timePassword.contains("_") )
			return false;
		    int search = timePassword.indexOf('_');
			String timePasswordnew = timePassword.substring(search+1);
			String client = timePassword.substring(0, search);
		if(client .equals( clientCode))
		{
		
		if ( timePasswordnew.length () < 16 )
			return false;
		try {
			Users user = DatabaseUtils.getInstance ( context ).getDaoSession ().getUsersDao ().queryBuilder ()
					.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( context ) ) , UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( context ) ) ).unique ();
			int day = Integer.parseInt ( timePasswordnew.substring ( timePasswordnew.length () - 2 , timePasswordnew.length () ) );
			int temp = Integer.parseInt ( timePasswordnew.substring ( timePasswordnew.length () - 4 , timePasswordnew.length () - 2 ) );
			int hours = Integer.parseInt ( timePasswordnew.substring ( timePasswordnew.length () - 6 , timePasswordnew.length () - 4 ) );
			int year = ( temp - hours ) + 2000;
			temp = Integer.parseInt ( timePasswordnew.substring ( timePasswordnew.length () - 8 , timePasswordnew.length () - 6 ) );
			int minutes = Integer.parseInt ( timePasswordnew.substring ( timePasswordnew.length () - 10 , timePasswordnew.length () - 8 ) );
			int month = temp - minutes;
			int typeDetail = Integer.parseInt ( timePasswordnew.substring ( timePasswordnew.length () - 11 , timePasswordnew.length () - 10 ) );
			int typeHeader = Integer.parseInt ( timePasswordnew.substring ( timePasswordnew.length () - 12 , timePasswordnew.length () - 11 ) );
			if ( typeHeader != header )
				return false;
			if ( ( typeHeader == TransactionHeaderType.TRANSACTION || typeHeader == TransactionHeaderType.MOVEMENT ) && typeDetail != detail )
				return false;
			temp = Integer.parseInt ( timePasswordnew.substring ( 0 , timePasswordnew.length () - 12 ) );
			if ( ( user.getPrefixID () * ( hours * 100 + minutes ) ) != temp )
				return false;
			Calendar savedTime = Calendar.getInstance ();
			Calendar allowedTime = Calendar.getInstance ();
			Calendar currentTime = Calendar.getInstance ();
			currentTime.add(Calendar.MINUTE, 1);
			savedTime.set ( Calendar.MILLISECOND , 0 );
			currentTime.set ( Calendar.SECOND , 0 );
			currentTime.set ( Calendar.MILLISECOND , 0 );
			savedTime.set ( year , month - 1 , day , hours , minutes , 0 );
			allowedTime.setTimeInMillis ( savedTime.getTimeInMillis () );
			allowedTime.add ( Calendar.MINUTE , PermissionsUtils.getPasswordMinuteInterval ( context , user.getUserCode () , user.getCompanyCode () ) );
			long currentTimeMilli = currentTime.getTimeInMillis ();
			long savedTimeMilli = savedTime.getTimeInMillis ();
			long allowedTimeMilli = allowedTime.getTimeInMillis ();
			if ( currentTimeMilli >= savedTimeMilli && currentTimeMilli <= allowedTimeMilli )
				return true;
			else 
				return false;
		} catch ( Exception exception ) {
			// Invalid time password
			return false;
		} // End try-catch block
	}
		else{
			return false;
		}
		}
//	/**   Ahmad
//	 * Determines if the provided time password is valid for Client.
//	 * 
//	 * @param context	The application context.
//	 * @param header	Integer holding the header type.
//	 * @param detail	Integer holding the detail type.
//	 * @param timePassword	String holding the time password to check.
//	 * @return	Boolean indicating if the provided time password is valid.
//	 */
//	public static boolean validateTimePasswordClients ( final Context context , final int header , final int detail , String timePassword,String clientCode ) {
//		if ( timePassword == null )
//			return false;
//		String defaultPasscode = PermissionsUtils.getDefaultTimePassword ( context , DatabaseUtils.getCurrentUserCode ( context ) , DatabaseUtils.getCurrentCompanyCode ( context ) );
//		timePassword = timePassword.trim ();
//		if ( defaultPasscode != null && ! defaultPasscode.trim ().isEmpty () ) {
//			defaultPasscode = defaultPasscode.trim ();
//			if ( defaultPasscode.equals ( timePassword ) )
//				return true;
//		}
// 
//		try {
//			Users user = DatabaseUtils.getInstance ( context ).getDaoSession ().getUsersDao ().queryBuilder ()
//					.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( context ) ) , UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( context ) ) ).unique ();
//		 
//			
//		 	if ( timePassword.length () <8 )
//				return false;
//		    String firstChar ="";
//		    String fifthChar ="";
//		    String HMPR = "";
//		    int hourMinutes = 0;
//		    String HourMinute="";
//		    String hour="",minute="";
//		    firstChar = Character.toString(clientCode.charAt(0)) ;
//		    fifthChar = Character.toString( clientCode.charAt(4));
//		    
//		    
//		    
//			String timepassFirstChar = ConvertChar(  firstChar);
//			String timepassFiftChar = ConvertChar( fifthChar);
//			String timePasswordFirstchar=Character.toString(timePassword.charAt(0));
//			String timePasswordSecondchar=Character.toString(timePassword.charAt(1));
//			if( !timepassFirstChar.equals(timePasswordFirstchar) )
//		    	return false;
//		  
//			if( !timepassFiftChar.equals(timePasswordSecondchar) )
//		    	return false;
//		  
//			HMPR = timePassword.substring(2,timePassword.length());
//		    hourMinutes=Integer.parseInt( HMPR ) / user.getPrefixID ();
//		    HourMinute = Integer.toString(hourMinutes) ;
//		   
//		    if(HourMinute .length() < 4)
//		    {
//		   	 HourMinute = "0"+hourMinutes;
//		    }
//		  
//		    hour = HourMinute.substring( 0 , 2 ); 
//		    minute = HourMinute.substring( 2 , 4 );
//		 //   System.out.println( HourMinute );
//		 //   System.out.println( hour );
//		  //  System.out.println( minute );
//		  
//		//	if ( ( user.getPrefixID () * ( hours * 100 + minutes ) ) != temp )
//		//		return false;
//			Calendar savedTime = Calendar.getInstance ();
//			Calendar allowedTime = Calendar.getInstance ();
//			Calendar currentTime = Calendar.getInstance ();
//			savedTime.set ( Calendar.MILLISECOND , 0 );
//			currentTime.set ( Calendar.SECOND , 0 );
//			currentTime.set ( Calendar.MILLISECOND , 0 );
//			currentTime.add(Calendar.MINUTE, 1);
//			//savedTime.set ( year , month - 1 , day , hours , minutes , 0 );
//			savedTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
//			savedTime.set(Calendar.MINUTE, Integer.parseInt(minute));
//			
//			
//			allowedTime.setTimeInMillis ( savedTime.getTimeInMillis () );
//			allowedTime.add ( Calendar.MINUTE , PermissionsUtils.getPasswordMinuteInterval ( context , user.getUserCode () , user.getCompanyCode () ) );
//			long currentTimeMilli = currentTime.getTimeInMillis ();
//			long savedTimeMilli = savedTime.getTimeInMillis ();
//			long allowedTimeMilli = allowedTime.getTimeInMillis ();
//			
//			if ( currentTimeMilli >= savedTimeMilli && currentTimeMilli <= allowedTimeMilli )
//				return true;
//			else 
//				return false;
//		} catch ( Exception exception ) {
//			// Invalid time password
//			return false;
//		} // End try-catch block
//	}
	/**   Ahmad
	 * Determines if the provided time password is valid for Client.
	 * 
	 * @param context	The application context.
	 * @param header	Integer holding the header type.
	 * @param detail	Integer holding the detail type.
	 * @param timePassword	String holding the time password to check.
	 * @return	Boolean indicating if the provided time password is valid.
	 */
	public static boolean validateTimePasswordClients ( final Context context , final int header , final int detail , String timePassword,String clientCode ) {
		if ( timePassword == null )
			return false;
		String defaultPasscode = PermissionsUtils.getDefaultTimePassword ( context , DatabaseUtils.getCurrentUserCode ( context ) , DatabaseUtils.getCurrentCompanyCode ( context ) );
		timePassword = timePassword.trim ();
		if ( defaultPasscode != null && ! defaultPasscode.trim ().isEmpty () ) {
			defaultPasscode = defaultPasscode.trim ();
			if ( defaultPasscode.equals ( timePassword ) )
				return true;
		}
 
		try {
			Users user = DatabaseUtils.getInstance ( context ).getDaoSession ().getUsersDao ().queryBuilder ()
					.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( context ) ) , UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( context ) ) ).unique ();
		 
			
		 	if ( timePassword.length () <6 )
				return false;
		    String firstChar ="";
		    String fifthChar ="";
		    String HMPR = "";
		    int hourMinutes = 0;
		
		    String HourMinute="";
		    String hour="",minute="";
		    firstChar = Character.toString(clientCode.charAt(0)) ;
		    fifthChar = Character.toString( clientCode.charAt(4));
		    
		    
		    
			String timepassFirstChar = ConvertChar(  firstChar);
			String timepassFiftChar = ConvertChar( fifthChar);
			String timePasswordFirstchar=Character.toString(timePassword.charAt(0));
			String timePasswordSecondchar=Character.toString(timePassword.charAt(1));
			if( !timepassFirstChar.equals(timePasswordFirstchar) )
		    	return false;
		  
			if( !timepassFiftChar.equals(timePasswordSecondchar) )
		    	return false;
		  
			Calendar cal = Calendar.getInstance();
		    int x  = 0;
		    int year = cal.get(Calendar.YEAR);
		    String years = Integer.toString(year);
		    String yearss = years.substring( 2 , 4 );
		    int yearsss = Integer.parseInt(yearss);
		    int month = cal.get(Calendar.MONTH)+1;
		    int day = cal.get(Calendar.DAY_OF_MONTH);
		    int y = yearsss * month * day;
			HMPR = timePassword.substring( 2 , timePassword.length() - 1 );
		    x = ( Integer.parseInt( HMPR ) / 11 )-user.getPrefixID () - y;
			//hourMinutes = Integer.parseInt( HMPR ) / user.getPrefixID ();
		    HourMinute = Integer.toString( x ) ;
//			HMPR = timePassword.substring(2,timePassword.length()-1);
//		    hourMinutes=Integer.parseInt( HMPR ) / user.getPrefixID ();
//		    HourMinute = Integer.toString(hourMinutes) ;
		   
		    if(HourMinute .length() < 4)
		    {
		   	 HourMinute = "0"+HourMinute;
		    }
		  
		    hour = HourMinute.substring( 0 , 2 ); 
		    minute = HourMinute.substring( 2 , 4 );
		 //   System.out.println( HourMinute );
		 //   System.out.println( hour );
		  //  System.out.println( minute );
		  
		//	if ( ( user.getPrefixID () * ( hours * 100 + minutes ) ) != temp )
		//		return false;
			Calendar savedTime = Calendar.getInstance ();
			Calendar allowedTime = Calendar.getInstance ();
			Calendar currentTime = Calendar.getInstance ();
			savedTime.set ( Calendar.MILLISECOND , 0 );
			currentTime.set ( Calendar.SECOND , 0 );
			currentTime.set ( Calendar.MILLISECOND , 0 );
			currentTime.add(Calendar.MINUTE, 1);
			//savedTime.set ( year , month - 1 , day , hours , minutes , 0 );
			savedTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
			savedTime.set(Calendar.MINUTE, Integer.parseInt(minute));
			
			
			allowedTime.setTimeInMillis ( savedTime.getTimeInMillis () );
			allowedTime.add ( Calendar.MINUTE , PermissionsUtils.getPasswordMinuteInterval ( context , user.getUserCode () , user.getCompanyCode () ) );
			long currentTimeMilli = currentTime.getTimeInMillis ();
			long savedTimeMilli = savedTime.getTimeInMillis ();
			long allowedTimeMilli = allowedTime.getTimeInMillis ();
			
			if ( currentTimeMilli >= savedTimeMilli && currentTimeMilli <= allowedTimeMilli )
				return true;
			else 
				return false;
		} catch ( Exception exception ) {
			// Invalid time password
			return false;
		} // End try-catch block
	}
//	/**   Ahmad
//	 * Determines if the provided time password is valid for Client.
//	 * 
//	 * @param context	The application context.
//	 * @param header	Integer holding the header type.
//	 * @param detail	Integer holding the detail type.
//	 * @param timePassword	String holding the time password to check.
//	 * @return	Boolean indicating if the provided time password is valid.
//	 */
//	public static boolean validateTimePasswordClientsCreditLimit ( final Context context , final int header , final int detail , String timePassword,String clientCode ) {
//		if ( timePassword == null )
//			return false;
//		String defaultPasscode = PermissionsUtils.getDefaultTimePassword ( context , DatabaseUtils.getCurrentUserCode ( context ) , DatabaseUtils.getCurrentCompanyCode ( context ) );
//		timePassword = timePassword.trim ();
//		if ( defaultPasscode != null && ! defaultPasscode.trim ().isEmpty () ) {
//			defaultPasscode = defaultPasscode.trim ();
//			if ( defaultPasscode.equals ( timePassword ) )
//				return true;
//		}
// 
//		try {
//			Users user = DatabaseUtils.getInstance ( context ).getDaoSession ().getUsersDao ().queryBuilder ()
//					.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( context ) ) , UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( context ) ) ).unique ();
//		 
//			
//		 	if ( timePassword.length () < 9 )
//				return false;
//		 	String lastChar = "";
//		    String firstChar = "";
//		    String fifthChar = "";
//		    String HMPR = "";
//		    int hourMinutes = 0;
//		    String HourMinute= "";
//		    String hour = "" , minute = "";
//		    firstChar = Character.toString(clientCode.charAt(0)) ;
//		    fifthChar = Character.toString( clientCode.charAt(4));
//		    lastChar = timePassword.substring(timePassword.length() - 1); 
//		    if( !lastChar.equals("0") )
//		    	return false;
//		    
//			String timepassFirstChar = ConvertChar(  firstChar);
//			String timepassFiftChar = ConvertChar( fifthChar);
//			String timePasswordFirstchar = Character.toString(timePassword.charAt(0));
//			String timePasswordSecondchar = Character.toString(timePassword.charAt(1));
//			if( ! timepassFirstChar.equals ( timePasswordFirstchar ) )
//		    	return false;
//		  
//			if( ! timepassFiftChar.equals( timePasswordSecondchar ) )
//		    	return false;
//		  
//			HMPR = timePassword.substring(2,timePassword.length()-1);
//		    hourMinutes=Integer.parseInt( HMPR ) / user.getPrefixID ();
//		    HourMinute = Integer.toString(hourMinutes) ;
//		   
//		    if(HourMinute .length() < 4)
//		    {
//		   	 HourMinute = "0"+hourMinutes;
//		    }
//		  
//		    hour = HourMinute.substring( 0 , 2 ); 
//		    minute = HourMinute.substring( 2 , 4 );
//		 //   System.out.println( HourMinute );
//		 //   System.out.println( hour );
//		  //  System.out.println( minute );
//		  
//		//	if ( ( user.getPrefixID () * ( hours * 100 + minutes ) ) != temp )
//		//		return false;
//			Calendar savedTime = Calendar.getInstance ();
//			Calendar allowedTime = Calendar.getInstance ();
//			Calendar currentTime = Calendar.getInstance ();
//			currentTime.add(Calendar.MINUTE, 1);
//			savedTime.set ( Calendar.MILLISECOND , 0 );
//			currentTime.set ( Calendar.SECOND , 0 );
//			currentTime.set ( Calendar.MILLISECOND , 0 );
//			//savedTime.set ( year , month - 1 , day , hours , minutes , 0 );
//			savedTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
//			savedTime.set(Calendar.MINUTE, Integer.parseInt(minute));
//			
//			//savedTime.add(Calendar.SECOND, -30); 
//		//	currentTime.add(Calendar.SECOND, -30);
//			
//			allowedTime.setTimeInMillis ( savedTime.getTimeInMillis () );
//			allowedTime.add ( Calendar.MINUTE , PermissionsUtils.getPasswordMinuteInterval ( context , user.getUserCode () , user.getCompanyCode () ) );
//			long currentTimeMilli = currentTime.getTimeInMillis ();
//			long savedTimeMilli = savedTime.getTimeInMillis ();
//			long allowedTimeMilli = allowedTime.getTimeInMillis ();
//			
//			if ( currentTimeMilli >= savedTimeMilli && currentTimeMilli <= allowedTimeMilli )
//				return true;
//			else 
//				return false;
//		} catch ( Exception exception ) {
//			// Invalid time password
//			return false;
//		} // End try-catch block
//	}		
//	/**   Ahmad
//	 * Determines if the provided time password is valid for Client.
//	 * 
//	 * @param context	The application context.
//	 * @param header	Integer holding the header type.
//	 * @param detail	Integer holding the detail type.
//	 * @param timePassword	String holding the time password to check.
//	 * @return	Boolean indicating if the provided time password is valid.
//	 */
//	public static boolean validateTimePasswordClientsCreditDays ( final Context context , final int header , final int detail , String timePassword,String clientCode ) {
//		if ( timePassword == null )
//			return false;
//		String defaultPasscode = PermissionsUtils.getDefaultTimePassword ( context , DatabaseUtils.getCurrentUserCode ( context ) , DatabaseUtils.getCurrentCompanyCode ( context ) );
//		timePassword = timePassword.trim ();
//		if ( defaultPasscode != null && ! defaultPasscode.trim ().isEmpty () ) {
//			defaultPasscode = defaultPasscode.trim ();
//			if ( defaultPasscode.equals ( timePassword ) )
//				return true;
//		}
// 
//		try {
//			Users user = DatabaseUtils.getInstance ( context ).getDaoSession ().getUsersDao ().queryBuilder ()
//					.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( context ) ) , UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( context ) ) ).unique ();
//		 
//			
//		 	if ( timePassword.length () <9 )
//				return false;
//		 	String lastChar = "";
//		    String firstChar = "";
//		    String fifthChar = "";
//		    String HMPR = "";
//		    int hourMinutes = 0;
//		    String HourMinute= "";
//		    String hour = "" , minute = "";
//		    firstChar = Character.toString(clientCode.charAt(0)) ;
//		    fifthChar = Character.toString( clientCode.charAt(4));
//		    lastChar = timePassword.substring(timePassword.length() - 1); 
//		    if(!lastChar.equals("1"))
//		    	return false;
//		  
//			String timepassFirstChar = ConvertChar(  firstChar);
//			String timepassFiftChar = ConvertChar( fifthChar);
//			String timePasswordFirstchar = Character.toString(timePassword.charAt(0));
//			String timePasswordSecondchar = Character.toString(timePassword.charAt(1));
//			if( ! timepassFirstChar.equals ( timePasswordFirstchar ) )
//		    	return false;
//		  
//			if( ! timepassFiftChar.equals( timePasswordSecondchar ) )
//		    	return false;
//		  
//			HMPR = timePassword.substring(2,timePassword.length()-1);
//		    hourMinutes=Integer.parseInt( HMPR ) / user.getPrefixID ();
//		    HourMinute = Integer.toString(hourMinutes) ;
//		   
//		    if(HourMinute .length() < 4)
//		    {
//		   	 HourMinute = "0"+hourMinutes;
//		    }
//		  
//		    hour = HourMinute.substring( 0 , 2 ); 
//		    minute = HourMinute.substring( 2 , 4 );
//		 //   System.out.println( HourMinute );
//		 //   System.out.println( hour );
//		  //  System.out.println( minute );
//		  
//		//	if ( ( user.getPrefixID () * ( hours * 100 + minutes ) ) != temp )
//		//		return false;
//			Calendar savedTime = Calendar.getInstance ();
//			Calendar allowedTime = Calendar.getInstance ();
//			Calendar currentTime = Calendar.getInstance ();
//			savedTime.set ( Calendar.MILLISECOND , 0 );
//			currentTime.add(Calendar.MINUTE, 1);
//			currentTime.set ( Calendar.SECOND , 0 );
//			currentTime.set ( Calendar.MILLISECOND , 0 );
//			//savedTime.set ( year , month - 1 , day , hours , minutes , 0 );
//			savedTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
//			savedTime.set(Calendar.MINUTE, Integer.parseInt(minute));
//			
//			
//			allowedTime.setTimeInMillis ( savedTime.getTimeInMillis () );
//			allowedTime.add ( Calendar.MINUTE , PermissionsUtils.getPasswordMinuteInterval ( context , user.getUserCode () , user.getCompanyCode () ) );
//			long currentTimeMilli = currentTime.getTimeInMillis ();
//			long savedTimeMilli = savedTime.getTimeInMillis ();
//			long allowedTimeMilli = allowedTime.getTimeInMillis ();
//			
//			if ( currentTimeMilli >= savedTimeMilli && currentTimeMilli <= allowedTimeMilli )
//				return true;
//			else 
//				return false;
//		} catch ( Exception exception ) {
//			// Invalid time password
//			return false;
//		} // End try-catch block
//	}		
	/**   Ahmad
	 * Determines if the provided time password is valid for Client.
	 * 
	 * @param context	The application context.
	 * @param header	Integer holding the header type.
	 * @param detail	Integer holding the detail type.
	 * @param timePassword	String holding the time password to check.
	 * @return	Boolean indicating if the provided time password is valid.
	 */
	public static boolean validateTimePasswordClientsCreditLimit ( final Context context , final int header , final int detail , String timePassword,String clientCode ) {
		if ( timePassword == null )
			return false;
		String defaultPasscode = PermissionsUtils.getDefaultTimePassword ( context , DatabaseUtils.getCurrentUserCode ( context ) , DatabaseUtils.getCurrentCompanyCode ( context ) );
		timePassword = timePassword.trim ();
		if ( defaultPasscode != null && ! defaultPasscode.trim ().isEmpty () ) {
			defaultPasscode = defaultPasscode.trim ();
			if ( defaultPasscode.equals ( timePassword ) )
				return true;
		}
 
		try {
			Users user = DatabaseUtils.getInstance ( context ).getDaoSession ().getUsersDao ().queryBuilder ()
					.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( context ) ) , UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( context ) ) ).unique ();
		 
			
		 	if ( timePassword.length () < 7 )
				return false;
		 	String lastChar = "";
		    String firstChar = "";
		    String fifthChar = "";
		    String HMPR = "";
		    int hourMinutes = 0;
		    String HourMinute= "";
		    String hour = "" , minute = "";
		    firstChar = Character.toString(clientCode.charAt(0)) ;
		    fifthChar = Character.toString( clientCode.charAt(4));
		    lastChar = timePassword.substring(timePassword.length() - 1); 
		    if( !lastChar.equals("0") )
		    	return false;
		    
			String timepassFirstChar = ConvertChar(  firstChar);
			String timepassFiftChar = ConvertChar( fifthChar);
			String timePasswordFirstchar = Character.toString(timePassword.charAt(0));
			String timePasswordSecondchar = Character.toString(timePassword.charAt(1));
			if( ! timepassFirstChar.equals ( timePasswordFirstchar ) )
		    	return false;
		  
			if( ! timepassFiftChar.equals( timePasswordSecondchar ) )
		    	return false;


			
			Calendar cal = Calendar.getInstance();
		    int x  = 0;
		    int year = cal.get(Calendar.YEAR);
		    String years = Integer.toString(year);
		    String yearss = years.substring( 2 , 4 );
		    int yearsss = Integer.parseInt(yearss);
		    int month = cal.get(Calendar.MONTH)+1;
		    int day = cal.get(Calendar.DAY_OF_MONTH);
		    int y = yearsss * month * day;
			HMPR = timePassword.substring( 2 , timePassword.length() - 1 );
		    x = ( Integer.parseInt( HMPR ) / 11 )-user.getPrefixID () - y;
			//hourMinutes = Integer.parseInt( HMPR ) / user.getPrefixID ();
		    HourMinute = Integer.toString( x ) ;
//			HMPR = timePassword.substring(2,timePassword.length()-1);
//		    hourMinutes=Integer.parseInt( HMPR ) / user.getPrefixID ();
//		    HourMinute = Integer.toString(hourMinutes) ;
		   
		    if(HourMinute .length() < 4)
		    {
		   	 HourMinute = "0"+HourMinute;
		    }
		  
		    hour = HourMinute.substring( 0 , 2 ); 
		    minute = HourMinute.substring( 2 , 4 );
		 //   System.out.println( HourMinute );
		 //   System.out.println( hour );
		  //  System.out.println( minute );
		  
		//	if ( ( user.getPrefixID () * ( hours * 100 + minutes ) ) != temp )
		//		return false;
			Calendar savedTime = Calendar.getInstance ();
			Calendar allowedTime = Calendar.getInstance ();
			Calendar currentTime = Calendar.getInstance ();
			currentTime.add(Calendar.MINUTE, 1);
			savedTime.set ( Calendar.MILLISECOND , 0 );
			currentTime.set ( Calendar.SECOND , 0 );
			currentTime.set ( Calendar.MILLISECOND , 0 );
			//savedTime.set ( year , month - 1 , day , hours , minutes , 0 );
			savedTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
			savedTime.set(Calendar.MINUTE, Integer.parseInt(minute));
			
			//savedTime.add(Calendar.SECOND, -30); 
		//	currentTime.add(Calendar.SECOND, -30);
			
			allowedTime.setTimeInMillis ( savedTime.getTimeInMillis () );
			allowedTime.add ( Calendar.MINUTE , PermissionsUtils.getPasswordMinuteInterval ( context , user.getUserCode () , user.getCompanyCode () ) );
			long currentTimeMilli = currentTime.getTimeInMillis ();
			long savedTimeMilli = savedTime.getTimeInMillis ();
			long allowedTimeMilli = allowedTime.getTimeInMillis ();
			
			if ( currentTimeMilli >= savedTimeMilli && currentTimeMilli <= allowedTimeMilli )
				return true;
			else 
				return false;
		} catch ( Exception exception ) {
			// Invalid time password
			return false;
		} // End try-catch block
	}		
	/**   Ahmad
	 * Determines if the provided time password is valid for Client.
	 * 
	 * @param context	The application context.
	 * @param header	Integer holding the header type.
	 * @param detail	Integer holding the detail type.
	 * @param timePassword	String holding the time password to check.
	 * @return	Boolean indicating if the provided time password is valid.
	 */
	public static boolean validateTimePasswordClientsCreditDays ( final Context context , final int header , final int detail , String timePassword,String clientCode ) {
		if ( timePassword == null )
			return false;
		String defaultPasscode = PermissionsUtils.getDefaultTimePassword ( context , DatabaseUtils.getCurrentUserCode ( context ) , DatabaseUtils.getCurrentCompanyCode ( context ) );
		timePassword = timePassword.trim ();
		if ( defaultPasscode != null && ! defaultPasscode.trim ().isEmpty () ) {
			defaultPasscode = defaultPasscode.trim ();
			if ( defaultPasscode.equals ( timePassword ) )
				return true;
		}
 
		try {
			Users user = DatabaseUtils.getInstance ( context ).getDaoSession ().getUsersDao ().queryBuilder ()
					.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( context ) ) , UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( context ) ) ).unique ();
		 
			
		 	if ( timePassword.length () <6 )
				return false;
		 	String lastChar = "";
		    String firstChar = "";
		    String fifthChar = "";
		    String HMPR = "";
		    int hourMinutes = 0;
		    String HourMinute= "";
		    String hour = "" , minute = "";
		    firstChar = Character.toString(clientCode.charAt(0)) ;
		    fifthChar = Character.toString( clientCode.charAt(4));
		    lastChar = timePassword.substring(timePassword.length() - 1); 
		    if(!lastChar.equals("1"))
		    	return false;
		  
			String timepassFirstChar = ConvertChar(  firstChar);
			String timepassFiftChar = ConvertChar( fifthChar);
			String timePasswordFirstchar = Character.toString(timePassword.charAt(0));
			String timePasswordSecondchar = Character.toString(timePassword.charAt(1));
			if( ! timepassFirstChar.equals ( timePasswordFirstchar ) )
		    	return false;
		  
			if( ! timepassFiftChar.equals( timePasswordSecondchar ) )
		    	return false;
		  
			Calendar cal = Calendar.getInstance();
		    int x  = 0;
		    int year = cal.get(Calendar.YEAR);
		    String years = Integer.toString(year);
		    String yearss = years.substring( 2 , 4 );
		    int yearsss = Integer.parseInt(yearss);
		    int month = cal.get(Calendar.MONTH)+1;
		    int day = cal.get(Calendar.DAY_OF_MONTH);
		    int y = yearsss * month * day;
			HMPR = timePassword.substring( 2 , timePassword.length() - 1 );
		    x = ( Integer.parseInt( HMPR ) / 11 )-user.getPrefixID () - y;
			//hourMinutes = Integer.parseInt( HMPR ) / user.getPrefixID ();
		    HourMinute = Integer.toString( x ) ;
//			HMPR = timePassword.substring(2,timePassword.length()-1);
//		    hourMinutes=Integer.parseInt( HMPR ) / user.getPrefixID ();
//		    HourMinute = Integer.toString(hourMinutes) ;
		   
		    if(HourMinute .length() < 4)
		    {
		   	 HourMinute = "0"+HourMinute;
		    }
		  
		    hour = HourMinute.substring( 0 , 2 ); 
		    minute = HourMinute.substring( 2 , 4 );
		 //   System.out.println( HourMinute );
		 //   System.out.println( hour );
		  //  System.out.println( minute );
		  
		//	if ( ( user.getPrefixID () * ( hours * 100 + minutes ) ) != temp )
		//		return false;
			Calendar savedTime = Calendar.getInstance ();
			Calendar allowedTime = Calendar.getInstance ();
			Calendar currentTime = Calendar.getInstance ();
			savedTime.set ( Calendar.MILLISECOND , 0 );
			currentTime.add(Calendar.MINUTE, 1);
			currentTime.set ( Calendar.SECOND , 0 );
			currentTime.set ( Calendar.MILLISECOND , 0 );
			//savedTime.set ( year , month - 1 , day , hours , minutes , 0 );
			savedTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
			savedTime.set(Calendar.MINUTE, Integer.parseInt(minute));
			
			
			allowedTime.setTimeInMillis ( savedTime.getTimeInMillis () );
			allowedTime.add ( Calendar.MINUTE , PermissionsUtils.getPasswordMinuteInterval ( context , user.getUserCode () , user.getCompanyCode () ) );
			long currentTimeMilli = currentTime.getTimeInMillis ();
			long savedTimeMilli = savedTime.getTimeInMillis ();
			long allowedTimeMilli = allowedTime.getTimeInMillis ();
			
			if ( currentTimeMilli >= savedTimeMilli && currentTimeMilli <= allowedTimeMilli )
				return true;
			else 
				return false;
		} catch ( Exception exception ) {
			// Invalid time password
			return false;
		} // End try-catch block
	}		
	private static String ConvertChar(String a)
	  {
	   if ( a.equals( "a") || a.equals("A"))
		  return "1";
	   if ( a.equals("b") || a.equals( "B"))
		   return "2";
	   if( a .equals("c") || a.equals( "C"))
		   return "3";
	   if ( a .equals("d") || a.equals( "D"))
		   return "4";
	   if(a .equals("e") || a.equals( "E"))
		   return "5";
	   if  ( a.equals( "f") || a.equals( "F"))
		   return "6";
	   if( a.equals( "g") || a.equals( "G"))
		   return "7";
	   if( a.equals( "h") || a.equals( "H"))
		   return "8";
	   
	   if( a.equals( "i") || a.equals( "I"))
		   return "9";
	   if( a.equals( "J" ) || a.equals( "J"))
		   return "0"; 
	   if( a.equals( "k" ) || a.equals( "K"))
		   return "1";
	  
	   if( a.equals( "l" ) || a.equals("L"))
		   return "2";
	   if( a.equals( "m" ) || a.equals( "M"))
		   return "3";
	   if( a.equals( "n" ) || a.equals( "N"))
		   return "4";
	   if( a.equals( "o" ) || a.equals( "O"))
		   return "5";
	   if( a.equals( "p" ) || a.equals( "P" ))
		   return "6";
	   if( a.equals( "q" ) || a.equals( "Q"))
		   return "7";
	   if( a.equals( "r" ) || a.equals( "R"))
		   return "8";
	   if( a.equals( "s" ) || a.equals( "S"))
		   return "9";
	   if( a.equals("t" ) || a.equals( "T"))
			return "0";
	   if( a.equals("u" ) || a.equals( "U"))
			 return "1";
	   if( a.equals( "v" ) || a.equals( "V"))
			return "2";
	   if( a.equals( "w" ) || a.equals( "W"))
			return "3";
	   if( a.equals( "x" ) || a.equals( "X"))
			return "4";
	   if( a.equals( "y" ) || a.equals( "Y"))
			return "5";
	   if( a.equals( "z" ) || a.equals( "Z"))
			return "6";
	  return a; 
	  }
	
//	/**  Ahmad
//	 * Determines if the provided time password is valid for User.
//	 * 
//	 * @param context	The application context.
//	 * @param header	Integer holding the header type.
//	 * @param detail	Integer holding the detail type.
//	 * @param timePassword	String holding the time password to check.
//	 * @param userCode   String holding the userCode
//	 * @return	Boolean indicating if the provided time password is valid.
//	 */
//	public static boolean validateTimePasswordUsers ( final Context context , final int header , final int detail , String timePassword,String userCode ) {
//		if ( timePassword == null )
//			return false;
//		String defaultPasscode = PermissionsUtils.getDefaultTimePassword ( context , DatabaseUtils.getCurrentUserCode ( context ) , DatabaseUtils.getCurrentCompanyCode ( context ) );
//		timePassword = timePassword.trim ();
//		if ( defaultPasscode != null && ! defaultPasscode.trim ().isEmpty () ) {
//			defaultPasscode = defaultPasscode.trim ();
//			if ( defaultPasscode.equals ( timePassword ) )
//				return true;
//		}
//		if ( timePassword.length () <8 )
//			return false;
//		
//		   
//		try {
//			Users user = DatabaseUtils.getInstance ( context ).getDaoSession ().getUsersDao ().queryBuilder ()
//					.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( context ) ) , UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( context ) ) ).unique ();
//			  String firstChar ="";
//			    String fifthChar ="";
//			    String HMPR = "";
//			    int hourMinutes = 0;
//			    String HourMinute="";
//			    String hour="",minute="";
//			    firstChar = Character.toString(userCode.charAt(0)) ;
//			    fifthChar = Character.toString( userCode.charAt(1));
//			    
//			    
//			    
//				String timepassFirstChar = ConvertChar(  firstChar);
//				String timepassFiftChar = ConvertChar( fifthChar);
//				String timePasswordFirstchar=Character.toString(timePassword.charAt(0));
//				String timePasswordSecondchar=Character.toString(timePassword.charAt(1));
//				if( !timepassFirstChar.equals(timePasswordFirstchar) )
//			    	return false;
//			  
//				if( !timepassFiftChar.equals(timePasswordSecondchar) )
//			    	return false;
//			  
//				HMPR = timePassword.substring(2,timePassword.length());
//			    hourMinutes=Integer.parseInt( HMPR ) / user.getPrefixID ();
//			    HourMinute = Integer.toString(hourMinutes) ;
//			   
//			    if(HourMinute .length() < 4)
//			    {
//			   	 HourMinute = "0"+hourMinutes;
//			    }
//			  
//			    hour = HourMinute.substring( 0 , 2 ); 
//			    minute = HourMinute.substring( 2 , 4 );
//			 //   System.out.println( HourMinute );
//			 //   System.out.println( hour );
//			  //  System.out.println( minute );
//			  
//			//	if ( ( user.getPrefixID () * ( hours * 100 + minutes ) ) != temp )
//			//		return false;
//				Calendar savedTime = Calendar.getInstance ();
//				Calendar allowedTime = Calendar.getInstance ();
//				Calendar currentTime = Calendar.getInstance ();
//				savedTime.set ( Calendar.MILLISECOND , 0 );
//				currentTime.set ( Calendar.SECOND , 0 );
//				currentTime.set ( Calendar.MILLISECOND , 0 );
//				//savedTime.set ( year , month - 1 , day , hours , minutes , 0 );
//				savedTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
//				savedTime.set(Calendar.MINUTE, Integer.parseInt(minute));
//				currentTime.add(Calendar.MINUTE, 1);
//				
//				allowedTime.setTimeInMillis ( savedTime.getTimeInMillis () );
//				allowedTime.add ( Calendar.MINUTE , PermissionsUtils.getPasswordMinuteInterval ( context , user.getUserCode () , user.getCompanyCode () ) );
//				long currentTimeMilli = currentTime.getTimeInMillis ();
//				long savedTimeMilli = savedTime.getTimeInMillis ();
//				long allowedTimeMilli = allowedTime.getTimeInMillis ();
//				
//				if ( currentTimeMilli >= savedTimeMilli && currentTimeMilli <= allowedTimeMilli )
//					return true;
//				else 
//					return false;
//			} catch ( Exception exception ) {
//				// Invalid time password
//				return false;
//			} // End try-catch block
//		}
	
	
	/**  Ahmad
	 * Determines if the provided time password is valid for User.
	 * 
	 * @param context	The application context.
	 * @param header	Integer holding the header type.
	 * @param detail	Integer holding the detail type.
	 * @param timePassword	String holding the time password to check.
	 * @param userCode   String holding the userCode
	 * @return	Boolean indicating if the provided time password is valid.
	 */
	public static boolean validateTimePasswordUsers ( final Context context , final int header , final int detail , String timePassword,String userCode ) {
		if ( timePassword == null )
			return false;
		String defaultPasscode = PermissionsUtils.getDefaultTimePassword ( context , DatabaseUtils.getCurrentUserCode ( context ) , DatabaseUtils.getCurrentCompanyCode ( context ) );
		timePassword = timePassword.trim ();
		if ( defaultPasscode != null && ! defaultPasscode.trim ().isEmpty () ) {
			defaultPasscode = defaultPasscode.trim ();
			if ( defaultPasscode.equals ( timePassword ) )
				return true;
		}
		if ( timePassword.length () <5 )
			return false;
		
		   
		try {
			Users user = DatabaseUtils.getInstance ( context ).getDaoSession ().getUsersDao ().queryBuilder ()
					.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( context ) ) , UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( context ) ) ).unique ();
			  String firstChar ="";
			    String fifthChar ="";
			    String HMPR = "";
			    int hourMinutes = 0;
			    String HourMinute="";
			    String hour="",minute="";
			    firstChar = Character.toString(userCode.charAt(0)) ;
			    fifthChar = Character.toString( userCode.charAt(1));
			    
			    
			    
				String timepassFirstChar = ConvertChar(  firstChar);
				String timepassFiftChar = ConvertChar( fifthChar);
				String timePasswordFirstchar=Character.toString(timePassword.charAt(0));
				String timePasswordSecondchar=Character.toString(timePassword.charAt(1));
				if( !timepassFirstChar.equals(timePasswordFirstchar) )
			    	return false;
			  
				if( !timepassFiftChar.equals(timePasswordSecondchar) )
			    	return false;
				  
				Calendar cal = Calendar.getInstance();
			    int x  = 0;
			    int year = cal.get(Calendar.YEAR);
			    String years = Integer.toString(year);
			    String yearss = years.substring( 2 , 4 );
			    int yearsss = Integer.parseInt(yearss);
			    int month = cal.get(Calendar.MONTH)+1;
			    int day = cal.get(Calendar.DAY_OF_MONTH);
			    int y = yearsss * month * day;
				HMPR = timePassword.substring( 2 , timePassword.length() );
			    x = ( Integer.parseInt( HMPR ) / 11 ) - user.getPrefixID () - y;
				//hourMinutes = Integer.parseInt( HMPR ) / user.getPrefixID ();
			    HourMinute = Integer.toString( x ) ;
//				HMPR = timePassword.substring(2,timePassword.length()-1);
//			    hourMinutes=Integer.parseInt( HMPR ) / user.getPrefixID ();
//			    HourMinute = Integer.toString(hourMinutes) ;
			   
			    if(HourMinute .length() < 4)
			    {
			   	 HourMinute = "0"+HourMinute;
			    }
			    hour = HourMinute.substring( 0 , 2 ); 
			    minute = HourMinute.substring( 2 , 4 );
			 
				Calendar savedTime = Calendar.getInstance ();
				Calendar allowedTime = Calendar.getInstance ();
				Calendar currentTime = Calendar.getInstance ();
				savedTime.set ( Calendar.MILLISECOND , 0 );
				currentTime.set ( Calendar.SECOND , 0 );
				currentTime.set ( Calendar.MILLISECOND , 0 );
				//savedTime.set ( year , month - 1 , day , hours , minutes , 0 );
				savedTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
				savedTime.set(Calendar.MINUTE, Integer.parseInt(minute));
				currentTime.add(Calendar.MINUTE, 1);
				
				allowedTime.setTimeInMillis ( savedTime.getTimeInMillis () );
				allowedTime.add ( Calendar.MINUTE , PermissionsUtils.getPasswordMinuteInterval ( context , user.getUserCode () , user.getCompanyCode () ) );
				long currentTimeMilli = currentTime.getTimeInMillis ();
				long savedTimeMilli = savedTime.getTimeInMillis ();
				long allowedTimeMilli = allowedTime.getTimeInMillis ();
				
				if ( currentTimeMilli >= savedTimeMilli && currentTimeMilli <= allowedTimeMilli )
					return true;
				else 
					return false;
			} catch ( Exception exception ) {
				// Invalid time password
				return false;
			} // End try-catch block
		}
	public static boolean validateTimePasswordUsers12 ( final Context context , final int header , final int detail , String timePassword,String userCode ) {
		if ( timePassword == null )
			return false;
		String defaultPasscode = PermissionsUtils.getDefaultTimePassword1 ( context , DatabaseUtils.getCurrentUserCode ( context ) , DatabaseUtils.getCurrentCompanyCode ( context ) );
		timePassword = timePassword.trim ();
		if ( defaultPasscode != null && ! defaultPasscode.trim ().isEmpty () ) {
			defaultPasscode = defaultPasscode.trim ();
			if ( defaultPasscode.equals ( timePassword ) )
				return true;
		}
		if ( timePassword.length () <5 )
			return false;
		
		   
		try {
			Users user = DatabaseUtils.getInstance ( context ).getDaoSession ().getUsersDao ().queryBuilder ()
					.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( context ) ) , UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( context ) ) ).unique ();
			  String firstChar ="";
			    String fifthChar ="";
			    String HMPR = "";
			    int hourMinutes = 0;
			    String HourMinute="";
			    String hour="",minute="";
			    firstChar = Character.toString(userCode.charAt(0)) ;
			    fifthChar = Character.toString( userCode.charAt(1));
			    
			    
			    
				String timepassFirstChar = ConvertChar(  firstChar);
				String timepassFiftChar = ConvertChar( fifthChar);
				String timePasswordFirstchar=Character.toString(timePassword.charAt(0));
				String timePasswordSecondchar=Character.toString(timePassword.charAt(1));
				if( !timepassFirstChar.equals(timePasswordFirstchar) )
			    	return false;
			  
				if( !timepassFiftChar.equals(timePasswordSecondchar) )
			    	return false;
				  
				Calendar cal = Calendar.getInstance();
			    int x  = 0;
			    int year = cal.get(Calendar.YEAR);
			    String years = Integer.toString(year);
			    String yearss = years.substring( 2 , 4 );
			    int yearsss = Integer.parseInt(yearss);
			    int month = cal.get(Calendar.MONTH)+1;
			    int day = cal.get(Calendar.DAY_OF_MONTH);
			    int y = yearsss * month * day;
				HMPR = timePassword.substring( 2 , timePassword.length() );
			    x = ( Integer.parseInt( HMPR ) / 11 ) - user.getPrefixID () - y;
				//hourMinutes = Integer.parseInt( HMPR ) / user.getPrefixID ();
			    HourMinute = Integer.toString( x ) ;
//				HMPR = timePassword.substring(2,timePassword.length()-1);
//			    hourMinutes=Integer.parseInt( HMPR ) / user.getPrefixID ();
//			    HourMinute = Integer.toString(hourMinutes) ;
			   
			    if(HourMinute .length() < 4)
			    {
			   	 HourMinute = "0"+HourMinute;
			    }
			    hour = HourMinute.substring( 0 , 2 ); 
			    minute = HourMinute.substring( 2 , 4 );
			 
				Calendar savedTime = Calendar.getInstance ();
				Calendar allowedTime = Calendar.getInstance ();
				Calendar currentTime = Calendar.getInstance ();
				savedTime.set ( Calendar.MILLISECOND , 0 );
				currentTime.set ( Calendar.SECOND , 0 );
				currentTime.set ( Calendar.MILLISECOND , 0 );
				//savedTime.set ( year , month - 1 , day , hours , minutes , 0 );
				savedTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
				savedTime.set(Calendar.MINUTE, Integer.parseInt(minute));
				currentTime.add(Calendar.MINUTE, 1);
				
				allowedTime.setTimeInMillis ( savedTime.getTimeInMillis () );
				allowedTime.add ( Calendar.MINUTE , PermissionsUtils.getPasswordMinuteInterval ( context , user.getUserCode () , user.getCompanyCode () ) );
				long currentTimeMilli = currentTime.getTimeInMillis ();
				long savedTimeMilli = savedTime.getTimeInMillis ();
				long allowedTimeMilli = allowedTime.getTimeInMillis ();
				
				if ( currentTimeMilli >= savedTimeMilli && currentTimeMilli <= allowedTimeMilli )
					return true;
				else 
					return false;
			} catch ( Exception exception ) {
				// Invalid time password
				return false;
			} // End try-catch block
		}
	/**  Ahmad
	 * Determines if the provided time password is valid for User.
	 * 
	 * @param context	The application context.
	 * @param header	Integer holding the header type.
	 * @param detail	Integer holding the detail type.
	 * @param timePassword	String holding the time password to check.
	 * @param userCode   String holding the userCode
	 * @return	Boolean indicating if the provided time password is valid.
	 */
	public static boolean validateTimePasswordUsers1 ( final Context context , final int header , final int detail , String timePassword,String userCode ) {
		if ( timePassword == null )
			return false;
		String defaultPasscode = PermissionsUtils.getDefaultTimePassword ( context , DatabaseUtils.getCurrentUserCode ( context ) , DatabaseUtils.getCurrentCompanyCode ( context ) );
		timePassword = timePassword.trim ();
		if ( defaultPasscode != null && ! defaultPasscode.trim ().isEmpty () ) {
			defaultPasscode = defaultPasscode.trim ();
			if ( defaultPasscode.equals ( timePassword ) )
				return true;
		}
		if ( timePassword.length () <7 )
			return false;
		
		   
		try {
			Users user = DatabaseUtils.getInstance ( context ).getDaoSession ().getUsersDao ().queryBuilder ()
					.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( context ) ) , UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( context ) ) ).unique ();
			  String firstChar ="";
			    String fifthChar ="";
			    String HMPR = "";
			    int hourMinutes = 0;
			    String HourMinute="";
			    String hour="",minute="";
			    firstChar = Character.toString(userCode.charAt(0)) ;
			    fifthChar = Character.toString( userCode.charAt(1));
			    
			    
			    
				String timepassFirstChar = ConvertChar(  firstChar);
				String timepassFiftChar = ConvertChar( fifthChar);
				String timePasswordFirstchar=Character.toString(timePassword.charAt(0));
				String timePasswordSecondchar=Character.toString(timePassword.charAt(1));
				if( !timepassFirstChar.equals(timePasswordFirstchar) )
			    	return false;
			  
				if( !timepassFiftChar.equals(timePasswordSecondchar) )
			    	return false;
			  
				HMPR = timePassword.substring(2,timePassword.length());
			    hourMinutes=Integer.parseInt( HMPR ) / user.getPrefixID ();
			    HourMinute = Integer.toString(hourMinutes) ;
			   
			    if(HourMinute .length() < 4)
			    {
			   	 HourMinute = "0"+hourMinutes;
			    }
			  
			    hour = HourMinute.substring( 0 , 2 ); 
			    minute = HourMinute.substring( 2 , 4 );
			 //   System.out.println( HourMinute );
			 //   System.out.println( hour );
			  //  System.out.println( minute );
			  
			//	if ( ( user.getPrefixID () * ( hours * 100 + minutes ) ) != temp )
			//		return false;
				Calendar savedTime = Calendar.getInstance ();
				Calendar allowedTime = Calendar.getInstance ();
				Calendar currentTime = Calendar.getInstance ();
				savedTime.set ( Calendar.MILLISECOND , 0 );
				currentTime.set ( Calendar.SECOND , 0 );
				currentTime.set ( Calendar.MILLISECOND , 0 );
				//savedTime.set ( year , month - 1 , day , hours , minutes , 0 );
				savedTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
				savedTime.set(Calendar.MINUTE, Integer.parseInt(minute));
				currentTime.add(Calendar.MINUTE, 1);
				
				allowedTime.setTimeInMillis ( savedTime.getTimeInMillis () );
				allowedTime.add ( Calendar.MINUTE , PermissionsUtils.getPasswordMinuteInterval ( context , user.getUserCode () , user.getCompanyCode () ) );
				long currentTimeMilli = currentTime.getTimeInMillis ();
				long savedTimeMilli = savedTime.getTimeInMillis ();
				long allowedTimeMilli = allowedTime.getTimeInMillis ();
				
				if ( currentTimeMilli >= savedTimeMilli && currentTimeMilli <= allowedTimeMilli )
					return true;
				else 
					return false;
			} catch ( Exception exception ) {
				// Invalid time password
				return false;
			} // End try-catch block
		}
	
	/**  Ahmad
	 * Determines if the provided time password is valid for User.
	 * 
	 * @param context	The application context.
	 * @param header	Integer holding the header type.
	 * @param detail	Integer holding the detail type.
	 * @param timePassword	String holding the time password to check.
	 * @param userCode   String holding the userCode
	 * @return	Boolean indicating if the provided time password is valid.
	 */
	public static boolean validateTimePasswordUser ( final Context context , final int header , final int detail , String timePassword,String userCode ) {
		if ( timePassword == null )
			return false;
		String defaultPasscode = PermissionsUtils.getDefaultTimePassword ( context , DatabaseUtils.getCurrentUserCode ( context ) , DatabaseUtils.getCurrentCompanyCode ( context ) );
		timePassword = timePassword.trim ();
		if ( defaultPasscode != null && ! defaultPasscode.trim ().isEmpty () ) {
			defaultPasscode = defaultPasscode.trim ();
			if ( defaultPasscode.equals ( timePassword ) )
				return true;
		}
		if(timePassword.length() < 18 || !timePassword.contains("_"))
			return false;
		
		    int search = timePassword.indexOf('_');
			String timePasswordnew = timePassword.substring(search+1);
			String users = timePassword.substring(0, search);
		if(users.equals(userCode))
		{
		
		if ( timePasswordnew.length () < 16 )
			return false;
		try {
			Users user = DatabaseUtils.getInstance ( context ).getDaoSession ().getUsersDao ().queryBuilder ()
					.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( context ) ) , UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( context ) ) ).unique ();
			int day = Integer.parseInt ( timePasswordnew.substring ( timePasswordnew.length () - 2 , timePasswordnew.length () ) );
			int temp = Integer.parseInt ( timePasswordnew.substring ( timePasswordnew.length () - 4 , timePasswordnew.length () - 2 ) );
			int hours = Integer.parseInt ( timePasswordnew.substring ( timePasswordnew.length () - 6 , timePasswordnew.length () - 4 ) );
			int year = ( temp - hours ) + 2000;
			temp = Integer.parseInt ( timePasswordnew.substring ( timePasswordnew.length () - 8 , timePasswordnew.length () - 6 ) );
			int minutes = Integer.parseInt ( timePasswordnew.substring ( timePasswordnew.length () - 10 , timePasswordnew.length () - 8 ) );
			int month = temp - minutes;
			int typeDetail = Integer.parseInt ( timePasswordnew.substring ( timePasswordnew.length () - 11 , timePasswordnew.length () - 10 ) );
			int typeHeader = Integer.parseInt ( timePasswordnew.substring ( timePasswordnew.length () - 12 , timePasswordnew.length () - 11 ) );
			if ( typeHeader != header )
				return false;
			if ( ( typeHeader == TransactionHeaderType.TRANSACTION || typeHeader == TransactionHeaderType.MOVEMENT ) && typeDetail != detail )
				return false;
			temp = Integer.parseInt ( timePasswordnew.substring ( 0 , timePasswordnew.length () - 12 ) );
			if ( ( user.getPrefixID () * ( hours * 100 + minutes ) ) != temp )
				return false;
			Calendar savedTime = Calendar.getInstance ();
			Calendar allowedTime = Calendar.getInstance ();
			Calendar currentTime = Calendar.getInstance ();
			savedTime.set ( Calendar.MILLISECOND , 0 );
			currentTime.set ( Calendar.SECOND , 0 );
			currentTime.add(Calendar.MINUTE, 1);
			currentTime.set ( Calendar.MILLISECOND , 0 );
			savedTime.set ( year , month - 1 , day , hours , minutes , 0 );
			allowedTime.setTimeInMillis ( savedTime.getTimeInMillis () );
			allowedTime.add ( Calendar.MINUTE , PermissionsUtils.getPasswordMinuteInterval ( context , user.getUserCode () , user.getCompanyCode () ) );
			long currentTimeMilli = currentTime.getTimeInMillis ();
			long savedTimeMilli = savedTime.getTimeInMillis ();
			long allowedTimeMilli = allowedTime.getTimeInMillis ();
			if ( currentTimeMilli >= savedTimeMilli && currentTimeMilli <= allowedTimeMilli )
				return true;
			else 
				return false;
		} catch ( Exception exception ) {
			// Invalid time password
			return false;
		} // End try-catch block
	}
		else{
			return false;
		}
		}
	public static boolean validateTimePasswordUsers12345 ( final Context context , final int header , final int detail , String timePassword,String userCode ) {
		if ( timePassword == null )
			return false;
		String defaultPasscode = PermissionsUtils.getJourneyTimePassword ( context , DatabaseUtils.getCurrentUserCode ( context ) , DatabaseUtils.getCurrentCompanyCode ( context ) );
		timePassword = timePassword.trim ();
		if ( defaultPasscode != null && ! defaultPasscode.trim ().isEmpty () ) {
			defaultPasscode = defaultPasscode.trim ();
			if ( defaultPasscode.equals ( timePassword ) )
				return true;
		}
		if ( timePassword.length () <5 )
			return false;
		
		   
		try {
			Users user = DatabaseUtils.getInstance ( context ).getDaoSession ().getUsersDao ().queryBuilder ()
					.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( context ) ) , UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( context ) ) ).unique ();
			  String firstChar ="";
			    String fifthChar ="";
			    String HMPR = "";
			    int hourMinutes = 0;
			    String HourMinute="";
			    String hour="",minute="";
			    firstChar = Character.toString(userCode.charAt(0)) ;
			    fifthChar = Character.toString( userCode.charAt(1));
			    
			    
			    
				String timepassFirstChar = ConvertChar(  firstChar);
				String timepassFiftChar = ConvertChar( fifthChar);
				String timePasswordFirstchar=Character.toString(timePassword.charAt(0));
				String timePasswordSecondchar=Character.toString(timePassword.charAt(1));
				if( !timepassFirstChar.equals(timePasswordFirstchar) )
			    	return false;
			  
				if( !timepassFiftChar.equals(timePasswordSecondchar) )
			    	return false;
				  
				Calendar cal = Calendar.getInstance();
			    int x  = 0;
			    int year = cal.get(Calendar.YEAR);
			    String years = Integer.toString(year);
			    String yearss = years.substring( 2 , 4 );
			    int yearsss = Integer.parseInt(yearss);
			    int month = cal.get(Calendar.MONTH)+1;
			    int day = cal.get(Calendar.DAY_OF_MONTH);
			    int y = yearsss * month * day;
				HMPR = timePassword.substring( 2 , timePassword.length() );
			    x = ( Integer.parseInt( HMPR ) / 11 ) - user.getPrefixID () - y;
				//hourMinutes = Integer.parseInt( HMPR ) / user.getPrefixID ();
			    HourMinute = Integer.toString( x ) ;
//				HMPR = timePassword.substring(2,timePassword.length()-1);
//			    hourMinutes=Integer.parseInt( HMPR ) / user.getPrefixID ();
//			    HourMinute = Integer.toString(hourMinutes) ;
			   
			    if(HourMinute .length() < 4)
			    {
			   	 HourMinute = "0"+HourMinute;
			    }
			    hour = HourMinute.substring( 0 , 2 ); 
			    minute = HourMinute.substring( 2 , 4 );
			 
				Calendar savedTime = Calendar.getInstance ();
				Calendar allowedTime = Calendar.getInstance ();
				Calendar currentTime = Calendar.getInstance ();
				savedTime.set ( Calendar.MILLISECOND , 0 );
				currentTime.set ( Calendar.SECOND , 0 );
				currentTime.set ( Calendar.MILLISECOND , 0 );
				//savedTime.set ( year , month - 1 , day , hours , minutes , 0 );
				savedTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
				savedTime.set(Calendar.MINUTE, Integer.parseInt(minute));
				currentTime.add(Calendar.MINUTE, 1);
				
				allowedTime.setTimeInMillis ( savedTime.getTimeInMillis () );
				allowedTime.add ( Calendar.MINUTE , PermissionsUtils.getPasswordMinuteInterval ( context , user.getUserCode () , user.getCompanyCode () ) );
				long currentTimeMilli = currentTime.getTimeInMillis ();
				long savedTimeMilli = savedTime.getTimeInMillis ();
				long allowedTimeMilli = allowedTime.getTimeInMillis ();
				
				if ( currentTimeMilli >= savedTimeMilli && currentTimeMilli <= allowedTimeMilli )
					return true;
				else 
					return false;
			} catch ( Exception exception ) {
				// Invalid time password
				return false;
			} // End try-catch block
		}
}