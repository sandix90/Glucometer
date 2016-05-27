package org.sandix.glucometer.models;

import org.sandix.glucometer.beans.GlBean;

/**
 * Created by sandakov.a on 27.05.2016.
 */
public class GlucometerProxy {

    private  static GlucometerProxy instance;
    private volatile UsbGlucometerDevice glucometerDevice;

    private GlucometerProxy(){

    }

    public static GlucometerProxy getInstance(){
        GlucometerProxy localInstance = instance;
        if(localInstance==null){
            synchronized (GlucometerProxy.class){
                localInstance =instance;
                if(localInstance==null){
                    instance=localInstance=new GlucometerProxy();
                }
            }
        }
        return localInstance;
    }

    public void setGlDevice(UsbGlucometerDevice device){
        glucometerDevice = device;
    }
    public boolean isGlucometerNull(){
        if(glucometerDevice==null){
            return true;
        }
        return false;
    }
    public String getSerialNumber(){
        return glucometerDevice.getSN();
    }

    public int getValuesCount(){
        return glucometerDevice.getRecordsCount();
    }

    public GlBean getValueByIndex(int index){
        return glucometerDevice.getRecord(index);
    }

}
