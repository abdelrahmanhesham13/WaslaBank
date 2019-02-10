package com.waslabank.waslabank;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;
import com.waslabank.waslabank.adapters.MessagesAdapter;
import com.waslabank.waslabank.models.ChatModel;
import com.waslabank.waslabank.models.MessageModel;
import com.waslabank.waslabank.models.MyRideModel;
import com.waslabank.waslabank.models.RideModel;
import com.waslabank.waslabank.models.UserModel;
import com.waslabank.waslabank.networkUtils.Connector;
import com.waslabank.waslabank.utils.Helper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatActivity extends AppCompatActivity {


    private final String TAG = ChatActivity.class.getSimpleName();
    @BindView(R.id.scroll)
    NestedScrollView scroll;
    @BindView(R.id.messages)
    RecyclerView mMessagesRecycler;
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.back_button)
    ImageView mBackButton;
    @BindView(R.id.store_image)
    ImageView mStoreImage;
    @BindView(R.id.refresh_button)
    ImageView refresh;
    @BindView(R.id.send_parent)
    View mSendParent;
    MessagesAdapter mAdapter;
    @BindView(R.id.message_text)
    EditText mMessageText;
    @BindView(R.id.send_btn)
    ImageView mSendMessageButton;
    @BindView(R.id.progressIndicator)
    ProgressBar mProgressBar;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    ArrayList<MessageModel> mMessageModels;
    ChatModel mChatModel;

    UserModel mUserModel;

    Connector mConnector;
    Connector mConnectorSendMessage;
    Connector mConnectorCompleteRide;

    ProgressDialog mProgressDialog;

    MyRideModel mMyRideModel;
    RideModel mRideModel;

    String message;

    UserModel mFromUser;

    Connector mConnectorRate;

    AlertDialog alertDialog;
    float mRatingNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ButterKnife.bind(this);

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        setSupportActionBar(mToolbar);
        if (getIntent() != null && getIntent().hasExtra("chat")) {
            mChatModel = (ChatModel) getIntent().getSerializableExtra("chat");
            mName.setText(mChatModel.getName());
        }

        if (getIntent() != null && getIntent().hasExtra("ride_2")) {
            mMyRideModel = (MyRideModel) getIntent().getSerializableExtra("ride_2");
        }

        if (getIntent() != null && getIntent().hasExtra("ride_1")) {
            mRideModel = (RideModel) getIntent().getSerializableExtra("ride_1");
        }

        if (getIntent() != null && getIntent().hasExtra("user")) {
            mFromUser = (UserModel) getIntent().getSerializableExtra("user");
            if (URLUtil.isValidUrl(mFromUser.getImage()))
                Picasso.get().load(mFromUser.getImage()).fit().centerCrop().into(mStoreImage);
            else {
                Picasso.get().load("http://www.cta3.com/waslabank/prod_img/" + mFromUser.getImage()).fit().centerCrop().into(mStoreImage);
            }
        }

        if (Helper.preferencesContainsUser(this)) {
            mUserModel = Helper.getUserSharedPreferences(this);
        }

        mMessageModels = new ArrayList<>();

        final MessagesAdapter adapter = new MessagesAdapter(this, mMessageModels, new MessagesAdapter.OnItemClicked() {
            @Override
            public void setOnItemClicked(int position) {

            }
        });

        mMessagesRecycler.setAdapter(adapter);


        mMessagesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


        mConnector = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)) {
                    mMessageModels.clear();
                    mMessageModels.addAll(Connector.getChatMessagesJson(response, mUserModel));
                    adapter.notifyDataSetChanged();
                    mMessagesRecycler.scrollToPosition(mMessageModels.size() - 1);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mMessagesRecycler.setVisibility(View.VISIBLE);
                    mSendParent.setVisibility(View.VISIBLE);
                } else {
                    Helper.showSnackBarMessage("لا يوجد رسائل", ChatActivity.this);
                    mMessagesRecycler.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mSendParent.setVisibility(View.VISIBLE);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mMessagesRecycler.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                mSendParent.setVisibility(View.VISIBLE);
                Helper.showSnackBarMessage("خطأ من فضلك اعد المحاوله", ChatActivity.this);

            }
        });

        mSendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = mMessageText.getText().toString();
                if (!Helper.validateFields(message)) {
                    Helper.showSnackBarMessage("ادخل الرساله", ChatActivity.this);
                } else {
                    Helper.hideKeyboard(ChatActivity.this, v);
                    mMessagesRecycler.setVisibility(View.INVISIBLE);
                    mSendParent.setVisibility(View.INVISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    String url;
                    if (getIntent() != null && getIntent().hasExtra("ride_2")) {
                        url = "https://www.cta3.com/waslabank/api/send_message" + "?chat_id=" + mChatModel.getChatId() +
                                "&user_id=" + mUserModel.getId() + "&to_id=" + mChatModel.getToId() + "&request_id=" + mMyRideModel.getId();
                    } else {
                        url = "https://www.cta3.com/waslabank/api/send_message" + "?chat_id=" + mChatModel.getChatId() +
                                "&user_id=" + mUserModel.getId() + "&to_id=" + mChatModel.getToId() + "&request_id=" + mRideModel.getId();
                    }
                    Uri builder = Uri.parse(url.toString())
                            .buildUpon()
                            .appendQueryParameter("message", message).build();


                    mConnectorSendMessage.getRequest(TAG, builder.toString());
                }
            }
        });

        mMessagesRecycler.setVisibility(View.INVISIBLE);
        mSendParent.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        String url = "https://www.cta3.com/waslabank/api/get_chat_messages" + "?chat_id=" + mChatModel.getChatId();
        mConnector.getRequest(TAG, url);


        mConnectorSendMessage = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                mMessageText.setText("");
                String url = "https://www.cta3.com/waslabank/api/get_chat_messages" + "?chat_id=" + mChatModel.getChatId();
                mConnector.getRequest(TAG, url);

            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mMessagesRecycler.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                mSendParent.setVisibility(View.VISIBLE);
                Helper.showSnackBarMessage("خطأ من فضلك اعد المحاوله", ChatActivity.this);

            }
        });


        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMessagesRecycler.setVisibility(View.INVISIBLE);
                mSendParent.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                String url = "https://www.cta3.com/waslabank/api/get_chat_messages" + "?chat_id=" + mChatModel.getChatId();
                mConnector.getRequest(TAG, url);
            }
        });

        mConnectorCompleteRide = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                mProgressDialog.dismiss();
                if (Connector.checkStatus(response)) {
                    mMessagesRecycler.setVisibility(View.INVISIBLE);
                    mSendParent.setVisibility(View.INVISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    String url;
                    if (getIntent() != null && getIntent().hasExtra("ride_2")) {
                        url = "https://www.cta3.com/waslabank/api/send_message" + "?chat_id=" + mChatModel.getChatId() +
                                "&user_id=" + mUserModel.getId() + "&to_id=" + mChatModel.getToId() + "&type=text" + "&request_id=" + mMyRideModel.getId();
                    } else {
                        url = "https://www.cta3.com/waslabank/api/send_message" + "?chat_id=" + mChatModel.getChatId() +
                                "&user_id=" + mUserModel.getId() + "&to_id=" + mChatModel.getToId() + "&type=text" + "&request_id=" + mRideModel.getId();
                    }
                    Uri builder = Uri.parse(url)
                            .buildUpon()
                            .appendQueryParameter("message", Connector.getMessage(response)).build();


                    mConnectorSendMessage.getRequest(TAG, builder.toString());
                    show();
                } else {
                    Helper.showSnackBarMessage(getString(R.string.error), ChatActivity.this);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressDialog.dismiss();
                Helper.showSnackBarMessage(getString(R.string.error), ChatActivity.this);
            }
        });


        mConnectorRate = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                alertDialog.dismiss();
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                alertDialog.dismiss();
                Helper.showSnackBarMessage(getString(R.string.error), ChatActivity.this);
            }
        });


    }


    private void show() {
        final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_rating, null);
        dialogBuilder.setView(dialogView);
        final RatingBar rating = dialogView.findViewById(R.id.rating_bar_2);
        final Button rate = dialogView.findViewById(R.id.btn_rate);
        final EditText comment = dialogView.findViewById(R.id.comment);
        rating.setIsIndicator(false);
        alertDialog = dialogBuilder.create();
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        alertDialog.show();
        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mRatingNumber = rating;
            }
        });
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentText = comment.getText().toString();
                if (TextUtils.isEmpty(commentText)) {
                    Helper.showSnackBarMessage(getString(R.string.enter_comment), ChatActivity.this);
                } else {
                    if (getIntent() != null && getIntent().hasExtra("ride_2")) {
                        if (mUserModel.getId().equals(mMyRideModel.getFromId())) {
                            if (getIntent() != null && getIntent().hasExtra("ride_2")) {
                                mConnectorRate.getRequest(TAG, "http://www.cta3.com/waslk/api/add_comment?comment=" + Uri.encode(commentText) + "&rating=" + mRatingNumber + "&request_id=" + mMyRideModel.getId() + "&delivery_id=" + mUserModel.getId());
                            } else {
                                mConnectorRate.getRequest(TAG, "http://www.cta3.com/waslk/api/add_comment?comment=" + Uri.encode(commentText) + "&rating=" + mRatingNumber + "&request_id=" + mRideModel.getId() + "&delivery_id=" + mUserModel.getId());

                            }
                        } else {
                            if (getIntent() != null && getIntent().hasExtra("ride_2")) {
                                mConnectorRate.getRequest(TAG, "http://www.cta3.com/waslk/api/add_comment?comment=" + Uri.encode(commentText) + "&rating=" + mRatingNumber + "&request_id=" + mMyRideModel.getId() + "&user_id=" + mUserModel.getId());
                            } else {
                                mConnectorRate.getRequest(TAG, "http://www.cta3.com/waslk/api/add_comment?comment=" + Uri.encode(commentText) + "&rating=" + mRatingNumber + "&request_id=" + mRideModel.getId() + "&user_id=" + mUserModel.getId());

                            }
                        }
                    } else {
                        if (mUserModel.getId().equals(mRideModel.getFromId())) {
                            if (getIntent() != null && getIntent().hasExtra("ride_2")) {
                                mConnectorRate.getRequest(TAG, "http://www.cta3.com/waslk/api/add_comment?comment=" + Uri.encode(commentText) + "&rating=" + mRatingNumber + "&request_id=" + mMyRideModel.getId() + "&delivery_id=" + mUserModel.getId());
                            } else {
                                mConnectorRate.getRequest(TAG, "http://www.cta3.com/waslk/api/add_comment?comment=" + Uri.encode(commentText) + "&rating=" + mRatingNumber + "&request_id=" + mRideModel.getId() + "&delivery_id=" + mUserModel.getId());

                            }
                        } else {
                            if (getIntent() != null && getIntent().hasExtra("ride_2")) {
                                mConnectorRate.getRequest(TAG, "http://www.cta3.com/waslk/api/add_comment?comment=" + Uri.encode(commentText) + "&rating=" + mRatingNumber + "&request_id=" + mMyRideModel.getId() + "&user_id=" + mUserModel.getId());
                            } else {
                                mConnectorRate.getRequest(TAG, "http://www.cta3.com/waslk/api/add_comment?comment=" + Uri.encode(commentText) + "&rating=" + mRatingNumber + "&request_id=" + mRideModel.getId() + "&user_id=" + mUserModel.getId());

                            }
                        }
                    }
                }
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.complete_ride) {
            mProgressDialog = Helper.showProgressDialog(this, getString(R.string.loading), false);
            if (getIntent() != null && getIntent().hasExtra("ride_2")) {
                mConnectorCompleteRide.getRequest(TAG, "http://www.cta3.com/waslk/api/cancel_offer?price=" + "" + "&id=" + mMyRideModel.getId() + "&delivery_id=" + mMyRideModel.getFromId() + "&user_id=" + mMyRideModel.getUserId());
            } else {
                mConnectorCompleteRide.getRequest(TAG, "http://www.cta3.com/waslk/api/cancel_offer?price=" + "" + "&id=" + mRideModel.getId() + "&delivery_id=" + mRideModel.getFromId() + "&user_id=" + mRideModel.getUserId());
            }

            return true;
        }
        return false;
    }
}
