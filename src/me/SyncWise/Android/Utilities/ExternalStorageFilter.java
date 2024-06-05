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

import java.io.File;
import java.io.FileFilter;

import android.annotation.SuppressLint;

/**
 * File filter implementation used to filter File objects based on whether they represent or not external storage devices.
 * 
 * @author Elias
 *
 */
public class ExternalStorageFilter implements FileFilter {

	/*
	 * Indicating whether a specific file should be included in a pathname list.
	 *
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	@SuppressLint("DefaultLocale") 
	@Override
	public boolean accept ( File pathname ) {
		// Check if the file is a directory
		if ( ! pathname.isDirectory () )
			return false;
		// Check if the file name contains 'sdcard'
		else if ( pathname.getName ().toLowerCase ().contains ( "sdcard" ) )
			return true;
		// Otherwise the file is not an external storage
		else
			return false;
	}
	
}