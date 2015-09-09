package com.mocaa.tagme.activity;
  

import java.util.TreeSet;

import android.view.KeyEvent;

import com.mocaa.tagme.R;
import com.mocaa.tagme.animation.Decelerator;
import com.mocaa.tagme.db.UserPref;
import com.mocaa.tagme.download.AsyncLoader;
import com.mocaa.tagme.download.AsyncLoader.ImageCallback;
import com.mocaa.tagme.entity.Notification;
import com.mocaa.tagme.entity.Tag;
import com.mocaa.tagme.entity.User;
import com.mocaa.tagme.friends.FriendsView;
import com.mocaa.tagme.friends.MyFollow;
import com.mocaa.tagme.global.GlobalDefs;
import com.mocaa.tagme.homepage.HomePageView;
import com.mocaa.tagme.homepage.MyLikes;
import com.mocaa.tagme.mypage.MyPageView;
import com.mocaa.tagme.mypage.MyPageView.OnTagChangedListener;
import com.mocaa.tagme.notification.NotiService;
import com.mocaa.tagme.notification.NotificationView;
import com.mocaa.tagme.setting.SettingView;
import com.mocaa.tagme.util.BitmapUtil;
import com.mocaa.tagme.view.AnimClickableView;
import com.mocaa.tagme.view.MainView;
import com.mocaa.tagme.view.ResideLayout;
import com.mocaa.tagme.view.ResideLayout.PanelSlideListener;

import android.R.integer;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TableLayout;

import com.mocaa.tagme.view.PortraitView;

public class MainActivity extends Activity{

	private static final int WHAT_MOVE = 0;
	private static final int WHAT_OPEN = 1;
	private static final int WHAT_CLOSE = 2;
	private static final int DURATION = 180;
//	private View indicator;
	private View content;
	private PortraitView headView;
	private TextView nameView;
	private TableLayout menuRoot;
	private boolean isPanelOepned = false;
	private static final int SLEEP = 5;
	private int DESTINATION;
	private static final int SIZE = 4;  
	private int mPage;
	private static final int PAGE_HOME = 0;
	private static final int PAGE_FRIENDS = 1;
	private static final int PAGE_NOTIFICATIONS = 2;
	private static final int PAGE_MY_PAGE = 3;
	private static final int PAGE_SETTINGS = 4;
	private MainView[] mainView = new MainView[5];// myPage, homePage;
	private User mUser;
	private UserPref pref;
	private ResideLayout layout;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if(savedInstanceState != null){
			System.out.println("everything alright?");
		}
		
		initAnimation();
		
		pref = new UserPref(this);
		mUser = pref.getMyProfile();
		
		headView = (PortraitView) findViewById(R.id.main_head);
        headView.setOnAnimClickListener(new AnimClickableView.OnAnimClickListener() {
            @Override
            public void onClick(View v, int flag) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra(GlobalDefs.EXTRA_USER, mUser);
                startActivityForResult(intent, MainView.REQUEST_EDIT_PROFILE);
            }
        });
		
        layout = (ResideLayout) findViewById(R.id.reside_layout);
        layout.setPanelSlideListener(new PanelSlideListener() {
			
			@Override
			public void onPanelSlide(View panel, float slideOffset) {
				startMoving();
			}
			
			@Override
			public void onPanelOpened(View panel) {
				isPanelOepned = true;
				stopMoving();
			}
			
			@Override
			public void onPanelClosed(View panel) {
				isPanelOepned = false;
				stopMoving();
			}
		});
		nameView = (TextView) findViewById(R.id.main_name);
		menuRoot = (TableLayout) findViewById(R.id.main_menu_root);
		
