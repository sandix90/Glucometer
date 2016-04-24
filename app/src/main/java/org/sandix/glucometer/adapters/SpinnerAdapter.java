package org.sandix.glucometer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.sandix.glucometer.R;

import java.util.ArrayList;

/**
 * Created by Alex on 24.04.2016.
 */
public class SpinnerAdapter extends ArrayAdapter<String> {

    LayoutInflater inflater;
    ArrayList<String> mDatas;

    public SpinnerAdapter(Context context, ArrayList<String> objects){
        super(context, R.layout.spinner_row,objects);
        mDatas = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = inflater.inflate(R.layout.spinner_row,parent,false);
        TextView tv_row = (TextView) row.findViewById(R.id.tv_item);
        tv_row.setText(mDatas.get(position).toString());
        return row;
    }
}
