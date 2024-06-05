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
 * Utilities class for generated GreenDao Entities, holding helper methods for additional functionality related to the {@link me.SyncWise.Android.Database.CollectionHeaders CollectionHeaders} objects.
 * 
 * @author Elias
 *
 */
public class CollectionUtils {
	
	public class CollectionType {
		public static final int PARTIAL = 2;
		public static final int FIFO = 1;
		public static final int ONACCOUNT = 3;
	}
	
	public static class PaymentType {
		public final static int CASH = 0;
		public final static int CHECK = 1;
	}
	
	public static class DuesType {
		public final static int CREDIT = 2;
		public final static int DEBIT = 1;
	}
	
	public static class Settlement {
		public final static int IsSettled = 1;
		public final static int IsNotSettled = 0;
	}

}