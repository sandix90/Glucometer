package org.sandix.glucometer.models;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;

import org.sandix.glucometer.beans.GlBean;

/**
 * Created by sandakov.a on 15.06.2016.
 */
public class AccuCheckPerformaNano extends UsbGlucometerDevice {

    public AccuCheckPerformaNano(UsbDevice device, UsbDeviceConnection connection) {
        super(device, connection);
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
        return false;
    }

    @Override
    public boolean close() {
        return false;
    }

    @Override
    public GlBean getRecord(int num) {
        return null;
    }
}
