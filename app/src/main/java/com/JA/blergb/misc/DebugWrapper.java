package com.JA.blergb.misc;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class DebugWrapper {
    final static public boolean DISPLAY_DEBUG_MSGS = false;
    final static public boolean DISPLAY_INFO_MSGS = false;
    final static public boolean DISPLAY_ERROR_MSGS = false;
//    public static final String TAG = "BL600 OTA";
    
    
    public static void debugMsg(String msg, String tag){
        if(DISPLAY_DEBUG_MSGS == true){
            Log.d(tag, msg);
        }
    }
    
    public static void infoMsg(String msg, String tag){
        if(DISPLAY_INFO_MSGS == true){
            Log.i(tag, msg);
        }
    }
    
    public static void errorMsg(String msg, String tag){
        if(DISPLAY_ERROR_MSGS == true){
            Log.e(tag, msg);
        }
    }
    
    /**
     * displays the given message on the screen
     * @param activity
     * @param msg the text to display
     */
    public static void toastMsg(final Activity activity, final String msg){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(activity, msg, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 450);
                toast.show();
            }
        });
    }
}