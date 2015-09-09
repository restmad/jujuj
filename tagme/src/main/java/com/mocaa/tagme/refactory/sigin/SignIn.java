package com.mocaa.tagme.refactory.sigin;

import android.content.Context;

import com.mocaa.tagme.R;
import com.mocaa.tagme.entity.User;

import framework.inj.ActivityInj;
import framework.inj.ViewInj;
import framework.inj.entity.Following;
import framework.inj.entity.Postable;
import framework.inj.entity.utility.Validatable;

@ActivityInj(R.layout.activity_signin)
public class SignIn implements Postable<User>, Validatable, Following<Tags>{

    @ViewInj(R.id.activity_sign_account)
    public String account;

    @ViewInj(R.id.activity_sign_pwd)
    public String pwd;

    @Override
    public int getSubmitButtonId() {
        return R.id.activity_sign_btn;
    }

    @Override
    public void onPostResponse(Context context, User user) {

    }

    @Override
    public String onPostUrl(Context context) {
        return null;
    }

    @Override
    public void onError(Context context, String msg) {

    }

    @Override
    public String validate(String field, String value) {
        return null;
    }
}
