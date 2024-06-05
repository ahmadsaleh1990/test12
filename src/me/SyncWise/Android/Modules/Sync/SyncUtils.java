/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Sync;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import me.SyncWise.Android.R;
import me.SyncWise.Android.Algorithms.Hash;
import me.SyncWise.Android.Database.BlankVisitContacts;
import me.SyncWise.Android.Database.BlankVisitContactsDao;
import me.SyncWise.Android.Database.BlankVisits;
import me.SyncWise.Android.Database.BlankVisitsDao;
import me.SyncWise.Android.Database.ClientAssetStatus;
import me.SyncWise.Android.Database.ClientAssetStatusDao;
import me.SyncWise.Android.Database.ClientAssetsPictures;
import me.SyncWise.Android.Database.ClientAssetsPicturesDao;
import me.SyncWise.Android.Database.ClientItemClassificationHistory;
import me.SyncWise.Android.Database.ClientItemClassificationHistoryDao;
import me.SyncWise.Android.Database.ClientStockCountDetails;
import me.SyncWise.Android.Database.ClientStockCountDetailsDao;
import me.SyncWise.Android.Database.ClientStockCountHeaders;
import me.SyncWise.Android.Database.ClientStockCountHeadersDao;
import me.SyncWise.Android.Database.CollectionDetails;
import me.SyncWise.Android.Database.CollectionDetailsDao;
import me.SyncWise.Android.Database.CollectionHeaders;
import me.SyncWise.Android.Database.CollectionHeadersDao;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.EntityUtils;
import me.SyncWise.Android.Database.IsProcessedUtils;
import me.SyncWise.Android.Database.Journeys;
import me.SyncWise.Android.Database.JourneysDao;
import me.SyncWise.Android.Database.JourneysUtils;
import me.SyncWise.Android.Database.MovementDetails;
import me.SyncWise.Android.Database.MovementDetailsDao;
import me.SyncWise.Android.Database.MovementHeaders;
import me.SyncWise.Android.Database.MovementHeadersDao;
import me.SyncWise.Android.Database.MsgNote;
import me.SyncWise.Android.Database.MsgNoteDao;
import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Database.ShareOfShelfTracker;
import me.SyncWise.Android.Database.ShareOfShelfTrackerDao;
import me.SyncWise.Android.Database.SurveyAnswers;
import me.SyncWise.Android.Database.SurveyAnswersDao;
import me.SyncWise.Android.Database.SurveyAnswersUtils;
import me.SyncWise.Android.Database.TransactionDetails;
import me.SyncWise.Android.Database.TransactionDetailsDao;
import me.SyncWise.Android.Database.TransactionHeaders;
import me.SyncWise.Android.Database.TransactionHeadersDao;
import me.SyncWise.Android.Database.TransactionPromotionDetails;
import me.SyncWise.Android.Database.UsersDao;
import me.SyncWise.Android.Database.VisitReasons;
import me.SyncWise.Android.Database.VisitReasonsDao;
import me.SyncWise.Android.Database.VisitTypes;
import me.SyncWise.Android.Database.VisitTypesDao;
import me.SyncWise.Android.Database.Visits;
import me.SyncWise.Android.Database.VisitsDao;
import me.SyncWise.Android.Database.VisitsUtils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.text.GetChars;
import android.text.TextUtils;
import android.util.Log;

/**
 * Utilities class related to data synchronization, holding helper methods for additional functionality.
 * 
 * @author Elias
 *
 */
public class SyncUtils {

	 	/**
	 * Constant integer holding the connection time out value in milliseconds.
	 */
	public static final int CONNECTION_TIME_OUT = 30000;//30000 200000;
	
	/**
	 * Constant integer holding the socket time out value in milliseconds.
	 */
	public static final int SOCKET_TIME_OUT = 30000;
	
	/**
	 * String holding the key of the {@code TABLE} argument.
	 */
	public static final String TABLE = "table";
	
	/**
	 * String holding the key of the {@code TABLES_COUNT} argument.
	 */
	public static final String TABLES_COUNT = "TablesCount";
	
	/**
	 * String holding the key of the {@code CHECKSUM} argument.
	 */
	public static final String CHECKSUM = "CHECKSUM";
	
	/**
	 * String holding the expected error message if an error occurs on the server side.
	 */
	public static final String ERROR = "ERROR";
	
	/**
	 * String holding the expected Success message if no errors occur on the server side.
	 */
	public static final String SUCCESS = "SUCCESS";
	
	/**
	 * String holding the temporary folder for checksum generation of files.
	 */
	private static final String FOLDER = "CHECKSUM";
	
	/**
	 * String holding the separator using between a key and its value.
	 */
	private static final String KEY_VALUE_SEPARATOR = "=";

	/**
	 * String holding the new line character.
	 */
	private static final String NEW_LINE = "\n";
	
