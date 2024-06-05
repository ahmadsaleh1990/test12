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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Utility class that provides helper methods to saved and restore activity instances.
 * 
 * @author Elias
 *
 */
public class ActivityInstance {

	/**
	 * Saves data using GSON in a file using {@code KEY} as file name and {@code ACTIVITY} as folder name.<br>
	 * Data is serialized using a Gson instance with the default configuration.
	 * 
	 * @param context	The application context.
	 * @param activity	Activity name.
	 * @param key	String hosting the key used as file name.
	 * @param object	Object holding the data to be saved.
	 * @see #saveComplexMapDataGson(Context, String, String, Object)
	 */
	public static void saveDataGson ( final Context context , final String activity , final String key , final Object object ) {
		// Declare and initialize Gson object, used to serialize data
		Gson gson = new Gson ();
		// Save data using default GSON
		saveDataGson ( context , activity , key , object , gson );
	}
	
	/**
	 * Saves data using GSON in a file using {@code KEY} as file name and {@code ACTIVITY} as folder name.<br>
	 * Data is serialized using a Gson instance built using pre-defined configuration options to serialize complex map data.
	 * 
	 * @param context	The application context.
	 * @param activity	Activity name.
	 * @param key	String hosting the key used as file name.
	 * @param object	Object holding the data to be saved.
	 * @see #saveDataGson(Context, String, String, Object)
	 */
	public static void saveComplexMapDataGson ( final Context context , final String activity , final String key , final Object object ) {
		// Declare and initialize a Gson builder in order to construct a Gson instance
		// This is needed to set configuration options other than the default
		GsonBuilder gsonBuilder = new GsonBuilder ();
		// Enable complex map key serialization in order to serialize the map keys as JSON objects (instead of using toString() method)
		gsonBuilder.enableComplexMapKeySerialization ();
		// Create a Gson instance based on the current configuration, used to serialize data
		Gson gson = gsonBuilder.create ();
		// Save data using default GSON
		saveDataGson ( context , activity , key , object , gson );
	}
	
	/**
	 * Saves data using GSON in a file using {@code KEY} as file name and {@code ACTIVITY} as folder name.
	 * 
	 * @param context	The application context.
	 * @param activity	Activity name.
	 * @param key	String hosting the key used as file name.
	 * @param object	Object holding the data to be saved.
	 * @param gson	Gson object used to serialize objects.
	 */
	private static void saveDataGson ( final Context context , final String activity , final String key , final Object object , final Gson gson ) {
		// Check if the arguments are valid
		if ( context == null || activity == null || key == null || object == null || gson == null )
			// Do nothing
			return;
    	// Serialize object using Gson
    	String jsonString = gson.toJson ( object );
    	// Create / open folder using the activity name
		File folder = context.getDir ( activity , Context.MODE_PRIVATE );
		// Create / open file using the key
		File file = new File ( folder , key );
		// Declare a buffered writer to write the json string into
		BufferedWriter bufferedWriter = null;
		try {
			// Open stream
			bufferedWriter = new BufferedWriter ( new FileWriter ( file , false ) );
			// Write object in file
			bufferedWriter.append ( jsonString );
		} catch ( IOException exception ) {
			// Do nothing
		} finally {
			try {
				// Check if the buffered writer is valid
				if ( bufferedWriter != null )
					// Close buffered writer
					bufferedWriter.close ();
			} catch ( IOException exception ) {
				// Do nothing
			}  // End of try-catch block
		}  // End of try-catch-finally block
	}
	
	/**
	 * Reads data using GSON from a file using {@code KEY} as file name and {@code ACTIVITY} as folder name.
	 * 
	 * @param context	The application context.
	 * @param activity	Activity name.
	 * @param key	String hosting the key used as file name.
	 * @param type	The object's type that should be retrieved into.
	 * @return	Object holding the retrieved data.
	 */
	public static Object readDataGson ( final Context context , final String activity , final String key , final Type type ) {
		// Create / open folder using the activity name
		File folder = context.getDir ( activity , Context.MODE_PRIVATE );
		// Create / open file using the key
		File file = new File ( folder , key );
		// Declare a buffered reader to read the json string
		BufferedReader bufferedReader = null;
		// Declare and initialize Gson object, used to deserialize data
    	Gson gson = new Gson ();
    	String jsonString = null;
    	Object object = null;
		try {
			// Open stream
			bufferedReader = new BufferedReader ( new FileReader ( file ) );
	        StringBuilder sb = new StringBuilder();
	        String line = null;
	        // Read file content
	        while ( ( line = bufferedReader.readLine() ) != null) {
	            sb.append(line);
	            sb.append("\n");
	        }
	        jsonString = sb.toString();
	        // Deserialize object using Gson
	        object = gson.fromJson ( jsonString , type );
		} catch ( IOException exception ) {
			// Do nothing
		} finally {
			try {
				// Check if the buffered reader is valid
				if ( bufferedReader != null )
					// Close buffered reader
					bufferedReader.close ();
			} catch ( IOException exception ) {
				// Do nothing
			} // End of try-catch block
		} // End of try-catch-finally block
		// Return the retrieved object from Gson
		return object;
	}
	
	/**
	 * Deletes data stored using GSON in files using {@code KEY} as file name and {@code ACTIVITY} as folder name.
	 * 
	 * @param context	The application context.
	 * @param activity	Activity name.
	 * @param key	String hosting the key used as file name.
	 */
	public static void removeDataGson ( final Context context , final String activity , final String key ) {
		// Create / open folder using the activity name
		File folder = context.getDir ( activity , Context.MODE_PRIVATE );
		// Create / open file using the key
		File file = new File ( folder , key );
		// Determine if the file exists
		if ( file.exists () )
			// Delete the file
			file.delete ();
	}
	
}