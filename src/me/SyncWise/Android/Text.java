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

import android.text.TextUtils;

/**
 * Helper Class used to apply various text properties.
 * 
 * @author Elias
 *
 */
public class Text {

	/**
	 * Converts text from vertical to horizontal alignment by eliminating new lines after each character.
	 * 
	 * @param vertical	String holding the text in the vertical alignment.
	 * @return	String holding the text in the horizontal alignment, or an empty string if the provided text is invalid.
	 */
	public static String getHorizontal ( final String vertical ) {
		// Determine if the provided text is valid
		if ( TextUtils.isEmpty ( vertical ) )
			// Invalid text
			return "";
		// Otherwise the provided text is valid
		// Declare and initialize a string builder used to hold the horizontal text
		StringBuilder horizontal = new StringBuilder ();
		// Compute the size of the vertical text
		int size = vertical.length ();
		// Iterate over all characters in the string
		for ( int i = 0 ; i < size - 1 ; i ++ )
			// Add the current character, and skip the next one
			horizontal.append ( vertical.charAt ( i ++ ) );
		// Return the computed horizontal text
		return horizontal.toString ();
	}
	
	/**
	 * Converts text from horizontal to vertical alignment by adding new lines after each character.
	 * 
	 * @param horizontal	String holding the text in the horizontal alignment.
	 * @return	String holding the text in the vertical alignment, or an empty string if the provided text is invalid.
	 */
	public static String getVertical ( final String horizontal ) {
		// Determine if the provided text is valid
		if ( TextUtils.isEmpty ( horizontal ) )
			// Invalid text
			return "";
		// Otherwise the provided text is valid
		// Declare and initialize a string builder used to hold the vertical text
		StringBuilder vertical = new StringBuilder ();
		// Compute the size of the horizontal text
		int size = horizontal.length ();
		// Iterate over all characters in the string (except the last character)
		for ( int i = 0 ; i < size - 1 ; i ++ )
			// Add the current character, followed by a new line
			vertical.append ( horizontal.charAt ( i ) ).append ( "\n" );
		// Add the last character
		vertical.append ( horizontal.charAt ( size - 1 ) );
		// Return the computed vertical text
		return vertical.toString ();
	}
	
}