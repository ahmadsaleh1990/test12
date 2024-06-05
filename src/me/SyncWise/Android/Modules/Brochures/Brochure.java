/**
 * Copyright 2015 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Modules.Brochures;


public class Brochure {

	private String name;
	/**
	 * Constructor.
	 * 
	 * @param name
	 * @param path
	 */
	public Brochure ( String name , String path ) {
		super ();
		this.name = name;
		this.path = path;
	}

	private String path;
	
	/**
	 * Getter - {@link #name}
	 * 
	 * @return the name
	 */
	public String getName () {
		return name;
	}
	
	/**
	 * Setter - {@link #name}
	 * 
	 * @param name the name to set
	 */
	public void setName ( String name ) {
		this.name = name;
	}
	
	/**
	 * Getter - {@link #path}
	 * 
	 * @return the path
	 */
	public String getPath () {
		return path;
	}
	
	/**
	 * Setter - {@link #path}
	 * 
	 * @param path the path to set
	 */
	public void setPath ( String path ) {
		this.path = path;
	}
	
}