	/**
	 * Computes and gets the checksum of the provided arguments.<br>
	 * It is required for the web service in order to validate the received data.
	 * 
	 * @param context	The application context.
	 * @param arguments	Map hosting the http arguments as key and value pairs.
	 * @return	String hosting the digest of the arguments, or {@code NULL} if an error occurred during the checksum generation.
	 */
	public static synchronized String getArgumentsChecksum ( final Context context , final Map < String , String > arguments ) {
		// Check if the provided arguments are valid
		if ( context == null || arguments == null || arguments.isEmpty () )
			// At least one argument is invalid
			return null;
		// Retrieve a reference to the temporary folder
		File folder = context.getDir ( FOLDER , Context.MODE_PRIVATE );
		// Create a new file (random name) to host the arguments
		File file = new File ( folder , UUID.randomUUID ().toString () );
		// Declare and initialize a buffer in order to write data into the file
		BufferedWriter bufferedWriter = null;
		try {
			// Initialize the buffered writer and set it to overwrite any existing file
			bufferedWriter = new BufferedWriter ( new FileWriter ( file , false ) );
			// Iterate over all the arguments
			for ( String key : arguments.keySet () ) {
				// Add the key
				bufferedWriter.append ( key );
				// Followed by an equal operator (used to separate the key and its value)
				bufferedWriter.append ( KEY_VALUE_SEPARATOR );
				// Followed by the value
				bufferedWriter.append ( arguments.get ( key ) );
				// Add a new line
				bufferedWriter.append ( NEW_LINE );
			} // End for each
			// Flush and close the buffer to commit the file changes
			bufferedWriter.flush ();
			bufferedWriter.close ();
			// Compute and store the file checksum
			String checksum = Hash.SHA512 ( file );
			// Delete the temporary file
			file.delete ();
			// Return the checksum
			return checksum;
		} catch ( Exception exception ) {
			// Could not generate checksum
			return null;
		} // End of try-catch block
	}
	/**
	 * Gets an http response (via an http post request) for the provided url and arguments.
	 * 
	 * @param url	String holding the url.
	 * @param arguments	Map hosting the http arguments as key and value pairs.
	 * @return	The Http response as a result of the http post request.
	 */
	public static HttpResponse post ( final String url , Map < String , String > arguments ,Context context) {
		// Check if the url is valid
		if ( TextUtils.isEmpty ( url ) )
			// Invalid url
			return null;
		// Declare and initialize a collection of HTTP protocol and framework parameters.
		HttpParams httpParameters = new BasicHttpParams ();
		
		String userCode = DatabaseUtils.getCurrentUserCode ( context );
	      // Retrieve the company code
	    String companyCode = DatabaseUtils.getCurrentCompanyCode ( context );
	    int timeout=0;
	    if(userCode==null || companyCode==null)
	    timeout=30000;
	    else
	    	timeout=PermissionsUtils.getTimeOut (context,  userCode ,companyCode);


		// Set the timeout in milliseconds until a connection is established
		HttpConnectionParams.setConnectionTimeout ( httpParameters , timeout );
		// Sets the default socket timeout in milliseconds which is the timeout for waiting for data
		HttpConnectionParams.setSoTimeout ( httpParameters , timeout );
//		SchemeRegistry schemeRegistry = new SchemeRegistry ();
		try {
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	    trustStore.load(null, null);

																
																																												  
  
  
  
 

//	    SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
//	    sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	  
//    	schemeRegistry.register (new Scheme ("http",PlainSocketFactory.getSocketFactory (), 8090));
    
    //	schemeRegistry.register(new Scheme("https",  (SocketFactory) delegate , 443));
//    	schemeRegistry.register (new Scheme ("http",PlainSocketFactory.getSocketFactory (), 8090));
//    
//    	schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));

		} catch (Exception e) {}
//		ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
//        .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0)
//        .cipherSuites(
//                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
//                CipherSuite.TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256,
//                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
//                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA)
//        .build();
//
		
	 	SchemeRegistry registry = new SchemeRegistry();
//    	try {
//			registry.register(new Scheme("https", new CustomSSLSocketFactory(),443));
//		} catch (KeyManagementException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnrecoverableKeyException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NoSuchAlgorithmException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (KeyStoreException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    	registry.register(new Scheme("https", new TlsSniSocketFactory(),443));
    			//new TlsSniSocketFactory(), 443));
    	registry.register (new Scheme ("http",PlainSocketFactory.getSocketFactory (), 8090));
    	
    	HttpClient httpClient  = new DefaultHttpClient(new ThreadSafeClientConnManager(httpParameters, registry),httpParameters);
    	
    	
//httpClient.connectionSpecs(Collections.singletonList(spec))
    	httpParameters.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
		// Initialize an http client using its default implementation
		//HttpClient httpClient = new DefaultHttpClient (new ThreadSafeClientConnManager(httpParameters, schemeRegistry), httpParameters );
	//	HttpClient httpClient = new DefaultHttpClient (httpParameters);
		// Initialize a http post request using the specified url
		HttpPost httpPost = new HttpPost ( url );
		// Check if the arguments map is valid
		if ( arguments != null ) {
			// Declare a list of name value pairs, used to encapsulate the arguments
			List < NameValuePair > nameValuePairs = new ArrayList < NameValuePair > ( arguments.size () );
		    String key, value;
		    Iterator < String > itKeys = arguments.keySet ().iterator (); 
		 
		    // Iterate over all argument keys
		    while ( itKeys.hasNext () ) {
		    	// Retrieve the argument key
		    	key = itKeys.next ();
		    	// Retrieve the argument value
		    	value = arguments.get ( key );
		    	// Build a basic name value pair using the key and value above
		        nameValuePairs.add ( new BasicNameValuePair ( key , value ) );
		    } // End while loop
		 
		    try {
		    // Hand the entity to the request
		    	httpPost.setEntity ( new UrlEncodedFormEntity ( nameValuePairs , HTTP.UTF_8 ) );
		    	// Execute the http post request using the default context
		    	HttpResponse response = httpClient.execute ( httpPost );
		    	// Return the http response
		    	return response;
			} catch ( UnsupportedEncodingException exception ) {
				 Log.e("UnsupportedEncodingException", "Error parsing data " + exception.toString());
				// generateNoteOnSD(  "UnsupportedEncodingException Synutil", exception.toString());
				// Do nothing.
			} catch ( ClientProtocolException exception ) {
				 Log.e("ClientProtocolException", "Error parsing data " + exception.toString());
				// generateNoteOnSD(  "ClientProtocolException Synutil", exception.toString());
				// Do nothing.
			} catch ( IOException exception ) {
				 Log.e("IOException", "Error parsing data " + exception.toString());
			//	 generateNoteOnSD(  "IOException Synutil", exception.toString());
			//	String error=exception.getMessage().toString();
			//	Log.e("ali","  "+error);
				// Do nothing.
			} // End try-catch block
		} // End if
		return null;
	}
	/**
	 * Gets an http response (via an http post request) for the provided url and arguments.
	 * 
	 * @param url	String holding the url.
	 * @param arguments	Map hosting the http arguments as key and value pairs.
	 * @return	The Http response as a result of the http post request.
	 */
	public static HttpResponse post ( final String url , Map < String , String > arguments  ) {
		// Check if the url is valid
		if ( TextUtils.isEmpty ( url ) )
			// Invalid url
			return null;
		// Declare and initialize a collection of HTTP protocol and framework parameters.
		HttpParams httpParameters = new BasicHttpParams ();
		 

		// Set the timeout in milliseconds until a connection is established
		HttpConnectionParams.setConnectionTimeout ( httpParameters , CONNECTION_TIME_OUT );
		// Sets the default socket timeout in milliseconds which is the timeout for waiting for data
		HttpConnectionParams.setSoTimeout ( httpParameters , SOCKET_TIME_OUT );
//		SchemeRegistry schemeRegistry = new SchemeRegistry ();
		try {
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	    trustStore.load(null, null);

//	    SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
//	    sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	  
	    
//	    SSLContext sslContext = null;
//	    sslContext = SSLContext.getInstance("TLSv1.2");
//        sslContext.init(null, null, null);
//        TlsSniSocketFactory delegate = null;
//        delegate = new TlsSniSocketFactory(sslContext.getSocketFactory());
//   	
//    	schemeRegistry.register (new Scheme ("http",PlainSocketFactory.getSocketFactory (), 8090));
    
    //	schemeRegistry.register(new Scheme("https",  (SocketFactory) delegate , 443));

		} catch (Exception e) {}

    	httpParameters.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
		// Initialize an http client using its default implementation
		//HttpClient httpClient = new DefaultHttpClient (new ThreadSafeClientConnManager(httpParameters, schemeRegistry), httpParameters );
		//HttpClient httpClient = new DefaultHttpClient (httpParameters);
     	SchemeRegistry registry = new SchemeRegistry();
    	registry.register(new Scheme("https", new TlsSniSocketFactory(), 443));
    	registry.register (new Scheme ("http",PlainSocketFactory.getSocketFactory (), 8090));
    	HttpClient httpClient  = new DefaultHttpClient(new ThreadSafeClientConnManager(httpParameters, registry),httpParameters);
    	
    	
    	
    	// Initialize a http post request using the specified url
		HttpPost httpPost = new HttpPost ( url );
		// Check if the arguments map is valid
		if ( arguments != null ) {
			// Declare a list of name value pairs, used to encapsulate the arguments
			List < NameValuePair > nameValuePairs = new ArrayList < NameValuePair > ( arguments.size () );
		    String key, value;
		    Iterator < String > itKeys = arguments.keySet ().iterator (); 
		 
		    // Iterate over all argument keys
		    while ( itKeys.hasNext () ) {
		    	// Retrieve the argument key
		    	key = itKeys.next ();
		    	// Retrieve the argument value
		    	value = arguments.get ( key );
		    	// Build a basic name value pair using the key and value above
		        nameValuePairs.add ( new BasicNameValuePair ( key , value ) );
		    } // End while loop
		 
		    try {
		   
		    	
		    
		    	
		    	// Hand the entity to the request
		    	httpPost.setEntity ( new UrlEncodedFormEntity ( nameValuePairs , HTTP.UTF_8 ) );
		    	// Execute the http post request using the default context
		    	HttpResponse response = httpClient.execute ( httpPost );
		    	// Return the http response
		    	return response;
			} catch ( UnsupportedEncodingException exception ) {
				 Log.e("UnsupportedEncodingException", "Error parsing data " + exception.toString());
			//	 generateNoteOnSD(  "UnsupportedEncodingException Synutil", exception.toString());
				// Do nothing.
			} catch ( ClientProtocolException exception ) {
				 Log.e("ClientProtocolException", "Error parsing data " + exception.toString());
			//	 generateNoteOnSD(  "ClientProtocolException Synutil", exception.toString());
				/// Do nothing.
			} catch ( IOException exception ) {
				 Log.e("IOException", "Error parsing data " + exception.toString());
				// generateNoteOnSD(  "IOException Synutil", exception.toString());
			//	String error=exception.getMessage().toString();
			//	Log.e("ali","  "+error);
				// Do nothing.
			} // End try-catch block
		} // End if
		return null;
	}
	
