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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.SyncWise.Android.Database.ClientPicturesUtils;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;



public class  BroadcastReceiverDownloads  extends BroadcastReceiver{
	
	
    @Override
    public void onReceive(Context context, Intent intent) {
       DownloadManager dm= (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    	long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
    	Query query = new Query();
    	query.setFilterById(downloadId);
    	Cursor cur = dm.query(query);
    	File tempFile = null;
    	File picture = null;
    	if(cur.moveToFirst()){
    		String filename = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_TITLE));

    		if(cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION)).equals("CLIENT_PICTURES")){
    			File folder = context.getDir ( ClientPicturesUtils.FOLDER , Context.MODE_PRIVATE );
    			picture = new File ( folder , filename);
    			File download = Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_DOWNLOADS);
    			tempFile = new File(download , filename );
    		}
    		else if(cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION)).equals("BROCHURES")){
    			File folder = new File (Environment.DIRECTORY_DOWNLOADS + "/BROCHURES/" );
    			picture = new File ( folder , filename);
    			File download = Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_DOWNLOADS);
    			tempFile = new File(download , filename );
    		}
    		if(tempFile != null && picture !=null){
     			try {
     				
					copyDirectoryOneLocationToAnotherLocation(tempFile,picture);
				} catch (IOException e) {

		            Log.e("tag", e.getMessage());
					e.printStackTrace();
				} 
				tempFile.delete();
    		}
    	}
    	cur.close();
    }
        

public static void copyDirectoryOneLocationToAnotherLocation(File sourceLocation, File targetLocation)
	    throws IOException {



	if (sourceLocation.exists()){
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.v("file", "exists");
	}
	    InputStream in = new FileInputStream(sourceLocation);
	    OutputStream out = new FileOutputStream(targetLocation);

	    // Copy the bits from instream to outstream
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    in.close();
	    out.flush();
	    out.close();
	    
	    File test = new File (targetLocation.getAbsolutePath());
	    Log.e("test","exists:"+ test.exists());
	}
}
