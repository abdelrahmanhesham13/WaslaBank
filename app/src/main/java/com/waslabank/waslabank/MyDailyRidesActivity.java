package com.waslabank.waslabank;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.android.volley.VolleyError;
import com.waslabank.waslabank.adapters.DailyRidesAdapter;
import com.waslabank.waslabank.models.DailyRideModel;
import com.waslabank.waslabank.networkUtils.Connector;
import com.waslabank.waslabank.utils.Helper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyDailyRidesActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rides)
    RecyclerView mRidesRecycler;

    ProgressDialog mProgressDialog;
    Connector mConnector;

    ArrayList<DailyRideModel> mDailyRides;
    DailyRidesAdapter mAdapter;
    private final String TAG = MyDailyRidesActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_daily_rides);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        mToolbar.setNavigationOnClickListener(v -> finish());

        mDailyRides = new ArrayList<>();

        mConnector = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                mProgressDialog.dismiss();
                if (Connector.checkStatus(response)){
                    mDailyRides.addAll(Connector.getMyRequestsDaily(response,MyDailyRidesActivity.this));
                    mAdapter.notifyDataSetChanged();
                } else {
                    Helper.showSnackBarMessage(getString(R.string.no_rides),MyDailyRidesActivity.this);
                }

            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressDialog.dismiss();
                Helper.showSnackBarMessage(getString(R.string.error),MyDailyRidesActivity.this);
            }
        });

        mAdapter = new DailyRidesAdapter(mDailyRides, this, new DailyRidesAdapter.OnItemClicked() {
            @Override
            public void setOnItemClicked(int position) {

            }
        });

        mRidesRecycler.setHasFixedSize(true);
        mRidesRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRidesRecycler.setAdapter(mAdapter);

        mProgressDialog = Helper.showProgressDialog(this,getString(R.string.loading),false);
        mConnector.getRequest(TAG,"http://www.cta3.com/waslabank/api/get_dailies?user_id=" + Helper.getUserSharedPreferences(this).getId());

    }
}
