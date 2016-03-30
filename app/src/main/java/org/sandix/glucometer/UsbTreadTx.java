package org.sandix.glucometer;

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.widget.Toast;

/**
 * Created by Alex on 28.02.2016.
 */
public class UsbTreadTx extends Thread {
    boolean running;

    UsbDeviceConnection txConnection;
    UsbEndpoint txEndPoint;
    Context context;


    public UsbTreadTx(UsbDeviceConnection conn,UsbEndpoint endpoint, Context context){
        this.txConnection = conn;
        this.txEndPoint = endpoint;
        this.context = context;
        running = true;
    }


    public void setRunning(boolean r){
        running = r;
    }

    @Override
    public void run() {
        super.run();
        byte[] buffer = new byte[64];
        txConnection.bulkTransfer(txEndPoint,buffer,txEndPoint.getMaxPacketSize(),0);
        Toast.makeText(context,"Answer: "+buffer.toString(),Toast.LENGTH_LONG).show();

    }
}