	/**
	 * Gets an http response (via an http post request) for the provided url and arguments.
	 * 
	 * @param url	String holding the url.
	 * @param arguments	Map hosting the http arguments as key and value pairs.
	 * @return	The Http response as a result of the http post request.
	 */
//	public static HttpResponse post ( final String url , Map < String , String > arguments ,Context context) {
//		// Check if the url is valid
//		if ( TextUtils.isEmpty ( url ) )
//			// Invalid url
//			return null;
//		// Declare and initialize a collection of HTTP protocol and framework parameters.
//		HttpParams httpParameters = new BasicHttpParams ();
//		
//		String userCode = DatabaseUtils.getCurrentUserCode ( context );
//	      // Retrieve the company code
//	    String companyCode = DatabaseUtils.getCurrentCompanyCode ( context );
//	    int timeout=0;
//	    if(userCode==null || companyCode==null)
//	    timeout = 30000;
//	    else
//	    	timeout=PermissionsUtils.getTimeOut (context,  userCode ,companyCode);
//
//
//		// Set the timeout in milliseconds until a connection is established
//		HttpConnectionParams.setConnectionTimeout ( httpParameters , timeout );
//		// Sets the default socket timeout in milliseconds which is the timeout for waiting for data
//		HttpConnectionParams.setSoTimeout ( httpParameters , timeout );
//		//SchemeRegistry schemeRegistry = new SchemeRegistry ();
//		try {
//		//KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//	  //  trustStore.load(null, null);
//
//	  //  SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
//	  //  sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//		
//		
//	 
//	
//
//    //	schemeRegistry.register (new Scheme ("http",    	    PlainSocketFactory.getSocketFactory (), 8090));
//    
//    //	schemeRegistry.register(new Scheme("https",   	          sf, 443));
//
//		} catch (Exception e) {}
//
//    	httpParameters.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
//		// Initialize an http client using its default implementation
//		//HttpClient httpClient = new DefaultHttpClient (new ThreadSafeClientConnManager(httpParameters, schemeRegistry), httpParameters );
//	
//    	SchemeRegistry registry = new SchemeRegistry();
//    	registry.register(new Scheme("https", new TlsSniSocketFactory(), 443));
//    	registry.register (new Scheme ("http",PlainSocketFactory.getSocketFactory (), 8090));
//    	HttpClient httpClient  = new DefaultHttpClient(new ThreadSafeClientConnManager(httpParameters, registry),httpParameters);
//    	
//    	// Initialize a http post request using the specified url
//		HttpPost httpPost = new HttpPost ( url );
//		// Check if the arguments map is valid
//		if ( arguments != null ) {
//			// Declare a list of name value pairs, used to encapsulate the arguments
//			List < NameValuePair > nameValuePairs = new ArrayList < NameValuePair > ( arguments.size () );
//		    String key, value;
//		    Iterator < String > itKeys = arguments.keySet ().iterator (); 
//		 
//		    // Iterate over all argument keys
//		    while ( itKeys.hasNext () ) {
//		    	// Retrieve the argument key
//		    	key = itKeys.next ();
//		    	// Retrieve the argument value
//		    	value = arguments.get ( key );
//		    	// Build a basic name value pair using the key and value above
//		        nameValuePairs.add ( new BasicNameValuePair ( key , value ) );
//		    } // End while loop
//		 
//		    try {
//		    // Hand the entity to the request
//		    	httpPost.setEntity ( new UrlEncodedFormEntity ( nameValuePairs , HTTP.UTF_8 ) );
//		    	// Execute the http post request using the default context
//		    	HttpResponse response = httpClient.execute ( httpPost );
//		    	// Return the http response
//		    	return response;
//			} catch ( UnsupportedEncodingException exception ) {
//				// Do nothing.
//			} catch ( ClientProtocolException exception ) {
//				// Do nothing.
//			} catch ( IOException exception ) {
//			//	String error=exception.getMessage().toString();
//			//	Log.e("ali","  "+error);
//				// Do nothing.
//			} // End try-catch block
//		} // End if
//		return null;
//	}
//	/**
//	 * Gets an http response (via an http post request) for the provided url and arguments.
//	 * 
//	 * @param url	String holding the url.
//	 * @param arguments	Map hosting the http arguments as key and value pairs.
//	 * @return	The Http response as a result of the http post request.
//	 */
//	public static HttpResponse post ( final String url , Map < String , String > arguments  ) {
//		// Check if the url is valid
//		if ( TextUtils.isEmpty ( url ) )
//			// Invalid url
//			return null;
//		// Declare and initialize a collection of HTTP protocol and framework parameters.
//		HttpParams httpParameters = new BasicHttpParams ();
//		 
//
//		// Set the timeout in milliseconds until a connection is established
//		HttpConnectionParams.setConnectionTimeout ( httpParameters , CONNECTION_TIME_OUT );
//		// Sets the default socket timeout in milliseconds which is the timeout for waiting for data
//		HttpConnectionParams.setSoTimeout ( httpParameters , SOCKET_TIME_OUT );
//		//SchemeRegistry schemeRegistry = new SchemeRegistry ();
//		try {
//		//KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//	 ///   trustStore.load(null, null);
//
//	  //  SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
//	//    sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//		
//	 //	schemeRegistry.register (new Scheme ("http", 	    PlainSocketFactory.getSocketFactory (), 8090));
//    
//    //	schemeRegistry.register(new Scheme("https", 	          sf, 443));
//
//		} catch (Exception e) {}
//
//    	httpParameters.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
//		// Initialize an http client using its default implementation
//		//HttpClient httpClient = new DefaultHttpClient (new ThreadSafeClientConnManager(httpParameters, schemeRegistry), httpParameters );
//    	SchemeRegistry registry = new SchemeRegistry();
//    	registry.register(new Scheme("https", new TlsSniSocketFactory(), 443));
//    	registry.register (new Scheme ("http",PlainSocketFactory.getSocketFactory (), 8090));
//    	HttpClient httpClient  = new DefaultHttpClient(new ThreadSafeClientConnManager(httpParameters, registry),httpParameters);
//    	
//    	// Initialize a http post request using the specified url
//		HttpPost httpPost = new HttpPost ( url );
//		// Check if the arguments map is valid
//		if ( arguments != null ) {
//			// Declare a list of name value pairs, used to encapsulate the arguments
//			List < NameValuePair > nameValuePairs = new ArrayList < NameValuePair > ( arguments.size () );
//		    String key, value;
//		    Iterator < String > itKeys = arguments.keySet ().iterator (); 
//		 
//		    // Iterate over all argument keys
//		    while ( itKeys.hasNext () ) {
//		    	// Retrieve the argument key
//		    	key = itKeys.next ();
//		    	// Retrieve the argument value
//		    	value = arguments.get ( key );
//		    	// Build a basic name value pair using the key and value above
//		        nameValuePairs.add ( new BasicNameValuePair ( key , value ) );
//		    } // End while loop
//		 
//		    try {
//		   
//		    	
//		    
//		    	
//		    	// Hand the entity to the request
//		    	httpPost.setEntity ( new UrlEncodedFormEntity ( nameValuePairs , HTTP.UTF_8 ) );
//		    	// Execute the http post request using the default context
//		    	HttpResponse response = httpClient.execute ( httpPost );
//		    	// Return the http response
//		    	return response;
//			} catch ( UnsupportedEncodingException exception ) {
//				// Do nothing.
//			} catch ( ClientProtocolException exception ) {
//				// Do nothing.
//			} catch ( IOException exception ) {
//			//	String error=exception.getMessage().toString();
//			//	Log.e("ali","  "+error);
//				// Do nothing.
//			} // End try-catch block
//		} // End if
//		return null;
//	}
//	
//	/**
//	 * Gets an http response (via an http post request) for the provided url and arguments.
//	 * 
//	 * @param url	String holding the url.
//	 * @param arguments	Map hosting the http arguments as key and value pairs.
//	 * @return	The Http response as a result of the http post request.
//	 */
//	public static HttpResponse post ( final String url , Map < String , String > arguments  ) {
//		// Check if the url is valid
//		if ( TextUtils.isEmpty ( url ) )
//			// Invalid url
//			return null;
//		// Declare and initialize a collection of HTTP protocol and framework parameters.
//		HttpParams httpParameters = new BasicHttpParams ();
//		
//		 
//
//		// Set the timeout in milliseconds until a connection is established
//		HttpConnectionParams.setConnectionTimeout ( httpParameters , CONNECTION_TIME_OUT );
//		// Sets the default socket timeout in milliseconds which is the timeout for waiting for data
//		HttpConnectionParams.setSoTimeout ( httpParameters , SOCKET_TIME_OUT );
//		// Initialize an http client using its default implementation
//		HttpClient httpClient = new DefaultHttpClient ( httpParameters );
//		// Initialize a http post request using the specified url
//		HttpPost httpPost = new HttpPost ( url );
//		// Check if the arguments map is valid
//		if ( arguments != null ) {
//			// Declare a list of name value pairs, used to encapsulate the arguments
//			List < NameValuePair > nameValuePairs = new ArrayList < NameValuePair > ( arguments.size () );
//		    String key, value;
//		    Iterator < String > itKeys = arguments.keySet ().iterator (); 
//		 
//		    // Iterate over all argument keys
//		    while ( itKeys.hasNext () ) {
//		    	// Retrieve the argument key
//		    	key = itKeys.next ();
//		    	// Retrieve the argument value
//		    	value = arguments.get ( key );
//		    	// Build a basic name value pair using the key and value above
//		        nameValuePairs.add ( new BasicNameValuePair ( key , value ) );
//		    } // End while loop
//		 
//		    try {
//		    	// Hand the entity to the request
//		    	httpPost.setEntity ( new UrlEncodedFormEntity ( nameValuePairs , HTTP.UTF_8 ) );
//		    	// Execute the http post request using the default context
//		    	HttpResponse response = httpClient.execute ( httpPost );
//		    	// Return the http response
//		    	return response;
//			} catch ( UnsupportedEncodingException exception ) {
//				// Do nothing.
//			} catch ( ClientProtocolException exception ) {
//				// Do nothing.
//			} catch ( IOException exception ) {
//				// Do nothing.
//			} // End try-catch block
//		} // End if
//		return null;
//	}
	/**
	 * Updates entities after/before they are sent to the server.<br>
	 * Their {@code IsProcessed} field (if any) is updated accordingly.<br>
	 * The object is NOT updated in DB !!
	 * 
	 * @param context	The application context.
	 * @param entities	List of sent entities.
	 */
	public static void updateProcessedEntities ( final Context context , ArrayList < Object > entities ) {
		// Determine if the arguments are valid
		if ( context == null || entities == null || entities.isEmpty () )
			// Invalid arguments
			return;
		// Retrieve a reference to the entity class
		Class < ? > entityClass = entities.get ( 0 ).getClass ();
		// Retrieve the entity's methods
		Method methods [] = entityClass.getDeclaredMethods ();
		// Determine if the current entity has a setter for an IsProcessed field
		Method setProcessed = null;
		// Iterate over all the entity methods
		for ( Method method : methods )
			// Determine if the current method is an IsProcessed setter
			if ( method.getName ().equalsIgnoreCase ( "setisprocessed" ) ) {
				// Initialize the IsProcessed setter
				setProcessed = method;
				// Exit loop
				break;
			} // End if
		// Check if the IsProcessed setter is valid
		if ( setProcessed == null )
			// Invalid setter
			// Do nothing
			// Exit method
			return;
		// Otherwise, the IsProcessed setter is valid
		// Iterate over all entities
		for ( Object entity : entities )
			try {
				// Use the IsProcessed setter to indicate that the entity is processed
				setProcessed.invoke ( entity , IsProcessedUtils.isSync () );
			} catch ( Exception exception ) {
				// Do nothing
			} // End of try-catch block
	}
	
	

