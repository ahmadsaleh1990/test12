package me.SyncWise.Android.Modules.DataManagement;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import me.SyncWise.Android.Database.ConnectionSettings;
import me.SyncWise.Android.Database.ConnectionSettingsUtils;
import me.SyncWise.Android.Modules.Sync.NullHostNameVerifier;
import me.SyncWise.Android.Modules.Sync.SyncUtils;
import me.SyncWise.Android.Modules.Sync.TLSSocketFactory;

/**
 * Class used to upload a file to the main server using the web service.
 * 
 * @author Elias
 *
 */
public final class UploadToServer {
	
    /**
     * Tag used to identify the source of a log message.
     */
	private static final String TAG = "UPLOAD TO SERVER";
	 TrustManager[] trustAllCerts = new TrustManager[]{
			    new X509TrustManager() {
			        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			            return null;
			        }
			        public void checkClientTrusted(
			            java.security.cert.X509Certificate[] certs, String authType) {
			        }
			        public void checkServerTrusted(
			            java.security.cert.X509Certificate[] certs, String authType) {
			        }
			    }
			};   
	 final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
	        public boolean verify(String hostname, SSLSession session) {
	            return true;
	        }
	 };
	/**
	 * Constructor.
	 * 
	 * @param uploadFilePaths	String hosting the file path.
	 * @param uploadFileNames	List of string hosting the file names. 
	 * @param TableName	String hosting the table name that will host the uploaded documents.
	 * @param connection	Connection setting used to upload the files.
	 */
    public UploadToServer ( final String uploadFilePaths , final ArrayList < String > uploadFileNames , final String TableName , final ConnectionSettings connection ) {
    	// Iterate over all the files to upload
    	for ( int i = 0 ; i < uploadFileNames.size () ; i ++ ) {
    		// Compute the file path
    		final String fileName = uploadFilePaths + File.separatorChar + uploadFileNames.get ( i );
    		// Retrieve the file name
    		final String tilteName = uploadFileNames.get ( i ) ; 
    		// Run a thread and upload the file asynchronously
            new Thread ( new Runnable () {
				public void run () {
					// Upload the file
                    uploadFile ( fileName  , tilteName,  TableName , connection.getConnectionSettingURL () );
                }
              } ).start ();  
    	}
    }
	
	/**
	 * Uploads a file to the service.
	 * 
	 * @param sourceFileUri
	 * @param titleName
	 * @param tableName
	 * @param sendingUrl
	 */
    @SuppressWarnings("static-access")
	public synchronized void uploadFile ( String sourceFileUri , String titleName, String tableName , final String sendingUrl ) {

    	// Logging
    	Log.e ( TAG , TAG + " : file upload process *STARTED* for [" + sourceFileUri + "] for table [" + tableName + "] on URL [" + sendingUrl + "]" );
    	// Set the file name
    	
    	if(sendingUrl.contains("https")){
    	String fileName = sourceFileUri;
    	
 
    	// Declare variables to initialize a http connection
    	HttpsURLConnection conn = null;
        DataOutputStream dos = null;  
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        // Retrieve a reference to the file to upload
        File sourceFile = new File ( sourceFileUri );
        // Check if the source file exists
        if ( ! sourceFile.isFile () )
        	// Invalid file
        	return;
        try { 
        	// open a URL connection to the service
        	FileInputStream fileInputStream = new FileInputStream ( sourceFile );
        	// Retrieve the URL
        	URL url = new URL ( ConnectionSettingsUtils.uploadFiles ( sendingUrl ) );
   
			// Open a HTTP  connection to  the URL
			conn = (HttpsURLConnection) url.openConnection (); 
			conn.setDoInput ( true ); // Allow Inputs
			conn.setDoOutput ( true ); // Allow Outputs
			conn.setUseCaches ( false ); // Don't use a Cached Copy
			conn.setRequestMethod ( "POST" );
			
	//		HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
//	 	    SSLContext sc  = SSLContext.getInstance("TLS");
//	 		sc.init(null, trustAllCerts, new java.security.SecureRandom());
//	 		conn.setHostnameVerifier(DO_NOT_VERIFY);
//	 	    conn.setSSLSocketFactory(sc.getSocketFactory());
			
	 	   SSLContext sslcontext = SSLContext.getInstance("TLSv1");
			sslcontext.init(null, null, null);
			TLSSocketFactory NoSSLv3Factory = new TLSSocketFactory();
			conn.setDefaultSSLSocketFactory(NoSSLv3Factory);
			conn = (HttpsURLConnection) url.openConnection();
			
			
			
			conn.setRequestProperty ( "Connection" , "Keep-Alive" );
			conn.setRequestProperty ( "ENCTYPE" , "multipart/form-data" );
			conn.setRequestProperty ( "Content-Type" , "multipart/form-data;boundary=" + boundary );
			conn.setRequestProperty ( "uploaded_file" , fileName );
			conn.setRequestProperty ( "table_name" , tableName );
			dos = new DataOutputStream ( conn.getOutputStream () );
			dos.writeBytes ( twoHyphens + boundary + lineEnd ); 
			dos.writeBytes ( "Content-Disposition: form-data;name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd );   
			dos.writeBytes ( lineEnd );
   
			// Copy the bits 1KB at a time
			byte [] buf = new byte [ 1024 ];
			int len;
			while ( (len = fileInputStream.read ( buf ) ) > 0) {
				dos.write ( buf , 0 , len );
			}
 
			// send multipart form data necesssary after file data...
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
 
			// Responses from the server (code and message)
			int serverResponseCode = conn.getResponseCode();
   
			if ( serverResponseCode == 200 ) {
				//close the streams 
				fileInputStream.close ();
				dos.flush ();
				dos.close ();
				HashMap < String , String > map = new HashMap < String , String > ();
				map.put ( "table_name" , tableName );
				map.put ( "file_name" , titleName );
				HttpResponse response = SyncUtils.post ( ConnectionSettingsUtils.confirmFiles ( sendingUrl ) , map );
				if ( response != null ) {
					String result = EntityUtils.toString ( response.getEntity () );
					if ( result.equalsIgnoreCase ( SyncUtils.SUCCESS ) ) {
						File file = new File ( fileName );
						file.delete();
					}
				}
			}
		} catch ( Exception e ) {
			// Logging
			Log.e ( TAG , TAG + " : Error occurred during upload process for [" + sourceFileUri + "] for table [" + tableName + "] on URL [" + sendingUrl + "]" );
			e.printStackTrace ();
		}  
		// Logging
		Log.e ( TAG , TAG + " : file upload process *ENDED* for [" + sourceFileUri + "] for table [" + tableName + "] on URL [" + sendingUrl + "]" );
    	}
    	else{
    		String fileName = sourceFileUri;
        	
    		 
        	// Declare variables to initialize a http connection
        	HttpURLConnection conn = null;
            DataOutputStream dos = null;  
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            // Retrieve a reference to the file to upload
            File sourceFile = new File ( sourceFileUri );
            // Check if the source file exists
            if ( ! sourceFile.isFile () )
            	// Invalid file
            	return;
            try { 
            	// open a URL connection to the service
            	FileInputStream fileInputStream = new FileInputStream ( sourceFile );
            	// Retrieve the URL
            	URL url = new URL ( ConnectionSettingsUtils.uploadFiles ( sendingUrl ) );
       
    			// Open a HTTP  connection to  the URL
    			conn = (HttpURLConnection) url.openConnection (); 
    			conn.setDoInput ( true ); // Allow Inputs
    			conn.setDoOutput ( true ); // Allow Outputs
    			conn.setUseCaches ( false ); // Don't use a Cached Copy
    			conn.setRequestMethod ( "POST" );
    			
    			//HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
    	 	 //   SSLContext sc  = SSLContext.getInstance("TLS");
    	 	//	sc.init(null, trustAllCerts, new java.security.SecureRandom());
    	 	//	conn.setHostnameVerifier(DO_NOT_VERIFY);
    	 	  //  conn.setSSLSocketFactory(sc.getSocketFactory());
    			
    			
    			
    			
    			
    			conn.setRequestProperty ( "Connection" , "Keep-Alive" );
    			conn.setRequestProperty ( "ENCTYPE" , "multipart/form-data" );
    			conn.setRequestProperty ( "Content-Type" , "multipart/form-data;boundary=" + boundary );
    			conn.setRequestProperty ( "uploaded_file" , fileName );
    			conn.setRequestProperty ( "table_name" , tableName );
    			dos = new DataOutputStream ( conn.getOutputStream () );
    			dos.writeBytes ( twoHyphens + boundary + lineEnd ); 
    			dos.writeBytes ( "Content-Disposition: form-data;name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd );   
    			dos.writeBytes ( lineEnd );
       
    			// Copy the bits 1KB at a time
    			byte [] buf = new byte [ 1024 ];
    			int len;
    			while ( (len = fileInputStream.read ( buf ) ) > 0) {
    				dos.write ( buf , 0 , len );
    			}
     
    			// send multipart form data necesssary after file data...
    			dos.writeBytes(lineEnd);
    			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
     
    			// Responses from the server (code and message)
    			int serverResponseCode = conn.getResponseCode();
       
    			if ( serverResponseCode == 200 ) {
    				//close the streams 
    				fileInputStream.close ();
    				dos.flush ();
    				dos.close ();
    				HashMap < String , String > map = new HashMap < String , String > ();
    				map.put ( "table_name" , tableName );
    				map.put ( "file_name" , titleName );
    				HttpResponse response = SyncUtils.post ( ConnectionSettingsUtils.confirmFiles ( sendingUrl ) , map );
    				if ( response != null ) {
    					String result = EntityUtils.toString ( response.getEntity () );
    					if ( result.equalsIgnoreCase ( SyncUtils.SUCCESS ) ) {
    						File file = new File ( fileName );
    						file.delete();
    					}
    				}
    			}
    		} catch ( Exception e ) {
    			// Logging
    			Log.e ( TAG , TAG + " : Error occurred during upload process for [" + sourceFileUri + "] for table [" + tableName + "] on URL [" + sendingUrl + "]" );
    			e.printStackTrace ();
    		}  
    		// Logging
    		Log.e ( TAG , TAG + " : file upload process *ENDED* for [" + sourceFileUri + "] for table [" + tableName + "] on URL [" + sendingUrl + "]" );
        	}
    }

}