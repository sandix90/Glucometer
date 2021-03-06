package org.sandix.glucometer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.sandix.glucometer.R;
import org.sandix.glucometer.beans.Bean;

import java.util.List;

/**
 * Created by sandakov.a on 18.04.2016.
 */
public class UserInfoAdapter extends RecyclerView.Adapter<UserInfoViewHolder> {

    private LayoutInflater mLayoutInflater;
    List<Bean> mDatas;



    public UserInfoAdapter(Context context, List<Bean> mDatas) {
        this.mDatas = mDatas;
        mLayoutInflater = mLayoutInflater.from(context);
        prepareItems();
    }

    private void prepareItems() {

    }

    @Override
    public UserInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserInfoViewHolder(mLayoutInflater.inflate(R.layout.userinfo_item, parent, false));
    }

    @Override
    public void onBindViewHolder(UserInfoViewHolder holder, int position) {
        //holder.bindData(0, mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

}
