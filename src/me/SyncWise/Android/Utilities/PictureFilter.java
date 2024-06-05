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

import android.annotation.SuppressLint;
import java.io.File;
import java.io.FileFilter;

/**
 * File filter implementation used to filter File objects based on whether they represent or not a picture.<br>
 * Additional functionality includes filtering based on the a search criteria for the file name.
 * 
 * @author Elias
 *
 */
public class PictureFilter implements FileFilter {

	/**
	 * String hosting the required characters to look for in the picture file name.
	 */
	private final String contains;
	
	/**
	 * Constructor.
	 */
	public PictureFilter () {
		this.contains = null;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param contains String hosting the required characters to look for in the picture file name.
	 */
	@SuppressLint("DefaultLocale")
	public PictureFilter ( final String contains ) {
		this.contains = contains.toLowerCase ();
	}
	
	/*
	 * Indicating whether a specific file should be included in a pathname list.
	 *
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	@SuppressLint("DefaultLocale")
	@Override
	public boolean accept ( File pathname ) {
		// Check if the file is not a directory
		if ( pathname.isDirectory () )
			return false;
		// Retrieve the file name
		String fileName = pathname.getName ().toLowerCase ();
		// Check if the file is a supported picture format
		if ( ! fileName.endsWith ( ".jpeg" ) && ! fileName.endsWith ( ".png" ) 
				&& ! fileName.endsWith ( ".bmp" ) && ! fileName.endsWith ( ".jpg" ) )
			return false;
		// Check if the file name is to be checked
		if ( contains == null )
			// No file name checking
			return true;
		// Otherwise check if the file name contains the search criteria
		if ( fileName.contains ( contains ) )
			return true;
		// Otherwise the file name does not match the search criteria
		return false;
	}

}