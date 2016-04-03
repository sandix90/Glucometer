package org.sandix.glucometer.models;

/**
 * Created by Alex on 03.04.2016.
 */
public class OneTouchUltraEasy implements IGlucometer{
    int vendorId;
    int productId;


    public void OneTouchUltraEase(){

    }

    @Override
    public int getRecordsCount() {
        byte[] recordsCount =  {0x02,0x0A,0x00,0x05,0x1F, (byte) 0xF5,0x01,0x03,0x038, (byte) 0xAA};
        return 0;
    }

    @Override
    public String getSerialNumber() {
        return null;
    }
}
