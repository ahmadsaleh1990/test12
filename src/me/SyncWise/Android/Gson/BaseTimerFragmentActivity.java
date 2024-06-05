package me.SyncWise.Android.Gson;

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Gson.BaseTimerActivity.IsBackgroundAsync;
 
import me.SyncWise.Android.Modules.DuoActivityTime;
import me.SyncWise.Android.Widgets.Baguette;
import android.app.ActivityManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

public class BaseTimerFragmentActivity extends FragmentActivity {

    public static long DISCONNECT_TIMEOUT = 50000; // 5 min = 05 * 60 * 1000 ms
    public static   long time =DISCONNECT_TIMEOUT;
    public static boolean isClosed ;

//    private static Handler       disconnectHandler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            // todo
//            return true;
//        }
//    });
// private Runnable timerCallBack = new Runnable() {
//		
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			time=time-1;
//		}
//	};
//    private Runnable disconnectCallback = new Runnable() {
//        @Override
//        public void run() {
////            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
////            homeIntent.addCategory( Intent.CATEGORY_HOME );
////            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////            startActivity(homeIntent);
// //       	Toast.makeText(BaseTimerFragmentActivity.this , "frag timer", 100000).show();
//        	 
//        	Intent intent = new Intent (  BaseTimerFragmentActivity.this , DuoActivityTime.class );
//        	intent.putExtra("aaa", "frag time") ;
//        	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//    		startActivity (intent );
//        }
//    };
    @Override
protected void onUserLeaveHint(){
	super.onUserLeaveHint();
	//new IsBackgroundAsync().execute();
}
//    public void resetDisconnectTimer() {
//    	String userCode = me.SyncWise.Android.Database.DatabaseUtils.getCurrentUserCode ( BaseTimerFragmentActivity. this );
//		// Retrieve the company code
//		String companyCode = me.SyncWise.Android.Database.DatabaseUtils.getCurrentCompanyCode ( this );
//           BaseTimerListActivity.    disconnectHandler.removeCallbacks(BaseTimerListActivity.   disconnectCallback);
//          DISCONNECT_TIMEOUT=PermissionsUtils.getDisconnectedDuo(this, userCode, companyCode);
//          BaseTimerListActivity.time=DISCONNECT_TIMEOUT;
//              BaseTimerListActivity.    disconnectHandler.postDelayed(BaseTimerListActivity.   disconnectCallback, BaseTimerListActivity.time);
//     //      BaseTimerListActivity.    disconnectHandler.postDelayed(timerCallBack, 1);
//    }
    public void resetDisconnectTimer() {
		   BaseTimerListActivity. context=BaseTimerFragmentActivity.this;
		 BaseTimerListActivity. resetDisconnectTimer();
    }
    public void stopDisconnectTimer() {
           BaseTimerListActivity.    disconnectHandler.removeCallbacks(BaseTimerListActivity.   disconnectCallback);
    }

    @Override
    public void onUserInteraction() {
    	 BaseTimerListActivity.  resetDisconnectTimer();
    }
    
 
    
    
    @Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0); 
		   BaseTimerListActivity. context=BaseTimerFragmentActivity.this;
		 BaseTimerListActivity. resetDisconnectTimer();
		
	}
	@Override
    public void onResume() {
        super.onResume();
   	 BaseTimerListActivity.  resetDisconnectTimer();
        BaseTimerListActivity. context=BaseTimerFragmentActivity.this;
        BaseTimerListActivity.isClosed=true;
        BaseTimerFragmentActivity.isClosed = false;
        BaseTimerActivity.isClosed = true;
//		  if(LockStatus){
////			  try {
////				wait(5000);
////			} catch (InterruptedException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
//			   LockStatus=false ;
//			   Handler handler = new Handler();
//			    handler.postDelayed(new Runnable() {
//
//			        public void run() {
//			        	Intent intent = new Intent (  BaseTimerFragmentActivity.this , DuoActivityTime.class );
//				     	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//			    		startActivity (intent );
//			             
//			        }
//			    }, 3000);
	    //		startService(new Intent( this, BackgroundJobService.class));
		    	
//		  }
    }

    @Override
    public void onStop() {
        super.onStop();
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
//        if (pm.isScreenOn() && !isApplicationInBackground()) {
//        stopDisconnectTimer();
//        }
//        else
//        stopDisconnectTimer();

//
//        if(BaseTimerActivity.isClosed && BaseTimerListActivity.isClosed && BaseTimerFragmentActivity.isClosed){
//        	 //stopDisconnectTimer();
//        }
//        else{
//        	stopDisconnectTimer();
//        }
//        BaseTimerFragmentActivity.isClosed = true;
//      //  stopDisconnectTimer();
    }
    Boolean LockStatus=false; 
	/*
	 * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause(), for your activity to start interacting with the user.
	 * 
	 * @see android.app.Activity#onResume()
	 */
 
    private boolean isApplicationInBackground() {
		ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
		ActivityManager.getMyMemoryState(myProcess);
	boolean	isInBackground = myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
		if(isInBackground) {
			 return true;
	    }    
	    return false;
	}
    @Override
	protected void onPause () {
		// Superclass method call
		super.onPause ();
	//	stopDisconnectTimer();
//		  if(isApplicationInBackground()) {
//		 LockStatus=true;
////		   	Intent intent = new Intent (  this , DuoActivityTime.class );
////	     	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
////    		startActivity (intent );
//		 }
//		 PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//
//		    boolean screenOn;
////		    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
////		        screenOn = pm.isInteractive();
////		    } else {
//		        screenOn = pm.isScreenOn();
////		    }
//
//		    if (!screenOn) {
//		    	Intent intent = new Intent (  this , DuoActivityTime.class );
//		     	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//	    		startActivity (intent );
//		        // Screen is still on, so do your thing here
//		    }
		   
		// Dismiss any displayed dialogs (to avoid activity context leak)
		AppDialog.getInstance ().dismiss ();
		// Remove any displayed baguette
		Baguette.remove ( this );
	}
    public class IsBackgroundAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String status) {
        	if(status.equals("B")){
        		Intent intent = new Intent ( BaseTimerFragmentActivity.this , DuoActivityTime.class );
            	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            	startActivity (intent );
        	}
        }

        @Override
        protected String doInBackground(String... strings) {
          	String userCode = me.SyncWise.Android.Database.DatabaseUtils.getCurrentUserCode ( BaseTimerFragmentActivity. this );
    		// Retrieve the company code
    		String companyCode = me.SyncWise.Android.Database.DatabaseUtils.getCurrentCompanyCode ( BaseTimerFragmentActivity. this  );
    		double sleep1=PermissionsUtils.getSleepDuo(BaseTimerFragmentActivity. this, userCode, companyCode);
        try {
			Thread.sleep((long) sleep1 );
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
		//ActivityManager.getMyMemoryState(myProcess);
    	boolean	isInBackground = myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
		if(isInBackground) {
			 return "B";
	    }   
        	return "F";
        }
    }
}


