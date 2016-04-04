package org.sandix.glucometer.models;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;

/**
 * Abstract class for detect device model
 * Use: \n
 * 1. call static method initializeUsbDevice
 * 2. then open()
 * 3. use prepared methods to get information from glucometer
 * 4. close connection
 */
public abstract class UsbGlucometerDevice {
    protected UsbDeviceConnection connection; //Can't be private, because it need for children classes access.
    protected UsbDevice device;

    private UsbEndpoint inEndPoint;
    private UsbEndpoint outEndPoint;


    public UsbGlucometerDevice(UsbDevice device, UsbDeviceConnection connection){
        this.device = device;
        this.connection = connection;
    }

    public static UsbGlucometerDevice initializeUsbDevice(UsbDevice device, UsbDeviceConnection connection){
        //Lets make a choice of device model
        int vendorID = device.getVendorId();
        int productID = device.getProductId();

        if(OneTouchUltraEasy.isDeviceSupported(vendorID,productID)) {
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

    protected void setUsbEndPoints(UsbEndpoint inEndPoint, UsbEndpoint outEndPoint){
        this.inEndPoint = inEndPoint;
        this.outEndPoint = outEndPoint;
    }

}
