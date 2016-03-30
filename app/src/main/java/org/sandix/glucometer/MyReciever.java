package org.sandix.glucometer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

/**
 * Created by sandakov.a on 30.03.2016.
 */
public class MyReciever extends BroadcastReceiver {

    UsbManager mUsbmanager;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if(UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)){

        }
        else if(UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)){

        }
    }
}
