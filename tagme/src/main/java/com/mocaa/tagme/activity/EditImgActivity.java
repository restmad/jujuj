package com.mocaa.tagme.activity;

import java.io.File;
import java.util.ArrayList;

import com.mocaa.tagme.R;
import com.mocaa.tagme.db.TagDao;
import com.mocaa.tagme.db.UserPref;
import com.mocaa.tagme.dialog.SelectImageDialog;
import com.mocaa.tagme.dialog.SelectTagDialog;
import com.mocaa.tagme.dialog.SelectTagDialog.BtnListener;
import com.mocaa.tagme.download.ImageLoaderImpl;
import com.mocaa.tagme.entity.Point;
import com.mocaa.tagme.entity.Tag;
import com.mocaa.tagme.global.GlobalDefs;
import com.mocaa.tagme.util.BitmapUtil;
import com.mocaa.tagme.util.FileUtil;
import com.mocaa.tagme.util.ImageSelector;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mocaa.tagme.view.TagView;
import com.soundcloud.android.crop.Crop;

/**
 * 
 * @author Shinado ss
 *
 */
public class EditImgActivity extends Activity{

//	private int mEditingTagId;
	private Tag mTag;
	private int mPId;
	private Point mCurrentTagPoint;
	@SuppressWarnings("deprecation")
	private AbsoluteLayout holderView;
	private ArrayList<View> dotArray = new ArrayList<View>();
	private EditText titleEt;
	private ImageView imageIv;
	private TagDao dao;
	private View titleBar, deleteBar, deleteIcon, deleteBtns;
	private View locView, hintView;
	private int colorRed, colorDelete;
	private int mDeleteId;
	private int mTitleHeight;
	private ImageSelector imgSelector;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_img);
		dao = new TagDao(this);
		imgSelector = new ImageSelector(this);
		
		mTitleHeight = (int) getResources().getDimension(R.dimen.title_bar_height);
		holderView = (AbsoluteLayout) findViewById(R.id.edit_img_group);
		resetTagGroupWidth();
		imageIv = (ImageView) findViewById(R.id.edit_img_img);
		resetImageWidth();
		titleEt = (EditText) findViewById(R.id.edit_img_title);
		titleBar = findViewById(R.id.edit_img_title_bar);
		deleteBar = findViewById(R.id.edit_img_delete);
		deleteIcon = findViewById(R.id.edit_img_delete_icon);
		deleteBtns = findViewById(R.id.edit_img_delete_btns);
		locView = findViewById(R.id.edit_img_loc_group);
		
		titleBar.setOnTouchListener(interceptListener);
		deleteBtns.setOnTouchListener(interceptListener);
		
		colorRed = getResources().getColor(R.color.delete_red);
		colorDelete = getResources().getColor(R.color.delete_dark);
		
		setOnTouchListener();
		setHintView();
		
		Object tag = null;
		if(savedInstanceState != null){
			mPId = savedInstanceState.getInt(GlobalDefs.EXTRA_PARENT_ID, -1);
			tag = savedInstanceState.getParcelable(GlobalDefs.EXTRA_TAG);
		}else{
			Intent intent = this.getIntent();
			mPId = intent.getIntExtra(GlobalDefs.EXTRA_PARENT_ID, -1);
			tag = intent.getParcelableExtra(GlobalDefs.EXTRA_TAG);
		}
		init(tag);
		System.out.println("init:"+mTag.getTitle());
		System.out.println("EIA onCreate");
	}
	
	private void init(Object tag){
		ImageButton nextBtn = (ImageButton)findViewById(R.id.edit_img_next_btn);
		if(tag != null){  
			mTag = (Tag)tag;
			Bitmap image = mTag.getImg(this);
			imageIv.setImageBitmap(image);
			titleEt.setText(mTag.getTitle());
			initTags();
		}else{
			mTag = new Tag(Tag.TYPE_IMAGE, UserPref.getUserAccount(this));
		}
		if(mPId < 0){
			nextBtn.setImageResource(R.drawable.ic_post_white);
		}else{
			nextBtn.setImageResource(R.drawable.ic_done);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInsanceState){
		super.onSaveInstanceState(savedInsanceState);
		System.out.println("EIA onSaveInstanceState");
		savedInsanceState.putInt(GlobalDefs.EXTRA_PARENT_ID, mPId);
		savedInsanceState.putParcelable(GlobalDefs.EXTRA_TAG, mTag);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInsanceState){
		super.onRestoreInstanceState(savedInsanceState);
		System.out.println("EIA onRestoreInstanceState");
		mPId = savedInsanceState.getInt(GlobalDefs.EXTRA_PARENT_ID, -1);
		Object tag = savedInsanceState.getParcelable(GlobalDefs.EXTRA_TAG);
		init(tag);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		System.out.println("EIA onResume");
	}
	
	private void setHintView(){

//		bFirstTime = new UserPref(this).isFirstTime();
//		if(bFirstTime){
			hintView = findViewById(R.id.edit_img_hint);
			hintView.setVisibility(View.VISIBLE);
			LayoutParams params = imageIv.getLayoutParams();
			params.width = GlobalDefs.getScreenWidth();
			params.height = params.width;
			hintView.setLayoutParams(params);
//		}
		
	}
	
	private void resetTagGroupWidth(){
		LayoutParams params = holderView.getLayoutParams();
		params.width = GlobalDefs.getScreenWidth();
		params.height = params.width + (int) getResources().getDimension(R.dimen.title_bar_height);
		holderView.setLayoutParams(params);
	}

	private void resetImageWidth(){
		LayoutParams params = imageIv.getLayoutParams();
		params.width = GlobalDefs.getScreenWidth();
		params.height = params.width;
		imageIv.setLayoutParams(params);
	}
	
	private OnTouchListener interceptListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return true;
		}
	};
	
	private void initTags(){
		if(mTag != null){
			ArrayList<Tag> tags = mTag.getTags();
			for(Tag t:tags){
				generateTag(t);
			}
		}
	}
	
	private void setOnTouchListener(){
		holderView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
//				if(bFirstTime){
//					bFirstTime = false;
					hintView.setVisibility(View.GONE);
//				}
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					int x = (int) event.getX();
					int y = (int) event.getY();
					startCounting(x, y);
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					stopCounting();
					break;
				}
				return true;
			}

			private final long CLICK_TIME = 200;
			private long mStartTime;
			private boolean counting = false;
			
			private void stopCounting(){
				counting = false;
			}
			private void startCounting(final int x, final int y){
				mStartTime = System.currentTimeMillis();
				counting = true;
				new Thread(){
					public void run(){
						while(counting){
						}
						if(System.currentTimeMillis() - mStartTime <= CLICK_TIME){
							Message msg = new Message();
							msg.what = WHAT_GENERATE;
							Point pt = new Point(x, y);
							msg.obj = pt;
							mHandler.sendMessage(msg);
						}
					}
				}.start();
			}
		});
	}
	
	private final int WHAT_GENERATE = 1;
	private final int WHAT_CLICK = 2;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			case WHAT_GENERATE:
				Point pt = (Point) msg.obj;
				showDialog();
				generateDot(pt.x, pt.y);
				mCurrentTagPoint = pt;
				break;
			case WHAT_CLICK:
				TextView v = (TextView) msg.obj;
				Tag tag = (Tag) v.getTag();
				Intent intent = null;
				switch(tag.getType()){
				case Tag.TYPE_IMAGE:
					intent = new Intent(EditImgActivity.this, EditImgActivity.class);
					break;
				case Tag.TYPE_TEXT:
				case Tag.TYPE_LOCATION:
					intent = new Intent(EditImgActivity.this, EditTextActivity.class);
					break;
				}
				intent.putExtra(GlobalDefs.EXTRA_PARENT_ID, mTag.getId());
				intent.putExtra(GlobalDefs.EXTRA_TAG, tag);
				startActivityForResult(intent, REQUEST_EDIT_TAG);
				System.out.println("click tag:"+tag.getId());
