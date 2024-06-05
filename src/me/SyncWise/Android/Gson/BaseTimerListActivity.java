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
import android.util.Log;
import android.widget.Toast;

public class BaseTimerListActivity extends ListActivity  {

    public static long DISCONNECT_TIMEOUT = 50000; // 5 min = 05 * 60 * 1000 ms
   public static Context context ;
    public static boolean isClosed ;
    public static   long time =DISCONNECT_TIMEOUT;
    public static Handler disconnectHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // todo
            return true;
        }
    });

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		   context=BaseTimerListActivity.this;
		   resetDisconnectTimer();
	        BaseTimerListActivity.isClosed=false;
		
	}
	public static Runnable disconnectCallback = new Runnable() {
        @Override
        public  void run() {
//            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
//            homeIntent.addCategory( Intent.CATEGORY_HOME );
//            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(homeIntent);
        //	Toast.makeText(BaseTimerListActivity.this , "list timer", 100000).show();
        	
         	Intent  intent = new Intent (  context , DuoActivityTime.class );
         	intent.putExtra("aaa", "list timer") ;
         	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
         	//BaseTimerListActivity.this.
         	context.	startActivity (intent );
        }
    };
    
    
    
//    private Runnable timerCallBack = new Runnable() {
//		
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			time=time-1000;
//		}
//	};
    
    
    
    @Override
protected void onUserLeaveHint(){
	super.onUserLeaveHint();
	//new IsBackgroundAsync().execute();
}
    public static void resetDisconnectTimer() {
    	String userCode = me.SyncWise.Android.Database.DatabaseUtils.getCurrentUserCode ( context );
		// Retrieve the company code
		String companyCode = me.SyncWise.Android.Database.DatabaseUtils.getCurrentCompanyCode ( context );
        disconnectHandler.removeCallbacks(disconnectCallback);
        DISCONNECT_TIMEOUT=PermissionsUtils.getDisconnectedDuo(context, userCode, companyCode);
        time=DISCONNECT_TIMEOUT;
        disconnectHandler.postDelayed(disconnectCallback, time);
      //  disconnectHandler.postDelayed(timerCallBack, 1000);
    }

    public static void stopDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction() {
        resetDisconnectTimer();
    }

    public void onResume() {
        super.onResume();
        context=BaseTimerListActivity.this;
        resetDisconnectTimer();
        BaseTimerListActivity.isClosed=false;
        BaseTimerFragmentActivity.isClosed = true;
        BaseTimerActivity.isClosed = true;
 //       disconnectHandler.postDelayed(timerCallBack, 1000);
//		  if(LockStatus){
////			  try {
////				wait(5000);
////			} catch (InterruptedException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
//			   LockStatus=false ;
//		    	
//	    	//	startService(new Intent( this, BackgroundJobService.class));
//			   Handler handler = new Handler();
//			    handler.postDelayed(new Runnable() {
//
//			        public void run() {
//			        	Intent intent = new Intent (  BaseTimerListActivity.this , DuoActivityTime.class );
//				     	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//			    		startActivity (intent );
//			             
//			        }
//			    }, 3000);
//		  }
    }
 
    
    @Override 
    public void onStop() {
        super.onStop();
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
//        if (pm.isScreenOn()&& !isApplicationInBackground()) {
//         stopDisconnectTimer();
//        }
//        else
//        stopDisconnectTimer();


//        if(BaseTimerActivity.isClosed && BaseTimerListActivity.isClosed && BaseTimerFragmentActivity.isClosed){
//        	 //stopDisconnectTimer();
//        }
//        else{
//        	stopDisconnectTimer();
//        }
//        BaseTimerListActivity.isClosed=true;
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
//		   
//		// Dismiss any displayed dialogs (to avoid activity context leak)
//		AppDialog.getInstance ().dismiss ();
//		// Remove any displayed baguette
		Baguette.remove ( this );
	}
    public class IsBackgroundAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String status) {
        	if(status.equals("B")){
        		Intent intent = new Intent ( BaseTimerListActivity.this , DuoActivityTime.class );
            	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            	startActivity (intent );
        	}
        }

        @Override
        protected String doInBackground(String... strings) {
         	String userCode = me.SyncWise.Android.Database.DatabaseUtils.getCurrentUserCode ( BaseTimerListActivity. this );
    		// Retrieve the company code
    		String companyCode = me.SyncWise.Android.Database.DatabaseUtils.getCurrentCompanyCode ( BaseTimerListActivity. this  );
    		double sleep1=PermissionsUtils.getSleepDuo(BaseTimerListActivity. this, userCode, companyCode);
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


