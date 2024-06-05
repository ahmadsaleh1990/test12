/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Algorithms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Helper Class used to apply and compute various security algorithms.
 * 
 * @author Elias
 *
 */
public class Hash {
	
	/**
	 * Generates a cryptographic hash using the SHA 512 algorithm for the provided message.
	 * 
	 * @param message	Message to hash.
	 * @return	The SHA 512 digest of the message.
	 * @throws NoSuchAlgorithmException	SHA 512 is not supported.
	 * @throws UnsupportedEncodingException	ASC-II is not supported.
	 * @see #SHA512(File)
	 */
	public static String SHA512 ( final String message ) throws NoSuchAlgorithmException , UnsupportedEncodingException {
		// Retrieve an instance of the SHA 512 message digest
	    MessageDigest messageDigest = MessageDigest.getInstance ( "SHA-512" );
	    // Set the message
	    messageDigest.update ( message.getBytes () );
	    // Compute the SHA 512 hash
	    byte [] sha512Hash = messageDigest.digest ();
	    // Convert the hash to hexadecimal
	    return convertToHex ( sha512Hash );
	}
	
	/**
	 * Generates a cryptographic hash using the SHA 512 algorithm for the provided file.
	 * 
	 * @param file	{@link java.io.File File} to hash.
	 * @return	The SHA 512 digest of the file.
	 * @throws NoSuchAlgorithmException	SHA 512 is not supported.
	 * @throws UnsupportedEncodingException	ASC-II is not supported.
	 * @throws FileNotFoundException	The specified file cannot be found.
	 * @throws IOException	I/O-related error.
	 * @see #SHA512(String)
	 */
	public static String SHA512 ( final File file ) throws NoSuchAlgorithmException , UnsupportedEncodingException , FileNotFoundException , IOException {
		// Retrieve an instance of the SHA 512 message digest
	    MessageDigest messageDigest = MessageDigest.getInstance ( "SHA-512" );
	    // Open a input stream in order to read the file's bytes
        InputStream is = new FileInputStream ( file );
        // Declare and initialize a buffer of 8 KB
        byte [] buffer = new byte [ 8192 ];
        // Declare and initialize an integer used to host the number of bytes read in the buffer
        int read;
        // Read the file 8 KB at a time
        while ( ( read = is.read ( buffer ) ) > 0 )
        	// Update the SHA 512 key using the current 8 KB of
        	messageDigest.update ( buffer , 0 , read );
	    // Compute the SHA 512 hash
	    byte [] sha512Hash = messageDigest.digest ();
	    // Convert the hash to hexadecimal
	    return convertToHex ( sha512Hash );
	}
	
	/**
	 * Converts a array of bytes into a readable (string) hexadecimal representation format.
	 * 
	 * @param data	Array of bytes to convert to hexadecimal.
	 * @return	String holding the converted hexadecimal number.
	 */
	private static String convertToHex ( final byte [] data ) {
		// Declare and initialize a string builder used to build the hexadecimal number
		StringBuilder buffer = new StringBuilder();
		// Iterate over all the bytes
	    for (byte b : data) {
	    	// Apply a mask used to retrieve the higher half (4 bits) (most significant) of the byte
	        int halfbyte = (b >>> 4) & 0x0F;
	        // Declare and initialize a counter
	        int two_halfs = 0;
	        // Iterate over the whole byte (each iteration handles half a byte)
	        do {
	        	// Append to the hexadecimal number the current 4 bits
	        	buffer.append ( ( 0 <= halfbyte ) && ( halfbyte <= 9 ) ? (char) ( '0' + halfbyte ) : (char) ( 'a' + ( halfbyte - 10 ) ) );
	        	// Retrieve the other (4 bits, lower half, least significant) half of the byte
	            halfbyte = b & 0x0F;
	        } while ( two_halfs ++ < 1 );
	    } // End for each
	    // Return the hexadecimal number
	    return buffer.toString ();
	}

}