//				mEditingTagId = v.getId();
				break;
			}
		}
	};
	
	private Animation getDotAnim(){
		Animation anim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_PARENT, -1, 
				Animation.RELATIVE_TO_SELF, 0.1f);
		anim.setFillAfter(false);
		anim.setInterpolator(new AccelerateDecelerateInterpolator());
		anim.setDuration(300);
		anim.setStartOffset(200);
		return anim;
	}
	
	private void generateDot(int x, int y){
		View dot = LayoutInflater.from(this).inflate(R.layout.layout_dot, null);
		@SuppressWarnings("deprecation")
		AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
				28, 28, x-14, y-14);
		dot.setLayoutParams(params);
		holderView.addView(dot);
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.scale_in_out);
		anim.setInterpolator(new DecelerateInterpolator(2.6f));
		dot.findViewById(R.id.layout_dot_frg).startAnimation(anim);
		dotArray.add(dot);
		dot.startAnimation(getDotAnim());
	}

	private void generateTag(final Tag vo){
        final TagView tag = new TagView(this);
        tag.resetTag(vo, true);
		tag.getView().getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			private boolean flag = true;

			@Override
			public void onGlobalLayout() {
				if(flag){
					if(mCurrentTagPoint != null){
						resetLayout(tag);
					}
					flag = false;
				}
			}
		});
		tag.getTextView().setOnTouchListener(new OnTouchListener() {

			private int startX, startY;
			private Animation showTitleAnim, hideTitleAnim, showDeleteAnim, hideDeleteAnim,
				touchDownAnim, touchUpAnim, shakingAnim;
			private boolean isDeleting = false;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				System.out.println("touch in editview");
				int x = (int) event.getRawX();
				int y = (int) event.getY();
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					ViewGroup.LayoutParams params = v.getLayoutParams();
					if(params instanceof AbsoluteLayout.LayoutParams){
						startX = x - ((AbsoluteLayout.LayoutParams)params).x;
						startY = y;
					}else{
						startX = x - ((AbsoluteLayout.LayoutParams)
								tag.getView().getLayoutParams()).x;
						startY = y;
					}
					startCounting(tag);
					onTouchDown(tag);
					break;
				case MotionEvent.ACTION_MOVE:
					moveTag(tag, x-startX, y-startY);
					break;
				case MotionEvent.ACTION_UP:
					stopCounting();
					onTouchUp(tag);
					break;
				case MotionEvent.ACTION_CANCEL:
					onTouchUp(tag);
					break;
				}
				return true;
			}

			private void touchDownTag(View v){
				if(touchDownAnim == null){
					touchDownAnim = new ScaleAnimation(
							1, 0.9f,
							1, 0.9f,
							Animation.RELATIVE_TO_SELF, 0.5f,
							Animation.RELATIVE_TO_SELF, 0.5f);
					touchDownAnim.setFillAfter(true);
					touchDownAnim.setDuration(80);
				}
				v.startAnimation(touchDownAnim);
			}

			private void touchUpTag(View v){
				if(touchUpAnim == null){
					touchUpAnim = new ScaleAnimation(
						0.9f, 1,
						0.9f, 1,
						Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.5f);
					touchUpAnim.setFillAfter(true);
					touchUpAnim.setDuration(80);
				}
				v.startAnimation(touchUpAnim);
			}

			private void showTitleBar(){
				if(showTitleAnim == null){
					showTitleAnim = new TranslateAnimation(
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, -1,
							Animation.RELATIVE_TO_SELF, 0);
					showTitleAnim.setFillAfter(true);
					showTitleAnim.setDuration(200);
				}
				titleBar.startAnimation(showTitleAnim);

				if(hideDeleteAnim == null){
					hideDeleteAnim = new TranslateAnimation(
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, 1);
					hideDeleteAnim.setFillAfter(true);
					hideDeleteAnim.setDuration(200);
				}
				deleteBar.startAnimation(hideDeleteAnim);
			}

			private void hideTitleBar(){
				if(hideTitleAnim == null){
					hideTitleAnim = new TranslateAnimation(
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, -1);
					hideTitleAnim.setFillAfter(true);
					hideTitleAnim.setDuration(200);
				}
				titleBar.startAnimation(hideTitleAnim);

				if(showDeleteAnim == null){
					showDeleteAnim = new TranslateAnimation(
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, 1,
							Animation.RELATIVE_TO_SELF, 0);
					showDeleteAnim.setFillAfter(true);
					showDeleteAnim.setDuration(200);
				}
				deleteBar.startAnimation(showDeleteAnim);
			}

			private void onTouchDown(TagView tagView){
				touchDownTag(tagView.getTextView());
				hideTitleBar();
			}

			private void onTouchUp(TagView tagView){
				View v = tagView.getView();
				if(isDeleting){
					mDeleteId = tagView.getId();
					deleteBtns.setVisibility(View.VISIBLE);
					deleteBtns.findViewById(R.id.edit_img_delete_done)
							.startAnimation(showDeleteAnim);
					deleteBtns.findViewById(R.id.edit_img_delete_cancel)
							.startAnimation(showDeleteAnim);
					v.clearAnimation();
					v.setVisibility(View.GONE);
				}else{
					mDeleteId = -1;
					showTitleBar();
					touchUpTag(tagView.getTextView());
					saveTagLocation(tagView);
				}
				isDeleting = false;
			}

			private void saveTagLocation(TagView tagView){
				View v = tagView.getView();
				Tag tag = (Tag) tagView.getTextView().getTag();
				if(tag != null){
					@SuppressWarnings("deprecation")
					AbsoluteLayout.LayoutParams params =
							(AbsoluteLayout.LayoutParams) v.getLayoutParams();
					Point pt = new Point(params.x, params.y - mTitleHeight);
					tag.setLocation(pt);
					System.out.println("save tag:"+ tag.getTitle()+" " +
							params.x +","+ (params.y - mTitleHeight));
				}
			}

			@SuppressWarnings("deprecation")
			private void moveTag(TagView tagView, int x, int offsetY){
                View v = tagView.getView();
				onDirectionChange(tagView);
				AbsoluteLayout.LayoutParams params = 
						(AbsoluteLayout.LayoutParams) v.getLayoutParams();
				System.out.println("on the move:"+x);
				params.x = x;
				params.y += offsetY;
                v.setLayoutParams(params);
				onDeleting(params.y);
			}

			private void onDeleting(int y){
				if(y < deleteBar.getHeight()){
					if(isDeleting == false){
						isDeleting = true;

						deleteBar.setBackgroundColor(colorDelete);
						if(shakingAnim == null){
							shakingAnim = new android.view.animation.RotateAnimation(
									-6, 6,
									Animation.RELATIVE_TO_SELF, 0.5f,
									Animation.RELATIVE_TO_SELF, 0.5f);
							shakingAnim.setRepeatCount(3);
							shakingAnim.setDuration(50);
							shakingAnim.setFillAfter(false);
						}
						deleteIcon.startAnimation(shakingAnim);
					}
				}else{
					if(isDeleting == true){
						isDeleting = false;
						deleteBar.setBackgroundColor(colorRed);
					}
				}
			}

			private void onDirectionChange(TagView tagView){
                View v = tagView.getView();
				Tag tag = (Tag) tagView.getTextView().getTag();
				int width = v.getWidth();
				AbsoluteLayout.LayoutParams params =
						(AbsoluteLayout.LayoutParams) v.getLayoutParams();
				if(tag.getDirection() == Tag.DIR_LEFT){
					if(params.x < 0){
						tag.setDirection(Tag.DIR_RIGHT);
						tagView.resetTagDirection(vo);
//						startX += width;
						System.out.println("1 change direction:"+params.x);
					}
				}else{
					if(params.x + width > GlobalDefs.getScreenWidth()){
						tag.setDirection(Tag.DIR_LEFT);
						tagView.resetTagDirection(vo);
//						startX -= width;
						System.out.println("2 change direction:"+params.x);
					}
				}
			}
			private final long CLICK_TIME = 200;
			private long mStartTime;
			private boolean counting = false;

			private void stopCounting(){
				counting = false;
			}

			private void startCounting(final TagView tagView){
				mStartTime = System.currentTimeMillis();
				counting = true;
				new Thread(){
					public void run(){
						while(counting){
						}
						if(System.currentTimeMillis() - mStartTime <= CLICK_TIME){
							Message msg = new Message();
							msg.what = WHAT_CLICK;
							msg.obj = tagView.getTextView();
							mHandler.sendMessage(msg);
						}
					}
				}.start();
			}
		});

		holderView.addView(tag.getView());
	}
	
    @SuppressWarnings("deprecation")
	private void resetLayout(TagView tagView){
		System.out.println("reset layout");
        View v = tagView.getView();
		int width = v.getWidth();
		int height = v.getHeight();
		Tag tag = (Tag) tagView.getTextView().getTag();
		if(mCurrentTagPoint.x < GlobalDefs.getScreenWidth()/2){
			tag.setDirection(Tag.DIR_RIGHT);
			System.out.println("5 change direction:"+mCurrentTagPoint.x);
			tagView.resetTagDirection(tag);
			int drawY = mCurrentTagPoint.y - height/2;
			int drawX = mCurrentTagPoint.x - width;
			AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, drawX, drawY);
			v.setLayoutParams(params);
		}else{
			tag.setDirection(Tag.DIR_LEFT);
			System.out.println("6 change direction:"+mCurrentTagPoint.x);
			tagView.resetTagDirection(tag);
			int drawY = mCurrentTagPoint.y - height/2;
			int drawX = mCurrentTagPoint.x;
			AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, drawX, drawY);
			v.setLayoutParams(params);
		}
		checkDirectionChange(tagView);
	}

	private void checkDirectionChange(TagView tagView){
        View v = tagView.getView();
		Tag tag = (Tag) tagView.getTextView().getTag();
		int width = v.getWidth();
		AbsoluteLayout.LayoutParams params = 
				(AbsoluteLayout.LayoutParams) v.getLayoutParams();
		if(tag.getDirection() == Tag.DIR_LEFT){
			if(params.x + width > GlobalDefs.getScreenWidth()){
				tag.setDirection(Tag.DIR_RIGHT);
				tagView.resetTagDirection(tag);
				params.x -= width;
				v.setLayoutParams(params);
				System.out.println("3 change direction:"+params.x);
			}
		}else{
			if(params.x < 0){
				tag.setDirection(Tag.DIR_LEFT);
				tagView.resetTagDirection(tag);
				params.x += width;
				v.setLayoutParams(params);
				System.out.println("4 change direction:"+params.x);
			}
		}
	}

	public void showDialog(){
		@SuppressWarnings("static-access")
		SelectTagDialog dailog = SelectTagDialog
				.createDialog(this)
				.setOnListener(new BtnListener() {
					@Override
					public void onClicked(int id) {
						Intent intent = null;
						switch(id){
						case Tag.TYPE_LOCATION:
						case Tag.TYPE_TEXT:
							intent = new Intent(EditImgActivity.this, 
									EditTextActivity.class);
							intent.putExtra(GlobalDefs.EXTRA_PARENT_ID, mTag.getId());
							intent.putExtra(GlobalDefs.EXTRA_TYPE, id);
							startActivityForResult(intent, REQUEST_NEW_TAG);
							break;
						case Tag.TYPE_IMAGE:
							imgSelector.selectGallery();
							break;
						}
					}
				}); 
		dailog.setCanceledOnTouchOutside(true);
		dailog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
     
				clearDots();
			}
		});
		dailog.show();  
	}         
  
	private final int REQUEST_EDIT_TAG = 3;
	private final int REQUEST_NEW_TAG = 4;

	private void clearDots(){
		for(View v:dotArray){
			v.clearAnimation();
			holderView.removeView(v);
		}
		dotArray.clear();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
		System.out.println("on result in edit image:"+requestCode+","+resultCode);
		switch(requestCode){
		
		case REQUEST_NEW_TAG:
			if(resultCode == RESULT_OK) {  
				Tag tag = (Tag) data.getParcelableExtra(GlobalDefs.EXTRA_TAG);
				if(tag != null){
					newTag(tag);
				}
			}  
			break;
		case REQUEST_EDIT_TAG:
			if(resultCode == RESULT_OK) {  
				Tag tag = (Tag) data.getParcelableExtra(GlobalDefs.EXTRA_TAG);
				setTagById(tag);
				System.out.println("edit tag:"+tag.getId());
				View v =  holderView.findViewById(tag.getId());
				TextView textview = (TextView) v.findViewById(R.id.tag_text);
				if(textview != null){
					textview.setText(tag.getTitle());
				}
			}
			break;
		}

		imgSelector.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Crop.REQUEST_CROP) {
	        handleCrop(resultCode, data);
	    }
		super.onActivityResult(requestCode, resultCode, data);  
	} 

	private void handleCrop(int resultCode, Intent result) {
	    if (resultCode == Activity.RESULT_OK) {
	        
	    	Tag tag = new Tag(Tag.TYPE_IMAGE, UserPref.getUserAccount(this));
	        tag.setImgUri(imgSelector.getImgUri());
		    
	        Intent intent = new Intent(this, EditImgActivity.class);
		    intent.putExtra(GlobalDefs.EXTRA_PARENT_ID, mTag.getId());
		    intent.putExtra(GlobalDefs.EXTRA_TAG, tag);
		    startActivityForResult(intent, REQUEST_NEW_TAG);
	    } else if (resultCode == Crop.RESULT_ERROR) {
	        Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
	    }
	}

	private Tag getTagById(int id){
		for(Tag tag:mTag.getTags()){
			if(tag.getId() == id){
				return tag;
			}
		}
		return null;
	}
	
	private void setTagById(Tag target){
		if(target == null){
			return;
		}
		for(Tag tag:mTag.getTags()){
			if(tag.getId() == target.getId()){
				tag.copy(target);
			}
		}
		dao.editTag(target);
	}   
	
	private void newTag(Tag tag){
		System.out.println("new");
		System.out.println("parent:"+tag.getTitle());
		testTags(tag);
		tag.setLocation(mCurrentTagPoint);
		dao.setTagLocation(tag, mCurrentTagPoint);
		mTag.getTags().add(tag);
		generateTag(tag);
	}
	
	private void testTags(Tag tag){
		for(Tag t:tag.getTags()){
			System.out.println("child:"+t.getTitle());
			testTags(t);
		}
	}
	
	public void next(View v){
		String title = titleEt.getText().toString().trim();
		if(title.equals("")){
			Toast.makeText(this, R.string.no_title, Toast.LENGTH_LONG).show();
			return;
		}
		mTag.setTitle(title);
		if(mTag.getId() <= 0){
			if(mPId <= 0){
				/*
				 * discard since it's saved in AddingTagView already
				 */
//				mTag.setUserAccount(UserPref.getUserAccount(this));
//				dao.saveTagWithId(mTag);
			}else{
				dao.saveTagWithParent(mPId, mTag);
			}
		}else{
			dao.editTag(mTag);
		}
		System.out.println("next:");
		System.out.println("parent:"+mTag.getTitle());
		testTags(mTag);
		Intent intent = new Intent();
		intent.putExtra(GlobalDefs.EXTRA_TAG, mTag);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	public void back(View v){
		/*
		 * 
		 */
		Intent intent = new Intent();
		intent.putExtra(GlobalDefs.EXTRA_TAG, mTag);
		setResult(RESULT_CANCELED, intent);
		finish();
	}
	
	private Animation showTitleAnim, hideDeleteAnim;

	private void showTitleBar(){
		
		if(showTitleAnim == null){
			showTitleAnim = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0, 
					Animation.RELATIVE_TO_SELF, 0, 
					Animation.RELATIVE_TO_SELF, -1,
					Animation.RELATIVE_TO_SELF, 0);
			showTitleAnim.setFillAfter(true);
			showTitleAnim.setDuration(200);
		}
		titleBar.startAnimation(showTitleAnim);
		
		if(hideDeleteAnim == null){
			hideDeleteAnim = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0, 
					Animation.RELATIVE_TO_SELF, 0, 
					Animation.RELATIVE_TO_SELF, 0,
					Animation.RELATIVE_TO_SELF, 1);
			hideDeleteAnim.setFillAfter(true);
			hideDeleteAnim.setDuration(200);
		}
		deleteBar.startAnimation(hideDeleteAnim);
	}
	
	public void delete_done(View v){
		deleteTag();
		removeTagView();
		deleteBtns.setVisibility(View.GONE);
		shakingDeleteIcon();
		mDeleteId = -1;
	}
	
	private void removeTagView(){
		View child = holderView.findViewById(mDeleteId);
		holderView.removeView(child);
	}
	
	private void deleteTag(){
		Tag tag = getTagById(mDeleteId);
		mTag.getTags().remove(tag);
		dao.deleteTag(tag);
	}
	
	private void shakingDeleteIcon(){
		Animation anim = new android.view.animation.RotateAnimation(
				-6, 6,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		anim.setRepeatCount(1);
		anim.setDuration(50);
		anim.setFillAfter(false);
		anim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				showTitleBar();
			}
		});
		deleteIcon.startAnimation(anim);
	}
	
	public void delete_cancel(View v){
		moveTagBack();
		deleteBtns.setVisibility(View.GONE);
		showTitleBar();
		mDeleteId = -1;
	}
	
	private void moveTagBack(){
		//TODO
		Tag tag = getTagById(mDeleteId);
		View view = holderView.findViewById(mDeleteId);
		view.setVisibility(View.VISIBLE);
		@SuppressWarnings("deprecation")
		AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				tag.getLocation().x, tag.getLocation().y);
		view.setLayoutParams(params);
	}
	
	@Override
	public boolean onKeyDown(int keyCode,KeyEvent event){
		switch(keyCode){
			case KeyEvent.KEYCODE_BACK:
				back(null);
				return super.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}
}
