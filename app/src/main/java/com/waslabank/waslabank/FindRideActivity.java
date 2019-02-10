package com.waslabank.waslabank;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.volley.VolleyError;
import com.waslabank.waslabank.adapters.RidesAdapter;
import com.waslabank.waslabank.models.RideModel;
import com.waslabank.waslabank.networkUtils.Connector;
import com.waslabank.waslabank.utils.Helper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FindRideActivity extends AppCompatActivity {

    private final String TAG = FindRideActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rides_recycler)
    RecyclerView mRidesRecycler;

    RidesAdapter mRidesAdapter;
    ArrayList<RideModel> mRides;

    Connector mConnector;
    ProgressDialog mProgressDialog;

    double mLatTo;
    double mLonTo;
    double mLatFrom;
    double mLonFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_ride);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        mToolbar.setNavigationOnClickListener(v -> finish());

        if (getIntent() != null){
            mLatFrom = getIntent().getDoubleExtra("latFrom",0);
            mLonTo = getIntent().getDoubleExtra("lonTo",0);
            mLatTo = getIntent().getDoubleExtra("latTo",0);
            mLonFrom = getIntent().getDoubleExtra("lonFrom",0);
        }

        mRides = new ArrayList<>();

        mConnector = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                mProgressDialog.dismiss();
                if (Connector.checkStatus(response)) {
                    mRides.addAll(Connector.getRequests(response));
                    mRidesAdapter.notifyDataSetChanged();
                } else {
                    Helper.showSnackBarMessage(getString(R.string.no_results), FindRideActivity.this);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressDialog.dismiss();
                Helper.showSnackBarMessage(getString(R.string.error), FindRideActivity.this);
            }
        });


        mRidesAdapter = new RidesAdapter(mRides, this, position -> {

        });

        mRidesRecycler.setHasFixedSize(true);
        mRidesRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRidesRecycler.setAdapter(mRidesAdapter);

        mProgressDialog = Helper.showProgressDialog(this,getString(R.string.loading),false);
        mConnector.getRequest(TAG,"http://www.cta3.com/waslabank/api/get_requests?lat_from=" + mLatFrom + "&long_from=" + mLonFrom + "&lat_to=" + mLatTo + "&long_to=" + mLonTo);


    }
}
