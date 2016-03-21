package com.shinado.tagme.login;

import android.app.Activity;
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

import com.shinado.tagme.R;
import com.shinado.tagme.animation.Decelerator;
import com.shinado.tagme.animation.Rotate3dAnimation;
import com.shinado.tagme.dialog.LoadingDialog;
import com.shinado.tagme.view.LockView;

import java.lang.ref.SoftReference;

import framework.core.Jujuj;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    void findView() {
        showPwd = (LockView) findViewById(R.id.activity_sign_show_pwd);
        lockView = (ImageView) findViewById(R.id.activity_sign_lock);
        signBtn = (Button) findViewById(R.id.sign_in_button);
        signInGroup = findViewById(R.id.activity_sign_in_group);
        signUpGroup = findViewById(R.id.activity_sign_up_group);
        accountEt = (EditText)findViewById(R.id.account);
        pwdEt = (EditText)findViewById(R.id.pwd);
    }

    private LoadingDialog dialog;
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
    private LoginHandler mHandler;
    private HandlerHelper helper = new HandlerHelper();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Jujuj.getInstance().inject(this, new LoginRequest(this));

        mHandler = new LoginHandler(this);
        findView();
        setLockViewListener();
        addTextWatcher();
        initAnimations();
    }

    public HandlerHelper getHelper(){
        return helper;
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

        expandingLockView(offset, des, LoginHandler.WHAT_TURN_SIGN_IN);
    }

    public void turn_sign_up(View v){
        signBtn.setText(R.string.btn_sign_up);

        signInGroup.startAnimation(rollDownAnim);
        signUpGroup.setVisibility(View.VISIBLE);
        signUpGroup.startAnimation(rollUpAnim);

        int offset = showPwd.getLayoutParams().width;
        int des = (int) getResources().getDimension(R.dimen.lock_width);

        expandingLockView(offset, des, LoginHandler.WHAT_TURN_SIGN_UP);
    }

    class HandlerHelper{

        private void expanding(int width){
            ViewGroup.LayoutParams params = showPwd.getLayoutParams();
            params.width = width;
            params.height = width;
            showPwd.setLayoutParams(params);
        }

        private void turnSignUp(){
            signInGroup.clearAnimation();
            signInGroup.setVisibility(View.GONE);
            mSign = SIGN_UP;
        }

        private void turnSignIn(){
            signUpGroup.clearAnimation();
            signUpGroup.setVisibility(View.GONE);
            mSign = SIGN_IN;
        }
    }

    static class LoginHandler extends Handler{

        static final int WHAT_EXPANDING = 1;
        static final int WHAT_TURN_SIGN_UP = 2;
        static final int WHAT_TURN_SIGN_IN = 3;

        private SoftReference<LoginActivity> activityRef;

        public LoginHandler(LoginActivity activity){
            activityRef = new SoftReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            HandlerHelper helper = activityRef.get().getHelper();

            switch(msg.what){
                case WHAT_EXPANDING:
                    helper.expanding(msg.arg1);
                    break;
                case WHAT_TURN_SIGN_UP:
                    helper.turnSignUp();
                    break;
                case WHAT_TURN_SIGN_IN:
                    helper.turnSignIn();
                    break;
            }
        }

    }

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
                    msg.what = LoginHandler.WHAT_EXPANDING;
                    msg.arg1 = anim.getMoving().y;
                    mHandler.sendMessage(msg);
                }
                mHandler.sendEmptyMessage(what);
            }
        }.start();
    }


    private Animation lockAnim, unlockAnim;

    private void show_pwd(){
        int pwd = InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD;
        int visible = InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
        System.out.println("type:" + pwd);
        if(pwdEt.getInputType() != pwd){
            pwdEt.setInputType(pwd);
            pwdEt.invalidate();
            if(lockAnim == null){
                lockAnim = new Rotate3dAnimation(150, 0,
                        lockView.getWidth()/2, 0, 0, true);
                lockAnim.setDuration(DURATION);
                lockAnim.setFillAfter(true);
            }
            lockView.startAnimation(lockAnim);
        }else{
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

    public void showProgress(boolean b) {
        if (b){
            if(dialog == null){
                dialog = LoadingDialog.createDialog(this);
            }
            dialog.show();
        }else {
            if (dialog != null){
                dialog.dismiss();
            }
        }
    }
}
