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

import java.io.File;
import java.util.ArrayList;

import me.SyncWise.Android.R;
import me.SyncWise.Android.Gson.BaseTimerListActivity;
import me.SyncWise.Android.Modules.Brochures.BrochureArrayAdapter.ViewHolder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


public class BrochuresActivity extends BaseTimerListActivity {
	
	/*
	 * Called when the activity is starting.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate ( Bundle savedInstanceState ) {
		// Superclass method call 
		super.onCreate ( savedInstanceState );
		// Set the activity content from a layout resource.
		setContentView ( R.layout.brochure_activity );
		// Change the title associated with this activity
		setTitle ( "BROCHURES" );
		// Define the empty list view
		getListView ().setEmptyView ( findViewById ( R.id.empty_list_view ) );
		// Display empty list label
		( (TextView) findViewById ( R.id.empty_list_view ) ).setText ( "No brochures." );
		
		// Determine if the list view has a valid adapter
		if ( getListAdapter () == null )
			// Populate the list by setting a new adapter
			new PopulateList ().execute ();
	}
	
	/*
	 * This method will be called when a brochure in the list is selected.
	 *
	 * @see android.support.v4.app.ListFragment#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	public void onListItemClick ( ListView listView , View view , int position , long id ) {
		ViewHolder viewHolder = (ViewHolder) view.getTag ();
		File brochureFile = new File ( viewHolder.brochure.getPath () );
		Uri uri = Uri.fromFile(brochureFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setDataAndType(uri, "application/pdf" );
        try {

            startActivityForResult ( intent , 1 );

        } catch (ActivityNotFoundException e) {
            // No application to view, ask to download one
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Application Found");
            builder.setMessage("Download one from Android Market?");
            builder.setPositiveButton("Yes, Please",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                int which) {
                            Intent marketIntent = new Intent(
                                    Intent.ACTION_VIEW);
                            marketIntent.setData(Uri
                                    .parse("market://details?id=com.adobe.reader"));
                            startActivity(marketIntent);
                        }
                    });
            builder.setNegativeButton("No, Thanks", null);
            builder.create().show();
        }
	}
	
	private class PopulateList extends AsyncTask < Void , Void , ArrayList < Brochure > > {
		/*
		 * Method to perform a computation on a background thread.
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@SuppressLint("DefaultLocale") @Override
		protected ArrayList < Brochure > doInBackground ( Void ... params ) {
			File downloadFolder = Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_DOWNLOADS);
			File brochureFolder = new File ( downloadFolder , "Brochures" );
			brochureFolder.mkdirs ();
			String brochuresArray [] = brochureFolder.list ();
			ArrayList < Brochure > brochures = new ArrayList < Brochure > ();
			for ( String brochurePath : brochuresArray ) {
				if ( brochurePath.toLowerCase ().endsWith ( ".pdf" ) ) {
					brochures.add ( new Brochure ( brochurePath.substring ( 0 , brochurePath.length () - 4 ) , brochureFolder + "/" + brochurePath ) );
				}
			}
			return brochures;
		}
		
		/*
		 * Runs on the UI thread after doInBackground(Params...).
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute ( ArrayList < Brochure > brochures ) {
			setListAdapter ( new BrochureArrayAdapter ( BrochuresActivity.this , R.layout.brochure_activity_item , brochures ) );
		}
	}
	
}