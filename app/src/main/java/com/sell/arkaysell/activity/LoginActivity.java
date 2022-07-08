package com.sell.arkaysell.activity;

import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sell.arkaysell.R;
import com.sell.arkaysell.application.MainApplication;
import com.sell.arkaysell.bean.User;
import com.sell.arkaysell.utils.Constants;
import com.sell.arkaysell.utils.LogEvent;
import com.sell.arkaysell.utils.Prefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    public Context context;
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;
    private LoginButton signInButtonFacebook;

    private FirebaseUser firebaseUser;

    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    private boolean is_google_sign_in;
    public static final int DEFAULT_TIMEOUT_MS = 30000;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private Boolean authFlag = false;

    private DatabaseReference databaseUserRegistraion;

    private User user;
    private Prefs prefs;
    private ProgressDialog mProgressDialog;
    private AppEventsLogger logger;
    boolean isNewUser = false;
    int authStateChagenCallCount = 0;
    private static Tracker mTracker;
    private CardView fb,gp;
    private TextView txtNotice,txtAppname,txtFab,txtGp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        AppEventsLogger.activateApp(this);
        logger = AppEventsLogger.newLogger(this);

        // Obtain the shared Tracker instance.
        MainApplication application = (MainApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(LogEvent.ENTER_LOGIN_SCREEN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        prefs = MainApplication.getInstance().getPrefs();

        txtNotice = (TextView) findViewById(R.id.txtNotice);
        txtAppname = (TextView) findViewById(R.id.txtAppname);
        txtGp = (TextView) findViewById(R.id.txtGp);
        txtFab = (TextView) findViewById(R.id.txtFab);

        txtNotice.setTypeface(MainApplication.getInstance().getAugustSansRegular());
        txtAppname.setTypeface(MainApplication.getInstance().getAugustSansMedium());
        txtGp.setTypeface(MainApplication.getInstance().getAugustSansMedium());
        txtFab.setTypeface(MainApplication.getInstance().getAugustSansMedium());

        fb = (CardView) findViewById(R.id.fb);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInButtonFacebook.performClick();
            }
        });

        user = new User();
        String uid = prefs.getUID();
        if (!uid.isEmpty()) {
            databaseUserRegistraion = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USER_PROFILE).child(uid);
            databaseUserRegistraion.keepSynced(true);
            databaseUserRegistraion.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                    Log.i("INFO", "User Name: " + user.getUserDisplayName());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled", databaseError.toException());
                }
            });
        }else{
            isNewUser = true;
        }
        gp = (CardView) findViewById(R.id.gp);
        gp.setOnClickListener(this);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                String fbemail = "";
                if (firebaseAuth.getCurrentUser() != null) {
                    if (authFlag == false) {
                        // Task to perform once
                        authFlag = true;
                        Log.i("INFO", "Call Auth State Change: ");

                        if (firebaseUser != null) {
                            databaseUserRegistraion = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USER_PROFILE).child(firebaseUser.getUid());

                            // Log.i("INFO","Check Child: "+);
                            databaseUserRegistraion.keepSynced(true);
                            databaseUserRegistraion.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    user = dataSnapshot.getValue(User.class);
                                    authStateChagenCallCount++;
                                    if(authStateChagenCallCount==1) {
                                        loginProcess(user);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e(TAG, "onCancelled", databaseError.toException());
                                }
                            });
                        }

                    }
                }
            }

        };

        mCallbackManager = CallbackManager.Factory.create();

        signInButtonFacebook = (LoginButton) findViewById(R.id.login__with_facebook_button);
        signInButtonFacebook.setReadPermissions("email", "public_profile");
        signInButtonFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //Log.d(TAG, "facebook:onSuccess:" + loginResult);
                prefs.setSignINGoogle(false);
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                updateUI(null);
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                logger.logEvent(LogEvent.FACEBOOK_LOGIN_FAIL);
                updateUI(null);
            }
        });
        context = this;
    }

    private void loginProcess(User user) {
        String fbemail = "";


        ///boolean isNewUser = false;
        Log.i("INFO","CHeck new User: "+isNewUser);

        if (user == null) {
            user = new User();
            isNewUser = true;
        }

        if (firebaseUser != null) {
            is_google_sign_in = prefs.getSignINGoogle();

            if (is_google_sign_in) {
                logger.logEvent(LogEvent.GOOGLE_LOGIN_SUCCESS);

                try {
                    if (user.getProfileImage() == null) {
                        user.setProfileImage(firebaseUser.getPhotoUrl().toString());
                    }
                    user.setActualPic(firebaseUser.getPhotoUrl().toString());
                } catch (NullPointerException npe) {
                }
                fbemail = firebaseUser.getEmail();
                if (fbemail == null) {
                    fbemail = "blank";
                }
                //Log.i("INFO", "EmaiL " + fbemail);
                String token = FirebaseInstanceId.getInstance().getToken();
                user.setUserID(firebaseUser.getUid());
                if (user.getUserDisplayName() == null) {
                    user.setUserDisplayName(firebaseUser.getDisplayName());
                }
                user.setEmail(fbemail);
                user.setActualName(firebaseUser.getDisplayName());

                Calendar c = Calendar.getInstance();
                user.setAccountCreateDate(c.getTimeInMillis());
                SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
                Log.i("INFO","Time: "+sdf.format(c.getTime()));

                getUserCountyAndUpdate();

                databaseUserRegistraion = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USER_PROFILE).child(firebaseUser.getUid());
                databaseUserRegistraion.setValue(user);

                prefs.setUID(firebaseUser.getUid());

                FirebaseMessaging.getInstance().subscribeToTopic("productlaunch");
                Log.d(TAG, "Subscribed to news topic");

                    Intent intent = new Intent(getApplicationContext(), MenuHomeScreenActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("uid", firebaseUser.getUid());
                    startActivity(intent);

            } else {
                logger.logEvent(LogEvent.FACEBOOK_LOGIN_SUCCESS);
                try {
                    if (user.getProfileImage() == null) {
                        user.setProfileImage(Profile.getCurrentProfile().getProfilePictureUri(200, 200).toString());

                    }
                    user.setActualPic(Profile.getCurrentProfile().getProfilePictureUri(200, 200).toString());
                } catch (NullPointerException npe) {
                }
                prefs.setUID(firebaseUser.getUid());

                user.setUserID(firebaseUser.getUid());
                if (user.getUserDisplayName() == null) {
                    user.setUserDisplayName(firebaseUser.getDisplayName());
                }
                user.setEmail(firebaseUser.getEmail());
                user.setActualName(firebaseUser.getDisplayName());

                Calendar c = Calendar.getInstance();
                user.setAccountCreateDate(c.getTimeInMillis());
                SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
                Log.i("INFO","Time: "+sdf.format(c.getTime()));

                getUserCountyAndUpdate();

                databaseUserRegistraion = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USER_PROFILE).child(firebaseUser.getUid());
                databaseUserRegistraion.keepSynced(true);
                databaseUserRegistraion.setValue(user);

                FirebaseMessaging.getInstance().subscribeToTopic("productlaunch");
                Log.d(TAG, "Subscribed to news topic");


                    Intent intent = new Intent(getApplicationContext(), MenuHomeScreenActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("uid", firebaseUser.getUid());
                    startActivity(intent);

            }

        } else {
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        showProgressDialog();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        Log.d(TAG, "signInWithCredential:onComplete:" + task);

                        // If sign in fails, display a message to the firebaseUser. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in firebaseUser can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            logger.logEvent(LogEvent.FACEBOOK_LOGIN_FAIL);
                        } else if (task.isSuccessful()) {
                            //Log.w(TAG, "signInWithCredential", task.getException());
                            //Log.i("INFO", "Login: " + task.toString());
                            Toast.makeText(LoginActivity.this, "Authentication successfully.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                updateUI(null);
            }
        }

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the firebaseUser. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in firebaseUser can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void signIn() {
        prefs.setSignINGoogle(true);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.gp:
                signIn();
                break;
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device firebaseUser's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the firebaseUser hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            this.firebaseUser = user;
        } else {
            System.out.println("firebaseUser null");
        }
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void getUserCountyAndUpdate() {

        String url = getResources().getString(R.string.get_user_county);

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    JSONObject jsonResponse;

                    @Override
                    public void onResponse(String response) {

                        try {

                            jsonResponse = new JSONObject(response);
                            System.out.println("json : " + jsonResponse.getString("countryCode"));
                            System.out.println("json : " + user.getCountryCode());
                            if (user.getCountryCode() == null) {
                                if (jsonResponse.getString("countryCode") != null) {
                                    user.setCountryCode(jsonResponse.getString("countryCode"));
                                }
                                if (jsonResponse.getString("country") != null) {
                                    user.setCountry(jsonResponse.getString("country"));
                                }
                                if (jsonResponse.getString("city") != null) {
                                    user.setCity(jsonResponse.getString("city"));
                                }
                                databaseUserRegistraion = FirebaseDatabase.getInstance().getReference("userprofile").child(firebaseUser.getUid());
                                databaseUserRegistraion.keepSynced(true);
                                databaseUserRegistraion.setValue(user);

                                getUserCountyFlag();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //showProgress(false);
                        error.printStackTrace();

                        Log.i("TIme Out", "Errot time out");

                    }

                }
        )

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                return params;
            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(postRequest).setRetryPolicy(new DefaultRetryPolicy(
                LoginActivity.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void getUserCountyFlag() {

        String url = getResources().getString(R.string.get_user_county_flag);
        url = url + "" + user.getCountryCode();

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    JSONObject jsonResponse;

                    @Override
                    public void onResponse(String response) {

                        try {

                            jsonResponse = new JSONObject(response);
                            //System.out.println("json : " + jsonResponse.getString("flag"));
                            if (jsonResponse.getString("flag") != null) {
                                user.setCountyFlagURL(jsonResponse.getString("flag"));
                            }

                            databaseUserRegistraion = FirebaseDatabase.getInstance().getReference("userprofile").child(firebaseUser.getUid());
                            databaseUserRegistraion.keepSynced(true);
                            databaseUserRegistraion.setValue(user);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                        Log.i("TIme Out", "Errot time out");

                    }

                }
        )

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                return params;
            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(postRequest).setRetryPolicy(new DefaultRetryPolicy(
                LoginActivity.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}

