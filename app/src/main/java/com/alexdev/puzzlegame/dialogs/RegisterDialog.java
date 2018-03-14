package com.alexdev.puzzlegame.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.alexdev.puzzlegame.R;
import com.alexdev.puzzlegame.api.ApiUtils;
import com.alexdev.puzzlegame.api.UserApi;
import com.alexdev.puzzlegame.models.UserPost;
import com.alexdev.puzzlegame.models.UserResponse;
import com.alexdev.puzzlegame.utils.Fonts;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by alexdev on 12/03/18.
 */

public class RegisterDialog extends Dialog{

    private Activity activity;
    private Dialog dialog;
    private Button mCloseButton;
    private Button mSaveButton;
    private EditText mUsernameEdit;
    private EditText mPasswordEdit;
    private EditText mPasswordRepEdit;
    private EditText mEmailEdit;
    private Spinner  mGenederSpinner;
    private String typeGender;
    private UserApi userApi;

    public RegisterDialog(Activity a)
    {
        super(a);
        activity = a;
        dialog = this;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_register);
        userApi = ApiUtils.getUserAPI();
        InitViews();
        InitEventsViews();
    }

    private void InitViews()
    {
        mCloseButton = findViewById(R.id.closeButton);
        mCloseButton.setTypeface(Fonts.getInstance().getPoiretOneRegular(activity.getApplicationContext()));
        mSaveButton = findViewById(R.id.saveButton);
        mSaveButton.setTypeface(Fonts.getInstance().getPoiretOneRegular(activity.getApplicationContext()));
        mUsernameEdit = findViewById(R.id.userEditText);
        mUsernameEdit.setTypeface(Fonts.getInstance().getPoiretOneRegular(activity.getApplicationContext()));
        mEmailEdit = findViewById(R.id.mailEditText);
        mEmailEdit.setTypeface(Fonts.getInstance().getPoiretOneRegular(activity.getApplicationContext()));
        mPasswordEdit = findViewById(R.id.passEditText);
        mPasswordEdit.setTypeface(Fonts.getInstance().getPoiretOneRegular(activity.getApplicationContext()));
        mPasswordRepEdit = findViewById(R.id.passRepeatEditText);
        mPasswordRepEdit.setTypeface(Fonts.getInstance().getPoiretOneRegular(activity.getApplicationContext()));
        mGenederSpinner = findViewById(R.id.genderSpinner);
    }

    private void InitEventsViews()
    {
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInformation();
            }
        });
        mGenederSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                typeGender = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void saveInformation()
    {
        SweetAlertDialog pDialog = new SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
        if(mUsernameEdit.getText().toString().equals("")) {mUsernameEdit.setError("Required username");pDialog.cancel(); return;}
        if(mEmailEdit.getText().toString().equals("")){ mEmailEdit.setError("Required email");pDialog.cancel(); return;}
        if(mPasswordEdit.getText().toString().equals("")) {mPasswordEdit.setError("Required password");pDialog.cancel(); return;}
        if(mPasswordRepEdit.getText().toString().equals("")) {mPasswordRepEdit.setError("Required repeat password");pDialog.cancel(); return;}
        if(!mPasswordEdit.getText().toString().equals(mPasswordRepEdit.getText().toString()))
        {
            //pDialog.cancel();
            Log.d("compare","sep");
                    new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Passwords are not the same!")
                            .show();
            pDialog.cancel();
                    return;
        }
        UserPost user = new UserPost();
        user.setEmail(mEmailEdit.getText().toString());
        user.setUsername(mUsernameEdit.getText().toString());
        user.setPassword(mPasswordEdit.getText().toString());
        user.setGender(typeGender);
        userApi.registerUser(user).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Good job "+response.body().getUser().getUsername()+"!")
                        .setContentText("Log In!")
                        .show();
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("error response", t.getMessage());
            }
        });
        pDialog.cancel();
        dismiss();
    }

}
