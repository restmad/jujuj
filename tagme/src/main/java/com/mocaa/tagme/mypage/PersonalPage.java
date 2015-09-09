package com.mocaa.tagme.mypage;

import java.util.HashMap;
import java.util.TreeSet;

import com.mocaa.tagme.R;
import com.mocaa.tagme.activity.PersonalPageActivity;
import com.mocaa.tagme.activity.ViewTagActivity;
import com.mocaa.tagme.db.TagDao;
import com.mocaa.tagme.db.UserDao;
import com.mocaa.tagme.download.AsyncLoader;
import com.mocaa.tagme.download.AsyncLoader.ImageCallback;
import com.mocaa.tagme.download.AsyncLoader.NetworkCallback;
import com.mocaa.tagme.entity.Tag;
import com.mocaa.tagme.entity.User;
import com.mocaa.tagme.friends.MyFollow;
import com.mocaa.tagme.friends.UserPool;
import com.mocaa.tagme.friends.MyFollow.OnFollowUpdateListener;
import com.mocaa.tagme.global.GlobalDefs;
import com.mocaa.tagme.mypage.MyPageView.OnTagChangedListener;
import com.mocaa.tagme.transport.Connection;
import com.mocaa.tagme.transport.DeleteTag;
import com.mocaa.tagme.transport.GetPersonalTag;
import com.mocaa.tagme.transport.GetTagInfo;
import com.mocaa.tagme.transport.GetTagTransport;
import com.mocaa.tagme.transport.GetUserTransport;
import com.mocaa.tagme.view.MainView;
import com.mocaa.tagme.view.ObservableScrollView;
import com.mocaa.tagme.view.ObservableScrollView.ScrollViewListener;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class PersonalPage {
	
	private final int MIN_TAG_COUNT = 4;
	private Context context;
	private User mUser;
	private UserDao userDao;
	private TreeSet<Tag> mTags = new TreeSet<Tag>();
	private TagDao tagDao;
	private MyPageAdapter adapter;
	private ImageView portraitIv, genderView;
	private TextView nameTv, livingTv, tagTv, followingTv, followersTv, likesTv;
	private View progressView;//, lineView;
//    ObservableScrollView scrollview = null;
	private GridView gridview;
	private final int NUM_COLUMN = 2;
	private AsyncLoader aLoader;
	private boolean resetHeight = true;
	private int spacing;
	
	public PersonalPage(Context context, User u){
		this.context = context;
		this.mUser = u;
		userDao = new UserDao(context);
		tagDao = new TagDao(context);
		aLoader = new AsyncLoader(context);
	}

	
	public void setProgressBar(View progressView){
		this.progressView = progressView;
	}
	
//	public void setLineView(View v){
//		lineView = v;
//		lineView.setBackgroundColor(context.getResources().getColor(R.color.bcg_theme));
//	}
	
	private void syncUserProfile(){
		aLoader.downloadMessage(new GetUserTransport(), null,
				new String[]{mUser.getUserAccount()}, new NetworkCallback(){

					@Override
					public void onLoaded(Object obj) {
						User newUser = (User) obj;
						String oldPortraitUrl = mUser.getPortraitUrl();
						mUser = newUser;
						userDao.updateUser(mUser);
						setView(oldPortraitUrl);
					}
			
		});
	}
	
	public void findAndSetUserInfo(View v){

		gridview = (GridView) v.findViewById(R.id.personal_page_gridview);
		spacing = (int) (5*GlobalDefs.getScreenDensity());
		gridview.setHorizontalSpacing(spacing);
		gridview.setVerticalSpacing(spacing);
		gridview.setPadding(spacing, spacing, spacing, 0);
		int width = (int) ((GlobalDefs.getScreenWidth() - spacing*(NUM_COLUMN+1)) / (float)NUM_COLUMN);
		adapter = new MyPageAdapter(context, mTags, width);
		gridview.setAdapter(adapter);
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Tag tag = ((Tag) mTags.toArray()[position]);
				Intent intent = new Intent(context, ViewTagActivity.class);
				intent.putExtra(GlobalDefs.EXTRA_TAG, tag);
				intent.putExtra(GlobalDefs.EXTRA_USER, mUser);
				((Activity) context).startActivityForResult(intent, MainView.REQUEST_VIEW_TAG);
			}
		});
		
