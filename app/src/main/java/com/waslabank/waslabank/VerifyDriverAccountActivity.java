package com.waslabank.waslabank;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.waslabank.waslabank.models.CarModel;
import com.waslabank.waslabank.models.ColorModel;
import com.waslabank.waslabank.networkUtils.Connector;
import com.waslabank.waslabank.utils.Helper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;

public class VerifyDriverAccountActivity extends AppCompatActivity {

    @BindView(R.id.car_color)
    Spinner mCarColorSpinner;
    @BindView(R.id.car_name)
    Spinner mCarNameSpinner;
    @BindView(R.id.license_image)
    ImageView mLicenseImageView;
    @BindView(R.id.car_image)
    ImageView mCarImageView;
    @BindView(R.id.national_id)
    EditText mNationalIdEditText;
    @BindView(R.id.car_details)
    EditText mCarDetailsEditText;
    @BindView(R.id.confirm)
    Button mConfirmButton;

    Connector mConnectorGetCarsData;
    Connector mConnectorGetColorsData;
    Connector mConnector;

    ArrayList<CarModel> mCars;
    ArrayList<ColorModel> mColors;

    private final String TAG = VerifyDriverAccountActivity.class.getSimpleName();

    String mLicenseImage = null;
    String mCarImage = null;
    String mSelectedColorId = null;
    String mSelectedCarId = null;

    int mType = -1;

