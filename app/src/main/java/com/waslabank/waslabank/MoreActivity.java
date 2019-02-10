package com.waslabank.waslabank;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thefinestartist.finestwebview.FinestWebView;
import com.waslabank.waslabank.models.UserModel;
import com.waslabank.waslabank.utils.Helper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoreActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.friends_button)
    TextView mFriendsButton;
    @BindView(R.id.profile_image)
    ImageView mProfileImage;
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.rating_bar)
    RatingBar mRatingBar;
    @BindView(R.id.verify_account)
    TextView mVerifyAccount;
    @BindView(R.id.my_rides)
    TextView mMyRides;
    @BindView(R.id.notifications)
    TextView mNotifications;
    @BindView(R.id.my_daily_rides)
    TextView mDailyRides;
    @BindView(R.id.card)
    View mProfile;
    @BindView(R.id.reviews)
    TextView mReviews;
    @BindView(R.id.chats)
    TextView mChats;
    @BindView(R.id.link_button)
    TextView mLink;
    @BindView(R.id.logout)
    TextView mLogout;
    @BindView(R.id.rate)
    TextView mRate;
    @BindView(R.id.refer)
    TextView mRefer;
    @BindView(R.id.terms)
    TextView mTerms;



    UserModel mUserModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        mToolbar.setNavigationOnClickListener(v -> finish());

        mFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MoreActivity.this,FriendsActivity.class));
            }
        });

        mMyRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MoreActivity.this,MyRidesActivity.class));
            }
        });

        mNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MoreActivity.this,NotificationsActivity.class));
            }
        });

        mDailyRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MoreActivity.this,MyDailyRidesActivity.class));
            }
        });

        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MoreActivity.this,ProfileActivity.class));
            }
        });

        mReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MoreActivity.this,ProfileActivity.class));
            }
        });

        mChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MoreActivity.this,ChatsActivity.class));
            }
        });

        mLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MoreActivity.this,LinkAccountActivity.class));
            }
        });

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.removeUserFromSharedPreferences(MoreActivity.this);
                Intent homeIntent = new Intent(MoreActivity.this,LoginActivity.class);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                finishAffinity();
            }
        });

        mRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        mRefer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Check Wasla Bank Application: https://play.google.com/store/apps/details?id=" + getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        mTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FinestWebView.Builder(MoreActivity.this).updateTitleFromHtml(false).titleDefault(getString(R.string.app_name)).show("https://google.com");
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        mUserModel = Helper.getUserSharedPreferences(this);

        if (URLUtil.isValidUrl(mUserModel.getImage()))
            Picasso.get().load(mUserModel.getImage()).fit().centerCrop().into(mProfileImage);
        else {
            Picasso.get().load("http://www.cta3.com/waslabank/prod_img/" + mUserModel.getImage()).fit().centerCrop().into(mProfileImage);
        }
        mVerifyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MoreActivity.this,VerifyDriverAccountActivity.class));
            }
        });
        mName.setText(mUserModel.getName());
        if (!mUserModel.getRating().isEmpty())
            mRatingBar.setRating(Float.parseFloat(mUserModel.getRating()));

        if (mUserModel.getStatus().equals("0"))
            mVerifyAccount.setVisibility(View.VISIBLE);
        else
            mVerifyAccount.setVisibility(View.GONE);
    }
}
