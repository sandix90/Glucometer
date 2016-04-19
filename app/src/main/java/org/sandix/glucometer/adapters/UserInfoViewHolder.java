package org.sandix.glucometer.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.sandix.glucometer.R;

/**
 * Created by sandakov.a on 18.04.2016.
 */
public class UserInfoViewHolder extends RecyclerView.ViewHolder {
    ImageView icon;
    TextView tv;

    public UserInfoViewHolder(View itemView){
        super(itemView);
        icon = (ImageView) itemView.findViewById(R.id.icon);
        tv = (TextView) itemView.findViewById(R.id.textview);
    }

    public void bindData(int icon_num, String data){
        this.icon.setImageResource(R.drawable.ic_email_black_24dp);
        this.tv.setText(data);
        this.tv.setHint("Test hint");

    }
}