	/**
	 * Updates entities after/before they are sent to the server.<br>
	 * Their {@code IsProcessed} field (if any) is updated accordingly.<br>
	 * The object is NOT updated in DB !!
	 * 
	 * @param context	The application context.
	 * @param entities	List of sent entities.
	 */
	public static void updateProcessedEntitiesTransactionHeaders ( final Context context , ArrayList < Object > entities ) {
		// Determine if the arguments are valid
		if ( context == null || entities == null || entities.isEmpty () )
			// Invalid arguments
			return;
		// Retrieve a reference to the entity class
		Class < ? > entityClass = entities.get ( 0 ).getClass ();
		// Retrieve the entity's methods
		Method methods [] = entityClass.getDeclaredMethods ();
		// Determine if the current entity has a setter for an IsProcessed field
		Method setProcessed = null;
		// Iterate over all the entity methods
		for ( Method method : methods )
			// Determine if the current method is an IsProcessed setter
			if ( method.getName ().equalsIgnoreCase ( "setisprocessed" ) ) {
				// Initialize the IsProcessed setter
				setProcessed = method;
				// Exit loop
				break;
			} // End if
		// Check if the IsProcessed setter is valid
		if ( setProcessed == null )
			// Invalid setter
			// Do nothing
			// Exit method
			return;
		// Otherwise, the IsProcessed setter is valid
		// Iterate over all entities
		for ( Object entity : entities )
			try {
				// Use the IsProcessed setter to indicate that the entity is processed
				setProcessed.invoke ( entity , IsProcessedUtils.isNotSync()  );
			} catch ( Exception exception ) {
				// Do nothing
			} // End of try-catch block
	}
	
	
	
	
	/**
	 * Collects data for deletion based on their age (similarly to the garbage collector).
	 * 
	 * @param context	The application context.
	 */
	public static void dataCollector ( final Context context ) { 
		String userCode = DatabaseUtils.getCurrentUserCode ( context );
		String companyCode = DatabaseUtils.getCurrentCompanyCode( context );
		// Check if the visit merging is enabled
		Integer nbrWeek= PermissionsUtils.getSummaryWeeks ( context , userCode,companyCode )  ;
		// Get date 8 days ago
		Calendar old = Calendar.getInstance ();
		old.add ( Calendar.DATE , -nbrWeek );
		// Retrieve the user prefix ID
		int prefixID = DatabaseUtils.getInstance ( context ).getDaoSession ().getUsersDao ().queryBuilder ()
				.where ( UsersDao.Properties.UserCode.eq ( DatabaseUtils.getCurrentUserCode ( context ) ) ,
						UsersDao.Properties.CompanyCode.eq ( DatabaseUtils.getCurrentCompanyCode ( context ) ) ).unique ().getPrefixID ();
		
		// Compute journey code for old date
		String oldJourneyCode = JourneysUtils.getJourneyCode ( prefixID , old );
		// Compute visit ID for old date
		Long oldVisitID = VisitsUtils.getVisitID ( old );
		// Compute survey ID for old date
		Long oldSurveyID = SurveyAnswersUtils.getSurveyAnswerID ( old );
		
		// Retrieve all the old and processed message notes
		List < MsgNote > msgNotes = DatabaseUtils.getInstance ( context ).getDaoSession ().getMsgNoteDao ().queryBuilder ()
				.where ( MsgNoteDao.Properties.StampDate.lt ( old.getTimeInMillis () ) , MsgNoteDao.Properties.IsProcessed.eq ( IsProcessedUtils.isSync () ) ).list ();
		
		// Retrieve all the old and processed journeys
		List < Journeys > journeys = DatabaseUtils.getInstance ( context ).getDaoSession ().getJourneysDao ().queryBuilder ()
				.where ( JourneysDao.Properties.JourneyCode.lt ( oldJourneyCode ) , JourneysDao.Properties.IsProcessed.eq ( IsProcessedUtils.isSync () ) ).list ();
		
		// Retrieve all the old and processed visits
		List < Visits > visits = DatabaseUtils.getInstance ( context ).getDaoSession ().getVisitsDao ().queryBuilder ()
				.where ( VisitsDao.Properties.JourneyCode.lt ( oldJourneyCode ) , VisitsDao.Properties.IsProcessed.eq ( IsProcessedUtils.isSync () ) ).list ();
		// Track the visit IDs
		List < Long > visitIds = new ArrayList < Long > ();
		for ( Visits visit : visits )
			visitIds.add ( visit.getVisitID () );
		
		// Retrieve all the old processed visit reasons
		List < VisitReasons > visitReasons = EntityUtils.queryByGroup ( visitIds ,
				DatabaseUtils.getInstance ( context ).getDaoSession ().getVisitReasonsDao () ,
				VisitReasonsDao.Properties.VisitID );
		
		// Retrieve all the old processed visit types
		List < VisitTypes > visitTypes = EntityUtils.queryByGroup ( visitIds ,
				DatabaseUtils.getInstance ( context ).getDaoSession ().getVisitTypesDao () ,
				VisitTypesDao.Properties.VisitID );
		
		// Retrieve all the processed blank visits of old visits
		List < BlankVisits > blankVisits = DatabaseUtils.getInstance ( context ).getDaoSession ().getBlankVisitsDao ().queryBuilder ()
				.where ( BlankVisitsDao.Properties.VisitID.lt ( oldVisitID ) , BlankVisitsDao.Properties.IsProcessed.eq ( IsProcessedUtils.isSync () ) ).list ();
		
		// Retrieve all the processed blank visits contacts of old visits
		List < BlankVisitContacts > blankVisitContacts = DatabaseUtils.getInstance ( context ).getDaoSession ().getBlankVisitContactsDao ().queryBuilder ()
				.where ( BlankVisitContactsDao.Properties.VisitID.lt ( oldVisitID ) , BlankVisitContactsDao.Properties.IsProcessed.eq ( IsProcessedUtils.isSync () ) ).list ();
		
		// Retrieve all the old and processed transaction headers
		List < TransactionHeaders > transactionHeaders = DatabaseUtils.getInstance ( context ).getDaoSession ().getTransactionHeadersDao ().queryBuilder ()
				.where ( TransactionHeadersDao.Properties.JourneyCode.lt ( oldJourneyCode ) , TransactionHeadersDao.Properties.IsProcessed.eq ( IsProcessedUtils.isSync () ) ).list ();
		// Track the transaction codes
		List < String > transactionCodes = new ArrayList < String > ();
		for ( TransactionHeaders transactionHeader : transactionHeaders )
			transactionCodes.add ( transactionHeader.getTransactionCode () );
		
		// Retrieve all the transaction details of old transaction headers
		List < TransactionDetails > transactionDetails = EntityUtils.queryByGroup ( transactionCodes ,
				DatabaseUtils.getInstance ( context ).getDaoSession ().getTransactionDetailsDao () ,
				TransactionDetailsDao.Properties.TransactionCode );
		
		// Retrieve all the transaction promotion details of old transaction headers
		List < TransactionPromotionDetails > transactionPromotionDetails = EntityUtils.queryByGroup ( transactionCodes ,
				DatabaseUtils.getInstance ( context ).getDaoSession ().getTransactionPromotionDetailsDao () ,
				TransactionDetailsDao.Properties.TransactionCode );
		
		// Retrieve all the processed client item classification history
		List < ClientItemClassificationHistory > clientItemClassificationHistory = DatabaseUtils.getInstance ( context ).getDaoSession ().getClientItemClassificationHistoryDao ()
				.queryBuilder ().where ( ClientItemClassificationHistoryDao.Properties.IsProcessed.eq ( IsProcessedUtils.isSync () ) ).list ();
		
		// Retrieve all the old and processed movement headers
		List < MovementHeaders > movementHeaders = DatabaseUtils.getInstance ( context ).getDaoSession ().getMovementHeadersDao ().queryBuilder ()
				.where ( MovementHeadersDao.Properties.MovementDate.lt ( old.getTimeInMillis () ) , MovementHeadersDao.Properties.IsProcessed.eq ( IsProcessedUtils.isSync () ) ).list ();
		// Track the movement codes
		List < String > movementCodes = new ArrayList < String > ();
		for ( MovementHeaders movementHeader : movementHeaders )
			movementCodes.add ( movementHeader.getMovementCode () );
		
		// Retrieve all the movement details of old movement headers
		List < MovementDetails > movementDetails = EntityUtils.queryByGroup ( movementCodes ,
				DatabaseUtils.getInstance ( context ).getDaoSession ().getMovementDetailsDao () ,
				MovementDetailsDao.Properties.MovementCode );
		
		// Retrieve all the old and processed client surveys answers
		List < SurveyAnswers > clientSurveyAnswers = DatabaseUtils.getInstance ( context ).getDaoSession ().getSurveyAnswersDao ().queryBuilder ()
				.where ( SurveyAnswersDao.Properties.SurveyID.lt ( oldSurveyID ) , SurveyAnswersDao.Properties.IsProcessed.eq ( IsProcessedUtils.isSync () ) ).list ();
		
		// Retrieve all the old and processed share of shelf trackers
		List < ShareOfShelfTracker > shareOfShelfTrackers = DatabaseUtils.getInstance ( context ).getDaoSession ().getShareOfShelfTrackerDao ().queryBuilder ()
				.where ( ShareOfShelfTrackerDao.Properties.StampDate.lt ( old.getTimeInMillis () ) , ShareOfShelfTrackerDao.Properties.IsProcessed.eq ( IsProcessedUtils.isSync () ) ).list ();
		
		// Retrieve all the old and processed collection headers
		List < CollectionHeaders > collectionHeaders = DatabaseUtils.getInstance ( context ).getDaoSession ().getCollectionHeadersDao ().queryBuilder ()
				.where ( CollectionHeadersDao.Properties.JourneyCode.lt ( oldJourneyCode ) , CollectionHeadersDao.Properties.IsProcessed.eq ( IsProcessedUtils.isSync () ) ).list ();
		// Track the collection codes
		List < String > collectionCodes = new ArrayList < String > ();
		for ( CollectionHeaders collectionHeader : collectionHeaders )
			collectionCodes.add ( collectionHeader.getCollectionCode () );
		
		// Retrieve all the collection details of old collection headers
		List < CollectionDetails > collectionDetails = EntityUtils.queryByGroup ( collectionCodes ,
				DatabaseUtils.getInstance ( context ).getDaoSession ().getCollectionDetailsDao () ,
				CollectionDetailsDao.Properties.CollectionCode );
		
		// Retrieve all the old and processed client stock count headers
		List < ClientStockCountHeaders > clientStockCountHeaders = DatabaseUtils.getInstance ( context ).getDaoSession ().getClientStockCountHeadersDao ().queryBuilder ()
				.where ( ClientStockCountHeadersDao.Properties.JourneyCode.lt ( oldJourneyCode ) , ClientStockCountHeadersDao.Properties.IsProcessed.eq ( IsProcessedUtils.isSync () ) ).list ();
		// Track the client stock count codes
		List < String > clientStockCountCodes = new ArrayList < String > ();
		for ( ClientStockCountHeaders clientStockCountHeader : clientStockCountHeaders )
			clientStockCountCodes.add ( clientStockCountHeader.getTransactionCode () );
		
		// Retrieve all the client stock count details of old client stock count headers
		List < ClientStockCountDetails > clientStockCountDetails = EntityUtils.queryByGroup ( clientStockCountCodes ,
				DatabaseUtils.getInstance ( context ).getDaoSession ().getClientStockCountDetailsDao () ,
				ClientStockCountDetailsDao.Properties.TransactionCode );
		
		// Retrieve all the client asset status of old client stock count headers
		List < ClientAssetStatus > clientAssetStatus = EntityUtils.queryByGroup ( clientStockCountCodes ,
				DatabaseUtils.getInstance ( context ).getDaoSession ().getClientAssetStatusDao () ,
				ClientAssetStatusDao.Properties.TransactionCode );
		
		// Retrieve all the client asset pictures of old client stock count headers
		List < ClientAssetsPictures > clientAssetsPictures = EntityUtils.queryByGroup ( clientStockCountCodes ,
				DatabaseUtils.getInstance ( context ).getDaoSession ().getClientAssetsPicturesDao () ,
				ClientAssetsPicturesDao.Properties.TransactionCode );
		
		// Delete all OLD data
		DatabaseUtils.getInstance ( context ).getDaoSession ().getMsgNoteDao ().deleteInTx ( msgNotes );
		DatabaseUtils.getInstance ( context ).getDaoSession ().getJourneysDao ().deleteInTx ( journeys );
		DatabaseUtils.getInstance ( context ).getDaoSession ().getVisitsDao ().deleteInTx ( visits );
		DatabaseUtils.getInstance ( context ).getDaoSession ().getVisitReasonsDao ().deleteInTx ( visitReasons );
		DatabaseUtils.getInstance ( context ).getDaoSession ().getVisitTypesDao ().deleteInTx ( visitTypes );
		DatabaseUtils.getInstance ( context ).getDaoSession ().getBlankVisitsDao ().deleteInTx ( blankVisits );
		DatabaseUtils.getInstance ( context ).getDaoSession ().getBlankVisitContactsDao ().deleteInTx ( blankVisitContacts );
		DatabaseUtils.getInstance ( context ).getDaoSession ().getTransactionHeadersDao ().deleteInTx ( transactionHeaders );
		DatabaseUtils.getInstance ( context ).getDaoSession ().getTransactionDetailsDao ().deleteInTx ( transactionDetails );
		DatabaseUtils.getInstance ( context ).getDaoSession ().getTransactionPromotionDetailsDao ().deleteInTx ( transactionPromotionDetails );
		DatabaseUtils.getInstance ( context ).getDaoSession ().getClientItemClassificationHistoryDao ().deleteInTx ( clientItemClassificationHistory );
		DatabaseUtils.getInstance ( context ).getDaoSession ().getMovementHeadersDao ().deleteInTx ( movementHeaders );
		DatabaseUtils.getInstance ( context ).getDaoSession ().getMovementDetailsDao ().deleteInTx ( movementDetails );
		DatabaseUtils.getInstance ( context ).getDaoSession ().getSurveyAnswersDao ().deleteInTx ( clientSurveyAnswers );
		DatabaseUtils.getInstance ( context ).getDaoSession ().getShareOfShelfTrackerDao ().deleteInTx ( shareOfShelfTrackers );
		DatabaseUtils.getInstance ( context ).getDaoSession ().getCollectionHeadersDao ().deleteInTx ( collectionHeaders );
		DatabaseUtils.getInstance ( context ).getDaoSession ().getCollectionDetailsDao ().deleteInTx ( collectionDetails );
		DatabaseUtils.getInstance ( context ).getDaoSession ().getClientStockCountHeadersDao ().deleteInTx ( clientStockCountHeaders );
		DatabaseUtils.getInstance ( context ).getDaoSession ().getClientStockCountDetailsDao ().deleteInTx ( clientStockCountDetails );
		DatabaseUtils.getInstance ( context ).getDaoSession ().getClientAssetStatusDao ().deleteInTx ( clientAssetStatus );
		DatabaseUtils.getInstance ( context ).getDaoSession ().getClientAssetsPicturesDao ().deleteInTx ( clientAssetsPictures );
	}
	
}