//		gridview.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener(){
//			@Override
//			public void onGlobalLayout() {
//				if(!resetHeight){
//					return;
//				}
//				if(gridview.getChildCount() > 0){
//					View v = gridview.getChildAt(0);
//					if(v != null){
//						int numColumn = 2;
//						int height = (v.getHeight() + spacing) * 
//								(mTags.size()/numColumn + mTags.size()%numColumn)
//								+spacing;
//						ViewGroup.LayoutParams params = gridview.getLayoutParams();
//						params.height = height;
//						gridview.setLayoutParams(params);
//						GlobalDefs.o("reset height in personal page:"+height);
//					}
//				}
//				
//				resetHeight = false;
//			}
//		});

        genderView = (ImageView) v.findViewById(R.id.layout_user_gender);
		portraitIv = (ImageView) v.findViewById(R.id.layout_user_portrait);
		resetImageView(portraitIv);
		nameTv = (TextView) v.findViewById(R.id.layout_user_user_name);
		livingTv = (TextView) v.findViewById(R.id.layout_user_living);
		tagTv = (TextView) v.findViewById(R.id.layout_user_tags);
		followersTv = (TextView) v.findViewById(R.id.layout_user_followers);
		followingTv = (TextView) v.findViewById(R.id.layout_user_follows);
		likesTv = (TextView) v.findViewById(R.id.layout_user_likes);

//		if(v instanceof ObservableScrollView){
//			scrollview = (ObservableScrollView) v;
//		}else{
//			scrollview = (ObservableScrollView) v.findViewById(R.id.personal_page_scrollview);
//		}
//        scrollview.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                scrollview.pageScroll(ScrollView.FOCUS_UP);
//            }
//        });
		
		setView(mUser.getPortraitUrl());
		syncUserProfile();
	}

	private void resetImageView(View v){
		ViewGroup.LayoutParams params = v.getLayoutParams();
        int width = GlobalDefs.getScreenWidth();
		params.height = width;
		params.width = width;
		v.setLayoutParams(params);
	}

	private void loadPortrait(){
		aLoader.downloadImage(mUser.getPortraitUrl(), true, new ImageCallback(){

			@Override
			public void onImageLoaded(Bitmap bitmap, String imageUrl) {
				if(bitmap != null){
					portraitIv.setImageBitmap(bitmap);
				}
			}
			
		});
	}
	
	private void setView(String oldPortraitUrl){
		Bitmap portrait = mUser.getPortrait(context);
		if(oldPortraitUrl.equals(mUser.getPortraitUrl())){
			if(portrait != null){
				portraitIv.setImageBitmap(portrait);
			}else{
				portraitIv.setImageResource(R.drawable.portrait_default);
				loadPortrait();
			}
		}
		//need to reupload
		else{
			if(portrait != null){
				portraitIv.setImageBitmap(portrait);
				loadPortrait();
			}else{
				portraitIv.setImageResource(R.drawable.portrait_default);
				loadPortrait();
			}
		}
		
		nameTv.setText(mUser.getUserName());
		livingTv.setText(context.getResources().getString(R.string.living_in) + " "+ 
					mUser.getPlace());
		tagTv.setText(""+mUser.getTags());
		followersTv.setText(""+mUser.getFollower());
		followingTv.setText(""+mUser.getFollowing());
		likesTv.setText(""+mUser.getLikes());
        if(mUser.getGender() == User.MALE){
            genderView.setImageResource(R.drawable.ic_male);
        }else if(mUser.getGender() == User.MALE){
            genderView.setImageResource(R.drawable.ic_female);
        }else{
            genderView.setVisibility(View.GONE);
        }
	}
	
