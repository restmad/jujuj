package com.mocaa.tagme.activity;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mocaa.tagme.R;
import com.mocaa.tagme.animation.Decelerator;
import com.mocaa.tagme.db.TagDao;
import com.mocaa.tagme.db.UserDao;
import com.mocaa.tagme.db.UserPref;
import com.mocaa.tagme.dialog.LoadingDialog;
import com.mocaa.tagme.download.ImageLoaderImpl;
import com.mocaa.tagme.download.AsyncLoader.NetworkCallback;
import com.mocaa.tagme.entity.Tag;
import com.mocaa.tagme.entity.User;
import com.mocaa.tagme.friends.MyFollow;
import com.mocaa.tagme.friends.UserPool;
import com.mocaa.tagme.homepage.MyLikes;
import com.mocaa.tagme.transport.Connection;
import com.mocaa.tagme.transport.GetTagTransport;
import com.mocaa.tagme.transport.GetUserTransport;
import com.mocaa.tagme.transport.SignInRequest;
import com.mocaa.tagme.transport.SignUpRequest;
import com.mocaa.tagme.util.Rotate3dAnimation;
import com.mocaa.tagme.view.LockView;

public class SignInActivity extends Activity{

	private UserPref pref;
	private EditText accountEt, pwdEt;
	private View signInGroup, signUpGroup;
	private String account = "";
	private String pwd = "";
	private Button signBtn;
	private final int SIGN_IN = 1;
	private final int SIGN_UP = -1;
	private int mSign = SIGN_UP;
	private LoadingDialog dialog;
	private LockView showPwd;
	private ImageView lockView;
	private TagDao tagDao;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin);
		pref = new UserPref(this);
		tagDao = new TagDao(this);
		showPwd = (LockView) findViewById(R.id.activity_sign_show_pwd);
		lockView = (ImageView) findViewById(R.id.activity_sign_lock);
		signBtn = (Button) findViewById(R.id.activity_sign_btn);
		signInGroup = findViewById(R.id.activity_sign_in_group);
		signUpGroup = findViewById(R.id.activity_sign_up_group);
		accountEt = (EditText)findViewById(R.id.activity_sign_account);
		pwdEt = (EditText)findViewById(R.id.activity_sign_pwd);
		
		setLockViewListener();
		addTextWatcher();
		initAnimations();
	}
	
	private void setLockViewListener(){
		showPwd.setOnClickListener(new LockView.OnClickListener() {
			@Override
			public void onClick(View v, int flag) {
				if(flag == LockView.FLAG_CLICK){
					show_pwd();
				}
			}
		});
	}
	
	private void addTextWatcher(){
		accountEt.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				account = s.toString().trim();
				doChecking();
			}
			
		});
		pwdEt.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				pwd = s.toString().trim();
				doChecking();
			}
			
		});
	}
	
	private void doChecking(){
		if(account.equals("")){
			signBtn.setEnabled(false);
			return;
		}
		if(pwd.equals("")){
			signBtn.setEnabled(false);
			return;
		}
		signBtn.setEnabled(true);
	}
	
	private void showDialog(String text){
		if(dialog == null){
			dialog = LoadingDialog.createDialog(this);
		}
		dialog.setTipText(text);
		dialog.show();
	}  
	
	private void signIn(){
		showDialog(SignInActivity.this.getResources().getString(R.string.signing_in));
		new Thread(){
			public void run(){
				SignInRequest signin = new SignInRequest();
				Object result = signin.getMsg(SignInActivity.this, new Connection(), 
						null, new String[]{account, pwd});
				if(result instanceof User){
					preLoadTags((User) result);
				}
				Message msg = new Message();
				msg.what = WHAT_SIGN_IN;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		}.start();
	}
	
	private void dealSignUpResult(Object result){
		if(result instanceof User){
			/*
			 * sign up
			 */
			pref.setUserAccount(account);
			pref.signIn();
			UserDao dao = new UserDao(this);
			dao.signInUser((User) result);
			if(dialog != null){
				dialog.dismiss();
				dialog = null;
			}
			
			startActivity(new Intent(this, MainActivity.class));
			finish();
			return;
		}else if(result instanceof Integer){
			int iResult = (Integer) result;
			if(iResult == -2){
				Toast.makeText(this, getResources().getString(R.string.toast_account_exist), 
						Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(this, getResources().getString(R.string.toast_conn_error), 
						Toast.LENGTH_SHORT).show();
			}
		}else{
			Toast.makeText(this, getResources().getString(R.string.toast_conn_error), 
					Toast.LENGTH_SHORT).show();
		}
		if(dialog != null){
			dialog.dismiss();
			dialog = null;
		}
	}
	
	private void dealSignInResult(Object result){
		if(result instanceof User){
			/*
			 * sign in
			 */
			pref.setUserAccount(account);
			pref.signIn();
			UserDao dao = new UserDao(this);
			ImageLoaderImpl.loadBitmapFromUrl(((User) result).getPortraitUrl(), SignInActivity.this);
			dao.signInUser((User) result);
			if(dialog != null){
				dialog.dismiss();
				dialog = null;
			}
			
			startActivity(new Intent(this, MainActivity.class));
			finish();
			return;
		}else if(result instanceof Integer){
			int iResult = (Integer) result;
			if(iResult == -3){
				Toast.makeText(this, getResources().getString(R.string.toast_pwd_wrong), 
						Toast.LENGTH_SHORT).show();
			}else if(iResult == -4){
				Toast.makeText(this, getResources().getString(R.string.toast_account_not_exist), 
						Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(this, getResources().getString(R.string.toast_conn_error), 
						Toast.LENGTH_SHORT).show();
			}
		}else{
			Toast.makeText(this, getResources().getString(R.string.toast_conn_error), 
					Toast.LENGTH_SHORT).show();
		}
		if(dialog != null){
			dialog.dismiss();
			dialog = null;
		}
	}
	
	public void sign(View v){
		Pattern p = Pattern.compile("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$");
		Matcher m = p.matcher(account);
		if(!m.matches()){
			Toast.makeText(this, getResources().getString(R.string.toast_email_wrong), 
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(mSign == SIGN_IN){
			signIn();
		}else{
			signUp();
		}
	}
	
	private void signUp(){
		showDialog(SignInActivity.this.getResources().getString(R.string.signing_up));
		new Thread(){
			public void run(){
				SignUpRequest signup = new SignUpRequest();
				Object result = signup.getMsg(SignInActivity.this, new Connection(), 
						null, new String[]{account, pwd});
				if(result instanceof User){
					preLoadTags((User) result);
				}
				Message msg = new Message();
				msg.what = WHAT_SIGN_UP;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		}.start();
	}
	
	private Animation rollDownAnim, rollUpAnim;
	private final int DURATION = 300;
	
	private void initAnimations(){
		DecelerateInterpolator it = new DecelerateInterpolator();
		
		rollDownAnim = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 1);
		rollDownAnim.setFillAfter(true);
		rollDownAnim.setDuration(300);
		rollDownAnim.setInterpolator(it);

		rollUpAnim = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, 1,
				Animation.RELATIVE_TO_SELF, 0);
		rollUpAnim.setDuration(300);
		rollUpAnim.setInterpolator(it);
	}

	public void turn_sign_in(View v){
		//reset lock view
		pwdEt.setInputType(InputType.TYPE_CLASS_TEXT | 
				InputType.TYPE_TEXT_VARIATION_PASSWORD);
		lockView.clearAnimation();
		
		signBtn.setText(R.string.btn_sign_in);
		signUpGroup.startAnimation(rollDownAnim);
		signInGroup.setVisibility(View.VISIBLE);
		signInGroup.startAnimation(rollUpAnim);

		int offset = showPwd.getLayoutParams().width;
		int des = 0;
		
		expandingLockView(offset, des, WHAT_TURN_SIGN_IN);
	}
	
	public void turn_sign_up(View v){
		signBtn.setText(R.string.btn_sign_up);
		
		signInGroup.startAnimation(rollDownAnim);
		signUpGroup.setVisibility(View.VISIBLE);
		signUpGroup.startAnimation(rollUpAnim);
		
		int offset = showPwd.getLayoutParams().width;
		int des = (int) getResources().getDimension(R.dimen.lock_width);
		
		expandingLockView(offset, des, WHAT_TURN_SIGN_UP);
	}
	
	private final int WHAT_EXPANDING = 1;
	private final int WHAT_TURN_SIGN_UP = 2;
	private final int WHAT_TURN_SIGN_IN = 3;
	private final int WHAT_SIGN_IN = 4;
	private final int WHAT_SIGN_UP = 5;
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			System.out.println("waht::"+msg.what);
			switch(msg.what){
			case WHAT_EXPANDING:
				expanding(msg.arg1);
				break;
			case WHAT_TURN_SIGN_UP:
				signInGroup.clearAnimation();
				signInGroup.setVisibility(View.GONE);
				mSign = SIGN_UP;
				break;
			case WHAT_TURN_SIGN_IN:
				signUpGroup.clearAnimation();
				signUpGroup.setVisibility(View.GONE);
				mSign = SIGN_IN;
				break;
			case WHAT_SIGN_IN:
				dealSignInResult(msg.obj);
				break;
			case WHAT_SIGN_UP:
				dealSignUpResult(msg.obj);
				break;
			}
		}
	};
	
	private void expandingLockView(final int offset, final int des, final int what){
		new Thread(){
			public void run(){
				Decelerator anim = new Decelerator(0, 0, offset, des);
				anim.setDuration(DURATION);
				anim.start();
				while(!anim.isEnding()){
					try {
						sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Message msg = new Message();
					msg.what = WHAT_EXPANDING;
					msg.arg1 = anim.getMoving().y;
					mHandler.sendMessage(msg);
				}
				mHandler.sendEmptyMessage(what);
			}
		}.start();
	}
	
	private void expanding(int width){
		LayoutParams params = showPwd.getLayoutParams();
		params.width = width;
		params.height = width;
		showPwd.setLayoutParams(params);
	}
	
	private Animation lockAnim, unlockAnim;
	
	private void show_pwd(){
		int pwd = InputType.TYPE_CLASS_TEXT | 
				InputType.TYPE_TEXT_VARIATION_PASSWORD;
		int visible = InputType.TYPE_CLASS_TEXT | 
				InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
		System.out.println("type:" + pwd);
		if(pwdEt.getInputType() != pwd){
			System.out.println("lock");
			pwdEt.setInputType(pwd);
			pwdEt.invalidate();
			if(lockAnim == null){
				lockAnim = new Rotate3dAnimation(150, 0,
						lockView.getWidth()/2, 0, 0, true);
				lockAnim.setDuration(DURATION);
				lockAnim.setFillAfter(true);
			}       
			lockView.startAnimation(lockAnim);
			return;
		}else{
			System.out.println("unlock");
			pwdEt.setInputType(visible);
			if(unlockAnim == null){
				unlockAnim = new Rotate3dAnimation(0, 150,
						lockView.getWidth()/2, 0, 0, true);
				unlockAnim.setDuration(DURATION);
				unlockAnim.setFillAfter(true);
			}
			lockView.startAnimation(unlockAnim);
		}
	}
	
	private void preLoadTags(User user){
		System.out.println("pre load tags");
		TreeSet<Tag> tags = tagDao.getAllTags();
		if(tags.size() == 0){
			int id = 0;
			GetTagTransport getTap = new GetTagTransport();
    		TreeSet<Tag> list = (TreeSet<Tag>) getTap.getMsg(
    				this, new Connection(),
    				null, new String[]{id+"", GetTagTransport.FLAG_REFRESH+""});
			saveAllTags(list);
    		preLoadUsers(list);
		}
		MyLikes.getInstance(this, user);
		MyFollow.getInstance(this, user);
	}
	
	private void preLoadUsers(TreeSet<Tag> list){
		Connection conn = new Connection();
		UserDao userDao = new UserDao(this);
		for(Tag tag:list){
			String account = tag.getUserAccount();
			GetUserTransport getUser = new GetUserTransport();
			String[] array = new String[]{account};
			User user = (User) getUser.getMsg(this, conn, null, array);
			userDao.createUser(user);
		}
	}

	private void saveAllTags(TreeSet<Tag> tags){
		for(Tag tag:tags){
			tagDao.saveTagAll(tag);
		}
	}
	     
}
