package com.JA.blergb;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.JA.blergb.gap.LairdGapBase;
import com.JA.blergb.misc.DebugWrapper;

import java.io.ByteArrayOutputStream;
import java.lang.Math;

public class MyActivity extends BleProfileActivity implements SerialUiManagerCallback {
    private static final int MAX_DATA_TO_READ_FROM_RX_BUFFER = 19;
    private SerialManager mSerialManager;
    private Switch mSwitchRed;
    private Switch mSwitchGreen;
    private Switch mSwitchBlue;
    private ImageView iLogo;
    Bitmap originalImage;
    int tempo;
    int width = 0;
    int height = 0;
    float scaleWidth ;
    float scaleHeight;
    Matrix matrix;
    Bitmap resizedBitmap;
    ByteArrayOutputStream outputStream;
    LinearLayout bkLayout;
    ////$$////private Button mBtnSend;
    ////$$////private EditText mValueVspInputEt;
    ////$$////private TextView mValueVspOutTv;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindViews();
        setListeners();
    }

    public void bindViews(){
        mSwitchRed = (Switch) findViewById(R.id.switchRED);
        mSwitchGreen = (Switch) findViewById(R.id.switchGREEN);
        mSwitchBlue = (Switch) findViewById(R.id.switchBLUE);
        mSwitchRed.setVisibility(View.INVISIBLE);
        mSwitchGreen.setVisibility(View.INVISIBLE);
        mSwitchBlue.setVisibility(View.INVISIBLE);
        mSwitchRed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SendTextColor(); } });
        mSwitchGreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SendTextColor(); } });
        mSwitchBlue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SendTextColor(); } });
        ////$$////mBtnSend = (Button) findViewById(R.id.btnSend);
        ////$$////mValueVspInputEt = (EditText) findViewById(R.id.valueVspInputEt);
        ////$$////mValueVspOutTv = (TextView) findViewById(R.id.valueVspOutTv);
        iLogo =(ImageView)findViewById(R.id.imageLogo);
        iLogo.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.logo));
    }

    public void SendTextColor() {
        String strSend;
        if (mSwitchRed.isChecked()) { strSend = "R"; }
        else { strSend = "-"; }
        if (mSwitchGreen.isChecked()) { strSend += "G"; }
        else { strSend += "-"; }
        if (mSwitchBlue.isChecked()) { strSend += "B"; }
        else { strSend += "-"; }
        mSerialManager.sendData(strSend);
        //int tempo;
        //tempo = iLogo.getHeight();
        if (strSend.toString().equalsIgnoreCase("R--")) {
            originalImage = BitmapFactory.decodeResource(getResources(), R.drawable.red); }
        if (strSend.toString().equalsIgnoreCase("RG-")) { originalImage = BitmapFactory.decodeResource(getResources(), R.drawable.yellow); }
        if (strSend.toString().equalsIgnoreCase("RGB")) { originalImage = BitmapFactory.decodeResource(getResources(), R.drawable.white); }
        if (strSend.toString().equalsIgnoreCase("---")) { originalImage = BitmapFactory.decodeResource(getResources(), R.drawable.black); }
        if (strSend.toString().equalsIgnoreCase("-G-")) { originalImage = BitmapFactory.decodeResource(getResources(), R.drawable.grenn); }
        if (strSend.toString().equalsIgnoreCase("-GB")) { originalImage = BitmapFactory.decodeResource(getResources(), R.drawable.cyan); }
        if (strSend.toString().equalsIgnoreCase("--B")) { originalImage = BitmapFactory.decodeResource(getResources(), R.drawable.blue); }
        if (strSend.toString().equalsIgnoreCase("R-B")) { originalImage = BitmapFactory.decodeResource(getResources(), R.drawable.magenta); }
        //width = originalImage.getWidth();
        //height = originalImage.getHeight();
        if ((width > 0) && (height > 0))
        {
            matrix = new Matrix();
            scaleWidth = ((float) tempo) / 200;
            scaleHeight = ((float) tempo) / 250;
            matrix.postScale(scaleWidth, scaleHeight);
            resizedBitmap = Bitmap.createBitmap(originalImage, 0, 0, width, height, matrix, true);
            outputStream = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            iLogo.setImageBitmap(resizedBitmap);
            iLogo.invalidate();
        }
    }

    private void setListeners(){
        ////$$////mBtnSend.setOnClickListener(this);
    }

    public void onClick(View view){
        int btnClickedId = view.getId();

        switch(btnClickedId){

            ////$$////case R.id.btnSend:
            /*
             * *********************
             * to send data to module
             * *********************
             */
            ////$$////  String dataToSend = mValueVspInputEt.getText().toString();
            ////$$////    if(dataToSend != null){
            ////$$////       mValueVspOutTv.append(">" + dataToSend);
            ////$$////       mValueVspOutTv.append("\n");
            ////$$////        mSerialManager.sendData(dataToSend);
            ////$$////    }

            ////$$////   break;
        }
        super.onClick(view);
    }

    private void disableBtn(final Button btn){
        if(btn != null){
            runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    btn.setEnabled(false);
                }
            });
        }
    }

    private void enableBtn(final Button btn){
        if(btn != null){
            runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    btn.setEnabled(true);
                }
            });
        }
    }

    @Override
    public void uiOnResponse(final String dataReceived) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ////$$////mValueVspOutTv.append(dataReceived);
                if(dataReceived.contains("\n00\r") || dataReceived.contains("\n01\t")){
                    ////$$////mValueVspOutTv.append("\n**************\n");
                }
            }
        });
    }

    @Override
    public void uiServiceVSPFound(BluetoothGatt gatt) {
        DebugWrapper.toastMsg(this, "VSP Service found");
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                ////$$////enableBtn(mBtnSend);
                mSwitchRed.setVisibility(View.VISIBLE);
                mSwitchGreen.setVisibility(View.VISIBLE);
                mSwitchBlue.setVisibility(View.VISIBLE);
            }
        });
        uiInvalidateBtnState();
    }

    @Override
    public void uiServiceVSPNotFound(BluetoothGatt gatt) {
        DebugWrapper.toastMsg(this, "VSP Service not found!!");
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                ////$$////disableBtn(mBtnSend);
            }
        });
        uiInvalidateBtnState();

    }

    @Override
    public void uiModuleVSPCharsFound(BluetoothGatt gatt) {
        // TODO Auto-generated method stub

    }

    @Override
    public void uiModuleVSPCharsNotFound(BluetoothGatt gatt) {
        // TODO Auto-generated method stub

    }

    @Override
    public void uiStartScanning() {
        uiInvalidateBtnState();
    }

    @Override
    public void uiStopScanning() {
        uiInvalidateBtnState();
    }

    @Override
    public void uiOnLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        handleFoundDevice(device, rssi, scanRecord);
    }

    @Override
    public void uiOnConnectionStateChangeConnected(BluetoothGatt gatt) {
        uiInvalidateBtnState();
    }

    @Override
    public void uiOnConnectionStateChangeDisconnected(BluetoothGatt gatt) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ////$$////disableBtn(mBtnSend);
            }
        });
        uiInvalidateBtnState();
    }

    @Override
    public void uiOnConnectionStateChangeConnecting(BluetoothGatt gatt) {
        // TODO Auto-generated method stub

    }

    @Override
    public void uiOnConnectionStateChangeDisconnecting(BluetoothGatt gatt) {
        // TODO Auto-generated method stub

    }

    @Override
    public void uiOnConnectionStateChangeFailure(BluetoothGatt gatt,
                                                 int status, int newState) {
        // TODO Auto-generated method stub

    }

    @Override
    public void uiOnReadRemoteRssiSuccess(BluetoothGatt gatt, int rssi) {
        // TODO Auto-generated method stub

    }

    @Override
    public void uiOnReadRemoteRssiFailure(BluetoothGatt gatt, int rssi,
                                          int status) {
        // TODO Auto-generated method stub

    }

    @Override
    public void uiOnServicesDiscoveredSuccess(BluetoothGatt gatt) {
        uiInvalidateBtnState();
    }

    @Override
    public void uiOnServicesDiscoveredFailure(BluetoothGatt gatt, int status) {
        // TODO Auto-generated method stub

    }

    @Override
    public void uiOnDescriptorReadSuccess(BluetoothGatt gatt,
                                          BluetoothGattDescriptor ch) {
        // TODO Auto-generated method stub

    }

    @Override
    public void uiOnDescriptorReadFailure(BluetoothGatt gatt,
                                          BluetoothGattDescriptor ch, int status) {
        // TODO Auto-generated method stub

    }

    @Override
    public void uiOnDescriptorWriteSuccess(BluetoothGatt gatt,
                                           BluetoothGattDescriptor descriptor) {
        // TODO Auto-generated method stub

    }

    @Override
    public void uiOnDescriptorWriteFailure(BluetoothGatt gatt,
                                           BluetoothGattDescriptor descriptor, int status) {
        // TODO Auto-generated method stub

    }

    @Override
    public void uiOnCharacteristicWriteSuccess(BluetoothGatt gatt,
                                               BluetoothGattCharacteristic ch) {
        // TODO Auto-generated method stub

    }

    @Override
    public void uiOnCharacteristicWriteFailure(BluetoothGatt gatt,
                                               BluetoothGattCharacteristic ch, int status) {
        // TODO Auto-generated method stub

    }

    @Override
    public void uiOnCharacteristicReadSuccess(BluetoothGatt gatt,
                                              BluetoothGattCharacteristic ch) {
        // TODO Auto-generated method stub

    }

    @Override
    public void uiOnCharacteristicReadFailure(BluetoothGatt gatt,
                                              BluetoothGattCharacteristic ch, int status) {
        // TODO Auto-generated method stub

    }

    @Override
    public void uiOnReliableWriteCompletedSuccess(BluetoothGatt gatt) {
        // TODO Auto-generated method stub

    }

    @Override
    public void uiOnReliableWriteCompletedFailure(BluetoothGatt gatt, int status) {
        // TODO Auto-generated method stub

    }

    @Override
    protected int setContentView() {
        // TODO Auto-generated method stub
        return R.layout.activity_serial;
    }

    @Override
    protected Activity setActivity() {
        // TODO Auto-generated method stub
        return this;
    }

    @Override
    protected LairdGapBase setLairdGapBase() {
        // TODO Auto-generated method stub
        return mSerialManager = new SerialManager(this, this, this, MAX_DATA_TO_READ_FROM_RX_BUFFER);
    }

    @Override
    protected void onScanningCancel() {
        // TODO Auto-generated method stub

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.serial_menu, menu);
        if(mSerialManager.isConnected() == false) {
            mSwitchRed.setVisibility(View.INVISIBLE);
            mSwitchGreen.setVisibility(View.INVISIBLE);
            mSwitchBlue.setVisibility(View.INVISIBLE);
            mSwitchRed.setChecked(false);
            mSwitchGreen.setChecked(false);
            mSwitchBlue.setChecked(false);
            iLogo = (ImageView) findViewById(R.id.imageLogo);
            originalImage = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        }
        if (mSerialManager.isScanning() == true) {
            menu.findItem(R.id.scanning_indicator)
                    .setActionView(R.layout.progress_indicator);
        } else if(mSerialManager.isConnected() == true && mSerialManager.getIsServiceDiscoveryFinished() == false){
            menu.findItem(R.id.scanning_indicator)
                    .setActionView(R.layout.progress_indicator);
        } else {
            menu.findItem(R.id.scanning_indicator).setActionView(null);
        }

        if ((width == 0) && (height == 0)) {
            iLogo = (ImageView) findViewById(R.id.imageLogo);
            tempo = iLogo.getHeight();
            originalImage = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
            width = originalImage.getWidth();
            height = originalImage.getHeight();
        }
        if ((width > 0) && (height > 0)) {
            matrix = new Matrix();
            scaleWidth = ((float) tempo) / 300;
            scaleHeight = ((float) tempo) / 350;
            matrix.postScale(scaleWidth, scaleHeight);
            resizedBitmap = Bitmap.createBitmap(originalImage, 0, 0, width, height, matrix, true);
            outputStream = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            iLogo.setImageBitmap(resizedBitmap);
        }
        return true;
    }

    @Override
    public void onBackPressed(){
        if(mSerialManager.isConnected() == true){
            mSerialManager.disconnect();
        }
        ////$$////disableBtn(mBtnSend);
        uiInvalidateBtnState();
        finish();
    }
}