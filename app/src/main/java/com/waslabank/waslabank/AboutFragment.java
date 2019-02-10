package com.waslabank.waslabank;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.waslabank.waslabank.models.UserModel;
import com.waslabank.waslabank.utils.Helper;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {


    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.car_name)
    TextView mCarName;
    @BindView(R.id.email)
    TextView mEmail;
    @BindView(R.id.mobile)
    TextView mMobile;
    @BindView(R.id.rating)
    TextView mRating;

    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this,v);
        UserModel user = Helper.getUserSharedPreferences(getContext());

        mName.setText("Name : " + user.getName());
        mCarName.setText("Car Name : " + user.getCarName());
        mEmail.setText("Email : " + user.getUsername());
        mMobile.setText("Mobile Number : " + user.getMobile());
        mRating.setText("Rating : " + user.getRating() + "/5.0");



        return v;
    }

}
