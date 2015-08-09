package com.JA.blergb;

import com.JA.blergb.vspservice.VSPUiCallback;



public interface SerialUiManagerCallback extends VSPUiCallback{
    public void uiOnResponse(String dataReceived);
}