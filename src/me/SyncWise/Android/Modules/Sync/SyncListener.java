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

import java.util.ArrayList;

import de.greenrobot.dao.AbstractDao;

/**
 * Interface definition for a callback to be invoked for various Synchronization events.
 * <ul>
 * <li>Before synchronization validation : {@link SyncListener#onPreFinish(int)}</li>
 * <li>On a successful synchronization during a GET : {@link SyncListener#onGetSuccess(AbstractDao, ArrayList, int)}</li>
 * <li>On a successful synchronization during a SET : {@link SyncListener#onSetSuccess(ArrayList, int)}</li>
 * <li>On a failed synchronization during a GET : {@link SyncListener#onGetFailure(AbstractDao, int)}</li>
 * <li>On a failed synchronization during a SET : {@link SyncListener#onSetFailure(int)}</li>
 * <li>After synchronization validation : {@link SyncListener#onPostFinish(int)}</li>
 * </ul>
 * 
 * @author Elias
 *
 */
public interface SyncListener {

	/**
	 * Call back method executed before determining if the sync result is a success or a failure.
	 * 
	 * @param requestCode	Integer holding the sync request code.
	 */
	void onPreFinish ( final int requestCode );
	
	/**
	 * Call back method executed if the get sync process is a success.
	 * 
	 * @param dao	Reference to the AbstractDao, which in turns references the main table.
	 * @param entities	Entities list retrieved.
	 * @param requestCode	Integer holding the sync request code.
	 */
	void onGetSuccess ( AbstractDao < ? , ? > dao , ArrayList < Object > entities , final int requestCode );
	
	/**
	 * Call back method executed if the set sync process is a success.
	 * 
	 * @param entites	Reference to the AbstractDao, which in turns references the main table.
	 * @param requestCode	Integer holding the sync request code.
	 */
	void onSetSuccess ( final ArrayList < ArrayList < Object > > entites , final int requestCode );
	
	/**
	 * Call back method executed if the get sync process is a failure.
	 * 
	 * @param dao	Reference to the AbstractDao, which in turns references the main table.
	 * @param requestCode	Integer holding the sync request code.
	 */
	void onGetFailure ( AbstractDao < ? , ? > dao , final int requestCode );
	
	/**
	 * Call back method executed if the set sync process is a failure.
	 * 
	 * @param requestCode	Integer holding the sync request code.
	 */
	void onSetFailure ( final int requestCode );
	
	/**
	 * Call back method executed after the sync process executed.
	 * 
	 * @param requestCode	Integer holding the sync request code.
	 */
	void onPostFinish ( final int requestCode );
	
}