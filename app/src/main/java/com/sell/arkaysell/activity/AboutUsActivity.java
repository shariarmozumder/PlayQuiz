package com.sell.arkaysell.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.sell.arkaysell.R;
import com.sell.arkaysell.application.MainApplication;
import com.bluejamesbond.text.DocumentView;
import com.bluejamesbond.text.style.TextAlignment;


public class AboutUsActivity extends AppCompatActivity implements OnClickListener {
	
	private TextView txtEmail, txtFacebook, txtTwitter;
	TextView txtAppTitle;
	private Typeface tpMarkOne;
	DocumentView documentView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);

		Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
		setSupportActionBar(toolbar);
		toolbar.setTitle("");
		toolbar.setSubtitle("");
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		// Show the Up button in the action bar.
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(false);
			actionBar.setDisplayHomeAsUpEnabled(true);

		}
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

		txtAppTitle = (TextView) findViewById(R.id.txtAppTitle);
		txtAppTitle.setText(getResources().getString(R.string.about));
		txtAppTitle.setTypeface(MainApplication.getInstance().getAugustSansRegular());

		tpMarkOne = MainApplication.getInstance().getAugustSansRegular();

		txtEmail = (TextView)findViewById(R.id.txtEmail);
		txtEmail.setOnClickListener(this);
		txtFacebook = (TextView)findViewById(R.id.txtFacebook);
		txtFacebook.setOnClickListener(this);
		txtTwitter = (TextView)findViewById(R.id.txtTwitter);
		txtTwitter.setOnClickListener(this);
		txtAppTitle.setTypeface(tpMarkOne);
		txtEmail.setTypeface(tpMarkOne);
		txtFacebook.setTypeface(tpMarkOne);
		txtTwitter.setTypeface(tpMarkOne);
		documentView = addDocumentView(new StringBuilder().append(getResources().getString(R.string.about_quiz_ranking)).toString(), DocumentView.PLAIN_TEXT);
		RelativeLayout linearLayout = (RelativeLayout)findViewById(R.id.linearLayout6);
		linearLayout.removeAllViews();
		linearLayout.addView(documentView);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.txtEmail:
			Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
		            "mailto",""+getResources().getString(R.string.email_url), null));
			intent.putExtra(Intent.EXTRA_SUBJECT, "EXTRA_SUBJECT");
			startActivity(Intent.createChooser(intent, "Send email..."));
			break;
		case R.id.txtFacebook:
			Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(getResources().getString(R.string.facebook_url)));
			startActivity(browserIntent);
			break;
		case R.id.txtTwitter:
			Intent browserIntents = new Intent("android.intent.action.VIEW", Uri.parse(getResources().getString(R.string.twitter_url)));
			startActivity(browserIntents);
			break;
		}
	}

	public DocumentView addDocumentView(CharSequence article, int type) {
		return addDocumentView(article, type, true);
	}

	public DocumentView addDocumentView(CharSequence article, int type, boolean rtl) {
		final DocumentView documentView = new DocumentView(this, type);
		documentView.getDocumentLayoutParams().setTextColor(getResources().getColor(R.color.textPrimary));
		documentView.getDocumentLayoutParams().setTextTypeface(MainApplication.getInstance().getAugustSansRegular());
		documentView.getDocumentLayoutParams().setTextAlignment(TextAlignment.JUSTIFIED);
		documentView.getDocumentLayoutParams().setInsetPaddingLeft(20f);
		documentView.getDocumentLayoutParams().setInsetPaddingRight(20f);
		documentView.getDocumentLayoutParams().setInsetPaddingTop(20f);
		documentView.getDocumentLayoutParams().setInsetPaddingBottom(20f);
		documentView.getDocumentLayoutParams().setLineHeightMultiplier(1.3f);
		documentView.getDocumentLayoutParams().setReverse(rtl);
		documentView.setText(article);
		return documentView;
	}
}
