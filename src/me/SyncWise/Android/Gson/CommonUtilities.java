/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Gson;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import me.SyncWise.Android.Database.ClientDues;
import me.SyncWise.Android.Database.TransactionHeaders;

import android.annotation.SuppressLint;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Utilities class providing methods and constants related to GSON.
 * 
 * @author Elias
 *
 */
public class CommonUtilities {

	/**
	 * String hosting the Date format used to properly convert date objects.
	 */
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	
	/**
	 * Gets the date format used to properly convert date objects.
	 * 
	 * @return	A simple date format for formatting and parsing dates in a locale-sensitive manner.
	 */
	@SuppressLint("SimpleDateFormat")
	public static SimpleDateFormat getDateFormat () {
		// Compute and return the date format used in de/serialization in Json
		return new SimpleDateFormat ( DATE_FORMAT );
	}
	
    /**
     * Gets the object out of the json string.
     * 
     * @param gson	Gson object used to de/serialize objects.
     * @param json	String hosting the json serialized object.
     * @param type	Class used to deserialize the message to a specific object type. 
     * @return	Deserialized object from json, or {@code NULL} if invalid.
     */
    public static Object getObject ( final Gson gson , final String json , final Class < ? > type ) {
    	try {
    		// Deserialize and return object
    		return gson.fromJson ( json , type );
    	} catch ( Exception exception ) {
			// Invalid deserialization
    		return null;
		} // End of try-catch block
    }
	
    /**
     * Builds and returns a Gson object using a specific configuration that matches the GET needs.
     * 
     * @return	Gson object used to de/serialize objects.
     */
    public static Gson buildGetGson () {
		// Declare and initialize a Gson builder in order to construct a Gson instance
		// This is needed to set configuration options other than the default
		GsonBuilder gsonBuilder = new GsonBuilder ();
		// Configures Gson for custom serialization or deserialization related to Date objects
		gsonBuilder.registerTypeAdapter ( java.util.Date.class , new JsonDeserializer < java.util.Date > () {
			/*
			 * Gson invokes this call-back method during deserialization when it encounters a field of the specified type.
			 *
			 * @see com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement, java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)
			 */
			public java.util.Date deserialize ( JsonElement json , Type type , JsonDeserializationContext context ) throws JsonParseException {
				try {
					// Parse and return the date in its string representation to a Date object
					return getDateFormat ().parse ( json.getAsJsonPrimitive ().getAsString () );
				} catch ( java.text.ParseException exception ) {
					// A parsing occur occurred
					throw new JsonParseException ( exception.getMessage () );
				} // End of try-catch block
			}
		} );
		// Create and return a Gson instance based on the current configuration
		return gsonBuilder.create ();
    }
    
    /**
     * Builds and returns a Gson object using a specific configuration that matches the SET needs.
     * 
     * @return	Gson object.
     */
    public static Gson buildSetGson () {
		// Declare and initialize a Gson builder in order to construct a Gson instance
		// This is needed to set configuration options other than the default
		GsonBuilder gsonBuilder = new GsonBuilder ();
		// Configures Gson for custom serialization or deserialization related to Date objects
		gsonBuilder.registerTypeAdapter ( java.util.Date.class , new JsonSerializer < java.util.Date > () {
			/* 
			 * Gson invokes this call-back method during serialization when it encounters a field of the specified type.
			 * 
			 * com.google.gson.JsonSerializer#serialize(T , java.lang.reflect.Type , com.google.gson.JsonSerializationContext )
			 */
			@Override
			public JsonElement serialize ( java.util.Date source , Type type , JsonSerializationContext context ) {
				// Return the date string representation as a Json primitive value
				return new JsonPrimitive ( getDateFormat ().format ( source ) );
			} 
		} );
		// Configure Gson to apply the passed in exclusion strategy during serialization
		gsonBuilder.addSerializationExclusionStrategy ( new ExclusionStrategy () {
			/*
			 * Indicates if the field should be ignored
			 *
			 * @see com.google.gson.ExclusionStrategy#shouldSkipField(com.google.gson.FieldAttributes)
			 */
			@Override
			public boolean shouldSkipField ( FieldAttributes fieldAttributes ) {
				// Skip field if its name is equals to ID
				return fieldAttributes.getName ().equals ( "id" )  
						|| ( fieldAttributes.getDeclaringClass ().equals ( ClientDues.class ) && fieldAttributes.getName ().equals ( "paidAmount" ) )
						|| ( fieldAttributes.getDeclaringClass ().equals ( TransactionHeaders.class ) && fieldAttributes.getName ().equals ( "paidAmount" ) );
			}
			
			/*
			 * Indicates if the class should be ignored
			 *
			 * @see com.google.gson.ExclusionStrategy#shouldSkipClass(java.lang.Class)
			 */
			@Override
			public boolean shouldSkipClass ( Class < ? > arg ) {
				// Never skip a class
				return false;
			}
		} );
		// Create and return a Gson instance based on the current configuration
		return gsonBuilder.create ();
    }
    
}