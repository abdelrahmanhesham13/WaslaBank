package com.waslabank.waslabank;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.waslabank.waslabank.networkUtils.Connector;
import com.waslabank.waslabank.utils.Helper;

public class SplashActivity extends AppCompatActivity {

    final int PERMISSIONS_REQUEST = 1;
    boolean isGPSEnabled;
    boolean isNetworkEnabled;

    Connector mConnector;
    ProgressDialog mProgressDialog;
    private final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mConnector = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                mProgressDialog.dismiss();
                if (Connector.checkStatus(response)){
                    Helper.saveUserToSharedPreferences(SplashActivity.this,Connector.getUser(response));
                    startActivity(new Intent(SplashActivity.this,WhereYouGoActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                } else {
                    Helper.showSnackBarMessage(getString(R.string.error_in_credentials),SplashActivity.this);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressDialog.dismiss();
                Helper.showSnackBarMessage(getString(R.string.error),SplashActivity.this);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    if (!isGPSEnabled && !isNetworkEnabled) {
                        showSettingsAlert();
                    } else {
                        if (Helper.preferencesContainsUser(SplashActivity.this)) {
                            mProgressDialog = Helper.showProgressDialog(SplashActivity.this,getString(R.string.loading),false);
                            mConnector.getRequest(TAG,"https://www.cta3.com/waslabank/api/login?password=" + Uri.encode(Helper.getUserSharedPreferences(SplashActivity.this).getPassword()) + "&username=" + Uri.encode(Helper.getUserSharedPreferences(SplashActivity.this).getUsername()) + "&token=" + Helper.getTokenFromSharedPreferences(SplashActivity.this));
                        } else {
                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                            finish();
                        }
                    }
                } else {
                    finish();
                }
            }
        }
    }


    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Location Settings");

        // Setting Dialog Message
        alertDialog.setMessage("Location is disabled, Please Turn it on in High Accuracy");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                dialog.cancel();
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        LocationManager locationManager = (LocationManager) SplashActivity.this
                .getSystemService(Context.LOCATION_SERVICE);

        // getting GPS status
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);

        } else {
            if (!isGPSEnabled && !isNetworkEnabled) {
                showSettingsAlert();
            } else {
                if (Helper.preferencesContainsUser(SplashActivity.this)) {
                    mProgressDialog = Helper.showProgressDialog(SplashActivity.this,getString(R.string.loading),false);
                    mConnector.getRequest(TAG,"https://www.cta3.com/waslabank/api/login?password=" + Uri.encode(Helper.getUserSharedPreferences(SplashActivity.this).getPassword()) + "&username=" + Uri.encode(Helper.getUserSharedPreferences(SplashActivity.this).getUsername()) + "&token=" + Helper.getTokenFromSharedPreferences(SplashActivity.this));
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }
    }
}
