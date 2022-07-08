package com.sell.arkaysell.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
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
import com.sell.arkaysell.application.MainApplication;
import com.sell.arkaysell.bean.GameData;
import com.sell.arkaysell.bean.Leaderboard;
import com.sell.arkaysell.bean.Settings;
import com.sell.arkaysell.bean.User;
import com.sell.arkaysell.customviews.CustomTypefaceSpan;
import com.sell.arkaysell.fragment.FragmentProfile;
import com.sell.arkaysell.fragment.QuizPlayFragment;
import com.sell.arkaysell.utils.Constants;
import com.sell.arkaysell.utils.LogEvent;
import com.sell.arkaysell.utils.Prefs;

import java.util.HashMap;


/**
 * Home Screen of this apps. Display Button to play quiz, Leaderboard, achievement, setting etc.
 * @author Arkay Apps
 *
 */
public class MenuHomeScreenActivity extends AppCompatActivity implements
		View.OnClickListener, GoogleApiClient.OnConnectionFailedListener,
		QuizPlayFragment.Listener,FragmentProfile.Listener{

	private Button btnPlay, btnLeaderboard, btnAchievement, btnLearning,btnSetting;
	private FragmentProfile fragmentProfile;
	private AboutUsActivity aboutUsActivity;


	/** The interstitial ad. */
	public static final String PREFS_NAME = "preferences";
	private static final String DATABASE_NAME = "database.db";
	public static final String myshareprefkey = "quizpower";

	public static final String SOUND_EFFECT = "sound_effect";
	public static final String VIBRATION = "vibration";

	public static final String TOTAL_SCORE = "total_score";
	public static final String LEVEL ="level";
	private SwitchCompat toggleSoundEffect,
			toggleVibration;
	private TextView lblSound,lblVibration,lblSetting;
	private boolean isSoundEffect, isVibration;
	private RelativeLayout relMore;
	private GoogleApiClient mGoogleApiClient;

	//Achivement
	public static final String LEVEL_COMPLETED = "level_completed";
	public static final String IS_LAST_LEVEL_COMPLETED = "is_last_level_completed";
	public static final String LAST_LEVEL_SCORE = "last_level_score";
	public static final String HOW_MANY_TIMES_PLAY_QUIZ = "how_many_time_play_quiz";
	public static final String COUNT_QUESTION_COMPLETED = "count_question_completed";
	public static final String COUNT_RIGHT_ANSWARE_QUESTIONS = "count_right_answare_questions";

	SharedPreferences settings;
	QuizPlayFragment mQuizPlayFragment;
	private InterstitialAd interstitial;

	Context context;
	static final String TAG = "MenuHomeScreenActivity";
	private DatabaseReference databaseUserLeaderboard;
	private GameData gameData;
	private final Handler mHandler = new Handler();
	Typeface tpMarkoOne;
	TextView txtAppname;

	// Request code used to invoke sign in user interactions.
	private static final int RC_SIGN_IN = 9001;
	FragmentTransaction ft;

	public static MediaPlayer backgorundMusic;
	public enum TrackerName {
		APP_TRACKER, // Tracker used only in this app.
		GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
		ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
	}
	HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
	AppEventsLogger logger;
	private static Tracker mTracker;
	private Prefs prefs;
	private String uid;
	private DatabaseReference firebaseUserSettingReference;
	private DatabaseReference scoreBoardDataRef;
	private DatabaseReference databaseUserRegistraion;
	public boolean isMusic = true;
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		FacebookSdk.sdkInitialize(getApplicationContext());
		setContentView(R.layout.activity_menu_home);
		context = getApplicationContext();
		AppEventsLogger.activateApp(this);
		logger = AppEventsLogger.newLogger(this);
		logger.logEvent(LogEvent.HOME_SCREEN_ENTER);
		MainApplication application = (MainApplication) getApplication();
		mTracker = application.getDefaultTracker();
		//Log.i(TAG, "Setting screen name: " + Constants.);
		mTracker.setScreenName(LogEvent.HOME_SCREEN_ENTER);
		mTracker.send(new HitBuilders.ScreenViewBuilder().build());

		prefs = MainApplication.getInstance().getPrefs();

		settings = getSharedPreferences(MenuHomeScreenActivity.PREFS_NAME, 0);
		gameData = new GameData(settings, myshareprefkey);
		// Create the Google Api Client with access to Plus and Games

		tpMarkoOne = MainApplication.getInstance().getAugustSansRegular();
		btnPlay = (Button) findViewById(R.id.btnPlay);
		btnPlay.setOnClickListener(this);
		btnLeaderboard = (Button) findViewById(R.id.btnLeaderboard);
		btnLeaderboard.setOnClickListener(this);
		txtAppname = (TextView) findViewById(R.id.txtAppname);
		txtAppname.setTypeface(MainApplication.getInstance().getAugustSansMedium());
		btnAchievement = (Button) findViewById(R.id.btnProfile);
		btnAchievement.setOnClickListener(this);
		btnLearning = (Button) findViewById(R.id.btnLearning);
		btnLearning.setOnClickListener(this);

		btnSetting = (Button) findViewById(R.id.btnSetting);
		btnSetting.setOnClickListener(this);
		relMore = (RelativeLayout) findViewById(R.id.relMore);
		//imgMenu = (ImageButton) findViewById(R.id.imgMenu);
		relMore.setOnClickListener(this);

		//registerForContextMenu(imgMenu);
		relMore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showFilterPopup(v);
			}
		});
		uid = prefs.getUID();


		databaseUserLeaderboard = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_LEADERBOARD).child(uid);

		if (!uid.isEmpty()) {
			firebaseUserSettingReference = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USER_SETTING).child(uid);
			firebaseUserSettingReference.keepSynced(true);
			if (firebaseUserSettingReference == null) {
				FirebaseDatabase database = FirebaseDatabase.getInstance();
				firebaseUserSettingReference = database.getReference();
			}

			firebaseUserSettingReference.addValueEventListener(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					Settings settings = dataSnapshot.getValue(Settings.class);

					if (dataSnapshot.getValue() != null) {
						isMusic = settings.isMusic();
						isSoundEffect = settings.isSound();
						prefs.setGamesound(isSoundEffect);
						playStopBackgroundMusic(isMusic);
					}
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
					Log.e(TAG, "onCancelled", databaseError.toException());
				}
			});
		}
		if (!uid.isEmpty()) {
			scoreBoardDataRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_GAME_SCORE).child(uid);
			scoreBoardDataRef.addValueEventListener(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					gameData = dataSnapshot.getValue(GameData.class);
					if (gameData == null) {
						gameData = new GameData();
					}
					Log.i("INF", "Score: " + gameData.getTotalScore());
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
					Log.e(TAG, "onCancelled", databaseError.toException());
				}
			});
		}


		mQuizPlayFragment = new QuizPlayFragment();
		mQuizPlayFragment.setListener(this);

		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestIdToken(getString(R.string.default_web_client_id))
				.requestEmail()
				.build();
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.enableAutoManage(this, this)
				.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
				.build();
		mGoogleApiClient.connect();

		backgorundMusic = MediaPlayer.create(getBaseContext(), R.raw.background_music);
		backgorundMusic.setLooping(true);


		if (prefs.getUID().equals("")) {
			Intent intent = new Intent(this, LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
			finish();
		} else {
			if (!uid.isEmpty()) {
				databaseUserRegistraion = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USER_PROFILE).child(uid);
				databaseUserRegistraion.keepSynced(true);
				databaseUserRegistraion.addValueEventListener(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						user = dataSnapshot.getValue(User.class);
						Log.i("INFO", "User Name: " + user);
						setValue();

					}

					@Override
					public void onCancelled(DatabaseError databaseError) {
						Log.e(TAG, "onCancelled", databaseError.toException());
					}
				});
			}

			Handler delayhandler = new Handler();

			delayhandler.postDelayed(stopLoadDataDialogSomeTime, 5000);

			// Create the interstitial.
			interstitial = new InterstitialAd(this);
			interstitial.setAdUnitId(getString(R.string.admob_intersitital));

			// Create ad request.
			Resources ress = getResources();
			boolean isTestMode = ress.getBoolean(R.bool.istestmode);
			AdRequest adRequest = null;
			if (isTestMode) {
				// Request for Ads
				System.out.println("Testing.... app");
				adRequest = new AdRequest.Builder()
						.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
						.addTestDevice("0C2DF43E6E70766851B6A3E5EE46A9B8")
						.addTestDevice("D4F9CC518EADF120DDA2EFF49390C315")
						.build();
			} else {
				System.out.println("Live Apps");
				adRequest = new AdRequest.Builder().build();
			}

			// Begin loading your interstitial.
			interstitial.loadAd(adRequest);

			mHandler.postDelayed(mUpdateUITimerTask, 10 * 1000);
		}
	}

	private void setValue() {
		if (isMusic) {
			playStopBackgroundMusic(true);
		} else {
			playStopBackgroundMusic(false);
		}
		prefs.setAdsRemove(user.isAdsRemove());

		Log.i("INFO","Ads Status: "+prefs.isAdsRemove());
	}

	public void playStopBackgroundMusic(boolean isMusicPlay) {
		if (isMusic) {
			if (isMusicPlay) {
				startService(new Intent(this, BackgroundSoundService.class));
			} else {
				stopService(new Intent(this, BackgroundSoundService.class));
			}
		} else {
			stopService(new Intent(this, BackgroundSoundService.class));
		}
	}



	Runnable stopLoadDataDialogSomeTime = new Runnable() {
		public void run(){
	}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btnPlay:
//				YoYo.with(Techniques.Landing).duration(1000).playOn(btnPlay);
				if(gameData.getLevelCompleted()==0 || gameData.getLevelCompleted()==1){
				}
				Bundle bundle = new Bundle();
				mQuizPlayFragment = QuizPlayFragment.newInstance(bundle);
				mQuizPlayFragment.setListener(this);
				mQuizPlayFragment.setArguments(bundle);

				ft = getSupportFragmentManager().beginTransaction();
//				ft.setCustomAnimations(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left,R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
				ft.replace(R.id.fragment_container, mQuizPlayFragment).addToBackStack(null).commit();

				break;
			case R.id.btnLeaderboard:
				YoYo.with(Techniques.Landing).duration(1000).playOn(btnLeaderboard);
				viewLeaderboard();

				break;
			case R.id.btnProfile:
				showUserProfile();
				break;
			case R.id.btnLearning:
				YoYo.with(Techniques.Landing).duration(1000).playOn(btnLearning);
				Intent intPlay = new Intent(this, LevelActivity.class);
				startActivity(intPlay);
				break;
			case R.id.btnSetting:
				YoYo.with(Techniques.Landing).duration(1000).playOn(btnSetting);
				SettingDialog();
				break;
		}
	}

	public void showUserProfile() {
		Bundle bundle = new Bundle();
		fragmentProfile = FragmentProfile.newInstance(bundle);
		fragmentProfile.setListener(this);
		getSupportFragmentManager().beginTransaction().replace(R.id.main_home_layout, fragmentProfile).addToBackStack("tag").commit();
	}

	public void showAbout(){
		Intent intent = new Intent(getApplicationContext(),AboutUsActivity.class);
		startActivity(intent);
	}

	public void viewLeaderboard() {
		Intent moreIntent = new Intent(getApplicationContext(), LeaderboardActivity.class);
		moreIntent.putExtra("name", "" + user.getUserDisplayName());
		startActivity(moreIntent);
	}


	public void SettingDialog(){
		Button btnShareMe,btnRateMe;
		settings = getSharedPreferences(MenuHomeScreenActivity.PREFS_NAME, 0);

		isSoundEffect = settings.getBoolean(
				MenuHomeScreenActivity.SOUND_EFFECT, true);
		isVibration = settings.getBoolean(MenuHomeScreenActivity.VIBRATION,
				true);

		final Dialog dialog = new Dialog(this);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.activity_setting_dialoug);
		dialog.setCanceledOnTouchOutside(true);
		toggleSoundEffect = (SwitchCompat) dialog.findViewById(R.id.toggleSoundEffect);
		lblVibration = (TextView) dialog.findViewById(R.id.lblVibration);
		lblSound = (TextView) dialog.findViewById(R.id.lblSound);
		lblSetting = (TextView) dialog.findViewById(R.id.lblSetting);

		lblSetting.setTypeface(tpMarkoOne);
		lblSound.setTypeface(tpMarkoOne);
		lblVibration.setTypeface(tpMarkoOne);
		toggleSoundEffect.setChecked(isSoundEffect);

		toggleVibration = (SwitchCompat) dialog.findViewById(R.id.toggleVibration);
		toggleVibration.setChecked(isVibration);
		btnShareMe = (Button) dialog.findViewById(R.id.btnShareMe);
		btnRateMe = (Button) dialog.findViewById(R.id.btnRateMe);
		btnRateMe.setTypeface(tpMarkoOne);
		btnShareMe.setTypeface(tpMarkoOne);

		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
		btnShareMe.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startSendMailActivity();
				dialog.dismiss();
			}
		});
		btnRateMe.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String appPackageName = getPackageName();
				Intent marketIntent = new Intent(Intent.ACTION_VIEW,
						Uri.parse("market://details?id=" + appPackageName));
				marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
						| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				startActivity(marketIntent);
				dialog.dismiss();
			}
		});
		toggleSoundEffect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences.Editor editor = settings.edit();
				Log.i("info", "" + toggleSoundEffect.isChecked());
				editor = settings.edit();
				editor.putBoolean(MenuHomeScreenActivity.SOUND_EFFECT,
						toggleSoundEffect.isChecked());
				editor.commit();
			}
		});
		toggleVibration.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences.Editor editor = settings.edit();
				Log.i("info", "" + toggleVibration.isChecked());
				editor = settings.edit();
				editor.putBoolean(MenuHomeScreenActivity.VIBRATION,
						toggleVibration.isChecked());
				editor.commit();
			}
		});

	}

	public void startSendMailActivity() {

		try {
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("text/plain");
			i.putExtra(Intent.EXTRA_SUBJECT,
					"" + getResources().getString(R.string.app_name));
			String sAux = "\nLet me recommend you this application\n\n";
			sAux = sAux + "https://play.google.com/store/apps/details?id="
					+ getPackageName() + " \n\n";
			i.putExtra(Intent.EXTRA_TEXT, sAux);
			startActivity(Intent.createChooser(i, "choose one"));
		} catch (Exception e) { // e.toString();
		}
	}

	@Override
	public void onBackPressed() {
		getSupportFragmentManager().popBackStack();
		this.findViewById(R.id.linearLayout1).setVisibility(View.VISIBLE);
		if(getSupportFragmentManager().getBackStackEntryCount()==0){
			super.onBackPressed();
		}
	}

	@Override
	public GameData getGameData() {
		return this.gameData;
	}

	@Override
	public void playAgain() {
		// TODO Auto-generated method stub
		//getSupportFragmentManager().popBackStack();
		getSupportFragmentManager().beginTransaction().replace( R.id.fragment_container, mQuizPlayFragment ).addToBackStack( "tag" ).commit();
	}
	/**
	 * Loads a Snapshot from the user's synchronized storage.
	 */

	/**
	 * Conflict resolution for when Snapshots are opened.
	 * @param result The open snapshot result to resolve on open.
	 * @return The opened Snapshot on success; otherwise, returns null.
	 */

	/**
	 * Prepares saving Snapshot to the user's synchronized storage, conditionally resolves errors,
	 * and stores the Snapshot.
	 */
	/**
	 * Generates metadata, takes a screenshot, and performs the write operation for saving a
	 * snapshot.
	 */

	/**
	 * Gets a screenshot to use with snapshots. Note that in practice you probably do not want to
	 * use this approach because tablet screen sizes can become pretty large and because the image
	 * will contain any UI and layout surrounding the area of interest.
	 */


	/** Shows the "sign in" bar (explanation and button). */

	/** Shows the "sign out" bar (explanation and button). */

	/** Prints a log message (convenience method). */
	public void displayInterstitial() {
		if (interstitial.isLoaded()) {
			interstitial.show();
		}
	}
	private final Runnable mUpdateUITimerTask = new Runnable() {
		public void run() {
			displayInterstitial();

		}
	};

	synchronized  Tracker getTracker(TrackerName trackerId) {
		if (!mTrackers.containsKey(trackerId)) {
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			Tracker t =      analytics.newTracker(R.xml.global_tracker);
			mTrackers.put(trackerId, t);
		}
		return mTrackers.get(trackerId);
	}

	private void showFilterPopup(View v) {
		PopupMenu popup = new PopupMenu(this, v);
		// Inflate the menu from xml
		popup.getMenuInflater().inflate(R.menu.list_menu, popup.getMenu());
		// Setup menu item selection
		Menu m = popup.getMenu();
		for (int i = 0; i < m.size(); i++) {
			MenuItem mi = m.getItem(i);

			//for aapplying a font to subMenu ...
			SubMenu subMenu = mi.getSubMenu();
			if (subMenu != null && subMenu.size() > 0) {
				for (int j = 0; j < subMenu.size(); j++) {
					MenuItem subMenuItem = subMenu.getItem(j);
					applyFontToMenuItem(subMenuItem);
					//YoYo.with(Techniques.BounceInLeft).duration(1000).playOn(subMenu.getItem(j).getActionView());
				}
			}
			//the method we have create in activity
			applyFontToMenuItem(mi);
		}



		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
					case R.id.menu_item_about:
						showAbout();
						return true;
					case R.id.menu_item_howtoplay:
						Intent intent1 = new Intent(getApplicationContext(),HowToPlayActivity.class);
						startActivity(intent1);
						return true;
					case R.id.menu_item_login:
						logout();
						return true;
					default:
						return false;
				}
			}
		});
		// Handle dismissal with: popup.setOnDismissListener(...);
		// Show the menu
		popup.show();
	}

	private void applyFontToMenuItem(MenuItem mi) {
		SpannableString mNewTitle = new SpannableString(mi.getTitle());
		mNewTitle.setSpan(new CustomTypefaceSpan("", tpMarkoOne), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		mi.setTitle(mNewTitle);
	}

	private void logout() {
		new AlertDialog.Builder(MenuHomeScreenActivity.this)
				.setTitle("Log Out?")
				.setMessage("Are you sure you want to log out?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// continue with delete
						signOut();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// do nothing

					}
				})
				.show();
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

		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
	}

	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}
	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		// An unresolvable error has occurred and Google APIs (including Sign-In) will not be available.
		Log.d("mainActivity", "onConnectionFailed:" + connectionResult);
	}
	public User getUser() {
		return this.user;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

	}

	public void saveScoreInFirebase() {


		int tempPlayCount = gameData.getCountHowManyTimePlay();
		tempPlayCount++;
		gameData.setCountHowManyTimePlay(tempPlayCount);
		scoreBoardDataRef.setValue(gameData);

		Leaderboard saveLeaderboard = new Leaderboard();
		saveLeaderboard.setName(user.getUserDisplayName());
		saveLeaderboard.setScore(gameData.getTotalScore());
		saveLeaderboard.setProfilePic(user.getProfileImage());
		saveLeaderboard.setCountryCode(user.getCountryCode());
		databaseUserLeaderboard.setValue(saveLeaderboard);
	}
}
		
		
