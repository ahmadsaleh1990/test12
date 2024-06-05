package me.SyncWise.Android.Gson;

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.AppResources;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.PermissionsUtils;
import me.SyncWise.Android.Modules.DuoActivityTime;
import me.SyncWise.Android.Widgets.Baguette;
import me.SyncWise.Android.Widgets.Baguette.BackgroundColor;
import android.annotation.SuppressLint;
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
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class BaseTimerActivity extends Activity {

    public static long DISCONNECT_TIMEOUT = 50000; // 5 min = 05 * 60 * 1000 ms
    public static long time ;
    public static boolean isClosed ;

//    private static Handler       disconnectHandler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            // todo
//            return true;
//        }
//    });

//    private Runnable disconnectCallback = new Runnable() {
//        @Override
//        public void run() {
////            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
////            homeIntent.addCategory( Intent.CATEGORY_HOME );
////            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////            startActivity(homeIntent);
//       // 	Toast.makeText(BaseTimerActivity.this , "activity timer", 100000).show();
//        	   //Log.e("timer", "activity timer");
//        	Intent intent = new Intent ( BaseTimerActivity.this , DuoActivityTime.class );
//        	 intent.putExtra("aaa", "activity timer") ;
//        	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//    		startActivity (intent );
//        }
//    };
    @Override
protected void onUserLeaveHint(){
	super.onUserLeaveHint();
	//Thread.sleep(5000);
	
	
	//new IsBackgroundAsync().execute();
	
}
  
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	    BaseTimerListActivity. context=BaseTimerActivity.this;
	    BaseTimerListActivity.  resetDisconnectTimer();
	}

//	public void resetDisconnectTimer() {
//    	String userCode = me.SyncWise.Android.Database.DatabaseUtils.getCurrentUserCode ( BaseTimerActivity. this );
//		// Retrieve the company code
//		String companyCode = me.SyncWise.Android.Database.DatabaseUtils.getCurrentCompanyCode ( this );
//           BaseTimerListActivity.    disconnectHandler.removeCallbacks(  BaseTimerListActivity.  disconnectCallback);
//         DISCONNECT_TIMEOUT=PermissionsUtils.getDisconnectedDuo(this, userCode, companyCode);
//         BaseTimerListActivity.time=DISCONNECT_TIMEOUT;
//         BaseTimerListActivity.disconnectHandler.postDelayed(  BaseTimerListActivity.  disconnectCallback, BaseTimerListActivity.time);
//     //      BaseTimerListActivity.    disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
//          
//    }

    public void stopDisconnectTimer() {
    	
           BaseTimerListActivity.    disconnectHandler.removeCallbacks(  BaseTimerListActivity.  disconnectCallback);
         //  BaseTimerListActivity.stopDisconnectTimer();
    }

    @Override
    public void onUserInteraction() {
   	 BaseTimerListActivity.   resetDisconnectTimer();
     BaseTimerListActivity. context=BaseTimerActivity.this;  
    }

    public void onResume() {
        super.onResume();
        BaseTimerListActivity. context=BaseTimerActivity.this;
   	 BaseTimerListActivity.     resetDisconnectTimer();
        BaseTimerListActivity.isClosed=true;
        BaseTimerFragmentActivity.isClosed = true;
        BaseTimerActivity.isClosed = false;
		  if(LockStatus){// Baguette.showText(BaseTimerActivity.this, "jjj", BackgroundColor.GREEN);
//			  try {
//				wait(5000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			   LockStatus=false ;
		    	
	    	//	startService(new Intent( this, BackgroundJobService.class));
//			   Handler handler = new Handler();
//			    handler.postDelayed(new Runnable() {
//
//			        public void run() {
//			        	Intent intent = new Intent (  BaseTimerActivity.this , DuoActivityTime.class );
//				     	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//			    		startActivity (intent );
//			            
//			        }
//			    }, 6000);
			   
			   //   BaseTimerListActivity.    disconnectHandler.postDelayed(disconnectCallback, 2000);
		//	   displayPaymentDialog(this);
		  }
    }
    public void displayPaymentDialog(final Context context   ) {
    	AlertDialog	dialog ;
		try {
			
			boolean cancelable = false;
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					context);

			alertDialogBuilder.setCancelable(false);
			LinearLayout linearLayout = new LinearLayout(
					  this);
			linearLayout.setOrientation(1);

			if (!TextUtils.isEmpty(""))

				alertDialogBuilder.setTitle("");
			EditText	edtxtPaidAmount = new EditText(context);
		//	edtxtPaidAmount.setHint(R.string.paid_amount_hint);
			
			edtxtPaidAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
			linearLayout.addView(edtxtPaidAmount);
			alertDialogBuilder.setPositiveButton(
					AppResources.getInstance(context).getString(context,
							R.string.confirm_label), null);
			alertDialogBuilder.setNegativeButton(
					AppResources.getInstance(context).getString(context,
							R.string.cancel_label), null);

			alertDialogBuilder.setView(linearLayout);
			 	dialog = alertDialogBuilder.create();
			if (cancelable)
				dialog.setCanceledOnTouchOutside(true);
			dialog.show();
		} catch (Exception exception) {
			dialog = null;
		}
	}
    @Override
    public void onStop() {
        super.onStop();
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
//        if (pm.isScreenOn()&& !isApplicationInBackground()) {
//     stopDisconnectTimer();
//        }//else
    //    stopDisconnectTimer();

//        if(BaseTimerActivity.isClosed && BaseTimerListActivity.isClosed && BaseTimerFragmentActivity.isClosed){
//        	 //stopDisconnectTimer();
//        }
//        else{
//        	stopDisconnectTimer();
//        }
//        BaseTimerActivity.isClosed=true;
//      //  stopDisconnectTimer();
    }
    Boolean LockStatus=false; 
	/*
	 * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause(), for your activity to start interacting with the user.
	 * 
	 * @see android.app.Activity#onResume()
	 */
 
    @SuppressLint("NewApi")
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
        		Intent intent = new Intent ( BaseTimerActivity.this , DuoActivityTime.class );
            	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            	startActivity (intent );
        	}
        }

        @Override
        protected String doInBackground(String... strings) {
         	String userCode = me.SyncWise.Android.Database.DatabaseUtils.getCurrentUserCode ( BaseTimerActivity. this );
    		// Retrieve the company code
    		String companyCode = me.SyncWise.Android.Database.DatabaseUtils.getCurrentCompanyCode ( BaseTimerActivity. this  );
    		double sleep1=PermissionsUtils.getSleepDuo(BaseTimerActivity. this, userCode, companyCode);
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


