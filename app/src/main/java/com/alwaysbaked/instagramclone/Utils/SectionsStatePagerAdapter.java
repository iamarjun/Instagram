package com.alwaysbaked.instagramclone.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SectionsStatePagerAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragmentList;
    private final HashMap<Fragment, Integer> mFragments;
    private final HashMap<String, Integer> mFragmentsNumbers;
    private final HashMap<Integer, String> mFragmentNames;

    public SectionsStatePagerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentList = new ArrayList<>();
        mFragments = new HashMap<>();
        mFragmentsNumbers = new HashMap<>();
        mFragmentNames = new HashMap<>();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String fragmentName){
        mFragmentList.add(fragment);
        mFragments.put(fragment, mFragmentList.size()-1);
        mFragmentsNumbers.put(fragmentName, mFragmentNames.size()-1);
        mFragmentNames.put(mFragmentList.size()-1, fragmentName);
    }

    /**
     * returns a fragment with name @params
     * @param fragmentName
     * @return
     */

    public Integer getFragmentNumber(String fragmentName){
        if (mFragmentsNumbers.containsKey(fragmentName))
            return mFragmentsNumbers.get(fragmentName);
        else
            return null;
    }

    /**
     * returns a fragment with name @params
     * @param fragment
     * @return
     */

    public Integer getFragmentNumber(Fragment fragment){
        if (mFragmentsNumbers.containsKey(fragment))
            return mFragmentsNumbers.get(fragment);
        else
            return null;
    }

    /**
     * returns a fragment with name @params
     * @param fragmentNumber
     * @return
     */

    public String getFragmentName(Integer fragmentNumber){
        if (mFragmentNames.containsKey(fragmentNumber))
            return mFragmentNames.get(fragmentNumber);
        else
            return null;
    }
}
