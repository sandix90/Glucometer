package org.sandix.glucometer.beans;

/**
 * Created by sandakov.a on 19.04.2016.
 */
public class GlBean {

    private float gl_value;
    private String date;

    public GlBean(float gl_value, String date){
        this.gl_value = gl_value;
        this.date = date;
    }

    public float getGl_value(){
        return gl_value;
    }

    public String getDate() {
        return date;
    }
}
