package com.mocaa.tagme.activity;

import com.mocaa.tagme.R;
import com.mocaa.tagme.db.TagDao;
import com.mocaa.tagme.db.UserPref;
import com.mocaa.tagme.entity.Tag;
import com.mocaa.tagme.global.GlobalDefs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class EditTextActivity extends Activity{
	
	private TagDao dao;
	private Tag mTag;
	private int mPId;
	private EditText titleEt;
//	private EditText contentEt;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_text);
		dao = new TagDao(this);
		titleEt = (EditText)findViewById(R.id.edit_text_title);
		titleEt.setHint(getResources().getString(R.string.hint_title));
//		contentEt = (EditText)findViewById(R.id.edit_text_content);
		Intent intent = this.getIntent();
		Object tag = intent.getParcelableExtra(GlobalDefs.EXTRA_TAG);
		mPId = intent.getIntExtra(GlobalDefs.EXTRA_PARENT_ID, 0);
		if(tag != null){
			mTag = (Tag)tag;
			titleEt.setText(mTag.getTitle());
//			contentEt.setText(mTag.getContent());
		}else{
			int type = intent.getIntExtra(GlobalDefs.EXTRA_TYPE, Tag.TYPE_TEXT);
			mTag = new Tag(type, UserPref.getUserAccount(this));
		}
	}
	
	public void done(View v){
		String title = titleEt.getText().toString();
		mTag.setTitle(title);
//		String content = contentEt.getText().toString();
//		mTag.setContent(content);
		if(mTag.getId() <= 0){
			dao.saveTagWithParent(mPId, mTag);
		}else{
			dao.editTag(mTag);
		}
		Intent data = new Intent();
		data.putExtra(GlobalDefs.EXTRA_TAG, mTag);
		setResult(RESULT_OK, data);
		finish();
	}

	public void back(View v){
		finish();
	}

}
