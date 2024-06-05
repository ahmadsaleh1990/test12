/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.GCM;

import android.content.Context;

/*
 * BroadcastReceiver that receives GCM messages and delivers them to an application-specific GCMBaseIntentService subclass.
 * 
 * @see com.google.android.gcm.GCMBroadcastReceiver
 */
public class GCMBroadcastReceiver extends com.google.android.gcm.GCMBroadcastReceiver {

	/*
	 * Gets the class name of the intent service that will handle GCM messages.
	 *
	 * @see com.google.android.gcm.GCMBroadcastReceiver#getGCMIntentServiceClassName(android.content.Context)
	 */
	@Override
    protected String getGCMIntentServiceClassName ( Context context ) {
        return GCMIntentService.class.getName ();
    }
	
}