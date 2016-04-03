package org.sandix.glucometer;

import java.nio.charset.StandardCharsets;

/**
 * Created by Alex on 03.04.2016.
 */
public class tools {

    public static String convertBytesToText(byte[] data){
        StringBuilder str = new StringBuilder();
        for(int i=0;i<data.length-1;i++){
            str.append(data[i]);
        }
        return new String(data, StandardCharsets.UTF_8);
    }

    public static float convertMgToMMoll(int value){
        return Math.round(value * 0.0555);
    }

}
