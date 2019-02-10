package com.waslabank.waslabank;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.waslabank.waslabank.networkUtils.Connector;
import com.waslabank.waslabank.utils.Helper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.profile_image)
    ImageView mProfileImageView;
    @BindView(R.id.f_name)
    EditText mFirstNameEditText;
    @BindView(R.id.l_name)
    EditText mLastNameEditText;
    @BindView(R.id.password)
    EditText mPasswordEditText;
    @BindView(R.id.confirm_password)
    EditText mPasswordConfirmEditText;
    @BindView(R.id.mobile)
    EditText mMobileEditText;
    @BindView(R.id.email)
    EditText mEmailEditText;
    @BindView(R.id.sign_up_button)
    Button mSignUpButton;

    String mImagePath = null;

    ProgressDialog mProgressDialog;
    File mSelectedFile;

    private final String TAG = SignUpActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);



        mProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });


        mSignUpButton.setOnClickListener(view -> {
            if (mImagePath == null){
                Helper.showSnackBarMessage(getString(R.string.upload_your_image),SignUpActivity.this);
            } else if (mFirstNameEditText.getText().toString().isEmpty()){
                Helper.showSnackBarMessage(getString(R.string.enter_first_name),SignUpActivity.this);
            } else if (mLastNameEditText.getText().toString().isEmpty()){
                Helper.showSnackBarMessage(getString(R.string.enter_last_name),SignUpActivity.this);
            } else if (mPasswordEditText.getText().toString().isEmpty()){
                Helper.showSnackBarMessage(getString(R.string.enter_password),SignUpActivity.this);
            } else if (mPasswordConfirmEditText.getText().toString().isEmpty()){
                Helper.showSnackBarMessage(getString(R.string.enter_password_confirm),SignUpActivity.this);
            } else if (!mPasswordConfirmEditText.getText().toString().equals(mPasswordEditText.getText().toString())){
                Helper.showSnackBarMessage(getString(R.string.password_match),SignUpActivity.this);
            } else if (mMobileEditText.getText().toString().isEmpty()){
                Helper.showSnackBarMessage(getString(R.string.mobile),SignUpActivity.this);
            } else if (mEmailEditText.getText().toString().isEmpty()){
                Helper.showSnackBarMessage(getString(R.string.enter_email),SignUpActivity.this);
            } else {
                Intent intent = new Intent(SignUpActivity.this,PhoneVerificationActivity.class);
                intent.putExtra("firstName",mFirstNameEditText.getText().toString());
                intent.putExtra("lastName",mLastNameEditText.getText().toString());
                intent.putExtra("password",mPasswordEditText.getText().toString());
                intent.putExtra("email",mEmailEditText.getText().toString());
                intent.putExtra("mobile","+2" + mMobileEditText.getText().toString());
                intent.putExtra("image",mImagePath);
                startActivity(intent);
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
                mProfileImageView.setImageBitmap(bitmapImage);
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
                            mImagePath = imagePaths.get(0);
                        } else {
                            Helper.showSnackBarMessage(getString(R.string.error), SignUpActivity.this);
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
        if (SignUpActivity.this.getExternalCacheDir() != null) {
            File f = new File(SignUpActivity.this.getExternalCacheDir().getAbsolutePath() + "/" + name);
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


    public void openFirstName(View view) {
        mFirstNameEditText.setFocusableInTouchMode(true);
        mFirstNameEditText.requestFocus();
        final InputMethodManager inputMethodManager = (InputMethodManager) SignUpActivity.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(mFirstNameEditText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void openLastName(View view) {
        mLastNameEditText.setFocusableInTouchMode(true);
        mLastNameEditText.requestFocus();
        final InputMethodManager inputMethodManager = (InputMethodManager) SignUpActivity.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(mLastNameEditText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void openPassword(View view) {
        mPasswordEditText.setFocusableInTouchMode(true);
        mPasswordEditText.requestFocus();
        final InputMethodManager inputMethodManager = (InputMethodManager) SignUpActivity.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(mPasswordEditText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void openPasswordConfirm(View view) {
        mPasswordConfirmEditText.setFocusableInTouchMode(true);
        mPasswordConfirmEditText.requestFocus();
        final InputMethodManager inputMethodManager = (InputMethodManager) SignUpActivity.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(mPasswordConfirmEditText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void openPhoneNumber(View view) {
        mMobileEditText.setFocusableInTouchMode(true);
        mMobileEditText.requestFocus();
        final InputMethodManager inputMethodManager = (InputMethodManager) SignUpActivity.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(mMobileEditText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void openEmail(View view) {
        mEmailEditText.setFocusableInTouchMode(true);
        mEmailEditText.requestFocus();
        final InputMethodManager inputMethodManager = (InputMethodManager) SignUpActivity.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(mEmailEditText, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
