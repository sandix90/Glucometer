package org.sandix.glucometer.beans;

/**
 * Created by sandakov.a on 19.04.2016.
 */
public class Bean{
    public static final int CATEGORY_FIRST_NAME = 0;
    public static final int CATEGORY_LAST_NAME = 1;
    public static final int CATEGORY_MIDDLE_NAME = 2;
    public static final int CATEGORY_EMAIL = 3;
    public static final int CATEGORY_PHONE = 4;
    public static final int CATEGORY_GLUC_VALUE = 8;

    private int id_category;
    private Object Beanobj;

    public Bean(int id_category, Object BeanObj){
        this.id_category = id_category;
        this.Beanobj = BeanObj;
    }
}
