package org.sandix.glucometer.models;

import android.hardware.usb.*;
import android.util.Log;

import org.sandix.glucometer.beans.GlBean;
import org.sandix.glucometer.tools;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Created by Alex on 03.04.2016.
 */
public class OneTouchUltraEasy extends UsbGlucometerDevice {


    private static final int PL2303_REQTYPE_HOST2DEVICE_VENDOR = 0x40;
    private static final int PL2303_REQTYPE_DEVICE2HOST_VENDOR = 0xC0;
    private static final int PL2303_REQTYPE_HOST2DEVICE = 0x21;

    private static final int PL2303_VENDOR_WRITE_REQUEST = 0x01;
    private static final int PL2303_SET_LINE_CODING = 0x20;
    private static final int PL2303_SET_CONTROL_REQUEST = 0x22;

    private byte[] defaultSetLine = new byte[]{
            (byte) 0x80, // [0:3] Baud rate (reverse hex encoding 9600:00 00 25 80 -> 80 25 00 00)
            (byte) 0x25,
            (byte) 0x00,
            (byte) 0x00,
            (byte) 0x00, // [4] Stop Bits (0=1, 1=1.5, 2=2)
            (byte) 0x00, // [5] Parity (0=NONE 1=ODD 2=EVEN 3=MARK 4=SPACE)
            (byte) 0x08  // [6] Data Bits (5=5, 6=6, 7=7, 8=8)
    };

    //    UsbDevice device;
    //UsbDeviceConnection connection;
    private static String LOG_TAG = "OneTouchUltraEasy";
    UsbInterface iface;
    UsbEndpoint inEndPoint;
    UsbEndpoint outEndPoint;

    private String serialNumber="";


    private static byte[] recordsCountCmd = {0x02, 0x0A, 0x00, 0x05, 0x1F, (byte) 0xF5, 0x01, 0x03, 0x038, (byte) 0xAA};
    private static byte[] SNCmd = {0x02, 0x12, 0x00, 0x05, 0x0B, 0x02, 0x00, 0x00, 0x00, 0x00, (byte) 0x84, 0x6A, (byte) 0xE8, 0x73, 0x00, 0x03, (byte) 0x9B, (byte) 0xEA};
    private byte[] readRecCmd = {0x02, 0x0A, 0x03, 0x05, 0x1F, 0x00, 0x00, 0x03, 0x4B, 0x5F}; //6,7 elements is number of record in glucometer. Need in bytes format


    public OneTouchUltraEasy(UsbDevice device, UsbDeviceConnection connection) {
        super(device, connection);
        iface = device.getInterface(0);
    }

    @Override
    public String getSN() {
        if(serialNumber.equals("")) {
            write(SNCmd);
            byte[] buf = new byte[64];
            if (read(buf) >= 0) {
                serialNumber = new String(Arrays.copyOfRange(buf, 11, 20), StandardCharsets.UTF_8);
                return serialNumber; //see the data transmission protocol
            }
            return null;
        }
        else{
            return serialNumber;
        }

    }

    @Override
    public int getRecordsCount() {
        write(recordsCountCmd);
        byte[] buf = new byte[inEndPoint.getMaxPacketSize()];
        if (read(buf) >= 0) {
            return buf[11] + buf[12]; // get an indexes. in data transmission protocol
        }
        return -1;
    }

