package org.sandix.glucometer.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sandix.glucometer.DetailedInfoClientForm;
import org.sandix.glucometer.R;
import org.sandix.glucometer.beans.MainListBean;
import org.sandix.glucometer.beans.UserBean;
import org.sandix.glucometer.viewHolders.MainListUserViewHolder;

import java.util.Collections;
import java.util.List;

/**
 * Created by Alex on 24.04.2016.
 */
public class MainListUsersAdapter extends RecyclerView.Adapter<MainListUserViewHolder> {
    private List<UserBean> mUserInfoBeanList;
    private LayoutInflater mLayoutInflater;
    private Context context;


    public MainListUsersAdapter(Context context, List<UserBean> userInfoBeanList){
        this.mUserInfoBeanList = userInfoBeanList;
        mLayoutInflater = mLayoutInflater.from(context);
        this.context = context;
        Collections.sort(mUserInfoBeanList);

    }
    @Override
    public MainListUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainListUserViewHolder(mLayoutInflater.inflate(R.layout.main_list_users_row, parent, false), new MainListUserViewHolder.OnViewHolderClickListener() {
            @Override
            public void onClick(View caller, UserBean selectedUserBean) {
                Intent intent = new Intent(context, DetailedInfoClientForm.class);
                //Bundle bundle = new Bundle();
                intent.putExtra("userBean", selectedUserBean);
                //intent.putExtra("user_id", caller.getId());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public void onBindViewHolder(MainListUserViewHolder holder, int position) {
        holder.userInfoTv.setText(mUserInfoBeanList.get(position).getFIO());
        holder.userInfoTv.setId(mUserInfoBeanList.get(position).getId());
        holder.setmUserBean(mUserInfoBeanList.get(position));
        //holder.userInfoTv.setTag(mUserInfoBeanList.get(position).get);


    }

    @Override
    public int getItemCount() {
        return mUserInfoBeanList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


}
