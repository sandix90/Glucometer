package org.sandix.glucometer;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
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
import java.util.TimeZone;

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
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy kk:mm:ss.SSS");
        Date date = new Date();
        return df.format(date);
    }

    public static String convertUnixToDate(long unixSeconds){
        Date date = new Date(unixSeconds*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(date);
    }

    public static String calculateCRC16(byte[] bytes){
        int crc = 0xFFFF;          // initial value
        int polynomial = 0x1021;   // 0001 0000 0010 0001  (0, 5, 12)


        for (byte b : bytes) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b   >> (7-i) & 1) == 1);
                boolean c15 = ((crc >> 15    & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= polynomial;
            }
        }

        crc &= 0xffff; //Младший байт
        return decimal2hex(crc);

    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static String decimal2hex(int d) {
        String digits = "0123456789ABCDEF";
        if (d == 0) return "0";
        String hex = "";
        while (d > 0) {
            int digit = d % 16; // rightmost digit
            hex = digits.charAt(digit) + hex;  // string concatenation
            d = d / 16;
        }
        return hex;
    }

}
