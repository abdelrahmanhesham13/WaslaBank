package com.waslabank.waslabank;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.waslabank.waslabank.networkUtils.Connector;
import com.waslabank.waslabank.utils.Helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.sign_up_button)
    View mSignUpButton;
    @BindView(R.id.login_button)
    Button mLoginButton;
    @BindView(R.id.facebook_login)
    Button mFacebookLogin;
    @BindView(R.id.google_login)
    Button mGoogleLogin;
    @BindView(R.id.email)
    EditText mEmailEditText;
    @BindView(R.id.password)
    EditText mPasswordEditText;

    CallbackManager callbackManager;
    GoogleSignInClient mGoogleSignInClient;

    Connector mConnector;
    Connector mConnectorSocial;
    Connector mConnectorSignUp;

    ProgressDialog mProgressDialog;

    String mEmail;
    String mName;
    String mImage;

    private final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        mConnector = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                mProgressDialog.dismiss();
                if (Connector.checkStatus(response)){
                    Helper.saveUserToSharedPreferences(LoginActivity.this,Connector.getUser(response));
                    startActivity(new Intent(LoginActivity.this,WhereYouGoActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                } else {
                    Helper.showSnackBarMessage(getString(R.string.error_in_credentials),LoginActivity.this);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressDialog.dismiss();
                Helper.showSnackBarMessage(getString(R.string.error),LoginActivity.this);
            }
        });

        mConnectorSocial = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)){
                    mProgressDialog.dismiss();
                    Helper.saveUserToSharedPreferences(LoginActivity.this,Connector.getUser(response));
                    startActivity(new Intent(LoginActivity.this,WhereYouGoActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                } else {
                    mConnectorSignUp.getRequest(TAG,"https://www.cta3.com/waslabank/api/signup?password=" + Uri.encode("") + "&username=" + mEmail + "&mobile=" + "" +"&name=" + Uri.encode(mName) + "&image=" + mImage + "&token=" + Helper.getTokenFromSharedPreferences(LoginActivity.this));

                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressDialog.dismiss();
                Helper.showSnackBarMessage(getString(R.string.error),LoginActivity.this);
            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.hideKeyboard(LoginActivity.this,view);
                mEmail = mEmailEditText.getText().toString();
                if (mEmailEditText.getText().toString().isEmpty()){
                    Helper.showSnackBarMessage(getString(R.string.mobile),LoginActivity.this);
                } else if (mPasswordEditText.getText().toString().isEmpty()){
                    Helper.showSnackBarMessage(getString(R.string.enter_password),LoginActivity.this);
                } else {
                    mProgressDialog = Helper.showProgressDialog(LoginActivity.this,getString(R.string.loading),false);
                    mConnector.getRequest(TAG,"https://www.cta3.com/waslabank/api/login?password=" + Uri.encode(mPasswordEditText.getText().toString()) + "&username=" + mEmailEditText.getText().toString() + "&token=" + Helper.getTokenFromSharedPreferences(LoginActivity.this));
                }
            }
        });

        mFacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callbackManager = CallbackManager.Factory.create();
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email"));
                LoginManager.getInstance().registerCallback(callbackManager,
                        new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                GraphRequest request = GraphRequest.newMeRequest(
                                        loginResult.getAccessToken(),
                                        new GraphRequest.GraphJSONObjectCallback() {
                                            @Override
                                            public void onCompleted(JSONObject object, GraphResponse response) {
                                                try {
                                                    mEmail = object.getString("email");
                                                    mName = object.getString("name");
                                                    mImage = "https://graph.facebook.com/" + object.get("id") + "/picture?type=large";
                                                    mProgressDialog = Helper.showProgressDialog(LoginActivity.this,getString(R.string.loading),false);
                                                    mConnectorSocial.getRequest(TAG, "https://www.cta3.com/waslabank/api/login?" + "username=" + mEmail + "&token=" + Helper.getTokenFromSharedPreferences(LoginActivity.this));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                Bundle parameters = new Bundle();
                                parameters.putString("fields", "id,name,email,gender,birthday");
                                request.setParameters(parameters);
                                request.executeAsync();

                            }

                            @Override
                            public void onCancel() {
                                Helper.showSnackBarMessage(getString(R.string.error), LoginActivity.this);
                            }

                            @Override
                            public void onError(FacebookException exception) {
                                exception.printStackTrace();
                                Helper.showSnackBarMessage(getString(R.string.error), LoginActivity.this);
                            }
                        });
            }
        });


        mConnectorSignUp = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                mProgressDialog.dismiss();
                if (Connector.checkStatus(response)){
                    Helper.saveUserToSharedPreferences(LoginActivity.this,Connector.getUser(response));
                    startActivity(new Intent(LoginActivity.this,WhereYouGoActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressDialog.dismiss();
                Helper.showSnackBarMessage(getString(R.string.error),LoginActivity.this);

            }
        });

        mGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGoogleSignInClient.signOut().addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mGoogleSignInClient.revokeAccess().addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                signIn();
                            }
                        });
                    }
                });
            }
        });

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 2);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                mEmail = account.getEmail();
                mName = account.getDisplayName();
                if (account.getPhotoUrl() != null)
                    mImage = account.getPhotoUrl().toString();
                else
                    mImage = "";
                if (mEmail != null) {
                    mProgressDialog = Helper.showProgressDialog(LoginActivity.this,getString(R.string.loading),false);
                    mConnectorSocial.getRequest(TAG, "https://www.cta3.com/waslabank/api/login?" + "username=" + mEmail + "&token=" + Helper.getTokenFromSharedPreferences(LoginActivity.this));
                }
            }

        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mConnector != null)
            mConnector.cancelAllRequests(TAG);
    }

    public void openEmail(View view) {
        mEmailEditText.setFocusableInTouchMode(true);
        mEmailEditText.requestFocus();
        final InputMethodManager inputMethodManager = (InputMethodManager) LoginActivity.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(mEmailEditText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void openPassword(View view) {
        mPasswordEditText.setFocusableInTouchMode(true);
        mPasswordEditText.requestFocus();
        final InputMethodManager inputMethodManager = (InputMethodManager) LoginActivity.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(mPasswordEditText, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
