package org.sandix.glucometer.beans;

/**
 * Created by Alex on 24.04.2016.
 */
public class MainListBean {
    private int id;
    private String last_name;
    private String first_name;

    public MainListBean(int id, String last_name, String first_name){
        this.id = id;
        this.last_name = last_name;
        this.first_name = first_name;
    }

    public String getName(){
        return last_name + " " + first_name;
    }
}
