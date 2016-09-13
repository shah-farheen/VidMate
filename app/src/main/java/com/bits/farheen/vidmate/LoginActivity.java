package com.bits.farheen.vidmate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private LoginButton facebookLoginButton;
    private CallbackManager callbackManager;
    private Profile profile;
    private ProfileTracker profileTracker;
    private AccessTokenTracker accessTokenTracker;

    private SignInButton googleLoginButton;
    private GoogleApiClient googleApiClient;

    private Context mContext;
    private SharedPreferences dataFile;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        dataFile = getSharedPreferences(Constants.dataFile, MODE_APPEND);

        if(isLoggedIn()){
            startActivity(new Intent(mContext, MainActivity.class));
            finish();
        }

        googleLoginButton = (SignInButton) findViewById(R.id.google_login_button);
        facebookLoginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);
        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            }
        };
        accessTokenTracker.startTracking();

        facebookLoginButton.setReadPermissions("public_profile");
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if(Profile.getCurrentProfile() == null){
                    profileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                            profile = currentProfile;
                            profileTracker.stopTracking();
                            completeFacebookLogin(profile);
                        }
                    };
                }
                else {
                    profile = Profile.getCurrentProfile();
                    completeFacebookLogin(profile);
                }
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Login Cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "onError: " + error);
            }
        });


        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN), new Scope(Scopes.PROFILE))
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        googleLoginButton.setSize(SignInButton.SIZE_STANDARD);
        googleLoginButton.setScopes(googleSignInOptions.getScopeArray());
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGoogleLogin();
            }
        });
    }

    private void completeFacebookLogin(Profile userProfile){
        dataFile.edit().putString(Constants.userId, userProfile.getId())
                .putString(Constants.userName, userProfile.getName())
                .putString(Constants.dpUrl, userProfile.getProfilePictureUri(200, 200).toString())
                .putBoolean(Constants.isLoggedIn, true)
                .apply();

        Intent mainIntent = new Intent(mContext, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void startGoogleLogin(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, Constants.googleSignInRequestCode);
    }

    private void completeGoogleLogin(GoogleSignInResult result){
        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            assert account != null;
            String dpUrl = null;
            if(account.getPhotoUrl() != null){
                dpUrl = account.getPhotoUrl().toString();
            }
            dataFile.edit().putString(Constants.userId, account.getId())
                    .putString(Constants.userName, account.getDisplayName())
                    .putString(Constants.dpUrl, dpUrl)
                    .putBoolean(Constants.isLoggedIn, true)
                    .apply();

            Intent mainIntent = new Intent(mContext, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.googleSignInRequestCode){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            completeGoogleLogin(result);
        }
        else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private boolean isLoggedIn() {
        return dataFile.getBoolean(Constants.isLoggedIn, false);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: " + connectionResult);
        Toast.makeText(getApplicationContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
    }
}
