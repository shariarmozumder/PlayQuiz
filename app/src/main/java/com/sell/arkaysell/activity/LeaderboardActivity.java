package com.sell.arkaysell.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sell.arkaysell.R;
import com.sell.arkaysell.adapter.LeaderboardAdapterNew;
import com.sell.arkaysell.application.MainApplication;
import com.sell.arkaysell.bean.CountyCode;
import com.sell.arkaysell.bean.Leaderboard;
import com.sell.arkaysell.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by India on 06-04-2017.
 */

public class LeaderboardActivity extends AppCompatActivity {

    private ImageView img_back, country1, country2, country3;
    private ListView listview;
    private LeaderboardAdapterNew leaderboardAdapterNew;
    private ArrayList<Leaderboard> leaderboards;
    private Typeface tp;
    private TextView txt1, txt2, txt3, txtUname1, txtUname2, txtUname3, txtScore1, txtScore2, txtScore3;
    private ImageView img1, img2, img3;
    private DatabaseReference databaseLeaderboard;
    long totalValue = 0;

    private String displayName = "";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_leaderboard);
        tp = MainApplication.getInstance().getAugustSansRegular();

        img_back = (ImageView) findViewById(R.id.img_back);

        listview = (ListView) findViewById(R.id.listview);

        txt1 = (TextView) findViewById(R.id.txt1);
        txt2 = (TextView) findViewById(R.id.txt2);
        txt3 = (TextView) findViewById(R.id.txt3);

        txtUname1 = (TextView) findViewById(R.id.txtUname1);
        txtUname2 = (TextView) findViewById(R.id.txtUname2);
        txtUname3 = (TextView) findViewById(R.id.txtUname3);

        txtScore1 = (TextView) findViewById(R.id.txtScore1);
        txtScore2 = (TextView) findViewById(R.id.txtScore2);
        txtScore3 = (TextView) findViewById(R.id.txtScore3);

        country1 = (ImageView) findViewById(R.id.country1);
        country2 = (ImageView) findViewById(R.id.country2);
        country3 = (ImageView) findViewById(R.id.country3);

        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);
        img3 = (ImageView) findViewById(R.id.img3);

        txt1.setTypeface(tp);
        txt2.setTypeface(tp);
        txt3.setTypeface(tp);
        txtUname1.setTypeface(tp);
        txtUname2.setTypeface(tp);
        txtUname3.setTypeface(tp);
        txtScore1.setTypeface(tp);
        txtScore2.setTypeface(tp);
        txtScore3.setTypeface(tp);

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        leaderboards = new ArrayList<>();
        displayName = getIntent().getStringExtra("name");

        leaderboardAdapterNew = new LeaderboardAdapterNew(this, leaderboards, displayName);
        listview.setAdapter(leaderboardAdapterNew);


        databaseLeaderboard = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_LEADERBOARD);
        databaseLeaderboard.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("We're done loading the initial " + dataSnapshot.getChildrenCount() + " items");
                totalValue = dataSnapshot.getChildrenCount();
                if (totalValue >= 1000) {
                    totalValue = 1000;
                }
                getLeaderboard();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && (listview.getLastVisiblePosition() - listview.getHeaderViewsCount() -
                        listview.getFooterViewsCount()) >= (listview.getCount() - 1)) {
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });



    }

    private void getLeaderboard() {

        databaseLeaderboard.orderByChild("score").limitToLast((int) totalValue).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Leaderboard leaderboard = dataSnapshot.getValue(Leaderboard.class);
                leaderboards.add(leaderboard);

                if (leaderboards.size() >= (int) totalValue) {
                    Collections.reverse(leaderboards);
                    leaderboardAdapterNew.notifyDataSetChanged();
                }

                if (leaderboards.size() == totalValue) {


                    if (leaderboards.get(0).getName().equalsIgnoreCase(displayName)) {
                        txtUname1.setText("Me");
                    } else {
                        txtUname1.setText(leaderboards.get(0).getName());
                    }
                    if (leaderboards.get(1).getName().equalsIgnoreCase(displayName)) {
                        txtUname2.setText("Me");
                    } else {
                        txtUname2.setText(leaderboards.get(1).getName());
                    }
                    if (leaderboards.get(2).getName().equalsIgnoreCase(displayName)) {
                        txtUname3.setText("Me");
                    } else {
                        txtUname3.setText(leaderboards.get(2).getName());
                    }

                    String formattedNumber = String.format("%,d", leaderboards.get(0).getScore());
                    txtScore1.setText("" + formattedNumber);

                    formattedNumber = String.format("%,d", leaderboards.get(1).getScore());
                    txtScore2.setText("" + formattedNumber);

                    formattedNumber = String.format("%,d", leaderboards.get(2).getScore());
                    txtScore3.setText("" + formattedNumber);


                    if (!leaderboards.get(0).getProfilePic().isEmpty()) {
                        Picasso.with(getApplicationContext())
                                .load(leaderboards.get(0).getProfilePic())
                                .resize((int) getResources().getDimension(R.dimen.fourty_dimen), (int) getResources().getDimension(R.dimen.fourty_dimen))
                                .onlyScaleDown()
                                .centerInside()
                                .into(img1);
                    }
                    if (!leaderboards.get(1).getProfilePic().isEmpty()) {
                        Picasso.with(getApplicationContext())
                                .load(leaderboards.get(1).getProfilePic())
                                .resize((int) getResources().getDimension(R.dimen.fourty_dimen), (int) getResources().getDimension(R.dimen.fourty_dimen))
                                .onlyScaleDown()
                                .centerInside()
                                .into(img2);
                    }
                    if (!leaderboards.get(2).getProfilePic().isEmpty()) {

                        Picasso.with(getApplicationContext())
                                .load(leaderboards.get(2).getProfilePic())
                                .resize((int) getResources().getDimension(R.dimen.fourty_dimen), (int) getResources().getDimension(R.dimen.fourty_dimen))
                                .onlyScaleDown()
                                .centerInside()
                                .into(img3);
                    }
                    if (leaderboards.get(0).getCountryCode() != null) {
                        if (!leaderboards.get(0).getCountryCode().isEmpty()) {
                            //Log.i("InFO", "CODL: " + leaderboards.get(0).getCountryCode());
                            Picasso.with(getApplicationContext())
                                    .load((String) CountyCode.countryCode.get(leaderboards.get(0).getCountryCode()))
                                    .resize((int) getResources().getDimension(R.dimen.twenty_margin), (int) getResources().getDimension(R.dimen.twenty_margin))
                                    .onlyScaleDown()
                                    .centerInside()
                                    .into(country1);
                        }
                    }
                    if (leaderboards.get(1).getCountryCode() != null) {
                        if (!leaderboards.get(1).getCountryCode().isEmpty()) {
                            Picasso.with(getApplicationContext())
                                    .load((String) CountyCode.countryCode.get(leaderboards.get(1).getCountryCode()))
                                    .resize((int) getResources().getDimension(R.dimen.twenty_margin), (int) getResources().getDimension(R.dimen.twenty_margin))
                                    .onlyScaleDown()
                                    .centerInside()
                                    .into(country2);
                        }
                    }
                    if (leaderboards.get(2).getCountryCode() != null) {
                        if (!leaderboards.get(2).getCountryCode().isEmpty()) {
                            Picasso.with(getApplicationContext())
                                    .load((String) CountyCode.countryCode.get(leaderboards.get(2).getCountryCode()))
                                    .resize((int) getResources().getDimension(R.dimen.twenty_margin), (int) getResources().getDimension(R.dimen.twenty_margin))
                                    .onlyScaleDown()
                                    .centerInside()
                                    .into(country3);
                        }
                    }

                    leaderboardAdapterNew.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
