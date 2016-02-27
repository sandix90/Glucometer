package org.sandix.glucometer;

import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    UsbManager mUsbManager;
    ListView deviceList;
    Button refreshBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        deviceList = (ListView) findViewById(R.id.device_list);
        refreshBtn = (Button) findViewById(R.id.refresh_btn);
        refreshBtn.setOnClickListener(this);
        findDevice();

    }

    private void findDevice(){

        for(UsbDevice usbDevice: mUsbManager.getDeviceList().values()){
            TextView tv = new TextView(this);
            tv.setText(usbDevice.getDeviceName().toString());
            deviceList.addView(tv);
        }

    }

    @Override
    public void onClick(View v) {
        findDevice();
    }
}
