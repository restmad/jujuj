package com.mocaa.tagme.activity;

import java.util.ArrayList;

import com.mocaa.tagme.R;
import com.mocaa.tagme.adapter.SearchingAdapter;
import com.mocaa.tagme.db.UserPref;
import com.mocaa.tagme.download.AsyncLoader;
import com.mocaa.tagme.download.AsyncLoader.NetworkCallback;
import com.mocaa.tagme.entity.User;
import com.mocaa.tagme.global.GlobalDefs;
import com.mocaa.tagme.transport.SearchUserRequest;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SearchingActivity extends Activity{
	
	private AsyncLoader aLoader;
	private SearchingAdapter adapter;
	private User mUser;
	private EditText inputEt;
	private InputMethodManager inputMethodManager;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searching);
		
		aLoader = new AsyncLoader(this);
		inputMethodManager = 
				(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		mUser = getIntent().getParcelableExtra(GlobalDefs.EXTRA_USER);
		if(mUser == null){
			mUser = new UserPref(this).getMyProfile();
		}
		
		setListView();
	}
	
	private void setListView(){
		ListView listview = (ListView) findViewById(R.id.activity_searching_result);
		adapter = new SearchingAdapter(this, mUser);
		listview.setAdapter(adapter);
		
		inputEt = (EditText) findViewById(R.id.activity_searching_input);
		inputEt.setOnEditorActionListener(new OnEditorActionListener(){
   
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if(actionId == EditorInfo.IME_NULL){
					hideInput();
					aLoader.downloadMessage(new SearchUserRequest(), null, 
							new String[]{v.getText().toString().trim()}, 
							new NetworkCallback(){

								@Override
								public void onLoaded(Object obj) {
									ArrayList<String> result = (ArrayList<String>) obj;
									result.remove(mUser.getUserAccount());
									adapter.setResult(result);
								}
						
					});
					
				}
				return false;
			}
			
		});
		
		inputEt.requestFocus();
		showInput();
	}
	
	private void showInput(){
		inputMethodManager.showSoftInput(inputEt, 0);
	}

	private void hideInput(){
		inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken() ,
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public void back(View v){
		finish();
	}
}
