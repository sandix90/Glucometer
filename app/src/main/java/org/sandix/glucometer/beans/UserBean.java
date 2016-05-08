package org.sandix.glucometer.beans;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import javax.microedition.khronos.opengles.GL;

/**
 * Created by Alex on 24.04.2016.
 */
public class UserBean implements Comparable, Serializable {
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
                    String diabetic_type, String phone, String gender, String comments){
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

    }

    public void setGlBeanList(List<GlBean> glBeanList) {
        this.glBeanList = glBeanList;
    }

    public int getId() {
        return id;
    }

    public String getSerial_number() {
        return serial_number;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public int getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public String getTherapy_type() {
        return therapy_type;
    }

    public String getDiabetic_type() {
        return diabetic_type;
    }

    public String getComments() {
        return comments;
    }

    public String getPhone() {
        return phone;
    }

    public String getGender() {
        return gender;
    }

    public String getFIO() { return last_name + " "+ first_name;}

    public List<GlBean> getGlBeanList() {
        return glBeanList;
    }

    @Override
    public int compareTo(Object another) {
        UserBean sec_obj = (UserBean)another;
        return getFIO().compareToIgnoreCase(sec_obj.getFIO());
    }
}
