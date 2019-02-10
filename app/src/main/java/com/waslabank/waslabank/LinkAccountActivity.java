package com.waslabank.waslabank;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.waslabank.waslabank.adapters.FriendsAdapter;
import com.waslabank.waslabank.adapters.UserAdapter;
import com.waslabank.waslabank.models.UserModel;
import com.waslabank.waslabank.networkUtils.Connector;
import com.waslabank.waslabank.utils.Helper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LinkAccountActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private final String TAG = LinkAccountActivity.class.getSimpleName();
    @BindView(R.id.friends_recycler)
    RecyclerView mUsersRecycler;
    @BindView(R.id.done)
    ImageView mDone;
    @BindView(R.id.search)
    EditText mSearch;

    ArrayList<UserModel> mUserModels;

    Connector mConnector;
    ProgressDialog mProgressDialog;
    UserAdapter mFriendsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_account);
        ButterKnife.bind(this);
        mUserModels = new ArrayList<>();

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        mToolbar.setNavigationOnClickListener(v -> finish());

        mConnector = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                mProgressDialog.dismiss();
                if (Connector.checkStatus(response)){
                    mUserModels.clear();
                    mUserModels.addAll(Connector.getUsersAdd(response));
                    mFriendsAdapter.notifyDataSetChanged();
                } else {
                    Helper.showSnackBarMessage(getString(R.string.no_results),LinkAccountActivity.this);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressDialog.dismiss();
                Helper.showSnackBarMessage(getString(R.string.error),LinkAccountActivity.this);

            }
        });

        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressDialog = Helper.showProgressDialog(LinkAccountActivity.this,getString(R.string.loading),false);
                mConnector.getRequest(TAG,"https://www.cta3.com/waslabank/api/search_users?mobile=" + mSearch.getText().toString() + "&user_id=" + Helper.getUserSharedPreferences(LinkAccountActivity.this).getId());

            }
        });


        mFriendsAdapter = new UserAdapter(mUserModels, this, new FriendsAdapter.OnItemClicked() {
            @Override
            public void setOnItemClicked(int position) {

            }
        });
        mUsersRecycler.setHasFixedSize(true);
        mUsersRecycler.setLayoutManager(new LinearLayoutManager(this));
        mUsersRecycler.setAdapter(mFriendsAdapter);
    }
}
