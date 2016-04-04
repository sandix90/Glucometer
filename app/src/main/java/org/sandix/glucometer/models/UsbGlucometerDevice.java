package org.sandix.glucometer.models;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;

/**
 * Created by sandakov.a on 04.04.2016.
 */
public abstract class UsbGlucometerDevice {
    private UsbDeviceConnection connection;
    private UsbDevice device;


    private UsbEndpoint inEndPoint;
    private UsbEndpoint outEndPoint;


    public UsbGlucometerDevice(UsbDevice device, UsbDeviceConnection connection){
        this.device = device;
        this.connection = connection;


    }



    public static UsbGlucometerDevice initializeUsbDevice(android.hardware.usb.UsbDevice device, UsbDeviceConnection connection){

        int vendorID = device.getVendorId();
        int productID = device.getProductId();
        if(OneTouchUltraEasy.isSupported(vendorID,productID)) {
            return new OneTouchUltraEasy(device, connection);
        }
        return null;
    }

    public abstract String getSN();
    public abstract int getRecordsCount();
    public abstract boolean open();
    public abstract boolean close();
    public abstract String[] getRecord(int num);

    public int write(byte[] data){
        if(data==null){
            return 0;
        }
        return connection.bulkTransfer(outEndPoint,data,data.length,0);
    }

    public int read(byte[] buffer){
        try {
            Thread.sleep(2000);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        if(buffer==null){
            return 0;
        }
        return connection.bulkTransfer(inEndPoint,buffer,buffer.length,0);
    }

}
