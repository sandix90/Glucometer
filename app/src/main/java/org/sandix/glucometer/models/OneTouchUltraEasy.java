package org.sandix.glucometer.models;

import android.hardware.usb.*;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Created by Alex on 03.04.2016.
 */
public class OneTouchUltraEasy extends UsbGlucometerDevice {

//    UsbDevice device;
    //UsbDeviceConnection connection;
    UsbInterface iface;
    UsbEndpoint inEndPoint;
    UsbEndpoint outEndPoint;


    private static byte[]recordsCountCmd = {0x02,0x0A,0x00,0x05,0x1F,(byte)0xF5,0x01,0x03,0x038,(byte)0xAA};
    private static byte[]SNCmd = {0x02,0x12,0x00,0x05,0x0B,0x02,0x00,0x00,0x00,0x00,(byte)0x84,0x6A,(byte)0xE8,0x73,0x00,0x03,(byte)0x9B,(byte)0xEA};
    private byte[] readRecCmd = {0x02,0x0A,0x03,0x05,0x1F,0x00,0x00,0x03,0x4B,0x5F}; //6,7 elements is number of record in glucometer. Need in bytes format


    public OneTouchUltraEasy(UsbDevice device, UsbDeviceConnection connection) {
        super(device, connection);
        iface  = device.getInterface(0);
    }

    @Override
    public String getSN() {
        write(SNCmd);
        byte[] buf = new byte[inEndPoint.getMaxPacketSize()];
        if(read(buf)>=0){
            return  new String(Arrays.copyOfRange(buf, 11, 20), StandardCharsets.UTF_8); //see the data transmission protocol
        }
        return null;
    }

    @Override
    public int getRecordsCount() {
        write(recordsCountCmd);
        byte[] buf = new byte[inEndPoint.getMaxPacketSize()];
        if(read(buf)>=0){
            return buf[11]+buf[12]; //TODO: get an indexes in data transmission protocol
        }
        return -1;
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
        setUsbEndPoints(inEndPoint,outEndPoint); //set endpoints to super class to be able to write and read
        return true;
    }

    @Override
    public boolean close() {
        return connection.releaseInterface(iface);
    }

    @Override
    public String[] getRecord(int num) { //index in glucometer
        String[] str = new String[2];
        readRecCmd[5] = (byte)num;
        write(readRecCmd);
        byte [] buffer = new byte[inEndPoint.getMaxPacketSize()];
        if(read(buffer)>0){
            //from 14 - 11 - date in unix from. //TODO: need to make util to convert unix format date to date
            // from 18 - 15 - glucomter value in mg/mL. use convert method in static util class
            str[0] = new StringBuffer(Arrays.copyOfRange(buffer,11,14).toString()).reverse().toString();
            str[1] = new StringBuffer(Arrays.copyOfRange(buffer,15,18).toString()).reverse().toString();
        }




        return new String[0];
    }

    public static boolean isDeviceSupported(int vendorID, int productID){
        if(vendorID==0x067b && productID==0x2303){
            return true;
        }
        return false;
    }


}


