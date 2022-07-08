package com.sell.arkaysell.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
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
import com.google.android.gms.plus.PlusShare;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.sell.arkaysell.R;
import com.sell.arkaysell.activity.MenuHomeScreenActivity;
import com.sell.arkaysell.application.MainApplication;
import com.sell.arkaysell.bean.GameData;
import com.sell.arkaysell.bean.Settings;
import com.sell.arkaysell.facebook.DialogError;
import com.sell.arkaysell.facebook.Facebook;
import com.sell.arkaysell.facebook.FacebookError;
import com.sell.arkaysell.playquizbeans.PlayQuizLevel;
import com.sell.arkaysell.playquizbeans.PlayQuizQuestion;
import com.sell.arkaysell.utils.Constants;
import com.sell.arkaysell.utils.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Quiz activity, on This screen user play quiz with four option.
 * @author Arkay App
 *
 */

public class QuizPlayFragment extends Fragment  implements OnClickListener{
	private static int levelNo=1;
	private PlayQuizLevel level;
	private int quextionIndex=0;
	private static int level_no = 1;

	private int NO_OF_QUESTION = 10;
	private int totalScore=0;
	private int score=0;
	private int correctQuestion=0;
	private int inCorrectQuestion=0;
	private Dialog dialog;
	private boolean isSoundEffect;
	private boolean isVibration;
	private String appName="";
	private Typeface tpMarkoOne;
	private TextView quizImage;
	private TextView btnOpt1, btnOpt2, btnOpt3, btnOpt4;
	private TextView txtQuestion, txtScore, txtLevel;
	private SharedPreferences settings;
	boolean isSoundOn=false;
	private Animation animation;
	private MediaPlayer rightAnsware, wrongeAnsware;
	private TextView txtTrueQuestion, txtFalseQuestion;
	private final Handler mHandler = new Handler();
	private SharedPreferences.Editor editor;
	private View v;
	AdView adView;
	private String appHashTag = "#quizranking";
	private InterstitialAd interstitial;
	private List<PlayQuizQuestion> playQuizquestions =null;
	private DatabaseReference databaseReferenceSetting;
	private String uid;

	private Facebook mFacebook = null;
	private RelativeLayout relBack;
	Animation animationFromRight,animationFromLeft;
	private Button btnPlayAgain, btnHome ;
	private TextView txtLevelHeading, txtLevelScore,txtLevelTotalScore,lblLevelScore,lblLevelTotalScore,txtShareScore;
	int lastLevelScore = 0;
	private ImageButton  btnFacebook, btnGooglePlus, btnShare;
	private Prefs prefs;
	private FirebaseDatabase database;
	public ProgressDialog mProgressDialog;
	private RelativeLayout cardOpt1,cardOpt2,cardOpt3,cardOpt4;
	private TextView txtStatus;

	public interface Listener {
		public GameData getGameData();
		public void playAgain();
		void saveScoreInFirebase();
	}

	Listener mListener = null;

	public static QuizPlayFragment newInstance(Bundle bundle) {
		QuizPlayFragment fragment = new QuizPlayFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.fragment_quiz_play, container, false);
		final int[] CLICKABLES = new int[] {
				R.id.cardOpt1, R.id.cardOpt2,
				R.id.cardOpt3,R.id.cardOpt4
		};
		for (int i : CLICKABLES) {
			v.findViewById(i).setOnClickListener(this);
		}
		relBack = (RelativeLayout) v.findViewById(R.id.relBack);

		relBack.setOnClickListener(this);
		//((AppCompatActivity) getActivity()).getSupportActionBar().hide();
		appName = getResources().getString(R.string.app_name);

		prefs = MainApplication.getInstance().getPrefs();
		animationFromRight = new TranslateAnimation(500f, 0f, 0f, 0f);
		animationFromRight.setDuration(600);
		animationFromLeft = new TranslateAnimation(-500f, 0f, 0f, 0f);
		animationFromLeft.setDuration(600);

		tpMarkoOne = MainApplication.getInstance().getAugustSansRegular();
		animationFromRight = new TranslateAnimation(500f, 0f, 0f, 0f);
		animationFromRight.setDuration(600);
		animationFromLeft = new TranslateAnimation(-500f, 0f, 0f, 0f);
		animationFromLeft.setDuration(600);

