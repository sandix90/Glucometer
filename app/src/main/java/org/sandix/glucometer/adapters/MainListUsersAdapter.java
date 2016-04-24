package org.sandix.glucometer.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sandix.glucometer.DetailedInfoClientForm;
import org.sandix.glucometer.R;
import org.sandix.glucometer.beans.MainListBean;
import org.sandix.glucometer.viewHolders.MainListUserViewHolder;

import java.util.List;

/**
 * Created by Alex on 24.04.2016.
 */
public class MainListUsersAdapter extends RecyclerView.Adapter<MainListUserViewHolder> {
    private List<MainListBean> mUserInfoBeanList;
    private LayoutInflater mLayoutInflater;
    private Context context;


    public MainListUsersAdapter(Context context, List<MainListBean> userInfoBeanList){
        this.mUserInfoBeanList = userInfoBeanList;
        mLayoutInflater = mLayoutInflater.from(context);
        this.context = context;

    }
    @Override
    public MainListUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainListUserViewHolder(mLayoutInflater.inflate(R.layout.main_list_users_row, parent, false), new MainListUserViewHolder.OnViewHolderClickListener() {
            @Override
            public void onClick(View caller) {
                Intent intent = new Intent(context, DetailedInfoClientForm.class);
                intent.putExtra("user_id", caller.getId());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public void onBindViewHolder(MainListUserViewHolder holder, int position) {
        holder.userInfoTv.setText(mUserInfoBeanList.get(position).getName());
        holder.userInfoTv.setId(mUserInfoBeanList.get(position).getId());


    }

    @Override
    public int getItemCount() {
        return mUserInfoBeanList.size();
    }
}
