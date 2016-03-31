package org.sandix.glucometer;

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by sandakov.a on 31.03.2016.
 */
public class MyRunnable implements Runnable {
    Context context;
    TextView mMessage;
    UsbDeviceConnection txConnection;
    UsbEndpoint txEndPoint;

    public MyRunnable(Context context, TextView mMessage, UsbDeviceConnection conn,UsbEndpoint endpoint){
        this.context = context;
        this.mMessage = mMessage;
        this.txConnection = conn;
        this.txEndPoint = endpoint;

    }
    @Override
    public void run() {
        mMessage.setText("hello");
        byte[] buf = new byte[txEndPoint.getMaxPacketSize()];
        int recv_num = txConnection.bulkTransfer(txEndPoint, buf, buf.length,0);
        Toast.makeText(context,"recv_num: " + recv_num + " answ:" + buf, Toast.LENGTH_LONG);
        
        mMessage.setText("recv_num: " + recv_num + " answ:" + buf);
        //mMessage.setText("recv_num: ");
        //Toast.makeText(context, "Hello", Toast.LENGTH_LONG).show();

    }
}