//		indicator = findViewById(R.id.setting_indicator);
		content = findViewById(R.id.main_content);

		DESTINATION = (int) (GlobalDefs.getScreenWidth()*0.8f);
		
		FrameLayout contentHolder = (FrameLayout) findViewById(R.id.main_content_holder);
		View titleBar = findViewById(R.id.main_title_bar);

		mainView[PAGE_HOME] = new HomePageView(this, contentHolder, titleBar, mUser);
		mainView[PAGE_FRIENDS] = new FriendsView(this, contentHolder, titleBar, mUser);
		mainView[PAGE_NOTIFICATIONS] = new NotificationView(this, contentHolder, titleBar, mUser);
		mainView[PAGE_MY_PAGE] = new MyPageView(this, contentHolder, titleBar, mUser);
		mainView[PAGE_SETTINGS] = new SettingView(this, contentHolder, titleBar, mUser);
		
		((HomePageView)mainView[PAGE_HOME]).setOnTagAddedListener(
				new HomePageView.OnTagAddedListener(){

			@Override
			public void onTagAdded(Tag tag) {
				((MyPageView)mainView[PAGE_MY_PAGE]).addTag(tag);
			}
			
		});
		((MyPageView)mainView[PAGE_MY_PAGE]).setOnTagChangedListener(new OnTagChangedListener() {
			
			@Override
			public void onTagChanged(Tag tag, int flag) {
				if(flag == OnTagChangedListener.FLAG_ADD){
					((HomePageView)mainView[PAGE_HOME]).addTag(tag);
				}else if(flag == OnTagChangedListener.FLAG_REMOVE){
					((HomePageView)mainView[PAGE_HOME]).removeTag(tag);
				}
			}
		});
		
		setUserProfile();
		
		Intent intent = new Intent(this, NotiService.class);
		startService(intent);
		registerReceiver(mBroadcastReceiver, 
				new IntentFilter(GlobalDefs.ACTION_NOTIFICATIONS));
		
		if(getIntent().getBooleanExtra(GlobalDefs.EXTRA_NOTI, false)){
			mPage = PAGE_NOTIFICATIONS;
		}else{
			mPage = PAGE_HOME;
		}
		mainView[mPage].showView();
		
		GlobalDefs.o("MainActivity onCreate");
	}
	
	@Override
	public void onNewIntent(Intent intent){
		super.onNewIntent(intent);
		if(intent.getBooleanExtra(GlobalDefs.EXTRA_NOTI, false)){
			mPage = PAGE_NOTIFICATIONS;
			mainView[mPage].showView();
		}
		GlobalDefs.o("MainActivity onNewIntent");
	}

	@Override
	public void onSaveInstanceState(Bundle saveInsanceState){
		super.onSaveInstanceState(saveInsanceState);
		GlobalDefs.o("MainActivity onSaveInstanceState");
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInsanceState){
		super.onRestoreInstanceState(savedInsanceState);
		GlobalDefs.o("MainActivity onRestoreInstanceState");
	}
	
	@Override
	public void onResume(){
		super.onResume();
		GlobalDefs.o("MainActivity onResume");
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		Intent intent = new Intent(this, NotiService.class);
		if(!pref.isNotificating()){
			stopService(intent);
		}
		unregisterReceiver(mBroadcastReceiver);
		MyFollow.destroy();
		MyLikes.destroy();
	}
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(GlobalDefs.ACTION_NOTIFICATIONS)){
				TreeSet<Notification> list = (TreeSet<Notification>) 
						intent.getSerializableExtra(GlobalDefs.ACTION_NOTIFICATIONS);
				((NotificationView)mainView[PAGE_NOTIFICATIONS]).addList(list);
			}
		}
		
	};
	
	private void loadPortrait(String url){
		AsyncLoader loader = new AsyncLoader(this);
		loader.downloadImage(url, true, new ImageCallback(){

			@Override
			public void onImageLoaded(Bitmap bitmap, String imageUrl) {
				if(bitmap != null){
					bitmap = BitmapUtil.getPortraitBitmap(MainActivity.this, bitmap);
					mUser.setPortraitThumb(bitmap);
					headView.setImageBitmap(bitmap);
				}
			}
		});
	}
	
	private void setUserProfile(){
		nameView.setText(mUser.getUserName());
		Bitmap portrait = mUser.getPortraitThumb();
		if(portrait == null){
			headView.setImageResource(R.drawable.portrait_default_round);
			//������������
			String url = mUser.getPortraitUrl();
			System.out.println("my portrait:"+url);
			if(url != null && !url.equals("")){
				loadPortrait(url);
			}
		}else{
			headView.setImageBitmap(portrait);
		}
	}

	private Animation hideAnim, showAnim;
	private AnimationSet headAnim;
	
	private void initAnimation(){
		hideAnim = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, -0.3f, 
				Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, 0);
		hideAnim.setDuration(300);
		hideAnim.setFillAfter(true);

		showAnim = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, -0.3f, 
				Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, 0);
		showAnim.setDuration(300);
		showAnim.setFillAfter(true);
		
		headAnim = new AnimationSet(true);
		headAnim.setInterpolator(new OvershootInterpolator());
		headAnim.setDuration(200);
		headAnim.setStartOffset(400);
		headAnim.addAnimation(new ScaleAnimation(0f, 1f, 0f, 1f,
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f));
		headAnim.addAnimation(new AlphaAnimation(0, 1));
	}
	
	private void startMoving(){
		mainView[PAGE_MY_PAGE].startMoving();
	}
	
	private void stopMoving(){
		mainView[PAGE_MY_PAGE].stopMoving();
	}
   
	public void setting(View v){
		if(isPanelOepned){
			layout.closePane();
		}else{
			layout.openPane();
		}
	}
	
	public void menu(View v){
		layout.closePane();
		onMenuHidden(v);
	}
	
	private void onMenuHidden(View v){
		int index = 0;
		try {
			String tag = (String) v.getTag();
			index = Integer.parseInt(tag);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		if(mPage != index){
			mainView[mPage].hide();
			mPage = index;
			mainView[mPage].showView();
		}
	}
	
	@Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		System.out.println("on result in main:"+requestCode+","+resultCode);
		if(MainView.REQUEST_EDIT_PROFILE == requestCode){
			if(resultCode == RESULT_OK){
				mUser = data.getParcelableExtra(GlobalDefs.EXTRA_USER);
				headView.setImageBitmap(mUser.getPortraitThumb());
				nameView.setText(mUser.getUserName());
			}else if(resultCode == ProfileActivity.RESULT_SIGN_OUT){
				stopService(new Intent(MainActivity.this, NotiService.class));
				startActivity(new Intent(MainActivity.this, SignInActivity.class));
				finish();
			}
		}
		for(MainView m:mainView){
			if(m.onActivityResult(requestCode, resultCode, data)){
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);  
	} 

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                if(!isPanelOepned){
            		layout.openPane();
                    return true;
                }
        }
        return super.onKeyDown(keyCode, event);
    }

}
