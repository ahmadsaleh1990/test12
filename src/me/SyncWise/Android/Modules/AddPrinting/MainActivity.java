package me.SyncWise.Android.Modules.AddPrinting;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import me.SyncWise.Android.AppDialog;
import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.Printers;
import me.SyncWise.Android.Database.PrintersDao;
import me.SyncWise.Android.Database.TransactionHeadersUtils;
import me.SyncWise.Android.Database.UserPasswordsUtils;
import me.SyncWise.Android.Widgets.Baguette;
import me.SyncWise.Android.Widgets.Baguette.BackgroundColor;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

 

 

public class MainActivity  extends   Activity implements AdapterView.OnItemClickListener{
    private static final String TAG = "MainActivity";
    public static ProgressDialog dialog_loading;

    BluetoothAdapter mBluetoothAdapter;
    Button btnEnableDisable_Discoverable;

    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<BluetoothDevice>();

    public DeviceListAdapter mDeviceListAdapter;

    ListView lvNewDevices;
    private Set<BluetoothDevice>pairedDevices;
    ListView lv;
    HashMap<String, String> Map_paired=new HashMap<String, String>();
    HashMap<String, String> Map_unpaired=new HashMap<String, String>();
    static BluetoothSocket mmSocket;
    static OutputStream mmOutputStream;
    static InputStream mmInputStream;
    static Thread workerThread;
    static Context instance;
    public static final int ICE_CREAM_SANDWICH = 14;
    public static final int ICE_CREAM_SANDWICH_MR1 = 15;
    public static final int JELLY_BEAN = 16;
    public static final int JELLY_BEAN_MR1 = 17;
    public static final int JELLY_BEAN_MR2 = 18;
    public static final int KITKAT = 19;
    public static final int KITKAT_WATCH = 20;
    public static final int LOLLIPOP = 21;
    public static final int LOLLIPOP_MR1 = 22;
    public static final int M = 23;
    public static final int N = 24;
    public static final int N_MR1 = 25;
    public static final int O = 26;
    public static final int O_MR1 = 27;
    public static final int P = 28;
    public static final int Q = 29;
    public static final int RR = 30;
    public static final int S = 31;
    public static final int S_V2 = 32;
    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        @SuppressWarnings("static-access")
		public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    /**
     * Broadcast Receiver for changes made to bluetooth states such as:
     * 1) Discoverability mode on/off or expire.
     */
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        if (Build.VERSION.SDK_INT >=  M) {
                            list();
                        }

                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }

            }
        }
    };




    /**
     * Broadcast Receiver for listing devices that are not yet paired
     * -Executed by btnDiscover() method.
     */
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                if(isAPrinter(device)) {

                    if(!Map_unpaired.containsKey(device.getAddress()) && !Map_paired.containsKey(device.getAddress())){
                        mBTDevices.add(device);
                        Map_unpaired.put(device.getAddress(),device.getName());
                    }
                }
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress() )
                ;
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                lvNewDevices.setAdapter(mDeviceListAdapter);
            }
        }
    };
    private String showUUID(BluetoothDevice device) {
  StringBuilder builder = new StringBuilder();
  ParcelUuid[] uuids = device.getUuids();
  if (null == uuids) {
    return "";
  }
 // for (int i = 0; i < uuids.length; i++) {
    ParcelUuid uuid = uuids[0];
    builder.append(uuid.getUuid().toString());
  //}
  return builder.toString();
}
    private   boolean isAPrinter(BluetoothDevice device){
//         int printerMask = 0; //0b000001000000011010000000;
//        int fullCod = device.getBluetoothClass().hashCode();
//        String uuid=    showUUID(device);
//        Log.d(TAG, "uuid " + uuid);
//        Log.d(TAG, "FULL COD: " + fullCod);
//        Log.d(TAG, "MASK RESULT " + (fullCod & printerMask));
//        if(uuid.equals("")|| uuid.toLowerCase().equals("00001101-0000-1000-8000-00805f9b34fb"))
//        	return true;
//        else
//        	return false;
    	
    	if (device.getBluetoothClass().getDeviceClass() == 1664) {
    		  return true;	
    	} else {
    		  return false;	
    	}
//    	BluetoothClass cls = device.getBluetoothClass();
//    //	if(cls.getMajorDeviceClass() ==BluetoothClass.Device.Major.PERIPHERAL){
//    		 
//       
//          
//                switch (cls.getDeviceClass() & 0x05C0) {
//                case 0x680:
//                return true;	
//                case 0x0540: // Keyboard - 1314
//                       return false;
//                    case 0x05C0: // Keyboard + mouse combo.
//                        return false;
//                    case 0x0580: // Mouse - 1408
//                        return false;
//                    default: // Other.
//                        return false;
//                }
//    //	}
//return false;
        			
       // return (fullCod & printerMask) == printerMask;
    } 

    /**
     * Broadcast Receiver that detects bond state changes (Pairing status changes)
     */
    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                    print_test(mDevice);
                    if (Build.VERSION.SDK_INT >=  M) {
                        list();
                    }
                }
                //case2: creating a bone
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };
    
    public void showDialog(final Context context,final BluetoothDevice mmDevice,final int type)
	{if(pairedDevices.size()>1)
	{
		Baguette.showText (  (Activity) context , "only one paired device needed" , Baguette.BackgroundColor.RED );
    	
		return;
	}

		LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.prompts, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		final EditText userInput = (EditText) promptsView
				.findViewById(R.id.editTextDialogUserInput);

		// set dialog message
		alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("OK",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				// get user input and set it to result
				// edit text
				//result.setText(userInput.getText());
			    	String tt=userInput.getText().toString();
			    	String user=DatabaseUtils.getCurrentUserCode(context);
			    	String company=DatabaseUtils.getCurrentCompanyCode(context);
			    	if(UserPasswordsUtils.validateTimePasswordUsers12 
			    			(  MainActivity.this , UserPasswordsUtils.TransactionHeaderType.TRANSACTION 
			    					, TransactionHeadersUtils.Type.SALES_INVOICE , 
			    					userInput.getText().toString().trim()
			    					, DatabaseUtils.getCurrentUserCode ( MainActivity.this )   ))  {
			    		Calendar now = Calendar.getInstance ();
			     Printers p =new Printers(null, mmDevice.getAddress(), DatabaseUtils.getCurrentUserCode(context),DatabaseUtils.getCurrentCompanyCode(context), type,1, now.getTime());
			    List< Printers> ps= DatabaseUtils.getInstance (  context ).getDaoSession ().getPrintersDao ().queryBuilder().where(PrintersDao.Properties.UserCode.eq(user)
			    		,PrintersDao.Properties.PrinterMAC.eq(mmDevice.getAddress()) 		).list();
			    if (ps!=null && ps.size()>0){
			    	ps.get(0).setStatus(1);
			    	ps.get(0).setType(type);
			    	DatabaseUtils.getInstance (  context ).getDaoSession ().getPrintersDao ().update(ps.get(0));
			    }else{
			    	DatabaseUtils.getInstance (  context ).getDaoSession ().getPrintersDao ().insert(p);
			    }
		 		Baguette.showText (  (Activity) context , " saved" , Baguette.BackgroundColor.GREEN );
			    list();
			     dialog.cancel();
			    	}else{
			    		Baguette.showText (  (Activity) context , " wrong password" , Baguette.BackgroundColor.RED );
			    		showDialog((Activity) context,mmDevice,type);

			    	}
			    }
			  })
			.setNegativeButton("Cancel",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			    }
			  });

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

	}
 
    public   void otp(BluetoothDevice mmDevice,int type) {
    	showDialog(MainActivity.this,mmDevice,type);
         }
    public static void print_test(BluetoothDevice mmDevice) {
        try {
//            dialog_loading = new ProgressDialog(instance);
//
//            dialog_loading.setMessage("Please Wait....");
//            dialog_loading.setCancelable(false);

            dialog_loading.show();
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
            beginListenForData();

        } catch (Exception e) {
            dialog_loading.dismiss();
            new AlertDialog.Builder(instance)
                    .setMessage("socket might closed or timeout")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
        }
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
//        unregisterReceiver(mBroadcastReceiver1);
//        unregisterReceiver(mBroadcastReceiver2);
//        unregisterReceiver(mBroadcastReceiver3);
//        unregisterReceiver(mBroadcastReceiver4);
        //mBluetoothAdapter.cancelDiscovery();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnONOFF = (Button) findViewById(R.id.btnONOFF);
        btnEnableDisable_Discoverable = (Button) findViewById(R.id.btnDiscoverable_on_off);
        lvNewDevices = (ListView) findViewById(R.id.lvNewDevices);
        mBTDevices = new ArrayList<BluetoothDevice>();
        instance=this;

        //Broadcasts when bond state changes (ie:pairing)
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);

        dialog_loading = new ProgressDialog(MainActivity.this);
        dialog_loading.setMessage("Preparing for printing...");
        dialog_loading.setCancelable(false);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btnEnableDisable_Discoverable_();

        lvNewDevices.setOnItemClickListener(MainActivity.this);
        lv = (ListView)findViewById(R.id.listView);

        btnONOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: enabling/disabling bluetooth.");
                enableDisableBT();
            }
        });

    }



    public void enableDisableBT(){
        if(mBluetoothAdapter == null){
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
        }
        if(!mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: enabling BT.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
        if(mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: disabling BT.");
            mBluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }

    }


    public void btnEnableDisable_Discoverable(View view) {
        btnEnableDisable_Discoverable_();

    }
    public void btnEnableDisable_Discoverable_() {
        Log.d(TAG, "btnEnableDisable_Discoverable: Making device discoverable for 300 seconds.");
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        IntentFilter intentFilter = new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReceiver2,intentFilter);

    }

    public void btnDiscover() {
        //if (Build.VERSION.SDK_INT >=  M) {
            list();
        //}
    }

  
    public void btnDiscover_() {
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");

        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
        if(!mBluetoothAdapter.isDiscovering()){

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
    }

    /**
     * This method is required for all devices running API23+
     * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
     * in the manifest is not enough.
     *
     * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
     */
 
    private void checkBTPermissions() {
//        if(Build.VERSION.SDK_INT >  LOLLIPOP){
//            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
//            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
//            if (permissionCheck != 0) {
//
//                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
//            }
//        }else{
//            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
//        }
    }

    @SuppressWarnings("unchecked")
	@Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    	Set<BluetoothDevice>  pairedDevices1 = mBluetoothAdapter.getBondedDevices();
        ArrayList<BluetoothDevice> mBTDevices2 = new ArrayList<BluetoothDevice>();
         
       //ArrayList list = new ArrayList();
       
         for(BluetoothDevice bt : pairedDevices1) {
         if(isAPrinter(bt)){
       	mBTDevices2.add(bt);
       
         
         } 
         }if(mBTDevices2!=null &&mBTDevices2.size()==1){
        	 Baguette.showText(this, "Remove Paired Printer Before Pairing a New One", BackgroundColor.RED);
        	 return;
         }
    	//first cancel discovery because its very memory intensive.
        mBluetoothAdapter.cancelDiscovery();

        Log.d(TAG, "onItemClick: You Clicked on a device.");
        String deviceName = mBTDevices.get(i).getName();
        String deviceAddress = mBTDevices.get(i).getAddress();

        Log.d(TAG, "onItemClick: deviceName = " + deviceName);
        Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

        //create the bond.
        //NOTE: Requires API 17+? I think this is JellyBean
        if(Build.VERSION.SDK_INT >  JELLY_BEAN_MR2){
            Log.d(TAG, "Trying to pair with " + deviceName);
         //   mBTDevices.get(i).createBond();
            try {
				createBond(mBTDevices.get(i));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }    
//        pairedDevices = mBluetoothAdapter.getBondedDevices();
//        ArrayList<BluetoothDevice> mBTDevices2 = new ArrayList<BluetoothDevice>();
//
//       //ArrayList list = new ArrayList();
//       Map_paired.clear();
//       Map_unpaired.clear();
//       mDeviceListAdapter=null;
//       if(mBTDevices!=null){
//           mBTDevices.clear();
//       }
//       for(BluetoothDevice bt : pairedDevices) {
//           mBTDevices2.add(bt);
//           Map_paired.put(bt.getAddress(),bt.getName());
//       }
//		( (ArrayAdapter < BluetoothDevice >) lv.getAdapter( ) ).clear ();
//		// Add the new filtered list of orders
//		( (ArrayAdapter < BluetoothDevice >) lv.getAdapter( )  ).addAll ( mBTDevices2 );
//		// Notifies the attached observers that the underlying data has been changed
//		( (ArrayAdapter < BluetoothDevice >) lv.getAdapter( )  ).notifyDataSetChanged ();
        runOnUiThread(new Runnable(){
            public void run(){
   	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(instance);

	// set title
	alertDialogBuilder.setTitle("Pair");

	// set dialog message
	alertDialogBuilder.setMessage("Wait to Pair").setCancelable(false);

	// create alert dialog
	AlertDialog alertDialog = alertDialogBuilder.create();

	// show it
	alertDialog.show();	   
        try {
		Thread.sleep(10000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
//   	long start = new Date().getTime();
//   	while(new Date().getTime() - start < 12000L){}
        alertDialog.dismiss();    } 
        });
        list();
  
    }
 
    public void list(){
    	Set<BluetoothDevice>  pairedDevices1 = mBluetoothAdapter.getBondedDevices();
         ArrayList<BluetoothDevice> mBTDevices2 = new ArrayList<BluetoothDevice>();

        //ArrayList list = new ArrayList();
        Map_paired.clear();
        Map_unpaired.clear();
        mDeviceListAdapter=null;
        if(mBTDevices!=null){
            mBTDevices.clear();
        }
        pairedDevices =new HashSet<BluetoothDevice>();
        for(BluetoothDevice bt : pairedDevices1) {
          if(isAPrinter(bt)){
        	mBTDevices2.add(bt);
            pairedDevices.add(bt);
            Map_paired.put(bt.getAddress(),bt.getName());
          }
          }
        Toast.makeText(getApplicationContext(), "Showing Paired Devices",Toast.LENGTH_SHORT).show();
      //  final ArrayAdapter adapter = new  ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        DeviceListAdapter2 mDeviceListAdapter2 = new DeviceListAdapter2(this, R.layout.device_adapter_view2, mBTDevices2);
        lv.setAdapter(mDeviceListAdapter2);
        btnDiscover_();
        btnDiscover_();
    }

    // this will send text data to be printed by the bluetooth printer
    public static void sendData() throws IOException {
        String msg="test print data";
        try {
            // the text typed by the user
            msg += "\n";
            msg += "\n";
            msg += "\n";
            msg += "\n";

            mmOutputStream.write(msg.getBytes());
            mmSocket.close();
            mmOutputStream.close();
            dialog_loading.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            dialog_loading.dismiss();
        }
    }
    static byte[] readBuffer;
    static int readBufferPosition;
    static volatile boolean stopWorker;

    public static void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();

                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    if (b == delimiter) {

                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            dialog_loading.dismiss();
            e.printStackTrace();
        }
        try {
            sendData();
        } catch (IOException e) {
            dialog_loading.dismiss();
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
    	if ( item.getItemId () == R.id.item1 ) {
    		 btnDiscover();
         return true;
    	}
    	if ( item.getItemId () == R.id.item_scan ) {
    		  ScanCode();
        return true;
   	}
       
        return true;
         
    }
    private void ScanCode() {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        //if(list.size() > 0)
            startActivityForResult(intent, 0);
        //else
         //   showDownloadDialog();
    }
    private AlertDialog showDownloadDialog () {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >=  LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Install Barcode Scanner?")
                .setMessage("This application requires Barcode Scanner. Would you like to install it?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String packageName = "com.google.zxing.client.android";
                        Uri uri = Uri.parse ( "market://details?id=" + packageName );
                        Intent intent = new Intent ( Intent.ACTION_VIEW , uri );
                        try {
                            startActivity ( intent );
                        } catch ( Exception exe ) {
                            Log.w ( "TAG" , "Google Play is not installed; cannot install " + packageName );
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                //.setIcon(R.drawable.google_play)
                .show()
                .setCancelable(false);;
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                try {
                    String contents = data.getStringExtra("SCAN_RESULT");
                	String mac = "";
                    if(!contents.contains(":")){
                    
    				// Iterate over the characters
    				for ( int i = 0 ; i < contents.length () ; i = i + 2 ) 
    					// Properly format the MAC address
    					mac += contents.substring ( i , i + 2 ) + ":";
    				// Set the properly formatted address
                    }else
                    	mac=contents;
    			  // End if	//F460774C8031
                    BluetoothDevice scan_device = mBluetoothAdapter.getRemoteDevice(mac.substring ( 0 , mac.length () - 1 ));
                    // scan_device.createBond();
                     createBond(scan_device);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "not a valid Bluetooth address",Toast.LENGTH_SHORT).show();

                }

            } else {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= LOLLIPOP) {
                    builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_DeviceDefault);
                } else {
                    builder = new AlertDialog.Builder(MainActivity.this);
                }
                builder
                        .setTitle("Sorry, No Result Found")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show()
                        .setCancelable(false);
            }
        }
    }

    public boolean createBond(BluetoothDevice btDevice)  
            throws Exception  
            { 
                @SuppressWarnings("rawtypes")
				Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
                @SuppressWarnings("unchecked")
				Method createBondMethod = class1.getMethod("createBond");  
                Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);  
                return returnValue.booleanValue();  
        }
    
    public   void unpairDevice(final BluetoothDevice device) {
    	runOnUiThread(new Runnable(){
            public void run(){
               
     
    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(instance);

    	// set title
    	alertDialogBuilder.setTitle("Pair");

    	// set dialog message
    	alertDialogBuilder.setMessage("Wait to Unpair").setCancelable(false);

    	// create alert dialog
    	AlertDialog alertDialog = alertDialogBuilder.create();

    	// show it
    	alertDialog.show();

    
		   
    	try {
            Method m = device.getClass()
                .getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
         try {
		 Thread.sleep(4000);
		
        } catch (InterruptedException e) {
		//	 TODO Auto-generated catch block
			e.printStackTrace();
		}
    	// After some action
    	
     
//    	long start = new Date().getTime();
//       	while(new Date().getTime() - start < 5000L){}
       	alertDialog.dismiss();
           
        //Printers p =new Printers(null, device.getAddress(), DatabaseUtils.getCurrentUserCode(this),DatabaseUtils.getCurrentCompanyCode(this), type,1, now.getTime());
	    List< Printers> ps= DatabaseUtils.getInstance (  instance ).getDaoSession ().getPrintersDao ().queryBuilder().where(
	    		PrintersDao.Properties.UserCode.eq(DatabaseUtils.getCurrentUserCode(instance))
	    		,PrintersDao.Properties.PrinterMAC.eq(device.getAddress()) 		).list();
	    if (ps!=null && ps.size()>0){
	    	ps.get(0).setStatus(0);
	    	DatabaseUtils.getInstance (  instance ).getDaoSession ().getPrintersDao ().update(ps.get(0));
	    }else{
	    	//DatabaseUtils.getInstance (  this ).getDaoSession ().getPrintersDao ().insert(p);
	    } }
        });
      list();
        
    } 
    public   void unpairotp(BluetoothDevice mmDevice ) {
    	showDialog1(MainActivity.this,mmDevice );
         }
    public void showDialog1(final Context context,final BluetoothDevice mmDevice )
  	{ 

  		LayoutInflater li = LayoutInflater.from(context);
  		View promptsView = li.inflate(R.layout.prompts, null);

  		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
  				context);

  		// set prompts.xml to alertdialog builder
  		alertDialogBuilder.setView(promptsView);

 
		final EditText userInput = (EditText) promptsView
  				.findViewById(R.id.editTextDialogUserInput);

  		// set dialog message
  		alertDialogBuilder
  			.setCancelable(false)
  			.setPositiveButton("OK",
  			  new DialogInterface.OnClickListener() {
  			    public void onClick(DialogInterface dialog,int id) {
  				// get user input and set it to result
  				// edit text
  				//result.setText(userInput.getText());
  			    	String tt=userInput.getText().toString();
  			    	String user=DatabaseUtils.getCurrentUserCode(context);
  			    	String company=DatabaseUtils.getCurrentCompanyCode(context);
  			    	if(UserPasswordsUtils.validateTimePasswordUsers12 
  			    			(  MainActivity.this , UserPasswordsUtils.TransactionHeaderType.TRANSACTION 
  			    					, TransactionHeadersUtils.Type.SALES_INVOICE , 
  			    					userInput.getText().toString().trim()
  			    					, DatabaseUtils.getCurrentUserCode ( MainActivity.this )   ))  {
  			    		unpairDevice(mmDevice);
  			     dialog.cancel();
  			    	}else{
  			    		Baguette.showText (  (Activity) context , " wrong password" , Baguette.BackgroundColor.RED );
  			    		showDialog1((Activity) context,mmDevice );

  			    	}
  			    }
  			  })
  			.setNegativeButton("Cancel",
  			  new DialogInterface.OnClickListener() {
  			    public void onClick(DialogInterface dialog,int id) {
  				dialog.cancel();
  			    }
  			  });

  		// create alert dialog
  		AlertDialog alertDialog = alertDialogBuilder.create();

  		// show it
  		alertDialog.show();

  	}
}