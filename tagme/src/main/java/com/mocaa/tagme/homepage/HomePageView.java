package com.mocaa.tagme.homepage;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import com.mocaa.tagme.R;
import com.mocaa.tagme.db.LikeDao;
import com.mocaa.tagme.db.TagDao;
import com.mocaa.tagme.db.UserDao;
import com.mocaa.tagme.db.UserPref;
import com.mocaa.tagme.dialog.LoadingDialog;
import com.mocaa.tagme.download.AsyncLoader;
import com.mocaa.tagme.download.AsyncLoader.ImageCallback;
import com.mocaa.tagme.download.AsyncLoader;
import com.mocaa.tagme.download.AsyncLoader.NetworkCallback;
import com.mocaa.tagme.download.ImageLoaderImpl;
import com.mocaa.tagme.entity.Tag;
import com.mocaa.tagme.entity.User;
import com.mocaa.tagme.global.GlobalDefs;
import com.mocaa.tagme.mypage.MyPageView;
import com.mocaa.tagme.transport.Connection;
import com.mocaa.tagme.transport.FollowRequest;
import com.mocaa.tagme.transport.GetTagInfo;
import com.mocaa.tagme.transport.GetTagTransport;
import com.mocaa.tagme.transport.GetUserTransport;
import com.mocaa.tagme.view.MainView;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalFocusChangeListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class HomePageView extends AddingTagView{
	
	private TagDao tagDao;
	private UserDao userDao;
	private View view;
	private HomePageAdapter adapter;
	//save user message
//	private HashMap<String, User> userPool = new HashMap<String, User>();
	private TreeSet<Tag> tags = new TreeSet<Tag>();
	private AsyncLoader aLoader;
	
	private View progressView;//, logoView;
	private int topMargin;

	public HomePageView(Context context, FrameLayout root, View titleBar, User u) {
		super(context, root, titleBar, u);
		progressView = titleBar.findViewById(R.id.main_loading_bar);
//		logoView = titleBar.findViewById(R.id.setting_logo);
		tagDao = new TagDao(context);
		userDao = new UserDao(context);
		aLoader = new AsyncLoader(context);
		view = inflateView();
	}
	
	private boolean focuseChanged = false;
	private int firstVisibleItem;
	private int visibleItemCount;
	private HashSet<Integer> showedTagsHashSet = new HashSet<Integer>();
//
	private void animateTags(){
		for(int i=firstVisibleItem; 
				i<firstVisibleItem+visibleItemCount; i++){
			if(!showedTagsHashSet.contains(i)){
				adapter.animateTags(i);
				adapter.setViewIfFastFling(i);
				showedTagsHashSet.add(i);
				GlobalDefs.o("animate tag:"+i);
			}
		}
	}
	
	private View inflateView(){
		View view = LayoutInflater.from(context).inflate(R.layout.view_home_page, null);
		adapter = new HomePageAdapter(context, mUser);
        final ListView listview = (ListView) view.findViewById(R.id.home_page_listview);
		listview.setAdapter(adapter);
		setDragOnRefresh(listview);
		
		listview.getViewTreeObserver().addOnGlobalFocusChangeListener(
				new OnGlobalFocusChangeListener() {
			
			@Override
			public void onGlobalFocusChanged(View oldFocus, View newFocus) {
				System.out.println("focus changed");
				focuseChanged = true;
			}
		});
		listview.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				topMargin = ((RelativeLayout.LayoutParams)
						listview.getLayoutParams()).topMargin;
			}
		});
		
		listview.setOnScrollListener(new OnScrollListener() {
			    
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
					int size = view.getChildCount();
					View lastView = view.getChildAt(size-1);
					if(lastView != null){
						System.out.println("bottom:"+view.getBottom() +" "+ lastView.getBottom());
						if(view.getBottom() == lastView.getBottom() + topMargin){
							loadMore();
						}
					}
				}
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					animateTags();
					break;
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int f, int v, int totalItemCount) {
				firstVisibleItem = f;
				visibleItemCount = v;
				if(focuseChanged){
					animateTags();
					focuseChanged = false;
				}

				removeInvisibleOnScroll(f, v);
			}
			
			private void removeInvisibleOnScroll(int f, int v){
				for(int index=0; index<showedTagsHashSet.size(); index++){
					Integer i = (Integer) (showedTagsHashSet.toArray()[index]);
					if(i < f || i >= f+v){
						showedTagsHashSet.remove(i);
						GlobalDefs.o("remove tag:"+i);
						index--;
					}
				}
			}
		});
		
		
		view.findViewById(R.id.home_page_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectGallery();
            }
        });
		
		return view;
	}

	private boolean isRoading = false;

	private void setDragOnRefresh(final ListView listview){
		listview.setOnTouchListener(new OnTouchListener(){
			
			private int startX, startY;
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(isRoading){
					return false;
				}
				int x = (int) event.getX();
				int y = (int) event.getY();
				switch(event.getAction()){
				case MotionEvent.ACTION_MOVE:
					refresh(x, y);
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					reset();
					break;
				}
				onDrag(event.getAction(), length);
				return false;
			}
			
			private void refresh(int x, int y){
				View firstView = listview.getChildAt(0);
				
				if(firstView == null || 
						firstView.getTop()+topMargin == listview.getTop()){
					if(!start){
						startX = x;
						startY = y;
						start = true;
					}else{
						length = Math.abs(startY-y);
					}
				}else{
					reset();
				}
				
				/*
				 * cancel when sliding on a horizontal scale
				 */
				if(Math.abs(startX-x) > Math.abs(startY-y)){
					reset();
				}
			}
			
			private boolean start = false;
			private int length = 0;
			private void reset(){
				length = 0;
				start = false;
				setTitle(context.getResources().getString(R.string.title_home));
			}
		});
		
	}

	private final int FLAG_NONE = -1;
	private final int FLAG_NORM = 0;
	private final int FLAG_READY = 1;
	private int flag = FLAG_NONE;
	
	private void onDrag(int action, int length) {
		switch(action){
		case MotionEvent.ACTION_MOVE:
			length *= 3f;
			int l = Math.min(length, GlobalDefs.getScreenWidth());
			if(l == 0){
				resetPrgs();
				break;
			}
			
			setRefreshPrgs(l);
			if(l >= GlobalDefs.getScreenWidth()){
				if(flag != FLAG_READY){
					flag = FLAG_READY;
					setTitle(context.getResources().
							getString(R.string.progress_ready));
                    break;
				}
			}else{
				if(flag != FLAG_NORM){
					flag = FLAG_NORM;
					setTitle(context.getResources().
							getString(R.string.progress_normal));
                    break;
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			if(flag == FLAG_READY){
				refresh();
				flag = FLAG_NONE;
			}else{
				resetPrgs();
			}
			break;
		}
	}

	public void loadMore(){
		int id = 1000000;
		if(tags.size() != 0 && tags.last() != null){
			id = tags.last().getServerId();
		}
		System.out.println("loadMore:"+id);
		new DownloadTask(GetTagTransport.FLAG_MORE, id).execute(); 
	}
	
	public void refresh(){
		int id = 0;
		if(tags.size() != 0 && tags.first() != null){
			id = tags.first().getServerId();
		}
		System.out.println("refresh:"+id);
		new DownloadTask(GetTagTransport.FLAG_REFRESH, id).execute(); 
	}
	
	private void resetPrgs(){
		setRefreshPrgs(0);
		flag = FLAG_NONE;
	}
	
	private void setRefreshPrgs(int l){
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) progressView.getLayoutParams();
		params.width = l;
		progressView.setLayoutParams(params);
	}

	private void runLoadingAnimation(){
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
		
		Animation rollAnim = new RotateAnimation(
				0, 360, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		rollAnim.setDuration(1200);
		rollAnim.setRepeatCount(Animation.INFINITE);
		rollAnim.setRepeatMode(Animation.RESTART);
		
//		logoView.startAnimation(rollAnim);
	}
	
	private void stopLoadingAnimation(){
		progressView.clearAnimation();
//		logoView.clearAnimation();
		setRefreshPrgs(0);
	}
	
	class DownloadTask extends AsyncTask<Integer, Integer, TreeSet<Tag>>{  
		
		private boolean isLocal = true;
		private int flag;
		private int id;
		
		public DownloadTask(int flag, int id){
			this.flag = flag;
			this.id = id;
		}
          
        @Override  
        protected void onPreExecute() {  
        	isRoading = true;
        	runLoadingAnimation();
			setTitle(context.getResources().getString(R.string.progress_loading));
            //��һ��ִ�з���  
            super.onPreExecute();  
        }  
          
        @Override  
        protected TreeSet<Tag> doInBackground(Integer... params) {  
        	
        	/* load from local
        	 * load more only
        	 */
        	if(flag == GetTagTransport.FLAG_MORE){
        		TreeSet<Tag> locList = dao.getMore(flag, id);
        		if(locList.size() != 0){
        			isLocal = true;
        			return locList;
        		}
        	}

			isLocal = false;
            // load from server
    		GetTagTransport getTap = new GetTagTransport();
    		TreeSet<Tag> list = (TreeSet<Tag>) getTap.getMsg(
    				context, new Connection(),
    				null, new String[]{id+"", flag+""});

            publishProgress();
            return list;  
        }  
  
        @Override  
        protected void onProgressUpdate(Integer... progress) {  
            super.onProgressUpdate(progress);  
        }  
  
        @Override  
        protected void onPostExecute(TreeSet<Tag> result) {  
//			onTagsLoad();
        	if(result == null){
        		return;
        	}
        	if(isHiding){
            	isRoading = false;
            	stopLoadingAnimation();
        		return;
        	}
			setTitle(context.getResources().getString(R.string.title_home));
        	isRoading = false;
        	stopLoadingAnimation();
			tags.addAll(result);
			onListViewNotified();
			if(!isLocal){
				saveAllTags(result);
			}
			if(tags.size() == 0){
				
			}
            super.onPostExecute(result);  
        }  
    }
	
	private Handler mHandler = new Handler();
	
//	private void onTagsLoad(){
//		showedTagsHashSet.clear();
//		new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(200);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                mHandler.post(new Runnable() {
//					@Override
//					public void run() {
//						System.out.println("onTagsLoad 1");
////						animateTags();
//					}
//                });
//            }
//		}).start();
//	}
	
	private boolean isHiding = true;
	    
	@Override
	public void show() {
		root.removeAllViews();
    	root.addView(view);
    	btn.setImageResource(R.drawable.mt_refresh);
    	adapter.updateData();
    	adapter.animateTags();
    	setTitle(context.getResources().getString(R.string.title_home));
    	loadTags();
    	isHiding = false;
	}

	@Override
	public void hide() {
    	isHiding = true;
	}

	@Override
	public void startMoving() {
		adapter.startMoving();
	}

	@Override
	public void stopMoving() {
		adapter.stopMoving();
	}
	
	private void saveAllTags(TreeSet<Tag> tags){
		for(Tag tag:tags){
			tagDao.saveTagAll(tag);
		}
	}
	     
	private void loadTags(){
		if(tags.size() != 0){
			return;
		}
		// load from local
		tags = tagDao.getAllTags();
		adapter.setTags(tags);
		onListViewNotified();
		if(tags.size() == 0){
			refresh();
		}else{
			syncTagInfo(tags);
		}
	}
	
	private void syncTagInfo(TreeSet<Tag> tags){
		aLoader.downloadMessage(new GetTagInfo(), tags, null, new NetworkCallback(){

			@Override
			public void onLoaded(Object obj) {
				if((Integer)obj > 0){
					onListViewNotified();
				}
			}
		});
	}

	@Override
	public void addTag(Tag tag) {
		System.out.println("fucking adding tag"+tag.getServerId());
		tags.add(tag);
		onListViewNotified();
		mUser.setTags(mUser.getTags()+1);
		userDao.updateTag(mUser);
//		onTagsLoad();
		if(mOnTagAddedListener != null){
			mOnTagAddedListener.onTagAdded(tag);
		}
	}
	
	public void removeTag(Tag tag){
		tags.remove(tag);
		onListViewNotified();
	}

	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		if(super.onActivityResult(requestCode, resultCode, data)){
			return true;
		}
		if(requestCode == MainView.REQUEST_VIEW_TAG){
			Tag tag = (Tag) data.getParcelableExtra(GlobalDefs.EXTRA_TAG);
			if(tag != null){
				setTag(tag);
			}
//			HashSet<Integer> likes = (HashSet<Integer>) 
//					data.getSerializableExtra(GlobalDefs.EXTRA_LIKES);
//			if(likes != null){
//				adapter.updateInfo(likes);
//			}
			onListViewNotified();
			return true;
		}else if(requestCode == REQUEST_VIEW_PERSONAL_PAGE){
			if(resultCode == Activity.RESULT_OK){
				boolean b = data.getBooleanExtra(GlobalDefs.EXTRA_FOLLOW, false);
				String account = data.getStringExtra(GlobalDefs.EXTRA_USER_ACCOUNT);
				adapter.asyncFollow(account, b ? FollowRequest.M_FOLLOW : FollowRequest.M_UNFOLLOW);
			}
			return true;
		}else if(requestCode == MainView.REQUEST_EDIT_PROFILE){
			if(resultCode == Activity.RESULT_OK){
				User u = data.getParcelableExtra(GlobalDefs.EXTRA_USER);
				adapter.updateUser(u);
				onListViewNotified();
			}
			return false;
		}
		
		return false;
	}

	private void setTag(Tag tag){
		for(Tag t:tags){
			if(t.getServerId() == tag.getServerId()){
				t.copy(tag);
			}
		}
	}
	
	private void onListViewNotified(){
		adapter.notifyDataSetChanged();
		adapter.animateTags();
	}
	
	public void setOnTagAddedListener(OnTagAddedListener mOnTagAddedListener) {
		this.mOnTagAddedListener = mOnTagAddedListener;
	}

	private OnTagAddedListener mOnTagAddedListener;
	
	public interface OnTagAddedListener{
		public void onTagAdded(Tag tag);
	}

	@Override
	public void onButtonClick(View v) {
		refresh();
	}
}
