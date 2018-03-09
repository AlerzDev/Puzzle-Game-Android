package com.alexdev.puzzlegame.activities.session;

import android.content.Intent;
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
import com.alexdev.puzzlegame.manager.NavigatorManager;
import com.alexdev.puzzlegame.utils.Fonts;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SplashScreenActivity extends AppCompatActivity {

    private static final long SPLASH_SCREEN_DELAY = 3000;

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
    private LoginButton mSingUpButton;
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        initViews();
        initEventsViews();
        loadSession();
        singUp();
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
        mSingUpButton.setReadPermissions("email");
        mLoadingConstraintLayout = findViewById(R.id.loadingConstraintLayout);
        mLoginConstraintLayout   = findViewById(R.id.loginConstraintLayout);

        mPuzzleTextView.setTypeface(Fonts.getInstance().getPoiretOneRegular(getApplicationContext()));
        mGameTextView.setTypeface(Fonts.getInstance().getPoiretOneRegular(getApplicationContext()));
        mAuthorTextView.setTypeface(Fonts.getInstance().getPoiretOneRegular(getApplicationContext()));
        mLoadTextView.setTypeface(Fonts.getInstance().getPoiretOneRegular(getApplicationContext()));
        mUserEditText.setTypeface(Fonts.getInstance().getPoiretOneRegular(getApplicationContext()));
        mPassEditText.setTypeface(Fonts.getInstance().getPoiretOneRegular(getApplicationContext()));
        mLoginButton.setTypeface(Fonts.getInstance().getPoiretOneRegular(getApplicationContext()));

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

            }
        });
    }

    private void singUp()
    {
        callbackManager = CallbackManager.Factory.create();
        mSingUpButton.setReadPermissions(Arrays.asList("user_status"));
        mSingUpButton.setReadPermissions(Arrays.asList("email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                    Profile oldProfile,
                    Profile currentProfile) {
                // App code

            }
        };
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using 
                // currentAccessToken when it's loaded or set.
            }
        };
        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
    }

    private void login(){

        if (BuildConfig.DEBUG) {
            // do something for a debug build
            mUserEditText.setText("alex");
            mPassEditText.setText("123");
        }

        if(mUserEditText.getText().toString().equals("")) {
            mUserEditText.setError("The username is necessary");
            return;
        }
        if(mPassEditText.getText().toString().equals("")) {
            mPassEditText.setError("The password is necessary");
            return;
        }
        if(mPassEditText.getText().toString().equals("123") && mUserEditText.getText().toString().equals("alex"))
        {
            SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);
            pDialog.setTitleText("Loading");
            pDialog.setCancelable(false);
            pDialog.show();
            NavigatorManager.getInstance().goHomeActivity(this);
            pDialog.cancel();
        }
        else {
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...Error")
                    .setContentText("Username or Password!")
                    .show();
        }
    }

    private void loadSession()
    {
        mLoadTextView.setText(R.string.loading_session);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                hideLoadOpenLogin();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, SPLASH_SCREEN_DELAY);
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
