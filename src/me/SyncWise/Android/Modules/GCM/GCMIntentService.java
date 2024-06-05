/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

/**
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.SyncWise.Android.Modules.GCM;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

/**
 * IntentService responsible for handling GCM messages.
 * 
 * @author Elias
 *
 */
public class GCMIntentService extends GCMBaseIntentService {

    /**
     * Tag used to identify the source of a log message.
     */
    private static final String TAG = "GCMIntentService";

    /**
     * Key used to retrieve the content of the message.
     */
    private static final String MESSAGE = "data";
    
    /**
     * Constructor.
     */
    public GCMIntentService () {
    	// Superclass method call
        super ( CommonUtilities.SENDER_ID );
    }

    /*
     * Called after a device has been registered.
     *
     * @see com.google.android.gcm.GCMBaseIntentService#onRegistered(android.content.Context, java.lang.String)
     */
    @Override
    protected void onRegistered ( Context context , String registrationId ) {
    	// Display debug output if required
        if ( CommonUtilities.showDebugOutput )
        	Log.i ( CommonUtilities.TAG , TAG + " : Device registered: regId = " + registrationId );
		// Indicate that the device is NOT registered on the server
        // So that the new registration ID get updated on the server
        GCMRegistrar.setRegisteredOnServer ( context , false );
        // Register this account/device pair within the server.
        ServerUtilities.register ( context , registrationId );
    }

    /*
     * Called after a device has been unregistered.
     *
     * @see com.google.android.gcm.GCMBaseIntentService#onUnregistered(android.content.Context, java.lang.String)
     */
    @Override
    protected void onUnregistered ( Context context , String registrationId ) {
    	// Display debug output if required
        if ( CommonUtilities.showDebugOutput )
        	Log.i ( CommonUtilities.TAG , TAG + " : Device unregistered" );
    }

    /*
     * Called when a cloud message has been received.
     *
     * @see com.google.android.gcm.GCMBaseIntentService#onMessage(android.content.Context, android.content.Intent)
     */
    @Override
    protected void onMessage ( Context context , Intent intent ) {
    	// Display debug output if required
        if ( CommonUtilities.showDebugOutput )
        	Log.i ( CommonUtilities.TAG , TAG + " : Received message" );
        // Retrieve the message
        String json = intent.getStringExtra ( MESSAGE );
        // Check if the message is valid
        if ( json == null )
        	// Invalid message
        	return;
        
//		// Build a get Gson object
//		Gson gson = me.SyncWise.Android.Gson.CommonUtilities.buildGetGson ();
//        // Determine the message type
//        Object object = null;
//        if ( ( object = me.SyncWise.Android.Gson.CommonUtilities.getObject ( gson , json , Conversations.class ) ) != null ) {
//        	// Message is a conversation object
//        	// Display debug output if required
//            if ( CommonUtilities.showDebugOutput )
//            	Log.i ( CommonUtilities.TAG , TAG + " : Received message is a CONVERSATION" );
//        	// Handle the new conversation
//        	int handlingResult = ChatUtils.handleNewMessage ( context , (Conversations) object );
//	    	// Send an ordered broadcast
//	    	sendOrderedBroadcast ( new Intent ( ChatUtils.NEW_MESSAGE_BROADCAST_ACTION ).putExtra ( ChatUtils.NEW_MESSAGE , (Conversations) object ).putExtra ( ChatUtils.HANDLING_RESULT , handlingResult ) , null );
//        } // End if
    }

    /*
     * Called when the GCM server tells pending messages have been deleted because the device was idle.
     *
     * @see com.google.android.gcm.GCMBaseIntentService#onDeletedMessages(android.content.Context, int)
     */
    @Override
    protected void onDeletedMessages ( Context context , int total ) {
    	// Display debug output if required
        if ( CommonUtilities.showDebugOutput )
        	Log.i ( CommonUtilities.TAG , TAG + " : Received deleted messages" );
    }

    /*
     * Called on registration or unregistration error.
     *
     * @see com.google.android.gcm.GCMBaseIntentService#onError(android.content.Context, java.lang.String)
     */
    @Override
    public void onError ( Context context , String errorId ) {
    	// Display debug output if required
        if ( CommonUtilities.showDebugOutput )
        	Log.i ( CommonUtilities.TAG , TAG + " : Received error: " + errorId );
    }

    /*
     * Called on a registration error that could be retried.
     *
     * @see com.google.android.gcm.GCMBaseIntentService#onRecoverableError(android.content.Context, java.lang.String)
     */
    @Override
    protected boolean onRecoverableError ( Context context , String errorId ) {
    	// Display debug output if required
        if ( CommonUtilities.showDebugOutput )
        	Log.i ( CommonUtilities.TAG , TAG + " : Received recoverable error: " + errorId );
        // Superclass method call
        return super.onRecoverableError ( context , errorId );
    }

}
