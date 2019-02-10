package com.waslabank.waslabank.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.waslabank.waslabank.models.UserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Abdelrahman Hesham on 3/9/2018.
 */

public class Helper {

    public static void showLongTimeToast(Context context, String message) {

        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

    }

    public static void writeToLog(String message) {

        Log.i("Helper Log", message);

    }


    public static boolean validateEmail(String email) {

        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }

    public static boolean validateFields(String name) {

        return !TextUtils.isEmpty(name);
    }

    public static boolean validateMobile(String string) {

        return !(TextUtils.isEmpty(string) || string.length() != 10);
    }

    public static void showSnackBarMessage(String message, AppCompatActivity activity) {

        if (activity.findViewById(android.R.id.content) != null) {

            Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
        }
    }

    public static void hideKeyboard(AppCompatActivity activity, View v){
        View currentFocus = activity.getCurrentFocus();
        if (currentFocus != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }


    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static void saveUserToSharedPreferences(Context context, UserModel userModel){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("id",userModel.getId());
        editor.putString("email",userModel.getUsername());
        editor.putString("mobile",userModel.getMobile());
        editor.putString("name",userModel.getName());
        editor.putString("role", userModel.getRole());
        editor.putString("country",userModel.getCountry());
        editor.putString("city",userModel.getCityId());
        editor.putString("gender",userModel.getGender());
        editor.putString("image",userModel.getImage());
        editor.putString("latitude",userModel.getLatitude());
        editor.putString("longitude",userModel.getLongitude());
        editor.putInt("orders",userModel.getOrders());
        editor.putInt("comments",userModel.getComments());
        editor.putString("password",userModel.getPassword());
        editor.putString("rating",userModel.getRating());
        editor.putString("status",userModel.getStatus());
        editor.putString("car_name",userModel.getCarName());
        editor.putString("ref_id",userModel.getRefId());
        editor.putString("credit",userModel.getCredit());
        editor.apply();
    }


    public static void saveTokenToSharePreferences(Context context, String token){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

    public static void saveStatusToSharePreferences(Context context, String status){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("status", status);
        editor.apply();
    }

    public static String getTokenFromSharedPreferences(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("token","null");
    }


    public static void removeUserFromSharedPreferences(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("id");
        editor.remove("email");
        editor.remove("mobile");
        editor.remove("name");
        editor.remove("role");
        editor.remove("gender");
        editor.remove("image");
        editor.remove("latitude");
        editor.remove("longitude");
        editor.remove("password");
        editor.remove("city");
        editor.remove("orders");
        editor.remove("comments");
        editor.remove("country");
        editor.remove("rating");
        editor.remove("status");
        editor.remove("car_name");
        editor.remove("ref_id");
        editor.remove("credit");
        editor.apply();
    }

    public static boolean preferencesContainsUser(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.contains("id");
    }

    public static UserModel getUserSharedPreferences(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String id = preferences.getString("id",null);
        String email = preferences.getString("email",null);
        String mobile = preferences.getString("mobile",null);
        String name = preferences.getString("name",null);
        String role = preferences.getString("role",null);
        String city = preferences.getString("city",null);
        int comment = preferences.getInt("comments",-1);
        String country = preferences.getString("country",null);
        String gender = preferences.getString("gender",null);
        String image = preferences.getString("image",null);
        String latitude = preferences.getString("latitude",null);
        String longitude = preferences.getString("longitude",null);
        int orders = preferences.getInt("orders",-1);
        String password = preferences.getString("password",null);
        String token = preferences.getString("token",null);
        String rating = preferences.getString("rating",null);
        String status = preferences.getString("status",null);
        String carName = preferences.getString("car_name",null);
        String refId = preferences.getString("ref_id",null);
        String credit = preferences.getString("credit",null);
        return new UserModel(name,email,token,password,mobile,longitude,latitude,city,country,gender,image,role,id,orders,comment,rating,status,carName,refId,credit);
    }


    public static ProgressDialog showProgressDialog(Context context, String bodyText, boolean cancelable) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(cancelable);
        progressDialog.setMessage(bodyText);
        progressDialog.show();
        return progressDialog;
    }


    public static void showAlertDialog(Context context, String body, String title, boolean cancelable, String positiveText, String negativeText, DialogInterface.OnClickListener yesListener, DialogInterface.OnClickListener NoListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setCancelable(cancelable).setMessage(body);
        if (!positiveText.isEmpty()) {
            builder.setPositiveButton(positiveText, yesListener);
        }
        if (!negativeText.isEmpty()) {
            builder.setNegativeButton(negativeText, NoListener);
        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }




}