		uid = prefs.getUID();

		databaseReferenceSetting = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_GAME_SETTING).child(uid);
		databaseReferenceSetting.keepSynced(true);


		database = FirebaseDatabase.getInstance();
		if (databaseReferenceSetting == null) {
			databaseReferenceSetting = database.getReference();
		}

		databaseReferenceSetting.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {

				Settings settings = dataSnapshot.getValue(Settings.class);

				if (dataSnapshot.getValue() != null) {
					isSoundEffect = settings.isSound();
					isVibration = settings.isVibration();
				}
			}

			@Override
			public void onCancelled(DatabaseError error) {
				// Failed to read value
				Log.w("SingleTrueFale", "Failed to read value.", error.toException());
			}
		});


		settings = getActivity().getSharedPreferences(MenuHomeScreenActivity.PREFS_NAME, 0);

		resetAllValue();


		// Create an ad
		adView = new AdView(getActivity());
		adView.setAdSize(AdSize.SMART_BANNER);
		adView.setAdUnitId(getString(R.string.admob_banner));

		// Add the AdView to the view hierarchy. The view will have no size  until the ad is loaded.
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.ads_layout);
		layout.addView(adView);
		// Create an ad request. Check logcat output for the hashed device ID to get test ads on a physical device.
		// Create ad request.
		Resources ress = getResources();
		boolean isTestMode = ress.getBoolean(R.bool.istestmode);
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
		interstitial = new InterstitialAd(getActivity());
		interstitial.setAdUnitId(getString(R.string.admob_intersitital));
		// Begin loading your interstitial.
		interstitial.loadAd(adRequest);

		return v;
	}
	public void setListener(Listener l) {
		mListener = l;
	}

	@Override
	public void onStart() {
		super.onStart();
		updateUi();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		updateUi();
	}

	void updateUi() {
		if (getActivity() == null) return;
	}

	private void nextQuizQuestion(){
		int count_question_completed = mListener.getGameData().getCountHowManyQuestionCompleted();
		count_question_completed++;
		mListener.getGameData().setCountHowManyQuestionCompleted(count_question_completed);
		//unlockhowManyQuestionCompleted(count_question_completed);
		System.out.println("Count Question Completed: "+ mListener.getGameData().getCountHowManyQuestionCompleted());
		if(quextionIndex>=NO_OF_QUESTION){

			int howManyTimesPlayQuiz = mListener.getGameData().getCountHowManyTimePlay();
			System.out.println("How Many Time Play: "+howManyTimesPlayQuiz);
			howManyTimesPlayQuiz++;
			//unlockPlayTime(howManyTimesPlayQuiz);
			mListener.getGameData().setCountHowManyTimePlay(howManyTimesPlayQuiz);

			count_question_completed = mListener.getGameData().getCountHowManyQuestionCompleted();
			count_question_completed--;
			mListener.getGameData().setCountHowManyQuestionCompleted(count_question_completed);

			saveScore();

			displayInterstitial();
			QuizCompleteDialog();

			blankAllValue();
			return;
		}

		cardOpt1.setClickable(true);
		cardOpt2.setClickable(true);
		cardOpt3.setClickable(true);
		cardOpt4.setClickable(true);

		if(quextionIndex<level.getNoOfQuestion()){
			int temp = quextionIndex;
			txtQuestion.setText(""+ ++temp+"/"+NO_OF_QUESTION);
			String imgName = level.getQuestion().get(quextionIndex).getQuestion();
			Pattern p = Pattern.compile(" ");
			Matcher m = p.matcher(imgName);
			imgName = m.replaceAll("_");
			quizImage.setText(level.getQuestion().get(quextionIndex).getQuestion());

			ArrayList<String> options = new ArrayList<String>();
			options.add(level.getQuestion().get(quextionIndex).getOptiona());
			options.add(level.getQuestion().get(quextionIndex).getOptionb());
			options.add(level.getQuestion().get(quextionIndex).getOptionc());
			options.add(level.getQuestion().get(quextionIndex).getOptiond());
			Collections.shuffle(options);

			btnOpt1.setText(""+options.get(0).trim());
			btnOpt2.setText(""+options.get(1).trim());
			btnOpt3.setText(""+options.get(2).trim());
			btnOpt4.setText(""+options.get(3).trim());
		}

		cardOpt1.startAnimation(animationFromLeft);
		cardOpt2.startAnimation(animationFromRight);
		cardOpt3.startAnimation(animationFromLeft);
		cardOpt4.startAnimation(animationFromRight);
		changeBtnTexColor();

		cardOpt1.setBackgroundResource(R.drawable.card);
		cardOpt2.setBackgroundResource(R.drawable.card);
		cardOpt3.setBackgroundResource(R.drawable.card);
		cardOpt4.setBackgroundResource(R.drawable.card);
	}

	public void QuizCompleteDialog(){

		mListener.getGameData().setTotalScore(totalScore);
		mListener.saveScoreInFirebase();

		dialog = new Dialog(getActivity());

		settings = getActivity().getSharedPreferences(MenuHomeScreenActivity.PREFS_NAME, 0);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialoug_game_over);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setCanceledOnTouchOutside(true);

		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

		appName = getActivity().getResources().getString(R.string.app_name);
		txtStatus = (TextView) dialog.findViewById(R.id.txtStatus);

		txtLevelHeading = (TextView)dialog.findViewById(R.id.txtLevelHeading);
		txtLevelHeading.setTypeface(tpMarkoOne);

		txtLevelScore = (TextView)dialog.findViewById(R.id.txtLevelScore);

		//totalScore = settings.getInt(MenuHomeScreenActivity.TOTAL_SCORE, 0);
		totalScore = mListener.getGameData().getTotalScore();
		txtLevelScore.setText(""+totalScore);
		txtLevelScore.setTypeface(MainApplication.getInstance().getAugustSansMedium());

		lastLevelScore = settings.getInt(MenuHomeScreenActivity.LAST_LEVEL_SCORE, 0);
		txtLevelTotalScore = (TextView)dialog.findViewById(R.id.txtLevelTotalScore);
		lblLevelScore = (TextView)dialog.findViewById(R.id.lblLevelScore);
		lblLevelTotalScore = (TextView)dialog.findViewById(R.id.lblLevelTotalScore);
		txtShareScore = (TextView)dialog.findViewById(R.id.txtShareScore);
		txtLevelTotalScore.setText(""+lastLevelScore);
		txtLevelTotalScore.setTypeface(MainApplication.getInstance().getAugustSansMedium());
		lblLevelScore.setTypeface(tpMarkoOne);
		lblLevelTotalScore.setTypeface(tpMarkoOne);
		txtShareScore.setTypeface(tpMarkoOne);

		btnPlayAgain = (Button) dialog.findViewById(R.id.btnPlayAgain);
		btnHome = (Button)dialog.findViewById(R.id.btnHome);
		btnHome.setTypeface(MainApplication.getInstance().getAugustSansMedium());
		btnPlayAgain.setTypeface(MainApplication.getInstance().getAugustSansMedium());

		btnPlayAgain.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				mListener.playAgain();
				resetAllValue();
				dialog.dismiss();
			}
		});
		btnHome.setOnClickListener(this);

		btnFacebook = (ImageButton) dialog.findViewById(R.id.btnFacebook);
		btnFacebook.setOnClickListener(this);

		btnGooglePlus = (ImageButton) dialog.findViewById(R.id.btnGooglePlus);
		btnGooglePlus.setOnClickListener(this);

		btnShare = (ImageButton) dialog.findViewById(R.id.btnShare);
		btnShare.setOnClickListener(this);
		txtStatus.setTypeface(tpMarkoOne);

		boolean islevelcomplted = settings.getBoolean(MenuHomeScreenActivity.IS_LAST_LEVEL_COMPLETED, false);

		level_no = settings.getInt(MenuHomeScreenActivity.LEVEL_COMPLETED, 1);
		Log.i("level_no",""+level_no);

		if(islevelcomplted){
			level_no--;
			txtLevelHeading.setText(getActivity().getString(R.string.level)+" "+ level_no +" "+  getActivity().getResources().getString(R.string.finished));
			btnPlayAgain.setText("Next");
			txtStatus.setText("Great Job!");
		}else{
			txtLevelHeading.setText(getActivity().getString(R.string.level)+" "+ level_no +" "+  getActivity().getResources().getString(R.string.not_completed));
			btnPlayAgain.setText(" Play again");
			txtStatus.setText("Failure is \n key to success!");
		}
	}

	public void changeBtnTexColor(){
		btnOpt1.setTextColor(Color.parseColor("#2c3e50"));
		btnOpt2.setTextColor(Color.parseColor("#2c3e50"));
		btnOpt3.setTextColor(Color.parseColor("#2c3e50"));
		btnOpt4.setTextColor(Color.parseColor("#2c3e50"));
		txtQuestion.setTextColor(Color.parseColor("#212121"));
		txtScore.setTextColor(Color.WHITE);
	}
	// Invoke displayInterstitial() when you are ready to display an interstitial.
	public void displayInterstitial() {
		if (interstitial.isLoaded()) {
			interstitial.show();
		}
	}


	@Override
	public void onClick(View v) {

		if(quextionIndex<level.getNoOfQuestion()){
			cardOpt1.setClickable(false);
			cardOpt2.setClickable(false);
			cardOpt3.setClickable(false);
			cardOpt4.setClickable(false);
			switch(v.getId()){
				case R.id.cardOpt1:
					if(btnOpt1.getText().toString().trim().equalsIgnoreCase(level.getQuestion().get(quextionIndex).getTrueAns().trim())){
						quextionIndex++;
						changeBtnTexColor();
						btnOpt1.setTextColor(Color.WHITE);
						addScore();
						cardOpt1.setBackgroundResource(R.drawable.card_green);
						cardOpt1.startAnimation(animation);
					}else{
						cardOpt1.setBackgroundResource(R.drawable.card_red);
						changeBtnTexColor();
						btnOpt1.setTextColor(Color.WHITE);
						wrongeQuestion();
//						cardOpt1.startAnimation(animation);
						quextionIndex++;
					}
					break;
				case R.id.cardOpt2:
					if(btnOpt2.getText().toString().trim().equalsIgnoreCase(level.getQuestion().get(quextionIndex).getTrueAns().trim())){
						quextionIndex++;
						changeBtnTexColor();
						btnOpt2.setTextColor(Color.WHITE);
						addScore();
						cardOpt2.setBackgroundResource(R.drawable.card_green);
						cardOpt2.startAnimation(animation);
					}else{
						cardOpt2.setBackgroundResource(R.drawable.card_red);
						changeBtnTexColor();
						btnOpt2.setTextColor(Color.WHITE);
						wrongeQuestion();
//						cardOpt2.startAnimation(animation);
						quextionIndex++;
					}
					break;
				case R.id.cardOpt3:
					if(btnOpt3.getText().toString().trim().equalsIgnoreCase(level.getQuestion().get(quextionIndex).getTrueAns().trim())){
						quextionIndex++;
						changeBtnTexColor();
						btnOpt3.setTextColor(Color.WHITE);
						addScore();
						cardOpt3.setBackgroundResource(R.drawable.card_green);
						cardOpt3.startAnimation(animation);
					}else{
						cardOpt3.setBackgroundResource(R.drawable.card_red);
						changeBtnTexColor();
						btnOpt3.setTextColor(Color.WHITE);
						wrongeQuestion();
//						cardOpt3.startAnimation(animation);
						quextionIndex++;
					}
					break;
				case R.id.cardOpt4:
					if(btnOpt4.getText().toString().trim().equalsIgnoreCase(level.getQuestion().get(quextionIndex).getTrueAns().trim())){
						quextionIndex++;
						changeBtnTexColor();
						btnOpt4.setTextColor(Color.WHITE);
						addScore();
						cardOpt4.setBackgroundResource(R.drawable.card_green);
						cardOpt4.startAnimation(animation);
					}else{
						cardOpt4.setBackgroundResource(R.drawable.card_red);
						changeBtnTexColor();
						btnOpt4.setTextColor(Color.WHITE);
						wrongeQuestion();
//						cardOpt4.startAnimation(animation);
						quextionIndex++;
					}
					break;
				case R.id.relBack:
					getActivity().onBackPressed();
					break;
				case R.id.btnHome:
					getFragmentManager().popBackStack();
					dialog.dismiss();
					break;
				case R.id.btnFacebook:
					facebookPost();
					break;
				case R.id.btnGooglePlus:
					Intent shareIntent = new PlusShare.Builder(getActivity())
							.setType("text/plain")
							.setText("\""+appName+"\"   I'm playing "+appHashTag+" Android App and just completed "+levelNo+" levels with "+totalScore+ " score Can you beat my high score?.")
							.setContentUrl(Uri.parse("https://play.google.com/store/apps/details?"+getActivity().getPackageName()))
							.getIntent();
					getActivity().startActivityForResult(shareIntent, 0);

					break;
				case R.id.btnShare:
					Intent s = new Intent(Intent.ACTION_SEND);
					s.setType("text/plain");
					s.putExtra(Intent.EXTRA_SUBJECT, ""+appName);
					s.putExtra(Intent.EXTRA_TEXT, ""+appName+"\"   I'm playing "+appHashTag+" Android App and just completed "+levelNo+" levels with "+totalScore+ " score Can you beat my high score?.");
					s.putExtra("url", "http://goo.gl/CrGQTI");
					getActivity().startActivity(Intent.createChooser(s, ""+appName));
					break;
			}
		}else{
			mHandler.postDelayed(mUpdateUITimerTask, 2 * 10);
		}
		mHandler.postDelayed(mUpdateUITimerTask, 2 * 1000);
		txtScore.setText("Score: "+totalScore);
	}
	private final Runnable mUpdateUITimerTask = new Runnable() {
		public void run() {
			cardOpt1.setBackgroundResource(R.drawable.card);
			cardOpt2.setBackgroundResource(R.drawable.card);
			cardOpt3.setBackgroundResource(R.drawable.card);
			cardOpt4.setBackgroundResource(R.drawable.card);
			cardOpt1.clearAnimation();
			cardOpt2.clearAnimation();
			cardOpt3.clearAnimation();
			cardOpt4.clearAnimation();
			nextQuizQuestion();
		}
	};

		public void facebookPost() {
			Bundle params = new Bundle();
			params.putString("name", ""+appName);
			params.putString("caption", "I'm playing "+appName+" Android App.");
			params.putString("description", "I'm just completed "+levelNo+" level on "+appHashTag+" with "+totalScore+" Can you beat my high score?.");
			params.putString("link", "https://play.google.com/store/apps/details?id="+getActivity().getPackageName());
			params.putString("picture", getActivity().getResources().getString(R.string.icon_url));
			mFacebook.dialog(getActivity(), "stream.publish", params, new Facebook.DialogListener() {

				@Override
				public void onFacebookError(FacebookError e) {
					// TODO handle error in publishing
				}

				@Override
				public void onError(DialogError e) {
					// TODO handle dialog errors
				}

				@Override
				public void onComplete(Bundle values) {
					Toast.makeText(getActivity(), "Post successful",
							Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onCancel() {
					// TODO user don't want to share and presses cancel button
				}
			});
		}

	private void addScore(){
		rightSound();
		correctQuestion++;
		txtTrueQuestion.setText(" "+correctQuestion +" ");
		totalScore = totalScore + 10;
		score = score + 10;
		txtScore.setText("Score: "+totalScore+" ");

		int rightAns = mListener.getGameData().getCountHowManyRightAnswareQuestion();
		rightAns++;
		Locale.setDefault(Locale.US);
		mListener.getGameData().setCountHowManyRightAnswareQuestion(rightAns);
		mListener.getGameData().setTotalScore(totalScore);
		mListener.getGameData().save(settings,MenuHomeScreenActivity.myshareprefkey);
		System.out.println("Right Answare: "+ mListener.getGameData().getCountHowManyRightAnswareQuestion());
	}


	private void wrongeQuestion(){
		playWrongSound();
		//saveScore();
		inCorrectQuestion++;
		totalScore = totalScore - 3;
		score = score - 3;
		txtFalseQuestion.setText(" "+ inCorrectQuestion +" ");

		if(btnOpt1.getText().toString().equalsIgnoreCase(level.getQuestion().get(quextionIndex).getTrueAns())){
			cardOpt1.setBackgroundResource(R.drawable.card_green);
			btnOpt1.setTextColor(Color.WHITE);
			cardOpt1.startAnimation(animation);
		}
		if(btnOpt2.getText().toString().equalsIgnoreCase(level.getQuestion().get(quextionIndex).getTrueAns())){
			cardOpt2.setBackgroundResource(R.drawable.card_green);
			btnOpt2.setTextColor(Color.WHITE);
			cardOpt2.startAnimation(animation);
		}
		if(btnOpt3.getText().toString().equalsIgnoreCase(level.getQuestion().get(quextionIndex).getTrueAns())){
			cardOpt3.setBackgroundResource(R.drawable.card_green);
			btnOpt3.setTextColor(Color.WHITE);
			cardOpt3.startAnimation(animation);
		}
		if(btnOpt4.getText().toString().equalsIgnoreCase(level.getQuestion().get(quextionIndex).getTrueAns())){
			cardOpt4.setBackgroundResource(R.drawable.card_green);
			btnOpt4.setTextColor(Color.WHITE);
			cardOpt4.startAnimation(animation);
		}

		if(totalScore<0){
			totalScore=0;
		}
	}

	private void saveScore(){
		editor = settings.edit();
		mListener.getGameData().setTotalScore(totalScore);
		//editor.putInt(MenuHomeScreenActivity.TOTAL_SCORE, totalScore);
		editor.putInt(MenuHomeScreenActivity.LAST_LEVEL_SCORE, score);

		if(correctQuestion>=7){
			//unlockLevelCompletedAchivement(levelNo);
			levelNo++;
			editor.putBoolean(MenuHomeScreenActivity.IS_LAST_LEVEL_COMPLETED, true);
			mListener.getGameData().setLevelCompleted(levelNo);
		}else{
			editor.putBoolean(MenuHomeScreenActivity.IS_LAST_LEVEL_COMPLETED, false);
		}
		mListener.getGameData().save(settings,MenuHomeScreenActivity.myshareprefkey);
		editor.commit();
	}


	public void rightSound()
	{
		if(isSoundEffect){
			AudioManager meng = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
			int volume = meng.getStreamVolume( AudioManager.STREAM_NOTIFICATION);

			if (volume != 0)
			{
				if (rightAnsware == null)
					rightAnsware = MediaPlayer.create(getActivity(), R.raw.right_ans);
				if (rightAnsware != null)
					rightAnsware.start();
			}
		}
	}

	private void playWrongSound(){
		if(isSoundEffect){
			AudioManager meng = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
			int volume = meng.getStreamVolume( AudioManager.STREAM_NOTIFICATION);

			if (volume != 0)
			{
				if (wrongeAnsware == null)
					wrongeAnsware = MediaPlayer.create(getActivity(), R.raw.wronge_ans);
				if (wrongeAnsware != null)
					wrongeAnsware.start();
			}
		}
		if(isVibration){
			Vibrator myVib = (Vibrator) getActivity().getSystemService(getActivity().VIBRATOR_SERVICE);
			myVib.vibrate(200);
		}
	}

	private void resetAllValue(){

		isSoundOn = settings.getBoolean("silentMode", true);
		levelNo = mListener.getGameData().getLevelCompleted();
		txtQuestion = (TextView)v.findViewById(R.id.txt_question);
		txtQuestion.setTypeface(tpMarkoOne);

		txtLevel = (TextView)v.findViewById(R.id.txtLevel);
		txtLevel.setTypeface(tpMarkoOne);
		txtLevel.setText(getString(R.string.level)+": "+levelNo);

		btnOpt1 = (TextView) v.findViewById(R.id.btnOpt1);
		btnOpt2 = (TextView)v.findViewById(R.id.btnOpt2);
		btnOpt3 = (TextView)v.findViewById(R.id.btnOpt3);
		btnOpt4 = (TextView)v.findViewById(R.id.btnOpt4);

		cardOpt1 = (RelativeLayout) v.findViewById(R.id.cardOpt1);
		cardOpt2 = (RelativeLayout) v.findViewById(R.id.cardOpt2);
		cardOpt3 = (RelativeLayout) v.findViewById(R.id.cardOpt3);
		cardOpt4 = (RelativeLayout) v.findViewById(R.id.cardOpt4);

		cardOpt1.setOnClickListener(this);
		cardOpt2.setOnClickListener(this);
		cardOpt3.setOnClickListener(this);
		cardOpt4.setOnClickListener(this);

		cardOpt1.setBackgroundResource(R.drawable.card);
		cardOpt2.setBackgroundResource(R.drawable.card);
		cardOpt3.setBackgroundResource(R.drawable.card);
		cardOpt4.setBackgroundResource(R.drawable.card);


		btnOpt1.setTypeface(tpMarkoOne);
		btnOpt2.setTypeface(tpMarkoOne);
		btnOpt3.setTypeface(tpMarkoOne);
		btnOpt4.setTypeface(tpMarkoOne);

		txtTrueQuestion = (TextView)v.findViewById(R.id.txtTrueQuestion);
		txtTrueQuestion.setTypeface(tpMarkoOne);
		txtTrueQuestion.setText("0");
		txtFalseQuestion = (TextView)v.findViewById(R.id.txtFalseQuestion);
		txtFalseQuestion.setTypeface(tpMarkoOne);
		txtFalseQuestion.setText("0");

		quizImage  = (TextView)v.findViewById(R.id.imgQuiz);
		quizImage.setTypeface(tpMarkoOne);

		animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
		animation.setDuration(500); // duration - half a second
		animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
		animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
		animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in


		totalScore  = mListener.getGameData().getTotalScore();
		txtScore = (TextView)v.findViewById(R.id.txtScore);


		txtScore.setText("Score: "+totalScore);
		txtScore.setTypeface(tpMarkoOne);

		Resources ress = getResources();
		playQuizquestions = new ArrayList<>();
		getQuestionFromWeb();
	}

	public void blankAllValue(){
		quextionIndex=0;
		isSoundEffect=false;
		isVibration=false;

		totalScore=0;
		score=0;
		correctQuestion=0;
		inCorrectQuestion=0;
	}

	private void getQuestionFromWeb() {
		showProgressDialog();
		//showProgress(isFirstTime);
		String url = getResources().getString(R.string.question_bank_url)+levelNo;

		//Log.i("urlans",""+url);

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
									PlayQuizQuestion prathnaSabha = gson.fromJson(String.valueOf(json), PlayQuizQuestion.class);
									playQuizquestions.add(prathnaSabha);
									Log.i("option 1:",""+playQuizquestions.get(i).getOptiona());
									String rightAns = playQuizquestions.get(i).getTrueAns();

									if(rightAns.equalsIgnoreCase("A")){
										prathnaSabha.setTrueAns(playQuizquestions.get(i).getOptiona());
									}else if(rightAns.equalsIgnoreCase("B")){
										prathnaSabha.setTrueAns(playQuizquestions.get(i).getOptionb());
									}else if(rightAns.equalsIgnoreCase("C")){
										prathnaSabha.setTrueAns(playQuizquestions.get(i).getOptionc());
									}else{
										prathnaSabha.setTrueAns(playQuizquestions.get(i).getOptiond());
									}
								}
								level = new PlayQuizLevel(levelNo,NO_OF_QUESTION,getActivity());
								Collections.shuffle(playQuizquestions);
								level.setQuestion(playQuizquestions);
								nextQuizQuestion();
								hideProgressDialog();

							} catch (JSONException e) {
								e.printStackTrace();
								Toast.makeText(getActivity(), "There are no any data",
										Toast.LENGTH_SHORT).show();
								hideProgressDialog();
							}
						}else{
							Toast.makeText(getActivity(), "There are no any data",
									Toast.LENGTH_SHORT).show();
							hideProgressDialog();
						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						error.printStackTrace();
						Log.i("ISSU", "Server ISSUE.");
						hideProgressDialog();
					}
				}
		)

		{			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<>();
				return params;
			}
		};

		MainApplication.getInstance().addToRequestQueue(postRequest);
	}

	public void showProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(getActivity());
			mProgressDialog.setMessage(getString(R.string.loading));
			mProgressDialog.setIndeterminate(true);
		}

		mProgressDialog.show();
	}

	public void hideProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}
}