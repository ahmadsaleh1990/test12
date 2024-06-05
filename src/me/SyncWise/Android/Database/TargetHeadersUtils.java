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

/**
 * Utilities class for generated GreenDao Entities, holding helper methods for additional functionality related to the {@link me.SyncWise.Android.Database.TargetHeaders TargetHeaders} objects.
 * 
 * @author Elias
 *
 */
public class TargetHeadersUtils {

	/**
	 * Helper class used to define the various values of a target header type.
	 * 
	 * @author Elias
	 *
	 */
	public static class Type {
		
		public static final int TOTAL_SALES_PER_CLIENT = 1;
		
		public static final int TOTAL_SALES_PER_CLIENT_PER_BRAND = 2;

		public static final int TOTAL_COVERAGE = 3;
		
		public static final int TOTAL_COLLECTION = 4;

		public static final int TOTAL_SALES_PER_CLIENT_PER_SKU_AMOUNT = 5;
		
		public static final int TOTAL_SALES_PER_CLIENT_PER_SKU_VOLUME = 6;
		
		public static final int TOTAL_SALES = 7;
		
	}
	
	public static class SubType {
		
		public static final int USER = 1;
		
		public static final int CLIENT = 2;

		public static final int DIVISION = 3;
		
		public static final int ITEM = 4;
		
	}
	
}