package org.sandix.glucometer.beans;

import java.io.Serializable;

/**
 * Created by sandakov.a on 19.04.2016.
 */
public class GlBean implements Serializable {
    /*
    * Serializable нужен для того, чтобы пересылать объект из одной активити в другую в адаптере MainListUsersAdapter в onCreateViewHolder
    * */
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
