package org.sandix.glucometer.models;

import android.hardware.usb.*;
import android.util.Log;

/**
 * Created by Alex on 03.04.2016.
 */
public class OneTouchUltraEasy extends UsbGlucometerDevice {

    UsbDevice device;
    UsbDeviceConnection connection;
    UsbInterface iface;
    UsbEndpoint inEndPoint;
    UsbEndpoint outEndPoint;


    private static byte[]recordsCountCmd = {0x02, 0x0A, 0x00, 0x05, 0x1F, (byte) 0xF5, 0x01, 0x03, 0x038, (byte) 0xAA};
    private static byte[]SNCmd = {0x02, 0x12,0x00, 0x05,0x0B,0x02,0x00,0x00,0x00,0x00,(byte)0x84,0x6A, (byte)0xE8,0x73,0x00,0x03,(byte)0x9B,(byte)0xEA};



    public OneTouchUltraEasy(UsbDevice device, UsbDeviceConnection connection) {
        super(device,connection);
        iface  = device.getInterface(0);
    }


    @Override
    public String getSN() {
        return null;
    }

    @Override
    public int getRecordsCount() {
        return 0;
    }

    @Override
    public boolean open() {
        if (connection.claimInterface(iface, true)) {
            Log.d("OneTouchUltraEasy", "Interface has been successfully claimed");
        } else {
            Log.d("OneTouchUltraEasy", "Interface could not be claimed");
            return false;
        }

        int numbersEndPoint = iface.getEndpointCount();
        for (int i = 0; i < numbersEndPoint - 1; i++) {
            UsbEndpoint tmpEndPoint = iface.getEndpoint(i);
            if (tmpEndPoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK && tmpEndPoint.getDirection() == UsbConstants.USB_DIR_IN) {
                inEndPoint = tmpEndPoint;
            } else if (tmpEndPoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK && tmpEndPoint.getDirection() == UsbConstants.USB_DIR_OUT) {
                outEndPoint = tmpEndPoint;
            }
        }
        return true;
    }

    @Override
    public boolean close() {
        return false;
    }

    @Override
    public String[] getRecord(int num) {
        return new String[0];
    }

    public static boolean isSupported(int vendorID, int productID){
        if(vendorID==1059 && productID==6089){
            return true;
        }
        return false;
    }
}


