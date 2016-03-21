package com.shinado.tagme.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.shinado.tagme.R;
import com.shinado.tagme.animation.Decelerator;
import com.shinado.tagme.animation.Rotate3dAnimation;
import com.shinado.tagme.view.LockView;

import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import framework.core.Jujuj;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    private View mProgressView;
    private View mLoginFormView;
   /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    void findView() {
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private EditText accountEt, pwdEt;
    private View signInGroup, signUpGroup;
    private String account = "";
    private String pwd = "";
    private Button signBtn;
    public static final int SIGN_IN = 1;
    public static final int SIGN_UP = -1;
    private int mSign = SIGN_UP;
    private LockView showPwd;
    private ImageView lockView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        showPwd = (LockView) findViewById(R.id.activity_sign_show_pwd);
        lockView = (ImageView) findViewById(R.id.activity_sign_lock);
        signBtn = (Button) findViewById(R.id.sign_in_button);
        signInGroup = findViewById(R.id.activity_sign_in_group);
        signUpGroup = findViewById(R.id.activity_sign_up_group);
        accountEt = (EditText)findViewById(R.id.account);
        pwdEt = (EditText)findViewById(R.id.activity_sign_pwd);

        setLockViewListener();
        addTextWatcher();
        initAnimations();

        findView();
        Jujuj.getInstance().inject(this, new LoginRequest(this));
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

    public int getFlag(){
        return mSign;
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
        ViewGroup.LayoutParams params = showPwd.getLayoutParams();
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

}
