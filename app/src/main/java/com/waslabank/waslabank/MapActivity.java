package com.waslabank.waslabank;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.waslabank.waslabank.utils.GPSTracker;
import com.waslabank.waslabank.utils.Helper;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import se.arbitur.geocoding.Callback;
import se.arbitur.geocoding.Response;
import se.arbitur.geocoding.ReverseGeocoding;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    GPSTracker mTracker;
    @BindView(R.id.progressIndicator)
    ProgressBar progressBar;
    @BindView(R.id.address)
    TextView mAddressTextView;
    @BindView(R.id.send_btn)
    FloatingActionButton mSendButton;

    double mLat = 0;
    double mLon = 0;
    String mAddress = "";
    String mCity = "";
    String mCountry = "";

    boolean mLocated = false;

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.map));

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        if (autocompleteFragment.getView() != null)
            ((EditText) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(13.0f);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 16.0f);
                mMap.animateCamera(cameraUpdate);
            }

            @Override
            public void onError(Status status) {
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLat != 0 && mLon != 0 && !mAddress.isEmpty() && !mCity.isEmpty() && !mCountry.isEmpty()) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("lat", mLat);
                    returnIntent.putExtra("lon", mLon);
                    returnIntent.putExtra("address", mAddress);
                    returnIntent.putExtra("city", mCity);
                    returnIntent.putExtra("country", mCountry);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mProgressDialog = Helper.showProgressDialog(MapActivity.this, getString(R.string.loading), false);
        mTracker = new GPSTracker(MapActivity.this, new GPSTracker.OnGetLocation() {
            @Override
            public void onGetLocation(double lat, double lon) {
                if (lat != 0 && lon != 0 && !mLocated) {
                    mProgressDialog.dismiss();
                    mLocated = true;
                    mLat = lat;
                    mLon = lon;
                    LatLng latLng = new LatLng(lat, lon);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f);
                    mMap.animateCamera(cameraUpdate);
                    if (progressBar.getVisibility() == View.VISIBLE && mAddressTextView.getVisibility() == View.INVISIBLE) {
                        progressBar.setVisibility(View.VISIBLE);
                        mAddressTextView.setVisibility(View.INVISIBLE);
                    }
                    if (progressBar.getVisibility() == View.GONE && mAddressTextView.getVisibility() == View.VISIBLE) {
                        progressBar.setVisibility(View.GONE);
                        mAddressTextView.setVisibility(View.VISIBLE);
                    }
                    mAddress = getAddress(latLng);
                    mAddressTextView.setText(mAddress);
                    mTracker.stopUsingGPS();
                }
            }
        });
        if (mTracker.canGetLocation()) {
            Location location = mTracker.getLocation();
            if (location != null) {
                if (location.getLatitude() != 0 && location.getLongitude() != 0 && !mLocated) {
                    mLat = location.getLatitude();
                    mProgressDialog.dismiss();
                    mLon = location.getLongitude();
                    mLocated = true;
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f);
                    mMap.animateCamera(cameraUpdate);
                    if (progressBar.getVisibility() == View.VISIBLE && mAddressTextView.getVisibility() == View.INVISIBLE) {
                        progressBar.setVisibility(View.VISIBLE);
                        mAddressTextView.setVisibility(View.INVISIBLE);
                    }
                    if (progressBar.getVisibility() == View.GONE && mAddressTextView.getVisibility() == View.VISIBLE) {
                        progressBar.setVisibility(View.GONE);
                        mAddressTextView.setVisibility(View.VISIBLE);
                    }
                    mAddress = getAddress(latLng);
                    mAddressTextView.setText(mAddress);
                    mTracker.stopUsingGPS();
                }
            }
        }
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng latLng = mMap.getCameraPosition().target;
                mLat = latLng.latitude;
                mLon = latLng.longitude;
                if (progressBar.getVisibility() == View.VISIBLE && mAddressTextView.getVisibility() == View.INVISIBLE) {
                    progressBar.setVisibility(View.VISIBLE);
                    mAddressTextView.setVisibility(View.INVISIBLE);
                }
                if (progressBar.getVisibility() == View.GONE && mAddressTextView.getVisibility() == View.VISIBLE) {
                    progressBar.setVisibility(View.GONE);
                    mAddressTextView.setVisibility(View.VISIBLE);
                }
                mAddressTextView.setText(getAddress(latLng));
            }
        });
    }


    public String getAddress(LatLng latLng) {
        new ReverseGeocoding(latLng.latitude, latLng.longitude, "AIzaSyATc3Nte8Pj1oWTFKAbLWUiJbzSIJEDzxc")
                .setLanguage("en")
                .fetch(new Callback() {
                    @Override
                    public void onResponse(Response response) {
                        mAddress = response.getResults()[0].getFormattedAddress();
                        for (int i = 0; i < response.getResults()[0].getAddressComponents().length; i++) {
                            for (int j = 0; j < response.getResults()[0].getAddressComponents()[i].getAddressTypes().length; j++) {
                                switch (response.getResults()[0].getAddressComponents()[i].getAddressTypes()[j]) {
                                    case "administrative_area_level_1":
                                        mCity = response.getResults()[0].getAddressComponents()[i].getLongName();
                                        break;
                                    case "country":
                                        mCountry = response.getResults()[0].getAddressComponents()[i].getLongName();
                                        break;
                                }
                            }
                        }
                        if (mAddress == null)
                            mAddress = "";
                        if (mCity == null)
                            mCity = "";
                        if (mCountry == null)
                            mCountry = "";
                        mAddressTextView.setText(mAddress);

                    }

                    @Override
                    public void onFailed(Response response, IOException e) {

                    }
                });
        return mAddress;

    }
}