//	public void resetLineView(){
//		if(lineView != null){
//			lineView.setBackgroundColor(context.getResources()
//					.getColor(R.color.bcg_theme_dark));
//		}
//	}

	public void setUser(User u){
		mUser = u;
		Bitmap portrait = u.getPortrait(context);
		if(portrait == null){
			portraitIv.setImageResource(R.drawable.portrait_default);
		}else{
			portraitIv.setImageBitmap(portrait);
		}
		nameTv.setText(u.getUserName());
		livingTv.setText(context.getResources().getString(R.string.living_in) + " " +
				u.getPlace());
		tagTv.setText(""+u.getTags());
		followingTv.setText(""+u.getFollowing());
		followersTv.setText(""+u.getFollower());
		likesTv.setText(""+u.getLikes());
	}

	public void notifyDateSetChanged(){
		adapter.notifyDataSetChanged();
	}
	
	public void loadTags(){
		int id = 1000000;
		if(mTags.size() != 0 && mTags.last() != null){
			id = mTags.last().getServerId();
		}
		// load from local
		TreeSet<Tag> all = tagDao.getMyTags(mUser.getUserAccount(), id);
//		if(all.size() < MIN_TAG_COUNT){
			mTags.addAll(all);
			adapter.notifyDataSetChanged();
			loadMore();
//		}else{
//			syncTagInfo(all);
//			mTags.addAll(all);
//			adapter.notifyDataSetChanged();
//		}
	}

	public void loadMore(){
		new DownloadTask().execute(); 
	}
	
	private boolean isRoading = false;
	
	class DownloadTask extends AsyncTask<Integer, Integer, TreeSet<Tag>>{  
        //锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷诜直锟斤拷遣锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷叱锟斤拷锟较⑹憋拷洌╋拷锟斤拷锟斤拷(publishProgress锟矫碉拷)锟斤拷锟斤拷锟斤拷值 锟斤拷锟斤拷  
          
        @Override  
        protected void onPreExecute() {  
        	isRoading = true;
        	runLoadingAnimation();
            //锟斤拷一锟斤拷执锟叫凤拷锟斤拷  
            super.onPreExecute();  
        }  
          
        @Override  
        protected TreeSet<Tag> doInBackground(Integer... params) {  
            try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
        	

        	TreeSet<Tag> list = new TreeSet<Tag>();
        	
            // load from server
        	GetPersonalTag getTag = new GetPersonalTag();
    		String first = "9999-12-12 12:12:12";
    		if(mTags.size() != 0 && mTags.first() != null){
    			first = mTags.last().getTime();
    		}
    		list = (TreeSet<Tag>) getTag.getMsg(context, new Connection(),
    				null, new String[]{mUser.getUserAccount(), first});
            
            publishProgress();
            return list;  
        }  
  
        @Override  
        protected void onProgressUpdate(Integer... progress) {  




            super.onProgressUpdate(progress);  
        }  
  
        @Override  
        protected void onPostExecute(TreeSet<Tag> result) {  
        	if(result == null){
        		return;
        	}
        	isRoading = false;
        	stopLoadingAnimation();
			mTags.addAll(result);
			GlobalDefs.o("before reset height:"+mTags.size());
			saveAllTags(result);
			adapter.notifyDataSetChanged();
			resetHeight();

            super.onPostExecute(result);  
        }  
    }

	private void saveAllTags(TreeSet<Tag> tags){
		for(Tag tag:tags){
			saveTag(tag);
		}
	}
	
	private void saveTag(Tag tag){
		tagDao.saveTagWithId(tag, true);
		for(Tag child:tag.getTags()){
			tagDao.saveTagWithParent(tag.getId(), child);
		}
	}
	
	private void runLoadingAnimation(){
		if(progressView == null){
			return;
		}
		int length = (int) context.getResources().getDimension(R.dimen.progress_bar);
		setRefreshPrgs(length);
		
		Animation anim = new TranslateAnimation(
				-GlobalDefs.getScreenWidth()/2, 
				GlobalDefs.getScreenWidth()/2+length/2, 0, 0);
		anim.setInterpolator(new DecelerateInterpolator(3));
		anim.setDuration(3600);
		anim.setRepeatCount(Animation.INFINITE);
		anim.setRepeatMode(Animation.RESTART);
		progressView.startAnimation(anim);
		
	}
	
	private void stopLoadingAnimation(){
		if(progressView == null){
			return;
		}
		progressView.clearAnimation();
		setRefreshPrgs(0);
	}
	
	private void setRefreshPrgs(int l){
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) progressView.getLayoutParams();
		params.width = l;
		progressView.setLayoutParams(params);
	}

	private void syncTagInfo(TreeSet<Tag> tags){
		aLoader.downloadMessage(new GetTagInfo(), tags, null, new NetworkCallback(){

			@Override
			public void onLoaded(Object obj) {
				if((Integer)obj > 0){
					adapter.notifyDataSetChanged();
				}
			}
		});
	}

	public void startMoving() {
		adapter.startMoving();
	}

	public void stopMoving() {
		adapter.stopMoving();
	}
	
	public void addTag(Tag tag) {
		mTags.add(tag);
		tagTv.setText(""+mUser.getTags());
		adapter.notifyDataSetChanged();
	}
	
	public Tag getTag(int p){
		Object[] array = mTags.toArray();
		if(p >= array.length || p < 0){
			return null;
		}
		return (Tag) array[p];
	}
	
	public void resetHeight(){
		resetHeight = true;
		gridview.requestLayout();
	}
	
	public void delteTag(final int index, final OnTagChangedListener l){
		final Tag tag = (Tag) mTags.toArray()[index];
		aLoader.downloadMessage(new DeleteTag(), null, 
				new String[]{tag.getServerId()+"", mUser.getUserAccount()}, 
				new NetworkCallback(){

			@Override
			public void onLoaded(Object obj) {
				if((Integer) obj > 0){
					tagDao.deleteTagByServerId(tag);
					mTags.remove(tag);
					adapter.notifyDataSetChanged();
					resetHeight();
					int t = mUser.getTags()-1;
					if(t < 0){
						t = 0;
					}
					mUser.setTags(t);
					userDao.updateTag(mUser);
					tagTv.setText(""+t);
					if(l != null){
						l.onTagChanged(tag, OnTagChangedListener.FLAG_REMOVE);
					}
				}else{
					Toast.makeText(context, R.string.toast_delete_fail, Toast.LENGTH_LONG).show();
				}
			}
			   
		});
	}

	
}
