package com.sell.arkaysell.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.sell.arkaysell.R;
import com.sell.arkaysell.adapter.AllListAdaptor;
import com.sell.arkaysell.application.MainApplication;
import com.sell.arkaysell.bean.SingleAnswareLevelInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Display Level on Single Answare activity.
 * @author Arkay
 *
 */
public class LevelActivity extends AppCompatActivity {

	ArrayList<SingleAnswareLevelInfo> questions;
	private ListView listView;
	private AllListAdaptor adapter;
	Activity thisActivity;
	TextView txtLevel;

	RelativeLayout relBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_level);
		thisActivity = this;

		relBack = (RelativeLayout) findViewById(R.id.relBack);
		txtLevel = (TextView) findViewById(R.id.txtLevel);
		txtLevel.setTypeface(MainApplication.getInstance().getAugustSansRegular());
		relBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		questions = new ArrayList<SingleAnswareLevelInfo>();

		fetchGkNews();

	}
	
	
	public void setAllValue(){
		
		listView = (ListView) findViewById(R.id.list);
		listView.setCacheColorHint(Color.TRANSPARENT);

		adapter = new AllListAdaptor(this, questions);
		listView.setAdapter(adapter);

		registerForContextMenu(listView);

		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				Intent intent = new Intent(getBaseContext(),SingleAnsQuizActivity.class);
				intent.putExtra("level_no",position);
				intent.putExtra("level_name",questions.get(position).getLevelName());
				startActivity(intent);
			}
		});
	}

	private void fetchGkNews() {
		String url = getResources().getString(R.string.get_level_info);


		// Volley's json array request object
		StringRequest postRequest = new StringRequest(Request.Method.GET, url,
				new Response.Listener<String>() {
					JSONArray jsonResponse;

					@Override
					public void onResponse(String response) {

						if (response != null && response.trim().length() >= 3 && !response.equalsIgnoreCase("[]")) {
							try {
									jsonResponse = new JSONArray(response);
									Log.i("INFL","ArrayLength: "+jsonResponse.length());
									for (int i = 0; i < jsonResponse.length(); i++) {
										JSONObject json = (JSONObject) jsonResponse.get(i);
										final Gson gson = new Gson();
										SingleAnswareLevelInfo prathnaSabha = gson.fromJson(String.valueOf(json), SingleAnswareLevelInfo.class);
										questions.add(prathnaSabha);

									}
									setAllValue();
									adapter = new AllListAdaptor(thisActivity, questions);
									listView.setAdapter(adapter);

							} catch (JSONException e) {
								e.printStackTrace();
								Toast.makeText(getApplicationContext(), "There are no any data",
										Toast.LENGTH_SHORT).show();
							}
						}else{
							Toast.makeText(getApplicationContext(), "There are no any data",
									Toast.LENGTH_SHORT).show();
						}

						if(adapter!=null) {
							adapter.notifyDataSetChanged();
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

	
}
