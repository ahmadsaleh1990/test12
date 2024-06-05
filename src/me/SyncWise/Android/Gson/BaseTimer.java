package me.SyncWise.Android.Gson;

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Modules.DuoActivityTime;
import me.SyncWise.Android.Widgets.Baguette;
import me.SyncWise.Android.Widgets.Baguette.BackgroundColor;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.LinearLayout;

public class BaseTimer  {

    public static long DISCONNECT_TIMEOUT = 50000; // 5 min = 05 * 60 * 1000 ms
   
    
    private static Handler disconnectHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // todo
            return true;
        }
    });

//    private Runnable disconnectCallback = new Runnable() {
//        @Override
//        public void run() {
// 
//        	
//        	Intent intent = new Intent (mContext, DuoActivityTime.class );
//        	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//    		startActivity (intent );
//        }
//    };
 
//    public void resetDisconnectTimer() {
////    	String userCode = me.SyncWise.Android.Database.DatabaseUtils.getCurrentUserCode ( BaseTimer. this );
////		// Retrieve the company code
////		String companyCode = me.SyncWise.Android.Database.DatabaseUtils.getCurrentCompanyCode ( this );
//        disconnectHandler.removeCallbacks(disconnectCallback);
////         DISCONNECT_TIMEOUT=PermissionsUtils.getDisconnectedDuo(BaseTimer.this, userCode, companyCode);
//        
//        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
//    }
//
//    public void stopDisconnectTimer() {
//        disconnectHandler.removeCallbacks(disconnectCallback);
//    }

   
  
    

    
    
     
 
 
 
    
 
    
}


