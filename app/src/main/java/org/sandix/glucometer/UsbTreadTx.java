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
        byte[] buf = new byte[txEndPoint.getMaxPacketSize()];
        int recv_num = txConnection.bulkTransfer(txEndPoint, buf, buf.length,0);
        //Toast.makeText(("recv_num: " + recv_num + " answ:" + buf);
        Toast.makeText(context, "recv_num: " + recv_num + " answ:" + buf, Toast.LENGTH_LONG).show();

    }
}
