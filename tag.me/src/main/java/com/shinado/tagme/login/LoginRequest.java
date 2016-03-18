package com.shinado.tagme.login;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.shinado.tagme.Globals;
import com.shinado.tagme.R;
import com.shinado.tagme.main.MainActivity;
import com.shinado.tagme.user.User;

import framework.inj.ActivityInj;
import framework.inj.ViewInj;
import framework.inj.entity.Postable;

@ActivityInj(R.layout.activity_login)
public class LoginRequest implements Postable<User>{

    private static String TAG = "LoginRequest";
    private LoginActivity mActivity;

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
    public void onPostResponse(Context context, User obj) {
        mActivity.showProgress(false);

        context.startActivity(new Intent(mActivity, MainActivity.class));
    }

    @Override
    public String onPostUrl(Context context) {
        mActivity.showProgress(true);
        return Globals.URL_MAIN + "sign_in";
    }

    @Override
    public void onError(Context context, String msg) {
        mActivity.showProgress(false);
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
}
