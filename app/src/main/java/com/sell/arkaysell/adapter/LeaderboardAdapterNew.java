package com.sell.arkaysell.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sell.arkaysell.R;
import com.sell.arkaysell.application.MainApplication;
import com.sell.arkaysell.bean.CountyCode;
import com.sell.arkaysell.bean.Leaderboard;
import com.sell.arkaysell.customviews.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by arkayapps on 11/04/17.
 */

public class LeaderboardAdapterNew extends BaseAdapter {
    private Activity activity;
    private List<Leaderboard> leaderboards;
    private Typeface tp;
    private TextView txtName, txtScore, txtRank;
    private CircularImageView imgUser;
    private ImageView imgCountyFlag;
    private String userName = "";

    public LeaderboardAdapterNew(Activity activity, List<Leaderboard> leaderboards, String userName) {
        this.activity = activity;
        this.leaderboards = leaderboards;
        this.userName = userName;
    }

    @Override
    public int getCount() {
        return leaderboards.size();
    }

    @Override
    public Leaderboard getItem(int location) {
        return leaderboards.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View v;
        if (convertView == null) {
            LayoutInflater li = activity.getLayoutInflater();
            v = li.inflate(R.layout.single_leader, null);
        } else {
            v = convertView;
        }

        tp = MainApplication.getInstance().getAugustSansRegular();

        txtScore = (TextView) v.findViewById(R.id.txtScore);
        txtName = (TextView) v.findViewById(R.id.txtName);
        txtRank = (TextView) v.findViewById(R.id.txtRank);

        imgCountyFlag = (ImageView) v.findViewById(R.id.imgCountyFlag);


        for (int i = 1; i < leaderboards.size(); i++) {
            int j = position + 1;
            txtRank.setText("" + j);
        }
        txtScore.setTypeface(tp);
        txtName.setTypeface(tp);
        txtRank.setTypeface(tp);

        txtScore.setText("" + leaderboards.get(position).getScore());

        if(userName!=null) {
            if(leaderboards.get(position).getName()!=null) {
                if (leaderboards.get(position).getName().equalsIgnoreCase(userName)) {
                    txtName.setText("Me");
                } else {
                    txtName.setText("" + leaderboards.get(position).getName());
                }
            }else{
                txtName.setText("Not Available");
            }
        }else{
            txtName.setText("" + leaderboards.get(position).getName());
        }

        imgUser = (CircularImageView) v.findViewById(R.id.imgUser);
        if (!leaderboards.get(position).getProfilePic().isEmpty()) {
            Picasso.with(activity)
                    .load(leaderboards.get(position).getProfilePic())
                    .resize((int) activity.getResources().getDimension(R.dimen.fourty_dimen), (int) activity.getResources().getDimension(R.dimen.fourty_dimen))
                    .onlyScaleDown()
                    .centerInside()
                    .into(imgUser);
        }

        imgCountyFlag = (ImageView) v.findViewById(R.id.imgCountyFlag);
        if (leaderboards.get(position).getCountryCode() != null && !leaderboards.get(position).getCountryCode().isEmpty()) {
            Picasso.with(activity)
                    .load((String) CountyCode.countryCode.get(leaderboards.get(position).getCountryCode()))
                    .resize((int) activity.getResources().getDimension(R.dimen.fourty_dimen), (int) activity.getResources().getDimension(R.dimen.fourty_dimen))
                    .onlyScaleDown()
                    .centerInside()
                    .into(imgCountyFlag);
        }

        return v;
    }
}