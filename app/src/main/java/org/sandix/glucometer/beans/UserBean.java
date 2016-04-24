package org.sandix.glucometer.beans;

import java.util.List;

import javax.microedition.khronos.opengles.GL;

/**
 * Created by Alex on 24.04.2016.
 */
public class UserBean {
    private int id;
    private String serial_number;
    private String last_name;
    private String first_name;
    private String middle_name;
    private int age;
    private String email;
    private String therapy_type;
    private String diabetic_type;
    private String comments;
    private String phone;
    private String gender;
    private List<GlBean> glBeanList;

    public UserBean(int id, String serial_number, String last_name, String first_name, String middle_name, int age,  String email, String therapy_type,
                    String diabetic_type, String phone, String gender, String comments, List<GlBean> glBeanList){
        this.id = id;
        this.serial_number = serial_number;
        this.last_name = last_name;
        this.first_name = first_name;
        this.middle_name = middle_name;
        this.age = age;
        this.email = email;
        this.therapy_type = therapy_type;
        this.diabetic_type = diabetic_type;
        this.phone = phone;
        this.gender = gender;
        this.comments = comments;
        this.glBeanList = glBeanList;




    }


}
