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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

import de.greenrobot.dao.AbstractDao;

/**
 * Interface for activities that handle or manage synchronization to implement.
 * 
 * @author Elias
 *
 */
public interface Synchronization {

	/**
	 * Gets all the abstract dao tables that should be requested during sync, along with their appropriate entities types.
	 * 
	 * @return	Map holding abstract dao tables and the entities java reflection types as key value pairs.
	 */
	public Map < AbstractDao < ? , ? > , Type > getSyncTables_Get ();
	
	/**
	 * Gets all the abstract dao tables that should be delivered during sync, grouped in multiple lists to preserver relationships.
	 * 
	 * @return	List of abstract dao group tables (lists).
	 */
	public ArrayList < ArrayList < AbstractDao < ? , ? > > > getSyncTables_Set ();
	
}