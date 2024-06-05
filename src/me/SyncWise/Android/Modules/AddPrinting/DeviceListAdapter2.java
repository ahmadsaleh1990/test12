package me.SyncWise.Android.Modules.AddPrinting;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.SyncWise.Android.R;
import me.SyncWise.Android.Database.DatabaseUtils;
import me.SyncWise.Android.Database.Printers;
import me.SyncWise.Android.Database.PrintersDao;
import me.SyncWise.Android.Modules.SalesInvoice.SalesInvoiceDetailsActivity;

public class DeviceListAdapter2 extends ArrayAdapter<BluetoothDevice> {

    private LayoutInflater mLayoutInflater;
    private ArrayList<BluetoothDevice> mDevices;
    private int  mViewResourceId;

    public DeviceListAdapter2(Context context, int tvResourceId, ArrayList<BluetoothDevice> devices){
        super(context, tvResourceId,devices);
        this.mDevices = devices;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = tvResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(mViewResourceId, null);

        final BluetoothDevice device = mDevices.get(position);
        final CheckBox checkbox_zebra = (CheckBox) convertView.findViewById(R.id.checkBox1);
        final CheckBox checkbox_interMac = (CheckBox) convertView.findViewById(R.id.checkBox2);
        final CheckBox checkbox_Mip = (CheckBox) convertView.findViewById(R.id.checkBox3);
       if (device != null) {
            TextView deviceName = (TextView) convertView.findViewById(R.id.tvDeviceName);
            TextView deviceAdress = (TextView) convertView.findViewById(R.id.tvDeviceAddress);
            Button btnact=(Button) convertView.findViewById(R.id.btnact);
            Button button1=(Button) convertView.findViewById(R.id.btnaccept);
            final List< Printers> ps= DatabaseUtils.getInstance (  (MainActivity) getContext () ).getDaoSession ().getPrintersDao ().queryBuilder().
            		where(PrintersDao.Properties.UserCode.eq(DatabaseUtils.getCurrentUserCode( (MainActivity) getContext ()))
    	    		,PrintersDao.Properties.PrinterMAC.eq(device.getAddress()) 		).list();
            String ass="";
            if(ps!=null && ps.size()>0){
             	ass="default Printer";
         
            	if(ps.get(0).getStatus()==0){
            	btnact.setVisibility(View.VISIBLE);
            	button1.setVisibility(View.VISIBLE);
               	convertView.setBackgroundColor ( 0 );
            	ass="";
            	}else{   	convertView.setBackgroundColor ( Color.rgb(255, 228, 225) );
            		btnact.setVisibility(View.VISIBLE);
                	button1.setVisibility(View.INVISIBLE);
            	}
           
            	if(ps.get(0).getType()==1)
            	{
             	    checkbox_zebra.setChecked(false);
                    checkbox_interMac.setChecked(true);
                    checkbox_Mip.setChecked(false);
            	}else  	if(ps.get(0).getType()==2){
            	    checkbox_zebra.setChecked(false);
                    checkbox_interMac.setChecked(true);
                    checkbox_Mip.setChecked(false);
            	}else 	if(ps.get(0).getType()==3){
            	    checkbox_zebra.setChecked(false);
                    checkbox_interMac.setChecked(false);
                    checkbox_Mip.setChecked(true);
            	}else
            	{
            	    checkbox_zebra.setChecked(true);
                    checkbox_interMac.setChecked(false);
                    checkbox_Mip.setChecked(false);
            	}
            	 
            }else{
            	btnact.setVisibility(View.VISIBLE);
            	button1.setVisibility(View.VISIBLE);
             convertView.setBackgroundColor ( 0 );
            checkbox_zebra.setChecked(true);
            checkbox_interMac.setChecked(false);
            checkbox_Mip.setChecked(false);
            }
            checkbox_zebra
            .setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                        boolean isChecked) {
                 if(isChecked){
                     checkbox_zebra.setChecked(true);
                     checkbox_interMac.setChecked(false);
                     checkbox_Mip.setChecked(false);
                 }
            
                }
            });
            checkbox_interMac
            .setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                        boolean isChecked) {
                 if(isChecked){
                     checkbox_zebra.setChecked(false);
                     checkbox_interMac.setChecked(true);
                     checkbox_Mip.setChecked(false);
                 }
            
                }
            });
            checkbox_Mip
            .setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                        boolean isChecked) {
                 if(isChecked){
                     checkbox_zebra.setChecked(false);
                     checkbox_interMac.setChecked(false);
                     checkbox_Mip.setChecked(true);
                 }
            
                }
            });
         //   Button btnact=(Button) convertView.findViewById(R.id.btnact);
            btnact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                	   if(ps!=null && ps.size()>0){
                        	 
                    
                       	if(ps.get(0).getStatus()==1){
                       		( (MainActivity) getContext () ).unpairotp(device);
                       	}else
                       		( (MainActivity) getContext () ) .unpairDevice(device);
                       	}else
                   // MainActivity.sendMac(device);
                	( (MainActivity) getContext () ) .unpairDevice(device);
                 //   ( (MainActivity) getContext () )   .list();
                }
            });
        
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                	int i=1;
                	if(checkbox_Mip.isChecked())
                		i=3;
                	if(checkbox_interMac.isChecked())
                		i=2;
                	
                	( (MainActivity) getContext () ) .otp(device,i);
                }
            });
            Button button=(Button) convertView.findViewById(R.id.btnPrint);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.print_test(device);
                }
            });

            if (deviceName != null) {
            	if(!ass.equals(""))
                deviceName.setText(device.getName()+"("+ass+")");
            	else
                    deviceName.setText(device.getName() );
            }
            if (deviceAdress != null) {
                deviceAdress.setText(device.getAddress());
            }
        }

        return convertView;
    }

}
