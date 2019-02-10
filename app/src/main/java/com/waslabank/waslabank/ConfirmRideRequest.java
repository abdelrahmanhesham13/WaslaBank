package com.waslabank.waslabank;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;
import com.waslabank.waslabank.models.ChatModel;
import com.waslabank.waslabank.models.MyRideModel;
import com.waslabank.waslabank.models.NotificationModel;
import com.waslabank.waslabank.models.RideModel;
import com.waslabank.waslabank.models.UserModel;
import com.waslabank.waslabank.networkUtils.Connector;
import com.waslabank.waslabank.utils.GPSTracker;
import com.waslabank.waslabank.utils.Helper;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConfirmRideRequest extends AppCompatActivity {

    private final String TAG = ConfirmRideRequest.class.getSimpleName();
    @BindView(R.id.profile_image)
    ImageView mProfileImage;
    @BindView(R.id.name)
    TextView mNameTextView;
    @BindView(R.id.car)
    TextView mCarDetailsTextView;
    @BindView(R.id.from_place)
    TextView mFromPlaceTextView;
    @BindView(R.id.to_place)
    TextView mToPlaceTextView;
    @BindView(R.id.date)
    TextView mDateTextView;
    @BindView(R.id.time)
    TextView mTimeTextView;
    @BindView(R.id.distance)
    TextView mDistanceTextView;
    @BindView(R.id.confirm_button)
    Button mConfirmButton;
    @BindView(R.id.number_button)
    ElegantNumberButton mNumberButton;
    GPSTracker mTracker;

    RideModel mRideModel;
    MyRideModel mMyRideMode;
    NotificationModel mNotificationModel;

    Connector mConnector;
    Connector mConnectorGetRequest;
    Connector mConnectorAcceptOffer;
    Connector mConnectorGetUser;
    ProgressDialog mProgressDialog;

    Connector mConnectorSendMessage;

    boolean mLocated = false;
    private ChatModel mChatModel;

    UserModel mUserModel;
    UserModel mFromUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_ride_request);
        ButterKnife.bind(this);

        mUserModel = Helper.getUserSharedPreferences(this);


        mConnectorSendMessage = new Connector(ConfirmRideRequest.this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (getIntent().getStringExtra("type").equals("offer")) {
                    if (mUserModel.getId().equals(mRideModel.getUserId()))
                        mChatModel = Connector.getChatModelJson(response, mFromUser.getName(), mRideModel.getFromId(), mUserModel.getId());
                    else
                        mChatModel = Connector.getChatModelJson(response, mRideModel.getUser().getName(), mRideModel.getUserId(), mUserModel.getId());

                    startActivity(new Intent(ConfirmRideRequest.this, ChatActivity.class).putExtra("chat", mChatModel).putExtra("user",mFromUser).putExtra("ride_1",mRideModel));

                } else {
                    if (mUserModel.getId().equals(mMyRideMode.getUserId()))
                        mChatModel = Connector.getChatModelJson(response, mFromUser.getName(), mMyRideMode.getFromId(), mUserModel.getId());
                    else
                        mChatModel = Connector.getChatModelJson(response, mMyRideMode.getUser().getName(), mMyRideMode.getUserId(), mUserModel.getId());

                    startActivity(new Intent(ConfirmRideRequest.this, ChatActivity.class).putExtra("chat", mChatModel).putExtra("user",mFromUser).putExtra("ride_2",mMyRideMode));

                }
                //Intent returnIntent = new Intent();
                //returnIntent.putExtra("chat",mChatModel);


                //setResult(Activity.RESULT_OK,returnIntent);
                //finish();
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                Helper.showSnackBarMessage("خطأ من فضلك اعد المحاوله", ConfirmRideRequest.this);
            }
        });

        mConnectorGetUser = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                mProgressDialog.dismiss();
                if (Connector.checkStatus(response)){
                    mFromUser = Connector.getUser(response);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressDialog.dismiss();

            }
        });

        mConnectorGetRequest = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)) {
                    mRideModel = Connector.getRequest(response);
                    mFromPlaceTextView.setText(mRideModel.getAddress());
                    mToPlaceTextView.setText(mRideModel.getAddressTo());
                    mDateTextView.setText(mRideModel.getRequestDate());
                    mTimeTextView.setText(mRideModel.getRequestTime());
                    mDistanceTextView.setText(String.format(Locale.ENGLISH,"%.2f KM", Float.valueOf(mRideModel.getDistance())));
                    if (getIntent().getStringExtra("type").equals("offer")){
                        if (mUserModel.getId().equals(mRideModel.getUserId()))
                            mConnectorGetUser.getRequest(TAG, "http://www.cta3.com/waslabank/api/get_user?id=" + mRideModel.getFromId());
                        else
                            mConnectorGetUser.getRequest(TAG, "http://www.cta3.com/waslabank/api/get_user?id=" + mRideModel.getUserId());

                    } else {
                        if (mUserModel.getId().equals(mMyRideMode.getUserId()))
                            mConnectorGetUser.getRequest(TAG, "http://www.cta3.com/waslabank/api/get_user?id=" + mMyRideMode.getFromId());
                        else
                            mConnectorGetUser.getRequest(TAG, "http://www.cta3.com/waslabank/api/get_user?id=" + mMyRideMode.getUserId());
                    }
                } else {
                    Helper.showSnackBarMessage(getString(R.string.error), ConfirmRideRequest.this);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                Helper.showSnackBarMessage(getString(R.string.error), ConfirmRideRequest.this);
            }
        });

        mConnector = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                mProgressDialog.dismiss();
                if (Connector.checkStatus(response)) {
                    finish();
                    Toast.makeText(ConfirmRideRequest.this, getString(R.string.registered_successfully), Toast.LENGTH_LONG).show();
                } else {
                    Helper.showSnackBarMessage(getString(R.string.error), ConfirmRideRequest.this);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressDialog.dismiss();
                Helper.showSnackBarMessage(getString(R.string.error), ConfirmRideRequest.this);

            }
        });

        mConnectorAcceptOffer = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                mProgressDialog.dismiss();
                if (Connector.checkStatus(response)) {
                    finish();
                } else {
                    Helper.showSnackBarMessage(getString(R.string.error), ConfirmRideRequest.this);

                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressDialog.dismiss();
                Helper.showSnackBarMessage(getString(R.string.error), ConfirmRideRequest.this);

            }
        });

        mNumberButton.setRange(1, 10);
        mNumberButton.setNumber("1");
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null) {
            if (getIntent().getStringExtra("type").equals("show")) {
                setTitle(getString(R.string.ride_details));
                mMyRideMode = (MyRideModel) getIntent().getSerializableExtra("request");
                if (URLUtil.isValidUrl(mMyRideMode.getUser().getImage()))
                    Picasso.get().load(mMyRideMode.getUser().getImage()).fit().centerCrop().into(mProfileImage);
                else {
                    Picasso.get().load("http://www.cta3.com/waslabank/prod_img/" + mMyRideMode.getUser().getImage()).fit().centerCrop().into(mProfileImage);
                }
                mNameTextView.setText(mMyRideMode.getUser().getName());
                mCarDetailsTextView.setText(mMyRideMode.getUser().getCarName());
                mFromPlaceTextView.setText(mMyRideMode.getAddress());
                mToPlaceTextView.setText(mMyRideMode.getAddressTo());
                mDateTextView.setText(mMyRideMode.getRequestDate());
                mTimeTextView.setText(mMyRideMode.getRequestTime());
                mDistanceTextView.setText(String.format(Locale.ENGLISH,"%.2f KM", Float.valueOf(mMyRideMode.getDistance())));
                mNumberButton.setVisibility(View.GONE);
                mProgressDialog = Helper.showProgressDialog(this,getString(R.string.loading),false);
                if (mUserModel.getId().equals(mMyRideMode.getUserId()))
                    mConnectorGetUser.getRequest(TAG,"http://www.cta3.com/waslabank/api/get_user?id=" + mMyRideMode.getFromId());
                else
                    mConnectorGetUser.getRequest(TAG,"http://www.cta3.com/waslabank/api/get_user?id=" + mMyRideMode.getUserId());

                if (mMyRideMode.getStatus().equals("1")) {
                    mConfirmButton.setText(getString(R.string.message));
                } else {
                    mConfirmButton.setVisibility(View.GONE);
                }
            } else if (getIntent().getStringExtra("type").equals("offer")) {
                setTitle(getString(R.string.ride_details));
                mNotificationModel = (NotificationModel) getIntent().getSerializableExtra("notification");
                if (URLUtil.isValidUrl(mNotificationModel.getmUserModel().getImage()))
                    Picasso.get().load(mNotificationModel.getmUserModel().getImage()).fit().centerCrop().into(mProfileImage);
                else {
                    Picasso.get().load("http://www.cta3.com/waslabank/prod_img/" + mMyRideMode.getUser().getImage()).fit().centerCrop().into(mProfileImage);
                }
                mNameTextView.setText(mNotificationModel.getmUserModel().getName());
                mCarDetailsTextView.setText(mNotificationModel.getmUserModel().getCarName());
                mNumberButton.setVisibility(View.GONE);
                mProgressDialog = Helper.showProgressDialog(this, getString(R.string.loading), false);
                mConnectorGetRequest.getRequest(TAG, "http://www.cta3.com/waslabank/api/get_request?id=" + mNotificationModel.getRequestId());
                if (mNotificationModel.getStatus().equals("0")) {
                    mConfirmButton.setText(getString(R.string.accept));
                } else {
                    mConfirmButton.setText(getString(R.string.message));
                }
            } else {
                setTitle(getString(R.string.confirm_ride_request));
                mRideModel = (RideModel) getIntent().getSerializableExtra("request");
                if (URLUtil.isValidUrl(mRideModel.getUser().getImage()))
                    Picasso.get().load(mRideModel.getUser().getImage()).fit().centerCrop().into(mProfileImage);
                else {
                    Picasso.get().load("http://www.cta3.com/waslabank/prod_img/" + mRideModel.getUser().getImage()).fit().centerCrop().into(mProfileImage);
                }
                mNameTextView.setText(mRideModel.getUser().getName());
                mCarDetailsTextView.setText(mRideModel.getUser().getCarName());
                mFromPlaceTextView.setText(mRideModel.getAddress());
                mToPlaceTextView.setText(mRideModel.getAddressTo());
                mDateTextView.setText(mRideModel.getRequestDate());
                mTimeTextView.setText(mRideModel.getRequestTime());
                mDistanceTextView.setText(String.format(Locale.ENGLISH,"%.2f KM", Float.valueOf(mRideModel.getDistance())));
                mProgressDialog = Helper.showProgressDialog(this,getString(R.string.loading),false);
                if (mUserModel.getId().equals(mRideModel.getUserId()))
                    mConnectorGetUser.getRequest(TAG,"http://www.cta3.com/waslabank/api/get_user?id=" + mRideModel.getFromId());
                else
                    mConnectorGetUser.getRequest(TAG,"http://www.cta3.com/waslabank/api/get_user?id=" + mRideModel.getUserId());
            }

        }

        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mConfirmButton.getText().equals(getString(R.string.confirm))) {
                    getLocation();
                } else if (mConfirmButton.getText().equals(getString(R.string.accept))) {
                    mProgressDialog.show();
                    mConnectorAcceptOffer.getRequest(TAG, "http://www.cta3.com/waslabank/api/accept_offer?id=" + mNotificationModel.getRequestId() + "&from_id=" + mNotificationModel.getFromId() + "&user_id=" + mNotificationModel.getUserId());
                } else {
                    if (getIntent().getStringExtra("type").equals("offer")) {
                        if (mUserModel.getId().equals(mRideModel.getUserId())) {
                            String url = "http://www.cta3.com/waslabank/api/start_chat" + "?message=&user_id=" + mUserModel.getId() + "&to_id=" + mRideModel.getFromId() + "&request_id=" + mRideModel.getId();
                            Helper.writeToLog(url);
                            mConnectorSendMessage.getRequest(TAG, url);
                        } else {
                            String url = "http://www.cta3.com/waslabank/api/start_chat" + "?message=&user_id=" + mUserModel.getId() + "&to_id=" + mRideModel.getUserId() + "&request_id=" + mRideModel.getId();
                            Helper.writeToLog(url);
                            mConnectorSendMessage.getRequest(TAG, url);
                        }
                    } else {
                        if (mUserModel.getId().equals(mMyRideMode.getUserId())) {
                            String url = "http://www.cta3.com/waslabank/api/start_chat" + "?message=&user_id=" + mUserModel.getId() + "&to_id=" + mMyRideMode.getFromId() + "&request_id=" + mMyRideMode.getId();
                            Helper.writeToLog(url);
                            mConnectorSendMessage.getRequest(TAG, url);
                        } else {
                            String url = "http://www.cta3.com/waslabank/api/start_chat" + "?message=&user_id=" + mUserModel.getId() + "&to_id=" + mMyRideMode.getUserId() + "&request_id=" + mMyRideMode.getId();
                            Helper.writeToLog(url);
                            mConnectorSendMessage.getRequest(TAG, url);
                        }
                    }
                }
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;

    }

    private void getLocation() {
        mTracker = new GPSTracker(ConfirmRideRequest.this, new GPSTracker.OnGetLocation() {
            @Override
            public void onGetLocation(double lat, double lon) {
                if (lat != 0 && lon != 0 && !mLocated) {
                    mLocated = true;
                    mProgressDialog = Helper.showProgressDialog(ConfirmRideRequest.this, getString(R.string.loading), false);
                    mConnector.getRequest(TAG, "http://www.cta3.com/waslabank/api/send_offer?request_id=" + mRideModel.getId() + "&user_id=" + mRideModel.getUserId() + "&longitude=" + lon + "&latitude=" + lat + "&address=address&distance=100&from_id=" + Helper.getUserSharedPreferences(ConfirmRideRequest.this).getId() + "&seats=" + mNumberButton.getNumber());
                    mTracker.stopUsingGPS();
                }
            }
        });
        if (mTracker.canGetLocation()) {
            Location location = mTracker.getLocation();
            if (location != null) {
                if (location.getLatitude() != 0 && location.getLongitude() != 0 && !mLocated) {
                    mProgressDialog = Helper.showProgressDialog(ConfirmRideRequest.this, getString(R.string.loading), false);
                    mConnector.getRequest(TAG, "http://www.cta3.com/waslabank/api/send_offer?request_id=" + mRideModel.getId() + "&user_id=" + mRideModel.getUserId() + "&longitude=" + location.getLongitude() + "&latitude=" + location.getLatitude() + "&address=address&distance=100&from_id=" + Helper.getUserSharedPreferences(ConfirmRideRequest.this).getId() + "&seats=" + mNumberButton.getNumber());
                    mLocated = true;
                    mTracker.stopUsingGPS();
                }
            }
        }
    }
}
