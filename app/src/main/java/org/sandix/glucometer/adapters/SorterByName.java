package org.sandix.glucometer.adapters;

import org.sandix.glucometer.beans.UserBean;

import java.util.Comparator;

/**
 * Created by Alex on 07.05.2016.
 */
public class SorterByName implements Comparator {
    @Override
    public int compare(Object lhs, Object rhs) {
        UserBean first_object = (UserBean)lhs;
        UserBean second_object = (UserBean)rhs;

        return first_object.getFIO().compareToIgnoreCase(second_object.getFIO());
    }
}
