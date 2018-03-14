package com.alexdev.puzzlegame.activities.session;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alexdev.puzzlegame.BuildConfig;
import com.alexdev.puzzlegame.R;
import com.alexdev.puzzlegame.api.ApiUtils;
import com.alexdev.puzzlegame.api.UserApi;
import com.alexdev.puzzlegame.dialogs.RegisterDialog;
import com.alexdev.puzzlegame.manager.NavigatorManager;
import com.alexdev.puzzlegame.manager.SessionManager;
import com.alexdev.puzzlegame.models.UserResponse;
import com.alexdev.puzzlegame.utils.Fonts;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreenActivity extends AppCompatActivity {

    private static final long SPLASH_SCREEN_DELAY = 2000;

    private TextView    mPuzzleTextView;
    private TextView    mGameTextView;
    private TextView    mAuthorTextView;
    private ProgressBar mProgressbar;
    private TextView    mLoadTextView;
    private EditText    mUserEditText;
    private EditText    mPassEditText;
    private ConstraintLayout mLoadingConstraintLayout;
    private ConstraintLayout mLoginConstraintLayout;
    private Button mLoginButton;
    private Button mSingUpButton;
    private Activity activity;
    private UserApi userApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        activity = this;
        userApi = ApiUtils.getUserAPI();
        initViews();
        initEventsViews();
        loadSession();
    }

    private void initViews(){

        mPuzzleTextView = findViewById(R.id.puzzleTextView);
        mGameTextView   = findViewById(R.id.gameTextView);
        mAuthorTextView = findViewById(R.id.authorTextView);
        mProgressbar    = findViewById(R.id.progressBar);
        mLoadTextView   = findViewById(R.id.loadTextView);
        mUserEditText   = findViewById(R.id.userEditText);
        mPassEditText   = findViewById(R.id.passEditText);
        mLoginButton    = findViewById(R.id.loginButton);
        mSingUpButton   = findViewById(R.id.singUpButton);
        mLoadingConstraintLayout = findViewById(R.id.loadingConstraintLayout);
        mLoginConstraintLayout   = findViewById(R.id.loginConstraintLayout);

        mPuzzleTextView.setTypeface(Fonts.getInstance().getPoiretOneRegular(getApplicationContext()));
        mGameTextView.setTypeface(Fonts.getInstance().getPoiretOneRegular(getApplicationContext()));
        mAuthorTextView.setTypeface(Fonts.getInstance().getPoiretOneRegular(getApplicationContext()));
        mLoadTextView.setTypeface(Fonts.getInstance().getPoiretOneRegular(getApplicationContext()));
        mUserEditText.setTypeface(Fonts.getInstance().getPoiretOneRegular(getApplicationContext()));
        mPassEditText.setTypeface(Fonts.getInstance().getPoiretOneRegular(getApplicationContext()));
        mLoginButton.setTypeface(Fonts.getInstance().getPoiretOneRegular(getApplicationContext()));
        mSingUpButton.setTypeface(Fonts.getInstance().getPoiretOneRegular(getApplicationContext()));

    }

    private void initEventsViews(){
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        mSingUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singUp();
            }
        });
    }

    private void singUp()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RegisterDialog dialog = new RegisterDialog(activity);
                dialog.show();
            }
        });
    }

    private void login(){

        final SweetAlertDialog pDialog = new SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
        //if (BuildConfig.DEBUG) {
            // do something for a debug build
          //  mUserEditText.setText("dev");
            //mPassEditText.setText("123");
        //}

        if(mUserEditText.getText().toString().equals("")) {
            mUserEditText.setError("The username is necessary");
            pDialog.cancel();
            return;
        }
        if(mPassEditText.getText().toString().equals("")) {
            mPassEditText.setError("The password is necessary");
            pDialog.cancel();
            return;
        }

        userApi.newSession(mUserEditText.getText().toString(),mPassEditText.getText().toString()).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                try{
                    if(response.body().getSuccess())
                    {
                        SessionManager sessionManager = new SessionManager(response.body().getUser(),activity);
                        pDialog.cancel();
                        NavigatorManager.getInstance().goHomeActivity(activity);
                    }
                    else {
                        new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...Error")
                                .setContentText("Username or Password!")
                                .show();
                        pDialog.cancel();
                    }
                }catch (Exception ex) {
                    new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...Error")
                            .setContentText("Username or Password!")
                            .show();
                    pDialog.cancel();
                }

            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...Error")
                        .setContentText("Username or Password!")
                        .show();
                pDialog.cancel();
            }
        });

    }

    private void loadSession()
    {
        mLoadTextView.setText(R.string.loading_session);
        SharedPreferences sharedpreferences = getSharedPreferences(SessionManager.REFERENCE, Context.MODE_PRIVATE);
        String username = sharedpreferences.getString("nameKey","no-username");
        if(username.equals("no-username")) {
            hideLoadOpenLogin();
        }else{
            NavigatorManager.getInstance().goHomeActivity(this);
        }
    }

    private void hideLoadOpenLogin(){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Animation animationLoading = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
                animationLoading.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mLoadingConstraintLayout.setVisibility(View.GONE);
                        Animation animationLogin = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
                        mLoginConstraintLayout.startAnimation(animationLogin);
                        mLoginConstraintLayout.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                mLoadingConstraintLayout.startAnimation(animationLoading);
            }
        });

    }

}