    @Override
    public boolean open() {
        if (connection.claimInterface(iface, true)) {
            Log.d(LOG_TAG, "Interface has been successfully claimed");
        } else {
            Log.d(LOG_TAG, "Interface could not be claimed");
            return false;
        }

        int numbersEndPoint = iface.getEndpointCount();
        Log.d(LOG_TAG, "numbersEndPoint: " + numbersEndPoint);
        for (int i = 0; i < numbersEndPoint; i++) {
            UsbEndpoint tmpEndPoint = iface.getEndpoint(i);
            Log.d(LOG_TAG, "tmpEndpoint Type: " + tmpEndPoint.getType() + " Direction: " + tmpEndPoint.getDirection());
            if (tmpEndPoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK && tmpEndPoint.getDirection() == UsbConstants.USB_DIR_IN) {
                inEndPoint = tmpEndPoint;
            } else if (tmpEndPoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK && tmpEndPoint.getDirection() == UsbConstants.USB_DIR_OUT) {
                outEndPoint = tmpEndPoint;
            }
        }

//        for (int i = 0; i < numbersEndPoint; i++) {
//            UsbEndpoint tmpEndPoint = iface.getEndpoint(i);
//            Log.d(LOG_TAG, "tmpEndpoint Type: "+tmpEndPoint.getType() +" Direction: "+tmpEndPoint.getDirection());
//            if ( tmpEndPoint.getDirection() == UsbConstants.USB_DIR_IN ) {
//                inEndPoint = tmpEndPoint;
//            } else if ( tmpEndPoint.getDirection() == UsbConstants.USB_DIR_OUT) {
//                outEndPoint = tmpEndPoint;
//            }
//        }
        Log.d(LOG_TAG, "inEndpoint Type/Direction: " + inEndPoint.getType() + "/" + inEndPoint.getDirection() + " outEndpoint Type/Direction: " + outEndPoint.getType() + "/" + outEndPoint.getDirection());
        setUsbEndPoints(inEndPoint, outEndPoint); //set endpoints to super class to be able to write and read
        //Default Setup
        byte[] buf = new byte[1];
        //Specific vendor stuff that I barely understand but It is on linux drivers, So I trust :)
        if (setControlCommand(PL2303_REQTYPE_DEVICE2HOST_VENDOR, PL2303_VENDOR_WRITE_REQUEST, 0x8484, 0, buf) < 0)
            return false;
        if (setControlCommand(PL2303_REQTYPE_HOST2DEVICE_VENDOR, PL2303_VENDOR_WRITE_REQUEST, 0x0404, 0, null) < 0)
            return false;
        if (setControlCommand(PL2303_REQTYPE_DEVICE2HOST_VENDOR, PL2303_VENDOR_WRITE_REQUEST, 0x8484, 0, buf) < 0)
            return false;
        if (setControlCommand(PL2303_REQTYPE_DEVICE2HOST_VENDOR, PL2303_VENDOR_WRITE_REQUEST, 0x8383, 0, buf) < 0)
            return false;
        if (setControlCommand(PL2303_REQTYPE_DEVICE2HOST_VENDOR, PL2303_VENDOR_WRITE_REQUEST, 0x8484, 0, buf) < 0)
            return false;
        if (setControlCommand(PL2303_REQTYPE_HOST2DEVICE_VENDOR, PL2303_VENDOR_WRITE_REQUEST, 0x0404, 1, null) < 0)
            return false;
        if (setControlCommand(PL2303_REQTYPE_DEVICE2HOST_VENDOR, PL2303_VENDOR_WRITE_REQUEST, 0x8484, 0, buf) < 0)
            return false;
        if (setControlCommand(PL2303_REQTYPE_DEVICE2HOST_VENDOR, PL2303_VENDOR_WRITE_REQUEST, 0x8383, 0, buf) < 0)
            return false;
        if (setControlCommand(PL2303_REQTYPE_HOST2DEVICE_VENDOR, PL2303_VENDOR_WRITE_REQUEST, 0x0000, 1, null) < 0)
            return false;
        if (setControlCommand(PL2303_REQTYPE_HOST2DEVICE_VENDOR, PL2303_VENDOR_WRITE_REQUEST, 0x0001, 0, null) < 0)
            return false;
        if (setControlCommand(PL2303_REQTYPE_HOST2DEVICE_VENDOR, PL2303_VENDOR_WRITE_REQUEST, 0x0002, 0x0044, null) < 0)
            return false;
        // End of specific vendor stuff
        if (setControlCommand(PL2303_REQTYPE_HOST2DEVICE, PL2303_SET_CONTROL_REQUEST, 0x0003, 0, null) < 0)
            return false;
        if (setControlCommand(PL2303_REQTYPE_HOST2DEVICE, PL2303_SET_LINE_CODING, 0x0000, 0, defaultSetLine) < 0)
            return false;
        if (setControlCommand(PL2303_REQTYPE_HOST2DEVICE_VENDOR, PL2303_VENDOR_WRITE_REQUEST, 0x0505, 0x1311, null) < 0)
            return false;




        return true;
    }

    public void setSerialNumber(String sn){

    }


    private int setControlCommand(int reqType , int request, int value, int index, byte[] data)
    {
        int dataLength = 0;
        if(data != null)
            dataLength = data.length;
        int response = connection.controlTransfer(reqType, request, value, index, data, dataLength, 5000);
        Log.i(LOG_TAG, "Control Transfer Response: " + String.valueOf(response));
        return response;
    }



    @Override
    public boolean close() {
        return connection.releaseInterface(iface);
    }

    @Override
    public GlBean getRecord(int num) { //value index in glucometer
        String[] str = new String[2];
        readRecCmd[5] = (byte)num;
        write(readRecCmd);
        byte [] buffer = new byte[64];
        int a = read(buffer);
        Log.d(LOG_TAG,"getRecord buffer answer: "+a);
        if(a>0){
            String date = tools.convertUnixToDate(Long.parseLong(tools.hexToString(tools.reverseArray(Arrays.copyOfRange(buffer,11,15))),16));
            double value = tools.convertMgToMMoll(Integer.parseInt(tools.hexToString(tools.reverseArray(Arrays.copyOfRange(buffer,15,19))),16));
            GlBean gl = new GlBean(value, date);
            return gl;
        }

        return null;
    }

    public static boolean isDeviceSupported(int vendorID, int productID){
        if(vendorID==0x067b && productID==0x2303){
            return true;
        }
        return false;
    }


}