    File mSelectedFile;

    ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_driver_account);
        ButterKnife.bind(this);
        mCars = new ArrayList<>();
        mColors = new ArrayList<>();
        setupSpinners();
        mLicenseImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mType = 1;
                pickImage();
            }
        });

        mCarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mType = 2;
                pickImage();
            }
        });

        mConnector = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                mProgressDialog.dismiss();
                if (Connector.checkStatus(response)){
                    Helper.saveStatusToSharePreferences(VerifyDriverAccountActivity.this,"1");
                    Toast.makeText(VerifyDriverAccountActivity.this,getString(R.string.verified_successfully),Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Helper.showSnackBarMessage(getString(R.string.error), VerifyDriverAccountActivity.this);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressDialog.dismiss();
                Helper.showSnackBarMessage(getString(R.string.error), VerifyDriverAccountActivity.this);
            }
        });

        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.hideKeyboard(VerifyDriverAccountActivity.this,view);
                if (mLicenseImage == null){
                    Helper.showSnackBarMessage(getString(R.string.license_image), VerifyDriverAccountActivity.this);
                } else if (mCarImage == null){
                    Helper.showSnackBarMessage(getString(R.string.upload_car_image), VerifyDriverAccountActivity.this);
                } else if (mNationalIdEditText.getText().toString().isEmpty()){
                    Helper.showSnackBarMessage(getString(R.string.enter_national_id), VerifyDriverAccountActivity.this);
                } else if (mCarDetailsEditText.getText().toString().isEmpty()){
                    Helper.showSnackBarMessage(getString(R.string.enter_car_details), VerifyDriverAccountActivity.this);
                } else if (mSelectedCarId == null){
                    Helper.showSnackBarMessage(getString(R.string.car_name_select), VerifyDriverAccountActivity.this);
                } else if (mSelectedColorId == null){
                    Helper.showSnackBarMessage(getString(R.string.car_color_select), VerifyDriverAccountActivity.this);
                } else {
                    mProgressDialog.show();
                    mConnector.getRequest(TAG,"http://www.cta3.com/waslabank/api/verify_account?user_id=" + Helper.getUserSharedPreferences(VerifyDriverAccountActivity.this).getId() + "&license=" + mLicenseImage + "&car_image=" + mCarImage + "&car_id=" + mSelectedCarId + "&color_id=" + mSelectedColorId + "&name=" + Uri.encode(mCarDetailsEditText.getText().toString()) + "&national_id=" + mNationalIdEditText.getText().toString());
                }
            }
        });

    }

    private void pickImage() {
        ImagePicker.with(this)
                .setFolderMode(true) // folder mode (false by default)
                .setFolderTitle(getString(R.string.image_folder)) // folder selection title
                .setImageTitle(getString(R.string.select_image)) // image selection title
                .setMaxSize(1) //  Max images can be selected
                .setMultipleMode(false) //single mode
                .setShowCamera(true) // show camera or not (true by default)
                .start(); // start image picker activity with Request code
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            Image img = images.get(0);
            try {
                Bitmap bitmapImage = getBitmap(img.getPath());
                if (mType == 1)
                    mLicenseImageView.setImageBitmap(bitmapImage);
                else
                    mCarImageView.setImageBitmap(bitmapImage);
                mSelectedFile = bitmapToFile(img.getName(), checkImage(img.getPath(), getBitmap(img.getPath())));
                UploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void UploadImage() {
        mProgressDialog = Helper.showProgressDialog(this,
                getString(R.string.loading),
                false);

        mProgressDialog.show();
        Ion.with(this)
                .load("https://www.cta3.com/waslabank/api/upload_image_api")
                .setMultipartFile("parameters[0]", "image", mSelectedFile)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        Helper.writeToLog(result);
                        if (Connector.checkImages(result)) {
                            mProgressDialog.dismiss();
                            ArrayList<String> imagePaths = Connector.getImages(result);
                            if (mType == 1)
                                mLicenseImage = imagePaths.get(0);
                            else
                                mCarImage = imagePaths.get(0);
                        } else {
                            Helper.showSnackBarMessage(getString(R.string.error), VerifyDriverAccountActivity.this);
                            mProgressDialog.dismiss();
                        }
                    }
                });
    }

    private Bitmap getBitmap(String path) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
        return getResizedBitmap(bitmap, 600);
    }

    private File bitmapToFile(String name, Bitmap bmap) throws IOException {
        if (VerifyDriverAccountActivity.this.getExternalCacheDir() != null) {
            File f = new File(VerifyDriverAccountActivity.this.getExternalCacheDir().getAbsolutePath() + "/" + name);
            boolean crated = f.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bmap.compress(Bitmap.CompressFormat.JPEG, 70, bos);
            byte[] bitmapData = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
            return f;
        }
        return null;
    }

    private Bitmap checkImage(String path, Bitmap bitmap) throws IOException {

        ExifInterface ei = new ExifInterface(path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);
        Bitmap rotatedBitmap = null;

        switch (orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }


        return rotatedBitmap;
    }

    private Bitmap getBitmapFromPath(String path, int size) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
        return getResizedBitmap(bitmap, size);
    }


    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    private void setupSpinners(){
        ArrayList<String> carNames = new ArrayList<>();
        carNames.add(0,getString(R.string.select_one));
        ArrayAdapter adapterNames = new ArrayAdapter<String>(this, R.layout.spinner_item, carNames) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {

                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        adapterNames.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCarNameSpinner.setAdapter(adapterNames);

        ArrayList<String> carColors = new ArrayList<>();
        carColors.add(0,getString(R.string.select_one));
        ArrayAdapter adapterColors = new ArrayAdapter<String>(this, R.layout.spinner_item, carColors) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {

                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        adapterColors.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCarColorSpinner.setAdapter(adapterColors);
        mCarColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (mColors.size() > 1) {
                    mSelectedColorId = mColors.get(i - 1).getId();
                    Helper.writeToLog(mSelectedColorId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mCarNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (mCars.size() > 1) {
                    mSelectedCarId = mCars.get(i - 1).getId();
                    Helper.writeToLog(mSelectedCarId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mConnectorGetCarsData = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)){
                    mCars.addAll(Connector.getCars(response));
                    for (int i = 0;i<mCars.size();i++){
                        carNames.add(mCars.get(i).getName());
                    }
                    adapterNames.notifyDataSetChanged();
                } else {
                    Helper.showSnackBarMessage(getString(R.string.error), VerifyDriverAccountActivity.this);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                Helper.showSnackBarMessage(getString(R.string.error), VerifyDriverAccountActivity.this);
            }
        });

        mConnectorGetColorsData = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)){
                    mColors.addAll(Connector.getColors(response));
                    for (int i = 0;i<mColors.size();i++){
                        carColors.add(mColors.get(i).getName());
                    }
                    adapterColors.notifyDataSetChanged();
                } else {
                    Helper.showSnackBarMessage(getString(R.string.error), VerifyDriverAccountActivity.this);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                Helper.showSnackBarMessage(getString(R.string.error), VerifyDriverAccountActivity.this);
            }
        });

        mConnectorGetCarsData.getRequest(TAG,"https://www.cta3.com/waslabank/api/cars");
        mConnectorGetColorsData.getRequest(TAG,"https://www.cta3.com/waslabank/api/colors");

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mConnectorGetColorsData != null)
            mConnectorGetColorsData.cancelAllRequests(TAG);
        if (mConnectorGetCarsData != null)
            mConnectorGetCarsData.cancelAllRequests(TAG);
    }

    public void openCarDetails(View view) {
        mCarDetailsEditText.setFocusableInTouchMode(true);
        mCarDetailsEditText.requestFocus();
        final InputMethodManager inputMethodManager = (InputMethodManager) VerifyDriverAccountActivity.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(mCarDetailsEditText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void openNationalId(View view) {
        mNationalIdEditText.setFocusableInTouchMode(true);
        mNationalIdEditText.requestFocus();
        final InputMethodManager inputMethodManager = (InputMethodManager) VerifyDriverAccountActivity.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(mNationalIdEditText, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
