package com.sell.arkaysell.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sell.arkaysell.R;
import com.sell.arkaysell.application.MainApplication;
import com.bluejamesbond.text.DocumentView;
import com.bluejamesbond.text.style.TextAlignment;

public class HowToPlayActivity extends AppCompatActivity {

	TextView txtAppTitle;
	DocumentView documentView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_how_to_play);

		Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
		setSupportActionBar(toolbar);

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
		toolbar.setTitle("");
		txtAppTitle = (TextView) findViewById(R.id.txtAppTitle);
		txtAppTitle.setText(getResources().getString(R.string.how_to_play_txt));
		txtAppTitle.setTypeface(MainApplication.getInstance().getAugustSansRegular());
		documentView = addDocumentView(new StringBuilder().append(getResources().getString(R.string.how_to_play)).toString(), DocumentView.PLAIN_TEXT);
		RelativeLayout linearLayout = (RelativeLayout)findViewById(R.id.linearLayout1);
		linearLayout.removeAllViews();
		linearLayout.addView(documentView);
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