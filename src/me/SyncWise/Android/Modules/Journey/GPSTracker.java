package me.SyncWise.Android.Modules.Journey;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class GPSTracker extends Service  {
	public static Location _lastLocationG;
	public static Location _lastLocationN;
	private LocationListener listener;
	private LocationManager locationManager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		locationManager.removeUpdates(listener);
	}
 
	@Override
	public void onCreate() {
		  final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	      
		listener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				if(location.getProvider() .equals(LocationManager.GPS_PROVIDER )){
					if(GPSTracker._lastLocationG !=null && 
					   GPSTracker._lastLocationG.getLatitude() != location.getLatitude())
						GPSTracker._lastLocationG = location;	
					
						else 	if(GPSTracker._lastLocationG ==null)
						GPSTracker._lastLocationG = location;
					else
						GPSTracker._lastLocationG = null;	
					
					Log.e( "Time GPS",  simpleDateFormat.format( location.getTime())+"");
				}
				if(location.getProvider().equals(LocationManager.NETWORK_PROVIDER )){
				//	GPSTracker._lastLocationN = location;
					//Log.e( "Time Network", simpleDateFormat.format( location.getTime())+"");
					if(GPSTracker._lastLocationN !=null && 
							   GPSTracker._lastLocationN.getLatitude() != location.getLatitude())
								GPSTracker._lastLocationN = location;	
							
								else 	if(GPSTracker._lastLocationN ==null)
								GPSTracker._lastLocationN = location;
							else
								GPSTracker._lastLocationN = null;	
				}
				
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			@Override
			public void onProviderEnabled(String provider) {
			}

			@Override
			public void onProviderDisabled(String provider) {
//				Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(i);
			}
		};

		locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
		
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//			Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			startActivity(i);
		}
		
		if (locationManager != null) {
			 locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, listener);
			 locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, listener);
		}

	}

}
