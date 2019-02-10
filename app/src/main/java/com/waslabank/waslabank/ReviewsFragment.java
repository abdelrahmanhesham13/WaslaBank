package com.waslabank.waslabank;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.waslabank.waslabank.adapters.ReviewsAdapter;
import com.waslabank.waslabank.models.ReviewModel;
import com.waslabank.waslabank.networkUtils.Connector;
import com.waslabank.waslabank.utils.Helper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewsFragment extends Fragment {

    @BindView(R.id.reviews)
    RecyclerView mReviews;

    ArrayList<ReviewModel> mReviewModels;
    ProgressDialog mProgressDialog;

    Connector mConnector;

    ReviewsAdapter mAdapter;



    public ReviewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_reviews, container, false);
        ButterKnife.bind(this,v);

        mReviewModels = new ArrayList<>();


        mConnector = new Connector(getContext(), new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                mProgressDialog.dismiss();
                if (Connector.checkStatus(response)){
                    mReviewModels.addAll(Connector.getReviews(response));
                    mAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(),"No Reviews",Toast.LENGTH_LONG).show();
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressDialog.dismiss();
                Toast.makeText(getContext(),getString(R.string.error),Toast.LENGTH_LONG).show();

            }
        });


        mAdapter = new ReviewsAdapter(mReviewModels, getContext(), new ReviewsAdapter.OnItemClicked() {
            @Override
            public void setOnItemClicked(int position) {

            }
        });

        mReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        mReviews.setAdapter(mAdapter);
        mReviews.setNestedScrollingEnabled(false);


        mProgressDialog = Helper.showProgressDialog(getContext(),getString(R.string.loading),false);
        mConnector.getRequest("ReviewsActivity","http://www.cta3.com/waslabank/api/get_comments?user_id=" + Helper.getUserSharedPreferences(getContext()).getId());



        return v;
    }

}
