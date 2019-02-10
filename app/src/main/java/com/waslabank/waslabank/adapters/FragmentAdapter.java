package com.waslabank.waslabank.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.waslabank.waslabank.MyRidesFragment;
import com.waslabank.waslabank.R;
import com.waslabank.waslabank.RideHistoryFragment;

/**
 * Created by Abdelrahman Hesham on 4/22/2017.
 */

public class FragmentAdapter extends FragmentStatePagerAdapter {
    private Context mContext;

    public FragmentAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
               return new MyRidesFragment();
            case 1:
                return new RideHistoryFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.my_rides_small);
            case 1:
                return mContext.getString(R.string.ride_history);
            default:
                return null;
        }
    }





}
