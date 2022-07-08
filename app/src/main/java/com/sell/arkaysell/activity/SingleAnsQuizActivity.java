package com.sell.arkaysell.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.sell.arkaysell.R;
import com.sell.arkaysell.application.MainApplication;
import com.sell.arkaysell.bean.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Single Answare activtiy that display question and next, prev button and show next button. 
 * @author Arkay
 *
 */
public class SingleAnsQuizActivity extends AppCompatActivity implements OnClickListener, OnTouchListener {
	private float x1 = 0, x3=0;
	private float moveCount;


	private ImageButton btnNext, btnPrev,  btnJump;
	private TextView txtQuestion, txtAnsware, txtQuestionNO,lblAnsware;
	private int currentQuestion=0;
	private int totalQuestion=0;
	AdView adView;
	private CardView btnShowAnsware;
	private InterstitialAd interstitial;
	private List<Question> playQuizquestions =null;
	 boolean isTestMode= false;
	int levelNo=1;
	String level_name;
	RelativeLayout playQuizmainLayout;
	private RelativeLayout relBack;

	private TextView txtLevel,txtAns;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_quiz);

		txtLevel = (TextView) findViewById(R.id.txtLevel);
		txtAns = (TextView) findViewById(R.id.txtAns);
		txtLevel.setTypeface(MainApplication.getInstance().getAugustSansRegular());



		txtQuestion = (TextView)findViewById(R.id.txtQuestion);
		txtAnsware = (TextView)findViewById(R.id.txtAnsware);
		txtQuestionNO = (TextView)findViewById(R.id.txtQuestionNO);
		lblAnsware = (TextView)findViewById(R.id.lblAnsware);

		btnNext = (ImageButton)findViewById(R.id.btnNext);
		btnNext.setOnClickListener(this);
		btnPrev = (ImageButton)findViewById(R.id.btnPrev);
		btnPrev.setOnClickListener(this);
		btnShowAnsware = (CardView) findViewById(R.id.btnShowAnsware);
		btnShowAnsware.setOnClickListener(this);

		btnJump = (ImageButton)findViewById(R.id.btnJump);
		relBack = (RelativeLayout) findViewById(R.id.relBack);
		btnJump.setOnClickListener(this);
		relBack.setOnClickListener(this);

		playQuizmainLayout = (RelativeLayout)findViewById(R.id.playQuizmainLayout);

		txtQuestionNO.setTypeface(MainApplication.getInstance().getAugustSansRegular());
		txtQuestion.setTypeface(MainApplication.getInstance().getAugustSansRegular());
		lblAnsware.setTypeface(MainApplication.getInstance().getAugustSansMedium());
		txtAnsware.setTypeface(MainApplication.getInstance().getAugustSansRegular());
		txtAns.setTypeface(MainApplication.getInstance().getAugustSansMedium());

		levelNo = getIntent().getIntExtra("level_no",0);
		level_name = getIntent().getStringExtra("level_name");
		txtLevel.setText(""+level_name);
		Resources ress = getResources();

		playQuizquestions = new ArrayList<Question>();
		getQuestionFromWeb();
		
		// Create an ad
		adView = new AdView(this);
	    adView.setAdSize(AdSize.SMART_BANNER);
	    adView.setAdUnitId(getString(R.string.admob_banner));
	    
	    // Add the AdView to the view hierarchy. The view will have no size until the ad is loaded.
	    LinearLayout layout = (LinearLayout) findViewById(R.id.ads_layout);
	    layout.addView(adView);

	    // Create ad request.
	    isTestMode = ress.getBoolean(R.bool.istestmode);
	    AdRequest adRequest =null;
	    if(isTestMode){
	    	 // Request for Ads
	    	 System.out.println("Testing.... app");
	          adRequest = new AdRequest.Builder()
	         .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	         .addTestDevice("0C2DF43E6E70766851B6A3E5EE46A9B8")
					  .addTestDevice("D4F9CC518EADF120DDA2EFF49390C315")
	                .build();
	    }else{
	    	System.out.println("Live Apps");
	    	 adRequest = new AdRequest.Builder().build();
	    }
	    
	    // Start loading the ad in the background.
	    adView.loadAd(adRequest);

	    // Create the interstitial.
	    interstitial = new InterstitialAd(this);
	    interstitial.setAdUnitId(getString(R.string.admob_intersitital));
	    // Begin loading your interstitial.
	    interstitial.loadAd(adRequest);

	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btnNext:
			nextButtonClick();
			break;
		case R.id.btnPrev:
			prevButtonClick();
			break;
		case R.id.btnShowAnsware:
			txtAnsware.setText(playQuizquestions.get(currentQuestion).getAnsware());
			break;
			case R.id.relBack:
				onBackPressed();
				break;
		case R.id.btnJump:
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			final EditText input = new EditText(this);
			input.setKeyListener(new DigitsKeyListener());

			alert.setView(input);

			alert.setTitle("Jump Question: ").setMessage("Question No:").setPositiveButton(
					"Go", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Editable go = input.getText();
							try{
								int tt = Integer.parseInt(go.toString().trim());
								currentQuestion = tt;
								currentQuestion = currentQuestion -2;
								nextButtonClick();
							}catch(NumberFormatException nfe){
								
							}
						}
					}).setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.cancel();
						}
					}).show();
			break;
		}
	}
	
	private void nextButtonClick(){
		currentQuestion++;
		if(currentQuestion<totalQuestion){
			txtQuestion.setText(playQuizquestions.get(currentQuestion).getQuestion());
			txtAnsware.setText("");
			if(currentQuestion%20==0){
				displayInterstitial();
			}
		}else{
			currentQuestion--;
			
		}
		txtQuestionNO.setText(""+(currentQuestion + 1)+"/"+totalQuestion);
	}
	private void prevButtonClick(){
		currentQuestion--;
		if(currentQuestion>=0){
			
			txtQuestion.setText(playQuizquestions.get(currentQuestion).getQuestion());
			txtAnsware.setText("");
		}else{
			currentQuestion++;
			
		}
		txtQuestionNO.setText(""+(currentQuestion + 1)+"/"+totalQuestion);
	}
	 // Invoke displayInterstitial() when you are ready to display an interstitial.
	  public void displayInterstitial() {
	    if (interstitial.isLoaded()) {
	      interstitial.show();
	      
	      // Create the interstitial.
		    interstitial = new InterstitialAd(this);
		    interstitial.setAdUnitId(getString(R.string.admob_intersitital));

		    AdRequest adRequest =null;
		    if(isTestMode){
		    	 // Request for Ads
		    	 System.out.println("Testing.... app");
		          adRequest = new AdRequest.Builder()
		         .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
		         .addTestDevice("B15149A4EC1ED23173A27B04134DD483")
		                .build();
		    }else{
		    	System.out.println("Live Apps");
		    	 adRequest = new AdRequest.Builder().build();
		    }
		    // Begin loading your interstitial.
		    interstitial.loadAd(adRequest);
	    }
	  }

	

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()){
		
		default:
			break;
		}
		
	 return false;
	}
	private void getQuestionFromWeb() {
		//showProgress(isFirstTime);
		String url = getResources().getString(R.string.get_question_right_answare)+levelNo;

		Log.i("urlans",""+url);

		// Volley's json array request object
		StringRequest postRequest = new StringRequest(Request.Method.GET, url,
				new Response.Listener<String>() {
					JSONArray jsonResponse;

					@Override
					public void onResponse(String response) {

						if (response != null && !response.equalsIgnoreCase("[]")) {
							try {
								jsonResponse = new JSONArray(response);
								Log.i("INFL","ArrayLength: "+jsonResponse.length());
								for (int i = 0; i < jsonResponse.length(); i++) {
									JSONObject json = (JSONObject) jsonResponse.get(i);
									final Gson gson = new Gson();
									Question prathnaSabha = gson.fromJson(String.valueOf(json), Question.class);
									playQuizquestions.add(prathnaSabha);

								}
								nextQuizQuestion();

							} catch (JSONException e) {
								e.printStackTrace();
								Toast.makeText(getApplicationContext(), "There are no any data",
										Toast.LENGTH_SHORT).show();
							}
						}else{
							Toast.makeText(getApplicationContext(), "There are no any data",
									Toast.LENGTH_SHORT).show();
						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						error.printStackTrace();
						Log.i("ISSU", "Server ISSUE.");
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

		MainApplication.getInstance().addToRequestQueue(postRequest);
	}
	public void nextQuizQuestion(){
		totalQuestion = playQuizquestions.size();
		System.out.println("Level: "+playQuizquestions.size());
		if(totalQuestion>=1){
			txtQuestion.setText(playQuizquestions.get(currentQuestion).getQuestion());
			txtQuestionNO.setText(""+(currentQuestion + 1)+"/"+totalQuestion);
		}else{
			AlertDialog alertDialog = new AlertDialog.Builder(
                    this).create();

		    // Setting Dialog Title
		    alertDialog.setTitle("No Questions");
		
		    // Setting Dialog Message
		    alertDialog.setMessage("There are no enough question of this level");
		
		    // Setting Icon to Dialog
		   // alertDialog.setIcon(R.drawable.tick);
		
		    // Setting OK Button
		    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            // Write your code here to execute after dialog closed
		            //Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
		            	finish();
		            }
		    });
		
		    // Showing Alert Message
		    alertDialog.show();
		}
	}
}
