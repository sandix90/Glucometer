package org.sandix.glucometer;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.widget.Toast;

import org.sandix.glucometer.db.DB;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by Alex on 03.04.2016.
 */
public class tools {

    private static final String HEXES = "0123456789ABCDEF";
    private static final String HEX_INDICATOR = "0x";
    private static final String SPACE = " ";

    public static String convertBytesToText(byte[] data){
        StringBuilder str = new StringBuilder();
        for(int i=0;i<data.length-1;i++){
            str.append(data[i]);
        }
        return new String(data, StandardCharsets.UTF_8);
    }

    public static double convertMgToMMoll(int value){
        DecimalFormat df=new DecimalFormat("0.00");
        String format = df.format(value* 0.0555);
        double finalValue;
        try {
            finalValue = (Double)df.parse(format);
        } catch (ParseException e) {
            return -1;
        }
        return finalValue;
        //return Math.rouround(value * 0.0555);
        //return value * 0.0555;
    }

    public static String hexToString(byte[] data)
    {
        if(data != null)
        {
            StringBuilder hex = new StringBuilder(2*data.length);
            for(int i=0;i<=data.length-1;i++)
            {
                byte dataAtIndex = data[i];
                //hex.append(HEX_INDICATOR);
                hex.append(HEXES.charAt((dataAtIndex & 0xF0) >> 4))
                        .append(HEXES.charAt((dataAtIndex & 0x0F)));
                //hex.append(SPACE);
            }
            return hex.toString();
        }else
        {
            return null;
        }
    }

    public static byte[] reverseArray(byte[] data){
        byte tmp;
        for(int i =0;i<data.length/2;i++){
            tmp = data[data.length-i-1];
            data[data.length-i-1] = data[i];
            data[i]=tmp;

        }
        return data;

    }

    public static String getAppDirectory(){
        return Environment.getExternalStorageDirectory().getAbsolutePath()+"/glucomaster/";
    }

    public static String getTimeStamp(){
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss.SSS");
        Date date = new Date();
        return df.format(date);
    }

    public static String convertUnixToDate(long unixSeconds){
        Date date = new Date(unixSeconds*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return sdf.format(date);
    }

}
