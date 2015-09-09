package com.mocaa.tagme.test;

import java.util.HashSet;
import java.util.TreeSet;

import com.mocaa.tagme.R;
import com.mocaa.tagme.db.TagDao;
import com.mocaa.tagme.db.UserDao;
import com.mocaa.tagme.db.UserPref;
import com.mocaa.tagme.download.AsyncLoader;
import com.mocaa.tagme.download.AsyncLoader.NetworkCallback;
import com.mocaa.tagme.entity.Tag;
import com.mocaa.tagme.entity.User;
import com.mocaa.tagme.global.GlobalDefs;
import com.mocaa.tagme.homepage.HomePageAdapter;
import com.mocaa.tagme.homepage.HomePageView;
import com.mocaa.tagme.homepage.HomePageView.OnTagAddedListener;
import com.mocaa.tagme.transport.Connection;
import com.mocaa.tagme.transport.FollowRequest;
import com.mocaa.tagme.transport.GetTagInfo;
import com.mocaa.tagme.transport.GetTagTransport;
import com.mocaa.tagme.view.MainView;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AbsListView.OnScrollListener;

public class TestActivity extends Activity{
	
	private HomePageAdapter adapter;
	private User mUser;
	private TagDao tagDao;
	private UserPref pref;
	private TreeSet<Tag> tags = new TreeSet<Tag>();
	private AsyncLoader aLoader;
	private int firstVisibleItem;
	private int visibleItemCount;
	private HashSet<Integer> showedTagsHashSet = new HashSet<Integer>();
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_home_page);

		initView();
    	adapter.updateData();
    	loadTags();
	}
	
	private void initView(){
		aLoader = new AsyncLoader(this);
		tagDao = new TagDao(this);
		pref = new UserPref(this);
		mUser = pref.getMyProfile();
		
		adapter = new HomePageAdapter(this, mUser);
        ListView listview = (ListView) findViewById(R.id.home_page_listview);
		listview.setAdapter(adapter);
		setDragOnRefresh(listview);
		
		listview.getViewTreeObserver().addOnGlobalFocusChangeListener(new OnGlobalFocusChangeListener() {
			
			@Override
			public void onGlobalFocusChanged(View oldFocus, View newFocus) {
				System.out.println("focus changed");

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
						if(view.getBottom() == lastView.getBottom()){
							loadMore();
						}
					}
				}








			}
			
			@Override
			public void onScroll(AbsListView view, int f, int v, int totalItemCount) {
				firstVisibleItem = f;
				visibleItemCount = v;




			}
			
		});
		
		
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
					reset();
					break;
				}
				onDrag(event.getAction(), length);
				return false;
			}
			
			private void refresh(int x, int y){
				View firstView = listview.getChildAt(0);
				if(firstView == null || 
						firstView.getTop() == listview.getTop()){
					if(!start){
						startX = x;
						startY = y;
						start = true;
					}else{
						length = (int) Math.sqrt(Math.pow(startX-x, 2) + Math.pow(startY-y, 2));
					}
				}else{
					reset();
				}
			}
			
			private boolean start = false;
			private int length = 0;
			private void reset(){
				length = 0;
				start = false;
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
			length *= 1.5f;
			int l = Math.min(length, GlobalDefs.getScreenWidth());
			if(l == 0){
				resetPrgs();
				break;
			}
			
			if(l >= GlobalDefs.getScreenWidth()){
				if(flag != FLAG_READY){
					flag = FLAG_READY;
                    break;
				}
			}else{
				if(flag != FLAG_NORM){
					flag = FLAG_NORM;
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
		flag = FLAG_NONE;
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
            super.onPreExecute();  
        }  
          
        @Override  
        protected TreeSet<Tag> doInBackground(Integer... params) {  
        	
        	/* load from local
        	 * load more only
        	 */
        	if(flag == GetTagTransport.FLAG_MORE){
        		TreeSet<Tag> locList = tagDao.getMore(flag, id);
        		if(locList.size() != 0){
        			isLocal = true;
        			return locList;
        		}
        	}

			isLocal = false;

    		GetTagTransport getTap = new GetTagTransport();
    		TreeSet<Tag> list = (TreeSet<Tag>) getTap.getMsg(
    				TestActivity.this, new Connection(),
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

        	if(result == null){
        		return;
        	}
        	isRoading = false;
			tags.addAll(result);
			adapter.notifyDataSetChanged();
			if(!isLocal){
				saveAllTags(result);
			}
            super.onPostExecute(result);  
        }  
    }
	
	private Handler mHandler = new Handler();
	
	private void onTagsLoad(){
		showedTagsHashSet.clear();
		new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.post(new Runnable() {
					@Override
					public void run() {
						System.out.println("onTagsLoad 1");

					}
                });
            }
		}).start();
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
	
	private void loadTags(){
		if(tags.size() != 0){
			return;
		}

		tags = tagDao.getAllTags();
		adapter.setTags(tags);
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
					adapter.notifyDataSetChanged();
				}
			}
		});
	}

	public void removeTag(Tag tag){
		tags.remove(tag);
		adapter.notifyDataSetChanged();
	}

}
