package com.waslabank.waslabank;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.waslabank.waslabank.adapters.MyRidesAdapter;
import com.waslabank.waslabank.models.MyRideModel;
import com.waslabank.waslabank.networkUtils.Connector;
import com.waslabank.waslabank.utils.Helper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class RideHistoryFragment extends Fragment {

    private final String TAG = RideHistoryFragment.class.getSimpleName();
    @BindView(R.id.my_rides_recycler)
    RecyclerView mMyRidesRecycler;

    MyRidesAdapter mMyRidesAdapter;
    ArrayList<MyRideModel> mMyRidesModels;

    Connector mConnector;
    ProgressDialog mProgressDialog;

    int mPos;

    public RideHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ride_history, container, false);
        ButterKnife.bind(this,v);
        mMyRidesModels = new ArrayList<>();

        mConnector = new Connector(getContext(), new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                mProgressDialog.dismiss();
                if (Connector.checkStatus(response)){
                    mMyRidesModels.addAll(Connector.getMyRequestsHistory(response,getContext()));
                    mMyRidesAdapter.notifyDataSetChanged();
                } else {
                    if (getActivity() != null)
                        Helper.showSnackBarMessage(getString(R.string.no_rides),(AppCompatActivity) getActivity());
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressDialog.dismiss();
                if (getActivity() != null)
                    Helper.showSnackBarMessage(getString(R.string.error),(AppCompatActivity) getActivity());
            }
        });

        mMyRidesAdapter = new MyRidesAdapter(mMyRidesModels,getContext(),position -> {
            startActivity(new Intent(getContext(),ConfirmRideRequest.class).putExtra("type","show").putExtra("request",mMyRidesModels.get(position)));
        });
        mMyRidesRecycler.setHasFixedSize(true);
        mMyRidesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mMyRidesRecycler.setAdapter(mMyRidesAdapter);
        mProgressDialog = Helper.showProgressDialog(getContext(),getString(R.string.loading),false);
        mConnector.getRequest(TAG,"http://www.cta3.com/waslabank/api/get_requests?user_id=" + Helper.getUserSharedPreferences(getContext()).getId());

        return v;
    }

}
