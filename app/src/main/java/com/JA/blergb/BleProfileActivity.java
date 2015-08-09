package com.JA.blergb;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.JA.blergb.gap.LairdGapBase;
import com.JA.blergb.misc.DebugWrapper;

public abstract class BleProfileActivity extends Activity implements OnClickListener{
    public static final int ENABLE_BT_REQUEST_ID = 1;
    protected LairdGapBase mLairdGapBase;
    private DeviceListAdapter mDevicesListAdapter = null;
    protected Dialog dialog;
    private Button mBtnConnect;
    protected Activity mActivity;
    
    /**
     * sets the view
     */
    protected abstract int setContentView();
    /**
     * sets the activity
     */
    protected abstract Activity setActivity();
    /**
     * set the BLE class base
     * @return
     */
    protected abstract LairdGapBase setLairdGapBase();
    /**
     * callback for when the scanning has been cancelled
     */
    protected abstract void onScanningCancel();
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setContentView());
        mActivity = setActivity();
        bindViews();
        setListeners();
        mLairdGapBase = setLairdGapBase();
        
        // check if we have BT and BLE on board
        if (mLairdGapBase.checkBleHardwareAvailable() == false) {
            DebugWrapper.toastMsg(this, "BLE Hardware is required but not available!");
            finish();
        }
        
        if(mLairdGapBase.initialize() == false){
            DebugWrapper.toastMsg(this, "Could not initialize Bluetooth");
            finish();
        }
    }
    
    private void bindViews(){
        mBtnConnect = (Button) findViewById(R.id.btnConnect);
    };
    
    private void setListeners(){
        mBtnConnect.setOnClickListener(this);
    }
    
    private void setDefaultViewValues(){
        mBtnConnect.setText("Connect");
    }

    @Override
    public void onClick(View view){
        int btnClickedId = view.getId();
        
        switch(btnClickedId){
        case R.id.btnConnect:
            if(mLairdGapBase.isBtEnabled() == true){
                if(mLairdGapBase.isScanning() == false && mLairdGapBase.isConnected() == false){
                    mLairdGapBase.startScanning();
                    devicesListDialog();
                } else if(mLairdGapBase.isScanning() == true && mLairdGapBase.isConnected() == false){
                    mLairdGapBase.stopScanning();
                } else if(mLairdGapBase.isConnected() == true){
                    mLairdGapBase.disconnect();
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            setDefaultViewValues();
                        }
                    });
                    
                }
            } else if(mLairdGapBase.isBtEnabled() == false){
                DebugWrapper.toastMsg(this, "Bluetooth must be enabled");
            }
            break;
        }
        uiInvalidateBtnState();
    }
    
    @Override
    protected void onResume(){
        super.onResume();
        /*
         *  on every Resume check if BT is enabled
         *  user could turn it off while app was in background etc
         */
        if (mLairdGapBase.isBtEnabled() == false) {
            // BT is not turned on - ask user to make it enabled
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST_ID);
            // see onActivityResult to check what is the status of our request
        }
        mDevicesListAdapter = new DeviceListAdapter(this);
    }
    
    protected void uiInvalidateBtnState(){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mLairdGapBase.isScanning() == false && mLairdGapBase.isConnected() == false){
                    // show connect
                    mBtnConnect.setText(R.string.connect);
                } else if(mLairdGapBase.isScanning() == true && mLairdGapBase.isConnected() == false){
                    // show scanning
                    mBtnConnect.setText(R.string.searching);
                } else if(mLairdGapBase.isConnected() == true){
                    // show disconnect
                    mBtnConnect.setText(R.string.disconnect);
                }
                invalidateOptionsMenu();
            }
        });
    }
    
    // add device to the current list of devices
    protected void handleFoundDevice(final BluetoothDevice device,
            final int rssi,
            final byte[] scanRecord)
    {
        // adding to the UI have to happen in UI thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDevicesListAdapter.addDevice(device, rssi, scanRecord);
                mDevicesListAdapter.notifyDataSetChanged();
                int i = mDevicesListAdapter.getCount();
                for(int x = 1 ; x <= i ; x ++)
                {
                    BluetoothDevice device = mDevicesListAdapter.getDevice(x-1);
                    String name = device.getName();
                    if (name.equals("JA_RGB")) {
                        if (mLairdGapBase.isConnected() == false)
                        {
                            DebugWrapper.errorMsg("BleProfileActivity / CONNECT Auto", " <<>> ");
                            mLairdGapBase.connect(device.getAddress());
                            DebugWrapper.toastMsg(mActivity, "Connect");
                            tempo();
                        }
                    }
                }

            }
        });
    }
    
    protected void devicesListDialog(){
        dialog = new Dialog(this);
        // get View
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.devices_listview, null, false);
        final ListView listView = (ListView) v;
        
        listView.setAdapter(mDevicesListAdapter);
        
        dialog.setContentView(v);
        dialog.setTitle("Select A BLE Device");
        dialog.setCanceledOnTouchOutside(false);
        
        listView.setOnItemClickListener(new OnItemClickListener(){
            
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position,
                    long id) {
                final BluetoothDevice device = mDevicesListAdapter.getDevice(position);
                if(device == null) return;
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        mLairdGapBase.connect(device.getAddress());
                    }
                });
                dialog.dismiss();
            }
        });
        
        // when back button is pressed clear the devices we found and stop scanning
         dialog.setOnCancelListener(new OnCancelListener(){
              @Override
              public void onCancel(DialogInterface arg0) {
                mDevicesListAdapter.clearList();
                mLairdGapBase.stopScanning();
                onScanningCancel();
              }
          });
          
        dialog.setOnDismissListener(new OnDismissListener(){
            @Override
            public void onDismiss(DialogInterface dialog) {
                mDevicesListAdapter.clearList();
            }
        });
        /////dialog.show();
    }
    
    /* check if user agreed to enable BT */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ENABLE_BT_REQUEST_ID) {
            if (resultCode == Activity.RESULT_CANCELED) {
                // user didn't enabled BT
                DebugWrapper.toastMsg(this, "Sorry, BT has to be turned ON for this App!");
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void tempo() {
        for(int i = 0;i<2;i++){
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    //do your Ui task here
                }
            });

            try {

                Thread.sleep(500);

            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


}