package org.sandix.glucometer.models;

/**
 * Created by Alex on 03.04.2016.
 */
public interface IGlucometer {


    public int getRecordsCount();
    public String getSerialNumber(byte[] data);
    public String[] getRecord(int num);
    public boolean isSupported(int vendorID, int productID);
    int write(byte[] data);
    int read(byte[] data);

}
