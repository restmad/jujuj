package com.shinado.tagme.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.shinado.tagme.BaseResult;
import com.shinado.tagme.Globals;
import com.shinado.tagme.R;
import com.shinado.tagme.common.GetMyFollowers;
import com.shinado.tagme.common.GetMyLikes;
import com.shinado.tagme.common.Toaster;
import com.shinado.tagme.common.UserPref;
import com.shinado.tagme.main.MainActivity;
import com.shinado.tagme.user.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import framework.core.Jujuj;
import framework.inj.ActivityInj;
import framework.inj.ViewInj;
import framework.inj.entity.MutableEntity;
import framework.inj.entity.Postable;
import framework.inj.entity.utility.Notifiable;
import framework.inj.entity.utility.Validatable;

@ActivityInj(R.layout.activity_signin)
public class LoginRequest implements Postable<LoginRequest.UserResult>, Validatable{

    private LoginActivity mActivity;
    private MutableEntity<GetMyLikes> myLikes;
    private MutableEntity<GetMyFollowers> myFollowers;

    public LoginRequest(LoginActivity activity){
        mActivity = activity;
    }

    @ViewInj
    public String account;

    @ViewInj
    public String pwd;

    @Override
    public int getSubmitButtonId() {
        return R.id.sign_in_button;
    }

    @Override
    public void onPostResponse(final Context context, UserResult obj) {
        if (obj.resultCode < 0){
            Toast.makeText(context, obj.msg, Toast.LENGTH_LONG).show();
            mActivity.showProgress(false);
        }else {
            if (obj.user != null){
                loadExtras(obj.user);
            }else {
                Toaster.toastUnknownError(mActivity);
            }
        }
    }

    private void logIn(User user){
        user.save();
        UserPref userPref = new UserPref(mActivity);
        userPref.signIn();
        userPref.setUserAccount(user.getAccount());
    }

    private void loadExtras(final User user){
        myLikes = new MutableEntity<>(new GetMyLikes(user.getAccount()), new Notifiable() {
            @Override
            public void onDownloadResponse() {
                Jujuj.getInstance().request(mActivity, myFollowers);
            }

            @Override
            public void onError(String msg) {
                mActivity.showProgress(false);
                Toast.makeText(mActivity, msg, Toast.LENGTH_LONG).show();
            }
        });
        Jujuj.getInstance().request(mActivity, myLikes);

        myFollowers = new MutableEntity<>(new GetMyFollowers(user.getAccount()), new Notifiable() {
            @Override
            public void onDownloadResponse() {
                mActivity.showProgress(false);

                letsRoll(user);
            }

            @Override
            public void onError(String msg) {
                mActivity.showProgress(false);
                Toast.makeText(mActivity, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void letsRoll(User user){
        logIn(user);

        Intent intent = new Intent(mActivity, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MainActivity.EXTRA_MY_LIKES, myLikes.getEntity().Likes);
        bundle.putSerializable(MainActivity.EXTRA_MY_FOLLOWERS, myFollowers.getEntity().Follows);
        intent.putExtras(bundle);

        mActivity.startActivity(intent);
    }

    @Override
    public String onPostUrl(Context context) {
        mActivity.showProgress(true);
        if (mActivity.getFlag() == LoginActivity.SIGN_IN){
            return Globals.URL_TAG_ME + "sign_in.php";
        }else {
            return Globals.URL_TAG_ME + "sign_up.php";
        }
    }

    @Override
    public void onError(Context context, String msg) {
        mActivity.showProgress(false);
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public String validate(String field, String value) {
        if (field.equals("account")){
            Pattern p = Pattern.compile("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$");
            Matcher m = p.matcher(value);
            if(!m.matches()) {
                return mActivity.getResources().getString(R.string.toast_email_wrong);
            }
        }

        return null;
    }

    public class UserResult extends BaseResult{
        User user;
    }
}
