package com.sell.arkaysell.fragment;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sell.arkaysell.R;
import com.sell.arkaysell.activity.LoginActivity;
import com.sell.arkaysell.application.MainApplication;
import com.sell.arkaysell.bean.GameData;
import com.sell.arkaysell.bean.User;
import com.sell.arkaysell.customviews.CircularImageView;
import com.sell.arkaysell.utils.Constants;
import com.sell.arkaysell.utils.Prefs;
import com.squareup.picasso.Picasso;

/**
 * Created by INDIA on 28-01-2017.
 */

public class FragmentProfile extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = FragmentProfile.class.getSimpleName();
    private Listener mListener = null;
    private TextView txtScore, txt, txt3, txtUname, txtEmailID;
    private ImageView image_county;
    private View view;
    private String url = "";
    private String countyImageUrl = "";
    private CircularImageView circularImageView;
    private Button btnSignOut, btnCancle;
    private Typeface tp;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference scoreBoardDataRef;
    private GameData gameScore = new GameData();
    private User user;
    private Prefs prefs;

    public static FragmentProfile newInstance(Bundle bundle) {
        FragmentProfile fragment = new FragmentProfile();
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setListener(Listener l) {
        mListener = l;
    }


    public interface Listener {
        public User getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getContext());
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view = getView();

        prefs = MainApplication.getInstance().getPrefs();
        tp = MainApplication.getInstance().getAugustSansRegular();
        user = mListener.getUser();

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.detail_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPressed();
            }
        });
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                //.enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();

        // Show the Up button in the action bar.
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);

        }
        toolbar.setTitle("");


        txt = (TextView) view.findViewById(R.id.txtAppTitle);
        txt3 = (TextView) view.findViewById(R.id.txt3);
        txtUname = (TextView) view.findViewById(R.id.txtUname);
        txt.setText("My Profile");


        url = user.getProfileImage();
        countyImageUrl = user.getCountyFlagURL();

        String uid = prefs.getUID();

        scoreBoardDataRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_GAME_SCORE).child(uid);

        scoreBoardDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                gameScore = dataSnapshot.getValue(GameData.class);
                if (gameScore != null) {
                    Log.i("INF", "Score: " + gameScore.getTotalScore());
                    txtScore.setText("" + gameScore.getTotalScore());
                } else {
                    txtScore.setText("0");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });


        btnSignOut = (Button) view.findViewById(R.id.btnSignOut);
        btnCancle = (Button) view.findViewById(R.id.btnCancle);

        circularImageView = (CircularImageView) view.findViewById(R.id.imageView);
        txtScore = (TextView) view.findViewById(R.id.txtScore);
        txtEmailID = (TextView) view.findViewById(R.id.txtEmailID);
        image_county = (ImageView) view.findViewById(R.id.image_county);


        txtEmailID.setText(user.getEmail());
        txtUname.setText(user.getUserDisplayName());

        if (url != null) {
            if (!url.isEmpty()) {
                Picasso.with(getActivity()).load(url).into(circularImageView);
            }
        }

        if (countyImageUrl != null) {
            if (!countyImageUrl.isEmpty()) {
                Log.i("INFO", "" + countyImageUrl);
                Picasso.with(getActivity()).load(countyImageUrl).resize(125, 83).into(image_county);
            }
        }

        txtScore.setTypeface(tp);
        txt.setTypeface(tp);
        btnSignOut.setTypeface(tp);
        btnCancle.setTypeface(tp);
        txt3.setTypeface(tp);
        txtUname.setTypeface(tp);
        txtEmailID.setTypeface(tp);

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPressed();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void signOut() {

        LoginManager.getInstance().logOut();


        prefs.setUID("");
        prefs.setToken("");

        FirebaseAuth.getInstance().signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        System.out.println("status : " + status);
                    }
                });

        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not be available.
        Log.d("mainActivity", "onConnectionFailed:" + connectionResult);
    }

    public void backPressed(){
        getActivity().onBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
}
