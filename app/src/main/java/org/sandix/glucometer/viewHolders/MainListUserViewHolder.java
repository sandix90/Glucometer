package org.sandix.glucometer.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.sandix.glucometer.R;


/**
 * Created by Alex on 24.04.2016.
 */
public class MainListUserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView userInfoTv;
    private OnViewHolderClickListener mListener;

    public MainListUserViewHolder(View itemView, OnViewHolderClickListener listener ) {
        super(itemView);
        userInfoTv = (TextView) itemView.findViewById(R.id.user_info);
        userInfoTv.setOnClickListener(this);
        mListener = listener;

    }

    @Override
    public void onClick(View v) {
        if(v instanceof TextView){
            mListener.onClick(v);
        }
    }

    public interface OnViewHolderClickListener{
        void onClick(View caller);
    }
}
