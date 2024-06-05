/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import me.SyncWise.Android.R;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.TextUtils;

/**
 * Helper Class used to handle network connectivity.
 * 
 * @author Elias
 * @sw.todo <b>Note :</b><br>This class requires the caller to hold the permission INTERNET and ACCESS_NETWORK_STATE.
 *
 */
public abstract class Network {

	/**
	 * Amount of time in milliseconds after which a time out occurs.
	 */
	public static final int TIME_OUT = 2000;
	
	/**
	 * Enumeration used to define the various network types that can be established.
	 * In order to implement an additional network type, refer to the <b>Future Work</b> in {@link me.SyncWise.Android.Network Network}.
	 * 
	 * @author Elias
	 *
	 */
	static enum Type {
		WIFI ,
		MOBILE_DATA
	}
	
	/**
	 * AsyncTask helper class used to determine network reachability.
	 * 
	 * @author Elias
	 *
	 */
	public static class NetworkStatus extends AsyncTask < Void , Void , Boolean > {
		
		/**
		 * The application context.
		 */
		private Context context;
		
		/**
		 * Network host.
		 */
		private String host;
		
		/**
		 * Network port.
		 */
		private final int port;
		
		/**
		 * Constructor.<br>
		 * All attributes are initialized.
		 * 
		 * @param context	The application context.
		 * @param host	{@code String} holding the network host. 
		 * @param port	Integer holding the network port.
		 */
		public NetworkStatus ( final Context context , final String host , final int port ) {
			// Superclass method call
			super ();
			// Initialize attributes
			this.context = context;
			this.host = host;
			this.port = port;
		}
		
		/*
		 * Runs on the UI thread before doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute () {
			// Display indeterminate progress before starting a new thread
			AppDialog.getInstance ().displayIndeterminateProgress ( context ,
					AppResources.getInstance ( context ).getString ( context , R.string.connecting_label ) ,
					AppResources.getInstance ( context ).getString ( context , R.string.connecting_server_message ) );
		}
		
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Boolean doInBackground ( Void ... params ) {
			// Check if the host is valid
			if ( TextUtils.isEmpty ( host ) )
				// The network cannot be reached
				return false;
			// Check if the port is valid (the range for valid port numbers is between 0 and 65535 inclusive)
			if ( port < 0 || port > 65535 )
				// The network cannot be reached
				return false;
			try {
				// Declare and initialize a socket end point with the given port number port and the host name
				SocketAddress socketAddress = new InetSocketAddress ( host , port );
                // Create an unbound socket
                Socket socket = new Socket ();
                // Connect the socket to the given remote host address and port specified by the SocketAddress remoteAddr with the specified timeout
                socket.connect ( socketAddress , TIME_OUT );
				// Indicate that the network is reachable
				return true;			
			} catch ( Exception exception ) {
				// Indicate that the network is unreachable
				return false;
			} finally {
				// Release memory
				context = null;
				host = null;
			} // End of try-catch-finally block
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( Boolean result ) {
			// Close the showing dialog
			AppDialog.getInstance ().dismiss ();
		}
			
	}
	
	/**
	 * Determines if a network is available or not.<br>
	 * The network type is irrelevant (i.e. WIFI or Mobile Data).
	 * 
	 * @param context	The application context.
	 * @return	Boolean indicating if a network (irrelevant of its type) is available or not (The network may or may not have Internet access). 
	 */
	public static boolean networkAvailability ( final Context context ) {
		// Store a reference to the connectivity service handle
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService ( Context.CONNECTIVITY_SERVICE );
		// Determine if the handle is valid
		if ( connectivity != null ) {
			// Retrieve all networks
			NetworkInfo [] info = connectivity.getAllNetworkInfo ();
			// Determine if the retrieve networks are valid
			if ( info != null ) {
				// Loop over all the retrieved networks to study their connectivity
				for ( int i = 0; i < info.length; i++ ) {
					// Check if the current network is established
					if ( info[i].getState () == NetworkInfo.State.CONNECTED ) {
						// The current network is established
						return true;
					} // End if
				} // End for loop
			} // End if
		} // End if
		// Otherwise, there are no network established
		return false;
	}
	
	/**
	 * Checks for network connectivity for the provided network type.
	 * 
	 * @param context	The application context.
	 * @param type	An enumeration constant of {@link me.SyncWise.Android.Network.Type Network.Type} indicating the required network type.
	 * @return	Boolean indicating if the provided network type is established or not.
	 */
	public static boolean networkConnectivity ( final Context context , final Type type ) {
		// Store a reference to the connectivity service handle
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService ( Context.CONNECTIVITY_SERVICE );
		// Determine if the handle is valid
		if ( connectivityManager == null )
			// Invalid handle
			return false;
		// Determine network type
		switch ( type ) {
		case WIFI:
			// Determine if a WIFI network is established
			return ( connectivityManager.getNetworkInfo ( ConnectivityManager.TYPE_WIFI ).getState () == NetworkInfo.State.CONNECTED ? true : false );
		case MOBILE_DATA:
			// Determine if a MOBILE DATA network is established
			return ( connectivityManager.getNetworkInfo ( ConnectivityManager.TYPE_MOBILE ).getState () == NetworkInfo.State.CONNECTED ? true : false );
		default:
			// Invalid network type
			return false;
		} // End switch
	}
	
}