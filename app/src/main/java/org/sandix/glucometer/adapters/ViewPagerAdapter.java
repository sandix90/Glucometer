package org.sandix.glucometer.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.sandix.glucometer.beans.UserBean;
import org.sandix.glucometer.tabFragments.LineDiagram;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sandakov.a on 18.05.2016.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    public void addFragment(Fragment fragment, String title){
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public Fragment getItem(int position) {
//        switch (position){
//            case 0:
//                LineDiagram lineDiagram = new LineDiagram();
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("userBean", userBean);
//                lineDiagram.setArguments(bundle);
//                return  lineDiagram;
//
//        }
        return mFragmentList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
