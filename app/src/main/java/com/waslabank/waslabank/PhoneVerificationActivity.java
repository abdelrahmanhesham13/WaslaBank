package com.waslabank.waslabank;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.waslabank.waslabank.networkUtils.Connector;
import com.waslabank.waslabank.utils.Helper;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhoneVerificationActivity extends AppCompatActivity {

    private final String TAG = PhoneVerificationActivity.class.getSimpleName();
    @BindView(R.id.verify)
    Button mVerifyButton;
    @BindView(R.id.firstPinView)
    PinView mPinView;

    Connector mConnector;
    ProgressDialog mProgressDialog;

    String mFirstName;
    String mLastName;
    String mMobile;
    String mEmail;
    String mPassword;
    String mImagePath;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    String mVerificationId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        ButterKnife.bind(this);
        mPinView.setAnimationEnable(true);

        if (getIntent() != null){
            mFirstName = getIntent().getStringExtra("firstName");
            mLastName = getIntent().getStringExtra("lastName");
            mEmail = getIntent().getStringExtra("email");
            mMobile = getIntent().getStringExtra("mobile");
            mPassword = getIntent().getStringExtra("password");
            mImagePath = getIntent().getStringExtra("image");
        }

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

                Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseTooManyRequestsException)
                    Helper.showSnackBarMessage(getString(R.string.blocked_device), PhoneVerificationActivity.this);
                else
                    Helper.showSnackBarMessage(getString(R.string.error_in_phone), PhoneVerificationActivity.this);

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
                mResendToken = token;

            }
        };

        FirebaseAuth.getInstance().useAppLanguage();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mMobile,
                30,
                TimeUnit.SECONDS,
                this,
                mCallbacks);

        mVerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.hideKeyboard(PhoneVerificationActivity.this,view);
                if (mPinView.getText() != null && !mPinView.getText().toString().isEmpty()) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, mPinView.getText().toString());
                    signInWithPhoneAuthCredential(credential);
                } else {
                    Helper.showSnackBarMessage(getString(R.string.enter_code),PhoneVerificationActivity.this);
                }
            }
        });


        mConnector = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                mProgressDialog.dismiss();
                if (Connector.checkStatus(response)){
                    Helper.saveUserToSharedPreferences(PhoneVerificationActivity.this,Connector.getUser(response));
                    startActivity(new Intent(PhoneVerificationActivity.this,WhereYouGoActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                } else {
                    finish();
                    Toast.makeText(PhoneVerificationActivity.this,getString(R.string.registered),Toast.LENGTH_LONG).show();
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressDialog.dismiss();
                Toast.makeText(PhoneVerificationActivity.this,getString(R.string.error),Toast.LENGTH_LONG).show();
                finish();
            }
        });



    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mProgressDialog = Helper.showProgressDialog(PhoneVerificationActivity.this,getString(R.string.loading),false);
                            mConnector.getRequest(TAG,"https://www.cta3.com/waslabank/api/signup?password=" + Uri.encode(mPassword) + "&username=" + mEmail + "&mobile=" + mMobile +"&name=" + Uri.encode(mFirstName + " " + mLastName) + "&image=" + mImagePath + "&token=" + Helper.getTokenFromSharedPreferences(PhoneVerificationActivity.this));
                        } else {
                            Helper.showSnackBarMessage(getString(R.string.error_in_code), PhoneVerificationActivity.this);
                        }
                    }
                });
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mConnector != null)
            mConnector.cancelAllRequests(TAG);
    }

}
